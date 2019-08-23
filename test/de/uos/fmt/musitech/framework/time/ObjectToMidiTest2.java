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
 * Created on 11.01.2005
 *
 * 
 */
package de.uos.fmt.musitech.framework.time;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.DynamicsLevelMarker;
import de.uos.fmt.musitech.data.score.DynamicsMarker;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.performance.ScoreToPerfomance;
import de.uos.fmt.musitech.performance.midi.MidiWriter;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Jan-H. Kramer
 *
 * 
 */
public class ObjectToMidiTest2 {
    Container container;
    ObjectToMidiFileProcessor OTMFileProc;
    NotationSystem testSystem;
    Piece testPiece;
    
    /**
     * 
     */
    public ObjectToMidiTest2() {
        init();
        makeNotes1();
        makeDyn();
        convert();
    }
    
    /**
     * 
     */
    private void makeDyn() {
        DynamicsLevelMarker dynLev1 = new DynamicsLevelMarker();
        DynamicsLevelMarker dynLev2 = new DynamicsLevelMarker();
        dynLev1.setMetricTime(new Rational(0,8));
        dynLev1.setLevel("ffff");
        dynLev2.setMetricTime(new Rational(12,8));
        dynLev2.setLevel("pppp");
        
        DynamicsMarker dynMar = new DynamicsMarker();
        dynMar.setMetricTime(new Rational(0,8));
        dynMar.setMetricDuration(new Rational(4,8));
        
        DynamicsMarker dynMar2 = new DynamicsMarker();
        dynMar2.setMetricTime(new Rational(8,8));
        dynMar2.setMetricDuration(new Rational(4,8));
        dynMar2.setCrescendo(false);
        
//        testPiece.getHarmonyTrack().add(dynLev1);
        testPiece.getHarmonyTrack().add(dynLev2);
//        testPiece.getHarmonyTrack().add(dynMar);
//        testPiece.getHarmonyTrack().add(dynMar2);
    }






    /**
     * 
     */
    private void init() {
        container = new BasicContainer();
        testSystem = new NotationSystem();
        testPiece = new Piece();
        testPiece.setScore(testSystem);
        testPiece.getContainerPool().add(testSystem);
        testSystem.setContext(testPiece.getContext());
        
        
    }
    
    /**
     * 
     */
    private void makeNotes1() {
        NotationStaff staff = new NotationStaff(testSystem);
        NotationVoice voice = new NotationVoice(staff);

        NotationChord[] chord = new NotationChord[50];
        ScoreNote[] scorNotes = new ScoreNote[50];
        Note[] notes = new Note[50];
        container = new BasicContainer();
        for (int i = 0; i < scorNotes.length; i++) {
            scorNotes[i] = new ScoreNote(new Rational(i,8), new Rational(1,8), 'c', (byte) 0, (byte) 0);
            notes[i] = new Note(scorNotes[i], new PerformanceNote());
            chord[i] = new NotationChord(testPiece.getContext());
            chord[i].add(notes[i]);
            voice.add(chord[i]);
            if (i%2 == 0){
                container.add(notes[i]);
            }
        }
        DynamicsMarker dynMar = new DynamicsMarker();
        dynMar.setMetricTime(new Rational(0,8));
        dynMar.setMetricDuration(new Rational(4,8));
        
        DynamicsLevelMarker dynLev = new DynamicsLevelMarker(new Rational(5,8), "ff");
        
        MetricalTimeLine timeLine = new MetricalTimeLine(testPiece.getContext());
        voice.setContextTimeLine(timeLine);
        voice.getContextTimeLine().add(dynMar);
        voice.getContextTimeLine().add(dynLev);
        
       
    }

    
    /**
     * 
     */
    private void convert() {
        ScoreToPerfomance ScoreConverter = new ScoreToPerfomance(testSystem, container);
        MidiWriter writer = new MidiWriter("D:\\Jan\\musitech\\test\\testWriter.mid");
        writer.setPiece(testPiece);
        writer.write();
    }

    public static void main(String[] args) {
        ObjectToMidiTest2 test = new ObjectToMidiTest2();
    }
}
