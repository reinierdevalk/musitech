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
package de.uos.fmt.musitech.structure.harmony.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.uos.fmt.musitech.data.structure.harmony.ChordDegreeSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordFunctionSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbolSequence;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.data.time.Timed;
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
import de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * This class provides a display for ChordSymbolSequences.
 * 
 * @author Kerstin Neubarth
 */
public class ChordSymbSeqDisplay extends JPanel implements Display,
        HorizontalTimedDisplay, SelectionListener, Timeable {

    /**
     * ChordSymbolSequence containing the sequence of ChordSymbols to be
     * displayed.
     */
    ChordSymbolSequence chordsContainer = null;

    /**
     * Vector containing the ChordSymbolDisplays used for displaying the
     * ChordSymbols of the <code>chordsContainer</code>.
     */
    List<ChordSymbolDisplay2> chordSymbolDisplays = new ArrayList<ChordSymbolDisplay2>();

    /**
     * Final int value giving the horizontal gap between two neighbouring
     * ChordSymbolDisplays.
     */
    private static final int H_GAP = 3;

    private Object editObj;

    private String propertyName;

    private Object propertyValue;

    /**
     * EditingProfile of this Display.
     */
    private EditingProfile profile;

    /**
     * Root display of this Display.
     */
    private Display root;

    /**
     * Flag of this DataChangeManager.
     */
    private boolean dataChanged = false;

    // ------------------------- constructors and setters
    // ---------------------------

    /**
     * Constructor. <br>
     * It is recommended to create a ChordSymbSeqDisplay using the
     * EditorFactory.
     */
    public ChordSymbSeqDisplay() {
        setLayout(null);
    }

    // --------------- methods implementing the HorizontalTimedDisplay interface
    // ---------

    /**
     * Returns an int value indicating the left-most position in pixels possible
     * for the specified time. <br>
     * If both arguments are given (i.e. <code>t</code> is not
     * Timed.INVALID_TIME and <code>m</code> is not null), the greater
     * position value is returned. If no valid position could be determined,
     * Integer.MIN_VALUE is returned.
     * 
     * @param t
     *            long giving the physical time
     * @param m
     *            Rational giving the metrical time
     * @return int giving the minimal position in pixels
     * @throws WrongArgumentException
     * 
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getMinimalPositionForTime(long,
     *      de.uos.fmt.musitech.utility.math.Rational)
     */
    @Override
	public int getMinimalPositionForTime(long t, Rational m)
            throws WrongArgumentException {
        if (t == Timed.INVALID_TIME && m == null)
            throw new WrongArgumentException(
                    "At least one argument of method getMinimalPositionForTime() in ChordSymbSeqDisplay must not be null.");
        //get last ChordSymbolDisplay "before" the specified time
        //		ChordSymbolDisplay symbolDisplay = getLastDisplayBeforeTime(t, m);
//        ChordSymbolDisplay2 symbolDisplay = getLastDisplayBeforeTime(t, m);
//        int minX = Integer.MIN_VALUE;
//        if (symbolDisplay != null)
//            //get position next to symbolDisplay
//            minX = symbolDisplay.getX() + symbolDisplay.getWidth() + H_GAP;
//        else {
//            //get ChordSymbolDisplay "at" the specified time (which then is the
//            // first of
//            // the symbol displays)
//            symbolDisplay = getDisplayAtTime(t, m);
//            if (symbolDisplay != null)
//                minX = symbolDisplay.getX();
//        }
        //take greater time of t and m
        long time = Timed.INVALID_TIME;
        if (t != Timed.INVALID_TIME)
            time = t;
        if (m != null) {
            long tMetrical = getTimeForMetrical(m);
            if (time < tMetrical)
                time = tMetrical;
        }
        int minX = Integer.MIN_VALUE;
        ChordSymbol syllable = (ChordSymbol) chordsContainer
        .getLastElementNotAfter(time);
        if (syllable != null) {
            Component display = getDisplayForElement(syllable);
            if (display != null)
                minX = display.getX();
        }
        return minX;
    }
    
    private long getTimeForMetrical(Rational metricalTime) {
        MetricalTimeLine mtl = null;
        if (chordsContainer.getContext() != null
                && chordsContainer.getContext().getPiece() != null)
            mtl = chordsContainer.getContext().getPiece().getMetricalTimeLine();
        if (mtl == null) {
            mtl = new MetricalTimeLine();
            mtl.add(chordsContainer.get(chordsContainer.size() - 1));
            //TODO prüfen, ob time valid?
        }
        long time = mtl.getTime(metricalTime);
        if (time >= 0)
            return time;
        return Timed.INVALID_TIME;
    }

    /**
     * Returns true if one or more ChordSymbolDisplays have been shifted to meet
     * the requirements of the specified minimal <code>position</code>. False
     * is returned if the ChordSymbSeqDisplay remains unchanged. <br>
     * Checks the position of the ChordSymbolDisplay used for the first
     * ChordSymbol at or after the specified time. If the display's position is
     * left to the specified minimal position, the positions of the display and
     * its successor ChordSymbolDisplays are changed to meet the requirements of
     * the minimal position.
     * 
     * @param t
     *            long giving the referent physical time
     * @param m
     *            Rational giving the referent metrical time
     * @param position
     *            int indicating the minimal position
     * @return boolean true if the ChordSymbSeqDisplay has been changed to fit
     *         the specified minimal position for the specified time, false if
     *         it remains unchanged
     * 
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#setMinimalPositionForTime(long,
     *      de.uos.fmt.musitech.utility.math.Rational, int)
     */
    @Override
	public boolean setMinimalPositionForTime(long t, Rational m, int position)
            throws WrongArgumentException {
        if ((t == Timed.INVALID_TIME && m == null) || position < 0)
            throw new WrongArgumentException(
                    "At least one of {t,m} must not be null, and position must be non-negative");
        //get first ChordSymbolDisplay "at" or "after" the specified time
        //		ChordSymbolDisplay display = getFirstChordSymbolDisplay(t, m);
        ChordSymbolDisplay2 display = getFirstChordSymbolDisplay(t, m);
        //get position of display
        int x = Integer.MAX_VALUE;
        if (display != null)
            x = display.getX();
        //if x<position, shift display and its successors
        int difference = position - x;
        if (difference > 0) {
            int i = 0;
            i = chordSymbolDisplays.indexOf(display);
            for (int j = i; j < chordSymbolDisplays.size(); j++) {
                //				ChordSymbolDisplay symbolDisplay = (ChordSymbolDisplay)
                // chordSymbolDisplays.get(j);
                ChordSymbolDisplay2 symbolDisplay = chordSymbolDisplays.get(j);
                symbolDisplay.setLocation(symbolDisplay.getLocation().x
                        + difference, symbolDisplay.getLocation().y);
                //for testing:
                //Point location = symbolDisplay.getLocation();
                //int n = 0;
            }
            setSizeOfDisplay();
            return true;
        }
        return false;
    }

    /**
     * Returns the time next to the given <code>startTime</code> at which a
     * ChordSymbolDisplay is to be positioned. <br>
     * The position in the <code>chordsContainer</code> corresponding to the
     * specified <code>startTime</code> is determined and the starting time of
     * the ChordSymbol at the next position "to the right" is returned.
     * 
     * @param long
     *            giving the current start time
     * @return long giving the start time of the next ChordSymbol in
     *         <code>lyricsContainer</code> relative to the specified
     *         <code>startTime</code>
     * 
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getNextPositioningTime(long)
     */
    @Override
	public long getNextPositioningTime(long startTime) {
        if (chordsContainer.size() == 0)
            //			return Integer.MAX_VALUE;
            return Timed.INVALID_TIME;
        //get position corresponding to the given startTime
        int pos = chordsContainer.findFirst(new ChordSymbol(null, startTime));
        if (pos < 0)
            pos = pos * (-1) - 2;
        //get ChordSymbol at next position
        ChordSymbol nextSymbol = null;
        do {
            if (pos < chordsContainer.size() - 1)
                nextSymbol = chordsContainer.get(++pos);
            else
                nextSymbol = null;
        } while (nextSymbol != null && nextSymbol.getTime() <= startTime);
        //		nextSymbol = (ChordSymbol) chordsContainer.get(pos);
        //return time of nextSymbol
        if (nextSymbol != null)
            return nextSymbol.getTime(); //TODO evtl. metricalTime umrechnen
        //		return Integer.MAX_VALUE;
        return Timed.INVALID_TIME;
    }

    /**
     * Creates ChordSymbolDisplays for the elements of the
     * <code>chordsContainer</code> and adds them from left to right according
     * to their preferred sizes.
     * 
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#doInitialLayout()
     */
    @Override
	public void doInitialLayout() {
        //setBackground(Color.WHITE);
    	setOpaque(false);
        //horizontal position for first ChordSymbolDisplay
        int x = 0;
        //precedent ChordSymbolDisplay
        //		ChordSymbolDisplay precedent = null;
        ChordSymbolDisplay2 precedent = null;
        if (chordSymbolDisplays == null || chordSymbolDisplays.isEmpty())
            createChordSymbolDisplays();
        //for all ChordSymbolDisplays:
        for (ChordSymbolDisplay2 chordSymbolDisplay2 : chordSymbolDisplays) {
            //			ChordSymbolDisplay symbolDisplay = (ChordSymbolDisplay)
            // iter.next();
            ChordSymbolDisplay2 symbolDisplay = chordSymbolDisplay2;
            //set bounds of symbol display and add display
            if (precedent != null) {
                x = precedent.getBounds().x + precedent.getBounds().width
                        + H_GAP;
            }
            Dimension size = symbolDisplay.getPreferredSize();
            symbolDisplay.setBounds(x, 1, size.width, size.height);
            add(symbolDisplay);
            //set precedent to current ChordSymbolDisplay
            precedent = symbolDisplay;
        }
        setSizeOfDisplay();
    }

    // ----------- helper methods for the HorizontalTimedDisplay
    // -------------------------

    /**
     * Returns the ChordSymbolDisplay of <code>chordSymbolDisplays</code>
     * which is used for diplaying the specified ChordSymbol.
     * 
     * @param symbol
     *            ChordSymbol whose ChordSymbolDisplay is to be returned
     * @return ChordSymbolDisplay displaying the specified ChordSymbol
     */
    //	private ChordSymbolDisplay getChordSymbolDisplay(ChordSymbol symbol) {
    private ChordSymbolDisplay2 getChordSymbolDisplay(ChordSymbol symbol) {
        //		ChordSymbolDisplay display = null;
        ChordSymbolDisplay2 display = null;
        for (Object element : chordSymbolDisplays) {
            //			ChordSymbolDisplay symbolDisplay = (ChordSymbolDisplay)
            // iter.next();
            ChordSymbolDisplay2 symbolDisplay = (ChordSymbolDisplay2) element;
            //			ChordSymbol chordSymbol = (ChordSymbol)
            // symbolDisplay.getEditObj();
            ChordSymbol chordSymbol = symbolDisplay.getChordSymbol();
            if (chordSymbol == symbol) {
                display = symbolDisplay;
                break;
            }
        }
        return display;
    }

    /**
     * Returns the ChordSymbolDisplay which is used for displaying the first
     * ChordSymbol at or after the specified time. <br>
     * At least one of both arguments must not be null.
     * 
     * @param t
     *            long indiciating the physical time
     * @param m
     *            Rational indicating the metrical time
     * @return ChordSymbolDisplay displaying the first ChordSymbol at or after
     *         the specified time
     */
    //	private ChordSymbolDisplay getFirstChordSymbolDisplay(long t, Rational m)
    // {
    private ChordSymbolDisplay2 getFirstChordSymbolDisplay(long t, Rational m) {
        //get ChordSymbol at t or m
        ChordSymbol symbol = null;
        if (t != Timed.INVALID_TIME) {
            ChordSymbol symbolT = chordsContainer.getFirstSymbolAtTime(t);
            if (symbolT != null)
                symbol = symbolT;
        }
        if (m != null) {
            ChordSymbol symbolM = chordsContainer.getFirstSymbolAtTime(m);
            if (symbolM != null)
                if (symbol == null || symbolM.getTime() > symbol.getTime())
                    symbol = symbolM;
        }
        //get ChordSymbolDisplay of symbol
        //		ChordSymbolDisplay display = null;
        ChordSymbolDisplay2 display = null;
        if (symbol != null)
            display = getChordSymbolDisplay(symbol);
        return display;
    }

    /**
     * Returns the ChordSymbolDisplay which is used for displaying the last
     * ChordSymbol before the specified time. <br>
     * At least one of both arguments must not be null.
     * 
     * @param t
     *            long indiciating the physical time
     * @param m
     *            Rational indicating the metrical time
     * @return ChordSymbolDisplay displaying the last ChordSymbol at or before
     *         the specified time
     */
    //	private ChordSymbolDisplay getLastDisplayBeforeTime(long t, Rational m) {
    private ChordSymbolDisplay2 getLastDisplayBeforeTime(long t, Rational m) {
        //get ChordSymbol at t or m
        ChordSymbol symbol = null;
        if (t != Timed.INVALID_TIME) {
            ChordSymbol symbolT = chordsContainer.getLastSymbolBeforeTime(t);
            if (symbolT != null)
                symbol = symbolT;
        }
        if (m != null) {
            ChordSymbol symbolM = chordsContainer.getLastSymbolBeforeTime(m);
            if (symbolM != null)
                if (symbol == null || symbolM.getTime() > symbol.getTime())
                    symbol = symbolM;
        }
        //get ChordSymbolDisplay of symbol
        //		ChordSymbolDisplay display = null;
        ChordSymbolDisplay2 display = null;
        if (symbol != null)
            display = getChordSymbolDisplay(symbol);
        return display;
    }

    /**
     * Returns a ChordSymbolDisplay which is displaying a ChordSymbol having the
     * specified time. If there are several ChordSymbolDisplays for the
     * specified time, the first of these is returned. If there is no
     * ChordSymbolDisplay at the specified time, null is returned. <br>
     * At least one of both arguments must be valid, i.e. <code>t</code> must
     * not be Timed.INVALID_TIME or <code>m</code> must not be null.
     * 
     * @param t
     *            long giving a physical time
     * @param m
     *            Rational giving a metric time
     * @return ChordSymbolDisplay whose editObj has the specified time
     */
    //	private ChordSymbolDisplay getDisplayAtTime(long t, Rational m){
    private ChordSymbolDisplay2 getDisplayAtTime(long t, Rational m) {
        //		ChordSymbolDisplay display = getFirstChordSymbolDisplay(t, m);
        ChordSymbolDisplay2 display = getFirstChordSymbolDisplay(t, m);
        if (display == null)
            return null;
        ChordSymbol editObj1 = (ChordSymbol) display.getEditObj();
        if (editObj1.getTime() != Timed.INVALID_TIME)
            if (editObj1.getTime() == t)
                return display;
            else if (editObj1.getMetricTime() != null)
                if (editObj1.getMetricTime().isEqual(m))
                    return display;
        return null;
    }

    //	-------------- methods implementing the Display interface
    // ----------------

    /**
     * Getter for the flag <code>dataChanged</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
     */
    @Override
	public boolean externalChanges() {
        return dataChanged;
    }

    /**
     * Removes this DataChangeListener from the DataChangeManager adn from the
     * SelectionManager.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
     */
    @Override
	public void destroy() {
        DataChangeManager.getInstance().removeListener(this);
        SelectionManager.getManager().removeListener(this);
    }

    /**
     * Updates the display if there are external changes.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
     */
    @Override
	public void focusReceived() {
        if (externalChanges())
            updateDisplay();
        if (root != this)
            root.focusReceived();
    }

    /**
     * Getter for <code>profile</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
     */
    @Override
	public EditingProfile getEditingProfile() {
        return profile;
    }

    /**
     * Returns the <code>editObj</code> of this ChordSymbSeqDisplay. The
     * <code>editObj</code> may be a ChordSymbolSequence or an object having a
     * ChordSymbolSequence property.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
     */
    @Override
	public Object getEditObj() {
        //		return chordsContainer;
        return editObj;
    }

    /**
     * Returns true if this Display or one of its ChordSymbolDisplays is the
     * focus owner.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    @Override
	public boolean isFocused() {
        boolean focused = false;
        if (this.isFocusOwner())
            focused = true;
        if (!focused) {
            for (ChordSymbolDisplay2 chordSymbolDisplay2 : chordSymbolDisplays) {
                JComponent element = chordSymbolDisplay2;
                if (element.isFocusOwner())
                    focused = true;
            }
        }
        return focused;
    }

    /**
     * Sets the specified arguments as <code>editObj</code>,
     * <code>profile</code> and <code>root</code> of this Display. Sets
     * <code>propertyName</code> and <code>propertyValue</code>, if the
     * <code>profile</code> specified a property name. Also sets the
     * <code>chordsContainer</code> used in this class to either the
     * <code>editObj</code> or the <code>propertyValue</code>, depending on
     * which one is the ChordSymbolSequence to be displayed. Registers this
     * Display as DataChangeListener at the DataChangeManager and at the
     * SelectionManager. Creates the initial layout. <br>
     * The specified <code>editObject</code> must not be null and must be of
     * type ChordSymbolSequence. It is recommended to create a
     * ChordSymbSeqDisplay using the EditorFactory.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Display)
     */
    @Override
	public void init(Object editObject, EditingProfile profile, Display root) {
        if (editObject != null)
            this.editObj = editObject;
        else {
            //		if (editObject instanceof ChordSymbolSequence)
            //			chordsContainer = (ChordSymbolSequence) editObject;
            //		if (chordsContainer == null){ //if the Display is created using
            // the
            // EditorFactory, this case does not occur
            System.err
                    .println("In ChordSymbolSeqDisplay, method init(Object, EditingProfile, Display):\n"
                            + "the argument Object must not be null and must be a ChordSymbolSequence.");
            return;
        }
        this.profile = profile;
        if (profile != null) {
            this.propertyName = profile.getPropertyName();
            setPropertyValue();
        }
        this.root = root;
        setChordsContainer();
        Collection<ChordSymbolSequence> objs = new ArrayList<ChordSymbolSequence>();
        objs.add(chordsContainer);
        DataChangeManager.getInstance().interestExpandElements(this, objs);
        SelectionManager.getManager().addListener(this);
        doInitialLayout();
    }

    /**
     * Sets the <code>propertyValue</code> via ReflectionAccess to that
     * property of <code>editObj</code> which is indicated by
     * <code>propertyName</code>.
     */
    private void setPropertyValue() {
        if (propertyName != null) {
            ReflectionAccess ref = ReflectionAccess.accessForClass(editObj
                    .getClass());
            if (ref.hasPropertyName(propertyName))
                propertyValue = ref.getProperty(editObj, propertyName);
        }
    }

    /**
     * Sets the <code>chordsContainer</code> to either the
     * <code>editObj</code> or the <code>propertyValue</code>.<br>
     * The <code>chordsContainer</code> allows to access the displayed
     * ChordSymbolSequence easily, without checking <code>propertName</code>,
     * <code>propertyValue</code> resp. <code>editObj</code> each time you
     * want to have the ChordSymbolSequence.
     */
    private void setChordsContainer() {
        if (editObj instanceof ChordSymbolSequence)
            chordsContainer = (ChordSymbolSequence) editObj;
        else if (propertyValue instanceof ChordSymbolSequence)
            chordsContainer = (ChordSymbolSequence) propertyValue;
    }

    /**
     * Updates the Display to external changes of the
     * <code>chordsContainer</code> by redoing the layout.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
     */
    @Override
	public void updateDisplay() {
        doInitialLayout();
        revalidate();
        repaint();
    }

    /**
     * Getter for <code>root</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getRootDisplay()
     */
    @Override
	public Display getRootDisplay() {
        return root;
    }

    /**
     * Sets the flag <code>dataChanged</code> true. This method is called when
     * this DataChangeListener gets a DataChangeEvent. <br>
     * As this Display is no Editor, data changes must have been performed
     * externally.
     * 
     * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
     */
    @Override
	public void dataChanged(DataChangeEvent e) {
        dataChanged = true;
    }

    /**
     * Creates a ChordSymbolsDisplay for each ChordSymbol in the
     * <code>chordsContainer</code> and adds it to the
     * <code>chordSymbolDisplays</code>.
     */
    private void createChordSymbolDisplays() {
        for (Iterator<ChordSymbol> iter = chordsContainer.iterator(); iter.hasNext();) {
            ChordSymbol chordSymbol = iter.next();
            //			ChordSymbolDisplay symbolDisplay = new
            // ChordSymbolDisplay(chordSymbol);
            //			ChordSymbolDisplay symbolDisplay = null;
            ChordSymbolDisplay2 symbolDisplay = null;
            try {
                //				symbolDisplay =
                // (ChordSymbolDisplay)EditorFactory.createDisplay(chordSymbol,
                // null);
                //				symbolDisplay =
                // (ChordSymbolDisplay)EditorFactory.createDisplay(chordSymbol,
                // null,
                // this);
                //				symbolDisplay =
                // (ChordSymbolDisplay2)EditorFactory.createDisplay(chordSymbol,
                // null,
                // null, this);

                if (chordSymbol instanceof ChordDegreeSymbol) {
                    symbolDisplay = (ChordSymbolDisplay2) EditorFactory
                            .createDisplay(chordSymbol, null, "ChordDegree",
                                    this);
                }
                if (chordSymbol instanceof ChordFunctionSymbol) {
                    symbolDisplay = (ChordSymbolDisplay2) EditorFactory
                            .createDisplay(chordSymbol, null, "ChordFunction",
                                    this);
                }
                if (symbolDisplay == null) {
                    symbolDisplay = (ChordSymbolDisplay2) EditorFactory
                            .createDisplay(chordSymbol, null, null, this);
                }

            } catch (EditorConstructionException e) {
                e.printStackTrace();
            } catch (ClassCastException ce) {
                ce.printStackTrace();
            }
            chordSymbolDisplays.add(symbolDisplay);
        }
    }

    /**
     * Prints the horizontal positions fo the ChordSymbolDisplays (left edge).
     * This method is used for testing only.
     */
    public void printSymbolPositions() {
        System.out.println("ChordSymbSeqDisplay: ");
        int i = 1;
        for (ChordSymbolDisplay2 element2 : chordSymbolDisplays) {
            //			ChordSymbolDisplay element = (ChordSymbolDisplay) iter.next();
            ChordSymbolDisplay2 element = element2;
            System.out.print("Position of " + (i++) + "th display: \t");
            System.out.println(element.getX());
        }
    }

    /**
     * Sets the preferred size of this ChordSymbSeqDisplay. The width is set to
     * the right edge of the last ChordSymbolDisplay, the height is set to the
     * height of the last ChordSymbolDisplay.
     */
    private void setSizeOfDisplay() {
        int x = 0;
        int y = 0;
        if (!chordSymbolDisplays.isEmpty()) {
            //			ChordSymbolDisplay lastDisplay =
            // (ChordSymbolDisplay)chordSymbolDisplays.get(chordSymbolDisplays.size()-1);
            ChordSymbolDisplay2 lastDisplay = chordSymbolDisplays
                    .get(chordSymbolDisplays.size() - 1);
            x = lastDisplay.getX() + lastDisplay.getWidth();
            //			y = lastDisplay.getHeight();
            for (ChordSymbolDisplay2 element2 : chordSymbolDisplays) {
                //				ChordSymbolDisplay element = (ChordSymbolDisplay)
                // iter.next();
                ChordSymbolDisplay2 element = element2;
                if (element.getHeight() > y)
                    y = element.getHeight();
            }
        }
        setPreferredSize(new Dimension(x, y));
    }

    /**
     * Marks those ChordSymbols in the <code>chordsContainer</code> which have
     * been selected, i.e. which are registered in the <code>selection</code>
     * of the SelectionManager.
     * 
     * @see de.uos.fmt.musitech.framework.selection.SelectionListener#selectionChanged(de.uos.fmt.musitech.framework.selection.SelectionChangeEvent)
     */
    @Override
	public void selectionChanged(SelectionChangeEvent e) {
        Selection selection = SelectionManager.getManager().getSelection();
        for (Iterator iter = chordSymbolDisplays.iterator(); iter.hasNext();) {
            ChordSymbolDisplay2 symbolDisplay = (ChordSymbolDisplay2) iter
                    .next();
            if (selection.isSelected(symbolDisplay.getChordSymbol())) {
                symbolDisplay.markSelected(true);
            } else
                symbolDisplay.markSelected(false); //to reset selections
        }
    }

    // ------ implementing Timeable ---------------

    /**
     * Physical time position. (Is set by the PlayTimer.)
     */
    private long currentTime = 0;

    /**
     * Sets <code>currentTime</code> to the specified <code>timeMicros</code>
     * and starts a Thread for highlighting that ChordsSymbolDisplay whose
     * ChordSymbol is currently played back.
     * 
     * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
     */
    @Override
	public void setTimePosition(long timeMicros) {
        currentTime = timeMicros;
        Thread thread = new Thread() {
            @Override
			public void run() {
                highlightChord();
            }
        };
        //        thread.start();
        SwingUtilities.invokeLater(thread);
    }

    /**
     * Returns the end time of the last element of the
     * <code>chordsContainer</code>, i.e. the time + duration of the last
     * ChordSymbol. If the <code>chordsContainer</code> is null or empty, or
     * if the last chord cannot determine a physical time, 0 is returned.
     * 
     * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
     */
    @Override
	public long getEndTime() {
        if (chordsContainer != null && chordsContainer.size() > 0) {
            ChordSymbol lastChordSymbol = chordsContainer
                    .get(chordsContainer.size() - 1);
            if (lastChordSymbol.getTime() != Timed.INVALID_TIME) {
                return lastChordSymbol.getTime()
                        + lastChordSymbol.getDuration();
            }
        }
        return 0;
    }

    /**
     * Highlights that ChordSymbolDisplay whose ChordSymbol covers the
     * <code>currentTime</code> which is set by the PlayTimer.
     */
    private void highlightChord() {
        //get current ChordSymbol
        ChordSymbol currentChord = (ChordSymbol) chordsContainer
                .getLastElementNotAfter(currentTime);
        //get corresponding ChordSymbolDisplay
        ChordSymbolDisplay2 currentDisplay = getChordSymbolDisplay(currentChord);
        if (currentDisplay != null) {
            //mark current display
            currentDisplay.markCurrentlyPlayed(true);
            //unmark all other displays
            for (Iterator iter = chordSymbolDisplays.iterator(); iter.hasNext();) {
                ChordSymbolDisplay2 display = (ChordSymbolDisplay2) iter.next();
                if (display != currentDisplay) {
                    display.markCurrentlyPlayed(false);
                }
            }
        }
    }
    
    /**
     * Returns that LyricsSyllableDisplay in the <code>syllableDisplays</code>
     * which is used for displaying the specified LyricsSyllable. If the
     * specified <code>syllable</code> is null or is not the editObj of one of
     * the displays in the <code>syllableDisplays</code>, null is returned.
     */
    protected ChordSymbolDisplay2 getDisplayForElement(ChordSymbol chordSymbol) {
        if (chordSymbol != null) {
            for (Iterator iter = chordSymbolDisplays.iterator(); iter.hasNext();) {
                ChordSymbolDisplay2 element = (ChordSymbolDisplay2) iter
                        .next();
                if (element.getChordSymbol() == chordSymbol)
                    return element;
            }
        }
        return null;
    }

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	@Override
	public Component asComponent() {
		return this;
	}

}