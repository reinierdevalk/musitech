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
import java.util.Hashtable;

import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a collection of events of a single staff starting
 *  at the same attack time.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class LocalSim extends ScoreContainer implements Metrical {
	private Rational attack; // attack time 
	private Rational minDuration; // duration of the shortest event
	private Rational maxDuration; // duration of the longest event
	private int optimalSpace = 0; // optimal distance to following LocalSim
	private GlobalSim globalSim = null;
	private float spacingFactor = 1;
	private static int ID;
	int id;
	private int alignment = STANDARD_ALIGNMENT;
	
	public static final int STANDARD_ALIGNMENT = 1;
	public static final int BAROQUE_ALIGNMENT = 2;
	
	/**
	 * The LocalSim keeps track of the Accidental placement, because only the LocalSim
	 * can make sure, that they are not overlapping. The basic idea is to have one Hashtable
	 * for each column of Accidental in front of the pitch. Such a column Hashtable contains
	 * ints as keys which correspond to the line-numbers occupied by an Accidental. The values
	 * of the Hashtable are not of interest. accidentalSpace has a column number as key and
	 * the corresponding column hash as value.
	 */
	private Hashtable accidentalSpace = new Hashtable(); 
	final static int LINES_PER_ACCIDENTAL = 4;
	
	/** Constructs a LocalSim beginning at the given attack time. */
	public LocalSim(Rational attack) {
		super();
		this.attack = attack;
		minDuration = maxDuration = Rational.ZERO;
		id = ID++;
	}

	/** Adds an event to this LocalSim. */
	public boolean add(Event ev) {
		super.add(ev);
		Rational dur = ev.getDuration().toRational();
		if (minDuration.getNumer() == 0) {
			minDuration = maxDuration = dur;
			optimalSpace = ev.optimalSpace();
		}
		else {
			if (dur.isLess(minDuration))
				minDuration = dur;
			if (dur.isGreater(maxDuration))
				maxDuration = dur;
			optimalSpace = ev.optimalSpace();
		}
		
		if (measure() != null)
			measure().lsimChanged(this);
			
      	return true;
	}

	/** this method removes all knowledge this LocalSim has about accidentals 
	 * It correponds to unShift() in Head 
	 **/
	
	void unShift() {
		accidentalSpace.clear();
	}
	
	/**
	 * @param line The line where you want to place an Accidental
	 * @return the lowest free column in front of the pitch (starting at 0) for your Accidental.
	 */
	int getAccidentalColumn(int line) {
		int column = 0;
		while (true) {
			Hashtable space;
			if (accidentalSpace.containsKey(new Integer(column))) {
				space = (Hashtable)accidentalSpace.get(new Integer(column));
			}
			else {
				space = new Hashtable();
				accidentalSpace.put(new Integer(column), space);
			}
			boolean columnStillFree = true;
			for (int i = line - LINES_PER_ACCIDENTAL / 2; 
			     i <= line + LINES_PER_ACCIDENTAL / 2; 
			     i++) {
				if (space.containsKey(new Integer(i))) {
					columnStillFree = false;
					break;
				}
			}
			if (columnStillFree) { //occupy the space around the to be placed accidental
				for (int i = line - LINES_PER_ACCIDENTAL / 2; 
				     i <= line + LINES_PER_ACCIDENTAL / 2; 
				     i++) {
					space.put(new Integer(i), new Integer(0));
				}				
				break;
			}
			column++;
		}
		
		return column;
	}
	
	public int arrange(int pass) {
		int numPasses = 3;
		numPasses = Math.max(numPasses, super.arrange(pass));
		
		if (pass == 0) {
			if (measure().numVoices() == 2) {
				// TODO warum ist diese n?chste Abfrage n?tig ?
				if(child(0) != null && child(1) != null &&
				   child(0) instanceof Chord && child(1) instanceof Chord){ //could be a Rest
					Chord c1 = (Chord)child(0);
					Chord c2 = (Chord)child(1);
					Iterator i1 = c1.stemUp ? c1.iterator() : c1.reverseIterator();
					while (i1.hasNext()) {
						Pitch p1 = (Pitch)i1.next();
						Iterator i2 = c2.stemUp ? c2.iterator() : c2.reverseIterator();
						while (i2.hasNext()) {
							Pitch p2 = (Pitch)i2.next();
							if (Math.abs(p2.line() - p1.line()) < 2 &&
								Math.abs(p2.line() - p1.line()) > 0) {
								if (c1.stemUp)
									c1.shiftRight();
								else
									c1.shiftLeft();
							}
						}
					}
				}
			}
		}
		else if (pass == 2) {
			int y = Integer.MAX_VALUE;
			for (int i = 0; i < numChildren(); i++) {
				y = Math.min(y, child(i).getLocation().y);
			}
			
			if (absY() > 50 && y == 0) {
				y = Integer.MAX_VALUE;
				for (int i = 0; i < numChildren(); i++) {
					y = Math.min(y, child(i).getLocation().y);
				}
			}
			
			if (y == Integer.MAX_VALUE)
				y = 0;
			
//			setLocation(absX() - lwidth() + getLeftPadding(), y);
			setLocation(absX() - lwidth(), y);
//			setSize(lwidth() + rwidth() - getLeftPadding(), depth() + height());
			setSize(lwidth() + rwidth(), depth() + height());
		}
		
		return numPasses;
	}
	
	/** Returns the duration of the shortest event in this LocalSim. */
	public Rational minDuration() {
		return minDuration;
	}

	/** Returns the duration of the longest event in this LocalSim. */
	public Rational maxDuration() {
		return maxDuration;
	}

	/** Returns the attack time. */
	public Rational getAttack() {
		return attack;
	}
	
	/** Returns the attack time but may lie about it if the alignment calls for it.
	 * This may happen if (e.g.) the alignment ist baroque where a 3/16 and a 1/16 are aligned
	 * as a triplet. 
	 * @return
	 */
	public Rational getAlignmentAttack() {
		if (alignment == STANDARD_ALIGNMENT)
			return getAttack();
		
		ScoreObject pred = getDirectPredecessor();
		if (!(pred instanceof LocalSim) ||
			pred == null) {
			return getAttack();
		}

		if (alignment == BAROQUE_ALIGNMENT) {
			LocalSim predSim = (LocalSim)pred;
			if (predSim.getMetricDuration().equals(getMetricDuration().mul(3))) { //we are following a dotted note of a one level higher duration
				return getAttack().sub(getMetricDuration().div(3));
			}
		}
		
		return getAttack();
	}
	
	/** This method sets the attack time. */
	public void setAttack(Rational attack) {
		this.attack = attack;
	}

	public LocalSim localSim() {
		return this;
	}

	/** This calculates the optimalSpace for a given note duration and rwidth.
	 * It is static so it can be used in other classes (e.g. Rest).
	 * TODO: find a better location for this method
	 * @param duration
	 * @param rwidth
	 * @return
	 */
	double optimalSpace(Rational duration, int rwidth) {
		if (child(0) instanceof Rest &&
			duration.isGreaterOrEqual(2, 1)) {//for rest which are longer than a double dottet full rest
			duration = new Rational(3, 8); //empirically found value
		}
		return spacingFactor * Math.pow(1.4, Math.log(duration.toDouble() / (1.0 / 32)) / Math.log(2)) * rwidth;
	}
	
	/** Returns the ideal distance of this LocalSim to the following (in pixel units). */
	public double optimalSpace() {
		Staff staff = staff();
		if (staff == null)
			return 0;
		double wmin = 1.0 / 32;
		Rational duration = minDuration;
		
		//find the biggest rwidth of my children:
		int maxRWidth = 0;
		for (int i = 0; i < numChildren(); i++) {
			ScoreObject obj = child(i);
			if (obj instanceof Chord) {
				Tuplet tuplet = staff().belongsToTuplet((Chord)obj);
				if (tuplet != null &&
					tuplet.isLast((Chord)obj)) {
					duration = tuplet.getDuration();
				}
			}
			maxRWidth = Math.max(obj.rwidth() + obj.extraRSpace(), maxRWidth);
		}

		return optimalSpace(duration, maxRWidth);
	}

	/** Returns the parent class of a LocalSim class (it's always a Measure.class) */
	Class parentClass() {
		return Measure.class;
	}

	/** Returns a string representation of this LocalSim. */
	public String toString() {
		String res = "LocalSim at " + attack + " with duration " + minDuration + " ";
		return res;
		//return res + super.toString();
	}

	public int rwidth() {
		int res = 0;
		for (int i = 0; i < numChildren(); i++)
			res = Math.max(res, child(i).rwidth());
		return res;
	}
	
	public int lwidth() {
		int res = 0;
		for (int i = 0; i < numChildren(); i++)
			res = Math.max(res, child(i).lwidth());
		return res + getLeftPadding();
	}
	
	public int depth() { //TODO: incorporate the position of the chord; not only the depth
		int lowestPoint = 0;
		for (int i = 0; i < numChildren(); i++)
			lowestPoint = Math.max(lowestPoint, child(i).absY() + child(i).depth());
		return lowestPoint - absY();
	}

	public int height() {
		int res = 0;
		for (int i = 0; i < numChildren(); i++)
			res = Math.max(res, child(i).height());
		return res;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public Rational getMetricDuration() {
		return maxDuration();
	}
	
	public Rational getMetricTime() {
		return getAttack();
	}
	/**
	 * @return Returns the globalSim.
	 */
	public GlobalSim getGlobalSim() {
		return globalSim;
	}
	/**
	 * @param globalSim The globalSim to set.
	 */
	public void setGlobalSim(GlobalSim globalSim) {
		this.globalSim = globalSim;
	}

	public Rational getCorrectDuration() {
		return ((Event)child(0)).getCorrectDuration(); //TODO: is 0 sufficient?
	}
	public float getSpacingFactor() {
		return spacingFactor;
	}
	public void setSpacingFactor(float spacingFactor) {
		this.spacingFactor = spacingFactor;
	}
	
	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
}
