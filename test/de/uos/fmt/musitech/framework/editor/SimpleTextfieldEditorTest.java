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
 * Created on 03.06.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * JUnit tests for SimpleTextfieldEditor and its subclasses.
 * 
 * @author Kerstin Neubarth
 *
 */
public class SimpleTextfieldEditorTest extends TestCase {

	/**
	 * Tests methods <code>getInputValue()</code> and 
	 * <code>applyChangesToPropertyValue()</code> 
	 * for an int property.
	 * <br>
	 * As <code>getInputValue()<(code> is not public and the <code>propertyValue</code>
	 * has no getter, the int property of the <code>editObj</code> is checked
	 * after <code>applyChanges()</code> has been performed.
	 */
	public void testApplyWithIntEditor() {
		Rational objInt = new Rational(2, 3);
		EditingProfile profileInt = new EditingProfile("numer");
		Editor intEditor = null;
		try {
			intEditor = EditorFactory.createEditor(objInt, profileInt);
			if (intEditor instanceof IntEditor) {
				((IntEditor) intEditor).getTextfield().setText("5");
				((IntEditor) intEditor).applyChangesToPropertyValue();
				intEditor.applyChanges();
				assertFalse(objInt.getNumer() == 2);
				assertEquals(objInt.getNumer(), 5);
			} else
				fail();
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests methods <code>getInputValue()</code> and 
	 * <code>applyChangesToPropertyValue()</code> 
	 * for a byte property.
	 * <br>
	 * As <code>getInputValue()<(code> is not public and the <code>propertyValue</code>
	 * has no getter, the byte property of the <code>editObj</code> is checked
	 * after <code>applyChanges()</code> has been performed.
	 */
	public void testApplyWithByteEditor() {
		ScoreNote objByte = new ScoreNote();
		objByte.setAlteration(new Integer(2).byteValue());
		EditingProfile profileByte = new EditingProfile("accidental");
		Editor byteEditor = null;
		try {
			byteEditor = EditorFactory.createEditor(objByte, profileByte);
			if (byteEditor instanceof ByteEditor) {
				((ByteEditor) byteEditor).getTextfield().setText("-1");
				((ByteEditor) byteEditor).applyChangesToPropertyValue();
				byteEditor.applyChanges();
				assertFalse(objByte.getAlteration() == 2);
				assertEquals(objByte.getAlteration(), -1);
			} else
				fail();
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests methods <code>getInputValue()</code> and 
	 * <code>applyChangesToPropertyValue()</code> 
	 * for a char property.
	 * <br>
	 * As <code>getInputValue()<(code> is not public and the <code>propertyValue</code>
	 * has no getter, the char property of the <code>editObj</code> is checked
	 * after <code>applyChanges()</code> has been performed.
	 */
	public void testApplyWithCharEditor() {
		ScoreNote objChar = new ScoreNote();
		objChar.setDiatonic('a');
		EditingProfile profileChar = new EditingProfile("diatonic");
		Editor charEditor = null;
		try {
			charEditor = EditorFactory.createEditor(objChar, profileChar);
			if (charEditor instanceof CharEditor) {
				((CharEditor) charEditor).getTextfield().setText("e");
				((CharEditor) charEditor).applyChangesToPropertyValue();
				charEditor.applyChanges();
				assertFalse(objChar.getDiatonic() == 'a');
				assertEquals(objChar.getDiatonic(), 'e');
			} else
				fail();
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests methods <code>getInputValue()</code> and 
	 * <code>applyChangesToPropertyValue()</code> 
	 * for a Rational property.
	 * <br>
	 * As <code>getInputValue()<(code> is not public and the <code>propertyValue</code>
	 * has no getter, the Rational property of the <code>editObj</code> is checked
	 * after <code>applyChanges()</code> has been performed.
	 */
	public void testApplyWithRationalEditor() {
		ScoreNote objRational = new ScoreNote();
		objRational.setMetricTime(new Rational(1, 3));
		EditingProfile profileRational = new EditingProfile("metricTime");
		profileRational.setEditortype("Rational");
		Editor rationalEditor = null;
		try {
			rationalEditor = EditorFactory.createEditor(objRational, profileRational);
			if (rationalEditor instanceof RationalEditor) {
				((RationalEditor) rationalEditor).getTextfield().setText("2");
				((RationalEditor) rationalEditor).applyChangesToPropertyValue();
				rationalEditor.applyChanges();
				assertFalse(objRational.getMetricTime().equals(new Rational(1, 3)));
				assertEquals(objRational.getMetricTime(), new Rational(2, 3));
			} else
				fail();
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}
	
}
