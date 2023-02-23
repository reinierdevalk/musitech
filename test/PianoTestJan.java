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
import java.awt.BorderLayout;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.performance.midi.MidiTee;
import de.uos.fmt.musitech.performance.midi.gui.KeyPiano;
import de.uos.fmt.musitech.performance.midi.gui.MousePiano;

/*
 * Created on 08.12.2003
 */

/**
 * @author Jan
 *
 */
public class PianoTestJan {

	public static void main(String[] args) {
		MousePiano mousePiano = new MousePiano(36, 37);
//		MidiSystem midiSystem = null;
		Receiver receiver = null;
		MidiDevice device = null;
		KeyPiano keyPiano = new KeyPiano();

		try {
			device =
				MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[1]);
			device.open();
		} catch (MidiUnavailableException e1) {
			// Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			receiver = device.getReceiver();

		} catch (MidiUnavailableException e2) {
			// Auto-generated catch block
			e2.printStackTrace();
		}
		mousePiano.setReceiver(receiver);
		keyPiano.setComponent(mousePiano);
		MidiTee midiTee = new MidiTee(receiver, mousePiano);
		keyPiano.setReceiver(midiTee);
		JFrame frame = new JFrame();
		JFrame frame2 = new JFrame();

		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(600, 300);
		frame.setLocation(300, 150);
		//frame.show();
		frame.getContentPane().add(mousePiano);
		

		frame2.getContentPane().setLayout(new BorderLayout());
		frame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame2.setSize(150, 150);
		frame2.setLocation(150, 150);
		frame2.getContentPane().add(keyPiano.getControlPanel());
		frame2.setFocusable(false);
		frame2.setVisible(true);
		frame.setVisible(true);
	}

}
