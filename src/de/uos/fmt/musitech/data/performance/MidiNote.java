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

import java.io.Serializable;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 * Class for a musical note in MIDI Representation.
 * 
 * @author TW
 * @version $Revision: 7971 $, $Date: 2011-07-04 23:24:46 +0200 (Mon, 04 Jul 2011) $
 * @hibernate.class table = "MidiNote"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 */
public class MidiNote extends PerformanceNote implements Serializable, Cloneable {

	public int channel = 0;

	/**
	 * MidiNote constructor. The velocity of the given PerformanceNote must be
	 * between 1 and 127, otherwise the next possible value is used.
	 * 
	 * @param pn PerformanceNote
	 * @see de.uos.fmt.musitech.data.performance.PerformanceNote
	 */
	public MidiNote(PerformanceNote pn) {
		super(pn.getTime(), pn.getDuration());
		this.setPitch(pn.getPitch());

		int velocity = pn.getVelocity();
		if (velocity > 127) {
			System.out.println("MidiNote.MidiNote(PerformanceNote): warning - the used velocity ("
								+ velocity + ") is too big");
			velocity = 127;
		}
		if (velocity < 1) {
			System.out.println("MidiNote.MidiNote(PerformanceNote): warning - the used velocity ("
								+ velocity + ") is too small");
			velocity = 1;
		}
		this.setVelocity(velocity);
	}

	/**
	 * MidiNote constructor.
	 */
	public MidiNote() {
	}

	/**
	 * MidiNote constructor. Creates a note with default duration (192000
	 * micro-sec) and default velocity (80).
	 * 
	 * @param b long, the beginning of the note
	 */
	public MidiNote(long b) {
		this(b, 192000);
	}

	/**
	 * MidiNote constructor.
	 * 
	 * @param b long, the beginning of the note
	 * @param l long, the duration of the note
	 */
	public MidiNote(long b, long l) {
		this(b, l, 80);
	}

	/**
	 * MidiNote constructor.
	 * 
	 * @param b long, the beginning of the note
	 * @param l long, the duration of the note
	 * @param v long, the velocity of the note
	 */
	public MidiNote(long b, long l, int v) {
		this(b, l, v, 60);
	}

	/**
	 * MidiNote constructor.
	 * 
	 * @param b long, the beginning of the note
	 * @param l long, the duration of the note
	 * @param v long, the velocity of the note
	 * @param p long, the pitch of the note
	 */
	public MidiNote(long b, long l, int v, int p) {
		super(b, l, v, p);
	}

	// /**
	// * MidiNote constructor.
	// * @param b long, the beginning of the note
	// * @param l long, the duration of the note
	// * @param v long, the velocity of the note
	// * @param p long, the pitch of the note
	// * @param o long, the offset of the note
	// */
	// public MidiNote(long b, long l, int v, int p, long o) {
	// setTime(b);
	// duration = l;
	// velocity = (short)v;
	// pitch = (byte)p;
	// offset = o;
	// }

	/**
	 * MidiNote constructor.
	 * 
	 * @param b long, the beginning of the note
	 * @param l long, the duration of the note
	 * @param p long, the pitch of the note
	 * @param v long, the velocity of the note
	 * @param o long, the offset of the note
	 * @param c int, the midi channel
	 */
	public MidiNote(long b, long l, int v, int p, long o, int c) {
		super(b, l, v, p);
		channel = c;
	}

	/**
	 * MidiNote constructor.
	 * 
	 * @param b long, the beginning of the note
	 * @param l long, the duration of the note
	 * @param p long, the pitch of the note
	 * @param v long, the velocity of the note
	 * @param c int, the midi channel
	 */
	public MidiNote(long b, long l, int v, int p, int c) {
		super(b, l, v, p);
		channel = c;
	}

	/**
	 * This converts any Performance note into a MidiNote. If the argument note
	 * is a MidiNote it will be returned, else a new MidiNote is created.
	 * Information from the argument that exceedd the PerformanceNote interface
	 * will not be present in the MidiNote, but the argument object remains
	 * unchanged. For a null argument a null value will be returned.
	 * 
	 * @param pn The performance note to convert.
	 * @return MidiNote The MIDI note corresponding to the argument.
	 */
	public static MidiNote convert(PerformanceNote pn) {
		if (pn == null)
			return null;
		if (pn instanceof MidiNote)
			return (MidiNote) pn;
		else
			return new MidiNote(pn);
	}

	// /**
	// * getOffset
	// * @return the offset of this Note.
	// */
	// public long getOffset() {
	// return offset;
	// }

	/**
	 * Accessor method
	 * 
	 * @return int
	 * @hibernate.property
	 */
	public int getChannel() {
		return channel;
	}

	// /**
	// * Accessor method
	// * @date (07.10.00 21:51:05)
	// * @param newOffset long
	// */
	// public void setOffset(long newOffset) {
	// offset = newOffset;
	// }

	/**
	 * Accessor method
	 * 
	 * @param newChannel int
	 */
	public void setChannel(int newChannel) {
		channel = newChannel;
	}

	/**
	 * Method to copy a note.
	 * 
	 * @date (14.10.00 19:07:58)
	 * @return music.MidiNote
	 */
	public MidiNote copy() {
		try {
			return (MidiNote) clone();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Method to compare two notes
	 * 
	 * @date (27.10.00 22:24:34)
	 * @return boolean
	 * @param other music.MidiNote
	 */
	public boolean isEquivalent(MidiNote other) {
		return (getTime() == other.getTime() & duration == other.duration && pitch == other.pitch && velocity == other.velocity);
	}

	/**
	 * Returns the MidiNote as a note on MidiEvent
	 * 
	 * @return MidiEvent
	 * @throws InvalidMidiDataException
	 */
	public MidiEvent getNoteOnMidiEvent() throws InvalidMidiDataException {
		ShortMessage sMessage = new ShortMessage();
		sMessage.setMessage(ShortMessage.NOTE_ON, channel, pitch, velocity);
		return new MidiEvent(sMessage, getTime());
	}

	/**
	 * Returns the MidiNote as a note off MidiEvent
	 * 
	 * @return MidiEvent
	 * @throws InvalidMidiDataException
	 */
	public MidiEvent getNoteOffMidiEvent() throws InvalidMidiDataException {
		ShortMessage sMessage = new ShortMessage();
		sMessage.setMessage(ShortMessage.NOTE_OFF, channel, pitch, velocity);
		return new MidiEvent(sMessage, getTime() + duration);
	}

	/**
	 * Returns the MIDI note as a string
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		String retString = "MIDI " + getPitch() + " (pitch) from " + getTime() + " for "
							+ getDuration();
		retString += " on channel " + getChannel();

		return retString;
	}

	/**
	 * TODO add comment
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		MidiNote mn = (MidiNote) obj;
		boolean equalPerfNotes = super.equals(obj);
		if (!equalPerfNotes)
			return false;
		if (channel != mn.getChannel())
			return false;
		return true;
	}

	/**
	 * TODO add comment
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash ^= channel;
		return super.hashCode();
	}
}
