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
 * Created on 06-Jan-2005
 *
 */
package de.uos.fmt.musitech.score.gui;

import java.awt.Graphics;

/**
 * @author collin
 *
 */
public class BarlineCustom extends Barline {
	private float from, to;
	
	/**
	 * a barline will be created which goes from from to to. from and to
	 * are given as line distances from the top line of the staff.
	 * @param from
	 * @param to
	 */
	public BarlineCustom(float from, float to) {
		this.from = from;
		this.to = to;
	}
	
	void movePropertiesTo(ScoreObject o) {
		super.movePropertiesTo(o);
		((BarlineCustom)o).from = from;
		((BarlineCustom)o).to = to;
	}
	
	/**
	 * @see de.uos.fmt.musitech.score.gui.Barline#paint(java.awt.Graphics, int, int, int)
	 */
	void paint(Graphics g, int x, int y1, int y2) {
		if(!isVisible()) return;
		super.paint(g, x, y1, y2);
		x -= rwidth();
		
		int ld = staff().getLineDistance();
		int topY = staff().getTopLineY();
		y1 = topY + (int)(from * ld);
		y2 = topY + (int)(to * ld);
		
		g.drawLine(x, y1, x, y2); 
	}

	/**
	 * @see de.uos.fmt.musitech.score.gui.Barline#create()
	 */
	public Barline create() {
		return new BarlineCustom(0, 5);
	}
	
	public int barlineRwidth() {
		return 1;
	}
}
