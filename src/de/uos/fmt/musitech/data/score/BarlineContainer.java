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
package de.uos.fmt.musitech.data.score;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.utility.math.Rational;

import java.util.Iterator;
/**
 * This class is used to contain barlines.
 * It is used inside the class NotationSystem.
 * @author collin
 * @version 1.0
 * @see de.uos.fmt.musitech.data.score.NotationSystem
 * @see de.uos.fmt.musitech.data.score.Barline
 * 
 * @hibernate.class table = "BarlineContainer"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 * 
 */
public class BarlineContainer extends SortedContainer<Barline> {

	/**
	 * This constructor calls the super constrcutor. It makes sure that only Barlines are
	 * allowed inside of this container. The Comparator used is the 
	 * @see de.uos.fmt.musitech.data.structure.MetricalComparator.
	 */
	public BarlineContainer(Context context) {
		super(context, Barline.class, new MetricalComparator());
	}

	/**
	 * This constructor calls the super constrcutor. It makes sure that only Barlines are
	 * allowed inside of this container. The Comparator used is the 
	 * @see de.uos.fmt.musitech.data.structure.MetricalComparator.
	 */
	public BarlineContainer() {
		super(Context.getDefaultContext(), Barline.class, new MetricalComparator());
	}

	/**
	 * This method checks if a barline exists at a specified point in metric time
	 * @param metricTime the point in metric time
	 * @return true if there is a barline at the specified point
	 */
	public Barline getBarlineAt(Rational metricTime) {
		for (Iterator iter = iterator(); iter.hasNext();) {
			Barline barline = (Barline) iter.next();
			if (barline.getMetricTime().isEqual(metricTime)) {
				return barline;
			}
			if (barline.getMetricTime().isGreater(metricTime)) {
				return null;
			}
		}
		return null;
	}
	
	public boolean hasBarlineAt(Rational metricTime) {
		return getBarlineAt(metricTime) != null;
	}
	
	/**
	 * This method checks if there's a barline between two points in metric time.
	 * Between means (in this case) excluding the first point, but including the second:
	 * ]metricTimeStart, metricTimeEnd].
	 * If there's more than one barline between the two points the last one is returned.
	 * @param metricTimeStart
	 * @param metricTimeEnd
	 * @return The barline between the two parameters or null if there is none.
	 */
	public Barline getBarlineBetween(Rational metricTimeStart, Rational metricTimeEnd) {
		for (Iterator iter = iterator(); iter.hasNext();) {
			Barline barline = (Barline) iter.next();
			if (barline.getMetricTime().isGreater(metricTimeStart) &&
				barline.getMetricTime().isLessOrEqual(metricTimeEnd)) {
				/* make sure we are returning the last barline between the two points: */
				while (iter.hasNext()) {
					Barline nextBarline = (Barline)iter.next();
					if (nextBarline.getMetricTime().isLessOrEqual(metricTimeEnd)) {
						barline = nextBarline;
						break;
					}
					if (nextBarline.getMetricTime().isGreater(metricTimeEnd)) {
						break;
					}
				}
				return barline;
			}
			if (barline.getMetricTime().isGreater(metricTimeEnd)) {
				return null;
			}
		}
		return null;		
	}

	/**
	 * This method adds a barline to the container, if there isn't one already
	 * at the barlines point of metric time. This makes it possible to go over
	 * different voices and call addBarline, but only get one set of barlines.
	 * @param b the barline to be added
	 */
	public boolean add(Barline b) {
		if (!hasBarlineAt(b.getMetricTime())) {
			return super.add(b);
		}
		else 
			return false;
	}
}
