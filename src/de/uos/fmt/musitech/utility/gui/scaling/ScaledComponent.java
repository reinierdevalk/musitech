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
 * Created on 09.04.2004
 */
package de.uos.fmt.musitech.utility.gui.scaling;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.utility.DebugState;

/**
 * A decorator for JComponents which supports scaling: a given JComponent is painted
 * according to a specific zoom-factor.
 * <break> 
 * <break> The original component (<code>comp</code>) is in the background of the layeredPane. 
 * A panel in the front (<code>frontpanel</code>) provides the painting. 
 * Thereby key events are forwarded automatically. Mouse events are forwarded explicitly.
 * <break>
 * <break> A KeyListener, a FocusListener and - in the case of a JButton - a ChangeListener
 * look for repainting if the component in the background gets a respective event.
 * <break> Additionally there is a static thread (<code>repaintingThread</code>) 
 * of this class which repaints in a given frequency (<code>repaintingTime</code>).
 * You can manipulate some parameters of this repainting, e. g. change the frequency 
 * or remove specific instances from the set of repainted instances. 
 * 
 * @author Tobias Widdra
 */
public class ScaledComponent extends JLayeredPane {

	/**
	 * The component which is to be scaled.
	 */
	JComponent comp;
	
	/**
	 * A panel which provides the painting of the scaled component.
	 */
	JPanel frontpanel;
	
	/**
	 * Constructs a new decorator for <code>component</code> with a default
	 * zoom-factor of 1 (i. e. original size).
	 * @param component
	 */
	public ScaledComponent(JComponent component) {
		this(component, 1);
	}
	
	/**
	 * Constructs a new decorator for <code>component</code> with the zoom-facor
	 * <code>zoom</code>. 
	 * @param component to be scaled
	 */
	public ScaledComponent(JComponent component, double z) {
		this.comp = component;
		this.zoom = z;
		
		// add comp in the background
		add(comp,DEFAULT_LAYER);

		// neccessary!
		comp.setSize(comp.getPreferredSize());
		comp.setDoubleBuffered(false);
		setDoubleBuffered(false);
		
		// neccessary?
		comp.revalidate();
		
		// create frontpanel which paints the scaled comp) and add it to the palette-layer.
		frontpanel = new JPanel(){
			public void paint(Graphics g) {
//				super.paint(g);
				g.clearRect(0,0,getWidth(),getHeight());
				vWidth = comp.getWidth();
				vHeight = comp.getHeight();
				g.setClip(0,0,getWidth(),getHeight());
				createOffscreenImage();
//				comp.doLayout();
				comp.paint(offscreenGraphics);

//				// enable antialiasing for all visible score components
				((Graphics2D) g).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
				
				g.drawImage(offscreenBuffer, 0,0,(int) (zoom*vWidth),(int) (zoom*vHeight),this);
			}
		};
		frontpanel.setOpaque(true);
		frontpanel.setDoubleBuffered(false);
		add(frontpanel,PALETTE_LAYER);

		/* care for repainting: register at painting thread and 
		 * add KeyListener to all focusable children of comp
		 */
		addRepaintingListenerToChildren(comp);
		getRepaintingThread().addRepaintingClient(this);
		addComponentListener(new RegisterListener());
		
		// redispatch mouse events
		MouseEventRedispatcher m = new MouseEventRedispatcher();
		frontpanel.addMouseListener(m);		
		frontpanel.addMouseMotionListener(m);
		
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				frontpanel.setSize(getSize());
			}
		});
	}


// ----------------- create graphics, overrided size methods ----------	

	BufferedImage offscreenBuffer;
	Graphics offscreenGraphics;
	int vWidth = 0;
	int vHeight = 0;
	
	/**
	 * Zoom factor.
	 */
	double zoom = 1;

	/**
	 * Sets the size of frontpanel to this.getSize() and calls super.paint().
	 */
	public void paint(Graphics g) {
//		g.clearRect(0,0,getWidth(),getHeight());
		frontpanel.paint(g);
	}	
	
	/**
	 * Creates offscreen-image of the component <code>comp</code>.
	 */
	void createOffscreenImage() {
		vWidth = comp.getSize().width;
		vHeight = comp.getSize().height;
		if (offscreenBuffer == null
			|| offscreenBuffer.getWidth() != vWidth
			|| offscreenBuffer.getHeight() != vHeight) {
			offscreenBuffer = (BufferedImage) createImage(vWidth, vHeight);
			offscreenGraphics = offscreenBuffer.createGraphics();
		}
	}

//	-------------------- getter --------------	

	
	/**
	 * Updates <code>vWidth</code> and <code>vHeight</code> 
	 * and returns the scaled preferred size.
	 * @return scaled preferred size of comp
	 */
	public Dimension getPreferredSize() {
		vWidth = (int)(comp.getPreferredSize().width * zoom);
		vHeight = (int)(comp.getPreferredSize().height * zoom);
		return new Dimension(vWidth,vHeight);
	}
	
	JPanel getFrontpanel() {
		return frontpanel;
	}
	
	JComponent getComp() {
		return comp;
	}
	
	public double getZoom() {
		return zoom;
	}
	
	public void setZoom(double z) {
		zoom = z;
	}
	
	
//------------- mouseevents ----------------

	/**
	 * Forwards the mouse- and mouse-motion-events from <code>frontpanel</code> 
	 * to <code>comp</code>.
	 */
	class MouseEventRedispatcher implements MouseListener, MouseMotionListener {

		public void mouseClicked(MouseEvent e) {
			redispatchMouseEvent(e);
		}

		/**
		 * Forward event to <code>comp</code>.
		 */
		public void mousePressed(MouseEvent e) {
			redispatchMouseEvent(e);
			frontpanel.repaint();						
		}

		/**
		 * Forward event to <code>comp</code>.
		 */
		public void mouseReleased(MouseEvent e) {
			redispatchMouseEvent(e);
			frontpanel.repaint();		
		}

		/**
		 * Forward event to <code>comp</code>.
		 */
		public void mouseEntered(MouseEvent e) {
			redispatchMouseEvent(e);
			frontpanel.repaint();		
		}

		/**
		 * Forward event to <code>comp</code>.
		 */
		public void mouseExited(MouseEvent e) {
			redispatchMouseEvent(e);
			frontpanel.repaint();			
		}

		/**
		 * Forward event to <code>comp</code>.
		 */
		public void mouseDragged(MouseEvent e) {
			redispatchMouseEvent(e);
			frontpanel.repaint();		
		}

		/**
		 * Forward event to <code>comp</code>.
		 */
		public void mouseMoved(MouseEvent e) {
			redispatchMouseEvent(e);			
		}
		
		/**
		 * This method does the forwarding to <code>comp</code>. It is invoked
		 * by the event methods.
		 */
		private void redispatchMouseEvent(MouseEvent e) {

			// convert point relative to comp
			if (zoom == 0) return;
			
			int x = (int) (e.getPoint().x / zoom);
			int y = (int) (e.getPoint().y / zoom);
			
			// find component
			Component c = SwingUtilities.getDeepestComponentAt(comp,x,y);
			if (c == null) return;

			// convert point relative to component which receives the event (c)
			Point p = SwingUtilities.convertPoint(comp,x,y,c);

			// dispatch new event
			c.dispatchEvent(
				new MouseEvent(
					c,
					e.getID(),
					e.getWhen(),
					e.getModifiers(),
					p.x,
					p.y,
					e.getClickCount(),
					e.isPopupTrigger()));
		}
	}


//	-------------------- repainting thread / repainting time ------------------

	/**
	 * Time of repainting rate of the <code>repaintingThread</code> in milliseconds.
	 * <break> The default is set to 100.
	 */
	static int repaintingTime = 100;
	
	 /**
	  * The static singleton-instance of the repainting thread.
	  * <break> Cares for repainting of all registered ScaledComponents.
	  */
	 private static RepaintingThread repaintingThread;

	 /**
	  * Repaints registered clients (ScaledComponents) in the 
	  * frequence of <code>repaintingTime</code>.
	  * <break> ScaledComponents register in their constructor. 
	  * They can remove themselves and reregister again.
	  * <break>
	  * <break> This class is intended to have a singleton-instance.
	  */
	 private static class RepaintingThread extends Thread {
		
		/**
		 * Vector of clients to repaint.
		 */
		private Set clients = new  HashSet();

		/**
		 * The thread runs while <code>running</code> is true.
		 */		
		private boolean running = true;
		
		void setRunning(boolean b) {
			running = b;
		}
		
		boolean isRunning() {
			return running;
		}
		 /**
		  * Repaint all registered clients every <code>repaintingTime</code> milliseconds.
		  * Runs while <code>running</code> is true. 
		  */
		 public void run(){
			 while(running){
			 	 if ((clients == null)||(clients.size() == 0)) {
			 	 	synchronized(this) {
			 	 		try {
							this.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
			 	 	}
			 	 }
				 for (Iterator iter = clients.iterator(); iter.hasNext();) {
					 ((ScaledComponent) iter.next()).repaint();
				 }
				 try {
					 Thread.sleep(repaintingTime);
				 } catch (InterruptedException e) {
					 // TODO painting thread: what to do with InterruptedException?
					 e.printStackTrace();
				 }
			 }
		 }

		/**
		 * Adds c to the list of repainted components.
		 * @param c
		 */		
		 void addRepaintingClient(ScaledComponent c) {
			 clients.add(c);
		 	 synchronized(this) {
		 	 	notify();
		 	 }
		 }

		 // TODO: remove "dead" components? in finalize()? check if no more other references?
		 /**
		  * Removes c from the list of repainted components.
		  */		
		 void removeRepaintingClient(ScaledComponent c) {
			 clients.remove(c);
		 }
	 }

	 /**
	  * Method to get the singleton-instance of the <code>repaintingThread</code>. 
	  */
	 static RepaintingThread getRepaintingThread() {
		 if (repaintingThread == null) {
			 repaintingThread = new RepaintingThread();
			 repaintingThread.start();	
		 } 
		 return repaintingThread;	
	 }
	 
	 /**
	  * Setter for repaintingTime.
	  * <break>
	  * <break> If t is negative the repaintingthread gets stopped.
	  * @param t
	  */
	 public void setRepaintingTime(int t) {
	 	
//	 	//if negative value stop thread
//	 	if (t < 0) {
//	 		getRepaintingThread().setRunning(false);
//	 		return;
//	 	}
//	 	
//	 	// TODOD: start thread again!
//	 	// if thread is not running start it
//	 	if (!getRepaintingThread().isRunning()) {
////	 		getRepaintingThread().start();
//	 	}
	 	
	 	// set repaintingtime
	 	repaintingTime = t;
	 }
	 
	 /**
	  * Getter for repaintingTime.
	  */
	 public int getRepaintingTime() {
	 	return repaintingTime;
	 }

	/**
	 * Removes this instance from the set of by the thread repainted instances.
	 *
	 */	
	public void removeFromRepaintingThread() {
		getRepaintingThread().removeRepaintingClient(this);
	}
	
	/**
	 * Adds this instance to the set of by the thread repainted instances.
	 * <break>
	 * <break> This is done by default in the constructor but might be useful
	 * if vemoveFromRepaintingThread() is used.
	 *
	 */	
	public void addToRepaintingThread() {
		getRepaintingThread().addRepaintingClient(this);
	}

//------------- methods/classes for event-related repainting ---------

	/**
	 * Adds Listeners for repainting to all children recursively.
	 * <break>
	 * <break> Warning: This method is designed for JPanels only. If a child
	 * is an instance of e. g. java.awt.Component it is ignored.
	 */
	private void addRepaintingListenerToChildren(Container c) {

		Component[] children = c.getComponents();
		
		// if comp is focusable add listeners
		if ((children == null)||(children.length == 0)) {
			if (c.isFocusable()) {
				c.addKeyListener(new KeyPainter());
				c.addFocusListener(new FocusPainter());
				
				// add changelistener for some components... 
				// might be interesting for other components as well...
				if (c instanceof JButton) {
					((JButton)c).addChangeListener(new ChangePainter());
				}
			}
			return;
		}

		// recursion for all children
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Container)
				addRepaintingListenerToChildren((Container) children[i]);
			else if (DebugState.DEBUG) System.err.println("Warning in ScaledComponent: Not a JComponent");
		}

//		// old non-recursive version (seemd to work, but not extensivly tested)
//		if (children != null) 
//			for (int i = 0; i < children.length; i++) {
//				if (children[i].isFocusable()) {
//					children[i].addKeyListener(new KeyPainter());
//					children[i].addFocusListener(new FocusPainter());
//				}
//			}
	}

	/**
	 * Invokes <code>frontpanel.repaint()</code> when key pressed or released.
	 */	
	class KeyPainter implements KeyListener {
		
		public void keyPressed(KeyEvent e) {
			frontpanel.repaint();
		}

		/**
		 * Empty.
		 */
		public void keyTyped(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
			frontpanel.repaint();
		}
	}
	
	/**
	 * Invokes <code>frontpanel.repaint()</code> when focus gained or lost.
	 */
	class FocusPainter implements FocusListener {

		public void focusGained(FocusEvent e) {		
			frontpanel.repaint();
		}

		public void focusLost(FocusEvent e) {
			frontpanel.repaint();
		} 
	}

	/**
	 * Invokes <code>frontpanel.repaint()</code> when changeEvent fired.
	 */	
	class ChangePainter implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
//			System.out.println("repaint in changeListener");
			frontpanel.repaint();			
		}
	}

	class RegisterListener extends ComponentAdapter {
    
		/**
		 * Invoked when the component has been made visible.
		 */
		public void componentShown(ComponentEvent e) {
			addToRepaintingThread();
		}
    
		/**
		 * Invoked when the component has been made invisible.
		 */
		public void componentHidden(ComponentEvent e) {
			removeFromRepaintingThread();
		}
		
	}
}