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
package de.uos.fmt.musitech.score.util;

import de.uos.fmt.musitech.utility.DebugState;

/** This class represents a symmetric Bézier curve.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Bow extends Bezier {
	/** Constructs a symmetric Bézier curve with given start and end point, height
	 * and gradient angle of the starting curve tangent. 
	 * @param p1     start point
	 * @param p4     end point
	 * @param height curve height (distance between (p1+p4)/2 and pointAt(1/2));
	 *               the height may be negative to change the bow's direction 
	 * @param theta  gradient angle (in radians) of curve tangent at p1; as a result the 
	 *               angle of curve tangent at p4 is -theta. */
	public Bow(Pair p1, Pair p4, double height, Angle theta) {
		if (theta.radians() == 0.0 || Math.abs(theta.radians()) >= Math.PI)
			throw new IllegalArgumentException("Bow: theta mod PI must not be zero, was: "+theta.radians());
		if (DebugState.DEBUG && height == 0.0)
			throw new IllegalArgumentException("Bow: height must not be zero");

		Pair heightUnitVec = p4.sub(p1).ortho().unitVector();
		Pair w = heightUnitVec.mul(4 * height / 3);
		Pair v = w.ortho().unitVector().mul(w.length() * theta.cot());
		Pair[] points = { p1, p1.add(w).sub(v), p4.add(w).add(v), p4 };
		setPoints(points);
	}

	/** Returns the height of this bow. */
	public double getHeight() {
		Pair[] p = getPoints();
		return 0.75 * Math.abs(p[1].getY() - p[0].getY());
	}

	/** Returns the tangent angle of this bow, i.e. the angle between line [p0,p3]
	 *  and line [p0,p1]. */
	public double getTangentAngle() {
		Pair[] p = getPoints();
		double h = getHeight();
		Pair tv = p[0].sub(p[1]);
		Pair hv = p[3].sub(p[0]).ortho().unitVector().mul(h * 4 / 3);
		Pair proj = tv.sub(hv);
		return Math.atan(h / tv.length());
	}

	/** Changes the height of this bow. */
	public void setHeight(double h) {
		Pair[] p = getPoints();
		double diff = (h - getHeight()) * 4 / 3;
		Pair diffVec = p[3].sub(p[0]).ortho().unitVector().mul(diff);
		p[1].inc(diffVec);
		p[2].inc(diffVec);
	}

	/** Adds a given value to the current height. */
	public void addToHeight(double dh) {
		Pair[] p = getPoints();
		dh *= 4. / 3;
		Pair diffVec = p[3].sub(p[0]).ortho().unitVector().mul(dh);
		p[1].inc(diffVec);
		p[2].inc(diffVec);
	}
}
