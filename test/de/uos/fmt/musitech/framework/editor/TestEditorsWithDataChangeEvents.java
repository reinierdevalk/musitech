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
 * Created on 18.11.2003
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.AbstractComplexEditor;
import de.uos.fmt.musitech.framework.editor.AbstractEditor;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * JUnit tests for the EditorFramework regarding the editors' interaction with the 
 * DataChangeManager.
 * <br>
 * <br>N.B.:
 * <br>Changing values in an editor here is achieved by changing values of 
 * <code>editObj</code> while the editor's local values (e.g. <code>propertyValue</code>
 * or <code>editObjCopy</code>) remain unchanged. So afterwards there is a difference 
 * between the values currently held by the editor and the state of its <code>editObj</code>.
 * <br>
 * This is done because the <code>propertyValue</code> "inside" an editor cannot be
 * directly set from outside, but is either getting its value from <code>editObj</code>
 * via ReflectionAccess in method <code>init(...)</code> when the editor is created, or
 * is changed according to the user's input in the GUI.
 * 
 * @author Kerstin Neubarth
 *
 */
public class TestEditorsWithDataChangeEvents extends TestCase {

	/**
	 * Tests a complex editor registering at the DataChangeManager.
	 * Then, the DataChangeManager's <code>table</code> is to contain the editor's
	 * <code>editObj</code> as a key leading to this editor.
	 */
	public void testRegisterAtChangeManager() {
		DataChangeManager.getInstance().clearTable();
		//provide editObj
		PerformanceNote perfNote = new PerformanceNote();
		perfNote.setPitch(65);
		perfNote.setVelocity(100);
		perfNote.setDuration(50);
		//provide editor (when editor is created, it registers at the DataChangeManager, cf. method init() of AbstractEditor)
		Editor perfNoteEditor = createTestEditor(perfNote);
		if (perfNoteEditor == null) {
			System.out.println(
				"In TestEditorsWithDataChangeEvents:\nEditor could not be created. testRegisterAtChangeManager() not completed.");
			return;
		}
		//get table of DataChangeManager
		Map table = DataChangeManager.getInstance().getTable();
		//check table for editor
		if (!table.containsKey(perfNote)) {
			System.out.println(
				"In TestEditorsWithDataChangeEvents.testRegisterAtChangeManager():\nError: editObj not registered at ChangeManager.");
			fail();
		} else {
			Object objectsOfView = table.get(perfNote);
			if (objectsOfView instanceof ArrayList) {
				if (!((ArrayList) objectsOfView).contains(perfNoteEditor)) {
					System.out.println(
						"In TestEditorsWithDataChangeEvents.testRegisterAtChangeManager():\nError: editor not registered at ChangeManager.");
					fail();
				}
			}
		}
	}

	/**
	 * Tests an editor receiving a DataChangeEvent.
	 * <br>Therefore, two editors sharing the same <code>editObj</code> are provided,
	 * <code>editor1</code> changes the object and tells the DataChangeManager.
	 * <code>editor2</code> is checked if knowing about conflicting changes.
	 */
	public void testReceivingDataChangeEvent() {
		//provide editObj
		PerformanceNote perf = new PerformanceNote();
		//provide 2 editors with same editObj
		Editor editor1 = createTestEditor(perf);
		Editor editor2 = createTestEditor(perf);
		//editor1 changing data
		Object obj = editor1.getEditObj();
		if (obj instanceof PerformanceNote) {
			((PerformanceNote) obj).setPitch(75);
		}
		//editor1 sending dataChangeEvent
		 ((AbstractEditor) editor1).sendDataChangeEvent();
		//checking editor2 for conflicting changes
		if (!editor2.externalChanges()) {
			System.out.println(
				"In TestEditorsWithDataChangeEvents.testReceivingDataChangeEvent():\nError: editor does not note data changes.");
			fail();
		}
	}

	/**
	 * Tests an editor evaluating an incoming DataChangeEvent.
	 * An editor is expected to neglect DataChangeEvents being caused by itself.
	 * 
	 * Therefore, the editor having sent the DataChangeEvent is been checked for
	 * conflicting changes.
	 */
	public void testEvaluatingDataChangeEvent() {
		//provide editObj
		PerformanceNote perf = new PerformanceNote();
		//provide 2 editors with same editObj
		Editor editor1 = createTestEditor(perf);
		Editor editor2 = createTestEditor(perf);
		//editor1 changing data
		Object obj = editor1.getEditObj();
		if (obj instanceof PerformanceNote) {
			((PerformanceNote) obj).setPitch(75);
		}
		//editor1 sending dataChangeEvent
		 ((AbstractEditor) editor1).sendDataChangeEvent();
		//check if editor1 recognizes the DataChangeEvent as coming from itself
		//(editor2 is checked for being sure an DataChangeEvent has in fact been sent)
		if (editor2.externalChanges()) {
			if (editor1.externalChanges()) {
				System.out.println(
					"In TestEditorsWithDataChangeEvents.testEvaluatingDataChangeEvent():\nError: editor does not neglect DataChangeEvent coming from itself.");
				fail();
			}
		}
	}

	/**
	 * Tests the interaction of two editors whose editObjs are not the same.
	 * 
	 * Therefore, <code>editor1</code> sends a DataChangeEvent and <code>editor2</code>
	 * is checked for conflicting changes. It should not know any.
	 */
	public void testTwoEditorsWithSimilarObjects() {
		DataChangeManager.getInstance().clearTable();
		//provide 2 editors whose editObjs are similar but not identical
		Editor editor1 = createTestEditor(new PerformanceNote());
		Editor editor2 = createTestEditor(new PerformanceNote());
		//compare editObjs 
		//		if (editor1.getEditObj().equals(editor2.getEditObj()))
		//			System.out.println("editObjs are equal.");
		//		if (editor1.getEditObj().hashCode()==editor2.getEditObj().hashCode())
		//			System.out.println("editObs have identical hashcode.");
		//editor1 changing data
		Object data = editor1.getEditObj();
		if (data instanceof PerformanceNote) {
			((PerformanceNote) data).setPitch(75);
		}
		((AbstractEditor) editor1).sendDataChangeEvent();
		//checking editor2 for conflicting data
		if (editor2.externalChanges()) {
			System.out.println(
				"In TestEditorsWithDataChangeEvents.testTwoEditorsWithEqualObjects():\n"
					+ "Error: editor whose editObj is not the same as that of the causing editor notes conflicting changes.");
			fail();
		}
	}

	/**
	 * Tests two editors whose editObjs are not identical registering at the DataChangeManager.
	 * The <code>table</code> of the DataChangeManager should not contain any entry in which
	 * one key (an object edited) is leading to more than one view.
	 * <br>
	 * Therefore, two editors, each with its own <code>editObj</code>, are created. These
	 * editors are registering at the DataChangeManager (see method <code>init(...)</code> of
	 * class <code>AbstractEditor</code>). The DataChangeManager's <code>table</code> is checked.
	 * 
	 */
	public void testRegisteringTwoIndependentEditors() {
		DataChangeManager.getInstance().clearTable();
		//provide 2 complex editors 
		//N.B.: These editors are needed for performing the registration at the DataChangeManager.
		Editor editor01 = createTestEditor(new PerformanceNote());
		Editor editor02 = createTestEditor(new PerformanceNote());
		//get table of DataChangeManager
		Map table = DataChangeManager.getInstance().getTable();
		for (Iterator iter = table.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			if (table.get(key) instanceof AbstractList) {
				AbstractList listOfViews = (AbstractList) table.get(key);
				int errors = 0;
				if (listOfViews.size() > 1) {
					errors++;
					System.out.println(
						"TestEditorsWithChangeEvents.testRegisteringTwoIndependentEditors():\n"
							+ "Error: table of ChangeManager contains two editors for same key.");
					System.out.println(
						"key of entry is of class: "
							+ key.getClass().toString());
				}
				if (errors > 0) {
					System.out.println(
						"number of entries with more than one view: " + errors);
					fail();
				}
			}
		}
	}

	/**
	 * Checks if the simple properties of two PerformanceNotes may be considered equal
	 * by the DataChangeManager.
	 * 
	 * This test method has been motivated by the results of methods 
	 * <code>testTwoEditorsWithSimilarObjects()</code> and
	 * <code>testRegisteringTwoIndependentEditors()</code>.
	 * 
	 * As the DataChangeManager uses an IdentityHashMap for its <code>table</code>,
	 * checking is performed in the following way:
	 * The properties of the <code>editObj</code> of <code>editor1</code> are put
	 * as keys into an IdentityHashMap. Then, the properties of <code>editObj2</code>
	 * are checked in this map using <code>containsKey(Object)</code>.
	 */
	public void testCompareSimpleProperties() {
		//provide 2 complex editors
		Editor editor1 = createTestEditor(new PerformanceNote());
		Editor editor2 = createTestEditor(new PerformanceNote());
		//get editObj of editor1 and its properties
		Object editObj1 = editor1.getEditObj();
		ReflectionAccess ref1 =
			ReflectionAccess.accessForClass(editObj1.getClass());
		String[] propertyNames1 = ref1.getPropertyNames();
		Map properties1 = Collections.synchronizedMap(new IdentityHashMap());
		for (int i = 0; i < propertyNames1.length; i++) {
			if (ref1.hasPropertyName(propertyNames1[i])) {
				properties1.put(
					ref1.getProperty(editObj1, propertyNames1[i]),
					null);
			}
		}
		//get editObj of editor2 and its propertyNames
		Object editObj2 = editor2.getEditObj();
		ReflectionAccess ref2 =
			ReflectionAccess.accessForClass(editObj2.getClass());
		String[] propertyNames2 = ref2.getPropertyNames();
		//check the properties of editObj2 against the IdentityHashMap properties1
		for (int i = 0; i < propertyNames2.length; i++) {
			if (ref2.hasPropertyName(propertyNames2[i])) {
				//get the property to be checked
				Object property = ref2.getProperty(editObj2, propertyNames2[i]);
				//check if this property is contained in the map
				if (properties1.containsKey(property)) {
					System.out.println(
						"TestEditorsWithDataChangeEvents.testCompareSimpleProperties():\n"
							+ "Warning: properties of two independent PerformanceNotes are considered equal.\n"
							+ "The matching property is: "
							+ propertyNames2[i]
							+ " = "
							+ property.toString()
							+ ".");
				}
			}
		}
	}

	/**
	 * Tests method <code>getEditedData()</code> of a complex editor which calls
	 * <code>getEditedData()</code> of its children editors.
	 * This method is expected to return a Collection containing objects whose values
	 * have been changed. So in case objects have not been changed at all, the Collection
	 * returned should be empty.
	 */
	public void testGetEditedDataForUnchangedObjects() {
		Editor editor = createTestEditor(new PerformanceNote());
		//getEditedData() before changes have taken place: is to return an empty collection
		Collection changedObjects = editor.getEditedData();
		if (changedObjects != null && !changedObjects.isEmpty()) {
			System.out.println(
				"In TestEditorsWithDataChangeEvents.testGetChangedData():\n"
					+ "Error: getEditedData() returns objects which have not been changed.");
			fail();
		}
	}

	/**
	 * Tests method <code>getEditedData()</code> of a complex editor which calls
	 * <code>getEditedData()</code> of its children editors.
	 * This method is expected to return a Collection containing objects whose values
	 * have been changed. So in case objects have retained their original values, 
	 * the Collection returned should be empty.
	 */
	public void testGetEditedDataForOriginalValues() {
		Editor editor = createTestEditor(new PerformanceNote());
		//edit data by repeating the original value
		Object obj = editor.getEditObj();
		if (obj instanceof PerformanceNote) {
			((PerformanceNote) obj).setPitch(
				((PerformanceNote) obj).getPitch());
		}
		//get edited data
		Collection changedObjects = editor.getEditedData();
		if (changedObjects != null && !changedObjects.isEmpty()) {
			System.out.println(
				"In TestEditorsWithDataChangeEvents.testGetChangedData():\n"
					+ "Error: getEditedData() returns objects which have been reset to their original values.");
			fail();
		}
	}

	/**
	 * Tests method <code>getEditedData()</code> of a complex editor which calls
	 * <code>getEditedData()</code> of its children editors.
	 * This method is expected to return a Collection containing objects whose values
	 * have been changed. So in case objects have been changed, the Collection 
	 * returned should not be empty.
	 */
	public void testGetEditedDataForChangedValues() {
		Editor editor = createTestEditor(new PerformanceNote());
		//change data
		Object obj = editor.getEditObj();
		if (obj instanceof PerformanceNote) {
			((PerformanceNote) obj).setPitch(
				((PerformanceNote) obj).getPitch()+15);
		}
		//get edited data
		Collection changedObjects = editor.getEditedData();
		if (changedObjects == null || changedObjects.isEmpty()) {
			System.out.println(
				"In TestEditorsWithDataChangeEvents.testGetChangedData():\n"
					+ "Error: getEditedData() does not return changed objects.");
			fail();
		}
	}
	
	public void testDestroy(){
		Editor editor = createTestEditor(new PerformanceNote());
		//check table of DataChangeManager
		Map table = DataChangeManager.getInstance().getTable();
		if (table.containsKey(editor.getEditObj())){
			editor.destroy();
//			if (table.containsKey(editor.getEditObj())){
			if (DataChangeManager.getInstance().tableContainsEditor(editor)){
				System.out.println("In TestEditorsWithDataChangeEvents: testDestroy()\n"
				+ "Error: table still contains the editor after destroy().");
				fail();
			}
		}
		//check table for children editors
		Vector remainingChildren = new Vector();
		if (editor instanceof AbstractComplexEditor){
			Editor[] childrenEditors = ((AbstractComplexEditor)editor).getChildren();
			if (childrenEditors!=null && childrenEditors.length>0){
				for (int i=0; i<childrenEditors.length; i++){
					if (DataChangeManager.getInstance().tableContainsEditor(childrenEditors[i])){
						remainingChildren.add(childrenEditors[i]);
					}
				}
			}
			if (!remainingChildren.isEmpty()){
				System.out.println("In TestEditorsWithDataChangeEvents: testDestroy()\n"
				+ "Error: table still contains children editors after destroy().\n"
				+ "children editors not removed from table: ");
				for (Iterator iter = remainingChildren.iterator();
					iter.hasNext();
					) {
					Editor element = (Editor) iter.next();
					System.out.println("Editor with editObj '"+element.getEditObj().toString()+"'.");
					fail();
				}
			}
		}
	}

	/**
	 * Private "helper" method creating an editor for the specified editObj.
	 * <br>
	 * In this way, the "real" test methods need not care for the creation 
	 * of the editor(s) themselves.
	 * 
	 * @param editObj Object for which an editor is to be created 
	 */
	private Editor createTestEditor(Object editObj) {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj);
		} catch (EditorConstructionException e) {
			System.err.println(e.getMessage());
		}
		return editor;
	}
	

}
