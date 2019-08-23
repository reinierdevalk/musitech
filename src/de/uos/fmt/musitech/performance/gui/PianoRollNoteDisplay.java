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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.MouseEditable;
import de.uos.fmt.musitech.framework.editor.MouseEditableAdapter;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.structure.form.gui.ContainerArrangeDisplay;
import de.uos.fmt.musitech.utility.Highlightable;

/**
 * Class to display a single note in a PianoRoll display.
 * 
 * @date (01.04.00 03:44:54)
 * @author Tillman Weyde and Alexander Luedeke
 * @version 0.109
 */
public class PianoRollNoteDisplay extends javax.swing.JPanel implements
        Highlightable, Display, MouseEditable {
    private PerformanceNote note;

    private int number = -1;

    /** Display the note as pianoroll or velocity */
    static public final int PIANOROLL = 0;

    static public final int VELOCITYROLL = 1;

    protected int displayType = PIANOROLL;

    private boolean selected = false;

    public java.awt.Color defaultColor = Color.red.darker();

    protected java.awt.Color highlightColor = Color.red;

    private boolean highlighted = false;

//    int editMode = 0;

    final int NOTHING = 0;

    final int CREATE_PERFNOTE = 1;

    final int EDIT_PERFNOTE = 2;

    final int DELETE_PERFNOTE = -1;

    /**
     * Constructor
     */
    public PianoRollNoteDisplay() {
        super();
        setLayout(new GridLayout(0, 1));
        initialize();
    }

    /**
     * PianoRollNoteDisplay constructor comment. The default displayType of a
     * PianoRollNoteDisplay is PIANOROLL so the background-color is set to the
     * velocity-value.
     */
    public PianoRollNoteDisplay(PerformanceNote n) {
        super();
        setLayout(new GridLayout(0, 1));
        setNote(n);
        if (displayType == PIANOROLL)
            setDefaultColor(getColorForVelocity());
        doDefault();
        setBorder(new javax.swing.border.LineBorder(Color.darkGray));
    }

    /**
     * Sets the backgroud to defaultColor.
     * 
     * @date (06.07.00 23:26:55)
     */

    public boolean isHighlighted() {
        return highlighted;
    }

    /**
     * Sets the background to highlightColor.
     * 
     * @date (06.07.00 23:25:44)
     */
    public void setHighlight(boolean highlighted) {
        this.highlighted = highlighted;

        setBackground(highlighted ? getHighlightColor() : defaultColor);

    }

    /**
     * Returns the center of a point.
     * 
     * @date (06.07.00 23:25:44)
     */
    public Point getCenter() {
        Rectangle r = getBounds();
        Point c = new Point();
        c.x = r.x + r.width / 2;
        c.y = r.y + r.height / 2;
        return c;
    }

    /**
     * Returns a color to represent a velocity.
     * 
     * @date (01.07.01 10:17:49)
     * @author: Alexander Luedeke
     * @return java.awt.Color
     */
    public Color getColorForVelocity() {
        //Case no velocity
        if (note.getVelocity() == 0)
            return getDefaultColor();
        //else case
        return new Color(255 - note.getVelocity() * 2,
                255 - note.getVelocity() * 2, 255 - note.getVelocity() * 2);
    }

    /**
     * Returns the default color.
     * 
     * @date (06.07.00 23:22:37)
     * @return java.awt.Color
     */
    public java.awt.Color getDefaultColor() {
        return defaultColor;
    }

    /**
     * Returns the displayType.
     * 
     * @date (18.06.01 14:18:42)
     * @param newAutoHScale
     *            boolean
     */
    public int getDisplayType() {
        return displayType;
    }

    /**
     * Returns the highlightColor.
     * 
     * @date (06.07.00 23:23:35)
     * @return java.awt.Color
     */
    public java.awt.Color getHighlightColor() {
        return highlightColor;
    }

    /**
     * Returns the note.
     * 
     * @date (01.04.00 04:16:08)
     * @return RhythmData.Note
     */
    public PerformanceNote getNote() {
        return note;
    }

    /**
     * Returns the number.
     * 
     * @date (18.04.00 00:58:34)
     * @return int
     */
    public int getNumber() {
        return number;
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        setName("PianoRollNoteDisplay");
        setLayout(null);
        setBackground(java.awt.Color.white);
        setSize(160, 120);
    }

    /**
     * Returns true if it is selected.
     * 
     * @date (04.06.00 13:49:51)
     * @return boolean
     */
    public boolean isSelected() {
        return SelectionManager.getManager().getSelection().isSelected(note);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args
     *            java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            PianoRollNoteDisplay aNoteDisplay;
            aNoteDisplay = new PianoRollNoteDisplay();
            frame.setContentPane(aNoteDisplay);
            frame.setSize(aNoteDisplay.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err
                    .println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Paint method. Empty.
     * 
     * @param g
     *            java.awt.Graphics
     */
    public void paint(java.awt.Graphics g) {
        super.paint(g);
    }

    /**
     * Sets the defaultColor.
     * 
     * @date (06.07.00 23:22:37)
     * @param newDefaultColor
     *            java.awt.Color
     */
    public void setDefaultColor(java.awt.Color newDefaultColor) {
        defaultColor = newDefaultColor;
    }

    /**
     * Sets the displayType.
     * 
     * @date (18.06.01 14:18:42)
     * @param newAutoHScale
     *            boolean
     */
    public void setDisplayType(int newDisplayType) {
        displayType = newDisplayType;
        if (newDisplayType == PIANOROLL) {
            setDefaultColor(getColorForVelocity());
            doDefault();
        } else {
            setDefaultColor(Color.red.darker());
            doDefault();
        }
    }

    /**
     * Sets the highlightColor
     * 
     * @date (06.07.00 23:23:35)
     * @param newHighlightColor
     *            java.awt.Color
     */
    public void setHighlightColor(java.awt.Color newHighlightColor) {
        highlightColor = newHighlightColor;
    }

    /**
     * Sets the note.
     * 
     * @date (01.04.00 04:16:08)
     * @param newNote
     *            RhythmData.Note
     */
    public void setNote(PerformanceNote newNote) {
        note = newNote;
    }

    /**
     * Sets the number.
     * 
     * @date (18.04.00 00:58:34)
     * @param newNumber
     *            int
     */
    public void setNumber(int newNumber) {
        number = newNumber;
    }

    /**
     * Selects a display.
     * 
     * @date (04.06.00 13:49:51)
     * @param newSelected
     *            boolean
     */
    public void setSelected(boolean newSelected) {
        if (newSelected != selected)
            if (newSelected)
                setBackground(Color.orange);
            else
                setBackground(getDefaultColor());
        selected = newSelected;
    }

    /**
     * @see de.uos.fmt.musitech.utility.Highlightable#doDefault()
     */
    public void doDefault() {
        setHighlight(false);
    }

    /**
     * @see de.uos.fmt.musitech.utility.Highlightable#doHighlight()
     */
    public void doHighlight() {
        setHighlight(true);
    }

    /**
     * TODO: check if color is already present method adds a group-element (a
     * colored JPanel) to the notedisplay
     * 
     * @param c
     *            color to be added
     */

    public void addColor(Color c) {
        JPanel jp = new JPanel();
        jp.setBackground(c);
        add(jp);
        validate();
    }

    /**
     * method removes a group-element (a colored JPanel) from the notedisplay
     * 
     * @param c
     */

    public void rmColor(Color c) {
        Component[] children = getComponents();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof JPanel) {
                if (children[i].getBackground().equals(c)) {
                    remove(children[i]);

                }
            }
        }
        validate();
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
     */
    public Object getEditObj() {
        return note;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
     */
    public void destroy() {

    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
     */
    public void focusReceived() {

    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
     */
    public EditingProfile getEditingProfile() {
        return null;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    public boolean isFocused() {
        return false;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Editor)
     */
    public void init(Object editObject, EditingProfile profile,
            Display rootEditor) {

    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#updateEditor()
     */
    public void updateDisplay() {
        // TODO Auto-generated method stub

    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getRootEditor()
     */
public Display getRootDisplay() {
        if (getParent() instanceof PianoRollContainerDisplay) {
            return (PianoRollContainerDisplay) getParent();
            }
        else return null;
    }
    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#isOutmostEditor()
     */
    public boolean isOutmostEditor() {
        return false;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#conflictingChanges()
     */
    public boolean externalChanges() {
        return false;
    }

    /**
     * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
     */
    public void dataChanged(DataChangeEvent e) {

    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.MouseEditable#mouseAction(int,
     *      int, int)
     */
    public void mouseAction(int mousePosition, int dx, int dy) {
//        System.out.println("PRND: mouseAction: " + MouseEditableAdapter.mousePositionToString(mousePosition) + "editMode: " + getEditMode());
        switch (getEditMode()) {
        case PianoRollEditor.DELETE_PERFNOTE:
            if (getParent() instanceof PianoRollContainerDisplay) {
                PianoRollContainerDisplay PCD = (PianoRollContainerDisplay) getParent();
                PCD.removeNoteDisplay(this);
                return;
            }
            break;
        case PianoRollEditor.CREATE_PERFNOTE:
            break;
        case PianoRollEditor.NOTHING:
            break;
        case PianoRollEditor.EDIT_PERFNOTE:
            editNote(mousePosition, dx, dy);
            break;
        case PianoRollEditor.VELOCITY_CHANGE:
            changeVelocity(dy);
            break;
        default:
            System.out.println("default");
        }

        sendDataChangeEvent();
//        ((PianoRollContainerDisplay) getParent()).updateDisplay();
    }

    /**
     * @param dy
     */
    private void changeVelocity(int dy) {
        int velo = note.getVelocity() - dy;
        if (velo < 0) velo = 0;
        if (velo > 127) velo = 127;
        System.out.println("PRND: setVelo: " + velo);
        note.setVelocity(velo);
    }

    private void editNote(int mousePosition, int dx, int dy) {
        switch (mousePosition) {
        case MouseEditableAdapter.NORTH:
        case MouseEditableAdapter.SOUTH:
        case MouseEditableAdapter.CENTER:
            note.setTime((long) (note.getTime() + dx * getMicrosPerPix()));
            note.setPitch((byte) (note.getPitch() - dy / getVScale()));
            break;
        case MouseEditableAdapter.NORTHWEST:
        case MouseEditableAdapter.SOUTHWEST:
        case MouseEditableAdapter.WEST:
            setWestDuration(Math.round(dx * getMicrosPerPix()));
            break;
        case MouseEditableAdapter.SOUTHEAST:
        case MouseEditableAdapter.NORTHEAST:
        case MouseEditableAdapter.EAST:
            setEastDuration(Math.round(dx * getMicrosPerPix()));
            break;
        default:
            System.out.println("default");
        }
    }

    private void setEastDuration(long micSec) {
        if (-micSec > note.getDuration()) {
            note.setTime(note.getTime() + note.getDuration() + micSec);
            note.setDuration(-note.getDuration() - micSec);

        } else {
            note.setDuration(note.getDuration() + micSec);
        }

    }

    private void setWestDuration(long micSec) {
        if (micSec > note.getDuration()) {
            note.setTime(note.getTime() + note.getDuration());
            note.setDuration(micSec - note.getDuration());

        } else {
            note.setTime(note.getTime() + micSec);
            note.setDuration(note.getDuration() - micSec);
        }

    }

    /**
     *  
     */
    private void sendDataChangeEvent() {
        ArrayList changeList = new ArrayList();
        changeList.add(note);
        DataChangeManager.getInstance().changed(changeList,
                new DataChangeEvent(this, changeList));
    }

    public double getMicrosPerPix() {
        if (getParent() instanceof PianoRollContainerDisplay) {
            PianoRollContainerDisplay PCD = (PianoRollContainerDisplay) getParent();
            return PCD.getMicrosPPix();
        }

        return 0;
    }

    public double getVScale() {
        if (getParent() instanceof PianoRollContainerDisplay) {
            PianoRollContainerDisplay PCD = (PianoRollContainerDisplay) getParent();
            return PCD.getVScale();
        }

        return 0;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.MouseEditable#setCursor(int)
     */
    public void setCursor(int mousePosition) {
        switch (getEditMode()) {
        case PianoRollEditor.DELETE_PERFNOTE:
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            break;
        case PianoRollEditor.CREATE_PERFNOTE:
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            break;
        case PianoRollEditor.NOTHING:
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            break;
        case PianoRollEditor.EDIT_PERFNOTE:
            setEditCursor(mousePosition);
            break;
        case PianoRollEditor.VELOCITY_CHANGE:
            setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
            break;
        default:
            System.out.println("setCursor.default");
        }

    }

    private void setEditCursor(int mousePosition) {

        switch (mousePosition) {
        case MouseEditableAdapter.NORTHWEST:
        case MouseEditableAdapter.SOUTHEAST:
        case MouseEditableAdapter.SOUTHWEST:
        case MouseEditableAdapter.NORTHEAST:
        case MouseEditableAdapter.EAST:
        case MouseEditableAdapter.WEST:
            setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            break;
        case MouseEditableAdapter.SOUTH:
        case MouseEditableAdapter.NORTH:
        case MouseEditableAdapter.CENTER:
            setCursor(new Cursor(Cursor.MOVE_CURSOR));
            break;

        default:
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            break;
        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.MouseEditable#selected()
     */
    public void selected() {
    }

    public int getEditMode() {
        if (getParent() instanceof PianoRollContainerDisplay){
            return ((PianoRollContainerDisplay) getParent()).getEditMode();
        }
        else return 0;
    }

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	public Component asComponent() {
		return this;
	}

}