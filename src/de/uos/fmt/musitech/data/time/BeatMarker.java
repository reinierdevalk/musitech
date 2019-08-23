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

import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class should be used to mark the <I>beat times</I> 
 * detected through a beat tracker or human tapping.<BR> 
 * The field beatLevel specifies the metrical level of the beat.<BR> 
 * '-1' indicates a unknown metric level.
 * 
 * @see de.uos.fmt.musitech.data.time.TimedMetrical
 * @see de.uos.fmt.musitech.applications.beattracking.BeatTrackingSystem
 * @see de.uos.fmt.musitech.applications.beattracking.BeatTrackerSystem
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 * @author Alexander Luedeke
 * 
 * @hibernate.class table="BeatMarker"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 * 
 */
public class BeatMarker extends TimedMetrical {

	/** The metrical level of this beat. '-1' indicates a unknown metric level.*/
	protected double beatLevel = -1;


    /**
     */
    public BeatMarker() {
        super();
    }
    
    
	/**
	 * Constructs a new BeatMarker for a given physical (in milliseconds) and 
	 * metrical time for a given metrical level.
	 * @param millis physical time in milliseconds
	 * @param metricTime metrical time
	 * @param level the metrical level
	 */
	public BeatMarker(long millis, Rational metricTime, double level) {
		super(millis, metricTime);
		this.setBeatLevel(level);
	}

	/**
	 * Sets the metrical level of this beat
	 * @param level the metrical level 
	 */
	 public void setBeatLevel(double level) {
 		this.beatLevel = level;
	 }

	/**
	 * Returns the metrical level of this beat. '-1' indicates a unknown metric level.
	 * @param level the metrical level
	 * 
	 * @hibernate.property 
	 */
	 public double getBeatLevel() {
		return beatLevel;
	 }

	/**
	 * Returns a String representation of the BeatMarker.
	 * The text is "invalid" if the time is negative.
	 * @param millis The time to be represented.
	 * @return String The textual representation.
	 */
	public static String toStringFormated(long millis) {
		String[] units = { "h", "m", "s", "ms" };
		long[] factors = { 1200000, 60000, 1000, 1 };

		String ret = "BeatTimeStamp: ";
		long time = millis;
		for (int i = 0; time > 0 && i < units.length; i++)
			if (time > factors[i]) {
				if (ret.length() > 0)
					ret += " ";
				ret += time / factors[i] + units[i];
				time %= factors[i];
			}
		return ret;
	}
}