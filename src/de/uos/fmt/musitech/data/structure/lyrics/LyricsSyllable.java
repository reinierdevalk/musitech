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
 * Created on 22.04.2003
 */
package de.uos.fmt.musitech.data.structure.lyrics;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.framework.editor.Editable;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Repesents a syllable of lyrics.
 * @author FX
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 * 
 * @hibernate.class table="LyricsSyllable"
 * 
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 * 
 */
public class LyricsSyllable extends TimedMetrical implements Editable {

	private String text = "";

	/**
	 * @param timeStamp
	 */
	public LyricsSyllable(long time, String text) {
		super(time, null);
		this.text = text;
	}
	
	public LyricsSyllable(Rational metricTime, long time){
	    super(time, metricTime);
	}
	
	public LyricsSyllable(Rational metricTime, String text){
	    this(metricTime, Timed.INVALID_TIME);
	    setText(text);
	}
	
	public LyricsSyllable(long time, Rational metricTime, String text){
	    this(metricTime, time);
	    setText(text);
	}
	
	

	/**
	 * @return
	 * 
	 * @hibernate.property
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param string
	 */
	public void setText(String string) {
		text = string;
	}

	public LyricsSyllable() {
//		super();
	    this(null, Timed.INVALID_TIME);
	}
	
	/**
	 * Returns the time of this LyricsSyllable if it is valid
	 * (i.e., if does not equal Timed.INVALID_TIME).
	 * If this LyricsSyllable has no valid time, but a metrical time,
	 * its metrical time is transformed to physical time via the
	 * MetricalTimeLine.
	 *  
	 * @return long time of this LyricsSyllable which is its time set or its metrical time transformed to physical time
	 * 
	 * @see de.uos.fmt.musitech.data.time.Timed#getTime()
	 */
	public long getTime(){
	    if (super.getTime() != Timed.INVALID_TIME)
	        return super.getTime();
	    if (getMetricTime()!=null){
	        MetricalTimeLine timeLine = Context.getDefaultContext().getPiece().getMetricalTimeLine();	//TODO immer neue TimeLine?
	        return timeLine.getTime(getMetricTime());
	    }
	    return Timed.INVALID_TIME;
	}

    /** 
     * @see de.uos.fmt.musitech.framework.editor.Editable#getEditingProfile()
     */
    public EditingProfile getEditingProfile() {
        return new EditingProfile("Lyrics Syllable", new EditingProfile[] {
				new EditingProfile("Text", "String", "text"),
				new EditingProfile("Time", "PhysicalTime", "time"), 
				new EditingProfile("Metric Time", "Rational", "metricTime"),
				new EditingProfile("Duration", "Rational", "metricDuration")}, "Panel");
    }
}
