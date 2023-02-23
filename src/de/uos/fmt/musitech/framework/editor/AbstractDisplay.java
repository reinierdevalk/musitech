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
 * Created on 28.07.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;

import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * Abstract class implementing the interface Display.
 * 
 * @author Kerstin Neubarth
 *  
 */
abstract public class AbstractDisplay extends JPanel implements Display {

    /**
     * Object to be displayed.
     */
    Object editObj;

    /**
     * EditingProfile specifying how to display the <code>editObj</code>.
     */
    EditingProfile profile;

    /**
     * Top display of the display hierarchy this Display is situated in. Might
     * be this Display itself.
     */
    Display rootDisplay;

    /**
     * String indicating which property of the <code>editObj</code> to
     * display. Might be null. In this case, the <code>editObj</code> itself
     * is displayed.
     */
    String propertyName = null;

    /**
     * Value of the property indicated by <code>propertyName</code>. The
     * <code>propertyValue</code> is set via ReflectionAccess in the
     * <code>init(Object, EditingProfile, Display)</code> method. Might be
     * null.
     */
    Object propertyValue = null;

    /**
     * Gets the propertyValue, which may be null if the propertyName is the
     * object to be edited. The <code>propertyValue</code> is set in the
     * <code>init(Object, EditingProfile, Display)</code> method. 
     * 
     * @return The propertyValue.
     */
    public Object getPropertyValue() {
        return propertyValue;
    }

    /**
     * Boolean recording if data has been changed in an external Editor. Is set
     * true, when this Display receives a DataChangeEvent. (As this object is a
     * Display and not an Editor, changes to data are always external changes.)
     */
    private boolean dataChanged = false;

    /**
     * Returns <code>dataChanged</code>.<br>
     * As this object is a Display and not an Editor, changes to data are always
     * external changes.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
     */
    @Override
	public boolean externalChanges() {
        return dataChanged;
    }

    /**
     * Removes this Display from the table of the DataChangeManager. Displays
     * registered at the SelectionManager must overwrite this method, to resign
     * from the SelectionManager as well.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
     */
    @Override
	public void destroy() {
        DataChangeManager.getInstance().removeListener(this);
    }

    /**
     * Updates the Display, if data has been changed externally, i.e. if
     * <code>externalChanges()</code> returns true (which in this class is
     * equivalent to <code>dataChanged</code> being true). After updating the
     * display, <code>dataChanged</code> is reset to false.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
     */
    @Override
	public void focusReceived() {
        if (externalChanges())
            updateDisplay();
        dataChanged = false;
    }

    /**
     * Returns the EditingProfile of this Display.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
     */
    @Override
	public EditingProfile getEditingProfile() {
        return profile;
    }

    /**
     * Returns the <code>editObj</code> of this Display.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
     */
    @Override
	public Object getEditObj() {
        return editObj;
    }

    /**
     * Returns true, if this Display is the focus owner, false otherwise.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    @Override
	public boolean isFocused() {
        return isFocusOwner();
    }

    /**
     * Initiliazes this Display. The Display's <code>editObj</code>,
     * <code>profile</code> and <code>rootDisplay</code> are set to the
     * specified arguments. If the <code>propertyName</code> specified in the
     * EditingProfile is not null, the Display's <code>propertyName</code> is
     * set and the <code>propertyValue</code> is set via ReflectionAccess. The
     * Display is registered at the DataChangeManager and the GUI is built.
     * Displays implementing the interface SelectionListener must overwrite this
     * method to register themselves at the SelectionManager as well.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Display)
     */
    @Override
	public void init(Object argEditObject, EditingProfile argProfile, Display root) {
        this.editObj = argEditObject;
        this.profile = argProfile;
        if (profile != null && profile.getPropertyName() != null) {
            this.propertyName = profile.getPropertyName();
            setPropertyValue();
        }
        this.rootDisplay = root;
        Collection<Object> data = new ArrayList<Object>();
        if (propertyValue != null)
            data.add(propertyValue);
        else
            data.add(editObj);
        DataChangeManager.getInstance().interestExpandElements(this, data);
        createGUI();
    }

    /**
     * Sets the <code>propertyValue</code> of this Display via
     * ReflectionAccess if <code>propertyName</code> is not null and the
     * <code>editObj</code> has a property of this name.
     */
    protected void setPropertyValue() {
        ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
        if (propertyName != null && ref.hasPropertyName(propertyName)) {
            propertyValue = ref.getProperty(editObj, propertyName);
        }
    }

    /**
     * Updates the Display's GUI by removing all components and calling
     * <code>createGUI()</code>. Should be overwritten in inheriting 
     * classes with a more efficient implementation.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
     */
    @Override
	public void updateDisplay() {
        removeAll();
        createGUI();
        revalidate();
    }

    /**
     * Returns the <code>rootDisplay</code> of this Display.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getRootDisplay()
     */
    @Override
	public Display getRootDisplay() {
        return rootDisplay;
    }

    /**
     * Sets <code>dataChanged</code> true.
     * 
     * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
     */
    @Override
	public void dataChanged(DataChangeEvent e) {
        dataChanged = true;
        if (isFocused())
			focusReceived();
    }

    /**
     * Creates the graphical user interface.
     */
    abstract public void createGUI();

    /**
     * If the display's EditingProfile specifies a <code>label</code>, this
     * label is converted to a text beginning with an upper case letter. Else
     * the default label provided by the EditorFactory is returned.
     * 
     * @return String label of this Display
     */
    //TODO possibly spaces within ?
    public String getLabelText() {
        String label = "";
        if (profile.getLabel() != null)
            label = profile.getLabel();
        else {
            label = EditorFactory.createDefaultProfile(editObj).getLabel();
            if (label.endsWith("Editor")) {
                StringBuffer defaultLabel = new StringBuffer();
                defaultLabel.append(label.substring(0, label.length() - "Editor".length()));
                defaultLabel.append(" Display");
                label = defaultLabel.toString();
            }
        }
       // return with first letter upper case
//        if (!Character.isUpperCase(label.charAt(0))) {
//            StringBuffer buffer = new StringBuffer();
//            buffer.append(Character.toUpperCase(label.charAt(0)));
//            buffer.append(label.substring(1));
//            label = buffer.toString();
//        }
        return label;
    }
    
	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	@Override
	public Component asComponent() {
		return this;
	}

}