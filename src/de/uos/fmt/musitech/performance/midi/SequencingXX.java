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

// Standard Java imports
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.BeatMarker;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.performance.midi.gui.*;
import de.uos.fmt.musitech.time.TimeKeeper;
import de.uos.fmt.musitech.time.TimeLine;


/**
 * Midi-System Wrapper.
 * Caution: The timing of the JavaSound-Synthesizer is only
 * correct when the synth is using a soundbank.
 * @author TW
 * @version $Revision: 7976 $, $Date: 2011-07-12 18:58:05 +0200 (Tue, 12 Jul 2011) $
 */
public class SequencingXX implements TimeKeeper {

	/**
	 * RecordingReceiver is a receiver that records Events into a
	 * MidiNoteSequence.
	 * @author TW
	 */
	class RecordReceiver implements Receiver {
		long pressed;
		boolean isPressed = false;
		int pitch;
		int velo;
		MidiNoteSequence nsq;

		//Constructor
		public RecordReceiver(MidiNoteSequence noteSequence) {
			nsq = noteSequence;
		}

		//Closes the recordreceiver
		@Override
		public void close() {
		}

		//Sends a midi-message
		@Override
		public void send(javax.sound.midi.MidiMessage message, long time) {
			long testTime = System.currentTimeMillis();
			if (!(message instanceof ShortMessage))
				return;
			ShortMessage shortMessage = (ShortMessage) message;
			int command = shortMessage.getCommand();
			if (isPressed
				&& (// current MidiNote released
			 (shortMessage.getData1()
					== pitch
					&& (command == ShortMessage.NOTE_OFF
						|| shortMessage.getData2() == 0)) // new MidiNote
					|| command == ShortMessage.NOTE_ON
					&& shortMessage.getData2() > 0)) {
				keyReleased(time);
			}
			if (command == ShortMessage.NOTE_ON
				&& shortMessage.getData2() > 0) {
				pitch = shortMessage.getData1();
				velo = shortMessage.getData2();
				if (midiThru == true)
					soundNoteOn(pitch, velo);
				isPressed = true;
				pressed = time - 50;
				if (java.lang.Math.abs(pressed - testTime) > 1000000) {
					// Workaround for Java-Implementations with strange event-time Values (IBM VAJ)
					pressed = testTime;
					System.out.println(
						this.getClass()
							+ ".keyReleased: Strange Timestamp received: "
							+ time);
				}
			}
		}

		public void keyReleased(long time) {
			long testTime = System.currentTimeMillis();
			if (isPressed) {
				if (midiThru == true)
					soundNoteOff();
				isPressed = false;
				long released = time;
				if (java.lang.Math.abs(released - testTime) > 1000000) {
					// Workaround for Java-Implementations with strange event-time Values (IBM VAJ)
					released = testTime;
					System.out.println(
						this.getClass()
							+ ".keyReleased: Strange Timestamp received: "
							+ time);
				}
				MidiNote n =
					new MidiNote(
						pressed - startTime,
						released - pressed,
						velo,
						pitch);
				if (nsq != null)
					nsq.addNote(n);
				System.out.println(
					this.getClass().getName()
						+ ".keyReleased() note begin:"
						+ n.getTime()
						+ " length:"
						+ n.getDuration());
			}
		}
	}

	/**
	 * This Listener listens for Keybord Events and produces
	 * a MIDI-Event when the Shift-KeyMarker is pressed.
	 * @author Tillman Weyde
	 */
	class RecordListener extends KeyAdapter {
		long pressed;
		boolean isPressed = false;
		MidiNoteSequence nsq;

		// Constructor
		public RecordListener(MidiNoteSequence noteSequence) {
			nsq = noteSequence;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			long testTime = System.currentTimeMillis();
			//			if (e.getKeyCode() != KeyEvent.VK_SHIFT)
			//				return;
			if (!isPressed) {
				// sound switched of due to timing problems
				soundNoteOn(keyToNote(e));
				isPressed = true;
				pressed = e.getWhen() - 100;
				if (java.lang.Math.abs(pressed - testTime) > 1000000)
					// Workaround for Java-Implementations with strange event-time Values (IBM VAJ)
					pressed = testTime;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			//			if (e.getKeyCode() != KeyEvent.VK_SHIFT)
			//				return;
			if (isPressed) {
				// sound switched of due to timing problems
				soundNoteOff();
				long testTime = System.currentTimeMillis();
				isPressed = false;
				long released = e.getWhen() - 100;
				if (java.lang.Math.abs(released - testTime) > 1000000)
					// Workaround for Java-Implementations with strange event-time Values (IBM VAJ)
					released = testTime;
				MidiNote n =
					new MidiNote(
						pressed - startTime,
						released - pressed,
						80,
						keyToNote(e));
				if (nsq != null)
					nsq.addNote(n);
				System.out.println(
					this.getClass().getName()
						+ ".keyReleased() note begin:"
						+ n.getTime()
						+ " length:"
						+ n.getDuration());
			}
		}

		private int keyToNote(KeyEvent e) {
			int noteIndex = 60;
			

			if (e.getKeyCode() != KeyEvent.VK_SHIFT
				|| e.getKeyCode() != KeyEvent.VK_CONTROL) {

				switch (e.getKeyCode()) {

					case (KeyEvent.VK_Y) :
						noteIndex = 60;
						break;
					case (KeyEvent.VK_X) :
						noteIndex = 62;
						break;
					case (KeyEvent.VK_C) :
						noteIndex = 64;
						break;
					case (KeyEvent.VK_V) :
						noteIndex = 65;
						break;
					case (KeyEvent.VK_B) :
						noteIndex = 67;
						break;
					case (KeyEvent.VK_N) :
						noteIndex = 69;
						break;
					case (KeyEvent.VK_M) :
						noteIndex = 71;
						break;
					case (KeyEvent.VK_COMMA) :
						noteIndex = 72;
						break;
					case (KeyEvent.VK_S) :
						noteIndex = 61;
						break;
					case (KeyEvent.VK_D) :
						noteIndex = 63;
						break;
					case (KeyEvent.VK_G) :
						noteIndex = 66;
						break;
					case (KeyEvent.VK_H) :
						noteIndex = 68;
						break;
					case (KeyEvent.VK_J) :
						noteIndex = 70;
						break;

				}
			} else {
				int offset = 0;
				switch (e.getKeyCode()){
					case (KeyEvent.VK_SHIFT)
						:

						offset = 12;
						break;
					case (KeyEvent.VK_CONTROL) :
						offset = -12;
						break;
				}
				noteIndex = noteIndex + offset;
			}
			

			return noteIndex;
		}
	}

	/*	private class WriteClickThread extends Thread {
	public void run() {
	System.out.println(getClass().getName() + ".run() started");
	System.out.println(getClass().getName() + " startTime="+ startTime);
	System.out.println(getClass().getName() + " now = "+System.currentTimeMillis());
	while (isPlaying()) {
	System.out.println(getClass().getName() + " writeClicksTo "+(clickTime + 200));
	writeClicksTo(clickTime + 200);
	//				long now = System.currentTimeMillis();
	//				System.out.println(getClass().getName() + " now = "+now);
	//				long nextTime = startTime + clickTime - 3000;
	//				System.out.println(getClass().getName() + " nextTime = "+nextTime );
	//				if (nextTime > now )
	try {
	Thread.sleep(100);
	} catch (Exception e) {
	e.printStackTrace();
	}
	}
	}
	}*/

	/**
	 *	System Time when recording / playback has started in milliseconds.
	 */
	static protected long startTime;
	/**
	 *	Time up to which the click is already written in milliseconds.
	 *	Relative to startTime.
	 */
	static protected SequencingXX instance;
	protected long clickTime;
	protected boolean recording;
	protected RecordListener recordListener;
	protected RecordReceiver recordReceiver;
	protected java.awt.Component keyComponent;
	protected Sequencer sequencer;
	protected Receiver receiver;
	protected Receiver seqReceiver;
	TimeSignature timeSig = new TimeSignature();
	int clickNoteStrong = 60;
	int clickNoteWeak = 61;
	int clickVeloStrong = 80;
	int clickVeloWeak = 60;
	long preCountTicks = 0;
	long preCountTime = 0;
	int preCountNum = 0;
	float tempo = 100;
	protected javax.sound.midi.Sequence sequence = null;
	protected int resolution = 384;
	protected javax.sound.midi.Track clickTrack;
	protected javax.sound.midi.Track soundTrack;
	public javax.sound.midi.MidiDevice outDevice = null;
	public javax.sound.midi.MidiDevice inDevice = null;
	protected int soundChannelNum = 0;
	protected int clickChannelNum = 9;
	protected int defaultNote = 60;
	protected int lastSoundNote = 60;
	public int defaultVelocity = 80;
	//	protected int metronome;
	//	WriteClickThread writeClickThread;
	private float millisPerBeat = (float) 60000.0 / getTempo();
	double ticksPerMillis = getResolution() / millisPerBeat;
	double millisPerTick = millisPerBeat / getResolution();
	protected boolean midiAvailable = false;
	static protected boolean playing = false;
	static public boolean metronomeOn = false;
	static SequencingEditorDialog sed;
	boolean preCount;
	protected javax.sound.midi.ShortMessage sm = new ShortMessage();
	javax.sound.midi.Transmitter transmitter;

	private javax.sound.midi.MidiDevice fieldTestDevice = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;

	/*
	 *	If you use a external midi-instrument, a keyboard for example.
	 *	You should turn midiThru to false. Otherwise you will hear a
	 *	midi-echo.
	 *	@author Alexander Luedeke
	 */
	public boolean midiThru = true;

	protected TimeThread timeThread = new TimeThread();
	static protected Vector timeablesVector = new Vector();

	Container container;

	public void setTimeLine(TimeLine tl) {

		//		while (isPlaying()) {
		//			try {
		//				long millis = ticksToMillis(getSequencer().getTickPosition());
		//				if ()
		//					TimeStamp ts = new TimeStamp(millis);
		//				for (Iterator iter = timeablesVector.iterator(); iter.hasNext();) {
		//					Timeable timeable = (Timeable) iter.next();
		//					timeable.setTime(ts);
		//				}
		//			} catch (Exception e) {}
		//		}
	}

	class TimeEventReceiver implements Receiver {

		TimeEventReceiver ter;

		/**
		 * @see javax.sound.midi.Receiver#send(javax.sound.midi.MidiMessage, long)
		 */
		@Override
		public void send(MidiMessage message, long time) {
			long millis; // XXX
			if (message.getStatus() == ShortMessage.TIMING_CLOCK)
				millis = ticksToMillis(getSequencer().getTickPosition());
		}

		/**
		 * @see javax.sound.midi.Receiver#close()
		 */
		@Override
		public void close() {
		}
	}

	/**
	 * Constructor
	 * @date (07.06.00 16:42:45)
	 */
	private SequencingXX() {
	}

	public void sendTime(long millis) {
		for (Iterator iter = timeablesVector.iterator(); iter.hasNext();) {
			try {
				Timeable timeable = (Timeable) iter.next();
				timeable.setTimePosition(millis);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(
		java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(
		java.lang.String propertyName,
		java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Exists the recording of the note-sequence
	 * @date (08.06.00 20:58:44)
	 * @return boolean
	 */
	public boolean exit() {
		sequencer.stop();
		sequencer.close();
		outDevice.close();
		instance = null;
		setMidiAvailable(false);
		return false;
	}

	/**
	 * Finalizes the recording of the note-sequence
	 * @date (08.06.00 20:57:47)
	 * @exception java.lang.Throwable The exception description.
	 */

	@Override
	protected void finalize() throws java.lang.Throwable {
		exit();
		super.finalize();
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
		getPropertyChange().firePropertyChange(evt);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(
		java.lang.String propertyName,
		int oldValue,
		int newValue) {
		getPropertyChange().firePropertyChange(
			propertyName,
			oldValue,
			newValue);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(
		java.lang.String propertyName,
		java.lang.Object oldValue,
		java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(
			propertyName,
			oldValue,
			newValue);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(
		java.lang.String propertyName,
		boolean oldValue,
		boolean newValue) {
		getPropertyChange().firePropertyChange(
			propertyName,
			oldValue,
			newValue);
	}

	/**
	 * Accessor method
	 * @return int
	 */
	public int getClickChannelNum() {
		return clickChannelNum;
	}

	/**
	 * Accessor method
	 * @return int
	 */
	public int getClickNoteStrong() {
		return clickNoteStrong;
	}

	/**
	 * Accessor method
	 * @return int
	 */
	public int getClickNoteWeak() {
		return clickNoteWeak;
	}

	/**
	 * Accessor method
	 * @return int
	 */
	public int getClickVeloStrong() {
		return clickVeloStrong;
	}

	/**
	 * Accessor method
	 * @return int
	 */
	public int getClickVeloWeak() {
		return clickVeloWeak;
	}

	/**
	 * Get the default note pitch.
	 * @return int
	 */
	public int getDefaultNote() {
		return defaultNote;
	}

	/**
	 * Get the default velocity.
	 * @return int
	 */
	public int getDefaultVelocity() {
		return defaultVelocity;
	}

	/**
	 * Returns the prefered midi-input device.
	 * @return javax.sound.midi.Receiver
	 */
	public javax.sound.midi.MidiDevice getInDevice() {
		if (inDevice == null) {
			try {
				MidiDevice.Info mdi[] = getMidiInputs();
				MidiDevice.Info prefDev = null;
				for (int i = 0; i < mdi.length; i++) {
					if (mdi[i].getName().startsWith("Wire:"))
						prefDev = mdi[i];
				}
				if (prefDev == null)
					prefDev = mdi[0];
				setInDevice(MidiSystem.getMidiDevice(prefDev));
			} catch (Exception e) {
				System.out.println("Sequencing.getOutDevice() " + e);
				return null;
			}
		}
		return inDevice;
	}

	/**
	 * Returns the info of the midi-input device.
	 * @return javax.sound.midi.Receiver
	 */
	public javax.sound.midi.MidiDevice.Info getInDeviceInfo() {
		MidiDevice.Info info = null;
		try {
			info = getInDevice().getDeviceInfo();
		} catch (Exception e) {
			System.out.println("Sequencing.getInDevice() " + e);
			return null;
		}
		return info;
	}

	/**
	 * Accessor method
	 * @return Sequencing
	 */
	static public SequencingXX getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}

	/**
	 * Returns vector of midi-in device infos.
	 * @return java.lang.String[]
	 */
	public MidiDevice.Info[] getMidiInputs() {
		boolean testing = false;
		MidiDevice.Info mdi[] = MidiSystem.getMidiDeviceInfo();
		Vector vec = new Vector();
		for (int i = 0; i < mdi.length; i++) {
			MidiDevice md = null;
			try {
				md = MidiSystem.getMidiDevice(mdi[i]);
				if (md instanceof Sequencer)
					continue;
				if (md.getMaxTransmitters() != 0)
					vec.add(mdi[i]);

				if (testing) {
					System.out.println("Device: " + i);
					System.out.println("Name: " + mdi[i].getName());
					System.out.println(
						"Descritpion: " + mdi[i].getDescription());
					System.out.println(
						"maxTransmitters: " + md.getMaxTransmitters());
					System.out.println("maxReceivers: " + md.getMaxReceivers());
					md.open();
					Receiver r = md.getReceiver();
					ShortMessage sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_ON,
						soundChannelNum,
						defaultNote,
						defaultVelocity);
					r.send(sm, -1);
					System.out.println("playing");
					System.out.flush();
					Thread.sleep(1000);
					sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_OFF,
						soundChannelNum,
						defaultNote,
						defaultVelocity);
					r.send(sm, -1);
					System.out.println("done");
					System.out.flush();
				}
			} catch (Exception e) {
				System.out.println("Sequencing.getMidiInputs() " + e);
			} finally {
				if (md != null)
					md.close();
			}
		}
		mdi = new MidiDevice.Info[vec.size()];
		vec.copyInto(mdi);
		return mdi;
	}

	/**
	 * Returns vector of midi-out device-infos.
	 * @return java.lang.String[]
	 */

	public MidiDevice.Info[] getMidiOutputs() {
		boolean testing = false;
		MidiDevice.Info mdi[] = MidiSystem.getMidiDeviceInfo();
		Vector vec = new Vector();
		for (int i = 0; i < mdi.length; i++) {
			MidiDevice md = null;
			try {
				md = MidiSystem.getMidiDevice(mdi[i]);
				if (md instanceof Sequencer)
					continue;
				int maxRec = md.getMaxReceivers();
				if (maxRec != 0)
					vec.add(mdi[i]);
				if (testing) {
					md.open();
					Receiver r = md.getReceiver();
					System.out.println("Device: " + i);
					System.out.println("Name: " + mdi[i].getName());
					System.out.println(
						"Descritpion: " + mdi[i].getDescription());
					System.out.println(
						"maxTransmitters: " + md.getMaxTransmitters());
					System.out.println("maxReceivers: " + md.getMaxReceivers());
					ShortMessage sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_ON,
						soundChannelNum,
						defaultNote,
						defaultVelocity);
					r.send(sm, -1);
					System.out.println("playing");
					System.out.flush();
					Thread.sleep(1000);
					sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_OFF,
						soundChannelNum,
						defaultNote,
						defaultVelocity);
					r.send(sm, -1);
					System.out.println("done");
					System.out.flush();
				}
			} catch (Exception e) {
				System.out.println("Sequencing.getMidiOutputs() " + e);
			} finally {
				if (md != null)
					md.close();
			}
		}
		mdi = new MidiDevice.Info[vec.size()];
		vec.copyInto(mdi);
		return mdi;
	}

	/**
	 * Returns a midi-out-device, the synthesizer.
	 * @return javax.sound.midi.Receiver
	 */
	public javax.sound.midi.MidiDevice getOutDevice() {
		if (outDevice == null) {
			try {
				setOutDevice(MidiSystem.getSynthesizer());
			} catch (Exception e) {
				System.out.println("Sequencing.getOutDevice() " + e);
				return null;
			}
		}
		return outDevice;
	}

	/**
	 * Returns info about the midi-out-device.
	 * @return javax.sound.midi.Receiver
	 */
	public javax.sound.midi.MidiDevice.Info getOutDeviceInfo() {
		if (outDevice == null) {
			try {
				setOutDevice(MidiSystem.getSynthesizer());
			} catch (Exception e) {
				System.out.println("Sequencing.getOutDevice() " + e);
				return null;
			}
		}
		return outDevice.getDeviceInfo();
	}

	/**
	 * Accessor method
	 * @return int
	 */
	public int getPreCountNum() {
		return preCountNum;
	}

	/**
	 * Accessor for the propertyChange field.
	 */
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	/**
	 * Accessor method
	 * @date (08.06.00 13:02:20)
	 * @return int
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * Accessor method
	 * @date (17.06.00 17:13:27)
	 * @return javax.sound.midi.Receiver
	 */
	public javax.sound.midi.Receiver getSeqReceiver() {
		try {
			if (seqReceiver == null)
				setSeqReceiver(getSequencer().getReceiver());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seqReceiver;
	}

	/**
	 * Accessor method
	 * @date (08.06.00 12:58:54)
	 * @return javax.sound.midi.Sequence
	 */
	public javax.sound.midi.Sequence getSequence() {
		if (sequence == null)
			setSequence();
		return sequence;
	}

	/**
	 *	Returns a empty sequence. The Track 0 of sequence will be created.
	 *	@return sequence javax.sound.midi.Sequence
	 */
	public Sequence getEmptySequence() {
		setSequence();
		getSequence().createTrack();
		return getSequence();
	}

	/**
	 * Accessor method
	 * @date (17.06.00 17:13:27)
	 * @return javax.sound.midi.Sequencer
	 */
	public javax.sound.midi.Sequencer getSequencer() {
		try {
			if (sequencer == null)
				setSequencer(MidiSystem.getSequencer());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sequencer != null && !sequencer.isOpen())
			try {
				sequencer.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return sequencer;
	}

	/**
	 * Returns info about the Sequencer.
	 * @date (17.06.00 17:13:27)
	 * @return javax.sound.midi.Sequencer
	 */
	public javax.sound.midi.MidiDevice.Info getSequencerInfo() {
		if (getSequencer() != null)
			return getSequencer().getDeviceInfo();
		else
			return null;
	}

	/**
	 * Accessor method
	 * @date (11.06.00 14:22:53)
	 * @return int
	 */
	public int getSoundChannelNum() {
		return soundChannelNum;
	}

	/**
	 * Accessor method
	 * @date (11.06.00 14:22:53)
	 * @return int
	 */
	public Track getSoundTrack() {
		if (soundTrack == null)
			soundTrack = getSequence().createTrack();
		return soundTrack;
	}

	/**
	 * Accessor method
	 * @date (21.06.00 18:43:37)
	 * @return float
	 */
	public float getTempo() {
		return tempo;
	}

	/**
	 * Accessor method
	 * @date (16.04.2001 19:49:47)
	 * @return javax.sound.midi.Transmitter
	 */
	protected javax.sound.midi.Transmitter getTransmitter() {
		if (transmitter == null) {
			try {
				transmitter = getInDevice().getTransmitter();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return transmitter;
	}

	/**
	 * The hasListeners method was generated to support the propertyChange field.
	 */
	public synchronized boolean hasListeners(java.lang.String propertyName) {
		return getPropertyChange().hasListeners(propertyName);
	}

	/**
	* The methode inits a sequencing object.
	*/
	static protected boolean init() {
		System.out.println("music.Sequencing.init() started");
		synchronized (SequencingXX.class) {
			if (instance == null) {
					
				instance = new SequencingXX();
				// Initialize Midi Sequencer
				if (instance.getSequencer() != null) {
					instance.setSequence(null);
					instance.setMidiAvailable(true);
					instance.preCountNum = 4;
				}
			}
		}
		if (instance != null)
			System.out.println(instance.getClass().getName() + ".init() done");
		System.out.println("getSequencer: " + instance.getSequencer());
		return true;
	}

	/**
	 * Inits the precount
	 * @date (09.06.00 16:11:36)
	 * @return long
	 * @param numClicks int
	 */
	protected long initClick() {
		if (!isMidiAvailable())
			return 0;
		if (preCount)
			preCountTicks = preCountNum * resolution;
		else
			preCountTicks = 0;
		preCountTime = ticksToMillis(preCountTicks);
		clickTime = 0;
		try {
			MidiSequence.timeShift(getSequence(), preCountTicks);
		} catch (Exception e) {
			//		e.printStackTrace();
		}
		try {
			if (isMetronomeOn())
				writeClicksTo(300000);
			else
				writeClicksTo(preCountTicks);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Accessor method
	 * @date (24.06.00 13:57:46)
	 * @return boolean
	 */
	public static boolean isMetronomeOn() {
		return metronomeOn;
	}

	/**
	 * Accessor method
	 * @date (16.06.00 08:25:11)
	 * @return boolean
	 */
	public boolean isMidiAvailable() {
		return midiAvailable;
	}

	/**
	 * Returns the midiThru value.
	 * @author Alexander Luedeke @date (11.08.01 11:55:33)
	 * @return boolean
	 */
	public boolean isMidiThru() {
		return midiThru;
	}

	/**
	 * Accessor method
	 * @date (19.06.00 13:08:12)
	 * @return boolean
	 */
	static public boolean isPlaying() {
		return playing;
	}

	/**
	 * Accessor method
	 * @date (18.10.00 20:34:29)
	 * @return boolean
	 */
	public boolean isPreCount() {
		return preCount;
	}

	/**
	 * Accessor method
	 * @date (20.06.00 11:34:22)
	 * @return boolean
	 */
	protected boolean isRecording() {
		return recording;
	}

	/**
	 * Converts timestamps in milliseconds to ticks ref. to the current tempo and resolution.
	 * @date (21.06.00 22:38:44)
	 * @return long
	 * @param millis long
	 */
	public long millisToTicks(long millis) {
		return (long) (ticksPerMillis * millis);
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(
		java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(
		java.lang.String propertyName,
		java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(
			propertyName,
			listener);
	}

	/**
	 * Sends a midi-message to the receiver
	 * @date (17.06.00 16:59:32)
	 * @return boolean
	 */
	public boolean sendMidiData(int type, int channel, int pitch, int velo) {
		if (receiver != null) {
			try {
				sm.setMessage(type, channel, pitch, velo);
				receiver.send(sm, -1);
				System.out.println("midi message sent: " + sm);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Accessor method
	 * @date (11.06.00 14:22:53)
	 * @param newClickChannelNum int
	 */
	public void setClickChannelNum(int newClickChannelNum) {
		clickChannelNum = newClickChannelNum;
	}

	/**
	 * Accessor method
	 * @date (11.06.00 14:22:53)
	 * @param newClickNoteStrong int
	 */
	public void setClickNoteStrong(int newClickNoteStrong) {
		clickNoteStrong = newClickNoteStrong;
	}

	/**
	 * Accessor method
	 * @date (11.06.00 14:22:53)
	 * @param newClickNoteWeak int
	 */
	public void setClickNoteWeak(int newClickNoteWeak) {
		clickNoteWeak = newClickNoteWeak;
	}

	/**
	 * Accessor method
	 * @date (11.06.00 14:22:53)
	 * @param newClickVeloStrong int
	 */
	public void setClickVeloStrong(int newClickVeloStrong) {
		clickVeloStrong = newClickVeloStrong;
	}

	/**
	 * Accessor method
	 * @date (11.06.00 14:22:53)
	 * @param newClickVeloWeak int
	 */
	public void setClickVeloWeak(int newClickVeloWeak) {
		clickVeloWeak = newClickVeloWeak;
	}

	/**
	 * Accessor method
	 * @date (08.06.00 13:26:11)
	 * @param newDefaultNote int
	 */
	public void setDefaultNote(int newDefaultNote) {
		defaultNote = newDefaultNote;
	}

	/**
	 * Accessor method
	 * @date (08.06.00 13:26:25)
	 * @param newDefaultVelocity int
	 */
	public void setDefaultVelocity(int newDefaultVelocity) {
		defaultVelocity = newDefaultVelocity;
	}

	/**
	 * Sets a new midi-in-device
	 * @date (16.04.2001 19:50:34)
	 * @param newInDevice javax.sound.midi.MidiDevice
	 */
	public void setInDevice(MidiDevice newInDevice) {
		if (inDevice != null && inDevice.isOpen())
			inDevice.close();
		MidiDevice oldDevice = inDevice;
		inDevice = newInDevice;
		System.out.println("new inDevice: " + inDevice.getDeviceInfo());
		try {
			setTransmitter(inDevice.getTransmitter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a new midi-in-device by info
	 * @date (17.06.00 17:01:10)
	 * @param info javax.sound.midi.MidiDevice
	 */
	public void setInDeviceInfo(MidiDevice.Info info) {
		try {
			MidiDevice inDevice = MidiSystem.getMidiDevice(info);
			setInDevice(inDevice);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Accessor method
	 * @date (19.06.00 13:20:51)
	 * @param newMetronomeOn boolean
	 */
	public void setMetronomeOn(boolean newMetronomeOn) {
		metronomeOn = newMetronomeOn;
	}

	/**
	 * Accessor method
	 * @date (16.06.00 08:25:11)
	 * @param newMidiAvailable boolean
	 */
	public void setMidiAvailable(boolean newMidiAvailable) {
		midiAvailable = newMidiAvailable;
	}

	/**
	 * Sets the midiThru value.
	 * @author Alexander Luedeke @date (11.08.01 11:55:33)
	 * @param newMidiThru boolean
	 */
	public void setMidiThru(boolean newMidiThru) {
		midiThru = newMidiThru;
	}

	/**
	 * Sets a new midi-out-device
	 * @date (17.06.00 17:01:10)
	 * @param newOutDevice javax.sound.midi.MidiDevice
	 */
	public void setOutDevice(javax.sound.midi.MidiDevice newOutDevice) {
		if (outDevice != null && outDevice.isOpen())
			outDevice.close();
		outDevice = newOutDevice;
		try {
			outDevice.open();
			setReceiver(outDevice.getReceiver());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a new midi-out-device by info
	 * @date (17.06.00 17:01:10)
	 * @param newOutDevice javax.sound.midi.MidiDevice
	 */
	public void setOutDeviceInfo(javax.sound.midi.MidiDevice.Info info) {
		try {
			MidiDevice outDevice = MidiSystem.getMidiDevice(info);
			setOutDevice(outDevice);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Accessor method
	 * @date (18.10.00 20:34:29)
	 * @param newPreCount boolean
	 */
	public void setPreCount(boolean newPreCount) {
		preCount = newPreCount;
	}

	/**
	 * Accessor method
	 * @date (24.06.00 22:47:11)
	 * @param newPreCountNum int
	 */
	public void setPreCountNum(int newPreCountNum) {
		preCountNum = newPreCountNum;
	}

	/**
	 * This methode connects the transmitter of the sequencer with
	 * the receiver.
	 * @date (17.06.00 17:02:37)
	 * @param receiver javax.sound.midi.Receiver
	 */
	void setReceiver(Receiver newReceiver) {
		receiver = newReceiver;
		System.out.println("new receiver: " + receiver);
		try {
			getSequencer().getTransmitter().setReceiver(receiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Accessor method
	 * @date (20.06.00 11:34:22)
	 * @param newRecording boolean
	 */
	protected void setRecording(boolean newRecording) {
		recording = newRecording;
	}

	/**
	 * Accessor method
	 * @date (08.06.00 13:02:20)
	 * @param newResolution int
	 */
	public void setResolution(int newResolution) {
		resolution = newResolution;
	}

	/**
	 * Accessor method
	 * @date (17.06.00 17:02:37)
	 * @param receiver javax.sound.midi.Receiver
	 */
	void setSeqReceiver(Receiver newReceiver) {
		seqReceiver = newReceiver;
		System.out.println("new seqReceiver: " + seqReceiver);
	}

	/**
	 * Accessor method
	 * @date (17.06.00 17:02:37)
	 */
	public void setSequence() {
		setSequence(null);
	}

	/**
	 * Sets a sequence. In case the sequence is null the methode creates
	 * a new sequence.
	 * @date (08.06.00 12:58:54)
	 * @param newSequence javax.sound.midi.Sequence
	 */
	public void setSequence(javax.sound.midi.Sequence newSequence) {
		System.out.println(
			"music.Sequencing.setSequence() " + newSequence + " started");
		if (newSequence == null) {
			try {
				float mode = Sequence.PPQ;
				int res = resolution;
				newSequence = new Sequence(mode, res);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sequence = newSequence;
		if (sequence != null) {
			try {
				clickTrack = sequence.createTrack();
				soundTrack = sequence.createTrack();
				// Add an MidiEvent to the click Track to set ist Length
				if (false) {
					ShortMessage sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_OFF,
						(int) preCountTicks,
						0,
						0);
					MidiEvent me = new MidiEvent(sm, Long.MAX_VALUE);
					clickTrack.add(me);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*	try {
		if(getSequencer()!=null)
		getSequencer().setSequence(sequence);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	/**
	 * Sets a sequencer. If there is already a sequencer the sequencer
	 * will be stop and closed.
	 * @date (17.06.00 17:13:27)
	 * @param newSequencer javax.sound.midi.Sequencer
	 */
	public void setSequencer(javax.sound.midi.Sequencer newSequencer) {
		if (sequencer != null && sequencer.isOpen()) {
			sequencer.stop();
			sequencer.close();
		}
		sequencer = newSequencer;
		try {
			sequencer.open();
			seqReceiver = sequencer.getReceiver();
			System.out.println(
				"Sequencing.setSequencer(): " + sequencer.getDeviceInfo());
			Transmitter t = sequencer.getTransmitter();
			Receiver r = getOutDevice().getReceiver();
			t.setReceiver(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set sequencer by info.
	 * @date (17.06.00 17:13:27)
	 * @param newSequencer javax.sound.midi.Sequencer
	 */
	public void setSequencerInfo(javax.sound.midi.MidiDevice.Info info) {
		try {
			MidiDevice seqDev = MidiSystem.getMidiDevice(info);
			if (seqDev instanceof Sequencer)
				setSequencer((Sequencer) seqDev);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Accessor method
	 * @date (11.06.00 14:22:53)
	 * @param newSoundChannelNum int
	 */
	public void setSoundChannelNum(int newSoundChannelNum) {
		soundChannelNum = newSoundChannelNum;
	}

	/**
	 * Accessor method
	 * @date (21.06.00 18:43:37)
	 * @param newTempo float
	 */
	public void setTempo(float newTempo) {
		tempo = newTempo;
		System.out.println("tempo set to " + tempo);
		millisPerBeat = (float) (60000.0 / tempo);
		System.out.println("millisPerBeat set to " + millisPerBeat);
		ticksPerMillis = getResolution() / millisPerBeat;
		System.out.println("ticksPerMillis set to " + ticksPerMillis);
		millisPerTick = millisPerBeat / getResolution();
		System.out.println("millisPerTick set to " + millisPerTick);
	}

	/**
	 * Sets a transmitter. If there is already a transmitter the transmitter
	 * will be closed.
	 * @date (16.04.2001 19:49:47)
	 * @param newTransmitter javax.sound.midi.Transmitter
	 */
	protected void setTransmitter(
		javax.sound.midi.Transmitter newTransmitter) {
		if (transmitter != null)
			transmitter.close();
		transmitter = newTransmitter;
	}
	
	/**
	 * Shows a SequencingEditorDialog.
	 * @param parent the parent of the dialog
	 * @param center if true the dialog will be centered
	 */
	public void showParameterEditor(JFrame parent, boolean center) {
		if (sed == null || !sed.isShowing())
			sed = new SequencingEditorDialog(parent);

		sed.centerOnScreen();
			
		sed.show();
	}

	/**
	 * Shows a SequencingEditorDialog.
	 * @date (24.06.00 18:08:36)
	 */
	public void showParameterEditor() {
		if (sed == null || !sed.isShowing())
			sed = new SequencingEditorDialog();
		sed.show();
	}

	/**
	 * Sends the midi-event NOTE_OFF.
	 * @date (17.06.00 16:59:32)
	 * @return boolean
	 */
	public boolean soundNoteOff() {
		//	System.out.println("soundNoteOff");
		return sendMidiData(
			ShortMessage.NOTE_OFF,
			soundChannelNum,
			lastSoundNote,
			0);
	}

	/**
	 * Accessor method
	 * @date (17.06.00 16:59:32)
	 * @return boolean
	 */
	public boolean soundNoteOn() {
		//	System.out.println("soundNoteOn");
		return soundNoteOn(defaultNote);
	}

	/**
	 * Sends a midi-event NOTE_ON
	 * @date (17.06.00 16:59:32)
	 * @return boolean
	 */
	public boolean soundNoteOn(int pitch) {
		//	System.out.println("soundNoteOn");
		return soundNoteOn(pitch, defaultVelocity);
	}

	/**
	 * Sends a midi-event NOTE_ON
	 * @date (17.06.00 16:59:32)
	 * @return boolean
	 */
	public boolean soundNoteOn(int pitch, int velo) {
		//	System.out.println("soundNoteOn");
		if (receiver != null) {
			lastSoundNote = pitch;
			return sendMidiData(
				ShortMessage.NOTE_ON,
				soundChannelNum,
				pitch,
				velo);
		}
		return false;

	}

	/**
	 *	Starts playing a sequence.
	 */
	@Override
	public void startPlaying() {
		System.out.println(getClass().getName() + ".startPlaying() ");
		stop();
		startTime = System.currentTimeMillis();
		System.out.println(
			getClass().getName()
				+ ".startPlaying(): getTempo() = "
				+ getTempo());
		if (getSequencer() != null) {
			getSequencer().setTempoInBPM(getTempo());
			System.out.println(
				getClass().getName()
					+ ".startPlaying(): getSequencer().getTempoInBPM()) = "
					+ getSequencer().getTempoInBPM());
			System.out.println(
				getClass().getName()
					+ ".startPlaying(): getSequencer().getTempoFactor(),) = "
					+ getSequencer().getTempoFactor());
			getSequencer().setTickPosition(0);
		}
		initClick();
		try {
			if (getSequencer() != null)
				getSequencer().setSequence(getSequence());
		} catch (Exception e) {
			e.printStackTrace();
		}
		playing = true;
		startTime = System.currentTimeMillis();
		if (getSequencer() != null) {
			getSequencer().start();
			getSequencer().setTempoInBPM(getTempo());
			System.out.println(
				getClass().getName()
					+ ".startPlaying(): getSequencer().getTempoInBPM()) = "
					+ getSequencer().getTempoInBPM());
			System.out.println(
				getClass().getName()
					+ ".startPlaying(): getSequencer().getTempoFactor(),) = "
					+ getSequencer().getTempoFactor());
		}
		startTimeThread();
		System.out.println(getClass().getName() + ".startPlaying() done");
	}

	/**
	 *	Starts the time thread.
	 */
	synchronized void startTimeThread() {
		if (!timeThread.isAlive())
			timeThread.start();
	}

	/**
	 *	Starts playing a MidiNoteSequence sequence.
	 *	@param conti Container
	 */
	public void startPlaying(Container conti) {
		startPlaying(MidiNoteSequence.convert(conti));
	}

	/**
	 *	Starts playing a MidiNoteSequence sequence.
	 *	@param nsq MidiNoteSequence
	 */
	public void startPlaying(MidiNoteSequence nsq) {
		setSequence(null);
		nsq.noteToMidi(getSequence());
		startPlaying();
	}
	
	/**
	 *	After adding the sequence to the soundTrack and the beats to 
	 *	the clickTrack the playing will be started.
	 *	@param nsq the sequence of notes
	 *	@param beatSequence the sequence of beats
	 *	@param veloWeak the MIDI velocity of the weak click MIDI note
	 */
	public void startPlaying(MidiNoteSequence nsq, Container beatSequence, int veloWeak) {
		
		// Write the sequence of notes
		nsq.noteToMidi(getSequence());
		
		// Set the strong click note
		setClickNoteWeak(37);
		setClickVeloWeak(veloWeak);
		
		// Write the sequence of beats
		this.writeClicksToClickTrack(beatSequence);
	
		// Finally start playing	
		startPlaying();
	}

	/**
	 *	Starts playing a JavaSound sequence.
	 *	@param sq Sequence
	 */
	public void startPlaying(Sequence sq) {
		try {
			setSequence(sq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		startPlaying();
	}

	/**
	 *	Starts recording a sequence.
	 *	@param keyComponent java.awt.Component
	 */
	public void startRecording(java.awt.Component keyComponent) {
		startRecording(keyComponent, null);
	}

	/**
	 *	Starts recording a sequence.
	 *	@param keyComponent java.awt.Component
	 *	@param seq Sequence
	 */
	public void startRecording(java.awt.Component keyComponent, Sequence seq) {
		System.out.println(
			"Sequencing.startRecording() " + keyComponent + ", " + seq);
		stop();
		playing = true;
		setRecording(true);
		MidiNoteSequence nsq = new MidiNoteSequence();
		recordListener = new RecordListener(nsq);
		recordReceiver = new RecordReceiver(nsq);
		if (getTransmitter() != null)
			getTransmitter().setReceiver(recordReceiver);
		this.keyComponent = keyComponent;
		keyComponent.addKeyListener(recordListener);
		setSequence(seq); // fresh sequence
		initClick(); // write Precount
		if (getSequencer() != null) {
			try {
				sequencer.setSequence(getSequence());
			} catch (Exception e) {
				e.printStackTrace();
			}
			sequencer.setTempoInBPM(getTempo());
			sequencer.recordDisable(clickTrack);
			sequencer.recordEnable(soundTrack, -1);
			sequencer.setTickPosition(0);
		}
		startTime = System.currentTimeMillis();
		System.out.println(
			"Sequencing.startRecording() startTime: " + startTime);
		if (getSequencer() != null) {
			sequencer.startRecording();
			long sysTime1 = System.currentTimeMillis();
			long sqrTime = sequencer.getMicrosecondPosition() / 1000;
			System.out.println(
				"Sequencing.startRecording() sqrTime: " + sqrTime);
			long sysTime2 = System.currentTimeMillis();
			long sysTimeAvg = sysTime1 + (sysTime2 - sysTime1) / 2;
			startTime = sysTimeAvg - sqrTime;
		}
		startTimeThread();
		System.out.println(
			"Sequencing.startRecording() startTime: " + startTime);
	}

	/**
	 *	Stops the playing.
	 */
	@Override
	public void stopPlaying() {
		stop();
	}

	/**
	 *	Stops recording and playing a sequence.
	 *	@return MidiNoteSequence
	 */
	public MidiNoteSequence stop() {
		if (isRecording()) {
			setRecording(false);
			if (keyComponent != null)
				keyComponent.removeKeyListener(recordListener);
			keyComponent = null;
		}
		playing = false;
		if (sequencer != null) {
			sequencer.stop();
			sequencer.setTickPosition(0);
		}
		try {
			timeThread.interrupt();
			timeThread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		timeThread = new TimeThread();
		//try{
		//if(writeClickThread!=null)
		//writeClickThread.interrupt();
		//}catch(Exception e){e.printStackTrace();}
		MidiNoteSequence nsq = null;
		if (recordListener != null)
			nsq = recordListener.nsq;
		if (nsq != null) {
			try {
				nsq.timeShift(-preCountTime);
			} catch (Exception e) {
				System.out.println("Sequencing.stop(): " + e);
			}
			for (int i = 0; i < nsq.size(); i++) {
				if (nsq.getNoteAt(i) != null
					&& nsq.getNoteAt(i).getTime() < 0) {
					nsq.getNoteAt(i).setTime(0);
					System.out.println("Note too early, begin set to 0.");
				}

			}
		}
		recordListener = null;
		return nsq;
	}

	/**
	 * Accessor method
	 * @return long
	 * @param ticks long
	 */
	public long ticksToMillis(long ticks) {
		return (long) (ticks * millisPerTick);
	}

	/**
	 * Sets the click notes as midi-event.
	 * @return long 	Time of last click
	 * @param timeLimit	Up to which point should clicks be written (exclusive).
	 */
	protected long writeClicksTo(long timeLimit) throws IllegalStateException {
		
		if (timeLimit == 0)
			return 0;
		if (isMidiAvailable()) {
			long evtTime;
			if (sequence.getDivisionType() != Sequence.PPQ) {
				throw (
					new IllegalStateException("Sequence not in PPQ mode, can't write click track."));
			}

			try {
				ShortMessage sm = new ShortMessage();
				sm.setMessage(
					ShortMessage.PROGRAM_CHANGE,
					clickChannelNum,
					0,
					0);
				MidiEvent me = new MidiEvent(sm, 1);
				soundTrack.add(me);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
				return 0;
			}

			clickLoop : for (int i = 0;; i++) {

				evtTime = i * sequence.getResolution();
				if (evtTime >= timeLimit)
					break clickLoop;
				int clickNote =
					(i % preCountNum == 0 ? clickNoteStrong : clickNoteWeak);
				int clickVelo =
					(i % preCountNum == 0 ? clickVeloStrong : clickVeloWeak);
				ShortMessage sm;
				try {
					sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_ON,
						clickChannelNum,
						clickNote,
						clickVelo);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
					return 0;
				}
				if (evtTime <= 1)
					evtTime = 2;
				MidiEvent me = new MidiEvent(sm, evtTime);
				soundTrack.add(me);
				try {
					sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_OFF,
						clickChannelNum,
						clickNote,
						0);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
					return 0;
				}
				me = new MidiEvent(sm, evtTime + 100);
				soundTrack.add(me);
				//			System.out.println("Sequencing.writeClicksTo() click "+ i +" written.");
			}
			System.out.println(
				"Sequencing.writeClicksTo() evtTime: " + evtTime);
			clickTime = evtTime;
			return evtTime;
		}
		return 0L;
	}
	
	/**
	 * Sets a sequence of beats to the click track with the help of 
	 * midi-events.
	 * @param beatSequence a sequence of beats
	 * @param alwaysStrong if true all beats will interpreted as strong
	 * @return the time of the last beat
	 */
	public long writeClicksToClickTrack(Container beatSequence) throws IllegalStateException {
		if (isMidiAvailable() && beatSequence.size() > 0) {
			
			if (sequence.getDivisionType() != Sequence.PPQ) {
				throw (
					new IllegalStateException("Sequence not in PPQ mode, can't write click track."));
			}
	
			// In order to write into the clickTrack send a program change 
			try {
				ShortMessage sm = new ShortMessage();
				sm.setMessage(
					ShortMessage.PROGRAM_CHANGE,
					clickChannelNum,
					0,
					0);
				MidiEvent me = new MidiEvent(sm, 1);
				clickTrack.add(me);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
				return 0;
			}

			// The time of the current beat 
			long beatTime = 0;
			// The beat level
			double beatLevel = 0; 

			// The note of the click event
			int clickNote = 0;
			// The velocity of the click event
			int clickVelo = 0;
			
			// The short MIDI message to create
			ShortMessage sm = null;
			// The MIDI event to create
			MidiEvent me = null;

			// Write each beat marker into the clickTrack
			Iterator beatIter = beatSequence.iterator();	
			while(beatIter.hasNext()) {
	
				// Get the beat
				BeatMarker beat = (BeatMarker) beatIter.next();
				beatTime = beat.getTime();
				beatLevel = beat.getBeatLevel();
				
				if (beatLevel == 2) {
					clickNote = clickNoteStrong;
					clickVelo = clickVeloStrong;
				} else {
					clickNote = clickNoteWeak;
					clickVelo = clickVeloWeak;
				}
				
				// Create the begin of the short MIDI message
				try {
					sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_ON,
						clickChannelNum,
						clickNote,
						clickVelo);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
					return 0;
				}
				
				if (beatTime <= 1)
					beatTime = 2;

				// Create the MIDI event
				me = new MidiEvent(sm, (long) (beatTime * ticksPerMillis));
				
				// Add the MIDI event to the track
				clickTrack.add(me);
				
				// Create the end of the short MIDI message
				try {
					sm = new ShortMessage();
					sm.setMessage(
						ShortMessage.NOTE_OFF,
						clickChannelNum,
						clickNote,
						0);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
					return 0;
				}
				
				// Create the MIDI event
				me = new MidiEvent(sm, (long) (beatTime * ticksPerMillis) + 100);
				
				// Add the MIDI event to the track
				clickTrack.add(me);
					// System.out.println("Sequencing.writeClicksTo(Container): click added at "+beatTime+" for beat level "+beatLevel);
			} // end-while
			
			clickTime = beatTime;
			return beatTime;
		}
		
		return 0L;
	}

	/**
	 * @see de.uos.fmt.musitech.time.TimeKeeper#addTimeable(de.uos.fmt.musitech.data.structure.Timeable)
	 */
	@Override
	public void addTimeable(Timeable timeable) {
		if (timeable != null && timeablesVector.indexOf(timeable) == -1)
			timeablesVector.add(timeable);
	}

	/**
	 * @see de.uos.fmt.musitech.time.TimeKeeper#removeTimeable(de.uos.fmt.musitech.data.structure.Timeable)
	 */
	@Override
	public void removeTimeable(Timeable timeable) {
		timeablesVector.remove(timeable);
	}

	/**
	 * The TimeThread tells the Time to all Timeables.
	 */
	class TimeThread extends Thread {

		boolean running = false;
		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			running = true;
			while (isPlaying()) {
				long timeMillis = System.currentTimeMillis() - startTime;
				for (Iterator iter = timeablesVector.iterator();
					iter.hasNext();
					) {
					Timeable timeable = (Timeable) iter.next();
					timeable.setTimePosition(timeMillis);
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			running = false;
		}
	}

	/**
	 * @see de.uos.fmt.musitech.time.TimeKeeper#getTimePosition()
	 */
	@Override
	public long getTimePosition() {
		return sequencer.getMicrosecondPosition() / 1000;
	}

	/**
	 * @see de.uos.fmt.musitech.time.TimeKeeper#setTimePosition(long)
	 */
	@Override
	public void setTimePosition(long timeMillis) {
		sequencer.setMicrosecondPosition(timeMillis * 1000);
		sendTime(timeMillis);
	}

	/**
	 * @see de.uos.fmt.musitech.time.TimeKeeper#addContainer(de.uos.fmt.musitech.data.structure.Container)
	 */
	@Override
	public void setContainer(Container cont) {
		MidiNoteSequence mns = MidiNoteSequence.convert(cont);
		setSequence(null);
		mns.noteToMidi(getSequence());

	}

}