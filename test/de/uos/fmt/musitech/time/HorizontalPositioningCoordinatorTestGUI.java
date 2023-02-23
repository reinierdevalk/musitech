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
 * Created on 23.06.2004
 */
package de.uos.fmt.musitech.time;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbolSequence;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.performance.gui.PianoRollContainerDisplay;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.structure.harmony.gui.ChordSymbSeqDisplay;
import de.uos.fmt.musitech.structure.text.LyricsDisplay;
import de.uos.fmt.musitech.time.gui.HorizontalPositioningCoordinator;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class for testing the HorizontalPositiongCoordinator in a GUI.
 * 
 * @author Kerstin Neubarth
 */
public class HorizontalPositioningCoordinatorTestGUI {

	/**
	 * Tests positioning a LyricsDisplay and a ChordSymbSeqDisplay
	 * when the LyricSyllables and ChordSymbols to be displayed have
	 * physical times. 
	 */
	public static void testWithLyricsAndChordDisplays() {
		//create LyricsSyllableSequence
		LyricsSyllableSequence seq = new LyricsSyllableSequence();
		//		seq.add(new LyricsSyllable(10, "Syl"));
		seq.add(new LyricsSyllable(0, "Syl"));
		seq.add(new LyricsSyllable(50, "la"));
		seq.add(new LyricsSyllable(70, "blelllllll"));
		//create LyricsDisplay
		LyricsDisplay lyrDisplay = null;
		try {
			lyrDisplay = (LyricsDisplay) EditorFactory.createDisplay(seq, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//create ChordSymbolSequence
		ChordSymbolSequence chords = new ChordSymbolSequence();
		//		chords.add(new ChordSymbol(null, 10));
		chords.add(new ChordSymbol(null, 0));
		chords.add(new ChordSymbol(null, 25));
		chords.add(new ChordSymbol(null, 50));
		chords.add(new ChordSymbol(null, 70));
		//create ChordSymbSeqDisplay
		ChordSymbSeqDisplay chordsDisplay = null;
		try {
			chordsDisplay =
				(ChordSymbSeqDisplay) EditorFactory.createDisplay(chords, null);
		} catch (EditorConstructionException e2) {
			e2.printStackTrace();
		}
		//create HorizontalPositioningCoordinator and register displays
		HorizontalPositioningCoordinator coord =
			new HorizontalPositioningCoordinator();
//		coord.registerDisplay(chordsDisplay);
		coord.registerDisplay(lyrDisplay);
		//		coord.setMinScale(1.0);
		coord.doPositioning();
		//show displays
		Box box = Box.createVerticalBox();
		box.add(lyrDisplay);
		box.add(chordsDisplay);
		//coord.doPositioning();
		JFrame frame = new JFrame("Test Coordinator with physical times");
		frame.getContentPane().add(box);
		frame.pack();
		frame.setVisible(true);
		//print positions for testing
		lyrDisplay.printSyllablePositions();
		System.out.println();
		chordsDisplay.printSymbolPositions();
	}
	
	/**
     * Tests the HorizontalPositioningCoordinator with HorizontalTimedDisplays
     * whose objects to display have only metrical, but no physical times.
     */
    public static void testWithMetricalTimes() {
        //create a LyricsSyllableSequence
        LyricsSyllableSequence lyrics = new LyricsSyllableSequence();
        LyricsSyllable s0 = new LyricsSyllable();
        s0.setText("Syl " //+
//        		"" +
//        		"  " +
//        		""
				)
				
				;
        s0.setMetricTime(new Rational(0, 4));
        lyrics.add(s0);
        LyricsSyllable s1 = new LyricsSyllable();
        s1.setText("lakjhkjhkjhkjh ");
        s1.setMetricTime(new Rational(1, 4));
        lyrics.add(s1);
        LyricsSyllable s2 = new LyricsSyllable();
        s2.setText("ble");
        s2.setMetricTime(new Rational(2, 4));
        lyrics.add(s2);
        //create a ChordSymbolSequence
        ChordSymbolSequence chords = new ChordSymbolSequence();
        chords.add(new ChordSymbol(new Rational(0, 4), Timed.INVALID_TIME));
        chords.add(new ChordSymbol(new Rational(1, 4), Timed.INVALID_TIME));
        chords.add(new ChordSymbol(new Rational(2, 4), Timed.INVALID_TIME));
        //create a NoteList
        NoteList noteList = new NoteList(new Context(new Piece()));
		noteList.add(
				new ScorePitch('g', 0, 0),
				new Rational(0, 4),
				new Rational(1, 4));
		noteList.add(
				new ScorePitch('a', 0, 0),
				new Rational(1, 4),
				new Rational(1, 4));
		noteList.add(
				new ScorePitch('b', 0, 0),
				new Rational(2, 4),
				new Rational(1, 8));
        
		
		//create displays and register them at HorizontalPositioningCoordinator
        		
		HorizontalPositioningCoordinator coord = new HorizontalPositioningCoordinator();
        PianoRollContainerDisplay pianoRoll = null;
		LyricsDisplay lyrDisplay = null;
        ChordSymbSeqDisplay chordsDisplay = null;
        NotationDisplay notesDisplay = null;      
        try {
            lyrDisplay = (LyricsDisplay) EditorFactory.createDisplay(lyrics,
                    null);
            coord.registerDisplay(lyrDisplay);
            chordsDisplay = (ChordSymbSeqDisplay) EditorFactory.createDisplay(
                    chords, null);
            coord.registerDisplay(chordsDisplay);
            notesDisplay = (NotationDisplay)EditorFactory.createDisplay(noteList, null);
            notesDisplay.setAutoZoom(false);
            coord.registerDisplay(notesDisplay);
            pianoRoll = new PianoRollContainerDisplay(noteList);
            coord.registerDisplay(pianoRoll);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //do positioning
        coord.doPositioning();
        Component[] syllables = lyrDisplay.getComponents();
        Component[] chordSymbols = chordsDisplay.getComponents();
        //show displays
        Box box = Box.createVerticalBox();
		box.add(lyrDisplay);
		box.add(chordsDisplay);
		box.add(notesDisplay);
		box.add(pianoRoll);
		JFrame frame = new JFrame("Test Coordinator with metrical times");
		frame.getContentPane().add(box);
		frame.pack();
		frame.setVisible(true);
		//print positions for testing
		lyrDisplay.printSyllablePositions();
		System.out.println();
		chordsDisplay.printSymbolPositions();
    }
    
    private static void forClosing(){
        JFrame frame = new JFrame("For closing");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

	public static void main(String[] args) {
	    forClosing();
		//testWithLyricsAndChordDisplays();
		testWithMetricalTimes();
	}
}
