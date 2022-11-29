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
package de.uos.fmt.musitech.data.structure.harmony;

import java.util.ArrayList;

import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.time.*;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This Class represents a key signature and is normally used in a Piece's MetricalTimeLine. 
 * It has a physical and a metrical time. 
 * 
 * @version $Revision: 8558 $, $Date: 2013-09-22 14:52:09 +0200 (Sun, 22 Sep 2013) $
 * @author Tillman Weyde and Kerstin Neubarth
 * 
 * @hibernate.class table="KeyMarker"
 * 
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 *  
 */

public class KeyMarker extends TimedMetrical implements Marker{

    /**
	 * 
	 */
	private static final long serialVersionUID = 3950191364764921329L;

	/**
     * The root of the key, one of {XCDEFGAB} X means unknown.
     * 
     * @param metricTime Rational
     * @param timeStamp TimeStamp
     */
    public KeyMarker(Rational metricTime, long timeStamp) {
        super(timeStamp, metricTime);
    }

    /**
     * The root of the key, one of {XCDEFGAB} X means unknown.
     */
    private char root = 'C';

    /**
     * The alteration of the key's root.
     */
    private int rootAlteration = 0;

    private int alterationNum = 0;

    private Mode mode = Mode.MODE_UNKNOWN;

    /**
     * getAccidentalNum
     * 
     * @return The number of sharps (positive) or flats (negative) for this key.
     * 
     * @hibernate.property
     * @deprecated use getAlterationNum instead
     */
    @Deprecated
	public int getAccidentalNum() {
        return alterationNum;
    }

    /**
     * getAlterationNum
     * 
     * @return The number of sharps (positive) or flats (negative) for this key.
     * 
     * @hibernate.property
     */
    public int getAlterationNum() {
        return alterationNum;
    }

    /**
     * setAccidentalNum Sets number of accidentals and adjusts root and
     * rootAlteration if necessary while the mode's actual value does not
     * change.
     * 
     * @param i accidentalNum to be set
     */
    public void setAlterationNum(int i) {
        //    	if (Math.abs(i) > 6)
        //    		throw new IllegalArgumentException("accidental must be between -6 and
        // 6.");
        alterationNum = i;
        ScorePitch tp = determineRootAndAccidental(i, mode);
        root = Character.toUpperCase(tp.getDiatonic()); // rdv 23.11.22: was root = tp.getDiatonic()
        rootAlteration = tp.getAlteration();
    }

    /**
     * setAccidentalNum Sets number of accidentals and adjusts root and
     * rootAccidental if necessary while the mode's actual value does not
     * change.
     * 
     * @param i accidentalNum to be set
     * @deprecated use setAlterationNum instead.
     */
    @Deprecated
	public void setAccidentalNum(int i) {
    	setAlterationNum(i);
    }

    public enum Mode {
	    /**
	     * Mode constants. Methods determineRootAndAccidental and
	     * determineAccidentalNum are based on the given values
	     * {-1,0,1,1,2,3,4,5,6,7}.
	     */
	    MODE_UNKNOWN(-1),
	    /** Minor mode, equivalent to aeolian */
	    MODE_MINOR(0),
	    /** Major mode */
	    MODE_MAJOR(1),
	    /** Ionian mode, equivalent to c major, originally starting from d */
	    MODE_IONIAN(1),
	    /** Dorian mode */
	    MODE_DORIAN(2),
	    /** Phrygian mode */
	    MODE_PHRYGIAN(3),
	    /** Lydian mode */
	    MODE_LYDIAN(4),
	    /** Mixolydian mode */
	    MODE_MIXOLYDIAN(5),
	    /** Aeolian mode */
	    MODE_AEOLIAN(6),
	    /** Locrian mode */
	    MODE_LOCRIAN(7),
	    /** Suspended fourth, for chords */ 
	    MODE_SUS(8);
		private final int code;
		Mode(int argCode){
			code = argCode;
		}
		public int getCode(){
			return code;
		}
    }


    /**
     * Returns the rootAccidental.
     * 
     * @return char
     * 
     * @hibernate.property
     */
    public int getRootAlteration() {
        return rootAlteration;
    }

    /**
     * Returns the rootAccidental.
     * 
     * @return char
     * 
     * @hibernate.property
     * @deprecated use getRootAlteration()
     */
    @Deprecated
	public int getRootAccidental() {
        return rootAlteration;
    }

    /**
     * Returns the mode.
     * 
     * @return int
     * 
     * @hibernate.property
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Returns the root.
     * 
     * @return char
     * 
     * @hibernate.property
     */
    public char getRoot() {
        return root;
    }

    /**
     * Sets the alteration of the root and adjusts number of alterations if
     * necessary, the mode does not change its actual value. E.g. -1 defines to
     * E-flat major if root is 'E' mode is MODE_MAJOR, leading to three flats 
     * for the key.
     * 
     * @param alteration The root alteration to set
     */
    public void setRootAlteration(int alteration) {
        this.rootAlteration = alteration;
        //accidentalNum = determineAccidentalNum(mode, root, rootAccidental);
    }
    
    /**
     * Sets the accidental of the root and adjusts number of accidentals if
     * necessary, the mode does not change its actual value. E.g. -1 defines to
     * E-flat major if root is 'E' mode is MODE_MAJOR, leading to three flats.
     * 
     * @param accidental The accidental to set
     * @deprecated use setRootAlteration()
     */
    @Deprecated
	public void setRootAccidental(int accidental) {
        this.rootAlteration = accidental;
        //accidentalNum = determineAccidentalNum(mode, root, rootAccidental);
    }
    
    /**
     * @param argAccidentalNum
     * @param argMode
     * @deprecated use setAlterationNumAndMode() instead 
     */
    @Deprecated
	public void setAccidentalNumAndMode(int argAccidentalNum, Mode argMode) {
    	setAccidentalNum(argAccidentalNum);
    	setMode(argMode);
    	ScorePitch rootSP = determineRootAndAccidental(argAccidentalNum, argMode);
    	setRoot(rootSP.getDiatonic());
    	setRootAccidental(rootSP.getAlteration());
    }

    /**
     * @param argAccidentalNum
     * @param argMode
     */
    public void setAlterationNumAndMode(int argAccidentalNum, Mode argMode) {
    	setAlterationNum(argAccidentalNum);
    	setMode(argMode);
    	ScorePitch rootSP = determineRootAndAccidental(argAccidentalNum, argMode);
    	setRoot(rootSP.getDiatonic());
    	setRootAlteration(rootSP.getAlteration());
    }

    /**
     * Sets the mode, using one of the mode constants defined in this class, and
     * adjusts number of accidentals if necessary while root and rootAccidental
     * do not change their actual values. E.g. MODE_MINOR leads to four flats if
     * root is 'F'.
     * 
     * @param mode The mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        //accidentalNum = determineAccidentalNum(mode, root, rootAccidental);
    }

    /**
     * Sets the root and adjusts the number of accidentals if necessary while
     * the mode does not change its actual value.
     * 
     * @param root The root to set
     */
    public void setRoot(char root) {
        this.root = Character.toUpperCase(root);
        //accidentalNum = determineAccidentalNum(mode, root, rootAccidental);
    }

    /**
     * returns the current mode in a text representation.
     * 
     * @return The English name of the current mode.
     */
    public String modeString() {
        return modeString(mode);
    }

    /**
     * Returns an English text representation for a given mode id.
     * 
     * @param m The mode in numeric representation as defined in this class.
     * @return The English name of the given mode.
     */
    public String modeString(Mode m) {
        switch (m) {
        case MODE_MAJOR:
            return "major";
        case MODE_MINOR:
            return "minor";
        case MODE_DORIAN:
            return "dorian";
        case MODE_PHRYGIAN:
            return "phrygian";
        case MODE_LYDIAN:
            return "lydian";
        case MODE_MIXOLYDIAN:
            return "mixolydian";
        case MODE_AEOLIAN:
            return "aeolian";
        case MODE_LOCRIAN:
            return "locrian";
        default:
            return "unknown";
        }
    }

    /**
     * Returns the rootAccidental as a text representation.
     * 
     * @return The string representing the accidental.
     */
    public String rootAccidentalString() {
        return accidentalString(rootAlteration);
    }

    /**
     * Returns an accidental as a text representation.
     * 
     * @param a The numeric accidental representation.
     * @return The string representing the accidental.
     */
    public String accidentalString(int a) {
        switch (a) {
        case 0:
            return ""; // natural
        case 1:
            return "#"; // sharp
        case 2:
            return "x"; // double sharp
        case -1:
            return "b"; // flat
        case -2:
            return "bb"; // double flat
        default:
            return "";
        }
    }

    /**
     * Returns the KeyMarker as a String.
     * 
     * @return String
     */
    @Override
	public String toString() {

        return "KeyMarker  at " + getMetricTime() + ": " + getTime() + "ms, root: " + getRoot()
               + ", mode: " + modeString(mode) + ", rootAccidental: " + getRootAlteration(); // getRootAccidental();
    }

    /**
     * Just for testing.
     * 
     * @param arguments Not used.
     */
    public static void main(String[] arguments) {

        KeyMarker keySignature = new KeyMarker(new Rational(1, 4), 20);
        keySignature.setRootAlteration(1);
        keySignature.setMode(Mode.MODE_DORIAN);
        keySignature.setRoot('D');

        System.out.println(keySignature);
    }

    /**
     * Determines root and rootAccidental of key for given number of accidentals
     * and mode. The root is set changing the default root 'C' with default
     * accidental natural.
     * 
     * @param argAccidentalNum number of accidentals (positive for sharp,
     *            negative for flat)
     * @param argMode mode given to determine the root
     * @return ScorePitch object of type ScorePitch with fields diatonic and
     *         accidental
     */
    public ScorePitch determineRootAndAccidental(int argAccidentalNum, Mode argMode) {
        ScorePitch tp = new ScorePitch();
        //default root and rootAccidental
        tp.setDiatonic('C');
        tp.setAlteration((byte) 0);
        //Roots for modes without accidentals. Used to change default root and
        // rootAccidental depending on mode.
        char[] rootMode = {'C', 'D', 'E', 'F', 'G', 'A', 'B'};
        //Circle of fifths. Used to change default root and rootAccidental
        // depending on
        // accidentalNum.
        char[] circleFifths = {'F', 'C', 'G', 'D', 'A', 'E', 'B'};
        ArrayList<Character> circle = new ArrayList<Character>();
        for (int j = 0; j < circleFifths.length; j++) {
            circle.add(Character.valueOf(circleFifths[j]));
        }
        int modeValue = argMode.getCode();
        /*
         * If mode is unknown, root cannot be determined. 'X' means unknown
         * root. if (modeValue == MODE_UNKNOW) tp.setDiatonic('X'); Previously with
         * UNKNOWN_MODE default was root C (rootAccidental 0)
         */
        if (modeValue == -1)
            modeValue = 6;
        if (modeValue >= 1) {
            try {
                int i = circle.indexOf(Character.valueOf(rootMode[modeValue - 1])) + argAccidentalNum;
                while (i >= circleFifths.length) {
                    tp.setAlteration((byte) (tp.getAlteration() + 1));
                    i -= circleFifths.length;
                }
                while (i < 0) {
                    tp.setAlteration((byte) (tp.getAlteration() - 1));
                    i += circleFifths.length;
                }
                tp.setDiatonic(circleFifths[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return tp;
    }

    /**
     * Determines number of accidentals for given mode and root with
     * rootAccidental.
     * 
     * @param argMode mode given to determine accidentalNum
     * @param argRoot given root of key
     * @param argRootAccidental given accidental of root
     * @return int number of accidentals
     */

    public int determineAccidentalNum(int argMode, char argRoot, int argRootAccidental) {
        int accNum = 0;
        //Number of accidentals for modes with root C natural. Used to
        // determine
        // accidentalNum depending on mode.
        int[] offsets = {-3, 0, -2, -4, 1, -1, -3, -5};
        // Circle of fifths. Used to determine accidentalNum depending on root.
        char[] circleFifths = {'F', 'C', 'G', 'D', 'A', 'E', 'B'};
        ArrayList<Character> circle = new ArrayList<Character>();
        for (int j = 0; j < circleFifths.length; j++) {
            circle.add(Character.valueOf(circleFifths[j]));
        }
        try {
            /*
             * if (mode == -1 || root == 'X') { //Unknown accidentalNum? }
             * Bisher bei UNKNOWN_MODE default accidentalNum (0).
             */
            if (argMode >= 0) {
                int i = circle.indexOf(Character.valueOf(argRoot)) - 1;
                i += argRootAccidental * 7;
                accNum = i + offsets[argMode];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return accNum;

        /*
         * First and last candidates to be tested by
         * determineRootAndAccidental(). Values chosen as to get natural roots
         * by getRoot().
         */
        // 		int first = -2 * mode + 1;
        //		int last = 7 - 2 * mode;
        //		if (last <= 0) last += 7;
        //
        //		for (int i = first; i <= last; i++) {
        //			determineRootAndAccidental();
        //			if (getRoot() == root)
        //				accidentalNum = i;
        //		}
        //		accidentalNum += rootAccidental * 7;
        //	}
    }

    /**
     * Default constructor, represents C major and position 0.
     */
    public KeyMarker() {
    	setMetricTime(Rational.ZERO);
    	setTime(0);
    }

    /**
     * Get an array of diatonic notes altered in this key.
     * 
     * @return The list of ditonics.
     */
    public char[] alteredDiatonics() {
        char[] alteredDiatonics = null;
        final char[] circleFifths = {'F', 'C', 'G', 'D', 'A', 'E', 'B'};
        if (alterationNum > 0) {
            if (alterationNum <= circleFifths.length) {
                alteredDiatonics = new char[alterationNum];
                for (int i = 0; i < alterationNum; i++) {
                    alteredDiatonics[i] = circleFifths[i];
                }
            } else {
                alteredDiatonics = new char[circleFifths.length];
                for (int i = 0; i < circleFifths.length; i++) {
                    alteredDiatonics[i] = circleFifths[i];
                }
                //TODO
            }
        }
        if (alterationNum < 0) {
            if (-alterationNum <= circleFifths.length) {
                alteredDiatonics = new char[-alterationNum];
                for (int i = 0; i < -alterationNum; i++) {
                    alteredDiatonics[i] = circleFifths[circleFifths.length - i - 1];
                }
            } else {
                alteredDiatonics = new char[circleFifths.length];
                for (int i = 0; i < circleFifths.length; i++) {
                    alteredDiatonics[i] = circleFifths[circleFifths.length - i - 1];
                }
                //TODO
            }
        }
        if (alteredDiatonics == null) {
            alteredDiatonics = new char[0];
        }
        return alteredDiatonics;
    }

}