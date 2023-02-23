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
import java.util.Comparator;
import java.util.Iterator;

/**
 * This is a typed Collection with sorted elements. The type must must be provided at
 * creation. If the type is not a Comparable a comparator must also be provided. Null
 * values are not allowed and values are unique, i.e. the same object will only be added
 * once.
 * 
 * @see de.uos.fmt.musitech.data.structure.TypedCollection<T>
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 * @author Tillman Weyde
 */
public class SortedUniqesCollection<T> extends SortedCollection<T> {

    /**
     * Constructor
     * 
     * @param type Class to be accepted by this Collection.
     * @param comp Comparator must work for type.
     *  
     */
    public SortedUniqesCollection(Comparator comp) {
        super(comp);
    }

    /**
     * Constructor
     * 
     * @param type Class to be accepted by this Collection.
     * @param comp Comparator must work for type.
     *  
     */
    public SortedUniqesCollection(Class type, Comparator comp) {
        super(type, comp);
    }

    /**
     * Constructor
     * 
     * @param type Classes that will be accepted.
     * @param comp Comparator must work for all Classes of Parameter types[].
     */
    public SortedUniqesCollection(Class types[], Comparator comp) {
        super(types, comp);
    }

    /**
     * Constructor. The default comparator will be used.
     * 
     * @param type Must be a Comparable.
     */
    public SortedUniqesCollection(Class type) throws IllegalArgumentException {
        super(type);
        if (!Comparable.class.isAssignableFrom(type))
            throw new IllegalArgumentException("Tpye for " + this.getClass().getName()
                                               + " must implement java.lang.Comparable");
        comp = new DefaultComparator();
    }

    /**
     * Constructor. The default comparator will be used. Will not work for most classes,
     * therefore made private.
     * 
     * @param type Must be Comparables.
     */
    protected SortedUniqesCollection(Class types[]) throws IllegalArgumentException {
        super(types);
        for (int i = 0; i < types.length; i++) {
            Class type = types[i];
            if (!Comparable.class.isAssignableFrom(type))
                throw new IllegalArgumentException("Tpye for " + this.getClass().getName()
                                                   + " must implement java.lang.Comparable");
        }
        comp = new DefaultComparator();
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    @Override
	public boolean add(T obj) {
        if (obj == null)
            return false;
        typeCheck(obj);
        if (indexOf(obj) >= 0)
            return false;
        super.add(obj);
        // TODO make implementation more efficient
        return true;
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
	public boolean addAll(Collection<T> c) {
        Iterator<T> iter = c.iterator();

        while (iter.hasNext()) {
            add(iter.next());
        }
        assert testOrder();
        return true;
    }

    /**
     * Tests the order of the SortedContainer
     * 
     * @return boolean true if the order is correct
     */
    private boolean testOrder() {
        Object array[] = list.toArray();
        for (int i = 0; i < array.length - 1; i++) {
            if (comp.compare(array[i], array[i + 1]) > 0) {
                System.out.println(getClass() + ".testOrder() failed at positions " + i + ","
                                   + (i + 1) + ".");
                System.out.println(array[i] + ", " + array[i + 1]);
                return false;
            }
        }
        return true;
    }

    @Override
	public T[] toArray(T[] array) {
        return list.toArray(array);
    }

}