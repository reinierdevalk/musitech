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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.uos.fmt.musitech.data.structure.harmony.ChordFunctionSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;

import de.uos.fmt.musitech.data.structure.harmony.ChordFunctionSymbol.FUNCTION;

/**
 * A ChordFunctionDisplay is a view for ChordFunctionSymbol objects.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class ChordFunctionDisplay extends ChordSymbolDisplay2 {
    

    /**
     * ChordFunctionSymbol to be displayed. Is set either to the
     * <code>editObj</code> or <code>propertyValue</code>.
     */
//    ChordFunctionSymbol cfs;
    

    /**
     * Additional JTextField for displaying the chord's root which here is
     * expressed as the chord's function. This additional field is needed e.g.
     * for displaying the double dominant.
     */
    JTextField rootField2 = null;

    /**
     * Sets <code>cfs</code>, i.e. the ChordFunctionSymbol to be displayed.
     * This may be either the <code>editObj</code> or the
     * <code>propertyValue</code>.<br>
     */
    @Override
	protected void setChordSymbol() {
        if (getEditObj() instanceof ChordFunctionSymbol){
//            cfs = (ChordFunctionSymbol) getEditObj();
            cs = (ChordSymbol) getEditObj();
        } else if (returnPropertyValue() instanceof ChordFunctionSymbol){
//            cfs = (ChordFunctionSymbol) returnPropertyValue();
            cs = (ChordSymbol) returnPropertyValue();
        }
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
        rootField = createChordSymbolTextfield(createFunctionSymbol());
        rootField.setHorizontalAlignment(SwingConstants.CENTER);
        //set empty mode field
        modeField.setText("");
        //use rootField2 for Doppeldominante or verkürzten Dominantseptakkord
        ChordFunctionSymbol cfs = null;
        if (cs instanceof ChordFunctionSymbol){
           cfs = (ChordFunctionSymbol)cs; 
        } 
        if (cfs==null)
            return;
        if (cfs.getChordFunction() == FUNCTION.DOPPEL_DOMINANTE.getString()){
            rootField2 = createChordSymbolTextfield("D");
            rootField2.setHorizontalAlignment(SwingConstants.NORTH_EAST);
        } else if (cfs.getChordFunction() == FUNCTION.ZWISCHEN_DOPPEL_DOMINANTE.getString()){
            rootField2 = createChordSymbolTextfield("D");
            rootField2.setHorizontalAlignment(SwingConstants.NORTH_EAST);
        }
        if (cfs.getChordFunction() == FUNCTION.DOMINANT_VERK.getString()){
            rootField2 = createChordSymbolTextfield("/");
        }
    }

    /**
     * 
     * @see de.uos.fmt.musitech.structure.harmony.gui.ChordSymbolDisplay2#addTextfields()
     */
/*    protected void addTextfields() {
        super.addTextfields();
        ChordFunctionSymbol cfs = null;
        if (cs instanceof ChordFunctionSymbol){
           cfs = (ChordFunctionSymbol)cs; 
        } 
        if (cfs==null)
            return;
        if (cfs.getChordFunction() == ChordFunctionSymbol.DOPPEL_DOMINANTE){
            rootField2 = new JTextField();
            rootField2.setHorizontalAlignment(JTextField.NORTH_EAST);
            rootField2.setText("D");
        }
        if (cfs.getChordFunction() == ChordFunctionSymbol.DOMINANT_VERK){
            rootField2 = new JTextField();
            rootField2.setHorizontalAlignment(JTextField.LEFT);
            rootField2.setText("/");
        }
        if (rootField2!=null){
            rootField2.setOpaque(false);
            rootField2.setFont(rootField.getFont());
            rootField2.setBounds(rootField.getBounds());
            Container parent = rootField.getParent();
            if (parent!=null){
                parent.add(rootField2);
            }
//            if (rootField.getPreferredSize()!=null)
//                rootField2.setPreferredSize(rootField.getPreferredSize());
//            rootField.add(rootField2);
        }
    } 
*/
    
    /**
     * Adds the textfield which are used to show the ChordFunctionSymbol to the GUI.
     */
    @Override
	protected void addTextfields(){
        //vertical box for root, top and base
		int topBaseInset = -10 * new Double(scale).intValue();
		Box left = Box.createVerticalBox();
		left.add(Box.createVerticalGlue());
		left.add(topField);
		left.add(Box.createVerticalStrut(topBaseInset)); //top nearer to root
		left.add(createRootComponent());
		left.add(Box.createVerticalStrut(topBaseInset)); //base nearer to root
		left.add(baseField);
		left.add(Box.createVerticalGlue());
		//horizontal box for accidentals and extensions
		Box accExtBox = Box.createHorizontalBox();
		accExtBox.add(accidentalField);
		accExtBox.add(
			Box.createHorizontalStrut(new Double(scale).intValue() * 5));
		accExtBox.add(extensionsField);
		accExtBox.add(Box.createHorizontalGlue());
		//horizontal box for mode and comment
		Box modeComBox = Box.createHorizontalBox();
		modeComBox.add(modeField);
		modeComBox.add(Box.createHorizontalStrut(new Double(scale).intValue() * 5));
		modeComBox.add(commentField);
		modeComBox.add(Box.createHorizontalGlue());
		//vertical box taking accidental-extensions-box and mode-comment-box
		Box right = Box.createVerticalBox();
		right.add(
			Box.createVerticalStrut(
				new Double(topField.getPreferredSize().getHeight() / 8 * scale)
					.intValue()));
		right.add(accExtBox);
		right.add(Box.createVerticalGlue());
		right.add(modeComBox);
		int baseHeight =
			new Double(baseField.getPreferredSize().getHeight() * scale)
				.intValue();
		right.add(Box.createVerticalStrut(baseHeight));
		//		add(left);
		//		add(right);
		Box all = Box.createHorizontalBox();
		all.add(left);
		all.add(Box.createHorizontalStrut(new Double(scale).intValue() * 5));
		all.add(right);
		add(all);
    }
    
    
    /**
     * Returns a JComponent displaying the root. In most cases, the <code>rootField</code>
     * will be returned. If the ChordFunctionSymbol stands for a double dominant or a
     * dominant without fundamental, the <code>rootField</code> is combined with 
     * <code>rootField2</code> in a Box which is returned.
     *  
     * @return JComponent displaying the chord's function
     */
    private JComponent createRootComponent(){
        if (rootField2!=null){ 
            rootField2.setOpaque(false);
            Box rootBox = Box.createHorizontalBox();
            rootBox.setBackground(Color.WHITE);
            if (rootField2.getText().equals("/")){
                Font font = new Font(FONT_NAME, Font.PLAIN, (int)(28*scale));
                rootField2.setFont(font);
                rootBox.add(rootField2);
                int offset = 0;
                if (rootField2.getPreferredSize()!=null){
                    offset = -(int)(rootField2.getPreferredSize().width*scale);
                }
                if (offset==0)
                    offset = (int)(-10*scale);
                Component strut = Box.createHorizontalStrut(offset);
                strut.setBackground(Color.WHITE);
                rootBox.add(strut);
                rootBox.add(rootField);
            }
            if (rootField2.getText().equals("D")){
                rootField2.setFont(rootField.getFont());
//                Font font = new Font(FONT_NAME, Font.PLAIN, (int)(36*scale));
//                rootField2.setFont(font);            
                rootBox.add(rootField);
                int offset = 0;
                if (rootField.getPreferredSize()!=null){
                    offset = - rootField2.getPreferredSize().width / 6;
                }
                if (offset==0)
                    offset = (int)(-10*scale);
                rootBox.add(Box.createHorizontalStrut(offset));
                rootBox.add(rootField2);
            }
            return rootBox;
        }
        return rootField;
    }
    

    /**
     * Returns a String representing the chord's function.
     * 
     * @return String representing the chord's function
     */
    private String createFunctionSymbol() {
        ChordFunctionSymbol cfs = null;
        if (cs instanceof ChordFunctionSymbol){
           cfs = (ChordFunctionSymbol)cs; 
        } 
        if (cfs==null)
            return ""+cs.getRoot();
        String function = cfs.getChordFunction();
        StringBuffer buffer = new StringBuffer();
        //tonic, subdominant and dominant
        if (function.length() == 1) {
            if (cfs.getMode() == Mode.MODE_MINOR) {
                //function symbol to lower case
                buffer.append(function.toLowerCase());
                return buffer.toString();
            }
            if (cfs.getMode() == Mode.MODE_MAJOR){
                return function;
            }
        }
        //Parallel- und Gegenklänge 
        if (function.length() > 2){
        	if ((function != FUNCTION.DOMINANT_VERK.getString()) 
        			&& (function != FUNCTION.DOPPEL_DOMINANTE.getString())
        			&& (function != FUNCTION.ZWISCHEN_DOMINANTE.getString()) 
        			&& (function == FUNCTION.ZWISCHEN_DOPPEL_DOMINANTE.getString())) 
        	{
                // TODO enable TG TP tp tg and corresponding for D and S 
        		if (cfs.getMode() == Mode.MODE_MAJOR) {
        			//set first char to lower case
        			buffer.append(function.charAt(1));
        			buffer.insert(0, function.toLowerCase().charAt(0));
        		} else {
        			if (cfs.getMode() == Mode.MODE_MINOR) {
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
        return buffer.toString();
    }

}