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
 * Created on 2005-1-3
 */
package de.uos.fmt.musitech.data.score.mpeg;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BarlineContainer;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.DynamicsLevelMarker;
import de.uos.fmt.musitech.data.score.DynamicsMarker;
import de.uos.fmt.musitech.data.structure.container.ClefContainer;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.BeatMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * @author Administrator
 */
public class MPEGContextLine extends ArrayList<Marker> implements IMPEGSerializable {

    public MPEGContextLine() {
//        super(new Class[] {Marker.class, Clef.class, Barline.class});
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    @Override
	public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        Element contextLine = XMLHelper.getOrCreateElement((Element) parent, "context");
        if (instance.knowsObject(contextLine, object))
            return true;

        for (Iterator<Marker> iter = iterator(); iter.hasNext();) {
            Object ctxtElement = iter.next();
            if (ctxtElement instanceof TimeSignatureMarker) {
                Element timeSign = XMLHelper.addElement(contextLine, "timeSign");
                instance.writeXML(timeSign, ((TimeSignatureMarker) ctxtElement).getRenderingHints(), null, null);
                // serialize time-attribute
                TimeSignature ts = ((TimeSignatureMarker) ctxtElement).getTimeSignature();
                timeSign.setAttribute("time", ts.getNumerator() + "/" + ts.getDenominator());
                // serialize metricTime
                MPEG_SMR_Tools.addMetricTime(timeSign, ((TimeSignatureMarker) ctxtElement).getMetricTime());
            } else if (ctxtElement instanceof KeyMarker) {
                Element keySign = XMLHelper.addElement(contextLine, "keySign");
                keySign.setAttribute("fifths", "" + ((KeyMarker) ctxtElement).getAccidentalNum());
                // serialize metricTime
                MPEG_SMR_Tools.addMetricTime(keySign, ((KeyMarker) ctxtElement).getMetricTime());
            } else if (ctxtElement instanceof DynamicsLevelMarker) {
                Element level = XMLHelper.addElement(contextLine, "level");
                MPEG_SMR_Tools.addMetricTime(level, ((DynamicsLevelMarker) ctxtElement).getMetricTime());
                level.setAttribute("level", ((DynamicsLevelMarker) ctxtElement).getLevel());
            } else if (ctxtElement instanceof DynamicsMarker) {
                Element change = XMLHelper.addElement(contextLine, "change");
                boolean cresc = ((DynamicsMarker) ctxtElement).isCrescendo();
                if (cresc)
                    change.setAttribute("cresc", "" + cresc);
                MPEG_SMR_Tools.addMetricTime(change, ((DynamicsMarker) ctxtElement).getMetricTime());
                Element duration = XMLHelper.addElement(change, "duration");
                duration.setAttribute("numerator", "" + ((DynamicsMarker) ctxtElement).getMetricDuration().getNumer());
                duration
                        .setAttribute("denominator", "" + ((DynamicsMarker) ctxtElement).getMetricDuration().getDenom());
            } else if (ctxtElement instanceof Clef || ctxtElement instanceof Barline) {
                // add ALL IMPEGSerializable can be handled this way
                instance.writeXML(contextLine, ctxtElement, null, null);
            } else if (ctxtElement instanceof ChordSymbol) {
                System.out.println("[WARNING]" + ctxtElement + " not serialized in MPEGContextLine");
            } else if (ctxtElement instanceof BeatMarker) {
                System.out.println("[WARNING]" + ctxtElement + " not serialized in MPEGContextLine");
            } else if (ctxtElement instanceof TimedMetrical) {
                Element timeSign = XMLHelper.addElement(contextLine, "tempo");
                // serialize time-attribute
                // serialize metricTime
                MPEG_SMR_Tools.addMetricTime(timeSign, ((TimedMetrical) ctxtElement).getMetricTime());
                timeSign.setAttribute("tempo", ""
                                               + instance.getContext().getPiece().getMetricalTimeLine()
                                                       .getTempoQPM(((TimedMetrical) ctxtElement).getMetricTime()));
            } else {
                System.out.println("[WARNING]" + " not serialized context-object: " + ctxtElement);
                assert false; // OR: add proper serializing information above
            }
        }

        return true;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element)
     */
    @Override
	public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
        // reference-handling
        Object reference = instance.getReferenced(node, this);
        if (reference != null)
            return reference;

        Element[] children = XMLHelper.getChildElements(node);
        for (int i = 0; i < children.length; i++) {
            Element ctxtElement = children[i];
            String name = ctxtElement.getNodeName();
            if ("clefSign".equalsIgnoreCase(name)) {
                this.add((Clef)instance.readXML(ctxtElement, Clef.class));
            } else if ("tempo".equalsIgnoreCase(name)) {
                Rational position = (Rational) instance.readXML((Element) ctxtElement
                        .getElementsByTagName("metricTime").item(0), Rational.class);
                TimedMetrical timedMetrical = new TimedMetrical();
                timedMetrical.setMetricTime(position);
                instance.getContext().getPiece().getMetricalTimeLine().setTempoQPM(
                                                                                   position,
                                                                                   Double.parseDouble(ctxtElement
                                                                                           .getAttribute("tempo")));
                /*
                 * TODO dies geht's schief, sowohl wenn duplikate nicht entfernt
                 * werden, als auch wenn sie entfernt werden:
                 */
                // MPEG_SMR_Tools.removeSameTypeAndTime(timedMetrical,
                // instance.getContext().getPiece() .getMetricalTimeLine());
                // instance.getContext().getPiece().getMetricalTimeLine().add(timedMetrical);
            } else if ("timeSign".equalsIgnoreCase(name)) {
                TimeSignature timeSignature = MPEG_SMR_Tools.getTimeSignature(ctxtElement.getAttribute("time"));
                Rational position = (Rational) instance.readXML((Element) ctxtElement
                        .getElementsByTagName("metricTime").item(0), Rational.class);
                TimeSignatureMarker tsm = new TimeSignatureMarker(timeSignature, position);
                tsm.setRenderingHints((RenderingHints) instance.readXML(XMLHelper.getElement(ctxtElement,
                                                                                             "renderingHints"),
                                                                        RenderingHints.class));
                instance.getContext().getPiece().getMetricalTimeLine().add(tsm);
            } else if ("dynamicMarker".equalsIgnoreCase(name)) {
                Element level = XMLHelper.getElement(ctxtElement, "level");
                Element change = XMLHelper.getElement(ctxtElement, "change");
                if (level != null) {
                    DynamicsLevelMarker dlm = new DynamicsLevelMarker();
                    dlm.setLevel(level.getAttribute("level"));
                    dlm.setMetricTime((Rational) instance.readXML(XMLHelper.getElement(level, "metricTime"),
                                                                  Rational.class));
                    instance.getContext().getPiece().getHarmonyTrack().add(dlm);
                } else if (change != null) {
                    DynamicsMarker dyn = new DynamicsMarker();
                    dyn.setMetricTime((Rational) instance.readXML(XMLHelper.getElement(change, "metricTime"),
                                                                  Rational.class));
                    Rational dur = (Rational) instance.readXML(XMLHelper.getElement(change, "metricTime"),
                                                               Rational.class);
                    if (dur != null)
                        dyn.setMetricDuration(dur);
                    if (change.hasAttribute("cresc"))
                        dyn.setCrescendo("true".equals(change.getAttribute("cresc")));
                    instance.getContext().getPiece().getHarmonyTrack().add(dyn);
                }
            } else if ("keySign".equalsIgnoreCase(name)) {
                KeyMarker km = new KeyMarker();
                Rational position = (Rational) instance.readXML((Element) ctxtElement
                        .getElementsByTagName("metricTime").item(0), Rational.class);
                km.setMetricTime(position);
                String fifth = ctxtElement.getAttribute("fifths");
                km.setAccidentalNum(Integer.parseInt(fifth));
                instance.getContext().getPiece().getHarmonyTrack().add(km);
            } else if ("barline".equalsIgnoreCase(name)) {
                this.add((Barline)instance.readXML(ctxtElement, Barline.class));
            } else {
                System.out.println("[WARNING] could not deserialize context-element: " + name);
                //                assert false;
            }
        }

        return this;
    }

    /**
     * @return
     */
    public ClefContainer getClefTrack() {
        ClefContainer clefTrk = new ClefContainer();
        for (Iterator<Marker> iter = this.iterator(); iter.hasNext();) {
            Marker element = iter.next();
            if (element instanceof Clef) {
                if (clefTrk == null)
                    clefTrk = new ClefContainer();
                clefTrk.add((Clef)element);
            }
        }
        return clefTrk;
    }

    /**
     * @return
     */
    public BarlineContainer getBarlines() {
        BarlineContainer barlines = new BarlineContainer();
        for (Iterator<Marker> iter = this.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof Barline) {
                if (barlines == null)
                    barlines = new BarlineContainer();
                barlines.add((Barline)element);
            }
        }
        return barlines;
    }

}