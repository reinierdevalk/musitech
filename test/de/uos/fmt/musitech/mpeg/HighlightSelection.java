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
import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.mpeg.ObjectEditor.NotationCellRenderer;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.score.gui.Score;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Kerstin Neubarth
 *  
 */
public class HighlightSelection extends JPanel {

    Piece piece;
    Score score;
    //	HashMap map = new HashMap();
    JFrame frame;
    JList list;
    //    private Container markUp = new BasicContainer();
    private Collection markUps = new ArrayList();
    private JButton showButton = new JButton();
    private NotationDisplay display = null;

    public HighlightSelection(Piece piece, JFrame mainFrame) {
        this.piece = piece;
        this.frame = mainFrame;
        createGUI();
    }
    
    /**
     * Returns a Collection whose elements are Containers. These Containers contain
     * the selected notes.
     * 
     * @return Collection
     */
    public Collection getSelected(){
        return markUps;
    }

    private void createGUI() {
        setLayout(new BorderLayout());
        //NotationDisplay
        try {
            display = (NotationDisplay) EditorFactory.createDisplay(piece);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        display.getScorePanel().setSelectionColorsMulti(true);
        JScrollPane scroll = new JScrollPane(display);
        scroll.setOpaque(false);
        add(scroll, BorderLayout.CENTER);
        //        add(display, BorderLayout.CENTER);
        Container selectionPool = piece.getSelectionPool();
        Vector vector = new Vector();
        for (int i = 0; i < selectionPool.size(); i++) {
            if (selectionPool.get(i) instanceof Container) {
                vector.add(selectionPool.get(i));
            }
        }
        list = new JList(vector);
        if (vector.size() > 0) {
            list.setCellRenderer(new NotationCellRenderer());
            //Erg. K.N. 11.01.05
            list.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    highlightSelection();
                }
            });
            Border out = BorderFactory.createLineBorder(Color.BLACK);
            Border in = BorderFactory.createEmptyBorder(10, 10, 10, 10);
            list.setBorder(BorderFactory.createCompoundBorder(out, in));
            Box listBox = Box.createVerticalBox();
            listBox.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 30));
            listBox.add(Box.createVerticalGlue());
            listBox.add(list);
            listBox.add(Box.createVerticalGlue());
            add(listBox, BorderLayout.EAST);
        } else {
            Box infoBox = Box.createVerticalBox();
            infoBox.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 30));
            infoBox.add(Box.createVerticalGlue());
            infoBox.add(new JLabel("No selections available."));
            infoBox.add(Box.createVerticalGlue());
            add(infoBox, BorderLayout.EAST);
        }
    }

    private void highlightSelection() {
        for (Iterator iter = markUps.iterator(); iter.hasNext();) {
            Container element = (Container) iter.next();
            display.removeMarkup(element);
        }
        markUps.clear();
        Object[] selectedObjs = list.getSelectedValues();
        for (int i = 0; i < selectedObjs.length; i++) {
            if (selectedObjs[i] instanceof Container) {
                Container markUp = new BasicContainer();
                Collection contents = ((Container) selectedObjs[i]).getContentsRecursiveList(null);
                for (Iterator iter = contents.iterator(); iter.hasNext();) {
                    Object element = (Object) iter.next();
                    if (element instanceof Note) {
                        markUp.add(element);
                    }
                }
                if (((Container) selectedObjs[i]).getRenderingHint("color") != null) {
                    markUp.addRenderingHint("color", ((Container) selectedObjs[i]).getRenderingHint("color"));
                }
                markUps.add(markUp);
                display.addMarkup(markUp);
            }
        }
    }

    class NotationCellRenderer extends JLabel implements ListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean hasFocus) {
            setText(((Container) value).getName());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    //  just for testing
    public static void main(String[] args) {
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
        for (Note n: nl)
        	voice.add(n);
        StaffContainer staff = new StaffContainer(context);
        staff.add(voice);
        Container cp = piece.getContainerPool();
        cp.add(staff);
        Container container1 = new BasicContainer();
        container1.setName("Selection1");
        container1.addRenderingHint("color", "8899CC");
        for (int i = 0; i < 3; i++) {
            container1.add(nl.get(i));
        }
        piece.getSelectionPool().add(container1);
        Container container2 = new BasicContainer();
        container2.setName("Selection2");
        container2.addRenderingHint("color", "081536");
        for (int i = 5; i < 7; i++) {
            container2.add(nl.get(i));
        }
        piece.getSelectionPool().add(container2);
        //create SelectObjects
        HighlightSelection test = new HighlightSelection(piece, null);
        //show SelectObjetcs
        JFrame frame = new JFrame("Test HighlightSelecions");
        frame.getContentPane().add(test);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}