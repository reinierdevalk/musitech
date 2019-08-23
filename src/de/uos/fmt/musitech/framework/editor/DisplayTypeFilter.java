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
 * Created on 06.12.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Kerstin Neubarth
 *  
 */
public class DisplayTypeFilter {

    /**
     * Possible category value.
     */
    public final static String CONTENT = "content";

    /**
     * Possible category value.
     */
    public final static String CLASS = "class";

    /**
     * Possible category value.
     */
    public final static String DEFAULT = "default";

    /**
     * HashMap which maps categories to display type names. Categories are used
     * to filter display types. The HashMap contains String objects as keys (the
     * categories) and String Array objects as values (list of display type
     * names).
     */
    private HashMap categoryToTypeNames = new HashMap();

    /**
     * If <code>allInCategory</code> is true, all display types in a requested
     * category are added to the accepted objects; if <code>allInCategory</code>
     * is false, only the most specific display type(s) of the category are
     * included, i.e. display classes which are superclasses of an already
     * contained display are rejected. <br>
     * Example: If <code>allInCategory</code> is false, for Containers the
     * display type ContainerEditor is included, but the super class
     * TypedCollectionEditor is not.
     */
    private boolean allInCategory = true;

    /**
     * Constructor. Fills the HashMap <code>categoryToTypeNames</code> for the
     * common MUSITECH displays.
     */
    public DisplayTypeFilter() {
        fillInRegistrations();
    }

    /**
     * Sets the flag <code>allInCategory</code> to the specified boolean
     * value. If <code>allInCategory</code> is true, all display types in a
     * requested category will be added to the accepted objects; if
     * <code>allInCategory</code> is false, only the most specific display
     * type(s) of the category will be included, i.e. display classes which are
     * superclasses of an already contained display will be rejected.
     * 
     * @param allInCategory
     *            boolean value to which <code>allInCategory</code> of this
     *            DisplayTypeFilter will be set
     */
    public void setAllInCategory(boolean allInCategory) {
        this.allInCategory = allInCategory;
    }

    /**
     * Returns the flag <code>allInCategory</code>. If
     * <code>allInCategory</code> is true, all display types in a requested
     * category are added to the accepted objects; if <code>allInCategory</code>
     * is false, only the most specific display type(s) of the category is/are
     * included, i.e. display classes which are superclasses of an already
     * contained display are rejected.
     * 
     * @return boolean <code>allInCategory</code> of this DisplayTypeFilter
     */
    public boolean getAllInCategory() {
        return allInCategory;
    }

    /**
     * Registers the specified String[] containing display type names for the
     * specified <code>category</code>. If the HashMap
     * <code>categoryToTypeNames</code> already contains a key
     * <code>category</code>, the type names are added to the already
     * registered type names.
     * 
     * @param category
     *            String the category of display which will be a key in the
     *            HashMap
     * @param typeNames
     *            String[] names of display types to be registered for the
     *            specified <code>category</code>
     */
    public void registerObjecsToAccept(String category, String[] typeNames) {
        if (category != null && categoryToTypeNames.containsKey(category)) {
            //            Collection registeredNames = Arrays
            //                    .asList((String[]) categoryToTypeNames.get(category));
            Collection registeredNames = new ArrayList();
            String[] names = (String[]) categoryToTypeNames.get(category);
            for (int i = 0; i < names.length; i++) {
                registeredNames.add(names[i]);
            }
            for (int i = 0; i < typeNames.length; i++) {
                if (!registeredNames.contains(typeNames[i])) {
                    registeredNames.add(typeNames[i]);
                }
            }
            String[] namesToRegister = new String[registeredNames.size()];
            int i = 0;
            for (Iterator iter = registeredNames.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                namesToRegister[i++] = element;
            }
            categoryToTypeNames.put(category, namesToRegister);
        } else {
            categoryToTypeNames.put(category, typeNames);
        }
    }

    /**
     * Registers the specified <code>categories</code> for the given String
     * <code>typeName</code>. Iterates the <code>categories</code> and
     * registers each category with the given <code>typeName</code>.
     * 
     * @param typeName
     *            String giving the name of a display type
     * @param categories
     *            String[] containing the categories for which the given type
     *            name will be registered
     */
    public void registerCategories(String typeName, String[] categories) {
        if (categories != null && categories.length > 0) {
            for (int i = 0; i < categories.length; i++) {
                registerObjecsToAccept(categories[i], new String[] { typeName });
            }
        }
    }

    /**
     * Returns a Collection containing the names of display types which are of
     * the specified <code>categories</code>. Please use the constant
     * category values of this class to specify the categories whose displays
     * are to be accepted. If <code>allInCategory</code> is false, only the
     * names of the most specific display types in a category are accepted.
     * 
     * @param displayTypeNames String[] with the names of the display types to filter
     * @param categories
     *            String[] giving the categories whose displays are to be
     *            accepted
     * @return Collection containng the names of the accepted displays
     */
    public Collection getAcceptedObjects(String[] displayTypeNames,
            String[] categories) {
        //        Collection typesToFilter = Arrays.asList(displayTypeNames);
        Collection accepted = new ArrayList();
        //        for (int i = 0; i < categories.length; i++) {
        //            String[] typeNames = (String[]) categoryToTypeNames
        //                    .get(categories[i]);
        //            for (int j = 0; j < typeNames.length; j++) {
        //                if (typesToFilter.contains(typeNames[j])) {
        //                    if (!accepted.contains(typeNames[j])) {
        //                        accepted.add(typeNames[j]);
        //                    }
        //                }
        //            }
        //        }
        Collection registeredNames = new ArrayList();
        for (int i = 0; i < categories.length; i++) {
            String[] typeNames = (String[]) categoryToTypeNames
                    .get(categories[i]);
            if (typeNames!=null)
            	registeredNames.addAll(Arrays.asList(typeNames));
        }
        for (int i = 0; i < displayTypeNames.length; i++) {
            if (registeredNames.contains(displayTypeNames[i])) {
                if (!accepted.contains(displayTypeNames[i])) {
                    accepted.add(displayTypeNames[i]);
                }
            }
        }
        if (!allInCategory) {
            removeSuperTypes(accepted);
        }
        return accepted;
    }

    /**
     * Removes those type names of the specified Collection, whose corresponding
     * classes are superclasses of another type class. The specified Collection
     * ist changed, and a newly created Collection is returned which contains
     * the elements removed from <code>typeNames</code>.
     * 
     * @param typeNames
     *            Collection from which super types are to be removed
     * @return Collection a Collection which contains the elements removed from
     *         the specified Collection
     */
    private Collection removeSuperTypes(Collection typeNames) {
        Collection removedTypes = new ArrayList();
        Collection typeClasses = determineTypeClasses(typeNames);
        for (Iterator iter = typeNames.iterator(); iter.hasNext();) {
            String typeName = (String) iter.next();
            Class cla = determineTypeClass(typeName);
            if (cla != null && isSuperClass(cla, typeClasses)) {
                removedTypes.add(typeName);
            }
        }
        typeNames.removeAll(removedTypes);
        return removedTypes;
    }

    /**
     * Returns a Collection which contains the display classes indicated by the
     * type names given in the specified Collection. The specified Collection
     * contains String elements, the returned Collection contains Class objects.
     * 
     * @param typeNames
     *            Collection with the names of display types
     * @return Collection a Collection containing the classes corresponding to
     *         the type names in the specified Collection
     */
    private Collection determineTypeClasses(Collection typeNames) {
        Collection typeClasses = new ArrayList();
        for (Iterator iter = typeNames.iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            Class cla = determineTypeClass(name);
            if (cla != null) {
                typeClasses.add(cla);
            }
        }
        return typeClasses;
    }

    /**
     * Returns the Class of the display type indicated by the specified
     * <code>typeName</code>. The class is asked from the EditorRegistry or,
     * if the EditorRegistry does not know the type name, is derived from the
     * type name by attaching "Editor" or "Display". If no display Class can be
     * determined, null is returned.
     * 
     * @param typeName
     *            String giving the name of a display type
     * @return Class the display class corresponding to the specified display
     *         type name
     */
    private Class determineTypeClass(String typeName) {
        Class cla = null;
        if (EditorRegistry.getEditorClassName(typeName) != null) {
            try {
                cla = Class
                        .forName(EditorRegistry.getEditorClassName(typeName));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String className = EditorFactory.getEditorClassName(typeName);
            if (className != null) {
                try {
                    cla = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return cla;
    }

    /**
     * Returns true if the specified Class is a super class of an element of the
     * specified Collection, false otherwise. The specified Collection contains
     * Class objects, representing display types.
     * 
     * @param cla
     *            Class to be checked if being a super class of one of the
     *            classes in the specified Collection
     * @param typeClasses
     *            Collection containing Class objects
     * @return boolean true if the specified Class is a super class of a Class
     *         element in the specified Collection, false otherwise
     */
    private boolean isSuperClass(Class cla, Collection typeClasses) {
        boolean isSuper = false;
        for (Iterator iter = typeClasses.iterator(); iter.hasNext();) {
            Class element = (Class) iter.next();
            if (cla.isAssignableFrom(element)) {
                if (!cla.equals(element)) {
                    isSuper = true;
                }
            }
        }
        return isSuper;
    }

    /**
     * Fills the Map <code>categoryToTypeNames</code> with default registrations.
     * A default DisplayTypeFilter accepts NotationDisplay, PianoRollContainerDisplay,
     * WaveDisplay as well as MetaDataEditor, ContextEditor and ArrangementPanel as
     * content-type displays, ContainerEditor and TypedCollectionDisplay as class-type
     * displays and PanelEditor as a default-type display.
     */
    public void fillInRegistrations() {
        if (categoryToTypeNames.size()>0){
            clearRegistrations();
        }
        registerObjecsToAccept(CONTENT, new String[] { "MetaData", "Context",
                "Notation", "ArrangementPanel" }); //TODO ArrangementPanel umbenennen
        // umbenennen
        registerObjecsToAccept(CONTENT, new String[] { "PianoRoll"});
        registerObjecsToAccept(CONTENT, new String[] { "WaveDisplay", "ScrollWaveDisplay"});
        registerObjecsToAccept(CONTENT, new String[]{"MeterDisplay"});
        //        registerTypeNames(CONTENT, new String[] {"ChordSymbol",
        // "ChordDegree", "ChordFunction"});
        //        registerTypeNames(CONTENT, new String[] {"Lyrics",
        // "LyricsSyllable"});
        registerObjecsToAccept(CLASS,
                new String[] { "ListView" });
        registerObjecsToAccept(DEFAULT, new String[] { "Panel" });
    }
    
    /**
     * Clears the Map <code>categoryToTypeNames</code>.
     */
    public void clearRegistrations(){
        categoryToTypeNames.clear();
    }

}