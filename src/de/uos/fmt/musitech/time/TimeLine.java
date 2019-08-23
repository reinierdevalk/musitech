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
package de.uos.fmt.musitech.time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.BasicTimedObject;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.time.TimedComparator;

/**
 * MTimeLine contains MTimedObjects in temporal order. 
 * Implements java.util.Collection accepting only TimedObjects
 * @author TW / MG
 * @version 0.114
 * 
 * @hibernate.class table = "TimeLine"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 * 
 */
public class TimeLine extends SortedContainer<Timed> {

	String name;
	/**
	 * Gets the name.
	 * @return java.lang.String
	 * 
	 * @hibernate.property
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * @param name java.lang.String
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}
	
	public TimeLine(){
		this(null);
	}

	public TimeLine(Context context){
		super(context,Timed.class,new TimedComparator()); // XXX evtl temporäre TimeLine zulassen ?
	}

	/**
	 * Index of an object (may be not the fist) with this timestamp, if it is
	 * contained in the timeline; otherwise, (-(insertion point) - 1). The
	 * insertion point is defined as the point at which the TimeStamp would be
	 * inserted into the list: the index of the first element greater than the
	 * TimeStamp, or TimeLine. size(), if all elements in the list are less than
	 * the specified TimeStamp. Note that this guarantees that the return value
	 * will be >= 0 if and only if the TimeStamp is found.
	 * @param ts The TimeStamp.
	 * @return int The index.
	 * @see findFirst(TimeStamp) 
	 */
	public int find(long ts) {
		Timed to = new BasicTimedObject(ts);
		return super.find(to);
	}

	/**
	 * Finds the first object with this timestamp.
	 * @return int
	 * @param timeStamp de.uos.fmt.musitech.data.structure.TimeStamp
	 */
	public int findFirst(long time) {
		Timed to = new BasicTimedObject(time);
		return super.findFirst(to);
	}

	/**
	 * Adds the Timed.
	 * @param to de.uos.fmt.musitech.data.structure.Timed
	 * @see java.util.Collection#add(Object)
	 */
	public synchronized boolean add(Timed o) throws ClassCastException {
		if (o == null)
			return false;
		Timed to = (Timed) o;
		return super.add(to);
	}

	/**
	 * Adds all of the elements in the specified collection to this collection
	 * (optional operation).  The behavior of this operation is undefined if
	 * the specified collection is modified while the operation is in progress.
	 * (This implies that the behavior of this call is undefined if the
	 * specified collection is this collection, and this collection is
	 * nonempty.)
	 *
	 * @param c elements to be inserted into this collection.
	 * @return <tt>true</tt> if this collection changed as a result of the
	 *         call
	 *
	 * @throws UnsupportedOperationException if this collection does not
	 *         support the <tt>addAll</tt> method.
	 * @throws ClassCastException if the class of an element of the specified
	 * 	       collection prevents it from being added to this collection.
	 * @throws IllegalArgumentException some aspect of an element of the
	 *	       specified collection prevents it from being added to this
	 *	       collection.
	 *
	 * @see #add(Object)
	 */
	public synchronized boolean addAll(java.util.Collection<Timed> c) throws ClassCastException {
		boolean retVal = false;
		Iterator<Timed> iter = c.iterator();
		while (iter.hasNext()) {
			retVal |= add(iter.next());
		}
		return retVal;
	}

	/**
	 * Finds all elements with the given TimeStamp. 
	 * @return de.uos.fmt.musitech.data.structure.Timed[]
	 * @param mts de.uos.fmt.musitech.data.structure.TimeStamp
	 */
	public int[] findAll(long ts) {
		Timed to = new BasicTimedObject(ts);
		return super.findAll(to);
	}

	/**
	 * Gets the index range from TimsStamp1 (inclusive) to TimeStamp2
	 * (exclusive).
	 * @return int[] Array of two ints. [0] ist the first element with tm1 or
	 * greater, [1] is the last element with ts2 of less.
	 * @param ts1 de.uos.fmt.musitech.data.structure.TimeStamp the first TimeStamp
	 * @param ts2 de.uos.fmt.musitech.data.structure.TimeStamp the second TimeStamp
	 */
	public int[] getRange(long ts1, long ts2) {
		if(ts1 < ts2){
			long tmp = ts1;
			ts1 = ts2;
			ts2 = tmp;
		}
		int pos1 = findFirst(ts1);
		int pos2 = pos1;
		while (pos2+1 < size() && !(((Timed) get(pos2+1)).getTime()>ts2) ) {
			pos2++;
		}
		return new int[]{pos1,pos2};
	}

//	/**
//	 * // TODO Insert the method's description here.
//	 * Creation date: (10.1.2002 12:57:45)
//	 * @return de.uos.fmt.musitech.data.structure.Timed[]
//	 */
//	public synchronized boolean remove(long mts) {
//		boolean result = false;
//		int range[] = findAll(mts);
//		for(int i = range[0]; i<range[1]; i++){
//			if( super.remove(i) != null)
//				result= true;
//		}
//		return result;
//	}	

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return this==obj;
	}
	
	/**
	 * @see de.uos.fmt.musitech.data.structure.container.Container#getContents()
	 */
	public Containable[] getContentsRecursive() {
		Collection vec = new ArrayList();
		for(int i=0; i<size();i++){
			Object obj = get(i);
			vec.add(obj);
			if(obj instanceof Container){
				Container container = (Container)obj;
				Containable contents[] = container.getContentsRecursive();
				for (int j = 0; j < contents.length; j++)
					vec.add(contents[j]);
			}
		}
		return (Containable[])vec.toArray(new Containable[]{});
	}

	/**
	 * @see de.uos.fmt.musitech.data.structure.container.Container#containsRecursive(de.uos.fmt.musitech.data.structure.Containable)
	 */
	public boolean containsRecursive(Containable containable) {
		for(int i=0; i<size(); i++){
			Object obj = get(i);
			if(obj==containable)
				return true;
			if(obj instanceof Container) {
				Container container = (Container) obj;
				if(container.containsRecursive(containable))
					return true;
			}
		}
		return false;
	}


}