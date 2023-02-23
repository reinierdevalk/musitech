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
package de.uos.fmt.musitech.audio.floatStream;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.uos.fmt.musitech.audio.AudioUtil;

/*
 * Created on 28.06.2004
 */
/**
 * @author Nicolai Strauch
 */
public class PCMFPreviewReader extends PCMFileFloatIS implements FloatPreviewReader {

	private float sInPerSOut = 1; // samples caming from the source for any
	// sample going out
	private int bytesPerSample;
	private int frameSize;
	private int[] channels; // channels.length = number of channels for the

	// output.

	// The values get the inputchannel for the respective outputchannel.
	// (channel[i] = channelInTheSource, i = channelOnTheOutput)
	// The values in channels must be shorter or equal them index.
	// (channels[i] >= i)

	//	private int position; // sample at that the next read begin

	public PCMFPreviewReader(File file) throws UnsupportedAudioFileException, IOException {
		super(file);
		initialise();
	}

	PCMFPreviewReader(MultiuseMapping mm) {
		super(mm);
		initialise();
	}

	private void initialise() {
		setChannels(new int[] { 0 });
		bytesPerSample = source.getAudioFormat().getSampleSizeInBits() / 8;
		frameSize = source.getAudioFormat().getFrameSize();
	}

	/**
	 * 
	 * chan.length = number of channels for the output. The values get the
	 * inputchannel for the respective outputchannel. (chan[i] =
	 * channelInTheSource, i = channelOnTheOutput)
	 * 
	 * Return false and do nothing if any channelreference is greater then the
	 * number of available channels.
	 * 
	 * For Default, the value set is chan = new int[]{0};
	 * 
	 * @param chan = inputchannels for the respektiv outpuchannels
	 * @return true if the param is coherent whith in- and output
	 */
	@Override
	public synchronized boolean setChannels(int[] chan) {
		int cDisp = source.getAudioFormat().getChannels();
		for (int i = 0; i < chan.length; i++) {
			if (chan[i] > cDisp)
				return false;
		}
		channels = chan;
		return true;
	}

	/**
	 * set channels to get all channels available in source by read(...)
	 * 
	 * @return number of channels
	 */
	@Override
	public synchronized int setAllChannels() {
		int chan = source.getAudioFormat().getChannels();
		channels = new int[chan];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = i;
		}
		return chan;
	}

	/**
	 * Get the number of channels available by read(), dependend from th int[]
	 * channels set by setChannels. If nothing was set, the default is 1, the
	 * first channel of source. The AudioFormat returned get the number of
	 * channels available in source, not the number of channels get by read().
	 * 
	 * @return
	 */
	@Override
	public int channelsDisponible() {
		return channels.length;
	}

	/**
	 * number of samples from source that will converge to one sample at the
	 * output.
	 * 
	 * @param iPo - samples coming in for any sample going out
	 */
	@Override
	public void setSampleRateRatio(float iPo) {
		sInPerSOut = iPo;
	}

	@Override
	public float getSampleRateRatio() {
		return sInPerSOut;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][], int)
	 */
	@Override
	public int previewRead(float[][] data,  int firstSampleToRead){
		return previewRead(data, 0, data[0].length, firstSampleToRead);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][], int, int, int)
	 */
	@Override
	public int previewRead(float[][] data, int start, int len, int firstSampleToRead){
		try {
			position(firstSampleToRead);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return previewRead(data, start, len);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][])
	 */
	@Override
	public int previewRead(float[][] data) {
		return previewRead(data, 0, data[0].length);
	}
	
	private boolean reading; // true if previewRead is running

	//	private int chOfs; // ChannelOffset. Needed?
	//	private int res; // sample
	private float s_si; // sample in source, exact

	/**
	 * Do not change position in MultiuseMapping
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][],
	 *      int, int)
	 */
	@Override
	public synchronized int previewRead(float[][] data, int start, int len) {
		reading = true;
		stopPreviewRead = false;
		if (data.length < channels.length) {
			// TODO: zu wenig Platz für Kanäle, ArrayIndexEtc-Exception
		}
		int t_i = 0; // target index (float-array)
		int sLen = (int) (len * sInPerSOut);
		sLen = source.framesAvailable() - position() > sLen ? sLen : source.framesAvailable()
																		- position();
		if (sLen < 1)
			return -1;

		s_si = 0.0f;
		sLen = (int) (sLen - sInPerSOut);

		for (int s_i = 0, channel = 0; s_i <= sLen && t_i < data[0].length && !stopPreviewRead; t_i++, s_i = (int) s_si) {
			for (channel = 0; channel < channels.length && !stopPreviewRead; channel++) {
				data[channel][start + t_i] = source.getSample(position() + s_i, channels[channel],
																true);
			}
			s_si += sInPerSOut; // next sample to read from source
		}
		if (t_i < len) {
			AudioUtil.fillUp(data, t_i, len - t_i, 0.0f);
		}
		reading = false;
		stopPreviewRead = false; // if it was true and read stop, reset to false
		return t_i;
	}
	
	private boolean stopPreviewRead = false; // true if previewRead is running and have to be stopped.
	
	/**
	 * Stop the preview-read at the position where it currently is. 
	 * previewRead(...) will return. 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#stopPreviewRead()
	 * @return true if reading was active, false if it was not.
	 */
	@Override
	public boolean stopPreviewRead(){
		if(reading){
			stopPreviewRead = true;
			return true;
		}else
			return false;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#getPositionableFIS()
	 */
	@Override
	public PositionableFIS getPositionableFIS() {
		return source.getPositionableFIS();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#availableMikroseconds()
	 */
	@Override
	public long availableMikroseconds() {
		return (long) (available() / getFormat().getFrameRate() * 1000000);
	}

}