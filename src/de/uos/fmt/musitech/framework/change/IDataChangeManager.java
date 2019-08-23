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
 * Created on Aug 25, 2004
 */

package de.uos.fmt.musitech.framework.change;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.Editor;

/**
 * This interface is used for the synchronization of local or distributed views.
 * Every view must implement DataChangeListener and register its interest in
 * specific objects at the DataChangeManager with
 * <code>interestExpanded()</code>. It is then notified of data changes of
 * the registered Objects. The view must unregister itself with one of the
 * <code>interestReduced*()</code> Methods is it does not want to be notified
 * any more.
 * 
 * @author Tillman Weyde, Udo Waechter
 */
public interface IDataChangeManager {

    /**
     * The view's interest is registered. Every object provided by the iteratior
     * given is registered, but not the iterator itself.
     * 
     * @param view
     *            the view interested in datachanges
     * @param it
     *            the dataObjects the view is interested in
     */
    public void interestExpandMulti(DataChangeListener view, Iterator it);

    /**
     * The view is interested in data changes of the Objects within the given
     * collection
     * 
     * @param view
     *            the view interestd in data changes
     * @param interestingObjects
     *            the data in which the view is interested
     */
    public void interestExpandElements(DataChangeListener view, Collection interestingObjects);

    /**
     * View is no longer interested in the objects contained in
     * <code>uninterestingObjects</code>.
     * 
     * @param view
     *            the view which is no longer interested
     * @param uninterestingObjects
     *            contains the data in which the view is no longer interested
     */
    public void interestReduceElements(DataChangeListener view, Collection uninterestingObjects);

    /**
     * View is no longer interested in objects contained in
     * <code>uniterestingObject</code>.
     * 
     * @param view
     *            which is no longer interested in the given data
     * @param uninterestingObject
     *            the data object in which the view is no longer interested
     */
    public void interestReduceObject(DataChangeListener view, Object uninterestingObject);

    /**
     * changed(...) is called by editors that made changes to objects. A
     * collection of changed objects and a DataChangeEvent, that may contain
     * details about the changes, are provided
     * 
     * @param changedObjects
     *            Collection of changed objects.
     * @param changeEvent
     *            Details about the changes.
     */
    public void changed(Collection changedObjects, DataChangeEvent changeEvent);

    /**
     * Removes the specified DataChangeListener <code>listener</code> from the
     * internal <code>table</code> containing all listeniong ChangeListeners.
     * 
     * @param listener
     *            DataChangeListener to be removed from the table
     */
    public void removeListener(DataChangeListener listener);

    /**
     * The view is interested in changes of the elements contained within this
     * container (not the container itself, which can be registered with
     * <code>interestExpandObject</code>).
     * 
     * @param view
     *            The view to be notified of changes.
     * @param interestingData
     *            The Container with the data elements the view is interested
     *            in.
     * @see #interestExpandMulti(DataChangeListener, Iterator)
     */
    public void interestExpandElements(DataChangeListener view, Container interestingData);

    /**
     * The view is interested in changes to the object
     * <code>interestingObject</code>.
     * 
     * @param view
     *            The view to be notified.
     * @param interestingObject
     *            The data object which the view is interested in.
     */
    public void interestExpandObject(DataChangeListener view, Object interestingObject);

    /**
     * Returns true if the specified <code>editor</code> is registered as a
     * view with this DataChangeManager. <br>
     * This method is for testing, e.g. in class
     * <code>TestEditorWithChangeEvents</code>.
     * 
     * @param editor
     *            Editor for which to check if it is known to this
     *            DataChangeManager.
     * @return true if the the specified editor is contained in this
     *         DataChangeManager.
     */
    public boolean tableContainsEditor(Editor editor);

    /**
     * Clears the table storing all registered DataChangeListeners
     */
    public void clearTable();

    /**
     * Retrieve all registered DataChangeListeners
     * 
     * @return the table with all regsitered DataChangeListeners
     */
    public Map getTable();
}