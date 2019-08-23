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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.form.NoteList;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorPanel;
import de.uos.fmt.musitech.framework.editor.TextEditor;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.score.gui.Clef;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * The NotesEpecExtendedEditor extends the NotesEpecEditor by menus for choosing
 * a clef, the key and the time signature. Additionally, the metrical start time
 * can be set.
 * 
 * TODO außerdem sollte existierender Context aus Datenbank o.ä. gewählt werden
 * können
 * 
 * TODO layout verbessern TODO Änderung key -> Fehler (Layout?) TODO Änderung
 * clef -> Einbindung fehlt noch
 * 
 * @author Kerstin Neubarth
 *  
 */
public class NotesEpecExtendedEditor extends NotesEpecEditor {

    /**
     * Context whose MetricalTimeLine is used for setting time signature and key.
     */
    private Context context = new Context(new Piece());

    /**
     * Records the clef type which is chosen.
     */
    private int clefType = 0;

    /**
     * Metrical time of the note sequence which is generated.
     */
    private Rational startTime = Rational.ZERO;

    /**
     * KeyMarker for setting a key (resp. the number of accidentals).
     */
    private KeyMarker key = new KeyMarker();

    /**
     * TimeSignatureMarker for setting a time signature.
     */
    private TimeSignatureMarker timeSignature = new TimeSignatureMarker(
            new TimeSignature(), startTime);

    /**
     * Font to be used with the texts which accompany the menus.
     */
    private static final Font TEXT_FONT = new Font("SansSerif", Font.BOLD, 12);

    /**
     * Text accompanying the menu for setting a clef.
     */
    private static String CLEF_TEXT;

    /**
     * Text accompanying the menu for setting a key.
     */
    private static String KEY_TEXT;

    /**
     * Text accompanying the menu for setting a time signature.
     */
    private static String TIME_TEXT;

    /**
     * Text accompanying the menu for setting a start time.
     */
    private static String START_TEXT;

    /**
     * Initialization of the texts.
     */
    static {
        initClefText();
        initKeyText();
        initTimeText();
        initStartText();
    }

    /**
     * Initializes the <ocde>CLEF_TEXT</code>.
     */
    private static void initClefText() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("The clef menu offers you to select a clef. ");
        buffer.append("If you want to set a clef not offered in the list, ");
        buffer.append("you can insert a clef in the epec field below. ");
        buffer
                .append("If you do not specify any clef, a treble clef will be used.");
        CLEF_TEXT = buffer.toString();
    }

    /**
     * Initializes the <ocde>KEY_TEXT</code>.
     */
    private static void initKeyText() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Please enter the key you want to set. ");
        buffer
                .append("You specify the number of accidentals you want to have. ");
        buffer
                .append("If you want to have a sharp key, please enter a positive integer. ");
        buffer
                .append("If you want to have a flat key, please enter a negative number.\n");
        buffer
                .append("Example: If you want to have A major, please enter '3'. ");
        buffer.append("If you want to have F major, you have to enter '-1'.\n");
        buffer
                .append("You can alternatively set the key in the epec field below. ");
        buffer
                .append("If you do not specify any key, 0 (no accidentals) will be set as a default.");
        KEY_TEXT = buffer.toString();
    }

    /**
     * Initializes the <ocde>TIME_TEXT</code>.
     */
    private static void initTimeText() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Please enter the time signature you want to set.\n");
        buffer.append("Example: If you want to have a 6/8 meter, ");
        buffer
                .append("you have to enter '6' in the left field and '8' in the right field.");
        buffer
                .append("You can alternatively set the time signature in the epec field below. ");
        buffer
                .append("If you do not specify any time signature, 4/4 will be set as a default.");
        TIME_TEXT = buffer.toString();
    }

    /**
     * Initializes the <ocde>START_TEXT</code>.
     */
    private static void initStartText() {
        StringBuffer buffer = new StringBuffer();
        buffer
                .append("If you want to let your music extract start later than at the beginning of a piece, ");
        buffer.append("you can give the start time here. ");
        buffer
                .append("The start time is given as the metrical position from the beginning ");
        buffer.append("of the whole context.\n");
        buffer
                .append("Example: If you have a piece in 4/4 meter and want to enter a voice ");
        buffer.append("beginning on the second beat of the second measure, ");
        buffer.append("you have to set the start to '8/4'. ");
        buffer.append("N.B.: The overall beginning is counted as 0/4.");
        START_TEXT = buffer.toString();
    }

    /**
     * Sets <code>context</code> as the Context of the <code>notes</code>.
     */
    private void initNotes() {
        //epec and notes are initialized in the superclass
        notes.setContext(context);
    }

    /**
     * Creates the GUI with the menus for setting the clef, key, time signature and
     * start time, the area for inserting the epec and a notation preview.
     *  
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    public void createGUI() {
        setLayout(new BorderLayout());
        Box menuBox = Box.createVerticalBox();
        //        int strut = 5;
        menuBox.add(createClefComponent());
        //        menuBox.add(Box.createVerticalStrut(strut));
        menuBox.add(createKeyComponent());
        menuBox.add(createTimeComponent());
        menuBox.add(createStartComponent());
        //        add(menuBox, BorderLayout.NORTH);
        //      add epecArea
        epecArea.setBorder(BorderFactory.createTitledBorder("Input in epec"));
        epecArea.setLineWrap(true);
        epecArea.setText(epec);
        menuBox.add(epecArea);
        JScrollPane scroll = new JScrollPane(menuBox); //TODO einzlne
        // ScrollPanes für clef,
        // key, etc. sections?
        add(scroll, BorderLayout.NORTH);
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
     * Returns a JComponent with the menu for setting a clef. The JComponent contains
     * a TextEditor with the <code>CLEF_TEXT</code> and a JComboBox for selecting a clef.
     * 
     * @return JComponent containing a text and a JComboBox for selecting a clef
     */
    private JComponent createClefComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        //        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Clef"));
        Editor editor = createTextEditor(CLEF_TEXT);
        if (editor != null) {
            panel.add((JComponent) editor, BorderLayout.NORTH);
            //            panel.add((JComponent)editor);
        }
        String[] clefs = { "treble", "bass", "soprano", "mezzo", "alto",
                "tenor", "baritone", "percussion" };
        final JComboBox clefList = new JComboBox(clefs);
        clefList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recordClef(clefList.getSelectedIndex());
            }
        });
        panel.add(clefList, BorderLayout.SOUTH);
        //        panel.add(clefList);
        return panel;
    }

    /**
     * Sets the specified int value as the <code>clefType</code> to record.
     * 
     * @param clefType int to be set as the <code>clefType</code>, corresponds to the clef selected form a JComboBox
     */
    private void recordClef(int clefType) {
        this.clefType = clefType;
    }

    /**
     * Returns a JComponent with a menu for setting a key. The JComponent contains
     * a TextEditor with the <code>KEY_TEXT</code> and an IntEditor for inserting 
     * the number of accidentals.
     * 
     * @return JComponent containing a text and an IntEditor for setting the nukber of accidentals
     */
    private JComponent createKeyComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        //        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Key"));
        Editor editor = createTextEditor(KEY_TEXT);
        if (editor != null) {
            panel.add((JComponent) editor, BorderLayout.NORTH);
            //            panel.add((JComponent)editor);
        }
        Editor keyEditor = createKeyEditor();
        if (keyEditor != null) {
            EditorPanel editorPanel = new EditorPanel();
            editorPanel.addEditor(keyEditor);
            //            panel.add((JComponent) keyEditor, BorderLayout.SOUTH);
            panel.add(editorPanel, BorderLayout.SOUTH);
            //            panel.add(editorPanel);
        }
        return panel;
    }

    /**
     * Returns an Editor for inserting the number of accidentals. The Editor edits the
     * <code>numberOfAccidentals</code> of the KeyMarker <code>key</code> which is added
     * to the MetricalTimeLine of the <code>context</code> (more precisely, of the
     * Context's Piece).
     * 
     * @return Editor for editing the number of accidentals
     */
    private Editor createKeyEditor() {
        //init Key and add to context
        key.setMetricTime(startTime);
        MetricalTimeLine mtl = context.getPiece().getMetricalTimeLine();
        if (mtl == null) {
            mtl = new MetricalTimeLine();
            context.getPiece().setMetricalTimeLine(mtl);
        }
        mtl.add(key);
        //create editor
        EditingProfile profile = new EditingProfile("accidentalNum");
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(key, profile);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return editor;
    }

    /**
     * Returns a JComponent for setting the time signature. The JComponent contains
     * a TextEditor with the <code>TIME_TEXT</code> and a RationalEditor for inserting
     * a time signature.
     * 
     * @return JComponent containing a text and a RationalEditor for setting the time signature
     */
    private JComponent createTimeComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        //        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Time signature"));
        Editor editor = createTextEditor(TIME_TEXT);
        if (editor != null) {
            panel.add((JComponent) editor, BorderLayout.NORTH);
            //            panel.add((JComponent)editor);
        }
        Editor keyEditor = createTimeEditor();
        if (keyEditor != null) {
            panel.add((JComponent) keyEditor, BorderLayout.SOUTH);
            //            panel.add((JComponent)keyEditor);
        }
        return panel;
    }

    /**
     * Returns an Editor for setting the time signature. The Editor edits the 
     * TimeSignature of the TimeSignatureMarker <code>timeSignature</code> which is
     * added to the MetricalTimeLine of the <code>context</code> (more precisely,
     * of the Context's Piece).
     * 
     * @return Editor for setting the time signature
     */
    private Editor createTimeEditor() {
        //init timeSignature
        timeSignature.setMetricTime(startTime);
        MetricalTimeLine mtl = context.getPiece().getMetricalTimeLine();
        if (mtl == null) {
            mtl = new MetricalTimeLine();
            context.getPiece().setMetricalTimeLine(mtl);
        }
        mtl.add(timeSignature);
        //create editor
        EditingProfile profile = new EditingProfile("timeSignature");
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(timeSignature, profile);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return editor;
    }

    /**
     * Returns a JComponent for setting a metrical start time. The JComponent contains
     * a TextEditor with the <code>START_TEXT</code> and a RationalEditor for the
     * metrical time.
     * 
     * @return JComponent containing a text and a RationalEditor for setting the metrical start time
     */
    private JComponent createStartComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        //        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Start time"));
        Editor editor = createTextEditor(START_TEXT);
        if (editor != null) {
            panel.add((JComponent) editor, BorderLayout.NORTH);
            //            panel.add((JComponent)editor);
        }
        Editor startEditor = createStartEditor();
        if (startEditor != null) {
            panel.add((JComponent) startEditor, BorderLayout.SOUTH);
            //            panel.add((JComponent)startEditor);
        }
        return panel;
    }

    /**
     * Returns an Editor for setting the metrical start time. The Editor is given the
     * <code>startTime</code> as its <code>editObj</code>.
     * 
     * @return Editor editing the <code>startTime</code>
     */
    private Editor createStartEditor() {
        //create editor
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(startTime);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return editor;
    }

    /**
     * Returns a TextEditor for the specified String. The text font is set to
     * <code>TEXT_FONT</code>.
     * 
     * @param text String to be edited
     * @return Editor for the specified String
     */
    private Editor createTextEditor(String text) {
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(text, true, "Text", null);
            if (editor instanceof TextEditor) {
                ((TextEditor) editor).setTextFont(TEXT_FONT);
                ((JComponent) editor).setOpaque(false);
                ((TextEditor) editor).setAreaOpaque(false);
            }
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return editor;
    }

    /**
     * Sets the <code>epec</code> and the <code>notes</code>.
     * The method of NotesEpecEditor is overwritten in order to set the <code>context</code>
     * as the Context of the <code>notes</code>. The <code>notes</code> are metrically 
     * shifted by <code>startTime</code>.
     * 
     * @see de.uos.fmt.musitech.framework.editor.translating.AbstractTranslatingEditor#determineLocalObjs()
     */
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
        notes = new NoteList(context, epec);
        notes.metricalTimeShift(startTime);
    }

    /**
     * Reads the text from the <code>epecArea</code> and writes it to
     * <code>epec</code>, updates the <code>notes</code> to the new
     * <code>epec</code> and updates the <code>notesPreview</code>. The
     * method of NotesEpecEditor is overwritten in order to shift the
     * <code>notes</code> according to the <code>startTime</code> if
     * necessary and to insert the clef which is selected.
     */
    protected void updatePreview() {
        if (epecArea.getText().equals(""))
            return;
        epec = insertClef(epecArea.getText()); //TODO einkommentieren
//        epec = epecArea.getText(); // TODO auskommentieren
        notes.clear();
        notes.addAll(new NoteList(context, epec));
        if (startTime.isGreater(Rational.ZERO)) {
            notes.metricalTimeShift(startTime);
        }
//        if (notesPreview != null) {
//            notesPreview.updateDisplay();	//Taktstriche werden nicht mit verändert
//            revalidate();
//        } else {
            createNotesPreview();
            add(notesPreview, BorderLayout.CENTER);
            revalidate();
//        }
    }

    /**
     * Inserts a clef expression corresponding to the <code>clefType</code> to the
     * specified epec. If the <code>epec</code> already contains a clef expression that
     * one is removed. The clef type and the <code>epec</code> are combined in a 
     * StringBuffer whose String representation is returned. 
     * 
     * @param epec String containing information about the notes
     * @return String created from the specified <code>epec</code> by inserting a clef according to <code>clefType</code>
     */
    private String insertClef(String epec) {
        //convert clef to epec and insert
        Clef clef = new Clef(clefType);
        StringBuffer buffer = new StringBuffer();
        if (clef != null) {
            buffer.append('C');
            buffer.append(clef.getShape());
            buffer.append(':');
            if (clef.getLine()!=0)
                buffer.append(clef.getLine());
        }
        //if epec already contains clef expression, do not append that one
        int index = epec.indexOf('C');
        if (index != -1) {
            char shape = epec.charAt(index + 1);
            if (shape == 'g' || shape == 'f' || shape == 'c' || shape == 'p') {
                if (index > 0) {
                    //append up to clef (if clef is not first expression in epec)
                    buffer.append(epec.substring(0, index));
                }
                //leave out clef and append remaining epec
                int j = epec.indexOf(' ', index + 1);
                if (j != -1) {
                    buffer.append(epec.substring(j));
                }
            }
        } else {
            buffer.append(epec);
        }
        return buffer.toString();
    }

}