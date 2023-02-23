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
 * Created on 20.09.2004
 *
 */
package de.uos.fmt.musitech.framework.editor.applets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * JApplet for demonstrating the MUSITECH Wrappers.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class WrapperApplet extends JApplet {

    Display popUpWrapper = null;
    
    Display previewWrapper = null;

    Display iconWrapper = null;

    Display expandWrapper = null;
    
    Container wrapperContainer = null;
    
    
    /**
     * Initiliazes the JApplet. Creates the <code>popUpWrapper</code>, 
     * <code>iconWrapper</code> and </code>expandWrapper</code> and
     * builds the GUI.
     *  
     * @see java.applet.Applet#init()
     */
    @Override
	public void init() {
        try {
            super.init();
            createWrappers();
            createGUI();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Creates the <code>popUpWrapper</code>, <code>iconWrapper</code> and 
     * </code>expandWrapper</code>.
     */
    private void createWrappers() {
        Object displayObj = createObjectToDisplay();
        if (displayObj != null) {
            Display displayToWrap = createDisplayToWrap(displayObj);
            if (displayToWrap != null) {
                popUpWrapper = createPopUpWrapper(displayToWrap);
                previewWrapper = createPreviewWrapper(displayToWrap);
                iconWrapper = createIconWrapper(displayToWrap);
                expandWrapper = createExpandWrapper(displayToWrap);
            }
        }
    }

    /**
     * Creates the graphical user interface. Shows the three wrappers, each
     * introduced by a JLabel.         
     */
    private void createGUI() {
        wrapperContainer = getContentPane();
        wrapperContainer.setLayout(new BoxLayout(wrapperContainer, BoxLayout.Y_AXIS));
        if (popUpWrapper != null) {
//            wrapperContainer.add(new JLabel("PopUpWrapper:"));
//            wrapperContainer.add((JComponent) popUpWrapper);
            wrapperContainer.add(createWrapperPanel(new JLabel("PopUpWrapper:"), (JComponent)popUpWrapper));
        }
        if (previewWrapper != null) {
            wrapperContainer.add(createWrapperPanel(new JLabel("PreviewWrapper:"), (JComponent)previewWrapper));
        }
        if (iconWrapper != null) {
//            wrapperContainer.add(new JLabel("IconWrapper:"));
//            wrapperContainer.add((JComponent) iconWrapper);
            wrapperContainer.add(createWrapperPanel(new JLabel("IconWrapper:"), (JComponent)iconWrapper));
        }
        if (expandWrapper != null) {
//            wrapperContainer.add(new JLabel("ExpandWrapper:"));
//            wrapperContainer.add((JComponent) expandWrapper);
            wrapperContainer.add(createWrapperPanel(new JLabel("ExpandWrapper:"), (JComponent)expandWrapper));
        }
    }
    
    /**
     * Returns a JPanel containing the specified JLabel and JComponent in vertical
     * orientation.
     * 
     * @param label JLabel to be displayed, introducing the specified wrapper
     * @param wrapper JComponent wrapper to be displayed
     * @return JPanel containing the specified JLabel and JComponent 
     */
    private JPanel createWrapperPanel(JLabel label, JComponent wrapper){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory
                .createEmptyBorder(10,10,10,10));
        label.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(wrapper);
        return panel;
    }

    /**
     * Returns a ChordSymbol which is used as <code>editObj</code> in the display
     * to be wrapped.
     * 
     * @return Object to be shown in the wrapped displays
     */
    private Object createObjectToDisplay() {
        ChordSymbol cs = new ChordSymbol();
        cs.setRoot('e');
        cs.setRootAccidental(-1);
        cs.setBase(3);
        cs.setTop(5);
        cs.setMode(ChordSymbol.Mode.MODE_MAJOR);
        return cs;
    }

    /**
     * Returns a Display showing the specified Object.
     * This Display is used as display to wrap.
     * 
     * @param objToDisplay Object to be displayed
     * @return Display showing the specified Object
     */
    private Display createDisplayToWrap(Object objToDisplay) {
        //create EditingProfile (with icon)
        EditingProfile profile = EditorFactory
                .createDefaultProfile(objToDisplay);
        URL iconURL = WrapperApplet.class.getResource("Icons/WrapperIcon.gif");
        Icon icon = new ImageIcon(iconURL);
        profile.setIcons(icon);
        //create Display
        Display displayToWrap = null;
        try {
            displayToWrap = EditorFactory.createDisplay(objToDisplay, profile);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return displayToWrap;
    }

    /**
     * Returns a PopUpDisplay wrapping the specified Display.
     * 
     * @param displayToWrap Display to be wrapped
     * @return Display wrapping the specified Display
     */
    private Display createPopUpWrapper(Display displayToWrap) {
        Display popUpWrapper = null;
        try {
            popUpWrapper = EditorFactory.createWrappingDisplay("PopUp",
                    displayToWrap);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return popUpWrapper;
    }
    
    /**
     * Returns a PreviewDisplay wrapping the specified Display.
     * 
     * @param displayToWrap Display to be wrapped
     * @return Display wrapping the specified Display
     */
    private Display createPreviewWrapper(Display displayToWrap){
        Display previewWrapper = null;
        try {
            previewWrapper = EditorFactory.createWrappingDisplay("Preview",
                    displayToWrap);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return previewWrapper;
    }
    
    /**
     * Returns an IconDisplay wrapping the specified Display.
     * 
     * @param displayToWrap Display to be wrapped
     * @return Display wrapping the specified Display
     */
    private Display createIconWrapper(Display displayToWrap) {
        Display iconWrapper = null;
        try {
            iconWrapper = EditorFactory.createWrappingDisplay("Icon",
                    displayToWrap);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return iconWrapper;
    }

    /**
     * Returns an ExpandDisplay wrapping the specified Display.
     * 
     * @param displayToWrap Display to be wrapped
     * @return Display wrapping the specified Display
     */
    private Display createExpandWrapper(Display displayToWrap) {
        Display expandWrapper = null;
        try {
            expandWrapper = EditorFactory.createWrappingDisplay("Expand",
                    displayToWrap);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return expandWrapper;
    }

    /**
     * Just for testing.
     * 
     * @param args
     */
/*    public static void main(String[] args) {
        WrapperApplet applet = new WrapperApplet();
        applet.init();
        JFrame frame = new JFrame();
        frame.getContentPane().add(applet.wrapperContainer);
        frame.pack();
        frame.setVisible(true);
    }
*/
    
}