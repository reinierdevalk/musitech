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
package de.uos.fmt.musitech.structure.harmony;

import java.util.Iterator;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.harmony.ChordMap;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * @author Christa Deiwiks and Klaus Dalinghaus
 *
 * The Pattern object is responsible for generating
 * a rhythm to accompanying with given chord notes
 * for a given time.
 */
public class Pattern {

	public static final int SIMPLE_FOURTH = 1;

	int pat_type;

	/**
	 * Method Pattern.
	 * @param pat_type
	 */
	public Pattern(int pat_type) {
		this.pat_type = pat_type;
	}

	static void simple_fourth(Container cont, ChordMap ch, Rational metricDuration, long begin, long duration) {

		Rational quarter = new Rational(1, 4);
		long length = (long) ((duration / metricDuration.toDouble()) * quarter.toDouble());
		
		for (long i = begin; i < begin + duration; i+=length) {
			PerformanceNote perNote = new PerformanceNote(i);
			perNote.setDuration((long) (length * 0.95));

			for (Iterator e = ch.values().iterator() ; e.hasNext() ;) {
				int pi = ((Integer) e.next()).intValue();
				perNote.setPitch(pi);
				Note note = new Note(ObjectCopy.copyObject(perNote));
				cont.add(note);
			}
		}
	}

	public void expand(Container cont, ChordMap ch, Rational metricDuration, long begin, long duration) {
		switch (pat_type) {
			case SIMPLE_FOURTH :
				simple_fourth(cont, ch, metricDuration, begin, duration);
			default :
				simple_fourth(cont, ch, metricDuration, begin, duration);
		}
	}

	/**
	 * Method expand. Use the Voicing object v to expand this chord as
	 * notes into the container cont.
	 * 
	 * @param cont
	 * @param metricDuration 
	 * @param begin 
	 * @param duration 
	 * @param chordSymbol TODO
	 * @param v
	 */
	public void expandChordSym(Container<Note> cont, Rational metricDuration, long begin, long duration, ChordSymbol chordSymbol, Voicing v) {
	
		ChordMap chordMap;		
				
		// int mode = getMode();
	
		// if the root is 'X' then nothing has to be done.
		if (chordSymbol.getRoot() == 'X')
			return;
	
		chordMap = v.createChord(chordSymbol);
		expand(cont, chordMap, metricDuration, begin, duration);
		
		/*if (typ.equals("x")) {}
		else if (typ.equals("xmaj")) {
			chordPitches[3] = root + 11;
		}
		else if (typ .equals( "xmaj9")) {
			chordPitches[3] = root + 11;
			chordPitches[4] = root + 14;
		}
		else if (typ.equals("xmaj#11")) {
			chordPitches[3] = root + 11;
			chordPitches[5] = root + 18;
		}
		else if (typ.equals("xmaj9#11")) {
			chordPitches[3] = root + 11;
			chordPitches[4] = root + 14;
			chordPitches[5] = root + 18;
		}
		else if (typ.equals("xmajb5")) {
			chordPitches[2] = root + 6;
			chordPitches[3] = root + 11;
		}
		else if (typ.equals("xmaj9#11")) {
			chordPitches[2] = root + 6;
			chordPitches[3] = root + 11;
			chordPitches[4] = root + 14;
		}
		else if (typ.equals("x6")) {
			chordPitches[3] = root + 9;
		}
		else if (typ.equals("x69")) {
			chordPitches[3] = root + 9;
			chordPitches[4] = root + 14;
		}
		else if (typ.equals("xmaj69#11")) {
			chordPitches[3] =root+ 9;
			chordPitches[4] = root + 14;
			chordPitches[5] = root + 18;
		}
		else if (typ.equals("xmaj13#11")) {
			chordPitches[3] = root + 11;
			chordPitches[4] = root + 14;
			chordPitches[5] = root + 18;
			chordPitches[6] = root + 21;
		}
		else if (typ.equals("x7")) {				// dominants
			chordPitches[3] = root + 10;
		}
		else if (typ.equals("x9")) {
			chordPitches[3] = root + 10;
			chordPitches[4] = root + 14;
		}
		else if (typ.equals("xb9")) {
			chordPitches[3] = root + 10;
			chordPitches[4] = root + 13;
		}
		else if (typ.equals("xm")) {				// minor chords
			chordPitches[1] = root + 3;
		}
		else if (typ.equals("xm6")) {
			chordPitches[1] = root + 3;
			chordPitches[3] = root + 9;
		}
		else if (typ.equals("xm7")) {
			chordPitches[1] = root + 3;
			chordPitches[3] = root + 10;
		}
		else if (typ.equals("xm79")) {
			chordPitches[1] = root + 3;
			chordPitches[3] = root + 10;
			chordPitches[4] = root + 14;
		}
		else if (typ.equals("xm7b5")) {
			chordPitches[1] = root + 3;
			chordPitches[2] = root + 6;
			chordPitches[3] = root + 10;
		}
		else if (typ.equals("xmmaj")) {
			chordPitches[1] = root + 3;
			chordPitches[3] = root + 11;
		}
		
		else if (typ.equals("xmdim7")) {
			chordPitches[1] = root + 3;
			chordPitches[2] = root + 6;
			chordPitches[3] = root + 9;
		}
		
		else if (typ.equals("xsus4")) {				// sus chords
			chordPitches[1] = root + 5;
		}
		else if (typ.equals("xsus47")) {
			chordPitches[1] = root + 5;
			chordPitches[3] = root + 10;
		}
		}	
		
		*/
	
		//Note note0 = new Note(perNote0);
		//Note note1 = new Note(perNote1);
		//Note note2 = new Note(perNote2);
		//cont.add(note0);
		//cont.add(note1);		
		//cont.add(note2);
	}

}
