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
 * Created on 23.09.2003
 */

package de.uos.fmt.musitech.framework.time;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.InvalidParameterException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * @author Jan
 */
public class TransportButtons extends JPanel implements Timeable, Recorder {

	private static final long serialVersionUID = 1L;

	PlayTimer playTimer = null;
	private JButton playButton = null;
	private JButton recButton = null;
	private JButton stopButton = null;
	private JButton resetButton = null;
	private JButton forwardButton = null;
	private JButton rewindButton = null;
	
	private JButton playStopButton = null;
	private boolean psb_state = false;
	
	JSlider timeSlider = null;
	private JPanel timePanel = null;
	private boolean playOnly = false;
	private boolean recording;
	private final String FORWARD = "forward";
	private final String REWIND = "rewind";

	private JToggleButton metronomToggle;

	private JToggleButton preCountToggle;

	private JPanel north;

	private JPanel south;

	int layoutType = BIG;
	
	/** Constant for BIG layout */
	public static final int BIG = 0;
	/** Constant for SMALL layout */
	public static final int SMALL = 1;
	/** Constant for one-click TINY layout */
	public static final int TINY = 2;

	/**
	 * Create TransportButtons with default (big) layout
	 * TODO comment
	 */
	public TransportButtons() {
		this(BIG);
	}

	/**
	 * Create TransportButtons for the standard PlayTimer in the given size.
	 * 
	 * @param size Either TransportButtons.BIsG
	 */
	public TransportButtons(int size) {
		super();
		layoutType = size;
		PlayTimer.getInstance().registerForPush(this);
		PlayTimer.getInstance().registerPlayer(this);
		createGUI();
	}

	/**
	 * Create TransportButtons for the given PlayTimer in the given size.
	 * 
	 * @param newPlayTimer
	 */
	public TransportButtons(PlayTimer newPlayTimer) {
		this(newPlayTimer, BIG);
	}

	/**
	 * Create TransportButtons for the given PlayTimer in the given size.
	 * 
	 * @param newPlayTimer
	 */
	public TransportButtons(PlayTimer newPlayTimer, int size) {
		super();
		layoutType = size;
		playTimer = newPlayTimer;
		playTimer.registerForPush(this);
		playTimer.registerPlayer(this);
		createGUI();
	}

	private void createGUI() {
		if (layoutType  == BIG) {
			removeAll();
			setLayout(new GridLayout(2, 1));
			north = new JPanel(new GridLayout(1, 4));
			south = new JPanel(new GridLayout(1, 2));
			north.add(getPlayButton());
			north.add(getStopButton());
			north.add(getRecButton());
			north.add(getResetButton());
			// add(getRewindButton());
			// add(getForwardButton());
			south.add(getTimePanel());
			south.add(getTimeSlider());
			add(north);
			add(south);
			setPreferredSize(new Dimension(250, 100));
			setMinimumSize(new Dimension(150, 60));
			setMaximumSize(new Dimension(350, 150));
		}
		if (layoutType == SMALL){
			removeAll();
			setLayout(new GridLayout(1, 0));
			add(getPlayButton());
			add(getTimeSlider());
			add(getStopButton());
			add(getResetButton());
			doLayout();
			setOpaque(false);
			setPreferredSize(new Dimension(100, 30));
			setMinimumSize(new Dimension( 50, 20));
			setMaximumSize(new Dimension(250, 100));
		}
		
		if (layoutType == TINY) {
			removeAll();
			setLayout(new GridLayout(1, 0));
			add(getPlayStopButton());
			doLayout();
			setOpaque(false);
			setPreferredSize(new Dimension(100, 30));
			setMinimumSize(new Dimension( 50, 20));
			setMaximumSize(new Dimension(250, 100));			
		}
	}

//	/**
//	 * @see javax.swing.JComponent#getPreferredSize()
//	 */
//	public Dimension getPreferredSize() {
//		if(layoutType == SMALL)
//			return new Dimension(150, 50);
//		else 
//			return new Dimension(250, 100);
//	}

//	/**
//	 * @see javax.swing.JComponent#getMaximumSize()
//	 */
//	public Dimension getMaximumSize() {
//		return new Dimension(250, 100);
//	}

//	/**
//	 * @see javax.swing.JComponent#getMinimumSize()
//	 */
//	public Dimension getMinimumSize() {
//		if(layoutType == SMALL)
//			return new Dimension(50, 20);
//		else 
//			return new Dimension(200, 75);
//	}

	public JPanel getTimePanel() {
		if (timePanel == null) {
			timePanel = new JPanel(new GridLayout(1, 3));
			TimePanel time = new TimePanel(playTimer);

			metronomToggle = new JToggleButton(createImageIcon("pict/metro_stop.gif",
				"stop Metronom"));

			metronomToggle.setDisabledIcon(createImageIcon("pict/metro_stop.gif", "stop Metronom"));
			metronomToggle.setSelectedIcon(new ImageIcon(getClass().getResource(
				"pict/metro_play.gif")));
			metronomToggle.setToolTipText("Metronome");
			metronomToggle.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						playTimer.setMetronome(false);
					} else {
						playTimer.setMetronome(true);
					}
				}
			});
			preCountToggle = new JToggleButton(new ImageIcon(getClass().getResource(
				"pict/precount_off.gif")));
			preCountToggle.setDisabledIcon(new ImageIcon(getClass().getResource(
				"pict/precount_off.gif")));
			preCountToggle.setSelectedIcon(new ImageIcon(getClass().getResource(
				"pict/precount_on.gif")));
			preCountToggle.setToolTipText("PreCount");
			preCountToggle.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						playTimer.setPreCount(false);
					} else {
						playTimer.setPreCount(true);
					}
				}
			});
			timePanel.add(metronomToggle);
			timePanel.add(preCountToggle);
			timePanel.add(time);
		}
		return timePanel;

	}

	public JButton getPlayButton() {
		if (playButton == null) {
			try {
				playButton = new javax.swing.JButton();
				playButton.setText("play");
				playButton.setName("playButton");
				playButton.setOpaque(false);
				// URL url =
				// TransportButtons.class.getResource("pict/play.gif");
				// System.out.println("Playbutton manual URL" + url);
				// playButton.setIcon(new ImageIcon(url));
//				playButton.setIcon(createImageIcon("pict/Play24.gif", "play"));
				playButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (DebugState.DEBUG)
							System.out.println("TransButton: play pressed");
						playTimer.start();
						getPlayButton().setEnabled(false);
						getStopButton().setEnabled(true);
					}
				});
			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return playButton;
	}

	public JButton getRecButton() {
		if (recButton == null) {
			try {
				recButton = new javax.swing.JButton();
				recButton.setName("recButton");
				recButton.setText("rec");
				recButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("TransButton: rec pressPoint");
						// playTimer.start(getRecButton());
						playTimer.record();
						paintRecButton();
					}
				});
			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return recButton;
	}

	/**
	 * Creates a button that handles control for playback
	 * Shows "Play" if inactive and "Stop" during playback.
	 * @return Object Reference
	 */
	public JButton getPlayStopButton() {
		if (playStopButton == null) {
			try {
				playStopButton = new javax.swing.JButton();				
				playStopButton.setName("playStopButton");
				playStopButton.setText("Play");
				playStopButton.setOpaque(false);
//				playStopButton.setIcon(createImageIcon("pict/Stop24.gif", "stop"));
				playStopButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg1) {
						if(psb_state) { // stop play back
							System.out.println("TransButton: playstop pressPoint");
							playTimer.stop();
							playTimer.reset(); // start from beginning
							psb_state = false;
							playStopButton.setText("Play");
							// TODO: change icon
//							playStopButton.setIcon(createImageIcon("pict/play.gif", "play"));							
						} else { // start play back
							playTimer.start();							
							psb_state = true;
							playStopButton.setText("Stop");
							// TODO: change icon
//							playStopButton.setIcon(createImageIcon("pict/Stop24.gif", "stop"));							
						}
					}
				});
				playStopButton.setEnabled(true);
			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return playStopButton;		
	}
	
	public void paintRecButton() {
		if (playTimer.isRecording())
			getRecButton().setBackground(Color.RED);
		else
			getRecButton().setBackground(Color.LIGHT_GRAY);
		getRecButton().repaint();
	}

	public JButton getStopButton() {
		if (stopButton == null) {
			try {
				stopButton = new javax.swing.JButton();
				stopButton.setName("stopButton");
				stopButton.setText("stop");
				stopButton.setOpaque(false);
//				stopButton.setIcon(createImageIcon("pict/Stop24.gif", "stop"));
				stopButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg1) {
						System.out.println("TransButton: stop pressPoint");
						playTimer.stop();
						playButton.setEnabled(true);
						stopButton.setEnabled(false);
						paintRecButton();
						// if (recording) {
						// System.out.println("recording stopped");
						// recording = false;
						// playTimer.stop();
						// }
					}
				});
				stopButton.setEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return stopButton;
	}

	boolean sliderDragging = false;

	public JSlider getTimeSlider() {
		if (timeSlider == null) {
			try {
				timeSlider = new JSlider();
				timeSlider.setName("timeSlider");
				timeSlider.setMinimum(0);
				timeSlider.setMaximum((int) (getPlayTimer().getEndOfAllPlayer() / 1000));
				timeSlider.setValue(0);
				timeSlider.setOpaque(false);
				timeSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mousePressed(MouseEvent arg0) {
						sliderDragging = true;
						timeSlider.setMaximum((int) (getPlayTimer().getEndOfAllPlayer() / 1000));

						// player.stop();
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						setTimeToSlider();
						sliderDragging = false;
					}
				});
				timeSlider.addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent event) {
						// if(!sliderDragging){
						// sliderDragging = true;
						// }
					}
				});
			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return timeSlider;
	}

	/**
	 * Adjusts the time of the playTimer to the slider Value. Interrupts to
	 * performance.
	 */
	void setTimeToSlider() {
		if (sliderDragging) {
			sliderDragging = false;
			int value;
			value = timeSlider.getValue();
			boolean playing = playTimer.isPlaying();
			if (playing) 
				playTimer.stop();

			playTimer.setPlayTimeMicros(value * 1000);
			if (playing)
				playTimer.start();
		}
	}

	/**
	 * @param timeMillis
	 */
	private void updateTimeSlider(long timeMillis, long end) {
		getTimeSlider().setValue((int) timeMillis / 1000);
		getTimeSlider().setMaximum((int) end / 1000);
	}

	public JButton getForwardButton() {
		if (forwardButton == null) {
			try {
				forwardButton = new javax.swing.JButton();
				forwardButton.setName("forwardButton");
				forwardButton.setText("forward");
				forwardButton.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent arg0) {
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						pressed = false;
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
						pressed = true;
						wind(FORWARD);
						// playTimer.setTempoInBPM(200);
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						pressed = false;
						// playTimer.enableExternalTempoChanges();
					}
				});
				forwardButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("TransButton: forward pressPoint");
					}
				});
			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return forwardButton;
	}
	boolean pressed = false;

	public JButton getRewindButton() {
		if (rewindButton == null) {
			try {
				rewindButton = new javax.swing.JButton();
				rewindButton.setName("rewindButton");
				rewindButton.setText("rewind");
				// rewindButton.addActionListener(new ActionListener() {
				// public void actionPerformed(ActionEvent arg0) {
				// System.out.println("TransButton: rewind pressPoint");
				//
				// }
				// });
				rewindButton.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
					}

					@Override
					public void mouseEntered(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
						pressed = false;
					}

					@Override
					public void mousePressed(MouseEvent e) {
						pressed = true;
						wind(REWIND);
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						pressed = false;
					}
				});
			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return rewindButton;
	}

	private void wind(String direction) {
		long currentTime = playTimer.getPlayTimeMicros();
		long time = 100;

		while (pressed && (direction == FORWARD || direction == REWIND)) {

			if (direction == FORWARD) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				playTimer.setPlayTimeMicros(currentTime + time);
				time += 100;
			}
			if (direction == REWIND) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				playTimer.setPlayTimeMicros(currentTime - time);
				time += 100;
			}
		}
	}

	public JButton getResetButton() {
		if (resetButton == null) {
			try {
				resetButton = new javax.swing.JButton();
				resetButton.setName("resetButton");
				resetButton.setText("reset");
//				resetButton.setIcon(createImageIcon("pict/StepBack24.gif", "reset"));
				resetButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("TransButton: reset pressPoint");
						playTimer.reset();
						paintRecButton();
						stopButton.setEnabled(false);
						playButton.setEnabled(true);
					}
				});
			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return resetButton;
	}

	/**
	 * @return
	 */
	public PlayTimer getPlayTimer() {
		return playTimer;
	}

	/**
	 * @param timer
	 */
	@Override
	public void setPlayTimer(PlayTimer timer) {
		// timer.unRegisterForPush(this);
		// timer.unRegisterPlayer(this);
		playTimer = timer;
		// playTimer.registerForPush(this);
		// playTimer.registerPlayer(this);
	}

	/**
	 * @see de.uos.fmt.musitech.data.time.Timeable#setTimePosition(long)
	 */
	@Override
	public void setTimePosition(long timeMicros) {
		if (!sliderDragging)
			updateTimeSlider(timeMicros, playTimer.getEndOfAllPlayer());
	}

	/**
	 * End time not relevant for this object.
	 * 
	 * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
	 */
	@Override
	public long getEndTime() {
		return -1;
	}

	/**
	 * TODO add comment
	 * 
	 * @see de.uos.fmt.musitech.framework.time.Player#start()
	 */
	@Override
	public void start() {
		getStopButton().setEnabled(true);
		getPlayButton().setEnabled(false);
		if(layoutType == TINY) {
			psb_state = true;
			getPlayStopButton().setText("Stop");
			getPlayStopButton().setEnabled(true);
			
		}
	}

	/**
	 * TODO add comment
	 * 
	 * @see de.uos.fmt.musitech.framework.time.Player#stop()
	 */
	@Override
	public void stop() {
		getStopButton().setEnabled(false);
		getPlayButton().setEnabled(true);
		if(layoutType == TINY) {
			psb_state = false;
			getPlayStopButton().setText("Start");			
			getPlayStopButton().setEnabled(true);
			reset();
		}
	}

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 * 
	 * @param path
	 * @param description
	 * @return
	 */
	protected static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = TransportButtons.class.getResource(path);
		if (imgURL != null)
			return new ImageIcon(imgURL, description);
		else
			System.err.println("Couldn't find file: " + path);
		return null;
	}

	/**
	 * Sets the stop button disabled and the play enabled.
	 * 
	 * @see de.uos.fmt.musitech.framework.time.Player#reset()
	 */
	@Override
	public void reset() {
		stopButton.setEnabled(false);
		playButton.setEnabled(true);
		if(layoutType == TINY)
			getPlayStopButton().setEnabled(true);		
	}

	/**
	 * Enables the <code>recButton</code> if <code>recording</code> is true,
	 * disables the <code>recButton</code> if <code>recording</code> is
	 * false.
	 * 
	 * @param recording boolean indicating the requested state of the
	 *            <code>recButton</code>
	 */
	public void setRecordingEnabled(boolean recording) {
		getRecButton().setEnabled(recording);
	}

	/**
	 * @see de.uos.fmt.musitech.framework.time.Recorder#setRecord(boolean)
	 */
	@Override
	public void setRecord(boolean record) {
		getRecButton().setEnabled(recording);
		getStopButton().setEnabled(true);
		getPlayButton().setEnabled(false);
	}

	public boolean isPlayOnly() {
		return playOnly;
	}

	public void setPlayOnly(boolean argPlayOnly) {
		this.playOnly = argPlayOnly;
		if (playOnly) {
			north.remove(recButton);
			getTimePanel().remove(metronomToggle);
			getTimePanel().remove(preCountToggle);
		} else {
			north.add(getRecButton());
			getTimePanel().add(metronomToggle);
			getTimePanel().add(preCountToggle);
		}
	}
	
	/**
	 * Sets the button layout
	 * @param layout one of the layout constants: BIG, SMALL, TINY
	 */
	public void setButtonLayout(int layout) {
		if(layout < 0)
			throw new InvalidParameterException("in TransportButtons: invalid layout");
		layoutType = layout;
		createGUI();
	}
	
	/**
	 * Returns the button layout type
	 * @return
	 */
	public int getButtonLayout() {
		return layoutType;
	}

	/**
	 * Sets small layout
	 * @param small
	 */
	public void setSmallLayout(boolean small) {
		layoutType = small?SMALL:BIG;
		createGUI();
	}

	public boolean getSmallLayout() {
		return layoutType==SMALL;
	}
	
	/**
	 * Enables or disables all components.
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		for(final Component c: getComponents())
			c.setEnabled(enabled);
	}
}