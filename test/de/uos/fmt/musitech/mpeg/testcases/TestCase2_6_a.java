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

import java.net.URL;
import java.util.Iterator;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.CharSymbol;
import de.uos.fmt.musitech.data.score.CustomSVGGraphic;
import de.uos.fmt.musitech.data.score.DualMetricAttachable;
import de.uos.fmt.musitech.data.score.DynamicsLevelMarker;
import de.uos.fmt.musitech.data.score.DynamicsMarker;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.SVGSymbol;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.StackSymbol;
import de.uos.fmt.musitech.data.score.StringSymbol;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestCase2_6_a extends TestCase {
	
	NotationVoice voice1(NotationSystem system, NotationStaff staff) {
		NoteList notes = new NoteList(context);
		NotationVoice voice = new NotationVoice(staff);
		Rational onset = Rational.ZERO;

		notes.addnext(b(0), new Rational(4, 1));
		notes.addnext(c(0), new Rational(4, 1));
		notes.addnext(e(2), new Rational(2, 1));
		
		notes.add(e(0), new Rational(11, 1), r64());
		
		notes.add(c(1).sharp(), new Rational(11, 1).add(new Rational(3, 64)), r64());
		notes.addnext(d(1), r64());
		notes.addnext(a(0).flat(), r64());
		notes.addnext(g(0), r64());
		notes.addnext(c(1).sharp(), r64());
		notes.addnext(d(1), r64());
		notes.addnext(a(0).flat(), r64());
		notes.addnext(g(0), r64());
		notes.addnext(c(1).sharp(), r64());
		notes.addnext(d(1), r64());
		notes.addnext(a(0).flat(), r64());
		notes.addnext(g(0), r64());
		notes.addnext(c(1).sharp(), r64());
		notes.addnext(d(1), r64());
		notes.addnext(a(0).flat(), r64());
		notes.addnext(g(0), r64());
		notes.addnext(c(1).sharp(), r64());
		notes.addnext(d(1), r64());
		notes.addnext(a(0).flat(), r64());
		notes.addnext(g(0), r64());

		notes.addnext(c(1).sharp(), r64());
		notes.addnext(c(1), r64());
		notes.addnext(g(0).flat(), r64());
		notes.addnext(f(0), r64());
		
		notes.addnext(b(2), new Rational(4, 1));

		voice.addNotes(notes);
		
		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 4, 23)));		
		BeamContainer bc = new BeamContainer(context, getScoreNotes(notes, 4, 23));
		RenderingHints bcrh = new RenderingHints();
		bcrh.registerHint("linear duration progression", new Rational[]{new Rational(1,8), new Rational(1, 64)});
		bc.setRenderingHints(bcrh);

		voice.addSlurContainer(new SlurContainer(context, getScoreNotes(notes, 24, 28)));		
		BeamContainer bc2 = new BeamContainer(context, getScoreNotes(notes, 24, 27));
		voice.addBeamContainer(bc);
		voice.addBeamContainer(bc2);
		
		RenderingHints ncrh = new RenderingHints();
		ncrh.registerHint("stem direction", "down");
		for (Iterator iter = getNotationChords(voice, 24, 27).iterator(); iter.hasNext();) {
			NotationChord element = (NotationChord) iter.next();
			element.setRenderingHints(ncrh);
		}
		
		RenderingHints ncrh2 = new RenderingHints();
		ncrh2.registerHint("draw duration extension", new Boolean(true));
		((NotationChord)voice.get(0)).setRenderingHints(ncrh2);
		((NotationChord)voice.get(1)).addRenderingHint("draw duration extension", new Boolean(true));
		((NotationChord)voice.get(1)).addRenderingHint("duration extension pulldown", new Float(0.5f));
		((NotationChord)voice.get(2)).setRenderingHints(ncrh2);
		((NotationChord)voice.get(voice.size() - 1)).addRenderingHint("draw duration extension", new Boolean(true));
		((NotationChord)voice.get(voice.size() - 1)).addRenderingHint("tremolo", "3");
		
		
		return voice;
	}
	
	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.mpeg.testcases.TestCase#createNotationSystem()
	 */
	public NotationSystem createNotationSystem() {
		NotationSystem system = new NotationSystem(context);
		TimeSignatureMarker tsm = new TimeSignatureMarker(Integer.MAX_VALUE,1,Rational.ZERO);
		RenderingHints tsmRH = new RenderingHints();
		tsmRH.registerHint("visible", Boolean.FALSE);
		tsm.setRenderingHints(tsmRH);
		
		system.getContext().getPiece().getMetricalTimeLine().add(tsm);

		NotationStaff staff = new NotationStaff(system);
		staff.setClefType('g');
		staff.setClefLine(-1);
		
		NotationVoice voice = voice1(system, staff); 
		
		RenderingHints srh = new RenderingHints();
		srh.registerHint("spaced notes", new Rational(1, 64));
		srh.registerHint("note spacing", new Float(4));
		staff.setRenderingHints(srh);
		
		DynamicsLevelMarker fm = new DynamicsLevelMarker();
		fm.setLevel("f");
		fm.setMetricTime(((NotationChord)voice.get(voice.size() - 5)).getMetricTime());
		if(voice.getContextTimeLine() == null){
		    voice.setContextTimeLine(new MetricalTimeLine());
		}
		voice.getContextTimeLine().add(fm);
		
//		MetricAttachable f = new MetricAttachable(Rational.ZERO, new CharSymbol('f'));
//		f.setRelativePosition(MetricAttachable.SOUTH);
//		f.setAlignment(MetricAttachable.CENTER);
//		f.setDistance(3);
//		f.setAnker(((NotationChord)voice.get(voice.size() - 5)).get(0));
//		staff.addAttachable(f);
		
//		MetricAttachable pp = new MetricAttachable(Rational.ZERO, new CharSymbol((char)113));
//		pp.setRelativePosition(MetricAttachable.SOUTH);
//		pp.setAlignment(MetricAttachable.CENTER);
//		pp.setDistance(0);
//		staff.addAttachable(pp);
		if(voice.getContextTimeLine()==null)
		    voice.setContextTimeLine(new MetricalTimeLine());
		DynamicsLevelMarker ppMark = new DynamicsLevelMarker();
		ppMark.setLevel("pp");
		ppMark.setMetricTime(Rational.ZERO);
		voice.getContextTimeLine().add(ppMark);
		
		CharSymbol fermat = new CharSymbol((char)117);
		MetricAttachable f1 = new MetricAttachable(new Rational(21, 2), fermat);
		f1.setRelativePosition(MetricAttachable.NORTH);
		f1.setAlignment(MetricAttachable.CENTER);
		f1.setDistance(0);
		staff.addAttachable(f1);

		MetricAttachable f2 = new MetricAttachable(new Rational(11, 1).add(r64()), fermat);
		f2.setRelativePosition(MetricAttachable.NORTH);
		f2.setAlignment(MetricAttachable.CENTER);
		f2.setDistance(0);
		staff.addAttachable(f2);
		
		StringSymbol sfz = new StringSymbol("sfz");
		URL tearURL = TestCase2_6_a.class.getResource("tear.svg");
		SVGSymbol tear = new SVGSymbol(tearURL.toString());
		StackSymbol stack = new StackSymbol();
		stack.push(sfz);
		stack.push(tear);
		
		MetricAttachable f3 = new MetricAttachable(((NotationChord)voice.get(3)).get(0), stack);
		f3.setRelativePosition(MetricAttachable.SOUTH);
		//f3.setAlignment(MetricAttachable.CENTER);
		f3.setDistance(2);
		staff.addAttachable(f3);
		
		//NotaionChord e = (NotationChord)((NotationVoice)staff.get(0)).get(3);
		
		//MetricAttachable sfz = new MetricAttachable(e, sym);
		
		RenderingHints rh = new RenderingHints();
//		rh.registerHint("barline", "none");
		system.setRenderingHints(rh);
		
		DynamicsMarker dm = new DynamicsMarker();
		dm.setMetricTime(new Rational(11, 1).add(new Rational(3, 64)));
		dm.setEnd(new Rational(11, 1).add(new Rational(22, 64)));
		dm.setCrescendo(true);
		if (voice.getContextTimeLine() == null) {
			voice.setContextTimeLine(new MetricalTimeLine(voice.getContext()));
		}
		voice.getContextTimeLine().add(dm);
		
		URL holdUrl = TestCase2_1.class.getResource("solid-line.svg");
		CustomSVGGraphic hold = new CustomSVGGraphic(holdUrl.toString(), 1, 1);
		hold.setLeadingText("no vib.");
		hold.setDocumentHeight(10);
		DualMetricAttachable h1 = new DualMetricAttachable(((NotationChord)voice.get(0)).get(0),
													   ((NotationChord)voice.get(voice.size() - 1)).get(0),
													   hold);
		h1.setRelativePosition(MetricAttachable.NORTH);
		h1.setDistance(10);
		h1.setRightAnkerAlignment(MetricAttachable.RIGHT);
		staff.addAttachable(h1);
		
		system.addRenderingHint("page bottom space", new Integer(110));
		
		system.processDynamics();
		
		return system;
	}

	public static void main(String argv[]) {
		//(new TestCase2_6_a("/tmp/testcase1.xml")).setVisible(true);
		(new TestCase2_6_a()).setVisible(true);
	}
	
}
