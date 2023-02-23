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
 * Created on 08.01.2005
 */
package de.uos.fmt.musitech.performance;

import java.util.Iterator;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteMulti;
import de.uos.fmt.musitech.data.performance.MidiNoteSysEx;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.performance.rendering.MidiNoteRendering;
import de.uos.fmt.musitech.data.score.DynamicsLevelMarker;
import de.uos.fmt.musitech.data.score.DynamicsMarker;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.utility.collection.SortedCollection;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Jan
 *  
 */
public class ScoreToPerfomance {

    NotationSystem notSystem;

    Container harmonieTrack;

    Piece piece;

    boolean enableOverwritePerfomanceNote = true;

    MetricalTimeLine pieceTimeLine;

    MetricalTimeLine voiceTimeLine;

    double staccato = 0.25, portato = 0.8;

    int trillTime = 100000; // time for next Trill

    int defaultVelo = 90;

    int crescendoDiv = 24;

    int veloDown = 25; // to reduce velocity from notes that are *not* in VeloUpContainer

    int veloUp = 10; // to increase velo from notes that are in VeloUpContainer

    int marcarto = 10; // velocity

    int alignment = 0;

    private final int BAROQUEALIGNMENT = 1;

    SortedCollection[] voiceDyn; // Dynamic Markers in each voice

    private Container veloUpCont;

    /**
     *  
     */
    public ScoreToPerfomance() {
    }

    public ScoreToPerfomance(NotationSystem notSystem) {
        this(notSystem, null);

    }

    public ScoreToPerfomance(NotationSystem notSystem, Container velUp) {
        this.veloUpCont = velUp;
        setNotationSystem(notSystem);
        init();
        convertPiece();
    }

    /**
     *  
     */
    private void init() {
        piece = notSystem.getContext().getPiece();
        pieceTimeLine = piece.getMetricalTimeLine();
        harmonieTrack = piece.getHarmonyTrack();
    }

    /**
     *  
     */
    private void convertPiece() {
        // check BaroqueAligment
        if (piece.getContainerPool().getRenderingHints() != null)
            if (piece.getContainerPool().getRenderingHint("alignment")
                    .toString().equals("baroque"))
                alignment = BAROQUEALIGNMENT;

        checkDynamics(piece);

        for (Iterator iter = notSystem.iterator(); iter.hasNext();) {
            NotationStaff staff = (NotationStaff) iter.next();

            for (Iterator iter2 = staff.iterator(); iter2.hasNext();) {
                NotationVoice voice = (NotationVoice) iter2.next();
                voiceTimeLine = voice.getContextTimeLine();
                int indV = 0;
                for (Iterator iterator = voice.iterator(); iterator.hasNext();) {

                    Object element = iterator.next();
                    if (element instanceof NotationChord) {
                        NotationChord notChord = (NotationChord) element;
                        convertChord(notChord, indV);
                    }

                }
                lastScoreNote = null;
            }
        }
    }

    /**
     * @param piece2
     */
    private void checkDynamics(Piece piece2) {
        int voices = getNumOfNotationElements(piece)[2];
        voiceDyn = new SortedCollection[voices];
        for (int i = 0; i < voices; i++) {
            voiceDyn[i] = new SortedCollection(Marker.class,
                    new MetricalComparator());
        }

        putDynamicsToAllVoices(piece.getHarmonyTrack().getContentsRecursive());
        int indV = 0;
        for (Iterator iter = notSystem.iterator(); iter.hasNext();) {
            NotationStaff staff = (NotationStaff) iter.next();
            if (staff.getContextTimeLine() != null)
                putDynamicsToAllVoices(staff.getContextTimeLine()
                        .getContentsRecursive());
            for (Iterator iter2 = staff.iterator(); iter2.hasNext();) {

                NotationVoice voice = (NotationVoice) iter2.next();
                if (voice.getContextTimeLine() != null)
                    putDynamicsToVoice(voice.getContextTimeLine()
                            .getContentsRecursive(), indV);

                indV++;
            }
        }

    }

    /**
     * @param contentsRecursive
     * @param indV
     */
    private void putDynamicsToVoice(Containable[] cont, int indV) {
        for (int i = 0; i < cont.length; i++) {
            if (cont[i] instanceof DynamicsMarker) {
                DynamicsMarker marker = (DynamicsMarker) cont[i];
                voiceDyn[indV].add(marker);
            }
            if (cont[i] instanceof DynamicsLevelMarker) {
                DynamicsLevelMarker marker = (DynamicsLevelMarker) cont[i];
                voiceDyn[indV].add(marker);
            }
        }
    }

    private void putDynamicsToAllVoices(Containable[] cont) {
        for (int i = 0; i < cont.length; i++) {
            if (cont[i] instanceof DynamicsMarker) {
                DynamicsMarker marker = (DynamicsMarker) cont[i];
                for (int j = 0; j < voiceDyn.length; j++) {
                    voiceDyn[j].add(marker);
                }
            }
            if (cont[i] instanceof DynamicsLevelMarker) {
                DynamicsLevelMarker marker = (DynamicsLevelMarker) cont[i];
                for (int j = 0; j < voiceDyn.length; j++) {
                    voiceDyn[j].add(marker);
                }
            }
        }
    }

    private int[] getNumOfNotationElements(Piece piece) {
        int system = 0;
        int staffs = 0;
        int voices = 0;
        for (Iterator iter = notSystem.iterator(); iter.hasNext();) {
            NotationStaff staff = (NotationStaff) iter.next();
            staffs++;
            for (Iterator iter2 = staff.iterator(); iter2.hasNext();) {
                NotationVoice voice = (NotationVoice) iter2.next();
                voices++;

            }
        }
        int[] elements = { system, staffs, voices };
        return elements;
    }

    ScoreNote lastScoreNote;

    /**
     * @param notChord1
     */
    private void convertChord(NotationChord notChord1, int voice) {
        for (Iterator<Note> iter = notChord1.iterator(); iter.hasNext();) {
            Note note = iter.next();
            ScoreNote scorNote = note.getScoreNote();
            PerformanceNote perfNote = new PerformanceNote();
            // Time
            boolean align = false;
            if (lastScoreNote != null && alignment == BAROQUEALIGNMENT) {

                Rational lastTime = lastScoreNote.getMetricDuration();
                Rational thisTime = scorNote.getMetricDuration();
                if (thisTime.getDenom() > pieceTimeLine
                        .getCurrentTimeSignature(thisTime).getDenom()
                        || lastTime.getDenom() > pieceTimeLine
                                .getCurrentTimeSignature(thisTime).getDenom()) {

                    // compare Numerator
                    if (lastTime.div(thisTime).isEqual(3, 1)) {
                        // found notes with begin time 1:3
                        align = true;

                    }
                }

            }
            if (align) {
                perfNote.setTime(getTime(scorNote, lastScoreNote));

                perfNote.setDuration(getDuration(scorNote, true));
            } else {
                perfNote.setTime(getTime(scorNote));
                perfNote.setDuration(getDuration(scorNote, false));
            }

            perfNote.setVelocity(getVelocity(note, voice));

            perfNote.setPitch(getPitch(scorNote));

            if (enableOverwritePerfomanceNote) {
                note.setPerformanceNote(perfNote);
            }
            if (scorNote.getDetune() != 0) {
                note.setPerformanceNote(makeDetune(scorNote, perfNote));
            }

            if (note.getRenderingHints() != null) {

                if (note.getRenderingHint("tremolo") != null)
                    if (note.getRenderingHint("tremolo").toString().length() == 1) {
                        int type;
                        try {
                            type = Integer.parseInt(note.getRenderingHint(
                                    "tremolo").toString());
                        } catch (Exception e) {
                            type = 0;
                        }

                        note.setPerformanceNote(makeTremolo(scorNote, perfNote,
                                type));
                    }
            }

            if (scorNote.getMidiRendering() != null) {
                note.setPerformanceNote(makeMidiRendering(scorNote, perfNote));
            }

            if (scorNote.getAccents() != null) {
                if (isTrill(scorNote))
                    note.setPerformanceNote(makeTriller(scorNote, perfNote));
                if (isMordentUp(scorNote))
                    note
                            .setPerformanceNote(makeMordent(scorNote, perfNote,
                                    +1));
                if (isMordentDown(scorNote))
                    note
                            .setPerformanceNote(makeMordent(scorNote, perfNote,
                                    -1));
            }
            lastScoreNote = scorNote;
        }
    }

    /**
     * @param perfNote
     * @return
     */
    private PerformanceNote makeTremolo(ScoreNote scoreNote,
            PerformanceNote perfNote, int type) {
        MidiNoteMulti[] multiNotes;
        Rational noteValue;
        switch (type) {
        case 1:
            noteValue = new Rational(1, 8);
            break;
        case 2:
            noteValue = new Rational(1, 16);
            break;
        case 3:
            noteValue = new Rational(1, 32);
            break;
        default:
            noteValue = new Rational(1, 1);
            break;
        }
        int count = (int) Math.round(scoreNote.getMetricDuration().div(
                noteValue).toDouble());
        multiNotes = new MidiNoteMulti[count];

        for (int i = 0; i < multiNotes.length; i++) {
            multiNotes[i] = new MidiNoteMulti(perfNote);
            multiNotes[i].setDuration(pieceTimeLine.getTime(noteValue));
            multiNotes[i].setTime(perfNote.getTime()
                    + pieceTimeLine.getTime(noteValue.mul(i)));
            multiNotes[i].setVelocity(perfNote.getVelocity() - 10);
            if (i > 0)
                multiNotes[i - 1].setNext(multiNotes[i]);
        }

        return multiNotes[0];
    }

    /**
     * @param scorNote
     * @param perfNote
     * @return
     */
    private PerformanceNote makeDetune(ScoreNote scorNote,
            PerformanceNote perfNote) {
        final double precision = 0.006103515625; // Precision of Microtuning in
        // cents

        long detune = Math.round(scorNote.getDetune() / precision);

        // Frequency data shall be defined in units which are fractions of a
        // semitone.
        // The frequency range starts at MIDI note 0, C = 8.1758 Hz, and extends
        // above
        // MIDI note 127, G = 12543.875 Hz. The first byte of the frequency data
        // word
        // specifies the nearest equal-tempered semitone below the frequency.
        // The next
        // two bytes (14 bits) specify the fraction of 100 cents above the
        // semitone at
        // which the frequency lies. Effective resolution = 100 cents / 2^14 =
        // .0061 cents.
        // http://www.midi.org/about-midi/tuning.shtml

        byte freqByte1 = (byte) (detune / 128);
        byte freqByte2 = (byte) (detune % 128);

        MidiNoteSysEx perfSysEx = new MidiNoteSysEx(perfNote);
        byte[] message = new byte[12];

        message[0] = (byte) 0xF0; // Universal RealTimeSysExHeader
        message[1] = (byte) 0x7F; // ID of target device (7F=all devices)
        message[2] = (byte) 0x08; // 08 sub-ID#1 = "MIDI tuning standard"
        message[3] = (byte) 0x07; // 07 sub-ID#2 = "SINGLE NOTE TUNING CHANGE
        // (REAL-TIME)(BANK)"
        message[4] = (byte) 0x00; // bb bank: 0-127 (described as 1-128 in MIDI
        // Tuning Specification)
        message[5] = (byte) 0x00; // tuning preset number: 0-127
        message[6] = (byte) 0x01; // number of changes (1 change = 1 set of [kk
        // xx yy zz])
        message[7] = perfNote.getPitch(); // MIDI key number
        message[8] = perfNote.getPitch(); // [xx yy zz] frequency data
        // for that key (repeated 'll'
        // number of times)
        //        message[9] = freqByte1;
        //        message[10] = freqByte2;
        message[9] = (byte) 0x7F;
        message[10] = (byte) 0x7E;
        //      message[10] = freqByte2;
        message[11] = (byte) 0xF7; // F7 EOX

        try {
            perfSysEx.getMessage().setMessage(message, 12);
        } catch (InvalidMidiDataException e) {

            e.printStackTrace();
        }

        return perfSysEx;
    }

    /**
     * @param scorNote
     * @param perfNote
     */
    private PerformanceNote makeMidiRendering(ScoreNote scorNote,
            PerformanceNote perfNote) {
        List start = scorNote.getMidiRendering().getStart();
        List repeat = scorNote.getMidiRendering().getRepeat();
        List end = scorNote.getMidiRendering().getEnd();

        MidiNoteMulti[] startNotes = new MidiNoteMulti[start.size()];
        MidiNoteMulti[] endNotes = new MidiNoteMulti[end.size()];

        initMidiNoteMulti(perfNote, startNotes);
        initMidiNoteMulti(perfNote, endNotes);

        int indS = 0;
        int indR = 0;
        int indE = 0;

        long startDur = 0; // duration of MidiRendering start
        long endDur = 0; // duration of MidiRendering end
        long repeatDur = 0; // duration of MidiRendering repeat
        long repeatNotesDur = 0; // duration of notes in repeat
        int repeatCount = 0; // number of repeats
        // repeatCount * repeatNotesDur = repeatDur
        long startTime = perfNote.getTime();

        // render Start ************************

        for (Iterator iter = start.iterator(); iter.hasNext();) {
            Object object = iter.next();
            if (object instanceof MidiNoteRendering) {
                MidiNoteRendering miNoRen = (MidiNoteRendering) object;
                if (miNoRen.isRest()) {
                    startTime += calcRestOffset(perfNote, miNoRen);
                } else {
                    //                    startTime = calcStartTime(perfNote, startNotes, indS);
                    //                    startTime = calcRestOffset(perfNote, miNoRen);
                    calcNote(perfNote, startNotes, indS, startTime, miNoRen);
                    startTime = calcStartTime(startNotes, indS);
                    indS++;
                }

            }

        }

        startDur = startNotes[indS - 1].getTime()
                + startNotes[indS - 1].getDuration() - perfNote.getTime();

        // calculate endDuration ************************
        for (Iterator iter = end.iterator(); iter.hasNext();) {
            Object object = iter.next();
            if (object instanceof MidiNoteRendering) {
                MidiNoteRendering miNoRen = (MidiNoteRendering) object;
                endDur += calcRestOffset(perfNote, miNoRen);
            }
        }
        // calculate repeatNotes Duration ************************
        for (Iterator iter = repeat.iterator(); iter.hasNext();) {
            Object object = iter.next();
            if (object instanceof MidiNoteRendering) {
                MidiNoteRendering miNoRen = (MidiNoteRendering) object;
                repeatNotesDur += calcRestOffset(perfNote, miNoRen);
            }
        }

        // time for repeat
        repeatDur = perfNote.getDuration() - (startDur + endDur);
        // repeatCount for repeat
        if (repeatNotesDur == 0)
            repeatCount = 0;
        else
            repeatCount = Math.round(repeatDur / repeatNotesDur);
        // relativ factor to add for all notes in repeat
        double relNoteAdd = 1.0;
        if (repeatCount * repeatNotesDur != 0)
            relNoteAdd = (double) repeatDur / (repeatCount * repeatNotesDur);

        MidiNoteMulti[] repeatNotes = new MidiNoteMulti[repeat.size()
                * repeatCount];
        initMidiNoteMulti(perfNote, repeatNotes);

        // render Repeat

        for (int count = 0; count < repeatCount; count++) {
            for (Iterator iter = repeat.iterator(); iter.hasNext();) {
                Object object = iter.next();
                if (object instanceof MidiNoteRendering) {
                    MidiNoteRendering miNoRen = (MidiNoteRendering) object;
                    if (miNoRen.isRest()) {
                        startTime += calcRestOffset(perfNote, miNoRen);
                    } else {

                        calcNote(perfNote, repeatNotes, indR, startTime,
                                miNoRen);
                        repeatNotes[indR].setDuration(Math
                                .round(repeatNotes[indR].getDuration()
                                        * relNoteAdd));
                        startTime = calcStartTime(repeatNotes, indR);

                        indR++;
                    }
                }

            }
        }

        // render End

        for (Iterator iter = end.iterator(); iter.hasNext();) {
            Object object = iter.next();
            if (object instanceof MidiNoteRendering) {
                MidiNoteRendering miNoRen = (MidiNoteRendering) object;
                if (miNoRen.isRest()) {
                    startTime += calcRestOffset(perfNote, miNoRen);
                } else {
                    // last note fills perfnote if no repeat is set
                    if (repeat.size() == 0 && indE + 1 == end.size()) {
                        calcNote(perfNote, endNotes, indE, startTime, miNoRen);
                        endNotes[indE].setDuration(perfNote.getDuration()
                                - (startTime - perfNote.getTime()));
                    } else {

                        calcNote(perfNote, endNotes, indE, startTime, miNoRen);
                        startTime = calcStartTime(endNotes, indE);
                    }
                    //                    startTime = calcStartTime(perfNote, endNotes, indE);
                    indE++;
                }
            }
        }

        // align in a line

        for (int i = 0; i < indS; i++) {

            if (i > 0)
                startNotes[i - 1].setNext(startNotes[i]);
        }

        for (int i = 0; i < indR; i++) {
            if (indS > 0) // startNotes exist
                startNotes[indS - 1].setNext(repeatNotes[0]);
            if (i > 0) {
                repeatNotes[i - 1].setNext(repeatNotes[i]);
            }
        }
        for (int i = 0; i < indE; i++) {
            if (indR > 0) // repeatNotes exist
                repeatNotes[indR - 1].setNext(endNotes[0]);
            else if (indS > 0) // no repeat but startnotes exist
                startNotes[indS - 1].setNext(endNotes[0]);
            if (i > 0) {
                endNotes[i - 1].setNext(endNotes[i]);
            }
        }

        if (indS > 0)
            return startNotes[0];
        else if (indR > 0)
            return repeatNotes[0];
        else if (indS > 0)
            return endNotes[0];
        else
            return perfNote;

    }

    /**
     * @param repeatNotes
     * @param indR
     * @return
     */
    private long calcStartTime(MidiNoteMulti[] repeatNotes, int indR) {
        long startTime;
        startTime = repeatNotes[indR].getTime()
                + repeatNotes[indR].getDuration();
        return startTime;
    }

    /**
     * @param perfNote
     * @param midiNoteMulti
     * @param index
     * @param startTime
     * @param miNoRen
     */
    private void calcNote(PerformanceNote perfNote,
            MidiNoteMulti[] midiNoteMulti, int index, long startTime,
            MidiNoteRendering miNoRen) {
        midiNoteMulti[index].setPitch(perfNote.getPitch()
                + miNoRen.getPitchAdd());
        midiNoteMulti[index].setVelocity(perfNote.getVelocity()
                + miNoRen.getVelocityAdd());

        midiNoteMulti[index].setTime(startTime
                + Math.round(perfNote.getDuration()
                        * miNoRen.getMetricTimeAddRel()));

        midiNoteMulti[index].setTime(midiNoteMulti[index].getTime()
                + miNoRen.getPhysicalTimeAddAbs());

        midiNoteMulti[index].setDuration(Math.round(perfNote.getDuration()
                * miNoRen.getDurationMult()));
    }

    /**
     * @param perfNote
     * @param miNoRen
     * @param timeOffset
     * @return
     */
    private long calcRestOffset(PerformanceNote perfNote,
            MidiNoteRendering miNoRen) {

        return miNoRen.getPhysicalTimeAddAbs()
                + Math.round(perfNote.getDuration()
                        * miNoRen.getMetricTimeAddRel()
                        + perfNote.getDuration() * miNoRen.getDurationMult());
    }

    /**
     * used to init midiNoteMultiArray This method initialize every
     * MidiNoteMulti with perfNotes values
     * 
     * @param perfNote
     * @param midiNoteMulti
     */
    private void initMidiNoteMulti(PerformanceNote perfNote,
            MidiNoteMulti[] midiNoteMulti) {
        for (int i = 0; i < midiNoteMulti.length; i++) {
            midiNoteMulti[i] = new MidiNoteMulti(perfNote);
        }
    }

    /**
     * @param scorNote
     */
    private MidiNote makeTriller(ScoreNote scorNote, PerformanceNote perfNote) {

        int count = (int) Math.round((double)pieceTimeLine.getTime(scorNote
                .getMetricDuration())
                / trillTime);
        int pitch = perfNote.getPitch();
        int upperNoteDistance = 1;

        MidiNoteMulti firstNote = new MidiNoteMulti(perfNote);
        MidiNoteMulti trillNote = new MidiNoteMulti();
        MidiNoteMulti nextNote;
        firstNote.setNext(trillNote);
        for (long i = 1; i < count; i++) {

            nextNote = new MidiNoteMulti();
            if (i % 2 == 0) {
                pitch = perfNote.getPitch() + upperNoteDistance;
            } else {
                pitch = perfNote.getPitch();
            }

            nextNote.setPitch(pitch);
            nextNote.setTime(perfNote.getTime() + i * trillTime);
            nextNote.setDuration(trillTime);
            nextNote.setVelocity(perfNote.getVelocity() - 20);

            trillNote.setNext(nextNote);
            trillNote = nextNote;
        }

        return firstNote;
    }

    private PerformanceNote makeMordent(ScoreNote scorNote,
            PerformanceNote perfNote, int pitch) {

        MidiNoteMulti firstNote = new MidiNoteMulti(perfNote);
        MidiNoteMulti nextNote = new MidiNoteMulti(perfNote);
        MidiNoteMulti lastNote = new MidiNoteMulti(perfNote);
        firstNote.setDuration(trillTime);

        nextNote.setTime(perfNote.getTime() + trillTime);
        nextNote.setDuration(trillTime);
        nextNote.setPitch(perfNote.getPitch() + pitch);
        nextNote.setVelocity(perfNote.getVelocity() - 15);

        lastNote.setTime(perfNote.getTime() + 2L * trillTime);
        lastNote.setDuration(perfNote.getDuration() - 2L * trillTime);

        firstNote.setNext(nextNote);
        nextNote.setNext(lastNote);
        return firstNote;
    }

    /**
     * @param scorNote
     */
    private boolean isTrill(ScoreNote scorNote) {
        byte[] accents = scorNote.getAccents();
        if (accents != null) {
            for (int i = 0; i < accents.length; i++) {
                if (accents[i] == Accent.TRILL)
                    return true;
            }
        }
        return false;
    }

    private boolean isMordentDown(ScoreNote scorNote) {
        byte[] accents = scorNote.getAccents();
        if (accents != null) {
            for (int i = 0; i < accents.length; i++) {
                if (accents[i] == Accent.MORDENT_DOWN)
                    return true;

            }
        }
        return false;
    }

    private boolean isMordentUp(ScoreNote scorNote) {
        byte[] accents = scorNote.getAccents();
        if (accents != null) {
            for (int i = 0; i < accents.length; i++) {

                if (accents[i] == Accent.MORDENT_UP)
                    return true;
            }
        }
        return false;
    }

    public int getVelocity(Note note, int voice) {
        int velocity = getVelocity(note.getScoreNote().getMetricTime(), voice);
        byte[] accents = note.getScoreNote().getAccents();

        if (accents != null) {
            for (int i = 0; i < accents.length; i++) {
                if (accents[i] == Accent.MARCATO)
                    velocity += marcarto;
            }
        }
        if (veloUpCont != null) {
            if (!veloUpCont.contains(note)) {
                velocity -= veloDown;
            } else
                velocity += veloUp;
        }

        if (velocity > 127) {
            velocity = 127;
        }
        if (velocity < 1) {
            velocity = 1;
        }
        return velocity;
    }

    private int getVelocity(Rational time, int voice) {
        int velo = defaultVelo;

        Marker dyn = findPreviousDyn(time, voiceDyn[voice], true);
        if (dyn instanceof DynamicsLevelMarker) {
            DynamicsLevelMarker dynMark = (DynamicsLevelMarker) dyn;
            velo = dynMark.getMidiLevel();
        }
        if (dyn instanceof DynamicsMarker) {
            DynamicsMarker dynLev = (DynamicsMarker) dyn;
            boolean found = false;
            int velDif = 0; // velocitys changed by crescendo
            Marker prevMark;
            // find previuos
            prevMark = findPreviousDyn(dynLev.getMetricTime(), voiceDyn[voice],
                    false);
            do {
                if (prevMark != null) {
                    if (prevMark instanceof DynamicsLevelMarker)
                        break;
                    if (prevMark instanceof DynamicsMarker) {
                        DynamicsMarker mark = (DynamicsMarker) prevMark;

                        if (mark.isCrescendo())
                            velDif += crescendoDiv;
                        else
                            velDif -= crescendoDiv;
                    }
                    prevMark = findPreviousDyn(prevMark.getMetricTime(),
                            voiceDyn[voice], false);

                } else
                    break;

            } while (!found);
            int prevVel = defaultVelo;
            if (prevMark != null) {

                DynamicsLevelMarker dynMar = (DynamicsLevelMarker) prevMark;

                prevVel = dynMar.getMidiLevel() + velDif; // velocity before
                // DynamicMarker
            }

            if (dynLev.getMetricTime().add(dynLev.getMetricDuration()).compare(
                    time) == -1

                    || dynLev.getMetricTime().add(dynLev.getMetricDuration())
                            .compare(time) == 0) {
                // Crescendo is over
                if (dynLev.isCrescendo())
                    velo = prevVel + crescendoDiv;
                else
                    velo = prevVel - crescendoDiv;

            } else if (dynLev.getMetricTime().compare(time) == -1
                    || dynLev.getMetricTime().compare(time) == 0) {
                // within Crescendo
                double cresPos = 0; // where in Crescendo are we
                cresPos = time.sub(dynLev.getMetricTime()).div(
                        dynLev.getMetricDuration()).toDouble();
                assert cresPos >= 0.0 && cresPos <= 1;
                if (!dynLev.isCrescendo())
                    velo = prevVel + (int) Math.round(cresPos * -crescendoDiv);
                else
                    velo = prevVel + (int) Math.round(cresPos * crescendoDiv);
            }

        }
        if (velo > 127)
            velo = 127;
        if (velo < 0)
            velo = 0;
        return velo;
    }

    public static Marker findPreviousDyn(Rational time,
            SortedCollection voiceDyn, boolean same) {
        Marker lastMarker = null;

        if (same) {
            for (Iterator iter = voiceDyn.iterator(); iter.hasNext();) {
                Marker element = (Marker) iter.next();

                if (element.getMetricTime().compare(time) == 1) {
                    // we went to far last, lastMarker is what we want
                    if (lastMarker != null) {
                        if (lastMarker.getMetricTime().compare(time) <= 0)
                            return lastMarker;
                    } else
                        // there is no marker with previous time or same time
                        return null;
                }
                if (element.getMetricTime().compare(time) == 0)
                    return element;
                lastMarker = element;
            }
            if (lastMarker != null) {
                return lastMarker;
            }
            return null;
        } else {
            for (Iterator iter = voiceDyn.iterator(); iter.hasNext();) {
                Marker element = (Marker) iter.next();

                if (element.getMetricTime().compare(time) >= 0) {
                    // we went to far last, lastMarker is what we want
                    if (lastMarker != null) {
                        if (lastMarker.getMetricTime().compare(time) < 0)
                            return lastMarker;
                    } else
                        // there is no marker with previous time
                        return null;
                }

                lastMarker = element;
            }
            // do we need this ???
            if (lastMarker != null
                    && lastMarker.getMetricTime().compare(time) < 0) {
                return lastMarker;
            }
            return null;
        }

    }

    public long getTime(ScoreNote thisNote, ScoreNote lastNote) {
        long time = 0;
        time = pieceTimeLine.getTime(lastNote.getMetricDuration().mul(8, 9)
                .add(lastNote.getMetricTime()));
        System.out.println(lastNote.getMetricTime()
                + " set To "
                + lastNote.getMetricDuration().mul(8, 9).add(
                        lastNote.getMetricTime()));
        return time;
    }

    public long getTime(ScoreNote scorNote) {
        long time = 0;
        time = pieceTimeLine.getTime(scorNote.getMetricTime());
        if (scorNote.getAudioDelay() != null){
            time += pieceTimeLine.getTime(scorNote.getAudioDelay());
        }
        
        return time;
    }

    public long getDuration(ScoreNote scorNote, boolean align) {
        long dur = 0;
        if (align)
            dur = pieceTimeLine.getTime(scorNote.getMetricDuration().mul(3, 4));
        else
            dur = pieceTimeLine.getTime(scorNote.getMetricDuration());
        byte[] accents = scorNote.getAccents();
        if (accents != null) {
            for (int i = 0; i < accents.length; i++) {
                if (accents[i] == Accent.STACCATO) {
                    dur = Math.round(dur * staccato);
                }
                if (accents[i] == Accent.PORTATO) {
                    dur = Math.round(dur * portato);
                }
            }
        }
        if (dur < 0) {
            dur = 0;
        }
        return dur;
    }

    public static int getPitch(ScoreNote scoreNote) {

        return scoreNote.getMidiPitch();
    }

    /**
     * @param notSystem
     *            The notSystem to set.
     */
    public void setNotationSystem(NotationSystem notSystem) {
        if (notSystem == null)
            throw new IllegalArgumentException(
                    "Notation System should not be null");
        this.notSystem = notSystem;
    }

    /**
     * @return Returns the marcarto.
     */
    public int getMarcarto() {
        return marcarto;
    }

    /**
     * @param marcarto
     *            The marcarto to set.
     */
    public void setMarcarto(int marcarto) {
        this.marcarto = marcarto;
    }

    /**
     * @return Returns the pieceTimeLine.
     */
    public MetricalTimeLine getPieceTimeLine() {
        return pieceTimeLine;
    }

    /**
     * @param pieceTimeLine
     *            The pieceTimeLine to set.
     */
    public void setPieceTimeLine(MetricalTimeLine pieceTimeLine) {
        this.pieceTimeLine = pieceTimeLine;
    }

    /**
     * @return Returns the portato.
     */
    public double getPortato() {
        return portato;
    }

    /**
     * @param portato
     *            The portato to set.
     */
    public void setPortato(double portato) {
        this.portato = portato;
    }

    /**
     * @return Returns the staccato.
     */
    public double getStaccato() {
        return staccato;
    }

    /**
     * @param staccato
     *            The staccato to set.
     */
    public void setStaccato(double staccato) {
        this.staccato = staccato;
    }

    /**
     * @return Returns the enableOverwritePerfomanceNote.
     */
    public boolean isEnableOverwritePerfomanceNote() {
        return enableOverwritePerfomanceNote;
    }

    /**
     * @param enableOverwritePerfomanceNote
     *            The enableOverwritePerfomanceNote to set.
     */
    public void setEnableOverwritePerfomanceNote(
            boolean enableOverwritePerfomanceNote) {
        this.enableOverwritePerfomanceNote = enableOverwritePerfomanceNote;
    }
}