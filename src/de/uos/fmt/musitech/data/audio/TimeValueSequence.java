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
package de.uos.fmt.musitech.data.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.uos.fmt.musitech.utility.math.MyMath;

/**
 * A class for values, that change over time. It contains time stamps and
 * associated values. 
 * 
 * @author Tillman Weyde
 */
public class TimeValueSequence {

	ArrayList timeList = new ArrayList();
	ArrayList valueList = new ArrayList();
	
	int size() {
		return timeList.size()/2;
	}
	
	public float value(int pos) {
		return ((Float)valueList.get(pos)).floatValue();
	}
	
	public long time(int pos) {
		return ((Long)timeList.get(pos)).longValue();
	}
	
	
	int findTime(long argTime) {
		return Collections.binarySearch(timeList,new Long(argTime));
	}
	
	/**
	 * Adds a time-value pair. If the time is already present, it is overwritten;
	 * if not, a new entry is added to  the list.
	 *  
	 * @param argTime The time stamp for the new value.
	 * @param argVal The value to set at the time stamp.
	 * @return The position of the time stamp if it was 
	 * 		already present; (-position)-1 if the time stamp has bee inserted. 
	 */
	public int addValue(long argTime, float argVal){
		int pos = findTime(argTime);
		if(pos>=0) {
			timeList.set(pos,new Long(argTime));
			valueList.set(pos,new Float(argVal));
		} else {
			timeList.add(-pos-1,new Long(argTime));
			valueList.add(-pos-1,new Float(argVal));
		}
		return pos;
	}
	
	public float valueAtTime(long argTime){
		return getInterploator().getValue(argTime);
	}

	Interploator interploator;

	private Interploator getInterploator() {
		if(interploator == null)
			interploator = new LinearInterpolator(this);
		return null;
	}

}
