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
package de.uos.fmt.musitech.score;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.PieceUtils;
import de.uos.fmt.musitech.score.gui.LocalSim;
import de.uos.fmt.musitech.score.gui.Measure;
import de.uos.fmt.musitech.score.gui.Page;
import de.uos.fmt.musitech.score.gui.SSystem;
import de.uos.fmt.musitech.score.gui.Score;
import de.uos.fmt.musitech.score.gui.Staff;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * @version 25.09.2008
 */
public class SelectionPanel extends NotationDisplay {

	private boolean selectionMode = false;
	private boolean dragging = false;
	private Point startP, stopP;
	private Rational start, stop;
	private SelectionPanelMouseListener selectionMouseListener;

	public SelectionPanel() {
		selectionMouseListener = new SelectionPanelMouseListener();
	}

	public void setSelectionMode(boolean selectionMode) {
		this.selectionMode = selectionMode;
		if (selectionMode) {
			addMouseListener(selectionMouseListener);
			addMouseMotionListener(selectionMouseListener);
		} else {
			removeMouseListener(selectionMouseListener);
			removeMouseMotionListener(selectionMouseListener);
		}
	}

	public boolean isSelectionMode() {
		return selectionMode;
	}

	public Piece getSelection() {
		return PieceUtils.extract(getScorePanel().getNotationSystem()
				.getContext().getPiece(), start, stop);
	}

	private void calculateSelectionBounds() {
		start = Rational.MAX_VALUE;
		stop = Rational.ZERO;
		Rectangle r = new Rectangle(startP.x, startP.y, stopP.x - startP.x,
			stopP.y - startP.y);
		Score s = getScorePanel().getScore();
		Page p = s.page(s.getActivePage());
		for (int i = 0; i < p.numChildren(); i++) {
			SSystem ssys = (SSystem) p.child(i);
			for (int j = 0; j < ssys.numChildren(); j++) {
				Staff st = (Staff) ssys.child(j);
				for (int k = 0; k < st.numChildren(); k++) {
					Measure m = (Measure) st.child(k);
					for (int l = 0; l < m.numChildren(); l++) {
						LocalSim ls = (LocalSim) m.child(l);
						if (ls.getBounds().intersects(r)) {
							if (ls.getMetricTime().isLess(start))
								start = ls.getMetricTime();
							if (ls.getMetricEndPoint().isGreater(stop))
								stop = ls.getMetricEndPoint();
						}
					}
				}
			}
		}
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		super.paint(g);
		if (selectionMode) {
			Color c = Color.YELLOW;
			if (dragging)
				c = Color.RED;
			int alpha = 100;
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
			if (start != null && stop != null) {
				markSelection(g);
			}
		}
	}

	private void markSelection(Graphics2D g) {
		Score s = getScorePanel().getScore();
		Page p = s.page(s.getActivePage());
		for (int i = 0; i < p.numChildren(); i++) {
			SSystem ssys = (SSystem) p.child(i);
			for (int j = 0; j < ssys.numChildren(); j++) {
				Staff st = (Staff) ssys.child(j);
					Rectangle stR = null;
					for (int k = 0; k < st.numChildren(); k++) {
						Measure m = (Measure) st.child(k);
						for (int l = 0; l < m.numChildren(); l++) {
							LocalSim ls = (LocalSim) m.child(l);
							if (stR == null
								&& ls.getMetricTime().isGreaterOrEqual(start)) {
								stR = st.getBounds();
								stR.x = ls.getBounds().x;
								stR.width = 0;
							}
							if (stR != null && ls.getMetricTime().isLess(stop))
								stR.width = ls.getBounds().x
											+ ls.getBounds().width - stR.x;
						}
					}
					if (stR != null)
						g.fill(stR);
			}
		}
	}

	private class SelectionPanelMouseListener extends MouseAdapter implements
			MouseMotionListener {

		@Override
		public void mousePressed(MouseEvent e) {
			start = null;
			stop = null;
			startP = e.getPoint();
			dragging = true;
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			dragging = false;
			repaint();
			startP = null;
			stopP = null;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			stopP = e.getPoint();
			calculateSelectionBounds();
			repaint();
		}

	}
}
