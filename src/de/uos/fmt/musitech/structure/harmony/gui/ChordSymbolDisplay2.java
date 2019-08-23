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
 * Created on 09.07.2004
 *
 */
package de.uos.fmt.musitech.structure.harmony.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import de.uos.fmt.musitech.data.structure.harmony.*;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker.Mode;
import de.uos.fmt.musitech.framework.change.*;
import de.uos.fmt.musitech.framework.editor.*;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.score.gui.MusicGlyph;
import de.uos.fmt.musitech.utility.gui.scaling.ScaledComponent;
import de.uos.fmt.musitech.utility.obj.*;

/**
 * At the moment, this class is for experimenting with the GUI only.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class ChordSymbolDisplay2 extends JPanel implements Display {

    boolean germanModeDisplay = false;

    /**
     * ChordSymbol to be displayed. Might be the <code>editObj</code> or the
     * <code>propertyValue</code>. This field is used for easier access to the
     * displayed ChordSymbol.
     */
    protected ChordSymbol cs;

    // -------- constructor --------------------------------

    /**
     * Empty constructor. <br>
     * In general, a ChordSymbolDisplay should be generated using the EditorFactory.
     */
    public ChordSymbolDisplay2() {
    }

    // ------------ field and methods for the GUI
    // -------------------------------

    //Constants and default values used in the GUI

    public static final int FONT_STYLE = Font.PLAIN;

    public static final String FONT_NAME = "Helvetica";

    protected double scale = 1.0f;

    JTextField rootField, accidentalField, extensionsField, topField, baseField,
            modeField, commentField;

    //		Collection textfields = new ArrayList();

    /**
     * Background color to be set when the displayed ChordSymbol is selected.
     */
    final static Color SELECTION_COLOR = Color.BLUE;

    final static Color CURRENT_COLOR = Color.RED;

    private String createRootSymbol() {

        String root = cs.getRoot() + "";
        if (germanModeDisplay) {
            if (cs.getMode() == Mode.MODE_MAJOR) {
                root = root.toUpperCase();
            }
            if (cs.getMode() == Mode.MODE_MINOR) {
                root = root.toLowerCase();
            }
        }
        return root;
    }

    /**
     * Returns a String representing the accidental to be displayed with the chord symbol.
     * Determines the String from the int <code>rootAccidental</code> of the
     * <code>cs</code> to be displayed.
     * 
     * @return String to be displayed in the <code>accidentalField</code>
     */
    private String createAccidentalSymbol() {
        String accidental = "";
        int acc = cs.getRootAlteration();
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
     * Returns a String representing the top to be displayed with the chord symbol. 
     * If the int value <code>top</code> of the <code>cs</code> to be displayed is 0, an
     * empty String is returned, if the int value is 1 "8" is returned. 
     * Else takes the String representation of the <code>top</code>.
     * 
     * @return String to be displayed in the <code>topField</code>
     */
    private String createTopSymbol() {
        String top = "";
        int topInt = cs.getTop();
        //TODO weitere Fälle bearbeiten?
        switch (topInt) {
        case 0:
            break;
        case 1:
            top = "8";
            break;
        default:
            top = new Integer(topInt).toString();
            break;
        }
        return top;
    }

    /**
     * Returns a String representing the base to be displayed with the chord symbol. Takes
     * the String representation ot the int value <code>base</code> of the
     * <code>cs</code> to be displayed. Base values are given within an octave. I.e. in
     * case of an value of 10, the third is taken. If the base is the fundamental of the
     * chord, an empty String is returned.
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
     * Returns a String to be displayed as the mode with this chord symbol. Determines the
     * String based on the <code>mode</code> value of the <code>cs</code> to be
     * displayed.
     * 
     * @return String to be displayed in the <code>modeField</code>
     */
    private String createModeSymbol() {
        String mode = "";
        Mode modeInt = cs.getMode();
        switch (modeInt) {
        case MODE_MAJOR:
            //				mode = "maj";
            mode = "";
            break;
        case MODE_MINOR:
            //				mode = "min";
            mode = "m";
            break;
        case MODE_DORIAN:
            mode = "dor";
            break;
        case MODE_PHRYGIAN:
            mode = "phryg";
            break;
        case MODE_LYDIAN:
            mode = "lyd";
            break;
        case MODE_MIXOLYDIAN:
            mode = "mixol";
            break;
        case MODE_AEOLIAN:
            mode = "ael";
            break;
        case MODE_LOCRIAN:
            mode = "loc";
            break;
        case MODE_SUS:
            mode = "sus";
            break;
        default:
            break;
        }
        return mode;
    }

    /**
     * Returns JTextField displaying the specified String. The JTextField has some special
     * layout settings.
     * 
     * @param text String to be displayed in a JTextField
     * @return JTextField displaying the specified String
     */
    protected JTextField createChordSymbolTextfield(String text) {
        JTextField textfield = new JTextField();
        if (text != null)
            textfield.setText(text);
        textfield.setBorder(null);
        textfield.setEditable(false);
        textfield.setHighlighter(null);
        textfield.setOpaque(false);
        //Modif
        textfield.addFocusListener(new FocusAdapter() {

            @Override
			public void focusGained(FocusEvent e) {
                focusReceived();
            }
        });
        //end
        return textfield;
    }

    // ----------- methods implementing interface Display
    // ------------------------

    /**
     * Flag of this DataChangeListener. Set true in method <code>dataChanged</code>, if
     * data has been changed in an external display.
     */
    boolean dataChanged = false;

    /**
     * The object displayed by this ChordSymbolDisplay. Is the ChordSymbol itself or has a
     * ChordSymbol property.
     */
    private Object editObj;

    /**
     * String indicating the property of <code>editObj</code> to be displayed. If the
     * <code>editObj</code> itself is the ChordSymbol to be displayed,
     * <code>propertyName</code> is null.
     */
    private String propertyName;

    /**
     * The ChordSymbol property of the <code>editObj</code> indicated by the
     * <code>propertyName</code>, or null if the <code>editObj</code> itself is the
     * ChordSymbol. <br>
     * The <code>propertyValue</code> of the display is set via ReflectionAccess if the
     * <code>propertyName</code> is not null and the <code>editObj</code> has a
     * property of this name.
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
     * Getter for <code>dataChanged</code>. Returns true, if data has been changed in
     * an external display.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
     */
    @Override
	public boolean externalChanges() {
        return dataChanged;
    }

    /**
     * Removes this DataChangeListener from the tabel of the DataChangeManager.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#destroy()
     */
    @Override
	public void destroy() {
        DataChangeManager.getInstance().removeListener(this);
    }

    /**
     * Updates the Display if there are external changes to its data.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
     */
    @Override
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
    @Override
	public EditingProfile getEditingProfile() {
        return profile;
    }

    /**
     * Getter for <code>editObj</code>. The <code>editObj</code> is a ChordSymbol or
     * an object having a ChordSymbol property.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
     */
    @Override
	public Object getEditObj() {
        return editObj;
    }

    /**
     * Getter for <code>cs</code>. Returns the ChordSymbol displayed which is either
     * the <code>editObj</code> or the <code>propertyValue</code>.
     * 
     * @return ChordSymbol this ChordSymbolDisplay shows
     */
    public ChordSymbol getChordSymbol() {
        return cs;
    }

    /**
     * Returns the <code>propertyValue</code>.
     * 
     * @return Object the <code>propertyValue</code>
     */
    protected Object returnPropertyValue() {
        return propertyValue;
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
     * Sets the specified arguments as <code>editObj</code>,<code>profile</code> and
     * <code>rootDisplay</code> of this Display. Sets <code>propertyName</code> and
     * <code>propertyValue</code>, if the <code>profile</code> specifies a property
     * name. Also sets the <code>cs</code> to either the <code>editObj</code> or
     * <code>propertyValue</code> depending on which one is the ChordSymbol to be
     * displayed. Registers this Display as a DataChangeManager at the DataChangeManager.
     * Creates the GUI. <br>
     * The argument <code>editObject</code> must not be null.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Display)
     */
    @Override
	public void init(Object editObject, EditingProfile profile, Display root) {
        if (editObject != null)
            this.editObj = editObject;
        else {
            //if this Display is created using the EditorFactory, this does not
            // occur
            System.err
                    .println("In ChordSymbolDisplay, method init(Object, EditingProfile, Display):\n"
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
     * Sets the <code>propertyValue</code> of this ChordSymbolDisplay by getting the
     * property corresponding to the <code>propertyName</code> from <code>editObj</code>
     * via ReflectionAccess.
     */
    private void setPropertyValue() {
        ReflectionAccess ref = ReflectionAccess.accessForClass(editObj.getClass());
        if (propertyName != null && ref.hasPropertyName(propertyName)) {
            propertyValue = ref.getProperty(editObj, propertyName);
        }
    }

    /**
     * Sets <code>cs</code>, i.e. the ChordSymbol to be displayed. This may be either
     * the <code>editObj</code> or the <code>propertyValue</code>.<br>
     * With <code>cs</code>, the displayed ChordSymbol can be accessed directly without
     * checking <code>propertyName<code>, <code>propertyValue</code> resp.
     * <code>editObj</code> each time you want to have the ChordSymbol.
     */
    protected void setChordSymbol() {
        if (editObj instanceof ChordSymbol)
            cs = (ChordSymbol) editObj;
        else if (propertyValue instanceof ChordSymbol)
            cs = (ChordSymbol) propertyValue;
    }

    /**
     * Updates the display if there are external changes to the data of this Display by
     * rebuilding the GUI.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
     */
    @Override
	public void updateDisplay() {
        if (externalChanges()) {
            updateGUI();
        }
    }

    /**
     * Getter for <code>rootDisplay</code>. Returns the root display of this Display.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getRootDisplay()
     */
    @Override
	public Display getRootDisplay() {
        return rootDisplay;
    }

    /**
     * Sets the flag <code>dataChanged</code> true. This method is called when this
     * DataChangeListener gets a DataChangeEvent. <br>
     * As this Display is no Editor, data changes must have been performed externally.
     * 
     * @see de.uos.fmt.musitech.framework.change.DataChangeListener#dataChanged(de.uos.fmt.musitech.framework.change.DataChangeEvent)
     */
    @Override
	public void dataChanged(DataChangeEvent e) {
        dataChanged = true;
    }

    /**
     * Marks or unmarks the displayed ChordSymbol as selected object. If <code>mark</code>
     * is true, the backgroudn color is set to SELECTION_COLOR, if <code>mark</code> is
     * false, the background is reset to white. <br>
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

    /**
     * If <code>mark</code> is true, the background color is changed to CURRENT_COLOR.
     * If <code>mark</code> is false, the background color is reset to white. <br>
     * This method is invoked by ChordSymbolSeqDisplay which is a Timeable. It highlights
     * the chord symbol currently played back with the <code>CURRENT_COLOR</code>.
     * 
     * @param mark
     */
    public void markCurrentlyPlayed(boolean mark) {
        if (mark) {
            setBackground(CURRENT_COLOR);
        } else {
            setBackground(Color.WHITE);
        }
    }

    /**
     * Initializes the graphical user interface. Sets this ChordSymbolDisplay2 as opaque
     * and its background colour to white. A FocusListener is added for this display
     * working as a DataChangeListener.
     *  
     */
    private void initGUI() {
        addFocusListener(new FocusAdapter() {

            @Override
			public void focusGained(FocusEvent e) {
                focusReceived();
            }
        });
        //			GridBagLayout gridbag = new GridBagLayout();
        //			setLayout(gridbag);
        //					setLayout(new BorderLayout());
        //		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    /**
     * Creates the graphical user interface. <br>
     * Creates the JTextFields for the parts of the ChordSymbol, sets the Fonts and adds
     * the JTextFields to this display.
     */
    public void createGUI() {
        initGUI();
        createTextfields();
        setFonts();
        addTextfields();
    }

    /**
     * Updates the GUI by updating the JTextFields, i.e. setting their text to the current
     * values of the ChordSymbol (resp. to their symbolic representation).
     */
    public void updateGUI() {
        updateTextfields();
        revalidate();
    }

    /**
     * Creates the JTextFields displaying the parts of the ChordSymbol in their symbolic
     * representation.
     */
    protected void createTextfields() {
        if (cs == null)
            return;
        rootField = createChordSymbolTextfield(createRootSymbol());
        rootField.setHorizontalAlignment(JTextField.CENTER);
        accidentalField = createChordSymbolTextfield(createAccidentalSymbol());
        accidentalField.setHorizontalAlignment(JTextField.LEFT);
        topField = createChordSymbolTextfield(createTopSymbol());
        topField.setHorizontalAlignment(JTextField.CENTER);
        baseField = createChordSymbolTextfield(createBaseSymbol());
        baseField.setHorizontalAlignment(JTextField.CENTER);
        String extensions = "";
        if (cs.getExtensions() != null)
            extensions = cs.getExtensions();
        extensionsField = createChordSymbolTextfield(extensions);
        extensionsField.setHorizontalAlignment(JTextField.LEFT);
        modeField = createChordSymbolTextfield(createModeSymbol());
        modeField.setHorizontalAlignment(JTextField.LEFT);
        String comment = "";
        if (cs.getComment() != null)
            comment = cs.getComment();
        commentField = createChordSymbolTextfield(comment);
    }

    /**
     * Sets the Fonts of the JTextFields.
     */
    protected void setFonts() {
        int small = (int) (18 * scale);
        int main = (int) (48 * scale);
        int mode_comment_size = (int) (26 * scale);
        int exts = (int) (18 * scale);
        rootField.setFont(new Font(FONT_NAME, Font.PLAIN, main));
        Font musicFont = MusicGlyph.createFont((int) (10 * scale));
        musicFont = musicFont.deriveFont(new AffineTransform(1.5, 0, 0, 1, 0, 0));
        accidentalField.setFont(musicFont);
        topField.setFont(new Font(FONT_NAME, FONT_STYLE, small));
        baseField.setFont(new Font(FONT_NAME, FONT_STYLE, small));
        extensionsField.setFont(new Font(FONT_NAME, FONT_STYLE, exts));
        modeField.setFont(new Font(FONT_NAME, Font.BOLD, mode_comment_size));
        commentField.setFont(new Font(FONT_NAME, Font.ITALIC, mode_comment_size));
    }

    /**
     * Adds the JTextFields to this display. <br>
     * (A nested set of combined Boxes is used.)
     */
    protected void addTextfields() {
        //vertical box for root, top and base
        int topBaseInset = -10 * new Double(scale).intValue();
        Box left = Box.createVerticalBox();
        left.add(Box.createVerticalGlue());
        left.add(topField);
        left.add(Box.createVerticalStrut(topBaseInset)); //top nearer to root
        left.add(rootField);
        left.add(Box.createVerticalStrut(topBaseInset)); //base nearer to root
        left.add(baseField);
        left.add(Box.createVerticalGlue());
        //horizontal box for accidentals and extensions
        Box accExtBox = Box.createHorizontalBox();
        accExtBox.add(accidentalField);
        accExtBox.add(Box.createHorizontalStrut(new Double(scale).intValue() * 5));
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
        right.add(Box.createVerticalStrut(new Double(topField.getPreferredSize()
                .getHeight()
                                                     / 8 * scale).intValue()));
        right.add(accExtBox);
        right.add(Box.createVerticalGlue());
        right.add(modeComBox);
        int baseHeight = new Double(baseField.getPreferredSize().getHeight() * scale)
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
     * Updates the JTextFields by setting their text to the current values of the
     * ChordSymbol resp. to their symbolic representations.
     */
    private void updateTextfields() {
        rootField.setText("" + cs.getRoot());
        rootField.revalidate();
        accidentalField.setText(createAccidentalSymbol());
        topField.setText(createTopSymbol());
        baseField.setText(createBaseSymbol());
        extensionsField.setText(cs.getExtensions());
        modeField.setText(createModeSymbol());
        commentField.setText(cs.getComment());
    }

    /**
     * Sets the <code>scale</code> of this display to the specified double value and
     * rebuilds the GUI.
     * 
     * @param scale double being the scaling factor for this display
     */
    public void setScale(double scale) {
        this.scale = scale;
        //		updateGUI();
        removeAll();
        createTextfields();
        setFonts();
        addTextfields();
        revalidate();
        repaint();
    }

    /**
     * For testing.
     * 
     * @param args
     */
    public static void main(String[] args) {
        //to prevent Exceptions if the entry in the EditorRegistry is not
        // "activated":
        if (!EditorRegistry.isRegisteredEditorClass(ChordSymbolDisplay2.class.getName())) {
            System.out.println("ChordSymbolDisplay2 is not known to the EditorRegistry.");
            return;
        }
        //Test 1
        ChordSymbol symbol = new ChordSymbol();
        symbol.setRootAccidental(1);
        symbol.setBase(3);
        symbol.setTop(5);
        symbol.setExtensions("7 9");
        ChordSymbolDisplay2 display = null;
        try {
            display = (ChordSymbolDisplay2) EditorFactory.createDisplay(symbol, null);
            JFrame frame = new JFrame("Test GUI");
            frame.getContentPane().add(display);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //Test 2
        ChordSymbol symbol2 = new ChordSymbol();
        symbol2.setRoot('E');
        symbol2.setRootAccidental(-2);
        symbol2.setBase(5);
        symbol2.setTop(1);
        symbol2.setMode(Mode.MODE_MINOR);
        symbol2.setExtensions("7");
        symbol.setExtensions("7 9");
        ChordSymbolDisplay2 display2 = null;
        try {
            display2 = (ChordSymbolDisplay2) EditorFactory.createDisplay(symbol2, null);
            JFrame frame2 = new JFrame("Test GUI 2");
            frame2.getContentPane().add(display2);
            frame2.pack();
            frame2.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //Test 3 (with Slider)
        ChordSymbol symbol3 = ObjectCopy.copyObject(symbol2);
        try {
            final ChordSymbolDisplay2 display3 = (ChordSymbolDisplay2) EditorFactory
                    .createDisplay(symbol3, null);
            JFrame frame = new JFrame("Test scaling");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(display3);
            JSlider slider = new JSlider(JSlider.VERTICAL, 1, 100, 50);
            display3.setScale((slider.getValue() * 2.0 / 100.0));
            slider.addChangeListener(new ChangeListener() {

                @Override
				public void stateChanged(ChangeEvent arg0) {
                    display3
                            .setScale((float) (((JSlider) arg0.getSource()).getValue() * 2.0 / 100.0));
                }
            });
            frame.getContentPane().add(slider, BorderLayout.EAST);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //Test 4 (in a ChordSymbSeqDisplay)
        ChordSymbolSequence editObj = new ChordSymbolSequence();
        editObj.add(new ChordSymbol(null, 0));
        editObj.add(new ChordSymbol(null, 100));
        editObj.add(new ChordSymbol(null, 250));
        Display seqDisplay = null;
        try {
            seqDisplay = EditorFactory.createDisplay(editObj, null);
            JFrame frame = new JFrame("ChordSymbseqDisplay");
            frame.getContentPane().add((Component) seqDisplay);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //Test 5 (with ScaledComponent decorator)
        ChordSymbol symbol5 = new ChordSymbol();
        symbol5.setRootAccidental(1);
        symbol5.setBase(3);
        symbol5.setTop(5);
        symbol5.setExtensions("7 9");
        ChordSymbolDisplay2 csDisplay = null;
        try {
            csDisplay = (ChordSymbolDisplay2) EditorFactory.createDisplay(symbol5, null);
            final ScaledComponent decorator = new ScaledComponent(csDisplay);
            JSlider slider = new JSlider(JSlider.VERTICAL, 1, 100, 50);
            slider.addChangeListener(new ChangeListener() {

                @Override
				public void stateChanged(ChangeEvent arg0) {
                    decorator
                            .setZoom((float) (((JSlider) arg0.getSource()).getValue() * 2.0 / 100.0));
                }
            });
            decorator.setZoom(slider.getValue() * 2.0 / 100.0);
            JFrame frame = new JFrame("Test in scaled component");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(decorator);
            frame.getContentPane().add(slider, BorderLayout.EAST);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //Test 6 (with comment)
        ChordSymbol symbol6 = new ChordSymbol();
        symbol6.setRootAccidental(1);
        symbol6.setBase(3);
        symbol6.setExtensions("7 9");
        symbol6.setComment("without 3");
        ChordSymbolDisplay2 display6 = null;
        try {
            display6 = (ChordSymbolDisplay2) EditorFactory.createDisplay(symbol6, null);
            JFrame frame = new JFrame("Test with comment");
            frame.getContentPane().add(display6);
            frame.pack();
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
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