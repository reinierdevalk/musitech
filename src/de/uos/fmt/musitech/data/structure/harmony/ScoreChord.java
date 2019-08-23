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
 * Created on 04.09.2006
 *
 */
package de.uos.fmt.musitech.data.structure.harmony;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Kerstin Neubarth
 *
 */
public class ScoreChord extends BasicContainer<Note> implements Metrical {
	
	private Rational metricTime;
	private Rational metricDuration;
	
	public ScoreChord(){
		super(null, Note.class);
	}
	
	public ScoreChord(Context context){
		super(context, Note.class);
	}

	/** 
	 * @see de.uos.fmt.musitech.data.time.Metrical#getMetricTime()
	 */
	public Rational getMetricTime() {
		return metricTime;
	}

	/** 
	 * @see de.uos.fmt.musitech.data.time.Metrical#getMetricDuration()
	 */
	public Rational getMetricDuration() {
		return metricDuration;
	}
	
	public void setMetricTime(Rational metricTime){
		this.metricTime = metricTime;
		Containable[] contents = getContentsRecursive();
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] instanceof Note){
				ScoreNote scoreNote = ((Note)contents[i]).getScoreNote();
				if (scoreNote!=null){
					scoreNote.setMetricTime(metricTime);
				}
			}
		}
	}
	
	public void setMetricDuration(Rational metricDuration){
		this.metricDuration = metricDuration;
		Containable[] contents = getContentsRecursive();
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] instanceof Note){
				ScoreNote scoreNote = ((Note)contents[i]).getScoreNote();
				if (scoreNote!=null){
					scoreNote.setMetricDuration(metricDuration);
				}
			}
		}
	}
	
	public void addNote(Note note){
		if (metricTime==null){
			metricTime = note.getScoreNote().getMetricEndTime();
		}
		if (metricDuration==null){
			metricDuration = note.getScoreNote().getMetricDuration();
		}
		if (!note.getScoreNote().getMetricTime().equals(metricTime)){
			note.getScoreNote().setMetricTime(metricTime);
			System.out.println("ScoreChord: metric time of added note has been changed for chord consistency.");
		}
		if (!note.getScoreNote().getMetricDuration().equals(metricDuration)){
			note.getScoreNote().setMetricDuration(metricDuration);
			System.out.println("ScoreChord: metric duration of added note has been changed for chord consistency.");
		}
		add(note);
	}
	
	public void removeNote(Note note){
		super.remove(note);
	}
	
	public Note getLowestNote(){
		Note[] notes = new Note[size()];
		int i=0;
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Note element = (Note) iter.next();
			notes[i++] = element;
		}
		Note note = notes[0];
		for (int j = 1; j < notes.length; j++) {
			if (notes[j].isLower(note)){
				note = notes[j];
			}
		}
		return note;
		
	}
	
	//TODO also add: public ScorePitch getFundamental()
	
	public boolean isEqualScoreChord(ScoreChord chord){
		Collection thisPitches = getScorePitches(this);
		Collection chordPitches = getScorePitches(chord);
		int hits = 0;
		if (chordPitches.size() == thisPitches.size()){
			for (Iterator iter = chordPitches.iterator(); iter.hasNext();) {
				ScorePitch chordElement = (ScorePitch) iter.next();
//				if (thisPitches.contains(element)){
//					hits++;
//				}
				for (Iterator iterator = thisPitches.iterator(); iterator
						.hasNext();) {
					ScorePitch thisElement = (ScorePitch) iterator.next();
					if (thisElement.getDiatonic()==chordElement.getDiatonic()
							&& thisElement.getAlteration()==chordElement.getAlteration()
							&& thisElement.getOctave()==chordElement.getOctave()){
						hits++;
					}
				}
			}
		}
		return (thisPitches.size()==hits);
	}
	
	private Collection getScorePitches(ScoreChord chord){
		Collection scorePitches = new ArrayList();
		Containable[] containables = chord.getContentsRecursive();
		for (int i = 0; i < containables.length; i++) {
			if (containables[i] instanceof Note){
				ScorePitch scorePitch = ((Note)containables[i]).getScoreNote().getPitch();
				if (!scorePitches.contains(scorePitch)){
					scorePitches.add(scorePitch);
				}
			}
		}
		return scorePitches;
	}
}
