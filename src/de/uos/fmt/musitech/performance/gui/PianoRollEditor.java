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
 * Created on 04.05.2005
 */
package de.uos.fmt.musitech.performance.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.MouseEditorUtils;
import de.uos.fmt.musitech.time.TimeRange;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class uses PianoRollPanel to Display Notes in Pianoroll View
 * Nots can be edited with Mouse. You can change Duration, Pitch, 
 * startTime and Velocity. 
 * It is also possible to delete and create Notes
 * 
 * @author Jan
 *  
 */
public class PianoRollEditor extends PianoRollPanel implements Editor {

    JToolBar tools;

    int editMode; // to distinguish differenz mouseEdit Modes

    final static int NOTHING = 0; // do nothing with mouse

    final static int CREATE_PERFNOTE = 1; // create Performance Note with mouse

    final static int EDIT_PERFNOTE = 2; // edit Note with mouse

    final static int VELOCITY_CHANGE = 3; // change velocity with mouse

    final static int DELETE_PERFNOTE = -1; // delete note with mouse

    private boolean showTools = true; // show Toolbar?

    protected class CreateNote extends AbstractAction {

        public CreateNote() {
            super("");

            putValue(Action.MNEMONIC_KEY, new Integer('N'));
            putValue(Action.SHORT_DESCRIPTION, "Create Note");

            putValue(Action.SMALL_ICON, createImageIcon("gfx/pen.png",
                    "Create Note"));
        }

        public void actionPerformed(ActionEvent e) {
            //            pianoRoll.removeMouseListener();
            //            pianoRoll.addMouseEditableListener();
            editMode = CREATE_PERFNOTE;
            pianoRoll.setEditMode(editMode);
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }

    }

    protected class DeleteNote extends AbstractAction {

        public DeleteNote() {
            super("");

            putValue(Action.MNEMONIC_KEY, new Integer('N'));
            putValue(Action.SHORT_DESCRIPTION, "Delete Note");

            putValue(Action.SMALL_ICON, createImageIcon("gfx/eraser.png",
                    "Delete Note"));
        }

        public void actionPerformed(ActionEvent e) {
            //          pianoRoll.removeMouseListener();
            editMode = DELETE_PERFNOTE;

            pianoRoll.setEditMode(editMode);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

    }

    protected class Pointer extends AbstractAction {

        public Pointer() {
            super("");

            putValue(Action.MNEMONIC_KEY, new Integer('1'));
            putValue(Action.SHORT_DESCRIPTION, "Pointer");

            putValue(Action.SMALL_ICON, createImageIcon("gfx/pointer.png",
                    "Pointer"));
        }

        public void actionPerformed(ActionEvent e) {
            editMode = NOTHING;
            pianoRoll.setEditMode(editMode);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

    protected class VelocityChange extends AbstractAction {

        public VelocityChange() {
            super("");

            putValue(Action.MNEMONIC_KEY, new Integer('1'));
            putValue(Action.SHORT_DESCRIPTION, "Velocity Change");

            putValue(Action.SMALL_ICON, createImageIcon("gfx/velo.png",
                    "Velocity Change"));
        }

        public void actionPerformed(ActionEvent e) {
            editMode = VELOCITY_CHANGE;
            pianoRoll.setEditMode(editMode);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

    protected class EditNote extends AbstractAction {

        public EditNote() {
            super("");

            putValue(Action.MNEMONIC_KEY, new Integer('1'));
            putValue(Action.SHORT_DESCRIPTION, "Edit Note");

            putValue(Action.SMALL_ICON, createImageIcon("gfx/pointer_move.png",
                    "Edit Note"));
        }

        public void actionPerformed(ActionEvent e) {
            //            pianoRoll.removeSelectionMouseListener();
            //            pianoRoll.addMouseEditableListener();
            editMode = EDIT_PERFNOTE;
            pianoRoll.setEditMode(editMode);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Editor#applyChanges()
     */
    public void applyChanges() {
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Editor#inputIsValid()
     */
    public boolean inputIsValid() {
        return false;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
     */
    public Collection getEditedData() {
        return null;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Editor#setPromptUpdate(boolean)
     */
    public void setPromptUpdate(boolean promptUpdate) {
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Editor#setDirty(boolean)
     */
    public void setDirty(boolean dirty) {

    }

    /**
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#objectAt(java.awt.Point)
     */
    public MObject objectAt(Point p) {
        return pianoRoll.objectAt(p);
    }

    /**
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#objectsTouched(java.awt.Rectangle)
     */
    public Collection objectsTouched(Rectangle r) {
        return pianoRoll.objectsTouched(r);
    }

    /**
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#timeCovered(java.awt.Rectangle)
     */
    public TimeRange timeCovered(Rectangle r) {
        return pianoRoll.timeCovered(r);
    }

    /**
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#paintDragArea(java.awt.Rectangle)
     */
    public void paintDragArea(Rectangle r) {
        pianoRoll.paintDragArea(r);
    }

    /**
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getMinimalPositionForTime(long,
     *      de.uos.fmt.musitech.utility.math.Rational)
     */
    public int getMinimalPositionForTime(long t, Rational m)
            throws WrongArgumentException {
        return pianoRoll.getMinimalPositionForTime(t, m);
    }

    /**
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#setMinimalPositionForTime(long,
     *      de.uos.fmt.musitech.utility.math.Rational, int)
     */
    public boolean setMinimalPositionForTime(long t, Rational m, int position)
            throws WrongArgumentException {
        return pianoRoll.setMinimalPositionForTime(t, m, position);
    }

    /**
     * Creates new Toolbar, if it was not created before
     * @return toolBar 
     */
    public JToolBar getToolBar() {
        if (tools == null) {
            tools = new JToolBar();
            tools.setLayout(new FlowLayout(FlowLayout.CENTER));
            tools.setPreferredSize(new Dimension(50, 50));
            JPanel gabPanel = new JPanel();
            gabPanel.setPreferredSize(new Dimension(30, 30));
            tools.add(gabPanel);

            JButton pointerButton = new JButton(new Pointer());
            tools.add(pointerButton);

            JButton editButton = new JButton(new EditNote());
            tools.add(editButton);

            JButton noteButton = new JButton(new CreateNote());
            tools.add(noteButton);

            JButton deleteButton = new JButton(new DeleteNote());
            tools.add(deleteButton);

            JButton veloButton = new JButton(new VelocityChange());
            tools.add(veloButton);

            AbstractAction action = new VelocityChange();

        }
        return tools;
    }

    /**
     * Creates new Performance note within dragged Rectangle
     * @param rec
     */
    public void createNote(Rectangle rec) {
        // calculate StartTime
        long start = Math.round(rec.getX() * pianoRoll.getMicrosPPix())
                + pianoRoll.getOffset_us();

        // calculate Duration
        long dur = Math.round(rec.getWidth() * pianoRoll.getMicrosPPix());
        if (dur < 10000) {
            // dur should be at least 10 ms
            dur = 10000;
        }
        // calculate Pitch
        int pitch = pianoRoll.getDisplayRangeMax()
                - 1
                - (int) ((rec.getY() / pianoRoll.getHeight()) * pianoRoll
                        .getNumberOfVNote());

        //        Note note = new Note(new PerformanceNote(start, dur, 80, pitch));
        PerformanceNote note = new PerformanceNote(start, dur, 80, pitch);

        pianoRoll.getContainer().add(note);
        sendDataChangeEvent(pianoRoll.getContainer(), note);
    }

    /**
     *  send DataChange Event to DataChangeManager with new created Note
     */
    private void sendDataChangeEvent(Container cont, Note note) {
        ArrayList changeList = new ArrayList();
        changeList.add(cont);
        changeList.add(note);
        DataChangeManager.getInstance().changed(changeList,
                new DataChangeEvent(this, changeList));
    }

    /**
     *  send DataChange Event to DataChangeManager with new created Note
     */
    private void sendDataChangeEvent(Container cont, PerformanceNote note) {
        ArrayList changeList = new ArrayList();
        changeList.add(cont);
        changeList.add(note);
        DataChangeManager.getInstance().changed(changeList,
                new DataChangeEvent(this, changeList));
    }

    /**
     * @see de.uos.fmt.musitech.performance.gui.PianoRollPanel#createGUI()
     */
    public void createGUI() {
        super.createGUI();
        if (showTools = true) {
            add(getToolBar(), BorderLayout.WEST);
            pianoRoll.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                    pressed = e.getPoint();
                }

                public void mouseReleased(MouseEvent e) {
                    released = e.getPoint();
                    if (editMode == CREATE_PERFNOTE) {
                        Rectangle rec = MouseEditorUtils
                                .calculateDragRectangle(pressed, released);
                        createNote(rec);
                        //                        create = NOTHING;
                        //                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }

            });
        }
    }

    /**
     * true: Tools are displayed. With Tools you can edit and create notes
     * false: tools are not displayed
     * 
     * @return tools are displayed
     */
    public boolean isShowTools() {
        return showTools;
    }

    /**
     * true: Tools are displayed. With Tools you can edit and create notes
     * false: tools are not displayed
     *  
     */
    public void setShowTools(boolean showMenu) {
        this.showTools = showMenu;
        createGUI();
    }

    /**
     * @see de.uos.fmt.musitech.performance.gui.PianoRollPanel#setContainer(de.uos.fmt.musitech.data.structure.container.Container)
     */
    public void setContainer(Container container) {
        super.setContainer(container);
        DataChangeManager.getInstance().interestExpandObject(this, container);
        DataChangeManager.getInstance().interestExpandElements(this, container);
    }

}