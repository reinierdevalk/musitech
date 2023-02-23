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
 * Created on 11.07.2004
 */
package de.uos.fmt.musitech.framework.time;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.uos.fmt.musitech.utility.DebugState;

/**
 * @author Jan
 *
 */
public class MidiOutSelector extends JPanel {

	MidiDevice outDevice;
	Receiver receiver;
	PlayTimer playTimer;
	JComboBox outDeviceCombo;
	ObjectPlayer objectPlayer;
	JLabel label = new JLabel();

	/**
	 * 
	 */
	public MidiOutSelector() {
		super();
		init();
	}

	/**
	 * 
	 */
	private void init() {

		setLayout(new GridLayout(2, 1));
		add(getLabel());
		add(getOutDeviceCombo());
		changeListener = new ArrayList();
//		getDefaultReceiver();

	}

	/**
	 * Find default receiver in following order
	 * 1. Search for Wavetable
	 * 2. Search for Synthesizer, but not Java
	 * 3. Search for something, but not Java
	 * 4. Take first available receiver 
	 * 
	 */
	public void getDefaultReceiver() {
		MidiDevice.Info ainfos[] = MidiSystem.getMidiDeviceInfo();
		MidiDevice.Info device = null;
		for (int i = 0; i < ainfos.length; i++) {

			//	We want to have Wavetable
			if (ainfos[i].toString().indexOf("Wavetable") != -1) {

				System.out.println(
					"Index of Wavetable"
						+ ainfos[i].toString().indexOf("Wavetable"));
				device = ainfos[i];
				getOutDeviceCombo().setSelectedIndex(i);
				break;
			}
		}

		if (device == null) {
			// no wavetable found, search for Synthesizer, 
			// but not for Java Synthesizer
			for (int i = 0; i < ainfos.length; i++) {
				if (ainfos[i].toString().indexOf("Synthesizer") != -1
					&& ainfos[i].toString().indexOf("Java") == -1) {
					//
					device = ainfos[i];
					getOutDeviceCombo().setSelectedIndex(i);
					break;
				}
			}

		}
		if (device == null) {
			//	no synthesizer found, search for something, beside
			// Java
			for (int i = 0; i < ainfos.length; i++) {
				if (ainfos[i].toString().indexOf("Java") == -1) {

					System.out.println(
						"Index of Wavetable"
							+ ainfos[i].toString().indexOf("Synthesizer"));
					device = ainfos[i];
					getOutDeviceCombo().setSelectedIndex(i);
					break;
				}
			}

		}
		//		we didn't find anything
		// we take the first
		if (device == null) {
			device = ainfos[0];
		}
		setOutDeviceInfo(device);
	}

	private JLabel getLabel() {
		if (label == null) {			
			label = new JLabel("MidiOut");
		}
		return label;
	}

	public void setLabelText(String newText) {
		label.setText("MidiOut " + newText);
	}

	private JComboBox getOutDeviceCombo() {
		if (outDeviceCombo == null) {
			outDeviceCombo = new JComboBox();
			outDeviceCombo.setName("OutDeviceCombo");
			MidiDevice.Info ainfos[] = MidiSystem.getMidiDeviceInfo();
			for (int i = 0; i < ainfos.length; i++) {
				MidiDevice md;
				try {
					md = MidiSystem.getMidiDevice(ainfos[i]);
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
					break;
				}
				if (md.getMaxReceivers() != 0)
					outDeviceCombo.addItem(ainfos[i]);
				// default Receiver

			}
			outDeviceCombo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setOutDeviceInfo(
						(javax.sound.midi.MidiDevice.Info) outDeviceCombo
							.getSelectedItem());
				}
			});
		}
		return outDeviceCombo;

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (21.11.2001 02:22:40)
	 * @return javax.sound.midi.MidiDevice.Info
	 */
	public void setOutDeviceInfo(MidiDevice.Info info) {
		if (info == null)
			return;
		try {
			MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
			if (midiDevice.getMaxReceivers() != 0)
				setOutDevice(midiDevice);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void notifyListener(Receiver oldRec, Receiver newRec) {
		for (Iterator iter = changeListener.iterator(); iter.hasNext();) {
			PropertyChangeListener listener =
				(PropertyChangeListener) iter.next();
			PropertyChangeEvent changeEvent =
				new PropertyChangeEvent(this, "newMidiOut", oldRec, newRec);
			listener.propertyChange(changeEvent);
		}
	}

	/**
	 * Den Synthesizer setzen.
	 * @param newSequence javax.sound.midi.Sequence
	 */
	public void setOutDevice(javax.sound.midi.MidiDevice newOutDevice) {
		if (outDevice == newOutDevice){
			return;
		}
		if (outDevice != null && outDevice.isOpen())
			outDevice.close();
		try {
			newOutDevice.open();
		} catch (MidiUnavailableException e) {
			System.out.println("MidiOutSelector: Could not open OutDevice");
			e.printStackTrace();
			setOutDevice(outDevice);
			return;
		}
		if (playTimer != null)
			playTimer.stop();
		outDevice = newOutDevice;
		getNewReceiver(outDevice);
		if(DebugState.DEBUG)
			System.out.println("setOutDevice: " + outDevice.getDeviceInfo());
	}

	public MidiDevice getOutDevice() {
		return outDevice;
	}

	public void getNewReceiver(MidiDevice device) {
		Receiver rec = null;
		try {
			rec = device.getReceiver();
		} catch (MidiUnavailableException e) {
			System.out.println(
				"MidiOutSelector: Could not get Receiver from "
					+ getOutDevice());
			e.printStackTrace();
		}
		if (rec != null) {
			notifyListener(receiver, rec);
			receiver = rec;
		}

	}

	ArrayList changeListener;

	public void registerChangeListener(PropertyChangeListener pCListener) {
		changeListener.add(pCListener);
	}

	public void removeChangeListener(PropertyChangeListener pCListener) {
		changeListener.remove(pCListener);
	}

	/**
	 * @return
	 */
	public ObjectPlayer getObjectPlayer() {
		return objectPlayer;
	}

	/**
	 * @param player
	 */
	public void setObjectPlayer(ObjectPlayer player) {
		objectPlayer = player;
	}

}
