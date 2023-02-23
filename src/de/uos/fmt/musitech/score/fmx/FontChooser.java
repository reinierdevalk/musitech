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
package de.uos.fmt.musitech.score.fmx;

/*
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//import ItemChooser;

/**
 * This is a JDialog subclass that allows the user to select a font, in any style and
 * size, from the list of available fonts on the system. The dialog is modal. Display it
 * with show(); this method does not return until the user dismisses the dialog. When
 * show() returns, call getSelectedFont() to obtain the user's selection. If the user
 * clicked the dialog's "Cancel" button, getSelectedFont() will return null.
 */
public class FontChooser extends JDialog {

    // These fields define the component properties
    private String family; // The name of the font family
    private int style; // The font style
    private int size; // The font size
    private Font selectedFont; // The Font they correspond to

    // This is the list of all font families on the system
    private String[] fontFamilies;

    // The various Swing components used in the dialog
    private ItemChooser families;
    // The various Swing components used in the dialog
    private ItemChooser styles;
    // The various Swing components used in the dialog
    private ItemChooser sizes;
    private JTextArea preview;
    private JButton okay;
    private JButton cancel;
    // The names to appear in the "Style" menu
    private static final String[] styleNames = new String[] {"Plain", "Italic", "Bold",
                                                     "BoldItalic"};
    // The style values that correspond to those names
    private static final Integer[] styleValues = new Integer[] {
                                                        new Integer(Font.PLAIN),
                                                        new Integer(Font.ITALIC),
                                                        new Integer(Font.BOLD),
                                                        new Integer(Font.BOLD
                                                                    + Font.ITALIC)};
    // The size "names" to appear in the size menu
    private static final String[] sizeNames = new String[] {"8", "10", "12", "14", "18", "20",
                                                    "24", "28", "32", "40", "48", "56",
                                                    "64", "72", "80", "88", "96", "104",
                                                    "120", "136", "152", "178"};

    // This is the default preview string displayed in the dialog box
    private static final String defaultPreviewString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n"
                                               + "abcdefghijklmnopqrstuvwxyz\n"
                                               + "1234567890!@#$%^&*()_-=+[]{}<,.>\n"
                                               + "The quick brown fox jumps over the lazy dog";

    /**
     * Create a font chooser dialog for the specified frame.
     * 
     * @param owner
     */
    public FontChooser(Frame owner) {
        super(owner, "Choose a Font"); // Set dialog frame and title

        // This dialog must be used as a modal dialog. In order to be used
        // as a modeless dialog, it would have to fire a PropertyChangeEvent
        // whenever the selected font changed, so that applications could be
        // notified of the user's selections.
        setModal(true);

        // Figure out what fonts are available on the system
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontFamilies = env.getAvailableFontFamilyNames();

        // Set initial values for the properties
        family = fontFamilies[0];
        style = Font.PLAIN;
        size = 18;
        selectedFont = new Font(family, style, size);

        // Create ItemChooser objects that allow the user to select font
        // family, style, and size.
        families = new ItemChooser("Family", fontFamilies, null, 0, ItemChooser.COMBOBOX);
        styles = new ItemChooser("Style", styleNames, styleValues, 0,
                                 ItemChooser.COMBOBOX);
        sizes = new ItemChooser("Size", sizeNames, null, 4, ItemChooser.COMBOBOX);

        // Now register event listeners to handle selections
        families.addItemChooserListener(new ItemChooser.Listener() {

            @Override
			public void itemChosen(ItemChooser.Event e) {
                setFontFamily((String) e.getSelectedValue());
            }
        });
        styles.addItemChooserListener(new ItemChooser.Listener() {

            @Override
			public void itemChosen(ItemChooser.Event e) {
                setFontStyle(((Integer) e.getSelectedValue()).intValue());
            }
        });
        sizes.addItemChooserListener(new ItemChooser.Listener() {

            @Override
			public void itemChosen(ItemChooser.Event e) {
                setFontSize(Integer.parseInt((String) e.getSelectedValue()));
            }
        });

        // Create a component to preview the font.
        preview = new JTextArea(defaultPreviewString, 5, 40);
        preview.setFont(selectedFont);

        // Create buttons to dismiss the dialog, and set handlers on them
        okay = new JButton("Okay");
        cancel = new JButton("Cancel");
        okay.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        cancel.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                selectedFont = null;
                setVisible(false);
            }
        });

        // Put the ItemChoosers in a Box
        Box choosersBox = Box.createHorizontalBox();
        choosersBox.add(Box.createHorizontalStrut(15));
        choosersBox.add(families);
        choosersBox.add(Box.createHorizontalStrut(15));
        choosersBox.add(styles);
        choosersBox.add(Box.createHorizontalStrut(15));
        choosersBox.add(sizes);
        choosersBox.add(Box.createHorizontalStrut(15));
        choosersBox.add(Box.createGlue());

        // Put the dismiss buttons in another box
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createGlue());
        buttonBox.add(okay);
        buttonBox.add(Box.createGlue());
        buttonBox.add(cancel);
        buttonBox.add(Box.createGlue());

        // Put the choosers at the top, the buttons at the bottom, and
        // the preview in the middle.
        Container contentPane = getContentPane();
        contentPane.add(new JScrollPane(preview), BorderLayout.CENTER);
        contentPane.add(choosersBox, BorderLayout.NORTH);
        contentPane.add(buttonBox, BorderLayout.SOUTH);

        // Set the dialog size based on the component size.
        pack();
    }

    /**
     * Call this method after show() to obtain the user's selection. If the user used the
     * "Cancel" button, this will return null
     * @return the selected font.
     */
    public Font getSelectedFont() {
        return selectedFont;
    }

    // These are other property getter methods
    /**
     * @return The font family. 
     */
    public String getFontFamily() {
        return family;
    }

    /**
     * @return The font style.
     */
    public int getFontStyle() {
        return style;
    }

    /**
     * @return the font size.
     */
    public int getFontSize() {
        return size;
    }

    // The property setter methods are a little more complicated.
    // Note that none of these setter methods update the corresponding
    // ItemChooser components as they ought to.
    /**
     * @param name The name of the font family.
     */
    public void setFontFamily(String name) {
        family = name;
        changeFont();
    }

    
    /**
     * @param argStyle The style to set.
     */
    public void setFontStyle(int argStyle) {
        this.style = argStyle;
        changeFont();
    }

    /**
     * @param argSize The size to set. 
     */
    public void setFontSize(int argSize) {
        this.size = argSize;
        changeFont();
    }

    /**
     * @param font The font to set. 
     */
    public void setSelectedFont(Font font) {
        selectedFont = font;
        family = font.getFamily();
        style = font.getStyle();
        size = font.getSize();
        preview.setFont(font);
    }

    /** 
     * This method is called when the family, style, or size changes
     */
    protected void changeFont() {
        selectedFont = new Font(family, style, size);
        preview.setFont(selectedFont);
    }

    /** 
     * Override this inherited method to prevent anyone from making us modeless
     * @see java.awt.Dialog#isModal()
     */
    @Override
	public boolean isModal() {
        return true;
    }

    /** This inner class demonstrates the use of FontChooser 
     * @param args ignored. 
     */
    public static void main(String[] args) {
        // Create some components and a FontChooser dialog
        final JFrame frame = new JFrame("demo");
        final JButton button = new JButton("Push Me!");
        final FontChooser chooser = new FontChooser(frame);

        // Handle button clicks
        button.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                // Pop up the dialog
                chooser.setVisible(true);
                // Get the user's selection
                Font font = chooser.getSelectedFont();
                // If not cancelled, set the button font
                if (font != null)
                    button.setFont(font);
            }
        });

        // Display the demo
        frame.getContentPane().add(button);
        frame.setSize(200, 100);
        frame.setVisible(true);
    }

}

