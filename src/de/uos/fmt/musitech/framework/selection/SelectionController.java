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
package de.uos.fmt.musitech.framework.selection;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import de.uos.fmt.musitech.framework.editor.Display;

/**
 * Provides MouseListener for <code>SelectingEditor</code>s.
 * If you want to use the SelectionController for a locally working SelectingEditor,
 * you have to set the SelectionController's <code>selection</code> to a LocalSelection.
 * Otherwise, the <code>selection</code> of the SelectionManager is taken which is
 * a DistributedSelection and notifies the SelectionManager when the selection is
 * changed. The SelectionManager then notifies all registered SelectingEditors.
 * 
 * @author Tillman Weyde, Felix Kugel
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class SelectionController implements MouseListener, MouseMotionListener {

    private SelectingEditor selectingEditor;
    private Selection selection = SelectionManager.getManager().getSelection();
    private boolean dragging = false;
    private Point dragStart;
    private SelectionAdapter selectionAdapter = new SelectionAdapter();

    /**
     * Create a controller for a display.
     * 
     * @param display create the controller for this.
     */
    public SelectionController(SelectingEditor display) {
        this.selectingEditor = display;
    }
    
    /**
     * Constructor. Creates a SelectionController for the specified SelectingEditor and
     * sets its <code>selection</code> to the specified Selection. If the SelectingEditor
     * is locally working, i.e. changes in the selection should not be committed to
     * other SelectingEditors, the specified Selection must be a LocalSelection.
     * 
     * @param display SelectingEditor which will use the SelectionController
     * @param selection Selection the SelectionController is working with
     */
    public SelectionController(SelectingEditor display, Selection selection){
        this(display);
        setSelection(selection);
    }
    
    /**
     * Sets the <code>selection</code> of this SelectionController to the specified
     * Selection. If the SelectionController should not notify other SelectingEditors 
     * about changes in the selection via the SelectionManager, the specified Selection
     * must be a LocalSelection.
     * 
     * @param selection Selection to be set as the <code>selection</code> this SelectionController is working with
     */
    public void setSelection(Selection selection){
        this.selection = selection;
    }

    /**
     * Add an object to the selection.
     * 
     * @param o the object to add
     */
    public void addObjectToSelection(Object o) {
        selection.add(o, selectingEditor);
    }

    /**
     * Clear the selection.
     */
    public void clearSelection() {
        selection.clear(selectingEditor);
    }

    /**
     * 1. Clicks kommen an <BR>
     * 2. Selection wird dementsprechend verändert (ist im SelectionManager)
     * <BR>
     * 3. SelectionManager uebermittelt SelectionChanged an alle Displays, die
     * sich vorher angemeldet haben
     */
    class SelectionAdapter extends MouseAdapter {

        @Override
		public void mouseClicked(MouseEvent e) {
            /*
             * Problem: der SelectionManager kann nur mit MObjects arbeiten. Wir
             * muessen an das Datenobjekt der angeklickten graphischen
             * Komponente kommen. D.h. wir muessen von der graphischen Ebene auf
             * die Datenebene kommen.
             */
            Object clicked;
            if (e.getSource() instanceof Display) {
                clicked = ((Display) e.getSource()).getEditObj();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.isControlDown())
                        if (selection.isSelected(clicked))
                            selection.remove(clicked, selectingEditor);
                        else
                            addObjectToSelection(clicked);

                    else {
                        clearSelection();
                        addObjectToSelection(clicked);
                    }
                }
            } else {
                System.out.println("WARNING: SelectionAdapter has been connected to a " 
                                   +"Component which is not a Display.");
            }
        }

        /**
         * Implementation of MouseListener method.
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
		public void mousePressed(MouseEvent e) {
            mouseClicked(e);
        }
    }

    /**
     * Get the SelectionAdapter, which is used for graphical objects, representing a . 
     * @return The SelectionAdapter.
     */
    public MouseListener getSelectionAdapter() {
        return selectionAdapter;
    }

    /**
     * This click is performed only if no object is clicked, since all objects
     * have their own event handlers.
     * 
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    @Override
	public void mouseClicked(MouseEvent e) {
        if (!e.isPopupTrigger()) {
            selection.clear(selectingEditor);
        }

    }

    /**
     * This event initiates a dragging procedure.
     * 
     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
     */
    @Override
	public void mousePressed(MouseEvent e) {
        dragStart = e.getPoint();
        setDragging(true);
    }

    /** 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
	public void mouseReleased(MouseEvent e) {
        if (isDragging()) {
            Point p = e.getPoint();
            int x = dragStart.x;
            int y = dragStart.y;
            int w = p.x - dragStart.x;
            int h = p.y - dragStart.y;
            if (w < 0) {
                x += w;
                w = -w;
            }
            if (h < 0) {
                y += h;
                h = -h;
            }
            Rectangle r = new Rectangle(x, y, w, h);

            selectingEditor.paintDragArea(r);
            if (selectingEditor.objectsTouched(r) != null)
                selection.replace(selectingEditor.objectsTouched(r), selectingEditor);
        }
        setDragging(false);
        selectingEditor.paintDragArea(null);
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
     */
    @Override
	public void mouseEntered(MouseEvent arg0) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
     */
    @Override
	public void mouseExited(MouseEvent arg0) {
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
     */
    @Override
	public void mouseMoved(MouseEvent arg0) {
    }

    /**
     * Mouse dragged
     * 
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
	public void mouseDragged(MouseEvent e) {

        if (isDragging()) {
            Point p = e.getPoint();
            int x = dragStart.x;
            int y = dragStart.y;
            int w = p.x - dragStart.x;
            int h = p.y - dragStart.y;
            if (w < 0) {
                x += w;
                w = -w;
            }
            if (h < 0) {
                y += h;
                h = -h;
            }
            Rectangle r = new Rectangle(x, y, w, h);

            selectingEditor.paintDragArea(r);
            //            if (selectingEditor.objectsTouched(r) != null)
            //                selection.replace(selectingEditor.objectsTouched(r),selectingEditor);
        }
    }

    /**
     * True if a dragging procedure is currently performed.
     * 
     * @return boolean
     */
    private boolean isDragging() {
        return dragging;
    }

    /**
     * Sets the dragging state.
     * 
     * @param dragging The dragging to set
     */
    private void setDragging(boolean _dragging) {
        this.dragging = _dragging;
    }

}