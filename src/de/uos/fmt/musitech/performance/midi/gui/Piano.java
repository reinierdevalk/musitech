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
 * Created on 06.07.2004
 */
package de.uos.fmt.musitech.performance.midi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.midi.Receiver;
import javax.swing.JPanel;

import de.uos.fmt.musitech.framework.time.MidiOutSelector;
import de.uos.fmt.musitech.performance.midi.MidiTee;

/**
 * Class which uses Mouse and KeyPiano to play
 * Midi with Mouse or Keys
 *
 * @author Jan Kramer
 *
 */
public class Piano extends JPanel implements PropertyChangeListener	 {
	MousePiano mousePiano;
	KeyPiano keyPiano;
	Receiver receiver;

	public Piano(Receiver receiver) {
		this();
		setReceiver(receiver);

	}

	public Piano() {
		init();
		setLayout(new BorderLayout());
		add(mousePiano, BorderLayout.CENTER);
	}

	public void init() {
		mousePiano = new MousePiano(36, 37);
		keyPiano = new KeyPiano();
		keyPiano.setComponent(mousePiano);
		setPreferredSize(new Dimension(500, 200));
		
	}
	/**
	 * @return
	 */
	public Receiver getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver
	 */
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
		mousePiano.setReceiver(receiver);
		MidiTee midiTee = new MidiTee(receiver, mousePiano);
		keyPiano.setReceiver(midiTee);
	}

	public JPanel getControlPanel() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 1));
		controlPanel.add(keyPiano.getControlPanel());
		controlPanel.add(getMidiOutSelector());
		return controlPanel;
	}

	MidiOutSelector midiOutSelector;

	private JPanel getMidiOutSelector() {
		if (midiOutSelector == null) {
			midiOutSelector = new MidiOutSelector();
			midiOutSelector.setLabelText("Piano");
			midiOutSelector.registerChangeListener(this);
		}
		return midiOutSelector;
	}

	/** 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "newMidiOut"){
			Receiver receiver = (Receiver) evt.getNewValue();
			setReceiver(receiver);
		}
	}

}
