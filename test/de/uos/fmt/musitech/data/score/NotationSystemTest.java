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
package de.uos.fmt.musitech.data.score;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class includes a couple of test methods for the NotationSystem class.
 * It is written using the JUnit framework.
 * @author collin
 * @version 1.0
 * @see de.uos.fmt.musitech.data.score.NotationSystem
 */
public class NotationSystemTest extends TestCase {
    /**
     * This method test the method addLinebreak in NotationSystem.
     * @see de.uos.fmt.musitech.data.score.NotationSystem#addLinebreak(de.uos.fmt.musitech.utility.Rational)
     */
    public void testAddLinebreak() {
	Context context = new Context(new Piece());
	NotationSystem system = new NotationSystem(context);
	NotationStaff staff = new NotationStaff( system);
	NotationVoice voice = new NotationVoice( staff);
	voice.add(new Note(new ScoreNote(Rational.ZERO, new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
	voice.add(new Note(new ScoreNote(new Rational(1, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
	voice.add(new Note(new ScoreNote(new Rational(2, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
	voice.add(new Note(new ScoreNote(new Rational(3, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));
	
	voice.add(new Note(new ScoreNote(new Rational(4, 4), new Rational(1, 1), 'c', (byte) 0, (byte) 0), null));
	voice.add(new Note(new ScoreNote(new Rational(8, 4), new Rational(1, 1), 'c', (byte) 0, (byte) 0), null));
	voice.add(new Note(new ScoreNote(new Rational(12, 4), new Rational(1, 4), 'c', (byte) 0, (byte) 0), null));

	voice.getContext().getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3, 4, Rational.ZERO));
	
	system.insertBarlines();

	staff.add(voice);

	system.add(staff);

	//this should work (no exception)
	system.addLinebreak(new Rational(3, 4));

	try {
	    system.addLinebreak(new Rational(4,4));
	} catch (IllegalArgumentException e) {
	    return;
	}
	fail("An IllegalArgumentException should have been thrown");
    }
}
