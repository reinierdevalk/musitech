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
 * Created on 04.10.2004
 *
 */
package de.uos.fmt.musitech.data.structure.harmony;

import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class for representing a chord as a scale degree plus mode and extensions.
 * 
 * @author Tillman Weyde, Kerstin Neubarth
 */
public class ChordDegreeSymbol extends ChordSymbol {
    
    /**
     * int representing the degree of the ChordSymbol.
     */
    private int chordDegree = 1;    
    private String extensions2 = "";
    
// ------------------------ constructors ---------------------------------
    
    /**
     * Constructor.
     * Calls the empty constructor of the superclass.
     */
    public ChordDegreeSymbol(){
        super();
    }
    
    /**
     * Constructor.
     * Calls the corresponding constructor of the superclass.
     * 
     * @param metricTime Rational metrical time to be set for this ChordDegreeSymbol
     * @param time long physical time to be set for this ChordDegreeSymbol
     */
    public ChordDegreeSymbol(Rational metricTime, long time){
        super(metricTime, time);
    }
    
    /**
     * Constructor.
     * Calls the corresponding constructor of the superclass.
     * 
     * @param metricTime Rational metrical time to be set for this ChordDegreeSymbol
     * @param piece Piece to be set for this ChordDegreeSymbol
     */
    public ChordDegreeSymbol(Rational metricTime, Piece piece){
        super(metricTime, piece);
    }
    
    /**
     * Constructor.
     * Sets the metrical time, physical time, and <code>chordDegree</code> of this
     * ChordDegreeSymbol according to the specified arguments.
     * 
     * @param metricTime Rational metrical time to be set for this ChordDegreeSymbol
     * @param time long physical time to be set for this ChordDegreeSymbol
     * @param chordDegree int to be set as the <code>chordDegree</code> of this ChordDegreeSymbol
     */
    public ChordDegreeSymbol(Rational metricTime, long time, int chordDegree){
        this(metricTime, time);
        setChordDegree(chordDegree);
    }
    
    /**
     * Constructor.
     * Sets the metrical time, physical time, <code>chordDegree</code> and
     * <code>extensions</code> of this ChordDegreeSymbol according to the 
     * specified arguments.
     * 
     * @param metricTime Rational metrical time to be set for this ChordDegreeSymbol
     * @param time long physical time to be set for this ChordDegreeSymbol
     * @param chordDegree int to be set as the <code>chordDegree</code> of this ChordDegreeSymbol
     * @param extensions String to be set as the <code>extensions</code> of this ChordDegreeSymbol
     */
    public ChordDegreeSymbol(int chordDegree, String extensions){
        this(Rational.ZERO, 0, chordDegree, extensions);
    }

    /**
     * Constructor.
     * Sets the metrical time, physical time, <code>chordDegree</code> and
     * <code>extensions</code> of this ChordDegreeSymbol according to the 
     * specified arguments.
     * 
     * @param metricTime Rational metrical time to be set for this ChordDegreeSymbol
     * @param time long physical time to be set for this ChordDegreeSymbol
     * @param chordDegree int to be set as the <code>chordDegree</code> of this ChordDegreeSymbol
     * @param extensions String to be set as the <code>extensions</code> of this ChordDegreeSymbol
     */
    public ChordDegreeSymbol(int chordDegree, String extensions, boolean major){
        this(Rational.ZERO, 0, chordDegree, extensions);
        if(major)
        	setMode(Mode.MODE_MINOR);
    }

    
    /**
     * Constructor.
     * Sets the metrical time, physical time, <code>chordDegree</code> and
     * <code>extensions</code> of this ChordDegreeSymbol according to the 
     * specified arguments.
     * 
     * @param metricTime Rational metrical time to be set for this ChordDegreeSymbol
     * @param time long physical time to be set for this ChordDegreeSymbol
     * @param chordDegree int to be set as the <code>chordDegree</code> of this ChordDegreeSymbol
     * @param extensions String to be set as the <code>extensions</code> of this ChordDegreeSymbol
     */
    public ChordDegreeSymbol(Rational metricTime, long time, int chordDegree, String extensions){
        this(metricTime, time);
        setChordDegree(chordDegree);
        setExtensions(extensions);
    }
    
// ------------------ getters & setters ---------------------------------
    
    /**
     * Getter for <code>chordDegree</code>.
     * 
     * @return int the <code>chordDegree</code> of this ChordDegreeSymbol
     */
    public int getChordDegree(){
        return chordDegree;
    }
    
    /**
     * Setter for <code>chordDegree</code>.
     * 
     * @param chordDegree int to be set as the <code>chordDegree</code> of this ChordDegreeSymbol
     */
    public void setChordDegree(int chordDegree){
        this.chordDegree = chordDegree;
    }
    
    public String getExtensions2(){
        return extensions2;
    }
    
    public void setExtensions2(String extensions2){
        this.extensions2 = extensions2;
    }
    
    
// ------------------------------------------------------------------------    
    
    /**
     * Returns a String representation of this ChordDegreeSymbol. 
     * 
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString(){
        return super.toString();
        //TODO überschreiben
    }
    
    /**
     * Returns a String containing the Roman number representation of the
     * <code>chordDegree</code> of the <code>cds</code>.
     * 
     * @return String representing the chord degree in Roman numbers
     */
    public String createDegreeString() {
        //degree must be between 1 and seven included
        if (chordDegree < 1 || chordDegree > 7) {
            chordDegree %= 8;
        }
        StringBuffer buffer = new StringBuffer();
        if (chordDegree == 4) {
            buffer.append("IV");
        } else {
            int i = 1;
            if (chordDegree >= 5) {
                buffer.append("V");
                i = 6;
            }
            for (int j = i; j <= chordDegree; j++) {
                buffer.append("I");
            }
        }
        return buffer.toString();
    }

    public String getDisplayString(){
        String function = createDegreeString();
        StringBuffer buffer = new StringBuffer(function);
        buffer.append(getExtensions());
        if(base != 0)
        	buffer.insert(0, getBase());
        return buffer.toString();
    }


}
