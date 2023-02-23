/**********************************************

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see
<http://www.gnu.org/licenses/>.
In addition to the rights granted to the GNU General Public License,
you opt to use this program as specified in the following:

MUSITECH LINKING EXCEPTION

Linking this library statically or dynamically with other modules is making
a combined work based on this library. Thus, the terms and conditions of the
GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you permission
to link this library with independent modules to produce an executable, regardless
of the license terms of these independent modules, and to copy and distribute the
resulting executable under terms of your choice, provided that you also meet,
for each linked independent module, the terms and conditions of the license of
that module. An independent module is a module which is not derived from or based
on this library.

For the MUSITECH library, this exceptional permission described in the paragraph
above is subject to the following three conditions:
- If you modify this library, you must extend the GNU General Public License and
       this exception including these conditions to your version of the MUSITECH library.
- If you distribute a combined work with this library, you have to mention the
       MUSITECH project and link to its web site www.musitech.org in a location
       easily accessible to the users of the combined work (typically in the "About"
       section of the "Help" menu) and in any advertising material for the combined
       software.
- If you distribute a combined work with the MUSITECH library, you allow the MUSITECH
               project to use mention your combined work for promoting the MUSITECH project.
       For the purpose of this licence, 'distribution' includes the provision of software
       services (e.g. over the World Wide Web).

**********************************************/
/*
 * Created on 20.12.2003
 */
package de.uos.fmt.musitech.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Uses java.nio.channels.FileChannel and works only whith Files. To use an
 * InputStream, use PCMFloatIS. Or write the InputStream into a file, to read it
 * from them. As InputStreamFileSwap. Implements resampling. Useful for wave
 * display applications. Implements setPosition (position()), so the position in
 * data can be changed very fast, whithout reading.
 * 
 * @author Nicolai Strauch
 */
public class AudioPreviewReader {

	private AudioFormat audioFormat; // format from the data coming in, not
	// the data coming out (see the code at
	// getFormat())
	// private FileChannel fileChannel;
	private int offset; // position in bytes in source
	private long framesAvailable;
	private int frameSize; // channels * bytesPerSample

	private long readLimit; // in samples: limit to be sett, to be used by
	// read-methods, when not all data have to be read
	// at the end. The read-methods will return -1 by
	// reach readLimit.
	private long mark; // in samples

	private int header = 44; // must allways be added by
	// fileChannel.position(long n)

	/** samples caming from the source for any sample going out */
	private double sInPerSOut = 1;

	private int bytesPerSample;

	/**
	 * channelsMap.length = number of channels for the output. The values give
	 * the inputchannel for an outputchannel. (channel[i] = channelInTheSource
	 * for output channel i) The values in channel must be less or equal than
	 * the number of source channels.
	 */
	private int[] channelMap;

	/**
	 * the given File must be an PCM-file supported by the audiopackege from
	 * java
	 */
	public AudioPreviewReader(File file) throws UnsupportedAudioFileException, IOException {

		AudioInputStream ais = AudioSystem.getAudioInputStream(file);

		audioFormat = ais.getFormat();
		framesAvailable = ais.getFrameLength();
		FileInputStream fis = new FileInputStream(file);
		FileChannel fileChannel = fis.getChannel();
		source = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
		frameSize = audioFormat.getFrameSize();
		readLimit = framesAvailable;
		sSizeBits = audioFormat.getSampleSizeInBits();
		bytesPerSample = sSizeBits / 8;
		isBigEndian = audioFormat.isBigEndian();
		encoding = audioFormat.getEncoding();
		setChannels(new int[] {0});
	}

	/**
	 * Set the mapping of input channels to output channels. chan.length =
	 * number of channels for the output. The values get the inputchannel for
	 * the respective outputchannel. (chan[i] = channelInTheSource, i =
	 * channelOnTheOutput) Return false and do nothing if any channelreference
	 * is greater then the number of available channels. For Default, the value
	 * set is chan = new int[]{0};
	 * 
	 * @param chan = the map of input channels to output channels
	 * @return true if the param is coherent whith in- and output
	 */
	public synchronized boolean setChannels(int[] chan) {
		int cDisp = audioFormat.getChannels();
		for (int i = 0; i < chan.length; i++) {
			if (chan[i] > cDisp)
				return false;
		}
		channelMap = chan;
		return true;
	}

	/**
	 * set channels to get all channels available in source by read(...)
	 * 
	 * @return number of channels
	 */
	public synchronized int setAllChannels() {
		int chan = audioFormat.getChannels();
		channelMap = new int[chan];
		for (int i = 0; i < channelMap.length; i++) {
			channelMap[i] = i;
		}
		return chan;
	}

	/**
	 * Get the number of channels available by read(), dependend from the int[]
	 * channels set by setChannels. If nothing was set, the default is 1, the
	 * first channel of source. The AudioFormat returned gets the number of
	 * channels available in source, not the number of channels get by read().
	 * TODO adapt AudioFormat to match read().
	 * 
	 * @return the number of available channels.
	 */
	public int channelsAvailable() {
		return channelMap.length;
	}

	/**
	 * Set that number of samples from source that will converge to one sample
	 * at the output.
	 * 
	 * @param iPo - samples coming in for any sample going out
	 */
	public void setSInPerSOut(double iPo) {
		sInPerSOut = iPo;
	}

	public double getSInPerSOut() {
		return sInPerSOut;
	}

	/**
	 * Return -1 by reach readLimit
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
	 */
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}

	private ByteBuffer source;
	// private byte[] buffer;

	private boolean isBigEndian;
	private AudioFormat.Encoding encoding;
	private int sSizeBits;
	private int chOfs; // ChannelOffset. Needed?
	private int res; // sample
	private float s_si; // sample in source, exact

	/**
	 * Will read from current position len samples, whithout pass the limit
	 * settet by readLimit (setReadLimit(...)). So it will be possible to define
	 * an branche as virtual dataamoungth, that will be read. So use (for
	 * example play) only an part of an file, define the end and begin whith
	 * this.setPosition(...).setReadLimit(...). Resampling, if needed, whith
	 * quadratical? interpolation. Not very exact, but fast. Follow condition is
	 * needed: data.length >= channelsDisponilbe() else exceptions will occure.
	 * Data written is normalised to -1 1. if no enough data was written, the
	 * place to be written in float[][] is filled whith zeros.
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][],
	 *      int, int)
	 */
	public synchronized int read(float[][] data, int start, int len) throws IOException {
		if (data.length < channelMap.length) {
			// TODO: zu wenig Platz für Kanäle, ArrayIndexEtc-Exception
		}
		int bLen = (int) (len * sInPerSOut * frameSize);
		int available = (int) ((readLimit - position()) * frameSize);
		bLen = available > bLen ? bLen : available;
		if (bLen < 1)
			return -1;
		// if(source==null)
		// source = fileChannel.map(FileChannel.MapMode.READ_ONLY,
		// fileChannel.position(), bLen);//fileChannel.size());
		// source = ByteBuffer.allocateDirect(bLen); // todo: ob
		// wiederverwendbar?
		// fileChannel.read(source);

		// resampling and floatextracting >>>>>>>
		long normalisationFactor = 1 << sSizeBits;

		s_si = 0.0f;
		int t_i = 0; // target index (float-array)
		int endAt = (int) (bLen - (frameSize * sInPerSOut));
		for (int s_i = 0, channel = 0; s_i <= endAt && t_i < data[0].length; t_i++, s_i = (int) s_si * frameSize) {
			for (channel = 0; channel < channelMap.length; channel++) {
				chOfs = channelMap[channel] * bytesPerSample;

				if (encoding == AudioFormat.Encoding.PCM_SIGNED) { // by
					// PCM_SIGNED
					// the
					// negativ
					// samples
					// must be
					// prepared
					// (example:
					// by 16
					// bps, the
					// bytes
					// 11001011
					// and
					// 00101110
					// must be
					// written
					// as
					// integer:
					// 11111111
					// 11111111
					// 11001011
					// 00101110)
					if (!isBigEndian) {
						if (source.get(offset + s_i + chOfs + bytesPerSample - 1) < 0) // if
							// the
							// biggest
							// byte
							// is
							// <0,
							// and
							// so
							// the
							// sample
							// is
							// negativ
							res = -1 << (sSizeBits); // the bits at that the
						// bytes will be placed
						// must be 0
						else
							res = 0;
					} else {
						if (source.get(offset + s_i + chOfs) < 0) // if the
							// biggest
							// byte is
							// <0, and
							// so the
							// sample is
							// negativ
							res = -1 << (sSizeBits); // the bits at that the
						// bytes will be placed
						// must be 0
						else
							res = 0;
					}
				} else
					res = 0;

				// the bytes are put in the sample
				if (!isBigEndian) {
					res |= (0xff & source.get(offset + s_i + chOfs + 0));
					if (bytesPerSample > 1) // 16 bps or grater
						res |= (0xff & source.get(offset + s_i + chOfs + 1)) << 8;
					if (bytesPerSample > 2) // 24 bps
						res |= (0xff & source.get(offset + s_i + chOfs + 2)) << 16;
				} else {
					if (bytesPerSample > 2) { // 24 bps
						res |= (0xff & source.get(offset + s_i + chOfs)) << 16;
						res |= (0xff & source.get(offset + s_i + chOfs + 1)) << 8;
						res |= (0xff & source.get(offset + s_i + chOfs + 2));
					} else if (bytesPerSample > 1) { // 16 bps
						res |= (0xff & source.get(offset + s_i + chOfs)) << 8;
						res |= (0xff & source.get(offset + s_i + chOfs + 1));
					} else {
						res |= (0xff & source.get(offset + s_i + chOfs));
					}
				} // sample filled, bigEndian respected
				data[channel][start + t_i] = (float) res / normalisationFactor;
			}
			s_si += sInPerSOut; // next sample to read from source
		}
		if (encoding == AudioFormat.Encoding.PCM_UNSIGNED) {
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < t_i; j++)
					data[i][j] -= sub[bytesPerSample - 1];
		}
		if (t_i < len) {
			AudioUtil.fillUp(data, t_i, len - t_i, 0.0f);
		}
		return t_i;
		// and resampling and floatextracting <<<<<<<<<
	}

	final static int[] sub = {0x80, 0x8000, 0x800000, 0x80000000};

	/**
	 * Note: the number of channels included at this AudioFormat correspond to
	 * the number of channels available in te source. To allow all channels by
	 * read(...), set them at setChannels. To get all channels available, use
	 * setAllChannels(). The frameRate and sampleRate do not will be necessary
	 * the values get out too. These values correspondto the output if
	 * getSInPerSOut() return 1.0f. Else divide frameRate or sampleRate by
	 * getSInPerSOut() to obtain the correct values. The values in this format
	 * returning are the vales disponible by source.
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	public AudioFormat getFormat() {
		AudioFormat out = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat
				.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(),
											audioFormat.getFrameSize(), audioFormat.getFrameRate(),
											true);
		return out;
	}

	/**
	 * Add n to the actual position. Dont execute a real skipping of data. The
	 * effect is the same, but much faster!
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		// fileChannel.position(fileChannel.position()+(n*frameSize));
		offset += n;
		return n;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	public void position(long n) throws IOException {
		// TODO: synchronized, or stop read by positionchange?
		// System.out.println("partReader. position changet to:
		// "+(n*frameSize+header));
		// fileChannel.position(n*frameSize+header);
		// source = fileChannel.map(FileChannel.MapMode.READ_ONLY,
		// fileChannel.position(), fileChannel.size());
		offset = (int) (header + (n * frameSize));
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	public long position() throws IOException {
		// return fileChannel.position()/frameSize;
		return (offset - header) / frameSize;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	public long readLimit(long newLimit) {
		readLimit = newLimit > framesAvailable ? framesAvailable : newLimit;
		return readLimit;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	public long readLimit() {
		return readLimit;
	}

	public void mark(long marker) {
		mark = marker;
	}

	public long mark() throws IOException {
		return mark;
	}

	/**
	 * Reset read-position to the marker mark.
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	public void reset() throws IOException {
		// fileChannel.position(mark*frameSize+header);
		offset = (int) (mark * frameSize + header);
	}

	/**
	 * @return samples available between position and readLimit
	 * @throws IOException
	 */
	public long available() throws IOException {
		return readLimit() - position();
	}

	/**
	 * Ignore readLimit
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	public long totalAvailable() {
		return framesAvailable;
	}

}
