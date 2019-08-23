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
package de.uos.fmt.musitech.score.mpegsmr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.score.mpegsmr.Attributes;
import de.uos.fmt.musitech.score.mpegsmr.Elements;

/**
 * @author Sascha Wegener
 * 
 * @version 10.03.2008
 */
public class AddressMapExport implements Elements,Attributes{

	private Map<Note, int[]> map;

	public AddressMapExport() {
		map = new HashMap<Note, int[]>();
	}
	
	public void put(Note n, int measure, int layer, int figure,
			int chordOrBeam, int chordInBeam) {
		if (n != null){
			map.put(n, new int[] { measure, layer, figure, chordOrBeam,
					chordInBeam });
//			System.out.printf("%2d,%2d,%2d,%2d,%2d -> %s%n",measure, layer, figure, chordOrBeam,
//					chordInBeam,n);
		}
	}

	public void put(Note n, int measure, int layer, int figure, int chordOrBeam) {
		put(n, measure, layer, figure, chordOrBeam, 0);
	}

	public void put(Note n, int measure, int layer, int figure) {
		put(n, measure, layer, figure, 0);
	}

	public int[] get(Note n) {
		if (!map.containsKey(n))
			System.err.println(n.toString() + " not found");
		return map.get(n);
	}

	public Element getAddress(Note n) {
		int[] v = get(n);
		Element a = new Element(ADDRESS);
		a.setAttribute(ADDRESS_MEASURE,Integer.toString(v[0]));
		a.setAttribute(ADDRESS_LAYER,Integer.toString(v[1]));
		a.setAttribute(ADDRESS_FIGURE,Integer.toString(v[2]));
		a.setAttribute(ADDRESS_CHORD_OR_BEAM,Integer.toString(v[
		                                                  3]));
		a.setAttribute(ADDRESS_CHORD_IN_BEAM,Integer.toString(v[4]));
		return a;
	}
	
	@SuppressWarnings("unchecked")
	public Element getExtAddress(Note n,int part){
		Element ea = getAddress(n);
		ea.setName(SMXF_MAIN_EXTADDRESS);
		List<Attribute> al = ea.getAttributes();
		al.add(0, new Attribute(SMXF_MAIN_EXTADDRESS_PART, Integer.toString(part)));
		return ea;
	}

	public boolean contains(Note n) {
		return map.containsKey(n);
	}
}
