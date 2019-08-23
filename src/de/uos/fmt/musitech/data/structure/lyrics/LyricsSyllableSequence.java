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
 * Created on 22.06.2004
 *
 */
package de.uos.fmt.musitech.data.structure.lyrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.time.TimedContainer;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * A LyricsSyllableSequence is a TimedContainer for taking LyricsSyllable
 * objects.
 * 
 * @author Kerstin Neubarth
 * 
 * @hibernate.class table="LyricsSyllableSequence"
 * 
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 *  
 */
public class LyricsSyllableSequence extends TimedContainer<LyricsSyllable> {

    private Locale locale;
    private byte verseNumber;

    /**
     * Constructor. Creates a TimedContainer with type LyricsSyllable.
     *  
     */
    public LyricsSyllableSequence() {
    }

    /**
     * Constructor. Sets the specified Context as the Context of this
     * LyricsSyllableSequence.
     * 
     * @param context
     *            Context to be set as the Context of the LyricsSyllabelSequence
     */
    public LyricsSyllableSequence(Context context) {
        this();
        setContext(context);
    }
    
    public LyricsSyllableSequence(Locale locale, byte verseNum) {
        this();
        setLanguage(locale);
        setVerseNumber(verseNum);
    }
    public LyricsSyllableSequence(byte verseNum) {
        this();
        setVerseNumber(verseNum);
    }
    public LyricsSyllableSequence(Locale locale) {
        this();
        setLanguage(locale);
    }

    /**
     * Overwrites method <code>add(Object)</code> of SortedUniquesCollection
     * in order to set the time of the added element according to the Context of
     * this LyricsSyllableSequence. If the element does not have a valid
     * physical time, its <code>time</code> is converted from the metrical
     * time via the MetricalTimeLine of the Context. If the element does not
     * have a metric time, its <code>metricTime</code> is converted from the
     * physical time.
     * 
     * @see de.uos.fmt.musitech.utility.collection.TypedCollection#add(java.lang.Object)
     */
    public boolean add(LyricsSyllable obj) {
        if (context != null) {
            if (( obj).getTime() == Timed.INVALID_TIME
                && ( obj).getMetricTime() != null) {
                ( obj).setTime(getTimeFromMetricalTimeLine(( obj).getMetricTime()));
            }
            if (( obj).getMetricTime() == null
                && ( obj).getTime() != Timed.INVALID_TIME) {
                ( obj)
                        .setMetricTime(getMetricTimeFromMetricalTimeLine(( obj).getTime()));
            }
        }
        return super.add(obj);
    }

    /**
     * Returns a long value representing the physical time position which
     * corresponds to the specified metric time position. The time is got from
     * the MetricalTimeLine of the Conetxt of this LyricsSyllableSequence. If
     * the Context, its Piece or the MetricalTimeLine of the Piece is null, a
     * new instance of MetricalTimeLine is created for converting metric time to
     * physical time. If no positive time value could be determined,
     * Timed.INVALID_TIME is returned.
     * 
     * @param metricalTime
     *            Rational giving the metrical time which is to convert to
     *            physical time
     * @return long giving the physical time derived from the specified metrical
     *         time, or Timed.INVALID_TIME
     */
    private long getTimeFromMetricalTimeLine(Rational metricalTime) {
        MetricalTimeLine mtl = null;
        if (getContext() != null && getContext().getPiece() != null)
            mtl = getContext().getPiece().getMetricalTimeLine();
        long time = Timed.INVALID_TIME;
        if (mtl == null) {
            mtl = new MetricalTimeLine();
            mtl.add(get(size() - 1)); //TODO ?berpr?fen, welche Zeitangaben
            // dieses ChordSymbol besitzt?
        }
        time = mtl.getTime(metricalTime);
        if (time >= 0)
            return time;
        else
            return Timed.INVALID_TIME;
    }

    /**
     * Returns a Rational representing the metrical time which corresponds to
     * the specified physical time. The time is got from the MetricalTimeLine of
     * the Conetxt of this LyricsSyllableSequence. If the Context, its Piece or
     * the MetricalTimeLine of the Piece is null, a new instance of
     * MetricalTimeLine is created for converting physical time to metric time.
     * If no positive metric time value could be determined, null is returned.
     * 
     * @param time
     *            long value giving the physical time position which is to be
     *            converted to metrical time
     * @return Rational being the metrical time corresponding to the specified
     *         physical time, or null
     */
    private Rational getMetricTimeFromMetricalTimeLine(long time) {
        MetricalTimeLine mtl = null;
        if (getContext() != null && getContext().getPiece() != null)
            mtl = getContext().getPiece().getMetricalTimeLine();
        if (mtl == null) {
            mtl = new MetricalTimeLine();
            mtl.add(get(size() - 1)); //TODO ?berpr?fen, welche Zeitangaben
            // dieses ChordSymbol besitzt?
        }
        Rational metricTime = mtl.getMetricTime(time);
        if (metricTime.isGreater(Rational.ZERO))
            return metricTime;
        return null;
    }

    public Locale getLanguage() {
        return locale;
    }

    public void setLanguage(Locale language) {
        this.locale = language;
    }

    public byte getVerseNumber() {
        return verseNumber;
    }

    public void setVerseNumber(byte verseNumber) {
        this.verseNumber = verseNumber;
    }

    /**
     * @param language2
     * @return
     */
    public boolean isLanguage(Locale language) {
        if(this.locale == null){
            return (language == null);
        }
        return this.locale.equals(language);
    }

    /**
     * @param metricTime
     * @return
     */
    public LyricsSyllable getSyllableAt(Rational metricTime) {
        for (Iterator<LyricsSyllable> iter = iterator(); iter.hasNext();) {
            LyricsSyllable syl = iter.next();
            if (syl.getMetricTime().equals(metricTime)) {
                return syl;
            }
        }
        return null;
    }

    public Collection<LyricsSyllableSequence> splitAtLinebreaks(Collection linebreaks) {
        ArrayList<LyricsSyllableSequence> sequences = new ArrayList<LyricsSyllableSequence>();

        ArrayList sortedLinebreaks = new ArrayList(linebreaks);
        Collections.sort(sortedLinebreaks);

        for (int i = 0; i < sortedLinebreaks.size() + 1; i++) {
            sequences.add(new LyricsSyllableSequence(context));
        }

        for (Iterator iter = iterator(); iter.hasNext();) {
            LyricsSyllable syllable = (LyricsSyllable) iter.next();
            int index = Collections.binarySearch(sortedLinebreaks, syllable, new RationalSyllableComparator());

            if (index >= 0) {
                index++;
            } else {
                index = -(index + 1);
            }
            sequences.get(index).add(syllable);
        }

        return sequences;
    }

    class RationalSyllableComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Rational r;
            LyricsSyllable l;
            if (o1 instanceof Rational) {
                r = (Rational) o1;
                l = (LyricsSyllable) o2;
            } else {
                r = (Rational) o2;
                l = (LyricsSyllable) o1;
            }
            return r.compareTo(l.getMetricTime());
        }
    }

    /**
     * @return
     */
    public boolean hasVerseNumber() {
        return (this.verseNumber >= 0);
    }

    String verseNumberingText;
    public String getVerseNumberingText() {
        return verseNumberingText;
    }
    public void setVerseNumberingText(String verseNumberingText) {
        this.verseNumberingText = verseNumberingText;
    }
}