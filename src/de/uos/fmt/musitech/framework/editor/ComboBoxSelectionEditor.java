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
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

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
 * This editor displays a choice of values for an editObject in a JComboBox. In
 * order to provide possible values, an editObject must implement the interface
 * #IParameterValueProvider.
 * 
 * @author Jens
 */
public class ComboBoxSelectionEditor extends AbstractSimpleEditor {

	/**
	 * JComboBox offering possible input values from IParameterValueProvider
	 */
	private JComboBox comboBox;

	@Override
	public boolean applyChangesToPropertyValue() {
		propertyValue = comboBox.getSelectedItem();
		return true;
	}

	/**
	 * Creates <code>booleanBox</code> and adds it to this editor.
	 */
	protected void addComboBox() {
		IParameterValueProvider<Object> value = getValueToEdit();
		if (profile.isReadOnly() && value != null) {
			removeAll();
			JTextField field = new JTextField(value.toString());
			field.setEditable(false);
			add(field);
			return;
		}

		comboBox = new JComboBox(getValues());
		if (value != null)
			comboBox.setSelectedItem(value);
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				applyChangesToPropertyValue();
				applyChanges();
			}
		});
		add(comboBox);
	}

	/**
	 * Returns the object being edited. Might be the <code>editObj</code>
	 * itself or the <code>propertyValue</code>.
	 * 
	 * @return Boolean object to edit
	 */
	@SuppressWarnings("unchecked")
	private IParameterValueProvider<Object> getValueToEdit() {
		if (editObj instanceof IParameterValueProvider)
			return (IParameterValueProvider<Object>) editObj;
		if (propertyValue instanceof IParameterValueProvider)
			return (IParameterValueProvider<Object>) propertyValue;
		return null;
	}

	@Override
	protected void createGUI() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// if (propertyName != null && propertyValue == null)
		// displayCreateButton();
		// else
		addComboBox();
		setFocusable(true);
		addFocusListener(focusListener);
	}

	/**
	 * Displays a JButton with label "Create" which leads to creating a new
	 * <code>propertyValue</code>. This method is invoked in case
	 * <code>propertyValue</code> is null while <code>propertyName</code> is
	 * not null.
	 */
	private void displayCreateButton() {
		JButton createButton = new JButton("Create");
		// Modif190504
		if (profile.isReadOnly())
			createButton.setEnabled(false);
		else
			createButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					createNewBooleanValue();
				}
			});
		// createButton.addFocusListener(focusListener);
		add(createButton);
	}

	/**
	 * Sets <code>propertyValue</code> to a newly created Boolean value. The
	 * value is asked for in a JDialog, displaying a JComboBox with entries
	 * "true" and "false".
	 */
	protected void createNewBooleanValue() {
		JComboBox newValueBox = new JComboBox(getValues());
		if (showNewValueBox("New propertyValue", newValueBox)) {
			propertyValue = new Boolean((String) newValueBox.getSelectedItem());
			updateGUI();
		}
	}

	private Object[] getValues() {
		Collection<Object> values = getValueToEdit().getParameterValues(
			this.propertyName);
		if( values!=null )
			return new ArrayList<Object>(values).toArray();
		else {
			System.err.println("[POSSIBLE ERROR] No values for '"+getValueToEdit()+"'");
			return new Object[0];
		}
	}

	/**
	 * Updates the graphical user interface.
	 */
	public void updateGUI() {
		removeAll();
		createGUI();
		revalidate();
	}

	/**
	 * Shows the specified JComboBox in a modal JDialog titled with the
	 * specified String.
	 * 
	 * @param title String to be displayed on top of the JDialog
	 * @param box JComboBox to be displayed in the center of the JDialog
	 * @return Boolean returns true if the user closes the JDialog by "OK",
	 *         false if the "Cancel" button has been activated
	 */
	private boolean showNewValueBox(String title, JComboBox box) {
		// provide "task" for user
		String text = "Please choose the new value.";
		// provide buttons OK and Cancel
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		// provide JDialog and add listener to the buttons
		JDialog dialog = new JDialog(new JFrame(), title, true);
		dialog.getContentPane().setLayout(new BorderLayout());
		DialogActionListener listener = new DialogActionListener(dialog);
		okButton.addActionListener(listener);
		cancelButton.addActionListener(listener);
		// add components to dialog adn show dialog
		dialog.getContentPane().add(new JLabel(text), BorderLayout.NORTH);
		dialog.getContentPane().add(box, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		return listener.confirmed;
	}
}
