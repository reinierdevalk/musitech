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


import junit.framework.TestCase;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class includes a couple of test methods for the KeyMarker class.
 * It is written using the JUnit framework.
 * @author kerstin
 * @version 1.0
 * @see de.uos.fmt.musitech.data.structure.harmony.KeyMarker
 */
public class KeyMarkerTest extends TestCase {

	/**
	 * This method tests setting root and accidental of root for given mode and number of accidentals.
	 * Used for testing former method in KeyMarker now changed to protected determineRootAndAccidental.
	 * @see de.uos.fmt.musitech.data.structure.harmony.KeyMarker#setMode(int)
	 * @see de.uos.fmt.musitech.data.structure.harmony.KeyMarker#setAccidentalNum(int)
	 */
	public void testSetRootWithAccidental() {
		int accNum;
//		Mode mode;
	
		Rational r = new Rational();
		KeyMarker km = new KeyMarker(r, 0);
	
		//Test fuer Kreuztonarten (Unterteilung zur Uebersichtlichkeit)
		for (int j = 0; j < 12; j++) {
			accNum = j;
			for (Mode mode: Mode.values()) {
				km.setMode(mode);
				km.setAccidentalNum(accNum);
				System.out.println(
					"Number of accidentals:  "
						+ accNum
						+ "\tMode:  "
						+ km.modeString(mode)
						+ "\tRoot:  "
						+ km.getRoot()
						+ " "
						+ km.rootAccidentalString());
			}
		}
		System.out.println();
	
		//Test fuer B-Tonarten
		for (int j = -1; j > -12; j--) {
			accNum = j;
			for (Mode mode: Mode.values()) {
				//mode = k;
				km.setMode(mode);
				km.setAlterationNum(accNum);
				System.out.println(
					"Number of accidentals:  "
						+ accNum
						+ "\tMode:  "
						+ km.modeString()
						+ "\tRoot:  "
						+ km.getRoot()
						+ " "
						+ km.rootAccidentalString());
			}
		}
	}

	/**
	 * Tests setting accidentalNum for given mode and root with rootAccidental.
	 * If method determineAccidentalNum in KeyMarker is changed to public just for testing.
	 * @see de.uos.fmt.musitech.data.structure.KeyMarker.determineAccidentalNum(int, char, int)(de.uos.fmt.musitech.data.structure)
	 */
	public void testSetNumberOfAccidentals() {
		Rational r = new Rational();
		KeyMarker km = new KeyMarker(r, 0);
		char[] diatonic = { 'C', 'D', 'E', 'F', 'G', 'A', 'B' };
	
		for (int i = 1; i <= 7; i++) {
			for (int j = 0; j < diatonic.length; j++) {
				for (int k = -1; k <= 1; k++) {
					System.out.println(
						"Mode: "
							+ i
							+ "\tRoot: "
							+ diatonic[j]
							+ k
							+ "\tAccidentalNum: "
							+ km.determineAccidentalNum(i, diatonic[j], k));
					//see Javadoc Comment
				}
			}
		}
	}

	/**
	 * Tests setting accidentalNum and adjusting root and rootAccidental for given modes.
	 * @see de.uos.fmt.musitech.data.structure.KeyMarker.setAccidentalNum (de.uos.fmt.musitech.data.structure)
	 */
	public void testSetAccNum() {
		Rational r = new Rational();
		KeyMarker km = new KeyMarker(r, 0);

		System.out.println(
			"\nTEST: setAccidentalNum -> determineRootAndAccidental");

		for (Mode i: Mode.values()) { //mode
			km.setMode(i);
			for (int j = -7; j <= 7; j++) { //accidentalNum
				km.setAlterationNum(j);
				System.out.println(
					"mode: "
						+ i
						+ "\taccidentalNum: "
						+ km.getAccidentalNum()
						+ "\troot: "
						+ km.getRoot()
						+ km.rootAccidentalString());
				;
			}
		}
	}

	/**
	 * Tests setting mode and adjusting accidentalNum for given roots.
	 * @see de.uos.fmt.musitech.data.structure.harmony.KeyMarker#setMode(int)
	 */
	public void testSetMode() {
		Rational r = new Rational();
		KeyMarker km = new KeyMarker(r, 0);
		char[] diatonic = { 'C', 'D', 'E', 'F', 'G', 'A', 'B' };

		System.out.println("\nTEST: setMode -> determineAccidentalNum");

		for (int i = 0; i < diatonic.length; i++) { //root
			km.setRoot(diatonic[i]);
			for (int j = -1; j <= 1; j++) { //rootAccidental
				km.setRootAlteration(j);
				for (Mode k: Mode.values()) {
					km.setMode(k);
					System.out.println(
						"root: "
							+ km.getRoot()
							+ km.rootAccidentalString()
							+ "\tmode: "
							+ km.getMode()
							+ "\taccidentalNum: "
							+ km.getAccidentalNum());
				}

			}
		}

	}

	/**
	 * Tests setting root and rootAccidental and adjusting accidentalNum for given modes.
	 * @see de.uos.fmt.musitech.data.structure.KeyMarker.setRoot (de.uos.fmt.musitech.data.structure)
	 * @see de.uos.fmt.musitech.data.structure.KeyMarker.setRootAccidental (de.uos.fmt.musitech.data.structure)
	 */
	public void testSetRootAndRootAccidental() {
		Rational r = new Rational();
		KeyMarker km = new KeyMarker(r, 0);
		char[] diatonic = { 'C', 'D', 'E', 'F', 'G', 'A', 'B' };

		System.out.println(
			"\nTEST: setRoot, setRootAccidental -> determineAccidentalNum");

		for (Mode i: Mode.values()) { //modes
			km.setMode(i);
			for (int j = 0; j < diatonic.length; j++) { //root
				for (int k = -2; k <= 2; k++) { //rootAccidental
					km.setRoot(diatonic[j]);
					km.setRootAccidental(k);
					System.out.println(
						"mode: "
							+ km.modeString()
							+ "\troot: "
							+ km.getRoot()
							+ km.rootAccidentalString()
							+ "\taccidentalNum: "
							+ km.getAccidentalNum());
				}
			}

		}
	}
	
	public static void testAlteredDiatonics(){
	    Rational r = new Rational();
		KeyMarker km = new KeyMarker(r, 0);
		System.out.println("Test KeyMarkerTest.returnAlteratedDiatonics():");
		for (int i = 0; i < 13; i++) {
		    km.setAccidentalNum(i);
            System.out.print("accidentalNum: "+km.getAccidentalNum()+"\talterated diatonics: ");
            char[] altered = km.alteredDiatonics();
            if(i > 1 ) assertTrue(new String(altered).indexOf('F')>-1);
            if(i > 2 ) assertTrue(new String(altered).indexOf('C')>-1);
            if(i > 3 ) assertTrue(new String(altered).indexOf('G')>-1);
            if(i > 4 ) assertTrue(new String(altered).indexOf('D')>-1);
            if(i > 5 ) assertTrue(new String(altered).indexOf('A')>-1);
            if(i > 6 ) assertTrue(new String(altered).indexOf('E')>-1);
            if(i > 7 ) assertTrue(new String(altered).indexOf('B')>-1);
            for (int j = 0; j < altered.length; j++) {
                System.out.print(" "+altered[j]+" ");
            }
            System.out.println();
        }
		for (int i = 0; i > -13; i--) {
		    km.setAccidentalNum(i);
            char[] altered = km.alteredDiatonics();
            System.out.print("accidentalNum: "+km.getAccidentalNum()+"\talterated diatonics: ");
            if(i < -1 ) assertTrue(new String(altered).indexOf('B')>-1);
            if(i < -2 ) assertTrue(new String(altered).indexOf('E')>-1);
            if(i < -3 ) assertTrue(new String(altered).indexOf('A')>-1);
            if(i < -4 ) assertTrue(new String(altered).indexOf('D')>-1);
            if(i < -5 ) assertTrue(new String(altered).indexOf('G')>-1);
            if(i < -6 ) assertTrue(new String(altered).indexOf('C')>-1);
            if(i < -7 ) assertTrue(new String(altered).indexOf('F')>-1);
            for (int j = 0; j < altered.length; j++) {
                System.out.print(" "+altered[j]+" ");
            }
            System.out.println();
        }
	}

}
