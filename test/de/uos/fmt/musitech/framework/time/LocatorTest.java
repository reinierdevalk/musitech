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
 * Created on 02.12.2004
 *
 * 
 */
package de.uos.fmt.musitech.framework.time;

import junit.framework.TestCase;

/**
 * @author Jan-H. Kramer
 *
 * 
 */
public class LocatorTest extends TestCase{
    
    Locator loc1, loc2, loc3, loc4;
    PlayTimer playTimer;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
	protected void setUp() throws Exception {
        loc1 = new Locator(0, "eins");
        loc2 = new Locator(100000, "zwei");
        loc3 = new Locator(200000, "drei");
        loc4 = new Locator(200000, "vier");
        playTimer = PlayTimer.getInstance();
        // forces default context
        playTimer.setContext(null);
    }
    
    /**
     * 
     */
    public void testComparator() {
        LocatorComparator locCom = new LocatorComparator();
        assertTrue(locCom.compare(loc1, loc2) == -1);
        assertTrue(locCom.compare(loc2, loc1) == 1);
        assertTrue(locCom.compare(loc2, loc3) == -1);
        assertTrue(locCom.compare(loc3, loc4) == 0);
    }
    
    /**
     * 
     */
    public void testLocator1() {
        playTimer.addLocator(loc1);
        assertFalse("next Locator nicht vorhanden", playTimer.nextLocator(0));
        assertTrue("Position unverändert", playTimer.getPlayTimeMicros() == 0);
        
        playTimer.addLocator(loc2);
        playTimer.addLocator(loc3);
        playTimer.addLocator(loc4);
        assertTrue("Locator loc2 vorhanden", playTimer.nextLocator());
        assertTrue("Position von 2. Locator", playTimer.getPlayTimeMicros() == 100000);
        assertTrue("Locator loc2 vorhanden", playTimer.nextLocator(0));
        assertTrue("Position von 2. Locator", playTimer.getPlayTimeMicros() == 100000);
        
        assertTrue("Locator loc1 vorhanden", playTimer.prevLocator());
        assertTrue("Position von 1. Locator", playTimer.getPlayTimeMicros() == 0);
        
        assertTrue("Locator loc1 vorhanden", playTimer.prevLocator(100000));
        assertTrue("Position von 1. Locator", playTimer.getPlayTimeMicros() == 0);
        
        
        
        assertTrue("Locator loc2 vorhanden", playTimer.nextLocator());
        assertTrue("Position von 2. Locator", playTimer.getPlayTimeMicros() == 100000);
        assertTrue("Locator loc2 vorhanden", playTimer.nextLocator());
        assertTrue("Position von 3. Locator", playTimer.getPlayTimeMicros() == 200000);
        
        assertTrue("Locator loc1 vorhanden", playTimer.prevLocator());
        assertTrue("Position von 2. Locator", playTimer.getPlayTimeMicros() == 100000);
        assertTrue("Locator loc1 vorhanden", playTimer.prevLocator());
        assertTrue("Position von 1. Locator", playTimer.getPlayTimeMicros() == 0);
        
        
    }
    
    /**
     * 
     */
    public void testPrev() {
        playTimer.addLocator(loc2);
        playTimer.addLocator(loc1);
        assertTrue(playTimer.getLocators().size() == 2);
        assertTrue("prev gibt es nicht", playTimer.prevLocator(10));
        
    }
    
    /**
     * 
     */
    public void testLocatorString() {
        playTimer.addLocator(loc1);
        playTimer.addLocator(loc2);
        playTimer.addLocator(loc3);
        playTimer.addLocator(loc4);
        assertTrue("Locator 'eins' vorhanden", playTimer.toLocator("eins"));
        assertTrue("Position von 1. Locator", playTimer.getPlayTimeMicros() == 0);
        assertTrue("Locator 'zwei' vorhanden", playTimer.toLocator("zwei"));
        assertTrue("Position von 2. Locator", playTimer.getPlayTimeMicros() == 100000);
        assertTrue("Locator 'drei' vorhanden", playTimer.toLocator("drei"));
        assertTrue("Position von 3. Locator", playTimer.getPlayTimeMicros() == 200000);
    }

}
