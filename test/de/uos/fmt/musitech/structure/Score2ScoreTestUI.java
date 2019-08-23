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
 * Created on 19.01.2005
 *
 */
package de.uos.fmt.musitech.structure;

import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.ClefContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.performance.midi.NegativeTimeStampException;
import de.uos.fmt.musitech.utility.math.Rational;


/**
 * @author Kerstin Neubarth
 *
 */
public class Score2ScoreTestUI {
    
    public static void testCopyScore(){
        Piece piece = new Piece();
        NoteList notes1 = new NoteList(piece.getContext(), "2'c 4d 2e 4d 2.c");
        BasicContainer<Note> voice1 = new BasicContainer<Note>(piece.getContext());
        voice1.addAll(notes1);
        piece.getContainerPool().add(voice1);
        NoteList notes2 = new NoteList(piece.getContext(), "2c 4,b 2'c 4c 2.c");
        BasicContainer<Note> voice2 = new BasicContainer<Note>(piece.getContext());
        voice2.addAll(notes2);
        piece.getContainerPool().add(voice2);
        piece.setScore(createNotationSystem(voice1, voice2));
        TimeSignatureMarker tsMarker = new TimeSignatureMarker(3,4,Rational.ZERO);
        piece.getMetricalTimeLine().add(tsMarker);
        //show original score
        JFrame frame1 = new JFrame("Original Score");
        try {
            Display display = EditorFactory.createDisplay(piece.getScore(), null, "Notation");
            frame1.getContentPane().add((JComponent)display);
            frame1.pack();
            frame1.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
       //show filtered score 
       if (voice2.getScore()==null){
           System.out.println("Score2ScoreTestUI: filtered score is null");
           return;
       }
       JFrame frame2 = new JFrame("Filtered Score");
       try {
           NotationSystem filteredScore = voice2.getScore();
           Display display = EditorFactory.createDisplay(filteredScore, null, "Notation");
           frame2.getContentPane().add((JComponent)display);
           frame2.pack();
           frame2.setVisible(true);
       } catch (EditorConstructionException e) {
           e.printStackTrace();
       }
    }
    
    private static NotationSystem createNotationSystem(Container<Note> voice1, Container<Note> voice2){
        NotationSystem system = new NotationSystem(voice1.getContext());
        NotationStaff staff1 = new NotationStaff(system);
        staff1.setClefType('g',-1,0);
//        Clef clefStaff1 = new Clef('g',-1);
//        ClefContainer clefTrack1 = new ClefContainer(staff1.getContext());
//        clefTrack1.add(clefStaff1);
//        staff1.setClefTrack(clefTrack1);
        NotationStaff staff2 = new NotationStaff(system);
        staff2.setClefType('f',1,0);
//        Clef clefStaff2 = new Clef('f',1);
//        ClefContainer clefTrack2 = new ClefContainer(staff2.getContext());
//        clefTrack2.add(clefStaff2);
//        staff2.setClefTrack(clefTrack2);
        NotationVoice nVoice1 = new NotationVoice(staff1);
        nVoice1.addNotes(voice1);
        NotationVoice nVoice2 = new NotationVoice(staff2);
        nVoice2.addNotes(voice2);
        SlurContainer slur = new SlurContainer();
        slur.add(voice2.get(0));
        slur.add(voice2.get(1));
        nVoice2.addSlurContainer(slur);
        return system;
    }
    
    private static void displayEmptyStaff(){
        Context context = new Context();
        NotationSystem system = new NotationSystem(context);
        NotationStaff staff1 = new NotationStaff(system);
        staff1.setClefType('g',0,0);
        NotationStaff staff2 = new NotationStaff(system);
        staff2.setClefType('f',1,0);
        NotationVoice voice1 = new NotationVoice(staff1);
        NotationVoice voice2 = new NotationVoice(staff2);       
        NoteList notes = new NoteList(context, "c d e f g e 2c");
        voice2.addNotes(notes);
        try {
            Display display = EditorFactory.createDisplay(system);
            JFrame frame = new JFrame("Test 1 empty staff");
            frame.getContentPane().add((JComponent)display);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }
    
    public static void testWithGaps(){
        Context context = new Context();
        NotationSystem system = new NotationSystem( context);
        system.addRenderingHint("gaps", "none");
        NotationStaff staff1 = new NotationStaff( system);
        staff1.setClefType('g',0,0);
        NotationStaff staff2 = new NotationStaff( system);
        staff2.setClefType('f',1,0);
        NotationVoice voice1 = new NotationVoice( staff1);
        NotationVoice voice2 = new NotationVoice( staff2);       
        NoteList notes = new NoteList(context, "c d e f g e 2c"); 
//            notes.timeShift(10000);
        notes.metricalTimeShift(new Rational(3,4));
        voice1.addAll((Container)notes.clone());
        voice2.addNotes(notes);
        try {
            Display display = EditorFactory.createDisplay(system);
            JFrame frame = new JFrame("Test with time shift");
            frame.getContentPane().add((JComponent)display);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        
    }
    
    private static void forClosing(){
        JFrame frame = new JFrame("Close");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        forClosing();
//        testCopyScore();
        displayEmptyStaff();
        testWithGaps();
    }
}
