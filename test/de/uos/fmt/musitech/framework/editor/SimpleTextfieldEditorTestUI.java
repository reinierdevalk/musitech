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
 * Created on 04.06.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * @author Kerstin Neubarth
 *
 */
public class SimpleTextfieldEditorTestUI {

	public static void emptyDisplayOption() {
		ScoreNote sn = new ScoreNote();
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(sn);
			//set emptyDisplayOption in SimpleTextField-children true
			if (editor instanceof AbstractComplexEditor) {
				Editor[] children =
					((AbstractComplexEditor) editor).getChildren();
				for (int i = 0; i < children.length; i++) {
					if (children[i] instanceof SimpleTextfieldEditor) {
						(
							(
								SimpleTextfieldEditor) children[i])
									.setEmptyDisplayOption(
							true);
					}
				}
			}
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow w = new EditorWindow("Test empty fields");
		w.addEditor(editor);
		w.show();
	}

	public static void emptyDisplay() {
		//inner class providing an Object with a null property
		final class ObjectWithEmptyProperty {
			//public fields for ReflectionAccess
			public String stringProperty = "Name";
			public Integer intProperty = null;
		}
		//create editObj
		ObjectWithEmptyProperty2 editObj = new ObjectWithEmptyProperty2();
		//create editor
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj);
			//set emptyDisplayOption in SimpleTextField-children true
			if (editor instanceof AbstractComplexEditor) {
				Editor[] children =
					((AbstractComplexEditor) editor).getChildren();
				for (int i = 0; i < children.length; i++) {
					if (children[i] instanceof SimpleTextfieldEditor) {
						(
							(
								SimpleTextfieldEditor) children[i])
									.setEmptyDisplayOption(
							true);
					}
				}
			}
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow w = new EditorWindow("Test empty fields");
		w.addEditor(editor);
		w.show();
	}

	public static class ObjectWithEmptyProperty2 {
		//		public String stringProperty = "Name";
		public String stringProperty = null;
		//		public Integer intProperty = null;
		public int intProperty = 5;
	}

	public static void textfieldEditorInFrame() {
		String editObj = "TestString";
		Editor editor1 = null;
		ScoreNote score = new ScoreNote();
		Editor editor2 = null;
		try {
			editor1 = EditorFactory.createEditor(editObj);
//			editor2 = EditorFactory.createEditor(score);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor1 != null) {
			JFrame frame = new JFrame("Test StringEditor in JFrame");
			frame.getContentPane().add((JComponent) editor1);
			frame.pack();
			frame.setVisible(true);
		}
		if (editor2 != null) {
			JFrame frame2 = new JFrame("Test ScoreNote in JFrame");
			frame2.getContentPane().add((JComponent) editor2);
			frame2.pack();
			frame2.setVisible(true);
		}
	}
	
	public static void sizeOfJTextfield(){
		JTextField field = new JTextField("TestString");
		if (field.getPreferredSize()==null)
			System.out.println("Preferred size of JTextField is null.");
		else
			System.out.println("Preferred size is: "+field.getPreferredSize().toString());
		JFrame frame1 = new JFrame("Frame1 with JTextField");
		frame1.getContentPane().add(field);
		frame1.pack();
		frame1.setVisible(true);
	}
	

	public static void main(String[] args) {
		//		emptyDisplayOption();
		//		emptyDisplay();
		textfieldEditorInFrame();
//		sizeOfJTextfield();
	}
}
