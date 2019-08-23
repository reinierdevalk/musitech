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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * This editor is itself a button and lets "pop up" a new editor-window when
 * pressed. <br>
 * <br>
 * It might be useful to use this editor in case of objects having properties
 * which are itself objects having properties... <br>
 * <br>
 * If the property <code>propertyName</code> is null the button displays
 * "create..." and creates a new instance when pressed using the empty
 * constructor. <br>
 * <br>
 * If you have an editor and want to have it popped by such a ButtonEditor you
 * can use the <code>setEditorToPopUp</code> method of this class. But it is
 * easier to use the EditorFactory's <code>createPopUpWrapper</code> method to
 * do this.
 * 
 * @author Tobias Widdra
 */
public class PopUpEditor extends AbstractComplexEditor implements Wrapper {

	/**
	 * Button to press for popup window.
	 */
	protected JButton editButton;

	/**
	 * Text displayed on the button when property is not null.
	 */
	protected final String EDIT_BUTTON_TEXT = "Edit...";

	/**
	 * Text displayed on the button when property is null.
	 */
	protected final String CREATE_BUTTON_TEXT = "Create...";

	/**
	 * Editor to pop up when the button is pressed. <br>
	 * <br>
	 * If this field is set by the <code>setEditorToPopUp</code> method. If it
	 * is not set/set to null a new editor is constructed in the
	 * <code>actionPerformed</code> mehtod according to <code>editObj</code>
	 * and <code>propertyName</code>.
	 */
	protected Editor editorToPopUp;

	/**
	 * Initializes this PopUpEditor by setting the editor's <code>editObj</code>,
	 * <code>profile</code> and <code>rootDisplay</code> to the specified
	 * arguments. Overwrites method
	 * <code>init(Object, EditingProfile, Display)</code> in class
	 * <code>AbstractComplexEditor</code> because the children editors need
	 * not be created at once. Children editors will be created by the
	 * <code>editorToPopUp</code> after the "Edit" has been activated.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
	 *      de.uos.fmt.musitech.framework.editor.EditingProfile,
	 *      de.uos.fmt.musitech.framework.editor.Display)
	 */
	public void init(Object editObj, EditingProfile profile, Display root) {
		this.editObj = editObj;
		this.profile = profile;
		if (profile != null) {
			this.propertyName = profile.getPropertyName();
			setPropertyValue();
		}
		this.rootDisplay = root;
		registerAtChangeManager();
		createGUI();
	}

	/**
	 * Creates the graphical user interface. <br>
	 * The <code>editButton</code> is created and its label set to
	 * <code>CREATE_BUTTON_TEXT</code> or <code>EDIT_BUTTON_TEXT</code>
	 * appropriatly.
	 */
	public void createGUI() {
		editButton = new JButton();
		editButton.setPreferredSize((new JButton(CREATE_BUTTON_TEXT))
				.getPreferredSize());
		// if ((propertyName != null) && (propertyValue == null))
		// editButton.setText(CREATE_BUTTON_TEXT);
		// else
		// editButton.setText(EDIT_BUTTON_TEXT);
		editButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showEditorToPopUp();
			}
		});
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(editButton);
		updateGUI();
	}

	/**
	 * Overwrites method <code>applyChanges()</code> of class
	 * <code>AbstractComplexEditor</code> as the PopUpEditor (resp. its
	 * <code>editorToPopUp</code>) is operating on the working copy
	 * <code>editObjCopy</code>. <br>
	 * The property of <code>editObjCopy</code> corresponding to
	 * <code>propertyName</code> is set to <code>propertyValue</code> and
	 * <code>editObjCopy</code> is written back to <code>editObj</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Editor#applyChanges()
	 */
	/*
	 * public void applyChanges(){ dataChanged = false; if
	 * (propertyValue!=null){ // ReflectionAccess refCopy =
	 * ReflectionAccess.accessForClass(editObjCopy.getClass()); // if
	 * (refCopy.hasPropertyName(propertyName)){ //
	 * refCopy.setProperty(editObjCopy, propertyName, propertyValue); // }
	 * ReflectionAccess refCopy =
	 * ReflectionAccess.accessForClass(editObj.getClass()); if
	 * (refCopy.hasPropertyName(propertyName)){ refCopy.setProperty(editObj,
	 * propertyName, propertyValue); } } //
	 * ObjectCopy.copyPublicProperties(editObjCopy, editObj); if
	 * (isOutmostEditor()) sendDataChangeEvent(); //just for testing: if
	 * (this==rootEditor) printEditObjForTesting(); }
	 */

	/**
	 * Sets <code>editorToPopUp</code> to the given Display <br>
	 * <code>editObj</code> is set to <code>editorToPopUp.getEditObj()</code>.
	 * 
	 * @param editorToPopUp Display to wrap
	 */
	public void setWrappedView(Display editorToPopUp) {
		this.editorToPopUp = (Editor) editorToPopUp;
		this.editObj = editorToPopUp.getEditObj();
	}

	private DataChangeEvent dataChangeEvent;

	/**
	 * Overwrites method <code>dataChanged(DataChangeEvent)</code> of
	 * <code>AbstractEditor</code> in order to record the incoming
	 * DataChangeEvent in the field <code>dataChangeEvent</code>. This allows
	 * sending the DataChangeEvent to the <code>editorToPopUp</code> when that
	 * editor is created and <code>dataChanged</code> of this PopUpEditor is
	 * true. Otherwise the <code>editorToPopUp</code> would know about
	 * external data changes only if it is already opened when the
	 * DataChangeEvent reaches the PopUpEditor.
	 * 
	 * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
	 */
	public void dataChanged(DataChangeEvent e) {
		dataChangeEvent = new DataChangeEvent(e.getSource(), e.getChangedData());
		super.dataChanged(e);
	}

	/**
	 * Overwrites method <code>isRemoteEventSource</code> in class
	 * <code>AbstractEditor</code> to check the the specified
	 * <code>eventSource</code> against <code>editorToPopUp</code> as well.
	 * Returns true if the specified <code>eventSource</code> is not this
	 * editor, this editor's <code>editorToPopUp</code> or another editor
	 * related to this editor, false otherwise.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#isRemoteEventSource(java.lang.Object)
	 */
	protected boolean isRemoteEventSource(Object eventSource) {
		boolean related = false;
		if (editorToPopUp != null)
			related = eventSource.equals(editorToPopUp);
		return super.isRemoteEventSource(eventSource) && !related;
	}

	/**
	 * Returns true if this editor has the focus.
	 * 
	 * @return true if this editor has the focus, false otherwise
	 * @see de.uos.fmt.musitech.framework.editor.Editor#isFocused()
	 */
	public boolean isFocused() {
		return isFocusOwner(); // TODO welche Komponenten fragen?
	}

	/**
	 * Shows the <code>editorToPopUp</code> in a separate EditorWindow.
	 */
	protected void showEditorToPopUp() {
		// if "Create" open editor to create new value
		if (editButton.getText().equalsIgnoreCase(CREATE_BUTTON_TEXT)) {
			propertyValue = createNewValue("New propertyValue");
			if (propertyValue != null)
				setValueCreated(true);
			updateGUI();
			return;
		}
		// else ("Edit") edit editObj/propertyValue
		editorToPopUp = createEditorToPopUp();
		EditorWindow w = new EditorWindow(getLabeltext() + " Editor");
		w.addEditor(editorToPopUp);
		w.addWindowListener(new PopUpWindowListener());
		w.show();
		// alternativ, falls modal:
		// showEditorAsModal(editorToPopUp, labeltext+" Editor");
		// updateEditor();
	}

	/**
	 * Returns the object to be edited in the <code>editorToPopUp</code>.
	 * This object is <code>propertyValue</code> if <code>propertyName</code>
	 * is not null, or <code>editObj</code> otherwise.
	 * 
	 * @return Object to be edited in the <code>editorToPopUp</code>
	 */
	protected Object getObjectForEditor() {
		if (editObj != null && propertyName == null)
			return editObj;
		if (editObj != null && propertyName != null) {
			if (propertyValue == null)
				setPropertyValue();
			return propertyValue;
		}
		return null;
	}

	/**
	 * Returns an Editor meant to be the <code>editorToPopUp</code>.
	 * 
	 * @return Editor to be the <code>editorToPopUp</code>
	 */
	protected Editor createEditorToPopUp() {

		if (getObjectForEditor() != null) {
			try {
				// editorToPopUp =
				// EditorFactory.createEditor(getObjectForEditor(), rootDisplay,
				// null);

				boolean readOnly = (profile != null && profile.isReadOnly());
				
				//Author: Amir Obertinca - This checks is the Editor to popup is an Array
				//therefore, it provides an <code>ObjectArrayEditor</code> if true,
				//and sets the properties of the profile accordingly
				if (propertyValue != null && propertyValue.getClass().isArray()) {
					EditingProfile prof = new EditingProfile();
					prof.setReadOnly(readOnly);
					String name = propertyValue.getClass().getName();
					// System.out.println("propertyValue: " + name);
					EditorType arrayEditorType = new EditorType(
						name,
						"de.uos.fmt.musitech.framework.editor.ObjectArrayEditor",
						null);
					prof.setEditortype(arrayEditorType.getTypeName());
					prof.setPropertyName(profile.getPropertyName());
					prof.setChildren(profile.getChildren());
					prof.setLabel(profile.getLabel());
					editorToPopUp = EditorFactory.createEditor(editObj, prof);
				} else {
					if (profile != null)
						editorToPopUp = EditorFactory.createEditor(
							editObj, profile);
					else
						editorToPopUp = EditorFactory.createEditor(
							getObjectForEditor(), readOnly);
				}
				if (externalChanges())
					editorToPopUp.dataChanged(dataChangeEvent);
			} catch (EditorConstructionException ece) {
				ece.printStackTrace();
			}
		}

		return editorToPopUp;
	}

	/**
	 * Getter for <code>editorToPopUp</code>
	 * 
	 * @return
	 */
	protected Editor getEditorToPopUp() {
		return editorToPopUp;
	}

	/**
	 * Invokes method <code>interestShrinked</code> in class
	 * <code>DataChangeManager</code> to remove this editor and its
	 * <code>eidotrToPopUp</code> as well as the <code>editObj</code> or
	 * <code>propertyValue</code> from the DataChangeManager's
	 * <code>table</code> of views and changed data.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#resignFromChangeManager()
	 */
	// protected void resignFromChangeManager() {
	// super.resignFromChangeManager();
	// if (editorToPopUp != null)
	// editorToPopUp.resignFromChangeManager();
	// }
	/**
	 * Updates the editor by setting <code>dataChanged</code> to false,
	 * updating <code>editObjCopy</code> and recreating the GUI.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#updateEditor()
	 */
	public void updateDisplay() {
		dataChanged = false;
		// if (editObjCopy != null)
		// ObjectCopy.copyPublicProperties(editObj, editObjCopy);
		updateGUI();
	}

	/**
	 * Updates the editor's GUI.
	 */
	public void updateGUI() {
		if ((propertyName != null) && (propertyValue == null))
			editButton.setText(CREATE_BUTTON_TEXT);
		else
			editButton.setText(EDIT_BUTTON_TEXT);
	}

	class PopUpWindowListener extends WindowAdapter {

		public void windowDeactivated(WindowEvent we) {
			// updateEditor();
			// Modif040803: if added
			if (dataChangeEvent != null)
				updateGUI();
			// System.out.println("The new editor" + propertyValue.toString());
		}
	}

}
