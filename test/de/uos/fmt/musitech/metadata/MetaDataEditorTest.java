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
 * Created on 2003-06-12
 */
package de.uos.fmt.musitech.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataProfile;
import de.uos.fmt.musitech.data.metadata.MetaDataProfileManager;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;

/**
 * Test class for MetaDataEditor. Uses the JUnit Framework.
 * @author Christophe
 */
public class MetaDataEditorTest extends TestCase {

	/**
	 * Provides a MetaDataCollection for testing.
	 * 
	 * @return 
	 */
	public MetaDataCollection createCollection() {
		//Creating a couple of test MetaDataItems

		MetaDataItem performingArtist =
			new MetaDataItem("Performing Artist(s)");
		MetaDataValue robinAlciatore = new MetaDataValue("String", "Robin Alciatore");
		performingArtist.setMetaValue(robinAlciatore);

		MetaDataItem instrInRecording =
			new MetaDataItem("Instrumentation in Recording");
		MetaDataValue instrRecPiano = new MetaDataValue("string", "Piano");
		instrInRecording.setMetaValue(instrRecPiano);

		MetaDataItem iSRC = new MetaDataItem("ISRC");
		MetaDataValue iSRC4908264 = new MetaDataValue("string", "4908264");
		iSRC.setMetaValue(iSRC4908264);

		MetaDataItem composer = new MetaDataItem("Composer");
		MetaDataValue beethoven = new MetaDataValue("string", "Ludwig van Beethoven");
		composer.setMetaValue(beethoven);

		MetaDataItem yearOfComposition =
			new MetaDataItem("Year(s) of composition");
		MetaDataValue year1825 = new MetaDataValue("string", "1825-26");
		yearOfComposition.setMetaValue(year1825);

		MetaDataItem title = new MetaDataItem("Title");
		MetaDataValue elise =
			new MetaDataValue("string", "Alla Ingharese quasi un Capriccio G-dur");
		title.setMetaValue(elise);

		MetaDataItem popularTitle = new MetaDataItem("Popular Title");
		MetaDataValue wut =
			new MetaDataValue("string", "Die Wut über den verlorenen Groschen");
		popularTitle.setMetaValue(wut);

		MetaDataItem opus = new MetaDataItem("Opus/Catalogue Number");
		MetaDataValue op129 = new MetaDataValue("string", "op. 129");
		opus.setMetaValue(op129);

		MetaDataItem instrumentation = new MetaDataItem("Instrumentation");
		MetaDataValue piano = new MetaDataValue("string", "Piano");
		instrumentation.setMetaValue(piano);

		MetaDataItem publisher = new MetaDataItem("Publisher");
		MetaDataValue henle = new MetaDataValue("string", "Henle Verlag");
		publisher.setMetaValue(henle);

		MetaDataItem yearE = new MetaDataItem("Year");
		MetaDataValue year1927 = new MetaDataValue("int", new Integer(1927));
		yearE.setMetaValue(year1927);

		MetaDataItem pages = new MetaDataItem("Pages");
		MetaDataValue pageNumber = new MetaDataValue("int", new Integer(12));
		pages.setMetaValue(pageNumber);

		//for testing FloatEditor
		//		MetaDataItem floatItem1 = new MetaDataItem("Float1");
		//		MetaDataValue floatValue1 = new MetaDataValue("float", new Float(1.3f));
		//		floatItem1.setValue(floatValue1);
		//		MetaDataItem floatItem2 = new MetaDataItem("Float2");
		//		MetaDataValue floatValue2 = new MetaDataValue("float", new Float(1.5));
		//		floatItem2.setValue(floatValue2);
		//		MetaDataItem floatItem3 = new MetaDataItem("Float3");
		//		MetaDataValue floatValue3 = new MetaDataValue("float", new Float(.5));
		//		floatItem3.setValue(floatValue3);

		//Creating a MetaDataCollection and adding MetaDataItems
		MetaDataCollection testMetaDataCollection = new MetaDataCollection();
		/* for former version of MetaDataCollection (extends SortedCollection)
		testMetaDataCollection.add(performingArtist);
		testMetaDataCollection.add(iSRC);
		testMetaDataCollection.add(yearOfComposition);
		testMetaDataCollection.add(composer);
		*/
		//if MetaDataCollection extends LinkedHashMap
		testMetaDataCollection.addMetaDataItem(performingArtist);
		//		testMetaDataCollection.addMetaDataItem(iSRC);
		testMetaDataCollection.addMetaDataItem(yearOfComposition);
		testMetaDataCollection.addMetaDataItem(composer);
		testMetaDataCollection.addMetaDataItem(title);
		testMetaDataCollection.addMetaDataItem(instrumentation);
		testMetaDataCollection.addMetaDataItem(publisher);
		//		testMetaDataCollection.addMetaDataItem(yearE);
		//		testMetaDataCollection.addMetaDataItem(pages);
		testMetaDataCollection.addMetaDataItem(opus);
		testMetaDataCollection.addMetaDataItem(popularTitle);
		testMetaDataCollection.addMetaDataItem(instrInRecording);
		//for testing FloatEditor
		//		testMetaDataCollection.addMetaDataItem(floatItem1);
		//		testMetaDataCollection.addMetaDataItem(floatItem2);
		//		testMetaDataCollection.addMetaDataItem(floatItem3);

		//Adding a couple of corresponding MetaDataProfiles to collection

		//		MetaDataProfile audio = (MetaDataProfile) MetaDataProfileManager.getMetaDataProfile("Audio");
		//		testMetaDataCollection.addProfile(audio);

		MetaDataProfile recording =
			(MetaDataProfile) MetaDataProfileManager.getMetaDataProfile(
				"Recording");
		testMetaDataCollection.addProfile(recording);

		MetaDataProfile piece =
			(MetaDataProfile) MetaDataProfileManager.getMetaDataProfile(
				"Piece");
		testMetaDataCollection.addProfile(piece);
		//MetaDataProfileManager.writeProfilesToXml();	TODO Kommentar wieder löschen
		return testMetaDataCollection;
	}


	/**
	 * Tests a MetaDataEditor registering at the DataChangeManager.
	 * <br>
	 * After the editor has been created (and is supposed to have registered
	 * at the DataChangeManager, see method <code>init(...)</code>), the <code>table</code> 
	 * of the DataChangeManager is checked if containing the MetaDataCollection 
	 * (i.e. the <code>editObj</code>) and the editor.
	 */
	public void testRegisteringAtChangeManager() {
		DataChangeManager.getInstance().clearTable();
		MetaDataCollection coll = createCollection();
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		Map tableOfChangeManager = DataChangeManager.getInstance().getTable();
		if (tableOfChangeManager.containsKey(coll)) {
			ArrayList views = (ArrayList) tableOfChangeManager.get(coll);
			if (!views.contains(editor)) {
				System.out.println(
					"In MetaDataEditorTest: testRegisteringAtChangeManager()\n"
						+ "Error: table of ChangeManager does not contain the editor.");
				fail();
			}
		} else {
			System.out.println(
				"In MetaDataEditorTest: testRegisteringAtChangeManager()\n"
					+ "Error: MetaDataCollection not known to the ChangeManager.");
			fail();
		}
	}

	/**
	 * Tests destroying the editor, i.e. removing it from the <code>table</code> of the
	 * DataChangeManager.
	 * <br>
	 * After the editor has been destroyed, the <code>table</code> of the DataChangeManager
	 * is checked if still containing the editor.
	 * To be sure the editor was registered and has in fact been removed (instead of 
	 * never having been contained), the <code>table</code> is checked before destroying 
	 * the editor as well. 
	 */
	public void testResigningFromChangeManager() {
		DataChangeManager.getInstance().clearTable();
		MetaDataCollection coll = createCollection();
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		if (DataChangeManager.getInstance().tableContainsEditor(editor)) {
			editor.destroy();
			if (DataChangeManager.getInstance().tableContainsEditor(editor)) {
				System.out.println(
					"In MetaDataEditorTest: testResigningFromChangeManager()\n"
						+ "Error: editor not deleted from table of ChangeManager after destroy().");
				fail();
			}
		} else {
			System.out.println(
				"In MetaDataEditorTest: testResigningFromChangeManager()\n"
					+ "Error: Registering at ChangeManager failed. table does not contain the editor.");
			fail();
		}
	}

	/**
	 * Tests method <code>hasMetaDataCollectionChanged()</code>.
	 * <br>
	 * <br>Changing data here is achieved by changing the <code>editObj</code> instead
	 * of its working copy, because the working copy is not reachable from outside the
	 * editor. Afterwards, nevertheless, there is a difference between the <code>editObj</code>
	 * and <code>collectionCopy</code>. So the condition for testing method
	 * <code>hasMetaDataCollectionChanged()</code> is met.
	 * 
	 * <br>
	 * <br>N.B.: 
	 * For running this test method, the method <code>hasMetaDataCollectionChanged()</code>
	 * must be changed to public!
	 * <br>Therefore, this test method is surrounded by comment as long as the 
	 * method in class MetaDataCollection is not public.
	 * (This test method, when being run under the conditions described above, 
	 * does not yield any failures or errors.)
	 */
	/*
	public void testHasMetaDataCollectionChanged() {
		DataChangeManager.getInstance().clearTable();
		MetaDataCollection coll = createCollection();
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		boolean error = false;
		//check before changing data
		if (((MetaDataEditor) editor).hasMetaDataCollectionChanged()) {
			error = true;
			System.out.println(
				"In MetaDataEditorTest: testHasMetaDataCollectionChanged()\n"
					+ "Error: hasMetaDataCollectionChanged() returns true although no changes have taken place.");
		}
		//change data
		MetaDataCollection collToChange =
			(MetaDataCollection) editor.getEditObj();
		MetaDataItem newItem = new MetaDataItem("New");
		newItem.setValue(new MetaDataValue("String", "New String"));
		collToChange.addMetaDataItem(newItem);
		//check after having changed data
		if (!((MetaDataEditor) editor).hasMetaDataCollectionChanged()) {
			error = true;
			System.out.println(
				"In MetaDataEditorTest: testHasMetaDataCollectionChanged()\n"
					+ "Error: hasMetaDataCollectionChanged() does not return true although changes have taken place.");
		}
		if (error)
			fail();
	}
	*/

	/**
	 * Tests method <code>getEditedData()</code> of class <code>MetaDataEditor</code>.
	 * <br>
	 * The method is expected to return a non-empty collection only in case the
	 * data in the editor has been changed. It is checked once before changing data
	 * (in this case it should return an empty collection) and again after having 
	 * changed the MetaDataCollection (now it should return a non-empty collection).
	 * <br>
	 * <br>Changing data here is achieved by changing the <code>editObj</code> instead
	 * of its working copy, because the working copy is not reachable from outside the
	 * editor. Afterwards, nevertheless, there is a difference between the <code>editObj</code>
	 * and <code>collectionCopy</code>. So the condition for testing method
	 * <code>getEditedData()</code> is met.
	 */
	public void testGetEditedData() {
		DataChangeManager.getInstance().clearTable();
		MetaDataCollection coll = createCollection();
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		boolean error = false;
		//check before changing data
		Collection editedDataBeforeChanging = editor.getEditedData();
		if (editedDataBeforeChanging != null)
			if (!editedDataBeforeChanging.isEmpty()) {
				error = true;
				System.out.println(
					"In MetaDataEditorTest: testGetEditedData()\n"
						+ "Error: Returns a non-empty collection although the editObj has not been changed.");
			}
		//changing data
		MetaDataCollection collToChange =
			(MetaDataCollection) editor.getEditObj();
		MetaDataItem newItem = new MetaDataItem("New");
		newItem.setMetaValue(new MetaDataValue("String", "New value"));
		collToChange.addMetaDataItem(newItem);
		//check after having changed data
		Collection editedDataAfterChanging = editor.getEditedData();
		if (editedDataAfterChanging == null
			|| editedDataAfterChanging.isEmpty()) {
			error = true;
			System.out.println(
				"In MetaDataEditorTest: testGetEditedData()\n"
					+ "Error: Does not return changed data although changes have taken place.");
		}
		if (error)
			fail();
	}

	/**
	 * Tests two editors editing the same MetaDataCollection in case one editor
	 * changes the collection.
	 * This editor then sends a DataChangeEvent to the DataChangeManager which, in turn,
	 * informs all views registered for this collection. The <code>editor1</code>,
	 * having sent the DataChangeEvent itself, is to ignore the incoming DataChangeEvent,
	 * while <code>editor2</code> is to note conflicting changes.
	 */
	public void testReceivingDataChangeEvent() {
		DataChangeManager.getInstance().clearTable();
		//create 2 editors for same collection
		MetaDataCollection coll = createCollection();
		Editor editor1 = null;
		Editor editor2 = null;
		try {
			editor1 = EditorFactory.createEditor(coll);
			editor2 = EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		//editor1 changes data and informs the DataChangeManager
		MetaDataCollection coll1 = (MetaDataCollection) editor1.getEditObj();
		MetaDataItem newItem = new MetaDataItem("New");
		newItem.setMetaValue(new MetaDataValue("String", "New String"));
		coll1.addMetaDataItem(newItem);
		((MetaDataEditor) editor1).sendDataChangeEvent();
		//editor1 is checked
		boolean error = false;
		if (editor1.externalChanges()) {
			System.out.println(
				"In MetaDataEditorTest: testReceivingDataChangeEvent()\n"
					+ "Error: editor1 does not ignore a DataChangeEvent coming from itself.");
			error = true;
		}
		//editor2 is checked
		if (!editor2.externalChanges()) {
			System.out.println(
				"In MetaDataEditorTest: testReceivingDataChangeEvent()\n"
					+ "Error: editor2 does not note a DataChangeEvent.");
			error = true;
		}
		if (error)
			fail();
	}

	/**
	 * Tests the interaction of two editors editing the same MetaDataCollection
	 * in case the content of an MetaDataItem is changed in one editor.
	 * This editor then sends a DataChangeEvent to the DataChangeManager which in turn
	 * informs all views registered with the changed data. 
	 * <br>Here, MetaDataItems edited within <code>editor1</code> which have a
	 * non-empty String as <code>value</code> of their <code>metaValue</code> are
	 * changed. The corresponding MetaDataItemsEditors used by <code>editor2</code>
	 * are to note conflicting changes. For reasons of simplicity, only the number
	 * of MetaDataItemEditors knowing about conflicting data is checked.
	 */
	public void testChangingAMetaDataItem() {
		DataChangeManager.getInstance().clearTable();
		//create 2 editors for same collection
		MetaDataCollection coll = createCollection();
		MetaDataEditor editor1 = null;
		MetaDataEditor editor2 = null;
		try {
			editor1 = (MetaDataEditor) EditorFactory.createEditor(coll);
			editor2 = (MetaDataEditor) EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		//change item with non-empty String values in editor1
		Collection editors1 = editor1.getItemEditors();
		Vector changingEditors1 = new Vector();
		for (Iterator iter = editors1.iterator(); iter.hasNext();) {
			MetaDataItemEditor elementEditor = (MetaDataItemEditor) iter.next();
			MetaDataValue metaDataValue =
				((MetaDataItem) elementEditor.getEditObj()).getMetaDataValue();
			if (metaDataValue.getMetaType().equalsIgnoreCase("string")) {
				if (metaDataValue.getMetaValue()!=null && !metaDataValue.getMetaValue().equals("")){
					metaDataValue.setMetaValue("new value");
					changingEditors1.add(elementEditor);
				}
			}
		}
		editor1.sendDataChangeEvent();
		//check item editors receiving DataChangeEvent in editor2
		Collection editors2 = editor2.getItemEditors();
		Vector changingEditors2 = new Vector();
		for (Iterator iter = editors2.iterator(); iter.hasNext();) {
			MetaDataItemEditor element = (MetaDataItemEditor) iter.next();
			if (element.externalChanges()) {
				changingEditors2.add(element);
			}
		}
		//assert
		//		assertTrue(changingEditors1.size()==changingEditors2.size());
		//		assertTrue(editor2.conflictingChanges());
		boolean error = false;
		if (changingEditors1.size() != changingEditors2.size()) {
			error = true;
			System.out.println(
				"In MetaDataEditorTest.testChangingAnMetaDataItem() \n"
					+ "number of itemEditors having received DataChangeEvent does not correspond to number of item editors having changed data.");
			System.out.println(
				"Number of editors having changed data: "
					+ changingEditors1.size());
			System.out.println(
				"Number of item editors knowing about conflicting changes: "
					+ changingEditors2.size());
		}
		if (!editor2.externalChanges()) {
			error = true;
			System.out.println(
				"In MetaDataEditorTest.testChangingAnMetaDataItem() \n"
					+ "MetaDataEditor editor2 does not know about changed data.");
		}
		if (error)
			fail();
	}

	/**
	 * Tests the MetaDataItemEditors used by the MetaDataEditor registering at
	 * the DataChangeManager. Therefore, two MetaDataEditors editing the same
	 * MetaDataCollection are created. After having registered at the DataChangeManager,
	 * the <code>table</code> of the DataChangeManager is to hold two views for each
	 * "non-empty" MetaDataItem.
	 */
	public void testItemEditorsRegisteringAtChangeManager() {
		DataChangeManager.getInstance().clearTable();
		//create 2 editors for same collection
		MetaDataCollection coll = createCollection();
		MetaDataEditor editor1 = null;
		MetaDataEditor editor2 = null;
		try {
			editor1 = (MetaDataEditor) EditorFactory.createEditor(coll);
			editor2 = (MetaDataEditor) EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		//compare editObjs
		MetaDataCollection editObj1 = (MetaDataCollection) editor1.getEditObj();
		MetaDataCollection editObj2 = (MetaDataCollection) editor2.getEditObj();
		if (editObj1!= editObj2)
			System.out.println("In MetaDataEditorTest.testItemEditorsRegisteringAtChangeManager()\n"
			+ "editor1 and editor2 do not have same editObj.");
		//copy of editObj2 used to avoid ConcurrentModificationError when removing elements from editObj1
		MetaDataCollection editObj2Copy = new MetaDataCollection();
		editObj2.copyToMetaDataCollection(editObj2Copy);
		editObj2Copy.removeMetaDataCollection(editObj1);
		if (!editObj2Copy.isEmpty()){
			System.out.println("In MetaDataEditorTest.testItemEditorsRegisteringAtChangeManager()\n"
			+ "editor1 and editor2 do not have same elements.");
		}
		boolean error = false;
		Map tableOfManager = DataChangeManager.getInstance().getTable();
		for (Iterator iter = tableOfManager.keySet().iterator(); iter.hasNext();) {
			Object key = (Object) iter.next();
			if (key instanceof MetaDataItem && editObj1.containsMetaDataItem((MetaDataItem)key)){
				ArrayList views = (ArrayList)tableOfManager.get(key);
				if (views.size()!=2){
					error = true;
					System.out.println("In MetaDataEditorTest.testTwoEditorsRegisteringAtChangeManager()\n"
					+ "ChangeManager.table: MetaDataItem does not lead to 2 views.");
					System.out.println("Key: "+key.toString());
				}
			}
		}
		if (error)
			fail();
	}

	//TODO assertions in class MetaDataEditor (für private-Methoden)
	public void testCreateEmptyItemsForCollectionCopy() {
		DataChangeManager.getInstance().clearTable();
		MetaDataCollection coll = createCollection();
		//create editor (empty items are created in initializing the editor)
		MetaDataEditor editor = null;
		try {
			editor = (MetaDataEditor) EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		MetaDataCollection copy = editor.getCollectionCopy();
		//test size (copy should be greater)
		assertTrue(coll.size() < copy.size());
	}

	//TODO assertions in class MetaDataEditor (für private-Methoden)
	public void testRemoveEmptyItemsFromCollectionCopy() {
		DataChangeManager.getInstance().clearTable();
		MetaDataCollection coll = createCollection();
		//create editor (empty items are created in initializing the editor)
		MetaDataEditor editor = null;
		try {
			editor = (MetaDataEditor) EditorFactory.createEditor(coll);
		} catch (EditorFactory.EditorConstructionException e) {
			e.printStackTrace();
		}
		MetaDataCollection copy = editor.getCollectionCopy();
		//test precondition (copy should be greater)
		assertTrue(coll.size() < copy.size());
		//remove empty items
		editor.removeEmptyItemsFromCollectionCopy();
		//test size after removing empty items (copy should have same size as coll)
		assertEquals(coll.size(), copy.size());
	}

}
