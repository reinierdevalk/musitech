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
 * Created on 23.06.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.collection.TypedCollection;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * JUnit test for the <code>EditorFactory</code>.
 * <br>
 * <br> Use the main method to play around with the windows.
 * 
 * @author Tobias Widdra
 */
public class TestEditorFactory  {

	static ScoreNote scoreNote = new ScoreNote(new Rational(1, 2), new Rational(1, 2), 'c', (byte) 0, (byte) 0);
	static Note note = new Note(scoreNote, new PerformanceNote());
	static Note note2 = new Note(new ScoreNote(), new PerformanceNote());
	static ScoreNote testDefaultScoreNote = new ScoreNote();

	/**
	 * Tests EditorFactory.createEditor(Object,EditingProfile) with <code>scoreNote</code>.
	 *
	 */
	public static void testCreateByProfile() {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(scoreNote, scoreNote.getEditingProfile());
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test By Profile");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Test EditorFactory.createEditor(Object) with <code>note</code>.
	 * <br>
	 * <br> The note and the changes after closing the window are displayed on
	 * System.out.
	 *
	 */
	public static void testNoteEditor() {
		System.out.println("note: " + note);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(note);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Note Editor");
		w.addEditor(editor);
		w.getEditorPanel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("note: " + note);
			}
		});
		w.show();
	}

	/**
	 * Test for a PopUpEditor: EditorWindow for <code>note</code> should pop up.
	 *
	 */
	public static void testPopUpEditor() {

		//		PopUpEditor editor = new PopUpEditor();
		//Modif291003
		Editor editor = null;
		EditingProfile prof = new EditingProfile(null, "PopUp", "scoreNote");
		try {
			editor = EditorFactory.createEditor(note, prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow w = new EditorWindow("Test Pop Up");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Test for EditorFactory.createDefaultProfile(Object).
	 * <br>
	 * <br> The editing profile of scoreNote does not contain TiedNote, but the
	 * default mechansim per reflection finds it.
	 * so at the level where the default comes into play an editor for
	 * TiedNote is displayed...
	 *
	 */
	public static void testCreateDefaultProfile() {

		EditingProfile profile = EditorFactory.createDefaultProfile(testDefaultScoreNote);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(testDefaultScoreNote, profile);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}

		EditorWindow w = new EditorWindow("Default Profile");
		w.addEditor(editor);

		/*
		 * EditingProfile of scoreNote does not contain TiedNote, but the
		 * default mechanism per reflection finds it.
		 * so at the level where the default comes into play an editor for
		 * TiedNote is displayed...
		 */
		System.out.println(
			"test default profile: " + testDefaultScoreNote + " TiedNote: " + testDefaultScoreNote.getTiedNote());
		w.getEditorPanel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(
					"test default profile: "
						+ testDefaultScoreNote
						+ " TiedNote: "
						+ testDefaultScoreNote.getTiedNote());
			}
		});
		w.show();
	}

	/**
	 * A test for EditorFactory.createEditor(Object) with several objects,
	 * some having a profile others not.
	 *
	 */
	public static void testCreateEditor() {

//				EditorWindow w1 = new EditorWindow("Chord Editor");	//TODO
//				Chord chord = new Chord();
//				Editor editor = null;
//				try {
//					editor = EditorFactory.createEditor(chord);
//				} catch (EditorConstructionException e) {
//					System.err.println(e.getMessage());
//				}
//				w1.addEditor(editor);
//				w1.show();

		EditorWindow w2 = new EditorWindow("Piece Editor");
		Editor editor2 = null;
		try {
			editor2 = EditorFactory.createEditor(new Piece());
		} catch (EditorConstructionException e1) {
			System.err.println(e1.getMessage());
		}
		w2.addEditor(editor2);
		w2.show();

		EditorWindow w3 = new EditorWindow("Time Signature");
		Editor editor3 = null;
		try {
			editor3 = EditorFactory.createEditor(new TimeSignature());
		} catch (EditorConstructionException e2) {
			System.err.println(e2.getMessage());
		}
		w3.addEditor(editor3);
		w3.show();

		EditorWindow w4 = new EditorWindow("Note Editor");
		Editor editor4 = null;
		try {
			editor4 = EditorFactory.createEditor(new Note(scoreNote, new PerformanceNote()));
		} catch (EditorConstructionException e3) {
			System.err.println(e3.getMessage());
		}
		w4.addEditor(editor4);
		w4.show();
	}

	/**
	 * Test for CollectionEditor with a vector filled with ScoreNotes.
	 *
	 */
	public static void testCollection() {

		Vector v = new Vector();
		for (int i = 0; i < 5; i++) {
			v.add(new ScoreNote(new Rational(i, 4), new Rational(1, 1), 'a', (byte) i, (byte) 0));
		}
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(v);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Collection Editor");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * A test for CollectionEditor with a Hashtable filled with ScoreNotes
	 * hashed by <code>"key "+i</code>.
	 *
	 */
	public static void testHashtable() {

		Hashtable h = new Hashtable();
		for (int i = 0; i < 5; i++) {
			h.put("key " + i, new ScoreNote(new Rational(i, 4), new Rational(1, 1), 'a', (byte) i, (byte) 0));
		}
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(h);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Hashtable Editor");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Test for EditorFactory.getEditorType() with several object classes.
	 *
	 */
	public static void testGetEditorType() {
//		System.out.println("byte -> \t" + EditorFactory.getEditorType(byte.class));
//		System.out.println("char -> \t" + EditorFactory.getEditorType(char.class));
//		System.out.println("String-> \t" + EditorFactory.getEditorType(String.class));
//		System.out.println("Rational-> \t" + EditorFactory.getEditorType(Rational.class));
//		System.out.println("Vector -> \t" + EditorFactory.getEditorType(Vector.class));
//		System.out.println("Hashtable -> \t" + EditorFactory.getEditorType(Hashtable.class));
//		System.out.println("Note -> \t" + EditorFactory.getEditorType(Note.class));
//		System.out.println("Byte -> \t" + EditorFactory.getEditorType(Byte.class));
//		System.out.println("Piece -> \t" + EditorFactory.getEditorType(Chord.class));
//		System.out.println("ScoreNote-> \t" + EditorFactory.getEditorType(ScoreNote.class));
	}

	/**
	 * A test for the PanelEditor with a note.
	 * <br>
	 * <br> The note is displayed on System.out during construction and after closing the window.
	 *
	 */
	public static void testPanelEditor() {
		EditingProfile snProfile = new ScoreNote().getEditingProfile();
		snProfile.setPropertyName("scoreNote");
		EditingProfile pfProfile = new EditingProfile("Performance Note", "PopUp", "performanceNote");
		EditingProfile nProfile = new EditingProfile("Note", new EditingProfile[] { snProfile, pfProfile }, "Panel");
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(note2, nProfile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow w = new EditorWindow("PanelEditor test");
		w.addEditor(editor);
		System.out.println("test panel editor: " + note2);
		w.getEditorPanel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("test panel editor: " + note2);
			}
		});
		w.show();
	}

	/**
	 * Tests Editing a String.
	 */
	public static void testStringEditor() {
		String testString = "ABCD";
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(testString);
			//			if (AbstractSimpleEditor.class.isAssignableFrom(editor.getClass()))
			//				 ((AbstractSimpleEditor) editor).setOutmostEditorOption(true);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("StringEditor test");
		w.addEditor(editor);
		w.show();
		System.out.println("String: " + testString);
	}

	/**
	 * Tests if the EditorFactory can create an IntEditor for a number wrapped in an
	 * Integer object.
	 */
	public static void testIntEditor() {
		int intPrimitive = 5;
		final Integer intWrapped = new Integer(intPrimitive);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(intWrapped);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("IntEditor - Test with WrapperClass");
		w.addEditor(editor);
		w.show();
		System.out.println("Integer: " + intWrapped.toString());
	}

	/**
	 * Tests if the EditorFactory can create a ByteEditor for a number wrapped in an
	 * Byte object.
	 */
	public static void testByteEditor() {
		byte bytePrimitive = 1;
		final Byte byteWrapped = new Byte(bytePrimitive);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(byteWrapped);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("ByteEditor - Test with WrapperClass");
		w.addEditor(editor);
		w.show();
		System.out.println("Byte: " + byteWrapped.toString());
	}

	/**
	 * Tests if the EditorFactory can create a ByteEditor for a number wrapped in an
	 * Byte object.
	 */
	public static void testLongEditor() {
		long longPrimitive = 5;
		final Long longWrapped = new Long(longPrimitive);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(longWrapped);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("LongEditor - Test with WrapperClass");
		w.addEditor(editor);
		w.show();
		System.out.println("Long: " + longWrapped.toString());
	}

	/**
	 * Test for editing IntegerObjects contained in a Vector.
	 */
	public static void testEditingIntegers() {
		Vector integers = new Vector();
		for (int i = 0; i < 5; i++) {
			integers.add(new Integer(i));
		}
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(integers);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("IntEditor - Test with IntegerObjects");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Tests creating an editor for a Rational as editObj (not as propertyValue).
	 */
	public static void testRationalAsEditObj() {
		Rational testRational = new Rational(2, 5);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(testRational);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("RationalEditor - Test Rational as editObj");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * This window exits the program when closed.
	 * <br>
	 * <br> It is used since the EditorWindows do not do this when closed.
	 * @author Tobias Widdra
	 */
	public static class ExitWindow extends javax.swing.JFrame {
		public ExitWindow() {
			super("I exist for you to exit the program :o)");
			setSize(350, 0);
			addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				}
			});
		}
	}

	/**
	 * Tests ExpandEditor.
	 *
	 */
	public static void testExpandEditor() {

		//		ExpandEditor editor = new ExpandEditor();
		//		editor.construct(note, "ScoreNote", null, "Score Note");
		Editor editor = null;
		EditingProfile prof = new EditingProfile(null, "Expand", "scoreNote");
		try {
			editor = EditorFactory.createEditor(note, prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow w = new EditorWindow("Test ExpandEditor");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Testing CollectionEditor for a Collection which has elements AND children.
	 * Class CollectionWithChildren (inner class of TestEditorFactory) provides 
	 * a simple collection of the required characteristics. 
	 */
	public static void testCollectionWithElementsAndChildren() {
		CollectionWithChildren coll = new CollectionWithChildren("EINS", 2);
		for (int i = 0; i < 3; i++)
			coll.add("Element" + (i + 1));

		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(coll);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test Collection with Elements and Children");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Inner class to provide a collection having properties and elements.
	 * Required for testing CollectionEditor in testCollectionWithElementsAndChildren().
	 *  @author Kerstin Neubarth
	 *
	 */
	public static class CollectionWithChildren extends Vector {
		private String field1;
		private int field2;

		public CollectionWithChildren(String arg1, int arg2) {
			field1 = arg1;
			field2 = arg2;
		}

		public void setField1(String arg1) {
			field1 = arg1;
		}

		public String getField1() {
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
	 * Testing MapEditor for a Map which has elements AND children.
	 * Class MapWithChildren (inner class of TestEditorFactory) provides 
	 * a simple map of the required characteristics. 
	 */
	public static void testMapWithElementsAndChildren() {
		MapWithChildren map = new MapWithChildren("EINS", 2);
		for (int i = 0; i < 3; i++)
			map.put("Key" + i, "Element " + i);
		map.put("Score Note", scoreNote);
		map.put("Note 1", note);
		map.put("Note 2", note2);

		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(map);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test Map with Elements and Children");
		w.addEditor(editor);
		w.show();

		//just for checking
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key + " " + map.get(key).toString() + " " + map.get(key).getClass().toString());
		}

	}

	/**
	 * Inner class to provide a map having properties and elements.
	 * Required for testing MapEditor in testMapWithElementsAndChildren().
	 */

	public static class MapWithChildren extends HashMap {
		private String field1;
		private int field2;

		public MapWithChildren(String arg1, int arg2) {
			field1 = arg1;
			field2 = arg2;
		}

		public void setField1(String arg1) {
			field1 = arg1;
		}

		public String getField1() {
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
	 * Tests editing WrapperObjects (Byte, Integer, Float, Long) as properties of an object.
	 * Class ObjectWithWrapperProperties (inner class of TestEditorFactory) provides 
	 * an object of the required characteristics. 
	 */
	public static void testWrapperAsProperty() {
		ObjectWithWrapperProperties testObject = new ObjectWithWrapperProperties();
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(testObject);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test Object with Wrapper properties");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Inner class to provide an object having Wrapper for primitive data types as properties.
	 * Required for testWrapperAsProperty().
	 */
	public static class ObjectWithWrapperProperties {
		private Byte byteWrap = new Byte("0");
		private Integer intWrap = new Integer(1);
		private Float floatWrap = new Float("0");
		private Long longWrap = new Long(0);

		public Byte getByteWrap() {
			return byteWrap;
		}

		public Float getFloatWrap() {
			return floatWrap;
		}

		public Integer getIntWrap() {
			return intWrap;
		}

		public Long getLongWrap() {
			return longWrap;
		}

		public void setByteWrap(Byte byte1) {
			byteWrap = byte1;
		}

		public void setFloatWrap(Float float1) {
			floatWrap = float1;
		}

		public void setIntWrap(Integer integer) {
			intWrap = integer;
		}

		public void setLongWrap(Long long1) {
			longWrap = long1;
		}

		public String toString() {
			StringBuffer stringBuffer = new StringBuffer(byteWrap.toString() + "\t");
			stringBuffer.append(intWrap.toString() + "\t");
			stringBuffer.append(floatWrap.toString() + "\t");
			stringBuffer.append(longWrap.toString());
			return stringBuffer.toString();
		}

	}

	/**
	 * Method to test the handling of null-propertyValues in simple and complex children editors.
	 * (The complex children first are shown as PopUpEditors. This editor at the present state
	 * has its own "create"-functionality not recurring on AbstractComplexEditor.)
	 * A simple Object with the required characteristics is provided by inner class
	 * ObjectWithMixedChildren.
	 */
	public static void testPanelEditorWithMixedChildren() {
		ObjectWithMixedChildren testObject = new ObjectWithMixedChildren();
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(testObject);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test Object with mixed properties");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Inner class to provide objects with "simple" as well as "complex" properties
	 * some of them being null.
	 * Required for testPanelEditorWithMixedChildren().
	 */
	public static class ObjectWithMixedChildren {
		private int intField = 3;
		private Integer integerField = null;
		private Note note = null;

		public Integer getIntegerField() {
			return integerField;
		}

		public int getIntField() {
			return intField;
		}

		public Note getNote() {
			return note;
		}

		public void setIntegerField(Integer integer) {
			integerField = integer;
		}

		public void setIntField(int i) {
			intField = i;
		}

		public void setNote(Note note) {
			this.note = note;
		}

		public String toString() {
			String string = "";
			string += getClass().toString();
			string += "  " + intField;
			if (integerField != null)
				string += "  " + integerField.toString();
			else
				string += "  integerField null";
			if (note != null)
				string += "  " + note.toString();
			else
				string += "  note null";
			return string;
		}

	}

	/**
	 * Method to test the functioning of CollectionEditor for a List,
	 * that is for a Collection having a defined order.
	 * The elements of the List can be addressed by their index.
	 */
	public static void testCollectionEditorWithList() {
		ArrayList list = new ArrayList();
		for (int i = 0; i < 5; i++)
			list.add(i, "Element at Index " + i);

		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(list);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test editing a list");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Method for testing CollectionEditor with a TypedCollection.
	 * With a TypedCollection, in case of adding a new element, the editor
	 * is to offer a selection of possible types.
	 */
	public static void testCollectionEditorWithTypedCollection() {
		Class[] types = { String.class, Note.class, PerformanceNote.class, ScoreNote.class };
		TypedCollection coll = new TypedCollection(types);
		coll.add(scoreNote);
		coll.add(note);
		coll.add(note2);
		coll.add("String");

		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(coll);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test editing typed collection");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Method for testing ExpandEditor as an outmost editor.
	 * In this case, the ExpandEditor is shown in a JScrollPane,
	 * while the editorToExpand (PanelEditor) does not have its 
	 * own scroll pane.
	 */
	public static void testExpandEditorAsOutmost() {
		//		ExpandEditor editor = new ExpandEditor();
		//		editor.construct(new ScoreNote(), null, null, "Score Note");
		Editor editor = null;
		EditingProfile prof = new EditingProfile("scoreNote", "Expand", null);
		try {
			editor = EditorFactory.createEditor(new ScoreNote(), prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}

		EditorWindow w = new EditorWindow("Test ExpandEditor As Outmost");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Method for testing the PreviewEditor in editing the editObj itself.
	 */
	public static void testPreviewEditorWithEditObj() {
		//		PreviewEditor previewEditor = new PreviewEditor();
		//		previewEditor.construct(new PerformanceNote(), null, "PerformanceNote");

		Editor previewEditor = null;
		EditingProfile prof =
			//			new EditingProfile("PerformanceNote", "Preview", null);
	new EditingProfile(null, "Preview", null);
		try {
			previewEditor = EditorFactory.createEditor(new PerformanceNote(), prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow w = new EditorWindow("Test PreviewEditorWithEditObj");
		w.addEditor(previewEditor);
		w.show();
	}

	/**
	 * Method for testing the PreviewEditor in editing a property of editObj.
	 */
	public static void testPreviewEditorWithProperty() {
		Editor previewEditor = null;
		EditingProfile prof = new EditingProfile(null, "preview", "performanceNote");
		try {
			previewEditor = EditorFactory.createEditor(note, prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow w = new EditorWindow("Test PreviewEditorWithProperty");
		w.addEditor(previewEditor);
		w.show();
	}

	/**
	 * Tests adding more than one editor (in this case, two editors) to the same
	 * EditorPanel.
	 */
	public static void testEditorPanelWithTwoEditors() {
		Editor editor1 = null;
		Editor editor2 = null;
		try {
			editor1 = EditorFactory.createEditor(scoreNote);
			editor2 = EditorFactory.createEditor(note2);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test EditorPanel with 2 editors");
		w.addEditor(editor1);
		w.addEditor(editor2);
		w.show();
	}

	/**
	 * Method for testing the input check in RationalEditor for a default
	 * ScoreNote. Here, Metrical and Duration at the beginning are null.
	 * The RationalEditor should allow empty input in both fields of the
	 * RationalEditor but not in only one of them.
	 * If the input is not complete or is not allowed (e.g. denominator 0)
	 * an ErrorDialog is shown and the focus is reset to the corresponding 
	 * textfield.
	 */
	public static void testRationalEditorInScoreNote() {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(new ScoreNote());
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		EditorWindow w = new EditorWindow("Test RationalEditor input check");
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Test for method <code>createEditor(Object, Editor, String)</code> where the String
	 * argument determines the editortype to be created.
	 */
	public static void testCreateEditorWithArgumentEditortype() {
		//specified editortype: panel
		Editor panelEditor = null;
		try {
			panelEditor = EditorFactory.createEditor(scoreNote, null, "panel");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow windowPanel = new EditorWindow("EditorType Panel");
		windowPanel.addEditor(panelEditor);
		windowPanel.show();
		//specified editortype: popup
		Editor popupEditor = null;
		try {
			popupEditor = EditorFactory.createEditor(scoreNote, null, "PopUp");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (popupEditor != null) {
			EditorWindow windowPopup = new EditorWindow("EditorType PopUp");
			windowPopup.addEditor(popupEditor);
			windowPopup.show();
		}
		//specified editortype: expand
		Editor expandEditor = null;
		try {
			expandEditor = EditorFactory.createEditor(scoreNote, null, "Expand");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (expandEditor != null) {
			EditorWindow windowExpand = new EditorWindow("EditorType Expand");
			windowExpand.addEditor(expandEditor);
			windowExpand.show();
		}
		/*
		 * Note:
		 * The EditingProfile of a ScoreNote does not contain editortypes "PopUp"
		 * and "Expand". So, for these types as arguments, no editor is created.
		 * To create editors of these types,
		 * method createEditor(Object, EditingProfile, String, Editor) should be
		 * used with an appropriate EditingProfile.
		 */
	}

	/**
	 * For testing the preview texttfield's update when PreviewEditors are used
	 * as children editors.
	 */
	public static void testPreviewEditorsAsChildren() {
		PerformanceNote perf = new PerformanceNote();
		EditingProfile profPerf = perf.getEditingProfile();
		if (profPerf != null) {
			profPerf.setEditortype("Preview");
			profPerf.setPropertyName("performanceNote");
		}
		ScoreNote score = new ScoreNote();
		EditingProfile profScore = score.getEditingProfile();
		if (profScore != null) {
			profScore.setEditortype("Preview");
			profScore.setPropertyName("scoreNote");
		}
		EditingProfile[] profChildren = { profScore, profPerf };
		Note note1 = new Note(score, perf);
		//		EditingProfile profNote = note1.getEditingProfile();
		//		profNote.setChildren(profChildren);
		Editor panelEditor = null;
		EditingProfile profNote = new EditingProfile("Note", profChildren);
		try {
			panelEditor = EditorFactory.createEditor(note1, profNote);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow windowPanel = new EditorWindow("PreviewEditors as Children");
		windowPanel.addEditor(panelEditor);
		windowPanel.show();
	}

	/**
	 * Tests method <code>createEditor(Object, EditingProfile, String, Editor)</code>
	 * of class <code>EditorFactory</code>.
	 */
	public static void testCreateEditorWithProfileAndType() {
		//create editObj
		ScoreNote score = new ScoreNote();
		//create EditingProfile
		String[] editortypes = { "Panel", "PopUp", "Preview", "Expand" };
		EditingProfile profile = new EditingProfile("ScoreNote", editortypes, null);
		Editor editor1 = null;
		Editor editor2 = null;
		Editor editor3 = null;
		Editor editor4 = null;
		Editor editor5 = null;
		try {
			//TODO ersetzen durch createEditor2(...)
			editor1 = EditorFactory.createEditor(score, profile, "PopUp", null);
			editor2 = EditorFactory.createEditor(score, profile, "Panel", null);
			editor3 = EditorFactory.createEditor(score, profile, "Expand", null);
			editor4 = EditorFactory.createEditor(score, profile, "Preview", null);
			editor5 = EditorFactory.createEditor(score, profile, "Map", null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow window1 = new EditorWindow("EditorType PopUp");
		window1.addEditor(editor1);
		window1.show();
		EditorWindow window2 = new EditorWindow("EditorType Panel");
		window2.addEditor(editor2);
		window2.show();
		EditorWindow window3 = new EditorWindow("EditorType Expand");
		window3.addEditor(editor3);
		window3.show();
		EditorWindow window4 = new EditorWindow("EditorType Preview");
		window4.addEditor(editor4);
		window4.show();
		if (editor5 != null) {
			EditorWindow window5 = new EditorWindow("EditorType Map");
			window5.addEditor(editor5);
			window5.show();
		}
	}

	/**
	 * Tests creating and displaying an IconEditor. 
	 */
	public static void testIconEditor() {
		EditingProfile profile = new EditingProfile("ScoreNote", "icon", null);
		Icon icon =
			new ImageIcon("C:\\Programme\\eclipse\\workspace\\musitech\\de\\uos\\fmt\\musitech\\music\\display\\icons\\toll_tempo.gif");
		profile.setIcons(icon);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(scoreNote, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor != null) {
			EditorWindow window = new EditorWindow("Test IconEditor");
			window.addEditor(editor);
			window.show();
		}
	}

	/**
	 * Tests an IconEditor handling a <code>propertyValue</code> of null.
	 */
	public static void testIconEditorWithNullProperty() {
		Icon icon =
			new ImageIcon("C:\\Programme\\eclipse\\workspace\\musitech\\de\\uos\\fmt\\musitech\\music\\display\\icons\\toll_tempo.gif");
		EditingProfile profile = new EditingProfile("Note", "icon", "performanceNote");
		profile.setIcons(icon);
		Note editObj = new Note(scoreNote, null);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor != null) {
			EditorWindow window = new EditorWindow("Test IconEditor");
			window.addEditor(editor);
			window.show();
		}
	}

	public static void testReadOnlyEditor() {
		//inner class providing an editObj
		final class ObjectWithRationalProperty {
			public int intProperty; //public to allow ReflectionAccess
			public Rational rationalProperty; //public to allow ReflectionAccess
			public ObjectWithRationalProperty() {
				intProperty = 25;
				rationalProperty = new Rational(3, 4);
			}
		}
		ObjectWithRationalProperty editObj = new ObjectWithRationalProperty();
		Editor editor = null;
		EditingProfile profile = EditorFactory.getOrCreateProfile(editObj);
		profile.setReadOnly(true);
		try {
			editor = EditorFactory.createEditor(editObj, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor != null) {
			EditorWindow window = new EditorWindow("Test ReadOnly Editor");
			window.addEditor(editor);
			window.show();
		}
	}

	public static void testReadOnlyEditorForScoreNote() {
		//provide editObj
		ScoreNote editObj = new ScoreNote();
		EditingProfile profile = editObj.getEditingProfile();
		profile.setReadOnly(true);
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor != null) {
			EditorWindow window = new EditorWindow("Test ScoreNote ReadOnly");
			window.addEditor(editor);
			window.show();
		}
	}

	/**
	 * @param argv
	 */
	/**
	 * A main class for displaying the several test windows since JUnit does
	 * not do this.
	 * @param argv
	 */
	public static void main(String argv[]) {
		new ExitWindow().show();
//					testNoteEditor();	//10.11.03 o.k. 
//					testCreateByProfile();	//10.11.03 o.k.
//					testPopUpEditor();	//11.11.03 o.k.
//			testCreateDefaultProfile();	//18.11.03 o.k.
//			testCreateEditor();
//			testCollection(); 	//14.11.03 o.k.
//			testHashtable();	//14.11.03 o.k.
//			testGetEditorType();	//deprecated
//			testPanelEditor();	//14.11.03 o.k.		TODO retesting nach Änderung in AbstractComplexEditor??
//			testExpandEditor();	//11.11.03 o.k.	
//			testStringEditor();
//			testIntEditor();
//			testByteEditor();
//			testLongEditor();
//			testEditingIntegers();	//11.11.03 o.k.
//			testRationalAsEditObj();	//11.11.03 o.k.
//					testCollectionWithElementsAndChildren();	//17.11.03 o.k.
//					testMapWithElementsAndChildren();	//17.11.03 o.k.
//			testWrapperAsProperty();	//11.11.03 o.k.
//					testPanelEditorWithMixedChildren();	//17.11.03 o.k.
//					testCollectionEditorWithList();	//17.11.03 o.k.
//					testCollectionEditorWithTypedCollection();	//17.11.03 o.k.
//			testExpandEditorAsOutmost();	//11.11.03 o.k.	
//				testPreviewEditorWithEditObj(); //11.11.03 o.k.
//				testPreviewEditorWithProperty(); //11.11.03 o.k.
//			testEditorPanelWithTwoEditors();	//11.11.03 o.k.
//			testRationalEditorInScoreNote();	//17.11.03 o.k.
//			testCreateEditorWithArgumentEditortype(); //11.11.03 o.k.
//					testPreviewEditorsAsChildren();	//14.11.03 o.k.
//					testCreateEditorWithProfileAndType();
//				testIconEditor();	//icon nicht mehr vorhanden
//				testIconEditorWithNullProperty();
//		testReadOnlyEditor();	//TODO 
		testReadOnlyEditorForScoreNote();	//TODO Exception wenn OK
	}
}
