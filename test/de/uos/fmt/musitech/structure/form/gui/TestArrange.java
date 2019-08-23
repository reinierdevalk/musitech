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
 * Created on 15.03.2004
 */
package de.uos.fmt.musitech.structure.form.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Iterator;

import javax.swing.JFrame;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.framework.time.TransportButtons;
import de.uos.fmt.musitech.score.gui.ScorePanel;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Jan and Tillman
 */
public class TestArrange {

	static ArrangementPanel arrPanel;
	static PlayTimer playTimer;
	static ObjectPlayer objPlayer;
	static JFrame frame = new JFrame();

	
	static public void showWindow() {
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(arrPanel);
		frame.setLocation(200, 150);
		frame.pack();
		frame.setSize(500, 300);
		frame.show();
	}

	public static Container makeCont() {
		Container cont = new BasicContainer();
		cont.setName("Sonata");
		Container cont1 = new BasicContainer();
		cont1.setName("Exposition");
		cont1.setRenderingHints(new RenderingHints());
		cont1.getRenderingHints().registerHint("color" , "FF0000");
		cont.add(cont1);
		
		cont1.add(new PerformanceNote(0, 500000));
		cont1.add(new PerformanceNote(6000000, 1000000));
		Container cont11 = new BasicContainer();
		cont1.add(cont11);
		cont11.setName("Theme 1");
		cont11.setRenderingHints(new RenderingHints());
		cont11.getRenderingHints().registerHint("color" , "0000FF");
		Container cont111 = new BasicContainer();
		cont11.add(cont111);
		cont111.setName("Motif a");
		cont111.add(new PerformanceNote(500000, 500000));
		cont111.add(new PerformanceNote(1000000, 500000));
		Container cont112 = new BasicContainer();
		cont11.add(cont112);
		cont112.setName("Motif b");
		cont112.add(new PerformanceNote(1500000, 500000));
		cont112.add(new PerformanceNote(2000000, 500000));
		Container cont12 = new BasicContainer();
		cont12.setName("Theme 2");
		cont1.add(cont12);
		cont12.add(new PerformanceNote(3500000, 500000));
		cont12.add(new PerformanceNote(4000000, 500000));
		Container cont121 = new BasicContainer();
		cont12.add(cont121);
		cont121.setName("Motif c");
		cont121.add(new PerformanceNote(3500000, 1000000));
		cont121.add(new PerformanceNote(4000000, 100000));
		Container cont122 = new BasicContainer();
		cont12.add(cont122);
		cont122.setName("Motif d");
		cont122.add(new PerformanceNote(4500000, 500000));
		cont122.add(new PerformanceNote(5000000, 500000));
//		Container cont1221 = new BasicContainer();
//		cont1221.setName("Test");
//		cont1221.add(new PerformanceNote(4800000, 500));
//		cont1221.add(new PerformanceNote(3000000, 400));
//		cont122.add(cont1221);
		NoteList nl = new NoteList();
//		nl.setName("NoteList");
		nl.add(new ScoreNote(new ScorePitch(60), new Rational(5), new Rational(1)));
//		cont112.add(nl);
		
		

		Container cont13 = new BasicContainer();
		cont1.add(cont13);
		cont13.setName("Transition");
		cont13.add(new PerformanceNote(5500000, 500000));
		cont13.add(new PerformanceNote(6500000, 500000));

		Container cont2 = new BasicContainer();
		cont2.setName("Development");
		cont.add(cont2);
		Container cont21 = new BasicContainer();
		cont21.setName("Variations on Motif a");
		cont2.add(cont21);
		cont21.add(new PerformanceNote(7000000, 500000));
		cont21.add(new PerformanceNote(10000000, 500000));
		return cont;
	}

	public static void setPlayTimer() {
		playTimer = PlayTimer.getInstance();
		arrPanel.setPlayTimer(playTimer);
		TransportButtons transbutt = new TransportButtons(playTimer);
		playTimer.stopAt(5000000);
//		JFrame frame2 = new JFrame();
//		frame2.getContentPane().setLayout(new BorderLayout());
//		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(BorderLayout.SOUTH,transbutt);
//		frame2.setLocation(200, 450);
//		frame2.pack();
//		frame2.setSize(200, 100);
//		frame2.show();
		frame.pack();
	}

	public static void scorePanel() {
		ScorePanel scorePanel = new ScorePanel();
		JFrame frame3 = new JFrame();
		frame3.getContentPane().setLayout(new BorderLayout());
		frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame3.getContentPane().add(scorePanel);
		frame3.setLocation(400, 450);
		frame3.pack();
		frame3.setSize(200, 100);
		frame3.show();

	}
	
	public static void objectPlayer(){
		objPlayer = ObjectPlayer.getInstance();
		objPlayer.setContainer(makeCont());
		objPlayer.setPlayTimer(playTimer);
	}

	public static void main(String[] args) {
		arrPanel = new ArrangementPanel(makeCont());
		objectPlayer();
//		scorePanel();
		showWindow();
		setPlayTimer();
	}

	/**
	 * Search rcursively for a display belonging to a specific containers.
	 * @param cad the Display to start with.
	 * @param cont the containers to look for
	 * @return the diplay showing the containers, null if none was found.
	 */
	static private ContainerArrangeDisplay findRecursive(
		ContainerArrangeDisplay cad,
		Container cont) {
		// test if cad is the sought for display.
		if (cad.getContainer() == cont)
			return cad;
		// make a depth first seach through the
		for (Iterator iter = cad.partPanels.iterator(); iter.hasNext();) {
			PartPanel pp = (PartPanel) iter.next();
			Component comps[] = pp.getComponents();
			for (int i = 0; i < comps.length; i++) {
				if (comps[i] instanceof ContainerArrangeDisplay) {
					ContainerArrangeDisplay found =
						findRecursive((ContainerArrangeDisplay) comps[i], cont);
					if (found != null)
						return found;
				}
			}
		}
		// search was not successful;
		return null;
	}
}
