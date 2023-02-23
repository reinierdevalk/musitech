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
 * Created on 22.10.2004
 *
 */
package de.uos.fmt.musitech.framework.editor.translating;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorRegistry;
import de.uos.fmt.musitech.framework.editor.EditorType;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * For testing the NotesEpecEditor with GUI and user iteraction.
 * 
 * @author Kerstin Neubarth
 *
 */
public class NotesEpecEditorTestGUI {
    
    /**
     * Tests an NotesEpecEditor with a given epec String as <code>inputObj</code>.
     */
    private static void testWithGivenEpec(){
        //create inputObj (epec)
        String epec = "''c4 d e f g";
        //create NotesEpecEditor
        NotesEpecEditor editor = new NotesEpecEditor();
        EditingProfile profile = EditorFactory.getOrCreateProfile(epec);
        editor.init(epec, profile, editor);
        //show editor
        JFrame frame = new JFrame("Test NotesEpecEditor");
        frame.getContentPane().add(editor);
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Tests creating an NotesEpecEditor using the EditorFactory.
     * <br>N.B.:
     * Here, the NotesEpecEditor is registered at the EditorRegistry before creating
     * the editor with the EditorFactory. For automatically deriving the editor class,
     * the path must be changed. (At the moment, only "-Editor"s in the editor-package
     * are found, but not those in the editor.translating-package.)
     * <br>
     * TODO
     * alternatives package in EditorFactory, das durchsucht wird, wenn in default nichts
     * gefunden
     * oder "fester" Eintrag in der EditorRegistry?
     */
    private static void testCreatingByFactory(){
        //create inputObj (epec)
        String epec = "''c4 d e f g";
        //register at EditorRegistry
        EditorType type = new EditorType("NotesEpec", NotesEpecEditor.class.getName(), null);
        EditorRegistry.registerEditortypeForClass("java.lang.String", type);
        //create NotesEpecEditor
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(epec, null, "NotesEpec");
            //show editor
            JFrame frame = new JFrame("Test with Factory");
            frame.getContentPane().add((JComponent)editor);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Tests generating notes with a NotesEpecEditor. The editor initially gets an empty
     * String as its <code>inputObj</code>.
     */
    private static void testGeneratingNotes(){
        //register at EditorRegistry
        EditorType type = new EditorType("NotesEpec", NotesEpecEditor.class.getName(), null);
        EditorRegistry.registerEditortypeForClass("java.lang.String", type);
        //create NotesEpecEditor
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor("", null, "NotesEpec");
            //show editor
            JFrame frame = new JFrame("Test with Factory");
            frame.getContentPane().add((JComponent)editor);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }
    
    private static void closing(){
        JFrame closingFrame = new JFrame("For closing");
        closingFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        closingFrame.setVisible(true);
     }

    public static void main(String[] args) {
        closing();
        testWithGivenEpec();
        testCreatingByFactory();
        testGeneratingNotes();
    }
    
}
