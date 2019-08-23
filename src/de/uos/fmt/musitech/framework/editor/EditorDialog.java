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
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;


/**
 * This class provides a window (a JFrame) for displaying editors
 * on a <code>EditorPanel</code>.
 * <br>
 * <br> As implementing the ActionListener interface it sets itself as a
 * ActionListener to the <code>EditorPanel</code>.
 * <br>
 * <br> The method <code>addEditor(AbstractEditor)</code> provides a useful
 * way to add an editor to the <code>EditorPanel</code> of this <code>EditorWindow</code>.
 * 
 * @author Tobias Widdra
 */

/**
 * Simply put into a JDialog by
 * @author Wolfram Heyer
 */

/**
 * Constructs a new EditorDialog with the given string as titel.
 * <br>
 * <br>A new EditorPanel is created and the <code>setEditorPanel</code> method
 * is invoked (setting this instance of EditorWindow as ActionListener). 
 * @param titel
 */
public class EditorDialog extends JDialog implements ActionListener {

	/**
	 * The EditorPanel where the editors are displayed.
	 */
	EditorPanel editorPanel = new EditorPanel();

	public EditorDialog(String title) {
		setTitle(title);

		setModal(true);
	
		
//		pack();
//		setVisible(true);
		setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
	}

			/**
		 * Adds the given editor to the <code>editorPanel</code>.
		 * <br>
		 * <br> <code>pack()</code> is invoked afterwards.
		 * @param c
		 */
		public void addEditor(Editor c) {
			editorPanel.addEditor(c);
			pack();
		}

		/**
		 * Sets the <code>editorPanel</code> to the given <code>EditorPanel</code>.
		 * <br>
		 * <br><code>panel.addActionListener(this);</code> is then invoked.
		 * @param panel
		 */
		public void setEditorPanel(EditorPanel panel) {
			editorPanel = panel;
			panel.addActionListener(this);
			getContentPane().add(editorPanel);
		}
	
		/**
		 * Returns <code>editorPanel</code>.
		 * @return
		 */
		public EditorPanel getEditorPanel() {
			return editorPanel;
		}
	
		/**
		 * Disposes the JDialog
		 */
		public void actionPerformed(ActionEvent e) {
//			System.exit(0);
			//hide();
			dispose();
		}
}
