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
package de.uos.fmt.musitech.framework.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * A class to copy objects and write/read them to/from XML files.
 * 
 * @author TW
 * @version 0.113
 */
public class MusiteXMLSerializer {

    private static Map primitiveWrapperHashtable = new HashMap();
    private static Map sigClassHashtable = new HashMap();
    private static Map classSigHashtable = new HashMap();
    private static Set elementaryTypes = new HashSet();
    private static Set primitiveTypes = new HashSet();
    public static Map toXML = new HashMap();
    public static Map fromXML = new HashMap();
    private static Map interfaceImplementation = new HashMap();
    private boolean mpegMode = false;

    static {
        primitiveWrapperHashtable.put(int.class, Integer.class);
        primitiveWrapperHashtable.put(long.class, Long.class);
        primitiveWrapperHashtable.put(short.class, Short.class);
        primitiveWrapperHashtable.put(byte.class, Byte.class);
        primitiveWrapperHashtable.put(boolean.class, Boolean.class);
        primitiveWrapperHashtable.put(char.class, Character.class);
        primitiveWrapperHashtable.put(double.class, Double.class);
        primitiveWrapperHashtable.put(float.class, Float.class);

        classSigHashtable.put("I", Integer.class);
        classSigHashtable.put("J", Long.class);
        classSigHashtable.put("S", Short.class);
        classSigHashtable.put("B", Byte.class);
        classSigHashtable.put("Z", Boolean.class);
        classSigHashtable.put("C", Character.class);
        classSigHashtable.put("D", Double.class);
        classSigHashtable.put("F", Float.class);

        sigClassHashtable.put(Integer.class, "I");
        sigClassHashtable.put(Long.class, "J");
        sigClassHashtable.put(Short.class, "S");
        sigClassHashtable.put(Byte.class, "B");
        sigClassHashtable.put(Boolean.class, "Z");
        sigClassHashtable.put(Character.class, "C");
        sigClassHashtable.put(Double.class, "D");
        sigClassHashtable.put(Float.class, "F");

        elementaryTypes.add(Integer.class);
        elementaryTypes.add(Long.class);
        elementaryTypes.add(Short.class);
        elementaryTypes.add(Byte.class);
        elementaryTypes.add(Boolean.class);
        elementaryTypes.add(Character.class);
        elementaryTypes.add(Double.class);
        elementaryTypes.add(Float.class);
        elementaryTypes.add(String.class);
        elementaryTypes.add(java.io.File.class);
        elementaryTypes.add(java.net.URL.class);
        elementaryTypes.add(java.awt.Color.class);

        primitiveTypes.add(int.class);
        primitiveTypes.add(long.class);
        primitiveTypes.add(short.class);
        primitiveTypes.add(byte.class);
        primitiveTypes.add(boolean.class);
        primitiveTypes.add(char.class);
        primitiveTypes.add(double.class);
        primitiveTypes.add(float.class);

        /*
         * mapping between element names and classes
         */
        fromXML.put("arrayList", java.util.ArrayList.class);
        fromXML.put("basicContainer", de.uos.fmt.musitech.data.structure.container.BasicContainer.class);
        fromXML.put("context", de.uos.fmt.musitech.data.structure.Context.class);
        fromXML.put("keyMarker", de.uos.fmt.musitech.data.structure.harmony.KeyMarker.class);
        fromXML.put("rational", de.uos.fmt.musitech.utility.math.Rational.class);
        fromXML.put("scoreNote", de.uos.fmt.musitech.data.score.ScoreNote.class);
        fromXML.put("timedMetrical", de.uos.fmt.musitech.data.time.TimedMetrical.class);
        fromXML.put("timeLine", de.uos.fmt.musitech.time.TimeLine.class);
        fromXML.put("timeSignature", de.uos.fmt.musitech.data.time.TimeSignature.class);
        fromXML.put("timeSignatureMarker", de.uos.fmt.musitech.data.time.TimeSignatureMarker.class);
        fromXML.put("audioFileObject", de.uos.fmt.musitech.data.audio.AudioFileObject.class);
        fromXML.put("metaDataItem", de.uos.fmt.musitech.data.metadata.MetaDataItem.class);
        fromXML.put("metaValue", de.uos.fmt.musitech.data.metadata.MetaDataValue.class);
        fromXML.put("midiNote", de.uos.fmt.musitech.data.performance.MidiNote.class);
        fromXML.put("part", de.uos.fmt.musitech.data.structure.linear.Part.class);
        fromXML.put("note", de.uos.fmt.musitech.data.structure.Note.class);
        fromXML.put("piece", de.uos.fmt.musitech.data.structure.Piece.class);
        fromXML.put("metricalTimeLine", de.uos.fmt.musitech.data.time.MetricalTimeLine.class);
        fromXML.put("string", java.lang.String.class);
        fromXML.put("hashMap", java.util.HashMap.class);
        fromXML.put("audioFormat", javax.sound.sampled.AudioFormat.class);
        fromXML.put("performanceNote", de.uos.fmt.musitech.data.performance.PerformanceNote.class);
        fromXML.put("noteList", de.uos.fmt.musitech.data.structure.form.NoteList.class);
        fromXML.put("midiTimedMessage", de.uos.fmt.musitech.performance.midi.MidiTimedMessage.class);
        fromXML.put("voice", de.uos.fmt.musitech.data.structure.linear.Voice.class);
        fromXML.put("staffContainer", de.uos.fmt.musitech.data.structure.container.StaffContainer.class);
        fromXML.put("barlineContainer", de.uos.fmt.musitech.data.score.BarlineContainer.class);
        fromXML.put("slurContainer", de.uos.fmt.musitech.data.score.SlurContainer.class);
        fromXML.put("clef", de.uos.fmt.musitech.data.score.Clef.class);
        fromXML.put("notationStaffConnector", de.uos.fmt.musitech.data.score.NotationStaffConnector.class);
        fromXML.put("barlineContainer", de.uos.fmt.musitech.data.score.BarlineContainer.class);
        fromXML.put("beamContainer", de.uos.fmt.musitech.data.score.BeamContainer.class);
        fromXML.put("metricAttachable", de.uos.fmt.musitech.data.score.MetricAttachable.class);
        fromXML.put("barline", de.uos.fmt.musitech.data.score.Barline.class);
        fromXML.put("smr", de.uos.fmt.musitech.data.score.Barline.class);

        interfaceImplementation.put(java.util.List.class, java.util.ArrayList.class);
        interfaceImplementation.put(de.uos.fmt.musitech.data.structure.container.Container.class,
                                    de.uos.fmt.musitech.data.structure.container.BasicContainer.class);
        interfaceImplementation.put(java.util.Map.class, java.util.HashMap.class);
        interfaceImplementation.put(de.uos.fmt.musitech.audio.floatStream.FloatInputStream.class,
                                    java.util.HashMap.class);

        toXML.put(de.uos.fmt.musitech.data.structure.linear.Voice.class, "voice");
        toXML.put(de.uos.fmt.musitech.data.structure.container.StaffContainer.class, "staffContainer");
        toXML.put(de.uos.fmt.musitech.data.score.BarlineContainer.class, "barlineContainer");
        toXML.put(de.uos.fmt.musitech.data.score.SlurContainer.class, "slurContainer");
        toXML.put(de.uos.fmt.musitech.data.score.Clef.class, "clef");
        toXML.put(de.uos.fmt.musitech.data.score.NotationStaffConnector.class, "notationStaffConnector");
        toXML.put(de.uos.fmt.musitech.data.score.Barline.class, "barline");
        toXML.put(de.uos.fmt.musitech.data.score.BarlineContainer.class, "barlineContainer");
        toXML.put(de.uos.fmt.musitech.data.score.BeamContainer.class, "beamContainer");
        toXML.put(de.uos.fmt.musitech.data.score.MetricAttachable.class, "metricAttachable");
        toXML.put(de.uos.fmt.musitech.performance.midi.MidiTimedMessage.class, "midiTimedMessage");
        toXML.put(de.uos.fmt.musitech.data.score.ScoreNote.class, "scoreNote");
        toXML.put(de.uos.fmt.musitech.data.structure.container.BasicContainer.class, "basicContainer");
        toXML.put(de.uos.fmt.musitech.data.structure.Context.class, "context");
        toXML.put(de.uos.fmt.musitech.data.structure.harmony.KeyMarker.class, "keyMarker");
        toXML.put(de.uos.fmt.musitech.data.time.TimedMetrical.class, "timedMetrical");
        toXML.put(de.uos.fmt.musitech.data.time.TimeSignature.class, "timeSignature");
        toXML.put(de.uos.fmt.musitech.data.time.TimeSignatureMarker.class, "timeSignatureMarker");
        toXML.put(de.uos.fmt.musitech.time.TimeLine.class, "timeLine");
        toXML.put(de.uos.fmt.musitech.utility.math.Rational.class, "rational");
        toXML.put(java.util.ArrayList.class, "arrayList");
        toXML.put(de.uos.fmt.musitech.data.audio.AudioFileObject.class, "audioFileObject");
        toXML.put(de.uos.fmt.musitech.data.metadata.MetaDataItem.class, "metaDataItem");
        toXML.put(de.uos.fmt.musitech.data.metadata.MetaDataValue.class, "metaValue");
        toXML.put(de.uos.fmt.musitech.data.performance.MidiNote.class, "midiNote");
        toXML.put(de.uos.fmt.musitech.data.structure.linear.Part.class, "part");
        toXML.put(de.uos.fmt.musitech.data.structure.Note.class, "note");
        toXML.put(de.uos.fmt.musitech.data.structure.Piece.class, "piece");
        toXML.put(de.uos.fmt.musitech.data.time.MetricalTimeLine.class, "metricalTimeLine");
        toXML.put(de.uos.fmt.musitech.data.performance.PerformanceNote.class, "performanceNote");
        toXML.put(java.lang.String.class, "string");
        toXML.put(java.util.HashMap.class, "hashMap");
        toXML.put(javax.sound.sampled.AudioFormat.class, "audioFormat");
        toXML.put(de.uos.fmt.musitech.data.structure.form.NoteList.class, "noteList");
    }

    public MusiteXMLSerializer() {
    }

    /**
     * Returns a class for a type name from a hashTable.
     * 
     * @date (21.10.00 15:53:36)
     * @return java.lang.String
     * @param sig_char
     *            char
     */
    static private Class getPrimitiveWrapper(Class type) {
        Class wrapper = (Class) primitiveWrapperHashtable.get(type);
        if (wrapper == null)
            return type;
        else
            return wrapper;
    }

    /**
     * Returns a type signature string for a class from a hashTable.
     * 
     * @return java.lang.String
     * @param sig_char
     *            char
     */
    static private Class classForSig(String sigStr) {
        return (Class) classSigHashtable.get(sigStr);
    }

    /**
     * Returns a class for a type from a hashTable.
     * 
     * @date (21.10.00 15:53:36)
     * @return java.lang.String
     * @param sig_char
     *            char
     */
    static private String sigForClass(Class cls) {
        return (String) sigClassHashtable.get(cls);
    }

    /**
     * Copies an object.
     * 
     * @date (21.07.00 11:57:54)
     * @return java.lang.Object
     * @param object
     *            java.io.Serializable
     */
    static Object copySerializable(java.io.Serializable obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            baos.close();
            byte byteArray[] = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object newObject = ois.readObject();
            ois.close();
            bais.close();
            return newObject;
        } catch (Exception e) {
            if (DebugState.DEBUG)
                e.printStackTrace();
            System.out.println("MusiteXMLSerializer.copySerializable: " + obj.getClass()
                               + " throws Exception while serializing.");
            System.out.println(e);
            return null;
        }
    }

    static Object emptyObjectArray[] = new Object[] {};
    /**
     * contains all serialized objects (key) and the corresponding nodes (value)
     */
    private Hashtable serialized;
    /**
     * contains serialized objects that are just references (key) and the
     * corresponding nodes (value) subset of field 'Hashtable serialized'
     */
    private Hashtable referencing;
    /**
     * contains the id (value) of a given object (key)
     */
    private Hashtable objectIds;

    private Document outputDOM;
    /**
     * counter of the current assigned id for referencing
     */
    private int objId;
    private Hashtable objectHash;

    /**
     * Returns a new document,
     */
    static private Document newDOM() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads from xml file.
     * 
     * @date (21.10.00 14:52:57)
     * @return java.lang.Object
     * @param document
     *            javax.swing.text.Document
     */
    private Object readXML(Document document) {
        Node root = document.getDocumentElement();
        if (isMpegMode()) {
            if (!"smr".equals(root.getNodeName()))
                return null;
        } else {
            if (!"MusiteXML".equals(root.getNodeName()))
                return null;
        }
        // TODO ??? serialize smr to MusicCollection if more than one child
        // (piece)
        Element child = XMLHelper.getFirstElementChild(root);
        child.normalize();
        // initialize object-hash
        objectHash = new Hashtable();
        // deserialize
        Object deserialized = readXML(child);
        // free object-hash
        objectHash = null;

        return deserialized;
    }

    /**
     * Reads from xml file.
     * 
     * @date (21.10.00 15:02:07)
     * @return java.lang.Object
     * @param node
     *            org.w3c.dom.Node
     */
    private Object readXML(Element node) {
        return readXML(node, null);
    }

    private static Class getClass(Element node) {
        String nodeName = node.getNodeName();
        //if (nodeName.equals("object"))
        //    nodeName = node.getAttribute("class");
        if (node.hasAttribute("class")) {
            String className = node.getAttribute("class");
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        Class c = getClassName(nodeName);
        if (c != null)
            return c;

        c = classForSig(nodeName);
        if (c != null)
            return c;

        System.out.println("Warning: could not find class for name " + nodeName);
        return null;
    }

    /**
     * This is the main method for reading Java Objects from a DOM.
     * 
     * @return deserialized object
     * @param node
     *            the element to be deserialized
     */
    public Object readXML(Element node, Class objectClass) {
        if (node == null)
            return null;
        /*
         * handle object-elements
         */
        Class c;
        if (objectClass == null)
            c = getClass(node);
        else
            c = objectClass;

        if (c == null)
            return null;

        // run fromXML() if class implements IXMLSerializable and has a conform
        // fromXML()-method

        if (isMpegMode() && IMPEGSerializable.class.isAssignableFrom(c)) {
            try {
                Constructor constructor = c.getConstructor(null);
                Object cInstance = constructor.newInstance(null);
                return ((IMPEGSerializable) cInstance).fromMPEG(this, node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (IXMLSerializable.class.isAssignableFrom(c)) {
            try {
                Constructor constructor = c.getConstructor(null);
                Object cInstance = constructor.newInstance(null);
                return ((IXMLSerializable) cInstance).fromXML(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fromXML(node, c);
    }

    /**
     * @param class1
     * @param fieldname
     * @return
     */
    private static Field getFieldIgnoreCase(Class class1, String fieldname) {
        Field[] fields = class1.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getName().equalsIgnoreCase(fieldname)) {
                return field;
            }
        }
        return null;
    }

    /**
     * @param nodeName
     * @return
     */
    private static Class getClassName(String nodeName) {
        Class c = (Class) fromXML.get(nodeName);
        return c;
    }

    /**
     * This is the main method for reading Java Objects from a DOM.
     * 
     * @param node
     *            node to deserialize
     * @param objectHash
     *            already deserialized objects
     * @param object
     *            the object-instance to deserialize the node to
     * @param objectClass
     *            the class of the object-instance
     * @return java.lang.Object
     */
    public Object fromXML(Element node, Class objectClass) {
        Class c;
        Object object = null;
        if (objectClass == null || isPrimitiveClass(objectClass) || node.hasAttribute("class"))
            c = getClass(node);
        else
            c = objectClass;

        if (c != null && isElementaryClass(c) && !(new Boolean(true).toString().equals(node.getAttribute("isArray")))) {
            object = readPrimitive(node);
            if (object != null)
                return object;
        }

        boolean isArray = new Boolean(node.getAttribute("isArray")).booleanValue();
        boolean isCollection = new Boolean(node.getAttribute("isCollection")).booleanValue();
        boolean isMap = new Boolean(node.getAttribute("isMap")).booleanValue();
        boolean isReference;
        if (node.hasAttribute("ref")) {
            isReference = true;
        } else {
            isReference = false;
        }
        //boolean isReference = new
        // Boolean(node.getAttribute("isReference")).booleanValue();
        if (object != null && !(c.isInstance(object)))
            return null;
        Element child = XMLHelper.getFirstElementChild(node);
        String idString;
        if (isReference) {
            // Object is already known
            idString = c.getName() + node.getAttribute("ref");
            object = objectHash.get(idString);
        } else
            idString = node.getAttribute("id");
        // new object
        notReference: {
            if (!isArray && (elementaryTypes.contains(c) || c == String.class)) {
                // Read an elementary type.
                Node textNode = node.getFirstChild();
                if (textNode != null) {
                    if (textNode.getNodeType() == Node.TEXT_NODE) {
                        // this is a text node
                        object = getPrimitive(c, textNode.getNodeValue());
                    }
                }
                if (object != null)
                    objectHash.put(idString, object);
                break notReference;
            } else if (isArray) {
                // read an array
                int arrayLength = new Integer(node.getAttribute("arrayLength")).intValue();
                assert c.isArray();
                object = Array.newInstance(c.getComponentType(), arrayLength);
                objectHash.put(idString, object);
                int i = 0;
                if ("arrayElements".equals(child.getNodeName())) {
                    Element grandChild = XMLHelper.getFirstElementChild(child);
                    while (grandChild != null) {
                        Object tmp = readXML(grandChild);
                        Array.set(object, i++, tmp);
                        grandChild = XMLHelper.getNextElementSibling(grandChild);
                    }
                    child = XMLHelper.getNextElementSibling(child);
                }
                while (child != null) {
                    if ("arrayElement".equals(child.getNodeName()))
                        Array.set(object, i, readXML(XMLHelper.getFirstElementChild(child)));
                    child = XMLHelper.getNextElementSibling(child);
                }
            } else { // not array && not elementary type
                // get a new object if necessary
                if (object == null) {
                    if (c.isInterface()) {
                        Class cimpl = getImplementation(c);
                        if (cimpl == null) {
                            System.out.println("[ERROR] " + c
                                               + " is an Interface, a mapping to a class must be defined");
                            return null;
                        } else
                            c = cimpl;
                    }
                    try {
                        object = c.newInstance();
                    } catch (Exception e) {
                        System.out.println("[ERROR] can not instantiate class " + c.getName());
                        e.printStackTrace();
                        return null;
                    }
                }
                objectHash.put(idString, object);
                if (isMap) { // Object is a map
                    Map map = (Map) object;
                    // this is the new way to code a map
                    while (child != null) {
                        if ("mapElements".equals(child.getNodeName())) {
                            Element grandChild = XMLHelper.getFirstElementChild(child);
                            while (grandChild != null) {
                                Object key = readXML(grandChild);
                                grandChild = XMLHelper.getNextElementSibling(grandChild);
                                if (grandChild == null) {
                                    System.out.println("WARNING: map value for key " + key + " is null.");
                                } else {
                                	Object value = readXML(grandChild);
                                	map.put(key, value);
                                	grandChild = XMLHelper.getNextElementSibling(grandChild);
                                }
                            }
                        } // this is for backward compatibility
                        else if (child != null && "mapElement".equals(child.getNodeName())) {
                            Object key = null, value = null;
                            Element keyOrValue = XMLHelper.getFirstElementChild(child);
                            while (keyOrValue != null) {
                                Node contentNode = XMLHelper.getFirstElementChild(keyOrValue);
                                Object contentObject;
                                contentObject = readXML((Element) contentNode);
                                if ("key".equals(keyOrValue.getNodeName()))
                                    key = contentObject;
                                if ("value".equals(keyOrValue.getNodeName()))
                                    value = contentObject;
                                keyOrValue = XMLHelper.getNextElementSibling(keyOrValue);
                            }
                            map.put(key, value);
                            child = XMLHelper.getNextElementSibling(child);
                        } else {
                            String fieldName = child.getAttribute("field-name");
                            Object value = readXML(child);
                            try {
                                setField(object, fieldName, value);
                            } catch (Throwable t) {
                            }
                        }
                        child = XMLHelper.getNextElementSibling(child);
                    }
                } else if (isCollection) {
                    java.util.Collection coll = (java.util.Collection) object;
                    while (child != null) {
                        coll.add(readXML(child));
                        /*
                         * if ("collElements".equals(child.getNodeName())) {
                         * Element grandChild =
                         * XMLHelper.getFirstElementChild(child); while
                         * (grandChild != null) { coll.add(readXML(grandChild,
                         * objectHash)); grandChild =
                         * XMLHelper.getNextElementSibling(grandChild); } } else {
                         * String fieldName = child.getAttribute("field-name");
                         * Object value = readXML(child, objectHash); try {
                         * setField(object, fieldName, value); } catch
                         * (Throwable t) { } }
                         */
                        child = XMLHelper.getNextElementSibling(child);
                    }
                }
            }// end if/else elementary-array-regular
            // Read primitive-field attributes of the current node
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attr = (Attr) attributes.item(i);
                String attrName = attr.getName();
                if (attrName == "isArray" || attrName == "isMap" || attrName == "isCollection" || attrName == "ref"
                    || attrName == "id") {
                    continue;
                }
                Method setter = getMethodIgnoreCase(c, "set" + attrName);
                if (setter != null) {
                    Class attrClass = getPrimitiveWrapper(setter.getParameterTypes()[0]);
                    Object content = getPrimitive(attrClass, attr.getValue());
                    setField(object, attrName, content);
                } else {
                    Field field = getFieldIgnoreCase(c, attrName);
                    if (field != null) {
                        Class attrClass = getPrimitiveWrapper(field.getType());
                        Object content = getPrimitive(attrClass, attr.getValue());
                        setField(object, field.getName(), content);
                    }
                }
            }
            // Read the children of the current node
            while (child != null) {
                /*
                 * if ("field".equals(child.getNodeName())) { String fieldName =
                 * child.getAttribute("name"); //String type =
                 * child.getAttribute("type"); Object value = null; Element
                 * grandChild = XMLHelper.getFirstElementChild(child); if
                 * (grandChild != null) value = readXML(grandChild, objectHash);
                 * try { setField(object, fieldName, value); } catch (Throwable
                 * t) { } child = XMLHelper.getNextElementSibling(child); } else {
                 */
                String fieldName = child.getAttribute("field-name");
                if (fieldName.length() > 0) {
                    Object value = readXML(child);
                    try {
                        setField(object, fieldName, value);
                    } catch (Throwable t) {
                    }
                    child = XMLHelper.getNextElementSibling(child);
                } else {
                    String nodename = child.getNodeName();
                    /*
                     * handle fieldname-elements
                     */
                    //Method setter =
                    // getMethodIgnoreCase(object.getClass(), "set" +
                    // nodename);
                    //if (setter != null) {
                    //    Object value = readXML((Element) objectElement,
                    // objectHash);
                    //    Method setter =
                    // getMethodIgnoreCase(object.getClass(), "set" +
                    // nodename);
                    //else {
                    Method setter = getMethodIgnoreCase(c, "set" + nodename);
                    if (setter != null) {
                        Class childClass = setter.getParameterTypes()[0];
                        Object value = readXML(child, childClass);
                        setField(object, nodename, value);
                    } else {
                        Field field = getFieldIgnoreCase(object.getClass(), nodename);
                        if (field != null) {
                            Object value = readXML(child, field.getClass());
                            setField(object, field.getName(), value);
                        }
                    }
                    child = XMLHelper.getNextElementSibling(child);
                }
            }
        } // end notReference
        return object;
    }

    /**
     * @param objectClass
     * @return
     */
    private static boolean isPrimitiveClass(Class class1) {
        return primitiveTypes.contains(class1);
    }

    /**
     * @param c
     * @return
     */
    private static Class getImplementation(Class c) {
        return (Class) interfaceImplementation.get(c);
    }

    /**
     * @param class1
     * @param methodName
     * @return
     */
    private static Method getMethodIgnoreCase(Class class1, String methodName) {
        Method[] methods = class1.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equalsIgnoreCase(methodName))
                return method;
        }
        return null;
    }

    /**
     * @param primClass
     * @param value
     * @return
     */
    private static Object getPrimitive(Class primClass, String value) {
        if (primClass == String.class || primClass == Object.class)
            return value;
        else if (primClass == Character.class)
            return new Character(value.charAt(0));
        else {
            final Class sca[] = new Class[] {String.class};
            try {
                return primClass.getConstructor(sca).newInstance(new Object[] {value});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Sets the field 'name' in the 'object' to 'value'.
     * 
     * @date (21.10.00 18:33:34)
     * @return boolean
     * @param object
     *            java.langObject
     * @param name
     *            java.lang.String
     * @param value
     *            java.lang.Object
     * @param type
     *            java.lang.String
     */
    private static boolean setField(Object object, String name, Object value) {
        Class c = object.getClass();
        Field fields[] = c.getFields(), field = null;
        Method methods[] = c.getMethods(), method = null;

        // get the class of the value object
        Class valueClass = null;
        if (value != null) {
            valueClass = value.getClass();
            /*
             * try { valueClass = Class.forName(type); } catch (Exception e) {
             * valueClass = value.getClass(); }
             */
        } else
            // value was null
            return false;

        // 	Try to find the field called name.
        for (int i = 0; i < fields.length; i++) {
            if (name.equals(fields[i].getName())) {
                field = fields[i];
                break;
            }
        }

        // if field found, set it.
        if (field != null) {
            try {
                field.set(object, value);
                // success
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if field not found, try to find an appropriate set-method.
        StringBuffer sb = new StringBuffer();
        sb.append("set");
        sb.append(name.substring(0, 1).toUpperCase());
        sb.append(name.substring(1));
        String setName = sb.toString();
        for (int i = 0; i < methods.length; i++) {
            if (setName.equals(methods[i].getName())) {
                Class[] paramClasses = methods[i].getParameterTypes();
                if (paramClasses.length > 1) //protect against polymorphic
                    // setters
                    continue;
                Class paramClass = methods[i].getParameterTypes()[0];
                if (paramClass.isPrimitive())
                    paramClass = getPrimitiveWrapper(paramClass);
                if (valueClass != null && paramClass.isAssignableFrom(valueClass)) {
                    method = methods[i];
                    break;
                }
            }
        }
        if (method != null) {
            try {
                method.invoke(object, new Object[] {value});
                // success
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // failed
        return false;
    }

    /**
     * Write an xml file.
     * 
     * @date (31.07.00 00:13:50)
     * @return boolean
     * @param object
     *            java.lang.Object
     * @param dest
     *            java.lang.Object
     */
    private synchronized boolean writeXML(Object object) {
        // basic document structure
        Element root;
        if (isMpegMode()) {
            root = this.outputDOM.createElement("smr");
        } else {
            root = this.outputDOM.createElement("MusiteXML");
        }
        this.outputDOM.appendChild(root);

        // initialize indexing-objects
        this.serialized = new Hashtable();
        this.referencing = new Hashtable();
        this.objectIds = new Hashtable();
        this.objId = 0;

        // main serialization
        boolean success = writeXML(root, object);
        // add id- and reference-tags
        generateIdentityTags();

        // free indexing-objects
        this.serialized = null;
        this.referencing = null;
        this.objectIds = null;
        return success;
    }

    /**
     * Write an xml file.
     * 
     * @date (31.07.00 00:13:50)
     * @return boolean
     * @param object
     *            java.lang.Object
     * @param dest
     *            java.lang.Object
     */
    private synchronized boolean writeXML(Node parent, Object object) {
        return writeXML(parent, object, null, null);
    }

    public synchronized boolean writeXML(org.w3c.dom.Node parent, Object object, String fieldname, String fieldClass) {
        if (object == null)
            return true;
        // zum Testen, ob parent-Parameter nicht gleich Element seien sollte
        assert (parent instanceof Element);
        if (this.mpegMode && object instanceof IMPEGSerializable) {
            if (fieldname != null) {
                Element fieldElement = this.outputDOM.createElement(fieldname);
                parent.appendChild(fieldElement);
                return ((IMPEGSerializable) object).toMPEG(this, fieldElement, object, fieldname);
            } else
                return ((IMPEGSerializable) object).toMPEG(this, parent, object, fieldname);
        } else if (object instanceof IXMLSerializable) {
            if (fieldname != null) {
                Element fieldElement = this.outputDOM.createElement(fieldname);
                parent.appendChild(fieldElement);
                return ((IXMLSerializable) object).toXML(fieldElement, object, fieldname);
            } else
                return ((IXMLSerializable) object).toXML(parent, object, fieldname);
        } else
            return toXML(parent, object, fieldname, fieldClass);
    }

    /**
     * Main method for writing Java objects into an XML DOM.
     * 
     * @param parent
     * @param object
     * @param hashtable
     * @param document
     * @param fieldname
     * @param fieldClass
     * @return
     */
    private synchronized boolean toXML(org.w3c.dom.Node parent, Object object, String fieldname, String fieldClass) {
        // the element to be written
        Element objElement;
        // case null object
        if (object == null) {
            //			objElement.setAttribute("id", "null");
            //			parent.appendChild(objElement);
            return true;
        } else {
            // object not null
            // get the class
            Class c = object.getClass();
            // and ist name
            String classname = c.getName();
            if (DebugState.DEBUG)
                System.err.println(classname);
            // store the class-name of the current object if it is different
            // from the class in the field-declaration (ignore primitive
            // types/wrappers)
            boolean storeInstanceClassName = (fieldClass != null && !classname.equals(fieldClass))
                                             && !(isElementaryType(object) || isPrimitiveClass(object.getClass()));
            // look for arrays
            if (classname.startsWith("[")) {
                while (classname.startsWith("["))
                    classname = classname.substring(1);
                if (classname.length() == 1) {
                    // this is an Array of primitives no action necessary
                } else {
                    // we need to remove the leading 'L'
                    // and the trailing ';'
                    classname = classname.substring(1, classname.length() - 1);
                }
            }
            // check if additional class-information are necessary
            // write primitves
            if ((isElementaryType(object) || isPrimitiveClass(object.getClass())) && !storeInstanceClassName) {
                return writePrimitive(parent, object, fieldname);
            }
            // else create element
            if (fieldname != null) {
                objElement = (Element) parent.appendChild(this.outputDOM.createElement(fieldname));
            } else
                objElement = (Element) parent.appendChild(this.outputDOM.createElement(getTagName(c)));
            if (storeInstanceClassName) {
                objElement.setAttribute("class", object.getClass().getName());
            }

            // do we know this Object?
            boolean isReference = false;
            if (serialized.containsKey(object)) {
                // if so make a reference
                referencing.put(object, objElement);
                isReference = true;
            } else {
                // else add to written objects
                serialized.put(object, objElement);
                // write the object ID
                objectIds.put(object, new Long(objId++));
            }
            if (isReference)
                return true;
            if (object.getClass().isArray()) {
                // object is an Array
                objElement.setAttribute("isArray", "true");
                objElement.setAttribute("arrayLength", Integer.toString(Array.getLength(object)));
                //				objElement.setAttribute("class", classname);
                Element arrayElement = this.outputDOM.createElement("arrayElements");
                for (int i = 0; i < Array.getLength(object); i++) {
                    writeXML(arrayElement, Array.get(object, i));
                }
                objElement.appendChild(arrayElement);
            } else {
                // non Array
                // regular object
                if (elementaryTypes.contains(c) || c == String.class) {
                    // primitive or String
                    objElement.appendChild(this.outputDOM.createTextNode(object.toString()));
                    return true;
                }
                regular_object: {
                    // hash set for field names
                    java.util.HashSet hsNames = new java.util.HashSet();
                    // get all private fields
                    Field fields[] = object.getClass().getFields();
                    // and write them
                    for (int i = 0; i < fields.length; i++) {
                        // no tansient or final fields
                        String name = fields[i].getName();
                        if (Modifier.isFinal(fields[i].getModifiers())
                            || Modifier.isTransient(fields[i].getModifiers())) {
                            hsNames.add(name.toLowerCase());
                            continue;
                        }
                        Object prop = null;
                        try {
                            prop = fields[i].get(object);
                        } catch (IllegalAccessException e) {
                            // field is not accessible
                            continue;
                        }
                        //				String type = fields[i].getType().getName();
                        //					if(isPrimitiveType(prop)){
                        //						writePrimitive(objElement, prop, document, name);
                        //					}else{
                        hsNames.add(name.toLowerCase());
                        // Write the object (recursive method call).
                        writeXML(objElement, prop, name, fields[i].getType().getName());
                    }
                    // get all private methods
                    Method mtds[] = object.getClass().getMethods();
                    for (int g = 0; g < mtds.length; g++) {
                        String nameGet = mtds[g].getName();
                        // find get methods
                        if (nameGet.startsWith("get")) {
                            String name = nameGet.substring(3);
                            if (name.length() == 0 || mtds[g].getParameterTypes().length > 0
                                || hsNames.contains(name.toLowerCase()))
                                continue;
                            String corresponding = "set" + name;
                            for (int s = 0; s < mtds.length; s++) {
                                // find corresponding set methods
                                String nameSet = mtds[s].getName();
                                if (corresponding.equals(nameSet)) {
                                    if (mtds[s].getParameterTypes().length != 1
                                        || !mtds[s].getParameterTypes()[0].isAssignableFrom(mtds[g].getReturnType())) {
                                        //System.out.println("[WARNING] " +
                                        // mtds[g] + " not assignable from "
                                        //                   + mtds[s] + " in " +
                                        // object.getClass());
                                        continue;
                                    }
                                    // read ...
                                    Object prop = null;
                                    try {
                                        prop = mtds[g].invoke(object, new Object[] {});
                                    } catch (Exception e) {
                                        // e.printStackTrace();
                                        continue;
                                    }
                                    //	if(isPrimitiveType(prop)){
                                    //		writePrimitive(objElement, prop,
                                    // document, name);
                                    //	}
                                    //	else{
                                    // ... and write
                                    // the object
                                    if (prop != null && prop.getClass() == Class.class)
                                        continue;
                                    // TODO: proper solution
                                    if (prop != null && prop.getClass().getName().indexOf("$") < 0) {
                                        //if (object.getClass() !=
                                        // mtds[g].getReturnType() &&
                                        // !isPrimitiveType(object))
                                        name = name.substring(0, 1).toLowerCase() + name.substring(1);
                                        writeXML(objElement, prop, name, mtds[g].getReturnType().getName());
                                        //else
                                        //    writeXML(objElement, prop,
                                        // hashtable, document, name, null);
                                    }
                                    //								}
                                }
                            }
                        }
                    }
                } // regular Object

                if (object instanceof Map) {
                    Map map = (Map) object;
                    Collection coll = map.keySet();
                    // object is a Collection
                    objElement.setAttribute("isMap", "true");
                    java.util.Iterator iter = coll.iterator();
                    Element mapElement = this.outputDOM.createElement("mapElements");
                    while (iter.hasNext()) {
                        Object key = iter.next();
                        //						Element keyElement =
                        // document.createElement("key");
                        //						mapElement.appendChild(keyElement);
                        //						Element valueElement =
                        // document.createElement("value");
                        //						mapElement.appendChild(valueElement);
                        //						writeXML(keyElement, key, hashtable, document);
                        //						writeXML(valueElement, map.get(key), hashtable,
                        // document);
                        writeXML(mapElement, key);
                        writeXML(mapElement, map.get(key));
                        //						objElement.appendChild(mapElement);
                    }
                    objElement.appendChild(mapElement);
                } else if (object instanceof java.util.Collection) {
                    java.util.Collection coll = (java.util.Collection) object;
                    // object is a Collection
                    objElement.setAttribute("isCollection", "true");
                    java.util.Iterator iter = coll.iterator();
                    //Element collElement =
                    // document.createElement("collElements");
                    while (iter.hasNext()) {
                        writeXML(objElement, iter.next());
                    }
                    //objElement.appendChild(collElement);
                }
            } // successful
        } // nonNullObject
        //parent.appendChild(objElement);
        return true;
    }

    /**
     * @param c
     * @return
     */
    private static String getTagName(Class c) {
        String tagname = (String) toXML.get(c);
        if (tagname != null) {
            return (String) toXML.get(c);
        } else {
            System.out.println(c.getName());
            //            try {
            //                throw new Exception("No Mapping for type " + c.getName() + "
            // defined");
            //            } catch (Exception e) {
            //                e.printStackTrace();
            //            }
        }
        return c.getName();
    }

    static public boolean isElementaryType(Object obj) {
        if (obj == null)
            return false;
        for (Iterator iter = elementaryTypes.iterator(); iter.hasNext();) {
            Class cls = (Class) iter.next();
            if (cls.isInstance(obj))
                return true;
        }
        return false;
    }

    static private boolean isElementaryClass(Class cls) {
        if (cls != null && elementaryTypes.contains(cls))
            return true;
        else
            return false;
    }

    private boolean writePrimitive(Node element, Object object, String fieldName) {
        if (element == null || object == null || this.outputDOM == null)
            return false;
        if (fieldName != null) {
            Attr primAttr = this.outputDOM.createAttribute(fieldName);
            primAttr.setValue(object.toString());
            element.getAttributes().setNamedItem(primAttr);
            return true;
        } else {
            String type = sigForClass(object.getClass());
            Element primElem = this.outputDOM.createElement(type);
            Text text = this.outputDOM.createTextNode(object.toString());
            primElem.appendChild(text);
            if (fieldName != null)
                primElem.setAttribute("field-name", fieldName);
            element.appendChild(primElem);
            return true;
        }
    }

    private static Object readPrimitive(Element element) {
        Object obj = null;
        Class c = classForSig(element.getTagName());
        if (c == null)
            return null;
        String value = element.getFirstChild().getNodeValue();
        if (value == null) {
            System.out.println("[ERROR] Tag for primitve contains no value.");
            return null;
        }
        if (c == Character.class) {
            if (value.length() == 0) {
                return null;
            } else
                obj = new Character(value.charAt(0));
        } else {

            Constructor constructor;
            try {
                constructor = c.getConstructor(new Class[] {String.class,});
                obj = constructor.newInstance(new Object[] {value});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public Document serialize(MObject object) {
        this.outputDOM = newDOM();
        boolean success = writeXML(object);
        if (success)
            return this.outputDOM;
        else
            return null;
    }

    /**
     * @param document
     * @param baseURI
     * @return
     */
    public Object deserialize(Document document, URI argBaseURI) {
        this.baseURI = argBaseURI;
        return readXML(document);
    }

    public boolean isMpegMode() {
        return mpegMode;
    }

    public void setMpegMode(boolean mpegMode) {
        this.mpegMode = mpegMode;
    }

    /**
     * Adds <code>id</code>- and <code>ref</code> -tags to the currently
     * serialized object and remebers the object as serialized
     * 
     * @param node
     *            current <code>element</code> to append <code>id</code> or
     *            <code>ref</code>
     * @param object
     *            currently serialized object
     */
    public synchronized boolean knowsObject(Node node, Object object) {
        Node serializedNode = (Node) serialized.get(object);
        if (serializedNode != null) {
            // if so make a reference
            referencing.put(object, node);
            return true;
        } else {
            // else add to written objects
            serialized.put(object, node);
            objectIds.put(object, new Long(objId++));
            return false;
        }
    }

    /**
     * returns the referenced object for a given element
     * 
     * @return null if element is not a reference
     */
    public Object getReferenced(Element element, Object object) {
        if (element.hasAttribute("id")) {
            String idString = element.getAttribute("id");
            objectHash.put(idString, object);
            return null;
        } else if (element.hasAttribute("ref")) {
            String idString =  element.getAttribute("ref");
            return objectHash.get(idString);
        }
        return null;
    }

    /**
     * generate proper <code>id</code> and <code>ref</code> attributes
     */
    private synchronized void generateIdentityTags() {

        Enumeration enumer = referencing.keys();
        while (enumer.hasMoreElements()) {

            Object key = enumer.nextElement();
            // get the referencing node
            Element referencingNode = (Element) referencing.get(key);
            // get the original node to reference to
            Element referencedNode = (Element) serialized.get(key);

            /*
             * get ID of the referenced element or create a new Id & Id-tag
             */
            Long objId = (Long) objectIds.get(key);
            referencedNode.setAttribute("id", "" + objId);

            /*
             * create a reference tag
             */
            referencingNode.setAttribute("ref", objId.toString());
        }

    }

    /**
     * return a new
     * 
     * @return MusiteXMLSerializer instance in MPEG-mode
     */
    public static MusiteXMLSerializer newMPEGSerializer() {
        MusiteXMLSerializer s = new MusiteXMLSerializer();
        s.setMpegMode(true);
        return s;
    }

    private Context context = null;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    Hashtable scontext = new Hashtable();

    public void setParent(Object child, Object parent) {
        scontext.put(child, parent);
    }

    public Object getParent(Object child) {
        return scontext.get(child);
    }

    Hashtable onsets = new Hashtable();

    /**
     * gets the next onset that should be assigned to an object from its
     * parent-container
     * 
     * @param parent
     *            the container
     * @return onset
     */
    public Rational getNextOnset(Object parent) {
        return (Rational) onsets.get(parent);
    }

    /**
     * sets the next onset that should be assigned to an object from its
     * parent-container
     * 
     * @param parent
     *            the container
     * @param onset
     *            the onset of the next object
     * @return onset
     */
    public void setNextOnset(Object parent, Rational onset) {
        onsets.put(parent, onset);
    }

    public Node getNodeToSerializedObject(Object object) {
        return (Node) serialized.get(object);
    }

    public Long getReferencedObjectID(Object object) {
        return (Long) objectIds.get(object);
    }
    
    public Object getReferencedObject(String id){
        return objectHash.get(id);
    }

    private URI baseURI;

    /**
     * TODO add comment
     * @return
     */
    public URI getBaseURI() {
        
        return baseURI;
    }
}