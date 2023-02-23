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
 * Created on 17.08.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * @author Kerstin Neubarth
 *
 */
public class PopUpDisplay extends AbstractComplexDisplay implements Wrapper {

	Display displayToPopUp;

	/** 
	 * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#createGUI()
	 */
	@Override
	public void createGUI() {
		JButton displayButton = new JButton("Display...");
		displayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showDisplayToPopUp();
			}
		});
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(displayButton);
	}

	/**
	 * Creates the <code>displayToPopUp</code> (if null before) and shows it in a JFrame.
	 */
	protected void showDisplayToPopUp() {
		//get object to display
		Object objToDisplay = getObjectForDisplay();
		if (objToDisplay!=null){
			//create displayToPopUp
		    EditingProfile displayProfile = EditorFactory.getOrCreateProfile(objToDisplay);
		    if (profile!=null && profile.getPropertyName()!=null)
		        displayProfile.setPropertyName(profile.getPropertyName());
		    profile.setReadOnly(true);
			if (displayToPopUp==null){
				try {
					displayToPopUp = EditorFactory.createDisplay(objToDisplay, displayProfile);
				} catch (EditorConstructionException e) {
					e.printStackTrace();
				}
			}
		}
		//show displayToPopUp
		if (displayToPopUp!=null){
			String label;
			if (profile.label!=null)
				label = profile.getLabel();
			else
				label = EditorFactory.createDefaultProfile(objToDisplay).getLabel();
			char[] titleChars = label.toCharArray();
			StringBuffer title = new StringBuffer();
			title.append(Character.toUpperCase(titleChars[0]));
			title.append(label.substring(1));
			JFrame frame = new JFrame(title.toString());
			frame.getContentPane().add((JComponent)displayToPopUp);
			frame.pack();
			frame.setVisible(true);
		}
	}

	/**
	 * Returns the object to be edited in the <code>editorToPopUp</code>.
	 * This object is <code>propertyValue</code> if <code>propertyName</code>
	 * is not null, or <code>editObj</code> otherwise.
	 * 
	 * @return Object to be edited in the <code>editorToPopUp</code>
	 */
	private Object getObjectForDisplay() {
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
	 * @return
	 */
	public Display getDisplayToPopUp() {
		return displayToPopUp;
	}

	/**
	 * @param display
	 */
	@Override
	public void setWrappedView(Display display) {
		displayToPopUp = display;
		this.editObj = display.getEditObj();
	}
	
	/** 
	 * Overwrites method <code>updateDisplay()</code> of class AbstractDisplay.
	 * As the PopUpDisplay does only show the button "Display", its GUI
	 * need not to be rebuilt.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
	 */
	@Override
	public void updateDisplay(){
	    //as the PopUpDisplay does only show a button
	    //its GUI need not be updated when data changes
	    //(updating the GUI of the displayToPopUp is
	    //controlled by the that Display itself)
	}

}
