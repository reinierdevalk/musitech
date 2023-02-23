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
 * Created on 11.01.2005
 *
 */
package de.uos.fmt.musitech.mpeg;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.framework.editor.DisplayTypeFilter;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.TransportButtons;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.structure.container.MusicTreeFilter;
import de.uos.fmt.musitech.structure.container.MusicTreeView;
import de.uos.fmt.musitech.time.TimeLine;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Kerstin Neubarth
 *  
 */
public class SelectObjects extends JPanel {

    Piece piece;
    
    public SelectObjects(Piece piece){
        setPiece(piece);
        createGUI();
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece.getScore() == null) {
            piece.setScore(NotationDisplay.createNotationSystem(piece));
        }
    }

    private void createGUI() {
        setLayout(new BorderLayout());
        JComponent treeView = createMusicTreeView();
        if (treeView != null) {
            add(treeView);
        }
        JComponent transportButtons = createTransportButtons();
        add(transportButtons, BorderLayout.SOUTH);
    }

    private JComponent createMusicTreeView() {
        //create MusicTreeView
        MusicTreeView view = new MusicTreeView();
        MusicTreeFilter filter = new MusicTreeFilter();
        filter.fillInRegistrations();
        String[] filterOut = new String[] {MetricalTimeLine.class.getName(), TimeLine.class.getName(),
                                           HashMap.class.getName()};
        filter.registerObjectsToAccept(MusicTreeFilter.NO_NODE, filterOut);
        view.setMusicTreeFilter(filter);
        DisplayTypeFilter displayFilter = new DisplayTypeFilter();
        displayFilter.clearRegistrations();
        displayFilter.registerObjecsToAccept(DisplayTypeFilter.CONTENT, new String[]{"Notation"});
        view.setDisplayTypeFilter(displayFilter);
        view.setPlayTimerOptions(true, false, false);
        view.setMObject(piece);
        return view;
    }
    
    private JComponent createTransportButtons() {
        final TransportButtons transportButtons = new TransportButtons(
                ObjectPlayer.getInstance().getPlayTimer());
        transportButtons.setRecordingEnabled(false);
        JButton startButton = transportButtons.getPlayButton();
        ActionListener[] listeners = startButton.getActionListeners();
        if (listeners != null && listeners.length > 0) {
            for (int i = 0; i < listeners.length; i++) {
                startButton.removeActionListener(listeners[i]);
            }
        }
        startButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                if (!transportButtons.getPlayTimer().nextLocator()){
                    transportButtons.getPlayTimer().prevLocator();//TODO notwendig?
                }
                transportButtons.getPlayTimer().start();
                transportButtons.getPlayButton().setEnabled(false);
                transportButtons.getStopButton().setEnabled(true);
            }
        });
        JButton resetButton = transportButtons.getResetButton();
        ActionListener[] buttonListeners = resetButton.getActionListeners();
        if (buttonListeners != null && buttonListeners.length > 0) {
            for (int i = 0; i < buttonListeners.length; i++) {
                resetButton.removeActionListener(buttonListeners[i]);
            }
        }
        resetButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                if (!transportButtons.getPlayTimer().prevLocator()) {
                    transportButtons.getPlayTimer().reset();
                }
                transportButtons.paintRecButton();
                transportButtons.getStopButton().setEnabled(false);
                transportButtons.getPlayButton().setEnabled(true);
            }
        });
        return transportButtons;
    }
    
    //just for testing
    public static void main(String[] args){
        //create test piece
        Piece piece = new Piece();
        piece.setName("Test Piece");
        piece.getMetricalTimeLine().setTempo(Rational.ZERO, 100, 8);
        Context context = piece.getContext();
        NoteList nl = new NoteList(context);
        nl.addnext(new ScorePitch('f', 1, 0), new Rational(3, 32));
        nl.addnext(new ScorePitch('d', 1, 0), new Rational(1, 32));
        nl.addnext(new ScorePitch('a', 0, 0), new Rational(3, 32));
        nl.addnext(new ScorePitch('d', 1, 0), new Rational(1, 32));
        nl.addnext(new ScorePitch('f', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('g', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('f', 1, 0), new Rational(1, 16));
        nl.addnext(new ScorePitch('e', 1, 0), new Rational(1, 16));
        Voice voice = new Voice(context);
        voice.setName("Voice");
        for(Note n : nl)
        	voice.add(n);
        StaffContainer staff = new StaffContainer(context);
        staff.setName("Staff");
        staff.add(voice);
        Container cp = piece.getContainerPool();
        cp.add(staff);
        Container container1 = new BasicContainer();
        container1.setName("Selection1");
        for (int i = 0; i < 3; i++) {
            container1.add(nl.get(i));        
        }
        piece.getSelectionPool().add(container1);
        Container container2 = new BasicContainer();
        container2.setName("Selection2");
        for (int i = 5; i < 7; i++) {
            container2.add(nl.get(i));
        }
        piece.getSelectionPool().add(container2);
        //create SelectObjects
        SelectObjects test = new SelectObjects(piece);
        //show SelectObjetcs
        JFrame frame = new JFrame("Test SelectObjects");
        frame.getContentPane().add(test);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}