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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.TimedContainer;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.change.IDataChangeManager;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.selection.SelectingEditor;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionController;
import de.uos.fmt.musitech.framework.selection.SelectionListener;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.time.TimeRange;

/**
 * This Class is used to Display the contens of a container. This class is used
 * in ArrangementPanel. Use this for having a timeScale, ScrollBar and zoom
 * function
 * 
 * @author Jan
 *  
 */
public class ContainerArrangeDisplay extends JPanel implements Display,
		SelectionListener, SelectingEditor {
	// TODO: Display raus, dafür in ArrangementPanel
	double microsPerPix = 50;

	long displayOffset = 0;

	Box box;

	Color highlightColor = new Color(50, 50, 50), defaultColor = new Color(210,
			210, 210);

	boolean highlighted;

	SelectionController selectionController;

	// used for Display
	boolean dataChanged = false;

	Object editObj;

	EditingProfile editProfile;

	/**
	 * 
	 * @uml.property name="label"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	JLabel label = new JLabel();

	/**
	 * 
	 * @uml.property name="containers"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	Container container;

	/**
	 * 
	 * @uml.property name="partPanels"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="de.uos.fmt.musitech.image.arrange.PartPanel"
	 */
	List partPanels = new ArrayList();

	/**
	 * 
	 * @uml.property name="mouseListener"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	MouseInputListener mouseListener = new MouseInputListener() {
		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON3) {
				Editor thisEditor;
				try {
					thisEditor = EditorFactory.createEditor(container);
				} catch (EditorConstructionException e1) {
					e1.printStackTrace();
					return;
				}
				makeframe((JComponent) thisEditor);
			}
			if (e.getButton() == MouseEvent.BUTTON1) {
				JPopupMenu popup = new JPopupMenu();
				JLabel label = new JLabel(" open '" + container.getName()
						+ "' as");
				popup.add(label);
				EditingProfile editprof = EditorFactory
						.getOrCreateProfile(container);
				final String[] editor = editprof.getEditortypes();
				for (int i = 0; i < editor.length; i++) {
					final String edit = editor[i];
					ActionListener actionList = new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							System.out.println("clicked ");
							Display thisEditor;
							//Editor thisEditor;
							try {
								thisEditor = EditorFactory.createDisplay(
										container, null, edit, null);
								//ergänzt 30/11/04 (K.N.)
								if (thisEditor instanceof NotationDisplay){
								    ((NotationDisplay)thisEditor).setAutoZoom(false);
								}
							} catch (EditorConstructionException e1) {
								e1.printStackTrace();
								return;
							}
							makeframe((JComponent) thisEditor);
						}
					};

					JMenuItem item = new JMenuItem(editor[i]);
					item.addActionListener(actionList);
					popup.add(item);
				}
				popup.show(e.getComponent(), e.getX(), e.getY());

			}

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
		}
	};

	public void makeframe(JComponent comp) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		//Änderung 30/11/04 (K.N.)
//		frame.getContentPane().add(comp);
		if (comp instanceof Display){
		    if (((Display)comp).getEditObj() instanceof Named) {
		        if (((Named)((Display)comp).getEditObj()).getName()!=null){
		            frame.setTitle(((Named)((Display)comp).getEditObj()).getName());
		        }
		    }
		}
		JScrollPane scroll = new JScrollPane(comp);
		frame.getContentPane().add(scroll);
		//Ende Änderung
		frame.setLocation(499, 150);
		frame.pack();
		frame.setSize(500, 300);
		frame.show();
	}

	public ContainerArrangeDisplay(Container cont) {
		this();
		setContainer(cont);
		SelectionManager selMan = SelectionManager.getManager();
		selMan.addListener(this);
		IDataChangeManager datMan = DataChangeManager.getInstance();
		datMan.interestExpandElements(this, cont);
	}

	/**
	 * Calculates the prefferedStart and PrefferedSize in Pix
	 */
	protected void calcBounds() {
		if (container != null) {

			if (container.getTime() + container.getDuration() < winBegin) {
//				System.out.println(container.getName() + ": getTime: "
//						+ container.getTime() + " length: "
//						+ container.getDuration() + " = "
//						+ (container.getTime() + container.getDuration())
//						+ " win begin: " + winBegin);
				preferredStart = 0;
				preferredSize.width = 0;
			} else {

				preferredStart = (int) (container.getTime() / getMicrosPerPix());
				preferredSize.width = (int) (container.getDuration() / getMicrosPerPix());
			}
		} else {
			preferredStart = 0;
			preferredSize = new Dimension(0, 100);
		}
	}

	/**
	 * This class is used to display the contens of a container
	 */
	public ContainerArrangeDisplay() {
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		setLayout(new BorderLayout());
		add(label, BorderLayout.NORTH);
		setBorder(new LineBorder(Color.BLACK, 1));
		box = new Box(BoxLayout.Y_AXIS);
		addComponentListener(getCompList());
		SelectionManager.getManager().addListener(this);
		setSelectionController(new SelectionController(this));
	}

	public void setColor(Color color) {
		setBackground(color);
	}

	/**
	 * @return
	 * @uml.property name="containers"
	 */
	public Container getContainer() {
		return container;
	}

	int preferredStart = 0;

	Dimension preferredSize = new Dimension(0, 25);

	/**
	 * @param containers
	 * 
	 * @uml.property name="containers"
	 */
	public void setContainer(Container container) {
		System.out.println("CAD: setContainer: " + container.getName());
		this.container = container;
		chooseColor(true);
		createGUI();
		DataChangeManager.getInstance().interestExpandObject(this, container);
        DataChangeManager.getInstance().interestExpandElements(this, container);
	}

	/**
	 * ChooseColor in following order: 1. Use renderings hints of container 2.
	 * Use rendering hints of father container 3. Use defaultColor
	 *  
	 */
	private void chooseColor(boolean setCont) {
		if (container.getRenderingHints() != null && setCont) {
			Object colorObj = container.getRenderingHints().getValue("color");
			// use color of rendering hits if possible

			if (colorObj != null) {
				if (((String) colorObj).length() == 6) {
					Color color = new Color(Integer.parseInt((String) colorObj,
							16));

					setDefaultColor(color);
				}
			}
		} 
		// renderingHints == null
		else {
			boolean foundParent = true;
//			System.out.println("'" + container.getName() + "' searches for color ");
			if (parentCAD != null){
				Color color = parentCAD.getBackground();
				color = color.darker();
				setDefaultColor(color);
			}
			else{
				setDefaultColor(getDefaultColor());
			}
			
//			while (foundParent) {
//				if (parentCAD != null) {
//					System.out.println(" ** has parent: " + parentCAD.container.getName());
//					if (parentCAD.container.getRenderingHints() != null) {
//						Object colorObj = parentCAD.container
//								.getRenderingHints().getValue("color");
//						if (((String) colorObj).length() == 6) {
//							Color color = new Color(Integer.parseInt(
//									(String) colorObj, 16));
//							color = color.darker();
//							setDefaultColor(color);
//							foundParent = false;
//
//						}
//					} else {
//						if (parentCAD.parentCAD != null) {
//							parentCAD = parentCAD.parentCAD;
//						} else {
//							foundParent = false;
//						}
//					}
//				}
//				else{
//					// no parent founf
//					foundParent = false;
//				}
//			}

		}

		if (getDefaultColor() == null) {
			
		}
		setBackground(getDefaultColor());
		revalidate();
		repaint();
	}

	/**
	 * finishes the Layout by setting the label text and renewing the box with
	 * the partPanels.
	 */
	private void finishLayout() {
		//		SwingUtilities.invokeLater(new Thread() {
		//			public void run() {
		if (container != null) {
			if (container.getName() != null)
		    label.setText(container.getName());
			else label.setText("kein Name");
			
			
		} else {
			label.setText("kein Container");

		}

		for (Iterator iter = partPanels.iterator(); iter.hasNext();) {
			PartPanel pp = (PartPanel) iter.next();
			box.add(pp);
		}
		add(box, BorderLayout.CENTER);

		int height = label.getPreferredSize().height;
		for (Iterator iter = partPanels.iterator(); iter.hasNext();) {
			PartPanel pp = (PartPanel) iter.next();
			height += pp.getPreferredSize().height;

		}
		height += getBorder().getBorderInsets(this).top
				+ getBorder().getBorderInsets(this).bottom;
		preferredSize.height = height + 5;

	}

	long winBegin;

	long winDuration;

	/**
	 * Changes the Window of all containing containers This is needed to
	 * calculate bounds in ArrangementPanel
	 * 
	 * @param cont
	 * @param begin
	 * @param width
	 */
	public void setWindowRecursive(java.awt.Container cont, long begin,
			long width) {
		Component[] components = cont.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof java.awt.Container) {
				setWindowRecursive((java.awt.Container) components[i], begin,
						width);
			}
		}
		if (cont instanceof ContainerArrangeDisplay) {
			((ContainerArrangeDisplay) cont).setWindow(begin, width);
		}

	}

	public void setWindow(long begin, long duration) {
		winBegin = begin;
		winDuration = duration;

		layoutChanged();
	}

	/**
	 * @see java.awt.Component#getPreferredSize()
	 * 
	 * @uml.property name="preferredSize"
	 */
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	ComponentListener compList = null;

	private ComponentListener getCompList() {
		if (compList == null) {
			compList = new ComponentListener() {
				public void componentResized(ComponentEvent arg0) {
					//					System.out.println("CAD resize" + arg0.getSource());

					//					calculateMPP();
					//					updateDisplay();

				}

				public void componentMoved(ComponentEvent arg0) {
//					System.out.println("CAD moved");

				}

				public void componentShown(ComponentEvent arg0) {
//					System.out.println("CAD shown and calcMPP");
					//					calculateMPP();

				}

				public void componentHidden(ComponentEvent arg0) {
					System.out.println("CAD hidden");

				}
			};
		}
		return compList;
	}

	private void calculateMPP() {
		setWinBegin(0);
		// to display all contents in window

		long defWinDur = container.getDuration();
//		System.out.print("calcMPP: winWidth: " + getWidth()
//				+ ", Dur: defWinDur" + defWinDur + ", newMPP: ");
		setWinDuration(defWinDur);
		if (getWidth() != 0) {

			setMicrosPerPix(defWinDur / getWidth());
		} else {

			setMicrosPerPix(30000);
		}
	}

	/**
	 * Put any nested containers into the partPanels, and create the partPanels
	 * if necessary.
	 */
	public void makeParts() {

		TimedContainer tc = new TimedContainer();
		// to get correct timeorder
		for (Iterator iter = container.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (!(obj instanceof Container)) {
				continue;
			}
			// obj is a container
			Container cont = (Container) obj;
			tc.add(cont);
		}
		partPanels.clear();
		containerLoop: for (Iterator iter = tc.iterator(); iter.hasNext();) {
			Container cont = (Container) iter.next();
			//			// see if the containers is already in one of the parts.
			//			partLoop1 : for (
			//				Iterator iterator = partPanels.iterator();
			//					iterator.hasNext();
			//				) { // start partloop1
			//				PartPanel pp = (PartPanel) iterator.next();
			//				Component comps[] = pp.getComponents();
			//				innerContLoop: for (int j = 0; j < comps.length; j++) {
			//					if(!(comps[j] instanceof ContainerArrangeDisplay))
			//						continue;
			//					ContainerArrangeDisplay cad = (ContainerArrangeDisplay) comps[j];
			//					if(cad.getContainer() == cont)
			//						break containerLoop; // containers is already in a partpanel
			//				} // end innerContLoop
			//			} // end partLoop1
			// see if the containers fits into one of the existing parts.
			int PartIndex = 0;
			for (Iterator iterator = partPanels.iterator(); iterator.hasNext();) {
				PartPanel pp = (PartPanel) iterator.next();
				PartIndex++;
				if (false)
					System.out.println("end of last: "
							+ pp.getEndOfLastContainer() + " contTime: "
							+ cont.getTime() + " name: " + cont.getName());
				if (pp.getEndOfLastContainer() <= cont.getTime()) {
					//					System.out.println(
					//						"end of last: "
					//							+ pp.getEndOfLastContainer()
					//							+ " contTime: "
					//							+ cont.getTime()
					//							+ " name: "
					//							+ cont.getName());
					pp.addContainer(cont);

					continue containerLoop;
				}
			}
			// if we got here there was no fitting PartPanel
			// and so we have to make a new one.
			PartPanel pp = new PartPanel(this.container.getTime());
			partPanels.add(pp);
			pp.setParentCAD(this);
//			System.out.println("CAD.makeParts(): (" + PartIndex + ") "+ this.container.getName() + " creates new PP for " + cont.getName());
			pp.addContainer(cont);
			pp.setBackground(getBackground());
			
		}

	}

	//	/**
	//	 * @see java.awt.Component#doLayout()
	//	 */
	//	public void doLayout() {
	//		calcBounds();
	//		finishLayout();
	//		for (Iterator iter = partPanels.iterator(); iter.hasNext();) {
	//			PartPanel pp = (PartPanel) iter.next();
	//			pp.doLayout();
	//		}
	//		super.doLayout();
	//	}

	/**
	 * @return
	 */
	public double getMicrosPerPix() {
		return microsPerPix;
	}

	public void setAllMillisPerPix(java.awt.Container cont, double i) {
		Component[] components = cont.getComponents();
		for (int j = 0; j < components.length; j++) {
			if (components[j] instanceof java.awt.Container) {
				setAllMillisPerPix((java.awt.Container) components[j], i);
			}
		}
		if (cont instanceof ContainerArrangeDisplay) {
			((ContainerArrangeDisplay) cont).setMicrosPerPix(i);
		}
	}

	public void layoutChanged() {
		calcBounds();
		for (Iterator iter = partPanels.iterator(); iter.hasNext();) {
			PartPanel pp = (PartPanel) iter.next();
			pp.layoutContainers();
		}

		finishLayout();
		revalidate();
	}

	/**
	 * @param i
	 */
	public void setMicrosPerPix(double i) {
		if (i < 0)
			i = 1;
		microsPerPix = i;
		//		makeParts();

		layoutChanged();

	}

	/**
	 * @return
	 */
	public long getWinBegin() {
		return winBegin;
	}

	/**
	 * @return
	 */
	public long getWinDuration() {
		return winDuration;
	}

	/**
	 * @param l
	 */
	public void setWinBegin(long l) {
		winBegin = l;
	}

	/**
	 * @param l
	 */
	public void setWinDuration(long l) {
		winDuration = l;
	}

	/**
	 * @return
	 */
	public long getDisplayOffset() {
		return displayOffset;
	}

	/**
	 * @param l
	 */
	public void setDisplayOffset(long l) {
		displayOffset = l;
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//		g.setPaintMode();

	}

	/**
	 * Sets the background to highlightColor.
	 * 
	 * @date (06.07.00 23:25:44)
	 */
	public void setHighlight(boolean highlighted) {
		this.highlighted = highlighted;

		setBackground(highlighted ? getHighlightColor() : getDefaultColor());

	}

	/**
	 * @return Returns the defaultColor.
	 */
	public Color getDefaultColor() {
		return defaultColor;
	}

	/**
	 * @param defaultColor
	 *            The defaultColor to set.
	 */
	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	/**
	 * @return Returns the highlightColor.
	 */
	public Color getHighlightColor() {
		return highlightColor;
	}

	/**
	 * @param highlightColor
	 *            The highlightColor to set.
	 */
	public void setHighlightColor(Color highLightColor) {
		this.highlightColor = highLightColor;
	}

	/**
	 * @return Returns the highlighted.
	 */
	public boolean isHighlighted() {
		return highlighted;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.selection.SelectionListener#selectionChanged(de.uos.fmt.musitech.framework.selection.SelectionChangeEvent)
	 */
	public void selectionChanged(SelectionChangeEvent e) {
		//		Component[] children = getComponents();
		if (e.removedObjects.contains(container)) {
			setHighlight(false);
		} else if (e.addedObjects.contains(container)) {
			setHighlight(true);
		}

		//		for (int i = 0; i < children.length; i++) {
		//
		//			if (children[i] instanceof ContainerArrangeDisplay) {
		//				ContainerArrangeDisplay display =
		//					((ContainerArrangeDisplay) children[i]);
		//				Container gotContainer = display.container;
		//
		//				if (e.removedObjects.contains(gotContainer)) {
		//					display.setHighlight(false);
		//				} else if (e.addedObjects.contains(gotContainer)) {
		//					display.setHighlight(true);
		//				}
		//
		//			}
		//		}
		//		getTopLevelCAD().setAllMillisPerPix((java.awt.Container)
		// getTopLevelCAD(), getMicrosPerPix());
	}

	/**
	 * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#setSelectionController(de.uos.fmt.musitech.framework.selection.SelectionController)
	 */
	public void setSelectionController(SelectionController c) {
		this.selectionController = c;
		CADMouseListener CADMouse = new CADMouseListener(getTopLevelCAD());
		addMouseListener(CADMouse);
		addMouseMotionListener(CADMouse);
	}

	/**
	 * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#objectAt(java.awt.Point)
	 */
	public MObject objectAt(Point p) {
		Component c = getComponentAt(p);
		if (c instanceof ContainerArrangeDisplay) {
			return ((ContainerArrangeDisplay) c).container;
		}

		return null;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#objectsTouched(java.awt.Rectangle)
	 */
	public Collection objectsTouched(Rectangle r) {
		return null;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#timeCovered(java.awt.Rectangle)
	 */
	public TimeRange timeCovered(Rectangle r) {
		return null;
	}

	int oldLeftX;

	int oldRightX;

	int oldUpperY;

	int oldLowerY;

	/**
	 * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#paintDragArea(java.awt.Rectangle)
	 */
	public void paintDragArea(Rectangle r) {
		Graphics g = getGraphics();
		g.setXORMode(Color.DARK_GRAY);

		if (oldLeftX != oldRightX)
			g.fillRect(oldLeftX, oldUpperY, oldRightX - oldLeftX, oldLowerY
					- oldUpperY);

		if (r != null) {

			g.fillRect(r.x, r.y, r.x + r.width - r.x, r.y + r.height - r.y);
			oldLeftX = r.x;
			oldRightX = r.x + r.width;
			oldUpperY = r.y;
			oldLowerY = r.y + r.height;
		} else {
			oldLeftX = oldRightX = oldUpperY = oldLowerY = 0;
		}

		g.setPaintMode();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
	 */
	public boolean externalChanges() {
		return dataChanged;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
	 */
	public void destroy() {
		SelectionManager selMan = SelectionManager.getManager();
		selMan.removeListener(this);
		IDataChangeManager datMan = DataChangeManager.getInstance();
		datMan.removeListener(this);
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
	 */
	public void focusReceived() {
//		System.out.println("Focus Received");
		if (dataChanged) {
			dataChanged = false;
			updateDisplay();
		}
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
	 */
	public EditingProfile getEditingProfile() {
		return editProfile;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
	 */
	public Object getEditObj() {
		return container;
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
	 */
	public boolean isFocused() {
		return isFocusOwner();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
	 *      de.uos.fmt.musitech.framework.editor.EditingProfile,
	 *      de.uos.fmt.musitech.framework.editor.Display)
	 */
	public void init(Object editObject, EditingProfile profile, Display root) {
		this.editObj = editObject;
		this.editProfile = profile;
		if (((Container) editObj).getTime() != 0) {
			// surounding container must start at 0
			// to avoid display errors
			BasicContainer suroundCont = new BasicContainer();
			suroundCont.setName("*");
			suroundCont.add(new PerformanceNote(0, 1, 0));
			suroundCont.add(((Container) editObj));
			this.container = suroundCont;
			setContainer(suroundCont);
		} else {
			setContainer((Container) editObj);
		}

		// register at selection managner / dataChangeManager
		SelectionManager selMan = SelectionManager.getManager();
		selMan.addListener(this);
		IDataChangeManager datMan = DataChangeManager.getInstance();
		datMan.interestExpandElements(this, getContainer());
		//		createGUI();
	}

	/**
	 *  
	 */
	private void createGUI() {
		// TODO delete debug SysOut
	    // just for debugging
	    
	    makeParts();
		calcBounds();
		finishLayout();

		calculateMPP();
		updateDisplay();

	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
	 */
	public void updateDisplay() {
//		System.out.print("ContainerArrangeDisplay.update Display(): ");
		ContainerArrangeDisplay cad = getTopLevelCAD();
//		System.out.println(" thisCAD: " + container.getName()
//				+ ",  TopLevelCAD: " + getTopLevelCAD().container.getName());

		cad.setAllMillisPerPix((java.awt.Container) cad, getMicrosPerPix());
		cad.setWindowRecursive((java.awt.Container) cad, getWinBegin(),
				getWinDuration());

	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#getRootDisplay()
	 */
	public Display getRootDisplay() {
		return getTopLevelCAD();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
	 */
	public void dataChanged(DataChangeEvent e) {
		dataChanged = true;
		// Display update in focusReceived()
//		System.out.println("CAD: dataChanged");
		updateDisplay();
	}

	ContainerArrangeDisplay parentCAD;

	/**
	 * @param parentCAD
	 */
	public void setParentCAD(ContainerArrangeDisplay parentCAD) {
		this.parentCAD = parentCAD;
//		chooseColor(true);
//		System.out.println("CAD.setParent: " + container.getName() + " has as parent: " + parentCAD.container.getName());
	}

	public ContainerArrangeDisplay getTopLevelCAD() {
		if (parentCAD == null)
			return this;
		else
			return parentCAD.getTopLevelCAD();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	public Component asComponent() {
		return this;
	}

}