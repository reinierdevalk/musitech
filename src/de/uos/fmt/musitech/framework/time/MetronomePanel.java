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
 * Created on 01.09.2004
 */
package de.uos.fmt.musitech.framework.time;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * Visual representation of a metronome.
 * 
 * @author Jan
 *
 */
public class MetronomePanel extends JPanel implements Player {

	private final Color activeBeat = new Color(10, 200, 71).darker();
	private final Color inactiveBeat = new Color(130, 180, 110).brighter();
	private final Color activeMeas = new Color(190, 40, 40).darker();
	private final Color inactiveMeas = new Color(190, 100, 100).brighter();

	PlayTimer playTimer;
	private Context context;
	MetricalTimeLine timeLine;
	private PlayThread playThread;
	private boolean playing = false;
	int numerator;

	BeatButton[] button = new BeatButton[128];

	long nextBeatMicros;
	long prefBeatMicros;

	MetronomePanel() {
		init();
		createGUI();
	}

	/**
	 * init is called by PlayTimer$MetronomPlayTimer, do avoid
	 * having // TODO havin what ?
	 */
	public void init() {
		playThread = new PlayThread();
		playThread.setPriority(Thread.MIN_PRIORITY);
		playThread.start();
	}

	/**
	 * 
	 */
	void createGUI() {
//		System.out.print("removed button ");
		for (int i = 0; i < button.length; i++) {

			if (button[i] != null) {
//				System.out.print(i + " ");
				remove(button[i]);
			}
		}
//		System.out.println();
		if (playTimer != null) {

			numerator =
				getTimeLine()
					.getTimeSignatureMarker(
						timeLine.getMetricTime(playTimer.getPlayTimeMicros()))
					.getTimeSignature()
					.getNumerator();
		} else {
			numerator =
				getTimeLine()
					.getTimeSignatureMarker(timeLine.getMetricTime(0))
					.getTimeSignature()
					.getNumerator();
		}
		if(numerator == Integer.MAX_VALUE)
		    return;
//		System.out.println("Create GUI numerator: " + numerator);
		setLayout(new GridLayout(1, numerator));

		if (timeLine != null) {
			for (int i = 0; i < numerator; i++) {
				if (i == 0) {
					button[i] = new BeatButton(activeMeas, inactiveMeas);
				} else {
					button[i] = new BeatButton(activeBeat, inactiveBeat);
				}
				add(button[i]);
			}
		}
		revalidate();
		repaint();

	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#start()
	 */
	public void start() {
		prefBeatMicros =
			getTimeLine().getPreviousBeat(playTimer.getPlayTimeMicros());
		//		nextBeatMicros =
		//			timeLine.getNextOrSameBeat(playTimer.getPlayTimeMicros());
		int but = getTimeLine().getBeatPosition(nextBeatMicros);
		button[but].setActive(true);
		playing = true;
		synchronized (playThread) {
			playThread.notify();
		}
	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#stop()
	 */
	public void stop() {
		playing = false;
		for (int i = 0; i < numerator; i++) {
			button[i].setActive(false);
		}
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
		nextBeatMicros = getTimeLine().getNextOrSameBeat(time);
		// used for start to know which button to active
		int but = timeLine.getBeatPosition(nextBeatMicros);
		createGUI();
		
		//		button[but].setActive(true);

	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#setPlayTimer(de.uos.fmt.musitech.framework.time.PlayTimer)
	 */
	public void setPlayTimer(PlayTimer timer) {
		playTimer = timer;
		//		context = playTimer.getContext();
		//		timeLine = context.getPiece().getMetricalTimeLine();
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
	public Context getContext() {
		if (context == null) {
			Piece piece = new Piece();
			context = piece.getContext();
		}
		return context;
	}

	/**
	 * @return
	 */
	public void setContext(Context context) {
		if (context == null) {
			Piece piece = new Piece();
			context = piece.getContext();

		}
		this.context = context;
		//		context = playTimer.getContext();
		timeLine = context.getPiece().getMetricalTimeLine();
		createGUI();
	}

	private class PlayThread extends Thread {

		public PlayThread() {
			super("MetronomePanel PlayThread");
		}

		/** 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			if(DebugState.DEBUG_AUDIO)
				System.out.println("MetronomePanel enter run");
			while (true) {

				synchronized (this) {
					if (playTimer == null || !playTimer.isPlaying()) {
						try {
							//							Thread.sleep(30);
							if(DebugState.DEBUG)
								System.out.println("MetronomePanel waiting to run");

							this.wait();

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} // while(!playTimer.isPlaying()
				if(DebugState.DEBUG)
					System.out.println("MetronomePanel now running");
				while (true) {

					if (playTimer.getPlayTimeMicros() + 10000
						> nextBeatMicros) {

						button[getTimeLine().getBeatPosition(
							nextBeatMicros)].setActive(
							true);
						button[timeLine.getBeatPosition(
							prefBeatMicros)].setActive(
							false);

						prefBeatMicros = nextBeatMicros;
						nextBeatMicros =
							timeLine.getNextBeat(
								playTimer.getPlayTimeMicros() + 10000);
//						System.out.println(
//							"run() old num: "
//								+ numerator
//								+ " new num: "
//								+ getTimeLine()
//									.getTimeSignatureMarker(
//										timeLine.getMetricTime(
//											playTimer.getPlayTimeMicros()))
//									.getTimeSignature()
//									.getNumerator());
						if (numerator
							!= getTimeLine()
								.getTimeSignatureMarker(
									timeLine.getMetricTime(
										playTimer.getPlayTimeMicros()))
								.getTimeSignature()
								.getNumerator()) {
							// TODO: write update GUI Method
							playTimer.stop();
							createGUI();
						}
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (!playing) {
						break;
					}
				}
			}
		}

	}

	private class BeatButton extends JPanel {

		Color activeColor;
		Color inactiveColor;
		boolean active;

		public BeatButton(Color activeColor, Color inactiveColor) {
			this.inactiveColor = inactiveColor;
			this.activeColor = activeColor;
		}

		public void setActiveColor(Color color) {
			this.activeColor = color;
		}

		Graphics graphics;

		/** 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			graphics = g;
			if (active) {
				paintButton(g, activeColor);
			} else {
				paintButton(g, inactiveColor);
			}
		}

		/**
		 * @param g
		 */
		private void paintButton(Graphics g, Color color) {
			setForeground(color);
			int rad = Math.min(getWidth(), getHeight());
			g.fillOval(0, 0, rad, rad);
		}

		/**
		 * @return
		 */
		public boolean isActive() {
			return active;
		}

		/**
		 * @param b
		 */
		public void setActive(boolean b) {
			active = b;
			if (graphics != null)
				paintComponent(graphics);
			repaint();

		}

	}

	public static void main(String[] args) {
		MetronomePanel metroPanel = new MetronomePanel();
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(metroPanel, BorderLayout.CENTER);
		frame.setLocation(200, 450);
		frame.pack();
		frame.setSize(500, 200);
		frame.show();
	}

	/**
	 * @return
	 */
	public MetricalTimeLine getTimeLine() {
		if (timeLine == null) {
			timeLine = getContext().getPiece().getMetricalTimeLine();
		}
		return timeLine;
	}

}
