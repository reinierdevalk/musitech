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
package de.uos.fmt.musitech.score;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.score.ScoreEditor.Mode;
import de.uos.fmt.musitech.score.mpegsmr.XsmReader;
import de.uos.fmt.musitech.scoreeditor.toolbar.ToolBarPanel;
import de.uos.fmt.musitech.utility.ExtensionFilter;
import de.uos.fmt.musitech.utility.gui.interact.ActionUtil;
import de.uos.fmt.musitech.utility.gui.swing.DirJFileChooser;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * This class is used to display a ScoreEditor in a frame. It provides a menu
 * for loading SMR/MusiteXML objects. 
 * TODO Loading MIDI files 
 * TODO Saving MPEG-SMR, MusiteXML, MIDI, (MusicXML?)
 * 
 * @author Jens Wissmann 
 */
public class ScoreEditorFrame extends JFrame {

	private static FileFilter smrFilter = new ExtensionFilter(
		"MPEG SMR Files", true, "xsm");
	private static FileFilter mtxFilter = new ExtensionFilter(
		"Musitech Files", true, "mtx", "xml");

	/**
	 * this action is used load and display an SMR object
	 */
	private class SMRLoadingAction extends AbstractAction {

		private Container awtContainer;

		public SMRLoadingAction(Container argAwtContainer) {
			this.awtContainer = argAwtContainer;
			ActionUtil.configureAction(this, KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK, true), 'O', "Open");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new DirJFileChooser(".");
			fc.addChoosableFileFilter(mtxFilter); // add the SMR filter
													// available
			fc.setFileFilter(smrFilter); // add and set the MTX Filter
			int showRet = fc.showOpenDialog(ScoreEditorFrame.this);

			if(showRet != JFileChooser.APPROVE_OPTION)
				return;
			
			File smrFile = fc.getSelectedFile();

			// read MTX/XML file
			Object musitechObject = ObjectCopy.readXML(smrFile);
			Piece piece;
			if (musitechObject != null && musitechObject instanceof Piece)
				piece = (Piece) musitechObject;
			else
				piece = XsmReader.readXSM(smrFile.getParentFile());

			// display piece
			if (piece != null) {

				NotationSystem nsys = piece.getScore();
				// NotationDisplay.processRenderingHints(nsys);

				// create score editor
				ScoreEditor scoreEd = new ScoreEditor(nsys);
				scoreEd.setModus(Mode.SELECT_AND_EDIT);
				scoreEd.setBackground(Color.WHITE);

				// container.setBackground(Color.WHITE);
				awtContainer.setLayout(new BorderLayout());

				// display score editor
				awtContainer.removeAll();
				awtContainer.add(new JScrollPane(scoreEd));

				awtContainer.add(new ToolBarPanel(scoreEd), BorderLayout.NORTH);
				
				awtContainer.doLayout();
			} else {
				JOptionPane.showConfirmDialog(awtContainer, "Could not load piece from file.");
			}
		}
	}

	/**
	 * this action is used load and display an SMR object
	 */
	private class NewPieceAction extends AbstractAction {

		private Container awtContainer;

		public NewPieceAction(Container argAwtContainer) {
			this.awtContainer = argAwtContainer;
			ActionUtil.configureAction(this, KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK, true), 'N', "New");
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			try { 
			Piece piece = new Piece();


				piece.generateScore();
				NotationSystem nsys = piece.getScore();
				// NotationDisplay.processRenderingHints(nsys);

				// create score editor
				ScoreEditor scoreEd = new ScoreEditor(nsys);
				scoreEd.setModus(Mode.SELECT_AND_EDIT);
				scoreEd.setBackground(Color.WHITE);
				scoreEd.getScoreManipulator().appendMeasure();
			

				// container.setBackground(Color.WHITE);
				awtContainer.setLayout(new BorderLayout());

				// display score editor
				awtContainer.removeAll();
				awtContainer.add(new JScrollPane(scoreEd));

				awtContainer.add(new ToolBarPanel(scoreEd), BorderLayout.NORTH);

				awtContainer.doLayout();
			} catch(RuntimeException re) {
				JOptionPane.showConfirmDialog(awtContainer, "Could not create new piece object.");
			}
		}
	}

	
	/**
	 * constructor
	 */
	public ScoreEditorFrame() {
		this.setSize(new Dimension(640, 480));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		createContentPane();
		createMenubar();
	}

	private void createContentPane() {
		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		this.setContentPane(contentPane);
	}

	/**
	 * create the menubar (currently only a file-open item is created)
	 */
	private void createMenubar() {
		JMenuBar menubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menubar.add(fileMenu);

		JMenuItem loadItem = new JMenuItem(new SMRLoadingAction(this
			.getContentPane()));
		fileMenu.add(loadItem);
		JMenuItem newPieceItem = new JMenuItem(new NewPieceAction(this
			.getContentPane()));
		fileMenu.add(newPieceItem);
		this.setJMenuBar(menubar);
	}

	/**
	 * For testing.
	 * @param args
	 */
	public static void main(String[] args) {
		new ScoreEditorFrame().setVisible(true);
	}

}
