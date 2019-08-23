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
 * File NotationDisplay.java
 * Created on 13.04.2004
 */

package de.uos.fmt.musitech.score;

import java.awt.*;
import java.awt.print.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.rendering.*;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.*;
import de.uos.fmt.musitech.data.structure.*;
import de.uos.fmt.musitech.data.structure.container.*;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.time.MetricTimeable;
import de.uos.fmt.musitech.framework.editor.*;
import de.uos.fmt.musitech.framework.selection.*;
import de.uos.fmt.musitech.framework.selection.Selection;
import de.uos.fmt.musitech.performance.midi.MidiReader;
import de.uos.fmt.musitech.score.gui.ScorePanel;
import de.uos.fmt.musitech.time.TimeRange;
import de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class implements a Notation component based on a ScorePanel for the editing
 * framework.
 * 
 * @author tweyde
 */
public class NotationDisplay extends AbstractDisplay implements HorizontalTimedDisplay,
        MetricTimeable, SelectingEditor, Printable {

    ScorePanel scorePanel = new ScorePanel();
    private double zoom = 1;
    
    private final MultipleSelectionManager manager = MultipleSelectionManager.getManager();

    /**
     * Create an empty NatationDisplay
     */
    public NotationDisplay() {
        setBackground(Color.WHITE);
        setOpaque(true);

        /*
         * SelectionManager.getManager().addListener(scorePanel);
         * setSelectionController(new SelectionController(this)); MouseAdapter ma = new
         * MouseAdapter() { public void mouseClicked(MouseEvent e) { MObject mobj =
         * objectAt(e.getPoint()); if (mobj != null) { Selection sel =
         * SelectionManager.getManager().getSelection(); sel.add(mobj, this); } } };
         * addMouseListener(ma);
         */
        //addMouseListener(selectionController.getSelectionAdapter());
    }

    private final HashMap<Container, DistributedSelection> markupToSelection = new HashMap<Container, DistributedSelection>();

    /**
     * TODO add comment
     * @param marked
     */
    public void addMarkup(Container marked) {
        DistributedSelection sel = new DistributedSelection();
        sel.setSelectionManager(manager);
        manager.add(sel);
        markupToSelection.put(marked, sel);

        for (Iterator iter = marked.iterator(); iter.hasNext();) {
            MObject element = (MObject) iter.next();
            if(element instanceof RenderingSupported){
                RenderingSupported rs = (RenderingSupported) element;
                rs.setRenderingHints(marked.getRenderingHints());
            }
            sel.add(element, this);
        }
    }

    /**
     * TODO add comment
     * @param marked
     */
    public void removeMarkup(Container marked) {
        Selection sel = markupToSelection.get(marked);
        if (sel != null) {
        	sel.clear(this);
        	manager.remove(sel);
        }
        markupToSelection.remove(marked);
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#objectAt(java.awt.Point)
     */
    @Override
	public MObject objectAt(Point p) {
        return scorePanel.objectAt(p);
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#objectsTouched(java.awt.Rectangle)
     */
    @Override
	public Collection objectsTouched(Rectangle r) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#paintDragArea(java.awt.Rectangle)
     */
    @Override
	public void paintDragArea(Rectangle r) {
        // TODO Auto-generated method stub

    }

    private SelectionController selectionController;

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#setSelectionController(de.uos.fmt.musitech.framework.selection.SelectionController)
     */
    @Override
	public void setSelectionController(SelectionController c) {
        if (this.selectionController == c) {
            return;
        }
        removeMouseListener(this.selectionController);
        removeMouseMotionListener(this.selectionController);
        this.selectionController = c;
        //addMouseListener(c);
        addMouseMotionListener(c);
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#timeCovered(java.awt.Rectangle)
     */
    @Override
	public TimeRange timeCovered(Rectangle r) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getMinimalPositionForTime(long, de.uos.fmt.musitech.utility.math.Rational)
     */
    @Override
	public int getMinimalPositionForTime(long t, Rational m)
            throws WrongArgumentException {
        return scorePanel.getMinimalPositionForTime(t, m);
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#setMinimalPositionForTime(long, de.uos.fmt.musitech.utility.math.Rational, int)
     */
    @Override
	public boolean setMinimalPositionForTime(long t, Rational m, int position)
            throws WrongArgumentException {
        return scorePanel.setMinimalPositionForTime(t, m, position);
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#getNextPositioningTime(long)
     */
    @Override
	public long getNextPositioningTime(long startTime) {
        return scorePanel.getNextPositioningTime(startTime);
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.time.gui.HorizontalTimedDisplay#doInitialLayout()
     */
    @Override
	public void doInitialLayout() {
        scorePanel.doInitialLayout();
    }

    /**
     * TODO add comment
     * @see javax.swing.JComponent#setOpaque(boolean)
     */
    @Override
	public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (scorePanel != null) {
            scorePanel.setOpaque(isOpaque);
        }
    }

    /**
     * TODO add comment
     * @return
     * @see de.uos.fmt.musitech.framework.editor.AbstractSimpleEditor#applyChangesToPropertyValue()
     */
    public boolean applyChangesToPropertyValue() {
        // TODO Implement real checks
        return true;
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    @Override
	public void createGUI() {
    	currentStaff = null;
    	currentVoice = null;
    	system = null;
    	
        setLayout(new BorderLayout());

        Object data;
        if (getPropertyValue() != null) {
            data = getPropertyValue();
        } else {
            data = getEditObj();
        }

        remove(scorePanel);
        manager.removeListener(scorePanel);
        scorePanel = new ScorePanel();
        scorePanel.setOpaque(isOpaque());

        if (data instanceof NoteList) {
            scorePanel.setNotationSystem(createNotationSystem((NoteList) data));
        } else if (data instanceof NotationSystem) {
            processRenderingHints((NotationSystem) data);
            scorePanel.setNotationSystem((NotationSystem) data);

        } else if (data instanceof Piece) {
            Piece piece = (Piece) data;
            NotationSystem nsys = piece.getScore();
            if(nsys != null){
                processRenderingHints(nsys);
            } else{
                nsys = createNotationSystem((Piece) data);
//                if (((Piece)data).getContainerPool().getRenderingHint("@@@collins super hack@@@") != null) {
//                	System.err.println("filling the notationSystem with data from Testcase2_1");
//                	TestCase2_1.fillNotationSystem(nsys);
//                }
                processRenderingHints(nsys);
            }
            scorePanel.setNotationSystem(nsys);
        } //Erg?nzung K.N. 06/01/05
        	else if (data instanceof Container){
        	    scorePanel.setNotationSystem(NotationSystem.createNotationSystem((Container)data));
        	}
        // Ende Erg?nzung	
        else {
            throw new IllegalStateException("Unknown type of object " + data.getClass());
        }
        manager.addListener(scorePanel);
        add(scorePanel);
        updateSize();
        if(zoom!=1)
        	scorePanel.setZoom(zoom);
    }

    /**
     * Changes the border and updates the size.
     * 
     * @see javax.swing.JComponent#setBorder(javax.swing.border.Border)
     */
    @Override
	public void setBorder(Border b) {
        super.setBorder(b);
        if (scorePanel != null)
            updateSize();
    }

    /**
     * Updates the size properties of this display. 
     */
    public void updateSize() {
        Dimension dim = (Dimension) scorePanel.getSize().clone();
        Insets insets = getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.bottom + insets.top;

        if (DebugState.DEBUG_SCORE) {
            System.err.println("NotationDisplay.updateSize " + dim);
        }
        setSize(dim);
        setPreferredSize(dim);
    }

    /**
     * Updates the notation to changed score data.
     * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
     */
    @Override
	public void updateDisplay() {
    	system = null;
    	currentStaff = null;
    	currentVoice = null;
    	
        Object data = getPropertyValue();
        if (data == null) {
            data = getEditObj();
        }
        if (data instanceof NoteList) {
            scorePanel.setNotationSystem(createNotationSystem((NoteList) data));
        } else if (data instanceof NotationSystem) {
            processRenderingHints((NotationSystem) data);
            scorePanel.setNotationSystem((NotationSystem) data);
        } else {
            //throw new IllegalStateException("Unknown type of object " + data.getClass());
        }

        scorePanel.getScore().arrange();
        scorePanel.stateChanged(new ChangeEvent(scorePanel.getScore()));
        updateSize();
    }

    private static boolean isExtra(Object cont, NotationVoice parent,
                                   NotationSystem sys) {
        if (parent == null) {
            System.out.println("WARNING: NotationDisplay.isExtra() parent is null.");
            return false;
        }
        if (cont instanceof BeamContainer) {
            parent.addBeamContainer((BeamContainer) cont);
            return true;
        } else if (cont instanceof SlurContainer) {
            parent.addSlurContainer((SlurContainer) cont);
            return true;
        } else if (cont instanceof BarlineContainer) {
            sys.addBarlines((BarlineContainer) cont);
            return true;
        } else if (cont instanceof MetricAttachable) {
            parent.getParent().addAttachable((MetricAttachable) cont);
            return true;
        } else if (cont instanceof NotationStaffConnector) {
            NotationStaffConnector con = (NotationStaffConnector) cont;
            StaffContainer from = (StaffContainer) con.get(0);
            StaffContainer to = (StaffContainer) con.get(1);

            NotationStaffConnector fresh = (NotationStaffConnector) con.clone();
            fresh.clear();
            fresh.add(staffContainerToNotationStaff.get(from));
            fresh.add(staffContainerToNotationStaff.get(to));
            sys.addStaffConnector(fresh);
        }
        return false;
    }

    private static NotationVoice currentVoice;
    private static NotationStaff currentStaff;
    private static NotationSystem system;


    private static HashMap<StaffContainer,NotationStaff> staffContainerToNotationStaff = new HashMap<StaffContainer,NotationStaff>();

    /**
     * Creates a Score from the notes in the container Pool, making a 
     * best attempt to interpret the container structure. 
     * If the piece already has a score, nothing is done.
     * 
     */
    public static NotationSystem createNotationSystem(Piece piece) {
        
        //Erg���nzung K.N. 26.05
        if (piece!=null && piece.getScore()!=null){
            return piece.getScore();
        }
        //Ende Erg���nzung 

        Container<Container<?>> containerPool = piece.getContainerPool();

        NotationSystem nSystem = NotationSystem.createNotationSystem(containerPool);
        nSystem.setRenderingHints(containerPool.getRenderingHints());
        return nSystem;

//        NotationSystem createSystem = new NotationSystem(context);
//        NotationVoice createVoice = null;
//        for (Iterator iter = containers.iterator(); iter.hasNext();) {
//            Object element = iter.next();
//            if (element instanceof NoteList) { //construct a staff containing this voice
//                                               // (solely)
//                NotationStaff staff = new NotationStaff();
//
//                staffContainerToNotationStaff.put(element, staff);
//
//                createSystem.add(staff);
//                staff.add(createVoice);
//                createVoice.addAll((NoteList) element);
//                createVoice.setRenderingHints(((NoteList) element).getRenderingHints());
//                createVoice = new NotationVoice(context, staff);
//            } else if (isExtra(element, createVoice, createSystem)) {
//                //do nothing: everything is done as a side effect of isExtra
//            } else if (element instanceof StaffContainer) { //a container (representing a
//                                                            // staff) of voices
//                StaffContainer sc = (StaffContainer) element;
//                NotationStaff staff = new NotationStaff();
//
//                staffContainerToNotationStaff.put(element, staff);
//
//                if (sc.getClefTrack() != null) {
//                    staff.setClefTrack(sc.getClefTrack());
//                } else {
//                    staff.setClefTrack(new ClefContainer(context));
//                }
//                createSystem.add(staff);
//                NotationVoice innerCurrentVoice = null;
//                for (Iterator iterator = ((Container) element).iterator(); iterator
//                        .hasNext();) {
//                    Object innerElement = iterator.next();
//                    if (innerCurrentVoice != null
//                        && isExtra(innerElement, innerCurrentVoice, createSystem)) {
//                        //do nothing: everything is done as a side effect of isExtra
//                    } else if (innerElement instanceof Voice) { //a container in a staff
//                                                                // represents voices which
//                                                                // shall be visulaized by
//                                                                // chords
//                        innerCurrentVoice = new NotationVoice(context, staff);
//                        staff.add(innerCurrentVoice);
//                        for (Iterator innerIterator = ((Container) innerElement)
//                                .iterator(); innerIterator.hasNext();) {
//                            Object innerElement2 = innerIterator.next();
//                            if (isExtra(innerElement2, innerCurrentVoice, createSystem)) {
//                                //do nothing: everything is done as a side effect of
//                                // isExtra
//                            } else if (innerElement2 instanceof Container) {
//                                innerCurrentVoice.addAll((Container) innerElement2);
//                                innerCurrentVoice
//                                        .setRenderingHints(((Container) innerElement2)
//                                                .getRenderingHints());
//                            }
//                        }
//
//                    } else if (innerElement instanceof Container) {
//                        innerCurrentVoice = new NotationVoice(context, staff);
//                        staff.add(innerCurrentVoice);
//                        innerCurrentVoice.addAll((Container) innerElement);
//                        innerCurrentVoice.setRenderingHints(((Container) innerElement)
//                                .getRenderingHints());
//                    } else {
//                        throw new IllegalStateException("I cannot handle a "
//                                                        + innerElement.getClass()
//                                                        + " in this context");
//                    }
//                }
//                staff.setRenderingHints(((Container) element).getRenderingHints());
//            }
//        }
//
//        createSystem.setRenderingHints(containers.getRenderingHints());
//        return createSystem;
    }

    static NotationSystem createNotationSystem(Container<Note> noteCont) {
        return createNotationSystem(noteCont, 'g', -1, 0);
    }

    static NotationSystem createNotationSystem(Container<Note> noteCont, char clefType,
                                               int line, int shift) {
        NotationSystem createSystem = new NotationSystem(noteCont.getContext());
        NotationStaff staff = new NotationStaff(createSystem);
        NotationVoice voice = new NotationVoice(staff);
        for(Note n : noteCont)
        	voice.add(n);
//        staff.add(voice);
        ClefContainer clefTrack = new ClefContainer(noteCont.getContext());
        clefTrack.add(new Clef(clefType, line, shift));
        staff.setClefTrack(clefTrack);
//        createSystem.add(staff);

        if (noteCont != null) {
            RenderingHints hints = ((RenderingSupported) noteCont).getRenderingHints();

            createSystem.setRenderingHints(hints);
            processRenderingHints(createSystem);
        }

        return createSystem;
    }

    public static void processRenderingHints(NotationSystem processSystem) {
        RenderingHints hints = processSystem.getRenderingHints();

        if (hints == null || //default if no hints are given
            hints.getValue("barline") == null || //if it is explicitly
            // wanted
            !hints.getValue("barline").equals("none")) { //if hints are
            // given but
            // none about
            // barlines
            if (hints!=null && hints.getValue("gaps")!=null && !hints.getValue("gaps").equals("none"))
            for (int i = 0; i < processSystem.size(); i++) {
                NotationStaff staff = processSystem.get(i);
                for (int j = 0; j < staff.size(); j++) {
                    NotationVoice voice = staff.get(j);
                    voice.fillGaps();
                }
            }
            processSystem.insertBarlines();
        }
        processSystem.createBeams();
    }

    /*
     * @see java.awt.Component#getPreferredSize() public Dimension getPreferredSize() {
     *      Dimension prefSize = (Dimension)scorePanel.getPreferredSize().clone();
     * 
     * Insets i = getInsets(); prefSize.height += i.top + i.bottom; prefSize.width +=
     * i.left + i.right;
     * 
     * System.err.println("NotationDisplay.getPreferredSize " + prefSize);
     * 
     * return prefSize; }
     */

    /**
     * Just for testing.
     * @param args not used.
     */
    public static void main(String[] args) {
        MidiReader mr = new MidiReader();
        Piece work = mr.getPiece(MidiReader.class.getResource("import_2.mid"));
        //		Piece work = new Piece(MidiRe);
        NoteList nl = new NoteList(work.getContext());
        Containable contents[] = work.getContainerPool().getContentsRecursive();
        /*
         * for (int i = 0; i < contents.length; i++) { Containable containable =
         * contents[i]; if (containable instanceof Note) { Note note = (Note) containable;
         * nl.add(note); } }
         */
        NotationDisplay nd = new NotationDisplay();
        nd.init(nl, EditorFactory.getOrCreateProfile(nl), null);
        JFrame frame = new JFrame("Test NotationDisplay");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(nd);
        frame.pack();
        frame.show();
    }

    /**
     * Sets the automatic scaling of the score.
     * @see de.uos.fmt.musitech.score.gui.ScorePanel#setAutoZoom(boolean)
     * @param b true makes the component scale the score automatically, false lets it clip.
     */
    public void setAutoZoom(boolean b) {
        scorePanel.setAutoZoom(b);
    }

    /**
     * Sets the automatic scaling of the score.
     * @see de.uos.fmt.musitech.score.gui.ScorePanel#getAutoZoom()
     * @return true makes the component scale the score automatically, false lets it clip.
     */
    public boolean getAutoZoom() {
        return scorePanel.getAutoZoom();
    }

    /**
     * Set the background color for the score.
     * @see java.awt.Component#setBackground(java.awt.Color)
     */
    @Override
	public void setBackground(Color c) {
        if (scorePanel != null)
            scorePanel.setBackground(c);
        super.setBackground(c);
    }

    /**
     * Set the metric time for the crusor.
     * @see de.uos.fmt.musitech.data.time.MetricTimeable#setMetricTime(de.uos.fmt.musitech.utility.math.Rational)
     */
    @Override
	public void setMetricTime(Rational r) {
        if (scorePanel != null)
            scorePanel.setMetricTime(r);
    }
    
    public void setZoom(double zoom){
    	this.zoom = zoom;
    	if(scorePanel!=null)
    		scorePanel.setZoom(zoom);
    }

	public ScorePanel getScorePanel() {
		return scorePanel;
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if(getScorePanel()==null)
			return NO_SUCH_PAGE;
		return getScorePanel().print(graphics, pageFormat, pageIndex);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getScorePanel() == null	? super.getPreferredSize()
										: getScorePanel().getPreferredSize();
	}
	
	public double getZoom(){
		return zoom;
	}
}