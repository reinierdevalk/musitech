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
 * Created on 22.09.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class for testing the RationalEditor. 
 * 
 * @author Kerstin Neubarth
 *
 */
public class RationalEditorTest {
    
    /**
     * For testing a RationalEditor as outmost editor.
     * If the input in the textfields is changed and "OK" is activated, the editObj
     * should have the changed value.
     * If the input in the textfields is changed and "Cancel" is activated, the
     * editObj should not have changed.
     * For checking the editObj, its values are written to the console when the
     * EditorWindow is closed.
     */
    private void changeRationalObj(){
        //create editObj
        Rational editObj = new Rational(1,3);
        //create Editor
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(editObj);
            addTestOutput(editor);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //show Editor
        if (editor!=null){
            EditorWindow window = new EditorWindow("Rational editObj");
            window.addEditor(editor);
            window.pack();
            window.setVisible(true);
        }
    }
    
    /**
     * For testing a RationalEditor as outmost editor editing a property.
     * If the input in the textfields is changed and "OK" is activated, the 
     * editObj's property should have the changed value.
     * If the input in the textfields is changed and "Cancel" is activated, the
     * editObj should not have changed.
     * For checking the editObj, its values are written to the console when the
     * EditorWindow is closed.
     */
    private void changeRationalProperty(){
        //create editObj
        ScoreNote sn = new ScoreNote();
        sn.setMetricDuration(new Rational(1,4));
        //create EditingProfile
        EditingProfile profile = new EditingProfile("metricDuration");
        //create editor
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(sn, profile);
            addTestOutput(editor);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //show editor
        if (editor!=null){
            EditorWindow window = new EditorWindow("Rational property");
            window.addEditor(editor);
            window.pack();
            window.setVisible(true);
        }
    }
    
    /**
     * For testing a RationalEditor as a child in a complex editor.
     * If the input in the textfields of the RationalEditor is changed and 
     * "OK" is activated, the editObj's Rational property should have the changed 
     * value.
     * If the input in the textfields is changed and "Cancel" is activated, the
     * editObj should not have changed.
     * For checking the editObj, its values are written to the console when the
     * EditorWindow is closed.
     */
    private void changeRationalInObj(){
        //create editObj
        ScoreNote sn = new ScoreNote();
        sn.setMetricDuration(new Rational(1,4));
        //create editor
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(sn);
            addTestOutput(editor);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //show editor
        if (editor!=null){
            EditorWindow window = new EditorWindow("Rational in editObj");
            window.addEditor(editor);
            window.pack();
            window.setVisible(true);
        }    
    }
    
    /**
     * For testing a RationalEditor with a null property. The editor is to show a
     * "Create..." button and to update when a new property is created.
     *
     */
    private void createNewRationalValue(){
        //create editObj
        LyricsSyllable syllable = new LyricsSyllable(100, null);
        //create editor
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(syllable, null, "Panel");
            addTestOutput(editor);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //show editor
        if (editor!=null){
            EditorWindow window = new EditorWindow("Create Rational value");
            window.addEditor(editor);
            window.pack();
            window.setVisible(true);
        }    
    }
    
    /**
     * Adds an AncestorListener to the specified Editor which writes the
     * editObj to the console when the ancestor is removed.
     * 
     * @param editor Editor which is tested
     */
    private void addTestOutput(final Editor editor){
        ((JComponent)editor).addAncestorListener(new AncestorListener(){
            @Override
			public void ancestorAdded(AncestorEvent event) {
            }
            @Override
			public void ancestorMoved(AncestorEvent event) {
            }
            @Override
			public void ancestorRemoved(AncestorEvent event) {
                if (editor instanceof AbstractSimpleEditor){
                    ((AbstractSimpleEditor)editor).applyChangesToPropertyValue();
                } else {
                    if (editor instanceof AbstractComplexEditor){
                        Editor[] children = ((AbstractComplexEditor)editor).getChildren();
                        for (int i = 0; i < children.length; i++) {
                            if (children[i] instanceof RationalEditor){
                                ((RationalEditor)children[i]).applyChangesToPropertyValue();
                            }
                        }
                    }
                }
                System.out.println("RationalEditor:");
                System.out.println("editObj:" + editor.getEditObj().toString());
            }
		    }
		);
    }
    
    /**
     * Provides a window with default close operation "exit".
     * This is used for closing all EditorWindows which are still open
     * and finishing the tests.
     */
    private void closeTestWindows(){
        JFrame frame = new JFrame("Closing frame");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Method for running the test methods.
     * 
     * @param args
     */
    public static void main(String[] args) {
        RationalEditorTest ret = new RationalEditorTest();
        ret.closeTestWindows();
        ret.changeRationalObj();
        ret.changeRationalProperty();
        ret.changeRationalInObj();
        ret.createNewRationalValue();
    }
}
