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
 * Created on 14.06.2004
 *
 */
package de.uos.fmt.musitech.structure.harmony.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbolSequence;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorWindow;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.selection.Selection;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.TransportButtons;

/**
 * Class for testing the GUI of a ChordSymbolSequenceDisplay.
 * 
 * @author Kerstin Neubarth
 *
 */
public class ChordSymbSeqDisplayTestGUI {

	/**
	 * Private helper method. Returns a ChordSymbol with the specified root.
	 * The <code>top</code> of the ChordSymbol is set to 5; all other fields use
	 * their default values. 
	 * 
	 * @param root char giving the root of the ChordSymbol to be created
	 * @return ChordSymbol with the specified root
	 */
	private static ChordSymbol createChordSymbol(char root) {
		ChordSymbol symbol = new ChordSymbol();
		symbol.setRoot(root);
		symbol.setTop(5);
		return symbol;
	}

	/**
	 * Private helper method. Returns a ChordSymbolSequence which can be used
	 * as editObj of the test ChordSymbSeqDisplays.
	 * <br>The ChordSymbolSequence will contain three ChordSymbols with roots
	 * c, d and e resp.
	 * 
	 * @return ChordSymbolSequence 
	 */
	private static ChordSymbolSequence createChordSymbolSequence() {
		ChordSymbolSequence sequence = new ChordSymbolSequence();
		ChordSymbol symbol1 = createChordSymbol('c');
		symbol1.setTime(0);
		sequence.add(symbol1);
		ChordSymbol symbol2 = createChordSymbol('d');
		symbol2.setTime(100);
		sequence.add(symbol2);
		ChordSymbol symbol3 = createChordSymbol('e');
		symbol3.setTime(200);
		sequence.add(symbol3);
		return sequence;
	}

	/**
	 * Creates a ChordSymbSeqDisplay and shows it in a JFrame.
	 */
	public static void testChordDisplayGUI() {
		ChordSymbSeqDisplay display = null;
		//new ChordSymbSeqDisplay(createChordSymbolSequence());
		try {
			display =
				(ChordSymbSeqDisplay) EditorFactory.createDisplay(
					createChordSymbolSequence(),
					null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		} catch (ClassCastException ce) {
			ce.printStackTrace();
		}
		if (display != null) {
			display.doInitialLayout();
			JFrame frame = new JFrame("ChordSymSeqDisplay");
			frame.getContentPane().add(display);
			frame.setVisible(true);
		}
	}

	/**
	 * Tests a ChordSymbSeqDisplay acting as SelectionListener.
	 * <br>
	 * A ChordSymbSeqDisplay is shown. Below there is a JButton with text "Select".
	 * When this button is activated, the first ChordSymbol of the displayed
	 * ChordSymbolSequence should be shown in red.
	 * (This first symbol has been added to the selection of the SelectionManager before.)
	 */
	public static void testSelectionListener() {
		final ChordSymbolSequence seq = createChordSymbolSequence();
		ChordSymbSeqDisplay display = null;
		try {
			display =
				(ChordSymbSeqDisplay) EditorFactory.createDisplay(seq, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		if (display != null) {
			final ChordSymbSeqDisplay d = display;
			JButton selectButton = new JButton("Select");
			selectButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Selection sel =
						SelectionManager.getManager().getSelection();
					sel.add(seq.get(0),d);
					SelectionManager.getManager().selectionChanged(
						new SelectionChangeEvent(null));
				}
			});
			Box box = Box.createVerticalBox();
			box.add(d);
			box.add(selectButton);
			JFrame frame = new JFrame("Test SelectionListener");
			frame.getContentPane().add(box);
			frame.pack();
			frame.setVisible(true);
		}
	}
	
	/**
	 * Tests a ChordSymbSeqDisplay acting as a DataChangeListener.
	 * <br>
	 * Therefore, an additional Editor is created for changing the first ChordSymbol
	 * of the displayed ChordSymbolSequence. After having changed the ChordSymbol in
	 * the Editor, the Display updates when receiving the focus.
	 */
	public static void testDataChangeListener(){
		//create ChordSymbolSequence to be displayed
		ChordSymbolSequence editObj = new ChordSymbolSequence();
		editObj.add(new ChordSymbol(null, 0));
		editObj.add(new ChordSymbol(null, 100));
		editObj.add(new ChordSymbol(null, 200));
		//create and show Display
		Display display = null;
		try {
			display = EditorFactory.createDisplay(editObj, null);
			JFrame frame = new JFrame("Display");
			frame.getContentPane().add((JComponent)display);
			frame.pack();
			frame.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		//create Editor for changing the first ChordSymbol
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj.get(0), null, "Panel");
			EditorWindow window = new EditorWindow("Editor");
			window.addEditor(editor);
			window.pack();
			window.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests a ChordsymbolSeqDisplay being controlled by the PlayTimer.
	 * The currently acitvated ChordSymbol (that is the ChordSymbol currently
	 * played back) is highlighted.
	 */
	public static void testAsTimeable(){
	    //create ChordSymbolSequence to be displayed
		ChordSymbolSequence editObj = new ChordSymbolSequence();
		editObj.add(new ChordSymbol(null, 0));
		editObj.add(new ChordSymbol(null, 1000000));
		editObj.add(new ChordSymbol(null, 2000000));
		//create and show Display
		Display display = null;
		try {
			display = EditorFactory.createDisplay(editObj, null);
			JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add((JComponent)display);
            if (display instanceof ChordSymbSeqDisplay){
                ObjectPlayer player = ObjectPlayer.getInstance();
                player.getPlayTimer().registerForPush((ChordSymbSeqDisplay)display);
                TransportButtons transport = new TransportButtons(player.getPlayTimer());
                panel.add(transport, BorderLayout.SOUTH);
            }
            JFrame frame = new JFrame("Test as Timeable");
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setVisible(true);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//		testChordDisplayGUI();
//		testSelectionListener();
//		testDataChangeListener();
		testAsTimeable();
	}
}
