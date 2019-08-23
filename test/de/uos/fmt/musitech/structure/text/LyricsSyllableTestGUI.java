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
 * Created on 29.06.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uos.fmt.musitech.structure.text;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorWindow;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Class for testing the GUI of a LyricsSyllableDisplay.
 * 
 * @author Kerstin Neubarth
 */
public class LyricsSyllableTestGUI {
	
	/**
	 * Tests marking and unmarking a displayed LyricsSyllable as selected object.
	 */
	public static void testMarkSelected(){
		LyricsSyllable editObj = new LyricsSyllable();
		editObj.setText("Syl-");
		LyricsSyllableDisplay display = null;
		try {
			display = (LyricsSyllableDisplay)EditorFactory.createDisplay(editObj, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (display!=null){
			final LyricsSyllableDisplay lsd = display;
			JButton selectButton = new JButton("Select");
			selectButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					lsd.markSelected(true);
				}
			});
			JButton unselectButton = new JButton("Reset");
			unselectButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					lsd.markSelected(false);
				}
			});
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(selectButton);
			buttonBox.add(unselectButton);
			Box box = Box.createVerticalBox();
			box.add(display);
			box.add(buttonBox);
			JFrame frame = new JFrame("Test mark selection");
			frame.getContentPane().add(box);
			frame.pack();
			frame.setVisible(true);
		}
	}
	
	/**
	 * Tests a LyricsSyllableDisplay acting as a DataChangeListener.
	 * <br>
	 * Therefore an additional Editor is created for the syllable shown in the
	 * LyricsSyllableDisplay. This Editor allows changing the syllable. The Display
	 * then updates to the changed syllable when it gains the focus.
	 */
	public static void testAsDataChangeListener(){
		//create shared editObj
		LyricsSyllable editObj = new LyricsSyllable(0, "Syl");
		//create and show Display
		Display display = null;
		try {
			display = EditorFactory.createDisplay(editObj, null);
			JFrame frame = new JFrame("Display");
			frame.getContentPane().add((JComponent)display);
			frame.pack();
			frame.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//create Editor
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj, null, "Panel");
			EditorWindow window = new EditorWindow("Editor");
			window.addEditor(editor);
			window.pack();
			window.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		testMarkSelected();
		testAsDataChangeListener();
	}
}
