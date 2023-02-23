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
 * Created on 08.01.2005
 */
package de.uos.fmt.musitech.framework.time;

import javax.sound.midi.Track;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.performance.rendering.MidiNoteRendering;
import de.uos.fmt.musitech.data.performance.rendering.MidiRendering;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.mpeg.testcases.BaroqueAlignmentTest;
import de.uos.fmt.musitech.performance.ScoreToPerfomance;
import de.uos.fmt.musitech.performance.midi.MidiWriter;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Jan
 *
 */
public class ObjectToMidiTest extends TestCase{
    
    Container container;
    ObjectToMidiFileProcessor OTMFileProc;
    NotationSystem testSystem;
    Piece testPiece;
    /** 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
	protected void setUp() throws Exception {
        container = new BasicContainer();
        testSystem = new NotationSystem();
        testPiece = new Piece();
        testPiece.setScore(testSystem);
        testPiece.getContainerPool().add(testSystem);
        testSystem.setContext(testPiece.getContext());
        
        NotationStaff staff = new NotationStaff(testSystem);
        NotationVoice voice = new NotationVoice(staff);
        NotationChord chordTrill = new NotationChord(testPiece.getContext());
        NotationChord chord = new NotationChord(testPiece.getContext());
        
        
        ScoreNote scoreNoteTrill = new ScoreNote(new Rational(0,4), new Rational(3,4), 'c', (byte) 0, (byte) 0);
        ScoreNote scoreNote = new ScoreNote(new Rational(3,4), new Rational(4,4), 'c', (byte) 0, (byte) 0);
//        scoreNoteTrill.addAccent(Accent.MORDENT_DOWN);
        
//        scoreNote.addAccent(Accent.PORTATO);
        MidiRendering midiRen = new MidiRendering();
        MidiNoteRendering noteRenStart = new MidiNoteRendering();
        
        
        noteRenStart.getRenderingHints().registerHint("velocityAdd", "10");
        noteRenStart.getRenderingHints().registerHint("pitchAdd", "0");
        noteRenStart.getRenderingHints().registerHint("durationMult", "0.1");
        
        MidiNoteRendering noteRenRep1 = new MidiNoteRendering();
        MidiNoteRendering noteRenRep2 = new MidiNoteRendering();
        
        noteRenRep1.getRenderingHints().registerHint("pitchAdd", "1");
//        noteRenRep1.getRenderingHints().registerHint("physicalTimeAddAbs", "10000");
//        noteRenRep1.getRenderingHints().registerHint("rest", "true");
        noteRenRep1.getRenderingHints().registerHint("durationMult", "0.12");
        noteRenRep1.getRenderingHints().registerHint("velocityAdd", "-10");
        noteRenRep2.getRenderingHints().registerHint("pitchAdd", "-1");
        noteRenRep2.getRenderingHints().registerHint("durationMult", "0.1");
        noteRenRep2.getRenderingHints().registerHint("velocityAdd", "-20");
        noteRenRep2.getRenderingHints().registerHint("metricTimeAddRel", "0.1");
        
        midiRen.getStart().add(noteRenStart);
//        midiRen.getStart().add(noteRenRep1);
//        midiRen.getRepeat().add(noteRenRep1);
//        midiRen.getRepeat().add(noteRenRep2);
        midiRen.getEnd().add(noteRenStart);
//        scoreNote.setMidiRendering(midiRen);
        scoreNote.setDetune(98);
        scoreNote.setTransposition(10);
        Note noteTrill = new Note(scoreNoteTrill, new PerformanceNote());
        Note note = new Note(scoreNote, new PerformanceNote());
        RenderingHints ren = new RenderingHints();
        ren.registerHint("tremolo", "3");
        note.setRenderingHints(ren);
        chordTrill.add(noteTrill);
        chord.add(note);
        voice.add(chordTrill);
        voice.add(chord);
        testSystem.add(staff);
    }
    
    /**
     * 
     */
    public void testPerformanceNote() {
        for (int i = 0; i < 100; i++) {
            PerformanceNote note = new PerformanceNote();
            note.setTime(i*1000);
            note.setDuration(500);
            note.setPitch((i%48)+24);
            note.setVelocity(80);
            container.add(note);
        }
        OTMFileProc = new ObjectToMidiFileProcessor(container);
        assertTrue(OTMFileProc.getMidiSequence().getTracks().length == 16);
        printTrackContent();
        OTMFileProc.makeFile();
    }
    
//    public void testMidiWriter() {
//        Piece piece = new Piece();
//        MetricalTimeLine timeLine = new MetricalTimeLine();
//        timeLine.setTempo(new Rational(1,1), 120, 4);
//        timeLine.setTempo(new Rational(2,1), 240, 8);
////        piece.setMetricalTimeLine(timeLine);
//        char[] notes = {'c','d','e','f','g','a','h'};
//        byte oct = 1;
//        byte acc = 0;
//        for (int i = 0; i < 24; i++) {
//            ScoreNote note = new ScoreNote(new Rational(i, 4), new Rational(1, 4), notes[i%7], oct, acc);
//            piece.getNotePool().add(note);
//        }
//        MidiWriter writer = new MidiWriter("E:\\testWriter.mid");
//        writer.setPiece(piece);
//        writer.write();
//    }

    public void testScoreToMidi(){
        Piece piece = new Piece();
        BaroqueAlignmentTest.fillPiece(piece);
        NotationDisplay display = null; 
        try {
            display = (NotationDisplay) EditorFactory.createDisplay(piece);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
       
        NotationSystem notSystem = display.getScorePanel().getNotationSystem();
//        NotationSystem notSystem = test.createNotationSystem();
        piece.setScore(notSystem);
        piece.getContainerPool().add(notSystem);
//        ScoreToPerfomance ScoreConverter = new ScoreToPerfomance(notSystem);
        ScoreToPerfomance ScoreConverter = new ScoreToPerfomance(testSystem);
        MidiWriter writer = new MidiWriter("testWriter.mid");
        writer.setPiece(testPiece);
        writer.write();
        
        
    }
    
    /**
     * 
     */
    private void printTrackContent() {
        Track[] tracks = OTMFileProc.getMidiSequence().getTracks();
        for (int i = 0; i < tracks.length; i++) {
            System.out.println("Track " + i + " size: " + tracks[i].size());
        }
    }
    
}
