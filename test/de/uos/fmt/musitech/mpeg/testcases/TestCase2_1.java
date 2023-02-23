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
 * Created on Dec 31, 2004
 *
 */
package de.uos.fmt.musitech.mpeg.testcases;

import java.net.URL;

import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.CustomSVGGraphic;
import de.uos.fmt.musitech.data.score.DualMetricAttachable;
import de.uos.fmt.musitech.data.score.Linebreak;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.StringSymbol;
import de.uos.fmt.musitech.data.score.TablatureNote;
import de.uos.fmt.musitech.data.score.TupletContainer;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *
 */
public class TestCase2_1 {
	
	private static TablatureNote addTablature(NotationChord chord, int instrStr, int fret, Rational pullUp, Note pullUpTarget) {
		TablatureNote tn = addTablature(chord.get(0), instrStr, fret, pullUp, pullUpTarget);
		chord.addRenderingHint("tabulatur note", tn);
		return tn;
	}
	
	private static TablatureNote addTablature(Note note, int instrStr, int fret, Rational pullUp, Note pullUpTarget) {
		TablatureNote tn = new TablatureNote();
		tn.setInstrString(instrStr);
		tn.setFret(fret);
		tn.setPullUp(pullUp);
		tn.setPullUpTarget(pullUpTarget);
		note.addRenderingHint("tabulatur note", tn);
		
		return tn;
	}
	
	private static NotationChord newChord(ScorePitch pitch, Rational onset, Rational duration) {
		NotationChord nc = new NotationChord();
		nc.add(new Note(new ScoreNote(pitch, onset, duration), null));
		return nc;
	}
	
	public static void fillNotationSystem(Piece piece, NotationSystem system) {
		system.addRenderingHint("scale", "0.7");
		
		NotationStaff staff = system.get(0);
		NotationVoice voice = staff.get(0);

		NotationChord c1 = newChord(new ScorePitch('a', 1, 0), new Rational(46, 16), new Rational(1, 4));
		NotationChord c2 = newChord(new ScorePitch('a', 1, 0), new Rational(50, 16), new Rational(1, 4));
		NotationChord c3 = newChord(new ScorePitch('a', 1, 0), new Rational(54, 16), new Rational(1, 8));
		c1.get(0).getScoreNote().setTiedNote(c2.get(0).getScoreNote());
		c2.get(0).getScoreNote().setTiedNote(c3.get(0).getScoreNote());
		
		voice.add(c1);
		voice.add(c2);
		voice.add(c3);
		
		addTablature(c1, 3, 14, null, null);
		addTablature(c3, 3, 14, null, null).setParanthised(true);
		
		NotationChord c4 = newChord(new ScorePitch('f', 2, 0).sharp(), new Rational(90, 24), new Rational(1, 16));
		voice.add(c4);
		NotationChord c5 = newChord(new ScorePitch('a', 2, 0), new Rational(91, 24), new Rational(1, 16)); 
		voice.add(c5);
		NotationChord c6 = newChord(new ScorePitch('c', 3, 0).sharp(), new Rational(92, 24), new Rational(1, 16));
		voice.add(c6);
		NotationChord c7 = newChord(new ScorePitch('c', 3, 0).sharp(), new Rational(93, 24), new Rational(1, 8));
		voice.add(c7);
		
		addTablature(c4, 2, 19, null, null);
		addTablature(c5, 1, 17, null, null);
		
		TupletContainer tc1 = new TupletContainer();
		tc1.setArity((byte)3);
		tc1.setMetricDuration(new Rational(1, 8));
		tc1.add(c4.get(0)); 
		tc1.add(c5.get(0)); 
		tc1.add(c6.get(0));
		voice.addTuplet(tc1);
		
		c6.get(0).getScoreNote().setTiedNote(c7.get(0).getScoreNote());
		
		NotationChord c8 = newChord(new ScorePitch('c', 3, 0).sharp(), new Rational(96, 24), new Rational(1, 4));
		voice.add(c8);
		
		c7.get(0).getScoreNote().setTiedNote(c8.get(0).getScoreNote());
		
		NotationChord c9 = newChord(new ScorePitch('c', 3, 0).sharp(), new Rational(102, 24), new Rational(1, 16));
		
		voice.add(c9);
		c8.get(0).getScoreNote().setTiedNote(c9.get(0).getScoreNote());

		TablatureNote tn2 = addTablature(c6, 1, 19, new Rational(1, 1), new Note(new ScoreNote(new ScorePitch('b', 2, 0), new Rational(1, 4)), null));
		tn2.setPullDownTarget(c9.get(0));
		tn2.setLongPullDown(true);
		
		NotationChord c10 = newChord(new ScorePitch('d', 3, 0), new Rational(69, 16), new Rational(1, 16));
		voice.add(c10);

		addTablature(c10, 1, 20, new Rational(1, 1), new Note(new ScoreNote(new ScorePitch('c', 3, 0), new Rational(1, 4)), null));
		
		NotationChord c11 = newChord(new ScorePitch('c', 3, 0).sharp(), new Rational(70, 16), new Rational(1, 4));
		voice.add(c11);
		
		NotationChord c12 = newChord(new ScorePitch('c', 3, 0).sharp(), new Rational(74, 16), new Rational(1, 16));
		voice.add(c12);
		NotationChord c13 = newChord(new ScorePitch('b', 2, 0), new Rational(14, 3), new Rational(1, 16));
		voice.add(c13);
		addTablature(c13, 1, 19, null, null).setParanthised(true);

		TablatureNote tn = addTablature(c11, 1, 19, new Rational(1, 1), new Note(new ScoreNote(new ScorePitch('b', 2, 0), new Rational(1, 4)), null));
		tn.setPullDownTarget(c13.get(0));
		tn.setLongPullDown(true);
		tn.setPullDownShift(1);
		
		NotationChord c14 = newChord(new ScorePitch('a', 2, 0), new Rational(113, 24), new Rational(1, 16));
		voice.add(c14);
		addTablature(c14, 1, 17, null, null);
		
		c11.get(0).getScoreNote().setTiedNote(c12.get(0).getScoreNote());

		TupletContainer tc2 = new TupletContainer();
		tc2.setArity((byte)3);
		tc2.setMetricDuration(new Rational(1, 8));
		tc2.add(c12.get(0)); 
		tc2.add(c13.get(0)); 
		tc2.add(c14.get(0));
		voice.addTuplet(tc2);

		NotationChord c15 = newChord(new ScorePitch('b', 2, 0), new Rational(114, 24), new Rational(3, 8));
		voice.add(c15);
		addTablature(c15, 1, 19, null, null);
		NotationChord c16 = newChord(new ScorePitch('b', 0, 0), new Rational(41, 8), new Rational(1, 8));
		voice.add(c16);
		addTablature(c16, 5, 14, null, null);
		NotationChord c17 = newChord(new ScorePitch('e', 1, 0), new Rational(42, 8), new Rational(1, 16));
		voice.add(c17);
		addTablature(c17, 4, 14, null, null);
		NotationChord c18 = newChord(new ScorePitch('b', 1, 0), new Rational(85, 16), new Rational(1, 16));
		voice.add(c18);
		addTablature(c18, 3, 16, null, null);
		NotationChord c19 = newChord(new ScorePitch('e', 1, 0), new Rational(86, 16), new Rational(1, 16));
		voice.add(c19);
		addTablature(c19, 4, 14, null, null);
		NotationChord c20 = newChord(new ScorePitch('e', 2, 0), new Rational(130, 24), new Rational(1, 16));
		voice.add(c20);
		addTablature(c20, 2, 17, null, null);
		NotationChord c21 = newChord(new ScorePitch('a', 1, 0), new Rational(131, 24), new Rational(1, 16));
		voice.add(c21);
		addTablature(c21, 3, 14, null, null);

		
		ChordSymbol chordSymbol3 = new ChordSymbol(Rational.ZERO, 'E', 0);
		chordSymbol3.setMode(Mode.MODE_SUS);
//		piece.getHarmonyTrack().add(chordSymbol3);

		MetricAttachable chordAttach3 = new MetricAttachable(c8.get(0), chordSymbol3);
		chordAttach3.setAlignment(MetricAttachable.NORTH);
		chordAttach3.setDistance(1);
		staff.addAttachable(chordAttach3);
		
		TupletContainer tc3 = new TupletContainer();
		tc3.setArity((byte)3);
		tc3.setMetricDuration(new Rational(1, 8));
		tc3.add(c19.get(0)); 
		tc3.add(c20.get(0)); 
		tc3.add(c21.get(0));
		voice.addTuplet(tc3);
		
		BeamContainer bc1 = new BeamContainer();
		bc1.add(c16.get(0));
		bc1.add(c17.get(0));
		bc1.add(c18.get(0));
		voice.addBeamContainer(bc1);

		BeamContainer bc2 = new BeamContainer();
		bc2.add(c9.get(0));
		bc2.add(c10.get(0));
		voice.addBeamContainer(bc2);
		
		SlurContainer sc1 = new SlurContainer();
		sc1.add(c12.get(0));
		sc1.add(c13.get(0));
		voice.addSlurContainer(sc1);
		
		SlurContainer sc2 = new SlurContainer();
		sc2.add(c13.get(0));
		sc2.add(c14.get(0));
		voice.addSlurContainer(sc2);

		URL zigzagUrl = TestCase2_1.class.getResource("zigzag.svg");
		CustomSVGGraphic tremolo = new CustomSVGGraphic(zigzagUrl.toString(), 1, 1);
		tremolo.setDocumentHeight(10);
		
		DualMetricAttachable dma1 = new DualMetricAttachable(c1.get(0), c3.get(0), tremolo);
		dma1.setRelativePosition(MetricAttachable.NORTH);
		dma1.setDistance(1);
		dma1.setRightAnkerAlignment(DualMetricAttachable.LEFT);
		staff.addAttachable(dma1);

		DualMetricAttachable dma2 = new DualMetricAttachable(c15.get(0), c16.get(0), tremolo);
		dma2.setRelativePosition(MetricAttachable.NORTH);
		dma2.setDistance(1);
		dma2.setRightAnkerAlignment(DualMetricAttachable.LEFT);
		staff.addAttachable(dma2);
		
		StringSymbol text5Symbol = new StringSymbol("hold bend");
		MetricAttachable text5 = new MetricAttachable(new Rational(10, 8), text5Symbol);
		text5.setRelativePosition(MetricAttachable.SOUTH);
		text5.setDistance(2);
		text5.setAlignment(MetricAttachable.CENTER);
		text5.setAnker(c6.get(0));
		staff.addAttachable(text5);
		
		//system.addRenderingHint("barline", "none");
	}
	
	public static void fillPiece(Piece piece) {
		Context context = piece.getContext();
		Container cp = context.getPiece().getContainerPool();
		
		NoteList nl = new NoteList(context);
		
		nl.add(new ScorePitch('e', 2, 0), new Rational(0, 1), new Rational(3,8));
		nl.add(new ScorePitch('d', 2, 0), new Rational(3, 8), new Rational(1, 16));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(7, 16), new Rational(1, 16));
		nl.add(new ScorePitch('b', 1, 0), new Rational(8, 16), new Rational(1, 16));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(9, 16), new Rational(1, 16));
		nl.add(new ScorePitch('r', 2, 0), new Rational(10, 16), new Rational(1, 8));
		
		nl.add(new ScorePitch('d', 2, 0), new Rational(12, 16), new Rational(1, 16));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(13, 16), new Rational(1, 32));
		nl.add(new ScorePitch('b', 1, 0), new Rational(27, 32), new Rational(1, 32));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(28, 32), new Rational(1, 16));
		nl.add(new ScorePitch('d', 2, 0), new Rational(15, 16), new Rational(1, 32));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(31, 32), new Rational(1, 32));
		
		nl.add(new ScorePitch('b', 1, 0), new Rational(32, 32), new Rational(1, 32));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(33, 32), new Rational(1, 32));
		nl.add(new ScorePitch('d', 2, 0), new Rational(34, 32), new Rational(1, 32));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(35, 32), new Rational(1, 32));
		nl.add(new ScorePitch('b', 1, 0), new Rational(36, 32), new Rational(1, 32));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(37, 32), new Rational(1, 32));
		nl.add(new ScorePitch('d', 2, 0), new Rational(38, 32), new Rational(1, 32));
		nl.add(new ScorePitch('b', 1, 0), new Rational(39, 32), new Rational(1, 32));

		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(10, 8), new Rational(1, 16));
		nl.add(new ScorePitch('d', 2, 0), new Rational(21, 16), new Rational(1, 16));
		nl.add(new ScorePitch('e', 2, 0), new Rational(22, 16), new Rational(1, 4));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(26, 16), new Rational(1, 16));
		nl.add(new ScorePitch('d', 2, 0), new Rational(27, 16), new Rational(1, 16));
		nl.add(new ScorePitch('e', 2, 0), new Rational(28, 16), new Rational(3, 16));
		nl.add(new ScorePitch('d', 2, 0), new Rational(31, 16), new Rational(1, 32));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(63, 32), new Rational(1, 32));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(64, 32), new Rational(1, 16));
		nl.add(new ScorePitch('d', 2, 0), new Rational(49, 24), new Rational(1, 16));
		nl.add(new ScorePitch('e', 2, 0), new Rational(50, 24), new Rational(1, 16));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(51, 24), new Rational(1, 4));
		nl.add(new ScorePitch('e', 2, 0), new Rational(19, 8), new Rational(3, 8));

		nl.add(new ScorePitch('a', 1, 0), new Rational(22, 8), new Rational(1, 16));
		nl.add(new ScorePitch('b', 1, 0), new Rational(45, 16), new Rational(1, 16));
		//nl.add(new ScorePitch('a', 1, 0), new Rational(46, 16), new Rational(5, 8));
		
		nl.add(new ScorePitch('d', 1, 0), new Rational(56, 16), new Rational(1, 16));
		nl.add(new ScorePitch('f', 1, 0).sharp(), new Rational(85, 24), new Rational(1, 16));
		nl.add(new ScorePitch('a', 1, 0), new Rational(86, 24), new Rational(1, 16));
		nl.add(new ScorePitch('b', 1, 0), new Rational(87, 24), new Rational(1, 16));
		nl.add(new ScorePitch('c', 2, 0).sharp(), new Rational(88, 24), new Rational(1, 16));
		nl.add(new ScorePitch('d', 2, 0), new Rational(89, 24), new Rational(1, 16));
		
		/*
		nl.add(new ScorePitch('f', 2, 0).sharp(), new Rational(90, 24), new Rational(1, 16));
		nl.add(new ScorePitch('a', 2, 0), new Rational(91, 24), new Rational(1, 16));
		nl.add(new ScorePitch('c', 3, 0).sharp(), new Rational(92, 24), new Rational(1, 16));
		nl.add(new ScorePitch('c', 3, 0).sharp(), new Rational(93, 24), new Rational(1, 8));
		*/
		
		addTablature(nl.get(0), 2, 15, new Rational(1, 1), new Note(new ScoreNote(new ScorePitch('d', 2, 0), new Rational(1, 4)), null));
		addTablature(nl.get(1), 2, 15, null, null);
		addTablature(nl.get(2), 2, 14, null, null);
		addTablature(nl.get(3), 3, 16, null, null);
		addTablature(nl.get(4), 2, 14, null, null);
		
		
		addTablature(nl.get(6), 2, 14, new Rational(1, 2), new Note(new ScoreNote(new ScorePitch('c', 2, 0).sharp(), new Rational(1, 4)), null))
			//.setPullDownTarget((Note)nl.get(7));
			.setPullDownTargetInt(1);

		
		addTablature(nl.get(7), 2, 14, null, null).setParanthised(true);
		addTablature(nl.get(8), 2, 12, null, null);
		addTablature(nl.get(9), 2, 14, null, null);
		addTablature(nl.get(10), 2, 15, null, null);
		addTablature(nl.get(11), 2, 14, null, null);
		
		addTablature(nl.get(12), 2, 12, null, null);
		addTablature(nl.get(13), 2, 14, null, null);
		addTablature(nl.get(14), 2, 15, null, null);
		addTablature(nl.get(15), 2, 14, null, null);
		addTablature(nl.get(16), 2, 12, null, null);
		addTablature(nl.get(17), 2, 14, null, null);
		addTablature(nl.get(18), 2, 15, null, null);
		addTablature(nl.get(19), 2, 12, null, null);
		
		addTablature(nl.get(20), 2, 14, null, null);
		addTablature(nl.get(21), 2, 15, null, null);
		addTablature(nl.get(22), 2, 17, null, null);
		addTablature(nl.get(23), 2, 14, null, null);
		addTablature(nl.get(24), 2, 15, null, null);
		addTablature(nl.get(25), 2, 17, null, null);
		addTablature(nl.get(26), 2, 15, null, null);
		addTablature(nl.get(27), 2, 14, null, null);
		addTablature(nl.get(28), 2, 14, null, null);
		addTablature(nl.get(29), 2, 15, null, null);
		addTablature(nl.get(30), 2, 17, null, null);
		addTablature(nl.get(31), 3, 16, new Rational(1, 1), new Note(new ScoreNote(new ScorePitch('d', 2, 0), new Rational(1, 4)), null));
		addTablature(nl.get(32), 2, 17, null, null);
		
		addTablature(nl.get(33), 3, 14, null, null);
		addTablature(nl.get(34), 3, 16, null, null);
		addTablature(nl.get(35), 5, 17, null, null);
		addTablature(nl.get(36), 4, 16, null, null);
		addTablature(nl.get(37), 4, 19, null, null);
		addTablature(nl.get(38), 3, 16, null, null);
		addTablature(nl.get(39), 3, 18, null, null);
		addTablature(nl.get(40), 3, 19, null, null);
		/*
		addTablature((Note)nl.get(42), 2, 19, null, null);
		addTablature((Note)nl.get(43), 1, 17, null, null);
		addTablature((Note)nl.get(44), 1, 19, new Rational(1, 1), new Note(new ScoreNote(new ScorePitch('b', 2, 0), new Rational(1, 4)), null));
		*/
		
		StaffContainer staff = new StaffContainer(context);
		
		Voice voice = new Voice(context);
		for(Note n: nl)
			voice.add(n);

		nl.get(22).addRenderingHint("tremolo", "3");
		nl.get(25).addRenderingHint("tremolo", "3");
		
		ChordSymbol chordSymbol1 = new ChordSymbol(Rational.ZERO, 'F', 1);
		chordSymbol1.setMode(Mode.MODE_MINOR);
		chordSymbol1.setExtensions("11");
		MetricAttachable chordAttach1 = new MetricAttachable(nl.get(20), chordSymbol1);
		chordAttach1.setAlignment(MetricAttachable.NORTH);
		chordAttach1.setDistance(5);
		staff.add(chordAttach1);
		
		ChordSymbol chordSymbol2 = new ChordSymbol(Rational.ZERO, 'D', 0);
		chordSymbol2.setMode(Mode.MODE_MAJOR);
		chordSymbol2.setExtensions("6");
		MetricAttachable chordAttach2 = new MetricAttachable(nl.get(33), chordSymbol2);
		chordAttach2.setAlignment(MetricAttachable.NORTH);
		chordAttach2.setDistance(5);
		staff.add(chordAttach2);

		
		BeamContainer bc1 = new BeamContainer(context);
		for (int i = 1; i < 5; i++)
			bc1.add(nl.get(i));
		voice.add(bc1); 

		BeamContainer bc2 = new BeamContainer(context);
		for (int i = 6; i < 12; i++)
			bc2.add(nl.get(i));
		voice.add(bc2); 

		BeamContainer bc3 = new BeamContainer(context);
		for (int i = 12; i < 20; i++)
			bc3.add(nl.get(i));
		voice.add(bc3); 

		BeamContainer bc6 = new BeamContainer(context);
		for (int i = 20; i < 22; i++)
			bc6.add(nl.get(i));
		voice.add(bc6);

		BeamContainer bc7 = new BeamContainer(context);
		for (int i = 23; i < 28; i++)
			bc7.add(nl.get(i));
		voice.add(bc7);

		BeamContainer bc8 = new BeamContainer(context);
		for (int i = 33; i < 35; i++)
			bc8.add(nl.get(i));
		voice.add(bc8);
		
		SlurContainer sc1 = new SlurContainer(context);
		sc1.add(nl.get(6));
		sc1.add(nl.get(7));
		voice.add(sc1);

		SlurContainer sc2 = new SlurContainer(context);
		sc2.add(nl.get(7));
		sc2.add(nl.get(8));
		voice.add(sc2);

		SlurContainer sc3 = new SlurContainer(context);
		sc3.add(nl.get(33));
		sc3.add(nl.get(34));
		voice.add(sc3);

		TupletContainer tc1 = new TupletContainer(context, (byte)3);
		for (int i = 28; i < 31; i++)
			tc1.add(nl.get(i));
		tc1.setMetricDuration(new Rational(1, 8));
		voice.add(tc1);

		TupletContainer tc2 = new TupletContainer(context, (byte)3);
		for (int i = 35; i < 38; i++)
			tc2.add(nl.get(i));
		tc2.setMetricDuration(new Rational(1, 8));
		voice.add(tc2);

		TupletContainer tc3 = new TupletContainer(context, (byte)3);
		for (int i = 38; i < 41; i++)
			tc3.add(nl.get(i));
		tc3.setMetricDuration(new Rational(1, 8));
		voice.add(tc3);

		/*
		TupletContainer tc4 = new TupletContainer(context, (byte)3);
		for (int i = 42; i < 45; i++)
			tc4.add(nl.get(i));
		tc4.setMetricDuration(new Rational(1, 8));
		voice.add(tc4);
		*/
		
		staff.add(voice);
		staff.addRenderingHint("note spacing", new Float(2));
		staff.addRenderingHint("draw tabulatur", new Boolean(true));
		
		cp.add(staff);

		NoteList nl3 = new NoteList(context);

		nl3.add(new ScorePitch('r', 2, 0), new Rational(0, 1), new Rational(1,8));
		nl3.add(new ScorePitch('e', 0, 0), new Rational(1, 8), new Rational(1,8));
		nl3.add(new ScorePitch('a', 0, 0), new Rational(2, 8), new Rational(1,8));
		nl3.add(new ScorePitch('b', 0, 0), new Rational(3, 8), new Rational(1,8));
		nl3.add(new ScorePitch('a', 0, 0), new Rational(4, 8), new Rational(1,8));
		nl3.add(new ScorePitch('e', 0, 0), new Rational(5, 8), new Rational(1,8));
		nl3.add(new ScorePitch('r', 0, 0), new Rational(6, 8), new Rational(1,8));
		nl3.add(new ScorePitch('e', 0, 0), new Rational(7, 8), new Rational(1,8));
		nl3.add(new ScorePitch('a', 0, 0), new Rational(8, 8), new Rational(1,8));
		nl3.add(new ScorePitch('r', 0, 0), new Rational(9, 8), new Rational(1,8));

		addTablature(nl3.get(1), 4, 2, null, null);
		addTablature(nl3.get(2), 3, 2, null, null);
		addTablature(nl3.get(3), 2, 0, null, null);
		addTablature(nl3.get(4), 3, 2, null, null);
		addTablature(nl3.get(5), 4, 2, null, null);
		addTablature(nl3.get(7), 4, 2, null, null);
		addTablature(nl3.get(8), 3, 2, null, null);
		
		StaffContainer staff2 = new StaffContainer(context);
		Voice voice2 = new Voice(context);
		for(Note n: nl3)
			voice2.add(n);
		
		BeamContainer bc4 = new BeamContainer(context);
		for (int i = 1; i < 3; i++)
			bc4.add(nl3.get(i));
		voice2.add(bc4);
		
		BeamContainer bc5 = new BeamContainer(context);
		for (int i = 3; i < 6; i++)
			bc5.add(nl3.get(i));
		voice2.add(bc5);

		URL holdUrl = TestCase2_1.class.getResource("dashed.svg");
		CustomSVGGraphic hold = new CustomSVGGraphic(holdUrl.toString(), 1, 1);
		hold.setLeadingText("hold");
		hold.setDocumentHeight(10);

		URL zigzagUrl = TestCase2_1.class.getResource("zigzag.svg");
		CustomSVGGraphic tremolo = new CustomSVGGraphic(zigzagUrl.toString(), 1, 1);
		tremolo.setDocumentHeight(10);
		
		DualMetricAttachable dma = new DualMetricAttachable(voice2.get(1), voice2.get(5), hold);
		dma.setRelativePosition(MetricAttachable.SOUTH);
		dma.setDistance(1);
		dma.addRenderingHint("allow duplication", new Boolean(false));
		staff2.add(dma);
		
		DualMetricAttachable dma2 = new DualMetricAttachable(voice2.get(7), voice2.get(8), hold);
		dma2.setRelativePosition(MetricAttachable.SOUTH);
		dma2.setDistance(1);
		dma2.addRenderingHint("allow duplication", new Boolean(false));
		staff2.add(dma2);

		DualMetricAttachable dma3 = new DualMetricAttachable(voice.get(31), voice.get(32), hold);
		dma3.setRelativePosition(MetricAttachable.SOUTH);
		dma3.setDistance(1);
		dma3.addRenderingHint("allow duplication", new Boolean(false));
		staff.add(dma3);
		
		DualMetricAttachable dma4 = new DualMetricAttachable(voice.get(32), voice.get(33), tremolo);
		dma4.setRelativePosition(MetricAttachable.NORTH);
		dma4.setDistance(1);
		dma4.setRightAnkerAlignment(DualMetricAttachable.LEFT);
		staff.add(dma4);
		
		
		StringSymbol text1Symbol = new StringSymbol("Gtr. 3");
		MetricAttachable text1 = new MetricAttachable(new Rational(10, 8), text1Symbol);
		text1.setRelativePosition(MetricAttachable.NORTH);
		text1.setAlignment(MetricAttachable.LEFT);
		text1.setDistance(1);
		staff.add(text1);

		StringSymbol text2Symbol = new StringSymbol("w/Rhy. Fig. 3 (Gtr. 2)");
		MetricAttachable text2 = new MetricAttachable(new Rational(10, 8), text2Symbol);
		text2.setRelativePosition(MetricAttachable.NORTH);
		text2.setAlignment(MetricAttachable.LEFT);
		text2.setDistance(3);
		staff.add(text2);
		
		StringSymbol text3Symbol = new StringSymbol("trem. pick");
		MetricAttachable text3 = new MetricAttachable(new Rational(10, 8), text3Symbol);
		text3.setRelativePosition(MetricAttachable.SOUTH);
		text3.setDistance(2);
		text3.setAlignment(MetricAttachable.CENTER);
		text3.setAnker(voice.get(22));
		staff.add(text3);
		
		MetricAttachable text4 = new MetricAttachable(new Rational(10, 8), text3Symbol);
		text4.setRelativePosition(MetricAttachable.SOUTH);
		text4.setDistance(2);
		text4.setAlignment(MetricAttachable.CENTER);
		text4.setAnker(voice.get(25));
		staff.add(text4);
		
		
		staff2.add(voice2);
		
		staff2.addRenderingHint("note spacing", new Float(2));
		staff2.addRenderingHint("draw tabulatur", new Boolean(true));
		cp.add(staff2);
		
		Barline barline1 = new Barline(new Rational(10, 8));
		cp.add(barline1);
		barline1.addRenderingHint("time signature preview", new int[]{12, 8});
		cp.add(new Barline(new Rational(22, 8)));
		
		Barline barline3 = new Barline(new Rational(32, 8));
		barline3.addRenderingHint("time signature preview", new int[]{12, 8});		
		cp.add(barline3);

		
		cp.add(new Linebreak(new Rational(10, 8)));
		cp.add(new Linebreak(new Rational(32, 8)));
		
		KeyMarker km = new KeyMarker(Rational.ZERO, 0);
		km.setAccidentalNum(3);
		piece.getHarmonyTrack().add(km);
		
		TimeSignatureMarker tsm = new TimeSignatureMarker(10, 8, Rational.ZERO);
		TimeSignatureMarker tsm2 = new TimeSignatureMarker(12, 8, new Rational(10, 8));
		TimeSignatureMarker tsm3 = new TimeSignatureMarker(10, 8, new Rational(22, 8));
		TimeSignatureMarker tsm4 = new TimeSignatureMarker(12, 8, new Rational(32, 8));
		piece.getMetricalTimeLine().add(tsm);
		piece.getMetricalTimeLine().add(tsm2);
		piece.getMetricalTimeLine().add(tsm3);
		piece.getMetricalTimeLine().add(tsm4);
		
		NotationSystem nsys = NotationDisplay.createNotationSystem(piece);
		piece.setScore(nsys);
		
		fillNotationSystem(piece, nsys);
		
		nsys.prepareForScore();
	
	}
}
