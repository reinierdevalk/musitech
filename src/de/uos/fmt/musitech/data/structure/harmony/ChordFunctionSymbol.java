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
 * Class for representing a ChordSymbol expressed in harmonic function
 * according to Hugo Riemann. Here, the symbols are given as used in German-speaking
 * music theory. The class provides final fields for the common functions.
 * The default function is the tonic.
 * 
 * @author Kerstin Neubarth, Tillman Weyde
 */
public class ChordFunctionSymbol extends ChordSymbol {

    public static enum FUNCTION { TONIKA, 
    	TONIKA_PARALLELE, TONIKA_GEGEN, SUBDOMINANTE, 
    	SUBDOMINANT_PARALLELE, SUBDOMINANT_GEGEN, DOMINANTE,DOMINANT_PARALLELE, 
    	DOMINANT_GEGENKLANG, DOPPEL_DOMINANTE, DOMINANT_VERK,
    	ZWISCHEN_DOMINANTE, ZWISCHEN_DOPPEL_DOMINANTE;
    
		public String getString() {
			switch (this) {
			case TONIKA: return "T";
			case TONIKA_PARALLELE: return "TP";
			case TONIKA_GEGEN: return "TG";
			case SUBDOMINANTE: return "S";
			case SUBDOMINANT_PARALLELE: return "SP";
			case SUBDOMINANT_GEGEN: return "SG";
			case DOMINANTE: return "D";
			case DOMINANT_PARALLELE: return "DP";
			case DOMINANT_GEGENKLANG: return "DG";
			case DOPPEL_DOMINANTE: return "DD";
			case DOMINANT_VERK: return "vD";
			case ZWISCHEN_DOMINANTE: return "D:";
			case ZWISCHEN_DOPPEL_DOMINANTE: return "DD:";
			default:
				throw new RuntimeException("unknown chord function");
			}
		}
    }	

    
//    public static final String TONIKA = "T";
//    public static final String TONIKA_PARALLELE = "TP";
//    public static final String TONIKA_GEGEN = "TG";
//    public static final String SUBDOMINANTE = "S";
//    public static final String SUBDOMINANT_PARALLELE = "SP";
//    public static final String SUBDOMINANT_GEGEN = "SG";
//    public static final String DOMINANTE = "D";
//    public static final String DOMINANT_PARALLELE = "DP";
//    public static final String DOMINANT_GEGENKLANG = "DG";
//    public static final String DOPPEL_DOMINANTE = "DD";
//    public static final String DOMINANT_VERK = "vD";	//verkürzter Dominant(sept)akkord
    
    /**
     * String representing the function of this ChordFunctionSymbol.
     */
    private FUNCTION chordFunction = FUNCTION.TONIKA;
    
    public String getDisplayString(){
        String function = getChordFunction();
        StringBuffer buffer = new StringBuffer();
        //tonic, subdominant and dominant
        if (function.length() == 1) {
            if (getMode() == Mode.MODE_MINOR) {
                //function symbol to lower case
                buffer.append(function.toLowerCase());
            } else {
                buffer.append(function);
            }
        }
        //Parallel- und Gegenklänge 
        if (function.length() >= 2){
        	if ((function != FUNCTION.DOMINANT_VERK.getString()) 
        			&& (function != FUNCTION.DOPPEL_DOMINANTE.getString())
        			&& (function != FUNCTION.ZWISCHEN_DOMINANTE.getString()) 
        			&& (function == FUNCTION.ZWISCHEN_DOPPEL_DOMINANTE.getString())) 
        	{
                // TODO enable TG TP tp tg and corresponding for D and S 
        		if (getMode() == Mode.MODE_MAJOR) {
        			//set first char to lower case
        			buffer.append(function.charAt(1));
        			buffer.insert(0, function.toLowerCase().charAt(0));
        		} else {
        			if (getMode() == Mode.MODE_MINOR) {
        				//set second char to lower case
        				buffer.append(function.charAt(0));
        				buffer.append(function.toLowerCase().charAt(1));
        			}
        		}
        	} else if(function == FUNCTION.ZWISCHEN_DOMINANTE.getString() || 
        			function == FUNCTION.ZWISCHEN_DOPPEL_DOMINANTE.getString()) {
        		buffer.append("D:"); 
        	}{
        		buffer.append("D"); 
        	}
        }
        buffer.append(getExtensions());
        if(base != 0)
        	buffer.insert(0, getBase());
        return buffer.toString();
    }
    
    /**
     * Constructor.
     * Calls the empty constructor of the superclass.
     */
    public ChordFunctionSymbol(){
        super();
    }

    /**
     * Constructor.
     * Calls the empty constructor of the superclass.
     */
    public ChordFunctionSymbol(FUNCTION fun){
        super();
        this.setChordFunction(fun,true);
    }

    /**
     * Constructor.
     * Calls the empty constructor of the superclass.
     */
    public ChordFunctionSymbol(FUNCTION fun, String s){
        super();
        this.setChordFunction(fun,true);
        this.setExtensions(s);    }

    /**
     * Constructor.
     * Calls the empty constructor of the superclass.
     */
    public ChordFunctionSymbol(FUNCTION fun, boolean major){
        super();
        this.setChordFunction(fun, major);
    }

    /**
     * Constructor.
     * Calls the corresponding constructor of the superclass.
     * 
     * @param metricTime Rational metrical time to be set for this ChordFunctionSymbol
     * @param time long physical time to be set for this ChordFunctionSymbol
     */
    public ChordFunctionSymbol(Rational metricTime, long time){
        super(metricTime, time);
    }
    
    /**
     * Constructor.
     * Calls the corresponding constructor of the superclass.
     * 
     * @param metricTime Rational metrical time to be set for this ChordFunctionSymbol
     * @param piece Piece to be set for this ChordFunctionSymbol 
     */
    public ChordFunctionSymbol(Rational metricTime, Piece piece){
        super(metricTime, piece);
    }
    
    /**
     * Setter for <code>chordFunction</code>.
     * 
     * @param chordFunction String to be set as the <code>chordFunction</code> of this ChordFunctionSymbol
     */
    public void setChordFunction(FUNCTION chordFunction){
        this.chordFunction = chordFunction;
    }

    /**
     * Setter for <code>chordFunction</code>.
     * 
     * @param chordFunction String to be set as the <code>chordFunction</code> of this ChordFunctionSymbol
     */
    public void setChordFunction(FUNCTION chordFunction, boolean major){
        this.chordFunction = chordFunction;
        if(!major)
        	this.setMode(Mode.MODE_MINOR);
        else 
        	this.setMode(Mode.MODE_MAJOR);
    }
    
    /**
     * Getter for the <code>chordFunction</code>.
     * 
     * @return String the <code>chordFunction</code> ofr this ChordFunctionSymbol  
     */
    public String getChordFunction(){
        return chordFunction.getString();
    }
    
    /**
     * Returns a String representation of this ChordFunctionSymbol. 
     * 
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString(){
        return getChordFunction() + " " + super.toString();
        //TODO überschreiben
    }

}
