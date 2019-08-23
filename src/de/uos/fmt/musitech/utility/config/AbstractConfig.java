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
package de.uos.fmt.musitech.utility.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.uos.fmt.musitech.utility.obj.*;

/**
 * Abstract basic class for configuration objects.
 * @date (04.09.00 11:54:28)
 * @author TW
 * @version 0.109 
 */
public abstract class AbstractConfig implements java.beans.VetoableChangeListener {
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected java.util.HashSet propertySet = new java.util.HashSet();
	protected boolean doCheck = true;

	/**
	 *	@author: Alexander Luedeke
	 *	This field solves a problem: You have three VetoListenableJTextFields
	 *	in DllPathConfigDialog for example. Each field has got a wrong
	 *	value. If you correct the first value and leave the field. The
	 *	method vetoableChange will be executed. But the method checks
	 *	all three fields. checkValues will throw a vetoPropertyException,
	 *	because the other two field have wrong values.
	 *	The field checkAllProperties solves this problem if you set its value
	 *	to false,
	 */
	protected transient boolean checkAllProperties = false;
	protected java.util.Map map;
	protected java.util.Map tmpMap;
	/**
	 * Constructor
	 */
	protected AbstractConfig() {
		super();
		addVetoableChangeListener(this);
		// initialize the propertySet
		Method methods[] = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (!methods[i].getName().startsWith("set"))
				continue;
			StringBuffer nameBuf = new StringBuffer(methods[i].getName().substring(3));
			nameBuf.setCharAt(0, Character.toLowerCase(nameBuf.charAt(0)));
			String name = nameBuf.toString();
			propertySet.add(name);
		}
		map = new java.util.HashMap();
		tmpMap = new java.util.HashMap();
	}
	/**
	 * The addPropertyChangeListener method was 
	 * generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	/**
	 * The addPropertyChangeListener method was generated to 
	 * support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(propertyName, listener);
	}
	/**
	 * The addVetoableChangeListener method was generated to 
	 * support the vetoPropertyChange field.
	 */
	public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoPropertyChange().addVetoableChangeListener(listener);
	}
	/**
	 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
		getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
	}
	/**
	 * Write the values to the file
	 * @date (09.09.00 00:12:48)
	 * @return boolean
	 */
	public boolean commitValues() {
		writeValues(this);
		if (!checkValues()) {
			javax.swing.JOptionPane.showMessageDialog(null, "Combination of Values is not valid.");
			return false;
		}
		saveToFile();
		java.util.Map.Entry[] entries = (java.util.Map.Entry[]) tmpMap.entrySet().toArray(new java.util.Map.Entry[] {});
		for (int i = 0; i < tmpMap.size(); i++) {
			map.put(entries[i].getKey(), entries[i].getValue());
		}
		return true;
	}
	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
		getPropertyChange().firePropertyChange(evt);
	}
	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	/**
	 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
	 */
	public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
		getVetoPropertyChange().fireVetoableChange(evt);
	}
	/**
	 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
	 */
	public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
		getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
	}
	/**
	 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
	 */
	public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
		getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
	}
	/**
	 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
	 */
	public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
		getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
	}
	/**
	 * Accessor for the propertyChange field.
	 */
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	/**
	 * Accessor for the vetoPropertyChange field.
	 */
	protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
		if (vetoPropertyChange == null) {
			vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoPropertyChange;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		// exception.printStackTrace(System.out);
	}
	/**
	 * The hasListeners method was generated to support the propertyChange field.
	 */
	public synchronized boolean hasListeners(java.lang.String propertyName) {
		return getPropertyChange().hasListeners(propertyName);
	}
	/**
	 * Get the values from a file.
	 * @date (09.09.00 00:12:48)
	 * @return boolean
	 */
	public void readFromFile() {
		java.io.File f = new java.io.File(getPath());
		doCheck = false;
		try {
			ObjectCopy.readXML(this, new java.io.FileInputStream(f));
		} catch (Exception e) {
		}
		writeValues(this);
		doCheck = true;
	}
	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(propertyName, listener);
	}
	/**
	 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
		getVetoPropertyChange().removeVetoableChangeListener(listener);
	}
	/**
	 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
		getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
	}
	/**
	 * Save the values to the file.
	 * @date (09.09.00 00:12:48)
	 * @return boolean
	 */
	public void saveToFile() {
		ObjectCopy.writeXML(this, new java.io.File(getPath()));
	}
	/**
	 * This method gets called when a constrained property is changed.
	 *
	 * @param     evt a <code>PropertyChangeEvent</code> object describing the
	 *   	      event source and the property that has changed.
	 * @exception PropertyVetoException if the recipient wishes the property
	 *              change to be rolled back.
	 */
	public void vetoableChange(java.beans.PropertyChangeEvent pce) throws java.beans.PropertyVetoException {
		if (!isProperty(pce.getPropertyName()))
			return;
		tmpMap.put(pce.getPropertyName(), pce.getNewValue().toString());
		if (checkAllProperties)
			if (doCheck && !checkValues()) {
				throw new java.beans.PropertyVetoException("Not an allowed value in this context.", pce);
			}
		if (!checkAllProperties)
			if (doCheck && !checkValues(pce.getPropertyName())) {
				throw new java.beans.PropertyVetoException("Not an allowed value in this context. " + pce.getPropertyName(), pce);
			}
	}
	/**
	 * Writes the values of this object to the tmpMap.
	 * @date (09.09.00 00:12:48)
	 * @return boolean
	 */
	public void writeValues(Object object) {
		Method methods[] = object.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (!methods[i].getName().startsWith("get") || methods[i].getName().equals("getClass"))
				continue;
			StringBuffer nameBuf = new StringBuffer(methods[i].getName().substring(3));
			nameBuf.setCharAt(0, Character.toLowerCase(nameBuf.charAt(0)));
			String name = nameBuf.toString();
			String value;
			try {
				value = methods[i].invoke(object, new Object[] {}).toString();
				tmpMap.put(name, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Check if the current values are valid
	 * @date (16.12.00 18:54:14)
	 */
	protected abstract boolean checkValues();


	/**
	 * Check if the current values are valid
	 * @date (16.12.00 18:54:14)
	 */
	protected abstract boolean checkValues(String propertyName);


	/**
	 * Checks all properties.
	 * @date (09.09.00 00:12:48)
	 * @return boolean
	 */
	protected boolean getCheckAllProperties() {
		return checkAllProperties;
	}


	/**
	 * Path of the file to save the values.
	 * @date (22.01.01 10:15:30)
	 * @return java.lang.String
	 */
	abstract protected String getPath();


	/**
	 * Is the argument a property name?
	 * @date (02.01.01 10:27:00)
	 * @return boolean
	 * @param name java.lang.String
	 */
	public boolean isProperty(String name) {
		return propertySet.contains(name);
	}


	/**
	 * Sets the boolean flag CheckAllProperties
	 * @date (09.09.00 00:12:48)
	 * @return boolean
	 */
	public void setCheckAllProperties(boolean checkAllPropertiesValue) {
		checkAllProperties = checkAllPropertiesValue;
	}


	/**
	 * Sets the map for this config object.
	 * @date (23.01.01 19:47:37)
	 * @return java.util.Map
	 */
	public void setMap(java.util.Map newMap) {
		map = newMap;
		tmpMap = (java.util.Map) ObjectCopy.copyObject(map);
		return;
	}


	/**
	 * The method gets the set-method form the subclass for the property
	 * "name" and sets the "value".
	 * @date (01.01.01 14:58:33)
	 * @param name java.lang.String
	 * @param value java.lang.Object
	 */
	public void setProperty(String name, Object value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		if (!isProperty(name))
			return;
		StringBuffer sb = new StringBuffer(name);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		sb.insert(0, "set");
		String setName = sb.toString();
		Method method;
		try {
			method = getClass().getMethod(setName, new Class[] {value.getClass()});
		} catch (NoSuchMethodException e) {
			method = getClass().getMethod(setName, new Class[] {ObjectCopy.primitiveForWrapper(value.getClass())});
		}
		method.invoke(this, new Object[] {value});
		tmpMap.put(name, value);
	}
}