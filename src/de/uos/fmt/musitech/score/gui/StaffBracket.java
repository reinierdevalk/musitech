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

import java.awt.Graphics;
import java.util.Vector;

import de.uos.fmt.musitech.score.util.Angle;
import de.uos.fmt.musitech.score.util.Bow;
import de.uos.fmt.musitech.score.util.Graphix;
import de.uos.fmt.musitech.score.util.Pair;

/** This class represents a long square bracket that is used to group
 * multiple successive staves.
 * @author Martin Gieseking
 * @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $ */
public class StaffBracket extends StaffConnector {
	private Vector bracketPoints;
	private int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
	private int maxX = 0, maxY = 0;
	
	public StaffBracket(Staff first, Staff last) {
		super(first, last);
	}

	public void paint(Graphics g) {
		if(!isVisible()) return;
		new Graphix(g).fillPolygon(bracketPoints);
		//drawFrame(g);
	}
	
	public int arrange(int pass) {
		if (pass >= 1) {
			int ld = getFirstStaff().getLineDistance();
			int y1 = getFirstStaff().absY();
			int y2 = getLastStaff().absY() + getLastStaff().naturalHeight();
			int height = y2 - y1;
			int width = 4 * ld / 3;
			int thickness = ld / 2;
			int gap = ld / 2; // horizontal gap between staves and bracket (in pixel units)
			int x = getFirstStaff().absX() - gap;
			//      g.fillRect(x-thickness, y1, thickness, y2-y1);

			Pair p1 = new Pair(x - thickness, y1 - thickness);
			Pair p2 = new Pair(x, y1 - thickness);
			Pair p3 = new Pair(x + width, y1 - thickness - ld);
			Pair p4 = new Pair(x, y1);
			Pair p5 = new Pair(x, y2);
			Pair p6 = new Pair(x + width, y2 + thickness + ld);
			Pair p7 = new Pair(x, y2 + thickness);
			Pair p8 = new Pair(x - thickness, y2 + thickness);

			bracketPoints = new Vector();
			bracketPoints.addAll(new Bow(p1, p3, 3, new Angle(30, Angle.DEG)).collectToList(10));
			bracketPoints.addAll(new Bow(p3, p4, -3, new Angle(-20, Angle.DEG)).collectToList(10));
			bracketPoints.addAll(new Bow(p5, p6, -3, new Angle(-20, Angle.DEG)).collectToList(10));
			bracketPoints.addAll(new Bow(p6, p8, 3, new Angle(30, Angle.DEG)).collectToList(10));
	
			for (int i = 0; i < bracketPoints.size(); i++) {
				Pair p = (Pair)bracketPoints.get(i);
				x = p.getRoundedX();
				int y = p.getRoundedY();
				minX = Math.min(minX, x);
				maxX = Math.max(maxX, x);
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
			}
		
		}
		if (pass == 2) {
			setLocation(minX, minY);
			setSize(maxX - minX, maxY - minY);
		}
		return 3;
	}
	
	public int rwidth() {
		return maxX - minX;
	}
}
