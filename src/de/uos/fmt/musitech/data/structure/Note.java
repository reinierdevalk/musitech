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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.rendering.RenderingSupported;
import de.uos.fmt.musitech.data.score.CustomSVGGraphic;
import de.uos.fmt.musitech.data.score.Interval;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.score.TablatureNote;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.form.FormType;
import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.utility.EqualsUtil;
import de.uos.fmt.musitech.data.utility.IEquivalence;
import de.uos.fmt.musitech.framework.editor.Editable;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * Note represents a musical note. It is divided into a PerformanceNote
 * containing data from performance (velocity, timing), and a ScoreNote
 * representing information as written in a score. The ScoreNote may be missing.
 * The PerformanceNote is needed for the TimeStamp.
 * 
 * @version $Revision: 8542 $, $Date: 2013-08-20 20:32:45 +0200 (Tue, 20 Aug 2013) $
 * @author Tillman Weyde and Martin Gieseking
 * 
 * @hibernate.class table="Note"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 * 
 *  
 */
public class Note implements MObject, java.io.Serializable, Containable, Metrical, Editable, Cloneable, Timed,
        RenderingSupported, IMPEGSerializable, IEquivalence, FormType {

    private ScoreNote scoreNote;

    private PerformanceNote performanceNote;

    private RenderingHints renderingHints;

    /**
     * Constructor taking a ScoreNote and a PerformanceNote.
     * 	
     * @param argScoreNote The score note.
     * @param srcPerformanceNote The performance note.
     */
    public Note(ScoreNote argScoreNote, PerformanceNote srcPerformanceNote) {
        this.scoreNote = argScoreNote;
        this.performanceNote = srcPerformanceNote;
    }

    @Override
	public Object clone() throws CloneNotSupportedException {
        Note c = (Note) super.clone();
        if (scoreNote != null) {
            c.scoreNote = (ScoreNote) scoreNote.clone();
        }
        if (performanceNote != null) {
            c.performanceNote = (PerformanceNote) performanceNote.clone();
        }
        return c;
    }

    /**
     * Gets the ScoreNote for this Note.
     * 
     * @return de.uos.fmt.musitech.data.structure.ScoreNote
     * 
     * @hibernate.many-to-one name = "ScoreNote" class =
     *                        "de.uos.fmt.musitech.data.score.ScoreNote"
     *                        foreign-key = "uid" cascade = "all"
     *  
     */
    public ScoreNote getScoreNote() {
        return scoreNote;
    }

    /**
     * Sets the ScoreNote for this Note.
     * 
     * @param newScoreNote
     *            de.uos.fmt.musitech.data.structure.ScoreNote
     */
    public void setScoreNote(ScoreNote newScoreNote) {
        scoreNote = newScoreNote;
    }

    /**
     * Gets the perfomace note of this note. May be null indcating a rest which
     * is not performed.
     * 
     * @return de.uos.fmt.musitech.data.structure.PerformanceNote the
     *         performance note.
     * 
     * @hibernate.many-to-one class =
     *                        "de.uos.fmt.musitech.data.performance.PerformanceNote"
     *                        foreign-key = "uid" cascade = "all"
     *  
     */
    public PerformanceNote getPerformanceNote() {
        return performanceNote;
    }

    /**
     * Get the PerformanceNote of this note as a MidiNote, performing a
     * conversion if necessary.
     * 
     * @return MidiNote
     *  
     */
    public MidiNote midiNote() {
        performanceNote = MidiNote.convert(performanceNote);
        return (MidiNote) performanceNote;
    }

    /**
     * Sets the performance note.
     * 
     * @param newPerformanceNote
     *            de.uos.fmt.musitech.data.structure.MPerformanceNote
     */
    public void setPerformanceNote(PerformanceNote newPerformanceNote) {
        performanceNote = newPerformanceNote;
    }

    /**
     * Default constructor creates a note with zero onset time. Creation date:
     * (11.02.2002 16:41:35)
     */
    public Note() {
        ;
    }

    /**
     * Default constructor creates a note with time stamp zero. Creation date:
     * (11.02.2002 16:41:35)
     */
    public Note(PerformanceNote pn) {
        this.setPerformanceNote(pn);
    }

    /**
     * Constructs a Note with only a scoreNote with pitch onset duration.
     * 
     * @param pitch The pitch of this note. 
     * @param onset The metrical onset time.
     * @param duration The metrical duration.
     */
    public Note(ScorePitch pitch, Rational onset, Rational duration) {
        this.scoreNote = new ScoreNote(pitch, duration);
        scoreNote.setMetricTime(onset);
    }

    public Note(ScoreNote note) {
        this.scoreNote = note;
    }
//    /**
//     * Constructs a Note from a scoreNote.
//     * 
//     * @param pitch
//     * @param duration
//     */
//    public Note(ScorePitch pitch, Rational duration) {
//        this.scoreNote = new ScoreNote(pitch, duration);
//    }

    /**
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricTime()
     */
    @Override
	public Rational getMetricTime() {
        if (getScoreNote() == null)
            return Rational.ZERO;
        else
            return getScoreNote().getMetricTime();
    }

    @Override
	public String toString() {
        //return scoreNote.toString() + "\t" + performanceNote.toString();
        //		return scoreNote.toString() + " " + (performanceNote == null ? "" :
        // performanceNote.toString());
        return (scoreNote == null ? "" : scoreNote.toString()) + " "
               + (performanceNote == null ? "" : performanceNote.toString());
    }

//    /**
//     * 
//     * @see java.lang.Object#equals(java.lang.Object)
//     */
//    public boolean equals(Object obj) {
//        if (!(obj instanceof Note)) {
//            return false;
//        } // end if name instanceof type) )
//        Note note = (Note) obj;
//        boolean scoreEquals = false;
//        if (scoreNote != null && note.scoreNote != null)
//            scoreEquals = scoreNote.equals(note.scoreNote);
//        else
//            scoreEquals = (scoreNote == null && note.scoreNote == null);
//
//        boolean perfEquals = false;
//        if (performanceNote != null && note.performanceNote != null)
//            perfEquals = performanceNote.equals(note.performanceNote);
//        else
//            perfEquals = (performanceNote == null && note.performanceNote == null);
//
//        return scoreEquals && perfEquals;
//    }

    @Override
	public EditingProfile getEditingProfile() {

        EditingProfile scoreNoteProfile = new ScoreNote().getEditingProfile();
        scoreNoteProfile.setPropertyName("scoreNote");

        EditingProfile performanceNoteProfile = new PerformanceNote().getEditingProfile();
        performanceNoteProfile.setPropertyName("performanceNote");

        return new EditingProfile("Note", new EditingProfile[] {scoreNoteProfile, performanceNoteProfile});

    }

    /**
     * @see de.uos.fmt.musitech.data.time.Timed#getTime()
     */
    @Override
	public long getTime() {
        if (getPerformanceNote() == null) {
            return Timed.INVALID_TIME;
        }

        // TODO generate PerformanceNote
        return getPerformanceNote().getTime();
    }

    /**
     * Set the time of the contained PerformanceNote.
     * 
     * @param time
     *            The time to set in microseconds.
     * @return true if the object has been changed, false if not (i.e.
     *         PerformanceNote is null).
     */
    public boolean setTime(long time) {
        if (getPerformanceNote() != null) {
            getPerformanceNote().setTime(time);
            return true;
        }
        return false;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricDuration()
     */
    @Override
	public Rational getMetricDuration() {
        return scoreNote.getMetricDuration();
    }

    /**
     * Convenience method for geting the numeric (MIDI) pitch of this note.
     * 
     * @return The pitch as in MIDI,
     */
    public int getMidiPitch() {
        if (getPerformanceNote() == null)
            return 0;
        return getPerformanceNote().getPitch();
    }

    /**
     * Convenience method for geting the numeric (MIDI) pitch of this note.
     * 
     * @return The pitch as in MIDI,
     */
    public int getVelocity() {
        if (performanceNote == null)
            return 0;
        return getPerformanceNote().getVelocity();
    }

    /**
     * Get the duration of this note, which is taken from its performance 
     * note and 0 if that does not exist.
     * 
     * @see de.uos.fmt.musitech.data.time.Timed#getDuration()
     */
    @Override
	public long getDuration() {
        if (getPerformanceNote() != null) {
            return getPerformanceNote().getDuration();
        } else
            return 0;

    }

    @Override
	public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    @Override
	public void setRenderingHints(RenderingHints renderingHints) {
        this.renderingHints = renderingHints;
        if(renderingHints != null	)
        renderingHints.setParentClass(this.getClass());
    }

    public void setPitch(int pitch) {
        if (performanceNote != null) {
            performanceNote.setPitch(pitch);
        }
    }

    public void addRenderingHint(String key, Object value) {
        if (renderingHints == null)
            renderingHints = new RenderingHints();
        renderingHints.registerHint(key, value);
    }

    public Object getRenderingHint(String key) {
        if (renderingHints == null)
            return null;
        return renderingHints.getValue(key);
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     */
    private Long uid;

    @Override
	public Long getUid() {
        return uid;
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
     */
    @Override
	public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
     *      java.lang.Object)
     */
    @Override
	public boolean isValidValue(String propertyName, Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    @Override
	public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        Note note = (Note) object;
        //TODO ??? Unterschiedliche Serialisierung Note wenn Kind von (1.)
        // /piece/score oder (2.) und /piece/selections ...

        Element head = XMLHelper.addElement(parent, "head");
        // metadata
        MPEG_SMR_Tools.serializeMetaData(instance, head, this);
        // rendering hints
        instance.writeXML(head, getRenderingHints(), null, null);

        // reference Note
        if (instance.knowsObject(head, object)) {
            // return true;
            //    TODO ueerprueen: im MPEG-xml sind Noten redundant;
            //    Wird die Methode hier nicht verlassen, so werden die heads
            //    ausgeschrieben und trotzdem ref-tags vergeben
        }
        // reference ScoreNote
        instance.knowsObject(head, object);

        
        // score-pitch

        MPEG_SMR_Tools.setPitch(head, note.getScoreNote());
        MPEG_SMR_Tools.addAlter(head, note.getScoreNote());

        int detune = getScoreNote().getDetune();
        if (detune != 0)
            head.setAttribute("detune", "" + detune);
        Rational delay = getScoreNote().getAudioDelay();
        if(delay!=null)
            head.setAttribute("audioDelay",delay.getNumer()+"/"+delay.getDenom());
            
        // tied-note
        ScoreNote tiedNote = note.getScoreNote().getTiedNote();
        if (tiedNote != null) {
            Element tie = XMLHelper.getOrCreateElement(head, "tie");
            tie.setAttribute("begin", "true");
            while (tiedNote.getTiedNote() != null) {
                // get recursivly next tied til tiedNote is last note in tie
                tiedNote = tiedNote.getTiedNote();
            }
            tiedNote.lastInTie = true;
        }
        if (getScoreNote().lastInTie) {
            Element tie = XMLHelper.getOrCreateElement(head, "tie");
            tie.setAttribute("end", "true");
        }

        instance.writeXML(head, note.getPerformanceNote(), "performanceNote", null);

        return true;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(org.w3c.dom.Element,
     *      java.util.Hashtable, java.lang.Object)
     */
    @Override
	public Object fromMPEG(MusiteXMLSerializer instance, Element head) {
        // reference-handling
        Object reference = instance.getReferenced(head, this);
        if (reference != null)
            return reference;
        //metadata
        MPEG_SMR_Tools.deserializeMetaData(instance, head, this);
        // rendering hints
        RenderingHints rhints = MPEG_SMR_Tools.deserializeRenderingHints(instance, head);
        if (rhints != null)
            this.setRenderingHints(rhints);

        // performanceNote
        Element pnote = (Element) head.getElementsByTagName("performanceNote").item(0);
        if (pnote != null) {
            this.setPerformanceNote((PerformanceNote) instance.readXML(pnote, PerformanceNote.class));
        }

        // create ScoreNote
        ScoreNote scoreNote = MPEG_SMR_Tools.getScoreNote(head);
        this.setScoreNote(scoreNote);
        MPEG_SMR_Tools.deserializeMetaData(instance, head, scoreNote);

        // tiedNote
        Element tie = XMLHelper.getElement(head, "tie");
        if (tie != null) {
            if (tie.hasAttribute("begin"))
                getScoreNote().firstInTie = "true".equals(tie.getAttribute("begin"));
            if (tie.hasAttribute("end"))
                getScoreNote().lastInTie = "true".equals(tie.getAttribute("end"));
        }

        // tabulature
        TablatureNote tn = (TablatureNote) instance.readXML(XMLHelper.getElement(head, "tablature"),
                                                            TablatureNote.class);
        if (tn != null)
            this.addRenderingHint("tabulatur note", tn);

        // custom SVGGraphic
        Element svg = XMLHelper.getElement(XMLHelper.getElement(XMLHelper.getElement(head, "drawObjects"), "drawObj"),
                                           "svg");
        if (svg != null) {
            CustomSVGGraphic graphic = MPEG_SMR_Tools.deserializeSVG(svg,instance.getBaseURI());
            this.addRenderingHint("custom head", graphic);
        }

        return this;
    }
    
    /**
     * Returns true of this note is higher in pitch than the 
     * specified note. False is returned when this note is either
     * of equal pitch as the specified note or lower than the
     * specified note.
     *  
     * @param note Note which this note is compared to
     * @return boolean true if this note is higher in pitch than the specified note, false otherwise
     */
    public boolean isHigher(Note note){
    	return scoreNote.getPitch().isHigher(note.getScoreNote().getPitch());
    }
    
    /**
     * Returns true of this note is lower in pitch than the 
     * specified note. False is returned when this note is either
     * of equal pitch as the specified note or higher than the
     * specified note.
     * 
     * @param note Note which this note is compared to
     * @return boolean true if this note is lower in pitch than the specified note, false otherwise
     */
    public boolean isLower(Note note){
    	return scoreNote.getPitch().isLower(note.getScoreNote().getPitch());
    }
    
    
    /** Checks two IEquality objects on equivalences. Two note are equivalent, iff all their properties are equal.
     * 
     * @param IEquivalence note
     * @return boolean
     * @throws Exception 
     */
    @Override
	public boolean isEquivalent(IEquivalence object) {
    	if( !(object instanceof Note) ) return false;
    	Method[] cmNote = object.getClass().getMethods();
    	for(int i=0; i < cmNote.length; ++i) {
    		String methodName = cmNote[i].getName();
    		if(methodName.startsWith("get") && !"getUid".equals(methodName) && !( methodName.startsWith("getMetric") ) )
				try {
					Class type = cmNote[i].getReturnType();
					if(type.isPrimitive()) {
						if( !EqualsUtil.areEqual( cmNote[i].invoke(this,null) , cmNote[i].invoke(object, null) ) )
							return false;
					}
					else {
						for(Class c:type.getInterfaces()) {
							boolean isEqualityType = "de.uos.fmt.musitech.data.utility.IEquality".equals( c.getName());
							if(  isEqualityType && !EqualsUtil.areEqual( (IEquivalence) cmNote[i].invoke(this, null) , (IEquivalence) cmNote[i].invoke(object, null) ) ) {
								return false;
							}
							/*else if ("de.uos.fmt.musitech.data.time.Metrical".equals( c.getName() )
										|| "java.io.Serializable".equals( c.getName()) 
										|| "de.uos.fmt.musitech.data.structure.container.Containable".equals(c.getName())
										|| "de.uos.fmt.musitech.framework.editor.Editable".equals(c.getName())
										|| "java.lang.Cloneable".equals(c.getName())
										|| "de.uos.fmt.musitech.data.MObject".equals(c.getName()))
								continue;
							else if ( !isEqualityType && !EqualsUtil.areEqual( cmNote[i].invoke(this, null) , cmNote[i].invoke(object, null) ) ) {
								System.out.println("fail on check:" + c.toString());
								return false;
							}*/
						}
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
    	}
    	return true;	
    }    
    
    public void transpose(Interval interv){
    	if(scoreNote != null)
    		scoreNote.transpose(interv);
    	if(performanceNote != null)
    		performanceNote.setPitch(performanceNote.getPitch()+interv.getSemitoneSize());
    }
}