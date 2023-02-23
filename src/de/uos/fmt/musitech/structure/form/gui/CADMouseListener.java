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
 * Created on 21.07.2004
 */
package de.uos.fmt.musitech.structure.form.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.selection.SelectingEditor;
import de.uos.fmt.musitech.framework.selection.Selection;
import de.uos.fmt.musitech.framework.selection.SelectionManager;

/**
 * @author Jan
 *
 */
public class CADMouseListener implements MouseListener, MouseMotionListener {

	SelectingEditor display;
	Selection selection = SelectionManager.getManager().getSelection();
	private Point dragStart;
	
	/**
	 * 
	 */
	public CADMouseListener(SelectingEditor CAD) {
		super();
		display = CAD;
		// TODO Auto-generated constructor stub
	}

	/** 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		/*
		* Problem: der SelectionManager kann nur mit MObjekts arbeiten.
		* Wir muessen an das Datenobjekt der angeklickten graphischen Komponente kommen.
		* D.h. wir muessen von der graphischen Ebene auf die Datenebene kommen.
		* 
		* Kopiert von SelectionController (Jan K.) 
		*/
		Object clicked;
		if (e.getSource() instanceof Display) {
			clicked = ((Display) e.getSource()).getEditObj();
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.isControlDown())
					if (selection.isSelected(clicked))
						selection.remove(clicked,display);
					else
						selection.add(clicked,display);

				else {
					selection.clear(display);
					selection.add(clicked,display);
				}
			}
		} else {
			System.out.println(
				"WARNING: SelectionAdapter has been connected to a Component which is not a Display.");
		}
	}

	/** 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/** 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/** 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		dragStart = e.getPoint();
	}

	/** 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/** 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		Point ds = (Point) dragStart.clone();

		if (p.x < ds.x) {
			int z = ds.x;
			ds.x = p.x;
			p.x = z;
		}

		if (p.y < ds.y) {
			int z = ds.y;
			ds.y = p.y;
			p.y = z;
		}

		Rectangle r = new Rectangle(ds.x, ds.y, p.x - ds.x, p.y - ds.y);

		display.paintDragArea(r);
		if (display.objectsTouched(r) != null)
			selection.addAll(display.objectsTouched(r),display);
	}

	/** 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
	}

}
