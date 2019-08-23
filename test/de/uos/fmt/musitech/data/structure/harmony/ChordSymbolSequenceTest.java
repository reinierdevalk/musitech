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
 * Created on 21.06.2004
 */
package de.uos.fmt.musitech.data.structure.harmony;

import junit.framework.TestCase;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * JUnit tests for class <code>ChordSymbolSequence</code>.
 * 
 * @author Kerstin Neubarth
 */
public class ChordSymbolSequenceTest extends TestCase {

	/**
	 * Tests method <code>getLastSymbolBeforeTime(long)</code>.
	 */
	public static void testGetLastBeforeTimeLong() {
		//create test sequence
		ChordSymbolSequence sequence = new ChordSymbolSequence();
		ChordSymbol symbol0 = new ChordSymbol(new Rational(0, 4), 0);
		sequence.add(symbol0);
		ChordSymbol symbol1 = new ChordSymbol(new Rational(1, 4), 100);
		sequence.add(symbol1);
		ChordSymbol symbol2 = new ChordSymbol(new Rational(2, 4), 200);
		sequence.add(symbol2);
		ChordSymbol symbol3 = new ChordSymbol(new Rational(3, 4), 300);
		sequence.add(symbol3);
		//test method getLastSymbolBeforeTime(long)
		assertEquals(sequence.getLastSymbolBeforeTime(50), symbol0);
		ChordSymbol s1 = sequence.getLastSymbolBeforeTime(100);
		assertEquals(sequence.getLastSymbolBeforeTime(100), symbol0);
		assertEquals(sequence.getLastSymbolBeforeTime(150), symbol1);
		assertEquals(sequence.getLastSymbolBeforeTime(250), symbol2);
		assertEquals(sequence.getLastSymbolBeforeTime(500), symbol3);
	}


	/**
	 * Tests method <code>getFirstSymbolAtTime(long)</code>.
	 */
	public static void testGetFirstAtTimeLong() {
		//create test sequence
		ChordSymbolSequence sequence = new ChordSymbolSequence();
		ChordSymbol symbol0 = new ChordSymbol(new Rational(0, 4), 0);
		sequence.add(symbol0);
		ChordSymbol symbol1 = new ChordSymbol(new Rational(1, 4), 100);
		sequence.add(symbol1);
		ChordSymbol symbol2 = new ChordSymbol(new Rational(2, 4), 200);
		sequence.add(symbol2);
		ChordSymbol symbol3 = new ChordSymbol(new Rational(3, 4), 300);
		sequence.add(symbol3);
		//test method getFirstSymbolAtTime(long)
		ChordSymbol s1 = sequence.getFirstSymbolAtTime(50);	
		assertEquals(sequence.getFirstSymbolAtTime(50), symbol1);
		ChordSymbol s2 = sequence.getFirstSymbolAtTime(100);
		assertEquals(sequence.getFirstSymbolAtTime(100), symbol1);
		assertEquals(sequence.getFirstSymbolAtTime(150), symbol2);
		assertEquals(sequence.getFirstSymbolAtTime(250), symbol3);
		assertEquals(sequence.getFirstSymbolAtTime(300), symbol3);
	}



	/**
	 * Tests method <code>getLastSymbolBeforeTime(Rational)</code>.
	 */
	public static void testGetLastBeforeTimeMetric() {
		//create test sequence
		ChordSymbolSequence sequence = new ChordSymbolSequence();
		ChordSymbol symbol0 = new ChordSymbol(new Rational(0, 4), 0);
		sequence.add(symbol0);
		ChordSymbol symbol1 = new ChordSymbol(new Rational(1, 4), 100);
		sequence.add(symbol1);
		ChordSymbol symbol2 = new ChordSymbol(new Rational(2, 4), 200);
		sequence.add(symbol2);
		ChordSymbol symbol3 = new ChordSymbol(new Rational(3, 4), 300);
		sequence.add(symbol3);
		//test method getLastSymbolBeforeTime(Rational)	
		//ChordSymbol s1 = sequence.getLastSymbolBeforeTime(new Rational(1,8));
		assertEquals(sequence.getLastSymbolBeforeTime(new Rational(1,8)), symbol0);
		//ChordSymbol s2 = sequence.getLastSymbolBeforeTime(new Rational(1,4));
		assertEquals(sequence.getLastSymbolBeforeTime(new Rational(1,4)), symbol0);
		//ChordSymbol s3 = sequence.getLastSymbolBeforeTime(new Rational(3,8));
		assertEquals(sequence.getLastSymbolBeforeTime(new Rational(3,8)), symbol1);
		//ChordSymbol s4 = sequence.getLastSymbolBeforeTime(new Rational(3,4));
		assertEquals(sequence.getLastSymbolBeforeTime(new Rational(3,4)), symbol2);
		//ChordSymbol s5 = sequence.getLastSymbolBeforeTime(new Rational(4,4));
		assertEquals(sequence.getLastSymbolBeforeTime(new Rational(4,4)), symbol3);
	}


	/**
	 * Tests method <code>getFirstSymbolAtTime(Rational)</code>.
	 */
	public static void testGetFirstAtTimeMetric() {
		//create test sequence
		ChordSymbolSequence sequence = new ChordSymbolSequence();
		ChordSymbol symbol0 = new ChordSymbol(new Rational(0, 4), 0);
		sequence.add(symbol0);
		ChordSymbol symbol1 = new ChordSymbol(new Rational(1, 4), 100);
		sequence.add(symbol1);
		ChordSymbol symbol2 = new ChordSymbol(new Rational(2, 4), 200);
		sequence.add(symbol2);
		ChordSymbol symbol3 = new ChordSymbol(new Rational(3, 4), 300);
		sequence.add(symbol3);
		//test method getFirstSymbolAtTime(Rational)	
		assertEquals(sequence.getFirstSymbolAtTime(new Rational(0,4)), symbol0);
		assertEquals(sequence.getFirstSymbolAtTime(new Rational(1,8)), symbol1);
		assertEquals(sequence.getFirstSymbolAtTime(new Rational(1,4)), symbol1);
		assertEquals(sequence.getFirstSymbolAtTime(new Rational(3,4)), symbol3);
	}


}
