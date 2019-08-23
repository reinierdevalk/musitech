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
 * HorizonatalTimedDisplay
 * Created on 10.04.2003
 */
package de.uos.fmt.musitech.time.gui;

import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Interface for displays that can be horizontally aligned according to the
 * temporal position of their elements (lyrics, scores, piano-roll etc). It can
 * be implemented by displays using linear as well as metrical time.
 * 
 * @author Tillman Weyde, Felix Kugel
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public interface HorizontalTimedDisplay {

    /**
     * Gets the minimal position (in pixels) for the time specified. One of
     * {t,m} may be invalid (i.e. t may be Timed.INVALID_TIME or m may be null)
     * but not both. A display that can only handle physical time needs only
     * look up metrical time, if physical time is invalid and vice versa.
     * 
     * @param t physical time
     * @param m metrical time
     * @return the position in pixels, should be the greater of values required
     *         for either argument
     * @throws WrongArgumentException should be thrown when both arguments are
     *             invalid (t == Timed.INVALID_TIME && m == null ).
     */
    public int getMinimalPositionForTime(long t, Rational m) throws WrongArgumentException;

    /**
     * Set an alignment constraint for the given time point. The given time
     * point defined by <code>t</code> and <code>m</code> must be displayed
     * at least at <code>position</code> (in pixels). One of {t,m} may be
     * Timed.INVALID_TIME or null respectively. The position returned must be
     * the greatest position of all objects with physical or metrical times
     * smaller or equal to arguments t or m.
     * 
     * @param t physical time
     * @param m metrical time
     * @param position in pixels (must be non-negative)
     * @return <code>true</code> if the layout had to be changed, <code>false</code> if there was not
     *         effect on the layout
     * @throws WrongArgumentException should be thrown when both {t,m} are null
     *             or if position is negative
     */
    public boolean setMinimalPositionForTime(long t, Rational m, int position) throws WrongArgumentException;

    /**
     * <code>getNextPositioningTime</code> returns the physical time of the
     * next object to be positioned after <code>startTime</code> (both in
     * microseconds). If an object has no physical time, physical time needs to
     * be determined using the MetricalTimeLine of the current work. If there is
     * no object starting after startTime, Timed.INVALID_TIME must to be
     * returned.
     * 
     * @param startTime The last startTime to be coordinated.
     * @return The time of the next position to coordinate in microseconds.
     */
    public long getNextPositioningTime(long startTime);

    /**
     * The component should layout itself without considering any constraints.
     */
    public void doInitialLayout();

}