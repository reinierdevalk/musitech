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
 * Created on 12.08.2004
 *	
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.collection.TypedCollection;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * @author Kerstin Neubarth
 *  
 */
public class TypedCollectionEditor extends CollectionEditor {

    //	Collection editCollCopy;
    //    TypedCollection workingCopy;

    /**
     * Initializes this TypedCollectionEditor by setting the specified arguments
     * as the editor's <code>editObj</code>, <code>profile</code> and
     * <code>rootDisplay</code> resp. If the <code>profileName</code> in the
     * <code>profile</code> is not null, the editor's
     * <code>propertyName</code> and (via ReflectionAccess)
     * <code>propertValue</code> are set. A working copy of the
     * TypedCollection to be edited, the <code>editCollCopy</code>, is
     * created. Children and element editors are created if necessary. The
     * editor is registered at the DataChangeManager and the GUI is built.
     */
    public void init(Object editObject, EditingProfile profile,
            Display rootEditor) {
        this.editObj = editObject;
        this.profile = profile;
        if (profile != null)
            this.propertyName = profile.getPropertyName();
        this.rootDisplay = rootEditor;
        setPropertyValue();
        //		if (editObj instanceof TypedCollection
        //			&& ((TypedCollection) editObj).size() > 0) {
        //			writeToCopy((TypedCollection) editObj);
        //		} else if (
        //			propertyValue != null
        //				&& propertyValue instanceof TypedCollection
        //				&& ((TypedCollection) propertyValue).size() > 0) {
        //			writeToCopy((TypedCollection) propertyValue);
        //		}
        determineLocalObj();

        if (children == null || !(children.length > 0))
            createChildrenEditorsII(this.rootDisplay);

        //Modif050104
        if (editCollCopy.size() > 0)
            createElementEditors();

        registerAtChangeManager();
        createGUI();
    }

    /**
     * Writes either the elements of either <code>editObj</code> or <code>propertyValue</code>
     * to the  <code>editCollCopy</code>.
     *
     */
    protected void determineLocalObj() {
        if (editObj instanceof TypedCollection
                && ((TypedCollection) editObj).size() > 0) {
            writeToCopy((TypedCollection) editObj);
        } else if (propertyValue != null
                && propertyValue instanceof TypedCollection
                && ((TypedCollection) propertyValue).size() > 0) {
            writeToCopy((TypedCollection) propertyValue);
        }
    }

    /**
     * Writes the elements of the <code>editCollCopy</code> back to
     * <code>editObj</code>. Elements that do not of the types contained in
     * the <code>editObj</code> are not added.
     * 
     * @see de.uos.fmt.musitech.framework.editor.CollectionMapEditor#writeCopyBackToEditObj()
     */
    protected void writeCopyBackToEditObj() {
        if (editObj instanceof TypedCollection) {
            ((TypedCollection) editObj).clear();
            if (editCollCopy != null && editCollCopy.size() > 0) {
                for (Iterator iter = editCollCopy.iterator(); iter.hasNext();) {
                    Object element = (Object) iter.next();
                    ((TypedCollection) editObj).add(element); //TODO bei
                                                              // NoteList:
                                                              // NullPointerException,
                                                              // wenn ScoreNote
                                                              // keine metrical
                                                              // time hat
                }
            }
        } else if (propertyValue != null
                && propertyValue instanceof TypedCollection) {
            ((TypedCollection) propertyValue).clear();
            if (editCollCopy != null && editCollCopy.size() > 0) {
                for (Iterator iter = editCollCopy.iterator(); iter.hasNext();) {
                    Object element = (Object) iter.next();
                    ((TypedCollection) propertyValue).add(element);
                }
            }
        }
    }

    /**
     * Adds all elements of the specified TypedCollection to
     * <code>editCollCopy</code>. If <code>editCollCopy</code> initially is
     * not empty, it is cleared before adding the <cdoe>typedColl</code>, so
     * that it will only contain the elements of the specified TypedCollection.
     * 
     * @param typedColl
     *            TypedCollection to be copied to <code>editCollCopy</code>
     */
    private void writeToCopy(TypedCollection typedColl) {
        if (editCollCopy == null) {
            editCollCopy = new ArrayList();
        }
        if (!editCollCopy.isEmpty()) {
            editCollCopy.clear();
        }
        for (Iterator iter = typedColl.iterator(); iter.hasNext();) {
            Object element = (Object) iter.next();
            editCollCopy.add(element);
        }
    }

    /**
     * Returns a new element of one of (several) possible types. This method is
     * invoked in case <code>editObj</code> is of type
     * <code>de.uos.fmt.musitech.utility.TypedCollection</code>. The user is
     * offered a selection of possible types to choose from.
     * 
     * @return Object new element of the type chosen by the user out of a
     *         selection of possible types, or null
     */
    protected Object getNewElement() {
        Object element = null;
        Class[] possibleTypes = ((TypedCollection) editObj).getTypes();
        Class chosenType = null;
        try {
            chosenType = (Class) JOptionPane.showInputDialog(this,
                    "Please choose the type of the new element.",
                    "CollectionEditor: New Element", JOptionPane.PLAIN_MESSAGE,
                    null, possibleTypes, possibleTypes[0]);
        } catch (HeadlessException e) {
            e.printStackTrace();
        }
        if (chosenType != null)
            if (chosenType.isPrimitive()
                    || ObjectCopy.isElementaryClass(chosenType)
                    || chosenType.isAssignableFrom(String.class))
                //				element = createNewPrimitiveElement(chosenType);
                element = createNewPrimitiveValue(chosenType);
            else
                element = createNewComplexValue(chosenType, "New Element");
        return element;
    }

    /**
     * Returns true if elements have been added, deleted or replaced, false
     * otherwise. This method does not check if properties have been changed.
     * 
     * @return true if elements have been added, deleted or replaced, false
     *         otherwise
     */
    protected boolean hasCollectionChanged() {
        boolean same = true;
        if (editCollCopy.size() == ((TypedCollection) editObj).size()) {
            for (Iterator iter = editCollCopy.iterator(); iter.hasNext();) {
                Object element = (Object) iter.next();
                if (!((TypedCollection) editObj).contains(element))
                    same = false;
            }
        } else
            same = false;
        return !same;
    }

    /**
     * Creates editors for the elements of <code>workingCopy</code> and adds
     * them to the Vector <code>elementEditors</code>.
     */
    protected void createElementEditors() {
        int count = 0;
        //for all elements in workingCopy: create an editor and add it to the
        // elementEditors
        for (Iterator iter = editCollCopy.iterator(); iter.hasNext();) {
            Object element = iter.next();
            Editor editor = null;
            try {
                StringBuffer textBuffer = new StringBuffer("Element ");
                textBuffer.append(count++);
                String text = new String(textBuffer);
                //create EditingProfile profile with specified editortype and
                // element number as label
                EditingProfile elementProfile = new EditingProfile(text,
                        EDITORTYPE_FOR_ELEMENTS, null);
                //TODO vorläufig (auf 3 Modi erweitern: read only, navigate,
                // editable)
                if (profile.isReadOnly())
                    elementProfile.setReadOnly(true);

                editor = EditorFactory.createEditor(element, elementProfile);
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
    public void updateDisplay() {
        dataChanged = false;
        setValueCreated(false);
        if (editCollCopy != null) {
            editCollCopy.clear();
            determineLocalObj();
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

}