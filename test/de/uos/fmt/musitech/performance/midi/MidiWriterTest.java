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
package de.uos.fmt.musitech.performance.midi;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * This class implements JUnit test for the MidiWriter class.
 * 
 * @see de.uos.fmt.musitech.performance.midi.MidiWriter
 * @version $Revision: 7838 $, $Date: 2010-05-01 22:23:24 +0200 (Sat, 01 May 2010) $
 * @author Alexander Luedeke
 */
public class MidiWriterTest extends TestCase{

    /**
     * Write containers of a work object into a MIDI file for testing.
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {

        // Create the MidiReader object
        MidiReader midiReader = new MidiReader();

        // read the MIDI file into the piece object
        Piece piece = midiReader.getPiece(MidiWriterTest.class.getResource("import_1.mid"));

        // The container object to be written
        Container currentContainer = null;

        // Create the MidiWriter object
        MidiWriter midiWriter;
        midiWriter = new MidiWriter(MidiWriterTest.class.getResource("export.mid").getPath());
        midiWriter.setPiece(piece);
//        // Get the container pool
//        java.util.ArrayList containerPool = (java.util.ArrayList) piece.getContainerPool();
//
//        for (int i = 0; i < containerPool.size(); i++) {
//            if (containerPool.get(i) instanceof Container) {
//                currentContainer = (Container) containerPool.get(i);
//                if (currentContainer.size() != 0) {
//                    try {
//                        // Set a container object
//                        midiWriter.setContainer(currentContainer);
//                    } catch (Throwable exception) {
//                        System.err.println("Exception occurred in main() of MidiReader");
//                        exception.printStackTrace(System.out);
//                    }
//                }
//            }
            // Start the export
            midiWriter.write();
//        }

        // TODO
//        System.exit(0);

        // Read a Piece object from a MIDI file and write it back into another MIDI file

        // Create the MidiReader object
//        MidiReader aMidiReader = null;
//        try {
//            // Try the command line argument
//            aMidiReader = new MidiReader(new File(args[0]));
//        } catch (java.lang.ArrayIndexOutOfBoundsException iOOBoundsException) {
//            aMidiReader = new MidiReader(new File("import.mid"));
//        } // 
//        Piece work = new Piece(); //
//        work.setSampleData();
//        // Create a Piece object from the Midi file
//        if (aMidiReader != null) {
//            Piece work = aMidiReader.processMidiFile();
//            // Create the MidiWriter object
//            MidiWriter midiWriter = new MidiWriter(work);
//            try {
//                // Try the command line argument
//                midiWriter.setFileName(args[1]);
//            } catch (java.lang.ArrayIndexOutOfBoundsException iOOBoundsException) {
//                midiWriter.setFileName("de/uos/fmt/musitech/midi/export.mid");
//            }
//            // Start the export
//            midiWriter.write();
//        }
//        // ToDo
//        System.exit(0);

    }
    
    /**
     * This method creates a piece and writes it into a MIDI file 'test.mid'. After 
     * that the method reads the piece from the file and compares it with the original
     * piece.
     */
    public void testO2M2O(){
    	Piece piece = new Piece();
    	Context context = piece.getContext();
    	MidiNoteSequence msq = new MidiNoteSequence();
    	msq.add(new MidiNote(0,500000,80,65));
    	piece.getContainerPool().add(msq);
    	MidiWriter midiWriter = new MidiWriter(MidiWriterTest.class.getResource(".").getPath()+"test.mid");
    	midiWriter.setPiece(piece);
    	midiWriter.write();
    	MidiReader midiReader = new MidiReader();
    	Piece newPiece = midiReader.getPiece(MidiWriterTest.class.getResource("test.mid"));
    	assertTrue(ObjectCopy.equalValues(piece,newPiece));
    }
}