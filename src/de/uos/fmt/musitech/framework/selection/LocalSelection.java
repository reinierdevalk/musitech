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
 * Created on 26.11.2004
 *
 */
package de.uos.fmt.musitech.framework.selection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.time.TimedComparator;
import de.uos.fmt.musitech.time.TimeRange;
import de.uos.fmt.musitech.utility.collection.SortedUniqesCollection;

/**
 * This class provides a Selection for locally working SelectingEditors. Objects can
 * be added to or removed from the selection, i.e. are selected resp. deselected.
 * The LocalSelection is not committed to the SelectionManager.If you want to notify
 * the SelectionManager about changes in the selection, you have to use a 
 * DistributedSelection.
 * 
 * @author Kerstin Neubarth
 *
 */
public class LocalSelection implements Selection {
    
    /**
     * Collection containing the currently selected objects.
     */
    protected Collection selected = Collections.synchronizedSet(new HashSet());
    
    /**
     * Adds the specified <code>selectedObject</code> to the Collection <code>selected</code>
     * and returns true, if <code>selected</code> has changed.
     * 
     * @param selectedObject Object which has been selected and is added to <code>selected</code> 
     * @param source Object source of the selection
     * @return boolean true if the specified <code>selectedObject</code> has been added to <code>selected</code>, false otherwise
     */
    public boolean add(Object selectedObject, Object source) {
        return selected.add(selectedObject);
    }
    
    /**
     * Adds all elements of the specified Collection to <code>selected</code> and returns
     * true if <code>selected</code> has changed.
     * 
     * @param selectedObjects Collection whose elements are to be added to <code>selected</code>
     * @param source Object source of the selection
     * @return boolean true if <code>selected</code> has changed, false otherwise
     */
    public boolean addAll(Collection selectedObjects, Object source) {
        return selected.addAll(selectedObjects);
    }
    
    /**
     * Replaces the current elements of <code>selected</code> by the elements of the
     * specified Collection, i.e. clears <code>selected</code> and adds all elements
     * of <code>newSelectedObjects</code>.
     * 
     * @param newSelectedObjects Collection which is to replace the current selection
     * @param source Object source of the selection
     */
    public synchronized void replace(Collection newSelectedObjects, Object source) {
        selected.clear();
        addAll(newSelectedObjects, source);
    }
    
    /**
     * Replaces the current elements of <code>selected</code> by the specified Object
     * <code>obj</code>, i.e. clears <code>selected</code> and adds <code>obj</code>.
     * 
     * @param obj Object which is to replace the current selection
     * @param source Object source of the selection
     */
    public synchronized void replace(Object obj, Object source) {
        selected.clear();
        add(obj, source);
    }
    
    /**
     * Returns true if the specified Object is contained in <code>selected</code>, 
     * false otherwise.
     * 
     * @param o Object which is checked if being contained in the selection
     * @return true if the specified Object is contained in the selection, false otherwise
     */
    public boolean isSelected(Object o) {
        return selected.contains(o);
    }
    
    /**
     * Returns the content of the selection as an unmodifyable collection.
     * 
     * @return Collection the elements of <code>selected</code> as an unmodifiable Collection
     */
    public Collection getAll() {
        return Collections.unmodifiableCollection(selected);
    }
    
    /**
     * Removes the specified <code>deselectedObject</code> from <code>selected</code>
     * and returns true if <code>selected</code> has changed.
     * 
     * @param deselectedObject Object ot be removed from the selection
     * @param source Object source of the selection
     * @return boolean true if <code>selected</code> has changed, false otherwise
     */
    public boolean remove(Object deselectedObject, Object source) {
        return selected.remove(deselectedObject);
    }
    
    /**
     * Removes all elements of the specified Collection from <code>selected</code> 
     * and returns true if <code>selected</code> has changed.
     * 
     * @param deselectedObjects Collection whose elements are to be removed from the selection
     * @param source Object source of the selection
     * @return boolean true if <code>selected</code> has changed, false otherwise
     */
    public boolean removeAll(Collection deselectedObjects, Object source) {
        return selected.removeAll(deselectedObjects);
    }
    
    /**
     * Clears the Collection <code>selected</code>.
     * 
     * @param source Object source of the selection
     */
    public void clear(Object source) {
        if (!selected.isEmpty()) {         
            selected.clear();        
        }
    }
    
    /**
     * Returns the temporal extension of the <code>selected</code>. Returns null,
     * if there are no timed objects in the selection.
     * 
     * @return TimeRange time covered by the selected, or null
     */
    public TimeRange getTimeRange() {
        SortedUniqesCollection timedObjects = getTimedObjects();
        if (timedObjects == null || timedObjects.size() == 0)
            return null;
        TimeRange timeRange = new TimeRange();
        timeRange.setStart(((Timed) timedObjects.get(0)).getTime());
        timeRange.setEnd(((Timed) timedObjects.get(timedObjects.size() - 1)).getTime());
        return timeRange;
    }

    /**
     * Returns all timed objects in the selection. If <code>selected</code> does not
     * contain timed objects, an empty SortedUniquesCollection is returned.
     * 
     * @return SortedUniquesCollection containing the timed objects of the selection, or an empty SortedUniquesCollection
     */
    public SortedUniqesCollection getTimedObjects() {
        SortedUniqesCollection setOfTimedObjects = new SortedUniqesCollection(
                                                                              Timed.class,
                                                                              new TimedComparator());
        for (Iterator iter = getAll().iterator(); iter.hasNext();) {
            MObject element = (MObject) iter.next();
            if (Timed.class.isInstance(element)) {
                setOfTimedObjects.add(element);
            }
        }
        return setOfTimedObjects;
    }


}
