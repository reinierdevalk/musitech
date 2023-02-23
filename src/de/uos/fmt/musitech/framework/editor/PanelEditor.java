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
 * Created on 27.05.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * A Complex editor displaying its children simply on a panel with a titeld border.
 * <br>
 * <br>Since the layout might be complicated for displaying both instance of 
 * simple editor (or a <code>ButtonEditor</code>) and instances of complex 
 * editors a <code>PanelEditor</code> displays only either of them. 
 * <br>To be able to still display all editors all complex editors are "wrapped"
 * in a <code>ButtonEditor</code> if a least one child is a simple editor. 
 * 
 * @author Tobias Widdra
 */
public class PanelEditor extends AbstractComplexEditor {

	
	/**
	 * Creates the graphical user interface.
	 * <br>
	 * <br> Complex editors are displayed vertically without any further label, 
	 * simple editors and <code>ButtonEditor</code>s in rows with their 
	 * <code>labeltext</code> on the left hand side.
	 * <br> Both is done by invoking the private methods <code>createGUI()</code>
	 * appropriatly.
	 * <br>
	 * <br>If at least one child is a simple editor all complex editors are
	 * "wrapped" in a button editor. 
	 */
	@Override
	protected void createGUI() {

		// if propertyValue is null (but propertyName is not) a create-button is shown
		if (propertyName != null && propertyValue == null) {
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder(propertyName));
			displayCreateButton(panel);
			add(panel);
			panel.addFocusListener(focusListener);
			return;
		}

		// check children: count # simple/popup and # complex editors.
		int numberSimple = 0; // incl. PopUp!!!
		int numberComplex = 0;
		for (int i = 0; i < children.length; i++) {
			if ((children[i] instanceof AbstractSimpleEditor)
				|| (children[i] instanceof PopUpEditor))
				numberSimple++;
			else
				numberComplex++;
		}

		// if all are "simple" create GUI for simple
		if (numberComplex == 0) {
			createGUIForSimple();
			return;
		}

		// if all are complex create GUI for complex
		if (numberSimple == 0) {
			createGUIForComplex();
			return;
		}

		// if mix: wrapp complex editors in a PopUpEditor and create "simple"
		for (int i = 0; i < children.length; i++) {
			if ((children[i] instanceof AbstractComplexEditor)
				&& (!(children[i] instanceof PopUpEditor))) {
				children[i] =
					EditorFactory.createPopUpWrapper(
						children[i]);
			}
		}
		createGUIForSimple();
	}

	/**
	 * Creates the GUI assuming that all children are complex editors.
	 * <br>
	 * <br> If any child is a simple editor it is ignored and simply not displayed 
	 *
	 */
	private void createGUIForComplex() {

		//create textfields/editing components and add them to this panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		for (int i = 0; i < children.length; i++) {
			// add editors (ignore simple editors!)
			if (children[i] instanceof AbstractComplexEditor) {
				panel.add((Component)children[i]);
			}
		}
		setBorder(BorderFactory.createTitledBorder(getLabeltext()));
		setLayout(new BorderLayout());
		
//		if (isOutmostEditor()) {
		if (rootDisplay == this) {
			JScrollPane scrollPane = new JScrollPane(panel);
			add(scrollPane);
			scrollPane.addFocusListener(focusListener);
		} else {
			add(panel);
			panel.addFocusListener(focusListener);
		}

	}

	/**
	 * Creates the GUI assuming that all children are either simple editors
	 * or instances of <code>ButtonEditor</code>.
	 * <br>
	 * <br> All other editors are ignored and simply not displayed.
	 *
	 */
	private void createGUIForSimple() {

		// panel for labels
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(0, 1));
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));

		// panel for textfields
		JPanel textfieldPanel = new JPanel();
		textfieldPanel.setLayout(new GridLayout(0, 1));
		textfieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));

		// add children
		for (int i = 0; i < children.length; i++) {
			// create labels (ignore complex editors!)
			if ((children[i] instanceof AbstractSimpleEditor)
				|| (children[i] instanceof PopUpEditor)) {
//				JLabel label = new JLabel(children[i].getEditingProfile().getLabel());
				JLabel label = new JLabel(((AbstractEditor)children[i]).getLabeltext());
				labelPanel.add(label);
				textfieldPanel.add((Component)children[i]);
			}
		}

		// add label- and textfield-panels to this panel
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(getLabeltext()));
		//Modif160903
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(labelPanel, BorderLayout.WEST);
		panel.add(textfieldPanel, BorderLayout.CENTER);
//		if (isOutmostEditor()) {
		if (rootDisplay == this) {
			JScrollPane scrollpane = new JScrollPane(panel);
			add(scrollpane);
			scrollpane.addFocusListener(focusListener);
		} else {
			add(panel);
			panel.addFocusListener(focusListener);
		}
		
	}

	/**
	 * Creates a JButton with text "Create..." and adds it to the specified JPanel.
	 * @param createPanel JPanel to which the newly created JButton is to be added
	 */
	private void displayCreateButton(JPanel createPanel) {
		JButton createButton = new JButton("Create...");
		
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
//				propertyValue = createNewPropertyValue("New propertyValue");
				propertyValue = createNewValue("New propertyValue");
				//Modif040610
				if (propertyValue!=null)
					setValueCreated(true);
				//end Modif
				if (children==null || children.length<=0)
					createChildrenEditors(propertyValue, getRootDisplay()); 
//				updateEditor();
				updateGUI();
			}
		});
		createPanel.add(createButton);
		createButton.addFocusListener(focusListener);
	}

	/**
	 * Updates the editor.
	 * This method is invoked after a new propertyValue has been created.
	 */
	@Override
	public void updateDisplay() {	
//		if (editObjCopy!=null)
////			editObjCopy = ObjectCopy.copyObject(editObj);
//			ObjectCopy.copyPublicProperties(editObj, editObjCopy);
		removeAll();
		//create child for property if newly created
//		if (children==null || getPropertyNumber()>children.length){
			createChildrenEditorsII(this.getRootDisplay());
//		}else	
		if (children != null && children.length>0){
			for (int i=0; i<children.length; i++){
				children[i].updateDisplay();
			}
		}
		createGUI();
		revalidate();
		repaint();
		dataChanged = false;
		setValueCreated(false);
		dirty = false;
	}	
	
	/**
	 * Updates the GUI.
	 */
	protected void updateGUI(){
		removeAll();
		createGUI();
		revalidate();
	}
	
	/** 
	 * Returns true if this editor or one of its children has the focus.
	 * @return boolean true if this editor or one of its children is focused
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Editor#isFocused()
	 */
	@Override
	public boolean isFocused(){
		Component[] components = getComponents();
		for (int i=0; i<components.length; i++){
			if (components[i].isFocusOwner())
				return true;
		}
		if (children!=null && children.length>0){
			for (int j=0; j<children.length; j++){
				if (children[j].isFocused())
					return true;
			}
		}
		return false;
	}
	
//	private int getPropertyNumber(){
//		if (editObj instanceof Editable){
//			EditingProfile prof = ((Editable)editObj).getEditingProfile();
//			if (prof.getChildren()!=null)
//				return prof.getChildren().length;
//		}
//		ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
//		String[] propertyNames = ref.getPropertyNames();
//		int n=0;
//		for (int i = 0; i < propertyNames.length; i++) {
//			if (ref.getProperty(editObj, propertyNames[i])!=null)
//				n++;
//		}
//		return n;
//	}

}
