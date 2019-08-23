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

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Class for testing PopUpEditors with user interaction.
 * 
 * @author Kerstin Neubarth
 *
 */
public class PopUpEditorTestUI {

	/**
	 * For testing the interaction between a PopUpEditor and its <code>editorToPopUp</code>.
	 * <br>
	 * If the "Edit"-button is activated, the <code>editorToPopUp</code> should be shown.
	 * If two editors are opened via the "Edit"-button and values are changed in one
	 * of them, the other should automatically update when it gets the focus.
	 */
	public static void editorToPopUp() {
		ScoreNote sn = new ScoreNote();
		EditingProfile profile = sn.getEditingProfile();
		profile.setEditortype("PopUp");
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(sn, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		showEditor(editor, "Test editorToPopUp");
	}

	public static void popUpEditorWithPrompt() {
		ScoreNote sn = new ScoreNote();
		EditingProfile profile = sn.getEditingProfile();
		profile.setEditortype("PopUp");
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(sn, profile);
			editor.setPromptUpdate(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		showEditor(editor, "Test editorToPopUp with prompt");
	}

	public static void popUpEditorWithPrompt2() {
		//shared editObj
		ScoreNote sn = new ScoreNote();
		//create and show PopUpEditor
		EditingProfile profile = sn.getEditingProfile();
		profile.setEditortype("PopUp");
		Editor popUpEditor = null;
		try {
			popUpEditor = EditorFactory.createEditor(sn, profile);
			popUpEditor.setPromptUpdate(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		showEditor(popUpEditor, "PopUpEditor with prompt");
		//create and show PanelEditor
		Editor panelEditor = null;
		try {
			panelEditor = EditorFactory.createEditor(sn);
			panelEditor.setPromptUpdate(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		showEditor(panelEditor, "PanelEditor with prompt");
	}
	
	public static void updatingGUI(){
		Note note = new Note(null, new PerformanceNote());
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(note);
			showEditor(editor, "Test update GUI");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the specified Editor in a EditorWindow titled by the specified String.
	 * 
	 * @param editor Editor to be shown
	 * @param title String to appear as title of the EditorWindow
	 */
	private static void showEditor(Editor editor, String title) {
		EditorWindow w = new EditorWindow(title);
		w.addEditor(editor);
		w.show();
	}

	/**
	 * Runs the test methods.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		editorToPopUp();
//		popUpEditorWithPrompt();
//		popUpEditorWithPrompt2();
		updatingGUI();
	}
}