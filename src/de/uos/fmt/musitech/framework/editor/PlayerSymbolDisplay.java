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
 * Created on 09.08.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.AbstractDisplay;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.PlayTimer;

/**
 * This class provides a Display wrapping and controlling an ObjectPlayer. The
 * Display's <code>editObj</code> is the Container to be played back by an
 * ObjectPlayer. The Display uses an Object Player and provides a GUI with a
 * button for starting to play back the Container (e.g. a MidiNoteSequence). The
 * ObjectPlayerDisplay can be used as an InteractiveTaskComponent in a
 * TaskInputUI.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class PlayerSymbolDisplay extends AbstractDisplay  //TODO
                                                                                             // immer?
{

    /**
     * ObjectPlayer used to play back the <code>editObj</code>.
     */
    private ObjectPlayer player = ObjectPlayer.getInstance();

    /**
     * JButton with text "Listen" (for starting playback).
     */
    private JButton listenButton = new JButton("Listen");

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
     */
    public void updateDisplay() {
        // TODO Auto-generated method stub
    }

    /**
     * Creates the graphical user interface. The GUI provides buttons for
     * starting and ending play back.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#createGUI()
     */
    public void createGUI() {
        setLayout(new BorderLayout());
        //upper part of display
        Icon icon = null;
        //		URL iconURL =
        // ObjectPlayerDisplay.class.getResource("icons//sound.gif");
        URL iconURL = PlayerSymbolDisplay.class.getResource("loudspeaker.gif");
        icon = new ImageIcon(iconURL);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 0));
        add(iconLabel, BorderLayout.CENTER);
        //create buttons
        //		TransportButtons transpButtons =
        //			new TransportButtons(player.getPlayTimer());
        //		//add buttons to GUI
        //		JPanel buttonPanel = new JPanel();
        //		buttonPanel.setLayout(new GridLayout(1, 0));
        //		buttonPanel.add(transpButtons.getPlayButton()); //TODO nur 1
        // "Listen"-button?
        //		buttonPanel.add(transpButtons.getStopButton());
        //		JPanel bottomPanel = new JPanel();
        //		bottomPanel.setLayout(new GridLayout(0, 1));
        //		bottomPanel.add(buttonPanel);
        //		bottomPanel.add(transpButtons.getTimeSlider());
        //		add(bottomPanel, BorderLayout.SOUTH);

        //		ObjectPlayer player = new ObjectPlayer();
        //		JButton listenButton = new JButton("Listen");
        listenButton.setEnabled(true);
        listenButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                playback();
            }
        });
        listenButton.setOpaque(isOpaque());
        add(listenButton, BorderLayout.SOUTH); //TODO Layout verbessern
    }

    /**
     * Starts playing back the <code>editObj</code>. The PlayTimer of the
     * <code>player</code> is reset, is given the <code>editObj</code> as
     * its <code>container</code> and is started.
     */
    private void playback() {
        //		ObjectPlayer objPlayer;
        if (getEditObj() instanceof Container || getEditObj() instanceof AudioFileObject) {
            PlayTimer playTimer = player.getPlayTimer();
            playTimer.reset();
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (getEditObj() instanceof Container) {
                player.setContainer((Container) getEditObj());
            } else {
                player.clear();
                player.addAudioFileObject((AudioFileObject) getEditObj());
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            playTimer.start();
        }
    }

    

    /**
     * If <code>interactive</code> is true, the <code>listenButton</code> is
     * enabled, if <code>interactive</code> is false, the
     * <code>listenButton</code> is disabled.
     * 
     * @see de.uos.fmt.musitech.applications.course.InteractiveTaskComponent#setInteractionEnabled(boolean)
     */
    public void setInteractionEnabled(boolean interactive) {
        listenButton.setEnabled(interactive);
    }

    /**
     * If <code>interactive</code> is true, the <code>listenButton</code> is
     * given a MouseListener for starting playback when the mouse is clicked and
     * a KeyListener for starting playback when a key is typed. <br>
     * If <code>interactive</code> is false, the current MouseListeners and
     * KeyListeners of the <code>listenButton</code> are removed.
     * 
     * @see de.uos.fmt.musitech.applications.course.InteractiveTaskComponent#setListenersForInteraction(boolean)
     */
    public void setListenersForInteraction(boolean interactive) {
        if (interactive) {
            listenButton.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent me) {
                    playback();
                }
            });
            listenButton.requestFocus();
            listenButton.addKeyListener(new KeyAdapter() {

                public void keyTyped(KeyEvent ke) {
                    playback();
                }
            });
        } else {
            MouseListener[] mouseListeners = listenButton.getMouseListeners();
            for (int i = 0; i < mouseListeners.length; i++) {
                listenButton.removeMouseListener(mouseListeners[i]);
            }
            KeyListener[] keyListeners = listenButton.getKeyListeners();
            for (int i = 0; i < keyListeners.length; i++) {
                listenButton.removeKeyListener(keyListeners[i]);
            }
        }
    }

    /**
     * Overwrites <code>setOpaque</code> in order to set the
     * <code>listenButton</code> opaque (resp. not) as well.
     */
    public void setOpaque(boolean opaque) {
        super.setOpaque(opaque);
        if (listenButton != null)
            listenButton.setOpaque(opaque);
    }

    /**
     * Returns a String explaining how to interact with the ObjectPlayerDisplay.
     * 
     * @see de.uos.fmt.musitech.applications.course.InteractiveTaskComponent#getHelpText()
     */
    public String getHelpText() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("This player allows to listen to a sound example. ");
        buffer.append("In order to hear the sound, please click on the 'Listen' button. ");
        buffer
                .append("The player will then play back the complete sound example and stop automatically.");
        return buffer.toString();
    }

}