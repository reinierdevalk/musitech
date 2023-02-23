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
 * File EpecPrserTest2
 * Created on May 20, 2003
 */

package de.uos.fmt.musitech.score.epec;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import de.uos.fmt.musitech.data.score.*;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.score.gui.ScoreMapper;
import de.uos.fmt.musitech.score.gui.ScorePanel;

/**
 * @author collin
 */
public class EpecParserTest2 extends JFrame {
	String epecString = "T4/4 {,,cf''''c'}{af''c'}|(aaaa)8<4:ggg>4ccc8[cccc][cccc]4bbb8b4bbb8b|aa ";
	JTextArea area;
	Context context;
	NotationSystem system;	
	ScorePanel spanel;
	ScoreMapper mapper;
	
	public EpecParserTest2() {
		super();

		context = new Context(new Piece());
		system = new NotationSystem(context);

		try {
			/*
			parser.run(system, "T4/4 aa cc | cde(f linebreak | c)c cc" + ";;" +
			                   "     bb dd | fegh            ff ff");
			*/
			//parser.run(system, "T4/4 {af''c'}{af''c'}|(aaaa)8<4:ccc>4ccc8[cccc][cccc]4bbb8b4bbb8b|aa ");
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
			@Override
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		spanel = new ScorePanel();
		spanel.setAutoZoom(false);
		//spanel.setSelectionClass("de.uos.fmt.musitech.gui.score.Tuplet");
		
		area = new JTextArea(epecString);
		JButton button = new JButton(new DrawAction("draw", this));

		mapper = new ScoreMapper(spanel, system);
		(new DrawAction("draw", this)).actionPerformed(null);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(spanel,BorderLayout.CENTER);
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(2,1));
		getContentPane().add(inputPanel,BorderLayout.SOUTH); 
		inputPanel.add(area);
		inputPanel.add(button);

//		button.setSize(800,50);
//		area.setSize(800,200);
//		spanel.setSize(800,1000);
		this.pack();
		this.setSize(800,500);
		

	}
	
	public static void main(String argv[]) {
		(new EpecParserTest2()).setVisible(true);
	}
	
	class DrawAction extends AbstractAction {
		JFrame parent;
		
		public DrawAction(String s, JFrame parent) {
			super(s);
			this.parent = parent;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			system = new NotationSystem(context);
			EpecParser parser = new EpecParser();

			try {
				parser.run(system, area.getText());
			} catch (Exception ex) {
				throw new Error(ex);
			}
			mapper.createView(spanel);
			((JComponent)getContentPane()).revalidate();
			repaint();
		}
	}
}
	
