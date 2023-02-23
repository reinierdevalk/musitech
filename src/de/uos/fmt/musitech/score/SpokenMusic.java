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
 * Created on 10.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uos.fmt.musitech.score;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BarlineContainer;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author kdaling
 * 
 * TODO Comment
 */
public class SpokenMusic {
	
	public static final int OCTAVE_SHIFT = 4;
	
	static int calcBase(char name, int oct) {
		int b = -1;
		switch (name) {
		case 'c':
			b = 0;
			break;
		case 'd':
			b = 1;
			break;
		case 'e':
			b = 2;
			break;
		case 'f':
			b = 3;
			break;
		case 'g':
			b = 4;
			break;
		case 'a':
			b = 5;
			break;
		case 'b':
			b = 6;
			break;
		}
		if (b >= 0) {
			return oct*7 + b;
		} else {
			return -1;
		}
	}

	static boolean needOctaveInformation(int base, int prev_base) {
		// do not use the octave information if the two notes differ at most a third
		if (Math.abs(base - prev_base) <= 2) {
			return false;
		}
		// use the octave information if the notes differ at least a sixth
		if (Math.abs(base - prev_base) > 4) {
			return true;
		}
		// only use octave information if the notes are not in the same octave
		if (base / 7 == prev_base / 7) {
			return false;
		} else {
			return true;
		}
	}
	
	static String getAccidental(byte [] acc, int base, byte new_acc) {
		if (acc[base] == new_acc) {
			return "";
		} else {
			return ScoreNote.convertAccidentalToString(new_acc);
		}
	}

	static String getAccidental(int [] acc, int base, int new_acc) {
		if (acc[base] == new_acc) {
			return "";
		} else {
			return ScoreNote.convertAccidentalToString(new_acc);
		}
	}

	static String convertDotNumberToString(int num) {
		switch (num) {
		case 0:
			return "";
		case 1:
			return "dotted";
		case 2:
			return "double dotted";
		case 3:
			return "triple dotted";
		default:
			return null;
		}
	}
	
	static String convertOrdinalToString(int i) {
		switch (i) {
		case 1:
			return "first";
		case 2:
			return "second";
		case 3:
			return "third";
		case 4:
			return "fourth";
		case 5:
			return "fifth";
		case 6:
			return "sixth";
		case 7:
			return "seventh";
		case 8:
			return "eighth";
		case 9:
			return "nineth";
		case 10:
			return "tenth";
		case 11:
			return "eleventh";
		case 12:
			return "twelvth";
		default:
			return null;
		}
	}
		
	static String convertBaseRationalToString(Rational r) {
		if (r.getNumer() == 2 && r.getDenom() == 1) {
			return "double";
		}
		if (r.getNumer() != 1) {
			// TODO handle wrong argument
			return null;
		}
		switch (r.getDenom()) {
		case 1:
			return "whole";
		case 2:
			return "half";
		case 4:
			return "quarter";
		case 8:
			return "eighth";
		case 16:
			return "sixteenth";
		case 32:
			return "thirtysecond";
		case 64:
			return "sixtyfourth";
		case 128:
			return "onehundredtwentyeighth";
		default:
			return null;
		}
	}
	
	static int [] getAcc(int num) {
		int [] acc = new int[7];
		switch (num) {
		case -7:
			acc[3] = -1;
		case -6:
			acc[0] = -1;
		case -5:
			acc[4] = -1;
		case -4:
			acc[1] = -1;
		case -3:
			acc[5] = -1;
		case -2:
			acc[2] = -1;
		case -1:
			acc[6] = -1;
		}

		switch (num) {
		case 7:
			acc[6] = 1;
		case 6:
			acc[2] = 1;
		case 5:
			acc[5] = 1;
		case 4:
			acc[1] = 1;
		case 3:
			acc[4] = 1;
		case 2:
			acc[0] = 1;
		case 1:
			acc[3] = 1;
		}

		return acc;
	}
	
	public static String generateSpokenMusic(Piece p) {
		String result = "";
		if (p.getScore()==null){
		    p.setScore(NotationDisplay.createNotationSystem(p));
		}
		NotationSystem nsys = p.getScore();
		Map mm = p.getMetaMap();
		Container mtl = p.getHarmonyTrack();
		KeyMarker km;
		int acc_num = 0;
		for (Iterator iter_mtl = mtl.iterator(); iter_mtl.hasNext();) {
			Marker mk = (Marker) iter_mtl.next();
			if (KeyMarker.class.isAssignableFrom(mk.getClass())) {
				km = (KeyMarker) mk;
				acc_num = km.getAccidentalNum();
				if (acc_num < -1) {
					result += -acc_num + " flats.\n";
				} else if (acc_num == -1) {
					result += "one flat.\n";
				} else if (acc_num == 0) {
					result += "no key signature.\n";
				} else if (acc_num == 1) {
					result += "one sharp.\n";
				} else {
					result += acc_num + "sharps.\n";
				}
				break;
			}
		}
		int [] acc = getAcc(acc_num);
		// meta information
		MetaDataCollection mdc = (MetaDataCollection) mm.get(p);
		String title = "";
		if (mdc != null) {
			Set ks = mdc.keySet();
			for (Iterator iter_ks = ks.iterator(); iter_ks.hasNext();) {
				String key = iter_ks.next().toString();
				MetaDataItem mdi = mdc.getItemByKey(key);
				String mv = mdi.getMetaDataValue().getMetaValue().toString();
				if (key != null) {
					result += "The " + key + " is " + mv + ".\n";
					if (key.compareToIgnoreCase("title") == 0) {
						title = mv;
					}
				}
			}
		}

		result += "The excerpt is read in fourth notes.\n";
		Rational standard_length = new Rational(1,4);

		BarlineContainer blc = nsys.getBarlines();
		result += "@f.\n";
		
		// iterate over the barlines numbered by i
		int i = 1;
		Iterator iter_blc = blc.iterator();
		boolean last_bar = false;
		Rational bar_begin = Rational.ZERO;
		Rational bar_end = null;
		Rational end_time = nsys.getEndTime();
		Barline bl;
		do {
			if (iter_blc.hasNext()) {
				bl = (Barline) iter_blc.next();
				bar_end = bl.getMetricTime();
				if (!end_time.isGreater(bar_end)) {
					last_bar = true;
				}
			} else {
				bar_end = end_time;
				last_bar = true;
			}
			result += "bar " + i + ".\n" ;
				// + "  begins at " + bar_begin.getNumer() + "/" + bar_begin.getDenom() + " and ends at " + bar_end.getNumer() + "/" + bar_end.getDenom() + ".\n";

			int j = 0;
			for (int iter_nsys = nsys.size() - 1; iter_nsys >= 0; iter_nsys--) {
	            NotationStaff staff = nsys.get(iter_nsys);
	            for (int iter_staff = staff.size() - 1; iter_staff>= 0; iter_staff--) {
	                NotationVoice voice = staff.get(iter_staff);
	                int bb = voice.find(bar_begin);
	                int be = voice.find(bar_end);
	                if (be < 0) {
	                	be = voice.size();
	                }
	                if (be > bb && bb >= 0) {
	                	j++;
	        			int prev_base = -100; // previous used octave;
	                	if (j > 1) {
	                		result += "in agreement with\n";
	                	}
	                	for (int k = bb; k<be; k++) {
	                		NotationChord chord = voice.get(k);
	                		int chord_size = chord.size();
	                		if (chord_size <= 0) {
	                			System.out.println("Problem: Chord without Note");
	                			// TODO handle exception
	                		}
	                		
	                		// begin slur
	                		if (voice.beginOfSlur(chord)) {
	                			result += "begin slur.\n";
	                		}
	                		
	                		// print octave
	                		int iter_chord = 0;
	                		Note n = chord.get(iter_chord);
	                		ScoreNote sn = n.getScoreNote();
	                		char note_name = sn.getDiatonic();
	                		int oct = sn.getOctave() + OCTAVE_SHIFT;
	                		int base = calcBase(note_name, oct);
	                		// print name
	                		if (note_name == 'r') {
	                			result += "rest";
	                		} else {
	                			// result += "needOctaveInformation(" + base + ", " + prev_base + ")\n";
	                			if (needOctaveInformation(base, prev_base)) {
	                				result += convertOrdinalToString(oct) + " octave ";
	                			}
	                			prev_base = base;
	                			result += note_name;
	                			if (sn.getAlteration() != 0) { 
	                				result += " " + ScoreNote.convertAccidentalToString(sn.getAlteration());
	                			}
	                		}
	                		// TODO consider tied notes
	                		
	                		// duration
	                		Rational dur = chord.getMetricDuration();
	                		Rational floor = dur.getFloorPower2();
	                		if (!floor.equals(standard_length)) {
	                			result += " " + convertBaseRationalToString(floor);
	                		}
	                		result += " " + convertDotNumberToString(MPEG_SMR_Tools.numberOfDots(dur));

	                		// further notes in chord
	                		if (chord_size > 1) {
	                			iter_chord++;
	                			result += " with";
	                			Note n2 = chord.get(iter_chord);
	                			ScoreNote sn2 = n2.getScoreNote();
	                			int base2 = calcBase(sn2.getDiatonic(), sn2.getOctave() + OCTAVE_SHIFT);
	                			result += " " + convertOrdinalToString(base2-base+1);
	                			result += " " + getAccidental(acc, base2 % 7, sn2.getAlteration());
	                			for (iter_chord++; iter_chord < chord_size; iter_chord++) {
									n2 = chord.get(iter_chord);
		                			sn2 = n2.getScoreNote();
		                			base2 = calcBase(sn2.getDiatonic(), sn2.getOctave() + OCTAVE_SHIFT);
		                			result += " and " + convertOrdinalToString(base2-base+1);
		                			result += " " + getAccidental(acc, base2 % 7, sn2.getAlteration());
								}
	                		}
	                		// accents
	                		byte [] accents = sn.getAccents();
	                		if (accents != null) {
	                			for (int index = 0; index < accents.length; index++) {
	                				if (index > 0) {
	                					result += " and ";
	                				} else {
	                					result += " ";
	                				}
	                				result += ScoreNote.convertAccentToString(accents[index]);
	                			}
	                		}
	                		result += ".\n";
	                		
	                		// end slur
	                		if (voice.endOfSlur(chord)) {
	                			result += "end slur.\n";
	                		}
	                		
	                	}
	                }
	            }
	        }
			i++;
			bar_begin = bar_end;
		} while (last_bar == false);
		

		result += "end of piece: " + title + ".\n";
		return result.replaceAll("  *", " ").replaceAll("  *\\.", ".");
	}
}
