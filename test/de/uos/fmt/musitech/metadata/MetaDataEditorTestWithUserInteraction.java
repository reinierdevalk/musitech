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
 * Created on 25.11.2003
 *
 */
package de.uos.fmt.musitech.metadata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;

import de.uos.fmt.musitech.audio.AFOPlayer;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.media.image.ImageURL;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorWindow;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.Player;

/**
 * @author Kerstin Neubarth
 *
 */
public class MetaDataEditorTestWithUserInteraction {

	/**
	 * Private "helper" method to free the test methods from the code involved
	 * in creating the editor. 
	 * 
	 * @param editObj Object an editor is to be created for
	 * @param title String the EditorWindow is to show as its title
	 */
	private static void provideEditor(Object editObj, String title) {
		Editor editor = null;
		try {
			editor = EditorFactory.createEditor(editObj);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		EditorWindow window = new EditorWindow(title);
		window.addEditor(editor);
		window.show();
	}

	/**
	 * Private "helper" method returning a MetaDataCollection.
	 * As long as no special MetaDataCollection is required, this method
	 * may be used to provide an editObj for the MetaDataEditor to create.
	 * 
	 * @return MetaDataCollection
	 */
	private static MetaDataCollection createDefaultMetaDataCollection() {
		MetaDataCollection coll = null;
		MetaDataEditorTest test = new MetaDataEditorTest();
		coll = test.createCollection();
		return coll;
	}
	/**
	 * This window exits the program when closed.
	 * <br>
	 * <br> It is used since the EditorWindows do not do this when closed.
	 * @author Tobias Widdra
	 */
	static class ExitWindow extends javax.swing.JFrame {
		ExitWindow() {
			super("I exist for you to exit the program :o)");
			setSize(350, 0);
			addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				}
			});
		}
	}

	/**
	 * Tests the interaction of two MetaDataEditors editing the same MetaDataCollection.
	 * <br>
	 * If the MetaDataCollection in one editor is changed and the other editor gets
	 * the focus, the second editor is expected to tell the user that the 
	 * MetaDataCollection has been changed. 
	 */
	public static void testTwoEditorsWithSameCollection() {
		MetaDataCollection editObj = createDefaultMetaDataCollection();
		//1st editor
		provideEditor(editObj, "MetaDataEditor1 (shared editObj)");
		//2nd editor 
		provideEditor(editObj, "MetaDataEditor2 (shared editObj)");
	}
	
	/**
	 * Tests the MetaDataEditor with language specification. Two editors for the
	 * same MetaDataCollection are created, one for English, the other for German
	 * presentation.
	 */
	public static void testMetaDataEditorWithLanguage(){
	    MetaDataCollection editObj = createTestMDCollection();
	    Editor english = null;
	    Editor german = null;
	    try {
            english = EditorFactory.createEditor(editObj);
            if (english instanceof MetaDataEditor){
                ((MetaDataEditor)english).setLanguage("EN-GB");
                showEditor(english, "English");
            }      
            german = EditorFactory.createEditor(editObj);
            if (german instanceof MetaDataEditor){
                ((MetaDataEditor)german).setLanguage("DE-DE");
                showEditor(german, "German");
            }
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
	}
	
	private static MetaDataCollection createTestMDCollection(){
	    MetaDataCollection mdColl = new MetaDataCollection();
	    MetaDataValue valueComp = new MetaDataValue("string", "Beethoven");
	    MetaDataItem itemComp = new MetaDataItem("Composer");
	    itemComp.setMetaValue(valueComp);
	    mdColl.addMetaDataItem(itemComp);
	    MetaDataValue valueTitle = new MetaDataValue("string", "Sonata");
	    valueTitle.addValueForLanguage("Sonate", "DE-DE");
	    MetaDataItem itemTitle = new MetaDataItem("Title");
	    itemTitle.setMetaValue(valueTitle);
	    mdColl.addMetaDataItem(itemTitle);
	    MetaDataValue valueInstr = new MetaDataValue("string", "Piano");
	    valueInstr.addValueForLanguage("Klavier", "DE-DE");
	    MetaDataItem itemInstr = new MetaDataItem("Instrumentation");
	    itemInstr.setMetaValue(valueInstr);
	    mdColl.addMetaDataItem(itemInstr);
	    return mdColl;
	}
	
	private static void showEditor(Editor editor, String title){
	    JFrame frame = new JFrame(title);
//	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add((JComponent)editor);
	    frame.pack();
	    frame.setVisible(true);
	}
	
	/**
	 * Tests the MetaDataEditor offering to select a language.
	 */
	public static void testWithLanguageSelection(){
	    MetaDataCollection editObj1 = createMDColl1Language();
	    MetaDataCollection editObj2 = createMDColl3Languages();
	    try {
            Editor editor1 = EditorFactory.createEditor(editObj1);
            showEditor(editor1, "1 language only");
            Editor editor2 = EditorFactory.createEditor(editObj2);
            showEditor(editor2, "3 languages");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
	}
	
	private static MetaDataCollection createMDColl1Language(){
	    MetaDataCollection mdColl = new MetaDataCollection();
	    MetaDataItem itemComp = new MetaDataItem("Composer", "György Ligeti");
	    mdColl.addMetaDataItem(itemComp);
	    MetaDataItem itemTitle = new MetaDataItem("Title", "Hungarian Rock");
	    mdColl.addMetaDataItem(itemTitle);
	    MetaDataItem itemInstr = new MetaDataItem("Instrumentation", "Harpsichord");
	    mdColl.addMetaDataItem(itemInstr);
	    return mdColl; 
	}
	
	private static MetaDataCollection createMDColl3Languages(){
	    MetaDataCollection mdColl = new MetaDataCollection();
	    MetaDataItem itemComp = new MetaDataItem("Composer", "György Ligeti");
	    mdColl.addMetaDataItem(itemComp);
	    MetaDataItem itemTitle = new MetaDataItem("Title", "Hungarian Rock");
	    mdColl.addMetaDataItem(itemTitle);
	    MetaDataValue valueInstr = new MetaDataValue("string", "Harpsichord");
	    valueInstr.addValueForLanguage("Cembalo", "DE-de");
	    valueInstr.addValueForLanguage("Clavicembalo", "IT");
	    MetaDataItem itemInstr = new MetaDataItem("Instrumentation");
	    itemInstr.setMetaValue(valueInstr);
	    mdColl.addMetaDataItem(itemInstr);
	    return mdColl;
	}
	
	public static MetaDataCollection testWithImageData(){
	    MetaDataCollection mdColl = new MetaDataCollection();
//	    ImageURL imageUrl = new ImageURL(MetaDataEditorTestWithUserInteraction.class.getResource("http://www.uos.de/schlosspu.jpg"));
	    ImageURL imageUrl;
        try {
            imageUrl = new ImageURL(new URL("http://www.uos.de/schlosspu.jpg"));
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        }
        MetaDataValue value = new MetaDataValue("Image", imageUrl);
	    MetaDataItem item = new MetaDataItem("Image");
	    item.setMetaValue(value);
	    mdColl.addMetaDataItem(item);
	    try {
            Editor editor = EditorFactory.createEditor(mdColl);
            showEditor(editor, "Image Metadata");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return mdColl;
	}
	
	public static void testWithAudioData(){
	    MetaDataCollection mdColl = new MetaDataCollection();
	    AudioFileObject audio = null;
	    URL audioURL = MetaDataEditorTestWithUserInteraction.class.getResource("..//tabla01.wav");
	    try {
            audio = new AudioFileObject(audioURL);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        if (audio!=null){
            MetaDataValue valueWave = new MetaDataValue("WaveDisplay", audio);
            MetaDataItem itemWave = new MetaDataItem("Wave Form");
            itemWave.setMetaValue(valueWave);
            mdColl.addMetaDataItem(itemWave);
            MetaDataValue valuePlayer = new MetaDataValue("Player", audio);
            MetaDataItem itemPlayer = new MetaDataItem("Sound");
            itemPlayer.setMetaValue(valuePlayer);
            mdColl.addMetaDataItem(itemPlayer);
        }
        if (mdColl.size()>0){
            try {
                Editor editor = EditorFactory.createEditor(mdColl);
                showEditor(editor, "Audio Metadata");
            } catch (EditorConstructionException e) {
                e.printStackTrace();
            }
        }
	}

	/**
	 * Method for running the test methods.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		new ExitWindow().show();
//		testTwoEditorsWithSameCollection();
//		testMetaDataEditorWithLanguage();
//		testWithLanguageSelection();
		testWithImageData();
		testWithAudioData();
	}
}
