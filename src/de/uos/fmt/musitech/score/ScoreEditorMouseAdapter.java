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
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.framework.selection.SelectionManager;
import de.uos.fmt.musitech.score.ScoreEditor.Line;
import de.uos.fmt.musitech.score.ScoreEditor.Mode;
import de.uos.fmt.musitech.score.gui.Chord;
import de.uos.fmt.musitech.score.gui.Head;
import de.uos.fmt.musitech.score.gui.Page;
import de.uos.fmt.musitech.score.gui.Pitch;
import de.uos.fmt.musitech.score.gui.SSystem;
import de.uos.fmt.musitech.score.gui.Score;
import de.uos.fmt.musitech.score.gui.ScoreObject;
import de.uos.fmt.musitech.score.gui.Staff;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * 
 * @version 28.04.2007
 */
public class ScoreEditorMouseAdapter implements MouseListener,
		MouseMotionListener {

	private ScoreEditor scoreEditor;

	private SelectionManager selectionManager = SelectionManager.getManager();

	private boolean dragging, moving;

	private Point dragStart, moveStart;

	private Rectangle draggingRectangle;

	private Map<ScoreObject, Point> oldLocations;

	/**
	 * @param scoreEditor
	 */
	public ScoreEditorMouseAdapter(ScoreEditor scoreEditor) {
		this.scoreEditor = scoreEditor;
		dragging = false;
		moving = false;
	}

	private ScoreManipulator getNotationManipulator() {
		return scoreEditor.getScoreManipulator();
	}

	public Mode getModus() {
		return scoreEditor.getModus();
	}

	public void setModus(Mode mode) {
		scoreEditor.setModus(mode);
	}

	public boolean isDragging() {
		return dragging;
	}

	public boolean isMoving() {
		return moving;
	}

	public Rectangle getDraggingRectangle() {
		return draggingRectangle;
	}

	public void mouseClicked(MouseEvent e) {
		// System.out.println("mouseClicked " + e);
		if(e.getButton()!=MouseEvent.BUTTON1)
			return;
		switch (getModus()) {
		case VIEW:
			return;
		case SELECT_AND_EDIT:
			dragging = false;
			Note mo = scoreEditor.getNoteAt(e.getPoint());
			if (mo != null) {
				if (!e.isControlDown())
					selectionManager.getSelection().clear(this);
				if (scoreEditor.getNotes(scoreEditor.getSelectedStaff())
						.contains(mo))
					selectionManager.getSelection().add(mo, this);
			} else {
				selectionManager.getSelection().clear(this);
			}
			break;
		case INSERT:
			if (scoreEditor.getNoteLength() == null)
				return;
			Note n = scoreEditor.getNote(e.getX(), e.getY(), scoreEditor
					.getNoteLength());
			if (n == null) {
				System.out.println("Cannot find Position");
				return;
			} else {
				System.out.println("Want to insert " + n.toString());
				if (scoreEditor.getScoreManipulator().insertNote(n)) {
					System.out.println("Note inserted");
					scoreEditor.getScoreManipulator().notationSystemChanged();
				} else
					System.out.println("Cannot insert note");
			}
			break;
		}
	}

	public void mousePressed(MouseEvent e) {
		// System.out.println("mousePressed " + e);
		if(e.getButton()!=MouseEvent.BUTTON1)
			return;
		if (scoreEditor.getModus() == Mode.SELECT_AND_EDIT) {
			Note mo = scoreEditor.getNoteAt(e.getPoint());
			if (mo == null) {
				selectionManager.getSelection().clear(this);
				dragStart = e.getPoint();
				dragging = true;
				moving = false;
			} else if (selectionManager.getSelection().getAll().size() > 0
					&& selectionManager.getSelection().isSelected(mo)
					&& scoreEditor.hasGrid()) {
				moveStart = e.getPoint();
				moving = true;
				dragging = false;
				oldLocations = new HashMap<ScoreObject, Point>();
				for (Iterator i = selectionManager.getSelection().getAll()
						.iterator(); i.hasNext();) {
					Note n = (Note) i.next();
					Chord c = (Chord) scoreEditor.getScoreMapper()
							.getNotationToGraphical().get(n);
					if (c.numChildren() == 1) {
						oldLocations
								.put(c, new Point(c.getXPos(), c.getYPos()));
						c.staff().unregisterAuxLines(c.getPitch(0).getHead());
					} else {
						boolean containsAll = true;
						for (Iterator pi = c.iterator(); pi.hasNext();) {
							Pitch pitch = (Pitch) pi.next();
							if (!selectionManager.getSelection().isSelected(
									pitch.getNote()))
								containsAll = false;
							else
								pitch.staff().unregisterAuxLines(
										pitch.getHead());
						}
						if (containsAll)
							oldLocations.put(c, new Point(c.getXPos(), c
									.getYPos()));
						else
							for (Iterator pi = c.iterator(); pi.hasNext();) {
								Pitch pitch = (Pitch) pi.next();
								if (pitch.getNote() == n) {
									Head h = pitch.getHead();
									oldLocations.put(h, new Point(h.getXPos(),
											h.getYPos()));
									h.staff().unregisterAuxLines(h);
								}
							}
					}
				}
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		// System.out.println("mouseDragged " + e);
		if(e.getButton()!=MouseEvent.BUTTON1)
			return;
		if (getModus() == Mode.SELECT_AND_EDIT)
			if (dragging) {
				int x = Math.min(dragStart.x, e.getPoint().x);
				int y = Math.min(dragStart.y, e.getPoint().y);
				int width = Math.abs(dragStart.x - e.getPoint().x);
				int height = Math.abs(dragStart.y - e.getPoint().y);
				draggingRectangle = new Rectangle(x, y, width, height);
				scoreEditor.repaint();
			} else if (moving) {
				scoreEditor.getAuxlines().clear();
				Score score = scoreEditor.getScore();
				Page page = (Page) score.child(score.getActivePage());
				SSystem sys = (SSystem) page.child(0);
				Staff staff = (Staff) sys.child(scoreEditor.getSelectedStaff());
				int extra = 10;
				int dx = e.getPoint().x - moveStart.x;
				int dy = e.getPoint().y - moveStart.y;
				for (ScoreObject so : oldLocations.keySet()) {
					Point start = oldLocations.get(so);
					so.setXPos(start.x + dx);
					so.setYPos(start.y + dy);
					ScoreObject o = so;
					if (so instanceof Chord) {
						Chord c = (Chord) so;
						o = c.bottomPitch();
					}
					if (o.absY() >= staff.getBottomLineY()) {
						int pos = staff.getBottomLineY();
						while (o.absY() > pos - staff.getLineDistance() / 2) {
							scoreEditor.getAuxlines().add(
									new Line(o.absX() - extra, pos, o.absX()
											+ o.lwidth() + o.rwidth(), pos));
							pos += staff.getLineDistance();
						}
					} else if (o.absY() < staff.getTopLineY()) {
						int pos = staff.getTopLineY();
						while (o.absY() < pos + staff.getLineDistance() / 2) {
							scoreEditor.getAuxlines().add(
									new Line(o.absX() - extra, pos, o.absX()
											+ o.lwidth() + o.rwidth(), pos));
							pos -= staff.getLineDistance();
						}
					}
					if (so instanceof Chord) {
						((Chord) so).arrange(0);
						((Chord) so).arrange(1);
						((Chord) so).arrange(2);
					}
				}
				scoreEditor.repaint();
			}
	}

	public void mouseReleased(MouseEvent e) {
		// System.out.println("mouseReleased " + e);
		if(e.getButton()!=MouseEvent.BUTTON1)
			return;
		if (getModus() == Mode.SELECT_AND_EDIT) {
			if (dragging && dragStart.distance(e.getPoint()) > 2) {
				dragging = false;
				List<Note> notes = scoreEditor.getNotes(scoreEditor
						.getSelectedStaff());
				for (Note n : notes) {
					Chord c = (Chord) scoreEditor.getScoreMapper()
							.getNotationToGraphical().get(n);
					for (int i = 0; i < c.numChildren(); i++) {
						Pitch p = c.getPitch(i);
						if (p.getBase() == n.getScoreNote().getDiatonic()
								&& p.getOctave() == n.getScoreNote()
										.getOctave()) {
							if (draggingRectangle != null && draggingRectangle.contains(p.getHead().absX(),
									p.getHead().absY()))
								selectionManager.getSelection().add(n, this);
						}
					}
				}
				draggingRectangle = null;
				scoreEditor.repaint();
			} else if (moving) {
				// TODO alles nach NotationManipulator verschieben
				Page p = (Page) scoreEditor.getScore().child(
						scoreEditor.getScore().getActivePage());
				SSystem sys = (SSystem) p
						.child(scoreEditor.getSelectedSystem());
				Staff s = (Staff) sys.child(scoreEditor.getSelectedStaff());
				int dy = e.getPoint().y - moveStart.y;
				int interval = 2 * dy / s.getLineDistance();
				Rational dx = new Rational();
				if (Math.abs(moveStart.x - e.getPoint().x) > 5)
					dx = scoreEditor.getMetricDistance(moveStart.x, e
							.getPoint().x);

				// Testet ob Note verschoben werden kann
				System.out.println(interval + "-" + dx.toString());
				getNotationManipulator().moveSelection(interval, dx);
				scoreEditor.repaint();
				moving = false;
				oldLocations = null;
				scoreEditor.getScoreManipulator().notationSystemChanged();
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (scoreEditor.getModus() == Mode.INSERT) {
			scoreEditor.getAuxlines().clear();
			Score score = scoreEditor.getScore();
			Page page = (Page) score.child(score.getActivePage());
			SSystem sys = (SSystem) page.child(0);
			Staff staff = (Staff) sys.child(scoreEditor.getSelectedStaff());

			int x = e.getX();
			int y = e.getY();
			int top = staff.getTopLineY();
			int bottom = staff.getBottomLineY();
			int lineDistance = staff.getLineDistance();
			int extra = 10;

			if (!(top < y && y < bottom))
				if (y < top) {
					for (int i = top - lineDistance; i > y - lineDistance / 2; i -= lineDistance) {
						scoreEditor.getAuxlines().add(
								new Line(x - extra, i, x + extra * 2, i));
					}
				} else {
					for (int i = bottom + lineDistance; i < y + lineDistance
							/ 2; i += lineDistance) {
						scoreEditor.getAuxlines().add(
								new Line(x - extra, i, x + extra * 2, i));
					}
				}
			if (scoreEditor.getNoteLength() != null) {
				if (scoreEditor.getNoteCursor() == null)
					scoreEditor.setNoteCursor(new NoteCursor(x, y, scoreEditor
							.getNoteLength().getDenom()));
				else {
					scoreEditor.getNoteCursor().setPosition(x, y);
					scoreEditor.getNoteCursor().setLength(
							scoreEditor.getNoteLength().getDenom());
				}
			}
			scoreEditor.repaint();
		} else if (scoreEditor.getModus() == Mode.SELECT_AND_EDIT) {
			if (mouseoverhead != null) {
				mouseoverhead.setColor(Color.BLACK);
				mouseoverhead = null;
			}
			Note n = scoreEditor.getNoteAt(e.getPoint());
			if (n != null) {
				Chord c = (Chord) scoreEditor.getScoreMapper()
						.getNotationToGraphical().get(n);
				if (c != null) {
					for (Iterator pi = c.iterator(); pi.hasNext();) {
						Pitch pitch = (Pitch) pi.next();
						if (n == pitch.getNote()) {
							mouseoverhead = pitch.getHead();
							mouseoverhead.setColor(Color.GRAY);
						}
					}
				}
			}
			scoreEditor.repaint();
		}
	}

	private Head mouseoverhead;

	private Cursor scoreEditorCursor;

	public void mouseEntered(MouseEvent e) {
		if (scoreEditor.getModus() == Mode.INSERT) {
			scoreEditorCursor = scoreEditor.getCursor();
			scoreEditor.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	public void mouseExited(MouseEvent e) {
		if (scoreEditor.getModus() == Mode.INSERT) {
			if (scoreEditorCursor != null)
				scoreEditor.setCursor(scoreEditorCursor);
			scoreEditor.setNoteCursor(null);
			scoreEditor.getAuxlines().clear();
			scoreEditor.repaint();
		}
		// dragging = false;
		// moving = false;
		// draggingRectangle = null;
	}

}
