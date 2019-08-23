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
 * File MidiRenderer.java
 * Created on 09.01.2005 by Tillman Weyde.
 */

package de.uos.fmt.musitech.data.performance.rendering;

import de.uos.fmt.musitech.data.rendering.RenderingHints;

/**
 * TODO add class comment
 * 
 * @author Tillman Weyde
 */
public class MidiNoteRendering {

    RenderingHints renderingHints = new RenderingHints();

    /*
     * <xsd:attribute name="velocityAdd" type="xsd:integer" default="0"/>
     * <xsd:attribute name="pitchAdd" type="xsd:integer" default="0"/>
     * <xsd:attribute name="durationMult" type="xsd:decimal" default="1"/>
     * <xsd:attribute name="metricTimeAddRel" type="xsd:decimal" default="0"/>
     * <xsd:attribute name="physicalTimeAddAbs" type="xsd:long" default="0"/>
     * <xsd:attribute name="rest" type="xsd:boolean" default="false"/>
     */

    /**
     * adds velocity to note, default 0
     * 
     * @return velocity add
     */
    public int getVelocityAdd() {
        try {
            return Integer.parseInt(renderingHints.getValue("velocityAdd")
                    .toString());
        } catch (NumberFormatException e) {
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * add pitch to note, default 0
     * 
     * @return
     */
    public int getPitchAdd() {
        try {
            return Integer.parseInt(renderingHints.getValue("pitchAdd")
                    .toString());
        } catch (NumberFormatException e) {
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * multiplicates duration to note, default 1
     * 
     * @return
     */
    public float getDurationMult() {
        try {
            return Float.parseFloat(renderingHints.getValue("durationMult")
                    .toString());
        } catch (NumberFormatException e) {
            return 1;
        } catch (NullPointerException e) {
            return 1;
        }
    }

    /**
     * change starttime of note relative to duration, default 0
     * 
     * @return
     */
    public float getMetricTimeAddRel() {
        try {
            return Float.parseFloat(renderingHints.getValue("metricTimeAddRel")
                    .toString());
        } catch (NumberFormatException e) {
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
        
    }

    public long getPhysicalTimeAddAbs() {
        
        try {
            return Long.parseLong(renderingHints.getValue("physicalTimeAddAbs")
                    .toString());
        } catch (NumberFormatException e) {
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
       
    }

    public boolean isRest() {
        if (renderingHints.containsKey("rest")) {
            try {
                return renderingHints.getValue("rest").toString().equalsIgnoreCase("true");
            } catch (NumberFormatException e) {
                return false;
            }
        } else
            return false;
    }

    /**
     * @return Returns the renderingHints.
     */
    public RenderingHints getRenderingHints() {
        return renderingHints;
    }
}
