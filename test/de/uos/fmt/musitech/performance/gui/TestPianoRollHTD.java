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
 * Created on 06.12.2004
 */
package de.uos.fmt.musitech.performance.gui;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.performance.gui.PianoRollContainerDisplay;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;

/**
 * @author Jan
 *
 */
public class TestPianoRollHTD extends TestCase{
    
    Container notes;
    MidiNoteSequence MNS;
    PianoRollContainerDisplay pianoRollCD;
    
    /** 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        MNS = new MidiNoteSequence();
        MidiNote note1 = new MidiNote(0, 100);
        MidiNote note2 = new MidiNote(200, 100);
        MidiNote note3 = new MidiNote(500, 100);
        
        MNS.add(note1);
        MNS.add(note2);
        MNS.add(note3);
        
        pianoRollCD = new PianoRollContainerDisplay(MNS);
        pianoRollCD.setMicrosPerPix(10);
    }
    
    /**
     * 
     */
    public void testInit() {
        assertTrue("erste Note: 0", MNS.getTime() == 0);
        assertTrue("letzte Note: 500 + 100", MNS.getDuration() == 600);
        assertTrue("Container kommt richtig an", pianoRollCD.getNoteSequence().getTime() == 0);
        assertTrue("Container kommt richtig an", pianoRollCD.getEndTime() == 600);
    }
    
    /**
     * 
     */
    public void testgetMinimalPositionForTime() {
        try {
            assertTrue("time 0: pix 0", pianoRollCD.getMinimalPositionForTime(0, null) == 0);
        } catch (WrongArgumentException e) {
            e.printStackTrace();
        }
        try {
            assertTrue("time 1000: pix 100", pianoRollCD.getMinimalPositionForTime(1000, null) == 100);
        } catch (WrongArgumentException e) {
            e.printStackTrace();
        }
        try {
            assertTrue("time -1000: pix -100", pianoRollCD.getMinimalPositionForTime(-1000, null) == -100);
        } catch (WrongArgumentException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     */
    public void testgetMinPosForTimeOffset() {
        try {
            assertTrue("offset wird verschoben", pianoRollCD.setMinimalPositionForTime(0, null, 100));
            assertTrue("time 0: pix 0", pianoRollCD.getMinimalPositionForTime(0, null) == 100);
            assertTrue("offset wird verschoben", pianoRollCD.setMinimalPositionForTime(100, null, 200));
        } catch (WrongArgumentException e1) {
            e1.printStackTrace();
        }
        
    }
    
    /**
     * 
     */
    public void testSetMinimalPostionForTime() {
        try {
            pianoRollCD.setMinimalPositionForTime(100, null, 1000);
        } catch (WrongArgumentException e) {
            e.printStackTrace();
        }
        assertTrue("MPP wurde angepasst: 0.1", pianoRollCD.getMicrosPPix()== 0.1);
        try {
            pianoRollCD.setMinimalPositionForTime(100, null, 500);
        } catch (WrongArgumentException e) {
            e.printStackTrace();
        }
        assertTrue("MPP unverändert: 0.1", pianoRollCD.getMicrosPPix()== 0.1);
    }
    
    /**
     * 
     */
    public void testNextPosition() {
        assertTrue(pianoRollCD.getNextPositioningTime(0) == 200);
        assertTrue(pianoRollCD.getNextPositioningTime(50) == 200);
        assertTrue(pianoRollCD.getNextPositioningTime(200) == 500);
        assertTrue(pianoRollCD.getNextPositioningTime(500) == Timed.INVALID_TIME);
    }
    
    /**
     * 
     */
    public void testOffset() {
        try {
            pianoRollCD.setMicrosPerPix(50);
            assertTrue(pianoRollCD.setMinimalPositionForTime(0, null, 2));
            assertTrue("Offset ist verändert worden", pianoRollCD.getMinimalPositionForTime(0, null) == 2);
            assertTrue("Offset ist verändert worden", pianoRollCD.offset_us == -100);
            assertTrue(pianoRollCD.setMinimalPositionForTime(200, null, 10));
            assertTrue("mpp: 25", pianoRollCD.getMicrosPPix() == 25);
//            assertTrue("Offset ist verändert worden", pianoRollCD.getMinimalPositionForTime(200, null) == 400);
        } catch (WrongArgumentException e) {
            e.printStackTrace();
        }
        
        
    }

}
