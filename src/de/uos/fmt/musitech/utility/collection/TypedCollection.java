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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.structure.container.Container;

/**
 * This Collection accepts only certain types of objects. These types are determined at
 * creation.
 * 
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 * @author Tillman Weyde
 */
public class TypedCollection<T> implements Serializable, Cloneable {

    private static final long serialVersionUID = -2500903171512800814L;

    /**
     * The types that this collection can hold.
     * 
     * @uml.property name="types"
     */
    private final Class types[];

    protected List<T> list = new ArrayList<T>();

    /**
     * Gets the contents as a list. This should only be used for persistence.
     * 
     * @return
     */
    public List<T> getContent() {
        return list;
    }

    /**
     * Set the contents of this collection. This should only be used for persistence.
     * 
     * @param argList The content list to set.
     * 
     * An attempt to hibernate a class inherited from an Array.
     * 
     * The Problem is that Hibernate stores a simple class presented as a record in table.
     * An Array contents already a set of simple objects. And content should be save in
     * its own field.
     */
    public void setContent(List<T> argList) {
        this.list = argList;
    }

    /**
     * The number of elements contained in this typed collection.
     * 
     * @return The number of contained elements.
     * @see java.util.List#size()
     */
    public int size() {
        return list.size();
    }

    /**
     * Get an iterator for this collection.
     * 
     * @return The iterator.
     * @see java.util.List#iterator()
     */
    public Iterator<T> iterator() {
        return list.iterator();
    }

    /**
     * Gets the object at position i.
     * 
     * @param i The position to get the object from.
     * @return The object stored at position i.
     * @see List#get(int)
     */
    public T get(int i) {
        return list.get(i);
    }

    public boolean remove(Object obj) {
        return list.remove(obj);
    }

    public boolean contains(Object obj) {
        return list.contains(obj);
    }

    public T remove(int i) {
        return list.remove(i);
    }

    public T[] toArray(T array[]) {
        return list.toArray(array);
    }

    public int indexOf(Object obj) {
        return list.indexOf(obj);
    }

    public void clear() {
        list.clear();
    }

    /**
     * Constructor for Collection of one Type.
     * 
     * @param type Class
     */
    public TypedCollection(Class type) {
        this.types = new Class[] {type};
    }

    /**
     * Constructor for Collection accepting several types.
     * 
     * @param types[] Class
     */
    public TypedCollection(Class types[]) {
        this.types = types;
    }

    /**
     * Method typeCheck checks if the argument is of any of the accepted types.
     * 
     * @param obj
     * @return boolean
     * @throws IllegalArgumentException
     */
    protected boolean typeCheck(Object obj) throws IllegalArgumentException {
        if (obj == null)
            throw new NullPointerException(
                    "TypedColletion - add(Object): Argument must not be null.");
        Class objClass = obj.getClass();
        for (int i = 0; i < types.length; i++) {
            if (types[i].isAssignableFrom(objClass))
                return true;
        }
        StringBuffer sb = new StringBuffer("TypedColletion - Argument " + obj
                                           + " is not of correct type. Valid types are ");
        for (int i = 0; i < types.length; i++) {
            sb.append(' ');
            sb.append(types[i]);
        }
        throw new IllegalArgumentException(sb.toString());
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(T obj) {
        typeCheck(obj);
        return list.add(obj);
    }

    /**
     * @see java.util.Collection#add(int pos, java.lang.Object)
     */
    public void add(int pos, T obj) {
        typeCheck(obj);
        list.add(pos, obj);
    }

    /**
     * Add the contents of another container to this one. 
     * 
     * @param c The Container with the elements to add.
     * @return true if this container has been changed as a result of this call.
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Container<? extends T> c) {
        boolean changed = false;
        for (Iterator<? extends T> iter = c.iterator(); iter.hasNext();) {
            changed |= add(iter.next());
        }
        return changed;
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<T> c) {
        for (Iterator<T> iter = c.iterator(); iter.hasNext();) {
            typeCheck(iter.next());
        }
        return list.addAll(list.size(), c);
    }

    /**
     * @param index 
     * @param c 
     * @return 
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<T> c) {
        Iterator<T> iter = c.iterator();
        T obj;
        boolean changed = false;
        int offset = 0;
        while (iter.hasNext()) {
            obj = iter.next();
            add(index + (offset++), obj);
            changed = true;
        }
        return changed;
    }

    /**
     * Returns an Array of the classes possible in this TypedCollection.
     * 
     * @return Class[] types
     * 
     * @uml.property name="types"
     */
    public Class[] getTypes() {
        return types;
    }

    /**
     * Returns a shallow copy of this TypedCollecton, i.e. 
     * the information of the collection is copied, but 
     * the elements contained are not copied.
     * 
     * @see java.lang.Object#clone()
     */
    public synchronized Object clone() {
        TypedCollection tc;
        try {
            tc = (TypedCollection) super.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // list needs to be cast to Cloneable to
        if(list instanceof Cloneable) {
            tc.list = (List)((ArrayList)list).clone();	
		} else {
			if(tc.list == null)
				tc.list = new ArrayList();
			tc.list.addAll(list);
    	}
//      tc.list = (List) ((ArrayList) tc.list).clone();
//        for (Iterator iter = iterator(); iter.hasNext();) {
//            Object elem = iter.next();
//            if (elem instanceof TypedCollection) {
//                TypedCollection elemTC = (TypedCollection) elem;
//                tc.remove(elemTC);
//                tc.add(elemTC.clone());
//            }
//        }
        return tc;
    }

    /**
     * Removes the content of the specified TypedCollection from this TypedCollection.
     * Returns true if elements have been removed, false otherwise.
     * 
     * @param typedCollection TypedCollection whose content is to be removed from this
     *            TypedCollection
     * @return
     */
    public boolean removeAll(TypedCollection typedCollection) {
        if (typedCollection != null && typedCollection.getContent() != null) {
            return list.removeAll(typedCollection.getContent());
        }
        return false;
    }

    /**
     * Removes the content of the specified TypedCollection from this TypedCollection.
     * Returns true if elements have been removed, false otherwise.
     * 
     * @param typedCollection TypedCollection whose content is to be removed from this
     *            TypedCollection
     * @return true if the list was changed as a result of this call.
     */
    public boolean removeAll(Collection<?> typedCollection) {
        return list.removeAll(typedCollection);
    }

    public boolean isEmpty() {
        return !(size() > 0);
    }
    
    /**
     * visit all content-objects
     * @param visitor the visitor that 
     */
    public void visit(ICollectionElementVisitor visitor){
        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            visitor.visit(iter.next());
       	}
    }
}