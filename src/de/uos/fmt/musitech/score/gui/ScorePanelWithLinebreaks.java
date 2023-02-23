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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * @version 11.08.2008
 */
public class ScorePanelWithLinebreaks extends ScorePanel {

	private int width;

	public ScorePanelWithLinebreaks(NotationSystem nsys) {
		super(nsys);
		setOpaque(true);
		setBackground(Color.WHITE);
	}

	@Override
	public void setNotationSystem(NotationSystem nsys) {
		processRenderingHints(nsys);
		super.setNotationSystem(nsys);
		Score score = getScore();
		if (score.numChildren() > 0 && width > 0) {
			Page p = score.page(0);
			List<Rational> breaks = new ArrayList<Rational>();
			for (int i = 0; i < p.numChildren(); i++) {
				SSystem ssys = (SSystem) p.child(i);
				for (int j = 0; j < ssys.numChildren(); j++) {
					int pos = width;
					Staff st = (Staff) ssys.child(j);
					for (int k = 0; k < st.numChildren(); k++) {
						Measure m = (Measure) st.child(k);
						int end = m.absX() + m.rwidth();
						if (end > pos) {
							pos += width;
							breaks.add(m.attackTime());
						}
					}
				}
			}
			for (Iterator<Rational> i = breaks.iterator(); i.hasNext();) {
				Rational r = i.next();
				if (nsys.getLinebreaks().contains(r))
					i.remove();
				else
					nsys.addLinebreak(r);
			}
			super.setNotationSystem(nsys);
			nsys.getLinebreaks().removeAll(breaks);
		}
	}

	private void processRenderingHints(NotationSystem processSystem) {
		RenderingHints hints = processSystem.getRenderingHints();
		if (hints == null || hints.getValue("barline") == null
			|| !hints.getValue("barline").equals("none")) {
			if (hints != null && hints.getValue("gaps") != null
				&& !hints.getValue("gaps").equals("none"))
				for (int i = 0; i < processSystem.size(); i++) {
					NotationStaff staff = processSystem.get(i);
					for (int j = 0; j < staff.size(); j++) {
						NotationVoice voice = staff.get(j);
						voice.fillGaps();
					}
				}
			processSystem.insertBarlines();
		}
	}

	public void setWidth(int width) {
		this.width = width;
		setNotationSystem(getNotationSystem());
	}
}
