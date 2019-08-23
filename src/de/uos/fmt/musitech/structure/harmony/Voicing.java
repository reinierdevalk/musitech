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
package de.uos.fmt.musitech.structure.harmony;


import de.uos.fmt.musitech.data.structure.harmony.ChordMap;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;

/**
 * @author Christa Deiwiks, Klaus Dalinghaus
 * 
 * The Voicing class generates the notes belonging to a chord depending on a
 * given voicing mechanism and an octave
 */
public class Voicing {

    public int octave;
    public int method;
    public static final int SIMPLE_VOICING = 1;
    public static final int STANDARD_OCTAVE = 4;

    /**
     * @see java.lang.Object#Object() creates the voicing object with octave 4
     *      and simple voicing
     */
    public Voicing() {
        this(SIMPLE_VOICING, STANDARD_OCTAVE);
    }

    /**
     * Method Voicing.
     * 
     * @param method
     */
    public Voicing(int method) {
        this(method, STANDARD_OCTAVE);
    }

    /**
     * Method Voicing.
     * 
     * @param octave
     * @param method
     */
    public Voicing(int method, int octave) {
        this.octave = octave;
        this.method = method;
    }

    public ChordMap createChord(ChordSymbol cs) {

        switch (method) {
        case SIMPLE_VOICING:
            return simpleVoicing(cs);
        default:
            return simpleVoicing(cs);
        }
    }

    ChordMap simpleVoicing(ChordSymbol cs) {

        ChordMap ch = new ChordMap();

        Mode mode = cs.getMode();
        char diatonic = cs.getRoot();
        int accid = cs.getRootAlteration();
        String ext = cs.getExtensions();

        ExtensionParser ep = new ExtensionParser();

        if (!ep.parse(mode, ext)) { // if parsing did not succeed
            System.out.println("Extension not valid");
        } else {
            int diatonicPitch = translate(diatonic, accid);

            // Root
            ch.put(ChordMap.ROOT, diatonicPitch);

            // Third
            ch.put(ChordMap.THIRD, diatonicPitch + ep.third);

            // FIFTH
            ch.put(ChordMap.FIFTH, diatonicPitch + ep.fifth);

            // Septe
            if (ep.seventh > 0) {
                ch.put(ChordMap.SEVENTH, diatonicPitch + ep.seventh);
            }

            // None
            if (ep.ninth > 0) {
                ch.put(ChordMap.NINTH, diatonicPitch + ep.ninth);
            }
            // Undezime
            if (ep.eleventh > 0) {
                ch.put(ChordMap.ELEVENTH, diatonicPitch + ep.eleventh);
            }
            // Tridezime
            if (ep.thirteenth > 0) {
                ch.put(ChordMap.THIRTEENTH, diatonicPitch + ep.thirteenth);
            }
        }

        return ch;
    }
    
    ChordMap framedVoicing(ChordMap.Component lowest, ChordMap.Component highest){
    	return null;
    }
    

    /**
     * Innner class for parsing chord extensions.
     * 
     * @author see above
     */
    static class ExtensionParser {

        int third = -1;
        int fifth = -1;
        int seventh = -1;
        int ninth = -1;
        int eleventh = -1;
        int thirteenth = -1;

        /**
         * Constructor for ExtensionParser
         */
        ExtensionParser() {
        }

        /**
         * Method readInt. Translates strings of numbers into ints
         * 
         * @param ext The extension String
         * @param i Position in the String to read
         * @return int The converted character
         */
        int readInt(String ext, int i) {
            switch (ext.charAt(i)) {
            case '4':
                return 4;
            case '5':
                return 5;
            case '7':
                return 7;
            case '9':
                return 9;
            case '1': //cases: 1, 11, 13
                i++;
                if (i >= ext.length())
                    return 0;
                switch (ext.charAt(i)) {
                case '1':
                    return 11;
                case '3':
                    return 13;
                default:
                    return 0;
                }
            default:
                return 0;
            }
        }

        /**
         * Method parse.
         * 
         * @param mode The mode of the chord
         * @param extension The extension string of the chord
         * @return boolean Success report
         */
        boolean parse(Mode mode, String extension) {
            // process the mode of the chord
            switch (mode) {
            case MODE_MINOR:
                third = 3;
                fifth = 7;
                break;
            case MODE_MAJOR: //same as MODE_IONIAN
                third = 4;
                fifth = 7;
                break;
            case MODE_AEOLIAN: //
                third = 4;
                break;
            case MODE_DORIAN:
                third = 4;
                break;
            case MODE_LOCRIAN:
                third = 4;
                break;
            case MODE_LYDIAN:
                third = 4;
                break;
            case MODE_MIXOLYDIAN:
                third = 4;
                break;
            case MODE_PHRYGIAN:
                third = 4;
                break;
            default:
                return false;
            }
            // process extension string
            int i = 0; // index for pos in extension string
            int j = 0; // integer values in extension string
            int diff = 0; // inflections: sharp/flat
            int sus = 0;
            int add = 0;
            while (i < extension.length()) {
                switch (extension.charAt(i)) {
                case '+':
                case '#':
                    i++;
                    diff = 1;
                    j = readInt(extension, i);
                    i++;
                    break;
                case '-':
                case 'b':
                    i++;
                    diff = -1;
                    j = readInt(extension, i);
                    i++;
                    break;
                case 'm':
                    if (!extension.substring(i, 4).equals("maj7"))
                        return false;
                    i += 4;
                    diff = 1;
                    j = 7;
                    break;
                case 's':
                    if (!extension.substring(i, 3).equals("sus"))
                        return false;
                    i += 3;
                    sus = 1;
                    continue;
                case 'a':
                    if (!extension.substring(i, 3).equals("add"))
                        return false;
                    i += 3;
                    add = 1;
                    continue;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    diff = 0;
                    j = readInt(extension, i); // read out from the string
                    i++;
                    break;
                default:
                    return false;
                }
                if (j > 10)
                    i++;
                switch (j) {
                case 4:
                    if (diff != 0)
                        return false; //e.g. C#4 does not exist
                    third = 5;
                    break;
                case 5:
                    fifth = diff + 7;
                    break;
                case 7:
                    seventh = diff + 10;
                    break;
                case 9:
                    ninth = diff + 14;
                    if (seventh < 0 && add == 0)
                        seventh = 10;
                    break;
                case 11:
                    eleventh = diff + 17;
                    if (seventh < 0 && add == 0)
                        seventh = 10;
                    if (ninth < 0 && add == 0)
                        ninth = 14;
                    break;
                case 13:
                    thirteenth = diff + 21;
                    if (seventh < 0 && add == 0)
                        seventh = 10;
                    if (ninth < 0 && add == 0)
                        ninth = 14;
                    if (eleventh < 0 && add == 0)
                        eleventh = 17;
                    break;
                default:
                    return false;
                }
                add = 0;
                sus = 0;
            }
            if ((add != 0) || (sus != 0))
                return false;
            return true;
        }
    }

    /**
     * Method translate. Translate a note description ("A") into a pitch (9) at
     * the appropriate level (octave) while considering accidentals.
     * 
     * @param diatonic
     * @param accid
     * @param octave
     * @return int
     */
    int translate(char diatonic, int accid) {
        int pitch;
        switch (diatonic) {
        case 'A':
            pitch = 9;
            break;
        case 'B':
            pitch = 11;
            break;
        case 'C':
            pitch = 0;
            break;
        case 'D':
            pitch = 2;
            break;
        case 'E':
            pitch = 4;
            break;
        case 'F':
            pitch = 5;
            break;
        case 'G':
            pitch = 7;
            break;
        default:
            assert false;
            return -1;
        }
        pitch = pitch + accid;
        if (pitch < 0)
            pitch = pitch + (octave + 1) * 12;
        else if (pitch > 11)
            pitch = pitch + (octave - 1) * 12;
        else
            pitch = pitch + octave * 12;
        return pitch;

    }

}