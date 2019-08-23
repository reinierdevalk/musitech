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
 * Created on 21.01.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import junit.framework.TestCase;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Methods for testing class <code>BooleanEditor</code>.
 * 
 * @author Kerstin Neubarth
 *
 */
public class BooleanEditorTestWithUserInteraction extends TestCase {

	/**
	 * Private "helper" method to free the test methods from the code involved
	 * in creating the editor to test.
	 * 
	 * @param objToEdit Object an editor is to be created for
	 * @return Editor created for objToEdit
	 */
	private static Editor createBooleanEditor(Object objToEdit) {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(objToEdit);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		return editor;
	}

	/**
	 * Private "helper" method for showing the specified Editor in an EditorWindow
	 * labelled with the specified String.
	 * 
	 * @param editor Editor to show 
	 * @param windowTitle String indicating the title to be displayed on top of the EditorWindow
	 */
	private static void showEditor(Editor editor, String windowTitle) {
		EditorWindow w = new EditorWindow(windowTitle);
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Tests creating a BooleanEditor as outmost editor (i.d. the Boolean object
	 * is the <code>editObj</code>).
	 */
	public static void creatingBooleanEditorForEditObj() {
		//provide editObj
		Boolean testBoolean = Boolean.valueOf(false);
		//create and show editor
		Editor testEditor = createBooleanEditor(testBoolean);
		if (testEditor != null)
			showEditor(testEditor, "testCreatingBooleanEditorForEditObj");
		else
			fail();
	}

	/**
	 * Tests creating a BooleanEditor as child editor (i.d. the Boolean object
	 * is the <code>propertyValue</code> of a child editor).
	 */
	public static void creatingBooleanEditorForPropertyValue() {
		//provide editObj
		ObjWithBooleanProperty testObj = new ObjWithBooleanProperty();
		//create and show editor
		Editor testEditor = createBooleanEditor(testObj);
		if (testEditor != null)
			showEditor(testEditor, "BooleanEditorForPropertyValue");
		else
			fail();
	}

	/**
	 * Inner class providing an Object with a boolean property used in method
	 * <code>CreatingBooleanEditorForPropertyValue()</code>.
	 */
	public static class ObjWithBooleanProperty {
		public String name = "Name";
		public int number = 10;
		public boolean isTrue = true;
	}

	/**
	 * Tests creating a new </code>propertyValue</code> in a BooleanEditor 
	 * (this BooleanEditor is a child editor inside a complex editor).
	 */
	public static void creatingBooleanPropertyValue() {
		//provide editObj
		ObjWithBooleanNullProperty testObj = new ObjWithBooleanNullProperty();
		//create and show editor
		Editor testEditor = createBooleanEditor(testObj);
		if (testEditor != null)
			showEditor(testEditor, "CreateNewPropertyValue");
		else
			fail();
	}

	/**
	 * Inner class providing an Object with a Boolean property being null.
	 * This class is used in method <code>CreatingBooleanEditorForPropertyValue()</code>.
	 */
	public static class ObjWithBooleanNullProperty {
		public String name = "Name";
		public int number = 10;
		public Boolean isTrue = null;
	}

	/**
	 * Tests two editors editing the same object knowing about data changes.
	 * The BooleanEditors appear as a child editor in a complex editor each. 
	 */
	public static void twoBooleanEditorsWithSameObject() {
		//provide editObj
		ObjWithBooleanProperty testObj = new ObjWithBooleanProperty();
		//create and show editor 1
		Editor editor1 = createBooleanEditor(testObj);
		if (editor1 != null)
			showEditor(editor1, "BooleanEditor1");
		else
			fail();
		//create and show editor 2
		Editor editor2 = createBooleanEditor(testObj);
		if (editor2 != null)
			showEditor(editor2, "BooleanEditor2");
		else
			fail();
	}

	/**
	 * Main method for running the test methods.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new TestEditorFactory.ExitWindow().show();
		creatingBooleanEditorForEditObj();
		creatingBooleanEditorForPropertyValue();
		creatingBooleanPropertyValue();
		twoBooleanEditorsWithSameObject();
	}
}
