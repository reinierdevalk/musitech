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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uos.fmt.musitech.data.score.mpeg.MPEGContextLine;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.ClefContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * Container for a notation staff. Contains NotationVoice objects.
 * 
 * @version $Revision: 8546 $, $Date: 2013-09-01 02:33:01 +0200 (Sun, 01 Sep 2013) $
 * @author Tillman Weyde
 * 
 * @hibernate.class table="NotationStaff"
 * 
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 *  
 */
public class NotationStaff extends BasicContainer<NotationVoice> implements NotationContainer, IMPEGSerializable {

    private NotationSystem parent;

    private List<IAttachable>  attachers;

    private ClefContainer clefTrack;

    private MetricalTimeLine contextTimeLine;

    /**
     * Gets the contextTimeLine.
     * 
     * @return Returns the contextTimeLine.
     */
    public MetricalTimeLine getContextTimeLine() {
        return contextTimeLine;
    }

    /**
     * Sets the contextTimeLine.
     * 
     * @param contextTimeLine
     *            The contextTimeLine to set.
     */
    public void setContextTimeLine(MetricalTimeLine contextTimeLine) {
        this.contextTimeLine = contextTimeLine;
    }

    /**
     * prepareForScore
     * 
     * @see de.uos.fmt.musitech.data.score.NotationContainer#prepareForScore()
     */
    public void prepareForScore(int pass) {
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof NotationContainer) {
                NotationContainer nc = (NotationContainer) obj;
                nc.prepareForScore(pass);
            } // end if
        } // end for
    }

    double scale = 1.0;

    /*
     * private char clefType = 'g'; private int clefLine = -1; private int
     * transposition = 0;
     */

//    public NotationStaff(Context context, NotationSystem parent) {
//        super(context, NotationVoice.class);
//        this.parent = parent;
//        if (!parent.contains(this))
//            parent.add(this);
//    }

    public NotationStaff(NotationSystem argParent) {
        super(argParent.getContext(), NotationVoice.class);
    	parent = argParent;
		if (!parent.contains(this))
			parent.add(this);
    }

    public NotationStaff() {
        super(new Context(), NotationVoice.class);
    }

    /**
     * This method returns the barlines for this staff
     * 
     * @return the barlines for this staff
     */
    public BarlineContainer getBarlines() {
        return parent.getBarlines();
    }

    /**
     * This method adds a barline to the staff
     * 
     * @param barline
     *            The barline to be added
     * @return 
     */
    public boolean addBarline(Barline barline) {
        return parent.addBarline(barline);
    }

    /**
     * Returns the scale. 1.0 is normal size.
     * 
     * @return double
     * 
     * @hibernate.property
     */
    public double getScale() {
        return scale;
    }

    /**
     * Sets the scale. The scale parameter can be used to make cue-notes.
     * 
     * @param scale
     *            The scale to set
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Returns the clefType. Types are defined by constants in
     * de.uos.fmt.musitech.gui.score.Clef.
     * 
     * @return int
     * 
     * @hibernate.property
     */
    /*
     * public char getClefType() { if (clefTrack == null || clefTrack.size() ==
     * 0) return 'g'; return ((Clef)clefTrack.get(0)).getClefType(); }
     */

    /**
     * @return the center line of the clef. 0 for a centered clef.
     * 
     * @hibernate.property
     */
    /*
     * public int getClefLine() { return clefLine; }
     */

    /**
     * 
     * @return the transpostion for this object. 0 for now transpostion and 1,
     *         -1 for an up or down transpostion.
     * 
     * @hibernate.property
     */
    /*
     * public int getTransposition() { return transposition; }
     */

    @Override
    public boolean add(NotationVoice obj) {
    	if(contains(obj))
    		return false;
    	return super.add(obj);
    }
    
    /**
     * Sets the clefType. Acceptable types are those defined by constants in
     * de.uos.fmt.musitech.gui.score.Clef.
     * 
     * @param clefType
     *            The clefType to set
     */
    public void setClefType(char clefType, int line, int shift) {
        Clef clef = new Clef(clefType, line, shift);
        clef.setMetricTime(Rational.ZERO);
        if (clefTrack == null)
            clefTrack = new ClefContainer(context);
        if (clefTrack.size() > 0 && ((Clef) clefTrack.get(0)).getMetricTime().equals(Rational.ZERO)) {
            clefTrack.remove(clefTrack.get(0));
        }
        clefTrack.add(clef);
    }

    /**
     * Sets the clefType. Acceptable types are those defined by constants in
     * de.uos.fmt.musitech.gui.score.Clef. Additionally you can also pass
     * (char)0 to indicate that this Staff has no clef
     * 
     * @param clefType
     *            clefType of the staff
     */

    public void setClefType(char clefType) {
        setClefType(clefType, 0, 0);
    }

    /**
     * This method returns whether this staff contains a given metric time.
     * 
     * @param metricTime
     * @return
     */
    public boolean containsMetricTime(Rational metricTime) {
        for (Iterator iter = iterator(); iter.hasNext();) {
            NotationVoice voice = (NotationVoice) iter.next();
            if (voice.containsMetricTime(metricTime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * Sets the clefType. Acceptable types are those defined by constants in
     * de.uos.fmt.musitech.gui.score.Clef. Additionally you can also pass
     * (char)0 to indicate that this Staff has no clef
     * 
     * @param clefType
     * @param shift
     */
    public void setClefType(char clefType, int shift) {
        switch (clefType) {
        case 'c':
            setClefType(clefType, 0, shift);
            break;
        case 'g':
            setClefType(clefType, -1, shift);
            break;
        case 'p':
            setClefType(clefType, 0, shift);
            break;
        case 'f':
            setClefType(clefType, 1, shift);
            break;
        }
    }

    /**
     * @return Returns the parent.
     * 
     * @hibernate.many-to-one class =
     *                        "de.uos.fmt.musitech.data.score.NotationSystem"
     *                        foreign-key = "uid" cascade = "all"
     *  
     */
    public NotationSystem getParent() {
        return parent;
    }

    /**
     * @param clefLine
     *            The clefLine to set.
     */
    public void setClefLine(int clefLine) {
        if (clefTrack != null && clefTrack.size() > 0) {
            Clef clef = (Clef) clefTrack.get(0);
            clef.setClefLine(clefLine);
        }
    }

    /**
     * @param parent
     *            The parent to set.
     */
    public void setParent(NotationSystem parent) {
        this.parent = parent;
    }

    /**
     * @param transposition
     *            The transposition to set.
     */
    /*
     * public void setTransposition(int transposition) { this.transposition =
     * transposition; }
     */

    public void prettyPrint(int indent) {
        for (int i = 0; i < indent; i++)
            System.out.print(" ");
        System.out.println("NotationStaff");
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            NotationVoice element = (NotationVoice) iter.next();
            element.prettyPrint(indent + 2);
        }
    }

    public void addAttachable(MetricAttachable ma) {
        if (attachers == null)
            attachers = new ArrayList();
        attachers.add(ma);
        ma.setParent(this);
    }

    public void addAttachable(Attachable a) {
        if (attachers == null)
            attachers = new ArrayList();
        attachers.add(a);
        a.setParent(this);
    }

    public void addClef(Clef clef) {
        if (clefTrack == null)
            clefTrack = new ClefContainer(context);
        clefTrack.add(clef);
    }

    public void setAttachables(List<IAttachable> attachables) {
        this.attachers = attachables;
    }

    public List<IAttachable> getAttachables() {
        return attachers;
    }

    public ClefContainer getClefTrack() {
    	if(clefTrack == null)
    		setClefTrack(new ClefContainer(getContext()));
        return clefTrack;
    }

    public void setClefTrack(ClefContainer clefTrack) {
        this.clefTrack = clefTrack;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.util.Hashtable,
     *      java.lang.String)
     */
    public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        //commons----------------------------------
        Element staff = XMLHelper.addElement(parent, "staff");
        if (instance.knowsObject(staff, object))
            return true;
        // metadata
        MPEG_SMR_Tools.serializeMetaData(instance, staff, this);
        // rendering hints
        instance.writeXML(staff, getRenderingHints(), null, null);
        //-----------------------------------------

        MPEGContextLine contextLine = new MPEGContextLine();
        if (this.getClefTrack() != null)
            for (int i = 0; i < this.getClefTrack().size(); i++) {
                contextLine.add(this.getClefTrack().get(i));
            }
        instance.writeXML(staff, contextLine, null, null);

        Element voices = XMLHelper.addElement(staff, "voices");

        // iterate over voices
        for (Iterator iter = getContent().iterator(); iter.hasNext();) {
            NotationVoice voice = (NotationVoice) iter.next();
            // serialize voice
            instance.setParent(voice, this);
            if (!instance.writeXML(voices, voice, null, null))
                return false;
        }

        return true;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element, java.util.Hashtable, java.lang.Object)
     */
    public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
        //commons----------------------------------
        // reference-handling
        Object reference = instance.getReferenced(node, this);
        if (reference != null)
            return reference;
        //metadata
        MPEG_SMR_Tools.deserializeMetaData(instance, node, this);
        // rendering hints
        this.setRenderingHints(MPEG_SMR_Tools.deserializeRenderingHints(instance, node));
        //-----------------------------------------

        /*
         * context
         */
        MPEGContextLine ctxt = (MPEGContextLine) instance.readXML(XMLHelper.getElement(node, "context"),
                                                                  MPEGContextLine.class);
        if (ctxt != null) {
            this.setClefTrack(ctxt.getClefTrack());
        }
        
        this.setParent((NotationSystem)instance.getParent(node));

        // onset of the NotationStaff same as NotationSystem
        instance.setNextOnset(this, instance.getNextOnset(instance.getParent(node)));
        if (DebugState.DEBUG_MPEG)
            System.out.println("Staff onset: " + instance.getNextOnset(this));

        Element voices = (Element) node.getElementsByTagName("voices").item(0);
        NodeList voiceList = voices.getElementsByTagName("voice");
        // loop over voice
        for (int iVoice = 0; iVoice < voiceList.getLength(); iVoice++) {
            Element voice = (Element) voiceList.item(iVoice);
            // deserialize voice
            instance.setParent(voice, this);
            NotationVoice mtVoice = (NotationVoice) instance.readXML(voice, NotationVoice.class);
            this.add(mtVoice);
        }

        return this;
    }

    /**
     * MPEG-Deserialization: reunite a following segment with the Staff
     * 
     * @author Jens
     */
    public void addStaffSegment(NotationStaff staff) {
        //TODO compute relevant semantic information
        // merge voices
        assert (this.getContent().size() == staff.getContent().size());
        Iterator iterThis = this.getContent().iterator();
        Iterator iterNext = staff.getContent().iterator();
        while(iterThis.hasNext()){
            NotationVoice voiceThis = (NotationVoice) iterThis.next();
            NotationVoice voiceNext = (NotationVoice) iterNext.next();
            voiceThis.addVoiceSegment(voiceNext);
        }
        ClefContainer clefTrk = staff.getClefTrack();
        if (clefTrk != null){
            for (int i = 0; i < clefTrk.size(); i++) {
                this.addClef((Clef) clefTrk.get(i));
            }
        }
        // merge context
        if(staff.getContextTimeLine()!= null){
            if(this.getContextTimeLine() != null ){
                this.getContextTimeLine().addAll(staff.getContextTimeLine()); 
            } else {
                this.setContextTimeLine(staff.getContextTimeLine());
            }
        }
        // merge attachables
        if(staff.getAttachables()!= null){
            if(this.getAttachables() != null ){
                this.getAttachables().addAll(staff.getAttachables()); 
            } else {
                this.setAttachables(staff.getAttachables());
            }
        }
    }

    /**
     * @return
     */
    public Rational getEndtime() {
        if (this.size() <= 0)
            return Rational.ZERO;
        Rational endTime = Rational.ZERO;
        for (Iterator i = getContent().iterator(); i.hasNext();) {
			NotationVoice nv = (NotationVoice) i.next();
			Rational nvet = nv.getEndTime();
			if(nvet.isGreater(endTime))
				endTime = nvet;
		}
        return endTime;
    }

    /**
     * @return
     */
    public Rational getMetricTime() {
        Rational start = null;
        for (Iterator iter = getContent().iterator(); iter.hasNext();) {
            NotationVoice voice = (NotationVoice) iter.next();
            Rational voiceStart = voice.getMetricTime();
            if (start == null)
                start = voiceStart;
            else if (voiceStart.isLess(start))
                start = voiceStart;
        }
        return start;
    }

    /**
     * get all IAttachables that are children of the given object
     * 
     * @param anchor
     * @return
     */
    public List<IAttachable> getAttachables(Object anchor) {
        List<IAttachable> all = getAttachables();
        List<IAttachable> children = new ArrayList<IAttachable>();
        if (all == null)
            return children;
        for (int i = 0; i < all.size(); i++) {
        	IAttachable attachable = all.get(i);
            if (attachable instanceof Attachable) {
                Object attAnker = ((Attachable) attachable).getAnker();
                if (attAnker != null && attAnker.equals(anchor))
                    children.add(attachable);
            } else if (attachable instanceof MetricAttachable) {
                Object attAnker = ((MetricAttachable) attachable).getAnker();
                if (attAnker != null && attAnker.equals(anchor))
                    children.add(attachable);
            } else {
                assert false;
            }
        }
        return children;
    }
    
    public void processDynamics(){
        Container harmoContext = getMergedHarmonyContext();
               
        for(Iterator iter = harmoContext.iterator(); iter.hasNext(); ){
            Object obj = iter.next();
            if (obj instanceof DynamicsLevelMarker) {
                DynamicsLevelMarker dynMark = (DynamicsLevelMarker) obj;
        		MetricAttachable ma = new MetricAttachable(dynMark, new CharSymbol(dynMark.getMusicGlyph()));
        		ma.setRelativePosition(MetricAttachable.SOUTH);
        		ma.setAlignment(MetricAttachable.CENTER);
        		ma.setDistance(2);
        		ma.setRenderingHints(dynMark.getRenderingHints());
        		ma.setGenerated(true);
        		addAttachable(ma);
            }
//            if (obj instanceof DynamicsMarker) {
//                DynamicsMarker dynMark = (DynamicsMarker) obj;
//        		MetricAttachable ma = new DualMetricAttachable(dynMark, new CharSymbol(dynMark.getMusicGlyph()));
//        		ma.setRelativePosition(MetricAttachable.SOUTH);
//        		ma.setAlignment(MetricAttachable.CENTER);
//        		ma.setDistance(0);
//        		ma.setRenderingHints(dynMark.getRenderingHints());
//        		getParent().addAttachable(ma);
//            }
        }
        for (Iterator iter = iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof NotationVoice) {
                NotationVoice voice = (NotationVoice) element;
                voice.processDynamics();
            }
            
        }
    }

    public void processHarmony(){
        Container harmoContext = getMergedHarmonyContext();
               
        for(Iterator iter = harmoContext.iterator(); iter.hasNext(); ){
            Object obj = iter.next();
            if (obj instanceof DynamicsLevelMarker) {
                DynamicsLevelMarker dynMark = (DynamicsLevelMarker) obj;
        		MetricAttachable ma = new MetricAttachable(dynMark, new CharSymbol(dynMark.getMusicGlyph()));
        		ma.setRelativePosition(MetricAttachable.SOUTH);
        		ma.setAlignment(MetricAttachable.CENTER);
        		ma.setDistance(0);
        		ma.setRenderingHints(dynMark.getRenderingHints());
        		addAttachable(ma);
            }
        }
    }

    /**
     * Gets the merged content of this voices context time line, the staffs context line and the harmony track.
     */
    public Container getMergedHarmonyContext() {
        Container cont = new SortedContainer(context, Marker.class, new MetricalComparator());
        if(getContextTimeLine() != null)
            cont.addAll(getContextTimeLine());
        cont.addAll(getContext().getPiece().getHarmonyTrack());
        return cont;
    }

    /**
     * TODO add comment
     * 
     */
    public void generateBeams() {
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof NotationVoice) {
                NotationVoice nc = (NotationVoice) obj;
//                nc.generateBeams();
            } // end if

        } // end for

        
    }

    /**
     * Checks if this staff contains multilingual lyrics.
     * 
     * @return true if there are multilingual lyrics in this staff.
     */
    public Set<Locale> getLyricsLanguages() {
        Set<Locale> langSet = new LinkedHashSet<Locale>();
        for (Iterator iter = iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof NotationVoice) {
                NotationVoice voice = (NotationVoice) obj;
                if(voice.getLyrics() != null){
                    Locale locales[] = voice.getLyrics().getLanguages();
                    for (int i = 0; i < locales.length; i++) {
                        langSet.add(locales[i]);
                    }
                }
            }
        }
        
        return langSet;
    }
    
    /**
     * Creates beams in all voices in this staff. Old beams are deleted.
     */
    public void createBeams(){
    	for (Iterator<NotationVoice> i = iterator(); i.hasNext();)
			i.next().createBeams();
    }
}