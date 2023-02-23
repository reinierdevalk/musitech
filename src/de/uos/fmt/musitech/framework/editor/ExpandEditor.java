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
 * Created on 25.08.2003
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Like the PopUpEditor, the ExpandEditor also shows only a button first
 * offering to display the object to be edited. But while PopUpEditor opens a
 * new editor window if the "Edit"-Button is pressed, ExpandEditor expands its
 * own JPanel to display the object if the button "Expand" is activated (and
 * changes the button text to "Collapse"). <br>
 * If ExpandEditor is used to edit a property with null value (that is
 * propertyValue is null, but propertyName is not), a "Create" button is shown.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class ExpandEditor extends AbstractComplexEditor implements Wrapper {

    protected JButton expandButton = new JButton();

    protected final String BUTTON_TEXT_EXPAND = "Expand";

    protected final String BUTTON_TEXT_COLLAPSE = "Collapse";

    //button text if propertyValue is null:
    protected final String BUTTON_TEXT_CREATE = "Create";

    //editor to be displayed in case "Expand" is selected:
    protected Editor editorToExpand;

    //option for remembering if "Expand" or "Collapse" is selected
    //is false if the collapsed display is shown
    protected boolean expandingEditor = false;

    /**
     * Sets the editor to be displayed if "Expand" is chosen as well as the
     * object to be edited in editorToExpand.
     * 
     * @param editorToExpand
     *            Editor to be shown in case of the expanded view
     */
    @Override
	public void setWrappedView(Display editorToExpand) {
        this.editorToExpand = (Editor) editorToExpand;
        this.editObj = editorToExpand.getEditObj();
    }

    /**
     * Returns the AbstractEditor to be added if "Expand" is selected.
     * 
     * @return
     */
    public Editor getEditorToExpand() {
        return this.editorToExpand;
    }

    /**
     * Overwrites method <code>applyChanges</code> of class
     * <code>AbstractComplexEditor</code> in order to propagate
     * <code>applyChanges()</code> to the <code>editorToPopUp</code>.
     * If this ExpandEditor is the outmost editor, its sends a DataChangeEvent
     * to the DataChangeManager.
     * The flags <code>dataChanged</code> and <code>dirty</code> are reset to false.
     */
    @Override
	public void applyChanges() {
        if (profile.isReadOnly())
            return;
        Collection editedData = getEditedData();
        //apply changed values
        if (editorToExpand != null) {
//            editedData = editorToExpand.getEditedData();
            editorToExpand.applyChanges();
        }
        setPropertyIfNull(editObj);
        //send DataChangeEvent
        if (getRootDisplay() == this) {
            sendDataChangeEvent(editedData);
        }
        dataChanged = false;
        dirty = false;
    }

    /**
     * Overwrites empty method <code>createGUI()</code> in class
     * AbstractComplexEditor. Sets the editor's LayoutManager, displays a title
     * bar in the top section and the <code>editorToExpand</code> if
     * "Expanded" is selected, i.e. if <code>expandingEditor</code> is true.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractComplexEditor#createGUI()
     */
    @Override
	public void createGUI() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(createTitleBar(), BorderLayout.NORTH);
        if (expandingEditor) {
            if (editorToExpand == null)
                editorToExpand = createEditorToExpand();
            if (editorToExpand != null)
                panel.add((Component) editorToExpand, BorderLayout.CENTER);
        }
        if (rootDisplay == this) {
            JScrollPane scroll = new JScrollPane(panel);
            add(scroll);
        } else {
            add(panel);
        }
        //TODO Layout an Layout der als editorToExpand in Frage kommenden
        // Editoren anpassen
    }

    /**
     * Returns a Box containing a JLabel and a JButton separated by horizontal
     * glue. The JLabel displays the editor's <code>labeltext</code>. The
     * JButton is labelled "Expand" or "Collapse", depending on the editor's
     * current state. If <code>propertyValue</code> is null (but
     * <code>propertyName</code> is not) the button is labelled "Create".
     * 
     * @return Box containing a JLabel with labeltext and a JButton ("Expand",
     *         "Collapse" or "Create")
     */
    private Box createTitleBar() {
        Box titleBar = Box.createHorizontalBox();
        JLabel label = new JLabel();
        titleBar.add(label);
        titleBar.add(Box.createHorizontalGlue());
        JButton expandCollapseButton = new JButton();
        expandCollapseButton
                .setPreferredSize((new JButton(BUTTON_TEXT_COLLAPSE))
                        .getPreferredSize());
        //GUI with "Create"-button
        if (propertyName != null && propertyValue == null) {
            JButton createButton = new JButton(BUTTON_TEXT_CREATE);

            createButton.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent ae) {
                    //					propertyValue = createNewPropertyValue("New
                    // propertyValue");
                    propertyValue = createNewValue("New propertyValue");
                    //Modif040610
                    if (propertyValue != null)
                        setValueCreated(true);
                    //ned Modif
                    //					setNewPropertyValueToEditObjCopy();
                    //					createEditorToExpand();
                    //					updateDisplay();
                    updateGUI();
                }
            });
            label.setText(getLabeltext());
            createExpandBorder(titleBar);
            titleBar.add(createButton);
            return titleBar;
        }
        //GUI with "Expand"/"Collapse"-button
        if (expandingEditor) {
            createCollapseBorder(titleBar);
            expandCollapseButton.setText(BUTTON_TEXT_COLLAPSE);
        } else {
            label.setText(getLabeltext());
            createExpandBorder(titleBar);
            expandCollapseButton.setText(BUTTON_TEXT_EXPAND);
        }

        expandCollapseButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent ce) {
                //				updateDisplay();
                updateGUI();
            }
        });

        titleBar.add(expandCollapseButton);
        //Modif0710
        //		addFocusListenerToEditorComponent(this, titleBar);
        //		addFocusListenerToEditorComponent(this, expandCollapseButton);
        return titleBar;
    }

    /**
     * Sets the Border for the editor's titleBox in case of JButton "Collapse".
     * 
     * @param titleBox
     *            Box with a Border containing a bottom line to separate the
     *            titleBox from the editorToExpand
     */
    private void createCollapseBorder(Box titleBox) {
        int outsideBorderHorizontalInset = 15;
        int outsideBorderTopInset = 2;
        int outsideBorderBottomInset = 5;
        int bottomLine = 1;
        Color bottomLineColor = Color.GRAY;
        int insideBorderBottomInset = 5;
        titleBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createCompoundBorder(
                        BorderFactory.createEmptyBorder(outsideBorderTopInset,
                                outsideBorderHorizontalInset,
                                outsideBorderBottomInset,
                                outsideBorderHorizontalInset), BorderFactory
                                .createMatteBorder(0, 0, bottomLine, 0,
                                        bottomLineColor)), BorderFactory
                .createEmptyBorder(0, 0, insideBorderBottomInset, 0)));
    }

    /**
     * Sets the Border for the editor's titleBox in case of JButton "Expand".
     * 
     * @param titleBox
     *            Box with an empty border
     */
    private void createExpandBorder(Box titleBox) {
        int bottomInset = 5;
        titleBox.setBorder(BorderFactory
                .createEmptyBorder(0, 0, bottomInset, 0));
    }

    /**
     * Returns the object to be edited in the <code>editorToExpand</code>.
     * Can be the editor's <code>editObj</code> itself or one of its
     * properties.
     * 
     * @return Object to be edited
     */
    private Object getObjectForEditor() {
        Object objectForEditor = null;
        if (editObj != null) {
            if (propertyName == null) {
                objectForEditor = editObj;
            } else {
                if (propertyValue == null)
                    setPropertyValue();
                if (propertyValue != null)
                    objectForEditor = propertyValue;
            }
        }
        return objectForEditor;
    }

    /**
     * Returns <code>editorToExpand</code> which is newly created if
     * this.editorToExpand is null.
     * 
     * @return Editor editorToExpand
     */
    private Editor createEditorToExpand() {
        //		if (editorToExpand == null) {
        if (getObjectForEditor() != null) {
            try {
                editorToExpand = EditorFactory.createEditor(
                        getObjectForEditor(), null, rootDisplay);
            } catch (EditorConstructionException ece) {
                ece.printStackTrace();
            }
        }
        //		}
        return editorToExpand;
    }

    /**
     * Updates this ExpandEditor and its <code>editorToExpand</code> to externally
     * changes values. The flags <code>dataChanged</code>, <code>dirty</code> and
     * <code>valueCreated</code> are reset to false.
     *  
     * @see de.uos.fmt.musitech.framework.editor.Display#updateDisplay()
     */
    @Override
	public void updateDisplay() {
        editorToExpand.updateDisplay();
        dataChanged = false;
        setValueCreated(false);
        dirty = false;
    }

    /**
     * Updates the graphical user interface.
     * Adjusts the editor to the state chosen by activating the
     * "Expand"/"Collapse"-button. Switches the value of
     * <code>expandingEditor</code> and expands or collapses the
     * ExpandEditor's display, depending on the changed value of
     * <code>expandingEditor</code>.
     */
    public void updateGUI() {
        expandingEditor = !expandingEditor;
        removeAll();
        createGUI();
        revalidate();
        if (getParent() != null) {
            getParent().repaint();
        }
    }

    /**
     * Overwrites method <code>isRemoteEventSource</code> in class
     * <code>Editor</code> to check the the specified <code>eventSource</code>
     * against <code>editorToExpand</code> as well. Returns true if the
     * specified <code>eventSource</code> is not this editor, this editor's
     * <code>editorToExpand</code> or another editor related to this editor,
     * false otherwise.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#isRemoteEventSource(java.lang.Object)
     */
    @Override
	protected boolean isRemoteEventSource(Object eventSource) {
        boolean related = true;
        if (editorToExpand != null)
            related = eventSource.equals(editorToExpand);
        return super.isRemoteEventSource(eventSource) && !related;
    }

    /**
     * Returns true if this ExpandEditor is the FocusOwner, false otherwise.
     *  
     * @see de.uos.fmt.musitech.framework.editor.Display#isFocused()
     */
    @Override
	public boolean isFocused() {
        return isFocusOwner(); //TODO welche Komponenten müssen überprüft
                               // werden?
    }

    /**
     * Overwrites method <code>getEditedData</code> of class <code>AbstractComplexEditor</code>
     * in order to gather the edited data from the <code>editorToExpand</code>. If the
     * <code>editorToExpand</code> does not return edited data, but this ExpandEditor is
     * dirty (which is the case when "Overwrite" is chosen after data has been changed),
     * the object edited by this ExpandEditor, which is the <code>propertyValue</code> or 
     * <code>editObj</code>, is added to the returned Collection.
     *  
     * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
     */
    @Override
	public Collection getEditedData() {
        Collection editedData = new ArrayList();
        if (editorToExpand != null) {
            Collection data = editorToExpand.getEditedData();
            if (data != null) {
                editedData.addAll(data);
            }
        }
        if (editedData.isEmpty() && dirty) {
            editedData.add(getObjectForEditor());
        }
        return editedData;
    }
    
    /**
     * Returns <code>dataChanged</code> of the <code>editorToExpand</code>.
     * If the <code>editorToExpand</code> is null, <code>dataChanged</code> of this
     * ExpandEditor is returned.
     *  
     * @see de.uos.fmt.musitech.framework.editor.Display#externalChanges()
     */
    @Override
	public boolean externalChanges(){
        if (editorToExpand!=null)
            dataChanged = editorToExpand.externalChanges();
        return dataChanged;
    }

}