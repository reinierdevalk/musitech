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
package de.uos.fmt.musitech.score.gui;

import java.awt.Color;
import java.awt.Graphics;

import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a cursor that marks a time position of the score.
 *  @author Martin Gieseking
 *  @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class ScoreCursor {
	private int x= - 1 ;
	private int y;
	private int length;
	private ScorePanel scorePanel;
	private boolean visible = false; // visibility of cursor

	public ScoreCursor(ScorePanel scorePanel) {
		if (scorePanel == null)
			throw new IllegalArgumentException("scorePanel parameter must not be null");
		this.scorePanel = scorePanel;
	}

	/** Paints the cursor onto the score's graphics context. */
	public void paint(Graphics g) {
		if (visible) {
			g.setXORMode(Color.CYAN);
			//         g.drawLine(x, y, x, y+length);
			g.fillRect(x, y, 2, length);
			g.setPaintMode();
		}
	}

	/** Gets the x position of the Cursor (in pixel units). */
	public int getX() {
		return x;
	}

	/** Sets the cursor to the given metric time. */
	public void setToTime(Rational time) {
		Score score = scorePanel.getScore();
		if (score == null)
			return;
		Page page = score.pageWithTime(time);
		if (page == null)
			return;
		SSystem system = page.systemWithTime(time);
		if (system == null)
			return;
		GlobalSimSequence simseq = GlobalSimFactory.buildGlobalSims(system);
		int newX = simseq.timeToPixel(time);
		int newY = system.absY();
		if (newX != x || newY != y) {
			if (x >= 0)
				paint(scorePanel.getGraphics()); // unpaint the old cursor from former position
			x = newX;
			y = newY;
			length = system.height();
			paint(scorePanel.getGraphics());
		}
	}

	/** Sets the visibility of the cursor.
	 * @param visible true if cursor should be visible */
	public void setVisible(boolean visible) {
		if (this.visible && !visible && x >= 0)
			paint(scorePanel.getGraphics()); // unpaint (remove) cursor
		this.visible = visible;
	}
}
