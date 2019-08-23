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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.ldap.HasControls;

import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * A <code>SimpleEditor</code> is an editor which has no children editors. These are
 * used for primitive or a very simple object like a String or a
 * <code>de.uos.fmt.musitech.utility.Rational</code> (which has two properties only),
 * but also for Editors editing a complex object as a whole. <br>
 * The implementor is responsible for creating a local copy 'copy' of the editObject or
 * propertyValue.
 * 
 * @author Tobias Widdra & Kerstin Neubarth
 */
public abstract class AbstractSimpleEditor extends AbstractEditor {

    /**
     * init
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Editor)
     */
    public void init(Object editObject, EditingProfile _profile, Display rootEditor) {
        this.editObj = editObject;
        this.profile = _profile;
        if (profile != null)
            this.propertyName = profile.getPropertyName();
        setPropertyValue();
        if (rootEditor != null)
            this.rootDisplay = rootEditor;
        registerAtChangeManager();
        createGUI();
    }

    // ------------- methods for setting up the editor and its GUI ----------

    /**
     * Updates the editor. <br>
     * Here, the editor is updated by regenerating the GUI. Subclasses should overwrite
     * this method to update only what is necessary, e.g. set the text in a JTextfield to
     * the new value.
     */
    public void updateDisplay() {
        //        dataChanged = false;
        //        valueCreated = false;
        //		if (editObjCopy != null)
        //			// editObjCopy = ObjectCopy.copyObject(editObj);
        //			ObjectCopy.copyPublicProperties(editObj, editObjCopy);
        setPropertyValue();
        removeAll();
        createGUI();
        revalidate();
        dataChanged = false;
        setValueCreated(false);
        dirty = false;
    }

    /**
     * Returns true if this editor's <code>textfield</code> has the focus.
     * 
     * @return
     */
    public boolean isFocused() {

        Component[] components = getComponents();
        if (this.isFocusOwner())
            return true;

        for (int i = 0; i < components.length; i++) {
            if (components[i].isFocusOwner())
                return true;
        }
        return false;
    }

    // -------------------- getter and setter -------------------

    public Object getPropertyValue() {
        return propertyValue;
    }

    // --------------- methods for editor functionality ---------------

    /**
     * This method is to be overwritten by the subclasses which create a simple editor for
     * a specific type. <br>
     * It has to apply the user input to <code>propertyValue</code>, not to the
     * property of the original <code>editObj</code>. (This is done in
     * <code>applyChanges()</code>.)<br>
     * <br>
     * The method is supposed to return true if the input is within the acceptable range
     * of values and false otherwise. // TODO who uses this.
     * 
     * @return true if the input is valid, false otherwise
     */
    //08/12/03: changed to public to use it in MetaDataEditor
    abstract public boolean applyChangesToPropertyValue();

    /**
     * Sets the property <code>propertyName</code> in <code>editObj</code> to the
     * value <code>propertyValue</code> via ReflectionAccess (i. e. applies the changes
     * to the original object). If this editor is the outmost editor, it sends a
     * DataChangeEvent. The flags <code>dataChanged</code> and <code>dirty</code> are
     * reset to false.
     */
    public void applyChanges() {
        if (profile.isReadOnly())
            return;
        //check for changed values
        if (hasDataChanged() && !dirty)
            dirty = true;
        //apply changed values
        if (propertyValue != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
            if (ref.hasPropertyName(propertyName))
                ref.setProperty(editObj, propertyName, propertyValue);
        }
        //send DataChangedEvent
        if (rootDisplay == this)
            sendDataChangeEvent();
        //reset flags
        dataChanged = false;
        dirty = false;
    }

    //------------------- methods for interaction with DataChangeManager
    // -------------

    /**
     * Returns a Collection containing the objects changed by this editor.
     * 
     * @return Collection with objects changed by this editor
     * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
     */
    public Collection getEditedData() {
        ArrayList changedData = new ArrayList();
        // if (hasDataChanged()) //ergänzt 19/11/03
        if (dirty) //ersetzt 01/06/04
            if (propertyValue != null) {
                changedData.add(propertyValue);
            } else
                changedData.add(editObj);
        //Modif040610
        if (isValueCreated())
            if (!changedData.contains(editObj))
                changedData.add(editObj);
        return changedData;
    }

    /**
     * Returns true if the value(s) the editor is currently holding in propertyValue
     * differ from the corresponding one(s) of <code>editObj</code>.<br>
     * <br>
     * This method must be overwritten in subclasses operating an a working copy of
     * <code>editObj</code> (in case <code>editObj</code> is to be edited itself and
     * might be changed) to compare the current values of the copy with those of
     * <code>editObj</code>.
     * 
     * @return true if the current <code>propertyValue</code> differs from the value of
     *         the corresponding property of <code>editObj</code>
     */
    protected boolean hasDataChanged() {
        if (propertyName != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
            if (ref.hasPropertyName(propertyName)) {
                Object propertyOfEditObj = ref.getProperty(editObj, propertyName);
                if (propertyOfEditObj != null)
                    return (!propertyOfEditObj.equals(propertyValue));
                else
                    return !(propertyValue == null);
            }
        }
        return false;
    }

    protected void printEditObjForTesting() {
        System.out.println("Class of editObj: " + editObj.getClass().toString());
        System.out.println("Values in editObj: " + editObj.toString());
    }

}