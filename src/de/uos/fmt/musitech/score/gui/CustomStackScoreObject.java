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
import java.util.ArrayList;
import java.util.Iterator;

import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.StackSymbol;
import de.uos.fmt.musitech.data.score.SymbolicObject;

/**
 * @author collin
 * 
 * This class is the graphical representation of a stack of ScoreObjects. The objects are
 * draw on top of one another, all with the same X-Coordinate. 
 * 
 */
public class CustomStackScoreObject extends CustomScoreObject {
	private ArrayList symbols = new ArrayList();
	private int space = 2;
	
	@Override
	int arrange(int pass) {
		super.arrange(pass);
		for (Iterator iter = symbols.iterator(); iter.hasNext();) {
			ScoreObject element = (ScoreObject) iter.next();
			element.arrange(pass);
		}
		if (pass == 2) {
			ScoreObject first = (ScoreObject)symbols.get(0); 
			int y = first.height();
			for (int i = 1; i < symbols.size(); i++) {
				ScoreObject element = (ScoreObject)symbols.get(i);
				element.addToYPos(-(y + element.depth() + space));
				y += element.depth() + element.height() + space;
			}
		}
		return 3;
	}
	
	public CustomStackScoreObject(ScoreObject master, StackSymbol symbol, MetricAttachable ma) {
		super(master, ma);
		ArrayList cache = new ArrayList();
		while (symbol.size() > 0) {
			MetricAttachable ma2 = new MetricAttachable(ma);
			SymbolicObject so = symbol.pop();
			ma2.setSymbol(so);
			cache.add(0, so);
			symbols.add(0, CustomScoreObject.createCustomScoreObject(master, ma2));
		}
		for (int i = 0; i < cache.size(); i++) {
			symbol.push((SymbolicObject)cache.get(i));
		}
	}

	@Override
	public void setAnker(ScoreObject anker) {
		super.setAnker(anker);
		for (Iterator iter = symbols.iterator(); iter.hasNext();) {
			CustomScoreObject element = (CustomScoreObject) iter.next();
			element.setAnker(anker);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		for (Iterator iter = symbols.iterator(); iter.hasNext();) {
			ScoreObject element = (ScoreObject) iter.next();
			element.paint(g);
		}
	}

	public int getSpace() {
		return space;
	}
	public void setSpace(int space) {
		this.space = space;
	}
}
