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
 * Created on 06.04.2004
 *
 */
package de.uos.fmt.musitech.utility.gui.interact;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.framework.TestCase;

/**
 * JUnitTests for class CompoundComponentManager.
 * 
 * @author Kerstin Neubarth
 *
 */
public class CompoundComponentManagerTest extends TestCase {

	/**
	 * Tests method <code>getInnerComponentsRecursive(Container)</code>.
	 * <br>
	 * Five JPanels are nested recursively and added to a JPanel <code>outerComp</code>.
	 * The five JPanels are recorded in a Vector <code>comps</code> against which
	 * the Vector returned by method <code>getInnerComponents(outerComp)</code> is
	 * checked.
	 */
	public static void testGetInnerComponentsRecursive() {
		//Vector to contain all components (unless outmost component)
		Vector comps = new Vector();
		//generate compound component
		for (int i = 0; i < 5; i++) {
			JPanel panel = new JPanel();
			comps.add(panel);
			if (i > 0) {
				((JPanel) comps.elementAt(i - 1)).add(panel);
			}
		}
		JPanel outerComp = new JPanel();
		outerComp.add((JPanel) comps.elementAt(0));
		//get inner components recursively
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(outerComp);
		//check innerComps against comps
		assertEquals(innerComps.size(), comps.size());
		assertTrue(innerComps.containsAll(comps));
		//for testing (both Vectors not null and size>0 ?):
		//		System.out.println("CompoundComponentManagerTest.testGetInnerComponentsRecursive():");
		//		System.out.println("inner components are:");
		//		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
		//			Component element = (Component) iter.next();
		//			System.out.println(element.toString()+"\t");
		//		}
		//		System.out.println();
	}

	/**
	 * Tests method <code>getInnerComponentsRecursive(Container)</code> with a more
	 * "irregular" nesting of components.
	 * <br>
	 * A JPanel <code>outmostPanel</code> is organized according to BorderLayout. The
	 * components given to the NORTH, CENTER and SOUTH areas themselves contain up to 
	 * three levels of components. All components added to <code>outmostPanel</code>
	 * or one of its components and components' components (ecc.) are recorded in a
	 * Vector <code>comps</code>. This Vector is used to check the return value of
	 *  method <code>testGetInnerComponentsRecursive(Container)</code>.
	 */
	public static void testGetInnerComponentsRecursive2() {
		//Vector to contain all components (unless outmost component)
		Vector comps = new Vector();
		//generate compound component
		JPanel outmostPanel = new JPanel();
		outmostPanel.setLayout(new BorderLayout());
		Box box = Box.createHorizontalBox();
		comps.add(box);
		for (int i = 0; i < 3; i++) {
			JLabel label = new JLabel();
			comps.add(label);
			box.add(label);
		}
		outmostPanel.add(box, BorderLayout.NORTH);
		JPanel centralPanel = new JPanel();
		comps.add(centralPanel);
		for (int i = 0; i < 2; i++) {
			JPanel panel = new JPanel();
			comps.add(panel);
			JButton button = new JButton();
			comps.add(button);
			panel.add(button);
			centralPanel.add(panel);
		}
		outmostPanel.add(centralPanel, BorderLayout.CENTER);
		JPanel southPanel = new JPanel();
		comps.add(southPanel);
		outmostPanel.add(southPanel, BorderLayout.SOUTH);
		//get inner components recursively
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(outmostPanel);
		//check innerComps against comps
		assertEquals(innerComps.size(), comps.size());
		assertTrue(innerComps.containsAll(comps));
	}

	/**
	 * Tests method <code>getInnerComponentsRecursive(Container)</code> with a JPanel
	 * containing a JScrollPane.
	 * <br><br>
	 * Note: Method <code>getInnerComponentsRecursive(Container)</code> returns
	 * the JScrollPane's components and the components of both ScrollBars as well
	 * (as is to be expected).
	 */
	public static void testGetInnerComponentsRecursive3() {
		//Vector to contain all components (unless outmost component)
		Vector comps = new Vector();
		//generate compound component
		JPanel outmostPanel = new JPanel();
		JPanel innerPanel = new JPanel();
		comps.add(innerPanel);
		JLabel label = new JLabel();
		comps.add(label);
		innerPanel.add(label);
		JScrollPane scroll = new JScrollPane(innerPanel);
		comps.add(scroll);
		comps.addAll(Arrays.asList(scroll.getComponents()));
		comps.addAll(Arrays.asList(scroll.getHorizontalScrollBar().getComponents()));
		comps.addAll(Arrays.asList(scroll.getVerticalScrollBar().getComponents()));
		outmostPanel.add(scroll);
		//get inner components recursively
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(outmostPanel);
		//check innerComps against comps
		assertEquals(innerComps.size(), comps.size());
		assertTrue(innerComps.containsAll(comps));
	}

	/**
	 * Tests method <code>getInnerComponentsRecursive(Container)</code> with a JFrame.
	 * <br><br>
	 * Note: Method <code>getInnerComponentsRecursive(Container)</code> returns
	 * the JFrame's root pane, layered pane, content pane and glass pane as well 
	 * as the JPanel added to the JFrame's content pane (as is to be expected).
	 */
	public static void testGetComponentsRecursive4() {
		//generate compound component
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JPanel());
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(frame);
		assertEquals(innerComps.size(), 5);
		//5 accounts for the RootPane, LayeredPane, ContentPane, GlassPane of the top-
		//level container (JFrame) and for the component added (the JPanel)
	}

	/**
	 * Tests method <code>removeInnerMouseListeners(Container)</code>.
	 * <br>
	 * Five JPanels are nested recursively and added to an outmost JPanel <code>outerComp</code>.
	 * Each of the five inner JPanels is given a MouseListener. The matching between
	 * JPanels and MouseListeners is recorded in Hashtable <code>tableForChecking</code>.
	 * After invoking method <code>removeInnerMouseListeners(outerComp)</code> the return
	 * value of this method is compared to <code>tableForChecking</code> (note: the values
	 * in <code>tableForChecking</code> are single MouseListeners, while the values in
	 * the returned Hashtable are Arrays of MouseListeners). Furthermore, the JPanels
	 * are checked if no longer having a MouseListener.
	 */
	public static void testRemoveInnerMouseListeners() {
		Hashtable tableForChecking = new Hashtable(0);
		//generate compound component
		JPanel[] comps = new JPanel[5];
		for (int i = 0; i < comps.length; i++) {
			comps[i] = new JPanel();
			MouseListener listener = new MouseAdapter() {
			};
			comps[i].addMouseListener(listener);
			tableForChecking.put(comps[i], listener);
			if (i > 0) {
				comps[i - 1].add(comps[i]);
			}
		}
		JPanel outerComp = new JPanel();
		outerComp.add(comps[0]);
		//remove inner MouseListeners
		Map removedListeners = CompoundComponentManager.removeInnerMouseListeners(outerComp);
		//check removedListeners against tableForChecking
		assertEquals(removedListeners.keySet(), tableForChecking.keySet());
		for (Iterator iter = removedListeners.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			MouseListener[] listeners = (MouseListener[]) removedListeners.get(key);
			assertEquals(listeners[0], tableForChecking.get(key));
		}
		//check inner components if MouseListeners in fact removed
		for (int i = 0; i < comps.length; i++) {
			assertEquals(comps[i].getMouseListeners().length, 0);
		}
	}

	/**
	 * Tests method <code>restoreInnerMouseListeners(Container, Hashtable)</code>.
	 * <br>
	 * The compound components consists of nested JPanels. All JPanels except of the
	 * outmost JPanel are given a MouseListener. These MouseListeners are removed by
	 * invoking method <code>removeInnerMouseListeners(Container)</code> and are restored
	 * afterwards. The current MouseListeners of the inner components are then compared
	 * to the original matching as initially recorded in a Hashtable.
	 */
	public static void testRestoreInnerMouseListeners() {
		//generate compound component
		JPanel[] comps = new JPanel[5];
		for (int i = 0; i < comps.length; i++) {
			comps[i] = new JPanel();
			MouseListener listener = new MouseAdapter() {
			};
			comps[i].addMouseListener(listener);
			if (i > 0) {
				comps[i - 1].add(comps[i]);
			}
		}
		JPanel outerComp = new JPanel();
		outerComp.add(comps[0]);
		//Hashtable for recording original matching of components and listeners
		Hashtable origTable = new Hashtable();
		for (int i = 0; i < comps.length; i++) {
			origTable.put(comps[i], comps[i].getMouseListeners());
		}
		//remove inner MouseListeners
		Map removedListeners = CompoundComponentManager.removeInnerMouseListeners(outerComp);
		//restore inner MouseListeners
		CompoundComponentManager.restoreInnerMouseListeners(outerComp, removedListeners);
		//check restored MouseListeners against origTable
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(outerComp);
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			if (origTable.keySet().contains(element)) {
				assertEquals(element.getMouseListeners()[0], ((MouseListener[]) origTable.get(element))[0]);
			} else {
				fail();
			}
		}
	}

	/**
	 * Tests method <code>removeInnerKeyListeners(Container)</code>.
	 * <br>
	 * Five JPanels are nested recursively and added to an outmost JPanel <code>outerComp</code>.
	 * Each of the five inner JPanels is given a KeyListener. The matching between
	 * JPanels and MouseListeners is recorded in Hashtable <code>origTable</code>.
	 * After invoking method <code>removeInnerKeyListeners(outerComp)</code> the return
	 * value of this method is compared to <code>origTable</code>. Furthermore, the 
	 * JPanels are checked if no longer having a KeyListener.
	 */
	public static void testRemoveInnerKeyListeners() {
		//generate compound component
		JPanel[] comps = new JPanel[5];
		for (int i = 0; i < comps.length; i++) {
			comps[i] = new JPanel();
			KeyListener listener = new KeyAdapter() {
			};
			comps[i].addKeyListener(listener);
			if (i > 0) {
				comps[i - 1].add(comps[i]);
			}
		}
		JPanel outerComp = new JPanel();
		outerComp.add(comps[0]);
		//Hashtable for recording original matching of components and listeners
		Hashtable origTable = new Hashtable();
		for (int i = 0; i < comps.length; i++) {
			origTable.put(comps[i], comps[i].getKeyListeners());
		}
		//remove inner KeyListeners
		Map removedListeners = CompoundComponentManager.removeInnerKeyListeners(outerComp);
		//check removedListeners against origTable
		for (Iterator iter = origTable.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			KeyListener[] listenersOrig = (KeyListener[]) origTable.get(key);
			if (removedListeners.keySet().contains(key)) {
				KeyListener[] listenersRemoved = (KeyListener[]) removedListeners.get(key);
				assertEquals(listenersOrig.length, listenersRemoved.length);
				assertTrue(Arrays.asList(listenersOrig).containsAll(Arrays.asList(listenersRemoved)));
			} else
				fail();
		}
		//check comps if KeyListeners in fact removed
		for (int i = 0; i < comps.length; i++) {
			assertEquals(comps[i].getKeyListeners().length, 0);
		}
	}

	/**
	 * Tests method <code>restoreInnerKeyListeners(Container, Hashtable)</code>.
	 * <br>
	 * The compound components consists of nested JPanels. All JPanels except of the
	 * outmost JPanel are given a KeyListener. These KeyListeners are removed by
	 * invoking method <code>removeInnerKeyListeners(Container)</code> and are restored
	 * afterwards. The current KeyListeners of the inner components are then compared
	 * to the original matching as initially recorded in a Hashtable.
	 */
	public static void testRestoreInnerKeyListeners() {
		//generate compound component
		JPanel[] comps = new JPanel[5];
		for (int i = 0; i < comps.length; i++) {
			comps[i] = new JPanel();
			KeyListener listener = new KeyAdapter() {
			};
			comps[i].addKeyListener(listener);
			if (i > 0) {
				comps[i - 1].add(comps[i]);
			}
		}
		JPanel outerComp = new JPanel();
		outerComp.add(comps[0]);
		//Hashtable for recording original matching of components and listeners
		Hashtable origTable = new Hashtable();
		for (int i = 0; i < comps.length; i++) {
			origTable.put(comps[i], comps[i].getKeyListeners());
		}
		//remove inner KeyListeners
		Map removedListeners = CompoundComponentManager.removeInnerKeyListeners(outerComp);
		//restore inner KeyListeners
		CompoundComponentManager.restoreInnerKeyListeners(outerComp, removedListeners);
		//check restored KeyListeners against origTable
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(outerComp);
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			if (origTable.keySet().contains(element)) {
				assertEquals(element.getKeyListeners()[0], ((KeyListener[]) origTable.get(element))[0]);
			} else {
				fail();
			}
		}
	}

	/**
	 * Tests method <code>disableInnerComponents(Container)</code>.
	 */
	public static void testDisableInnerComponents() {
		//generate compound component
		JPanel outerComp = new JPanel();
		for (int i = 0; i < 3; i++) {
			JPanel panel = new JPanel();
			panel.setEnabled(true);
			JButton button = new JButton();
			button.setEnabled(true);
			panel.add(button);
			outerComp.add(panel);
		}
		//check pre-condition (inner components are enabled)
		for (Iterator iter = CompoundComponentManager.getInnerComponentsRecursive(outerComp).iterator();
			iter.hasNext();
			) {
			Component element = (Component) iter.next();
			assertTrue(element.isEnabled());
		}
		//disable all inner components
		CompoundComponentManager.disableInnerComponents(outerComp);
		//check if inner components are in fact disabled
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(outerComp);
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			assertFalse(element.isEnabled());
		}
	}

	/**
	 * Tests method <code>addMouseListenerToInnerComponents(Container, MouseListener)</code>.
	 */
	public static void testAddMouseListenerToInnerComponents() {
		//generate compound component
		JPanel outerComp = new JPanel();
		for (int i = 0; i < 3; i++) {
			JPanel panel = new JPanel();
			panel.add(new JLabel());
			outerComp.add(panel);
		}
		//provide MouseListener to add
		MouseListener listener = new MouseAdapter() {
		};
		//add listener to inner components
		CompoundComponentManager.addMouseListenerToInnerComponents(outerComp, listener);
		//check if inner components now have listener
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(outerComp);
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			MouseListener[] listeners = element.getMouseListeners();
			assertEquals(listeners[0], listener);
		}
	}

	/**
	 * Tests method <code>addKeyListenerToInnerComponents(Container, KeyListener)</code>.
	 */
	public static void testAddKeyListenerToInnerComponents() {
		//generate compound component
		JPanel outerComp = new JPanel();
		for (int i = 0; i < 3; i++) {
			JPanel panel = new JPanel();
			panel.add(new JLabel());
			outerComp.add(panel);
		}
		//provide KeyListener to add
		KeyListener listener = new KeyAdapter() {
		};
		//add listener to inner components
		CompoundComponentManager.addKeyListenerToInnerComponents(outerComp, listener);
		//check if inner components now have listener
		Collection innerComps = CompoundComponentManager.getInnerComponentsRecursive(outerComp);
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			KeyListener[] listeners = element.getKeyListeners();
			assertEquals(listeners[0], listener);
		}
	}

	/**
	 * Tests method <code>deactivateContainer(Container)</code>.
	 */
	public static void testDeactivateContainer() {
		//generate compound component
		JPanel outerComp = new JPanel();
		for (int i = 0; i < 3; i++) {
			JPanel panel = new JPanel();
			panel.add(new JLabel());
			outerComp.add(panel);
		}
		//add MouseListeners to container and its components
		MouseListener listener = new MouseAdapter(){};
		outerComp.addMouseListener(listener);
		CompoundComponentManager.addMouseListenerToInnerComponents(outerComp,listener);
		//test pre-conditions
		assertTrue(outerComp.isEnabled());
		assertTrue(outerComp.getMouseListeners().length>0);
		Collection comps = CompoundComponentManager.getInnerComponentsRecursive(outerComp);
		for (Iterator iter = comps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			assertTrue(element.getMouseListeners().length>0);
		}
		//deactivate outerComp
		Map[] removedListeners = CompoundComponentManager.deactivateContainer(outerComp);
		//check result
		assertNotNull(removedListeners);
		assertTrue(removedListeners[CompoundComponentManager.MOUSE].size()>0);
		assertFalse(outerComp.isEnabled());
		assertFalse(outerComp.getMouseListeners().length>0);
		for (Iterator iter = comps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			assertFalse(element.isEnabled());
			assertFalse(element.getMouseListeners().length>0);
		}	
	}

}
