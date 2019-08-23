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
 * Created on Dec 30, 2004
 *
 */
package de.uos.fmt.musitech.mpeg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.w3c.dom.Document;

import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.mpeg.testcases.BaroqueAlignmentTest;
import de.uos.fmt.musitech.mpeg.testcases.TestCase2_1;
import de.uos.fmt.musitech.mpeg.testcases.TestCase2_6_a;
import de.uos.fmt.musitech.mpeg.testcases.TestCase2_8;
import de.uos.fmt.musitech.performance.ScoreToPerfomance;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.structure.text.LyricsDisplay;
import de.uos.fmt.musitech.time.gui.HorizontalPositioningCoordinator;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * @author collin
 *
 */
public class Viewer extends JFrame implements ActionListener {
	ObjectPlayer player = ObjectPlayer.getInstance();
	PlayTimer timer = player.getPlayTimer();
	JLabel banner;
	
	/**
	 * From the Java Tutorial
	 */
	protected JButton makeButton(String category,
			                     String imageName,
            				     String actionCommand,
								 String toolTipText,
								 String altText) {
		//Look for the image.
		String imgLocation = "toolbarButtonGraphics/" + category + "/"
			+ imageName
			+ ".gif";
		URL imageURL = Viewer.class.getResource(imgLocation);

		//Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null) {                      //image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else {                                     //no image found
			button.setText(altText);
			//System.err.println("Resource not found: " + imgLocation);
		}

		return button;
	}
	
	JToolBar toolBar;
	void gui() {
		getContentPane().setLayout(new BorderLayout());

		toolBar = new JToolBar("Viewer Toolbar");
		getContentPane().add(toolBar, BorderLayout.PAGE_START);
		
		toolBar.add(makeButton("general", "Open24", "Open", "Open file", "Open"));
		toolBar.add(makeButton("media", "Play24", "Play", "Play file", "Play"));
		toolBar.add(makeButton("", "", "2.1", "Testcase 2.1 (Tablature)", "2.1"));
		toolBar.add(makeButton("", "", "2.5", "Testcase 2.5 (Baroque Alignment)", "2.5"));
		toolBar.add(makeButton("", "", "2.6a", "Testcase 2.6a (Modern Music)", "2.6a"));
		toolBar.add(makeButton("", "", "2.8", "Testcase 2.8 (Neume)", "2.8"));
		toolBar.add(makeButton("", "", "10", "Requirement 10", "10"));
		toolBar.add(makeButton("", "", "6", "Requirement 6", "6"));
		
		ImageIcon ii = new ImageIcon(Viewer.class.getResource("musitech.png"));
		banner = new JLabel(ii);
		getContentPane().add(banner, BorderLayout.CENTER);
	}
	
	void processFile(File file) {
		//TODO: call the method which returns a piece for the given file instead of fillPiece()
	}
	
	void clearContentPane() {
		getContentPane().removeAll();
		getContentPane().add(toolBar, BorderLayout.PAGE_START);
	}
	
	void showPiece(String command, Piece piece) {
		clearContentPane();
		NotationDisplay display = new NotationDisplay();
		display.init(piece, null, null); 
		
		NotationSystem system = display.getScorePanel().getNotationSystem();

		if ("2.5".equals(command)) {
			BaroqueAlignmentTest.fillNotationSystem(system);
		}
		else if ("2.1".equals(command)) {
			//TestCase2_1.fillNotationSystem(system);
		}
		else if ("2.8".equals(command)) {
			TestCase2_8.fillNotationSystem(system);
		}

		display.getScorePanel().setNotationSystem(system);
		//ScoreToPerfomance convert = new ScoreToPerfomance(system);
		piece.getContainerPool().add(system);
		display.updateDisplay();
		
		display.setOpaque(true);
		display.setAutoZoom(false);
		
		JScrollPane scrollPane = new JScrollPane(display);
		scrollPane.setPreferredSize(new Dimension(800, 600));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		pack();
		
		//make the piece ready for midi playback:
		player.setContainer(piece.getScore());
		timer.setContext(piece.getContext());
		
		//deserialize to "tmp/"
		piece.setScore(system);
		deserialize("tmp/SMR-TC-"+command.replace('.','-')+".xml", piece);
	}
	
	public Viewer() {
		gui();
		
		getContentPane().setBackground(Color.WHITE);
		setSize(800, 600);
		setTitle("Viewer");
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
	}
	
	private final JFileChooser fileChooser = new JFileChooser();
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		Piece piece = new Piece();
		
		if ("Open".equals(command)) {
			int returnVal = fileChooser.showOpenDialog(this);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
	            processFile(file);
	            showPiece(command, piece);
	        }
		}
		else if ("Play".equals(command)) {
			timer.reset();
			timer.start();
		}
		else if ("2.5".equals(command)) {
			BaroqueAlignmentTest.fillPiece(piece);
			showPiece(command, piece);
		}
		else if ("2.1".equals(command)) {
			TestCase2_1.fillPiece(piece);
			showPiece(command, piece);
		}
		else if ("2.6a".equals(command)) {
			NotationSystem system = (new TestCase2_6_a()).createNotationSystem();
			piece.setScore(system);
			showPiece(command, piece);
		}
		else if ("2.8".equals(command)) {
			TestCase2_8.fillPiece(piece);
			clearContentPane();
			NotationDisplay display = null;
			try {
				display = (NotationDisplay)EditorFactory.createDisplay(piece);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}
			
			NotationSystem system = display.getScorePanel().getNotationSystem();
			TestCase2_8.fillNotationSystem(system);
			Collection systems = system.splitAtLineBreaks();
			Collection lyrics = ((LyricsSyllableSequence)((NotationVoice)((NotationStaff)system.get(0)).get(0)).getLyrics().getVerse((byte)0)).splitAtLinebreaks(system.getLinebreaks());
			
			//Box vBox = new Box(BoxLayout.Y_AXIS);
			JPanel vBox = new JPanel();
			vBox.setLayout(null);
			getContentPane().add(new JScrollPane(vBox), BorderLayout.CENTER);
			
			Iterator lyricsIterator = lyrics.iterator();
			int y = 0;
			int x = 0;
			for (Iterator iter = systems.iterator(); iter.hasNext();) {
				NotationSystem splitSystem = (NotationSystem) iter.next();
				LyricsSyllableSequence splitLyrics = (LyricsSyllableSequence)lyricsIterator.next();
				NotationDisplay splitDisplay;
				LyricsDisplay lyricsDisplay;
				try {
					HorizontalPositioningCoordinator coord = new HorizontalPositioningCoordinator();
					splitDisplay = (NotationDisplay)EditorFactory.createDisplay(splitSystem);
					lyricsDisplay = (LyricsDisplay)EditorFactory.createDisplay(splitLyrics);
					splitDisplay.setAutoZoom(false);
					
					coord.registerDisplay(splitDisplay);
					coord.registerDisplay(lyricsDisplay);
					coord.doPositioning();
					
					splitDisplay.setLocation(0, y);
					y += splitDisplay.getPreferredSize().getHeight();
					x = Math.max(x, (int)splitDisplay.getPreferredSize().getWidth());
					splitDisplay.setSize(splitDisplay.getPreferredSize().width + 10, splitDisplay.getPreferredSize().height);
					vBox.add(splitDisplay);
					lyricsDisplay.setLocation(0, y);
					y += lyricsDisplay.getPreferredSize().getHeight();
					x = Math.max(x, (int)lyricsDisplay.getPreferredSize().getWidth());
					vBox.add(lyricsDisplay);
					lyricsDisplay.setVisible(true);
					lyricsDisplay.setSize(lyricsDisplay.getPreferredSize());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			vBox.setPreferredSize(new Dimension(x, y));
			vBox.setBackground(Color.WHITE);
			pack();
			//deserialize to "tmp/"
			piece.setScore(system);
			deserialize("tmp/SMR-TC-"+command.replace('.','-')+".xml", piece);
		}
		else if ("10".equals(command)) {
			BaroqueAlignmentTest.fillPiece(piece);
			FindObjects findObject = new FindObjects(piece, this);
			clearContentPane();
			getContentPane().add(findObject, BorderLayout.CENTER);
			pack();
		}
		else if ("6".equals(command)) {
			BaroqueAlignmentTest.fillPiece(piece);
			ObjectEditor editor = new ObjectEditor(this, piece);
			clearContentPane();
			getContentPane().add(editor, BorderLayout.CENTER);
			pack();
		}
	}
	
	
	public static void main(String[] args) {
		(new Viewer()).setVisible(true);
	}
	
	private void deserialize(String filename, Piece piece){
        if (filename != null) {
            
            Document doc = MusiteXMLSerializer.newMPEGSerializer().serialize(piece);
            String xml = XMLHelper.asXML(doc);
            try {
                FileWriter fw = new FileWriter(filename);
                fw.write(xml);
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            doc = XMLHelper.parse(xml);
        }
	}
}
