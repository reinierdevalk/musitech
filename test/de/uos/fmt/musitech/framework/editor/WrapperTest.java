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
 * Created on 16.09.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * JUnit tests for Wrappers. Tests creating Wrappers with the EditorFactory.
 * 
 * @author Kerstin Neubarth
 *
 */
public class WrapperTest extends TestCase {
    
    /**
     * Tests method <code>createWrappingDisplay(String, Display)</code> of the 
     * EditorFactory.
     * The method is tested with the String arguments "PopUp", "Icon" and "Expand".
     *
     */
    public void testCreateWrapperDisplays(){
        //create display to wrap
        Object objToWrap = new LyricsSyllable();
        ((LyricsSyllable)objToWrap).setText("la");
        Display displayToWrap = null;
        try {
            displayToWrap = EditorFactory.createDisplay(objToWrap);
            assertTrue(displayToWrap instanceof Display);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
            fail();
        }
        //create "PopUp" wrapper
        Display wrapper1 = null;
        try {
            wrapper1 = EditorFactory.createWrappingDisplay("PopUp", displayToWrap);
            assertTrue(wrapper1 instanceof Wrapper);
            assertTrue(wrapper1 instanceof PopUpDisplay);
            assertTrue(wrapper1.getEditObj() == displayToWrap.getEditObj());
            assertTrue(((PopUpDisplay)wrapper1).getDisplayToPopUp() == displayToWrap);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
            fail();
        }
        //create "Icon" wrapper
        Display wrapper2 = null;
        try {
            wrapper2 = EditorFactory.createWrappingDisplay("Icon", displayToWrap);
            assertTrue(wrapper2 instanceof Wrapper);
            assertTrue(wrapper2 instanceof IconDisplay);
            assertTrue(wrapper2.getEditObj() == displayToWrap.getEditObj());
            assertTrue(((IconDisplay)wrapper2).getDisplayToPopUp() == displayToWrap);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
            fail();
        }
        //create "Expand" wrapper
        Display wrapper3 = null;
        try {
            wrapper3 = EditorFactory.createWrappingDisplay("Expand", displayToWrap);
            assertTrue(wrapper3 instanceof Wrapper);
            assertTrue(wrapper3 instanceof ExpandDisplay);
            assertTrue(wrapper3.getEditObj() == displayToWrap.getEditObj());
            assertTrue(((ExpandDisplay)wrapper3).getDisplayToExpand() == displayToWrap);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    public void testCreateWrapperEditors(){
        //create editor to wrap
        Object editObj = new ScoreNote();
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(editObj);
            assertTrue(editor instanceof Editor);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //create "PopUp" wrapper
        Display wrapper1 = null;
        try {
            wrapper1 = EditorFactory.createWrappingDisplay("PopUp", editor);
            assertTrue(wrapper1 instanceof Wrapper);
            assertTrue(wrapper1 instanceof PopUpEditor);
            assertTrue(((PopUpEditor)wrapper1).getEditorToPopUp() == editor);
            assertTrue(wrapper1.getEditObj() == editObj);
            assertTrue(wrapper1.getEditObj() == editor.getEditObj());
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //create "Icon" wrapper
        Display wrapper2 = null;
        try {
            wrapper2 = EditorFactory.createWrappingDisplay("Icon", editor);
            assertTrue(wrapper2 instanceof Wrapper);
            assertTrue(wrapper2 instanceof IconEditor);
            assertTrue(((IconEditor)wrapper2).getEditorToPopUp() == editor);
            assertTrue(wrapper2.getEditObj() == editObj);
            assertTrue(wrapper2.getEditObj() == editor.getEditObj());
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //create "Exapnd" wrapper
        Display wrapper3 = null;
        try {
            wrapper3 = EditorFactory.createWrappingDisplay("Expand", editor);
            assertTrue(wrapper3 instanceof Wrapper);
            assertTrue(wrapper3 instanceof ExpandEditor);
            assertTrue(((ExpandEditor)wrapper3).getEditorToExpand() == editor);
            assertTrue(wrapper3.getEditObj() == editObj);
            assertTrue(wrapper3.getEditObj() == editor.getEditObj());
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

}
