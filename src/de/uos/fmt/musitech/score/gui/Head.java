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
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import de.uos.fmt.musitech.data.score.CustomGraphic;
import de.uos.fmt.musitech.score.util.Bezier;
import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.math.Rational;
/**
 * This class represents a note head.
 * @author Martin Gieseking
 * @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $ 
 */
public class Head extends ScoreObject {
	private int type; // 1=whole, 2=white, 4 = black
	private int shifted = 0; // how much we shifted
	
	/**
	 * If this is true, then notes longer than 1/1 are drawn as a 1/4 with a long trailing beam
	 */
	private boolean drawDurationExtension;

	private String glyph;
	private CustomGraphic customHead;
	
	private static final String HEAD_WHOLE = "" + MusicGlyph.HEAD_WHOLE;
	private static final String HEAD_WHITE = "" + MusicGlyph.HEAD_WHITE;
	private static final String HEAD_BLACK = "" + MusicGlyph.HEAD_BLACK;

	
	public Head(Pitch parent, Rational dur) {
		super(parent);
		
		
		if (dur.getDenom() < 1)
			throw new IllegalArgumentException("denominator of duration must be >= 1");
		type = dur.isLess(1,2) ? 4 : dur.isLess(1,1) ? 2 : 1;
		
		glyph = getGlyph();
		//addMouseListener(new MouseAction(this));
	}

	public int getType() {
		return type;
	}

	int numAuxLines() {
		if (Math.abs(pitch().line()) <= 5)
			return 0;
		return (Math.abs(pitch().line()) - 4) / 2;
	}

	
	@Override
	int arrange(int pass) {
		if (pass == 0) {
			Pitch pitch = pitch();
			if (pitch.getNote() != null &&
				pitch.getNote().getRenderingHints() != null &&
				pitch.getNote().getRenderingHints().containsKey("custom head")) {
				customHead = (CustomGraphic)pitch.getNote().getRenderingHints().getValue("custom head");
			}
		}
		if (pass == 2) {
			if (customHead != null) {
				setLocation(absX(), absY() - (height() / 2));
			}
			else {
				setLocation(absX(), absY() - height());
			}
			setSize(rwidth(),height() + depth());

			if (pitch().isVisible() && numAuxLines() > 0) {
				staff().registerAuxLines(this);
			}
		}
		return 3;
	}

	/**
	 * Die Beschreibung der Methode hier eingeben.
	 * Erstellungsdatum: (06.11.2001 15:41:19)
	 * @param g java.awt.Graphics
	 */
	@Override
	public void paint(Graphics g) {
		if(!isVisible()) return;
		int ld = staff().getLineDistance();
		if (customHead != null) {
			int height = ld * customHead.getHeight();
			int width = ld * customHead.getWidth();
			customHead.paint(g, absX(), absY(), width, height, this);
		}
		else {
			setScaledFont(g);
			if (drawDurationExtension) {
				g.drawString(HEAD_BLACK, absX(), absY());

				int x = (int)((MusicGlyph.width(ld, MusicGlyph.HEAD_BLACK) + 4) * scale);
				int optimalSpace = (int)localSim().optimalSpace(localSim().getMetricDuration(), rwidth()) - rwidth();
				if (chord().getDurationExtensionPulldown() != 0) {
					float factor = chord().getDurationExtensionPulldown();
					int startX = absX() + x;
					int startY = absY() - ld / 4;
					Bezier b1 = new Bezier(startX,
											startY,
											startX + (int)(optimalSpace * factor * 0.75),
											startY,
											startX + (int)(optimalSpace * factor * 0.9),
											startY + ld / 2,
											startX + (int)(optimalSpace * factor),
											startY + ld);
					startX = startX + (int)(optimalSpace * factor);
					startY = startY + ld;
					Bezier b2 = new Bezier(startX,
										   startY,
										   startX + (int)(optimalSpace * (1 - factor) * 0.1),
										   startY - ld * 1.5,
										   startX + (int)(optimalSpace * (1 - factor) * 0.75),
										   startY - ld,
										   absX() + x + optimalSpace,
										   absY() - ld / 4);

					Graphics2D g2 = (Graphics2D)g;

					Stroke oldStroke = g2.getStroke();
					RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
					qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); 
					g2.setRenderingHints(qualityHints); 
					g2.setStroke(new BasicStroke(4.0f));
					b1.paint(g);
					b2.paint(g);
					g2.setStroke(oldStroke);
				}
				else {

					g.fillRect(absX() + x, absY() - ld / 4, optimalSpace, ld / 2);
				}
			}
			else {
				g.drawString(glyph, absX(), absY());
			}
			restoreFont(g);
		}
		// draw auxiliary lines below or above staff
		// @@ noch nicht optimal: Hilfslinien werden u.U. mehrfach ?bereinander gezeichnet
		// @@ ausgelagerte K?pfe werden noch nicht ber?cksichtigt
		
		/*
		if (numAuxLines() > 0) {
			int auxy = staff.absY(); // absolute y-pos of first aux. line
			auxy += (y > staff.absY() ? staff.getNumberOfLines() : -1) * staff.getLineDistance();
			int dir = y > staff.absY() ? 1 : -1;
			int overlap = staff.getLineDistance() / 3;
			for (int i = numAuxLines(); i > 0; i--) {
				g.drawLine(x - overlap, auxy, x + hw + overlap, auxy);
				auxy += dir * staff.getLineDistance();
			}
		} 
		*/
		//drawFrame(g);
	}

	int getShiftAmount() {
		return (int)(MusicGlyph.width(staff().getLineDistance(), getGlyph().charAt(0)) * scale);
	}

	/** Moves this Head one head width to the right. */
	void shiftRight() {
		int shift = getShiftAmount();
		addToXPos(shift);
		shifted += shift;
	}

	/** Moves this Head one head width to the right. */
	void shiftLeft() {
		int shift = -getShiftAmount();
		addToXPos(shift);
		shifted += shift;
	}
	
	void unShift() {
		addToXPos(-shifted);
		shifted = 0;
	}
	
	int getShifted() {
		return shifted;
	}

	
	public String getGlyph() {
		switch (type) {
			case 1 :
				return HEAD_WHOLE;
			case 2 :
				return HEAD_WHITE;
			case 4 :
				return HEAD_BLACK;
		}
		return "";
	}

	@Override
	public int rwidth() {
		int width;
		Staff staff = staff();
		int ld = staff.getLineDistance();

		if (customHead != null) {
			width = customHead.getWidth() * ld;
		}
		else {
			if (type == 1) {
				width = MusicGlyph.width(ld, MusicGlyph.HEAD_WHOLE);
			}
			else {
				width = MusicGlyph.width(ld, MusicGlyph.HEAD_WHITE);
			}
		}
		
		if (staff.getScale() == 1) { //the staff is unscaled => use own scale 
			if (type == 1)
				return (int)(width * scale);
			return (int)(width * scale);
		}
		else { //the staff is scaled => disregard own scale
			if (type == 1)
				return width;
			return width;
		}	
	}

	@Override
	Class parentClass() {
		return Pitch.class;
	}

	@Override
	public int height() {
		int height;
		Staff staff = staff();
		int ld = staff.getLineDistance();
		if (customHead != null) {
			height = customHead.getHeight() * ld;
		}
		else {
			height = ld / 2;
		}
		
		if (staff.getScale() == 1) { //the staff is unscaled => use own scale
			return (int)(height * scale);
		}
		else { //the staff is scaled => disregard own scale
			return height / 2;
		}
	}

	@Override
	public int depth() {
		if (customHead != null)
			return 0;
		
		return height();
	}

   /** Returns the point where to fix potential tie ends. 
    * @param left true if point of left tie end shall be returned
    * @param up   true if tie curves upwards */	
   Pair tiePoint(boolean left, boolean up) {
		int ld = staff().getLineDistance();		
      int x = absX() + (left ? rwidth() : 0);
      int y = absY() + ld/2 * (up ? -1 : 1);
      Pair res = new Pair(x, y);
      return res;
		// move point between 2 staff lines      
//		if (!staff().isInside(res))
//			return res;
//		int hs = staff().pixelToHs(res.getRoundedY());
//		if (hs % 2 == 0) // on line
//			hs += up ? 1 : -1;
//		return new Pair(res.getX(), staff().hsToPixel(hs));
	}
	public boolean isDrawDurationExtension() {
		return drawDurationExtension;
	}
	public void setDrawDurationExtension(boolean drawDurationExtension) {
		this.drawDurationExtension = drawDurationExtension;
	}
}
