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


import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;

/**
 * In order to import MIDI1 and MIDIPlus the velocity of MIDI Note_On 
 * events has to be analysed. After that the notes diatonic, accidental, 
 * velocity will be changed if necessary.
 * 
 * @see www.musedata.org
 * @author Alexander Luedeke
 */
public class MidiVelocityAnalyser {
    
   // The range of the MIDI velocity is [0-127], so we need 7 bits.
   final private int MAX = 7;
   
   // The array for the binary representation of the decimal velocity
   private int[] binary = new int[MAX];
   
   // The position in the binary representation of the decimal velocity
   private int position;
   
   // The note to process
   private PerformanceNote midiNote = null;
   private ScoreNote scoreNote = null;
   
   // The diatonics for pitch spelling 2 (x,x,x,x,x,1,0)
   private final char[] keyNames = {'C', 'C', 'D', 'E', 'E', 'F', 'F', 'G',
           'G', 'A', 'B', 'B'};
   
   // The diatonics for pitch spelling 1, flat (x,x,x,x,x,0,1)
   private final char[] keyNamesFlat = {'D', 'D', 'E', 'F', 'F', 'G', 'G', 'A',
           'A', 'B', 'C', 'C'};

   // The diatonics for pitch spelling 3, sharp (x,x,x,x,x,1,1)
   private final char[] keyNamesSharp = {'B', 'B', 'C', 'D', 'D', 'E', 'E', 'F',
           'F', 'G', 'A', 'A'};
   
   // The accidental for pitch spelling 2 (x,x,x,x,x,1,0)
   private final byte[] accidental = {0, 1, 0, -1, 0, 0, 1, 0, 1, 0, -1, 0};

   // The accidental for pitch spelling 1, flat (x,x,x,x,x,0,1)
   private final byte[] accidentalFlat = {-2, -1, -2, -2, -1, -2, -1, -2, -1, -2, -2, -1};

   // The accidental for pitch spelling 3, sharp (x,x,x,x,x,1,1)
   private final byte[] accidentalSharp = {1, 2, 2, 1, 2, 1, 2, 2, 3, 2, 1, 2};

   /**
    * In order to get MIDI1 input informations this method will analyse the MIDI velocity 
    * of the given note. After that the note's diatonic, accidental, velocity will be changed 
    * if necessary.
    * @param note the note to process
	* @throws WrongArgumentException
    */
   public void processVelocity(Note note) throws WrongArgumentException {
       scoreNote = note.getScoreNote();
       midiNote = note.getPerformanceNote();
       
       if (scoreNote == null || midiNote == null) {
           throw new WrongArgumentException(
                   "Error: MidiVelocityAnalyser.analyseVelocity(): there is no score or midi note." 
                   + getClass());
       }
       
       // Write the given decimal MIDI velocity into the array 'binary[]'
       this.createBinaryRepresentation(midiNote.getVelocity());
       
       // Get the pitch spelling
       int pitchSpelling = pitchSpelling();
       // Get the MIDI pitch (key no.)
       int midiPitch = midiNote.getPitch();
       
       // Ignore the octave
       int midPitchMod = midiPitch % 12;
       
       if (pitchSpelling == 1) {
           // Set the new diatonic
           scoreNote.setDiatonic(keyNamesFlat[midPitchMod]);
           // Set the new accidental
           scoreNote.setAlteration(accidentalFlat[midPitchMod]);
           // Set the MIDI velocity, means eliminate the pitch spelling
           midiNote.setVelocity(midiNote.getVelocity() - 1);
       } else if (pitchSpelling == 2) {
           // Set the new diatonic
           scoreNote.setDiatonic(keyNames[midPitchMod]);
           // Set the new accidental
           scoreNote.setAlteration(accidental[midPitchMod]);
           // Set the MIDI velocity, means eliminate the pitch spelling
           midiNote.setVelocity(midiNote.getVelocity() - 2);
       } else if (pitchSpelling == 3) {
           // Set the new diatonic
           scoreNote.setDiatonic(keyNamesSharp[midPitchMod]);
           // Set the new accidental
           scoreNote.setAlteration(accidentalSharp[midPitchMod]);
           // Set the MIDI velocity, means eliminate the pitch spelling
           midiNote.setVelocity(midiNote.getVelocity() - 3);
       }
       // pitchSpelling == 0, means spelling unknown, nothing to do
   }

   /**
    * This method writes the given decimal velocity-value into a array 'binary[]'.
    * @param decimal the velocity-value
    */
   private void createBinaryRepresentation(int decimal) {
       // The flipped array
       int[] flipBinary = new int[MAX];

       // Init the array
       for (int i = 0; i < MAX; i++)
           flipBinary[i] = 0;
       // Init the position
       position = 0;
       
       // Detect the bits
       while (decimal > 0) {
          flipBinary[position] = decimal % 2;
          decimal /= 2;
          position++;
       }

       // Copy the values to the target array
       for (int i = 0; i < MAX; i++)
           binary[MAX-i-1] = flipBinary[i];    
   }

    
   /**
    * This method returns the pitch spelling With the help of 
    * the two low bits of the velocity stored in the array binary[].
    * @return the pitch spelling
    */
   private int pitchSpelling() {
       
       if (binary[5] == 0 && binary[6] == 1) {
           // (x,x,x,x,x,0,1) return 1 
           return 1;          
       }

       if (binary[5] == 1 && binary[6] == 0) {
           // (x,x,x,x,x,1,0) return 2 
           return 2;          
       }

       if (binary[5] == 1 && binary[6] == 1) {
           // (x,x,x,x,x,1,1) return 3 
           return 3;          
       }
       
       // (x,x,x,x,x,0,0) return 0, means unknown spelling 
       return 0;
   }
   
   /**
    * This method returns the slur informations With the help of 
    * four bits of the velocity stored in the array binary[]. <BR>
    * 
    * Note: Not implemented yet.
    * @return the pitch spelling
    */
   private int getSlur() {
       int spelling;
       
       if (binary[1] == 0 && binary[2] == 0 && binary[3] == 0 && binary[4] == 1) {
           // (x,0,0,0,1,x,x) return 1 
           return 4;          
       }

       // TODO The other case has to be written here.
       
       // (x,0,0,0,0,x,x) return 0, means no slur information attached to this note 
       return 0;
   }
   
   /**
    * This method will return some infos about the velocity analyser as a string.
    * @return some infos
    */
   @Override
public String toString() {
       // String to return
       String retString = "VeloctyAnalyser: ";

       retString += "MIDI velocity: ";
       for (int i = 0; i < MAX; i++)
           retString += binary[i]+"";
       retString += ", diatonic: "+scoreNote.getDiatonic()+"";
       retString += ", accidental: "+scoreNote.getAlteration()+"\n";
      
       return retString;
   }
   
	/**
	 * Just a demonstration of this class. There is a MidiReader import. The used MIDI file 
	 * 'import_MIDI1_1.mid' has got the input type MIDI1. Although the pitch of all four notes 
	 * - stored in the midi file - is equal (pitch = 60 = C4), the MidiVelocityAnalyser will set 
	 * different diatonic and accidental to the created ScoreNotes. Because the note are different 
	 * in there velocity. <P>
	 * 1. note: velocity 88 = 10110.00, means pitch spelling unkown <P> 
	 * 2. note: velocity 89 = 10110.01, diatonic: d, accidental: -2 (bb) <P>
	 * 3. note: velocity 90 = 10110.10, diatonic: c, accidental: 0 <P>
	 * 4. note: velocity 91 = 10110.11, diatonic: b, accidental: 1 (#)
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {

		// Create the MidiReader object
		MidiReader aMidiReader = new MidiReader();
		// Name of the file to process
		String filename = "import_MIDI1_1.mid";
		// Target of the import
		Piece piece = null;

		try {
			// Try the command line argument like file:/d:/Progamme/Eclipse/.../import.mid
			// to read the MIDI file into the piece object
		    piece =
				aMidiReader.getPiece(
					aMidiReader.getClass().getResource(filename), "midi1");
		} catch (java.lang.ArrayIndexOutOfBoundsException iOOBoundsException) {
			System.out.println("MidiReader: No command line argument.");
			return;
		} catch (WrongArgumentException wAException) {
			System.out.println("MidiReader: wrong argument. Check the input type.");
			return;		    
		} catch (NullPointerException nPException) {
			System.out.println("MidiReader: File (" + filename + ") not found");
			return;
		}

		// Get the create not objects
		Container notePool = piece.getNotePool();

		for (int i=0; i < notePool.size(); i++) {
		    ScoreNote scoreNote = ((Note) notePool.get(i)).getScoreNote();
		    System.out.println(i+". "+scoreNote);
		}
	}
}

