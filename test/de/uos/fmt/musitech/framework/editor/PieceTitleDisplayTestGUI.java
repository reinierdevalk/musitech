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
 * Created on 08.11.2004
 *
 */
package de.uos.fmt.musitech.framework.editor;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataProfile;
import de.uos.fmt.musitech.data.metadata.MetaDataProfileManager;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;

/**
 * Class for testing PieceTitleDisplay.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class PieceTitleDisplayTestGUI {

    /**
     * Returns a Piece with MetaMap for testing a PieceTitleDisplay.
     * 
     * @return Piece
     */
    private static Piece createPiece() {
        Piece piece = new Piece();
        piece.getMetaMap().put(piece, createMetaDataPiece());
        return piece;
    }

    /**
     * Returns a MetaDataCollection with MetaDataProfile "Piece" and MetaDataItems
     * matching that profile.
     * 
     * @return MetaDataCollection
     */
    private static MetaDataCollection createMetaDataPiece() {
        MetaDataCollection mdColl = new MetaDataCollection();
        MetaDataItem mdItemComposer = new MetaDataItem("Composer");
//        MetaDataValue mdValueComposer = new MetaDataValue("string",
//                "Wolfgang Amadeus Mozart");
        MetaDataValue mdValueComposer = new MetaDataValue("string",
              "Mozart, Wolfgang Amadeus");
        mdItemComposer.setMetaValue(mdValueComposer);
        mdColl.addMetaDataItem(mdItemComposer);
        MetaDataItem mdItemTitle = new MetaDataItem("Title");
        MetaDataValue mdValueTitle = new MetaDataValue("string", "Sonate A-dur");
        mdItemTitle.setMetaValue(mdValueTitle);
        mdColl.addMetaDataItem(mdItemTitle);
        MetaDataItem mdItemOpus = new MetaDataItem("Opus/Catalogue Number");
        MetaDataValue mdValueOpus = new MetaDataValue("string", "KV 331");
        mdItemOpus.setMetaValue(mdValueOpus);
        mdColl.addMetaDataItem(mdItemOpus);
        MetaDataItem mdItemYear = new MetaDataItem("Year of publication");
        MetaDataValue mdValueYear = new MetaDataValue("string", "1778");
        mdItemYear.setMetaValue(mdValueYear);
        mdColl.addMetaDataItem(mdItemYear);
        MetaDataItem mdItemInstrument = new MetaDataItem("Instrumentation");
        MetaDataValue mdValueInstrument = new MetaDataValue("string", "Klavier");
        mdItemInstrument.setMetaValue(mdValueInstrument);
        mdColl.addMetaDataItem(mdItemInstrument);
        MetaDataItem mdItemMvt = new MetaDataItem("Movement");
        MetaDataValue mdValueMvt = new MetaDataValue("string", "Andante grazioso");
        mdItemMvt.setMetaValue(mdValueMvt);
        mdColl.addMetaDataItem(mdItemMvt);
        MetaDataProfile mdProfile = MetaDataProfileManager
                .getMetaDataProfile("Piece");
        mdColl.addProfile(mdProfile);
        return mdColl;
    }

    /**
     * Returns a Display for the specified piece. The display type is specified as
     * "PieceTitle" and the PieceTitleDisplay is registered at the EditorRegistry.    
     *  
     * @param piece Piece for which a Display to create
     * @return Display for the specified Piece
     */
    private static Display createDisplay(Piece piece) {
        if (piece == null)
            return null;
        //register PieceTitleDisplay at EditorRegistry
        EditorType editorType = new EditorType("PieceTitle",
                PieceTitleDisplay.class.getName(), null);
        EditorRegistry.registerEditortypeForClass(Piece.class.getName(),
                editorType);
        //create Display
        Display display = null;
        try {
            display = EditorFactory.createDisplay(piece, null, "PieceTitle");
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return display;
    }

    /**
     * Shows the specified Display in a JFrame.
     * 
     * @param display Display to be shown
     */
    private static void showDisplay(Display display) {
        if (display != null) {
            JFrame frame = new JFrame("Test PieceTitleDisplay");
            frame.getContentPane().add((JComponent) display);
            frame.pack();
            frame.setVisible(true);
        }
    }

    /**
     * Sets up a PieceTitleDisplay and shows it in a JFrame. 
     *
     */
    private static void testPieceTitleDisplay() {
        Piece piece = createPiece();
        Display display = createDisplay(piece);
        showDisplay(display);
    }
    
    /**
     * Opens a JFrame with default close operation EXIT.
     */
    private static void forClosing(){
        JFrame frame = new JFrame("For closing");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * For running the test method.
     * 
     * @param args
     */
    public static void main(String[] args) {
        forClosing();
        testPieceTitleDisplay();
    }
}