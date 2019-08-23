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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;

import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.DynamicsLevelMarker;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.TupletContainer;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * 
 * @version 25.01.2008
 */
public class PartImporter extends Mappings {

	public static Piece importSMR(File part) {
		try {
			Document doc = new SAXBuilder().build(part);
			return importSMR(doc);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (JDOMException ex) {
			ex.printStackTrace();
		}
		return new Piece();
	}

	public static Piece importSMR(Document partDoc) {
		Piece p = new Piece();
		PartImporter i = new PartImporter(partDoc);
		i.importIntoPiece(p);
		return p;
	}

	public static void importSMR(Document partDoc, Piece p) {
		PartImporter i = new PartImporter(partDoc);
		i.importIntoPiece(p);
	}

	private Piece p;
	private Element part;
	private NotationSystem nsys;
	private NotationStaff[] staffs;
	private NotationVoice[] voices;
	private Element[] clefs;
	private Element[] keysignatures;
	private Element timesignature;
	private List<NotationChord> entryChords;
	private int accidentals;
	private List<Map<Character, Byte>> alteredDiatonics;
	private AddressMapImport addressMap;
	private Element parentMeasure;
	private Element parentLayer;
	private Element parentBeam;
	private Element parentChord;

	@SuppressWarnings("unchecked")
	public PartImporter(Document partDoc) {
		Namespace ns = Namespace.getNamespace("", "");
		Iterator<Element> it = partDoc.getDescendants(new Filter() {
			public boolean matches(Object obj) {
				if (obj instanceof Element) {
					return true;
				}
				return false;
			}
		});
		while (it.hasNext())
			it.next().setNamespace(ns);
		this.part = partDoc.getRootElement();
		entryChords = new ArrayList<NotationChord>();
		accidentals = 0;
		addressMap = new AddressMapImport();
	}

	public void importIntoPiece(Piece p) {
		this.p = p;
		this.nsys = p.getScore();
		if (nsys == null) {
			nsys = new NotationSystem(p.getContext());
			p.setScore(nsys);
		}
		Element score = part.getChild(SCORE);
		if (score != null)
			try {
				importScore(score);
			} catch (DataConversionException ex) {
				ex.printStackTrace();
			}
	}

	public AddressMapImport getAddressMap() {
		return addressMap;
	}

	@SuppressWarnings("unchecked")
	private void importScore(Element score) throws DataConversionException {
		int numberOfStaffs = score.getAttribute(SCORE_NUMBEROFSTAFFS) != null ? score
				.getAttribute(SCORE_NUMBEROFSTAFFS).getIntValue()
				: 1;
		staffs = new NotationStaff[numberOfStaffs];
		clefs = new Element[numberOfStaffs];
		keysignatures = new Element[numberOfStaffs];
		alteredDiatonics = new ArrayList<Map<Character, Byte>>(numberOfStaffs);
		for (int i = 0; i < numberOfStaffs; i++) {
			NotationStaff staff = new NotationStaff(nsys);
			staffs[i] = staff;
			alteredDiatonics.add(new HashMap<Character, Byte>());
		}
		List<Element> measureList = score.getChildren(MEASURE);
		if (measureList.size() == 0)
			return;
		int numberOfVoices = 0;
		for (Element m : measureList)
			numberOfVoices = Math.max(numberOfVoices, m.getChildren(LAYER)
					.size());
		voices = new NotationVoice[numberOfVoices];
		for (int i = 0; i < numberOfVoices; i++)
			voices[i] = new NotationVoice(staffs[guessStaff(
					measureList, i)]);
		for (Element m : measureList) {
			importMeasure(m);
		}
		List<Element> horizontalList = score.getChildren(HORIZONTAL);
		List<TupletContainer> tclist = new ArrayList<TupletContainer>();
		for (Element h : horizontalList) {
			Note n1 = addressMap.getNote((Element) h.getChildren(ADDRESS)
					.get(0));
			Note n2 = addressMap.getNote((Element) h.getChildren(ADDRESS)
					.get(1));
			if (n1 != null && n2 != null) {
				int vNumber = getVoice(n1);
				NotationVoice v = voices[vNumber];
				if (h.getAttributeValue(HORIZONTAL_TYPE).equals(
						HORIZONTAL_TYPE_SLUR)) {
					SlurContainer sc = new SlurContainer(p.getContext());
					sc.add(n1);
					sc.add(n2);
					v.addSlurContainer(sc);
				} else if (h.getAttributeValue(HORIZONTAL_TYPE).equals(
						HORIZONTAL_TYPE_TIE)) {
					n1.getScoreNote().setTiedNote(n2.getScoreNote());
				} else if (h.getAttributeValue(HORIZONTAL_TYPE).equals(
						HORIZONTAL_TYPE_TUPLE)) {
					TupletContainer tc = new TupletContainer(p.getContext(),
							(byte) h.getAttribute(HORIZONTAL_TUPLENUMBER)
									.getIntValue());
					List<Note> notes = findNotesBetween(n1, n2);
					tc.addAll(notes);
					tc.calcMetricDuration();
					tclist.add(tc);
				}
			}
		}
		Collections.sort(tclist, new Comparator<TupletContainer>() {
			public int compare(TupletContainer t1, TupletContainer t2) {
				return t1.getMetricTime().compare(t2.getMetricTime());
			}
		});
		for (int i = 0; i < tclist.size(); i++) {
			TupletContainer tc = tclist.get(i);
			Note n = ((Note) tc.get(tc.size() - 1));
			Rational diff = n.getMetricDuration().mul(1);
			Rational oldEndtime = n.getMetricTime().add(n.getMetricDuration());
			NotationVoice v = voices[getVoice(n)];
			v.addTuplet(tc);
			System.out.println(tc.getContent());
			for (int j = 0; j < v.size(); j++) {
				NotationChord nc = (NotationChord) v.get(j);
				if (nc.getMetricTime().isGreaterOrEqual(oldEndtime)) {
					nc.setMetricTime(nc.getMetricTime().sub(diff));
					NotationChord[] ecs = nc.getEntryChord();
					if (ecs != null)
						for (NotationChord ec : ecs)
							ec.setMetricTime(ec.getMetricTime().sub(diff));
				}
			}
		}
	}

	private List<Note> findNotesBetween(Note n1, Note n2) {
		List<Note> notes = new ArrayList<Note>();
		NotationVoice v = voices[getVoice(n1)];
		boolean add = false;
		for (int i = 0; i < v.size(); i++) {
			NotationChord nc = (NotationChord) v.get(i);
			if (nc.contains(n1))
				add = true;
			if (add) {
				for (Object n : nc) {
					notes.add((Note) n);
				}
			}
			if (nc.contains(n2))
				return notes;
		}
		return notes;
	}

	private int getVoice(Note n) {
		for (int i = 0; i < voices.length; i++) {
			for (int j = 0; j < voices[i].size(); j++) {
				NotationChord nc = (NotationChord) voices[i].get(j);
				if (nc.contains(n))
					return i;
				NotationChord[] ec = nc.getEntryChord();
				if (ec != null)
					for (NotationChord nec : ec)
						if (nec.contains(n))
							return i;
			}
		}
		System.out.println("Voice not found");
		return 0;
	}

	@SuppressWarnings("unchecked")
	private void importMeasure(Element m) throws DataConversionException {
		parentMeasure = m;
		List<Element> headers = m.getChildren(HEADER);
		for (int i = 0; i < headers.size(); i++) {
			Element h = headers.get(i);
			if (h.getChild(CLEF) != null
					&& (clefs[i] == null || !clefs[i].getAttributeValue(
							CLEF_TYPE).equals(
							h.getChild(CLEF).getAttributeValue(CLEF_TYPE)))) {
				clefs[i] = h.getChild(CLEF);
				Clef c = Util.clefType2clef(h.getChild(CLEF));
				c.setMetricTime(staffs[i].getEndtime());
				staffs[i].addClef(c);
			}
			KeyMarker km = Util
					.keysignature2keyMarker(h.getChild(KEYSIGNATURE));
			// FIXME change: like in PartExporter
			accidentals = km.getAccidentalNum();
			char[] ad = km.alteredDiatonics();
			alteredDiatonics.get(i).clear();
			for (int j = 0; j < ad.length; j++)
				alteredDiatonics.get(i).put(Character.toLowerCase(ad[j]),
						accidentals > 0 ? (byte) 1 : (byte) -1);
			if (h.getChild(KEYSIGNATURE) != null
					&& (keysignatures[i] == null || !keysignatures[i]
							.getAttributeValue(KEYSIGNATURE_TYPE).equals(
									h.getChild(KEYSIGNATURE).getAttributeValue(
											KEYSIGNATURE_TYPE)))) {
				keysignatures[i] = h.getChild(KEYSIGNATURE);
				km.setMetricTime(staffs[i].getEndtime());
				p.getHarmonyTrack().add(km);
			}
		}
		if (m.getChild(TIMESIGNATURE) != null) {
			if (timesignature == null
					|| !Util.isEqual(timesignature, m.getChild(TIMESIGNATURE))) {
				timesignature = m.getChild(TIMESIGNATURE);
				TimeSignatureMarker tsm = new TimeSignatureMarker(Util
						.timesignatureType2timeSignature(timesignature), nsys
						.getEndTime());
				p.getMetricalTimeLine().add(tsm);
			}
		}
		List<Element> layerList = m.getChildren(LAYER);
		for (int i = 0; i < layerList.size(); i++) {
			Element l = layerList.get(i);
			parentLayer = l;
			parentBeam = null;
			parentChord = null;
			importElements(i, l.getChildren());
		}
	}

	@SuppressWarnings("unchecked")
	private List<Note> importElements(int voiceNumber, List<Element> objects)
			throws DataConversionException {
		List<Note> notes = new ArrayList<Note>();
		for (Element o : objects) {
			if (o.getName().equals(NOTE)) {
				Note n = Util.noteType2note(o,
						clefs[getStaffIndex(voiceNumber)]);
				n.getScoreNote()
						.setMetricTime(voices[voiceNumber].getEndTime());
				notes.add(n);
				putNote(n, o.getAttribute(NOTE_ID).getIntValue());
				correctAlteration(n, o.getChildren(ACCIDENTAL),
						getStaffIndex(voiceNumber));
				NotationChord nc = new NotationChord(p.getContext(), n);
				addDynamic(o.getChildren(DYNAMICTEXT), nc.getMetricTime(),
						voices[voiceNumber]);
				addNotationChord(nc, voiceNumber, o
						.getAttributeValue(NOTE_STATUS));
			} else if (o.getName().equals(REST)) {
				ScoreNote sn = new ScoreNote(voices[voiceNumber].getEndTime(),
						Util.durationType2rational(o
								.getAttributeValue(NOTE_DURATION), o
								.getChild(AUGMENTATION) != null ? o.getChild(
								AUGMENTATION).getAttribute(AUGMENTATION_DOTS)
								.getIntValue() : 0), 'r', (byte) 0, (byte) 0);
				Note n = new Note(sn, null);
				notes.add(n);
				putNote(n, o.getAttribute(NOTE_ID).getIntValue());
				NotationChord nc = new NotationChord(p.getContext(), n);
				addNotationChord(nc, voiceNumber, o
						.getAttributeValue(NOTE_STATUS));
			} else if (o.getName().equals(BEAM)) {
				BeamContainer bc = new BeamContainer(p.getContext());
				List<Element> b_objects = o.getChildren();
				parentBeam = o;
				List<Note> b_notes = importElements(voiceNumber, b_objects);
				parentBeam = null;
				bc.addAll(b_notes);
				notes.addAll(b_notes);
				voices[voiceNumber].addBeamContainer(bc);
			} else if (o.getName().equals(CHORD)) {
				NotationChord nc = Util.chordType2notationChord(o,
						clefs[getStaffIndex(voiceNumber)]);
				nc.setContext(p.getContext());
				nc.setMetricTime(voices[voiceNumber].getEndTime());
				addDynamic(o.getChildren(DYNAMICTEXT), nc.getMetricTime(),
						voices[voiceNumber]);
				List<Element> chordNotes = o.getChildren(CHORDNOTE);
				parentChord = o;
				int staff = getStaffIndex(voiceNumber);
				for (int i = 0; i < nc.size(); i++) {
					Note n = (Note) nc.get(i);
					correctAlteration(n, chordNotes.get(i).getChildren(
							ACCIDENTAL), staff);
					notes.add((Note) n);
					putNote(n, chordNotes.get(i).getAttribute(NOTE_ID)
							.getIntValue());
				}
				parentChord = null;
				addNotationChord(nc, voiceNumber, o
						.getAttributeValue(NOTE_STATUS));
			}
		}
		return notes;
	}

	private void addDynamic(List<Element> dynamictextList, Rational time,
			NotationVoice v) {
		for (Element d : dynamictextList) {
			DynamicsLevelMarker dlm = new DynamicsLevelMarker();
			dlm
					.setLevel(d.getAttributeValue(DYNAMICTEXT_DYNAMIC)
							.toLowerCase());
			dlm.setMetricTime(time);
			if (v.getContextTimeLine() == null) {
				v.setContextTimeLine(new MetricalTimeLine());
			}
			v.getContextTimeLine().add(dlm);
		}
	}

	private void putNote(Note n, int id) {
		try {
			if (parentMeasure == null || parentLayer == null)
				return;
			if (parentBeam == null) {
				if (parentChord == null)
					addressMap.put(parentMeasure.getAttribute(NOTE_ID)
							.getIntValue(), parentLayer.getAttribute(
							LAYER_NUMBER).getIntValue(), id, 0, 0, n);
				else
					addressMap.put(parentMeasure.getAttribute(NOTE_ID)
							.getIntValue(), parentLayer.getAttribute(
							LAYER_NUMBER).getIntValue(), parentChord
							.getAttribute(NOTE_ID).getIntValue(), id, 0, n);
			} else {
				if (parentChord == null)
					addressMap.put(parentMeasure.getAttribute(NOTE_ID)
							.getIntValue(), parentLayer.getAttribute(
							LAYER_NUMBER).getIntValue(), parentBeam
							.getAttribute(NOTE_ID).getIntValue(), id, 0, n);
				else
					addressMap.put(parentMeasure.getAttribute(NOTE_ID)
							.getIntValue(), parentLayer.getAttribute(
							LAYER_NUMBER).getIntValue(), parentBeam
							.getAttribute(NOTE_ID).getIntValue(), parentChord
							.getAttribute(NOTE_ID).getIntValue(), id, n);
			}
		} catch (DataConversionException ex) {
			ex.printStackTrace();
		}
	}

	private void correctAlteration(Note n, List<Element> accidentalList,
			int staff) {
		ScoreNote sn = n.getScoreNote();
		if (!accidentalList.isEmpty()) {
			byte acc = ACCIDENTALTYPES_BYTE.get(accidentalList.get(0)
					.getAttributeValue(ACCIDENTAL_TYPE));
			alteredDiatonics.get(staff).put(sn.getDiatonic(), acc);
		}
		if (alteredDiatonics.get(staff).containsKey(sn.getDiatonic()))
			sn.setAlteration(alteredDiatonics.get(staff).get(sn.getDiatonic()));
	}

	private void addNotationChord(NotationChord nc, int voiceNumber,
			String status) {
		if (status != null
				&& (status.equals(NOTE_STATUS_HIDDEN) || status
						.equals(NOTE_STATUS_GHOSTED)))
			for (Object n : nc)
				((Note) n).addRenderingHint("hidden", true);
		if (status != null && status.equals(NOTE_STATUS_GRACED))
			entryChords.add(nc);
		else {
			if (entryChords.size() > 0) {
				nc.setEntryChord(entryChords.toArray(new NotationChord[] {}));
				entryChords.clear();
			}
			voices[voiceNumber].add(nc);
		}
	}

	private int guessStaff(List<Element> measureList, int voiceNumber)
			throws DataConversionException {
		int[] counter = new int[staffs.length];
		for (Element m : measureList) {
			if (voiceNumber < m.getChildren(LAYER).size()) {
				Element l = (Element) m.getChildren(LAYER).get(voiceNumber);
				for (Iterator<?> i = l.getChildren().iterator(); i.hasNext();) {
					Element element = (Element) i.next();
					if (element.getAttribute(NOTE_STAFF) != null)
						counter[element.getAttribute(NOTE_STAFF).getIntValue()]++;
					if (element.getName().equals(BEAM))
						for (Iterator<?> i2 = element.getChildren().iterator(); i2
								.hasNext();) {
							Element e2 = (Element) i2.next();
							if (e2.getAttribute(NOTE_STAFF) != null)
								counter[e2.getAttribute(NOTE_STAFF)
										.getIntValue()]++;
						}
				}
			}
		}
		int max = 0;
		int maxValue = 0;
		for (int i = 0; i < counter.length; i++) {
			if (counter[i] > maxValue) {
				max = i;
				maxValue = counter[i];
			}
		}
		return max;
	}

	private int getStaffIndex(int voiceIndex) {
		for (int i = 0; i < staffs.length; i++)
			if (voices[voiceIndex].getParent() == staffs[i])
				return i;
		return 0;
	}
}
