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
 * Created on 28.01.2005
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.Collection;
import java.util.Map;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Editor for editing RenderingHints. The editor shows a list of rendering keys 
 * mapping to values.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class RenderingHintsEditor extends AbstractEditor {

    private Map hintsMap;
    private MapEditor mapEditor;

    /**
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#applyChanges()
     */
    public void applyChanges() {
        if (profile.isReadOnly())
            return;
        if (mapEditor != null) {
            Collection editedData = getEditedData();
            //apply changed values
            mapEditor.applyChanges();
            //send DataChangeEvent
            if (rootDisplay == this)
                sendDataChangeEvent(editedData);
        }
        //reset flags
        dataChanged = false;
        dirty = false;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#updateDisplay()
     */
    public void updateDisplay() {
        if (mapEditor != null) {
            mapEditor.updateDisplay();
            revalidate();
        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    protected void createGUI() {
        if (hintsMap != null) {
            try {
                mapEditor = (MapEditor) EditorFactory.createEditor(hintsMap, this, "Map");
                add(mapEditor);
            } catch (EditorConstructionException e) {
                e.printStackTrace();
            } catch (ClassCastException cce) {
                cce.printStackTrace();
            }
        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
     */
    public Collection getEditedData() {
        return mapEditor.getEditedData();
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    public boolean isFocused() {
        boolean mapEditorFocused = false;
        if (mapEditor!=null)
            mapEditorFocused = mapEditor.isFocused();
        return (mapEditorFocused || isFocusOwner());
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Display)
     */
    public void init(Object editObject, EditingProfile profile, Display rootDisplay) {
        this.editObj = editObject;
        this.profile = profile;
        if (profile != null) {
            if (profile.getPropertyName() != null) {
                this.propertyName = profile.getPropertyName();
            }
            setPropertyValue();
        }
        determineLocalObj();
        if (rootDisplay != null)
            this.rootDisplay = rootDisplay;
        registerAtChangeManager();
        createGUI();
    }

    private void determineLocalObj() {
        if (propertyValue != null && propertyValue instanceof RenderingHints) {
            hintsMap = ((RenderingHints)propertyValue).getContentsMap();
        } else {
            if (editObj instanceof RenderingHints) {
                hintsMap = ((RenderingHints)editObj).getContentsMap();
            }
        }
    }

}