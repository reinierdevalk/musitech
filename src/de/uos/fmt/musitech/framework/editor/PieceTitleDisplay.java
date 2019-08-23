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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTextArea;

import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataProfile;
import de.uos.fmt.musitech.data.structure.Piece;

/**
 * A PieceTitleDisplay is a display which shows the title of a Piece. The title
 * is extracted from the Piece's metadata and gives the composer's name and the
 * title and opus resp. catalogue number of the composition. If the MetaMap of
 * the Piece does not contain metadata about composer and composition, the
 * Piece's <code>name</code> is displayed. <br>
 * The PieceTitleDisplay gets a Piece as its <code>editObj</code> or
 * <code>propertyValue</code>.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class PieceTitleDisplay extends AbstractDisplay {

    /**
     * Piece whose title is to be displayed.
     */
    Piece piece;

    /**
     * JTextArea used for displaying the title of the <code>piece</code>.
     */
    JTextArea textArea = new JTextArea();

    private boolean linebreak = true;

    /**
     * Creates the graphical user interface. Displays the title of the Piece in
     * a JTextArea.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractDisplay#createGUI()
     */
    public void createGUI() {
        determinePiece();
        //init textArea
        textArea.setEnabled(false);
        textArea.setDisabledTextColor(Color.BLACK);
        //        textArea.setLineWrap(true);
        //        textArea.setWrapStyleWord(true);
        setLayout(new BorderLayout());
        add(textArea);
        //set text
        if (piece != null) {
            updateTextArea();
        }
    }

    /**
     * Updates the <code>textArea</code> by setting its text.
     *  
     */
    private void updateTextArea() {
        String title = createTitle();
        if (title != null) {
            textArea.setText(title);
        } else {
            if (piece.getName() != null) {
                textArea.setText(piece.getName());
            }
        }
    }

    /**
     * Sets the <code>piece</code> to either the <code>propertyValue</code>
     * or the <code>editObj</code>.
     */
    private void determinePiece() {
        if (propertyValue != null && propertyValue instanceof Piece) {
            piece = (Piece) propertyValue;
        } else {
            if (editObj instanceof Piece) {
                piece = (Piece) editObj;
            }
        }
    }

    /**
     * Returns a String which represents the title of the <code>piece</code>.
     * The title gives the composer's name and the title and opus or catalogue
     * number of the composition as specified in the metadata of the
     * <code>piece</code>. If the MetaMap of the Piece does not specify
     * information about the composer and title, null is returned. If the metadata
     * contains information about the instrumentation of the piece, this information
     * is added to the String.
     * 
     * @return String composer's name and composition's title with eventually the composition's instrumentation, or null
     */
    private String createTitle() {
        MetaDataCollection mdPiece = determinePieceMetaData();
        if (mdPiece != null) {
            StringBuffer buffer = new StringBuffer();
            String composer = createTitleComposer(mdPiece);
            //add composer's name
            if (composer != null) {
                buffer.append(composer);
            }
            //add title and opus of composition
            String composition = createTitleComposition(mdPiece);
            if (composition != null) {
                if (buffer.length() > 0) {
                    if (linebreak) {
                        buffer.append(":\n");
                    } else {
                        buffer.append(":  ");
                    }
                }
                buffer.append(composition);
            }
            //add instrumentation if specified in metadata 
            String instrumentation = createTitleInstrumentation(mdPiece);
            if (instrumentation!=null){
                if (buffer.length() > 0) {
                    if (linebreak) {
                        buffer.append("\n");
                    } else {
                        buffer.append("  ");
                    }
                }
                buffer.append(instrumentation);
            }
            return buffer.toString();
        }
        return null;
    }

    /**
     * Returns a MetaDataCollection which contains those MetaDataItems of the
     * Piece's meta map that match the MetaDataProfile "Piece". Null is
     * returned, if the meta map does not contain items with keys that are
     * contained in the profile "Piece". <br>
     * First, the metadata registered for the Piece itself is analyzed. If its
     * does not contain items about the "Piece", all values
     * (MetaDataCollections) of the meta map are checked against a
     * MetaDataProfile "Piece". <br>
     * N.B.: At the moment, MetaDataItems for the Piece are found only if the
     * <code>listOfProfiles</code> of a MetaDataCollection contains a profile
     * "Piece".
     * 
     * @return MetaDataCollection with MetaDataItems mnatching the "Piece"
     *         MetaDataProfile
     */
    private MetaDataCollection determinePieceMetaData() {
        MetaDataCollection mdPiece = null;
        Map metaMap = piece.getMetaMap();
        if (metaMap.containsKey(piece)) {
            MetaDataCollection metaData = (MetaDataCollection) metaMap
                    .get(piece);
            mdPiece = determinePieceMetaData(metaData);
        }
        if (mdPiece == null) {
            for (Iterator iter = metaMap.values().iterator(); iter.hasNext();) {
                MetaDataCollection mdColl = (MetaDataCollection) iter.next();
                mdPiece = determinePieceMetaData(mdColl);
                if (mdPiece != null) {
                    break;
                }
            }
        }
        return mdPiece;
    }

    /**
     * Returns a MetaDataCollection containing those MetaDataItems from the
     * specified MetaDataCollection whose keys match the keys of the
     * MetaDataProfile "Piece".
     * 
     * @param metaData
     *            MetaDataCollection from which the MetaDataItems for "Piece"
     *            are to be extracted
     * @return MetaDataCollection containing those MetaDataItems form the
     *         specified MetaDataCollection which match the MetaDataProfile
     *         "Piece"
     */
    private MetaDataCollection determinePieceMetaData(
            MetaDataCollection metaData) {
        java.util.List profiles = metaData.getListOfProfiles();
        for (Iterator iter = profiles.iterator(); iter.hasNext();) {
            MetaDataProfile mdProfile = (MetaDataProfile) iter.next();
            if (mdProfile.getName().equals("Piece")) {
                return metaData.returnMetaDataForProfile(mdProfile);
            }
        }
        return null;
    }

    /**
     * Returns a String with the composer's name as specified in the Piece's
     * metadata. If the metadata does not a contain a MetaDataItem with key
     * "Composer", null is returned. <br>
     * The composer's name is given in the form first name + surname.
     * 
     * @param mdPiece
     *            MetaDataCollection from which the composer's name is to be
     *            extracted
     * @return String with the composer's name, or null
     */
    private String createTitleComposer(MetaDataCollection mdPiece) {
        MetaDataItem item = mdPiece.getItemByKey("Composer");
        if (item.getMetaDataValue() != null) {
            if (item.getMetaDataValue().getMetaValue() instanceof String) {
                StringBuffer buffer = new StringBuffer();
                buffer.append((String) item.getMetaDataValue().getMetaValue());
                //if "surname, name" -> "name surname"
                if (buffer.indexOf(",") != -1) {
                    buffer.append(" ");
                    buffer.append(buffer.substring(0, buffer.indexOf(",")));
                    buffer.delete(0, buffer.indexOf(",") + 1);
                    if (buffer.charAt(0) == ' ') {
                        buffer.deleteCharAt(0);
                    }
                }
                if (buffer.length() > 0
                        && !(buffer.length() == 1 && buffer.charAt(0) == ' ')) {
                    return buffer.toString();
                }
            }
        }
        return null;
    }

    /**
     * Returns a String with the composition's title as specified in the Piece's
     * metadata. The title gives the value of the MetaDataItem with key "Title"
     * and the opus or cataloge number if specified. If the metadata does not a
     * contain a MetaDataItem with key "Title", null is returned. <br>
     * The title is given in the form "title, opus number".
     * 
     * @param mdPiece
     *            MetaDataCollection from which the composition's title is to be
     *            extracted
     * @return String with the composition's title, or null
     */
    private String createTitleComposition(MetaDataCollection mdPiece) {
        MetaDataItem item = mdPiece.getItemByKey("Title");
        if (item.getMetaDataValue() != null) {
            if (item.getMetaDataValue().getMetaValue() instanceof String) {
                StringBuffer buffer = new StringBuffer();
                buffer.append((String) item.getMetaDataValue().getMetaValue());
                String opus = createTitleOpus(mdPiece);
                if (buffer.length() > 0
                        && !(buffer.length() == 1 && buffer.charAt(0) == ' ')) {
                    if (opus != null) {
                        buffer.append(", ");
                        buffer.append(opus);
                    }
                    return buffer.toString();
                }
            }
        }
        return null;
    }

    /**
     * Returns a String with the opus or catalogue number as specified in the
     * <code>mdPiece</code>. If the specified MetaDataCollection does not
     * contain a MetaDataItem with key "Opus/Catalogue Number" or the item does
     * not contain a value, null is returned. <br>
     * This method is called in method
     * <code>createTitleComposition(MetaDataCollection)</code>.
     * 
     * @param mdPiece
     *            MetaDataCollection from which the opus or catalogie number is
     *            to be extracted
     * @return String with the opus or catalogue number of the piece, or null
     */
    private String createTitleOpus(MetaDataCollection mdPiece) {
        MetaDataItem item = mdPiece.getItemByKey("Opus/Catalogue Number");
        if (item.getMetaDataValue() != null) {
            if (item.getMetaDataValue().getMetaValue() instanceof String) {
                StringBuffer buffer = new StringBuffer();
                buffer.append((String) item.getMetaDataValue().getMetaValue());
                if (buffer.length() > 0
                        && !(buffer.length() == 1 && buffer.charAt(0) == ' ')) {
                    return buffer.toString();
                }
            }
        }
        return null;
    }
    
    /**
     * Returns a String with the instrumentation as specified in the Piece's
     * metadata. If the metadata does not a contain a MetaDataItem with key
     * "Instrumentation" or the MetaDataItem does not have a value, 
     * null is returned. 
     * 
     * @param mdPiece MetaDataCollection from which the instrumentation is to be extracted
     * @return String with the instrumentation of the piece, or null
     */
    private String createTitleInstrumentation(MetaDataCollection mdPiece){
        MetaDataItem item = mdPiece.getItemByKey("Instrumentation");
        if (item.getMetaDataValue()!=null){
            if (item.getMetaDataValue().getMetaValue() instanceof String) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("für ");
                buffer.append((String) item.getMetaDataValue().getMetaValue());
                if (buffer.length() > 0
                        && !(buffer.length() == 1 && buffer.charAt(0) == ' ')) {
                    return buffer.toString();
                }
            }
        }
        return null;
    }

    /**
     * Sets the specified Font as the Font of the <code>textArea</code>.
     * 
     * @param titleFont
     *            Font to be set as the Font of the <code>textArea</code>
     */
    public void setTitleFont(Font titleFont) {
        if (textArea != null) {
            textArea.setFont(titleFont);
        }
    }

    public void setLineBreak(boolean linebreak) {
        this.linebreak = linebreak;
        updateTextArea();
    }

}