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
 * MidiPlayer.java
 */

package de.uos.fmt.musitech.framework.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.sound.midi.Sequencer.SyncMode;

import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.RingBuffer;

/**
 * A player that delivers time-stamped MIDI events from a buffer in real time.
 * 
 * @author Tillman Weyde and Jan Kramer
 */
public class MidiPlayer implements MidiDevice, Runnable, Transmitter, Player {

	private boolean settingTime;

	private boolean open = false;

	boolean[][] pressed = new boolean[16][256];

	private Receiver receiver;

	private BitSet m_muteBitSet;

	private BitSet m_soloBitSet;

	/**
	 * Contains the enabled state of the tracks. This BitSet holds the
	 * pre-calculated effect of mute and solo status.
	 */
	private BitSet m_enabledBitSet;

	private BitSet recordBitSet;

	private Set m_metaListeners = new TreeSet();

	/**
	 * The listeners that want to be notified of control change events. They are
	 * organized as follows: this array is indexed with the number of the
	 * controller change events listeners are interested in. If there is any
	 * interest, the array element contains a reference to a Set containing the
	 * listeners. These sets are allocated on demand.
	 */
	private Set[] m_aControllerListeners;

	// This is for use in Collection.toArray(Object[]).
	// private static final SyncMode[] EMPTY_SYNCMODE_ARRAY = new SyncMode[0];

	// old Variable
	private static final SyncMode[] MASTER_SYNC_MODES = {SyncMode.INTERNAL_CLOCK};

	private static final SyncMode[] SLAVE_SYNC_MODES = {SyncMode.NO_SYNC};

	private boolean midiTempoChangesEnabled = true;

	javax.sound.midi.Transmitter transmitter;

	// public javax.sound.midi.MidiDevice inDevice = null;

	private PlayTimer playTimer;

	private RingBuffer metronomBuffer = new RingBuffer(100);

	// private Receiver m_receiver;
	private Thread thread;

	// For debugging
	static final boolean DEBUG = false;

	static final boolean SDEBUG = false;

	private boolean DELIVER_DEBUG = false;

	// private ObjectPlayer objectPlayer; // needed to get Ringbuffer in run()

	private static MidiPlayer instance = new MidiPlayer();

	public static MidiPlayer getInstance() {
		if (!instance.isOpen()) {
			try {
				instance.open();
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	// /**
	// * ObjectPlayer needed to get RingBuffer
	// *
	// * @param objectPlayer
	// */
	// private MidiPlayer(ObjectPlayer newObjectPlayer) {
	// this();
	// objectPlayer = newObjectPlayer;
	// }

	private MidiPlayer() {
		this(Arrays.asList(MASTER_SYNC_MODES), Arrays.asList(SLAVE_SYNC_MODES));
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.<init>(): begin");
		}
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.<init>(): end");
		}
	}

	private MidiPlayer(Collection masterSyncModes, Collection slaveSyncModes) {

		playing = false;

		m_aControllerListeners = new Set[128];
		// m_masterSyncModes = masterSyncModes;
		// m_slaveSyncModes = slaveSyncModes;
		// if (getMasterSyncModes().length > 0) {
		// m_masterSyncMode = getMasterSyncModes()[0];
		// }
		// if (getSlaveSyncModes().length > 0) {
		// m_slaveSyncMode = getSlaveSyncModes()[0];
		// }
		m_muteBitSet = new BitSet();
		m_soloBitSet = new BitSet();
		m_enabledBitSet = new BitSet(16);

		updateEnabled();
	}

	/**
	 * MidiPlayer wird in einem neuen Thread mit höchster Priorität gestartet.
	 */
	protected void openImpl() {
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.openImpl(): begin");
		}
		if (thread == null)
			thread = new Thread(this, "MidiPlayer");
		thread.setPriority(Thread.MAX_PRIORITY);
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.openImpl(): starting thread");
		}
		open = true;
		if (!thread.isAlive())
			thread.start();
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.openImpl(): end");
		}

	}

	/**
	 * Stopped den Thread MidiPlayer.
	 */
	protected void closeImpl() {
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.closeImpl(): begin");
		}
		if (playing) {
			stop();
			// TODO: wait for real stopped state
		}
		if (DEBUG)
			System.out.println(">>>MidiPlayer: Timer stopped");

		// now the thread should terminate
		thread = null;
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.closeImpl(): end");
		}
		open = false;
	}

	protected void startImpl() {
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.startImpl(): begin");
		}
		// TODO: move to base class?
		if (!isOpen()) {
			System.out.println("Midi Device is not open");
		}

		synchronized (this) {
			this.notifyAll();
		}
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.startImpl(): end");
		}

	}

	public void print(MidiEvent event) {
		if (event.getMessage() instanceof ShortMessage) {
			ShortMessage shortmes = (ShortMessage) event.getMessage();
			if (DEBUG && (shortmes.getStatus() >= 0xC0 && shortmes.getStatus() <= 0xCF)) {

				System.out.println("Midi Player: Channel: " + shortmes.getChannel() + " data1: "
									+ shortmes.getData1() + " data2: " + shortmes.getData2()
									+ " status: " + shortmes.getStatus());
			}
		}

	}

	/**
	 * This method contains the main loop for delivering MIDI events.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		if (DEBUG) {
			System.out.println(">>>MidiPlayer.run(): begin");
		}
		while (isOpen()) {
			synchronized (this) {
				while (!playing) {
					if (DEBUG) {
						// System.out.println(
						// ">>>MidiPlayer.run(): waiting to run");
					}
					try {
						if (!playing)
							this.wait();
					} catch (InterruptedException e) {
						if (DEBUG) {
							System.out.println(e);
						}
					}
				}
			}
			if (DEBUG) {
				System.out.println(">>>MidiPlayer.run(): now running");
			}

			while (playing) {
				MidiEvent bufferEvent = (MidiEvent) buffer.get();
				MidiEvent metroEvent = (MidiEvent) metronomBuffer.get();
				if (DELIVER_DEBUG) {
					System.out.println("MidiPlayer.run(): buffer.get" + "timeMillis: "
										+ playTimer.getPlayTimeMicros());
				}
				MidiEvent eventToDeliver = null;
				if (bufferEvent != null && metroEvent != null) {
					if (bufferEvent.getTick() < metroEvent.getTick()) {
						eventToDeliver = (MidiEvent) buffer.read();
						if (DELIVER_DEBUG) {
							System.out.println("MidiPlayer.run(): buffer");
						}
					} else {
						eventToDeliver = (MidiEvent) metronomBuffer.read();
						if (DELIVER_DEBUG) {
							System.out.println("MidiPlayer.run(): metronomBuffer" + "timeMillis: "
												+ playTimer.getPlayTimeMicros());
						}
					}
					if (deliverEvent(eventToDeliver.getMessage(), eventToDeliver.getTick())) {
						// print(deliverEvent);
						rememberNoteOn(eventToDeliver);
					}
				}
				if ((bufferEvent != null ^ metroEvent != null)) {
					if (bufferEvent == null) {
						eventToDeliver = (MidiEvent) metronomBuffer.read();
					} else {
						eventToDeliver = (MidiEvent) buffer.read();
					}
					if (deliverEvent(eventToDeliver.getMessage(), eventToDeliver.getTick()))
						// print(deliverEvent);
						rememberNoteOn(eventToDeliver);
				}
				if (bufferEvent == null && metroEvent == null) {
					if (DEBUG)
						System.out.println("Midiplayer.run: No Event to play");
					// wait to get new MidiEvent
					try {
						synchronized (this) {
							this.wait();
						}
					} catch (InterruptedException e) {
						// e.printStackTrace();
					}
				}
			} // while (playing)

			// if (DEBUG)
			// System.out.println("MidiPlayer.run: out of while playing");
		} // while (isOpen())
		if (DEBUG) {
			System.out.println(">>>MidiPlayer.run(): end");
		}
	}

	public void rememberNoteOn(MidiEvent deliverEvent) {
		if (deliverEvent.getMessage() instanceof ShortMessage
			// to remember pressPoint keys
			&& deliverEvent.getMessage().getStatus() >= ShortMessage.NOTE_ON
			&& deliverEvent.getMessage().getStatus() <= ShortMessage.NOTE_ON + 15) {
			if (DELIVER_DEBUG)
				System.out.println("MidiPlayer.run: note on, pitch: "
									+ deliverEvent.getMessage().getMessage()[1]);
			pressed[deliverEvent.getMessage().getStatus()
			// channel
					- ShortMessage.NOTE_ON][deliverEvent // pitch
					.getMessage().getMessage()[1]] = true;

		}
		// note on ch 1-15
		if (deliverEvent.getMessage() instanceof ShortMessage
			&& deliverEvent.getMessage().getStatus() >= ShortMessage.NOTE_OFF
			&& deliverEvent.getMessage().getStatus() <= ShortMessage.NOTE_OFF + 15) {
			if (DELIVER_DEBUG)
				System.out.println("MidiPlayer.run: note off,pitch: "
									+ deliverEvent.getMessage().getMessage()[1]);
			pressed[deliverEvent.getMessage().getStatus()
			// channel
					- ShortMessage.NOTE_OFF][deliverEvent // pitch
					.getMessage().getMessage()[1]] = false;

		}

	}

	// RingBuffer is uses to buffer MidiEvents
	// Alle MidiEvents are put into the RingBuffer
	// You get next MidiEvent with biffer.read()
	// uses in run()
	RingBuffer buffer = new RingBuffer(100);

	/**
	 * This is the buffer from which the MidiPlayer reads events. It an be used
	 * to feed events to the player in while playing.
	 * 
	 * @return The RingBuffer
	 */
	public RingBuffer getRingBuffer() {
		return buffer;
	}

	// If the message has to be delivered in a short time (in milliseconds)
	final long ACTIVEWAITING = 5;

	// For waiting
	private long timeToWait = 0;

	private long nextEventMicros;

	// lScheduledTime is in milliseconds
	private boolean deliverEvent(MidiMessage message, long lScheduledTime) {
		// if (DELIVER_DEBUG) {
		// System.out.println(">>>MidiPlayer.deliverEvent(): begin"); }
		boolean delivered = false;
		nextEventMicros = (lScheduledTime);

		if (DELIVER_DEBUG) {
			System.out.println(">>>timeToWait is " + timeToWait / 1000 + ", nextEvent: "
								+ lScheduledTime / 1000 + " getTime: "
								+ playTimer.getPlayTimeMicros() / 1000);
		}
		do {
			// time before next Event
			timeToWait = nextEventMicros - playTimer.getPlayTimeMicros();

			// if (DELIVER_DEBUG)
			// System.out.println(
			// "deliver Event: sleepTime: "
			// + Math.min(1000000, timeToWait / 1000 - ACTIVEWAITING)
			// + " timeToWait: "
			// + timeToWait / 1000
			// + " ms");
			// sleep at most 200 ms
			long sleepTime = Math.min(200, timeToWait / 1000 - ACTIVEWAITING);

			// sleep if time left
			if (sleepTime > 0) {
				try {
					if (!playing) {
						if (DEBUG)
							System.out.println("del.Event: exit, because not playing");
						break;
					}
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {

					if (DELIVER_DEBUG) {
						System.out.println(e);
					}

					System.out.println(">>>Interrupted in deliverEvent");
					return false;
				}
			} else {
				// time to work
				if (DELIVER_DEBUG) {
					System.out.println(">>>Leaving while-loop because timeToWait is " + timeToWait
										/ 1000 + "ms " + "playTime: "
										+ playTimer.getPlayTimeMicros() / 1000 + " ms");
				}
				break;
			}

		} while (playing && !settingTime);
		if (DELIVER_DEBUG) {
			System.out.println(">>>sleep(0) because timeToWait is " + timeToWait / 1000 + " in ms");
		}
		// Active waiting now, i.e. do not sleep until event is delivered.
		while (playTimer.getPlayTimeMicros() < nextEventMicros && !settingTime) {
			try {
				// Von 0 auf 1 gesetzt! Jetzt reagiert das System besser,
				// dafuer wird das timing schlechter.
				Thread.sleep(0, 100);
			} catch (InterruptedException e) {
				if (DELIVER_DEBUG) {
					System.out.println(e);
				}
				System.out.println(">>>Interrupted in deliverEvent");
			}

		}
		if (DELIVER_DEBUG) {
			System.out.println(" sendImpl: nextEvent-getPlayTime "
								+ (lScheduledTime - playTimer.getPlayTimeMicros()) / 1000 + " ms");
		}
		if (!settingTime) {
			sendImpl(message, -1);
			delivered = true;
		}
		if (DELIVER_DEBUG) {
			System.out.println(">>>MidiPlayer.sending message at " + playTimer.getPlayTimeMicros()
								/ 1000 + "ms (should be) " + lScheduledTime / 1000 + " ms");
			System.out.println();

		}

		// sendImpl(message, event.getTick());
		notifyListeners(message);
		settingTime = false;
		// System.out.println(">>>MidiPlayer.deliverEvent(): end");
		return delivered;

	}

	/**
	 * @param message
	 * @param i
	 */
	private void sendImpl(MidiMessage message, int lTimeStamp) {
		receiver.send(message, lTimeStamp);

	}

	/**
	 * Switches off all sounding notes in two steps
	 * <ol>
	 * <li>sending the MIDI command <i>all notes off</i></li>
	 * <li>in case all note off does not work, by sending a MIDI <i>note off</i>
	 * command for every note that is currently sounding.</li>
	 * </ol>
	 */
	public void allNotesOff() {
		ShortMessage sm = new ShortMessage();
		if (DEBUG) {
			System.out.println("try all notes off");
		}
		try {
			sm.setMessage(0xBF, 0x78, 0x00);
			// sm.setMessage(176, 120, 0);
			if (DEBUG)
				System.out.println("\"All notes off\" was sent");
		} catch (InvalidMidiDataException e) {

			e.printStackTrace();
		}
		sendImpl(sm, -1);
		// while (settingTime){
		// System.out.println("setting time in note of");
		// }
		for (int channel = 0; channel < 16; channel++) {
			for (int pitch = 0; pitch < 128; pitch++) {
				if (pressed[channel][pitch]) {
					try {
						sm.setMessage(ShortMessage.NOTE_OFF, channel, pitch, 0);
						// note off
					} catch (InvalidMidiDataException e1) {

						e1.printStackTrace();
					}
					sendImpl(sm, -1);
					pressed[channel][pitch] = false;
				}
			}
		}
	}

	/**
	 * Returns vector of midi-in device infos, not including the Sequencers and
	 * Inputs that do not have transmitters.
	 * 
	 * @return MidiDevice.Info[]
	 */
	MidiDevice.Info[] getMidiInputs() {
		MidiDevice.Info mdi[] = MidiSystem.getMidiDeviceInfo();
		List<MidiDevice.Info> vec = new ArrayList<MidiDevice.Info>();
		for (int i = 0; i < mdi.length; i++) {
			MidiDevice md = null;
			try {
				md = MidiSystem.getMidiDevice(mdi[i]);
				if (md instanceof Sequencer)
					continue;
				if (md.getMaxTransmitters() != 0)
					vec.add(mdi[i]);
			} catch (Exception e) {
				System.out.println("Sequencing.getMidiInputs() " + e);
			} finally {
				if (md != null)
					md.close();
			}
		}
		return vec.toArray(new MidiDevice.Info[] {});
	}

	protected void setTransmitter(javax.sound.midi.Transmitter newTransmitter) {
		if (transmitter != null)
			transmitter.close();
		transmitter = newTransmitter;
	}

	// /**
	// * Sets a new midi-in-device
	// *
	// * @date (16.04.2001 19:50:34)
	// * @param newInDevice javax.sound.midi.MidiDevice
	// */
	// public void setInDevice(MidiDevice newInDevice) {
	// if (inDevice != null && inDevice.isOpen())
	// inDevice.close();
	// MidiDevice oldDevice = inDevice;
	// inDevice = newInDevice;
	// System.out.println("new inDevice: " + inDevice.getDeviceInfo());
	// try {
	// setTransmitter(inDevice.getTransmitter());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// /**
	// * Returns the prefered midi-input device.
	// *
	// * @return javax.sound.midi.Receiver
	// */
	// public javax.sound.midi.MidiDevice getInDevice() {
	// if (inDevice == null) {
	// try {
	// MidiDevice.Info mdi[] = getMidiInputs();
	// MidiDevice.Info prefDev = null;
	// for (int i = 0; i < mdi.length; i++) {
	// if (mdi[i].getName().startsWith("Wire:"))
	// prefDev = mdi[i];
	// }
	//
	// if (prefDev == null)
	// prefDev = mdi[0];
	// setInDevice(MidiSystem.getMidiDevice(prefDev));
	// } catch (Exception e) {
	// System.out.println("Sequencing.getOutDevice() " + e);
	// return null;
	// }
	// }
	// return inDevice;
	// }

	// /**
	// * @param pitch
	// * @param velo
	// */
	// private ShortMessage soundNoteOn(int pitch, int velo) {
	// ShortMessage sm = new ShortMessage();
	// try {
	// sm.setMessage(ShortMessage.NOTE_ON, 1, pitch, velo);
	// } catch (InvalidMidiDataException e1) {
	//
	// e1.printStackTrace();
	// }
	// sendImpl(sm, -1);
	// return sm;
	// }

	// /**
	// * @param pitch
	// * @param velo
	// */
	// private ShortMessage soundNoteOff(int pitch, int velo) {
	// ShortMessage sm = new ShortMessage();
	// try {
	// sm.setMessage(ShortMessage.NOTE_OFF, 1, pitch, velo);
	// } catch (InvalidMidiDataException e1) {
	//
	// e1.printStackTrace();
	// }
	// sendImpl(sm, -1);
	// return sm;
	// }

	// static protected Vector timeablesVector = new Vector();

	private boolean playing = false; // not used (playtimer instead)

	// /**
	// * @param timeable
	// * @see
	// de.uos.fmt.musitech.time.TimeKeeper#addTimeable(de.uos.fmt.musitech.data.structure.Timeable)
	// */
	// public void addTimeable(Timeable timeable) {
	// if (timeable != null && timeablesVector.indexOf(timeable) == -1)
	// timeablesVector.add(timeable);
	// }

	// /**
	// * @see
	// de.uos.fmt.musitech.time.TimeKeeper#removeTimeable(de.uos.fmt.musitech.data.structure.Timeable)
	// */
	// public void removeTimeable(Timeable timeable) {
	// timeablesVector.remove(timeable);
	// }

	public void start() {
		settingTime = false;
		playing = true;

		// lStartTime = ntimer.getTimeMillis() * 1000 - timePlayed;
		// playTimer.start();
		if (DebugState.DEBUG)
			System.out.println("MidiPlayer: start");
		startImpl();
	}

	/**
	 * Has no effect.
	 * 
	 * @see de.uos.fmt.musitech.framework.time.Player#reset()
	 */
	public void reset() {
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#stop()
	 */
	public void stop() {
		if (DebugState.DEBUG)
			System.out.println("MidiPlayer: stop()");
		// if (playing) {
		playing = false;
		thread.interrupt();

		// TODO: perhaps wait here?
		// try {
		// thread.wait();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		allNotesOff();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#setTimePosition(long)
	 */
	public void setTimePosition(long timeMillis) {
		// settingTime flag is used in delivering events to avoid hanging in
		// while loop
		settingTime = true;
	}

	public void programChange(int channel, int program) {
		ShortMessage sm = new ShortMessage();
		try {
			sm.setMessage(ShortMessage.PROGRAM_CHANGE, channel, program, 0);
		} catch (InvalidMidiDataException e) {

			e.printStackTrace();
		}

		sendImpl(sm, -1);
	}

	public void setMidiTempoChanges(boolean changes) {
		midiTempoChangesEnabled = changes;
		// setTimePosition(ticksToMillis(lastEventPlayed));
	}

	public boolean isMidiTempoChangesEnabled() {
		return midiTempoChangesEnabled;
	}

	/**
	 * Convenience method to convert a unsingende byte to an int.
	 * 
	 * @param num A byte to be interpreted as an unsigned number.
	 * @return The unsigend number represented by the byte parameter.
	 */
	public int byteToInt(byte num) {
		return num >= 0 ? num : 256 + num;
	}

	private void updateEnabled() {
		BitSet oldEnabledBitSet = (BitSet) m_enabledBitSet.clone();
		boolean bSoloExists = m_soloBitSet.length() > 0;
		if (bSoloExists) {
			m_enabledBitSet = (BitSet) m_soloBitSet.clone();
		} else {
			for (int i = 0; i < m_muteBitSet.size(); i++) {
				if (m_muteBitSet.get(i)) {
					m_enabledBitSet.clear(i);
				} else {
					m_enabledBitSet.set(i);
				}
			}
		}
		oldEnabledBitSet.xor(m_enabledBitSet);
		/*
		 * oldEnabledBitSet now has a bit set if the status for this bit
		 * changed.
		 */
		for (int i = 0; i < oldEnabledBitSet.size(); i++) {
			if (oldEnabledBitSet.get(i)) {
				setTrackEnabledImpl(i, m_enabledBitSet.get(i));
			}
		}

	}

	/**
	 * Shows that a track state has changed. This method is called for each
	 * track where the enabled state (calculated from mute and solo) has
	 * changed. The boolean value passed represents the new state.
	 * 
	 * @param nTrack The track number for which the enabled status has changed.
	 * @param bEnabled The new enabled state for this track.
	 */
	public void setTrackEnabledImpl(int nTrack, boolean bEnabled) {
		if (m_enabledBitSet.size() >= nTrack)
			m_enabledBitSet.set(nTrack, bEnabled);
	}

	public boolean isTrackEnabled(int nTrack) {
		return m_enabledBitSet.get(nTrack);
	}

	public void setRecEnable(int track, boolean enabled) {
		if (recordBitSet.size() >= track)
			recordBitSet.set(track, enabled);

	}

	public boolean isRecEnabled(int track) {
		return recordBitSet.get(track);
	}

	public void setVolume(int channel, int newVolume) {
		ShortMessage sm = new ShortMessage();
		try {
			sm.setMessage(ShortMessage.CONTROL_CHANGE, channel, 7, newVolume);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		sendImpl(sm, -1);
	}

	/**
	 * @param sm
	 * @param i
	 */
	private void sendImpl(ShortMessage sm, int i) {
		if (receiver != null)
			receiver.send(sm, -1);

	}

	protected void notifyListeners(MidiMessage message) {

		if (message instanceof MetaMessage) {
			// TODO: use extra thread for event delivery
			sendMetaMessage((MetaMessage) message);
		} else if (message instanceof ShortMessage
					&& ((ShortMessage) message).getCommand() == ShortMessage.CONTROL_CHANGE) {
			sendControllerEvent((ShortMessage) message);
		}
	}

	protected void sendControllerEvent(ShortMessage message) {
		// TDebug.out("TSequencer.sendControllerEvent(): called");
		int nController = message.getData1();
		if (m_aControllerListeners[nController] != null) {
			Iterator iterator = m_aControllerListeners[nController].iterator();
			while (iterator.hasNext()) {
				ControllerEventListener controllerEventListener = (ControllerEventListener) iterator
						.next();
				ShortMessage copiedMessage = (ShortMessage) message.clone();
				controllerEventListener.controlChange(copiedMessage);
			}
		}
	}

	protected void sendMetaMessage(MetaMessage message) {

		Iterator iterator = getMetaEventListeners();
		while (iterator.hasNext()) {
			MetaEventListener metaEventListener = (MetaEventListener) iterator.next();
			MetaMessage copiedMessage = (MetaMessage) message.clone();
			metaEventListener.meta(copiedMessage);
		}
	}

	protected Iterator getMetaEventListeners() {
		synchronized (m_metaListeners) {
			return m_metaListeners.iterator();
		}
	}

	/**
	 * @see javax.sound.midi.Sequencer#addControllerEventListener(javax.sound.midi.ControllerEventListener,
	 *      int[])
	 */
	public int[] addControllerEventListener(ControllerEventListener listener,
											int[] controllerNumbers) {
		for (int i = 0; i < controllerNumbers.length; i++) {
			if (m_aControllerListeners[controllerNumbers[i]] == null)
				m_aControllerListeners[controllerNumbers[i]] = new HashSet();
			m_aControllerListeners[controllerNumbers[i]].add(listener);
		}
		return controllerNumbers;
	}

	public void setPlayTimer(PlayTimer timer) {
		playTimer = timer;

	}

	/**
	 * @see javax.sound.midi.Transmitter#getReceiver()
	 */
	public Receiver getReceiver() {
		// Auto-generated method stub
		return receiver;
	}

	/**
	 * @see javax.sound.midi.Transmitter#setReceiver(javax.sound.midi.Receiver)
	 */
	public void setReceiver(Receiver newReceiver) {
		receiver = newReceiver;
	}

	/**
	 * @see javax.sound.midi.Transmitter#close()
	 */
	public void close() {
		// Auto-generated method stub

	}

	/**
	 * @see javax.sound.midi.MidiDevice#getMaxReceivers()
	 */
	public int getMaxReceivers() {
		return 0;
	}

	/**
	 * @see javax.sound.midi.MidiDevice#getMaxTransmitters()
	 */
	public int getMaxTransmitters() {
		// Auto-generated method stub
		return 0;
	}

	/**
	 * @see javax.sound.midi.MidiDevice#getMicrosecondPosition()
	 */
	public long getMicrosecondPosition() {
		return 0;
	}

	/**
	 * @see javax.sound.midi.MidiDevice#open()
	 */
	public void open() throws MidiUnavailableException {
		openImpl();
	}

	/**
	 * @see javax.sound.midi.MidiDevice#isOpen()
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @see javax.sound.midi.MidiDevice#getDeviceInfo()
	 */
	public Info getDeviceInfo() {
		return null;
	}

	/**
	 * @see javax.sound.midi.MidiDevice#getTransmitter()
	 */
	public Transmitter getTransmitter() throws MidiUnavailableException {
		return null;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#getEndTime()
	 */
	public long getEndTime() {

		return -1;
	}

	/**
	 * @return
	 */
	public RingBuffer getMetronomBuffer() {
		return metronomBuffer;
	}

	/**
	 * @param buffer
	 */
	public void setMetronomBuffer(RingBuffer buffer) {
		metronomBuffer = buffer;
	}

	/**
	 * @see javax.sound.midi.MidiDevice#getReceivers()
	 */
	public List getReceivers() {
		// TODO Auto-generated in order to fully implement JAVA 1.5 interface
		return null;
	}

	/**
	 * @see javax.sound.midi.MidiDevice#getTransmitters()
	 */
	public List getTransmitters() {
		// TODO Auto-generated in order to fully implement JAVA 1.5 interface
		return null;
	}

}

/** * MidiPlayer.java ** */
