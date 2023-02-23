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
 * Created on 22.10.2004
 *
 */
package de.uos.fmt.musitech.framework.editor.translating;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import de.uos.fmt.musitech.utility.obj.ObjectCopy;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * TranslatingEditor which generates an Icon from an URL.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class IconURLEditor extends AbstractTranslatingEditor {

    /**
     * URL of the icon.
     */
    private URL url = null;

    /**
     * Icon to be generated from the <code>url</code>.
     */
    private Icon icon = null;

    /**
     * JTextField for editing the URL.
     */
    private JTextField urlField = new JTextField();

    /**
     * JButton for showing the generated Icon.
     */
    private JButton iconButton = new JButton();
    
    /**
     * To record the text for resetting the <code>urlField</code>.
     */
    private String previousText = null;

    /**
     * Updates the ImageIconEditor by updating <code>url</code> according to
     * the changed <code>editObj</code> and creating the <code>icon</code>
     * form this <code>url</code>. The GUI is updated by setting the text of
     * the <code>urlField</code> and the icon of the <code>iconButton</code>
     * according to the updated <code>url</code> and <code>icon</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#updateDisplay()
     */
    @Override
	public void updateDisplay() {
        //update URL
        determineURL();
        //update urlField
        if (url != null) {
            urlField.setText(url.toString());
            //update Icon
            createIcon();
            //update iconButton
            if (icon != null) {
                iconButton.setText("");
                iconButton.setIcon(icon);
            }
            revalidate();
        }     
    }

    /**
     * Creates the graphical user interface. The JTextField
     * <code>urlField</code> is titled "URL" and shown in the NORTH area, the
     * <code>iconButton</code> displaying the <code>icon</code> is titled
     * "Preview: Icon" and shown in the SOUTH area. The <code>urlField</code>
     * is given a FocusListener to write changed input to the <code>url</code>
     * when the focus is lost.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    @Override
	protected void createGUI() {
        setLayout(new BorderLayout());
        urlField.setBorder(BorderFactory.createTitledBorder("URL"));
        urlField.addFocusListener(new FocusAdapter() {
            @Override
			public void focusGained(FocusEvent e) {
                previousText = urlField.getText();
            }
        });
        if (url != null){
            urlField.setText(url.toString());
            if (previousText==null){
                previousText = urlField.getText();
            }
        }
        add(urlField, BorderLayout.NORTH);
        iconButton.setEnabled(false);
        iconButton.setBorder(BorderFactory.createTitledBorder("Preview: Icon"));
        add(iconButton, BorderLayout.CENTER);
        add(createPreviewButton(), BorderLayout.SOUTH);
        updateDisplay();
    }

    /**
     * Returns a JComponent containing a JButton for updating the preview.
     * 
     * @return JComponent containing a JButton for updating the preview
     */
    private JComponent createPreviewButton() {
        JButton previewButton = new JButton("Update Preview");
        previewButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        });
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(previewButton);
        buttonBox.add(Box.createHorizontalGlue());
        return buttonBox;
    }

    /**
     * Reads the text from the <code>epecArea</code> and writes it to
     * <code>epec</code>, updates the <code>notes</code> to the new
     * <code>epec</code> and updates the <code>notesPreview</code>.
     */
    private void updatePreview() {
        if (applyChangesToURL()) {
            if (url != null) {
                urlField.setText(url.toString());
                //update Icon
                createIcon();
                //update iconButton
                if (icon != null) {
                    iconButton.setText("");
                    iconButton.setIcon(icon);
                }
                revalidate();
            } 
        } //else reset to old text
        else {
            if (previousText!=null)
                urlField.setText(previousText);
        }
    }

    /**
     * Returns true if this ImageIconEditor or its <code>urlField</code> has
     * the focus.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    @Override
	public boolean isFocused() {
        return (isFocusOwner() || urlField.isFocusOwner());
    }

    /**
     * Sets the <code>url</code> and the <code>icon</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.translating.AbstractTranslatingEditor#determineLocalObjs()
     */
    @Override
	protected void determineLocalObjs() {
        determineURL();
        createIcon();
    }

    /**
     * Sets the <code>url</code> to a copy of either the
     * <code>propertyValue</code> or the <code>editObj</code>.
     */
    private void determineURL() {
        if (propertyValue != null && propertyValue instanceof URL) {
            url = (URL) ObjectCopy.copyObject(propertyValue);
        } else if (inputObj instanceof URL) {
            url = (URL) ObjectCopy.copyObject(inputObj);
        }
    }

    /**
     * Creates an ImageIcon from the <code>url</code> and sets it as the
     * <code>icon</code>.
     */
    private void createIcon() {
        if (url != null && !url.toString().equals("")) {
            icon = new ImageIcon(url);
        }
    }

    /**
     * Reads the text from the <code>urlField</code>. If the text has
     * changed, <code>dirty</code> is set true. In this case an URL is created
     * from the text and set as the <code>url</code>. If the <code>url</code>
     * is changed, true is returned. False is returned, if the text in the
     * <code>urlField</code> has not changed or a MalformedURLExcpetion has
     * occurred while trying to create the URL.
     * 
     * @return boolean true if the <code>url</code> has been set to a changed
     *         URL, false otherwise
     */
    private boolean applyChangesToURL() {
        String textInField = urlField.getText();
        if (url == null || !url.toString().equals(textInField)) {
            dirty = true;
            try {
                url = new URL(textInField);
                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(iconButton, "This is not a valid URL.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
        return false;
    }

    /**
     * Returns the <code>inputObj</code>.
     * 
     * @param Object
     *            the <code>inputObj</code> of this IconURLEditor
     * 
     * @see de.uos.fmt.musitech.framework.editor.translating.TranslatingEditor#getInputObj()
     */
    public Object getInputObj() {
        return inputObj;
    }

    /**
     * Returns the <code>editObj</code> of this IconURLEditor.
     * 
     * TODO update comment (can be null returned?)
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#getEditObj()
     */
    @Override
	public Object getEditObj() {
        if (editObj == null) {
            applyChangesToEditObj();
        }
        return editObj; //TODO garantieren, dass nicht null, sonst evtl.
                        // inputObj?
    }

    /**
     * Applies the changes entered by the user to the <code>inputObj</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.translating.AbstractTranslatingEditor#applyChangesToInputObj()
     */
    @Override
	protected void applyChangesToInputObj() {
        if (propertyValue != null) {
            ObjectCopy.copyPublicProperties(url, propertyValue);
            ReflectionAccess ref = ReflectionAccess.accessForClass(inputObj
                    .getClass());
            if (ref.hasPropertyName(propertyName))
                ref.setProperty(inputObj, propertyName, propertyValue);
        } else {
            if (inputObj instanceof URL) {
                ObjectCopy.copyPublicProperties(url, inputObj);
            }
        }
    }

    /**
     * Updates the <code>editObj</code> according to the changes in the
     * <code>inputObj</code>. The <code>icon</code> is created from the URL
     * and set as <code>editObj</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.translating.AbstractTranslatingEditor#applyChangesToEditObj()
     */
    @Override
	protected void applyChangesToEditObj() {
        createIcon();
        editObj = icon;
    }

}