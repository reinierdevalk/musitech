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
package de.uos.fmt.musitech.audio.floatStream.generators;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import de.uos.fmt.musitech.audio.floatStream.FloatInputStream;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * This is an abstract base class for oscillators.
 * @author Martin Gieseking, Tillman Weyde
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public abstract class FloatOscillator implements FloatInputStream {

	/**
	 * 2 pi constant for periodic oscillators.
	 */
	public static final float pi2 = (float) (2 * Math.PI);

	/** samples per second */
	protected int sampleRate = 44100;
	/**
	 * the number of channels, although it does not have to be used:
	 * read(float[][],...) will usually work with any number of channels for
	 * generators that copy mono signals all output signals.
	 */
	protected int channelNum = 1;

	/**
	 * Set the cannel number.
	 * @param argChannelNum The channelNum to set.
	 */
	public void setChannelNum(int argChannelNum) {
		this.channelNum = argChannelNum;
	}

	/**
	 * Returns a single sample.
	 * @return the next generated sample.
	 */
	public abstract float read();

	/**
	 * Fills the provided array with the generated signal copied into every
	 * channel.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][])
	 */
	@Override
	public int read(float[][] data) throws IOException {
		return read(data, 0, data[0].length);
	}

	/**
	 * Fills the provided array with the generated signal copied into every
	 * channel. This implementation is not particularly efficient and should be
	 * overridden, where efficiency is an issue.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#read(float[][],
	 *      int, int)
	 */
	@Override
	public int read(float[][] data, int start, int len) throws IOException {
		if (DebugState.DEBUG_AUDIO && data.length != channelNum)
			System.err
					.println("WARNING: "
								+ getClass()
								+ ".read(float[][], int, int ) data.length does not match channelNum.");
		int end = start + len;
		for (int i = start; i < end; i++) {
			float val = read();
			for (int channel = 0; channel < data.length; channel++) {
				data[channel][i] = val;
			}
		}
		return len;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		return new AudioFormat(sampleRate, 1, channelNum, true, true);
	}

	/**
	 * Gets the sample rate.
	 * @return The sample rate in Hz.
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * Sets the sample rate in Hz. Implementations should respond immediately
	 * where possible but avoid clicks when changing the rate.
	 * @param argSampleRate The new sample rate.
	 */
	public void setSampleRate(int argSampleRate) {
		this.sampleRate = argSampleRate;
	}

	/**
	 * The number of samples remaining, unlimited for oscillators.
	 * @return -1 indicating that this stream has no defined end.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#remainingSamples()
	 */
	@Override
	public long remainingSamples() {
		return -1;
	}

	/**
	 * The oscillator has no defined position or duration.
	 * @return -1
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public long getPositionInSamples() {
		return -1;
	}

	/**
	 * Does nothing, should be overridden where possible.
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#getPositionInSamples()
	 */
	@Override
	public void setPositionInSamples(long newPos) throws IOException {
	}
}
