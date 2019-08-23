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
 * Created on Nov 30, 2004
 *
 */
package de.uos.fmt.musitech.data.score;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MPEG_SMR_Tools;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * @author collin
 *  
 */
public class Clef implements Metrical, Marker, Containable, IMPEGSerializable {

    private Rational metricTime;
    private char clefType = 'g';
    private int clefLine = -1;
    private int transposition = 0;
    private Long uid;

    private RenderingHints renderingHints;

    public Clef() {
    }

    public Clef(char clefType, int clefLine) {
        this(clefType, clefLine, Rational.ZERO);
    }

    public Clef(char clefType, int clefLine, Rational metricTime) {
        this.clefType = Character.toLowerCase(clefType);
        this.clefLine = clefLine;
        this.metricTime = metricTime;
    }

    public Clef(char clefType, int clefLine, int transposition) {
        this(clefType, clefLine);
        this.transposition = transposition;
    }

    /**
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricTime()
     */
    public Rational getMetricTime() {
        return metricTime;
    }

    /**
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricDuration()
     */
    public Rational getMetricDuration() {
        return Rational.ZERO;
    }

    /**
     * TODO add comment
     * 
     * @return
     */
    public int getClefLine() {
        return clefLine;
    }

    public void setClefLine(int clefLine) {
        this.clefLine = clefLine;
    }

    public char getClefType() {
        return clefType;
    }

    public void setClefType(char clefType) {
        this.clefType = Character.toLowerCase(clefType);
    }

    public int getTransposition() {
        return transposition;
    }

    public void setTransposition(int transposition) {
        this.transposition = transposition;
    }

    public void setMetricTime(Rational metricTime) {
        this.metricTime = metricTime;
    }

    /**
     * getUid
     * 
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     * 
     * @hibernate.id generator-class="native"
     */
    public Long getUid() {
        return uid;
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
     *      java.lang.Object)
     */
    public boolean isValidValue(String propertyName, Object value) {
        // TODO Auto-generated method stub
        return true; //default
    }

    /**
     * 
     * @see de.uos.fmt.musitech.data.rendering.RenderingSupported#getRenderingHints()
     */
    public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    /**
     * 
     * @see de.uos.fmt.musitech.data.rendering.RenderingSupported#setRenderingHints(de.uos.fmt.musitech.data.rendering.RenderingHints)
     */
    public void setRenderingHints(RenderingHints renderingHints) {
        this.renderingHints = renderingHints;
        if (renderingHints != null)
            renderingHints.setParentClass(this.getClass());
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
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        //commons----------------------------------
        // create element representing this object
        Element clef = XMLHelper.addElement(parent, "clefSign");
        // check for reference
        //if (instance.knowsObject(clef, object))
        //    return true;
        // metadata
        MPEG_SMR_Tools.serializeMetaData(instance, clef, this);
        // rendering hints
        instance.writeXML(clef, getRenderingHints(), null, null);
        //-----------------------------------------

        MPEG_SMR_Tools.addMetricTime(clef, this.getMetricTime());

        clef.setAttribute("clef", "" + this.getClefType() + (this.getClefLine() + 3));

        return true;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element)
     */
    public Object fromMPEG(MusiteXMLSerializer instance, Element clefSign) {
        //commons----------------------------------
        // reference-handling
        Object reference = instance.getReferenced(clefSign, this);
        if (reference != null)
            return reference;
        //metadata
        MPEG_SMR_Tools.deserializeMetaData(instance, clefSign, this);
        // rendering hints
        setRenderingHints((RenderingHints) instance.readXML(XMLHelper.getElement(clefSign, "renderingHints"), RenderingHints.class));
        //-----------------------------------------

        this.setMetricTime((Rational) instance.readXML(XMLHelper.getElement(clefSign, "metricTime"), Rational.class));

        String type = clefSign.getAttribute("clef");
        // TODO deserialize
        if (type.equals("treble")) {
            this.setClefType('g');
            this.setClefLine(-1);
        } else if (type.equals("bass")) {
            this.setClefType('f');
            this.setClefLine(1);
        } else if (type.equals("soprano")) {
            this.setClefType('c');
            this.setClefLine(-2);
        } else if (type.equals("mezzo")) {
            this.setClefType('c');
            this.setClefLine(-1);
        } else if (type.equals("alto")) {
            this.setClefType('c');
            this.setClefLine(0);
        } else if (type.equals("tenor")) {
            this.setClefType('c');
            this.setClefLine(1);
        } else if (type.equals("baritone")) {
            this.setClefType('c');
            this.setClefLine(2);
        } else if (type.equals("percussion")) {
            this.setClefType('p');
            this.setClefLine(0);
        } else {
            this.setClefType(type.charAt(0));
            this.setClefLine(Integer.parseInt("" + type.substring(1)) - 3);
        }

        // custom SVGGraphic
        Element svg = XMLHelper.getElement(XMLHelper.getElement(XMLHelper.getElement(clefSign, "drawObjects"),
                                                                "drawObj"), "svg");
        if (svg != null) {
            CustomSVGGraphic graphic = MPEG_SMR_Tools.deserializeSVG(svg,instance.getBaseURI());
            this.addRenderingHint("custom clef", graphic);
        }

        return this;
    }
}