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
 * Created on 06.07.2004
 */
package de.uos.fmt.musitech.audio.floatStream;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import de.uos.fmt.musitech.audio.AudioUtil;

/**
 * @author Nicolai Strauch
 */
public class MP3FPreviewReader extends MP3FileFloatIS implements FloatPreviewReader {

	private float sInPerSOut = 1; // samples caming from the source for any
	// sample going out
	private int bytesPerSample;
	private int frameSize;
	private int[] channels; // channels.length = number of channels for the
	// private int normalisationFactor;

	private BilateralRingBuffer ringBuff = new BilateralRingBuffer();

	/**
	 * @param file
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public MP3FPreviewReader(File file) throws UnsupportedAudioFileException, IOException {
		super(file);
		super.setNormalised(true);
		initialise();
	}

	MP3FPreviewReader(ChannelInputStream cis) throws UnsupportedAudioFileException, IOException {
		super(cis);
	}

	private void initialise() {
		setChannels(new int[] {0});
		bytesPerSample = getFormat().getSampleSizeInBits() / 8;
		frameSize = getFormat().getFrameSize();
		// normalisationFaktor = 1 << (getFormat().getSampleSizeInBits() - 1);
		mikrosecondsAvailable = (long) (available() / getFormat().getFrameRate() * 1000000);
	}

	/**
	 * chan.length = number of channels for the output. The values get the
	 * inputchannel for the respective outputchannel. (chan[i] =
	 * channelInTheSource, i = channelOnTheOutput) Return false and do nothing
	 * if any channelreference is greater then the number of available channels.
	 * For Default, the value set is chan = new int[]{0};
	 * 
	 * @param chan = inputchannels for the respektiv outpuchannels
	 * @return true if the param is coherent whith in- and output
	 */
	public synchronized boolean setChannels(int[] chan) {
		int cDisp = getFormat().getChannels();
		for (int i = 0; i < chan.length; i++) {
			if (chan[i] > cDisp)
				return false;
		}
		channels = chan;
		sample = new float[channels.length][1];
		ringBuff.setChannels(channels.length);
		return true;
	}

	/**
	 * set channels to get all channels available in source by read(...)
	 * 
	 * @return number of channels
	 */
	public synchronized int setAllChannels() {
		int chan = getFormat().getChannels();
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
	public int channelsDisponible() {
		return channels.length;
	}

	/**
	 * number of samples from source that will converge to one sample at the
	 * output.
	 * 
	 * @param iPo - samples coming in for any sample going out
	 */
	public void setSampleRateRatio(float iPo) {
		if (iPo == sInPerSOut)
			return;
		ringBuff.resetCounters();
		sInPerSOut = iPo;
	}

	public float getSampleRateRatio() {
		return sInPerSOut;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][])
	 */
	public int previewRead(float[][] data) {
		return previewRead(data, 0, data[0].length, position());
	}

	// private int chOfs; // ChannelOffset. Needed?
	private float[][] sample; // sample - needed for channelconversion (by
								// default only one channel is read)
	private float s_si; // sample in source, exact

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][],
	 *      int)
	 */
	public int previewRead(float[][] data, int firstSampleToRead) {
		return previewRead(data, 0, data[0].length, firstSampleToRead);
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][],
	 *      int, int, int)
	 */
	public int previewRead(float[][] data, int start, int len, int firstSampleToRead) {
		// try {
		// position(firstSampleToRead);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// int out = loadPreviewData(data, start, len);
		int out = ringBuff.readPD(data, start, len, firstSampleToRead);
		if (out < len && !stopPreviewRead) {
			AudioUtil.fillUp(data, out, len - out, 0.0f);
		}
		stopPreviewRead = false;
		return out;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#previewRead(float[][],
	 *      int, int)
	 */
	public int previewRead(float[][] data, int start, int len) {
		return previewRead(data, start, len, position());
	}

	public int loadPreviewData(float[][] data, int start, int len) {
		reading = true;
		if (data.length < channels.length) {
			// TODO: zu wenig Platz für Kanäle, ArrayIndexEtc-Exception
		}
		if (len == 0)
			return 0;
		// int offset = position();
		int t_i = 0; // target index (float-array)
		int sLen = (int) (len * sInPerSOut);
		sLen = available() - position() > sLen ? sLen : available() - position();
		if (sLen < 1)
			return -1;

		s_si = 0.0f;
		sLen = (int) (sLen - sInPerSOut);
		int read = 0;

		for (int s_i = 0, channel = 0; s_i <= sLen && t_i < data[0].length && !stopPreviewRead; t_i++, s_i = (int) s_si) {
			try {
				// position(offset + s_i);
				skip(s_i / (t_i + 1));
				read = read(sample/* position() + s_i */, 0, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (read > 0) {
				for (channel = 0; channel < channels.length; channel++) {
					data[channel][start + t_i] = sample[channels[channel]][0]; // data
																				// allready
																				// normalised.
																				// /
																				// normalisationFaktor;
				}
				s_si += sInPerSOut; // next sample to read from source
				readProblems = 0;
			} else if (read == -1) {
				// if(t_i==0) return -1;
				break;
			} else {
				if (readProblems > 10) {
					System.out
							.println("MP3PreviewReader.previewRead(): have not got data for 10 times. now breaking.");
					break;
				}
				t_i--; // try to read again, es muss ja mal klappen.
				readProblems++;
			}
		}
		reading = false;
		return t_i;
	}
	private int readProblems = 0;

	private boolean reading = false; // true if previewRead is running
	private boolean stopPreviewRead = false; // true if previewRead is
												// running and have to be
												// stopped.

	/**
	 * Stop the preview-read by the sample at that it is. previewRead(...) will
	 * return.
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#stopPreviewRead()
	 * @return true if stop preview-read, false if it do not was running.
	 */
	public boolean stopPreviewRead() {
		if (reading) {
			stopPreviewRead = true;
			return true;
		} else
			return false;
	}

	/**
	 * Not implemented, return null.
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#getPositionableFIS()
	 */
	public PositionableFIS getPositionableFIS() {
		return null;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#available()
	 */
	public int available() {
		return (int) (remainingSamples() + getPositionInSamples());
	}

	private long mikrosecondsAvailable;

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatPreviewReader#availableMikroseconds()
	 */
	public long availableMikroseconds() {
		return mikrosecondsAvailable;
	}

	/**
	 * RingBuffer that can load forward and backward.
	 * 
	 * @author Nicolai Strauch
	 */
	class BilateralRingBuffer {

		private int srcPos; // position in the source of the sample at the first
							// position in Buffer
		private float[][] buffer;
		private int buffLoadFrom; // first sample loaded - index in buffer
		// private int lenLoadInBuff; // number of samples load in buffer
		private int buffLoadTo; // first samplenot loaded - endindex in buffer
		private boolean isWrappos; // the first position (buffLoadFrom) is an
									// index greater than the endposition
									// (buffLoadTo)
		private int bufferSize = 2 * 1152;

		BilateralRingBuffer() {

		}

		public void setChannels(int length) {
			buffer = new float[length][bufferSize];
			resetCounters();
		}

		public boolean load(int beginPos, int len) {
			int allreadyLoaded = isWrappos	? ((bufferSize - buffLoadFrom) + buffLoadTo)
											: (buffLoadTo - buffLoadFrom);
			int load = 0; // load be a read
			int toLoad = 0;
			// load backward: if we have to load data before srcPos
			if (beginPos < srcPos) { // beginPos befor samples loaded
				try {
					position(beginPos);
				} catch (IOException e) {
					e.printStackTrace();
				}
				toLoad = srcPos - beginPos;
				// if data to load is out of buffersize, and so not connectable
				// whith the data in buffer
				if (toLoad >= bufferSize) { // samples loaded isn't useful
					resetCounters();
					buffLoadTo = loadPreviewData(buffer, 0, len);
					srcPos = beginPos;
					return true;
				} else {
					// if no enogth place from pos 0 to buffLoadFrom, we must
					// break data at end of buffer, wrapping.
					if (toLoad > buffLoadFrom) {
						load = loadPreviewData(buffer, bufferSize - (toLoad - buffLoadFrom),
							(toLoad - buffLoadFrom));
						if (stopPreviewRead || load != (toLoad - buffLoadFrom))
							return false;
						load = loadPreviewData(buffer, 0, buffLoadFrom);
						if (stopPreviewRead || load != buffLoadFrom)
							return false;
						buffLoadFrom = bufferSize - (toLoad - buffLoadFrom); // now
																				// buffLoadFrom
																				// is
																				// the
																				// loadbegin
																				// too
																				// (to
																				// determinate
																				// lenLoadInBuff)
						if (isWrappos || buffLoadTo > buffLoadFrom) // if
																	// buffLoadFrom
																	// overtake
																	// going
																	// backward
																	// buffLoadTo
							buffLoadTo = buffLoadFrom;
						isWrappos = true;
					} else {
						load = loadPreviewData(buffer, buffLoadFrom - toLoad, toLoad);
						if (stopPreviewRead || load != toLoad)
							return false;
						buffLoadFrom = buffLoadFrom - toLoad;
						if (isWrappos && buffLoadFrom < buffLoadTo) // if
																	// buffLoadFrom
																	// overtake
																	// going
																	// backward
																	// buffLoadTo
							buffLoadTo = buffLoadFrom;
						if (buffLoadTo == 0) {
							buffLoadTo = bufferSize;
							isWrappos = false;
						}
					} // end if(diffQuestToLoaded>buffLoadFrom){}else{
					srcPos = beginPos;
				} // end if(diffQuestToLoaded>=bufferSize){}else{
			} // end if(diffQuestToLoaded < 0){
			// load foward: if we have to load data passing the data allreay
			// loaded
			if ((beginPos + len) > (srcPos + allreadyLoaded)) {
				toLoad = (beginPos + len) - (srcPos + allreadyLoaded); // lastSampleNeed
																		// -
																		// lastSampleLoad
				// if data to load is out of buffersize, and so not connectable
				// whith the data in buffer
				if (toLoad > bufferSize) {
					resetCounters();
					try {
						position(beginPos);
					} catch (IOException e) {
						e.printStackTrace();
					}
					buffLoadTo = loadPreviewData(buffer, 0, len);
					srcPos = beginPos;
					return true;
				} else {
					try {
						position(beginPos + (len - toLoad));
					} catch (IOException e) {
						e.printStackTrace();
					}
					// if we have to go at the end of buffer and wrap them
					if (toLoad > bufferSize - buffLoadTo) {
						load = loadPreviewData(buffer, buffLoadTo, bufferSize - buffLoadTo);
						if (stopPreviewRead || load != bufferSize - buffLoadTo)
							return false;
						load = loadPreviewData(buffer, 0, toLoad - (bufferSize - buffLoadTo));
						if (stopPreviewRead || load != toLoad - (bufferSize - buffLoadTo))
							return false;
						buffLoadTo += toLoad - bufferSize;
						if (isWrappos || buffLoadTo > buffLoadFrom) { // if
																		// buffLoadTo
																		// overtake
																		// buffLoadFrom
							srcPos += buffLoadTo - buffLoadFrom;
							buffLoadFrom = buffLoadTo;
						}
						isWrappos = true;
					} else {
						load = loadPreviewData(buffer, buffLoadTo, toLoad);
						if (stopPreviewRead || load != toLoad)
							return false;
						buffLoadTo += toLoad;
						if (isWrappos && buffLoadFrom < buffLoadTo) { // if
																		// buffLoadTo
																		// overtake
																		// buffLoadFrom
							srcPos += buffLoadTo - buffLoadFrom;
							buffLoadFrom = buffLoadTo;
						}
						if (buffLoadFrom == bufferSize) {
							buffLoadFrom = 0;
							isWrappos = false;
						}
					}
				}
			}
			return true;
		}

		public int readPD(float[][] data, int start, int len, int firstSampleToRead) {
			if (len > bufferSize)
				len = bufferSize;
			int load = 0;
			if (!load(firstSampleToRead, len) && stopPreviewRead)
				return 0;
			// >>>> calculate index in buffer
			// position in buffer whith the first sample to be copied to data at
			// position start
			int beginIndex = firstSampleToRead - srcPos;
			if (beginIndex < 0
				|| beginIndex > (isWrappos	? ((bufferSize - buffLoadFrom) + buffLoadTo)
											: (buffLoadTo - buffLoadFrom))) // if
																			// beginIndex
																			// begin
																			// befor
																			// or
																			// after
																			// data
																			// in
																			// buffer
				return 0;
			beginIndex += buffLoadFrom;
			if (beginIndex > bufferSize)
				beginIndex -= bufferSize; // wrapping
			// <<<< index in buffer is determinated
			int toLoad = buffLoadTo - beginIndex;
			// if !isWrappos or isWrappos and beginIndex is returned in buffer
			// by overtake bufferSize,
			// or isWrappos but we do not need more samples as available to the
			// end of buffer
			if (toLoad > 0 || len <= bufferSize - beginIndex) {
				if (toLoad <= 0 || len < toLoad)
					toLoad = len;
				for (int i = 0; i < buffer.length; i++) {
					assert buffer[i].length >= beginIndex + toLoad
							&& data[i].length >= start + toLoad;
					System.arraycopy(buffer[i], beginIndex, data[i], start, toLoad);
				}
			} else { // if isWrappos and we have to wrap over buffer
				toLoad = bufferSize - beginIndex;
				for (int i = 0; i < buffer.length; i++) {
					assert buffer[i].length >= beginIndex + toLoad
							&& data[i].length >= start + toLoad;
					System.arraycopy(buffer[i], beginIndex, data[i], start, toLoad);
				}
				len -= toLoad;
				len = len > buffLoadTo ? buffLoadTo : len;
				for (int i = 0; i < buffer.length; i++) {
					System.arraycopy(buffer[i], 0, data[i], start + toLoad, len);
				}
				toLoad += len;
			}
			return toLoad;
		}

		public void resetCounters() {
			buffLoadFrom = 0;
			buffLoadTo = 0;
			isWrappos = false;
			srcPos = 0;
		}

	}

}
