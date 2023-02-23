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
 * Created on 24.01.2006
 *
 * @author Jan-H. Kramer
 */
package de.uos.fmt.musitech.performance.midi.gui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

/**
 * @author Jan-H. Kramer This class provides the basic functionality of a piano
 *         key A mouse listener is implemented to react on MouseEvents, it will
 *         send Midi-Messages to the receiver set by setReceiver.
 *  
 */
public abstract class Keys extends JPanel {

    /**
     * Comment for <code>mousePiano</code> In MousePiano the claviature is
     * built
     */
    MousePiano mousePiano;

    boolean pressed = false;

    int size = 10;

    /**
     * Comment for <code>midiNotePitch</code> The pitch of the Key, 60 is c4
     */
    int midiNotePitch = 60;

    /**
     * Comment for <code>channel</code> MIDI-Channel, this channel is used to
     * send MIDI-Data
     */
    int channel = 0;

    /**
     * Comment for <code>velocity</code> This velocity is used when sending
     * MIDI-Data
     */
    int velocity = 100;

    /**
     * Comment for <code>color</code> One color for each of 16 MIDI-Channel
     */
    int[][] color = new int[][] { { 210, 210, 210 }, { //grey
            90, 110, 110 }, { // dark blue
            250, 240, 100 }, { // yellow
            100, 90, 140 }, { // lilac
            120, 50, 50 }, { // red
            250, 70, 90 }, { // bright red
            200, 140, 90 }, { // pale red

            230, 210, 115 }, { //yellow 2
            120, 165, 100 }, { //green
            120, 165, 160 }, { // blue
            100, 100, 180 }, { // blue 2
            160, 100, 180 }, { 180, 100, 100 }, { // red 2
            100, 100, 100 }, { // grey
            200, 220, 100 }, { // green 2
            80, 145, 130 } // blue 3
    };

    /**
     * default Constructor midiPitch 60 = c4 size = 8
     */
    public Keys() {
        this(60, 8);
    }

    /**
     * @param midiNote
     *            the pitch of the note, which is pressed by this key
     */
    public Keys(int midiNote, MousePiano mousePiano) {
        this(midiNote, 8);
        setMousePiano(mousePiano);
    }

    /**
     * @param newMidiNote
     *            the pitch of the note, which is pressed by this key
     * @param newSize
     *            the size of the key
     */
    public Keys(int newMidiNote, int newSize) {
        size = newSize;
        midiNotePitch = newMidiNote;
        this.addMouseListener(new MouseListener() {

            @Override
			public void mouseClicked(MouseEvent e) {
            }

            @Override
			public void mouseEntered(MouseEvent e) {
                if (isGlissando()) {
                    setPressed(true);
                    playNote(midiNotePitch, ShortMessage.NOTE_ON);
                }
            }

            @Override
			public void mouseExited(MouseEvent e) {
                if (isGlissando() || pressed) {
                    setPressed(false);
                    playNote(midiNotePitch, ShortMessage.NOTE_OFF);
                }
            }

            @Override
			public void mousePressed(MouseEvent e) {
                //setGlissando(true);
                setPressed(true);
                playNote(midiNotePitch, ShortMessage.NOTE_ON);
            }

            @Override
			public void mouseReleased(MouseEvent e) {
                //setGlissando(false);
                setPressed(false);
                playNote(midiNotePitch, ShortMessage.NOTE_OFF);
            }
        });
    }

    /**
     * @param pitch,
     *            ptich of the Note
     * @param noteOn,
     *            must be either ShortMessage.NOTE_OFF, ShortMessage.NOTE_ON
     *            Send MIDI-Note to connected Receiver
     */
    public void playNote(int pitch, int noteOn) {
        if (noteOn == ShortMessage.NOTE_ON || noteOn == ShortMessage.NOTE_OFF) {
            ShortMessage sm = new ShortMessage();
            try {
                sm.setMessage(noteOn, channel, pitch, velocity);
            } catch (InvalidMidiDataException e1) {
                e1.printStackTrace();
            }
            getReceiver().send(sm, -1);
        }
    }

    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics) subclasses overwrite
     *      paint, to get the look of a piano
     */
    @Override
	abstract public void paint(Graphics g);

    /**
     * @return
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * @param b
     */
    public void setPressed(boolean b) {
        pressed = b;
        repaint();
    }

    /**
     * @return
     */
    public int getMidiNotePitch() {
        return midiNotePitch;
    }

    /**
     * @param i
     */
    public void setChannel(int i) {
        channel = i;
    }

    /**
     *  
     */
    private Receiver getReceiver() {
        return getMousePiano().getReceiver();
    }

    public boolean isGlissando() {
        return getMousePiano().isGlissando();
    }

    public void setGlissando(boolean pressed) {
        getMousePiano().setGlissando(pressed);
    }

    public MousePiano getMousePiano() {
        return mousePiano;
    }

    public void setMousePiano(MousePiano mousePiano) {
        this.mousePiano = mousePiano;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
}
