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
/**
 * 
 */
package de.uos.fmt.musitech.data.structure.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * @version 07.03.2008
 */
public class Selection extends BasicContainer<Note> {

	public static enum SelectionType {
		MEASURE, CHORD, NOTE
	};

	private SelectionType type;
	private List<Object> annotations;

	public Selection(Context context) {
		super(context, Note.class);
		annotations = new ArrayList<Object>();
		type = SelectionType.NOTE;
	}

	public SelectionType getType() {
		return type;
	}

	public void setType(SelectionType type) {
		this.type = type;
	}

	public List<Object> getAnnotations() {
		return annotations;
	}

	@SuppressWarnings("unchecked")
	public Container<Note> getAllNotes() {
		if (type == SelectionType.CHORD || type == SelectionType.NOTE)
			return this;
		Container<Note> notes = new BasicContainer<Note>(getContext(),
			Note.class);
		Piece p = getContext().getPiece();
		MetricalTimeLine mtl = p.getMetricalTimeLine();
		for (Iterator<Note> i = iterator(); i.hasNext();) {
			Note n = i.next();
			Rational start = mtl.getPreviousOrSameMeasure(n.getMetricTime());
			Rational end = mtl.getNextMeasure(n.getMetricTime());
			NotationSystem nsys = p.getScore();
			for (Iterator<NotationStaff> j = nsys.iterator(); j.hasNext();) {
				NotationStaff nst = j.next();
				for (Iterator<NotationVoice> k = nst.iterator(); k.hasNext();) {
					NotationVoice nv = k.next();
					for (Iterator<NotationChord> l = nv.iterator(); l.hasNext();) {
						NotationChord nc = l.next();
						if (nc.getMetricTime().isGreaterOrEqual(start)
							&& nc.getMetricTime().isLess(end))
							notes.addAll(nc);
					}
				}
			}
		}
		return notes;
	}
}
