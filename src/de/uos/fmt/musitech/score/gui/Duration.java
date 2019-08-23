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
package de.uos.fmt.musitech.score.gui;

import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class represents a simple binary duration.
 * 
 * @author Martin Gieseking
 * @version $Revision: 7929 $, $Date: 2005-01-12 22:57:06 +0000 (Wed, 12 Jan
 *          2005) $
 */
public class Duration implements Comparable {

	Rational base; // base duration
	int dots; // number of dots
	static final int DOT_LIMIT = 2;

	/**
	 * Creates a Duration with given numerator and denominator.
	 * 
	 * @param num numerator
	 * @param den denominator
	 */
	public Duration(int num, int den) {
		if (num < 0 || den <= 0)
			throw new IllegalArgumentException("Duration(" + num + "," + den
												+ ")");
		base = new Rational(num, den);
		dots = 0;
	}

	/**
	 * Creates a duration with given numerator, denominator and number of dots.
	 * 
	 * @param num numerator
	 * @param den denominator
	 * @param dots number of dots
	 */
	public Duration(int num, int den, int dots) {
		if (num < 0 || den <= 0 || dots < 0)
			throw new IllegalArgumentException("Duration(" + num + "," + den
												+ "," + dots + ")");
		base = new Rational(num, den);
		this.dots = dots;
	}

	/**
	 * Creates a duration with given numerator, denominator and number of dots.
	 * 
	 * @param argBase base duration
	 * @param argDots number of dots
	 */
	public Duration(Rational argBase, int argDots) {
		if (!MyMath.isPowerOf2(argBase.getDenom())) {
			System.out.println("WARNING in Class Duration: base denominator is not a power of 2 " +
					": base: " + argBase + ", dots: " + argDots);
			argBase.setDenom(MyMath.floorPower2(argBase.getDenom()));
			System.out.println("WARNING changed base to be power of 2, " +
					"new base: " + argBase + ", dots: " + argDots);
			// throw new IllegalArgumentException("base isn't power of 2 (use
			// Irregular duration instead): base: "+base+", dots: "+dots);
		}
		if (argBase.sign() < 0 || argDots < 0)
			throw new IllegalArgumentException("Duration(" 
				+ argBase + ","	+ argDots + ")");
		this.base = argBase;
		this.dots = argDots;
	}

	/** Copy constructor (copies the values, not the Rational object). 
	 * @param argDuration object to copy from 
	 */
	public Duration(Duration argDuration) {
		base = argDuration.base;
		dots = argDuration.dots;
	}

	/** Returns this Duration as a new Rational object. */
	public Rational toRational() {
		Rational ret = new Rational(2);
		ret = ret.sub(1, 1 << dots);
		ret = ret.mul(base);
		return ret;
	}

	public Rational getBase() {
		return base;
	}

	public int getDots() {
		return dots;
	}

	/** Returns a string representation of this Duration. */
	public String toString() {
		String res = "Duration(" + base;
		if (dots > 0)
			res += ", " + dots + " dots";
		return res + ")";
	}

	/**
	 * Splits this Duration in smaller pieces (if necessary) so that every piece
	 * can be notated without ties.
	 */
	public Duration[] split() {
		List<Duration> durs = new ArrayList<Duration>();
		// remove preselected dots
		base = toRational();
		dots = 0;
		int numer = toRational().getNumer();
		int current = 0;
		int dots = 0;
		int diff = 0;
		while (numer > 0) {
			int ilog = MyMath.ilog2(numer);
			if (dots < DOT_LIMIT && diff == 1)
				dots++;
			else {
				if (current > 0)
					durs.add(new Duration(
						new Rational(current, base.getDenom()), dots));
				current = 1 << ilog;
				dots = 0;
			}
			numer -= 1 << ilog;
			if (numer > 0)
				diff = ilog - MyMath.ilog2(numer);
		}
		durs.add(new Duration(new Rational(current, base.getDenom()), dots));
		return durs.toArray(new Duration[durs.size()]);
	}

	/**
	 * Splits this Duration in smaller pieces (if necessary) so that every piece
	 * can be notated without ties. Additionally every piece won't be longer
	 * than the given max parameter.
	 */
	public Duration[] split(Rational max) {
		if (max == null)
			return split();
		Rational abs = toRational();
		int full = abs.div(max).floor();
		Rational remain = abs.mod(max);
		Duration[] res1 = remain.isGreater(0, 1) ? new Duration(remain, 0)
				.split() : new Duration[0];
		Duration[] res2 = new Duration[res1.length + full];
		Duration dmax = new Duration(max, 0);
		for (int i = 0; i < full; i++)
			res2[i] = dmax;
		for (int i = 0; i < res1.length; i++)
			res2[full + i] = res1[i];
		return res2;
	}

	/**
	 * Returns true if this Duration cannot be notated by a single (dotted) note
	 * or rest. That means in the case of notes we need ties. E.g. 5/16 = 1/4 +
	 * 1/16 is a compound duration.
	 */
	public boolean isCompoundDuration() {
		int dots = MyMath.bitRunLength(toRational().getNumer()) - 1;
		return dots < 0 || dots > DOT_LIMIT;
	}

	/** 
	 * This comparison is consistent
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		Rational r1 = toRational();
		Rational r2 = ((Duration) o).toRational();
		int comp = r1.compareTo(r2);
		if(comp != 0)
			return comp;
		else{
			Duration d2 = (Duration) o;
			return d2.base.compareTo(this.base);
		}
	}
	
	public boolean equals(Object o){
		if(o == null) 
			return false; 
		if(!(o instanceof Duration )) 
			return false; 
		return compareTo(o) == 0;
	}
	
	public int hashCode() {
		return base.mul(851348334, dots).ceil();
	}
	
}
