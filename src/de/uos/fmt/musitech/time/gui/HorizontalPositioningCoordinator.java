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
 * File HorizontalPositioningCoordinator.java
 * Created on 15.05.2003
 */
package de.uos.fmt.musitech.time.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;

/**
 * This class coordinates horizontal positioning of
 * <code>HorizontalTimedDisplay</code>s. It coordinates the display of notes,
 * piano roll, lyrics etc. by pushing the time-point-positions to the right to
 * give enough room for every item that needs to be displayed.
 * 
 * @author Tillman Weyde
 */
public class HorizontalPositioningCoordinator {

    private List<HorizontalTimedDisplay> displays = new ArrayList<HorizontalTimedDisplay>();
    //	private double minScale=0.0; //TODO prüfen
    private double minScale = 1.0;
    private double scale = 0.0;

    /**
     * Adds the specified HorizontalTimedDisplay to the List 
     * <code>displays</code> of displays to be positioned.
     * 
     * @param display HorizontalTimedDisplay to be registered at the
     *            HorizontalPositioningCoordinator
     */
    public void registerDisplay(HorizontalTimedDisplay display) {
        displays.add(display);
    }
    
    /**
     * Removes the specified HorizontalTimedDisplay from the registered <code>displays</code>.
     * 
     * @param display HorizontalTimedDisplay to be removed from the registered displays
     */
    public void unregisterDisplay(HorizontalTimedDisplay display){
        displays.remove(display);
    }
    
    /**
     * Removes all currently registered displays.
     */
    public void clearDisplays(){
        displays.clear();
    }
    
    /**
     * Getter for <code>displays</code>. Returns a Collection containing all registered
     * HorizontalTimedDisplays.
     * 
     * @return Collection containing all registered HorizontalTimedDisplays
     */
    public Collection<HorizontalTimedDisplay> getDisplays(){
        return displays;
    }

    static int MAX_PASSES = 3;

    /**
     * This method starts a coordinated layout process for the registered
     * displays.
     */
    public void doPositioning() {
        // do the initial layout for all displays
        for (Iterator<HorizontalTimedDisplay> iter = displays.iterator(); iter.hasNext();) {
            HorizontalTimedDisplay display = iter.next();
            display.doInitialLayout();
        }
        // execute the necessary number of positioning passes
        try {

            for (int i = 0; i < MAX_PASSES; i++) {
                System.out.println("Pass: " + i);
                if (!positioningPass())
                    break; //break if on previous pass all displays have
                           // remained unchanged
            } // stop when max. num of passes reached

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs the positioning of the elements once. The doPositioning()
     * algorithm calls multiple passes.
     * 
     * Returns true, if at least one of the registered displays has been changed
     * in this pass. Therefore, if false is returned, all displays have been
     * remained unchanged and the positioning is finished.
     * 
     * @throws WrongArgumentException
     */
    private boolean positioningPass() throws WrongArgumentException {
        boolean changed = false;
        //		long currentTime = 0;
        long currentTime = -1;
        int nextPosition = -1; 	//TODO ggf. wieder löschen
        do {
            currentTime = getNextPositioningTime(currentTime);
            if (currentTime == Timed.INVALID_TIME)
                break;
//            int nextPosition = getGreatestPosition(currentTime);
            //for testing;
            nextPosition = getGreatestPosition(currentTime, nextPosition);
            System.out.println(getClass() + " nextPosition: " + nextPosition + "  currentTime: " + currentTime);
            for (Iterator<HorizontalTimedDisplay> it = displays.iterator(); it.hasNext();) {
                HorizontalTimedDisplay display = it.next();
                if (display.setMinimalPositionForTime(currentTime, null, nextPosition))
                    changed = true;
            }
        } while (true);
        return changed;
    }

    /**
     * Get the greatest position of all displays for the current time.
     * 
     * @param currentTime
     * @return the position
     * @throws WrongArgumentException
     */
    private int getGreatestPosition(long currentTime) throws WrongArgumentException {

        //		int nextPosition = (int)(currentTime/minScale);
        int nextPosition = 0;
        for (Iterator<HorizontalTimedDisplay> iter = displays.iterator(); iter.hasNext();) {
            HorizontalTimedDisplay display = iter.next();
            //for debugging:
            //int minPos = display.getMinimalPositionForTime(currentTime, null);
            nextPosition = Math.max(display.getMinimalPositionForTime(currentTime, null), nextPosition);
        }
        if (nextPosition != 0)
            scale = (double) currentTime / nextPosition;
        return nextPosition;
    }

    private int getGreatestPosition(long currentTime, int currentPosition) throws WrongArgumentException {
        int nextPosition = currentPosition;
        for (Iterator<HorizontalTimedDisplay> iter = displays.iterator(); iter.hasNext();) {
            HorizontalTimedDisplay display = iter.next();
            //for debugging:
            //int minPos = display.getMinimalPositionForTime(currentTime, null);
            nextPosition = Math.max(display.getMinimalPositionForTime(currentTime, null), nextPosition);
        }
        if (nextPosition != 0)
            scale = (double) currentTime / nextPosition;
        return nextPosition;
    }

    /**
     * Gets the next time (after nowTime) over all dipslays at which there is an
     * object to be aligned.
     * 
     * @param nowTime
     * @return
     */
    private long getNextPositioningTime(long nowTime) {
        long nextPosTime = Long.MAX_VALUE;
        Iterator<HorizontalTimedDisplay> iter = displays.iterator();
        while (iter.hasNext()) {
            HorizontalTimedDisplay display = iter.next();
            long displaysTime = display.getNextPositioningTime(nowTime);
            nextPosTime = Math.min(displaysTime, nextPosTime);
        }
        if (nextPosTime == Long.MAX_VALUE)
            return Timed.INVALID_TIME;
        if (nextPosTime != Timed.INVALID_TIME && nextPosTime <= nowTime) {
            nextPosTime += 1000;
        }
        return nextPosTime;
    }

    /**
     * @return
     */
    public double getMinScale() {
        return minScale;
    }

    /**
     * @param d
     */
    public void setMinScale(double d) {
        minScale = d;
        doPositioning();
    }

    /**
     * @return
     */
    public double getScale() {
        return scale;
    }

}