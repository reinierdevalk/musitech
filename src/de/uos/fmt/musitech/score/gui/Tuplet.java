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

import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a tuplet, i.e. an event sequence with irregular divided
 *  durations.
 *  @author Martin Gieseking
 *  @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $ */
public class Tuplet extends EventSpanner {
	private int size; // 3=triplet, 4=quadruplet, 5=quintuplet etc.
	private Rational duration; // duration of whole tuplet group   
	private boolean needsBracket; // true if a square bracket must be drawn
	private Beam beam; //the Beam accompanying this Tuplet
	private Slur slur; //the slur accompanying this Tuplet (for 1/4 or slower)
	
	/** Constructs a new tuplet.
	 * @param size     size of tuplet group (3 = triplet, 4 = quadruplet, etc.)
	 * @param duration duration of whole tuplet group */
	public Tuplet(int size, Rational duration) {
		if (size < 2 || size > 9)
			throw new IllegalArgumentException("tuplet size " + size + " not supported");
		this.size = size;
		this.duration = duration;
		needsBracket = false;
	}
	
	public boolean containsRest() {
		for (int i = 0; i < numEvents(); i++) {
			if (getEvent(i) instanceof Rest)
				return true;
		}
		return false;
	}
	
	public void setBeam(Beam beam) {
		this.beam = beam;
	}

	public void setSlur(Slur slur) {
		this.slur = slur;
	}

	public boolean isLast(Event ev) {
		return indexOf(ev) == numEvents() - 1;
	}
	
	/** Adds an event to this tuplet group. */
	@Override
	public void add(Event ev) {
		super.add(ev);
		needsBracket |= ev.getDuration().toRational().isGreaterOrEqual(1, 4); // @@ auf Balken pr?fen
		ev.setInTuplet(true);
	}

	/** Returns the duration of the whole tuplet. */
	public Rational getDuration() {
		return duration;
	}

	/** Returns the size of this tuplet (3 = triplet, 4 = quadruplet etc.) */
	public int getTupletSize() {
		return size;
	}

	@Override
	int arrange(int pass) {
		super.arrange(pass);

		return 1;
	}

	@Override
	public int height() {
		char glyph = (char) (MusicGlyph.TUPLET_ZERO + size);
		int ld = getEvent(0).staff().getLineDistance();
		return MusicGlyph.height(ld, glyph) / 2;
	}

	Pair calculateCoordinates() {
		char glyph = (char) (MusicGlyph.TUPLET_ZERO + size);
		int y = 0;
		int x = getEvent(numEvents() / 2).beamPoint().getRoundedX();
		int ld = getEvent(0).staff().getLineDistance();
		x -= MusicGlyph.width(ld, glyph) / 2;
	
		int space = ld;
		if (beam != null) {
			int beamY = getEvent(numEvents() / 2).beamPoint().getRoundedY();
			if (beam.computeDirection() == 1) {
				y = beamY - space;
			}
			else {
				y = beamY + space;
			}
		}
		else if (slur != null) {
			int slurY = slur.extremum().getRoundedY();//slur.computeHeight();
			if (slur.computeDirection() == 1) {
				y = slurY - space;
			}
			else {
				y = slurY + space;
			}
		}	 
		return new Pair(x, y);
	}
	
	/** Draws all additional elements of this tuplet (numer, bracket) onto the
	 * given graphics device. */
	@Override
	public void paint(Graphics g) {
		if(!isVisible()) return;
		/*
		int x = getEvent(0).absX() + (getEvent(-1).absX() - getEvent(0).absX()) / 2;
		
		*/
		char glyph = (char) (MusicGlyph.TUPLET_ZERO + size);
		Pair p = calculateCoordinates();
		
		Color oldColor = g.getColor();
		g.setColor(getColor());
		setScaledFont(g);
		g.drawString("" + glyph, p.getRoundedX(), p.getRoundedY());
		restoreFont(g);
		g.setColor(oldColor);
	}

	/** Returns the position of the tuplet markings (bracket and/or size number) 
	 * @return -1 if marking is placed below the noteheads, 1 otherwise */
	protected int direction() {
		return 1; // @@
	}

	/** Returns the slope of the square bracket drawn over or below the notes 
	 * belonging to this tuplet. */
	protected double computeSlope() // see also Beam.computeSlope
	{
		if (numEvents() == 0)
			return 0;
		double xx = 0, yy = 0;
		int count = 0;
		for (int i = 0; i < numEvents(); i++) {
			Event ev = getEvent(i);
			if (ev instanceof Chord) {
				Pair p = ev.regressionPoint();
				xx += p.getX();
				yy += p.getY();
				count++;
			}
		}
		if (count == 0)
			return 0;
		xx /= count;
		yy /= count;
		double numer = 0, denom = 1; // numerator and denominator of solution
		for (int i = 0; i < numEvents(); i++) {
			Event ev = getEvent(i);
			if (ev instanceof Chord) {
				Pair p = ev.regressionPoint();
				double dx = p.getX() - xx;
				numer += dx * (p.getY() - yy);
				denom += dx * dx;
			}
		}
		return 0.6 * MyMath.tanh(numer / denom); // damped regression slope
	}

	/** Returns the parent class of this Tuplet (always Staff.class) */
	@Override
	Class parentClass() {
		return Staff.class;
	}
	
	@Override
	public void setColor(Color c) {
		super.setColor(c);
		if (beam != null)
			beam.setColor(c);
	}
}
