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
 * Created on 20.05.2003
 */
package de.uos.fmt.musitech.utility.gui.scaling;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import junit.framework.TestCase;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.MusicCollection;
import de.uos.fmt.musitech.performance.midi.MidiReader;
import de.uos.fmt.musitech.score.NotationDisplay;

/**
 * A JUnit test for ScaledComponent...
 * @author tobias widdra
 */
public class TestComponentScaling extends TestCase {

	static JFrame wScaled = new JFrame("Scaled");
	static JFrame wUnscaled = new JFrame("Window with UNscaled Component");
	static ScaledComponent decorator;
	static JPanel panelU;	// panel for "unscaled" componentes
	static JPanel panelS;	// panel for "scaled" components (to put into decorator)
	static JButton buttonS;
	static JTextField textfieldS;  

	/**
	 * Building up components (panelS, panelU, buttonS, textfieldS).
	 */
	static void init() {
		wScaled.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		// panel 1: "unscaled" button and textfield
		panelU = new JPanel();
		
		JButton button1 = new JButton("Unscaled");
		button1.addFocusListener(new FocusOutput());
		button1.addActionListener(new ActionOutput());
		button1.setMnemonic('u');
		button1.setName("Unscaled Button");
		panelU.add(button1);
		
		JTextField textfield1 = new JTextField("edit me");
		textfield1.addFocusListener(new FocusOutput());
		textfield1.addActionListener(new ActionOutput());
		textfield1.setName("Unscaled Textfield");
		panelU.add(textfield1);
		
		wScaled.getContentPane().setLayout(new BorderLayout());
		wScaled.getContentPane().add(panelU,BorderLayout.WEST);
		
		// panel 2 (to put into decorator): "scaled" button and textfield
		panelS = new JPanel();
		panelS.setLayout(new BorderLayout());

		textfieldS = new JTextField("edit me");
		textfieldS.setName("Scaled Textfield");
		textfieldS.addFocusListener(new FocusOutput());
		textfieldS.addActionListener(new ActionOutput());
		textfieldS.setColumns(7);
		panelS.add(textfieldS,BorderLayout.CENTER);
		
		buttonS = new JButton("Scaled");
		buttonS.addFocusListener(new FocusOutput());
		buttonS.addActionListener(new ActionOutput());
		buttonS.addMouseListener(new MouseOutput());
		buttonS.setMnemonic('s');
		buttonS.setName("Scaled Button");
		panelS.add(buttonS,BorderLayout.WEST);
	}


	/**
	 * Panel (with textfield and button) in decoraor: works.
	 *
	 */
	static void testPanelInDecorator() {
		// build scaled component (containing panelS)
		decorator = new ScaledComponent(panelS,3);	
		decorator.addFocusListener(new FocusOutput());
		
		wScaled.getContentPane().add(decorator,BorderLayout.CENTER);
		wScaled.pack();
//		wScaled.setSize(500,300);
		wScaled.show();
	}

	/**
	 * Textfield in decorator: works.
	 *
	 */
	static void testTextfieldInDecorator() {
		// build scaled component (containing panelS)
		decorator = new ScaledComponent(textfieldS);	

		wScaled.getContentPane().add(decorator,BorderLayout.CENTER);
//		wScaled.pack();
		wScaled.setSize(500,300);
		wScaled.show();
	}


	/**
	 *
	 */
	static void testDecoratorInDecorator() {
		
		// build second decorator
		JButton button3 = new JButton("button of inner decorator");
		button3.addFocusListener(new FocusOutput());
		button3.addActionListener(new ActionOutput());
		button3.setMnemonic('i');
		button3.setName("button of inner decorator");

		ScaledComponent decorator2 = new ScaledComponent(button3,2);

		// add inner decorator to panelS
		panelS.setLayout(new BorderLayout());
		panelS.add(textfieldS,BorderLayout.EAST);
		panelS.add(decorator2,BorderLayout.CENTER);
		panelS.add(buttonS,BorderLayout.WEST);
				
		// build outer decorator (containing panelS)
		decorator = new ScaledComponent(panelS,2);

		wScaled.getContentPane().add(decorator,BorderLayout.CENTER);
//		wScaled.pack();
		wScaled.setSize(500,300);
		wScaled.show();		
	}

	/**
	 *
	 */
	static void testBorderLayout() {

		// build scaled component (containing panelS)
		decorator = new ScaledComponent(panelS,2);	
		
		// clear window and add new panels
		wScaled.getContentPane().removeAll();
		wScaled.getContentPane().setLayout(new BorderLayout());
		wScaled.getContentPane().add(panelU,BorderLayout.CENTER);				
		wScaled.getContentPane().add(decorator,BorderLayout.EAST);
		
		System.out.println("FCR: "+decorator.getFocusCycleRootAncestor());
				
//		wScaled.pack();
		wScaled.setSize(500,300);
		wScaled.show();
	}
	
	public static void testRecursiveChildren() {

		JButton button3 = new JButton("inner panel");
		button3.addFocusListener(new FocusOutput());
		button3.addActionListener(new ActionOutput());
		button3.setMnemonic('i');
		button3.setName("inner panel");

		JPanel panel3 = new JPanel();
		panel3.add(button3);
		
		panelS.add(panel3, BorderLayout.EAST);
		decorator = new ScaledComponent(panelS);
		decorator.setRepaintingTime(1000);	
		
		wScaled.getContentPane().setLayout(new BorderLayout());
		wScaled.getContentPane().add(panelU,BorderLayout.CENTER);				
		wScaled.getContentPane().add(decorator,BorderLayout.EAST);
		
		System.out.println("FCR: "+decorator.getFocusCycleRootAncestor());
				
//		wScaled.pack();
		wScaled.setSize(500,300);
		wScaled.show();
		
	}
	
	public static void testTime() {

		// build scaled component (containing panelS)
		decorator = new ScaledComponent(panelS);	

		decorator.setRepaintingTime(1000);
		decorator.setZoom(5);

		wScaled.getContentPane().add(decorator,BorderLayout.CENTER);
		wScaled.pack();
//		wScaled.setSize(500,300);
		wScaled.show();		
	}
	
	public static void testRemoveFromThread() {
		decorator = new ScaledComponent(panelS,3);	

		decorator.removeFromRepaintingThread();
		decorator.addToRepaintingThread();
		
		wScaled.getContentPane().add(decorator,BorderLayout.CENTER);
		wScaled.pack();
		wScaled.show();		
		
	}
	
	public static void main(String argv[]) {
		init();
//		testPanelInDecorator();
//		testTextfieldInDecorator();
//		testDecoratorInDecorator();
//		testBorderLayout();
//		testRecursiveChildren();
//		testTime();
//		testRemoveFromThread();
		testMusicTree();
	}
	
	public static void testMusicTree() {

//		MusicTreeView musicTree = new MusicTreeView();

		MusicCollection workCollection = new MusicCollection();
		workCollection.setName("Test Collection");

		//		Piece work = new Piece();
		//		music.setSampleData();

		try {
			Piece work2=null;
			work2 = new MidiReader().getPiece(NotationDisplay.class.getResource("DadmeAlbricias.mid"));
			workCollection.add(work2);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		try {
			Piece work1 = new MidiReader().getPiece(new File("src/de/uos/fmt/musitech/bach.mid").toURL());
			workCollection.add(work1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

//		musicTree.setMObject(workCollection, "test");

//		decorator = new ScaledComponent(musicTree,2);
		
		wScaled.getContentPane().setLayout(new BorderLayout());
		wScaled.getContentPane().add(panelU,BorderLayout.CENTER);				
		wScaled.getContentPane().add(decorator,BorderLayout.EAST);
		wScaled.pack();
		wScaled.show();
		
	}


	/**
	 * Prints name of source which has focus gained.
	 * @author tobi
	 */	
	static class FocusOutput extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			if (e.getSource() instanceof java.awt.Component)
				System.out.println(((java.awt.Component)e.getSource()).getName()+" has got the focus");
			else System.out.println(e.getSource()+" has got the focus");
		}
	}
	
	/**
	 * Prints action.
	 * @author tobi
	 */
	static class ActionOutput implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("\t"+e);
//			if (e.getSource() instanceof Container) {
//				System.out.println("\tFocusTraversalPolicy is: "+ ((Container)e.getSource()).getFocusTraversalPolicy());
//				System.out.println("\tFocusCycleRootAncestor is: "+ ((Container)e.getSource()).getFocusCycleRootAncestor());
//			}
		}
		
	}

	static class MouseOutput extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			System.out.println("\t"+e);
		}
		
	}


}
