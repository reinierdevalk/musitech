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
 * Created on 06.10.2004
 *
 */
package de.uos.fmt.musitech.metadata;

import java.awt.BorderLayout;

import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.AbstractComplexEditor;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * The ContextEditor edits the MetaDataCollection associated with
 * the <code>editObj</code> (a Container). It delegates editing to an internal
 * MetaDataEditor.
 * 
 * @author Kerstin Neubarth
 *
 */
public class ContextEditor extends AbstractComplexEditor {
    
    /**
     * Container to be edited.
     * Might be the <code>editObj</code>
     */
    Container container = null;
    
    /**
     * MetaDataCollection to be edited.
     */
    MetaDataCollection metadata = null;
    
    /**
     * MetaDataEditor used to edit the <code>metadata</code>.
     */
    MetaDataEditor metaDataEditor = null;
    
    /**
     * Initiliazes this ContextEditor by setting its fields according to the
     * specified arguments.
     * Sets the <code>editObj</code> <code>profile</code> and <code>rootDisplay</code>
     * of the ContextEditor. If the <code>propertyName</code> in the EditingProfile
     * is not null, the ContextEditor's <code>propertyName</code> and
     * <code>propertyValue</code> are set.
     * The <code>container</code> of the ContextEditor is set either to the
     * <code>editObj</code> or <code>propertyValue</code>. The <code>metadata</code>
     * is set to the MetaDataCollection which is associated with the <code>container</code>
     * in the Piece of the <code>container</code>'s Context, and the 
     * <code>metaDataEditor</code> is created for the <code>metadata</code>.
     * Finally, the GUI is created.
     *  
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object, de.uos.fmt.musitech.framework.editor.EditingProfile, de.uos.fmt.musitech.framework.editor.Display)
     */
    public void init(Object editObj, EditingProfile profile, Display root){
        this.editObj = editObj;
        this.profile = profile;
        if (profile!=null && profile.getPropertyName()!=null){
            this.propertyName = profile.getPropertyName();
            setPropertyValue();
        }
        this.rootDisplay = root;
        determineContainer();
        determineMetadata();
        createMetaDataEditor();
        //for the metadata the metaDataEditor is registered at the DataChangeManager
        createGUI();
    }
    
    /**
     * Sets the <code>container</code> to the <code>editObj</code> or
     * <code>propertyValue</code> of this ContextEditor.
     */
    private void determineContainer(){
        if (this.editObj instanceof Container){
            container = (Container) this.editObj;
        } else {
            if (propertyValue!=null && propertyValue instanceof Container){
                container = (Container) propertyValue;
            }
        } 
    }
    
    /**
     * Sets the <code>metadata</code> to the MetaDataCollection which is connected
     * with the <code>container</code> in the <code>metaDataMap</code> of the Piece
     * of the <code>container's</code> Context.
     *
     */
    private void determineMetadata(){
       if (container!=null){
           if (container.getContext()!=null && container.getContext().getPiece()!=null){
               Piece piece = container.getContext().getPiece();
               metadata = piece.getMetaData(container);
           }
       } else {
           if (editObj instanceof Piece){
               metadata = ((Piece)editObj).getMetaData((Piece)editObj);
           } else if (propertyValue!=null && propertyValue instanceof Piece){
               metadata = ((Piece)propertyValue).getMetaData((Piece)propertyValue);
           }
       }
    }
    
    /**
     * Creates a MetaDataEditor for the <code>metadata</code> and records it in the
     * <code>metaDataEditor</code> of this ContextEditor.
     *
     */
    private void createMetaDataEditor(){
        if (metadata!=null){
            Editor editor = null;
            try {
                editor = EditorFactory.createEditor(metadata);
                if (editor instanceof MetaDataEditor){
                    metaDataEditor = (MetaDataEditor)editor;                
                }
            } catch (EditorConstructionException e) {
                e.printStackTrace();
            }
        }
    }

    /** 
     * Updates the <code>metaDataEditor</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#updateDisplay()
     */
    public void updateDisplay() {
        metaDataEditor.updateDisplay();
    }

    /** 
     * Adds the <code>metaDataEditor</code> to this ContextEditor.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    protected void createGUI() {
        setLayout(new BorderLayout());
        if (metaDataEditor!=null){
            add(metaDataEditor);
        }
    }

    /** 
     * Returns true of this ContextEditor or its <code>metaDataEditor</code> is
     * the focus owner.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    public boolean isFocused() {
        return isFocusOwner() || metaDataEditor.isFocusOwner();
    }

}
