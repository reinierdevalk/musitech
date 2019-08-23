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
package de.uos.fmt.musitech.score.mpegsmr;

/**
 * @author Sascha Wegener
 * 
 * @version 11.03.2008
 */
public interface Attributes {
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final String SCORE_ID = "ID";
	public static final String SCORE_NUMBEROFSTAFFS = "NUMBEROFSTAFFS";
	public static final String SCORE_TYPE = "TYPE";
	public static final String SCORE_TYPE_NORMAL = "NORMAL";
	public static final String SCORE_INSTRUMENT = "INSTRUMENT";
	
	public static final String LAYER_NUMBER = "NUMBER";
	
	public static final String DURATION_D4M = "D4M";
	public static final String DURATION_D2M = "D2M";
	public static final String DURATION_D2 = "D2";
	public static final String DURATION_D1 = "D1";
	public static final String DURATION_D1_2 = "D1_2";
	public static final String DURATION_D1_4 = "D1_4";
	public static final String DURATION_D1_8 = "D1_8";
	public static final String DURATION_D1_16 = "D1_16";
	public static final String DURATION_D1_32 = "D1_32";
	public static final String DURATION_D1_64 = "D1_64";
	public static final String DURATION_D1_128 = "D1_128";
	public static final String DURATION_DGENERIC = "DGENERIC";
	
	public static final String DIRECTION_UP = "UP";
	public static final String DIRECTION_DOWN = "DOWN";
	public static final String DIRECTION_AUTO = "AUTO";
	
	public static final String CLEF_TYPE = "TYPE";
	public static final String CLEF_TYPE_BARITONE = "BARITONE";
	public static final String CLEF_TYPE_BASS = "BASS";
	public static final String CLEF_TYPE_BASSOLD = "BASSOLD";
	public static final String CLEF_TYPE_ALTO = "ALTO";
	public static final String CLEF_TYPE_MEZZOSOPRANO = "MEZZOSOPRANO";
	public static final String CLEF_TYPE_SOPRANO = "SOPRANO";
	public static final String CLEF_TYPE_TENOR = "TENOR";
	public static final String CLEF_TYPE_TENOR8 = "TENOR8";
	public static final String CLEF_TYPE_TREBLE = "TREBLE";
	public static final String CLEF_TYPE_TREBLE8 = "TREBLE8";
	public static final String CLEF_TYPE_8TREBLE = "8TREBLE";
	public static final String CLEF_TYPE_BASS8 = "BASS8";
	public static final String CLEF_TYPE_8BASS = "8BASS";
	public static final String CLEF_TYPE_EMPTY = "EMPTY";
	public static final String CLEF_TYPE_PERCUSBOX = "PERCUSBOX";
	public static final String CLEF_TYPE_PERCUS2LINES = "PERCUS2LINES";
	public static final String CLEF_TYPE_TAB = "TAB";
	
	public static final String KEYSIGNATURE_TYPE = "TYPE";
	public static final String KEYSIGNATURE_TYPE_DOdM = "DOdM";
	public static final String KEYSIGNATURE_TYPE_FAdM = "FAdM";
	public static final String KEYSIGNATURE_TYPE_SIM = "SIM";
	public static final String KEYSIGNATURE_TYPE_MIM = "MIM";
	public static final String KEYSIGNATURE_TYPE_LAM = "LAM";
	public static final String KEYSIGNATURE_TYPE_REM = "REM";
	public static final String KEYSIGNATURE_TYPE_SOLM = "SOLM";
	public static final String KEYSIGNATURE_TYPE_DOM = "DOM";
	public static final String KEYSIGNATURE_TYPE_FAM = "FAM";
	public static final String KEYSIGNATURE_TYPE_SIbM = "SIbM";
	public static final String KEYSIGNATURE_TYPE_MIbM = "MIbM";
	public static final String KEYSIGNATURE_TYPE_LAbM = "LAbM";
	public static final String KEYSIGNATURE_TYPE_REbM = "REbM";
	public static final String KEYSIGNATURE_TYPE_SOLbM = "SOLbM";
	public static final String KEYSIGNATURE_TYPE_DObM = "DObM";
	public static final String KEYSIGNATURE_TYPE_LAdm = "LAdm";
	public static final String KEYSIGNATURE_TYPE_REdm = "REdm";
	public static final String KEYSIGNATURE_TYPE_SOLdm = "SOLdm";
	public static final String KEYSIGNATURE_TYPE_DOdm = "DOdm";
	public static final String KEYSIGNATURE_TYPE_FAdm = "FAdm";
	public static final String KEYSIGNATURE_TYPE_SIm = "SIm";
	public static final String KEYSIGNATURE_TYPE_MIm = "MIm";
	public static final String KEYSIGNATURE_TYPE_LAm = "LAm";
	public static final String KEYSIGNATURE_TYPE_REm = "REm";
	public static final String KEYSIGNATURE_TYPE_SOLm = "SOLm";
	public static final String KEYSIGNATURE_TYPE_DOm = "DOm";
	public static final String KEYSIGNATURE_TYPE_FAm = "FAm";
	public static final String KEYSIGNATURE_TYPE_SIbm = "SIbm";
	public static final String KEYSIGNATURE_TYPE_MIbm = "MIbm";
	public static final String KEYSIGNATURE_TYPE_LAbm = "LAbm";
	
	public static final String TIMESIGNATURE_TYPE = "TYPE";
	public static final String TIMESIGNATURE_TYPE_C = "C";
	public static final String TIMESIGNATURE_TYPE_CSLASH = "CSLASH";
	public static final String TIMESIGNATURE_TYPE_FRACTION = "FRACTION";
	public static final String TIMESIGNATURE_NUMERATOR = "NUMERATOR";
	public static final String TIMESIGNATURE_DENOMINATOR = "DENOMINATOR";
	public static final String TIMESIGNATURE_TIMENUMERATOR = "TIMENUMERATOR";
	public static final String TIMESIGNATURE_TIMEDENOMINATOR = "TIMEDENOMINATOR";
	public static final String TIMESIGNATURE_STATUS = "STATUS";
	public static final String TIMESIGNATURE_STATUS_NORMAL = "NORMAL";
	public static final String TIMESIGNATURE_STATUS_HIDDEN = "HIDDEN";
	public static final String TIMESIGNATURE_STATUS_ALWAYSVISIBLE = "ALWAYSVISIBLE";
	
	public static final String BARLINE_TYPE = "TYPE";
	public static final String BARLINE_TYPE_SINGLE = "SINGLE";
	public static final String BARLINE_TYPE_DOUBLE = "DOUBLE";
	public static final String BARLINE_TYPE_START_REFRAIN = "START_REFRAIN";
	public static final String BARLINE_TYPE_END_REFRAIN = "END_REFRAIN";
	public static final String BARLINE_TYPE_START_END_REFRAIN = "START_END_REFRAIN";
	public static final String BARLINE_TYPE_END = "END";
	public static final String BARLINE_TYPE_INVISIBLE = "INVISIBLE";
	public static final String BARLINE_LINETYPE = "LINETYPE";
	public static final String BARLINE_LINETYPE_SOLID = "SOLID";
	public static final String BARLINE_LINETYPE_DOT = "DOT";
	public static final String BARLINE_LINETYPE_DASH = "DASH";
	public static final String BARLINE_REPEAT = "REPEAT";
	public static final String BARLINE_FERMATA = "FERMATA";
	
	public static final String NOTE_ID = "ID";
	public static final String NOTE_DURATION ="DURATION";
	public static final String NOTE_STAFF = "STAFF";
	public static final String NOTE_HEIGHT = "HEIGHT";
	public static final String NOTE_STATUS = "STATUS";
	public static final String NOTE_STATUS_NORMAL = "NORMAL";
	public static final String NOTE_STATUS_GRACED = "GRACED";
	public static final String NOTE_STATUS_HIDDEN = "HIDDEN";
	public static final String NOTE_STATUS_GHOSTED = "GHOSTED";
	
	public static final String ACCIDENTAL_TYPE ="TYPE";
	public static final String ACCIDENTAL_TYPE_SHARP ="SHARP";
	public static final String ACCIDENTAL_TYPE_FLAT ="FLAT";
	public static final String ACCIDENTAL_TYPE_NATURAL ="NATURAL";
	public static final String ACCIDENTAL_TYPE_DSHARP ="DSHARP";
	public static final String ACCIDENTAL_TYPE_DFLAT ="DFLAT";
	public static final String ACCIDENTAL_TYPE_SHARP1Q ="SHARP1Q";
	public static final String ACCIDENTAL_TYPE_SHARP3Q ="SHARP3Q";
	public static final String ACCIDENTAL_TYPE_FLAT1Q ="FLAT1Q";
	public static final String ACCIDENTAL_TYPE_FLAT3Q ="FLAT3Q";
	public static final String ACCIDENTAL_TYPE_NATURALDOWN ="NATURALDOWN";
	public static final String ACCIDENTAL_TYPE_NATURALUP ="NATURALUP";
	
	public static final String AUGMENTATION_DOTS = "DOTS";
	
	public static final String FERMATA_TYPE ="TYPE";
	public static final String FERMATA_TYPE_SHORT ="SHORT";
	public static final String FERMATA_TYPE_MEDIUM ="MEDIUM";
	public static final String FERMATA_TYPE_LONG ="LONG";
	public static final String FERMATA_UPDOWN ="UPDOWN";
	
	public static final String ORNAMENT_TYPE = "TYPE";
	public static final String ORNAMENT_TYPE_TRILL = "TRILL";
	public static final String ORNAMENT_TYPE_TURN = "TURN";
	public static final String ORNAMENT_TYPE_TURNBACK = "TURNBACK";
	public static final String ORNAMENT_TYPE_TURNUP = "TURNUP";
	public static final String ORNAMENT_TYPE_TURNSLASH = "TURNSLASH";
	public static final String ORNAMENT_TYPE_MORDENT = "MORDENT";
	public static final String ORNAMENT_TYPE_MORDENTSUP = "MORDDENTSUP";
	public static final String ORNAMENT_TYPE_DMORDENT = "DMORDENT";
	public static final String ORNAMENT_TYPE_DMORDENTSUP = "DMORDENTSUP";
	public static final String ORNAMENT_TYPE_TREMOLO = "TREMOLO";
	public static final String ORNAMENT_TYPE_STEMTREMOLO = "STEMTREMOLO";
	public static final String ORNAMENT_TYPE_GLISSWAVE = "GLISSWAVE";
	public static final String ORNAMENT_TYPE_GLISSLINE = "GLISSLINE";
	public static final String ORNAMENT_NUMBER = "NUMBER";
	public static final String ORNAMENT_UPDOWN= "UPDOWN";
	
	public static final String MARKER_TYPE ="TYPE";
	public static final String MARKER_TYPE_PORTATO ="PORTATO";
	public static final String MARKER_TYPE_TENUTO ="TENUTO";
	public static final String MARKER_TYPE_SFORZATO ="SFORZATO";
	public static final String MARKER_TYPE_ACCENTOFORTE ="ACCENTOFORTE";
	public static final String MARKER_TYPE_ACCENTO ="ACCENTO";
	public static final String MARKER_TYPE_PUNTOSOPRA ="PUNTOSOPRA";
	public static final String MARKER_TYPE_STACCATO ="STACCATO";
	public static final String MARKER_TYPE_MARTDOLCE ="MARTDOLCE";
	public static final String MARKER_TYPE_PUNTOALLUNGATO ="PUNTOALLUNGATO";
	public static final String MARKER_TYPE_MARTELLATO ="MARTELLATO";
	public static final String MARKER_TYPE_GENERIC ="GENERIC";
	public static final String MARKER_TYPE_ARCO ="ARCO";
	public static final String MARKER_TYPE_PONTICELLO ="PONTICELLO";
	public static final String MARKER_TYPE_TASTIERA ="TASTIERA";
	public static final String MARKER_TYPE_PUNTA ="PUNTA";
	public static final String MARKER_TYPE_TALLONE ="TALLONE";
	public static final String MARKER_TYPE_BOWUP ="BOWUP";
	public static final String MARKER_TYPE_BOWDOWN ="BOWDOWN";
	
	public static final String DYNAMICTEXT_DYNAMIC = "DYNAMIC";
	public static final String DYNAMICTEXT_UPDOWN = "UPDOWN";
	
	public static final String ADDRESS_MEASURE ="MEASURE";
	public static final String ADDRESS_LAYER ="LAYER";
	public static final String ADDRESS_FIGURE ="FIGURE";
	public static final String ADDRESS_CHORD_OR_BEAM ="CHORD.OR.BEAM";
	public static final String ADDRESS_CHORD_IN_BEAM ="CHORD.IN.BEAM";
	
	public static final String HORIZONTAL_ID = "ID";
	public static final String HORIZONTAL_TYPE = "TYPE";
	public static final String HORIZONTAL_TYPE_SLUR = "SLUR";
	public static final String HORIZONTAL_TYPE_TUPLE = "TUPLE";
	public static final String HORIZONTAL_TYPE_TIE = "TIE";
	public static final String HORIZONTAL_TUPLENUMBER = "TUPLENUMBER";
	public static final String HORIZONTAL_UPDOWN = "UPDOWN";
	
	public static final String SMXF_MAIN_EXTADDRESS_PART = "PART";
}