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

/** This class represents a single accidental that is part of a Pitch object.
 * @see Pitch
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
class Accidental extends ScoreObject {
	public static final byte NATURAL = 0;
	public static final byte SHARP = 1;
	public static final byte DOUBLESHARP = 2;
	public static final byte FLAT = -1;
	public static final byte DOUBLEFLAT = -2;
	
	/** type of accidental: 
	 * 0 = natural, 
	 * 1 = sharp, 2 = double sharp etc.
	 * -1 = flat, -2 = double flat etc. */
	private byte type;

	private int shiftAmount = 0; //we have to shift by that much
	
	/** Creates an Accidental of given type.
	 * @param type type of accidental (-2: double flat, -1: flat, 
	 * 0: natural, 1: sharp, 2: double sharp */
	public Accidental(byte type) {
		if (type < -2 || type > 2)
			throw new IllegalArgumentException("\nAccidental type " + type + " not supported");
		this.type = type;
	}

	/** Creates an Accidental of given type for an existing Pitch.
	 * @param pitch pitch that owns this accidental
	 * @param type  type of accidental */
	public Accidental(Pitch pitch, byte type) {
		super(pitch);
		if (type < -2 || type > 2)
			throw new IllegalArgumentException("\nAccidental type " + type + " not supported");
		this.type = type;
	}

	public boolean equals (Object o) {
		if (!(o instanceof Accidental)) 
			return false;
		Accidental a = (Accidental)o;
		return type == a.type;
	}

	/** Returns the type of this Accidental. */
	public byte getType() {
		return type;
	}

	/** Returns the related character out of the music font. */
	protected char getGlyph() {
		char[] glyphs =
			{
				MusicGlyph.DOUBLEFLAT,
				MusicGlyph.FLAT,
				MusicGlyph.NATURAL,
				MusicGlyph.SHARP,
				MusicGlyph.DOUBLESHARP };
		return glyphs[type + 2];
	}

	public void paint(Graphics g) {
		if (isVisible()) {
			setScaledFont(g);
			g.drawString("" + getGlyph(), absX() - lwidth() + shiftAmount, absY());
			restoreFont(g);
		}
	}
	
	public void setShiftAmount(int amount) {
		shiftAmount = amount;
	}
	
	public int getShiftAmount() {
		return shiftAmount;
	}

	Class parentClass() {
		return Pitch.class;
	}

	public String toString() {
		String res = "";
		for (int i = Math.abs(type); i > 0; i--)
			res += type > 0 ? "#" : "b";
		return res;
	}

	public int lwidth() {
		int ld = staff().getLineDistance();
		int gap = ld / 3; // gap between accidental and head
		return MusicGlyph.width(ld, getGlyph()) + gap;
	}

	public int depth() {
		return MusicGlyph.depth(staff().getLineDistance(), getGlyph());
	}
	
	public int height() {
		return MusicGlyph.height(staff().getLineDistance(), getGlyph());
	}

	void computeVisibility() {
		KeySignature keysig = measure().activeKeySignature();
		if (keysig == null)
			keysig = new KeySignature(0);
		boolean inScale = keysig.isInScale(pitch());
		byte affectedByPrevious = pitch().affectedByPreviousAccidental();
		if (inScale && affectedByPrevious != Pitch.AFFECTED_BY_PREVIOUS_SHOW)
			setVisible(false);
		else
			setVisible(true);
	}

	int arrange(int pass) {
		if (pass == 0) {
			computeVisibility();
			
		}
		else if (pass == 1) {
			setLocation(absX() - lwidth() + shiftAmount, absY() - height());
			setSize(lwidth(), depth() + height());
		}
		return 2;
	}
}