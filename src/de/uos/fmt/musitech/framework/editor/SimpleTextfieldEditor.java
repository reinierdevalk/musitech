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
 * Created on 05.11.2003
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * Abstract class for those simple editors using a JTextfield to display the
 * value of <code>editObj</code> or <code>propertyValue</code>.
 * 
 * @author Kerstin Neubarth
 *  
 */
abstract public class SimpleTextfieldEditor extends AbstractSimpleEditor {

    /**
     * JTextfield used to display the value of <code>editObj</code> or its
     * property to be edited.
     */
    JTextField textfield;

    /**
     * Flag for remembering if the textfield might be displayed without a text.
     * This flag is checked when reading the textfield's content in method
     * <code>applyChangesToPropertyValue()</code>.
     */
    private boolean emptyDisplayOption = false;

    /**
     * Getter for the textfield.
     * 
     * @return
     */
    public JTextField getTextfield() {
        return textfield;
    }

    /**
     * Setter for <code>emptyDisplayOption</code>.
     * 
     * @param emptyDisplayOption
     */
    public void setEmptyDisplayOption(boolean emptyDisplayOption) {
        this.emptyDisplayOption = emptyDisplayOption;
    }

    /**
     * Getter for <code>emptyDisplayOption</code>.
     * 
     * @return
     */
    public boolean isEmptyDisplayOptionSet() {
        return emptyDisplayOption;
    }

    /**
     * Shows an error dialog displaying the wrong input value for a specific
     * property. <br>
     * The idea of this central error message handling was to have one point in
     * the code where to change the text displayed.
     *  
     */
    void showErrorMessage() {
        JOptionPane.showMessageDialog(null, "\"" + textfield.getText()
                + "\" is not a valid input for " + getLabeltext(),
                "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Calls <code>applyChangesToEditObj()</code> if focus is lost. If false
     * is returned (i. e. the input was incorrect) a request to get the focus
     * back is send.
     * 
     * @author Tobias Widdra
     */
    public class MyFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent arg0) {
            //Modif040604
            if (textfield.getText().equals("") && isEmptyDisplayOptionSet()) {
                if (propertyName != null && propertyValue == null)
                    //does not allow deleting an existing value
                    return;
            }
            //end Modif
            if (!applyChangesToPropertyValue())
                textfield.requestFocusInWindow();
        }
    }

    /**
     * Creates the components for the graphical user interface and adds a
     * FocusListener to the textfield <code>textfield</code>.<br>
     * <br>
     * If you overwrite this method make sure you insert a line like <br>
     * <br>
     * <code>	textfield.addFocusListener(new MyFocusListener());
     * <br>
     * <br> where <code>FocusListener</code> is an instance of the inner class
     * <code>MyFocusListener</code> of this class.
     */
    protected void createGUI() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (propertyName != null && propertyValue == null) {
            displayCreateButton();
            return;
        }
        initTextField();
        //set text in textfield
        updateGUI();
    }
    
    private void initTextField(){
        textfield = new JTextField();
        // focus listener triggering the input format check (when focus
        // lost):
        textfield.addFocusListener(new MyFocusListener());
        //focus listener triggering the reaction to DataChangeEvents (when
        // focus gained):
        textfield.addFocusListener(focusListener);
        //add DocumentListener which sets the dirty flag when data is changed
        textfield.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                setDirty(true);
            }
            public void insertUpdate(DocumentEvent e) {
                setDirty(true);
            }
            public void removeUpdate(DocumentEvent e) {
                setDirty(true);
            }
        }); 
        //add textfield to GUI
        removeAll();
        add(textfield);
        revalidate();
    }

    /**
     * Displays the editObj by setting the text of <code>textfield</code> to
     * the String representation of <code>editObj</code>. The textfield is
     * disabled.
     */
    private void displayEditObj() {
        if (editObj.getClass().isPrimitive()
                || ObjectCopy.isElementaryClass(editObj.getClass())
                || editObj instanceof String) {
            if (profile.isReadOnly()) {
                textfield.setEnabled(false);
                textfield.setDisabledTextColor(Color.BLACK);
            }
            textfield.setText(editObj.toString());
            add(textfield);
            //			}
        }
    }

    /**
     * Creates a JButton displaying text "Create..." and adds this JButton to
     * the editor with size and location given by the arguments
     * <code>size</code> and <code>location</code>. This method is invoked
     * if <code>propertyValue</code> is null (but <code>propertyName</code>
     * is not) to offer creating a new propertyValue.
     * 
     * @param location
     *            Point indicating where the JButton <code>createButton</code>
     *            is to be added
     * @param size
     *            Dimension indicating to which size the JButton
     *            <code>createButton</code> is to be set
     */
    private void displayCreateButton(Point location, Dimension size) {
        JButton createButton = new JButton("Create...");
        createButton.setLocation(location);
        createButton.setPreferredSize(size);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                propertyValue = createNewValue("New propertyValue");
                updateGUI();
            }
        });
        add(createButton);
    }

    /**
     * Creates a JButton with label "Create" and adds this button to the editor.
     * When the button is activated a new <code>propertyValue</code> is
     * created. (This method is called, when <code>propertyValue</code> is
     * null, but <code>propertyName</code> is not.)
     */
    protected void displayCreateButton() {
        JButton createButton = new JButton("Create...");
        //Modif190504
        if (profile.isReadOnly())
            createButton.setEnabled(false);
        else
            createButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    //				propertyValue = createNewPropertyValue("New
                    // propertyValue");
                    propertyValue = createNewValue("New propertyValue");
                    //Modif040610
                    if (propertyValue != null)
                        setValueCreated(true);
                    //end Modif
                    updateGUI();
                }
            });
        createButton.addFocusListener(focusListener);
        add(createButton);
    }
    
//    protected void setPropertyIfNull(Object obj) {
//        if (propertyValue != null) {
//            ReflectionAccess ref = ReflectionAccess.accessForClass(obj.getClass());
//            if (ref.hasPropertyName(propertyName)
//                && ref.getProperty(obj, propertyName) == null)
//                ref.setProperty(obj, propertyName, propertyValue);
//        }
//    }

    /**
     * Updates the GUI. If the <code>textfield</code> is not null, its text is set
     * to the current <code>propertyValue</code>. Else, method <code>createGUI()</code>
     * is called to show either the "Create"-button or a newly created JTextField.
     * The flag <code>dirty</code> is reset to false.
     */
    protected void updateGUI() {
        if (textfield == null){
//            initTextField();
            removeAll();
            createGUI();
            revalidate();
        }
        if (propertyValue != null) {
            textfield.setText(propertyValue.toString());
            if (profile.isReadOnly())
                textfield.setEditable(false);
        } else if (propertyName == null && editObj != null)
            displayEditObj();
        setDirty(false);

    }

    /**
     * Checks if the JTextField <code>textfield</code> displays the value(s)
     * currently held by <code>editObj</code> resp. <code>propertyValue</code>.
     * Returns true, if the text in <code>textfield</code> represents an
     * obsolete state of the object edited.
     * 
     * @return true if the edited object (<code>propertyValue</code> or
     *         <code>editObj</code> has changed in relation to the value
     *         currently displayed in <code>textfield</code>
     */
    public boolean hasContentChanged() {
        String content = null;
        if (propertyName == null)
            content = editObj.toString();
        else if (propertyValue != null)
            content = propertyValue.toString();
        if (textfield.getText().equals(content)){
            return false;
        }
        else
            return true;
    }

    /**
     * Overwrites method <code>updateEditor()</code> in class
     * <code>AbstractSimpleEditor</code>. The text of the
     * <code>textfield</code> is set to the new <code>propertyValue</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#updateEditor()
     */
    public void updateDisplay() {
        dataChanged = false;
        setValueCreated(false);
        setPropertyValue();
        if (propertyValue != null) {
            if (textfield == null) {
//                removeAll(); //remove "Create"-button
//                textfield = new JTextField();
//                add(textfield);
                initTextField();
            }
            textfield.setText(propertyValue.toString());
        }
        dirty = false;
    }

    /**
     * Notifies the user that the input is invalid and resets the textfield to
     * the former input (which corresponds to the current
     * <code>propertyValue</code>).
     */
    protected void resetInput() {
        //notify user
        showErrorMessage();
        //reset textfield
        textfield.setText(propertyValue.toString());
    }

    /**
     * Returns true unless the <code>editObj</code> is a MObject and does not
     * accept the input as a possible property value (see method
     * <code>isPossibleValue</code> in class MObject).
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#inputIsValid()
     */
    public boolean inputIsValid() {
        //		if (getInputValue()==null)
        //			return false; //auch Fälle, wo null-input möglich?
    	//System.out.println("in inputIsvalid()");
        if (editObj instanceof MObject && propertyName != null
                && getInputValue() != null)
            return ((MObject) editObj).isValidValue(propertyName, getInputValue());
        return true;
    }

    /**
     * Returns a representation of the input which is corresponding to the
     * object type the editor is appropriate for. If no such object could be
     * created from the input, null is returned. <br>
     * E.g., the IntEditor returns an Integer or null, the CharEditor returns a
     * Character or null, etc.
     * 
     * @return Object representing the input according to the editortype
     */
    abstract protected Object getInputValue();

    /**
     * Returns true if the input could be applied to the
     * <code>propertyValue()</code>. Otherwise notifies the user about
     * invalid input, resets the text in the <code>textfield</code> to the
     * former input and returns false. <br>
     * Input is not applied to the <code>propertyValue</code>, if no input
     * value of an appropriate type could be created from the input (see method
     * <code>getInputValue()</code>) or if the created value is not valid
     * (see method <code>inputIsValid()</code>).
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractSimpleEditor#applyChangesToPropertyValue()
     */
    public boolean applyChangesToPropertyValue() {
    	//Author: Amir Obertinca - checks is the <code>editObj</code> is primitive, and instance of String
    	//if so, returns true, and gets the input value and passes into the editObj
        if (editObj.getClass().isPrimitive() || ObjectCopy.isElementaryClass(editObj.getClass())
                || editObj instanceof String) {
    			//System.out.println("STE The editObj = " + editObj);
    			propertyValue = (Object) getInputValue();
        		editObj = (Object) getInputValue();
         		//System.out.println("STE The editObj = " + editObj);
        		return true;
        }
        else {
        	if (getInputValue() != null && inputIsValid()) {
            	//System.out.println("STE Before The propertyValue is: "+propertyValue);
            	propertyValue = getInputValue();
            	//System.out.println("STE After The propertyValue is: "+propertyValue);
            	return true;
        	}
    	}

        resetInput();
        return false;
    }

}