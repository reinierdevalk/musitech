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

import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;

import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a measure that spans all staves. 
 * @author Martin Gieseking, Collin Rogowski
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class GlobalMeasure {
	private Vector measures = new Vector();

	public GlobalMeasure(SSystem system, int measureNo) {
		if (system.numChildren() == 0) {
			throw new IllegalArgumentException("You cannot build a GlobalMeasure around empty systems.");
		}
		for (int i = 0; i < system.numChildren(); i++) {
			Staff staff = (Staff) system.child(i);
			Measure measure = (Measure)staff.child(measureNo);
			if (measure == null) {
				throw new IllegalArgumentException("The measureNo given to GlobalMeasure is invalid for staff " + staff);
			}
			measure.setGlobalMeasure(this);
			measures.add(measure);
		}
	}

	public void setRWidth(int width) {
		for (Iterator iter = measures.iterator(); iter.hasNext();) {
			Measure element = (Measure) iter.next();
			element.setRWidth(width);
		}
	}
	
	public int numStaves() {
		return measures.size();
	}

	public Measure getMeasure(int n) {
		return (Measure) measures.get(n);
	}

	public Barline getRightBarline() {
		return ((Measure) measures.get(0)).getRightBarline();
	}

	public void setRightBarline(Barline barline) {
		for (int i = 0; i < measures.size(); i++)
			 ((Measure) measures.get(i)).setRightBarline(barline);
	}

	public Rational attackTime() {
		return ((Measure) measures.get(0)).attackTime();
	}

	public int rwidth() {
		int width = 0;
		for (int i = 0; i < measures.size(); i++) {
			Measure measure = (Measure) measures.get(i);
			if (measure != null)
				width = Math.max(width, measure.rwidth());
		}
		Barline barline = getRightBarline();
		if (barline != null &&
			barline.isVisible())
			width += barline.rwidth();
		return width;
	}

	public ClefSim initialClefs() {
		ClefSim clefs = new ClefSim();
		for (int i = 0; i < measures.size(); i++) {
			Measure measure = (Measure) measures.get(i);
			if (measure != null && measure.getClef() != null)
				clefs.add(measure.getClef());
		}
		return clefs;
	}

	public KeySignatureSim keySignatures() {
		KeySignatureSim kss = new KeySignatureSim();
		for (int i = 0; i < measures.size(); i++) {
			Measure measure = (Measure) measures.get(i);
			if (measure != null && measure.getKeySignature() != null)
				kss.add(measure.getKeySignature());
		}
		return kss;
	}

	public TimeSignatureSim timeSignatures() {
		TimeSignatureSim tss = new TimeSignatureSim();
		for (int i = 0; i < measures.size(); i++) {
			Measure measure = (Measure) measures.get(i);
			if (measure != null && measure.getTimeSignature() != null)
				tss.add(measure.getTimeSignature());
		}
		return tss;
	}

	public void assimilateBarlines() {
		Barline barline = null;
		int first; // first measure with set barline
		for (first = 0; first < measures.size() && barline == null; first++) {
			Measure m = (Measure) measures.get(first);
			if (m != null && m.getRightBarline() != null)
				barline = m.getRightBarline();
		}
		if (barline == null) {
			barline = new BarlineSingle();
			barline.setVisible(false);
		}
		//		first--; 
		while (--first >= 0) {
			Measure m = (Measure) measures.get(first);
			if (m != null) {
				Barline b = barline.create();
				barline.movePropertiesTo(b);
				b.setParent(m);
				m.setRightBarline(b);
			}
		}
	}

	public void paintRightBarline(Graphics g) {
		Measure firstMeasure = (Measure)measures.get(0);
		Barline barline = firstMeasure.getRightBarline();
		if (!barline.isVisible())
			return;
		int x = barline.absX();
		int y1 = firstMeasure.staff().absY();
		Measure lastMeasure = ((Measure) measures.get(measures.size() - 1));
		if (lastMeasure == null) {
			lastMeasure = firstMeasure;
		}
		Staff botStaff = lastMeasure.staff();
		int y2 = botStaff.absY() + botStaff.naturalHeight();
		boolean[] interruptedBarlines = firstMeasure.system().getInterruptedBarlines(); 
		if (interruptedBarlines.length > 0) {
			for (int i = 0; i < measures.size(); i++) {
				Measure m = (Measure)measures.get(i);
				Staff s = m.staff();
				if (i < interruptedBarlines.length &&
				    interruptedBarlines[i]) {
					barline.paint(g, barline.absX(), s.absY(), s.absY() + s.naturalHeight());
				}
				else {
					int secondY; 
					if (i < (measures.size() - 1)) {
						Measure nextMeasure = (Measure)measures.get(i + 1);
						secondY = nextMeasure.staff().absY();
					}
					else {
						secondY = s.absY() + s.naturalHeight();
					}
					barline.paint(g, barline.absX(), s.absY(), secondY);
				}
			}
		}
		else {
			barline.paint(g, x, y1, y2);
		}
	}
	
	public Vector getMeasures() {
		return measures;
	}
}
