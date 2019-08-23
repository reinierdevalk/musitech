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
 * File ChordSymbolDisplay.java Created on 03.07.2003
 */
package de.uos.fmt.musitech.structure.harmony.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.score.gui.MusicGlyph;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * This is a Display for a ChordSymbol.
 * TODO: sizes adjustable, top and bottom close to main symbol 
 * TODO: which fields should have getters?
 * @author tweyde & FX
 * @version $Revision: 8054 $, $Date: 2012-02-20 04:01:21 +0100 (Mon, 20 Feb 2012) $
 */
public class ChordSymbolDisplay extends JPanel implements Display {

	//	private ChordDisplayText root = new ChordDisplayText();
	//	private ChordDisplayText accidental = new ChordDisplayText();
	//	private ChordDisplayText top = new ChordDisplayText();
	//	private ChordDisplayText base = new ChordDisplayText();
	//	private ChordDisplayText extensions = new ChordDisplayText();
	//	private ChordDisplayText mode = new ChordDisplayText();
	//	private ChordDisplayText comment = new ChordDisplayText();

	/**
	 * ChordSymbol to be displayed.
	 * Might be the <code>editObj</code> or the <code>propertyValue</code>.
	 * This field is used for easier access to the displayed ChordSymbol.  
	 */
	private ChordSymbol cs;

	// -------- constructor --------------------------------

	/**
	 * Empty constructor. 
	 * <br>
	 * In general, a ChordSymbolDisplay should be generated using the EditorFactory.
	 */
	public ChordSymbolDisplay() {
	}

	// ------------ field and methods for the GUI -------------------------------

	//Constants and default values used in the GUI

	public static final int FONT_STYLE = Font.PLAIN;

	public static final String FONT_NAME = "Helvetica";

	double scale = 1.0f;

	private Insets acc_insets, top_insets, base_insets, mode_comment_insets;

	//	private Insets mode_comment_insets = new Insets(0, -16, 0, 0);
	//
	//	private Insets base_insets = new Insets(-12, 0, 0, 0);
	//
	//	private Insets top_insets =
	////		new Insets(0, 0, root.getFont().getBaselineFor('A') - 12, 0);
	//		new Insets(0, 0, rootField.getFont().getBaselineFor('A') - 12, 0);
	//
	//	private Insets acc_insets = new Insets(0, 0, 0, 4);

	//JComponents used in the GUI

	JPanel mode_comment_panel = new JPanel();
	
	JTextField rootField,
		accidentalField,
		extensionsField,
		topField,
		baseField,
		modeField,
		commentField;
//	Collection textfields = new ArrayList();

	/**
	 * Background color to be set when the displayed ChordSymbol is selected.
	 */
	final static Color SELECTION_COLOR = Color.BLUE;

	/**
	 * Sets the GridBagConstraints for the JPanels used in the GUI
	 * and adds the JPanels to this ChordSymbolDisplay. (The JPanels are nested.)
	 */
	private void setLabels() {
		if (rootField == null || accidentalField == null || topField == null 
				|| baseField == null || extensionsField == null	|| commentField == null) 
			setTextSymbols();
		if (top_insets == null || base_insets == null || mode_comment_insets == null
			|| acc_insets == null)
			setInsets();
		//		setScale(2.0f);
		JPanel root_acc_Panel = new JPanel();
		root_acc_Panel.setOpaque(false);
		//		root_acc_Panel.add(root);
		//		root_acc_Panel.add(accidental);
		root_acc_Panel.add(rootField);
		root_acc_Panel.add(accidentalField);
		add( 	// root,
			rootField,
			new GridBagConstraints(	0, 1, 1, 2,	0, 
				// 1,
				0, GridBagConstraints.SOUTH, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0),	1, 	1));
		add(	//			accidental,
			accidentalField,
			new GridBagConstraints( 1, 1, 1, 2, 0, 
				//				.25,
				0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, acc_insets,
				1, 1));
		add( //	extensions,
			extensionsField,
			new GridBagConstraints( 2, 1, 1, 1, 1,
//				0,
				0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0),	1, 1)
			);
		add( //	top,
			topField, 
			new GridBagConstraints( 0, 0, 1, 1, 0, 0,
				GridBagConstraints.SOUTH, GridBagConstraints.NONE,
				top_insets,	1, 1),
			1);
		System.out.println("root.getFont().getBaselineFor('g') = "
		//				+ root.getFont().getBaselineFor('g'));
			+rootField.getFont().getBaselineFor('g'));
		add( //	base,
			baseField,
			new GridBagConstraints( 0, 3, 1, 1, 0, 1,
				GridBagConstraints.NORTH, GridBagConstraints.NONE,
				base_insets, 1, 1),
			2);
		mode_comment_panel.setOpaque(false);
		//mode_comment_panel.setLayout(new BorderLayout());
		//		mode_comment_panel.add(mode, BorderLayout.WEST);
		mode_comment_panel.add(modeField, BorderLayout.WEST);
		//		mode_comment_panel.add(comment, BorderLayout.CENTER);
		mode_comment_panel.add(commentField, BorderLayout.CENTER);
		add(
			mode_comment_panel,
			new GridBagConstraints( 2, 2, 1, 1, 1,
//				0,
				0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				mode_comment_insets,
//				new Insets(0,0,0,0),
				1, 1));
	}

	//	private void init(ChordSymbol cs) {
	//		accidental.setText("" + MusicGlyph.FLAT); //FX
	//		root.setText("" + cs.getRoot());
	//		top.setText("" + cs.getTop());
	//		base.setText("" + cs.getBase());
	//		extensions.setText(cs.getExtensions());
	//		mode.setText("sus"); //FX
	//		comment.setText(" " + "Comment");
	//	}

	/**
	 * Creates the JPanels used in the GUI.
	 */
	private void setTextSymbols() {
		if (cs == null)
			return;
//		textfields.clear();
		rootField = createChordSymbolTextfield("" + cs.getRoot());
//		textfields.add(rootField);
		accidentalField = createChordSymbolTextfield(createAccidentalSymbol());
//		textfields.add(accidentalField);
		topField = createChordSymbolTextfield(createTopSymbol());
//		textfields.add(topField);
		baseField = createChordSymbolTextfield(createBaseSymbol());
//		textfields.add(baseField);
		extensionsField = createChordSymbolTextfield(cs.getExtensions());
//		textfields.add(extensionsField);
		modeField = createChordSymbolTextfield(createModeSymbol());
//		textfields.add(modeField);
		String comment = ""; //TODO woher?
		commentField = createChordSymbolTextfield(comment);
//		textfields.add(commentField);
	}

	/**
	 * Returns a String representing the accidental to be displayed 
	 * with the chord symbol.
	 * Determines the String from the int <code>rootAccidental</code> 
	 * of the <code>cs</code> to be displayed.
	 * 
	 * @return String to be displayed in the <code>accidentalField</code>
	 */
	private String createAccidentalSymbol() {
		String accidental = "";
		int acc = cs.getRootAccidental();
		if (acc < 0) {
			for (int i = 1; i <= acc * (-1); i++) {
				accidental = accidental.concat(MusicGlyph.FLAT + "");
			}
		}
		if (acc > 0)
			for (int i = 1; i <= acc; i++) {
				accidental = accidental.concat(MusicGlyph.SHARP + "");
			}
		return accidental;
	}

	/**
	 * Returns a String representing the top to be displayed
	 * with the chord symbol.
	 * If the int value <code>top</code> of the <code>cs</code> to be displayed is 0,
	 * an empty String is returned, if the int value is 1 "8" is returned. Else
	 * takes the String representation of the <code>top</code>.
	 * 
	 * @return String to be displayed in the <code>topField</code>
	 */
	private String createTopSymbol() {
		String top = "";
		int topInt = cs.getTop();
		//TODO weitere Fälle bearbeiten?
		switch (topInt) {
			case 0 :
				break;
			case 1 :
				top = "8";
				break;
			default :
				top = new Integer(topInt).toString();
				break;
		}
		return top;
	}

	/**
	 * Returns a String representing the base to be displayed with the chord symbol.
	 * Takes the String representation ot the int value <code>base</code> of the
	 * <code>cs</code> to be displayed.
	 * Base values are given within an octave. I.e. in case of an value of 10, the 
	 * third is taken. If the base is the fundamental of the chord, an empty String
	 * is returned.
	 * 
	 * @return String to be displayed in the <code>baseField</code>
	 */
	private String createBaseSymbol() {
		String base = "";
		int baseInt = cs.getBase();
		if (baseInt > 7) {
			baseInt = baseInt % 7;
		}
		if (baseInt > 1)
			base = new Integer(baseInt).toString();
		return base;
	}

	/**
	 * Returns a String to be displayed as the mode with this chord symbol.
	 * Determines the String based on the <code>mode</code> value of the
	 * <code>cs</code> to be displayed.
	 * 
	 * @return String to be displayed in the <code>modeField</code>
	 */
	private String createModeSymbol() {
		String mode = "";
		Mode modeInt = cs.getMode();
		switch (modeInt) {
			case MODE_MAJOR :
				mode = "maj";
				break;
			case MODE_MINOR :
				mode = "min";
				break;
			case MODE_DORIAN :
				mode = "dor";
				break;
			case MODE_PHRYGIAN :
				mode = "phryg";
				break;
			case MODE_LYDIAN :
				mode = "lyd";
				break;
			case MODE_MIXOLYDIAN :
				mode = "mixol";
				break;
			case MODE_AEOLIAN :
				mode = "ael";
				break;
			case MODE_LOCRIAN :
				mode = "loc";
				break;
			case MODE_SUS :
				mode = "sus";
				break;
			default :
				break;
		}
		return mode;
	}

	/**
	 * Sets the Insets used in the GUI to fixed values.
	 * 
	 * TODO flexible Werte?
	 */
	private void setInsets() {
		if (rootField == null)
			setTextSymbols();
		//		acc_insets = new Insets(0, 0, 0, 4);
		int accRight = new Double(2 * scale).intValue();
		acc_insets = new Insets(0, 0, 0, accRight);
		//		top_insets = new Insets(0, 0, root.getFont().getBaselineFor('A') - 12, 0);
		//		top_insets =
		//			new Insets(0, 0, rootField.getFont().getBaselineFor('A') - 12, 0);	
		//		base_insets = new Insets(-12, 0, 0, 0);
		int baseTop = new Double(-6 * scale).intValue();
		base_insets = new Insets(baseTop, 0, 0, 0);
		//		mode_comment_insets = new Insets(0, -16, 0, 0);
		int descentRoot =
			rootField.getFontMetrics(rootField.getFont()).getDescent();
		//*rootField.getFont().getSize();
		int descentMode =
			modeField.getFontMetrics(modeField.getFont()).getDescent();
		//*modeField.getFont().getSize();
		int mode_comment_Left = new Double(-8 * scale).intValue();
		int mode_comment_Bottom = descentRoot-descentMode;
		mode_comment_insets =
			new Insets(0, mode_comment_Left, mode_comment_Bottom, 0);
		//TODO wird nicht richtig mit skaliert
		mode_comment_insets.top = (int) (6.0 * (1.0 / scale));
		int topBottom = new Double(-32 * scale).intValue();
		top_insets = new Insets(0, 0, topBottom, 0);
	}

	/**
	 * Sets the FONT sizes for the JPanels used in the GUI according to the
	 * specified scaling factor.
	 * 
	 * @param scale double indicating the scaling factor
	 */
	public void setScale(double scale) {
		System.out.println("Scale factor: " + scale);
		int small = (int) (18 * scale);
		int main = (int) (48 * scale);
		//		int acc = (int)(28*scale);
		int mode_comment_size = (int) (26 * scale);
		int exts = (int) (18 * scale);
		//		if (mode_comment_insets!=null)
		//			mode_comment_insets.top = (int) (6.0 * (1.0 / scale));
		//(int)(-150*scale)+18;
		//		root.setFont(new Font(ChordDisplayText.FONT_NAME, Font.PLAIN, main));
		rootField.setFont(new Font(FONT_NAME, Font.PLAIN, main));
		Font musicFont = MusicGlyph.createFont((int) (10 * scale));
		musicFont =
			musicFont.deriveFont(new AffineTransform(1.5, 0, 0, 1, 0, 0));
		//		accidental.setFont(musicFont);
		accidentalField.setFont(musicFont);
		//		top.setFont(
		topField.setFont(new Font(
		//				ChordDisplayText.FONT_NAME,
		//				ChordDisplayText.FONT_STYLE,
		FONT_NAME, FONT_STYLE, small));
		//		base.setFont(
		baseField.setFont(new Font(
		//				ChordDisplayText.FONT_NAME,
		//				ChordDisplayText.FONT_STYLE,
		FONT_NAME, FONT_STYLE, small));
		//		extensions.setFont(
		extensionsField.setFont(new Font(
		//				ChordDisplayText.FONT_NAME,
		//				ChordDisplayText.FONT_STYLE,
		FONT_NAME, FONT_STYLE, exts));
		//		mode.setFont(
		modeField.setFont(new Font(
		//				ChordDisplayText.FONT_NAME,
		FONT_NAME, Font.BOLD, mode_comment_size));
		//		comment.setFont(
		commentField.setFont(new Font(
		//				ChordDisplayText.FONT_NAME,
		FONT_NAME, Font.ITALIC, mode_comment_size));
		//Modif
		setInsets();
		doLayout();
		mode_comment_panel.doLayout();
	}

	/**
	 * Returns JTextField displaying the specified String. 
	 * The JTextField has some special layout settings.
	 * 
	 * @param text String to be displayed in a JTextField
	 * @return JTextField displaying the specified String
	 */
	private JTextField createChordSymbolTextfield(String text) {
		JTextField textfield = new JTextField();
		textfield.setText(text);
		textfield.setBorder(null);
		textfield.setEditable(false);
		textfield.setHighlighter(null);
		textfield.setOpaque(false);
		//Modif
		textfield.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				focusReceived();
			}
		});
		//end
		return textfield;
	}

	/**
	 * Creates the graphical user interface.
	 */
	public void createGUI() {
//		addFocusListener(new FocusAdapter() {
//			public void focusGained(FocusEvent e) {
//				focusReceived();
//			}
//		});
//		GridBagLayout gridbag = new GridBagLayout();
//		setLayout(gridbag);
//		setOpaque(true);
//		setBackground(Color.WHITE);
//		//just for testing:
//		//		setBorder(BorderFactory.createLineBorder(Color.RED));
//		//		setLabels();
//		//		init(cs);	
//		setTextSymbols();
//		setScale(2.0f);
//		//		setInsets();
//		setLabels();
//		doLayout();
		initGUI();
		createGUI2();
	}

	//TODO main-Methode später streichen

	//protected abstract void setMode(int mode);
	public static void main(String args[]) {
		ChordSymbol cs = new ChordSymbol(null, 0);
		cs.setRoot('A');
		cs.setRootAccidental(0); // int- Konstanten fehlen noch
		cs.setMode(Mode.MODE_MINOR);
		cs.setBase(5);
		cs.setTop(3);
		cs.setExtensions("7(#5)");
		//		final ChordSymbolDisplay cd = new ChordSymbolDisplay(cs);
		ChordSymbolDisplay display = null;
		try {
			display =
				(ChordSymbolDisplay) EditorFactory.createDisplay(cs, null);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
		}
		final ChordSymbolDisplay cd = display;
		final JFrame frame = new JFrame("Chord symbol test");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(cd);
		JSlider slider = new JSlider(JSlider.VERTICAL, 1, 100, 50);
		cd.setScale((slider.getValue() * 2.0 / 100.0));
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				cd.setScale(
					(float) (((JSlider) arg0.getSource()).getValue()
						* 2.0
						/ 100.0));
			}
		});
		frame.getContentPane().add(slider, BorderLayout.EAST);
		frame.setBackground(Color.WHITE);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	// ----------- methods implementing interface Display ------------------------

	/**
	 * Flag of this DataChangeListener.
	 * Set true in method <code>dataChanged</code>, if data has been changed in 
	 * an external display.
	 */
	boolean dataChanged = false;

	/**
	 * The object displayed by this ChordSymbolDisplay.
	 * Is the ChordSymbol itself or has a ChordSymbol property.
	 */
	private Object editObj;

	/**
	 * String indicating the property of <code>editObj</code> to be displayed.
	 * If the <code>editObj</code> itself is the ChordSymbol to be displayed,
	 * <code>propertyName</code> is null.
	 */
	private String propertyName;

	/**
	 * The ChordSymbol property of the <code>editObj</code> indicated by the
	 * <code>propertyName</code>, or null if the <code>editObj</code> itself
	 * is the ChordSymbol.
	 * <br>
	 * The <code>propertyValue</code> of the display is set via ReflectionAccess
	 * if the <code>propertyName</code> is not null and the <code>editObj</code>
	 * has a property of this name.
	 */
	private Object propertyValue;

	/**
	 * EditingProfile of this Display.
	 */
	EditingProfile profile;

	/**
	 * Root display of this Display.
	 */
	Display rootDisplay;

	/** 
	 * Getter for <code>dataChanged</code>.
	 * Returns true, if data has been changed in an external display.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
	 */
	public boolean externalChanges() {
		return dataChanged;
	}

	/** 
	 * Removes this DataChangeListener from the tabel of the DataChangeManager.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
	 */
	public void destroy() {
		DataChangeManager.getInstance().removeListener(this);
	}

	/** 
	 * Updates the Display if there are external changes to its data.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
	 */
	public void focusReceived() {
		if (externalChanges())
			updateDisplay();
		if (rootDisplay != this)
			rootDisplay.focusReceived();
	}

	/** 
	 * Getter for <code>profile</code>.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#getEditingProfile()
	 */
	public EditingProfile getEditingProfile() {
		return profile;
	}

	/** 
	 * Getter for <code>editObj</code>.
	 * The <code>editObj</code> is a ChordSymbol or an object having a ChordSymbol
	 * property.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
	 */
	public Object getEditObj() {
		return editObj;
	}

	/**
	 * Getter for <code>cs</code>. 
	 * Returns the ChordSymbol displayed which is either the <code>editObj</code> or
	 * the <code>propertyValue</code>.
	 * 
	 * @return ChordSymbol this ChordSymbolDisplay shows
	 */
	public ChordSymbol getChordSymbol() {
		return cs;
	}

	/** 
	 * Returns true, if this Display is the focus owner.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
	 */
	public boolean isFocused() {
		return isFocusOwner();
	}

	/** 
	 * Sets the specified arguments as <code>editObj</code>, <code>profile</code> and
	 * <code>rootDisplay</code> of this Display.
	 * Sets <code>propertyName</code> and <code>propertyValue</code>, if the
	 * <code>profile</code> specifies a property name. Also sets the <code>cs</code>
	 * to either the <code>editObj</code> or <code>propertyValue</code> depending
	 * on which one is the ChordSymbol to be displayed.
	 * Registers this Display as a DataChangeManager at the DataChangeManager. 
	 * Creates the GUI.
	 * <br>
	 * The argument <code>editObject</code> must not be null.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object, de.uos.fmt.musitech.framework.editor.EditingProfile, de.uos.fmt.musitech.framework.editor.Display)
	 */
	public void init(Object editObject, EditingProfile profile, Display root) {
		//set parameters of display
		//		if (editObject instanceof ChordSymbol)
		//			cs = (ChordSymbol) editObject;
		if (editObject != null)
			this.editObj = editObject;
		//		if (cs == null) {
		//		if (editObj == null) {
		else {
			//if this Display is created using the EditorFactory, this does not occur
			System.err.println(
				"In ChordSymbolDisplay, method init(Object, EditingProfile, Display):\n"
					+ "The Object argument must not be null.");
			return;
		}
		this.profile = profile;
		if (profile != null) {
			this.propertyName = profile.getPropertyName();
			setPropertyValue();
		}
		this.rootDisplay = root;
		setChordSymbol();
		//register at DataChangeManager
		Collection<ChordSymbol> objs = new ArrayList<ChordSymbol>();
		objs.add(cs);
		DataChangeManager.getInstance().interestExpandElements(this, objs);
		//create layout
		createGUI();
	}

	/**
	 * Sets the <code>propertyValue</code> of this ChordSymbolDisplay by getting
	 * the property corresponding to the <code>propertyName</code> from <code>editObj</code>
	 * via ReflectionAccess.
	 */
	private void setPropertyValue() {
		ReflectionAccess ref =
			ReflectionAccess.accessForClass(editObj.getClass());
		if (propertyName != null && ref.hasPropertyName(propertyName)) {
			propertyValue = ref.getProperty(editObj, propertyName);
		}
	}

	/**
	 * Sets <code>cs</code>, i.e. the ChordSymbol to be displayed.
	 * This may be either the <code>editObj</code> or the <code>propertyValue</code>.
	 * <br>
	 * With <code>cs</code>, the displayed ChordSymbol can be accessed directly
	 * without checking <code>propertyName<code>, <code>propertyValue</code> resp.
	 * <code>editObj</code> each time you want to have the ChordSymbol. 
	 */
	private void setChordSymbol() {
		if (editObj instanceof ChordSymbol)
			cs = (ChordSymbol) editObj;
		else if (propertyValue instanceof ChordSymbol)
			cs = (ChordSymbol) propertyValue;
	}

	/** 
	 * Updates the display if there are external changes to the data of this Display
	 * by rebuilding the GUI.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
	 */
	//	public void updateDisplay() {
	//		if (externalChanges()) {
	//			removeAll();
	//			createGUI();
	//		}
	//	}

	public void updateDisplay() {
		if (externalChanges()) {
//			removeAll();
//			mode_comment_panel.removeAll();
//			setTextSymbols();
//			setScale(2.0f);
//			setLabels();
//			doLayout();
			updateGUI();
//			updateGUI2();
		}
	}

	/** 
	 * Getter for <code>rootDisplay</code>.
	 * Returns the root display of this Display.
	 * 
	 * @see de.uos.fmt.musitech.framework.editor.Display#getRootDisplay()
	 */
	public Display getRootDisplay() {
		return rootDisplay;
	}

	/** 
	 * Sets the flag <code>dataChanged</code> true.
	 * This method is called when this DataChangeListener gets a DataChangeEvent.
	 * <br>
	 * As this Display is no Editor, data changes must have been performed externally.
	 * 
	 * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
	 */
	public void dataChanged(DataChangeEvent e) {
		dataChanged = true;
	}

	/**
	 * Marks or unmarks the displayed ChordSymbol as selected object.
	 * If <code>mark</code> is true, the backgroudn color is set to
	 * SELECTION_COLOR, if <code>mark</code> is false, the background is
	 * reset to white.
	 * <br>
	 * This method is invoked by the ChordSymbSeqDisplay which is a SelectionListener.
	 * 
	 * @param mark boolean inidicating if to mark or to unmark the ChordSymbol
	 */
	public void markSelected(boolean mark) {
		if (mark) {
			setBackground(SELECTION_COLOR);
		} else
			setBackground(Color.WHITE);
	}
	
	
	// GUI under construction ;-)

	private void initGUI() {
		addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				focusReceived();
			}
		});
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		setOpaque(true);
		setBackground(Color.WHITE);
	}

	public void createGUI2() {
		mode_comment_panel.removeAll();	//TODO für Test
		initGUI();
		createTextfields();
		setFonts();
		setInsets2();
		addTextfields();
	}

	public void updateGUI() {
//		removeAll();
		updateTextfields();
		revalidate();
	}
	
	public void updateGUI2(){
		removeAll();
		mode_comment_panel.removeAll();
//		textfields.clear();
		createGUI();
	}

	private void createTextfields() {
		setTextSymbols(); //TODO später zusammenfassen (evtl. umbenennen)
	}

	private void setFonts() {
		int small = (int) (18 * scale);
		int main = (int) (48 * scale);
		int mode_comment_size = (int) (26 * scale);
		int exts = (int) (18 * scale);
		rootField.setFont(new Font(FONT_NAME, Font.PLAIN, main));
		Font musicFont = MusicGlyph.createFont((int) (10 * scale));
		musicFont =
			musicFont.deriveFont(new AffineTransform(1.5, 0, 0, 1, 0, 0));
		accidentalField.setFont(musicFont);
		topField.setFont(new Font(FONT_NAME, FONT_STYLE, small));
		baseField.setFont(new Font(FONT_NAME, FONT_STYLE, small));
		extensionsField.setFont(new Font(FONT_NAME, FONT_STYLE, exts));
		modeField.setFont(new Font(FONT_NAME, Font.BOLD, mode_comment_size));
		commentField.setFont(
			new Font(FONT_NAME, Font.ITALIC, mode_comment_size));
	}

	private void setInsets2() {
		int accRight = new Double(2 * scale).intValue();
		acc_insets = new Insets(0, 0, 0, accRight);
		int baseTop = new Double(-6 * scale).intValue();
		base_insets = new Insets(baseTop, 0, 0, 0);
		int descentRoot =
			rootField.getFontMetrics(rootField.getFont()).getDescent();
		int descentMode =
			modeField.getFontMetrics(modeField.getFont()).getDescent();
		int mode_comment_Left = new Double(-8 * scale).intValue();
		mode_comment_insets =
			new Insets(0, mode_comment_Left, (descentRoot - descentMode), 0);
		//TODO wird nicht richtig mit skaliert
		mode_comment_insets.top = (int) (6.0 * (1.0 / scale));
		int topBottom = new Double(-24 * scale).intValue();
		top_insets = new Insets(0, 0, topBottom, 0);
		
		//Test:
//		accidentalField.setPreferredSize(new JTextField(MusicGlyph.SHARP+""+MusicGlyph.SHARP+""+MusicGlyph.SHARP+"").getPreferredSize());
		
	}

	private void addTextfields() {
		setLabels(); //TODO zusammenfassen
	}
	
	private void updateTextfields(){
		rootField.setText("" + cs.getRoot());
		rootField.revalidate();
		accidentalField.setText(createAccidentalSymbol());
		topField.setText(createTopSymbol());
		baseField.setText(createBaseSymbol());
		extensionsField.setText(cs.getExtensions());
		modeField.setText(createModeSymbol());
		//TODO commentField
	}

	public void setScale2(double scale) {
		this.scale = scale;
		updateGUI();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.editor.Display#asComponent()
	 */
	public Component asComponent() {
		return null;
	}
}