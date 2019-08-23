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
 * File ScoreNoteTest.java
 * Created on 23.05.2004
 */

package de.uos.fmt.musitech.data.score;

import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.utility.math.Rational;
import junit.framework.TestCase;


/**
 * Unit test for class ScoreNote
 * @author Tillman Weyde
 */
public class ScoreNoteTest extends TestCase {

	/**
	 * TODO comment
	 * 
	 */
	public ScoreNoteTest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * TODO comment
	 * @param arg0
	 */
	public ScoreNoteTest(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
//	public void testTranspose(){
//		ScoreNote snote = new ScoreNote(new ScorePitch('c',0,0),Rational.ZERO);
//		ScoreInterval scInt = new ScoreInterval(ScoreInterval.FIFTH);
//		scInt.setType(ScoreInterval.PERFECT);
//		snote.transpose(scInt);
//		assertEquals(snote.getDiatonic(),'g');
//		assertEquals(snote.getAlteration(),0);
//		assertEquals(snote.getOctave(),0);
//		scInt.setDiatonicInterval(-ScoreInterval.FOURTH);
//		scInt.setType(ScoreInterval.AUGMENTED);
//		snote.transpose(scInt);
//		assertEquals(snote.getDiatonic(),'d');
//		assertEquals(snote.getAlteration(),-1);
//		assertEquals(snote.getOctave(),0);
//		scInt.setDiatonicInterval(ScoreInterval.THIRD);
//		scInt.setType(ScoreInterval.MINOR);
//		snote.transpose(scInt);
//		assertEquals(snote.getDiatonic(),'f');
//		assertEquals(snote.getAlteration(),-1);
//		assertEquals(snote.getOctave(),0);
//
//	}
	
	public void testTranspose(){
		ScoreNote snote = new ScoreNote(new ScorePitch('c',0,0),Rational.ZERO);
		Interval scInt = new Interval(Interval.iSize.FIFTH,Interval.iQual.PERFECT);
		snote.transpose(scInt);
		assertEquals('g',snote.getDiatonic());
		assertEquals(snote.getAlteration(),0);
		assertEquals(snote.getOctave(),0);
		scInt.setIntervalSize(Interval.iSize.FOURTH);
		scInt.setIntervalQuality(Interval.iQual.AUGMENTED);
		scInt.setUp(false);
		snote.transpose(scInt);
		assertEquals(snote.getDiatonic(),'d');
		assertEquals(snote.getAlteration(),-1);
		assertEquals(snote.getOctave(),0);
		scInt.setIntervalSize(Interval.iSize.THIRD);
		scInt.setIntervalQuality(Interval.iQual.MINOR);
		scInt.setUp(true);
		snote.transpose(scInt);
		assertEquals('f',snote.getDiatonic());
		assertEquals(snote.getAlteration(),-1);
		assertEquals(snote.getOctave(),0);

	}
	

	
	/**
	 * Tests method <code>isPossibleValue(String, Object)</code>.
	 */
	public void testIsPossibleValue(){
		ScoreNote sn = new ScoreNote();
		//test class check
		assertFalse(sn.isValidValue("diatonic", new Integer(5)));
		assertFalse(sn.isValidValue("accidental", "fis"));
		//test diatonic values
		assertTrue(sn.isValidValue("diatonic", new Character('c')));
		assertFalse(sn.isValidValue("diatonic", new Character('w')));
		//test accidental values
		assertTrue(sn.isValidValue("alteration", new Byte(new Integer(0).byteValue())));
		assertTrue(sn.isValidValue("alteration", new Byte(new Integer(-1).byteValue())));
		assertFalse(sn.isValidValue("alteration", new Byte(new Integer(6).byteValue())));
		//test octave values
		assertTrue(sn.isValidValue("Octave", new Byte(new Integer(1).byteValue())));
		assertFalse(sn.isValidValue("Octave", new Byte(new Integer(-5).byteValue())));
		//test metricTime values
		assertTrue(sn.isValidValue("metricTime", new Rational(0,1)));
		assertTrue(sn.isValidValue("metricTime", new Rational(3,4)));
		assertFalse(sn.isValidValue("metricTime", new Rational(-1,2)));
		//test metricDuration values
		assertTrue(sn.isValidValue("metricDuration", new Rational(1,8)));
		assertFalse(sn.isValidValue("metricDuration", new Rational(-3,4)));
	}	

}
