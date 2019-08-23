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
package de.uos.fmt.musitech.audio.processor;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import de.uos.fmt.musitech.audio.floatStream.FISReader;
import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;

/**
 * PanningController This class changes the panning of floatInputStream with two
 * channels
 * 
 * @author Jan-Hendrik Kramer
 */

public class PanningController implements FloatInputStream, FISReader {

    FloatInputStream floatInputStream;

    float[] gain = new float[2];

    /**
     * ATTENTION! FloatInputStream must be initialised!
     */
    public PanningController() {
        this(null);
    }

    /**
     * This constructor conctructs a new VolumeController input stream
     * 
     * @param fis
     *            the source FloatInputStream
     */

    public PanningController(FloatInputStream fis) {

        setFloatInputStream(fis);

    }

    public void init() {
        gain[0] = 1.0f;
        gain[1] = 1.0f;
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
     */
    public int read(float[][] data) throws IOException {
        return read(data, 0, data[0].length);
    }

    /**
     * This method changes the volume of floatInputStream with factor gain for
     * each channel
     * @param data The buffer to write the data to.
     * @param start The start position.
     * @param len The number of frames to write.
     * @return The number of frames written.
     * @throws IOException 
     */
    public int read(float[][] data, int start, int len) throws IOException {

        int retval = floatInputStream.read(data, start, len);

        for (int channel = 0; channel < data.length; channel++) {
            for (int i = start; i < len; i++) {

                data[channel][i] *= gain[channel];
            }
        }
        return retval;

    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
     */
    public AudioFormat getFormat() {
        return floatInputStream.getFormat();
    }

    public long skip(long n) throws IOException {
        return floatInputStream.skip(n);
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
     */
    public void reset() throws IOException {
        floatInputStream.reset();
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
     * Sets the gain.
     * 
     * @param gain
     *            The gain to set negative gain is not allowed
     */

    public void setGain(float gain, int channel) {
        if (gain < 0)
            throw new IllegalArgumentException(
                    "negative gain value is not allowed");
        else
            this.gain[channel] = gain;
    }

    /**
     * This method getGain returns the gain value
     * 
     * @return float
     */
    public float getGain(int channel) {
        return gain[channel];
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FISReader#setFloatInputStream(de.uos.fmt.musitech.audio.floatStream.FloatInputStream)
     */
    public FISReader setFloatInputStream(FloatInputStream fis) {
        this.floatInputStream = fis;
        return this;
    }

    /**
     * @see de.uos.fmt.musitech.audio.floatStream.FISReader#getFloatInputStream()
     */
    public FloatInputStream getFloatInputStream() {
        return floatInputStream;
    }
}
