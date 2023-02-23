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
 * Created on 15.10.2003
 *
 */
package de.uos.fmt.musitech.framework.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.framework.selection.SelectingEditor;
import de.uos.fmt.musitech.framework.selection.SelectionController;
import de.uos.fmt.musitech.time.TimeRange;

/**
 * This is a superclass for CollectionEditor and MapEditor containing those
 * features that are common to both classes.
 * 
 * @author Kerstin Neubarth
 *  
 */
public abstract class CollectionMapEditor extends AbstractComplexEditor
        implements SelectingEditor {

    /**
     * Vector containing the editors used for editing the elements of the
     * collection or map. That is, the elements in this Vector are of type
     * AbstractEditor.
     */
    List elementEditors = new ArrayList();
    
    /*
     * Toolkit to ge used to get the dimesion of the screen where the application is running
     * and allow the user to alocate the hight and width of the window accoriding to the screen dimensions
     * code edited: 10/April/2008
     */
    
    //Amir Obertinca: Edited 10/April/2008
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    /**
     * Default editortype for elementEditors. May be changed to "PopUp", for
     * example.
     */
    public static final String EDITORTYPE_FOR_ELEMENTS = "Preview";

    private SelectionController selectionController;

    /**
     * PersistenceManager used when adding existing objects as new elements.
     */
    //    private PersistenceManager persistenceManager;
    //TODO dirty wird nicht genutzt
    /**
     * Setter for <code>persistenceManager</code>.
     * 
     * @param persistenceManager
     *            PersistenceManager to be set as the
     *            <code>persistenceManager</code> of this CollectionMapEditor.
     */
    //    public void setPersistenceManager(PersistenceManager persistenceManager){
    //        this.persistenceManager = persistenceManager;
    //    }
    /**
     * Getter for <code>persistenceManager</code>.
     * 
     * @return PersistenceManager the <code>persistenceManager</code> of this
     *         CollectionMapEditor
     */
    //    public PersistenceManager getPersistenceManager(){
    //        return persistenceManager;
    //    }
    // -------------------- Methods for creating the GUI ------------------
    /**
     * Creates the graphical user interface. <br>
     * There are two main sections which, however, are not distinguished
     * graphically: the first lists the properties of the collection or map, the
     * second displays the elements of the collection or map. <br>
     * For both, properties and elements, a JPanel on the left side is given the
     * labels and another JPanel on the right side is given the values (more
     * precisely: the editors displaying the values) either as a JTextfield or
     * hidden behind a JButton. <br>
     * The editortype for the element editors is set in
     * <code>EDITORTYPE_FOR_ELEMENTS</code>.<br>
     * At the bottom, there is a further JPanel containing the JButtons
     * necessary for offering the special editor funcionality (adding, deleting
     * or replacing elements).
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractComplexEditor#createGUI()
     */
    @Override
	protected void createGUI() {

        // panel for labels
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(0, 1));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));

        // panel for textfields
        JPanel textfieldPanel = new JPanel();
        textfieldPanel.setLayout(new GridLayout(0, 1));
        textfieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));

        //add children editors and element editors
        if (getChildren() != null && getChildren().length > 0)
            fillInChildrenEditors(labelPanel, textfieldPanel);
        fillInElementEditors(labelPanel, textfieldPanel);
        // add label- and textfield-panels to panel with scrollpane
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        //Amir Obertinca: Edited 10/April/2008
        mainPanel.setPreferredSize(new Dimension(screenSize.width-60, screenSize.height-500));
        
        mainPanel.add(labelPanel, BorderLayout.WEST);
        mainPanel.add(textfieldPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainPanel);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(getLabeltext()));
        add(scrollPane, BorderLayout.CENTER);
        if (profile != null && !profile.isReadOnly())
            add(createButtonPane(), BorderLayout.SOUTH);
        setFocusable(true);
        addFocusListener(new FocusListener() {
            @Override
			public void focusGained(FocusEvent e) {
                focusReceived();
            }

            @Override
			public void focusLost(FocusEvent e) {
            }
        });
    }

    /**
     * Adds the editors used to edit the properties of <code>editObj</code>.
     * Complex editors are wrapped behind an "Edit..."-button.
     * 
     * @param labelPanel
     *            JPanel meant to contain the labeltexts of the children and
     *            element ditors
     * @param textfieldPanel
     *            JPanel meant to contain the children and element editors
     */
    protected void fillInChildrenEditors(JPanel labelPanel,
            JPanel textfieldPanel) {
        for (int i = 0; i < children.length; i++) {
            // TODO: hier auch andere Editoren zulassen
            //			labelPanel.add(
            //				new JLabel(children[i].getEditingProfile().getLabel()));
            if (children[i] instanceof AbstractSimpleEditor
                    || children[i] instanceof PopUpEditor) {
                labelPanel.add(new JLabel(((AbstractEditor) children[i])
                        .getLabeltext()));
                textfieldPanel.add((Component) children[i]);
                if (children[i] instanceof SimpleTextfieldEditor)
                    ((SimpleTextfieldEditor) children[i])
                            .setEmptyDisplayOption(true);
            } else {
                if (children[i] instanceof AbstractComplexEditor) {
                    labelPanel.add(new JLabel(((AbstractEditor) children[i])
                            .getLabeltext()));
                    children[i] = EditorFactory
                            .createPopUpWrapper(children[i]);
                    textfieldPanel.add((Component) children[i]);
                }
            }
        }
    }

    /**
     * Adds the editors used to edit the collection's or map's elements to the
     * GUI.
     * 
     * @param labelPanel
     *            JPanel meant to contain the labeltexts of the children and
     *            element editors.
     * @param textfieldPanel
     *            JPanel meant to conatin the children and element editors
     */
    protected void fillInElementEditors(JPanel labelPanel, JPanel textfieldPanel) {
        for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
            Editor elementEditor = (Editor) iter.next();
            if (elementEditor instanceof AbstractEditor) {
                labelPanel.add(new JLabel(((AbstractEditor) elementEditor)
                        .getLabeltext()));
            }
            textfieldPanel.add((Component) elementEditor);
            if (selectionController != null) {
                ((JComponent) elementEditor)
                        .addMouseListener(selectionController.getSelectionAdapter());
            }
        }
    }

    /**
     * Returns a JPanel containing the JButtons offering to add, delete or
     * replace an element of the collection or map. <br>
     * As replacing an element varies according to the type of
     * <code>editObj</code> (in case of a Map either the key or the value of
     * an element may be replaced), this method here is abstract and to be
     * implemented in the subclasses.
     * 
     * @return JPanel containing JButtons
     */
    abstract protected JPanel createButtonPane();

    /**
     * Updates this editor's GUI.
     */
    protected void updateGUI() {
        removeAll();
        createGUI();
        revalidate();
        if (getParent() != null)
            getParent().repaint();
    }

    /**
     * Overwrites method <code>focusReceived()</code> of AbstractEditor in
     * order to update the element previews.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Display#focusReceived()
     */
    @Override
	public void focusReceived() {
        updatePreviews();
        super.focusReceived();
    }

    /**
     * Returns true if this editor, one of its children or one of its element
     * editors is focused.
     * 
     * @return boolean true if this editor or one of its children or element
     *         editors is focused, false otherwise
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editor#isFocused()
     */
    @Override
	public boolean isFocused() {
        Component[] components = getComponents();
        if (components != null)
            for (int i = 0; i < components.length; i++) {
                if (components[i].isFocusOwner())
                    return true;
            }
        if (children != null && children.length > 0) {
            for (int j = 0; j < children.length; j++) {
                if (children[j].isFocused())
                    return true;
            }
        }
        if (elementEditors != null && elementEditors.size() > 0) {
            for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
                Editor element = (Editor) iter.next();
                if (element.isFocused())
                    return true;
            }
        }
        return false;
    }

    /**
     * Updates the GUI in case the elementEditors are of type
     * <code>PreviewEditor</code>. Consequently, if one of the objects
     * contained in the collection or map has been changed elsewhere, the
     * preview text displays the changed values of this object.
     */
    protected void updateElementEditors() {
        //				if (hasChanged){
        for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
            Editor elementEditor = (Editor) iter.next();
            elementEditor.updateDisplay();
        }
        updateGUI();
        //				}

    }

    /**
     * Updates the preview texts of the element editors.
     */
    private void updatePreviews() {
        if (elementEditors != null && elementEditors.size() > 0) {
            for (int i = 0; i < elementEditors.size(); i++) {
                if (elementEditors.get(i) instanceof PreviewEditor) {
                    ((PreviewEditor) elementEditors.get(i)).updatePreview();
                }
            }
        }
    }

    /**
     * Returns true if for any of the elementEditors the value currently
     * displayed differs from the actual value in the current state of its
     * <code>editObj</code>. (In this case, the element editor will have to
     * update its GUI.)
     * 
     * @return boolean true if any one of the element editors displays an
     *         obsolete value, false otherwise
     */
    protected boolean elementsHaveChanged() {
        boolean hasChanged = false;
        for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
            Editor elementEditor = (Editor) iter.next();
            if (elementEditor instanceof PreviewEditor) {
                if (((PreviewEditor) elementEditor).hasContentChanged())
                    hasChanged = true;
            } else if (elementEditor instanceof SimpleTextfieldEditor) {
                if (((SimpleTextfieldEditor) elementEditor).hasContentChanged())
                    hasChanged = true;
            }
        }
        return hasChanged;
    }

    // ---- Methods for interacting with the DataChangeManager ----

    /**
     * Overwrites method <code>destroy()</code> of class AbstractEditor in
     * order to remove the element editors from the DataChangeManager's
     * <code>table</code> as well as to remove this editor.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#destroy()
     */
    @Override
	public void destroy() {
        super.destroy();
        if (!elementEditors.isEmpty()) {
            for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
                AbstractEditor element = (AbstractEditor) iter.next();
                element.destroy();
            }
        }
    }

    /**
     * Returns a Vector containing the objects this editor is editing. The
     * Vector does not only contain the collection or map as a whole but also
     * the collection's or map's elements as well as the objects edited by the
     * children editors. <br>
     * Overwrites method <code>getChangedData()</code> in class
     * AbstractEditor. This method is used in <code>sendDataChangeEvent()</code>
     * of the subclasses of AbstractEditor.
     * 
     * @see de.uos.fmt.musitech.framework.editor.AbstractEditor#getChangedData()
     */
    //TODO reicht Methode aus AbstractComplexEditor?
    /*
     * public Collection getEditedData() { ArrayList changedObjects = new
     * ArrayList(); if (super.getEditedData() != null &&
     * !super.getEditedData().isEmpty()) {
     * changedObjects.addAll(super.getEditedData()); } for (Iterator iter =
     * elementEditors.iterator(); iter.hasNext();) { AbstractEditor element =
     * (AbstractEditor) iter.next(); if (element.getEditedData() != null &&
     * !element.getEditedData().isEmpty()) { for (Iterator iterator =
     * element.getEditedData().iterator(); iterator.hasNext(); ) { Object
     * changedObject = iterator.next(); if
     * (!changedObjects.contains(changedObject))
     * changedObjects.add(changedObject); } } } return changedObjects; }
     */

    /**
     * Returns true if the specified <code>editor</code> is contained in this
     * editor's <code>elementEditors</code>.
     * 
     * @param editor
     *            AbstractEditor to be chekced if contained in
     *            <code>elementEditors</code>
     * @return true if the specified <code>editor</code> is containes in
     *         <code>elementEditors</code>, false otherwise
     */
    public boolean isElementEditor(AbstractEditor editor) {
        boolean isElement = false;
        if (elementEditors != null && !elementEditors.isEmpty())
            isElement = elementEditors.contains(editor);
        return isElement;
    }

    /**
     * Getter for <code>elementEditors</code>.
     * 
     * @return Vector containing the editors used to edit the MetaDataItems
     */
    public List getElementEditors() {
        return elementEditors;
    }

    // --------------- methods for editing functionality --------------------

    /**
     * Propagates <code>applyChanges()</code> to all children and to all
     * elements of the vector <code>elementEditors</code>, i. e. to all
     * contained editors.
     */
    @Override
	public void applyChanges() {
        if (profile.isReadOnly())
            return;
        //gather edited data
        Collection editedData = getEditedData();
        //apply changed values
        //propagate to children
        if (children != null) {
            for (int i = 0; i < children.length; i++)
                children[i].applyChanges();
        }
        //propagate to elementEditors (if not PopUpEditor, i.e. an editor
        // opening a new outmost editor)
        if (elementEditors != null && elementEditors.size() > 0) {
            for (Iterator iter = elementEditors.iterator(); iter.hasNext();) {
                Editor elementEditor = (Editor) iter.next();
                if (!(elementEditor instanceof PopUpEditor))
                    elementEditor.applyChanges();
            }
        }
        //copy elements of working copy back into editObj
        writeCopyBackToEditObj();
        //send DataChangeEvent
        if (rootDisplay == this) {
            sendDataChangeEvent(editedData);
        }
        //reset flag
        dataChanged = false;
        //for testing
        //		printCollectionDataForTesting();
    }

    /**
     * Prints the collection's or map's elements to the command line. This
     * method is used for testing only.
     */
    public void printCollectionDataForTesting() {
    }

    /**
     * Prints the working copy's elements to the command line. This method is
     * used for testing only.
     */
    protected void printCopyForTesting() {
    }

    /**
     * Writes the elements contained in the working copy of the subclasses back
     * to the <code>editObj</code>. This method is invoked in method
     * <code>applyChanges()</code>.
     *  
     */
    abstract protected void writeCopyBackToEditObj();

    // --------- methods implementing SelectingEditor -------------

    /**
     * Sets the <code>selectionController</code> of this CollectionMapEditor to the
     * specified SelectionController. If there are element editors, the SelectionAdapter
     * of the SelectionController is added to each of the element editors as a MouseListener.
     * 
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#setSelectionController(de.uos.fmt.musitech.framework.selection.SelectionController)
     */
    @Override
	public void setSelectionController(SelectionController c) {
        this.selectionController = c;
        if (elementEditors.size()>0){
            for (int i = 0; i < elementEditors.size(); i++) {
                ((JComponent)elementEditors.get(i)).addMouseListener(c.getSelectionAdapter());
            }
        }
    }

    
    /** 
     * Returns the MObject displayed at the specified Point. If the Component at the
     * specified Point is not one of the element displays or the <code>editObj</code>
     * of this element editor is not a MObject, null is returned.
     * 
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#objectAt(java.awt.Point)
     */
    @Override
	public MObject objectAt(Point p) {
        Component c = getComponentAt(p);
        if (c instanceof Display) {
            if (elementEditors.contains(c)) {
                if (((Display) c).getEditObj() instanceof MObject)
                    return (MObject) ((Display) c).getEditObj();
            }
        }
        return null;
    }

    /** 
     * Returns a Collection containing the selected MObjects covered graphically by 
     * the specified Rectangle.
     * 
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#objectsTouched(java.awt.Rectangle)
     */
    @Override
	public Collection objectsTouched(Rectangle r){
        Collection objects = new ArrayList();
        int stepX = 10;
        int stepY = 5;
        for (int x = (int)r.getX(); x < (int)(r.getX()+r.getWidth()); x+=stepX) {
            for (int y = (int)r.getY(); y < (int)(r.getY()+r.getHeight()); y+=stepY){
                MObject obj = objectAt(new Point(x,y));
                if (obj!=null && !objects.contains(obj)){
                    objects.add(obj);
                }
            }
        }
        return objects;
    }

    /** 
     * Returns the TimeRange that is covered by the touched objects. If the touched
     * objects contain timed objects, the minimum start time and the maximum end time
     * form the TimeRange. If there are not timed objects or if they do not have
     * valid times, null is returned.
     * 
     * @param r Rectangle covering the selected elements grahically
     * @return TimeRange covered by the selected elements, or null
     * 
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#timeCovered(java.awt.Rectangle)
     */
    @Override
	public TimeRange timeCovered(Rectangle r){
        long start = Long.MAX_VALUE;
        long end = Timed.INVALID_TIME;
        Collection objsTouched = objectsTouched(r);
        for (Iterator iter = objsTouched.iterator(); iter.hasNext();) {
            MObject element = (MObject) iter.next();
            if (element instanceof Timed){
                long beginTime = ((Timed)element).getTime();
                if (beginTime!=Timed.INVALID_TIME && beginTime<start){
                    start = beginTime;
                }
                long endTime = beginTime + ((Timed)element).getDuration();
                if (endTime>=0 && endTime>end){
                    end = endTime;
                }
            }
        }
        if (start==Long.MAX_VALUE){
            start = Timed.INVALID_TIME;
        }
        if (end<start){
            end = start;	//TODO überprüfen
        }
        if (start==Timed.INVALID_TIME && end==Timed.INVALID_TIME)
            return null;
        return new TimeRange(start, end);
    }

    /** 
     *  
     * @see de.uos.fmt.musitech.framework.selection.SelectingEditor#paintDragArea(java.awt.Rectangle)
     */
    @Override
	public void paintDragArea(Rectangle r){
        //TODO highlight selected element editors?
    }

}