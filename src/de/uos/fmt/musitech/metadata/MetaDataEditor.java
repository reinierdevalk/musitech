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
 * Created on 2003-06-12
 */
package de.uos.fmt.musitech.metadata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.metadata.MetaDataProfile;
import de.uos.fmt.musitech.data.metadata.MetaDataProfileItem;
import de.uos.fmt.musitech.data.metadata.MetaDataProfileManager;
import de.uos.fmt.musitech.data.metadata.MetaDataValue;
import de.uos.fmt.musitech.framework.editor.AbstractEditor;
import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.SimpleTextfieldEditor;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * The MetaDataEditor displays the metadata about an object included in a
 * MetaDataCollection according to specified MetaDataProfiles. A
 * MetaDataCollection contains MetaDataItems the keys of which lead to
 * MetaValues consisting of type and value. For editing the elements of the
 * MetaDataCollection, MetaDataItemEditors are used. <br>
 * <br>
 * Data of the collection matched by the keys of a MetaDataProfile is listed
 * first, additional data of the collection is displayed below. The Editor also
 * allows to change values of metadata, to remove profiles or metadata or to
 * insert new metadata. <br>
 * <br>
 * During edition a copy of the original MetaDataCollection collectionCopy is
 * changed. When the "Apply"-button is pressed and method applyChanges() is
 * invoked the collection edited is set to collectionCopy. collectionCopy may
 * contain "empty" MetaDataItems, that is, MetaDataItems whose value (of
 * MetaDataValue) is an empty String. When applying the changes to the original
 * collection, all empty items are removed so that all MetaDataItems in the
 * edited collection have a (at the present state formally) valid value.
 * 
 * @author Christophe Hinz, Kerstin Neubarth
 */

/*
 * The value of the MetaDataValue in "empty" MetaDataItems (e.g. in creating a new
 * MetaDataItem) is set to "".
 * 
 * When removing a profile from the editor, all items with value "" are removed
 * in order not to have empty textfields appearing in the "Additional Data"
 * section. On the other hand, when creating a new item this should appear
 * there, even if its value has not been set yet. Therefore, the item removal is
 * performed inside method removeProfileInEditor(). (In a former version, it had
 * been performed in method buildCollectionPanel().)
 * 
 * When adding a new MetaDataItem whose key is matched by one of the
 * MetaDataProfiles defined in the MetaDataProfileManager, the whole profile is
 * to be added. (That is, when the user is asked to add the profile and he
 * answers "no", the MetaDataItem will not be created.)
 *  
 */

public class MetaDataEditor extends AbstractEditor {

    // used as copy for manipulating the MetaDataCollection to be edited, will
    // contain those data not matched by the collection's profiles
    private MetaDataCollection collectionRemainder = new MetaDataCollection();
    // used as copy of the MetaDataCollection to be edited in changing, removing
    // or adding metadata
    private MetaDataCollection collectionCopy = new MetaDataCollection();
    private Box collectionPanel = Box.createVerticalBox();
    List<String> panelsToExpand = new ArrayList<String>();
    List<Editor> itemEditors = new ArrayList<Editor>();

    private String language = null;

    //private List mimes; //just for testing

    //for testing
    /*
     * public static void main(String[] arg) {
     * 
     * MetaDataEditorTest t = new MetaDataEditorTest();
     * 
     * MetaDataEditor md = null; EditingProfile profile = new
     * EditingProfile("MetaDataCollection", "MetaData", null); try { if
     * (EditorFactory.createEditor(t.createCollection(), profile) instanceof
     * MetaDataEditor) md = (MetaDataEditor) EditorFactory.createEditor(
     * t.createCollection(), profile); } catch (EditorConstructionException e) {
     * e.printStackTrace(); } md.mimes = t.createListOfMimeTypes();
     * TestEditor.editorWindow(md, 500, 500); }
     */

    /**
     * Setter for <code>language</code>. Sets the language in the item editor
     * as well.
     * 
     * @param argLanguage String giving the language in which the meta data is
     *            shown
     */
    public void setLanguage(String argLanguage) {
        this.language = argLanguage;
        for (int i = 0; i < itemEditors.size(); i++) {
            ((MetaDataItemEditor) itemEditors.get(i)).setLanguage(argLanguage);
        }
    }

    /**
     * Getter for <code>language</code>.
     * 
     * @return String giving the language in which the meta data is shown
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the MetaDataCollection to be edited and copies MetaDataItems and
     * MetaDataProfiles of this collection to <code>collectionCopy</code>.
     * 
     * @param argEditObj Object to be edited
     */
    public void setEditObj(Object argEditObj) {
        if (typeCheckEditObj(argEditObj)) {
            this.editObj = argEditObj;
            ((MetaDataCollection) argEditObj).copyToMetaDataCollection(collectionCopy);
            createEmptyItemsInCollectionCopy();
        } //TODO else?
    }

    /**
     * Checks the type of the object to be edited. Returns true if
     * <code>editObj</code> is of type MetaDataCollection. Throws an
     * IllegalArgumentException otherwise.
     * 
     * @param argEditObj object to be edited
     * @return boolean true if editObj is of type MetaDataCollection
     * @throws IllegalArgumentException
     */
    private boolean typeCheckEditObj(Object argEditObj) {
        if (argEditObj.getClass().isAssignableFrom(MetaDataCollection.class)) {
            return true;
        } else
            throw new IllegalArgumentException("MetaDataEditor - setEditObj(Object): Argument " + argEditObj
                                               + " must be of type MetaDataCollection.");
    }

    /**
     * If a MetaDataProfileItem of a MetaDataProfile in the
     * <code>listOfProfilesInCollection</code> of <code>collectionCopy</code>
     * does not have a corresponding MetaDataItem in <code>collectionCopy</code>,
     * a new MetaDataItem is created and added to <code>collectionCopy</code>.
     * This new MetaDataItem gets the <code>key</code> and <code>type</code>
     * of the MetaDataProfileItem as its <code>key</code> and the
     * <code>mimeType</code> of its <code>metaValue</code> and an empty
     * String as the <code>value</code> of its <code>metaValue</code>.<br>
     * <br>
     * (Empty items are necessary for creating the corresponding item editors.)
     */
    private void createEmptyItemsInCollectionCopy() {
        List profiles = collectionCopy.getListOfProfiles();
        for (Iterator iter = profiles.iterator(); iter.hasNext();) {
            MetaDataProfile mdProfile = (MetaDataProfile) iter.next();
            createEmptyItemsInCollectionCopy(mdProfile);
        }
    }

    /**
     * For those MetaDataProfileItems of <code>profile</code> which do not
     * have a corresponding MetaDataItem in <code>collectionCopy</code>, an
     * "empty" MetaDataItem is created and added to <code>collectionCopy</code>.
     * 
     * @param profile MetaDataProfile whose MetaDataProfileItems are used in
     *            creating "empty" MetaDataItems
     */
    private void createEmptyItemsInCollectionCopy(MetaDataProfile profile) {
        String[] keys = profile.listOfKeys();
        for (int i = 0; i < keys.length; i++) {
            if (collectionCopy.getItemByKey(keys[i]) == null) {
                MetaDataItem emptyItem = createMetaDataItemByProfileItem(keys[i]);
                collectionCopy.addMetaDataItem(emptyItem);
            }
        }
    }

    /**
     * Removes those MetaDataItems from <code>collectionCopy</code> whose
     * <code>value</code> of <code>metaValue</code> is an empty String.
     */
    //	private void removeEmptyItemsFromCollectionCopy(){
    protected void removeEmptyItemsFromCollectionCopy() {
        MetaDataCollection itemsToRemove = new MetaDataCollection();
        for (Iterator iter = collectionCopy.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            MetaDataItem mdItem = collectionCopy.getItemByKey(key);
            MetaDataValue metaDataValue = mdItem.getMetaDataValue();
            if (metaDataValue != null) {
                if (metaDataValue.getMetaValue() != null && metaDataValue.getMetaValue().equals("")) {
                    //collectionCopy.removeMetaDataItem(mdItem);
                    itemsToRemove.addMetaDataItem(mdItem);
                    //Modif2001
                    if (!collectionCopy.isKeyInProfiles(mdItem.getKey())) {
                        itemEditors.remove(getItemEditor(mdItem));
                        updateGUI();
                    }

                }
            }
        }
        collectionCopy.removeMetaDataCollection(itemsToRemove);
    } //TODO assertion + private

    /**
     * Getter for <code>collectionCopy</code>.<br>
     * Used in class <code>MetaDataEditorTest</code> only, therefore is
     * declared "protected".
     * 
     * @return
     */
    protected MetaDataCollection getCollectionCopy() {
        return collectionCopy;
    } //TODO Methode löschen (wenn createEmptyItems() and removeEmptyItems()
      // mit assertions)

    /**
     * Sets Layout-Managers for the main JPanel and for
     * <code>collectionPanel</code> in the GUI. <br>
     * The main panel will contain a title (<code>mainLabel</code>), the
     * <code>collectionPanel</code> and buttons for choosing to add or remove
     * metadata. The <code>collectionPanel</code> will contain one or more
     * DataPanels corresponding to MetaDataProfiles and eventually an
     * "Additional Data"-DataPanel. <br>
     * <br>
     * The <code>collectionPanel</code> is packed into an outer
     * <code>panel</code> to have it in the top part of the editor in case of
     * collapsed DataPanels. This outer <code>panel</code> is given a
     * JScrollPane.
     */
    protected void initLayout() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(collectionPanel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(panel);
        add(scroll, BorderLayout.CENTER);
        JLabel mainLabel = new JLabel(getLabeltext());
        add(mainLabel, BorderLayout.NORTH);
        add(createButtons(), BorderLayout.SOUTH);
        panel.setFocusable(true);
        panel.addFocusListener(focusListener);
    }

    /**
     * Creates editors for the MetaDataItems in <code>collectionCopy</code>
     * and records them in the List <code>itemEditors</code>.
     */
    protected void createItemEditors() {
        if (!collectionRemainder.isEmpty())
            collectionRemainder.clear();
        collectionRemainder.addMetaDataCollection(collectionCopy);
        List profilesOfColl = ((MetaDataCollection) editObj).getListOfProfiles();
        //create item editors according to the MetaDataProfiles
        for (Iterator iter = profilesOfColl.iterator(); iter.hasNext();) {
            MetaDataProfile mdProfile = (MetaDataProfile) iter.next();
            for (Iterator iterator = mdProfile.iterator(); iterator.hasNext();) {
                MetaDataProfileItem profileItem = (MetaDataProfileItem) iterator.next();
                MetaDataItem mdItem = collectionCopy.getItemByKey(profileItem.getKey());
                //				if (mdItem == null) {
                //					mdItem =
                //						createMetaDataItemByProfileItem(profileItem.getKey());
                //					collectionCopy.addMetaDataItem(mdItem);
                //				}//should not be reached as empty items are created when
                // initializing collectionCopy
                createItemEditorForNewItem(mdItem);
                collectionRemainder.removeMetaDataItem(mdItem);
            }
        }
        //create item editors for those MetaDataItems not matched by a
        // MetaDataProfileItem
        if (!collectionRemainder.isEmpty()) {
            for (Iterator iter = collectionRemainder.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                MetaDataItem remainingItem = collectionRemainder.getItemByKey(key);
                if (remainingItem != null)
                    createItemEditorForNewItem(remainingItem);
            }
        }
    }

    /**
     * Creates an editor for the specified MetaDataItem and adds the created
     * editor to the List <code>itemEditors</code>.
     * 
     * @param newItem MetaDataItem an editor is to be created for
     */
    protected void createItemEditorForNewItem(MetaDataItem newItem) {
        Editor newItemEditor = null;
        try {
//            newItemEditor = EditorFactory.createEditor(newItem, null, rootDisplay);    
                newItemEditor = EditorFactory.createEditor(newItem, profile.isReadOnly(), null, rootDisplay);
            
        } catch (EditorConstructionException ece) {
            ece.printStackTrace();
        }
        if (newItemEditor != null){
            itemEditors.add(newItemEditor);
        }
    }

    /**
     * Creates editors for MetaDataItems matched by the specified
     * MetaDataProfile and adds them to the List <code>itemEditors</code>, in
     * case there have not been a corresponding MetaDataItem and item editor
     * yet. <br>
     * This method is invoked in adding a new profile. For those
     * MetaDataProfileItems not matched by a MetaDataItem of
     * <code>collectionCopy</code>, a corresponding "empty" MetaDataItem is
     * created and added to <code>collectionCopy</code> and an editor for this
     * MetaDataItem is created and added to <code>itemEditors</code>. (This
     * means, that no item editor is created for an already existing
     * MetaDataItem which has been edited in the "Additional Data" section
     * before adding the new profile.)
     * 
     * @param profile MetaDataProfile leading to MetaDataItems for which item
     *            editors are to be created
     */
    protected void createItemEditorsForProfile(MetaDataProfile profile) {
        String[] keysInProfile = profile.listOfKeys();
        for (int i = 0; i < keysInProfile.length; i++) {
            MetaDataItem item = collectionCopy.getItemByKey(keysInProfile[i]);
            //			if (item == null) {
            //				item = createMetaDataItemByProfileItem(keysInProfile[i]);
            //				collectionCopy.addMetaDataItem(item);
            if (getItemEditor(item) == null) //Modif1401
                createItemEditorForNewItem(item);
            //			}
        }
    }

    /**
     * Updates <code>collectionRemainder</code> and builds the GUI.
     */
    protected void buildLayout() {
        collectionCopy.copyToMetaDataCollection(collectionRemainder);
        buildCollectionPanel();
        revalidate();
        doLayout();
    }

    /**
     * Fills the <code>collectionPanel</code> to be displayed in the
     * MetaDataEditor. <br>
     * The <code>collectionPanel</code> contains one or more sections in which
     * DataPanels are listed according to the specified MetaDataProfiles to get
     * from <code>collectionCopy</code>. In the last section, data of
     * <code>collectionCopy</code> not matched by the MetaDataProfiles is
     * displayed, if there is any.
     */
    protected void buildCollectionPanel() {
        collectionPanel.removeAll();
        List profilesOfCollection = collectionCopy.getListOfProfiles();
        //Data according to profiles of collection
        if (!profilesOfCollection.isEmpty()) {
            for (int index = 0; index < profilesOfCollection.size(); index++) {
                MetaDataProfile profile = (MetaDataProfile) profilesOfCollection.get(index);
                DataPanel profilePanel = new DataPanel(profile);
                profilePanel.setFocusable(true);
                profilePanel.addFocusListener(focusListener);
                collectionPanel.add(profilePanel);
            }
        }
        //Data in collection not displayed yet, i.e. data not taken into
        // consideration by the profiles
        if (!collectionRemainder.isEmpty()) {
            DataPanel additionalDataPanel = new DataPanel("Additional Data", collectionRemainder);
            collectionPanel.add(additionalDataPanel);
        }
    }

    /**
     * Returns a MetaDataCollection containing those MetaDataItems of
     * <code>actCollection</code> that are matched by the MetaDataProfileItems
     * of <code>actProfile</code>. The MetaDataCollection
     * <code>collectionRemainder</code> is meant to contain those metadata not
     * matched by any MetaDataProfile of <code>collectionCopy</code>.
     * 
     * @param actProfile MetaDataProfile indicating the MetaDataItems to be
     *            extracted
     * @param actCollection MetaDataCollection from which the requested
     *            MetaDataItems are extracted
     * @return MetaDataCollection containing the MetaDataItems extracted from
     *         <code>actCollection</code> according to <code>actProfile</code>
     */
    protected MetaDataCollection extractItemsMatchingProfile(MetaDataProfile actProfile,
                                                             MetaDataCollection actCollection) {
        MetaDataCollection collection = new MetaDataCollection();
        for (int i = 0; i < actProfile.size(); i++) {
            String keyOfItem = actProfile.listOfKeys()[i];
            MetaDataItem actItem = actCollection.getItemByKey(keyOfItem);
            if (actItem != null) {
                if (!collectionRemainder.isEmpty())
                    collectionRemainder.removeMetaDataItem(actItem);
            }
            //			else {
            //				actItem = createMetaDataItemByProfileItem(keyOfItem);
            //				//collectionCopy.addMetaDataItem(actItem);
            //			}
            collection.addMetaDataItem(actItem);
        }
        return collection;
    }

    /**
     * Returns the maximum width of the JLabels containing the keys of
     * <code>collectionCopy</code>.
     * 
     * @return int width of JLabel with greatest width containing a key of
     *         <code>collectionCopy</code>
     */
    int getWidthLabelPanel() {
        int maxWidth = 0;
        for (Iterator iter = collectionCopy.keySet().iterator(); iter.hasNext();) {
            String keyInSet = (String) iter.next();
            JLabel label = new JLabel(keyInSet);
            //			int ms = (int) label.getMinimumSize().getWidth();
            //			int ps = (int) label.getPreferredSize().getWidth();
            if (label.getMinimumSize().getWidth() > maxWidth)
                maxWidth = (int) label.getMinimumSize().getWidth();
            //maxWidth = (int)label.getPreferredSize().getWidth() +
            // label.getParent().getInsets().left +
            // label.getParent().getInsets().right;
        }
        return maxWidth + 70; //value obtained by testing
        //return maxWidth;
    }

    /**
     * Returns a JPanel containing four buttons for choosing to add or remove a
     * MetaDataProfile or a MetaDataItem.
     * 
     * @return JPanel containing buttons with labels "Add Profile", "Remove
     *         Profile", "Add Item" and "Remove Item"
     */
    protected JPanel createButtons() {
        JPanel buttonPanel = new JPanel();
        // quick hack for MPEG 
//        if (profile.isReadOnly())
//            return buttonPanel;
        JButton addProfile = new JButton("Add Profile");
        addProfile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                addProfileInEditor();
            }
        });

        JButton removeProfile = new JButton("Remove Profile");
        removeProfile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                removeProfileInEditor();
            }
        });

        JButton addItem = new JButton("Add Item");
        addItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                addItemInEditor();
            }
        });

        JButton removeItem = new JButton("Remove Item");
        removeItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                removeItemInEditor();
            }
        });

        buttonPanel.add(addProfile);
        buttonPanel.add(removeProfile);
        buttonPanel.add(addItem);
        buttonPanel.add(removeItem);

        //Ergänzung 06/01/05
        final JButton languageButton = new JButton("Select Language");
        languageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectLanguage(languageButton);
            }
        });
        if (collectionCopy.returnProvidedLanguages().size()<=1){
            languageButton.setEnabled(false);
        }
        buttonPanel.add(languageButton);
        //Ende Ergänzung

        //addFocusListenerToEditorComponent(buttonPanel);
        //addFocusListenerToEditorComponent(addProfile);
        //addFocusListenerToEditorComponent(removeProfile);
        //addFocusListenerToEditorComponent(addItem);
        //addFocusListenerToEditorComponent(removeItem);
        buttonPanel.setFocusable(true);
        buttonPanel.addFocusListener(focusListener);
        addProfile.addFocusListener(focusListener);
        return buttonPanel;
    }

    /**
     * Metadata is stored in the MetaDataCollection edited when the
     * "Apply"-button of the editor window is activated. <br>
     * Overrides method <code>applyChanges()</code> in abstract class
     * AbstractEditor.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#applyChanges()
     */
    public void applyChanges() {
        dataChanged = false;
        //		if (isOutmostEditor())
        if (rootDisplay == this)
            sendDataChangeEvent();
        for (Iterator iter = itemEditors.iterator(); iter.hasNext();) {
            Editor itemEditor = (Editor) iter.next();
            itemEditor.applyChanges();
        }
        writeCollectionCopyToEditObj();
        //		printEditObjForTesting();
        //collectionCopy is restored to the state before removing empty items.
        //(to continue working with latest state in case "Apply" is chosen
        // without destroying editor afterwards)
        restoreCollectionCopy();
    }

    /**
     * Updates the <code>editObj</code> to the elements and EditingProfiles
     * currently held in <code>collectionCopy</code>.
     */
    protected void writeCollectionCopyToEditObj() {
        //		removeEmptyItemsFromCollectionCopy();
        collectionCopy.copyToMetaDataCollection((MetaDataCollection) editObj);
    }

    /**
     * Copies values from the GUI to the collectionCopy.
     */
    //alte Version
    /*
     * void updateCopy() { for (Iterator iter = itemEditors.iterator();
     * iter.hasNext();) { Editor itemEditor = (Editor) iter.next();
     * itemEditor.applyChanges(); String key =
     * itemEditor.getEditingProfile().getLabel(); //testing Object metaValue =
     * itemEditor.getEditObj(); if
     * (metaValue.getClass().isAssignableFrom(MetaDataValue.class)) { Object value =
     * ((MetaDataValue) metaValue).getValue(); if (value != null) { if
     * (collectionCopy.getItemByKey(key) == null)
     * collectionCopy.addMetaDataItem( createMetaDataItemByProfileItem(key));
     * collectionCopy.getItemByKey(key).getMetaValue().setValue( value); } } // } // } }
     * 
     * //just for testing: display for checking changed, removed and inserted
     * data for (Iterator iter = collectionCopy.keySet().iterator();
     * iter.hasNext(); ) { String key = (String) iter.next(); if
     * (collectionCopy.getItemByKey(key).getMetaValue().getValue() != null) {
     * System.out.println( key + "\t" + collectionCopy .getItemByKey(key)
     * .getMetaValue() .getValue() .toString() + "\tclass type: " +
     * collectionCopy .getItemByKey(key) .getMetaValue() .getValue() .getClass()
     * .getName() .toString() + "\t\t" + collectionCopy .getItemByKey(key)
     * .getMetaValue() .getMimeType()); } else System.out.println("empty item: " +
     * key); } System.out.println(); }
     */

    /**
     * Updates the working copy <code>collectionCopy</code>.
     */
    //ersetzt updateCopy()
    public void updateCollectionCopy() {
        for (Iterator iter = itemEditors.iterator(); iter.hasNext();) {
            Editor itemEditor = (Editor) iter.next();
            itemEditor.applyChanges();
        }
    }

    /**
     * Restores <code>collectionCopy</code> to the state previous to removing
     * empty MetaDataItems. Afterwards <code>collectionCopy</code> contains
     * MetaDataItems corresponding to the editors in <code>itemEditors</code>.
     * <br>
     * <br>
     * Empty items are removed from <code>collectionCopy</code> before writing
     * it to <code>editObj</code>. When continuing to work in the
     * MetaDataEditor, however, empty items corresponding to
     * MetaDataProfileItems are needed. Therefore, the MetaDataItems known by
     * the item editors are added to <code>collectionCopy</code> again. <br>
     * (This way is chosen not to change <code>editObj</code> itself unless
     * applying <code>collectionCopy</code> to it, particularly not to have
     * empty items in <code>editObj</code> at any time.)
     */
    private void restoreCollectionCopy() {
        for (Iterator iter = itemEditors.iterator(); iter.hasNext();) {
            Editor itemEditor = (Editor) iter.next();
            if (!collectionCopy.containsMetaDataItem((MetaDataItem) itemEditor.getEditObj()))
                collectionCopy.addMetaDataItem((MetaDataItem) itemEditor.getEditObj());
        }
    }

    /**
     * Returns a MetaDataItem with the specified <code>key</code> and a
     * MetaDataValue with <code>mimeType</code> extracted from the
     * MetaDataProfileItem matching the specified <code>key</code>.
     * 
     * @param key String <code>key</code> of the MetaDataItem to be created
     * @return MetaDataItem with specified <code>key</code> and a MetaDataValue
     *         with <code>mimeType</code> and with value ""
     */
    private MetaDataItem createMetaDataItemByProfileItem(String key) {
        MetaDataProfile profile = MetaDataProfileManager.getMetaDataProfileContainingKey(key);
        MetaDataItem newItem = new MetaDataItem(key);
        for (Iterator iter = profile.iterator(); iter.hasNext();) {
            MetaDataProfileItem profileItem = (MetaDataProfileItem) iter.next();
            if (profileItem.getKey() == key) {
                MetaDataValue metaDataValue = null;
                metaDataValue = new MetaDataValue(profileItem.getType(), "");
                //TODO evtl. null für komplexe Typen?
                newItem.setMetaValue(metaDataValue);
            }
        }
        return newItem;
    }

    /**
     * Returns a MetaDataCollection containing those MetaDataItems of the
     * specified MetaDataCollection whose <code>value</code> (of
     * <code>metaValue</code>) is an empty String.
     * 
     * @param actCollection MetaDataCollection from which MetaDataItems with
     *            value "" should be extracted
     * @return MetaDataCollection with those MetaDataItems of
     *         <code>actCollection</code> whose value is an empty String
     */
    private MetaDataCollection extractEmptyItems(MetaDataCollection actCollection) {
        MetaDataCollection emptyItems = new MetaDataCollection();
        if (actCollection != null && !actCollection.isEmpty()) {
            for (Iterator iter = actCollection.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                MetaDataItem item = actCollection.getItemByKey(key);
                if (item.getMetaDataValue() != null) {
                    //if (item.getValue().getValue() == null)
                    //	emptyItems.addMetaDataItem(item);
                    //else
                    if (item.getMetaDataValue().getMetaValue().equals(""))
                        emptyItems.addMetaDataItem(item);
                }
            }
        }
        return emptyItems;
    }

    /**
     * Returns a MetaDataCollection containing those MetaDataItems of the
     * specified MetaDataCollection whose <code>value</code> (of MetaDataValue) is
     * an empty String.
     * 
     * @param actCollection MetaDataCollection from which MetaDataItems with
     *            value "" should be extracted
     * @return MetaDataCollection with those MetaDataItems of
     *         <code>actCollection</code> whose value is an empty String
     */
    private MetaDataCollection extractEmptyTextfields(MetaDataCollection actCollection) {
        MetaDataCollection emptyItems = new MetaDataCollection();
        if (actCollection != null && !actCollection.isEmpty()) {
            for (Iterator iter = actCollection.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                MetaDataItem item = actCollection.getItemByKey(key);
                if (item.getMetaDataValue() != null) {
                    if (item.getMetaDataValue().getMetaValue().equals(""))
                        emptyItems.addMetaDataItem(item);
                }
            }
        }
        return emptyItems;
    }

    /**
     * Asks for the MetaDataProfile to be added and adds the chosen profile to
     * <code>collectionCopy</code>. Creates item editors according to the
     * profile's MetaDataProfileItems.
     */
    void addProfileInEditor() {
        final String profileName = getNameOfProfileToAdd();
        if (profileName != null) {
            MetaDataProfile profile = MetaDataProfileManager.getMetaDataProfile(profileName);
            collectionCopy.addProfile(profile);
            createEmptyItemsInCollectionCopy(profile);
            createItemEditorsForProfile(profile);
            panelsToExpand.add(profileName);
            buildLayout();
            SwingUtilities.invokeLater(new Thread() {

                public void run() {
                    scrollToDataPanel(profileName);
                }
            });
        }
    }

    /**
     * Returns a String indicating the name of the chosen MetaDataProfile. Only
     * MetaDataProfiles not edited yet can be chosen.
     * 
     * @return String name of the chosen MetaDataProfile, or null if there is no
     *         MetaDataProfile to choose
     */
    private String getNameOfProfileToAdd() {
        //create list of MetaDataProfiles not contained in collectionCopy
        ArrayList listOfProfiles = new ArrayList();
        String[] profilesOfManager = MetaDataProfileManager.getMetaDataProfileNames();
        List profilesInCollection = collectionCopy.getListOfProfiles();
        for (int i = 0; i < profilesOfManager.length; i++) {
            String nameOfProfile = MetaDataProfileManager.getMetaDataProfileNames()[i];
            if (!profilesInCollection.contains(MetaDataProfileManager.getMetaDataProfile(nameOfProfile))) {
                listOfProfiles.add(nameOfProfile);
            }
        }
        //show dialog with MetaDataProfiles that can be chosen
        if (!listOfProfiles.isEmpty()) {
            return (String) JOptionPane.showInputDialog(collectionPanel,
                                                        "Please choose a MetaDataProfile to be added.", "Add Profile",
                                                        JOptionPane.PLAIN_MESSAGE, null, listOfProfiles.toArray(),
                                                        listOfProfiles.toArray()[0]);
        } else {
            JOptionPane.showMessageDialog(collectionPanel, "All the known profiles are already displayed.",
                                          "Add Profile", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    /**
     * Gets the MetaDataProfile to be removed from <code>collectionCopy</code>
     * and removes it. MetaDataItems matched by the removed MetaDataProfile are
     * retained or are removed as well, depending on the users choice.
     */
    void removeProfileInEditor() {
        MetaDataProfile profileToRemove = getProfileToRemove();
        if (profileToRemove != null) {
            if (!retainDataWithoutProfile(profileToRemove)) {
                for (int i = 0; i < profileToRemove.listOfKeys().length; i++) {
                    MetaDataItem itemToRemove = collectionCopy.getItemByKey(profileToRemove.listOfKeys()[i]);
                    if (itemToRemove != null) {
                        collectionCopy.removeMetaDataItem(itemToRemove);
                        Editor editorToRemove = getItemEditor(itemToRemove);
                        if (editorToRemove != null)
                            itemEditors.remove(editorToRemove);
                    }

                }
            } else {
                MetaDataCollection remainingItems = extractItemsMatchingProfile(profileToRemove, collectionCopy);
                if (remainingItems != null && !remainingItems.isEmpty()) {
                    if (extractEmptyTextfields(remainingItems) != null) {
                        MetaDataCollection itemsToRemove = extractEmptyTextfields(remainingItems);
                        collectionCopy.removeMetaDataCollection(itemsToRemove);
                        itemEditors.removeAll(getItemEditors(itemsToRemove));
                    }
                }
                if (collectionCopy != null && !collectionCopy.isEmpty())
                    if (!panelsToExpand.contains("Additional Data"))
                        panelsToExpand.add("Additional Data");
            }

            collectionCopy.removeProfile(profileToRemove);
            buildLayout();
        }
    }

    /**
     * Returns the MetaDataProfile to be removed from
     * <code>collectionCopy</code>. This profile can be chosen from a list of
     * all MetaDataProfiles of <code>collectionCopy</code>.
     * 
     * @return MetaDataProfile to be removed from <code>collectionCopy</code>,
     *         or null if there are no MetaDataProfiles in
     *         <code>collectionCopy</code>
     */
    private MetaDataProfile getProfileToRemove() {
        //List of MetaDataProfiles in collectionCopy must be "converted" into a
        // list of the profilenames to be displayed in the dialog
        ArrayList profiles = new ArrayList();
        String[] profileList = new String[collectionCopy.listOfProfilesInCollection.size()];
        for (Iterator iter = collectionCopy.listOfProfilesInCollection.iterator(); iter.hasNext();) {
            MetaDataProfile element = (MetaDataProfile) iter.next();
            profiles.add(element);
        }
        if (!profiles.isEmpty()) {
            int i = 0;
            for (Iterator iter = profiles.iterator(); iter.hasNext();) {
                MetaDataProfile element = (MetaDataProfile) iter.next();
                profileList[i] = element.getName();
                i++;
            }
            String profileName = (String) JOptionPane.showInputDialog(collectionPanel,
                                                                      "Please choose the profile to be removed.",
                                                                      "Remove Profile", JOptionPane.PLAIN_MESSAGE,
                                                                      null, profileList, profileList[0]);
            return MetaDataProfileManager.getMetaDataProfile(profileName);
        } else {
            JOptionPane.showMessageDialog(collectionPanel, "There is no profile to remove.", "Remove Profile",
                                          JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    /**
     * Asks if the MetaDataItems matched by the specified MetaDataProfile should
     * be retained in <code>collectionCopy</code> while the profile is
     * removed. Returns true if the data is to be retained.
     * 
     * @param profile MetaDataProfile matching the metadata to be removed or
     *            retained
     * @return boolean true if the matching metadata is to be retained, false
     *         otherwise
     */
    private boolean retainDataWithoutProfile(MetaDataProfile profile) {
        boolean matchesData = false;
        for (int i = 0; i < profile.listOfKeys().length; i++) {
            MetaDataItem item = (MetaDataItem) collectionCopy.getItemByKey(profile.listOfKeys()[i]);
            if (item != null) {
                MetaDataValue metaDataValue = item.getMetaDataValue();
                if (metaDataValue != null)
                    if (!metaDataValue.getMetaValue().equals(""))
                        matchesData = true;
            }
        }
        if (matchesData) {
            int option = JOptionPane.showConfirmDialog(collectionPanel, "The profile '" + profile.getName()
                                                                        + "' contains MetaDataItems.\n"
                                                                        + "Do you want to retain the MetaDataItems?");
            if (option == JOptionPane.YES_OPTION) {
                return true;
            }
        }
        return false;
    }

    /**
     * Asks for key and mimeType of a new MetaDataItem, creates the new item and
     * adds it to <code>collectionCopy</code>.
     */
    void addItemInEditor() {
        final String key = getKeyOfNewItem();
        if (key != null) {
            String mimeType = getMimeTypeOfNewItem();
            if (mimeType != null) {
                MetaDataItem newItem = new MetaDataItem(key);
                newItem.setMetaValue(new MetaDataValue(mimeType, ""));

                collectionCopy.addMetaDataItem(newItem);
                //create editor for newItem and add it to List itemEditors
                createItemEditorForNewItem(newItem);

                if (!panelsToExpand.contains("Additional Data"))
                    panelsToExpand.add("Additional Data");

                buildLayout();
                SwingUtilities.invokeLater(new Thread() {

                    public void run() {
                        setFocusToTextfield("Additional Data", key);
                        scrollToDataPanel("Additional Data");
                    }
                });

            }
        }
    }

    /**
     * Creates a dialog to ask for the key of a new MetaDataItem and returns a
     * String if the key is "valid". <br>
     * A key is not valid as key of a new MetaDataItem if is already contained
     * in <code>collectionCopy</code> or if it is an empty String. In case the
     * key matches one of the MetaDataProfileItems defined in
     * MetaDataProfileManager, the MetaDataProfile containing this key can be
     * added.
     * 
     * @return String indicating the <code>key</code> of the MetaDataItem, or
     *         null if the inserted key is invalid.
     */
    private String getKeyOfNewItem() {
        String newKey = (String) JOptionPane.showInputDialog(collectionPanel,
                                                             "Please give the key for the new MetaDataItem");
        if (newKey != null) {
        	newKey = newKey.trim();
            if (checkKeyInCollection(newKey)) {
                return null;
            }
            if (!checkKeyInProfileManager(newKey))
                if (!checkKeyIfEmpty(newKey))
                    return newKey;
        }
        return null;
    }

    /**
     * Checks if <code>newKey</code> is an empty String If <code>newKey</code>
     * is an empty String, the dialog to ask for a new key is shown again.
     * Returns false, if <code>newKey</code> is a String of at least one
     * character.
     * 
     * @param newKey String
     * @return boolean
     */
    private boolean checkKeyIfEmpty(String newKey) {
        if (newKey.equals("")) {
            JOptionPane.showMessageDialog(collectionPanel, "The key must have at least one character.");
            addItemInEditor();
            return true;
        }
        return false;
    }

    /**
     * Returns true if the specified String does already exist as
     * <code>key</code> in <code>collectionCopy</code>.
     * 
     * @param newKey String to be checked
     * @return boolean true if <code>newKey</code> is a key in
     *         <code>collectionCopy</code>, false otherwise
     */
    private boolean checkKeyInCollection(final String newKey) {
        boolean keyInCollection = false;
        if (collectionCopy.isKeyInProfiles(newKey) || collectionCopy.getItemByKey(newKey) != null) {
            JOptionPane.showMessageDialog(collectionPanel, "The key '" + newKey + "' is already there.", "",
                                          JOptionPane.INFORMATION_MESSAGE);
            // The DataPanel containing newKey is expanded (if it is not
            // expanded yet).
            String profileName = null;
            MetaDataProfile profile = MetaDataProfileManager.getMetaDataProfileContainingKey(newKey);
            if (profile != null)
                profileName = profile.getName();
            String panelTitle = "";
            if (profileName == null
                || !collectionCopy.getListOfProfiles().contains(
                                                                MetaDataProfileManager
                                                                        .getMetaDataProfileContainingKey(newKey))) {
                if (!panelsToExpand.contains("Additional Data"))
                    panelsToExpand.add("Additional Data");
                panelTitle = "Additional Data";
            } else {
                if (!panelsToExpand.contains(profileName))
                    panelsToExpand.add(profileName);
                panelTitle = profileName;
            }
            //The focus is set to the JTextfield associated with the existing
            // key.
            //If this textfield is outside the part cuurently shown in the
            // scroll pane
            //the editorpanel is scrolled.
            final String title = panelTitle;
            buildLayout();
            SwingUtilities.invokeLater(new Thread() {

                public void run() {
                    setFocusToTextfield(title, newKey);
                    scrollToDataPanel(title);
                }
            });

            keyInCollection = true;
        }
        return keyInCollection;
    }

    /**
     * Returns true if the specified String is a <code>key</code> in one of
     * the MetaDataProfiles defined in MetaDataProfileManager and if this
     * MetaDataProfile is to be added to <code>collectionCopy</code>.
     * 
     * @param newKey String to be checked
     * @return boolean true if <code>newKey</code> is a key of a
     *         MetaDataProfile in MetaDataProfileManager and the MetaDataProfile
     *         shall be added, false otherwise
     */
    private boolean checkKeyInProfileManager(String newKey) {
        boolean profileWithKey = false;
        MetaDataProfile profile = MetaDataProfileManager.getMetaDataProfileContainingKey(newKey);
        if (profile != null) {
            profileWithKey = true;
            int option = JOptionPane.showConfirmDialog(collectionPanel, "The key '" + newKey
                                                                        + "' is part of the MetaDataProfile '"
                                                                        + profile.getName() + "'.\n"
                                                                        + "Do you want to add this MetaDataProfile?");
            if (option == JOptionPane.YES_OPTION) {
                collectionCopy.addProfile(profile);
                createEmptyItemsInCollectionCopy(profile); //Modif1401
                createItemEditorsForProfile(profile);
                buildLayout();
            }
        }
        return profileWithKey;
    }

    /**
     * Returns the <code>mimeType</code> of the MetaDataValue of a new
     * MetaDataItem. The <code>mimeType</code> can be chosen from a list of
     * possible mimeTypes.
     * 
     * @return String indicating the <code>mimeType</code> of
     *         <code>metaValue</code> of a MetaDataItem
     */
    private String getMimeTypeOfNewItem() {
        //TODO List mit möglichen mimeTypes "holen"
        List<String> mimes = createListOfMimeTypes();
        String[] mimeTypes = new String[mimes.size()];
        mimes.toArray(mimeTypes);
        return (String) JOptionPane.showInputDialog(collectionPanel, "Please choose a type.", "Add Item",
                                                    JOptionPane.PLAIN_MESSAGE, null, mimeTypes, mimeTypes[0]);
    }
    
	/**
	 * Creates a Vector containing some mimeType-values (type String) for testing.
	 * 
	 * @return
	 */
	private List<String> createListOfMimeTypes() {
		List<String> listOfMimeTypes = new ArrayList<String>();
		listOfMimeTypes.add("char");
		listOfMimeTypes.add("int");
		listOfMimeTypes.add("Rational");
		listOfMimeTypes.add("String");
		//for testing FloatEditor
		listOfMimeTypes.add("Float");

		//listOfMimeTypes.add("text/plain");	auskommentiert, solange kein TextPlainEditor o.ä. in EditorFactory
		return listOfMimeTypes;
	}


    /**
     * Asks for the <code>key</code> of the MetaDataItem to be removed and
     * removes the item from <code>collectionCopy</code>.
     */
    void removeItemInEditor() {
        String keyOfItemToRemove = getKeyOfItemToRemove();
        if (keyOfItemToRemove != null) {
            final MetaDataItem itemToRemove = collectionCopy.getItemByKey(keyOfItemToRemove);
            if (itemToRemove != null) {
                //remove item from collectionCopy if not matched by any
                // MetaDataProfileItem
                List profiles = collectionCopy.getListOfProfiles();
                Collection keys = new ArrayList();
                for (Iterator iter = profiles.iterator(); iter.hasNext();) {
                    MetaDataProfile profile = (MetaDataProfile) iter.next();
                    List keysOfProfile = Arrays.asList(profile.listOfKeys());
                    keys.addAll(keysOfProfile);
                }
                if (!keys.contains(itemToRemove.getKey())) {
                    collectionCopy.removeMetaDataItem(itemToRemove);
                    Editor editorToRemove = getItemEditor(itemToRemove);
                    if (editorToRemove != null)
                        itemEditors.remove(editorToRemove);
                    SwingUtilities.invokeLater(new Thread() {

                        public void run() {
                            scrollToDataPanel("Additional Data");
                        }
                    });
                } else {
                    itemToRemove.getMetaDataValue().setMetaValue("");
                    getItemEditor(itemToRemove).updateDisplay();
                    SwingUtilities.invokeLater(new Thread() {

                        public void run() {
                            scrollToDataPanel(MetaDataProfileManager.getMetaDataProfileContainingKey(
                                                                                                     itemToRemove
                                                                                                             .getKey())
                                    .getName());
                        }
                    });
                }
            }
            buildLayout();
        }
    }

    /**
     * Returns the <code>key</code> of the MetaDataItem to be removed from
     * <code>collectionCopy</code>. The key is chosen from a list of existing
     * keys of <code>collectionCopy</code>.
     * 
     * @return String indicating the <code>key</code> of a MetaDataItem, or
     *         null if the list of keys is empty
     */
    private String getKeyOfItemToRemove() {
        if (!collectionCopy.isEmpty()) {
            String[] keyList = new String[collectionCopy.keySet().size()];
            int i = 0;
            for (Iterator iter = collectionCopy.keySet().iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                keyList[i] = element;
                i++;
            }
            return (String) JOptionPane.showInputDialog(collectionPanel,
                                                        "Please choose the item to be removed (by key).",
                                                        "Remove Item", JOptionPane.PLAIN_MESSAGE, null, keyList,
                                                        keyList[0]);
        } else {
            JOptionPane.showMessageDialog(collectionPanel, "There is no item to remove.", "Remove Item",
                                          JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    /**
     * Sets the focus to the <code>textfield</code> of the itemEditor
     * associated with <code>labeltext</code>.
     * 
     * @param panelTitle String <code>title</code> of the DataPanel containing
     *            the itemEditor with labeltext <code>label</code>
     * @param label String indicating the <code>labeltext</code> of the
     *            itemEditor the <code>textfield</code> of which is to be
     *            focused
     */
    void setFocusToTextfield(String panelTitle, String label) {
        DataPanel actPanel = getDataPanel(panelTitle);
        List editors = actPanel.getItemEditorsInDataPanel();
        for (Iterator iter = editors.iterator(); iter.hasNext();) {
//            Editor element = (Editor) iter.next();
            Display element = (Display) iter.next();
            if (element.getEditingProfile().getLabel().equals(label)) {
                Display valueEditor = ((MetaDataItemEditor) element).getValueEditor();
                if (valueEditor instanceof SimpleTextfieldEditor)
                    ((SimpleTextfieldEditor) valueEditor).getTextfield().requestFocus();
            }
        }
    }

    /**
     * Scrolls the MetaDataEditor's panel (containing
     * <code>collectionPanel</code>) so that the DataPanel specified by
     * <code>nameOfPanel</code> is shown inside the currently visible part of
     * the scrollpane.
     * 
     * @param nameOfPanel String representing the <code>title</code> of the
     *            DataPanel to be scrolled to
     */
    void scrollToDataPanel(String nameOfPanel) {
        JPanel dataPanel = getDataPanel(nameOfPanel);
        if (dataPanel != null) {
            dataPanel.scrollRectToVisible(dataPanel.getBounds());
        }
    }

    /**
     * Returns the DataPanel with the specified <code <title</code>.
     * 
     * @param title String indicating the <code>title</code> of the requested
     *            DataPanel
     * @return DataPanel with the specified <code>title</code>
     */
    private DataPanel getDataPanel(String title) {
        Object[] dataPanels = collectionPanel.getComponents();
        for (int i = 0; i < dataPanels.length; i++) {
            if (((DataPanel) dataPanels[i]).getTitle().equals(title))
                return (DataPanel) dataPanels[i];
        }
        return null;
    }

    /**
     * Returns true if one of the editors used to display the MetaDataItems is
     * focused.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#isFocused()
     */
    public boolean isFocused() {
        for (Iterator iter = itemEditors.iterator(); iter.hasNext();) {
            Editor itemEditor = (Editor) iter.next();
            if (itemEditor.isFocused())
                return true;
        }
        return false;
    }

    /**
     * Updates the editor, in this case updates the <code>collectionCopy</code>
     * to the MetaDataCollection to be edited (<code>editObj</code>),
     * creates the item editors for the MetaDataItems in
     * <code>collectionCopy</code> and builds the editor's GUI.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#updateDisplay()
     */
    public void updateDisplay() {
        dataChanged = false;
        ((MetaDataCollection) editObj).copyToMetaDataCollection(collectionCopy);
        createEmptyItemsInCollectionCopy();
        if (!itemEditors.isEmpty()) {
            for (Iterator iter = itemEditors.iterator(); iter.hasNext();) {
                Editor editor = (Editor) iter.next();
                editor.destroy();
            }
            itemEditors.clear();
        }
        createItemEditors();
        buildLayout();
    }

    /**
     * Creates the graphical user interface.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#createGUI()
     */
    protected void createGUI() {
        initLayout();
        buildLayout();
        addFocusListenerToComponent(this);
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++)
            addFocusListenerToComponent(comps[i]);

        //				java.awt.Container focusCycleRoot = getFocusCycleRootAncestor();
        //				if (focusCycleRoot != null) {
        //					FocusTraversalPolicy pol = focusCycleRoot.getFocusTraversalPolicy();
        //					if (pol != null) {
        //						java.awt.Component comp =
        //							pol.getDefaultComponent(getFocusCycleRootAncestor());
        //						addFocusListenerToComponent(comp);
        //					}
        //					if (focusCycleRoot instanceof Window) {
        //						(
        //							(
        //								Window) focusCycleRoot)
        //									.addWindowFocusListener(new WindowFocusListener() {
        //		
        //							public void windowGainedFocus(WindowEvent w) {
        //								MetaDataEditor.this.reactToFocus();
        //							}
        //							public void windowLostFocus(WindowEvent w) {
        //							}
        //						});
        //					}
        //				}

    }

    /**
     * Adds the FocusListener defined inside the method to the specified
     * Component.
     * 
     * @param comp Component the FocusListener is to be added to.
     */
    private void addFocusListenerToComponent(Component comp) {
        comp.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                reactToFocus();
                //TODO evtl. direkt durch focusReceived() ersetzen
            }

            public void focusLost(FocusEvent e) {
            }
        });
    }

    /**
     * Updates the editor's GUI.
     */
    protected void updateGUI() {
        removeAll();
        createGUI();
        revalidate();
    }

    /**
     * Updates the item editors.
     */
    protected void updateItemEditors() {
        for (Iterator iter = itemEditors.iterator(); iter.hasNext();) {
            Editor elementEditor = (Editor) iter.next();
            elementEditor.updateDisplay();
        }
        revalidate();
        repaint();
    }

    /**
     * Initializes the editor's main parameters, that is, its
     * <code>editObj</code>,<code>profile</code>,
     * <code>propertyName</code>,<code>propertyValue</code> and
     * <code>rootEditor</code>. The editor is registered at the
     * DataChangeManager, and the GUI is built.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#init(java.lang.Object,
     *      de.uos.fmt.musitech.framework.editor.EditingProfile,
     *      de.uos.fmt.musitech.framework.editor.Editor)
     */
    public void init(Object editObject, EditingProfile profile, Display rootEditor) {
        setEditObj(editObject);
        //includes type check and initializes collectionCopy
        if (editObj == null)
            return; //TODO error message?
        this.profile = profile;
        this.propertyName = profile.getPropertyName();
        this.rootDisplay = rootEditor;
        setPropertyValue();
        createItemEditors();
        registerAtChangeManager();
        createGUI();
    }

    /**
     * Returns a List containing the MetaDataCollection edited if this
     * MetaDataCollection has been changed, an empty List otherwise.
     * 
     * @return List containing <code>editObj</code> if <code>editObj</code>
     *         has been changed, an empty List otherwise
     * @see de.uos.fmt.musitech.framework.editor.Editor#getEditedData()
     */
    public Collection getEditedData() {
        List data = new ArrayList();
        //		if (hasMetaDataCollectionChanged())
        //			data.add(getEditObj());
        for (Iterator iter = itemEditors.iterator(); iter.hasNext();) {
            Editor itemEditor = (Editor) iter.next();
            Collection dataOfItemEditor = itemEditor.getEditedData();
            if (!dataOfItemEditor.isEmpty())
                data.addAll(dataOfItemEditor);
        }
        if (!data.isEmpty() || hasMetaDataCollectionChanged())
            data.add(getEditObj());
        return data;
    }

    /**
     * Returns true if MetaDataItems in the MetaDataCollection to be edited have
     * been added, removed or replaced or if a MetaDataProfile has been added or
     * removed. <br>
     * This method does not check if the value in a MetaDataItem has been
     * changed. (Those changes are known to the corresponding item editors.)
     * 
     * @return true if <code>collectionCopy</code> has more or less or other
     *         objects (MetaDataItems or MetaDataProfiles) than
     *         <code>editObj</code>, false otherwise
     */
    protected boolean hasMetaDataCollectionChanged() {
        removeEmptyItemsFromCollectionCopy();
        if (((MetaDataCollection) editObj).size() != collectionCopy.size()) {
            return true;
        } else {
            Set entriesInEditObj = ((MetaDataCollection) editObj).entrySet();
            Set entriesInCopy = collectionCopy.entrySet();
            if (!entriesInEditObj.containsAll(entriesInCopy) || !entriesInCopy.containsAll(entriesInEditObj))
                return true;
        }
        List profilesInEditObj = ((MetaDataCollection) editObj).getListOfProfiles();
        List profilesInCopy = collectionCopy.getListOfProfiles();
        if (!ObjectCopy.equalValues(profilesInEditObj, profilesInCopy)) {
            return true;
        }
        return false;
    }

    /**
     * Overwrites method <code>destroy()</code> of class
     * <code>AbstractEditor</code> in order to destroy the editors used to
     * edit the MetaDataItems as well as the MetaDataEditor itself.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#destroy()
     */
    public void destroy() {
        //print for testing:
        //printEditObjForTesting();
        super.destroy();
        List elementEditors = getItemEditors();
        if (elementEditors != null && !elementEditors.isEmpty())
            for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
                Editor elementEditor = (Editor) iter.next();
                elementEditor.destroy();
            }
    }

    /**
     * Overwrites method <code>isRemoteEventSource(Object)</code> of class
     * <code>AbstractEditor</code>. Returns false if the specified Object
     * <code>eventSource</code> is this editor or one of the item editors,
     * i.e. one of the editors used to edit the MetaDataItems of the
     * MetaDataCollection, true otherwise.
     * 
     * @return true, if <code>eventSource</code> is not this editor or one of
     *         the subordinated editors used to edit the MetaDataItems of the
     *         MetaDataCollection
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#isRemoteEventSource(java.lang.Object)
     */
    protected boolean isRemoteEventSource(Object eventSource) {
        //return false if eventSource is this editor
        if (eventSource == this)
            return false;
        //return false if eventSource is one of the itemEditors (i.e. of the
        // editors inside the DaraPanels)
        List editors = getItemEditors();
        if (editors != null && !editors.isEmpty()) {
            if (editors.contains(eventSource))
                return false;
        }
        return true;
    }

    /**
     * Returns true if the specified Editor is an item editor of this
     * MetaDataEditor, that is, if the List <code>itemEditors</code> contains
     * <code>editor</code>.
     * 
     * @param editor Editor to be checked if contained in List
     *            <code>itemEditors</code>
     * @return true if <code>itemEditors</code> contains the specified Editor,
     *         false otherwise
     */
    public boolean isItemEditor(Editor editor) {
        return itemEditors.contains(editor);
    }

    /**
     * Updates the item editors and calls <code>focusReceived()</code>.<br>
     * <br>
     * (Method <code>focusReceived()</code> is not overwritten, because the
     * item editors should only be updated, when the focus is shifted to the
     * MetaDataEditor from outside the editor. The item editors are not to be
     * updated when the focus is shifted from one item editor to another inside
     * this MetaDataEditor, in which case however the item editor gaining the
     * focus invokes <code>focusReceived()</code> of its rootEditor, being
     * this MetaDataEditor.)
     *  
     */
    //TODO update comment
    public void reactToFocus() {
        System.out.println("reactToFocus()");

        //if editor "reached" from outside: display current values of elements
        //		if (previousFocusOutside(e))
        //		updateItemEditors();
        //		updateGUI();
        focusReceived();
    }

    /**
     * Returns true if the focus is shifted to this editor from outside the
     * editor.
     * 
     * @param e FocusEvent
     * @return true if the Component owning the focus before is an Component
     *         outside this editor
     */
    private boolean previousFocusOutside(FocusEvent e) {
        Component previousComponent = e.getOppositeComponent();
        java.awt.Container root = previousComponent.getFocusCycleRootAncestor();
        boolean outestRoot = false;
        if (root == null) {
            outestRoot = true;
            root = (java.awt.Container) previousComponent;
        }

        while (!outestRoot) {
            if (root.getFocusCycleRootAncestor() == null || root.getFocusCycleRootAncestor() == root) {
                outestRoot = true;
            } else
                root = root.getFocusCycleRootAncestor();
        }
        Component comp = this;
        boolean componentsLeft = true;
        boolean outside = true;
        while (componentsLeft) {
            if (comp == root) {
                outside = false;
                componentsLeft = false;
                //for testing
                System.out.println("focus from inside");
            } else {
                if (comp.getParent() != null)
                    comp = comp.getParent();
                else
                    componentsLeft = false;
            }

        }
        return outside;
    }

    /**
     * Getter for <code>itemEditors</code>.
     * 
     * @return <code>itemEditors</code> of this MetaDataEditor
     */
    protected List getItemEditors() {
        return itemEditors;
    }

    /**
     * Returns that editor in List <code>itemEditors</code> whose
     * <code>editObj</code> is the specified MetaDataItem.
     * 
     * @param item MetaDataItem edited by the requested item editor
     * @return Editor editing the specified MetaDataItem
     */
    private Editor getItemEditor(MetaDataItem item) {
        for (Iterator iter = itemEditors.iterator(); iter.hasNext();) {
            Editor itemEditor = (Editor) iter.next();
            if (itemEditor.getEditObj() == item)
                return itemEditor;
        }
        return null;
    }

    /**
     * Returns a List containing those item editors in <code>itemEditors</code>
     * which edit the MetaDataItems of <code>subsetOfColl</code>.
     * 
     * @param subsetOfColl MetaDataCollection for whose MetaDataItems the item
     *            editors are looekd for
     * @return List containing the item editors used for the MetaDataItems in
     *         <code>subsetOfColl</code>
     */
    private List getItemEditors(MetaDataCollection subsetOfColl) {
        List editors = new ArrayList();
        for (Iterator iter = subsetOfColl.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            MetaDataItem item = subsetOfColl.getItemByKey(key);
            Editor itemEditor = getItemEditor(item);
            editors.add(itemEditor);
        }
        return editors;
    }

    /**
     * Prints the MetaDataCollection edited to the command line. This method is
     * used for testing only.
     */
    private void printEditObjForTesting() {
        for (Iterator iter = ((MetaDataCollection) editObj).keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            System.out.println("MetaDataEditor: printEditObjForTesting()");
            System.out.println(((MetaDataCollection) editObj).getItemByKey(key).toString());
        }
    }

    private void selectLanguage(JButton languageButton) {
        //get languages to offer
        Collection languages = collectionCopy.returnProvidedLanguages();
        languages.remove(null);
        Object[] array = new String[languages.size()+1];
        array[0] = "Standard";
        int i=1;
        for (Iterator iter = languages.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            array[i++] = element;
        }
        if (languages.size() > 1) {
            //get selected language
            String selectedLanguage = (String)JOptionPane.showInputDialog(this, "Please select a language.", "Language Options",
                                                              JOptionPane.QUESTION_MESSAGE, null, array,
                                                              languages.toArray()[0]);
            //change language
            if (selectedLanguage.equals(array[0]))
                setLanguage(null);
            else
                setLanguage(selectedLanguage);
        } else {
            JOptionPane.showMessageDialog(this, "This Meta Data Collection does not provide further languages.", "Language Option", JOptionPane.INFORMATION_MESSAGE, null);
        }
    }

    /**
     * Inner class DataPanel. Extends a JPanel to edit MetaDataItems of a
     * MetaDataCollection. The DataPanel can be expanded to show the
     * MetaDataItems or reduced to hide the MetaDataItems, depending on the
     * current state of the panel. The class offers methods for managing the
     * DataPanel's layout as well as for addressing the edited MetaDataItems via
     * the itemEditors, objects of class AbstractEditor for editing a single
     * MetaDataItem.
     * 
     * @author Kerstin Neubarth
     *  
     */
    protected class DataPanel extends JPanel {

        String title;
        //Array of JPanels of length 2, to contain labelPanel and valuePanel,
        // i.e.
        //the JPanels for editing the MetaDataItems' keys and their MetaDataValue's
        // value.
        JPanel[] contentPanels = new JPanel[2];
        List itemEditorsInDataPanel = new ArrayList();

        /**
         * Constructor. Sets the <code>title</code> of the DataPanel to be
         * displayed in its titlebar and creates the <code>contentPanels</code>
         * to edit keys and values of the MetaDataItems contained in the
         * MetaDataCollection <code>actCollection</code>.
         * 
         * @param title String to indicate the title of the DataPanel (a
         *            MetaDataProfile's name or "Additional Data")
         * @param actCollection MetaDataCollection containing the MetaDataItems
         *            to be edited in this instance of class DataPanel.
         */
        public DataPanel(String title, MetaDataCollection actCollection) {
            this.title = title;
            if (!itemEditorsInDataPanel.isEmpty())
                itemEditorsInDataPanel.clear();
            itemEditorsInDataPanel.addAll(extractItemEditorsForDataPanel(actCollection));
            contentPanels = createLabelAndValuePanel();
            fillDataPanel();
        }

        /**
         * Constructor. Sets the DataPanel's <code>title</code> to the
         * <code>name</code> of the specified MetaDataProfile. Puts those item
         * editors of the MetaDataEditor's <code>itemEditors</code> into
         * <code>itemEditorsInDataPanel</code> which edit the MetaDataItems of
         * <code>collectionCopy</code> corresponding to the specified
         * MetaDataProfile.
         * 
         * @param profile MetaDataProfile for which a DataPanel is to be created
         */
        public DataPanel(MetaDataProfile profile) {
            this.title = profile.getName();
            if (!itemEditorsInDataPanel.isEmpty())
                itemEditorsInDataPanel.clear();
            itemEditorsInDataPanel.addAll(extractItemEditorsForDataPanel(profile));
            contentPanels = createLabelAndValuePanel();
            fillDataPanel();
        }

        /**
         * Extracts from the <code>itemEditors</code> of the MetaDataEditor
         * those editors editing the MetaDataItems in <code>coll</code> in
         * this DataPanel.
         * 
         * @param coll MetaDataCollection
         * @return List
         */
        protected List extractItemEditorsForDataPanel(MetaDataCollection coll) {
            List itemEditorsInPanel = new ArrayList();
            for (Iterator iter = coll.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                //TODO Suche nach itemEditors effizienter?
                for (Iterator iterator = itemEditors.iterator(); iterator.hasNext();) {
                    Editor element = (Editor) iterator.next();
                    if (element.getEditObj() == coll.getItemByKey(key)) {
                        itemEditorsInPanel.add(element);
                        break;
                    }
                }
            }
            return itemEditorsInPanel;
        }

        /**
         * Returns a List containing the item editors of the MetaDataEditor's
         * <code>itemEditors</code> matching the specified MetaDataProfile.
         * 
         * @param profile MetaDataProfile for which the corresponding item
         *            editors are to be extracted
         * @return List containing the item editors which match the specified
         *         MetaDataProfile
         */
        protected List extractItemEditorsForDataPanel(MetaDataProfile profile) {
            MetaDataCollection coll = extractItemsMatchingProfile(profile, collectionCopy);
            for (Iterator iter = profile.iterator(); iter.hasNext();) {
                MetaDataProfileItem profileItem = (MetaDataProfileItem) iter.next();
                if (coll.getItemByKey(profileItem.getKey()) == null) {
                    MetaDataItem item = createMetaDataItemByProfileItem(profileItem.getKey());
                    coll.addMetaDataItem(item);
                }
            }
            return extractItemEditorsForDataPanel(coll);
        }

        /**
         * Shows the titlebar of the DataPanel and the contentPanels if an
         * expanded display is requested.
         */
        private void fillDataPanel() {
            removeAll();
            int borderInset = 2;
            int lineBorderInset = 2;
            Color lineColor = Color.BLACK;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(borderInset, borderInset,
                                                                                         borderInset, borderInset),
                                                         BorderFactory.createCompoundBorder(BorderFactory
                                                                 .createLineBorder(lineColor), BorderFactory
                                                                 .createEmptyBorder(lineBorderInset, lineBorderInset,
                                                                                    lineBorderInset, lineBorderInset))));
            add(createTitleBar(), BorderLayout.NORTH);
            if (panelsToExpand.contains((title)))
                expand(contentPanels[0], contentPanels[1]);
        }

        /**
         * Returns a Box containing a JLabel and a JButton separated by
         * horizontal glue. The JLabel displays the <code>name</code> of a
         * MetaDataProfile or "Additional Data", depending on the metadata to be
         * edited in the DataPanel. The JButton is labeled "Expand" or
         * "Collapse" depending on the DataPanel's current state.
         * 
         * @return Box containing a JLabel with the DataPanel's
         *         <code>title</code> and a JButton ("Expand" or "Collapse")
         */
        private Box createTitleBar() {
            Box titleBar = Box.createHorizontalBox();
            titleBar.add(new JLabel(title));
            titleBar.add(Box.createHorizontalGlue());
            JButton expandReduceButton = new JButton();
            expandReduceButton.setPreferredSize((new JButton("Collapse")).getPreferredSize());
            if (panelsToExpand.contains(title)) {
                createCollapseBorder(titleBar);
                expandReduceButton.setText("Collapse");
            } else {
                createExpandBorder(titleBar);
                expandReduceButton.setText("Expand");
            }
            //add ActionListener to button (and define ActionListener)
            expandReduceButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ce) {
                    JButton source = (JButton) ce.getSource();
                    if (source.getText().equals("Expand")) {
                        panelsToExpand.add(title);
                        buildLayout();
                        SwingUtilities.invokeLater(new Thread() {

                            public void run() {
                                scrollToDataPanel(title);
                            }
                        });
                    } else {
                        panelsToExpand.remove(title);
                        buildLayout();
                    }
                }
            });

            titleBar.add(expandReduceButton);
            return titleBar;
        }

        /**
         * Sets the Border for the DataPanel's <code>titleBox</code> with the
         * JButton "Collapse".
         * 
         * @param titleBox Box with a Border containing a bottom line to
         *            separate the titleBox from the contentPanels
         */
        private void createCollapseBorder(Box titleBox) {
            int outsideBorderHorizontalInset = 15;
            int outsideBorderTopInset = 2;
            int outsideBorderBottomInset = 5;
            int bottomLine = 1;
            Color bottomLineColor = Color.GRAY;
            int insideBorderBottomInset = 5;
            titleBox.setBorder(BorderFactory
                    .createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory
                            .createEmptyBorder(outsideBorderTopInset, outsideBorderHorizontalInset,
                                               outsideBorderBottomInset, outsideBorderHorizontalInset), BorderFactory
                            .createMatteBorder(0, 0, bottomLine, 0, bottomLineColor)), BorderFactory
                            .createEmptyBorder(0, 0, insideBorderBottomInset, 0)));
        }

        /**
         * Sets the Border of the DataPanel's <code>titleBox</code> with the
         * JButton "Expand".
         * 
         * @param titleBox Box with an empty border to create empty space
         *            surrounding the box's components
         */
        private void createExpandBorder(Box titleBox) {
            int bottomInset = 5;
            titleBox.setBorder(BorderFactory.createEmptyBorder(0, 0, bottomInset, 0));
        }

        /**
         * Returns two JPanels, the first containing the labels of the
         * MetaDataItems to be displayed, the second containing objects of class
         * AbstractEditor to edit the MetaDataItems' values. The displayed
         * values are the objects of field <code>value</code> in the
         * MetaDataItems' <code>metaValue</code>.
         * 
         * @return JPanel[] Array of JPanels of length 2, the first element
         *         being the <code>labelPanel</code>, the second the
         *         <code>valuePanel</code>
         */
        private JPanel[] createLabelAndValuePanel() {
            JPanel[] contentPanels = new JPanel[2];
            int insetOutside = 15;
            int insetInside = 10;
            int insetBottom = 2;
            //prepare JPanel to take the labels
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new GridLayout(0, 1));
            labelPanel.setBorder(BorderFactory.createEmptyBorder(0, insetOutside, insetBottom, insetInside));
            labelPanel.setPreferredSize(new Dimension(getWidthLabelPanel(), 0));
            //prepare JPanel to take objects of class AbstractEditor displaying
            // the values (called "item editors" in following comments)
            JPanel valuePanel = new JPanel();
            valuePanel.setLayout(new GridLayout(0, 1));
            valuePanel.setBorder(BorderFactory.createEmptyBorder(0, insetInside, insetBottom, insetOutside));
            //fill labelPanel and valuePanel
            for (Iterator iter = itemEditorsInDataPanel.iterator(); iter.hasNext();) {
                Editor itemEditor = (Editor) iter.next();
                labelPanel.add(new JLabel(((AbstractEditor) itemEditor).getLabeltext()));
                valuePanel.add((Component) itemEditor);
            }
            contentPanels[0] = labelPanel;
            contentPanels[1] = valuePanel;
            return contentPanels;
        }

        /**
         * Expands the DataPanel's display by adding the specified
         * contentPanels.
         * 
         * @param labelPanel JPanel containing JLabels to display the
         *            MetaDataItem's keys
         * @param valuePanel JPanel containing objects of class AbstractEditor
         *            to edit the metadata values
         */
        private void expand(JPanel labelPanel, JPanel valuePanel) {
            add(labelPanel, BorderLayout.WEST);
            add(valuePanel, BorderLayout.CENTER);
        }

        /**
         * Returns the <code>itemEditorsInDataPanel</code> of this DataPanel.
         * 
         * @return List <code>itemEditorsInDataPanel</code> of this DataPanel
         */
        public List getItemEditorsInDataPanel() {
            return itemEditorsInDataPanel;
        }

        /**
         * Returns the DataPanel's title.
         * 
         * @return String giving the title of this DataPanel
         */
        public String getTitle() {
            return title;
        }

    } //end of inner class DataPanel

}