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

/**
 * A Comparator comparing objects implementing the <code>Timed</code> interface.
 * @author Tillman Weyde
 */
public class TimedComparator implements java.util.Comparator, java.io.Serializable {

	/**
	 * Compares its two arguments for order. Returns a negative integer, zero,
	 * or a positive integer as the first argument is less than, equal to, or
	 * greater than the second.
	 * <p>
	 * @return a negative integer, zero, or a positive integer if the first
	 *         argument is less than, equal to, or greater than the second.
	 * @throws ClassCastException if the arguments are not Timed.
	 */
	public int compare(Object o1, Object o2) {
		long t1 = ((Timed) o1).getTime();
		long t2 = ((Timed) o2).getTime();
		if (t1 -t2 < 0)
			return -1;
		else if ( t1 - t2 > 0)
			return 1;
		else
			return 0;
	}
	
	// one static instance for reuse
	private static TimedComparator instance = new TimedComparator();
	
	
	/**
	 * Gets a instance of this class for reuse, to reduce the number
	 * of created objects. 
	 * @return
	 */
	public static TimedComparator getInstance(){
		return instance;
	}

}