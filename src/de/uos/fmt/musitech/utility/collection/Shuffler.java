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
package de.uos.fmt.musitech.utility.collection;/**
 * Implements shuffling a vector.
 * @date (09.12.00 03:54:51)
 * @author TW
 * @version 0.113
 */
public class Shuffler {

	/**
	 * VectorShuffle constructor.
	 */
	private Shuffler() {
		super();
	}

	/**
	 * Shuffles a list.
	 * @date (09.12.00 03:55:56)
	 * @param list List
	 */
	static public void shuffleList(java.util.List<?> list) {
		int i = 0;
		java.util.Random rand = new java.util.Random();
		int max = list.size();
		while (i < max) {
			int j = rand.nextInt(max);
			swap(list, i, j);
			i++;
		}
	}

	/**
	 * Swaps two elements of a list.
	 * @date (09.12.00 03:57:04)
	 * @param list 
	 * @param i 
	 * @param j 
	 */
	@SuppressWarnings("unchecked")
	static public void swap(java.util.List list, int i, int j) {
		Object tmp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, tmp);
	}
	

	/**
	 * Shuffles an array.
	 * @date (09.12.00 03:55:56)
	 * @param array List
	 */
	static public void shuffleArray(Object[] array) {
		int i = 0;
		java.util.Random rand = new java.util.Random();
		int max = array.length;
		while (i < max) {
			int j = rand.nextInt(max);
			swap(array, i, j);
			i++;
		}
	}

	/**
	 * Swaps two elements of an array.
	 * @date (09.12.00 03:57:04)
	 * @param array 
	 * @param i 
	 * @param j 
	 */
	static public void swap(Object[] array, int i, int j) {
		Object tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	/**
	 * Shuffles an array.
	 * @date (09.12.00 03:55:56)
	 * @param array List
	 */
	static public void shuffleArray(int[] array) {
		int i = 0;
		java.util.Random rand = new java.util.Random();
		int max = array.length;
		while (i < max) {
			int j = rand.nextInt(max);
			swap(array, i, j);
			i++;
		}
	}

	/**
	 * Swaps two elements of an array.
	 * @date (09.12.00 03:57:04)
	 * @param array 
	 * @param i 
	 * @param j 
	 */
	static public void swap(int[] array, int i, int j) {
		int tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}


}