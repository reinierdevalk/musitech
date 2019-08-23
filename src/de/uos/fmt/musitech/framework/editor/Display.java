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
 * File Display.java created on 11.12.2003 by tweyde.
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Component;

import de.uos.fmt.musitech.framework.change.DataChangeListener;

/**
 * Interface to be implemented by classes which represent a display. A Display
 * only shows its data, but does not allow editing, that is data cannot be
 * changed inside this Display. (But the Display should adapt if its data have
 * been changed in another editor which is simultaneously operating on the same
 * objects.)
 * 
 * @author tweyde
 */
public interface Display extends DataChangeListener {

	/**
	 * Checks if data of this Display or one of its subordinate Displays
	 * (children, grandchildren etc. if there are any) has been changed from
	 * outside. This method must be called by the <code>rootDisplay</code> to
	 * check whether there are conflicting changes. <br>
	 * As the Display is functioning as a DataChangeListener, this method
	 * returns the flag <code>dataChanged</code>, which is set when the
	 * Display receives a DataChangeEvent (see method <code>dataChanged()</code>
	 * in interface <code>DataChangeListener</code>).
	 * 
	 * @return true if data has been changed from outside this editor hierarchy,
	 *         false otherwise
	 */
	abstract public boolean externalChanges();

	/**
	 * This method is called when the Display will not be needed any more. It
	 * should release all resources currently held and unregister from
	 * DataChangeManager and SelectionManager if applicable.
	 */
	abstract public void destroy();

	/**
	 * When the Display gains the focus, it has to evaluate whether data has
	 * been changed and to react appropriately, e.g. by updating or overwriting.
	 * This is controlled by the <code>rootDisplay</code>. So, if this
	 * Display is not the <code>rootDisplay</code>, it calls method
	 * <code>focusReceived()</code> of its <code>rootDisplay</code>. If
	 * this Display is the <code>rootDisplay</code>, it updates the editor
	 * (in case of a simple Display) or it offers the user to accept or
	 * overwrite changes (if it is an Editor which allows editing), in case data
	 * have been changed.
	 */
	abstract public void focusReceived();

	/**
	 * Returns the EditingProfile of this Display. <br>
	 * The EditingProfile holds information about how to display an object.
	 * 
	 * @return Editing Profile of this Display
	 */
	public abstract EditingProfile getEditingProfile();

	/**
	 * Getter for <code>editObj</code>. The <code>editObj</code> is the
	 * object displayed by this display.
	 * 
	 * @return Object displayed by this Display
	 */
	public abstract Object getEditObj();

	/**
	 * Returns true if the Display (resp. one of its focusable components) has
	 * the focus. <br>
	 * In general, the flag <code>dataChanged</code> is evaluated when an
	 * editor gains the focus. Additionally, a Display can already have the
	 * focus. This method allows to check for the latter condition. TODO können
	 * sich dann aber überhaupt Daten extern ändern?
	 * 
	 * @return true if this editor has the focus
	 */
	public boolean isFocused();

	/**
	 * Records the specified arguments (all arguments must not be null),
	 * registers the Display at the DataChangeManager and builds the GUI. If an
	 * Display or Editor should mark selected data (e.g. by highlighting), it
	 * must also register at the SelectionManager. <br> - The
	 * <code>editObj</code> is the object to be displayed by this Display.
	 * <br> - The <code>profile</code> holds basic information about what
	 * (e.g. which property of the object, indicated by the
	 * <code>propertyName</code>, if only a property shall be displayed) and
	 * how (e.g. with which type of display, see <code>editortype</code>) the
	 * Display should display. <br> - The <code>rootDisplay</code> is the
	 * outmost Display of the display hierarchy. <br>
	 * This Display should not use a JScrollPane, if it is not the
	 * <code>rootDisplay</code>. If this Display is the
	 * <code>rootDisplay</code>, it is responsible for scrolling, at least if
	 * it is used inside an EditorPanel or EditorWindow (which uses an
	 * EditorPanel) because the EditorPanel does not provide a scroll pane.
	 * 
	 * @param editObject Object to be displayed (or Object a property of which
	 *            is to be displayed)
	 * @param profile EditingProfile specifying how to display the
	 *            <code>editObj</code>
	 * @param rootDisplay Display being the outmost Display of the display
	 *            hierarchy
	 */
	public void init(Object editObject, EditingProfile profile,
						Display rootDisplay);

	/**
	 * Updates the Display, after being updated the Display contains the values
	 * read from <code>editObj</code>. The flag <code>dataChanged</code> of
	 * the Display is to be set to false.
	 */
	public abstract void updateDisplay();

	/**
	 * Getter for <code>rootDisplay</code>. <br>
	 * The <code>rootDisplay</code> is the top Display of a display hierarchy.
	 * E.g. given a complex display having children displays, the outmost
	 * complex display is the <code>rootDisplay</code> of its children
	 * displays (and grandchildren etc., if there are several levels of
	 * displays). <br>
	 * The <code>rootDisplay</code> might be this Display itself.
	 * 
	 * @return Display <code>rootDisplay</code> of this Display
	 */
	public Display getRootDisplay();

	/**
	 * Displays are (normally) Swing/AWT Components, and this methods allow them to
	 * be used as such without casting.
	 * 
	 * @return This display as a Component.
	 */
	public Component asComponent();
}
