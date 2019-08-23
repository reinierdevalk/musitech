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

import java.util.Comparator;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.utility.math.Rational;


/**
 * Compares two ScorePitches (including ScoreNotes) by their pitch, ignoring enharmonics (i.e. G# == Ab)
 * 
 * @author Tillman Weyde
 *
 */
public class ScorePitchComparator implements Comparator<ScorePitch> {
	
	private ScorePitchComparator(){
		// private constructor to prevent instantiation.
	}
	
	static private ScorePitchComparator instance = new ScorePitchComparator();
	
	/**
	 * Gets a singleton instance of this pitch. 
	 * @return The comparator instance.
	 */
	public static ScorePitchComparator getInstance(){
		return instance;
	}

	/**
	 * Returns the Pitch of two notes using getMidiPitch.  
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ScorePitch o1, ScorePitch o2) {
		int p1, p2;
		p1 = o1.getPerformancePitch();
		p2 = o2.getPerformancePitch();

		if(p1 < p2)
			return -1;
		if(p2 < p1)
			return 1;
		return 0;
	}

}
