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
package de.uos.fmt.musitech.data.performance;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.time.TimedComparator;
import de.uos.fmt.musitech.performance.midi.NegativeTimeStampException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * A Sequence of MidiNotes.
 * 
 * @author TW
 * @version $Revision: 8260 $, $Date: 2012-07-31 20:01:34 +0200 (Tue, 31 Jul 2012) $
 * 
 * @hibernate.class table = "MidiNoteSequence"
 * @hibernate.joined-subclass @hibernate.joined-subclass-key column = "uid"
 *  
 */

public class MidiNoteSequence extends SortedContainer implements //java.util.Comparator,
        java.io.Serializable {

    static final long serialVersionUID = 1L;

    private String name = null;

    /**
     * MidiNoteSequence constructor.
     */
    public MidiNoteSequence() {
        super(null, MidiNote.class, new TimedComparator());
    }

    /**
     * Converts a Container into a MidiNoteSequence
     * 
     * @param cont Container
     * @return mns MidiNoteSequence
     */
    public static MidiNoteSequence convert(Container cont) {
        Object objects[] = cont.getContentsRecursive();
        MidiNoteSequence mns = new MidiNoteSequence();
        mns.setContext(cont.getContext());
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            if (obj instanceof Note) {
                MidiNote midinote;
                Note note = (Note) obj;
                midinote = note.midiNote();
                if (note.getPerformanceNote() instanceof MidiNoteSysEx) {
                    MidiNoteSysEx midiSysEx = (MidiNoteSysEx) note.getPerformanceNote();
                    mns.add(midiSysEx);

                }
                int secureCount = 0; // to avoid endless loop
                do {

                    if (midinote != null) {
                        mns.addNote(midinote);
                        midinote = MidiNote.convert(midinote.getNext());                    
                    } else { // create  performance note from the ScoreNote
                    	try{
                    		if(note.getScoreNote()==null || note.getScoreNote().getDiatonic()=='r')
                    			obj = null;
                    		else
                    			obj = note.getScoreNote().toPerformanceNote(Piece.getDefaultPiece().getMetricalTimeLine());
                    	} catch(Exception e){e.printStackTrace();};
                    }
                    secureCount++;
                } while (midinote != null && secureCount < 1000);
            }

            if (obj != null && obj instanceof PerformanceNote) {
                MidiNote note = null;
                note = new MidiNote((PerformanceNote) obj);
                do {
                    // for MidiNoteMulti (Triller etc..)

                    mns.addNote(note);
                    note = MidiNote.convert(note.getNext());
                } while (note != null);
            }

        }
        mns.setName(cont.getName());
        return mns;
    }

    /**
     * Adds a note to the sequence.
     * 
     * @date (01.04.00 03:23:37)
     */
    public void addNote(MidiNote note) {
        super.add(note);
    }

    /**
     * Sets the name of the MidiNoteSequence
     * 
     * @param newName String
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Returns the name of the MidiNoteSequence
     * 
     * @return name String
     * 
     * @hibernate.property
     */
    public String getName() {
        return name;
    }

    //    /**
    //     * Compares two notes with respect to their beginning (earlier is
    // smaller).
    //     *
    //     * @return int
    //     */
    //    public int compare(Object o1, Object o2) {
    //        PerformanceNote n1, n2;
    //        if (o1 instanceof PerformanceNote)
    //            n1 = (PerformanceNote) o1;
    //        else
    //            return 0;
    //        if (o2 instanceof PerformanceNote)
    //            n2 = (PerformanceNote) o2;
    //        else
    //            return 0;
    //        return (int) (n1.getTime() - n2.getTime());
    //    }

    /**
     * Deletes a note from the sequence.
     * 
     * @param note The note to delete.
     */
    public void deleteNote(MidiNote note) {
        super.remove(note);
    }

    /**
     * Deletes a note at a given index from the sequence.
     * 
     * @param i The index of the note to delete.
     */
    public void deleteNoteAt(int i) {
        super.remove(i);
    }

    /**
     * Make sure that notes have a minimum distance of 50 ms and a minimum rest
     * of 10 ms.
     * 
     * @return int The number of notes changed.
     */
    public int ensureNoteDist() {
        int notesChanged = 0;
        //		this.sort();
        for (int i = 1; i < size(); i++) {
            MidiNote note = getNoteAt(i);
            MidiNote noteBefore = getNoteAt(i - 1);
            if (getNoteAt(i).getTime() - getNoteAt(i - 1).getTime() < 100) {
                getNoteAt(i).setTime(getNoteAt(i - 1).getTime() + 100);
                notesChanged++;
            }
        }
        for (int i = 1; i < size(); i++) {
            MidiNote note = getNoteAt(i);
            MidiNote noteBefore = getNoteAt(i - 1);
            if (getNoteAt(i).getTime() - getNoteAt(i - 1).getDuration() - getNoteAt(i - 1).getTime() < 20) {
                getNoteAt(i - 1).setDuration(getNoteAt(i).getTime() - getNoteAt(i - 1).getTime() - 20);
            }
        }
        return notesChanged;
    }

    /**
     * Returns the index of the highest note of the MidiNoteSequence. Returns -1
     * when there is no note.
     * 
     * @return int The index of the note with the highest pitch.
     */
    public int getIndexOfHighestNote() {
        int indexOfHighestNote = -1;
        int pitchOfHighestNote = 0;
        for (int i = 1; i < size(); i++) {
            MidiNote note = getNoteAt(i);
            if (pitchOfHighestNote < note.getPitch()) {
                indexOfHighestNote = i;
                pitchOfHighestNote = note.getPitch();
            }
        }
        return indexOfHighestNote;
    }

    /**
     * Returns the index of the lowest note of the MidiNoteSequence. Returns -1
     * of there is no note.
     * 
     * @return int The index of the note with the lowest pitch.
     */
    public int getIndexOfLowestNote() {
        int indexOfLowestNote = -1;
        int pitchOfLowestNote = 128;
        for (int i = 1; i < size(); i++) {
            MidiNote note = getNoteAt(i);
            if (pitchOfLowestNote > note.getPitch()) {
                indexOfLowestNote = i;
                pitchOfLowestNote = note.getPitch();
            }
        }
        return indexOfLowestNote;
    }

    /**
     * Returns the note with index i
     * 
     * @date (01.04.00 03:23:37)
     * @param i int, the index
     */
    public MidiNote getNoteAt(int i) {
        return (MidiNote) get(i);
    }

    /**
     * Inserts the note at index i
     * 
     * @date (01.04.00 03:23:37)
     * @param i int, the index
     */
    public void insertNoteAt(MidiNote note, int pos) {
        super.add(pos, note);
    }

    /**
     * Tests two note-sequences of equality
     * 
     * @date (27.10.00 22:21:31)
     * @return boolean
     * @param other music.MidiNote
     */
    public boolean isEquivalent(MidiNoteSequence other) {
        boolean isEquiv = true;
        for (int i = 0; i < size() && isEquiv; i++) {
            if (!getNoteAt(i).isEquivalent(other.getNoteAt(i)))
                isEquiv = false;
        }
        return isEquiv;
    }

    /**
     * Sets the notes' durations to a standard value using a length factor of
     * 0.8.
     * 
     * @see MidiNoteSequence#normalizeDurations(double)
     */
    public void normalizeDurations() {
        normalizeDurations(0.8);
    }

    /**
     * Sets the notes' durations to a standard value defined by the length
     * factor argument. The length of each note is set to the onset interval
     * from the previous note, if there is a previous note. In case of the last
     * note the duration is set to the duration of the previous note. If there
     * is only one note, its length is set to the duration of one beat times.
     * 
     * @param lenFactor the factor to use. Must be between 0 and 1.
     */
    public void normalizeDurations(double lenFactor) {
        // for all notes
        for (int i = 0; i < size(); i++) {
            MidiNote note = getNoteAt(i);
            if (i + 1 < size()) {
                // if there is a next note
                MidiNote nextNote = getNoteAt(i + 1);
                note.duration = (long) ((nextNote.getTime() - note.getTime()) * lenFactor);
            } else {
                if (i > 0) {
                    // if note is the last of mulitple
                    MidiNote prevNote = getNoteAt(i - 1);
                    note.duration = prevNote.duration;
                } else
                    note.duration = (long) (1.0 / context.getPiece().getMetricalTimeLine()
                            .getTimeSignatureMarker(Rational.ZERO).getTimeSignature().getDenominator() * lenFactor);
            }
        }
    }

    /**
     * Converts this MidiNoteSequence into a java.sound.midi.Sequence with the
     * current tempo and resolution.
     * 
     * @return javax.sound.midi.Sequence
     * @return Number of notes converted.
     */
    public int noteToMidi(Sequence sq) {
        try {
            if (sq == null) {
                sq = new Sequence(Sequence.PPQ, 384);
                System.out.println("MidiNoteSequence: creating sequence with divisionType: " + sq.getDivisionType()
                                   + ", resolution: " + sq.getResolution());
            }
            Track tr = sq.createTrack();
            int res = sq.getResolution();
            return noteToMidi(tr, res);
        } catch (InvalidMidiDataException e) {
            System.out.println(e);
            return 0;
        }
    }

    /**
     * Converts this MidiNoteSequence into a MIDI Data with the current tempo
     * and resolution. Data is written into the Track passed as argument. The
     * MIDI Track gets the name of the MidiNoteSequence.
     * 
     * @param Track for the data.
     * @return Number of notes converted.
     */
    private int noteToMidi(Track tr, int res) {
        try {
            assert tr != null;
            if (tr == null)
                return 0;
            for (int i = 0; i < 16; i++) {
                ShortMessage sm = new ShortMessage();
                sm.setMessage(ShortMessage.PROGRAM_CHANGE, i, 0);
                MidiEvent me = new MidiEvent(sm, 0);
            }
            //			tr.add(me);
            int i;
            for (i = 0; i < size(); i++) {
                MidiNote n = getNoteAt(i);
                // System.out.println("MidiNoteSequence.noteToMidi(Track):
                // processing note (begin= "+n.getTime()+"ms): "+n.toString());
                ShortMessage sm = new ShortMessage();
                sm.setMessage(ShortMessage.NOTE_ON, n.pitch, n.velocity);
                long ticks = (long) context.getPiece().getMetricalTimeLine().getMetricTime(n.getTime()).mul(res * 4)
                        .toDouble();
                // TODO:
//                if (ticks == 0)
//                    ticks = 1;
                // System.out.println("MidiNoteSequence.noteToMidiSequence()
                // n.begin " + n.getTime() + " ticks " + ticks);
                MidiEvent me = new MidiEvent(sm, ticks);
                tr.add(me);
                sm = new ShortMessage();
                // To Do: millisToTicks wird so nicht immer richtig sein.
                long endTicks = (long) context.getPiece().getMetricalTimeLine()
                        .getMetricTime(n.getTime() + n.getDuration()).mul(res * 4).toDouble();
                // System.out.println("MidiNoteSequence.noteToMidiSequence()
                // n.duration " + n.duration + " ticks " + ticks);
                sm.setMessage(ShortMessage.NOTE_OFF, n.pitch, 0);
                me = new MidiEvent(sm, endTicks);

                tr.add(me);
                if (n instanceof MidiNoteSysEx) {
                    MidiNoteSysEx midiSys = (MidiNoteSysEx) n;
                    MidiEvent sysEv = new MidiEvent(midiSys.getMessage(), ticks + 1);
                    tr.add(sysEv);

                }
            }

            // Give the MIDI Track the MidiNoteSequence name
            if (getName() != null) {
                // Write the MidiNoteSequence name into a byte Array
                byte[] data = getName().getBytes();
                // Write the Array a MetaMessage. Typ=3 (0x03) means
                // Sequence/Track name.
                MetaMessage mMessage = new MetaMessage();
                try {
                    mMessage.setMessage(3, data, data.length);
                } catch (InvalidMidiDataException iMDE) {
                    System.out.println(iMDE);
                    System.out.println(">>MidiWriter: InvalidMidiDataException in write()");
                }
                // Create a MidiEvent
                MidiEvent mEvent = new MidiEvent(mMessage, 0);

                // Write the MidiEvent into the first track of the sequence
                tr.add(mEvent);
            }

            return i;
        } catch (InvalidMidiDataException e) {
            System.out.println(e);
            return 0;
        }
    }

    /**
     * Converts this MidiNoteSequence into a java.sound.midi.Sequence with the
     * current tempo and resolution.
     * 
     * @return sq javax.sound.midi.Sequence
     */
    public javax.sound.midi.Sequence noteToMidiSequence() {
        try {
            Sequence sq = new Sequence(Sequence.PPQ, 384);
            System.out.println("MidiNoteSequence: creating sequence with divisionType: " + sq.getDivisionType()
                               + ", resolution: " + sq.getResolution());
            noteToMidi(sq);
            return sq;
        } catch (InvalidMidiDataException e) {
            System.out.println(e);
            return null;
        }
    }

    //	public void sort() {
    //		Collections.sort(this,comp);
    //	}

    /**
     * Moves all notes by the specified amount of time.
     * 
     * @param offset The amount of time to move the notes.
     * @throws NegativeTimeStampException if a note's time results to be less
     *             than zero.
     */
    public void timeShift(long offset) throws NegativeTimeStampException {
        for (int i = 0; i < size(); i++)
            getNoteAt(i).setTime(getNoteAt(i).getTime() + offset);
        if (size() > 0 && getNoteAt(0).getTime() < 0)
            throw new NegativeTimeStampException();
    }

    /**
     * Multiplies every notes' begin and duration with the given factor.
     * 
     * @param factor The factor to multiply with.
     */
    public void timeStretch(double factor) throws NegativeTimeStampException {
        for (int i = 0; i < size(); i++) {

            getNoteAt(i).setTime((long) (getNoteAt(i).getTime() * factor));
            getNoteAt(i).duration *= factor;
        }
        if (size() > 0 && getNoteAt(0).getTime() < 0)
            throw new NegativeTimeStampException();
    }

    /**
     * Multiplies every notes' begin and duration with the given factor,
     * centered around <code>fix</code>, i.e. notes at fix are not changed in
     * their position.
     * 
     * @param factor The factor to multiply with.
     * @param fix The point in time to keep fixed.
     * @throws NegativeTimeStampException Notificatoin if a note's time results
     *             to be less than zero, does not prevent execution.
     */
    public void timeStretch(double factor, long fix) throws NegativeTimeStampException {
        for (int i = 0; i < size(); i++) {
            long time = getNoteAt(i).getTime();
            getNoteAt(i).setTime((long) ((time - fix) * factor + fix));
            getNoteAt(i).duration *= factor;
        }
        if (size() > 0 && getNoteAt(0).getTime() < 0)
            throw new NegativeTimeStampException();
    }

    /**
     * Adds a PerformanceNote with the specified <code>pitch</code> and
     * <code>duration</code>. The PerformanceNote starts with the
     * <code>offset</code> after the previous PerformanceNote (i.e. the last
     * element in the MidiNoteSequence before adding) has ended. If the sequence
     * currently is empty, the added PerformanceNote is given the
     * <code>time</code>0. Its velocity is set to a default value of 90.
     * 
     * @param pitch int pitch of the PerformanceNote to add
     * @param duration long duration of the PerformanceNote to add
     * @param offset long time offset between the end of the previous
     *            PerformanceNote and the beginning of the PerformanceNote to
     *            add
     */
    public void addnext(int pitch, long duration, long offset) {
        short velocity = 90;
        long time = 0;
        if (size() > 0) {
            PerformanceNote previous = (PerformanceNote) get(size() - 1);
            if (previous.getTime() != Timed.INVALID_TIME)
                time = previous.getTime() + previous.getDuration() + offset;
        } else
            time = 0;
        add(new MidiNote(time, duration, velocity, pitch));
    }

    /**
     * Adds a PerformanceNote with the specified <code>pitch</code>,
     * <code>duration</code> and
     * <code>velocity/code>. The PerformanceNote starts with the <code>offset</code>
     * after the previous PerformanceNote (i.e. the last element in the
     * MidiNoteSequence before adding) has ended. If the sequence currently is empty,
     * the added PerformanceNote is given the <code>time</code> 0. 
     * Its velocity is set to a default value of 90.
     * 
     * @param pitch int pitch of the PerformanceNote to add
     * @param duration long duration of the PerformanceNote to add
     * @param velocity short velocity of the PerformanceNote to add
     * @param offset long time offset between the end of the previous PerformanceNote and the beginning of the PerformanceNote to add
     */
    public void addnext(int pitch, long duration, short velocity, long offset) {
        long time = 0;
        if (size() > 0) {
            PerformanceNote previous = (PerformanceNote) get(size() - 1);
            if (previous.getTime() != Timed.INVALID_TIME)
                time = previous.getTime() + previous.getDuration() + offset;
        } else
            time = 0;
        add(new PerformanceNote(time, duration, velocity, pitch));
    }

}