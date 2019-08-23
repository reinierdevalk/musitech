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
 * Created on 10-Dec-2004
 *
 */
package de.uos.fmt.musitech.mpeg.testcases;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.BezierCurveSymbol;
import de.uos.fmt.musitech.data.score.CharSymbol;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.CustomSVGGraphic;
import de.uos.fmt.musitech.data.score.DualMetricAttachable;
import de.uos.fmt.musitech.data.score.Linebreak;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.TablatureNote;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.score.gui.ScoreMapper;
import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *
 */
public class SmallTestCase extends JFrame {
	ScoreMapper mapper;
	NotationSystem system;
	Context context = new Context(new Piece());

	Container markup1 = new BasicContainer<Containable>();
	Container markup2 = new BasicContainer<Containable>();
	void fillPiece() {
		Container cp = context.getPiece().getContainerPool();
		
		NoteList nl = new NoteList(context);
		
		nl.add(new ScorePitch('a', 0, 0), new Rational(5, 1), new Rational(1,2));
		Note note = (Note)nl.get(0);
		RenderingHints nrh = new RenderingHints();
		TablatureNote tn = new TablatureNote();
		tn.setInstrString(2);
		tn.setFret(14);
		//tn.setPullUp(new Rational(1, 2));
		nrh.registerHint("tabulatur note", tn);
		
		nl.addnext(new ScorePitch('f', 0, 0), new Rational(1, 4));
		nl.addnext(new ScorePitch('f', 0, 0), new Rational(1, 4));
		nl.addnext(new ScorePitch('f', 0, 0), new Rational(1, 4));
		nl.addnext(new ScorePitch('f', 0, 0), new Rational(1, 4));
		nl.addnext(new ScorePitch('g', 0, 0), new Rational(1, 8));
		nl.addnext(new ScorePitch('g', 0, 0), new Rational(1, 8));

		for (Iterator iter = nl.iterator(); iter.hasNext();) {
			Note element = (Note) iter.next();
			element.setRenderingHints(nrh);
		}
		RenderingHints nrh2 = new RenderingHints();
		TablatureNote tn2 = new TablatureNote();
		tn2.setInstrString(2);
		tn2.setFret(7);
		tn2.setPullUp(new Rational(1, 2));
		nrh2.registerHint("tabulatur note", tn2);
		((Note)nl.get(1)).setRenderingHints(nrh2);
		
		NoteList nl2 = new NoteList(context);
		nl2.add(new ScorePitch('d', 0, 0), new Rational(5, 1), new Rational(1,2));
		nl2.addnext(new ScorePitch('c', 0, 0), new Rational(1, 4));
		nl2.addnext(new ScorePitch('d', 0, 0), new Rational(1, 4));
		nl2.addnext(new ScorePitch('e', 0, 0), new Rational(1, 4));
		nl2.addnext(new ScorePitch('d', 0, 0), new Rational(1, 4));
		nl2.addnext(new ScorePitch('c', 0, 0), new Rational(1, 8));
		nl2.addnext(new ScorePitch('d', 0, 0), new Rational(1, 8));

		StaffContainer staff = new StaffContainer(context);
		RenderingHints rh = new RenderingHints();
		rh.registerHint("draw tabulatur", new Boolean(true));
		staff.setRenderingHints(rh);
		rh.registerHint("staff lines", "rhythm");
		
		Voice voice = new Voice(context);
		voice.addAll(nl.getContentsRecursiveList(null));
		// TODO find clean solution for type problem
		
		Voice voice2 = new Voice(context);
		voice2.addAll(nl2.getContentsRecursiveList(null));
		// TODO find clean solution for type problem
		
		staff.add(voice);
		staff.add(voice2);

		cp.add(staff);
		
		cp.add(new Barline(new Rational(6, 1)));
		cp.add(new Barline(new Rational(11, 2)));
		SlurContainer sc = new SlurContainer(context);
		sc.add(nl.get(1));
		sc.add(nl.get(2));
		voice.add(sc);
		BeamContainer bc = new BeamContainer(context);
		bc.add(nl.get(5));
		bc.add(nl.get(6));
		voice.add(bc);
		cp.add(new Clef('p', -1, Rational.ZERO));
		cp.add(new Linebreak(new Rational(11, 2)));

		CharSymbol f = new CharSymbol('f');
		f.setParanthesised(true);
		MetricAttachable forte = new MetricAttachable(new Rational(13,2), f);
		forte.setRelativePosition(MetricAttachable.NORTH);
		forte.setDistance(2);
		
		cp.add(forte);

		URL zzUrl = getClass().getResource("zigzag.svg");
		CustomSVGGraphic zzGraphic = new CustomSVGGraphic(zzUrl.toString(), 1, 1);

		DualMetricAttachable dma = new DualMetricAttachable(voice.get(1), voice.get(2), zzGraphic);
		dma.setRelativePosition(MetricAttachable.NORTH);
		dma.setDistance(2);
		
		cp.add(dma);

		BezierCurveSymbol bezierSymbol = new BezierCurveSymbol(new Pair[]{new Pair(1, 10), new Pair(10, 2)});
		DualMetricAttachable dma2 = new DualMetricAttachable(voice.get(0), voice2.get(6), bezierSymbol);
		
		cp.add(dma2);
		
		URL url = getClass().getResource("cross.svg");
		CustomSVGGraphic graphic = new CustomSVGGraphic(url.toString(), 1, 1);
		((Note)voice.get(0)).addRenderingHint("custom head", graphic);
		
		/*
		SVGSymbol svg = new SVGSymbol(url.toString());
		MetricAttachable card = new MetricAttachable(new Rational(11,2), svg);
		card.setRelativePosition(MetricAttachable.NORTH);
		card.setDistance(2);
		*/
		
		//cp.add(card);
		
		((Note)voice.get(0)).addRenderingHint("tremolo", "2");
		
		
		markup1.add(nl.get(1));
		markup1.add(nl2.get(2));
		markup1.add(nl2.get(3));
		markup1.add(nl.get(4));
		
		markup2.add(nl.get(4));
		markup2.add(nl.get(5));
		markup2.add(nl2.get(4));
		markup2.add(nl2.get(5));
	}

	NotationDisplay display = null;
	public SmallTestCase() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		
		fillPiece();
		
		try {
			//display = (NotationDisplay)EditorFactory.createDisplay(system);
			display = (NotationDisplay)EditorFactory.createDisplay(context.getPiece());
		} catch (Exception e) {
			e.printStackTrace();
		}
		display.setOpaque(true);
		display.setAutoZoom(false);
		
		display.addMarkup(markup1);
		
		getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(display, BorderLayout.CENTER);
		MyButtonAction buttonAction = new MyButtonAction();
		JButton button = new JButton(buttonAction);
		buttonAction.putValue(AbstractAction.NAME, "mark");
		getContentPane().add(button, BorderLayout.SOUTH);
		pack();
	}
	
	class MyButtonAction extends AbstractAction {
		int action = 0;
		public void actionPerformed(ActionEvent e) {
			switch (action) {
			case 0: display.addMarkup(markup2); break;
			case 1: display.removeMarkup(markup1); break;
			case 2: display.removeMarkup(markup2); break;
			case 3: display.addMarkup(markup1); break; 
			}
			action++;
			action %= 4;
		}
	}
	
	public static void main(String argv[]) {
		(new SmallTestCase()).setVisible(true);
	}
}
