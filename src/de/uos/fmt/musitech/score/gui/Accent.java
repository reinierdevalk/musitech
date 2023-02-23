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

/**
 * This class represents a musical accent.
 * 
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2008-02-18 10:00:16 +0100 (Mo, 18 Feb
 *          2008) $
 */
public class Accent extends ScoreObject {

	private byte type;
	private int ypos;

	// currently supported accent types
	public final static byte EMPTY = 0;
	public final static byte STACCATO = 1;
	public final static byte MARCATO = 2;
	public final static byte PORTATO = 3;
	public final static byte TRILL = 4;
	public final static byte MORDENT_UP = 5;
	public final static byte MORDENT_DOWN = 6;
	public final static byte TURN = 7;

	public final static byte SFORZATO = 8;
	public final static byte FORZANDO = 9;

	public final static byte DOWN_BOW = 10;
	public final static byte UP_BOW = 11;

	public final static byte FERMATA = 12;

	public final static byte MARTELLATO = 13;

	/** constructs a new Accent of given type */
	public Accent(byte type) {
		this.type = type;
	}

	@Override
	Class parentClass() {
		return Chord.class;
	}
	
	public char getGlyph() {
		return getGlyph(type);
	}

	public static char getGlyph(int type) {
		if (type == STACCATO)
			return MusicGlyph.STACCATO;
		if (type == MARCATO)
			return MusicGlyph.MARCATO;
		if (type == PORTATO)
			return MusicGlyph.PORTATO;
		if (type == TRILL)
			return MusicGlyph.TRILL;
		if (type == MORDENT_UP)
			return MusicGlyph.MORDENT_UP;
		if (type == MORDENT_DOWN)
			return MusicGlyph.MORDENT_DOWN;
		if (type == TURN)
			return MusicGlyph.TURN;
		if (type == SFORZATO)
			return MusicGlyph.SFORZATO;
		if (type == FORZANDO)
			return MusicGlyph.FORZANDO;
		if (type == DOWN_BOW)
			return MusicGlyph.DOWN_BOW;
		if (type == UP_BOW)
			return MusicGlyph.UP_BOW;
		if (type == FERMATA)
			return MusicGlyph.FERMATA_UP;
		if (type == MARTELLATO)
			return MusicGlyph.MARTELLATO;
		return 0;
	}

	@Override
	int arrange(int pass) {
		Event ev = event();
		int width = 0;
		if (ev instanceof Rest)
			width = ev.rwidth();
		else if (ev instanceof Chord) {
			Chord c = (Chord) ev;
			width = ((Pitch) c.child(0)).getHead().rwidth();
			if (getType() != UP_BOW && getType() != DOWN_BOW
				&& getType() != FERMATA) {
				if (measure().numVoices() == 1) {
					if (c.stemUp) {
						Head h = c.lowestPitch().getHead();
						ypos = h.absY() + staff().getLineDistance() + h.depth()
								+ h.height();
					} else {
						Head h = c.highestPitch().getHead();
						ypos = h.absY() - h.height()
								- staff().getLineDistance();
					}
				} else {
					if (!c.stemUp) {
						Head h = c.lowestPitch().getHead();
						ypos = staff().absY();
					} else {
						Head h = c.highestPitch().getHead();
						ypos = staff().absY() + staff().naturalHeight()
								+ (int) (staff().getLineDistance() * 1.5);
					}
				}
			} else {
				if (c.stemUp) {
					Head h = c.highestPitch().getHead();
					ypos = h.absY() - c.stemLength
							- (int) (staff().getLineDistance() * 0.5);
				} else {
					Head h = c.highestPitch().getHead();
					ypos = h.absY() - h.height() - staff().getLineDistance();
				}
			}
		}
		// setYPos(-staff().getLineDistance());
		// setXPos(width);

		return 0;
	}

	@Override
	public int absY() {
		return ypos;
	}

	/** Draws the this Accent onto the given graphics context. */
	@Override
	public void paint(Graphics g) {
		if (!isVisible() && type > 0)
			return;
		int nw = event().rwidth();
		int x = absX() + (nw - rwidth()) / 2;
		g.drawString("" + getGlyph(), x, absY());
	}

	/** Returns the accent type of this Accent */
	public byte getType() {
		return type;
	}

	@Override
	public int rwidth() {
		return MusicGlyph.width(getScoreParent().staff().getLineDistance(),
			getGlyph());
	}

	public static String toString(byte acc) {
		if (acc == STACCATO)
			return "staccato";
		if (acc == MARCATO)
			return "marcato";
		if (acc == PORTATO)
			return "portato";
		if (acc == TRILL)
			return "trill";
		if (acc == MORDENT_UP)
			return "mordent up";
		if (acc == MORDENT_DOWN)
			return "mordent down";
		if (acc == TURN)
			return "turn";
		if (acc == DOWN_BOW)
			return "down bow";
		if (acc == UP_BOW)
			return "up bow";
		if (acc == FERMATA)
			return "fermata";
		if (acc == MARTELLATO)
			return "martellato";
		return "empty";
	}

	public static byte toAccent(String s) {
		if (s.equals("staccato"))
			return STACCATO;
		if (s.equals("marcato"))
			return MARCATO;
		if (s.equals("portato"))
			return PORTATO;
		if (s.equals("trill"))
			return TRILL;
		if (s.equals("mordent up"))
			return MORDENT_UP;
		if (s.equals("mordent down"))
			return MORDENT_DOWN;
		if (s.equals("turn"))
			return TURN;
		if (s.equals("down bow"))
			return DOWN_BOW;
		if (s.equals("up bow"))
			return UP_BOW;
		if (s.equals("fermata"))
			return FERMATA;
		if (s.equals("martellato"))
			return MARTELLATO;
		return EMPTY;
	}

}
