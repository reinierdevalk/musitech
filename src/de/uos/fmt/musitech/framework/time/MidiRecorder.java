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
 * Created on 03.11.2003
 */
package de.uos.fmt.musitech.framework.time;

import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

//import sun.awt.DebugSettings;

//import com.sun.net.ssl.internal.ssl.Debug;

import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.performance.midi.PhysTimeMidiReader;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * @author Jan
 *
 */
public class MidiRecorder implements Recorder, Receiver {

	private boolean record;
	Sequence sequence = null;
	Track track = null;
	Piece piece;
	PlayTimer playTimer = null;
	ArrayList containers = new ArrayList();
	Container cont = new BasicContainer();

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#start()
	 */
	@Override
	public void start() {

		try {
			sequence = new Sequence(Sequence.PPQ, 384);
		} catch (InvalidMidiDataException e) {

			e.printStackTrace();
		}
		track = sequence.createTrack();

	}

	//	public void getTempoChanges(){
	//		MetricalTimeLine timeLine = null;
	//		double[][] tempo = timeLine.getTempo();
	//		for (int i = 0; i < tempo.length; i++) {
	//			tempo[i][0];
	//		}
	//	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#stop()
	 */
	@Override
	public void stop() {
		PhysTimeMidiReader reader = new PhysTimeMidiReader();
		if (sequence != null && record)
			piece = reader.getPiece(sequence);
			if (piece != null && piece.getContainerPool() != null){
			
				Container cont1 = piece.getContainerPool();
				cont = piece.getContainerPool();
				containers.add(cont1);
			}
			
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#reset()
	 */
	@Override
	public void reset() {
		// Auto-generated method stub
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#setTimePosition(long)
	 */
	@Override
	public void setTimePosition(long time) {

	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#setPlayTimer(de.uos.fmt.musitech.framework.time.PlayTimer)
	 */
	@Override
	public void setPlayTimer(PlayTimer timer) {
		playTimer = timer;
	}

	/**
	 * @see javax.sound.midi.Receiver#close()
	 */
	@Override
	public void close() {
		// Auto-generated method stub
	}

	/**
	 * @see javax.sound.midi.Receiver#send(javax.sound.midi.MidiMessage, long)
	 */
	@Override
	public void send(MidiMessage message, long timeStamp) {
				if (record) {
					MidiEvent event =
						new MidiEvent(message, playTimer.getPlayTimeMicros());
					// TODO: Zeit wird in MidiReader falsch berechnet, da er mit ticks rechnet
					// und nicht mit millis
					track.add(event);
				}


	}

	/**
	 * @param b
	 */
	@Override
	public void setRecord(boolean b) {
		record = b;
	}

	/**
	 * @return
	 */
	public Piece getWork() {
		return piece;
	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#getEndTime()
	 */
	@Override
	public long getEndTime() {
		return 0;
	}
	
	public Container getLastRecorded(){
//		Container cont = (Container) containers.get(containers.size());
		if (DebugState.DEBUG) System.out.println("getLastRecorded");
		return cont;
	}
	
	public Container getAllRecorded(){
		Container cont = new BasicContainer();
		for (Iterator iter = containers.iterator(); iter.hasNext();) {
			Container element = (Container) iter.next();
			cont.add(element);
		}
		return cont;
	}

}
