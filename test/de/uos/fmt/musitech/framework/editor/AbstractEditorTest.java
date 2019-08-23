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
 * Created on 02.12.2003
 *
 */
package de.uos.fmt.musitech.framework.editor;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * JUnit Test class for class <code>AbstractEditor</code> and its subclasses.
 * <br>
 * <br>Methods concerning the interaction between the editor and the DataChangeManager
 * are covered by test class <code>TestEditorsWithDataChangeEvents</code>.
 * Tests integrating GUI and user actions and therefore having a <code>main</code>-method
 * are found in test class <code>TestInteractionEditorChangeManager</code>.
 * 
 * @author Kerstin Neubarth
 *
 */
public class AbstractEditorTest extends TestCase {

	/**
	 * Tests method <code>getClassOfValueToCreate()</code> of class <code>AbstractEditor</code>.
	 * <br>
	 * Method <code>getClassOfValueToCreate()</code> is used in case the 
	 * <code>propertyValue</code> of the editor is null and a new 
	 * <code>propertyValue</code> is to be created. The method is expected 
	 * to return the class of the object to be created.
	 * <br>To test the method, the Class returned by <code>getClassOfValueToCreate()</code>
	 * is checked against the Class got from <code>editObj</code> via ReflectionAccess
	 * (for the property which is null).
	 */
	public void testGetClassOfValueToCreate() {
		//create editObj: Note with null-property scoreNote
		Note editObj = new Note(null, new PerformanceNote());
		//get property class via ReflectionAccess from object
		Class classOfProperty = null;
		ReflectionAccess refNote =
			ReflectionAccess.accessForClass(editObj.getClass());
		String[] propertyNames = refNote.getPropertyNames();
		for (int i = 0; i < propertyNames.length; i++) {
			if (refNote.hasPropertyName(propertyNames[i])) {
				classOfProperty = refNote.getPropertyType(propertyNames[i]);
			}
			//for the property being null, check method getClassOfValueToCreate()
			if (refNote.getProperty(editObj, propertyNames[i]) == null) {
				//create EditingProfile
				EditingProfile profile = new EditingProfile(propertyNames[i]);
				//provide editor for property scoreNote
				Editor editor = createTestEditor(editObj, profile);
				//check property class returned by method getClassOfValueToCreate()
				Class propertyClass =
					((AbstractEditor) editor).getClassOfValueToCreate();
				if (!classOfProperty.isAssignableFrom(propertyClass)) {
					System.out.println(
						"In AbstractEditorTest: testGetClassOfValueToCreate()\n"
							+ "Class returned by method getClassOfValueToCreate() does not match the actual property class.\n"
							+ "Class returned: "
							+ classOfProperty
							+ ".\n"
							+ "Required class: "
							+ propertyClass
							+ ".");
					fail();
				}
			}
		}
	}

	/**
	 * Tests method <code>getClassOfValueToCreate()</code> of class <code>AbstractEditor</code>
	 * for an <code>editObj</code> of type MetaDataItem.
	 * <br>
	 * Method <code>getClassOfValueToCreate()</code> is used in case the 
	 * <code>propertyValue</code> of the editor is null and a new 
	 * <code>propertyValue</code> is to be created. The method is expected 
	 * to return the class of the object to be created.
	 * <br>To test the method, the Class returned by <code>getClassOfValueToCreate()</code>
	 * is checked against the Class got from <code>editObj</code> via ReflectionAccess
	 * (for the property which is null).
	 */
	public void testGetClassOfValueToCreateForMetaValue() {
		//create editObj: MetaDataItem whose MetaDataValue has null-value
		String key = "Year of Composition";
		MetaDataItem editObj = new MetaDataItem(key);
		//value is only used to derive type
		int year = 1925;
		Object value = new Integer(year);
		String type = value.getClass().toString();
		//convert type to required form of String
		int index = type.lastIndexOf(".");
		type = type.substring(index + 1).toLowerCase();
		if (type.equals("integer"))
			type = "int";
		MetaDataValue metaDataValue = new MetaDataValue(type, null);
		editObj.setMetaValue(metaDataValue);
		//create editor
		Editor editor =
			createTestEditor(editObj.getMetaDataValue(), editObj.getEditingProfile());
		if (editor != null) {
			//check method getClassOfValueToCreate()
			Class valueClass =
				((AbstractEditor) editor).getClassOfValueToCreate();
			if (valueClass == null) {
				System.out.println(
					"In AbstractEditorTest: testGetClassOfValueToCreateForMetaValue.\n"
						+ "Method getClassOfValueToCreate() returns null.");
				fail();
			}
			if (!value.getClass().isAssignableFrom(valueClass)) {
				System.out.println(
					"In AbstractEditorTest: testGetClassOfValueToCreateForMetaValue.\n"
						+ "Class returned by method getClassOfValueToCreate() does not match the actual property class.\n"
						+ "Class returned: "
						+ valueClass
						+ ".\n"
						+ "Required class: "
						+ value.getClass()
						+ ".");
				fail();
			}
		}
	}

	/**
	 * Tests method <code>createNewValue(String)</code> of class <code>AbstractEditor</code>.
	 * <br>
	 * When the editor window with title "New property" is shown, please press
	 * the "OK"-button.
	 * NOTE: If you want to run the JUnit tests without waiting for user action,
	 * please hide this method between comment.
	 * <br>
	 * <br>If the property of <code>editObj</code> to be edited is null the editor shows
	 * a JButton labeled "Create". If this button is pressPoint, a new editor is opened
	 * for entering the value(s) the property shall have. The newly created object is
	 * to be written to the editor's <code>propertyValue</code>, while the property of
	 * <code>editObj</code> is to be set ONLY after <code>applyChanges()</code> has 
	 * been activated. 
	 * <br>In this test method, the ActionListener attached to the "Create"-button of 
	 * the editor is triggered. The editor for entering the value(s) having been closed, 
	 * the property of <code>editObj</code> then is checked before and after performing
	 * <code>applyChanges()</code>.
	 */
	public void testCreateNewValue() {
		//create editObj: Note with null-property scoreNote
		Note editObj = new Note(null, new PerformanceNote());
		//create editor for null-property of editObj
		ReflectionAccess refNote = ReflectionAccess.accessForClass(editObj.getClass());
		String[] propertyNames = refNote.getPropertyNames();
		for (int i=0; i<propertyNames.length; i++){
			if (refNote.getProperty(editObj, propertyNames[i])==null){
				EditingProfile profile = new EditingProfile(propertyNames[i]);
				Editor editor = createTestEditor(editObj, profile);
//				((AbstractEditor)editor).propertyValue = ((AbstractEditor)editor).createNewValue("New property");
				if (editor instanceof PopUpEditor){
//					((PopUpEditor)editor).actionPerformed(new ActionEvent(((PopUpEditor)editor).editButton, 1, ""));
					((PopUpEditor)editor).showEditorToPopUp();
				}
				boolean error = false;
				if (((AbstractEditor)editor).propertyValue==null){
					System.out.println("In AbstractEditorTest: testCreateNewValue().\n"
					+ "The propertyValue of editObj is still null.");
					error = true;
				}
				if (refNote.getProperty(editObj, propertyNames[i])!=null){
					System.out.println("In AbstractEditorTest: testCreateNewValue().\n"
					+ "null-property of editObj is set before applyChanges() is performed.");
					error = true;
				}
				editor.applyChanges();
				if (refNote.getProperty(editObj, propertyNames[i])==null){
					System.out.println("In AbstractEditorTest: testCreateNewValue().\n"
					+ "After applyChanges() property of editObj is still null.");
					error = true;
				}
				if (error)
					fail();
			}
		}
	}

	/**
	 * Private "helper" method creating an editor for the specified editObj.
	 * <br>
	 * In this way, the 'real' test methods need not care for the creation 
	 * of the editor(s) themselves.
	 * 
	 * @param editObj Object for which an editor is to be created 
	 */
	private Editor createTestEditor(Object editObj, EditingProfile profile) {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj, profile);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		return editor;
	}

}

