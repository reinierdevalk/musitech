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
 * Created on 08.12.2003
 *
 */
package de.uos.fmt.musitech.metadata;

import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.AbstractEditor;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorPanel;
import de.uos.fmt.musitech.framework.editor.StringEditor;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * JUnit tests for class <code>MetaDataItemEditor</code>.
 * 
 * @author Kerstin Neubarth
 *
 */
public class MetaDataItemEditorTest extends TestCase {

	/**
	 * Tests creating a MetaDataItem given a MetaDataItem.
	 * <br>
	 * <br>The editor is shown inside a modal JDialog. You should hide the line
	 * "showEditor(editor);" behind comments if you do not want to wait for the user
	 * to close the dialog during testing.
	 * <br>
	 * <br>In some methods, the editor in its declaration is set to a more special
	 * editortype (not just <code>Editor</code>) so that a type cast is only necessary 
	 * once when creating the editor.
	 */
	public static void testCreateMDItemEditor() {
		//provide an editObj
		MetaDataValue metaDataValue = new MetaDataValue("string", "Johannes Brahms");
		MetaDataItem mdItem = new MetaDataItem("Composer");
		mdItem.setMetaValue(metaDataValue);
		//create editor
		Editor editor = createTestEditor(mdItem);
		//display editor
		//		showEditor(editor);
		//check editor
		if (!(editor instanceof MetaDataItemEditor)) {
			System.out.println(
				"In MetaDataItemEditorTest: createMDITemEditor()\n"
					+ "The created editor is not of type MetaDataItemEditor.");
			fail();
		}

	}

	/**
	 * Tests creating a MetaDataItemEditor according to a specified EditingProfile.
	 * <br>
	 * <br>The editor is shown inside a modal JDialog. You should hide the line
	 * "showEditor(editor);" behind comments if you do not want to wait for the user
	 * to close the dialog during testing.
	 */
	public static void testCreateMDItemEditorWithProfile() {
		//provide an editObj
		MetaDataValue metaDataValue = new MetaDataValue("string", "Johannes Brahms");
		MetaDataItem mdItem = new MetaDataItem("Composer");
		mdItem.setMetaValue(metaDataValue);
		//create EditingProfile
		EditingProfile profile =
			new EditingProfile(mdItem.getKey(), "MetaDataItem", null);
		//create editor
		Editor editor = createTestEditor(mdItem, profile);
		//display editor
		//		showEditor(editor);
		//check editor
		if (!(editor instanceof MetaDataItemEditor)) {
			System.out.println(
				"In MetaDataItemEditorTest: createMDItemEditor()\n"
					+ "The created editor is not of type MetaDataItemEditor.");
			fail();
		}
	}

	/**
	 * Tests the editor registering at the DataChangeManager. 
	 * Therefore, after the editor has been initialized and should have registered 
	 * at the DataChangeManager, the <code>table</code> of the DataChangeManager holding 
	 * the DataChangeListeners is checked if knowing the MetaDataItemEditor.
	 */
	public static void testRegisterAtChangeManager() {
		//provide an editObj
		MetaDataValue metaDataValue = new MetaDataValue("string", "Johannes Brahms");
		MetaDataItem mdItem = new MetaDataItem("Composer");
		mdItem.setMetaValue(metaDataValue);
		//create editor
		Editor editor = createTestEditor(mdItem);
		//check the table of the DataChangeManager if containing this editor
		if (!DataChangeManager.getInstance().tableContainsEditor(editor)) {
			System.out.println(
				"In MetaDataItemEditorTest: testRegisterAtChangeManager()\n"
					+ "The ChangeManager does not know about the MetaDataItemEditor.");
			fail();
		}
	}

	/**
	 * Tests the editor resigning from the DataChangeManager when being destroyed.
	 * Therefore, after the editor has been destroyed, the <code>table</code>
	 * of the DataChangeManager i checked if still containing the MetaDataItemEditor.
	 * (Checking the <code>table</code> before destroying the editor ensures
	 * htat the editor in fact had been registered.	 
	 */
	public static void testResignFromChangeManager() {
		//provide an editObj
		MetaDataValue metaDataValue = new MetaDataValue("string", "Johannes Brahms");
		MetaDataItem mdItem = new MetaDataItem("Composer");
		mdItem.setMetaValue(metaDataValue);
		//create editor
		Editor editor = createTestEditor(mdItem);
		//check the table of the DataChangeManager if containing this editor
		if (!DataChangeManager.getInstance().tableContainsEditor(editor)) {
			System.out.println(
				"In MetaDataItemEditorTest: testRegisterAtChangeManager()\n"
					+ "The ChangeManager does not know about the MetaDataItemEditor.");
			fail();
		}
		//editor resigns from DataChangeManager
		editor.destroy();
		//check table of DataChangeManager if still containing the editor
		if (DataChangeManager.getInstance().tableContainsEditor(editor)) {
			System.out.println(
				"In MetaDataItemEditorTest: testRegisterAtChangeManager()\n"
					+ "The ChangeManager still considers the MetaDataItemEditor an active DataChangeListener.");
			fail();
		}

	}

	
	/**
	 * Tests method <code>getEditedData()</code> for a MetaDataItemEditor.
	 * The method is meant to return a non-empty collection only if data has really
	 * been changed. The method is checked before and after changing the editor's data.
	 * <br>
	 * <br>N.B.: Changing data here is achieved by changing the <code>editObj</code> instead
	 * of the <code>propertyValue</code> or the working copy of <code>editObj</code>
	 * as neither of these two is reachable from outside the editor. 
	 * As the <code>propertyValue</code> resp. the copy remains unchanged, there is a
	 * difference between data after changing <code>editObj</code>, so the condition 
	 * for applying method <code>hasDataChanged()</code> is met. 
	 */
	//Anm: setzt Methode zur Überprüfung von Wertegleichheit in ObjectCopy voraus (vgl. has DataChanged())
	 public static void testGetEditedData() {
		boolean error = false;
		//provide an editObj
		MetaDataValue metaDataValue = new MetaDataValue("string", "Johannes Brahms");
		MetaDataItem mdItem = new MetaDataItem("Composer");
		mdItem.setMetaValue(metaDataValue);
		//create editor
		Editor editor = createTestEditor(mdItem);
		//check before changing data
		Collection changedData = editor.getEditedData();
		if (!changedData.isEmpty()){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testGetEditedData()\n"
			+ "getEditedData() returns an unempty collection although data has not been changed.");
			changedData.clear();
		}
		//change data
		String newValue = "Robert Schumann";
		MetaDataItem data = (MetaDataItem)editor.getEditObj();
		data.getMetaDataValue().setMetaValue(newValue);
		//get changed data
		changedData = editor.getEditedData();
		//check changed data
		if (changedData.isEmpty()){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testGetEditedData()\n"
			+ "getEditedData() returns an empty collection after data has been changed.");
		}
		if (error)
			fail();
	}


	/**
	 * Tests method <code>getClassOfValue(MetaDataValue)</code>. This method is expected
	 * to return a Class derived on basis of the <code>mimeType</code> given in the
	 * specified MetaDataValue.
	 * <br>
	 * <br>Testing is rather "static": One Array of mimetypes to be tested and one
	 * Array containing the corresponding classes (i.d. the objetcs the method is to
	 * return) are defined. The Object returned by method <code>getClassOfValue()</code>
	 * is checked against the appropriate element of the Array <code>classes</code>.
	 * <br>So, if you want to add further mimetypes to Array <code>mimetypes</code>
	 * for testing, be careful in adding the required classes to the Array 
	 * <code>classes</code>.
	 */
	public static void testGetClassOfValue(){
		boolean error = false;
		MetaDataItem item = new MetaDataItem("Test");
		//mimetypes appearing in the field mimeType of the specified MetaDataValue
		//and the corresponding classes
		//TODO um weitere mimetypes (und classes) erweitern
		String[] mimetypes = {"string", "int", "char"};
		Class[] classes = {String.class, Integer.class, Character.class}; 
		for (int i=0; i<mimetypes.length; i++){
			item.setMetaValue(new MetaDataValue(mimetypes[i], null));
			//create editor
			MetaDataItemEditor mdItemEditor = (MetaDataItemEditor) createTestEditor(item);
			//get class of value of metaValue
			Class cla = mdItemEditor.getClassOfValue(item.getMetaDataValue());
			//check class returned by method getClassOfValue() against required class
			//(might be value converted to String, in case of default valueEditor)
			if (!classes[i].isAssignableFrom(cla) && !String.class.isAssignableFrom(cla)){
				error = true;
				System.out.println("In MetaDataItemEditorTest: testGetClassOfValue()\n"
				+ "Method getClassOfValue(MetaDataValue) does not return correct class.");
			}
		}
		if (error)
			fail();	
	}
	
	/**
	 * Tests method <code>createNewValue()</code>. This method is expected to create
	 * a new <code>value</code> for the <code>metaValue</code> of the MetaDataItem
	 * <code>mdItem</code>.
	 */
	public static void testCreateNewValue(){
		//provide an editObj
		MetaDataItem mdItem = new MetaDataItem("Composer");
		String mimetype = "string";
		Class cla = String.class;
		mdItem.setMetaValue(new MetaDataValue(mimetype, null));
		//create editor
		MetaDataItemEditor mdItemEditor = (MetaDataItemEditor) createTestEditor(mdItem);
		//eventually hide behind comments:
//		showEditor(mdItemEditor);
		//create new value
//		mdItemEditor.createNewValue();
		mdItemEditor.applyChanges();
		//check newly created value
		MetaDataItem editObj = (MetaDataItem) mdItemEditor.getEditObj();
		Object value = editObj.getMetaDataValue().getMetaValue();
		if (value==null){
			System.out.println("In MetaDataItemEditorTest: testCreateNewValue()\n"
			+ "value is null (correct if 'Abbrechen' has been chosen).");
		}
		if (value!=null && !cla.isAssignableFrom(value.getClass())){
			System.out.println("In MetaDataItemEditorTest: testCreateNewValue()\n"
			+ "Newly created value is not of correct class.");
			fail();
		}	
	}
	
	/**
	 * Tests editing a MetaDataItem as a property. That is, the MetaDataItemEditor 
	 * used for editing the MetaDataItem is the child editor of a complex editor.
	 * <br>
	 * For testing, an instance of the inner class <code>ObjWithMDItemProperty</code> 
	 * is provided as the <code>editObj</code>. The complex editor editing this instance
	 * is checked if having a MetaDataItemEditor among its children editors and the 
	 * child editor being a MetaDataItemEditor is checked for its <code>editObj</code>
	 * and for the <code>propertyName</code> of its EditingProfile 
	 * (as the <code>propertyValue</code> is not reachable from outside the editor).  
	 */
/*	public static void testMDItemAsPropertyValue(){
		//provide editObj
		ObjWithMDItemProperty editObj = new ObjWithMDItemProperty();
		//create editor for editObj
		Editor editor = createTestEditor(editObj);
		//check editor
		boolean error = false;
		if (editor instanceof AbstractComplexEditor){
			Editor[] childrenEditors = ((AbstractComplexEditor)editor).getChildren();
			boolean mdItemEditor = false;
			if (childrenEditors!=null){
				for (int i=0; i<childrenEditors.length; i++){
//					if (childrenEditors[i] instanceof MetaDataItemEditor){
				    if (childrenEditors[i] instanceof PopUpEditor){
						mdItemEditor = true;
						Object childObj = childrenEditors[i].getEditObj();
						if (mdItemEditor){
							if (!(childObj instanceof ObjWithMDItemProperty)){
							 	error = true;
								System.out.println("In MetaDataItemEditorTest: testMDItemAsPorpertyValue()\n"
									+ "editObj of child MetaDataItemEditor is not correct.");
							}	
							Object childPropertyName = childrenEditors[i].getEditingProfile().getPropertyName();
							if (!childPropertyName.toString().equalsIgnoreCase("mdItemProperty")){
								error = true;
								System.out.println("In MetaDataItemEditorTest: testMDItemAsPropertyValue()\n"
									+ "propertyName of child MetaDataItemEditor is not correct.");
							}
						}
					}
				}
			}
			if (!mdItemEditor){
				error = true;
				System.out.println("In MetaDataItemEditorTest: testMDItemAsPropertyValue()\n"
				+ "childrenEditors does not contain an MetaDataItemEditor.");
			}	
		}else{
			 error = true;
			 System.out.println("In MetaDataItemEditorTest: testMDItemAsPropertyValue()\n"
			 + "Editor for ObjWithMDItemProperty is not a complex editor."); 
		}
		if (error)
			fail();
	}
*/
	
	/**
	 * Inner class providing an object with has a MetaDataItem as one of its properties.
	 * This class is used in test method <code>testMDItemAsPropertyValue()</code>.
	 */
	static public class ObjWithMDItemProperty{
		//fields are defined as public to keep this class clear from the getters and setters
		//which would otherwise be necessary (e.g. for using ReflectionAccess)
		public int number;
		public String name;
		public MetaDataItem mdItemProperty;
		//Constructors
		public ObjWithMDItemProperty(){
			number = 10;
			name = "TestName";
			mdItemProperty = new MetaDataItem("TestKey");
			mdItemProperty.setMetaValue(new MetaDataValue("string", "TestValue"));
		}
		public ObjWithMDItemProperty(MetaDataItem mdItem){
			number = 10;
			name = "TestName";
			mdItemProperty = mdItem;
		}
		@Override
		public String toString(){
			StringBuffer objectToString = new StringBuffer();
			String value = "null"; 
			if (mdItemProperty.getMetaDataValue()!=null){
				Object v = mdItemProperty.getMetaDataValue().getMetaValue();
				if (v!=null)
					value = v.toString();
			}
			objectToString.append(this.getClass().toString()+"\t")
				.append("  number: "+number)
				.append("  name: "+name)
				.append("  Key of mdItemProperty: "+mdItemProperty.getKey())
				.append("  value of metaValue of mdItemProperty: "+value);
			return objectToString.toString();
		}
	}
	
	/**
	 * Tests editing an MetaDataItem as <code>propertyValue</code> whose
	 * <code>value</code> of <code>metaValue</code> is null.
	 */
	public static void testMDItemAsNullProperty(){
		//provide editObj
		MetaDataItem itemWithNullValue = new MetaDataItem("TestKey");
		itemWithNullValue.setMetaValue(new MetaDataValue("string", null));
		ObjWithMDItemProperty editObj = new ObjWithMDItemProperty(itemWithNullValue);
		//create editor
		Editor editor = createTestEditor(editObj);
		//check editor
//		showEditor(editor);
	}
	
	/**
	 * Tests method <code>applyChanges()</code> for a MetaDataItem being the 
	 * outmost editor, i.d. for the MetaDataItem to be edited being the 
	 * <code>editObj</code> (not the <code>propertyValue</code>).
	 * <br>For testing, the text in the <code>valueEditor</code> (of type StringEditor)
	 * is changed, and then <code>applyChanges()</code> is called.
	 * Afterwards, the <code>editObj</code> of the MetaDataItemEditor is checked
	 * against its state before having changed data (recorded in some variables):
	 * It is to be the same object, having changed only the <code>value</code> of
	 * its <code>metaValue</code> (which in this case is a String).
	 */
	public static void testApplyChangesForEditObj(){
		//provide editObj
		MetaDataItem editObjItem = new MetaDataItem("Performing Artist(s)");
		editObjItem.setMetaValue(new MetaDataValue("string", "Il Giardino Armonico"));
		//create editor
		MetaDataItemEditor mdItemEditor = (MetaDataItemEditor)createTestEditor(editObjItem);
		//check editObj befor changing data
		MetaDataItem itemOfEditor = (MetaDataItem) mdItemEditor.getEditObj();
		Long uid1 = itemOfEditor.getUid();
		String valueClass1 = itemOfEditor.getMetaDataValue().getMetaValue().getClass().toString();
		String value1 = itemOfEditor.getMetaDataValue().getMetaValue().toString();
		//change data
		StringEditor valueEditor = (StringEditor)mdItemEditor.getValueEditor();
		valueEditor.getTextfield().setText("Concentus Musicus Wien"); //simulates user input in valueEditor
		valueEditor.applyChangesToPropertyValue(); //"simulates" loosing the focus
		//apply changed
		mdItemEditor.applyChanges();
		//check editObj after having changed data
		MetaDataItem item2 = (MetaDataItem)mdItemEditor.getEditObj();
		Long uid2 = item2.getUid();
		String valueClass2 = item2.getMetaDataValue().getMetaValue().getClass().toString();
		String value2 = item2.getMetaDataValue().getMetaValue().toString();
		boolean error = false;
		if (itemOfEditor.getMetaDataValue().getMetaValue().toString().equals(value1)){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testApplyChangesForEditObj()\n"
			+ "value of metaValue of MDItem has not changed.");
		}
		if (uid1 != uid2){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testApplyChangedForEditObj()\n"
			+ "editObj replaced by a new object.");
		}
		if (!valueClass1.equals(valueClass2)){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testApplyChangesForEditObj()\n"
			+ "Class of value of metaValue has changed.");
		}
		if (value1.equals(value2)){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testApplyChangesForEditObj()\n"
			+ "value of metaValue has not changed.");
		}
		if (error)
			fail();
//		showEditor(mdItemEditor);
		mdItemEditor.applyChanges();
		//editObj after changing
		System.out.println("MetaDataItemEditorTest: testApplyChangesForPropertyValue()");
		System.out.println("editObj at end of test method:"+ mdItemEditor.getEditObj().toString());
	}
	
	/**
	 * Tests method <code>applyChanges()</code> for an MetaDataItemEditor being a child
	 * editor of a complex editor.
	 */
/*	public static void testApplyChangesForPropertyValue(){
		//provide editObj
		ObjWithMDItemProperty editObj = new ObjWithMDItemProperty();
		//create editor
		AbstractComplexEditor editor = (AbstractComplexEditor)createTestEditor(editObj);
		//record values of editObj before changing data
		MetaDataItem itemProperty = ((ObjWithMDItemProperty)editor.getEditObj()).mdItemProperty;
		String value1 = itemProperty.getMetaValue().getValue().toString();
		//get children to change input in MetaDataItemEditor
		Editor[] childrenEditors = editor.getChildren();
		MetaDataItemEditor childMDItemEditor = null;
		for (int i=0; i<childrenEditors.length; i++){
			if (childrenEditors[i] instanceof MetaDataItemEditor)
				childMDItemEditor = (MetaDataItemEditor)childrenEditors[i];
		}
		//change input in childMDItemEditor (resp. in its valueEditor)
		StringEditor valueEditor = (StringEditor)childMDItemEditor.getValueEditor();
		valueEditor.getTextfield().setText("changed Value");
		valueEditor.applyChangesToPropertyValue();	//triggered when the textfield looses the focus
		//apply changes to editObj
		editor.applyChanges();
		//check edit Obj after havong changed data
		boolean error = false;
		if (itemProperty.getMetaValue().getValue().toString().equals(value1)){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testApplyChangesForPropertyValue()\n"
				+ "value of metaValue of MDItem has not changed.");
		}
		MetaDataItem item2 = ((ObjWithMDItemProperty)editor.getEditObj()).mdItemProperty;
		if (item2 != itemProperty){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testApplyChangesForPropertyValue\n"
				+ "property mdItemProperty has been replaced by a new object MetaDataItem.");
		}
		String value2 = item2.getMetaValue().getValue().toString();
		if (value2.equals(value1)){
			error = true;
			System.out.println("In MetaDataItemEditorTest: testApplyChangesForPropertyValue()\n"
				+ "value of metaValue of MDItem has not changed."); 
		}
		System.out.println("MetaDataItemEditorTest: testApplyChangesForPropertyValue()");
		System.out.println("editObj at end of testmethod:"+editor.getEditObj().toString()+".");
		if (error)
			fail();
	}
*/

	/**
	 * "Helper" method for creating an MetaDataItemEditor for a given MetaDataItem.
	 * 
	 * @param editObj Object the editor is to be created for
	 * @return Editor for the specified <code>editObj</code>
	 */
	private static Editor createTestEditor(Object editObj) {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		return editor;
	}

	/**
	 * "Helper" method for creating an MetaDataItemEditor for a MetaDataItem
	 * according to a given EditingProfile.
	 * 
	 * @param editObj Object the editor is to be created for
	 * @param profile EditingProfile specifying how the editor is to be created
	 * @return Editor for the specified <code>editObj</code>
	 */
	private static Editor createTestEditor(
		Object editObj,
		EditingProfile profile) {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		return editor;
	}

	/**
	 * "Helper" method for showing the specified editor.
	 * In order to retain the editor on the screen, the editor is shown
	 * in a modal dialog.
	 * N.B.: The buttons of the dialog do not have any functionality.
	 * 
	 * @param editor Editor to be shown
	 */
	private static void showEditor(Editor editor) {
		if (editor == null) {
			System.out.println(
				"In MetaDataItemEditorTest: testCreateMDItemEditor\n"
					+ "The editor could not be created.");
		}
		String title = ((AbstractEditor) editor).getLabeltext();
		final JDialog dialog = new JDialog(new JFrame(), "Key: " + title, true);
		EditorPanel editorPanel = new EditorPanel();
		editorPanel.addEditor(editor);
		dialog.getContentPane().add(editorPanel);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.show();
	}

}
