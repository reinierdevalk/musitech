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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a combination of note head and accidental.
 * 
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ 
 * */
public class Pitch extends ScoreObject {
	private char base; // Diatonic note, Stammton
	private byte octave; // (0 = middle Octave)
	private Accidental accidental;
	private Head head;
	private Note note;
	
	public Pitch(Duration duration, char base, byte shift, boolean forceAccidental, byte octave) {
		head = new Head(this, duration.toRational());
		//      if (shift != 0 || forceAccidental)
		accidental = new Accidental(this, shift);
		base = Character.toLowerCase(base); // ensure lower case letters
		if (base < 'a' || base > 'h')
			throw new IllegalArgumentException("Pitch base '" + base + "' unknown");
		this.base = (base == 'h' ? 'b' : base);
		this.octave = octave;
	}
	
	public Pitch(Duration duration, char base, byte shift, boolean forceAccidental, byte octave, Note note) {
		this(duration, base, shift, forceAccidental, octave);
		this.note = note;
	}

	protected Pitch() {
		
	}
	
	/**
	 * @see de.uos.fmt.musitech.score.gui.ScoreObject#setScale(float)
	 */
	@Override
	public void setScale(final float argScale) {
		super.setScale(argScale);
		if (head != null)
			head.setScale(argScale);
		if (accidental != null)
			accidental.setScale(argScale);
	}
	
	/**
	 * @see de.uos.fmt.musitech.score.gui.ScoreObject#getMetricEndPoint()
	 */
	@Override
	public Rational getMetricEndPoint() {
		if (note == null)
			return super.getMetricEndPoint();
		
		return note.getScoreNote().getMetricTime().add(note.getScoreNote().getMetricDuration());
	}
	
	/**
	 * @see de.uos.fmt.musitech.score.gui.ScoreObject#getMetricTime()
	 */
	@Override
	public Rational getMetricTime() {
		if (note == null)
			return super.getMetricTime();
		return note.getScoreNote().getMetricTime();
	}
	/**
	 * @return the Note associated with this pitch. May be null
	 */
	public Note getNote() {
		return note;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals (Object o) {
		if (!(o instanceof Pitch))
			return false;
		Pitch p = (Pitch)o;	
		boolean res = (base == p.base);
		if (accidental != null)
			if (p.accidental != null)
				res &= accidental.equals(p.accidental);
			else
				return false;			 
		return res;
	}

	@Override
	public void registerComponent(Class[] classes) {
		super.registerComponent(classes);
		/*
		if (note != null) {
			setToolTipText(note + "");
		}
		*/
	}

	@Override
	int arrange(int pass) {
		int max = 2;
		if (pass == 0) {
			if (head != null) 
				head.unShift();
			setYPos(staff().hsToPixel(line()));
			if (accidental != null && accidental.isVisible()) {
				accidental.setShiftAmount(localSim().getAccidentalColumn(line()) * 
							 			  -head.getShiftAmount());
			}
		}
		
   		if (accidental != null)
	     	max = Math.max(max, accidental.arrange(pass));
		if (head != null)
		   max = Math.max(max, head.arrange(pass));

		if (pass == 2) {
			setLocation(absX() - lwidth(), absY() - height());
			setSize(lwidth() + rwidth(), depth() + height());
		}

		max = Math.max(max, super.arrange(pass));
		return max;
	}

	@Override
	public int compareTo(Object p) {
		int line1 = trebleLine();
		int line2 = ((Pitch) p).trebleLine();
		return (line1 < line2) ? -1 : (line1 > line2) ? 1 : 0;
	}

	/** Returns the accidental of this Pitch. */
	public Accidental getAccidental() {
		return accidental;
	}

	/** Returns the note head of this Pitch */
	public Head getHead() {
		return head;
	}

	/** Returns the half space unit where a treble clef would be placed. */
	public int trebleLine() {
		int offset = (base - 'a' + 5) % 7;
		int c1line = -6;
		return offset + c1line + 7 * octave;
	}

	/// @return line number of current pitch (0 = middle line)
	public int line() {
		//      int offset[7] = {5,6,0,1,2,3,4};
		int offset = (base - 'a' + 5) % 7;
		Measure m = measure();
		Clef c = m.activeClef();
		int c1line = c.c1Line();
		//int c1line = measure().activeClef().c1Line();
		return offset + c1line + 7 * (octave + c.getOctaveShift());
	}

   /** Draws the pitch onto the given graphics context. */
	@Override
	public void paint(Graphics g) {
		if(isVisible()){
			paintBackground(g);
			Color oldColor = g.getColor();
			g.setColor(getColor());
			if (head != null)
				head.paint(g);
			if (accidental != null)
				accidental.paint(g);
	
			g.setColor(oldColor);
			super.paint(g);
		}
	}
	
	@Override
	public int lwidth() {
		int lwidth = 0;
		if (head != null) {
			lwidth += head.lwidth();
		}
		else {
			lwidth += super.lwidth();
		}
		/* Add the width of a possible accidential to the left of the Pitch center */
		if (accidental != null &&
			accidental.isVisible()) {
			lwidth += accidental.lwidth() + Math.abs(accidental.getShiftAmount());
		}

		return lwidth + getLeftPadding();
	}
	
	@Override
	public int rwidth() {
		int w;
		if (head != null)
			w = head.rwidth();
		else
			w = super.rwidth();
		return w;
	}
	
	@Override
	public int height() {
		int height = 0;
		if (head != null) {
			height = head.height();
		}
		if (accidental != null &&
			accidental.isVisible() &&
			accidental.height() > height) {
			height = accidental.height();
		}
		
		if (height > 0)
			return height;
		else
			return super.height();
	}
	
	@Override
	public int depth() {
		int depth = 0;
		if (head != null) {
			depth = head.depth();
		}
		if (accidental != null &&
			accidental.isVisible() &&
			accidental.depth() > depth) {
			depth = accidental.depth();
		}
		
		if (depth > 0)
			return depth;
		else
			return super.depth();
	}
	
	
	@Override
	public Pitch pitch() {
		return this;
	}

   /** Returns a string representation of this pitch. */
	@Override
	public String toString() {
		return "Pitch: " + base + accidental + " o" + octave;
	}

	@Override
	Class parentClass() {
		return Chord.class;
	}

	/** Returns the half space where a potential augmentation dot. */
	protected int getDotLine() {
		int line = line();
		if (line % 2 != 0) // is pitch located between 2 staff lines
			return line; // dot(s) can be placed there as well
		if (localSim().numChildren() == 1)
			return line + 1;
		Chord c = (Chord) event();
		return line + (c.isStemUp() ? 1 : -1); //@@
	}
	
	/**
	 * 	 
	 * @return by much x-space a dot has to be shifted from its original position
	 */
	protected int getDotXOffset() {
		return head.getShifted();
	}

	/** Returns the octave of this pitch (1 = middle octave) */
	public byte getOctave() {
		return octave;
	}

	/** Returns the base (c diatonic) pitch letter. */
	public char getBase() {
		return base;
	}

	public final static byte NOT_AFFECTED_BY_PREVIOUS = 0;
	public final static byte AFFECTED_BY_PREVIOUS_SHOW = 1;
	public final static byte AFFECTED_BY_PREVIOUS_HIDE = 2;
   /** Returns true if this Pitch is affected by a previous accidental in the 
    * same measure. */
	byte affectedByPreviousAccidental() {
		byte value = NOT_AFFECTED_BY_PREVIOUS;
		
		Measure measure = measure();
		for (int i = 0; i < measure.numChildren(); i++) {
			LocalSim lsim = (LocalSim) measure.child(i);
			for (int j = 0; j < lsim.numChildren(); j++) {
				if (lsim.child(j) instanceof Chord) {
					Chord chord = (Chord) lsim.child(j);
					List pitches = new ArrayList();
					for (int k = 0; k < chord.numChildren(); k++) {
						pitches.add(chord.child(k));
					}
					if (chord.getEntryChord() != null) {
						for (int k = 0; k < chord.getEntryChord().numChildren(); k++) {
							pitches.add(chord.getEntryChord().child(k));
						}
					}
					
					for (int k = 0; k < pitches.size(); k++) {
						Pitch p = (Pitch)pitches.get(k);
						if (p == this) {
							return value;
						}
						//check for (e.g.) a g# followed by a g#
						if (p.base == base
							&& p.accidental.getType() == accidental.getType()
							&& p.octave == octave) {
							value = AFFECTED_BY_PREVIOUS_HIDE;
							continue;
						}
						//check for (e.g.) a g# followed by a g
						if (p.base == base
							&& p.accidental.getType() != Accidental.NATURAL
							&& accidental.getType() == Accidental.NATURAL) {
							value = AFFECTED_BY_PREVIOUS_SHOW;
							continue;
						}
						//check for (e.g.) a f# after an f in f-major 
						if (p.base == base
							&& p.accidental.getType() == Accidental.NATURAL
							&& accidental.getType() != Accidental.NATURAL) {
							value = AFFECTED_BY_PREVIOUS_SHOW;
							continue;
						}
					}
				}
			}
		}
		return NOT_AFFECTED_BY_PREVIOUS;
	}
	
	void shiftLeft() {
		head.shiftLeft();
	}
	
	void shiftRight() {
		head.shiftRight();
	}

	void unShift() {
		if (head != null)
			head.unShift();
	}
	
	@Override
	public void remove(Component comp) {
		super.remove(comp);
	}
	
	@Override
	public boolean contains(int x, int y) {
		Point loc = getLocation();
		Dimension dim = getSize();
		if (x >= loc.x && x <= loc.x + dim.width &&
			y >= loc.y && y <= loc.y + dim.height) {
			return true;
		}
		return false;
	}
}
