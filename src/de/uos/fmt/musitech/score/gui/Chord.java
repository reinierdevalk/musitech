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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.score.util.ReverseIterator;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class represents a Chord. Single notes are modelled as 
 * chords with only one pitch assigned.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $  */
public class Chord extends Event implements ScoreContainerBase, Cloneable {
	private List<ScoreObject> children; // vector of pitches (sorted from lowest to highest pitch)
	int stemLength;  // in pixel units
	private boolean forcedStemDirection = false; //set if the user set the stem direction
	boolean stemUp;  // true if stem points up
	private boolean forceDrawStem = false; //true if the user set the drawStem variable
	private boolean drawStem = false; //if a stem has to be drawn
	private int[] stemCoordinates = new int[3];  //the coordinates of the stem. only defined if drawStem == true
	private boolean drawFlag = false;
	private Pair flagCoords;
	char flagGlyph;
	private int shiftStem = 0; //if the chord was shifted because of two voices in one staff, we have to shift the stem
	private Chord entryChord = null;
	private boolean isEntryChord = false;
	// the number of temolo bars
	private int tremolo;
	
	private Collection listeners;
	
	private boolean drawDurationExtension;
	private float durationExtensionPulldown;
	
	private int entryChordSpace = 20; 
	
	public Chord(Duration dur, int voice) {
		super(dur, voice);
		children = new ArrayList<ScoreObject>();
		stemLength = 0;
		stemUp = true;
	}
	
	/**
	 * This is necessary as a Chord may not only be hold by a LocalSim, but
	 * also by another Chord (for entryChords).
	 */
	@Override
	Class parentClass() {
		return ScoreObject.class;
	}
	
	/** This is the same implementation as in ScoreContainer. But as Chord does
	 * not extend ScoreContainer we have to suply it manually.
	 */
	@Override
	public Rational getMetricEndPoint() {
		return child(-1).getMetricEndPoint();
	}

   /** Returns an iterator that can be used to successively step through the pitches
    * starting with the lowest one. */
 	@Override
	public Iterator iterator() {
		return children.iterator();
	}

   /** Returns an iterator that can be used to successively step through the pitches
     * starting with the highest one. */
 	@Override
	public Iterator reverseIterator() {
		return new ReverseIterator(children);
	}

   /** Removes all pitches from this chord. */
	@Override
	public void clear() {
		children.clear();
	}

	@Override
	public boolean contains(ScoreObject o) {
		return children.contains(o);
	}
	
	@Override
	public Rational getMetricTime() {
		return child(0).getMetricTime();
	}

	/** Adds a new Pitch to this Chord. The lowest Pitch resides at the
	 * beginning of the container, the highest at the end. 
	 * @param obj ScoreObject to be added (must be of type Pitch) 
	 * @throws ClassCastException if obj is no Pitch. */
	@Override
	public boolean add(ScoreObject obj) {
		obj.setParent(this);
		Pitch pitch = (Pitch) obj;
		int i = 0;
		while (i < children.size() && ((Pitch) (children.get(i))).trebleLine() < pitch.trebleLine())
			i++;
      if (i < children.size() && ((Pitch) (children.get(i))).trebleLine() == pitch.trebleLine())
         return false;  // we don't support pitches at the same vertical position (yet)
		
      if (scale != 1)
      	obj.setScale(scale);
      children.add(i, obj);
      return true;
	}

	@Override
	public int depth() {
		if (drawStem) {
			if (stemUp) { 
				return child(0).depth();
			}
			else {
				return Math.abs(stemCoordinates[1] - stemCoordinates[2]);
			}
		}
		else {
			return child(0).depth();
		}
	}
	
	@Override
	public int height() {
		int height;
		if (drawStem) {
			if (stemUp) { 
				height = Math.abs(stemCoordinates[1] - stemCoordinates[2]);
			}
			else {
				height = child(-1).height();
			}
		}
		else {
			height = child(0).height();
		}
		
		if (entryChord != null) {
			int entryChordTop = entryChord.absY() - entryChord.height();
			int top = absY() - height;
			if (entryChordTop < top) {
				height += top - entryChordTop;
			}
		}
		
		return height;
	}
	
	/*
	public int height() {
		int height = 0;
		for (int i = 0; i < numChildren(); i++) {
			height += child(i).height();
		}
		
		return height;
	}
	*/
	
	@Override
	public int lwidth() {
		int max = 0;
		
		for (int i = 0; i < numChildren(); i++) {
			if (child(i).lwidth() > max)
				max = child(i).lwidth();	
		}

		if (entryChord != null) {
			max += entryChordSpace + entryChord.lwidth();
		}
		
		return max;
	}

	@Override
	public int rwidth() {
		int max = 0;
		
		for (int i = 0; i < numChildren(); i++) {
			if (child(i).rwidth() > max)
				max = child(i).rwidth();	
		}
		if (drawFlag) {
			max += (int)(MusicGlyph.width(staff().getLineDistance(), flagGlyph) * scale);
		}
		return max;
	}

	@Override
	public int arrange(int pass) {
		if(pass==0){
			boolean draw = false;
			for (int i = 0; i < numChildren(); i++) 
				if(((Pitch)child(i)).isVisible())
					draw = true;
			setVisible(draw);
		}
		int numPasses = 3;
		if (entryChord != null)
			entryChord.arrange(pass);
		if (numChildren() == 0)
			return numPasses;

		flagGlyph = getFlagGlyph();
		if (!isInBeam() && flagGlyph != 0) {
			drawFlag = true;
			flagCoords = beamPoint();
		}

		localSim().unShift();
		int max = numPasses;
		for (int i = numChildren() - 1; i >= 0; i--) {
			max = Math.max(max, child(i).arrange(pass));
		}

		if (pass == 0) {
			unShift();
			
			// compute stem direction and length
			int upper = Math.abs(highestPitch().line());
			int lower = Math.abs(lowestPitch().line());
			if (!isInBeam() && !forcedStemDirection) {
				if (measure().numVoices() == 1)
					stemUp = averageLine() < 0;
				else
					stemUp = getVoice() + 1 <= measure().numVoices() / 2;
			}
			stemLength = computeStemLength();

			// arrange note heads
			Iterator iter = stemUp ? iterator() : reverseIterator();
			int lastLine = ((Pitch) iter.next()).line();
			while (iter.hasNext()) {
				Pitch pitch = (Pitch) iter.next();
				int currentLine = pitch.line();
				if (Math.abs(currentLine - lastLine) < 2) {
					if (stemUp)
						pitch.shiftRight();
					else
						pitch.shiftLeft();
				}
				else
					lastLine = currentLine;
			}
		}
		super.arrange(pass);

		if (isEntryChord) {
			setXPos(-entryChordSpace);
		}
		
		if (pass == 2) { //wait till SSystem has done it's layout (otherwise absY() is not correct)

			calculateStemCoordinates();
			if (!getDuration().toRational().isLess(1, 1) && !forceDrawStem) {
				drawStem = false;
			}

			
			int y;
			/* This is a bit of a hack. we cannot alter absY, but measure needs a different 
			 * ypos than the one of absY(). So we have to implement an extra method... It would
			 * be better to find out why absY cannot be altered (perhaps it has to be altered at
			 * an earlier arrange-round than 2???), but this would open a can of worms...
			 */
			if (drawStem) {
				if (stemUp) { 
					y = Math.min(stemCoordinates[1], stemCoordinates[2]);
				}
				else {
					y = child(-1).absY() - child(-1).height();
				}
			}
			else {
				/*
				 sizeY = Math.abs(child(-1).absY() + child(-1).height() -
				 child(0).absY() + child(0).depth());
				 */
				y = child(0).absY() - child(0).height();
			}
			
			if (entryChord != null) {
				y = Math.min(entryChord.getLocation().y, y);
			}
			
			setLocation(absX() - lwidth(), y);
			setSize(lwidth() + rwidth(), depth() + height());
			//setToolTipText(toString());
		}

		return max;
	}
	
	void unShift() {
		shiftStem = 0;
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Pitch p = (Pitch)iter.next();
			p.unShift();
		}
	}
	
	void shiftRight() {
		if (shiftStem != 0)
			return; //do not shift twice
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Pitch p = (Pitch)iter.next();
			p.shiftRight();
			shiftStem = p.getHead().getShiftAmount();
		}
	}
		
	void shiftLeft() {
		if (shiftStem != 0)
			return; //do not shift twice
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Head h = ((Pitch)iter.next()).getHead();
			h.shiftLeft();
			shiftStem = -h.getShiftAmount();
		}
	}
	
	private void calculateStemCoordinates() {
		if (forceDrawStem)
			return;
		
		Head head = ((Pitch) child(0)).getHead();
		int xofs = stemUp && head != null ? head.rwidth() - 1 : 0;
		xofs += shiftStem;
		stemCoordinates[0] = absX() + xofs;
		stemCoordinates[1] = bottomPitch().absY(); // stem base
		if (stemUp)
			stemCoordinates[1]--;
		int dy = bottomPitch().absY() - topPitch().absY();
		stemCoordinates[2] = stemCoordinates[1] - dy - (stemLength * (stemUp ? 1 : -1)); // stem tip
		if (stemUp)
			stemCoordinates[2]++;
		else
			stemCoordinates[2]--;
		drawStem = true;
	}

	/** Returns the n-th pitch of this Chord. The lowest pitch is located at 
	 *  index 0. A negative index indicates a reverse lookup: -1 denotes the 
	 *  last (highest) pitch. */
	@Override
	public ScoreObject child(int n) {
		if (n < -children.size() || n >= children.size())
			return null;
		if (n < 0)
			n += children.size();
		return children.get(n);
	}

	public Pitch getPitch(int n) {
		return (Pitch) child(n);
	}

	/** Returns true if stem points up. */
	public boolean isStemUp() {
		return stemUp;
	}

	/** Returns the stem length in pixel units */
	public int getStemLength() {
		return stemLength;
	}

	/** Sets the stem length in pixel units. */
	void setStemLength(int l) {
		stemLength = l;
	}

	/** Sets the stem direction.
	 * @param up true: stem points upwards */
	void setStemUp(boolean up) {
		if (forcedStemDirection)
			return;
		stemUp = up;
	}

	/** Returns the highest Pitch of this Chord.*/
	public Pitch highestPitch() {
		return (Pitch) child(-1);
	}

	/** Returns the lowest Pitch of this Chord. */
	public Pitch lowestPitch() {
		return (Pitch) child(0);
	}

	/** Returns the Pitch closest to the stem tip. */
	public Pitch topPitch() {
		return stemUp ? highestPitch() : lowestPitch();
	}

	/** Returns the Pitch farthest to the stem tip. */
	public Pitch bottomPitch() {
		return stemUp ? lowestPitch() : highestPitch();
	}

	/** Returns the average vertical position of all noteheads of this chord (in line units). */
	public int averageLine() {
		int n = numChildren();
		if (n == 0)
			return 0;
		int res = 0;
		for (int i = 0; i < n; i++)
			res += ((Pitch) child(i)).line();
		return res / n;
	}

	/** Returns the Pitch width the largest distance from the middle line */
	public Pitch farthestPitch() {
		int line1 = highestPitch().line();
		int line2 = lowestPitch().line();
		if (Math.abs(line1) < Math.abs(line2))
			return highestPitch();
		return lowestPitch();
	}

	/** Returns number of pitches assigned to this chord. */
	@Override
	public int numChildren() {
		return children.size();
	}
	
	protected void paintTremolo(Graphics g, int num) {
		int ld = staff().getLineDistance();
		int x = stemCoordinates[0];
		int averageY = Math.max(stemCoordinates[1], stemCoordinates[2]) - 
		              Math.abs(stemCoordinates[1] - stemCoordinates[2]) / 2;
		int y1 = averageY + ld / 2;
		int y2 = averageY - ld / 2;
		int x1 = x - ld / 2;
		int x2 = x + ld / 2;
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x1, y1 + ld / 2, x2, y2 + ld / 2); 
		g.drawLine(x1, y1 - ld / 2, x2, y2 - ld / 2); 
	}

	/** Draws this Chord onto the given Graphics object. */
	@Override
	public void paint(Graphics g) {
		if (!isVisible())
			return;
		paintBackground(g);
		Color oldColor = g.getColor();
		g.setColor(getColor());
		for (int i = 0; i < children.size(); i++)
			child(i).paint(g);
		// draw stem if necessary
		
		if (drawStem) {
			g.drawLine(stemCoordinates[0], stemCoordinates[1], 
                       stemCoordinates[0], stemCoordinates[2]);
		}
		if (getTremolo()>0) {
			paintTremolo(g,getTremolo());
		}
		
		// draw flag if necessary
		if (drawStem && drawFlag) {
			setScaledFont(g);
			g.drawString(flagGlyph + "", (int) flagCoords.getX() + shiftStem, (int) flagCoords.getY());
			restoreFont(g);
		}
		if (!drawDurationExtension) {
			// draw augmentation dots if necessary
			Staff staff = staff();
			int r = staff.getLineDistance() / 4;
			int maxWidth = 0;
			for (int i = 0; i < numChildren(); i++) {
				maxWidth = Math.max(maxWidth, ((Pitch)child(i)).rwidth());
			}
			for (int d = 0; d < getDuration().getDots(); d++) {
				int x = absX() + maxWidth + staff.getLineDistance() + d * 3 * r; // @@
				for (int i = 0; i < numChildren(); i++) {
					Pitch pitch = (Pitch)child(i);
					int y = absY() + staff.hsToPixel(pitch.getDotLine());
					x += pitch.getDotXOffset();
					g.fillOval(x - r, y - r, 2 * r, 2 * r);
				}
			}
		}
		g.setColor(oldColor);
		
		if (entryChord != null) {
			entryChord.paint(g);
		}
			
		super.paint(g);
		//drawFrame(g);
	}

	/*	public int getStemAbsX ()
		{
		   int x = absX();
		   if (stemUp)
		   	x += ((Pitch)child(0)).getHead().rwidth();
		   return x;
		}*/

	/** Returns a string representation of this Chord. */
	@Override
	public String toString() {
		String res = "Chord of voice " + getVoice() + " with duration " + getDuration().toRational() + " {";
		for (int i = 0; i < children.size(); i++) {
			res += (children.get(i));
         if (i < children.size()-1)
            res += ", ";
      }
      res += "}";
		return res;
	}

	/** Line position that is used to compute the beam direction. */
	@Override
	int beamDirectionLine() {
		return farthestPitch().line();
	}

	/** Returns a 2D point that should be used in the regression subroutine 
	 * to calculate the beam slope. */
	@Override
	Pair regressionPoint() {
		return new Pair(absX(), topPitch().absY());
	}

	/** Returns the 2D point where a potential beam will be attached. */
	@Override
	Pair beamPoint() {
		int ld = staff().getLineDistance();
		Head head = ((Pitch) child(0)).getHead();
		int hw = head != null ? head.rwidth() : 0;
		if (stemUp)
			return new Pair(absX() + hw, topPitch().absY() - stemLength);
		return new Pair(absX(), topPitch().absY() + stemLength);
	}

	/** Returns the point where potential slur ends are placed. 
	 * @param left  true if point of left slur endpoint should be returned, 
	 *              right endpoint otherwise
	 * @param above true if slur lies abobe the noteheads */
	@Override
	Pair slurPoint(boolean left, boolean above, boolean atStem) {
		int ld = staff().getLineDistance();
		Pair bp = beamPoint();
		Pair res;
		if (atStem) {
			if (stemUp) {
				res = bp.sub(new Pair(0, ld / 2));
			}
			else {
				res = bp.add(new Pair(0, ld / 2));
			}
		}
		else if (isInBeam()) {
			if (above) // slur above notes
				if (stemUp)
					res = bp.sub(new Pair(0, ld / 2));
				else { // stem down					
					//int dx = getPitch(0).getHead().rwidth() * (left ? 1 : -1);
					//res = new Pair(absX() + dx, absY() + 3 * ld / 2);
					int dx = getPitch(0).getHead().rwidth() * (left ? 1 : 0);
					res = new Pair(absX() + dx, highestPitch().absY() - ld);
				}
			else // slur below notes
				if (stemUp) {
					int dx = (int)(getPitch(0).getHead().rwidth() * (left ? .5 : .5));
					res = new Pair(absX() + dx, lowestPitch().getHead().absY() + lowestPitch().getHead().depth() + (ld / 3));
				}
				else // stem down
					res = bp.add(new Pair(0, ld / 2));
		}
		// chord isn't part of a beam group //@@ noch nicht optimiert
		else if (above) // slur above notes
			if (stemUp) {
				//res = bp.sub(new Pair(0, ld / 2));
				int dx =(int)(getPitch(0).getHead().rwidth() * (left ? 1 : .5));
				res = new Pair(absX() + dx + shiftStem, highestPitch().absY() - ld / 2);
			}
			else { // stem down				
				int dx = getPitch(0).getHead().rwidth() * (left ? 1 : 0);
				res = new Pair(absX() + dx + shiftStem, lowestPitch().absY() - ld);
			}
		else // slur below notes
			if (stemUp) {
				int dx = getPitch(0).getHead().rwidth() * (left ? 1 : 0);
				res = new Pair(absX() + dx + shiftStem, lowestPitch().absY() + ld);
			}
			else // stem down
				res = bp.add(new Pair(0, ld / 2));

		if (drawDurationExtension)
			res.setX(res.getX() + localSim().optimalSpace(localSim().getMetricDuration(), rwidth()));
		// move point between 2 staff lines      
		if (!staff().isInside(res))
			return res;
		int hs = staff().pixelToHs(res.getRoundedY());
		if (hs % 2 == 0) // on line
			hs += above ? 1 : -1;
		return new Pair(res.getX(), staff().absY() + staff().hsToPixel(hs));
	}

	/** Computes and returns the prevered stem length of this Chord. This method is
	 *  used internally and should not be called by the user. To get the stem length
	 *  use getStemLength instead. */
	int computeStemLength() {
		int spaces = 7; // stem length in half line distances (7 = normal length)
		int pivot = topPitch().line();
		if (isInBeam() || getDuration().toRational().getDenom() <= 4)
			if ((stemUp && pivot > 0) || (!stemUp && pivot < 0))
				spaces = 5; // shortened length
		else
			spaces = Math.max(7, Math.abs(pivot)); // lengthened stem
		
		if (getDuration().toRational().getDenom() >= 32) {
			spaces += getDuration().toRational().getDenom() / 64 * 5; 
		}
		
		if (staff().getScale() == 1) { //only the chord is scaled
			return (int)((spaces * staff().getLineDistance() / 2) * scale);
		}
		else { //the whole staff is scaled... ignore the individual scale
			return (spaces * staff().getLineDistance()) / 2;
		}
	}

	/** Returns the glyph representing the flag for the current chord duration and 
	 *  stem direction. If the current duration doesn't imply a flag or if it isn't
	 *  supported 0 is returned. */
	char getFlagGlyph() {
		switch (getDuration().getBase().getDenom()) {
			case 8 :
				return stemUp ? MusicGlyph.FLAG_8_UP : MusicGlyph.FLAG_8_DOWN;
			case 16 :
				return stemUp ? MusicGlyph.FLAG_16_UP : MusicGlyph.FLAG_16_DOWN;
			case 32 :
				return stemUp ? MusicGlyph.FLAG_32_UP : MusicGlyph.FLAG_32_DOWN;
			case 64 :
				return stemUp ? MusicGlyph.FLAG_64_UP : MusicGlyph.FLAG_64_DOWN;
		}
		return 0;
	}

	/*
	public int rwidth() {
		if (numChildren() == 0)
			return 0;
		Head h = ((Pitch) child(0)).getHead();
		return h.rwidth(); // @@ ausgelagerte K?pfe werden noch nicht ber?cksichtigt
	}
	*/

	/** Returns the topmost visible point of this Chord. */
	@Override
	public Pair highestPoint() {
		if (stemUp)
			return beamPoint();
		Head head = highestPitch().getHead();
		return new Pair(head.absX() + head.rwidth() / 2, head.absY() - head.height());
	}

	/** Returns the bottommost visible point of this Chord. */
	@Override
	public Pair lowestPoint() {
		if (!stemUp)
			return beamPoint();
		Head head = highestPitch().getHead();
		return new Pair(head.absX() + head.rwidth() / 2, head.absY() + head.depth());
	}

	//   public TiedChord toTiedChord () throws CloneNotSupportedException
	//   {
	//      TiedChord tc = (TiedChord)clone();
	//      return tc;
	//   }

	void movePropertiesTo(Chord c) {
		super.movePropertiesTo(c);
		c.children = children;
		for (int i = 0; i < numChildren(); i++)
			 ((Pitch) child(i)).setParent(c);
		c.stemLength = stemLength;
		c.stemUp = stemUp;
	}
	
	@Override
	public ScoreObject catchScoreObject(int x, int y, Class objectClass) {
		//the Chord matches if one of its pitches matches:
		if (this.getClass().equals(objectClass)) {
			for (int i = 0; i < numChildren(); i++) {
				if (getPitch(i).contains(x, y)) {
					return this;		
				}
			}
		}	
		
		for (int i = 0; i < numChildren(); i++) {
			ScoreObject so = child(i).catchScoreObject(x, y, objectClass);
			if (so != null)
				return so;
		}
		
		return null;
	}

	@Override
	public void setColor(Color c) {
		super.setColor(c);
		
		for (int i = 0; i < numChildren(); i++) {
			child(i).setColor(c);
		}
	}
	
	@Override
	public void setScale(float s) {
		super.setScale(s);
		for (int i = 0; i < numChildren(); i++) { 
			child(i).setScale(s);
		}
	}
	
	final static int STEM_AUTOMATIC = 1;
	final static int STEM_UP = 2;
	final static int STEM_DOWN = 3;
	
	public void setStemDirection(int direction) {
		switch (direction) {
		case STEM_AUTOMATIC:
			forcedStemDirection = false;
			break;
		case STEM_UP:
			forcedStemDirection = true;
			stemUp = true;
			break;
		case STEM_DOWN:
			forcedStemDirection = true;
			stemUp = false;
			break;
		default:
			throw new IllegalArgumentException(direction + " is not a valid argument");
		}
	}
	
	@Override
	public Chord chord() {
		return this;
	}

	/**
	 * @return Returns the entryChord.
	 */
	public Chord getEntryChord() {
		return entryChord;
	}
	/**
	 * @param entryChord The entryChord to set.
	 */
	public void setEntryChord(Chord entryChord) {
		this.entryChord = entryChord;
		entryChord.setParent(this);
		entryChord.isEntryChord = true;
	}
	
	@Override
	public void setVisible(boolean v) {
		super.setVisible(v);
		if (entryChord != null) {
			entryChord.setVisible(v);
		}
	}
	public boolean isDrawDurationExtension() {
		return drawDurationExtension;
	}
	public void setDrawDurationExtension(boolean drawDurationExtension) {
		for (int i = 0; i < numChildren(); i++) { 
			((Pitch)child(i)).getHead().setDrawDurationExtension(drawDurationExtension);
		}
		this.drawDurationExtension = drawDurationExtension;
	}
	public boolean isForcedStemDirection() {
		return forcedStemDirection;
	}
	public void setForcedStemDirection(boolean forcedStemDirection) {
		this.forcedStemDirection = forcedStemDirection;
	}
	
	@Override
	public void addListener(ContentChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(listener);
	}
	
	@Override
	public void removeListener(ContentChangeListener listener) {
		listeners.remove(listener);
	}
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	
	public int getTremolo() {
		return tremolo;
	}
	
	public void setTremolo(int tremolo) {
		this.tremolo = tremolo;
	}
	
	public boolean isDrawStem() {
		return drawStem;
	}
	public void setDrawStem(boolean drawStem) {
		this.drawStem = drawStem;
		forceDrawStem = true;
	}
	public float getDurationExtensionPulldown() {
		return durationExtensionPulldown;
	}
	public void setDurationExtensionPulldown(float durationExtensionPulldown) {
		this.durationExtensionPulldown = durationExtensionPulldown;
	}
}

