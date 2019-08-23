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
 * Created on 16.06.2004
 */
package de.uos.fmt.musitech.structure.harmony.gui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.editor.EditorWindow;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class for testing the GUI of a ChordSymbolDisplay.
 * 
 * TODO anpassen (vgl. neue Klasse ChordSymbolDisplay2)
 * 
 * 
 * @author Kerstin Neubarth
 *
 */
public class ChordSymbolDisplayTestGUI {

	/**
	 * Private helper method. Returns a ChordSymbol which can be used as editObj
	 * of a ChordSymbolDisplay.
	 * 
	 * @return ChordSymbol
	 */
	private static ChordSymbol createChordSymbol() {
		ChordSymbol chordSymbol = new ChordSymbol(new Rational(1, 2), 1000);
		chordSymbol.setRoot('A');
		chordSymbol.setRootAlteration(-1);
		chordSymbol.setTop(5);
		chordSymbol.setBase(3);
		chordSymbol.setExtensions("7");
		return chordSymbol;
	}
	
	/**
	 * Private helper method. Returns a ChordSymbolDisplay for the specified
	 * ChordSymbol.
	 * <br>
	 * This method does not use the EditorFactory, as class ChordSymbol is deprecated.
	 * 
	 * @param cs
	 * @return
	 */
	private static ChordSymbolDisplay createDisplay(ChordSymbol cs){
		ChordSymbolDisplay display = new ChordSymbolDisplay();
		EditingProfile profile = EditorFactory.getOrCreateProfile(cs);
		display.init(cs, profile, display);
		return display;
	}

	/**
	 * Creates a ChordSymbolDisplay and shows it in a JFrame.
	 */
	public static void testCreateGUI() {
		ChordSymbol editObj = createChordSymbol();
		Display display = null;
//		try {
//			display = EditorFactory.createDisplay(editObj, null);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//		}
		display = createDisplay(editObj);
		if (display != null && display instanceof ChordSymbolDisplay) {
			//			((ChordSymbolDisplay) display).createGUI();
			JFrame frame1 = new JFrame("Test createGUI()");
			frame1.getContentPane().add((JComponent) display);
			frame1.setSize(100, 100);
			frame1.setVisible(true);
		} else
			System.out.println(
				"ChordSymbolTestGUI.testCreateGUI():\n"
					+ "EditorFactory has not created ChordSymbolDisplay");
	}

	/**
	 * Tests the layout of the symbols used in a ChordSymbol, 
	 * i.e. displaying the accidental signs.
	 */
	public static void testCreateSymbols() {
		ChordSymbol chordSymbol1 = new ChordSymbol(new Rational(1, 2), 1000);
		chordSymbol1.setRoot('A');
		chordSymbol1.setRootAccidental(-2);
		chordSymbol1.setMode(Mode.MODE_MAJOR);
		chordSymbol1.setTop(1);
		chordSymbol1.setBase(10);
		chordSymbol1.setExtensions("7");
		Display display1 = null;
//		try {
//			display1 = EditorFactory.createDisplay(chordSymbol1, null);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//		}
		display1 = createDisplay(chordSymbol1);
		if (display1 != null && display1 instanceof ChordSymbolDisplay) {
			final ChordSymbolDisplay display = (ChordSymbolDisplay) display1;
			JFrame frame =
				new JFrame("A double flat maj with top 1, base 10, extension 7");
			frame.getContentPane().add((JComponent) display);
			JSlider slider = new JSlider(JSlider.VERTICAL, 1, 100, 50);
			display.setScale((slider.getValue() * 2.0 / 100.0));
			slider.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent arg0) {
					display.setScale(
						(float) (((JSlider) arg0.getSource()).getValue()
							* 2.0
							/ 100.0));
				}
			});
			frame.getContentPane().add(slider, BorderLayout.EAST);
			frame.setSize(400, 400);
			frame.setVisible(true);
		}
		ChordSymbol chordSymbol2 = new ChordSymbol(new Rational(1, 2), 1000);
		chordSymbol2.setRoot('G');
		chordSymbol2.setRootAccidental(+1);
		chordSymbol2.setMode(Mode.MODE_PHRYGIAN);
		chordSymbol2.setTop(5);
		chordSymbol2.setBase(8);
		Display display2 = null;
//		try {
//			display2 = EditorFactory.createDisplay(chordSymbol2, null);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//		}
		display2 = createDisplay(chordSymbol2);
		if (display2 != null && display2 instanceof ChordSymbolDisplay) {
			JFrame frame2 =
				new JFrame("G sharp with top 5, base 8, no extension");
			frame2.getContentPane().add((JComponent) display2);
			frame2.setSize(400, 400);
			frame2.setVisible(true);
		}
	}

	/**
	 * For experimenting with the layout.
	 * 
	 */
	public static void testGridBagConstraints() {
		ChordSymbol symbol = createChordSymbol();
		//create ChordSymbolDisplay (for fonts etc.)
		ChordSymbolDisplay display = null;
//		try {
//			display =
//				(ChordSymbolDisplay) EditorFactory.createDisplay(symbol, null);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//		} catch (ClassCastException cce) {
//			cce.printStackTrace();
//		}
		display = createDisplay(symbol);
		//get components of the ChordSymbolDisplay
		JTextField root = display.rootField;
		JTextField acc = display.accidentalField;
		JTextField top = display.topField;
		JTextField base = display.baseField;
		JTextField ext = display.extensionsField;
		JPanel mc = display.mode_comment_panel;
		//remove all components from ChordSymbolDisplay and... 
		display.removeAll();
		//...add them with new constraints
		display.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		GridBagConstraints gbc = new GridBagConstraints();
		//		gbc.insets=new Insets(0,0,0,0); gbc.fill=GridBagConstraints.NONE; gbc.ipadx=1; gbc.ipady=1;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.PAGE_END;
		//gbc.fill=GridBagConstraints.BOTH;
		display.add(top, gbc);
		JPanel rootAcc = new JPanel();
		rootAcc.setLayout(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		rootAcc.add(root, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		rootAcc.add(acc, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 4;
		gbc.gridheight = 3;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		display.add(rootAcc, gbc);
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		display.add(ext, gbc);
		gbc.gridx = 4;
		gbc.gridy = 3;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		display.add(mc, gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		display.add(base, gbc);
		//revalidate
		display.revalidate();
		//show display in a JFrame
		JFrame frame = new JFrame("Test GridBagConstraints");
		frame.getContentPane().add(display);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

	/**
	 * Tests marking the ChordSymbol as selected.
	 * <br>
	 * Therefore, below a ChordSymbolDisplay two buttons are shown.
	 * When the "Select" button is activated, the background color is changed.
	 * When "Reset" is chosen, the background color is reset to white.
	 * <br>(In this test method, both buttons are always enabled.)
	 */
	public static void testMarkSelection() {
		ChordSymbol editObj = createChordSymbol();
		ChordSymbolDisplay display = null;
//		try {
//			display =
//				(ChordSymbolDisplay) EditorFactory.createDisplay(editObj, null);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//		}
		display = createDisplay(editObj);
		if (display != null) {
			final ChordSymbolDisplay d = display;
			JButton selectButton = new JButton("Select");
			selectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					d.markSelected(true);
				}
			});
			JButton unselectButton = new JButton("Reset");
			unselectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					d.markSelected(false);
				}
			});
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(selectButton);
			buttonBox.add(unselectButton);
			Box box = Box.createVerticalBox();
			box.add(display);
			box.add(buttonBox);
			JFrame frame = new JFrame("Test selection");
			frame.getContentPane().add(box);
			frame.pack();
			frame.setVisible(true);
		}
	}

	public static void testAsDataChangeListener() {
		//editObj
		ChordSymbol cs = new ChordSymbol();
		//create ChordSymbolDisplay
		ChordSymbolDisplay display = null;
//		try {
//			display =
//				(ChordSymbolDisplay) EditorFactory.createDisplay(cs, null);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//		}
		display = createDisplay(cs);
		//create Editor for changing the ChordSymbol
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(cs, null, "Panel");
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//show display
		if (display != null) {
			JFrame frame = new JFrame("ChordSymbolDisplay");
			frame.getContentPane().add(display);
			frame.pack();
			frame.setVisible(true);
		}
		//show editor
		if (editor != null) {
			EditorWindow window = new EditorWindow("Editor with ChordSymbol");
			window.addEditor(editor);
			window.pack();
			window.setVisible(true);
		}
	}

	public static void testCreateGUI2() {
		ChordSymbol editObj = createChordSymbol();
		ChordSymbolDisplay display = null;
//		try {
//			display =
//				(ChordSymbolDisplay) EditorFactory.createDisplay(editObj, null);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//		}
		display = createDisplay(editObj);
		if (display != null) {
			display.removeAll();
			display.createGUI2();
			JFrame frame = new JFrame("Test createGUI");
			frame.getContentPane().add(display);
			frame.pack();
			frame.setVisible(true);
		}
	}

	public static void testUpdateGUI() {
		ChordSymbol editObj = createChordSymbol();
		ChordSymbolDisplay display = null;
//		try {
//			display =
//				(ChordSymbolDisplay) EditorFactory.createDisplay(editObj, null);
//		} catch (EditorConstructionException e) {
//			e.printStackTrace();
//		}
		display = createDisplay(editObj);
		if (display != null) {
			final ChordSymbolDisplay d = display;
//			d.removeAll();
//			d.createGUI2();
			JButton updateButton = new JButton("Update");
			updateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((ChordSymbol) d.getEditObj()).setRoot('F');
					d.updateGUI();
//					d.updateGUI2();
//					d.updateDisplay();
				}
			});
			Box box = Box.createVerticalBox();
			box.add(d);
			box.add(updateButton);
			JFrame frame = new JFrame("Test updateGUI");
			frame.getContentPane().add(box);
			frame.pack();
			frame.setVisible(true);
		}
	}

	public static void main(String[] args) {
//				testCreateGUI();
				testCreateSymbols();
		//		testGridBagConstraints();
		//		testMarkSelection();
				testAsDataChangeListener();
//		testCreateGUI2();
//		testUpdateGUI();
	}
}
