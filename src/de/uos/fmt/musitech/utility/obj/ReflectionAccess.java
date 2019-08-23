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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReflectionAccess encapsulates the method and field access by reflection to
 * simplify generic programming. Properties are values that can be read and
 * written, normally by get- and set-methods. For convenience public fields are
 * also supported, but their use is not recommended for production code. <BR>
 * <BR>
 * Property names begin with lower case when extracted from the names of getters
 * and setters, e.g. the property defined by <code>setColor</code> and
 * <code>getColor</code> can be accessed by the name "color". If there are
 * both a field and getter and setter the access by the methods is used. A field
 * name beginning with a capital letter (e.g. "Color") is distinct form the one
 * defined by get its own property <code>setColor</code> and
 * <code>getColor</code> (using this option is not recommended).
 * 
 * @author Tillman Weyde
 */
public class ReflectionAccess {

	private final Class<?> cls;

	//private static final Map<?, ?> classAccessMap = new HashMap<Object, Object>();

	private static final List<Class<?>> excludeClassList = new ArrayList<Class<?>>();

	/**
	 * This method attempts to adapt the use of primitive vs. wrapper classes
	 * (e.g. Inteter vs. int) for an array. This is necessary when arrays of
	 * primitive values are stored as arrays of Integer values. To use these as
	 * values for corresponding properties they have to be converted to
	 * primitives again.
	 * 
	 * @param object The array to convert.
	 * @param cls The class to convert to.
	 * @return The converted array.
	 */
	static Object adaptPrimArrayTypes(Object object, Class<?> cls) {
		if (object == null || !object.getClass().isArray()
			|| object.getClass().equals(cls))
			return object;
		Object target = null;
		try {
			target = Array.newInstance(cls.getComponentType(), Array
					.getLength(object));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		try {
			copyArrayRec(object, target);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return target;
	}

	/**
	 * Copy an array source to a target using a class specification.
	 * 
	 * @param source The source where to copy from.
	 * @param target The target to copy from.
	 * @param cls The class of the target.
	 */
	private static void copyArrayRec(Object source, Object target) {
		if (source.getClass().isArray()) {
			int len = Array.getLength(source);
			if (source.getClass().getComponentType().isArray()) {
				for (int i = 0; i < len; i++) {
					Object sourceValue = Array.get(source, i);
					Object childTarget = Array.newInstance(target.getClass()
							.getComponentType().getComponentType(), len);
					Array.set(target, i, childTarget);
					copyArrayRec(sourceValue, childTarget);
				}
			} else {
				for (int i = 0; i < len; i++) {
					Object sourceValue = Array.get(source, i);
					Array.set(target, i, sourceValue);
				}
			}
		}
	}

	/**
	 * Private constructor, access objects through using
	 * <code>accessForClass()</code>.
	 * 
	 * @param argCls The class to construct the <code>ReflectionAccess</code>
	 *            for.
	 */
	protected ReflectionAccess(Class<?> argCls) {
		this.cls = argCls;
		updateFields();
	}

	/**
	 * PropertyAccess defines how to access propertys of this class.
	 * 
	 * @author tweyde
	 */
	interface PropertyAccess {

		/**
		 * getProperty gets the property value of the object.
		 * 
		 * @param object The object of which to get the property value.
		 * @return The property value of the object.
		 */
		public Object getProperty(Object object);

		/**
		 * setProperty sets the value of the property of the object.
		 * 
		 * @param object The object of which the property is to be set.
		 * @param value The value to set the property to.
		 */
		public void setProperty(Object object, Object value);

		/**
		 * getType gets the class of objects for which this PropertyAccess
		 * works.
		 * 
		 * @return The class.
		 */
		public Class<?> getType();
	}

	/**
	 * <code>FieldAccess</code> encapsulates the access to properties as
	 * fields.
	 * 
	 * @author Tillman Weyde
	 */
	class FieldAccess implements PropertyAccess {

		private Field field;

		/**
		 * Create a <code>FieldAccess</code> for a <code>Field</code>
		 * object.
		 * 
		 * @param field The field to create the <code>FieldAccess</code> for.
		 */
		FieldAccess(Field field) {
			if (!Modifier.isPublic(field.getModifiers()))
				throw new IllegalArgumentException(
					"PropertyAccess may only be created for public fields.");
			this.field = field;
		}

		/**
		 * @see de.uos.fmt.musitech.utility.obj.ReflectionAccess.PropertyAccess#getProperty(java.lang.Object)
		 */
		public Object getProperty(Object object) {
			try {
				return field.get(object);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				assert false;
			}
			return null;
		}

		/**
		 * @see ReflectionAccess.PropertyAccess#setProperty(Object, Object)
		 */
		public void setProperty(Object object, Object value) {
			try {
				field.set(object, value);
				// field.set(object, adaptPrimArrayTypes(value,getType()));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				assert false; // only public fields should be created
			}
		}

		/**
		 * @see de.uos.fmt.musitech.utility.obj.ReflectionAccess.PropertyAccess#getType()
		 */
		public Class<?> getType() {
			return field.getType();
		}
	}

	/**
	 * Empty array used to indicate empty argument lists.
	 */
	static final Object zeroArgs[] = new Object[] {};

	/**
	 * <code>MethodAccess</code> encapsulates the access to properties by
	 * accessors.
	 * 
	 * @author Tillman Weyde
	 */
	class MethodAccess implements PropertyAccess {

		private Method methodGet;
		private Method methodSet;

		/**
		 * Create a new <code>MethodAccess</code> from a get and set method.
		 * 
		 * @param argMethodGet
		 * @param argMethodSet
		 */
		MethodAccess(Method argMethodGet, Method argMethodSet) {
			if (!Modifier.isPublic(argMethodGet.getModifiers())
				|| !Modifier.isPublic(argMethodSet.getModifiers()))
				throw new IllegalArgumentException(
					"MethodAccess may only be created for public get and set methods.");
			this.methodGet = argMethodGet;
			this.methodSet = argMethodSet;
		}

		/**
		 * @see de.uos.fmt.musitech.utility.obj.ReflectionAccess.PropertyAccess#getProperty(java.lang.Object)
		 */
		public Object getProperty(Object object) {
			try {
				return methodGet.invoke(object, zeroArgs);
			} catch (InvocationTargetException e) {
				// System.out.println("WARNING:" + e.getClass() + " " +
				// e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				assert false;
			}
			return null;
		}

		private final Object oneArg[] = new Object[1];

		/**
		 * @see de.uos.fmt.musitech.utility.obj.ReflectionAccess.PropertyAccess#setProperty(java.lang.Object,
		 *      java.lang.Object)
		 */
		public void setProperty(Object object, Object value) {
			// oneArg[0] = value;
			oneArg[0] = adaptPrimArrayTypes(value, getType());
			try {
				methodSet.invoke(object, oneArg);
			} catch (Exception e) {
				e.printStackTrace();
				assert false; // only public fields should be created
			}

		}

		/**
		 * @see de.uos.fmt.musitech.utility.obj.ReflectionAccess.PropertyAccess#getType()
		 */
		public Class<?> getType() {
			return methodGet.getReturnType();
		}
	}

	// for the names of fields and its property access objects.
	private Map<String, PropertyAccess> namesMap;

	/**
	 * <code>updateFields</code> updates the list of accessible fields in the
	 * class. It can be used to track changes at runtime. It stores all
	 * properties in a HashMap with their names.
	 * 
	 * @return The number of accessible fields found.
	 */
	public synchronized int updateFields() {
		namesMap = new HashMap<String, PropertyAccess>();
		// get all public fields
		Field fields[] = cls.getFields();
		// and write them
		for (int i = 0; i < fields.length; i++) {
			// no transient or final fields
			String name = fields[i].getName();
			// || Modifier.isTransient(fields[i].getModifiers())) { // TODO
			// reactive this
			if (Modifier.isPublic(fields[i].getModifiers())
				&& !Modifier.isFinal(fields[i].getModifiers())) {
				PropertyAccess ma = new FieldAccess(fields[i]);
				// htNames.put(name.toLowerCase(),ma);
				namesMap.put(name, ma);
			}
			continue;
		}
		// get all public methods
		Method mtds[] = cls.getMethods();
		allGetMethLabel: for (int i = 0; i < mtds.length; i++) {
			if (!Modifier.isPublic(mtds[i].getModifiers()))
				continue;
			if (mtds[i].getParameterTypes().length > 0)
				continue;
			for (Class<?> exclCls : excludeClassList) {
				Class<?> declClass = mtds[i].getDeclaringClass();
				if (declClass.equals(exclCls))
					continue allGetMethLabel;
			}
			String nameGet = mtds[i].getName();
			// we look for get methods
			if (nameGet.startsWith("get")) {
				String name = nameGet.substring(3);
				if (name.length() == 0)
					continue;
				// find corresponding set methods
				String corresponding = "set" + name;
				for (int j = 0; j < mtds.length; j++) {
					if (!Modifier.isPublic(mtds[j].getModifiers()))
						continue;
					if (mtds[j].getParameterTypes().length != 1)
						continue;
					if (!mtds[i].getReturnType().equals(
						mtds[j].getParameterTypes()[0]))
						continue;
					String nameSet = mtds[j].getName();
					// test whether the names match
					if (corresponding.equals(nameSet)) {
						PropertyAccess ma = new MethodAccess(mtds[i], mtds[j]);
						StringBuffer buffer = new StringBuffer(name);
						buffer.setCharAt(0, Character.toLowerCase(buffer
								.charAt(0)));
						name = buffer.toString();
						namesMap.put(name, ma);
						// htNames.put(name.toLowerCase(),ma);
					}
				}
			}
		}
		return namesMap.size();
	}

	/**
	 * <code>getPropertyNames</code> gets the names of all the accessible
	 * properties of this class. The name must begin with a lower case letter if
	 * the property is accessed by get and set methods.
	 * 
	 * @return An array with the names.
	 */
	public String[] getPropertyNames() {
		return namesMap.keySet().toArray(new String[] {});
	}

	/**
	 * <code>setProperty</code> sets the property <code>name</code> of
	 * object <code>obj</code> to value <code>value</code>. The name must
	 * begin with a lower case letter if the property is accessed by get and set
	 * methods.
	 * 
	 * @param obj the object to modify
	 * @param name the name of the property
	 * @param value the value to set the property to
	 * @return true if successful, else false (e.g. when Security restricitons
	 *         apply).
	 */
	public boolean setProperty(Object obj, String name, Object value) {
		if (!cls.isInstance(obj))
			throw new IllegalArgumentException("obj must be an instance of "
												+ cls);
		PropertyAccess pa = namesMap.get(name);
		if (pa == null) {
			char c = Character.toLowerCase(name.charAt(0));
			String name2 = c + name.substring(1);
			pa = namesMap.get(name2);
		}
		if (pa == null)
			return false;
		pa.setProperty(obj, value);
		return true;
	}

	/**
	 * getProperty gets the property <code>name</code> of object
	 * <code>obj</code>. The name must begin with a lower case letter if the
	 * property is accessed by get and set methods.
	 * 
	 * @param obj The object to get the property value from.
	 * @param name The name of the property.
	 * @return The value. Return value is null if the .
	 * @throws IllegalArgumentException is thown if the object is not an
	 *             instance of the right class, of if it is not an acdessible
	 *             property.
	 */
	public Object getProperty(Object obj, String name) {
		if (!cls.isInstance(obj))
			throw new IllegalArgumentException("obj must be an instance of "
												+ cls);
		PropertyAccess ma = namesMap.get(name);
		if (ma == null) {
			ma = namesMap.get(name.toLowerCase());
		}
		if (ma == null)
			throw new IllegalArgumentException(
				name + " does not specify a public property of " + cls);
		return ma.getProperty(obj);
	}

	/**
	 * getPropertyType gets the type of the property <code>name</code>. The
	 * name must begin with a lower case letter if the property is accessed by
	 * get and set methods.
	 * 
	 * @param name The name of the property.
	 * @return the type
	 */
	public Class<?> getPropertyType(String name) {
		PropertyAccess pa = namesMap.get(name);
		if (pa == null)
			throw new IllegalArgumentException(
				name + " does not specify a public property of " + cls);
		return pa.getType();
	}

	/**
	 * hasPropertyName checks whether a property with the name <code>name</code>
	 * is accessable. The name must begin with a lower case letter if the
	 * property is accessed by get and set methods.
	 * 
	 * @param name The name of the property.
	 * @return true if such a property is accessable
	 */
	public boolean hasPropertyName(String name) {
		String[] names = getPropertyNames();
		boolean found = false;
		for (int i = 0; i < names.length; i++) {
			if (name.equals(names[i]))
				found = true;
		}
		return found;
	}

	static private Map<Class<?>, ReflectionAccess> clasMap = new HashMap<Class<?>, ReflectionAccess>();

	/**
	 * accessForClass is the method to obtain instances of ReflectionAccess.
	 * There is no public Constructor. One ReflectionAccess instance per class
	 * is used.
	 * 
	 * @param cls
	 * @return the ReflectionAccess for this class.
	 */
	static public ReflectionAccess accessForClass(Class<?> cls) {
		ReflectionAccess ra = clasMap.get(cls);
		if (ra == null) {
			ra = new ReflectionAccess(cls);
			synchronized (clasMap) {
				clasMap.put(cls, ra);
			}
		}
		return ra;
	}

	/**
	 * The ExcludeClassList is the list of classes, whose interface will be
	 * excluded from access. This means that the get and set methods of this
	 * class will not be used. The interface of sub- and superclasses will
	 * however be used. This serves at the moment only to avoid problems with
	 * XML Beans XMLObjectBase.
	 * 
	 * @return The list of classes, whose interface will be ignored.
	 */
	public static final List<Class<?>> getExcludeClassList() {
		return excludeClassList;
	}

}
