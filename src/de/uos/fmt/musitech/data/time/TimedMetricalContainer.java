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
/*
 * Created on 30.04.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.uos.fmt.musitech.data.time;

import de.uos.fmt.musitech.data.structure.*;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;

/**
 * This is a container that contains Timed elements in sorted order.
 * @author FX & Tillman
 * @version $Revision: 8548 $, $Date: 2013-09-02 00:54:49 +0200 (Mon, 02 Sep 2013) $
 * 
 * @hibernate.class table = "TimedMetricalContainer"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 * 
 */
public class TimedMetricalContainer extends SortedContainer<TimedMetrical> {

	public TimedMetricalContainer(Piece piece){
		super(piece.getContext(), new TimedComparator());
		//	super(null, TimedMetrical.class, new TimedComparator());
	}
	
	public TimedMetrical getLastElementNotAfter(long timed){
		
		Timed t = new LyricsSyllable(
							timed,
							"[dummy]"
						);
		
		int position;
		int i = findFirst(t);
		if (i<0){
			position = i*(-1)-2;
		} else {
			position = i;
		}

		if (position < 0) return null;

		return get(position);
			
	}
	
	public static TimedMetricalContainer generateTestLyrics1(){
		String[] s = new String[]{
			"das",
			"wan-",
			"dern",
			"ist",
			"des",
			"Mül-",
			"lers",
			"Lust"
		};
		
		TimedMetricalContainer l = new TimedMetricalContainer(Piece.getDefaultPiece());
		
		for (int i = 0; i < s.length; i++) {
			l.add(new LyricsSyllable(
				i*500,
				"["+i*500+s[i]+"]"
			));
		}
		
		return l;
	}
	
	public static TimedMetricalContainer generateTestLyrics2(){
		String[] s = new String[]{
			"häns-",
			"chen",
			"kleinganzlangesilbeganzlangesilbeganzlangesilbeganzlangesilbe",
			"ging",
			"allein",
			"in",
			"die",
			"weite"
		};
		
		TimedMetricalContainer l = new TimedMetricalContainer(Piece.getDefaultPiece());
		
		for (int i = 0; i < s.length; i++) {
			l.add(new LyricsSyllable(
				i*200,
				"["+i*200+s[i]+"]"
			));
		}
		
		return l;
	}
	
	public static void main(String[] args) {
		TimedMetricalContainer l = generateTestLyrics2();
		LyricsSyllable ls = (LyricsSyllable)
			l.getLastElementNotAfter(564);
		
		System.out.println("\""+ls.getText() + "\" time:"+ls.getTime());
	}
}
