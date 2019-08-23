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
 * Created on 12-Nov-2004
 *
 */
package de.uos.fmt.musitech.score.gui;

import java.awt.Graphics;

import de.uos.fmt.musitech.data.score.CharSymbol;
import de.uos.fmt.musitech.data.score.MetricAttachable;
/**
 * @author collin
 *
 * This class is the graphical representation of a char which is to be drawn somewhere in the
 * score. The gin.ttf font, which contains musical symbols is used for drawing.
 * 
 */
public class CustomCharScoreObject extends CustomScoreObject {
	char c;
	
	public CustomCharScoreObject(ScoreObject master, CharSymbol symbol, MetricAttachable ma) {
		super(master, ma);
		c = symbol.getSymbol();
	}
	
	public int rwidth() {
		return MusicGlyph.width(master.staff().getLineDistance(), c);
	}
	
	public int height() {
		return MusicGlyph.height(master.staff().getLineDistance(), c);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		int x = absX();
		if (getAttachable().getAlignment() == MetricAttachable.CENTER) {
			x -= MusicGlyph.width(master.staff().getLineDistance(), c) / 2;
		}
		else if (getAttachable().getAlignment() == MetricAttachable.RIGHT) {
			x -= MusicGlyph.width(master.staff().getLineDistance(), c);
		}
		setScaledFont(g);
		g.drawChars(new char[]{c}, 0, 1, x, absY());
		restoreFont(g);
	}


}
