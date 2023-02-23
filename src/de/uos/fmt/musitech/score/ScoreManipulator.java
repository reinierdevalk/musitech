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
package de.uos.fmt.musitech.score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.change.IDataChangeManager;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.score.gui.Score;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * 
 * @version 28.04.2007
 */
public class ScoreManipulator {

	private ScoreEditor scoreEditor;

	private SelectionManager selectionManager = SelectionManager.getManager();

	private IDataChangeManager dataChangeManager = DataChangeManager
			.getInstance();

	public ScoreManipulator(ScoreEditor scoreEditor) {
		this.scoreEditor = scoreEditor;
	}

	public void notationSystemChanged() {
		// getNotationSystem().prettyPrint();
		// for (Note n : scoreEditor.getNotes()) {
		// System.out.println(n);
		// }
		getNotationSystem().getBarlines().clear();
		getNotationSystem().setPrepared(false);

		Rational endTime = trim();

		expandTo(endTime);

		getNotationSystem().prepareForScore();

		// for (Note n : scoreEditor.getNotes()) {
		// System.out.println(n);
		// }

		MetricalTimeLine mtl = getNotationSystem().getContext().getPiece()
				.getMetricalTimeLine();
		for (Note n : scoreEditor.getNotes()) {
			ScoreNote sn = n.getScoreNote();
			if (getPreviousTiedNote(sn) == null && sn.getDiatonic() != 'r') {
				PerformanceNote pn = new PerformanceNote();
				int midi_pitch = sn.getMidiPitch();
				long midi_ts = mtl.getTime(sn.getMetricTime());
				long midi_ts_end = mtl.getTime(sn.getMetricTime().add(
						sn.getTotalMetricDuration()));
				long midi_duration = midi_ts_end - midi_ts;
				pn.setPitch(midi_pitch);
				pn.setDuration(midi_duration);
				pn.setTime(midi_ts);
				n.setPerformanceNote(pn);
			} else
				n.setPerformanceNote(null);

		}
		Piece p = getNotationSystem().getContext().getPiece();
		// TODO check this
//		p.getNotePool().clear();
//		p.getNotePool().addAll(getNotationSystem());
		Collection col = new ArrayList(1);
		col.add(getNotationSystem());

		// getNotationSystem().prettyPrint();
		dataChangeManager.changed(col, new DataChangeEvent(this, col));
	}

	private Note scoreNote2Note(ScoreNote sn) {
		for (Note n : scoreEditor.getNotes())
			if (n.getScoreNote() == sn)
				return n;
		return null;
	}

	private Note getPreviousTiedNote(ScoreNote sn) {
		for (Note n : scoreEditor.getNotes())
			if (n.getScoreNote().getTiedNote() == sn)
				return n;
		return null;
	}

	private void expandTo(Rational endTime) {
		for (int i = 0; i < scoreEditor.getNotationSystem().size(); i++) {
			NotationStaff ns = scoreEditor.getNotationSystem()
					.get(i);
			for (int j = 0; j < ns.size(); j++) {
				NotationVoice nv = ns.get(j);
				List<Note> rests = new ArrayList<Note>();
				Rational pos = nv.getEndTime();
				if (pos.isLess(endTime)) {
					while (pos.isLess(endTime)) {
						Rational barEnd = getNotationSystem().getContext()
								.getPiece().getMetricalTimeLine()
								.getNextMeasure(pos);
						ScoreNote sno = new ScoreNote(pos, barEnd.sub(pos),
								'r', (byte) 0, (byte) 0);
						rests.add(new Note(sno, null));
						pos = barEnd;
					}
				}
				for (Note r : rests)
					nv.add(r);
			}
		}
	}

	public Rational trim() {
		Rational endTime = getNotationSystem().getEndTime();
		for (int i = 0; i < scoreEditor.getNotationSystem().size(); i++) {
			NotationStaff ns = scoreEditor.getNotationSystem()
					.get(i);
			for (int j = 0; j < ns.size(); j++) {
				NotationVoice nv = ns.get(j);
				List<NotationChord> rests = new ArrayList<NotationChord>();
				for (int k = 0; k < nv.size(); k++) {
					NotationChord nc = nv.get(k);
					if (nc.isRest())
						rests.add(nc);
				}
				for (NotationChord nc : rests) {
					nv.remove(nc);
				}
			}
		}
		return endTime;
	}

	public NotationSystem getNotationSystem() {
		return scoreEditor.getNotationSystem();
	}

	public Score getScore() {
		return scoreEditor.getScore();
	}

	public void addStaff() {
		Rational endPoint = getNotationSystem().getEndTime();
		TimeSignatureMarker tsm = getNotationSystem().getContext().getPiece()
				.getMetricalTimeLine().getTimeSignatureMarker(Rational.ZERO);
		Rational barLength = tsm.getTimeSignature().getMeasureDuration();
		barLength.reduce();
		int bars = endPoint.div(barLength).floor();
		NotationStaff nstaff = new NotationStaff(getNotationSystem());
		NotationVoice nv = new NotationVoice(nstaff);
		for (int i = 0; i < bars; i++) {
			ScoreNote sn = new ScoreNote(new ScorePitch('r', 0, 0), barLength);
			sn.setMetricTime(barLength.mul(i));
			nv.add(new Note(sn, null));
		}
		nv.normalizeChords();
		notationSystemChanged();
	}

	public void addStaff(String code) {
		if (code == null || code.length() == 0)
			code = "1r1r";
		NotationStaff nstaff = new NotationStaff(getNotationSystem());
		NoteList nl = new NoteList(code);
		NotationVoice nv = new NotationVoice(nstaff);
		for (int i = 0; i < nl.size(); i++) {
			nv.add(nl.get(i));
		}
		notationSystemChanged();
	}

	public void removeStaff(int staffNumber) {
		if (getNotationSystem().size() > 1) {
			getNotationSystem().remove(staffNumber);
			notationSystemChanged();
		}
	}

	public void removeStaff() {
		removeStaff(scoreEditor.getSelectedStaff());
	}

	public boolean insertNote(Note n) {
		return insertNote(n, true);
	}

	public boolean insertNote(Note n, boolean check) {
		if (check && (!canInsert(n.getMetricTime(), n.getMetricDuration())))
			return false;
		NotationStaff ns = scoreEditor.getNotationSystem().get(
				scoreEditor.getSelectedStaff());
		NotationVoice nv = ns.get(scoreEditor
				.getSelectedVoice());
		Rational position = n.getMetricTime();
		Rational length = n.getMetricDuration();

		NotationChord before = scoreEditor.getNotationChordBefore(n
				.getMetricTime());
		NotationChord at = scoreEditor.getNotationChordAt(n.getMetricTime());
		NotationChord after = scoreEditor.getNotationChordAfter(n
				.getMetricTime());

		if (at != null) {
			if (at.getMetricDuration().equals(length)) {
				if (at.isRest())
					at.remove(0);
				at.add(n);
				return true;
			} else if (at.getMetricDuration().isGreater(length) && at.isRest()) {
				ScoreNote sn = new ScoreNote(new ScorePitch('r', 0, 0), at
						.getMetricDuration().sub(length));
				sn.setMetricTime(position.add(length));
				at.remove(0);
				at.add(n);
				nv.add(new Note(sn, null));
				return true;
			} else if (at.isRest()
					&& (after == null || (after.isRest() && after
							.getMetricTime().add(after.getMetricDuration())
							.isGreaterOrEqual(position.add(length))))) {
				at.remove(0);
				at.add(n);
				if (after != null) {
					Rational d = position.add(length)
							.sub(after.getMetricTime());
					after.setMetricTime(after.getMetricTime().add(d));
					after.setMetricDuration(after.getMetricDuration().sub(d));
				}
				return true;
			} else
				return false;
		}
		if (before != null && before.isRest()) {
			if (before.getMetricDuration().add(before.getMetricTime())
					.isGreaterOrEqual(position.add(length))) {
				before.setMetricDuration(position.sub(before.getMetricTime()));
				nv.add(n);
				return true;
			}
			nv.add(n);
		}
		return false;
	}

	public boolean canInsert(Rational position, Rational length) {
		if (position.isLess(Rational.ZERO))
			return false;
		NotationChord before = scoreEditor.getNotationChordBefore(position);
		NotationChord at = scoreEditor.getNotationChordAt(position);
		NotationChord after = scoreEditor.getNotationChordAfter(position);
		System.out.println(before + "-" + at + "-" + after);
		if (at != null) {
			if (at.getMetricDuration().equals(length))
				return true;
			else if (at.getMetricDuration().isGreater(length) && at.isRest())
				return true;
			else if (at.isRest()
					&& (after == null || (after.isRest() && after
							.getMetricTime().add(after.getMetricDuration())
							.isGreaterOrEqual(position.add(length)))))
				return true;
			else {
				System.out.println("Cannot insert because at<length");
				return false;
			}
		}
		if (before != null && before.isRest()) {
			if (before.getMetricDuration().add(before.getMetricTime())
					.isGreaterOrEqual(position.add(length)))
				return true;
			else
				return true;
		}
		System.out.println("Cannot insert");
		return false;
	}

	public void deleteSelection() {
		for (Iterator it = selectionManager.getSelection().getAll().iterator(); it
				.hasNext();) {
			Note n = (Note) it.next();
			deleteNote(n, true);
		}
		selectionManager.getSelection().clear(this);
		notationSystemChanged();
	}

	public void deleteNote(Note n, boolean fillWithRest) {
		for (int i = 0; i < scoreEditor.getNotationSystem().size(); i++) {
			NotationStaff ns = scoreEditor.getNotationSystem()
					.get(i);
			for (int j = 0; j < ns.size(); j++) {
				NotationVoice nv = ns.get(j);
				for (int k = 0; k < nv.size(); k++) {
					NotationChord nc = nv.get(k);
					if (nc.contains(n)) {
						nc.remove(n);
						if (nc.isEmpty()) {
							if (fillWithRest) {
								ScoreNote sn = new ScoreNote(new ScorePitch(
										'r', 0, 0), n.getMetricDuration());
								sn.setMetricTime(n.getMetricTime());
								nc.add(new Note(sn, null));
							} else
								nv.remove(nc);
						}
					}
				}
			}
		}
	}

	public void setAlterationlOnSelection(byte accidental) {
		for (Iterator it = selectionManager.getSelection().getAll().iterator(); it
				.hasNext();) {
			Note n = (Note) it.next();
			n.getScoreNote().setAlteration(accidental);
		}
		notationSystemChanged();
	}

	public void setAccentOnSelection(byte accent) {
		for (Iterator it = selectionManager.getSelection().getAll().iterator(); it
				.hasNext();) {
			Note n = (Note) it.next();
			n.getScoreNote().setAccents(new byte[] { accent });
		}
		notationSystemChanged();
	}

	public void transpose(Note n, int interval) {
		char[] pitches = { 'c', 'd', 'e', 'f', 'g', 'a', 'b' };
		int index = 0;
		while (n.getScoreNote().getDiatonic() != pitches[index]
				&& index < pitches.length)
			index++;
		int newIndex = (index + interval) % 7;
		int oct = (index + interval) / 7;
		if (newIndex < 0) {
			newIndex += 7;
			oct -= 1;
		}
		n.getScoreNote().setDiatonic(pitches[newIndex]);
		n.getScoreNote().setOctave((byte) (n.getScoreNote().getOctave() + oct));
		KeyMarker km = null;
		for (Iterator<Marker> i = getNotationSystem().getContext().getPiece().getHarmonyTrack().iterator(); i.hasNext();) {
			Marker o = i.next();
			if (o instanceof KeyMarker) {
				KeyMarker k = (KeyMarker) o;
				if (k.getMetricTime().isLessOrEqual(n.getMetricTime()))
					km = k;
			}
		}
		if (km != null)
			n.getScoreNote().setAlteration(
					KEY_SIGNATURES[km.getAlterationNum() + 6][newIndex]);
		else
			n.getScoreNote().setAlteration((byte) 0);
	}

	public static final byte[][] KEY_SIGNATURES = {
			{ -1, -1, -1, 0, -1, -1, -1 }, { 0, -1, -1, 0, -1, -1, -1 },
			{ 0, -1, -1, 0, 0, -1, -1 }, { 0, 0, -1, 0, 0, -1, -1 },
			{ 0, 0, -1, 0, 0, 0, -1 }, { 0, 0, 0, 0, 0, 0, -1 },
			{ 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0, 0, 0 },
			{ 1, 0, 0, 1, 0, 0, 0 }, { 1, 0, 0, 1, 1, 0, 0 },
			{ 1, 1, 0, 1, 1, 0, 0 }, { 1, 1, 0, 1, 1, 1, 0 },
			{ 1, 1, 1, 1, 1, 1, 0 }, };

	public void moveSelection(int interval, Rational dx) {
		if (dx.equals(Rational.ZERO))
			for (Iterator i = selectionManager.getSelection().getAll()
					.iterator(); i.hasNext();) {
				transpose((Note) i.next(), interval * -1);
			}
		else {
			for (Iterator it = selectionManager.getSelection().getAll()
					.iterator(); it.hasNext();) {
				Note n = (Note) it.next();
				deleteNote(n, true);
			}
			boolean canMove = true;
			for (Iterator i = selectionManager.getSelection().getAll()
					.iterator(); i.hasNext();) {
				Note n = (Note) i.next();
				canMove &= canInsert(n.getMetricTime().add(dx), n
						.getMetricDuration());
			}
			if (canMove) {
				for (Iterator i = selectionManager.getSelection().getAll()
						.iterator(); i.hasNext();) {
					Note n = (Note) i.next();
					transpose(n, interval * -1);
					n.getScoreNote().setMetricTime(n.getMetricTime().add(dx));
				}
			}
			for (Iterator it = selectionManager.getSelection().getAll()
					.iterator(); it.hasNext();) {
				Note n = (Note) it.next();
				insertNote(n, false);
			}
		}
		// selectionManager.getSelection().clear(this);
		notationSystemChanged();
	}

	public void appendMeasure() {
		Rational endPoint = getNotationSystem().getEndTime();
		endPoint.reduce();
		TimeSignatureMarker tsm = getNotationSystem().getContext().getPiece()
				.getMetricalTimeLine().getTimeSignatureMarker(Rational.ZERO);
		Rational barLength = tsm.getTimeSignature().getMeasureDuration();
		barLength.reduce();
		for (int i = 0; i < getNotationSystem().size(); i++) {
			NotationVoice nv = getNotationSystem()
					.get(i).get(0);
			ScoreNote sn = new ScoreNote(new ScorePitch('r', 0, 0), barLength);
			sn.setMetricTime(endPoint);
			nv.add(new Note(sn, null));
			nv.normalizeChords();
		}
		notationSystemChanged();
	}

	public void setClef(char clef, int line, int shift) {
		NotationStaff ns = scoreEditor.getNotationSystem().get(
				scoreEditor.getSelectedStaff());
		ns.setClefType(clef, line, shift);
		notationSystemChanged();
	}

	public void setTimeSignature(Rational r) {
		Piece piece = getNotationSystem().getContext().getPiece();
		MetricalTimeLine mtl = piece.getMetricalTimeLine();
		if (mtl == null) {
			mtl = new MetricalTimeLine();
			piece.setMetricalTimeLine(mtl);
		}
		List<TimeSignatureMarker> markersToRemove = new ArrayList<TimeSignatureMarker>();
		for (Iterator i = mtl.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof TimeSignatureMarker) {
				TimeSignatureMarker tsm = (TimeSignatureMarker) o;
				markersToRemove.add(tsm);
			}
		}
		for (TimeSignatureMarker tsm : markersToRemove)
			mtl.remove(tsm);

		mtl.add(new TimeSignatureMarker(r.getNumer(), r.getDenom(),
				Rational.ZERO));
		notationSystemChanged();
	}

	public void setKey(int accidentals) {
		Piece piece = getNotationSystem().getContext().getPiece();
		Container ht = piece.getHarmonyTrack();
		List<KeyMarker> kms = new ArrayList<KeyMarker>();
		for (Iterator i = ht.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) o;
				kms.add(km);
			}
		}
		for (KeyMarker km : kms) {
			ht.remove(km);
		}
		KeyMarker km = new KeyMarker(Rational.ZERO, 0);
		km.setAccidentalNum(accidentals);
		ht.add(km);
		notationSystemChanged();
	}

	public void tieSelection() {
		List<Note> notes = new ArrayList<Note>(selectionManager.getSelection()
				.getAll().size());
		for (Iterator i = selectionManager.getSelection().getAll().iterator(); i
				.hasNext();) {
			Note n = (Note) i.next();
			notes.add(n);
		}
		tie(notes);
	}

	public void tie(List<Note> notes) {
		if (notes.size() == 0)
			return;
		Collections.sort(notes, new Comparator<Note>() {
			@Override
			public int compare(Note o1, Note o2) {
				return o1.getMetricTime().compare(o2.getMetricTime());
			}
		});
		char pitch = notes.get(0).getScoreNote().getDiatonic();
		Note lastNote = null;
		for (Note note : notes) {
			if (note.getScoreNote().getDiatonic() != pitch)
				return;
			if (lastNote != null
					&& !lastNote.getScoreNote().getMetricEndTime().equals(
							note.getScoreNote().getMetricTime()))
				return;
			lastNote = note;
		}
		for (int i = 0; i < notes.size() - 1; i++)
			notes.get(i).getScoreNote().setTiedNote(
					notes.get(i + 1).getScoreNote());
		notationSystemChanged();
	}

	public void untieSelection() {
		for (Iterator i = selectionManager.getSelection().getAll().iterator(); i
				.hasNext();) {
			Note n = (Note) i.next();
			n.getScoreNote().setTiedNote(null);
		}
		notationSystemChanged();
	}

	public void untie(List<Note> notes) {
		for (Note note : notes)
			note.getScoreNote().setTiedNote(null);
		notationSystemChanged();
	}

	public void joinSelection() {
		List<Note> notes = new ArrayList<Note>(selectionManager.getSelection()
				.getAll().size());
		for (Iterator i = selectionManager.getSelection().getAll().iterator(); i
				.hasNext();) {
			Note n = (Note) i.next();
			notes.add(n);
		}
		join(notes);
	}

	public void join(List<Note> tiedNotes) {
		//Testen ob alle angebundenen Noten selektiert wurden
		for (Note n1 : tiedNotes) {
			ScoreNote sn = n1.getScoreNote().getTiedNote();
			while (sn != null){
				boolean found = false;
				for (Note n2 : tiedNotes) {
					ScoreNote sn2 = n2.getScoreNote();
					if(sn==sn2){
						found=true;
					}
				}
				if(!found)
					return;
				sn = sn.getTiedNote();
			}
		}
		
		//Alle angebundenen Noten entfernen
		List<Note> tiedNotesToRemove = new ArrayList<Note>();
		for (Note n1 : tiedNotes)
			for (Note n2 : tiedNotes)
				if (n2.getScoreNote().getTiedNote() == n1.getScoreNote())
					tiedNotesToRemove.add(n1);
		tiedNotes.removeAll(tiedNotesToRemove);
		for (Note tntr : tiedNotesToRemove)
			deleteNote(tntr, false);
		for (Note n : tiedNotes){
			n.getScoreNote().setMetricDuration(n.getScoreNote().getTotalMetricDuration());
			n.getScoreNote().setTiedNote(null);
		}
		notationSystemChanged();
	}
}
