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
package de.uos.fmt.musitech.data.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.StringSymbol;
import de.uos.fmt.musitech.data.score.TupletContainer;
import de.uos.fmt.musitech.data.structure.container.ClefContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.Selection;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * @author Sascha Wegener
 * @version 25.09.2008
 */
public class PieceUtils {

	public static Piece extract(Piece p, Rational start, Rational end) {
		Piece newP = new Piece();
		NotationSystem nsys1 = p.getScore();
		NotationSystem nsys2 = new NotationSystem(newP.getContext());
		newP.setScore(nsys2);
		if (p == null || start == null || end == null)
			return newP;
		for (int j = 0; j < nsys1.size(); j++) {
			NotationStaff nst1 = nsys1.get(j);
			NotationStaff nst2 = new NotationStaff(nsys2);
			for (int k = 0; k < nst1.size(); k++) {
				NotationVoice nv1 = nst1.get(k);
				NotationVoice nv2 = new NotationVoice(nst2);
				Map<Note, Note> noteMap = new HashMap<Note, Note>();
				for (int l = 0; l < nv1.size(); l++) {
					NotationChord nc1 = nv1.get(l);
					if (nc1.getMetricTime().isGreaterOrEqual(start)
						&& nc1.getMetricTime().isLess(end)) {
						NotationChord nc2 = new NotationChord(nv2.getContext());
						for (Iterator<Note> notes = nc1.iterator(); notes
								.hasNext();) {
							Note note = notes.next();
							Note copy = ObjectCopy.copyObject(note);
							nc2.add(copy);
							noteMap.put(note, copy);
						}
						if (nc2.size() > 0)
							nv2.add(nc2);
					}
				}
				for (SlurContainer sc1 : nv1.getSlurContainers()) {
					if (noteMap.keySet().containsAll(sc1.getContent())) {
						SlurContainer sc2 = new SlurContainer(nv2.getContext());
						for (Iterator<Note> i = sc1.iterator(); i.hasNext();) {
							sc2.add(noteMap.get(i.next()));
						}
						nv2.addSlurContainer(sc2);
					}
				}
				for (TupletContainer tc1 : nv1.getTupletContainers()) {
					if (noteMap.keySet().containsAll(tc1.getContent())) {
						TupletContainer tc2 = new TupletContainer(nv2
								.getContext(), tc1.getArity());
						for (Iterator<Note> i = tc1.iterator(); i.hasNext();) {
							tc2.add(noteMap.get(i.next()));
						}
						nv2.addTuplet(tc2);
					}
				}
			}
		}
		copyMarker(p.getMetricalTimeLine(), newP.getMetricalTimeLine(), p
				.getMetricalTimeLine().getZeroMarker(), p.getMetricalTimeLine()
				.getEndMarker());
		copyMarker(p.getHarmonyTrack(), newP.getHarmonyTrack());
		trimPiece(newP);
		newP.getScore().createBeams();
		return newP;
	}

	public static Piece[] selections2newPieces(Piece p) {
		Container<Container<?>> selectionPool = p.getSelectionPool();
		Piece[] pieces = new Piece[selectionPool.size()];
		for (int i = 0; i < pieces.length; i++) {
			Selection s = (Selection) selectionPool.get(i);
			Container<Note> notes = s.getAllNotes();
			pieces[i] = new Piece();
			NotationSystem nsys1 = p.getScore();
			NotationSystem nsys2 = new NotationSystem(pieces[i].getContext());
			pieces[i].setScore(nsys2);
			for (int j = 0; j < nsys1.size(); j++) {
				NotationStaff nst1 = nsys1.get(j);
				NotationStaff nst2 = new NotationStaff(	nsys2);
				for (int k = 0; k < nst1.size(); k++) {
					NotationVoice nv1 = nst1.get(k);
					NotationVoice nv2 = new NotationVoice( nst2);
					Map<Note, Note> noteMap = new HashMap<Note, Note>();
					for (int l = 0; l < nv1.size(); l++) {
						NotationChord nc1 = nv1.get(l);
						NotationChord nc2 = new NotationChord(nv2.getContext());
						for (Note note : notes) {
							if (nc1.contains(note)) {
								Note copy = ObjectCopy.copyObject(note);
								nc2.add(copy);
								noteMap.put(note, copy);
							}
						}
						if (nc2.size() > 0)
							nv2.add(nc2);
					}
					for (SlurContainer sc1 : nv1.getSlurContainers()) {
						if (noteMap.keySet().containsAll(sc1.getContent())) {
							SlurContainer sc2 = new SlurContainer(nv2
									.getContext());
							for (Iterator<Note> it = sc1.iterator(); it
									.hasNext();) {
								sc2.add(noteMap.get(it.next()));
							}
							nv2.addSlurContainer(sc2);
						}
					}
					for (TupletContainer tc1 : nv1.getTupletContainers()) {
						if (noteMap.keySet().containsAll(tc1.getContent())) {
							TupletContainer tc2 = new TupletContainer(nv2
									.getContext(), tc1.getArity());
							for (Iterator<Note> it = tc1.iterator(); it
									.hasNext();) {
								tc2.add(noteMap.get(it.next()));
							}
							nv2.addTuplet(tc2);
						}
					}
				}
			}
			copyMarker(p.getMetricalTimeLine(),
				pieces[i].getMetricalTimeLine(), p.getMetricalTimeLine()
						.getZeroMarker(), p.getMetricalTimeLine()
						.getEndMarker());
			copyMarker(p.getHarmonyTrack(), pieces[i].getHarmonyTrack());
			trimPiece(pieces[i]);
		}
		return pieces;
	}

	private static void copyMarker(Container<Marker> in, Container<Marker> out,
									Marker... exclude) {
		List<Marker> excludeList = Arrays.asList(exclude);
		for (Iterator<Marker> i = in.iterator(); i.hasNext();) {
			Marker m = i.next();
			if (!excludeList.contains(m))
				out.add(ObjectCopy.copyObject(m));
		}
	}

	@SuppressWarnings("unchecked")
	private static void trimPiece(Piece piece) {
		Rational min = Rational.MAX_VALUE;
		Rational max = Rational.MIN_VALUE;
		NotationSystem nsys = piece.getScore();
		for (Iterator<NotationStaff> i = nsys.iterator(); i.hasNext();) {
			NotationStaff nst = i.next();
			for (Iterator<NotationVoice> j = nst.iterator(); j.hasNext();) {
				NotationVoice nv = j.next();
				if (nv.isEmpty())
					j.remove();
				else {
					NotationChord nc = nv.get(0);
					if (nc.getMetricTime().isLess(min))
						min = nc.getMetricTime();
					nc = nv.get(nv.size() - 1);
					if (nc.getMetricTime().isGreater(max))
						max = nc.getMetricTime();
				}
			}
			if (nst.isEmpty())
				i.remove();
		}
		for (Iterator<NotationStaff> i = nsys.iterator(); i.hasNext();) {
			NotationStaff nst = i.next();
			for (Iterator<NotationVoice> j = nst.iterator(); j.hasNext();) {
				NotationVoice nv = j.next();
				for (Iterator<NotationChord> k = nv.iterator(); k.hasNext();) {
					NotationChord nc = k.next();
					nc.setMetricTime(nc.getMetricTime().sub(min));
				}
			}
		}
		shift(piece.getMetricalTimeLine(), min, max);
		shift(piece.getHarmonyTrack(), min, max);
	}

	private static void shift(Container<Marker> c, Rational min, Rational max) {
		List<Marker> tmp = new ArrayList<Marker>();
		for (Iterator<Marker> i = c.iterator(); i.hasNext();) {
			Marker m = i.next();
			if (m instanceof TimeSignatureMarker || m instanceof KeyMarker) {
				if (m.getMetricTime().isLessOrEqual(min)) {
					ReflectionAccess ra = ReflectionAccess.accessForClass(m
							.getClass());
					if (ra.hasPropertyName("metricTime"))
						ra.setProperty(m, "metricTime", Rational.ZERO);
					tmp.add(m);
					i.remove();
				} else if (m.getMetricTime().isGreater(max))
					i.remove();
			}
		}
		if (tmp.size() > 0)
			c.add(tmp.get(tmp.size() - 1));
	}

	public static Piece connect(Piece... pieces) {
		PieceConnector cp = new PieceConnector(pieces);
		return cp.concatenate();
	}

	static class PieceConnector {

		private Piece[] pieces;

		public PieceConnector(Piece... pieces) {
			this.pieces = pieces;
		}

		public Piece concatenate() {
			Piece p = new Piece();
			NotationSystem score = new NotationSystem(p.getContext());
			p.setScore(score);
			int[] bounds = calculateBounds();
			for (int i = 0; i < bounds.length; i++) {
				NotationStaff nst = new NotationStaff(score);
				nst.setClefTrack(new ClefContainer(p.getContext()));
				for (int j = 0; j < bounds[i]; j++) {
					NotationVoice nv = new NotationVoice(nst);
				}
			}
			Rational r = Rational.ZERO;
			for (int i = 0; i < pieces.length; i++) {
				MetricalTimeLine mtl = p.getMetricalTimeLine();
				for (Iterator<Marker> j = pieces[i].getMetricalTimeLine()
						.iterator(); j.hasNext();) {
					Marker m = ObjectCopy.copyObject(j.next());
					ReflectionAccess ra = ReflectionAccess.accessForClass(m
							.getClass());
					if (ra.hasPropertyName("metricTime"))
						ra.setProperty(m, "metricTime", r
								.add(m.getMetricTime()));
					mtl.add(m);
				}
				Container<Marker> ht = p.getHarmonyTrack();
				for (Iterator<Marker> j = pieces[i].getHarmonyTrack()
						.iterator(); j.hasNext();) {
					Marker m = ObjectCopy.copyObject(j.next());
					ReflectionAccess ra = ReflectionAccess.accessForClass(m
							.getClass());
					if (ra.hasPropertyName("metricTime"))
						ra.setProperty(m, "metricTime", r
								.add(m.getMetricTime()));
					ht.add(m);
				}

				NotationSystem nsys_o = pieces[i].getScore();
				Rational systemEndTime = pieces[i].getMetricalTimeLine()
						.getNextOrSameMeasure(nsys_o.getEndTime());
				for (int j = 0; j < nsys_o.size(); j++) {
					NotationStaff nst_o = nsys_o.get(j);
					NotationStaff nst_n = score.get(j);
					if (nst_o.getClefTrack() != null) {
						for (Clef o : nst_o.getClefTrack()) {
							Clef c = ObjectCopy.copyObject(o);
							c.setMetricTime(r.add(c.getMetricTime()));
							nst_n.getClefTrack().add(c);
						}
					}
					for (int k = 0; k < nst_o.size(); k++) {
						NotationVoice nv_o = nst_o.get(k);
						Map<Note, Note> noteMap = new HashMap<Note, Note>();
						Rational voiceEndTime = nv_o.getEndTime();
						if (voiceEndTime.isLess(systemEndTime)) {
							ScoreNote sn = new ScoreNote(voiceEndTime,
								systemEndTime.sub(voiceEndTime), 'r', (byte) 0,
								(byte) 0);
							nv_o.add(new Note(sn, null));
						}
						NotationVoice nv_n = nst_n.get(k);
						for (int l = 0; l < nv_o.size(); l++) {
							NotationChord nc_o = nv_o.get(l);
							NotationChord nc_n = new NotationChord(p
									.getContext());
							for (int m = 0; m < nc_o.size(); m++) {
								Note note = nc_o.get(m);
								Note copy = ObjectCopy.copyObject(note);
								ScoreNote sn = copy.getScoreNote();
								sn.setMetricTime(r.add(sn.getMetricTime()));
								nc_n.add(copy);
								noteMap.put(note, copy);
							}
							nc_n.setMetricTime(nc_o.getMetricTime().add(r));
							nv_n.add(nc_n);
						}
						for (SlurContainer sc1 : nv_o.getSlurContainers()) {
							SlurContainer sc2 = new SlurContainer(nv_n
									.getContext());
							for (Iterator<Note> it = sc1.iterator(); it
									.hasNext();) {
								sc2.add(noteMap.get(it.next()));
							}
							nv_n.addSlurContainer(sc2);
						}
						for (TupletContainer tc1 : nv_o.getTupletContainers()) {
							TupletContainer tc2 = new TupletContainer(nv_n
									.getContext(), tc1.getArity());
							for (Iterator<Note> it = tc1.iterator(); it
									.hasNext();) {
								tc2.add(noteMap.get(it.next()));
							}
							nv_n.addTuplet(tc2);
						}

					}
				}
				if (score.get(0) != null && pieces[i].getName() != null) {
					System.out.println(pieces[i].getName());
					score.get(0).addAttachable(
						new MetricAttachable(r, new StringSymbol(pieces[i]
								.getName())));
				}
				r = score.getEndTime();
				for (Iterator<NotationStaff> j = score.iterator(); j.hasNext();) {
					NotationStaff nst = j.next();
					nst.addBarline(new Barline(r, true));
				}
				if (i < pieces.length - 1)
					score.addLinebreak(r);
			}
			score.createBeams();
			return p;
		}

		private int[] calculateBounds() {
			int staffs = 0;
			for (int i = 0; i < pieces.length; i++) {
				if (pieces[i].getScore() == null)
					pieces[i].generateScore();
				NotationSystem nsys = pieces[i].getScore();
				staffs = Math.max(staffs, nsys.size());
			}
			int[] size = new int[staffs];
			for (int i = 0; i < pieces.length; i++) {
				NotationSystem nsys = pieces[i].getScore();
				for (int j = 0; j < nsys.size(); j++)
					size[j] = Math.max(size[j], nsys.get(j)
							.size());
			}
			return size;
		}
	}
}
