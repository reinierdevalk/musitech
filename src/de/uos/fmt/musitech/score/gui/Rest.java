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

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.score.util.Pair;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a single rest.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class Rest extends Event {
	private Note note;
	
	public Rest(Duration dur, int voice, Note note) {
		super(dur, voice);
		this.note = note;
	}

	@Override
	public int arrange(int pass) {
		int passes = super.arrange(pass);
		
		if (pass == 2) {
			setLocation(absX() - lwidth(), staff().absY() + staff().hsToPixel(0) - height());
			setSize(lwidth() + rwidth(), height() + depth());
		}
		return passes;
	}
	
	@Override
	public void paint(Graphics g) {
		if (!isVisible())
			return;
		
		Color oldColor = g.getColor();
		g.setColor(getColor());
		
		setScaledFont(g);

		Staff staff = staff();
		
		Rational wDuration = new Rational(getDuration().getBase()); //working Duration
		int x = absX();
		if (wDuration.isGreater(new Rational(1, 1))) {
			Rational two = new Rational(2, 1);
			int y = staff.absY() + staff.hsToPixel(1) - staff.getLineDistance() / 2;
			int height = staff.getLineDistance();
			int width = staff.getLineDistance() / 2;
			
			int i = 0;
			while (wDuration.isGreaterOrEqual(two)) {
				g.fillRect(x, y, width, height);
				switch (i) {
					case 0: x += 4 * width; break;
					case 1: x = absX(); 
					        y = staff.absY() + staff.hsToPixel(0); 
					        break;
					case 2: x += 4 * width; break;
				}
				i++;
				wDuration = wDuration.sub(two);
			}
			g.drawString(getDuration().getBase().getNumer() + "", absX() + ((x - absX()) / 2), staff.absY() - staff.hsToPixel(2) - staff.getLineDistance());
		}
		if (wDuration.equals(Rational.ZERO))
			return;
		
		g.drawString("" + getGlyph(wDuration), x, staff().absY() + staff().hsToPixel(0));
	
		int r = staff.getLineDistance() / 4;
		for (int d = 0; d < getDuration().getDots(); d++) {
			x = absX() + rwidth() + staff.getLineDistance() + d * 3 * r; // @@
			int y = absY() + (height() + depth() / 2);
			g.fillOval(x - r, y - r, 2 * r, 2 * r);
		}
		
		restoreFont(g);
		
		g.setColor(oldColor);
	}

	protected char getGlyph() {
		return getGlyph(getDuration().getBase());
	}
	
	/** Returns the glyph representing the current rest. */
	protected char getGlyph(Rational duration) {
		int denom = duration.getDenom();
		char[] glyphs = {
				MusicGlyph.REST_1,
				MusicGlyph.REST_2,
				MusicGlyph.REST_4,
				MusicGlyph.REST_8,
				MusicGlyph.REST_16,
				MusicGlyph.REST_32,
				MusicGlyph.REST_64 };
		return glyphs[MyMath.ilog2(denom)];
	}

	@Override
	public String toString() {
		return "Rest of voice " + getVoice() + " with duration " + getDuration().toRational();
	}

	@Override
	Pair regressionPoint() {
		return new Pair(absX(), absY());
	}

	@Override
	Pair slurPoint(boolean left, boolean above, boolean atStem) {
		int x, y;
		int top = staff().absY() + staff().hsToPixel(0) - height();
		
		if (above) {
			y = top;
		}
		else {
			y = top + depth() + height();
		}
		int lw = lwidth();
		x = absX() - lw + ((lw + rwidth()) / 2);
		return new Pair(x, y);
	}

	@Override
	Pair beamPoint() {
		return new Pair(absX(), absY()); // @@
	}

	char getFlagGlyph() {
		switch (getDuration().getBase().getDenom()) {
			case 8 :
				return MusicGlyph.FLAG_8_DOWN;
			case 16 :
				return MusicGlyph.FLAG_16_DOWN;
			case 32 :
				return MusicGlyph.FLAG_32_DOWN;
			case 64 :
				return MusicGlyph.FLAG_64_DOWN;
		}
		return 0;
	}

	@Override
	public int extraRSpace() {
		   return MusicGlyph.width(staff().getLineDistance(), getFlagGlyph());
	}
	
	@Override
	public int rwidth() {
		return MusicGlyph.width(staff().getLineDistance(), getGlyph());
	}

	@Override
	public int lwidth() {
		return getLeftPadding();
	}
	
	@Override
	Pair highestPoint() {
		return new Pair(absX() + rwidth() / 2, absY() + height());
	}

	@Override
	Pair lowestPoint() {
		return new Pair(absX() + rwidth() / 2, absY() + depth());
	}

	@Override
	public int depth() {
		return MusicGlyph.depth(staff().getLineDistance(), getGlyph());
	}

	@Override
	public int height() {
		return MusicGlyph.height(staff().getLineDistance(), getGlyph());
	}

	@Override
	public Rational getMetricEndPoint() {
		if (note == null)
			return super.getMetricEndPoint();
		
		return note.getScoreNote().getMetricTime().add(note.getScoreNote().getMetricDuration());
	}

	
	public Note getNote() {
		return note;
	}
	public void setNote(Note note) {
		this.note = note;
	}
}
