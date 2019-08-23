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
 * Created on 01.09.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * This class provides GUI and basic functionality for editing objects of type Map.
 * It is similar to CollectionEditor, but takes into account the structure of map elements 
 * having key and value.
 * <br>
 * The Map Editor edits the map's properties (using "children editors") and the map's
 * elements (using "element editors") wrapped behind an "Edit"-button. That is, it is
 * a complex editor delegating functionality to other, simple or complex, editors.
 * <br>
 * For editing elements in the map, the functions of adding a new element, deleting an
 * already existing element or replacing an element are provided.
 * In case of adding an element, to assure that the input is reasonable as a new element
 * is up to the user. There are no control mechanisms except of checking if the newly 
 * proposed key is already existing in the map.
 * In case of "replacing" an element, there are two possible choices: 
 * either the key of an element can be changed maintaining the element's value, or the 
 * value of an item with given key can be replaced. In fact, in both cases a new element
 * is added given either key or value of the element to replace resp. the "old" element
 * being removed.
 * 
 * @author Kerstin Neubarth
 *
 */
public class MapEditor extends CollectionMapEditor {

	/**
	 * Working copy of <code>editObj</code> to be operated on 
	 * in adding, deleting or replacing elements.
	 * Contains only the elements of <code>editObj</code>, but no properties.
	 * It is set in method <code>init(Object, String, String)</code>.
	 * The content of this copy is written back to <code>editObj</code> in method 
	 * <code>applyChanges()</code>.
	 */
	Map editMapCopy = new HashMap();

	/** 
	 * Initializes the editor's attributes according to the specified arguments.
	 * Sets the <code>editMapCopy</code>, registers the editor at the DataChangeManager
	 * and creates the GUI.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Editor#init(java.lang.Object, de.uos.fmt.musitech.framework.editor.EditingProfile, de.uos.fmt.musitech.framework.editor.Editor)
	 */
	public void init(
		Object editObject,
		EditingProfile profile,
		Display rootEditor) {
		this.editObj = editObject;
		this.profile = profile;
		if (profile != null)
			this.propertyName = profile.getPropertyName();
		this.rootDisplay = rootEditor;
		setPropertyValue();
		if (editObj instanceof Map && !((Map) editObj).isEmpty()) {
			if (!editMapCopy.isEmpty())
				editMapCopy.clear();
			editMapCopy.putAll((Map) editObj);
		} else if (
			propertyValue != null
				&& propertyValue instanceof Map
				&& !((Map) propertyValue).isEmpty()) {
			if (!editMapCopy.isEmpty())
				editMapCopy.clear();
			editMapCopy.putAll((Map) propertyValue);
		}
		if (children == null || !(children.length > 0))
			createChildrenEditorsII(this.rootDisplay);
		if (!editMapCopy.isEmpty())
			createElementEditors();
		registerAtChangeManager();
		createGUI();
	}

	/**
	 * Creates editors for the elements of <code>editMapCopy</code> and adds them to
	 * the Vector <code>elementEditors</code>.
	 */
	protected void createElementEditors() {
//		if (!elementEditors.isEmpty())
//			elementEditors.clear();
		for (Iterator iter = editMapCopy.keySet().iterator();
			iter.hasNext();
			) {
			Object key = iter.next();
			Object value = editMapCopy.get(key);
			//create EditingProfile with specified element editortype and key of element as label
			//TODO allow other than String reperesentations of key?
			EditingProfile elementProfile =
				new EditingProfile(
					key.toString(),
					EDITORTYPE_FOR_ELEMENTS,
					null);
			//TODO vorläufig (auf 3 Modi erweitern: read only, navigate, editable)
			if (profile.isReadOnly())
			  elementProfile.setReadOnly(true);
			  
			//create element editor according to profile 
			Editor editor = null;
			try {
				editor = EditorFactory.createEditor(value, elementProfile);
				elementEditors.add(editor);
			} catch (EditorConstructionException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/** 
	 * Overwrites method <code>updateEditor()</code> in class 
	 * <code>AbstractComplexEditor</code> to initialize <code>editMapCopy</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Editor#updateEditor()
	 */
	public void updateDisplay() {
		dataChanged = false;
		setValueCreated(false);
		if (editMapCopy != null) {
			editMapCopy.clear();
			if (!((Map) editObj).isEmpty())
				editMapCopy.putAll((Map) editObj);
		}
		if (children != null && children.length > 0) {
			for (int i = 0; i < children.length; i++) {
				children[i].updateDisplay();
			}
		}
		if (!elementEditors.isEmpty())
			elementEditors.clear();
		createElementEditors();
		updateGUI();
	}

	/** 
	 * Implements abstract method <code>writeCopyBackToEditObj()</code> of class
	 * <code>CollectionMapEditor</code>. Updates <code>editObj</code> to the elements
	 * of <code>editMapCopy</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.CollectionMapEditor#writeCopyBackToEditObj()
	 */
	protected void writeCopyBackToEditObj() {
		if (editObj instanceof Map) {
			((Map) editObj).clear();
			if (editMapCopy != null && !editMapCopy.isEmpty()) {
				((Map) editObj).putAll(editMapCopy);
			}
		} else {
			if (propertyValue != null && propertyValue instanceof Map) {
				((Map) propertyValue).clear();
				if (editMapCopy != null && !editMapCopy.isEmpty()) {
					((Map) propertyValue).putAll(editMapCopy);
				}
			}
		}
	}

	/** 
	 * Overwrites method <code>getEditedData()</code> of class <code>AbstractComplexEditor</code>
	 * to check whether entries have been added, deleted or replaced. 
	 * Returns a non-empty Collection if new entries have been inserted or existing
	 * entries have been removed and/or if properties have changed.
	 * 
	 * @return Collection containing changed objects
	 * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
	 */
	public Collection getEditedData() {
		ArrayList changedData = new ArrayList();
		//add editObj and its non-primitive properties if properties have been changed
		changedData.addAll(super.getEditedData());
		//add editObj if entries have been added, removed or replaced
		if (hasMapChanged() && !changedData.contains(editObj))
			changedData.add(editObj);
		return changedData;
	}

	/**
	 * Returns true if entries have been added, deleted or replaced, false otherwise.
	 * This method does not check if properties have been changed.
	 * 
	 * @return true if entries in the map have been added, deleted or replaced, false otherwise
	 */
	protected boolean hasMapChanged() {
		if (editMapCopy.equals((Map) editObj))
			return false;
		return true;
	}

	//	protected boolean elementsHaveChanged() {
	//		if (editMapCopy.entrySet().containsAll(((Map) editObj).entrySet()))
	//			return false;
	//		return true;
	//	}

	// ------------------------- methods for GUI ------------------------------

	/**
	 * Adds the editors used to display the map's elements to the JPanel 
	 * textfieldPanel and the elements' key (on JLabels) to the JPanel labelPanel.
	 * The element editors are wrapped behind a PopUpWrapper showing itself as an
	 * "edit"-button. 
	 * 
	 * @param labelPanel JPanel meant to contain the JLabels displaying the elements' keys
	 * @param textfieldPanel JPanel meant to contain the element editors
	 */
/*	protected void fillInElementEditors(
		JPanel labelPanel,
		JPanel textfieldPanel) {
			
//		if (editObj instanceof Map) {
//			elementEditors.clear();
//			for (Iterator iter = editMapCopy.keySet().iterator();
//				iter.hasNext();
//				) {
//				Object key = iter.next();
//				Object value = editMapCopy.get(key);
//
//				EditingProfile profile =
//					new EditingProfile(
//						key.toString(),
//						EDITORTYPE_FOR_ELEMENTS,
//						null);
//				Editor editor = null;
//				try {
//					editor = EditorFactory.createEditor(value, profile);
//					elementEditors.add(editor);
//					textfieldPanel.add((Component) editor);
//					JLabel label = new JLabel(key.toString());
//					labelPanel.add(label);
//				} catch (EditorConstructionException e) {
//					System.err.println(e.getMessage());
//				}
//			}
//		}

		//TODO wenn o.k., in CollectionMapEditor verschieben
		for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
			Editor elementEditor = (Editor) iter.next();
			if (elementEditor instanceof AbstractEditor){
				labelPanel.add(new JLabel(((AbstractEditor)elementEditor).getLabeltext()));
			}	
			textfieldPanel.add((Component)elementEditor);
		}

	}
	*/

	/**
	 * Returns a JLabel containing four JButtons with button texts
	 * "Add Element", "Delete Element", "Replace Key" and "Replace Value".
	 * 
	 * @return
	 */
	protected JPanel createButtonPane() {
		JPanel buttonPane = new JPanel();

		JButton addElement = new JButton("Add Element");
		addElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				addElement();
			}
		});

		JButton deleteElement = new JButton("Delete Element");
		deleteElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				deleteElement();
			}
		});

		JButton replaceKey = new JButton("Replace Key");
		replaceKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				replaceKey();
			}
		});

		JButton replaceValue = new JButton("Replace Value");
		replaceValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				replaceValue();
			}
		});

		buttonPane.add(addElement);
		buttonPane.add(deleteElement);
		buttonPane.add(replaceKey);
		buttonPane.add(replaceValue);
		//disable delete and replace buttons if the map is empty 
		//(i.e. there are no elements to be deleted or replaced)
		if (editMapCopy.isEmpty()) {
			deleteElement.setEnabled(false);
			replaceKey.setEnabled(false);
			replaceValue.setEnabled(false);
		}
		buttonPane.addFocusListener(focusListener);
		addElement.addFocusListener(focusListener);
		deleteElement.addFocusListener(focusListener);
		replaceKey.addFocusListener(focusListener);
		replaceValue.addFocusListener(focusListener);
		return buttonPane;
	}

	// --------------------- methods for editing functionality ----------------------

	/**
	 * Asks for key and value of a new element and adds the new element to the map.
	 * <br>
	 * This method is invoked when the "Add Element"-button is pressed.
	 * <br>
	 * For getting the key of the element to be added, an InformationDialog is opened.
	 * For getting the value of the element to be added, first the new element's class name 
	 * is asked for and then an editor for typing the elements properties is shown. This 
	 * editor is set into a modal JDialog to force the user to type his input before
	 * going on to other actions.
	 */
	void addElement() {
		Object newKey = getKeyOfElementToAdd();
		if (newKey != null) {
			Object newValue = getValueOfElementToAdd();
			if (newValue != null)
				editMapCopy.put(newKey, newValue);
		}
		if (!elementEditors.isEmpty())
			elementEditors.clear();
		createElementEditors();
		updateGUI();
	}

	/**
	 * Asks for the key of the element to delete and removes the element from the map.
	 */
	void deleteElement() {
		Object key = getKeyOfElementToDelete();
		if (key != null) {
			if (editMapCopy.get(key) != null)
				editMapCopy.remove(key);
		}
		elementEditors.clear();
		createElementEditors();
		updateGUI();
	}

	/**
	 * Asks for the key that is to be replaced as well as for the new key 
	 * by which it is to be replaced.
	 * A new element with the new key and the value of the "old" element is put
	 * into the map editObj and the "old" element is removed from the map.
	 */
	void replaceKey() {
		Object key = getKeyOfElementToReplace();
		if (key != null) {
			Object newKey = getNewKeyForElement();
			if (newKey != null) {
				if (editMapCopy.get(key) != null) {
					editMapCopy.put(newKey, editMapCopy.get(key));
					editMapCopy.remove(key);
				}
			}
		}
		elementEditors.clear();
		createElementEditors();
		updateGUI();
	}

	/**
	 * Asks for the key of the element the value of which is to be replaced
	 * as well as for the new value. The element having the "old" value is removed
	 * and a new object with the "old" key but new value is put into the map.
	 * 
	 * For getting the new value, an editor is opened in which the elements properties
	 * can be set.
	 */
	void replaceValue() {
		Object key = getKeyOfElementToReplace();
		if (key != null) {
			if (editMapCopy.get(key) != null) {
				Object newValue = getNewValueForElement();
				if (newValue != null) {
					editMapCopy.remove(key);
					editMapCopy.put(key, newValue);
				}
			}
		}
		elementEditors.clear();
		createElementEditors();
		updateGUI();
	}

	/**
	 * Returns the key of a new element.
	 * The key is asked for in an InputDialog and the user's input is checked
	 * if matching an already existing key.
	 * 
	 * @return Object key of the new element, or null if the input matches an already existing key
	 */
	private Object getKeyOfElementToAdd() {
		String newKey =
			((String) JOptionPane
				.showInputDialog(
					this,
					"Please type the key of the new element."));
		if (newKey != null) {
			newKey = newKey.trim();
			if (newKey.equals("")) {
				JOptionPane.showMessageDialog(
					this,
					"The key must have at least one character.");
				return null;
			}
			if (!editMapCopy.containsKey(newKey))
				return newKey;
			else {
				JOptionPane.showMessageDialog(
					this,
					"The map already contains a key '"
						+ newKey.toString()
						+ "'.");
			}
		}
		return null;
	}

	/**
	 * Returns the value of a new element.
	 * The value's type is asked for in an InputDialog and an editor is opened
	 * in which the new element's properties can be set.
	 * 
	 * @return Object value of the new element, or null if creating a new value failed
	 */
	private Object getValueOfElementToAdd() {
		Object value = null;
		String type =
			((String) JOptionPane
				.showInputDialog(
					this,
					"Please type the object type of the element you want do add.\nThe exact package is required."));
		if (type != null) {
			type = type.trim();
			Class cla = null;
			try {
				cla = Class.forName(type);
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				showNoPossibleTypeMessage();
			}
			if (cla != null) {
				if (cla.isPrimitive()
					|| ObjectCopy.isElementaryClass(cla)
					|| cla.isAssignableFrom(String.class))
					value = createNewPrimitiveValue(cla);
				else {
					value = createNewComplexValue(cla, "New Element: Value");
				}
			}
		}
		return value;
	}

	/**
	 * Returns the key of the element that is to be deleted.
	 * The key is asked for in an InputDialog and the user's input is checked
	 * if matching one of the existing keys.
	 * 
	 * @return Object key of the element to be deleted, or null if the map does not contain an element with this key
	 */
	private Object getKeyOfElementToDelete() {
		String key =
			((String) JOptionPane
				.showInputDialog(
					this,
					"Please type the key of the element you want to delete."));
		if (key != null) {
			key =key.trim();
			if (editMapCopy.containsKey(key)) {
				return key;
			} else {
				JOptionPane.showMessageDialog(
					this,
					"The map does not contain the key '"
						+ key.toString()
						+ "'.\nPlease check for spelling errors.");
			}
		}
		return null;
	}

	/**
	 * Returns the key that is to be replaced.
	 * The key is asked for in an InputDialog and the user's input is checked
	 * if matching one of the existing keys.
	 * 
	 * @return Object key to be replaced, or null if the map does not contain an element with this key
	 */
	private Object getKeyOfElementToReplace() {
		String key =
			((String) JOptionPane
				.showInputDialog(
					this,
					"Please type the key of the element you want to replace."));
		if (key != null) {
			key = key.trim();
			if (editMapCopy.containsKey(key))
				return key;
			else {
				JOptionPane.showMessageDialog(
					this,
					"The map does not contain a key '" + key.toString() + "'.");
			}
		}
		return null;
	}

	/**
	 * Returns the key by which an existing key is to be replaced.
	 * The key is asked for in an InputDialog and the user's input is checked
	 * if matching an already existing key.
	 * 
	 * @return Object key by which an existing key is to be replaced, or null if this new key matches an already existing key
	 */
	private Object getNewKeyForElement() {
		String newKey =
			((String) JOptionPane
				.showInputDialog(this, "Please type the new key."));
		if (newKey != null) {
			newKey = newKey.trim();
			if (!editMapCopy.containsKey(newKey))
				return newKey;
			else {
				JOptionPane.showMessageDialog(
					this,
					"The map already contains a key '"
						+ newKey.toString()
						+ "'.");
			}
		}
		return null;
	}

	/**
	 * Returns the value by which the value of an existing element is to be replaced.
	 * The value's type is asked for in an InputDialog and an editor is opened
	 * in which the element's properties to be replaced can be set.
	 * 
	 * @return value by which an existing value is to be replaced, or null if the creation of the new value failed
	 */
	private Object getNewValueForElement() {
		Object value = null;
		String type =
			((String) JOptionPane
				.showInputDialog(
					this,
					"Please type the object type of the element you want do replace.\nThe exact package is required."));
		if (type != null) {
			type = type.trim();
			Class cla = null;
			try {
				cla = Class.forName(type);
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				showNoPossibleTypeMessage();
			}

			if (cla != null) {
				if (cla.isPrimitive()
					|| ObjectCopy.isElementaryClass(cla)
					|| cla.isAssignableFrom(String.class))
					value = createNewPrimitiveValue(cla);
				else {
					value = createNewComplexValue(cla, "New Value");
				}
			}
		}
		return value;
	}

	/**
	 * Prints the map's elements to the command line.
	 * This method is for control in testing only.
	 */
	public void printCollectionDataForTesting() {
		HashMap coll = new HashMap();
		if (propertyValue instanceof Map)
			coll.putAll((Map) propertyValue);
		else if (editObj instanceof Map)
			coll.putAll((Map) editObj);
		System.out.println("after apply, the map contains:");
		for (Iterator iter = coll.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			System.out.println(key + ": " + coll.get(key).toString());
		}
	}

}
