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
package de.uos.fmt.musitech.utility;/**
 * A class to implement a selector "Pianoroll/Velocity" for the
 * class MainConfigDialog
 *
 * @author Alexander Luedeke
 * @version 0.113
 */
public class DisplayTextSelector extends javax.swing.JComboBox {

	protected Object lastSelectedItem;
	protected String textPropertyName;

	/**
	 * Constructor.
	 */
	public DisplayTextSelector() {
		super();
		reBuild();
	}

	/**
	 * Builds the selector.
	 */
	public void reBuild() {
		if (getItemCount() > 0)
			removeAllItems();
			addItem("Pianoroll");
			addItem("Velocity");
	}

	/**
	 * Fires a PropertyChange event.
	 */
	protected void selectedItemChanged() {
		Object selectedItem = getSelectedItem();
		if (selectedItem != null && selectedItem.equals(lastSelectedItem))
			return;
		try {
			fireVetoableChange("selectedBoolean", lastSelectedItem, selectedItem);
			if (textPropertyName != null)
				fireVetoableChange(textPropertyName, lastSelectedItem, selectedItem);
		} catch (java.beans.PropertyVetoException pve) {
			setSelectedItem(lastSelectedItem);
			javax.swing.JOptionPane.showMessageDialog(
				getTopLevelAncestor(),
				pve.getMessage(),
				"Illegal value!",
				javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		super.selectedItemChanged();
		lastSelectedItem = selectedItem;
	}

	/**
	 * Returns true if the selected item equals "Velocity".
	 */
	public boolean getSelectedBoolean() {

		if((getSelectedItem().toString()).equals("Velocity"))
			return true;
		else
			return false;
	}

	/**
	 * Returns the textPropertyName.
	 */
	public java.lang.String getTextPropertyName() {
		return textPropertyName;
	}

	/**
	 * Sets the selection.
	 */
	public void setSelectedBoolean(boolean i) {
		try {
			java.lang.String item;
			if (i)
				item = "Velocity";
			else
				item = "Pianoroll";
			setSelectedItem(item);
		} finally {
		}
	}

	/**
	 * Sets the selection.
	 */
	public void setSelectedItem(boolean i) {
		try {
			java.lang.String item;
			if (i)
				item = "Velocity";
			else
				item = "Pianoroll";
			setSelectedItem(item);
		} finally {
		}
	}

	/**
	 * Sets the textPropertyName.
	 */
	public void setTextPropertyName(java.lang.String newTextPropertyName) {
		textPropertyName = newTextPropertyName;
	}
}