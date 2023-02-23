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
 * Created on 26.10.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Editor for editing StringBuffer objects.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class StringBufferEditor extends AbstractSimpleEditor {

    /**
     * Local variable for operating on the StringBuffer.
     */
    StringBuffer stringBuffer = new StringBuffer();

    /**
     * JTextArea used for displaying the StringBuffer.
     */
    JTextArea textArea = new JTextArea();

    /**
     * Initializes this StringBufferEditor by setting the <code>editObj</code>,
     * <code>profile</code> and <code>rootDisplay</code> according to the
     * specified arguments. If the <code>propertyName</code> in the
     * <code>_profile</code> is not null, the <code>propertyName</code> and
     * <code>propertyValue</code> of this StringBufferEditor are set. The
     * local <code>stringBuffer</code> is set to either the
     * <code>propertyValue</code> or <code>editObj</code>. The
     * StringBufferEditor is registered at the DataChangeManager and the GUI is
     * built.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Editor)
     */
    @Override
	public void init(Object editObject, EditingProfile _profile,
            Display rootDisplay) {
        this.editObj = editObject;
        this.profile = _profile;
        if (profile != null)
            this.propertyName = profile.getPropertyName();
        setPropertyValue();
        if (rootDisplay != null)
            this.rootDisplay = rootDisplay;
        determineLocalObj();
        registerAtChangeManager();
        createGUI();
    }

    /**
     * Sets the <code>stringBuffer</code> to either the
     * <code>propertyValue</code> or the <code>editObj</code>.
     */
    private void determineLocalObj() {
        if (stringBuffer.length()>0){
            stringBuffer.delete(0, stringBuffer.length());
        }
        if (propertyValue != null && propertyValue instanceof StringBuffer) {
            stringBuffer.append((StringBuffer) propertyValue);
        } else {
            if (editObj instanceof StringBuffer) {
                stringBuffer.append((StringBuffer) editObj);
            }
        }
    }

    /**
     * Applies the changes to the <code>editObj</code>. If
     * <code>porpertyValue</code> is not null, the changes are applied to the
     * property of the <code>editObj</code>, therefore changing the
     * <code>editObj</code>. Send a DataChangeEvent to the DataChangeManager
     * if this StringBufferEditor is the outmost editor and resets
     * <code>dataChanged</code> and <code>dirty</code> to false.
     */
    @Override
	public void applyChanges() {
        if (profile.isReadOnly())
            return;
        //apply changes
        updateBufferFromGUI();
        rewriteToEditObj();
        //send DataChangeEvent
        if (rootDisplay == this)
            sendDataChangeEvent();
        //reset dataChanged and dirty
        dataChanged = false;
        dirty = false;
    }

    /**
     * Writes the content of <code>stringBuffer</code> to the
     * <code>propertyValue</code> (which is a property fo the
     * <code>editObj</code>) or to the <code>editObj</code>.
     */
    private void rewriteToEditObj() {
        if (propertyValue != null && propertyValue instanceof StringBuffer) {
            if (((StringBuffer) propertyValue).length() > 0) {
                ((StringBuffer) propertyValue).delete(0,
                        ((StringBuffer) propertyValue).length());
            }
            ((StringBuffer) propertyValue).append(stringBuffer);
        } else {
            if (editObj instanceof StringBuffer) {
                if (((StringBuffer) editObj).length() > 0) {
                    ((StringBuffer) editObj).delete(0, ((StringBuffer) editObj)
                            .length());
                }
                ((StringBuffer) editObj).append(stringBuffer);
                System.out.println("In StringBufferEditor.rewriteToEditObj():");
                System.out.println("Content of editObj: "+editObj.toString());
            }
        }
    }

    /**
     * Reads the text from the <code>textArea</code> and appends it to the
     * <code>stringBuffer</code> after the <code>stringBuffer</code> has
     * been cleared. Returns true, because there are no constraints on the text
     * in the <code>textArea</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractSimpleEditor#applyChangesToPropertyValue()
     */
    @Override
	public boolean applyChangesToPropertyValue() {
        if (stringBuffer.length() > 0) {
            stringBuffer.delete(0, stringBuffer.length());
        }
        stringBuffer.append(textArea.getText());
        //		if (propertyName!=null && textArea != null) {
        //			propertyValue = textArea.getText();
        //			return true;
        //		}
        //		return false;
        return true;
    }

    /**
     * Prepares the the layout of this StringBufferEditor and the
     * <code>textArea</code>.
     */
    private void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        if (profile != null && profile.isReadOnly()) {
            textArea.setEnabled(false);
            textArea.setDisabledTextColor(Color.BLACK);
        }
    }

    /**
     * Creates the GUI. Sets the text in the <code>textArea</code>. Adds a
     * FocusListener and a DocumentChangeListener to the <code>textArea</code>
     * for checking the input and applying changes to the
     * <code>propertyValue</code> if the focus is lost and for setting
     * <code>dirty</code> true, if the document changes. The
     * <code>textArea</code> is added to this StringBufferEditor.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    @Override
	protected void createGUI() {
        initLayout();
        //add DocumentListener which sets the dirty flag when data is changed
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void changedUpdate(DocumentEvent e) {
                setDirty(true);
            }

            @Override
			public void insertUpdate(DocumentEvent e) {
                setDirty(true);
            }

            @Override
			public void removeUpdate(DocumentEvent e) {
                setDirty(true);
            }
        });
        textArea.addFocusListener(new FocusAdapter() {
            @Override
			public void focusLost(FocusEvent e) {
                if (!applyChangesToPropertyValue())
                    textArea.requestFocusInWindow();
            }
            @Override
			public void focusGained(FocusEvent e){
                focusReceived();
            }
        });
        //add textArea to this StringBufferEditor
        add(textArea);
        updateGUI();
    }

    /**
     * Updates the GUI by setting the text in the <code>textArea</code> to the
     * current content of the <code>StringBuffer</code>.
     */
    private void updateGUI() {
        if (stringBuffer == null) {
            determineLocalObj();
        }
        if (stringBuffer != null) {
            textArea.setText(stringBuffer.toString());
        }
        dirty = false;
    }

    /**
     * Returns <code>dirty</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractSimpleEditor#hasDataChanged()
     */
    @Override
	protected boolean hasDataChanged() {
        return dirty;
    }
    
    @Override
	public void updateDisplay(){
        determineLocalObj();
        updateGUI();
        dataChanged = false;
    }
    
    private void updateBufferFromGUI(){
        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append(textArea.getText());
        //for testing:
//        if (DebugState.DEBUG){
            System.out.println("In StringBufferEditor.updateBufferFromGUI():");
            System.out.println("Content of buffer: "+stringBuffer.toString());
//        }
    }
    
    @Override
	public boolean isFocused(){
        return textArea.isFocusOwner();
    }
    
    @Override
	protected boolean conflictingData(){
        if (!stringBuffer.toString().equals(textArea.getText())){
            return true;
        }
        if (propertyValue!=null){
            if (!stringBuffer.equals(propertyValue)){
                return true;
            }
        }
        if (editObj instanceof StringBuffer){
            if (!stringBuffer.equals(editObj)){
                return true;
            }
        }
        return false;
    }
    

}