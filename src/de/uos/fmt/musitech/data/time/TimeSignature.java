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
package de.uos.fmt.musitech.data.time;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;

import de.uos.fmt.musitech.time.gui.TimeSignatureDisplay;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * Class representing a time signature.
 * 
 * @author TW
 * @version $Revision $, $Date $
 */

public class TimeSignature implements VetoableChangeListener, Serializable {

    static final long serialVersionUID = 1L;
    protected transient java.beans.PropertyChangeSupport propertyChange;
    private int fieldNumerator = 0;
    private int fieldDenominator = 0;
    private Rational duration = Rational.ZERO;

    public TimeSignature() {
        this(4, 4);
    }

    public TimeSignature(int numerator, int denominator) {
        setNumDenom(numerator,denominator);
    }
    
    public TimeSignature(Rational meter){
        this(meter.getNumer(), meter.getDenom());
    }
    
    /**
     * Convenience method to set the Numerator and Denominator
     * @param num
     * @param denom
     */
    public void setNumDenom(int num, int denom){
        fieldNumerator = num;
        fieldDenominator = denom;
        updateDuration();
    }

    /**
     * Updates the duration value from the numerator and denominator fields. 
     */
    private void updateDuration() {
        duration = new Rational(fieldNumerator,fieldDenominator);        
    }

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange
     * field.
     */
    public synchronized void addPropertyChangeListener(
                                                       java.beans.PropertyChangeListener listener) {
        getPropertyChange().addPropertyChangeListener(listener);
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Gets the numerator property (int) value.
     * 
     * @return The numerator property value.
     * @see #setCounter
     */
    public int getNumerator() {
        return fieldNumerator;
    }

    /**
     * Gets the denominator property (int) value.
     * 
     * @return The denominator property value.
     * @see #setDenominator
     */
    public int getDenominator() {
        return fieldDenominator;
    }

    /**
     * Getter for the propertyChange field.
     */
    protected java.beans.PropertyChangeSupport getPropertyChange() {
        if (propertyChange == null) {
            propertyChange = new java.beans.PropertyChangeSupport(this);
        }
        ;
        return propertyChange;
    }

    /**
     * Method to compare two TimeSignatures. Compares the numerator and the denominator.
     * 
     * @return boolean
     * @param other music.TimeSignature
     */
    public boolean isEquivalent(TimeSignature other) {
        return getNumerator() == other.getNumerator()
               && getDenominator() == other.getDenominator();
    }

    /**
     * The removePropertyChangeListener method was generated to support the propertyChange
     * field.
     */
    public synchronized void removePropertyChangeListener(
                                                          java.beans.PropertyChangeListener listener) {
        getPropertyChange().removePropertyChangeListener(listener);
    }

    /**
     * Sets the numerator property (int) value.
     * 
     * @param numerator
     *            The new value for the property.
     * @see #getCounter
     */
    public void setNumerator(int numerator) {
        int oldValue = fieldNumerator;
        fieldNumerator = numerator;
        firePropertyChange("numerator", Integer.valueOf(oldValue), Integer.valueOf(numerator));
        updateDuration(); 
    }

    /**
     * Sets the denominator property (int) value.
     * 
     * @param denominator
     *            The new value for the property.
     * @see #getDenominator
     */
    public void setDenominator(int denominator) {
        int oldValue = fieldDenominator;
        fieldDenominator = denominator;
        firePropertyChange("denominator", new Integer(oldValue), new Integer(denominator));
        updateDuration();
    }

    /**
     * Sets the denominator property (int) value.
     * 
     * @param denominator
     *            The new value for the property.
     * @see #getDenominator
     */
    public void setDenominator(String denominator) {
        int oldValue = fieldDenominator;
        fieldDenominator = Integer.parseInt(denominator);
        firePropertyChange("denominator", new Integer(oldValue), new Integer(denominator));
    }

    /**
     * Returns the duration of one measure / one bar.
     * 
     * @return the duration of one measure/bar.
     */
    public Rational getMeasureDuration() {
        return duration;
    }

    /**
     * Returns the TimeSignature.
     * 
     * @return String
     */
    @Override
	public String toString() {
        return "(TimeSignature) " + getNumerator() + "/" + getDenominator();
    }

    /**
     * Method to control the DenominatorField.
     * @date (04.04.00 03:07:19)
     * @param evt java.beans.PropertyChangeEvent
     * @exception java.beans.PropertyVetoException The exception description.
     */
    @Override
	public void vetoableChange(PropertyChangeEvent evt)
            throws java.beans.PropertyVetoException {
//        try {
            int iNew = Integer.parseInt(evt.getNewValue().toString());
            if (iNew < 1 || iNew > 64)
                throw (new PropertyVetoException("out of range", evt));
            if (evt.getSource() == ((TimeSignatureDisplay) ((java.awt.Component) evt
                    .getSource()).getParent()).getDenominatorField()) {
                double log2 = Math.log(iNew) / Math.log(2);
                if (Math.abs(log2 - (long) log2) > 0)
                    throw (new PropertyVetoException("invalid denominator, must be power of 2", evt));
            }
//        } catch (Exception e) {
//            throw (new PropertyVetoException("format error", evt));
//        }
    }
    
    
    /**
     * Checks if the <code>obj</code> argument is of class TimeSignature and 
     * has equal numerator and denominator values as this.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object obj) {
        if(obj == null || !(this.getClass().isInstance(obj)))
            return false;
        TimeSignature tsmObj = (TimeSignature) obj;
        if(this.fieldDenominator != tsmObj.fieldDenominator)
            return false;
        if(this.fieldNumerator != tsmObj.fieldNumerator)
            return false;
        return true;
    }
   
}