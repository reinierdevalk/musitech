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
 * Created on 22.06.2004
 */
package de.uos.fmt.musitech.structure.text;

import java.awt.Component;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.framework.editor.Display;
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
 * JUnit tests for class LyricsDisplay.
 * 
 * @author Kerstin Neubarth
 */
public class LyricsDisplayTest extends TestCase {

	/**
	 * Tests creating a LyricsDisplay using the EditorFactory.
	 */
	public static void testCreateWithEditorFactory() {
		//create editObj
		LyricsSyllableSequence lyrics = new LyricsSyllableSequence();
		LyricsSyllable syl1 = new LyricsSyllable();
		syl1.setText("Syll");
		lyrics.add(syl1);
		LyricsSyllable syl2 = new LyricsSyllable();
		syl2.setText("la");
		lyrics.add(syl2);
		//create display
		Display display = null;
		try {
			display = EditorFactory.createDisplay(lyrics, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(display);
		assertTrue(display instanceof LyricsDisplay);
		assertEquals(display.getEditObj(), lyrics);
	}

	/**
	 * Tests method <code>getMinimalPositionForTime(long, Rational)</code>.
	 */
	public static void testGetMinimalPositionWithPhysicalTime() {
		//create LyricsSyllableSequence
		LyricsSyllableSequence seq = new LyricsSyllableSequence();
		LyricsSyllable syl0 = new LyricsSyllable(0, "syl");
		syl0.setMetricTime(new Rational(0, 4));
		seq.add(syl0);
		LyricsSyllable syl1 = new LyricsSyllable(100, "la");
		syl1.setMetricTime(new Rational(1, 4));
		seq.add(syl1);
		LyricsSyllable syl2 = new LyricsSyllable(200, "ble");
		syl2.setMetricTime(new Rational(2, 4));
		seq.add(syl2);
		//create LyricsDisplay
		LyricsDisplay display = null;
		try {
			display = (LyricsDisplay) EditorFactory.createDisplay(seq, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
		}
		if (display == null) {
			System.out.println(
				"In LyricsDisplayTest, method testGetMinimalPositionForTime()\n"
					+ "LyricsDisplay could not be created.");
			fail();
			return;
		}
		//get position next to display of syl1
		int pos = Integer.MIN_VALUE;
		Component[] comps = display.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof LyricsSyllableDisplay) {
				Object obj = ((LyricsSyllableDisplay) comps[i]).getEditObj();
				if (obj == syl1)
					pos = comps[i].getX() + comps[i].getWidth() + 3;
			}
		}
		//test with physical time
		int pixels = Integer.MIN_VALUE;
		try {
			pixels = display.getMinimalPositionForTime(120, null);
		} catch (WrongArgumentException e) {
			e.printStackTrace();
		}
		assertTrue(pixels != Integer.MIN_VALUE);
		assertEquals(pixels, pos);
		//test with metrical time
		pixels = Integer.MIN_VALUE;
		try {
			pixels =
				display.getMinimalPositionForTime(
					Timed.INVALID_TIME,
					new Rational(3, 8));
		} catch (WrongArgumentException e) {
			e.printStackTrace();
		}
		assertTrue(pixels != Integer.MIN_VALUE);
		assertEquals(pixels, pos);
	}

	/**
	 * Tests method <code>setMinimalPositionForTime(long, Rational, int)</code>.
	 */
	public static void testSetMinimalPositionForTime() {
		//create LyricsSyllableSequence
		LyricsSyllableSequence seq = new LyricsSyllableSequence();
		LyricsSyllable syl0 = new LyricsSyllable(0, "syl");
		syl0.setMetricTime(new Rational(0, 4));
		seq.add(syl0);
		LyricsSyllable syl1 = new LyricsSyllable(100, "la");
		syl1.setMetricTime(new Rational(1, 4));
		seq.add(syl1);
		LyricsSyllable syl2 = new LyricsSyllable(200, "ble");
		syl2.setMetricTime(new Rational(2, 4));
		seq.add(syl2);
		//create LyricsDisplay
		LyricsDisplay display = null;
		try {
			display = (LyricsDisplay) EditorFactory.createDisplay(seq, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
		}
		if (display == null) {
			System.out.println(
				"In LyricsDisplayTest, method testGetMinimalPositionForTime()\n"
					+ "LyricsDisplay could not be created.");
			fail();
			return;
		}
		//get position of display of syl1
		int pos = Integer.MIN_VALUE;
		LyricsSyllableDisplay sylDisplay = null;
		Component[] comps = display.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof LyricsSyllableDisplay) {
				Object obj = ((LyricsSyllableDisplay) comps[i]).getEditObj();
				if (obj == syl1) {
					pos = comps[i].getX();
					sylDisplay = (LyricsSyllableDisplay) comps[i];
				}
			}
		}
		//test for physical time
		boolean changed = false;
		try {
			changed = display.setMinimalPositionForTime(100, null, pos - 1);
			assertFalse(changed);
			changed = display.setMinimalPositionForTime(100, null, pos + 20);
			assertTrue(changed);
			assertTrue(sylDisplay.getX() > pos);
			assertEquals(sylDisplay.getX(), pos + 20);
		} catch (WrongArgumentException e) {
			e.printStackTrace();
		}
		//test for metrical time
		changed = false;
		try {
			changed = display.setMinimalPositionForTime(100, null, pos - 1);
			assertFalse(changed);
			changed = display.setMinimalPositionForTime(100, null, pos + 40);
			//sylDisplay has been shifted by 20 (therefore: +40)
			assertTrue(changed);
			assertTrue(sylDisplay.getX() > pos + 20);
			//sylDisplay has been shifted by 20
			assertEquals(sylDisplay.getX(), pos + 40);
		} catch (WrongArgumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests method <code>getNextPositioningTime(long)</code>.
	 */
	public static void testGetNextPositioningTime() {
		//create LyricsSyllableSequence
		LyricsSyllableSequence seq = new LyricsSyllableSequence();
		LyricsSyllable syl0 = new LyricsSyllable(0, "syl");
		syl0.setMetricTime(new Rational(0, 4));
		seq.add(syl0);
		LyricsSyllable syl1 = new LyricsSyllable(100, "la");
		syl1.setMetricTime(new Rational(1, 4));
		seq.add(syl1);
		LyricsSyllable syl2 = new LyricsSyllable(200, "ble");
		syl2.setMetricTime(new Rational(2, 4));
		seq.add(syl2);
		//create LyricsDisplay
		LyricsDisplay display = null;
		try {
			display = (LyricsDisplay) EditorFactory.createDisplay(seq, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
		}
		if (display == null) {
			System.out.println(
				"In LyricsDisplayTest, method testGetMinimalPositionForTime()\n"
					+ "LyricsDisplay could not be created.");
			fail();
			return;
		}
		//test getNextPositiongTime(long)
		long pos = Timed.INVALID_TIME;
		pos = display.getNextPositioningTime(syl0.getTime());
		assertEquals(pos, syl1.getTime());
		pos = display.getNextPositioningTime(syl1.getTime()-50);
		assertEquals(pos, syl1.getTime());
	}

}
