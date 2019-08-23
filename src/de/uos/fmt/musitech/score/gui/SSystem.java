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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.uos.fmt.musitech.data.score.NotationStaffConnector;
import de.uos.fmt.musitech.data.score.NotationStaffConnector.CType;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a collection of several staves (a single
 *  staff is also allowed, of course).
 *  @author Martin Gieseking
 *  @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class SSystem extends ScoreContainer {
	private static int ID;
	public int id;
	
	private List<Beam> beams; // all beams contained in this System
	private List<Slur> slurs; // all slurs contained in this System
	private List<StaffConnector> staffConnectors; // brackets/braces
	private List<Rational> linebreaks;
	private int staffSize = 0; //number of measure in a staff. Must be equal for all staves
	//private int currentCursorXPos = 0; 
	public static int systemNo = 0;
	private int height = 0;
	private boolean[] interruptedBarlines = new boolean[]{};
	
	
	public SSystem() {
		systemNo++;
		id = ID++;
	}

	/**
	 * @param from
	 * @param to
	 * @param type
	 */
	public void addStaffConnector(int from, int to, CType type) {
		if (staffConnectors == null) {
			staffConnectors = new ArrayList<StaffConnector>();
		}
		if (type == NotationStaffConnector.BRACKET) {
			staffConnectors.add(new StaffBracket((Staff) child(from), (Staff) child(to)));
		}
		else if (type == NotationStaffConnector.BRACE) {
			staffConnectors.add(new StaffBrace((Staff) child(from), (Staff) child(to)));
		}
	}
	
	int arrange(int pass) {
		int numPasses = 3;
		int max = Math.max(numPasses, super.arrange(pass));
		if (pass == 0) {
			if (beams != null)
				for (int i = 0; i < beams.size(); i++)
					 beams.get(i).adaptStemDirections();

			//         int ypos = 50;
			/*
			setYPos(50);
			int ypos;
			for (int i = 0; i < numChildren(); i++) {
				ypos = i * 80;
				child(i).setYPos(ypos); // @@
			}
			*/
			//         child(0).setYPos(50);         // @@     	   
			int numMeasures = numberOfMeasures();
			for (int i = 0; i < numChildren(); i++) {
				if (numMeasures != ((Staff)child(i)).numChildren()) {
					throw new IllegalStateException("The staffs in this system have different numbers of measures :" + numMeasures + " != " + ((Staff)child(i)).numChildren());
				}
			}
			for (int i = 0; i < numMeasures; i++) {
				GlobalMeasure gm = new GlobalMeasure(this, i);
				gm.assimilateBarlines();
			}
			LineSpacer.space(this);
			if (beams != null)
				for (int i = 0; i < beams.size(); i++)
					 beams.get(i).arrange(0);

			if (slurs != null)
				for (int i = 0; i < slurs.size(); i++)
					 slurs.get(i).arrange(0);

		}
		if (staffConnectors != null) {
			for (Iterator<StaffConnector> iter = staffConnectors.iterator(); iter.hasNext();) {
				StaffConnector element = iter.next();
				max = Math.max(element.arrange(pass), max);
			}
		}
		
		if (pass == 1) {  //wait till the staves know their aux lines
			int ypos = 0;
			int depthFromAbove = 0;
			int xpos = 0;
			if (staffConnectors != null && staffConnectors.size() > 0) {
				StaffConnector element = staffConnectors.get(0);
				xpos = element.rwidth() / 2;
			}
			
			
			for (int i = 0; i < numChildren(); i++) {
				Staff staff = (Staff)child(i);
				int staffNHeight = staff.naturalHeight();
				int staffHeight = staff.height();
				//increase the ypos by:
				//    the natural size of a staff (4 * lineSpace in general)
				//AND everything the staff wants in excess of that (his total height minus the natural height)
				//AND the space the staff which is (geometrically) above this one wants below itself
				ypos += staffNHeight + (staffHeight - staffNHeight) + depthFromAbove;
				depthFromAbove = staff.depth();
				staff.setYPos(ypos);
				staff.setXPos(xpos);
			}
		}

		if (pass == 2) {
			int y = Integer.MAX_VALUE;
			for (int i = 0; i < numChildren(); i++) {
				y = Math.min(y, child(i).getLocation().y);
			}
			if (y < 0) { //quick hack
				height = -y;
				y = 0;
			}
			int[] minMaxY;
			int[] minMaxX;

			if (staffConnectors != null) {
				minMaxY = getMinMaxY(staffConnectors.toArray(new ScoreObject[]{}));
				minMaxX = getMinMaxX(staffConnectors.toArray(new ScoreObject[]{}));
			}
			else {
				minMaxY = getMinMaxY();
				minMaxX = getMinMaxX();
			}
			//setLocation(absX(), minMaxY[0]);
			setLocation(minMaxX[0], minMaxY[0]);
			//setSize(lwidth() + rwidth(), minMaxY[1] - minMaxY[0]);
			
			setSize(minMaxX[1] - minMaxX[0], minMaxY[1] - minMaxY[0]);
		}
		return max;
	}

	/** Adds a beam to this System. Beams are managed by the system because they can
	 *  span multiple staves. */
	public void addBeam(Beam beam) {
		if (beams == null) // only create vector when necessary
			beams = new ArrayList<Beam>();
		beams.add(beam);
	}
	
	public void addBeams(List<Beam> b) {
		if (beams == null)
			beams = new ArrayList<Beam>();
		for (Iterator<Beam> iter = b.iterator(); iter.hasNext(); ) {
			beams.add(iter.next());
		}
	}

	public void removeBeam(Beam beam) {
		beams.remove(beam);
	}
	
	public void removeSlur(Slur slur) {
		slurs.remove(slur);
	}
	
	/**
	 * Adds a linebreak to the system.
	 * @param linebreak the linebreak to be added
	 */
	public void addLinebreak(Rational linebreak) {
		if (linebreaks == null)
			linebreaks = new Vector<Rational>();
		linebreaks.add(linebreak);
	}

	/**
	 * Sets the linebreaks of this system.
	 * @param linebreaks a vector of rationals, representing linebreaks
	 */
	public void setLinebreaks(Vector<Rational> linebreaks) {
		this.linebreaks = linebreaks;
	}

	/** Adds a slur to this System. */
	public void addSlur(Slur slur) {
		if (slurs == null) // only create vector when necessary
			slurs = new Vector<Slur>();
		slurs.add(slur);
	}

	/** Returns the depth of this System (in pixel units). */
	public int depth() {
		int depth = 0;
		for (int i = 0; i < numChildren(); i++) {
			Staff staff = (Staff)child(i);
			//depth += staff.depth();
			depth += staff.getSize().height;
			if (i < (numChildren() - 1))
				depth += staff.naturalHeight();
		}
		return depth;
	}

	public int height() {
		return height;
	}
	
	/**
	 * determine whether this systems contains chords or rests
	 * @return
	 */
	public boolean containsEvents() {
		for (int i = 0; i < numChildren(); i++) {
			Staff staff = (Staff) child(i);
			if (staff.containsEvents())
				return true;
		}
		
		return false;
	}
	
	/**
	 * removes all staffs from the system for which Staff.containsEvents() returns false
	 *
	 */
	public void removeEmptyStaffs() {
		ArrayList<Staff> toBeRemoved = new ArrayList<Staff>();
		for (int i = 0; i < numChildren(); i++) {
			Staff staff = (Staff) child(i);
			if (!staff.containsEvents()) {
				toBeRemoved.add(staff);
			}
		}
		
		for (Iterator<Staff> iter = toBeRemoved.iterator(); iter.hasNext();) {
			Staff element = iter.next();
			remove(element);
		}
	}
	
	/** Returns the number of measures contained in this System. */
	public int numberOfMeasures() {
		int num = 0;
		for (int i = 0; i < numChildren(); i++) {
			Staff staff = (Staff) child(i);
			num = Math.max(num, staff.numChildren());
		}
		return num;
	}

	/** Draws this System onto the given graphics device. */
	public void paint(Graphics g) {
		super.paint(g);
		if (numChildren() == 0)
			return;
		Staff ts = (Staff) child(0); // top staff
		Staff bs = (Staff) child(-1); // bottom staff
		int x = ts.absX();
		int y1 = ts.absY();
		int y2 = bs.absY() + (bs.getNumberOfLines() - 1) * bs.getLineDistance();
		if (ts != bs)
			g.drawLine(x, y1, x, y2); // initial line that connects staves
		int numMeasures = numberOfMeasures();
		for (int i = 0; i < numMeasures; i++) {
			GlobalMeasure gm = new GlobalMeasure(this, i);
			if (i == (numMeasures - 1)) { //last measure. align to the right
				int barlinePos = page().absX() + page().rwidth();
				for (int j = 0; j < gm.numStaves(); j++) {
					Barline barline = gm.getMeasure(j).getRightBarline();
					/* this is a little hack:
					 * we want to set the absolute x-value of the barline,
					 * but if we just call setXPos it does not work, as the
					 * absX() adds the xpos of barlines parent. So we have to
					 * substract that first... Cannot think of a better solution
					 * in the moment (collin).
					 */
					
					if (barline != null)
						barline.setXPos(barlinePos - barline.getScoreParent().absX());
				}
			}
			gm.paintRightBarline(g);
		}

		// draw beams
		if (beams != null)
			for (int i = 0; i < beams.size(); i++)
				 beams.get(i).paint(g);
		// draw slurs
		if (slurs != null)
			for (int i = 0; i < slurs.size(); i++)
				 slurs.get(i).paint(g);
		// draw brackets or braces
		if (staffConnectors != null)
			for (int i = 0; i < staffConnectors.size(); i++)
				 staffConnectors.get(i).paint(g);
		//drawFrame(g);
	}

	int timeToPixel(Rational time) {
		Staff staff = (Staff) child(0);
		LocalSim lsim = staff.localSimInTime(time);
		if (lsim == null)
			return -1;
		return lsim.absX();
	}

	/** Returns the parent class of this System (it's always Page.class) */
	Class<Page> parentClass() {
		return Page.class;
	}

	public SSystem system() {
		return this;
	}

	/*
	void setMeasureXPos(int measureNo, int xpos) {
		for (int i = 0; i < numChildren(); i++) {
			Measure measure = (Measure) ((Staff) child(i)).child(measureNo);
			if (measure != null)
				measure.setXPos(xpos);
		}
	}
	*/

	/** Returns the amount of space inserted between key signatures 
	 *  and first events. This may be stretched or shrunken by the line spacer. */
	int initialGap() {
		int maxLineDist = 0;
		for (int i = 0; i < numChildren(); i++)
			maxLineDist = Math.max(maxLineDist, ((Staff) child(i)).getLineDistance());
		return maxLineDist;
	}

	public int rwidth() {
		int width = 0;
		for (int i = 0; i < numChildren(); i++)
			width = Math.max(width, child(i).rwidth());
	
		int xpos = 0;
		if (staffConnectors != null && staffConnectors.size() > 0) {
			StaffConnector element = staffConnectors.get(0);
			xpos = element.rwidth() / 2;
		}
		
		return width + xpos;
	}
	
	/*
	public int rwidth() {
		System.err.println("ssys.rwidth");
		return page().rwidth();
	}
	*/

	/** Returns the minimum and maximum attack time of all events belonging to 
	 *  this system. 
	 *  @return array with 2 elements [0]=minimum, [1]=maximum */
	public Rational[] minMaxTime() {
		Rational[] minmax = new Rational[2];
		minmax[0] = Rational.MAX_VALUE;
		minmax[1] = Rational.MIN_VALUE;
		for (int i = 0; i < numChildren(); i++) {
			Staff staff = (Staff) child(i);
			Rational[] mmt = staff.minMaxTime();
			if (mmt[0].isLess(minmax[0]))
				minmax[0] = mmt[0];
			if (mmt[1].isGreater(minmax[1]))
				minmax[1] = mmt[1];
		}
		return minmax;
	}

	/** Returns the attack time of this System (attack time of first global sim in it). */
	Rational attackTime() {
		return ((Staff) child(0)).attackTime();
	}

	public String toString() {
      return "System " + systemNo;      
	}
	
	
	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.gui.score.ScoreObject#catchScoreObject(int, int, java.lang.Class)
	 */
	public ScoreObject catchScoreObject(int x, int y, Class objectClass) {
		if (objectClass.equals(Beam.class) && beams != null) {
			Beam caughtBeam = null;
			for (int i = 0; i < beams.size(); i++) {
				caughtBeam = (Beam)beams.get(i).catchScoreObject(x, y, objectClass);
				if (caughtBeam != null)
					return caughtBeam;
			}
		}
		
		if (objectClass.equals(Slur.class) && slurs != null) {
			Slur caughtSlur = null;
			for (int i = 0; i < slurs.size(); i++) {
				caughtSlur = (Slur)slurs.get(i).catchScoreObject(x, y, objectClass);
				if (caughtSlur != null)
					return caughtSlur;
			}
		}
		
		return super.catchScoreObject(x, y, objectClass);
	}

	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.gui.score.ScoreContainerBase#add(de.uos.fmt.musitech.gui.score.ScoreObject)
	 */
	public boolean add(ScoreObject obj) {
		assert obj instanceof Staff;
		if (staffSize == 0)
			staffSize = ((Staff)obj).numChildren();
		else 
			assert staffSize == ((Staff)obj).numChildren();
		return super.add(obj);
	}
	public boolean[] getInterruptedBarlines() {
		return interruptedBarlines;
	}
	public void setInterruptedBarlines(boolean[] interruptedBarlines) {
		this.interruptedBarlines = interruptedBarlines;
	}
	
	public List<Slur> getSlurs() {
		return slurs;
	}
	
	public void setSlurs(List<Slur> slurs) {
		this.slurs = slurs;
	}
}
