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
package de.uos.fmt.musitech.framework.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.Editor;

/**
 * Interested views are notified of data changes by this DataChangeManager. m:n -
 * relation between data objects and views. Editors must know their changeable
 * objects and register them here. When an Editor changes objects, it must call
 * changed() in this manager. DataChangeListeners must return fast from
 * dataChanged().
 * 
 * @author Felix Kugel, Tillman Weyde, Kerstin Neubarth
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class DataChangeManager implements IDataChangeManager {

    private DataChangeManager() {
    }

    static IDataChangeManager changeManager = new DataChangeManager();

    /**
     * getInstance() returns the single object instance of DataChangeManager.
     * This is a singleton.
     * 
     * @return the manager instance
     */
    public static IDataChangeManager getInstance() {
        return changeManager;
    }

    // Keys are data objects
    // values are ArrayLists, containing the dependent views

    private Map data2ViewSetMap = Collections.synchronizedMap(new IdentityHashMap());

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        StringBuffer result = new StringBuffer("");

        for (Iterator it = data2ViewSetMap.keySet().iterator(); it.hasNext();) {
            Object element = it.next();
            result.append(element.toString() + " -> " + data2ViewSetMap.get(element).toString() + "\n");
        }

        return result.toString();
    }

    /**
     * Register a view for an object.
     * 
     * @param view The view expanding its interest.
     * @param it The interesting Objects.
     */
    @Override
	public synchronized void interestExpandMulti(DataChangeListener view, Iterator it) {
        // für jedes Objekt der Collection:
        // - schon vorhanden?
        //   ja: view mit in das HashSet hängen, ggf. neuen erzeugen
        //   nein: view direkt einhängen

        while (it.hasNext()) {
            Object dataObject = it.next();
            interestExpandObject(view, dataObject);
            //			if ( data2ViewSetMap.containsKey( dataObject ) )
            //			{
            //				Object value = data2ViewSetMap.get( dataObject );
            //				try
            //				{
            //					Collection viewList = (Collection) value;
            //					if (! viewList.contains( view ) ) viewList.add( view );
            //				} catch ( ClassCastException e )
            //				{
            //					System.out.println( "WARNING: ChangeManager.interestExpanded:
            // entry is not a HashSet!" );
            //				}
            //
            //			} else
            //			{
            //				ArrayList list = new ArrayList();
            //				list.add( view );
            //				data2ViewSetMap.put( dataObject, list );
            //			}
        }
        //for testing:
        //		printTable();
    }

    /**
     * @see de.uos.fmt.musitech.framework.change.IDataChangeManager#interestExpandElements(de.uos.fmt.musitech.framework.change.DataChangeListener,
     *      java.util.Collection)
     */
    @Override
	public synchronized void interestExpandElements(DataChangeListener view, Collection newData) {
        Iterator it = newData.iterator();
        interestExpandMulti(view, it);
    }

    /**
     * @see de.uos.fmt.musitech.framework.change.IDataChangeManager#interestExpandElements(de.uos.fmt.musitech.framework.change.DataChangeListener,
     *      de.uos.fmt.musitech.data.structure.container.Container)
     */
    @Override
	public synchronized void interestExpandElements(DataChangeListener view, Container newData) {
        Iterator it = newData.iterator();
        interestExpandMulti(view, it);
    }

    /**
     * @see de.uos.fmt.musitech.framework.change.IDataChangeManager#interestReduceElements(de.uos.fmt.musitech.framework.change.DataChangeListener,
     *      java.util.Collection)
     */
    @Override
	public synchronized void interestReduceElements(DataChangeListener view, Collection oldData) {
        // für jedes Objekt der Collection:
        // falls schon vorhanden:
        //    view aus dem HashSet nehmen, ggf. neuen erzeugen

        Iterator it = oldData.iterator();
        while (it.hasNext()) {
            Object dataObject = it.next();
            interestReduceObject(view, dataObject);
            //            if (data2ViewSetMap.containsKey(dataObject)) {
            //                Object value = data2ViewSetMap.get(dataObject);
            //                if (value instanceof Collection) {
            //                    // schon mehrere Views, in HashSet zusammengefasst
            //                    ((Collection) value).remove(view);
            //                } else {
            //                    System.out
            //                            .println("WARNING: " + this.getClass()
            //                                     + "interesShrinked(): viewer map entry is not a collection.");
            //                }
            //            }
        }
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.change.IDataChangeManager#interestReduceObject(de.uos.fmt.musitech.framework.change.DataChangeListener,
     *      java.util.Collection)
     */
    @Override
	public void interestReduceObject(DataChangeListener view, Object dataObject) {
        if (data2ViewSetMap.containsKey(dataObject)) {
            Object value = data2ViewSetMap.get(dataObject);
            if (value instanceof Collection) {
                // schon mehrere Views, in HashSet zusammengefasst
                ((Collection) value).remove(view);
            } else {
                System.out.println("WARNING: " + this.getClass()
                                   + "interesShrinked(): viewer map entry is not a collection.");
            }
        }
    }

    /**
     * changed(...) is called by editors that made changes to objects. A
     * collection of changed objects and a DataChangeEvent, that may contain
     * details about the changes, are provided
     * 
     * @param changedObjects Collection of changed objects.
     * @param changeEvent Details about the changes.
     */
    @Override
	public synchronized void changed(Collection changedObjects, DataChangeEvent changeEvent) {
        // Markierungen an die Datenobjekte setzen (changed = true...)

        // gather all interested views.
        Collection views = new HashSet();

        for (Iterator it = changedObjects.iterator(); it.hasNext();) {
            Object next = it.next();
            if (data2ViewSetMap.containsKey(next)) {
                Collection viewContainer = (Collection) (data2ViewSetMap.get(next));
                views.addAll(viewContainer); // Views aus aktuellem
                // ViewContainer sammeln
            }
        }

        // notify all views
        for (Iterator it = views.iterator(); it.hasNext();) {
            DataChangeListener view = (DataChangeListener) it.next();
            view.dataChanged(changeEvent); // gefährlich, weils dauern kann
        }

    }

    /**
     * Prints the contents of <code>data2ViewSetMap</code> to the command
     * line. This method is for testing only.
     */
    private void printTable() {
        System.out.println("ChangeManager: data2ViewSetMap \t(Class " + data2ViewSetMap.getClass().toString() + ")");
        for (Iterator iter = data2ViewSetMap.keySet().iterator(); iter.hasNext();) {
            Object keyToElement = iter.next();
            //			if (keyToElement != null)
            System.out.println("Key: " + keyToElement.getClass().toString() + "\tObject to String: "
                               + keyToElement.toString() + "\tHashcode: " + keyToElement.hashCode() + "\t");
            Collection entries = (ArrayList) data2ViewSetMap.get(keyToElement);
            int i = 0;
            if (entries != null)
                for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
                    DataChangeListener element = (DataChangeListener) iterator.next();
                    System.out.print("\tValue " + i++ + " Class: " + element.getClass().toString());
                    if (element instanceof Editor)
                        System.out.println("\tLabeltext: " + ((Editor) element).getEditingProfile().getLabel());
                }
            System.out.println();
        }
    }

    /**
     * Removes the specified DataChangeListener <code>listener</code> from the
     * <code>data2ViewSetMap</code>. More precisely, the
     * <code>listener</code> is removed from the ArrayLists containing the
     * views associated with given objects as keys. If a key is left without any
     * views, its entry is removed.
     * 
     * @param listener DataChangeListener to be removed from the data2ViewSetMap
     */
    @Override
	public void removeListener(DataChangeListener listener) {
        for (Iterator iter = data2ViewSetMap.entrySet().iterator(); iter.hasNext();) {
            try {
                Map.Entry entry = (Map.Entry)iter.next();
                Object key = entry.getKey();
                Collection views = (Collection) data2ViewSetMap.get(key);
                if (views.contains(listener))
                    views.remove(listener);
                if (views.isEmpty())
                    iter.remove();
            } catch (ConcurrentModificationException e) {
                // this should only happen in case of thread problems, 
                // not by the call to remove
                e.printStackTrace();
                iter = data2ViewSetMap.entrySet().iterator();
                continue;
            }
        }

    }

    /**
     * Returns the <code>data2ViewSetMap</code> which associates the obejcts
     * with the vies.
     * 
     * @return An unmodifiable copy of the map.
     */
    //for use in testing (cf. class TestEditorsWithDataChangeEvents)
    @Override
	public Map getTable() {
        return Collections.unmodifiableMap(data2ViewSetMap);
    }

    /**
     * Returns true if the specified <code>editor</code> is contained in the
     * data2ViewSetMap of the DataChangeManager. <br>
     * This method is for testing, e.g. in class
     * <code>TestEditorWithChangeEvents</code>.
     * 
     * @param editor Editor to be checked if known to the DataChangeManager
     * @return true if the data2ViewSetMap contains the specified
     *         <code>editor</code>
     */
    @Override
	public boolean tableContainsEditor(Editor editor) {
        boolean editorContained = false;
        Set keys = data2ViewSetMap.keySet();
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            Object key = iter.next();
            Collection views = (Collection) data2ViewSetMap.get(key);
            if (views.contains(editor))
                editorContained = true;
        }
        return editorContained;
    }

    /**
     * Clears the <code>data2ViewSetMap</code>. This method is used in
     * testing (e.g. in class <code>TestEditorsWithDataChangeEvents</code>)
     * for clearing the <code>data2ViewSetMap</code> at the end of a test
     * method to avoid interferences between test methods.
     */
    @Override
	public void clearTable() {
        if (data2ViewSetMap != null && !data2ViewSetMap.isEmpty())
            data2ViewSetMap.clear();
    }

    /**
     * @param manager The changeManager to set.
     */
    public static void setInstance(IDataChangeManager manager) {
        DataChangeManager.changeManager = manager;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.framework.change.IDataChangeManager#interestExpandObject(de.uos.fmt.musitech.framework.change.DataChangeListener,
     *      java.lang.Object)
     */
    @Override
	public void interestExpandObject(DataChangeListener view, Object dataObject) {
        if (data2ViewSetMap.containsKey(dataObject)) {
            Object value = data2ViewSetMap.get(dataObject);
            try {
                Collection viewList = (Collection) value;
                if (!viewList.contains(view))
                    viewList.add(view);
            } catch (ClassCastException e) {
                System.out.println("WARNING: ChangeManager.interestExpandObject: entry is not a HashSet!");
            }

        } else {
            ArrayList list = new ArrayList();
            list.add(view);
            data2ViewSetMap.put(dataObject, list);
        }

    }

}