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
 * Created on 26.02.2005
 */
package de.uos.fmt.musitech.data.media.image;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;

import de.uos.fmt.musitech.framework.editor.MouseEditable;
import de.uos.fmt.musitech.framework.editor.MouseEditableAdapter;
import de.uos.fmt.musitech.framework.time.TimePanel;

/**
 * @author Jan
 *  
 */
public class StaffPositionGUI extends JComponent implements MouseEditable{

    StaffPosition staffPos;

    Color paintColor = Color.BLUE;

    protected Point pressedPoint;
    
    boolean changeCursor = true, selected = false;
    
    
    
    MouseEditableAdapter mouseEdit;
    
    public ArrayList changeListener = new ArrayList();
    
    double zoomFactor = 1.0;
    
    

    /**
     *  
     */
    public StaffPositionGUI(StaffPosition staffPos) {
        this.staffPos = staffPos;
        initialize();
        createGUI();
    }

    /**
     * 
     */
    private void initialize() {
        mouseEdit = new MouseEditableAdapter(this);
        addMouseListener(mouseEdit.getMouseListener());
        addMouseMotionListener(mouseEdit.getMouseMotionListern());
    }

    /**
     *  
     */
    private void createGUI() {
        setOpaque(false);
        setSize(staffPos.getWidth(), staffPos.getHeight());

    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        paintTime(g);
        paintTriangle(g);
        g.setColor(paintColor);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        

    }

    /**
     * @param g
     */
    private void paintTriangle(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillPolygon(new int[]{1,2,3,4}, new int[]{5,4,3,2}, 4);
    }

    private void paintTime(Graphics g) {
        
        g.setFont(new Font(null, Font.PLAIN, 9));
        g.setColor(Color.WHITE);
        int fontHeight = g.getFontMetrics().getHeight();
        int startSize = g.getFontMetrics().stringWidth(
                TimePanel.timeOutput(staffPos.getStart()));
        g.fillRect(8, getHeight() - fontHeight, startSize, fontHeight);
        g.setColor(Color.BLACK);
        g.drawString(TimePanel.timeOutput(staffPos.getStart()), 7,
                getHeight() - 2);
        int endsize = g.getFontMetrics().stringWidth(
                TimePanel.timeOutput(staffPos.getEnd()));
        g.setColor(Color.WHITE);
        g.fillRect(getWidth() - endsize - 7, getHeight() - fontHeight, endsize, fontHeight);
        g.setColor(Color.BLACK);
        g.drawString(TimePanel.timeOutput(staffPos.getEnd()), getWidth() - endsize - 8,
                getHeight() - 2);
        
    }

    public void setCursor(int mousePosition) {

        if (changeCursor) {
            switch (mousePosition) {
            case MouseEditableAdapter.NORTHEAST:
            case MouseEditableAdapter.SOUTHWEST:
                setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
                break;
            case MouseEditableAdapter.NORTHWEST:
            case MouseEditableAdapter.SOUTHEAST:
                setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                break;
            case MouseEditableAdapter.NORTH:
            case MouseEditableAdapter.SOUTH:
                setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
                break;
            case MouseEditableAdapter.EAST:
            case MouseEditableAdapter.WEST:
                setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
                break;
            case MouseEditableAdapter.CENTER:
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
                break;

            default:
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                break;
            }
        }
    }

    /**
     * @return Returns the staffPos.
     */
    public StaffPosition getStaffPos() {
        return staffPos;
    }

    /**
     * @param staffPos
     *            The staffPos to set.
     */
    public void setStaffPos(StaffPosition staffPos) {
        this.staffPos = staffPos;
    }

    /**
     * @return Returns the paintColor.
     */
    public Color getPaintColor() {
        return paintColor;
    }

    /**
     * @param paintColor
     *            The paintColor to set.
     */
    public void setPaintColor(Color paintColor) {
        this.paintColor = paintColor;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.MouseEditable#moveRelative(int,
     *      int)
     */
    public void moveRelative(int dx, int dy) {

    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.MouseEditable#moveAbsolute(int,
     *      int)
     */
    public void moveAbsolute(int x, int y) {
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.MouseEditable#mouseAction(int,
     *      int, int)
     */
    public void mouseAction(int type, int dx, int dy) {
        System.out.println(MouseEditableAdapter.mousePositionToString(type)
                + " x: " + dx + " y: " + dy);
        dx = (int) (dx / zoomFactor);
        dy = (int) (dy / zoomFactor);
        
        switch (type) {
        case MouseEditableAdapter.CENTER:
            getStaffPos().moveRelative(dx, dy);
            break;
        case MouseEditableAdapter.WEST:
            getStaffPos().resizeRelative(dx, 0, 0, 0);
            break;
        case MouseEditableAdapter.NORTHWEST:
            getStaffPos().resizeRelative(dx, 0, dy, 0);
            break;
        case MouseEditableAdapter.NORTHEAST:
            getStaffPos().resizeRelative(0, dx, dy, 0);
            break;
        case MouseEditableAdapter.NORTH:
            getStaffPos().resizeRelative(0, 0, dy, 0);
            break;
        case MouseEditableAdapter.EAST:
            getStaffPos().resizeRelative(0, dx, 0, 0);
            break;
        case MouseEditableAdapter.SOUTHEAST:
            getStaffPos().resizeRelative(0, dx, 0, dy);
            break;
        case MouseEditableAdapter.SOUTH:
            getStaffPos().resizeRelative(0, 0, 0, dy);
            break;
        case MouseEditableAdapter.SOUTHWEST:
            getStaffPos().resizeRelative(dx, 0, 0, dy);
            break;

        default:
            break;
        }

        setSize(staffPos.getWidth(), staffPos.getHeight());
        notifyListeners(null, null, null);
    }

    public void registerChangeListener(PropertyChangeListener listener){
        changeListener.add(listener);
    }
    
    public void removeListener(PropertyChangeListener listener){
        changeListener.remove(listener);
    }
    
    /**
     * @see de.uos.fmt.musitech.framework.editor.MouseEditableAdapter#notifyListeners(java.lang.Object,
     *      java.lang.Object)
     */
    public void notifyListeners(String propertyName, Object oldValue, Object newValue) {
        for (Iterator iter = changeListener.iterator(); iter.hasNext();) {
            PropertyChangeListener listener = (PropertyChangeListener) iter
                    .next();
            PropertyChangeEvent changeEvent = new PropertyChangeEvent(this,
                    propertyName, oldValue, newValue);
            listener.propertyChange(changeEvent);
        }
    }

    /** 
     * @see de.uos.fmt.musitech.framework.editor.MouseEditable#selected(javax.swing.JComponent)
     */
    public void selected() {
        
    }

    public double getZoomFactor() {
        return zoomFactor;
    }
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }
}