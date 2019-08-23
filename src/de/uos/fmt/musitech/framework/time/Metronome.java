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
 * Created on 20.08.2004
 */
package de.uos.fmt.musitech.framework.time;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.utility.RingBuffer;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * 
 * 
 * @author Jan
 *
 */
public class Metronome implements Player {

	Receiver receiver;
	private boolean preCount = false;
	boolean playMetronom = false;
	Context context;
	PlayTimer playTimer;
	MidiPlayer midiPlayer = MidiPlayer.getInstance();

	int mainVelo = 127;
	int mediumVelo = 40;

	public Metronome(Context context) {
//		this.context = context;
		if (context == null){
			Piece piece = new Piece();
			piece.setMetricalTimeLine(new MetricalTimeLine());
			context = new Context(piece);
//			setContext(context);
		}
		setContext(context);
		init();
	}

	/** 
	 * @see javax.sound.midi.Transmitter#close()
	 */
	public void close() {
	}

	public void init() {
		
	}

	private MidiEvent getEvent(
		int time,
		int pitch,
		int velocity,
		int channel) {
		ShortMessage onMessage = new ShortMessage();
		try {
			onMessage.setMessage(
				ShortMessage.NOTE_ON,
				channel,
				pitch,
				velocity);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return new MidiEvent(onMessage, time);
	}

	private MidiEvent getEvent(int time, int pitch, int velocity) {
		return getEvent(time, pitch, velocity, 9);
	}

	private MidiEvent getEvent(int time, int pitch) {
		return getEvent(time, pitch, 100, 9);
	}

	private MidiEvent getEvent(int time) {
		return getEvent(time, 76, 100, 9);
	}
	/**
	 * @return
	 */
	public boolean isPreCount() {
		return preCount;
	}

	/**
	 * @param b
	 */
	public void setPreCount(boolean b) {
		preCount = b;
	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#start()
	 */
	public void start() {

	}

	long timeToFill = 0; // used for filling buffer
	Rational rationalToFill;
	int nextBeat = 1;
	
	long nextBeatMicros = 0;

	/**
	 * 
	 */
	private void fillBuffer() {
		RingBuffer buffer = midiPlayer.getMetronomBuffer();
		// TODO: Tempo Changes

		int velocity = 100;
		MetricalTimeLine timeLine = getContext().getPiece().getMetricalTimeLine();
		int didWrite = 1;
		
		for (int i = 0; i < buffer.getBuffSize(); i++) {
			if (timeLine.isNextBeatMeasure(nextBeatMicros)) {
				velocity = mainVelo;
			} else {
				velocity = mediumVelo;
			}

			didWrite =
				buffer.write(
					getEvent((int) (nextBeatMicros), 76, velocity));
			if (didWrite != 0) {
//				rationalToFill = timeLine.fromMeasureBeatRemainder(new int[] {0,nextBeat,0,0});
				nextBeatMicros = timeLine.getNextPreCount(nextBeatMicros+100);
				
			} else
				break;
		
		}
	}

	/**
	 * This inner class is used to Fill ringbuffer while Playing
	 * to avoid using setTimeMicros twice
	 * @author Jan
	 *
	 */
	public class MetronomTimeable implements Timeable {

		/** 
		 * Is used to fill ring buffer while playing
		 * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
		 */
		public void setTimePosition(long timeMicros) {
			if (playMetronom)
				fillBuffer();
			
		}

		/** 
		 * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
		 */
		public long getEndTime() {
			return 0;
		}

	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#stop()
	 */
	public void stop() {
	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#reset()
	 */
	public void reset() {
	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#setTimePosition(long)
	 */
	public void setTimePosition(long time) {
		if (playMetronom) {

			// Fill Ringbuffer.  
			midiPlayer.getMetronomBuffer().clearBuffer();
			System.out.println("Metronom setTime not playing");
			timeToFill = playTimer.getPlayTimeMicros();
			// TODO Calculate next beat
			
			MetricalTimeLine timeline =
				context.getPiece().getMetricalTimeLine();
			
			nextBeatMicros = timeline.getNextPreCount(time);			
//			Rational nextRational = timeline.getNextBeat(timeline.getMetricTime(time));
			
//			rationalToFill = timeline.getNextBeat(new Rational(11,8));
//			rationalToFill = new Rational(nextBeat, 4);
//			timeline.getMetricTime(time);
//			while (timeToFill < time) {
//
//				timeToFill = timeline.getTime(rationalToFill);
//				rationalToFill = new Rational(nextBeat, 4);
//				nextBeat++;
//			}

			fillBuffer();
		}
	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#setPlayTimer(de.uos.fmt.musitech.framework.time.PlayTimer)
	 */
	public void setPlayTimer(PlayTimer timer) {
		this.playTimer = timer;
		MetronomTimeable timeable = new MetronomTimeable();
		playTimer.registerForPush(timeable);

	}

	/**
	 * @return
	 */
	public Context getContext() {
	    if(context == null)
	        context = Context.getDefaultContext();
		return context;
	}

	/**
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#getEndTime()
	 */
	public long getEndTime() {
		return 0;
	}

	/**
	 * @return
	 */
	public boolean isPlayMetronom() {

		return playMetronom;
	}

	/**
	 * @param b
	 */
	public void setPlayMetronom(boolean metronom) {
		playMetronom = metronom;
		if (!metronom)
			midiPlayer.getMetronomBuffer().clearBuffer();
//		else
//			setTimePosition(playTimer.getPlayTimeMicros());

	}

}
