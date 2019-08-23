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
package de.uos.fmt.musitech.time;


public class TimeStamp {
	
	
	private static final long MAX_MILLIS = Long.MAX_VALUE /1000 - 1;
	private static final long MAX_SECONDS = MAX_MILLIS /1000 - 1;
	private static final long MAX_MINUTES = MAX_SECONDS / 60 - 1;
	@SuppressWarnings("unused")
	private static final long MAX_HOURS = MAX_MINUTES / 60 - 1;

	private static final long[] factors = { 3600000000L, 60000000, 1000000, 1000 , 1 };
	@SuppressWarnings("unused")
	private static final long hFac = factors[0];
	private static final long mFac = factors[1];
	private static final long sFac = factors[2];
	private static final long msFac = factors[3];
	@SuppressWarnings("unused")
	private static final long usFac = factors[4];
	private static final String[] units = { "h", "m", "s", "ms", "us" };
	
	private TimeStamp(){
	}


	/**
	 * Get the timeStamp in microseconds.
	 * @param sec the seconds
	 * @param millis the milliseconds
	 * @return The time value in microseconds. 
	 */
	static public long timeStampSecMs(int sec, int millis)  {
		return sec * sFac + millis * msFac;
	}

	/**
	 * Get the timestamp in microseconds for minutes, seconds, millis.
	 * @param min
	 * @param sec
	 * @param millis
	 * @return The time value in microseconds.
	 */
	static public long timeStampMinSecMs(int min, int sec, int millis) {
		return  min * mFac + sec * sFac + millis * msFac;
	}

	/**
	 * Get the time stamp in microseconds for milliseconds.
	 * @param millis long
	 * @return The time value in microseconds.
	 */
	static public long timeStampMillis(long millis)  {
		return millis * msFac;
	}


	/**
	 * Returns a String representation of this TimeStamp.
	 * The text starts with "negative" if the time is negative.
	 * @param micros The time to be represented.
	 * @return String The textual representation.
	 */
	public static String toStringFormated(long micros) {
		StringBuffer sb = new StringBuffer();
		if(micros < 0)
			sb = new StringBuffer("negative");
		for (int i = 0; micros > 0 && i < units.length; i++) {
			if (micros > factors[i]) {
				if (sb.length() > 0)
					sb.append(" ");
				sb.append( micros / factors[i]);
				sb.append(units[i]);
				micros %= factors[i];
			}
		}
		return sb.toString();
	}

	/**
	 * Method toStringMicros returns a String with the time in 
	 * milliseconds and the text " ms" trailing.
	 * @param millis The time.
	 * @return String The string.
	 */
	public static String toStringMicros(long millis) {
		return millis + " ms";
	}

}