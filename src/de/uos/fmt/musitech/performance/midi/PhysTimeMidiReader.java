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
 * Created on 12.11.2003
 */
package de.uos.fmt.musitech.performance.midi;

import javax.sound.midi.Sequence;

import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.linear.Part;

/**
 * Is used for sequences, containing Midi Events with
 * Physical Time.
 * @author Jan
 *
 */
public class PhysTimeMidiReader extends MidiReader {

	/**
	 * Starts the import of Sequence and returns a new Piece object. 
	 * If the MidiSystem returns no Sequence the method returns null.
	 * @param Sequence
	 * @returns the new work object
	 */
	public Piece getPiece(Sequence seq) {
		setSequence(seq);
	
		
	
		//Create the target of the conversion
		setPiece(new Piece());
	
		setDumpReceiver(new PhysTimeMidiDumpReceiver());
		
		//Init the MidiDumpReceiver		
		getDumpReceiver().init(this);
	
		/**
		 * Process the MIDI file content
		 */
		for (int trackNumber = 0;
			trackNumber < getNumberOfTracks();
			trackNumber++) {
	
			//Create a part for each track of the MIDI file and add it to the work object
			//System.out.println(">>MidiReader: reading track " + trackNumber + " into part...");
			NotationStaff aPart = new NotationStaff(getPiece().getScore());
			aPart.setName("Track " + trackNumber + ", ");
	
			readTrackIntoPart(trackNumber, aPart);
	
			getPiece().getScore().add(aPart);
		}
		getPiece().setName("Imported from Sequence");
	
		// TODO check modularization
		//		NotationSystem system = new NotationSystem(work.getContext());
		//
		//		//Create 16 objects to represent the MidiChannels and add them to the Piece object
		//		for (int i = 0; i < 16; i++) {
		//			NotationStaff staff = new NotationStaff(work.getContext());
		//			NotationVoice voice = new NotationVoice(work.getContext(), system);
		//			staff.add(voice);
		//			voice.setName("Midi channel " + i);
		//			dumpReceiver.setChannel(voice, i);
		//		}
		//		work.getContainerPool().add(system);
	
		// TODO
		//		NotationSystem notationSystem = new NotationSystem(work.getContext());
		//		//Add 16 objects to the Piece object
		//		for (int i = 0; i < 16; i++) {
		//			NotationStaff notationStaff = new NotationStaff(work.getContext());
		//			notationStaff.add(dumpReceiver.getChannel(i));
		//			notationStaff.setName("Midi Channel "+i);
		//			notationSystem.add(notationStaff);
		//			notationSystem.setName("Notation System");
		//		}
		//		work.getContainerPool().add(notationSystem);
	
		return getPiece();
	}

}
