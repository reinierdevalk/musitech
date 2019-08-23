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

package de.uos.fmt.musitech.audio.floatStream;

/**
 * 
 * All implementing classes receive data from another FloatInputStream.
 * That FloatInputStream can be set with this Methods.
 * <br>
 * <br>
 * Classes implementing this interface can form a sequence. 
 * With this methods, it is possible to add FloatInputStreams to the sequence,
 * and to delete FloatInputStreams.
 * <br>
 * <br>
 * Example of adding:
 * <ul> 
 * 	<li>a FloatInputStream "c" reads from the FloatInputStream "b".<br>
 * 	to add the FloatInputStream "b" the code will be:
 * 		<code>FloatInputStream tmp = c.getFloatInputStream();	// that's "a"
 * 		<br>b.setFloatInputStream(tmp);
 * 		<br>c.setFloatInputStream(b);
 * 		</code>
 *  now "c" reads from "b" and "b" reads from "a".
 *  *  To delete "b":
 * 		FloatInputStream tmp = c.getFloatInputStream();	// that's "b"
 * 		FloatInputStream tmp1 = tmp.getFloatInputStream(); // that's "a"
 * 		c.setFloatInputStream(tmp1);	// include "tmp1", that's "a", into "c"
 *  now "c" reads from "a", "b" is "tmp"
 * </ul>
 *  Maybe any class that change the AudioFormat of the audiodata is a FISReader.
 *  In this case, be careful whith classes that were initialized by the actual AudioFormat
 *  before the format converting FISReader was added or deleted.
 *  For example a SourceDataLine. 
 * 
 *  Every FISReader must have an default-constructor to generate Classes at runtime.
 * 
 * @author Nicolai Strauch
 */
public interface FISReader {

	/**
	 * Set the FloatInputStream, this reader reads from.
	 * @param fis The FIS to read from
	 * @return This FISReader.
	 */
	public FISReader setFloatInputStream(FloatInputStream fis);

	/**
	 * @return Get the FloatInputStream this reader is reading from. 
	 */
	public FloatInputStream getFloatInputStream();

}
