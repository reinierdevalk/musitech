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
 */
package de.uos.fmt.musitech.utility.gui.interact;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class provides static methods for managing MouseListeners and KeyListeners 
 * of compound components.
 * <br><br>
 * Methods are implemented for:
 * <br> - disabling the inner components of a compound component
 * <br> - removing the current MouseListeners from the inner components of a compound component
 * <br> - removing the current KeyListeners from the inner components of a compound component
 * <br> - adding a specified MouseListener to the inner components of a compound component
 * <br> - adding a specified KeyListenener to the inner components of a compound component
 * <br> - adding different MouseListeners to different inner components of a compound component
 * <br> - adding different KeyListeners to different inner components of a compound component.
 * <br><br>
 * Classes are provided that redirect Mouse and Keyboard events.
 * <br> - Disable MouseListeners
 * <br> - adding different KeyListeners to different inner components of a compound component.
 * 
 * @author Kerstin Neubarth & Tillman Weyde
 */
public class CompoundComponentManager {

	/**
	 * Returns a Vector containing all inner components of the specified Container, 
	 * but not the container itself. The components are determined recursively. 
	 * So the Vector does not only contain the Container's own components but 
	 * their respective components as well.
	 * 
	 * @param container Container whose inner components are to be returned
	 * @return Vector containing all inner components of the specified Container
	 */
	public static Collection getInnerComponentsRecursive(Container container) {
		java.util.List innerComponents = new ArrayList();
		Component[] components = container.getComponents();
		if (components != null && components.length > 0) {
			for (int i = 0; i < components.length; i++) {
				if (components[i] != null) {
					innerComponents.add(components[i]);
					if (components[i] instanceof Container) {
						innerComponents.addAll(
							getInnerComponentsRecursive(
								(Container) components[i]));
					}
				}
			}
		}
		return innerComponents;
	}

	/**
	 * Removes the MouseListeners from all inner components of the specified Container.
	 * Returnes a HashMap whose keys are the inner components of the specified
	 * Container as determined by method <code>getInnerComponentsRecursive(Container)</code>.
	 * Each key leads to an Array containing the MouseListeners initially registered 
	 * with this component. So the HashMap records the original matching between 
	 * components and MouseListeners and allows to reattach the MouseListeners 
	 * later. 
	 * 
	 * @param container Container from whose inner components the MouseListeners are to be removed
	 * @return HashMap with the specified Container's inner components as keys and their original MouseListeners as values
	 */
	public static Map removeInnerMouseListeners(Container container) {
		HashMap mouseListenerMap = new HashMap();
		Collection comps = getInnerComponentsRecursive(container);
		//for all inner components:
		for (Iterator iter = comps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			if (element != null) {
				//get MouseListeners of each inner component
				MouseListener[] listeners = element.getMouseListeners();
				if (listeners != null && listeners.length > 0) {
					//record MouseListeners attached to each inner component
					mouseListenerMap.put(element, listeners);
					//remove the MouseListeners from the inner component
					for (int i = 0; i < listeners.length; i++) {
						element.removeMouseListener(listeners[i]);
					}
				}
			}
		}
		return mouseListenerMap;
	}

	/**
	 * Reattaches the MouseListeners recorded as values in the specified HashMap to
	 * the inner components of the specified Container.
	 * <br>The specified HashMap must contain as keys the inner components of the
	 * specified Container. Each key must lead to an Array of MouseListeners to be
	 * added to the corresponding component. (In general, the HashMap will be the
	 * return value of method <code>removeInnerMouseListeners(Container)</code> applied
	 * to the specified Container.) 
	 * 
	 * @param container Container to whose inner components the MouseListeners will be added as recorded in the specified HashMap
	 * @param componentsToListeners HashMap whose keys are the inner components of the specified Container and whose values are the MouseListeners to be restored
	 */
	public static void restoreInnerMouseListeners(
		Container container,
		Map componentsToListeners) {
		Collection innerComps = getInnerComponentsRecursive(container);
		//for all inner components:
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			//get the component's MouseListeners from the HashMap ...
			if (componentsToListeners.keySet().contains(element)) {
				Object listeners = componentsToListeners.get(element);
				if (listeners instanceof MouseListener[]) {
					//... and add them to the component
					for (int i = 0;
						i < ((MouseListener[]) listeners).length;
						i++) {
						element.addMouseListener(
							((MouseListener[]) listeners)[i]);
					}
				}
			}
		}
	}

	/**
	 * Removes the KeyListeners from all inner components of the specified Container.
	 * Returnes a HashMap whose keys are the inner components of the specified
	 * Container as determined by method <code>getInnerComponentsRecursive(Container)</code>.
	 * Each key leads to an Array containing the KeyListeners initially registered 
	 * with this component. So the HashMap records the original matching between 
	 * components and KeyListeners and allows to reattach the KeyListeners later. 
	 * 
	 * @param container Container from whose inner components the KeyListeners are to be removed
	 * @return with the specified Container's inner components as keys and their original KeyListeners as values
	 */
	public static Map removeInnerKeyListeners(Container container) {
		HashMap componentsToKeyListeners = new HashMap();
		Collection comps = getInnerComponentsRecursive(container);
		//for all inner components:
		for (Iterator iter = comps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			if (element != null) {
				//get KeyListeners of each inner component
				KeyListener[] listeners = element.getKeyListeners();
				if (listeners != null && listeners.length > 0) {
					//record KeyListeners attached to each inner component
					componentsToKeyListeners.put(element, listeners);
					//remove the KeyListeners from the inner component
					for (int i = 0; i < listeners.length; i++) {
						element.removeKeyListener(listeners[i]);
					}
				}
			}
		}
		return componentsToKeyListeners;
	}

	/**
	 * Reattaches the KeyListeners recorded as values in the specified HashMap to
	 * the inner components of the specified Container.
	 * <br>The specified HashMap must contain as keys the inner components of the
	 * specified Container. Each key must lead to an Array of KeyListeners to be
	 * added to the corresponding component. (In general, the HashMap will be the
	 * return value of method <code>removeInnerKeyListeners(Container)</code> applied
	 * to the specified Container.) 
	 * 
	 * @param container Container to whose inner components the KeyListeners will be added as recorded in the specified HashMap
	 * @param componentsToListeners HashMap whose keys are the inner components of the specified Container and whose values are the KeyListeners to be restored
	 */
	public static void restoreInnerKeyListeners(
		Container container,
		Map componentsToListeners) {
		Collection innerComps = getInnerComponentsRecursive(container);
		//for all inner components:
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			//get the component's KeyListeners from the HashMap ...
			if (componentsToListeners.keySet().contains(element)) {
				Object listeners = componentsToListeners.get(element);
				if (listeners instanceof KeyListener[]) {
					//... and add them to the component
					for (int i = 0;
						i < ((KeyListener[]) listeners).length;
						i++) {
						element.addKeyListener(((KeyListener[]) listeners)[i]);
					}
				}
			}
		}
	}

	/**
	 * Sets the enabled status for all inner components of the specified Container. 
	 * The inner components are determined recursively, so the Container's components' 
	 * components (ecc.) are set as well.
	 * 
	 * @param container Container whose inner components are to be disabled
	 */
	public static void setInnerComponentsEnabled(
		Container container,
		boolean state) {
		Collection innerComps = getInnerComponentsRecursive(container);
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			if (element != null)
				element.setEnabled(state);
		}
	}

	/**
	 * Disables all inner components of the specified Container. The inner components
	 * are determined recursively, so the Container's components' components (ecc.) are
	 * disabled as well.
	 * 
	 * @param container Container whose inner components are to be disabled
	 */
	public static void disableInnerComponents(Container container) {
		setInnerComponentsEnabled(container, false);
	}

	/**
	 * Enables all inner components of the specified Container. The inner components
	 * are determined recursively, so the Container's components' components (ecc.) are
	 * enabled as well.
	 * 
	 * @param container Container whose inner components are to be enabled
	 */
	public static void enableInnerComponents(Container container) {
		setInnerComponentsEnabled(container, true);
	}

	/**
	 * Adds the specified MouseListener to all inner components of the specified 
	 * Container. The inner components are determined recursively, so the 
	 * Container's components' components (ecc.) are given the MouseListener as well.
	 * 
	 * @param container Container to whose inner components the specified MouseListener is to be added
	 * @param listener MouseListener which is to be added to the inner components of the specified Container
	 */
	public static void addMouseListenerToInnerComponents(
		Container container,
		MouseListener listener) {
		Collection innerComps = getInnerComponentsRecursive(container);
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			if (element != null)
				element.addMouseListener(listener);
		}
	}

	/**
	 * Adds the specified KeyListener to all inner components of the specified 
	 * Container. The inner components are determined recursively, so the 
	 * Container's components' components (ecc.) are given the KeyListener as well.
	 * 
	 * @param container Container to whose inner components the specified KeyListener is to be added
	 * @param listener KeyListener which is to be added to the inner components of the specified Container
	 */
	public static void addKeyListenerToInnerComponents(
		Container container,
		KeyListener listener) {
		Collection innerComps = getInnerComponentsRecursive(container);
		for (Iterator iter = innerComps.iterator(); iter.hasNext();) {
			Component element = (Component) iter.next();
			if (element != null)
				element.addKeyListener(listener);
		}
	}

	/**
	 * TODO add comment
	 * @param comp
	 * @param mouseListenerMap
	 * @return
	 */
	public static Map removeMouseListeners(
		Component comp,
		Map mouseListenerMap) {

		if (mouseListenerMap == null)
			mouseListenerMap = new HashMap();
		MouseListener[] listeners = comp.getMouseListeners();
		if (listeners != null && listeners.length > 0) {
			//record MouseListeners attached to the component
			mouseListenerMap.put(comp, listeners);
			//remove the MouseListeners from the component
			for (int i = 0; i < listeners.length; i++) {
				comp.removeMouseListener(listeners[i]);
			}
		}
		return mouseListenerMap;
	}

	// TODO testing & commenting
	public static Map restoreMouseListeners(
		Component comp,
		Map mouseListenerMap) {
		MouseListener listeners[] = (MouseListener[]) mouseListenerMap.get(comp);
		//restore the MouseListeners from the component
		for (int i = 0; i < listeners.length; i++) {
			comp.addMouseListener(listeners[i]);
		}
		return mouseListenerMap;
	}

	/**
	 * TODO add comment
	 * @param comp
	 * @param keyListenerMap
	 * @return
	 */
	public static Map removeKeyListeners(Component comp, Map keyListenerMap) {

		if (keyListenerMap == null)
			keyListenerMap = new HashMap();
		KeyListener[] listeners = comp.getKeyListeners();
		if (listeners != null && listeners.length > 0) {
			//record KeyListeners attached to each inner component
			keyListenerMap.put(comp, listeners);
			//remove the KeyListeners from the inner component
			for (int i = 0; i < listeners.length; i++) {
				comp.removeKeyListener(listeners[i]);
			}
		}
		return keyListenerMap;
	}

	static public final int MOUSE = 0;
	static public final int KEY = 1;
	static public final int LIST_NUM = 2;


	/**
	 * Deactivated a container by disabling removing all 
	 * mouse and key listeners from the component and its 
	 * descendants. 
	 * The map array returned stores the removed listeners
	 * for reactivation.
	 * @param cont The component to deactivate.
	 * @return an array with mouse and key listeners
	 */
	public static Map[] deactivateContainer(Container cont) {
//		disableInnerComponents(cont);
//		cont.setEnabled(false);
		Map mouseListenerMap = removeInnerMouseListeners(cont);
		removeMouseListeners(cont, mouseListenerMap);
		Map keyListenerMap = removeInnerKeyListeners(cont);
		removeKeyListeners(cont, keyListenerMap);
		Map[] maps = new Map[LIST_NUM];
		maps[MOUSE] = mouseListenerMap;
		maps[KEY] = keyListenerMap;
		return new Map[] { mouseListenerMap, keyListenerMap };
	}

	/**
	 * Reactivates a component by enabling and restoring the key and 
	 * mouse listeners for the component and its descendants.
	 * @param cont the component to reactivate
	 * @param maps the listeners to restore,
	 */
	public static void reactivateContainer(Container cont, Map[] maps) {
		restoreInnerMouseListeners(cont, maps[MOUSE]);
		removeMouseListeners(cont, maps[MOUSE]);
		restoreInnerKeyListeners(cont, maps[KEY]);
		removeKeyListeners(cont, maps[KEY]);
//		enableInnerComponents(cont);
//		cont.setEnabled(true);
	}

}
