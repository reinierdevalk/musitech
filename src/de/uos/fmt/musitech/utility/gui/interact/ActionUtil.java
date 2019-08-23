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
/**
 * File ActionUtil.java created 01/2003 by Tillman Weyde
 */

package de.uos.fmt.musitech.utility.gui.interact;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 * The ActionUtil provides utility methods to handle Swing Actions.
 * 
 * @author Tillman Weyde
 */
public class ActionUtil {

	/**
	 * The method addAction adds an Action to a menu and a toolbar working
	 * around a bug in the JToolbar.add(Action) method.
	 * 
	 * @param action The Action to add.
	 * @param menu The menu to add the Action to. May be <code>null</code>.
	 * @param toolbar The toolbar to add the Action to. May be <code>null</code>.
	 */
	public static void addAction(Action action, JMenu menu, JToolBar toolbar) {
		if (menu != null) {
			menu.add(new JMenuItem(action));
		} // end if
		if (toolbar != null) {
			JButton button = toolbar.add(action);
			// if(action.getValue(Action.MNEMONIC_KEY) != null){
			// button.setMnemonic((char) ((Integer)
			// action.getValue(Action.MNEMONIC_KEY)).intValue());
			// } else {
			try {
				// this is a workaround for the bug in Toolbar.add(Action)
				button.setMnemonic((char) 0);
				Object description = action.getValue(Action.SHORT_DESCRIPTION);
				if (description == null) {
					description = action.getValue(Action.NAME).toString();
				}
				if (description == null) {
					description = action.getValue(Action.LONG_DESCRIPTION)
							.toString();
				}
				if (description == null) {
					description = "";
				}
				String descText = description.toString();
				Object shortcut = action.getValue(Action.ACCELERATOR_KEY);
				if (shortcut != null) {
					descText += shortcut.toString();
				}
				if (description == null) {
					button.setToolTipText(descText);
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			} // end try/catch
			button.setFocusable(false); // only available in JDK 1.4
			button.setRequestFocusEnabled(false);
		} // end if
	}

	/**
	 * The method configureAction sets properties of the the action passed
	 * 
	 * @param action The action to configure.
	 * @param keyStroke The keyStroke for the Accelerator.
	 * @param menmonic The mnemonic key to set.
	 * @param name The name to display, will also be used as short Description.
	 * @return Action Returns the action passed in the <code>action</code>
	 *         parameter.
	 */
	public static Action configureAction(Action action, KeyStroke keyStroke,
											int menmonic,
											String name) {

		if (action != null) {
			action.putValue(Action.ACCELERATOR_KEY, keyStroke);
			action.putValue(Action.MNEMONIC_KEY, new Integer(menmonic));
			action.putValue(Action.ACTION_COMMAND_KEY, name);
			action.putValue(Action.NAME, name);
			action.putValue(Action.SHORT_DESCRIPTION, name);
		}
		return action;
	}

	/**
	 * The method configureAction sets properties of the the action passed
	 * 
	 * @param action The action to configure.
	 * @param keyStroke The keyStroke for the Accelerator.
	 * @param menmonic The mnemonic key to set.
	 * @param name The name to display, will also be used as short Description.
	 * @param commandString The command name used in the actionEvents.
	 * @return Action Returns the action passed in the <code>action</code>
	 *         parameter.
	 */
	public static Action configureAction(Action action, KeyStroke keyStroke,
											int menmonic,
											String name,
											String commandString) {
		if (action != null) {
			action.putValue(Action.ACCELERATOR_KEY, keyStroke);
			action.putValue(Action.MNEMONIC_KEY, menmonic);
			action.putValue(Action.NAME, name);
			action.putValue(Action.SHORT_DESCRIPTION, name);
			action.putValue(Action.ACTION_COMMAND_KEY, commandString);
		}
		return action;
	}

	/**
	 * The method configureAction sets properties of the the action passed
	 * 
	 * @param action The action to configure.
	 * @param keyStroke The keyStroke for the Accelerator.
	 * @param menmonic The mnemonic key to set.
	 * @param displayName The name to display (no the button or menu-item).
	 * @param shortDescription The short description e.g. for ToolTips.
	 * @param commandString The command name used in the actionEvents.
	 * @return Action Returns the action passed in the <code>action</code>
	 *         parameter.
	 */
	public static Action configureAction(Action action, KeyStroke keyStroke,
											int menmonic, String displayName,
											String shortDescription,
											String commandString) {

		if (action != null) {
			action.putValue(Action.ACCELERATOR_KEY, keyStroke);
			action.putValue(Action.MNEMONIC_KEY, new Integer(menmonic));
			action.putValue(Action.NAME, displayName);
			action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
			action.putValue(Action.ACTION_COMMAND_KEY, commandString);
		}
		return action;
	}

	/**
	 * The method configureAction sets properties of the the action passed
	 * 
	 * @param action The action to configure.
	 * @param keyStroke The keyStroke for the Accelerator.
	 * @param menmonic The mnemonic key to set.
	 * @param displayName The name to display (no the button or menu-item).
	 * @param shortDescription The short description e.g. for ToolTips.
	 * @param commandString The command name used in the actionEvents.
	 * @param icon
	 * @return Action Returns the action passed in the <code>action</code>
	 *         parameter.
	 */
	public static Action configureAction(Action action, KeyStroke keyStroke,
											int menmonic, String displayName,
											String shortDescription,
											String commandString, Icon icon) {

		if (action != null) {
			action.putValue(Action.ACCELERATOR_KEY, keyStroke);
			action.putValue(Action.MNEMONIC_KEY, new Integer(menmonic));
			action.putValue(Action.NAME, displayName);
			action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
			action.putValue(Action.ACTION_COMMAND_KEY, commandString);
			action.putValue(Action.SMALL_ICON, icon);
		}
		return action;
	}

}
