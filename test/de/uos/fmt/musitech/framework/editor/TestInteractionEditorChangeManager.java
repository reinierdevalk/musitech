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
 * Created on 19.09.2003
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllableSequence;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * JUnit test for interaction between EditorFramework and DataChangeManager
 * 
 * @author Kerstin Neubarth
 *  
 */
public class TestInteractionEditorChangeManager {

    /**
     * For testing if an editor handels DataChangeEvents in the way it is
     * supposed to do. Therefore, two editors are created sharing some objects
     * to be edited. Here, the "main" editObjs are a Collection and a Map, which
     * share some elements.
     */
    public void testEditorReactionWithSharedElements() {
        //elements for both editObjs
        String string1 = "Element 1";
        ScoreNote scoreNote1 = new ScoreNote();
        PerformanceNote performanceNote1 = new PerformanceNote();
        //editObj for 1st editor
        Vector editObj1 = new Vector();
        editObj1.add(string1);
        editObj1.add(scoreNote1);
        editObj1.add(performanceNote1);
        editObj1.add(new Integer(25));
        //editObj for 2nd editor
        Hashtable editObj2 = new Hashtable();
        editObj2.put("Note1", new Note(scoreNote1, performanceNote1));
        editObj2.put("String1", string1);
        editObj2.put("String2", "AnotherString");
        //1st editor
        provideTestEditor(editObj1, "Vector with 4 elements");
        //2nd editor
        provideTestEditor(editObj2, "Hashmap with 3 elements");
    }

    /**
     * For testing two complex editors which do not have the same editObjs but
     * whose editObjs share one property respectively. <br>
     * Three editors editing Note objects are generated. Two notes share their
     * PerformanceNotes, two notes share their ScoreNotes.
     */
    public void testEditorReactionWithSharedProperties() {
        //shared objects
        ScoreNote scoreNote1 = new ScoreNote();
        PerformanceNote perfNote1 = new PerformanceNote();
        //editObj for 1st editor
        Note note1 = new Note(scoreNote1, new PerformanceNote());
        //editObj for 2nd editor
        Note note2 = new Note(scoreNote1, perfNote1);
        //editObj for 3rd editor
        Note note3 = new Note(new ScoreNote(), perfNote1);
        //1st editor
        provideTestEditor(note1, "Note with shared ScoreNote");
        //2nd editor
        provideTestEditor(note2, "Note with both properties shared");
        //3rd editor
        provideTestEditor(note3, "Note with shared PerformanceNote");
    }

    /**
     * For testing if an editor is known as DataChangeListener to the
     * DataChangeManager and if it is sending a ChangeEvent when "OK" or "Apply"
     * have been activated
     */
    public void testEditorSendingChangeEvent() {
        provideTestEditor(new PerformanceNote(), "Test sending ChangeEvent");
        /*
         * Debugging ergibt: Editor meldet sich bei DataChangeManager an editObj
         * (PerformanceNote) wird in table des ChangeManagers eingetragen
         * 
         * bei apply im Editor: ChangeEvent wird ausgelöst; Methode changed des
         * DataChangeManager wird aufgerufen ; views in
         * DataChangeManager.changed() enthält PanelEditor, 2 intEditors und
         * LongEditor (= editor and children editors); Editor bekommt
         * ChangeEvent, changed-Methode wird aufgerufen, dataChanged auf true
         * gesetzt
         */
    }

    /**
     * For testing if an editor is receiving a DataChangeEvent if it gaines the
     * focus. Therefore, a second editor is provided, to be able to change focus
     * between two editors.
     */
    public void testEditorReceivingChangeEvent() {
        provideTestEditor(new PerformanceNote(), "Editor to be tested");
        provideTestEditor(new ScoreNote(), "Editor for changing focus");
        /*
         * Wenn source-check des DataChangeEvent auskommentiert: JDialog mit
         * Nachricht und Frage erscheint, wenn erneut Focus. Wenn
         * source-Vergleich nicht auskommentiert: Keine Meldung, wenn erneut
         * Focus.
         */
    }

    /**
     * For testing if an editor is receiving a DataChangeEvent if the object it
     * edits has been changed in another editor. Therefore, two editors with the
     * same editObj are created.
     */
    public void testEditorReceivingChangeEventWithSameObject() {
        ScoreNote scoreNote = new ScoreNote();
        provideTestEditor(scoreNote, "Editor 1");
        provideTestEditor(scoreNote, "Editor 2");
    }

    /**
     * For testing if the editor notices that it gaines or looses the focus.
     */
    public void testFocusEventsInEditor() {
        provideTestEditor(new Note(new ScoreNote(), new PerformanceNote()),
                "Note Editor with 2 children PanelEditors");
    }

    /**
     * For testing if an editor is sending and receiving a DataChangeEvent if an
     * element of the collection it edits is or has been changed. Therefore, two
     * CollectionEditors are creating sharing some of their elements.
     */
    public void testWithSharedElementsForBothCollections() {
        //elements for both editObjs
        String string1 = "Element 1";
        ScoreNote s1 = new ScoreNote();
        PerformanceNote p1 = new PerformanceNote();
        //editObj for 1st editor
        Vector editObj1 = new Vector();
        editObj1.add(string1);
        editObj1.add(s1);
        editObj1.add(p1);
        editObj1.add(new Integer(25));
        //editObj for 1st editor
        Vector editObj2 = new Vector();
        editObj2.add("String2");
        editObj2.add(s1);
        editObj2.add(p1);
        editObj2.add(new Note(s1, p1));
        //editors
        provideTestEditor(editObj1, "CollectionEditor 1");
        provideTestEditor(editObj2, "CollectionEditor 2");
    }

    /**
     * For testing editors sending and receiving DataChangeEvents if an element
     * of the collection is deleted, replaced or added, that is if the
     * collection as a whole has been changed.
     */
    public void testTwoEditorsWithSameCollection() {
        //elements for both collections
        int max = 2;
        BasicContainer elements = new BasicContainer();
        for (int i = 0; i < max; i++) {
            elements.add(new PerformanceNote(100 * i));
        }
        //editors for the collections
        provideTestEditor(elements, "CollectionEditor1");
        provideTestEditor(elements, "CollectionEditor2");
    }

    /**
     * For testing the FocusListeners added to the components of the
     * EditorPanel. If the editPanel or one of the buttons is selected, the user
     * is informed if data has been changed in another editor.
     *  
     */
    public void testFocusListenersInEditorPanel() {
        PerformanceNote perfNote = new PerformanceNote();
        provideTestEditor(perfNote, "Editor 1");
        provideTestEditor(perfNote, "Editor 2");

        //Testing21/10/03: o.k.
    }

    /**
     * For testing if changing the focus to a component of this editor yields in
     * setting dataChanged true (it should not).
     */
    public void testDataChangedInOneCollectionEditor() {
        int max = 2;
        BasicContainer elements = new BasicContainer();
        for (int i = 0; i < max; i++) {
            elements.add(new PerformanceNote(100 * i));
        }
        //editor for the collection
        provideTestEditor(elements, "CollectionEditor");
    }

    /**
     * For testing if gaining the focus is registered.
     */
    public void testFocusListenerInSimpleEditor() {
        provideTestEditor("Text", "Simple Editor");
    }

    /**
     * For testing the editor update. An ExpandEditor is chosen to check if the
     * data update shows itself in the graphics when collapsing and again
     * expanding the editor contained.
     */
    public void testUpdateInEditorPanel() {
        //		provideTestEditor(new PerformanceNote(), "Test Editor Update");
        //		ExpandEditor editor = new ExpandEditor();
        //		editor.construct(new PerformanceNote(), null, null, "Performance
        // Note");
        Editor editor = null;
        EditingProfile prof = new EditingProfile(null, "Expand",
                "PerformanceNote");
        try {
            editor = EditorFactory.createEditor(new PerformanceNote(), prof);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        EditorWindow w = new EditorWindow(
                "Test ExpandEditor For EditorPanel Update");
        w.addEditor(editor);
        w.show();
    }

    /**
     * For testing the update of the CollectionEditor and its children and
     * element editors after editing a property or element.
     */
    public void testUpdateInCollectionEditor() {
        //elements for both editObjs
        String string1 = "Element 1";
        ScoreNote s1 = new ScoreNote();
        PerformanceNote p1 = new PerformanceNote();
        //editObj for 1st editor
        Vector editObj1 = new Vector();
        editObj1.add(string1);
        editObj1.add(s1);
        editObj1.add(p1);
        editObj1.add(new Integer(25));
        //editor for editObj
        provideTestEditor(editObj1, "CollectionEditor TestUpdate");
    }

    /**
     * For testing if the editorToPopUp in a PreviewEditor is updated.
     */
    public void testUpdateInPreviewEditor() {
        TestEditorFactory.testPreviewEditorWithProperty();

        //This method was motivated because the PanelEditor-Update
        //after receiving a DataChangeEvent had not been working correctly.
        //Updating in reaction to changed data has been corrected in the
        // meantime.
    }

    /**
     * For testing if a simple editor does recognize its own DataChangeEvents.
     * If it does, it should not show a dialog informing about changed data when
     * the apply button is activated and the focus is again set to one of its
     * components.
     */
    public void testSimpleEditorCheckingChangeEventSource() {
        provideTestEditor(new Rational(3, 4), "RationalEditor source check");
        //when testing: no dialog is shown
    }

    /**
     * For testing if a PanelEditor does recognize its own DataChangeEvents,
     * that is DataChangeEvents sent by itself or one of its children. If it
     * does, it should not show a dialog informing about changed data when the
     * apply button is activated and the focus is again set to one of its
     * components.
     */
    public void testComplexEditorCheckingChangeEventSource() {
        provideTestEditor(new PerformanceNote(), "PanelEditor source check");
        //when testing: no dialog is shown - o.k.
    }

    /**
     * For testing if a PanelEditor does recognize a DataChangeEvent coming from
     * another editor operating on the same object. Therefore, two PanelEditors
     * with the same object <code>scoreNote1</code> are generated.
     */
    public void testTwoPanelEditorsWithSameObject() {
        ScoreNote scoreNote1 = new ScoreNote();
        provideTestEditor(scoreNote1, "ScoreNote Editor1");
        provideTestEditor(scoreNote1, "ScoreNote Editor2");
        //Testing 21/10/03: o.k.
        //(not yet checked if values really have changed)
    }

    /**
     * For testing if a complex editor does recognize DataChangeEvents coming
     * from one of its complex children or their children. This method is
     * similar to method
     * <code>testComplexEditorCheckingChangeEventSource()</code>.
     */
    public void testPanelEditorWithComplexChildrenForSourceCheck() {
        provideTestEditor(new Note(new ScoreNote(), new PerformanceNote()),
                "Source check with complex children");
    }

    /**
     * For testing the application at the DataChangeManager with simple objects
     * to edit.
     * 
     * Note: Method <code>testEditorReactionWithSharedProperties()</code> has
     * shown a DataChangeEvent even in editors not sharing the same object but
     * equal values in the objects' simple properties: Editor 1 and Editor 2 are
     * both editing objects of type Note. They both have their own properties
     * ScoreNote and PerformanceNote (that is, different instances of classes
     * ScoreNote and PerformanceNote). But in both cases, the default
     * constructor is used to create these properties resulting in the same
     * numerical values for the fields like pitch, accidental etc. When editing
     * one of the fields of ScoreNote in Editor 1 and then changing to Editor 2,
     * a JDialog is opened informing that data of Editor 2 has been changed.
     * Therefore, an error must occur either in the editors' applying at the
     * DataChangeManager or in setting the variable <code>dataChanged</code>
     * in the editors. When debugging the <code>table</code> of the
     * DataChangeManager does contain the simple fields (diatonic, accidental
     * etc.) only once as a key with the corresponding children editors of both
     * Editor 1 and Editor 2. So, in this method the problem has been reduced to
     * simple editors. Again, <code>table</code> has one entry only with key
     * "string" and value HashSet of size 3 with the three StringEditors.
     *  
     */
    public void testInterestExpandInChangeManager() {
        String string1 = "string";
        provideTestEditor(string1, "Editor 1 with string1");
        provideTestEditor(string1, "Editor 2 with string1");
        provideTestEditor("string", "Editor 3 with new string");

        //after changing table to type IdentityHashMap:
        //When all three editors have applied at the DataChangeManager,
        //table contains 1 entry with key "string" and value HashSet of size 3
        //(with the three StringEditors).
    }

    /**
     * For testing the application at the DataChangeManager with simple objects
     * to edit. Similar to method
     * <code>testInterestExpandInChangeManager()</code>.
     */
    public void testInterestExpandWithIntegers() {
        Integer int1 = new Integer(25);
        if (int1 == new Integer(25))
            System.out.println("int1 == new Integer(25)");
        provideTestEditor(int1, "Editor 1 with int1");
        provideTestEditor(new Integer(25), "Editor 2");
        provideTestEditor(int1, "Editor 3 with int1");

        //after changing table to type IdentityHashMap:
        //When all three editors have applied at the DataChangeManager,
        //table contains 2 entries, in one case the key leads to 2 editors.
    }

    /**
     * For testing the application at the DataChangeManager with simple objects
     * to edit. Similar to method
     * <code>testInterestExpandInChangeManager()</code>.
     */
    public void testInterestExpandWithStrings() {
        String string1 = "string";
        String string2 = "string";
        if (string1 == string2) //is true
            System.out.println("string1 == string2");
        provideTestEditor(string1, "Editor1 with string1");
        provideTestEditor(string2, "Editor 2 with string2");
        provideTestEditor(string1, "Editor3 with string1");

        //after changing table to type IdentityHashMap:
        //When all three editors have applied at the DataChangeManager,
        //table contains 1 entry with key "string" and value HashSet of size 3
        //(with the three StringEditors).
    }

    /**
     * For testing the application at the DataChangeManager with simple objects
     * to edit. Similar to method
     * <code>testInterestExpandInChangeManager()</code>.
     */
    public void testInterestExpandWithCharacters() {
        Character char1 = Character.valueOf('c');
        Character char2 = Character.valueOf('c');
        if (char1 == char2)
            System.out.println("char1 == char2");
        provideTestEditor(char1, "Editor 1 with char1");
        provideTestEditor(char2, "Editor2 with char2");
        provideTestEditor(char1, "Editor3 with char1");

        //after changing table to type IdentityHashMap:
        //When all three editors have applied at the DataChangeManager,
        //table contains 2 entries, in one case the key leads to 2 editors.
    }

    /**
     * For testing the application at the DataChangeManager with simple objects
     * to edit. Similar to method
     * <code>testInterestExpandInChangeManager()</code>. But here, two String
     * objects are created separately.
     */
    public void testInterestExpandWithStrings2() {
        String string1 = new String("string");
        String string2 = new String("string");
        if (string1 == string2) //is false
            System.out.println("string1 == string2");
        provideTestEditor(string1, "Editor1 with string1");
        provideTestEditor(string2, "Editor 2 with string2");
        provideTestEditor(string1, "Editor3 with string1");

        //after changing table to type IdentityHashMap:
        //When all three editors have applied at the DataChangeManager,
        //table contains 2 entries, in one case the key leads to 2 editors.
    }

    /**
     * For testing two complex editors which do not have the same editObjs but
     * whose editObjs share one property respectively. <br>
     * Three editors editing Note objects are generated. Two notes share their
     * PerformanceNotes, two notes share their ScoreNotes. <br>
     * While method <code>testEditorReactionWithSharedProperties()</code> uses
     * the default editortypes for the children editors (which for complex
     * editors is the PopUpEditor), this methods chooses PanelEditors for
     * children editors.
     */
    public void testEditorReactionWithSharedProperties2() {
        //shared objects
        ScoreNote scoreNote1 = new ScoreNote();
        PerformanceNote perfNote1 = new PerformanceNote();
        //editObj for 1st editor
        Note note1 = new Note(scoreNote1, new PerformanceNote());
        //editObj for 2nd editor
        Note note2 = new Note(scoreNote1, perfNote1);
        //editObj for 3rd editor
        Note note3 = new Note(new ScoreNote(), perfNote1);
        //1st editor
        provideTestEditor2(note1, "Note with shared ScoreNote");
        //2nd editor
        provideTestEditor2(note2, "Note with both properties shared");
        //3rd editor
        provideTestEditor2(note3, "Note with shared PerformanceNote");
    }

    /**
     * Tests a Display automatically updating to external data changes.
     * Therefore, a Display and an Editor are created for the same
     * <code>editObj</code>. When data is changed in the editor and then the
     * Display gets the focus, the Display updates automatically to the new
     * values.
     */
    public void testAutomaticUpdateInDisplay() {
        //shared editObj
        ScoreNote scoreN = new ScoreNote();
        //Editor which can change the editObj
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(scoreN);
            EditorWindow w = new EditorWindow("Editor for changing data");
            w.addEditor(editor);
            w.show();
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        //Display which must update automatically
        Display display = null;
        try {
            display = EditorFactory.createDisplay(scoreN, null);
            JFrame frame = new JFrame("Display for testing update");
            frame.getContentPane().add((JComponent) display);
            frame.setSize(200, 200);
            frame.setVisible(true);
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the update of Editors depending on the setting of
     * <code>promptUpdate</code>. Therefore, two Editors are generated. The
     * first is expected to update automatically (i.e.,
     * <code>promptUpdate</code> is false which is the default), the second
     * should notifiy the user before updating (i.e., <code>promptUpdate</code>
     * is set true).
     */
    public void testPromptUpdate() {
        //shared editObj
        ScoreNote scoreN = new ScoreNote();
        //Editor which is to update automatically
        Editor editor1 = null;
        //Editor which should ask before updating
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(scoreN);
            EditorWindow w1 = new EditorWindow("Editor with automatic update");
            w1.addEditor(editor1);
            w1.show();
            editor2 = EditorFactory.createEditor(scoreN);
            editor2.setPromptUpdate(true);
            EditorWindow w2 = new EditorWindow("Editor with dialog");
            w2.addEditor(editor2);
            w2.show();
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests setting the <code>dirty</code> flag when changing data and
     * checking the flag before updating an editor. Therefore, two editors
     * editing the same object are generated. For testing, change data in the
     * "Editor for testing" without "apply", then change to the other editor and
     * apply some changes to the <code>editObj</code>. When you then change
     * back to the first editor, a dialog is shown offering to accept or
     * overwrite changes.
     */
    public void testUpdateWhenDirty() {
        //shared editObj
        ScoreNote scoreN = new ScoreNote();
        //Editor which is to update automatically
        Editor editor1 = null;
        //Editor which should ask before updating
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(scoreN);
            EditorWindow w1 = new EditorWindow(
                    "Editor for changing data externally");
            w1.addEditor(editor1);
            w1.show();
            editor2 = EditorFactory.createEditor(scoreN);
            EditorWindow w2 = new EditorWindow("Editor for testing update");
            w2.addEditor(editor2);
            w2.show();
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests DataChangeEvents when a new "simple" property has been created. The
     * editors update automatically.
     */
    public void testUpdateForNewSimpleValue() {
        //editObj with null-property
        ScoreNote score1 = new ScoreNote();
        //editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(score1);
            EditorWindow w1 = new EditorWindow("Editor1 with null-property");
            w1.addEditor(editor1);
            w1.show();
            editor2 = EditorFactory.createEditor(score1);
            EditorWindow w2 = new EditorWindow("Editor2 with null-property");
            w2.addEditor(editor2);
            w2.show();
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    /**
     * For testing DataChangeEvents when a new complex property has been
     * created. The editors prompt before updating.
     */
    public void testUpdateForNewComplexValue() {
        //editObj with complex null-property
        Note note1 = new Note(new ScoreNote(), null);
        //editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(note1);
            editor1.setPromptUpdate(true);
            EditorWindow w1 = new EditorWindow(
                    "Editor1 with complex null-property");
            w1.addEditor(editor1);
            w1.show();
            editor2 = EditorFactory.createEditor(note1);
            editor2.setPromptUpdate(true);
            EditorWindow w2 = new EditorWindow(
                    "Editor2 with complex null-property");
            w2.addEditor(editor2);
            w2.show();
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    /**
     * For testing DataChangeEvents in Editors with complex children without
     * complex null properties. <br>
     * This test method was motivated by a failure in method
     * <code>testUpdateForNewComplexValue()</code> in order to locate the
     * source of failure.
     */
    public void testPromptWithComplexChildren() {
        //shared editObj
        Note note = new Note(new ScoreNote(), new PerformanceNote());
        //editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(note);
            editor1.setPromptUpdate(true);
            EditorWindow w1 = new EditorWindow("Editor1 with complex children");
            w1.addEditor(editor1);
            w1.show();
            editor2 = EditorFactory.createEditor(note);
            editor2.setPromptUpdate(true);
            EditorWindow w2 = new EditorWindow("Editor2 with complex children");
            w2.addEditor(editor2);
            w2.show();
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests DataChangeEvents when a new "simple" property has been created. The
     * editors prompt before updating. If the user chooses to overwrite the
     * newly created property by the editor still having the null value, the
     * property of the <code>editObj</code> is reset to null.
     */
    public void testPromptForNewSimpleValue() {
        //editObj with null-property
        ScoreNote score1 = new ScoreNote();
        //editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(score1);
            editor1.setPromptUpdate(true);
            EditorWindow w1 = new EditorWindow("Editor1 with null-property");
            w1.addEditor(editor1);
            w1.show();
            editor2 = EditorFactory.createEditor(score1);
            editor2.setPromptUpdate(true);
            EditorWindow w2 = new EditorWindow("Editor2 with null-property");
            w2.addEditor(editor2);
            w2.show();
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    public void testWithRational() {
        Rational rational = new Rational(3, 4);
        provideTestEditor(rational, "Editor 1");
        provideTestEditor(rational, "Editor 2");
    }

    public void testTwoStringBufferEditors() {
        //register StringBufferEditor at EditorRegistry
        EditorType editorType = new EditorType("StringBuffer",
                StringBufferEditor.class.getName(), null);
        EditorRegistry.registerEditortypeForClass(StringBuffer.class.getName(),
                editorType);
        //provide editObj
        StringBuffer buffer = new StringBuffer();
        buffer.append("This is a StringBufferEditor.\n");
        buffer.append("Test data changes.");
        //create two editors for same editObj
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(buffer, null, "StringBuffer");
            editor1.setPromptUpdate(true);
            showEditor(editor1, "Prompt update");
            editor2 = EditorFactory.createEditor(buffer, null, "StringBuffer");
            showEditor(editor2, "Automatic update");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    public void testTwoExpandEditors() {
        //create editObj
        ScoreNote sn = new ScoreNote();
        sn.setMetricTime(new Rational(1, 4));
        sn.setMetricDuration(new Rational(1, 2));
        //create editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(sn, null, "Expand");
            editor1.setPromptUpdate(true);
            showEditor(editor1, "Expand prompt");
            editor2 = EditorFactory.createEditor(sn, null, "Expand");
            showEditor(editor2, "Expand automatic");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    public void testTwoCollectionEditors() {
        //create editObj (Collection)
        Collection editObj = new ArrayList();
        //fill Collection
//        editObj.add(new ScorePitch(60));
//        editObj.add(new ScorePitch(62));
//        editObj.add(new ScorePitch(64));
        
        editObj.add(new ScoreNote());
        editObj.add(new PerformanceNote());
        //create and show editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(editObj, null,
                    "Collection");
            editor1.setPromptUpdate(true);
            showEditor(editor1, "Collection prompt");
            editor2 = EditorFactory.createEditor(editObj, null,
                    "Collection");
            showEditor(editor2, "Collection automatic");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }
    
    public void testTwoMapEditors(){
        //create editObj
        HashMap editObj = new HashMap();
        editObj.put("Score note", new ScoreNote());
        editObj.put("Performance note", new PerformanceNote());
        //create and show editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(editObj, null,
                    "Map");
            editor1.setPromptUpdate(true);
            showEditor(editor1, "Map prompt");
            editor2 = EditorFactory.createEditor(editObj, null,
                    "Map");
            showEditor(editor2, "Map automatic");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }

    public void testTwoTypedCollectionEditors() {
        //create editObj (TypedCollection)
        LyricsSyllableSequence lyrics = new LyricsSyllableSequence();
        lyrics.add(new LyricsSyllable(Rational.ZERO, "Syl-"));
        lyrics.add(new LyricsSyllable(new Rational(1, 4), "la-"));
        lyrics.add(new LyricsSyllable(new Rational(2, 4), "ble"));
        //create and show editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(lyrics, null,
                    "TypedCollection");
            editor1.setPromptUpdate(true);
            showEditor(editor1, "TypedCollection prompt");
            editor2 = EditorFactory.createEditor(lyrics, null,
                    "TypedCollection");
            showEditor(editor2, "TypedCollection automatic");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
    }
    
    private void testWithScorePitch(){
        //create editObj
        ScorePitch editObj = new ScorePitch(60);
        //create editors
        provideTestEditor(editObj, "Editor 1");
        provideTestEditor(editObj, "Editor 2");
    }
    
    private void testWithNullProperty(){
        //create editObj
        LyricsSyllable editObj = new LyricsSyllable();
        editObj.setText(null);
        //create editors
        Editor editor1 = null;
        Editor editor2 = null;
        try {
            editor1 = EditorFactory.createEditor(editObj, null, "Panel");
            editor1.setPromptUpdate(true);
            EditorWindow window1 = new EditorWindow("Create with prompt");
            window1.addEditor(editor1);
            window1.setVisible(true);
            editor2 = EditorFactory.createEditor(editObj, null, "Panel");
            EditorWindow window2 = new EditorWindow("Create automatic");
            window2.addEditor(editor2);
            window2.setVisible(true);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * Private "helper" method creating an editor for the specified editObj.
     * <br>
     * In this way, the "real" test methods need not care for the creation of
     * the editor(s) themselves.
     * 
     * @param editObj
     *            Object for which an editor is to be created (and shown)
     * @param windowTitle
     *            String displayed on top of the EditorWindow
     */
    private void provideTestEditor(Object editObj, String windowTitle) {
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(editObj);
        } catch (EditorConstructionException e) {
            System.err.println(e.getMessage());
        }
        EditorWindow w = new EditorWindow(windowTitle);
        w.addEditor(editor);
        w.show();
    }

    /**
     * Private "helper" method creating an editor for the specified editObj.
     * This method should be used instead of
     * <code>provideTestEditor(Object, String)</code> if children editors
     * should be PanelEditors. <br>
     * In this way, the "real" test methods need not care for the creation of
     * the editor(s) themselves.
     * 
     * @param editObj
     *            Object for which an editor is to be created (and shown)
     * @param windowTitle
     *            String displayed on top of the EditorWindow
     */
    private void provideTestEditor2(Object editObj, String windowTitle) {
        EditingProfile profile = null;
        if (editObj instanceof Editable)
            profile = ((Editable) editObj).getEditingProfile();
        else
            profile = EditorFactory.createDefaultProfile(editObj);
        if (profile != null) {
            EditingProfile[] childrenProfiles = profile.getChildren();
            if (childrenProfiles != null && childrenProfiles.length > 0) {
                for (int i = 0; i < childrenProfiles.length; i++)
                    childrenProfiles[i].setEditortype("Panel");
            }
        }
        Editor editor = null;
        try {
            editor = EditorFactory.createEditor(editObj, profile);
        } catch (EditorConstructionException e) {
            System.err.println(e.getMessage());
        }
        EditorWindow w = new EditorWindow(windowTitle);
        w.addEditor(editor);
        w.show();
    }

    private static void showEditor(Editor editor, String windowTitle) {
        EditorWindow window = new EditorWindow(windowTitle);
        window.addEditor(editor);
        window.pack();
        window.setVisible(true);
    }

    /**
     * main method for testing user interaction.
     * 
     * @param args
     */
    public static void main(String[] args) {
        /*
         * Framework musitech = new BasicPeer();
         * musitech.getDataChangeManager();
         */

        new TestEditorFactory.ExitWindow().show();
        TestInteractionEditorChangeManager testIEC = new TestInteractionEditorChangeManager();
        //		testIEC.testEditorSendingChangeEvent();
        //		testIEC.testEditorReceivingChangeEvent();
        //		testIEC.testEditorReceivingChangeEventWithSameObject();
        //		testIEC.testFocusEventsInEditor();
        //				testIEC.testEditorReactionWithSharedElements(); //19.11.03 o.k.
        //		testIEC.testEditorReactionWithSharedProperties(); //21/10/03: o.k.
        // Retesting 18.11.03 o.k.
        //		testIEC.testWithSharedElementsForBothCollections(); //19.11.03 o.k.
        //						testIEC.testTwoEditorsWithSameCollection(); //19.11.03 o.k. //TODO
        //		testIEC.testFocusListenersInEditorPanel(); //21/10/03: o.k.
        //				testIEC.testDataChangedInOneCollectionEditor(); //
        //		testIEC.testFocusListenerInSimpleEditor();
        //		testIEC.testUpdateInEditorPanel(); //21/10/03: o.k.
        //		testIEC.testUpdateInCollectionEditor(); //21/10/03: o.k.
        //		testIEC.testUpdateInPreviewEditor(); //21/10/03: o.k.
        //				testIEC.testSimpleEditorCheckingChangeEventSource(); //21/10/03: o.k.
        // Retesting 18.11.03: o.k.
        //						testIEC.testComplexEditorCheckingChangeEventSource(); //21/10/03:
        // o.k. Retesting 18.11.03: o.k.
        //						testIEC.testTwoPanelEditorsWithSameObject(); //21/10/03: o.k.
        // Retesting 18.11.03 o.k.
        //				testIEC.testPanelEditorWithComplexChildrenForSourceCheck();
        // //21/10/03: o.k. Retesting 18.11.03: o.k.
        //		testIEC.testInterestExpandInChangeManager(); //see description in
        // method
        //		testIEC.testInterestExpandWithIntegers(); //see description in method
        //		testIEC.testInterestExpandWithStrings(); //see description in method
        //		testIEC.testInterestExpandWithCharacters(); //see description in
        // method
        //		testIEC.testInterestExpandWithStrings2(); //see description in method
        //		testIEC.testEditorReactionWithSharedProperties2();
        //				testIEC.testAutomaticUpdateInDisplay();
        				testIEC.testPromptUpdate();
        //		testIEC.testUpdateWhenDirty();
        //		testIEC.testUpdateForNewSimpleValue();
        //		testIEC.testUpdateForNewComplexValue();
        //		testIEC.testPromptWithComplexChildren();
        //		testIEC.testPromptForNewSimpleValue();
        //		testIEC.testWithRational();
        //		testIEC.testTwoStringBufferEditors();
        //		testIEC.testTwoExpandEditors();
//        testIEC.testTwoCollectionEditors();
//        testIEC.testTwoMapEditors();
        testIEC.testTwoTypedCollectionEditors();
//        testIEC.testWithScorePitch();
        testIEC.testWithNullProperty();
    }

}