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
 * Created on 07.12.2004
 *
 */
package de.uos.fmt.musitech.structure.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.form.NoteList;

/**
 * The MusicTreeFilter determines which kinds of nodes are used in a
 * MusicTreeView. Nodes are categorized as complex (with children nodes), simple
 * (without children nodes) or no node at all. The filter specifies which kind
 * of node to use with certain object classes. <br>
 * You should use a MusicTreeFilter if you <br>- do not want TypedCollection
 * objects (or objects of a subclass of TypedCollection) to be shown with a
 * complex node: in this case, register the objects' class for simple or no
 * nodes, <br>- want an object other than TypedCollection not to be shown in
 * the tree at all: in this case, register the object's class for no node. <br>
 * <br>
 * The default of a MusicTreeFilter does not show NotationChord, NotationVoice
 * and Note objects and gives a simple instead of a complex node to NoteList
 * objects. If you want to have another implementation, please clear these
 * registrations and register your own ones.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class MusicTreeFilter {

    /**
     * Possible value of a node category. Complex nodes have children nodes.
     */
    public static String COMPLEX_NODE = "complex";

    /**
     * Possible value of a node category. Simple nodes do not have children
     * nodes.
     */
    public static String SIMPLE_NODE = "simple";

    /**
     * Possible value of a node category. Objects of a class registered for this
     * category, will not be shown in the MusicTreeView.
     */
    public static String NO_NODE = "none";

    /**
     * Maps node categories to class names. The Map's keys are of type String,
     * the Map's values or String Arrays containing the names of the classes
     * which are registered for the category given by the key.
     */
    private Map categoryToClassNames = new HashMap();

    /**
     * If <code>includeSubClasses</code> is true, the inheriting classes of a
     * registered class will be accepted as well. If
     * <code>includeSubClasses</code> is false, only the directly registered
     * classes themselves will be accepted for the category with which they are
     * registered.
     */
    private boolean includeSubClasses = true;

    /**
     * Sets the specified boolean value as the <code>includeSubClasses</code>
     * of this MusicTreeFilter. If <code>includeSubClasses</code> is true, the
     * inheriting classes of a registered class will be accepted as well. If
     * <code>includeSubClasses</code> is false, only the directly registered
     * classes themselves will be accepted for the category with which they are
     * registered.
     * 
     * @param includeSubClasses boolean to be set as
     *            <code>includeSubClasses</code> of this MusicTreeFilter
     */
    public void setIncludeSubClasses(boolean includeSubClasses) {
        this.includeSubClasses = includeSubClasses;
    }

    /**
     * Returns the boolean value <code>includeSubClasses</code> of this
     * MusicTreeFilter. If <code>includeSubClasses</code> is true, the
     * inheriting classes of a registered class will be accepted as well. If
     * <code>includeSubClasses</code> is false, only the directly registered
     * classes themselves will be accepted for the category with which they are
     * registered.
     * 
     * @return boolean <code>includeSubClasses</code> of this MusicTreeFilter
     */
    public boolean getIncludeSubClasses() {
        return includeSubClasses;
    }

    /**
     * Creates a MusicTreeFilter with no registrations. If you want to use the
     * default registrations, invoke method <code>fillInRegistrations</code>.
     */
    public MusicTreeFilter() {
    }

    /**
     * Registers the class names given in the specified String Array with the
     * specified <code>category</code> as their key. If the Map
     * <code>categoryToClassNames</code> already contains a key
     * <code>category</code>, the elements of the Array argument are added to
     * the registered class names.
     * 
     * @param category String indicating the type of node in a MusicTreeView
     * @param classNames String[] containing the names of classes which are
     *            registered for the <code>category</code>
     */
    public void registerObjectsToAccept(String category, String[] classNames) {
        if (category == null) {
            return;
        }
        if (categoryToClassNames.containsKey(category)) {
            //            Collection registeredList = Arrays
            //                    .asList((String[]) categoryToClassNames.get(category));
            String[] names = (String[]) categoryToClassNames.get(category);
            Collection registeredList = new ArrayList();
            for (int i = 0; i < names.length; i++) {
                registeredList.add(names[i]);
            }
            for (int i = 0; i < classNames.length; i++) {
                if (!registeredList.contains(classNames[i])) {
                    registeredList.add(classNames[i]);
                }
                String[] toRegister = new String[registeredList.size()];
                int j=0;
                for (Iterator iter = registeredList.iterator(); iter.hasNext();) {
                    String element = (String) iter.next();
                    toRegister[j++] = element;
                }
                categoryToClassNames.put(category, toRegister);
            }
        } else {
            categoryToClassNames.put(category, classNames);
        }
    }

    /**
     * Returns a Collection containing the class names registered for the
     * specified <code>categories</code>. Only class names in the Map will be
     * returned, this method does not take into account
     * <code>includeSubClasses</code>.
     * 
     * @param classNames String[] containing the names of the classes to be
     *            checked if accepted for one of the specified categories
     * @param categories String[] containing the categories against which the
     *            given class names are checked
     * @return Collection containing the class names registered for the
     *         specified categories
     */
    public Collection getAcceptedObjects(String[] classNames, String[] categories) {
        Collection registeredClasses = new ArrayList();
        for (int i = 0; i < categories.length; i++) {
            String[] registered = (String[]) categoryToClassNames.get(categories[i]);
            for (int j = 0; j < registered.length; j++) {
                registeredClasses.add(registered[j]);
            }
        }
        Collection acceptedClassNames = new ArrayList();
        for (int i = 0; i < classNames.length; i++) {
            if (registeredClasses.contains(classNames[i])) {
                acceptedClassNames.add(classNames[i]);
            }
        }
        return acceptedClassNames;
    }

    /**
     * Returns true if the specified class name is accepted for the given
     * <code>category</code>, false otherwise. If
     * <code>includeSubClasses</code> is false, true is returned only if the
     * map contains the given <code>className</code> for the specified
     * <code>category</code>. If <code>includeSubClasses</code> is true,
     * true is also returned, if the Map contains the name of a class which is a
     * superclass or an interface of the specified class.
     * 
     * @param className String giving the name of the class to be checked
     * @param category String giving the category whose values are accepted
     * @return boolean true if the <code>className</code> is accepted for the
     *         given category, false otherwise
     */
    public boolean isAcceptedObject(String className, String category) {
        String[] registered = (String[]) categoryToClassNames.get(category);
        if (registered==null){
            return false;
        }
        Collection registeredList = Arrays.asList(registered);
        if (!includeSubClasses) {
            //check if map contains className
            if (registeredList.contains(className)) {
                return true;
            }
        } else {
            //check if map contains name of a superclass or interface for
            // className
            Class cla = null;
            try {
                cla = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            for (Iterator iter = registeredList.iterator(); iter.hasNext();) {
                String registeredClassName = (String) iter.next();
                Class registeredClass = null;
                try {
                    registeredClass = Class.forName(registeredClassName);
                    if (registeredClass.isAssignableFrom(cla)) {
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Fills the Map <code>categoryToClassNames</code> with default
     * registrations. The default MusicTreeFilter gives a simple node to
     * NoteList objects and no nodes at all to NotationChord, NotationVoice and
     * Note objects. SlurContainer and BeamContainer objects are not shown
     * neither.
     */
    public void fillInRegistrations() {
        if (categoryToClassNames.size() > 0) {
            clearRegistrations();
        }
        registerObjectsToAccept(NO_NODE, new String[] {NotationChord.class.getName(), NotationVoice.class.getName(),
                                                       Note.class.getName()});
        registerObjectsToAccept(SIMPLE_NODE, new String[] {NoteList.class.getName()});
        registerObjectsToAccept(NO_NODE, new String[] {SlurContainer.class.getName(), BeamContainer.class.getName()});
    }

    /**
     * Clears the Map <code>categoryToClassNames</code>.
     */
    public void clearRegistrations() {
        categoryToClassNames.clear();
    }

}