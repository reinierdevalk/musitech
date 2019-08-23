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
 * Created on 10.09.2003
 */
package de.uos.fmt.musitech.framework.time;

/**
 * @author Nicolai Strauch
 * Make it possible to begin and stop timer, whith actualisation of the time.
 * 
 */
public class MSyncTimer implements MTimer {

	private MTimer timer = MTimerFactory.getTimer();
	private long timeOffset = 0;

	/**
	 * Return the time running. The time change only and only if
	 * isRunning() get true.
	 * To start the MSyncTimer, call start().
	 * @see de.uos.fmt.musitech.framework.time.MTimer#getTimeMicros()
	 */
	public long getTimeMicros() {
		if(isRunning)
			return timer.getTimeMicros() - timeOffset;
		return rememberTime;
	}
	
	/**
	 * Set the current time. Method for timeactualisation
	 * @param newTime
	 * @return newGivenTime - oldCurrentTime 
	 */
	public long setTimeMicros(long newTime){
		long diff = newTime - getTimeMicros();
		timeOffset -= diff; 
		return diff;
	}
	
	private boolean isRunning = false;
	private long rememberTime = 0;
	
	/**
	 * Set the Time to zero and set isRunning to true.
	 * So the time given is changing (running).
	 *
	 */
	public void start(){
		timeOffset = timer.getTimeMicros();
		isRunning = true;
//System.out.println("MSyncTimer.start() whith time: "+getTimeMicros()+", timeOffset = "+timeOffset);
	}
	/**
	 * Set the Time to atTime and set isRunning of true.
	 * So the time given is changing (running).
	 * Begin to count the time at atTime.
	 * @param atTime
	 */
	public void start(long atTime){
		timeOffset = timer.getTimeMicros() - atTime;
		isRunning = true;
//System.out.println("MSyncTimer.start(...) whith time: "+getTimeMicros()+", timeOffset = "+timeOffset);
	}
	/**
	 * Set the Time to the last stop, and set isRunning of true.
	 * Begin to count the time at the last stop-time. (Or paused time)
	 * @param atTime
	 */
	public void reStart(){
		start(rememberTime);
	}
	/**
	 * Remember the current time, adn set isRunning to false.
	 * Now getTimeMicros return every the some value, without timer.
	 */
	public void stop(){
		rememberTime = getTimeMicros();
		isRunning = false;
//System.out.println("MSyncTimer.stop() at time: "+getTimeMicros()+", timeOffset = "+timeOffset);
	}
	/**
	 * If running, executes stop(), if paused, executes reStart()
	 *
	 */
	public void pause(){
		if(isRunning)
			stop();
		else
			reStart();
	}
	
	/**
	 * @return true if the time is count.
	 */
	public boolean isRunning(){
		return isRunning; 
	}

	/**
	 * Gets a value in microseconds, the Time pas since a marker, 
	 * and decide, if the intern pas time equals the time given, or if the 
	 * intern time must be actualised.
	 * @param tPas
	 * @return actual time at that the marker was setted
	 */
	public long timePasSinceMarker(long tPas){
		deltaSum += tPas - (getTimeMicros()-marker);
		retardCounter++;
		if(retardCounter>=retard){
			setTimeMicros((long) (getTimeMicros()-(deltaSum/retard)));
			retardCounter = 0;
// System.out.println("MSyncTimer.timePasSinceMarker(...) correct Time on: "+(deltaSum/retard));
		}
		marker = getTimeMicros();
		return marker;
	}
	
	private long marker;
	private float deltaSum = 0.0f;
	private int retard = 50;
	private int retardCounter = 0;
	
	/**
	 * set how many MSyncTimer.timePasSinceMarker(long tPas) must be
	 * call to have a good value to aktualise the time
	 * @param i
	 */
	public void setActualisationRetard(int i){
		retard = i;
	}

}



