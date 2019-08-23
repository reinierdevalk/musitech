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
 * Created on 23.11.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.selection.LocalSelection;
import de.uos.fmt.musitech.framework.selection.SelectingEditor;
import de.uos.fmt.musitech.framework.selection.Selection;
import de.uos.fmt.musitech.framework.selection.SelectionController;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * The ContainerEditor is an Editor for Container objects. It extends
 * TypedCollectionEditor. This allows to consider the Container's Context when
 * for example adding elements.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class ContainerEditor extends TypedCollectionEditor {

    /**
     * Local working copy of the Container to edit.
     */
    Container containerCopy;	//TODO Gemeinsame Objektvariable in Superklasse?
    
    /**
     * Initializes this TypedCollectionEditor by setting the specified arguments
     * as the editor's <code>editObj</code>,<code>profile</code> and
     * <code>rootDisplay</code> resp. If the <code>profileName</code> in the
     * <code>profile</code> is not null, the editor's
     * <code>propertyName</code> and (via ReflectionAccess)
     * <code>propertValue</code> are set. A working copy of the
     * TypedCollection to be edited, the <code>editCollCopy</code>, is
     * created. Children and element editors are created if necessary. The
     * editor is registered at the DataChangeManager and the GUI is built.
     * 
     * @param editObject
     *            the Object edited by this ContainerEditor
     * @param profile
     *            EditingProfile spüecifying how to edit the
     *            <code>editObj</code>
     * @param rootEditor
     *            Display being the top of the display hierarchy this
     *            ContainerEditor is situated in
     */
    public void init(Object editObject, EditingProfile profile,
            Display rootEditor) {
        this.editObj = editObject;
        this.profile = profile;
        if (profile != null)
            this.propertyName = profile.getPropertyName();
        this.rootDisplay = rootEditor;
        setPropertyValue();
        determineLocalObj();
        if (children == null || !(children.length > 0))
            createChildrenEditorsII(this.rootDisplay);
        if (containerCopy.size() > 0)
            createElementEditors();
        registerAtChangeManager();
        createGUI();
    }

    /**
     * Sets the <code>containerCopy</code>. The elements of either the
     * <code>editObj</code> or the <code>propertyValue</code> are copied to
     * the <code>containerCopy</code>.
     */
    protected void determineLocalObj() {
        if (editObj instanceof Container && ((Container) editObj).size() >= 0) {  
            writeToCopy((Container) editObj);
        } else if (propertyValue != null && propertyValue instanceof Container
                && ((Container) propertyValue).size() > 0) {
            writeToCopy((Container) propertyValue);
        }
    }

    /**
     * Adds all elements of the specified Container to the
     * <code>containerCopy</code>. If the <code>containerCopy</code> has
     * contained elements, it is cleared before adding the elements of the
     * <code>container</code>. If the <code>containerCopy</code> initially
     * is null, a new BasicContainer is created with the Context of the
     * specified Container.
     * 
     * @param container
     *            Container whose elements are to be added to the
     *            <code>containerCopy</code>
     */
    private void writeToCopy(Container container) {
        if (containerCopy == null) {
            containerCopy = new BasicContainer(container.getContext());
        }
        if (containerCopy.size() > 0) {
            clearContainer(containerCopy);
        }
        for (int i = 0; i < container.size(); i++) {
            containerCopy.add(container.get(i));
        }
    }

    /**
     * Writes the elements of the <code>containerCopy</code> back to either
     * the <code>editObj</code> or <code>propertyValue</code>. All old
     * elements in the <code>editObj</code> or <code>propertyValue</code>
     * are removed before adding the elements of <code>containerCopy</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.CollectionMapEditor#writeCopyBackToEditObj()
     */
    protected void writeCopyBackToEditObj() {
        //        if (editObj instanceof Container) {
        //            clearContainer((Container)editObj);
        //            if (containerCopy!=null && containerCopy.size()>0){
        //                for (int i = 0; i < containerCopy.size(); i++) {
        //                    ((Container)editObj).add(containerCopy.get(i));
        //                }
        //            }
        //        } else if (propertyValue != null
        //                && propertyValue instanceof Container) {
        //            clearContainer((Container)propertyValue);
        //            if (containerCopy!=null && containerCopy.size()>0){
        //                for (int i = 0; i < containerCopy.size(); i++) {
        //                    ((Container)propertyValue).add(containerCopy.get(i));
        //                }
        //            }
        //        }
        Container containerToEdit = returnContainerToEdit();
        clearContainer(containerToEdit);
        if (containerCopy != null && containerCopy.size() > 0) {
            for (int i = 0; i < containerCopy.size(); i++) {
                containerToEdit.add(containerCopy.get(i));
            }
        }
    }

    /**
     * Removes all elements from the specified Container.
     * 
     * @param container
     *            Container to be cleared
     */
    private void clearContainer(Container container) {
        Container objectsToRemove = new BasicContainer();
        for (int i = 0; i < container.size(); i++) {
            objectsToRemove.add(container.get(i));
        }
        for (int i = 0; i < objectsToRemove.size(); i++) {
            container.remove(objectsToRemove.get(i));
        }
    }

    /**
     * Returns either the <code>propertyValue</code> (if its is of type
     * Container) or the <code>editObj</code> (if the
     * <code>propertyValue</code> is not a Container, but the
     * <code>editObj</code> is).
     * 
     * @return Container the Container being edited, which is either the
     *         <code>propertyValue</code> or the <code>editObj</code>
     */
    private Container returnContainerToEdit() {
        if (propertyValue instanceof Container)
            return (Container) propertyValue;
        if (editObj instanceof Container)
            return (Container) editObj;
        return null;
    }
    /**
     * Creates editors for the elements of <code>workingCopy</code> and adds
     * them to the Vector <code>elementEditors</code>.
     */
    protected void createElementEditors() {
        int count = 0;
        //for all elements in workingCopy: create an editor and add it to the
        // elementEditors
        for (Iterator iter = containerCopy.iterator(); iter.hasNext();) {
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
     * Returns a Collection containing as Objects the content of the Context of
     * the <code>containerCopy</code>. If the Context of the
     * <code>containerCopy</code> is null, this method returns null.
     * 
     * @return Collection containing the elements of the
     *         <code>containerCopy</code>'s Context, or null
     */
    private Collection getObjectsInContext() {
        if (containerCopy!=null && containerCopy.getContext() != null) {
            return getObjectsInPiece(containerCopy.getContext().getPiece());	
        }
        Container containerToEdit = returnContainerToEdit();
        if (containerToEdit != null && containerToEdit.getContext() != null) {
            //if containerToEdit has context, containerCopy should have the
            // same (and method should have already returned)
            return getObjectsInPiece(containerToEdit.getContext().getPiece());
        }
        return null;
    }

    /**
     * Returns a Collection containing as Objects those Objects in the Context
     * of the <code>containerCopy</code> which are not already contained in
     * the <code>containerCopy</code>. If there are no other Objects in the
     * Context, an empty Collection is returned.
     * 
     * @return Collection with the elements of the <code>containerCopy</code>
     *         's Context not already contained in the
     *         <code>containerCopy</code>, or an empty Collection
     */
    private Collection getOtherObjectsInContext() {
        Collection objects = new ArrayList();
        Collection objectsInContext = getObjectsInContext();
        if (objectsInContext != null && objectsInContext.size() > 0) {
            for (Iterator iter = objectsInContext.iterator(); iter.hasNext();) {
                Object element = (Object) iter.next();
                if (containerCopy != null && !containerCopy.contains(element)) {
                    objects.add(element);
                }
            }
        }
        return objects;
    }
    
    private Collection getObjectsInPiece(Piece piece){
        Collection objectsInPiece = new ArrayList();
        Container container = piece.getContainerPool();
        objectsInPiece = container.getContentsRecursiveList(objectsInPiece);
        container = piece.getNotePool();
        objectsInPiece = container.getContentsRecursiveList(objectsInPiece);
        container = piece.getAudioPool();
        objectsInPiece = container.getContentsRecursiveList(objectsInPiece);
        //TODO auch Elemente aus MetricalTimeLine und HarmonyTrack hinzufügen?
        return objectsInPiece;
    }

    /**
     * Returns the element to add. The user is offered to select this element
     * from a Collection of Objects contained in the Context of the edited
     * Container or to create a new element. If no element is selected or
     * created, null is returned.
     * 
     * @see de.uos.fmt.musitech.framework.editor.TypedCollectionEditor#getNewElement()
     */
    protected Object[] getNewElements() { //TODO getElementToAdd()
        Collection newElements = new ArrayList();
        String message = "Do you want to select from already existing objects or create a new object?";
        String[] options = { "Select an existing object",
                "Create a new object", "Cancel" };
        int option = JOptionPane.showOptionDialog(this, message, "Add element",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        switch (option) {
        case JOptionPane.YES_OPTION:
            newElements.addAll(getSelectedElements());
            break;
        case JOptionPane.NO_OPTION:
            newElements.add(getCreatedElement());
            break;
        default:
            break;
        }
        return newElements.toArray();
    }
    
    protected Object getNewElement(){
        return getNewElements()[0];
    }
    
    //TODO im Moment kann für getNewElement() nur ein einzelnes Object zurückgegeben werden
    private Collection getSelectedElements() {
        //determine objects to select from
        Collection offeredObjects = getOtherObjectsInContext();
        //create editor for objects from which to select
        CollectionEditor collEditor = null;
        Selection s = new LocalSelection();
        try {
            collEditor = (CollectionEditor) EditorFactory.createEditor(
                    offeredObjects, true, "Collection", null);
            SelectionController sc = new SelectionController(collEditor);  
            collEditor.setSelectionController(sc);
            sc.setSelection(s);
            showSelectionDialog(collEditor, s);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        if (s.getAll()!=null && !s.getAll().isEmpty()){
            return s.getAll(); //.toArray()[0];
        }
        return null;
    }
    
    private void showSelectionDialog(SelectingEditor display, final Selection selection){
        final JDialog dialog = new JDialog(new JDialog(), "Select element to add", true);
        final Selection selectionBefore = new LocalSelection();
        selectionBefore.addAll(selection.getAll(), null);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add((JComponent)display);
        JButton selectButton = new JButton("Select and close");
        selectButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                //for testing:
                Selection debugSelection = selection;
                System.out.println(debugSelection);
                dialog.dispose();
            }         
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                selection.clear(null);
                selection.addAll(selectionBefore.getAll(), null);
                dialog.dispose();
            }     
        });
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(selectButton);
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(cancelButton);
        buttonBox.add(Box.createHorizontalGlue());
        panel.add(buttonBox, BorderLayout.SOUTH);
//        JDialog dialog = new JDialog(new JDialog(), "Select element to add", true);
        dialog.getContentPane().add(panel);
        dialog.setSize(400,250);
        dialog.setVisible(true);
    }    

    private Object getCreatedElement() {
        //TODO
//        return super.getNewElement();	//bietet Containable an, reicht nicht aus
//        return null;
        
    		Object newElement = null;
    		newElement = getElementTypeAndValues();

    		if (editObj instanceof Set && newElement != null) {
    			//if (((Collection) editObj).contains(newElement)) {
    			//if (((Collection) editObjCopy).contains(newElement)) {
    			if (editCollCopy.contains(newElement)) {
    				//Should not happen, at present state, as newElement is newly created.
    				JOptionPane.showMessageDialog(
    					this,
    					"Duplicate elements are not allowed.");
    			}
    		}
    		return newElement;
    	
    }
    
    /** 
     * Asks the user to add one or more elements and adds the object or objects to
     * <code>containerCopy</code>. The user is offered to select one or more Objects 
     * among the existing objects in the Container's Context or to create a new object.
     * When the <code>containerCopy</code> is changed, the GUI is updated.
     * 
     * @see de.uos.fmt.musitech.framework.editor.CollectionEditor#addElement()
     */
    void addElement() {
		Object[] newElements = getNewElements();
		if (newElements!=null && newElements.length>0){
		    for (int i = 0; i < newElements.length; i++) {
		        containerCopy.add(newElements[i]);
		    }	
			if (!elementEditors.isEmpty()){
				elementEditors.clear();
			}
			createElementEditors();
			updateGUI();
		}
    }
    
    /** 
     * Overwrites method <code>createButtonPane()</code> of CollectionEditor in order
     * to enable resp. disable the buttons according to the content of the 
     * <code>containerCopy</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.CollectionEditor#createButtonPane()
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

		JButton replaceElement = new JButton("Replace Element");
		replaceElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				replaceElement();
			}
		});

		buttonPane.add(addElement);
		buttonPane.add(deleteElement);
		buttonPane.add(replaceElement);
		//disable delete and replace buttons if collection is empty 
		//(i.e. there are no elements to be deleted or replaced)
		if (containerCopy.size()<=0) {
			deleteElement.setEnabled(false);
			replaceElement.setEnabled(false);
		}
		buttonPane.addFocusListener(focusListener);
		addElement.addFocusListener(focusListener);
		deleteElement.addFocusListener(focusListener);
		replaceElement.addFocusListener(focusListener);
		return buttonPane;
	}
    
    void deleteElement() {
		Object elementToDelete = getElementToDelete();
		if (elementToDelete != null) {
			containerCopy.remove(elementToDelete);
			elementEditors.clear();
			createElementEditors();
			//updateEditor();
			updateGUI();
		}
	}
	
    
    /** 
     * Asks for the type of the element to edit and checks if the type is a Containable.
     * If it is, an Editor or Dialog for entering the values is opened. If it is not,
     * a message is shown. Returns the newly created element, or null if no element could
     * be created.
     * 
     * @see de.uos.fmt.musitech.framework.editor.CollectionEditor#getElementTypeAndValues()
     */
    protected Object getElementTypeAndValues() {
		Object newElement = null;
		String type = "";
		type =
			((String) JOptionPane
				.showInputDialog(
					this,
					"Please type the object type of the new element.\nThe exact package is required."));
		if (type != null) {
			type = type.trim();
			//get Class for type
			Class cla = null;
			try {
				cla = Class.forName(type);
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				showCreationFailedMessage();
			}
			if (cla != null) {
			    //check if cla implements Containable
			    if (!(Containable.class.isAssignableFrom(cla))) {
					JOptionPane.showMessageDialog(
						this,
						"The type "
							+ type
							+ " is no type of this collection.\n A Containable type is needed.\n");
					return null;
				}
				if (cla.isPrimitive()
					|| ObjectCopy.isElementaryClass(cla)
					|| cla.isAssignableFrom(String.class))
					newElement = createNewPrimitiveValue(cla);
				else {
					newElement = createNewComplexValue(cla, "New Element");
				}
			}
		}
		return newElement;
	}



}