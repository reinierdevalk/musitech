// $ANTLR : "Kern.g" -> "KernLexer.java"$

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
package de.uos.fmt.musitech.score.kern;

import de.uos.fmt.musitech.data.performance.*;
import de.uos.fmt.musitech.data.structure.*;
import de.uos.fmt.musitech.data.structure.container.*;
import de.uos.fmt.musitech.data.structure.harmony.*;
import de.uos.fmt.musitech.data.structure.linear.*;
import de.uos.fmt.musitech.data.score.*;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.time.*;
import de.uos.fmt.musitech.utility.math.*;

import java.util.List;
import java.util.ArrayList;

public interface KernParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int KERNTAG = 4;
	int DYNAMTAG = 5;
	int LYRICSTAG = 6;
	int INSTRUMENT_BEGIN = 7;
	int STAFF_BEGIN = 8;
	int CLEF_BEGIN = 9;
	int STAR = 10;
	int SLASH = 11;
	int COLON = 12;
	int KEYSIG_BEGIN = 13;
	int SQUARBRACE_END = 14;
	int METERSIGMM_BEGIN = 15;
	int UNKNOWN3 = 16;
	int CANCEL = 17;
	int UNKNOWN2 = 18;
	int UNKNOWN1 = 19;
	int REST = 20;
	int TRANSPOS = 21;
	int FLAT = 22;
	int DIGITS = 23;
	int CHARACTERS = 24;
	int BARLINE = 25;
	int REPEAT = 26;
	int DOT = 27;
	int DUMMY = 28;
	int STAFFEND = 29;
	int TAB = 30;
	int NL = 31;
	int BEAM_BEGIN = 32;
	int BEAM_END = 33;
	int PARTIALBEAM_BEGIN = 34;
	int PARTIALBEAM_END = 35;
	int TIE_BEGIN = 36;
	int TIE_MIDDLE = 37;
	int SLUR_BEGIN = 38;
	int SLUR_END = 39;
	int CURVEDBRACE_OPEN = 40;
	int CURVEDBRACE_CLOSE = 41;
	int DOTDOT = 42;
	int ORNAMENT = 43;
	int FERMATE = 44;
	int BACKSLASH = 45;
	int SHARP = 46;
	int COMMENT_BEGIN = 47;
	int UNSUPPORTED = 48;
}
