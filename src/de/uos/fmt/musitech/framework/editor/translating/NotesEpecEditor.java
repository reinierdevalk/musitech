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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;

/**
 * TranslatingEditor which generates a NoteList from epec.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class NotesEpecEditor extends AbstractTranslatingEditor {

    /**
     * Local NoteList used for working on the <code>editObj</code>.
     */
    protected NoteList notes = new NoteList();

    /**
     * Local String used for working in the <code>inputObj</code>.
     */
    protected String epec = "";

    /**
     * JTextArea used for entering epec.
     */
    protected JTextArea epecArea = new JTextArea();

    /**
     * NotationDisplay used for displaying a preview.
     */
    protected NotationDisplay notesPreview;

    /**
     * Updates the local objects <code>epec</code> and <code>notes</code>
     * and the displays <code>epecEditor</code> and <code>notesPreview</code>
     * to changes in the <code>inputObj</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#updateDisplay()
     */
    @Override
	public void updateDisplay() {
        //update epec and notes
        determineLocalObjs();
        //update epecEditor and notesPreview
        epecArea.setText(epec);
        notes.clear();
        notes.addAll(new NoteList(epec));
        if (notesPreview != null) {
            notesPreview.updateDisplay();
        }
        revalidate();
    }

    /**
     * Creates the GUI with the <code>epecArea</code> in the NORTH area, the
     * <code>notesPreview</code> in the CENTER and a button for updating the
     * preview in the SOUTH.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    @Override
	protected void createGUI() {
        setLayout(new BorderLayout());
        //add epecArea
        epecArea.setBorder(BorderFactory.createTitledBorder("Input in epec"));
        epecArea.setLineWrap(true);
        epecArea.setText(epec);
        add(epecArea, BorderLayout.NORTH);
        //add notesPreview
        createNotesPreview();
        if (notesPreview != null) {
            notesPreview.setBorder(BorderFactory
                    .createTitledBorder("Preview: Notation"));
            add(notesPreview, BorderLayout.CENTER);
            
        }
        add(createPreviewButton(), BorderLayout.SOUTH);
    }

    /**
     * Creates the <code>notesPreview</code>.
     */
    protected void createNotesPreview() {
        if (notes.size() > 0) {
            try {
                notesPreview = (NotationDisplay) EditorFactory.createDisplay(
                        notes, null, "Notation");
            } catch (EditorConstructionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a JComponent containing a JButton for updating the preview.
     * 
     * @return JComponent containing a JButton for updating the preview
     */
    protected JComponent createPreviewButton() {
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
    protected void updatePreview() {
        epec = epecArea.getText();
        if (epec.equals(""))
            return;
        notes.clear();
        notes.addAll(new NoteList(epec));
        if (notesPreview != null) {
            notesPreview.updateDisplay();
            revalidate();
        } else {
            createNotesPreview();
            add(notesPreview, BorderLayout.CENTER);
            revalidate();
        }
    }

    /**
     * Applies the changes entered by the user to the <code>inputObj</code>.
     * The <code>epec</code> is written to the <code>inputObj</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.translating.AbstractTranslatingEditor#applyChangesToInputObj()
     */
    @Override
	protected void applyChangesToInputObj() {
        if (propertyValue != null) {
            propertyValue = epec;
            ReflectionAccess ref = ReflectionAccess.accessForClass(inputObj
                    .getClass());
            if (ref.hasPropertyName(propertyName))
                ref.setProperty(inputObj, propertyName, propertyValue);
        } else {
            if (inputObj instanceof String) {
                inputObj = epec;
            }
        }
    }

    /**
     * Applies the changes in the <code>inputObj</code> accordingly to the
     * <code>editObj</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.translating.AbstractTranslatingEditor#applyChangesToEditObj()
     */
    @Override
	protected void applyChangesToEditObj() {
        //read from inputObj
        String code = "";
        if (propertyValue != null && propertyValue instanceof String) {
            code = (String) propertyValue;
        } else {
            if (inputObj instanceof String) {
                code = (String) inputObj;
            }
        }
        notes.clear();
        notes.addAll(new NoteList(code));
        if (editObj == null || !(editObj instanceof NoteList)) {
            editObj = notes;
        } else {
            if (editObj instanceof NoteList) {
                ((NoteList) editObj).clear();
                ((NoteList) editObj).addAll(notes);
            }
        }
    }

    /**
     * Sets the <code>epec/code> and the <code>notes</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.translating.AbstractTranslatingEditor#determineLocalObjs()
     */
    @Override
	protected void determineLocalObjs() {
        //set epec
        if (propertyValue != null && propertyValue instanceof String) {
            epec = (String) propertyValue;
        } else {
            if (inputObj instanceof String) {
                epec = (String) inputObj;
            }
        }
        //set notes
        notes = new NoteList(epec);
    }
    
    /**
     * @return Returns the notes.
     */
    public NoteList getNotes() {
        return notes;
    }

}