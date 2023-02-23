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
 * Created on 28.09.2004
 *
 */
package de.uos.fmt.musitech.framework.change.applets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;

import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorWindow;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Applet for illustrating how the DataChangeManager works.
 * Two editors sharing the same <code>editObj</code> are generated and shown.
 * The applet is started when the start button is activated.
 * 
 * @author Kerstin Neubarth
 *
 */
public class SharingEditors extends JApplet {
    
    /**
     * Starts this applet.
     *  
     * @see java.applet.Applet#init()
     */
    @Override
	public void init(){
        try {
            super.init();
            createGUI();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }    
    }
    
    /**
     * Returns an Array containing two Editors sharing the same <code>editObj</code>.
     * 
     * @return Editor[] Array containing two Editors sharing their <code>editObj</code>
     */
    private Editor[] createEditors(){
        //provide shared editObj
        ScoreNote editObj = new ScoreNote();
        //create editors 
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(editObj);
            editor2 = EditorFactory.createEditor(editObj);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //return editors
        return new Editor[]{editor1, editor2};
    }
    
    /**
     * Creates the GUI.
     * A button is shown which offers to start the applet, e.g. opening the editors.
     */
    private void createGUI(){
        JButton startButton = new JButton("Start applet");
        startButton.addActionListener(new ActionListener(){
            @Override
			public void actionPerformed(ActionEvent e) {
                openEditors();
            }            
        });
        getContentPane().add(startButton);
    }
    
    /**
     * Creates two editors sharing the same <code>editObj</code> and opens each of them
     * in its own EditorWindow.
     */
    private void openEditors(){
        Editor[] editors = createEditors();
        if (editors!=null && editors.length>1){
            if (editors[0]!=null && editors[1]!=null){
                openEditor(editors[0], 1);
                openEditor(editors[1], 2);
            }
        }
    }
    
    /**
     * Shows the specified Editor in an EditorWindow. 
     * The int argument is used in the title of the EditorWindow
     * ("Editor 1", "Editor 2" etc.).
     * 
     * @param editor Editor to be shown in an EditorWindow
     * @param i int to appear as the editor's number in the window's title
     */
    private void openEditor(Editor editor, int i){
        EditorWindow window = new EditorWindow("Editor "+i);
        window.addEditor(editor);
        window.pack();
        window.setBounds(i*300, 200, window.getWidth(), window.getHeight());
        window.setVisible(true);
    }

}
