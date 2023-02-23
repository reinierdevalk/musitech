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
 * Created on 15.06.2004
 *
 */
package de.uos.fmt.musitech.data.structure.harmony;

import java.util.ArrayList;
import java.util.Collection;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.time.TimedContainer;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * A ChordSymbolSequence is a TimedContainer for taking ChordSymbol objects.
 * 
 * @author Kerstin Neubarth
 * 
 * @hibernate.class table="ChordSymbolSequence"
 * 
 * @hibernate.joined-subclass @hibernate.joined-subclass-key column = "uid"
 *  
 */
public class ChordSymbolSequence extends TimedContainer<ChordSymbol> {

    /**
     * Constructor. Creates a TimedContainer with element type ChordSymbol.
     */
    public ChordSymbolSequence() {
    }

    /**
     * Constructor. Sets the specified Context as the Context of this
     * ChordSymbolSequence.
     * 
     * @param context
     *            Context fo tbe set as the Context of this ChordSymbolSequence
     */
    public ChordSymbolSequence(Context context) {
        setContext(context);
    }
    
    
    public ChordSymbolSequence(Piece p ) {
    	this(p.getContext());
    	addChordSymbols(p.getHarmonyTrack());
    }

    /**
     * Returns the last ChordSymbol before the specified time. If there is no
     * ChordSymbol before this time, null is returned.
     * 
     * @param time
     *            long giving the time position for which the immediately
     *            preceding ChordSymbol is requested
     * @return ChordSymbol the last ChordSymbol which occurs at or before the
     *         specified time, or null
     */
    public ChordSymbol getLastSymbolBeforeTime(long time) {
        //get last symbol before time
        SortedContainer symbolsBefore = getAllBefore(time);
        if (symbolsBefore.size() == 0)
            return null;
        Object lastSymbol = symbolsBefore.get(symbolsBefore.size() - 1);
        //check following symbol in this sequence if being at the specified
        // time
        //		if (indexOf(lastSymbol) < size() - 1) {
        //			ChordSymbol symbol = (ChordSymbol) get(indexOf(lastSymbol)+1);
        //			if (symbol != null && symbol.getTime() == time)
        //				return symbol;
        //		}
        if (lastSymbol != null && lastSymbol instanceof ChordSymbol)
            return (ChordSymbol) lastSymbol;
        return null;
    }

    /**
     * Returns the last ChordSymbol before the specified metrical time. If there
     * is no ChordSymbol at or before the specified metrical time, null is
     * returned.
     * 
     * @param metricalTime
     *            Rational giving the metrical time position for which the
     *            immediately preceding ChordSymbol is requested
     * @return ChordSymbol the last ChordSymbol which occurs at or before the
     *         specified metrical time, or null
     */
    public ChordSymbol getLastSymbolBeforeTime(Rational metricalTime) {
        //get time from MetricalTimeLine and return last symbol before time
        long time = getTimeFromMetricalTimeLine(metricalTime);
        if (time != Timed.INVALID_TIME)
            return getLastSymbolBeforeTime(time);
        //the following should not be necessary
        //		for (Iterator iter = this.iterator(); iter.hasNext();) {
        //			ChordSymbol element = (ChordSymbol) iter.next();
        //			if (element.getMetricTime() != null
        //				&& element.getMetricTime().isGreater(metricalTime))
        //				if (indexOf(element) > 0)
        //					return (ChordSymbol) get(indexOf(element) - 1);
        //		} //does not reach last element, therefore:
        //		ChordSymbol last = (ChordSymbol) get(size() - 1);
        //		if (last.getMetricTime().isLessOrEqual(metricalTime))
        //			return last;
        return null;
    }

    /**
     * Returns the first ChordSymbol at the specified time. If there is no
     * ChordSymbol at the specified time, null is returned.
     * 
     * @param time
     *            long giving the time position for which the nearest
     *            ChordSymbol not starting earlier is requested
     * @return ChordSymbol the first ChordSymbol which occurs at or after the
     *         specified time, or null
     */
    public ChordSymbol getFirstSymbolAtTime(long time) {
//        SortedContainer symbolsBefore = getAllBefore(time);
//        Object nextSymbol = get(symbolsBefore.size());
//        if (nextSymbol != null && nextSymbol instanceof ChordSymbol)
//            return (ChordSymbol) nextSymbol;
//        return null;
        for (int i = 0; i < size(); i++) {
            ChordSymbol symbol = get(i);
            Rational metricTime = symbol.getMetricTime();
            if (metricTime!=null){
                MetricalTimeLine mtl = getContext().getPiece().getMetricalTimeLine();
                long physicalTime = mtl.getTime(metricTime);
                if (physicalTime>=time){
                    return symbol;
                }
            }
        }
        return null;
    }

    /**
     * Returns the first ChordSymbol at the specified metrical time. If there is
     * no ChordSymbol at the specified metrical time, null is returned.
     * 
     * @param metricalTime
     *            Rational giving the metrical time position for which the
     *            nearest ChordSymbol not starting earlier is requested
     * @return ChordSymbol the first ChordSymbol which occurs at or after the
     *         specified metrical time
     */
    public ChordSymbol getFirstSymbolAtTime(Rational metricalTime) {
        long time = getTimeFromMetricalTimeLine(metricalTime);
        if (time != Timed.INVALID_TIME)
            return getFirstSymbolAtTime(time);
        return null;
    }

    /**
     * Gets the time value for the specified <code>metricalTime</code> from
     * the MetricalTimeLine. Returns a non-negative long value or
     * Timed.INVALID_TIME, if the MetricalTimeLine does not provide a
     * non-negative time value. <br>
     * If this ChordSymbolSequence does not have a Context and/or Work, a new
     * MetricalTimeLine is created.
     * 
     * @param metricalTime
     *            Rational indicating the metrical time for which the
     *            corresponding physical time is to be returned
     * @return long representing the physical time corresponding to the
     *         specified metrical time, or Timed.INVALID_TIME
     */
    private long getTimeFromMetricalTimeLine(Rational metricalTime) {
        MetricalTimeLine mtl = null;
        if (getContext() != null && getContext().getPiece() != null)
            mtl = getContext().getPiece().getMetricalTimeLine();
        long time = Timed.INVALID_TIME;
        if (mtl == null) {
            mtl = new MetricalTimeLine();
            mtl.add(get(size() - 1)); //TODO überprüfen, welche Zeitangaben
            // dieses ChordSymbol besitzt?
        }
        time = mtl.getTime(metricalTime);
        if (time >= 0)
            return time;
        else
            return Timed.INVALID_TIME;
    }
    
    /**
	 * Returns a Rational representing the metrical time which corresponds to the
	 * specified physical time. The time is got from the MetricalTimeLine
	 * of the Conetxt of this ChordSymbolSequence. If the Context, its Piece or the
	 * MetricalTimeLine of the Piece is null, a new instance of MetricalTimeLine is 
	 * created for converting physical time to metric time. If no positive metric time 
	 * value could be determined, null is returned.
	 * 
	 * @param time long value giving the physical time position which is to be converted to metrical time
	 * @return Rational being the metrical time corresponding to the specified physical time, or null
	 */
    private Rational getMetricTimeFromMetricalTimeLine(long time){
	    MetricalTimeLine mtl = null;
        if (getContext() != null && getContext().getPiece() != null)
            mtl = getContext().getPiece().getMetricalTimeLine();
        if (mtl == null) {
            mtl = new MetricalTimeLine();
            mtl.add(get(size() - 1)); //TODO überprüfen, welche Zeitangaben
            // dieses ChordSymbol besitzt?
        }
        Rational metricTime = mtl.getMetricTime(time);
        if (metricTime.isGreater(Rational.ZERO))
            return metricTime;
        return null;
	}

    /**
	 * Overwrites method <code>add(Object)</code> of SortedUniquesCollection in order
	 * to set the time of the added element according to the Context of this
	 * ChordSymbolSequence. If the element does not have a valid physical time,
	 * its <code>time</code> is converted from the metrical time via the MetricalTimeLine
	 * of the Context. If the element does not have a metric time, its <code>metricTime</code>
	 * is converted from the physical time. 
     * @param obj 
     * @return true if contents of this has been changed.  
	 *  
	 * @see de.uos.fmt.musitech.utility.collection.TypedCollection#add(java.lang.Object)
	 */
    @Override
	public boolean add(ChordSymbol obj) {
        if (context != null) {
            if (obj.getMetricTime() != null) {
                obj.setTime(getTimeFromMetricalTimeLine(obj.getMetricTime()));
            }
            if (obj.getMetricTime()==null
                    && obj.getTime()!= Timed.INVALID_TIME){
                    obj.setMetricTime(getMetricTimeFromMetricalTimeLine(obj.getTime()));
                }
        }
        return super.add(obj);
    }
    
    /**
     * Adds all chordSymbols contained in the argument to this.  
     * 
     * @param con The container to sift through.
     * @return true if any chord symbols were added to this. 
     */
    public boolean addChordSymbols(Container<?> con) {
    	boolean retVal = false;
    	Collection<Containable> col =  con.getContentsRecursiveList(new ArrayList<Containable>());
    	for(Object obj: col) {
    		if(obj instanceof ChordSymbol) {
    			retVal &= add((ChordSymbol) obj);
    		}
    	}
    	return retVal;
    }

}