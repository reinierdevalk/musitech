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
package de.uos.fmt.musitech.performance.midi.gui;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.utility.DebugState;

/**
 * @author Jan
 *  
 */
public class MousePiano extends JLayeredPane implements Receiver, Transmitter,
        PropertyChangeListener {

    Receiver receiver;

    /**
     * When you click and move the mouse over the keys, all keys entered with
     * mouse will play a sound, for this behaviour the variable glissando is need
     * 
     * Comment for <code>glissando</code>
     */
    boolean glissando = false;

    int lastMidiPitch = -1;

	private boolean thru;

    int channel = 1;

    int velocity = 80;

    int lowestKey;

    int numberOfKeys;

    public MousePiano() {
        this(48, 25);
    }

    public MousePiano(int lowestKey) {
        this(lowestKey, 25);
    }

    public MousePiano(int newLowestKey, int newNumberOfKeys) {
        lowestKey = newLowestKey;
        numberOfKeys = newNumberOfKeys;
        setLayout(null);
        buildPiano(lowestKey, numberOfKeys);
        setFocusable(true);

        // dummy Receiver
        receiver = new Receiver() {
            @Override
			public void close() {
            }

            @Override
			public void send(MidiMessage message, long timeStamp) {
            }
        };
        addComponentListener(new ComponentListener() {
            @Override
			public void componentHidden(ComponentEvent e) {
            }

            @Override
			public void componentMoved(ComponentEvent e) {
            }

            @Override
			public void componentResized(ComponentEvent e) {
                pianoLayout(lowestKey, numberOfKeys);
            }

            @Override
			public void componentShown(ComponentEvent e) {
                //				getParent().getFocusTraversalPolicy().;
                requestFocus();
            }
        });
    }

    ArrayList<WhiteKey> whiteKeys = new ArrayList<WhiteKey>();

    ArrayList<BlackKey> blackKeys = new ArrayList<BlackKey>();


    void pianoLayout(int lowestKey, int numberOfKeys) {
        int size = getWidth() / whiteKeys.size(); // width of one white Key
        int x = 0;
        if (isBlackKey(lowestKey)) // skip one white key
            x = size;
        for (Iterator iter = whiteKeys.iterator(); iter.hasNext();) {
            WhiteKey element = (WhiteKey) iter.next();
            element.setBounds(x, 0, size, getHeight());
            x += size;
            add(element, 0);
        }
        x = 0;
        if (lowestKey % 12 == 11 || lowestKey % 12 == 4) // e or h skip black
                                                         // key
            x = size;
        int index = 0;
        for (Iterator iter = blackKeys.iterator(); iter.hasNext();) {
            BlackKey element = (BlackKey) iter.next();
            setLayer(element, 1);

            element.setBounds(x + 2 * size / 3, 0, 3 * size / 5,
                    2 * getHeight() / 3);
            if ((element.getMidiNotePitch() % 12) == 3
                    || element.getMidiNotePitch() % 12 == 10) { // es and b
                x = x + 2 * size;
            } else
                x += size;
            add(element);
            index++;
        }

    }

    public boolean isBlackKey(int number) {
        return (number % 12 == 1 || number % 12 == 3 || number % 12 == 6
                || number % 12 == 8 || number % 12 == 10);
    }

    public void buildPiano(int lowestKey, int numberOfKey) {
        for (int i = 0; i < numberOfKey; i++) {
            if (isBlackKey(i + lowestKey)) {
                BlackKey blackkey = new BlackKey(i + lowestKey, this);
                blackKeys.add(blackkey);
            } else {
                WhiteKey whitekey = new WhiteKey(i + lowestKey, this);
                whiteKeys.add(whitekey);
            }
        }
        pianoLayout(lowestKey, numberOfKey);

    }

    public static void main(String[] args) { // just for testing
        MousePiano piano = new MousePiano();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 200);
        frame.setLocation(200, 300);

        frame.getContentPane().add(piano);
        frame.setVisible(true);

    }

    /**
     * @see javax.sound.midi.Transmitter#close()
     */
    @Override
	public void close() {
    }

    @Override
	public Receiver getReceiver() {
        return receiver;
    }

    /**
     * Note messages press and release the keys, if 'thru' is on, everything 
     * is passed on to the receivers
     * @see javax.sound.midi.Receiver#send(MidiMessage, long)
     */

    @Override
	public void send(MidiMessage message, long timeStamp) {
    	if(getThru()) {
    		receiver.send(message, timeStamp);
    	}
        if (message instanceof ShortMessage
                && message.getStatus() >= ShortMessage.NOTE_OFF
                && message.getStatus() <= ShortMessage.NOTE_ON + 15)
            // note on or off ch 1-15
            if (message.getMessage()[1] < lowestKey
                    || message.getMessage()[1] >= lowestKey + numberOfKeys) {
                if (DebugState.DEBUG)
                    System.out.println("not in range to display in mousePiano");
            } else {
                boolean keyFound = false;
                int keyToFind = message.getMessage()[1];
                int keyToTry = lowestKey;

                int distance = 0;
                // next white key can be one or two pitches
                // next black kay can be two or three pitches
                if (isBlackKey(keyToFind)) { // we search for black key
                    if (!isBlackKey(lowestKey)) {
                        if (lowestKey % 12 == 4 || lowestKey % 12 == 11) { // e or
                                                                           // h
                            keyToTry += 2;
                            // this is the first black key
                        } else
                            keyToTry += 1;
                        // this is the first black key
                    }
                    int index = 0;
                    while (!keyFound && keyToTry < lowestKey + numberOfKeys) {
                        // not found or out of range
                        if (keyToTry == keyToFind) {
                            keyFound = true;
                            BlackKey blackKey = blackKeys.get(index);
                            if (message.getStatus() >= ShortMessage.NOTE_ON
                                    && message.getStatus() <= ShortMessage.NOTE_ON + 15)
                                // note on ch 1-15
                                blackKey.setChannel(message.getStatus()
                                        - ShortMessage.NOTE_ON);
                            blackKey.setPressed(true);

                            if (message.getStatus() >= ShortMessage.NOTE_OFF
                                    && message.getStatus() <= ShortMessage.NOTE_OFF + 15)
                                blackKey.setPressed(false); // note off
                        }

                        if (keyToTry % 12 == 3 || keyToTry % 12 == 10) // es or
                                                                       // b
                            distance = 3;
                        else
                            distance = 2;
                        keyToTry += distance; // next black key
                        index++;
                    }

                } else { // KeyTofind white
                    if (isBlackKey(lowestKey)) {
                        keyToTry++; // now we have a white key
                    }
                    int index = 0;
                    while (!keyFound && keyToTry < lowestKey + numberOfKeys) {
                        // not found or out of range
                        if (keyToTry == keyToFind) {
                            keyFound = true;
                            WhiteKey whiteKey = whiteKeys.get(index);
                            if (message.getStatus() >= ShortMessage.NOTE_ON
                                    && message.getStatus() <= ShortMessage.NOTE_ON + 15)
                                whiteKey.setPressed(true);
                            whiteKey.setChannel(message.getStatus()
                                    - ShortMessage.NOTE_ON);
                            if (message.getStatus() >= ShortMessage.NOTE_OFF
                                    && message.getStatus() <= ShortMessage.NOTE_OFF + 15)
                                whiteKey.setPressed(false);
                        }
                        if (keyToTry % 12 == 4 || keyToTry % 12 == 11) // e or h
                            distance = 1;
                        else
                            distance = 2;
                        keyToTry += distance; // next white key
                        index++;
                    }
                }
            }
    }

    //	}

    /**
     * @see javax.sound.midi.Transmitter#setReceiver(javax.sound.midi.Receiver)
     */
    @Override
	public void setReceiver(Receiver newReceiver) {
        receiver = newReceiver;
    }

    public void setThru(boolean thru) {
		this.thru = thru;
	}

    public boolean getThru() {
		return thru;
	}

	/**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
	public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "trans") {
            if (DebugState.DEBUG)
                System.out.println("KeyPiano: (" + evt.getPropertyName()
                        + ") new Value: " + evt.getNewValue() + " old val: "
                        + evt.getOldValue());
            //			transpose = ((Integer) evt.getNewValue()).intValue();

        }
        if (evt.getPropertyName() == "cha") {
            if (DebugState.DEBUG)
                System.out.println("KeyPiano: (" + evt.getPropertyName()
                        + ") new Value: " + evt.getNewValue() + " old val: "
                        + evt.getOldValue());
            channel = ((Integer) evt.getNewValue()).intValue();

        }
        if (evt.getPropertyName() == "vel") {
            if (DebugState.DEBUG)
                System.out.println("KeyPiano: (" + evt.getPropertyName()
                        + ") new Value: " + evt.getNewValue() + " old val: "
                        + evt.getOldValue());
            velocity = ((Integer) evt.getNewValue()).intValue();

        }
    }

    public boolean isGlissando() {
        return glissando;
    }

    public void setGlissando(boolean globalPressed) {
        this.glissando = globalPressed;
    }

}