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
package de.uos.fmt.musitech.utility.math;

import junit.framework.TestCase;

/**
 * This class represents a rational number (fraction) consisting of
 * a numerator and a denominator. The fraction is stored in the most 
 * reduced form. All oprations return reduced numerators and reduce 
 * the Rationals before they are used, but getters and setters do 
 * no not perform reduction.
 * <BR><BR>
 * Example: 8/6 = 1 + 1/3 will be written as <BR> <code>Rational(8,6).equals(Rational(1,3).add(new Rational(1)))</code>
 * and returns <code>true<code>.
 * But <code>r.setNumer(8); r.setDenom(6);</code> will not produce a 
 * reduced fraction automatically.
 * Rational objects are not thread-safe.
 * @author Tillman Weyde    
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class RationalTest extends TestCase {


	public RationalTest() {
	}

	/** 
	 * Tests the reduction and sign normalization in the Rational constructor.  
	 */
	public void testRational() {
		Rational r = new Rational(6,-8);
		assertEquals(r.getNumer(),-3);
		assertEquals(r.getDenom(),4);
	}

	
	/**
	 * Tests for handling of Rational additions.
	 */
	public void testRationalAdd() {
		Rational r1 = new Rational(6,8);
		Rational r2 = new Rational(8,6);
		Rational sum = r1.add(r2);
		assertEquals(25,sum.getNumer());
		assertEquals(12,sum.getDenom());
	}
	
	/**
	 * Tests for handling of Rational multiplications.
	 */
	public void testRationalMul() {
		Rational r1 = new Rational(6,8);
		Rational r2 = new Rational(8,6);
		Rational prod = r1.mul(r2);
		assertEquals(1,prod.getNumer());
		assertEquals(1,prod.getDenom());
	}
	
	/**
	 * Tests for handling of Rational subtractions.
	 */
	public void testRationalSub() {
		Rational r1 = new Rational(6,8);
		Rational r2 = new Rational(8,6);
		Rational diff = r1.sub(r2);
		assertEquals(-7,diff.getNumer());
		assertEquals(12,diff.getDenom());
	}
	
	/**
	 * Tests for handling of Rational divisions.
	 */
	public void testRationalDiv() {
		Rational r1 = new Rational(6,8);
		Rational r2 = new Rational(8,6);
		Rational diff = r1.div(r2);
		assertEquals(9,diff.getNumer());
		assertEquals(16,diff.getDenom());
	}
	
	/**
	 * tests for handling of integer overflows.
	 */
	public void testOverflow() {
		// test without reduction in constructor
		Rational r1 = new Rational();
		r1.setNumer(Integer.MIN_VALUE);
		r1.setDenom(Integer.MIN_VALUE);
		Rational prod = r1.mul(r1);
		assertEquals(prod.getNumer(),1);
		assertEquals(prod.getDenom(),1);
		Rational sum = r1.add(r1);
		assertEquals(sum.getNumer(),2);
		assertEquals(sum.getDenom(),1);
		// test without reduction in constructor
		Rational r2 = new Rational();
		r1.setNumer(1845736254);
		r1.setDenom(Integer.MIN_VALUE);
		prod = r1.mul(r1);
		double prod_val = prod.toDouble();
		assertTrue(prod_val > 0.73871948);
		assertTrue(prod_val < 0.73871949);
		sum = r1.add(r1);
		double sum_val = sum.toDouble();
		assertTrue(sum_val < -1.71897584);
		assertTrue(sum_val > -1.71897585);
	}

}
