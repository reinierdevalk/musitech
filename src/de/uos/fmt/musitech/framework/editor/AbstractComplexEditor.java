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
 * Created on 25.06.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import de.uos.fmt.musitech.framework.change.DataChangeListener;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.gui.interact.CompoundComponentManager;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * An <code>AbstractComplexEditor</code> is an editor for editing "complex" objects with
 * several properties to be edited. <br>
 * This is realized by containing another editor for each property - which in turn might
 * contain other editors again. These contained editors are called the "children" of this
 * editor. A <code>AbstractComplexEditor</code> should not have any GUI for user input
 * itself but rather display its children having such a GUI.
 * 
 * @author Tobias Widdra
 */
public abstract class AbstractComplexEditor extends AbstractEditor implements
        DataChangeListener {

    /**
     * The "children" editors. <br>
     * This can be an empty array but it is set to <code>new AbstractEditor[0]</code> by
     * default to be safer against null pointer exceptions.
     */
    Editor[] children = new AbstractEditor[0];

    // -------------- Getter and setter -----------------------------------------

    /**
     * Setter for <code>children</code>.
     * 
     * @param editors
     *            The editors for the child elements.
     */
    public void setChildren(Editor[] editors) {
        children = editors;
    }

    /**
     * Getter for <code>children</code>.
     * 
     * @return AbstractEditor[]
     */
    public Editor[] getChildren() {
        return children;
    }

    /**
     * Creates the children editors according to the children-EditingProfiles in
     * <code>profile</code>.
     */
    //	protected void createChildrenEditors() {
    //		if (profile.getChildren() != null) {
    //			//check if children editors are needed for editObj or property
    //			Object obj = null;
    //			if (profile.getPropertyName() == null && editObj != null)
    //				obj = editObj;
    //			else {
    //				if (propertyName != null) {
    //					ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
    //					Object property = ref.getProperty(editObj, profile.getPropertyName());
    //					if (property != null)
    //						obj = property;
    //				}
    //			}
    //			//create children editors for obj
    //			if (obj != null)
    //				try {
    //					setChildren(EditorFactory.createChildren(obj, profile));
    //				} catch (EditorConstructionException ece) {
    //					ece.printStackTrace();
    //				}
    //		}
    //	}
    /**
     * TODO wenn fehlerfrei, statt createChildrenEditors() (oder alternativ???)
     * 
     * @param root
     */
    public void createChildrenEditorsII(Display root) {
        //Modif261103
        Object obj = getObjectToEdit();
        if (profile.getChildren() == null) {
            createDefaultChildrenProfiles(obj, profile);
        }
        if (profile.getChildren() != null) {
            //			//check if children editors are needed for editObj or property
            //			Object obj = null;
            //			if (profile.getPropertyName() == null && editObj != null)
            //				obj = editObj;
            //			else {
            //				if (propertyName != null) {
            //					ReflectionAccess ref =
            //						ReflectionAccess.accessForClass(editObj.getClass());
            //					Object property =
            //						ref.getProperty(editObj, profile.getPropertyName());
            //					if (property != null)
            //						obj = property;
            //				}
            //			}
            //create children editors
            if (obj != null) {
                List<Editor> childrenEditors = new ArrayList<Editor>();
                ReflectionAccess ref = ReflectionAccess.accessForClass(obj.getClass());
                for (int i = 0; i < profile.getChildren().length; i++) {
                    Editor childEditor = null;
                    EditingProfile childProfile = profile.getChildren()[i];
                    //					if (ref.hasPropertyName(childProfile.getPropertyName()))
                    // {
                    if (childProfile.getPropertyName() != null
                        && ref.hasPropertyName(childProfile.getPropertyName())) {
                        try {
                            //Modif
                            Object property = ref.getProperty(obj, childProfile
                                    .getPropertyName());
                            //							if (property!=null)
                            if (property == null || ObjectCopy.isPrimitiveType(property)
                                || property instanceof String
                                || property instanceof Rational)

                                childEditor = EditorFactory.createEditor(obj,
                                                                         childProfile,
                                                                         root);

                            else
                                childEditor = EditorFactory.createEditor(obj,
                                                                         childProfile,
                                                                         "PopUp", root);
                        } catch (EditorConstructionException ec) {
                            ec.printStackTrace();
                        }
                    }
                    if (childEditor != null) {
                        childrenEditors.add(childEditor);
                    }
                }
                if (!childrenEditors.isEmpty()) {
                    Editor[] editorArray = new Editor[childrenEditors.size()];
                    int i = 0;
                    for (Editor element : childrenEditors) {
                        editorArray[i++] = element;
                    }
                    setChildren(editorArray);
                }
            }
        }
    }

    /**
     * @param property
     * @param root
     */
    public void createChildrenEditors(Object property, Display root) {
        if (property != null) {
            List<Editor> childrenEditors = new ArrayList<Editor>();
            ReflectionAccess ref = ReflectionAccess.accessForClass(property.getClass());
            for (int i = 0; i < profile.getChildren().length; i++) {
                Editor childEditor = null;
                EditingProfile childProfile = profile.getChildren()[i];
                if (ref.hasPropertyName(childProfile.getPropertyName())) {
                    try {
                        childEditor = EditorFactory.createEditor(property, childProfile,
                                                                 root);
                    } catch (EditorConstructionException ec) {
                        ec.printStackTrace();
                    }
                }
                if (childEditor != null) {
                    childrenEditors.add(childEditor);
                }
            }
            if (!childrenEditors.isEmpty()) {
                Editor[] editorArray = new Editor[childrenEditors.size()];
                int i = 0;
                for (Editor element : childrenEditors) {
                    editorArray[i++] = element;
                }
                setChildren(editorArray);
            }
        }
    }

    /**
     * Determines the object this editor is to edit which may be the <code>editObj</code>
     * itself or its property corresponding to <code>propertyName</code> (if
     * <code>propertyName</code> is not null).
     * 
     * @return Object to be edited
     */
    protected Object getObjectToEdit() {
        if (profile.getPropertyName() == null && editObj != null)
            return editObj;
        if (propertyName != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
            Object property = ref.getProperty(editObj, profile.getPropertyName());
            if (property != null)
                return property;
        }
        return null;
    }

    /**
     * Sets <code>children</code> of the specified EditingProfile <code>profile</code>.
     * The children profiles are created according to the properties of the specified
     * Object <code>objectToEdit</code> which in turn may be the <code>editObj</code>
     * or its property to be edited.
     * 
     * @param objectToEdit
     *            Object to be edited by this editor
     * @param profile
     *            EditingProfile the <code>children</code> of which are created
     */
    protected void createDefaultChildrenProfiles(Object objectToEdit,
                                                 EditingProfile profile) {
        if (objectToEdit != null && profile != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(objectToEdit
                    .getClass());
            String[] propertyNames = ref.getPropertyNames();

            //Modif 040803
            if (propertyNames == null || propertyNames.length == 0)
                return;

            //end Modif

            //			Vector propertyProfiles = new Vector();
            //			for (int i = 0; i < propertyNames.length; i++) {
            //				Object property = ref.getProperty(objectToEdit, propertyNames[i]);
            //				if (property != null) {
            //					if (property instanceof Editable)
            //						propertyProfiles.add(((Editable) property).getEditingProfile());
            //					else
            //						// propertyProfiles.add(
            //						// EditorFactory.createDefaultProfile(
            //						// createDefaultProfile2
            //						// property,
            //						// propertyNames[i]));
            //						propertyProfiles.add(EditorFactory.createDefaultProfile(property,
            // propertyNames[i]));
            //				} else {
            //					if (propertyNames[i] != null) {
            //						EditingProfile profileForNullProperty = new
            // EditingProfile(propertyNames[i]);
            //						propertyProfiles.add(profileForNullProperty);
            //					}
            //				}
            //			}
            //			//if readOnly in profile is true: set readOnly of children profiles
            //			// true as well
            //			if (profile != null && profile.isReadOnly()) {
            //				for (Iterator iter = propertyProfiles.iterator(); iter.hasNext();) {
            //					EditingProfile propertyProfile = (EditingProfile) iter.next();
            //					propertyProfile.setReadOnly(true);
            //				}
            //			}
            //			EditingProfile[] childrenProfiles = new
            // EditingProfile[propertyProfiles.size()];
            //			int j = 0;
            //			for (Iterator iter = propertyProfiles.iterator(); iter.hasNext();) {
            //				EditingProfile childProfile = (EditingProfile) iter.next();
            //				childrenProfiles[j++] = childProfile;
            //			}
            EditingProfile[] childrenProfiles = EditorFactory
                    .createChildrenProfiles(objectToEdit, propertyNames);
            if (profile != null && profile.isReadOnly()) {
                for (int i = 0; i < childrenProfiles.length; i++) {
                    childrenProfiles[i].setReadOnly(true);
                }
            }
            profile.setChildren(childrenProfiles);
        }
    }

    /**
     * Apply by propagating <code>applyChanges()</code> to all children.
     * If this editor is the outmost editor, it sends a DataChangeEvent to the
     * DataChangeManager.
     * The flags <code>dataChanged<code> and <code>dirty</code> are reset to false.
     */
    public void applyChanges() {
        if (profile.isReadOnly())
            return;
        Collection<Object> editedData = getEditedData();
        //apply changed values
        if (isValueToRemove())
            resetNullProperty();
        if (children != null)
            for (int i = 0; i < children.length; i++) {
                children[i].applyChanges();
            }
        setPropertyIfNull(editObj);   
        //send DataChangeEvent
        if (rootDisplay == this)
            sendDataChangeEvent(editedData);
        //reset flags
        dataChanged = false;
        dirty = false;
    }
    
    /**
	 * If the <code>propertyValue</code> is null, but the <code>propertyName</code>
	 * is not, the corresponding property of the <code>editObj</code> is set null.
	 * If <code>propertyName</code> is null, the children editors are reset if
	 * necessary.
	 */
	protected void resetNullProperty() {
		if (propertyName != null && propertyValue == null) {
			ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
			if (ref.hasPropertyName(propertyName))
				ref.setProperty(editObj, propertyName, null);
		}
		else {
		    if (children!=null && children.length>0){
		        for (int i = 0; i < children.length; i++) {
                    if (children[i] instanceof AbstractEditor){
                        ((AbstractEditor)children[i]).resetNullProperty();
                    }
                }
		    }
		}
	}

    /**
     * Return false if at least one children is invalid.
     */
    public boolean inputIsValid() {
        if (children == null)
            return true;
        boolean allChildrenValid = true;
        for (int i = 0; i < children.length; i++) {
            if (allChildrenValid)
                allChildrenValid = children[i].inputIsValid();
        }
        return allChildrenValid;
    }

    // ------------------ methods for handling null-propertyValues ------------
    /**
     * Returns an Object meant to be a new propertyValue.
     * 
     * @return Object meant to be a new propertyValue
     */
    //	protected Object createNewPropertyValue(String editorTitle) {
    //		Object newValue = null;
    //		//Modif051103 (for testing)
    //		Object object = null;
    //		// if (editObjCopy != null)
    //		// object = editObjCopy;
    //		// else
    //		object = editObj;
    //		//get class
    //		Class propertyClass = null;
    //		ReflectionAccess ref =
    //			// ReflectionAccess.accessForClass(editObjCopy.getClass());
    //	ReflectionAccess.accessForClass(object.getClass());
    //		if (ref.hasPropertyName(propertyName)) {
    //			propertyClass = ref.getPropertyType(propertyName);
    //		}
    //		//create instance
    //		if (propertyClass != null)
    //			newValue = createNewComplexValue(propertyClass, editorTitle);
    //		return newValue;
    //	}
    /**
     * If the property of <code>editObj</code> corresponding to
     * <code>propertyName</code> is null, it is set to <code>propertyValue</code> (if
     * <code>propertyValue<code> is
     * not null).
     * <br>
     * This might be the case if <code>editObj</code> originally had a null-property
     * and the user has chosen the option to create a new property value.
     * <br>
     * Checking the property if null is done inside this method in order to construct
     * the ReflectionAccess only once.
     */
    protected void setPropertyIfNull(Object obj) {
        if (propertyValue != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(obj.getClass());
            if (ref.hasPropertyName(propertyName)
                && ref.getProperty(obj, propertyName) == null)
                ref.setProperty(obj, propertyName, propertyValue);
        }
    }

    // -------------- methods for interaction with DataChangeManager
    // ------------------
    /**
     * Invokes method <code>interestShrinked</code> in class
     * <code>DataChangeManager</code> to remove this editor and its <code>editObj</code>
     * or <code>propertyValue</code> from the DataChangeManager's <code>table</code>
     * of views and changed data.
     */
    public void destroy() {
        //		Vector objsOfEditor = new Vector();
        //		if (propertyValue != null) {
        //			objsOfEditor.add(propertyValue);
        //		} else if (editObj != null)
        //			objsOfEditor.add(editObj);
        //		if (!objsOfEditor.isEmpty())
        //			DataChangeManager.getInstance().interestShrinked(this, objsOfEditor);
        super.destroy();
        if (children != null)
            for (int i = 0; i < children.length; i++) {
                children[i].destroy();
            }
    }

    /**
     * Returns true if boolean <code>dataChanged</code> of this editor or of one of its
     * children editors is true. Overwrites method <code>isDataChanged()</code> in class
     * AbstractEditor to check also the children's <code>dataChanged</code>.
     */
    public boolean externalChanges() {
        if (dataChanged)
            return true;
        if (!dataChanged) {
            if (children != null && children.length > 0) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i].externalChanges())
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Overwrites method <code>isRemoteEventSource(Object)</code> of class
     * <code>AbstractEditor</code>. Returns false if the specified Objetc
     * <code>eventSource</code> is this editor, its <code>rootEditor</code> or one of
     * its children editors, true otherwise.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#isRemoteEventSource(java.lang.Object)
     */
    /*
     * protected boolean isRemoteEventSource(Object eventSource) { boolean childOfSource =
     * false; if (eventSource instanceof AbstractComplexEditor) { childOfSource =
     * ((AbstractComplexEditor) eventSource).isChildEditor(this); } return
     * super.isRemoteEventSource(eventSource) && childOfSource; }
     */
    //	------------- helper
    // ----------------------------------------------------------
    /**
     * Returns true if the specified editor is contained in <code>children</code>,
     * false otherwise.
     * 
     * @param editor
     *            AbstractEditor to be checked if contained in <code>children</code>
     * @return true, if the specified editor is contained in <code>children</code>
     */
    public boolean isChildEditor(AbstractEditor editor) {
        if (children != null && children.length > 0)
            for (int i = 0; i < children.length; i++) {
                if (children[i].equals(editor))
                    return true;
            }
        return false;
    }

    /**
     * Update the editor from the editObj by recursing through all the children.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#updateEditor()
     */
    //	public void updateDisplay() {
    //		dataChanged = false;
    //		// ergänzen
    //	}
    /**
     * init
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Editor)
     */
    public void init(Object editObject, EditingProfile profile, Display rootEditor) {
        this.editObj = editObject;
        this.profile = profile;
        if (profile != null)
            this.propertyName = profile.getPropertyName();
        this.rootDisplay = rootEditor;
        setPropertyValue();
        if (children == null || !(children.length > 0))
            createChildrenEditorsII(this.rootDisplay); //TODO Methode aus EditorFactory?
        registerAtChangeManager();
        createGUI();
    }

    /**
     * Returns a Vector containing the objects edited by this editor and its
     * <code>children</code>. Only changed objects are included.
     * 
     * @return Vector with objects edited by this editor and its children editors
     * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
     */
    public Collection<Object> getEditedData() {
        ArrayList<Object> changedData = new ArrayList<Object>();
        if (children != null && children.length > 0) {
            for (int i = 0; i < children.length; i++) {
                if (children[i].getEditedData() != null ) {
                    for (Object changedObject : children[i].getEditedData()) {
                        //if primitive has been replaced, add object having the primitive
                        // property
                        if (ObjectCopy.isPrimitiveType(changedObject)
                            || changedObject instanceof String) {
                            if (propertyName != null)
                                changedObject = propertyValue;
                            else
                                changedObject = editObj;
                        }
                        if (!changedData.contains(changedObject))
                            changedData.add(changedObject);
                    }
                }
            }
            //Modif040610
            if (isValueCreated() || isValueToRemove()) //11.06.04: isValueRemoved() ergänzt
                if (!changedData.contains(editObj))
                    changedData.add(editObj);
            //end Modif
        }
        // nicht alle übergeordneten Objekte einfügen,
        // da diese von sendDataChangeEvent an den DataChangeManager
        // geschickt werden. Testen und ggf. andere Methoden anpassen.
        //		if (!changedData.isEmpty()) {
        //			if (propertyValue != null) {
        //				changedData.add(propertyValue);
        //			} else
        //				changedData.add(editObj);
        //		}
        if (changedData.isEmpty() && dirty){
            changedData.add(editObj);
        }
        return changedData;
    }

    /**
     * Prints the object edited by this editor. This method is for testing only.
     */
    protected void printEditObjForTesting() {
        System.out.println("Class of editObj: " + editObj.getClass().toString());
        System.out.println("rootEditor :\t" + editObj.toString());
        if (children != null && children.length > 0) {
            for (int i = 0; i < children.length; i++) {
                String labelOfEditor = children[i].getEditingProfile().getLabel();
                if (labelOfEditor != null) {
                    System.out.println("Editor with label '" + labelOfEditor + "':\t"
                                       + editObj.toString());
                } else
                    System.out.println("(no label)\t" + editObj.toString());
            }
        }
    }

    /**
     * Overwrites method <code>isValueRemoved()</code> of class
     * <code>AbstractEditor</code> in order to check the property values of the children
     * editors as well. Returns true if the <code>propertyValue</code> of this editor or
     * one of its children is null, but the corresponding property of the
     * <code>editObj</code> is not.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#isValueRemoved()
     */
    protected boolean isValueToRemove() {
        String nullPropertyName = null;
        if (propertyName != null && propertyValue == null)
            nullPropertyName = propertyName;
        if (propertyName == null) {
            if (children != null && children.length > 0) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i] instanceof AbstractEditor
                        && ((AbstractEditor) children[i]).isValueToRemove())
                        nullPropertyName = children[i].getEditingProfile()
                                .getPropertyName();
                }
            }
        }
        if (nullPropertyName != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
            if (ref.hasPropertyName(nullPropertyName))
                if (ref.getProperty(editObj, nullPropertyName) != null) {
                    return true;
                }
        }
        return false;
    }

    public void setOpaque(boolean opaque) {
        Collection comps = CompoundComponentManager.getInnerComponentsRecursive(this);
        for (Iterator iter = comps.iterator(); iter.hasNext();) {
            Object element = (Object) iter.next();
            if (element instanceof JComponent)
                ((JComponent) element).setOpaque(opaque);
        }
    }
    
    
//    public void setDirty(boolean dirty){
//        this.dirty = dirty;
//        if (children!=null && children.length>0){
//            for (int i = 0; i < children.length; i++) {
//                children[i].setDirty(dirty);//TODO führt zu Endlosschleife
//            }
//        }
//    }
    
}