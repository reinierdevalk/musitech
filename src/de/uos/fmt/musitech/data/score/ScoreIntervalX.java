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
 * File ScoreInterval.java
 * Created on 04.04.2004
 */
package de.uos.fmt.musitech.data.score;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * A pitch interval in a diatonic representation, i.e. an augmented second is
 * distinguishable from a minor third.
 * 
 * @author tweyde
 */
public class ScoreIntervalX {
    
    public static final short PRIME = 0;
    public static final short SECOND = 1;
    public static final short THIRD = 2;
    public static final short FOURTH = 3;
    public static final short FIFTH = 4;
    public static final short SIXTH = 5;
    public static final short SEVENTH = 6;
    public static final short OCTAVE = 7;
    public static final short NINTH = 8;
    public static final short TENTH = 9;
    public static final short ELEVENTH = 10;
    public static final short TWELFTH = 11;

    public static final byte PERFECT = 0;
    public static final byte MAJOR = 1;
    public static final byte MINOR = -1;
    public static final byte AUGMENTED = 2;
    public static final byte DIMINISHED = -2;
    public static final byte DOUBLE_AUGMENTED = 3;
    public static final byte DOUBLE_DIMINISHED = -3;
    public static final int modifOffset = 3; 
    
    private static String[] intervalNames = new String[12];
    static{
        intervalNames[PRIME] = "prime";
        intervalNames[SECOND] = "second";
        intervalNames[THIRD] = "third";
        intervalNames[FOURTH] = "fourth";
        intervalNames[FIFTH] = "fifth";
        intervalNames[SIXTH] = "sixth";
        intervalNames[SEVENTH] = "seventh";
        intervalNames[OCTAVE] = "octave";
        intervalNames[NINTH] = "ninth";
        intervalNames[TENTH] = "tenth";
        intervalNames[ELEVENTH] = "eleventh";
        intervalNames[TWELFTH] = "twelfth";
    }
    
    static final boolean UP = true;
    static final boolean DOWN = false;
    
    boolean direction = UP;

    private static String[] modifNames = new String[7];
    static{
        intervalNames[PERFECT + modifOffset] = "perfect";
        intervalNames[MAJOR + modifOffset] = "major";
        intervalNames[AUGMENTED + modifOffset] = "aufgmented";
        intervalNames[DOUBLE_AUGMENTED + modifOffset] = "aufgmented";
        intervalNames[MINOR + modifOffset] = "minor";
        intervalNames[DIMINISHED + modifOffset] = "diminished";
        intervalNames[DOUBLE_DIMINISHED + modifOffset] = "diminished";
    }

    private int diatonicInt;
    private byte type;
    
    ScoreIntervalX intervals[][] = new ScoreIntervalX[intervalNames.length][modifNames.length];
    
    static final List<Integer> P_INTERVALS;
    static final List<Integer> M_INTERVALS;
    static{
        int[] p_ints = new int[]{0,3,4,7,10,11};
    	P_INTERVALS = new ArrayList<Integer>(p_ints.length);
    	for (int i = 0; i < p_ints.length; i++) {
			P_INTERVALS.add(p_ints[i]);
		}
        int[] i_ints = new int[]{1,2,5,6,8,9};
    	M_INTERVALS = new ArrayList<Integer>(p_ints.length);
    	for (int i = 0; i < p_ints.length; i++) {
			M_INTERVALS.add(i_ints[i]);
		}
    	
    }
    
    
    
    
	/**
	 * Create a new Interval with the given diatonic size in perfect or major type. 
	 * @param argDiatonic
	 */
	public ScoreIntervalX(int argDiatonic) {
		diatonicInt = argDiatonic;
		if(M_INTERVALS.contains(argDiatonic))
			setType(MAJOR);
	}

	/**
	 * Create a new Interval with the given diatonic size in perfect or major type. 
	 * @param argDiatonic
	 */
	public ScoreIntervalX(int argDiatonic, byte argType) throws IllegalArgumentException {
		this(argDiatonic);
		if(M_INTERVALS.contains(argDiatonic)){ 
			if(argType == 0)
				throw new IllegalArgumentException("A interval or size "+intervalNames[diatonicInt]+"can not be of type "+modifNames[argType+modifOffset]);
		}else{
			if(Math.abs(argType) == 1)
				throw new IllegalArgumentException("A interval or size "+intervalNames[diatonicInt]+"can not be of type "+modifNames[argType+modifOffset]);
		}
		setType(argType);
	}

	/**
	 * Create a new perfect prime.
	 */
	private ScoreIntervalX() {
	}

    /**
     * The interval as diatonic distance, e.g. a prime is 0, a fifth is 4, an
     * octave is 7.
     * 
     * @return Returns the interval.
     */
    public int getDiatonicInterval() {
        return diatonicInt;
    }
    
    /**
     * The interval as diatonic distance, e.g. a prime is 0, a fifth is 4, an
     * octave is 7.
     * 
     * @param interval
     *            The interval to set.
     */
    public void setDiatonicInterval(int interval) {
        this.diatonicInt = interval;
    }
    
    /**
     * The type of the interval: perfect is 0, major 1, augmented, 2, minor -1,
     * diminished -2, etc.
     * 
     * @return Returns the type.
     */
    public byte getType() {
        return type;
    }

    /**
     * The type of the interval: perfect is 0, major 1, augmented, 2, minor -1,
     * diminished -2, etc.
     * 
     * @param type
     *            The type to set.
     */
    public void setType(byte type) {
        this.type = type;
    }
    
    /**
     * Returns a String with the name of this ScoreInterval. The name consists of the
     * quality (derived from the <code>type</code>) and size (derived from <code>interval</code>).
     * 
     * @return String the music-theoretical name of this ScoreInterval
     */
    public String returnName(){
        int intervalPos = diatonicInt;
        if (diatonicInt<0){
            intervalPos*=-1;
        }
        String size=null;
        String quality=null;
        Field[] fields = getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            int modifier = fields[i].getModifiers();
//            if (modifier == Modifier.STATIC){
                Class fieldType = fields[i].getType();
                if (fieldType.equals(byte.class)){
                    try {
                        if (fields[i].getByte(this) == type)
                            quality = fields[i].getName()+" ";
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }  
                }
                if (fieldType.equals(int.class)){
                    try {
                        if (fields[i].getInt(this) == intervalPos)
                            size = fields[i].getName();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
//            }
        }
        String name = quality + size;
        return name.toLowerCase();
    }
}
