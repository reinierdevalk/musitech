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
 * File SimpleUserManager.java
 * Created on 25.05.2004
 */

package de.uos.fmt.musitech.framework.user;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorWindow;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * A user manager provides data about users.
 * 
 * @author Tillman Weyde
 */
public class SimpleUserManager {

	/**
	 * TODO this is public only for persistence, this should be changed when
	 * persistence implementation allows.
	 */
	// public List<User> userList = Collections.synchronizedList(new
	// ArrayList<User>());
	public List<User> userList = new ArrayList<User>();

	/**
	 * This is not public to allow singleton implementations.
	 */
	protected SimpleUserManager() {
	}

	/**
	 * This methods tries to authenticate a user by asking for a user-name and
	 * password.
	 * 
	 * @param parent The parent component.
	 * @return The user who has successfully loged in, null else.
	 */
	public User login(Frame parent) {
		User user = null;
		JLogin login = new JLogin(parent, "Login");
		do {
			if (login.showDialog() == JLogin.CANCEL_OPTION)
				return null;
			else
				user = lookupUser(login.getUserName());
			// lookup the user
			// if (user == null) {
			// if (JOptionPane.showConfirmDialog(parent,
			// "Sorry, this user is not known.\nTry again ?", "Login Problem",
			// JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
			// break;
			// else
			// continue;
			// }
			String passwd = null;
			if (user != null) {
				// compare the encrypted passwords
				try {
					passwd = EncryptService.getInstance().encrypt(
						login.getPassword());
				} catch (SystemUnavailableException e) {
					if (DebugState.DEBUG)
						e.printStackTrace();
					JOptionPane
							.showMessageDialog(
								parent,
								"Sorry, the login failed because the encryption service is not available."
										+ " \n Please ask your system administrator for help.");
					break;
				}
			}
			if (user == null || !passwd.equals(user.getPassword())) {
				if (JOptionPane
						.showConfirmDialog(
							parent,
							"Sorry, the user "
									+ login.getUserName()
									+ " with the password you entered is not known.\nTry again ?",
							"Login Problem", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
					break;
				else {
					user = null;
					continue;
				}
			}
		} while (user == null);
		return user;
	}

	/**
	 * This methods tries to authenticate a user by username and password.
	 * 
	 * @param username The Username.
	 * @param password The Password.
	 * @return The user who has successfully loged in, null else.
	 */
	public User login(String username, String passwd) {
		User user = lookupUser(username);
		if (user != null) {
			// compare the encrypted passwords
			try {
				passwd = EncryptService.getInstance().encrypt(passwd);
			} catch (SystemUnavailableException e) {
				if (DebugState.DEBUG)
					e.printStackTrace();
				JOptionPane
						.showMessageDialog(
							null,
							"Sorry, the login failed because the encryption service is not available."
									+ " \n Please ask your system administrator for help.");
			}
		}
		if (user != null && passwd.equals(user.getPassword())) {
			return user;
		}
		return null;
	}

	/**
	 * lookup a user for a given login name.
	 * 
	 * @param userName the login name
	 * @return the user object
	 */
	protected User lookupUser(String userName) {
		if (userName == null)
			return null;
		for (Iterator iter = userList.iterator(); iter.hasNext();) {
			User user = (User) iter.next();
			if (userName.equals(user.getLoginName()))
				return user;
		}
		return null;
	}

	/**
	 * Create a new user in the database. The parameters are used to create the
	 * user. If the user can not be registered in the database (e.g. duplicate
	 * username), an error message is returned, a return value <code>null</code>
	 * indicates successful registration of the user.
	 * 
	 * @param fName The user's first name.
	 * @param lName The user's last name.
	 * @param login The user's loginName
	 * @param password The user's password (clear text)
	 * @return null if sucessful, an error message if user could not be created
	 */
	public String createUser(String fName, String lName, String login,
								String password) {
		if (lookupUser(login) != null)
			return "Login name '" + login + "' already exists.";
		try {
			EncryptService encServ = EncryptService.getInstance();
			String encPwd = encServ.encrypt(password);
			User user = new User(fName, lName, login, encPwd);
			userList.add(user);
		} catch (SystemUnavailableException e) {
			return "Password encryption service unavailable.";
		}
		return null;
	}

	/**
	 * Just for testing.
	 * 
	 * @param args Ignored.
	 */
	public static void main(String[] args) {
		SimpleUserManager sum = new SimpleUserManager();
		User testUser1 = new User("Mark", "Test", "mtest",
			"qUqP5cyxm6YcTAhz05Hph5gvu9M=");
		sum.userList.add(testUser1);
		User testUser2 = new User("Peter", "Schmidt", "pschmidt",
			"qUqP5cyxm6YcTAhz05Hph5gvu9M=");
		sum.userList.add(testUser2);
		User user = sum.login(null);
		try {
			Editor editor = EditorFactory.createEditor(user);
			EditorWindow edWin = new EditorWindow("User data");
			edWin.addEditor(editor);
			edWin.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}
}
