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
 * Created on 02.08.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.collection.TypedCollection;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * This class provides an editor for instances of the interface
 * <code>java.util.Collection</code>.
 * 
 * @author Tobias Widdra
 */
public class CollectionEditor extends CollectionMapEditor {

	// TODO dirty flag not used

	/**
	 * Working copy of editObj to be operated on in adding, deleting or
	 * replacing elements. Contains only the elements of editObj, but no
	 * properties. It is set in method <code>init(Object, String, String)</code>.
	 * The content of this copy is written to editObj in method
	 * <code>applyChanges()</code>.
	 */
	Collection editCollCopy = new ArrayList();

	/**
	 * Returns a JPanel containing three JButtons with texts "Add Element",
	 * "Delete Element" and "Replace Element".
	 * 
	 * @return JPanel containing JButtons
	 */
	@Override
	protected JPanel createButtonPane() {
		JPanel buttonPane = new JPanel();

		JButton addElement = new JButton("Add Element");
		addElement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				addElement();
			}
		});

		JButton deleteElement = new JButton("Delete Element");
		deleteElement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				deleteElement();
			}
		});

		JButton replaceElement = new JButton("Replace Element");
		replaceElement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				replaceElement();
			}
		});

		buttonPane.add(addElement);
		
		buttonPane.add(deleteElement);
		buttonPane.add(replaceElement);
		// disable delete and replace buttons if collection is empty
		// (i.e. there are no elements to be deleted or replaced)
		if (editCollCopy.isEmpty()) {
			deleteElement.setEnabled(false);
			replaceElement.setEnabled(false);
		}
		buttonPane.addFocusListener(focusListener);
		addElement.addFocusListener(focusListener);
		deleteElement.addFocusListener(focusListener);
		replaceElement.addFocusListener(focusListener);
		return buttonPane;
	}

	/**
	 * Asks for a new element and adds it to the collection <code>editObj</code>.
	 */
	//TODO Alternative: add already existing object (currently just newly created object)
	void addElement() {
		Object newElement = getNewElement();
		String position = "";
		if (newElement != null) {
			if (editObj instanceof List) {
				position = JOptionPane
						.showInputDialog(
							this,
							"Please give the index at which you want to add the new element.\n(All subsequent elements will be shifted.)");
				try {
					int index = Integer.valueOf(position).intValue();
					// normalize index
					if (index < 0)
						index = 0;
					else if (index > editCollCopy.size())
						index = editCollCopy.size();
					((List) editCollCopy).add(index, newElement);
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
					JOptionPane.showMessageDialog(this,
						"'" + position + " ' is no valid index.");
				}
			} else
				editCollCopy.add(newElement);
			if (!elementEditors.isEmpty())
				elementEditors.clear();
			createElementEditors();
			updateGUI();
		}
	}
	

	/**
	 * Asks for the element to delete and removes it from the collection
	 * <code>editObj</code>.
	 */
	void deleteElement() {
		Object elementToDelete = getElementToDelete();
		System.out.println("The element being deleted is: " + elementToDelete.getClass().toString());
		if (elementToDelete != null) {
			editCollCopy.remove(elementToDelete);
			elementEditors.clear();
			createElementEditors();
			// updateEditor();
			updateGUI();
		}
	}

	/**
	 * Replaces an element of the collection <code>editObj</code>. That is,
	 * deletes the element to be replaced and adds a new element by which the
	 * old one is to be replaced. The old and new elements are asked for in
	 * InputDialogs.
	 */
	void replaceElement() {
		Object newElement = null;
		// if editObj of type List: replace element at a specified index
		if (editObj instanceof java.util.List) {
			Object oldElement = getElementByIndex("replace");
			System.out.println("The Element to be replaced: " + oldElement.toString());
			if (oldElement != null) {
				// int index = ((AbstractList) editObj).indexOf(oldElement);
				// int index = ((AbstractList) editObjCopy).indexOf(oldElement);
				int index = ((ArrayList) editCollCopy).indexOf(oldElement);
				newElement = getNewElement();
				if (newElement != null)
					// ((AbstractList) editObj).set(index, newElement);
					// ((AbstractList) editObjCopy).set(index, newElement);
					((ArrayList) editCollCopy).set(index, newElement);
			}
		} else {
			// if editObj not of type List
			Object oldElement = getElementByLabel("replace");
			if (oldElement != null) {
				newElement = getNewElement();
				if (newElement != null) {
					editCollCopy.remove(oldElement);
					editCollCopy.add(newElement);
				}
			}
		}
		// updateEditor();
		elementEditors.clear();
		createElementEditors();
		updateGUI();
	}

	/**
	 * Returns the element to be deleted. This element is asked for in an
	 * InputDialog.
	 * 
	 * @return Object element to be deleted, or null
	 */
	protected Object getElementToDelete() {
		Object elementToDelete = null;
		if (editObj instanceof java.util.List)
			// if (editObjCopy instanceof java.util.List)
			elementToDelete = getElementByIndex("delete");
		else
			elementToDelete = getElementByLabel("delete");
		return elementToDelete;
	}

	/**
	 * Returns an element of the collection being addressed by index. This
	 * method is invoked in case <code>editObj</code> is of the type
	 * <code>java.util.List</code>. The String <code>editing</code> is used
	 * in the InputDialog that asks for the element to be edited (removed or
	 * replaced).
	 * 
	 * @param editing String to indicate the kind of editing to be done
	 * @return Object element addressed by the index given in an InputDialog, or
	 *         null
	 */
	private Object getElementByIndex(String editing) {
		Object element = null;
		String chosenIndex = "";
		chosenIndex = JOptionPane.showInputDialog(this,
			"Please type the index (an integer number) of the element you want to "
					+ editing + ".");
		if (chosenIndex != null) {
			chosenIndex = chosenIndex.trim();
			try {
				int index = (new Integer(chosenIndex)).intValue();
				// element = ((AbstractList) editObj).get(index);
				// element = ((AbstractList) editObjCopy).get(index);
				element = ((ArrayList) editCollCopy).get(index);
			} catch (ArrayIndexOutOfBoundsException aie) {
				aie.printStackTrace();
				JOptionPane.showMessageDialog(this,
					"The index must not be less than 0 or greater than "
					// + ((Collection) editObjCopy).size()
							+ editCollCopy.size() + ".");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			if (element == null) {
				JOptionPane.showMessageDialog(this,
					"There is no object with index '" + chosenIndex + " '.");
			}
		}
		return element;
	}

	/**
	 * Returns an element of the collection being addressed by its
	 * elementEditor's <code>labeltext</code>. This method is invoked in case
	 * <code>editObj</code> is not of type <code>java.util.List</code>. The
	 * String <code>editing</code> is used in the InputDialog that asks for
	 * the element to be edited (removed or replaced).
	 * 
	 * @param editing String to indicate the kind of editing to be done
	 * @return Object element addressed by its elementEditor's labeltext given
	 *         in an InputDialog, or null
	 */
	private Object getElementByLabel(String editing) {
		Object elementToRemove = null;
		String label = "";
		label = JOptionPane
				.showInputDialog(
					this,
					"Please type the labeltext for the element you want to "
							+ editing
							+ ".\nThe labeltext is the text seen on the left side of the element."
							+ "\nAfter the element has been deleted, the labeltexts will be newly set.");
		if (label != null) {
			Editor elementEditor = getElementEditor(label);
			if (elementEditor != null)
				elementToRemove = elementEditor.getEditObj();
			else {
				JOptionPane.showMessageDialog(this, "There is no labeltext '"
													+ label + "'.");
			}
		}
		return elementToRemove;
	}

	/**
	 * Returns the elementEditor with the specified <code>labeltext</code>.
	 * 
	 * @param labeltext String labeltext of the AbstractEditor to return
	 * @return AbstractEditor the elementEditor with the specified labeltext, or
	 *         null if no elementEditor with the specified labeltext has been
	 *         found
	 */
	private Editor getElementEditor(String labeltext) {
		for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
			Editor editor = (Editor) iter.next();
			if (editor.getEditingProfile().getLabel().equals(labeltext))
				return editor;
		}
		return null;
	}

	protected Object getElementToAdd() {
		// TODO
		return getNewElement();
	}

	/**
	 * Returns a new element to be added to the collection <code>editObj</code>.
	 * This element can be an additional element or can replace an other element
	 * of the collection.
	 * 
	 * @return Object new element, or null if a new element could not be created
	 */
	// TODO only newly created or also existing objects?
	protected Object getNewElement() {
		Object newElement = null;
		newElement = getElementTypeAndValues();

		if (editObj instanceof Set && newElement != null) {
			// if (((Collection) editObj).contains(newElement)) {
			// if (((Collection) editObjCopy).contains(newElement)) {
			if (editCollCopy.contains(newElement)) {
				// Should not happen, at present state, as newElement is newly
				// created.
				JOptionPane.showMessageDialog(this,
					"Duplicate elements are not allowed.");
			}
		}
		return newElement;
	}

	/**
	 * Returns a new element of the type asked for in an InputDialog. No
	 * selection of types is given. The element's values are asked for in an
	 * InputDialog in case of a "simple" data type, or in an appropriate editor
	 * in case of a "complex" data type.
	 * 
	 * @return Object new element to be specified by the user
	 */
	/*
	 * private Object getElementWithoutTypeConstraints() { Object newElement =
	 * null; String type = ""; type = ((String) JOptionPane .showInputDialog(
	 * this, "Please type the object type of the new element.\nThe exact package
	 * is required.")); if (type != null) { type.trim(); Class cla = null; try {
	 * cla = Class.forName(type); } catch (ClassNotFoundException cnfe) {
	 * cnfe.printStackTrace(); showCreationFailedMessage(); } if (cla != null) {
	 * if (cla.isPrimitive() || ObjectCopy.isPrimitiveClass(cla) ||
	 * cla.isAssignableFrom(String.class)) newElement =
	 * createNewPrimitiveValue(cla); else { newElement =
	 * createNewComplexValue(cla, "New Element"); } } } return newElement; }
	 */

	/**
	 * Returns a new element of the type asked for in an InputDialog. The
	 * element's values are asked for in an InputDialog in case of a "simple"
	 * data type, or in an appropriate editor in case of a "complex" data type.
	 * 
	 * @deprecated not elegant; TODO remove or replace by better method
	 * @return Object new element to be specified by the user, or null
	 */
	@Deprecated
	protected Object getElementTypeAndValues() {
		Object newElement = null;
		String type = "";
		type = (JOptionPane
				.showInputDialog(
					this,
					"Please type the object type of the new element.\nThe exact package is required."));
		if (type != null) {
			type = type.trim();

			Class cla = null;
			try {
				cla = Class.forName(type);
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				showCreationFailedMessage();
			}
			if (cla != null) {
				if (cla.isPrimitive() || ObjectCopy.isElementaryClass(cla)
					|| cla.isAssignableFrom(String.class))
					newElement = createNewPrimitiveValue(cla);
				else {
					newElement = createNewComplexValue(cla, "New Element");
				}
			}
		}
		return newElement;
	}

	/**
	 * Returns true if the type specified by the String argument
	 * <code>chosenType</code> is a type of the TypedCollection
	 * <code>editObj<(code>.
	 * 
	 * @param chosenType
	 * @return
	 */
	private boolean chosenTypeKnownToCollection(String chosenType) {
		boolean possibleType = false;
		if (!(editObj instanceof TypedCollection)) {
			System.out
					.println("In method chosenTypeKnownToTypedCollection of class CollectionEditor: editObj is not a TypedCollection!");
			return false;
		}
		Class[] allowedTypes = ((TypedCollection) editObj).getTypes();
		for (int i = 0; i < allowedTypes.length; i++) {
			try {
				if (allowedTypes[i].isAssignableFrom(Class.forName(chosenType)))
					possibleType = true;
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		return possibleType;
	}

	/**
	 * Returns a String indicating the types known to the TypedCollection
	 * <code>editObj</code>.
	 * 
	 * @return String list of the types of the TypedCollection
	 *         <code>editObj</code>
	 */
	private String getListOfPossibleTypes() {
		String listOfTypes = "";
		if (!(editObj instanceof TypedCollection)) {
			System.out
					.println("In method getListOfPossibleTypes of class CollectionEditor: editObj is not a TypedCollection.");
			return null;
		}
		Class[] types = ((TypedCollection) editObj).getTypes();
		for (int i = 0; i < types.length; i++) {
			listOfTypes = listOfTypes.concat(types[i].toString() + "\n");
		}
		return listOfTypes;
	}

	/**
	 * Initializes the editor's attributes according to the specified arguments.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Editor#init(java.lang.Object,
	 *      de.uos.fmt.musitech.framework.editor.EditingProfile,
	 *      de.uos.fmt.musitech.framework.editor.Editor)
	 */
	@Override
	public void init(Object editObject, EditingProfile profile,
						Display rootEditor) {
		this.editObj = editObject;
		this.profile = profile;
		if (profile != null)
			this.propertyName = profile.getPropertyName();
		this.rootDisplay = rootEditor;
		setPropertyValue();
		if (editObj instanceof Collection && !((Collection) editObj).isEmpty()) {
			// editObjCopy = ObjectCopy.copyObject(editObj);
			// ((Collection) editObjCopy).addAll((Collection) editObj);
			if (!editCollCopy.isEmpty())
				editCollCopy.clear();
			editCollCopy.addAll((Collection) editObj);
		} else if (propertyValue != null && propertyValue instanceof Collection
					&& !((Collection) propertyValue).isEmpty()) {
			if (!editCollCopy.isEmpty())
				editCollCopy.clear();
			editCollCopy.addAll((Collection) propertyValue);
		}

		if (getChildren() == null || !(getChildren().length > 0))
			createChildrenEditorsII(this.rootDisplay);

		// Modif050104
		if (!editCollCopy.isEmpty())
			createElementEditors();

		registerAtChangeManager();
		createGUI();
	}

	/**
	 * Creates editors for the elements of <code>editCollCopy</code> and adds
	 * them to the Vector <code>elementEditors</code>.
	 */
	protected void createElementEditors() {
		int count = 0;
		// for all elements in editCollCopy: create an editor and add it to the
		// elementEditors
		for (Iterator iter = editCollCopy.iterator(); iter.hasNext();) {
			Object element = iter.next();
			Editor editor = null;
			try {
				//Author: Amir Obertinca: Passing a value of java String 'Element'
				//it used to be an 'Object' String value
				StringBuffer textBuffer = new StringBuffer("Element ");
				textBuffer.append(count++);
				String text = new String(textBuffer);
				// String text = "Element ";
				// text = text.concat(new Integer(count++).toString());
				// create EditingProfile profile with specified editortype and
				// element number as label
				EditingProfile elementProfile = new EditingProfile(text,
					EDITORTYPE_FOR_ELEMENTS, null);
				// TODO provisionally (extend to 3 modes: read only, navigate, editable)
				if (profile.isReadOnly())
					elementProfile.setReadOnly(true);

				editor = EditorFactory.createEditor(element, elementProfile,this);
				//System.out.println("The Editor for each elemtnt of Collection is: " + editor);
				if (editor != null)
					elementEditors.add(editor);
			} catch (EditorConstructionException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/**
	 * Overwrites method <code>updateEditor()</code> in class
	 * <code>AbstractComplexEditor</code> to initialize
	 * <code>editCollCopy</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Editor#updateEditor()
	 */
	@Override
	public void updateDisplay() {
		dataChanged = false;
		setValueCreated(false);
		if (editCollCopy != null) {
			editCollCopy.clear();
			if (editObj instanceof Collection
				&& !((Collection) editObj).isEmpty())
				editCollCopy.addAll((Collection) editObj);
			else if (propertyValue != null
						&& propertyValue instanceof Collection
						&& !((Collection) propertyValue).isEmpty())
				editCollCopy.addAll((Collection) propertyValue);
		}
		if (getChildren() != null && getChildren().length > 0) {
			for (int i = 0; i < getChildren().length; i++) {
				getChildren()[i].updateDisplay();
			}
		}
		if (!elementEditors.isEmpty())
			elementEditors.clear();
		createElementEditors();
		updateGUI();
	}

	/**
	 * Implements abstract method <code>writeCopyBackToEditObj()</code> of
	 * class <code>CollectionMapEditor</code>. Updates <code>editObj</code>
	 * to the elements of <code>editCollCopy</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.CollectionMapEditor#writeCopyBackToEditObj()
	 */
	@Override
	protected void writeCopyBackToEditObj() {
		if (editObj instanceof Collection) {
			((Collection) editObj).clear();
			if (editCollCopy != null && !editCollCopy.isEmpty()) {
				((Collection) editObj).addAll(editCollCopy);
			}
		} else if (propertyValue != null && propertyValue instanceof Collection) {
			((Collection) propertyValue).clear();
			if (editCollCopy != null && !editCollCopy.isEmpty()) {
				((Collection) propertyValue).addAll(editCollCopy);
			}
		}
	}

	/**
	 * Overwrites method <code>getEditedData()</code> of class
	 * <code>AbstractComplexEditor</code> to check whether elements have been
	 * added, deleted or replaced. Returns a non-empty Collection if new
	 * elements have been inserted or existing elements have been removed and/or
	 * if properties have changed.
	 * 
	 * @return Collcetion containing changed objects
	 * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
	 */
	@Override
	public Collection getEditedData() {
		ArrayList changedData = new ArrayList();
		// add editObj and its properties if properties have been changed
		changedData.addAll(super.getEditedData());
		// add editObj if elements have been added, deleted or replaced
		if (hasCollectionChanged() && !changedData.contains(editObj))
			changedData.add(editObj);
		return changedData;
	}

	/**
	 * Returns true if elements have been added, deleted or replaced, false
	 * otherwise. This method does not check if properties have been changed.
	 * 
	 * @return true if elements have been added, deleted or replaced, false
	 *         otherwise
	 */
	protected boolean hasCollectionChanged() {
		if (editCollCopy.size() == ((Collection) editObj).size()) {
			if (((Collection) editObj).containsAll(editCollCopy))
				return false;
		}
		return true;
	}

	/**
	 * Prints the collection's elements to the command line. This method is for
	 * control in testing only.
	 */
	@Override
	public void printCollectionDataForTesting() {
		Vector coll = new Vector();
		if (propertyValue instanceof Collection)
			coll.addAll((Collection) propertyValue);
		else if (editObj instanceof Collection)
			coll.addAll((Collection) editObj);
		System.out.println("after apply, Collection contains:");
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			Object element = iter.next();
			System.out.println(element.toString());
		}
	}

	/**
	 * Prints the working copy's elements to the command line. This method is
	 * used for testing only.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.CollectionMapEditor#printCopyForTesting()
	 */
	@Override
	protected void printCopyForTesting() {
		System.out.println("Print editCollCopy for testing:");
		for (Iterator iter = editCollCopy.iterator(); iter.hasNext();) {
			Object element = iter.next();
			System.out.println(element.toString());
		}
	}

}
