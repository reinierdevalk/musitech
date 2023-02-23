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
package de.uos.fmt.musitech.structure.harmony.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import de.uos.fmt.musitech.data.structure.harmony.*;
import de.uos.fmt.musitech.score.gui.MusicGlyph;

/**
 * A ChordDegreeDisplay is a view for ChordDegreeSymbol objects.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class ChordDegreeDisplay extends ChordSymbolDisplay2 {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8990548689733445658L;

	/**
     * ChordDegreeSymbol to be displayed.
     * Is set to either the <code>editObj</code> or <code>propertyValue</code>
     * when the display is initialized.
     */
    ChordDegreeSymbol cds;

    /**
     * JTextField for displaying the chord's degree.
     */
    JTextField degreeField;

    /**
     * JTextField for displaying the chord's extension.
     * This is the upper textfield of the two extension fields.
     */
    JTextField extensionField1;

    /**
     * JTextField for displaying the chord's extension.
     * This is the lower textfield of the two extension fields.
     */
    JTextField extensionField2;

    /**
     * Sets <code>cds</code>, i.e. the ChordDegreeSymbol to be displayed.
     * This may be either the <code>editObj</code> or the
     * <code>propertyValue</code>.<br>
     * With <code>cds</code>, the displayed ChordDegreeSymbol can be accessed
     * directly without checking
     * <code>propertyName<code>, <code>propertyValue</code> resp.
     * <code>editObj</code> each time you want to have the ChordDegreeSymbol.
     */
    @Override
	protected void setChordSymbol() {
        if (getEditObj() instanceof ChordDegreeSymbol){
            cds = (ChordDegreeSymbol) getEditObj();
        } else if (returnPropertyValue() instanceof ChordDegreeSymbol){
            cds = (ChordDegreeSymbol) returnPropertyValue();
        }
    }
    
    /**
     * Returns the ChordDegreeSymbol <code>cds</code>.
     *  
     * @see de.uos.fmt.musitech.structure.harmony.gui.ChordSymbolDisplay2#getChordSymbol()
     */
    @Override
	public ChordSymbol getChordSymbol() {
		return cds;
	}

    /**
     * Overwrites method <code>createTextfields()</code> in the superclass in
     * order to display the chord degree instead of the root. That is, the
     * <code>rootField</code> is created to display the degree symbol. 
     * Furthermore, two extension textfields are created.
     * 
     * @see de.uos.fmt.musitech.structure.harmony.gui.ChordSymbolDisplay2#createTextfields()
     */
    @Override
	protected void createTextfields() {
                super.createTextfields();
        //change rootField
        rootField = createChordSymbolTextfield(cds.createDegreeString());
        rootField.setHorizontalAlignment(SwingConstants.CENTER);
        rootField.setOpaque(false);
        //set extension fields
        extensionField1 = createChordSymbolTextfield(createExtensions()[0]);
        extensionField1.setHorizontalAlignment(SwingConstants.LEFT);
        extensionField1.setOpaque(false);
        extensionField2 = createChordSymbolTextfield(createExtensions()[1]);
        extensionField2.setHorizontalAlignment(SwingConstants.LEFT);
        extensionField2.setOpaque(false);
        //set top and base fields
        topField = createChordSymbolTextfield(cds.getTop()+"");
        topField.setHorizontalAlignment(SwingConstants.CENTER);
        topField.setOpaque(false);
        baseField = createChordSymbolTextfield(cds.getBase()+"");
        baseField.setHorizontalAlignment(SwingConstants.CENTER);
        topField.setOpaque(false);
    }

    /**
     * Adds the <code>rootField</code>, <code>extensionsField1</code> and
     * <code>extensionField2</code> to the GUI.
     *  
     * @see de.uos.fmt.musitech.structure.harmony.gui.ChordSymbolDisplay2#addTextfields()
     */
    @Override
	protected void addTextfields() {
        Box leftBox = Box.createVerticalBox();
        leftBox.setOpaque(false);
//        leftBox.add(Box.createVerticalStrut((int) (2 * scale)));
        leftBox.add(topField);
        leftBox.add(rootField);
        leftBox.add(baseField);
        //	    Box extBox = Box.createVerticalBox();
        JPanel extBox = new JPanel();
//        Box extBox = Box.createVerticalBox();
//        extBox.setBackground(Color.WHITE);
        extBox.setOpaque(false);
        extBox.setLayout(new GridLayout(0, 1));
        extBox.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
        extBox.add(Box.createVerticalStrut(2*(int)scale));	
        extBox.add(extensionField1);
//        extBox.add(Box.createVerticalStrut(-10*(int)scale));	
        extBox.add(extensionField2);
        extBox.add(Box.createVerticalGlue());
        Box symbolBox = Box.createHorizontalBox();
        symbolBox.setOpaque(false);
        symbolBox.add(leftBox);
        symbolBox.add(extBox);
        add(symbolBox);
    }

//    /**
//     * Returns a String containing the Roman number representation of the
//     * <code>chordDegree</code> of the <code>cds</code>.
//     * 
//     * @return String representing the chord degree in Roman numbers
//     */
//    private String createDegreeSymbol() {
//        int degree = cds.getChordDegree();
//        //degree must be between 1 and seven included
//        if (degree < 1 || degree > 7) {
//            degree %= 8;
//        }
//        StringBuffer buffer = new StringBuffer();
//        if (degree == 4) {
//            buffer.append("IV");
//        } else {
//            int i = 1;
//            if (degree >= 5) {
//                buffer.append("V");
//                i = 6;
//            }
//            for (int j = i; j <= degree; j++) {
//                buffer.append("I");
//            }
//        }
//        return buffer.toString();
//    }

    /**
     * Returns an Array containing two Strings to be displayed in the two
     * extension textfields. The chord's extension with the higher tone number
     * is the first element of the Array. Example: If there are extensions "4" and
     * "6", the "4" will appear as the second, the "6" as the first element in the
     * Array which is returned.
     * If there are no extensions, the Array will contain two empty Strings. If there
     * is only one extension, the second element in the Array will be an empty
     * String.
     * 
     * @return String[] containing to Strings to be displayed in the first and second extension textfield resp.
     */
    private String[] createExtensions() {
        String[] extensions = new String[2];
        String ext = cds.getExtensions();
        String ext2 = cds.getExtensions2();
        int number1 = -1;
        if (ext!=null){
            number1 = getFirstNumberInString(ext);
        }
        int number2 = -1;
        if (ext2!=null){
            number2 = getFirstNumberInString(ext2);
        }
        if (number1 > 0 ){
            if (number1 > number2){
                extensions[0] = ext;
                if (number2>0){
                    extensions[1] = ext2;
                } else {
                    extensions[1] = "";
                }
            } else {
                extensions[0] = ext2;
                extensions[1] = ext;
            }       
        } else {
            if (number2>0){
                extensions[0] = ext2;
                extensions[1] = "";
            } else {
                extensions[0] = "";
                extensions[1] = "";
            }
        }
        return extensions;
    }

    /**
     * Returns an int corresponding to the first number symbol which appears in the
     * specified String. If the String does not contain a number symbol, -1 is returned.
     * 
     * @param string String whose first number symbol is returned as an int value
     * @return int corrseponding to the first number symbol appearing in the specified String 
     */ 
    private int getFirstNumberInString(String string) {
        for (int i = 0; i < string.length(); i++) {
            try {
                Integer integer = new Integer(string.charAt(i));
                return integer.intValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    
    /**
	 * Sets the Fonts of the JTextFields.
	 * Overwrites the method in the superclass in order to set the fonts only
	 * in the <code>rootField</code>, <code>extensionField1</code> and 
	 * <code>extensionField2</code>.
	 */
	@Override
	protected void setFonts() {
		int small = (int) (18 * scale);
		int main = (int) (48 * scale);
		int mode_comment_size = (int) (26 * scale);
		int exts = (int) (18 * scale);
		rootField.setFont(new Font(FONT_NAME, Font.PLAIN, main));
		Font musicFont = MusicGlyph.createFont((int) (10 * scale));
		musicFont =
			musicFont.deriveFont(new AffineTransform(1.5, 0, 0, 1, 0, 0));
//		accidentalField.setFont(musicFont);
		topField.setFont(new Font(FONT_NAME, FONT_STYLE, small));
		baseField.setFont(new Font(FONT_NAME, FONT_STYLE, small));
		extensionField1.setFont(new Font(FONT_NAME, FONT_STYLE, exts));
		extensionField2.setFont(new Font(FONT_NAME, FONT_STYLE, exts));
//		modeField.setFont(new Font(FONT_NAME, Font.BOLD, mode_comment_size));
//		commentField.setFont(
//			new Font(FONT_NAME, Font.ITALIC, mode_comment_size));
	}

}