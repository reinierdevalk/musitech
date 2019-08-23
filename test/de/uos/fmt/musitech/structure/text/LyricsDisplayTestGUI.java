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
 * Created on 29.06.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uos.fmt.musitech.structure.text;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorWindow;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.selection.Selection;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.TransportButtons;
import de.uos.fmt.musitech.time.gui.HorizontalPositioningCoordinator;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * For testing the GUI of LyricsDisplays. To this end, this class has a
 * <code>main()</code> method.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class LyricsDisplayTestGUI {

    /**
     * For testing a LyricsDisplay as a SelectionListener. <br>
     * Below a LyricsDisplay, a "Select" button is shown. When this button is activated,
     * the SelectionManager is notified that the second syllable in the
     * LyricsSyllableSequence has been selected. The Display reacts by changing the
     * background colour of the corrsponding LyricsSyllableDisplay.
     */
    public static void testSelectionListener() {
        //create editObj
        final LyricsSyllableSequence lyrics = new LyricsSyllableSequence();
        lyrics.add(new LyricsSyllable(0, "Syl"));
        lyrics.add(new LyricsSyllable(100, "la"));
        lyrics.add(new LyricsSyllable(200, "ble"));
        //create display
        final LyricsDisplay display;
        try {
            display = (LyricsDisplay) EditorFactory.createDisplay(lyrics, null);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
            return;
        }
        //create button "select"
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Selection selection = SelectionManager.getManager().getSelection();
                selection.add(lyrics.get(1), display);
                SelectionManager.getManager().selectionChanged(new SelectionChangeEvent(null));
            }
        });
        //add button and display to Box
        Box box = Box.createVerticalBox();
        box.add(display);
        box.add(selectButton);
        //create JFrame and add box
        JFrame frame = new JFrame("Test SelectionListener");
        frame.getContentPane().add(box);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Tests a LyricsSyllableDisplay as a DataChangeListener. <br>
     * Therefore, an additional Editor is created for the first syllable in the
     * LyricsSyllableSequence shown in a LyricsSyllableDisplay. The Editor allows changing
     * the syllable. The Display is updated when gaining the focus.
     */
    public static void testDataChangeListener() {
        //create shared editObj
        LyricsSyllableSequence editObj = new LyricsSyllableSequence();
        editObj.add(new LyricsSyllable(0, "Syl"));
        editObj.add(new LyricsSyllable(100, "la"));
        editObj.add(new LyricsSyllable(200, "ble"));
        //create and show Display
        Display display = null;
        try {
            display = EditorFactory.createDisplay(editObj, null);
            JFrame frame = new JFrame("Display");
            frame.getContentPane().add((JComponent) display);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //create Editor for changing a syllable
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(editObj.get(0), null, "Panel");
            EditorWindow window = new EditorWindow("Editor");
            window.addEditor(editor);
            window.pack();
            window.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Tests a LyricsDisplay being controlled by a PlayTimer.
     * The LyricsSyllable which is currently played back, is highlighted.
     */
    public static void testAsTimeable(){
        //create editObj
        LyricsSyllableSequence editObj = new LyricsSyllableSequence();
        editObj.add(new LyricsSyllable(0, "Syl"));
        editObj.add(new LyricsSyllable(1000000, "la"));
        editObj.add(new LyricsSyllable(2000000, "ble"));
        //create and show Display
        Display display = null;
        try {
            display = EditorFactory.createDisplay(editObj, null);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add((JComponent)display);
            if (display instanceof LyricsDisplay){
                ObjectPlayer player = ObjectPlayer.getInstance();
                player.getPlayTimer().registerForPush((LyricsDisplay)display);
                TransportButtons transport = new TransportButtons(player.getPlayTimer());
                panel.add(transport, BorderLayout.SOUTH);
            }
            JFrame frame = new JFrame("Test as Timeable");
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Tests a LyricsDisplay being controlled by a PlayTimer with the
     * <code>cursorOption</code> set true.
     * A cursor is moving along the display indicating the current time position.
     */
    public static void testWithCursor(){
        //create editObj
        LyricsSyllableSequence editObj = new LyricsSyllableSequence();
        editObj.add(new LyricsSyllable(0, "Syl"));
        editObj.add(new LyricsSyllable(1000000, "la"));
        editObj.add(new LyricsSyllable(2000000, "ble"));
        //create and show Display
        Display display = null;
        try {
            display = EditorFactory.createDisplay(editObj, null);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add((JComponent)display);
            if (display instanceof LyricsDisplay){
                ObjectPlayer player = ObjectPlayer.getInstance();
                player.getPlayTimer().registerForPush((LyricsDisplay)display);
                TransportButtons transport = new TransportButtons(player.getPlayTimer());
                panel.add(transport, BorderLayout.SOUTH);
                ((LyricsDisplay)display).setCursorOption(true);
            }
            JFrame frame = new JFrame("Test with cursor");
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        
    }
    
    public static void testTwoLyricsAligned(){
        //create editObj 1
        LyricsSyllableSequence lyrics1 = new LyricsSyllableSequence();
        lyrics1.add(new LyricsSyllable(Rational.ZERO, "S0"));
        lyrics1.add(new LyricsSyllable(new Rational(1,8), "S1"));
        lyrics1.add(new LyricsSyllable(new Rational(2,8), "S2"));
        lyrics1.add(new LyricsSyllable(new Rational(3,8), "S3"));
        lyrics1.add(new LyricsSyllable(new Rational(4,8), "S4"));
        //create editObj 2
        LyricsSyllableSequence lyrics2 = new LyricsSyllableSequence();
        lyrics2.add(new LyricsSyllable(Rational.ZERO, "Syl0"));
        lyrics2.add(new LyricsSyllable(new Rational(2,8), "Syl2"));
        lyrics2.add(new LyricsSyllable(new Rational(4,8), "Syl4"));
        //create displays
        LyricsDisplay display1 = null;
        LyricsDisplay display2 = null;
        try {
            display1 = (LyricsDisplay) EditorFactory.createDisplay(lyrics1);
            display2 = (LyricsDisplay) EditorFactory.createDisplay(lyrics2);
            HorizontalPositioningCoordinator coord = new HorizontalPositioningCoordinator();
            coord.registerDisplay(display1);
            coord.registerDisplay(display2);
            coord.doPositioning();
            Box displayBox = Box.createVerticalBox();
            displayBox.add(display1);
            displayBox.add(display2);
            JFrame frame = new JFrame("Test Alignment");
            frame.getContentPane().add(displayBox);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }
    
    private static void forClosing(){
        JFrame frame = new JFrame("Closing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        forClosing();
        //		testSelectionListener();
//        testDataChangeListener();
//        testAsTimeable();
//        testWithCursor();
        testTwoLyricsAligned();
    }
}