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
package de.uos.fmt.musitech.utility.obj;

import com.sun.org.apache.xml.internal.utils.XMLChar;

/**
 * This class en/decodes arbitrary strings to/from valid XML element names.
 * 
 * @author Tillman Weyde
 * @version 
 */
public class XMLNameCoder {

	/**
	 * Creates a valid XML name from a raw string. Valid characters are not
	 * changed, apart from the ':' character which is doubled. Invalid
	 * characters are coded using valid characters.
	 * 
	 * @param raw The raw string.
	 * @return The encoded valid name string.
	 */
	static public String encode(String raw) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			if (c == ':') {
				sb.append("::");
			} else if (i == 0 && !XMLChar.isNameStart(c)) {
				sb.append(':' + Integer.toString(c) + '_');
			} else if (i > 0 && !XMLChar.isName(c)) {
				sb.append(':' + Integer.toString(c) + '_');
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Creates raw string from an encoded valid XML name. Only characters
	 * following a ':' are changed, expecting them to encode a number in the
	 * form <code>:[0-9]*_</code>.
	 * 
	 * @param coded The encoded valid name string.
	 * @return The raw string.
	 */
	static public String decode(String coded) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < coded.length(); i++) {
			char c = coded.charAt(i);
			if (c == ':') {
				if (coded.length() <= i + 1 || coded.charAt(i + 1) == ':') {
					sb.append(':');
					i++;
				} else {
					StringBuffer sb2 = new StringBuffer();
					for (i++; i < coded.length() && coded.charAt(i) != '_'; i++) {
						sb2.append(coded.charAt(i));
					}
					char rawChar = (char) Integer.parseInt(sb2.toString());
					sb.append(rawChar);
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
