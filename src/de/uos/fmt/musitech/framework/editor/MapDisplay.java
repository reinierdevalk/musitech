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
 * Created on 18.08.2004
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * @author Kerstin Neubarth
 *
 */
public class MapDisplay extends AbstractComplexDisplay {
    
    /**
     * Map to be displayed.
     * May be the <code>editObj</code> or the <code>propertyValue</code>.
     */
    Map map;
    
    /**
     * Collection containing the Displays used to display the elements of the
     * <code>map</code>.
     */
    Collection elementDisplays = new ArrayList();
    

    /**
     * Default display type to be used for the element displays.
     * Currently set to "Preview".
     */
    private final String ELEMENT_DISPLAYTYPE = "Preview";

    /**
     * Initiliazes this AbstractComplexDisplay. Sets the specified arguments as
     * the <code>editObj</code>,<code>profile</code> and
     * <code>rootDisplay</code> of this AbstractComplexDisplay. If the
     * <code>propertyName</code> in the <code>profile</code> is not null,
     * the display's <code>propertyName</code> and (via ReflectionAccess)
     * <code>propertyValue</code> are set. The children and element displays 
     * are created and the GUI is built. The display is registered at the 
     * DataChangeManager.
     * 
     * @param editObj
     *            Object to be displayed
     * @param profile
     *            EditingProfile specifying how to display the Object
     * @param root
     *            Display superordinated to this display, the top of the display
     *            hierarchy
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Display)
     */
    public void init(Object editObj, EditingProfile profile, Display root) {
        //set arguments
        this.editObj = editObj;
        this.profile = profile;
        if (profile != null && profile.getPropertyName() != null) {
            this.propertyName = profile.getPropertyName();
            setPropertyValue();
        }
        this.rootDisplay = root;
        //set collection
        if (editObj instanceof Map){
            map = (Map)editObj;
        } else 
            if (propertyValue instanceof Collection)
                map = (Map) propertyValue;
            else 
//                throw new EditorConstructionException("In CollectionDisplay: Neither the editObj nor the propertyValue is of type Collection.");
                return;
        //register at DataChangeManager
        Collection data = new ArrayList();
        if (propertyValue != null)
            data.add(propertyValue);
        else
            data.add(editObj);
        DataChangeManager.getInstance().interestExpandElements(this, data);
        //create children and element displays and build GUI
        createChildrenDisplays();
        createElementDisplays();
        createGUI();
    }
    
    /**
     * Creates Displays for the elements of the <code>map</code> and adds
     * them to the <code>elementDisplays</code>. The <code>label</code> of
     * the displays' EditingProfile is set to the corresponding key of the map.
     * The Collection <code>elementDisplays</code> is cleared before adding
     * the newly created displays.
     */
    private void createElementDisplays(){
        if (map.isEmpty())
            return;
        if (elementDisplays.size()>0)
            elementDisplays.clear();
        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            Object key = (Object) iter.next();
            Object element = map.get(key);
            Display elementDisplay = null;
            try {
                elementDisplay = EditorFactory.createDisplay(element, null, "Preview", rootDisplay);
                elementDisplay.getEditingProfile().setLabel(key.toString());
                elementDisplays.add(elementDisplay);
            } catch (EditorConstructionException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * Creates the GUI. Fills in the children displays and element displays.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#createGUI()
     */
    public void createGUI() {
        //panel for labels
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(0, 1));
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
		// panel for editors
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new GridLayout(0, 1));
		displayPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));
		//add children editors (wrapped by PopUpDisplays)
		if (children!=null && children.length>0){
		    for (int i = 0; i < children.length; i++) {
                String label = firstLetterUpperCase(children[i].getEditingProfile().getPropertyName());
		        labelPanel.add(new JLabel(label));
		        if (children[i] instanceof AbstractSimpleEditor)
		            displayPanel.add((AbstractSimpleEditor)children[i]);
		        else {
		            Display wrapper = EditorFactory.createPopUpWrapper(children[i]);
	                displayPanel.add((JComponent) wrapper);
		        }   
            }
		}
		//add element editors
		if (!elementDisplays.isEmpty()){
		    for (Iterator iter = elementDisplays.iterator(); iter.hasNext();) {
                Display element = (Display) iter.next();
                String label = element.getEditingProfile().getLabel();
                if (label==null)
                    label = EditorFactory.createDefaultProfile(element.getEditObj()).getLabel();
                label = firstLetterUpperCase(label);
                labelPanel.add(new JLabel(label));
                displayPanel.add((JComponent)element);
            }
		}
		// add label- and display-panels to panel 
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(labelPanel, BorderLayout.WEST);
		mainPanel.add(displayPanel, BorderLayout.CENTER);
		setLayout(new BorderLayout());
//		setBorder(BorderFactory.createTitledBorder(getLabeltext()));
		if (this==rootDisplay){
		    JScrollPane scrollPane = new JScrollPane(mainPanel);
			add(scrollPane, BorderLayout.CENTER);  
		} else
		    add(mainPanel, BorderLayout.CENTER);		
    }
    
    /** 
     * Removes this CollectionDisplay, its children displays and its element displays
     * from the table of the DataChangeManager.
     * Overwrites method <code>destroy()</code> of class AbstractComplexDisplay
     * in order to destroy the element displays as well.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
     */
    public void destroy(){
        super.destroy();
        if (!elementDisplays.isEmpty())
            for (Iterator iter = elementDisplays.iterator(); iter.hasNext();) {
                Display elementDisplay = (Display) iter.next();
                elementDisplay.destroy();
            }
    }
    
    /**
     * Returns the specified String with its first letter changed to an upper
     * case letter (if the String does not already begin with an upper case letter).
     * 
     * @param string String to be converted to a String beginning with upper case letter
     * @return String specified String beginning with an upper case letter
     */
    private String firstLetterUpperCase(String string){
        if (Character.isUpperCase(string.charAt(0)))
            return string;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Character.toUpperCase(string.charAt(0)));
        stringBuffer.append(string.substring(1));
        return stringBuffer.toString();
    }
    
}
