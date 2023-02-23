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
package de.uos.fmt.musitech.framework.user;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * User Class to represent a user.
 * 
 * @author TW
 * @version 0.109
 */
public class User implements java.io.Serializable {

	private java.lang.String lastName;
	private java.lang.String firstName;
	// the SHA-1 encrypted password
	private String password;
	private String loginName;
	private Calendar dateOfBirth = null;

	private Map<String,Object> propertyMap = new HashMap<String,Object>();
	private transient Map<String,Object> tempPropertyMap = new HashMap<String,Object>();

	/**
	 * Creates a user with a given first and last name.
	 * 
	 * @param fn The first name (including middle initial).
	 * @param ln The last name (family name).
	 */
	public User(String fn, String ln) {
		this.setFirstName(fn);
		this.setLastName(ln);
	}

	/**
	 * Create a new User object with initial information.
	 * 
	 * @param fn The first name(s) (incl. middle initial).
	 * @param ln The user's last name (family name).
	 * @param login The login name.
	 * @param passwd The encrypted password.
	 */
	public User(String fn, String ln, String login, String passwd) {
		this.setFirstName(fn);
		this.setLastName(ln);
		this.setLoginName(login);
		this.setPassword(passwd);
	}

	/**
	 * Default constructor, creates an empty user object.
	 */
	public User() {
	}

	/**
	 * Gets the user's first name(s). If there is more than one name they should
	 * be separated by blanks.
	 * 
	 * @return The user's first name(s).
	 */
	public java.lang.String getFirstName() {
		return firstName;
	}

	/**
	 * Gets the user's last name. If there is more than one name they should be
	 * separated by blanks.
	 * 
	 * @return The user's last name.
	 */
	public java.lang.String getLastName() {
		return lastName;
	}

	/**
	 * Sets the user's first name(s). If there is more than one name they should
	 * be separated by blanks.
	 * 
	 * @param string The user's first name(s)
	 */
	public void setFirstName(java.lang.String string) {
		firstName = string;
	}

	/**
	 * Sets the user's first name(s). If there is more than one name they should
	 * be separated by blanks.
	 * 
	 * @param string The user's last name.
	 */
	public void setLastName(java.lang.String string) {
		lastName = string;
	}

	/**
	 * Returns the first and last name.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getFirstName() + " " + getLastName() + " (" + getLoginName() + ")";
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the loginName.
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * Sets the loginName.
	 * 
	 * @param loginName The loginName to set.
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	/**
	 * Sets a temporary property of this user.
	 * 
	 * @param key The key string.
	 * @param value The value to put.
	 * @return The old value for this key (<code>null</code> if there was
	 *         none).
	 */
	public Object putProperty(String key, Object value) {
		return propertyMap.put(key, value);
	}

	/**
	 * Gets an application-defined property of this user.
	 * 
	 * @param key The key for the property.
	 * @return The stored value, <code>null</code> if there is none.
	 */
	public Object getProperty(String key) {
		return propertyMap.get(key);
	}


	/**
	 * Sets a property of this user.
	 * 
	 * @param key The key string.
	 * @param value The value to put.
	 * @return The old value for this key (<code>null</code> if there was
	 *         none).
	 */
	public Object putTempProperty(String key, Object value) {
		return tempPropertyMap.put(key, value);
	}

	/**
	 * Gets an application-defined property of this user.
	 * 
	 * @param key The key for the property.
	 * @return The stored value, <code>null</code> if there is none.
	 */
	public Object getTempProperty(String key) {
		return tempPropertyMap.get(key);
	}

	/**
	 * Gets this user's property map, only to be used for persistence.
	 * 
	 * @return Returns the propertyMap.
	 */
	public Map getPropertyMap() {
		return propertyMap;
	}

	/**
	 * Sets this user's property map, only to be used for persistence.
	 * 
	 * @param propertyMap The propertyMap to set.
	 */
	public void setPropertyMap(Map propertyMap) {
		this.propertyMap = propertyMap;
	}

	public void setDateOfBirth(Calendar dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Calendar getDateOfBirth() {
		return dateOfBirth;
	}
	
}