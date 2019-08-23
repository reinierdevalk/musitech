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

package de.uos.fmt.musitech.time;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;

import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.performance.gui.PianoRollContainerDisplay;
import de.uos.fmt.musitech.time.gui.HorizontalPositioningCoordinator;
import de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay;
import de.uos.fmt.musitech.time.gui.LinearDisplayPanel;
import de.uos.fmt.musitech.utility.math.Rational;

/*
 * Created on 29.09.2004
 *
 */

/**
 * JApplet for demonstrating the integrated representation of musical
 * information in MUSITECH. One MObject is created (a NoteList) and shown in
 * three different "views": in a NotationDisplay, in a PianoRollContainerDisplay
 * and as playback using the ObjectPlayer. The NotationDisplay and
 * PianoRollContainerDisplay are arranged by a HorizontalPositioningCoordinator.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class IntegratedRepresentation extends JApplet {

    /**
     * Starts the applet. Creates the GUI.
     * 
     * @see java.applet.Applet#init()
     */
    public void init() {
        try {
            super.init();
//            createGUI();
            	createGUIWithTimeLine();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a NoteList and sets the time signature, key and tempo in the
     * MetricalTimeLine of the NoteList's Context (via the Context's Piece). The
     * NoteList gives the melody of the first eight bars of the Andante grazioso
     * from Mozart's piano sonata in A major, KV 331.
     * 
     * @return NoteList representing the melody of Mozart, KV 331, Andante
     *         grazioso, bb. 1-8
     */
    private NoteList createMelody() {
        //TODO evtl. später Melodie von ExamplePiece holen
        //create Context with time signature, key and tempo (via Piece)
        Piece piece = new Piece();
        if (piece.getMetricalTimeLine()==null){
            piece.setMetricalTimeLine(new MetricalTimeLine());
        }
        //set tempo
        piece.getMetricalTimeLine().setTempo(new Rational(0,8), 120, 8);
        //set time signature
        TimeSignatureMarker timeSignatureMarker = new TimeSignatureMarker(new TimeSignature(6,8), new Rational(0,8));
        piece.getMetricalTimeLine().add(timeSignatureMarker);
        //set key
        KeyMarker key = new KeyMarker(new Rational(0,8), 0);
        key.setRoot('a');
        key.setAlterationNum(3);
        //Context context = new Context(piece);
        //create melody
        String epec = createMelodyInEpec();
//        NoteList melody = new NoteList(context, epec);	//TODO Collin: fürs Testen auskommentieren	
        NoteList melody = new NoteList(epec);	//TODO Collin: fürs Testen einkommentieren
        return melody;
    }
    
    /**
     * Returns a String containing the epec representation of the melody of the
     * Andante grazioso from the piano sonata in A, KV 331 by Mozart. (No key
     * and time signature are specified as they will be given in the
     * MetricalTimeLine of the melody's context.)
     * 
     * @return String with an epec representation of the melody of Mozart, KV
     *         331, Andante grazioso, bb. 1-8
     */
    private String createMelodyInEpec(){
        StringBuffer epec = new StringBuffer();
        //time signature and key
        epec.append("T6/8 "); //TODO Collin: fürs Testen einkommentieren
        epec.append("K3+ "); //TODO Collin: fürs Testen einkommentieren
        //Vordersatz
        epec.append("8.''c+ 16d 8c+ 4e 8e 8.'b 16''c+ 8'b 4''d 8d");
        epec.append("4'a 8a 4b 8b 4''c+ 16e d 4c+ 8'b");
        //Nachsatz
        epec.append("8.''c+ 16d 8c+ 4e 8e 8.'b 16''c+ 8'b 4''d 8d");
        epec.append("4'a 8b 4''c+ 8d 4c+ 8'b 4a 8r");
        return epec.toString();
    }

    /**
     * Returns a Display created for the specified NoteList by the
     * EditorFactory. The editortype to be created is set to "Notation".
     * 
     * @param notes
     *            NoteList for which the Display is to be created
     * @return Display for the specified NoteList (requested type "Notation")
     */
    private Display createNotationDisplay(NoteList notes) {
        Display display = null;
        try {
            display = EditorFactory.createDisplay(notes, null, "Notation");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return display;
    }

    /**
     * Returns a Display created for the specified NoteList by the
     * EditorFactory. The NoteList is converted to MidiNoteSequence. The
     * editortype to be created is set to "PianoRoll".
     * 
     * @param notes
     *            NoteList for which the Display is to be created
     * @return Display for the specified NoteList (requested type "PianoRoll")
     */
    private Display createPianoRollDisplay(NoteList notes) {
        MidiNoteSequence midiSeq = MidiNoteSequence.convert(notes);
        Display display = null;
        try {
            display = EditorFactory.createDisplay(midiSeq, null, "PianoRoll");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return display;
    }

    /**
     * Returns a Display created for the specified NoteList by the
     * EditorFactory. (The ObjectPlayerDisplay is registered with name "Player"
     * at the EditorRegistry first.) The NoteList is converted to
     * MidiNoteSequence. The editortype to be created is set to "Player".
     * 
     * @param notes
     *            NoteList for which the Display is to be created
     * @return Display for the specified NoteList (requested type "Player")
     */
/*
 * private Display createObjectPlayerDisplay(NoteList notes) { //register
 * ObjectPlayerDisplay at EditorRegistry
 * EditorRegistry.registerEditortypeForClass(Container.class.getName(), new
 * EditorType("Player", ObjectPlayerDisplay.class.getName(), null)); //create
 * display MidiNoteSequence midiSeq = MidiNoteSequence.convert(notes); Display
 * display = null; try { display = EditorFactory.createDisplay(midiSeq, null,
 * "Player"); } catch (EditorConstructionException e) { e.printStackTrace(); }
 * return display; }
 */
    
    /**
     * Returns an Array with three buttons "Play", "Pause" and "Stop" which
     * control playing back the specified NoteList using the ObjectPlayer. The
     * NoteList is converted to MidiNoteSequence.
     * 
     * @param notes
     *            NoteList to be played back
     * @return JButton[] with three buttons for starting, interrupting and
     *         stopping the playback
     */
    private JButton[] createButtons(NoteList notes){
        final ObjectPlayer player = ObjectPlayer.getInstance();
        MidiNoteSequence midiSeq = MidiNoteSequence.convert(notes);
        player.setContainer(midiSeq);
        Border buttonBorder = BorderFactory.createEmptyBorder(5,10,5,10);
        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                player.getPlayTimer().start();
            }           
        });
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                player.getPlayTimer().stop();
            }           
        });
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                player.getPlayTimer().stop();
                player.getPlayTimer().reset();
            }           
        });
        return new JButton[] {playButton, pauseButton, stopButton};
    }

    /**
     * Creates the graphical user interface, adding a NotationDisplay, a
     * PianoRollContainerDisplay and buttons for controlling the ObjectPlayer.
     */
    private void createGUI() {
        NoteList notes = createMelody();
        getContentPane().setLayout(new BorderLayout());
        //create and add HorizontalTimedDisplays (notation, pianoRoll)
        Display notationDisplay = createNotationDisplay(notes);
        Display pianoRollDisplay = createPianoRollDisplay(notes);
        if (notationDisplay != null && pianoRollDisplay != null) {
            Box horizontalTimedBox = Box.createVerticalBox();
            if (notationDisplay instanceof HorizontalTimedDisplay
                && pianoRollDisplay instanceof HorizontalTimedDisplay) {
                HorizontalPositioningCoordinator coord = new HorizontalPositioningCoordinator();
                coord.registerDisplay((HorizontalTimedDisplay) notationDisplay);
                coord.registerDisplay((HorizontalTimedDisplay) pianoRollDisplay);
                coord.doPositioning();
            }
            horizontalTimedBox.add((JComponent) notationDisplay);
            horizontalTimedBox.add((JComponent) pianoRollDisplay);
            getContentPane().add(horizontalTimedBox, BorderLayout.NORTH);
            //create and add buttons for ObjectPlayer
//            Display playerDisplay = createObjectPlayerDisplay(notes);
//            if (playerDisplay!=null){
//                Box playerBox = Box.createHorizontalBox();
//                playerBox.add(Box.createHorizontalGlue());
//                playerBox.add((JComponent)playerDisplay);
//                playerBox.add(Box.createHorizontalGlue());
//                getContentPane().add(playerBox);
//            }
            JButton[] buttons = createButtons(notes);
            if (buttons!=null && buttons.length>0){
                Box buttonsBox = Box.createHorizontalBox();
                for (int i = 0; i < buttons.length; i++) {
                    buttonsBox.add(Box.createHorizontalGlue());
                    buttonsBox.add(buttons[i]);
                }
                buttonsBox.add(Box.createHorizontalGlue());
                getContentPane().add(buttonsBox, BorderLayout.SOUTH);
            }
        }  
    }
    
    /**
     * Here for testing only.
     *  
     */
    private void createGUIWithTimeLine(){
        NoteList notes = createMelody();
        getContentPane().setLayout(new BorderLayout());
        //create and add HorizontalTimedDisplays (notation, pianoRoll)
        Display notationDisplay = createNotationDisplay(notes);
        Display pianoRollDisplay = createPianoRollDisplay(notes);
        if (notationDisplay != null && pianoRollDisplay != null) {
            Box horizontalTimedBox = Box.createVerticalBox();
            if (notationDisplay instanceof HorizontalTimedDisplay
                && pianoRollDisplay instanceof HorizontalTimedDisplay) {
                HorizontalPositioningCoordinator coord = new HorizontalPositioningCoordinator();
                coord.registerDisplay((HorizontalTimedDisplay) notationDisplay);
                coord.registerDisplay((HorizontalTimedDisplay) pianoRollDisplay);
                coord.doPositioning();
            }
            horizontalTimedBox.add((JComponent) notationDisplay);
//          horizontalTimedBox.add((JComponent) pianoRollDisplay);
            LinearDisplayPanel linearPanel = new LinearDisplayPanel();
            linearPanel.setDisplay((PianoRollContainerDisplay)pianoRollDisplay);
            linearPanel.remove(linearPanel.getScrollBar());
            linearPanel.remove(linearPanel.getSouthPanel());
            linearPanel.setTimePosition(0);
            horizontalTimedBox.add(linearPanel);
            getContentPane().add(horizontalTimedBox, BorderLayout.NORTH);
            //create and add buttons for ObjectPlayer
            JButton[] buttons = createButtons(notes);
            if (buttons!=null && buttons.length>0){
                Box buttonsBox = Box.createHorizontalBox();
                for (int i = 0; i < buttons.length; i++) {
                    buttonsBox.add(Box.createHorizontalGlue());
                    buttonsBox.add(buttons[i]);
                }
                buttonsBox.add(Box.createHorizontalGlue());
                getContentPane().add(buttonsBox, BorderLayout.SOUTH);
            }
        }
    }

}