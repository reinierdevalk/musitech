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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uos.fmt.musitech.data.rendering.RenderingSupported;
import de.uos.fmt.musitech.data.score.mpeg.MPEGContextLine;
import de.uos.fmt.musitech.data.score.mpeg.MPEGSystem;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.StaffContainer;
import de.uos.fmt.musitech.data.structure.linear.Voice;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.collection.SortedCollection;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * Container for one system in notation. Can contains multiple NotationStaff
 * objects, containing NotationVoices.
 * 
 * @author Tillman Weyde
 * @version $Revision: 8542 $, $Date: 2008-07-25 00:35:08 +0200 (Fr, 25 Jul
 *          2008) $
 * @hibernate.class table="NotationSystem"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 */
public class NotationSystem extends BasicContainer<NotationStaff> implements
		NotationContainer, RenderingSupported, IMPEGSerializable {
	
	/**
	 * Adds a staff to this system.
	 * @see de.uos.fmt.musitech.data.structure.container.BasicContainer#add(de.uos.fmt.musitech.data.structure.container.Containable)
	 */
	@Override
	public boolean add(NotationStaff obj) {
		// TODO Auto-generated method stub
		if(contains(obj))
			return false;
		return super.add(obj);
	}

	public class Break extends Rational {

		private boolean soft = false;

		public boolean isSoft() {
			return soft;
		}

		public Break(Rational r, boolean soft) {
			super(r);
			this.soft = soft;
		}

	}

	class TypedBreak extends Break {

		private String breakType = "none";

		/**
		 * @return Returns the breakType.
		 */
		public String getBreakType() {
			return breakType;
		}

		public TypedBreak(Break breaK, String type) {
			super(breaK, breaK.isSoft());
			this.breakType = type;
		}
	}

	/**
	 * indicates if this NotationSystem was already prepared
	 */
	private boolean isPrepared = false;

	/**
	 * this contains the barlines for the score
	 */
	private BarlineContainer barlines;

	private List<NotationStaffConnector> staffConnectors;

	/** Checks if this system has a rendering hint of the given type. 
	 * @param hint The hint type. 
	 * @return true, if a hint of the given type exists for this system, false otherwise. 
	 */
	public boolean hasHint(String hint) {
		if (renderingHints == null)
			return false;
		return renderingHints.containsKey(hint);
	}

	/** Gets the rendering hint of the given type for this NotationSystem, if there is one. 
	 * @param hint The hint type. 
	 * @return The rendering hint registered for the given type.  
	 */
	public Object getHint(String hint) {
		if (renderingHints == null)
			return null;
		return renderingHints.getValue(hint);
	}

	/**
     * 
     */
	public void processDynamics() {
		for (NotationStaff staff: this) {
			staff.processDynamics();
		} // end for
	}

	public void prepareForScore() {
		prepareForScore(1);
		prepareForScore(2);
	}

	/**
	 * Describe <code>prepareForScore</code> method here.
	 * @param pass The pass number. 
	 */
	public void prepareForScore(int pass) {
		for (NotationContainer nc : this ) {
			nc.prepareForScore(pass);
		} // end for
		if (getRenderingHints() == null
			|| getRenderingHints().getValue("barline") == null
			|| !getRenderingHints().getValue("barline").equals("none")) {
			insertBarlines();
		}
		isPrepared = true;
	}

	/**
	 * This method indicates whether the NotationSystem is prepared or not.
	 * @return true if this system is prepared. 
	 * 
	 * @hibernate.property
	 */
	public boolean isPrepared() {
		return isPrepared;
	}

	/**
	 * This Vector contains Rationals at which a linebreak shall occur.
	 */
	private List<Break> linebreaks = new ArrayList<Break>();

	/**
	 * This Vector contains Rationals at which a pagebreak shall occur.
	 */
	private List<Break> pagebreaks = new ArrayList<Break>();

	/**
	 * This method adds a barline to the system
	 * 
	 * @param barline The barline to be added
	 * @return true if this method call has changed the barlines of this System.
	 */
	public boolean addBarline(Barline barline) {
		return barlines.add(barline);
	}

	/**
	 * this method adds all barlines from the parameter to this system
	 * 
	 * @param cont
	 */
	public void addBarlines(BarlineContainer cont) {
		barlines.addAll(cont);
	}

	/**
	 * This method returns the barlines for this system.
	 * 
	 * @return the barlines for this system
	 * @hibernate.many-to-one class =
	 *                        "de.uos.fmt.musitech.data.score.BarlineContainer"
	 *                        foreign-key = "id"
	 */
	public BarlineContainer getBarlines() {
		return barlines;
	}

	/**
	 * This method returns the List of Rationals which describes the linebreaks
	 *      for this system
	 * @return the List of Rationals
	 * @hibernate.bag role="Linebreaks" table="Rational" cascade="all"
	 *                readonly="true"
	 * @hibernate.collection-key column="id"
	 * @hibernate.collection-one-to-many 
	 *                                   class="de.uos.fmt.musitech.utility.math.Rational"
	 */
	public List<Break> getLinebreaks() {
		return linebreaks;
	}

	/**
	 * This method returns the List of Rationals which describes the pagebreaks
	 *      for this system
	 * @return the List of Rationals
	 * @hibernate.bag role="Pagebreaks" table="Rational" cascade="all"
	 *                readonly="true"
	 * @hibernate.collection-key column="id"
	 * @hibernate.collection-one-to-many 
	 *                                   class="de.uos.fmt.musitech.utility.math.Rational"
	 */
	public List<Break> getPagebreaks() {
		return pagebreaks;
	}

	/**
	 * @param barline
	 * @param breaks
	 * @return true id there is a break
	 */
	public boolean hasHardBreakAt(Barline barline, List<Break> breaks) {
		int index = breaks.indexOf(barline.getMetricTime());
		if (index >= breaks.size() || index < 0) {
			return false;
		}
		return !(breaks.get(index)).isSoft();
	}

	/**
	 * @param barline
	 * @return
	 */
	public boolean hasHardBreakAt(Barline barline) {
		return hasHardBreakAt(barline, linebreaks)
				|| hasHardBreakAt(barline, pagebreaks);
	}

	/**
	 * This method adds a linebreak to the system. If the linebreak is not at
	 * the end of a measure an IllegalArgumentException is thrown.
	 * 
	 * @param linebreak the point of metric time at which a linebreak shall
	 *            occur
	 * @exception IllegalArgumentException if the argument is not the end of a
	 *                measure
	 * @exception IllegalStateException if a linebreak is set without any
	 *                barlines present
	 * @see java.lang.IllegalArgumentException
	 */
	public void addLinebreak(Rational linebreak)
			throws IllegalArgumentException, IllegalStateException {
		addLinebreak(linebreak, false);
	}

	/**
	 * @param linebreak
	 * @param soft
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public void addLinebreak(Rational linebreak, boolean soft)
			throws IllegalArgumentException, IllegalStateException {
		/*
		 * if (size() == 0) { throw new IllegalStateException("You cannot set
		 * linebreaks in an empty system"); } NotationStaff staff =
		 * (NotationStaff) get(0); if (staff.size() == 0) { throw new
		 * IllegalStateException("You cannot set linebreaks in an empty staff");
		 * } NotationVoice voice = (NotationVoice) staff.get(0); if
		 * (voice.size() == 0) { throw new IllegalStateException("You cannot set
		 * linebreaks in an empty voice"); }
		 */

		if (!linebreaks.contains(linebreak)) {
			linebreaks.add(new Break(linebreak, soft));
		}

		/*
		 * if (barlines.hasBarlineAt(linebreak)) { } else { throw new
		 * IllegalArgumentException("There is no barline at the specified time
		 * index " + linebreak); }
		 */
	}

	/**
	 * This method adds a pagebreak to the system. If the pagebreak is not at
	 * the end of a measure an IllegalArgumentException is thrown.
	 * 
	 * @param pagebreak the point of metric time at which a linebreak shall
	 *            occur
	 * @exception IllegalArgumentException if the argument is not the end of a
	 *                measure
	 * @exception IllegalStateException if a pagebreak is set without any
	 *                barlines present
	 * @see java.lang.IllegalArgumentException
	 */
	public void addPagebreak(Rational pagebreak) {
		addPagebreak(pagebreak, false);
	}

	public void addPagebreak(Rational pagebreak, boolean soft) {
		if (size() == 0) {
			throw new IllegalStateException(
				"You cannot set pagebreaks in an empty system");
		}
		NotationStaff staff = (NotationStaff) get(0);
		if (staff.size() == 0) {
			throw new IllegalStateException(
				"You cannot set pagebreaks in an empty staff");
		}
		NotationVoice voice = (NotationVoice) staff.get(0);
		if (voice.size() == 0) {
			throw new IllegalStateException(
				"You cannot set pagebreaks in an empty voice");
		}
		if (barlines.hasBarlineAt(pagebreak)) {
			if (!pagebreaks.contains(pagebreak)) {
				pagebreaks.add(new Break(pagebreak, soft));
			}
		} else {
			throw new IllegalArgumentException(
				"There is no barline at the specified time index " + pagebreak);
		}
	}

	public void removePagebreak(Rational pagebreak) {
		removePagebreak(pagebreak, false);
	}

	public void removePagebreak(Rational pagebreak, boolean onlySoft) {
		if (!onlySoft
			|| pagebreaks.get(pagebreaks.indexOf(pagebreak)).isSoft()) {
			pagebreaks.remove(pagebreak);
		}
	}

	public void removeLinebreak(Rational linebreak) {
		removeLinebreak(linebreak, false);
	}

	public void removeLinebreak(Rational linebreak, boolean onlySoft) {
		if (!onlySoft
			|| linebreaks.get(linebreaks.indexOf(linebreak)).isSoft()) {
			linebreaks.remove(linebreak);
		}
	}

	public boolean moveLinebreakLeft(Rational linebreak, boolean onlySoft)
			throws IllegalArgumentException {
		if (!linebreaks.contains(linebreak))
			throw new IllegalArgumentException(
				"There is no linebreak to move at" + linebreak);

		Barline leftBarline = (Barline) barlines.previousElement(barlines
				.getBarlineAt(linebreak));
		if (leftBarline == null)
			return false;

		removeLinebreak(linebreak, onlySoft);
		addLinebreak(leftBarline.getMetricTime(), onlySoft);
		return true;
	}

	/**
	 * Constructs a notation system with the context of the default piece. 
	 */
	public NotationSystem() {
		this(Piece.getDefaultPiece().getContext());
	}

	/**
	 * Creates a new <code>NotationSystem</code> instance, the NotationSystem 
	 * is not automatically set as the Piece's score. 
	 * 
	 * @param piece The piece to use as context. 
	 */
	public NotationSystem(Piece piece) {
		super(piece.getContext(), NotationStaff.class);
	}

	/**
	 * Creates a new <code>NotationSystem</code> instance.
	 * 
	 * @param argContext a <code>Context</code> value
	 */
	public NotationSystem(Context argContext) {
		super(argContext, NotationStaff.class);
		barlines = new BarlineContainer(argContext);
	}

	/**
	 * Sets the Context for this NotationSystem,
	 * 
	 * @see de.uos.fmt.musitech.data.structure.container.BasicContainer#setContext(de.uos.fmt.musitech.data.structure.Context)
	 */
	@Override
	public void setContext(Context argContext) {
		super.setContext(argContext);
		if (barlines == null)
			barlines = new BarlineContainer(argContext);
		barlines.setContext(argContext);
	}

	/**
	 * This method returns the first staff which contains a given metric time or null
	 * if such a staff does not exist.
	 * 
	 * @param metricTime
	 * @return
	 */
	public NotationStaff getStaffContainingMetricTime(Rational metricTime) {
		for (NotationStaff staff : this) {
			if (staff.containsMetricTime(metricTime)) {
				return staff;
			}
		}
		return null;
	}

	/**
	 * @hibernate.many-to-one class =
	 *                        "de.uos.fmt.musitech.data.structure.Context"
	 *                        foreign-key = "uid"
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param barlines The barlines to set.
	 */
	public void setBarlines(BarlineContainer barlines) {
		this.barlines = barlines;
	}

	/**
	 * @param isPrepared The isPrepared to set.
	 */
	public void setPrepared(boolean isPrepared) {
		this.isPrepared = isPrepared;
	}

	/**
	 * @param linebreaks The linebreaks to set.
	 */
	public void setLinebreaks(List<Break> linebreaks) {
		this.linebreaks = linebreaks;
	}

	/**
	 * @param pagebreaks The pagebreaks to set.
	 */
	public void setPagebreaks(List<Break> pagebreaks) {
		this.pagebreaks = pagebreaks;
	}

	/**
	 * this method determines if in any voice in any staff of this system there
	 * are actual notes
	 * 
	 * @return true if there are notes deep in this system
	 */
	public boolean containsNotes() {
		for (Iterator<?> iter = this.iterator(); iter.hasNext();) {
			NotationStaff staff = (NotationStaff) iter.next();
			for (Iterator<?> iter2 = staff.iterator(); iter2.hasNext();) {
				NotationVoice voice = (NotationVoice) iter2.next();
				if (voice.size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param metricTime a point in metric Time
	 * @return the point in metric Time which comes just after the given point.
	 *         If such a point does not exist null is returned.
	 */
	public Rational getNextMetricTime(Rational metricTime) {
		Rational minimal = new Rational(Integer.MAX_VALUE);
		boolean found = true;

		for (Iterator<?> iter = this.iterator(); iter.hasNext();) {
			NotationStaff staff = (NotationStaff) iter.next();
			for (Iterator<?> iter2 = staff.iterator(); iter2.hasNext();) {
				NotationVoice voice = (NotationVoice) iter2.next();
				for (Iterator<?> iter3 = voice.iterator(); iter3.hasNext();) {
					NotationChord chord = (NotationChord) iter3.next();
					if (chord.getMetricTime().isGreater(metricTime)
						&& chord.getMetricTime().isLess(minimal)) {
						minimal = chord.getMetricTime();
						found = true;
					}
				}
			}
		}
		return found ? minimal : null;
	}

	/**
	 * This method inserts a Barline at a specified point in metrical time. If a
	 * NotationChord is on that point of time, it will be splitted and the
	 * resulting chords will be tied. This method also splits the chords in all
	 * other voices belonging to this system as this (this's sibling voices)
	 * 
	 * @param t the point in metric time where the Barline shall be added
	 */
	public void insertBarline(Rational t) {
		for (Iterator<?> iter = this.iterator(); iter.hasNext();) {
			NotationStaff staff = (NotationStaff) iter.next();
			for (Iterator<?> iter2 = staff.iterator(); iter2.hasNext();) {
				NotationVoice voice = (NotationVoice) iter2.next();
				int index = voice.find(t);
				if (index < 0 && ((-index) - 1) != voice.size()) { // since no
					// chord starts at timestamp or the voice ends at
					// timestamp, we have to split the one which starts before
					index = (-index) - 2; // turn the negativ index positiv and
					// go to
					// the chord before
					if (index < 0) { // may happen if the first chord needs to
						// be split
						index = 0;
					}
					if (index < voice.size()) {
						// TODO hier geht's schief, wenn index == -1 ist
						NotationChord left = (NotationChord) voice.get(index);
						if (left.getMetricTime().add(left.getMetricDuration())
								.isGreater(t)
							&& left.getMetricTime().isLess(t)) {
							// NotationChord right = (NotationChord)
							// ObjectCopy.copyObject(left);
							NotationChord right = (NotationChord) left.clone();
							left.setMetricDuration(t.sub(left.getMetricTime()));
							right.setMetricTime(t);
							right.setMetricDuration(left.getMetricTime().add(
								right.getMetricDuration()).sub(t));
							voice.add(right);
							for (int i = 0; i < left.size(); i++) {
								ScoreNote leftScoreNote = ((Note) left.get(i))
										.getScoreNote();
								ScoreNote rightScoreNote = ((Note) right.get(i))
										.getScoreNote();
								leftScoreNote.setTiedNote(rightScoreNote);
							}
						}
					}
				}
			}
		}
		addBarline(new Barline(t));
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

	NotationChord getLastChord() {
		Rational lastAttack = Rational.ZERO;
		NotationChord lastChord = null;
		for (Iterator<?> iter = iterator(); iter.hasNext();) {
			NotationStaff staff = (NotationStaff) iter.next();
			for (Iterator<?> iterator = staff.iterator(); iterator.hasNext();) {
				NotationVoice voice = (NotationVoice) iterator.next();
				if (voice.size() > 0) {
					NotationChord chord = (NotationChord) voice.get(voice
							.size() - 1);
					if (chord.getMetricTime().isGreater(lastAttack)) {
						lastChord = chord;
						lastAttack = chord.getMetricTime();
					}
				}
			}
		}

		return lastChord;
	}

	public Rational getEndTime() {
		Rational endTime = Rational.ZERO;
		for (Iterator<?> iter = iterator(); iter.hasNext();) {
			NotationStaff staff = (NotationStaff) iter.next();
			for (Iterator<?> iterator = staff.iterator(); iterator.hasNext();) {
				NotationVoice voice = (NotationVoice) iterator.next();
				Rational et = voice.getEndTime();
				if (et.isGreater(endTime)) {
					endTime = et;
				}
			}
		}
		return endTime;
	}

	/**
	 * This method inserts barlines into the score. It reads the time signature
	 * and inserts Barlines via insertBarline. It honors already existing
	 * barlines. Before a new barline is added it is checked that between the
	 * last barline and the to be added barline no manual set barline can be
	 * found. If there is a manual set barline, no new one is added, but the
	 * manual set one becomes the last added barline for the next round.
	 * 
	 * @see insertBarline(Timestamp)
	 */
	public void insertBarlines() {
		/*
		 * if (!gapsFilled) { throw new IllegalStateException("you must not call
		 * insertBarlines without calling fillGaps first."); }
		 */

		if (size() == 0)
			return;
		NotationChord lastChord = getLastChord();
		if (lastChord == null) {
			return;
		}
		Rational endOfVoice = lastChord.getMetricTime().add(
			lastChord.getMetricDuration());
		Rational barTime = Rational.ZERO;
		while (barTime.isLess(endOfVoice)) {
			Rational timeSignature = getTimeSignature(barTime);
			// Barline manualSetBarline = getBarlines().getBarlineBetween(beat,
			// beat.add(timeSignature));
			// if (manualSetBarline != null) {
			// beat = manualSetBarline.getMetricTime();
			// } else {
			// Rational newbeat = beat.add(timeSignature);
			// TimeSignatureMarker tsm =
			// (getContext().getPiece().getMetricalTimeLine().
			// getTimeSignatureMarker(beat));
			// if(tsm.getMetricTime().isGreater(beat)){
			// beat = tsm.getMetricTime();
			// } else {
			// beat = newbeat;
			// }
			barTime = getContext().getPiece().getMetricalTimeLine()
					.getNextMeasure(barTime);
			insertBarline(barTime);
			lastChord = getLastChord();
			endOfVoice = lastChord.getMetricTime().add(
				lastChord.getMetricDuration());
			// }
		}
	}

	public void prettyPrint() {
		System.out.println("NotationSystem");
		for (Iterator<?> iter = this.iterator(); iter.hasNext();) {
			NotationStaff element = (NotationStaff) iter.next();
			element.prettyPrint(2);
		}
	}

	public List<NotationStaffConnector> getStaffConnectors() {
		return staffConnectors;
	}

	public void setStaffConnectors(List<NotationStaffConnector> staffConnectors) {
		this.staffConnectors = staffConnectors;
	}

	public void addStaffConnector(NotationStaffConnector con) {
		if (staffConnectors == null)
			staffConnectors = new ArrayList<NotationStaffConnector>();
		staffConnectors.add(con);
	}

	/**
	 * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(org.w3c.dom.Node,
	 *      java.lang.Object, java.util.Hashtable, org.w3c.dom.Document,
	 *      java.lang.String)
	 */
	public synchronized boolean toMPEG(MusiteXMLSerializer instance,
										Node piece, Object object,
										String fieldname) {
		assert (piece.getNodeName().equals("piece"));
		// create element representing this object
		Element score = XMLHelper.addElement(piece, "score");
		// check for reference
		if (instance.knowsObject(score, object))
			return true;

		// metadata
		MPEG_SMR_Tools.serializeMetaData(instance, score, this);
		// rendering hints
		instance.writeXML(score, getRenderingHints(), null, null);

		MetricalTimeLine mtl = this.getContext().getPiece()
				.getMetricalTimeLine();
		Container<Marker> harmonyTrack = this.getContext().getPiece().getHarmonyTrack();

		/*
		 * layout TODO fill with content (right now serializes empty elements)
		 */
		Element layout = XMLHelper.addElement(score, "layout");
		Element layout_pages = XMLHelper.addElement(layout, "pages");
		layout_pages.setAttribute("left", "");
		layout_pages.setAttribute("top", "");
		layout_pages.setAttribute("right", "");
		layout_pages.setAttribute("bottom", "");
		Element layout_distances = XMLHelper.addElement(layout_pages,
			"distances");
		Element distances_staffLines = XMLHelper.addElement(layout_distances,
			"staffLines");
		distances_staffLines.setAttribute("small", "");
		distances_staffLines.setAttribute("normal", "");
		Element distances_systems = XMLHelper.addElement(layout_distances,
			"systems");
		distances_systems.setAttribute("top", "");
		distances_systems.setAttribute("between", "");

		/*
		 * add systems-element
		 */
		Node systems = XMLHelper.addElement(score, "systems");

		/*
		 * get Breaks
		 */

		SortedCollection<TypedBreak> breaks = new SortedCollection<TypedBreak>(Break.class);

		for (Iterator<?> iterator = getLinebreaks().iterator(); iterator.hasNext();) {
			Break breaK = (Break) iterator.next();
			if (breaK.isSoft()) {
				breaks.add(new TypedBreak(breaK, "line-soft"));
			} else {
				breaks.add(new TypedBreak(breaK, "line-hard"));
			}
		}
		for (Iterator<?> iterator = getPagebreaks().iterator(); iterator.hasNext();) {
			Break breaK = (Break) iterator.next();
			if (breaK.isSoft()) {
				breaks.add(new TypedBreak(breaK, "page-soft"));
			} else {
				breaks.add(new TypedBreak(breaK, "page-hard"));
			}
		}

		/*
		 * segment the NotationSystem
		 */

		/*
		 * create systems
		 */
		int numSystems = breaks.size() + 1; // '+1': measure behind last break

		for (int i = 0; i < numSystems; i++) {

			// use new system if break

			MPEGSystem mpegSystem = new MPEGSystem();

			// begin and end of the the segment
			Rational begin;
			Rational end;

			if (i == 0)
				begin = Rational.ZERO;
			else
				begin = breaks.get(i - 1);

			// set wrap-attribute; exclude last measure
			if (i < breaks.size()) {
				TypedBreak breaK = breaks.get(i);
				mpegSystem.setWrapType(breaK.getBreakType());
			}
			// last end
			if (i < numSystems - 1)
				end = breaks.get(i);
			else
				end = Rational.MAX_VALUE;

			/*
			 * create Staff-Segments 1. clone complete staff 2. remove
			 * noteObject that or not in the system's time slice
			 */
			List<?> staffList = getContent();
			for (Iterator<?> staffIter = staffList.iterator(); staffIter.hasNext();) {
				NotationStaff mtNotationStaff = (NotationStaff) staffIter
						.next();
				// clone the staff in order not to loose original references
				// during segmentation
				mtNotationStaff = (NotationStaff) mtNotationStaff.clone();

				List<?> voiceList = mtNotationStaff.getContent();
				for (Iterator<?> voiceIter = voiceList.iterator(); voiceIter
						.hasNext();) {
					NotationVoice mtVoice = (NotationVoice) voiceIter.next();
					List<?> chordList = mtVoice.getContent();
					for (Iterator<?> chordIter = chordList.iterator(); chordIter
							.hasNext();) {
						try {
							NotationChord chord = (NotationChord) chordIter
									.next();
							Rational cOnset = chord.getMetricTime();
							// remove chord if not in segment's time slice
							if (!(begin.isLessOrEqual(cOnset) && end
									.isGreater(cOnset))) {
								mtVoice.remove(chord);
							}
						} catch (ConcurrentModificationException e) {
							chordIter = chordList.iterator();
						}
					}
				}
				mpegSystem.add(mtNotationStaff);
			}
			// get relevant MetricalTimeLine information
			List<Marker> markerList = mtl.getContent();
			for (Iterator<Marker> iter = markerList.iterator(); iter.hasNext();) {
				Marker marker = iter.next();
				// test if in segment's time slice
				// if (marker instanceof Marker) {
				Rational markerTime = ((Marker) marker).getMetricTime();
				if (begin.isLessOrEqual(markerTime)
					&& end.isGreater(markerTime)) {
					// marker is relevant
					mpegSystem.getContextLine().add(marker);
				}
				// } else {
				// assert false; // OR: cast, check time and add to contextLine
				// }
			}
			// get relevant HarmonyTrack information
			for (Iterator<Marker> iter = harmonyTrack.iterator(); iter
					.hasNext();) {
				Marker marker = iter.next();
				// test if in segment's time slice
				// if (marker instanceof Marker) {
				Rational markerTime = ((Marker) marker).getMetricTime();
				if (begin.isLessOrEqual(markerTime)
					&& end.isGreater(markerTime)) {
					// marker is relevant
					mpegSystem.getContextLine().add(marker);
				}
				// } else {
				// assert false; // OR: cast, check time and add to contextLine
				// }
			}
			// TODO ??? get relevant Dynamic information
			// get relevant Barlines information
			for (Iterator<?> iter = getBarlines().iterator(); iter.hasNext();) {
				Barline barline = (Barline) iter.next();
				// test if in segment's time slice
				Rational markerTime = barline.getMetricTime();
				if (begin.isLessOrEqual(markerTime)
					&& end.isGreater(markerTime)) {
					// marker is relevant
					mpegSystem.getContextLine().add(barline);
				}
			}

			instance.writeXML(systems, mpegSystem, null, null);

		} // loop over new systems

		return true;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
	 *      org.w3c.dom.Element, java.util.Hashtable, java.lang.Object)
	 */
	public Object fromMPEG(MusiteXMLSerializer instance, Element score) {
		// commons----------------------------------
		// reference-handling
		Object reference = instance.getReferenced(score, this);
		if (reference != null)
			return reference;
		// metadata
		MPEG_SMR_Tools.deserializeMetaData(instance, score, this);
		// rendering hints
		setRenderingHints(MPEG_SMR_Tools.deserializeRenderingHints(instance,
			score));
		// -----------------------------------------

		Element systems = XMLHelper.getElement(score, "systems");
		NodeList systemList = systems.getElementsByTagName("system");

		// context
		this.setContext(instance.getContext());

		// Array for the unsegmented staffes
		NotationStaff[] combinedStaves = null;

		String lastWrap = null;

		// layout
		Element layout = XMLHelper.getElement(score, "layout");
		if (layout != null) {
			Element pages = XMLHelper.getElement(score, "pages");
			if (pages != null) {
				if (pages.hasAttribute("bottom"))
					this.addRenderingHint("page bottom space", new Integer(
						pages.getAttribute("bottom")));
			}
		}

		// iterate over systems
		for (int i = 0; i < systemList.getLength(); i++) {
			Element system = (Element) systemList.item(i);

			// deserialize context-line
			Element contextLine = (Element) system.getElementsByTagName(
				"context").item(0);
			MPEGContextLine mpegContext = (MPEGContextLine) instance.readXML(
				contextLine, MPEGContextLine.class);
			// barlines
			if (mpegContext != null)
				for (Iterator<?> iter = mpegContext.getBarlines().iterator(); iter
						.hasNext();) {
					this.addBarline((Barline) iter.next());
				}

			// deserialze systemSync
			Node sync = system.getElementsByTagName("sync").item(0);
			Rational syncMetricTime = null;
			if (sync != null) {
				// TODO synchronize tempo, physicalTime

				// look for MetricTime for synchronization
				Node metricTime = ((Element) sync).getElementsByTagName(
					"metricTime").item(0);
				if (metricTime != null) {
					syncMetricTime = (Rational) instance.readXML(
						(Element) metricTime, Rational.class);
				}
			}
			if (syncMetricTime != null) {
				// syncronize metric time
				instance.setNextOnset(this, syncMetricTime);
			} else {
				if (combinedStaves == null) {
					instance.setNextOnset(this, Rational.ZERO);
				} else {
					if (!checkStaveConsistence(combinedStaves))
						System.out
								.println("[WARNING] deserialized Staves in have different length in system "
											+ i);
					NotationStaff precedingStaff = combinedStaves[0];
					Rational lastEndtime = precedingStaff.getEndtime();
					instance.setNextOnset(this, lastEndtime);
				}
			}

			if (DebugState.DEBUG_MPEG)
				System.out.println("System " + i + " onset: "
									+ instance.getNextOnset(this));

			/*
			 * deserialize wrap
			 */
			Rational systemOnset = instance.getNextOnset(this);
			if (lastWrap != null) {
				if ("line-soft".equals(lastWrap)) {
					this.addLinebreak(systemOnset, true);
				} else if ("line-hard".equals(lastWrap)) {
					this.addLinebreak(systemOnset, false);
				} else if ("page-soft".equals(lastWrap)) {
					this.addPagebreak(systemOnset, true);
				} else if ("page-hard".equals(lastWrap)) {
					this.addPagebreak(systemOnset, false);
				}
			}
			// wrap information is processed in next loop/system
			lastWrap = system.getAttribute("wrap");

			/*
			 * deserialize & combine staves
			 */
			Element staves = (Element) system.getElementsByTagName("staves")
					.item(0);
			NodeList staffList = staves.getElementsByTagName("staff");
			if (combinedStaves == null) {
				combinedStaves = new NotationStaff[staffList.getLength()];
			}
			for (int iStaff = 0; iStaff < staffList.getLength(); iStaff++) {
				// deserialize staff
				Element mpegStaff = (Element) staffList.item(iStaff);
				instance.setParent(mpegStaff, this);
				NotationStaff staffSegment = (NotationStaff) instance.readXML(
					mpegStaff, NotationStaff.class);
				if (combinedStaves[iStaff] == null) {
					// current mpeg-staff is first segment of the combinded
					// NotationStaff
					combinedStaves[iStaff] = staffSegment;
				} else {
					// mpeg-staff is a following part of a segment staff
					// and is added to the NotationStaff
					combinedStaves[iStaff].addStaffSegment(staffSegment);
				}
			} // mpeg-staff loop
		} // mpeg-system loop

		// add deserialized Staves to NotationSystem
		for (int i = 0; i < combinedStaves.length; i++) {
			// add staff
			this.add(combinedStaves[i]);
		}

		// insert barlines if necessary
		if (getRenderingHints() == null
			|| getRenderingHints().getValue("barline") == null
			|| !getRenderingHints().getValue("barline").equals("none")) {
			insertBarlines();
		}
		generateBeams();
		return this;
	}

	/**
	 * TODO add comment
	 */
	private void generateBeams() {
		for (Iterator<?> iter = this.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof NotationStaff) {
				NotationStaff nc = (NotationStaff) obj;
				nc.generateBeams();
			} // end if

		} // end for

	}

	/**
	 * @param combinedStaves
	 * @return
	 */
	private boolean checkStaveConsistence(NotationStaff[] combinedStaves) {
		for (int i = 0; i < combinedStaves.length - 1; i++) {
			Rational endThis = combinedStaves[i].getEndtime();
			Rational endSibling = combinedStaves[i].getEndtime();
			if (endThis.equals(endSibling))
				return false;
		}
		// all staves equal end or only one staff if arrived here
		return true;
	}

	public Collection<NotationSystem> splitAtLineBreaks() {
		ArrayList<NotationSystem> systems = new ArrayList<NotationSystem>();

		// for every linebreak + 1 create a system which has the same staffs and
		// voices as *this*
		// also create a lookup HashMap which links an old voice to a list of
		// new ones
		HashMap<NotationVoice, ArrayList<NotationVoice>> lookup = new HashMap<NotationVoice, ArrayList<NotationVoice>>();
		for (int i = 0; i < linebreaks.size() + 1; i++) {
			NotationSystem newSystem = new NotationSystem(context);
			newSystem.setRenderingHints(getRenderingHints());
			newSystem.setBarlines(getBarlines());

			systems.add(newSystem);
			for (Iterator<?> iter = iterator(); iter.hasNext();) {
				NotationStaff element = (NotationStaff) iter.next();
				NotationStaff newElement = new NotationStaff(newSystem);
				newElement.setRenderingHints(element.getRenderingHints());
				newElement.setClefTrack(element.getClefTrack());
				newElement.setAttachables(element.getAttachables());

				for (Iterator<?> iterator = element.iterator(); iterator.hasNext();) {
					NotationVoice element2 = (NotationVoice) iterator.next();
					NotationVoice newElement2 = new NotationVoice(newElement);
					element2.setRenderingHints(newElement2.getRenderingHints());

					ArrayList<NotationVoice> newElements;
					if (lookup.containsKey(element2)) {
						newElements = lookup.get(element2);
					} else {
						newElements = new ArrayList<NotationVoice>();
						lookup.put(element2, newElements);
					}
					newElements.add(newElement2);
				}
			}
		}

		List<Break> sortedLinebreaks = new ArrayList<Break>(linebreaks);
		Collections.sort(sortedLinebreaks);

		for (Iterator<?> iter = iterator(); iter.hasNext();) {
			NotationStaff staff = (NotationStaff) iter.next();
			for (Iterator<?> iterator = staff.iterator(); iterator.hasNext();) {
				NotationVoice voice = (NotationVoice) iterator.next();
				for (Iterator<?> iterator2 = voice.iterator(); iterator2.hasNext();) {
					NotationChord chord = (NotationChord) iterator2.next();
					int index = Collections.binarySearch(sortedLinebreaks,
						chord.getMetricTime());

					NotationVoice currentVoice;
					if (index >= 0) { // the chord starts at a linebreak => we
						// have to insert it after that
						currentVoice = (NotationVoice) lookup
								.get(voice).get(index + 1);
					} else {
						int insertionPoint = -(index + 1);
						currentVoice = (NotationVoice) lookup
								.get(voice).get(insertionPoint);
					}
					currentVoice.add(chord);
				}
			}
		}

		return systems;
	}

	/**
	 * Checks if this system contains multilingual lyrics.
	 * 
	 * @return true if there are multilingual lyrics in this system.
	 */
	public Set<?> getLyricsLanguages() {
		Set<Locale> langSet = new LinkedHashSet<Locale>();
		for (Iterator<?> iter = iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof NotationStaff) {
				NotationStaff staff = (NotationStaff) obj;
				langSet.addAll(staff.getLyricsLanguages());
			}
		}
		return langSet;
	}

	/**
	 * Creates beams in all voices in this system. Old beams are deleted.
	 */
	public void createBeams() {
		for (Iterator<NotationStaff> i = iterator(); i.hasNext();)
			i.next().createBeams();
	}

	/**
	 * Creates a Score from the notes in the container Pool, making a best
	 * attempt to interpret the container structure.
	 */
	public static NotationSystem createNotationSystem(Piece piece) {

		if (piece != null && piece.getScore() != null) {
			return piece.getScore();
		}

		Container<?> containers = piece.getContainerPool();
		Context context = piece.getContext();

		NotationSystem nSystem = NotationSystem
				.createNotationSystem(containers);
		nSystem.setRenderingHints(containers.getRenderingHints());
		return nSystem;
	}

	/**
	 * Creates a Score from the argumnet container, making a best attempt to
	 * interpret the container structure.
	 * 
	 * @param cont The container to use.
	 * @param context The context
	 * @return
	 */
	public static NotationSystem createNotationSystem(Container<?> cont) {
		NotationSystem system = new NotationSystem(cont.getContext());
		system.setRenderingHints(cont.getRenderingHints());
		NotationStaff currentStaff = new NotationStaff(	system);
		NotationVoice currentVoice = new NotationVoice( currentStaff);
		for (Iterator<?> iter = cont.iterator(); iter.hasNext();) {
			boolean continueGeneric = false;
			Object element = iter.next();
			if (element instanceof Note) {
				currentVoice.add((Note)element);
			} else if (element instanceof Voice) {
				if (currentVoice.size() == 0)
					currentStaff.remove(currentVoice);
				currentVoice = new NotationVoice( currentStaff);
				currentVoice.setRenderingHints(((Voice) element)
						.getRenderingHints());
				continueGeneric = true;
			} else if (element instanceof StaffContainer) {
				if (currentStaff.size() == 0
					|| (currentStaff.size() == 1 && ( currentStaff.get(0)).size() == 0)) {
					system.remove(currentStaff);
				}
				currentStaff = new NotationStaff(system);
				currentStaff.setRenderingHints(((StaffContainer) element)
						.getRenderingHints());
				continueGeneric = true;
			} else if (element instanceof BeamContainer) {
				currentVoice.addBeamContainer((BeamContainer) element);
			} else if (element instanceof SlurContainer) {
				currentVoice.addSlurContainer((SlurContainer) element);
			} else if (element instanceof TupletContainer) {
				currentVoice.addTuplet((TupletContainer) element);
			} else if (element instanceof Barline) {
				system.addBarline((Barline) element);
			} else if (element instanceof Linebreak) {
				system.addLinebreak(((Linebreak) element).getMetricTime());
			} else if (element instanceof Clef) {
				currentStaff.addClef((Clef) element);
			} else if (element instanceof MetricAttachable) {
				currentStaff.addAttachable((MetricAttachable) element);
			} else if (element instanceof Container) {
				continueGeneric = true;
			}
			if (continueGeneric && element instanceof Container) {
				// Recursive call
				createNotationSystem((Container<?>) element);
			}
		}
		return system;
	}

	/**
	 * Returns the first voice of the first staff. If it not exists, it is
	 * created.
	 */
	public NotationVoice getFirstVoice() {
		NotationStaff nst = size() == 0	? new NotationStaff(this) : get(0);
		NotationVoice nv = nst.size() == 0 ? new NotationVoice(	nst) : nst.get(0);
		return nv;
	}

}
