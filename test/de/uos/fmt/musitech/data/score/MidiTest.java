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
 * Created on 21-Jun-2004
 * File MidiTest.java
 */
package de.uos.fmt.musitech.data.score;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.performance.midi.MidiReader;
import de.uos.fmt.musitech.score.gui.Score;
import de.uos.fmt.musitech.score.gui.ScoreMapper;
import de.uos.fmt.musitech.score.gui.ScorePanel;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Test for MIDI-Import
 * @author collin
 */
public class MidiTest extends JFrame {
	ScorePanel spanel;
	JLabel currentPage;
	JButton left, right;
	JToggleButton go;
	
	void fillVoice(Piece p, NotationVoice voice) {
		Context context = new Context(p);

		Container notePool = p.getNotePool();
		int i = 0;
		Hashtable chordPool = new Hashtable();

		for (Iterator iter = notePool.iterator(); iter.hasNext();) {
			Note element = (Note) iter.next();
			ScoreNote sn = element.getScoreNote();
			if (chordPool.containsKey(sn.getMetricTime())) {
				((NotationChord)chordPool.get(sn.getMetricTime())).add(element, true);
			}
			else {
				if (sn.getMetricDuration().equals(Rational.ZERO)) {
					System.err.println("skipping a ScoreNote with zero duration");
					continue;
				}
				NotationChord nc = new NotationChord(context, element);
				voice.add(nc);
				chordPool.put(sn.getMetricTime(), nc);
			}
			i++;
			//if (i == 200) break;
		}
		System.err.println(i + " notes, " + chordPool.size() + " chords");
	}
	
	public MidiTest() throws MalformedURLException {
		super();
		MidiReader mr = new MidiReader();
		Piece p = mr.getPiece(this.getClass().getResource("../../performance/midi/import_5.mid"));
		Context context = new Context(p);
		NotationSystem system = new NotationSystem(context);
		NotationStaff staff = new NotationStaff(system);
		system.add(staff);
		NotationVoice voice = new NotationVoice(staff);
		staff.add(voice);
		fillVoice(p, voice);

		NotationStaff staff2 = new NotationStaff(system);
		system.add(staff2);
		NotationVoice voice2 = new NotationVoice(staff);
		staff2.add(voice2);
		fillVoice(p, voice2);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		spanel = new ScorePanel();
		spanel.setAutoZoom(false);
//		spanel.setSelectionClass("de.uos.fmt.musitech.gui.score.Clef"); not used
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(spanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

		
		system.prepareForScore();
		//system.addLinebreak(new Rational(8,4));
		//system.addPagebreak(new Rational(12, 4));
		ScoreMapper mapper = new ScoreMapper(spanel, system, 1000, 800);
		//ScoreMapper mapper = new ScoreMapper(spanel, system, true);

		JPanel buttonPanel = new JPanel();
		left = new JButton(new LeftAction());
		left.setText("<-");
		right = new JButton(new RightAction());
		right.setText("->");
		currentPage = new JLabel((mapper.getScore().getActivePage() + 1) + "/" + mapper.getScore().numChildren());
		go = new JToggleButton(new GoAction());
		go.setText("go");
		
		buttonPanel.add(left);
		buttonPanel.add(currentPage);
		buttonPanel.add(right);
		buttonPanel.add(go);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
		setSize(spanel.getSize().width + 30, 800);
	}		
	
	class LeftAction extends AbstractAction {
		Score score;
		
		public LeftAction() {
			score = spanel.getScore();
		}
		
		public void actionPerformed(ActionEvent e) {
			if (score.getActivePage() > 0) {
				score.setActivePage(score.getActivePage() - 1);
			}
			currentPage.setText((score.getActivePage() + 1) + "/" + score.numChildren());
			setSize(spanel.getSize().width + 30, 800);
		}	
	}
	
	class RightAction extends AbstractAction {
		Score score;
		
		public RightAction() {
			score = spanel.getScore();
		}
		
		public void actionPerformed(ActionEvent e) {
			if (score.getActivePage() < score.numChildren()) {
				score.setActivePage(score.getActivePage() + 1);
			}
			currentPage.setText((score.getActivePage() + 1) + "/" + score.numChildren());
			setSize(spanel.getSize().width + 30, 800);
		}
	}
	
	class GoAction extends AbstractAction {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			Timer timer = new Timer();
			timer.schedule(new CursorThread(), (long)(0.5 * 1000), (long)(0.5 * 1000));
		}
		
	}
	
	class CursorThread extends TimerTask {
		Rational inc = new Rational(1,10);
		Rational current = Rational.ZERO;
		
		public void run() {
			spanel.setMetricTime(current);
			current = current.add(inc);
		}
		
	}
	
	
	public static void main(String[] args) {
		try {
			(new MidiTest()).setVisible(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
