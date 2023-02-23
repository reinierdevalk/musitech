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
 * File GlassPaneMouseListener.java
 * Created on 17.02.2004
 */

package de.uos.fmt.musitech.utility.gui.interact;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Mouse listener for usage in glass panes to
 * selectively redispatch events ( not completely finished ).  
 * @author tweyde
 */
// TODO check if MenuBar events are treated correctly
// 		ar could be treated like other components
// TODO write GlassPaneKeyListener
public class GlassPaneMouseListener
	extends MouseMotionAdapter
	implements MouseListener {

	Toolkit toolkit;
	JMenuBar menuBar;
	Container contentPane;
	Container topLevel;
	Component glassPane;
	boolean inDrag = false;
	Collection specialTargets;
	MouseListener specialMouseListener;
	MouseMotionListener specialMouseMotionListener;

	Component oldTarget;
	boolean oldTargetIsSpecial;

	/**
	 * Constructs a MouseListener for the glass pane of <code>topLevel</code>. 
	 * <code>topLevel</code> must be either a JFrame, a JDialog, or a JApplet,
	 * else the behaviour is undefined and it will probably throw lots of null-
	 * pointer exceptions.  
	 * @param topLevel The top-level container to use.
	 */
	public GlassPaneMouseListener(Container topLevelContainer) {
		topLevel = topLevelContainer;
		toolkit = Toolkit.getDefaultToolkit();
		if (topLevel instanceof JFrame) {
			JFrame frame = (JFrame) topLevel;
			this.menuBar = ((JFrame) topLevel).getJMenuBar();
			this.glassPane = frame.getGlassPane();
			this.contentPane = frame.getContentPane();
		}
		if (topLevel instanceof JDialog) {
			JDialog dialog = (JDialog) topLevel;
			this.menuBar = ((JFrame) topLevel).getJMenuBar();
			this.glassPane = dialog.getGlassPane();
			this.contentPane = dialog.getContentPane();
		}
		if (topLevel instanceof JApplet) {
			JApplet applet = (JApplet) topLevel;
			this.menuBar = ((JFrame) topLevel).getJMenuBar();
			this.glassPane = applet.getGlassPane();
			this.contentPane = applet.getContentPane();
		} else {
			System.out.println(
				this.getClass()
					+ "Error in Constuctor: type "
					+ topLevel.getClass()
					+ " is not a known topLevel Container.");
		}
	}

	/**
	 * Test if the given target component is one of the special
	 * targets or their sub-components. 
	 * @param target The component to test. 
	 * @return the specialTarget if target is one of the special targets or 
	 * 				a sub-component of one, else it is false.  
	 */
	public Component testForSpecialTarget(Component target) { // Test if component is a special target
		if (target == null || specialTargets == null)
			return null;
		for (Iterator iter = specialTargets.iterator(); iter.hasNext();) {
			try {
				JComponent stComponent = (JComponent) iter.next();
				if (SwingUtilities.isDescendingFrom(target, stComponent))
					return stComponent;
			} catch (Exception e) {
				//				e.printStackTrace();
				iter = specialTargets.iterator();
				if (iter != null)
					continue;
				else
					break;
			}
		}
		return null;
	}

	// MouseMotion methods
	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		//System.out.println(e);
		Component target = findComponent(e);
		if (target == null)
			return;
		Component specialTarget = testForSpecialTarget(target);
		if (specialTarget != null) {
			if (specialMouseMotionListener != null)
				specialMouseMotionListener.mouseMoved(
					changeSource(e, specialTarget));
		} else {
			redispatchMouseEvent(e, target);
		}
		if (specialTarget != null) {
			target = specialTarget;
		}
		// if there is a target and it has changed 
		if (target != null && oldTarget != target) {
			// if there is an old target send mouse exit
			if (oldTarget != null) {
				MouseEvent me =
					createMouseEvent(e, oldTarget, MouseEvent.MOUSE_EXITED);
				if (oldTargetIsSpecial) {
					if (specialMouseListener != null)
						specialMouseListener.mouseExited(me);
				} else {
					oldTarget.dispatchEvent(me);
				}
			}
			// send mouse entered
			MouseEvent me =
				createMouseEvent(e, target, MouseEvent.MOUSE_ENTERED);
			if (specialTarget != null) {
				if (specialMouseListener != null)
					specialMouseListener.mouseEntered(me);
				oldTargetIsSpecial = true;
			} else {
				target.dispatchEvent(me);
				oldTargetIsSpecial = false;
			}
			oldTarget = target;
		}
	}

	/*
	 * We must forward at least the mouse drags that started
	 * with mouse presses over the check box.  Otherwise,
	 * when the user presses the check box then drags off,
	 * the check box isn't disarmed -- it keeps its dark
	 * gray background or whatever its L&F uses to indicate
	 * that the button is currently being pressed.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		Component target;
		if (inDrag) {
			target = lastDragStartComp;
			//			System.out.println("in drag");
		} else {
			//			System.out.println("no drag");
			target = findComponent(e);
		}
		target = lastDragStartComp;
		if (target == null)
			return;
		Component specialTarget = testForSpecialTarget(target);
		if (specialTarget != null) {
			if (specialMouseMotionListener != null) {
				specialMouseMotionListener.mouseDragged(
					changeSource(e, specialTarget));
			}
		} else
			redispatchMouseEvent(e, specialTarget);
	}

	// Mouse methods
	@Override
	public void mouseClicked(MouseEvent e) {
		Component target = findComponent(e);
		if (target == null)
			return;
		Component specialTarget = testForSpecialTarget(target);
		if (specialTarget != null) {
			if (specialMouseListener != null)
				specialMouseListener.mouseClicked(
					changeSource(e, specialTarget));
		} else
			redispatchMouseEvent(e, target);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Component target = findComponent(e);
		if (target == null)
			return;
		Component specialTarget = testForSpecialTarget(target);
		if (specialTarget != null) {
			if (specialMouseListener != null)
				specialMouseListener.mouseEntered(
					changeSource(e, specialTarget));
		} else
			redispatchMouseEvent(e, specialTarget);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Component target = findComponent(e);
		if (target != null) {
			Component specialTarget = testForSpecialTarget(target);
			if (specialTarget != null) {
				if (specialMouseListener != null)
					specialMouseListener.mouseExited(
						changeSource(e, specialTarget));
			} else
				redispatchMouseEvent(e, target);
		}
		if (oldTarget != null) {
			e = changeSource(e, oldTarget);
			if (oldTargetIsSpecial) {
				if (specialMouseListener != null)
					specialMouseListener.mouseExited(e);
			} else
				oldTarget.dispatchEvent(e);
			oldTarget = null;
		}

	}

	Component lastDragStartComp;
	@Override
	public void mousePressed(MouseEvent e) {
		lastDragStartComp = findComponent(e);
		if (lastDragStartComp == null)
			return;
		Component specialTarget = testForSpecialTarget(lastDragStartComp);
		if (specialTarget != null)
			lastDragStartComp = specialTarget;
		if (specialTarget != null) {
			if (specialMouseListener != null)
				specialMouseListener.mousePressed(
					changeSource(e, specialTarget));
		} else
			redispatchMouseEvent(e, lastDragStartComp);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Component target;
		if (inDrag && lastDragStartComp != null)
			target = lastDragStartComp;
		else
			target = findComponent(e);
		Component specialTarget = testForSpecialTarget(target);
		if (specialTarget != null) {
			if (specialMouseListener != null)
				specialMouseListener.mouseReleased(
					changeSource(e, specialTarget));
		} else
			redispatchMouseEvent(e, target);
		inDrag = false;
	}

	boolean inButton = false;
	boolean inMenuBar = false;
	Point glassPanePoint;
	Point containerPoint;
	//	Component component;

	/**
	 * FindComponent finds the component under the glass pane,
	 * which is visible at the event postion and should get the 
	 * event in case of regular processing. 
	 * @param e The MouseEvent to evealuate
	 * @return The compontent 
	 */
	private Component findComponent(MouseEvent e) {
		glassPanePoint = e.getPoint();
		Component component = null;
		Container container = contentPane.getParent();
		if (container instanceof JLayeredPane) {
			Component targetComp = null;
			JLayeredPane layered = (JLayeredPane) container;
			List compList = testComponentsrecursive(e, container, null);
			int maxLayer = Integer.MIN_VALUE;
			if (compList != null) {
				for (Iterator iter = compList.iterator(); iter.hasNext();) {
					Component comp = (Component) iter.next();
					if (layered.getLayer(comp) > maxLayer
						&& comp != glassPane
						&& comp != layered) {
						targetComp = comp;
						maxLayer = layered.getLayer(comp);
					}
				}
			}
			return targetComp;
		} else {
			System.out.println("Component.getParent() is not a JLayeredPane");
		}
		if (e.getSource() instanceof Component) {
			containerPoint =
				SwingUtilities.convertPoint(
					(Component) e.getSource(),
					e.getPoint(),
					container);
		} else {
			System.out.println(
				"MouseEvent source is not a container in this.getClass");
			containerPoint =
				SwingUtilities.convertPoint(
					glassPane,
					glassPanePoint,
					container);
		}
		component =
			SwingUtilities.getDeepestComponentAt(
				container,
				containerPoint.x,
				containerPoint.y);
		if (menuBar != null) {
			//			inMenuBar = true;
			Component menuComp = isComponentHit(e, menuBar);
			if (menuComp != null) {
				component = menuComp;
			}
			//			testForDrag(e.getID()); // TODO is this correct ??
		}
		//XXX: If the event is from a component in a popped-up menu,
		//XXX: then the container should probably be the menu's
		//XXX: JPopupMenu, and containerPoint should be adjusted
		//XXX: accordingly.
		// XXX this could perhaps be done if 
		return component;
	}

	private List testComponentsrecursive(
		MouseEvent e,
		Container c,
		List list) {
		Point compPoint =
			SwingUtilities.convertPoint(
				(Component) e.getSource(),
				e.getPoint(),
				c);
		Component comp =
			SwingUtilities.getDeepestComponentAt(
				c,
				compPoint.x,
				compPoint.y);
		if (comp != null) {
			if (list == null) {
				list = new ArrayList();
			}
			if(!list.contains(comp)){
				list.add(comp);
			}
		}
		Container cont = c;
		Component children[] = cont.getComponents();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Container) {
				testComponentsrecursive(e, (Container) children[i], list);
			}
		}
		return list;
	}

	private Component isComponentHit(MouseEvent e, Component comp) {
		Point compPoint =
			SwingUtilities.convertPoint(
				(Component) e.getSource(),
				e.getPoint(),
				comp);
		comp =
			SwingUtilities.getDeepestComponentAt(
				comp,
				compPoint.x,
				compPoint.y);
		return comp;
	}

	private void redispatchMouseEvent(MouseEvent e, Component component) {
		if (component == null)
			return;
		Point componentPoint = e.getPoint();
		if (component != e.getSource())
			componentPoint =
				SwingUtilities.convertPoint(
					(Component) e.getSource(),
					componentPoint,
					component);
		component.dispatchEvent(
			new MouseEvent(
				component,
				e.getID(),
				e.getWhen(),
				e.getModifiers(),
				componentPoint.x,
				componentPoint.y,
				e.getClickCount(),
				e.isPopupTrigger()));
	}

	private void testForDrag(int eventID) {
		if (eventID == MouseEvent.MOUSE_PRESSED) {
			inDrag = true;
		}
	}

	/**
	 * @return
	 */
	public MouseListener getSpecialMouseListener() {
		return specialMouseListener;
	}

	/**
	* @return
	*/
	public MouseMotionListener getSpecialMouseMotionListener() {
		return specialMouseMotionListener;
	}

	/**
	* @return
	*/
	public Collection getSpecialTargets() {
		return specialTargets;
	}

	/**
	 * @param listener
	 */
	public void setSpecialMouseListener(MouseListener listener) {
		specialMouseListener = listener;
	}

	/**
	 * @param listener
	 */
	public void setSpecialMouseMotionListener(MouseMotionListener listener) {
		specialMouseMotionListener = listener;
	}

	/**
	 * @param collection
	 */
	public void setSpecialTargets(Collection collection) {
		specialTargets = collection;
	}

	/**
	 * @return
	 */
	public Component getGlassPane() {
		return glassPane;
	}

	/**
	 * Creates new new MouseEvent, that is a copy of the old one, but 
	 * with the new type/ID (one of the constants from MouseEvent, e.g. 
	 * MOUSE_EXITED) and source set and coordinates adapted. 
	 * @param me The MouseEvent
	 * @param newSource The new source
	 * @param type The new Type
	 * @return The newly constructed MouseEvent.
	 * @throws IllegalArgumentException if the MouseEvent or the newSource are null. 
	 */
	public static MouseEvent createMouseEvent(
		MouseEvent me,
		Component newSource,
		int type)
		throws IllegalArgumentException {
		if (me == null || newSource == null)
			throw new IllegalArgumentException("error: mouseEvent and source must not be null !");
		MouseEvent newME =
			new MouseEvent(
				(Component) me.getSource(),
				type,
				me.getWhen(),
				me.getModifiers(),
				me.getX(),
				me.getY(),
				me.getClickCount(),
				me.isPopupTrigger());
		return changeSource(newME, newSource);
	}

	/**
	 * Changes the source of a mouse event adjusting the coordinates.
	 * The new source must not be null and the mouse event's source 
	 * must be a Component. 
	 * @param me The mouse event.
	 * @param newSource The new source component.
	 * @return the passed mouse event with new values set.
	 * @throws IllegalArgumentException if the MouseEvent is null.
	 */
	public static MouseEvent changeSource(MouseEvent me, Component newSource)
		throws IllegalArgumentException {
		if (newSource == null)
			throw new IllegalArgumentException("error: new source of mouse event must not be null !");
		Point p = me.getPoint();
		Component oldSource;
		try {
			oldSource = (Component) me.getSource();
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("error: source of mouse event is not a component !");
		}
		p = SwingUtilities.convertPoint(oldSource, p, newSource);
		me.setSource(newSource);
		me.getPoint().x = p.x;
		me.getPoint().y = p.y;
		return me;
	}

	/** Just for testing
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame("GlassPaneMouseListenerTest");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JButton button1 = new JButton("Button 1");
		JButton button2 = new JButton("Button 2");
		frame.getContentPane().setLayout(new GridLayout(3, 1));
		frame.getContentPane().add(button1);
		frame.getContentPane().add(button2);
		final JTextField textField = new JTextField();
		frame.getContentPane().add(textField);
		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				textField.setText("normal action listener");
			}
		});
		GlassPaneMouseListener gpml = new GlassPaneMouseListener(frame);
		Collection specialTargets = new ArrayList();
		specialTargets.add(button2);
		gpml.setSpecialTargets(specialTargets);
		gpml.setSpecialMouseListener(new MouseAdapter() {
			/**
			* @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			*/
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				textField.setText("glass pane mouse listener");
			}

		});
		frame.getGlassPane().addMouseListener(gpml);
		frame.getGlassPane().setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.pack();
				frame.show();
			}
		});
	}
}
