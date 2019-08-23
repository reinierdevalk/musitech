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
 * Created on 16.03.2004
 */
package de.uos.fmt.musitech.structure.form.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.TimedComparator;
import de.uos.fmt.musitech.utility.collection.SortedUniqesCollection;

/**
 * This class displays one or more music containers as a part
 * in an arrangementPanel.    
 * @author Jan & Tillman
 */
public class PartPanel extends JPanel {

	long endOfLastContainer = 0;

	ArrayList contArrDis = new ArrayList();

	/**
	 * 
	 * @uml.property name="containers"
	 * @uml.associationEnd multiplicity="(0 -1)" elementType="de.uos.fmt.musitech.music.Container"
	 */
	protected SortedUniqesCollection containers = new SortedUniqesCollection(
		Container.class,
		new TimedComparator());

	long time = 0; // the begin time of this PartPanel

	/**
	 * 
	 */
	public PartPanel() {
		setLayout(null);
	}

	public PartPanel(long time) {
		this();
		this.time = time;
	}

	/**
	 * @return
	 * 
	 * @uml.property name="endOfLastContainer"
	 */
	public long getEndOfLastContainer() {
		return endOfLastContainer;
	}

		/**
	 * @param cont
	 */
	public void addContainer(Container cont) {
		containers.add(cont);
		long containerStart = cont.getTime();
		if (time > containerStart) {
			time = containerStart;
		}
		if (cont.getName() == "rechte Hand")
		    System.out.println("rechte Hand");
		// TODO: hier Fehler bei Pausen
		long endOfThisContainer = containerStart + cont.getDuration();
		if (endOfLastContainer < endOfThisContainer)
			endOfLastContainer = endOfThisContainer;
		// TODO: hier schon create ???
		createContArrDis();
		layoutContainers();
	}

	int maxHeight;

	private void createContArrDis() {
		removeAll();
		for (Iterator iter = containers.iterator(); iter.hasNext();) {
			Container cont = (Container) iter.next();
//			System.out.println("PP: " + cont.getName() + " creates new CAD");
			ContainerArrangeDisplay cad = new ContainerArrangeDisplay();
			cad.setParentCAD(parentCAD);
			cad.setContainer(cont);
//			cad.setColor(cad.getBackground().brighter());
			add(cad);
		}
	}

	final boolean DEBUGLAYOUT = false;
	
	/**
	 * 
	 */
	public void layoutContainers() {

		maxHeight = 0;

		Component[] contArrDis = getComponents();
		long start = 0;
		long width = 0;

		for (int i = 0; i < contArrDis.length; i++) {

			Container cADCont =
				((ContainerArrangeDisplay) contArrDis[i]).getContainer();
			ContainerArrangeDisplay CAD =
				(ContainerArrangeDisplay) contArrDis[i];

			double millisPPix = ((ContainerArrangeDisplay) contArrDis[i])
					.getMicrosPerPix();
			long CADTime = cADCont.getTime();
			long winBegin = CAD.getWinBegin();
			long winDur = CAD.getWinDuration();
			if (DEBUGLAYOUT) System.out.println("*" + cADCont.getName() + "*:  CADTime: " + CADTime / 1000 + " PPTime: "
					+ time/1000 + ", winBegin: " + winBegin / 1000 + ", winDur: "
					+ winDur / 1000);
			// container begins left of visible window
			if (cADCont.getTime() <= CAD.getWinBegin()) {
				// container is not visible
				if (CAD.getWinBegin() + CAD.getWinDuration() <= cADCont
						.getTime()) {
					if (DEBUGLAYOUT) System.out.println("left, not visible");
					start = -1;
					width = 0;
				}
				// containers end is visible
				else {
					if (DEBUGLAYOUT) System.out.println("left, visible");
					start -= (int) millisPPix;
					width = cADCont.getTime() + cADCont.getDuration()
							- CAD.getWinBegin() + (int) millisPPix;
				}
			}
			// container begins right of window begin
			else {
				// container is not visible
				if (CAD.getWinBegin() + CAD.getWinDuration() < cADCont
						.getTime()) {
					if (DEBUGLAYOUT) System.out.println("right, not visible");
					start = -1;
					width = 0;

				}
				// container begins in visible window
				else {
					if (DEBUGLAYOUT) System.out.print("right, visible ");
					if (time < CAD.getWinBegin()) {
						if (DEBUGLAYOUT) System.out.println("time < winBegin");
						start = cADCont.getTime() - CAD.getWinBegin();
					} else {
						if (DEBUGLAYOUT) System.out.println("else");
						start = cADCont.getTime() - time;
					}

					width = Math.min(CAD.getWinBegin() + CAD.getWinDuration()
							- start, cADCont.getDuration());
				}
			}
			start = Math.round(start / millisPPix);
			width = Math.round(width / millisPPix);
			contArrDis[i].setBounds((int) start, 0, (int) width, contArrDis[i]
					.getPreferredSize().height);
			if (contArrDis[i].getPreferredSize().height > maxHeight) {
				maxHeight = contArrDis[i].getPreferredSize().height;
			}
		}

	}

	/**
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		Dimension prefSize = super.getPreferredSize();
		prefSize.height = maxHeight;
		return prefSize;
	}

	ContainerArrangeDisplay parentCAD;
	
	/**
	 * @param display
	 */
	public void setParentCAD(ContainerArrangeDisplay display) {
		parentCAD = display;
	}

	//	/**
	//	 * @see java.awt.Component#doLayout()
	//	 */
	//	public void doLayout() {
	//		layoutContainers();
	////		super.doLayout();
	//	}

}
