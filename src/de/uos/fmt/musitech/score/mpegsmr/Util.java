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
package de.uos.fmt.musitech.score.mpegsmr;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.DataConversionException;
import org.jdom.Element;

import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * 
 * @version 30.01.2008
 */
public class Util extends Mappings implements Elements, Attributes {

	private static final char[] SCALE = { 'c', 'd', 'e', 'f', 'g', 'a', 'b' };

	public static int[] append(int[] a, int i) {
		return join(a, new int[] { i });
	}

	public static int[] join(int[] a1, int[] a2) {
		int[] a3 = new int[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	public static Note noteType2note(Element nt, Element ct) {
		try {
			if (ct == null) {
				ct = new Element(CLEF);
				ct.setAttribute(CLEF_TYPE, CLEF_TYPE_TREBLE);
			}
			int height = nt.getAttribute(NOTE_HEIGHT).getIntValue();
			ScorePitch sp = height2scorePitch(ct, height);
			Rational dur = durationType2rational(nt
					.getAttributeValue(NOTE_DURATION), nt
					.getChild(AUGMENTATION) != null ? nt.getChild(AUGMENTATION)
					.getAttribute(AUGMENTATION_DOTS).getIntValue() : 0);
			ScoreNote sn = new ScoreNote(sp, dur);
			Note n = new Note(sn, null);
			transferAccents(nt, n);
			return n;
		} catch (DataConversionException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static NotationChord chordType2notationChord(Element nt, Element ct) {
		try {
			if (ct == null) {
				ct = new Element(CLEF);
				ct.setAttribute(CLEF_TYPE, CLEF_TYPE_TREBLE);
			}
			Rational dur = durationType2rational(nt
					.getAttributeValue(NOTE_DURATION), nt
					.getChild(AUGMENTATION) != null ? nt.getChild(AUGMENTATION)
					.getAttribute(AUGMENTATION_DOTS).getIntValue() : 0);
			NotationChord nc = new NotationChord();
			List<Element> chordNotes = nt.getChildren(CHORDNOTE);
			for (Element cn : chordNotes) {
				int height = cn.getAttribute(NOTE_HEIGHT).getIntValue();
				ScorePitch sp = height2scorePitch(ct, height);
				ScoreNote sn = new ScoreNote(sp, dur);
				nc.add(new Note(sn, null));
			}
			transferAccents(nt, nc);
			return nc;
		} catch (DataConversionException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static ScorePitch height2scorePitch(Element ct, int height) {
		Clef c = clefType2clef(ct);

		int index = height - 2 * c.getClefLine();
		if (c.getClefType() == 'g') {

		} else if (c.getClefType() == 'c') {
			index -= 4;
		} else if (c.getClefType() == 'f') {
			index -= 8;
		}
		int octave = index / 7;
		index %= 7;
		if (index < 0) {
			index += 7;
			octave--;
		}
		char dia = SCALE[index];
		return new ScorePitch(dia, (byte) octave, (byte) 0);
	}

	public static int scorePitch2height(ScorePitch s, Clef c) {
		int height = 0;
		while (height < SCALE.length && SCALE[height] != s.getDiatonic())
			height++;

		height += 2 * c.getClefLine();
		height += 7 * s.getOctave();

		if (c.getClefType() == 'g') {

		} else if (c.getClefType() == 'c') {
			height += 4;
		} else if (c.getClefType() == 'f') {
			height += 8;
		}
		return height;
	}

	public static Clef clefType2clef(Element ct) {
		Clef c = new Clef();
		if (ct.getAttributeValue(CLEF_TYPE).equals(CLEF_TYPE_ALTO)) {
			c.setClefType('c');
			c.setClefLine(0);
		} else if (ct.getAttributeValue(CLEF_TYPE).equals(
				CLEF_TYPE_MEZZOSOPRANO)) {
			c.setClefType('c');
			c.setClefLine(-1);
		} else if (ct.getAttributeValue(CLEF_TYPE).equals(CLEF_TYPE_SOPRANO)) {
			c.setClefType('c');
			c.setClefLine(-2);
		} else if (ct.getAttributeValue(CLEF_TYPE).equals(CLEF_TYPE_TENOR)) {
			c.setClefType('c');
			c.setClefLine(1);
		} else if (ct.getAttributeValue(CLEF_TYPE).equals(CLEF_TYPE_TREBLE)) {
			c.setClefType('g');
			c.setClefLine(-1);
		} else if (ct.getAttributeValue(CLEF_TYPE).equals(CLEF_TYPE_BARITONE)) {
			c.setClefType('f');
			c.setClefLine(0);
		} else if (ct.getAttributeValue(CLEF_TYPE).equals(CLEF_TYPE_BASS)) {
			c.setClefType('f');
			c.setClefLine(1);
		} else {
			System.err.println("Clef not supported");
		}
		// TODO Es fehlen noch SchlŸssel
		return c;
	}

	public static Element clef2element(Clef c) {
		Element ct = new Element(CLEF);
		if (c.getClefType() == 'c') {
			if (c.getClefLine() == 0)
				ct.setAttribute(CLEF_TYPE, CLEF_TYPE_ALTO);
			else if (c.getClefLine() == -1)
				ct.setAttribute(CLEF_TYPE, CLEF_TYPE_MEZZOSOPRANO);
			else if (c.getClefLine() == -2)
				ct.setAttribute(CLEF_TYPE, CLEF_TYPE_SOPRANO);
			else if (c.getClefLine() == 1)
				ct.setAttribute(CLEF_TYPE, CLEF_TYPE_TENOR);
			else {
				System.err.println("Clef not supported");
			}
		} else if (c.getClefType() == 'g')
			ct.setAttribute(CLEF_TYPE, CLEF_TYPE_TREBLE);
		else if (c.getClefType() == 'f') {
			if (c.getClefLine() == 0)
				ct.setAttribute(CLEF_TYPE, CLEF_TYPE_BARITONE);
			else if (c.getClefLine() == 1)
				ct.setAttribute(CLEF_TYPE, CLEF_TYPE_BASS);
			else {
				System.err.println("Clef not supported");
			}
		} else {
			System.err.println("Clef not supported");
		}
		// TODO Es fehlen noch SchlŸssel
		return ct;
	}

	public static Rational durationType2rational(String duration, int dots) {
		Rational r = Rational.ZERO;
		if (duration.equals(DURATION_D1))
			r = new Rational(1, 1);
		else if (duration.equals(DURATION_D1_2))
			r = new Rational(1, 2);
		else if (duration.equals(DURATION_D1_4))
			r = new Rational(1, 4);
		else if (duration.equals(DURATION_D1_8))
			r = new Rational(1, 8);
		else if (duration.equals(DURATION_D1_16))
			r = new Rational(1, 16);
		else if (duration.equals(DURATION_D1_32))
			r = new Rational(1, 32);
		else if (duration.equals(DURATION_D1_64))
			r = new Rational(1, 64);
		else if (duration.equals(DURATION_D1_128))
			r = new Rational(1, 128);
		if (dots == 1)
			r = r.mul(3, 2);
		else if (dots == 2)
			r = r.mul(7, 4);
		return r;
	}

	private static final Rational _1_1 = new Rational(1, 1);
	private static final Rational _1_2 = new Rational(1, 2);
	private static final Rational _1_4 = new Rational(1, 4);
	private static final Rational _1_8 = new Rational(1, 8);
	private static final Rational _1_16 = new Rational(1, 16);
	private static final Rational _1_32 = new Rational(1, 32);
	private static final Rational _1_64 = new Rational(1, 64);
	private static final Rational _1_128 = new Rational(1, 128);

	public static String rational2durationType(Rational r) {
		if (r.isGreaterOrEqual(_1_1))
			return DURATION_D1;
		else if (r.isGreaterOrEqual(_1_2))
			return DURATION_D1_2;
		else if (r.isGreaterOrEqual(_1_4))
			return DURATION_D1_4;
		else if (r.isGreaterOrEqual(_1_8))
			return DURATION_D1_8;
		else if (r.isGreaterOrEqual(_1_16))
			return DURATION_D1_16;
		else if (r.isGreaterOrEqual(_1_32))
			return DURATION_D1_32;
		else if (r.isGreaterOrEqual(_1_64))
			return DURATION_D1_64;
		else if (r.isGreaterOrEqual(_1_128))
			return DURATION_D1_128;
		return DURATION_DGENERIC;
	}

	private static final Rational[] MAIN_RATIONALS = { _1_1, _1_2, _1_4, _1_8,
			_1_16, _1_32, _1_64, _1_128 };

	public static int rational2dots(Rational r) {
		for (Rational mr : MAIN_RATIONALS) {
			if (r.equals(mr.mul(3, 2)))
				return 1;
			else if (r.equals(mr.mul(7, 4)))
				return 2;
		}
		return 0;
	}

	public static KeyMarker keysignature2keyMarker(Element kt) {
		KeyMarker km = new KeyMarker();
		String kte = kt.getAttributeValue(KEYSIGNATURE_TYPE);
		if (kte.equals(KEYSIGNATURE_TYPE_DOdM))
			km.setAlterationNumAndMode(7, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_LAdm))
			km.setAccidentalNumAndMode(7, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_FAdM))
			km.setAccidentalNumAndMode(6, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_REdm))
			km.setAccidentalNumAndMode(6, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_SIM))
			km.setAccidentalNumAndMode(5, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_SOLdm))
			km.setAccidentalNumAndMode(5, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_MIM))
			km.setAccidentalNumAndMode(4, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_DOdm))
			km.setAccidentalNumAndMode(4, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_LAM))
			km.setAccidentalNumAndMode(3, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_FAdm))
			km.setAccidentalNumAndMode(3, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_REM))
			km.setAccidentalNumAndMode(2, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_SIm))
			km.setAccidentalNumAndMode(2, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_SOLM))
			km.setAccidentalNumAndMode(1, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_MIm))
			km.setAccidentalNumAndMode(1, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_DOM))
			km.setAccidentalNumAndMode(0, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_LAm))
			km.setAccidentalNumAndMode(0, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_FAM))
			km.setAccidentalNumAndMode(-1, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_REm))
			km.setAccidentalNumAndMode(-1, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_SIbM))
			km.setAccidentalNumAndMode(-2, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_SOLm))
			km.setAccidentalNumAndMode(-2, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_MIbM))
			km.setAccidentalNumAndMode(-3, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_DOm))
			km.setAccidentalNumAndMode(-3, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_LAbM))
			km.setAccidentalNumAndMode(-4, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_FAm))
			km.setAccidentalNumAndMode(-4, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_REbM))
			km.setAccidentalNumAndMode(-5, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_SIbm))
			km.setAccidentalNumAndMode(-5, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_SOLbM))
			km.setAccidentalNumAndMode(-6, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_MIbm))
			km.setAccidentalNumAndMode(-6, Mode.MODE_MINOR);

		else if (kte.equals(KEYSIGNATURE_TYPE_DObM))
			km.setAccidentalNumAndMode(-7, Mode.MODE_MAJOR);
		else if (kte.equals(KEYSIGNATURE_TYPE_LAbm))
			km.setAccidentalNumAndMode(-7, Mode.MODE_MINOR);

		return km;
	}

	public static Element keyMarker2element(KeyMarker km) {
		Element kt = new Element(KEYSIGNATURE);
		switch (km.getAccidentalNum()) {
		case -7:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_DOdM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_LAdm);
			break;
		case -6:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_FAdM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_REdm);
			break;
		case -5:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_SIM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_SOLdm);
			break;
		case -4:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_MIM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_DOdm);
			break;
		case -3:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_LAM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_FAdm);
			break;
		case -2:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_REM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_SIm);
			break;
		case -1:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_SOLM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_REm);
			break;
		case 0:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_DOM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_LAm);
			break;
		case 1:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_FAM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_REm);
			break;
		case 2:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_SIbM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_SOLm);
			break;
		case 3:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_MIbM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_DOm);
			break;
		case 4:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_LAbM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_FAm);
			break;
		case 5:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_REbM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_SIbm);
			break;
		case 6:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_SOLbM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_MIbm);
			break;
		case 7:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_DObM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_LAbm);
			break;
		default:
			if (km.getMode() == Mode.MODE_MAJOR)
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_DOM);
			else
				kt.setAttribute(KEYSIGNATURE_TYPE, KEYSIGNATURE_TYPE_LAm);
			break;

		}
		return kt;
	}

	public static boolean isEqual(Element ts1, Element ts2) {
		if (ts1.getAttributeValue(TIMESIGNATURE_TYPE).equals(
				ts2.getAttributeValue(TIMESIGNATURE_TYPE))) {
			if (ts1.getAttributeValue(TIMESIGNATURE_TYPE).equals(
					TIMESIGNATURE_TYPE_FRACTION)) {
				try {
					if (ts1.getAttribute(TIMESIGNATURE_NUMERATOR).getIntValue() == ts2
							.getAttribute(TIMESIGNATURE_NUMERATOR)
							.getIntValue()
							&& ts1.getAttribute(TIMESIGNATURE_DENOMINATOR)
									.getIntValue() == ts2.getAttribute(
									TIMESIGNATURE_DENOMINATOR).getIntValue())
						return true;
				} catch (DataConversionException ex) {
					ex.printStackTrace();
				}
			} else
				return true;
		}
		return false;
	}

	public static TimeSignature timesignatureType2timeSignature(Element ts) {
		if (ts.getAttribute(TIMESIGNATURE_TYPE).getValue().equals(TIMESIGNATURE_TYPE_C))
			return new TimeSignature(4, 4);
		if (ts.getAttribute(TIMESIGNATURE_TYPE).getValue().equals(TIMESIGNATURE_TYPE_CSLASH))
			return new TimeSignature(2, 2);
		try {
			if (ts.getAttribute(TIMESIGNATURE_TIMENUMERATOR) != null
					&& ts.getAttribute(TIMESIGNATURE_TIMEDENOMINATOR) != null)
				return new TimeSignature(ts.getAttribute(
						TIMESIGNATURE_TIMENUMERATOR).getIntValue(), ts
						.getAttribute(TIMESIGNATURE_TIMEDENOMINATOR)
						.getIntValue());
			if (ts.getAttribute(TIMESIGNATURE_NUMERATOR) != null
					&& ts.getAttribute(TIMESIGNATURE_DENOMINATOR) != null) {
				return new TimeSignature(ts.getAttribute(
						TIMESIGNATURE_NUMERATOR).getIntValue(), ts
						.getAttribute(TIMESIGNATURE_DENOMINATOR).getIntValue());
			}
		} catch (DataConversionException ex) {
			ex.printStackTrace();
		}
		return new TimeSignature();
	}

	public static Element timeSignature2timesignatureType(TimeSignature ts) {
		Element tst = new Element(TIMESIGNATURE);
		tst.setAttribute(TIMESIGNATURE_TYPE, TIMESIGNATURE_TYPE_FRACTION);
		tst.setAttribute(TIMESIGNATURE_NUMERATOR, Integer.toString(ts
				.getNumerator()));
		tst.setAttribute(TIMESIGNATURE_DENOMINATOR, Integer.toString(ts
				.getDenominator()));
		return tst;
	}

	public static void transferAccents(Note src, Element dst) {
		ScoreNote sn = src.getScoreNote();
		if (sn != null && sn.getAccents() != null)
			transferAccents(sn.getAccents(), dst);
	}

	public static void transferAccents(NotationChord src, Element dst) {
		Set<Byte> accents = new HashSet<Byte>();
		for (Iterator<?> i = src.iterator(); i.hasNext();) {
			Note n = (Note) i.next();
			if (n.getScoreNote() != null
					&& n.getScoreNote().getAccents() != null)
				for (byte acc : n.getScoreNote().getAccents())
					accents.add(acc);
		}
		if (accents.size() == 0)
			return;
		Byte[] accents2 = accents.toArray(new Byte[] {});
		byte[] accents3 = new byte[accents.size()];
		for (int i = 0; i < accents2.length; i++)
			accents3[i] = accents2[i];
		transferAccents(accents3, dst);
	}

	private static void transferAccents(byte[] accents, Element e) {
		for (byte acc : accents) {
			if (ACCENTS_FERMATATYPES.containsKey(acc)) {
				Element fermata = new Element(FERMATA);
				fermata.setAttribute(FERMATA_TYPE, ACCENTS_FERMATATYPES
						.get(acc));
				e.addContent(fermata);
			} else if (ACCENTS_ORNAMENTTYPES.containsKey(acc)) {
				Element ornament = new Element(ORNAMENT);
				ornament.setAttribute(ORNAMENT_TYPE, ACCENTS_ORNAMENTTYPES
						.get(acc));
				e.addContent(ornament);
			} else if (ACCENTS_MARKERTYPES.containsKey(acc)) {
				Element marker = new Element(MARKER);
				marker.setAttribute(MARKER_TYPE, ACCENTS_MARKERTYPES.get(acc));
				e.addContent(marker);
			}
		}
	}

	public static void transferAccents(Element src, Note dst) {
		transferAccents(src, new NotationChord(null, dst));
	}

	@SuppressWarnings("unchecked")
	public static void transferAccents(Element src, NotationChord dst) {
		transferFermata(src.getChildren(FERMATA), dst);
		transferOrnaments(src.getChildren(ORNAMENT), dst);
		transferMarker(src.getChildren(MARKER), dst);
	}

	private static void transferMarker(List<Element> markerList,
			NotationChord dst) {
		if (markerList != null && markerList.size() > 0)
			for (Element mt : markerList) {
				if (MARKERTYPES_ACCENTS.containsKey(mt
						.getAttributeValue(MARKER_TYPE)))
					for (Object n : dst)
						((Note) n).getScoreNote().addAccent(
								MARKERTYPES_ACCENTS.get(mt
										.getAttributeValue(MARKER_TYPE)));
				else
					System.out.printf("Marker %s not supported%n", mt
							.getAttributeValue(MARKER_TYPE));
			}
	}

	private static void transferOrnaments(List<Element> ornamentList,
			NotationChord dst) {
		if (ornamentList != null && ornamentList.size() > 0)
			for (Element ot : ornamentList) {
				if (ORNAMENTTYPES_ACCENTS.containsKey(ot
						.getAttributeValue(ORNAMENT_TYPE)))
					for (Object n : dst)
						((Note) n).getScoreNote().addAccent(
								ORNAMENTTYPES_ACCENTS.get(ot
										.getAttributeValue(ORNAMENT_TYPE)));
				else
					System.out.printf("Ornament %s not supported%n", ot
							.getAttributeValue(ORNAMENT_TYPE));
			}
	}

	private static void transferFermata(List<Element> fermataList,
			NotationChord dst) {
		if (fermataList != null && fermataList.size() > 0) {
			if (FERMATATYPES_ACCENTS.containsKey(fermataList.get(0)
					.getAttributeValue(FERMATA_TYPE)))
				for (Object n : dst)
					((Note) n).getScoreNote().addAccent(
							FERMATATYPES_ACCENTS.get(fermataList.get(0)
									.getAttributeValue(FERMATA_TYPE)));
			else
				System.out.printf("Marker %s not supported%n", fermataList.get(
						0).getAttributeValue(FERMATA_TYPE));
		}
	}

}
