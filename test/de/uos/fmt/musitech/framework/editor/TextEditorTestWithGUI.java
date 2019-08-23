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
 * Created on 17.05.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * Class containing methods for testing TextEditor.
 * 
 * @author Kerstin Neubarth
 *
 */
public class TextEditorTestWithGUI {

	/**
	 * Tests setting up a TextEditor and shows the viewer in an EditorWindow.
	 */
	public static void testCreateTextViewer() {
		String text =
			"This is a text being too long to be displayed in a simple JTextField. Instead, the viewer is to use a JTextArea and to wrap lines.";
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(text, null, "Text");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor != null) {
			EditorWindow w = new EditorWindow("TextEditor");
			w.addEditor(editor);
			w.show();
		} else
			JOptionPane.showMessageDialog(null, "Editor has not been created.");
	}

	/**
	 * Tests setting the size fo a TextEditor.
	 */
	public static void testChangingSize() {
		String text =
			"This is a text being too long to be displayed in a simple JTextField. Instead, the viewer is to use a JTextArea and to wrap lines.";
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(text, null, "Text");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor != null && editor instanceof TextEditor) {
			((TextEditor) editor).setEditorSize(new Dimension(500, 400));
		}
		if (editor != null) {
			EditorWindow w = new EditorWindow("Test changing size");
			w.addEditor(editor);
			w.show();
		} else
			JOptionPane.showMessageDialog(null, "Editor has not been created.");
	}

	public static void testEditing() {
		final Object editObj = new ObjectWithTextProperty();
		final EditingProfile profile = new EditingProfile("text");
		profile.setEditortype("Text");
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor != null) {
			EditorWindow w = new EditorWindow("TextEditor for property");
			w.addWindowListener(new WindowAdapter(){
				public void windowIconified(WindowEvent e){
					printPropertyValueForTesting(editObj, profile.getPropertyName());
				}
			});
//			w.addW
			w.addEditor(editor);
			w.show();
		}
	}
	
	public static class ObjectWithTextProperty{
		public String text = "This is a text being too long to be displayed in a simple JTextField. Instead, the viewer is to use a JTextArea and to wrap lines."
			+ "The text can be edited.";
		public int intProperty = 5;
	}
	
	protected static void printPropertyValueForTesting(Object obj, String propertyName){
		ReflectionAccess ref = ReflectionAccess.accessForClass(ObjectWithTextProperty.class);
		if (ref!=null && ref.hasPropertyName(propertyName)){
			Object propertyValue = ref.getProperty(obj, propertyName);
			if (propertyValue!=null)
				System.out.println("PropertyValue of ObjectWithTextProperty: "+propertyValue);
			else
				System.out.println("PropertyValue of ObjectWithTextProperty is null");
		}
	}
	
	/**
	 * main method for running the test methods.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		testCreateTextViewer();
//		testChangingSize();
		testEditing();
	}
}
