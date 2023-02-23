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
 * Created on 27.02.2005
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

/**
 * This can be used to add possibility of MouseEdits to JComponents i.e.
 * resizing, moving ... etc. MouseAction will be called, if user has dragged one
 * edge of the component. JComponents who wants to use this class have to
 * implement MouseEditable You get MouseMotionLister and MouseListern via get
 * Methods
 * 
 * @author Jan-H. Kramer
 *  
 */
public class MouseEditableAdapter {

    // Resize Constants, negative: west, positive: east, even: north, odd: south
    // combination like NORTHWEST is WEST*NORTH+NORTH, or SOUTHEAST is    
	// EAST*SOUTH+SOUTH
    public final static int NORTHWEST = -18, NORTH = 2, NORTHEAST = 22,
            WEST = -10, CENTER = 0, EAST = 10, SOUTHWEST = -27, SOUTH = 3,
            SOUTHEAST = 33;

    int mousePosition = 0;

    final int editDistance = 10; // distance from side to Edit

    Point pressed, released;

    // two references of the same Object !
    JComponent jComponent;

    MouseEditable mouseEdit;

    /**
     * Default Constructor, should not be used. Use
     * MouseEditableAdapter(JComponent) instead.
     */
    public MouseEditableAdapter() {
    }

    /**
     * Constructor JComponent must implement MouseEditable to use this class!!
     */
    public MouseEditableAdapter(JComponent comp) {
        setJComponent(comp);
    }

    /**
     * Inner class MouseAdapter
     */
    public class MouseEditableListener extends MouseAdapter {

        @Override
		public void mouseClicked(MouseEvent e) {
        }

        @Override
		public void mouseEntered(MouseEvent e) {
        }

        @Override
		public void mousePressed(MouseEvent e) {
            pressed = e.getPoint();
            mousePosition = getMousePosition(e.getPoint());
        }

        @Override
		public void mouseReleased(MouseEvent e) {
            released = e.getPoint();
            mouseEdit.mouseAction(mousePosition, released.x - pressed.x,
                    released.y - pressed.y);
        }

        @Override
		public void mouseExited(MouseEvent e) {
        }
    };

    private class MouseEditableMotionListern extends MouseMotionAdapter {
        @Override
		public void mouseDragged(MouseEvent e) {
        }

        @Override
		public void mouseMoved(MouseEvent e) {
            mousePosition = getMousePosition(e.getPoint());
            //            Point convertPoint = SwingUtilities.convertPoint((JComponent)
            // e.getSource(), e.getPoint(), null);
            //            System.out.println("x: " + e.getX() + " y: " + e.getY() + "
            // convertTo x: " + " y: ");
            mouseEdit.setCursor(mousePosition);

        }
    };

    /**
     * Calculates the Mouse Position in JComponent e.g. North is upper position
     * in Component (boarder + editDistance)
     * 
     * @param point
     * @return mousePosition (e.g. 2 = NORTH, 33 = SOUTHEAST)
     */
    private int getMousePosition(Point point) {
        int pointPos = 0; // = CENTER: default
        if (point.x <= editDistance) {
            // Clicked left border
            pointPos = WEST;
        } else if (point.x >= jComponent.getWidth() - editDistance - 1) {
            // Clicked right border
            pointPos = EAST;
        }
        if (point.y <= editDistance) {
            // Clicked upper border
            pointPos *= NORTH;
            pointPos += NORTH;
        } else if (point.y >= jComponent.getHeight() - editDistance - 1) {
            // Clicked lower border border
            pointPos *= SOUTH;
            pointPos += SOUTH;
        }
        return pointPos;
    }

    /**
     * @return Returns the mousePosition.
     */
    public int getMousePosition() {
        return mousePosition;
    }

    public static String mousePositionToString(int mousePosition) {
        switch (mousePosition) {
        case CENTER:
            return "center";
        case NORTH:
            return "north";
        case SOUTH:
            return "south";
        case NORTHWEST:
            return "northwest";
        case NORTHEAST:
            return "northeast";
        case SOUTHWEST:
            return "southwest";
        case SOUTHEAST:
            return "southeast";
        case WEST:
            return "west";
        case EAST:
            return "east";
        default:
            return "undefined";

        }
    }

    public JComponent getJComponent() {
        return jComponent;
    }

    /**
     * JComponent must implement MouseEditable to use this class!!
     * 
     * 
     * @param comp
     */
    public void setJComponent(JComponent comp) {
        if (comp instanceof MouseEditable) {
            this.jComponent = comp;
            this.mouseEdit = (MouseEditable) comp;
        } else
            throw new IllegalArgumentException(
                    "JComponent must implement mouseEditable !!");
    }

    public MouseMotionListener getMouseMotionListern() {
        return new MouseEditableMotionListern();
    }

    public MouseListener getMouseListener() {
        return new MouseEditableListener();
    }

}