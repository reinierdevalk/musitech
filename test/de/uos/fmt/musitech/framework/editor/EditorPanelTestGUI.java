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
 * Created on 09.07.2004
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Class for testing the GUI of an IntMouseEditor.
 * 
 * @author Kerstin Neubarth
 */
public class EditorPanelTestGUI {

	/**
	 * Tests an IntMouseEditor with an Editor that is not read-only.
	 * <br>
	 * The IntMouseEditor shows three buttons "OK", "Cancel" and "Apply".
	 */
	public static void activatedPanel() {
		//		Editor editor = null;
		try {
			final Editor editor =
				EditorFactory.createEditor(new PerformanceNote());
			EditorWindow window = new EditorWindow("Test editing");
			window.addEditor(editor);
			window.pack();
			window.setVisible(true);
			window.addWindowListener(new WindowAdapter() {
				@Override
				public void windowDeactivated(WindowEvent we) {
					System.out.println(editor.getEditObj().toString());
				}
			});
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests an IntMouseEditor with an Editor that is read-only.
	 * <br>
	 * The IntMouseEditor has only one button "OK".
	 */
	public static void deactivatedPanel() {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(new Integer(5));
			if (!editor.getEditingProfile().isReadOnly())
				editor.getEditingProfile().setReadOnly(true);
			EditorWindow window = new EditorWindow("Test read only");
			window.addEditor(editor);
			window.pack();
			window.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests an IntMouseEditor with two Editors that are read-only.
	 * <br>
	 * The IntMouseEditor has only one button "OK".
	 */
	public static void twoReadOnlyEditors() {
		Editor editor1 = null;
		Editor editor2 = null;
		try {
			editor1 = EditorFactory.createEditor(new Integer(5));
			editor2 = EditorFactory.createEditor(new Integer(15));
			EditorWindow window = new EditorWindow("Test 2 editors read-only");
			window.addEditor(editor1);
			window.addEditor(editor2);
			window.pack();
			window.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Tests an IntMouseEditor with two Editors. One of the Editors is read-only,
	 * the other is not.
	 * <br>
	 * Here, the read-only Editor is added to the IntMouseEditor first.
	 * <br>
	 * The IntMouseEditor shows three buttons "OK", "Cancel" and "Apply".
	 */
	public static void mixedEditors1() {
		Editor editor1 = null;
		Editor editor2 = null;
		try {
			editor1 = EditorFactory.createEditor(new Integer(25));
			editor2 = EditorFactory.createEditor(new PerformanceNote());
			EditorWindow window = new EditorWindow("Test mixed editors 1");
			window.addEditor(editor1);
			window.addEditor(editor2);
			window.pack();
			window.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests an IntMouseEditor with two Editors. One of the Editors is read-only,
	 * the other is not.
	 * <br>
	 * Here, the Editor which is not read-only is added to the IntMouseEditor first.
	 * <br>
	 * The IntMouseEditor shows three buttons "OK", "Cancel" and "Apply".
	 */
	public static void mixedEditors2() {
		Editor editor1 = null;
		Editor editor2 = null;
		try {
			editor1 = EditorFactory.createEditor(new Integer(25));
			editor2 = EditorFactory.createEditor(new PerformanceNote());
			EditorWindow window = new EditorWindow("Test mixed editors 2");
			window.addEditor(editor2);
			window.addEditor(editor1);
			window.pack();
			window.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests an IntMouseEditor with two complex editors. One of the Editors is read-only,
	 * the other is not.
	 * <br>
	 * The read-only Editor does not apply any changes to its editObj.
	 */
	public static void mixedEditors3() {
//		Editor editor1 = null;
		Editor editor2 = null;
		try {
//			EditingProfile profile = EditorFactory.createDefaultProfile(new PerformanceNote());
//			profile.setReadOnly(true);
//			final Editor editor1 = EditorFactory.createEditor(new PerformanceNote(), profile);
			final Editor editor1 = EditorFactory.createEditor(new PerformanceNote());
			editor1.getEditingProfile().setReadOnly(true);
			editor1.updateDisplay();
			editor2 = EditorFactory.createEditor(new ScoreNote());
			EditorWindow window = new EditorWindow("2 complex editors mixed");
			window.addEditor(editor1);
			window.addEditor(editor2);
			window.pack();
			window.setVisible(true);
			window.addWindowListener(new WindowAdapter(){
				@Override
				public void windowIconified(WindowEvent we){
					System.out.println(editor1.getEditObj().toString());
				}
			});
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * For running the test methods. 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		activatedPanel();
		deactivatedPanel();
		twoReadOnlyEditors();
		mixedEditors1();
		mixedEditors2();
		mixedEditors3();
	}
}
