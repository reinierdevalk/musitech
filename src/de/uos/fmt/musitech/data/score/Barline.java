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
/**
 * File Barline.java
 * Created on 11.02.2003
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
import de.uos.fmt.musitech.utility.HashCodeGenerator;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * This class represents a barline. It is used in the class {@link NotationVoice} to mark a barline.
 * @author collin
 * @version 1.0
 * 
 * @hibernate.class table = "Barline"
 */
public class Barline implements Containable, Marker, IMPEGSerializable {

    private Rational metricTime;
    private boolean barlineDouble = false;
    private boolean barlineBold = false;
    private RenderingHints renderingHints;
    private boolean startRepeat;
    private boolean endRepeat;

    /**
     * Gets the barlineBold.
     * 
     * @return Returns the barlineBold.
     */
    public boolean isBarlineBold() {
        return barlineBold;
    }

    /**
     * Sets the barlineBold.
     * 
     * @param barlineBold
     *            The barlineBold to set.
     */
    public void setBarlineBold(boolean barlineBold) {
        this.barlineBold = barlineBold;
    }

    /**
     * Gets the barlineDouble.
     * 
     * @return Returns the barlineDouble.
     */
    public boolean isBarlineDouble() {
        return barlineDouble;
    }

    /**
     * Sets the barlineDouble.
     * 
     * @param barlineDouble
     *            The barlineDouble to set.
     */
    public void setBarlineDouble(boolean barlineDouble) {
        this.barlineDouble = barlineDouble;
    }

    /**
     * Gets the endRepeat.
     * 
     * @return Returns the endRepeat.
     */
    public boolean isEndRepeat() {
        return endRepeat;
    }

    /**
     * Sets the endRepeat.
     * 
     * @param endRepeat
     *            The endRepeat to set.
     */
    public void setEndRepeat(boolean endRepeat) {
        this.endRepeat = endRepeat;
    }

    /**
     * Gets the startRepeat.
     * 
     * @return Returns the startRepeat.
     */
    public boolean isStartRepeat() {
        return startRepeat;
    }

    /**
     * Sets the startRepeat.
     * 
     * @param startRepeat
     *            The startRepeat to set.
     */
    public void setStartRepeat(boolean startRepeat) {
        this.startRepeat = startRepeat;
    }
    //	Unique ID for this object.
    private Long uid;

    private int hashCode = HashCodeGenerator.getHashCode();

    /**
     * @see java.lang.Object#hashCode()
     * 
     * @hibernate.property
     */
    public int hashCode() {
        return hashCode;
    }

    public Barline() {
    }

    /**
     * This is the main constructor.
     * 
     * @param metricTime
     *            This is timestamp of the barline
     */
    public Barline(Rational metricTime) {
        this.metricTime = metricTime;
    }

    /**
     * This is the main constructor.
     * 
     * @param metricTime
     *            This is timestamp of the barline
     * @param barlineDouble
     *            If the barline is double
     */
    public Barline(Rational metricTime, boolean barlineDouble) {
        this(metricTime);
        this.barlineDouble = barlineDouble;
    }

    /**
     * Returns true if the barline is double.
     * 
     * @return is double
     * @hibernate.property
     */
    public boolean isDouble() {
        return barlineDouble;
    }

    /**
     * This method returns the metric time of this barline.
     * 
     * @return the metric time of this barline.
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricTime()
     * 
     * @hibernate.many-to-one name = "metricTime" class =
     *                        "de.uos.fmt.musitech.utility.math.Rational"
     *                        foreign-key = "uid" cascade = "all"
     */
    public Rational getMetricTime() {
        return metricTime;
    }

    public void setMetricTime(Rational metricTime) {
        this.metricTime = metricTime;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricDuration()
     */
    public Rational getMetricDuration() {
        return Rational.ZERO;
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
     * getUid
     * 
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     * 
     * @hibernate.id generator-class="native"
     * 
     *  
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
        Element barline = XMLHelper.addElement(parent, "barline");
        // check for reference
        //if (instance.knowsObject(barline, object))
        //    return true;
        // metadata
        MPEG_SMR_Tools.serializeMetaData(instance, barline, this);
        // rendering hints
        instance.writeXML(barline, getRenderingHints(), null, null);
        //-----------------------------------------

        MPEG_SMR_Tools.addMetricTime(barline, this.getMetricTime());

        // mpeg-types: single, double, end, repEnd, repBegin, repEndBegin
        if (isDouble())
            barline.setAttribute("type", "double");
        else if (isStartRepeat() && isEndRepeat())
            barline.setAttribute("type", "repEndBegin");
        else if (isStartRepeat())
            barline.setAttribute("type", "repBegin");
        else if (isEndRepeat())
            barline.setAttribute("type", "repEnd");
        else {
            // nothing => default = single
        }
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
        XMLHelper.getElement(clefSign, "renderingHints");
        setRenderingHints((RenderingHints) instance.readXML(XMLHelper.getElement(clefSign, "renderingHints"),
                                                            RenderingHints.class));
        //-----------------------------------------

        this.setMetricTime((Rational) instance.readXML(XMLHelper.getElement(clefSign, "metricTime"), Rational.class));

        // custom SVGGraphic
        Element svg = XMLHelper.getElement(XMLHelper.getElement(XMLHelper.getElement(clefSign, "drawObjects"),
                                                                "drawObj"), "svg");
        if (svg != null) {
            CustomSVGGraphic graphic = MPEG_SMR_Tools.deserializeSVG(svg,instance.getBaseURI());
            this.addRenderingHint("preview", graphic);
        }

        // type single, double, end, repEnd, repBegin, repEndBegin
        String type = clefSign.getAttribute("type");
        if ( type == null || // unnecessary under Java 1.4 but who knows ....  
                "".equals(type) || "single".equals(type)) {
            // default
        } else if ("double".equals(type)) {
            setBarlineDouble(true);
        } else if ("end".equals(type)) {
            // TODO what to do in this case?
            assert false;
        } else if ("repEnd".equals(type)) {
            setEndRepeat(true);
        } else if ("repBegin".equals(type)) {
            setStartRepeat(true);
        } else if ("repEndBegin".equals(type)) {
            setEndRepeat(true);
            setStartRepeat(true);
        } else {
            assert false; // or: new attribute-value in schema
        }

        String range = clefSign.getAttribute("range");
        // TODO what to do with barline range information??

        String hardCoded = clefSign.getAttribute("hard-coded");
        // TODO where to store this ??

        return this;
    }
}
