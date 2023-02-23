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

import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a single measure of a staff. The whole staff
 * is build up by a sequence of multiple LocalStaffs.
 * @author Martin Gieseking, Collin Rogowski
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Staff extends ScoreContainer {
	private static int ID;
	public int id;
	
	private int numLines = 5; // number of staff lines
	private int lineDistance = DEFAULT_LINE_DISTANCE; // distance between 2 staff lines in pixel units
	private Font musicFont = MusicGlyph.createFont(lineDistance);
	private int number = 0; // @@ currently unused
	private Vector tuplets = null;
	private Rational offset = Rational.ZERO; /** This value is substracted from each Notes attack time before drawing*/
	private HashMap auxLines = new HashMap(); /** holds coordinates for the Heads we have to draw auxilliary lines for */
	private int numAuxLinesAbove = 0; /** the number of aux lines above the staff **/
	private int numAuxLinesBelow = 0; /** the number of aux lines below the staff **/
	static final int INITIAL_NOTE_GAP = 2;

	private boolean rhythmStaff = false;
	
	private boolean primary = true;

	private List crescendos = new ArrayList();
	
	static final int HEAD_X = 0;
	static final int HEAD_Y = 1;
	static final int HEAD_WIDTH = 2;
	static final int HEAD_NUM_AUX_LINES = 3;

	private static final int DEFAULT_LINE_DISTANCE = 10;
	
	public Staff() {
		id = ID++;
	}

	public Staff(Rational offset) {
		this();
		this.offset = offset;	
	}

	public void registerAuxLines(Head head) {
		int[] coord = new int[]{head.absX(), head.absY(), head.rwidth(), head.numAuxLines()};
		
		auxLines.put(head, coord);
	}
	
	public void unregisterAuxLines(Head head){
		auxLines.remove(head);
	}

	/** Returns the size of a staff space (distance between 2 staff lines)
	 * in pixel units. */
	public int getLineDistance() {
		return lineDistance;
	}

	/** Returns the number of staff lines. */
	public int getNumberOfLines() {
		return numLines;
	}

	/** set the number of lines in this staff. */
	public void setNumberOfLines(int numLines) {
		this.numLines = numLines;
	}

	/**
	 * This method splits this staff in two halves. The first half contains all measures
	 * which end before the given linebreak; the second half contains all measures
	 * which begin (>=) after the linebreak. "This" becomes the first half, the second half
	 * ist returned.
	 * @param linebreak The attack point at which to break the staff
	 * @return the new Staff (right half)
	 */
	Staff split(Rational linebreak) {
		Staff right = new Staff();
		right.setParent(getScoreParent());
		
		Vector toBeRemoved = new Vector();
		
		for (Iterator iter = iterator(); iter.hasNext();) {
			Measure measure = (Measure)iter.next();
			if (measure.attackTime().isGreaterOrEqual(linebreak)) {
				right.add(measure);
				toBeRemoved.add(measure);
			}
		}	
		for (int i = 0; i < toBeRemoved.size(); i++) {
			getChildren().remove(toBeRemoved.get(i));
		}
		return right;
	}

	/* Returns the predecessor of this Staff. 
	protected Staff predecessor ()
	{
	   if (number == 0  || measure() == null)
	   	return null;
		Measure prevMeasure = measure().predecessor();    
		return (Staff)prevMeasure.child(number-1);
	}*/

	/** Converts a vertical unit given in half spaces (1hs = half of the distance
	 *  between two staff lines) to pixel units. 
	 *  @param hs the given multiple of 1hs. 0 denotes denotes the middle line,
	 *            1 and -1 the centers of the above and below space respectively, etc. */
	protected int hsToPixel(int hs) {
		return 2 * lineDistance - hs * lineDistance / 2;
		//return absY() + 2 * lineDistance - hs * lineDistance / 2;
	}

	/** Converts a vertical unit given in pixel units to multiples of 1hs (@see hsToPixel) */
	public int pixelToHs(int pixel) {
		return (int) Math.round(2.0 * (absY() + 2 * lineDistance - pixel) / lineDistance);
	}

	/** Returns true if the given point is inside this staff. */
	public boolean isInside(Pair p) {
		return Math.abs(pixelToHs(p.getRoundedY())) <= 5; //@@ 
	}

	/*
	void fillGapsWithRests(Rational initialAttack) {
		Rational attack = initialAttack;
		for (int i = 0; i < numChildren(); i++) {
         Measure measure = (Measure)child(i);
         measure.fillGapsWithRests(attack);
         attack.add(measure.duration()); 
		}
	}
	*/

	@Override
	int arrange(int pass) {
		int numPasses = 3;
		if (pass == 0) {
			Measure firstMeasure = (Measure) child(0);
			if (firstMeasure == null)
				return numPasses;
			if (firstMeasure.getClef() == null) {
				Clef clef = new Clef('g', -1);
				clef.setVisible(false);
				firstMeasure.setClef(clef); // @@ maybe make it invisible
			}
		}
		int max = Math.max(numPasses, super.arrange(pass));
		if (pass == 1) { //the heads have registered their aux lines
			for (Iterator iter = auxLines.keySet().iterator(); iter.hasNext();) {
				Head head = (Head)iter.next();
				//TODO: use the heads color for the aux lines
				int[] coord = (int[])auxLines.get(head);
				if (coord[HEAD_Y] < absY()) {
					if (coord[HEAD_NUM_AUX_LINES] > numAuxLinesAbove) {
						numAuxLinesAbove = coord[HEAD_NUM_AUX_LINES];
					}
				}
				else {
					if (coord[HEAD_NUM_AUX_LINES] > numAuxLinesBelow) {
						numAuxLinesBelow = coord[HEAD_NUM_AUX_LINES];
					}
				}
			}
		}
		if (pass == 2) {
			int y = Integer.MAX_VALUE;
			for (int i = 0; i < numChildren(); i++) {
				y = Math.min(y, child(i).getLocation().y);
			}
			y = Math.min(y, ((Measure)child(0)).getClef().getLocation().y);
			
			int[] minMaxY = getMinMaxY();
			setLocation(absX(), minMaxY[0]);

			setSize(lwidth() + rwidth(), minMaxY[1] - minMaxY[0]);//depth() + height());
			if ((lwidth() + rwidth()) == 0)
				rwidth();
		}
		return max;
	}

	/**
	 *
	 * @return the y of the top line of this staff
	 */
	public int getTopLineY() {
		return absY();
	}
	
	public int getBottomLineY() {
		return absY() + ((getNumberOfLines() - 1) * lineDistance);
	}
	
	/** Paints this staff onto the given graphics context. */
	@Override
	public void paint(Graphics g) {
		if (isVisible()) {
			g.setFont(musicFont);
			int x = absX();
			int y = absY();
			int width = page().rwidth();
			for (int i = getNumberOfLines(); i > 0; i--) {
				
				if (!rhythmStaff ||
					(getNumberOfLines() / 2) == i){
				    try{
				        g.drawLine(x, y, x + width, y);
				    }catch(RuntimeException e){
				        if(DebugState.DEBUG_SCORE)
				            e.printStackTrace();
				    }
					
				}
				
				y += lineDistance;
			}

			//draw aux. lines:
			for (Iterator iter = auxLines.values().iterator(); iter.hasNext();) {
				int[] coord = (int[])iter.next();
				int auxy = absY(); // absolute y-pos of first aux. line
				auxy += (coord[HEAD_Y] > absY() ? getNumberOfLines() : -1) * getLineDistance();
				int dir = coord[HEAD_Y] > absY() ? 1 : -1;
				int overlap = getLineDistance() / 3;
				for (int i = coord[HEAD_NUM_AUX_LINES]; i > 0; i--) {
					g.drawLine(coord[HEAD_X] - overlap, auxy, coord[HEAD_X] + coord[HEAD_WIDTH] + overlap, auxy);
					auxy += dir * getLineDistance();
				}

			}
			
			super.paint(g); // paint the children
			if (tuplets != null)
				for (int i = 0; i < tuplets.size(); i++)
					 ((Tuplet) tuplets.get(i)).paint(g);
			
			for (Iterator iter = crescendos.iterator(); iter.hasNext();) {
				Crescendo element = (Crescendo) iter.next();
				element.paint(g);
			}
		}
		//drawFrame(g);
	}
	
	/**
	 * determines whether this staff contains chords or rests
	 * @return
	 */
	public boolean containsEvents() {
		for (int i = 0; i < numChildren(); i++) {
			Measure measure = (Measure)child(i);
			if (measure.numChildren() > 0)
				return true;
		}
		return false;
	}
	
	void putGlyph(Graphics g, char glyph, int line, int x) {
		g.drawString("" + glyph, x, absY() + hsToPixel(line));
	}

   /** Returns the parent class of this staff (it's always SSystem.class) */
	@Override
	Class parentClass() {
		return SSystem.class;
	}

	@Override
	public Staff staff() {
		return this;
	}

	/** Returns the height of this Staff in pixel units. */
	@Override
	public int height() {
		Clef[] clefs = getClefs();
		int maxHeight = 0;
		for (int i = 0; i < clefs.length; i++) {
			//highestY = Math.min(highestY, clefs[i].absY() - clefs[i].height());
			maxHeight = Math.max(maxHeight, clefs[i].height() - Math.abs(clefs[i].absY() - absY()));
		}
		return Math.max(maxHeight, lineDistance * (numAuxLinesAbove + 1));
		//return Math.max(absY() - highestY, lineDistance * (numAuxLinesAbove + 1));
		/*
		return Math.max(absY() - highestY, 
						lineDistance * ((numLines - 1) + numAuxLinesAbove));
						*/
	}
	
	Clef[] getClefs() {
		Vector clefs = new Vector();
		for (int i = 0; i < numChildren(); i++) {
			Measure measure = (Measure)child(i);
			Clef clef = measure.getClef();
			if (clef != null) {
				clefs.add(clef);
			}
		}
		
		return (Clef[])clefs.toArray(new Clef[]{});
	}
	
	/**
	 * returns the depth of this staff (line distance * number of auxiliary lines below the staff)
	 */
	@Override
	public int depth() {
		int high = 0;
		for (int i = 0; i < numChildren(); i++) {
			high = Math.max(child(i).getLocation().y + child(i).depth(), high); 
		}

		return high - getLocation().y;

		/*
		Clef[] clefs = getClefs();
		int lowestY = 0;
		for (int i = 0; i < clefs.length; i++) {
			//lowestY = Math.max(lowestY, clefs[i].absY() + clefs[i].depth());
			lowestY = Math.max(lowestY, clefs[i].depth() - Math.abs(clefs[i].absY() - absY()));
		}
		return Math.max(lowestY, lineDistance * (numAuxLinesBelow + numLines));
		//return Math.max(lowestY - absY(), lineDistance * (numAuxLinesBelow + numLines));
		 */
	}

	public int naturalHeight() {
		return lineDistance * (numLines - 1);
	}

	@Override
	public int rwidth() {
		int res = 0;
		// 		if (clef != null)
		//			res += clef.rwidth() + lineDistance*Clef.BACK_GAP;
		//		if (keySignature != null)
		//			res += keySignature.rwidth();
		//		res += lineDistance * INITIAL_NOTE_GAP;
		for (int i = 0; i < numChildren(); i++) {
			res += child(i).rwidth();
		}
		return res;
	}


	@Override
	public boolean add(ScoreObject obj) {
		Measure measure = (Measure) obj;
		measure.setNumber(numChildren());
		
		for (Iterator iter = measure.iterator(); iter.hasNext();) {
			LocalSim element = (LocalSim)iter.next();
			element.setAttack(element.getAttack().sub(offset));
		}
		
		return super.add(obj);
	}

	public Tuplet belongsToTuplet(Chord chord) {
		if (tuplets == null)
			return null;
		
		for (Iterator iter = tuplets.iterator(); iter.hasNext();) {
			Tuplet element = (Tuplet) iter.next();
			if (element.contains(chord)) {
				return element;
			}
		}
		return null;
	}
	
	public void addTuplet(Tuplet tuplet) {
		if (tuplets == null)
			tuplets = new Vector();
		tuplets.add(tuplet);
	}

	public void removeTuplet(Tuplet tuplet) {
		tuplets.remove(tuplet);
	}
	
	/** Returns the minimum and maximum attack time of all events belonging to 
	 *  this system. 
	 *  @return array with 2 elements [0]=minimum, [1]=maximum */
	public Rational[] minMaxTime() {
		if (numChildren() == 0)
			return null;
		Rational[] ret = new Rational[2];
		ret[0] = ((Measure) child(0)).minMaxTime()[0];
		ret[1] = ((Measure) child(-1)).minMaxTime()[1];
		return ret;
	}

	/** Returns the minimum and maximum attack time of all events belonging to
	 * this staff. Additionally the duration of the last event is added to the
	 * maximum attack time resulting in a "start" and "stop" time of the staff.
	 * @return array with 2 elements [0]=minimum, [1]=maximum
	 */
	public Rational[] minMaxTimePlusDuration() {
		Rational[] minMax = minMaxTime();
		if (minMax == null)
			return null;
		minMax[1] = minMax[1].add(((LocalSim)((Measure)child(-1)).child(-1)).maxDuration());
		
		return minMax;
	}
	
	/** Returns the attack time of this Staff. */
	Rational attackTime() {
		return ((LocalSim) child(0)).getAttack();
	}

	/** Returns the LocalSim beginning or continuing on the given metric time. */
	LocalSim localSimInTime(Rational time) {
		LocalSim lsim = null;
		for (int i = 0; i < numChildren() && lsim == null; i++)
			lsim = ((Measure) child(i)).localSimInTime(time);
		return lsim;
	}

	@Override
	public String toString() {
		return "Staff with " + numLines + " lines\n" + super.toString();
	}
	
	@Override
	public ScoreObject catchScoreObject(int x, int y, Class objectClass) {
		ScoreObject so = super.catchScoreObject(x, y, objectClass);
		if (so != null)
			return so;
			
		if (tuplets != null) {
			for (int i = 0; i < tuplets.size(); i++) {
				so = ((Tuplet)tuplets.get(i)).catchScoreObject(x, y, objectClass);
				if (so != null)
					return so;
			}
		}
		
		return null;
	}
	
	@Override
	public void removeScoreObject(int measure) {
		Head[] heads = ((Measure)child(measure)).getHeads();
		for (int i = 0; i < heads.length; i++) {
			auxLines.remove(heads[i]);
		}

		super.removeScoreObject(measure);
	}

	/**
	 * If set to true this staff displays only the middle line. This is used to signify
	 * that only the rhythm of the piece is of interest.
	 * @param rhythmStaff
	 */
	public void setRhythmStaff(boolean rhythmStaff) {
		this.rhythmStaff = rhythmStaff;
	}
	
	@Override
	public void setScale(float scale) {
		super.setScale(scale);
		lineDistance = (int)(scale * DEFAULT_LINE_DISTANCE);
		
		if (tuplets != null) {
			for (Iterator iter = tuplets.iterator(); iter.hasNext();) {
				Tuplet element = (Tuplet) iter.next();
				element.setScale(scale);
			}
		}
		
	}
	
	public boolean isPrimary() {
		return primary;
	}
	
	/**
	 * set if this is a primary staff. A non-primary staff is one which is mirroring another staff through
	 * the listener interface. See TabulatorStaff as an example of a non-primary staff.
	 * @param primary
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	int getXForMetricTime(Rational metric) {
		for (int i = 0; i < numChildren(); i++) {
			Measure measure = (Measure)child(i);
			Rational[] minMax = measure.minMaxTime(true);
			if (metric.isGreaterOrEqual(minMax[0]) &&
				metric.isLessOrEqual(minMax[1])) {
				return measure.getXForMetricTime(metric);
			}
		}
		return -1;
	}
	
	public void addCrescendo(Rational start, Rational end, int type) {
		Crescendo cres = new Crescendo(start, end, type, this);
		cres.setYPos(getBottomLineY() + getLineDistance() * 6);
		crescendos.add(cres);
	}
}