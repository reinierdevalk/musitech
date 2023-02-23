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
 * Created on 17.11.2003
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.Vector;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.editor.TestEditorFactory.CollectionWithChildren;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Test class for editors in handling null-propertyValues.
 * <br>
 * <br>If an editor gets an <code>editObj</code> with a property the value of which 
 * is null (that is, <code>propertyValue</code> is null, but<code>propertyName</code> 
 * is not), it is to show a JButton with text "create". 
 * If the user activates this button, an input dialog 
 * (its form depending on the class of <code>propertyValue</code>)
 * is opened asking the required information from the user.
 * 
 * 
 * @author Kerstin Neubarth
 *
 */
public class TestNullPropertyHandling {

	static ScoreNote scoreNote =
		new ScoreNote(
			new Rational(1, 2),
			new Rational(1, 2),
			'c',
			(byte) 0,
			(byte) 0);

	/**
	 * This window exits the program when closed.
	 * <br>
	 * <br> It is used since the EditorWindows do not do this when closed.
	 * @author Tobias Widdra
	 */
	static class ExitWindow extends javax.swing.JFrame {
		ExitWindow() {
			super("I exist for you to exit the program :o)");
			setSize(350, 0);
			addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				}
			});
		}
	}

	/**
	 * Test when one property is null: A new note is created with only the PerformanceNote set.
	 *
	 */
	public static void testNoteWithOnePropertyNull() {
		Note note = new Note(new PerformanceNote());
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(note, note.getEditingProfile());
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test with one property null");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Tests ExpandEditor for null-property.
	 * A "Create"-button should be shown.
	 */
	public static void testExpandEditorWithNullProperty() {
		Note noteWithScoreNoteOnly = new Note(scoreNote, null);
		//		ExpandEditor editor = new ExpandEditor();
		//		editor.construct(
		//			noteWithScoreNoteOnly,
		//			"PerformanceNote",
		//			null,
		//			"Performance Note");
		Editor editor = null;
		EditingProfile prof =
			new EditingProfile("PerformanceNote", "Expand", "PerformanceNote");
		try {
			editor = EditorFactory.createEditor(noteWithScoreNoteOnly, prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}

		EditorWindow w =
			new EditorWindow("Test ExpandEditor with null property");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Method for testing the creation of a new propertyValue in CollectionEditor.
	 */
	public static void testCollectionWithNullPropertyAndElements() {
		CollectionWithChildren coll = new CollectionWithChildren(null, 2);
		for (int i = 0; i < 3; i++)
			coll.add("Element" + (i + 1));

		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(coll);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w =
			new EditorWindow("Test Collection with null property and elements");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Method for testing the creation of a new complex propertyValue in CollectionEditor.
	 */
	public static void testCollectionWithComplexNullPropertyAndElements() {
		CollectionWithComplexChildren coll =
			new CollectionWithComplexChildren(null, 2);
		for (int i = 0; i < 3; i++)
			coll.add("Element" + (i + 1));

		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(coll);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w =
			new EditorWindow("Test Collection with complex null property and elements");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Inner class for method <code>testCollectionWithComplexNullPropertyAndElements()</code>.
	 */
	public static class CollectionWithComplexChildren extends Vector {
		private PerformanceNote field1;
		private int field2;

		public CollectionWithComplexChildren(PerformanceNote arg1, int arg2) {
			field1 = arg1;
			field2 = arg2;
		}

		public void setField1(PerformanceNote arg1) {
			field1 = arg1;
		}

		public PerformanceNote getField1() {
			return field1;
		}

		public void setField2(int arg2) {
			field2 = arg2;
		}

		public int getField2() {
			return field2;
		}

	}

	/**
	 * For testing method <code>displayCreateButton</code> in class 
	 * <code>SimpleTextfieldEditor</code>.
	 */
	public static void testCreateSimpleNullProperty() {
		Editor panelEditor = null;
		try {
			panelEditor =
				EditorFactory.createEditor(new ObjectWithSimpleNullProperty());
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow windowPanel = new EditorWindow("Simple null property");
		windowPanel.addEditor(panelEditor);
		windowPanel.show();
	}

	/**
	 * Inner class which provides an object for testmethod 
	 * <code>testCreateSimpleNullProperty()</code>.
	 */
	public static class ObjectWithSimpleNullProperty {
		private String string1 = "StringProperty";
		private String string2 = null;
		private Integer integerObject = null;

		public String getString1() {
			return string1;
		}

		public String getString2() {
			return string2;
		}

		public Integer getIntegerObject() {
			return integerObject;
		}

		public void setString1(String string) {
			string1 = string;
		}

		public void setString2(String string) {
			string2 = string;
		}

		public void setIntegerObject(Integer integer) {
			integerObject = integer;
		}

		@Override
		public String toString() {
			StringBuffer stringBuf = new StringBuffer(string1);
			if (string2 != null)
				stringBuf.append("\t" + string2);
			else
				stringBuf.append("string2 null");
			if (integerObject != null)
				stringBuf.append("\t" + integerObject);
			else
				stringBuf.append("integerObject null");
			return stringBuf.toString();
		}

	}

	public static void main(String argv[]) {
		new ExitWindow().show();
		testNoteWithOnePropertyNull(); //nach Ergaenzungen 10.11.03 o.k.
		testExpandEditorWithNullProperty(); //11.11.03 o.k.
		testCollectionWithNullPropertyAndElements(); //17.11.03 o.k.
		testCollectionWithComplexNullPropertyAndElements(); //17.11.03 o.k.
		testCreateSimpleNullProperty(); //11.11.03 o.k.
	}

}
