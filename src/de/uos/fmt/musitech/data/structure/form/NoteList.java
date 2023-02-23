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
package de.uos.fmt.musitech.data.structure.form;

import java.io.IOException;
import java.util.Iterator;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.MidiNoteSequence;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.rendering.RenderingSupported;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.utility.IEquivalence;
import de.uos.fmt.musitech.performance.midi.NegativeTimeStampException;
import de.uos.fmt.musitech.score.epec.EpecParser;
import de.uos.fmt.musitech.score.epec.EpecParser.yyException;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * A convenience class to create a score and generated 
 * performance data.    
 * 
 * @author Jens Wissmann
 * 
 * @hibernate.class table="NoteList" * 
 * @hibernate.joined-subclass 
 * @hibernate.joined-subclass-key column = "uid"
 *  
 */
public class NoteList extends SortedContainer<Note> implements Cloneable, RenderingSupported, IEquivalence {

//    /**
//     * The <code>RenderingHints</code> contains information about how to show
//     * certain features of this NoteList. Might be null.
//     */
//    RenderingHints renderingHints;

    /**
     * TODO add comment
     * 
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return size() + " note(s) in " + super.toString();
    }

    public String contentDescription() {
        String content = "";
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Note note = (Note) iter.next();
            content+=note.toString()+"\n";
        }
        return content;
    }

    /**
     * only Note objects may be put into this container.
     */
    public static final Class types = Note.class;

    public NoteList() {
        super(null, Note.class, new MetricalComparator());
    }

    public NoteList(Context context) {
        super(context, Note.class, new MetricalComparator());
    }

    /**
     * Creates a NoteList from 'Plaine and Easie' Code.
     * 
     * @param pec A string with 'plaine and easie' code. 
     */
    public NoteList(String pec) {
        this(Context.getDefaultContext(), pec);
    }
    
    public int removeRests(){
        int count=0;
        for (int i=0; i < size() ; i++) {
            Note note = get(i);
            if(note.getPerformanceNote() == null){
                remove(note);
                count++;
                i--;
            }
        }
        return count;
    }

    /**
     * Creates a NoteList from 'Plaine and Easie' Code.
     * 
     * @param context
     * @param pec
     */
 
    public NoteList(Context context, String pec) {
        super(context, types, new MetricalComparator());

        EpecParser parser = new EpecParser();
        NotationSystem system = new NotationSystem(context);
        try {
            parser.run(system, pec);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (yyException e) {
            e.printStackTrace();
        }
        Containable[] contents = system.getContentsRecursive();

        MetricalTimeLine mt = context.getPiece().getMetricalTimeLine();

        for (int i = 0; i < contents.length; i++) {
            if (contents[i] instanceof Note) {
                Note note = (Note) contents[i];
                if (note.getScoreNote().getPitch().getDiatonic() == 'r'){
                    note.setPerformanceNote(null);
                } else {
                    note.setPerformanceNote(note.getScoreNote().toPerformanceNote(mt));
                }
                add(note);
            }
        }

    }

    public Rational getMetricDuration() {
        Rational onset;

        if (size() > 0) {
            Note previous = get(size() - 1);
            onset = previous.getScoreNote().getMetricTime().add(previous.getScoreNote().getMetricDuration());
        } else {
            onset = new Rational(0);
        }

        return onset;
    }
    
    /**
     * Method adds a Note generated form a score information to the end of the
     * NoteList.
     * 
     * @param note
     */
    public void addnext(ScorePitch pitch, Rational duration) {
    	Rational onset = getMetricDuration();

        MetricalTimeLine mt = this.getContext().getPiece().getMetricalTimeLine();

        ScoreNote score = new ScoreNote(pitch, onset, duration);

        PerformanceNote performance = score.toPerformanceNote(mt);

        Note note = new Note(score, performance);

        add(note);
    }
    
    /** This method adds a Note using the same onset as the last added note.
     * @param pitch
     * @param duration
     */
    public void addAnother(ScorePitch pitch, Rational duration) {
    	Rational onset;
    	if (size() > 0) {
    		onset = get(size() - 1).getMetricTime();
    	}
    	else {
    		onset = Rational.ZERO;
    	}
    	MetricalTimeLine mt = this.getContext().getPiece().getMetricalTimeLine();

        ScoreNote score = new ScoreNote(pitch, onset, duration);

        PerformanceNote performance = score.toPerformanceNote(mt);

        Note note = new Note(score, performance);

        add(note);
    	
    }
    
    public void addnext(ScorePitch pitch, Rational duration, byte accent) {
    	addnext(pitch, duration);
    	get(size() - 1).getScoreNote().addAccent(accent);
    }

    public void add(ScorePitch pitch, Rational onset, Rational duration, byte accent) {
    	ScoreNote sn = new ScoreNote(pitch, onset, duration);
    	add(sn);
    	sn.addAccent(accent);
    }
    
    public Note add(ScoreNote score) {
        MetricalTimeLine mt = this.getContext().getPiece().getMetricalTimeLine();
        PerformanceNote performance = score.toPerformanceNote(mt);
        Note note = new Note(score, performance);
        add(note);
        return note;
    }

    public void add(ScorePitch pitch, Rational onset, Rational duration) {
        add(new ScoreNote(pitch, onset, duration));
    }

    /**
     * Method adds <code>times</code> repetitions of the current contents of
     * this NoteList the end of this NoteList.
     * 
     * @param times Repetitions to add.
     */
    public void multiply(int times) {
        int size = size();
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < size; j++) {
                ScoreNote scorenote = ObjectCopy.copyObject(get(j).getScoreNote());
                this.addnext(scorenote.getPitch(), scorenote.getMetricDuration());
            }
        }
    }

    /**
     * Generate a NoteList from a given MidiNoteSequence by generating the Notes
     * and contained ScoreNotes.
     * 
     * @param mns the MIDINoteSequence
     * @return the generated NoteList.
     */
    public int convert(MidiNoteSequence mns) {
        MetricalTimeLine timeline;

        if (getContext() != null) {
            timeline = getContext().getPiece().getMetricalTimeLine();
        } else {
            timeline = new MetricalTimeLine();
        }

        for (Iterator iter = mns.iterator(); iter.hasNext();) {

            MidiNote element = (MidiNote) iter.next();
            PerformanceNote perfNote = new PerformanceNote(element.getTime());
            perfNote.setVelocity(element.getVelocity());
            perfNote.setPitch(element.getPitch());
            perfNote.setDuration(element.getDuration());

            ScoreNote scorNote = toScoreNote(perfNote, timeline);

            Note note = new Note(scorNote, perfNote);

            this.add(note);
        }
        return mns.size();
    }

    /**
     * Constructs a scoreNote from this performanceNote according to the given
     * MetricalTimeLine
     * 
     * @param perfNote the performanceNote to evaluate
     * @param timeline the timeline to use
     * @return the generated ScoreNote.
     */
    public ScoreNote toScoreNote(PerformanceNote perfNote, MetricalTimeLine timeline) {

        ScoreNote scorNote = new ScoreNote();

        Rational startTime = timeline.getMetricTime(perfNote.getTime());
        Rational endTime = timeline.getMetricTime(perfNote.getTime() + perfNote.getDuration());

        // crude quantizeation of metric times.
        startTime.changeDenom(16);
        if (endTime.isLess(1, 16))
            endTime = new Rational(1, 16);
        else
            endTime.changeDenom(16);

        scorNote.setMetricTime(startTime);
        scorNote.setMetricDuration(endTime.sub(startTime));

        char dia = 'c'; // diatonic note a-g
        byte acc = 0; // accidental
        byte oct; // octave
        switch (perfNote.getPitch() % 12) {
        case 0:
            dia = 'c';
            break;
        case 1:
            dia = 'c';
            acc = 1;
            break;
        case 2:
            dia = 'd';
            break;
        case 3:
            dia = 'e';
            acc = -1;
            break;
        case 4:
            dia = 'e';
            break;
        case 5:
            dia = 'f';
            break;
        case 6:
            dia = 'f';
            acc = +1;
            break;
        case 7:
            dia = 'g';
            break;
        case 8:
            dia = 'a';
            acc = -1;
            break;
        case 9:
            dia = 'a';
            break;
        case 10:
            dia = 'b';
            acc = -1;
            break;
        case 11:
            dia = 'b';
            break;
        default:
            break;
        }
        oct = (byte) ((perfNote.getPitch() / 12) - 5);

        scorNote.setOctave(oct);
        scorNote.setAlteration(acc);
        scorNote.setDiatonic(dia);

        return scorNote;

    }

    /**
     * Gets the note at position i.
     * 
     * @param i The index to get the note from.
     * @return The note.
     */
    public Note getNoteAt(int i) {
        return get(i);
    }

    /**
     * Shifts all PerformanceNotes in this list by dist microseconds.
     * 
     * @param dist The distance to shift the notes by.
     */
    public void timeShift(long dist) throws NegativeTimeStampException {
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Note note = (Note) iter.next();
            note.setTime(note.getTime() + dist);
        }
        if (size() > 0 && getNoteAt(0).getTime() < 0)
            throw new NegativeTimeStampException();
    }

    /**
     * Stretches all PerformanceNotes' times in this list by the given factor
     * with zero as fixed point (timeStrech(x)=x*factor).
     * 
     * @param factor The factor to multiply the times by.
     */
    public void timeStretch(double factor) throws NegativeTimeStampException {
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Note note = (Note) iter.next();
            note.setTime((long) (note.getTime() * factor));
            note.getPerformanceNote().setDuration((long) (note.getDuration() * factor));
            if (size() > 0 && getNoteAt(0).getTime() < 0)
                throw new NegativeTimeStampException();
        }
    }

    /**
     * Stretches all PerformanceNotes' times in this list by the given factor
     * with fixed point fix ( timeStrech(x) = (x-fix)*factor+x ).
     * 
     * @param factor The distance to shift the notes.
     * @param fix The fixed point.
     */
    public void timeStretch(double factor, long fix) throws NegativeTimeStampException {
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Note note = (Note) iter.next();
            if(note.getPerformanceNote()==null)
                continue;
            note.setTime((long) ((note.getTime() - fix) * factor + fix));
            note.getPerformanceNote().setDuration((long) (note.getDuration() * factor));
        }
        if (size() > 0 && getNoteAt(0).getTime() < 0)
            throw new NegativeTimeStampException();
    }

    /**
     * TODO add comment
     * 
     * @see de.uos.fmt.musitech.utility.collection.TypedCollection#clone()
     */
    @Override
	public Object clone() {
        NoteList klon = (NoteList) super.clone();
        for (int i = 0; i < size(); i++) {
            try {
                klon.list.set(i, (Note)klon.getNoteAt(i).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        return klon;
    }

    /**
     * Returns the <code>renderingHints</code> of this NoteList. <br>
     * <b>Standard hints for a NoteList are: <br>
     * "barline" -> "full" (barlines crossing systems), "half" (barlines between
     * systems), "none" (no barlines) <br>
     * "time signature" -> "standard" (signature with numerator and
     * denominator), "none" (no signature)
     * 
     * @return RenderingHints the <code>renderingHints</code> of this NoteList
     * 
     * @see de.uos.fmt.musitech.data.rendering.RenderingSupported#getRenderingHints()
     */
    @Override
	public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    /**
     * Sets the specified RenderingHints as the <code>renderingHints</code> of
     * this NoteList. <br>
     * <b>Standard hints for a NoteList are: <br>
     * "barline" -> "full" (barlines crossing systems), "half" (barlines between
     * systems), "none" (no barlines) <br>
     * "time signature" -> "standard" (signature with numerator and
     * denominator), "none" (no signature)
     * 
     * @param RenderingHints to be sets as the <code>renderingHints</code> of
     *            this NoteList
     * 
     * @see de.uos.fmt.musitech.data.rendering.RenderingSupported#setRenderingHints(de.uos.fmt.musitech.data.rendering.RenderingHints)
     */
    @Override
	public void setRenderingHints(RenderingHints renderingHints) {
        this.renderingHints = renderingHints;
    }

    public void metricalTimeShift(Rational shift) {
        for (Iterator iter = iterator(); iter.hasNext();) {
            ScoreNote note = ((Note) iter.next()).getScoreNote();
            note.setMetricTime(note.getMetricTime().add(shift));
        }
    }
    
    /**
     * Tests if a NoteList is equivalent to another NoteList in terms
     * of their Note content; no other attributes other than Note objects 
     * are taken into account.
     * @param IEquivalence o the NoteList to compare on equivalence
     * @return boolean specifying if the NoteLists are equivalent
     */
    @Override
	public boolean isEquivalent(IEquivalence o) {
    	if(!(o instanceof NoteList))
    		return false;
    	NoteList nl = (NoteList)o; 
    	if(nl.size() != this.size())
    		return false;
    	for(int i=0;i<this.size();++i) {
    		if(!this.getNoteAt(i).isEquivalent(nl.getNoteAt(i))) {
    		//if(this.getNoteAt(i)!=(nl.getNoteAt(i))) {
    			return false;
    		}
    	}
    	return true;
    }

	/**
	 * Method adds a ScoreNote the end of the NoteList.
	 * 
	 * @param scoreNote
	 */
	public void addnext(ScoreNote scoreNote) {
		ScoreNote newScore = ObjectCopy.copyObject(scoreNote);
		MetricalTimeLine mt = this.getContext().getPiece()
				.getMetricalTimeLine();

		scoreNote.setMetricTime(getMetricDuration());
		PerformanceNote performance = newScore.toPerformanceNote(mt);

		Note note = new Note(newScore, performance);

		add(note);
	}
}