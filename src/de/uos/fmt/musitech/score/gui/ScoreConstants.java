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

import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Score Constants (Metric Time and Pitches).
 * @author Jens Wissmann and Tillman Weyde
 */
public interface ScoreConstants {

	/**
	 * Duration Constants
	 */
	final Rational BREVE    = new Rational(2, 1);
	final Rational DOUBLE = new Rational(2, 1);

	final Rational SEMIBREVE = new Rational(1, 1);
	final Rational WHOLE = new Rational(1, 1);

	final Rational MINIM     = new Rational(1, 2);
	final Rational HALF  = new Rational(1, 2);

	final Rational CROTCHET = new Rational(1, 4);
	final Rational QUARTER  = new Rational(1, 4);

	final Rational QUAVER    = new Rational(1, 8);
	final Rational EIGHTH    = new Rational(1, 8);

	final Rational SEMIQUAVER = new Rational(1, 16);
	final Rational SIXTEENTH  = new Rational(1, 16);

	final Rational DEMISEMIQUAVER = new Rational(1, 32);
	final Rational THIRTYSECOND   = new Rational(1, 32);

	/**
	 * Pitch Constants
	 */

	final ScorePitch C0 = new ScorePitch('c',-3,0);
	final ScorePitch D0 = new ScorePitch('d',-3,0);
	final ScorePitch E0 = new ScorePitch('e',-3,0);
	final ScorePitch F0 = new ScorePitch('f',-3,0);
	final ScorePitch G0 = new ScorePitch('g',-3,0);
	final ScorePitch A0 = new ScorePitch('a',-3,0);
	final ScorePitch B0 = new ScorePitch('b',-3,0);

	final ScorePitch C1 = new ScorePitch('c',-2,0);
	final ScorePitch D1 = new ScorePitch('d',-2,0);
	final ScorePitch E1 = new ScorePitch('e',-2,0);
	final ScorePitch F1 = new ScorePitch('f',-2,0);
	final ScorePitch G1 = new ScorePitch('g',-2,0);
	final ScorePitch A1 = new ScorePitch('a',-2,0);
	final ScorePitch B1 = new ScorePitch('b',-2,0);

	final ScorePitch C2 = new ScorePitch('c',-1,0);
	final ScorePitch D2 = new ScorePitch('d',-1,0);
	final ScorePitch E2 = new ScorePitch('e',-1,0);
	final ScorePitch F2 = new ScorePitch('f',-1,0);
	final ScorePitch G2 = new ScorePitch('g',-1,0);
	final ScorePitch A2 = new ScorePitch('a',-1,0);
	final ScorePitch B2 = new ScorePitch('b',-1,0);

	final ScorePitch C3 = new ScorePitch('c',0,0);
	final ScorePitch D3 = new ScorePitch('d',0,0);
	final ScorePitch E3 = new ScorePitch('e',0,0);
	final ScorePitch F3 = new ScorePitch('f',0,0);
	final ScorePitch G3 = new ScorePitch('g',0,0);
	final ScorePitch A3 = new ScorePitch('a',0,0);
	final ScorePitch B3 = new ScorePitch('b',0,0);

	final ScorePitch C4 = new ScorePitch('c',1,0);
	final ScorePitch D4 = new ScorePitch('d',1,0);
	final ScorePitch E4 = new ScorePitch('e',1,0);
	final ScorePitch F4 = new ScorePitch('f',1,0);
	final ScorePitch G4 = new ScorePitch('g',1,0);
	final ScorePitch A4 = new ScorePitch('a',1,0);
	final ScorePitch B4 = new ScorePitch('b',1,0);

	final ScorePitch C5 = new ScorePitch('c',2,0);
	final ScorePitch D5 = new ScorePitch('d',2,0);
	final ScorePitch E5 = new ScorePitch('e',2,0);
	final ScorePitch F5 = new ScorePitch('f',2,0);
	final ScorePitch G5 = new ScorePitch('g',2,0);
	final ScorePitch A5 = new ScorePitch('a',2,0);
	final ScorePitch B5 = new ScorePitch('b',2,0);

	final ScorePitch C6 = new ScorePitch('c',3,0);
	final ScorePitch D6 = new ScorePitch('d',3,0);
	final ScorePitch E6 = new ScorePitch('e',3,0);
	final ScorePitch F6 = new ScorePitch('f',3,0);
	final ScorePitch G6 = new ScorePitch('g',3,0);
	final ScorePitch A6 = new ScorePitch('a',3,0);
	final ScorePitch B6 = new ScorePitch('b',3,0);

}
