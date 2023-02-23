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
package de.uos.fmt.musitech.score.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;

/** This class represents a musical score.
 * @author Martin Gieseking
 * @version $Revision: 8272 $, $Date: 2012-09-09 18:08:15 +0200 (Sun, 09 Sep 2012) $ */
public class Score extends ScoreContainer {
	//   private Rational cursorPos = new Rational(-1); // current time position of score cursor (negative value = cursor invisible)
	private SSystem cursoredSystem = null;
	private JComponent component = null;
	private int height = 0;
	private int activePage = 0;
	
	public Score(JComponent c) {
		if (!(c instanceof ChangeListener)) {
			throw new IllegalArgumentException("the given JComponent has to implement the ChangeListener interface.");
		}
		component = c;
	}

	/** Returns a list of score notes. */
	public static List createScoreNotes(String code) {
		try {
			Score score = new Score(new ScorePanel());
			Parser parser = new Parser();
			parser.run(score, code);
			return score.getScoreNotes();
		}
		catch (Exception e) {
		    e.printStackTrace();
			return null;
		}
	}

	public void setActivePage(int activePage) {
		removeAllComponents();
		this.activePage = activePage;
		updateSize();
		stateChanged();
		child(this.activePage).registerComponent(new Class[]{Pitch.class});
	}
	
	void stateChanged() {
		((ChangeListener)component).stateChanged(new ChangeEvent(this));
		
	}
	
	public int getActivePage() {
		return activePage;
	}
	
	public boolean activePageIsLast() {
		return activePage == numChildren() - 1;
	}
	
	//public Score (String code)  {}  
	public Dimension prefSize = new Dimension(); 
	@Override
	public void arrange() {
		//setXPos(20); // @@
		
		super.arrange();
		
		updateSize();
	}

	void updateSize() {
		setLocation(0, absY());
		int prefWidth = child(activePage).getSize().width;
		int prefHeight = child(activePage).getSize().height;
		
		if (prefHeight > 3000) {
			if(DebugState.DEBUG_SCORE)
			    System.err.println(getClass()+".updateSize(), arg " +prefHeight +" is too large. limiting to 3000");
			prefHeight = 3000;
		}
//		if (prefWidth > 30000) { // changed from 3000--RdV
//			if(DebugState.DEBUG_SCORE)
//			    System.err.println(getClass()+".updateSize(), arg " +prefWidth +" is too large. limiting to 3000");
//			prefWidth = 30000; // changed from 3000--RdV
//		}
		
		prefSize = new Dimension(prefWidth, prefHeight);

		setPreferredSize(prefSize);
		setSize(prefSize);
	}
	
	
	@Override
	public Dimension getPreferredSize(){
		return prefSize;
	}

	public void hasChanged() {
		((ChangeListener)component).stateChanged(new ChangeEvent(this));
	}
	
	@Override
	public int rwidth() {
	    ScoreObject scoreObject = child(activePage);
	    if(scoreObject != null)
	        return child(activePage).rwidth();
	    System.err.println("WARNING Score.rwidth() scoreObject is null.");
	    //return 100;
		return child(0).rwidth() + 20; //TODO: what about multiple pages
	}


	@Override
	public void paint(Graphics g) {
		// Changed by Jan-H. Kramer, because child could be return null
	    ScoreObject object = child(activePage);
		if (object != null){
		    object.paint(g);
		}
		//drawFrame(g);
		//super.paint(g);
	}

	public long registeringTime = 0;
	void registerComponent(Component comp) {
		component.add(comp, ScorePanel.SCORE_OBJECT_LAYER);
	}
	
	void removeComponent(Component c) {
		component.remove(c);
	}
	
	void removeAllComponents() {
		component.removeAll();
	}

	public Vector getScoreNotes() {
		Vector scoreNotes = new Vector();
		for (int pageNo = 0; pageNo < numChildren(); pageNo++) {
			Page page = (Page) child(pageNo);
			Rational onset = Rational.ZERO;
			for (int systemNo = 0; systemNo < page.numChildren(); systemNo++) {
				SSystem system = (SSystem) page.child(systemNo);
				GlobalSimSequence gss = GlobalSimFactory.buildGlobalSims(system);
				for (Iterator iter = gss.iterator(); iter.hasNext();) {
					GlobalSim gsim = (GlobalSim) iter.next();
					Vector localSims = gsim.getLocalSims();
					for (Iterator lsiter = localSims.iterator(); lsiter.hasNext();) {
						LocalSim lsim = (LocalSim) lsiter.next();
						for (int eventNo = 0; eventNo < lsim.numChildren(); eventNo++) {
							Event event = (Event) lsim.child(eventNo);
							Rational duration = event.getDuration().toRational();
							if (event instanceof Chord) {
								Chord chord = (Chord) event;
								for (int pitchNo = 0; pitchNo < chord.numChildren(); pitchNo++) {
									Pitch pitch = (Pitch) chord.child(pitchNo);
									Accidental a = pitch.getAccidental();
									scoreNotes.add(
										new ScoreNote(
											onset,
											duration,
											pitch.getBase(),
											(byte) (pitch.getOctave() + 1),
											a == null ? 0 : a.getType()));
								}
							}
						}
					}
					onset = onset.add(gsim.minDuration());
				}
			}
		}
		return scoreNotes;
	}

	//protected int grade () {return 0;}
	@Override
	public Score score() {
		return this;
	}

	public Page page(int n) {
		return (Page) child(n);
	}

	/** Always returns null because a score doesn't have a parent. */
	@Override
	Class parentClass() {
		return null;
	}

	/** Returns page object that contains the given metrical time. */
	Page pageWithTime(Rational time) {
		for (int i = 0; i < numChildren(); i++) {
			Rational[] mmt = page(i).minMaxTime();
			if (time.isGreaterOrEqual(mmt[0]) && time.isLessOrEqual(mmt[1]))
				return page(i);
		}
		return null;
	}

	/** Main-method just for testing purposes. */
	public static void main(String[] args) {
		List notes = Score.createScoreNotes("4cde+ 8 ''f;abc");
		if (notes == null)
			System.out.println("no ScoreNotes created");
		else
			for (int i = 0; i < notes.size(); i++)
				System.out.println(notes.get(i));
	}
	
	@Override
	public int depth() {
		return ((Page)child(activePage)).depth();
	}
	
	@Override
	public int height() {
		return ((Page)child(activePage)).height();
	}

}
