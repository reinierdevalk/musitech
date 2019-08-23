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
 * Created on 17.08.2004
 */
package de.uos.fmt.musitech.audio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * @author Nicolai Strauch
 */
public class TestAFOPlayerPerformance {
	
	static AFOPlayer afoPlayer = new AFOPlayer();
	
	static int allTestcounter = 0;
	
	public static void allTest(){
		int i = allTestcounter;
		System.out.println("**>**>**>**>**>**>**>**>**>**>**>**** starting allTest "+i+"********************");
		//PlayTimer playTimer = player.getPlayTimer();
		//playTimer.reset();
		afoPlayer.reset();
		//player.setContainer((Container) editObj);
		//playTimer.start();
		afoPlayer.start();
		System.out.println("**<**<**<**<**<**<**<**<**<**<**<**** ending allTest "+i+" ******************** allTestCounter: "+allTestcounter);
		allTestcounter++;
	}
	
	
	
	
	public static void main(String a[]){
		new ButtonFrame("Button"){
			public void doByButtonPress() {
				TestAFOPlayerPerformance.allTest();
			}};
	}
	
	
	public static abstract class ButtonFrame extends JFrame {
		
		public ButtonFrame(String frameName){
			super(frameName);
			JPanel panel = new JPanel();
			JButton button = new JButton("Press");
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					doByButtonPress();
				}});
			panel.add(button);
			getContentPane().add(panel);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			pack();
			setVisible(true);
		}
		
		public abstract void doByButtonPress();
		
	}

}
