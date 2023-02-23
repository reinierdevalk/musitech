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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.data.audio.AudioObject;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.structure.container.UnmodifiableContainer;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.data.structure.linear.Part;
import de.uos.fmt.musitech.data.time.BeatMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.performance.ScoreToPerfomance;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.time.TimeLine;
import de.uos.fmt.musitech.utility.HashCodeGenerator;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * This object represents a piece of music, containing an number of components
 * like audio, scores, performance objects and containers.
 * 
 * @author Tillman Weyde
 * @version $Revision: 8542 $, $Date: 2008-05-08 00:30:58 +0100 (Thu, 08 May
 *          2008) $
 * @hibernate.class table="Piece"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 */
public class Piece implements MObject, Named, Containable, IMPEGSerializable {

	// needed by the MusicSOM application
	private static final long serialVersionUID = -6256891923668365724l;
	/**
	 * The timeLine can be
	 */
	TimeLine timeLine;
	private Container<Container<?>> containerPool;
	private Container<AudioObject> audioPool;
	private Container<Container<?>> selectionPool;
	private SortedContainer<Marker> harmonyTrack;
	private MetricalTimeLine metricalTimeLine;
	private String name;
	private Map<MObject, MetaDataCollection> metaMap = new HashMap<MObject, MetaDataCollection>();
	private NotationSystem score;

	// Unique ID for this object.
	private Long uid;

	private int hashCode = HashCodeGenerator.getHashCode();

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * The context of this class
	 */
	Context context;

	/**
	 * Constructor for an empty Piece.
	 */
	public Piece() {
		context = new Context(this);
		audioPool = new BasicContainer<AudioObject>(context);
		audioPool.setName("Audio Elements");
		harmonyTrack = new SortedContainer<Marker>(context, Marker.class,
			new MetricalComparator());
		harmonyTrack.setName("Harmonies");
		metricalTimeLine = new MetricalTimeLine(context);
		metricalTimeLine.setName("Meter Track");
		timeLine = new TimeLine(context);
		timeLine.setName("Time Line");
		containerPool = new BasicContainer<Container<?>>(context);
		containerPool.setName("Structures");
		selectionPool = new BasicContainer<Container<?>>(context);
		selectionPool.setName("Selections");
	}


	/**
	 * Copy constructor.
	 * 
	 * @param p
	 * @author Reinier
	 */
	public Piece(Piece p) {
		this.context = p.getContext();
		this.audioPool = p.getAudioPool();
		this.harmonyTrack = p.getHarmonyTrack();
		this.metricalTimeLine = p.getMetricalTimeLine();
		this.timeLine = p.getTimeLine();
		this.containerPool = p.getContainerPool();
		this.selectionPool = p.getSelectionPool();

		this.name = p.getName();
		this.metaMap = p.getMetaMap();
		this.score = p.getScore();
		this.mapKeyList = p.getMapKeyList();
		this.mapValueList = p.getMapValueList();
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
		// return ObjectCopy.comparePublicProperties( this, obj );
	}

	/**
	 * Method to test the class
	 * 
	 * @return this piece, filled with example data.
	 */
	public final Piece setSampleData() {

		// Make two scorenotes
		ScoreNote aScoreNote = new ScoreNote();
		aScoreNote.setAlteration((byte) 1);
		aScoreNote.setDiatonic('C');
		aScoreNote.setMetricDuration(new Rational(4));
		aScoreNote.setOctave((byte) 2);
		aScoreNote.setMetricTime(new Rational(8));

		ScoreNote bScoreNote = new ScoreNote();
		bScoreNote.setAlteration((byte) 2);
		bScoreNote.setDiatonic('D');
		bScoreNote.setMetricDuration(new Rational(8));
		bScoreNote.setOctave((byte) 3);
		bScoreNote.setMetricTime(new Rational(16));

		// Make two timestamps
		long aTime = 100;
		long bTime = 200;

		// Make two notes and add the ScoreNotes
		Note aNote = new Note(new PerformanceNote(aTime));
		aNote.setScoreNote(aScoreNote);
		Note bNote = new Note(new PerformanceNote(bTime));
		bNote.setScoreNote(bScoreNote);

		// Make two MidiNotes
		MidiNote aMidiNote = new MidiNote(100, 30, 70, 80);
		MidiNote bMidiNote = new MidiNote(200, 20, 80, 70);

		// Add the MidiNotes
		aNote.setPerformanceNote(aMidiNote);
		bNote.setPerformanceNote(bMidiNote);

		// Make a timeline
		TimeLine aTimeLine = new TimeLine(context);
		aTimeLine.add(bNote);
		aTimeLine.add(aNote);

		getScore().getFirstVoice().add(aNote);
		getScore().getFirstVoice().add(bNote);
		
		setName("Sample music data");

		// Alex todo
		Container<Note> part = new Part(context);
		part.add(aNote);
		part.setName("Section with Note 'C'");
		getContainerPool().add(part);

		this.setTimeLine(aTimeLine);

		// Make a MetaDataItem (text/plain)
		MetaDataItem metaDataItem1 = new MetaDataItem(
			"Biographie des Komponisten");
		// Make a MetaDataValue (text/plain)
		MetaDataValue aMetaValue = new MetaDataValue();

		aMetaValue.setMetaType("text/plain");
		aMetaValue
				.setMetaValue("Franz Schubert. Composer of symphonies (one uncompleted), string quartets und piano music.");
		// Set the MetaDataValue
		metaDataItem1.setMetaValue(aMetaValue);

		// Make a second MetaDataItem (image/jpeg)
		MetaDataItem metaDataItem2 = new MetaDataItem("Bild des Komponisten");
		// Make a second MetaDataValue
		MetaDataValue bMetaValue = new MetaDataValue();
		bMetaValue.setMetaType("image/jpeg");

		// System.out.println("Piece: aImage: "+aImage);
		bMetaValue.setMetaValue(getClass().getResource("Schubert.jpg"));
		// Set the MetaDataValue
		metaDataItem2.setMetaValue(bMetaValue);

		// Make a third MetaDataItem (image/gif)
		MetaDataItem metaDataItem3 = new MetaDataItem("Partitur");
		// Make a third MetaDataValue
		MetaDataValue metaValue3 = new MetaDataValue();
		metaValue3.setMetaType("image/gif");

		// System.out.println("Piece: cImage: "+cImage);
		metaValue3.setMetaValue(getClass().getResource("Notes.gif"));
		// Set the MetaDataValue
		metaDataItem3.setMetaValue(metaValue3);

		// Add the three MetaDataItem objects to the metaInfoPool
		MetaDataCollection mdc = new MetaDataCollection();
		mdc.addMetaDataItem(metaDataItem1);
		mdc.addMetaDataItem(metaDataItem2);
		mdc.addMetaDataItem(metaDataItem3);

		this.metaMap.put(this, mdc);

		// Create an AudioFileObject with TimeStamp set a name and add it to the
		// audioPool
		de.uos.fmt.musitech.data.audio.AudioFileObject aAudioObject = new de.uos.fmt.musitech.data.audio.AudioFileObject(
			aTime);
		aAudioObject.setName("AudioTest");
		// aAudioObject.loadURL(getClass().getResource("type.wav"));
		// aAudioObject.setUrl("type.wav");
		try {
			aAudioObject.setSourceURL(getClass().getResource("type.wav"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.audioPool.add(aAudioObject);

		// Make a fourth MetaDataItem (image/panel)
		MetaDataItem metaDataItemD = new MetaDataItem("Program screenshot");
		// Make a second MetaDataValue
		MetaDataValue metaValueD = new MetaDataValue();
		metaValueD.setMetaType("image/gif");

		// java.awt.Frame frame = null;
		// java.awt.Graphics g = null;

		// TODO write Image to file before setting it as metadata value.
		// try {
		// frame = new java.awt.Frame();
		// frame.addNotify();
		//
		// Image dImage = frame.createImage(200, 60);
		// g = dImage.getGraphics();
		// g.setFont(new java.awt.Font("Serif", java.awt.Font.ITALIC, 48));
		// g.drawString("Musitech", 10, 50);
		//
		// // Set the Image
		// dMetaValue.setMetaValue(dImage);
		// // Set the MetaDataValue
		// dMetaInfo.setMetaValue(dMetaValue);
		//
		// } finally {
		// if (g != null)
		// g.dispose();
		// if (frame != null)
		// frame.removeNotify();
		// }

		// Add the MetaDataItem object to the metaInfoPool
		mdc.addMetaDataItem(metaDataItemD);

		// Add some TimedMetrical and TimeSignatureMarker to the
		// metricalTimeLine
		System.out.println("Adding TimedMetrical at 3/2");
		metricalTimeLine.add(new TimedMetrical(1245, new Rational(3, 2)));
		System.out.println("Adding TimeSignatureMarker at 3/2");
		metricalTimeLine.add(new TimeSignatureMarker(4, 4, new Rational(3, 2)));
		System.out.println("Adding TimeSignatureMarker at 19/4");
		metricalTimeLine
				.add(new TimeSignatureMarker(3, 4, new Rational(19, 4)));
		System.out.println("Adding TimedMetrical at 19/4");
		metricalTimeLine.add(new TimedMetrical(65433, new Rational(19, 4)));

		// Add a KeyMarker to the metricalTimeLine
		System.out.println("Adding a keyMarker to metTL");
		KeyMarker keyMarker = new KeyMarker(new Rational(1, 4), 15);
		keyMarker.setRootAccidental(1);
		keyMarker.setMode(Mode.MODE_DORIAN);
		keyMarker.setRoot('D');
		metricalTimeLine.add(keyMarker);

		// Add a second KeyMarker to the metricalTimeLine
		System.out.println("Adding a second keyMarker to metTL");
		KeyMarker keyMarker2 = new KeyMarker(new Rational(2, 4), 25);
		keyMarker2.setRootAlteration(1);
		keyMarker2.setMode(Mode.MODE_PHRYGIAN);
		keyMarker2.setRoot('E');
		metricalTimeLine.add(keyMarker2);

		de.uos.fmt.musitech.utility.obj.ObjectCopy.writeXML(this, new File(
			"sampleMusic.xml"));

		return this;

	}

	/**
	 * Insert the method's description here. Creation date: (10.1.2002 18:02:29)
	 * 
	 * @return java.util.Collection hibernate.list name="audioContainer"
	 *         cascade="all" table = "audioObj_in_Piece"
	 *         hibernate.collection-key column="piece_id"
	 *         hibernate.collection-index column="position"
	 *         hibernate.collection-many-to-many class =
	 *         "de.uos.fmt.musitech.data.MObject" column="MObject_id"
	 * @hibernate.many-to-one class = "de.uos.fmt.musitech.data.MObject"
	 *                        foreign-key = "uid" cascade = "all"
	 */
	public Container<AudioObject> getAudioPool() {
		return audioPool;
	}

	/**
	 * Insert the method's description here. Creation date: (10.1.2002 18:01:09)
	 * 
	 * @return int hibernate.list name="Container" cascade="all" table =
	 *         "container_in_Piece" hibernate.collection-key column="piece_id"
	 *         hibernate.collection-index column="position"
	 *         hibernate.collection-many-to-many class =
	 *         "de.uos.fmt.musitech.data.MObject" column="MObject_id"
	 * @hibernate.many-to-one class = "de.uos.fmt.musitech.data.MObject"
	 *                        foreign-key = "uid" cascade = "all"
	 */
	public Container<Container<?>> getContainerPool() {
		return containerPool;
	}

	/**
	 * Insert the method's description here. Creation date: (10.1.2002 17:53:47)
	 * 
	 * @return de.uos.fmt.musitech.data.structure.TimeLine
	 * @hibernate.many-to-one class = "de.uos.fmt.musitech.time.TimeLine"
	 *                        foreign-key = "uid" cascade = "all"
	 */
	public TimeLine getTimeLine() {
		return timeLine;
	}

	/**
	 * Get the meta data collection associated with the object contained in this
	 * piece.
	 * 
	 * @param obj The object of which to retrieve the MetaData
	 * @return the MetaData from the given object
	 */

	public MetaDataCollection getMetaData(MObject obj) {
		return metaMap.get(obj);
	}

	/**
	 * Get the meta data value associated with the given key for the object 
	 * contained in this piece.
	 * 
	 * @param obj The object of which to retrieve the MetaData
	 * @param key the key with which to retrieve the object
	 * @return the MetaData from the given object
	 */
	public Object getMetaData(MObject obj, String key) {
		MetaDataCollection mdc = metaMap.get(obj);
		if(mdc == null)
			return null;
		MetaDataItem mdi =  mdc.getItemByKey(key);
		if(mdi == null)
			return null;
		MetaDataValue mdv = mdi.getMetaDataValue();
		if (mdv == null) 
			return null;
		return mdv.getMetaValue();
	}

	/**
	 * Set the meta data collection of the given object in this piece
	 * 
	 * @param obj the Object of which to set the MetaData
	 * @param metaData the Data to set for the object
	 * @return The Data given
	 */
	public MetaDataCollection setMetaData(MObject obj,
											MetaDataCollection metaData) {
		return metaMap.put(obj, metaData);
	}

	/**
	 * Set the meta data collection of the given object in this piece
	 * 
	 * @param obj the Object of which to set the MetaData
	 * @param metaDataItem the Data to set for the object
	 * @return The Data given
	 */
	public void setMetaData(MObject obj,
											MetaDataItem metaDataItem) {
		MetaDataCollection mdc = getMetaData(obj);
		if(mdc == null) {
			mdc = new MetaDataCollection();
			setMetaData(obj, mdc);
		}
		mdc.addMetaDataItem(metaDataItem);		
	}

	/**
	 * This method returns the beat markers stored in the meter track as a new
	 * container.
	 * 
	 * @return the beat markers in a container
	 * @see BeatMarker
	 */
	public Container<BeatMarker> getBeatMarkers() {
		Container<BeatMarker> cont = new BasicContainer<BeatMarker>(context);

		if (metricalTimeLine == null)
			return cont;

		// Process all elements of the meter track
		for (int i = 0; i < metricalTimeLine.size(); i++) {
			// Just add the beat markers
			if (metricalTimeLine.get(i) instanceof BeatMarker)
				cont.add((BeatMarker) metricalTimeLine.get(i));
		}

		return cont;
	}

	/**
	 * Just for testing.
	 * 
	 * @param args ignored
	 */
	public static void main(String[] args) {

		// Make a music object
		Piece aMusic = new Piece();
		aMusic.setSampleData();

		System.out.println("Piece object was made.");
		System.out.println("Printing the TimeLine ...");

		TimeLine aTimeLine = aMusic.getTimeLine();

		// int index1 = aTimeLine.find(100);
		// System.out.println("index1 "+index1);

		// int index2 = aTimeLine.find(200);
		// System.out.println("index2 "+index2);

		Note aNote;

		for (int i = 0; i < aTimeLine.size(); i++) {
			aNote = (Note) aTimeLine.get(i);
			System.out.println("ScoreNote: " + i + "\t ---------------");
			System.out.println("Diatonic \t"
								+ aNote.getScoreNote().getDiatonic());
			System.out.println("Onset \t\t"
								+ aNote.getScoreNote().getMetricTime());
			System.out.println("Duration \t"
								+ aNote.getScoreNote().getMetricDuration());
			System.out.println("Accidental \t"
								+ aNote.getScoreNote().getAlteration());

			System.out.println("MidiNote: " + i + "\t\t ---------------");
			System.out.println("Pitch \t\t" + aNote.midiNote().getPitch());
			System.out.println("Time \t\t" + aNote.midiNote().getTime());
			System.out
					.println("Duration \t\t" + aNote.midiNote().getDuration());
			System.out.println("Velocity \t" + aNote.midiNote().getVelocity());
		}

	}

	// /**
	// * Insert the method's description here. Creation date: (11.02.2002
	// * 03:05:36)
	// *
	// * @return de.uos.fmt.musitech.data.structure.Container hibernate.list
	// * name="MetaInfoPool" cascade="all" table = "metaInfo_in_Piece"
	// * hibernate.collection-key column="piece_id"
	// * hibernate.collection-index column="position"
	// * hibernate.collection-many-to-many class =
	// * "de.uos.fmt.musitech.data.MObject" column="MObject_id"
	// * hibernate.many-to-one class = "de.uos.fmt.musitech.data.MObject"
	// * foreign-key = "uid" cascade = "all"
	// */
	// public Container getMetaInfoPool() {
	// return metaInfoPool;
	// }

	/**
	 * Gets the name of this piece.
	 * 
	 * @return java.lang.String
	 * @hibernate.property
	 */
	@Override
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Gets the NotePool of this piece.
	 * 
	 * @return java.util.Collection hibernate.list name="NoteContainer"
	 *         cascade="all" table = "Notes_in_Piece" hibernate.collection-key
	 *         column="piece_id" hibernate.collection-index column="position"
	 *         hibernate.collection-many-to-many class =
	 *         "de.uos.fmt.musitech.data.MObject" column="MObject_id"
	 * @hibernate.many-to-one class = "de.uos.fmt.musitech.data.MObject"
	 *                        foreign-key = "uid" cascade = "all"
	 */
	@Deprecated
	public Container<Note> getNotePool() {
		List<Note> notes = new ArrayList<Note>();
		if (score != null) {
			for (Containable c : score.getContentsRecursive()) {
				if (c instanceof Note) {
					notes.add((Note) c);
				}
			}
		}
		Collections.sort(notes, new MetricalComparator());
		return new UnmodifiableContainer<Note>(notes);
	}

	/**
	 * Get the audio objects in this piece.
	 * 
	 * @param newAudioPool java.util.Collection
	 */
	public void setAudioPool(Container<AudioObject> newAudioPool) {
		audioPool = newAudioPool;
	}

	/**
	 * This method is only for persistence and should not be used by
	 * applications. Sets the container pool of this piece.
	 * 
	 * @param newContainerPool java.util.Collection
	 */
	public void setContainerPool(Container<Container<?>> newContainerPool) {
		containerPool = newContainerPool;
	}

	/**
	 * Set the name of this piece.
	 * 
	 * @param newName java.lang.String
	 */
	@Override
	public void setName(java.lang.String newName) {
		name = newName;
	}

	/**
	 * Set the TimeLine
	 * 
	 * @param newTimeLine de.uos.fmt.musitech.data.structure.TimeLine
	 */
	public void setTimeLine(TimeLine newTimeLine) {
		timeLine = newTimeLine;
	}

	/**
	 * Sets the de.uos.fmt.musitech.data.structure.MetricalTimeLine Creation
	 * date: (09.11.2002 12:20:29)
	 * 
	 * @param newMeterTrack de.uos.fmt.musitech.data.structure.MetricalTimeLine
	 */
	public void setMetricalTimeLine(MetricalTimeLine newMeterTrack) {
		metricalTimeLine = newMeterTrack;
	}

	/**
	 * Returns the metricalTimeLine.
	 * 
	 * @return MetricalTimeLine
	 * @hibernate.many-to-one class =
	 *                        "de.uos.fmt.musitech.data.time.MetricalTimeLine"
	 *                        foreign-key = "uid" cascade = "all"
	 */
	public MetricalTimeLine getMetricalTimeLine() {
		return metricalTimeLine;
	}

	/**
	 * Returns the context.
	 * 
	 * @return Context
	 * @hibernate.many-to-one class =
	 *                        "de.uos.fmt.musitech.data.structure.Context"
	 *                        foreign-key = "uid"
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Sets the context.
	 * 
	 * @param argContext The context to set
	 */
	public void setContext(Context argContext) {
		this.context = argContext;
	}

	/**
	 * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isValidValue(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return true; // default
	}

	/**
	 * getUid
	 * 
	 * @see de.uos.fmt.musitech.data.MObject#getUid()
	 * @hibernate.id generator-class="native"
	 */
	@Override
	public Long getUid() {
		return uid;
	}

	/**
	 * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
	 */
	@Override
	public void setUid(Long argUid) {
		this.uid = argUid;
	}

	/**
	 * @return Returns the metaMap.
	 */
	public Map<MObject, MetaDataCollection> getMetaMap() {
		return metaMap;
	}

	/**
	 * @param argMetaMap The metaMap to set.
	 */
	public void setMetaMap(Map<MObject, MetaDataCollection> argMetaMap) {
		this.metaMap = argMetaMap;
	}

	/**
	 * @return the Keys in the Map as a List
	 * @hibernate.list name="mapKeyList" cascade="all" table = "PieceMapKeyList"
	 * @hibernate.collection-key column="piece_id"
	 * @hibernate.collection-index column="position"
	 * @hibernate.collection-many-to-many class =
	 *                                    "de.uos.fmt.musitech.data.MObject"
	 *                                    column="mobject_id"
	 */

	public List<MObject> getMapKeyList() {
		List<MObject> list = new ArrayList<MObject>();
		Set<Map.Entry<MObject, MetaDataCollection>> entries = metaMap
				.entrySet();
		for (Iterator<Map.Entry<MObject, MetaDataCollection>> iter = entries
				.iterator(); iter.hasNext();) {
			Map.Entry<MObject, MetaDataCollection> entry = iter.next();
			list.add(entry.getKey());
		}
		return list;
	}

	/**
	 * The MapValueList is only for persistence.
	 * 
	 * @return the Values of the Map as a List
	 * @hibernate.list name="PieceMapValueList" cascade="all" table =
	 *                 "PieceMapValueList"
	 * @hibernate.collection-key column="piece_id"
	 * @hibernate.collection-index column="position"
	 * @hibernate.collection-many-to-many class =
	 *                                    "de.uos.fmt.musitech.data.metadata.MetaDataCollection"
	 *                                    column="metadata_id"
	 */
	public List<MetaDataCollection> getMapValueList() {
		List<MetaDataCollection> list = new ArrayList<MetaDataCollection>();
		Set<Map.Entry<MObject, MetaDataCollection>> entries = metaMap
				.entrySet();
		for (Iterator<Map.Entry<MObject, MetaDataCollection>> iter = entries
				.iterator(); iter.hasNext();) {
			Map.Entry<MObject, MetaDataCollection> entry = iter.next();
			list.add(entry.getValue());
		}
		return list;
	}

	private List<MObject> mapKeyList;
	private List<MetaDataCollection> mapValueList;

	/**
	 * The mapValueList is only for persistence.
	 * 
	 * @param list
	 */
	public void setMapKeyList(List<MObject> list) {
		mapKeyList = new ArrayList<MObject>();
		mapKeyList.addAll(list);
		restoreMap();
	}

	/**
	 * The mapValueList is only for persistence.
	 * @param list
	 */
	public void setMapValueList(List<MetaDataCollection> list) {
		mapValueList = new ArrayList<MetaDataCollection>();
		mapValueList.addAll(list);
		restoreMap();
	}

	private void restoreMap() {
		metaMap.clear();
		if (mapKeyList == null || mapValueList == null) {
			return;
		}
		for (int i = 0; i < mapKeyList.size(); i++) {
			metaMap.put(mapKeyList.get(i), mapValueList.get(i));
		}
	}

	private static Piece defaultPiece;

	/**
	 * Gets a default Piece instance, is used in the default context. Can also
	 * be used for Testing.
	 * 
	 * @return the default Piece
	 */
	public static Piece getDefaultPiece() {
		if (defaultPiece == null) {
			defaultPiece = new Piece();
		}
		return defaultPiece;
	}

	/**
	 * Gets the harmonyTrack. The HarmonyTrack is a container with all harmonic
	 * information for a piece.
	 * 
	 * @return Returns the harmonyTrack.
	 * @hibernate.many-to-one class = "de.uos.fmt.musitech.data.MObject"
	 *                        foreign-key = "uid" cascade = "all"
	 */
	public SortedContainer<Marker> getHarmonyTrack() {
		return harmonyTrack;
	}

	/**
	 * Sets the harmonyTrack, only for persistence.
	 * 
	 * @param argHarmonyTrack The harmonyTrack to set.
	 */
	public void setHarmonyTrack(SortedContainer<Marker> argHarmonyTrack) {
		this.harmonyTrack = argHarmonyTrack;
	}

	/**
	 * Get the score for this piece as an object of class NotationSystem.
	 * 
	 * @return The NotationSystem object.
	 */
	public NotationSystem getScore() {
		if(score == null)
			score = new NotationSystem(this);
		return score;
	}

	/**
	 * Set the score for this piece. Other properties of this object are not
	 * automatically updated.
	 * 
	 * @param argScore The new score for this piece.
	 */
	public void setScore(final NotationSystem argScore) {
		this.score = argScore;

	}
	
	/**
	 * Creates a score in this piece from the container argument 
	 * of this method. A previous score in this piece will be deleted. 
	 * 
	 * @param cont
	 */
	public void setScore(final Container<?> cont) {
		this.score = NotationSystem.createNotationSystem(cont);
	}

	/**
	 * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(org.w3c.dom.Node,
	 *      java.lang.Object, java.util.Hashtable, org.w3c.dom.Document,
	 *      java.lang.String)
	 */
	@Override
	public boolean toMPEG(MusiteXMLSerializer instance, Node parent,
							Object object, String fieldname) {

		// create 'piece' element

		Element piece = XMLHelper.addElement(parent, "piece");
		if (instance.knowsObject(piece, object))
			return true;

		// rember context
		instance.setContext(this.getContext());

		// metadata
		MPEG_SMR_Tools.serializeMetaData(instance, piece, this);

		// serialize children

		boolean success = true;

		success &= instance.writeXML(piece, getScore(), null, null);
		Container<Container<?>> selectionCont = getSelectionPool();
		if (selectionCont != null) {
			Element selections = XMLHelper.addElement(piece, "selections");
			for (Container<?> container : selectionCont) {
				MPEG_SMR_Tools.addSymbolicSelection(instance, selections,
					container);
			}
		}
		// Node audioPool = XMLHelper.addElement(piece, "audioPool");
		// success &= instance.writeXML(audioPool, getAudioPool(), null, null);

		return success;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
	 *      org.w3c.dom.Element, java.util.Hashtable, java.lang.Object)
	 */
	@Override
	public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
		// reference-handling
		Object reference = instance.getReferenced(node, this);
		if (reference != null)
			return reference;

		instance.setContext(this.getContext());

		// remember context
		// metadata
		MPEG_SMR_Tools.deserializeMetaData(instance, node, this);

		// deserialize score

		Object objScore = instance.readXML(XMLHelper.getElement(node, "score"),
			NotationSystem.class);
		this.setScore((NotationSystem) objScore);
		this.getContainerPool().add(this.getScore());
		new ScoreToPerfomance(this.getScore());
		/*
		 * deserialize selections
		 */
		Element sels = XMLHelper.getElement(node, "selections");
		if (sels != null) {
			BasicContainer<Container<?>> mtSels = new BasicContainer<Container<?>>();
			Element[] selections = XMLHelper.getChildElements(sels,
				"symbolicSelection");
			for (int i = 0; i < selections.length; i++) {
				mtSels.add(MPEG_SMR_Tools.getSymbolicSelection(instance,
					selections[i]));
			}
			this.setSelectionPool(mtSels);
		}
		return this;
	}

	/**
	 * @return Returns the selectionPool.
	 */
	public Container<Container<?>> getSelectionPool() {
		return selectionPool;
	}

	/**
	 * @param argSelectionPool The selectionPool to set.
	 */
	public void setSelectionPool(Container<Container<?>> argSelectionPool) {
		this.selectionPool = argSelectionPool;
	}

	/**
	 * generates the Score from Container pool. Fills the Score with the
	 * NotationSystem generated by NotationDisplay.createNotationSystem(...)
	 * 
	 * @see de.uos.fmt.musitech.score.NotationDisplay
	 */
	public void generateScore() {
		this.setScore(NotationDisplay.createNotationSystem(this));
	}
	
	public NotationSystem createNotationSystem(){
		NotationSystem nsys = new NotationSystem(getContext());
		setScore(nsys);
		return nsys;
	}

}
