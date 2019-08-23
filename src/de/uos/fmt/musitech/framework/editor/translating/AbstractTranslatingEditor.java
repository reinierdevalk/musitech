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
 * Created on 22.10.2004
 *
 */
package de.uos.fmt.musitech.framework.editor.translating;

import java.util.ArrayList;
import java.util.Collection;

import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.AbstractEditor;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * Abstract class implementing the interface TranslatingEditor.
 * 
 * @author Kerstin Neubarth
 *
 */
abstract public class AbstractTranslatingEditor extends AbstractEditor {
    
    /**
     * Object coding the <code>editObj</code> which is used for input.
     */
    protected Object inputObj;
    
    /**
     * Records the occurrence of changes.
     */
    protected boolean dirty;
    
    /** 
     * Initializes the TranslatingEditor by setting its parameters <code>inputObj</code>,
     * <code>profile</code> (as well as <code>propertyName</code> and <code>)propertyValue</code>
     * if the <code>propertyName</code> in the <code<profile</code> is not null) and
     * <code>rootDisplay</code>. Method <code>determineLocalObjs()</code> is called
     * to set the locally used fields (e.g. a working copy) if there are any used.
     * The TranslatingEditor registers at the DataChangeManager and builds its GUI.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object, de.uos.fmt.musitech.framework.editor.EditingProfile, de.uos.fmt.musitech.framework.editor.Display)
     */
    public void init(Object inputObject, EditingProfile profile, Display rootDisplay) {
        //set parameters of this TranslatingEditor
        if (inputObject==null){	//TODO oder auf default (z.B. "") setzen?
            return;
        }
        this.inputObj = inputObject;
        this.profile = profile;
        if (profile!=null){
            if (profile.getPropertyName()!=null){
                this.propertyName = profile.getPropertyName();
                setPropertyValue();
            }
        }
        this.rootDisplay = rootDisplay;
        determineLocalObjs();
        //register at DataChangeManager
        Collection dataToEdit = new ArrayList();
        if (propertyValue!=null){
            dataToEdit.add(propertyValue);
        } else {
            dataToEdit.add(inputObj);	//TODO inputObj or editObj or both?
        }
        DataChangeManager.getInstance().interestExpandElements(this, dataToEdit);
        //create GUI
        createGUI();
    }

    /**
	 * This method is invoked by the <code>init</code> method in case the propertyName is set. 
	 * It sets the value of <code>propertyValue</code>.
	 * <br> 
	 * <br>By default (i. e. by this implementation) <code>propertyValue</code>
	 * is set to the value of the property <code>propertyName</code> in <code>initObj</code>
	 * via reflection access. Notice that this mechanism provides a <i>reference</i> rather than
	 * a copy of the object!
	 * <br> If you want some different behavior (e. g. do not set propertyValue at all
	 * for you do not use it) overwrite this method. 
	 */
	protected void setPropertyValue() {
		ReflectionAccess ref = ReflectionAccess.accessForClass(inputObj.getClass());
		if ((propertyName != null) && (ref.hasPropertyName(propertyName)))
			propertyValue = ref.getProperty(inputObj, propertyName);
	}
	
	/**
	 * If the subclass uses local fields for operating on the data (e.g. a working copy
	 * of the <code>inputObj</code>), these should be set in this method. In this case,
	 * you must overwrite this method. Here, the method has an empty body.
	 * The method is called in the <code>init()</code>-method. 
	 */
	protected void determineLocalObjs(){}
	
	/** 
     * Returns a Collection containing the object that has been changed.
     * If <code>dirty</code> is true, the Collection will contain either the 
     * <code>propertyValue</code> or the <code>editObj</code>.
     * Otherwise, an empty Collection is returned.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
     */
    public Collection getEditedData() {
        Collection editedData = new ArrayList();
        if (dirty){
            if (propertyValue!=null)	//TODO inputObj oder editObj hinzufügen?
                editedData.add(propertyValue);	
            else
                editedData.add(inputObj);
        }
        return editedData;
    }
    
    /** 
     * Applies occured changes to the <code>inputObj</code>, updates the
     * <code>editObj</code> accordingly, notifies the DataChangeManager 
     * if this TranslatingEditor is the <code>root</code> and 
     * resets <code>dirty</code> and <code>dataChanged</code> to false.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#applyChanges()
     */
    public void applyChanges() {
        if (dirty){
            //write changes to inputObj
            applyChangesToInputObj();
            //update editObj to changes in inputObj
            applyChangesToEditObj();
            //notify DataChangeManager if outmost editor
            if (this == getRootDisplay()){
                sendDataChangeEvent();
            }
            //reset dirty and dataChanged
            dirty = false;
            dataChanged = false;
        }
    }
    
    /**
     * Applies changes entered in the GUI to the <code>inputObj</code>.
     */
    abstract protected void applyChangesToInputObj();
    
    /**
     * Creates or updates the <code>editObj</code> according to the changes applied 
     * to the <code>inputObj</code>.
     */
    abstract protected void applyChangesToEditObj();
    
    /** 
     * Returns true if this TranslatingEditor is the focus owner.
     * If you want to check certain parts of your GUI whether being focussed, 
     * please overwrite this method. 
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    public boolean isFocused() {
        return isFocusOwner();
    }
}
