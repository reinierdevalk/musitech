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
 * Created on 27.04.2005
 */
package de.uos.fmt.musitech.performance.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.TransportButtons;

/**
 * @author Jan
 *  
 */
public class TestPianoRoll {

    public static void main(String[] args) {

        //        MidiNote perfNote = new MidiNote(100000);
        //        MidiNoteSequence MNS = new MidiNoteSequence();
        //        MNS.add(perfNote);
        //        
        //        Editor editor = null;
        //		try {
        //			editor = EditorFactory.createEditor(MNS, null, "PianoRoll");
        //			String[] editors = EditorFactory.getDisplayTypeNames(MNS, null);
        //			for (int i = 0; i < editors.length; i++) {
        //                System.out.println(editors[i]);
        //            }
        //		} catch (EditorConstructionException e) {
        //			System.err.println(e.getMessage());
        //		}
        //		EditorWindow w = new EditorWindow("Note Editor");
        //		w.addEditor(editor);
        //		w.getEditorPanel().addActionListener(new ActionListener() {
        //			public void actionPerformed(ActionEvent arg0) {
        //// System.out.println("note: " + perfNote);
        //			}
        //		});
        //		w.show();
        //        

        PianoRollEditor pianoRoll = new PianoRollEditor();
        ObjectPlayer player = ObjectPlayer.getInstance();
        TransportButtons transBut = new TransportButtons();
        transBut.setPlayTimer(player.getPlayTimer());

        Container cont = new BasicContainer();
        cont.add(new Note(new PerformanceNote(100000)));
        pianoRoll.setContainer(cont);
        player.setContainer(cont);

        //        pianoRoll.setShowTools(true);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pianoRoll, BorderLayout.CENTER);
        frame.getContentPane().add(transBut, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.show();
    }

}