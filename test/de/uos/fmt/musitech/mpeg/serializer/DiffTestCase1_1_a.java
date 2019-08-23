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
 * Created on 30.12.2004
 */
package de.uos.fmt.musitech.mpeg.serializer;

import java.util.Iterator;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BarlineContainer;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.CharSymbol;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationStaffConnector;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.StringSymbol;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.ClefContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Administrator
 */
public class DiffTestCase1_1_a extends MPEGDiffViewer {

	void fluteVoice(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);
		BarlineContainer barlineContainer = new BarlineContainer();
		
		//Measure 1:
		notes.addnext(g(1), r8());
		notes.addnext(f(1).sharp(), r16());
		notes.addnext(g(1), r16());

		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		//Measure 2:
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(d(1), r8());
		notes.addnext(c(1).sharp(), r16());
		notes.addnext(d(1), r16());

		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		//Measure 3:
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r8());
		notes.addnext(a(0), r16());
		notes.addnext(g(0), r16());

		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		//Measure 4:
		notes.addnext(f(0).sharp(), r8(), Accent.STACCATO);
		notes.addnext(g(0), r8(), Accent.STACCATO);
		notes.addnext(g(0).sharp(), r8(), Accent.STACCATO);
		notes.addnext(a(0), r8(), Accent.STACCATO);
		notes.addnext(b(0).flat(), r8(), Accent.STACCATO);
		notes.addnext(b(0), r8(), Accent.STACCATO);
		
		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		
		//Measure 5:
		notes.addnext(c(1), r4());
		notes.addnext(r(), r4());
		notes.addnext(c(1), r8());
		notes.addnext(b(0), r16());
		notes.addnext(c(1), r16());
		
		cont.add(voice);
		cont.add(barlineContainer);
		
		
		//Measure 1:
		
		
		cont.add(new BeamContainer(context, getScoreNotes(notes, 0, 2)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 0, 2)));
		
		//Measure 2:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 5, 7)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 5, 7)));
		
		//Measure 3:

		cont.add(new BeamContainer(context, getScoreNotes(notes, 10, 12)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 10, 12)));
		
		//Measure 4:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 13, 18)));
		
		//Measure 5:

		cont.add(new BeamContainer(context, getScoreNotes(notes, 21, 23)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 21, 23)));

		ScoreNote n = new ScoreNote(a(1), Rational.ZERO, Rational.ZERO);
		RenderingHints rh = new RenderingHints();
		rh.registerHint("appoggiatura", new Rational(1, 16));
		notes.add(n).setRenderingHints(rh);

		ScoreNote n2 = new ScoreNote(e(1), new Rational(3, 4), Rational.ZERO);
		notes.add(n2).setRenderingHints(rh);

		ScoreNote n3 = new ScoreNote(c(1), new Rational(6, 4), Rational.ZERO);
		notes.add(n3).setRenderingHints(rh);

		ScoreNote n4 = new ScoreNote(d(1), new Rational(12, 4), Rational.ZERO);
		notes.add(n4).setRenderingHints(rh);
	}
	
	void oboeVoice1(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);
		BarlineContainer barlineContainer = new BarlineContainer();
		
		
		//Measure 1:
		notes.addnext(g(1), r8());
		notes.addnext(f(1).sharp(), r16());
		notes.addnext(g(1), r16());

		barlineContainer.add(new Barline(notes.getMetricDuration()));

		//Measure 2:
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(d(1), r8());
		notes.addnext(c(1).sharp(), r16());
		notes.addnext(d(1), r16());
	
		//Measure 3:
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r8());
		notes.addnext(a(0),r16());
		notes.addnext(g(0), r16());
		
		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		//Measure 4:
		notes.addnext(f(0).sharp(), r8(), Accent.STACCATO);
		notes.addnext(g(0), r8(), Accent.STACCATO);
		notes.addnext(g(0).sharp(), r8(), Accent.STACCATO);
		notes.addnext(a(0), r8(), Accent.STACCATO);
		notes.addnext(b(0).flat(), r8(), Accent.STACCATO);
		notes.addnext(b(0), r8(), Accent.STACCATO);
		
		barlineContainer.add(new Barline(notes.getMetricDuration()));

		//Measure 5:
		notes.addnext(c(1), r4());
		notes.addnext(r(), r4());
		notes.addnext(c(1), r8());
		notes.addnext(b(0), r16());
		notes.addnext(c(1), r16());
		
		cont.add(voice);
		cont.add(barlineContainer);
		
		//Measure 1:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 0, 2)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 0, 2)));
		
		//Measure 2:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 5, 7)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 5, 7)));
		
		//Measure 3:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 10, 12)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 10, 12)));
		
		//Measure 4:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 13, 18)));

		//Measure 5:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 21, 23)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 21, 23)));

		ScoreNote n = new ScoreNote(a(1), Rational.ZERO, Rational.ZERO);
		RenderingHints rh = new RenderingHints();
		rh.registerHint("appoggiatura", new Rational(1, 16));
		notes.add(n).setRenderingHints(rh);

		ScoreNote n2 = new ScoreNote(e(1), new Rational(3, 4), Rational.ZERO);
		notes.add(n2).setRenderingHints(rh);

		ScoreNote n3 = new ScoreNote(c(1), new Rational(6, 4), Rational.ZERO);
		notes.add(n3).setRenderingHints(rh);

		ScoreNote n4 = new ScoreNote(d(1), new Rational(12, 4), Rational.ZERO);
		notes.add(n4).setRenderingHints(rh);

	
	}

	void oboeVoice2(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		//Measure 1:
		notes.addnext(r(), r4());
		
		//Measure 2:
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());

		//Measure 3:
		notes.addnext(g(0), r4(), Accent.STACCATO);
		notes.addnext(g(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r8());
		notes.addnext(a(0),r16());
		notes.addnext(g(0), r16());
	
		//Mesaure 4:
		notes.addnext(f(0).sharp(), r8(), Accent.STACCATO);
		notes.addnext(g(0), r8(), Accent.STACCATO);
		notes.addnext(g(0).sharp(), r8(), Accent.STACCATO);
		notes.addnext(a(0), r8(), Accent.STACCATO);
		notes.addnext(b(0).flat(), r8(), Accent.STACCATO);
		notes.addnext(b(0), r8(), Accent.STACCATO);
		
		//Measure 5:
		notes.addnext(c(1), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());
		
		cont.add(voice);
		
		//Mesaure 3:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 6, 8)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 6, 8)));
		
		RenderingHints rh = new RenderingHints();
		rh.registerHint("duplicate of other voice", new Boolean(true));
		for (Iterator iter = getScoreNotes(notes, 6, 8).iterator(); iter.hasNext();) {
			Note element = (Note)iter.next();
			element.setRenderingHints(rh);
		}

		MetricAttachable ma = new MetricAttachable(new Rational(7, 4), new StringSymbol("a 2"));
		ma.setRelativePosition(MetricAttachable.NORTH);
		ma.setDistance(2);
		cont.add(ma);
		//Measure 4:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 9, 14)));
		for (Iterator iter = getScoreNotes(notes, 9, 14).iterator(); iter.hasNext();) {
			Note element = (Note) iter.next();
			element.setRenderingHints(rh);
		}
		
		//Measure 5:
		for (Iterator iter = getScoreNotes(notes, 15, 16).iterator(); iter.hasNext();) {
			Note element = (Note) iter.next();
			element.setRenderingHints(rh);
		}
		
		ScoreNote n = new ScoreNote(c(1), new Rational(6,4), Rational.ZERO);
		RenderingHints rh2 = new RenderingHints();
		rh2.registerHint("appoggiatura", new Rational(1, 16));
		notes.add(n).setRenderingHints(rh2);
	}
	
	void bassoonVoice1(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);
		
		//Measure 1:
		notes.addnext(r(), r4());
		
		//cont.add(new Barline(notes.getMetricDuration()));
		
		//Measure 2:
		
		notes.addnext(b(-1), r4(), Accent.STACCATO);
		notes.addnext(b(-1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());

		//cont.add(new Barline(notes.getMetricDuration()));
		
		//Measure 3:
		notes.addnext(d(0), r4(), Accent.STACCATO);
		notes.addnext(d(0), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());

		//cont.add(new Barline(notes.getMetricDuration()));

		//Measure 4:
		notes.addnext(a(-1), r2());
		notes.addnext(b(-1), r4());
		
		//cont.add(new Barline(notes.getMetricDuration()));
		
		//Measure 5:
		notes.addnext(a(-1), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());
		
		cont.add(voice);
		
		cont.add(new SlurContainer(context, getScoreNotes(notes, 7, 9)));		
	}

	void bassoonVoice2(Container cont) {
		NoteList notes = null;
		for (int i = cont.size() - 1; i >= 0; i--) {
			if (cont.get(i) instanceof Voice) {
				notes = (NoteList)((Voice)cont.get(i)).get(0);
				break;
			}
		}

		Rational onset = Rational.ZERO;
			
		//Measure 1:
		notes.add(r(), onset, r4());
		onset = onset.add(r4());
		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		//Measure 2:
		notes.add(g(-1), onset, r4(), Accent.STACCATO);
		onset = onset.add(r4());
		notes.add(g(-1), onset, r4(), Accent.STACCATO);
		onset = onset.add(r4());
		notes.add(r(), onset, r4());
		onset = onset.add(r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		//Measure 3:
		notes.add(b(-1), onset, r4(), Accent.STACCATO);
		onset = onset.add(r4());
		notes.add(b(-1), onset, r4(), Accent.STACCATO);
		onset = onset.add(r4());
		notes.add(r(), onset, r4());
		onset = onset.add(r4());
		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		SlurContainer slur = new SlurContainer(context);
		//Measure 4:
		Note sn = notes.add(new ScoreNote(c(0), onset, r2()));
		onset = onset.add(r2());
		Note sn2 = notes.add(new ScoreNote(g(-1), onset, r4()));
		onset = onset.add(r4());
		//barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		//Measure 5:
		Note sn3 = notes.add(new ScoreNote(f(-1).sharp(), onset, r4()));
		slur.add(sn); slur.add(sn2); slur.add(sn3);
		
		onset = onset.add(r4());
		notes.add(r(), onset, r4());
		onset = onset.add(r4());
		notes.add(r(), onset, r4());
		onset = onset.add(r4());

		cont.add(slur);		
	}		

	void hornVoice1(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		notes.addnext(r(), r4());
		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		notes.addnext(c(1), r4(), Accent.STACCATO);
		notes.addnext(c(1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(e(1), r4(), Accent.STACCATO);
		notes.addnext(e(1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());

		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(1), r2());
		notes.addnext(e(1), r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(f(1).sharp(), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());
		
		SlurContainer sc = new SlurContainer(context, getScoreNotes(notes, 7, 9));
		RenderingHints rh = new RenderingHints();
		rh.registerHint("dotted", new Boolean(true));
		sc.setRenderingHints(rh);

		cont.add(voice);
		cont.add(sc);
		
	}
	
	void hornVoice2(Container cont) {
		NoteList notes = null;
		for (int i = cont.size() - 1; i >= 0; i--) {
			if (cont.get(i) instanceof Voice) {
				notes = (NoteList)((Voice)cont.get(i)).get(0);
				break;
			}
		}
		Rational onset = Rational.ZERO;
		
		notes.add(r(), onset, r4());
		onset = onset.add(r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		
		notes.add(e(0), onset, r4(), Accent.STACCATO);
		onset = onset.add(r4());
		notes.add(e(0), onset, r4(), Accent.STACCATO);
		onset = onset.add(r4());
		notes.add(r(), onset, r4());
		onset = onset.add(r4());

		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.add(c(1), onset, r4(), Accent.STACCATO);
		onset = onset.add(r4());
		notes.add(c(1), onset, r4(), Accent.STACCATO);
		onset = onset.add(r4());
		notes.add(r(), onset, r4());
		onset = onset.add(r4());

		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		Note sn = notes.add(new ScoreNote(g(0), onset, r2()));
		onset = onset.add(r2());
		Note sn2 = notes.add(new ScoreNote(c(1), onset, r4()));
		onset = onset.add(r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		Note sn3 = notes.add(new ScoreNote(d(1), onset, r4()));
		onset = onset.add(r4());
		notes.add(r(), onset, r4());
		onset = onset.add(r4());
		notes.add(r(), onset, r4());
		onset = onset.add(r4());
		
		SlurContainer sc = new SlurContainer(context);
		sc.add(sn); sc.add(sn2); sc.add(sn3);
		RenderingHints rh = new RenderingHints();
		rh.registerHint("dotted", new Boolean(true));
		sc.setRenderingHints(rh);

		cont.add(sc);
	}

	void clarinetVoice1(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		notes.addnext(r(), r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(1), new Rational(3,4));

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(1), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());

		cont.add(voice);
		cont.add(new SlurContainer(context, getScoreNotes(notes, 7, 8)));		
	}
	
	void clarinetVoice2(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		notes.addnext(r(), r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		notes.addnext(g(0), r4(), Accent.STACCATO);
		notes.addnext(g(0), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(g(0), r4(), Accent.STACCATO);
		notes.addnext(g(0), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(1), new Rational(3,4));

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(1), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());
		
		cont.add(voice);
		cont.add(new SlurContainer(context, getScoreNotes(notes, 7, 8)));		

		RenderingHints rh = new RenderingHints();
		rh.registerHint("duplicate of other voice", new Boolean(true));
		for (Iterator iter = getScoreNotes(notes, 7, 8).iterator(); iter.hasNext();) {
			Note element = (Note) iter.next();
			element.setRenderingHints(rh);
		}
		MetricAttachable ma = new MetricAttachable(new Rational(7, 4), new StringSymbol("a 2"));
		ma.setRelativePosition(MetricAttachable.NORTH);
		ma.setDistance(2);
		cont.add(ma);
		
	}
	
	void timpaniVoice(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		notes.addnext(r(), r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(e(0), r4());
		notes.addnext(e(0), r4());
		notes.addnext(r(), r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(e(0), r4());
		notes.addnext(e(0), r4());
		notes.addnext(r(), r4());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(b(0), r8());
		notes.addnext(b(0), r8());
		notes.addnext(b(0), r8());
		notes.addnext(b(0), r8());
		notes.addnext(b(0), r8());
		notes.addnext(b(0), r8());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(b(0), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());

		cont.add(voice);
		
		cont.add(new BeamContainer(context, getScoreNotes(notes, 7, 12)));
	}

	void violinVoice(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);
		
		//Measure 1:
		notes.addnext(g(1), r8());
		notes.addnext(f(1).sharp(), r16());
		notes.addnext(g(1), r16());

		//barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		//Measure 2:
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(d(1), r4(), Accent.STACCATO);
		notes.addnext(d(1), r8());
		notes.addnext(c(1).sharp(), r16());
		notes.addnext(d(1), r16());

//		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		//Measure 3:
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r8());
		notes.addnext(a(0), r16());
		notes.addnext(g(0), r16());

//		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		//Measure 4:
		notes.addnext(f(0).sharp(), r8(), Accent.STACCATO);
		notes.addnext(g(0), r8(), Accent.STACCATO);
		notes.addnext(g(0).sharp(), r8(), Accent.STACCATO);
		notes.addnext(a(0), r8(), Accent.STACCATO);
		notes.addnext(b(0).flat(), r8(), Accent.STACCATO);
		notes.addnext(b(0), r8(), Accent.STACCATO);
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		
		//Measure 5:
		notes.addnext(c(1), r4());
		notes.addnext(r(), r4());
		notes.addnext(c(1), r8());
		notes.addnext(b(0), r16());
		notes.addnext(c(1), r16());
		
		cont.add(voice);
		
		//Measure 1:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 0, 2)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 0, 2)));
		
		//Measure 2:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 5, 7)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 5, 7)));
		
		//Measure 3:

		cont.add(new BeamContainer(context, getScoreNotes(notes, 10, 12)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 10, 12)));
		
		//Measure 4:
		cont.add(new BeamContainer(context, getScoreNotes(notes, 13, 18)));
		
		//Measure 5:

		cont.add(new BeamContainer(context, getScoreNotes(notes, 21, 23)));
		cont.add(new SlurContainer(context, getScoreNotes(notes, 21, 23)));
		
		
		ScoreNote n = new ScoreNote(a(1), Rational.ZERO, Rational.ZERO);
		RenderingHints rh = new RenderingHints();
		rh.registerHint("appoggiatura", new Rational(1, 16));
		notes.add(n).setRenderingHints(rh);

		ScoreNote n2 = new ScoreNote(e(1), new Rational(6,8), Rational.ZERO);
		notes.add(n2).setRenderingHints(rh);

		ScoreNote n3 = new ScoreNote(c(1), new Rational(12,8), Rational.ZERO);
		notes.add(n3).setRenderingHints(rh);

		ScoreNote n4 = new ScoreNote(d(1), new Rational(24,8), Rational.ZERO);
		notes.add(n4).setRenderingHints(rh);
	}

	void violinVoice2(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		notes.addnext(r(), r4());

//		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(b(0), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(c(0), r2());
		notes.addnext(b(-1), r4());

		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(a(-1), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());
		
		cont.add(voice);
		cont.add(new SlurContainer(context, getScoreNotes(notes, 7, 8)));		
	}

	void violinVoice3(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		notes.addnext(r(), r4());

//		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		notes.addnext(d(0), r4(), Accent.STACCATO);
		notes.addnext(d(0), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(0), r4(), Accent.STACCATO);
		notes.addnext(d(0), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(c(0), r2());
		notes.addnext(b(-1), r4());

		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(a(-1), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());
		
		cont.add(voice);
		cont.add(new SlurContainer(context, getScoreNotes(notes, 7, 8)));		
	}

	void violaVoice(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		notes.addnext(r(), r4());

//		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		notes.addnext(g(-1), r4(), Accent.STACCATO);
		notes.addnext(g(-1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(g(-1), r4(), Accent.STACCATO);
		notes.addnext(g(-1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(a(-1), r2());
		notes.addnext(g(-1), r4());

		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(f(-1).sharp(), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());
		
		cont.add(voice);
		cont.add(new SlurContainer(context, getScoreNotes(notes, 7, 8)));		
	}
	
	void bassVoice(Container cont) {
		NoteList notes = new NoteList(context); Voice voice = new Voice(context); voice.add(notes);

		notes.addnext(r(), r4());

//		barlineContainer.add(new Barline(notes.getMetricDuration()));
		
		notes.addnext(g(-1), r4(), Accent.STACCATO);
		notes.addnext(g(-1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(g(-1), r4(), Accent.STACCATO);
		notes.addnext(g(-1), r4(), Accent.STACCATO);
		notes.addnext(r(), r4());
		
//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(-1), new Rational(3,4));

//		barlineContainer.add(new Barline(notes.getMetricDuration()));

		notes.addnext(d(-2), r4());
		notes.addnext(r(), r4());
		notes.addnext(r(), r4());
		
		cont.add(voice);
	}
	
	public NotationSystem createNotationSystem() {
		context.getPiece().getMetricalTimeLine().add(new TimeSignatureMarker(3, 4, Rational.ZERO));
		KeyMarker marker = new KeyMarker(Rational.ZERO, 0);
		marker.setAccidentalNum(1);
		context.getPiece().getMetricalTimeLine().add(marker);
		//NotationSystem system = new NotationSystem(context);

		
		BasicContainer fluteStaff = new StaffContainer(context);
		fluteVoice(fluteStaff);
		
		BasicContainer oboeStaff = new StaffContainer(context);
		oboeVoice1(oboeStaff);
		oboeVoice2(oboeStaff);
		
		MetricAttachable forte = new MetricAttachable(new Rational(1,4), new CharSymbol('f'));
		forte.setRelativePosition(MetricAttachable.SOUTH);
		oboeStaff.add(forte);
		
		
		StaffContainer bassoonStaff = new StaffContainer(context);
		bassoonVoice1(bassoonStaff);
		bassoonVoice2(bassoonStaff);
		
		ClefContainer bassoonClefs = new ClefContainer(context);
		bassoonClefs.add(new Clef('f', 1));
		bassoonStaff.setClefTrack(bassoonClefs);
		
		
		StaffContainer hornStaff = new StaffContainer(context);
		hornVoice1(hornStaff);
		hornVoice2(hornStaff);

		StaffContainer clarinetStaff = new StaffContainer(context);
		clarinetVoice1(clarinetStaff);
		clarinetVoice2(clarinetStaff);
		RenderingHints rh = new RenderingHints();
		rh.registerHint("voices as chords", new Boolean(true));
		clarinetStaff.setRenderingHints(rh);
		
		StaffContainer timpaniStaff = new StaffContainer(context);
		timpaniVoice(timpaniStaff);
		
		StaffContainer violinStaff1 = new StaffContainer(context);
		violinVoice(violinStaff1);

		StaffContainer violinStaff2 = new StaffContainer(context);
		violinVoice2(violinStaff2);
		violinVoice3(violinStaff2);
		violinStaff2.setRenderingHints(rh);

		StaffContainer violaStaff = new StaffContainer(context);
		ClefContainer violaClefs = new ClefContainer(context);
		violaClefs.add(new Clef('c', 0));
		violaStaff.setClefTrack(violaClefs);
		violaVoice(violaStaff);

		StaffContainer bassStaff = new StaffContainer(context);
		ClefContainer bassClefs = new ClefContainer(context);
		bassClefs.add(new Clef('f', 1));
		bassStaff.setClefTrack(bassClefs);
		bassVoice(bassStaff);
		
		
		/*
		RenderingHints rh = new RenderingHints();
		rh.registerHint("barline", "none");
		system.setRenderingHints(rh);
		*/

		Container cont = context.getPiece().getContainerPool();
		cont.add(fluteStaff);
		cont.add(oboeStaff);
		cont.add(bassoonStaff);
		cont.add(hornStaff);
		cont.add(clarinetStaff);
		cont.add(timpaniStaff);
		cont.add(violinStaff1);
		cont.add(violinStaff2);
		cont.add(violaStaff);
		cont.add(bassStaff);
		
		NotationStaffConnector con1 = new NotationStaffConnector(NotationStaffConnector.BRACKET);
		con1.add(fluteStaff);
		con1.add(bassoonStaff);
		cont.add(con1);

		NotationStaffConnector con2 = new NotationStaffConnector(NotationStaffConnector.BRACKET);
		con2.add(hornStaff);
		con2.add(clarinetStaff);
		cont.add(con2);
		
		NotationStaffConnector con3 = new NotationStaffConnector(NotationStaffConnector.BRACKET);
		con3.add(violinStaff1);
		con3.add(bassStaff);
		cont.add(con3);
		
		NotationStaffConnector con4 = new NotationStaffConnector(NotationStaffConnector.BRACE);
		con4.add(violinStaff1);
		con4.add(violinStaff2);
		cont.add(con4);
		
		return system;
	}
	
    public DiffTestCase1_1_a(String file) {
        super(file);
    }

    public static void main(String argv[]) {
        DiffTestCase1_1_a m = (new DiffTestCase1_1_a("tmp/Melody1.xml"));
        m.setVisible(true);
    }

}