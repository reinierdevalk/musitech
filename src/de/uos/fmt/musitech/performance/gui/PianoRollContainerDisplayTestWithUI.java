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
 * Created on 26.05.2004
 *
 */
package de.uos.fmt.musitech.performance.gui;

import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Class for testing the PianoRollContainerDisplay as part of the
 * MUSITECH Editor-Framework.
 * <br>
 * As the GUI shall be tested, too, this class has a <code>main()</code> method.
 * 
 * @author Kerstin Neubarth
 *
 */
public class PianoRollContainerDisplayTestWithUI {

	
	/**
	 * Tests setting up a PianoRollContainerDisplay as a Display
	 * corresponding to the MUSITECH Editor-Framework.
	 * This method does not comprise yet generating a
	 * PianoRollContainerDisplay using the EditorFactory.
	 *
	 */
	public static void testPRCAsDisplay() {
		//provide editObj and EditingProfile
		MidiNoteSequence editObj = createMidiNoteSequence();
		EditingProfile profile = new EditingProfile();
		if (editObj==null){
			System.err.println("PianoRollContainerDisplayTestWithUI\n" +
				"editObj could not be created.");
			return;
		}
		//create display
		PianoRollContainerDisplay prcDisplay = new PianoRollContainerDisplay();
		prcDisplay.init(editObj, profile, prcDisplay); 
		//show display
		showDisplay(prcDisplay, "Test PRC as Display");
	}
	
	/**
	 * Tests creating a PianoRollContainerDisplay using the EditorFactory.
	 */
	public static void testCreatingWithEditorFactory(){
		//provide editObj and EditingProfile
		MidiNoteSequence editObj = createMidiNoteSequence();
		Display prcDisplay = null;
		try {
			prcDisplay = EditorFactory.createDisplay(editObj, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//show display
		showDisplay((JComponent)prcDisplay, "Test via EditorFactory");
	}

	public static void testPRCAsDataChangeListener() {
	}

	/**
	 * Private "helper" method to free the test methods from generating
	 * a MidiNoteSequence to be used as editObj of a PianoRollContainerDisplay.
	 * 
	 * @return MidiNoteSequence to be used as editObj of a PianoRollContainerDisplay
	 */
	private static MidiNoteSequence createMidiNoteSequence() {
		MidiNoteSequence ns = new MidiNoteSequence();
		ns.addNote(new MidiNote(0, 50, 30, 51));
		ns.addNote(new MidiNote(62, 50, 40, 60));
		ns.addNote(new MidiNote(182, 280, 60, 70));
		ns.addNote(new MidiNote(1000, 380, 10, 53));
		return ns;
	}
	
	/**
	 * Shows the specified JComponent in a JFrame.
	 * The specified String is displayed as the JFrame's label.
	 * It is used for distinguishing the different test frames.
	 * 
	 * @param display JComponent to be shown in a JFrame
	 * @param title String displayed on top of the JFrame
	 */
	private static void showDisplay(JComponent display, String title){
		JFrame frame = new JFrame(title);
		frame.getContentPane().add(display);
		frame.setSize(500,500);
		frame.setVisible(true);
	}
	
	/**
	 * Tests creating a PianoRollContainerDisplay for a Container.
	 */
	public static void testWithContainer(){
	    Context context = new Context();
	    NoteList notes = new NoteList(context, "c e g");
	    Container voice1 = new BasicContainer(context);
	    voice1.addAll(notes);
	    Container voice2 = new BasicContainer(context);
	    voice2.add(notes);
	    try {
            Display display1 = EditorFactory.createDisplay(voice1, null, "PianoRoll");
            JFrame frame1 = new JFrame("Container with notes");
            frame1.getContentPane().add((JComponent)display1);
            frame1.pack();
            frame1.setVisible(true);
            Display display2 = EditorFactory.createDisplay(voice2, null, "PianoRoll");
            JFrame frame2 = new JFrame("Container with NoteList");
            frame2.getContentPane().add((JComponent)display2);
            frame2.pack();
            frame2.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
	    
	}

	/**
	 * Method for running the tests.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		testPRCAsDisplay();
		testCreatingWithEditorFactory();
	    testWithContainer();
	}
}
