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
package de.uos.fmt.musitech.data.time;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.time.TimeStamp;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 *
 * @hibernate.class table="TimedMetrical"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 * 
 */

public class TimedMetrical extends BasicTimedObject implements Marker, MObject {

	Rational metricTime;
	Rational metricDuration;


	public TimedMetrical(long time, Rational metricTime) {
		super(time);
		this.metricTime = metricTime;
	}

	/**
	 * Returns the metricTime.
	 * @returns the metric time
	 * 
     * @hibernate.many-to-one name = "metricTime"
     * class = "de.uos.fmt.musitech.utility.math.Rational"
     * foreign-key = "uid"
     * cascade = "all"
	 *
	 */
	public Rational getMetricTime() {
		return metricTime;
	}

	/**
	 * Sets the metricTime.
	 * @param metricTime The metricTime to set
	 */
	public void setMetricTime(Rational metricTime) {
		this.metricTime = metricTime;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TimedMetrical at ");
		sb.append(metricTime);
		sb.append(" (metricTime): ");
		sb.append(TimeStamp.toStringFormated(getTime()));
		return sb.toString();
	}

	public static void main(String[] arguments) {
		Piece work = new Piece();
		Context context = new Context(work);
		MetricalTimeLine mt = new MetricalTimeLine(context);
		// Add some TimedMetrical and TimeSignatureMarker
		System.out.println("Adding TimedMetrical at 0");
		mt.add(new TimedMetrical(1, new Rational(1)));
		System.out.println("Adding TimedMetrical at 3/2");
		mt.add(new TimedMetrical(1245, new Rational(3, 2)));
		System.out.println("Adding TimeSignatureMarker at 3/2");
		mt.add(new TimeSignatureMarker(4, 4, new Rational(3, 2)));
		System.out.println("Adding TimeSignatureMarker at 19/4");
		mt.add(new TimeSignatureMarker(3, 4, new Rational(19, 4)));
		System.out.println("Adding TimedMetrical at 19/4");
		mt.add(new TimedMetrical(65433, new Rational(19, 4)));

		System.out.println(mt);
		//System.out.println(mt.getTimeStamp(new Rational(19, 4)));
	}

	/**
	 * TODO add comment
	 * @see de.uos.fmt.musitech.data.time.Metrical#getMetricDuration()
	 * 
     * @hibernate.many-to-one name = "metricDuration"
     * class = "de.uos.fmt.musitech.utility.math.Rational"
     * foreign-key = "id"
     * cascade = "all"
	 * 
	 */
	public Rational getMetricDuration() {
		return metricDuration;
	}
	/**
	 * @param metricDuration The metricDuration to set.
	 */
	public void setMetricDuration(Rational metricDuration) {
		this.metricDuration = metricDuration;
	}

	public TimedMetrical() {
		super();
	}
}