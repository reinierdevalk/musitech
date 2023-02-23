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
 * File GlassPaneMouseListenerTest.java
 * Created on 18.02.2004
 */

package de.uos.fmt.musitech.utility.gui.interact;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import junit.framework.TestCase;

/**
 * @author tweyde
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GlassPaneMouseListenerTest extends TestCase {

	static boolean testNormal = false;
	static boolean testSpecial = true;

	/**
	 * Tests marking a component in a JFrame whose glass pane is set visible.
	 * For handling MouseEvents a GlassPaneMouseListener is added, which gets
	 * as special targets the components to be marked.
	 */
	public static void testMarkingWithGlassPane() {
		Collection specialTargets = new Vector();
		JPanel panel = new JPanel();
//		specialTargets.add(panel);
		JLabel label1 = new JLabel("Label 1");
		panel.add(label1);
		specialTargets.add(label1);
		JLabel label2 = new JLabel("Label 2");
		panel.add(label2);
		specialTargets.add(label2);

		MouseAdapter adapter = new BorderMouseAdapter();

		JFrame frame = new JFrame("Test marking with glass pane");

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem openItem = new JMenuItem("Open");
		fileMenu.add(openItem);
		JMenuItem saveItem = new JMenuItem("Save");
		fileMenu.add(saveItem);
		frame.setJMenuBar(menuBar);

		frame.getContentPane().add(panel);
		GlassPaneMouseListener gpml = new GlassPaneMouseListener(frame);
		if (testNormal) {
			label1.addMouseListener(adapter);
			label2.addMouseListener(adapter);
			panel.addMouseListener(adapter);
		}
		if (testSpecial) {
			gpml.setSpecialTargets(specialTargets);
			gpml.setSpecialMouseListener(adapter);
		}
		frame.getGlassPane().addMouseListener(gpml);
		frame.getGlassPane().addMouseMotionListener(gpml);

		frame.getGlassPane().setVisible(true);
		frame.setSize(200, 100);
		frame.setVisible(true);
	}

	static class BorderMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			JOptionPane.showMessageDialog(
				(JComponent) e.getSource(),
				"Mouse clicked");
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			JComponent comp = (JComponent) e.getSource();
			comp.setBorder(BorderFactory.createEtchedBorder());
			System.out.println("entered " + comp);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			JComponent comp = (JComponent) e.getSource();
			comp.setBorder(null);
			System.out.println("exited " + comp);
		}
	}

	public static void main(String[] args) {
		testMarkingWithGlassPane();
	}
}
