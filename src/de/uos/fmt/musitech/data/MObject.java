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

package de.uos.fmt.musitech.data;

/**
 * Interface for musical data objects.
 * 
 * @author TW / MG
 * @version 0.116
 * @hibernate.class
 */
public interface MObject extends java.io.Serializable
{

	/**
	 * Return the Unique ID of this Object
	 * @return the Unique ID
	 * @hibernate.id generator-class="native"
	 */
	public Long getUid();

	/**
	 * Set the Unique ID of this MObject
	 * 
	 * @param uid
	 */
	public void setUid( Long uid );

	/**
	 * Returns false, if the specified Object is not a possible value of the property indicated by
	 * the specified <code>propertyName</code>, true otherwise. (For properties being primitives,
	 * the primitive value of the specified Object must be checked.) <br>
	 * This method is called by editors to check user input before applying it to the edited MObject.
	 * E.g. the <code>diatonic</code> of a <code>ScoreNote</code> should not be other than 'c',
	 * 'd', 'e', 'f', 'g', 'a' or 'b'. So, in addition to the input's type (char), the value is
	 * checked using this method.
	 * 
	 * @param propertyName
	 *           String indicating the property for which the specified value should be checked
	 * @param value
	 *           Object to be checked if a possible value for the indicated property
	 * @return false if the Object does not represent a possible value of the property given by
	 *         <code>propertyName</code>, true otherwise
	 */
	public boolean isValidValue( String propertyName, Object value );

}