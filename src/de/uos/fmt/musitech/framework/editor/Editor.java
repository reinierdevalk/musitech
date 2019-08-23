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
 * Created on 28.10.2003
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.Collection;


/**
 * Interface to be implemented by all classes defining editors as
 * parts of the MUSITECH Editor-Framework.
 * <br>
 * Such editors may be created using the EditorFactory, are controlled by the
 * DataChangeManager when working simultaneously and may be embedded in an editor
 * hierarchy.
 * 
 * @author Kerstin Neubarth
 */
public interface Editor extends Display {
	
	/**
	 * This method should get invoked if an event occurs which indicates that the
	 * changes done by the editor should be applied to the <code>editObj</code>.
	 * The flag <code>dataChanged</code> of the editor is to be set to false.
	 * If this Editor is the outmost editor of the editor hierarchy (i.e. it
	 * is the <code>rootEditor</code>), it sends a DataChangeEvent to the
	 * DataChangeManager.
	 */
	public abstract void applyChanges();
		
	
	/**
	 * Checks if data of this editor or one of its subordinate 
	 * editors (children, grandchildren etc. if there are any) 
	 * has been changed from outside. This method must be 
	 * called by the <code>rootEditor</code> when it gets the focus.
	 * 
	 * @return true if data has been changed from outside this editor hierarchy, false otherwise
	 */
//	abstract public boolean externalChanges();


	/** 
	 * Returns true if the user's input is a valid 
	 * input for this editor.
	 * 
	 * @return true if the user's input is a valid input for this editor, false otherwise
	 */
	public abstract boolean inputIsValid();

	/** 
	 * Returns data that has been edited by the user and is 
	 * not yet written back to the object.
	 * If no objects have been changed, an empty Collection is returned.
	 * TODO in implementierenden Klassen prüfen (empty, nicht null) 
	 * 
	 * @return Collection containing objects having been changed by the user
	 */
	public Collection getEditedData();
	
	/**
	 * Records the specified boolean.
	 * <br>
	 * The return value is evaluated when the Editor gets the focus
	 * and has to react to changes in its data (if data has changed).
	 * See method <code>focusReceived()</code>.
	 * <br>
	 * If the flag <code>promptUpdate</code> of an Editor is false,
	 * the Editor updates automatically to external changes as long as
	 * there have not been any changes in the Editor itself. 
	 * I.e. it does update automatically only if there are no conflicting 
	 * changes.
	 * <br>
	 * If the flag <code>promptUpdate</code> of an Editor is true,
	 * the Editor notifies the user before updating even if there are
	 * no confilcting changes. E.g., the <code>AbstractEditor</code> 
	 * and its subclasses ask the user if he wants to accept the changes 
	 * (i.e. update) or overwrite the external changes with his own data.
	 * <br>
	 * The default value of the flag <code>promptUpdate</code> in 
	 * <code>AbstractEditor</code> is false.
	 * 
	 * @param promptUpdate boolean indicating if update should not be performed automatically
	 */
	public void setPromptUpdate(boolean promptUpdate);
	
	/**
	 * Returns true if the editor should notify the user before updating to data
	 * changed from outside, false otherwise.
	 * <br>Getter for a flag <code>promptUpdate</code>.
	 * <br><br>
	 * This method is used by the PopUpEditor to set the flag <code>promptUpdate</code>
	 * of its <code>editorToPopUp</code>.
	 * 
	 * @return boolean true means that update should not be performed automatically
	 */
//	public boolean getPromptUpdate();
	
	/**
	 * Records the specified boolean value in a flag boolean <code>dirty</code>. 
	 * The value indicates whether data have been changed, but changes have not been
	 * applied to the <code>editObj</code> yet.
	 * <br>
	 * The flag must be set "true" whenever the user changes data in an editor.
	 * Subordinate editors performing data changes must also set the <code>dirty</code>
	 * flag of their root editor true. 
	 * The flag must be reset "false" in <code>applyChanges()</code>.
	 * (When the user applies his changes to the <code>editObj</code>, the method
	 * <code>applyChanges()</code> is first invoked in the root editor which then
	 * propagates <code>applyChanges()</code> to its children and/or element editors
	 * (if it is a complex editor). So, each editor itself resets the flag to false
	 * when performing <code>applyChanges()</code>.)
	 * <br>
	 * The flag should be evaluated by the root editor when it gets the focus.
	 * At that moment, the root checks for external changes. If data has been changed
	 * externally and the flag <code>dirty</code> is true, there are conflicting 
	 * changes. In this case, the user should be notified before updating the editor.
	 * E.g., the class <code>AbstractEditor</code> offers the user to update the editor
	 * or to overwrite the current values in the <code>editObj</code> with his own 
	 * changes.
	 * 
	 * @param dirty boolean indicating if data is changed in the editor
	 */
	public void setDirty(boolean dirty);


}
