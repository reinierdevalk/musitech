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
 * Created on Jan 9, 2005
 *
 */
package de.uos.fmt.musitech.data.score;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * A Marker for dynamics as text, e.g. piano, mezzopiano, forte etc.
 * 
 * @author Tillman Weyde
 */
public class DynamicsLevelMarker implements Marker {

    private Rational metricTime;
    private String level = "mp";
    private Long id;
    private int midiLevel = 60;
    private RenderingHints renderingHints;

    /**
     * 
     */
    public DynamicsLevelMarker() {
    }
    
    /**
     * 
     */
    public DynamicsLevelMarker(Rational startTime) {
        setMetricTime(startTime);
    }
    
    /**
     * 
     */
    public DynamicsLevelMarker(Rational startTime, String level) {
        setMetricTime(startTime);
        setLevel(level);
        
    }
    
    /**
     * 
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
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     */
    public Long getUid() {
        return id;
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
     */
    public void setUid(Long uid) {
        this.id = uid;

    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
     *      java.lang.Object)
     */
    public boolean isValidValue(String propertyName, Object value) {
        return true;
    }

    public int getMidiLevel() {
        return midiLevel;
    }

    public void setMidiLevel(int midiLevel) {
        this.midiLevel = midiLevel;
    }

    public void setMetricTime(Rational metricTime) {
        this.metricTime = metricTime;
    }

    /**
     * The dynamic level as combination of either 'p's, 'f's, 'mf' of 'mp'. As a regular
     * expression: "p{1,5}|mp|mf|p{1,5}" .
     * 
     * @return The current level.
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the dynamic level as combination of either 'p's, 'f's, 'mf' of 'mp'. As
     * regular expression: "p{1,4}|mp|mf|p{1,4}" .
     * 
     * @return The current level.
     */
    public void setLevel(String level) {
        if (level == null) {
            level = "mf";
            if (DebugState.DEBUG_SCORE)
                System.out.println("WARNING: " + getClass().getName()
                                   + ".setLevel() called with argument " + null);
        }
        this.level = level;
        if (level.matches("p{1,4}"))
            midiLevel = 63 - 12 * level.length();
        else if (level.equals("mp}"))
            midiLevel = 63;
        else if (level.equals("mf"))
            midiLevel = 75;
        else if (level.matches("f{1,4}"))
            midiLevel = 75 + 12 * level.length();
        else if (DebugState.DEBUG_SCORE)
            System.out.println("WARNING: " + getClass().getName()
                               + ".setLevel() called with invalid string argument "
                               + level);

    }

    /**
     * TODO add comment
     *  
     */
    public char getMusicGlyph() {
        char c = 0;
//        char c2 = 0;
        if (level.matches("p{1,3}"))
            c = (char) (0x6F + level.length());
//        else if (level.equals("pppp}"))
//            c = c2 = (char) (0x71);
        else if (level.equals("mp}"))
            c = 0x69;
        else if (level.equals("mf"))
            c = 0x6A;
        else if (level.matches("f{1,3}"))
            c = (char) (0x65 + level.length());
//        else if (level.equals("ffff}"))
//            c = c2 = (char) (0x66);
        StringBuffer sb = new StringBuffer();
        return c;
//        if(c>0)
//            sb.append(c);
//        if(c2>0)
//            sb.append(c2);
//        return sb.toString();
    }
    public RenderingHints getRenderingHints() {
        return renderingHints;
    }
    public void setRenderingHints(RenderingHints renderingHints) {
        this.renderingHints = renderingHints;
    }
}