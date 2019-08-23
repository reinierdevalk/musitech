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
//import de.uos.fmt.musitech.time.TimeKeeper;
//import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

public class RealToRational {

	static long defaultMaxDen = Integer.MAX_VALUE/1000;

	public static void main(String[] args) {
		double startx = 0;
		if (args.length > 0)
			startx = Double.parseDouble(args[1]);
		long maxden = Integer.MAX_VALUE/100;

//		startx = MyMath.root(2, 12);
		startx = Math.PI * Math.E;
//		long startTime = System.nanoTime();
		Rational r = doubleToRational(startx, maxden);
//		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("" + r.getNumer() + "/" + r.getDenom());

	}
	
	public static Rational doubleToRational(double startx) {
		return doubleToRational(startx, defaultMaxDen);
	}

	/*
	** find rational approximation to given real number
	** David Eppstein / UC Irvine / 8 Aug 1993
	** ported to Java by Tillman Weyde 2007
	**
	** usage: a.out r d
	**   r is real number to approximate
	**   d is the maximum denominator allowed
	**
	** based on the theory of continued fractions
	** if x = a1 + 1/(a2 + 1/(a3 + 1/(a4 + ...)))
	** then best approximation is found by truncating this series
	** (with some adjustments in the last term).
	**
	** Note the fraction can be recovered as the first column of the matrix
	**  ( a1 1 ) ( a2 1 ) ( a3 1 ) ...
	**  ( 1  0 ) ( 1  0 ) ( 1  0 )
	** Instead of keeping the sequence of continued fraction terms,
	** we just keep the last partial product of these matrices.
	*/
	public static Rational doubleToRational(double startx, long maxden) {
		double x = startx;
		long m[][] = new long[2][2];
		/* initialise matrix */
		m[0][0] = m[1][1] = 1;
		m[0][1] = m[1][0] = 0;

		long ai;
		int i = 0;
		/* loop finding terms until denom gets too big */
		while (m[1][0] * (ai = (long) x) + m[1][1] <= maxden) {
			long t;
			t = m[0][0] * ai + m[0][1]; // extend the numerator
			m[0][1] = m[0][0]; // 
			m[0][0] = t; // 
			t = m[1][0] * ai + m[1][1]; // extend the denominator
			m[1][1] = m[1][0]; // 
			m[1][0] = t; // 
			x = 1 / (x - (double) ai); // remaining number to be converted
			i++; // 
		}
		System.out.println(i + " iterations ");
		/* now remaining x is between 0 and 1/ai */
		/* approx as either 0 or 1/m where m is max that will fit in maxden */
		/* first try zero */
//		System.out.printf("%d/%d, error = %e\n", m[0][0], m[1][0],
//			startx - ((double) m[0][0] / (double) m[1][0]));
		
		long numer1 = m[0][0];
		long denom1 = m[1][0];
		double error1 = startx - ((double) numer1 / (double) denom1);

		/* now try other possibility */
		ai = (maxden - m[1][1]) / m[1][0];
		m[0][0] = m[0][0] * ai + m[0][1];
		m[1][0] = m[1][0] * ai + m[1][1];
//		System.out.printf("%d/%d, error = %e\n", m[0][0], m[1][0],
//			startx - ((double) m[0][0] / (double) m[1][0]));
		long numer2 = m[0][0];
		long denom2 = m[1][0];
		double error2 = startx - ((double) numer2 / (double) denom2);

		// we now choose the option with the smaller error
		Rational r;
		if(Math.abs(error1)<Math.abs(error2))
			r = new Rational(numer1, denom1);
		else
			r = new Rational(numer2, denom2);
		
		return r;
	}

}
