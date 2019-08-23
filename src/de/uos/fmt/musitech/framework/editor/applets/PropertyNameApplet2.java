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
 * Created on 20.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uos.fmt.musitech.framework.editor.applets;

import java.awt.BorderLayout;

import javax.swing.JApplet;
import javax.swing.JComponent;

import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * JApplet with an Editor showing one single property of an Object.
 * 
 * @author Kerstin Neubarth
 *
 */
public class PropertyNameApplet2 extends JApplet {
    
    /**
     * Starts this applet.
     *  
     * @see java.applet.Applet#init()
     */
    public void init(){
        try {
            super.init();
            createGUI();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }    
    }
    
    
    /**
     * Returns a KeyMarker whose root is to be edited.
     * 
     * @return KeyMarker
     */
    private KeyMarker createEditObj(){
        KeyMarker key = new KeyMarker();
        key.setRoot('g');
        return key;
    }
    
    /**
     * Returns an Editor showing the root of the specified KeyMarker.
     * 
     * @param editObj KeyMarker whose root is to be edited
     * @return Editor editing the root of the specified KeyMarker
     */
    private Editor createForProperty(KeyMarker editObj){
        EditingProfile profile = new EditingProfile();
        profile.setLabel("Root");
        profile.setPropertyName("root");
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(editObj, profile);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return editor;
    }
    
    /**
     * Creates the GUI.
     *
     */
    private void createGUI(){
        KeyMarker editObj = createEditObj();
        Editor editor = createForProperty(editObj);
        if (editor!=null){
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add((JComponent)editor);
        }
    }

}
