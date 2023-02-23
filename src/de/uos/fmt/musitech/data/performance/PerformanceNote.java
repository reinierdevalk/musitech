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
package de.uos.fmt.musitech.data.performance;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.time.*;
import de.uos.fmt.musitech.data.utility.IEquivalence;
import de.uos.fmt.musitech.framework.editor.Editable;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.utility.DebugState;

/**
 * A performed note with pitch, "expressive" timing and dynamics.
 * 
 * @version 1.0
 * @author Tillman Weyde
 * @hibernate.class table="PerformanceNote"
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 */
public class PerformanceNote extends BasicTimedObject implements Editable, Cloneable, MObject, IEquivalence {

	long duration = 120000;
	short velocity = 90;
	byte pitch = 60;
	boolean generated = false;

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	public PerformanceNote(long time) {
		setTime(time);
	}

	public PerformanceNote(long time, long duration) {
		this(time);
		setDuration(duration);
	}

	public PerformanceNote(long time, long duration, int velocity) {
		this(time, duration);
		setVelocity(velocity);
	}

	public PerformanceNote(long time, long duration, int velocity, int pitch) {
		this(time, duration, velocity);
		setPitch(pitch);
	}

	public PerformanceNote() {
		super(0);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	// /**
	// * Transforms a ScoreNote into a PerformanceNote
	// * @param scoreNote de.uos.fmt.musitech.data.structure.ScoreNote
	// */
	// public static PerformanceNote convert(ScoreNote score)
	// {
	// PerformanceNote perf = new PerformanceNote();
	// perf.setPitch(ChordSymbol.translate(score.getDiatonic(),score.getAccidental(),score.getOctave()));
	// XXX
	// return perf;
	// }

	/**
	 * Returns the performance note as a string
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return "PerformanceNote " + getPitch() + " (pitch) from " + getTime() + " for "
				+ getDuration();
	}

	@Override
	public EditingProfile getEditingProfile() {
		return new EditingProfile("Performance Note",
									new EditingProfile[] {new EditingProfile("pitch"),
															new EditingProfile("velocity"),
															new EditingProfile("duration")},
									"Panel");
	}

	/**
	 * Returns the duration in milliseconds.
	 * 
	 * @return long
	 * @hibernate.property
	 */
	@Override
	public long getDuration() {
		return duration;
	}

	/**
	 * Sets the duration in milliseconds.
	 * 
	 * @param duration The duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Returns the velocity in MIDI coding.
	 * 
	 * @return int
	 * @hibernate.property
	 */
	public short getVelocity() {
		return velocity;
	}

	/**
	 * Sets the velocity.
	 * 
	 * @param argVelo int
	 */
	public void setVelocity(int argVelo) {
		if (argVelo > 127) {
			if (DebugState.DEBUG)
				System.out.println("WARNING! PerformanceNote.setVelocity() velocity " + argVelo
									+ " is out of range, capping to 127.");
			argVelo = 127;
		}
		this.velocity = (short) argVelo;
	}

	/**
	 * Sets the velocity.
	 * 
	 * @param argVelo int
	 */
	public void setVelocity(short argVelo) {
		if (argVelo > 127) {
			if (DebugState.DEBUG)
				System.out.println("WARNING! PerformanceNote.setVelocity() velocity " + argVelo
									+ " is out of range, capping to 127.");
			argVelo = 127;
		}
		this.velocity = argVelo;
	}

	/**
	 * The Method getNext returns the next PerformanceNote connected to this
	 * note. This is used for ScoreNotes connected to several Performance Notes
	 * in case of trills. The default is to return no next note (one SoreNote
	 * corresponds to one PerformanceNote normally).
	 * 
	 * @return PerformanceNote The next note if there is one, null else.
	 */
	public PerformanceNote getNext() {
		return null;
	}

	/**
	 * getPitch
	 * 
	 * @return
	 * @hibernate.property
	 */
	public byte getPitch() {
		return pitch;
	}

	/**
	 * setPitch
	 * 
	 * @param i
	 */
	public void setPitch(int i) {
		pitch = (byte) i;
	}

	/**
	 * setPitch
	 * 
	 * @param i
	 */
	public void setPitch(byte i) {
		pitch = i;
	}

	/**
	 * TODO add comment
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj ==  null || obj.getClass() != this.getClass())
			return false;
		PerformanceNote pn = (PerformanceNote) obj;
		if (getTime() != pn.getTime())
			return false;
		if (duration != pn.duration)
			return false;
		if (pitch != pn.pitch)
			return false;
		if (velocity != pn.velocity)
			return false;
		return true;
		// return ObjectCopy.comparePublicProperties2(this, obj);
	}
	
	

	/**
	 * TODO add comment
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = (int) getTime() ^ (int) (getTime() << 32);
		hash ^= duration;
		hash ^= duration << 32;
		hash ^= pitch << 24;
		hash ^= velocity << 8;
		return hash;
	}

	@Override
	public boolean isEquivalent(IEquivalence o) {
		if( !(o instanceof PerformanceNote) ) return false;
		return equals( o);
	}
}