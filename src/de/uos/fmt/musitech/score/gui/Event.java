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
import java.util.List;

import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

/** This abstract class represents an time-spanning event that can 
 * occur in a measure of music. Currently only chords and rests are supported.
 * @author Martin Gieseking, Sascha Wegener, Tillman Weyde
 * @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $  */
public abstract class Event extends ScoreObject {
	private Duration duration;
	private int voice;
	private boolean inBeam = false;

	private List<Slur> slurs = new ArrayList<Slur>();
	
	private Accent accent = null;
	private boolean inTuplet = false;
	private Rational correctDuration;
	
	
	/**
	 * @param argDuration The duration of this event.
	 * @param argVoice The voice this event belongs to.
	 */
	public Event(Duration argDuration, int argVoice) {
		this.duration = argDuration;
		this.voice = argVoice;
	}

	/**
	 * returns the (grand-)parent of type parentClass of this object
	 */
	public Object getParent(Class parentClass) {
		ScoreObject parent = this;
		do {
			if (parent.getClass().equals(parentClass)) {
				return parent;
			}
			parent = parent.getScoreParent();
		} while (parent != null);
		
		return null;
	}
	
	public Duration getDuration() {
		return duration;
	}
   
   
	public int getVoice() {
		return voice;
	}

	/** Changes the beaming state of this Event.
	 * @param ib true, if this Event should be part of a beam. */
	void setInBeam(boolean ib) {
		inBeam = ib;
	}

	/** Returns true, if this Event is part of a beam group. */
	public boolean isInBeam() {
		return inBeam;
	}

	Class parentClass() {
		return LocalSim.class;
	}

	/** Line position that is used to compute the beam direction. */
	int beamDirectionLine() {
		return 0;
	}

	abstract Pair regressionPoint();
	abstract Pair beamPoint();
	abstract Pair highestPoint();
	abstract Pair lowestPoint();

	abstract Pair slurPoint(boolean left, boolean above, boolean atStem);

	public int numberOfFlags() {
		int denom = duration.getBase().getDenom();
		if (denom <= 4)
			return 0;
		return MyMath.ilog2(denom) - 2;
	}

	/**
	 * 	
	 * @return the factor used in the optimalSpace() method
	 */
	double optimalSpaceFactor() {
		double wmin = 1.0 / 32;
		double factor =
			Math.pow(1.4, Math.log(getDuration().toRational().toDouble() / wmin) / Math.log(2));
		
		return factor;	
	}

	int optimalSpace() {
		Staff staff = staff();
		if (staff == null)
			return 0;
		return (int) Math.round(
			optimalSpaceFactor() * MusicGlyph.width(staff.getLineDistance(), MusicGlyph.HEAD_BLACK));
	}

	public Event event() {
		return this;
	}

	/** Returns the accent assigned to this Event (null if none). */
	public Accent getAccent() {
		return accent;
	}

	int arrange(int pass) {
		if (accent != null)//if (pass == 0 && accent != null)
			accent.arrange(pass);
		return 0;
	}

	public void paint(Graphics g) {
		if(!isVisible()) return;
		if (accent != null)
			accent.paint(g);
	}

	/** Assigns an accent to this event. */
	public void setAccent(Accent accent) {
		this.accent = accent;
		accent.setParent(this);
	}

	void movePropertiesTo(Event ev) {
		super.movePropertiesTo(ev);
		ev.accent = accent;
		ev.duration = duration;
		ev.inBeam = inBeam;
		ev.voice = voice;
	}
	
	public boolean isInTuplet() {
		return inTuplet;
	}
	public void setInTuplet(boolean inTuplet) {
		this.inTuplet = inTuplet;
	}
	
	public Rational getCorrectDuration() {
		return correctDuration;
	}
	public void setCorrectDuration(Rational correctDuration) {
		this.correctDuration = correctDuration;
	}
	
	public List getSlurs() {
		return slurs;
	}
	
	public void setSlurs(List slurs) {
		this.slurs = slurs;
	}
	
	public void addSlur(Slur slur) {
		if (!slurs.contains(slur)) {
			slurs.add(slur);
		}
	}
}
