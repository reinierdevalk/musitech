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
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uos.fmt.musitech.mpeg.testcases;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.CharSymbol;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.StackSymbol;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * MPEG SMR Test Case 1.1 C 
 * @author collin
 */
public class TestCase1_1_c extends TestCase {
	
	NotationVoice voice(NotationSystem system, NotationStaff staff) {
		NoteList notes = new NoteList(context);
		NotationVoice voice = new NotationVoice();

		notes.addnext(e(1).flat(), new Rational(3, 8));
		notes.addnext(c(1), new Rational(1, 8));
		notes.addnext(e(1).flat(), r8());
		notes.addnext(c(1), r8());
		notes.addnext(e(1).flat(), r8());
		notes.addnext(c(1), r8());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(b(0).flat(), r8());
		notes.addnext(d(1), r8());
		notes.addnext(f(1), r2());
		notes.addnext(d(1), r8());
		notes.addnext(c(1), r16());
		notes.addnext(b(0).flat(), r16());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(c(1), r1());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(b(0).flat(), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(r(), r1());
		system.addBarline(new Barline(notes.getMetricDuration()));

		voice.addNotes(notes);

		Rational onset = notes.getMetricDuration();
		
		Note[] tuplet1 = new Note[]{
			new Note(new ScoreNote(b(0).flat(), onset, r8()), null),
			new Note(new ScoreNote(a(0), onset, r8()), null),
			new Note(new ScoreNote(g(0), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());

		tuplet1 = new Note[]{
			new Note(new ScoreNote(f(0), onset, r8()), null),
			new Note(new ScoreNote(g(0), onset, r8()), null),
			new Note(new ScoreNote(a(0), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());

		tuplet1 = new Note[]{
			new Note(new ScoreNote(b(0).flat(), onset, r8()), null),
			new Note(new ScoreNote(c(1), onset, r8()), null),
			new Note(new ScoreNote(d(1), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());

		tuplet1 = new Note[]{
			new Note(new ScoreNote(e(1).flat(), onset, r8()), null),
			new Note(new ScoreNote(d(1), onset, r8()), null),
			new Note(new ScoreNote(b(0).flat(), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());
		
		system.addBarline(new Barline(onset));
		
		tuplet1 = new Note[]{
			new Note(new ScoreNote(b(0).flat(), onset, r8()), null),
			new Note(new ScoreNote(a(0), onset, r8()), null),
			new Note(new ScoreNote(g(0), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());
		
		tuplet1 = new Note[]{
			new Note(new ScoreNote(f(0), onset, r8()), null),
			new Note(new ScoreNote(g(0), onset, r8()), null),
			new Note(new ScoreNote(a(0), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());
		
		tuplet1 = new Note[]{
			new Note(new ScoreNote(b(0).flat(), onset, r8()), null),
			new Note(new ScoreNote(c(1), onset, r8()), null),
			new Note(new ScoreNote(d(1), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());

		tuplet1 = new Note[]{
			new Note(new ScoreNote(e(1).flat(), onset, r8()), null),
			new Note(new ScoreNote(d(1), onset, r8()), null),
			new Note(new ScoreNote(c(1), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());

		system.addBarline(new Barline(onset));
		System.err.println(onset);
		
		NotationChord nchord = new NotationChord(context);
		nchord.add(new Note(new ScoreNote(b(0).flat(), onset, r4()), null));
		voice.add(nchord);
		onset = onset.add(r4());
		
		tuplet1 = new Note[]{
			new Note(new ScoreNote(r(), onset, r8()), null),
			new Note(new ScoreNote(f(1), onset, r8()), null),
			new Note(new ScoreNote(d(1), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());

		NotationChord nchord2 = new NotationChord(context);
		nchord2.add(new Note(new ScoreNote(b(0).flat(), onset, r4()), null));
		voice.add(nchord2);
		onset = onset.add(r4());

		tuplet1 = new Note[]{
			new Note(new ScoreNote(r(), onset, r8()), null),
			new Note(new ScoreNote(d(1), onset, r8()), null),
			new Note(new ScoreNote(b(0).flat(), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());
		
		system.addBarline(new Barline(onset));

		NotationChord nchord3 = new NotationChord(context);
		nchord3.add(new Note(new ScoreNote(f(0), onset, r4()), null));
		voice.add(nchord3);
		onset = onset.add(r4());

		tuplet1 = new Note[]{
			new Note(new ScoreNote(r(), onset, r8()), null),
			new Note(new ScoreNote(b(0).flat(), onset, r8()), null),
			new Note(new ScoreNote(f(0), onset, r8()), null)
		};
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());

		tuplet1 = new Note[]{
			new Note(new ScoreNote(d(0), onset, r8()), null),
			new Note(new ScoreNote(f(0), onset, r8()), null),
			new Note(new ScoreNote(d(0), onset, r8()), null)
		};
		tuplet1[2].getScoreNote().addAccent(Accent.STACCATO);
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());

		tuplet1 = new Note[]{
			new Note(new ScoreNote(b(-1).flat(), onset, r8()), null),
			new Note(new ScoreNote(f(0), onset, r8()), null),
			new Note(new ScoreNote(b(-1).flat(), onset, r8()), null)
		};
		tuplet1[2].getScoreNote().addAccent(Accent.STACCATO);
		voice.addTuplet(tuplet1, r4());
		onset = onset.add(r4());
		
		system.addBarline(new Barline(onset));
		
		NotationChord nchord4 = new NotationChord(context);
		nchord4.add(new Note(new ScoreNote(g(-1), onset, r4()), null));
		voice.add(nchord4);
		onset = onset.add(r4());

		onset = addnext(voice, onset, r(), r8());
		onset = addnext(voice, onset, f(0), r8());
		onset = addnext(voice, onset, g(0), r8());
		onset = addnext(voice, onset, a(0), r8());
		onset = addnext(voice, onset, b(0).flat(), r8());
		onset = addnext(voice, onset, c(1), r8());
		
		system.addBarline(new Barline(onset));
		
		onset = addnext(voice, onset, c(1), r1());
		
		system.addBarline(new Barline(onset));
		System.err.println(onset);
		
		onset = addnext(voice, onset, b(0).flat(), r4());
		onset = addnext(voice, onset, r(), r4());
		onset = addnext(voice, onset, r(), r2());
		
		system.addBarline(new Barline(onset));
		
		onset = addnext(voice, onset, r(), new Rational(3, 1));
		
		system.addBarline(new Barline(onset));
		
		onset = addnext(voice, onset, b(0).flat(), new Rational(3,8));
		onset = addnext(voice, onset, c(1), new Rational(1,8));
		onset = addnext(voice, onset, c(1), new Rational(3,8));
		onset = addnext(voice, onset, b(0).flat(), r16());
		onset = addnext(voice, onset, c(1), r16());

		system.addBarline(new Barline(onset));

		onset = addnext(voice, onset, b(0).flat(), r4());
		onset = addnext(voice, onset, r(), r4());
		onset = addnext(voice, onset, r(), r2());
		
		system.addBarline(new Barline(onset));
		
		onset = addnext(voice, onset, r(), new Rational(8,1));
		
		system.addBarline(new Barline(onset));
		
		onset = addnext(voice, onset, r(), r4());
		
		onset = addnext(voice, onset, b(0).flat(), new Rational(3, 16));
		onset = addnext(voice, onset, b(0).flat(), new Rational(1, 16));
		onset = addnext(voice, onset, b(0).flat(), r4());
		onset = addnext(voice, onset, b(0).flat(), r4());

		system.addBarline(new Barline(onset));

		onset = addnext(voice, onset, b(0).flat(), r4());
		onset = addnext(voice, onset, b(-1).flat(), new Rational(3, 16));
		onset = addnext(voice, onset, b(-1).flat(), new Rational(1, 16));
		onset = addnext(voice, onset, b(-1).flat(), r4());
		onset = addnext(voice, onset, b(-1).flat(), r4());

		system.addBarline(new Barline(onset));

		onset = addnext(voice, onset, b(-1).flat(), r2());
		onset = addnext(voice, onset, r(), r2());
		
		system.addBarline(new Barline(onset, true));
		
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 0, 1)));		
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 2, 3)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 6, 8)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 9, 11)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 12, 13)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 43, 44)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 47, 48)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 51, 52)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 53, 54)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 56, 57)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 66, 67)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 71, 72)));
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 73, 75)));

		
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 2, 5)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 6, 7)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 9, 11)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 43, 44)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 47, 48)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 51, 52)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 62, 65)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 74, 75)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 81, 82)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 86, 87)));
		
		return voice;
	}
	
	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.mpeg.testcases.TestCase#createNotationSystem()
	 */
	@Override
	public NotationSystem createNotationSystem() {
		context.getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3, 4, Rational.ZERO));
		KeyMarker marker = new KeyMarker(Rational.ZERO, 0);
		marker.setAccidentalNum(-2);
		context.getPiece().getMetricalTimeLine().add(marker);
		NotationSystem system = new NotationSystem(context);

		NotationStaff staff = new NotationStaff();
		staff.setClefType('g');
		staff.add(voice(system, staff));

		system.add(staff);

		system.addLinebreak(new Rational(48, 8));
		system.addLinebreak(new Rational(88, 8));

		int i = staff.get(0).find(new Rational(15, 1));
		
		MetricAttachable ma = new MetricAttachable(staff.get(0).get(i), new CharSymbol((char)117));
		ma.setRelativePosition(MetricAttachable.NORTH);
		ma.setAlignment(MetricAttachable.CENTER);
		ma.setDistance(0);
		staff.addAttachable(ma);

		i = staff.get(0).find(new Rational(31, 2));
		StackSymbol sa = new StackSymbol();
		sa.push(new CharSymbol((char)(116)));
		sa.push(new CharSymbol((char)(117)));
		
		MetricAttachable ma2 = new MetricAttachable(staff.get(0).get(i), sa);
		ma2.setRelativePosition(MetricAttachable.NORTH);
		ma2.setAlignment(MetricAttachable.CENTER);
		ma2.setDistance(0);
		staff.addAttachable(ma2);
	
		RenderingHints rh = new RenderingHints();
		rh.registerHint("barline", "none");
		system.setRenderingHints(rh);
		
		return system;
	}

	public static void main(String argv[]) {
		//(new TestCase1_1_c("/tmp/testcase1.xml")).setVisible(true);
		(new TestCase1_1_c()).setVisible(true);
	}

	
}
