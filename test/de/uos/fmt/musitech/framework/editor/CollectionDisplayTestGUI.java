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
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbolSequence;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * For testing the CollectionDisplay.
 * To test the GUI this class has a main-method.
 * 
 * @author Kerstin Neubarth
 *
 */
public class CollectionDisplayTestGUI {
    
    /**
     * Creates a JFrame with default close operation "exit".
     *
     */
    private static void provideClosingFrame(){
        JFrame frame = new JFrame("For closing all running frames");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Sets up a CollectionDisplay for an ObjForCollectionDisplay.
     */
    public static void testGUI(){
        //create editObj
        ObjForCollectionDisplay editObj = new ObjForCollectionDisplay();
        //create CollectionDisplay
        Display display = null;
        try {
            display = EditorFactory.createDisplay(editObj, null, "Collection", null);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //show CollectionDisplay
        if (display!=null){
            JFrame frame = new JFrame("Test GUI");
            frame.getContentPane().add((JComponent)display);
            frame.pack();
            frame.setVisible(true);
        }
    } 
    
    /**
     * Inner class providing an Object with properties and elements for
     * which displays will be used.
     * 
     *  @author Kerstin Neubarth
     *
     */
    public static class ObjForCollectionDisplay extends ArrayList {
        //public properties for ReflectionAccess
        public String name = "Name";
//        public NoteList notes = null;
        //Constructor
        public ObjForCollectionDisplay(){
//            notes = createNotes();
            add(createSyllable());
            add(createSyllable());
            add(createLyrics());
            add(new ChordSymbol());
            add(createChords());
        }
        //methods for creating notes value and elements
        private NoteList createNotes(){
            NoteList noteList = new NoteList();
            noteList.add(new ScorePitch(60), new Rational(0,4), new Rational(1,4));
            return noteList;
        }
        private LyricsSyllable createSyllable(){
            LyricsSyllable syl = new LyricsSyllable();
            syl.setText("bla");
            return syl;
        }
        private ChordSymbolSequence createChords(){
            ChordSymbolSequence seq = new ChordSymbolSequence();
            seq.add(new ChordSymbol());
            seq.add(new ChordSymbol());
            return seq;
        }
        private LyricsSyllableSequence createLyrics(){
            LyricsSyllableSequence seq = new LyricsSyllableSequence();
            seq.add(createSyllable());
            seq.add(createSyllable());
            seq.add(createSyllable());
            return seq;
        }
    }

    public static void main(String[] args) {
        provideClosingFrame();
        testGUI();
    }
}