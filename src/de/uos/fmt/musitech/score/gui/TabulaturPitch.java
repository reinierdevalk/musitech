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
 * Created on 20-Dec-2004
 *
 */
package de.uos.fmt.musitech.score.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;

import de.uos.fmt.musitech.data.score.TablatureNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.score.util.Bezier;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *
 */
public class TabulaturPitch extends Pitch {
	private HashMap graphicalToNotation;
	private Pitch shadowCaster;
	private Font tabFont = new Font("Serif", Font.PLAIN, 12);
	
	
	public TabulaturPitch(Pitch shadowCaster, HashMap graphicalToNotation) {
		super();
		this.shadowCaster = shadowCaster;
		this.graphicalToNotation = graphicalToNotation;
		if (graphicalToNotation == null)
			throw new NullPointerException("graphicalToNotation may not be null");
	}
	
	public void contentAdded(Object newContent) {
		
	}
	
	public void contentRemoved(Object content) {
		
	}

	@Override
	public int absY() {
		TablatureNote tn = getTabulaturNote();
		
		if (tn != null) {
			int[] ys = getPullUpYs(tn);
			return ys[0] + 25; //TODO: the 25 must come from the FontMetrics... but where do we get those?
		}
		else {
			return super.absY();
		}
	}
	
	TablatureNote getTabulaturNote() {
		Note n = (Note)graphicalToNotation.get(shadowCaster);
		TablatureNote tn = null;
		if (n.getRenderingHints() != null &&
			n.getRenderingHints().containsKey("tabulatur note")) {
			tn = (TablatureNote)n.getRenderingHints().getValue("tabulatur note");
		}

		return tn;
	}
	
	int[] getPullUpYs(TablatureNote tn) {
		int ld = staff().getLineDistance();

		int[] ys = new int[4];
		ys[0] = ((TabulaturStaff)staff()).getYForLine(tn.getInstrString());
		ys[1] = ys[0];
		ys[2] = ys[0] - (int)(ld * 1.5);
		ys[3] = ys[0] - ld * 2;
		
		return ys;
	}
	
	int getTopPullUpY() {
		TablatureNote tn = getTabulaturNote();
		if (tn == null ||
			tn.getPullUp() == null ||
			tn.getPullUp().equals(Rational.ZERO)) {
			return 0;
		}
		return getPullUpYs(tn)[3];
	}
	
	@Override
	public void paint(Graphics g) {

		setScaledFont(g);

		Font oldFont = g.getFont();
		g.setFont(tabFont.deriveFont(oldFont.getSize() / 3));

		TablatureNote tn = getTabulaturNote();
		if (tn != null) {
			String s = tn.getFret() + "";
			if (tn.isParanthised()) {
				s = "(" + s + ")";
			}
			int width = g.getFontMetrics().stringWidth(s);
			int[] ys = getPullUpYs(tn);
			
			g.drawString(s, absX() - width / 2, ys[0] + g.getFontMetrics().getAscent() / 2);
			if (tn.getPullUp() != null && 
				!tn.getPullUp().equals(Rational.ZERO)) {
				int ld = staff().getLineDistance();
				int xspace = (int)(ld * 0.75);
				int x = absX() + width / 2 + xspace;
				Bezier b = new Bezier(absX() + width / 2,
									  ys[0],
									  x,
									  ys[1],
									  x,
									  ys[2],
									  x,
									  ys[3]);
				b.paint(g);
				String pullUpString;
				if (tn.getPullUp().getDenom() == 1) {
					pullUpString = tn.getPullUp().getDenom() + "";
				}
				else {
					pullUpString = tn.getPullUp().toString();
				}
				int desc = g.getFontMetrics().getDescent();
				int pWidth = g.getFontMetrics().stringWidth(pullUpString);
				g.drawString(pullUpString, x - pWidth / 2, ys[3] - desc);
			}
		}
		restoreFont(g);
	}
}
