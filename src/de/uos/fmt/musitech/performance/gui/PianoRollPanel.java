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
 * Created on 30.07.2004
 */
package de.uos.fmt.musitech.performance.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.AbstractDisplay;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.MouseEditorUtils;
import de.uos.fmt.musitech.framework.editor.TimedImageStaffEditor;
import de.uos.fmt.musitech.time.gui.LinearDisplayPanel;

/**
 * This class combines a PianoRollContainerDisplay with TimeScale and a
 * Scrollbar
 * 
 * @author Jan
 *  
 */
public class PianoRollPanel extends AbstractDisplay implements Timeable {

    PianoRollContainerDisplay pianoRoll;

    LinearDisplayPanel displayPanel;

    Container cont;

    /**
     *  
     */
    public PianoRollPanel() {
        //		init();
        createGUI();
    }

    public PianoRollPanel(Container cont) {
        setContainer(cont);
        createGUI();
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Display)
     */
    public void init(Object editObject, EditingProfile profile, Display root) {
        super.init(editObject, profile, root);
        cont = (Container) getPropertyValue();
        if (cont == null) {
            cont = (Container) getEditObj();
        }
        setContainer(cont);
        DataChangeManager.getInstance().interestExpandElements(this, cont);
    }

    Point pressed, released;

    /**
     *  
     */
    public void createGUI() {
        if (cont == null) {
            // default Container
            cont = new BasicContainer();
        }
        pianoRoll = new PianoRollContainerDisplay();
        //        pianoRoll.setMouseListenerDisable(true);
        pianoRoll.setContainer(cont);
        displayPanel = new LinearDisplayPanel();
        displayPanel.setDisplay(pianoRoll);
        setLayout(new BorderLayout(0, 0));
        add(displayPanel, BorderLayout.CENTER);
    }

    /**
     * @return
     */
    public Container getContainer() {
        return cont;
    }

    /**
     * @param container
     */
    public void setContainer(Container container) {
        cont = container;
        if (pianoRoll != null)
            pianoRoll.setContainer(container);
        DataChangeManager.getInstance().interestExpandElements(this, container);
        DataChangeManager.getInstance().interestExpandObject(this, container);
    }

    private void expandInterest() {

    }

    public PianoRollContainerDisplay getPianoRoll() {
        return pianoRoll;
    }

    /**
     * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
     */
    public void setTimePosition(long timeMicros) {
        displayPanel.setTimePosition(timeMicros);
    }

    /**
     * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
     */
    public long getEndTime() {
        return displayPanel.getEndTime();
    }

    /**
     *  
     */
    public double getMicrosPerPix() {
        return displayPanel.getMicrosPPix();
    }

    public void setMicrosPerPix(double mPP) {
        displayPanel.setMicrosPPix(mPP);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * 
     * @param path
     * @param description
     * @return
     */
    protected static ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = PianoRollPanel.class.getResource(path);
        if (imgURL != null)
            return new ImageIcon(imgURL, description);
        else
            System.err.println("Couldn't find file: " + path);
        return null;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
     */
    public void dataChanged(DataChangeEvent e) {
        super.dataChanged(e);
        System.out.println("PianoRollPanel: Data changed");
        displayPanel.updateDisplay();
        DataChangeManager.getInstance().interestExpandElements(this, cont);

    }

    /**
     * @return Returns the displayPanel.
     */
    public LinearDisplayPanel getDisplayPanel() {
        return displayPanel;
    }
}