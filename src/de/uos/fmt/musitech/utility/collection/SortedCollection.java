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
package de.uos.fmt.musitech.utility.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This is a typed Collection with sorted elements. The type must 
 * must be provided at creation. If the type is not a Comparable 
 * a comparator must also be provided.
 * @see de.uos.fmt.musitech.data.structure.TypedCollection 
 * @version $Revision: 8311 $, $Date: 2012-10-31 12:32:57 +0100 (Wed, 31 Oct 2012) $
 * @author Tillman Weyde
 */
public class SortedCollection<T> extends TypedCollection<T> {

	/**
	 * Inner class: A default Comparator, that compares only Comparables.
	 * For anything else 0 is returned.
	 */
	class DefaultComparator implements Comparator {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			if (o1 instanceof Comparable && o2 instanceof Comparable)
				return ((Comparable) o1).compareTo(o2);
			return 0;
		}
	}

	/**
	 * The comparator of this Collection.
	 */
	protected Comparator comp;

	/**
	 * Constructor 
	 * @param type Class to be accepted by this Collection.
	 * @param comp Comparator must work for type.
	
	 */
	public SortedCollection(Comparator comp) {
		super(new Class[]{});
		this.comp = comp;
	}

	/**
	 * Constructor 
	 * @param type Class to be accepted by this Collection.
	 * @param comp Comparator must work for type.
	
	 */
	public SortedCollection(Class type, Comparator comp) {
		super(type);
		this.comp = comp;
	}

	/**
	 * Constructor
	 * @param type Classes that will be accepted.
	 * @param comp Comparator must work for all Classes of Parameter types[].
	 */
	public SortedCollection(Class types[], Comparator comp) {
		super(types);
		this.comp = comp;
	}

	/**
	 * Constructor. The default comparator will be used.
	 * @param type Must be a Comparable.
	 */
	public SortedCollection(Class type) throws IllegalArgumentException {
		super(type);
		if (!Comparable.class.isAssignableFrom(type))
			throw new IllegalArgumentException(
				"Tpye for " + this.getClass().getName() + " must implement java.lang.Comparable");
		comp = new DefaultComparator();
	}

	/**
	 * Constructor. The default comparator will be used.
	 * @param type Must be Comparables.
	 */
	protected SortedCollection(Class types[]) throws IllegalArgumentException {
		super(types);
		for (int i = 0; i < types.length; i++) {
			Class type = types[i];
			if (!Comparable.class.isAssignableFrom(type))
				throw new IllegalArgumentException(
					"Tpye for "
						+ this.getClass().getName()
						+ " must implement java.lang.Comparable");
		}
		comp = new DefaultComparator();
	}

	/**
	 * This find method searches a collection sorted by metric time for the object 
	 * at <code>metricTime</code>, if there is one; 
	 * otherwise, (-(insertion point) - 1). 
	 * The insertion point is defined as the point at which an object with <code>metricTime</code> 
	 * would be inserted into the list: the index of the first element greater than the key, or 
	 *  list.size(), if all elements in the list are less than the specified key. 
	 *  Note that this guarantees that the return value will be >= 0 if and only if 
	 *  there is an element at <code>metricTime</code>.
	 * @param obj
	 * @return int  
	 */
	public int find(final Rational metricTime) throws IllegalArgumentException {
		Metrical metrical = new Metrical(){
			public Rational getMetricTime() {
				return metricTime;
			}
			public Rational getMetricDuration() {
				return Rational.ZERO;
			}
		};
	    return Collections.binarySearch(list, metrical, comp);
	}

	/**
	 * Method find searches by Comparator, in case one
	 * of several objects redarded equal by the comparator or 
	 * by their 'natural order' resp (see java.lang.Comparable)
	 * is is indetermined which of the elements will be returned.
	 * For an search by equality of object references use <code>indexOf()</code>.
	 * @param obj
	 * @return int
	 */
	public int find(Object obj) throws IllegalArgumentException {
	    return Collections.binarySearch(list, obj, comp);
	}

	/**
	 * Method findFirst searches by Comparator, gives the first of possibly 
	 * several objects which are equal by comparator.compare(). If no equal 
	 * element is found, the position at which the element would have been 
	 * inserted is calculated and ((insert_position * -1) - 1) is returned.
	 * @param obj The object to be searched. Must be of the type required by this collection's comparator.
	 * @return int The position of the first matching object, if not found returns (insert_position * -1) - 1.
	 * @throws IllegalArgumentException if the argument is not of a type expected by this Collection.
	 */
	public int findFirst(Object obj) throws IllegalArgumentException {
	    try{
		// use bin. search
		int pos = find(obj);
		Object elem;
		if (pos > 0) {
			do {
				pos--;
				elem = list.get(pos);
			} while (pos > 0 && comp.compare(elem, obj) == 0);
			if (comp.compare(elem, obj) != 0)
				pos++;
		}
		return pos;
	    }catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
	}

	/**
	 * Method findAll searches by Comparator, gives the range of 'equal' 
	 * objects.
	 * @param obj The object to be looked for. Must be of appropriate type for this collection.
	 * @return int[] Array containing the first and the last index of objects
	 * 'equal' to the obj argument. If not found, an array of length 1 is
	 * returned containing (insertion position * -1) - 1.
	 * @throws IllegalArgumentException if this is not the required type.
	 */
	public int[] findAll(Object obj) throws IllegalArgumentException {
		typeCheck(obj);
		int pos = find(obj);
		Object elem;
		if (pos < 0)
			return new int[] { pos };
		// (pos >=0 )
		int pos1 = pos;
		//shift the first index of the range until the metric time is different
		while (pos1 - 1 >= 0 && comp.compare(obj, list.get(pos1 - 1)) == 0)
			pos1--;
		int pos2 = pos;
		int size = list.size();
		//shift the last index of the range until the metric time is different
		if (pos2 > 0 && pos2 < size)
			while (pos2 + 1 < size && comp.compare(obj, list.get(pos2 + 1)) == 0)
				pos2++;
		return new int[] { pos1, pos2 };
	}

	/**
	 * Implements indexOf making use of the element order.
	 * @see java.util.List#indexOf(Object)
	 */
	public int indexOf(Object obj) {
		int pos = find(obj);
		if (pos >= 0) {
			int pos1 = pos;
			Object elem = list.get(pos1);
			// go looking at the lower indices
			while (pos1 - 1 >= 0 && obj != elem && comp.compare(obj, elem) == 0) {
				pos1--;
				elem = list.get(pos1);
			}
			// if we found the object return
			if (obj == elem) {
				return pos1;
			} else if (pos < list.size() - 1) {
				// else look at the higher indices
				int pos2 = pos+1;
				int size_1 = list.size()-1;
				elem = list.get(pos2);
				while (pos2 < size_1 && obj != elem && comp.compare(obj, elem) == 0){
					pos2++;
					elem = list.get(pos2);
				}
				if (obj == elem) {
					pos2--;
					return pos2;
				}
			}
		}
		return -1;
	}

	/**
	 * This has the same effect as 'add(Object)' 
	 * but works faster if the added element's position after sorting is  
	 * at the end of the list. Adds only non-null objects 
	 * that are not yet contained in the collection.
	 * @param obj the object
	 * @return boolean true if added
	 * @throws IllegalArgumentException or NullPointerException
	 * @see #add(T)
	 */
	public boolean append(T obj) {
		typeCheck(obj);
		if (obj == null)
			return false;
		if (list.contains(obj))
			return false;
		if(list.size()==0){
			add(obj);
			return true;
		}
		T lastObj = list.get(list.size() - 1);
		// Test if the new object can be inserted at the last position,
		if (comp.compare(lastObj, obj) <= 0)
			// if so, no searching is necessary 
			 super.add( list.size(), obj );
		else
			// else we use the normal way
			add(obj);
		assert testOrder();
		return true;
	}

	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(T obj) {
		typeCheck(obj);
		int pos = find(obj);
		if (pos < 0)
			pos = -pos - 1;
		super.add(pos, obj);
		if(!testOrder()){
			String msg = "Order corrupted in SortedCollection add(Object)!";
			System.out.println(msg);
			assert false:msg;
		}
		return true;
	}

	/**
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<T> c) {
		Iterator<T> iter = c.iterator();
		T obj;

		while (iter.hasNext()) {
			obj = iter.next();
			add(obj);
		}
		assert testOrder();
		return true;
	}

	/**
	 * Add at a special position could destroy the order so it's not allowed. Please use
	 * @see add(Object obj)
	 */
	public void add(int pos, Object obj) {
		throw new UnsupportedOperationException("SortedColletion - add(pos,obj) not supported.");
	}

	/**
	 * Add at a special position could destroy the order so it's not allowed. Please use
	 * 	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c) {
		throw new UnsupportedOperationException("SortedColletion - add(pos,obj) not supported.");
	}

	/**
	 * Tests the order of the SortedContainer
	 * @return boolean true if the order is correct
	 */
	private boolean testOrder() {
		Object array[] = list.toArray();
		for (int i = 0; i < array.length - 1; i++) {
			if (comp.compare(array[i], array[i + 1]) > 0) {
				System.out.println(
					getClass() + ".testOrder() failed at positions " + i + "," + (i + 1) + ".");
				System.out.println(array[i] + ", " + array[i + 1]);
				return false;
			}
		}
		return true;
	}

}
