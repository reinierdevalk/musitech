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
package de.uos.fmt.musitech.score.kern;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.score.*;
import de.uos.fmt.musitech.score.gui.ScoreMapper;
import de.uos.fmt.musitech.score.gui.ScorePanel;
import de.uos.fmt.musitech.score.kern.KernReader;


/**
 * Class for KernReader testing. <P>
 * A kern file will be imported into a NotationSystem. After that the 
 * NotationSystem will be displayed with the help of a ScoreMapper-
 * Object in a ScorePanel. 
 * 
 * @author Alexander Luedeke
 */
public class KernReaderTest9 extends JFrame {
	
    // For mapping of a music.NotationSystem and a GIN score.
	// The ScoreMapper will display the NotationSystem in a ScorePanel
	private ScoreMapper scoreMapper = null;
    
	/**
	 * KernReaderTest constructor. 
	 * @param filename the file to process
	 */
	public KernReaderTest9(String filename) {
		super();
		
	    // Target of the conversion
		NotationSystem notationSystem;
		
		// Create the kern reader		
		KernReader kernReader = new KernReader();
		
		// Execute the import
		try {
		    URL url = kernReader.getClass().getResource(filename);
		    notationSystem = kernReader.getNotationSystem(url);
		} catch (Exception e){
			throw new Error(e);
		}

		// To display the NotationSystem we need a ScorePanel
		ScorePanel scorePanel = new ScorePanel();
		
	    // For mapping of a music.NotationSystem and a GIN score.
		// The ScoreMapper will display the NotationSystem in a ScorePanel
		scoreMapper = new ScoreMapper(scorePanel, notationSystem);
		
		// Add a listener to the JFrame
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});

		// Config the ScorePanel and add it to the JFrame
		scorePanel.setOpaque(true);
		scorePanel.setAutoZoom(false);
		//scorePanel.setSelectionClass("de.uos.fmt.musitech.gui.score.Clef");
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scorePanel, BorderLayout.CENTER);
		
		// Add a button to the ScorePanel
		JButton actionButton = new JButton(new Action1(notationSystem, scorePanel));
		actionButton.setText("go");
		getContentPane().add(actionButton, BorderLayout.SOUTH);
		
		pack();
		setSize(1000, 400);
		setTitle("imported form '"+filename+"'");
	}
	
	/**
	 * Action of the button. The mapper will create a view and will repaint.
	 */
	class Action1 extends AbstractAction {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		private NotationSystem notationSystem;;
		private ScorePanel scorePanel;
		
		public Action1(NotationSystem notationSystem, ScorePanel scorePanel) {
			this.notationSystem = notationSystem;
			this.scorePanel = scorePanel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			scoreMapper.createView(scorePanel);
			((JComponent)getContentPane()).revalidate();
			repaint();
		}
	}

	/**
	 * The main-method of KernReaderTest
	 * @param argv
	 */
	public static void main(String[] argv) {
	    
	    // Create the KernReaderTest object
	    KernReaderTest9 kernReaderTest = null;
	    
		// Name of the file to import
		String filename = "musedata//corelli-op5n01//kern//op5n1-1a.krn";

		try {
		    // Process the command line argument or the default file
		    if (argv.length > 0) {
			    // Create the KernReaderTest object and start the import
				kernReaderTest = 
				    new KernReaderTest9(argv[0]);	       
		    } else {
			    // Create the KernReaderTest object and start the import
				kernReaderTest = 
				    new KernReaderTest9(filename);
		    }
		    kernReaderTest.setVisible(true);
		} catch (Exception exception) {
			System.out.println("KernReaderTest: exception");
			exception.printStackTrace();
			return;
		}
	}
}
	
