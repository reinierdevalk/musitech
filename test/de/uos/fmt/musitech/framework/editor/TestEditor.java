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

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFrame;

import de.uos.fmt.musitech.data.media.image.ImageURL;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * A JUnit test for editors.
 * <br>
 * <br> The tests do not use the <code>EditorFactory</code>. So it meight be
 * better to either use <code>EditorFactory</code> and write a test for 
 * the specific editor or to write a new test using the factory in this class.
 * <br>
 * <br> The main method can be used to play around with the windows.
 * 
 * @author tobias widdra
 */
public class TestEditor {

	static ScoreNote scoreNote =
		new ScoreNote(
			new Rational(1, 2),
			new Rational(1, 2),
			'c',
			(byte) 0,
			(byte) 0);
	static EditorWindow w = new EditorWindow("Editor");

	public static void editorWindow() {
		EditorWindow w = new EditorWindow("Test");
		w.setSize(300, 200);
		w.show();
	}

	public static void editorWindow(AbstractEditor content, int x, int y) {
		EditorWindow w = new EditorWindow("Test");
		w.addEditor(content);
		w.setSize(x, y);
		w.show();
	}

	public static void testByteEditor() {
		//		ByteEditor editor = new ByteEditor();
		//		editor.construct(scoreNote, "Accidental", "Accidental");
		Editor editor = null;
		EditingProfile prof =
			new EditingProfile("Accidental", "byte", "Accidental");
		try {
			editor = EditorFactory.createEditor(scoreNote, prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		w.addEditor(editor);
		w.show();
	}

	public static void testCharEditor() {
		//		CharEditor editor = new CharEditor();
		//		editor.construct(scoreNote, "Diatonic", "Diatonic");
		Editor editor = null;
		EditingProfile prof =
			new EditingProfile("Diatonic", "char", "Diatonic");
		try {
			editor = EditorFactory.createEditor(scoreNote, prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (editor instanceof CharEditor)
			 ((CharEditor) editor).setAllowedCharacters("abcdefg");
		w.addEditor(editor);
		w.show();
	}

	public static void testTreeEditor() {
		//		TreeEditor editor = new TreeEditor();
		//		editor.construct(scoreNote,null,new AbstractEditor[0],"Score Note");

		Editor editor = null;
		EditingProfile prof = new EditingProfile(null, "Tree", "ScoreNote");
		try {
			editor = EditorFactory.createEditor(scoreNote, prof);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		w.addEditor(editor);
		w.show();
	}

	public static void testImageEditor() {
		//		TreeEditor editor = new TreeEditor();
		//		editor.construct(scoreNote,null,new AbstractEditor[0],"Score Note");
		ImageURL imgUrl = new ImageURL();
		try {
			imgUrl.setSourceUrl(new File("c:/data/jsbach.jpg").toURL());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Editor editor = null;
		//		EditingProfile prof = new EditingProfile(null, "Tree", "ScoreNote");
		try {
			editor = EditorFactory.createEditor(imgUrl);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		w.addEditor(editor);
		w.show();
	}

	public static void testNoteEditor() {
		//		TreeEditor editor = new TreeEditor();
		//		editor.construct(scoreNote,null,new AbstractEditor[0],"Score Note");
		Piece work = new Piece();
		NoteList nl = new NoteList(work.getContext(),"4'cdef|gg");
		Editor editor = null;
		//		EditingProfile prof = new EditingProfile(null, "Tree", "ScoreNote");
		try {
			editor = EditorFactory.createEditor(nl);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		w.addEditor(editor);
		w.show();
	}


	public static void testCollectionEditorReadOnly() {
		Collection editObj = new Vector();
		for (int i = 0; i < 5; i++) {
			editObj.add(new String("Element " + (i + 1)));
		}
		Editor editor1 = null;
		try {
			editor1 = EditorFactory.createEditor(editObj);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow window1 = new EditorWindow("CollectionEditor editable");
		window1.addEditor(editor1);
		window1.show();
		Editor editor2 = null;
		EditingProfile profile = new EditingProfile();
		profile.setReadOnly(true);
		try {
			editor2 = EditorFactory.createEditor(editObj, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow window2 = new EditorWindow("CollectionEditor read only");
		window2.addEditor(editor2);
		window2.show();
	}

	/**
	 * This window invokes <code>System.exit(0)</code> when closed.
	 * <br> It is used since the <code>EditorWindows</code> do not exit the virtual machine.
	 * @author tobi
	 */
	static class ExitWindow extends javax.swing.JFrame {
		ExitWindow() {
			super("I exist for you to exit the program :o)");
			setSize(350, 0);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

	public static void main(String argv[]) {
		new ExitWindow().show();
		//testImageEditor();
		//		testStringEditor();
		//		testEditorWindow();
		//		testDefaultComplexEditor();
		//		testEditorWindow(new DefaultComplexEditor(new Note().getEditingProfile()),
		//						250,400);
		//		testEditorWindow(new DefaultComplexEditor(
		//			new EditingProfile("complex",new EditingProfile[] {
		//				new Note().getEditingProfile(),
		//				new EditingProfile("Test") })),
		//			250,400);
		//		testByteEditor();
		//		testCharEditor();
		//		testTreeEditor();
		testNoteEditor();
		testCollectionEditorReadOnly();
	}

}
