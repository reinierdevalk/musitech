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
 * Created on 16.11.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import de.uos.fmt.musitech.data.time.Timed;

/**
 * The PhysicalTimeEditor edits long values which represent physical time.
 * 
 * TODO evtl. statt textfield JComboBox oder button set/unset
 * 
 * @author Kerstin Neubarth
 *
 */
public class PhysicalTimeEditor extends LongEditor {
    
    /**
     * String to be displayed in the textfield if the time value is Timed.INVALID_TIME.
     */
    private static final String INVALID_TEXT = "time not set";
    
    /** 
	 * Returns a Long object representing the user's input.
	 * If the text in the textfield equals INVALID_TEXT, a Long of value
	 * Timed.INVALID_TIME is returned. If no Long  object could be created 
	 * from the input, null is returned.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.SimpleTextfieldEditor#getInputValue()
	 */
	protected Object getInputValue() {
		Object inputValue = null;
		if (textfield.getText().equalsIgnoreCase(INVALID_TEXT)){
		    return new Long(Timed.INVALID_TIME);
		}
		try {
			inputValue = Long.valueOf(textfield.getText());
			//TODO if <0 feedback?
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		return inputValue;
	}
	
	/**
	 * Overwrites <code>updateGUI</code> of SimpleTextfieldEditor in order to set
	 * the text in the textfield so the INVALID_TEXT if the value equals
	 * Timed.INVALID_TIME.
	 *  
	 * @see de.uos.fmt.musitech.framework.editor.SimpleTextfieldEditor#updateGUI()
	 */
	protected void updateGUI() {
	    super.updateGUI();
	    if (textfield!=null){
	        if (textfield.getText().equalsIgnoreCase(String.valueOf(Timed.INVALID_TIME))){
	            textfield.setText(INVALID_TEXT);
	        }
	    }
	}

}
