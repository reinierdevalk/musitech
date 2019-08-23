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
import java.util.HashMap;
import java.util.Map;

import de.uos.fmt.musitech.score.gui.Accent;

/**
 * @author Sascha Wegener
 * 
 * @version 02.02.2008
 */
public class Mappings implements Elements,Attributes{

	public static final Map<String, Byte> ACCIDENTALTYPES_BYTE = new HashMap<String, Byte>();
	public static final Map<Byte, String> BYTE_ACCIDENTALTYPES = new HashMap<Byte, String>();
	{
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_NATURALUP, (byte) 0);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_NATURAL, (byte) 0);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_NATURALDOWN, (byte) 0);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_SHARP1Q, (byte) 1);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_SHARP, (byte) 1);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_SHARP3Q, (byte) 1);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_FLAT1Q, (byte) -1);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_FLAT, (byte) -1);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_FLAT3Q, (byte) -1);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_DSHARP, (byte) 2);
		ACCIDENTALTYPES_BYTE.put(ACCIDENTAL_TYPE_DFLAT, (byte) -2);

		BYTE_ACCIDENTALTYPES.put((byte) 0, ACCIDENTAL_TYPE_NATURAL);
		BYTE_ACCIDENTALTYPES.put((byte) 1, ACCIDENTAL_TYPE_SHARP);
		BYTE_ACCIDENTALTYPES.put((byte) -1, ACCIDENTAL_TYPE_FLAT);
		BYTE_ACCIDENTALTYPES.put((byte) 2, ACCIDENTAL_TYPE_DSHARP);
		BYTE_ACCIDENTALTYPES.put((byte) -2, ACCIDENTAL_TYPE_DFLAT);
	}
	
	public static final Map<String, Byte> ORNAMENTTYPES_ACCENTS = new HashMap<String, Byte>();
	public static final Map<Byte,String> ACCENTS_ORNAMENTTYPES = new HashMap<Byte,String>();
	{	
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_TRILL, Accent.TRILL);
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_TURN, Accent.TURN);
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_TURNBACK, Accent.TURN);
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_TURNUP, Accent.TURN);
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_TURNSLASH, Accent.TURN);
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_MORDENT, Accent.MORDENT_DOWN);
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_MORDENTSUP, Accent.MORDENT_UP);
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_DMORDENT, Accent.MORDENT_DOWN);
		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_DMORDENTSUP, Accent.MORDENT_UP);
		
//		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_TREMOLO, Accent.DUMMY);
//		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_STEMTREMOLO, Accent.DUMMY);
//		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_GLISSWAVE, Accent.DUMMY);
//		ORNAMENTTYPES_ACCENTS.put(ORNAMENT_TYPE_GLISSLINE, Accent.DUMMY);
		// TODO Fehlende Akzente in Musitech vervollständigen
		
		ACCENTS_ORNAMENTTYPES.put(Accent.TRILL, ORNAMENT_TYPE_TRILL);
		ACCENTS_ORNAMENTTYPES.put(Accent.TURN, ORNAMENT_TYPE_TURN);
		ACCENTS_ORNAMENTTYPES.put(Accent.MORDENT_DOWN, ORNAMENT_TYPE_MORDENT);
		ACCENTS_ORNAMENTTYPES.put(Accent.MORDENT_UP, ORNAMENT_TYPE_MORDENTSUP);
	}
	
	public static final Map<String, Byte> FERMATATYPES_ACCENTS = new HashMap<String, Byte>();
	public static final Map<Byte,String> ACCENTS_FERMATATYPES = new HashMap<Byte,String>();
	{
		FERMATATYPES_ACCENTS.put(FERMATA_TYPE_SHORT, Accent.FERMATA);
		FERMATATYPES_ACCENTS.put(FERMATA_TYPE_MEDIUM, Accent.FERMATA);
		FERMATATYPES_ACCENTS.put(FERMATA_TYPE_LONG, Accent.FERMATA);
		ACCENTS_FERMATATYPES.put(Accent.FERMATA, FERMATA_TYPE_MEDIUM);
		// TODO Fehlende Fermaten in Musitech vervollständigen
	}
	
	public static final Map<String, Byte> MARKERTYPES_ACCENTS = new HashMap<String, Byte>();
	public static final Map<Byte,String> ACCENTS_MARKERTYPES = new HashMap<Byte,String>();
	{
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_PORTATO, Accent.PORTATO);
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_TENUTO, Accent.PORTATO);
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_SFORZATO, Accent.SFORZATO);
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_ACCENTO, Accent.MARCATO);
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_ACCENTOFORTE, Accent.MARCATO);
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_STACCATO, Accent.STACCATO);
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_PUNTOSOPRA, Accent.STACCATO);
		// MARTDOLCE fehlt
		// PUNTOALLUNGATO fehlt
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_MARTELLATO, Accent.MARTELLATO);
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_MARTELLATO, Accent.MARTELLATO);
		// ARCO fehlt
		// PONTICELLO fehlt
		// TASTIERA fehlt
		// PUNTA fehlt
		// TALLONE fehlt
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_BOWUP, Accent.UP_BOW);
		MARKERTYPES_ACCENTS.put(MARKER_TYPE_BOWDOWN, Accent.DOWN_BOW);
		// PIZZ fehlt
		
		ACCENTS_MARKERTYPES.put(Accent.STACCATO, MARKER_TYPE_STACCATO);
		ACCENTS_MARKERTYPES.put(Accent.MARCATO, MARKER_TYPE_ACCENTO);
		ACCENTS_MARKERTYPES.put(Accent.PORTATO, MARKER_TYPE_PORTATO);
		ACCENTS_MARKERTYPES.put(Accent.SFORZATO, MARKER_TYPE_SFORZATO);
		ACCENTS_MARKERTYPES.put(Accent.DOWN_BOW, MARKER_TYPE_BOWDOWN);
		ACCENTS_MARKERTYPES.put(Accent.UP_BOW, MARKER_TYPE_BOWUP);
		ACCENTS_MARKERTYPES.put(Accent.MARTELLATO, MARKER_TYPE_MARTELLATO);
	}
}
