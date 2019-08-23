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

import de.uos.fmt.musitech.score.fmx.FontMetrics;

/** This class provides static methods and constants to handle the 
 * music symbols. A successful instantiation requires
 * 2 additional files: 
 * a) the font file that should be installed correctly on the system
 * b) the font metrics file containing metrical information about the glyphs
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $  */
public class MusicGlyph {
	// clefs
	static final public char CLEF_G = 'A';
	static final public char CLEF_G_SMALL = 'B';
	static final public char CLEF_F = 'E';
	static final public char CLEF_F_SMALL = 'F';
	static final public char CLEF_C = 'C';
	static final public char CLEF_C_SMALL = 'D';
	static final public char CLEF_PERC = 'G';

	// rests
	static final public char REST_1 = 'I';
	static final public char REST_2 = 'J';
	static final public char REST_4 = 'K';
	static final public char REST_8 = 'L';
	static final public char REST_16 = 'M';
	static final public char REST_32 = 'N';
	static final public char REST_64 = 'O';

	// note heads
	static final public char HEAD_BLACK = 229; // head for notes with value less 1/2
	static final public char HEAD_WHITE = 228;
	static final public char HEAD_WHOLE = 227; // whole note head

	// flags
	static final public char FLAG_8_UP = 230; // 8th note flag pointing up
	static final public char FLAG_8_DOWN = 234;
	static final public char FLAG_16_UP = 231; // 16th note flag pointing up
	static final public char FLAG_16_DOWN = 235;
	static final public char FLAG_32_UP = 232; // 32th note flag
	static final public char FLAG_32_DOWN = 236;
	static final public char FLAG_64_UP = 233; // 64th note flag
	static final public char FLAG_64_DOWN = 237;

	// accidentals   
	static final public char DOUBLEFLAT = 'P';
	static final public char FLAT = 'Q';
	static final public char NATURAL = 'R';
	static final public char SHARP = 'S';
	static final public char DOUBLESHARP = 'T';

	// time signature elements
	static final public char ALLABREVE = ';'; // C with vertical line
	static final public char ALLASEMIBREVE = ':'; // C (4/4 replacement)   
	static final public char TS_0 = '0'; // time signature digit 0 (digits 1..9: '1'..'9')

	// accents
	static final char STACCATO = 46;
	static final char PORTATO = 200;
	static final char MARCATO = 202;
	static final char MORDENT_UP = 108;
	static final char MORDENT_DOWN = 120;
	static final char TURN = 119;
	static final char TRILL = 116;
	static final char FERMATA_UP = 117;
	static final char FERMATA_DOWN = 107;
	static final char MARTELLATO = 201;

	static final char FORTE = 102;
	static final char FORTE2 = 103;
	static final char FORTE3 = 104;
	static final char MEZZOFORTE = 0x6A;
	static final char MOZZOPIANO = 0x69;
	static final char PIANO = 112;
	static final char PIANO2 = 113;
	static final char PIANO3 = 114;

	static final char SFORZATO = 115;
	static final char FORZANDO = 115;
	
	static final char DOWN_BOW = 89;
	static final char UP_BOW = 90;

	
	// tuplet numbers 
	static final char TUPLET_ZERO = 246; // digits 1--9: values 247--255

	private final static FontMetrics metrics =
		new FontMetrics(FontMetrics.class.getResource("gin.fmx"));

	/** Returns the height of a given character. 
	 * @param  lineDistance distance between 2 staff lines
	* @param  c the character to be measured 
	* @return height of character c in pixel units */
	public static int height(int lineDistance, char c) {
		return 4 * lineDistance * metrics.getHeight(c) / metrics.getFontSize();
	}

	/** Returns the depth of a given character. 
	 * @param  lineDistance distance between 2 staff lines
	 * @param  c the character to be measured 
	 * @return depth of character c in pixel units */
	public static int depth(int lineDistance, char c) {
		return 4 * lineDistance * metrics.getDepth(c) / metrics.getFontSize();
	}

	/** Returns the width of a given character. 
	 * @param  lineDistance distance between 2 staff lines
	 * @param  c the character to be measured 
	 * @return width of character c in pixel units */
	public static int width(int lineDistance, char c) {
		return 4 * lineDistance * metrics.getWidth(c) / metrics.getFontSize();
	}

	/** Returns the width of a given array of single music symbols. 
	 * No inter-character space is regarded. 
	 * @param lineDistance distance between 2 staff lines
	 * @param glyphs glyphs to be measured
	 * @return with of characters in given String */
	public static int width(int lineDistance, char[] glyphs) {
		int width = 0;
		for (int i = 0; i < glyphs.length; i++)
			width += width(lineDistance, glyphs[i]);
		return width;
	}

	/** Creates a new music font so that the symbols fit on a staff with
	 *  the given line distants (in pixel units). */
	public static Font createFont(int lineDistance) {
		return metrics.createFont(4 * lineDistance);
	}
}