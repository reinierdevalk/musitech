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
 * Created on 29.08.2004
 */
package de.uos.fmt.musitech.data.time;


import junit.framework.TestCase;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Jan
 *
 */
public class MTLTest_jan extends TestCase{

	MetricalTimeLine MTL;

		/** 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        MTL = new MetricalTimeLine();
		MTL.setTempo(new Rational(0, 1), 120, 4);
		addMTS();
	}


    /**
	 * 
	 */
	private void addMTS() {
		MTL.add(new TimeSignatureMarker(4, 4, new Rational(0, 1)));
		MTL.add(new TimeSignatureMarker(3, 4, new Rational(2, 1)));
		MTL.add(new TimeSignatureMarker(4, 8, new Rational(4, 1)));
	}

	public void testBeat() {
		System.out.println("*** MetricalTimeLine Test ***");
		MTL.toString();
		Rational rat = new Rational(0, 8);
		System.out.println("Rational " + rat);
		assertTrue("Prev Beat " + rat, MTL.getPreviousBeat(rat).isEqual(0,8));
		assertTrue("PrOS Beat " + rat, MTL.getPreviousOrSameBeat(rat).isEqual(0,8));
		assertTrue("Next Beat " + rat, MTL.getNextBeat(rat).isEqual(1,4));
		assertTrue("NeOS Beat" + rat, MTL.getNextOrSameBeat(rat).isEqual(0,1));
		rat = new Rational(1, 8);
		assertTrue("Prev Beat " + rat, MTL.getPreviousBeat(rat).isEqual(0,8));
		assertTrue("PrOS Beat " + rat, MTL.getPreviousOrSameBeat(rat).isEqual(0,8));
		assertTrue("Next Beat " + rat, MTL.getNextBeat(rat).isEqual(1,4));
		assertTrue("NeOS Beat " + rat, MTL.getNextOrSameBeat(rat).isEqual(1,4));
		rat = new Rational(2, 8);
		assertTrue("Prev Beat " + rat, MTL.getPreviousBeat(rat).isEqual(0,8));
		assertTrue("PrOS Beat " + rat, MTL.getPreviousOrSameBeat(rat).isEqual(1,4));
		assertTrue("Next Beat " + rat, MTL.getNextBeat(rat).isEqual(2,4));
		assertTrue("NeOS Beat " + rat, MTL.getNextOrSameBeat(rat).isEqual(1,4));
		rat = new Rational(-1, 8);
		assertTrue("Prev Beat " + rat, MTL.getPreviousBeat(rat).isEqual(-1,4));
		assertTrue("PrOS Beat " + rat, MTL.getPreviousOrSameBeat(rat).isEqual(-1,4));
		assertTrue("Next Beat " + rat, MTL.getNextBeat(rat).isEqual(0,4));
		assertTrue("NeOS Beat " + rat, MTL.getNextOrSameBeat(rat).isEqual(0,4));
		rat = new Rational(4, 1);
		
		assertTrue("Prev Beat " + rat, MTL.getPreviousBeat(rat).isEqual(4,1));
		assertTrue("PrOS Beat " + rat, MTL.getPreviousOrSameBeat(rat).isEqual(4,1));
		assertTrue("Next Beat " + rat, MTL.getNextBeat(rat).isEqual(33,8));
		assertTrue("NeOS Beat " + rat, MTL.getNextOrSameBeat(rat).isEqual(4,1));
	}
	
	/**
     * 
     */
    public void testMeasure() {
        
    }
	
	private void oldFindMethods() {
		System.out.println("*** MetricalTimeLine Test ***");
		MTL.toString();
		for (int i = -20; i < 20; i++) {
			Rational rat = new Rational(i, 8);
			System.out.println("Rational " + rat);
			System.out.println("  Prev Beat: " + MTL.getPreviousBeat(rat));
			System.out.println("  Next Beat: " + MTL.getNextBeat(rat));
			System.out.println("  Prev Meas: " + MTL.getPreviousMeasure(rat));
			System.out.println("  Next Meas: " + MTL.getNextMeasure(rat));
			
			System.out.println("  PrOS Beat: " + MTL.getPreviousOrSameBeat(rat));
			System.out.println("  NeOS Beat: " + MTL.getNextOrSameBeat(rat));
			System.out.println("  PrOS Meas: " + MTL.getPreviousOrSameMeasure(rat));
			System.out.println("  NeOS Meas: " + MTL.getNextOrSameMeasure(rat));
			System.out.println("  Beat Pos : " + MTL.getBeatPosition(rat));
			System.out.println("");
		}
	}

}
