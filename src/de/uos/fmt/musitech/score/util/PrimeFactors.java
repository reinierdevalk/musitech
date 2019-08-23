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
package de.uos.fmt.musitech.score.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** This class represents a sequence of ascending prime factors.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public class PrimeFactors {
	private List<Integer> factors = new ArrayList<Integer>();

	/** Constructs a new prime factor sequence by calculating the 
	 * prime factor decomposition of n. */
	public PrimeFactors(int n) {
		if (n < 0)
			throw new IllegalArgumentException("PrimeFactors: n must be non-negative");
		PrimeNumbers primes = new PrimeNumbers(n);
		for (int i = 0; n > 1 && i < primes.size(); i++)
			while (n % primes.get(i) == 0) {
				factors.add(new Integer(primes.get(i)));
				n /= primes.get(i);
			}
	}

	/** Returns the product of all stores factors. */
	public int product() {
		int res = 1;
		for (int i = 0; i < factors.size(); i++)
			res *= get(i);
		return res;
	}

	/** Returns number of stored factors. */
	public int size() {
		return factors.size();
	}

	/** Gets the n-th factor. */
	public int get(int n) {
		return ((Integer) factors.get(n)).intValue();
	}

	/** Returns the biggest factor of this sequence */
	public int max() {
		return get(size() - 1);
	}

	public boolean contains(int p) {
		// @@ we could speed this up by binary search
		for (int i = 0; i < factors.size() && get(i) <= p; i++)
			if (get(i) == p)
				return true;
		return false;
	}

	/** Removes a given prime factor p from the list. 
	 * @return true if p was removed */
	public boolean remove(int p) {
		Iterator it = factors.iterator();
		int n = 0;
		while (it.hasNext() && n <= p) {
			n = ((Integer) it.next()).intValue();
			if (n == p) {
				it.remove();
				return true;
			}
		}
		return false;
	}
}
