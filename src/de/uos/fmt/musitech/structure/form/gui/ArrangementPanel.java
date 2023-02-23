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
 * Created on 15.03.2004
 */
package de.uos.fmt.musitech.structure.form.gui;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.change.IDataChangeManager;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionController;
import de.uos.fmt.musitech.framework.selection.SelectionListener;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.framework.time.Player;
import de.uos.fmt.musitech.time.gui.LinearDisplayPanel;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * This class displays the contents of a container. Scrolling and zooming is
 * implemented. A cursor is displayed, which follows the time of a playTimer.
 * 
 * @author Jan
 *  
 */
public class ArrangementPanel extends JPanel implements Player, Timeable,
        Display, SelectionListener {

    TimeScale timeScale;

    ContainerArrangeDisplay CAD;

    JScrollBar scrollBar;

    JSlider zoomSlider;

    JPanel southPanel;

    JLabel winBeginLabel;

    JLabel winSizeLabel;
    
    private JButton zoomInButton, zoomOutButton;

    Container container;

    DefaultBoundedRangeModel model;

    ComponentListener compList;

    PlayTimer playTimer;

    long playTime;

    long winBegin = 0;

    long winDuration = 5000;

    double microsPPix = 50;

    // Variables for displaying cursor
    private Graphics offscreenGraphics;

    private BufferedImage offscreenBuffer;

    private int vHeight;

    private int vWidth;

    // Variables used for Display / SelectionController
    SelectionController selectionController;

    Object editObj;

    EditingProfile editProfile;

    boolean dataChanged = false;

    public ArrangementPanel() {

    }

    public ArrangementPanel(Container cont) {
        setContainer(cont);
    }

    /**
     * Set default values
     */
    private void initialize() {
        winDuration = Math.round(getWidth() * microsPPix);
        getTimeScale().setOffset(winBegin);
        getTimeScale().setMicrosPerPix(microsPPix);
        //		winDuration = container.getDuration();
        //		if (getWidth() != 0)
        //			setMicrosPPix(winDuration / getWidth());
        CAD.setWindowRecursive(CAD, winBegin, winDuration);
        CAD.setMicrosPerPix(microsPPix);
//        updateBeginLabel(winBegin);
//        updateDurLabel(winDuration);
    }

    public void finishLayout() {
        setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(getTimeScale(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(CAD,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        innerPanel.add(scrollPane);
        innerPanel.add(getScrollBar(), BorderLayout.SOUTH);
        add(innerPanel, BorderLayout.CENTER);
        add(getSouthPanel(), BorderLayout.SOUTH);

    }

    private ComponentListener getComponentListener() {
        if (compList == null) {
            compList = new ComponentListener() {
                @Override
				public void componentHidden(ComponentEvent e) {
                }

                @Override
				public void componentMoved(ComponentEvent e) {
                }

                @Override
				public void componentResized(ComponentEvent e) {
                    initialize();
                }

                @Override
				public void componentShown(ComponentEvent e) {
                    initialize();
                }
            };
        }
        return compList;
    }

    public TimeScale getTimeScale() {
        if (timeScale == null) {
            timeScale = new TimeScale();
        }
        return timeScale;
    }

    long lastChangeTime = 0;

    // Used for scrollBar to allow change only every 200 ms

    public JScrollBar getScrollBar() {
        if (scrollBar == null) {
            scrollBar = new JScrollBar(Adjustable.HORIZONTAL, 0, 10, 0, 500);
            lastChangeTime = System.currentTimeMillis();
            // calculates the new value of the scrollbar
            int newValue = (int) Math.min(Math.round(500 * (double) winBegin
                    / CAD.container.getDuration()), 500);
            int extent = (int) Math.min(Math.round(500 * (double) winDuration
                    / (CAD.container.getDuration() + 1000)), 500);
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
                        long offset = Math.round((e.getValue() / 500.0)
                                * CAD.container.getDuration());

                        timeScale.setOffset(offset);
                        winBegin = offset;
                        updateWindow();
//                        updateBeginLabel(offset);
                        // repaint to set cursor to
                        repaint();

                    }
                }

            });
        }
        return scrollBar;
    }

    /**
     * Redraw all containing containers
     */
    private void updateWindow() {
        CAD.setWindowRecursive(CAD, winBegin, winDuration);
    }

    public JPanel getSouthPanel() {
        if (southPanel == null) {
            southPanel = new JPanel();
            southPanel.setLayout(new FlowLayout());
//            southPanel.add(getZoomSlider());
//            southPanel.add(getWinBeginLabel());
//            southPanel.add(getWinSizeLabel());
            southPanel.add(getZoomOutButton());
            southPanel.add(getZoomInButton());

        }
        return southPanel;
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
     * Returns an ImageIcon, or null if the path was invalid.
     * 
     * @param path
     * @param description
     * @return
     */
    protected static ImageIcon createImageIcon(String path, String description) {
      java.net.URL imgURL = LinearDisplayPanel.class.getResource(path);
      if (imgURL != null)
        return new ImageIcon(imgURL, description);
      else
        System.err.println("Couldn't find file: " + path);
      return null;
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
                    // f(200) = 7200.000; // 2 hours
                    microsPPix = 0.97020000000000000000000000000018 * Math.pow(
                            1.0658917840356320873664302666472, zoomSlider
                                    .getValue());
                    //					System.out.println("slider: " + zoomSlider.getValue()
                    //							+ " milPP: " + microsPPix);
                    updateDisplay();
                }
            });
        }
        return zoomSlider;
    }

    @Override
	public void updateDisplay() {
        getTimeScale().setMicrosPerPix(microsPPix);
        CAD.setAllMillisPerPix(CAD, microsPPix);
        winDuration = Math.round(CAD.getWidth() * microsPPix);
        updateWindow();
        updateDurLabel(winDuration);

        int newBegin = (int) Math.min(Math.round(500 * (double) winBegin
                / CAD.container.getDuration()), 500);
        int extent = (int) Math.min(Math.round(500 * (double) winDuration
                / (CAD.container.getDuration() + 1000)), 500);
        if (extent + newBegin > 500) {
            newBegin = 500 - extent;
            scrollBar.setValue(newBegin);
        }
        //		System.out.println(" new begin: " + newBegin + " extent " + extent
        //				+ " microsPP " + microsPPix);
        model = new DefaultBoundedRangeModel(newBegin, extent, 0, 500);
        getScrollBar().setModel(model);
        repaint();
    }

    public JLabel getWinBeginLabel() {
        if (winBeginLabel == null) {
            winBeginLabel = new JLabel();
            winBeginLabel.setText("begin: " + winBegin / 1000);
        }
        return winBeginLabel;
    }

//    public void updateBeginLabel(long newBegin) {
//        winBeginLabel.setText("begin: " + newBegin / 1000);
//    }

    public JLabel getWinSizeLabel() {
        if (winSizeLabel == null) {
            winSizeLabel = new JLabel();
            winSizeLabel.setText("size: " + winDuration / 1000);
        }
        return winSizeLabel;
    }

    void updateDurLabel(long newSize) {
//        winSizeLabel.setText("size: " + newSize / 1000);
    }

    /**
     * @see de.uos.fmt.musitech.framework.time.Player#start()
     */
    @Override
	public void start() {
    }

    /**
     * @see de.uos.fmt.musitech.framework.time.Player#stop()
     */
    @Override
	public void stop() {
    }

    /**
     * @see de.uos.fmt.musitech.framework.time.Player#reset()
     */
    @Override
	public void reset() {
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
     * @see de.uos.fmt.musitech.framework.time.Player#setPlayTimer(de.uos.fmt.musitech.framework.time.PlayTimer)
     */
    @Override
	public void setPlayTimer(PlayTimer timer) {
        playTimer = timer;
        playTimer.registerForPush(this);
    }
    
    /**
     * @param container The container to set.
     */
    public void setContainer(Container container) {
        if (container == null) {
            // dummy Container
            container = new BasicContainer();
        }
        BasicContainer suroundCont = new BasicContainer();
        suroundCont.setName("*");
        suroundCont.add(new PerformanceNote(0, 1, 0));
        suroundCont.add(container);
        this.container = suroundCont;
        
        createGUI();
        addComponentListener(getComponentListener());
        DataChangeManager.getInstance().interestExpandObject(this, container);
        DataChangeManager.getInstance().interestExpandElements(this, container);
    }

    /**
     * @see de.uos.fmt.musitech.framework.time.Player#getEndTime()
     */
    @Override
	public long getEndTime() {
        return container.getDuration();
    }

    /**
     * Creates an offscreen Image
     */
    private void createOffscreenImage() {
        vWidth = getWidth();
        vHeight = getHeight();
        // new graphics only, when something has changed

        if ((offscreenBuffer == null || offscreenBuffer.getWidth() != vWidth || offscreenBuffer
                .getHeight() != vHeight)
                && isVisible()) {
            if (vWidth * vHeight > 1333333) {
                if (DebugState.DEBUG)
                    System.out
                            .println("WARNING: offscreen buffer not created in "
                                    + this.getClass()
                                    + ". It would have taken more than 4 MB!");
                offscreenBuffer = null;
                offscreenGraphics = null;
                return;
            }
            offscreenBuffer = (BufferedImage) createImage(vWidth, vHeight);
            offscreenGraphics = offscreenBuffer.createGraphics();
            offscreenGraphics.setClip(0, 0, vWidth, vHeight);
        }

    }

    Graphics timeCursor;

    long lastTime; // to remember last cursor position

    private void paintCursor(boolean callFromPaint) {

        createOffscreenImage();
        if (offscreenGraphics == null)
            return;

        offscreenGraphics.setXORMode(Color.WHITE);

        // delete old cursor
        if (!callFromPaint) {
            offscreenGraphics.fillRect(
                    (int) ((lastTime - winBegin) / microsPPix), 0, 1,
                    getHeight()
                            - (scrollBar.getHeight() + getSouthPanel()
                                    .getHeight()));
        }
        // paint new cursor
        offscreenGraphics
                .fillRect((int) ((playTime - winBegin) / microsPPix), 0, 1,
                        getHeight()
                                - (scrollBar.getHeight() + getSouthPanel()
                                        .getHeight()));

        offscreenGraphics.setPaintMode();
        if (!callFromPaint) {
            if (getGraphics() != null) {
                getGraphics().drawImage(offscreenBuffer, 0, 0, getWidth(),
                        getHeight(), this);
            }
        }

    }

    /**
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
	public void paint(Graphics g) {
        createOffscreenImage();
        if (offscreenGraphics != null) {
            super.paint(offscreenGraphics);
            paintChildren(offscreenGraphics);
            paintCursor(true);
            g.drawImage(offscreenBuffer, 0, 0, offscreenBuffer.getWidth(), offscreenBuffer.getHeight(), this);
        } else {
            super.paint(g);
            paintChildren(g);
            paintCursor(true);
        }

    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
     */
    @Override
	public boolean externalChanges() {
        return dataChanged;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
     */
    @Override
	public void destroy() {
        SelectionManager selMan = SelectionManager.getManager();
        selMan.removeListener(this);
        IDataChangeManager datMan = DataChangeManager.getInstance();
        datMan.removeListener(this);
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
     */
    @Override
	public void focusReceived() {
        System.out.println("Arrangement Panel: Focus Received");
        if (dataChanged) {
            dataChanged = false;
            updateDisplay();
        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
     */
    @Override
	public EditingProfile getEditingProfile() {
        return editProfile;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
     */
    @Override
	public Object getEditObj() {
        return container;
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    @Override
	public boolean isFocused() {

        return isFocusOwner();
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Display)
     */
    @Override
	public void init(Object editObject, EditingProfile profile, Display root) {
        this.editObj = editObject;
        //		System.out.println("ArrangementPanel.init() cont.start(): " +
        // ((Container) editObj).getTime());
        if (((Container) editObj).getTime() != 0) {
            // surounding container must start at 0
            // to avoid display errors
            //			System.out.println("ArrangementPanel.init() cont.start(): " +
            // ((Container) editObj).getTime());
            BasicContainer suroundCont = new BasicContainer();
            suroundCont.setName("*");
            suroundCont.add(new PerformanceNote(0, 1, 0));
            suroundCont.add(((Container) editObj));
            this.container = suroundCont;
            container = suroundCont;
        } else {
            container = (Container) editObj;
        }

        this.editProfile = profile;

        SelectionManager selMan = SelectionManager.getManager();
        selMan.addListener(this);
        IDataChangeManager datMan = DataChangeManager.getInstance();
        datMan.interestExpandElements(this, container);
        addComponentListener(getComponentListener());
        createGUI();
    }

    /**
     *  
     */
    private void createGUI() {
        //		System.out.println("ArrangementPanel: new CAD(container)");
        CAD = new ContainerArrangeDisplay(container);
        setMicrosPPix(50000);
        finishLayout();

        initialize();

        setMicrosPPix(50000);
        updateDisplay();

    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Display#getRootDisplay()
     */
    @Override
	public Display getRootDisplay() {
        return this;
    }

    
    
    /** 
     * @see java.awt.Component#getPreferredSize()
     */
    @Override
	public Dimension getPreferredSize() {
        return new Dimension(10, 10);
    }
    /**
     * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
     */
    @Override
	public void dataChanged(DataChangeEvent e) {
        //		System.out.println("ArragementPanel: Data Changed");
        dataChanged = true;
        DataChangeManager.getInstance().interestExpandElements(this, container);
        // Display update when focus received
    }

    /**
     * @see de.uos.fmt.musitech.framework.selection.SelectionListener#selectionChanged(de.uos.fmt.musitech.framework.selection.SelectionChangeEvent)
     */
    @Override
	public void selectionChanged(SelectionChangeEvent e) {
    }

    /**
     * @return
     */
    public double getMicrosPPix() {
        return microsPPix;
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
        sliderValue = (int) (num1 / num2);
        getZoomSlider().setValue(sliderValue);
        updateDisplay();
    }

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	@Override
	public Component asComponent() {
		return this;
	}

}