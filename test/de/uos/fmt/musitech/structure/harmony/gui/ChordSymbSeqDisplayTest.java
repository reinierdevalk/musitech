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
/*
 * Created on 15.06.2004
 *
 */
package de.uos.fmt.musitech.structure.harmony.gui;

import java.awt.Component;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbolSequence;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de
	.uos
	.fmt
	.musitech
	.framework
	.editor
	.EditorFactory
	.EditorConstructionException;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * JUnit tests for class ChordSymbSeqDisplay.
 * 
 * @author Kerstin Neubarth
 *
 */
public class ChordSymbSeqDisplayTest extends TestCase {

	/**
	 * For testing method <code>getNextPositioningTime(long)</code>.
	 */
	public static void testGetNextPosition() {
		//create a ChordSymbolSequence
		ChordSymbolSequence sequence = new ChordSymbolSequence();
		ChordSymbol symbol1 = new ChordSymbol();
		symbol1.setTime(0);
		sequence.add(symbol1);
		ChordSymbol symbol2 = new ChordSymbol();
		symbol2.setTime(100);
		sequence.add(symbol2);
		ChordSymbol symbol3 = new ChordSymbol();
		symbol3.setTime(200);
		sequence.add(symbol3);
		ChordSymbol symbol4 = new ChordSymbol();
		symbol4.setTime(300);
		sequence.add(symbol4);
		//create ChordSymbSeqDisplay
		ChordSymbSeqDisplay display = null;
		try {
			display =
				(ChordSymbSeqDisplay) EditorFactory.createDisplay(
					sequence,
					null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check method getNextPosition()
		assertEquals(display.getNextPositioningTime(0), symbol2.getTime());
		assertEquals(display.getNextPositioningTime(50), symbol2.getTime());
		assertEquals(display.getNextPositioningTime(110), symbol3.getTime());
		assertEquals(display.getNextPositioningTime(280), symbol4.getTime());
		assertEquals(
			display.getNextPositioningTime(symbol4.getTime()),
			Timed.INVALID_TIME);
	}

	/**
	 * For testing method <code>getMinimalPositionForTime(long, Rational)</code>.
	 */
	public static void testGetMinimalPositionForTime() {
		//create a ChordSymbolSequence
		ChordSymbolSequence seq = new ChordSymbolSequence();
		ChordSymbol s0 = new ChordSymbol(new Rational(0, 4), 0);
		seq.add(s0);
		ChordSymbol s1 = new ChordSymbol(new Rational(1, 4), 100);
		seq.add(s1);
		ChordSymbol s2 = new ChordSymbol(new Rational(2, 4), 200);
		seq.add(s2);
		ChordSymbol s3 = new ChordSymbol(new Rational(3, 4), 300);
		seq.add(s3);
		//create the ChordSymbSeqDisplay
		ChordSymbSeqDisplay display = null;
		try {
			display =
				(ChordSymbSeqDisplay) EditorFactory.createDisplay(seq, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (display != null) {
			try {
				int pos = display.getMinimalPositionForTime(150, null);
				int posRational =
					display.getMinimalPositionForTime(
						Timed.INVALID_TIME,
						new Rational(3, 8));
				//get end position of ChordSymbolDisplay for s1
				int x = Integer.MIN_VALUE;
				Component[] comps = display.getComponents();
				for (int i = 0; i < comps.length; i++) {
					if (comps[i] instanceof ChordSymbolDisplay) {
						if (((ChordSymbolDisplay) comps[i]).getEditObj() == s1)
							x = comps[i].getX() + comps[i].getWidth();
					}
				}
				//compare x+const (H_GAP) to pos
				assertEquals(x + 3, pos);
				assertEquals(x + 3, posRational);
			} catch (WrongArgumentException wae) {
				wae.printStackTrace();
			}
		} else {
			System.out.println(
				"In ChordSymbSeqDisplayTest.testGetMinimalPositionForTime()\n"
					+ "The ChordSymbSeqDisplay could not be created.");
			fail();
		}

	}

	/**
	 * For testing method <code>setMinimalPositionForTime(long, Rational, int)</code>.
	 */
	public static void testSetMinimalPositionForTime() {
		//create a ChordSymbolSequence
		ChordSymbolSequence seq = new ChordSymbolSequence();
		ChordSymbol s0 = new ChordSymbol(new Rational(0, 4), 0);
		seq.add(s0);
		ChordSymbol s1 = new ChordSymbol(new Rational(1, 4), 100);
		seq.add(s1);
		ChordSymbol s2 = new ChordSymbol(new Rational(2, 4), 200);
		seq.add(s2);
		ChordSymbol s3 = new ChordSymbol(new Rational(3, 4), 300);
		seq.add(s3);
		//create the ChordSymbSeqDisplay
		ChordSymbSeqDisplay display = null;
		try {
			display =
				(ChordSymbSeqDisplay) EditorFactory.createDisplay(seq, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (display != null) {
			//get position of ChordSymbolDisplay for s1
			int x = Integer.MIN_VALUE;
			ChordSymbolDisplay symbolDisplay = null;
			Component[] comps = display.getComponents();
			for (int i = 0; i < comps.length; i++) {
				if (comps[i] instanceof ChordSymbolDisplay) {
					if (((ChordSymbolDisplay) comps[i]).getEditObj() == s1) {
						x = comps[i].getX();
						symbolDisplay = (ChordSymbolDisplay) comps[i];
					}
				}
			}
			//check method setMinimalPositionForTime()
			boolean displayChanged = false;
			try {
				//set minimal position on the left side of symbolDisplay
				displayChanged =
					display.setMinimalPositionForTime(100, null, x - 50);
				assertFalse(displayChanged);
				//set minimal position to the right of symbolDisplay
				displayChanged =
					display.setMinimalPositionForTime(100, null, x + 50);
				assertTrue(displayChanged);
				assertFalse(symbolDisplay.getX() == x);
				assertTrue(symbolDisplay.getX() > x);
				assertEquals(symbolDisplay.getX(), x + 50);
				//check with Rational argument
				displayChanged =
					display.setMinimalPositionForTime(
						Timed.INVALID_TIME,
						new Rational(1, 4),
						x - 50);
				assertFalse(displayChanged);
				displayChanged =
					display.setMinimalPositionForTime(
						Timed.INVALID_TIME,
						new Rational(1, 4),
						x + 100);
				assertTrue(displayChanged);
			} catch (WrongArgumentException wae) {
				wae.printStackTrace();
			}
		} else {
			System.out.println(
				"In ChordSymbSeqDisplayTest.testSetMinimalPositionForTime()\n"
					+ "The ChordSymbSeqDisplay could not be created.");
			fail();
		}
	}

}
