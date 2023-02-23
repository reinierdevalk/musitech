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
 * File MyMathTest.java Created on 08.01.2005 by Tillman Weyde.
 */

package de.uos.fmt.musitech.utility.math;

import de.uos.fmt.musitech.utility.collection.Shuffler;
import de.uos.fmt.musitech.utility.math.MyMath;
import junit.framework.TestCase;

/**
 * Tests the MyMath class.
 * 
 * @author Tillman Weyde
 */
public class MyMathTest extends TestCase {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(MyMathTest.class);
	}

	public void testFloorPower2() {
		assertEquals(0, MyMath.floorPower2(-1));
		assertEquals(0, MyMath.floorPower2(0));
		assertEquals(1, MyMath.floorPower2(1));
		assertEquals(2, MyMath.floorPower2(2));
		assertEquals(2, MyMath.floorPower2(3));
		assertEquals(4, MyMath.floorPower2(4));
		assertEquals(4, MyMath.floorPower2(7));
	}

	public void testFloorEven() {
		assertEquals(0, MyMath.floorEven(0));
		assertEquals(0, MyMath.floorEven(1));
		assertEquals(2, MyMath.floorEven(2));
		assertEquals(2, MyMath.floorEven(3));
		assertEquals(4, MyMath.floorEven(4));
		assertEquals(4, MyMath.floorEven(5));
		assertEquals(6, MyMath.floorEven(6));
	}

	public void testCeilPower2() {
		assertEquals(0, MyMath.ceilPower2(-1));
		assertEquals(0, MyMath.ceilPower2(0));
		assertEquals(1, MyMath.ceilPower2(1));
		assertEquals(2, MyMath.ceilPower2(2));
		assertEquals(4, MyMath.ceilPower2(3));
		assertEquals(4, MyMath.ceilPower2(4));
		assertEquals(8, MyMath.ceilPower2(7));
	}

	public void testDBconversion() {

		// dB amplitude
		double gain = 10;
		double dB = MyMath.linearToDBAmp(gain);
		assertTrue(dB == 10);
		gain = MyMath.dBToLinearAmp(dB);
		assertTrue(Math.abs(gain - 10) < 0.000001); // allow for rounding errors

		gain = 100;
		dB = MyMath.linearToDBAmp(gain);
		assertTrue(dB == 20);
		gain = MyMath.dBToLinearAmp(dB);
		assertTrue(Math.abs(gain - 100) < 0.000001); // allow for rounding
														// errors

		gain = 2;
		dB = MyMath.linearToDBAmp(gain);
		assertTrue(Math.abs(dB - 3) < 0.05); // 3 is only approximate value
		gain = MyMath.dBToLinearAmp(dB);
		assertTrue(Math.abs(gain - 2) < 0.000001); // allow for rounding errors

		// dB energy

		gain = 2;
		dB = MyMath.linearToDBEnergy(gain);
		assertTrue(Math.abs(dB - 6) < 0.05); // 6 is only approximate value
		gain = MyMath.dBEnergyToLinear(dB);
		assertTrue(Math.abs(gain - 2) < 0.000001); // allow for rounding errors

		gain = 10;
		dB = MyMath.linearToDBEnergy(gain);
		assertTrue(dB == 20);
		gain = MyMath.dBEnergyToLinear(dB);
		assertTrue(Math.abs(gain - 10) < 0.000001); // allow for rounding errors

		gain = 100;
		dB = MyMath.linearToDBEnergy(gain);
		assertTrue(Math.abs(dB - 40) < 0.000001); // allow for rounding errors
		gain = MyMath.dBEnergyToLinear(dB);
		assertTrue(Math.abs(gain - 100) < 0.000001); // allow for rounding
														// errors
	}

	public void testMedian() {
		for (int i = 0; i < 10; i++) {
			{
				int array[] = {0, 1, 2, 3, 4, 5, 6, 7};
				Shuffler.shuffleArray(array);
				int test = MyMath.median(array);
				assertEquals(4, test);
			}
			{
				int array[] = {0, 1, 2, 3, 4, 5, 6, 7, 8};
				Shuffler.shuffleArray(array);
				int test = MyMath.median(array);
				assertEquals(4, test);
			}
			{
				int array[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
				Shuffler.shuffleArray(array);
				int test = MyMath.median(array);
				assertEquals(5, test);
			}
		}
	}

}
