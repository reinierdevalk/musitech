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

import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a 'global simultanity'. This is the set of
 * all LocalSims with equal attack time, so a GlobalSim describes all
 * Events of all staves starting at a given time.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $  */
public class GlobalSim implements SpringAttacher {
	private Vector localSims = new Vector();
	private Rational minDuration = Rational.ZERO;
	private boolean firstInMeasure = false;
	private int lwidth = 0;
	private int rwidth = 0;
	private float optimalSpace = 0;
	private static int ID;
	private int id;

	public GlobalSim(boolean firstInMeasure) {
		this.firstInMeasure = firstInMeasure;
		id = ID++;
	}

	/** Returns the attack (onset) time of this GlobalSim */
	@Override
	public Rational attackTime() {
		if (localSims.isEmpty())
			return new Rational(-1);
		return ((LocalSim) (localSims.get(0))).getAlignmentAttack();
	}

	/** Adds a LocalSim to this GlobalSim. */
	public void add(LocalSim ls) {
		if (!localSims.isEmpty() && !attackTime().isEqual(ls.getAlignmentAttack()))
			throw new IllegalArgumentException(
				"\nwrong attack time (" + attackTime() + " expexted, " + ls.getAlignmentAttack() + " received)");
		localSims.add(ls);
		lwidth = Math.max(lwidth, ls.lwidth());
		rwidth = Math.max(rwidth, ls.rwidth());
		if (minDuration.getNumer() == 0) {
			minDuration = ls.minDuration();
			optimalSpace = (float) ls.optimalSpace();
		}
		else if (ls.minDuration().isLess(minDuration)) {
			minDuration = ls.minDuration();
			optimalSpace = (float) ls.optimalSpace();
		}
		ls.setGlobalSim(this);
		
		/*
		System.err.print("GlobalSim " + id + ": ");
		for (Iterator iter = localSims.iterator(); iter.hasNext();) {
			LocalSim element = (LocalSim) iter.next();
			Pitch p = ((Pitch)((Chord)element.child(0)).child(0));
			System.err.print(p.getBase() + "," + element.getAttack() + " ");
		}
		System.err.println("");
		*/
	}

	@Override
	public int lwidth() {
		return lwidth;
	}
	@Override
	public int rwidth() {
		return rwidth;
	}

	/** Returns the duration of the shortest event in this GlobalSim. */
	public Rational minDuration() {
		return minDuration;
	}

	/** Returns true if this GlobalSim is first in its (global) measure. */
	public boolean isFirstInMeasure() {
		return firstInMeasure;
	}

	/** Returns the ideal distance between this GlobalSim and the following (in pixel units).
	 *  This space is assigned if the current line of music need not to be stretched. Otherwise
	 *  the distance will be enlarged or reduced (based on the optimal value). */
	@Override
	public int optimalSpace(SpringAttacher successor) {
		double factor = 1;
		if (successor != null && successor.attackTime() != null && minDuration.sign() > 0)
			factor = (successor.attackTime().sub(attackTime())).div(minDuration).toDouble();

		//@@ This is not yet the corerect formula. All events starting or continuing(!) at
		// the current attack time should be taken into account (not just this one).
		// Those with the shortest duration dictates the optimal space.
		int result = (int) Math.round(optimalSpace * factor + successor.lwidth()); 
		return result;
	}

	public Vector getLocalSims() {
		return localSims;
	}

	@Override
	public void setXPos(int x) {
		for (int i = 0; i < localSims.size(); i++)
			 ((LocalSim) localSims.get(i)).setXPos(x);
	}

	@Override
	public void setParentXPos(int x) {
		for (int i = 0; i < localSims.size(); i++) {
			LocalSim lsim = (LocalSim) localSims.get(i);
			if (lsim.getScoreParent() != null) {
				Measure parent = (Measure)lsim.getScoreParent();
				GlobalMeasure gm = parent.getGlobalMeasure();
				for (int j = 0; j < gm.numStaves(); j++) {
					gm.getMeasure(j).setXPos(x);
				}
			}
		}
	}

	@Override
	public void setMeasureRWidth(int width) {
		for (int i = 0; i < localSims.size(); i++) {
			LocalSim lsim = (LocalSim) localSims.get(i);
			Measure m = lsim.measure();
			if (m != null)
				m.setRWidth(width);
		}
	}

	public int absX() {
		return ((LocalSim) localSims.get(0)).absX();
	}

	public Dimension getSize() {
		return ((LocalSim)localSims.get(0)).getSize();
	}
	
	public Point getLocation() {
		return ((LocalSim)localSims.get(0)).getLocation();
		/*
		Point p = new Point(Integer.MAX_VALUE, 0);
		for (int i = 0; i < localSims.size(); i++) {
			LocalSim lsim = (LocalSim) localSims.get(i);
			p.x = Math.min(lsim.getLocation().x, p.x);
			p.y = Math.max(lsim.getLocation().y, p.y);
		}
		return p;
		*/
	}
	
	public void setLeftPadding(int pad) {
		((LocalSim)localSims.get(0)).setLeftPadding(pad);
		/*
		for (int i = 0; i < localSims.size(); i++) {
			LocalSim lsim = (LocalSim) localSims.get(i);
			lsim.setLeftPadding(pad);
		}
		*/
	}
	
	@Override
	public String toString() {
		String res = "GlobalSim at " + attackTime() + " with duration " + minDuration + "\n";
		for (int i = 0; i < localSims.size(); i++) 
			res += "Staff " + i + ": " + localSims.get(i) + "\n";
		return res;
	}
}
