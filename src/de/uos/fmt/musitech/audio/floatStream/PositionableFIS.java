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
 * Created on 23.04.2003
 */
package de.uos.fmt.musitech.audio.floatStream;

import java.io.IOException;

/**
 * Guarantees  the capabilities to mark positions, and give all informations important
 * to set the marks correctly.
 * Tree kinds of positions are marked:
 * 	   -  The position at that read(...) will begin to read (it will be good, if read(...) set position by the number of data read)
 * 	   -  An position at that reset will go back
 * 	   -  an virtual end-position; read(...) have not to read more data then (endPosition - position). If end-position was reached, read(...) have to return -1. 
 * available() from FloatInputStream have to return (endPosition - position).
 * The number of data realy available at total (the position, at that the end-position can be setted) is returned by totalAvailable().
 * @author Nicolai Strauch
 */
public interface PositionableFIS extends FloatInputStream
{
	
	/**
	 * Set the position (in frames) in File, in samples, at that read(...) will begin
	 * to extract data, by the next invocation.
	 * @param n - new position in samples
	 * @throws IOException
	 */
	public void position(int n) throws IOException;
	
	/**
	 * Get the actual read-position (in frames) in the data.
	 * @return long - the aktual position, in samples, at that read(...) will begin
	 * to extract data, by the next invocation.
	 */
	public int position();

	/**
	 * Optional - return null if not implemented.
	 * Get a FloatPreviewReader that read from the ByteBuffer that allways is source
	 * of the PositionableFISimplementation
	 * @return
	 */
	public FloatPreviewReader getFloatPreviewReader();
}
