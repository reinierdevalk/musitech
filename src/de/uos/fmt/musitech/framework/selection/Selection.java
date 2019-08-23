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
package de.uos.fmt.musitech.framework.selection;

import java.util.Collection;

import de.uos.fmt.musitech.time.TimeRange;
import de.uos.fmt.musitech.utility.collection.SortedUniqesCollection;

/**
 * Represents a selection, containing references to the selected objects and calculating the
 * time range covered by the objects.
 * 
 * @author Felix Kugel, Tillman Weyde
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public interface Selection {

//    private Collection selected = Collections.synchronizedSet(new HashSet());

    /**
     * A selectable object is added to the Selection, i.e. it is selected.
     * Returns true if the selection has changed, false otherwise.
     * The <code>source</code> is the Object causing the change in the selection.
     * In DistributedSelecton it is used in sending a SelectionChangeEvent.
     * 
     * @param selectedObject The additional selected Object.
     * @param source The component causing the change.
     * @return boolean true if the selection has changed, false otherwise
     */
    public boolean add(Object selectedObject, Object source); //{ Anm.: vorher void
//        if (selected.add(selectedObject)) {// TODO operation notwendig?
//            SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
//
//            sce.addedObjects.add(selectedObject);
//
//            selectionChanged(sce);
//        }
//    }

    /**
     * Adds the objects in the given collection to the selection and sends a
     * SelectionChangedEvent.
     * 
     * @param selectedObjects the additional selected obejcts.
     * @param source The component causing the change.
     * @return
     */
    public boolean addAll(Collection selectedObjects, Object source);// { Anm.: vorher void
//        if (selected.addAll(selectedObjects)) {// operation notwendig?
//            SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
//            sce.addedObjects.addAll(selectedObjects);
//            selectionChanged(sce);
//        }
//    }

    /**
     * Sets the new selection to the objects in the parameter collection. Removes any
     * previous selected objects and sends a SelectionChangedEvent.
     * 
     * @param newSelectedObjects
     * @param source The component causing the change.
     */
    public void replace(Collection newSelectedObjects, Object source);// { Anm.:war vorher synchronized
//        SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
//        sce.removedObjects.addAll(selected);
//        selected.clear();
//        for (Iterator iter = newSelectedObjects.iterator(); iter.hasNext();) {
//            Object obj = iter.next();
//            selected.add(obj);
//            // if the object was not in before
//            if (!sce.removedObjects.remove(obj))
//                // then we have added the object
//                sce.addedObjects.add(obj);
//        }
//        selectionChanged(sce);
//    }

    /**
     * Sets the new selection to the given object. Removes any
     * previous selected objects and sends a SelectionChangedEvent.
     * 
     * @param obj The new only selected object 
     * @param source The component causing the change.
     */
    public void replace(Object obj, Object source);// { Anm.: war vorher synchronized
//        SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
//        sce.removedObjects.addAll(selected);
//        selected.clear();
//        selected.add(obj);
//        // if the object was not in before
//        if (!sce.removedObjects.remove(obj)) {
//            // then we have added the object
//            sce.addedObjects.add(obj);
//        }
//        selectionChanged(sce);
//    }

    /**
     * isSelected
     * 
     * @param o the Object to be checked
     * @return true if the object is selected.
     */
    public boolean isSelected(Object o);// {
//        return selected.contains(o);
//    }

    /**
     * Returns the content of the selection as an unmodifyable collection.
     * 
     * @return the contents
     */
    public Collection getAll(); // {
//        return Collections.unmodifiableCollection(selected);
//    }

    /**
     * Clear the selected completely.
     * @param source
     */
    public void clear(Object source);// {
//        if (!selected.isEmpty()) {
//            SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
//            sce.removedObjects.addAll(selected);
//            selected.clear();
//            selectionChanged(sce);
//        }
//    }

    /**
     * Method remove. Das Selectable wird aus der Selektion entfent ("deselektiert").
     * @param deselectedObject
     * @param source
     * @return
     */
    public boolean remove(Object deselectedObject, Object source); // { Anm.: vorher void
//        if (selected.remove(deselectedObject)) { // operation notwendig?
//
//            SelectionChangeEvent sce = new SelectionChangeEvent(this);
//
//            sce.removedObjects.add(deselectedObject);
//
//            selectionChanged(sce);
//
//        }
//    }

    /**
     * removeAll
     * 
     * @param deselectedObjects
     * @param source
     * @return
     */
    public boolean removeAll(Collection deselectedObjects, Object source); // { Anm.: vorher void
//        if (selected.removeAll(deselectedObjects)) { // operation notwendig?
//
//            SelectionChangeEvent sce = new SelectionChangeEvent(this);
//
//            sce.removedObjects.addAll(deselectedObjects);
//
//            selectionChanged(sce);
//
//        }
//    }

    /**
     * Method selectionChanged. Ändert sich etwas an der Selektion, so müssen die Views
     * benachrichtigt werden.
     */
//    private void selectionChanged(SelectionChangeEvent event) {
//        SelectionManager.getManager().selectionChanged(event);
//    }

    /**
     * Method getTimeRange gets the temporal extension of the selected.
     * 
     * @return TimeRange The time covered by the selected. This is null if there are no
     *         timed objects in the selected.
     */
    public TimeRange getTimeRange(); // {
//
//        //		long start = 0;
//        //		long end = 0;
//
//        SortedUniqesCollection timedObjects = getTimedObjects();
//        if (timedObjects == null || timedObjects.size() == 0)
//            return null;
//        TimeRange timeRange = new TimeRange();
//        timeRange.setStart(((Timed) timedObjects.get(0)).getTime());
//        timeRange.setEnd(((Timed) timedObjects.get(timedObjects.size() - 1)).getTime());
//
//        return timeRange;
//    }

    /**
     * getTimedObjects
     * 
     * @return
     */
    public SortedUniqesCollection getTimedObjects(); // {
//        SortedUniqesCollection setOfTimedObjects = new SortedUniqesCollection(
//                                                                              Timed.class,
//                                                                              new TimedComparator());
//        for (Iterator iter = getAll().iterator(); iter.hasNext();) {
//            MObject element = (MObject) iter.next();
//            if (Timed.class.isInstance(element)) {
//                setOfTimedObjects.add(element);
//            }
//        }
//        return setOfTimedObjects;
//    }

}