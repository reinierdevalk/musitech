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
 * Created on 20.05.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * This should be the superclass of all editors. <br>
 * <br>
 * See package description for detail of the basic editor concept.
 * 
 * @author Tobias Widdra and Kerstin Neubarth
 */
public abstract class AbstractEditor extends JPanel implements Editor {

	/**
	 * The <code>focusListener</code> calls <code>focusReceived()</code>,
	 * when it gets a <code>focusGained()</code> message.
	 */
	protected FocusListener focusListener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			AbstractEditor.this.focusReceived();
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	};

	/**
	 * Collects the data that has been changed by this editor (or an editor
	 * associated with this one, i.e. a child or element editor) into one
	 * Vector. As this depends on the editor's structure and its objects, this
	 * method is to be implemented in the subclasses.
	 * 
	 * @return Vector containing the objects that have been changed by this
	 *         editor
	 * @see de.uos.fmt.musitech.framework.editor.Editor#getChangedData()
	 */
	// abstract public Collection getEditedData();
	/**
	 * Outmost Display this editor is attached to. Might be this editor itself.
	 * <br>
	 * The <code>rootDisplay</code> may use a JScrollPane while its nested
	 * displays must not. TODO Ausnahmen? (was für dynamische Displays und
	 * zooming/scaling?) <br>
	 * For roots being Editors (not just Displays): If data is changed in an
	 * Editor, i.e. changes are applied to the <code>editObj</code> (see
	 * method <code>applyChanges()</code>), the root editor gathers the
	 * changed data from its nested editors and sends a DataChangeEvent to the
	 * DataChangeManager. (So, only editors which are root editors send a
	 * DataChangeEvent. When sending the event, the DataChangeEvent's
	 * <code>source</code> is set to the root editor.)
	 */
	protected Display rootDisplay = this;

	/**
	 * Reference to the object which has one or more properties to be edited.
	 * <br>
	 * Notice: In case of a complex editor <code>editObj</code> might be a
	 * property of a "surrounding" object!
	 */
	protected Object editObj;

	/**
	 * EditingProfile (provided by editObj or created by the EditorFactory)
	 */
	protected EditingProfile profile;

	/**
	 * <code>propertyName</code> is the name of the property of
	 * <code>editObj</code> which this editor edits. <br>
	 * This should be set to null in case of the outmost editor (see package
	 * description). <br>
	 * If the property is null it is possible to get the class of this property
	 * via reflection access to <code>editObj</code>.
	 */
	protected String propertyName;

	/**
	 * Contains the value of the property <code>propertyName</code> of
	 * <code>editObj</code>. This is set in <code>setPropertyValue()</code>
	 * via reflection access. <br>
	 * Notice that the default provides a <i>reference</i> rather than a copy.
	 * (That might be changed in further releases).
	 */
	protected Object propertyValue;

	/**
	 * Field to check if data this editor is working on has been changed by
	 * another editor. Is to be set true, if data has been changed (see method
	 * <code>dataChanged(DataChangeEvent)</code>).
	 */
	public boolean dataChanged = false;

	/**
	 * Is set true, if the user changes the input to the editor. The flag must
	 * be reset, when the changes are applied to the <code>editObj</code>.
	 */
	boolean dirty = false;

	/**
	 * Is set true, when a new propertyValue is created. <br>
	 * The flag is checked in methods <code>getEditedData()</code> of the
	 * <code>AbstractSimpleEditor</code> and
	 * <code>AbstractComplexEditor</code>. If it is true, the
	 * <code>editObj</code> of the editor is added to the Collection
	 * containing the changed data.
	 */
	private boolean valueCreated = false;

	/**
	 * If <code>promptUpdate</code> is false, the Editor is automatically
	 * updated to external changes als long as no changes have been performed in
	 * this Editor as well (that is, <code>dirty</code> is false). Otherwise
	 * the user is offered to update or to overwrite.
	 */
	boolean promptUpdate = false;

	/**
	 * This method should get invoked if an event occurs which indicates that
	 * the changes done by the editor should be applied to the
	 * <code>editObj</code>.
	 */
	@Override
	abstract public void applyChanges();

	/**
	 * Returns true if all input is valid, false otherwise. <br>
	 * <br>
	 * This method returns always true as default. So you should overwrite it in
	 * case you want to have some different behavior. An example could be that
	 * the last input was invalid so you want to prevent the editor to be closed
	 * by a "OK" button.
	 */
	@Override
	public boolean inputIsValid() {
		return true;
	}

	// --------------------- getter & setter -----------------

	/**
	 * Getter for <code>editObj</code>.
	 * 
	 * @return
	 */
	@Override
	public Object getEditObj() {
		return editObj;
	}

	/**
	 * Returns the label as indicated in the EditingProfile <code>profile</code>.
	 * If <code>profile</code> is null, an EditingProfile is created using the
	 * EditorFactory.
	 * 
	 * @return String <code>label</code> in the <code>profile</code>
	 */
	public String getLabeltext() {
		if (profile == null)
			profile = EditorFactory.getOrCreateProfile(editObj);
		if (profile.getLabel() == null)
			// profile.setLabel(
			// EditorFactory
			// .createDefaultProfile(editObj, profile.getPropertyName())
			// .getLabel());
			profile.setLabel(EditorFactory.createDefaultProfile(editObj,
				profile.getPropertyName()).getLabel());
		// return profile.getLabel();
		// Modif200104
		String labeltext = profile.getLabel();
		// if (Character.isLowerCase(labeltext.charAt(0))) {
		// labeltext =
		// new
		// Character(Character.toUpperCase(labeltext.charAt(0))).toString().concat(labeltext.substring(1));
		// }
		return labeltext;
	}

	/**
	 * Getter for <code>profile</code>.
	 * 
	 * @return Editing Profile
	 */
	@Override
	public EditingProfile getEditingProfile() {
		return profile;
	}

	/**
	 * This method is invoked by the <code>init</code> method in case the
	 * propertyName is set. It sets the value of <code>propertyValue</code>.
	 * <br>
	 * <br>
	 * By default (i. e. by this implementation) <code>propertyValue</code> is
	 * set to the value of the property <code>propertyName</code> in
	 * <code>editObj</code> via reflection access. Notice that this mechanism
	 * provides a <i>reference</i> rather than a copy of the object! <br>
	 * If you want some different behavior (e. g. do not set propertyValue at
	 * all for you do not use it) overwrite this method.
	 */
	protected void setPropertyValue() {
		ReflectionAccess ref = ReflectionAccess.accessForClass(editObj
				.getClass());
		if ((propertyName != null) && (ref.hasPropertyName(propertyName)))
			propertyValue = ref.getProperty(editObj, propertyName);
	}

	/**
	 * Getter for <code>dataChanged</code>.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean externalChanges() {
		return dataChanged;
	}

	/**
	 * Checks if editor <code>isOutmost</code>.
	 * 
	 * @return
	 */
	public boolean isOutmostEditor() {
		return (rootDisplay == this);
	}

	/**
	 * Getter for <code>rootEditor</code>.
	 * 
	 * @return AbstractEditor <code>rootEditor</code> of this editor
	 */
	@Override
	public Display getRootDisplay() {
		return rootDisplay;
	}

	/**
	 * Setter for <code>promptUpdate</code>.
	 * 
	 * @param promptUpdate boolean
	 */
	@Override
	public void setPromptUpdate(boolean promptUpdate) {
		this.promptUpdate = promptUpdate;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Editor#getPromptUpdate()
	 */
	// public boolean getPromptUpdate() {
	// return promptUpdate;
	// }
	/**
	 * Setter for <code>dirty</code> of this AbstractEditor. Sets
	 * <code>dirty</code> of the root editor as well (if this editor is not
	 * the root).
	 * 
	 * @param dirty boolean to be set as <code>dirty</code> of this editor
	 * @see de.uos.fmt.musitech.framework.editor.Editor#setDirty(boolean)
	 */
	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		if (rootDisplay != this && rootDisplay instanceof Editor) // when the
																	// subordinate
																	// editors
																	// allow
																	// data
																	// changes,
																	// the root
																	// is an
																	// editor
			((Editor) rootDisplay).setDirty(dirty);
	}

	// -------------- methods for interacting with the DataChangeManager
	// ---------

	/**
	 * Adds this editor and its <code>editObj</code> or
	 * <code>propertyValue</code> to the DataChangeManager's
	 * <code>table</code> of views and changed data.
	 */
	public void registerAtChangeManager() {
		// Vector objsToChange = new Vector();
		Collection objsToChange = new ArrayList();
		if (propertyValue != null) {
			// if (!ObjectCopy.isPrimitiveType(propertyValue) && !(propertyValue
			// instanceof String))
			objsToChange.add(propertyValue);
		} else if (propertyName == null && editObj != null)
			// if (!ObjectCopy.isPrimitiveType(editObj) && !(editObj instanceof
			// String))
			objsToChange.add(editObj);
		if (!objsToChange.isEmpty())
			DataChangeManager.getInstance().interestExpandElements(this,
				objsToChange);
	}

	/**
	 * Informs the DataChangeManager that data has been changed by this editor.
	 */
	public void sendDataChangeEvent() {
		try {
			// gather changed data
			ArrayList changedObjects = new ArrayList();
			Collection editedData = getEditedData();
			if (editedData != null && !editedData.isEmpty())
				changedObjects.addAll(editedData);
			// send DataChangeEvent
			if (!changedObjects.isEmpty())
				DataChangeManager.getInstance().changed(changedObjects,
					new DataChangeEvent(this, changedObjects));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void sendDataChangeEvent(Collection editedData) {
		if (editedData != null && !editedData.isEmpty()) {
			DataChangeManager.getInstance().changed(editedData,
				new DataChangeEvent(this, editedData));
		}
	}

	/**
	 * Updates the editor.
	 */
	@Override
	abstract public void updateDisplay();

	/**
	 * Is set true as long as the dialog notifiying about changed data is shown.
	 * <br>
	 * (See method <code>showChangedObjectsDialog()</code>.)
	 */
	boolean changeDialogShowing = false;

	/**
	 * Displays a JDialog informing the user that objects this editor is working
	 * on have been changed by another editor and offering to accept or
	 * overwrite these changes.
	 */
	public void showChangedObjectsDialog() {
		if (changeDialogShowing)
			return;
		changeDialogShowing = true;
		Object[] buttons = {"Accept Changes", "Overwrite Changes", "Cancel"};
		int response = JOptionPane
				.showOptionDialog(
					this,
					"The data has been changed in another editor.\nDo you want to accept these changes or overwrite them with the data in this editor?",
					"Changed Data", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
		if (response == JOptionPane.YES_OPTION) {
			// accept changes
			updateDisplay();
			// dirty = false;
			// dataChanged = false;
		} else {
			// overwrite changes
			// Modif040611
			dirty = true;
			// end Modif
			applyChanges();
		}
		changeDialogShowing = false;
	}

	/**
	 * This method is invoked when this editor receives a DataChangeEvent.
	 * <code>dataChanged</code> is set true, if the DataChangeEvent has not
	 * been caused by this editor or an editor related to this editor, e.g. the
	 * <code>rootDisplay</code>
	 * 
	 * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.datamanager.DataChangeEvent)
	 */
	@Override
	public void dataChanged(DataChangeEvent e) {
		Object eventSource = null;
		try {
			eventSource = e.getSource();
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		}
		if (eventSource != null)
			dataChanged = isRemoteEventSource(eventSource);
		if (rootDisplay != this)
			rootDisplay.dataChanged(e);
		else if (isFocused())
			focusReceived();
	}

	/**
	 * Returns false if the specified <code>eventSource</code> is related to
	 * this editor in either of the following ways: eventSource is this editor;
	 * eventSource is the <code>rootEditor</code> of this editor; this editor
	 * is a child editor or element editor of <code>eventSource</code>.
	 * 
	 * @param eventSource Object to be checked if related to this editor via
	 *            identity or dependency
	 * @return true if the specified <code>eventSource</code> is not this
	 *         editor, its <code>rootEditor</code> or a kind of parent editor,
	 *         false otherwise
	 */
	protected boolean isRemoteEventSource(Object eventSource) {
		// boolean isSource = eventSource.equals(this);
		// boolean sourceIsRoot = rootEditor.equals(eventSource);
		// boolean childOfSource = false;
		// if (eventSource instanceof AbstractComplexEditor) {
		// childOfSource =
		// ((AbstractComplexEditor) eventSource).isChildEditor(this);
		// }
		// boolean elementOfSource = false;
		// if (eventSource instanceof CollectionMapEditor) {
		// elementOfSource =
		// ((CollectionMapEditor) eventSource).isElementEditor(this);
		// }
		// if (eventSource instanceof MetaDataEditor)
		// elementOfSource = ((MetaDataEditor) eventSource).isItemEditor(this);
		// return !isSource && !childOfSource && !elementOfSource &&
		// !sourceIsRoot;
		return (eventSource != rootDisplay);
	}

	/**
	 * When the editor gains the focus, it requests the root editor to check if
	 * data has been changed in one of the editors belonging to this editor
	 * hierarchy. If data has been changed, the user is informed and offered to
	 * accept or overwrite these changes.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Editor#focusReceived()
	 */
	@Override
	public void focusReceived() {
		if (rootDisplay != this)
			rootDisplay.focusReceived();
		else
			synchronized (editObj) {
				updateToChanges();
			}
	}

	/**
	 * Checks the conditions for updating the editor and updates if the
	 * conditions are met. <br>
	 * If there are conflicting changes (i.e. there are external changes and the
	 * user has changes data inside this editor so that <code>dirty</code> is
	 * true), the user is notified and offered to update or to overwrite
	 * external changes with his own data. If there are external changes and
	 * <code>promptUpdate</code> the user is asked as well, even if there are
	 * no conflicts. An automatic update is performed if there are external
	 * changes but no conflicts and if <code>promptUpdate</code> is false
	 * (which is the default).
	 */
	protected void updateToChanges() {
		// if no external changes, return
		if (!externalChanges())
			return;
		if (this instanceof Editor && (dirty || promptUpdate)) {
			SwingUtilities.invokeLater(new Thread() {

				@Override
				public void run() {
					showChangedObjectsDialog();
				}
			});
		} else {
			SwingUtilities.invokeLater(new Thread() {

				@Override
				public void run() {
					updateDisplay();
				}
			});
		}
	}

	/**
	 * Removes the editor from the table of the DataChangeManager.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Editor#destroy()
	 */
	@Override
	public void destroy() {
		DataChangeManager.getInstance().removeListener(this);
	}

	// -------- methods for handling null-propertyValues (creating new values)
	// ------

	/**
	 * Returns an Object meant to be the <code>propertyValue</code> of this
	 * editor which before has been null. A dialog is opened asking the user to
	 * enter the value(s) for the <code>propertyValue</code> to be created.
	 * 
	 * @param editorTitle String to be displayed on top of the editor used for
	 *            specifying the value(s) of the Object to be created
	 * @return Object to be the newly created <code>propertyValue</code>
	 */
	protected Object createNewValue(String editorTitle) {
		Object newValue = null;
		Class classOfValueToCreate = getClassOfValueToCreate();
		if (classOfValueToCreate == null) {
			return null;
		}
		if (!classOfValueToCreate.isArray()
			&& (ObjectCopy.isElementaryClass(classOfValueToCreate) || classOfValueToCreate
					.isAssignableFrom(String.class))) {
			newValue = createNewPrimitiveValue(classOfValueToCreate);
		} else
			newValue = createNewComplexValue(classOfValueToCreate, editorTitle);
		// modif010604
		if (newValue != null) {
			dirty = true;
			// valueCreated = true;
		}
		// end modif
		return newValue;
	}

	/**
	 * Returns the class of the <code>propertyValue</code>. This method
	 * currently is invoked when <code>propertyValue</code> originally is null
	 * and the user chooses to create a new <code>propertyValue</code>. <br>
	 * If <code>editObj</code> is of type MetaDataValue (and the
	 * <code>value</code> of MetaDataValue is null), the class is derived from
	 * the <code>mimeType</code> given in the MetaDataValue. For all other
	 * <code>editObj<code>s, the class is 
	 * determined via ReflectionAccess using the <code>propertyName</code>.
	 * 
	 * @return Class of the propertyValue to create
	 */
	protected Class getClassOfValueToCreate() {
		Class propertyClass = null;
		ReflectionAccess ref = ReflectionAccess.accessForClass(editObj
				.getClass());
		if (propertyName != null && ref.hasPropertyName(propertyName))
			propertyClass = ref.getPropertyType(propertyName);
		return propertyClass;
	}

	/**
	 * Returns an Object of the specified class. This class <code>cla</code>
	 * represents a primitive data type.
	 * 
	 * @param cla Class the new value is to have
	 * @return Object a new instance of the specified class with a value asked
	 *         for in an InformationDialog, or null if the instance could not be
	 *         created
	 */
	protected Object createNewPrimitiveValue(Class cla) {
		// extract "simple" String representation of type represented by cla
		String type = cla.toString();
		int index = type.lastIndexOf(".");
		if (index != -1)
			type = type.substring(index + 1);
		// ask for value
		String input = (JOptionPane.showInputDialog(this,
			"Please enter a new value (of type " + type + ")."));
		/*String input = (String) JOptionPane.showInputDialog(
            this,
            "Please enter a new value (of type " + type + ").",
            "Replace Element",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            type);*/
		if (input == null)
			return null;
		input = input.trim();
		// get Constructor
		Constructor constructor = null;
		Class[] classes = {String.class};
		try {
			constructor = cla.getConstructor(classes);
		} catch (SecurityException se) {
			se.printStackTrace();
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		}
		// get instance
		Object obj = null;
		try {
			if (constructor != null) {
				Object[] arguments = {input};
				obj = constructor.newInstance(arguments);
			} else
				showNoPossibleTypeMessage();
		} catch (IllegalArgumentException illArgE) {
			illArgE.printStackTrace();
			showNoPossibleValueMessage(input, cla.toString());
		} catch (InstantiationException instE) {
			instE.printStackTrace();
			showNoPossibleValueMessage(input, cla.toString());
		} catch (IllegalAccessException illAccE) {
			illAccE.printStackTrace();
			showNoPossibleValueMessage(input, cla.toString());
		} catch (InvocationTargetException invTarE) {
			invTarE.printStackTrace();
			showNoPossibleValueMessage(input, cla.toString());
		}
		return obj;
	}

	/**
	 * Returns a new instance of the specified Class. For getting the new
	 * values, an appropriate editor is opened.
	 * 
	 * @param cla Class the new instance is to have
	 * @return Object new instance of the specified class with values asked for
	 *         in an editor
	 */
	protected Object createNewComplexValue(Class cla, String editorTitle) {
		// create new object of specified class to set up an editor
		Object newValue = null;
		// get Constructor
		Constructor constructor = null;
		try {
			constructor = cla.getConstructor(new Class[0]);
		} catch (SecurityException se) {
			se.printStackTrace();
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		}
		// get instance
		Object newObj = null;
		try {
			if (constructor != null)
				newObj = constructor.newInstance(new Object[0]);
			else
				showCreationFailedMessage();
		} catch (IllegalArgumentException illArgE) {
			illArgE.printStackTrace();
		} catch (InstantiationException instE) {
			instE.printStackTrace();
		} catch (IllegalAccessException illAccE) {
			illAccE.printStackTrace();
		} catch (InvocationTargetException invTarE) {
			invTarE.printStackTrace();
		}
		// create and show editor for setting the new value
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(newObj);
		} catch (EditorConstructionException ece) {
			ece.printStackTrace();
			showCreationFailedMessage();
		}
		if (editor != null) {
			if (showEditorAsModal(editor, editorTitle))
				newValue = editor.getEditObj();
		}
		return newValue;
	}

	/**
	 * Returns true if this editor edits a property (that is, the
	 * <code>propertyName</code> is not null) and its
	 * <code>propertyValue</code> is null, but the corresponding property in
	 * the <code>editObj</code> is not. <br>
	 * This method is used in the interaction of editors in the following
	 * scenario: Two editors are editing the same object which initially has one
	 * property null. In one editor a new property value is created. After
	 * applying the changes to the edited object, the second editor shows a
	 * dialog when gaining the focus which offers the user to accept the changes
	 * (performed in the first editor) or to overwrite them. When the user
	 * chooses to overwrite, this corresponds to removing the property.
	 * 
	 * @return boolean true if <code>propertyName</code> is not null,
	 *         <code>propertyValue</code> is null and the corresponding
	 *         property of <code>editObj</code> is not null, false otherwise
	 */
	protected boolean isValueToRemove() {
		String nullPropertyName = null;
		if (propertyName != null && propertyValue == null)
			nullPropertyName = propertyName;
		if (nullPropertyName != null) {
			ReflectionAccess ref = ReflectionAccess.accessForClass(editObj
					.getClass());
			if (ref.hasPropertyName(nullPropertyName))
				if (ref.getProperty(editObj, nullPropertyName) != null)
					return true;
		}
		return false;
	}

	/**
	 * If the <code>propertyValue</code> is null, but the
	 * <code>propertyName</code> is not, the corresponding property of the
	 * <code>editObj</code> is set null.
	 */
	protected void resetNullProperty() {
		if (propertyName != null && propertyValue == null) {
			ReflectionAccess ref = ReflectionAccess.accessForClass(editObj
					.getClass());
			if (ref.hasPropertyName(propertyName))
				ref.setProperty(editObj, propertyName, null);
		}
	}

	/**
	 * DialogActionListener is a listener, that closes a dialog, when it
	 * receives an ActionEvent with message 'OK or 'cancel' and remembers,
	 * whether the dialog was confirmed by 'OK' or not.
	 * 
	 * @author tweyde
	 */
	class DialogActionListener implements ActionListener {

		Dialog dialog;
		boolean confirmed;

		public DialogActionListener(Dialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			String command = ae.getActionCommand();
			if (command.equals("cancel")) {
				dialog.dispose();
				confirmed = false;
			}
			if (command.equals("OK")) {
				dialog.dispose();
				confirmed = true;
			}
		}
	}

	/**
	 * Shows the specified editor in a modal JDialog. <br>
	 * This method is used in creating a new complex propertyValue. The modal
	 * character of the JDialog forces the user to complete the input before
	 * performing other editing.
	 * 
	 * @param editor to be shown in the modal JDialog
	 * @param title String to be shown as title of the modal JDialog
	 * @return true if the dialog has been confirmed, false, if it has been
	 *         canceled.
	 */
	protected boolean showEditorAsModal(final Editor editor, String title) {
		final JDialog dialog = new JDialog(new JFrame(), title, true);
		EditorPanel editorPanel = new EditorPanel();
		editorPanel.addEditor(editor);
		DialogActionListener listener = new DialogActionListener(dialog);
		editorPanel.addActionListener(listener);
		dialog.getContentPane().add(editorPanel);
		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.show();
		return listener.confirmed;
	}

	/**
	 * Shows a MessageDialog to tell that the creation of the new value has
	 * failed.
	 */
	protected void showCreationFailedMessage() {
		JOptionPane.showMessageDialog(this,
			"The new value could not be created.");
		// TODO weitere Hinweise ergänzen?
	}

	/**
	 * Displays a MessageDialog to indicate that the creation of a new instance
	 * of the type chosen by the user has failed.
	 */
	protected void showNoPossibleTypeMessage() {
		JOptionPane.showMessageDialog(this, "This type is not possible here.");
		// TODO ergänzen, welche Bedingungen type erfüllen muss?
	}

	/**
	 * Displays a MessageDialog telling that the user's input is not possible.
	 * 
	 * @param input String indicating the user's input
	 * @param requiredClass Class indicating the required type of the value
	 */
	protected void showNoPossibleValueMessage(String input, String requiredClass) {
		if (requiredClass.startsWith("class"))
			requiredClass = requiredClass.substring(new String("class")
					.length() + 1);
		JOptionPane.showMessageDialog(this, "'" + input
											+ "' is no valid input.\n "
											+ "The value must be of type "
											+ requiredClass);
	}

	/**
	 * Builds up the GUI. This method is invoked by the <code>init()</code>
	 * method. It has an empty body in this class and should be overwritten by a
	 * subclass.
	 */
	abstract protected void createGUI();

	protected boolean conflictingData() {
		// TODO compare values
		return false;
	}

	/**
	 * TODO add comment
	 * @param argValueCreated
	 */
	public void setValueCreated(boolean argValueCreated) {
		this.valueCreated = argValueCreated;
	}

	boolean isValueCreated() {
		return valueCreated;
	}
	
	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	@Override
	public Component asComponent() {
		return this;
	}

}
