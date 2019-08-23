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
package de.uos.fmt.musitech.performance.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeListener;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.MouseEditable;
import de.uos.fmt.musitech.framework.editor.MouseEditableAdapter;
import de.uos.fmt.musitech.framework.selection.SelectingEditor;
import de.uos.fmt.musitech.framework.selection.Selection;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionController;
import de.uos.fmt.musitech.framework.selection.SelectionListener;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.structure.form.gui.TimeScale;
import de.uos.fmt.musitech.time.TimeRange;
import de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay;
import de.uos.fmt.musitech.time.gui.LinearDisplay;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * Piano roll display of PerformanceNotes in a container.
 * 
 * @author Tillman Weyde
 */
public class PianoRollContainerDisplay extends JPanel implements
        SelectingEditor, Timeable, SelectionListener, HorizontalTimedDisplay,
        Display, DataChangeListener, LinearDisplay {

    int pxStart;

    int pxMove;

    static int calcCount = 0;

    public MidiNoteSequence noteSequence = null;

    private long startTime = 0;

    private Container container;

    private double microsPPix = 50000;

    public long offset_us = 0; // in us

    private long offset_pix = 0; // for HorizontalDisplay in pixels

    /**
     * hScale = milliseconds/pixels
     */
    // public double hScale = 1;
    public double vScale = 4;

    protected List noteDisplays = new ArrayList();

    public boolean dragEnabled = false;

    public boolean dropEnabled = false;

    public int[] groupSelection = new int[] { 0, 0 };

    protected java.awt.Color defaultNoteColor;

    protected java.awt.Color highlightNoteColor;

    // Use the full JPanel width

    protected boolean autoHScale;

    // Use the full JPanel heigth
    public Hashtable<Container<?>,Color> hashtable = new Hashtable<Container<?>,Color>();

    // color to container

    /**
     * ToDo: Eine checkBox fuer autoVScale muss noch der Oberflaeche
     * hinzugefuegt werden!
     */
    protected boolean autoVScale = true;

    /** Comment for <code>PIANOROLL</code> */
    static public final int PIANOROLL = 0;

    static public final int VELOCITY = 1;

    protected int displayType = PIANOROLL;

    // de.uos.fmt.musitech.framework.user.MainSettings.getDefaultDisplayType();

    // Don't show notes in pianoroll display typ when the note is lower than
    private int displayRangeMin = 36;

    // Don't show notes in pianoroll display typ when the note is upper than
    private int displayRangeMax = 96;

    protected int i = 0;

    long time = -Long.MIN_VALUE;

    long lastRepaint = 0;

    private SelectionController selectionController;

    TimeRange tr;

    boolean dragged = false;

    // some applications don't need MouseListeners, so they can't disable them
    private boolean mouseListenerDisable = false;

    int editMode = 0;

    /**
     * NoteSequneceDisplay constructor.
     */
    public PianoRollContainerDisplay() {
        super();
        initialize();

        SelectionManager.getManager().addListener(this);
        setSelectionController(new SelectionController(this));
        addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicked");
                if (e.isPopupTrigger()) {
                    Object source = e.getSource();
                    if (source instanceof PianoRollContainerDisplay)
                        ((PianoRollContainerDisplay) source).showContextMenu(e
                                .getX(), e.getY());
                }

            }
        });

    }

    /**
     * NoteSequenceDisplay constructor.
     * 
     * @param cont
     *            Container
     */
    public PianoRollContainerDisplay(Container cont) {
        this();
        setContainer(cont);
    }

    /**
     *  
     */
    void createGUI() {
        if (container != null) {
            createPianoRollNoteDisplays(container);
            if (!isMouseListenerDisable()) {
                addMouseListener();
            }
            updateDisplay();
        }
    }

    public void setContainer(Container cont) {
        container = cont;
        if (cont != null) {
            //            DataChangeManager.getInstance().interestExpandElements(this,
            // cont);
            DataChangeManager.getInstance().interestExpandObject(this, cont);
            //        setNoteSequence(MidiNoteSequence.convert(cont));
            createGUI();
        }
    }

    public Container getContainer() {
        return container;
    }

    /**
     * Calculates the PianoRollNoteDisplay's size and position in the JPanel.
     */
    public void calculateNoteDisplays() {
        if (container == null)
            return;

        // Height of the JPanel
        int jPHeight = getSize().height;

        //		if (isAutoHScale())
        //			hScale = getPreferredHScale();

        //		if (!(hScale > 0))
        //			return;

        if (isAutoVScale() && getDisplayType() == PIANOROLL)
            vScale = getPreferredVScale();

        double winWidth = getWidth() * microsPPix; // window width in us
        for (int i = 0; i < noteDisplays.size(); i++) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) noteDisplays
                    .get(i);
            if (nd == null)
                continue;
            PerformanceNote n = nd.getNote();
            long w = 3;
            long h = 3;
            long x = 3;
            long y = 3;
            // calculate the PianoRollNoteDisplay's size
            if (displayType == PIANOROLL) {
                // We don't display notes under DisplayRangeMin and over
                // DisplayRangeMax
                // pitch
                if (displayRangeMin <= n.getPitch()
                        && n.getPitch() <= displayRangeMax) {
                    // note begins left of visible window
                    if (n.getTime() < offset_us) {

                        if (n.getTime() + n.getDuration() < offset_us) {
                            //                          notes end is left of visible window
                            w = 0;
                            x = -1;
                        }

                        else {
                            //                          notes end is in visible window
                            w = n.getDuration() - (offset_us - n.getTime());
                            x = 0;
                        }
                    }

                    else {
                        // note begins right of offset_us

                        if (n.getTime() < offset_us + winWidth) {
                            //                          note begins in visible window
                            x = n.getTime() - offset_us;
                            w = Math.round(Math.min(offset_us + winWidth - x, n
                                    .getDuration()));
                        }
                        // note begins right of visible window
                        else {
                            x = Math.round(winWidth) + 1; // start is at
                            // window's right
                            // end
                            w = 0;
                        }
                    }
                    //					w = Math.max(1, (int) ((double) n.getDuration() /
                    // getMicrosPPix()));

                    // We shift the NoteDisplays about h to the right side.
                    //					x = (int) ((double) n.getTime() / getMicrosPPix());
                    w = Math.round(w / microsPPix);
                    // Width should be at least 4 pixels
                    if (w < 4)
                        w = 4;
                    h = (int) vScale + 1;
                    x = Math.round(x / microsPPix);
                    y = (int) ((double) jPHeight + (displayRangeMin - 1 - n
                            .getPitch())
                            * vScale);
                }
            }
            if (displayType == VELOCITY) {
                w = Math.max(1,
                        (int) ((double) n.getDuration() * getMicrosPPix()));
                h = (int) ((double) n.getVelocity() * vScale / 5);
                //We shift the NoteDisplays about vScale + 1 to the right side.
                x = (int) ((double) n.getTime() * getMicrosPPix())
                        + (int) vScale + 1;
                y = jPHeight - 10 - h;
            }
            // and set the size
            if ((displayRangeMin <= n.getPitch() && n.getPitch() <= displayRangeMax)
                    || (displayType == VELOCITY)) {
                nd.setBounds((int) x, (int) y, (int) w, (int) h);
                nd.setToolTipText("MidiNote " + i + ": begin: " + n.getTime()
                        + ", length: " + n.getDuration() + ",velocity: "
                        + n.getVelocity()
                        //					+ ", deviation: "
                        //					+ n.offset_us
                        + ", pitch: " + n.getPitch());
            }
        }
    }

    /**
     * Writes the position of a note in the current view into the long[]
     * position. This is the numerical position, i.e. it may contain negative or
     * very large values, if the not is outside the currently displayed range.
     * 
     * @param note
     * @param position
     *            [0]: x Position, [1]: y Position, [2]: width, [3]: height
     */
    public void notePosition(Note note, long[] position) {
        double winWidth = getWidth() * microsPPix; // window width in us
        // x Position
        position[0] = Math.round((note.getTime() - offset_us) / microsPPix);
        // y Position
        position[1] = (int) (getSize().height + (displayRangeMin - 1 - note
                .getMidiPitch())
                * vScale);
        // width
        position[2] = Math.round(Math.min(offset_us + winWidth - x, note
                .getDuration())
                / microsPPix);
        // height
        position[3] = Math.round(vScale + 1);
    }

    public long getOffset_us() {
        return offset_us;
    }

    /**
     * Gets the pianoRollNoteDisplay for this note.
     * 
     * @param note
     * @return
     */
    public PianoRollNoteDisplay displayForNote(Note note) {
        for (Iterator iter = noteDisplays.iterator(); iter.hasNext();) {
            PianoRollNoteDisplay display = (PianoRollNoteDisplay) iter.next();
            if (display.getNote().equals(note.midiNote()))
                return display;
        }
        return null;
    }

    /**
     * To get the autoHScale field
     */
    public boolean getAutoHScale() {
        return autoHScale;
    }

    /**
     * Returns the default note color.
     * 
     * @return java.awt.Color
     */
    public java.awt.Color getDefaultNoteColor() {
        return defaultNoteColor;
    }

    /**
     * Returns the group selection.
     * 
     * @return int[]
     */
    public int[] getGroupSelection() {
        if (groupSelection[0] < groupSelection[1])
            return groupSelection;
        else
            return null;
    }

    /**
     * Returns the highlight note color.
     * 
     * @return java.awt.Color
     */
    public java.awt.Color getHighlightNoteColor() {
        return highlightNoteColor;
    }

    /**
     * Returns the noteSequence.
     * 
     * @return RhythmData.MidiNoteSequence
     */
    public MidiNoteSequence getNoteSequence() {
        return noteSequence;
    }

    /**
     * Returns the VScale value.
     * 
     * @return double
     */
    public double getVScale() {
        return vScale;
    }

    /**
     * Number of Notes, that can be displayed vertically Calculated by
     * JPanel.height / VScale
     * 
     * @return number of Notes, that can be displayed vertically
     */
    public int getNumberOfVNote() {
        return (int) Math.round(getHeight() / vScale);
    }

    /**
     * Called whenever the part throws an exception.
     * 
     * @param exception
     *            java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("PianoRollContainerDisplay");
            setOpaque(true);
            setLayout(null);
            setBackground(java.awt.Color.white);
            setBounds(new java.awt.Rectangle(0, 0, 450, 120));
            setSize(450, 120);
            setMinimumSize(new java.awt.Dimension(20, 120));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        setOpaque(false);

        // user code end

    }

    /**
     * Sets the default note color.
     * 
     * @param newDefaultNoteColor
     *            java.awt.Color
     */
    public void setDefaultNoteColor(java.awt.Color newDefaultNoteColor) {
        defaultNoteColor = newDefaultNoteColor;
    }

    /**
     * This method gets a integer Vector of two elements. These two elements are
     * two index-values of a PianoRollNoteDisplay to define a GroupSelection.
     * 
     * @param newGroupSelection
     *            int[]
     */
    public void setGroupSelection(int[] newGroupSelection) {
        groupSelection = newGroupSelection;
        int i = 0;
        //The NoteDisplays on the left side of the groupSelection are not
        // selected
        while (i < groupSelection[0] && i < noteDisplays.size()) {
            ((PianoRollNoteDisplay) noteDisplays.get(i)).setSelected(false);
            i++;
        }
        //The NoteDisplays as part of the groupSelection are selected
        while (i < groupSelection[1] && i < noteDisplays.size()) {
            ((PianoRollNoteDisplay) noteDisplays.get(i)).setSelected(true);
            i++;
        }
        //The NoteDisplays on the right side of the groupSelection are not
        // selected
        while (i < noteDisplays.size()) {
            ((PianoRollNoteDisplay) noteDisplays.get(i)).setSelected(false);
            i++;
        }
    }

    /**
     * This method gets a integer Vector of two elements. These two elements are
     * x-coordinats of NoteDisplays defining a selection. The x-values results
     * from mouse-events caused by the clicking user. The left value defines the
     * beginning and the right the end of the selection. The methode finds for
     * each x-value the corresponding index of the PianoRollNoteDisplay. At
     * least two index-values are given to the method setGroupSelection.
     * 
     * @date (04.06.00 13:28:26)
     * @param newGroupSelection
     *            int[]
     */
    public void setGroupSelectionByXPosition(int[] xRange) {
        //sort range boundary
        int lowX = xRange[(xRange[0] < xRange[1] ? 0 : 1)];
        int hiX = xRange[(xRange[0] < xRange[1] ? 1 : 0)];
        //Init first and last.
        int first = Integer.MAX_VALUE, last = Integer.MIN_VALUE;
        int size = noteDisplays.size();
        int i = 0;
        //first gets the index of the left PianoRollNoteDisplay
        while (i < size) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) noteDisplays
                    .get(i);
            if (nd.getBounds().x + nd.getBounds().width >= lowX) {
                first = i;
                break;
            }
            i++;
        }
        //last gets the index of the right PianoRollNoteDisplay
        for (; i < size; i++) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) noteDisplays
                    .get(i);
            if (nd.getBounds().x > hiX) {
                last = i;
                break;
            }
        }
        if (i == size)
            last = size;
        setGroupSelection(new int[] { first, last });
    }

    /**
     * Sets the default highlight note color.
     * 
     * @param newHighlightNoteColor
     *            java.awt.Color
     */
    public void setHighlightNoteColor(java.awt.Color newHighlightNoteColor) {
        highlightNoteColor = newHighlightNoteColor;
    }

    /**
     * Sets the HScale value.
     * 
     * @param newHScale
     *            double
     */
    //	public void setHScale(double newHScale) {
    //		hScale = newHScale;
    //		calculateNoteDisplays();
    //	}
    /**
     * Set the PianoRollContainerDisplay's MidiNoteSequence
     * 
     * @param newNoteSequence
     *            RhythmData.MidiNoteSequence
     */
    public void setNoteSequence(MidiNoteSequence newNoteSequence) {

        noteSequence = newNoteSequence;
        if (noteSequence != null) {
            createPianoRollNoteDisplays(noteSequence);
            calculateNoteDisplays();
        }
    }

    private void createPianoRollNoteDisplays(Container cont) {
        removeAll();
        // for all Notes
        for (int i = 0; i < cont.size(); i++) {
            //            if (cont.get(i) instanceof PerformanceNote)
            PerformanceNote n = null;
            if (cont.get(i) instanceof Note) {
                n = ((Note) cont.get(i)).getPerformanceNote();
            }
            if (cont.get(i) instanceof PerformanceNote) {
                n = (PerformanceNote) cont.get(i);
            }
            if (n != null) {
                PianoRollNoteDisplay nd = new PianoRollNoteDisplay(n);
                //Klaus Dalinghaus KD : not useful for my purpose

                //                    nd.addMouseListener(getSelectionController().getSelectionAdapter());
                //                    if (!mouseListenerDisable) {
                //                        MouseEditableAdapter mouseEdit = new MouseEditableAdapter(nd);
                //                        nd.addMouseListener(mouseEdit.getMouseListener());
                //                        nd.addMouseMotionListener(mouseEdit
                //                                .getMouseMotionListern());
                //                    }
                add(nd);
//              noteDisplays.add(i, nd); 
                noteDisplays.add(nd); //TODO check if change from previous line is correct. 
                // each PianoRollNoteDisplay should know its number
                nd.setNumber(i);
            }

        }
    }

    /**
     * connEtoC1:
     * (PianoRollContainerDisplay.component.componentResized(java.awt.event.ComponentEvent)
     * --> PianoRollContainerDisplay.calculateNoteDisplays()V)
     * 
     * @param arg1
     *            java.awt.event.ComponentEvent
     */
    private void connEtoC1(java.awt.event.ComponentEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.calculateNoteDisplays();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * To get the autoVScale field
     */
    public boolean getAutoVScale() {
        return autoVScale;
    }

    /**
     * Returns the display type.
     * 
     * @return the display type
     *  
     */
    public int getDisplayType() {
        return displayType;
    }

    /**
     * Calculates the HScale to use the full JPanel width.
     * 
     * @return The preferred horizontal scale.
     */
    public double getPreferredHScale() {
        if (noteDisplays.size() != 0) {
            /**
             * Get the last noteDisplay and its length to calculate the
             * NoteDisplays duration
             */
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) noteDisplays
                    .get(noteDisplays.size() - 1);
            if (nd != null) {
                PerformanceNote n = nd.getNote();
                double duration = n.getTime() + n.getDuration();
                int jPanelWidth = getWidth();

                double prefHScale = (jPanelWidth - 10) / duration;
                if (!(prefHScale > 0))
                    return -1;
                else
                    return prefHScale;
            }
        }
        /** Unable to calculate the preferred HScale */
        return -1;
    }

    /**
     * Calculates the vScale to use the full JPanel height. Notes with a pitch
     * above 99 won't be displayed. // TODO correct this
     * 
     * @return The preferred vertical scaling.
     */
    public double getPreferredVScale() {
        int jPHeight = getHeight();
        double Nenner = (displayRangeMax - displayRangeMin);
        double prefVScale = jPHeight / Nenner;
        /**
         * Case the scale factor is too small: otherwise the lines are to near
         */
        if (prefVScale < 2) {
            return 2;
        }
        /**
         * Case the scale factor is too big: otherwise the bounds are to far
         * apart
         */
        if (prefVScale > 8) {
            return 8;
        }
        return prefVScale;
    }

    /**
     * Returns the preferred size
     * 
     * @return the preferred size
     */
    public java.awt.Dimension getPreferredSize() {
        return new Dimension(500, 300);
    }

    /**
     * Calculates the displayed distance in pixel between two seconds for the
     * displayType PIANOROLL.
     * 
     * @return int
     */
    public int getSecondInPixel() {
        // XXX hallo alex, hier gab's einen Fehler bei noteDisplays.size() == 0;
        // Wofür war das überhaupt gut? TW
        //PianoRollNoteDisplay lastND = (PianoRollNoteDisplay)
        // noteDisplays.elementAt(noteDisplays.size() - 1);
        //We have to multiply with 1000 because begin is in milliseconds.
        return (int) (1000000 / getMicrosPPix());
    }

    /**
     * Returns true if autoHScale is true.
     * 
     * @return boolean
     */
    public boolean isAutoHScale() {
        return autoHScale;
    }

    /**
     * Returns true if autoVScale is true.
     * 
     * @return boolean
     */
    public boolean isAutoVScale() {
        return autoVScale;
    }

    /**
     * Calculates the noteDisplays and repaints. Draws lines for the halftones
     * and vertical lines for each second if the displayType is PIANOROLL.
     * 
     * @param g
     *            java.awt.Graphics
     */
    public void paintComponent(java.awt.Graphics g) {

        super.paintComponent(g);

        calculateNoteDisplays();
        int jPWidth = getSize().width;
        int jPHeight = getSize().height;

        if (getMicrosPPix() > 0 && getVScale() > 0) {

            if (displayType == PIANOROLL) {
                //Draw the painoroll background
                if (g != null) {
                    //Set the background line color
                    g.setColor(new java.awt.Color(164, 197, 255));
                    //Draw the painoroll background
                    for (int j = jPHeight - (int) vScale; j > 0; j -= vScale) {
                        g.drawLine(0, j, jPWidth, j);
                    }
                }
            }

            /**
             * Draw vertical lines for each second
             */
            g.setColor(new java.awt.Color(204, 227, 255));
            //            g.setColor(new java.awt.Color(255, 0, 255));
            /**
             * We have shifted all PianoRollNoteDisplay a little bit to the
             * right side. The exact value depends from vScale. So a small
             * window has got a small border (means shift of the
             * PianoRollNoteDisplay's) and a big windows has a got a big border.
             */
            int rShift = (int) vScale + 1;
            for (double j = ((-offset_us % 1000000.0) / microsPPix); j < jPWidth; j += majorTickSpace) {
                //We shift pixel because the PianoRollNoteDisplay is shifted
                // right too.
                g.drawLine((int) j, 0, (int) j, jPHeight);
            }

            /**
             * Draw one line to seperate input and task in InterpretationDisplay
             * for example.
             */
            g.setColor(new java.awt.Color(100, 100, 100));
            g.drawLine(0, 0, jPWidth, 0);

            if (tr != null) {
                paintRange(g, Color.CYAN);
            }

            //paintCursor(g, g.getClipBounds());
            g.setPaintMode();
        }
    }

    /**
     * Sets the autoHScale.
     * 
     * @param newAutoHScale
     *            boolean
     */
    public void setAutoHScale(boolean newAutoHScale) {
        autoHScale = newAutoHScale;
        calculateNoteDisplays();
    }

    /**
     * Sets the autoVScale.
     * 
     * @param newAutoVScale
     *            boolean
     */
    public void setAutoVScale(boolean newAutoVScale) {
        autoVScale = newAutoVScale;
        calculateNoteDisplays();
    }

    /**
     * Set the DisplayType to PianoRollContainerDisplay and all
     * PianoRollNoteDisplay's
     * 
     * @param newDisplayType
     *            boolean
     */
    public void setDisplayType(int newDisplayType) {
        displayType = newDisplayType;
        for (int i = 0; i < noteDisplays.size(); i++) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) noteDisplays
                    .get(i);
            nd.setDisplayType(newDisplayType);
        }
    }

    /**
     * Sets the VScale value.
     * 
     * @param newVScale
     *            int
     */
    public void setVScale(int newVScale) {
        vScale = newVScale;
    }

    /**
     * Draws a line at the position representing the given TimeStamp.
     * 
     * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
     */
    public void setTimePosition(long timeMillis) {
        Rectangle bounds = getBounds();
        ////paintCursor(getGraphics(), bounds);
        time = timeMillis;
        ////paintCursor(getGraphics(), bounds);
        //		if(Math.abs(time-lastRepaint)>500){
        //			lastRepaint=time;
        //			SwingUtilities.invokeLater(new Runnable(){public void
        // run(){repaint();}});
        //		}
    }

    /**
     * Paint the cursor to the graphics with in the bounds.
     * 
     * @param g
     *            The graphics context.
     * @param bounds
     *            The bounds to draw within.
     */
    void paintCursor(Graphics g, Rectangle bounds) {
        int cursorX = (int) (time * getMicrosPPix());
        g.setXORMode(Color.CYAN);
        g.drawLine(cursorX, (int) bounds.getY(), cursorX,
                (int) (bounds.getY() + bounds.getHeight()));
    }

    int oldLeftX;

    int oldRightX;

    int oldUpperY;

    int oldLowerY;

    /**
     * Method getHScale gets the horizontal scaling factor.
     * 
     * @return double
     */
    public double getMicrosPPix() {
        //		if (autoHScale) {
        //			hScale = getPreferredHScale();
        //		}
        return microsPPix;
    }

    /**
     * paints a range
     * 
     * @param g
     *            grapic object
     * @param c
     *            color of the range
     */
    public void paintRange(Graphics g, Color c) {

        int leftX = (int) (tr.getStart() * getMicrosPPix());
        int rightX = (int) (tr.getEnd() * getMicrosPPix());

        int breite = (int) ((rightX - leftX));

        g.setXORMode(Color.BLUE);
        g.fillRect(leftX, 0, breite, this.getHeight());
        g.setPaintMode();

    }

    public static void main(java.lang.String[] args) {
        // Create the frame
        JFrame frame = new JFrame("PianoRollContainerDisplay");
        final PianoRollContainerDisplay pRCDisplay = new PianoRollContainerDisplay();
        final PianoRollContainerDisplay pRC = new PianoRollContainerDisplay();
        MidiNoteSequence ns = new MidiNoteSequence();

        try {
            ns.addNote(new MidiNote(0, 50, 30, 51));
            ns.addNote(new MidiNote(62, 50, 40, 60));
            ns.addNote(new MidiNote(182, 280, 60, 70));
            ns.addNote(new MidiNote(1000, 380, 10, 53));
            pRCDisplay.setNoteSequence(ns);
            //pRCDisplay.setAutoHScale(true);
            pRCDisplay.setDisplayType(PIANOROLL);

            pRC.setNoteSequence(ns);
            pRC.setAutoHScale(true);
            pRC.setDisplayType(VELOCITY);

            frame.getContentPane().setBackground(Color.white);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.setSize(500, 400);
            frame.getContentPane().add(BorderLayout.CENTER, pRC);
            frame.getContentPane().add(BorderLayout.NORTH, pRCDisplay);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {

                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err
                    .println("Exception occurred in main() of java.awt.Container");
            exception.printStackTrace(System.out);
        }
        Thread t = new Thread() {

            public void run() {
                long time = 1;
                while (true) {
                    pRCDisplay.setTimePosition(time);
                    time = (time + 10) % 3000;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        };
        t.start();
    }

    /**
     * @see de.uos.fmt.musitech.gui.display.selection.SelectingEditor#differingSelectionArea()
     */
    public void differingSelectionArea() {
    }

    /**
     * @see de.uos.fmt.musitech.gui.display.selection.SelectingEditor#objectAt(Point)
     */
    public MObject objectAt(Point p) {

        Component c = getComponentAt(p);
        if (c instanceof PianoRollNoteDisplay) {
            return ((PianoRollNoteDisplay) c).getNote();
        }

        return null;
    }

    /**
     * @see de.uos.fmt.musitech.gui.display.selection.SelectingEditor#objectsCovered(Rectangle)
     */
    public MObject[] objectsCovered(Rectangle r) {
        return null;
    }

    public void setSelectionController(SelectionController c) {
        if (this.selectionController == c) {
            return;
        }
        removeMouseListener(this.selectionController);
        removeMouseMotionListener(this.selectionController);
        this.selectionController = c;
        addMouseListener(c);
        addMouseMotionListener(c);
    }

    /**
     * @see de.uos.fmt.musitech.framework.selection.SelectionListener#selectionChanged(SelectionChangeEvent)
     */
    public void selectionChanged(SelectionChangeEvent e) {
        Component[] children = getComponents();

        for (int i = 0; i < children.length; i++) {

            if (children[i] instanceof PianoRollNoteDisplay) {
                PianoRollNoteDisplay display = ((PianoRollNoteDisplay) children[i]);
                PerformanceNote gotnote = display.getNote();

                if (e.removedObjects.contains(gotnote)) {
                    display.setHighlight(false);
                } else if (e.addedObjects.contains(gotnote)) {
                    display.setHighlight(true);
                }

            }
        }
    }

    /**
     * @see de.uos.fmt.musitech.gui.display.selection.SelectingEditor#objectsTouched(Rectangle)
     */
    public Collection objectsTouched(Rectangle r) {

        Vector v = new Vector();
        for (Iterator it = noteDisplays.iterator(); it.hasNext();) {
            PianoRollNoteDisplay element = (PianoRollNoteDisplay) it.next();

            if (element.getBounds().intersects(r))
                v.add(element.getNote());
        }

        return v;
    }

    /**
     * @see de.uos.fmt.musitech.gui.display.selection.SelectingEditor#paintDragArea(Rectangle)
     */
    public void paintDragArea(Rectangle r) {

        Graphics g = getGraphics();
        g.setXORMode(Color.DARK_GRAY);

        if (oldLeftX != oldRightX)
            g.fillRect(oldLeftX, oldUpperY, oldRightX - oldLeftX, oldLowerY
                    - oldUpperY);

        if (r != null) {

            g.fillRect(r.x, r.y, r.x + r.width - r.x, r.y + r.height - r.y);
            oldLeftX = r.x;
            oldRightX = r.x + r.width;
            oldUpperY = r.y;
            oldLowerY = r.y + r.height;
        } else {
            oldLeftX = oldRightX = oldUpperY = oldLowerY = 0;
        }

        g.setPaintMode();
    }

    /**
     * @see de.uos.fmt.musitech.gui.display.selection.SelectingEditor#timeCovered(Rectangle)
     */
    public TimeRange timeCovered(Rectangle r) {
        return null;
    }

    /**
     * Returns the selectionController.
     * 
     * @return SelectionController
     */
    public SelectionController getSelectionController() {
        return selectionController;
    }

    /**
     * shows context menu at the clicked position this menu provides items to
     * insert Elements into group of displays and to remove Elemets after
     * insertion
     * 
     * @see de.uos.fmt.musitech.gui.display.selectoin.SelectionController
     */

    public void showContextMenu(int x, int y) {

        JPopupMenu popUp = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Group Elements");
        JMenuItem menuItem2 = new JMenuItem("Delete Elements");
        popUp.add(menuItem1);
        popUp.add(menuItem2);

        menuItem1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {

                BasicContainer bc = new BasicContainer();

                Selection s = SelectionManager.getManager().getSelection();
                bc.addAll(s.getAll());
                addDisplayGroup(bc);
            }
        });

        menuItem2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if ((!hashtable.isEmpty())) {
                    Container c = (Container) (hashtable.keys().nextElement());
                    rmDisplayGroup(c);
                }
            }
        });

        popUp.show(this, x, y);
    }

    /**
     * method to remove a container from a group if a child of this
     * PianoRollContainerDisplay is an instance of PianoRollNoteDisplay and is
     * member of the group the appropriate colored JPanel will be removed
     */
    public void rmDisplayGroup(Container container) {
        if (container == null)
            return;

        Component[] children = getComponents();

        for (int i = 0; i < children.length; i++) {

            if (children[i] instanceof PianoRollNoteDisplay) {
                PianoRollNoteDisplay display = ((PianoRollNoteDisplay) children[i]);

                PerformanceNote gotnote = display.getNote();

                for (Iterator iter = container.iterator(); iter.hasNext();) {
                    Containable groupElement = (Containable) iter.next();

                    if (groupElement.equals(gotnote)) {

                        display.rmColor((Color) hashtable.get(container));

                    }
                }
            }
        }
        hashtable.remove(container);
        repaint();
    }

    /**
     * method to add a container to a group if a child of this
     * PianoRollContainerDisplay is an instance of PianoRollNoteDisplay and is
     * member of the group a new colored JPanel will be inserted
     * 
     * @see PianoRollNoteDisplay
     * @param argContainer
     */

    public void addDisplayGroup(Container<?> argContainer) {

        Color c = getFreeColor(argContainer);
        hashtable.put(argContainer, c);

        Component[] children = getComponents();

        for (int i = 0; i < children.length; i++) {

            if (children[i] instanceof PianoRollNoteDisplay) {
                PianoRollNoteDisplay display = ((PianoRollNoteDisplay) children[i]);

                PerformanceNote gotnote = display.getNote();

                for (Iterator iter = argContainer.iterator(); iter.hasNext();) {
                    Containable groupElement = (Containable) iter.next();

                    if (groupElement.equals(gotnote)) {

                        display.addColor(c);

                    }

                }
            } else
                System.out.println("");
        }

    }

    public Enumeration getDisplayGroups() {
        return hashtable.keys();
    }

    static int x;

    static int y;

    static int z = 0;

    static int ColorCounter = 0;

    static Color[] c = { new Color(0, 255, 255), new Color(255, 255, 0),
            new Color(255, 0, 255) };

    /**
     * each time this method is invoked, a new color is calculated.
     * 
     * the method could be optimized to use the colorspace more completely
     */
    private void calculateNewColors() {
        Color[] oldColors = c;
        c[0] = new Color((oldColors[0].getRed() + oldColors[1].getRed()) / 2,
                (oldColors[0].getGreen() + oldColors[1].getGreen()) / 2,
                (oldColors[0].getBlue() + oldColors[1].getBlue()) / 2);
        c[1] = new Color((oldColors[0].getRed() + oldColors[2].getRed()) / 2,
                (oldColors[0].getGreen() + oldColors[2].getGreen()) / 2,
                (oldColors[0].getBlue() + oldColors[2].getBlue()) / 2);
        c[2] = new Color((oldColors[2].getRed() + oldColors[1].getRed()) / 2,
                (oldColors[2].getGreen() + oldColors[1].getGreen()) / 2,
                (oldColors[2].getBlue() + oldColors[1].getBlue()) / 2);
    }

    /**
     * provides a free color
     * 
     * @param basicContainer
     * @return a new color wich is not assigned already
     */
    public Color getFreeColor(Container basicContainer) {
        if (ColorCounter > c.length - 1) {
            calculateNewColors();
            ColorCounter = 0;
        }
        return c[ColorCounter++];
    }

    /**
     * getMinimalPositionForTime
     * 
     * @see de.uos.fmt.musitech.gui.display.HorizontalTimedDisplay#getMinimalPositionForTime(long,
     *      de.uos.fmt.musitech.utility.Rational)
     */
    // TODO selektion sichtbar machen z.B. rand colorieren
    // roter/grauer streifen-problem lösen(layout-manager)
    // normales Verhalten der Selektion
    // TODO Methode zur Darstellung von Grppierungen unterschiedlicher
    // Farben PaintMethode der PianoRollNoteDisplay flaggenmaessig verändern
    /**
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getPositionForTime(de.uos.fmt.musitech.data.structure.Timed,
     *      de.uos.fmt.musitech.data.structure.Metrical)
     */
    public int getMinimalPositionForTime(long time, Rational m)
            throws WrongArgumentException {
        if (time == Timed.INVALID_TIME) {
            MetricalTimeLine timeLine = container.getContext().getPiece()
                    .getMetricalTimeLine();
            time = timeLine.getTime(m);
        }
        return (int) Math.round(((time - offset_us) / microsPPix));

        //        if (t != Timed.INVALID_TIME) {
        //            System.out.println("getMiniPos: t: " + t + " retPos: " + (int)
        // Math.round(t / microsPPix));
        //            return (int) Math.round(t / microsPPix);
        //        } else {
        //            MetricalTimeLine timeLine =
        // container.getContext().getPiece().getMetricalTimeLine();
        //            long time = timeLine.getTime(m);
        //            return (int) Math.round(time / microsPPix);
        //        }

    }

    /**
     * 
     * @see de.uos.fmt.musitech.gui.display.HorizontalTimedDisplay#getNextPositioningTime(long)
     */
    public long getNextPositioningTime(long startTime) {
        long nextTime = 0;
        for (int i = 0; i < noteDisplays.size(); i++) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) noteDisplays
                    .get(i);
            PerformanceNote note = nd.getNote();
            if (note.getTime() > startTime) {
                System.out.println("pianoRollCD.getNextPosTime startTime: "
                        + startTime + " nextTime: " + note.getTime());
                return note.getTime();

            }
        }
        return Timed.INVALID_TIME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uos.fmt.musitech.gui.display.HorizontalTimedDisplay#setMinimalPositionForTime(de.uos.fmt.musitech.data.structure.Timed,
     *      de.uos.fmt.musitech.data.structure.Metrical, int)
     */
    public boolean setMinimalPositionForTime(long t, Rational m, int position)
            throws WrongArgumentException {
        if (t == Timed.INVALID_TIME) {
            MetricalTimeLine timeLine = container.getContext().getPiece()
                    .getMetricalTimeLine();
            t = timeLine.getTime(m);
        }
        System.out
                .println("PianoRoll.setMinPos: setMinimalPositionForTime: t: "
                        + t + ", position: " + position);
        if ((t - offset_us) / microsPPix >= position) {
            return false;
        } else {
            // (t/position));
            if (t == 0) {
                setOffset((long) (-position * microsPPix));
                System.out.println("changed offset_us to " + offset_us);
            } else {
                long displayOffset = Math.round(offset_us / microsPPix);
                setMicrosPerPix((double) (t) / (position + displayOffset));
                offset_us = Math.round(displayOffset * microsPPix);
                System.out.println("changed microsPerPix to " + microsPPix);
                System.out.println("changed offset to " + offset_us);
            }
            return true;

        }

    }

    /**
     * 
     * @see de.uos.fmt.musitech.gui.display.HorizontalTimedDisplay#doInitialLayout()
     */
    public void doInitialLayout() {
        // XXX Auto-generated method stub

    }

    /**
     * @return
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param l
     */
    public void setStartTime(long l) {
        startTime = l;
    }

    //	----------- methods implementing interface DataChangeListener
    // -------------

    boolean dataChanged = false;

    /**
     * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
     */
    public void dataChanged(DataChangeEvent e) {
        System.out.println("****PCD: dataChanged");
        dataChanged = true;
        createGUI();
    }

    // ---------- methods implementing interface Display -------------------

    EditingProfile profile;

    Display rootDisplay;

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
     */
    public void destroy() {
        DataChangeManager.getInstance().removeListener(this);
    }

    /**
     * Updates the editor if data have been changed.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
     */
    public void focusReceived() {
        if (externalChanges())
            updateDisplay();
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#conflictingChanges()
     */
    public boolean externalChanges() {
        return dataChanged;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
     */
    public EditingProfile getEditingProfile() {
        return profile;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
     */
    public Object getEditObj() {
        return noteSequence;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    public boolean isFocused() {
        return isFocusOwner(); //TODO prüfen
    }

    /**
     * Initiliazes the Display PianoRollContainerDisplay by setting the
     * specified <code>editObject</code> as <code>noteSequence</code> (if
     * its is of type MidiNoteSequence) and the specified <code>profile</code>
     * and <code>rooteEditor</code> as this Display's <code>profile</code>
     * and <code>rootDisplay</code> resp. Registers this Display as a
     * DataChangeListener at the DataChangeManager. In setting the notesequence,
     * the GUI is built. TODO korrekt?
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Editor)
     */
    public void init(Object editObject, EditingProfile profile,
            Display rootDisplay) {
        //        if (editObject instanceof MidiNoteSequence) {
        //            setNoteSequence((MidiNoteSequence) editObject);
        determineNoteSequence(editObject);
        this.profile = profile;
        this.rootDisplay = rootDisplay;
        if (noteSequence != null)
            DataChangeManager.getInstance().interestExpandElements(this,
                    noteSequence);
        //        }
        //TODO else Fehlermeldung???
    }

    /**
     * Sets the <code>noteSequence</code> of this PianoRollContainerDisplay to
     * the <code>editObject</code> or its property specified by the
     * <code>propertyName</code> in the EditingProfile <code>profile</code>.
     * If the <code>editObject</code> or the property is a Container, it is
     * converted to a MidiNoteSequence.
     * 
     * @param editObject
     *            mObject to be displayed
     */
    private void determineNoteSequence(Object editObject) {
        if (editObject instanceof MidiNoteSequence) {
            setNoteSequence((MidiNoteSequence) editObject);
        } else {
            if (editObject instanceof Container) {
                setNoteSequence(MidiNoteSequence
                        .convert((Container) editObject));
            } else {
                if (profile != null && profile.getPropertyName() != null) {
                    ReflectionAccess ref = ReflectionAccess
                            .accessForClass(editObject.getClass());
                    if (ref.hasPropertyName(profile.getPropertyName())) {
                        Object propertyValue = ref.getProperty(editObject,
                                profile.getPropertyName());
                        if (propertyValue instanceof MidiNoteSequence) {
                            setNoteSequence((MidiNoteSequence) propertyValue);
                        } else {
                            if (propertyValue instanceof Container) {
                                setNoteSequence(MidiNoteSequence
                                        .convert((Container) propertyValue));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#updateEditor()
     */
    public void updateDisplay() {
        if (container != null) {

            calculateNoteDisplays();
        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#getRootEditor()
     */
    public Display getRootDisplay() {
        return rootDisplay;
    }

    double majorTickSpace = 20000;

    /**
     * @see de.uos.fmt.musitech.time.gui.LinearDisplay#setMicrosPerPix(double)
     */
    public void setMicrosPerPix(double microsPPix) {
        this.microsPPix = microsPPix;
        majorTickSpace = (TimeScale.getScaleValueX(1 / microsPPix, 60))
                / microsPPix;
        calculateNoteDisplays();
    }

    /**
     * @see de.uos.fmt.musitech.time.gui.LinearDisplay#setOffset(long)
     */
    public void setOffset(long micros) {
        offset_us = micros;
        calculateNoteDisplays();
    }

    public void setDisplayOffset(long pixel) {
        offset_pix = pixel;
    }

    /**
     * @see de.uos.fmt.musitech.time.gui.LinearDisplay#getEndTime()
     */
    public long getEndTime() {
        if (container != null)
            return container.getDuration();
        else
            return -1;
    }

    //####################

    /**
     * TODO add comment
     * 
     * @param segment
     * @param segCoor
     * @return
     */
    public boolean getSegmentBounds(NoteList segment, int segCoor[]) {
        // TODO should return false if notes not visible
        boolean found = false;
        long position[] = new long[4];
        segCoor[0] = Integer.MAX_VALUE;
        segCoor[1] = Integer.MAX_VALUE;
        segCoor[2] = Integer.MIN_VALUE;
        segCoor[3] = Integer.MIN_VALUE;
        for (Iterator iter = segment.iterator(); iter.hasNext();) {
            Note note = (Note) iter.next();
            if (container.containsRecursive(note)) {
                found = true;
                notePosition(note, position);
                if (position[0] < segCoor[0])
                    segCoor[0] = (int) position[0];
                if (position[1] < segCoor[1])
                    segCoor[1] = (int) position[1];
                if (position[0] + position[2] > segCoor[2])
                    segCoor[2] = (int) (position[0] + position[2]);
                if (position[1] + position[3] > segCoor[3])
                    segCoor[3] = (int) (position[1] + position[3]);
            }
        }
        return found;
    }

    /**
     * Returns the noteDisplays.
     * 
     * @return Returns the noteDisplays.
     */
    public List getNoteDisplays() {
        return noteDisplays;
    }

    /**
     * Sets the minimum display range. So notes won't be displayed in pianoroll
     * display-typ if the note is lower than the given value.
     * 
     * @param displayRangeMin
     *            the lowest displayed note
     */
    public void setDisplayRangeMin(int displayRangeMin) {
        this.displayRangeMin = displayRangeMin;
    }

    /**
     * Sets the maximum display range. So notes won't be displayed in pianoroll
     * display-typ if the note is upper than the given value.
     * 
     * @param displayRangeMin
     *            the lowest displayed note
     */
    public void setDisplayRangeMax(int displayRangeMax) {
        this.displayRangeMax = displayRangeMax;
    }

    public int getDisplayRangeMax() {
        return displayRangeMax;
    }

    public int getDisplayRangeMin() {
        return displayRangeMin;
    }

    /**
     * false: disables folowing MouseListeners: SelectionAdapter and
     * MouseEditable true: enables both
     * 
     * @return
     */
    public boolean isMouseListenerDisable() {
        return mouseListenerDisable;
    }

    /**
     * false: disables folowing MouseListeners: SelectionAdapter and
     * MouseEditable true: enables both
     * 
     * @return
     */
public void setMouseListenerDisable(boolean mouseListenerDisable) {
        this.mouseListenerDisable = mouseListenerDisable;
        if (mouseListenerDisable){
            removeMouseListener();
        }
        else {
            addMouseListener();
        }

    }
    

/**
     * 
     */
    public void addMouseListener() {
        addMouseSelectionListener();
        addMouseEditableListener();
    }

    public void addMouseEditableListener() {
        for (Iterator iter = noteDisplays.iterator(); iter.hasNext();) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) iter.next();
            MouseEditableAdapter mouseEdit = new MouseEditableAdapter(nd);
            nd.addMouseListener(mouseEdit.getMouseListener());
            nd.addMouseMotionListener(mouseEdit.getMouseMotionListern());
        }
    }

    public void removeMouseListener() {
        long time = System.currentTimeMillis();

        for (Iterator iter = noteDisplays.iterator(); iter.hasNext();) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) iter.next();
            MouseListener[] listener = nd.getMouseListeners();
            for (int i = 0; i < listener.length; i++) {
                
                if (listener[i] instanceof ToolTipManager) {
                } else {
                    nd.removeMouseListener(listener[i]);
                }

            }
            MouseMotionListener[] motionListeners = nd.getMouseMotionListeners();
            for (int i = 0; i < motionListeners.length; i++) {
                nd.removeMouseMotionListener(motionListeners[i]);
            }
            nd.setCursor(Cursor.getDefaultCursor());
        }
        System.out.println("time for removeListeners: "
                + (System.currentTimeMillis() - time));
    }

    public void removeSelectionMouseListener() {
        long time = System.currentTimeMillis();

        for (Iterator iter = noteDisplays.iterator(); iter.hasNext();) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) iter.next();
            for (int i = 0; i < nd.getMouseListeners().length; i++) {
                //                if
                // (nd.getMouseListeners()[i].getClass().equals(getSelectionController().getSelectionAdapter().getClass()))
                // {
                //                    System.out.println("SelectionAdapter gefunden!");
                nd.removeMouseListener(getSelectionController()
                        .getSelectionAdapter());
                //                }

            }
            //            for (int i = 0; i < nd.getMouseMotionListeners().length; i++) {
            //                nd.removeMouseMotionListener(nd.getMouseMotionListeners()[i]);
            //            }
            //            nd.setCursor(Cursor.getDefaultCursor());
        }
        System.out.println("time for remove SelectionListener: "
                + (System.currentTimeMillis() - time));
    }

    public void addMouseSelectionListener() {
        long time = System.currentTimeMillis();
        for (Iterator iter = noteDisplays.iterator(); iter.hasNext();) {
            PianoRollNoteDisplay nd = (PianoRollNoteDisplay) iter.next();
            nd.addMouseListener(getSelectionController().getSelectionAdapter());
        }
        //        System.out.println("time for adding SelectionListener: "
        //                + (System.currentTimeMillis() - time));
    }

    public void setEditMode(int editMode) {
        this.editMode = editMode;
    }

    public int getEditMode() {
        return editMode;
    }

    /**
     * @param display
     */
    public void removeNoteDisplay(PianoRollNoteDisplay display) {
        noteDisplays.remove(display);
        container.remove(display.getNote());
        remove(display);
        ArrayList changeList = new ArrayList();
        changeList.add(container);
        DataChangeManager.getInstance().changed(changeList,
                new DataChangeEvent(this, changeList));

    }

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	public Component asComponent() {
		return this;
	}

}