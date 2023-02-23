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

import de.uos.fmt.musitech.score.util.Bezier;

/** This class represents a flexible brace that connects multiple
 * consecutive staves on the left of a System. The curly version
 * normaly denotes staves that belong to the same instrument.
 * @author Martin Gieseking
 * @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $ */
public class StaffBrace extends StaffConnector {
	private Bezier b, b2;
	private int width;
	
	public StaffBrace(Staff first, Staff last) {
		super(first, last);
	}
	
	@Override
	public int arrange(int pass) {
		int ld = getFirstStaff().getLineDistance();
		int y1 = getFirstStaff().absY();
		int y2 = getLastStaff().absY() + getLastStaff().naturalHeight();
		int x1 = getFirstStaff().absX();
		int x2 = getLastStaff().absX();
		
		int dent = 3 * ld;
		
		int middleX = x1 - 12;
		int middleY = y1 + ((y2 - y1) / 2);
		
		b = new Bezier(x1, y1, 
		               x1 - dent, y1 + (middleY - y1) / 3,
			           x1 + dent / 2, y1 + ((middleY - y1) / 3) * 2,
			  	       middleX, middleY);
		b2 = new Bezier(middleX, middleY,
  				        x2 + dent / 2, y2 - (y2 - middleY) / 3,
				        x2 - dent, y2 - ((y2 - middleY) / 3) * 2,
						x2, y2);
		
		width = 24;
		
		return 3;
	}

	@Override
	public void paint(java.awt.Graphics g) {
		if(!isVisible()) return;
		g.setPaintMode();
		b.paint(g);
		b2.paint(g);
	}
	
	@Override
	public int rwidth() {
		return width;
	}
}
