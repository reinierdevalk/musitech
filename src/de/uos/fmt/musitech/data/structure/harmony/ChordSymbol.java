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
package de.uos.fmt.musitech.data.structure.harmony;

import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * This class represents a chord symbol, i.e. information on a chord 
 * as used in song books or lead-sheets. It contains information the 
 * root note, its alteration, and the mode as in {@link KeyMarker}, the 
 * base (lowest) note, and the top note.
 *   
 * @version $Revision: 8555 $, $Date: 2013-09-21 14:09:47 +0200 (Sat, 21 Sep 2013) $
 * @author Christa Deiwiks, Klaus Dalinghaus
 * 
 * @hibernate.class table="ChordSymbol"
 *
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key  column = "uid"
 */
public class ChordSymbol extends KeyMarker {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor creates a ChordSymbol at time 0 with the given mode 
	 * and the diatonic and alteration of the rootPitch argument.
	 *
	 * @param mode The mode of this chord. 
	 * @param rootPitch This defines the diatonic and alteration of this chord, octave is ignored.
	 */
	public ChordSymbol(Mode mode, ScorePitch rootPitch) {
		super(Rational.ZERO, 0);
		setMode(mode);
		setRoot(rootPitch.getDiatonic());
		setRootAlteration(rootPitch.getAlteration());
	}

	/**
	 * Constructor for ChordSymbol
	 * @param metricTime The metrical time for the chord.
	 * @param timeStamp The physical time for the chord.
	 */
	public ChordSymbol(Mode mode, char diatonic, int alteration) {
		super(Rational.ZERO, 0);
		setMode(mode);
		setRoot(diatonic);
	}

	/**
	 * Constructor for ChordSymbol
	 * @param metricTime The metrical time for the chord.
	 * @param timeStamp The physical time for the chord.
	 */
	public ChordSymbol(Rational metricTime, long timeStamp) {
		super(metricTime, timeStamp);
	}
	
	/**
	 * Create a new chord symbol at a given metrical time, using 
	 * a piece (that must not be null) to determine the physical time. 
	 * 
	 * @param metricTime
	 * @param piece the piece defining the metrical timeline, must not be null.
	 */
	public ChordSymbol(Rational metricTime, Piece piece) {
		super(metricTime,0L);
		if(piece == null)
			throw new IllegalArgumentException("Error in "+this+". Piece must not be null!");
		this.setTime(piece.getMetricalTimeLine().getTime(metricTime));
	}
	
	/**
	 * Creates a new chord symbol with given metric time, root and root accidental. 
	 * 
	 * @param metricTime The metric time for this chord. 
	 * @param root The root for this chord. 
	 * @param rootAlteration The alteration of the root (natural = 0, sharp = +1 ..., flat = -1 ...)
	 */
	public ChordSymbol(Rational metricTime, char root, int rootAlteration){
	    this(metricTime, Timed.INVALID_TIME);
	    setRoot(root);
	    setRootAlteration(rootAlteration);
	}
	
	/**
	 *  base (lowest) note, relative in degrees to the root.
	 */
	int base = 0;
	
	/**
	 *  top (highest) note, relative in degrees to the root.
	 */
	int top = 0;
	
	/**
	 *  contains a string consisting of b,#, and number 
	 *  representing a chord note in degrees from the root.
	 */
	String extensions = "";

	/**
	 * Returns the base.
	 * @return int
	 * 
	 * @hibernate.property
	 */
	public int getBase() {
		return base;
	}

	/**
	 * Returns the extensions.
	 * @return String
	 * 
	 * @hibernate.property
	 */
	public String getExtensions() {
		return extensions;
	}

	/**
	 * Returns the top.
	 * @return int
	 * 
	 * @hibernate.property
	 */
	public int getTop() {
		return top;
	}

	/**
	 * Sets the base, i.e. the lowest note of the chord.
	 * @param argBase The base to set. 
	 */
	public void setBase(int argBase) {
		this.base = argBase;
	}

	/**
	 * Sets the extensions.
	 * @param argExtensions The extensions to set
	 */
	public void setExtensions(String argExtensions) {
		this.extensions = argExtensions;
	}

	/**
	 * Sets the top.
	 * @param argTop The top to set
	 */
	public void setTop(int argTop) {
		this.top = argTop;
	}
	
	
	private String comment; // see comment below
	
	/**
	 * A chord can have a comment containing additional information (e.g. use open position).
	 * @return Returns the comment.
	 * 
	 * @hibernate.property
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param argComment The comment to set.
	 */
	public void setComment(String argComment) {
		this.comment = argComment;
	}
	/**
	 * Create a new chord symbol with no physical or metrical time. 
	 */
	public ChordSymbol() {
	    this(null, Timed.INVALID_TIME);
	}
	
	/** 
	 * Returns the time of this ChordSymbol if it is valid
	 * (i.e., if does not equal Timed.INVALID_TIME).
	 * If this ChordSymbol has no valid time, but a metrical time,
	 * its metrical time is transformed to physical time via the
	 * MetricalTimeLine.
	 *  
	 * @return long time of this ChordSymbol which is its time set or its metrical time transformed to physical time
	 * 
	 * @see de.uos.fmt.musitech.data.time.Timed#getTime()
	 */
	@Override
	public long getTime(){
	    if (super.getTime() != Timed.INVALID_TIME)
	        return super.getTime();
	    MetricalTimeLine timeLine = new MetricalTimeLine();
	    if(getMetricTime() == null)
	        return Timed.INVALID_TIME;
	    return timeLine.getTime(getMetricTime());
	}

	/** 
	 * Returns the duration of this ChordSymbol, where its metrical 
	 * time is transformed to physical time via the MetricalTimeLine.
	 *  
	 * @return long time of this ChordSymbol which is its time set or its metrical time transformed to physical time
	 * 
	 * @see de.uos.fmt.musitech.data.time.Timed#getTime()
	 */
	@Override
	public long getDuration(){
	    MetricalTimeLine timeLine = new MetricalTimeLine();
	    if(getMetricTime() == null)
	        return Timed.INVALID_TIME;
	    return timeLine.getTime(getMetricTime().add(getMetricDuration()))-getTime();
	}

	/**
	 * Returns a String representation of this ChordSymbol.
	 *  
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

        return "ChordSymbol  at " + getMetricTime() + ": " + (getTime()>=0?getTime()+ "ms, root: ":"")
               + getRoot() + ", mode: " + modeString(getMode()) + ", rootAcc: "
               + getRootAlteration()+ ", ext: "
               + getExtensions();
	    
    }
}
