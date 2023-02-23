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
 * Created on 17.08.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * An AbstractComplexDisplay is a Display using children displays for the properties
 * of the object to display.
 * This abstract class provides methods for setting up a complex display and its 
 * children displays, but does not define a GUI.
 * 
 * @author Kerstin Neubarth
 *
 */
abstract public class AbstractComplexDisplay extends AbstractDisplay {
	
	/**
	 * Array of Displays used to display the properties of the editObj
	 */
    Display[] children;

	/** 
	 * Initiliazes this AbstractComplexDisplay. Sets the specified arguments as
	 * the <code>editObj</code>, <code>profile</code> and <code>rootDisplay</code>
	 * of this AbstractComplexDisplay. If the <code>propertyName</code> in the
	 * <code>profile</code> is not null, the display's <code>propertyName</code>
	 * and (via ReflectionAccess) <code>propertyValue</code> are set. The children
	 * displays are created and the GUI is built. The display is registered at the
	 * DataChangeManager.
	 * 
	 * @param editObj Object to be displayed
	 * @param profile EditingProfile specifying how to display the Object
	 * @param root Display superordinated to this display, the top of the display hierarchy
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object, de.uos.fmt.musitech.framework.editor.EditingProfile, de.uos.fmt.musitech.framework.editor.Display)
	 */
	@Override
	public void init(Object editObj, EditingProfile profile, Display root) {
		//set arguments
	    this.editObj = editObj;
		this.profile = profile;
		if (profile != null && profile.getPropertyName() != null) {
			this.propertyName = profile.getPropertyName();
			setPropertyValue();
		}
		this.rootDisplay = root;
		//register at DataChangeManager
		Collection data = new ArrayList();
		if (propertyValue != null)
			data.add(propertyValue);
		else
			data.add(editObj);
		DataChangeManager.getInstance().interestExpandElements(this, data);
		//create children displays and build GUI
		createChildrenDisplays();
		createGUI();
	}
	
	/**
	 * Creates the Displays for displaying the properties of the <code>editObj</code>
	 * and sets them as <code>children</code>.
	 * The children displays are created according to the children profiles 
	 * in this display's <code>profile</code>. If the <code>profile</code> does 
	 * not specify children profiles default children profiles are created via
	 * the EditorFactory. In this case all accessible properties will be displayed.
	 */
	protected void createChildrenDisplays(){
	    //if profile does not specify children, create default children profiles
	    if (profile.getChildren()==null || profile.getChildren().length<1){
	        String[] propertyNames;
		    //get property names of the editObj
		    ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
		    propertyNames = ref.getPropertyNames();
		    if (propertyNames!=null && propertyNames.length>0)
		        profile.setChildren(EditorFactory.createChildrenProfiles(editObj, propertyNames)); 
	    }
	    //create children displays
	    if (profile.getChildren()!=null && profile.getChildren().length>0){
	        Collection childrenDisplays = new ArrayList();
	        for (int i = 0; i < profile.getChildren().length; i++) {
                EditingProfile childProfile = profile.getChildren()[i];
                childProfile.setReadOnly(true);
                //get first display type in profile (or take "PopUp" as default)
                String displayType = "PopUp";
                for (int j = 0; j < childProfile.getEditortypes().length; j++) {
                    if (EditorFactory.isDisplay(childProfile.getEditortypes()[j])){
                        displayType = childProfile.getEditortypes()[j];
                        break;
                    }
                }
                //create child display according to child profile
                Display childDisplay = null;
                try {
                    childDisplay = EditorFactory.createDisplay(editObj, childProfile, null, rootDisplay);
                    childrenDisplays.add(childDisplay);
                } catch (EditorConstructionException e) {
                    e.printStackTrace();
                }
               
            }
	        //set children
	        if (childrenDisplays.size()>0){
	            //create new children Array, if children null or to "clear" current Array
	            if (children==null || children.length>0)
	                children = new Display[childrenDisplays.size()];
	            int i=0;
	            for (Iterator iter = childrenDisplays.iterator(); iter.hasNext();) {
                    Display display = (Display) iter.next();
                    children[i++] = display;
                }     
	        }
	    }  
	}
		
	/** 
	 * Removes this AbstractComplexDisplay and its children displays from the
	 * table of the DataChangeManager.
	 * Overwrites method <code>destroy()</code> of class AbstractDisplay to destroy
	 * the children displays as well.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
	 */
	@Override
	public void destroy(){
	    super.destroy();
	    if (children!=null && children.length>0)
	        for (int i = 0; i < children.length; i++) {
                children[i].destroy();
            }
	}

}
