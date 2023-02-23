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
 * Created on 23.05.2003
 */
package de.uos.fmt.musitech.framework.editor;

import javax.swing.Icon;

import de.uos.fmt.musitech.data.utility.IEquivalence;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * Profile for an editor for primitives and objects. <br>
 * <br>
 * There are two types of editors and therefore two types of editorprofiles.
 * "Simple editors" are editors which are meant to edit primitives and
 * "uncomplicated" object like <code>String</code> or
 * <code>de.uos.fmt.musitech.utility.Rational</code>. "Complex editors" are
 * meant to be able to edit more complex objects. The idea is that the complex
 * editors do not edit any object or primitive themselves but refer to another
 * editor (a "child") for each field of the object which should be editable.
 * This is defined recursively of course. <br>
 * <br>
 * In simple editors you specify: <br> - a label to be displayed <br> - the
 * property to be edited <br> - one or more editortypes with one being the
 * default type (optional) <br>
 * If no editor type is given (i. e. editorType is null or has length zero) the
 * editor constructing process will look for a default editor according to the
 * type of the field. <br>
 * <br>
 * In complex editors you specify: <br> - a label <br> - profiles of the
 * "children" <br> - one or more editortypes (optinal) <br> - (optional) if the
 * object itself is a proeprty of another object (e. g. ScoreNote is a property
 * of Note) then the profile of the complex editor should have set the
 * <code>propertyName</code> to the appropriate name. <br>
 * If <code>propertyName</code> is not set it is assumed that the profile is
 * for an object which is not a property of another object and therefor for an
 * "outmost" editor (see package description for more detail).
 * 
 * @author tobias widdra
 * @see de.uos.fmt.musitech.framework.editor.Editable
 */
public class EditingProfile implements IEquivalence {

	/**
	 * The label displayed in the editor.
	 */
	String label;

	/**
	 * Specifications of different types of editors. The fist one is chosen as
	 * default editor.
	 */
	String[] editortype;

	/**
	 * The name of the property to be edited. <br>
	 * This field is unused for the outmost editorprofile but must be set for
	 * all other editorprofiles, i. e. all profiles which specify a child of
	 * some other profile have to set this field!
	 */
	String propertyName;

	/**
	 * Profiles of the children.
	 */
	EditingProfile[] children;

	/**
	 * The readOnly property indicates whether the object should be editable in
	 * this editor.
	 */
	boolean readOnly = false;

	/**
	 * Array of icons to represent this editor. The first element of the Array
	 * is used as the default icon.
	 */
	Icon[] icons;

	// ------------------------- empty constructor -----------------------

	/**
	 * Empty constructor.
	 */
	public EditingProfile() {
	}

	// --------------------- constructors for simple editors
	// ---------------------

	/**
	 * Constructor for constructing a simple but specific editorprofile.
	 * 
	 * @param label to be displayed
	 * @param editortype to be used
	 * @param name of propertyName to be edited
	 */
	public EditingProfile(String label, String editortype, String propertyName) {
		this.label = label;
		this.editortype = new String[] {editortype};
		this.propertyName = propertyName;
	}

	/**
	 * Constructor for constructing a simple but specific editorprofile with a
	 * set of different editortypes.
	 * 
	 * @param label to be displayed
	 * @param editortypes to be used (first one is default)
	 * @param name of propertyName to be edited
	 */
	public EditingProfile(String label, String[] editortype, String propertyName) {
		this.label = label;
		this.editortype = editortype;
		this.propertyName = propertyName;
	}

	/**
	 * Constructor for constructing a simple editorprofile with default values.
	 * Label and propertyName are equal to the parameter <code>label</code>.
	 * For the editorType the default type will be choosen (if editorType is
	 * null in the profile, the constructing process looks for a default
	 * editor).
	 * 
	 * @param propertyName, equal to label
	 */
	public EditingProfile(String propertyName) {
		this.label = propertyName;
		this.propertyName = propertyName;
	}

	// ------------- constructors for complex editors ---------------------

	/**
	 * Constructor for constructing complex editors with default editorType.
	 * 
	 * @param label to be displayed
	 * @param profiles of children
	 */
	public EditingProfile(String label, EditingProfile[] children) {
		this.label = label;
		this.children = children;
	}

	/**
	 * Constructor for constructing complex editors of a specific type.
	 * 
	 * @param label to be displayed
	 * @param editortype
	 * @param children
	 */
	public EditingProfile(String label, EditingProfile[] children,
			String editortype) {
		this.label = label;
		this.editortype = new String[] {editortype};
		this.children = children;
	}

	/**
	 * Constructor for constructing complex editors with a set of specific
	 * alternative editortypes. The first one is set to be the default choice.
	 * 
	 * @param label to be displayed
	 * @param editortype
	 * @param children
	 */
	public EditingProfile(String label, EditingProfile[] children,
			String[] editortype) {
		this.label = label;
		this.propertyName = label;
		this.editortype = editortype;
		this.children = children;
	}

	// -------------------- getter and setter ------------------------

	/**
	 * Returns the default editortype. This the editor stored under
	 * editortype[0].
	 * 
	 * @return default editortype
	 */
	public String getDefaultEditortype() {
		if ((editortype == null) || (editortype.length == 0)) {
			return null;
		}
		return editortype[0];
	}

	/**
	 * @return childrenprofiles
	 */
	public EditingProfile[] getChildren() {
		return children;
	}

	/**
	 * @return editortypes
	 */
	public String[] getEditortypes() {
		return editortype;
	}

	/**
	 * @return propertyName to be edited
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Getter for <code>icons</code>.
	 * 
	 * @return icons
	 */
	public Icon[] getIcons() {
		return icons;
	}

	/**
	 * Returns the first Icon of the Array <code>icons</code>.
	 * 
	 * @return Icon being the first element of <code>icons</code>
	 */
	public Icon getDefaultIcon() {
		return icons[0];
	}

	/**
	 * Sets the editortypes. The first editor in the array is set to be the
	 * default editor.
	 * 
	 * @param editortypes
	 */
	public void setEditortype(String[] string) {
		editortype = string;
		for (int i = 0; i < string.length; i++) {
			if (!EditorFactory.isEditor(string[i])) {
				if (DebugState.DEBUG)
					System.out
							.println("WARNING in EditingProfile.setEditortype:\n"
										+ "The type name '"
										+ string[i]
										+ "' refers to a Display, but not to an Editor.");
			}
		}
	}

	/**
	 * Sets editortype with name <code>editortype</code> as the only
	 * editortype.
	 * 
	 * @param editortype
	 */
	public void setEditortype(String editortype) {
		this.editortype = new String[] {editortype};
		if (EditorFactory.isEditor(editortype)) {
			// TODO check concept
			// if (DebugState.DEBUG)
			// System.out.println("WARNING in EditorProfile.setEditortype:\n"
			// + "The specified editortype '"+editortype+"' refers to a Display,
			// but not to an Editor.");
		}
	}

	/**
	 * Sets the propertyName
	 * 
	 * @param propertyName
	 */
	public void setPropertyName(String string) {
		propertyName = string;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label
	 */
	public void setLabel(String string) {
		label = string;
	}

	/**
	 * Sets children to the given profiles.
	 * 
	 * @param profiles of children
	 */
	public void setChildren(EditingProfile[] profiles) {
		children = profiles;
	}

	/**
	 * Gets the ReadOnly property.
	 * 
	 * @return true if this object is read-only
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Sets the ReadOnly property.
	 * 
	 * @param b
	 */
	public void setReadOnly(boolean b) {
		readOnly = b;
		// Modif190504
		if (children != null && children.length > 0) {
			for (int i = 0; i < children.length; i++) {
				children[i].setReadOnly(b);
			}
		}
		// end Modif190504
	}

	/**
	 * Setter for <code>icons</code>.
	 * 
	 * @param icon
	 */
	public void setIcons(Icon icon) {
		this.icons = new Icon[] {icon};
	}

	/**
	 * Setter for <code>icons</code>.
	 * 
	 * @param icons
	 */
	public void setIcons(Icon[] icons) {
		this.icons = icons;
	}

	// ------------------------------- helper -----------------------

	/**
	 * Returns whether a simple or a complex editor is specified by this
	 * profile. <br>
	 * <br>
	 * Returns true if (and only if) children == null. Notice that this makes it
	 * possible to specify a complex editor with actually no children: If
	 * children.length == 0 false is returned!
	 * 
	 * @return true if children == null, false otherwise
	 */
	public boolean isSimpleEditor() {
		return children == null;
	}

	/**
	 * Returns a String representation of the profile.
	 */
	@Override
	public String toString() {

		String s = "EditingProfile: '" + label + "'";
		if (propertyName != null)
			s += "(" + propertyName + ")";
		if (children != null) {
			s += ", [";
			for (int i = 0; i < children.length; i++) {
				s += children[i].toString() + "  ";
			}
			s += "]";
		}

		return s;
	}

	@Override
	public boolean isEquivalent(IEquivalence object) {
		// if( !(object instanceof EditingProfile) ) return false;
		// TODO implement editing profile method if needed
		/*
		 * Method[] cmNote = object.getClass().getMethods(); for(int i=0; i <
		 * cmNote.length; ++i) { String methodName = cmNote[i].getName();
		 * if(methodName.startsWith("get") && !"getUid".equals(methodName) ) try {
		 * Class type = cmNote[i].getReturnType(); if(type.isPrimitive()) { if(
		 * !EqualsUtil.areEqual( cmNote[i].invoke(this,null) ,
		 * cmNote[i].invoke(object, null) ) ) { System.out.println("Testing for " +
		 * methodName); return false; } } else { boolean isEqualityType = false;
		 * for(Class c:type.getInterfaces()) if( "IEquality".equals(c.getName()) )
		 * isEqualityType = true; if( isEqualityType && !EqualsUtil.areEqual(
		 * (IEquality) cmNote[i].invoke(this, null) , (IEquality)
		 * cmNote[i].invoke(object, null) ) ) return false; else if (
		 * !EqualsUtil.areEqual( cmNote[i].invoke(this, null) ,
		 * cmNote[i].invoke(object, null) ) ) return false; } } catch
		 * (IllegalArgumentException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IllegalAccessException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (InvocationTargetException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
		return true;
	}

}
