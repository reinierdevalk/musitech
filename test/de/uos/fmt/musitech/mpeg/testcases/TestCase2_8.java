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
import java.util.Locale;

import de.uos.fmt.musitech.data.score.Attachable;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.CustomSVGGraphic;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.SVGSymbol;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsContainer;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *
 */
public class TestCase2_8 {
	
	private static NotationChord newChord(ScorePitch pitch, Rational onset, Rational duration) {
		NotationChord nc = new NotationChord();
		nc.add(new Note(new ScoreNote(pitch, onset, duration), null));
		return nc;
	}
	
	private static void addLyric(NotationVoice voice, NotationChord nc, String syl) {
		LyricsSyllable lsyl = new LyricsSyllable(nc.getMetricTime(), syl);
		voice.getLyrics().getVerse((byte)0).add(lsyl);
	}

	private static void addHead(NotationChord nc, int form) {
		int width = 0;
		int height = 0;
		
		switch (form) {
		case 1:
		case 4:
			width = 1;
			height = 1;
			break;
		case 2:
		case 3:
	    case 12:
	    case 14:
			width = 1;
			height = 2;
			break;
		case 5:
		case 6:
		case 11:
		case 13:
			width = 2;
			height = 2;
			break;
		case 7:
		case 8:
		case 15:
			width = 3;
			height = 2;
			break;
		case 9:
		case 10:
			width = 2;
			height = 3;
			break;
		default:
			throw new IllegalArgumentException("form is unknown");
		}
		
		URL url = TestCase2_8.class.getResource("neum" + form + ".svg");
		CustomSVGGraphic graphic = new CustomSVGGraphic(url.toString(), width, height);
		if (form == 2 || form == 3 || form == 5 || form == 6 || 
		    form == 7 || form == 8 || form == 9 || form == 10 || 
			form == 11 || form == 12 || form == 13 || form == 14 || 
			form == 15) {
			graphic.setVerticalAlignment(-4);
		}
		nc.get(0).addRenderingHint("custom head", graphic);
		nc.addRenderingHint("draw stem", new Boolean(false));
		// added by Jan Kramer
		TestCase2_8MidiRendering.setMidiRendering(nc.get(0).getScoreNote(), form);
	}

	public static void fillPiece(Piece piece) {
		piece.getContainerPool().addRenderingHint("barline", "none");
	}
	
	
	public static void fillNotationSystem(NotationSystem system) {
		NotationStaff staff = system.get(0);
		NotationVoice voice = staff.get(0);
		
		Locale language = Locale.getDefault();
		voice.setLyrics(new LyricsContainer(null));
		voice.getLyrics().setDefaultLanguage(language);
		LyricsSyllableSequence lss = new LyricsSyllableSequence(voice.getContext());
		lss.setLanguage(language);
		voice.getLyrics().add(lss);
		
		system.addRenderingHint("barline", "none");
		
		staff.addRenderingHint("number of lines", new Integer(4));
		
		Clef clef = new Clef('g', -1, Rational.ZERO);
		URL clefUrl = TestCase2_8.class.getResource("neumClef.svg");
		CustomSVGGraphic neumClef = new CustomSVGGraphic(clefUrl.toString(), 1, 1);
		neumClef.setVerticalShift(3);
		clef.addRenderingHint("custom clef", neumClef);
		staff.addClef(clef);
		
		Rational onset = Rational.ZERO;

		NotationChord nc1 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 2));
		addHead(nc1, 1); voice.add(nc1); onset = onset.add(new Rational(1, 2));
		addLyric(voice, nc1, "l");

		NotationChord nc3 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 4));
		addHead(nc3, 10); voice.add(nc3); onset = onset.add(new Rational(1, 4));
		addLyric(voice, nc3, "le");
		
		NotationChord nc4 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc4, 2); voice.add(nc4); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc4, "lu");
		
		NotationChord nc5 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc5, 3); voice.add(nc5); onset = onset.add(new Rational(1, 64));

		NotationChord nc6 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc6, 4); voice.add(nc6); onset = onset.add(new Rational(1, 64));

		NotationChord nc7 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 64));
		addHead(nc7, 4); voice.add(nc7); onset = onset.add(new Rational(1, 64));

		NotationChord nc8 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 2));
		addHead(nc8, 1); voice.add(nc8); onset = onset.add(new Rational(1, 2));

		NotationChord nc9 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 32));
		addHead(nc9, 1); voice.add(nc9); onset = onset.add(new Rational(1, 32));
		addLyric(voice, nc9, "ia.");

		Barline barline1 = new Barline(onset);
		barline1.addRenderingHint("custom", new float[]{-0.5f, 0.5f});
		system.addBarline(barline1);

		NotationChord nc10 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 32));
		addHead(nc10, 1); voice.add(nc10); onset = onset.add(new Rational(1, 32));
		
		addLyric(voice, nc10, "*");
		NotationChord nc11 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc11, 1); voice.add(nc11); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc11, "ij.");
		
		NotationChord nc12 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc12, 2); voice.add(nc12); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc13 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc13, 4); voice.add(nc13); onset = onset.add(new Rational(1, 64));

		NotationChord nc14 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 64));
		addHead(nc14, 4); voice.add(nc14); onset = onset.add(new Rational(1, 64));

		NotationChord nc15 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc15, 5); voice.add(nc15); onset = onset.add(new Rational(1, 64));

		NotationChord nc16 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc16, 5); voice.add(nc16); onset = onset.add(new Rational(1, 64));

		NotationChord nc17 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc17, 6); voice.add(nc17); onset = onset.add(new Rational(1, 64));

		NotationChord nc18 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc18, 5); voice.add(nc18); onset = onset.add(new Rational(1, 64));

		NotationChord nc19 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 64));
		addHead(nc19, 6); voice.add(nc19); onset = onset.add(new Rational(1, 64));

		Barline barline2 = new Barline(onset);
		barline2.addRenderingHint("custom", new float[]{0.5f, 2.5f});
		system.addBarline(barline2);

		NotationChord nc20 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 64));
		addHead(nc20, 1); voice.add(nc20); onset = onset.add(new Rational(1, 64));

		NotationChord nc21 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc21, 2); voice.add(nc21); onset = onset.add(new Rational(1, 64));

		NotationChord nc22 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc22, 7); voice.add(nc22); onset = onset.add(new Rational(1, 64));

		NotationChord nc23 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 64));
		addHead(nc23, 4); voice.add(nc23); onset = onset.add(new Rational(1, 64));

		NotationChord nc24 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 64));
		addHead(nc24, 4); voice.add(nc24); onset = onset.add(new Rational(1, 64));

		NotationChord nc25 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc25, 8); voice.add(nc25); onset = onset.add(new Rational(1, 64));

		NotationChord nc26 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 64));
		addHead(nc26, 5); voice.add(nc26); onset = onset.add(new Rational(1, 64));
		
		Barline barline3 = new Barline(onset, true);
		system.addBarline(barline3);
		
		URL preview1Url = TestCase2_8.class.getResource("preview1.svg");
		CustomSVGGraphic preview1 = new CustomSVGGraphic(preview1Url.toString(), 2, 2);
		barline3.addRenderingHint("preview", preview1);
		
		system.addLinebreak(onset);
		
		NotationChord nc27 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 2));
		addHead(nc27, 1); voice.add(nc27); onset = onset.add(new Rational(1, 2));
		addLyric(voice, nc27, "Os");
		
		NotationChord nc28 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc28, 1); voice.add(nc28); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc28, "ten");
		
		NotationChord nc29 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc29, 1); voice.add(nc29); onset = onset.add(new Rational(1, 64));

		NotationChord nc30 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc30, 1); voice.add(nc30); onset = onset.add(new Rational(1, 64));

		NotationChord nc31 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 8));
		addHead(nc31, 1); voice.add(nc31); onset = onset.add(new Rational(1, 8));

		NotationChord nc32 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 8));
		addHead(nc32, 9); voice.add(nc32); onset = onset.add(new Rational(1, 8));
		addLyric(voice, nc32, "de");
		
		NotationChord nc33 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc33, 2); voice.add(nc33); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc33, "mi");
		
		NotationChord nc34 = newChord(new ScorePitch('g', 1, 0), onset, new Rational(1, 32));
		addHead(nc34, 6); voice.add(nc34); onset = onset.add(new Rational(1, 32));

		NotationChord nc35 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc35, 1); voice.add(nc35); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc35, "hi");
		
		NotationChord nc36 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc36, 5); voice.add(nc36); onset = onset.add(new Rational(1, 64));

		NotationChord nc37 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc37, 6); voice.add(nc37); onset = onset.add(new Rational(1, 64));

		NotationChord nc38 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc38, 6); voice.add(nc38); onset = onset.add(new Rational(1, 64));

		Barline barline4 = new Barline(onset);
		barline4.addRenderingHint("custom", new float[]{-0.5f, 0.5f});
		system.addBarline(barline4);
		
		NotationChord nc39 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc39, 6); voice.add(nc39); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc39, "fa");
		
		NotationChord nc40 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 8));
		addHead(nc40, 12); voice.add(nc40); onset = onset.add(new Rational(1, 8));

		NotationChord nc41 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 8));
		addHead(nc41, 8); voice.add(nc41); onset = onset.add(new Rational(1, 8));
		addLyric(voice, nc41, "ci");
		
		NotationChord nc42 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 4));
		addHead(nc42, 1); voice.add(nc42); onset = onset.add(new Rational(1, 4));
		addLyric(voice, nc42, "em");
		
		NotationChord nc43 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc43, 2); voice.add(nc43); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc43, "tu");
		
		NotationChord nc44 = newChord(new ScorePitch('g', 1, 0), onset, new Rational(1, 64));
		addHead(nc44, 3); voice.add(nc44); onset = onset.add(new Rational(1, 64));

		NotationChord nc45 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc45, 4); voice.add(nc45); onset = onset.add(new Rational(1, 64));

		NotationChord nc46 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc46, 4); voice.add(nc46); onset = onset.add(new Rational(1, 64));

		NotationChord nc47 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc47, 13); voice.add(nc47); onset = onset.add(new Rational(1, 64));

		NotationChord nc48 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc48, 4); voice.add(nc48); onset = onset.add(new Rational(1, 64));

		NotationChord nc49 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc49, 4); voice.add(nc49); onset = onset.add(new Rational(1, 64));

		NotationChord nc50 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 32));
		addHead(nc50, 2); voice.add(nc50); onset = onset.add(new Rational(1, 32));

		NotationChord nc51 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 32));
		addHead(nc51, 1); voice.add(nc51); onset = onset.add(new Rational(1, 32));
		addLyric(voice, nc51, "am,");
		
		Barline barline5 = new Barline(onset);
		barline5.addRenderingHint("custom", new float[]{0.5f, 2.5f});
		system.addBarline(barline5);
		barline5.addRenderingHint("preview", preview1);
		
		system.addLinebreak(onset);
		
		NotationChord nc52 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc52, 1); voice.add(nc52); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc52, "so");
		
		NotationChord nc53 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc53, 1); voice.add(nc53); onset = onset.add(new Rational(1, 64));

		NotationChord nc54 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 32));
		addHead(nc54, 1); voice.add(nc54); onset = onset.add(new Rational(1, 32));

		NotationChord nc55 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc55, 1); voice.add(nc55); onset = onset.add(new Rational(1, 64));

		NotationChord nc56 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc56, 1); voice.add(nc56); onset = onset.add(new Rational(1, 64));

		NotationChord nc57 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 8));
		addHead(nc57, 1); voice.add(nc57); onset = onset.add(new Rational(1, 16));

		NotationChord nc58 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 16));
		addHead(nc58, 8); voice.add(nc58); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc58, "net");
		
		NotationChord nc59 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc59, 5); voice.add(nc59); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc59, "vox");
		
		NotationChord nc60 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 32));
		addHead(nc60, 6); voice.add(nc60); onset = onset.add(new Rational(1, 32));

		NotationChord nc61 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc61, 6); voice.add(nc61); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc61, "tu");
		
		NotationChord nc62 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 32));
		addHead(nc62, 6); voice.add(nc62); onset = onset.add(new Rational(1, 32));

		NotationChord nc63 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 32));
		addHead(nc63, 1); voice.add(nc63); onset = onset.add(new Rational(1, 32));
		addLyric(voice, nc63, "a");
		
		Barline barline6 = new Barline(onset);
		barline6.addRenderingHint("custom", new float[]{-0.5f, 0.5f});
		system.addBarline(barline6);

		NotationChord nc64 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 8));
		addHead(nc64, 1); voice.add(nc64); onset = onset.add(new Rational(1, 8));
		addLyric(voice, nc64, "in");
		
		NotationChord nc65 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 64));
		addHead(nc65, 2); voice.add(nc65); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc65, "au");
		
		NotationChord nc66 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 16));
		addHead(nc66, 2); voice.add(nc66); onset = onset.add(new Rational(1, 16));

		NotationChord nc67 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 16));
		addHead(nc67, 1); voice.add(nc67); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc67, "ri");
		
		NotationChord nc68 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 4));
		addHead(nc68, 1); voice.add(nc68); onset = onset.add(new Rational(1, 4));
		addLyric(voice, nc68, "bus");
		
		NotationChord nc69 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc69, 2); voice.add(nc69); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc69, "me");
		
		NotationChord nc70 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc70, 3); voice.add(nc70); onset = onset.add(new Rational(1, 64));

		NotationChord nc71 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc71, 4); voice.add(nc71); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc72 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc72, 4); voice.add(nc72); onset = onset.add(new Rational(1, 64));

		NotationChord nc73 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 64));
		addHead(nc73, 4); voice.add(nc73); onset = onset.add(new Rational(1, 64));

		NotationChord nc74 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc74, 12); voice.add(nc74); onset = onset.add(new Rational(1, 64));

		NotationChord nc75 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc75, 3); voice.add(nc75); onset = onset.add(new Rational(1, 64));
	
		NotationChord nc76 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc76, 4); voice.add(nc76); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc77 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc77, 4); voice.add(nc77); onset = onset.add(new Rational(1, 64));

		NotationChord nc78 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 32));
		addHead(nc78, 1); voice.add(nc78); onset = onset.add(new Rational(1, 32));
		
		NotationChord nc79 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 32));
		addHead(nc79, 6); voice.add(nc79); onset = onset.add(new Rational(1, 32));
		addLyric(voice, nc79, "is:");
		
		Barline barline7 = new Barline(onset);
		system.addBarline(barline7);
		CustomSVGGraphic preview2 = new CustomSVGGraphic(preview1Url.toString(), 2, 2);
		preview2.setVerticalShift(-0.5f);
		barline7.addRenderingHint("preview", preview2);
		
		system.addLinebreak(onset);
	
		//TODO: this is a different symbol
		NotationChord nc80 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 16));
		addHead(nc80, 2); voice.add(nc80); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc80, "vox");
		
		NotationChord nc81 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 16));
		addHead(nc81, 6); voice.add(nc81); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc81, "e");
		
		NotationChord nc82 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc82, 1); voice.add(nc82); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc82, "nim");
		
		NotationChord nc83 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc83, 1); voice.add(nc83); onset = onset.add(new Rational(1, 64));

		NotationChord nc84 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 8));
		addHead(nc84, 1); voice.add(nc84); onset = onset.add(new Rational(1, 8));

		NotationChord nc85 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 32));
		addHead(nc85, 2); voice.add(nc85); onset = onset.add(new Rational(1, 32));
		addLyric(voice, nc85, "tu");
		
		NotationChord nc86 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 32));
		addHead(nc86, 6); voice.add(nc86); onset = onset.add(new Rational(1, 32));
		addLyric(voice, nc86, "a");
		
		NotationChord nc87 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc87, 3); voice.add(nc87); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc87, "dul");
		
		NotationChord nc88 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 64));
		addHead(nc88, 4); voice.add(nc88); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc89 = newChord(new ScorePitch('g', 0, 0), onset, new Rational(1, 64));
		addHead(nc89, 4); voice.add(nc89); onset = onset.add(new Rational(1, 64));

		NotationChord nc90 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 64));
		addHead(nc90, 13); voice.add(nc90); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc91 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 64));
		addHead(nc91, 4); voice.add(nc91); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc92 = newChord(new ScorePitch('g', 0, 0), onset, new Rational(1, 64));
		addHead(nc92, 4); voice.add(nc92); onset = onset.add(new Rational(1, 64));

		NotationChord nc93 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 16));
		addHead(nc93, 2); voice.add(nc93); onset = onset.add(new Rational(1, 16));
		
		NotationChord nc94 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 16));
		addHead(nc94, 1); voice.add(nc94); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc94, "cis,");
		
		Barline barline8 = new Barline(onset);
		barline8.addRenderingHint("custom", new float[]{0.5f, 2.5f});
		system.addBarline(barline8);
		
		NotationChord nc95 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 16));
		addHead(nc95, 14); voice.add(nc95); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc95, "et");
		
		//TODO: this is a different symbol
		NotationChord nc96 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 32));
		addHead(nc96, 2); voice.add(nc96); onset = onset.add(new Rational(1, 32));
		addLyric(voice, nc96, "fa");
		
		NotationChord nc97 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 32));
		addHead(nc97, 1); voice.add(nc97); onset = onset.add(new Rational(1, 32));
		addLyric(voice, nc97, "ci");
		
		NotationChord nc98 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 8));
		addHead(nc98, 2); voice.add(nc98); onset = onset.add(new Rational(1, 8));
		addLyric(voice, nc98, "es");
		
		NotationChord nc99 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 16));
		addHead(nc99, 7); voice.add(nc99); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc99, "tu");
		
		NotationChord nc100 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 16));
		addHead(nc100, 1); voice.add(nc100); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc100, "a *");
		
		Barline barline9 = new Barline(onset);
		barline9.addRenderingHint("custom", new float[]{-0.5f, 0.5f});
		system.addBarline(barline9);
		
	
		NotationChord nc101 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc101, 1); voice.add(nc101); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc101, "de");
		
		NotationChord nc102 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc102, 1); voice.add(nc102); onset = onset.add(new Rational(1, 64));

		NotationChord nc103 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc103, 1); voice.add(nc103); onset = onset.add(new Rational(1, 64));

		NotationChord nc104 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc104, 1); voice.add(nc104); onset = onset.add(new Rational(1, 64));

		Barline barline9_5 = new Barline(onset);
		barline9_5.addRenderingHint("preview", preview2);
		barline9_5.addRenderingHint("custom", new float[]{0f, 0f});
		system.addBarline(barline9_5);
		
		system.addLinebreak(onset);
	
		NotationChord nc105 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 16));
		addHead(nc105, 9); voice.add(nc105); onset = onset.add(new Rational(1, 16));
		addLyric(voice, nc105, "co");
		
		NotationChord nc106 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc106, 2); voice.add(nc106); onset = onset.add(new Rational(1, 64));
		addLyric(voice, nc106, "ra");
		
		NotationChord nc107 = newChord(new ScorePitch('g', 1, 0), onset, new Rational(1, 32));
		addHead(nc107, 6); voice.add(nc107); onset = onset.add(new Rational(1, 32));

		NotationChord nc108 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc108, 15); voice.add(nc108); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc109 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc109, 3); voice.add(nc109); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc110 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc110, 4); voice.add(nc110); onset = onset.add(new Rational(1, 64));

		NotationChord nc111 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc111, 4); voice.add(nc111); onset = onset.add(new Rational(1, 64));

		NotationChord nc112 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc112, 1); voice.add(nc112); onset = onset.add(new Rational(1, 64));

		NotationChord nc113 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc113, 1); voice.add(nc113); onset = onset.add(new Rational(1, 64));

		NotationChord nc114 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc114, 1); voice.add(nc114); onset = onset.add(new Rational(1, 64));

		NotationChord nc115 = newChord(new ScorePitch('g', 1, 0), onset, new Rational(1, 64));
		addHead(nc115, 3); voice.add(nc115); onset = onset.add(new Rational(1, 64));

		NotationChord nc116 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc116, 4); voice.add(nc116); onset = onset.add(new Rational(1, 64));

		NotationChord nc117 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 32));
		addHead(nc117, 4); voice.add(nc117); onset = onset.add(new Rational(1, 32));
	
		NotationChord nc118 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc118, 2); voice.add(nc118); onset = onset.add(new Rational(1, 64));

		NotationChord nc119 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc119, 4); voice.add(nc119); onset = onset.add(new Rational(1, 64));

		NotationChord nc120 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 64));
		addHead(nc120, 4); voice.add(nc120); onset = onset.add(new Rational(1, 64));

		NotationChord nc121 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc121, 1); voice.add(nc121); onset = onset.add(new Rational(1, 64));

		NotationChord nc122 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 16));
		addHead(nc122, 7); voice.add(nc122); onset = onset.add(new Rational(1, 16));

		Barline barline10 = new Barline(onset);
		barline10.addRenderingHint("custom", new float[]{-0.5f, 0.5f});
		system.addBarline(barline10);
		
		NotationChord nc123 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc123, 1); voice.add(nc123); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc124 = newChord(new ScorePitch('f', 1, 0), onset, new Rational(1, 64));
		addHead(nc124, 3); voice.add(nc124); onset = onset.add(new Rational(1, 64));

		NotationChord nc125 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc125, 4); voice.add(nc125); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc126 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 32));
		addHead(nc126, 4); voice.add(nc126); onset = onset.add(new Rational(1, 32));

		NotationChord nc127 = newChord(new ScorePitch('e', 1, 0), onset, new Rational(1, 64));
		addHead(nc127, 3); voice.add(nc127); onset = onset.add(new Rational(1, 64));

		NotationChord nc128 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc128, 4); voice.add(nc128); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc129 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc129, 4); voice.add(nc129); onset = onset.add(new Rational(1, 64));

		NotationChord nc130 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 32));
		addHead(nc130, 4); voice.add(nc130); onset = onset.add(new Rational(1, 32));

		NotationChord nc131 = newChord(new ScorePitch('d', 1, 0), onset, new Rational(1, 64));
		addHead(nc131, 3); voice.add(nc131); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc132 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc132, 4); voice.add(nc132); onset = onset.add(new Rational(1, 64));

		NotationChord nc133 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 64));
		addHead(nc133, 4); voice.add(nc133); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc134 = newChord(new ScorePitch('a', 0, 0), onset, new Rational(1, 64));
		addHead(nc134, 4); voice.add(nc134); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc135 = newChord(new ScorePitch('c', 1, 0), onset, new Rational(1, 64));
		addHead(nc135, 8); voice.add(nc135); onset = onset.add(new Rational(1, 64));
		
		NotationChord nc136 = newChord(new ScorePitch('b', 0, 0), onset, new Rational(1, 16));
		addHead(nc136, 6); voice.add(nc136); onset = onset.add(new Rational(1, 16));
		
		Barline barline11 = new Barline(onset, true);
		system.addBarline(barline11);
		
        system.addRenderingHint("page bottom space", new Integer(-80));

		
		
		URL verseURL = TestCase2_8.class.getResource("verse.svg");
		SVGSymbol verse = new SVGSymbol(verseURL.toString());
		Attachable verseAttach = new Attachable(nc27.get(0), verse, -5.0f, 4.0f);
		staff.addAttachable(verseAttach);
	}
}
