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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import antlr.CharStreamException;
import antlr.RecognitionException;
import antlr.TokenStreamException;

import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.linear.Part;
import de.uos.fmt.musitech.performance.gui.PianoRollContainerDisplay;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class to convert a **kern file into a NotationSystem object. <BR>
 * 
 * Usage-example:
 * <PRE>
 * 
 * KernReader kernReader = new KernReader();
 * piece = kernReader.getNotationSystem(URL);
 * </PRE>
 * 
 * @see de.uos.fmt.musitech.score.kern.KernReaderTest
 * @author Alexander Luedeke
 */
public class KernReader {

	//Name and path of the kern file
	private String fileName;

	//The kern lexer
	private KernLexer kernLexer = null;
	//The kern parser
	private KernParser kernParser = null;
	
	/**
	 * KernReader constructor. 
	 */
	public KernReader() {
	    //
	}
	
	private static boolean done = false;

	public void uponEOF() throws TokenStreamException, CharStreamException {
		done=true;
	}

	/**
	 * Starts the import of the kern file and returns a new NotationSystem object. If the 
	 * given file is not in kern format the method throws a WrongArgumentException.
	 * @param url java.net.URL
	 * @return the new NotationSystem object
	 */
	public NotationSystem getNotationSystem(URL url) {

		// Store the fileName
		fileName = url.getFile();
		
		// Input stream to read the file into the lexer
		FileInputStream inputStream = null;

	    System.out.println("KernReader: The name of the processed kern file is: "+fileName);
	    try {
	        // Create a input stream to read the file into the lexer
	    	inputStream = new FileInputStream(fileName);
	    } catch (FileNotFoundException fNFE) {
	        System.out.println("KernReader: file (" + fileName + ") not found");
	        fNFE.printStackTrace();
	    	return null;
	    }

		try {
			// Create the lexer class
            kernLexer = new KernLexer(inputStream);
            kernLexer.setFilename(url.getFile());
            // Create the parser class
            kernParser = new KernParser(kernLexer);
			kernParser.setFilename(url.getFile());
		    // Create some new musical objects in the KernParser
            kernParser.init();
			/**
			 * Parse the kern input file
			 */
			kernParser.expr();
			
            // Process the comment of the kern file
            System.out.println("\n[Kern file comment:\n"+kernLexer.getComment()+"]");
		} catch (TokenStreamException tSE) {
		    System.out.println(
				    ">>KernReader: TokenStreamException. URL ("
				    + url
				    + ")");
		    tSE.printStackTrace();
		    return null;
		} catch (RecognitionException rNE) {
		    System.out.println(
				    ">>KernReader: RecognitionException. URL ("
				    + url
				    + ")");
		    rNE.printStackTrace();
		    return null;
		}

		/**
		 * Finish the import
		 */
		// Add the names of the NotationStaffs to the created parts
		for (int i=0; i<kernParser.parts.size(); i++) {
		    if (kernParser.parts.get(i) != null && kernParser.notationStaffs.get(i) != null)
		        ((Part) kernParser.parts.get(i))
		        	.setName(((NotationStaff) kernParser.notationStaffs.get(i)).getName());
		}
		
		// Add the created parts to the ContainerPool of the Piece object
		for (int i=0; i<kernParser.parts.size(); i++) {
		    if (kernParser.parts.get(i) != null)
		        kernParser.piece.getContainerPool()
		        	.add((Part) kernParser.parts.get(i));
		}

		// Sort the notationStaffs because, the kern format stores 
		// '*staff' columns in reverse order
		kernParser.sortStaffs();
		
		// Add the created NotationVoices to their NotationStaff
		// Note: Voices with equal staff name will be added to the same NotationStaff
		NotationVoice lastVoice = null; 
		for (int i=0; i<kernParser.notationVoices.size(); i++) {
		    if (kernParser.notationVoices.get(i) != null && kernParser.notationStaffs.get(i) != null) {
		        // TODO merge voices of the same staff
		        if (lastVoice == null 
		                || (((NotationVoice) kernParser.notationVoices.get(i))
	                        	.getName().matches(""))
		                || !(((NotationVoice) kernParser.notationVoices.get(i))
		                        	.getName().matches(lastVoice.getName()))
		        	) {
		            	((NotationStaff) kernParser.notationStaffs.get(i))
		        			.add((NotationVoice) kernParser.notationVoices.get(i));
		            	if (((NotationVoice) kernParser.notationVoices.get(i))
		            	        .getName().matches("staff.*"))
		            	    lastVoice = (NotationVoice) kernParser.notationVoices.get(i);
		        } else {
		            // Add the current voice to the last NotationStaff.
	            	((NotationStaff) kernParser.notationStaffs.get(i-1))
	            		.add((NotationVoice) kernParser.notationVoices.get(i));
		            lastVoice = null;
		            kernParser.notationStaffs.set(i, null);
		            kernParser.notationVoices.set(i, null);
		        }
		    }
		}
		
		// Add the created NotationStaffs to the NotationSystem
		for (int i=0; i<kernParser.notationStaffs.size(); i++) {
		    if (kernParser.notationStaffs.get(i) != null)
		    	kernParser.notationSystem
		    		.add((NotationStaff) kernParser.notationStaffs.get(i));
		}
		
		// We must add the linebreaks afterwards, so the system can check if they are valid
		for (int i = 0; i < kernParser.linebreaks.size(); i++) {
		    kernParser.notationSystem.addLinebreak((Rational) kernParser.linebreaks.get(i));
		}
		
		return kernParser.notationSystem;
	}
	
	/**
	 * Just a demonstration of this class.
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {

		// Create the KernReader object
		KernReader kernReader = new KernReader();
		// Name of the file to process
		//String filename = "import_Kern_1.krn";
		//String filename = "import_Kern_2_2.krn";
		String filename = "import_Kern_4_5.krn";

		NotationSystem notationSystem = null;

		try {
			/**
			 * The method getNotationSystem will start the kern import
			 */
		    notationSystem =
				kernReader.getNotationSystem(
					kernReader.getClass().getResource(filename));
		} catch (Exception exception) {
			System.out.println("KernReader: exception");
			exception.printStackTrace();
			return;
		}

		// Just for debugging
		//System.out.println(kernReader.getInfos());

		System.out.println(">>>terminated. And now for testing - some infos about"
		        +" the notation system's container pool:");
		
		Container aContainerPool = notationSystem.getContext().getPiece().getContainerPool();
	
		Container currentContainer = null;

		try {
			for (int counter = 0; counter < aContainerPool.size(); counter++) {
				if (aContainerPool.get(counter) instanceof Container) {
					currentContainer = (Container) aContainerPool.get(counter);
					System.out.println(
						"Container name=" + currentContainer.getName());
					System.out.println(
						"Container size=" + currentContainer.size());
				}

				if (currentContainer.size() != 0) {

					javax.swing.JFrame frame = new javax.swing.JFrame();

					PianoRollContainerDisplay display;

					display = new PianoRollContainerDisplay(currentContainer);

					display.setAutoHScale(true);
					// Calculate a optimal horizontal scaling
					if (1.0/display.getPreferredHScale() > 0)
					    display.setMicrosPerPix(1.0/display.getPreferredHScale());
					
					frame.setTitle(currentContainer.getName());
					//DisplayTyp is PIANOROLL
					display.setDisplayType(0);
					display.setSize(800,300);
					display.setDisplayRangeMax(110);
					display.setDisplayRangeMin(20);
					frame.getContentPane().setBackground(java.awt.Color.white);
					frame.getContentPane().add("Center", display);
					frame.setSize(display.getSize());
					frame.setLocation(counter * 25 + 10, counter * 25 + 15);
					frame
						.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosing(
							java.awt.event.WindowEvent e) {
							System.exit(0);
						};
					});
					frame.setVisible(true);
				}
			}

			/**
			 * Display the note pool
			 */
			Container notePool = notationSystem.getContext().getPiece().getNotePool();

			System.out.println("Notepool name=" + notePool.getName());
			System.out.println("Notepool size=" + notePool.size());

			if (notePool.size() != 0) {
				javax.swing.JFrame nPFrame = new javax.swing.JFrame();

				PianoRollContainerDisplay nPDisplay;

				nPDisplay = new PianoRollContainerDisplay(notePool);

				nPDisplay.setAutoHScale(true);
				// Calculate a optimal horizontal scaling 
				if (1.0/nPDisplay.getPreferredHScale() > 0)
				    nPDisplay.setMicrosPerPix(1.0/nPDisplay.getPreferredHScale());
				nPFrame.setTitle(notePool.getName());
				//DisplayTyp is PIANOROLL
				nPDisplay.setDisplayType(0);
				nPDisplay.setSize(800,300);
				nPDisplay.setDisplayRangeMax(110);
				nPDisplay.setDisplayRangeMin(20);
				nPFrame.getContentPane().setBackground(java.awt.Color.white);
				nPFrame.getContentPane().add("Center", nPDisplay);
				nPFrame.setSize(nPDisplay.getSize());
				nPFrame.setLocation(5, 50);
				nPFrame.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					};
				});
				nPFrame.setVisible(true);
			}
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of KernReader");
			exception.printStackTrace(System.out);
		}
	}
}