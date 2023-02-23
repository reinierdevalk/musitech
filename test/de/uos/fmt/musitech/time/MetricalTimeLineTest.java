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
 * File MetricalTimeLineTest.java
 * Created on 04.03.2004
 */

package de.uos.fmt.musitech.time;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Test case for MetricalTimeLine
 * @author tweyde
 */
public class MetricalTimeLineTest extends TestCase {
	
	MetricalTimeLine timeLine;	
	
	public void testFromMeasureBeatRemainder(){
		Rational rational;
		int[] value = new int[4];

		rational = new Rational(5,4);
		value[0] = 2;	// measure
		value[1] = 2;	// beat
		value[2] = 0;	// remainder.numerator
		value[3] = 1;	// remainder.denominator

		if ( timeLine.fromMeasureBeatRemainder(value).compare(rational) != 0 ) fail();
	}

	public void testToMeasureBeatRemainder(){
		Rational rational;
		int[] value = new int[4];

		rational = new Rational (1);
		value[0] = 2;	// measure
		value[1] = 1;	// beat
		value[2] = 0;	// remainder.numerator
		value[3] = 1;	// remainder.denominator
		// das Ergebnis stimmt noch nicht!

		if (
			( timeLine.toMeasureBeatRemainder(rational)[0] != value[0] ) ||
			( timeLine.toMeasureBeatRemainder(rational)[1] != value[1] ) ||
			( timeLine.toMeasureBeatRemainder(rational)[2] != value[2] ) ||
			( timeLine.toMeasureBeatRemainder(rational)[3] != value[3] )
		)
		fail();
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		timeLine = new MetricalTimeLine();
	}

}
