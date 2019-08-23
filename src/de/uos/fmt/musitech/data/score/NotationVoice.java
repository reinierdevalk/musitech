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
package de.uos.fmt.musitech.data.score;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsContainer;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * Class containing one voice for Notation. A voice can contain other voices to
 * representing temporary additional voices (e. g. temporary second voice in a
 * piece for violin). Additional voices are only necessary, if the second voice
 * is rhythmically different. Chords can be used within one voice. Nested Voices
 * can also be used to change the color of the Notes within the Voice. TODO
 * introduce generic type parameter
 * 
 * @author Tillman Weyde
 * @version $Revision: 8542 $, $Date: 2010-02-02 18:04:08 +0000 (Tue, 02 Feb
 *          2010) $
 * @hibernate.class table = "NotationVoice"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 */
public class NotationVoice extends SortedContainer<NotationChord> implements
		NotationContainer, IMPEGSerializable {

	/**
	 * This variable indicates whether the barlines were already set. It is used
	 * by fillGaps() to make a sanity check.
	 */
	private boolean barlinesSet = false;

	private boolean gapsFilled = false;

	private List<BeamContainer> beamContainers = new ArrayList<BeamContainer>();

	private List<SlurContainer> slurContainers = new ArrayList<SlurContainer>();

	private List<TupletContainer> tupletContainers = new ArrayList<TupletContainer>();

	/**
     * 
     */
	private Color color = Color.BLACK;

	/**
	 * Gets the contextTimeLine.
	 * 
	 * @return Returns the contextTimeLine.
	 */
	public MetricalTimeLine getContextTimeLine() {
		if (contextTimeLine == null) {
			try {
				return context.getPiece().getMetricalTimeLine();
			} catch (NullPointerException e) {
			}
		}
		return contextTimeLine;
	}

	/**
	 * Sets the contextTimeLine.
	 * 
	 * @param argContextTimeLine The contextTimeLine to set.
	 */
	public void setContextTimeLine(MetricalTimeLine argContextTimeLine) {
		this.contextTimeLine = argContextTimeLine;
	}
	private MetricalTimeLine contextTimeLine;

	/**
	 * The pointer to the parent is needed to get access to the barlines which
	 * are saved in the NotationSystem
	 */
	private NotationStaff parent;

	/**
	 * This method prepares this NotationVoice to be displayed. It only works if
	 * a MeterTrack can be found in the associated Piece (if the MeterTrack is
	 * null, this method does simply nothing). If a MeterTrack can be found gaps
	 * in the voice are filled with rests, missing barlines are inserted and the
	 * Chords are normalized.
	 * 
	 * @param pass TODO
	 */
	public void prepareForScore(int pass) {
		if (getContext() == null && getParent() != null)
			setContext(getParent().getContext());
		if (getContext() == null)
			setContext(new Piece().getContext()); // TODO change when context
		// concept is finished.
		if (getContext().getPiece().getMetricalTimeLine() == null)
			return;
		// if (pass == 2) {
		// generateBeams();
		// } else {
		// System.err.println("clean: " + this);
		fillGaps();
		// System.err.println("gaps: " + this);
		// System.err.println("barline: " + this);
		normalizeChords();
		// System.err.println("normalized: " + this);

		Barline lastBarline = null;
		assert parent != null;
		if (parent.getBarlines() != null && parent.getBarlines().size() > 0) {
			lastBarline = (Barline) parent.getBarlines().get(
				parent.getBarlines().size() - 1);
		}

		NotationChord lastChord = null;
		if (size() > 0) {
			lastChord = (NotationChord) get(size() - 1);
		}
		// TODO check what this was meant to do
		// else {
		// if (lastChord != null)
		// // TODO what's the effect of this code ??
		// lastChord.size();
		// }

		if (lastBarline != null && lastChord != null
			&& lastBarline.getMetricTime().isLess(lastChord.getMetricTime())) {
			addBarline(new Barline(lastChord.getMetricTime().add(
				lastChord.getMetricDuration()), true));
		}
		// }
		processDynamics();

	} // end prepareNotes

	/**
	 * Constructor NotationVoice gets the work context. Does not add anything to
	 * the works structure.
	 * 
	 * @param context
	 * @param parent The
	 * @see de.uos.fmt.musitech.data.structure.NotationStaff in which this voice
	 *      is used TODO: Create right implementation of no-argument
	 *      NotationVoice constructor
	 */
	public NotationVoice() {
		super(new Context(), NotationChord.class, new MetricalComparator());
		// this.parent = parent;
	}

	/**
	 * Constructor NotationVoice gets the work context. Does not add anything to
	 * the works structure.
	 * 
	 * @param context
	 * @param parent The
	 * @see de.uos.fmt.musitech.data.structure.NotationStaff in which this voice
	 *      is used
	 */
	public NotationVoice(NotationStaff parent) {
		super(parent.getContext(), NotationChord.class,
				new MetricalComparator());
		this.parent = parent;
		if (!parent.contains(this))
			parent.add(this);
	}

//	/**
//	 * Constructor NotationVoice gets the work context and addToPool.
//	 * 
//	 * @param context The Context of this NotationVoice.
//	 * @param parent The
//	 * @see de.uos.fmt.musitech.data.structure.NotationStaff in which this voice
//	 *      is used
//	 * @param addToPool If true, the this NotationVoice is added to the Piece.
//	 */
//	public NotationVoice(Context context, NotationStaff parent,
//			boolean addToPool) {
//		super(context, NotationChord.class, new MetricalComparator());
//		this.parent = parent;
//	}

	/**
	 * This method returns the List cotaining the BeamContainers. An empty List
	 * may be returned, but the return value is never null.
	 * 
	 * @return the List containing the BeamContainers
	 * @see de.uos.fmt.musitech.data.score.BeamContainer
	 * @hibernate.bag name="BeamContainer" cascade="save-update" table =
	 *                "BeamObj_in_NotVoice"
	 * @hibernate.collection-key column="NotationVoice_id"
	 * @hibernate.collection-many-to-many class =
	 *                                    "de.uos.fmt.musitech.data.MObject"
	 *                                    column="MObject_id"
	 */
	public List getBeamContainers() {
		return beamContainers;
	}

	/**
	 * This method adds a BeamContainer. This does not change the notes in
	 * voice. All notes contained in the BeamContainer, have to be added
	 * manually into the voice.
	 * 
	 * @param bc The BeamContainer to be added
	 * @see de.uos.fmt.musitech.data.score.BeamContainer
	 */
	public void addBeamContainer(BeamContainer bc) {
		beamContainers.add(bc);
	}

	/**
	 * This method returns the index of beam container the given NotationChord
	 * belongs to. If the chord does not belong to any beam container -1 is
	 * returned.
	 * 
	 * @param chord the NotationChord
	 * @return the index of the beam container the chord belongs to, or -1
	 * @see NotationChord
	 * @see NotationVoice#getBeamContainers()
	 */
	public int belongsToBeam(NotationChord chord) {
		for (int i = 0; i < beamContainers.size(); i++) {
			for (int j = 0; j < chord.size(); j++) {
				Note note = (Note) chord.get(j);
				if (beamContainers.get(i).contains(note)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * This method returns the indices of the slur containers the given
	 * NotationChord belongs to. If the chord does not belong to any slur
	 * container an empty ArrayList is returned.
	 * 
	 * @param chord the NotationChord
	 * @return the indices of the slur container the chord belongs to. The
	 *         returned ArrayList will contain Integers
	 * @see NotationChord
	 * @see NotationVoice#getSlurContainers()
	 */
	public List<Integer> belongsToSlur(NotationChord chord) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < slurContainers.size(); i++) {
			if (slurContainers.get(i) instanceof SlurContainer && // TODO check
				// why this
				// is
				// neceseary
				// (perhaps
				// from
				// deserialisation)
				slurContainers.get(i).contains(chord.get(0))) {
				indices.add(i);
			}
		}
		return indices;
	}

	/**
	 * This method returns the list of containers the given NotationChord
	 * belongs to. If the chord does not belong to any slur container an empty
	 * ArrayList is returned.
	 * 
	 * @param chord the NotationChord
	 * @return the list of slur containers the chord belongs to. The returned
	 *         List will contain Containers
	 * @see NotationChord
	 * @see NotationVoice#getSlurContainers()
	 */
	public List<SlurContainer> belongsToSlurContainer(NotationChord chord) {
		ArrayList containers = new ArrayList();
		for (int i = 0; i < slurContainers.size(); i++) {
			if (slurContainers.get(i).contains(chord.get(0))) {
				containers.add(slurContainers.get(i));
			}
		}
		return containers;
	}

	/**
	 * This method checks whether the given NotationChod is the beginning of a
	 * slur.
	 * 
	 * @param chord the NotationChord
	 * @return true if the NotationChord is the beginning of a slur
	 */
	public boolean beginOfSlur(NotationChord chord) {
		List<SlurContainer> cont = belongsToSlurContainer(chord);
		for (int i = 0; i < cont.size(); i++) {
			SlurContainer sc = (SlurContainer) cont.get(i);
			boolean res = true;
			for (Iterator<Note> iter = sc.iterator(); iter.hasNext();) {
				Note n = iter.next();
				if (chord.getMetricTime().isGreater(n.getMetricTime())) {
					res = false;
					break;
				}
			}
			if (res == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method checks whether the given NotationChod is the beginning of a
	 * slur.
	 * 
	 * @param chord the NotationChord
	 * @return true if the NotationChord is the beginning of a slur
	 */
	public boolean endOfSlur(NotationChord chord) {
		List<SlurContainer> cont = belongsToSlurContainer(chord);
		for (int i = 0; i < cont.size(); i++) {
			SlurContainer sc = cont.get(i);
			boolean res = true;
			for (Iterator<Note> iter = sc.iterator(); iter.hasNext();) {
				Note n = iter.next();
				if (n.getMetricTime().isGreater(chord.getMetricTime())) {
					res = false;
					break;
				}
			}
			if (res == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method returns the List cotaining the SlurContainers. An empty List
	 * may be returned, but the return value is never null.
	 * 
	 * @return the List containing the SlurContainers
	 * @see de.uos.fmt.musitech.data.score.SlurContainer
	 * @hibernate.bag name="SlurContainer" cascade="save-update" table =
	 *                "SlurObj_in_NotVoice"
	 * @hibernate.collection-key column="NotationVoice_id"
	 * @hibernate.collection-many-to-many class =
	 *                                    "de.uos.fmt.musitech.data.MObject"
	 *                                    column="MObject_id"
	 */
	public List<SlurContainer> getSlurContainers() {
		return slurContainers;
	}

	/**
	 * This method adds a SlurContainer to the voice. This does not change
	 * anything about the notes in the voices. This means especially that the
	 * notes references inside the SlurContainer are not added to the voice.
	 * 
	 * @param sc the to be added SlurContainer
	 * @see de.uos.fmt.musitech.data.score.SlurContainer
	 */
	public void addSlurContainer(SlurContainer sc) {
		slurContainers.add(sc);
	}

	/**
	 * This method returns the Vectore containing the TupletContainers. An empty
	 * List may be returned, but the return value is never null.
	 * 
	 * @return the List containing the TupletContainers
	 * @see de.uos.fmt.musitech.data.score.TupletContainer
	 * @hibernate.bag name="TupletContainer" cascade="save-update" table =
	 *                "TupletObj_in_NotVoice"
	 * @hibernate.collection-key column="NotationVoice_id"
	 * @hibernate.collection-many-to-many class =
	 *                                    "de.uos.fmt.musitech.data.MObject"
	 *                                    column="MObject_id"
	 */
	public List<TupletContainer> getTupletContainers() {
		return tupletContainers;
	}

	/**
	 * TODO extend to work with tuplets containing notes of different durations. 
	 * 
	 * This method declares a tuplet. When the notes are to be added to the
	 * score, their onset will be modified, the duration is the unchanged. The
	 * resulting overlap is recognised by the removeOverlaps method as
	 * unproblematic. The onset of note i in the tuplet containing n notes is
	 * (tupletOnset + (i * (tupletDuration / n))). To get the duration of an
	 * individual Note, the passed duration is divided by the number of notes in
	 * the tuplet. The resulting note duration must be an inverse power of two.
	 * If it is not, an Error is thrown.
	 * 
	 * @param notes an array of notes which belong to one tuplet, must not contain empty slots. 
	 * @param metricDuration the duration of the whole tuplet
	 * @see de.uos.fmt.musitech.data.score.NotationVoice#getTupletContainers()
	 */

	public void addTuplet(Note[] notes, Rational metricDuration) {

		Rational noteDuration = notes[0].getScoreNote().getMetricDuration();
		// duration.div(new Rational(notes.length));//the duration of each
		// individual note in the tuplet
		if (!MyMath.isPowerOf2(noteDuration.getDenom())) {
			throw new Error("you can only create tuplets where the "
				+ "duration of an individual note has a negative power of 2");
		}
		TupletContainer tuplet = new TupletContainer(this.getContext(),
			(byte) notes.length);
		for (int i = 0; i < notes.length; i++) {
			tuplet.add(notes[i]);
		}
		tupletContainers.add(tuplet);
		tuplet.setMetricDuration(metricDuration);

		// we have to add here, because if we add earlier the removeOverlaps
		// check would not recognize that the note belongs to a tuplet and
		// would fail 
		for (int i = 0; i < notes.length; i++) {
			add(notes[i]);
		}
		tuplet.modifyOnsets();
	}

	/**
	 * This method adds the given TupletContainer to this voice. It also calls
	 * TupletContainer.modifyOnsets.
	 * 
	 * @param cont
	 */
	public void addTuplet(TupletContainer cont) {
		cont.modifyOnsets();
		tupletContainers.add(cont);
	}

	/**
	 * TODO Extend this work with tuplets where not all notes have have the same duraton. 
	 * 
	 * This method declares a tuplet. The notes are added to a Container and the 
	 * container is then added to the vector where it can be retrieved. When the  
	 * notes are added to the score, their onset, but not their duration, will be 
	 * modified. The onset of note i in the tuplet containing n notes is (tupletOnset + (i * (tupletDuration / n))).
	 * 
	 * @param notes an array of notes which belong to one tuplet
	 * @see de.uos.fmt.musitech.data.score.NotationVoice#getTupletContainers()
	 */
	public void addTuplet(Note[] notes) {
		addTuplet(notes, notes[0].getScoreNote().getMetricDuration().mul(notes.length));
	}

	/**
	 * Checks if a
	 * 
	 * @see de.uos.fmt.musitech.data.structure.NotationChord qualifies for being
	 *      under a beam.
	 * @param chord the
	 * @see de.uos.fmt.musitech.data.structure.NotationChord to be checked.
	 * @return true if the duration of the chord is 1/8 or lower
	 * @author collin
	 */
	private boolean isBeamable(NotationChord chord) {
		return chord.getMetricDuration().isLessOrEqual(new Rational(1, 8));
	}

	/**
	 * This method fills the beamContainer attribute of this class It goes
	 * linearly through the voice. If a beamable chord is encountered it is
	 * added to a BeamContainer. If a non-beamable chord or a barline is
	 * encountered the BeamContainer is added to the List if it contains more
	 * than one element.
	 * 
	 * @return the List containing the BeamContainers
	 * @see de.uos.fmt.musitech.data.score.NotationVoice#getBeamContainers()
	 * @see de.uos.fmt.musitech.data.score.NotationVoice#isBeamable(de.uos.fmt.musitech.data.structure.NotationChord)
	 * @see de.uos.fmt.musitech.data.score.BeamContainer
	 */
	public List generateBeams() {
		// assert barlinesSet == true: "You have to call insertBarlines() before
		// calling this method";
		// BeamContainer currentBeamContainer = new
		// BeamContainer(this.getContext());
		// for (Iterator iter = this.iterator(); iter.hasNext();) {
		// NotationChord chord = (NotationChord) iter.next();
		// if (belongsToBeam(chord) < 0 && isBeamable(chord) ) {
		// currentBeamContainer.add(chord);
		// }
		// if (!isBeamable(chord) || (chord.getBeamType() ==
		// NotationChord.BEAM_SPLIT)
		// || hasBarlineAt(chord.getMetricTime().add(chord.getMetricDuration()))
		// || !iter.hasNext() ) {
		// if (currentBeamContainer.size() > 1) {
		// beamContainers.add(currentBeamContainer);
		// }
		// currentBeamContainer = new BeamContainer(this.getContext());
		// }
		// }
		return beamContainers;
	}

	/**
	 * This method generates one SlurContainer for each slur in the voice. These
	 * Containers are all put into a List which is then returned. The List is
	 * also saved as an attribute which can be read by getSlurContainers in this
	 * class. The Algorithm assumes the following (I am not sure if this is true
	 * in the general case): for all NotationChord x,y in voice: exists Note a
	 * in x and exists Note b in y and slur(a,b) -> forall Note c in x, Note d
	 * in y: slur(c, d)
	 * 
	 * @deprecated THIS METHOD IS WRONG. SLURCONTAINERS ARE NOT EQUAL TO TIED
	 *             NOTES. SLURCONTAINERS CANNOT BE GENERATED! collin
	 * 
	 * @return a List of SlurContainers
	 * 
	 * @see de.uos.fmt.musitech.data.score.SlurContainer
	 * @see java.util.List
	 * @see de.uos.fmt.musitech.data.score.NotationVoice#getSlurContainers()
	 */
	public List generateSlurContainers() {
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof NotationChord) {
				int advanceChords = 0;
				NotationChord chord = (NotationChord) element;
				for (Iterator chordIter = chord.iterator(); chordIter.hasNext();) {
					SlurContainer slurContainer = new SlurContainer(this
							.getContext());
					Note note = (Note) chordIter.next();
					ScoreNote scoreNote = note.getScoreNote();
					while (scoreNote.getTiedNote() != null) {
						slurContainer.add(note);
						scoreNote = scoreNote.getTiedNote();
					}
					if (slurContainer.size() > 0) {
						slurContainer.add(note);
						slurContainers.add(slurContainer);
						advanceChords = slurContainer.size() - 1;
					}
				}
				for (int i = 0; i < advanceChords; i++) { // move the pointer
					// forward
					iter.next();
				}
			}
		}
		return slurContainers;
	}

	/**
	 * little helper function, so I don't have to type that much...
	 * 
	 * @author collin
	 * @param metricTime the metric time of the time signature we are interested
	 *            in
	 * @return the time signature
	 */
	private Rational getTimeSignature(Rational metricTime) {
		return getContext().getPiece().getMetricalTimeLine()
				.getTimeSignatureMarker(metricTime).getTimeSignature()
				.getMeasureDuration();
	}

	/**
	 * This method checks if a chord belongs to a tuplet.
	 * 
	 * @param de.uos.fmt.musitech.data.structure.NotationChord
	 * @return the index of the TupletContainer the note belongs to, or -1
	 * @see de.uos.fmt.musitech.data.structure.NotationChord
	 */
	public int belongsToTuplet(NotationChord chord) {
		if (chord == null)
			return -1;
		for (int i = 0; i < tupletContainers.size(); i++) {
			for (int j = 0; j < chord.size(); j++) {
				if (tupletContainers.get(i) instanceof TupletContainer && // TODO
					// check
					// why
					// this
					// isnecessary
					tupletContainers.get(i).contains((Note) chord.get(j))) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * This method checks if a chord belongs to a tuplet.
	 * 
	 * @param de.uos.fmt.musitech.data.structure.NotationChord
	 * @return the index of the TupletContainer the note belongs to, or null
	 * @author collin
	 */
	public TupletContainer belongsToTupletContainer(NotationChord chord) {
		if (chord == null)
			return null;
		for (int i = 0; i < tupletContainers.size(); i++) {
			boolean found = false;
			for (int j = 0; j < chord.size(); j++) {
				found |= ((TupletContainer) tupletContainers.get(i))
						.contains((Note) chord.get(j));
			}
			if (found) {
				// if (((TupletContainer)
				// tupletContainers.get(i)).contains((Note)
				// chord.get(0))) {
				return (TupletContainer) tupletContainers.get(i);
			}
		}
		return null;
	}

	/**
	 * This is just a convenience method. It calls
	 * 
	 * @see de.uos.fmt.musitech.data.score.NotationSystem#addBarline(de.uos.fmt.musitech.data.structure.Barline)
	 *      on the parent provided by the constructor
	 * @param barline The barline to be added
	 * @author collin
	 */
	void addBarline(Barline barline) {
		parent.addBarline(barline);
	}

	/**
	 * This is just a convenience method. It calls
	 * 
	 * @see de.uos.fmt.musitech.data.score.Barline#hasBarlineAt(de.uos.fmt.musitech.utility.Rational)
	 *      on the barlineConatiner aquiered through the parent
	 * @param metricTime a point in metric time
	 * @return true if there is a barline at the point in metric time
	 * @author collin
	 */
	boolean hasBarlineAt(Rational metricTime) {
		return parent.getBarlines().hasBarlineAt(metricTime);
	}

	/**
	 * This method returns the last barline in this voice
	 * 
	 * @return the last barline
	 * @author collin
	 */
	Barline getLastBarline() {
		return (Barline) parent.getBarlines().get(
			parent.getBarlines().size() - 1);
	}

	Collection getSiblings() {
		ArrayList sibs = new ArrayList();

		for (Iterator iter = ((NotationStaff) getParent()).iterator(); iter
				.hasNext();) {
			NotationVoice element = (NotationVoice) iter.next();
			if (element != this)
				sibs.add(element);
		}

		return sibs;
	}

	/**
	 * Normalises all NotationCords in this NotationVoice
	 * 
	 * @see NotationChord#normalizeChord()
	 */
	public void normalizeChords() {
		List<NotationChord> toBeRemoved = new ArrayList<NotationChord>();
		List<NotationChord> toBeAdded = new ArrayList<NotationChord>();

		// int j = 1;
		for (Iterator<NotationChord> iter = iterator(); iter.hasNext();) {
			NotationChord chord = iter.next();
			// System.err.print("in: " + chord.getSingleMetricDuration() + ":" +
			// chord.getMetricTime() + " " );
			NotationChord[] parts = chord.normalizeChord();
			// System.err.print("out: ");
			/*
			 * for (int i = 0; i < parts.length; i++)
			 * System.err.print(parts[i].getSingleMetricDuration() + ":" +
			 * parts[i].getMetricTime() + " "); System.err.println();
			 */
			if (parts.length > 1) {
				toBeRemoved.add(chord);
				for (int i = 0; i < parts.length; i++) {
					toBeAdded.add(parts[i]);
				}
			}
		}
		for (int i = 0; i < toBeRemoved.size(); i++)
			remove(toBeRemoved.get(i));
		for (int i = 0; i < toBeAdded.size(); i++) {
			add(toBeAdded.get(i));
		}
	}

	/**
	 * This method adds rests to the voice. A rest is added when there's a gap
	 * between to notes. This method may only be called if barlines are not yet
	 * set.
	 */
	public void fillGaps() {
		// assert barlinesSet == false: "You must call fillGaps before the
		// barlines are set!";

		NotationChord previousElement = null;
		List toAdd = new ArrayList();

		for (Iterator iter = this.iterator(); iter.hasNext();) {
			NotationChord element = (NotationChord) iter.next();
			if (previousElement != null) {
				Rational onset = previousElement.getMetricTime().add(
					previousElement.getMetricDuration());
				if (onset.isLess(element.getMetricTime())) {
					ScoreNote sNote = new ScoreNote(onset, element
							.getMetricTime().sub(onset), 'r', (byte) 0,
						(byte) 0);
					Note restNote = new Note(sNote, null);
					toAdd.add(restNote);
				}
			} else if (element.getMetricTime().isGreater(Rational.ZERO)) {
				// a gap at the beginning
				ScoreNote sNote = new ScoreNote(Rational.ZERO, element
						.getMetricTime(), 'r', (byte) 0, (byte) 0);
				Note restNote = new Note(sNote, null);
				toAdd.add(restNote);
			}
			previousElement = element;
		}

		for (Iterator iter = toAdd.iterator(); iter.hasNext();) {
			Note element = (Note) iter.next();
			this.add(element);
		}
		gapsFilled = true;
	}

	/**
	 * Quantizes the notes to the given values.
	 * 
	 * @param quantum The smallest divisor allowed for note positions and
	 *            durations.
	 */
	public void quantize(Rational quantum) {
		quantize(quantum, quantum);
	}

	/**
	 * Quantizes the notes to the given values.
	 * 
	 * @param onsetQuantum The smallest divisor allowed for note positions and
	 *            durations.
	 * @param restQuantum The smallest divisor allowed for rest times.
	 */
	public void quantize(Rational onsetQuantum, Rational restQuantum) {
		NotationChord nc[] = (NotationChord[]) toArray(new NotationChord[] {});
		for (int i = 0; i < nc.length; i++) {
		}
	}

	/**
	 * Describe <code>quantizeOnset</code> method here.
	 * 
	 * @param nc a <code>NotationChord</code> value
	 * @param quantum a <code>Rational</code> value
	 * @return a <code>boolean</code> value
	 */
	public boolean quantizeOnset(NotationChord nc, Rational quantum) {
		Rational onset = nc.getMetricTime();
		Rational remainder = onset.mod(quantum);
		Rational qOnset; // quatized onset
		if (!remainder.equals(Rational.ZERO)) {
			if (remainder.isGreater(quantum.div(new Rational(2))))
				qOnset = onset.add(quantum).sub(remainder);
			else
				qOnset = onset.sub(remainder);
			nc.setMetricTime(qOnset);
			return true;
		}
		return false;
	}

	/**
	 * Describe <code>quantizeDuration</code> method here.
	 * 
	 * @param nc a <code>NotationChord</code> value
	 * @param quantum a <code>Rational</code> value
	 * @return a <code>boolean</code> value
	 */
	public boolean quantizeDuration(NotationChord nc, Rational quantum) {
		Rational duration = nc.getMetricDuration();
		Rational remainder = duration.mod(quantum);
		Rational qDuration; // quatized duration
		if (!remainder.equals(Rational.ZERO)) {
			if (remainder.isGreater(quantum.div(new Rational(2))))
				qDuration = duration.add(quantum).sub(remainder);
			else
				qDuration = duration.sub(remainder);
			nc.setMetricTime(qDuration);
			return true;
		}
		return false;
	}

	/**
	 * Describe <code>quantizeOnset</code> method here.
	 * 
	 * @param n a <code>Note</code> value
	 * @param quantum a <code>Rational</code> value
	 * @return a <code>boolean</code> value
	 */
	public boolean quantizeOnset(Note n, Rational quantum) {
		Rational onset = n.getScoreNote().getMetricTime();
		Rational remainder = onset.mod(quantum);
		Rational qOnset; // quatized onset
		if (!remainder.equals(Rational.ZERO)) {
			if (remainder.isGreater(quantum.div(new Rational(2))))
				qOnset = onset.add(quantum).sub(remainder);
			else
				qOnset = onset.sub(remainder);
			n.getScoreNote().setMetricTime(qOnset);
			return true;
		}
		return false;
	}

	/**
	 * Describe <code>quantizeDuration</code> method here.
	 * 
	 * @param n a <code>Note</code> value
	 * @param quantum a <code>Rational</code> value
	 * @return a <code>boolean</code> value
	 */
	public boolean quantizeDuration(Note n, Rational quantum) {
		Rational duration = n.getScoreNote().getMetricDuration();
		Rational remainder = duration.mod(quantum);
		Rational qDuration; // quatized duration
		if (!remainder.equals(Rational.ZERO)) {
			if (remainder.isGreater(quantum.div(new Rational(2))))
				qDuration = duration.add(quantum).sub(remainder);
			else
				qDuration = duration.sub(remainder);
			n.getScoreNote().setMetricTime(qDuration);
			return true;
		}
		return false;
	}

	NotationVoice[] split() {
		List voices = new ArrayList();
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			NotationChord nc = (NotationChord) iter.next();

		}
		return null;
	}

	boolean doOverlap(NotationChord nc1, NotationChord nc2) {
		int comp = nc1.getMetricTime().compare(nc2.getMetricTime());
		switch (comp) {
		case 0:
			return true;
		case 1:
			if (nc1.getMetricTime().add(nc1.getMetricDuration()).isGreater(
				nc2.getMetricTime()))
				return true;
		case 2:
			if (nc2.getMetricTime().add(nc2.getMetricDuration()).isGreater(
				nc1.getMetricTime()))
				return true;
		}
		return false;
	}

	/**
	 * Returns the color.
	 * 
	 * @return Color
	 * @hibernate.property
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color.
	 * 
	 * @param color The color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	private HashMap<Rational, List<NotationChord>> entryChords = new HashMap<Rational, List<NotationChord>>();

	public boolean addNotes(Container<Note> nc) {
		boolean retVal = false;
		if (nc == null)
			return false;
		for (Note n : nc)
			retVal |= add(n);
		return retVal;
	}

	/**
	 * Takes the given ScoreNote wraps it into a new note that is returned and
	 * adds it at the end of the voice, overwriting the metric time of the
	 * ScoreNote. This method throws a NullPointerException if the context of
	 * the voice is not set or does not contain a piece with a MetricalTimeLine.
	 * 
	 * @param sc The ScoreNote to add, metrical time will be overwritten.
	 * @return The Note object in which the ScoreNote is wrapped, and which
	 */
	public Note appendAtEnd(ScoreNote sc) {
		sc.setMetricTime(getEndTime());
		Note n = new Note(sc, sc.toPerformanceNote(getContextTimeLine()));
		append(new NotationChord(context, n));
		return n;
	}

	public boolean add(Note n) {
		if (this.containsRecursive(n))
			return false;
		NotationChord nc = null;
		if (n.getMetricDuration().equals(Rational.ZERO)) {
			List<NotationChord> chords;
			if (entryChords.containsKey(n.getMetricTime())) {
				chords = entryChords.get(n.getMetricTime());
			} else {
				chords = new ArrayList<NotationChord>();
				entryChords.put(n.getMetricTime(), chords);
			}
			NotationChord chord = new NotationChord(context);
			chord.setTrueDuration((Rational) n.getRenderingHints().getValue(
				"appoggiatura"));
			chord.add(n);
			int ordinal = 0;
			if (n.getRenderingHints().containsKey("appoggiatura ordinal")) {
				ordinal = ((Integer) n.getRenderingHints().getValue(
					"appoggiatura ordinal")).intValue() - 1;
			}
			while (chords.size() <= ordinal)
				chords.add(null);
			chords.set(ordinal, chord);
		} else {
			nc = new NotationChord(context, n);
		}
		if (nc != null)
			nc.setRenderingHints(n.getRenderingHints());
		if (nc != null) {
			if (entryChords.containsKey(nc.getMetricTime())) {
				List<NotationChord> chords = entryChords
						.get(nc.getMetricTime());
				for (int i = 0; i < chords.size(); i++) {
					nc.addAppoggiatura(chords.get(i), i + 1);
				}
			}
			boolean result = false, done = false;
			for (int i = 0; i < size(); i++) {
				Object elem = get(i);
				if (!(elem instanceof NotationChord))
					continue;
				NotationChord ncTmp = (NotationChord) elem;
				if (nc.getMetricTime().equals(ncTmp.getMetricTime())
					&& nc.getMetricDuration().equals(ncTmp.getMetricDuration())) {
					result = ncTmp.addAll(nc);
					done = true;
				}
			}
			if (!done)
				result = super.add(nc);
			/*
			 * TODO: put that back in... there is a bug in there regarding
			 * tuplets removeOverlaps(indexOf(nc) - 1, indexOf(nc) + 1);
			 */

			return result;
		} else
			return false;

	}

	/**
	 * Describe <code>add</code> method here.
	 * 
	 * @param o an <code>Object</code> value
	 * @return a <code>boolean</code> value
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(NotationChord nc) {
		if (nc != null) {
			if (entryChords.containsKey(nc.getMetricTime())) {
				List<NotationChord> chords = entryChords
						.get(nc.getMetricTime());
				for (int i = 0; i < chords.size(); i++) {
					nc.addAppoggiatura(chords.get(i), i + 1);
				}
			}
			boolean result = false, done = false;
			for (int i = 0; i < size(); i++) {
				Object elem = get(i);
				if (!(elem instanceof NotationChord))
					continue;
				NotationChord ncTmp = (NotationChord) elem;
				if (nc.getMetricTime().equals(ncTmp.getMetricTime())
					&& nc.getMetricDuration().equals(ncTmp.getMetricDuration())) {
					result = ncTmp.addAll(nc);
					done = true;
				}
			}
			if (!done)
				result = super.add(nc);

			/*
			 * TODO: put that back in... there is a bug in there regarding
			 * tuplets removeOverlaps(indexOf(nc) - 1, indexOf(nc) + 1);
			 */

			return result;
		} else
			return false;

	}

	/**
	 * Describe <code>add</code> method here.
	 * 
	 * @param o an <code>Object</code> value
	 * @return a <code>boolean</code> value
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Barline argBL) {
		return getParent().addBarline(argBL);
	}

	/**
	 * Describe <code>add</code> method here.
	 * 
	 * @param o an <code>Object</code> value
	 * @return a <code>boolean</code> value
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Containable o) {
		NotationChord nc = null;
		if (o instanceof ScoreNote) {
			o = new Note((ScoreNote)o);
		}
		if (o instanceof Note) {
			Note n = (Note) o;
			if (n.getMetricDuration().equals(Rational.ZERO)) {
				List<NotationChord> chords;
				if (entryChords.containsKey(n.getMetricTime())) {
					chords = entryChords.get(n.getMetricTime());
				} else {
					chords = new ArrayList<NotationChord>();
					entryChords.put(n.getMetricTime(), chords);
				}
				NotationChord chord = new NotationChord(context);
				chord.setTrueDuration((Rational) n.getRenderingHints()
						.getValue("appoggiatura"));
				chord.add(n);
				int ordinal = 0;
				if (n.getRenderingHints().containsKey("appoggiatura ordinal")) {
					ordinal = ((Integer) n.getRenderingHints().getValue(
						"appoggiatura ordinal")).intValue() - 1;
				}
				while (chords.size() <= ordinal)
					chords.add(null);
				chords.set(ordinal, chord);
			} else {
				nc = new NotationChord(context, (Note) o);
			}
			if (nc != null)
				nc.setRenderingHints(n.getRenderingHints());
		} else if (o instanceof Barline) {
			// super.add(o);
			parent.addBarline((Barline) o);
			return true;
		} else if (o instanceof NotationChord) {
			nc = (NotationChord) o;
		}
		if (nc != null) {
			if (entryChords.containsKey(nc.getMetricTime())) {
				List<NotationChord> chords = entryChords
						.get(nc.getMetricTime());
				for (int i = 0; i < chords.size(); i++) {
					nc.addAppoggiatura(chords.get(i), i + 1);
				}
			}
			boolean result = false, done = false;
			for (int i = 0; i < size(); i++) {
				Object elem = get(i);
				if (!(elem instanceof NotationChord))
					continue;
				NotationChord ncTmp = (NotationChord) elem;
				if (nc.getMetricTime().equals(ncTmp.getMetricTime())
					&& nc.getMetricDuration().equals(ncTmp.getMetricDuration())) {
					result = ncTmp.addAll(nc);
					done = true;
				}
			}
			if (!done)
				result = super.add(nc);

			/*
			 * TODO: put that back in... there is a bug in there regarding
			 * tuplets removeOverlaps(indexOf(nc) - 1, indexOf(nc) + 1);
			 */

			return result;
		} else
			return false;

	}

	/**
	 * Describe <code>removeOverlaps</code> method here.
	 */
	public void removeOverlaps() {
		removeOverlaps(0, size() - 1);
	}

	/**
	 * Describe <code>removeOverlaps</code> method here.
	 * 
	 * @param start an <code>int</code> value
	 * @param end an <code>int</code> value
	 */
	public void removeOverlaps(int start, int end) {
		if (start < 0)
			start = 0;
		if (end >= size())
			end = size() - 1;
		// NotationChord nc[] = (NotationChord[]) toArray(new NotationChord[]
		// {});
		// for (int i = start; i < end; i++) {
		// if (nc[i].getMetricTime().add(nc[i].getDuration()).isGreater(nc[i +
		// 1].getMetricTime()))
		// nc[i].setDuration(nc[i +
		// 1].getMetricTime().sub(nc[i].getMetricTime()));
		// }
		RANGE: for (int i = start; i < end; i++) {
			if ((get(i) instanceof NotationChord)
				&& (get(i + 1) instanceof NotationChord)) {

				NotationChord left = (NotationChord) get(i);
				NotationChord right = (NotationChord) get(i + 1);

				if (belongsToTuplet(left) < 0
					&& left.getMetricTime().add(left.getMetricDuration())
							.isGreater(right.getMetricTime())) {
					// System.err.println(left.getMetricTime() + " + " +
					// left.getMetricDuration() + " => " +
					// right.getMetricTime());
					left.setMetricDuration(right.getMetricTime().sub(
						left.getMetricTime()));
				}
			}
		}
	}

	public boolean containsMetricTime(Rational metricTime) {
		if (size() == 0) {
			return false;
		}

		NotationChord firstChord = (NotationChord) get(0);
		NotationChord lastChord = (NotationChord) get(size() - 1);

		if (firstChord.getMetricTime().isLessOrEqual(metricTime)
			&& lastChord.getMetricTime().add(lastChord.getMetricDuration())
					.isGreaterOrEqual(metricTime)) {
			return true;
		}

		return false;
	}

	public String toString() {
		String s = "";

		for (Iterator iter = iterator(); iter.hasNext();) {
			NotationChord element = (NotationChord) iter.next();
			s += element.getSingleMetricDuration() + " ";
		}

		return s;
	}

	/**
	 * @param beamContainers The beamContainers to set.
	 */
	public void setBeamContainers(List beamContainers) {
		this.beamContainers = beamContainers;
	}

	/**
	 * @param slurContainers The slurContainers to set.
	 */
	public void setSlurContainers(List slurContainers) {
		this.slurContainers = slurContainers;
	}

	/**
	 * @param tupletContainers The tupletContainers to set.
	 */
	public void setTupletContainers(List tupletContainers) {
		this.tupletContainers = tupletContainers;
	}

	/**
	 * @return Returns the parent.
	 * @hibernate.many-to-one class =
	 *                        "de.uos.fmt.musitech.data.score.NotationStaff"
	 *                        foreign-key = "uid" cascade = "all"
	 */
	public NotationStaff getParent() {
		return parent;
	}

	/**
	 * @param argParent The parent to set.
	 */
	public void setParent(NotationStaff argParent) {
		this.parent = argParent;
		if (argParent != null)
			this.context = argParent.getContext();
	}

	public void prettyPrint(int indent) {
		for (int i = 0; i < indent; i++)
			System.out.print(" ");
		System.out.println("NotationVoice");
		for (int i = 0; i < indent + 2; i++)
			System.out.print(" ");
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			NotationChord element = (NotationChord) iter.next();
			element.prettyPrint();
			System.out.print(" ");
		}
		System.out.println("");
	}

	/**
	 * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
	 *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
	 */
	public boolean toMPEG(MusiteXMLSerializer instance, Node parent,
							Object object, String fieldname) {
		// commons----------------------------------
		// create element representing this object
		Element voice = XMLHelper.addElement(parent, "voice");
		// check for reference
		if (instance.knowsObject(voice, object))
			return true;
		// metadata
		MPEG_SMR_Tools.serializeMetaData(instance, voice, this);
		// rendering hints
		instance.writeXML(voice, getRenderingHints(), null, null);
		// -----------------------------------------

		Element noteObjects = XMLHelper.addElement(voice, "noteObjects");

		// variables for calculation of beam grouping type
		int lastBeamIndex = -1;
		NotationChord lastBeamObject = null;
		// iterate over noteObjects to calculate beams
		for (Iterator iter = getContent().iterator(); iter.hasNext();) {
			NotationChord noteObject = (NotationChord) iter.next();
			int beamIndex = belongsToBeam(noteObject);
			if (beamIndex >= 0) {
				if (lastBeamIndex != beamIndex) {
					// first note in beam => serialize beam rendering hints (as
					// hints in chord)
					RenderingHints rhints = ((BeamContainer) beamContainers
							.get(beamIndex)).getRenderingHints();
					if (rhints != null)
						for (Iterator iterator = rhints.getKeySet().iterator(); iterator
								.hasNext();) {
							String key = (String) iterator.next();
							noteObject.addRenderingHint("beam." + key, rhints
									.getValue(key));
						}
					// last chord is not a beam or the last chord in beam =>
					// ignore for beam-attribute
				} else {
					// last chord belongs to same beam => force grouping for
					// last chord
					lastBeamObject.setBeamType(NotationChord.BEAM_FORCE);
				}
			}
			lastBeamIndex = beamIndex;
			lastBeamObject = noteObject;
		}

		List<Integer> previousSlurIndices = new ArrayList();

		// iterate over noteObjects to serialize
		for (Iterator iter = getContent().iterator(); iter.hasNext();) {
			NotationChord noteObject = (NotationChord) iter.next();

			/*
			 * serialize entryChords
			 */
			NotationChord[] entryChords = noteObject.getEntryChord();
			if (entryChords != null)
				for (int i = 0; i < entryChords.length; i++) {
					NotationChord entryChord = entryChords[i];
					entryChord.setIsEntryChord(true);
					instance.setParent(entryChord, this);
					instance.writeXML(noteObjects, entryChord, null, null);
				}

			/*
			 * serialize slur
			 */
			List<Integer> slurIndices = this.belongsToSlur(noteObject);
			for (Integer slurIndex : slurIndices) {
				if (!previousSlurIndices.contains(slurIndex)) {
					int numSlurs = -1;
					for (Iterator contentIter = getContent().iterator(); contentIter
							.hasNext();) {
						NotationChord nChord = (NotationChord) contentIter
								.next();
						List<Integer> slurIndices2 = this.belongsToSlur(nChord);
						for (Iterator slurIter2 = slurIndices2.iterator(); slurIter2
								.hasNext();) {
							Integer slurIndex2 = (Integer) slurIter2.next();
							if (slurIndex2.equals(slurIndex)) {
								numSlurs++;
							}
						}
					}
					noteObject.setNumSlurNotes(numSlurs);
				}
			}

			previousSlurIndices = slurIndices;

			// remember parent and serialize chord/rest
			instance.setParent(noteObject, this);
			instance.writeXML(noteObjects, noteObject, null, null);
		}
		return true;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(org.w3c.dom.Element,
	 *      java.util.Hashtable, java.lang.Object)
	 */
	public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
		// commons----------------------------------
		// reference-handling
		Object reference = instance.getReferenced(node, this);
		if (reference != null)
			return reference;
		setContext(instance.getContext());
		// metadata
		MPEG_SMR_Tools.deserializeMetaData(instance, node, this);
		// rendering hints
		this.setRenderingHints(MPEG_SMR_Tools.deserializeRenderingHints(
			instance, node));
		// -----------------------------------------

		// voice onset corresponds to onset of the container-staff;
		NotationStaff parentStaff = (NotationStaff) instance.getParent(node);
		this.setParent(parentStaff);
		instance.setNextOnset(this, instance.getNextOnset(parentStaff));
		if (DebugState.DEBUG_MPEG)
			System.out.println("Voice onset: " + instance.getNextOnset(this));

		Element noteObjects = (Element) node
				.getElementsByTagName("noteObjects").item(0);
		NodeList noteObjectList = noteObjects.getChildNodes();
		// loop over chord/rests
		boolean lastForcedBeam = false;
		NotationChord lastChord = null;
		BeamContainer beamCont = null;

		SlurContainer slur = null;
		int slurNoteCounter = 0;
		ArrayList entryChords = null;
		for (int iNObj = 0; iNObj < noteObjectList.getLength(); iNObj++) {

			Node childNode = noteObjectList.item(iNObj);
			if (childNode instanceof Element) {
				Element noteObject = (Element) childNode;
				instance.setParent(noteObject, this);

				/*
				 * deserialize noteObject
				 */
				if (noteObject.getNodeName().equals("chord")
					|| noteObject.getNodeName().equals("rest")) {
					NotationChord chord = (NotationChord) instance.readXML(
						noteObject, NotationChord.class);
					assert (chord.size() > 0);
					// check whether EntryChord or normal chord/rest
					if (chord.isEntryChord()) {
						// add to EntryChords
						if (entryChords == null) {
							entryChords = new ArrayList();
						}
						entryChords.add(chord);
					} else {
						// add to EntryChords to NotationChord
						if (entryChords != null) {
							NotationChord[] entryChordArr = (NotationChord[]) entryChords
									.toArray(new NotationChord[entryChords
											.size()]);
							chord.setEntryChord(entryChordArr);
							entryChords = null;
						}
						// deserialize tuplet information
						Rational trueDuration = MPEG_SMR_Tools
								.readTupletInformation(XMLHelper.getElement(
									noteObject, "duration"), this, chord);

						// add to NotationVoice
						this.add(chord);

						/*
						 * calculate onset for next chord/rest
						 */
						// Rational onsetNextChord =
						// instance.getNextOnset(this).add(chord.getMetricDuration());
						Rational onsetNextChord = instance.getNextOnset(this)
								.add(trueDuration);
						instance.setNextOnset(this, onsetNextChord);
					}

					/*
					 * beam grouping
					 */
					boolean forceBeam = (chord.getBeamType() == NotationChord.BEAM_FORCE);
					Note beamNote = chord.getUppermostNote(); // Note to add to
					// BeamContainer
					if (forceBeam && (forceBeam != lastForcedBeam)) {
						// first note/chord forcing beam
						beamCont = new BeamContainer();
						// get renderingHints for this beam from NotationChord
						RenderingHints allChordHints = chord
								.getRenderingHints();
						if (allChordHints != null)
							for (Iterator iter = allChordHints.getKeySet()
									.iterator(); iter.hasNext();) {
								String key = (String) iter.next();
								if (key.startsWith("beam.")) {
									beamCont.addRenderingHint(key.substring(5),
										allChordHints.getValue(key));
									if (DebugState.DEBUG_MPEG)
										System.out
												.println("add beam RenderingHint: "
															+ key.substring(5));
								}
							}
						this.addBeamContainer(beamCont);
						beamCont.add(beamNote);
					}
					if (lastForcedBeam) {
						// middle and end of beam
						beamCont.add(beamNote);
					}
					// remember for next loop
					lastForcedBeam = forceBeam;
					lastChord = chord;

					/*
					 * slur
					 */
					// TODO gibt es ein Problem, wenn der Slur ueber EntryChords
					// geht???
					if (slurNoteCounter > 0) {
						// add current note to current slur
						slur.add(chord.getUppermostNote());
						slurNoteCounter--;
						if (slurNoteCounter == 0) {
							// all slur-notes have been assign to slurcontainer
							// and
							// container can be written
							this.addSlurContainer(slur);
						}
					}
					Element slurElement = MPEG_SMR_Tools
							.getSlurElement(noteObject);
					if (slurElement != null) {
						// new Slur
						slur = new SlurContainer();
						slur.add(chord.getUppermostNote());
						// set the counter to the number of following notes that
						// belong to the slur
						// TODO test this
						slurNoteCounter = 1;
						Element basic = XMLHelper
								.getNextElementSibling(slurElement);
						if (basic != null) {
							String rangeString = basic
									.getAttribute("noteRange");
							if (rangeString != null) {
								try {
									slurNoteCounter = Integer
											.parseInt(rangeString);
								} catch (Exception e) {
									if (DebugState.DEBUG)
										e.printStackTrace();
								}
							}
						}
						// slurNoteCounter = Integer.parseInt(slurElement
						// .getAttribute("noteNum"));
					}

					/*
					 * tied
					 */
					/*
					 * if (MPEG_SMR_Tools.isSlurBegin(chord)) { slur = new
					 * SlurContainer(); } if (slur != null) { // TODO ??? which
					 * note in the chord should be added to // SlurContainer?
					 * slur.add(); } if (MPEG_SMR_Tools.isSlurEnd(chord)) { slur
					 * = null; }
					 */
				} else if (noteObject.getNodeName().equals("clefSign")) {
					Clef clef = (Clef) instance.readXML(noteObject, Clef.class);
					clef.setMetricTime(instance.getNextOnset(this));
					MPEG_SMR_Tools.removeSameTypeAndTime(clef, parentStaff
							.getClefTrack());
					parentStaff.addClef(clef);
				} else if (noteObject.getNodeName().equals("timeSign")) {
					TimeSignature timeSignature = MPEG_SMR_Tools
							.getTimeSignature(noteObject.getAttribute("time"));
					TimeSignatureMarker tsm = new TimeSignatureMarker(
						timeSignature, instance.getNextOnset(this));
					MPEG_SMR_Tools.removeSameTypeAndTime(tsm, getContext()
							.getPiece().getMetricalTimeLine());
					getContext().getPiece().getMetricalTimeLine().add(tsm);
				} else if (noteObject.getNodeName().equals("keySign")) {
					KeyMarker keyMarker = new KeyMarker();
					keyMarker.setMetricTime(instance.getNextOnset(this));
					try {
						keyMarker.setAccidentalNum(Integer.parseInt(noteObject
								.getAttribute("fifths")));
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
					MPEG_SMR_Tools.removeSameTypeAndTime(keyMarker,
						getContext().getPiece().getHarmonyTrack());
					getContext().getPiece().getHarmonyTrack().add(keyMarker);
				} else {
					// TODO deserialize other noteobjects
				}
			}
		}
		generateBeams();
		return this;
	}

	/**
	 * just for mpeg
	 */
	private void generateTiedNotes() {
		Set tieBegin = new HashSet();
		for (Iterator iter = getContent().iterator(); iter.hasNext();) {
			NotationChord chord = (NotationChord) iter.next();
			for (Iterator iter2 = chord.getContent().iterator(); iter2
					.hasNext();) {
				Note note = (Note) iter2.next();
				ScoreNote scoreNote = note.getScoreNote();
				if (scoreNote.firstInTie) {
					tieBegin.add(scoreNote);
				} else {
					for (Iterator iter3 = tieBegin.iterator(); iter3.hasNext();) {
						ScoreNote begin = (ScoreNote) iter3.next();
						if (begin.equals(scoreNote)) {
							begin.setTiedNote(scoreNote);
							tieBegin.remove(begin);
							if (!scoreNote.lastInTie) {
								tieBegin.add(scoreNote);
							}
						}
					}
				}
			}
		}
	}

	public Rational getEndTime() {
		// if (this.size() <= 0)
		// return null;
		Rational endTime = Rational.ZERO;
		for (Iterator iter = this.getContent().iterator(); iter.hasNext();) {
			NotationChord chord = (NotationChord) iter.next();
			Rational chordEndTime = chord.getMetricTime().add(
				chord.getMetricDuration());
			if (chordEndTime.isGreater(endTime))
				endTime = chordEndTime;
		}
		return endTime;
	}
	/*
	 * Contains the Lyrics belonging to this voice
	 */
	private LyricsContainer lyrics;

	public LyricsContainer getLyrics() {
		return lyrics;
	}

	public void setLyrics(LyricsContainer lyrics) {
		this.lyrics = lyrics;
	}

	/**
	 * @return
	 */
	public Rational getMetricTime() {
		Rational start = null;
		for (Iterator iter = getContent().iterator(); iter.hasNext();) {
			NotationChord chord = (NotationChord) iter.next();
			Rational chordStart = chord.getMetricTime();
			if (start == null)
				start = chordStart;
			else if (chordStart.isLess(start))
				start = chordStart;
		}
		return start;
	}

	public void addVoiceSegment(NotationVoice voice) {
		// merge NotationChords
		this.addAll(voice);
		// merge lyrics
		if (getLyrics() != null)
			getLyrics().addLyricsSegment(voice.getLyrics());
		else
			setLyrics(voice.getLyrics());
		// merge slurs
		if (getSlurContainers() != null)
			getSlurContainers().addAll(voice.getSlurContainers());
		else
			setSlurContainers(voice.getSlurContainers());
		// merge beam
		if (getBeamContainers() != null)
			getBeamContainers().addAll(voice.getBeamContainers());
		else
			setBeamContainers(voice.getBeamContainers());
	}

	public void processDynamics() {
		Container ctl = getContextTimeLine();
		if (ctl == null)
			return;

		for (Iterator iter = ctl.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof DynamicsLevelMarker) {
				DynamicsLevelMarker dynMark = (DynamicsLevelMarker) obj;
				MetricAttachable ma = new MetricAttachable(dynMark
						.getMetricTime(), new CharSymbol(dynMark
						.getMusicGlyph()));
				ma.setRelativePosition(MetricAttachable.SOUTH);
				ma.setAlignment(MetricAttachable.CENTER);
				ma.setDistance(2);
				ma.setGenerated(true);
				ma.setRenderingHints(dynMark.getRenderingHints());
				getParent().addAttachable(ma);
			}
			// if (obj instanceof DynamicsMarker) {
			// DynamicsMarker dynMark = (DynamicsMarker) obj;
			// MetricAttachable ma = new DualMetricAttachable(dynMark, new
			// CharSymbol(dynMark.getMusicGlyph()));
			// ma.setRelativePosition(MetricAttachable.SOUTH);
			// ma.setAlignment(MetricAttachable.CENTER);
			// ma.setDistance(0);
			// ma.setRenderingHints(dynMark.getRenderingHints());
			// getParent().addAttachable(ma);
			// }
		}
	}

	public void processHarmony() {
		Container harmoContext = getMergedHarmonyContext();

		for (Iterator iter = harmoContext.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof DynamicsLevelMarker) {
				DynamicsLevelMarker dynMark = (DynamicsLevelMarker) obj;
				MetricAttachable ma = new MetricAttachable(dynMark,
					new CharSymbol(dynMark.getMusicGlyph()));
				ma.setRelativePosition(MetricAttachable.SOUTH);
				ma.setAlignment(MetricAttachable.CENTER);
				ma.setDistance(0);
				ma.setRenderingHints(dynMark.getRenderingHints());
				getParent().addAttachable(ma);
			}
		}
	}

	/**
	 * Gets the merged content of this voices conteline, the staffs context line
	 * and the harmony track.
	 */
	public Container getMergedHarmonyContext() {
		Container cont = new SortedContainer(context, Marker.class,
			new MetricalComparator());
		if (getContextTimeLine() != null)
			cont.addAll(getContextTimeLine());
		if (getParent().getContextTimeLine() != null)
			cont.addAll(getParent().getContextTimeLine());
		cont.addAll(getContext().getPiece().getHarmonyTrack());
		return cont;
	}

	/**
	 * Creates beams for this voice. Old beams are deleted.
	 */
	public void createBeams() {
		BeamGenerator bg = new BeamGenerator(this);
		bg.generateBeams();
	}

	private class BeamGenerator {

		private NotationVoice voice;
		private int notePoolPosition;
		private Rational position;
		private List<Note> notePool;

		private BeamGenerator(NotationVoice voice) {
			this.voice = voice;
			this.notePool = new ArrayList<Note>(voice.size());
			for (Iterator i = voice.iterator(); i.hasNext();) {
				NotationChord chord = (NotationChord) i.next();
				if (chord.size() > 0)
					notePool.add(chord.getUppermostNote());
			}
			this.notePoolPosition = 0;
			this.position = Rational.ZERO;
		}

		public void generateBeams() {
			removeOldBeamcontainer();

			Measure m = nextMeasure();
			while (m != null) {
				if (m.notes.size() > 1) {
					int position = 0;
					while (position < m.notes.size()) {
						int startweight = 0;
						int minweight = 0;
						if (hasHook(m.notes.get(position))
							&& m.notes.get(position).getScoreNote()
									.getDiatonic() != 'r') {
							BeamContainer bc = new BeamContainer(voice
									.getContext());
							bc.add(m.notes.get(position));
							startweight = m.weights.get(m.notes.get(position)
									.getMetricTime());
							minweight = startweight;
							position++;
							while (position < m.notes.size()) {
								Note before = m.notes.get(position - 1);
								Note actual = m.notes.get(position);
								minweight = Math.min(minweight, m.weights
										.get(actual.getMetricTime()));
								position++;
								if (actual.getMetricTime().isGreater(
									before.getMetricTime().add(
										before.getMetricDuration()))) {
									position--;
									break; // Nicht notierte Pause
								} else if (actual.getScoreNote().getDiatonic() == 'r') {
									// System.out.println("Fall2");
									break; // Note ist Pause
								} else if (!hasHook(actual)) {
									// System.out.println("Fall3");
									break; // Note hat kein FŠhnchen
								} else if (m.weights
										.get(actual.getMetricTime()) >= startweight
											&& !actual.getMetricTime().equals(
												before.getMetricTime())) {
									position--;
									break; // Note soll nicht grš§er als
									// Anfangsnoten des Balkens sein
								} else if (m.weights
										.get(actual.getMetricTime()) >= minweight + 2) {
									position--;
									break;
								}
								bc.add(actual);
							}
							if (bc.size() > 1)
								voice.addBeamContainer(bc);
						} else
							position++;
					}
				}
				m = nextMeasure();
			}
		}

		private void removeOldBeamcontainer() {
			voice.getBeamContainers().clear();
		}

		private boolean hasHook(Note n) {
			return n.getMetricDuration().isLess(new Rational(1, 4));
		}

		private Measure nextMeasure() {
			if (notePoolPosition >= notePool.size())
				return null;
			Measure m = new Measure();
			m.start = position;
			m.timeSignature = getTimeSignatureAt(position);
			m.stop = position.add(m.timeSignature.getMeasureDuration());
			m.notes = new ArrayList<Note>();
			while (notePoolPosition < notePool.size()
					&& notePool.get(notePoolPosition).getMetricTime().isLess(
						m.stop)) {
				// if (((Note) notePool.get(notePoolPosition)).getScoreNote()
				// .getMetricTime() == null)
				// ((Note) notePool.get(notePoolPosition)).getScoreNote()
				// .setMetricTime(Rational.ZERO);
				m.notes.add(notePool.get(notePoolPosition));
				notePoolPosition++;
			}
			m.calculateWeights();
			position = m.stop;
			return m;
		}

		private TimeSignature getTimeSignatureAt(Rational position) {
			MetricalTimeLine mtl = voice.getContext().getPiece()
					.getMetricalTimeLine();
			TimeSignatureMarker lastTsm = null;
			for (Iterator i = mtl.iterator(); i.hasNext();) {
				Object o = i.next();
				if (o instanceof TimeSignatureMarker) {
					TimeSignatureMarker tsm = (TimeSignatureMarker) o;
					if (tsm.getMetricTime().isLessOrEqual(position))
						lastTsm = tsm;
					else
						break;
				}
			}
			if (lastTsm == null)
				return new TimeSignature(4, 4);
			else
				return lastTsm.getTimeSignature();
		}
	}

	private class Measure {

		public Rational start, stop;
		public TimeSignature timeSignature;
		public List<Note> notes;
		public Map<Rational, Integer> weights;

		public void calculateWeights() {
			weights = new TreeMap<Rational, Integer>();
			int biggestDenominator = timeSignature.getDenominator();
			for (Note n : notes)
				biggestDenominator = Math.max(biggestDenominator, n
						.getMetricTime().getDenom());
			Rational gridSize = new Rational(1, biggestDenominator);
			int[] pf = primeFactorization(timeSignature.getNumerator());
			while (gridSize.isLessOrEqual(timeSignature.getMeasureDuration())) {
				for (Rational i = start; i.isLess(stop); i = i.add(gridSize)) {
					if (weights.containsKey(i))
						weights.put(i, weights.get(i) + 1);
					else
						weights.put(i, 1);
				}
				if (gridSize.getDenom() <= timeSignature.getDenominator()
					&& pf[1] > 0) {
					gridSize = gridSize.mul(3);
					pf[1]--;
				} else {
					gridSize = gridSize.mul(2);
					pf[0]--;
				}
			}
		}

		private int[] primeFactorization(int numerator) {
			// numerator = 2^m * 3^n
			int m = 0;
			int n = 0;
			while (numerator % 2 == 0) {
				m++;
				numerator /= 2;
			}
			while (numerator % 3 == 0) {
				n++;
				numerator /= 3;
			}
			return new int[] {m, n};
		}

		@Override
		public String toString() {
			String toString = "Measure start: " + start + "; stop: " + stop
								+ "; Notes(" + notes.size() + "): ";
			for (Note n : notes) {
				toString += n.getScoreNote().toString();
				if (weights != null)
					toString += " weight=" + weights.get(n.getMetricTime())
								+ " --- ";
			}
			return toString;
		}
	}

	public boolean beginOfSlur(Note note) {
		int pos = find(note.getMetricTime());
		NotationChord nchord = get(pos);
		return beginOfSlur(nchord);
	}
}
