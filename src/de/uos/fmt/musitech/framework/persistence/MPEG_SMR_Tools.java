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
/*
 * Created on 29.12.2004
 */
package de.uos.fmt.musitech.framework.persistence;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.CustomSVGGraphic;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.TupletContainer;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.MyMath;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * Utility class for reading and writing MPEG-SMR.
 * 
 * @author Jens Wissmann, Tillman Weyde
 */
public class MPEG_SMR_Tools {

    /**
     * Reads the duration from a Duration element, which appears in Chord and
     * Rest elements. TODO This needs more information to calculate durations
     * expressed in whole bars.
     * 
     * @param durElem
     *            The duration element to read from.
     * @return The duration as a rational.
     */
    public static Rational getDuration(Element durElem) {
        String base = durElem.getAttribute("base");
        StringTokenizer st = new StringTokenizer(base, "/");
        int numer = 1;
        if (st.hasMoreTokens()) {
            numer = Integer.parseInt(st.nextToken());
        } else {
            if (DebugState.DEBUG_MPEG)
                System.out.println("Warning: MPEG_SMR_Tools.getDuration() numerator is missing, using 1 instead.");
        }
        int denom = 1;
        if (st.hasMoreTokens()) {
            denom = Integer.parseInt(st.nextToken());
            // TODO correct treatment for measures with duration different from
            // one.
        } else {
            System.out
                    .println("Warning: MPEG_SMR_Tools.getDuration() denominator is missing, correction needed for time signatures with durations other than 1.");
        }
        Rational duration = new Rational();
        duration.setNumer(numer);
        duration.setDenom(denom);
        String dotStr = durElem.getAttribute("dots");
        int dots;
        try {
            dots = Integer.parseInt(dotStr);
        } catch (Exception e) {
            // no valid dots information
            dots = 0;
        }
        Rational dotted = duration;
        int div = 2;
        for (int i = 1; i <= dots; i++) {
            // add duration/(2^i) to dotted
            dotted = dotted.add(duration.div(div));
            div = div * 2;
        }
        return dotted;
    }

    /**
     * Reads the tuplet information from the xml element duration and creates
     * the tuplet information in the voice and chord. Requires, that the notes
     * in the chord are already created and have the correct duration.
     * 
     * @param durationElem
     *            The xml duration element to read.
     * @param voice
     *            The voice, to which the chord belongs.
     * @param chord
     *            The chord, to which the tuplet information belongs.
     * @return The effective duration of the note considering tuplet
     *         information.
     */
    public static Rational readTupletInformation(Element durationElem, NotationVoice voice, NotationChord chord) {
        NodeList nodeList = durationElem.getChildNodes();
        Element tupElem = XMLHelper.getFirstElementChild(durationElem);
        //        if (nodeList != null && nodeList.getLength() > 0) {
        //            tupElem = (Element) nodeList.item(0);
        //        } else {
        if (tupElem == null)
            return chord.getSingleMetricDuration();
        //        }
        int count = Integer.parseInt(tupElem.getAttribute("count"));
        boolean isTripartite = Boolean.valueOf(tupElem.getAttribute("count")).booleanValue();
        boolean isProlong = Boolean.valueOf(tupElem.getAttribute("prolong")).booleanValue();
        int denom;
        try {
            denom = Integer.parseInt(tupElem.getAttribute("denom"));
        } catch (NumberFormatException e) {
            denom = -1;
        }
        if (denom == -1) {
            if (!isProlong) {
                if (!isTripartite) {
                    denom = MyMath.floorPower2(count);
                } else {
                    // TODO check this
                    int countBase2 = count / 3 * 2;
                    denom = MyMath.floorPower2(countBase2);
                    denom = denom * 3 / 2;
                }
            } else {
                if (!isTripartite) {
                    denom = MyMath.ceilPower2(count);
                } else {
                    // TODO check this
                    int countBase2 = count / 3 * 2;
                    denom = MyMath.ceilPower2(countBase2);
                    denom = denom * 3 / 2;
                }
            }
        }
        Rational tupletRatio = new Rational(count, denom);
        //        int prevIndex = voice.indexOf(chord) - 1;
        int prevIndex = voice.size() - 1;
        NotationChord prevChord = null;
        if (prevIndex >= 0 && prevIndex < voice.size()) {
            prevChord = (NotationChord) voice.get(prevIndex);
        }
        TupletContainer tupCont = voice.belongsToTupletContainer(prevChord);
        String groupStr = tupElem.getAttribute("count");

        if (tupCont == null || groupStr.equals("end")
            || (tupCont.size() == tupCont.getArity() && !groupStr.equals("continue"))) {
            tupCont = new TupletContainer(voice.getContext(), (byte) count, (byte) denom);
            voice.getTupletContainers().add(tupCont);
        }
        // add the first not of the cord to the TupletContainer.
        tupCont.add(chord.get(0));
        for (int i = 0; i < chord.size(); i++) {
            Note note = (Note) chord.get(i);
            note.getScoreNote().setTupletDivision(tupletRatio);
        }
        tupCont.calcMetricDuration();

        return chord.getSingleMetricDuration().div(tupletRatio);
    }

    /**
     * Writes the Duration element into a Chord or Rest element. TODO tuplet
     * information
     * 
     * 
     * @param noteElement
     *            The chord or rest to write to.
     * @param chord
     *            The element
     * @param voice
     * @return The duration element that has been created and added to the
     *         noteElement.
     */
    public static Element addDuration(Element noteElement, NotationChord chord, NotationVoice voice) {
        Rational duration = chord.getSingleMetricDuration();
        Document doc = noteElement.getOwnerDocument();
        Element durElem = doc.createElement("duration");
        durElem.setAttribute("base", duration.getFloorPower2().toString());
        int dots = numberOfDots(duration);
        if (dots > 0) {
            durElem.setAttribute("dots", Integer.toString(dots));
        }
        TupletContainer tupCont = voice.belongsToTupletContainer(chord);
        if (tupCont != null) {
            Element tupletElem = doc.createElement("tuplet");
            tupletElem.setAttribute("count", Integer.toString(tupCont.getArity()));
            tupletElem.setAttribute("denom", Integer.toString(tupCont.getRegular()));
            durElem.appendChild(tupletElem);
        }
        noteElement.appendChild(durElem);
        return durElem;
    }

    /**
     * Writes the Duration element into a Chord or Rest element. TODO needs to
     * be done, where triplet information is available, or with additional
     * argument.
     * 
     * @param noteObject
     *            The chord or rest to write to.
     * @param duration
     *            The duration to write.
     */
    public static void addDuration(Element noteObject, Rational duration) {
        Document doc = noteObject.getOwnerDocument();
        Element durElem = doc.createElement("duration");
        durElem.setAttribute("base", duration.getFloorPower2().toString());
        int dots = numberOfDots(duration);
        if (dots > 0) {
            durElem.setAttribute("dots", Integer.toString(dots));
        }
        noteObject.appendChild(durElem);
    }

    /**
     * Class to represent duration information as in MPEG_SMR
     */
    class Duration {

        Rational base;
        int dots;
        Rational triplet;
    }

    /**
     * Get the number of dots this duration has.
     * 
     * @param dur
     *            The duration to inspect.
     * @return The number of dots.
     */
    public static int numberOfDots(Rational dur) {
        dur.reduce();
        int num = dur.getNumer();
        int dots = 0;
        int len = 1;
        while (num > len) {
            len = 2 * len + 1;
            dots++;
        }
        if (num != len) {
            System.out
                    .println("WARNING MPEG_SMR_Tools.numberOfDots(Rational): numerator is not sptlit correctly to fit into dotted note.");
        }
        return dots;
    }

    /**
     * Splits a rational, that has a power of two as denominator, into
     * durations, that can be displayed as notes.
     * 
     * @param dur
     *            The duration to split.
     * @param maxDots
     *            The maximal number of dots to use.
     * @return A List of Rationals that should be used to display this note with
     *         tied notes.
     */
    public static List splitDualDurations(Rational dur, int maxDots) {
        if (!MyMath.isPowerOf2(dur.getDenom())) {
            throw new Error("MPEG_SMR_Tools.splitDualDurations() arg dur must have power of two as denominator. ");
        }
        List durList = new ArrayList();
        Rational rest = dur;
        do {
            Rational dottedDur = getDotted(dur, maxDots);
            rest = rest.sub(dottedDur);
            durList.add(dottedDur);
        } while (rest.isGreater(Rational.ZERO));
        return durList;
    }

    /**
     * Gets the maximal part of the duration, that can be expressed as a note
     * using dots (may be <code>from</code> zero to <code>maxDots</code>
     * dots).
     * 
     * @param dur
     *            The duration to inspect.
     * @param maxDots
     *            The maximal number of dots to use.
     * @return The maximal duration that can be
     */
    public static Rational getDotted(Rational dur, int maxDots) {
        if (!MyMath.isPowerOf2(dur.getDenom())) {
            throw new Error("MPEG_SMR_Tools.splitDualDurations() arg dur must have power of two as denominator. ");
        }
        Rational base = dur.getFloorPower2();
        int dots = -1;
        Rational dotted = new Rational();
        do {
            dotted = dotted.add(base);
            dots++;
            base = base.div(2);
        } while (dur.isGreater(dotted) && dots <= maxDots);

        return dotted; //TODO proper return
    }

    /**
     * converts an MPEG type'Time' String into a TimeSignature
     * 
     * @return TimeSignature
     */
    public static TimeSignature getTimeSignature(String time) {
        int sep = time.indexOf('/');
        if (sep > -1) {
            int numer = new Integer(time.substring(0, sep)).intValue();
            int denom = new Integer(time.substring(sep + 1)).intValue();
            return new TimeSignature(numer, denom);
        } else if (time.equals("infinite")) {
            // TODO ??? correct
            return new TimeSignature(Rational.MAX_VALUE);
        } else if (time.equals("C")) {
            // TODO ??? correct
            return new TimeSignature(4, 4);
        } else if (time.equals("allaBreve")) {
            // TODO ??? correct
            return new TimeSignature(2, 2);
        }

        return null;
    }

    /**
     * sets the onsets of chords/rests in the staff relative to the given first
     * onset
     * 
     * @deprecated
     * 
     * @param musitechStaff
     * @param firstOnset
     *            the onset of the first NotationChord in a voice
     */
    public static void calculateOnsets(NotationStaff musitechStaff, Rational firstOnset) {
        List voiceList = musitechStaff.getContent();
        for (int v = 0; v < voiceList.size(); v++) {
            NotationVoice voice = (NotationVoice) voiceList.get(v);
            // list of chords / rests
            List chordList = voice.getContent();

            // cnset of the first chord/rest in each voice is given by
            Rational onset = firstOnset;

            for (int c = 0; c < chordList.size(); c++) {
                NotationChord chord = (NotationChord) chordList.get(c);
                chord.setMetricTime(onset);
                // calculate onset for next chord given the current onset +
                // duration
                onset = onset.add(chord.getMetricDuration());
            }
        }

    }

    /**
     * adds an 'metricTime' element to a parent-element
     * 
     * @param parent
     *            the parent elementc
     * @param metricTime
     */
    public static void addMetricTime(Element parent, Rational metricTime) {
        Element mtElement = (Element) parent.appendChild(parent.getOwnerDocument().createElement("metricTime"));
        mtElement.setAttribute("numerator", "" + metricTime.getNumer());
        mtElement.setAttribute("denominator", "" + metricTime.getDenom());
    }

    /**
     * gets the slur element from a noteObject
     * 
     * @param noteObject
     * @return slur element or null
     */
    public static Element getSlurElement(Element noteObject) {
        Element drawObjects = XMLHelper.getElement(noteObject, "drawObjects");
        if (drawObjects == null)
            return null;
        NodeList drawObjList = drawObjects.getElementsByTagName("drawObj");
        for (int i = 0; i < drawObjList.getLength(); i++) {
            Element drawObj = (Element) drawObjList.item(i);
            return XMLHelper.getElement(drawObj, "slur");
        }
        return null;
    }

    /**
     * adds an <code>element</code> to the parent
     * 
     * @param parent
     *            the <code>element</code> to with the new
     *            <code>element</code> is attached
     * @param string
     * @return
     */
    public Element addElement(Node parent, String tagName) {
        return (Element) parent.appendChild(parent.getOwnerDocument().createElement(tagName));
    }

    /**
     * adds metadata about an object to a parent element
     * 
     * @param instance
     *            serializer
     * @param parent
     *            element to attach metadata to
     * @param about
     *            the object that the metadata are about
     */
    public static void serializeMetaData(MusiteXMLSerializer instance, Element parent, MObject about) {
        MetaDataCollection mdc = instance.getContext().getPiece().getMetaData(about);
        if (mdc == null) // no metadata about object
            return;

        Element metaData = XMLHelper.getOrCreateElement(parent, "metaData");
        Set keys = mdc.getDataMap().keySet();
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            Object metaDataObj = mdc.getDataMap().get(iter.next());
            assert (metaDataObj instanceof MetaDataItem);
            instance.writeXML(metaData, metaDataObj, null, null);
        }
    }

    /**
     * deserializes the metadata
     * 
     * @param instance
     *            deserializer instance
     * @param parent
     *            parent-element of the metadata element
     * @param about
     *            the object that the metadata are about
     */
    public static void deserializeMetaData(MusiteXMLSerializer instance, Element parent, MObject about) {
        Element metaData = XMLHelper.getElement(parent, "metaData");
        if (metaData == null)
            return;
        MetaDataCollection mdc = (MetaDataCollection) instance.readXML(metaData, MetaDataCollection.class);
        instance.getContext().getPiece().setMetaData(about, mdc);
    }

    /**
     * @param instance
     * @param node
     * @param staff
     * @return
     */
    public static RenderingHints deserializeRenderingHints(MusiteXMLSerializer instance, Element node) {
        Element rhints = XMLHelper.getElement(node, "renderingHints");
        if (rhints == null)
            return null;
        return (RenderingHints) instance.readXML(rhints, RenderingHints.class);
    }

    /**
     * @param scoreNote
     * @return
     */
    public static void setPitch(Element parent, ScoreNote scoreNote) {
        String pitch = "" + scoreNote.getDiatonic() + (scoreNote.getOctave() + 5);
        parent.setAttribute("pitch", pitch.toUpperCase());
    }

    /**
     * @param head
     * @param scoreNote
     */
    public static void addAlter(Element parent, ScoreNote scoreNote) {
        int acc = scoreNote.getAlteration();
        if (acc != 0) {
            Element alter = XMLHelper.addElement(parent, "alter");
            alter.setAttribute("step", "" + acc);
        }
    }

    public static ScoreNote getScoreNote(Element head) {
        ScoreNote scoreNote = new ScoreNote();
        String pitch = head.getAttribute("pitch");
        assert (pitch.length() >= 2);

        if (head.hasAttribute("detune"))
            scoreNote.setDetune(Integer.parseInt(head.getAttribute("detune")));
        if (head.hasAttribute("audioDelay")) {
            String delay = head.getAttribute("audioDelay");
            int slash = delay.indexOf('/');
            scoreNote.setAudioDelay(new Rational(Integer.parseInt(delay.substring(0, slash)), Integer.parseInt(delay
                    .substring(slash + 1))));
        }

        scoreNote.setDiatonic(pitch.toLowerCase().charAt(0));
        scoreNote.setOctave((byte) (Integer.parseInt(pitch.substring(1)) - 5));
        Node alter = XMLHelper.getElement(head, "alter");
        if (alter != null) {
            String step = ((Element) alter).getAttribute("step");
            try {
                if (step != null && step.length() > 0)
                    scoreNote.setAlteration(Byte.parseByte(((Element) alter).getAttribute("step")));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return scoreNote;
    }

    /**
     * @param svg
     * @return
     */
    public static CustomSVGGraphic deserializeSVG(Element svg, URI base) {
        String svg_uri = svg.getAttribute("url");
        try {
            URI uri = new URI(svg_uri);
            if(!uri.isAbsolute()){
                svg_uri = base.resolve(uri).toString();
            }
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } 
        int svg_width = Integer.parseInt(svg.getAttribute("width"));
        int svg_height = Integer.parseInt(svg.getAttribute("height"));
        CustomSVGGraphic graphic = new CustomSVGGraphic(svg_uri, svg_width, svg_height);
        try{
            int x = Integer.parseInt(svg.getAttribute("x"));
            graphic.setCutX(x);
        }catch(RuntimeException re){
        }
        try{
            int y = Integer.parseInt(svg.getAttribute("y"));
            graphic.setCutY(y);
        }catch(RuntimeException re){
        }
        Element basic = XMLHelper.getElement((Element) svg.getParentNode(), "basic");
        if (basic != null) {
            if (basic.hasAttribute("vertAlign")) {
                int vAlign = Integer.parseInt(basic.getAttribute("vertAlign"));
                graphic.setVerticalAlignment(vAlign);
            }
            if (basic.hasAttribute("vertShift")) {
                float vShift = Float.parseFloat(basic.getAttribute("vertShift"));
                graphic.setVerticalShift(vShift);
            }
        }
        return graphic;
    }

    /**
     * Removes elements of the same type and at the same metric time from the
     * given Container.
     * 
     * @param metrical
     */
    public static void removeSameTypeAndTime(Metrical metrical, Container cont) {
        for (Iterator iter = cont.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (metrical.getClass().equals(element.getClass())) {
                Metrical om = metrical;
                Metrical em = (Metrical) element;
                if (om.getMetricTime().equals(em.getMetricTime())) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * @param selections
     * @param container
     */
    public static void addSymbolicSelection(MusiteXMLSerializer instance, Element selections, Container container) {
        Element symSelection = XMLHelper.addElement(selections, "symbolicSelection");
        // has been serialized?
        if (instance.knowsObject(symSelection, container))
            return;
        // metadata
        MPEG_SMR_Tools.serializeMetaData(instance, symSelection, container);
        // rendering hints
        instance.writeXML(symSelection, container.getRenderingHints(), null, null);

        //name
        if (container.getName() != null)
            symSelection.setAttribute("name", container.getName());
        //content
        Container subSelections = new BasicContainer();
        for (Iterator iter = container.iterator(); iter.hasNext();) {
            Object referencedObjOrSubSelection = iter.next();
            Node referenced = instance.getNodeToSerializedObject(referencedObjOrSubSelection);
            if (referenced != null) {
                Long ref_id = instance.getReferencedObjectID(referencedObjOrSubSelection);
                assert ref_id != null;
                XMLHelper.addElement(symSelection, "reference").setAttribute("ref_id", "" + ref_id);
            } else if (referencedObjOrSubSelection instanceof Container) {
                subSelections.add(referencedObjOrSubSelection);
            } else {
                // neither a reference (contained object has not been serialized
                // yet) not a child-symbolicCollection
                System.out.println("[WARNING] Element in Selection has not been serialized to Piece and is ignoted: "
                                   + referencedObjOrSubSelection.toString());
            }
        }
        // app-data
        Element appData = XMLHelper.addElement(symSelection, "appData");
        appData.setAttribute("class", container.getClass().getName());
        // subSelection
        for (Iterator iter = subSelections.iterator(); iter.hasNext();) {
            Container subSelection = (Container) iter.next();
            MPEG_SMR_Tools.addSymbolicSelection(instance, symSelection, subSelection);
        }

    }

    public static Container getSymbolicSelection(MusiteXMLSerializer instance, Element selection) {
        Container container = new BasicContainer(instance.getContext());
        // reference-handling
        Object reference = instance.getReferenced(selection, container);
        if (reference != null)
            return (Container) reference;
        //metadata
        MPEG_SMR_Tools.deserializeMetaData(instance, selection, container);
        // rendering hints
        container.setRenderingHints(MPEG_SMR_Tools.deserializeRenderingHints(instance, selection));
        // appdata
        Element appdata = XMLHelper.getElement(selection, "appData");
        // TODO Do something with appdata. For example instanciate class form
        // contained
        // information. Or: add AppData to Container -> new field?

        if(selection.hasAttribute("name")){
        container.setName(selection.getAttribute("name"));    
        }
        
        // content
        Element[] content = XMLHelper.getChildElements(selection, "reference");
        for (int i = 0; i < content.length; i++) {
            String ref_id = content[i].getAttribute("ref_id");
            container.add(instance.getReferencedObject(ref_id));
        }
        // subSelections
        Element[] subSelections = XMLHelper.getChildElements(selection, "symbolicSelection");
        for (int i = 0; i < subSelections.length; i++) {
            container.add(getSymbolicSelection(instance, subSelections[i]));
        }
        return container;
    }
}

