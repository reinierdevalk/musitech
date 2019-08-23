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
 * Created on 10.04.2003
 *
 */
package de.uos.fmt.musitech.structure.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.time.TimedContainer;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.selection.Selection;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionListener;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.performance.gui.PianoRollContainerDisplay;
import de.uos.fmt.musitech.time.gui.HorizontalPositioningCoordinator;
import de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * A Display for LyricsSyllableSequences.
 * 
 * @author FX
 * 
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class LyricsDisplay extends JPanel implements HorizontalTimedDisplay,
        Display, SelectionListener, Timeable {

    /**
     * The LyricsSyllableSequence to be displayed. Is the editObj of this
     * Display.
     */
    private LyricsSyllableSequence lyricsContainer;

    /**
     * Object to be displayed. Is a LyricsSyllableSequence or an object having a
     * LyricsSyllableSequence property.
     */
    private Object editObj;

    /**
     * String indicating the LyricsSyllableSequence property of the
     * <code>editObj</code> which is to be displayed by this LyricsDisplay. If
     * the <code>editObj</code> itself is the LyricsSyllableSequence, the
     * <code>propertyName</code> is null.
     */
    private String propertyName;

    /**
     * Property of the <code>editObj</code> which is named by
     * <code>propertyName</code>. The
     * <code>propertyValue</code< of the LyricsDisplay is set via ReflectionAccess
     * if the <code>propertyName</code> is not null.
     */
    private Object propertyValue;

    /**
     * The EditingProfile of this Display.
     */
    EditingProfile profile;

    /**
     * The root of this Display.
     */
    Display rootDisplay;

    /**
     * Is set true, if data is changed by an external Editor. The flag is set
     * when this Display receives a DataChangeEvent.
     */
    boolean dataChanged = false;

    /**
     * Vector containing the LyricsSyllableDisplays used for displaying the
     * LyricsSyllables in the <code>syllableContainer</code>.
     */
    List<LyricsSyllableDisplay> syllableDisplays = new ArrayList<LyricsSyllableDisplay>();

    /**
     * Final int value giving the horizontal gap between two neighbouring
     * LyricsSyllableDisplays.
     */
    private static final int H_GAP = 3;

    /**
     * Empty constructor. <br>
     * Necessary for creating a LyricsDisplay using the EditorFactory.
     *  
     */
    public LyricsDisplay() {
        setLayout(null);
    }

    /**
     * Returns the left-most possible position in pixels for the specified time.
     * At least, one of both arguments must not be null, more precisely:
     * <code>t</code> must not be Timed.INVALID_TIME or <code>m</code> must
     * not be null. If both arguments are valid, the position corresponding to
     * the greater time value is returned. If no valid position could be
     * determined, Integer.MIN_VALUE is returned.
     * 
     * @param t
     *            long giving the physical time
     * @param m
     *            Rational giving the metrical time
     * @throws WrongArgumentException
     * 
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getMinimalPositionForTime(long,de.uos.fmt.musitech.utility.math.Rational)
     */
    public int getMinimalPositionForTime(long t, Rational m)
            throws WrongArgumentException {
        if (t == Timed.INVALID_TIME && m == null)
            throw new WrongArgumentException(
                    "At least one argument must not be null");
        //take greater time of t and m
        long time = Timed.INVALID_TIME;
        if (t != Timed.INVALID_TIME)
            time = t;
        if (m != null) {
            long tMetrical = getTimeForMetrical(m);
            if (time < tMetrical)
                time = tMetrical;
        }
        //get right edge + H_GAP of last LyricsSyllableDisplay before time
        int pixel = Integer.MIN_VALUE;
//        LyricsSyllable syllable = (LyricsSyllable) lyricsContainer
//                .getLastElementBefore(time);
//        if (syllable != null) {
//            Component theDisplay = getDisplayForElement(syllable);
//            if (theDisplay != null){
////                pixel = theDisplay.getX() + theDisplay.getWidth() + H_GAP;
//                //Modif 11/11/04
//                if (time < thisNextTime){
//                    pixel = theDisplay.getX();
//                } else {
//                    pixel = theDisplay.getX() + theDisplay.getWidth() + H_GAP;
//                }
//                //end Modif
//            }
//        } else {
            //if there is no display before time, check if first display is at
            // time and get its position
            LyricsSyllable syllable = (LyricsSyllable) lyricsContainer
                    .getLastElementNotAfter(time);
            if (syllable != null) {
                Component display = getDisplayForElement(syllable);
                if (display != null)
                    pixel = display.getX();
            }
//        }
        return pixel;
    }

    /**
     * Returns true if one or more LyricsSyllableDisplays have been shifted to
     * meet the requirements of the specified minimal <code>position</code>.
     * False is returned if the LyricsDisplay remains unchanged. <br>
     * Checks the position of the LyricsSyllableDisplay used for the first
     * LyricsSyllable at or after the specified time. If the display's position
     * is left to the specified minimal position, the positions of the display
     * and its successor LyricsSyllableDisplays are changed to meet the
     * requirements of the minimal position.
     * 
     * @param t
     *            long giving the referent physical time
     * @param m
     *            Rational giving the referent metrical time
     * @param position
     *            int indicating the minimal position
     * @return boolean true if the LyricsDisplay has been changed to fit the
     *         specified minimal position for the specified time, false if the
     *         LyricsDisplay remains unchanged
     * 
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#setMinimalPositionForTime(long, Rational, int)
     */
    public boolean setMinimalPositionForTime(long t, Rational m, int position)
            throws WrongArgumentException {

        if ((t == Timed.INVALID_TIME && m == null) || position < 0)
            throw new WrongArgumentException(
                    "At least one of {t,m} must not be null, and position must be non-negative");

        long time = Timed.INVALID_TIME;
        if (t != Timed.INVALID_TIME)
            time = t;
        if (m != null) {
            long tMetrical = getTimeForMetrical(m);
            if (tMetrical > time)
                time = tMetrical;
        }
        
        LyricsSyllable ls = new LyricsSyllable(time, "[dummy]");

        int index = lyricsContainer.findFirst(ls);
        if (index < 0) {
            index = -1 * index - 1;
        }

        LyricsSyllable syllable = (lyricsContainer.get(index));

        Component theDisplay = getDisplayForElement(syllable);

        int x = theDisplay.getX();

        boolean hasChanged = false;

        int difference = position - x;
        if (difference > 0) {
            //shift all syllable displays on the right side of x by difference
            for (LyricsSyllableDisplay element : syllableDisplays) {
                if (element.getX() >= x) {
                    hasChanged = true;
                    Point location = element.getLocation();
                    location.x += difference;
                    element.setLocation(location);
                }
            }

        }
        if (hasChanged) {
            setSizeOfDisplay();
        }
        return hasChanged;

    }

    /**
     * Creates the GUI according to the preferred sizes of the
     * LyricsSyllableDisplays used to display the elements of the
     * <code>lyricsContainer</code>. The LyricsSyllableDisplays are added to
     * the <code>syllableDisplays</code>.
     * 
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#doInitialLayout()
     */
    public void doInitialLayout() {
        removeAll(); // Remove old displays first
        setBackground(Color.WHITE);
        if (!syllableDisplays.isEmpty())
            syllableDisplays.clear();

        LyricsSyllableDisplay lastDisplay = null;

        for (LyricsSyllable element : lyricsContainer) {

            int x = 0;
            if (lastDisplay != null) {
                Rectangle bounds = lastDisplay.getBounds();
                x = bounds.x + bounds.width + H_GAP;
            }

            LyricsSyllableDisplay lsd = null;
            try {
                lsd =
                //					(LyricsSyllableDisplay) EditorFactory.createDisplay(
                //						lyricsSyllable,
                //						null);
                (LyricsSyllableDisplay) EditorFactory.createDisplay(
                        element, null, "LyricsSyllable", this);
            } catch (EditorConstructionException e) {
                e.printStackTrace();
            }
            if (lsd != null) {
                add(lsd);
                Dimension size = lsd.getPreferredSize();
                lsd.setBounds(x, 1, size.width, size.height);
                syllableDisplays.add(lsd);
                lastDisplay = lsd;
            }
        }
        setSizeOfDisplay();
    }

    long thisNextTime = Timed.INVALID_TIME;
    
    /**
     * Returns the time next to the given <code>startTime</code> at which a
     * LyricsSyllableDisplay is to be positioned. The specified long must not be
     * Timed.INVALID_TIMED. <br>
     * The position of that LyricsSyllable in the <code>chordsContainer</code>
     * is determined which has or would have the specified
     * <code>startTime</code>. The starting time of the LyricsSyllable at the
     * next position "to the right" is returned. If no positioning time could be
     * determined, Timed.INVALID_TIME is returned.
     * 
     * @param startTime giving the current start time
     * @return long giving the start time of the next LyricsSyllable in
     *         <code>lyricsContainer</code> relative to the specified
     *         <code>startTime</code>, or Timed.INVALID_TIME
     * 
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getNextPositioningTime(long)
     */
    public long getNextPositioningTime(long startTime) {

        if (lyricsContainer.size() == 0)
            return Timed.INVALID_TIME;

        int pos = lyricsContainer.findFirst(new LyricsSyllable(startTime, ""));

        if (pos < 0) {
            pos = pos * (-1) - 2;	//original
//            pos = pos * (-1) - 1;	//TODO überprüfen
        }
        LyricsSyllable syl = null;
        do {
            if (pos < lyricsContainer.size() - 1)
                syl = lyricsContainer.get(++pos);
            else
                syl = null;
        } while (syl != null && syl.getTime() <= startTime);
        if (syl != null) {
//            return syl.getTime();
            thisNextTime = syl.getTime();
            return thisNextTime;
        } else {
            return Timed.INVALID_TIME;
        }
    }

    /**
     * Returns that LyricsSyllableDisplay in the <code>syllableDisplays</code>
     * which is used for displaying the specified LyricsSyllable. If the
     * specified <code>syllable</code> is null or is not the editObj of one of
     * the displays in the <code>syllableDisplays</code>, null is returned.
     * @param syllable 
     * @return 
     */
    protected LyricsSyllableDisplay getDisplayForElement(LyricsSyllable syllable) {
        if (syllable != null) {
            for (LyricsSyllableDisplay element : syllableDisplays) {
                //if (element.getEditObj() == syllable)
                if (element.getSyllable() == syllable)
                    return element;
            }
        }
        return null;
    }

    /**
     * Returns <code>dataChanged</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
     */
    public boolean externalChanges() {
        return dataChanged;
    }

    /**
     * Removes this Display from the table of the DataChangeManager and from the
     * SelectionManager.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
     */
    public void destroy() {
        DataChangeManager.getInstance().removeListener(this);
        SelectionManager.getManager().removeListener(this);
    }

    /**
     * Updates this Display if there are external data changes. This method is
     * invoked when the display gets the focus.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
     */
    public void focusReceived() {
        if (externalChanges())
            updateDisplay();
        if (rootDisplay != this)
            rootDisplay.focusReceived();
    }

    /**
     * Getter for <code>profile</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
     */
    public EditingProfile getEditingProfile() {
        return profile;
    }

    /**
     * Returns the <code>editObj</code>.<br>
     * The <code>editObj</code> may be a LyricsSyllableSequence or an object
     * having a LyricsSyllableSequence property.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
     */
    public Object getEditObj() {
        //		return lyricsContainer;
        return editObj;
    }

    /**
     * Returns true if this Display or one of its LyricsSyllableDisplays is the
     * focus owner.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    public boolean isFocused() {
        if (isFocusOwner())
            return true;
        for (LyricsSyllableDisplay element : syllableDisplays) {
            if (element.isFocusOwner())
                return true;
        }
        return false;
    }

    /**
     * Initializes this Display. Sets the specified arguments as the
     * <code>editObj</code>,<code>profile</code> and
     * <code>rootDisplay</code> of this Display. <code>propertyName</code>
     * and <code>propertyValue</code> are set if the <code>profile</code>
     * specified a property name. Registers this DataChangeListener at the
     * DataChangeManager and at the SelectionManager. Creates the GUI. <br>
     * The specified <code>editObject</code> must not be null. It is
     * recommended to created a LyricsDisplay using the EditorFactory.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Display)
     */
    public void init(Object editObject, EditingProfile profile1, Display root) {
        //		if (editObject != null && editObject instanceof
        // LyricsSyllableSequence)
        //			lyricsContainer = (LyricsSyllableSequence) editObject;
        //		else
        //			//if the Display is created using the EditorFactory, this does not
        // occur
        //			return;
        if (editObject != null)
            this.editObj = editObject;
        else {
            System.err
                    .println("In LyricsDisplay, method init(Object, EditingProfile, Display):\n"
                            + "The Object argument must not be null.");
            return;
        }
        this.profile = profile1;
        if (profile1 != null) {
            propertyName = profile1.getPropertyName();
            readPropertyValue();
        }
        this.rootDisplay = root;
        setLyricsContainer();
        if (lyricsContainer != null) {
            //register at DataChangeManager and SelectionManager
            Collection<LyricsSyllableSequence> data = new ArrayList<LyricsSyllableSequence>();
            data.add(lyricsContainer);
            DataChangeManager.getInstance().interestExpandElements(this, data);
            SelectionManager.getManager().addListener(this);
            //create GUI
            doInitialLayout();
        }
    }

    /**
     * Sets the <code>propertyValue</code> via ReflectionAccess if
     * <code>propertyName</code> is not null and the <code>editObj</code>
     * has a property of this name.
     */
    private void readPropertyValue() {
        if (propertyName != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(editObj
                    .getClass());
            if (ref.hasPropertyName(propertyName))
                propertyValue = ref.getProperty(editObj, propertyName);
        }
    }

    /**
     * Sets the <code>lyricsContainer</code> to either the
     * <code>editObj</code> or the <code>propertyValue</code>.
     */
    private void setLyricsContainer() {
        if (editObj instanceof LyricsSyllableSequence)
            lyricsContainer = (LyricsSyllableSequence) editObj;
        else if (propertyValue instanceof LyricsSyllableSequence)
            lyricsContainer = (LyricsSyllableSequence) propertyValue;
    }

    /**
     * Updates this Display to data changed by an external editor. Rebuilds the
     * GUI.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
     */
    public void updateDisplay() {
        for (LyricsSyllableDisplay element : syllableDisplays) {
            element.updateDisplay();
        }
        removeAll();
        doInitialLayout();
    }

    /**
     * Getter for <code>rootDisplay/code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getRootDisplay()
     */
    public Display getRootDisplay() {
        return rootDisplay;
    }

    /**
     * Sets the flag <code>dataChanged</code> true. This method is invoked
     * when the display gets a DataChangeEvent. <br>
     * As the LyricsDisplay is a Display (and not an Editor), data changes must
     * have been performed in an external Editor. Therefore, the source of the
     * specified DataChangeEvent needs not be checked.
     * 
     * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
     */
    public void dataChanged(DataChangeEvent e) {
        dataChanged = true;
    }

    /**
     * Creates LyricsSyllableDisplays for the LyricsSyllables in the
     * <code>lyrisContainer</code> and adds them to the
     * <code>syllablesDisplays</code>.
     */
    /*
     * private void createSyllableDisplays() { if (!syllableDisplays.isEmpty())
     * syllableDisplays.clear(); if (lyricsContainer != null &&
     * !lyricsContainer.isEmpty()) { for (Iterator iter =
     * lyricsContainer.iterator(); iter.hasNext();) { LyricsSyllable syllable =
     * (LyricsSyllable) iter.next(); LyricsSyllableDisplay syllableDisplay =
     * null; try { syllableDisplay = (LyricsSyllableDisplay)
     * EditorFactory.createDisplay( syllable, null, this); if (syllableDisplay !=
     * null) syllableDisplays.add(syllableDisplay); } catch
     * (EditorConstructionException e) { e.printStackTrace(); } catch
     * (ClassCastException cce) { cce.printStackTrace(); } } } }
     */

    /**
     * Returns the physical time for the specified metrical time. The physical
     * time is got from the MetricalTimeLine. If no corresponding physical time
     * could be determined, Timed.INVALID_TIME is returned.
     * 
     * @param metricalTime
     *            Rational giving a metrical time
     * @return long giving the physical time for the specified metrical time, or
     *         Timed.INVALID_TIME
     */
    private long getTimeForMetrical(Rational metricalTime) {
        MetricalTimeLine mtl = null;
        if (lyricsContainer.getContext() != null
                && lyricsContainer.getContext().getPiece() != null)
            mtl = lyricsContainer.getContext().getPiece().getMetricalTimeLine();
        if (mtl == null) {
            mtl = new MetricalTimeLine();
            mtl.add(lyricsContainer.get(lyricsContainer.size() - 1));
            //TODO prüfen, ob time valid?
        }
        long time = mtl.getTime(metricalTime);
        if (time >= 0)
            return time;
        return Timed.INVALID_TIME;
    }

    /**
     * Prints the horizontal positions fo the ChordSymbolDisplays (left edge).
     * This method is used for testing only.
     */
    public void printSyllablePositions() {
        System.out.println("ChordSymbSeqDisplay: ");
        int i = 1;
        for (LyricsSyllableDisplay element : syllableDisplays) {
            System.out.print("Position of " + (i++) + "th display: \t");
            System.out.println(element.getX());
        }
    }

    /**
     * Sets the preferred size of this LyricsDisplay. The width is set to the
     * right edge of the last LyricsSyllableDisplay, the height is set to the
     * height of the last LyricsSyllableDisplay.
     */
    private void setSizeOfDisplay() {
        int x = 0;
        int y = 0;
        if (!syllableDisplays.isEmpty()) {
            LyricsSyllableDisplay lastDisplay = (LyricsSyllableDisplay) syllableDisplays
                    .get(syllableDisplays.size() - 1);
            x = lastDisplay.getX() + lastDisplay.getWidth();
            //			y = lastDisplay.getHeight();
            for (Iterator iter = syllableDisplays.iterator(); iter.hasNext();) {
                LyricsSyllableDisplay element = (LyricsSyllableDisplay) iter
                        .next();
                if (element.getHeight() > y)
                    y = element.getHeight();
            }
        }
        setPreferredSize(new Dimension(x, y));
    }

    // ----------- for testing ----------------------------------------

    //TODO folgende Methoden später löschen (z.Zt. für Testzwecke)

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lyrics Display Test");
        frame.getContentPane().setBackground(Color.white);

        populateTestFrame(frame);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            };
        });
        frame.setSize(800, 430);

        frame.setVisible(true);
    }

    /**
     * For testing (used by main())
     */
    private static void populateTestFrame(JFrame frame) {

        PianoRollContainerDisplay pRCDisplay = createTestPianoRoll();

        //		TimedContainer testLyrics = TimedContainer.generateTestLyrics1();
        LyricsSyllableSequence testLyrics = TimedContainer
                .generateTestLyrics1();
        //		LyricsDisplay ld = new LyricsDisplay(testLyrics);
        LyricsDisplay ld = null;
        try {
            ld = (LyricsDisplay) EditorFactory.createDisplay(testLyrics, null);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        ld.setBorder(new LineBorder(Color.red));

        //		TimedContainer testLyrics2 = TimedContainer.generateTestLyrics2();
        //		LyricsDisplay ld2 = new LyricsDisplay(testLyrics2);
        LyricsSyllableSequence testLyrics2 = TimedContainer
                .generateTestLyrics2();
        LyricsDisplay ld2 = null;
        try {
            ld2 = (LyricsDisplay) EditorFactory
                    .createDisplay(testLyrics2, null);
        } catch (EditorConstructionException e2) {
            e2.printStackTrace();
        }
        ld2.setBorder(new LineBorder(Color.blue));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(pRCDisplay);
        panel.add(ld);
        panel.add(ld2);

        JScrollPane scrollPane = new JScrollPane(panel);
        frame.getContentPane().add(scrollPane);

        // LayoutCoordinator's job starts here...

        HorizontalPositioningCoordinator hpc = new HorizontalPositioningCoordinator();
        //hpc.addDisplay(pRCDisplay);
        hpc.registerDisplay(ld);
        hpc.registerDisplay(ld2);

        hpc.doPositioning();
    }

    /**
     * For testing (used by main())
     * 
     * @return a living PianoRollContainerDisplay
     */
    private static PianoRollContainerDisplay createTestPianoRoll() {
        final PianoRollContainerDisplay pRCDisplay = new PianoRollContainerDisplay();
        MidiNoteSequence ns = new MidiNoteSequence();

        ns.addNote(new MidiNote(0, 50, 30, 51));
        ns.addNote(new MidiNote(62, 50, 40, 60));
        ns.addNote(new MidiNote(182, 280, 60, 70));
        ns.addNote(new MidiNote(1000, 380, 10, 53));
        pRCDisplay.setNoteSequence(ns);
        pRCDisplay.setAutoHScale(false);
        pRCDisplay.setDisplayType(PianoRollContainerDisplay.PIANOROLL);
        return pRCDisplay;
    }

    /**
     * Marks any LyricsSyllable in the <code>lyricsContainer</code> which has
     * been selected, i.e. which is registered in the <code>selection</code>
     * of the SelectionChangeManager. <br>
     * This method is invoked when this SelectionListener gets a
     * SelectionChangeEvent.
     * 
     * @see de.uos.fmt.musitech.framework.selection.SelectionListener#selectionChanged(de.uos.fmt.musitech.framework.selection.SelectionChangeEvent)
     */
    public void selectionChanged(SelectionChangeEvent e) {
        Selection selection = SelectionManager.getManager().getSelection();
        for (Iterator iter = syllableDisplays.iterator(); iter.hasNext();) {
            LyricsSyllableDisplay syllableDisplay = (LyricsSyllableDisplay) iter
                    .next();
            if (selection.isSelected(syllableDisplay.getSyllable())) {
                syllableDisplay.markSelected(true);
            } else
                syllableDisplay.markSelected(false); //to reset selections
        }
    }

    // ----- implementing Timeable + additional methods --------

    /**
     * Physical time position. (Is set by the PlayTimer.)
     */
    private long currentTime = 0;

    /**
     * Horizontal coordinate corresponding to the <code>currentTime</code>.
     * Position where the cursor will be painted.
     */
    private int cursorX = 0;

    /**
     * If <code>cursorOption</code> is true, the playback is accompanied by a
     * cursor. If <code>cursorOption</code> is false, the syllable which is
     * currently played back will be highlighted.
     */
    private boolean cursorOption = false;

    /**
     * Setter for <code>cursorOption</code>. If <code>cursorOption</code>
     * is true, the playback is accompanied by a cursor. If
     * <code>cursorOption</code> is false, the syllable which is currently
     * played back will be highlighted.
     * 
     * @param cursorOption
     *            boolean if true a cursor will be moving, if false the current
     *            syllable will be highlighted
     */
    public void setCursorOption(boolean cursorOption) {
        this.cursorOption = cursorOption;
    }

    /**
     * Getter for <code>cursorOption</code>. If <code>cursorOption</code>
     * is true, the playback is accompanied by a cursor. If
     * <code>cursorOption</code> is false, the syllable which is currently
     * played back will be highlighted.
     * 
     * @return the <code>cursorOption</code>
     */
    public boolean getCursorOption() {
        return cursorOption;
    }

    /**
     * Sets <code>currentTime</code> to the specified <code>timeMicros</code>
     * and starts a Thread for highlighting that LyricsSyllableDisplay whose
     * LyricsSyllable is currently played back (if <code>cursorOption<code> is
     * false, which is the default value) or for moving a cursor along the display
     * (if <code>cursorOption</code> is true).
     * 
     * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
     */
    public void setTimePosition(long timeMicros) {
        currentTime = timeMicros;
        Thread thread = new Thread() {
            public void run() {
                if (cursorOption) {
                    moveCursor();
                } else {
                    highlightSyllable();
                }
            }
        };
        //        thread.start();
        SwingUtilities.invokeLater(thread);
    }

    /**
     * Returns the end time of the last element of the
     * <code>lyricsContainer</code>, i.e. the time + duration of the last
     * syllable. If the <code>lyricsContainer</code> is null or empty, or if
     * the last syllable cannot determine a physical time, 0 is returned.
     * 
     * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
     */
    public long getEndTime() {
        if (lyricsContainer != null && lyricsContainer.size() > 0) {
            LyricsSyllable lastSyllable = (LyricsSyllable) lyricsContainer
                    .get(lyricsContainer.size() - 1);
            if (lastSyllable.getTime() != Timed.INVALID_TIME) {
                return lastSyllable.getTime() + lastSyllable.getDuration();
            }
        }
        return 0;
    }

    /**
     * Highlights that LyricsSyllableDisplay whose LyricsSyllable covers the
     * <code>currentTime</code> which is set by the PlayTimer.
     */
    private void highlightSyllable() {
        //get current syllable
        LyricsSyllable currentSyllable = (LyricsSyllable) lyricsContainer
                .getLastElementNotAfter(currentTime);
        //get corresponding LyricsSyllableDisplay
        LyricsSyllableDisplay currentDisplay = getDisplayForElement(currentSyllable);
        //mark current display
        if (currentDisplay != null) {
            currentDisplay.markCurrentlyPlayed(true);
            //unmark all other displays
            for (Iterator iter = syllableDisplays.iterator(); iter.hasNext();) {
                LyricsSyllableDisplay display = (LyricsSyllableDisplay) iter
                        .next();
                if (display != currentDisplay) {
                    display.markCurrentlyPlayed(false);
                }
            }
        }
    }

    /**
     * Moves a cursor along this display. The cursor's position
     * <code>cursorX</code> is interpolated between adjacent syllables
     * according to the <code>currentTime</code>.
     */
    private void moveCursor() {
        int xWidth = 0;
        long timeWidth = 0;
        //get current syllable
        LyricsSyllable currentSyllable = (LyricsSyllable) lyricsContainer
                .getLastElementNotAfter(currentTime);
        //determine time and pixel range between currentSyllable and
        //next syllabe (or end of currentSyllable)
        int[] pixels = determinePixelWidth(currentSyllable);
        xWidth = pixels[1] - pixels[0];
        long[] timePoints = determineTimeWidth(currentSyllable);
        timeWidth = timePoints[1] - timePoints[0];
        //determine cursor position
        int oldCursor = cursorX;
        if (timeWidth > 0 && currentTime >= timePoints[0]) {
            cursorX = (int) ((currentTime - timePoints[0]) * xWidth / timeWidth + pixels[0]);
        }
        if (timeWidth == 0) {
            cursorX += H_GAP; //position immediately before last syllable (if
                              // the syllabel does not have a duration)
        }
        //paint cursor
        if (cursorX >= getX() && cursorX <= getX() + getWidth()) {
            if (oldCursor <= cursorX) {
                repaint(oldCursor, getY(), cursorX - oldCursor + 1,
                        getHeight() + 1);
            } else {
                repaint(cursorX, getY(), oldCursor - cursorX + 1,
                        getHeight() + 1);
            }
        }
    }

    /**
     * Returns a Array containing two long values; the first element of the
     * Array gives the physical begin time of the specified
     * <code>currentSyllable</code>, the second element gives the begin time
     * of the following syllable or the end time of the
     * <code>currentSyllable</code> if there is no next syllable. If the
     * determined end time is less than the begin time, an Array with two 0
     * values is returned.
     * 
     * @param currentSyllable
     *            LyricsSyllable whose begin and end time (mostly given by time
     *            of the next syllable) are returned
     * @return long[] Array containing the physical begin and end times of the
     *         specified LyricsSyllable (the end time being the begin of the
     *         next syllable if existing)
     */
    private long[] determineTimeWidth(LyricsSyllable currentSyllable) {
        long beginTime = 0;
        long endTime = 0;
        if (currentSyllable != null) {
            beginTime = currentSyllable.getTime();
            int i = lyricsContainer.indexOf(currentSyllable);
            if (lyricsContainer.size() > i + 1
                    && lyricsContainer.get(i + 1) != null) {
                endTime = ((LyricsSyllable) lyricsContainer.get(i + 1))
                        .getTime();
            } else {
                endTime = beginTime + currentSyllable.getDuration();
                if (endTime == beginTime) {
                    //TODO if the cursor shall move beyond the last syllable
                    //at the moment it stops immediately before the last
                    // syllable
                }
            }
        }
        if (endTime >= beginTime)
            return new long[] { beginTime, endTime };
        else
            return new long[] { 0, 0 };
    }

    /**
     * Returns an Array containing two int values; the first element of the
     * Array gives the x coordinate of the LyricsSyllableDisplay which is
     * showing the specified <code>currentSyllable</code>, the second element
     * is the x coordinate of the following LyricsSyllableDisplay if there is
     * one. Otherwise the second element is the x coordinate of the right margin
     * of the current display. If the determined second value is less than the
     * first, an Array with two 0 values is returned.
     * 
     * @param currentSyllable
     *            LyricsSyllable whose x coordinates are returned (more
     *            precisely: the coordinates of the corresponding
     *            LyricsSyllableDisplay(s))
     * @return int[] Array containing the x position of the LyricsSyllable
     *         showing the specified LyricsSyllable and that of the next display
     *         or of the current display's right margin
     */
    private int[] determinePixelWidth(LyricsSyllable currentSyllable) {
        int beginX = 0;
        int endX = 0;
        if (currentSyllable != null) {
            LyricsSyllableDisplay currentDisplay = getDisplayForElement(currentSyllable);
            if (currentDisplay != null) {
                beginX = currentDisplay.getX();
                int index = syllableDisplays.indexOf(currentDisplay);
                if (syllableDisplays.size() > index + 1
                        && syllableDisplays.get(index + 1) != null) {
                    endX = ((LyricsSyllableDisplay) syllableDisplays
                            .get(index + 1)).getX();
                } else {
                    endX = beginX + currentDisplay.getWidth();
                }
            }
        }
        if (endX >= beginX)
            return new int[] { beginX, endX };
        else
            return new int[] { 0, 0 };
    }

    /**
     * Overwrites method <code>piant(Graphics)</code> of JComponent in order
     * to add the cursor.
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        super.paint(g);
        Color color = g.getColor();
        g.setColor(Color.RED);
        g.drawLine(cursorX, getY(), cursorX, getY() + getHeight());
        g.setColor(color);
    }

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	public Component asComponent() {
		return null;
	}

}