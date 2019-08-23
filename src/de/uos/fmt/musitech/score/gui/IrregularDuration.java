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

import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents an irregular divided event duration. An event with
 *  such a duration is part of a tuplet.
 *  @author Martin Gieseking
 *  @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class IrregularDuration extends Duration {
	private Tuplet tuplet; // reference to tuplet where this duration belongs to

	/** Constructs a new irregular duration.
	 * @param tuplet an irregular duration is always part of a tuplet; this parameter
	 *               specifies this tuplet
	 * @param base   notated duration */
	public IrregularDuration(Tuplet tuplet, Rational base) {
		super(base, 0);
		this.tuplet = tuplet;
	}

	public IrregularDuration(Tuplet tuplet, Rational base, int dots) {
		super(base, dots);
		this.tuplet = tuplet;
	}

	public IrregularDuration(Tuplet tuplet, Duration duration) {
		super(duration);
		this.tuplet = tuplet;
	}

	/** Returns the total duration (including augmentation dots). */
	public Rational toRational() {
		// vgl. Gieseking, S. 68
		int exp = (int) MyMath.log(2, tuplet.getTupletSize() / tuplet.getDuration().toDouble());
		if (MyMath.isPowerOf2(tuplet.getTupletSize()))
			exp++;
		Rational b = getBase().mul(1 << exp, 1).div(tuplet.getTupletSize(), 1);
		int pow = 1 << dots;
		return b.mul(2 * pow - 1, pow);
	}
}
