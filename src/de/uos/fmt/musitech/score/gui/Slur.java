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

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;
import de.uos.fmt.musitech.score.util.Angle;
import de.uos.fmt.musitech.score.util.Bezier;
import de.uos.fmt.musitech.score.util.Bow;
import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.math.MyMath;

/** This class represents a musical slur.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Slur extends EventSpanner {
	private boolean aboveNotes; // true if slur curves upwards
	private int height; // in pixel units
	private double tangentAngle; // left tangent angle 
	private int dangling = 0; // 
	private boolean forceAbove = false;
	private boolean dotted = false;
	private boolean atStem = false;
	
	private boolean pullUp = false;
	private boolean pullDown = false;
	private boolean longPullDown = false;
	private float pullDownShift = 0;
	
	public final static int LEFT_DANGLING = 1;
	public final static int RIGHT_DANGLING = 2;
	public final static int NOT_DANGLING = 0; 
	
	private final int DANGLING_X_VALUE = 23; //@@ TODO: get rid of the constant

	@Override
	public void add(Event ev) {
		// avoid leading rests in slur group, so exclude them <= why? we need that for triols with rests
		//if (ev instanceof Chord || numEvents() > 0)
		super.add(ev);
		ev.addSlur(this);
	}

	public void setForceAbove(boolean forceAbove) {
		this.forceAbove = forceAbove;
		this.aboveNotes = forceAbove;
	}
	
	Pair firstSlurPoint(boolean left, boolean above) {
		Pair p = getEvent(0).slurPoint(left, above, atStem);
		if (dangling == LEFT_DANGLING)
			p = p.sub(new Pair(DANGLING_X_VALUE,0));
			
		return p;
	}

	Pair lastSlurPoint(boolean left, boolean above) {
		//System.err.println(getEvent(-1));
		Pair p = getEvent(-1).slurPoint(left, above, atStem);
		if (dangling == RIGHT_DANGLING)
			p = p.add(new Pair(DANGLING_X_VALUE,0));
			
		return p;
	}
		
	public void setDangling(int dangling) {
		this.dangling = dangling;
	}

	/** Draws the slur. */
	@Override
	public void paint(Graphics g) {
		if (numEvents() > 0 && isVisible()) {
			Pair p1 = firstSlurPoint(true, aboveNotes);
			Pair p2 = lastSlurPoint(false, aboveNotes);

			int ld = getEvent(0).staff().getLineDistance();
			int shift = (int)(ld * pullDownShift);
			
			double oldX = p1.getX(); //may get overriden if its a long pull down
			if (pullUp) {
				p1.setY(p1.getY() - shift);
				p2.setY((p2.getY() - getEvent(0).staff().getLineDistance() / 2) - shift);
				aboveNotes = false;
			}
			if (pullDown) {
				
				if (numEvents() != 2) {
					throw new IllegalArgumentException("pull downs need two chords");
				}
				
				p1 = firstSlurPoint(false, aboveNotes);
				p1.setY(p1.getY() - getEvent(0).staff().getLineDistance() / 2);
				if (longPullDown) {
					p1.setX(p2.getX() - getEvent(0).staff().getLineDistance() * 2);
				}
			}
			/*
			double thickness = getEvent(0).staff().getLineDistance() / 4.0;
			Vector points =
				new Bow(p1, p2, Math.round(height - thickness / 2), new Angle(-tangentAngle)).collectToVector(30);
			*/
			//new Bow(p1, p2, Math.round(height + thickness / 2), new Angle(-tangentAngle)).addToVector(points, 30, true);

			/*         Pair pivot = (Pair)points.get(0);
			         for (int i=0; i < points.size(); i++)
			         {
			            Pair p = (Pair)points.get(i);
			            Pair p = (Pair)points.get(i);
			            if (p.equals(pivot))
			            {
			               points.remove(i);
			            }
			            else
			               pivot = p; 
			         }*/

			//         new Graphix(g).fillPolygon(points);
			int height = this.height;
			double tangentAngle = computeTangentAngle(p2, p1);
			if (aboveNotes) {
				height = -height;
				tangentAngle = -tangentAngle;
			}
			try {
				Bezier bezier = new Bow(p1, p2, height, new Angle(tangentAngle));
				bezier.setDotted(dotted);

				Color oldColor = g.getColor();
				g.setColor(getColor());
				if (longPullDown) {
					Staff firstStaff = getEvent(0).staff();
					Staff secondStaff = getEvent(1).staff();
					Pair p3 = getEvent(0).slurPoint(false, true, false);
					// this is the modification from the pullUp above:
					p3.setY(p3.getY() - firstStaff.getLineDistance() / 2);
					if (firstStaff == secondStaff) {
						g.drawLine((int)p1.getX(), (int)p1.getY() - shift, (int)p2.getX(), (int)p2.getY() - shift);
						drawDottedHorizontalLine(g, (int)p3.getX(), (int)p1.getX(), (int)p3.getY() - shift);
					}
					else {
						g.drawLine((int)p1.getX(), (int)p2.getY(), (int)p2.getX(), (int)getEvent(1).slurPoint(false, true, false).getY() + secondStaff.getLineDistance() / 3);

						drawDottedHorizontalLine(g, (int)p3.getX(), firstStaff.absX() + firstStaff.rwidth(), (int)p3.getY());
						drawDottedHorizontalLine(g, secondStaff.absX(), (int)p1.getX(), (int)p2.getY());
					}
				}
				else {
					bezier.paint(g);
				}
				g.setColor(oldColor);
			} catch (IllegalArgumentException e) {
				System.err.println("cannot draw bezier");
			}

			/*
			// for testing purposes only
			Pair extr = extremum();
			g.setColor(Color.RED);
			new Graphix(g).drawCross(extr);
			double[] y = bezier.getY(extr.getX());
			 */
		}
	}
	
	void drawDottedHorizontalLine(Graphics g, int x1, int x2, int y) {
		int length = x2 - x1;
		int ld = getEvent(0).staff().getLineDistance() / 2;
		int x = x1;
		
		while (length > (ld * 2)) {
			g.drawLine(x, y, x + ld, y);
			x += ld * 2;
			length -= ld * 2;
		}
		if (length > 0)
			g.drawLine(x, y, x + length, y);
	}

	/** Computes the slur's direction.
	 * @return 1 if beam above note heads, -1 if below. */
	protected int computeDirection() {
		if (forceAbove)
			return 1;
		int numVoices = getEvent(0).measure().numVoices();
		// only one voice in measure         
		if (numVoices == 1) {
			int i = 0;
			while (getEvent(i) instanceof Rest) {
				i++;
			}
			boolean below = ((Chord) getEvent(i)).isStemUp();
			boolean atHeads = !atStem;
			for (int j = i + 1; j < numEvents() && atHeads; j++)
				if (getEvent(j) instanceof Chord)
					atHeads &= ((Chord) getEvent(j)).isStemUp() == below;

			return (below ^ atHeads) ? 1 : -1;
		}
		// several voices in current staff         
		return (getEvent(0).getVoice() + 1 <= numVoices / 2) ? 1 : -1;
	}

	/** Returns the slope of the line running through both slur endpoints. */
	public double slope() {
		if (numEvents() < 2)
			return 0;
		Pair p1 = firstSlurPoint(true, aboveNotes);
		Pair p2 = lastSlurPoint(false, aboveNotes);
		double dx = p2.getX() - p1.getX();
		if (dx == 0.0)
			return Double.MAX_VALUE; // infinite slope if denominator is 0
		return (p2.getY() - p1.getY()) / dx;
	}

	/** Returns the "length" of this slur, i.e. the distance between the 2 endpoints. */
	public double length() {
		/*
		if (numEvents() < 2)
			return 0;
		*/
		Pair p1 = firstSlurPoint(true, aboveNotes);
		Pair p2 = lastSlurPoint(false, aboveNotes);
		return p1.distanceTo(p2);
	}

	/** Computes the slur height. If b:[0,1]->R<sup>2</sup> is the function that describes
	 *  the current slur with endpoints b(0) and b(1), height h is given as the distance 
	 *  between b(0.5) and b(0)+0.5(b(1)-b(0)). 
	 *  @return height in pixel units */
	protected int computeHeight() {
		double ld = getEvent(0).staff().getLineDistance();
		double len = length() / ld;
		double h; // height in staff spaces

		if (len < 5)
			h = 3.0 * len / 20;
		else if (len < 25)
			h = (3.0 * len + 15) / 40;
		else
			h = 2.25;
		
		return (int) Math.round(h * ld); // height in pixels
	}

	/** Computes and returns the angle of the tangent touching the left endpoint b(0). 
	 *  This angle is equal to arctan (db/dt)(0). */
	protected double computeTangentAngle() {
		Pair p1 = firstSlurPoint(true, aboveNotes);
		Pair p2 = lastSlurPoint(false, aboveNotes);
		
		return computeTangentAngle(p1, p2);
	}
		
	protected double computeTangentAngle(Pair p1, Pair p2) {
		double ld = getEvent(0).staff().getLineDistance();
		double len = p2.distanceTo(p1) / ld;

		// (see F. J. Sola, Computer Design of Musical Slurs, S. 111)
		double yd;
		if (len < 5)
			yd = 0.2 * len;
		else if (len < 25)
			yd = (len + 5.0) / 10;
		else
			yd = 3.0;

		double xd;
		if (len < 4)
			xd = len * 0.3 - 0.1;
		else if (len < 25)
			xd = (len * 3.0 + 15) / 20;
		else
			xd = 2.16;

		return Math.atan(yd / xd);
	}

	/** Returns the highest point if the slur runs above the noteheads, otherwise
	 *  the lowest point is returned. In case of big slopes the extremum can also 
	 *  be one of the endpoints. */
	protected Pair extremum() {
		Pair[] extrema = createBow().verticalExtrema();
		return extrema[aboveNotes ? 0 : 1];
	}

	/** Creates a bow representing the main run of this Slur. The real slur consists 
	 * of (probably) 2 slightly differing bows with equal end points. */
	protected Bow createBow() {
		Pair p1 = firstSlurPoint(true, aboveNotes);
		Pair p2 = lastSlurPoint(false, aboveNotes);
		if (aboveNotes)
			return new Bow(p1, p2, -height, new Angle(-tangentAngle));
		return new Bow(p1, p2, height, new Angle(tangentAngle));
	}

	@Override
	int arrange(int level) {
		if (level > 0)
			return 0;
		
		if (numEvents() > 0) {
			// remove trailing rests
			for (int i = numEvents() - 1; i >= 0; i--)
				if (getEvent(i) instanceof Rest)
					removeLast();
				else
					break;

			boolean visible = false;
			for (int i = 0; i < numEvents(); i++) {
				Event ev = get(i);
				if (ev.isVisible()) {
					visible = true;
					break;
				}
			}
			setVisible(visible);
			
			
			if (numEvents() > 1) {
				Staff leftParent = (Staff)get(0).getParent(Staff.class);
				Staff rightParent = (Staff)get(numEvents() - 1).getParent(Staff.class);
				if (leftParent != rightParent && !isPullDown()) {
					boolean remove = false;
					Slur newSlur = new Slur();
					((SSystem)rightParent.getScoreParent()).addSlur(newSlur);
					List toBeRemoved = new ArrayList();
					for (int i = 1; i < numEvents(); i++) {
						Staff parent = (Staff)get(i).getParent(Staff.class);
						if (parent != leftParent) { //the first event in a different staff
							remove = true;
						}
						if (remove) {
							newSlur.add(get(i));
							toBeRemoved.add(get(i));
						}
					}
					for (int i = 0; i < toBeRemoved.size(); i++) {
						remove((Event)toBeRemoved.get(i));
					}
					newSlur.setDangling(LEFT_DANGLING);
					this.setDangling(RIGHT_DANGLING);
				}
			}
			
			
			// the order of following computations is significant (don't change it)
			aboveNotes = computeDirection() > 0;
			height = computeHeight();
			tangentAngle = computeTangentAngle();
			//System.out.println("barycenter = " + barycenter(createBow()));
			// @@ TODO: St?rnoten beseitigen, 
			//          h?chsten Bogenpunkt in Zwischenraum verlegen
		}
		return 0;
	}

	// @@ noch nicht fertig
	// berechnet die Verteilung der St?rnoten (vgl. Gieseking S. 199)
	private double barycenter(Bow bow) {
		double left = 0, right = 0;
		int numLeft = 0, numRight = 0;
		boolean foundDisturbingPoints = false;
		for (int i = 1; i < numEvents() - 1; i++) {
			Pair p = aboveNotes ? getEvent(i).highestPoint() : getEvent(i).lowestPoint();
			double dy = (p.getY() - bow.getY(p.getX())[0]) * (aboveNotes ? 1 : -1);
			//@@ what happens if multiple solutions exist (rare but possible)?
			if (dy < 0) {
				foundDisturbingPoints = true;
				double bc = barycenter(p);
				if (bc < 0)
					left = MyMath.mean(left, bc, ++numLeft);
				else
					right = MyMath.mean(right, bc, ++numRight);
			}
		}
		if (!foundDisturbingPoints) // no disturbing points? 
			return Double.NaN; // => no barycenter
		return MyMath.mean(left, right, numLeft + numRight);
	}

	/** */
	protected double barycenter(Pair p) {
		Pair p1 = firstSlurPoint(true, aboveNotes);
		Pair p2 = lastSlurPoint(false, aboveNotes);
		Pair dir = p2.sub(p1).unitVector();
		return 2 * p.sub(p1).dotProduct(dir) / length() - 1;
	}
	
	public boolean isDotted() {
		return dotted;
	}
	
	public void setDotted(boolean dotted) {
		this.dotted = dotted;
	}
	public boolean isAtStem() {
		return atStem;
	}
	public void setAtStem(boolean forceAtStem) {
		this.atStem = forceAtStem;
	}
	
	public boolean isPullUp() {
		return pullUp;
	}
	
	/** 
	 * Set if this is a pullUp Slur.
	 * @param pullUp
	 */
	public void setPullUp(boolean pullUp) {
		this.pullUp = pullUp;
	}
	
	public boolean isPullDown() {
		return pullDown;
	}
	
	/**
	 * set if this slur is a pull down slur
	 * @param pullDown
	 */
	public void setPullDown(boolean pullDown) {
		this.pullDown = pullDown;
	}
	
	public boolean isLongPullDown() {
		return longPullDown;
	}
	
	public void setLongPullDown(boolean longPullDown) {
		this.longPullDown = longPullDown;
	}
	
	public float getPullDownShift() {
		return pullDownShift;
	}
	public void setPullDownShift(float pullDownShift) {
		this.pullDownShift = pullDownShift;
	}
}
