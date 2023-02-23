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
package de.uos.fmt.musitech.audio.proc.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;

import de.uos.fmt.musitech.audio.floatStream.FISReader;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;

/**
 * @author Felix Kugel
 *
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class FXFloatFilterInputStream implements FloatInputStream, FISReader {

	Coefficients coefficients;

	boolean infinite = true;

	int aSparseIndices[];
	int bSparseIndices[];

	float inBuffer[][];
	float outBuffer[][];
	int inDataEnd = 0; // end of data exclusive
	int processingEnd = 0; // end of data exclusive
	int outEnd = 0; // end of data exclusive
	int buffSize = 1024;
	float gain = 1.0f;

	FloatInputStream floatInputStream;

	/**
	 * ATTENTION!
	 *   FloatInputStream must be initialised!
	 *   Default Coefficients are used. Set them again.
	 */
	public FXFloatFilterInputStream() {
		setCoefficients(DEFAULT_COEFFICIENTS);
	}

	public FXFloatFilterInputStream(FloatInputStream fis, Coefficients coefficients) {
		setFloatInputStream(fis);
		setCoefficients(coefficients);
	}

	public void setCoefficients(Coefficients coefficients) {
//		float aCoefficients[] = coefficients.getXCoefficients();
//		float bCoefficients[] = coefficients.getYCoefficients();

		infinite = true;

		this.coefficients = coefficients;

		inDataEnd = coefficients.getOrder();
		processingEnd = coefficients.getOrder();

		calculateSparseIndices();
	}

	/** put the indices of the non-null rows of the matrix into {a,b}SparseIndices */
	void calculateSparseIndices() {
		Vector aSi = new Vector();
		Vector bSi = new Vector();

		for (int i = 0; i < this.coefficients.getOrder(); i++) {
			if (coefficients.getXCoefficients()[i] > 0)
				aSi.add(new Integer(i));
			if (coefficients.getYCoefficients()[i] > 0)
				bSi.add(new Integer(i));
		}

		aSparseIndices = new int[aSi.size()];
		bSparseIndices = new int[bSi.size()];

		{
			int i = 0;
			for (Iterator it = aSi.iterator(); it.hasNext();) {
				aSparseIndices[i++] = ((Integer) it.next()).intValue();
			}
		}

		{
			int i = 0;
			for (Iterator it = bSi.iterator(); it.hasNext();) {
				bSparseIndices[i++] = ((Integer) it.next()).intValue();
			}
		}
	}

	public int fillInBuffer() {
		int samplesRead = 0;
		if (inDataEnd < inBuffer[0].length) {
			try {
				samplesRead = floatInputStream.read(inBuffer, inDataEnd, inBuffer[0].length - inDataEnd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			inDataEnd += samplesRead;
		}
		return samplesRead;
	}

	
	/** 
	 * processData processes all data until the end of available input
	 * @return
	 */
	public int processData() {
		processData(processingEnd,inDataEnd);
		return inDataEnd-processingEnd;
	}

	public void processData(int pos) {
		for (int channel = 0; channel < outBuffer.length; channel++) {
			float value = 0;
			for (int i = 0; i < aSparseIndices.length; i++) {
				value += inBuffer[channel][pos
					- aSparseIndices[i]] * coefficients.getXCoefficients()[aSparseIndices[i]];
			}
			if (infinite) {
				for (int i = 0; i < bSparseIndices.length; i++) {
					value += outBuffer[channel][pos
						- bSparseIndices[i]] * coefficients.getYCoefficients()[bSparseIndices[i]];
				}
			}
			outBuffer[channel][pos] = value * gain;

			processingEnd = pos + 1;
		}
	}

	
	/** 
	 * processData processes all data starting at from (inclusive) 
	 * ending at to (exclusive).
	 * @param from strarting point of processing (inclusive)
	 * @param to end point of processing (exclusive)
	 */
	public void processData(int from, int to) {
		for (int pos = from; pos < to; pos++) {
			for (int channel = 0; channel < outBuffer.length; channel++) {
				float value = 0;
				for (int i = 0; i < aSparseIndices.length; i++) {
					value += inBuffer[channel][pos - aSparseIndices[i]] * coefficients.getXCoefficients()[aSparseIndices[i]];
				}
				if (infinite) {
					for (int i = 0; i < bSparseIndices.length; i++) {
						value += outBuffer[channel][pos - bSparseIndices[i]] * coefficients.getYCoefficients()[bSparseIndices[i]];
					}
				}
				outBuffer[channel][pos] = value * gain;
					
				processingEnd = pos + 1;
			}
		}
	}


	/**
	 * Returns the floatInputStream.
	 * @return FloatInputStream
	 */
	@Override
	public FloatInputStream getFloatInputStream() {
		return floatInputStream;
	}

	/**
	 * Sets the floatInputStream.
	 * @param floatInputStream The floatInputStream to set
	 */
	@Override
	public FISReader setFloatInputStream(FloatInputStream fis) {
		this.floatInputStream = fis;
		if (fis != null) {
			inBuffer = new float[floatInputStream.getFormat().getChannels()][buffSize + coefficients.getOrder()];
			outBuffer = new float[floatInputStream.getFormat().getChannels()][buffSize + coefficients.getOrder()];
		}
		return this;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#getChannelNumber()
	 */
	public int getChannelNumber() {

		if (floatInputStream == null)
			return 0;
		return floatInputStream.getFormat().getChannels();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float, int, int)
	 */
	@Override
	public int read(float[][] data, int start, int len) throws IOException {

		// XXXX return floatInputStream.read(data, start, len);

		int samplesRead;
		int toRead = len;
		do {
			samplesRead = fillInBuffer();
			processData();
			int writeLen = Math.min(toRead, processingEnd - coefficients.getOrder());
			for (int channel = 0; channel < inBuffer.length; channel++) {
				System.arraycopy(outBuffer[channel], coefficients.getOrder(), data[channel], start, writeLen);
				System.arraycopy(
					inBuffer[channel],
					writeLen,
					inBuffer[channel],
					0,
					inBuffer[channel].length - writeLen);
				System.arraycopy(
					outBuffer[channel],
					writeLen,
					outBuffer[channel],
					0,
					inBuffer[channel].length - writeLen);
			}
			processingEnd -= writeLen;
			inDataEnd -= writeLen;
			toRead -= samplesRead;
		} while (samplesRead > 0 && toRead > 0);
		return len - toRead;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float)
	 */
	@Override
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}

	/**
	 * Returns the gain.
	 * @return float
	 */
	public float getGain() {
		return gain;
	}

	/**
	 * Sets the gain.
	 * @param gain The gain to set
	 */
	public void setGain(float gain) {
		this.gain = gain;
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#FloatInputStream()
	 */
	public void FloatInputStream() {
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		return floatInputStream.getFormat();
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		return floatInputStream.skip(n);
	}
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	@Override
	public void reset() throws IOException {
		floatInputStream.reset();
	}
	
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return floatInputStream.remainingSamples();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return floatInputStream.getPositionInSamples();
	}
	
	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
		floatInputStream.setPositionInSamples(newPos);
	}
	

	public final static Coefficients DEFAULT_COEFFICIENTS = new Coefficients(1);

}
