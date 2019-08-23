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
 * Created on 24.05.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * This class provides a panel displaying editors and a row of buttons
 * for "OK", "Cancel" and "Apply". 
 * <br> 
 * <br> This class is itself an ActionListener which gets informed when one
 * of these buttons are pressed. The method <code>applyChanges</code> of all
 * editors is invoked when "Apply" or "OK" was pressed and a new ActionEvent
 * is propagated to all registered ActionListeners of this <code>EditorPanel</code>
 * (e. g. a window containing this panel).
 * <br>
 * <br> You can set/add editors which are displayed above the row of buttons.
 * 
 * @author Tobias Widdra
 */
public class EditorPanel extends JPanel implements ActionListener {

	// TODO constants for "OK", "Cancel" and "Apply" to be set as buttonlable AND as ActionCommand!
	JButton OKButton = new JButton("OK");
	JButton cancelButton = new JButton("Cancel");
	JButton applyButton = new JButton("Apply");

	/**
	 * Panel where the editors are added to.
	 */
	JPanel editPanel = new JPanel();

	/**
	 * Vector of all ActionListeners.
	 */
//	Vector actionListeners = new Vector();
	Collection actionListeners = new ArrayList();

	/**
	 * Vector containing the editor(s) added to this EditorPanel.
	 */
//	Vector editors = new Vector();
	Collection editors = new ArrayList();

	boolean activated = false;

	/**
	 * Constructs a new instance by creating and adding the buttons 
	 * (setting their actionCommand first) and by adding the <code>editPanel</code>
	 * with a tiny empty border.
	 */

	// set to public by Wolfram Heyer
	public EditorPanel() {

		// add buttons
		OKButton.setActionCommand("OK");
		cancelButton.setActionCommand("cancel");
		applyButton.setActionCommand("apply");

		OKButton.addActionListener(this);
		cancelButton.addActionListener(this);
		applyButton.addActionListener(this);

		// TODO "setVerityInputWhenFocusTarget(false" still needed? i don't think so... but test it!		
		cancelButton.setVerifyInputWhenFocusTarget(false);

		JPanel buttonPane = new JPanel();
		buttonPane.add(OKButton);
		buttonPane.add(cancelButton);
		buttonPane.add(applyButton);

		setLayout(new BorderLayout());
		add(buttonPane, BorderLayout.SOUTH);

		add(editPanel, BorderLayout.CENTER);
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(new EmptyBorder(2, 3, 3, 3));

	}

	/**
	 * Adds the given editor to <code>editPanel</code> in <code>BorderLayout.CENTER</code>
	 * if <code>editor</code> is not null.
	 * @param editor
	 */
	public void addEditor(final Editor editor) {
		if (editor != null) {
			editPanel.add((Component) editor, BorderLayout.CENTER);
			editors.add(editor);
			FocusListener focusListener = new FocusListener() {
					//				Editor editor; 
		//				public void setEditor(Editor newEditor){
		//					editor = newEditor;
		//				}

	public void focusGained(FocusEvent e) {
					editor.focusReceived();
				}

				public void focusLost(FocusEvent e) {
				}

			};
			editPanel.addFocusListener(focusListener);
			OKButton.addFocusListener(focusListener);
			cancelButton.addFocusListener(focusListener);
			applyButton.addFocusListener(focusListener);
		}
		//if all the editors added to editPanel are read only-editors, the apply-button is disabled
		if (editor != null) {
			if (editor.getEditingProfile() != null) {
				//				if (editor.getEditingProfile().isReadOnly())
				//					applyButton.setEnabled(false);
				//				else
				//					applyButton.setEnabled(true);	
				if (editor.getEditingProfile() != null
					&& !editor.getEditingProfile().isReadOnly())
					activated = true;
			}
		}
		//if only read-only Editors: remove cancelButton and OKButton
		if (!activated) {
			Container buttonPane = OKButton.getParent();
			if (buttonPane.getComponents().length == 3) {
				buttonPane.remove(cancelButton);
				buttonPane.remove(applyButton);
				buttonPane.doLayout();
			}
		} else {
			//else: show all three buttons
			Container buttonPane = OKButton.getParent();
			if (buttonPane.getComponents().length == 1) {
				buttonPane.add(cancelButton);
				buttonPane.add(applyButton);
				buttonPane.doLayout();
			}
		}
	}

	/**
	 * Adds the given ActionListener to <code>actionListeners</code>.
	 * @param l
	 */
	public void addActionListener(ActionListener l) {
		actionListeners.add(l);
	}

	/**
	 * If "OK" or "Apply" was pressed <code>applyChanges()<code> of this class
	 * is invoked (which calls <code>applyChanges()</code> of all editors on this panel).
	 * <br>
	 * <br> A new ActionEvent is propagated to all registered ActionListeners
	 * (done by the method <code>propagateAction(String)</code> of this class).
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("OK")) {
			if (allInputsAreValid()) {
				//Modif040709: if added
				if (activated)
					applyChanges();
				resign();
				propagateAction("OK");
				//System.out.println("EditorPanel.java: The OK clicked");
			}
		} else if (event.getActionCommand().equals("apply")) {
			if (allInputsAreValid())
				applyChanges();
		} else if (event.getActionCommand().equals("cancel")) {
			resign();
			propagateAction("cancel");
		}
	}

	/**
	 * <code>applyChanges()</code> of all editors on the <code>editPanel</code> is invoked.
	 *
	 */
	private void applyChanges() {
		if (!editors.isEmpty()) {
			for (Iterator iter = editors.iterator(); iter.hasNext();) {
				Editor element = (Editor) iter.next();
				element.applyChanges();
				
				//System.out.println("EditorPanel.java RootDisplay: " + element.toString());
			}
		}
	}

	/**
	 * Returns false if at least one editor's input is invalid.
	 * @return
	 */
	private boolean allInputsAreValid() {
		//		Component[] components = editPanel.getComponents();
		boolean allInputsAreValid = true;
		for (Iterator iter = editors.iterator(); iter.hasNext();) {
			Editor element = (Editor) iter.next();
			if (allInputsAreValid)
				allInputsAreValid = element.inputIsValid();
		}
		return true; //allInputsAreValid;
	}

	/**
	 * Propagate <code>new ActionEvent(this,0,command)</code> to all registered
	 * ActionListeners.
	 * @param command
	 */
	private void propagateAction(String command) {
		for (Iterator iter = actionListeners.iterator(); iter.hasNext();) {
			ActionListener l = (ActionListener) iter.next();
			l.actionPerformed(new ActionEvent(this, 0, command));
		}
	}

	/**
	 * When the EditorPanel is closed, the editor(s) of this EditorPanel
	 * resign as DataChangeListeners from the DataChangeManager by invoking
	 * method <code>interestShrinked(DataChangeListener, Collection)</code>
	 * in class DataChangeManager. Therefore, each editor executes its method
	 * <code>resignFromChangeManager()</code>.
	 */
	public void resign() {
		if (!editors.isEmpty()) {
			for (Iterator iter = editors.iterator(); iter.hasNext();) {
				Editor element = (Editor) iter.next();
				element.destroy();
			}
		}
	}

	/**
	 * Returns a Vector containing the editor(s) added to this EditorPanel.
	 * @return Vector conatining the editors added to thi EditorPanel
	 */
	public Collection getEditor() {
		return editors;
	}

	/**
	 * Prints the labeltexts and contents of this EditorPanel's editors to the commandline.
	 * This method is for testing only.
	 */
	private void printContents() {
		if (!editors.isEmpty()) {
			for (Iterator iter = editors.iterator(); iter.hasNext();) {
				Editor element = (Editor) iter.next();
				Component parent =
					getParent().getParent().getParent().getParent();
				if (parent instanceof EditorWindow)
					System.out.println(((EditorWindow) parent).getTitle());
				System.out.println(
					((AbstractEditor) element).getEditingProfile().getLabel());
				System.out.println(
					((AbstractEditor) element).getEditObj().toString());
			}
		}
	}
	
	private void exitSystemIfUserProfile() {
		if (!editors.isEmpty()) {
			for (Iterator iter = editors.iterator(); iter.hasNext();) {
				Editor element = (Editor) iter.next();
				Component parent =
					getParent().getParent().getParent().getParent();
				if (parent instanceof EditorWindow)
					if(((EditorWindow) parent).getTitle().toString().equalsIgnoreCase("User Profile Editor")){
						System.exit(0);
					}
			}
		}
	}

}
