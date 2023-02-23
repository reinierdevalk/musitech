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
package de.uos.fmt.musitech.score;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeListener;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.change.IDataChangeManager;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.PlayTimer;
import de.uos.fmt.musitech.score.ScoreEditorListener.ScoreEditorEvent;
import de.uos.fmt.musitech.score.gui.Barline;
import de.uos.fmt.musitech.score.gui.LocalSim;
import de.uos.fmt.musitech.score.gui.Measure;
import de.uos.fmt.musitech.score.gui.Page;
import de.uos.fmt.musitech.score.gui.Pitch;
import de.uos.fmt.musitech.score.gui.SSystem;
import de.uos.fmt.musitech.score.gui.Score;
import de.uos.fmt.musitech.score.gui.ScoreObject;
import de.uos.fmt.musitech.score.gui.ScorePanel;
import de.uos.fmt.musitech.score.gui.Staff;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * @version 02.02.2007
 */
public class ScoreEditor extends ScorePanel implements DataChangeListener {

	public static final Color BACKGROUND_COLOR = new Color(230, 230, 230);

	public static final Rational[] RATIONALS = {new Rational(1, 1),
												new Rational(1, 2),
												new Rational(1, 4),
												new Rational(1, 8),
												new Rational(1, 16)};

	public enum Mode {
		VIEW, SELECT_AND_EDIT, INSERT
	};

	private Mode mode;

	private List<Integer> grid;

	private List<Line> auxlines;

	private Rational gridSize, noteLength;

	private int selectedStaff, selectedSystem, selectedVoice;

	private SelectionManager selectionManager = SelectionManager.getManager();

	private IDataChangeManager dataChangeManager = DataChangeManager
			.getInstance();

	private ScoreManipulator scoreManipulator;

	private ScoreEditorMouseAdapter scoreEditorMouseAdapter;

	private int cursor;

	private NoteCursor noteCursor;

	private List<ScoreEditorListener> scoreEditorListener;

	private ScoreEditorContextMenu scoreEditorContextMenu;

	private boolean playerMode;

	public ScoreEditor() {
		this(new NotationSystem());
	}

	public ScoreEditor(NotationSystem nsys) {
		playerMode = false;
		setNotationSystem(nsys);
		grid = new ArrayList<Integer>();
		auxlines = new ArrayList<Line>();
		selectedStaff = 0;
		selectedSystem = 0;
		selectedVoice = 0;
		selectionManager.addListener(this);
		dataChangeManager.interestExpandObject(this, getNotationSystem());
		scoreEditorListener = new ArrayList<ScoreEditorListener>();
		scoreManipulator = new ScoreManipulator(this);
		scoreEditorMouseAdapter = new ScoreEditorMouseAdapter(this);
		scoreEditorContextMenu = new ScoreEditorContextMenu(this);
		addMouseListener(scoreEditorContextMenu);
		addMouseListener(scoreEditorMouseAdapter);
		addMouseMotionListener(scoreEditorMouseAdapter);
		cursor = -1;
		mode = Mode.VIEW;
		setOpaque(true);
		getScoreManipulator().notationSystemChanged();
		setGrid(null);
		setNoteLength(RATIONALS[3]);
		setModus(Mode.VIEW);
	}

	@Override
	public void setNotationSystem(NotationSystem nsys) {
		if(getNotationSystem()!=null){
			dataChangeManager.interestReduceObject(this, getNotationSystem());
		}
		if (playerMode) {
			ObjectPlayer player = ObjectPlayer.getInstance();
			PlayTimer timer = player.getPlayTimer();
			timer.setContext(nsys.getContext());
			timer.registerMetricForPush(this);
			player.setContainer(nsys);
		}
		super.setNotationSystem(nsys);
		dataChangeManager.interestExpandObject(this, getNotationSystem());
	}

	public ScoreManipulator getScoreManipulator() {
		return scoreManipulator;
	}

	public ScoreEditorMouseAdapter getScoreEditorMouseAdapter() {
		return scoreEditorMouseAdapter;
	}

	public Rational getTime(int posX) {
		if (gridSize == null)
			return null;
		int dx = Integer.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < grid.size(); i++) {
			int d = Math.abs(grid.get(i) - posX);
			if (d < dx) {
				dx = d;
				index = i;
			}
		}
		return gridSize.mul(index);
	}

	public Note getNote(int x, int y, Rational duration) {
		Rational time = getTime(x);
		if (time == null)
			return null;
		if (duration == null)
			duration = new Rational(1, 4);
		Note n = new Note(new ScoreNote(new ScorePitch(pitchForY(y), octForY(y), 0), duration));
		n.getScoreNote().setMetricTime(time);
		KeyMarker km = null;
		for (Iterator i = getNotationSystem().getContext().getPiece()
				.getHarmonyTrack().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof KeyMarker) {
				KeyMarker k = (KeyMarker) o;
				if (k.getMetricTime().isLessOrEqual(n.getMetricTime()))
					km = k;
			}
		}
		char[] pitches = {'c', 'd', 'e', 'f', 'g', 'a', 'b'};
		int index = 0;
		while (n.getScoreNote().getDiatonic() != pitches[index]
				&& index < pitches.length)
			index++;
		if (km != null)
			n
					.getScoreNote()
					.setAlteration(
						ScoreManipulator.KEY_SIGNATURES[km.getAccidentalNum() + 6][index]);
		else
			n.getScoreNote().setAlteration((byte) 0);
		return n;
	}

	private char pitchForY(int y) {
		Page p = (Page) getScore().child(getScore().getActivePage());
		SSystem sys = (SSystem) p.child(selectedSystem);
		Staff s = (Staff) sys.child(selectedStaff);
		char[] pitches = {'c', 'd', 'e', 'f', 'g', 'a', 'b'};
		int clefline = -1;
		if (getNotationSystem().get(selectedStaff).getClefTrack() != null
			&& getNotationSystem().get(selectedStaff).getClefTrack().size() > 0)
				clefline = (getNotationSystem().get(selectedStaff).getClefTrack().get(0)).getClefLine();
		int hs = s.pixelToHs(y) + clefline;
		int pitchIndex = hs % 7;
		if (pitchIndex < 0)
			pitchIndex += 7;
		return pitches[pitchIndex];
	}

	private int octForY(int y) {
		Page p = (Page) getScore().child(getScore().getActivePage());
		SSystem sys = (SSystem) p.child(selectedSystem);
		Staff s = (Staff) sys.child(selectedStaff);
		Clef c = null;
		if (getNotationSystem().get(selectedStaff).getClefTrack() != null
			&& getNotationSystem().get(selectedStaff).getClefTrack().size() > 0) {
				c = (getNotationSystem().get(selectedStaff).getClefTrack().get(0));
		}
		int clefline = -1;
		if (c != null)
			clefline = c.getClefLine();
		int hs = s.pixelToHs(y) + clefline;
		int oct = 0;
		if (hs < 0) {
			hs++;
		} else {
			oct++;
		}
		oct += (hs / 7);
		if (hs >= 0) {
		}
		if (c != null)
			if (c.getClefType() == 'c')
				oct -= 1;
			else if (c.getClefType() == 'f')
				oct -= 2;
		return oct;
	}

	public void setGrid(Rational gridSize) {
		Rational oldValue = this.gridSize;
		Rational newValue = gridSize;
		this.gridSize = gridSize;
		grid.clear();
		if (gridSize != null) {
			Rational end = getNotationSystem().getEndTime();
			for (Rational pos = Rational.ZERO; pos.isLess(end); pos = pos
					.add(gridSize)) {
				grid.add(getPosition(pos));
			}
		}
		repaint();
		fireScoreEditorChangedEvent(
			ScoreEditorListener.ScoreEditorEvent.GRID_CHANGED, oldValue,
			newValue);
	}

	public Rational getGridSize() {
		return gridSize;
	}

	public boolean hasGrid() {
		return gridSize != null;
	}

	private int getPosition(Rational metric) {
		Score score = getScore();
		Page page = (Page) score.child(score.getActivePage());
		SSystem sys = (SSystem) page.child(0);
		Staff staff = (Staff) sys.child(0);
		LocalSim lastLocalSim = null;
		for (Object o : staff.getChildren()) {
			Measure m = (Measure) o;
			for (int i = 0; i < m.numChildren(); i++) {
				if (m.child(i) instanceof LocalSim) {
					LocalSim currentLocalSim = (LocalSim) m.child(i);
					if (currentLocalSim.getAttack().equals(metric))
						return currentLocalSim.absX();
					if (currentLocalSim.getAttack().isGreater(metric)) {
						if (lastLocalSim != null) {
							Rational distanceToLast = metric.sub(lastLocalSim
									.getAttack());
							Rational distanceToCurrent = currentLocalSim
									.getAttack().sub(lastLocalSim.getAttack());
							double ratio = distanceToLast
									.div(distanceToCurrent).toDouble();
							int graphicalDistance = currentLocalSim.absX()
													- lastLocalSim.absX();
							return lastLocalSim.absX()
									+ (int) (graphicalDistance * ratio);
						}
					}
					lastLocalSim = currentLocalSim;
				}
			}
		}
		Rational endPoint = lastLocalSim.getMetricEndPoint();
		Measure lastMeasure = (Measure) staff.child(staff.numChildren() - 1);
		Barline bl = lastMeasure.getRightBarline();
		if (endPoint.isGreater(metric)) {
			if (lastLocalSim != null) {
				Rational distanceToLast = metric.sub(lastLocalSim.getAttack());
				Rational distanceToCurrent = endPoint.sub(lastLocalSim
						.getAttack());
				double ratio = distanceToLast.div(distanceToCurrent).toDouble();
				int graphicalDistance = bl.absX() - lastLocalSim.absX();
				return lastLocalSim.absX() + (int) (graphicalDistance * ratio);
			}
		}
		return -1;
	}

	public Note getNoteAt(Point p) {
		ScoreObject so = getScore().catchScoreObject(p.x, p.y, Pitch.class);
		if (so == null) {
			// System.err.println("did not find a score object");
			return null;
		}
		Pitch pi = (Pitch) so;
		return pi.getNote();
	}

	public NotationChord getNotationChordAt(Rational time) {
		NotationStaff ns = getNotationSystem().get(
			selectedStaff);
		NotationVoice nv = ns.get(selectedVoice);
		for (int i = 0; i < nv.size(); i++) {
			NotationChord nc = nv.get(i);
			if (nc.getMetricTime().equals(time))
				return nc;
		}
		return null;
	}

	public NotationChord getNotationChordBefore(Rational time) {
		NotationStaff ns = getNotationSystem().get(selectedStaff);
		NotationVoice nv = ns.get(selectedVoice);
		NotationChord before = null;
		for (int i = 0; i < nv.size(); i++) {
			NotationChord nc = nv.get(i);
			if (nc.getMetricTime().isGreaterOrEqual(time))
				return before;
			before = nc;
		}
		return before;
	}

	public NotationChord getNotationChordAfter(Rational time) {
		NotationStaff ns = getNotationSystem().get(selectedStaff);
		NotationVoice nv = ns.get(selectedVoice);
		for (int i = 0; i < nv.size(); i++) {
			NotationChord nc = nv.get(i);
			if (nc.getMetricTime().isGreater(time))
				return nc;
		}
		return null;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		Color old = g2.getColor();
//		g2.setColor(BACKGROUND_COLOR);
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(old);
		paintGrid(g2);
		getScore().paint(g2);
		if (scoreEditorMouseAdapter.isDragging()
			&& scoreEditorMouseAdapter.getDraggingRectangle() != null) {
			Color c = Color.YELLOW.brighter().brighter();
			int alpha = 100;
			g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
						alpha));
			g2.fillRect(scoreEditorMouseAdapter.getDraggingRectangle().x,
				scoreEditorMouseAdapter.getDraggingRectangle().y,
				scoreEditorMouseAdapter.getDraggingRectangle().width,
				scoreEditorMouseAdapter.getDraggingRectangle().height);
		}
		if (cursor > -1) {
			old = g2.getColor();
			g2.setColor(Color.RED);
			g2.fillRect(cursor - 2, 0, 4, getHeight());
			// g.drawLine(cursor, 0, cursor, getHeight());
			g2.setColor(old);
		}
		paintAuxLines(g2);
		if (getModus() == Mode.INSERT && noteCursor != null)
			noteCursor.paint(g2);
	}

	public void paintGrid(Graphics g) {
		Color old = g.getColor();
		g.setColor(Color.GRAY);
		for (Integer pos : grid) {
			g.drawLine(pos, 0, pos, getHeight());
		}
		g.setColor(old);
	}

	public void paintAuxLines(Graphics g) {
		if (getScoreEditorMouseAdapter().isMoving()
			|| getModus() == Mode.INSERT) {
			Color old = g.getColor();
			g.setColor(Color.DARK_GRAY);
			for (Line l : auxlines) {
				g.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
			}
			g.setColor(old);
		}
	}

	public Rational getNoteLength() {
		return noteLength;
	}

	public void setNoteLength(Rational noteLength) {
		Rational oldValue = this.noteLength;
		Rational newValue = noteLength;
		this.noteLength = noteLength;
		fireScoreEditorChangedEvent(
			ScoreEditorListener.ScoreEditorEvent.LENGTH_CHANGED, oldValue,
			newValue);
	}

	public Mode getModus() {
		return mode;
	}

	public void setModus(Mode mode) {
		Mode oldValue = this.mode;
		Mode newValue = mode;
		this.mode = mode;
		fireScoreEditorChangedEvent(
			ScoreEditorListener.ScoreEditorEvent.MODUS_CHANGED, oldValue,
			newValue);
	}

	public List<Note> getNotes() {
		List<Note> notes = new ArrayList<Note>();
		for (int i = 0; i < getNotationSystem().size(); i++) {
			NotationStaff ns = getNotationSystem().get(i);
			for (int j = 0; j < ns.size(); j++) {
				NotationVoice nv = ns.get(j);
				for (int k = 0; k < nv.size(); k++) {
					NotationChord nc = nv.get(k);
					for (int l = 0; l < nc.size(); l++) {
						notes.add(nc.get(l));
					}
				}
			}
		}
		return notes;
	}

	public List<Note> getNotes(int staff) {
		List<Note> notes = new ArrayList<Note>();
		NotationStaff ns = getNotationSystem().get(staff);
		for (int j = 0; j < ns.size(); j++) {
			NotationVoice nv = ns.get(j);
			for (int k = 0; k < nv.size(); k++) {
				NotationChord nc = nv.get(k);
				for (int l = 0; l < nc.size(); l++) {
					notes.add(nc.get(l));
				}
			}
		}
		return notes;
	}

	public Rational getMetricDistance(int x1, int x2) {
		Rational r1 = getTime(x1);
		Rational r2 = getTime(x2);
		return r2.sub(r1);
	}

	public List<Line> getAuxlines() {
		return auxlines;
	}

	public int getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(int selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public int getSelectedSystem() {
		return selectedSystem;
	}

	public void setSelectedSystem(int selectedSystem) {
		this.selectedSystem = selectedSystem;
	}

	public int getSelectedVoice() {
		return selectedVoice;
	}

	public void setSelectedVoice(int selectedVoice) {
		this.selectedVoice = selectedVoice;
	}

	@Override
	public void dataChanged(DataChangeEvent e) {
		setNotationSystem(getNotationSystem());
		selectionChanged(new SelectionChangeEvent(this, selectionManager
				.getSelection()));
		setGrid(gridSize);
	}

	@Override
	public void setMetricTime(Rational time) {
		cursor = getPosition(time);
		if (cursor == getPosition(Rational.ZERO))
			cursor = -1;
		repaint();
	}

	public NoteCursor getNoteCursor() {
		return noteCursor;
	}

	public void setNoteCursor(NoteCursor noteCursor) {
		this.noteCursor = noteCursor;
	}

	public void addScoreEditorListener(ScoreEditorListener sel) {
		scoreEditorListener.add(sel);
	}

	public void removeScoreEditorListener(ScoreEditorListener sel) {
		scoreEditorListener.remove(sel);
	}

	protected void fireScoreEditorChangedEvent(ScoreEditorEvent e,
												Object oldValue, Object newValue) {
		for (ScoreEditorListener sel : scoreEditorListener)
			sel.scoreEditorChanged(e, oldValue, newValue);
	}

	public void setScoreEditorContextMenu(
											ScoreEditorContextMenu scoreEditorContextMenu) {
		if (this.scoreEditorContextMenu != null)
			removeMouseListener(this.scoreEditorContextMenu);
		this.scoreEditorContextMenu = scoreEditorContextMenu;
		if (scoreEditorContextMenu != null)
			addMouseListener(scoreEditorContextMenu);
	}

	static class Line {

		private int x1, x2, y1, y2;

		public Line() {
		}

		public Line(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y2;
			this.x2 = x2;
			this.y2 = y2;
		}

		public int getX1() {
			return x1;
		}

		public void setX1(int x1) {
			this.x1 = x1;
		}

		public int getX2() {
			return x2;
		}

		public void setX2(int x2) {
			this.x2 = x2;
		}

		public int getY1() {
			return y1;
		}

		public void setY1(int y1) {
			this.y1 = y1;
		}

		public int getY2() {
			return y2;
		}

		public void setY2(int y2) {
			this.y2 = y2;
		}
	}
}
