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
package de.uos.fmt.musitech.data.time;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class for Markers of TimeSignature that can be put into a MetricalTimeLine.
 * 
 * @author Martin Gieseking
 * @version $Revision: 8039 $, $Date: 2012-01-21 13:46:38 +0100 (Sat, 21 Jan 2012) $
 * 
 * @hibernate.class table = "TimeSignatureMarker"
 * 
 */
public class TimeSignatureMarker implements Marker {
	private static final long serialVersionUID = 4436536333450669558L;
    TimeSignature timeSignature;
    Rational metricTime;
    private RenderingHints renderingHints;

    //	Unique ID for this object.
    private Long uid;

	/** 
	 * getUid
	 * @see de.uos.fmt.musitech.data.MObject#getUid()
	 * 
	 * @hibernate.id 
	 * 		generator-class="native" 
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
     * TODO comment
     * 
     */
    public TimeSignatureMarker() {
    }

    /**
     * Constructor.
     * 
     * @param numerator
     *            int
     * @param denominator
     *            int
     * @param rational
     *            metric time
     */
    public TimeSignatureMarker(int numerator, int denominator, Rational rational) {
        timeSignature = new TimeSignature(numerator, denominator);
        metricTime = rational;
    }

    /**
     * Constructor.
     * 
     * @param timeSignature
     *            TimeSignature
     * @param rational
     *            metric time
     */
    public TimeSignatureMarker(TimeSignature timeSignature, Rational rational) {
        this.timeSignature = timeSignature;
        this.metricTime = rational;
    }

    /**
     * Returns the metricTime.
     * 
     * @return Rational
     * 
     * @hibernate.many-to-one name = "metricTime"
     * class = "de.uos.fmt.musitech.utility.math.Rational"
     * foreign-key = "uid"
     * cascade = "all"
     * 
     */
    public Rational getMetricTime() {
        return metricTime;
    }

    /**
     * Returns the timeSignature.
     * 
     * @return TimeSignature
     * 
     *
     * @hibernate.property 
     */
    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    /**
     * Sets the metricTime.
     * 
     * @param metricTime
     *            The metricTime to set
     */
    public void setMetricTime(Rational metricTime) {
        this.metricTime = metricTime;
    }

    /**
     * Sets the timeSignature.
     * 
     * @param timeSignature
     *            The timeSignature to set
     */
    public void setTimeSignature(TimeSignature timeSignature) {
        this.timeSignature = timeSignature;
    }

    /**
     * Returns the TimeSignatureMarker.
     * 
     * @return String
     */
    public String toString() {
        return "TimeSignatureMarker at " + metricTime + " (metricTime): " + timeSignature;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.data.time.Metrical#getMetricDuration()
     * 
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

	public RenderingHints getRenderingHints() {
		return renderingHints;
	}
	public void setRenderingHints(RenderingHints renderingHints) {
		this.renderingHints = renderingHints;
	}
}