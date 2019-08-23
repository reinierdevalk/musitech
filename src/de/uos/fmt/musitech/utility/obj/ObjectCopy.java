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
package de.uos.fmt.musitech.utility.obj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.framework.persistence.PersistenceManager;
import de.uos.fmt.musitech.framework.persistence.exceptions.PersistenceException;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * A class to copy objects and write/read them to/from XML files.
 * 
 * @author TW
 * @version 0.113
 */
public class ObjectCopy {

	static final String INT_SIG = "I";
	static final String SHORT_SIG = "S";
	static final String LONG_SIG = "J";
	static final String BOOL_SIG = "Z";
	static final String BYTE_SIG = "B";
	static final String CHAR_SIG = "C";
	static final String FLOAT_SIG = "F";
	static final String DOUBLE_SIG = "D";
	static final String URL_SIG = "URL";
	static final String INT_TYPE = "int";
	static final String SHORT_TYPE = "short";
	static final String LONG_TYPE = "long";
	static final String BOOL_TYPE = "boolean";
	static final String BYTE_TYPE = "byte";
	static final String CHAR_TYPE = "char";
	static final String FLOAT_TYPE = "float";
	static final String DOUBLE_TYPE = "double";
	static final Class<?> INT_CLASS = Integer.class;
	static final Class<?> SHORT_CLASS = Short.class;
	static final Class<?> LONG_CLASS = Long.class;
	static final Class<?> BOOL_CLASS = Boolean.class;
	static final Class<?> BYTE_CLASS = Byte.class;
	static final Class<?> CHAR_CLASS = Character.class;
	static final Class<?> FLOAT_CLASS = Float.class;
	static final Class<?> DOUBLE_CLASS = Double.class;
	static final Class<?> INT_P_CLASS = int.class;
	static final Class<?> SHORT_P_CLASS = short.class;
	static final Class<?> LONG_P_CLASS = long.class;
	static final Class<?> BOOL_P_CLASS = boolean.class;
	static final Class<?> BYTE_P_CLASS = byte.class;
	static final Class<?> CHAR_P_CLASS = char.class;
	static final Class<?> FLOAT_P_CLASS = float.class;
	static final Class<?> DOUBLE_P_CLASS = double.class;

	private static java.util.Hashtable<String, Class<?>> classTypeHashtable = new java.util.Hashtable<String, Class<?>>();
	private static java.util.Hashtable<Class<?>, String> typeClassHashtable = new java.util.Hashtable<Class<?>, String>();
	private static java.util.Hashtable<Class<?>, String> sigClassHashtable = new java.util.Hashtable<Class<?>, String>();
	private static java.util.Hashtable<String, Class<?>> classSigHashtable = new java.util.Hashtable<String, Class<?>>();
	/** @see #isElementaryClass(Class) */
	private static java.util.HashSet<Class<?>> elementaryTypes = new java.util.HashSet<Class<?>>();
	/** @see #isPrimitiveType(Object) */
	private static java.util.HashSet<Class<?>> primitiveTypes = new java.util.HashSet<Class<?>>();

	/** @see */
	private static java.util.HashSet<Class<?>> imsioTypes = new java.util.HashSet<Class<?>>();

	static {
		classTypeHashtable.put(INT_TYPE, INT_CLASS);
		classTypeHashtable.put(LONG_TYPE, LONG_CLASS);
		classTypeHashtable.put(SHORT_TYPE, SHORT_CLASS);
		classTypeHashtable.put(BYTE_TYPE, BYTE_CLASS);
		classTypeHashtable.put(BOOL_TYPE, BOOL_CLASS);
		classTypeHashtable.put(CHAR_TYPE, CHAR_CLASS);
		classTypeHashtable.put(DOUBLE_TYPE, DOUBLE_CLASS);
		classTypeHashtable.put(FLOAT_TYPE, FLOAT_CLASS);

		typeClassHashtable.put(INT_CLASS, INT_TYPE);
		typeClassHashtable.put(LONG_CLASS, LONG_TYPE);
		typeClassHashtable.put(SHORT_CLASS, SHORT_TYPE);
		typeClassHashtable.put(BYTE_CLASS, BYTE_TYPE);
		typeClassHashtable.put(BOOL_CLASS, BOOL_TYPE);
		typeClassHashtable.put(CHAR_CLASS, CHAR_TYPE);
		typeClassHashtable.put(DOUBLE_CLASS, DOUBLE_TYPE);
		typeClassHashtable.put(FLOAT_CLASS, FLOAT_TYPE);

		classSigHashtable.put(INT_SIG, INT_CLASS);
		classSigHashtable.put(LONG_SIG, LONG_CLASS);
		classSigHashtable.put(SHORT_SIG, SHORT_CLASS);
		classSigHashtable.put(BYTE_SIG, BYTE_CLASS);
		classSigHashtable.put(BOOL_SIG, BOOL_CLASS);
		classSigHashtable.put(CHAR_SIG, CHAR_CLASS);
		classSigHashtable.put(DOUBLE_SIG, DOUBLE_CLASS);
		classSigHashtable.put(FLOAT_SIG, FLOAT_CLASS);
		classSigHashtable.put(URL_SIG, java.net.URL.class);

		sigClassHashtable.put(INT_CLASS, INT_SIG);
		sigClassHashtable.put(LONG_CLASS, LONG_SIG);
		sigClassHashtable.put(SHORT_CLASS, SHORT_SIG);
		sigClassHashtable.put(BYTE_CLASS, BYTE_SIG);
		sigClassHashtable.put(BOOL_CLASS, BOOL_SIG);
		sigClassHashtable.put(CHAR_CLASS, CHAR_SIG);
		sigClassHashtable.put(DOUBLE_CLASS, DOUBLE_SIG);
		sigClassHashtable.put(FLOAT_CLASS, FLOAT_SIG);
//		sigClassHashtable.put(java.net.URL.class, URL_SIG);

		elementaryTypes.add(INT_CLASS);
		elementaryTypes.add(LONG_CLASS);
		elementaryTypes.add(SHORT_CLASS);
		elementaryTypes.add(BYTE_CLASS);
		elementaryTypes.add(BOOL_CLASS);
		elementaryTypes.add(CHAR_CLASS);
		elementaryTypes.add(DOUBLE_CLASS);
		elementaryTypes.add(FLOAT_CLASS);
		elementaryTypes.add(java.io.File.class);
		elementaryTypes.add(java.net.URL.class);
		elementaryTypes.add(java.lang.String.class);
		elementaryTypes.add(java.awt.Color.class);

		imsioTypes.add(URL.class);
		imsioTypes.add(File.class);
		imsioTypes.add(String.class);
		imsioTypes.add(java.awt.Color.class);

		primitiveTypes.add(INT_P_CLASS);
		primitiveTypes.add(LONG_P_CLASS);
		primitiveTypes.add(SHORT_P_CLASS);
		primitiveTypes.add(BYTE_P_CLASS);
		primitiveTypes.add(BOOL_P_CLASS);
		primitiveTypes.add(CHAR_P_CLASS);
		primitiveTypes.add(DOUBLE_P_CLASS);
		primitiveTypes.add(FLOAT_P_CLASS);

	}

	private static long objId = 0;

	/**
	 * ListenableJTextField constructor.
	 */
	public static void buildDom() {
		Document document;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument(); // Create from whole cloth

			Element root = document.createElement("rootElement");
			document.appendChild(root);
			root.appendChild(document.createTextNode("Some"));
			root.appendChild(document.createTextNode(" "));
			root.appendChild(document.createTextNode("text"));
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		}
	}

	/**
	 * Returns a class for a type name from a hashTable.
	 * 
	 * @return {@link String}
	 * @param type 
	 */
	static public Class<?> classForType(String type) {
		return classTypeHashtable.get(type);
	}

	/**
	 * Returns a type name for a class from a hashTable.
	 * 
	 * @return String
	 * @param cls
	 */
	static public String typeForClass(Class<?> cls) {
		return typeClassHashtable.get(cls);
	}

	/**
	 * Returns a class for a type signature from a hashTable.
	 * 
	 * @return String
	 * @param sig
	 */
	static public Class<?> classForSig(char sig) {
		return classForSig(new String(new char[] {sig}));
	}

	/**
	 * @param string The name of the class.
	 * @return The class object for the name.
	 */
	public static Class<?> classForSig(String string) {
		return classSigHashtable.get(string);
	}

	/**
	 * Returns a type signature string for a class from a hashTable.
	 * @param cls A class object.
	 * @return The signature.
	 */
	static public String sigForClass(Class<?> cls) {
		return sigClassHashtable.get(cls);
	}
	
	

	/**
	 * Copies a Serializable object.
	 * 
	 * @param obj The Serializable object to copy.
	 * @return java.lang.Object
	 */
	@SuppressWarnings("unchecked")
	static <O extends Serializable> O copySerializable(O obj) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			baos.close();
			byte byteArray[] = baos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
			ObjectInputStream ois = new ObjectInputStream(bais);
			O newObject = (O)ois.readObject();
			ois.close();
			bais.close();
			return newObject;
		} catch (Exception e) {
			if (DebugState.DEBUG)
				e.printStackTrace();
			System.out.println("ObjectCopy.copySerializable: " + obj.getClass()
								+ " throws Exception while serializing.");
			System.out.println(e);
			return null;
		}
	}

	/**
	 * Makes a deep copy of an object. It uses (standard binary)seralization, if
	 * possible. Else an XML-Serialization is used, which is much slower.
	 * Therefore objects that are copied often should be Serializable. If the
	 * object is not serializable, only public properties of the object are
	 * copied using getters and setters or public fields (using public fields is
	 * not recommended). It depends on the object, whether this is sufficient to
	 * copy all relevant information of the object, it should suffice for
	 * JavaBeans, but this needs to be checked by the user .
	 * 
	 * @param <O> The type of the object to copy 
	 * 
	 * @param in The object to copy.
	 * @return A deep copy of the argument object.
	 */
 	@SuppressWarnings("unchecked")
	public static <O> O copyObject(O in) {
		if (in == null)
			return null;
		O out = null;
		if (in instanceof Serializable)
			try { // TODO check why it's not working for octave in ScoreNote.
				out = (O) copySerializable((Serializable) in);
			} catch (RuntimeException e) {
				if (DebugState.DEBUG)
					e.printStackTrace();
			}
		if (out == null) { // not serializable
			if (DebugState.DEBUG)
				System.out.println("WARNING: Class " + in.getClass() + " is not serializable.");			
			try {
					out = copyObjectXML(in);
			} catch (RuntimeException e) {
				if (DebugState.DEBUG) {
						e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
//		if (out == null) {
//			if (DebugState.DEBUG)
//				System.out.println("WARNING: Class " + in.getClass() + " could not be copied by copyObjectXML.");				
//			// not XML-able
//			try {
//				out = copyObjectRef(in);
////				out = (O) in.getClass().getConstructor(new Class[] {}).newInstance(new Object[] {});
////				copyPublicProperties(in, out);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		return out;
	}

	/**
	 * Copies an object using reflection. If the class of <code>src</code> is 
	 * contained in "imsioTypes", <code>src</code> will be returned.
	 * 
	 * @param <O> The class of the object to copy. 
	 * @param src The source object.
	 * @return A copy of the object, null if the object was null or if an error
	 *         occurred.
	 */
	public static <O> O copyObjectRef(O src) {
		return copyObjectRef(src, null);
	}

	@SuppressWarnings("unchecked")
	private static <O> O copyObjectRef(O src, Map<Object,Object> copyMap) {
		if (src == null)
			return null;
		if(imsioTypes.contains(src.getClass()))
			return src;
		if (copyMap == null) {
			copyMap = new HashMap<Object, Object>();
		}
		Class<O> cls =  (Class<O>) src.getClass();
		O dest;
		// create the copy of the object
		try {
			dest = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// get the reflection access for the class
		ReflectionAccess ra = ReflectionAccess.accessForClass(cls);
		String[] pnames = ra.getPropertyNames();
		// iterate through the property names
		for (int i = 0; i < pnames.length; i++) {
			String name = pnames[i];
			Object sprop = ra.getProperty(src, name);
			Object dprop = copyMap.get(sprop);
			if (dprop == null) {
				copyMap.put(sprop, sprop);
				if (isPrimitiveClass(ra.getPropertyType(name))
					|| imsioTypes.contains(ra.getPropertyType(name))) { 
					// TODO check
					dprop = sprop;
				} else {
					dprop = copyObjectRef(sprop, copyMap);
					copyMap.put(sprop, dprop);
				}
			}
			ra.setProperty(dest, name, dprop);
		}
		return dest;
	}

	/**
	 * Copies the given object preserving the ID of MObjects using a
	 * PersistenceManager. The Identity of MObjects stored in the
	 * PersistenceManager is not changed.
	 * 
	 * @param src The object to copy.
	 * @param pm The PersistenceManager to use.
	 * @return The copied object.
	 */
	public static Object copyObjectPresID(Object src, PersistenceManager pm) {
		return copyObjectPresID(src, null, pm, null);
	}

	private static Object copyObjectPresID(Object src, Map<Object, Object> copyMap, PersistenceManager pm,
											Object dest) {
		if (src == null)
			return null;
		if (copyMap == null) {
			copyMap = new HashMap<Object, Object>();
		}
		Class<?> cls = src.getClass();
		if (dest == null || !cls.isInstance(dest))
			// get the object form the persistence manager
			if (src instanceof MObject) {
				try {
					dest = pm.read(((MObject) src).getUid());
				} catch (PersistenceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		if (dest == null) {
			// create the copy of the object
			try {
				dest = cls.newInstance();
				if (dest instanceof MObject) {
					pm.store((MObject) dest);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		// get the reflection access for the class
		ReflectionAccess ra = ReflectionAccess.accessForClass(cls);
		String[] pnames = ra.getPropertyNames();
		// iterate through the property names
		for (int i = 0; i < pnames.length; i++) {
			String name = pnames[i];
			Object sprop = ra.getProperty(src, name);
			Object dprop = copyMap.get(sprop);
			if (dprop == null) {
				copyMap.put(sprop, sprop);
				if (isPrimitiveClass(ra.getPropertyType(name))
					|| imsioTypes.contains(ra.getPropertyType(name))) { // TODO
					// check
					dprop = sprop;
				} else {
					dprop = copyObjectRef(sprop, copyMap);
					copyMap.put(sprop, dprop);
				}
			}
			ra.setProperty(dest, name, dprop);
		}
		return dest;
	}

	private static Class<?> emptyClassArray[] = new Class<?>[] {};

	/**
	 * Creates a shallow copy of an object by using copyPublicProperties. May
	 * return null if the new Object could not be created. The most probable
	 * reason for this is a missing default constructor.
	 * @param <O> The type of the argument is used for the return value.
	 * 
	 * @param obj The object to copy.
	 * @return The copy of the argument.
	 */
	@SuppressWarnings("unchecked")
	public static <O> O copyObjectShallow(O obj) {
		O out = null;
		// use clone() if cloneable is implemented // TODO this was not correct
		// !!
		// if (obj instanceof Cloneable) {
		// try {
		// Method cloneMethod = obj.getClass().getMethod("clone",
		// emptyClassArray);
		// out = cloneMethod.invoke(obj, null);
		// return out;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// else copyPublicProperties
		try {
			out = (O) obj.getClass().getConstructor(emptyClassArray).newInstance(new Object[] {});
			copyPublicProperties(obj, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * Copies all entries of a map into another one.
	 * 
	 * @param src Map to copy from.
	 * @param dest Map to copy to.
	 * @return true if copying successful.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static boolean copyMap( Map src, Map<Object, Object> dest) {
		try {
			Set<Entry> entrySet = src.entrySet();
			for (Iterator<Entry> iter = entrySet.iterator(); iter.hasNext();) {
				Entry entry = iter.next();
				dest.put(entry.getKey(), entry.getValue());
			}
		} catch (RuntimeException re) {
			if (DebugState.DEBUG)
				re.printStackTrace();
			return false;
		}
		return true;
	}

//	/**
//	 * Copies all elements of a collection into another one.
//	 * @param <O> The type of the elements in the collection to copy to and from.
//	 * 
//	 * @param src The source.
//	 * @param dest The destination.
//	 */
//	public static <O> void copyCollection(Collection<O> src, Collection<O> dest) {
//		try {
//			dest.addAll(src);
//		} catch (RuntimeException re) {
//			if (DebugState.DEBUG)
//				re.printStackTrace();
//		}
//	}

	/**
	 * Writes an object to an xml representation and then reads it from this
	 * file.
	 * @param <O> The type of object used.
	 * @return The copy of the argument object.
	 * @param object The object to copy.
	 */
	@SuppressWarnings("unchecked")
	public static <O> O copyObjectXML(O object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writeXML(object, baos);
			baos.flush();
			baos.close();
			byte byteArray[] = baos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
			O obj = (O) readXML(bais);
			bais.close();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static Object emptyObjectArray[] = new Object[] {};

	/**
	 * Copies the public properties from source object to destination object,
	 * including the contents of collections and maps. The result is
	 * <code>dest</code> being a shallow copy of <code>src</code>.
	 * 
	 * @return boolean true if successful
	 * @param src java.lang.Object the source object to be copied
	 * @param dest java.lang.Object the destination object
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static boolean copyPublicProperties2(Object src, Object dest) {
		// copy collection contents
		if (src instanceof Collection && dest instanceof Collection) {
			Collection coll1 = (Collection) src;
			Collection coll2 = (Collection) dest;
			coll2.clear();
			coll2.addAll(coll1);
		}
		// check map contents
		if (src instanceof Map && dest instanceof Map) {
			Map map1 = (Map) src;
			Map map2 = (Map) dest;
			map2.clear();
			// check if map contents is equal
			for (Iterator<Entry> iter = map1.entrySet().iterator(); iter.hasNext();) {
				Entry entry = iter.next();
				map2.put(entry.getKey(), entry.getValue());
			}
		}
		ReflectionAccess ra1 = ReflectionAccess.accessForClass(src.getClass());
		ReflectionAccess ra2 = ReflectionAccess.accessForClass(dest.getClass());
		String pNames[] = ra1.getPropertyNames();
		for (int i = 0; i < pNames.length; i++) {
			try {
				ra2.setProperty(dest, pNames[i], ra1.getProperty(src, pNames[i]));
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Compares the publicly accessible properties of two objects. The obejcts
	 * must be of the same class to be considered equal.
	 * 
	 * @param obj1 The first object to compare.
	 * @param obj2 The second object to compare.
	 * @return True if both objects contain equal values, fales if not.
	 */
	public static boolean comparePublicProperties(Object obj1, Object obj2) {
		return comparePublicProperties2(obj1, new HashSet<Object>(), obj2, new HashSet<Object>());
	}

	/**
	 * Copies the public properties from source object to destination object.
	 * 
	 * @param obj1 java.lang.Object The first object to be compared.
	 * @param obj1Set The set of objects already compared in the obj1 graph,
	 *            used to avoid endless cycles.
	 * @param obj2 java.lang.Object The second object to be compared.
	 * @param obj2Set The set of objects already compared in the obj2 graph,
	 *            used to avoid endless cycles.
	 * @return boolean true if successful
	 */
	static boolean comparePublicProperties2(Object obj1, Set<Object> obj1Set, Object obj2, Set<Object> obj2Set) {
		if (obj1 == null && obj2 == null)
			return true;
		if (obj1 == null && obj2 != null || obj1 != null && obj2 == null)
			return false;
		if (obj1.getClass() != obj2.getClass())
			return false;
		if (obj1Set.contains(obj1)) {
			return true;
		}
		obj1Set.add(obj1);
		if (obj2Set.contains(obj2)) {
			return true;
		}
		obj2Set.add(obj2);
		ReflectionAccess ra1 = ReflectionAccess.accessForClass(obj1.getClass());
		ReflectionAccess ra2 = ReflectionAccess.accessForClass(obj2.getClass());
		String pNames[] = ra1.getPropertyNames();
		try {
			// check collection contents
			if (obj1 instanceof List) {
				List<?> list1 = (List<?>) obj1;
				List<?> list2 = (List<?>) obj2;
				if (list1.size() != list2.size())
					return false;
				for (int j = 0; j < list1.size(); j++) {
					if (!comparePublicProperties2(list1.get(j), obj1Set, list2.get(j), obj2Set))
						return false;
				}
			} else if (obj1 instanceof Collection) {
				Collection<?> coll1 = (Collection<?>) obj1;
				Collection<?> coll2 = (Collection<?>) obj2;
				if (coll1.size() != coll2.size())
					return false;
				loop1: for (Iterator<?> iter = coll1.iterator(); iter.hasNext();) {
					// for every element of coll1
					Object element1 = iter.next();
					// look for equal element of coll2
					for (Iterator<?> iter2 = coll2.iterator(); iter2.hasNext();) {
						Object element2 = iter2.next();
						if (comparePublicProperties2(element1, obj1Set, element2, obj2Set))
							continue loop1;
					}
					// there was no equal object in coll2
					return false;
				}
				loop1: for (Iterator<?> iter = coll2.iterator(); iter.hasNext();) {
					// for every element of coll2
					Object element2 = iter.next();
					// look for equal element of coll1
					for (Iterator<?> iter2 = coll1.iterator(); iter2.hasNext();) {
						Object element1 = iter2.next();
						if (comparePublicProperties2(element1, obj1Set, element2, obj2Set))
							continue loop1;
					}
					// there was no equal object in coll1
					return false;
				}
			}
			// check map contents
			if (obj1 instanceof Map) {
				@SuppressWarnings("rawtypes")
				Map map1 = (Map) obj1;
				@SuppressWarnings("rawtypes")
				Map map2 = (Map) obj2;
				// check size of keysets
//				if (map1.keySet().size() != map2.keySet().size()) {
				if (map1.size() != map2.size()) {
					return false;
				}
				// check if map contents is equal
				for (@SuppressWarnings({"rawtypes", "unchecked"}) Iterator<Entry> iter = map1.entrySet().iterator(); iter.hasNext();) {
					@SuppressWarnings("rawtypes")
					Entry entry = iter.next();
					if (!comparePublicProperties2(entry.getValue(), obj1Set, map2.get(entry.getKey()), obj2Set)) {
						return false;
					}
				}
//				// check if map contents is equal
//				for (Iterator<?> iter = map1.keySet().iterator(); iter.hasNext();) {
//					Object key = iter.next();
//					if (!comparePublicProperties2(map1.get(key), obj1Set, map2.get(key), obj2Set)) {
//						return false;
//					}
//				}
			}
			// check properties
			for (int i = 0; i < pNames.length; i++) {
				String name = pNames[i];
				Object prop1 = ra1.getProperty(obj1, pNames[i]);
				Object prop2 = ra2.getProperty(obj2, pNames[i]);
				if (isPrimitiveType(prop1) || isElementaryType(prop1)) {
					if (!prop1.equals(prop2)) {
						return false;
					}
				} else {
					if (!comparePublicProperties2(prop1, obj1Set, prop2, obj2Set)) {
						return false;
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * @param obj1 java.lang.Object The first object to be compared.
	 * @param obj1Set The set of objects already compared in the obj1 graph,
	 *            used to avoid endless cycles.
	 * @param obj2 java.lang.Object The second object to be compared.
	 * @param obj2Set The set of objects already compared in the obj2 graph,
	 *            used to avoid endless cycles.
	 * @param diffSet In this set the different objects are collected.
	 * @return boolean true if the objects are equal, false if problems occured.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	static boolean differentObjects1(Object obj1, Set obj1Set, Object obj2, Set obj2Set, Set diffSet) {
		if (obj1 == null && obj2 == null)
			return true;
		if (obj1 == null && obj2 != null || obj1 != null && obj2 == null)
			return false;
		if (obj1.getClass() != obj2.getClass())
			return false;
		// maintain the anti-cycle list.
		if (obj1Set.contains(obj1)) {
			return true;
		}
		obj1Set.add(obj1);
		if (obj2Set.contains(obj2)) {
			return true;
		}
		obj2Set.add(obj2);

		try {
			// check collection contents
			if (obj1 instanceof List) {
				List list1 = (List) obj1;
				List list2 = (List) obj2;
				if (list1.size() != list2.size())
					return false;
				for (int j = 0; j < list1.size(); j++) {
					if (!differentObjectsCompat(list1.get(j), obj1Set, list2.get(j), obj2Set,
						diffSet))
						diffSet.add(list1);
				}
			} else if (obj1 instanceof Collection) {
				Collection<?> coll1 = (Collection<?>) obj1;
				Collection<?> coll2 = (Collection<?>) obj2;
				if (coll1.size() != coll2.size())
					return false;
				loop1: for (Iterator<?> iter = coll1.iterator(); iter.hasNext();) {
					// for every element of coll1
					Object element1 = iter.next();
					// look for equal element of coll2
					for (Iterator<?> iter2 = coll2.iterator(); iter2.hasNext();) {
						Object element2 = iter2.next();
						if (differentObjects1(element1, obj1Set, element2, obj2Set, diffSet))
							continue loop1;
					}
					// there was no equal object in coll2
					return false;
				}
				loop1: for (Iterator<?> iter = coll2.iterator(); iter.hasNext();) {
					// for every element of coll2
					Object element2 = iter.next();
					// look for equal element of coll1
					for (Iterator<?> iter1 = coll1.iterator(); iter1.hasNext();) {
						Object element1 = iter1.next();
						if (comparePublicProperties2(element1, obj1Set, element2, obj2Set))
							continue loop1;
					}
					// there was no equal object in coll1
					return false;
				}
			}
			// check map contents
			if (obj1 instanceof Map) {
				Map<?, ?> map1 = (Map<?, ?>) obj1;
				Map<?, ?> map2 = (Map<?, ?>) obj2;
				// check size of keysets
				if (map1.keySet().size() != map2.keySet().size()) {
					return false;
				}
				// check if map contents is equal
				for (Iterator<?> iter = map1.keySet().iterator(); iter.hasNext();) {
					Object key = iter.next();
					if (!comparePublicProperties2(map1.get(key), obj1Set, map2.get(key), obj2Set)) {
						return false;
					}
				}
			}
			// check properties
			ReflectionAccess ra1 = ReflectionAccess.accessForClass(obj1.getClass());
			ReflectionAccess ra2 = ReflectionAccess.accessForClass(obj2.getClass());
			String pNames[] = ra1.getPropertyNames();
			for (int i = 0; i < pNames.length; i++) {
				Object prop1 = ra1.getProperty(obj1, pNames[i]);
				Object prop2 = ra2.getProperty(obj2, pNames[i]);
				if (isPrimitiveType(prop1)) {
					if (!prop1.equals(prop2))
						diffSet.add(obj1);
				} else {
					differentObjects1(prop1, obj1Set, prop2, obj2Set, diffSet);
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * @param obj1 Object The first object to be compared.
	 * @param obj1Set The set of objects already compared in the obj1 graph,
	 *            used to avoid endless cycles.
	 * @param obj2 Object The second object to be compared.
	 * @param obj2Set The set of objects already compared in the obj2 graph,
	 *            used to avoid endless cycles.
	 * @param diffSet In this set the different objects are collected.
	 * @return boolean true if the objects are equal, false if problems occurred.
	 */
	@SuppressWarnings("unchecked")
	static boolean differentObjectsCompat(Object obj1, Set obj1Set, Object obj2, Set obj2Set,
											Set diffSet) {
		if (obj1 == null && obj2 == null)
			return true;
		if (obj1 == null && obj2 != null || obj1 != null && obj2 == null)
			return false;
		if (obj1.getClass() != obj2.getClass())
			return false;
		// maintain the anti-cycle list.
		if (obj1Set.contains(obj1)) {
			return true;
		}
		obj1Set.add(obj1);
		if (obj2Set.contains(obj2)) {
			return true;
		}
		obj2Set.add(obj2);

		try {
			// check collection contents
			if (obj1 instanceof List) {
				List<?> list1 = (List<?>) obj1;
				List<?> list2 = (List<?>) obj2;
				if (list1.size() != list2.size())
					return false;
				for (int j = 0; j < list1.size(); j++) {
					if (!comparePublicProperties2(list1.get(j), obj1Set, list2.get(j), obj2Set))
						diffSet.add(list1);
				}
			} else if (obj1 instanceof Collection) {
				Collection<?> coll1 = (Collection<?>) obj1;
				Collection<?> coll2 = (Collection<?>) obj2;
				if (coll1.size() != coll2.size())
					return false;
				loop1: for (Iterator<?> iter = coll1.iterator(); iter.hasNext();) {
					// for every element of coll1
					Object element1 = iter.next();
					// look for equal element of coll2
					for (Iterator<?> iter2 = coll2.iterator(); iter2.hasNext();) {
						Object element2 = iter2.next();
						if (comparePublicProperties2(element1, obj1Set, element2, obj2Set))
							continue loop1;
					}
					// there was no equal object in coll2
					return false;
				}
				loop1: for (Iterator<?> iter = coll2.iterator(); iter.hasNext();) {
					// for every element of coll2
					Object element2 = iter.next();
					// look for equal element of coll1
					for (Iterator<?> iter1 = coll1.iterator(); iter1.hasNext();) {
						Object element1 = iter1.next();
						if (comparePublicProperties2(element1, obj1Set, element2, obj2Set))
							continue loop1;
					}
					// there was no equal object in coll1
					return false;
				}
			}
			// check map contents
			if (obj1 instanceof Map) {
				Map<?, ?> map1 = (Map<?, ?>) obj1;
				Map<?, ?> map2 = (Map<?, ?>) obj2;
				// check size of keysets
				if (map1.keySet().size() != map2.keySet().size()) {
					return false;
				}
				// check if map contents is equal
				for (Iterator<?> iter = map1.keySet().iterator(); iter.hasNext();) {
					Object key = iter.next();
					if (!comparePublicProperties2(map1.get(key), obj1Set, map2.get(key), obj2Set)) {
						return false;
					}
				}
			}
			// check properties
			ReflectionAccess ra1 = ReflectionAccess.accessForClass(obj1.getClass());
			ReflectionAccess ra2 = ReflectionAccess.accessForClass(obj2.getClass());
			String pNames[] = ra1.getPropertyNames();
			for (int i = 0; i < pNames.length; i++) {
				Object prop1 = ra1.getProperty(obj1, pNames[i]);
				Object prop2 = ra2.getProperty(obj2, pNames[i]);
				if (isPrimitiveType(prop1)) {
					if (!prop1.equals(prop2)) {
						return false;
					}
				} else {
					if (!comparePublicProperties2(prop1, obj1Set, prop2, obj2Set)) {
						return false;
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Copies the public properties from source object to destination object
	 * (non-recursively).
	 * 
	 * @return boolean true if successful
	 * @param src java.lang.Object the source object to be copied
	 * @param dest java.lang.Object the destination object
	 */
	// TODO auch Elemente von Collections and Maps kopieren
	public static boolean copyPublicProperties(Object src, Object dest) {
		try {
			// get all public methods
			Method mtds[] = src.getClass().getMethods();
			Method destMtds[] = dest.getClass().getMethods();
			for (int i = 0; i < mtds.length; i++) {
				String nameGet = mtds[i].getName();
				// find get methods
				if (nameGet.startsWith("get") && nameGet.length() > 3) {
					// construct corresponding set name
					String property = nameGet.substring(3);
					String corresponding = "set" + property;
					forDestMdts: for (int j = 0; j < destMtds.length; j++) {
						// find corresponding set methods
						String nameSet = destMtds[j].getName();
						// is it the corresponding setMethod
						if (corresponding.equals(nameSet)) {
							// copy property
							// read ...
							try {
								Object prop = mtds[i].invoke(src, emptyObjectArray);
								// ... and write
								destMtds[j].invoke(dest, new Object[] {prop});
							} catch (Exception e) {
								continue forDestMdts;
							}
						}
					}
				}
			}
			// get all public fields
			Field fields[] = src.getClass().getFields();
			Field destFields[] = dest.getClass().getFields();
			// and copy them
			for (int i = 0; i < fields.length; i++) {
				for (int j = 0; j < destFields.length; j++) {
					if (!Modifier.isFinal(destFields[j].getModifiers())
						&& !Modifier.isTransient(destFields[j].getModifiers())
						&& destFields[j].getName().equals(fields[i].getName())) {
						Object prop = fields[i].get(src);
						destFields[j].set(dest, prop);
					}
				}
			}
			// successful
			return true;
		} catch (Exception e) {
			// error occurred
			e.printStackTrace();
			return false;
		}
	}

	// TODO use another method, to make result independent of
	// object graph structure and to make it more efficient.
	/**
	 * equalValues tests if the two argument objects are of equal value.
	 * 
	 * @param arg1 first argument to be compared.
	 * @param arg2 second argument to be compared.
	 * @return true if the values are equal, false otherwise.
	 */
	public static boolean equalValues(Object arg1, Object arg2) {
		StringWriter sw1 = new StringWriter();
		StringWriter sw2 = new StringWriter();
		writeXML(arg1, sw1);
		writeXML(arg2, sw2);
		String s1 = sw1.toString();
		String s2 = sw2.toString();
		return s1.equals(s2);
	}

	/**
	 * Main method for testing.
	 * 
	 * @param args Strings[]
	 */
	public static void main(java.lang.String[] args) {
//		class test {
//			public char c = 'a';
//		}
		// rhythm.Performance test1 = new rhythm.Performance();
		// music.MidiNoteSequence test1 = new music.MidiNoteSequence();
		String test1 = "Hallo";
		// twv.add(tw);
//		File testfile = new File("test.xml");
		File testfile1 = new File("test1.xml");
		File testfile2 = new File("test2.xml");
		Object test = test1;
		// Object test = readXML(testfile);
		try {
			writeXML(test, testfile1);
			Object test2 = readXML(testfile1);
			writeXML(test2, testfile2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a new document,
	 * @return The newly created document.
	 */
	static public Document newDom() {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument(); // Create from

		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		}
		return document;
	}

	/**
	 * Returns the primitiv for a standard wrapper class.
	 * 
	 * @return java.lang.Class the primitive class
	 * @param wrapper java.lang.Class the wrapper class
	 */
	public static Class<?> primitiveForWrapper(Class<?> wrapper) {
		Class<?> primitiveClass;
		if (wrapper == INT_CLASS)
			primitiveClass = INT_P_CLASS;
		else if (wrapper == SHORT_CLASS)
			primitiveClass = SHORT_P_CLASS;
		else if (wrapper == BYTE_CLASS)
			primitiveClass = BYTE_P_CLASS;
		else if (wrapper == LONG_CLASS)
			primitiveClass = LONG_P_CLASS;
		else if (wrapper == CHAR_CLASS)
			primitiveClass = CHAR_P_CLASS;
		else if (wrapper == BOOL_CLASS)
			primitiveClass = BOOL_P_CLASS;
		else if (wrapper == FLOAT_CLASS)
			primitiveClass = FLOAT_P_CLASS;
		else if (wrapper == DOUBLE_CLASS)
			primitiveClass = DOUBLE_P_CLASS;
		else
			primitiveClass = null;
		return primitiveClass;
	}

	/**
	 * Reads from an xml file.
	 * @param file The xml file to read from. 
	 * @return The object built from the data in the file.
	 * 
	 */
	public static Object readXML(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			Object returnVal = readXML(fis);
			fis.close();
			return returnVal;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads from xml file.
	 * 
	 * @param file The xml file to read from. 
	 * @param stylesheet The stylesheet to aply before rading. 
	 * @return The object built from the data in the file.
	 */
	public static Object readXML(File file, File stylesheet) {
		try {
			// Use a Transformer
			StreamSource stylesource = new StreamSource(stylesheet);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(stylesource);

			StreamSource source = new StreamSource(file);
			StreamResult result = new StreamResult(new File("temp.xml"));
			transformer.transform(source, result);
			return readXML(new File("temp.xml"));
		} catch (TransformerConfigurationException tce) {
			// Error generated by the parser
			System.out.println("\n** Transformer Factory error");
			System.out.println("   " + tce.getMessage());

			// Use the contained exception, if any
			Throwable x = tce;
			if (tce.getException() != null)
				x = tce.getException();
			x.printStackTrace();
			return null;
		} catch (TransformerException te) {
			// Error generated by the parser
			System.out.println("\n** Transformation error");
			System.out.println("   " + te.getMessage());

			// Use the contained exception, if any
			Throwable x = te;
			if (te.getException() != null)
				x = te.getException();
			x.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads from an XML input stream.
	 * @param is the {@link InputStream} to read from.
	 * @return The object created from the XML.
	 */
	public static Object readXML(InputStream 
	                             is) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			return readXML(document);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads from XML file.
	 * 
	 * @param rd The reader to read from.
	 * @return The object created from the XML.
	 */
	public static Object readXML(Reader rd) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(rd));
			return readXML(document);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads an object from and XML file.
	 * TODO Test this method.
	 * 
	 * @param is The input stream to read from.
	 * @return The object created from the XML.
	 */
	public static Object readXMLzipped(InputStream is) {
		InputStream deflatedStream = new ZipInputStream(is);
		return readXML(deflatedStream);
	}

	/**
	 * Reads an object from and XML file.
	 * 
	 * @param object The object to read
	 * @param is The input stream to read from.
	 * @return The object created from the XML.
	 */
	public static Object readXML(Object object, InputStream is) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			return readXML(object, document);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads an object from an XML document object.
	 * 
	 * @param document The document object
	 * @return The created object
	 */
	protected static Object readXML(Document document) {
		Node root = document.getFirstChild();
		while (root != null && !"JavaObject".equals(root.getNodeName()))
			root = root.getFirstChild();
		if (root == null)
			return null;
		Element child = XMLHelper.getFirstElementChild(root);
		child.normalize();
		return readXML(child);
	}

	/**
	 * Reads from a DOM element.
	 * 
	 * @param node The node to read from.
	 * @return The created object.
	 */
	protected static Object readXML(Element node) {
		return readXML(node, new HashMap<String,Object>());
	}

	/**
	 * Reads from a DOM element using a reference map.
	 * 
	 * @date (21.10.00 15:02:07)
	 * 
	 * @param node 
	 * @param objectHash The map to put the objects in.
	 * @return The created object.
	 */
	protected static Object readXML(Element node, Map<String,Object> objectHash) {
		return readXML(node, objectHash, null);
	}

	/**
	 * This is the main method for reading Java Objects from a DOM.
	 * 
	 * @return java.lang.Object
	 * @param node org.w3c.dom.Node
	 * @param objectHash
	 * @param object
	 */
	// TODO change to using Reflection Access
	protected static Object readXML(Element node, Map<String, Object> objectHash, Object object) {
		String className = XMLNameCoder.decode(node.getNodeName());
		if (className.equals("object"))
			className = node.getAttribute("class");
		Class<?> c = null;
		try {
			if (!className.equals(""))
				c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			c = classForSig(className);
			if (c != null && !(new Boolean(true).toString().equals(node.getAttribute("isArray")))) {
				object = readPrimitive(node);
				if (object != null)
					return object;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null) {
			System.out.println("Warning: could not find class for name " + className);
			return null;
		}
		if(c.isEnum())
			return readEnum(node);

		boolean isArray = new Boolean(node.getAttribute("isArray")).booleanValue();
		boolean isCollection = new Boolean(node.getAttribute("isCollection")).booleanValue();
		boolean isMap = new Boolean(node.getAttribute("isMap")).booleanValue();
		boolean isReference = new Boolean(node.getAttribute("isReference")).booleanValue();
		
		
		Element child = XMLHelper.getFirstElementChild(node);
		if (object != null && !(c.isInstance(object)))
			return null;
		String idString = className + node.getAttribute("id");
		if (isReference) {
			// Object is already known
			object = objectHash.get(idString);
		} else
			// new object
			notReference: {
				if (!isArray && (elementaryTypes.contains(c) || c == String.class)) {
					// Read an elementary type.
					Node textNode = node.getFirstChild();
					if (textNode != null) {
						if (textNode.getNodeType() == Node.TEXT_NODE) {
							// this is a text node
							String strValue = textNode.getNodeValue();
							if (c == String.class)
								object = strValue;
							else if (c == Character.class)
								object = new Character(strValue.charAt(0));
							else {
								object = ObjectStringCoder.fromString(strValue, c);
//								final Class sca[] = new Class[] {String.class};
//								try {
//									object = c.getConstructor(sca).newInstance(
//										new Object[] {strValue});
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
							}
						}
					}
					if (object != null)
						objectHash.put(idString, object);
					break notReference;
				} else if (isArray) {
					// read an array
					int arrayLength = new Integer(node.getAttribute("arrayLength")).intValue();
					// TODO: check if .getComponentType() is correct, use code
					// from setField here
					object = Array.newInstance(c, arrayLength);
					objectHash.put(idString, object);
					int i = 0;
					if ("arrayElements".equals(child.getNodeName())) {
						Element grandChild = XMLHelper.getFirstElementChild(child);
						while (grandChild != null) {
							Array.set(object, i++, readXML(grandChild, objectHash));
							grandChild = XMLHelper.getNextElementSibling(grandChild);
						}
						child = XMLHelper.getNextElementSibling(child);
					}
					while (child != null) {
						if ("arrayElement".equals(child.getNodeName()))
							Array.set(object, i, readXML(XMLHelper.getFirstElementChild(child),
								objectHash));
						child = XMLHelper.getNextElementSibling(child);
					}
				} else { // not array && not elementary type
					// get a new object if necessary
					try {
						if (object == null)
							try {
								object = c.newInstance();
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}

						objectHash.put(idString, object);
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
					if (isMap) { // Object is a map
						Map<Object, Object> map = (Map<Object, Object>) object;
						// this is the new way to code a map
						while (child != null) {
							if ("mapElements".equals(child.getNodeName())) {
								Element grandChild = XMLHelper.getFirstElementChild(child);
								while (grandChild != null) {
									Object key = readXML(grandChild, objectHash);
									grandChild = XMLHelper.getNextElementSibling(grandChild);
									if (grandChild == null) {
										System.out.println("WARNING: map value for key " + key
															+ " is null.");
									} else {
										Object value = readXML(grandChild, objectHash);
										map.put(key, value);
										grandChild = XMLHelper.getNextElementSibling(grandChild);
									} // end if
								}
							} // this is for backward compatibility
							else if (child.getNodeName().equals("mapElement")) {
								Object key = null, value = null;
								Element keyOrValue = XMLHelper.getFirstElementChild(child);
								while (keyOrValue != null) {
									Node contentNode = XMLHelper.getFirstElementChild(keyOrValue);
									Object contentObject;
									contentObject = readXML((Element) contentNode, objectHash);
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
								Object value = readXML(child, objectHash);
								try {
									setField(object, fieldName, value);
								} catch (Throwable t) {
								}
							}
							child = XMLHelper.getNextElementSibling(child);
						}
					} else if (isCollection) {
						java.util.Collection<Object> coll = (Collection<Object>) object;
						while (child != null) {
							if ("collElements".equals(child.getNodeName())) {
								Element grandChild = XMLHelper.getFirstElementChild(child);
								while (grandChild != null) {
									coll.add(readXML(grandChild, objectHash));
									grandChild = XMLHelper.getNextElementSibling(grandChild);
								}
							} else {
								String fieldName = child.getAttribute("field-name");
								Object value = readXML(child, objectHash);
								try {
									setField(object, fieldName, value);
								} catch (Throwable t) {
								}
							}
							child = XMLHelper.getNextElementSibling(child);
						}
					}
				}// end if/else elementary-array-regular
				// Read the children of the current node
				while (child != null) {
					if ("field".equals(child.getNodeName())) {
						String fieldName = child.getAttribute("name");
						// String type = child.getAttribute("type");
						Object value = null;
						Element grandChild = XMLHelper.getFirstElementChild(child);
						if (grandChild != null)
							value = readXML(grandChild, objectHash);
						try {
							setField(object, fieldName, value);
						} catch (Throwable t) {
							t.printStackTrace();
						}
						child = XMLHelper.getNextElementSibling(child);
					} else {
						String fieldName = child.getAttribute("field-name");
						Object value = readXML(child, objectHash);
						try {
							boolean couldSet = setField(object, fieldName, value);
							if (DebugState.DEBUG && !couldSet) {
								Exception ex = new Exception("Could not set field " + fieldName
																+ " of object " + object
																+ " to value " + value);
								ex.printStackTrace();
							}

						} catch (Throwable t) {
							t.printStackTrace();
						}
						child = XMLHelper.getNextElementSibling(child);
					}
				}
			} // end notReference
		return object;
	}

	/**
	 * Reads from an xml file into an object.
	 * 
	 * @param object This object is filled with the data in the file, it must
	 *            therefore be of the type used for the root object in the file;
	 *            it may be <b>null </b>, then a new object is created.
	 * @param document The XML document to read from.
	 * @return java.lang.Object The object read. It the parameter object was not
	 *         <code>null</code> and not of the class specified forthe root
	 *         object in the file, then <code>null</code> is returned, else
	 *         the object passed as parameter <code>object</code> is returned.
	 */
	protected static Object readXML(Object object, Document document) {
		Node root = document.getFirstChild();
		while (root != null && !"JavaObject".equals(root.getNodeName()))
			root = root.getFirstChild();
		if (root == null)
			return null;
		Element child = XMLHelper.getFirstElementChild(root);
		child.normalize();
		return readXML(child, new HashMap<String,Object>(), object);
	}

	/**
	 * Sets the field 'name' in the 'object' to 'value'.
	 * 
	 * @param object The object of which the field shall be set.
	 * @param name The name of the field.
	 * @param value The value to set it to.
	 * @return boolean <code>true</code> if the field is set successfully.
	 */
	public static boolean setField(Object object, String name, Object value) {
		if(object == null)
			return false;
		Class<?> c = object.getClass();

		ReflectionAccess ra = ReflectionAccess.accessForClass(c);

		return ra.setProperty(object, name, value);

	} // end of method setField

	// /**
	// * Sets the field 'name' in the 'object' to 'value'.
	// *
	// * @param object The object of which the field is to be set.
	// * @param name The name of the field to set.
	// * @param value The value to set the field to.
	// * @return boolean
	// */
	// public static boolean setField(Object object, String name, Object value)
	// {
	// Class c = object.getClass();
	// Field fields[] = c.getFields(), field = null;
	// Method methods[] = c.getMethods(), method = null;
	//
	// // get the class of the value object
	// Class valueClass = null;
	// if (value != null) {
	// valueClass = value.getClass();
	// /*
	// * try { valueClass = Class.forName(type); } catch (Exception e) {
	// * valueClass = value.getClass(); }
	// */
	// } else
	// // value was null
	// return false;
	//
	// // Try to find the field with the right name.
	// for (int i = 0; i < fields.length; i++) {
	// if (name.equals(fields[i].getName())) {
	// field = fields[i];
	// break;
	// }
	// }
	//
	// // if field found, set it.
	// if (field != null) {
	// try {
	// Object val2;
	// // TODO move this to where the array is created in readXML
	// // TODO multidimensional Arrays
	// if (field.getType().isArray()) {
	// if (isPrimitiveClass(field.getType().getComponentType())) {
	// val2 = Array.newInstance(field.getType().getComponentType(), Array
	// .getLength(value));
	// for (int i = 0; i < Array.getLength(val2); i++) {
	// Array.set(val2, i, Array.get(value, i));
	// }
	// } else
	// val2 = value;
	// } else
	// val2 = value;
	// try {
	// field.set(object, val2);
	// } catch (RuntimeException re) {
	// // forsensic analysis
	// System.out.println("field.getType().isArray(): " +
	// field.getType().isArray());
	// System.out.println("field.getType().getComponentType() "
	// + field.getType().getComponentType());
	// System.out.println("value.getClass().isArray(): " +
	// value.getClass().isArray());
	// System.out.println("value.getClass().getComponentType() "
	// + value.getClass().getComponentType());
	// System.out.println("val2.getClass().isArray(): " +
	// val2.getClass().isArray());
	// System.out.println("val2.getClass().getComponentType() "
	// + val2.getClass().getComponentType());
	// System.out.println("do something ...."); // TODO
	// throw new IllegalArgumentException("");
	// }
	// // success
	// return true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// // if field not found, try to find a appropriate set-method.
	// StringBuffer sb = new StringBuffer();
	// sb.append("set");
	// sb.append(name.substring(0, 1).toUpperCase());
	// sb.append(name.substring(1));
	// String setName = sb.toString();
	// for (int i = 0; i < methods.length; i++) {
	// if (setName.equals(methods[i].getName())) {
	// Class paramClass = methods[i].getParameterTypes()[0];
	// if (paramClass.isPrimitive())
	// paramClass = classForType(paramClass.getName());
	// else if (paramClass.isArray() &&
	// paramClass.getComponentType().isPrimitive()) {
	// paramClass = Array.newInstance(
	// classForType(paramClass.getComponentType()
	// .getName()), 1).getClass();
	// }
	// if (valueClass != null && paramClass.isAssignableFrom(valueClass)) {
	// method = methods[i];
	// break;
	// }
	// }
	// }
	// if (method != null) {
	// try {
	// Object val2;
	// // TODO move this to where the array is created in readXML
	// // TODO multidimensional Arrays
	// if (method.getParameterTypes()[0].isArray()) {
	// if (isPrimitiveClass(method.getParameterTypes()[0].getComponentType())) {
	// val2 =
	// Array.newInstance(method.getParameterTypes()[0].getComponentType(),
	// Array.getLength(value));
	// for (int i = 0; i < Array.getLength(val2); i++) {
	// Array.set(val2, i, Array.get(value, i));
	// }
	// } else
	// val2 = value;
	// } else
	// val2 = value;
	// method.invoke(object, new Object[] {val2});
	// // success
	// return true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// // failed
	// return false;
	// } // end of method setField

	static File writeStylesheet;

	public static File getWriteStylesheet() {
		return writeStylesheet;
	}

	public static void setWriteStylesheet(File f) {
		writeStylesheet = f;
	}

	static File readStylesheet;

	public static File getReadStylesheet() {
		return readStylesheet;
	}

	public static void setReadStylesheet(File f) {
		readStylesheet = f;
	}

	// /**
	// * Sets a field.
	// *
	// * @return boolean
	// * @param object java.langObject
	// * @param name java.lang.String
	// * @param value java.lang.Object
	// * @param type java.lang.String
	// */
	// public static boolean setField(Object object, String name, Object value,
	// String type) {
	// Class c = object.getClass();
	// Field fields[] = c.getFields();
	// Field field = null;
	// Method methods[] = c.getMethods();
	// Method method = null;
	// Class valueClass = null;
	// if (value != null) {
	// valueClass = value.getClass();
	// /*
	// * try { valueClass = Class.forName(type); } catch (Exception e) {
	// * valueClass = value.getClass(); }
	// */
	// } else
	// return false;
	// for (int i = 0; i < fields.length; i++) {
	// if (name.equals(fields[i].getName())) {
	// field = fields[i];
	// break;
	// }
	// }
	// if (field != null) {
	// try {
	// field.set(object, value);
	// return true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// StringBuffer sb = new StringBuffer();
	// sb.append("set");
	// sb.append(name.substring(0, 1).toUpperCase());
	// sb.append(name.substring(1));
	// String setName = sb.toString();
	// for (int i = 0; i < methods.length; i++) {
	// if (setName.equals(methods[i].getName())) {
	// Class paramClass = methods[i].getParameterTypes()[0];
	// if (paramClass.isPrimitive())
	// paramClass = classForType(paramClass.getName());
	// if (valueClass != null && paramClass == valueClass) {
	// method = methods[i];
	// break;
	// }
	// }
	// }
	// if (method == null) {
	// System.out.println("Warning: could not find set Method for field " + name
	// + ", class "
	// + valueClass);
	// } else {
	// try {
	// method.invoke(object, new Object[] {value});
	// return true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return false;
	// }

	/**
	 * Writes an xml file.
	 * 
	 * @param object The object to write.
	 * @param file The File to write to.
	 * @return true if the file was written successfully.
	 */
	public static boolean writeXML(Object object, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			writeXML(object, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Writes an object to an XML file using a stylesheet.
	 * 
	 * @param object The object to write.
	 * @param file The file to write to.
	 * @param stylesheet The XLS-File to use.
	 * @return true if the file was written successfully, false otherwise.
	 */
	public static boolean writeXML(Object object, File file, File stylesheet) {
		Document document = newDom();
		if (!writeXML(document, object))
			return false;

		try {
			// Use a Transformer for output
			StreamSource stylesource = new StreamSource(stylesheet);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(stylesource);

			DOMSource source = new DOMSource(document);
			// StreamResult result = new StreamResult(System.out);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException tce) {
			// Error generated by the parser
			System.out.println("\n** Transformer Factory error");
			System.out.println("   " + tce.getMessage());

			// Use the contained exception, if any
			Throwable x = tce;
			if (tce.getException() != null)
				x = tce.getException();
			x.printStackTrace();
		} catch (TransformerException te) {
			// Error generated by the parser
			System.out.println("\n** Transformation error");
			System.out.println("   " + te.getMessage());

			// Use the contained exception, if any
			Throwable x = te;
			if (te.getException() != null)
				x = te.getException();
			x.printStackTrace();
		}
		return true;
	}

	/**
	 * Writes an object to an OutputStream as zipped XML.
	 * TODO Test this method.
	 * 
	 * @param object The object to write.
	 * @param os The outputStream to write to.
	 * @return true if written successfully.
	 * 
	 */
	public static boolean writeXMLzipped(Object object, OutputStream os) {
		ZipOutputStream deflatedStream = new ZipOutputStream(os);
		deflatedStream.setLevel(9);
		return writeXML(object,deflatedStream);
	}
	/**
	 * Writes an object to an OutputStream as XML.
	 * 
	 * @param object The object to write.
	 * @param os The outputStream to write to.
	 * @return true if written sucessfully.
	 */
	public static boolean writeXML(Object object, OutputStream os) {
		Document document = newDom();
		if (!writeXML(document, object))
			return false;
		try {
			Writer pw = new OutputStreamWriter(os, "UTF-8");
			writeXML(object, pw);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Writes an object to a writer as XML.
	 * 
	 * @param object The object to write.
	 * @param writer The writer to write to.
	 * @return <code>true</code> if the object was written sucessfully, else
	 *         false.
	 */
	public static boolean writeXML(Object object, Writer writer) {
		Document document = newDom();
		if (!writeXML(document, object))
			return false;

		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);

			// com.sun.xml.tree.XmlDocument xdoc =
			// (com.sun.xml.tree.XmlDocument) document;
			// xdoc.write(writer, "US-ASCII");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Write an xml file.
	 * @param document 
	 * @param object 
	 * @return boolean true 
	 */
	public static synchronized boolean writeXML(Document document, Object object) {
		Element root = document.createElement("JavaObject");
		document.appendChild(root);
		objId = 0;
		return writeXML(root, object, new java.util.Hashtable<Object, Long>());
	}

	// /**
	// * Write an xml file.
	// * @date (31.07.00 00:13:50)
	// * @return boolean
	// * @param object java.lang.Object
	// * @param dest java.lang.Object
	// */
	// public static synchronized boolean writeXML(org.w3c.dom.Node parent,
	// Object object)
	// {
	// return writeXML(parent, object, new java.util.Hashtable());
	// }

	/**
	 * Write an xml file.
	 * @param parent 
	 * 
	 * @date (31.07.00 00:13:50)
	 * @return boolean
	 * @param object java.lang.Object
	 * @param hashtable 
	 */
	protected static synchronized boolean writeXML(org.w3c.dom.Node parent, Object object,
													Map<Object, Long> hashtable) {
		// get document reference
		Document document;
		if (parent instanceof Document)
			document = (Document) parent;
		else
			document = parent.getOwnerDocument();
		return writeXML(parent, object, hashtable, document);
	}

	/**
	 * Write an xml file.
	 * @param parent 
	 * @param object 
	 * @param hashtable 
	 * @param document 
	 * 
	 * @return boolean true if the copying was successful
	 */
	protected synchronized static boolean writeXML(org.w3c.dom.Node parent, Object object,
													Map<Object, Long> hashtable, Document document) {
		return writeXML(parent, object, hashtable, document, null);

	}

	/**
	 * Main method for writing Java objects into an XML DOM.
	 * @param parent 
	 * @param object java.lang.Object
	 * @param hashtable 
	 * @param document 
	 * @param fieldname 
	 * 
	 * @return boolean
	 */
	// TODO change to using Reflection Access
	protected synchronized static boolean writeXML(org.w3c.dom.Node parent, Object object,
													Map<Object, Long> hashtable,
													Document document, String fieldname) {
		// the element to be written
		Element objElement;
		// case null object
		if (object == null) {
			// objElement.setAttribute("id", "null");
			// parent.appendChild(objElement);
			return true;
		}
		nonNullObject: {
			// object not null
			// get the class
			Class<? extends Object> c = object.getClass();
			// and its name
			String classname = c.getName();
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
			if(c.isEnum())
				return writeEnum(parent, object, document, fieldname);
			// write primitves
			if (isPrimitiveType(object))
				return writePrimitive(parent, object, document, fieldname);
			// else create element

			objElement = document.createElement(XMLNameCoder.encode(classname));

			// do we know this Object?
			boolean isReference = false;
			Long objIdObj = hashtable.get(object);
			if (objIdObj != null) {
				// if so make a reference
				objElement.setAttribute("isReference", "true");
				isReference = true;
			} else {
				// else add to written objects
				objIdObj = new Long(objId++);
				hashtable.put(object, objIdObj);
			}
			// write the object ID
			objElement.setAttribute("id", objIdObj.toString());
			if (fieldname != null)
				objElement.setAttribute("field-name", fieldname);
			if (isReference)
				break nonNullObject;
			if (object.getClass().isArray()) {
				// object is an Array
				objElement.setAttribute("isArray", "true");
				objElement.setAttribute("arrayLength", Integer.toString(Array.getLength(object)));
				// objElement.setAttribute("class", classname);
				Element arrayElement = document.createElement("arrayElements");
				for (int i = 0; i < Array.getLength(object); i++) {
					writeXML(arrayElement, Array.get(object, i), hashtable);
				}
				objElement.appendChild(arrayElement);
			} else {
				// non Array
				// regular object
				if (elementaryTypes.contains(c) || c == String.class) {
					// primitive or String
					objElement.appendChild(document.createTextNode(ObjectStringCoder.toString(object)));
//					objElement.appendChild(document.createTextNode(object.toString()));
					break nonNullObject;
				}
				//regular_object: 
				{
					// hash set for field names
					java.util.HashSet<String> hsNames = new java.util.HashSet<String>();
					// get all public fields
					Field fields[] = object.getClass().getFields();
					// and write them
					for (int i = 0; i < fields.length; i++) {
						// no transient or final fields
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
						// String type = fields[i].getType().getName();
						// if(isPrimitiveType(prop)){
						// writePrimitive(objElement, prop, document, name);
						// }else{
						hsNames.add(name.toLowerCase());
						// Write the object (recursive method call).
						writeXML(objElement, prop, hashtable, document, name);
						// }
					}
					// get all public methods
					Method mtds[] = object.getClass().getMethods();
					for (int i = 0; i < mtds.length; i++) {
						String nameGet = mtds[i].getName();
						// find get methods
						if (nameGet.startsWith("get")) {
							String name = nameGet.substring(3);
							if (name.length() == 0 || mtds[i].getParameterTypes().length > 0
								|| hsNames.contains(name.toLowerCase()))
								continue;
							String corresponding = "set" + name;
							for (int j = 0; j < mtds.length; j++) {
								// find corresponding set methods
								String nameSet = mtds[j].getName();
								if (corresponding.equals(nameSet)) {
									if (mtds[j].getParameterTypes().length != 1
										|| !mtds[j].getParameterTypes()[0].isAssignableFrom(mtds[i]
												.getReturnType())) {
										continue;
									}

									// read ...
									Object prop = null;
									try {
										prop = mtds[i].invoke(object, new Object[] {});
									} catch (Exception e) {
										// e.printStackTrace();
										continue;
									}
									// if(isPrimitiveType(prop)){
									// writePrimitive(objElement, prop,
									// document, name);
									// }
									// else{
									// ... and write
									// the object
									if (prop != null && prop.getClass() == Class.class)
										continue;
									// TODO: proper solution
									if (prop != null && prop.getClass().getName().indexOf("$") < 0) {
										writeXML(objElement, prop, hashtable, document, name);
									}
									// }
								}
							}
						}
					}
				} // regular Object

				if (object instanceof Map) {
					Map<?, ?> map = (Map<?, ?>) object;
					Collection<?> coll = map.keySet();
					// object is a Collection
					objElement.setAttribute("isMap", "true");
					java.util.Iterator<?> iter = coll.iterator();
					Element mapElement = document.createElement("mapElements");
					while (iter.hasNext()) {
						Object key = iter.next();
						// Element keyElement =
						// document.createElement("key");
						// mapElement.appendChild(keyElement);
						// Element valueElement =
						// document.createElement("value");
						// mapElement.appendChild(valueElement);
						// writeXML(keyElement, key, hashtable, document);
						// writeXML(valueElement, map.get(key), hashtable,
						// document);
						writeXML(mapElement, key, hashtable, document);
						writeXML(mapElement, map.get(key), hashtable, document);
						// objElement.appendChild(mapElement);
					}
					objElement.appendChild(mapElement);
				} else if (object instanceof java.util.Collection) {
					java.util.Collection<?> coll = (java.util.Collection<?>) object;
					// object is a Collection
					objElement.setAttribute("isCollection", "true");
					java.util.Iterator<?> iter = coll.iterator();
					Element collElement = document.createElement("collElements");
					while (iter.hasNext()) {
						writeXML(collElement, iter.next(), hashtable, document);
					}
					objElement.appendChild(collElement);
				}
			} // successful
		} // nonNullObject
		parent.appendChild(objElement);
		return true;
	}
	

	/**
	 * Tests if the given object is of one the classes that represent primitve
	 * types as objects (e.g. java.lang.Integer).
	 * 
	 * @param obj The object to test.
	 * @return true if it is a primitive type.
	 */
	static public boolean isElementaryType(Object obj) {
		if (obj == null)
			return false;
		for (Iterator<Class<?>> iter = elementaryTypes.iterator(); iter.hasNext();) {
			Class<?> cls = iter.next();
			if (cls.isInstance(obj))
				return true;
		}
		return false;
	}



	/**
	 * Tests if the given object is of one the classes that represent primitve
	 * types as objects (e.g. java.lang.Integer).
	 * 
	 * @param obj The object to test.
	 * @return true if it is a primitive type.
	 */
	static public boolean isPrimitiveType(Object obj) {
		if (obj == null)
			return false;
		for (Iterator<Class<?>> iter = primitiveTypes.iterator(); iter.hasNext();) {
			Class<?> cls = iter.next();
			if (cls.isInstance(obj))
				return true;
		}
		return false;
	}

	/**
	 * Tests if the given class is one of the classes that represent primitve
	 * types as objects (e.g. int.class).
	 * 
	 * @param cls The class to check.
	 * @return true if cls is on of the above listed.
	 */
	static public boolean isPrimitiveClass(Class<?> cls) {
		if (cls == null)
			return false;
		for (Iterator<Class<?>> iter = primitiveTypes.iterator(); iter.hasNext();) {
			if (cls == iter.next())
				return true;
		}
		return false;
	}

	/**
	 * Tests if the given class is one of the classes that represent primitve
	 * types as objects (e.g. java.lang.Integer) or the String class.
	 * 
	 * @param cls The class to check.
	 * @return true if cls is on of the above listed.
	 */
	static public boolean isElementaryClass(Class<?> cls) {
		if (cls == null)
			return false;
		for (Iterator<Class<?>> iter = elementaryTypes.iterator(); iter.hasNext();) {

			if (cls == iter.next())
				return true;
		}
		return false;
	}

	static private boolean writeEnum(Node element, Object object, Document document, String fieldName) {
		// TODO double check correctness of this code and use names instead of numbers. 
		if (element == null || object == null || document == null)
			return false;		
		String type = object.getClass().getName();
		Element primElem = document.createElement(XMLNameCoder.encode(type));
		Object constants[] = object.getClass().getEnumConstants();
		int i;
		for (i = 0; i < constants.length; i++) {
			if(constants[i] == object)
				break;
		}
		assert(i<constants.length);
		
		Text text = document.createTextNode(Integer.toString(i+1));
		primElem.appendChild(text);
		if (fieldName != null)
			primElem.setAttribute("field-name", fieldName);
		element.appendChild(primElem);
		return true;
	}

	static boolean writePrimitive(Node element, Object object, Document document, String fieldName) {
		if (element == null || object == null || document == null)
			return false;
		
		String type = sigForClass(object.getClass());
		Element primElem = document.createElement(type);
		Text text = document.createTextNode(object.toString());
		primElem.appendChild(text);
		if (fieldName != null)
			primElem.setAttribute("field-name", fieldName);
		element.appendChild(primElem);
		return true;
	}
	
	static private Object readEnum(Element element) {
		// TODO double check correctness of this code and user names instead of numbers. 
		Class<?> c;
		try {
			c = Class.forName(XMLNameCoder.decode(element.getTagName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		if (c == null)
			return null;
		String value = element.getFirstChild().getNodeValue();
		int num = Integer.parseInt(value);
		if(num == 0) {
			System.out.println("WARINING: readEnum found value 0!");
			return null;
		}
		try {
			return c.getEnumConstants()[num-1];
		}catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}


	static Object readPrimitive(Element element) {
		Object obj = null;
		Class<?> c = classForSig(element.getTagName());
		if (c == null)
			return null;
		String value = element.getFirstChild().getNodeValue();
		if (value == null) {
			System.out.println("Error: Tag for primitve contains no value.");
			return null;
		}
		if (c == Character.class) {
			if (value.length() == 0) {
				return null;
			} else
				obj = new Character(value.charAt(0));
		} else {

			Constructor<?> constructor;
			try {
				constructor = c.getConstructor(new Class[] {String.class,});
				obj = constructor.newInstance(new Object[] {value});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

}