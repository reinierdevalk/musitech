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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.structure.harmony.Pattern;
import de.uos.fmt.musitech.structure.harmony.Voicing;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * The MetricalTimeLine contains the information on meter and harmony of a given
 * piece. The metrical information is encoded in Metrical objects. Every Metrical
 * object contains a metrical time by which they are ordered. MarkerObjects may
 * also contain additional Information like TimeSignature, KeyMarker or Harmony.
 * addMethods check for temporal and metrical monotony, that means the elements
 * are sorted by their metrical and the order must hold for the timeStamps.
 * otherwise Illegal ArgumentExceptions are thrown.
 * 
 * @author Tillman Weyde / Martin Gieseking
 * @version $Revision: 8053 $, $Date: 2012-02-20 04:01:02 +0100 (Mon, 20 Feb 2012) $
 * 
 * @hibernate.class table = "MetricalTimeLine"
 * @hibernate.joined-subclass 
 * @hibernate.joined-subclass-key column = "uid"
 *  
 */
public class MetricalTimeLine extends SortedContainer<Marker> {

    private static final long serialVersionUID = 2150986315261000432L;

    /** the default TimeSignatureMarker defines a 4/4 beat. */
    TimeSignatureMarker defaultTSM;

    /**
     * The <code>endMarker</code> is last Marker and its values must modified as other
     * markers are added.
     */
    final TimedMetrical endMarker = new TimedMetrical(20000000, new Rational(10));
	/** The <code>zeroMarker</code> is first Marker and must not be modified. */
	final TimedMetrical zeroMarker = new TimedMetrical(0, Rational.ZERO);

	///** The <code>zeroTimeSignatureMarker</code> sets a default timesignature to 4/4. */
	//final TimeSignatureMarker zeroTimeSignatureMarker = new TimeSignatureMarker(4, 4, Rational.ZERO);

    // TODO Check calculation of the end.
    /**
     * Get the endMarker, which is the last marker in the TimeLine.
     * 
     * @return The end marker.
     */ 
    public TimedMetrical getEndMarker() {
        return endMarker;
    }
    
    /**
     * Get the zeroMarker, which is the first marker in the TimeLine.
     * 
     * @return The end marker.
     */ 
    public TimedMetrical getZeroMarker() {
        return zeroMarker;
    }

    public MetricalTimeLine() {
        this(null);
    }

    /**
     * Constructor with a default MetricalComparator, zeroMarker and endMarker.
     */
    public MetricalTimeLine(Context context) {
        super(context, Marker.class, new MetricalComparator());
        // XXX possibly. several Timelines ?
        // Default Tempo is 120, i.e. we have one whole note every two seconds.
        // to get the Timestamp, multiply r.getNumer() by 2,000,000.
        int bars = 1000000;
        //		Rational r = new Rational(bars);
        //		endMarker = new TimedMetrical(bars * 2000000L, r);
        super.add(zeroMarker);
        super.add(endMarker);
        defaultTSM = new TimeSignatureMarker(4, 4, Rational.ZERO);
        RenderingHints rh = new RenderingHints();
        rh.registerHint("visible", new Boolean(false));
        defaultTSM.setRenderingHints(rh);
        super.add(defaultTSM);
        // XXX just for Testing
        //		KeyMarker km = new KeyMarker(Rational.ZERO, new TimeStamp(0));
        //		km.setRoot('E');
        //		km.setRootAccidental(-1);
        //		km.accidentalNum = -6;
        //		add(km);
    }

    public boolean add(Marker o) {
        if (o instanceof TimeSignatureMarker)
            remove(defaultTSM);
        return super.add(o);
    }

    boolean hasTimedMetrical(Rational metricTime) {
        return getTimedMetrical(metricTime).getMetricTime().equals(metricTime);
    }

    /**
     * Method add. This Method adds a TimedMetrical.
     * 
     * @throws IllegalArgumentException if the new timedMetrical is incompatible with the
     *             already exisiting markers (the order by metrical time must not be
     *             different from the order by physical time since we do not allow
     *             negative tempi).
     * @throws IllegalArgumentException if there's already a timedMetrical with the metric
     *             time of the given parameter.
     * @param timedMetrical The new TimedMetrical.
     * @return TimedMetrical The result is true if the method is successful.
     */
    public boolean add(TimedMetrical timedMetrical) {
        if (timedMetrical == null) {
            return false;
        }
        if (timedMetrical instanceof KeyMarker) {
            // TODO this should not be necessary, check all calling classes
            getContext().getPiece().getHarmonyTrack().add(timedMetrical);
            return false;
        }
        if (hasTimedMetrical(timedMetrical.getMetricTime())
            && getTimedMetrical(timedMetrical.getMetricTime()).getClass().equals(
                    timedMetrical.getClass())) {
            if (getTimedMetrical(timedMetrical.getMetricTime()) == endMarker) {
                endMarker.setMetricTime(endMarker.getMetricTime()
                        .add(new Rational(10, 1)));
                endMarker.setTime(getTime(endMarker.getMetricTime()));
                System.out.println("MetricalTimeLine in method add(TimedMetrical):");
                System.out
                        .println("TimedMetrical set to previous endMarker position, endMarker shifted to right.");
            } else
                throw new IllegalArgumentException("there's already a TimedMetrical at "
                                                   + timedMetrical.getMetricTime());
        }
//        Marker metricIndices[] = lookupBounds(timedMetrical.getMetricTime(),
//                TimedMetrical.class);
//        TimedMetrical tmLeft = (TimedMetrical) metricIndices[0];
//        TimedMetrical tmRight = (TimedMetrical) metricIndices[1];
        // TODO Problem inserting new Markers (test_wolf)
        //        if ((tmLeft != null && tmLeft.getTime() > timedMetrical.getTime())
        //            || (tmRight != null && tmRight.getTime() < timedMetrical.getTime())) {
        //            throw new IllegalArgumentException("TimedMetrical doesn't fit in existing meter
        // scheme: " + timedMetrical);
        //        }
        boolean added = super.add(timedMetrical);
        if (added) {
            int index = indexOf(timedMetrical);
            if (timedMetrical.getMetricTime().isGreater(endMarker.getMetricTime())) {
                remove(endMarker);
                endMarker.setMetricTime(timedMetrical.getMetricTime().add(10, 1));
                endMarker.setTime(getTime(endMarker.getMetricTime()));
                add(endMarker);
            }
            TimedMetrical next = getNextTimedMetrical(timedMetrical.getMetricTime());
            if (next == endMarker) {
                TimedMetrical previous = getPreviousTimedMetrical(timedMetrical
                        .getMetricTime());
                if (previous == null)
                    previous = zeroMarker;
                if (!(timedMetrical.getMetricTime().sub(previous.getMetricTime())
                        .equals(Rational.ZERO))) {
                    // the ratio of the metricals
                    double ratio = next.getMetricTime().sub(previous.getMetricTime())
                            .div(
                                    timedMetrical.getMetricTime().sub(
                                            previous.getMetricTime())).toDouble();
                    // apply it to the physical
                    endMarker.setTime(previous.getTime()
                                      + Math.round((timedMetrical.getTime() - previous
                                              .getTime())
                                                   * ratio));
                }
            }
            assert checkMonotony();
        }
        return added;
    }

    /**
     * this method works like add(TimedMetrical), except that in case of an already
     * existing TimedMetrical, this existsting one is replaced by the parameter.
     * 
     * @param timedMetrical the to be inserted TimedMetrical
     * @return if something was added
     */
    public boolean replace(TimedMetrical timedMetrical) {
        if (hasTimedMetrical(timedMetrical.getMetricTime())) {
            remove(getTimedMetrical(timedMetrical.getMetricTime()));
        }
        return add(timedMetrical);
    }

    //	public TimedMetrical add(KeyMarker km) {
    //		int metricIndices[] = findAll(km);
    //		
    //		for (int i = 0; i < metricIndices.length; i++) {
    //			Marker marker = (Marker)get(metricIndices[i]);
    //			if(marker instanceof TimedMetrical){
    //				TimedMetrical tm = (TimedMetrical) marker;
    //				if(!tm.getTimeStamp().equals(km.getTimeStamp()))
    //					throw new IllegalArgumentException("TimedMetrical doesn't fit in existing
    // meter
    // scheme: ");
    //			}
    //		}
    //
    //		super.add(km);
    //		assert checkMonotony();
    //		return null;
    //	}
    //
    //
    //	/**
    //	 * Add a new TimeSignatureMarker indicating a change of time signature. If
    //	 * there was already a TimeSignature at the same metrical Position, that
    //	 * one will be returened and the new Marke will not be added.
    //	 * @param tsigMarker The new TimeSignatureMarker.
    //	 * @return TimeSignatureMarker null if successful, else the exisiting
    // marker at that
    //	 * position.
    //	 */
    //	public TimeSignatureMarker add(TimeSignatureMarker tsigMarker) {
    //		int metricIndex = lookup(tsigMarker.getMetricTime(),
    // TimeSignatureMarker.class);
    //
    //		if (metricIndex >= 0) {
    //			Marker marker = (Marker) get(metricIndex);
    //			//We allow a TimedMetrical and a TimeSignatureMarker at the same metrical
    // time
    //			if (marker.getClass().isAssignableFrom(TimeSignatureMarker.class))
    //				return (TimeSignatureMarker) marker;
    //		}
    //		super.add(tsigMarker);
    //		return null;
    //	}
    //	

    /**
     * Gets the metric time for s given physical time. It is calculated by retrieving the
     * surrounding timed metricals and interpolating the time.
     * 
     * @param timeMicros the physical time
     * @return the metric time
     */
    public Rational getMetricTime(long timeMicros) {

        TimedMetrical[] timedMetricals = lookupBounds(timeMicros);

        long p1 = timedMetricals[0].getTime();
        long p2 = timedMetricals[1].getTime();
        long p_dist = p2 - p1;

        Rational mr1 = timedMetricals[0].getMetricTime();
        // Rational mr2 = timedMetricals[1].getMetricTime();
        // Rational m_dist = mr2.sub(mr1);

        double md1 = timedMetricals[0].getMetricTime().toDouble();
        double md2 = timedMetricals[1].getMetricTime().toDouble();
        double md_dist = md2 - md1;

        double tempo = md_dist / p_dist;

        double relMTime = (timeMicros - p1) * tempo;
        int denom = 65536;
        int numer = (int) Math.floor(relMTime * denom);

        return mr1.add(new Rational(numer, denom));
    }

    //TODO: refactor get-Methods
    /**
     * Get the keyMarker before a given time, or after if there is none before.
     *  
     * @param metricTime The metric time to look for.
     * @return The last KeyMarker before, or if there is none, the first after.
     */
    public KeyMarker getKeyMarker(Rational metricTime) {
        Marker ks[] = lookupBounds(metricTime, KeyMarker.class);
        if (ks[0] != null)
            return (KeyMarker) ks[0];
        return (KeyMarker) ks[1];
    }

    /**
     * Gets the metric duration of the anacrusis, the result can be 0 (no anacrusis) or a
     * positive value smaller that the measureDuration.
     * 
     * @return The metric duration of the anacrusis.
     */
    public Rational getAnacrusis() {
        cleanAnacrusis();
        TimeSignatureMarker tsm = getTimeSignatureMarker(Rational.ZERO);
        return tsm.getMetricTime();
    }

    /**
     * The Method cleanAnacrusis ensures that an anacrusis at the beginning of a piece is
     * shorter than a measure of the following time signature.
     * 
     * @return int The number of measures the first time signature marker has been moved
     *         towards the beginning.
     */
    public int cleanAnacrusis() {
        int measuresChanged = 0;
        //		Rational zeroRational = new Rational(0);
        // get the first TimeSignatureMarker
        TimeSignatureMarker tsm = getTimeSignatureMarker(new Rational(0));
        if (tsm != null) {
            // if first Marker, make sure, the anacrusis is shorter than one
            // measure.
            Rational measureDuration = tsm.getTimeSignature().getMeasureDuration();
            while (tsm.getMetricTime().isGreaterOrEqual(measureDuration)) {
                measuresChanged++;
                tsm.setMetricTime(tsm.getMetricTime().sub(measureDuration));
            }
        }
        if (measuresChanged > 0) {
            remove(tsm);
            add(tsm);
        }
        return measuresChanged;
    }

    /**
     * Tries to find a Marker of the specified type with a given metric time. If such
     * markers exist, the corresponding list index of the first marker is returned.
     * Otherwise a negative index is returned that indicates the position where the
     * searched metric time was expected: index = -return-1
     * 
     * @param metricTime Rational
     * @param type Class
     */
    protected int lookup(Rational metricTime, Class type) {
        //		if (metricTime.sign() < 0)
        //			throw new IllegalArgumentException("metric Time must not be
        // negative");
        Marker marker = new TimedMetrical(0L, metricTime);
        return lookup(marker, type);
    }

    /**
     * Tries to find a Marker of the specified type. If such markers exist, the
     * corresponding list index of the first marker is returned. Otherwise a negative
     * index is returned that indicates the position where the searched metric time was
     * expected: index = -return-1
     * 
     * @param marker Marker
     * @param type Class
     * @return index list index of the first matching marker
     */
    protected int lookup(Marker marker, Class type) {
        int a[] = findAll(marker);
        if (a[0] < 0)
            return a[0];
        for (int i = a[0]; i <= a[1]; i++)
            if (type.isAssignableFrom(get(i).getClass()))
                return i;
        return -a[0] - 1;
    }

    /**
     * Tries to find a TimedMetrical with a given time. If such a marker exists the
     * corresponding list index is returned. Otherwise a negative number is returned that
     * indicates the index where the searched time was expected: index = -return-1
     * 
     * @param time The time to look for.
     * @return index See above.
     */
    protected int lookupTimedMetrical(long time) {
        assert size() > 1;
        //		if (time < 0)
        //			throw new IllegalArgumentException("metric Time must not be
        // negative");

        // modified binary search 
        int left = 0, right = size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            // find the first TimedMetrical going up.
            while (mid <= right && !(get(mid) instanceof TimedMetrical)) {
                mid++;
            }
            // If no TimedMetrical found, find the first TimedMetrical going
            // down.
            if (mid > right) {
                mid = (left + right) / 2;
                while (mid >= left && !(get(mid) instanceof TimedMetrical)) {
                    mid--;
                }
            }
            // If still no TimedMetrical found,then finish.
            if (mid < left)
                break;
            assert get(mid) instanceof TimedMetrical;
            TimedMetrical marker = (TimedMetrical) get(mid);

            if (marker.getTime() == time)
                return mid;
            if (marker.getTime() < time)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return -left - 1;
    }

    /**
     * Get the greatest marker of the given class, that has a metric time less than or equal
     * to the argument <code>metricTime</code>.
     * 
     * @param metricTime The time to search for.
     * @param markerClass The class to search for.
     * @return See above, may be null if no markers of the given class are present.
     */
    public Marker lastBefore(Rational metricTime, Class<?> markerClass) {
        Marker[] markers = lookupBounds(metricTime, markerClass);
        // if both markers less than metricTime, return the greater one
        if (markers[1] != null && markers[1].getMetricTime().isLessOrEqual(metricTime)) {
            return markers[1];
            // TODO check this line
//        } else if (markers[0] == null && markers[1] != null) {
//            return markers[1];
        } else {
            return markers[0];
        }
    }

    /**
     * The method lookupBounds returns the matching marker of the given class for this
     * metric time. If there is none (the normal case) it returns the next larger and the
     * next smaller marker. It there are only smaller or greater markers, the two markers
     * closest to the metric time are returned. If there are more than one marker, the first 
     * and the last are returned. 
     * 
     * @param metricTime
     * @return A TimedMetrical array of size two. If there is a matching marker it is in
     *         the first element. The second element then contains the element on right of
     *         the found marker. If there is no matching marker the nearest left and
     *         nearest right around the given point are returned.
     */
    public Marker[] lookupBounds(Rational metricTime, Class markerClass) {
        if (!Marker.class.isAssignableFrom(markerClass))
            throw new IllegalArgumentException(
                    this.getClass()
                        + ".lookupBounds(Rational metricTime, "
                        + "Class markerClass) may only be called with classes "
                        + "as argument, that inhertit from Marker.");
        //		if (metricTime.sign() < 0)
        //			throw new IllegalArgumentException("metric Time must not be
        // negative");

        Marker markers[] = (Marker[]) Array.newInstance(markerClass, 2);
        // Marker[2];
        markers[0] = markers[1] = null;

        int pos = lookup(metricTime, markerClass);
        found: if (pos >= 0) {
            // Marker at exact position found.
            markers[0] = (Marker) get(pos);
            int right = pos;
            while (right < size()
                   && metricTime.equals(((Marker) get(right)).getMetricTime())) {
                right++;
            }
            if(right == size())
            	right--;
            while (right > pos &&
            		!markerClass.isAssignableFrom(get(right).getClass()))
            	right--;
            markers[1] = (Marker) get(right);
            break found;
        } else {
            // Marker not found, calc search position. Search to the left.
            pos = -pos - 1;
            int left = findPrevious(pos - 1, markerClass);
            int right = findNext(pos, markerClass);
            if (left < 0) {
                left = right;
                right = findNext(right + 1, markerClass);
                if (right < 0) {
                    right = left;
                    left = -1;
                }
            } else if (right < 0) {
                right = left;
                left = findPrevious(left - 1, markerClass);
                if (left < 0) {
                    left = right;
                    right = -1;
                }
            }
            if (left >= 0)
                markers[0] = (Marker) get(left);
            if (right >= 0)
                markers[1] = (Marker) get(right);
            //            while (left >= 0 && !(markerClass.isAssignableFrom(get(left).getClass())))
            //                left--;
            //            if (left >= 0) //ToDo: casting throws exception
            //                markers[0] = (Marker) get(left);
            //            else {
            //                left = 0;
            //                // look for the first
            //                while (left < size() &&
            // !markerClass.isAssignableFrom(get(left).getClass()))
            //                    left++;
            //                assert (markerClass.equals(TimedMetrical.class)) ? left >= 0 && left <
            // size() : true;
            //                if (left >= size())
            //                    markers[0] = null;
            //                else
            //                    markers[0] = (Marker) get(left);
            //            }
            //            Rational leftMetricTime = null;
            //            if (markers[0] != null)
            //                leftMetricTime = markers[0].getMetricTime();
            //            int size = size();
            //            while (right < size
            //                   && (!(markerClass.isAssignableFrom(get(right).getClass())) || ((Marker)
            // get(right)).getMetricTime()
            //                           .equals(leftMetricTime))) {
            //                right++;
            //            }
            //            if (right < size())
            //                markers[1] = (Marker) get(right);
            //            else
            //                markers[1] = null;
        }
        return markers;
    }

    private int findPrevious(int pos, Class cls) {
        if (pos >= size() || pos < 0) {
            return -1;
        }
        do {
            if (cls.isInstance(get(pos)))
                return pos;
            pos--;
        } while (pos >= 0);
        return -1;
    }

    private int findNext(int pos, Class cls) {
        if (pos < 0 || pos >= size()) {
            return -1;
        }
        do {
            if (cls.isInstance(get(pos)))
                return pos;
            pos++;
        } while (pos < size());
        return -1;
    }

    /**
     * Returns the first TimedMetrical with a metrical time greater than the argument.
     * 
     * @param Rational the current metric time
     * @return The next TimedMetrical of MetricalTimeLine
     */
    public TimedMetrical getNextTimedMetrical(Rational currentMetricTime) {
        if (currentMetricTime.sign() < -1)
            throw new IllegalArgumentException("metric Time must not be negative");

        TimedMetrical nextTimedMetrical = null;
        // TODO make more efficient
        for (int i = 0; i < size(); i++) {
            if (get(i) instanceof TimedMetrical) {
                nextTimedMetrical = (TimedMetrical) get(i);
                if (nextTimedMetrical.getMetricTime().isGreater(currentMetricTime))
                    return nextTimedMetrical;
            }
        }
        return null;
    }

    /**
     * Returns the last TimedMetrical with a metrical time more less than the argument.
     * 
     * @param Rational the current metric time
     * @return The next TimedMetrical of MetricalTimeLine
     */
    public TimedMetrical getPreviousTimedMetrical(Rational currentMetricTime) {
        if (currentMetricTime.sign() < -1)
            throw new IllegalArgumentException("metric Time must not be negative");

        TimedMetrical nextTimedMetrical = null;

        // TODO make more efficient
        for (int i = size() - 1; i >= 0; i--) {
            if (get(i) instanceof TimedMetrical) {
                nextTimedMetrical = (TimedMetrical) get(i);
                if (nextTimedMetrical.getMetricTime().isLess(currentMetricTime))
                    return nextTimedMetrical;
            }
        }
        return null;
    }

    /**
     * The method lookupBounds returns the matching marker for this metric time. If there
     * is none (the normal case) it returns the next larger and the next smaller marker.
     * 
     * @param metricTime
     * @return A TimedMetrical array of size two. If there is a matching marker it is in
     *         the first element. The second element then contains the element on right of
     *         the found marker. If there isn't a matching marker the nearest left and
     *         nearest right around the given point are returned.
     */
    protected TimedMetrical[] lookupBounds(long time) {
        TimedMetrical markers[] = new TimedMetrical[2];

        //		if (time < 0)
        //			throw new IllegalArgumentException("metric Time must not be
        // negative");
        markers[0] = markers[1] = null;

        int pos = lookupTimedMetrical(time);
        if (pos >= 0) {
            // Marker at exact position found
            markers[0] = (TimedMetrical) get(pos);
            // int right = pos + 1; ersetzt durch die folgenden drei Zeilen
            int right = pos;
            while (right < size()
                        && (!(get(right) instanceof TimedMetrical) || ((TimedMetrical) get(right))
                                .getTime() == time)){
                right++;
            }
            if (right != size())
                markers[1] = (TimedMetrical) get(right);
            else {
                // go to left if no marker found to the right
                right = pos;
                int left = pos - 1;
                while (left >= 0
                       && (!(get(left) instanceof TimedMetrical) || ((TimedMetrical) get(left))
                               .getTime() == time)) {
                    left--;
                }
                if (left >= 0) {
                    markers[0] = (TimedMetrical) get(left);
                    markers[1] = (TimedMetrical) get(right);
                } else {
                    markers[0] = zeroMarker;
                    markers[1] = endMarker;
                }
            }

        } else {
            // Marker not found, calc search position.
            pos = -pos - 1;
            // search to the left.
            int left, right;
            left = pos - 1;
            right = pos;
            while (left >= 0 && !(get(left) instanceof TimedMetrical)){
                left--;
            }
            // there should always be a TimedMetrical at zero.

            // the following assertion does not hold, as the first element may be a
            //  TimeSignatureMarker... But this is accounted for later
            //            assert(left >= 0);
            int size = size();
            while (right < size && !(get(right) instanceof TimedMetrical))
                right++;
            if (right >= size) {
                // no right TimedMetrical found
                // there should always be at least two TimedMetricals,
                // so if none to the right we must have two to the left.
                assert left >= 1;
                right = left;
                left = left - 1;
                while (left >= 0 && !(get(left) instanceof TimedMetrical)){
                    left--;
                }
            }
            if (left < 0) { //no TimedMetrical could be found => use default one
                markers[0] = zeroMarker;
            } else {
                markers[0] = (TimedMetrical) get(left);
            }
            if (right < 0) { //no TimedMetrical could be found => use default one
                markers[1] = (TimedMetrical) endMarker;
            } else {
                markers[1] = (TimedMetrical) get(right);
            }
        }
        return markers;
    }

    /**
     * Method checkMonotony checks if the monotonic ordering of the metricals and if the
     * have TimeStamps if they are odered too.
     * 
     * @return boolean
     */
    protected boolean checkMonotony() {
        Marker previousMarker = null;
        TimedMetrical previousTimedMetrical = null;
        for (Marker marker: this ) {
            // Check the order of the Markers for metrical time.
            if (previousMarker != null)
                if (marker.getMetricTime().isLess(previousMarker.getMetricTime()))
                    return false;
            // Checks the order of the TimsStamps.
            if (marker instanceof TimedMetrical && previousTimedMetrical != null)
                if (((TimedMetrical) marker).getTime() < previousTimedMetrical.getTime())
                    return false;
            previousMarker = marker;
            if (marker instanceof TimedMetrical)
                previousTimedMetrical = (TimedMetrical) marker;
        }
        return true;
    }

    // public void markTime(TimeStamp timeStamp, Rational metricTime) {}

    /**
     * Converts metrical time to physical time by linear interpolation of the Time flow,
     * that means, the tempo between two TimedMetricals is constant.
     * 
     * @param metricTime the metric time to look for.
     * @return Time in milliseconds.
     */
    public long getTime(Rational metricTime) {
        TimedMetrical markers[] = (TimedMetrical[]) lookupBounds(metricTime,
                TimedMetrical.class);
        TimedMetrical leftMarker = markers[0];
        TimedMetrical rightMarker = markers[1];
        // **** added by Jan Kramer ****
        if (metricTime.toDouble() < 0) {
            System.out.println("Calculate getTime of Rational < 0" + metricTime);
        }

        // **** end of added by Jan Kramer
        if (leftMarker != null && leftMarker.getMetricTime().isEqual(metricTime))
            return leftMarker.getTime();
        if (rightMarker != null && rightMarker.getMetricTime().isEqual(metricTime))
            return rightMarker.getTime();
        if (leftMarker != null && rightMarker != null && leftMarker == rightMarker)
            return -1; // @@ Error

        double y1 = leftMarker.getTime();
        double y2 = rightMarker.getTime();
        double x1 = leftMarker.getMetricTime().toDouble();
        double x2 = rightMarker.getMetricTime().toDouble();
        double slope = (y2 - y1) / (x2 - x1);

        long time = Math.round(y1 + slope * (metricTime.toDouble() - x1));
        return time;
    }

    /**
     * Method getTimeSignature gets the valid TimeSignature at a given metrical time. The
     * returned object can have a smaller or, in case of the first TimeSignatureMarker, a
     * greater metrical time.
     * 
     * @param metricTime Rational
     * @return timeSignature TimeSignature
     */
    public TimeSignatureMarker getTimeSignatureMarker(Rational metricTime) {
        return (TimeSignatureMarker) lastBefore(metricTime, TimeSignatureMarker.class);
    }

    /**
     * Method getTimedMetrical returns the valid TimedMetrical for a given metrical time.
     * The returned object can have a smaller metrical time.
     * 
     * @param metricTime Rational
     * @return timedMetrical TimedMetrical
     */
    public TimedMetrical getTimedMetrical(Rational metricTime) {
        Marker tsm[] = lookupBounds(metricTime, TimedMetrical.class);
        if (tsm[0] == null)
            return (TimedMetrical) tsm[1];
        return (TimedMetrical) tsm[0];
    }

    /**
     * Returns the MetricalTimeLine as a String.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            sb.append(get(i).toString());
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Gets the Tempo at that metric position in Quarters per Minute.
     * 
     * @param mTime
     * @return
     */
    public double getTempoQPM(Rational mTime) {
        return getTempo(mTime, 4);
    }

    /**
     * Gets the Tempo at that metric position in bpm.
     * 
     * @param mTime
     * @return
     */
    public double getTempo(Rational mTime, int beatDenom) {
        Marker[] markers = lookupBounds(mTime, TimedMetrical.class);
        TimedMetrical timedMetricals[] = new TimedMetrical[2];
        timedMetricals[0] = (TimedMetrical) markers[0];
        timedMetricals[1] = (TimedMetrical) markers[1];

        double y1 = timedMetricals[0].getTime();
        double y2 = timedMetricals[1].getTime();
        double x1 = timedMetricals[0].getMetricTime().toDouble();
        double x2 = timedMetricals[1].getMetricTime().toDouble();

        while ((x2 == x1 || y2 == y1) && timedMetricals[0] != null) {
            timedMetricals[0] = getNextTimedMetrical(timedMetricals[0].getMetricTime());
            // If the calculation is not possible
            if (timedMetricals[0] == null)
                return -1;
            x2 = timedMetricals[0].getMetricTime().toDouble();
            y2 = timedMetricals[0].getTime();
        }

        return (((x2 - x1) * beatDenom) / ((y2 - y1) / 1000000)) * 60;
        // Wolfram: 1 second = 1000000 microseconds
    }

    /**
     * The method returns the tempo in bpm. The denominator of the beat unit has to be
     * known, e.g. 8 for eighth beats or 2 for alla breve. The value '-1' is returned when
     * the calculation of the tempo is not possible.
     * 
     * @param time The time for which the tempo shall be calculated.
     * @param beatDenom The denominator.
     * @return The tempo in BPM.
     */
    public double getTempo(long time, int beatDenom) {
        TimedMetrical[] timedMetricals = lookupBounds(time);

        double y1 = timedMetricals[0].getTime();
        double y2 = timedMetricals[1].getTime();
        double x1 = timedMetricals[0].getMetricTime().toDouble();
        double x2 = timedMetricals[1].getMetricTime().toDouble();

        while (x2 == x1 && y2 == y1 && timedMetricals[0] != null) {
            timedMetricals[0] = getNextTimedMetrical(timedMetricals[0].getMetricTime());
            // If the calculation is not possible
            if (timedMetricals[0] == null)
                return -1;
            x2 = timedMetricals[0].getMetricTime().toDouble();
            y2 = timedMetricals[0].getTime();
        }

        return (((x2 - x1) * beatDenom) / ((y2 - y1) / 1000000)) * 60;
        //Wolfram: 1
        // second =
        // 1000000
        // microseconds
    }

    /**
     * The method returns the tempo in BPM (for four beats per bar). The value '-1' is
     * returned when the calculation of the tempo is not possible.
     * 
     * @param time The time at which to get the Time.
     * @return The calculated tempo in BPM.
     */
    public double getTempo(long time) {
        return getTempo(time, 4);
    }

    /**
     * The method returns the tempo in BPM of the whole song.
     * 
     * @return An array of tempos and their physical times
     */
    public double[][] tempoTable() {
        double[][] tempi = null;
        int changed = 0;
        long lastTime = -2;
        long time;

        // Process all objects of the MetricalTimeLine to get the
        // number of tempo changes
        for (int i = 0; i < size(); i++) {
            if (get(i) instanceof TimedMetrical) {
                time = ((TimedMetrical) get(i)).getTime();
                if (time != lastTime) {
                    changed++;
                    lastTime = ((TimedMetrical) get(i)).getTime();
                }
            }
        }

        tempi = new double[changed - 1][2];
        lastTime = -2;
        int index = 0;

        // Process all objects of the MetricalTimeLine
        for (int i = 0; i < size() && index < changed - 1; i++) {
            if (get(i) instanceof TimedMetrical) {
                time = ((TimedMetrical) get(i)).getTime();
                if (time != lastTime) {
                    // If there is no time signature use the default value
                    int numerator = 4;
                    if (getTimeSignatureMarker(getMetricTime(time)) != null)
                        numerator = getTimeSignatureMarker(getMetricTime(time))
                                .getTimeSignature().getNumerator();

                    tempi[index][0] = getTempo(time, numerator);

                    tempi[index][1] = time;
                    index++;

                    lastTime = ((TimedMetrical) get(i)).getTime();
                }
            }
        }
        return tempi;
    }

    /**
     * The method returns the time signatures of the whole metrical time line (i.e. normally one piece).
     * 
     * @return a array of time signatures and their physical times [index][0]:
     *         TimeSignatureNumerator [index][1]: TimeSignatureDenominator [index][2]:
     *         getTime [index][3]: TimeSignatureMarkerNumerator [index][4]:
     *         TimeSignatureMarkerDenominator
     *  
     */
    public long[][] getTimeSignature() {
        long[][] timeSig = null;
        int changed = 0;
        Rational lastMetricTime = new Rational(-2);
        Rational metricTime;

        // Process all objects of the MetricalTimeLine to get the
        // number of time signature changes
        for (int i = 0; i < size(); i++) {
            if (get(i) instanceof TimeSignatureMarker) {
                metricTime = ((TimeSignatureMarker) get(i)).getMetricTime();
                if (!metricTime.isEqual(lastMetricTime)) {
                    changed++;
                    lastMetricTime = ((TimeSignatureMarker) get(i)).getMetricTime();
                }
            }
        }

        timeSig = new long[changed][5];
        lastMetricTime = new Rational(-2);
        int index = 0;

        // Process all objects of the MetricalTimeLine
        for (int i = 0; i < size() && index < changed; i++) {
            if (get(i) instanceof TimeSignatureMarker) {
                metricTime = ((TimeSignatureMarker) get(i)).getMetricTime();
                if (!metricTime.isEqual(lastMetricTime)) {
                    timeSig[index][0] = getTimeSignatureMarker(metricTime)
                            .getTimeSignature().getNumerator();
                    timeSig[index][1] = getTimeSignatureMarker(metricTime)
                            .getTimeSignature().getDenominator();

                    timeSig[index][2] = getTime(metricTime);
                    timeSig[index][3] = getTimeSignatureMarker(metricTime)
                            .getMetricTime().getNumer();
                    timeSig[index][4] = getTimeSignatureMarker(metricTime)
                            .getMetricTime().getDenom();
                    index++;

                    lastMetricTime = ((TimeSignatureMarker) get(i)).getMetricTime();
                }
            }
        }
        return timeSig;
    }

    /**
     * The method returns the tempo in bpm and the time signature of the whole song as
     * string.
     * 
     * @return same infos about the metrical time line
     */
    public String getInfo() {
        // The string to return
        String ret = "[METRICALTIMELINE ";

        double[][] tempi = tempoTable();
        ret += "\n\ttempi:";
        for (int i = 0; i < tempi.length; i++) {
            ret += "\n\t\t " + (int) java.lang.Math.rint(tempi[i][0]) + " bpm at "
                   + (int) tempi[i][1] + " ms ";
            if (tempi[i][0] > 0)
                ret += "(IBI = " + (int) (60000 / tempi[i][0]) + ")";
        }

        long[][] timeSig = getTimeSignature();
        ret += "\n\ttime signatures:";
        for (int i = 0; i < timeSig.length; i++)
            ret += "\n\t\t " + timeSig[i][0] + " | " + timeSig[i][1] + " at "
                   + timeSig[i][2] + " ms";

        return ret + "\n]";
    }

    /**
     * The method expandChords expands all chords of MetricalTimeLine into the specified
     * container. To create the notes, the pattern p and the voicing v is used.
     * 
     * @param cont Container
     * @param p
     * @param v
     */
    public void expandChords(Container cont, Pattern p, Voicing v) {

        Marker actualMarker = null, nextMarker = null;

        int i = 0;
        // Suche nach dem ersten Marker vom Typ ChordSymbol
        while (i < size()) {
            actualMarker = (Marker) get(i);
            i++;
            if (actualMarker instanceof ChordSymbol) {
                break;
            }
        }

        // Mit hilfe des n?chsten Marker vom Typ ChordSymbol ergibt sich
        // ein Zeitabschnitt, der mit diesem Akkord begleitet wird
        while (i < size()) {
            nextMarker = (Marker) get(i);
            i++;
            if (nextMarker instanceof ChordSymbol) {
                Rational metricDuration = nextMarker.getMetricTime().sub(
                        actualMarker.getMetricTime());
                long duration = getTime(nextMarker.getMetricTime())
                                - getTime(actualMarker.getMetricTime());
                // Begeitung berechnen
                p.expandChordSym(cont, metricDuration,
                        getTime(actualMarker.getMetricTime()), duration, ((ChordSymbol) actualMarker), v);
                // Den n?chsten Akkord zum aktuellen Akkord machen
                actualMarker = nextMarker;
            }
        }

    }

    /**
     * This method returns a array of beat times of all beatMarkers with the given
     * metrical level, i.e. beatLevel. <BR>
     * Note that the unknown beat level '-1' will be treated as beat level '1'.
     * 
     * @param beatLevel the beat level to process
     * @return a array of beatMarkers for the level
     * @see de.uos.fmt.musitech.applications.beattracking.BeatMarkers
     */
    public int[] getBeatMarkers(double beatLevel) {

        int[] bMarkers = new int[this.size()];
        int index = 0;
        BeatMarker bMarker = null;

        for (int i = 0; i < this.size(); i++) {
            if (!(this.get(i) instanceof BeatMarker))
                continue;

            bMarker = (BeatMarker) get(i);

            if (bMarker.getBeatLevel() == beatLevel
                || (beatLevel == 1 && bMarker.getBeatLevel() == -1)) {
                bMarkers[index] = (int) bMarker.getTime();
                index++;
            }
        }

        // Compact the array
        int[] beatMarkers = new int[index + 1];
        for (int i = 0; i < index + 1; i++) {
            beatMarkers[i] = bMarkers[i];
        }

        return beatMarkers;
    }

    /**
     * @param r The current metric time
     * @return The TimeSignature at this position
     */
    public Rational getCurrentTimeSignature(Rational r) {

        if (DebugState.DEBUG_SCORE)
            System.out.println("MetricalTimeLine.getCurrentTimeSignature(" + r + ")");

        TimeSignatureMarker tsm = null;
    	TimeSignatureMarker lastTsm = null;

        // collect all TimeSignatureMarker in markers which are before r
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            Marker marker = (Marker) iter.next();

            if (marker instanceof TimeSignatureMarker) {
            	lastTsm = tsm;
            	tsm = (TimeSignatureMarker) marker;
                if (DebugState.DEBUG_SCORE)
                    System.out.println("TimeSignatureMarker found. Position: "
                                       + tsm.getMetricTime() + ", TimeSignature:"
                                       + tsm.getTimeSignature());

                if (tsm.getMetricTime().isGreater(r)) {
                    
                    if (DebugState.DEBUG_SCORE)
                        System.out.println("TimeSignatureMarker is before r. Position: "
                                           + tsm.getMetricTime() + ", TimeSignature:"
                                           + tsm.getTimeSignature());
                    tsm = lastTsm;
                    break;
                } 
            }
        }
        // now markers holds all TimeSignatureMarkers which are before r

        Rational tsmTimeSig = new Rational(0);
        if (tsm != null) {
            //      get TimeSignature
            tsmTimeSig = new Rational(tsm.getTimeSignature().getMeasureDuration());
        } else {
            if (DebugState.DEBUG_SCORE)
                System.out.println("MetricalTimeLine.getCurrentTimeSignature(" + r
                                   + "): Cannot find a TimeSignatureMarker before " + r
                                   + ".");
        }

        if (DebugState.DEBUG_SCORE)
            System.out.println("TimeSignature at current position is " + tsmTimeSig + "\n");           

        return tsmTimeSig;
    }

    /**
     * Converts the given rational into a representation of measures, beats and a
     * remainder (MBR). The return value is an int-array containing:
     * <ul>
     * <li>0: measure</li>
     * <li>1: beat</li>
     * <li>2: numerator remainder</li>
     * <li>3: denominator remainder</li>
     * </ul>
     * 
     * Procedure:
     * <UL>
     * <LI>Go backwards to last TimeSignatureMarker.</LI>
     * <LI>Divide difference (a Rational) by length of measure (for example: 6/8 equals
     * new Rational(6,8)).</LI>
     * <LI>Divide remainder by denominator (for example 8ths) -> result is beats.</LI>
     * <LI>remainder remains.</LI>
     * </UL>
     * This has to be repeated until beginning of metricalTimeLine is reached (only
     * measures do matter now). <BR>
     * For now anacrusis are ignored. <BR>
     * <BR>
     * Since the way this program counts measures and beats and the way users count
     * measures and beats are different, we have to adjust the values:
     * <UL>
     * <LI>program: first measure in song is measure 0, first beat in measure is 0</LI>
     * <LI>user: first measure in song is measure 1, first beat in measure is 1</LI>
     * </UL>
     * So here we have to add 1 to measure and beat here.
     * 
     * @param r The metric time to convert as a Rational.
     * @return The MBR representation as an array containing the four components listed
     *         above.
     *  
     */
    /*
     * TODO correct method errors gesetzt (TimeSignature = Achtel) gekuerzt
     * MetricalTimeLine.toMeasureBeatRemainder 0/0 m0 b0 13/8 m1 b2 +1/8 14/8 7/4 m1 b3
     * 15/8 m1 b3 +1/8 16/8 2/1 m2 b0 17/8 m2 b0 +1/8 18/8 9/4 m2 b1 19/8 m2 b1 +1/8 20/8
     * 5/2 m2 b2 21/8 m2 b2 +1/8 22/8 11/4 m2 b3 23/8 m2 b3 +1/8 24/8 3/1 m3 b0 25/8 m3 b0
     * +1/8
     */
    public int[] toMeasureBeatRemainder(Rational r) {
        boolean debug = false;
        //		boolean debug = true;
        if (debug)
            System.out.println("MetricalTimeLine.toMeasureBeatRemainder(" + r + ")");

        int[] output = new int[4];
        ArrayList markers = new ArrayList();
        Marker marker;

        // collect all TimeSignatureMarker in markers which are before r
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            marker = (Marker) iter.next();

            if (marker instanceof TimeSignatureMarker) {
                TimeSignatureMarker tsm = (TimeSignatureMarker) marker;
                if (debug)
                    System.out.println("TimeSignatureMarker found. Position: "
                                       + tsm.getMetricTime() + ", TimeSignature:"
                                       + tsm.getTimeSignature());

                if (tsm.getMetricTime().isLessOrEqual(r)) {
                    markers.add(marker);
                    if (debug)
                        System.out.println("TimeSignatureMarker is before r. Position: "
                                           + tsm.getMetricTime() + ", TimeSignature:"
                                           + tsm.getTimeSignature());
                } else
                    break;
            }
        }
        // now markers holds all TimeSignatureMarkers which are before r

        // go backwards to last TimeSignatureMarker

        // set count to last marker -1 (start value)
        int count = markers.size();
        TimeSignatureMarker tsm;
        Rational tsmTimeSig;
        Rational tsmMetricTime;
        Rational difference;
        int measure = 0;
        int beat = 0;
        Rational remainder = new Rational(0);

        while (count > 0) {
            if (debug)
                System.out.println("count = " + count);
            //      last TimeSignatureMarker
            tsm = (TimeSignatureMarker) markers.get(count - 1);
            //      get TimeSignature
            tsmTimeSig = new Rational(tsm.getTimeSignature().getMeasureDuration());
            //      get MetricTime from last TimeSignatureMarker
            tsmMetricTime = new Rational(tsm.getMetricTime());

            // (1) get difference between r and last TimeSignatureMarker
            difference = r.sub(tsmMetricTime);

            if (count == markers.size()) {
                // (2) divide difference by size of measure
                measure = (int) (difference.div(tsmTimeSig).toDouble());
                Rational beats = difference.mod(tsmTimeSig);
                beat = (int) beats.div(1, tsmTimeSig.getDenom()).toDouble();
                remainder = beats.mod(1, tsmTimeSig.getDenom());
            } else {
                // loop: only measures do matter now
                // increase measure by measures
                measure = measure + (int) difference.div(tsmTimeSig).toDouble();
            }
            count--;
            r = tsmMetricTime;
        }

        /*
         * since the way this program counts measures and beats and the way users count
         * measures and beats are different, we have to adjust the values: program: first
         * measure in song is measure 0, first beat in measure is 0 user: first measure in
         * song is measure 1, first beat in measure is 1
         * 
         * so here we have to add 1 to measure and beat:
         */
        measure++;
        beat++;

        output[0] = measure;
        output[1] = beat;
        output[2] = remainder.getNumer();
        output[3] = remainder.getDenom();

        if (debug)
            System.out.println(" = measure: " + (output[0] - 1) + ", beat: "
                               + (output[1] - 1) + " remainder " + output[2] + "/"
                               + output[3]);
        if (debug)
            System.out.println("");

        return output;
    }

    /**
     * Gets the metrical time of a certain measure, give as a measure number (counting
     * from 1).
     * 
     * @param measureNum The index of the measure to look for.
     * @return The metric time (as fraction in whole notes).
     */
    public Rational getMeasureTime(int measureNum) {
        return fromMeasureBeatRemainder(new int[] {measureNum, 0, 0, 1});
    }

    /**
     * Calculates a metric time from an integer-array, which contains:
     * <ul>
     * <li>0: measure</li>
     * <li>1: beat</li>
     * <li>2: numerator rest</li>
     * <li>3: denominator rest</li>
     * </ul>
     * <BR>
     * Since the way this program counts measures and beats and the way users count
     * measures and beats are different, we have to adjust the values:
     * <UL>
     * <LI>program: first measure in song is measure 0, first beat in measure is 0</LI>
     * <LI>user: first measure in song is measure 1, first beat in measure is 1</LI>
     * </UL>
     * So here we subtract 1 from measure and beat here.
     * 
     * @param values The array containing the four components listed above..
     * @return A Rational representing the metric time.
     */
    public Rational fromMeasureBeatRemainder(int values[]) {
		boolean debug = false;
//		boolean debug = true;
	   if (debug)
		   System.out.println("MetricalTimeLine.fromMeasureBeatRemainder(" + values[0] + "m , "+values[1]+"b , " + values[2] + "/" + values[3] + ")");

        Rational theMetricTime = new Rational(0);
        int measure = values[0];
        int beat = values[1];
        int remainder_numerator = values[2];
        int remainder_denominator = values[3];

        /*
         * since the way this program counts measures and beats and the way users count
         * measures and beats differently, we have to adjust the values: program: first
         * measure in song is measure 0, first beat in measure is 0 user: first measure in
         * song is measure 1, first beat in measure is 1
         * 
         * so here we have to substract 1 from measure and beat:
         */
       // measure--;
       // beat--;

        /*
         * This goes the other way around than toMeasureBeatRemainder: Count all measures,
         * beats and rest from beginning of metricalTimeLine including
         * timeSignatureChanges
         */

        /*
         * Deal with anacrusis: Get default TimeSignature and next TimeSignatureMarker. If
         * metric time between these TimeSignatureMarkers is more or less than full
         * measures, there is an anacrusis. In this case only count full measures.
         */

        // add measures to theMetricTime
        for (int count = 0; count < measure; count++) {
            // add a full measure (numerator/denominator) to theMetricTime
			theMetricTime = getNextMeasure(theMetricTime);
        }

        // add beats to theMetricTime
        for (int count = 0; count < beat; count++) {
            // add a beat (1/denominator) to theMetricTime
			theMetricTime = getNextBeat(theMetricTime);
        }

        // add rest to theMetricTime
        theMetricTime = theMetricTime.add(remainder_numerator, remainder_denominator);

		if (debug) System.out.println("MetricalTimeLine.fromMeasureBeatRemainder: theMetricTime = "+theMetricTime);
		if (debug) System.out.println("MetricalTimeLine.fromMeasureBeatRemainder: getTime = "+getTime(theMetricTime));
		if (debug) System.out.println();
		
        return theMetricTime;
    }

    /**
     * Just for testing.
     */
    public static void main(String[] arguments) {

        Piece work = new Piece();
        work.setSampleData();
        Context context = new Context(work);

        System.out.println("Creating metTL");
        MetricalTimeLine metTL = new MetricalTimeLine(context);
        System.out.println("Creating tMet's");
        TimedMetrical tMet = new TimedMetrical(20, new Rational(1));
        TimedMetrical tMet2 = new TimedMetrical(30, new Rational(3, 2));
        System.out.println("Adding tMet to metTL");
        metTL.add(tMet);
        System.out.println("Adding tMet2 to metTL");
        metTL.add(tMet2);
        System.out.println("Printing metTL");
        System.out.println(metTL);
        System.out.println("Adding tMet to metTL again");
        metTL.add(tMet);
        System.out.println("Printing metTL (no change)");
        System.out.println(metTL);
        System.out.println("Adding a tMet at 1 ms and 1/40 to metTL");
        metTL.add(new TimedMetrical(1, new Rational(1, 40)));
        System.out.println("Printing metTL (no change)");
        System.out.println(metTL);
        System.out.println("Adding a zeroMarker (again) will cause no change");
        metTL.add(new TimedMetrical(0, new Rational(0, 4)));
        System.out.println("Printing metTL (no change)");
        System.out.println(metTL);
        System.out.println("Adding a keySig to metTL");
        KeyMarker keySig = new KeyMarker(new Rational(1, 4), 15);
        keySig.setRootAccidental(1);
        keySig.setMode(Mode.MODE_DORIAN);
        keySig.setRoot('D');
        metTL.add(keySig);
        System.out.println(metTL);
        System.out.println("Adding a TimeSignatureMarker at 1/4");
        metTL.add(new TimeSignatureMarker(new TimeSignature(3, 4), new Rational(1, 4)));
        System.out.println(metTL);
        System.out.println("Adding a second TimeSignatureMarker at 1/8");
        metTL.add(new TimeSignatureMarker(new TimeSignature(4, 4), new Rational(1, 8)));
        System.out.println(metTL);
        System.out.println("Adding a tMet (there is already a TimeSignatureMarker)");
        metTL.add(new TimedMetrical(3, new Rational(1, 8)));
        System.out.println("Printing metTL");
        System.out.println(metTL);
        System.out.println("Adding a second keySig to metTL");
        //		KeyMarker keySig3 = new KeyMarker(new Rational(2, 4), 25);
        keySig.setRootAccidental(1);
        keySig.setMode(Mode.MODE_PHRYGIAN);
        keySig.setRoot('E');
        metTL.add(keySig);
        System.out.println(metTL);
        System.out.println("Lookup for a KeyMarker at 1/4.");
        System.out.println("List index of the first matching marker is: "
                           + metTL.lookup(new Rational(1, 4), KeyMarker.class));
        System.out.println("Lookup for a tMet at 1/4.");
        System.out.println("List index of the first matching marker is: "
                           + metTL.lookup(new Rational(1, 4), TimedMetrical.class));
        System.out.println("Lookup for a tMet at 3/4 returns the insertion position.");
        System.out
                .println("List index of the first matching marker is: "
                         + metTL.lookup(new Rational(3, 4), TimedMetrical.class) + "\n");

        System.out.print("Getting the TimeSignature at 1/5: ");
        System.out.println(metTL.getTimeSignatureMarker(new Rational(1, 5)));

        System.out.print("Getting the TimeSignature at 1/9: (to get the default)");
        System.out.println(metTL.getTimeSignatureMarker(new Rational(1, 9)));

        //ToDo: Check the output
        System.out.print("Getting the Tempo at 2 ms: ");
        System.out.println(metTL.getTempo(2));

        System.out.print("Getting the Tempo at 2 ms and beat=2: ");
        System.out.println(metTL.getTempo(2, 2));

        /**
         * Just for testing exceptions System.out.println("Adding five wrong keySig's to
         * metTL (inconsistency)");
         */
        //        KeyMarker keySig_G = new KeyMarker(new Rational(1, 7), 3);
        //        keySig_G.setRoot('G');
        //        System.out.println("Added: " + metTL.add(keySig_G));
        //        KeyMarker keySig_B = new KeyMarker(new Rational(1, 8), 2);
        //        keySig_B.setRoot('B');
        //        System.out.println("Added: " + metTL.add(keySig_B));
        //        KeyMarker keySig_E = new KeyMarker(new Rational(1, 7), -3);
        //        keySig_E.setRoot('E');
        //        System.out.println("Added:" + metTL.add(keySig_E));
        //        KeyMarker keySig_F = new KeyMarker(new Rational(-1, 8), -2);
        //        keySig_F.setRoot('F');
        //        System.out.println("Added: " + metTL.add(keySig_F));
        //        System.out
        //                .println("Added: " + metTL.add(new TimeSignatureMarker(new
        // TimeSignature(3, 4),
        // new
        // Rational(1, -4))));
        //        System.out.println("Printing metTL");
        //        System.out.println(metTL);
        System.out.println("Adding a TimeSignatureMarker at 0");
        metTL.add(new TimeSignatureMarker(new TimeSignature(5, 4), new Rational(0, 4)));
        System.out.println(metTL);
    }

    public boolean hasAnacrusis() {
        getTimeSignatureMarker(Rational.ZERO);
        // FIXME correct this
        if (false) /* n?chste TimeSignature marker vor Taktende */
            return true;
        else
            return false;
    }

    public void setTempoQPM(Rational metricTime, double qpm) {
        setTempo(metricTime, bpm2upw(qpm, 4));
    }
    
    /**
     * Convenience method for setting the tempo in beats per minute. Calls setTempo(int,
     * double) using bpm2upw.
     * 
     * @param metricTime The position where to start the new tempo.
     * @param bpm The tempo in beats per minute.
     * @param beatDenom The denominator of the beat length.
     */
    public void setTempo(Rational metricTime, double bpm, int beatDenom) {
        setTempo(metricTime, bpm2upw(bpm, beatDenom));
    }

    /**
     * Set the tempo at a given metric position. The tempo is effective until the next
     * timed metrical. The following timed metricals are adjusted so that until the next
     * one the given tempo is set and the following timed metricals are adjusted so that
     * the tempo after the next timed metrical is not changed. <BR>
     * 
     * @param metricTime the position where to start the new tempo
     * @param micros_per_whole the tempo in microseconds per whole note.
     */
    public void setTempo(Rational metricTime, double micros_per_whole) {
        int[] mIndices = findAll(new TimedMetrical(0, metricTime));
        TimedMetrical tm = null;
        long physTime = 0;
        if (mIndices[0] >= 0) {
            // marker(s) at that metrical time
            for (int i = 0; i < mIndices.length; i++) {
                if (get(mIndices[i]) instanceof TimedMetrical) {
                    tm = (TimedMetrical) get(mIndices[i]);
                    physTime = tm.getTime();
                    break;
                }
            }
        } else {
            // no marker at that metrical time
            mIndices[0] = (mIndices[0] * -1) - 1;
        }
        if (tm == null) {
            // no timed metrical found, so make one
            physTime = getTime(metricTime);
//            tm = new TimedMetrical(physTime, metricTime);
            tm = new TempoMarker(physTime, metricTime);	//TODO Änderung in TempoMarker korrekt?
            add(tm);
        }
        // adjust following events
        for (int i = mIndices[0] + 1; i < size(); i++) {
            if (get(i) instanceof TimedMetrical) {
                TimedMetrical tm1 = (TimedMetrical) get(i);
                Rational metricOffset = tm1.getMetricTime().sub(metricTime);
                double physOffset = metricOffset.toDouble() * micros_per_whole;
                tm1.setTime(Math.round(physTime + physOffset));
            }
        }
    }

    /**
     * Calculate the tempo in microseconds per whole note from a tempo in beats per minute
     * and the denominator of the beat length.
     * 
     * @param bpm beats per minute
     * @param beatDenom denominator of the beat length, e.g. 8 for eigth beats or 2 for
     *            alla breve.
     * @return the tempo in mocroseconds per whole note
     */
    public static double bpm2upw(double bpm, int beatDenom) {
        return 60000000 / bpm * beatDenom;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.data.structure.container.SortedContainer#getTime()
     */
    public long getTime() {
        return 0;
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.data.time.Timed#getDuration()
     */
    public long getDuration() {
        for (int i = size() - 2; i >= 0; i++) {
            if (get(i) instanceof Timed) {
                Timed timed = (Timed) get(i);
                return timed.getTime();
            }
        }
        return 0;
    }

    /**
     * Returns first TimeSignature before millis
     * 
     * @param millis
     * @return
     */
    public TimeSignatureMarker getPreviousTimeSignature(long micros) {
        return getTimeSignatureMarker(getMetricTime(micros));
    }

    /**
     * Returns next Measure after the next Rational as Rational. If the next measure and
     * rational are the same, next is returned
     * 
     * @param rational
     * @return
     */
    public Rational getNextMeasure(Rational rational) {
        return rationalToFind(rational, false, true, false);
    }

    /**
     * Returns next Measure after the next Rational as Rational. If the next measure and
     * rational are the same, same is returned
     * 
     * @param rational
     * @return
     */

    public Rational getNextOrSameMeasure(Rational rational) {
        return rationalToFind(rational, false, true, true);
    }

    /**
     * Returns next Measure after the next Rational as Micros. If the next measure and
     * rational are the same, next is returned
     * 
     * @param micros
     * @return
     */
    public long getNextMeasure(long micros) {
        return getTime(getNextMeasure(getMetricTime(micros)));
    }

    /**
     * Returns next Measure after the next Rational as Micros. If the next measure and
     * rational are the same, same is returned
     * 
     * @param micros
     * @return
     */
    public long getNextOrSameMeasure(long micros) {
        return getTime(getNextOrSameMeasure(getMetricTime(micros)));
    }

    /**
     * Return previous beat as Rational
     * 
     * If the previous beate and rational are the same, previous is returned
     * 
     * @param rational
     * @return previous beat as Rational
     */
    public Rational getPreviousBeat(Rational rational) {
        return rationalToFind(rational, true, false, false);
    }

    public long getPreviousBeat(long micros) {
        return getTime(getPreviousBeat(getMetricTime(micros)));
    }

    /**
     * Return previus beat as Rational
     * 
     * If the previous beate and rational are the same, same is returned
     * 
     * @param rational
     * @return previous beat as Rational
     */
    public Rational getPreviousOrSameBeat(Rational rational) {
        return rationalToFind(rational, true, false, true);
    }

    /**
     * Return previuos beat as micros
     * 
     * @param micros
     * @return previuos beat in MicroSec
     */
    public long getPreviousOrSameBeat(long micros) {
        return getTime(getPreviousOrSameBeat(getMetricTime(micros)));
    }

    /**
     * Return previuos measure as Rational
     * 
     * @param rational
     * @return previuos measure as Rational
     */
    public long getPreviousMeasure(long micros) {
        return getTime(getPreviousMeasure(getMetricTime(micros)));
    }

    /**
     * Return previuos measure as Rational
     * 
     * @param rational
     * @return previuos measure as Rational
     */
    public long getPreviousOrSameMeasure(long micros) {
        return getTime(getPreviousOrSameMeasure(getMetricTime(micros)));
    }

    /**
     * Return previuos measure as Rational
     * 
     * @param rational
     * @return previuos measure as Rational
     */
    public Rational getPreviousMeasure(Rational rational) {
        return rationalToFind(rational, false, false, false);
    }

    /**
     * Return previuos measure as Rational
     * 
     * @param rational
     * @return previuos measure as Rational
     */
    public Rational getPreviousOrSameMeasure(Rational rational) {
        return rationalToFind(rational, false, false, true);
    }

    /**
     * Returns whether next beat is begin of a new measure
     * 
     * @param micros
     * @return next beat is begin of new measure
     */
    public boolean isNextBeatMeasure(long micros) {
        if (micros < 0)
            return false;
        else {

            long measure = getNextMeasure(micros);
            long beat = getNextBeat(micros);
            return (getNextOrSameMeasure(micros) == getNextOrSameBeat(micros));
        }
    }

    /**
     * Method used by findNextBeat and findNextMeasure and findpreviousBeat and
     * previousMeasure
     * 
     * @param rational
     * @param searchBeat, flag to switch between searchBar and searchBeat (true: beat,
     *            false: measure)
     * @param next, flag to switch between next and previos search (true: next, false:
     *            previous)
     * @param same, flag to switch between returning same note when (searchNote =
     *            NoteToFind) or find next/previous (true: return sameNote, false:
     *            next/previous note)
     * @return next/previuos Measure/Beat
     */
    private Rational rationalToFind(Rational rational, boolean searchBeat, boolean next,
                                    boolean same) {
        TimeSignatureMarker lastTSM = getTimeSignatureMarker(rational);
        int denominator = lastTSM.getTimeSignature().getDenominator();
        int numerator = lastTSM.getTimeSignature().getNumerator();
        Rational rationalToFind = lastTSM.getMetricTime();
        if (rational.toDouble() < 0) {
            rationalToFind = new Rational(-rational.ceil(), 1); // TODO why this ??
        }
        //	want to find next & return next if (next = same)
        if (next && !same) {
            while (rational.compare(rationalToFind) >= 0) {
                if (searchBeat) {
                    rationalToFind = rationalToFind.add(new Rational(1, denominator));
                } else {
                    rationalToFind = rationalToFind.add(new Rational(numerator,
                            denominator));
                }
            }
            // check for time signature markers after the rational to look for.
            TimeSignatureMarker nextTSM = getTimeSignatureMarker(rationalToFind);
            if (nextTSM.getMetricTime().isGreater(rational)) {
                rationalToFind = nextTSM.getMetricTime();
            }
            return rationalToFind;
        }
        //	want to find next & return same if (next = same)
        if (next && same) {
            while (rational.compare(rationalToFind) > 0) {
                if (searchBeat) {
                    rationalToFind = rationalToFind.add(new Rational(1, denominator));
                } else {
                    rationalToFind = rationalToFind.add(new Rational(numerator,
                            denominator));
                }
            }
            // check for time signature markers after the rational to look for.
            TimeSignatureMarker nextTSM = getTimeSignatureMarker(rationalToFind);
            if (nextTSM.getMetricTime().isGreater(rational)) {
                rationalToFind = nextTSM.getMetricTime();
            }
            return rationalToFind;
        }
        //	want to find previuos & return previuos if (previuos = same)
        if (!next && !same) {
            Rational lastFound = rationalToFind;
            while (rationalToFind.compare(rational) <= 0) {
                lastFound = rationalToFind;
                if (searchBeat) {
                    rationalToFind = rationalToFind.add(new Rational(1, denominator));
                } else {
                    rationalToFind = rationalToFind.add(new Rational(numerator,
                            denominator));
                }
                // we went to fare, lastFound is what we search
                if (rationalToFind.compare(rational) >= 0) {
                    rationalToFind = lastFound;
                    break;
                }
            }
            return rationalToFind;
        }
        // want to find previuos & return same if (previuos = same)
        else {
            Rational lastFound = rationalToFind;
            while (rationalToFind.compare(rational) < 0) {
                lastFound = rationalToFind;
                if (searchBeat) {
                    rationalToFind = rationalToFind.add(new Rational(1, denominator));
                } else {
                    rationalToFind = rationalToFind.add(new Rational(numerator,
                            denominator));
                }
                // we went to fare, lastFound is what we search
                if (rationalToFind.compare(rational) > 0) {
                    rationalToFind = lastFound;
                    break;
                }
            }
            return rationalToFind;
        }

    }

    /**
     * Returns microssec Position of the next beat
     * 
     * @param millis
     * @return
     */
    public long getNextBeat(long micros) {
        Rational rational = getMetricTime(micros);
        return getTime(getNextBeat(rational));
    }

    /**
     * Returns the next beat after the rational as Rational. If the next beat and rational
     * are the same, rational is returned
     * 
     * @param rational
     * @return next beat after rational as Rational
     */
    public Rational getNextOrSameBeat(Rational rational) {
        return rationalToFind(rational, true, true, true);
    }

    /**
     * Returns the next beat after the rational as Rational. If the next beat and rational
     * are the same, rational is returned
     * 
     * @param rational
     * @return next beat after rational as Rational
     */
    public Rational getNextBeat(Rational rational) {
        return rationalToFind(rational, true, true, false);
    }

    /**
     * Returns microssec Position of the next beat
     * 
     * @param millis
     * @return
     */
    public long getNextOrSameBeat(long micros) {
        Rational rational = getMetricTime(micros);
        return getTime(getNextOrSameBeat(rational));
    }

    public long getNextPreCount(long micros) {
        if (micros < 0) {
            long time = 0;
            // Distance from 0 to next Quarter
            long quarDis = getNextBeat(100);
            while (time - quarDis >= micros) {
                time -= quarDis;
            }
            return time;
        } else
            return getNextOrSameBeat(micros);
    }

    public long getPrecountTime(long micros) {
        // Calculate time between next two measures

//        long measureDis = getNextMeasure(getNextMeasure(micros + 100))
//                          - getNextMeasure(micros + 100);
//        // calculate time to previous measure
//        long prevMeas = micros - getPreviousMeasure(micros + 100);
        
        long measureDis = getNextOrSameMeasure(getNextMeasure(micros))
        - getNextOrSameMeasure(micros);
        // 	calculate time to previous measure to have at least
        // measure of precount if time is within measure
        long prevMeas = micros - getPreviousMeasure(micros);

        return measureDis + prevMeas;
    }

    /**
     * Returns the position of a rational from last measure 0 is returned, if rational
     * equals measure 1 is returned, if rational is first beat after measure -1 ist
     * returned, if rational is not a full beat use getNextBeat() to get next beat
     * 
     * @param rational
     * @return
     */
    public int getBeatPosition(Rational rational) {
        Rational ratToFind = getPreviousOrSameMeasure(rational);
        int denominator = getTimeSignatureMarker(rational).getTimeSignature()
                .getDenominator();
        int count = 0;
        while (ratToFind.compare(rational) <= 0) {
            ratToFind = ratToFind.add(1, denominator);
            count++;
        }

        return count - 1;

    }

    /**
     * Returns the position of a rational from last measure 0 is returned, if rational
     * equals measure 1 is returned, if rational is first beat after measure -1 ist
     * returned, if rational is not a full beat use getNextBeat() to get next beat
     * 
     * @param rational
     * @return
     */

    public int getBeatPosition(long micros) {
        return getBeatPosition(getMetricTime(micros));
    }

    /**
     * @param metricTime
     * @return string[0] holds the measure and beats, string[1] holds the remainder
     */
    public String[] toStringFormatted(Rational metricTime) {
        int[] myData = toMeasureBeatRemainder(metricTime);
        String[] outString = new String[2];
        outString[0] = "m. " + (myData[0] - 1) + " b. " + (myData[1] - 1);
        outString[1] = "";
        if (myData[2] != 0) {
            outString[1] = " +[" + myData[2] + "/" + myData[3] + "]";
        }
        return outString;
    }
}