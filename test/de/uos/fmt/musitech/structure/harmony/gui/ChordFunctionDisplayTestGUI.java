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
 * Created on 04.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uos.fmt.musitech.structure.harmony.gui;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.structure.harmony.*;
import de.uos.fmt.musitech.data.structure.harmony.ChordFunctionSymbol.FUNCTION;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.editor.EditorRegistry;
import de.uos.fmt.musitech.framework.editor.EditorType;

/**
 * Class for testing the ChordFunctionDisplay.
 * To test the GUI, this class contains a <code>main</code> method.
 * 
 * @author Kerstin Neubarth
 *
 */
public class ChordFunctionDisplayTestGUI {

    private static ChordFunctionSymbol createChordFunctionSymbol1() {
        ChordFunctionSymbol cfs = new ChordFunctionSymbol();
        cfs.setChordFunction(FUNCTION.TONIKA_PARALLELE);
        cfs.setBase(3);
        cfs.setMode(Mode.MODE_MINOR);
        return cfs;
    }
    
    private static ChordFunctionSymbol createChordFunctionSymbol2() {
        ChordFunctionSymbol cfs = new ChordFunctionSymbol();
        cfs.setChordFunction(FUNCTION.DOPPEL_DOMINANTE);
        cfs.setExtensions("7");
        cfs.setBase(3);
        cfs.setMode(Mode.MODE_MAJOR);
        return cfs;
    }
    
    private static ChordFunctionSymbol createChordFunctionSymbol3() {
        ChordFunctionSymbol cfs = new ChordFunctionSymbol();
        cfs.setChordFunction(FUNCTION.DOMINANT_VERK);
        cfs.setBase(3);
        cfs.setExtensions("9");
        cfs.setMode(Mode.MODE_MAJOR);
        return cfs;
    }

    private static Display createChordFunctionDisplay(ChordFunctionSymbol cfs) {
        //register ChordFunctionDisplay at EditorRegistry
        EditorType editorType = new EditorType("ChordFunction",
                ChordFunctionDisplay.class.getName(), null);
        EditorRegistry.registerEditortypeForClass(FUNCTION.class
                .getName(), editorType);
        Display display = null;
        try {
            display = EditorFactory.createDisplay(cfs);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return display;
    }

    private static void showDisplay(Display display, String title) {
        if (display != null) {
            JFrame frame = new JFrame(title);
            frame.getContentPane().add((JComponent) display);
            frame.pack();
            frame.setVisible(true);
        } else {
            System.out
                    .println("In ChordDegreeDisplayTestGUI: Display is null.");
        }
    }

    private static void testChordDegreeDisplay1() {
        ChordFunctionSymbol cfs = createChordFunctionSymbol1();
        Display display = createChordFunctionDisplay(cfs);
        showDisplay(display, "Test: Tp");
    }
    
    private static void testChordDegreeDisplay2() {
        ChordFunctionSymbol cfs = createChordFunctionSymbol2();
        Display display = createChordFunctionDisplay(cfs);
        showDisplay(display, "Test: DD");
    }
    
    private static void testChordDegreeDisplay3() {
        ChordFunctionSymbol cfs = createChordFunctionSymbol3();
        Display display = createChordFunctionDisplay(cfs);
        showDisplay(display, "Test: vD");
    }
    
    private static void provideClosingFrame(){
        JFrame frame = new JFrame("For closing");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    private static void testOverlappingTextfields(){
        JTextField field1 = new JTextField("Field1    ");
//        field1.setHorizontalAlignment(JTextField.LEFT);
        JTextField field2 = new JTextField("   Field2");
        field2.setOpaque(false);
        field1.add(field2);
        JFrame frame = new JFrame("2 TextFields");
        frame.getContentPane().add(field1);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        provideClosingFrame();
        testChordDegreeDisplay1();
        testChordDegreeDisplay2();
        testChordDegreeDisplay3();
    }
    
}
