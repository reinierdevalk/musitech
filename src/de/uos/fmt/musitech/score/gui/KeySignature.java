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
 * @author Martin Gieseking
 * @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $
 */
public class KeySignature extends ScoreObject {
	private int type;         // positive values denote sharps, negative flats (valid range: -7..7)
	private int[] positions;

//	private int[] flatBorders  = { 3, 4, 3, 3, 2, 2, 1 };
//	private int[] sharpBorders = { 4, 2, 5, 3, 3, 4, 4 };
	private int[] flatBorders  = { 3, 4, 3, 3, 2, 2, 1, 3, 3, 3, 3 };
	private int[] sharpBorders = { 4, 2, 5, 3, 3, 4, 4, 4, 4, 4, 4 };

	
	public KeySignature(int type) {
		if (Math.abs(type) > 7)
			throw new IllegalArgumentException("the KeysignatureType must be between -7 and 7");
		this.type = type;
		positions = new int[Math.abs(type)];
	}
   
   /** Constructs a KeySignature based on a given key (e.g. c sharp major). 
    *  @param base  base pitch of key
    *  @param accid accidental of key
    *  @param mode  "major" or "minor" (more to come) */
   public KeySignature (char base, int accid, String mode) {
      if (Math.abs(accid) > 1)
         throw new IllegalArgumentException("KeySignature: invalid accidental value");     
         
      if (mode.equalsIgnoreCase("major")) {      
         int types[][] = {
            {-4, -2, -7, -5, -3, 10, -6},  // flat (e.g. e flat major)
            { 3,  5,  0,  2,  4, -1,  1},  // no accidental (e.g. g major) 
            {10, 10,  7, 10, 10,  6, 10}   // sharp (e.g. f sharp major)
         };
         type = types[accid+1][base-'a'];
         if (type == 10)
            throw new IllegalArgumentException(base + " "+mode+" is not valid");
      }
      else if (mode.equalsIgnoreCase("minor")) {
         int types[][] = {
            {-7, -5, 10, 10, -6, 10, 10},
            { 0,  2, -3, -1,  1, -4, -2},
            { 7, 10,  4,  6, 10,  3,  5}
         };
         type = types[accid+1][base-'a'];
         if (type == 10)
            throw new IllegalArgumentException(base + " "+mode+" is not valid");
      }
      else
         throw new IllegalArgumentException("KeySignature: mode "+mode+" unknown");
      positions = new int[Math.abs(type)];
   }

	int arrange(int pass) {
		if (pass == 0) {
			int[] borders = type < 0 ? flatBorders : sharpBorders;
			int affectedPitch = type < 0 ? 6 : 3; // 0=c, 1=d, 2=e,...,6=b
			int max = Math.abs(type);
			int cLine = measure().getClef().c1Line() + 14;
			// calculate vertical positions of all accidentals
			for (int i = 0; i < max; i++) {
				positions[i] = cLine + affectedPitch;
				while (positions[i] > borders[i])
					positions[i] -= 7;
				affectedPitch = (affectedPitch + (type < 0 ? 3 : 4)) % 7;
			}
		}
		if (pass == 2) {
			int maxY = 0;
			int minY = Integer.MAX_VALUE;
			char glyph = type < 0 ? MusicGlyph.FLAT : MusicGlyph.SHARP;
			int height = MusicGlyph.height(staff().getLineDistance(), glyph);
			int depth = MusicGlyph.depth(staff().getLineDistance(), glyph);
			for (int i = 0; i < positions.length; i++) {
				maxY = Math.max(maxY, absY() + staff().hsToPixel(positions[i]) + height);
				minY = Math.min(minY, absY() + staff().hsToPixel(positions[i]) - depth);
			}
			setLocation(absX(), minY);
			setSize(rwidth() + lwidth(), maxY - minY);
		}
		
		return 3;
	}

	Class parentClass() {
		return Measure.class;
	}

	/** Returns the space between two successive accidentals in pixel units. */
	protected int interGlyphGap() {
		return staff().getLineDistance() / 4;
	}

	static int counter = 0;
	public void paint(Graphics g) {
		if(!isVisible()) return;
		char glyph = type < 0 ? MusicGlyph.FLAT : MusicGlyph.SHARP;
		int offset = 0;
		
		setScaledFont(g);
		for (int i = 0; i < positions.length; i++) {
			g.drawString("" + glyph, absX() + offset, absY() + staff().hsToPixel(positions[i]));
			offset += (int)((MusicGlyph.width(staff().getLineDistance(), glyph) + interGlyphGap()) * scale);
		}
		restoreFont(g);
	}

	public int rwidth() {
		char glyph = type < 0 ? MusicGlyph.FLAT : MusicGlyph.SHARP;
		int num = Math.abs(type);
		int width = num * MusicGlyph.width(staff().getLineDistance(), glyph);
		return width + (num - 1) * interGlyphGap();
	}

	public int optimalSpace() {
		int space = rwidth();
		Staff staff = staff();
		if (staff != null)
			space += staff.getLineDistance();
		return space;
	}

	/** Returns true if the given pitch is a member of a scale with this KeySignature. */
	public boolean isInScale(Pitch pitch) {
		//             a  b  c  d  e  f  g  h
		int[] flat = { 3, 1, 6, 4, 2, 7, 5, 1 };
		int[] sharp = { 5, 7, 2, 4, 6, 1, 3, 7 };
		int index = pitch.getBase() - 'a';
		int accid = pitch.getAccidental().getType();
		if (type < 0) // key with flats
			return (accid < 0 && flat[index] <= -type) || (accid == 0 && flat[index] > -type);
		if (type > 0) // key with sharps
			return (accid > 0 && sharp[index] <= type) || (accid == 0 && sharp[index] > type);
		return accid == 0;
	}
}
