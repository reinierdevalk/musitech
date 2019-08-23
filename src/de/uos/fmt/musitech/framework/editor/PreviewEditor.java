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
 * Created on 24.09.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextField;

import de.uos.fmt.musitech.framework.selection.SelectionController;

/**
 * This class provides a PopUpEditor showing a preview of the editor's content.
 * In the basic PopUpEditor, only a "Edit"-button is shown besides the
 * labeltext. The PreviewEditor, in addition, also displayes a disabled
 * JTextfield. This JTextfield contains the labeltext and first value which will
 * be shown by the <code>editorToPopUp</code>.
 * 
 * @author Kerstin Neubarth
 */
public class PreviewEditor extends PopUpEditor {

	/**
	 * JTextField used for displaying the preview text.
	 */
	JTextField preview = new JTextField();

	/**
	 * Default width of the JTextField <code>preview</code>
	 */
	final int PREVIEWWIDTH = 120;

	/**
	 * Text displayed on the button when property is null.
	 */
	protected final String DELTE_BUTTON_TEXT = "Delete";

	private JButton deleteButton;

	/**
	 * Creates the GUI of the editor containing a disabled JTextfield for the
	 * preview and a JButton displaying "Edit" (or "Create" in case the
	 * <code>propertyValue</code> is null).
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.AbstractComplexEditor#createGUI()
	 */
	public void createGUI() {

		if (editButton == null)
			editButton = new JButton();
		editButton.setPreferredSize((new JButton(CREATE_BUTTON_TEXT))
				.getPreferredSize());
		//System.out.println(getObjectForEditor().getClass());
		
		// Author: Amir Obertinca: Edited 17/April/2008
		//checks if the <code>rootDisplay</code> is a collection, and if it is primitive (instance of String)
		//to replace the existing value with the value that the user wishes to replace with
		if (rootDisplay instanceof CollectionEditor
			&& (getObjectForEditor().getClass().isPrimitive() || getObjectForEditor() instanceof String)) {
			editButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CollectionEditor editor = (CollectionEditor) rootDisplay;
					Object oldObj = getObjectForEditor();
					List list = (List) editor.editCollCopy;
					int index = list.indexOf(oldObj);

					Object newObj = editor.createNewPrimitiveValue(oldObj
							.getClass());
					list.remove(oldObj);
					list.add(index, newObj);
					// update GUI after change
					editor.elementEditors.clear();
					editor.createElementEditors();
					editor.updateGUI();
				}
			});
		} else
			editButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					showEditorToPopUp();
				}
			});
		editButton.addFocusListener(new FocusAdapter() {

			public void focusGained(FocusEvent e) {
				updatePreview();
			}
		});
		preview.setEnabled(false);
		preview.setDisabledTextColor(Color.BLACK);
		preview.setPreferredSize(new Dimension(PREVIEWWIDTH, (int) editButton
				.getPreferredSize().getHeight()));
		preview.setHorizontalAlignment(JTextField.LEADING | JTextField.LEFT);
		updateGUI();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(preview);
		add(editButton);

		// Amir Obertinca: Edited 12/April/2008
		// this checks is the <code>rootDisplay</code> is instanceof
		// ColletionsEditor
		// then it adds a 'delete' button for each element in the collection,
		// and the
		// action listenere - which deletes the specified element of the
		// Collection
		if (rootDisplay instanceof CollectionEditor) {
			deleteButton = new JButton(DELTE_BUTTON_TEXT);
			deleteButton.setPreferredSize((new JButton(CREATE_BUTTON_TEXT))
					.getPreferredSize());
			deleteButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent ae) {
					// System.out.println("The Item '" + profile + "' should be
					// deleted, with the content '" +editObj+"'.");
					CollectionEditor editor = (CollectionEditor) rootDisplay;
					Object elementToDelete = getObjectForEditor();
					editor.editCollCopy.remove(elementToDelete);
					editor.elementEditors.clear();
					editor.createElementEditors();
					editor.updateGUI();
				}
			});
			add(deleteButton);
		}

	}

	/**
	 * Returns a String meant to be displayed beside the "Edit"-button.
	 * 
	 * @return String text to be displayed as a preview to the object edited by
	 *         the editor to pop up
	 */
	protected String getPreviewText() {
		if (propertyValue != null)
			return propertyValue.toString();
		// if (editObjCopy!=null)
		// return editObjCopy.toString();

		else
			return editObj.toString();
		// return "";
	}

	/**
	 * Checks the text currently displayed in JTextField <code>preview</code>
	 * against the text to be displayed on basis of the current value(s) of
	 * <code>editObj</code>. Returns true, if <code>preview</code> does not
	 * show the text currently valid.
	 * 
	 * @return true if JTextField</code>preview</code> displayes an obsolete
	 *         text, false otherwise
	 */
	public boolean hasContentChanged() {
		if (preview.getText().equals(getPreviewText()))
			return false;
		else
			return true;
	}

	/**
	 * Updates the GUI by adjusting the button's text and setting the preview
	 * text in the JTextField.
	 */
	public void updateGUI() {
		super.updateGUI();
		updatePreview();
	}

	/**
	 * Updates the JTextField.
	 */
	public void updatePreview() {
		preview.setText(getPreviewText());
		preview.setCaretPosition(0);
	}

	/**
	 * Sets <code>dataChanged</code> to false and updates the preview text.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
	 */
	public void updateDisplay() {
		dataChanged = false;
		updatePreview();
	}

	/**
	 * If the specified MouseListener is a SelectionAdapter (inner class of
	 * SelectionController), a MouseListener is added to the JTextField
	 * <code>preview</code> which delegates its clicked and pressed
	 * MouseEvents to the specified MouseListener, with the PreviewEditor as the
	 * source of the MouseEvent.
	 * 
	 * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
	 */
	public void addMouseListener(final MouseListener l) {
		SelectionController sc = new SelectionController(null);
		Class saClass = sc.getSelectionAdapter().getClass();
		if (saClass.isAssignableFrom(l.getClass())) {
			preview.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					e.setSource(PreviewEditor.this);
					l.mouseClicked(e);
					preview.setBackground(Color.LIGHT_GRAY);
				}

				public void mousePressed(MouseEvent e) {
					e.setSource(PreviewEditor.this);
					l.mousePressed(e);
					preview.setBackground(Color.LIGHT_GRAY);
				}
			});
		}
	}

}
