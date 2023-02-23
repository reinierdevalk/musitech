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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JLayeredPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.rendering.RenderingSupported;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.MetricTimeable;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.framework.selection.Selection;
import de.uos.fmt.musitech.framework.selection.SelectionChangeEvent;
import de.uos.fmt.musitech.framework.selection.SelectionListener;
import de.uos.fmt.musitech.performance.midi.MidiReader;
import de.uos.fmt.musitech.score.util.FileUtils;
import de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This is a Swing component displaying musical scores. The score to be drawn is
 * described by an alphanumeric code that currently supports the following
 * syntax: ...
 * 
 * @author Martin Gieseking
 * @version $Revision: 8449 $, $Date: 2008-03-06 12:56:11 +0100 (Do, 06 Mrz
 *          2008) $
 */
public class ScorePanel extends JLayeredPane implements ChangeListener,
		MetricTimeable, HorizontalTimedDisplay, SelectionListener, Printable {

	private Score score = new Score(this);
	private ScoreCursor scoreCursor = new ScoreCursor(this);
	private ScoreMapper scoreMapper = null;
	//private List marked = new ArrayList();
	//private String selectionClass = "de.uos.fmt.musitech.gui.score.Chord"; TODO CHECK what this should be doing. 
	private boolean redraw = true;
	private Cursor cursor;
	private NotationSystem notationSystem;

	public final static Integer SCORE_OBJECT_LAYER = new Integer(10);
	public final static Integer CURSOR_LAYER = new Integer(11);

	/** ScorePanel constructor. */
	public ScorePanel() {
		this(false);
		setLayout(null);
	}

	/**
	 * Creates a ScorePanel out of an interactively entered code String.
	 * 
	 * @param shouldAskForCode TODO
	 */
	public ScorePanel(boolean shouldAskForCode) {
		addListener();
		// setBackground(Color.WHITE);
		// addMouseListener(new GraphicalSelection());
		if (shouldAskForCode)
			askForCode();
	}

	/**
	 * Creates a ScorePanel out of a given code String.
	 * 
	 * @param code TODO add comment
	 */
	public ScorePanel(String code) {
		this(false);
		Parser parser = new Parser();
		try {
			parser.run(score, code);
			score.arrange();
		} catch (Exception e) {
		}
	}

	/**
	 * Creates a ScorePanel out of a code that is read from the given
	 * InputStream.
	 * 
	 * @param istream
	 */
	public ScorePanel(InputStream istream) {
		this(false);
		Parser parser = new Parser();
		try {
			parser.run(score, istream);
			score.arrange();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a ScorePanel from a code that is read from the given File.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public ScorePanel(File file) throws FileNotFoundException {
		this(false);
		readFile(file);
	}

	/**
	 * TODO comment
	 * 
	 * @param nsys
	 */
	public ScorePanel(NotationSystem nsys) {
		this(false);
		setNotationSystem(nsys);
	}

	// ============ end constructors =============

	/**
	 * TODO add comment
	 * 
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getMinimalPositionForTime(long,
	 *      de.uos.fmt.musitech.utility.math.Rational)
	 */
	@Override
	public int getMinimalPositionForTime(long t, Rational m)
			throws WrongArgumentException {
		if (t == Timed.INVALID_TIME && m == null)
			throw new WrongArgumentException(
				"at least one argument has to be non-null");
		if (t == Timed.INVALID_TIME)
			return 0;
		if (m == null)
			m = notationSystem.getContext().getPiece().getMetricalTimeLine()
					.getMetricTime(t);

		if (m.equals(Rational.ZERO)) {
			LocalSim lsim = ((LocalSim) ((Measure) ((Staff) ((SSystem) ((Page) score
					.child(0)).child(0)).child(0)).child(0)).child(0));
			if (lsim == null)
				return 0; // TODO
			if (lsim.getGlobalSim() != null) {
				GlobalSim gsim = lsim.getGlobalSim();
				return gsim.getLocation().x;// + gsim.getSize().width;
			} else {
				return lsim.getLocation().x;// + lsim.getSize().width;
			}
		}

		LocalSim s = (LocalSim) score.getScoreObjectBeforeMetricTime(m,
			LocalSim.class);
		if (s != null) {
			if (s.getGlobalSim() != null) {
				return s.getGlobalSim().getLocation().x;// +
				// s.getGlobalSim().getSize().width;
			} else {
				return s.getLocation().x;// + s.getSize().width;
			}
		} else {
			throw new IllegalArgumentException(
				"did not find a ScoreObject before metric time " + m);
		}
	}

	/**
	 * TODO add comment
	 * 
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#setMinimalPositionForTime(long,
	 *      de.uos.fmt.musitech.utility.math.Rational, int)
	 */
	@Override
	public boolean setMinimalPositionForTime(long t, Rational m, int position)
			throws WrongArgumentException {
		if ((t == Timed.INVALID_TIME && m == null) || position < 0)
			throw new WrongArgumentException(
				"at least one argument has to be non-null and position has be >= 0");
		if (t == Timed.INVALID_TIME)
			return false;
		if (m == null)
			m = notationSystem.getContext().getPiece().getMetricalTimeLine()
					.getMetricTime(t);

		ScoreObject so = score
				.getScoreObjectBeforeMetricTime(m, LocalSim.class);
		if (so == null) {
			if (m.equals(Rational.ZERO)) {
				so = ((Measure) ((Staff) ((SSystem) ((Page) score
						.child(0)).child(0)).child(0)).child(0)).child(0);
			} else { // TODO: what to we do here?
				return false;
			}
		}

		if (((LocalSim) so).getGlobalSim() != null) { // probably more than
			// one staff
			GlobalSim gs = ((LocalSim) so).getGlobalSim();
			int x = gs.getLocation().x;// + gs.getSize().width;
			if (x >= position)
				return false;
			// gs.setLeftPadding(position - x); // TODO check this !
			gs.setLeftPadding(((LocalSim) gs.getLocalSims().get(0))
					.getLeftPadding()
								+ (position - x));
		} else {
			int x = so.getLocation().x;// + so.getSize().width;
			if (x >= position)
				return false;

			so.setLeftPadding(position - x);
		}
		Point test = so.getLocation(); // TODO check this !
		score.arrange();
		Point p = so.getLocation(); // TODO check this !
		return true;

	}

	/**
	 * TODO add comment
	 * 
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getNextPositioningTime(long)
	 */
	@Override
	public long getNextPositioningTime(long startTime) {
		MetricalTimeLine mtl = notationSystem.getContext().getPiece()
				.getMetricalTimeLine();
		Rational metricTime;
		if (startTime != -1) {
			// TODO the 10 is to compensate for losses in the conversion
			// metrical<->physical
			// TODO proper solution needed.
			metricTime = mtl.getMetricTime(startTime + 10000);
		} else
			metricTime = new Rational(-1, 1);
		Rational nextMetricTime = notationSystem.getNextMetricTime(metricTime);
		if (nextMetricTime == null)
			return Timed.INVALID_TIME;

		return mtl.getTime(nextMetricTime);
	}

	/**
	 * TODO add comment
	 * 
	 * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#doInitialLayout()
	 */
	@Override
	public void doInitialLayout() {
		// as the score is arranged in the constructor this method does nothing
	}

	/**
	 * TODO add comment
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		setSize(score.getSize().width, score.getSize().height);
		redraw = true;
		repaint();
	}

	/**
	 * TODO add comment
	 * 
	 * @see de.uos.fmt.musitech.data.time.MetricTimeable#setMetricTime(de.uos.fmt.musitech.utility.math.Rational)
	 */
	// Cursor debugCursor;
	@Override
	public void setMetricTime(Rational time) {
		/*
		 * if (cursor != null) { remove(cursor); }
		 */

		/*
		 * Rational[] _minMax =
		 * ((Staff)((SSystem)((Page)score.child(0)).child(0)).child(0)).minMaxTimePlusDuration();
		 * if (time.isGreater(_minMax[1])){ System.err.println(time + " exceeds " +
		 * _minMax[1]); return; }
		 */

		Staff activeStaff = findActiveStaff(time, true);

		if (activeStaff == null) {
			if (DebugState.DEBUG_SCORE)
				System.err.println("did not find an active staff for " + time);
			return;
		}

		ScoreObject first = activeStaff.getScoreObjectBeforeMetricTime(time,
			LocalSim.class);
		if (first == null) {
			first = ((Measure) activeStaff.child(0)).child(0);
		}
		ScoreObject succ = first.getScoreSuccessor();

		Rational secondMetricTime = null;
		int secondX;
		if (succ == null ||
		// the next condition needs explanation: if (e.g.) first and succ are
			// localSims we want
			// their grandparents (Staves) to be the same, as otherwise they are
			// on
			// different y-coords and
			// therefor first.metricTime < succ.metricTime does not hold anymore
			first.getScoreParent().getScoreParent() != succ.getScoreParent()
					.getScoreParent()) {
			secondMetricTime = first.getMetricEndPoint();
			secondX = first.getBounds().x + first.getSize().width;
		} else {
			secondMetricTime = succ.getMetricTime();
			secondX = succ.getBounds().x + succ.getLeftPadding();
		}

		Rational ratio = time.sub(first.getMetricTime()).div(
			secondMetricTime.sub(first.getMetricTime()));

		Rectangle bounds = new Rectangle();
		bounds.x = first.getBounds().x + first.getLeftPadding();
		bounds.y = activeStaff.getLocation().y;
		bounds.height = activeStaff.getScoreParent().getSize().height;
		bounds.width = secondX - bounds.x;// first.getBounds().x;

		if (cursor == null) {
			cursor = new Cursor(bounds.x + ratio.mul(bounds.width).floor(),
				bounds.y, bounds.height);
			cursor.setVisible(true);
			add(cursor, ScorePanel.CURSOR_LAYER);

			/*
			 * debugCursor = new Cursor(0, 0, 0); debugCursor.setVisible(true);
			 * add(debugCursor, ScorePanel.CURSOR_LAYER);
			 */

		} else {
			cursor.setLocation(bounds.x + ratio.mul(bounds.width).floor(),
				bounds.y);
			cursor.setSize(1, bounds.height);
		}

		/*
		 * debugCursor.setLocation(first.getBounds().x, bounds.y);
		 * debugCursor.setSize(secondX - first.getBounds().x, 1);
		 */

		repaint();
	}

	Staff findActiveStaff(Rational metricTime, boolean turnPage) {
		Page page = (Page) score.child(score.getActivePage());
		for (int i = 0; i < page.numChildren(); i++) {
			SSystem sys = (SSystem) page.child(i);
			for (int j = 0; j < sys.numChildren(); j++) {
				Staff staff = (Staff) sys.child(j);
				Rational[] minMax = staff.minMaxTimePlusDuration();
				if (metricTime.isGreaterOrEqual(minMax[0])
					&& metricTime.isLessOrEqual(minMax[1])) {
					return staff;
				}
			}
		}
		if (turnPage) {
			if (score.activePageIsLast()) {
				return null;
			}

			score.setActivePage(score.getActivePage() + 1);
			return findActiveStaff(metricTime, turnPage);
		}
		return null;
	}

	private void addListener() {
		addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				adjustZoom();
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}
		});
	}

	private boolean autoZoom = false;

	// TODO this is inefficient, but reliable.
	// should be optimized in the future.
	void adjustZoom() {
		if (autoZoom) {
			double xFactor = getWidth()
								/ score.getPreferredSize().getWidth();
			double yFactor = getHeight()
								/ score.getPreferredSize().getHeight();
			zoom = Math.min(xFactor, yFactor);
		}
	}

//	public void setSelectionClass(String s) {
//		selectionClass = s;
//	}

	public void askForCode() {
		System.out.println("-----------------------------------------------");
		System.out.println("Please insert code, then type 'exit' to proceed");
		score.clear();
		Parser parser = new Parser();
		try {
			parser.run(score);
			score.arrange();
			repaint();
		} catch (Exception e) {
		}
	}

	@Override
	public void setSize(int w, int h) {
		Insets i = getInsets();
		h += i.top + i.bottom;
		w += i.left + i.right;
		super.setSize(w, h);
	}

	@Override
	public void setSize(Dimension dim) {
		setSize(dim.width, dim.height);
	}

	/**
	 * Replaces the displayed score by a new one.
	 * 
	 * @param code Code that describes the score
	 */
	public void setCode(String code) {
		score.clear();
		Parser parser = new Parser();
		try {
			parser.run(score, code);
			score.arrange();
			repaint();
		} catch (Exception e) {
		}
	}

	/**
	 * TODO add comment
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	// ToDo: This method read from URL - not from file
	public void readFile(File file) throws FileNotFoundException {
		score.clear();
		if (FileUtils.getExtension(file).equalsIgnoreCase("gin")) {
			Parser parser = new Parser();
			try {
				parser.run(score, new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (FileUtils.getExtension(file).equalsIgnoreCase("mid")) {
			// Create the MidiReader object
			MidiReader midiReader = new MidiReader();

			Piece work;
			try {
				// Read the MIDI file into the work object
				work = midiReader.getPiece(file.toURI().toURL());
			} catch (MalformedURLException mFUException) {
				System.out.println("ScorePanel: mal formed URL (" + file + ")");
				return;
			}

			Container<Container<?>> containerPool = work
					.getContainerPool();
			Iterator<Container<?>> it = containerPool.iterator();
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof NotationSystem) {
					scoreMapper = new ScoreMapper(this, (NotationSystem) o);
					score = scoreMapper.getScore();
				}
			}
		}
		score.arrange();
		repaint();
	}

	/**
	 * @see Display#setMObject(MObject)
	 */
	public void setMObject(MObject newMObject) {
		if (newMObject instanceof NotationSystem)
			setNotationSystem((NotationSystem) newMObject);
		else if (newMObject instanceof NotationStaff) {
			NotationStaff nstaff = (NotationStaff) newMObject;
			NotationSystem nsys = new NotationSystem(nstaff.getContext());
			nsys.add(nstaff);
			setNotationSystem(nsys);
		} else
			// XXX add import options for arbitraty containers
			return;

	}

	public void setNotationSystem(NotationSystem nsys) {
		notationSystem = nsys;
		score.clear();
		scoreMapper = new ScoreMapper(this, nsys);
		score = scoreMapper.getScore();

		repaint();
	}

	int vWidth;
	int vHeight;
	BufferedImage offscreenBuffer;
	Graphics offscreenGraphics;
	double zoom = 1;

	private void createOffscreenImage() {
		vWidth = (int) Math.round(score.getPreferredSize().width*zoom);
		vHeight = (int) Math.round(score.getPreferredSize().height*zoom);
		if ((offscreenBuffer == null || offscreenBuffer.getWidth() != vWidth || offscreenBuffer
				.getHeight() != vHeight)
			&& vWidth > 0 && vHeight > 0 // TODO should it be possible that this happens?
		) {
			if(vWidth>3000)
				vWidth = 3000;
//			offscreenBuffer = (BufferedImage) createImage(vWidth, vHeight);
			offscreenBuffer = new BufferedImage(vWidth, vHeight,BufferedImage.TYPE_INT_ARGB);
			offscreenGraphics = offscreenBuffer.createGraphics();
			offscreenGraphics.setClip(0, 0, vWidth, vHeight);
		}
	}

	/**
	 * @see javax.swing.JLayeredPane#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	/** Paints the score to the given graphics object. 
	 * @param g */
	@Override
	public void paintComponent(Graphics g) {
		if (score != null) {
			if (redraw) {
				adjustZoom();
				createOffscreenImage();
				super.paintComponent(offscreenGraphics);
				if (isOpaque()) {
					offscreenGraphics.setColor(getBackground());
					offscreenGraphics.fillRect(0, 0, vWidth, vHeight);
				}
				offscreenGraphics.setColor(Color.BLACK);
				if(offscreenGraphics instanceof Graphics2D){
					Graphics2D g2d = (Graphics2D) offscreenGraphics;
					g2d.setRenderingHint(
						java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setRenderingHint(
						java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
						java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g2d.setRenderingHint(
						java.awt.RenderingHints.KEY_RENDERING,
						java.awt.RenderingHints.VALUE_RENDER_QUALITY);
					AffineTransform at = g2d.getTransform();
					AffineTransform at2 = new AffineTransform(at);
					at2.scale(zoom, zoom);
					g2d.setTransform(at2);
					score.paint(g2d);
					scoreCursor.paint(g2d);
					g2d.setTransform(at);
				}else{
					score.paint(offscreenGraphics);
					scoreCursor.paint(offscreenGraphics);
				}
				redraw = false;
			}
			if(g instanceof Graphics2D){
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(
					java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(
					java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
					java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setRenderingHint(
					java.awt.RenderingHints.KEY_RENDERING,
					java.awt.RenderingHints.VALUE_RENDER_QUALITY);
				g2d.drawImage(offscreenBuffer, 0, 0,vWidth,	vHeight, this);
			}else{
				g.drawImage(offscreenBuffer, 0, 0, (int) (vWidth * zoom),
					(int) (vHeight * zoom), this);
			}
		}
		Component[] cursors = getComponentsInLayer(CURSOR_LAYER.intValue());
		for (int i = 0; i < cursors.length; i++) {
			cursors[i].paint(g);
		}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex == 0) {
			adjustZoom();
			if (graphics instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D) graphics;
				AffineTransform at = g2d.getTransform();
				AffineTransform at2 = new AffineTransform(at);
				at2.scale(zoom, zoom);
				g2d.setTransform(at2);
				score.paint(graphics);
				g2d.setTransform(at);
			} else {
				createOffscreenImage();
				((Graphics2D) offscreenGraphics).setRenderingHint(
					java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				((Graphics2D) offscreenGraphics).setRenderingHint(
					java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
					java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				((Graphics2D) offscreenGraphics).setRenderingHint(
					java.awt.RenderingHints.KEY_RENDERING,
					java.awt.RenderingHints.VALUE_RENDER_QUALITY);
				offscreenGraphics.setColor(Color.WHITE);
				offscreenGraphics.fillRect(0, 0, offscreenBuffer.getWidth(),
					offscreenBuffer.getHeight());
				offscreenGraphics.setColor(Color.BLACK);
				score.paint(offscreenGraphics);
				((Graphics2D) graphics).setRenderingHint(
					java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				((Graphics2D) graphics).setRenderingHint(
					java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
					java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				((Graphics2D) graphics).setRenderingHint(
					java.awt.RenderingHints.KEY_RENDERING,
					java.awt.RenderingHints.VALUE_RENDER_QUALITY);
				double zoom = 0.6;
				graphics.drawImage(offscreenBuffer, 0, 0,
					(int) (vWidth * zoom), (int) (vHeight * zoom), this);
			}
			return PAGE_EXISTS;
		}
		return NO_SUCH_PAGE;
	}

	@Override
	public void paintChildren(Graphics g) {
		// we take care of that ourselfs in paintComponent().
	}

	/** Returns the score cursor object of this panel. */
	public ScoreCursor getScoreCursor() {
		return scoreCursor;
	}

	/** Returns the score cursor object of this panel. */
	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		if (score != null) {
			this.score = score;
			score.arrange();
		}
		setSize(score.getSize().width, score.getSize().height);
	}

	public ScoreMapper getScoreMapper() {
		return scoreMapper;
	}

	/**
	 * Changes the color of given notes. This method only works if this
	 * ScorePanel was constructed out of a NotationSystem.
	 * 
	 * @param notes notes to be colored
	 * @param color new color
	 * @throws UnsupportedOperationException if method can't be executed
	 * @see ScorePanel(NotationSystem)
	 */
	public void setColor(Note[] notes, Color color) {
		if (scoreMapper != null)
			scoreMapper.setColor(notes, color);
		else
			throw new UnsupportedOperationException(
				"MusicScorePanel wasn't constructed by a NotationSystem");
	}

	public void setColor(Collection notes, Color color) {
		if (scoreMapper != null) {
			scoreMapper.setColor(notes, color);
			repaint();
		} else
			throw new UnsupportedOperationException(
				"MusicScorePanel wasn't constructed by a NotationSystem");
	}

	public boolean isEmpty() {
		return score.numChildren() == 0;
	}

	/*
	 * class GraphicalSelection implements MouseListener { public void
	 * mouseClicked(MouseEvent e) { Point p = e.getPoint(); Class c; try { c =
	 * Class.forName(selectionClass); } catch (Exception ex) { c = Pitch.class; }
	 * Object o = score.catchScoreObject(p.x, p.y, c); if (o == null) return; if
	 * (marked.contains(o)) { //@@ TODO: contains() uses .equals()... we only
	 * need == marked.remove(o); ((ScoreObject)o).setColor(Color.BLACK); } else {
	 * marked.add(o); ((ScoreObject)o).setColor(Color.BLUE); } repaint(); }
	 * public void mouseExited(MouseEvent e) { } public void
	 * mouseReleased(MouseEvent e) { } public void mouseEntered(MouseEvent e) { }
	 * public void mousePressed(MouseEvent e) { } }
	 */

	/**
	 * @see java.awt.Component#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int)Math.round(score.getPreferredSize().width*zoom),
			(int)Math.round(score.getPreferredSize().height*zoom));
	}

	/**
	 * @return
	 */
	public boolean getAutoZoom() {
		return autoZoom;
	}

	/**
	 * @return
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * @param b
	 */
	public void setAutoZoom(boolean b) {
		autoZoom = b;
	}

	/**
	 * @param d
	 */
	public void setZoom(double d) {
		zoom = d;
	}

	public MObject objectAt(Point p) {
		ScoreObject so = score.catchScoreObject(p.x, p.y, Chord.class);

		if (so == null) {
			System.err.println("did not find a score object");
			return null;
		}

		NotationChord chord = (NotationChord) scoreMapper
				.getGraphicalToNotation().get(so);

		if (chord == null) {
			System.err.println("did not find a logical object");
			return null;
		}

		return chord.get(0);
	}

	private final Color[] selectionColors = new Color[] {
															Color.RED,
															Color.GREEN,
															Color.BLUE,
															Color.CYAN,
															Color.YELLOW,
															Color.MAGENTA,
															Color.ORANGE,
															Color.PINK,
															Color.RED,
															Color.RED
																	.brighter(),
															Color.GREEN
																	.brighter(),
															Color.BLUE
																	.brighter(),
															Color.CYAN
																	.brighter(),
															Color.YELLOW
																	.brighter(),
															Color.MAGENTA
																	.brighter(),
															Color.ORANGE
																	.brighter(),
															Color.PINK
																	.brighter(),
															Color.RED
																	.brighter(),
															Color.RED.darker(),
															Color.GREEN
																	.darker(),
															Color.BLUE.darker(),
															Color.CYAN.darker(),
															Color.YELLOW
																	.darker(),
															Color.MAGENTA
																	.darker(),
															Color.ORANGE
																	.darker(),
															Color.PINK.darker(),
															Color.RED.darker()};

	private boolean colorMulti;

	/**
	 * Color Multi makes the panel use different colors for selections. If false
	 * the selection is always blue.
	 * 
	 * @param multi
	 * @return
	 */
	public boolean setSelectionColorsMulti(boolean multi) {
		boolean oldMulti = multi;
		this.colorMulti = multi;
		return oldMulti;
	}

	private int colorCounter = 0;

	/**
	 * @see de.uos.fmt.musitech.framework.selection.SelectionListener#selectionChanged(SelectionChangeEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangeEvent e) {
		Selection sel = e.getSelection();
		HashMap notationToGraphical = scoreMapper.getNotationToGraphical();
		GraphicalSelection gSel = (GraphicalSelection) notationToGraphical
				.get(sel);
		if (gSel == null) {
			gSel = new GraphicalSelection();
			Color selectionColor = null;
			Collection content = sel.getAll();
			if (content != null) {
				for (Iterator iter = content.iterator(); iter.hasNext();) {
					Object element = iter.next();
					if (element instanceof RenderingSupported) {
						RenderingHints rh = ((RenderingSupported) element)
								.getRenderingHints();
						if (rh != null) {
							String str = (String) ((RenderingSupported) element)
									.getRenderingHints().getValue("color");
							if (str != null)
								selectionColor = rh.stringToColor(str);
						}
					}
				}
			}
			if (selectionColor == null) {
				if (colorMulti)
					selectionColor = selectionColors[colorCounter++
														% selectionColors.length];
				else
					selectionColor = Color.BLUE;
			}
			gSel.setColor(selectionColor);
			for (Iterator iter = sel.getAll().iterator(); iter.hasNext();) {
				Object notationElement = iter.next();
				ScoreObject element = (ScoreObject) notationToGraphical
						.get(notationElement);
				if (element == null)
					System.err.println("I don't have a graphical object for "
										+ notationElement);
				else
					gSel.add(element);
			}
			notationToGraphical.put(sel, gSel);
		}
		for (Iterator iter = e.removedObjects.iterator(); iter.hasNext();) {
			Object notationElement = iter.next();
			ScoreObject element = (ScoreObject) notationToGraphical
					.get(notationElement);
			if (element == null)
				System.err.println("I don't have a graphical object for "
									+ notationElement);
			else
				gSel.remove(element);
		}
		for (Iterator iter = e.addedObjects.iterator(); iter.hasNext();) {
			Object notationElement = iter.next();
			ScoreObject element = (ScoreObject) notationToGraphical
					.get(notationElement);
			if (element == null)
				System.err.println("I don't have a graphical object for "
									+ notationElement);
			else
				gSel.add(element);
		}
	}

	public NotationSystem getNotationSystem() {
		return notationSystem;
	}
}
