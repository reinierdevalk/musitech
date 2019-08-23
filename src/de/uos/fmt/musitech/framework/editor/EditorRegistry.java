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
 * Created on 30.06.2004
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.utility.DebugState;

/**
 * The EditorRegistry holds the Editortypes for classes of objects to be edited.
 * 
 * @author Kerstin Neubarth
 */
public class EditorRegistry {

    /**
     * HashMap mapping the class of an object to be edited to EditorTypes
     * adequate for the class. The keys are Strings giving the fully qualified
     * class names, the values are Arrays of EditorTypes.
     */
    private static HashMap<String,EditorType[]> classToEditortypeMap = new HashMap<String,EditorType[]>();

    //static block for filling the HashMap
    static {
    	// TODO check if this is really necessary 
        if (classToEditortypeMap == null)
            classToEditortypeMap = new HashMap<String,EditorType[]>();
        registerEditortypesForClass(
                                    "java.lang.String",
                                    new EditorType[] {
                                                      new EditorType(
                                                                     "String",
                                                                     "de.uos.fmt.musitech.framework.editor.StringEditor",
                                                                     null),
                                                      new EditorType("Text",
                                                                     "de.uos.fmt.musitech.framework.editor.TextEditor",
                                                                     null)});
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.structure.form.NoteList",
                                    new EditorType[] {new EditorType("Notation",
                                                                     "de.uos.fmt.musitech.score.NotationDisplay", null)});
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.score.NotationSystem",
                                    new EditorType[] {new EditorType("Notation",
                                                                     "de.uos.fmt.musitech.score.NotationDisplay", null)});
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.structure.Piece",
                                    new EditorType[] {
                                                      new EditorType("Notation",
                                                                     "de.uos.fmt.musitech.score.NotationDisplay", null),
//                                                      new EditorType(
//                                                                     "PianoRoll",
//                                                                     "de.uos.fmt.musitech.performance.gui.PianoRollContainerDisplay",
//                                                                     null),
                                                      new EditorType("MetaData",
                                                                     "de.uos.fmt.musitech.metadata.ContextEditor", null)});
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.performance.MidiNoteSequence",
                                    new EditorType[] {new EditorType(
                                                                     "PianoRoll",
                                                                     "de.uos.fmt.musitech.performance.gui.PianoRollPanel",
                                                                     null)});
        //		classToEditortypeMap.put(
        //			"de.uos.fmt.musitech.data.structure.harmony.ChordSymbol",
        //			new EditorType[] {
        //				 new EditorType(
        //					"ChordSymbol",
        //					"de.uos.fmt.musitech.structure.harmony.gui.ChordSymbolDisplay",
        //					null)});
        //		classToEditortypeMap.put(
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.structure.harmony.ChordSymbol",
                                    new EditorType[] {new EditorType(
                                                                     "ChordSymbol",
                                                                     "de.uos.fmt.musitech.structure.harmony.gui.ChordSymbolDisplay2",
                                                                     null)});
        //		classToEditortypeMap.put(
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.structure.harmony.ChordSymbolSequence",
                                    new EditorType[] {new EditorType(
                                                                     "ChordSymbolSequence",
                                                                     "de.uos.fmt.musitech.structure.harmony.gui.ChordSymbSeqDisplay",
                                                                     null)});
        registerEditortypeForClass("de.uos.fmt.musitech.data.structure.harmony.ChordDegreeSymbol",
                                   new EditorType("ChordDegree",
                                                  "de.uos.fmt.musitech.structure.harmony.gui.ChordDegreeDisplay", null));
        registerEditortypeForClass("de.uos.fmt.musitech.data.structure.harmony.ChordFunctionSymbol",
                                   new EditorType("ChordFunction",
                                                  "de.uos.fmt.musitech.structure.harmony.gui.ChordFunctionDisplay",
                                                  null));
        //		classToEditortypeMap.put(
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable",
                                    new EditorType[] {new EditorType(
                                                                     "LyricsSyllable",
                                                                     "de.uos.fmt.musitech.structure.text.LyricsSyllableDisplay",
                                                                     null)});
        //		classToEditortypeMap.put(
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence",
                                    new EditorType[] {new EditorType(
                                                                     "Lyrics",
                                                                     "de.uos.fmt.musitech.structure.text.LyricsDisplay",
                                                                     null)});
        registerEditortypesForClass(
                                    de.uos.fmt.musitech.data.Named.class.getName(),
                                    new EditorType[] {new EditorType(
                                                                     "Name",
                                                                     de.uos.fmt.musitech.structure.text.NameStringDisplay.class
                                                                             .getName(), null)});
        //		classToEditortypeMap.put(
        registerEditortypesForClass("de.uos.fmt.musitech.data.media.image.ImageURL",
                                    new EditorType[] {new EditorType("Image",
                                                                     "de.uos.fmt.musitech.media.image.ImageDisplay",
                                                                     null)});

        //		classToEditortypeMap.put(
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.structure.container.BasicContainer",
                                    new EditorType[] {
                                                      new EditorType(
                                                                     "ArrangementPanel",
                                                                     "de.uos.fmt.musitech.structure.form.gui.ArrangementPanel",
                                                                     null),
                                                      new EditorType(
                                                                     "ContainerArrangeDisplay",
                                                                     "de.uos.fmt.musitech.structure.form.gui.ContainerArrangeDisplay",
                                                                     null),
                                                      //				new EditorType(
                                                      //					"Player",
                                                      //					"de.uos.fmt.musitech.framework.editor.ObjectPlayerDisplay",
                                                      //					null)
                                                      new EditorType("Notation",
                                                                     "de.uos.fmt.musitech.score.NotationDisplay", null),
                                                      new EditorType(
                                                                     "PianoRoll",
                                                                     "de.uos.fmt.musitech.performance.gui.PianoRollPanel",
                                                                     null),
                                                      new EditorType("ListView", "de.uos.fmt.musitech.framework.editor.ContainerEditor", null)});
        registerEditortypesForClass("de.uos.fmt.musitech.data.structure.container.SortedContainer",
                                    new EditorType[]{new EditorType("ListView", "de.uos.fmt.musitech.framework.editor.ContainerEditor", null)});
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.audio.AudioFileObject",
                                    new EditorType[] {
                                                      new EditorType("WaveDisplay",
                                                                     "de.uos.fmt.musitech.audio.display.WaveDisplay",
                                                                     null),
                                                      new EditorType(
                                                                     "Player",
                                                                     "de.uos.fmt.musitech.framework.editor.ObjectPlayerDisplay",
                                                                     null)});
        registerEditortypesForClass("de.uos.fmt.musitech.data.metadata.MetaDataItem",
                                    new EditorType[] {new EditorType("MetaDataItem",
                                                                     "de.uos.fmt.musitech.metadata.MetaDataItemEditor",
                                                                     null)});
        //		classToEditortypeMap.put(
        registerEditortypesForClass("de.uos.fmt.musitech.data.metadata.MetaDataCollection",
                                    new EditorType[] {new EditorType("MetaDataCollection",
                                                                     "de.uos.fmt.musitech.metadata.MetaDataEditor",
                                                                     null)});
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.structure.container.Container",
                                    new EditorType[] {new EditorType("MetaData",
                                                                     "de.uos.fmt.musitech.metadata.ContextEditor", null)});
        registerEditortypesForClass(
                                    "java.net.URL",
                                    new EditorType[] {new EditorType(
                                                                     "HTML",
                                                                     "de.uos.fmt.musitech.framework.editor.HTMLDisplay",
                                                                     null)});
        registerEditortypesForClass("de.uos.fmt.musitech.data.time.MetricalTimeLine",
                                   new EditorType[] {new EditorType(
                                                                    "MeterDisplay",
                                                                    "de.uos.fmt.musitech.time.MeterDisplay",null)});
        registerEditortypesForClass(
                                    "de.uos.fmt.musitech.data.rendering.RenderingHints",
                                    new EditorType[] {new EditorType("RenderingHints",
                                                                     "de.uos.fmt.musitech.framework.editor.RenderingHintsEditor", null)});
        registerEditortypesForClass(
        	"de.uos.fmt.musitech.framework.editor.IParameterValueProvider",
            new EditorType[] {new EditorType("ComboBox",
            	"de.uos.fmt.musitech.framework.editor.IParameterValueProvider", null)});
    }

    /**
     * Returns an Array containing the Editortypes registered for the specified
     * class. The <code>classToEditortypeMap</code> is also checked for the
     * superclasses of the class and the interfaces implemented by the class and
     * its superclasses.
     * 
     * @param cls the class for which Editortypes are requested
     * @return EditorType[] Array of the Editortypes registered for the
     *         specified class
     */
    public static EditorType[] getEditortypesForClass(Class cls) {
        //		return (EditorType[]) classToEditortypeMap.get(className);
        //Collection to take the className and the names of super classes and
        // associated interfaces
        Collection classesAndInterfaces = getClassesAndInterfaces(cls);
        Collection types = new ArrayList();
        for (Iterator iter = classesAndInterfaces.iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            if (classToEditortypeMap.get(name) != null) {
                EditorType[] mapValues = (EditorType[]) classToEditortypeMap.get(name);
                for (int i = 0; i < mapValues.length; i++) {
                    if (mapValues[i] != null)
                        types.add(mapValues[i]);
                }
            }
            ;
        }
        EditorType[] editorTypes = new EditorType[types.size()];
        int i = 0;
        for (Iterator iter = types.iterator(); iter.hasNext();) {
            EditorType element = (EditorType) iter.next();
            editorTypes[i++] = element;
        }
        return editorTypes;
    }

    /**
     * Returns a Collection containing the names of the classes and interfaces
     * associated with the class indicated by the specified String. The class
     * itself, its superclasses,and the interfaces implemented by these classes 
     * are added to the Collection by their names.
     * 
     * @param cla Class of which to get the superclasses and interfaces.
     * @return The names of the given class, superclasses, 
     * and interfaces implemented by these classes
     */
    private static Collection<String> getClassesAndInterfaces(Class<?> cla) {
        Collection<String> classNames = new ArrayList<String>();
        Collection<String> interfaceNames = new ArrayList<String>();
        while (cla != null) {
        	// add class 'cla' to the appropriate list
            if (cla.isInterface())
                interfaceNames.add(cla.getName());
            else
                classNames.add(cla.getName());
            // add the interfaces implemented by 'cla' to 
            // the interface list
            Class<?>[] interfaces = cla.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (!interfaceNames.contains(interfaces[i].getName()))
                    interfaceNames.add(interfaces[i].getName());
            }
            // move up to the superclass, if there is one
            cla = cla.getSuperclass();
        }
        classNames.addAll(interfaceNames);
        return classNames;
    }

    /**
     * Returns an Array containing Editortypes which correspond to views that
     * are Displays but not Editors. <br>
     * N.B.: Editors can be used as Displays in a "read only" mode. Here, only
     * "pure" Displays are considered.
     * 
     * @param cls The class for which the editorTypes are requested.
     * @return EditorType[] Array of Editortypes which represent Displays but
     *         not Editors
     */
    public static EditorType[] getDisplayTypesForClass(Class cls) {
        EditorType[] editorTypes = getEditortypesForClass(cls);
        Collection<EditorType> displayTypes = new ArrayList<EditorType>();
        for (int i = 0; i < editorTypes.length; i++) {
            Class<?> editorClass = getEditorClass(editorTypes[i]);
            if (Display.class.isAssignableFrom(editorClass)) {
                if (!Editor.class.isAssignableFrom(editorClass))
                    displayTypes.add(editorTypes[i]);
            }
        }
        EditorType[] types = (EditorType[]) displayTypes.toArray(new EditorType[]{});
        return types;
    }

    /**
     * Returns a String Array containing the names of the editor types
     * associated with the specified object class.
     * 
     * @param cls String giving the name of a class of an object to edit
     * @return String[] names of the editor types for the specified class
     */
    public static String[] getEditortypeNamesForClass(Class cls) {
        EditorType[] types = getEditortypesForClass(cls);
        if (types != null) {
            String[] typeNames = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                typeNames[i] = types[i].getTypeName();
            }
            return typeNames;
        }
        return null;
    }

    /**
     * Returns a Class object corresponding to the editorClassName given in the
     * specified EditorType.
     * 
     * @param editortype EditorType for which the editor class is requested
     * @return Class of the editor corresponding to the specified EditorType
     */
    private static Class getEditorClass(EditorType editortype) {
        Class cla = null;
        try {
            cla = Class.forName(editortype.getEditorClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cla;
    }

    /**
     * Registers the specified EditorType for the specified class, i.e. adds an
     * entry to the HashMap of the EditorRegistry with the
     * <code>className</code> as its key and an Array containing the
     * EditorType as its value. If the HashMap already contains the
     * <code>className</code> as a key, the <code>editortype</code> is added
     * to its registered Editortypes.
     * 
     * @param className String giving the fully qualified name of a class
     * @param editortype EditorType to be registered with the specified class
     *            name
     */
    public static void registerEditortypeForClass(String className, EditorType editortype) {
        if (editortype == null) {
            if (DebugState.DEBUG)
                System.out.println("In EditorRegistry.registerEditortypeForClass:\n"
                                   + "The argument EditorType must not be null.");
            return;
        }
        if (classToEditortypeMap.containsKey(className))
            addEditortype(className, editortype);
        else
            classToEditortypeMap.put(className, new EditorType[] {editortype});
        if (!EditorFactory.isEditor(editortype.getTypeName())) {
            if (DebugState.DEBUG)
                System.out.println("WARNING in EditorRegistry.registerEditortypeForClass:\n"
                                   + "The specified EditorType refers to a Display, but not to an Editor.");
        }
    }

    /**
     * Registers the EditorTypes in the specified Array for the object class
     * given by the String. I.e. adds an entry to the HashMap of the
     * EditorRegistry with the <code>className</code> as its key and the Array
     * <code>editortypes</code> as its value. If the HashMap already contains
     * the <code>className</code> as a key, the elements of
     * <code>editortypes</code> are added to its registered Editortypes.
     * 
     * @param className String giving the fully qualified name of a class
     * @param editortypes Array of EditorTypes to be registered with the
     *            specified class name
     */
    public static void registerEditortypesForClass(String className, EditorType[] editortypes) {
        for (int i = 0; i < editortypes.length; i++) {
            registerEditortypeForClass(className, editortypes[i]);
        }
    }

    /**
     * Adds the specified EditorType to the Editortypes registered for the
     * specified <code>className</code>. Returns true if the EditorType has
     * been added, false if there is no entry with the specified class as its
     * key.
     * 
     * @param className String giving the fully qualified name of a class
     * @param editortype EditorType to be added to the Editortypes registered
     *            for the specified class
     * @return boolean true if the EditorType is added, false if there is no
     *         entry for the specified className
     */
    private static boolean addEditortype(String className, EditorType editortype) {
        if (editortype == null) {
            if (DebugState.DEBUG)
                System.out.println("In EditorRegistry.addEditortype:\n" + "The argument EditorType must not be null.");
            return false;
        }
        if (classToEditortypeMap.containsKey(className)) {
            EditorType[] types = (EditorType[]) classToEditortypeMap.get(className);
            EditorType[] newTypes = new EditorType[((EditorType[]) types).length + 1];
            for (int i = 0; i < types.length; i++) {
                newTypes[i] = types[i];
            }
            newTypes[newTypes.length - 1] = editortype;
            classToEditortypeMap.put(className, newTypes);
            return true;
        }
        if (EditorFactory.isEditor(editortype.getTypeName())) {
            if (DebugState.DEBUG)
                System.out.println("WARNING in EditorRegistry.addEditortype:\n"
                                   + "The specified EditorType refers to a Display, but not to an Editor.");
        }
        return false;
    }

    /**
     * Returns true, if the EditorType indicated by the specified
     * <code>editortypeName</code> is registered for the specified Object. If
     * the HashMap of the EditorRegistry does not contain the
     * <code>className</code> as a key, or if this key does not lead to an
     * EditorType of the specified name, false is returned.
     * 
     * @param className String giving the fully qualified name of a class
     * @param editortypeName String giving the <code>typeName</code> of an
     *            EditorType
     * @return boolean true if the specified <code>className</code> leads to
     *         an EditorType of the specified <code>editortypeName</code>,
     *         false otherwise
     */
    public static boolean isAssociatedEditortype(String className, String editortypeName) {
        if (classToEditortypeMap.containsKey(className)) {
            EditorType[] value = (EditorType[]) classToEditortypeMap.get(className);
            for (int i = 0; i < value.length; i++) {
                if (value[i].getTypeName().equals(editortypeName))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the EditorType with the specified <code>typeName</code>.
     * Returns null, if there is not EditorType of the specified name among the
     * values of the EditorRegistry's HashMap.
     * 
     * @param typeName String giving the name ot the requested EditorType
     * @return EditorType with the specified name, or null
     */
    private static EditorType getEditortypeByName(String typeName) {
        Collection editortypes = classToEditortypeMap.values();
        for (Iterator iter = editortypes.iterator(); iter.hasNext();) {
            EditorType[] element = (EditorType[]) iter.next();
            for (int i = 0; i < element.length; i++) {
                if (element[i] != null && element[i].getTypeName().equals(typeName))
                    return element[i];
            }
        }
        return null;
    }

    /**
     * Returns the class name of the editor corresponding to the specified
     * <code>typeName</code>.
     * 
     * @param typeName String indicating the editory type
     * @return String giving the name of the editor class
     */
    public static String getEditorClassName(String typeName) {
        EditorType type = getEditortypeByName(typeName);
        if (type != null)
            return type.getEditorClassName();
        else
            return null;
    }

    /**
     * Returns true, if the <code>classToEditortypeMap</code> contains an
     * EditorType whose <code>editorClassName</code> matches the specified
     * String.
     * 
     * @param className String indicating the name of the editor class to be
     *            checked if registered
     * @return true if the specified class name is an
     *         <code>editorClassName</code> of one of the Editorytypes in the
     *         <code>classToEditorTypeMap</code>
     */
    public static boolean isRegisteredEditorClass(String className) {
        return getAllRegisteredEditorClasses().contains(className);
    }

    /**
     * Returns a Collection containing the names of all editor classes known to
     * the registry. That is, the Collection contains the
     * <code>editorClassName</code> of the elements of all values (of type
     * EditorType[]) in the <code>classToEditortypeMap</code>.
     * 
     * @return Collection containing the names of all editor classes known to
     *         the EditorRegistry
     */
    public static Collection getAllRegisteredEditorClasses() {
        Collection<String> editorClassNames = new ArrayList<String>();
        Collection values = classToEditortypeMap.values();
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            EditorType[] element = (EditorType[]) iter.next();
            for (int i = 0; i < element.length; i++) {
                editorClassNames.add(element[i].getEditorClassName());
            }
        }
        return editorClassNames;
    }

    /**
     * Returns a Collection containing all EditorTypes known to the
     * EditorRegistry. The method iterates all values of the
     * <code>classToEditortypeMap</code> (which are Arrays of EditorTypes).
     * Only EditorTypes which are not already contained in the Collection are
     * added. I.e. the returned Collection will not contain any EditorType more
     * than once.
     * 
     * @return Collection containing the EditorTypes knwon to the EditorRegistry
     */
    public static Collection getAllRegisteredEditorTypes() {
        Collection editorTypes = new ArrayList();
        Collection values = classToEditortypeMap.values();
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            EditorType[] element = (EditorType[]) iter.next();
            for (int i = 0; i < element.length; i++) {
                if (element[i] instanceof EditorType && !editorTypes.contains(element[i])) {
                    editorTypes.add(element[i]);
                }
            }
        }
        return editorTypes;
    }

    /**
     * Removes the specified EditorType from the EditorTypes registered for the
     * specified <code>className</code>.
     * 
     * @param className String indicating the class name of an object to edit
     *            (key in the HashMap)
     * @param editorType EditorType to be removed from the EditorTypes
     *            registered for the specified String
     */
    public static void unregisterEditortypeForClass(String className, EditorType editorType) {
        if (editorType == null)
            return;
        if (classToEditortypeMap.containsKey(className)) {
            EditorType[] values = (EditorType[]) classToEditortypeMap.get(className);
            List typesAsList = Arrays.asList(values);
            if (typesAsList.contains(editorType))
                typesAsList.remove(editorType);
            EditorType[] newValues = (EditorType[]) typesAsList.toArray();
            classToEditortypeMap.put(className, newValues);
        } else {
            if (DebugState.DEBUG)
                System.out.println("In EditorRegistry.unregisterEditortypeForClass:\n"
                                   + "The specified String is not registered as a class name.");
        }
    }

    /**
     * Removes the specified <code>typeName</code> from the editor type names
     * registered for the specified <code>className</code>.
     * 
     * @param className String giving the class name of an object to edit
     * @param typeName String giving the name of an editor type
     */
    public static void unregisterEditorTypeForClass(String className, String typeName) {
        if (typeName == null)
            return;
        if (classToEditortypeMap.containsKey(className)) {
            EditorType[] values = (EditorType[]) classToEditortypeMap.get(className);
            List typesAsList = new ArrayList();
            for (int i = 0; i < values.length; i++) {
                if (!values[i].getTypeName().equals(typeName)) {
                    typesAsList.add(values[i]);
                }
            }
            if (typesAsList.isEmpty()) {
                classToEditortypeMap.remove(className);
            } else {
                EditorType[] newValues = (EditorType[]) typesAsList.toArray(new EditorType[]{});
                classToEditortypeMap.put(className, newValues);
            }
        }
    }

}