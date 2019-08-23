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

import de.uos.fmt.musitech.audio.floatStream.FISPlayer;
import de.uos.fmt.musitech.utility.math.MyMath;

/**
 * This class implements a simple noise generator.
 * 
 * @author Tillman Weyde
 */

public class GaussNoiseOscillator extends FloatOscillator {

	private float stdDev;
	private float mean = 0;


	/**
	 * Creates a new Guassian noise oscillator with a given std deviation.
	 * 
	 * @param argStdDev
	 */
	public GaussNoiseOscillator(float argStdDev) {
		this.stdDev = argStdDev;
	}

	/**
	 * Creates a new Guassian noise oscillator with a given std deviation and mean.
	 * 
	 * @param argStdDev The standard deviation 
	 * @param argMean The mean.
	 */
	public GaussNoiseOscillator(float argStdDev, float argMean) {
		this.stdDev = argStdDev;
		this.mean = argMean;
	}

	/**
	 * Returns a single sample.
	 * 
	 * @return one gaussian value.
	 */
	public float read() {
		return (float) MyMath.gaussRand(stdDev, mean);
	}

	/** always successful, but does nothing
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return n;
	}

	/** do nothing
	 * @see de.uos.fmt.musitech.audio.floatStream.FloatInputStream#reset()
	 */
	public void reset() throws IOException {
	}
	
	/**
	 * Just a small test.
	 * 
	 * @param args ignored
	 */
	public static void main(String[] args) {
		FloatOscillator oszi = new GaussNoiseOscillator(400);
		FISPlayer player = new FISPlayer(oszi);
		player.play();
	}


}
