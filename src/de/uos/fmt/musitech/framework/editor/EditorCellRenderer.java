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
 * File EditorCellRenderer.java
 * Created on 12.07.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.Named;


class EditorCellRenderer
	implements javax.swing.tree.TreeCellRenderer, javax.swing.ListCellRenderer {

	/**
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean selected,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus) {
		EditorFactory.tcr.getTreeCellRendererComponent(
			tree,
			value,
			selected,
			expanded,
			leaf,
			row,
			hasFocus);

		TreePath path = tree.getPathForRow(row);
		mobject : {
			Object obj;
			if (path != null)
				obj = path.getLastPathComponent();
			else
				break mobject;
			TreeNode node;
			if (obj instanceof TreeNode)
				node = (TreeNode) obj;
			else
				break mobject;
			if (node instanceof DefaultMutableTreeNode)
				value = ((DefaultMutableTreeNode) node).getUserObject();
			else
				break mobject;
			if (value instanceof MObject) {
				return setCellRendererInfo(EditorFactory.tcr, (MObject) value);
			}
		}
		return EditorFactory.tcr;
	}

	/**
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus) {
		EditorFactory.lcr.getListCellRendererComponent(
			list,
			value,
			index,
			isSelected,
			cellHasFocus);

		Object o = list.getModel().getElementAt(index);
		if (o instanceof MObject)
			return setCellRendererInfo(EditorFactory.lcr, (MObject) o);
		return EditorFactory.lcr;
	}

	/**
	 * Return a component that has been configured to display the specified
	 * value. That component's <code>paint</code> method is then called to
	 * "render" the cell. If it is necessary to compute the dimensions of a
	 * list because the list cells do not have a fixed size, this method is
	 * called to generate a component on which <code>getPreferredSize</code>
	 * can be invoked.
	 */
	public Component setCellRendererInfo(JLabel cellLabel, MObject value) {

		EditingProfile ep = EditorFactory.getOrCreateProfile(value);
		Icon icon = (Icon) EditorFactory.classIconMap.get(value);
		if (ep != null) {
			cellLabel.setIcon(icon);
			String name = null;
			if (value instanceof Named){
				name = ((Named) value).getName();
			} else if(value != null){
			    String className = value.getClass().getName();
			    int dotIndex = className.lastIndexOf('.');
			    if(dotIndex >= 0){
				    name = className.substring(dotIndex+1);
			    }
			} else {
			    name = "null";
			}
			if (name != null && name.length() > 0)
				cellLabel.setText(name);
		}
		cellLabel.setToolTipText(null); //no tool tip
		return cellLabel;
	}

}