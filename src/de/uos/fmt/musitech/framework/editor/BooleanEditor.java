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
 * Created on 21.01.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


/**
 * This class provides an editor for editing objects of type <code>Boolean</code>.
 * 
 * @author Kerstin Neubarth
 *
 */
public class BooleanEditor extends AbstractSimpleEditor {

	/**
	 * JComboBox offering possible input values ("true" and "false") 
	 */
	JComboBox booleanBox;

	/** 
	 * Applies the user's input (resp. the item selected in <code>booelanBox</code>)
	 * to <code>propertyValue</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.AbstractSimpleEditor#applyChangesToPropertyValue()
	 */
	public boolean applyChangesToPropertyValue() {
		propertyValue = new Boolean((String) booleanBox.getSelectedItem());
		return true;
	}

	/** 
	 * Creates the graphical user interface.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
	 */
	protected void createGUI() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		if (propertyName != null && propertyValue == null)
			displayCreateButton();
		else
			addComboBox();
		setFocusable(true);
		addFocusListener(focusListener);
	}

	/**
	 * Displays a JButton with label "Create" which leads to creating a new 
	 * <code>propertyValue</code>. This method is invoked in case <code>propertyValue</code>
	 * is null while <code>propertyName</code> is not null.
	 */
	private void displayCreateButton() {
		JButton createButton = new JButton("Create");
		//Modif190504
		if (profile.isReadOnly())
			createButton.setEnabled(false);
		else
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewBooleanValue();
			}
		});
		//createButton.addFocusListener(focusListener);
		add(createButton);
	}

	/**
	 * Sets <code>propertyValue</code> to a newly created Boolean value. The value is 
	 * asked for in a JDialog, displaying a JComboBox with entries "true" and "false".
	 */
	protected void createNewBooleanValue() {
		String[] values = { "true", "false" };
		JComboBox newValueBox = new JComboBox(values);
		if (showNewValueBox("New propertyValue", newValueBox)) {
			propertyValue = new Boolean((String) newValueBox.getSelectedItem());
			updateGUI();
		}
	}

	/**
	 * Creates <code>booleanBox</code> and adds it to this editor.
	 */
	protected void addComboBox() {
		if (profile.isReadOnly()){
			Boolean value = getBooleanValueToEdit();
			if (value!=null){
				removeAll();
				JTextField field = new JTextField(value.toString());
				field.setEditable(false);
				add(field);
				return;
			}
		}
		String[] booleanValues = { "true", "false" };
		booleanBox = new JComboBox(booleanValues);
		Boolean value = getBooleanValueToEdit();
		if (value != null)
			booleanBox.setSelectedItem(value.toString());
		booleanBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyChangesToPropertyValue();
			}
		});
//		if (profile.isReadOnly())
//			booleanBox.setEnabled(false);
		//		booleanBox.addFocusListener(focusListener);
		add(booleanBox);
	}

	/**
	 * Returns the Boolean object being edited. Might be the <code>editObj</code> itself
	 * or the <code>propertyValue</code>.
	 * 
	 * @return Boolean object to edit
	 */
	private Boolean getBooleanValueToEdit() {
		if (editObj instanceof Boolean)
			return (Boolean) editObj;
		if (propertyValue instanceof Boolean)
			return (Boolean) propertyValue;
		return null;
	}

	/**
	 * Shows the specified JComboBox in a modal JDialog titled with the specified String.
	 * 
	 * @param title String to be displayed on top of the JDialog
	 * @param box JComboBox to be displayed in the center of the JDialog
	 * @return Boolean returns true if the user closes the JDialog by "OK", false if the "Cancel" button has been activated
	 */
	private boolean showNewValueBox(String title, JComboBox box) {
		//provide "task" for user
		String text = "Please choose the new Boolean value.";
		//provide buttons OK and Cancel
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		//provide JDialog and add listener to the buttons
		JDialog dialog = new JDialog(new JFrame(), title, true);
		dialog.getContentPane().setLayout(new BorderLayout());
		DialogActionListener listener = new DialogActionListener(dialog);
		okButton.addActionListener(listener);
		cancelButton.addActionListener(listener);
		//add components to dialog adn show dialog
		dialog.getContentPane().add(new JLabel(text), BorderLayout.NORTH);
		dialog.getContentPane().add(box, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.show();
		return listener.confirmed;
	}

	/**
	 * Updates the graphical user interface.
	 */
	public void updateGUI() {
		removeAll();
		createGUI();
		revalidate();
	}

}
