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
 */
package de.uos.fmt.musitech.framework.selection;

import java.util.Collection;
import java.util.Iterator;

/**
 * The DistributedSelection extends the LocalSelection in that it connects the selection
 * to the SelectionManager. If objects are added to or removed from a DistributedSelection
 * the SelectionManager is notified about the changes in the selection.
 * 
 * @author Kerstin Neubarth
 *
 */
public class DistributedSelection extends LocalSelection {	//TODO implements Selection
    ISelectionManager manager = SelectionManager.getManager();
    
    /**
     * Adds the specified <code>selectedObject</code> to the Collection <code>selected</code>
     * and sends a SelectionChangeEvent with the specified <code>source</code> and the
     * <code>selectedObject</code> added to its <code>addedObjects</code> to the
     * SelectionManager. Returns true if <code>selected</code> has changed and the 
     * SelectionChangeEvent has been sent, false otherwise.
     * 
     * @param selectedObject the additional selected Object
     * @param source the component causing the change
     * @return boolean true if the selection has changed and a SelectionChangeEvent has been sent, false otherwise
     */
    public boolean add(Object selectedObject, Object source) {
        if (super.add(selectedObject, source)) {
            SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
            sce.addedObjects.add(selectedObject);
            selectionChanged(sce);
            return true;
        }
        return false;
    }

    /**
     * Adds the objects in the given collection to the selection and sends a
     * SelectionChangedEvent.
     * 
     * @param selectedObjects the additional selected obejcts.
     * @param source The component causing the change.
     * @return
     */
    public boolean addAll(Collection selectedObjects, Object source) {
        if (super.addAll(selectedObjects, source)) {
            SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
            sce.addedObjects.addAll(selectedObjects);
            selectionChanged(sce);
            return true;
        }
        return false;
    }

    /**
     * Sets the new selection to the objects in the parameter collection. Removes any
     * previous selected objects and sends a SelectionChangedEvent.
     * 
     * @param newSelectedObjects
     * @param source The component causing the change.
     */
    public synchronized void replace(Collection newSelectedObjects, Object source) {
        SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
        sce.removedObjects.addAll(selected);
        selected.clear();
        for (Iterator iter = newSelectedObjects.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            selected.add(obj);
            // if the object was not in before
            if (!sce.removedObjects.remove(obj))
                // then we have added the object
                sce.addedObjects.add(obj);
        }
        selectionChanged(sce);
    }

    /**
     * Sets the new selection to the given object. Removes any
     * previous selected objects and sends a SelectionChangedEvent.
     * 
     * @param obj The new only selected object 
     * @param source The component causing the change.
     */
    public synchronized void replace(Object obj, Object source) {
        SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
        sce.removedObjects.addAll(selected);
        selected.clear();
        selected.add(obj);
        // if the object was not in before
        if (!sce.removedObjects.remove(obj)) {
            // then we have added the object
            sce.addedObjects.add(obj);
        }
        selectionChanged(sce);
    }

    /**
     * Clears the Collection <code>selected</code> and sends a SelectionChangeEvent with
     * the specified Object as its source and all former elements of <code>selected</code>
     * added to its <code>removedObjects</code> to the SelectionManager.
     */
    public void clear(Object source) {
        if (!selected.isEmpty()) {
            SelectionChangeEvent sce = new SelectionChangeEvent(source, this);
            sce.removedObjects.addAll(selected);
            selected.clear();
            selectionChanged(sce);
        }
    }

    /**
     * Removes the specified <code>deselectedObject</code> from the Collection 
     * <code>selected</code> and sends a SelectionChangeEvent with the specified
     * <code>source</code> and the <code>deselectedObject</code> added to its
     * <code>removedObjects</code> to the SelectionManager.
     * Returns true if <code>selected</code> has changed and a SelectionChangeEvent 
     * has been sent.
     * 
     * @param deselectedObject Object to be removed from the selection
     * @param source Object causing the change of the selection
     * @return boolean true if <code>selected</code> has changed and a SelectionChangeEvent has been sent, false otherwise
     */
    public boolean remove(Object deselectedObject, Object source) {
        if (super.remove(deselectedObject, source)) { 
            SelectionChangeEvent sce = new SelectionChangeEvent(this);
            sce.removedObjects.add(deselectedObject);
            selectionChanged(sce);
            return true;
        }
        return false;
    }

    /**
     * Removes all elements of the specified Collection from <code>selected</code> and
     * sends a SelectionChangeEvent with the specified <code>source</code> and the
     * deselected objects added to its <code>removedObjects</code> to the 
     * SelectionManager. Returns true if <code>selected</code> has changed and
     * a SelectionChangeEvent has been sent.
     * 
     * @param deselectedObjects Collection whose elements are to be removed from the selection
     * @param source Object causing the change of the selection
     * @return boolean true if <code>selected</code> has changed and a SelectionChangeEvent has been sent, false otherwise
     */
    public boolean removeAll(Collection deselectedObjects, Object source) {
        if (super.removeAll(deselectedObjects, source)) { 
            SelectionChangeEvent sce = new SelectionChangeEvent(this);
            sce.removedObjects.addAll(deselectedObjects);
            selectionChanged(sce);
            return true;
        }
        return false;
    }

    public void setSelectionManager(ISelectionManager manager) {
    	this.manager = manager;
    }
    
    /**
     * Sends the specified SelectionChangeEvent to the SelectionManager.
     */
    private void selectionChanged(SelectionChangeEvent event) {
        if (manager==null){
            //TODO überprüfen, wieso das auftritt
            manager = SelectionManager.getManager();
        }
    	manager.selectionChanged(event);
    }

    
}
