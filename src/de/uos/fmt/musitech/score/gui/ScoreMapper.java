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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.rendering.RenderingSupported;
import de.uos.fmt.musitech.data.score.Attachable;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.Clef;
import de.uos.fmt.musitech.data.score.CustomGraphic;
import de.uos.fmt.musitech.data.score.DualMetricAttachable;
import de.uos.fmt.musitech.data.score.DynamicsMarker;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationStaffConnector;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.TablatureNote;
import de.uos.fmt.musitech.data.score.TupletContainer;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.ClefContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.framework.gui.Colored;
import de.uos.fmt.musitech.utility.DebugState;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class maps a music.NotationSystem to a GIN score.
 * 
 * @author Martin Gieseking, Collin Rogowski
 * @version $Revision: 8542 $, $Date: 2013-08-20 20:32:45 +0200 (Tue, 20 Aug 2013) $
 */
public class ScoreMapper {

    //private ScorePanel scorePanel; //TODO clarify use of this variable
    private Score score;
    private Map<Note,ScoreObject> hashtable = new HashMap<Note, ScoreObject>(); // maps music.Notes to gin.Pitches
    private MetricalTimeLine meterTrack;
    private Container<Marker> harmonyTrack;
    private Map<NotationStaff,Map<NotationVoice,Map>> slurs = new HashMap<NotationStaff,Map<NotationVoice,Map>>();
    //private boolean drawToolTips;
    private NotationSystem nsys;
    private boolean firstMeasureInStaff = true;
    private boolean firstMeasure = true;

    private HashMap notationToGraphical = new HashMap();
    private HashMap graphicalToNotation = new HashMap();

    private HashMap<NotationStaff,Rational> currentAttackTimes = new HashMap<NotationStaff, Rational>();

    public ScoreMapper(ScorePanel panel, NotationSystem nsys, //boolean drawToolTips,
            int maxWidth, int maxHeight) {
        //this.drawToolTips = drawToolTips;
        this.nsys = nsys;

        /*
         * nsys.remove(0);
         * 
         * NotationStaff staff = ((NotationStaff)nsys.get(0)); for (int i = 0; i < 2; i++) {
         * NotationVoice voice = (NotationVoice)staff.get(i); while (voice.size() > 4) {
         * voice.remove(voice.size() - 1); } }
         */

        //nsys.prettyPrint();
        /*
         * if (!nsys.isPrepared()) nsys.prepareForScore();
         */
        if (nsys.containsNotes()) {
            createView(panel, maxWidth, maxHeight);
        } else {
            createEmptyView(panel, maxWidth);
        }
    }

    public ScoreMapper(ScorePanel panel, NotationSystem nsys) {
//        this(panel, nsys, false);
    		this(panel, nsys, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

//    public ScoreMapper(ScorePanel panel, NotationSystem nsys //, boolean drawToolTips
//    		) {
//        this(panel, nsys, drawToolTips, Integer.MAX_VALUE, Integer.MAX_VALUE);
//    }

    private Staff[] createStaff(NotationStaff nstaff) {
        Staff[] staffs;
        Staff staff = new Staff();
        if (nstaff.getRenderingHints() != null
            && nstaff.getRenderingHints().containsKey("draw tabulatur")
            && ((Boolean) nstaff.getRenderingHints().getValue("draw tabulatur"))
                    .booleanValue()) {
            staffs = new Staff[2];
            TabulaturStaff tStaff = new TabulaturStaff(staff, graphicalToNotation,
                    notationToGraphical);
            staffs[1] = tStaff;
        } else {
            staffs = new Staff[1];
        }

        staffs[0] = staff;

        if (nstaff.getRenderingHint("number of lines") != null) {
            staff.setNumberOfLines(((Integer) nstaff.getRenderingHint("number of lines"))
                    .intValue());
        }

        for (Iterator iter = nstaff.iterator(); iter.hasNext();) {
            NotationVoice voice = (NotationVoice) iter.next();
            if (voice.getContextTimeLine() != null) {
                for (Iterator iterator = voice.getContextTimeLine().iterator(); iterator
                        .hasNext();) {
                    Object element = iterator.next();
                    if (element instanceof DynamicsMarker) {
                        DynamicsMarker dm = (DynamicsMarker) element;
                        staff.addCrescendo(dm.getMetricTime(), dm.getEnd(), dm
                                .isCrescendo() ? Crescendo.CRESCENDO
                                              : Crescendo.DECRESCENDO);
                    }

                }
            }
        }

        return staffs;
    }

    private static int noOfSystems = 0;

    private SSystem createSystem(NotationSystem nsys) {
        SSystem system = new SSystem();

        if (nsys.hasHint("interrupted barlines")) {
            system.setInterruptedBarlines((boolean[]) nsys
                    .getHint("interrupted barlines"));
        }

        for (int i = 0; i < nsys.size(); i++) {
            NotationStaff nstaff = nsys.get(i);
            if (!currentAttackTimes.containsKey(nstaff)) {
                currentAttackTimes.put(nstaff, Rational.ZERO);
            }

            Staff[] staffs = createStaff(nstaff);
            Staff staff = staffs[0];
            firstMeasureInStaff = true;
            if (nstaff.getRenderingHints() != null
                && "rhythm".equals(nstaff.getRenderingHints().getValue("staff lines")))
                staff.setRhythmStaff(true);
            if (nstaff.getRenderingHints() != null
                && nstaff.getRenderingHints().containsKey("scale")) {
                try {
                    float scale = Float.parseFloat(nstaff.getRenderingHints().getValue(
                            "scale").toString());
                    staff.setScale(scale);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // TODO remove invalid entry
                }
            }
            for (int j = 0; j < staffs.length; j++) {
                system.add(staffs[j]);
            }
        }
        if (nsys.getStaffConnectors() != null) {
            for (Iterator<NotationStaffConnector> iter = nsys.getStaffConnectors().iterator(); iter.hasNext();) {
                NotationStaffConnector element = iter.next();
                NotationStaff from = (NotationStaff) element.get(0);
                NotationStaff to = (NotationStaff) element.get(1);
                system.addStaffConnector(nsys.indexOf(from), nsys.indexOf(to), element
                        .getType());
            }
        }
        return system;
    }

    private boolean doneWithLayout(int[][] currentChordInVoices) {
        for (int i = 0; i < nsys.size(); i++) {
            NotationStaff nstaff = nsys.get(i);
            for (int j = 0; j < nstaff.size(); j++) {
                if (currentChordInVoices[i][j] < nstaff.get(j).size()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void createEmptyView(ScorePanel panel, int pageWidth) {
        score = new Score(panel);
        //this.scorePanel = panel;

        notationToGraphical.clear();
        Page page = new Page(pageWidth);
        SSystem system = new SSystem();
        Staff staff = new Staff();
        Measure measure = new Measure();
        page.add(system);
        system.add(staff);
        staff.add(measure);
        measure.setClef(new de.uos.fmt.musitech.score.gui.Clef('g', 0));
        score.add(page);
        score.arrange();
    }

    void initializePage(Page page) {
        if (nsys.getRenderingHint("page bottom space") != null) {
            page.setBottomSpace(((Integer) nsys.getRenderingHint("page bottom space"))
                    .intValue());
        }
    }

    long arrangeTime = 0;

    public void createView(ScorePanel panel, int maxWidth, int maxHeight) {
        notationToGraphical.clear();
        long overallStart = System.currentTimeMillis();
        long start;

        score = new Score(panel);
        //this.scorePanel = panel;
        this.meterTrack = nsys.getContext().getPiece().getMetricalTimeLine();
        this.harmonyTrack = nsys.getContext().getPiece().getHarmonyTrack();
        ArrayList systems = new ArrayList();

        int[][] currentChordInVoices = new int[nsys.size()][];
        TiedChord[][] openTies = new TiedChord[nsys.size()][]; //one (possible) open tie
        // for every voice
        for (int i = 0; i < nsys.size(); i++) {
            currentChordInVoices[i] = new int[nsys.get(i).size()];
            openTies[i] = new TiedChord[nsys.get(i).size()];
        }

        SSystem currentSystem = createSystem(nsys);
        Page currentPage = new Page();
        initializePage(currentPage);
        Measure[] leftOverMeasures = new Measure[nsys.size()];
        SSystem leftOverSystem = null;

        while (!doneWithLayout(currentChordInVoices) || leftOverMeasures[0] != null
               || leftOverSystem != null) {
            Barline measureEnd = null;
            int staffCounter = 0;
            for (int i = 0; i < nsys.size(); i++) {
                NotationStaff nstaff = nsys.get(i);

                Staff currentStaff;

                //find the primary staff belonging to the nstaff. This skips all
                // non-primary staffs (e.g. tablatures):
                do {
                    currentStaff = (Staff) currentSystem.child(staffCounter++);
                } while (currentStaff != null && !currentStaff.isPrimary());

                if (currentStaff == null || currentStaff.numChildren() == 0) {
                    firstMeasureInStaff = true;
                }

                Measure measure;
                if (leftOverMeasures[i] != null) {
                    measure = leftOverMeasures[i];
                    leftOverMeasures[i] = null;
                } else {
                    measure = createNextMeasure(nstaff, currentChordInVoices[i],
                            openTies[i]);
                }
                if (currentStaff != null) {
                    currentStaff.add(measure);
                    addMeasureAuxiliaries(currentSystem, currentStaff);
                }
                if (i == 0) {
                    measureEnd = new Barline(measure.attackTime().add(
                            measure.getCorrectDuration()));
                }
            }
            firstMeasure = false;

            currentSystem.removeEmptyStaffs();

            if (!currentSystem.containsEvents()) { //if the system does not have chords
                // or rests it is skipped
                continue;
            }

            if (nsys.getRenderingHint("scale") != null) {
                try {
                    float scale = Float.parseFloat(nsys.getRenderingHint("scale")
                            .toString());
                    currentSystem.setScale(scale);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    // TODO delete invalid entry
                }
            }

            start = System.currentTimeMillis();
            currentSystem.arrange();
            arrangeTime += (System.currentTimeMillis() - start);

            boolean tooBigSystem = currentSystem.getSize().width > maxWidth;
            if (tooBigSystem || nsys.hasHardBreakAt(measureEnd)) {
                firstMeasureInStaff = true; //the next to be created measure will be the
                // first in a new staff
                if (tooBigSystem && ((Staff) currentSystem.child(0)).numChildren() > 1) { // undo
                    // the
                    // last
                    // measures-adding
                    // round:
                    for (int i = 0; i < currentSystem.numChildren(); i++) {
                        Staff staff = (Staff) currentSystem.child(i);
                        int lastChild = staff.numChildren() - 1;
                        leftOverMeasures[i] = (Measure) staff.child(lastChild);
                        staff.removeScoreObject(lastChild);
                        initializeMeasure(leftOverMeasures[i], nsys
                                .get(i));
                        undoAuxiliaries(currentSystem, staff);
                    }
                    firstMeasureInStaff = false;
                }
                addSystemAuxiliaries(currentSystem);
                currentPage.add(currentSystem);
                start = System.currentTimeMillis();
                currentPage.arrange();
                arrangeTime += (System.currentTimeMillis() - start);
                boolean tooBigPage = currentPage.getSize().height > maxHeight;
                if (tooBigPage || nsys.hasHardBreakAt(measureEnd, nsys.getPagebreaks())) {
                    if (tooBigPage && currentPage.numChildren() > 1) {//undo the last
                        // system-adding:
                        leftOverSystem = (SSystem) currentPage.child(currentPage
                                .numChildren() - 1);
                        currentPage.removeScoreObject(currentPage.numChildren() - 1);
                    }
                    score.add(currentPage);
                    currentPage = new Page();
                    initializePage(currentPage);
                }
                if (leftOverSystem != null) {
                    currentSystem = leftOverSystem;
                    leftOverSystem = null;
                } else {
                    currentSystem = createSystem(nsys);
                }
            } else { //no linebreak
                firstMeasureInStaff = false;
            }
        }
        if (currentSystem != null) {
            currentSystem.removeEmptyStaffs();
            if (currentSystem.containsEvents()) {
                addSystemAuxiliaries(currentSystem);
                currentPage.add(currentSystem);
            }
        }
        if (currentPage != null && currentPage.numChildren() > 0) {
            score.add(currentPage);
        }

        if (openSlurs.size() != 0) {
            throw new IllegalStateException("there are still open slurs left");
        }

        /*
         * start = System.currentTimeMillis(); score.arrange(); arrangeTime +=
         * (System.currentTimeMillis() - start);
         */
        score.setActivePage(0);
        panel.setScore(score);

        if(DebugState.DEBUG_SCORE)
        	System.out.println("creating the view took "
                           + ((System.currentTimeMillis() - overallStart) / 1000.0)
                           + " seconds.\n" + (arrangeTime / 1000.0)
                           + " seconds were spent arranging.\n"
                           + (score.registeringTime / 1000.0)
                           + " seconds were spent registering");
    }

    public void createView(ScorePanel panel) {
        createView(panel, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    void checkForBeam(NotationChord nchord, NotationVoice voice, Event event,
                      Map beams) {
        int beamIndex = voice.belongsToBeam(nchord);
        if (beamIndex >= 0) {
            event.setInBeam(true);
            Integer beamIndexI = new Integer(beamIndex);
            if (!beams.containsKey(beamIndexI)) {
                beams.put(beamIndexI, new Beam());
            }
            Beam beam = (Beam) beams.get(beamIndexI);
            beam.add(event);
            RenderingHints rh = ((BeamContainer) voice.getBeamContainers().get(beamIndex))
                    .getRenderingHints();
            if (rh != null){
                if(rh.containsKey("linear duration progression")) {
                    beam.setLinearProgression((Rational[]) rh
                        .getValue("linear duration progression"));
                }
            }
        }
    }

    void checkForSlur(NotationChord nchord, NotationVoice voice, Event event,
                      Map slurs) {
        List<Integer> slurIndices = voice.belongsToSlur(nchord);
        for (Iterator iter = slurIndices.iterator(); iter.hasNext();) {
            Integer slurIndexI = (Integer) iter.next();
            if (!slurs.containsKey(slurIndexI)) {
                List v = new ArrayList();
                slurs.put(slurIndexI, v);
                Slur s = new Slur();
                SlurContainer sc = voice.getSlurContainers().get(
                        slurIndexI.intValue());
                if (sc.hasHint("dotted")
                    && ((Boolean) sc.getHint("dotted")).booleanValue()) {
                    s.setDotted(true);
                }
                v.add(s);
            }
            Slur slur = (Slur) ((List) slurs.get(slurIndexI)).get(((List) slurs
                    .get(slurIndexI)).size() - 1);
            slur.add(event);
        }
    }

    int checkForTuplet(NotationChord nchord, NotationVoice voice, Event event,
                       Map<Integer,Tuplet> tuplets) {
        int tupletIndex = voice.belongsToTuplet(nchord);
        if (tupletIndex >= 0) {
            Integer tupletIndexI = new Integer(tupletIndex);
            if (!tuplets.containsKey(tupletIndexI)) {
                TupletContainer tc = voice.getTupletContainers().get(
                        tupletIndex);
                tuplets.put(tupletIndexI, new Tuplet(tc.getArity(), tc
                        .getMetricDuration()));
            }
            Tuplet tuplet = tuplets.get(tupletIndexI);
            tuplet.add(event);
        }
        return tupletIndex;
    }

    private SSystem fetchSystem(List systems, List staves) {
        int last = staves.size() - 1;
        if (last < systems.size()) {
            return (SSystem) systems.get(last);
        } else {
            SSystem fresh = new SSystem();
            systems.add(fresh);
            return fresh;
        }
    }

    private Page fetchPage(List pages, int currentPage) {
        if (currentPage < pages.size()) {
            return (Page) pages.get(currentPage);
        } else {
            Page fresh = new Page();
            pages.add(fresh);
            return fresh;
        }
    }

    //This maps NotationStaffs to timeSignatures.
    //It is used to determine if the time signature changed and thus must be drawn
    private HashMap currentTimeSignatureMarker = new HashMap();

    private void initializeMeasure(Measure measure, NotationStaff nstaff) {
        KeyMarker km = null;
        if (harmonyTrack != null) 
        	for (Object object : harmonyTrack) {
        		if (object instanceof TimedMetrical) {
					TimedMetrical mt = (TimedMetrical) object;
					if ( mt.getMetricTime().isGreater(currentAttackTimes.get(nstaff)) )
						break;
				}        			
        		if(object instanceof KeyMarker && !(object instanceof ChordSymbol)) {
        			km = (KeyMarker) object;
        		}
        	}
        ClefContainer clefTrack = nstaff.getClefTrack();
        Clef defaultClef;
        if ("rhythm".equals(nstaff.getRenderingHint("staff lines"))) {
            defaultClef = new Clef('p', -1, 0);
        } else {
            defaultClef = new Clef('g', -1, 0);
        }

        Clef clef;
        if (clefTrack == null || clefTrack.size() == 0) {
            if (firstMeasureInStaff)
                clef = defaultClef;
            else
                clef = new Clef((char) 0, 0, 0);
        } else {
            Rational currentAttackTime = currentAttackTimes.get(nstaff);
            int index = clefTrack.find(currentAttackTime);
            if (index >= 0) {
                clef = clefTrack.get(index);
            } else if (index <= -2) {
                index += 2; //look at Collections.binarySearch in the Java API doc for an
                // explanation
                index *= -1;
                clef = clefTrack.get(index);
                if (!firstMeasureInStaff
                    && !clef.getMetricTime().equals(currentAttackTimes.get(nstaff))) {
                    clef = new Clef((char) 0, 0, 0);
                }
            } else {
                if (firstMeasureInStaff) {
                    clef = defaultClef;
                } else {
                    clef = new Clef((char) 0, 0, 0);
                }
            }
        }
        if (clef.getClefType() != 0
            &&
            //firstMeasureInStaff &&
            (nsys.getRenderingHints() == null
             || nsys.getRenderingHints().getValue("clef") == null || nsys
                    .getRenderingHints().getValue("clef").equals("standard"))) {
            de.uos.fmt.musitech.score.gui.Clef scoreClef = new de.uos.fmt.musitech.score.gui.Clef(
                    clef.getClefType(), clef.getClefLine());
            scoreClef.setTransposition(clef.getTransposition());
            if (clef.getRenderingHint("custom clef") != null) {
                scoreClef.setCustomGraphic((CustomGraphic) clef
                        .getRenderingHint("custom clef"));
            }

            measure.setClef(scoreClef);
        }

        if (meterTrack != null) {
            Rational currentAttackTime = currentAttackTimes.get(nstaff);
            TimeSignatureMarker tsm = meterTrack.getTimeSignatureMarker(currentAttackTime);
            if (nsys.getRenderingHints() == null
                || nsys.getRenderingHints().getValue("time signature") == null
                || nsys.getRenderingHints().getValue("time signature").equals("standard")) {
                if (!currentTimeSignatureMarker.containsKey(nstaff) //we are in the first
                                                                    // measure of the
                                                                    // staff
                    || (!tsm.getTimeSignature()
                            .equals(
                                    ((TimeSignatureMarker) currentTimeSignatureMarker
                                            .get(nstaff)).getTimeSignature())))
                // the time signature changed
                {
                    de.uos.fmt.musitech.data.time.TimeSignature ts = tsm
                            .getTimeSignature();
                    TimeSignature visualTs = new TimeSignature(ts.getNumerator(), ts
                            .getDenominator(), false);
                    if (tsm.getRenderingHints() != null
                        && tsm.getRenderingHints().containsKey("visible")
                        && !((Boolean) tsm.getRenderingHints().getValue("visible"))
                                .booleanValue()) {
                        visualTs.setVisible(false);
                    }
                    measure.setTimeSignature(visualTs);
                    currentTimeSignatureMarker.put(nstaff, tsm);
                }
            }
            if (km != null 
            		&& (firstMeasureInStaff 
            				|| (km.getMetricTime().isGreater(
            						currentAttackTimes.get(nstaff).
            							sub(tsm.getMetricDuration())
            						)
            					)
            			)
            	)
                measure.setKeySignature(new KeySignature(km.getAlterationNum()));
        }
        measure.setXPos(0);
    }

    private Map<Integer,Tuplet> tuplets = new HashMap();
    private Map beams = new HashMap();
    private Map chordCache;
    //This maps a Note to a slur...
    //If the note comes up in the rendering the corresponding chord will be added to the
    // slur:
    private HashMap openSlurs = new HashMap();

    Measure createNextMeasure(NotationStaff nstaff, int[] currentChordInVoice,
                              TiedChord[] currentTiedChordsInVoice) {
        Measure measure = new Measure();
        initializeMeasure(measure, nstaff);
        boolean firstChord = true;

        VOICES: for (int voiceNo = 0; voiceNo < nstaff.size(); voiceNo++) {
            NotationVoice voice = nstaff.get(voiceNo);

            Map currentSlurHash;
            if (slurs.containsKey(nstaff)) {
                currentSlurHash = slurs.get(nstaff);
            } else {
                currentSlurHash = new HashMap();
                slurs.put(nstaff, currentSlurHash);
            }
            if (currentSlurHash.containsKey(voice)) {
                currentSlurHash = (Map)currentSlurHash.get(voice);
            } else {
                Map h = new HashMap();
                currentSlurHash.put(voice, h);
                currentSlurHash = h;
            }

            for (int chordNo = currentChordInVoice[voiceNo]; chordNo < voice.size(); chordNo++) {
                NotationChord nchord = voice.get(chordNo);
                if (firstChord) {
                    measure.setMetricTime(nchord.getMetricTime());
                    firstChord = false;
                }
                currentAttackTimes.put(nstaff, nchord.getMetricTime());
                LocalSim lsim = measure.localSim(currentAttackTimes.get(nstaff));
                boolean addToLocalSim = true;

                if (lsim == null) {
                    lsim = new LocalSim(currentAttackTimes.get(nstaff));
                    measure.add(lsim);

                    if ("baroque".equals(nsys.getRenderingHint("alignment"))) {
                        lsim.setAlignment(LocalSim.BAROQUE_ALIGNMENT);
                    }
                }

                Rational duration = nchord.getSingleMetricDuration();
                int denom = duration.getDenom();
                //int numer = duration.getNumer();
                int dots = 0;
                Rational new_duration;
                if (duration.isLessOrEqual(new Rational(7, 4))) {
                    while (denom != 1 && duration.isGreater(new Rational(1, denom / 2))) {
                        duration = duration.sub(new Rational(1, denom / 2));
                        denom /= 2;
                    }
                    new_duration = new Rational(1, denom);
                    if (duration.isEqual(Rational.ZERO) || duration.isEqual(new_duration)) {
                        dots = 0;
                    } else if (duration.isGreater(new_duration.mul(1, 2))) {
                        dots = 2;
                    } else {
                        dots = 1;
                    }
                } else { //this is normally handled by the rendering hint "draw duration
                    // extension"
                    new_duration = duration;
                    dots = 0;
                }

                //                if(DebugState.DEBUG_SCORE)
                //                    System.out.println("ScoreMapper.createNextMesure() "+nchord.get(0));
                Duration dur = new Duration(new_duration, dots);
                Chord chord;

                if (nstaff.getRenderingHints() != null
                    && nstaff.getRenderingHints().containsKey("voices as chords")
                    && nstaff.getRenderingHints().getValue("voices as chords").equals(
                            new Boolean(true)) && voiceNo > 0) {
                    chord = (Chord) chordCache.get(nchord.getMetricTime());
                    addToLocalSim = false;
                } else {
                    if (nchord.hasTiedNote()) {
                        chord = new TiedChord(dur, voiceNo);
                    } else {
                        chord = new Chord(dur, voiceNo);
                    }
                    if (nstaff.getRenderingHints() != null
                        && nstaff.getRenderingHints().containsKey("voices as chords")
                        && nstaff.getRenderingHints().getValue("voices as chords")
                                .equals(new Boolean(true))) {
                        //we are at the first voice of a staff which shall be rendered as
                        // chords...
                        if (chordNo == 0) { //the first NotationChord of this staff...
                            // initialize
                            chordCache = new HashMap();
                        }
                        chordCache.put(nchord.getMetricTime(), chord);
                    }
                    if (nstaff.getRenderingHints() != null
                        && nchord.getRenderingHint("tremolo") != null) {
                        try {
                            chord.setTremolo(Integer.parseInt(nchord.getRenderingHint(
                                    "tremolo").toString()));
                        } catch (RuntimeException e) {
                            if (DebugState.DEBUG_SCORE)
                                e.printStackTrace();
                        }
                    }
                }

                if ((new Boolean(false)).equals(nchord.getRenderingHint("draw stem"))) {
                    chord.setDrawStem(false);
                }

                Event event = chord;

                if ((nchord.getRenderingHint("tabulatur note") != null && ((TablatureNote) nchord
                        .getRenderingHint("tabulatur note")).getPullUp() != null)
                    || (nchord.get(0).getRenderingHint("tabulatur note") != null
                        && ((TablatureNote) nchord.get(0)
                                .getRenderingHint("tabulatur note")).getPullUp() != null && ((TablatureNote) nchord
                            .get(0).getRenderingHint("tabulatur note"))
                            .getPullUpTarget() != null)) {

                    TablatureNote tn = (TablatureNote) nchord
                            .getRenderingHint("tabulatur note");
                    if (tn == null) {
                        tn = (TablatureNote) nchord.get(0)
                                .getRenderingHint("tabulatur note");
                    }
                    NotationChord entryChord = new NotationChord(nchord.getContext());
                    if (tn.getPullUpTarget() != null)
                        entryChord.add(tn.getPullUpTarget());
                    nchord.setEntryChord(new NotationChord[] {entryChord});
                    nchord.getEntryChord()[0].addRenderingHint("draw stem", new Boolean(
                            false));

                    if (tn.getPullDownTarget() == null && tn.getPullDownTargetInt() != -1) {
                        int index = voice.indexOf(nchord);
                        index += tn.getPullDownTargetInt();
                        tn.setPullDownTarget(voice.get(index)
                                .get(0));
                    }

                    if (tn.getPullDownTarget() != null) {
                        Slur slur = new Slur();
                        slur.add(chord);
                        if (!currentSlurHash.containsKey(new Integer(-1))) {
                            currentSlurHash.put(new Integer(-1), new ArrayList());
                        }
                        ((List) currentSlurHash.get(new Integer(-1))).add(slur);
                        slur.setPullDown(true);
                        slur.setLongPullDown(tn.isLongPullDown());
                        slur.setPullDownShift(tn.getPullDownShift());

                        openSlurs.put(tn.getPullDownTarget(), slur);
                        if (DebugState.DEBUG_SCORE)
                            System.out.println("waiting for note "
                                               + tn.getPullDownTarget());
                    }
                }

                if (nchord.getEntryChord() != null) {
                    //TODO: iterate over array, don't just take the first chord
                    Duration entryDur = new Duration(nchord.getEntryChord()[0]
                            .getMetricDuration(), 0);
                    Chord entryChord = new Chord(entryDur, voiceNo);

                    if (nchord.getEntryChord()[0].getRenderingHint("draw stem") != null) {
                        entryChord.setDrawStem(((Boolean) nchord.getEntryChord()[0]
                                .getRenderingHint("draw stem")).booleanValue());
                    }

                    //TODO: iterate over array, don't just take the first chord
                    ScoreNote note = nchord.getEntryChord()[0].get(0).getScoreNote();
                    //TODO: iterate over array, don't just take the first chord
                    Pitch entryPitch = new Pitch(dur, note.getDiatonic(), (byte) (note
                            .getAlteration()), false, (byte)(note.getOctave()), nchord.getEntryChord()[0].get(0));
                    entryChord.add(entryPitch);
                    entryChord.setScale(0.5f);
                    entryChord.setStemDirection(Chord.STEM_UP);
                    chord.setEntryChord(entryChord);

                    Slur slur = new Slur();
                    slur.add(entryChord);
                    slur.add(chord);
                    if (!currentSlurHash.containsKey(new Integer(-1))) {
                        currentSlurHash.put(new Integer(-1), new ArrayList());
                    }
                    ((List) currentSlurHash.get(new Integer(-1))).add(slur);

                    if ((nchord.getRenderingHint("tabulatur note") != null 
                    	&& ((TablatureNote) nchord.getRenderingHint("tabulatur note")).getPullUp() != null)
                        || (nchord.get(0).getRenderingHint("tabulatur note") != null 
                        && ((TablatureNote) (nchord.get(0)).getRenderingHint("tabulatur note")).getPullUp() != null)) 
                    {
                        slur.setPullUp(true);
                        slur.setPullDownShift(((TablatureNote) nchord.get(0)
                                .getRenderingHint("tabulatur note")).getPullDownShift());
                    }
                }
                if (nchord.hasAccents()) {
                    event.setAccent(new Accent(nchord.getAccents()[0]));
                }
                if (nchord.getRenderingHints() != null) {
                    if (nchord.getRenderingHints()
                            .containsKey("duplicate of other voice")
                        && !(nstaff.getRenderingHints() != null && nstaff
                                .getRenderingHints().containsKey("voices as chords"))) {
                        chord.setVisible(false);
                    }
                    if (nchord.getRenderingHints().containsKey("scale")) {
                        event.setScale(((Float) nchord.getRenderingHints().getValue(
                                "scale")).floatValue());
                    }
                    if (nchord.getRenderingHints().containsKey("stem direction")) {
                        String direction = (String) nchord.getRenderingHints().getValue(
                                "stem direction");
                        if ("up".equals(direction))
                            ((Chord) event).setStemDirection(Chord.STEM_UP);
                        else if ("down".equals(direction))
                            ((Chord) event).setStemDirection(Chord.STEM_DOWN);
                    }
                }

                notationToGraphical.put(nchord, event);
                for (Note element: nchord) {
                    //Note element = (Note) iter.next();
                    notationToGraphical.put(element, event);

                    if (openSlurs.containsKey(element)) {
                        System.err.println("adding note " + element);
                        ((Slur) openSlurs.get(element)).add(event);
                        openSlurs.remove(element);
                    }
                }
                graphicalToNotation.put(event, nchord);
                
                for (int pitchNo = 0; pitchNo < nchord.size(); pitchNo++) {
                    ScoreNote note = nchord.get(pitchNo).getScoreNote();
                    if (note.getDiatonic() >= 'a' && note.getDiatonic() <= 'h') {
                        Pitch pitch = new Pitch(dur, note.getDiatonic(), 
                        	(byte)note.getAlteration(), false, (byte)note.getOctave(), 
                        	nchord.get(pitchNo));

                        chord.add(pitch);

                        graphicalToNotation.put(pitch, nchord.get(pitchNo));

                        if (nchord.getRenderingHints() != null
                            && nchord.getRenderingHints().containsKey(
                                    "draw duration extension")
                            && ((Boolean) nchord.getRenderingHints().getValue(
                                    "draw duration extension")).booleanValue()) {
                            chord.setDrawDurationExtension(true);
                        }

                        if (nchord.getRenderingHints() != null
                            && nchord.getRenderingHints().containsKey(
                                    "duration extension pulldown")) {
                            chord.setDurationExtensionPulldown(((Float) nchord
                                    .getRenderingHint("duration extension pulldown"))
                                    .floatValue());
                        }
                        
                        if (nchord.getRenderingHints() != null
							&& nchord.getRenderingHints()
									.containsKey("visible")) {
							pitch.setVisible(((Boolean) nchord
									.getRenderingHint("visible"))
									.booleanValue());
						}
                        
                        if (nchord.get(pitchNo).getRenderingHints() != null
							&& nchord.get(pitchNo).getRenderingHints()
									.containsKey("visible")) {
							pitch.setVisible(((Boolean) nchord
									.get(pitchNo).getRenderingHint("visible"))
									.booleanValue());
						}

                        hashtable.put(nchord.get(pitchNo), pitch);
                    } else { // non-diatonic characters interpreted as rests
                        if (nchord.getMetricDuration().isGreater(new Rational(1, 1))) {
                            dur = new Duration(nchord.getMetricDuration(), 0);
                        }

                        event = new Rest(dur, voiceNo, nchord.get(pitchNo));
                        hashtable.put(nchord.get(pitchNo), event);
                        if (nchord.getRenderingHints() != null
                            && nchord.getRenderingHints().containsKey("visible")) {
                            event.setVisible(((Boolean) nchord
                                    .getRenderingHint("visible")).booleanValue());
                        }
                        Note n = nchord.get(pitchNo);
                        if (n.getRenderingHints() != null
                            && n.getRenderingHints().containsKey("visible")) {
                            event.setVisible(((Boolean) n.getRenderingHint("visible"))
                                    .booleanValue());
                        }
                    }
                    if (nchord.get(pitchNo) instanceof RenderingSupported) {
                        RenderingHints rh = ((RenderingSupported) nchord.get(pitchNo))
                                .getRenderingHints();
                        if (rh != null && rh.getValue("color") != null) {
                            Color col = rh.stringToColor(rh.getValue("color").toString());
                            event.setColor(col);
                        }
                    }
                    // this is left here for backwards compatibility.
                    if (nchord.get(pitchNo) instanceof Colored) {
                        Colored cn = (Colored) nchord.get(pitchNo);
                        event.setColor(cn.getColor());
                    }
                }

                checkForBeam(nchord, voice, event, beams);
                checkForSlur(nchord, voice, event, currentSlurHash);
                int currentTupletIndex = checkForTuplet(nchord, voice, event, tuplets);

                if (addToLocalSim)
                    lsim.add(event);

                if (nstaff.getRenderingHints() != null
                    && //there're rendering hints
                    nstaff.getRenderingHints().containsKey("note spacing")
                    && //they tell us about notespacing
                    (!nstaff.getRenderingHints().containsKey("spaced notes") || //the key
                    // is not
                    // there
                    // => all
                    // notes
                    // should
                    // be
                    // spaced
                    ((Rational) nstaff.getRenderingHints().getValue("spaced notes"))
                            .equals(lsim.getMetricDuration()))) { //the key is there =>
                    // only notes with this
                    // duration should be
                    // spaced
                    lsim.setSpacingFactor(((Float) nstaff.getRenderingHints().getValue(
                            "note spacing")).floatValue());
                }

                if (currentTiedChordsInVoice[voiceNo] != null) { //the last chord was a
                    // tied chord. the
                    // current chord is its
                    // partner
                    currentTiedChordsInVoice[voiceNo].setSuccessor(chord);
                }

                if (chord instanceof TiedChord) {
                    currentTiedChordsInVoice[voiceNo] = (TiedChord) chord;
                } else {
                    currentTiedChordsInVoice[voiceNo] = null;
                }

                Rational correctDuration;
                if (currentTupletIndex != -1) {
                    TupletContainer tuplet = voice
                            .getTupletContainers().get(currentTupletIndex);
                    correctDuration = tuplet.getMetricDuration().div(tuplet.size(), 1);
                } else {
                    correctDuration = dur.toRational();
                }
                event.setCorrectDuration(correctDuration);

                Rational currentAttackTime = currentAttackTimes.get(nstaff);
                if ((nsys.getBarlines().hasBarlineAt(
                        currentAttackTime.add(correctDuration)) && measure.numChildren() > 0)
                    || nsys.hasHardBreakAt(new Barline(currentAttackTime
                            .add(correctDuration)))) {
                    Barline barline = nsys.getBarlines().getBarlineAt(
                            currentAttackTime.add(correctDuration));

                    if (barline != null) {
                        de.uos.fmt.musitech.score.gui.Barline graphicalBarline;
                        if (barline.getRenderingHint("custom") != null) {
                            float[] fromTo = (float[]) barline.getRenderingHint("custom");
                            graphicalBarline = new BarlineCustom(fromTo[0], fromTo[1]);
                        } else if (barline.isDouble()) {
                            graphicalBarline = new BarlineDouble();
                        } else {
                            graphicalBarline = new BarlineSingle();
                        }

                        if (barline.getRenderingHint("preview") != null) {
                            graphicalBarline.setPreview((CustomGraphic) barline
                                    .getRenderingHint("preview"));
                        } else if (barline.getRenderingHint("time signature preview") != null) {
                            graphicalBarline.setTimeSignaturePreview((int[]) barline
                                    .getRenderingHint("time signature preview"));
                        }

                        if (barline.getRenderingHint("visible") != null) {
                            graphicalBarline.setVisible(((Boolean) barline
                                    .getRenderingHint("visible")).booleanValue());
                        }

                        measure.setRightBarline(graphicalBarline);
                    }
                    currentChordInVoice[voiceNo] = ++chordNo;

                    continue VOICES;
                }

                if (chordNo == voice.size() - 1) { //we are on the last chord
                    currentChordInVoice[voiceNo] = ++chordNo;
                }
            }
        }
        //all voices are done:
        /*
         * for (int voiceNo = 0; voiceNo < nstaff.size(); voiceNo++) {
         * currentChordInVoice[voiceNo] = ((NotationVoice)nstaff.get(voiceNo)).size(); }
         */

        if (measure.numChildren() != 0) {
            Rational[] minMax = measure.minMaxTime(true);
            if (nstaff.getAttachables() != null) {
                for (Iterator iter = nstaff.getAttachables().iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    if (o instanceof MetricAttachable) {
                        MetricAttachable element = (MetricAttachable) o;
                        if (element.getMetricTime().isGreaterOrEqual(minMax[0])
                            && element.getMetricTime().isLessOrEqual(minMax[1])) {
                            ScoreObject anker = null;
                            if (element.getAnker() != null) {
                                anker = (ScoreObject) notationToGraphical.get(element
                                        .getAnker());
                            }
                            CustomScoreObject ccso;
                            if (element instanceof DualMetricAttachable) {
                                ScoreObject rightAnker = null;
                                DualMetricAttachable dma = (DualMetricAttachable) element;
                                ccso = CustomScoreObject.createCustomDualScoreObject(
                                        measure, anker, this, dma);
                            } else {
                                ccso = CustomScoreObject.createCustomScoreObject(measure,
                                        anker, element);
                            }
                            measure.addCustomScoreObject(ccso);
                        }
                    } else if (o instanceof Attachable) {
                        Attachable element = (Attachable) o;
                        if (!(element.getAnker() instanceof Note)) {
                            throw new IllegalArgumentException(
                                    "only Notes are allowed as anchors for Attachables");
                        }
                        Event event = (Event) notationToGraphical.get(element.getAnker());
                        if (event != null
                            && event.getMetricTime().isGreaterOrEqual(minMax[0])
                            && event.getMetricTime().isLessOrEqual(minMax[1])) {
                            CustomScoreObject ccso = CustomScoreObject
                                    .createCustomScoreObject(measure, event, element);
                            measure.addCustomScoreObject(ccso);
                        }
                    }
                }
            }
        }

        currentAttackTimes.put(nstaff, measure.getMetricEndPoint()); //the beginning of
        // the next measure

        return measure;
    }

    void addMeasureAuxiliaries(SSystem system, Staff staff) {
        addBeams(system, beams);
        addTuplets(system, staff, tuplets);
    }

    void addSystemAuxiliaries(SSystem system) {
        addSlurs(system, slurs);
    }

    void undoAuxiliaries(SSystem system, Staff staff) {
        int i = 0;
        for (Iterator iter = undoBeams.get(system).iterator(); iter.hasNext();) {
            Beam beam = (Beam) iter.next();
            system.removeBeam(beam);
            beams.put(new Integer(++i), beam);
        }
        /*
         * i = 0; for (Iterator iter = ((List)undoSlurs.get(system)).iterator();
         * iter.hasNext();) { Slur slur = (Slur)iter.next(); system.removeSlur(slur); List
         * wrapper = new ArrayList(1); wrapper.add(slur); slurs.put(new Integer(++i),
         * wrapper); }
         */
        i = 0;
        for (Iterator iter = ((List) undoTuplets.get(staff)).iterator(); iter.hasNext();) {
            Tuplet tuplet = (Tuplet) iter.next();
            staff.removeTuplet(tuplet);
            tuplets.put(new Integer(++i), tuplet);
        }
    }

    private Map<SSystem,List> undoBeams = new HashMap<SSystem,List>();

    void addBeams(SSystem system, Map beams) {
        List undo = new ArrayList();
        undoBeams.put(system, undo);
        Iterator i = beams.values().iterator();
        while (i.hasNext()) {
            Beam element = (Beam) i.next();
            system.addBeam(element);
            undo.add(element);
        }
        beams.clear();
    }

    private Map<SSystem,List> undoSlurs = new HashMap<SSystem,List>();

    /**
     * get all Slurs belonging to system and add them to the system
     * 
     * @param system the system which shall be filled with its slurs
     * @param slurs a hashtable of all slurs across all systems
     */
    void addSlurs(SSystem system, Map<NotationStaff,Map<NotationVoice,Map>> slurs) {
        /*
         * List undo = new ArrayList(); undoSlurs.put(system, undo);
         */
        Collection<Map<NotationVoice,Map>> slurHashes = slurs.values();
        for (Map<NotationVoice,Map> slurMap : slurHashes) {
            Collection<Map> e = slurMap.values();
            for (Map h : e) {
                Collection<List> e2 = h.values();
                for (List element : e2) {
                    // go through all slurs:
                    for (Iterator iter = element.iterator(); iter.hasNext();) {
                        Slur slur = (Slur) iter.next();
                        Staff staff = (Staff) slur.getEvent(0).getParent(Staff.class);

                        if (system.contains(staff)) {
                            system.addSlur(slur);
                            //undo.add(slur);
                        }
                    }
                }
            }
        }

        /*
         * //check if all events belonging to one slur are in the same Staff: if
         * (slur.spannerSize() > 0 && event.getParent(Staff.class) !=
         * slur.get(0).getParent(Staff.class)) { //they are not //draw two slurs, dangling
         * in the air: slur.setDangling(Slur.RIGHT_DANGLING); Slur nextSlur = new Slur();
         * nextSlur.setDangling(Slur.LEFT_DANGLING);
         * ((List)slurs.get(slurIndexI)).add(nextSlur); nextSlur.add(event); } else {
         * slur.add(event); }
         */
        //TODO: we may have to do this:
        //slurs.clear();
    }

    private HashMap undoTuplets = new HashMap();

    void addTuplets(SSystem system, Staff staff, Map<Integer,Tuplet> tuplets) {
        List undo = new ArrayList();
        undoTuplets.put(staff, undo);
        Iterator i = tuplets.values().iterator();
        while (i.hasNext()) {
            Tuplet element = (Tuplet) i.next();
            staff.addTuplet(element);
            undo.add(element);
            if (element.containsRest()
                || element.getEvent(0).getDuration().getBase().isGreater(
                        new Rational(1, 8))) {
                Slur slur = new Slur();
                slur.setAtStem(true);
                for (Iterator j = element.iterator(); j.hasNext();) {
                    Event event = (Event) j.next();
                    slur.add(event);
                }
                system.addSlur(slur);
                element.setSlur(slur);
                if (!undoSlurs.containsKey(system)) {
                    List undo2 = new ArrayList();
                    undoSlurs.put(system, undo2);
                }
                undoSlurs.get(system).add(slur);
            } else {
                Beam beam = new Beam();
                element.setBeam(beam);
                for (Iterator j = element.iterator(); j.hasNext();) {
                    Event event = (Event) j.next();
                    event.setInBeam(true);
                    beam.add(event);
                }
                system.addBeam(beam);
                if (!undoBeams.containsKey(system)) {
                    List undo2 = new ArrayList();
                    undoBeams.put(system, undo2);
                }
                undoBeams.get(system).add(beam);
            }
        }
        tuplets.clear();
    }

    /**
     * Changes the color of notes given by an array.
     * 
     * @param notes notes to colorize
     * @param color new color
     */
    public void setColor(Note[] notes, Color color) {
        for (int i = 0; i < notes.length; i++) {
            Pitch pitch = (Pitch) hashtable.get(notes[i]);
            if (pitch != null)
                pitch.setColor(color);
        }
    }

    /**
     * Changes the color of notes given by a collection.
     * 
     * @param notes notes to colorize
     * @param color new color
     */
    public void setColor(Collection notes, Color color) {
        Iterator it = notes.iterator();
        while (it.hasNext()) {
            Pitch pitch = (Pitch) hashtable.get(it.next());
            if (pitch != null)
                pitch.setColor(color);
        }
    }

    public Score getScore() {
        return score;
    }

    Map<Note, ScoreObject> getNoteMap() {
        return hashtable;
    }

    public HashMap getGraphicalToNotation() {
        return graphicalToNotation;
    }

    public HashMap getNotationToGraphical() {
        return notationToGraphical;
    }
}