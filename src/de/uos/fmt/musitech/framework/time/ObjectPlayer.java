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
 * Created on 08.10.2003
 */
package de.uos.fmt.musitech.framework.time;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.JPanel;

import de.uos.fmt.musitech.audio.AFOPlayer;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeListener;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.performance.midi.MidiTee;
import de.uos.fmt.musitech.performance.midi.gui.KeyPiano;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * The ObjectPlayer manages MIDI playback and recording.
 * 
 * @author Jan Kramer, Tillman Weyde
 */
public class ObjectPlayer implements Player, PropertyChangeListener, DataChangeListener {

	private MidiPlayer midiPlayer = MidiPlayer.getInstance();
	private ObjectToMidiProcessor o2MProcessor = new ObjectToMidiProcessor();
	private AFOPlayer audioPlayer = new AFOPlayer();
	private MidiRecorder midiRecorder = new MidiRecorder();
	private Container<?> container;
	private PlayTimer playTimer = PlayTimer.getInstance();
	private Receiver receiver;
	private MidiDevice outDevice;
	private MidiOutSelector midiOutSelector;

	/**
	 * This thread fills MidiEvents into the RingBuffer.
	 */
	private Thread midiPlayThread = new Thread("midiPlayThread") {

		public void run() {
			while (!finalized) {
				o2MProcessor.fillRingbuffer();

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};
	private boolean finalized = false;

	static ObjectPlayer objectPlayer = new ObjectPlayer();

	/**
	 * TODO comment
	 */
	private ObjectPlayer() {
		registerPlayers();
		// setDefaultReceiver();

		getMidiOutSelector();

		midiPlayThread.start();
	}

	/**
	 * TODO comment
	 * 
	 * @param cont
	 */
	// public ObjectPlayer(Container cont) {
	// this();
	// setContainer(cont);
	// getMidiOutSelector();
	// }
	public static ObjectPlayer getInstance() {
		return objectPlayer;
	}

	/**
	 * Registers the standard players for audio and MIDI.
	 */
	public void registerPlayers() {
		playTimer.registerPlayer(audioPlayer);
		playTimer.registerPlayer(midiRecorder);
		playTimer.registerPlayer(this);
		// not needed anymore, because midiPlayer is registered at
		// MetronomPlayTimer
		// playTimer.registerPlayer(midiPlayer);
		try {
			midiPlayer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void unregisterPlayers() {
		playTimer.unRegisterPlayer(audioPlayer);
		playTimer.unRegisterPlayer(midiRecorder);
		playTimer.unRegisterPlayer(this);
		playTimer.unRegisterPlayer(midiPlayer);
		midiPlayer.close();
	}

	public void setContainer(Container<?> newContainer) {
		clear();
		container = newContainer;
		DataChangeManager.getInstance().interestExpandObject(this, newContainer);
		DataChangeManager.getInstance().interestExpandElements(this, newContainer);
		// o2MProcessor = new ObjectToMidiProcessor(midiPlayer, container);
		updateContainer();
		audioPlayer.setContainer(newContainer);
	}

	private void updateContainer() {

		o2MProcessor.setContainer(container);
		playTimer.computeEnd();
		DataChangeManager.getInstance().interestExpandObject(this, container);
	}

	/**
	 * TODO add comment
	 */
	public void clear() {
		if (o2MProcessor != null)
			o2MProcessor.clear();
		// midiPlayer.getRingBuffer().clearBuffer();
		if (afoPlayer != null)
			afoPlayer.clearAudioFileObjects();
		// container = null;
		// playTimer.computeEnd();
	}

	// public void stopPlaying() {
	// playTimer.stop();
	// }

	public void startRecording(java.awt.Component keyComponent) {
		if (DebugState.DEBUG)
			System.out.println("startRecording in Object Player");
		KeyPiano record = new KeyPiano(keyComponent);

		record.setReceiver(new MidiTee(midiRecorder, midiPlayer.getReceiver()));
		midiRecorder.setRecord(true);
		playTimer.start();

	}

	public void stopRecording() {

	}

	/**
	 * Returns a MidiOutSelector to choose your MidiDevice to use
	 * 
	 * @return
	 */
	public JPanel getControlPanel() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		jPanel.add(getMidiOutSelector(), BorderLayout.CENTER);
		return jPanel;
	}

	private JPanel getMidiOutSelector() {
		if (midiOutSelector == null) {

			midiOutSelector = new MidiOutSelector();
			midiOutSelector.setLabelText("ObjectPlayer");
			midiOutSelector.registerChangeListener(this);
			midiOutSelector.getDefaultReceiver();

		}
		return midiOutSelector;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#setTimePosition(long)
	 */
	public void setTimePosition(long millis) {
		if (o2MProcessor != null)
			;
		o2MProcessor.setTimeMicros(millis);
	}

	/**
	 * @return
	 */
	public PlayTimer getPlayTimer() {
		return playTimer;
	}

	public Transmitter getTransmitter() {
		Transmitter transmitter = null;
		try {
			transmitter = midiPlayer.getTransmitter();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		return transmitter;
	}

	public void setReceiver(Receiver receiver) {
		midiPlayer.setReceiver(receiver);
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public void setOutDevice(MidiDevice device) {
		try {
			receiver = device.getReceiver();

		} catch (MidiUnavailableException e2) {
			e2.printStackTrace();
			return;
		}
		if (DebugState.DEBUG) {
			System.out.println("MidiDevice: " + outDevice);
			System.out.println("receiver: " + receiver);
		}
		midiPlayer.setReceiver(receiver);
	}

	// public void setTransmitter(Component comp) {
	// KeyPiano keyPiano = new KeyPiano(comp);
	// midiPlayer.setTransmitter(keyPiano);
	// }

	public MidiRecorder getMidiRecorder() {
		return midiRecorder;
	}

	public ObjectToMidiProcessor getRingBuffer() {
		return o2MProcessor;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#start()
	 * @deprecated TODO use this.getPlayTimer().start() instead
	 */
	public void start() {
		// o2MProcessor.fillRingbuffer();
		if (DebugState.DEBUG)
			System.out.println("ObjectPlayer.start()");

	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#stop()
	 * @deprecated TODO use this.getPlayTimer().stop() instead
	 */
	public void stop() {
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#reset()
	 */
	public void reset() {
		if (o2MProcessor != null)
			o2MProcessor.setTimeMicros(0);
		o2MProcessor.fillRingbuffer();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#setPlayTimer(de.uos.fmt.musitech.framework.time.PlayTimer)
	 */
	public void setPlayTimer(PlayTimer timer) {
		playTimer = timer;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#getEndTime()
	 */
	public long getEndTime() {
		if (container != null) {
			// return container.getDuration();
			long duration = container.getDuration();
			if (container.getTime() != Timed.INVALID_TIME && duration >= 0) {
				return container.getTime() + duration;
			} else
				return Timed.INVALID_TIME;
		} else if (o2MProcessor != null)
			return o2MProcessor.getLastTimeStamp();
		else {
			// TODO: get real end
			return 100000000;
		}
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "newMidiOut") {
			receiver = (Receiver) evt.getNewValue();
			setReceiver(receiver);
		}
	}

	/**
	 * TODO add comment
	 * 
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		finalized = true;
		super.finalize();
	}

	/**
	 * Sets the play time of the <code>playTimer</code> to the start time of
	 * the <code>container</code>.
	 */
	public void setContainerTime() {
		if (container != null) {
			if (container.getTime() != Timed.INVALID_TIME) {
				playTimer.setPlayTimeMicros(container.getTime());
			}
		}
	}

	AFOPlayer afoPlayer;

	public void addAudioFileObject(AudioFileObject afo) {
		if (afoPlayer != null) {
			afoPlayer.getFISAFOConnecter().clearAudioFileObjects();
		} else {
			afoPlayer = new AFOPlayer();
		}
		afoPlayer.addAudioFileObject(afo);
		playTimer.registerPlayer(afoPlayer);
	}

	public void removeAFOPlayer() {
		if (afoPlayer != null)
			playTimer.unRegisterPlayer(afoPlayer);
	}

	/**
	 * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
	 */
	public void dataChanged(DataChangeEvent e) {
		System.out.println("ObjectPlayer: dataChanged");
		DataChangeManager.getInstance().interestExpandElements(this, container);
		updateContainer();
	}

}