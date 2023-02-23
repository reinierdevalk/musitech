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
 * Created on 13.04.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JComponent;
import junit.framework.TestCase;
import de.uos.fmt.musitech.data.media.image.ImageURL;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbolSequence;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.metadata.MetaDataEditor;
import de.uos.fmt.musitech.metadata.MetaDataItemEditor;
import de.uos.fmt.musitech.structure.harmony.gui.ChordSymbSeqDisplay;
import de.uos.fmt.musitech.structure.harmony.gui.ChordSymbolDisplay2;
import de.uos.fmt.musitech.structure.text.LyricsDisplay;
import de.uos.fmt.musitech.structure.text.LyricsSyllableDisplay;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * JUnit tests for class <code>EditorFactory</code>.
 * 
 * @author Kerstin Neubarth
 *
 */
public class EditorFactoryTest extends TestCase {

	/**
	 * Tests the setting of editortypes in method <code>createDefaultProfile()</code>.
	 * 
	 */
	public void testCreateDefaultProfile() {
		String editObj = "Test-String";
		EditingProfile profile =
			EditorFactory.createDefaultProfile(editObj, null);
		assertNull(profile.getPropertyName());
		String[] editortypes = profile.getEditortypes();
//		assertEquals(editortypes.length, 2);	//jetzt auch default types
		assertTrue(Arrays.asList(editortypes).contains("String"));
		assertTrue(Arrays.asList(editortypes).contains("Text"));
		assertNull(profile.getChildren());
		Integer testInt = new Integer(5);
		//		EditingProfile profileInt = EditorFactory.createDefaultProfile(testInt);
		EditingProfile profileInt =
			EditorFactory.createDefaultProfile(testInt, null);
//		assertEquals(profileInt.getEditortypes().length, 1);	//außerdem default types
		//		assertEquals(profileInt.getEditortypes()[0], "int");
		assertEquals(profileInt.getEditortypes()[0], "Int");
	}

	/**
	 * Test method <code>getDefaultEditorType()</code> for a case in which the
	 * <code>editObj</code> itself is to be edited.
	 */
	public void testGetDefaultEditorTypeForEditObj() {
		String editObj = "Test-String";
		//check editortype derived from class of editObj
		String defaultWithoutProfile =
			EditorFactory.getDefaultEditorType(editObj, null);
		assertEquals(defaultWithoutProfile, "String");
		//check default editortype derived from profile	
		EditingProfile profile = new EditingProfile();
		profile.setEditortype(new String[] { "PopUp", "Preview", "String" });
		String defaultWithProfile =
			EditorFactory.getDefaultEditorType(editObj, profile);
		assertEquals(defaultWithProfile, "PopUp");
	}

	/**
	 * Tests method <code>getDefaultEditorType()</code> for a case in which a
	 * property of the <code>editObj</code> is to be edited.
	 */
	public void testGetDefaultEditorTypeForProperty() {
		PerformanceNote editObj = new PerformanceNote();
		//check editortype derived from class of editObj
		String defaultWithoutProfile =
			EditorFactory.getDefaultEditorType(editObj, null);
		assertEquals(defaultWithoutProfile, "Panel");
		//check default editortype for property pitch
		EditingProfile profile = new EditingProfile("pitch");
		String defaultForPitch =
			EditorFactory.getDefaultEditorType(editObj, profile);
		assertEquals(defaultForPitch, "Byte");
	}

	/**
	 * Tests method <code>createDefaultProfile()</code>.
	 */
	public void testCreateDefaultProfile2() {
		//profile for complex editObj 
		PerformanceNote editObj = new PerformanceNote();
		EditingProfile profile1 =
			EditorFactory.createDefaultProfile(editObj, null);
		assertEquals(profile1.getLabel(), "Performance Note");
		assertNull(profile1.getPropertyName());
		assertEquals(profile1.getEditortypes()[0], "Panel");
		assertEquals(
			EditorFactory.getDefaultEditorType(editObj, null),
			"Panel");
		assertNotNull(profile1.getChildren());
		ReflectionAccess ref =
			ReflectionAccess.accessForClass(editObj.getClass());
//		assertEquals(
//			profile1.getChildren().length,
//			ref.getPropertyNames().length);	ref... liefert 1 mehr, weil auch für uid
		//profile for property of complex editObj
		EditingProfile profile2 =
			EditorFactory.createDefaultProfile(editObj, "pitch");
		assertEquals(profile2.getPropertyName(), "pitch");
		assertEquals(profile2.getLabel(), "pitch");
		assertNull(profile2.getChildren());
	}

	/**
	 * Tests method <code>createEditor(Object)</code>.
	 */
	public void testCreateEditorWithDefaultProfile() {
		//editObj of type Rational
		Rational editObj1 = new Rational(1, 4);
		Editor editor1 = null;
		try {
			editor1 = EditorFactory.createEditor(editObj1);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor1 instanceof AbstractSimpleEditor);
		assertEquals(editor1.getClass(), RationalEditor.class);
		//editObj of type char
		Character editObj2 = new Character('c');
		Editor editor2 = null;
		try {
			editor2 = EditorFactory.createEditor(editObj2);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor2 instanceof AbstractSimpleEditor);
		assertEquals(editor2.getClass(), CharEditor.class);
		//editObj of type MetaDataItem
		MetaDataItem editObj3 = new MetaDataItem("Key");
		editObj3.setMetaValue(new MetaDataValue());
		Editor editor3 = null;
		try {
			editor3 = EditorFactory.createEditor(editObj3);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor3 instanceof AbstractEditor);
		assertEquals(editor3.getClass(), MetaDataItemEditor.class);
		//editObj of type MetaDataCollection
		MetaDataCollection editObj4 = new MetaDataCollection();
		editObj4.addMetaDataItem(editObj3);
		Editor editor4 = null;
		try {
			editor4 = EditorFactory.createEditor(editObj4);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor4 instanceof AbstractEditor);
		assertEquals(editor4.getClass(), MetaDataEditor.class);
		//editObj of type ImageURL
//		ImageURL editObj5 = new ImageURL(); //TODO
//		Editor editor5 = null;
//		try {
//			editor5 = EditorFactory.createEditor(editObj5);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//			fail();
//		}
//		//check editor
//		assertEquals(editor5.getClass(), ImageDisplay.class);
		//editObj of type NoteList
//		NoteList editObj6 = new NoteList();
//		//		Editor editor6 = null;
//		Display editor6 = null;
//		try {
//			//editor6 = EditorFactory.createEditor(editObj6);
//			editor6 = EditorFactory.createDisplay(editObj6, null, null, null);	//TODO erzeugt IndexOutOfBoundsException
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//			fail();
//		}
//		//check editor
//		assertEquals(editor6.getClass(), NotationDisplay.class);
		//editObj of type Byte
		Byte editObj7 = new Byte("1");
		Editor editor7 = null;
		try {
			editor7 = EditorFactory.createEditor(editObj7);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor7 instanceof AbstractSimpleEditor);
		assertEquals(editor7.getClass(), ByteEditor.class);
		//editObj of type Long
		Long editObj8 = new Long(1);
		Editor editor8 = null;
		try {
			editor8 = EditorFactory.createEditor(editObj8);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor8 instanceof AbstractSimpleEditor);
		assertEquals(editor8.getClass(), LongEditor.class);
		//editObj of type ChordSymbol
		ChordSymbol editObj9 = new ChordSymbol(new Rational(0, 1), 1);
		//		Editor editor9 = null;
		Display editor9 = null;
		try {
			//			editor9 = EditorFactory.createEditor(editObj9);
			editor9 = EditorFactory.createDisplay(editObj9, null, null, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertEquals(editor9.getClass(), ChordSymbolDisplay2.class);
	}

	/**
	 * Tests method <code>createEditor(Object, EditingProfile)</code>.
	 */
	public void testCreateEditorWithProfile() {
		//edit property pitch of PerformanceNote
		PerformanceNote editObj1 = new PerformanceNote();
		EditingProfile profile1 = new EditingProfile("pitch");
		Editor editor1 = null;
		try {
			editor1 = EditorFactory.createEditor(editObj1, profile1);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor1 instanceof AbstractSimpleEditor);
		assertEquals(editor1.getClass(), ByteEditor.class);
		assertNotNull(editor1.getEditingProfile().getPropertyName());
		//edit property PerformanceNote of Note in editor of default editortype (PopUp)
		Note editObj2 = new Note();
		EditingProfile profile2 = new EditingProfile("performanceNote");
		Editor editor2 = null;
		try {
			editor2 = EditorFactory.createEditor(editObj2, profile2);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor2 instanceof AbstractComplexEditor);
		assertEquals(editor2.getClass(), PopUpEditor.class);
		//cf. defaultPropertyEditorType
		assertNotNull(editor2.getEditingProfile().getPropertyName());
		//edit property PerformanceNote of Note in PanelEditor
		Note editObj3 = new Note();
		EditingProfile profile3 = new EditingProfile("performanceNote");
		profile3.setEditortype("Panel");
		Editor editor3 = null;
		try {
			editor3 = EditorFactory.createEditor(editObj3, profile3);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor3 instanceof AbstractComplexEditor);
		assertEquals(editor3.getClass(), PanelEditor.class);
		assertNotNull(editor3.getEditingProfile().getPropertyName());
		//edit PeformanceNote in ExpandEditor
		PerformanceNote editObj4 = new PerformanceNote();
		EditingProfile profile4 = new EditingProfile();
		profile4.setEditortype("Expand");
		Editor editor4 = null;
		try {
			editor4 = EditorFactory.createEditor(editObj4, profile4);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor4 instanceof AbstractComplexEditor);
		assertEquals(editor4.getClass(), ExpandEditor.class);
		assertNull(editor4.getEditingProfile().getPropertyName());
		//edit PerformanceNote read only
		PerformanceNote editObj5 = new PerformanceNote();
		EditingProfile profile5 = new EditingProfile();
		profile5.setReadOnly(true);
		Editor editor5 = null;
		try {
			editor5 = EditorFactory.createEditor(editObj5, profile5);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor5 instanceof AbstractComplexEditor);
		assertEquals(editor5.getClass(), PanelEditor.class);
		assertNull(editor5.getEditingProfile().getPropertyName());
		assertTrue(editor5.getEditingProfile().isReadOnly());
	}

	/**
	 * Tests methods <code>createEditor(Object, EditingProfile, Editor)</code>,
	 * <code>createEditor(Object, Editor, String)</code> and 
	 * <code>createEditor(Object, EditingProfile, String, Editor)</code>.
	 */
	public void testCreateEditorWithRootEditor() {
		//provide editor to be used as rootEditor
		Note editObj = new Note();
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		if (editor == null) {
			System.out.println(
				"EditorFactoryTest.testCreateEditorWithRootEditor(): root editor could not be created.");
			return;
		} else {
			assertTrue(editor instanceof AbstractComplexEditor);
			assertEquals(editor.getRootDisplay(), editor);
		}
		//use editor as rootEditor 
		PerformanceNote editObj1 = new PerformanceNote();
		Editor editor1 = null;
		try {
			editor1 = EditorFactory.createEditor(editObj1, null, editor);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor1 instanceof AbstractComplexEditor);
		assertEquals(editor1.getRootDisplay(), editor);
		try {
			editor1 = EditorFactory.createEditor(editObj1);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor1 instanceof AbstractComplexEditor);
		assertEquals(editor1.getRootDisplay(), editor1);
		//test method createEditor(Object, EditingProfile, String, Editor)
		ScoreNote editObj2 = new ScoreNote();
		Editor editor2 = null;
		EditingProfile profile2 = new EditingProfile();
		profile2.setEditortype(new String[] { "Panel", "Expand" });
		try {
			editor2 =
				//					EditorFactory.createEditor(
	EditorFactory.createEditor(editObj2, profile2, "Expand", editor);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor2 instanceof AbstractComplexEditor);
		assertTrue(editor2 instanceof ExpandEditor);
		assertEquals(editor2.getRootDisplay(), editor);
		//test method createEditor(Object, Editor, String)
		PerformanceNote editObj3 = new PerformanceNote();
		Editor editor3 = null;
		try {
			editor3 = EditorFactory.createEditor(editObj3, editor, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor3 instanceof AbstractComplexEditor);
		assertEquals(editor3.getRootDisplay(), editor);
	}

	/**
	 * Tests methods <code>createEditor(Object, EditingProfile, String, Editor)</code>
	 * and <code>createEditor(Object, Editor, String)</code>.
	 */
	public void testCreateEditorWithEditortype() {
		//test method createEditor(Object, EditingProfile, String, Editor)
		Note editObj1 = new Note();
		EditingProfile profile1 = new EditingProfile("scoreNote");
		profile1.setEditortype(new String[] { "Panel", "Expand" });
		Editor editor1 = null;
		try {
			editor1 =
				EditorFactory.createEditor(editObj1, profile1, "Expand", null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor1 instanceof AbstractComplexEditor);
		assertTrue(editor1 instanceof ExpandEditor);
		assertNotNull(editor1.getEditingProfile().getPropertyName());
		//test method createEditor(Object, Editor, String)
		EditableWithEditortypes editObj2 = new EditableWithEditortypes();
		Editor editor2 = null;
		try {
			editor2 = EditorFactory.createEditor(editObj2, null, "Expand");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor2 instanceof AbstractComplexEditor);
		assertTrue(editor2 instanceof ExpandEditor);
		editor2 = null;
		try {
			editor2 = EditorFactory.createEditor(editObj2, null, "PopUp");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
			fail();
		}
		//check editor
		assertTrue(editor2 instanceof AbstractComplexEditor);
		assertTrue(editor2 instanceof PopUpEditor);
	}

	/**
	 * Inner class for objects to be used in <code>testCreateEditorWithEditortypes()</code>.
	 * The class provides an Editable whose EditingProfile registers more than one
	 * editortype.
	 * 
	 * @author Kerstin Neubarth
	 */
	public static class EditableWithEditortypes implements Editable {
		EditingProfile profile;

		/**
		 * Getter for <code>profile</code>. 
		 * @see de.uos.fmt.musitech.framework.editor.Editable#getEditingProfile()
		 */
		@Override
		public EditingProfile getEditingProfile() {
			profile = new EditingProfile();
			profile.setEditortype(new String[] { "Panel", "PopUp", "Expand" });
			return profile;
		}
	}

	/**
	 * For testing method <code>createDisplay2(Object, EditingProfile, String, Display)</code> 
	 * in class EditorFactory.
	 */
	public void testCreateDisplay() {
		//provide editObj
		LyricsSyllableSequence lyrics = new LyricsSyllableSequence();
		lyrics.add(new LyricsSyllable(0, "Syl"));
		lyrics.add(new LyricsSyllable(500, "la"));
		lyrics.add(new LyricsSyllable(700, "ble"));
		//create display
		Display display = null;
		try {
			display = EditorFactory.createDisplay(lyrics, null, null, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check display
		assertTrue(display != null);
		assertTrue(display instanceof LyricsDisplay);
		assertEquals(display.getEditObj(), lyrics);
		assertTrue(display.getEditingProfile().isReadOnly());
		assertEquals(display.getRootDisplay(), display);
	}

	/**
	 * Tests method <code>createEditor2(Object, EditingProfile, String, Display)</code>
	 * with a ScoreNote <code>editObj</code>.
	 */
	public void testCreateEditor() {
		//provide editObj
		ScoreNote sn = new ScoreNote();
		//create Editor
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(sn, null, null, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check editor
		assertTrue(editor != null);
		assertTrue(editor instanceof AbstractComplexEditor);
		assertTrue(editor instanceof PanelEditor);
		assertEquals(editor.getRootDisplay(), editor);
	}

	/**
	 * Tests method <code>createEditor2(Object, EditingProfile, String, Display)</code>
	 * with specifying the editortype (String argument).
	 */
	public void testCreateEditorWithEditortype2() {
		//provide editObj
		ScoreNote editObj = new ScoreNote();
		//create Editor
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj, null, "Expand", null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check editor
		assertTrue(editor != null);
		assertTrue(editor instanceof ExpandEditor);
	}

	/**
	 * Tests method <code>createEditor2(Object, EditingProfile, String, Display)</code>
	 * with specifying the editortype in the EditingProfile argument.
	 */
	public void testCreateEditorWithProfile2() {
		//provide editObj
		ScoreNote editObj = new ScoreNote();
		//create EditingProfile
		EditingProfile profile = new EditingProfile();
		profile.setEditortype("Expand");
		//create Editor
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj, profile, null, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check editor
		assertTrue(editor instanceof ExpandEditor);
		assertNull(editor.getEditingProfile().getPropertyName());
	}

	public void testCreateDisplayWithEditortype() {
		//provide editObj
		LyricsSyllableSequence lyrics = new LyricsSyllableSequence();
		lyrics.add(new LyricsSyllable(0, "Syl"));
		lyrics.add(new LyricsSyllable(500, "la"));
		lyrics.add(new LyricsSyllable(700, "ble"));
		//create display
		Display display = null;
		try {
			display = EditorFactory.createDisplay(lyrics, null, "Panel", null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check display
		assertTrue(display != null);
		assertTrue(display instanceof PanelEditor);
		assertEquals(display.getEditObj(), lyrics);
		assertTrue(display.getEditingProfile().isReadOnly());
		assertEquals(display.getRootDisplay(), display);
	}

	/**
	 * Tests creating a LyricsSyllableDisplay using method
	 * <code>createDisplay2(Object, EditingProfile, null, null, null)</code>.
	 * <br><br>
	 * This method was motivated by an EditorConstructionException in 
	 * <code>testCreateDisplay2()</code>.
	 * (Creating the syllable displays inside a LyricsDisplay, which had caused
	 * the Exception, has been adapted to the changes in the EditorFactory.) 
	 */
	public void testCreateLyricsSyllableDisplay() {
		//provide editObj
		LyricsSyllable editObj = new LyricsSyllable(100, "one");
		//create Display
		Display display = null;
		try {
			display = EditorFactory.createDisplay(editObj, null, null, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check display
		assertTrue(display instanceof LyricsSyllableDisplay);
	}

	/**
	 * Tests creating a ChordSymbSeqDisplay using method
	 * <code>createDisplay2(Object, EditingProfile, null, null, null)</code>.
	 * <br><br>
	 * For testing if the creation of the symbol displays inside the
	 * ChordSymbSeqDisplay has been adapted correctly to the changes in the
	 * EditorFactory.
	 */
	public void testCreateChordSymbSeqDisplay() {
		//provide editObj
		ChordSymbolSequence editObj = new ChordSymbolSequence();
		editObj.add(new ChordSymbol(null, 100));
		editObj.add(new ChordSymbol(null, 150));
		//create Display
		Display display = null;
		try {
			display = EditorFactory.createDisplay(editObj, null, null, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check Display
		assertTrue(display instanceof ChordSymbSeqDisplay);
	}

	/**
	 * Tests the different <code>createEditor()</code>-methods (with one to three
	 * arguments). 
	 */
	public void testCreateEditorDivArguments() {
		//provide editObj
		Note editObj = new Note(new ScoreNote(), new PerformanceNote());
		//create Editors with different createEditor() methods
		Editor editor1 = null, editor2 = null, editor3 = null, editor4 = null;
		try {
			editor1 = EditorFactory.createEditor(editObj);
			editor2 = EditorFactory.createEditor(editObj, null);
			editor3 = EditorFactory.createEditor(editObj, null, "Panel");
			editor4 = EditorFactory.createEditor(editObj, null, editor1);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//check Editors
		assertNotNull(editor1);
		assertEquals(editor1.getRootDisplay(), editor1);
		assertNotNull(editor2);
		assertEquals(editor2.getRootDisplay(), editor2);
		assertNotNull(editor3);
		assertTrue(editor3 instanceof PanelEditor);
		assertNotNull(editor4);
		assertEquals(editor4.getRootDisplay(), editor1);
	}

	/**
	 * For testing method <code>getEditortypeNames(Object, EditingProfile)</code>.
	 */
	public void testGetEditortypeNames() {
		//editortype for editObj from Registry
		String[] types1 =
			EditorFactory.getDisplayTypeNames(
				new ChordSymbol(),
				new EditingProfile());
		assertEquals(types1[0], "ChordSymbol");
		String[] types2 =
			EditorFactory.getDisplayTypeNames(
				new LyricsSyllable(),
				new EditingProfile());
		assertEquals(types2[0], "LyricsSyllable");
		//editortype for editObj: default 
		String[] types3 =
			EditorFactory.getDisplayTypeNames(
				new ScoreNote(),
				new EditingProfile());
		assertEquals(types3[0], "Panel");
		//editortype for editObj from EditingProfile
		EditingProfile profile4 = new EditingProfile();
		profile4.setEditortype("Expand");
		String[] types4 =
			EditorFactory.getDisplayTypeNames(new PerformanceNote(), profile4);
		assertEquals(types4[0], "Expand");
		//editortype for property from Registry
		String[] types5 =
			EditorFactory.getDisplayTypeNames(
				new ObjWithChordSymbolProperty(),
				new EditingProfile("symbol"));
		assertEquals(types5[0], "ChordSymbol");
		//editortype for property: default
		String[] types6 =
			EditorFactory.getDisplayTypeNames(
				new ScoreNote(),
				new EditingProfile("pitch"));
		assertEquals(types6[0], "PopUp");
		String[] types7 =
			EditorFactory.getDisplayTypeNames(
				new Note(new ScoreNote(), new PerformanceNote()),
				new EditingProfile("scoreNote"));
		assertEquals(types7[0], "PopUp");
		//editortype for property from profile
		EditingProfile profile8 = new EditingProfile("scoreNote");
		profile8.setEditortype("Expand");
		String[] types8 =
			EditorFactory.getDisplayTypeNames(new Note(), profile8);
		assertEquals(types8[0], "Expand");
	}

	/**
	 * Inner class for an object with a ChordSymbol property.
	 * Used in method <code>testGetEditortypeNamesTest()</code>.
	 * 
	 * @author Kerstin Neubarth
	 *
	 * To change the template for this generated type comment go to
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
	public static class ObjWithChordSymbolProperty {
		//public properties for ReflectionAccess
		public String name = "Name";
		public ChordSymbol symbol = new ChordSymbol();
	}

	/**
	 * For testing method <code>createDefaultProfile(Object, String)</code>.
	 * <br><br>
	 * This method has been written in addition to 
	 * <code>testCreateDefaultProfile()</code> when the EditorFactory was being
	 * modified.
	 */
	public void testCreateProfile() {
		//for primitive object
		EditingProfile profile1 =
			EditorFactory.createDefaultProfile(new String("String"), null);
		assertNotNull(profile1);
		assertNull(profile1.getPropertyName());
		assertEquals(profile1.getLabel(), "String");
		assertEquals(profile1.getDefaultEditortype(), "String");
//		assertEquals(profile1.getEditortypes().length, 2);	//liefert auch default types
		assertNull(profile1.getChildren());
		assertTrue(profile1.isReadOnly());
		//for Collection without properties
		CollectionWithoutProperties coll = new CollectionWithoutProperties();
		EditingProfile profile2 =
			EditorFactory.createDefaultProfile(coll, null);
		assertNull(profile2.getPropertyName());
		assertNotNull(profile2.getLabel());
		assertNull(profile2.getChildren());
		//for Collection with properties
		LyricsSyllableSequence seq = new LyricsSyllableSequence();
		seq.add(new LyricsSyllable(0, "Syl"));
		EditingProfile profile3 = EditorFactory.createDefaultProfile(seq, null);
		assertNull(profile3.getPropertyName());
//		assertEquals(profile3.getEditortypes().length, 1);	//außerdem default types
		assertEquals(profile3.getEditortypes()[0], "Lyrics");
		assertNotNull(profile3.getChildren());
		//for complex object as a whole
		EditingProfile profile4 =
			EditorFactory.createDefaultProfile(new ScoreNote(), null);
		assertNull(profile4.getPropertyName());
		assertNotNull(profile4.getLabel());
		assertEquals(profile4.getDefaultEditortype(), "Panel");
		assertNotNull(profile4.getChildren());
		ReflectionAccess ref = ReflectionAccess.accessForClass(ScoreNote.class);
		List propertyNames = Arrays.asList(ref.getPropertyNames());
		assertTrue(
			propertyNames.contains(
				profile4.getChildren()[2].getPropertyName()));
		List childrenTypes =
			Arrays.asList(new String[] { "Character", "Rational", "Byte" });
		assertTrue(
			childrenTypes.contains(
				profile4.getChildren()[1].getEditortypes()[0]));
		//for simple property of a complex object
		EditingProfile profile5 =
			EditorFactory.createDefaultProfile(new PerformanceNote(), "pitch");
		assertEquals(profile5.getPropertyName(), "pitch");
		assertEquals(profile5.getLabel(), "pitch");
		assertEquals(profile5.getDefaultEditortype(), "Byte");
		assertNull(profile5.getChildren());
		//for complex property of a complex object
		EditingProfile profile6 =
			EditorFactory.createDefaultProfile(
				new Note(new ScoreNote(), new PerformanceNote()),
				"scoreNote");
		assertEquals(profile6.getPropertyName(), "scoreNote");
		assertEquals(profile6.getLabel(), "score Note");
		//		assertEquals(profile6.getDefaultEditortype(), "Panel");	//TODO ist PopUp
		assertNotNull(profile6.getChildren());
	}

	/**
	 * Inner class representing a Collection without properties.
	 * This class is used in method <code>testCreateProfile()</code>.
	 */
	public static class CollectionWithoutProperties extends HashSet {
	}

	/**
	 * For testing method <code>createEditortypes(Class)</code>.
	 */
	public void testCreateEditortypesForClass() {
		String[] types1 = EditorFactory.getDisplayTypeNames(String.class);
		assertTrue(Arrays.asList(types1).contains("String"));
		assertTrue(Arrays.asList(types1).contains("Text"));
		String[] types2 = EditorFactory.getDisplayTypeNames(Byte.class);
		assertEquals(types2[0], "Byte");
		String[] types3 = EditorFactory.getDisplayTypeNames(Rational.class);
		assertEquals(types3[0], "Rational");
		String[] types4 = EditorFactory.getDisplayTypeNames(NoteList.class);
		assertEquals(types4[0], "Notation");
		String[] types5 = EditorFactory.getDisplayTypeNames(ChordSymbol.class);	//TODO
		assertEquals(types5[0], "ChordSymbol");
		String[] types6 =
			EditorFactory.getDisplayTypeNames(ChordSymbolSequence.class);
		assertEquals(types6[0], "ChordSymbolSequence");
		String[] types7 =
			EditorFactory.getDisplayTypeNames(LyricsSyllable.class);
		assertEquals(types7[0], "LyricsSyllable");
		String[] types8 =
			EditorFactory.getDisplayTypeNames(LyricsSyllableSequence.class);
		assertEquals(types8[0], "Lyrics");
		String[] types9 = EditorFactory.getDisplayTypeNames(ImageURL.class);
		assertEquals(types9[0], "Image");
		String[] types10 =
			EditorFactory.getDisplayTypeNames(MidiNoteSequence.class);
		assertEquals(types10[0], "PianoRoll");
	}

	/**
	 * For testing method <code>createEditor(Object, boolean)</code>.
	 */
	public void testCreateEditorWithReadOnly() {
		Editor editor1 = null;
		try {
			editor1 = EditorFactory.createEditor(new PerformanceNote(), true);
			assertNotNull(editor1);
			assertTrue(editor1 instanceof PanelEditor);
			assertTrue(editor1.getEditingProfile().isReadOnly());
			Component[] comps = ((JComponent) editor1).getComponents();
			for (int i = 0; i < comps.length; i++) {
				if (comps[i] instanceof SimpleTextfieldEditor) {
					assertFalse(
						((SimpleTextfieldEditor) comps[i])
							.getTextfield()
							.isEditable());
				}
			}
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		Editor editor2 = null;
		try {
			editor2 = EditorFactory.createEditor(new PerformanceNote(), false);
			assertFalse(editor2.getEditingProfile().isReadOnly());
			Component[] comps = ((JComponent) editor2).getComponents();
			for (int i = 0; i < comps.length; i++) {
				if (comps[i] instanceof SimpleTextfieldEditor) {
					assertTrue(
						((SimpleTextfieldEditor) comps[i])
							.getTextfield()
							.isEditable());
				}
			}
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		Editor editor3 = null;
		try {
			editor3 =
				EditorFactory.createEditor(
					new PerformanceNote(),
					true,
					null,
					null);
			assertTrue(editor3.getEditingProfile().isReadOnly());
			assertEquals(editor3, editor3.getRootDisplay());
			assertTrue(editor3 instanceof PanelEditor);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		Editor editor4 = null;
		try {
			editor4 = EditorFactory.createEditor(new Rational(2,3), true, "Panel", editor2);
			assertTrue(editor4.getEditingProfile().isReadOnly());
			assertFalse(editor4 instanceof RationalEditor);
			assertTrue(editor4 instanceof PanelEditor);
			assertEquals(editor4.getRootDisplay(), editor2);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		Editor editor5 = null;
		try {
			editor5 = EditorFactory.createEditor(new Rational(2,3), false, null, editor1);
			assertTrue(editor5 instanceof RationalEditor);
			assertFalse(editor5.getEditingProfile().isReadOnly());
			assertTrue(((RationalEditor)editor5).getTextfield().isEditable());
			assertEquals(editor5.getRootDisplay(), editor1);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * For testing that all properties of a complex object are edited by Editors
	 * (and not by Displays). Therefore, a complex object with one property of
	 * type NoteList is created as object to be edited, because the type registered
	 * for NoteList in the EditorRegistry is a Display (NotationDisplay). As a child
	 * editor, however, an Editor is to be used (which is a PopUpEditor, i.e.
	 * corresponding to the <code>defaultPropertyEditorType</code> of the EditorFactory). 
	 */
	public void testComplexEditor(){
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(new ObjectWithNoteListProperty());
			assertTrue(editor instanceof Editor);
			assertTrue(editor instanceof AbstractComplexEditor);
			Editor[] children = ((AbstractComplexEditor)editor).getChildren();
			assertEquals(children.length, 3);
			for (int i = 0; i < children.length; i++) {
				assertTrue(children[i] instanceof Editor);
			}
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inner class used in method <code>testComplexEditor()</code>.
	 * 
	 *  @author Kerstin Neubarth
	 *
	 */
	public static class ObjectWithNoteListProperty{
		//public fields are used so that the properties can be reached via
		//ReflectionAccess without having getters and setters
		public String name;
		public int priority;
		public NoteList notes;
		public ObjectWithNoteListProperty(){
			name = "Name";
			priority = 1;
			notes = new NoteList();
			notes.add(new ScorePitch(60), new Rational(0,1), new Rational(1,4));
			notes.add(new ScorePitch(61), new Rational(1,1), new Rational(1,4));
		}
	}
	
	/**
	 * For testing method <code>createPopUpWrapper(Display)</code> of the EditorFactory.
	 */
/*	public void testCreatePopUpWrapperDisplay(){
		//create NoteList as editObj
		NoteList notes = new NoteList();
		notes.add(new ScorePitch(60), new Rational(0,4), new Rational(1,4));
		//create display to be wrapped
		Display display = null;
		try {
			display = EditorFactory.createDisplay(notes, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();		
		}
		assertTrue(display instanceof Display);
		//create wrapper
		Display wrapper = null;
		wrapper = EditorFactory.createPopUpWrapper(display);
		assertTrue(wrapper instanceof PopUpDisplay);
		assertTrue(((PopUpDisplay)wrapper).getDisplayToPopUp() instanceof Display);
		assertTrue(((PopUpDisplay)wrapper).getDisplayToPopUp() instanceof NotationDisplay);
		assertTrue(((PopUpDisplay)wrapper).getDisplayToPopUp().getEditObj() instanceof NoteList);
	}*/
	
	/**
	 * Tests method <code>createDisplay(Object, String, boolean)</code>.
	 */
	public static void testCreateDisplayWithReadOnly(){
	    //provide editObj
	    PerformanceNote perfNote = new PerformanceNote(); 
	    //create display
	    Display display1 = null;
	    try {
            display1 = EditorFactory.createDisplay(perfNote, "Panel", false);
            assertTrue(display1 instanceof Editor);
            assertTrue(display1 instanceof PanelEditor);
            assertFalse(display1.getEditingProfile().isReadOnly());
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //provide display object
        LyricsSyllable syllable = new LyricsSyllable(100, "one");
        //create display
        Display display2 = null;
        try {
            display2 = EditorFactory.createDisplay(syllable, "LyricsSyllable", false);
            assertTrue(display2 instanceof Display);
            assertTrue(display2.getEditingProfile().isReadOnly());
            //show (for testing if editing impossible)
//            JFrame frame = new JFrame("Display readOnly false");
//            frame.getContentPane().add((JComponent)display2);
//            frame.pack();
//            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Tests method <code>createEditor(Class, Object, EditingProfile, Display)</code>.
	 */
	public static void testCreateEditorWithClass(){
	    //provide editObj and editorclass
	    PerformanceNote editObj = new PerformanceNote();
	    Class editorClass = PanelEditor.class;
	    //create Editor
	    Editor editor = null;
	    try {
            editor = EditorFactory.createEditor(editorClass, editObj, null, null);
            assertTrue(editor instanceof PanelEditor);
            assertEquals(editor.getEditObj(), editObj);
            assertNotNull(editor.getEditingProfile());
            assertEquals(editor.getRootDisplay(), editor);
        } catch (EditorConstructionException e) {
            e.printStackTrace(); 
            fail();
        }
	}
	
	/**
	 * Tests method <code>createDisplay(Class, Object, EditingProfile, Display)</code>.
	 */
	public static void testCreateDisplayWithClass(){
	    //provide editObj and displayClass
	    LyricsSyllable editObj = new LyricsSyllable(100, "one");
	    Class displayClass = LyricsSyllableDisplay.class;
	    //create Display
	    Display display = null;
	    try {
            display = EditorFactory.createDisplay(displayClass, editObj, null, null);
            assertTrue(display instanceof LyricsSyllableDisplay);
            assertEquals(display.getEditObj(), editObj);
            assertNotNull(display.getEditingProfile());
            assertEquals(display.getRootDisplay(), display);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
            fail();
        }
	}

}
