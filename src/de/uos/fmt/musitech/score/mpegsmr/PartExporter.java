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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.Filter;

import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.TupletContainer;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.ClefContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * @version 03.02.2008
 */
public class PartExporter extends Mappings implements Elements, Attributes {

	public static Document exportStaff(NotationStaff staff) {
		return exportStaffs(new NotationStaff[] {staff});
	}

	public static Document exportStaffs(NotationStaff[] staffs) {
		PartExporter e = new PartExporter(staffs);
		return e.export();
	}

	private static int staffID = 1;
	private Piece piece;
	private NotationSystem system;
	private NotationStaff[] staffs;
	private NotationVoice[] voices;
	private Element part;
	private int measureID;
	private int layerID;
	private int figureID;
	private int activeStaff;
	private int accidentals;
	private Map<Character, Byte> alteredDiatonics;
	private AddressMapExport addressMap;
	private List<Note> tiedNotes;

	public PartExporter(NotationStaff[] staffs) {
		this.staffs = staffs;
		this.piece = staffs[0].getContext().getPiece();
		this.system = staffs[0].getParent();
		List<NotationVoice> voiceList = new ArrayList<NotationVoice>();
		for (NotationStaff staff : staffs)
			for (Object voice : staff)
				voiceList.add((NotationVoice) voice);
		this.voices = voiceList.toArray(new NotationVoice[] {});
		alteredDiatonics = new HashMap<Character, Byte>();
		addressMap = new AddressMapExport();
		tiedNotes = new ArrayList<Note>();
	}

	public AddressMapExport getAddressMap() {
		return addressMap;
	}

	@SuppressWarnings("unchecked")
	public Document export() {
		part = new Element(PART);
		exportScore();
		part.addContent(new Element(PRINTPAGES));
		Document doc = new Document(part);
		Namespace ns = Namespace
				.getNamespace("urn:mpeg:mpeg-4:schema:smr:smxf-part:2007");
		Iterator<Element> it = doc.getDescendants(new Filter() {

			public boolean matches(Object obj) {
				if (obj instanceof Element)
					return true;
				return false;
			}
		});
		while (it.hasNext())
			it.next().setNamespace(ns);
		return doc;
	}

	private void exportScore() {
		Element score = new Element(SCORE);
		part.addContent(score);
		score.setAttribute(SCORE_ID, Integer.toString(staffID++));
		score.setAttribute(SCORE_NUMBEROFSTAFFS, Integer
				.toString(staffs.length));
		score.setAttribute(SCORE_TYPE, SCORE_TYPE_NORMAL);
		score.setAttribute(SCORE_INSTRUMENT, "");

		List<Rational> measureEnds = new ArrayList<Rational>();
		MetricalTimeLine mtl = piece.getMetricalTimeLine();
		Rational cursor = Rational.ZERO;
		Rational endTime = system.getEndTime();
		while (cursor.isLess(endTime)) {
			TimeSignatureMarker tsm = mtl.getTimeSignatureMarker(cursor);
			if (tsm == null)
				tsm = new TimeSignatureMarker(4, 4, cursor);
			cursor = cursor.add(tsm.getTimeSignature().getMeasureDuration());
			measureEnds.add(cursor);
		}
		List<List<Element>> layers = new ArrayList<List<Element>>();
		layerID = 1;
		for (NotationVoice v : voices) {
			for (int i = 0; i < staffs.length; i++) {
				if (staffs[i].contains(v)) {
					activeStaff = i;
					break;
				}
			}
			Set<NotationChord> beamContainerStarts = new HashSet<NotationChord>();
			Set<NotationChord> beamContainerEnds = new HashSet<NotationChord>();
			for (Iterator<?> i = v.getBeamContainers().iterator(); i.hasNext();) {
				BeamContainer bc = (BeamContainer) i.next();
				if (bc.size() > 0) {
					Note first = (Note) bc.get(0);
					Note last = (Note) bc.get(bc.size() - 1);
					int pos = 0;
					while (!((NotationChord) v.get(pos)).contains(first))
						pos++;
					beamContainerStarts.add((NotationChord) v.get(pos));
					while (!((NotationChord) v.get(pos)).contains(last))
						pos++;
					beamContainerEnds.add((NotationChord) v.get(pos));
				}
			}
			List<Element> ll = new ArrayList<Element>();
			Iterator<Rational> measureEndIterator = measureEnds.iterator();
			Rational end = measureEndIterator.next();
			updateAlterations(Rational.ZERO);
			measureID = 1;
			figureID = 1;
			Element l = new Element(LAYER);
			l.setAttribute("NUMBER", Integer.toString(layerID));
			for (Iterator<?> i = v.iterator(); i.hasNext();) {
				NotationChord nc = (NotationChord) i.next();
				if (!nc.getMetricTime().isLess(end)) {
					updateAlterations(end);
					end = measureEndIterator.next();
					ll.add(l);
					l = new Element(LAYER);
					l.setAttribute("NUMBER", Integer.toString(layerID));
					measureID++;
					figureID = 1;
				}
				if (beamContainerStarts.contains(nc)) {
					beamContainerStarts.remove(nc);
					Element bt = new Element(BEAM);
					bt.setAttribute("ID", Integer.toString(figureID));
					int idInBeam = 1;
					while (!beamContainerEnds.contains(nc)) {
						addNotationChord(bt, nc, idInBeam, true);
						nc = (NotationChord) i.next();
						idInBeam++;
					}
					beamContainerEnds.remove(nc);
					addNotationChord(bt, nc, idInBeam, true);
					l.addContent(bt);
					idInBeam = 0;
				} else {
					addNotationChord(l, nc, figureID);
				}
				figureID++;
			}
			ll.add(l);
			layers.add(ll);
			layerID++;
		}

		measureID = 1;
		Rational start = Rational.ZERO;
		for (int i = 0; i < measureEnds.size(); i++) {
			Element m = new Element(MEASURE);
			score.addContent(m);
			m.setAttribute("PROGRESSIVE", Integer.toString(measureID));
			m.setAttribute("ID", Integer.toString(measureID));
			measureID++;
			// TODO Unterschied PROGRESSIVE - ID
			m.setAttribute("NUMBEROFSTAFFS", Integer.toString(staffs.length));

			// Funktioniert nicht im SMR-Editor
			if (isLinebreakAt(measureEnds.get(i))) {
				Element j = new Element(JUSTIFICATION);
				j.setAttribute("MAINLINEBREAK", TRUE);
				j.setAttribute("PARTLINEBREAK", TRUE);
				j.setAttribute("PMAINLINEBREAK", TRUE);
				j.setAttribute("PPARTLINEBREAK", TRUE);
				m.addContent(j);
			}

			for (int j = 0; j < staffs.length; j++) {
				Element h = new Element(HEADER);
				m.addContent(h);
				h.addContent(Util.clef2element(getClef(staffs[j], start)));
				h.addContent(Util.keyMarker2element(getKeyMarkerAt(start)));
			}
			TimeSignatureMarker tsm = mtl.getTimeSignatureMarker(start);
			if (tsm == null)
				tsm = new TimeSignatureMarker(4, 4, start);
			m.addContent(Util.timeSignature2timesignatureType(tsm
					.getTimeSignature()));
			for (List<Element> list : layers) {
				if (i < list.size()) {
					Element l = list.get(i);
					m.addContent(l);
				}
			}
			Element bl = new Element(BARLINE);
			if (isDoubleBarlineAt(measureEnds.get(i))) {
				bl.setAttribute(BARLINE_TYPE, BARLINE_TYPE_END);
			} else
				bl.setAttribute(BARLINE_TYPE, BARLINE_TYPE_SINGLE);
			m.addContent(bl);
			start = measureEnds.get(i);
		}
		int horizontalID = 1;
		// Export tied notes
		for (Note n : tiedNotes) {
			Element h = new Element(HORIZONTAL);
			h.setAttribute(HORIZONTAL_ID, Integer.toString(horizontalID++));
			h.setAttribute(HORIZONTAL_TYPE, HORIZONTAL_TYPE_TIE);
			Element a1 = addressMap.getAddress(n);
			Element a2 = addressMap.getAddress(getTiedNote(n));
			h.addContent(a1);
			h.addContent(a2);
			score.addContent(h);
		}
		// Export slurs and tuplets
		for (NotationVoice v : voices) {
			for (Iterator<?> i = v.getSlurContainers().iterator(); i.hasNext();) {
				SlurContainer sc = (SlurContainer) i.next();
				if (sc.size() > 0) {
					Element h = new Element(HORIZONTAL);
					h.setAttribute(HORIZONTAL_ID, Integer
							.toString(horizontalID++));
					h.setAttribute(HORIZONTAL_UPDOWN, DIRECTION_UP);
					h.setAttribute(HORIZONTAL_TYPE, HORIZONTAL_TYPE_SLUR);
					Element a1 = addressMap.getAddress((Note) sc.get(0));
					Element a2 = addressMap.getAddress((Note) sc
							.get(sc.size() - 1));
					h.addContent(a1);
					h.addContent(a2);
					score.addContent(h);
				}
			}
			for (Iterator<?> i = v.getTupletContainers().iterator(); i
					.hasNext();) {
				TupletContainer tc = (TupletContainer) i.next();
				if (tc.size() > 0) {
					Element h = new Element(HORIZONTAL);
					h.setAttribute(HORIZONTAL_ID, Integer
							.toString(horizontalID++));
					h.setAttribute(HORIZONTAL_UPDOWN, DIRECTION_UP);
					h.setAttribute(HORIZONTAL_TYPE, HORIZONTAL_TYPE_TUPLE);
					Element a1 = addressMap.getAddress((Note) tc.get(0));
					Element a2 = addressMap.getAddress((Note) tc
							.get(tc.size() - 1));
					h.addContent(a1);
					h.addContent(a2);
					score.addContent(h);
				}
			}
		}
	}

	private void addNotationChord(Element parent, NotationChord nc, int id,
									boolean inBeam) {
		for (Note n : nc)
			if (n.getScoreNote().getTiedNote() != null)
				tiedNotes.add(n);
		Element e = notationChord2xmlObject(nc, id, inBeam);
		parent.addContent(e);
	}

	private void addNotationChord(Element parent, NotationChord nc, int id) {
		addNotationChord(parent, nc, id, false);
	}

	private Element notationChord2xmlObject(NotationChord nc, int id,
											boolean inBeam) {
		Clef c = getClef(staffs[activeStaff], nc.getMetricTime());
		if (nc.size() == 1 || nc.isRest()) {
			Element nt = null;
			if (nc.isRest())
				nt = new Element(REST);
			else
				nt = new Element(NOTE);
			nt.setAttribute(NOTE_ID, Integer.toString(id));
			int dots = Util.rational2dots(nc.getSingleMetricDuration());
			if (dots > 0) {
				Element at = new Element(AUGMENTATION);
				at.setAttribute(AUGMENTATION_DOTS, Integer.toString(dots));
				nt.addContent(at);
			}
			nt.setAttribute(NOTE_DURATION, Util.rational2durationType(nc
					.getSingleMetricDuration()));
			nt.setAttribute(NOTE_STAFF, Integer.toString(activeStaff));
			if (!nc.isRest()) {
				ScoreNote sn = ((Note) nc.get(0)).getScoreNote();
				nt.setAttribute(NOTE_HEIGHT, Integer.toString(Util
						.scorePitch2height(sn.getPitch(), c)));
				// TODO untested
				if ((!alteredDiatonics.containsKey(sn.getDiatonic()) && sn
						.getAlteration() != 0)
					|| (alteredDiatonics.containsKey(sn.getDiatonic()) && alteredDiatonics
							.get(sn.getDiatonic()) != sn.getAlteration())) {
					Element acc = new Element(ACCIDENTAL);
					acc.setAttribute(ACCIDENTAL_TYPE, BYTE_ACCIDENTALTYPES
							.get(sn.getAlteration()));
					if (sn.getAlteration() == 0)
						alteredDiatonics.remove(sn.getDiatonic());
					else
						alteredDiatonics.put(sn.getDiatonic(), (byte) sn
								.getAlteration());
					nt.addContent(acc);
				}
				Util.transferAccents((Note) nc.get(0), nt);
			}
			if (inBeam)
				addressMap.put((Note) nc.get(0), measureID, layerID, figureID,
					id);
			else
				addressMap.put((Note) nc.get(0), measureID, layerID, figureID);
			return nt;
		} else {
			Element ct = new Element(CHORD);
			ct.setAttribute(NOTE_ID, Integer.toString(id));
			int dots = Util.rational2dots(nc.getSingleMetricDuration());
			if (dots > 0) {
				Element at = new Element(AUGMENTATION);
				at.setAttribute(AUGMENTATION_DOTS, Integer.toString(dots));
				ct.addContent(at);
			}
			ct.setAttribute(NOTE_DURATION, Util.rational2durationType(nc
					.getSingleMetricDuration()));
			int chordNoteID = 1;
			for (int i = 0; i < nc.size(); i++) {
				Note n = (Note) nc.get(i);
				Element cn = new Element(CHORDNOTE);
				ct.addContent(cn);
				cn.setAttribute(NOTE_ID, Integer.toString(chordNoteID));
				chordNoteID++;
				ScoreNote sn = n.getScoreNote();
				cn.setAttribute(NOTE_HEIGHT, Integer.toString(Util
						.scorePitch2height(sn.getPitch(), c)));
				// TODO untested
				if ((!alteredDiatonics.containsKey(sn.getDiatonic()) && sn
						.getAlteration() != 0)
					|| (alteredDiatonics.containsKey(sn.getDiatonic()) && alteredDiatonics
							.get(sn.getDiatonic()) != sn.getAlteration())) {
					Element acc = new Element(ACCIDENTAL);
					acc.setAttribute(ACCIDENTAL_TYPE, BYTE_ACCIDENTALTYPES
							.get(sn.getAlteration()));
					if (sn.getAlteration() == 0)
						alteredDiatonics.remove(sn.getDiatonic());
					else
						alteredDiatonics.put(sn.getDiatonic(), (byte) sn
								.getAlteration());
					cn.addContent(acc);
				}
				if (inBeam)
					addressMap.put((Note) nc.get(0), measureID, layerID,
						figureID, id, chordNoteID);
				else
					addressMap.put((Note) nc.get(0), measureID, layerID,
						figureID, chordNoteID);
			}
			chordNoteID = 0;
			Util.transferAccents(nc, ct);
			return ct;
		}
	}

	private Clef getClef(NotationStaff nStaff, Rational pos) {
		ClefContainer cc = nStaff.getClefTrack();
		if (cc == null) {
			cc = new ClefContainer();
			nStaff.setClefTrack(cc);
		}
		Clef c = cc.getClefAt(pos);
		if (c == null)
			c = new Clef();
		return c;
	}

	private KeyMarker getKeyMarkerAt(Rational pos) {
		Container<?> ht = piece.getHarmonyTrack();
		KeyMarker km = null;
		for (int i = 0; i < ht.size(); i++) {
			if (ht.get(i) instanceof KeyMarker) {
				KeyMarker tmp = (KeyMarker) ht.get(i);
				if (tmp.getMetricTime().isLessOrEqual(pos))
					km = tmp;
				else
					break;
			}
		}
		if (km == null)
			km = new KeyMarker(pos, 0);
		return km;
	}

	private void updateAlterations(Rational r) {
		KeyMarker km = getKeyMarkerAt(r);
		accidentals = km.getAccidentalNum();
		char[] ad = km.alteredDiatonics();
		alteredDiatonics.clear();
		for (int i = 0; i < ad.length; i++)
			alteredDiatonics.put(Character.toLowerCase(ad[i]),
				accidentals > 0 ? (byte) 1 : (byte) -1);
	}

	private boolean isDoubleBarlineAt(Rational position) {
		for (NotationStaff nst : staffs) {
			if (nst.getBarlines().hasBarlineAt(position)
				&& nst.getBarlines().getBarlineAt(position).isDouble())
				return true;
		}
		return false;
	}

	private boolean isLinebreakAt(Rational position) {
		for (Iterator<NotationSystem.Break> i = system.getLinebreaks().iterator(); i
				.hasNext();)
			if (i.next().equals(position))
				return true;
		return false;
	}

	private Note getTiedNote(Note n) {
		for (NotationVoice v : voices)
			for (Iterator<NotationChord> i = v.iterator(); i.hasNext();)
				for (Note note : i.next())
					if (n.getScoreNote().getTiedNote() == note.getScoreNote())
						return note;
		return null;
	}
}
