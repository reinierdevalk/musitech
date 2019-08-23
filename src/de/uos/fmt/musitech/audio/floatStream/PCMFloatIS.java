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

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import de.uos.fmt.musitech.audio.AudioUtil;

/**  
 * This class implements a FloatInputStream that
 * reads its data from an AudioInputStream. 
 * 
 * @author Nicolai Strauch, Tillman Weyde, Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */

public class PCMFloatIS implements FloatInputStream {
	private AudioInputStream inputStream;
	
	boolean normalised;
	
	private long samplesRead;

	
	/**
	 * Constructs a new PCMFloatIS reading from the passed AudioInputStream.
	 * @param inputStream The AudioInputStream to read from.
	 */
	public PCMFloatIS(AudioInputStream inputStream) {
		setInputStream(inputStream);
	}

	/** 
	 * setInputStream sets the AudioInputStream to read from.
	 * @param inputStream The stream to read from.
	 */
	public void setInputStream(AudioInputStream inputStream) {
		this.inputStream = inputStream;
		
	}

	/** 
	 * read a frame 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
	 */
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}

	/** 
	 * read an number of frames  
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][], int, int)
	 */
	public int read(float[][] data, int start, int len) throws IOException {
		len = AudioUtil.readSamples(data, start, len, inputStream, normalised);
		samplesRead += len; 
		return len;
	}

	/** 
	 * getInputStream get the current input stream
	 * @return AudioInputStream
	 */
	public AudioInputStream getInputStream() {
		return inputStream;
	}

//	private static byte[] skipBuffer;
	
	/**
	 * Skip a number of frames
	 * using AudioInputStream.skip(n*frameSize).
	 * @param n Frames to be skipped
	 * @return the number of frames skipped, or -1 if streamend is reached.
	 */ 
	 // (not more) * @return long Frames skipped, if the end of stream is reached, the returned number negative and equals -(skipedFrames+1).
	 
	public long skip(long n) throws IOException {
		int frameSize = inputStream.getFormat().getFrameSize();
//		long skipBytes = n * frameSize;
//		long skippedBytes = 0;
//		int skipNow = 0;
//		if (skipBuffer == null)
//			skipBuffer = new byte[16384];
//		for (int skippedNow = 0; skipBytes > 0; skipBytes -= skippedNow) {
//			skipNow = (int) (skipBytes>skipBuffer.length ? skipBuffer.length : skipBytes);
//			skippedNow = inputStream.read(skipBuffer, 0, (int) skipNow);
//			if (skippedNow < 0)
//				return - (skippedBytes + 1);
//			skippedBytes += skippedNow;
//		}
//		assert(
//			skippedBytes % frameSize != 0) : "(skippedBytes % frameSize != 0)";
//		return skippedBytes / frameSize;
		long skippedBytes = inputStream.skip(n*frameSize);
		return skippedBytes/frameSize;
	}
	
	/** 
	 * Resets the stream, effect depends on the AudioInputStream used. 
	 * TODO adapt to InputStream behaviour
	 * 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	public void reset() throws IOException {
		inputStream.reset();
	}

	
	/**
	 * Gets audio format information.
	 * The audio format information is kept from the data source.
	 * Of these only channel number and samplerate are relevant for the 
	 * float input stream.
	 * The other infomation can be used when converting to byte streams. 
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	public AudioFormat getFormat() {
		AudioFormat in = inputStream.getFormat();
		AudioFormat out = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, in.getSampleRate(), normalised?1:in.getSampleSizeInBits(), in.getChannels(), in.getFrameSize(), in.getFrameRate(), true);
		return out;
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
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	public long remainingSamples() {
		return inputStream.getFrameLength() - samplesRead;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	public long getPositionInSamples() {
		return samplesRead;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#setPositionInSamples(long)
	 */
	public void setPositionInSamples(long newPos) throws IOException {
		reset();
		skip(newPos);
	}

}
