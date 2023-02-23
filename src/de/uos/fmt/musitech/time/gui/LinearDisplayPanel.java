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
 * Created on 30.07.2004
 */
package de.uos.fmt.musitech.time.gui;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.selection.SelectionController;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.structure.form.gui.TimeScale;

/**
 * This class combines a TimeScale with zoomfunction and 
 * Scrollbar. You can put a JComponent which implements LinearDisplay
 * in center. 
 * It is implemented in PianoRollPanel.
 * 
 * @author Jan
 *
 */
public class LinearDisplayPanel extends JPanel implements Timeable {

	// this is the same object !!
	// two variables are used to have methods of JComponent and LinearDisplay
	 
	private JComponent jComponentMain;
	private LinearDisplay linearDisplayMain;

	private TimeScale timeScale;
	private JScrollBar scrollBar;
	private JSlider zoomSlider;
	private JPanel southPanel;
	private JLabel winBeginLabel;
	private JLabel winSizeLabel;
	private Container container;
	private DefaultBoundedRangeModel model;
	private JButton zoomInButton, zoomOutButton;

	private ComponentListener compList;
	private PlayTimer playTimer;
	private long playTime;

	private long winBegin = 0;
	private long winSize = 5000;
	private double microsPPix = 10000;

	// Variables for displaying cursor
	private Graphics offscreenGraphics;
	private BufferedImage offscreenBuffer;
	private int vHeight;
	private int vWidth;

	// Variables used for Display / SelectionController
	private SelectionController selectionController;

	private Object editObj;
	private EditingProfile editProfile;
	private boolean dataChanged = false;

	/**
	 * 
	 */
	public LinearDisplayPanel() {
		init();
	}

	/**
	 * 
	 */
	private void init() {

		playTimer = PlayTimer.getInstance();
//		playTimer.registerForPush(this);
		addComponentListener(new ComponentListener() {
            @Override
			public void componentHidden(ComponentEvent e) {
            }

            @Override
			public void componentMoved(ComponentEvent e) {
                repaint();
            }

            @Override
			public void componentResized(ComponentEvent e) {
                repaint();
            }

            @Override
			public void componentShown(ComponentEvent e) {
                repaint();
            }
        });

	}

	/**
	 * 
	 */
	private void createGUI() {
		setLayout(new BorderLayout());
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(getTimeScale());
		box.add(getMainPanel());
		box.add(getScrollBar());
		add(box, BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);
		setMicrosPPix(10000);
	}

	/**
	 * TODO comment
	 * @return
	 */
	public TimeScale getTimeScale() {
		if (timeScale == null) {
			timeScale = new TimeScale();
		}
		return timeScale;
	}

	private long lastChangeTime;

	public JComponent getMainPanel() {
		if (jComponentMain != null) {
			return jComponentMain;
		} else {
			JComponent dummyComp = new JPanel();
			return dummyComp;
		}
	}

	public JScrollBar getScrollBar() {
		if (scrollBar == null) {
			scrollBar = new JScrollBar(Adjustable.HORIZONTAL, 0, 10, 0, 500);
			lastChangeTime = System.currentTimeMillis();
			// calculates the new value of the scrollbar 
			int newValue =
				(int) Math.min(
					Math.round(
						500
							* (double) winBegin
							/ linearDisplayMain.getEndTime()),
					500);
			int extent =
				(int) Math.min(
					Math.round(
						500
							* (double) winSize
							/ (linearDisplayMain.getEndTime() + 1000)),
					500);
			if (extent + newValue > 500) {
				newValue = 500 - extent;
				scrollBar.setValue(newValue);
			}
			model = new DefaultBoundedRangeModel(newValue, extent, 0, 500);
			scrollBar.setModel(model);
			scrollBar.addAdjustmentListener(new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {

					if (System.currentTimeMillis() - lastChangeTime > 100) {
						lastChangeTime = System.currentTimeMillis();
						setScrollbarValue(e.getValue());
					}
				}

			});
		}
		return scrollBar;
	}
	
	public void setScrollbarValue(int value){
	    long offset =
			Math.round(
				(value / 500.0)
					* linearDisplayMain.getEndTime());

		timeScale.setOffset(offset);
		linearDisplayMain.setOffset(offset);
		winBegin = offset;
		updateWindow();
		// repaint to set cursor to
		repaint();
	    
	}

	public void updateWindow() {
		linearDisplayMain.updateDisplay();
	}

	public void updateDisplay() {
		timeScale.setMicrosPerPix(microsPPix);
		linearDisplayMain.setMicrosPerPix(microsPPix);
		winSize = Math.round(jComponentMain.getWidth() * microsPPix);
		updateWindow();
		

		int newBegin =
			(int) Math.min(
				Math.round(
					500
						* (double) winBegin
						/ linearDisplayMain.getEndTime()),
				500);
		int extent =
			(int) Math.min(
				Math.round(
					500
						* (double) winSize
						/ (linearDisplayMain.getEndTime() + 1000)),
				500);
		if (extent + newBegin > 500) {
			newBegin = 500 - extent;
			scrollBar.setValue(newBegin);
		}
		model = new DefaultBoundedRangeModel(newBegin, extent, 0, 500);
		getScrollBar().setModel(model);
		repaint();
	}

	/** 
		 * @see de.uos.fmt.musitech.framework.time.Player#setPlayTimer(de.uos.fmt.musitech.framework.time.PlayTimer)
		 */
	public void setPlayTimer(PlayTimer timer) {
		playTimer = timer;
		playTimer.registerForPush(this);
	}

	public JPanel getSouthPanel() {
		if (southPanel == null) {
			southPanel = new JPanel();
			southPanel.setLayout(new FlowLayout());
//			southPanel.add(getZoomSlider());
			southPanel.add(getZoomOutButton());
			southPanel.add(getZoomInButton());
			

		}
		return southPanel;
	}

	public JSlider getZoomSlider() {
		if (zoomSlider == null) {
			zoomSlider = new JSlider(1, 300);

			zoomSlider.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}
				@Override
				public void mouseEntered(MouseEvent e) {
				}
				@Override
				public void mouseExited(MouseEvent e) {
				}
				@Override
				public void mousePressed(MouseEvent e) {
				}
				@Override
				public void mouseReleased(MouseEvent e) {

					// f(x) : microsPPix
					// x : SliderValue
					// f(x) = 0.9702 * 1.0658^x
					// f(1) = 22 // 22 microsec Per Pix = 1/44100 Hz
					// f(200) = 7200.000;  // 2 hours
					microsPPix =
						0.97020000000000000000000000000018
							* Math.pow(
								1.0658917840356320873664302666472,
								zoomSlider.getValue());
					System.out.println(
						"slider: "
							+ zoomSlider.getValue()
							+ " milPP: "
							+ microsPPix);
					updateDisplay();
				}
			});
		}
		return zoomSlider;
	}
	
	public JButton getZoomInButton(){
	    if (zoomInButton == null){
	        zoomInButton = new JButton(createImageIcon("gfx/ZoomIn24.gif", "Zoom in"));
	        zoomInButton.setText("in");
	        zoomInButton.setToolTipText("zoom in");
	        zoomInButton.setMaximumSize(new Dimension(30, 30));
	        zoomInButton.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent arg0) {
                    setMicrosPPix(getMicrosPPix()/1.5);
                }
            });
	    }
	    return zoomInButton; 
	}
	
	public JButton getZoomOutButton(){
	    if (zoomOutButton == null){
	      
	        zoomOutButton = new JButton(createImageIcon("gfx/zoomOut24.gif", "Zoom out"));
	        zoomOutButton.setText("out");
	        zoomOutButton.setToolTipText("zoom Out");
	        zoomOutButton.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent arg0) {
                    setMicrosPPix(getMicrosPPix()*1.5);
                }
            });
	    }
	    return zoomOutButton; 
	}
	
	
	/**
	 * @return
	 */
	public LinearDisplay getDisplay() {
		return linearDisplayMain;
	}

	/**
	 * The Display must be a JComponent which implements LinearDisplay,
	 * otherwise you will force a cast Exception.
	 * 
	 * @param display
	 */
	public void setDisplay(LinearDisplay display) {
		linearDisplayMain = display;
		jComponentMain = (JComponent) display;
		createGUI();
	}

	private Graphics timeCursor;
	private long lastTime; // to remember last cursor position

	/**
     * Returns an ImageIcon, or null if the path was invalid.
     * 
     * @param path
     * @param description
     * @return
     */
    protected static ImageIcon createImageIcon(String path, String description) {
      java.net.URL imgURL = LinearDisplayPanel.class.getResource(path);
      // System.out.println("linearDisplay: url " + description + ": " + imgURL);
      if (imgURL != null)
        return new ImageIcon(imgURL, description);
      else
        System.err.println("Couldn't find file: " + path);
      return null;
    }
	
	
	/**
	 * TODO comment
	 * @param callFromPaint
	 */
	private void paintCursor(boolean callFromPaint) {

		createOffscreenImage();

		offscreenGraphics.setXORMode(Color.WHITE);

		// delete old cursor		
		if (!callFromPaint) {
			offscreenGraphics.fillRect(
				(int) ((lastTime - winBegin) / microsPPix),
				0,
				1,
				getHeight()
					- (scrollBar.getHeight() + getSouthPanel().getHeight()));
		}
		// paint new cursor
		offscreenGraphics.fillRect(
			(int) ((playTime - winBegin) / microsPPix),
			0,
			1,
			getHeight()
				- (scrollBar.getHeight() + getSouthPanel().getHeight()));

		offscreenGraphics.setPaintMode();
		if (!callFromPaint) {
			getGraphics().drawImage(
				offscreenBuffer,
				0,
				0,
				getWidth(),
				getHeight(),
				this);
		}

	}

	/**
	 * Creates an offscreen Image
	 */
	private void createOffscreenImage() {
		vWidth = getWidth();
		vHeight = getHeight();
		// new graphics only, when something has changed
		if (offscreenBuffer == null
			|| offscreenBuffer.getWidth() != vWidth
			|| offscreenBuffer.getHeight() != vHeight) {
			offscreenBuffer = (BufferedImage) createImage(vWidth, vHeight);
			offscreenGraphics = offscreenBuffer.createGraphics();
			offscreenGraphics.setClip(0, 0, vWidth, vHeight);
		}

	}

	/** 
		 * @see java.awt.Component#paint(java.awt.Graphics)
		 */
	@Override
	public void paint(Graphics g) {
		createOffscreenImage();
		super.paint(offscreenGraphics);

		paintChildren(offscreenGraphics);
		paintCursor(true);
		g.drawImage(offscreenBuffer, 0, 0, getWidth(), getHeight(), this);

	}

	/** 
	 * @see de.uos.fmt.musitech.framework.time.Player#setTimePosition(long)
	 */
	@Override
	public void setTimePosition(long time) {
		lastTime = playTime;
		playTime = time;
		SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                paintCursor(false);
            }
        });
		

	}
	
	/**
	 * @param d
	 */
	public void setMicrosPPix(double mpp) {
		microsPPix = mpp;
		int sliderValue;
		// inverse function of zoomslider
		// f(x) = 0.9702 * 1.0658^x
		double num1 = Math.log(mpp / 0.97020000000000000000000000000018);
 		double num2 = Math.log(1.0658917840356320873664302666472);
 		sliderValue = (int) (num1/num2);
 		getZoomSlider().setValue(sliderValue);
 		updateDisplay();
	}

	/**
	 * TODO comment
	 * @see de.uos.fmt.musitech.data.time.Timeable#getEndTime()
	 */
	@Override
	public long getEndTime() {
		if (linearDisplayMain != null)
			return linearDisplayMain.getEndTime();
		else return 0;
	}

    /**
     * @return Returns the microsPPix.
     */
    public double getMicrosPPix() {
        return microsPPix;
    }
}
