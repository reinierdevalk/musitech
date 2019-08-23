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

import javax.sound.sampled.AudioFormat;

import de.uos.fmt.musitech.audio.floatStream.FISReader;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;

/**
 * FISFilter implements a FIR or IIR linear filter that reads from an
 * FloatInputStream and filters its output.
 * 
 * @author tweyde
 */
public class FISFilter implements FloatInputStream, FISReader {

    //	private int order;
    private boolean infinite = false; //  the type of the filter
    private float aCoefficients[]; // the coefficients for an fir
    private float bCoefficients[]; // the additional coefficients for an iir
    private float inBuffer[][]; // buffer for input values, earliest samples
                                // coming first
    private float outBuffer[][]; // buffer for output values, earliest samples
                                 // coming first
    private int inDataEnd = 0; // end index of data in input buffer, exclusive
    private int processingEnd = 0;
    // end index of processed data in output buffer, exclusive
    private int outEnd = 0; // end of data in output buffer, exclusive
    private int buffSize = 1024; // buffer size
    private float gain = 1.0f; // gain multiplied onto the output to avoid
                               // distortions
    boolean endReached = false; // true if the input stream has reached its end

    FloatInputStream floatInputStream; //  the stream to read from

    /**
     * Default constructor. The filter will return -1 on read, while no
     * FloatInputStream is set.
     */
    public FISFilter() {
        this(null, new float[] {1.0f});
    }

    /**
     * Creates an FIR-filtered-stream from the given FloatInputStream using the
     * supplied coefficients.
     * 
     * @param fis the stream to read from.
     * @param aCoefficients the array of coefficients
     */
    public FISFilter(FloatInputStream fis, float[] aCoefficients) {
        this(fis, aCoefficients, null);

    }

    /**
     * Creates an IIR-filtered-stream from the given FloatInputStream using the
     * supplied coefficients.
     * 
     * @param fis the FloatInputStream
     * @param aCoefficients the aCoefficients array
     * @param bCoefficients the bCoefficients array must be of the length as the
     *            aCoefficients
     */
    public FISFilter(FloatInputStream fis, float[] aCoefficients, float[] bCoefficients) {
        setCoefficients(aCoefficients, bCoefficients);
        setFloatInputStream(fis);
    }

    /**
     * setCoefficients sets the coefficients to the given arrays.
     * 
     * @param aCoefficients the non-recursive coefficients
     * @param bCoefficients the recursive coefficients
     */
    public void setCoefficients(float[] aCoefficients, float[] bCoefficients) {
        this.aCoefficients = aCoefficients;
        // set bCoefficients only if not null
        if (bCoefficients != null) {
            // check length
            if (bCoefficients.length != aCoefficients.length)
                throw new IllegalArgumentException("Both coefficient arrays need to have the same size.");
            this.bCoefficients = bCoefficients;
        }
        inDataEnd = aCoefficients.length;
        processingEnd = aCoefficients.length;
        // TODO adjust the lenght of the inBuffer
        infinite = (bCoefficients != null);
    }

    /**
     * fillInBuffer fills the inBuffer by reading from the stream.
     * 
     * @return the number of samples read.
     */
    public int fillInBuffer() {
        int samplesRead = 0;
        if (inDataEnd < inBuffer[0].length) {
            try {
                samplesRead = floatInputStream.read(inBuffer, inDataEnd, inBuffer[0].length - inDataEnd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (samplesRead < 0) {
                endReached = true;
            } else {
                inDataEnd += samplesRead;
            }
        }
        return samplesRead;
    }

    /**
     * processData applies the filter processing to the Data from the point to
     * which filtering has been done (processingEnd) to the current end of the
     * data.
     * 
     * @return the number of samples processed.
     */
    private int processData() {
        processData(processingEnd, inDataEnd);
        return inDataEnd - processingEnd;
    }

    /**
     * processData applies the filter operations in the specified range
     * 
     * @param from start position (inclusive)
     * @param to end position (exclusive)
     */
    private void processData(int from, int to) {
        int pos;
        for (pos = from; pos < to; pos++) {
            for (int channel = 0; channel < outBuffer.length; channel++) {
                float value = 0.0F;
                if (!infinite) {
                    for (int i = 0; i < aCoefficients.length; i++) {
                        value += inBuffer[channel][pos - i] * aCoefficients[i];
                    }
                } else {
                    for (int i = 0; i < aCoefficients.length; i++) {
                        value += inBuffer[channel][pos - i] * aCoefficients[i];
                    }
                    for (int i = 1; i < aCoefficients.length; i++) {
                        if (Float.isInfinite(outBuffer[channel][pos - i] * bCoefficients[i])) {
                            continue;
                        }
                        value -= outBuffer[channel][pos - i] * bCoefficients[i];
                    }
                }
                if (Float.isInfinite(value)) {
                    value = 0;
                }
                if (value > 70000 || value < -70000) {
                    value = 0;
                }
                //				outBuffer[channel][pos] = value;
                outBuffer[channel][pos] = value * gain;
            }
        }
        processingEnd = pos;
    }

    //	/** This is no longer needed, but it can serve for illustration
    // 
    //		 * processData applies the filter operations at the specified position.
    //		 * @param pos
    //		 */
    //		private void processData(int pos) {
    //			for (int channel = 0; channel < outBuffer.length; channel++) {
    //				float value = 0;
    //				for (int i = 0; i < aCoefficients.length; i++) {
    //					value += inBuffer[channel][pos - i] * aCoefficients[i];
    //				}
    //				if (infinite) {
    //					for (int i = 1; i < aCoefficients.length; i++) {
    //						value += outBuffer[channel][pos - i] * bCoefficients[i];
    //					}
    //				}
    //				outBuffer[channel][pos] = value;
    //				// outBuffer[channel][pos] = value * gain;
    //				processingEnd = pos + 1;
    //			}
    //		}

    /**
     * Returns the floatInputStream from which the data are read.
     * 
     * @return FloatInputStream
     */
    public FloatInputStream getFloatInputStream() {
        return floatInputStream;
    }

    /**
     * Sets the floatInputStream from which the data are read.
     * 
     * @param floatInputStream The floatInputStream to set
     */
    public FISReader setFloatInputStream(FloatInputStream fis) {
        this.floatInputStream = fis;
        if (fis != null) {
            inBuffer = new float[floatInputStream.getFormat().getChannels()][buffSize + aCoefficients.length];
            for (int i = 0; i < inBuffer.length; i++) {
                for (int j = 0; j < inBuffer[i].length; j++) {
                    inBuffer[i][j] = 0.0F;
                }
            }
            outBuffer = new float[floatInputStream.getFormat().getChannels()][buffSize + aCoefficients.length];
            for (int i = 0; i < outBuffer.length; i++) {
                for (int j = 0; j < outBuffer[i].length; j++) {
                    outBuffer[i][j] = 0.0F;
                }
            }
        }
        endReached = false;
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
     * Reads the filtered data from the
     * 
     * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float, int, int)
     */
    public int read(float[][] data, int start, int len) {
        int samplesRead=0;
        int dataWritePos = start;
        int toRead = len;
        do {
            if (!endReached) {
                samplesRead = fillInBuffer();
            }
            processData();
            int writeLen = Math.min(toRead, processingEnd - aCoefficients.length);
            // for all channels
            for (int channel = 0; channel < inBuffer.length; channel++) {
                // copy results from the outBuffer to the read array.
                System.arraycopy(outBuffer[channel], aCoefficients.length, data[channel], dataWritePos, writeLen);
                // shift data inInput buffer down by writeLen
                System
                        .arraycopy(inBuffer[channel], writeLen, inBuffer[channel], 0, inBuffer[channel].length
                                                                                      - writeLen);
                // shift data in the outBuffer down by writeLen
                System.arraycopy(outBuffer[channel], writeLen, outBuffer[channel], 0, outBuffer[channel].length
                                                                                      - writeLen);
            }
            processingEnd -= writeLen;
            inDataEnd -= writeLen;
            toRead -= writeLen;
            dataWritePos += writeLen;
        } while (samplesRead > 0 && toRead > 0);
        int retVal = len - toRead; // TODO just for testing
        if (endReached && retVal == 0)
            return -1;
        else
            return retVal;
    }

    /**
     * @see de.uos.fmt.musitech.audio.FloatInputStream#read(float)
     */
    public int read(float[][] data) {
        return read(data, 0, data[0].length);
    }

    public long skip(long n) throws IOException {
        return floatInputStream.skip(n);
    }

    public void reset() throws IOException {
        floatInputStream.reset();
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
     */
    public AudioFormat getFormat() {
        return floatInputStream.getFormat();
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
     */
    public long remainingSamples() {
        return floatInputStream.remainingSamples();
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
     */
    public long getPositionInSamples() {
        return floatInputStream.getPositionInSamples();
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
     */
    public void setPositionInSamples(long newPos) throws IOException {
        floatInputStream.setPositionInSamples(newPos);
    }

    /**
     * Returns the aCoefficients, i.e. those for FIR.
     * 
     * @return float[]
     */
    public float[] getACoefficients() {
        return aCoefficients;
    }

    /**
     * Returns the bCoefficients, i.e. those for IIR.
     * 
     * @return float[]
     */
    public float[] getBCoefficients() {
        return bCoefficients;
    }

    /**
     * @return
     */
    public float getGain() {
        return gain;
    }

    /**
     * @param f
     */
    public void setGain(float f) {
        gain = f;
    }

}