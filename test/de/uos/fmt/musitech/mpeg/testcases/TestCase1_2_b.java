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
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.StackSymbol;
import de.uos.fmt.musitech.data.score.StringSymbol;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestCase1_2_b extends TestCase {
	
	NotationVoice voice1(NotationSystem system, NotationStaff staff) {
		NoteList notes = new NoteList(context);
		NotationVoice voice = new NotationVoice();
		Rational onset = Rational.ZERO;
		
		onset = addnext(voice, onset, new ScorePitch[]{f(1), d(1)}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{g(1).flat(), e(1).flat()}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{f(1), d(1).flat()}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{b(0), d(1)}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{d(1).flat(), f(1)}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{c(1), e(1).flat()}, r16());
		system.addBarline(new Barline(onset));
		
		onset = addnext(voice, onset, new ScorePitch[]{d(1).flat(), b(0).flat()}, r4());
		onset = addnext(voice, onset, new ScorePitch[]{c(1), a(0)}, r8());
		system.addBarline(new Barline(onset));
		
		onset = addnext(voice, onset, new ScorePitch[]{b(-1), d(0)}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{c(0), e(0).flat()}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{b(-1).flat(), d(0).flat()}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{b(-1), g(-1).sharp()}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{b(-1).flat(), d(0).flat()}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{c(0), a(-1)}, r16());
		
		system.addBarline(new Barline(onset));
		
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 6, 7)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 0, 5)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 8, 13)));
		return voice;
	}
	
	NotationVoice voice2(NotationSystem system, NotationStaff staff) {
		NoteList notes = new NoteList(context);
		NotationVoice voice = new NotationVoice();
		Rational onset = new Rational(2, 8);
		
		onset = addnext(voice, onset, new ScorePitch[]{g(0).flat()}, r8());
		onset = addnext(voice, onset, new ScorePitch[]{g(0).flat(), e(0).flat()}, new Rational(3,16));
		((Note)((NotationChord)voice.get(voice.size() - 2)).get(0)).getScoreNote()
			.setTiedNote(((Note)((NotationChord)voice.get(voice.size() - 1)).get(0)).getScoreNote());
		onset = addnext(voice, onset, new ScorePitch[]{f(0), d(0)}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{e(0).flat(), g(0).flat()}, r16());
		onset = addnext(voice, onset, new ScorePitch[]{f(0), d(0).flat()}, r16());
		
		//voice.addSlurContainer(new SlurContainer(context, getScoreNotes(voice, 73, 75)));
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 1, 4)));
		
		return voice;
	}

	
	
	
	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.mpeg.testcases.TestCase#createNotationSystem()
	 */
	public NotationSystem createNotationSystem() {
		KeyMarker marker = new KeyMarker(Rational.ZERO, 0);
		marker.setAccidentalNum(-6);
		context.getPiece().getMetricalTimeLine().add(marker);
		NotationSystem system = new NotationSystem(context);

		NotationStaff staff = new NotationStaff();
		staff.setClefType('g');
		staff.add(voice1(system, staff));
		staff.add(voice2(system, staff));
		
		RenderingHints rh = new RenderingHints();
		rh.registerHint("barline", "none");
		system.setRenderingHints(rh);

		
		system.add(staff);

		return system;
	}

	public static void main(String argv[]) {
		//(new TestCase1_1_c("/tmp/testcase1.xml")).setVisible(true);
		(new TestCase1_2_b()).setVisible(true);
	}

	
}
