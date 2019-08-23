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
 * Created on 18.01.2005
 *
 */
package de.uos.fmt.musitech.framework.time;

import javax.swing.JFrame;

import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.utility.math.Rational;


/**
 * @author Kerstin Neubarth
 *
 */
public class PlayerWithUI {
    
    public static void testMeterChange(){
        //create context and change meter in metrical time line
        Piece piece = new Piece();
        Context context = new Context(piece);
        MetricalTimeLine mtl = piece.getMetricalTimeLine();
        TimeSignatureMarker marker1 = new TimeSignatureMarker(2,4, Rational.ZERO);
        mtl.add(marker1);
        TimeSignatureMarker marker2 = new TimeSignatureMarker(3,4, new Rational(4,4));
        mtl.add(marker2);
        //create midi example
        NoteList notes = new NoteList(context, "4c d e f 2g 4e 2c 4r");
        MidiNoteSequence midi = MidiNoteSequence.convert(notes);
        ObjectPlayer player = ObjectPlayer.getInstance();
        player.setContainer(midi);
        player.getPlayTimer().setContext(context);
        //show TransportButtons
        TransportButtons buttons = new TransportButtons(player.getPlayTimer());
        JFrame frame = new JFrame("Test player with meter change");
        frame.getContentPane().add(buttons);
        frame.pack();
        frame.setVisible(true);
    }
    
    private static void forExit(){
        JFrame frame = new JFrame("For exit");
        frame.setSize(200,0);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    

    public static void main(String[] args) {
        forExit();
        testMeterChange();
    }
}
