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
 * Created on 18.02.2004
 */
package de.uos.fmt.musitech.audio.floatStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.uos.fmt.musitech.audio.AudioUtil;

/**
 * This class combines FloatInputStream and FloatPreviewReader, so that 
 * only one Mapping is used.
 * 
 * @author Nicolai Strauch 
 */
public abstract class PCMFileFloatIS_old implements RangeFloatIS, FloatPreviewReader {
	
	private ByteBuffer source;
	
	private boolean normalised;
	
	// format from the data coming in, not the data coming out (see the code at getFormat())
	private AudioFormat audioFormat;
	
	private long framesAvailable; // total of frames (samples) available in the file
	private int frameSize; // number of bytes in the frame
	
	private long mark; // in samples
	private int header = 44; // must allways be added by fileChannel.position(long n)
	private int offset = header; // position in bytes in source
								   // autorized to manipulate offset: position-methods
								   // and read - methods.
								   // any other method must use position(...)
								   // to evit code-errors
	private int readLimit; // byteposition in source at that virtual end is reached
							 // (at that byte, read return -1)
	private boolean isBigEndian;
	private AudioFormat.Encoding encoding;
	private int sSizeBits; // sampleSizeInBits
	private int numChannels; // number of channels available on source and by read(...)
	private double sInPerSOut = 1; // samples caming from the source for any sample going out
	private int bytesPerSample;
	private int[] channels; // channels.length = number of channels for the output. 
						 	  // The values get the inputchannel for the respective outputchannel.
							  // (channel[i] = channelInTheSource, i = channelOnTheOutput)
							  // The values in channels must be shorter or equal them index.
							  // (channels[i] >= i)
							  
	public PCMFileFloatIS_old(File file) throws UnsupportedAudioFileException, IOException {
		AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
		if(aff.getType() != AudioFileFormat.Type.WAVE)
			throw new UnsupportedAudioFileException("Not PCM-WAVE.");
		AudioInputStream ais = AudioSystem.getAudioInputStream(file);
		AudioFormat format = ais.getFormat();
		framesAvailable = ais.getFrameLength();
		FileInputStream fis = new FileInputStream(file);
		FileChannel fileChannel = fis.getChannel();
		ByteBuffer bb = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
		setByteBuffer(bb, format, framesAvailable);
	}
	
	public PCMFileFloatIS_old(ByteBuffer source, AudioFormat format, long framesAvailable){
		setByteBuffer(source, format, framesAvailable);
	}
	
	private void setByteBuffer(ByteBuffer source, AudioFormat format, long framesAvailable){
		audioFormat = format;
		this.framesAvailable = framesAvailable;
		this.source = source;
		frameSize = audioFormat.getFrameSize();
		readLimit = (int) framesAvailable * frameSize + header;
		sSizeBits = audioFormat.getSampleSizeInBits();
		bytesPerSample = sSizeBits / 8;
		isBigEndian = audioFormat.isBigEndian();
		encoding = audioFormat.getEncoding();
		numChannels = audioFormat.getChannels();
		setChannels(new int[] { 0 });
		offset = header;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
	 */
	@Override
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}
	
	
	private int res; // sample
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][], int, int)
	 */
	@Override
	public int read(float[][] data, int start, int len) throws IOException {
		int t_i = 0; // target index (float-array)
		int lim = readLimit-frameSize;
		for (; offset < lim && t_i<len; t_i++) {
			for (int channel = 0; channel < numChannels; channel++) {
				
				if(encoding == AudioFormat.Encoding.PCM_SIGNED){	// by PCM_SIGNED the negativ samples must be prepared (example: by 16 bps, the bytes 11001011 and 00101110 must be written as integer: 11111111 11111111 11001011 00101110)
				   if(!isBigEndian){
					   if(source.get(offset + bytesPerSample - 1) < 0)	// if the biggest byte is <0, and so the sample is negativ
						   res = -1 << (sSizeBits);		// the bits at that the bytes will be placed must be 0
					   else res = 0;
				   }else{
					   if(source.get(offset) < 0)	// if the biggest byte is <0, and so the sample is negativ
						   res = -1 << (sSizeBits);		// the bits at that the bytes will be placed must be 0
					   else res = 0;
				   }
				}else res = 0;
				
				// the bytes are put in the sample
				if (isBigEndian) {
					if (bytesPerSample > 2) // 24 bps			  
						res |= (0xff & source.get(offset++)) << 16;
					if (bytesPerSample > 1)
						res |= (0xff & source.get(offset++)) << 8;
					res |= (0xff & source.get(offset++));
				} else {
					res |= (0xff & source.get(offset++));
					if (bytesPerSample > 1) { // 16 bps or grater
						res |= (0xff & source.get(offset++)) << 8;
						if (bytesPerSample > 2) // 24 bps
							res |= (0xff & source.get(offset++)) << 16;
					}
				}
				if (sSizeBits==8){
					res <<= 8;
				}
				data[channel][start + t_i] = res;
				if (sSizeBits==24){ 
					data[channel][start + t_i] /= 8;
				}
			}
		}
		if (encoding == AudioFormat.Encoding.PCM_UNSIGNED) {
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < data[i].length; j++)
					data[i][j] -= sub[bytesPerSample - 1];
		}
		
		if (normalised)
		{
			float f = 1 << (sSizeBits - 1);
			for(int i=0; i<data.length; i++)
				for(int j=start; j<t_i; j++)
					data[i][j] /= f;
		}
		
		return t_i;
	}
	final static int[] sub = { 0x80, 0x8000, 0x800000, 0x80000000 };
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		AudioFormat out =
			new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				audioFormat.getSampleRate(),
				audioFormat.getSampleSizeInBits(),
				audioFormat.getChannels(),
				audioFormat.getFrameSize(),
				audioFormat.getFrameRate(),
				true);
		return out;
	}
	/**
	 * Skip n samples
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		position((int) (position()+n));
		return position();
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		position((int) mark);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position(long)
	 */
	@Override
	public void position(int n) throws IOException {
		offset = header + n * frameSize;
		if(offset>readLimit) offset = readLimit;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#position()
	 */
	@Override
	public int position() {
		return (offset-header) / frameSize;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#readLimit(long)
	 */
	@Override
	public int markEnd(int newLimit) {
		if(newLimit > getMaxEndPos())
			newLimit = getMaxEndPos();
		readLimit = (newLimit * frameSize) + header;
		return getEndMark();
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#readLimit()
	 */
	@Override
	public int getEndMark() {
		return (readLimit-header)/frameSize;
	}
	/**
	 * if(marker > getEndMark()) marker = getEndMark();
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#mark(long)
	 */
	@Override
	public void markBegin(int marker) {
		if(marker > getEndMark()) marker = getEndMark();
		mark = marker;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#mark()
	 */
	@Override
	public int getBeginMark() throws IOException {
		return (int)mark;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#totalAvailable()
	 */
	@Override
	public int getMaxEndPos() {
		return (int)framesAvailable;
	}
	
	
	private float s_si;	// sample in source, exact	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][], int, int)
	 */
	@Override
	public int previewRead(float[][] data, int start, int len) {
		
		// TODO TODO TODO: in arbeit
		//  Baustelle
		// was tun, wenn für play und für Previwe ganz durcheinander die Positionen
		// verstellt werden? Ob es doch keine gute Idee war,
		// das alles in eine Klasse zu stecken? Ob man lieber ein gemeinsames
		// mapping anlegen sollte? 
		
		
		
		if(data.length<channels.length){
			// TODO: zu wenig Platz für Kanäle, ArrayIndexEtc-Exception
		}
		int bLen = (int) (len * sInPerSOut * frameSize);
		int available = 0;
		available = (readLimit - position()) * frameSize;
		bLen = available>bLen?bLen:available;
		if(bLen<1) return -1;

		int chOfs = 0;

// resampling and floatextracting   >>>>>>>	
		long normalisationFactor = 1 << sSizeBits;
		
		s_si = 0.0f;
		int t_i = 0;	// target index (float-array)
		int endAt = (int) (bLen-(frameSize*sInPerSOut));
		for (int s_i = 0, channel = 0; s_i <= endAt && t_i<data[0].length; t_i++, s_i = (int)s_si*frameSize) {
		   for (channel = 0; channel < channels.length; channel++) {
				chOfs = channels[channel] * bytesPerSample;
					
				if(encoding == AudioFormat.Encoding.PCM_SIGNED){	// by PCM_SIGNED the negativ samples must be prepared (example: by 16 bps, the bytes 11001011 and 00101110 must be written as integer: 11111111 11111111 11001011 00101110)
				   if(!isBigEndian){
					   if(source.get(offset+s_i+chOfs + bytesPerSample - 1) < 0)	// if the biggest byte is <0, and so the sample is negativ
						   res = -1 << (sSizeBits);		// the bits at that the bytes will be placed must be 0
					   else res = 0;
				   }else{
					   if(source.get(offset+s_i+chOfs) < 0)	// if the biggest byte is <0, and so the sample is negativ
						   res = -1 << (sSizeBits);		// the bits at that the bytes will be placed must be 0
					   else res = 0;
				   }
				}else res = 0;				
					
				// the bytes are put in the sample
				if (!isBigEndian){
					res |= (0xff & source.get(offset+s_i+chOfs + 0));
					if(bytesPerSample>1)	// 16 bps or grater
						res |= (0xff & source.get(offset+s_i+chOfs + 1)) << 8;
					if(bytesPerSample>2)	// 24 bps
						res |= (0xff & source.get(offset+s_i+chOfs + 2)) << 16;
				}
				else{
					if(bytesPerSample>2){	// 24 bps
						res |= (0xff & source.get(offset+s_i+chOfs)) << 16;
						res |= (0xff & source.get(offset+s_i+chOfs + 1)) << 8;
						res |= (0xff & source.get(offset+s_i+chOfs + 2));
					}
					else if(bytesPerSample>1){	// 16 bps
						res |= (0xff & source.get(offset+s_i+chOfs)) << 8;
						res |= (0xff & source.get(offset+s_i+chOfs + 1));
					}
					else{
						res |= (0xff & source.get(offset+s_i+chOfs));
					}
				} // sample filled, bigEndian respected
				data[channel][start + t_i] = (float)res / normalisationFactor;
		   }
		   s_si += sInPerSOut;	// next sample to read from source
		}
		if (encoding == AudioFormat.Encoding.PCM_UNSIGNED)
		{
		   for(int i=0; i<data.length; i++)
			   for(int j=0; j<t_i; j++)
				   data[i][j] -= sub[bytesPerSample - 1];
		}
		if(t_i<len){
			AudioUtil.fillUp(data, t_i, len-t_i, 0.0f);
		}
		return t_i; 
// and resampling and floatextracting <<<<<<<<<
	
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#setSinPerSOut(float)
	 */
	@Override
	public void setSampleRateRatio(float s) {
		// TODO Auto-generated method stub
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#getSinPerSOut()
	 */
	@Override
	public float getSampleRateRatio() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#setAllChannels()
	 */
	@Override
	public int setAllChannels() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#setChannels(int[])
	 */
	@Override
	public boolean setChannels(int[] chan) {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#channelsDisponible()
	 */
	@Override
	public int channelsDisponible() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * @return true if data is normalised in to the range of -1 to 1.
	 */
	public boolean isNormalised() {
		return normalised;
	}

	/**
	 * @param b - true if the data has to be normalised in to the range of -1 to 1.
	 */
	public void setNormalised(boolean b) {
		normalised = b;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#getMapping()
	 */
	public ByteBuffer getMapping() {
		return source;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#getPositionableFIS()
	 */
	@Override
	public PositionableFIS getPositionableFIS() {
		return null;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.PositionableFIS#getFloatPreviewReader()
	 */
	public FloatPreviewReader getFloatPreviewReader() {
		return null;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return getMaxEndPos() - position();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return position();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#setPositionInSamples(long)
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
		position((int) newPos);
	}
	
}
