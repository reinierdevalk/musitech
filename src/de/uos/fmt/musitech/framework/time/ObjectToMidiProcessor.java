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
 * Created on 07.10.2003
 *
 */
package de.uos.fmt.musitech.framework.time;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.performance.midi.MidiTimedMessage;
import de.uos.fmt.musitech.time.TimeLine;
import de.uos.fmt.musitech.utility.RingBuffer;
import de.uos.fmt.musitech.utility.collection.SortedCollection;

/**
 * This class converts PerfomanceNote to MidiNotes An Arraylist, which contains
 * TimeMidiEvents. This Class is needed to playback timelines, which contains
 * MidiNotes
 * 
 * @author Jan Kramer
 *  
 */
public class ObjectToMidiProcessor {

	private SortedCollection<MidiEvent> sortedCollection = new SortedCollection<MidiEvent>(
			MidiEvent.class, new MidiEventTimeComparator());

	private SortedCollection<MidiEvent> playedList = new SortedCollection<MidiEvent>(MidiEvent.class,
			new MidiEventTimeComparator());

	private MidiPlayer midiPlayer = MidiPlayer.getInstance();

	private int playIndex;

	private long lastTimeStamp;

	private TimeLine timeLine;

	Container container;

	public ObjectToMidiProcessor() {

	}

	/**
	 * TODO comment
	 * 
	 * @param newPlayer
	 * @param line
	 */
	public ObjectToMidiProcessor(MidiPlayer newPlayer, TimeLine line) {
		midiPlayer = newPlayer;
		timeLine = line;
		processTimeLine();
	}

	/**
	 * TODO comment
	 * @param argPlayer 
	 * @param argContainer 
	 */
	public ObjectToMidiProcessor(MidiPlayer argPlayer, Container<?> argContainer) {
		midiPlayer = argPlayer;
		container = argContainer;
		processContainer();
	}

	/**
	 * Adds a PerformanceNote in correct time order with Note-on and Note-off event in
	 * ArrayList
	 * 
	 * @param perfNote the Note to be added
	 * @return true if note added successfully, false otherwise
	 */
	public synchronized boolean add(PerformanceNote perfNote) {
		ShortMessage onMessage = new ShortMessage();
		int midiChannel = 1;
		if (perfNote instanceof MidiNote) {
			midiChannel = ((MidiNote) perfNote).getChannel();
		}
		try {
			onMessage.setMessage(ShortMessage.NOTE_ON, midiChannel, perfNote
					.getPitch(), perfNote.getVelocity());
		} catch (InvalidMidiDataException e) {
			System.out.println("ObjectToMidi: setMessage note_on failt");
			e.printStackTrace();
		}
		MidiEvent onEvent = new MidiEvent(onMessage, perfNote.getTime());
		sortedCollection.add(onEvent);
		ShortMessage offMessage = new ShortMessage();
		try {
			offMessage.setMessage(ShortMessage.NOTE_OFF, midiChannel, perfNote
					.getPitch(), perfNote.getVelocity());
		} catch (InvalidMidiDataException e1) {
			e1.printStackTrace();
		}
		MidiEvent offEvent = new MidiEvent(offMessage, perfNote.getTime()
				+ perfNote.getDuration());
		sortedCollection.add(offEvent);
		return true;
	}

	/**
	 * Adds a Note in correct timeorder with Note-on and Note-off event in
	 * ArrayList
	 * @param note the Note to be added
	 * @return false, if no note added, else true
	 */
	public synchronized boolean add(Note note) {

		if (note.midiNote() == null)
			return false;

		MidiEvent newEvent = null;
		try {
			newEvent = note.midiNote().getNoteOnMidiEvent();

		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return false;
		}
		sortedCollection.add(newEvent);

		try {
			newEvent = note.midiNote().getNoteOffMidiEvent();

		} catch (InvalidMidiDataException e1) {
			e1.printStackTrace();
			return false;
		}
		sortedCollection.add(newEvent);
		return true;
	}

	/**
	 * TODO add comment
	 * 
	 * @param event
	 */
	public synchronized void add(MidiEvent event) {
		sortedCollection.add(event);
	}

	/**
	 * TODO add comment
	 *  
	 */
	public void processTimeLine() {
		for (int i = 0; i < timeLine.size(); i++) {
			if (timeLine.get(i) instanceof Note)
				add((Note) timeLine.get(i));
		}
		MetaMessage endOfTrack = new MetaMessage();
		try {
			endOfTrack.setMessage(0x2F, new byte[0], 0);
		} catch (InvalidMidiDataException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		lastTimeStamp = 0;
		if (sortedCollection.size() > 0) {
			MidiEvent lastEvent = sortedCollection.get(sortedCollection.size() - 1);
			lastTimeStamp = lastEvent.getTick();
		}
		//MidiEvent endOfTrackEvent = new MidiEvent(endOfTrack, lastTimeStamp);
		playIndex = 0;
	}

	/**
	 * TODO add comment
	 */
	protected void processContainer() {

		if (container != null) {
			Containable[] contents = container.getContentsRecursive();

			for (int i = 0; i < contents.length; i++) {
				if (contents[i] instanceof Note) {
					MidiNote midinote = ((Note) contents[i]).midiNote();

					int secureCount = 0; // to avoid endless loop
					do {

						if (midinote != null){
							add(midinote);
						midinote = MidiNote.convert(midinote.getNext());
						secureCount++;
						}
					} while (midinote != null && secureCount < 1000);

					
				}
				if (contents[i] instanceof PerformanceNote)
					add((PerformanceNote) contents[i]);
				if (contents[i] instanceof MidiTimedMessage) {
					MidiTimedMessage timed = new MidiTimedMessage();
					timed = (MidiTimedMessage) contents[i];
					MidiEvent event = timed.getMidiEvent();

					if (event.getMessage() instanceof ShortMessage) {
						ShortMessage shortmes = (ShortMessage) event.getMessage();
						// TODO What should be done here?
					}
					add(timed.getMidiEvent());
				}
			}
		}

		MetaMessage endOfTrack = new MetaMessage();
		try {
			endOfTrack.setMessage(0x2F, new byte[0], 0);
		} catch (InvalidMidiDataException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		lastTimeStamp = 0;
		if (sortedCollection.size() > 0) {
			MidiEvent lastEvent = sortedCollection.get(sortedCollection.size() - 1);
			lastTimeStamp = lastEvent.getTick();
		}
		// MidiEvent endOfTrackEvent = new MidiEvent(endOfTrack, lastTimeStamp);
		playIndex = 0;
		//		collToString();
	}

	// only for debugging
	// outputs the contents of the sortedCollection
	private void collToString() {
		for (int i = 0; i < sortedCollection.size(); i++) {
			System.out.println("sortColl: start: "
					+ sortedCollection.get(i).getTick()
					+ " pitch "
					+ sortedCollection.get(i).getMessage()
							.getMessage()[1]);

		}
	}

	public void fillRingbuffer() {
		RingBuffer ringBuffer = midiPlayer.getRingBuffer();

		synchronized (ringBuffer) {
			int writeCount = 0;
			if (sortedCollection.size() > 0) {

				while (playIndex < sortedCollection.size()) {
					int didWrite = ringBuffer.write(sortedCollection
							.get(playIndex));
					// if didWrite = 0 it could be write into buffer,
					// so we can't increase playIndex
					if (didWrite != 0) {
						playIndex++;
						writeCount++;
					} else {
						break;
					}
					if (writeCount > 0)
						synchronized (midiPlayer) {
							midiPlayer.notify();
						}
				}
			}
		}

	}

	public synchronized void setTimeMicros(long micros) {
		int i = 0;
		MidiEvent event = null;

		RingBuffer ringBuffer = midiPlayer.getRingBuffer();
		synchronized (ringBuffer) {

			if (sortedCollection.size() != 0) {
				event = sortedCollection.get(0);
				// search for last event befor newMicros or last element in list
				while (event.getTick() < micros && i + 1 < size()) {
					i++;
					event = sortedCollection.get(i);
				}
				playIndex = i;
			}
			ringBuffer.clearBuffer();
		}
		fillRingbuffer();

	}

	public long getLastTimeStamp() {
		return lastTimeStamp;
	}

	/**
	 * Returns a TimedMidiEvent from the specified index
	 * 
	 * @param index
	 * @return TimeMidiEvent
	 */
	public MidiEvent get(int index) {
		return sortedCollection.get(index);
	}

	/**
	 * Returns the first TimedMidiEvent in ArrayList
	 * 
	 * @return
	 */
	public MidiEvent getFirst() {
		return sortedCollection.get(0);
	}

	/**
	 * Returns the size of the ArrayList
	 * 
	 * @return the size of the Arraylist
	 */
	public int size() {
		return sortedCollection.size();
	}

	/**
	 * Removes a TimedMidiEvent at the specified index
	 * 
	 * @param index
	 * @return true, if element was removed
	 */
	public synchronized boolean removeElementAt(int index) {
		if (index > sortedCollection.size())
			return false;
		else {
			sortedCollection.remove(index);
			return true;
		}
	}

	/**
	 * Removes the TimedMidiEvent from the list
	 * 
	 * @param event
	 * @return
	 */
	public synchronized boolean remove(MidiEvent event) {
		return sortedCollection.remove(event);
	}

	public synchronized void clear() {
		sortedCollection.clear();
		midiPlayer.getRingBuffer().clearBuffer();
		lastTimeStamp = 0;
	}

	/**
	 * @return
	 */
	public Container<?> getContainer() {
		return container;
	}

	protected SortedCollection<MidiEvent> getSortedCollection() {
		return sortedCollection;
	}

	/**
	 * @param container
	 */
	public void setContainer(Container<?> container) {
		this.container = container;
		clear();
		processContainer();
		fillRingbuffer();
	}

}