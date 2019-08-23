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
 * Created on 23.04.2004
 */
package de.uos.fmt.musitech.audio.floatStream;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import de.uos.fmt.musitech.audio.TimedFloatDataLine;
import de.uos.fmt.musitech.framework.time.MSyncTimer;
import de.uos.fmt.musitech.framework.time.MTimer;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.framework.time.Player;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * Play a FloatInputStream on an audio device.
 * 
 * @author Nicolai Strauch and Tillman Weyde
 */
public class FISPlayer implements Player {

	TimedFloatDataLine dataLine;

	private PlayTimer playTime = PlayTimer.getInstance();

	private FloatInputStream fis;

	boolean playing = false;

	boolean stop = true;

	/**
	 * Default constructor, FloatInputStream must be set before use.
	 *  
	 */
	public FISPlayer() {
		// inititalize the play thread
		playThread = new PlayThread();
		// it should not keep the JVM from exiting
		playThread.setDaemon(true);
		// high priority for the play thread avoid drop-outs
		playThread.setPriority(Thread.MAX_PRIORITY);
		//playThread.setPriority(3);
		// star the thread
		playThread.start();

	}

	/**
	 * Constructs a player for the given stream. The float input stream must be
	 * set before the Player is started.
	 * 
	 * @param fis The stream to play back.
	 */
	public FISPlayer(FloatInputStream fis) {
		this();
		setFloatInputStream(fis);
	}

	/**
	 * Sets the FloatInputStream to play.
	 * 
	 * @param fis The stream to play back.
	 */
	public void setFloatInputStream(FloatInputStream fis) {
		if (fis == null)
			return;
		this.fis = fis;
		sampleBuffer = new float[fis.getFormat().getChannels()][sampleBufferLength];
		AudioFormat af = fis.getFormat();
		dataLine = new TimedFloatDataLine(new MSyncTimer(), fis.getFormat());
		// TODO was macht das genau, wann muss man das tun
		// und sollte das evtl. ins API von TimedFloatDataLine ?
		((MSyncTimer) (dataLine.getTimer())).start();
		// initialises SourceDataLine
	}

	/**
	 * 
	 * @return the current state of the Player
	 */
	public boolean isPlaying() {
		return playing && !stop;
	}

	private final int sampleBufferLength = 1024;

	//float[][] sampleBuffer = new float[2][sampleBufferLength];
	float[][] sampleBuffer;

	int framesToWrite = sampleBufferLength;

	// to controll the amoungth of data read before conect a new FIS

	private PlayThread playThread;

	/**
	 * This thread feeds the data for the floatInputStream into the dataLine.
	 */
	private class PlayThread extends Thread {

		/**
		 * Call {@link FISPlayer#transportSamples()}if not stopped. The
		 * starting and stopping is realized through wait and notify on the
		 * playThread.
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {
				//				while (samplesRead >= 0) {
				while (true) { // TODO we have to end this while?
					// if it end if (samplesRead >= 0),
					// we cannot restart the play.

					// If the player is stopped, we wait here
					// to be notified
					if (stop) {
						synchronized (this) {
							try {
								if (DebugState.DEBUG_AUDIO) {
									System.out.println("FloatISPLayer: playThread stopped.");
								}
								this.wait();
							} catch (InterruptedException e1) {
								if (DebugState.DEBUG_AUDIO) {
									System.out.println("FloatISPLayer: playThread interupted");
								}
							}
							if (DebugState.DEBUG_AUDIO) {
								System.out.println("FloatISPLayer: playThread returned from wait, starting to play.");
							}
						}
					}

					// PLAYPART
					//if (!stop &&!AFOPlayButtons.stopppp) {
					if (!stop) {
						transportSamples();
					}
					// END PLAYPART

				} // END WHILE
			} catch (IOException e) {
				e.printStackTrace();
			}
			dataLine.drain();
			if (stop) {
				playing = false;
			}
		}
	}

	private int samplesWritten; // samples written into the dataLine

	private int samplesRead; // samples read from fis and not yet written into

	// the dataLine

	/**
	 * Writes samples into the dataLine.
	 * 
	 * @throws IOException if an IOError occurs
	 */
	// TODO check if method should be synchronized
	private void transportSamples() throws IOException {
		if (samplesRead == 0) {
			samplesRead = fis.read(sampleBuffer, 0, framesToWrite);
			samplesWritten = 0;
		}
		if (samplesRead == -1) {
			dataLine.drain();
			FISPlayer.this.stop();
			fis.reset(); // TODO: is that ok?
		} else {
			samplesWritten = dataLine.write(sampleBuffer, samplesWritten, samplesRead);
			//	AudioUtil.writeIntoSourceDataLine(sampleBuffer, samplesWritten, samplesRead);
			samplesRead -= samplesWritten;
			if (samplesRead != 0) { // if we could not write all samples to
				// the dataline, therefore we wait a moment.
				try {
					// System.out.println("samplesRead = "+samplesRead+",
					// sampleRate = "+fis.getFormat().getFrameRate()+", to
					// spleep =
					// "+(samplesRead*1000/fis.getFormat().getFrameRate()));
					// wait half the time it takes to play the samples written
					// last time.
					// Thread.sleep((long) (samplesWritten * 500 /
					// fis.getFormat().getSampleRate()));
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Play back the current FIS.
	 */
	public synchronized void play() {
		if (playing)
			return;
		//dataLine.flush();
		playing = true;
		stop = false;
		try {
			transportSamples();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//		dataLine.start();
		synchronized (playThread) {
			playThread.notifyAll();
		}
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#stop()
	 */
	public synchronized void stop() {
		stop = true;
		dataLine.stop();
		playing = false;
		//	((MSyncTimer)(dataLine.getTimer())).stop();
		// TODO: auch Timer stoppen?
	}

	/**
	 * Convenience method for implementing an 'pause' button, i.e. on that
	 * toggles between silence and playback.
	 */
	public synchronized void pause() {
		//	((MSyncTimer)(dataLine.getTimer())).pause();
		if (!playing)
			play();
		else
			stop();
	}

	/**
	 * Get the timer used by this player.
	 * 
	 * @return the timer used, a MSyncTimer
	 */
	public MTimer getTimer() {
		return null;//dataLine.getTimer();
	}

	/**
	 * Get the number of samples played back by this player <BR>
	 * TODO: implemented as the number of samples read form the
	 * FloatInputStream. May give wrong results when the player is initialised
	 * with a stream at non-zero position.
	 * 
	 * @return The number of samples played yet.
	 * @throws IOException
	 */
	public long getSamplesPlayed() throws IOException {
		return fis.getPositionInSamples();
	}

	/**
	 * Get the current playback position in microseconds.
	 * 
	 * @return The current playback position in microseconds. 
	 * @throws IOException
	 */
	public long getTimePosition() throws IOException {
		return (long) (getSamplesPlayed() * 1000000 / (double)fis.getFormat().getFrameRate());
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#start()
	 */
	public void start() {
		play();
	}

	/**
	 * Set the playback position in samples.
	 * @param sample The sample position to move to.
	 */
	public void setToSample(long sample) {
		try {
			if (DebugState.DEBUG_AUDIO)
				System.out.println(">>>>>>>>>>Setting sample in player to: " + sample);
			boolean wasPause = !playing;
			if (!wasPause)
				stop();
			dataLine.flush();
			fis.setPositionInSamples(sample);
			if (!wasPause)
				start();
			if (DebugState.DEBUG_AUDIO)
				System.out.println("<<<<<<<<<<Sample set.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets the time position
	 * 
	 * @param time - microseconds
	 * @see de.uos.fmt.musitech.framework.time.Player#setTimePosition(long)
	 */
	public void setTimePosition(long time) {
		long sample = (long) (time * fis.getFormat().getFrameRate() / 1000000);
		setToSample(sample);
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#setPlayTimer(de.uos.fmt.musitech.framework.time.PlayTimer)
	 */
	public void setPlayTimer(PlayTimer timer) {
		// TODO check if other actions are necessary.
		playTime = timer;
	}

	/**
	 * Get the end time of the stream.
	 * 
	 * @return Total playable time from begin to end in microsekonds
	 * @see de.uos.fmt.musitech.framework.time.Player#getEndTime()
	 */
	public long getEndTime() {
		long out = (long) ((fis.getPositionInSamples() + fis.remainingSamples()) / fis.getFormat().getSampleRate() * 1000000);
		if(DebugState.DEBUG_AUDIO)
			System.out.println("FISPlayer.getEndTime() returns " + out);
		return out;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Player#reset()
	 */
	public synchronized void reset() {
		stop();
		while (playing) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		try {
			if(DebugState.DEBUG_AUDIO)
				System.out.println("reset fis ->");
			fis.reset();
			if(DebugState.DEBUG_AUDIO)
				System.out.println("<- fis reset");
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataLine.flush();
	}

}