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
 * Created on May 20, 2003
 */
package de.uos.fmt.musitech.score.epec;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.score.*;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.score.epec.EpecParser;
import de.uos.fmt.musitech.score.gui.ScoreMapper;
import de.uos.fmt.musitech.score.gui.ScorePanel;


/**
 * @author collin
 */
public class EpecParserTest extends JFrame {
	
	ScoreMapper mapper;
	
	public static void main(String[] argv) {
		(new EpecParserTest()).setVisible(true);
	}
	
	public EpecParserTest() {
		super();
		Context context = new Context(new Piece());
		NotationSystem system = new NotationSystem(context);
		EpecParser parser = new EpecParser();
		try {
			
//			parser.run(system, "T4/4 4 ''g-- 'a-- 4c-- 16c-- 16c-- 8d++ | 4c--de(f linebreak | c+)c+c+c+ 8[ccdd][geff] linebreak | 4cccc aaa bbb " + ";;" +
//			                   "     4 bb,c,c | 'f--eg(h             f)fff 2'''cc                       4'ffff (ab)");
			//parser.run(system, "T4/4 4 ''g-- 'a-- 4''c-- 16,c-- 16''c-- 8'd++ | 4''c--'def ;;" +
			//				   "     4 bb'''c8cc | 4'f--egh");
			//parser.run(system, "4. b 8 b 4 b b ;; 4. b 8 b 2 b ;; 4 rb 4. b 8b");
			//parser.run(system, "4. b 8 b 4 b b ;; 4 bb 4. b 8a");
			parser.run(system, "'c");
		    //parser.run(system, "2'a''d|c'a|g+4ab|2''(c8['c)(dc),b]|4'a");
			//parser.run(system, "{d+f+a+''c+}");
			//parser.run(system, "8cccc cccc|aaaa aaaa|gggg gggg|rddd rddd | cccc cccc | aaaa aaaa");
			//parser.run(system, "Cg aaaa linebreak | aaaa");
			//parser.run(system, "gggggggg;2(,b4'aa2,g)");
			system.prepareForScore();
			//((NotationStaff)system.get(0)).setClefType((char)0);
			//system.getContext().getWork().setMetricalTimeLine(null);
			
//			parser.run(system, "T4/4 {af''c'}{af''c'}|(aaaa)8<4:ccc>4ccc8[cccc][cccc]4bbb8b4bbb8b|aa ");
		} catch (Exception e){
			throw new Error(e);
		}
		
		/*
		 NotationStaff firstStaff = (NotationStaff)system.get(0);
		NotationVoice firstVoice = (NotationVoice)firstStaff.get(0);
		NotationChord lastChord = (NotationChord)firstVoice.get(firstVoice.size() - 1);
		lastChord.setDuration(new Rational(15,16));
		*/
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		ScorePanel spanel = new ScorePanel();
		spanel.setOpaque(true);
		spanel.setAutoZoom(false);
		//spanel.setSelectionClass("de.uos.fmt.musitech.gui.score.Clef");
		
		getContentPane().setLayout(new BorderLayout());
		
		NotationDisplay display = null;
		try {
			display = (NotationDisplay)EditorFactory.createDisplay(system, null, "Notation");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		display.setAutoZoom(false);
		display.setBorder(BorderFactory.createTitledBorder("Notation"));
		getContentPane().add(display, BorderLayout.NORTH);
		
		getContentPane().add(spanel, BorderLayout.CENTER);

		//mapper = new ScoreMapper(spanel, system, true);//, 400, 600);
		
		JButton actionButton = new JButton(new Action1(system, spanel));
		actionButton.setText("go");
		getContentPane().add(actionButton, BorderLayout.SOUTH);
		pack();
		//setSize(800, 400);
	}
	
	class Action1 extends AbstractAction {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		private NotationSystem system;
		private ScorePanel spanel;
		
		public Action1(NotationSystem system, ScorePanel spanel) {
			this.spanel = spanel;
			this.system = system;
		}
		
		public void actionPerformed(ActionEvent e) {
			mapper.createView(spanel);
			((JComponent)getContentPane()).revalidate();
			repaint();
		}
	}
}
	
