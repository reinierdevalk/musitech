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
package de.uos.fmt.musitech.utility;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

/**
 * Implements a menu for internal windows.
 * @version 0.113
 */
public class InternalWindowMenu extends javax.swing.JMenu implements java.awt.event.ActionListener, java.awt.event.ContainerListener {

	protected JDesktopPane desktop;


	public java.util.Hashtable hash = new Hashtable();

	/**
	 * Adds a component.
	 * @param e java.awt.event.ContainerEvent
	 */
	public void componentAdded(java.awt.event.ContainerEvent e) {
		createMenuItems();
	}

	/**
	 * Removes a component.
	 * @param e java.awt.event.ContainerEvent
	 */
	public void componentRemoved(java.awt.event.ContainerEvent e) {
		createMenuItems();
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		Object source = e.getSource();
		if (source != null && source instanceof JMenuItem) {
			JInternalFrame intFrame = (JInternalFrame) hash.get(e.getSource());
			try {
				intFrame.setIcon(false);
				intFrame.setSelected(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Creates a menu item that corresponds to the given internal
	 * frame and adds it to the menu and to the hashtable.
	 * Creation date: (09.12.00 20:02:35)
	 * @param: intFrame javax.swing.JInternalFrame The internal Frame.
	 */
	protected void createMenuItems() {
		removeAll();
		hash.clear();
		if (desktop == null)
			return;
		JInternalFrame[] intFrames = desktop.getAllFrames();
		Arrays.sort(intFrames, new Comparator() {
			public int compare(Object o1, Object o2) {
				JInternalFrame j1 = (JInternalFrame) o1;
				JInternalFrame j2 = (JInternalFrame) o2;
				if (j1 == null || j1.getTitle() == null)
					return -1;
				if (j2 == null || j2.getTitle() == null)
					return 1;
				return j1.getTitle().compareTo(j2.getTitle());
			}
			public boolean equals(Object obj) {
				return this == obj;
			}
		});
		for (int i = 0; i < intFrames.length; i++) {
			String title = intFrames[i].getTitle();
			if (title == null || title.equals(""))
				title = " ";
			JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(title, intFrames[i].isSelected());
			menuItem.addActionListener(this);
			hash.put(menuItem, intFrames[i]);
			add(menuItem);
		}
	}

	/**
	 * Set the container which contains the internal frames for this menu.
	 * Creation date: (11.12.00 01:45:42)
	 * @param:
	 * @return:
	 */
	public void setDesktop(JDesktopPane newDesktop) {
		if (newDesktop == desktop)
			return;
		if (desktop != null)
			desktop.removeContainerListener(this);
		desktop = newDesktop;
		createMenuItems();
		desktop.addContainerListener(this);
	}
}