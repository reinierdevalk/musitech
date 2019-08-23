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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataProfileManager;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.AbstractComplexEditor;
import de.uos.fmt.musitech.framework.editor.AbstractEditor;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.PopUpEditor;
import de.uos.fmt.musitech.framework.editor.SimpleTextfieldEditor;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * This class provides an editor for editing objects of type
 * <code>MetaDataItem</code>.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class MetaDataItemEditor extends AbstractEditor {

    private String language = null;

    /**
     * Editor used to edit the <code>value</code> of the MetaDataItem's
     * <code>metaValue</code>. The <code>valueEditor</code> is given the
     * <code>metaValue</code> as its <code>editObj</code> and "value" as its
     * <code>propertyName</code>. Its <code>propertyValue</code> is then
     * set to the <code>value</code> to be edited (in method
     * <code>init()</code> of the <code>valueEditor</code>).
     */
    //	Editor valueEditor;
    Display valueEditor;

    /**
     * MetaDataItem to be edited. Might be the <code>editObj</code> or the
     * <code>propertyValue</code> of the editor. In case, <code>editObj</code>
     * is of type MetaDataItem <code>mdItem</code> holds a copy of
     * <code>editObj</code>.
     */
    MetaDataItem mdItem = null;

    /*
     * public String getLabeltext(){ String labeltext = ""; if (profile!=null)
     * labeltext = profile.getLabel(); if (labeltext.equals("")){ MetaDataItem
     * mdItem = null; if (editObj instanceof MetaDataItem) mdItem =
     * (MetaDataItem)editObj; else if (propertyValue instanceof MetaDataItem)
     * mdItem = (MetaDataItem)propertyValue; labeltext = mdItem.getKey(); }
     * return labeltext; }
     */

    /**
     * Returns the MetaDataItem whose <code>value</code> is to be edited.
     * Might be the <code>editObj</code> or the <code>propertyValue</code>.
     * 
     * @return MetaDataItem to be edited
     */
    protected MetaDataItem getMetaDataItem() {
        MetaDataItem mdItem = null;
        if (editObj instanceof MetaDataItem)
            mdItem = (MetaDataItem) editObj;
        else if (propertyValue instanceof MetaDataItem)
            mdItem = (MetaDataItem) propertyValue;
        return mdItem;
    }

    /**
     * Creates the graphical user interface. <br>
     * The MetaDataItemEditor uses a subordinate editor, the
     * <code>valueEditor</code>, to edit the <code>value</code> of the
     * MetaDataItem's <code>metaValue</code>. The editortype of this
     * subordinate editor corresponds to the <code>mimeType</code> of the
     * MetaDataItem's <code>metaValue</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    protected void createGUI() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (mdItem == null)
            return;
        if (mdItem.getMetaDataValue() == null) {
            //TODO
        }
        //if value null: "create"
        //		if (mdItem.getMetaValue()!=null &&
        // mdItem.getMetaValue().getMetaValue() == null) {
        if (returnValue() == null) {
            displayCreateButton();
            return;
        }
        //add valueEditor
        if (valueEditor != null) {
            //			if (valueEditor instanceof AbstractComplexEditor
            //			    && !(valueEditor instanceof PopUpEditor))
            //				add(
            //					(Component) EditorFactory.createPopUpWrapper(
            //						(AbstractComplexEditor) valueEditor));
            //			else
            //				add((Component) valueEditor);
            if (valueEditor instanceof SimpleTextfieldEditor || valueEditor instanceof PopUpEditor) {
                add((JComponent) valueEditor);
            } else {
                try {
//                    add((JComponent) EditorFactory.createPopUpWrapper(valueEditor));
                    if (valueEditor instanceof Editor) {
                        add((JComponent) EditorFactory.createWrappingEditor("Preview", (Editor)valueEditor));
                    } else {
                        add((JComponent) EditorFactory.createWrappingDisplay("Preview", valueEditor));
                    }
                } catch (EditorConstructionException e) {

                    e.printStackTrace();
                }
            }
        }
        //gaining the focus is handled by the valueEditor which then delegates
        // to its rootEditor
    }

    /**
     * Updates the graphical user interface.
     */
    protected void updateGUI() {
        removeAll();
        if (valueEditor != null)
            valueEditor.updateDisplay();
        createGUI();
        revalidate();
    }

    /**
     * Sets <code>valueEditor</code> to an Editor created for the
     * <code>metaValue</code> of the specified MetaDataItem
     * <code>mdItem</code>.
     * 
     * @param mdItem MetaDataItem the <code>valueEditor</code> is to be
     *            created for
     */
    protected void createValueEditor(MetaDataItem mdItem) {
        //create EditingProfile for editing property "metaValue" of the
        // mdItem's MetaDataValue
        EditingProfile valueProfile = new EditingProfile("metaValue");
        valueProfile.setReadOnly(profile.isReadOnly());
        //TODO auffangen, wenn metaValue null
        if (mdItem.getMetaDataValue() == null)
            return;
        if (mdItem.getMetaDataValue().getMetaType() != null)
            valueProfile.setEditortype(mdItem.getMetaDataValue().getMetaType());
        valueProfile.setLabel(mdItem.getEditingProfile().getLabel());
        //create editor according to EditingProfile
        try {
            mdItem.getMetaDataValue().setActiveLanguage(language);
            //			valueEditor =
            //				EditorFactory.createEditor(
            //					mdItem.getMetaValue(),
            //					profile,
            //					rootDisplay);
            valueEditor = EditorFactory.createDisplay(mdItem.getMetaDataValue(), valueProfile, valueProfile.getDefaultEditortype(),
                                                      rootDisplay);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //if creation of editor failed, use a StringEditor as default
        if (valueEditor == null) {
            try {
                valueEditor = EditorFactory.createEditor(mdItem.getMetaDataValue().getMetaValue(), rootDisplay, "string");
            } catch (EditorConstructionException e) {
                e.printStackTrace();
            }
        }
        //an SimpleTextfieldEditor is to allow empty display
        if (valueEditor instanceof SimpleTextfieldEditor) {
            ((SimpleTextfieldEditor) valueEditor).setEmptyDisplayOption(true);
        }
        //create PopUpWrapper for complex valueEditor
        //		if (valueEditor instanceof AbstractComplexEditor){
        //		    if (!(valueEditor instanceof PopUpEditor))
        //		        valueEditor =
        // EditorFactory.createPopUpWrapper((AbstractComplexEditor)valueEditor);
        //		}
    }

    /**
     * Creates a JButton and adds it to this editor. The button is labeled by
     * String <code>BUTTON_TEXT</code> (which at the present state is
     * "Create") and is given an ActionListener for triggering the creation of a
     * new <code>value</code> of the MetaDataItem's <code>metaValue</code>.
     */
    protected void displayCreateButton() {
        String BUTTON_TEXT = "Create";
        final JButton createButton = new JButton(BUTTON_TEXT);
        createButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                createNewValue();
                createButton.setText("Create");
                updateGUI();
            }
        });
        add(createButton);
        //focus listener triggering the evaluation of flag dataChanged
        createButton.addFocusListener(focusListener);
    }

    /**
     * Creates a new <code>value</code> of the MetaDataItem's
     * <code>metaValue</code>. The class of the object to be created is
     * derived from the <code>mimeType</code> of the MetaDataItem's
     * <code>metaValue</code>.
     */
    protected void createNewValue() {
        Class classOfValue = getClassOfValue(mdItem.getMetaDataValue());
        if (ObjectCopy.isElementaryClass(classOfValue) || String.class.isAssignableFrom(classOfValue))
            mdItem.getMetaDataValue().setMetaValue(createNewPrimitiveValue(classOfValue));
        else
            mdItem.getMetaDataValue().setMetaValue(createNewComplexValue(classOfValue, "New value of MetaDataValue."));
        //create valueEditor for the new value (the valueEditor is used in
        // applyChanges())
        //		createValueEditor(mdItem);
        updateGUI();
    }

    /**
     * Returns the Class of the <code>value</code> of the specified MetaDataValue
     * <code>metaValue</code> as derived from the <code>mimeType</code> of
     * <code>metaValue</code>.<br>
     * (This method is called in the process of creating a new object in case
     * the <code>value</code> originally is null.)
     * 
     * @param metaDataValue MetaDataValue whose <code>mimeType</code> is used to
     *            derive the Class of its <code>value</code>
     * @return Class of the <code>value</code> of the specified MetaDataValue as
     *         derived from the <code>mimeType</code> of the specified
     *         MetaDataValue
     */
    protected Class getClassOfValue(MetaDataValue metaDataValue) {
        if (metaDataValue == null) {
            System.out
                    .println("In MetaDataItemEditor.getClassOfValue(MetaDataValue): Argument is null. No mimeType is given.");
            return null;
        }
        Class cla = null;
        String type = metaDataValue.getMetaType();
        String className = MetaDataProfileManager.getFullyQualifiedClassName(type);
        //TODO "flexibilisieren"
        if (className == null)
            return null;
        try {
            cla = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cla;
    }

    /**
     * Applies the user's input (given in the <code>valueEditor</code>) to
     * the <code>editObj</code>. Sends a DataChangeEvent to the
     * DataChangeManager in case this editor is the <code>rootDisplay</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#applyChanges()
     */
    public void applyChanges() {
        //		if (isOutmostEditor())
        if (rootDisplay == this)
            sendDataChangeEvent();

        if (valueEditor != null && valueEditor instanceof Editor) {
            ((Editor) valueEditor).applyChanges();
        }
    }

    /**
     * Updates the editor to a changed MetaDataItem.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#updateDisplay()
     */
    public void updateDisplay() {
        dataChanged = false;
        setPropertyValue();
        valueEditor.updateDisplay();
        updateGUI();
    }

    /**
     * Sets the editor's parameters as specified by the arguments.
     * <code>propertyValue</code> and <code>valueToEdit</code> are derived
     * from the parameters known. Furthermore, the editor registers at the
     * DataChangeManager and creates the GUI.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Editor)
     */
    public void init(Object editObject, EditingProfile profile, Display rootEditor) {
        this.editObj = editObject;
        this.profile = profile;
        if (profile != null)
            propertyName = profile.getPropertyName();
        setPropertyValue();
        //		setValueToEdit();
        this.rootDisplay = rootEditor;
        mdItem = getMetaDataItem();
        if (mdItem != null)
            createValueEditor(mdItem);
        registerAtChangeManager();
        createGUI();
    }

    /**
     * Returns true if the <code>value</code> of the MetaDataItem's
     * <code>metaValue</code> has been changed.
     * 
     * @return boolean true, if the MetaDataItem to be edited has been changed,
     *         false otherwise
     */
    /*
     * protected boolean hasDataChanged() { MetaDataValue metaValue = null; if
     * (propertyValue instanceof MetaDataItem) { metaValue = ((MetaDataItem)
     * propertyValue).getMetaValue(); } else { metaValue = ((MetaDataItem)
     * editObj).getMetaValue(); } if (metaValue != null &&
     * metaValue.getValue()!=null) { return !(ObjectCopy.equalValues(metaValue,
     * valueEditor.getEditObj())); } return false; }
     */

    /**
     * Returns true if the <code>valueEditor</code> has the focus. The
     * <code>valueEditor</code> might be a simple editor (e.g. a
     * SimpleTextfieldEditor) or a complex editor "hidden" behind an
     * "Edit"-button.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#isFocused()
     */
    public boolean isFocused() {
        return valueEditor.isFocused();
    }

    /**
     * Getter for <code>valueEditor</code>.
     * 
     * @return Editor <code>valueEditor</code>
     */
    //	public Editor getValueEditor(){
    //		return valueEditor;
    //	}
    public Display getValueEditor() {
        return valueEditor;
    }

    /**
     * Returns a Collection containing the objects changed by the user.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
     */
    public Collection getEditedData() {
        Vector data = new Vector();
        if (valueEditor != null && valueEditor instanceof Editor) {
            data.addAll(((Editor) valueEditor).getEditedData());
        }
        if (!data.isEmpty())
            data.add(getMetaDataItem());
        return data;
    }

    /**
     * Adds this editor and the MetaDataItem to be edited to the
     * <code>table</code> of the DataChangeManager.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#registerAtChangeManager()
     */
    public void registerAtChangeManager() {
        Vector objsToChange = new Vector();
        MetaDataItem item = getMetaDataItem();
        objsToChange.add(item);
        //		if (item.getMetaValue()!=null)
        //			objsToChange.add(item.getMetaValue());
        DataChangeManager.getInstance().interestExpandElements(this, objsToChange);
    }

    /**
     * Setter for <code>language</code>.
     * 
     * @param language String language in which the meta data is to be displayed
     */
    public void setLanguage(String language) {
        this.language = language;
        createValueEditor(mdItem);
        updateGUI();
    }

    /**
     * Getter for <code>language</code>.
     * 
     * @return String giving the language in which the meta data item is shown
     */
    public String getLanguage() {
        return language;
    }

    private Object returnValue() {
        if (mdItem.getMetaDataValue() != null) {
            //	        if (mdItem.getMetaValue().getMetaValue(language)!=null){
            //	            return mdItem.getMetaValue().getMetaValue(language);
            //	        }
            //	        return mdItem.getMetaValue().getMetaValue();
            mdItem.getMetaDataValue().setActiveLanguage(language);
            return mdItem.getMetaDataValue().getMetaValue();
        }
        return null;
    }

}