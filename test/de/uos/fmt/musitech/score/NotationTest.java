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
package de.uos.fmt.musitech.score;
	
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.TransportButtons;
import de.uos.fmt.musitech.score.gui.Accent;


public class NotationTest {
	
    String melody1 =  "c d e f g e 2c"; 

	
    public static void main(String[] args) {
    	Piece piece = Piece.getDefaultPiece();
    	Context context = piece.getContext();
        NotationSystem system = new NotationSystem(context);
        NotationStaff staff1 = new NotationStaff(system);
        staff1.setClefType('g');
        NotationVoice voice1 = new NotationVoice(staff1);
        NoteList notes = new NoteList(context, "c d e f g e 2c"); 
        
        int i = 0;
        for (Iterator iter = notes.iterator(); iter.hasNext();) {
			Note note = (Note) iter.next();
			i++;
			if(i % 2  == 0)
				note.getScoreNote().addAccent(Accent.DOWN_BOW);
			else
				note.getScoreNote().addAccent(Accent.UP_BOW);
			int pitch = note.getMidiPitch();
			long duration = note.getDuration();
			
		}

        voice1.addNotes(notes);

        ObjectPlayer player = ObjectPlayer.getInstance();        
        player.setContainer(MidiNoteSequence.convert(voice1));        
        TransportButtons trans = new TransportButtons(player.getPlayTimer());
        trans.setPlayOnly(true);

        try {
            Display display = EditorFactory.createDisplay(system);
            player.getPlayTimer().registerMetricForPush((NotationDisplay)display);
            JFrame frame = new JFrame("Notation Test");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add((JComponent)display);
            frame.getContentPane().add(trans,BorderLayout.NORTH);
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        
        printScoreNotes(voice1);
    }
    
    /**
     * Prints all ScoreNotes in a container.
     * @param cont The container. 
     */
    public static void printScoreNotes(Container cont) {
    	List list = new ArrayList();
    	cont.getContentsRecursiveList(list);
    	for(Object obj: list) {
    		if (obj instanceof Note) {
				Note note = (Note) obj;
				System.out.println("Score: "+note.getScoreNote());
				System.out.println("Performance: "+note.getPerformanceNote());
			}
    	}
    }
}
