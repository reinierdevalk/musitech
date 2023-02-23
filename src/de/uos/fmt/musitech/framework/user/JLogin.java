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
 * File Login.java Created on 26.05.2004
 */

package de.uos.fmt.musitech.framework.user;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author Ashley Martens
 * @version 1.0 JLogin provides a dialog to get the login name and the password
 *          from the user. The following example shows the simple use of JLogin.
 *          <br>
 *          <br>
 *          <b>Example: </b> <br>
 *          <p>
 *          JLogin logpane = new JLogin("My Application:Login"); <br>
 *          if(logpane.showDialog() == JLogin.LOGIN_OPTION) {<br>
 *          String user_name = logpane.getUserName(); <br>
 *          String password = logpane.getPassword(); <br>}<br>
 *          else <br>
 *          return; <br>
 *          <p>
 */
public class JLogin extends JDialog {

	/** Return value from class method if Login is choosen. */
	public static final int LOGIN_OPTION = 0;
	/** Return value from class method if OK is choosen. */
	public static final int OK_OPTION = 1;
	/** Return value from class method if Cancel is choosen. */
	public static final int CANCEL_OPTION = 2;

	private static boolean modal = true;

	/**
	 * Defaults to a modal dialog box
	 */
	public JLogin() {
		this(null, Messages.getString("JLogin.Login"));} //$NON-NLS-1$

	/**
	 * Creates a modal dialog box with the standard title.
	 * 
	 * @param owner The owner frame.
	 */
	public JLogin(java.awt.Frame owner) {
		this(owner, Messages.getString("JLogin.Login"));} //$NON-NLS-1$

	/**
	 * Defaults to a modal dialog box
	 * 
	 * @param title The title of the dialog.
	 */
	public JLogin(String title) {
		this(null, title);
	}

	/**
	 * Creates a login pane with dialog title and username.
	 * 
	 * @param owner The frame which owns Dialog.
	 * @param title The title for the Dialog.
	 */
	public JLogin(java.awt.Frame owner, String title) {
		super(owner, title, modal);

		Container pane = this.getContentPane();

		// ---> Constructing Components
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel labelLogin = new JLabel(Messages.getString("JLogin.UserName")), //$NON-NLS-1$
		labelPassword = new JLabel(Messages.getString("JLogin.Password")); //$NON-NLS-1$
		m_usernameField = new JTextField(20);
		String sUser = System.getProperty("user.name");
		if (sUser != null) {
			m_usernameField.setText(sUser);
		}
		m_passwordField = new JPasswordField(12);
		m_passwordField = new JPasswordField(12);
		m_buttonLogin = new JButton(Messages.getString("JLogin.Login")); //$NON-NLS-1$
		m_buttonCancel = new JButton(Messages.getString("JLogin.Cancel")); //$NON-NLS-1$

		// ---> Add Listeners
		m_buttonLogin.addActionListener(m_eventHandler);
		m_buttonCancel.addActionListener(m_eventHandler);
		m_usernameField.addActionListener(m_eventHandler);
		m_passwordField.addActionListener(m_eventHandler);

		// ---> Adding Components to Panel
		java.awt.Dimension d1 = labelLogin.getPreferredSize(), d2 = labelPassword
				.getPreferredSize(), d3 = m_usernameField.getPreferredSize(), d4 = m_passwordField
				.getPreferredSize(), d5 = m_buttonLogin.getPreferredSize(), d6 = m_buttonCancel
				.getPreferredSize();
		int firstColWidth = (d1.width > d2.width) ? d1.width : d2.width, firstRowHeight = (d1.height > d3.height)	? d1.height
																													: d3.height, secondRowHeight = (d2.height > d4.height)	? d2.height
																																											: d4.height;

		int firstColumn = 12, secondColumn = firstColumn + firstColWidth + 10, firstRow = 12, secondRow = firstRow
																											+ firstRowHeight
																											+ 5, thirdRow = secondRow
																															+ secondRowHeight
																															+ 17, panelWidth = secondColumn
																																				+ d3.width
																																				+ 11, buttonRowWidth = d5.width
																																										+ d6.width
																																										+ 12
																																										+ 10
																																										+ 11;
		if (buttonRowWidth > panelWidth) {
			panelWidth = buttonRowWidth;
		}

		labelLogin.setBounds(firstColumn, firstRow, d1.width, d1.height);
		panel.add(labelLogin);
		m_usernameField.setBounds(secondColumn, firstRow, d3.width, d3.height);
		panel.add(m_usernameField);
		labelPassword.setBounds(firstColumn, secondRow, d2.width, d2.height);
		panel.add(labelPassword);
		m_passwordField.setBounds(secondColumn, secondRow, d4.width, d4.height);
		panel.add(m_passwordField);

		m_buttonLogin.setBounds(panelWidth / 2 - 5 - d5.width, thirdRow, d5.width, d5.height);
		panel.add(m_buttonLogin);
		m_buttonCancel.setBounds(panelWidth / 2 + 5, thirdRow, d6.width, d6.height);
		panel.add(m_buttonCancel);

		panel.setSize(panelWidth, thirdRow + d5.height + 11);
		panel.setPreferredSize(panel.getSize());

		// ---> Add the panel to the content pane
		pane.add(panel);
		pane.setSize(panel.getPreferredSize());

		this.pack();
		this.getRootPane().setDefaultButton(m_buttonLogin);
		this.setResizable(false);

		// ---> Center the dialog on the screen or the owner
		Dimension screenSize;
		if (owner == null) /* Calculate the screen size */
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		else
			screenSize = owner.getSize();

		/* Center frame on the screen */
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		int screenPosX = 0;
		int screenPosY = 0;
		if(owner != null) {
			screenPosX = owner.getX();
			screenPosY = owner.getY();
		}
		this.setLocation(screenPosX + (screenSize.width - frameSize.width) / 2,
			screenPosY + (screenSize.height - frameSize.height) / 2);

	}

	/**
	 * Shows a modal login dialog with a dialog title, username and blocks until
	 * the dialog is hidden. If the user presses the "OK" button, then this
	 * method hides/disposes the dialog and returns the selected button value.
	 * If the user presses the "Cancel" button or closes the dialog without
	 * pressing "OK", then this method hides/disposes the dialog and returns
	 * null.
	 * 
	 * @return the return state of the login pane: OK_OPTION or CANCEL_OPTION.
	 */
	public int showDialog() {
		m_usernameField.requestFocus();
		super.show();
		return this.getValue();
	}

	public int getValue() {
		return m_res;
	}

	/**
	 * Gets the user name.
	 */
	public String getUserName() {
		return m_usernameField.getText();
	}

	/**
	 * Sets the users name to username.
	 */
	public void setUserName(String username) {
		m_usernameField.setText(username);
	}

	/**
	 * Returns the password string.
	 */
	public String getPassword() {
		if (m_res == LOGIN_OPTION)
			return new String(m_passwordField.getPassword());
		else
			return "";
	}

	/**
	 * Returns the character to be used for echoing. The default is character is
	 * '*'.
	 */
	public char getEchoChar() {
		return m_passwordField.getEchoChar();
	}

	/**
	 * Sets the echo character for the password field.. Note that this is
	 * largely a suggestion to the view as the view that gets installed can use
	 * whatever graphic techniques it desires to represent the field. Setting a
	 * value of 0 unsets the echo character.
	 */
	public void setEchoChar(char c) {
		m_passwordField.setEchoChar(c);
		return;
	}

	public static void main(String args[]) {
		// try {
		// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		JLogin logpane = new JLogin();
		if (logpane.showDialog() == JLogin.LOGIN_OPTION) {
			System.out.println(logpane.getUserName());
			System.out.println(logpane.getPassword());
		}
		System.exit(0);
	}

	/**
	 * EventHandler handles all events in the dialog.
	 * 
	 * @author tweyde
	 */
	protected class EventHandler implements java.awt.event.ActionListener {

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == m_passwordField || e.getSource() == m_usernameField) {
				m_buttonLogin.doClick();
			} else {
				if (e.getSource() == m_buttonLogin)
					m_res = LOGIN_OPTION;
				if (e.getSource() == m_buttonCancel)
					m_res = CANCEL_OPTION;
				JLogin.this.dispose();
			}
		};
	};

	protected EventHandler m_eventHandler = new EventHandler();
	private JButton m_buttonLogin;
	private JButton m_buttonCancel;
	private JPasswordField m_passwordField;
	private JTextField m_usernameField;
	private int m_res = CANCEL_OPTION;
}