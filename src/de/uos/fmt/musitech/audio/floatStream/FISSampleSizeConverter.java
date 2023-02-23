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

/**
 * 
 * Gets a FloatInputStream whith determinated SampleSize, and converts it to
 * another SampleSize. Conversion is only correct whith signed datastreams.
 *
 * @author Nicolai Strauch
 */
public class FISSampleSizeConverter implements FloatInputStream, FISReader {

	FloatInputStream fis;
	byte sourceBitRate;
	byte targetBitRate;

//	/**
//	 * ATTENTION!
//	 * FloatInputStream must be initialised!
//	 */
//	public FISSampleSizeConverter() {
//		this(null);
//	}

	public FISSampleSizeConverter(FloatInputStream inputStream) {
		this(inputStream, DefaultFormat.getSampleSizeInBits());
	}
	
	/**
	 * Constructs a new SampleSizeConverter
	 * 
	 * @param inputStream
	 * @param bitsPerSample the bits per Sample (per Channel, not in all Channels!)
	 */
	public FISSampleSizeConverter(FloatInputStream inputStream, int argSampleSizeInBits) {
		setFloatInputStream(inputStream);
		setTargetSampleSizeInBits(argSampleSizeInBits);
	}

	@Override
	public FISReader setFloatInputStream(FloatInputStream inputStream) {
		fis = inputStream;
		sourceBitRate = (byte) (fis.getFormat().getSampleSizeInBits());
		return this;
	}
	@Override
	public FloatInputStream getFloatInputStream() {
		return fis;
	}

	public void setTargetSampleSizeInBits(int newRate) {
		targetBitRate = (byte) newRate;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float[][])
	 */
	@Override
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float[][], int, int)
	 */
	@Override
	public int read(float[][] data, int start, int len) throws IOException {
		int out = fis.read(data, start, len);
		if (sourceBitRate != targetBitRate)
			convert(data, start, len);
		return out;
	}
	/**
	 * Multiple all Samples by the Faktor:
	 * ( (2^(newBitRate-1)) / (2^(oldBitRate-1)) )
	 * Only useful whith signed datastreams!
	 * @param data
	 * @param start
	 * @param len
	 */
	private void convert(float[][] data, int start, int len) {
		double factor = 0;
		if(targetBitRate == 1){ // data is normalized
			factor = (1 << (sourceBitRate - 1));
		}else{
			factor = (double) (1 << (targetBitRate - 1)) // at 8 bit this will return 128.
								/ (double) (1 << (sourceBitRate - 1));
		}
		for (int i = 0; i < data.length; i++){ // channel
			for (int j = start; j < start + len; j++){ // samples
				//TODO: replace multiplication by bit-level modification
				// except when normalising to [-1,1].
				data[i][j] *= factor;
			}
		}
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		AudioFormat in = fis.getFormat();
		AudioFormat out =
			new AudioFormat(
				in.getEncoding(),
				in.getSampleRate(),
				targetBitRate,
				in.getChannels(),
				in.getFrameSize(),
				in.getFrameRate(),
				in.isBigEndian());
		return out;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		return fis.skip(n);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		fis.reset();
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return fis.remainingSamples();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return fis.getPositionInSamples();
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
		fis.setPositionInSamples(newPos);
	}
	

}
