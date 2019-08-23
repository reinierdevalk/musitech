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
 */
package de.uos.fmt.musitech.framework.editor;

import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de
	.uos
	.fmt
	.musitech
	.framework
	.editor
	.EditorFactory
	.EditorConstructionException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class for testing PopUpDisplay.
 * 
 * @author Kerstin Neubarth
 *
 */
public class PopUpDisplayTestGUI {

	/**
	 * Returns a NoteList which can be used as editObj.
	 * 
	 * @return NoteList
	 */
	private static NoteList createNoteList() {
		NoteList notes = new NoteList();
		notes.add(new ScorePitch(60), new Rational(0, 4), new Rational(1, 4));
		notes.add(new ScorePitch(62), new Rational(1, 4), new Rational(1, 4));
		notes.add(new ScorePitch(64), new Rational(2, 4), new Rational(1, 4));
		return notes;
	}

	/**
	 * Returns a NotationDisplay which can be taken as the Display to be wrapped
	 * by a PopUpDisplay.
	 * 
	 * @return Display displaying a NoteList
	 */
	private static Display createNotationDisplay() {
		Display display = null;
		try {
			display = EditorFactory.createDisplay(createNoteList(), null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		return display;
	}

	/**
	 * Shows the specified Display in a JFrame titled by the specified String.
	 * 
	 * @param display Display to be shown in a JFrame
	 * @param frameTitle String to appear on top of the JFrame
	 */
	private static void showDisplay(Display display, String frameTitle) {
		JFrame frame = new JFrame(frameTitle);
		frame.getContentPane().add((JComponent) display);
//		frame.pack();
		frame.setSize(300,100);
		frame.setVisible(true);
	}

	/**
	 * Tests displaying a NoteList in a PopUpDisplay.
	 * The Display is created giving the EditorFactory the type name "PopUp".
	 */
	public static void testPopUpDisplay() {
		//create NoteList as editObj
		NoteList editObj = createNoteList();
		//create Display for editObj of type PopUp
		Display display = null;
		try {
			display = EditorFactory.createDisplay(editObj, null, "PopUp", null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (display!=null)
			showDisplay(display, "Test PopUpDisplay for editObj");
	}

	/**
	 * Tests wrapping a NotationDisplay in a PopUpDisplay.
	 * The PopUpWrapper is asked from the EditorFactory.
	 */
	public static void testPopUpWrapper() {
		//create display to wrap
		Display displayToWrap = createNotationDisplay();
		//create PopUpWrapper for Display
		Display popUpWrapper = null;
		popUpWrapper = EditorFactory.createPopUpWrapper(displayToWrap);		
		//show display wrapper
		if (popUpWrapper != null)
			showDisplay(popUpWrapper, "Test PopUpWrapper");
	}
	
	/**
	 * Tests wrapping an Editor run as Display.
	 */
	public static void testWrapperForReadOnlyEditor(){
		//create read only-Editor
		PerformanceNote perf = new PerformanceNote();
		EditingProfile profile = perf.getEditingProfile();
		profile.setReadOnly(true);
		Display editor = null;
		try {
			editor = EditorFactory.createDisplay(perf, profile);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//create wrapper
		Display wrapper = null;
		if (editor!=null){
			wrapper = EditorFactory.createPopUpWrapper(editor);
		}
		//show wrapper
		if (wrapper!=null)
			showDisplay(wrapper, "Wrap read only-Editor");
	}
	
	/**
	 * Shows a JFrame for closing all running frames.
	 */
	private static void provideClosingFrame(){
		JFrame frame = new JFrame("For closing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Method for running the test methods.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		provideClosingFrame();
		testPopUpDisplay();
		testPopUpWrapper();
		testWrapperForReadOnlyEditor();
	}
}
