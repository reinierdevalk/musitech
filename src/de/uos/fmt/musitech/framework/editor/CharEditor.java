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
 * Created on 25.06.2003
 */
package de.uos.fmt.musitech.framework.editor;


/**
 * Editor for editing properties of type "char".
 * @author Tobias Widdra
 */
public class CharEditor extends 
//AbstractSimpleEditor
SimpleTextfieldEditor 
{

	/**
	 * Applies actual text within the textfield to <code>propertyValue</code>.
	 * <br> Rejects inputs which are longer than one character or are not
	 * within in the string <code>allowedCharacters</code> and shows an error dialog
	 * accordingly.
	 * @return true if the input is accepted
	 */
//	public boolean applyChangesToPropertyValue() {
//		String s = textfield.getText();
//		if ((s.length() == 1)&&(allowedCharacters.indexOf(s.charAt(0)) != -1)) {
//			propertyValue = new Character(s.charAt(0));
//			return true;
//		} else {
//			if (textfield.getText().equals("") && isEmptyDisplayOptionSet())
//				return true;
//			showErrorMessage();			
//			textfield.setText(propertyValue.toString());
//			return false;
//		}		
//	}
	
//--------------------------- handle allowed characters ------------

	/**
	 * A String containing all latin lower case characters.
	 */	
	public final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
	
	/**
	 * A String containing all latin upper case characters.
	 */
	public final String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/**
	 * A String contianing all 10 digits.
	 */
	public final String NUMBERS = "0123456789";
	
	/**
	 * A String representing all allowed input characters.
	 */
	String allowedCharacters = LOWERCASE_CHARACTERS + UPPERCASE_CHARACTERS + NUMBERS;
	
	/**
	 * Returns the set of allowed characters contained in a string.
	 * @return
	 */
	public String getAllowedCharacters() {
		return allowedCharacters;
	}

	/**
	 * Sets the set of allowed characters to the characters contained in the string
	 * <code>string</code>.
	 * @param string
	 */
	public void setAllowedCharacters(String string) {
		allowedCharacters = string;
	}
	
	/**
	 * Adds the characters contained in the string <code>string</code> to the set
	 * of allowed characters.
	 * @param string
	 */
	public void addAllowedCharacters(String string) {
		allowedCharacters += string;
	}

	/** 
	 * Returns a Character object representing the user's input.
	 * If no Character could be created from the input, null is returned.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.SimpleTextfieldEditor#getInputValue()
	 */
	@Override
	protected Object getInputValue() {
		Character inputValue = null;
		String s = textfield.getText();
		if ((s.length() == 1)&&(allowedCharacters.indexOf(s.charAt(0)) != -1)) {
			inputValue = new Character(s.charAt(0));
		}
		return inputValue;
	}
}
