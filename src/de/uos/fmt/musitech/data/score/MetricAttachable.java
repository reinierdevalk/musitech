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
 * Created on 12-Nov-2004
 *
 */
package de.uos.fmt.musitech.data.score;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author collin
 *
 */
public class MetricAttachable implements Containable, IAttachable {
	private Rational metricTime;
	private Object symbol;
	private int relativePosition = NORTH;
	private Object parent;
	private Object anchor;
	private int distance;
	private int alignment;
	private Long uid;
	private boolean generated;
	
	private RenderingHints renderingHints;
	
	public final static int NORTH = 1;
	public final static int SOUTH = 2;
	
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	public final static int CENTER = 3;
	
	public MetricAttachable() {}
	
	public MetricAttachable(MetricAttachable ma) {
		this();
		metricTime = ma.metricTime;
		symbol = ma.symbol;
		relativePosition = ma.relativePosition;
		parent = ma.parent;
		anchor = ma.anchor;
		distance = ma.distance;
		alignment = ma.alignment;
	}
	
	public MetricAttachable(Rational metricTime, Object symbol) {
		this.metricTime = metricTime;
		this.symbol = symbol;
	}
	
	public MetricAttachable(Object anker, Object symbol) {
		this.anchor = anker;
		this.symbol = symbol;
	}
	/**
	 * @return Returns the metricTime.
	 */
	public Rational getMetricTime() {
		if (anchor == null)
			return metricTime;
		if (anchor instanceof NotationChord)
			return ((NotationChord)anchor).getMetricTime();
		else if (anchor instanceof Note)
			return ((Note)anchor).getMetricTime();
		
		throw new IllegalStateException("the type " + anchor.getClass() + " ist not yet supported for use as an anchor");
	}
	/**
	 * @param metricTime The metricTime to set.
	 */
	public void setMetricTime(Rational metricTime) {
		this.metricTime = metricTime;
	}
	/**
	 * @return Returns the symbol.
	 */
	public Object getSymbol() {
		return symbol;
	}
	/**
	 * @param symbol The symbol to set.
	 */
	public void setSymbol(Object symbol) {
		this.symbol = symbol;
	}
	/**
	 * @return Returns the parent.
	 */
	public Object getParent() {
		return parent;
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(Object parent) {
		this.parent = parent;
	}
	/**
	 * @return Returns the anchor.
	 */
	public Object getAnker() {
		return anchor;
	}
	/**
	 * @param anchor The anchor to set.
	 */
	public void setAnker(Object anker) {
		this.anchor = anker;
	}
	/**
	 * @return Returns the relativePosition.
	 */
	public int getRelativePosition() {
		return relativePosition;
	}
	/**
	 * @param relativePosition The relativePosition to set.
	 */
	public void setRelativePosition(int relativePosition) {
		this.relativePosition = relativePosition;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getAlignment() {
		return alignment;
	}
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
	
    /**
     * getUid
     * 
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     * 
     * @hibernate.id generator-class="native"
     */
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
        return true; //default
    }
	
	public RenderingHints getRenderingHints() {
		return renderingHints;
	}
	
	
	public void setRenderingHints(RenderingHints renderingHints) {
		this.renderingHints = renderingHints;
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

    public boolean isGenerated() {
        return generated;
    }
    public void setGenerated(boolean generated) {
        this.generated = generated;
    }
}
