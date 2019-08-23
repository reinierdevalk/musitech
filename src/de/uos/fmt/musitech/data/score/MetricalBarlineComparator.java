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
 * File MetricalBarlineComparator.java
 * Created on 11.02.2003
 */
package de.uos.fmt.musitech.data.score;

import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.data.time.MetricalComparator;

/**
 * The purpose of this class is to be able to sort mixed Conatiners 
 * consisting of Barlines and NotationChords. If a NotationChord and 
 * a Barline have the same metrical timestamp, the barline comes first.
 * @author collin
 */
public class MetricalBarlineComparator extends MetricalComparator implements java.io.Serializable {

	/**
	 * This method returns -1 if the first Object is of type Barline, the second Object is of
	 * type NotationChord and the metric timestamp of both is the same.
	 * It returns 1 if the first Object is of type NotationChord, the second Object is of type
	 * Barline and the metric timestamp of both is the same.
	 * In any other case the result of @see MetricalComparator.compare(Object, Object) is returned.
	 */
	@Override
	public int compare(Metrical o1, Metrical o2) {
		if (o1 instanceof Barline
			&& o2 instanceof NotationChord
			&& ((Barline) o1).getMetricTime().isEqual(((NotationChord) o2).getMetricTime())) {
			return -1;
		}

		if (o2 instanceof Barline
			&& o1 instanceof NotationChord
			&& ((Barline) o2).getMetricTime().isEqual(((NotationChord) o1).getMetricTime())) {
			return 1;
		}
		return super.compare(o1, o2);
	}
}
