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
 * Created on 17.05.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class provides an editor for displaying String objects which are to long to
 * be shown in a single-line JTextField. Thus, a JTextArea is used and the lines are
 * wrapped if necessary.
 * <br>
 * The TextEditor does not check the input, i.e. all forms of String input are accepted.
 * 
 * TODO TextEditor cannot really edit
 * 
 * @author Kerstin Neubarth
 *
 */
public class TextEditor extends AbstractSimpleEditor {

	/**
	 * JTextArea showing the String to be displayedJTextArea showing the String to be displayed
	 */
	JTextArea textArea = new JTextArea();

	/** 
	 * Returns true, if the <code>textArea</code> is not null and its text is set as
	 * <code>propertyValue</code>, false otherwise.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.AbstractSimpleEditor#applyChangesToPropertyValue()
	 */
	@Override
	public boolean applyChangesToPropertyValue() {
		if (textArea != null) {
			propertyValue = textArea.getText();
			return true;
		}
		return false;
	}

	/** 
	 * Creates the graphical user interface.
	 * <br> The text to be displayed is shown in a JTextArea which wraps lines
	 * between words if necessary. 
	 * <br>
	 * If line wrapping occurs, the size of the JTextArea is modified to show all lines. 
	 * Therefore, if you want to set the size of the TextEditor by your own, 
	 * you should do so AFTER having invoked method <code>createGUI()</code>  
	 * and you should use method <code>setEditorSize(Dimension)</code> of this class. 
	 * (The other way round, <code>createGUI()</code> would change your settings.)
	 * When using the EditorFactory to create an editor, the GUI will be set up
	 * in creating the editor and you can simply change the size of the editor
	 * returned by the EditorFactory.
	 * 	  
	 * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
	 */
	@Override
	protected void createGUI() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		//set up textArea
//		textArea = new JTextArea();
		if (propertyValue != null)
			textArea.setText(propertyValue.toString());
//		else if (propertyName == null && editObj != null)
//			textArea.setText(editObj.toString());
		else if (editObj !=null)
		    textArea.setText(editObj.toString());
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		if (profile!=null && profile.isReadOnly()){
//			textArea.setEditable(false);
			textArea.setEnabled(false);
			textArea.setDisabledTextColor(Color.BLACK);
		}
		//as textArea initilially shows only one line of text, change its size
//		textArea.addAncestorListener(new AncestorListener() {
//			public void ancestorAdded(AncestorEvent event) {
//				determineSize();
//			}
//			public void ancestorMoved(AncestorEvent event) {
//				determineSize();
//			}
//			public void ancestorRemoved(AncestorEvent event) {
//			}
//		});
		//		add DocumentListener which sets the dirty flag when data is changed
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				setDirty(true);
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				//changedUpdate(e);
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

		});
		textArea.addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e){
				if (!applyChangesToPropertyValue())
					textArea.requestFocusInWindow();
			}
		});
		//add textArea to TextEditor
		add(textArea);
	}

	/**
	 * Sets the preferred size of this TextEditor. The width equals the preferred
	 * width of the <code>textArea</code>. The height is determined so that
	 * all lines of the text are shown.
	 */
/*	protected void determineSize() {
		int width = (int) textArea.getPreferredSize().getWidth();
		String text = textArea.getText();
		String substr = "";
		int substrWidth = 0;
		int i = 0;
		while (substrWidth < width && i < text.length()) {
			substr = text.substring(0, i++);
			substrWidth = new JTextField(substr).getPreferredSize().getWidth();
		}
		int lineNumber = text.length() / (substr.length());
		int charHeight = textArea.getFont().getSize();
		setPreferredSize(new Dimension(width, charHeight * lineNumber));
	}
	*/

	/**
	 * Sets the preferred size of this TextEditor to the specified <code>size</code>
	 * and rebuilds the GUI.
	 * 
	 * @param size Dimension to be set as preferred size of this TextEditor
	 */
	public void setEditorSize(Dimension size) {
		//		textArea.setPreferredSize(size);
		//		setPreferredSize(size);
		////		setSize(size);
		//		revalidate();
		//		repaint();
		removeAll();
		textArea = new JTextArea();
		if (propertyValue != null)
			textArea.setText(propertyValue.toString());
		else if (propertyName == null && editObj != null)
			textArea.setText(editObj.toString());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		//		textArea.setEditable(false);
		add(textArea);
		setPreferredSize(size);
		revalidate();
	}

	/**
	 * Sets the background of the <code>textArea</code> to the specified Color. 
	 * 
	 * @param background Color to be set for the background
	 */
	public void setBackgroundColor(Color background) {
		textArea.setBackground(background);
		revalidate(); //TODO or updateGUI()?
	}
	
	/**
	 * Sets the foreground color of the <code>textArea</code> to the specified Color.
	 * 
	 * @param foreground
	 */
//	public void setForegroundColor(Color foreground){
//		textArea.setForeground(foreground);
//		revalidate(); //TODO or updateGUI()?
//	}
	
	/**
	 * Sets the Font of the <code>textArea</code> to the specified Font.
	 * 
	 * @param Font to be set as font of the <code>textArea</code>
	 */
	public void setTextFont(Font textFont){
		textArea.setFont(textFont);
		revalidate(); //TODO or updateGUI()?
	}
	
	public void setTextLineWrap(boolean lineWrap){
	    textArea.setLineWrap(lineWrap);
	    if (lineWrap){
	        textArea.setWrapStyleWord(true);
	    }
	}
	
	/**
	 * Sets the opacity of the <code>textArea</code> according to the specified boolean.
	 * If <code>opaque</code> is true, the textArea is set opaque, if <code>opaque</code>
	 * is false, the <code>textArea</code> will not be opaque.
	 * 
	 * @param opaque boolean indicating how to set the opacity of the <code>textArea</code>
	 */
	public void setAreaOpaque(boolean opaque){
		textArea.setOpaque(opaque);
	}
}
