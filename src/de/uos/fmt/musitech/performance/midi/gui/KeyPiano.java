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
 * Created on 03.11.2003
 */
package de.uos.fmt.musitech.performance.midi.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.framework.editor.*;
import de.uos.fmt.musitech.utility.DebugState;

/**
* This Transmitter listens for KeyEvents and produces midi messages.
* You can use it polyphonically. <br>
* Use "Y"-"." for low white keys <br>
* Use "A"-"L" for low black keys <br>
* Use "Q"-"I" for high white keys <br>
* Use "1"-"9" for high black keys <br>
* Use Arrows up/down to transpose a octave <br>
* Use Arrows left/right to tranpose halftone <br>
* Use Page up/down for velocity change <br>
* Use Pos1/End for Program change <br>
* Use F1/F2 for midi channel change <br>
*/

public class KeyPiano
	extends KeyAdapter
	implements Transmitter, PropertyChangeListener {

	//private long pressed;
	private boolean[] isPressed = new boolean[132];

	// default parameters
	private int transpose = 0;
	private int channel = 1;
	private int velocity = 80;
	private int program = 0;
	private Receiver receiver;

	private JLabel transLabel = new JLabel();
	private JLabel velocityLabel = new JLabel();
	private JLabel channelLabel = new JLabel();
	private JSlider velocitySlider = new JSlider(0, 127);
	private JSpinner programSpinner;
	private IntMouseEditor editPanTrans; // to edit transpose
	private IntMouseEditor editPanVel; // to edit velocity
	private IntMouseEditor editPanPrg; // to edit Program
	private IntMouseEditor editPanCha; // to edit MidiChannel
	private boolean zySwitched;

	// Constructor
	public KeyPiano(Component component) {
		// dummy Receiver
		receiver = new Receiver() {
			@Override
			public void close() {
			}
			@Override
			public void send(MidiMessage message, long timeStamp) {
			}
		};
		component.addKeyListener(this);
	}

	public KeyPiano() {
		// dummy Receiver
		receiver = new Receiver() {
			@Override
			public void close() {
			}
			@Override
			public void send(MidiMessage message, long timeStamp) {
			}
		};

	}

	/**
	 * a component is needed to receive key Events
	 * @param comp
	 */
	public void setComponent(Component comp) {
		comp.addKeyListener(this);
	}

	/**
	 * Receives the KeyEvents and generates MIDI Events.
	 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		//long testTime = System.currentTimeMillis();
		switch (e.getKeyCode()) {
			case (KeyEvent.VK_DOWN) :
				getEditTrans().setValue(transpose - 12);
				break;
			case (KeyEvent.VK_UP) :
				getEditTrans().setValue(transpose + 12);
				break;
			case (KeyEvent.VK_LEFT) :
				getEditTrans().setValue(transpose - 1);
				break;
			case (KeyEvent.VK_RIGHT) :
				getEditTrans().setValue(transpose + 1);
				break;
			case (KeyEvent.VK_PAGE_DOWN) :
				getEditVel().setValue(velocity-1);
				updateVeloSlider();
				break;
			case (KeyEvent.VK_PAGE_UP) :
				getEditVel().setValue(velocity+1);
				updateVeloSlider();
				break;
			case (KeyEvent.VK_END) :
				getEditPrg().setValue(program - 1);
				break;
			case (KeyEvent.VK_HOME) :
				getEditPrg().setValue(program + 1);
				break;
			case (KeyEvent.VK_F2) :
				getEditCha().setValue(channel + 1);
				break;
			case (KeyEvent.VK_F1) :
				getEditCha().setValue(channel - 1);
				break;
		}

		if (keyToNote(e) != -1) {
			if (!isPressed[keyToNote(e)]) {

				ShortMessage sm = new ShortMessage();
				int pitch = keyToNote(e) + transpose;
				try {

					sm.setMessage(
						ShortMessage.NOTE_ON,
						channel-1,
						pitch,
						velocity);
				} catch (InvalidMidiDataException e1) {
					e1.printStackTrace();
				}
				if(DebugState.DEBUG_MIDI)
					System.out.println("key pressed: " + pitch);
				receiver.send(sm, -1);

				isPressed[keyToNote(e)] = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (keyToNote(e) != -1) {
			if (isPressed[keyToNote(e)]) {

				ShortMessage sm = new ShortMessage();
				int pitch = keyToNote(e) + transpose;
				try {
					sm.setMessage(ShortMessage.NOTE_OFF, channel-1, pitch, 80);
				} catch (InvalidMidiDataException e1) {
					e1.printStackTrace();
				}
				isPressed[keyToNote(e)] = false;
				if(DebugState.DEBUG_MIDI)
					System.out.println("key released: " + pitch);
				receiver.send(sm, -1);
			}
		}
	}

	private int keyToNote(KeyEvent e) {
		int noteIndex = -1;
		switch (e.getKeyCode()) {

			case (KeyEvent.VK_Y) :
				if(zySwitched)
					noteIndex = 60;
				else
					noteIndex = 81;
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
			case (KeyEvent.VK_PERIOD) :
				noteIndex = 74;
				break;
			case (KeyEvent.VK_MINUS) :
				noteIndex = 76;
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
			case (KeyEvent.VK_L) :
				noteIndex = 73;
				break;

			case (KeyEvent.VK_Q) :
				noteIndex = 72;
				break;

			case (KeyEvent.VK_W) :
				noteIndex = 74;
				break;
			case (KeyEvent.VK_E) :
				noteIndex = 76;
				break;
			case (KeyEvent.VK_R) :
				noteIndex = 77;
				break;
			case (KeyEvent.VK_T) :
				noteIndex = 79;
				break;
			case (KeyEvent.VK_Z) :
				if(zySwitched)
					noteIndex = 81;
				else
					noteIndex = 60;
				break;
			case (KeyEvent.VK_U) :
				noteIndex = 83;
				break;
			case (KeyEvent.VK_I) :
				noteIndex = 84;
				break;
			case (KeyEvent.VK_2) :
				noteIndex = 73;
				break;
			case (KeyEvent.VK_3) :
				noteIndex = 75;
				break;
			case (KeyEvent.VK_5) :
				noteIndex = 78;
				break;
			case (KeyEvent.VK_6) :
				noteIndex = 80;
				break;
			case (KeyEvent.VK_7) :
				noteIndex = 82;
				break;

		}

		return noteIndex;
	}

	public JPanel getControlPanel() {
		JPanel mainPanel = new JPanel(new GridLayout(1, 2));
		JPanel leftPanel = new JPanel(new GridLayout(4, 1));
		Box rightPanel = new Box(BoxLayout.Y_AXIS);
		
		leftPanel.add(getEditCha());
		leftPanel.add(getEditPrg());
		leftPanel.add(getEditVel());
		leftPanel.add(getEditTrans());
		rightPanel.add(getVelocitySlider());

		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
		return mainPanel;
	}

	private JSlider getVelocitySlider() {
		if (velocitySlider == null)
			velocitySlider = new JSlider();
		velocitySlider.setOrientation(SwingConstants.VERTICAL);
		velocitySlider.setSnapToTicks(true);
		velocitySlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				getEditVel().setValue(velocitySlider.getValue());
			}
		});
		return velocitySlider;
	}

	private IntMouseEditor getEditTrans() {
		if (editPanTrans == null) {
			editPanTrans = new IntMouseEditor(-48, 48, transpose, "trans");
			editPanTrans.registerChangeListener(this);
			editPanTrans.setToolText("use array keys for Transpose");
		}
		return editPanTrans;
	}

	private IntMouseEditor getEditCha() {
		if (editPanCha == null) {
			editPanCha = new IntMouseEditor(1, 16, "cha");
			editPanCha.registerChangeListener(this);
			editPanCha.setToolText("use F1,F2 to change channel");
		}
		return editPanCha;
	}

	private IntMouseEditor getEditPrg() {
		if (editPanPrg == null) {
			editPanPrg = new IntMouseEditor(0, 127, "prg");
			editPanPrg.registerChangeListener(this);
			editPanPrg.setToolText("use pos1 and end to change programm");
		}
		return editPanPrg;
	}

	private IntMouseEditor getEditVel() {
		if (editPanVel == null) {
			editPanVel = new IntMouseEditor(0, 127, velocity, "vel");
			editPanVel.registerChangeListener(this);
			editPanVel.setToolText(
				"use bild ab und bild down to change velocity");
		}
		return editPanVel;
	}


	/**
	 * 
	 */
	private void updateVeloSlider() {
		velocitySlider.setValue(velocity);
	}


	private void programChange(int channel, int program) {
		ShortMessage sm = new ShortMessage();
		try {
			sm.setMessage(ShortMessage.PROGRAM_CHANGE, channel-1, program, 0);
		} catch (InvalidMidiDataException e) {

			e.printStackTrace();
		}
		receiver.send(sm, -1);
	}

	/**
	 * @see javax.sound.midi.Transmitter#close()
	 */
	@Override
	public void close() {
	}

	/** 
	 * @see javax.sound.midi.Transmitter#getReceiver()
	 */
	@Override
	public Receiver getReceiver() {
		return receiver;
	}
	/** 
	 * @see javax.sound.midi.Transmitter#setReceiver(javax.sound.midi.Receiver)
	 */
	@Override
	public void setReceiver(Receiver newReceiver) {
		if (newReceiver != null)
			receiver = newReceiver;
	}

	/** 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == editPanTrans.getText()) {
			if (DebugState.DEBUG)
				System.out.println(
					"KeyPiano: ("
						+ evt.getPropertyName()
						+ ") new Value: "
						+ evt.getNewValue()
						+ " old val: "
						+ evt.getOldValue());
			transpose = ((Integer) evt.getNewValue()).intValue();
			
		}
		if (evt.getPropertyName() == getEditCha().getText()) {
			if (DebugState.DEBUG)
				System.out.println(
					"KeyPiano: ("
						+ evt.getPropertyName()
						+ ") new Value: "
						+ evt.getNewValue()
						+ " old val: "
						+ evt.getOldValue());
			channel = ((Integer) evt.getNewValue()).intValue();
			
		}
		if (evt.getPropertyName() == getEditPrg().getText()) {
			if (DebugState.DEBUG)
				System.out.println(
					"KeyPiano: ("
						+ evt.getPropertyName()
						+ ") new Value: "
						+ evt.getNewValue()
						+ " old val: "
						+ evt.getOldValue());
			program = ((Integer) evt.getNewValue()).intValue();
			programChange(channel, program);

		}
		if (evt.getPropertyName() == editPanVel.getText()) {
			if (DebugState.DEBUG)
				System.out.println(
					"KeyPiano: ("
						+ evt.getPropertyName()
						+ ") new Value: "
						+ evt.getNewValue()
						+ " old val: "
						+ evt.getOldValue());
			velocity = ((Integer) evt.getNewValue()).intValue();
			updateVeloSlider();

		}
	}

	
	/**
	 * @return true if z and y are swapped for German keybord layout.
	 */
	public boolean isZySwitched() {
		return zySwitched;
	}

	
	/**
	 * @param zy_switch if true z and y are swapped for German keyboard layout.
	 */
	public void setZySwitched(boolean zy_switch) {
		this.zySwitched = zy_switch;
	}
}