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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * Editor for editing properties of type "de.uos.fmt.musitech.utility.Rational".
 * 
 * @author Tobias Widdra
 */
public class RationalEditor extends SimpleTextfieldEditor {

    /**
     * Textfield for the denominator (this textfield inherited from
     * AbstractSimpleEditor) is used for the nominator.
     */
    JTextField textfield2;
    
    /**
     * Working copy of the Rational which is to be edited. Is either a copy of
     * the <code>editObj</code> or of the <code>propertyValue</code>. 
     */
    Rational workingRational = null;

    /**
     * Applies actual text within the textfield to <code>propertyValue</code>.
     * <br>
     * Shows an error dialog if the traverasl from String to Rational was
     * unsuccessfull.
     * 
     * @return true if the input is accepted
     */
    /*
     * protected boolean applyChangesToPropertyValue() { try { int numer =
     * Integer.valueOf(textfield.getText()).intValue(); int denom =
     * Integer.valueOf(textfield2.getText()).intValue(); //Modif111103 if
     * (propertyName != null) propertyValue = new Rational(numer, denom);
     * //"orig" else if (editObjCopy != null) editObjCopy = new Rational(numer,
     * denom); //Modif111103 return true; } catch (NumberFormatException e) { if
     * ((textfield.getText().equals("") && textfield2.getText().equals("")) &&
     * isEmptyDisplayOptionSet()) return true; if
     * ((textfield.getText().equals("") && textfield2.getText().equals(""))) {
     * if (!isEmptyDisplayOptionSet()) if (isNullProperty()) {
     * setEmptyDisplayOption(true); propertyValue = null; return true; } //TODO
     * gibt es Fälle, wo das nicht erwünscht ist? //return true; } } catch
     * (IllegalArgumentException e2) { // denom set to zero!
     * JOptionPane.showMessageDialog( null, "Denominator must not be zero!",
     * "Input Error", JOptionPane.ERROR_MESSAGE);
     * 
     * //Modif121103 if (propertyValue == null && !(editObj instanceof
     * Rational)) { textfield2.setText(""); textfield2.requestFocus(); } else
     * resetTextfields(); return false; } }
     */

    /**
     * Creates a GUI with two textfields, one for the nominator and one for the
     * denominator. In between a slash is displayed. 
     * If <code>propertyName</code> is not null, but <code>propertyValue</code> is,
     * a "Create" button is offered.
     */
    protected void createGUI() {
        //display a button with text "create" if the value of the property is
        // null
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (propertyName != null && propertyValue == null) {
            displayCreateButton();
            return;
        }
        //else display the Rational in two textfields with a slash between them
        initTextfields();
        fillTextfields();
        setDirty(false);
    }
    
    /**
     * Creates a JPanel which contains the two textfields separated by a slash
     * and adds the panel to this RationalEditor.
     */
    private void addTextfields(){
        //set the textfields into a JPanel which is added to the editor
        JPanel textfieldPanel = new JPanel();
        textfieldPanel
                .setLayout(new BoxLayout(textfieldPanel, BoxLayout.X_AXIS));
        textfieldPanel.add(textfield);
        textfieldPanel.add(new JLabel(" / "));
        textfieldPanel.add(textfield2);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(textfieldPanel);
    }
    
    /**
     * Initializes the <code>textfield</code> and <code>textfield2</code>, i.e.
     * creates the two JTextFields, adds to both a DocumentListener for registering
     * changes and FocusListeners for checking the input (when the focus is lost)
     * and for evaluating <code>dataChanged</code> (when the focus is gained), and
     * adds the textfields to the GUI.
     * If the editor's EditingProfile specified a read only editor, the textfields
     * are disabled.
     */
    private void initTextfields(){
        //create textfields
        textfield = new JTextField();
        textfield2 = new JTextField();
        //disable textfields if readOnly mode
        if (profile.isReadOnly()) {
            textfield.setEditable(false);
            textfield2.setEditable(false);
        }
        textfield2.getDocument().addDocumentListener(new DocumentListener(){
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
        //add FocusListener for input check
        textfield.addFocusListener(new MyFocusListener() {
            public void focusGained(FocusEvent e) {}
            public void focusLost(FocusEvent e) {
                checkConditionsForTextfield();
            }
        });
        textfield2.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {}
            public void focusLost(FocusEvent e) {
                checkConditionsForTextfield2();
            }
        });
        // add focusListener (see class AbstractEditor) for interaction with
        // DataChangeManager
        textfield.addFocusListener(focusListener);
        textfield2.addFocusListener(focusListener);
        addTextfields();
    }

    /**
     * Overwrites method <code>init(Object, EditingProfile, Editor)</code> of
     * class <code>SimpleTextfieldEditor</code> in order to create
     * <code>editObjCopy</code> if the Rational to be edited is the
     * <code>editObj</code> itself instead of <code>propertyValue</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Editor)
     */
    public void init(Object editObject, EditingProfile profile,
            Display rootEditor) {
        this.editObj = editObject;
        this.profile = profile;
        this.rootDisplay = rootEditor;
        if (profile != null)
            this.propertyName = profile.getPropertyName();
        setPropertyValue(); //Modif171103
        //		if (propertyValue instanceof Rational)
        //			 ((Rational) propertyValue).reduce();
        determineWorkingRational();
        registerAtChangeManager();
        createGUI();
    }

    /**
     * Sets the <code>workingRational</code>. The <code>workingRational</code>
     * will be a copy of either the <code>editObj</code> or the
     * <code>propertyValue</code>.
     *
     */
    private void determineWorkingRational() {
        if (editObj instanceof Rational) {
            workingRational = (Rational) ObjectCopy.copyObject(editObj);
        } else {
            if (propertyValue != null && propertyValue instanceof Rational) {
                workingRational = (Rational) ObjectCopy
                        .copyObject(propertyValue);
            }
        }
    }

    /**
     * Overwrites method <code>applyChanges()</code> of class
     * <code>SimpleTextfieldEditor</code> in order to write
     * <code>workingRational</code> back to <code>editObj</code> resp. 
     * <code>propertyValue</code>. 
     * <br>
     * Resets <code>dataChanged</code> and <code>dirty</code> to false.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#applyChanges()
     */
    public void applyChanges() {
        if (profile.isReadOnly())
            return;
        //get edited data
        Collection editedData = getEditedData();
        //apply changed values
        if (editObj instanceof Rational) {
            ObjectCopy.copyPublicProperties(workingRational, editObj);
        } else {
            if (propertyValue != null && propertyValue instanceof Rational) {
                ObjectCopy.copyPublicProperties(workingRational, propertyValue);
            }
        }
        setPropertyIfNull(editObj);
        //send DataChangeEvent
        if (rootDisplay == this)
            sendDataChangeEvent(editedData);
        //reset flags
        dataChanged = false;
        dirty = false;
    }
    
    protected void setPropertyIfNull(Object obj) {
        if (propertyValue != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(obj.getClass());
            if (ref.hasPropertyName(propertyName)
                && ref.getProperty(obj, propertyName) == null)
                ref.setProperty(obj, propertyName, propertyValue);
        }
    }

    /**
     * Returns true if the property of <code>workingRational</code> corresponding to
     * <code>propertyName</code> is null.
     * 
     * @return
     */
    private boolean isNullProperty() {
        if (workingRational != null) {
            ReflectionAccess ref = ReflectionAccess
                    .accessForClass(workingRational.getClass());
            if (propertyName != null && ref.hasPropertyName(propertyName)
                    && ref.getProperty(workingRational, propertyName) == null)
                return true;
        }
        return false;
    }
    
    /**
     * Sets the text in <code>textfield</code> to the numerator of the
     * <code>workingRational</code> and the text in <code>textfield2</code>
     * to its denominator. Returns true if the textfields are set, false otherwise.
     */
    private boolean fillTextfields(){
        if (workingRational==null){
            determineWorkingRational();
        }
        if (workingRational != null) {
            textfield.setText(workingRational.getNumer() + "");
            textfield2.setText(workingRational.getDenom() + "");
            return true;
        }
        return false;
    }

    /**
     * Resets the JTextfields <code>textfield</code> and
     * <code>textfield2</code> to their former text.
     * 
     * @return true if resetting the JTextfields has been successful, false
     *         otherwise
     */
/*    private boolean resetTextfields() {
        //		if (propertyValue != null) {
        //			textfield.setText(((Rational) propertyValue).getNumer() + "");
        //			textfield2.setText(((Rational) propertyValue).getDenom() + "");
        //			return true;
        //		} else if (editObjCopy != null && editObjCopy instanceof Rational) {
        //			textfield.setText(((Rational) editObjCopy).getNumer() + "");
        //			textfield2.setText(((Rational) editObjCopy).getDenom() + "");
        //			return true;
        //		}
        //		return false;

//        if (workingRational != null) {
//            textfield.setText(workingRational.getNumer() + "");
//            textfield2.setText(workingRational.getDenom() + "");
//            return true;
//        }
//        return false;
        
        return fillTextfields();

    }
*/

    /**
     * Returns true if both JTextfields do not contain any text and empty input
     * is allowed either because <code>emptyDisplayOption</code> has been set
     * true or because the property this editor has been created for is null
     * (this might be the case for the properties <code>metricTime</code> and
     * <code>duration</code> of a ScoreNote).
     * 
     * @return true if the texts of <code>textfield</code> and
     *         <code>textfield2</code> are empty Strings and
     *         <code>isEmptyDisplayOptionSet()</code> returns true
     */
    private boolean allowedEmptyInput() {
        if (!isEmptyDisplayOptionSet()) {
            if (isNullProperty())
                setEmptyDisplayOption(true);
        }
        if (textfield.getText().equals("") && textfield2.getText().equals("")
                && isEmptyDisplayOptionSet())
            return true;
        return false;
    }

    /**
     * Applies actual text within the textfield to <code>propertyValue</code>.
     * <br>
     * Shows an error dialog if the traverasl from String to Rational was
     * unsuccessfull.
     * 
     * @return true if the input is accepted
     */
    public boolean applyChangesToPropertyValue() {
        //		try {
        //			int num = Integer.valueOf(textfield.getText()).intValue();
        //			int denom = Integer.valueOf(textfield2.getText()).intValue();
        //			if (propertyName != null)
        //				propertyValue = new Rational(num, denom);
        //			else if (editObjCopy != null && (editObjCopy instanceof Rational))
        //				editObjCopy = new Rational(num, denom);
        //			return true;
        //		} catch (NumberFormatException e) {
        //			if (allowedEmptyInput())
        //				return true;
        //			showErrorMessage();
        //		} catch (IllegalArgumentException eZero) {
        //			JOptionPane.showMessageDialog(
        //				null,
        //				"The denominator must not be zero.",
        //				"Input Error",
        //				JOptionPane.ERROR_MESSAGE);
        //		}
        //		resetTextfields();
        //		return false;

        if (getInputValue() != null && getInputValue() instanceof Rational
                && inputIsValid()) {
            if (!ObjectCopy.equalValues(getInputValue(), workingRational)) {
                dirty = true;
                workingRational = (Rational) getInputValue();
                return true;
            }
        }
        return false;
    }

    /**
     * Tries to set the focus to <code>textfield2</code>, but allows to move
     * the focus elsewhere if both JTextfields are empty or contain a valid
     * input. (Otherwise it would not be possible to step back to the foregoing
     * JTextfield when moving between JComponents via tabstop.) <br>
     * This method is called by the FocusListener added to
     * <code>textfield</code> when the focus is lost.
     */
    void checkConditionsForTextfield() {
        boolean bothValid = true;
        try {
            Integer.valueOf(textfield.getText());
            if (Integer.valueOf(textfield2.getText()).intValue() == 0)
                bothValid = false;
        } catch (NumberFormatException e) {
            bothValid = allowedEmptyInput();
        }
        if (!bothValid)
            textfield2.requestFocus();
        else
            applyChangesToPropertyValue();
    }

    /**
     * Invokes method <code>applyChangesToPropertyValue()</code>. This method
     * is called by the FocusListener added to <code>textfield2</code> when
     * the focus is lost. If applying the JTextfields' input to
     * <code>propertyValue</code> fails, <code>textfield</code> asks for the
     * focus again.
     */
    void checkConditionsForTextfield2() {
        if (!applyChangesToPropertyValue()) {
            textfield.requestFocus();
        }
    }

    /**
     * Reads the text from the textfields and creates a Rational from it.
     * Returns this Rational as the input value.
     * If no Rational could be created from the input, null is returned.
     * 
     * @see de.uos.fmt.musitech.framework.editor.SimpleTextfieldEditor#getInputValue()
     */
    protected Object getInputValue() {
        if (textfield == null)
            return null;
        Rational inputValue = null;
        try {
            int num = Integer.valueOf(textfield.getText()).intValue();
            int denom = Integer.valueOf(textfield2.getText()).intValue();
            inputValue = new Rational(num, denom);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        } catch (IllegalArgumentException eZero) {
            eZero.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "The denominator must not be zero.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return inputValue;
    }

    /**
     * Overwrites method <code>resetInput()</code> of
     * <code>SimpleTextfieldEditor</code> in order to reset both JTextFields.
     * As in <code>SimpleTextfieldEditor</code> the user is notified about
     * invalid input.
     */
    protected void resetInput() {
        //notify user
        showErrorMessage();
        //reset textfields
//        resetTextfields();
        fillTextfields();
    }

    /**
     * Overwrites method <code>updateDisplay()</code> of class
     * <code>SimpleTextfieldEditor</code> in order to update both JTextFields.
     * If the textfields already exist, their text is set to the new values. If
     * a new Rational has been created, the "Create"-button is removed and the
     * JTextFields are created and added to the editor.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
     */
    public void updateDisplay() {
        dataChanged = false;
        setValueCreated(false);
        if (textfield != null && textfield2 != null) {
            //update the workingRational
            determineWorkingRational();
            //update the text in the textfields
            fillTextfields();
        } else {
            //show Create-button or create the textfields
            removeAll();
            createGUI();
            revalidate();
            repaint();
        }
        dirty = false;
    }

    /**
     * Compares the <code>workingRational</code> with the <code>editObj</code> or
     * <code>propertyValue</code>. If their values differ, true is returned. If they
     * have equal values, false is returned.
     *  
     * @see de.uos.fmt.musitech.framework.editor.AbstractSimpleEditor#hasDataChanged()
     */
    protected boolean hasDataChanged() {
        //	    return dirty;
        if (workingRational != null) {
            if (editObj instanceof Rational) {
                return !ObjectCopy.equalValues(editObj, workingRational);
            } else {
                if (propertyValue != null && propertyValue instanceof Rational) {
                    return !ObjectCopy.equalValues(propertyValue, workingRational);
                }
            }
        }
        return dirty;
    }
    
    /**
     * Updates the GUI, i.e. sets the text in the textfields to the curretn values.
     * If the textfields are null, method <code>createGUI()</code> is called in order
     * to show either a Create-button or to create and add the textfields.
     *  
     * @see de.uos.fmt.musitech.framework.editor.SimpleTextfieldEditor#updateGUI()
     */
    protected void updateGUI() {
        if (textfield==null || textfield2==null){
            removeAll();
            createGUI();
            revalidate();
        } else {
            fillTextfields();
        }
        setDirty(false);
    }
    

}