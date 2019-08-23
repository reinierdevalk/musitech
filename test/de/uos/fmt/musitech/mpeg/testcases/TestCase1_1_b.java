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
 */
package de.uos.fmt.musitech.mpeg.testcases;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.CharSymbol;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationStaffConnector;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestCase1_1_b extends TestCase {

	NotationVoice chorusVoice1(NotationSystem system, NotationStaff staff) {
		NotationVoice voice = new NotationVoice();
		NoteList notes = new NoteList(context);
		
		notes.addnext(r(), r4());
		notes.addnext(b(0).flat(), r4());
		notes.addnext(c(1), r4());
		notes.addnext(d(1), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(d(1), r4());
		notes.addnext(c(1), r4());
		notes.addnext(b(0).flat(), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(a(0), r2());
		notes.addnext(a(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(d(1), new Rational(3, 8));
		notes.addnext(d(1), new Rational(1, 8));
		notes.addnext(c(1), r4());
		notes.addnext(c(1), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(f(1), new Rational(3, 8));
		notes.addnext(f(1), new Rational(1, 8));
		notes.addnext(d(1), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(r(), r4());
		notes.addnext(c(1), r4());
		notes.addnext(f(0), r4());
		notes.addnext(g(0), r4());
		
		voice.addNotes(notes);
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 3, 4)));		
		
		return voice;
	}

	NotationVoice chorusVoice2(NotationSystem system, NotationStaff staff) {
		NotationVoice voice = new NotationVoice();
		NoteList notes = new NoteList(context);

		notes.addnext(f(0), r8());
		notes.addnext(e(0), r16());
		notes.addnext(f(0), r16());
		notes.addnext(g(0), r2());
		notes.addnext(f(0), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(e(0), r2());
		notes.addnext(d(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(d(0), r2());
		notes.addnext(r(), r4());
		notes.addnext(f(0), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(b(0).flat(), new Rational(3, 8));
		notes.addnext(b(0).flat(), new Rational(1, 8));
		notes.addnext(a(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(r(), r4());
		notes.addnext(a(0), r4());
		notes.addnext(b(0).flat(), new Rational(3, 8));
		notes.addnext(b(0).flat(), new Rational(1, 8));
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(a(0), r4());
		notes.addnext(a(0), r4());
		notes.addnext(d(1), new Rational(3, 8));
		notes.addnext(d(1), new Rational(1, 8));
		
		voice.addNotes(notes);
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 6, 7)));		
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 0, 2)));
		
		return voice;
	}

	NotationVoice chorusVoice3(NotationSystem system, NotationStaff staff) {
		NotationVoice voice = new NotationVoice();
		NoteList notes = new NoteList(context);

		notes.addnext(b(-1).flat(), r2());
		notes.addnext(a(-1), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(a(-1), r2());
		notes.addnext(r(), r4());
		notes.addnext(b(-1).flat(), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(f(0), new Rational(3, 8));
		notes.addnext(f(0), new Rational(1, 8));
		notes.addnext(d(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(r(), r4());
		notes.addnext(b(-1).flat(), r4());
		notes.addnext(f(0), new Rational(3, 8));
		notes.addnext(f(0), new Rational(1, 8));
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(d(0), r2());
		notes.addnext(g(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(f(0), r4());
		notes.addnext(f(-1), r4());
		notes.addnext(b(-1).flat(), new Rational(3, 8));
		notes.addnext(b(-1).flat(), new Rational(1, 8));
		
		voice.addNotes(notes);
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 1, 2)));		
		
		return voice;
	}

	NotationVoice pianoVoice1(NotationSystem system, NotationStaff staff) {
		NotationVoice voice = new NotationVoice();
		NoteList notes = new NoteList(context);

		notes.addnext(r(), r4());
		notes.addnext(b(0).flat(), r4());
		notes.addnext(c(1), r4());
		notes.addnext(d(1), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(d(1), r4());
		notes.addnext(c(1), r4());
		notes.addnext(b(0).flat(), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(a(0), r2());
		notes.addnext(a(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(d(1), new Rational(3, 8));
		notes.addnext(d(1), new Rational(1, 8));
		notes.addnext(c(1), r4());
		notes.addnext(c(1), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(f(1), new Rational(3, 8));
		notes.addnext(f(1), new Rational(1, 8));
		notes.addnext(d(1), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(r(), r4());
		notes.addnext(c(1), r4());
		notes.addnext(f(0), r4());
		notes.addnext(g(0), r4());
		
		
		voice.addNotes(notes);
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 3, 4)));		
		
		return voice;
	}

	NotationVoice pianoVoice2(NotationSystem system, NotationStaff staff) {
		NotationVoice voice = new NotationVoice();
		NoteList notes = new NoteList(context);

		notes.addnext(f(0), r8());
		notes.addnext(e(0), r16());
		notes.addnext(f(0), r16());
		notes.addnext(g(0), r2());
		notes.addnext(f(0), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));

		notes.addnext(e(0), r2());
		notes.addnext(d(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(d(0), r2());
		notes.addnext(r(), r4());
		notes.addnext(f(0), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(b(0).flat(), new Rational(3, 8));
		notes.addnext(b(0).flat(), new Rational(1, 8));
		notes.addnext(a(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(r(), r4());
		notes.addnext(a(0), r4());
		notes.addnext(b(0).flat(), new Rational(3, 8));
		notes.addnext(b(0).flat(), new Rational(1, 8));
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(a(0), r4());
		notes.addnext(a(0), r4());
		notes.addnext(d(1), new Rational(3, 8));
		notes.addnext(d(1), new Rational(1, 8));
		
		voice.addNotes(notes);
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 6, 7)));		
		voice.addBeamContainer(new BeamContainer(context, getNotationChords(voice, 0, 2)));
		
		return voice;
	}

	NotationVoice pianoVoice3(NotationSystem system, NotationStaff staff) {
		NotationVoice voice = new NotationVoice();
		NoteList notes = new NoteList(context);

		notes.addnext(b(-1).flat(), r2());
		notes.addnext(a(-1), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(a(-1), r2());
		notes.addnext(r(), r4());
		notes.addnext(b(-1).flat(), r4());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(f(0), new Rational(3, 8));
		notes.addnext(d(0), new Rational(1, 8));
		notes.addnext(d(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(r(), r4());
		notes.addnext(b(-1).flat(), r4());
		notes.addnext(f(0), new Rational(3, 8));
		notes.addnext(f(0), new Rational(1, 8));
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(d(0), r2());
		notes.addnext(g(0), r2());
		system.addBarline(new Barline(notes.getMetricDuration()));
		
		notes.addnext(f(0), r4());
		notes.addnext(d(-1), r4());
		notes.addnext(b(-1).flat(), new Rational(3, 8));
		notes.addnext(b(-1).flat(), new Rational(1, 8));
		
		voice.addNotes(notes);
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 1, 2)));		
		
		return voice;
	}

	
	
	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.mpeg.testcases.TestCase#createNotationSystem()
	 */
	public NotationSystem createNotationSystem() {
		//context.getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3, 4, Rational.ZERO));
		KeyMarker marker = new KeyMarker(Rational.ZERO, 0);
		marker.setAccidentalNum(-1);
		context.getPiece().getMetricalTimeLine().add(marker);
		NotationSystem system = new NotationSystem(context);

		RenderingHints systemRH = new RenderingHints();
		systemRH.registerHint("interrupted barlines", new boolean[]{true, true, true, false, false});
		system.setRenderingHints(systemRH);
		
		NotationStaff chorusStaff1 = new NotationStaff();
		chorusStaff1.setClefType('g');
		chorusStaff1.add(chorusVoice1(system, chorusStaff1));

		CharSymbol f = new CharSymbol('f');
		f.setParanthesised(true);
		MetricAttachable forte = new MetricAttachable(new Rational(10,4), f);
		forte.setRelativePosition(MetricAttachable.NORTH);
		chorusStaff1.addAttachable(forte);
		
		NotationStaff chorusStaff2 = new NotationStaff();
		chorusStaff2.setClefType('g');
		chorusStaff2.add(chorusVoice2(system, chorusStaff2));

		CharSymbol f2 = new CharSymbol('f');
		f2.setParanthesised(true);
		MetricAttachable forte2 = new MetricAttachable(new Rational(11,4), f2);
		forte2.setRelativePosition(MetricAttachable.NORTH);
		chorusStaff2.addAttachable(forte2);
		
		NotationStaff chorusStaff3 = new NotationStaff();
		chorusStaff3.setClefType('g');
		chorusStaff3.add(chorusVoice3(system, chorusStaff3));
//		chorusStaff3.setTransposition(-12); TODO reparieren

		CharSymbol f3 = new CharSymbol('f');
		f3.setParanthesised(true);
		MetricAttachable forte3 = new MetricAttachable(new Rational(13,4), f3);
		forte3.setRelativePosition(MetricAttachable.NORTH);
		chorusStaff3.addAttachable(forte3);
		
		RenderingHints rh = new RenderingHints();
		rh.registerHint("scale", new Float(0.8));
		NotationStaff pianoStaff1 = new NotationStaff();
		pianoStaff1.setClefType('g');
		pianoStaff1.add(pianoVoice1(system, pianoStaff1));
		pianoStaff1.add(pianoVoice2(system, pianoStaff1));
		pianoStaff1.setRenderingHints(rh);
		
		NotationStaff pianoStaff2 = new NotationStaff();
		pianoStaff2.setClefType('f');
		pianoStaff2.add(pianoVoice3(system, pianoStaff2));
		pianoStaff2.setRenderingHints(rh);
		
		system.add(chorusStaff1);
		system.add(chorusStaff2);
		system.add(chorusStaff3);
		system.add(pianoStaff1);
		system.add(pianoStaff2);
		
		NotationStaffConnector con1 = new NotationStaffConnector(NotationStaffConnector.BRACKET);
		con1.add(chorusStaff1);
		con1.add(chorusStaff3);
		system.addStaffConnector(con1);

		NotationStaffConnector con2 = new NotationStaffConnector(NotationStaffConnector.BRACE);
		con2.add(pianoStaff1);
		con2.add(pianoStaff2);
		system.addStaffConnector(con2);
		
		RenderingHints rh2 = new RenderingHints();
		rh2.registerHint("barline", "none");
		system.setRenderingHints(rh2);

		
		return system;
	}
	
	public static void main(String argv[]) {
		//(new TestCase1_1_b("/tmp/testcase1.xml")).setVisible(true);
		(new TestCase1_1_b()).setVisible(true);
	}


}
