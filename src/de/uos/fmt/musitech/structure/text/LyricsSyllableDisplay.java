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
 * Created on 22.04.2003
 */
package de.uos.fmt.musitech.structure.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JTextField;

import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * Display object for one lyrics syllable.
 * 
 * @author FX
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class LyricsSyllableDisplay extends 
//JLabel
 JTextField
implements Display {

	/**
	 * The LyricSyllable to be displayed.
	 * Is the <code>editObj</code> or the <code>propertyValue</code>.
	 */
	private LyricsSyllable syllable = null;

	/**
	 * Object to be displayed.
	 * Is a LyricsSyllable or an object having a LyricsSyllable property.
	 */
	private Object editObj;
	
	/**
	 * String identifying the property to be displayed.
	 * If the <code>editObj</code> itself is the LyricsSyllable to be displayed,
	 * the <code>propertyName</code> is null.
	 */
	private String propertyName;
	
	/**
	 * Property of the <code>editObj</code> named by <code>propertyName</code>.
	 * The <code>propertyValue</code> is set via ReflectionAccess if the
	 * <code>propertyName</code> is not null.
	 */
	private Object propertyValue;

	/**
	 * EditingProfile of this Display.
	 */
	EditingProfile profile;

	/**
	 * The root of this Display.
	 */
	Display rootDisplay;

	/**
	 * Is set true, if the <code>syllable</code> displayed by this Display is
	 * change in another Editor. The flag is set, when this Display receives
	 * a DataChangeEvent.
	 */
	boolean dataChanged = false;
	
	/**
	 * Set as background color when the displayed LyricsSyllable is selected. 
	 */
	final static Color SELECTION_COLOR = Color.YELLOW;
	
	final static Color CURRENT_COLOR = Color.RED;
	
	final static Font Text_Font = new Font("SansSerif", Font.BOLD, 24);

	/**
	 * Empty constructor.
	 * <br>
	 * Necessary for creating a LyricsSyllableDisplay using the EditorFactory.
	 *
	 */
	public LyricsSyllableDisplay() {
	}

	/** 
	 * Returns the flag <code>dataChanged</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
	 */
	@Override
	public boolean externalChanges() {
		return dataChanged;
	}

	/** 
	 * Removes this DataChangeListener from the table of the DataChangeManager.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
	 */
	@Override
	public void destroy() {
		DataChangeManager.getInstance().removeListener(this);
	}

	/** 
	 * Updates this Display if there are external data changes.
	 * If this Display is not the <code>rootDisplay</code>, calls
	 * method <code>focusReceived()</code> of the <code>rootDisplay</code>.
	 * <br>
	 * This method is invoked when this Display gains the focus.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
	 */
	@Override
	public void focusReceived() {
		if (externalChanges())
			updateDisplay();
	}

	/** 
	 * Getter for the <code>profile</code>.
	 * Returns the EditingProfile of this Display.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
	 */
	@Override
	public EditingProfile getEditingProfile() {
		return profile;
	}

	/** 
	 * Returns the <code>editObj</code> of this LyricsSyllableDisplay.
	 * <br>
	 * The <code>editObj</code> may be a LyricsSyllable or an object having
	 * a LyricsSyllable property.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
	 */
	@Override
	public Object getEditObj() {
		return editObj;
	}

	/**
	 * Getter for <code>syllable</code>.
	 * Returns the LyricsSyllable to be displayed.
	 * Is the <code>editObj</code> or the <code>propertyValue</code>.
	 * 
	 * @return LyricsSyllable the LyricsSyllableDisplay shows
	 */
	public LyricsSyllable getSyllable() {
		return syllable;
	}

	/** 
	 * Returns true, if this Display is the focus owner.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
	 */
	@Override
	public boolean isFocused() {
		return isFocusOwner();
	}

	/** 
	 * Initiliazes this Display. Sets the specified arguments as the <code>editObj</code>,
	 * the <code>profile</code> and the <code>rootDisplay</code> of this Display.
	 * <code>propertyName</code> and <code>propertyValue</code> are set, if the
	 * <code>profile</code> specifies a property name.
	 * Registers this Display at the DataChangeManager and creates the GUI.
	 * <br>
	 * The argument <code>editObject</code> must not be null.
	 * It is recommended to create a LyricsSyllableDisplay using the EditorFactory.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object, de.uos.fmt.musitech.framework.editor.EditingProfile, de.uos.fmt.musitech.framework.editor.Display)
	 */
	@Override
	public void init(Object editObject, EditingProfile profile, Display root) {
		if (editObject != null)
			this.editObj = editObject;
		else {
			//		if (editObject!=null && editObject instanceof LyricsSyllable)
			//			syllable = (LyricsSyllable)editObject;
			//		if (syllable == null){ //if this Display is created using the EditorFactory, this does not occur
			System.err.println(
				"In LyricsSyllableDisplay, method init(Object, EditingProfile, Display)\n"
					+ "The argument Object must not be null.");
			return;
		}
		this.profile = profile;
		if (profile != null) {
			this.propertyName = profile.getPropertyName();
			setPropertyValue();
		}
		this.rootDisplay = root;
		setSyllable();
		if (syllable != null) {
			//register at DataChangeManager
			Collection data = new ArrayList();
			data.add(syllable);
			DataChangeManager.getInstance().interestExpandElements(this, data);
			createGUI();
		}
	}

	/**
	 * Sets the <code>propertyValue</code> via ReflectionAccess
	 * if <code>propertyName</code> is not null and the <code>editObj</code>
	 * has a property of this name.
	 */
	private void setPropertyValue() {
		if (propertyName != null) {
			ReflectionAccess ref =
				ReflectionAccess.accessForClass(editObj.getClass());
			if (ref.hasPropertyName(propertyName))
				propertyValue = ref.getProperty(editObj, propertyName);
		}
	}

	/**
	 * Sets the <code>syllable</code>, i.e. the LyricsSyllable to be displayed
	 * to either the <code>editObj</code> or the <code>propertyValue</code>.
	 */
	private void setSyllable() {
		if (editObj instanceof LyricsSyllable)
			syllable = (LyricsSyllable) editObj;
		else if (propertyValue instanceof LyricsSyllable)
			syllable = (LyricsSyllable) propertyValue;
	}

	/** 
	 * Updates the display to changed data by rebuilding the GUI.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
	 */
	@Override
	public void updateDisplay() {
		removeAll();
		createGUI();
	}

	/** 
	 * Getter for <code>rootDisplay</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#getRootDisplay()
	 */
	@Override
	public Display getRootDisplay() {
		return rootDisplay;
	}

	/** 
	 * Sets the flag <code>dataChanged</code>.
	 * This method is invoked when the display received a DataChangeEvent.
	 * <br>
	 * As the LyricsSyllableDisplay is a Display (at not an Editor), data changes
	 * have been performed outside this Display, so the source of the specified
	 * DataChangeEvent needs not be checked.
	 * 
	 * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
	 */
	@Override
	public void dataChanged(DataChangeEvent e) {
		dataChanged = true;
	}

	/**
	 * Creates  the graphical user interface. 
	 */
	public void createGUI() {
		setEditable(false);
		setBackground(Color.WHITE);
		setBorder(null);
		setFont(Text_Font);
		setText(syllable.getText());
		setFocusable(true);
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				focusReceived();
			}
		});
	}
	
	/**
	 * If <code>mark</code> is true, the background color is changed to 
	 * SELECTION_COLOR. If <code>mark</code> is false, the background color
	 * is reset to white.
	 * <br>
	 * This method is invoked by LyricsDisplay which is a SelectionListener.
	 * 
	 * @param mark
	 */
	public void markSelected(boolean mark){
		if (mark){
			setBackground(SELECTION_COLOR);
		} else {
			setBackground(Color.WHITE);
		}
	}
	
	/**
	 * If <code>mark</code> is true, the background color is changed to 
	 * CURRENT_COLOR. If <code>mark</code> is false, the background color
	 * is reset to white.
	 * <br>
	 * This method is invoked by LyricsDisplay which is a Timeable. It highlights
	 * the syllable currently played back with the <code>CURRENT_COLOR</code>.
	 * 
	 * @param mark
	 */
	public void markCurrentlyPlayed(boolean mark){
	    if (mark) {
	        setBackground(CURRENT_COLOR);
	    } else {
	        setBackground(Color.WHITE);
	    }
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	@Override
	public Component asComponent() {
		return this;
	}

}
