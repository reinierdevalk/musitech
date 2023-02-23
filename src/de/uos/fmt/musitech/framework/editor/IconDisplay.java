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
 * Created on 16.09.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * An IconDisplay is a PopUpDisplay which show an icon on its button.
 * If the button is activated the wrapped view is opened in a separate frame.
 * 
 * @author Kerstin Neubarth
 *
 */
public class IconDisplay extends PopUpDisplay {
    /** 
	 * Overwrites method <code>createGUI()</code> of class <code>PopUpDisplay</code>
	 * to display the <code>displayButton</code> showing an icon.
	 * If the EditingProfile does not specify an icon, the button is labelled "Display...".
	 *  
	 * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
	 */
	@Override
	public void createGUI() {
		displayIconButton();
	}

	/**
	 * Displays the <code>editButton</code> with an Icon given in the editor's 
	 * <code>profile</code> and recorded in <code>editIcon</code>.
	 * If there is no Icon provided by the EditingProfile, that is, if <code>editIcon</code>
	 * remains null, the <code>editButton</code> is labelled "Edit". In either case,
	 * activating the button results in popping up the editor, that is opening the
	 * <code>editorToPopUp</code>.
	 */
	private void displayIconButton() {
	    Icon icon = null;
		if (profile != null && profile.getDefaultIcon() != null)
			icon = profile.getDefaultIcon();
		if (icon == null) {
			super.createGUI(); //displays "Display..."-button
			return;
		}
		JButton displayButton = new JButton(icon);
		displayButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				IconDisplay.this.showDisplayToPopUp();
			}
		});
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(displayButton);
	}

}
