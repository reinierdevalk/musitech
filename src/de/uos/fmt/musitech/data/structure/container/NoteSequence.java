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
package de.uos.fmt.musitech.data.structure.container;

import java.util.Comparator;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.time.TimedComparator;
import de.uos.fmt.musitech.performance.midi.NegativeTimeStampException;

/**
 * A Sequence of MidiNotes in a Vector.
 * @author TW
 * @version $Revision: 8157 $, $Date: 2012-05-25 13:45:24 +0200 (Fri, 25 May 2012) $
 * 
 * @hibernate.class table = "NoteSequence"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 * 
 */

public class NoteSequence
	extends SortedContainer<Note>
	implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	private String name = null;

	/**
	 * NoteSequence constructor.
	 */
	public NoteSequence() {
		super(null,Note.class,new TimedComparator());
	}

	/**
	 * NoteSequence constructor.
	 */
	public NoteSequence(Comparator<Note> comp) {
		super(null,Note.class,comp);
	}

	/**
	 * Adds a note to the vector.
	 * @date (01.04.00 03:23:37)
	 */
	public void addNote(Note note) {
		super.add(note);
	}

	/**
	 *	Sets the name of the NoteSequence
	 *	@param newName String
	 */
	@Override
	public void setName(String newName) {
		name = newName;
	}

	/**
	 *	Returns the name of the NoteSequence
	 *	@return name String
	 *
	 * @hibernate.property
	 */
	@Override
	public String getName() {
		return name;
	}


	/**
	 * Deletes a note from the vector
	 * @date (01.04.00 03:23:37)
	 */
	public void deleteNote(Note note) {
		super.remove(note);
	}

	/**
	 * Deletes a note from the vector
	 * @date (01.04.00 03:23:37)
	 */
	public void deleteNoteAt(int i) {
		list.remove(i);
	}
	
	/**
	 * Swaps the notes at indices i and j.
	 * @param i
	 * @param j
	 */
	public void swapNotes(int i, int j) {
		Note firstObject = list.get(i);
		Note secondObject = list.get(j);
		list.set(i, secondObject);
		list.set(j,  firstObject);
	}
	
	
	/**
	 * Replaces the note at index i by the note given as argument. 
	 * @author Reinier
	 * @param i
	 * @param n 
	 */
	public void replaceNoteAt(int i, Note n) {
		list.set(i, n);
	}
	
	
	/** 
	 * Returns the index within the NoteSequence of the note given as argument.
	 * @author Reinier
	 * @param n
	 * @return
	 */
	public int getIndexOf(Note n) {
		int index = 0;
		for (int i = 0; i < this.size(); i++) {
			if (this.getNoteAt(i).equals(n)) {
				index = i;
				break;
			}
		}
		return index;
	}
	

	/**
	 * Make sure that notes have a minimum distance (IOI)
	 * of 50 ms and a minimum rest (OOI) of 10 ms.
	 * @date (07.05.2001 10:13:04)
	 * @return The number of notes changed.
	 */
	public int ensureNoteDist() {
		int notesChanged = 0;
//		this.sort();
		for (int i = 1; i < size(); i++) {
			if (getMidiNoteAt(i).getTime() - getMidiNoteAt(i - 1).getTime() < 100) {
				getMidiNoteAt(i).setTime(getMidiNoteAt(i - 1).getTime() + 100);
				notesChanged++;
			}
		}
		for (int i = 1; i < size(); i++) {
			if (getMidiNoteAt(i).getTime()  
					- (getMidiNoteAt(i - 1).getTime() + getMidiNoteAt(i - 1).getDuration())
					< 10) 
			{
				getMidiNoteAt(i - 1).setDuration(
					getMidiNoteAt(i).getTime() - getMidiNoteAt(i - 1).getTime() - 10);
			}
		}
		return notesChanged;
	}

	/**
	 * Returns the index of the highest note of the NoteSequence.
	 * Returns -1 when there is no note.
	 * @date (27.10.2001 13:13:04)
	 * @return int The number of notes changed.
	 */
	public int getIndexOfHighestNote() {
		int indexOfHighestNote = -1;
		int pitchOfHighestNote = 0;
		for (int i = 1; i < size(); i++) {
			MidiNote note = getMidiNoteAt(i);
			if (pitchOfHighestNote < note.getPitch()) {
				indexOfHighestNote = i;
				pitchOfHighestNote = note.getPitch();
			}
		}
		return indexOfHighestNote;
	}

	/**
	 * Returns the index of the lowest note of the NoteSequence.
	 * Returns -1 when there is no note.
	 * @date (27.10.2001 13:13:04)
	 * @return int The number of notes changed.
	 */
	public int getIndexOfLowestNote() {
		int indexOfLowestNote = -1;
		int pitchOfLowestNote = 128;
		for (int i = 1; i < size(); i++) {
			MidiNote note = getMidiNoteAt(i);
			if (pitchOfLowestNote > note.getPitch()) {
				indexOfLowestNote = i;
				pitchOfLowestNote = note.getPitch();
			}
		}
		return indexOfLowestNote;
	}

	/**
	 * Returns the note with index i
	 * @date (01.04.00 03:23:37)
	 * @param i int, the index
	 */
	public Note getNoteAt(int i) {
		return get(i);
	}
	
	
	/**
	 * Returns the note with index i
	 * @date (01.04.00 03:23:37)
	 * @param i int, the index
	 */
	public MidiNote getMidiNoteAt(int i) {
		return getNoteAt(i).midiNote();
	}

	/**
	 * Inserts the note at index i
	 * @date (01.04.00 03:23:37)
	 * @param i int, the index
	 */
	public void insertNoteAt(Note note, int pos) {
		super.add(pos,note);
	}

	/**
	 * Tests two note-sequences of equality
	 * @date (27.10.00 22:21:31)
	 * @return boolean
	 * @param other music.Note
	 */
	public boolean isEquivalent(NoteSequence other) {
		boolean isEquiv = true;
		for (int i = 0; i < size() && isEquiv; i++) {
			if (!getMidiNoteAt(i).isEquivalent(other.getMidiNoteAt(i)))
				isEquiv = false;
		}
		return isEquiv;
	}

	/**
	 * Sets the note length to a standard value.
	 * @date (18.10.00 17:15:43)
	 */
	public void normalizeDurations() {
		normalizeDurations(0.8);
	}

	/**
	 * Sets the note length to a standard value
	 * @date (18.10.00 17:15:43)
	 * @param lenFactor, double
	 */
	public void normalizeDurations(double lenFactor) {
		for (int i = 0; i < size(); i++) {
			MidiNote note = getMidiNoteAt(i);
			if (i + 1 < size()) {
				MidiNote nextNote = getMidiNoteAt(i + 1);
				note.setDuration( (long) ((nextNote.getTime() - note.getTime()) * lenFactor));
			} else {
				if (i > 0) {
					MidiNote prevNote = getMidiNoteAt(i - 1);
					note.setDuration( prevNote.getDuration());
				} else
					note.setDuration((long) (note.getDuration() * 0.8));
			}
		}
	}

//	/**
//	 * 	Converts this NoteSequence into a java.sound.midi.Sequence
//	 *	with the current tempo and resolution.
//	 *	@return javax.sound.midi.Sequence
//	 *	@return Number of notes converted.
//	 */
//	public int noteToMidi(Sequence sq) {
//		try {
//			if (sq == null) {
//				sq = new Sequence(Sequence.PPQ, Sequencing.getInstance().getResolution());
//				System.out.println(
//					"NoteSequence: creating sequence with divisionType: "
//						+ sq.getDivisionType()
//						+ ", resolution: "
//						+ sq.getResolution());
//			}
//			Track tr = sq.createTrack();
//
//			return noteToMidi(tr);
//		} catch (InvalidMidiDataException e) {
//			System.out.println(e);
//			return 0;
//		}
//	}

//	/**
//	 * 	Converts this NoteSequence into a MIDI Data
//	 *	with the current tempo and resolution. 
//	 *	Data is written into the Track passed as argument.
//	 *	The MIDI Track gets the name of the NoteSequence.
//	 *  @param	Track for the data.
//	 *	@return Number of notes converted.
//	 */
//	public int noteToMidi(Track tr) {
//		try {
//			if (tr == null)
//				tr = Sequencing.getInstance().getSequence().createTrack();
//			for (int i = 0; i < 16; i++) {
//				ShortMessage sm = new ShortMessage();
//				sm.setMessage(ShortMessage.PROGRAM_CHANGE, i, 0);
//				MidiEvent me = new MidiEvent(sm, 0);
//			}
//			//			tr.add(me);
//			int i;
//			for (i = 0; i < size(); i++) {
//				Note n = getNoteAt(i);
//					// System.out.println("NoteSequence.noteToMidi(Track): processing note (begin= "+n.getTime()+"ms): "+n.toString());
//				ShortMessage sm = new ShortMessage();
//				sm.setMessage(ShortMessage.NOTE_ON, n.pitch, n.velocity);
//				long ticks = Sequencing.getInstance().millisToTicks(n.getTime());
//				if (ticks == 0)
//					ticks = 1;
//				// System.out.println("NoteSequence.noteToMidiSequence() n.begin " + n.getTime() + " ticks " + ticks);
//				MidiEvent me = new MidiEvent(sm, ticks);
//				tr.add(me);
//				sm = new ShortMessage();
//				// To Do: millisToTicks wird so nicht immer richtig sein.
//				ticks = Sequencing.getInstance().millisToTicks(n.length);
//				// System.out.println("NoteSequence.noteToMidiSequence() n.length " + n.length + " ticks " + ticks);
//				sm.setMessage(ShortMessage.NOTE_OFF, n.pitch, 0);
//				me =
//					new MidiEvent(
//						sm,
//						Sequencing.getInstance().millisToTicks(n.length + n.getTime()));
//				tr.add(me);
//			}
//
//			// Give the MIDI Track the NoteSequence name
//			if (getName() != null) {
//				// Write the NoteSequence name into a byte Array
//				byte[] data = getName().getBytes();
//				// Write the Array a MetaMessage. Typ=3 (0x03) means Sequence/Track name.
//				MetaMessage mMessage = new MetaMessage();
//				try {
//					mMessage.setMessage(3, data, data.length);
//				} catch (InvalidMidiDataException iMDE) {
//					System.out.println(iMDE);
//					System.out.println(">>MidiWriter: InvalidMidiDataException in write()");
//				}
//				// Create a MidiEvent
//				MidiEvent mEvent = new MidiEvent(mMessage, 0);
//
//				// Write the MidiEvent into the first track of the sequence
//				tr.add(mEvent);
//			}
//
//			return i;
//		} catch (InvalidMidiDataException e) {
//			System.out.println(e);
//			return 0;
//		}
//	}

//	/**
//	 * 	Converts this NoteSequence into a java.sound.midi.Sequence
//	 *	with the current tempo and resolution.
//	 *	@return sq javax.sound.midi.Sequence
//	 */
//	public javax.sound.midi.Sequence noteToMidiSequence() {
//		try {
//			Sequence sq = new Sequence(Sequence.PPQ, Sequencing.getInstance().getResolution());
//			System.out.println(
//				"NoteSequence: creating sequence with divisionType: "
//					+ sq.getDivisionType()
//					+ ", resolution: "
//					+ sq.getResolution());
//			noteToMidi(sq);
//			return sq;
//		} catch (InvalidMidiDataException e) {
//			System.out.println(e);
//			return null;
//		}
//	}

//	public void sort() {
//		Collections.sort(this,comp);
//	}

	public void timeShift(long offset) throws NegativeTimeStampException {
		for (int i = 0; i < size(); i++)
			getMidiNoteAt(i).setTime(getMidiNoteAt(i).getTime() + offset);
		if (size() > 0 && getMidiNoteAt(0).getTime() < 0)
			throw new NegativeTimeStampException();
	}

	public void timeStretch(double factor) {
		for (int i = 0; i < size(); i++) {
			getMidiNoteAt(i).setTime((long) (getMidiNoteAt(i).getTime() * factor));
			getMidiNoteAt(i).setDuration((long) (getMidiNoteAt(i).getDuration() * factor)) ;
		}
	}
}