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
 * Created on 30.06.2004
 */
package de.uos.fmt.musitech.framework.editor;

import javax.swing.ImageIcon;

/**
 * Class for objects gathering information about an editor type.
 * 
 * @author Kerstin Neubarth
 */
public class EditorType {

	/**
	 * Name of the editor type.
	 * <br>
	 * This name is used in the EditingProfiles. TODO oder ändern?
	 */
	String typeName;

	/**
	 * String representation of the editor's class name.
	 * <br>The fully qualified path is required.
	 * TODO HashMap short names to fully qualified names? (dann reicht hier Kurzform) 
	 */
	String editorClassName;

	/**
	 * Icon to represent the editor type.
	 * <br>
	 * This icon may be used in an Explorer view, for example. 
	 */
	ImageIcon icon;

	/**
	 * Empty constructor.
	 */
	public EditorType() {
	}

	/**
	 * Constructor.
	 * Sets the specified arguments as <code>typeName</code>, <code>editorClassName</code>
	 * and <code>icon</code> of this EditorType resp.
	 * 
	 * @param name String giving the name of the editor type
	 * @param classname String giving the name of the editor's class
	 * @param icon ImageIcon representing the editor type
	 */
	public EditorType(String name, String classname, ImageIcon icon) {
		typeName = name;
		editorClassName = classname;
		this.icon = icon;
	}

	/**
	 * Setter for <code>typeName</code>.
	 * 
	 * @param typeName String giving the name of the editor type
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * Getter for <code>typeName</code>.
	 * 
	 * @return String typeName name of the editor type
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Setter for <code>editorClassName</code>.
	 * <br>
	 * The specified String must give the fully qualified name of the editor class.
	 * 
	 * @param editorClassName String giving the fully qualified name of the editor class
	 */
	public void setEditorClassName(String editorClassName) {
		this.editorClassName = editorClassName;
	}

	/**
	 * Getter for <code>editorClassName</code>.
	 * 
	 * @return String giving the fully qualified name of the editor class
	 */
	public String getEditorClassName() {
		return editorClassName;
		//TODO falls Variable Kurzform des Names, Wert aus HashMap
	}

	/**
	 * Setter for <code>icon</code>.
	 * 
	 * @param icon ImageIcon to represent this EditorType
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	/**
	 * Getter for <code>icon</code>.
	 * 
	 * @return ImageIcon representing this EditorType
	 */
	public ImageIcon getIcon() {
		return icon;
	}

}
