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
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * @author Kerstin Neubarth
 *  
 */
public class ExpandDisplay extends AbstractComplexDisplay implements Wrapper {

    Display displayToExpand = null;

    protected boolean expanding = false;

    /**
     * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#createGUI()
     */
    public void createGUI() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(createTitleBar(), BorderLayout.NORTH);
        if (expanding) {
            if (displayToExpand == null)
                displayToExpand = createEditorToExpand();
            if (displayToExpand != null)
                panel.add((Component) displayToExpand, BorderLayout.CENTER);
        }
        //		if (isOutmostEditor()) {
        if (rootDisplay == this) {
            JScrollPane scroll = new JScrollPane(panel);
            add(scroll);
        } else {
            add(panel);
        }
    }

    /**
     * @see de.uos.fmt.musitech.framework.editor.Wrapper#setWrappedView(de.uos.fmt.musitech.framework.editor.Display)
     */
    public void setWrappedView(Display view) {
        this.displayToExpand = view;
        this.editObj = view.getEditObj();
    }

    public Display getDisplayToExpand() {
        return displayToExpand;
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
        expandCollapseButton.setPreferredSize((new JButton("Collapse"))
                .getPreferredSize());
        //GUI with "Expand"/"Collapse"-button
        if (expanding) {
            createCollapseBorder(titleBar);
            expandCollapseButton.setText("Collapse");
        } else {
            label.setText(getLabelText());
            createExpandBorder(titleBar);
            expandCollapseButton.setText("Expand");
        }
        expandCollapseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ce) {
                changeState();
            }
        });
        titleBar.add(expandCollapseButton);
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
     * Returns <code>editorToExpand</code> which is newly created if
     * this.editorToExpand is null.
     * 
     * @return Display displayToExpand
     */
    Display createEditorToExpand() {
        if (getObjectForEditor() != null) {
            try {
                displayToExpand = EditorFactory.createEditor(
                        getObjectForEditor(), null, rootDisplay);
            } catch (EditorConstructionException ece) {
                ece.printStackTrace();
            }
        }
        return displayToExpand;
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
    
    public void changeState() {
		expanding = !expanding;
		removeAll();
		createGUI();
		revalidate();
		if (getParent() != null)
			getParent().repaint();
	}

}