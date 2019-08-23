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
 * File EditorFactory.java
 * Created on 23.06.2003
 */
package de.uos.fmt.musitech.framework.editor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;


import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.metadata.MetaDataItem;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.time.TimeLine;
import de.uos.fmt.musitech.utility.collection.TypedCollection;
import de.uos.fmt.musitech.utility.math.Rational;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;
import de.uos.fmt.musitech.utility.obj.ReflectionAccess;
import de.uos.fmt.musitech.utility.text.StringUtils;

/**
 * This class is a Factory for creating instances of <code>Display</code> and 
 * <code>Editor</code>.
 * <br>
 * In order to get a Display, use one of the methods <code>createDisplay(...)</code>.
 * The object returned might be not just a Display, but an Editor. In this case,
 * the Editor is in the mode "read only". <br>
 * In order to get an Editor which allows editing, use one of the methods
 * <code>createEditor(...)</code>. In this case, all inner editors (e.g., for editing
 * the properties of an Object or elements of a Container TODO) will be Editors (and not
 * Displays) as well.
 * <br>
 * If you want to know all types of Display possible for the Object, use method
 * <code>getDisplayTypeNames(Object, EditingProfile)</code>. 
 * Method <code>getEditorTypeNames(Object, EditingProfile)</code> returns only
 * type names which lead to Editors (not just Displays).
 * <br>
 * For a given type name, you can check if it corresponds to an Editor or just to
 * a Display using method <code>isEditor(String)</code>.
 * <br>
 * So, if you do not know if to use <code>createDisplay(...)</code> or 
 * <code>createEditor(...)</code> for a given Object, you ask for the display 
 * type names, choose one and check if it stands for an Editor. Or you directly
 * call for the editor type names and then use <code>createEditor(...)</code> with
 * argument "editortypeName". 
 * <br><br>
 * The EditorFactory class also provides some public methods to create a default 
 * EditingProfile for a given object and methods to wrap an editor by a PopUpEditor, 
 * ExpandEditor or IconEditor. <br>
 * <br>
 * When an exception occurs during the process of creation an
 * <code>EditorConstructionExceptioin</code>- implemented as an inner class
 * of this class - is thrown.
 * 
 * @author Kerstin Neubarth, Tobias Widdra
 */
public class EditorFactory {

	/**
	 * Specifies location of the editor class files (prefix of
	 * editor-classnames).
	 */
	private static final String EDITORPACKAGEHIERACHY =
		"de.uos.fmt.musitech.framework.editor.";

	/**
	 * The type of the editor taken as default for an "outmost" editor when no
	 * other type is given. <br>
	 * An "outmost" editor is an editor where the <code>propertyName</code> is
	 * set to null so it does not edit any property of another object but rather
	 * a given object "itself".
	 */
	private static String defaultOutmostEditorType = "Panel";

	/**
	 * The type of the editor taken as default for an editor editing a specific
	 * property of another object (i. e.<code>propertyName</code> is set).
	 */
	private static String defaultPropertyEditorType = "PopUp";

	/**
	 * This Collection contains Classes whose instances need not have properties,
	 * but nevertheless are to be edited by a complex editor.
	 */
	private static Collection complexClasses;
	static {
		complexClasses = new ArrayList();
		complexClasses.add(Collection.class);
		complexClasses.add(Map.class);
		complexClasses.add(TypedCollection.class);
	}
	
	/**
	 * Collection containing the type names of Displays 
	 * whose class names end on "Display" and for which there are
	 * corresponding editor type names. E.g. "PopUp".
	 * This Collection is used in method <code>createByDisplayType(String)</code>.
	 */
	private static Collection defaultDisplays;
	static {
	    defaultDisplays = new ArrayList();
	    defaultDisplays.add("PopUp");
	    defaultDisplays.add("Preview");
	    defaultDisplays.add("Icon");
	    defaultDisplays.add("Expand");
	    defaultDisplays.add("Collection");
	    defaultDisplays.add("Map");
	}

	static Map classIconMap = new HashMap();
	static {
		classIconMap.put(
			Container.class,
			new ImageIcon(
				EditorFactory.class.getResource("icons/container.gif")));
		classIconMap.put(
			MetaDataItem.class,
			new ImageIcon(
				EditorFactory.class.getResource("icons/metainfo.gif")));
		classIconMap.put(
			TimeLine.class,
			new ImageIcon(EditorFactory.class.getResource("icons/time.gif")));
		classIconMap.put(
			AudioFileObject.class,
			new ImageIcon(EditorFactory.class.getResource("icons/audio.gif")));
		classIconMap.put(
			Piece.class,
			new ImageIcon(EditorFactory.class.getResource("icons/music.gif")));
		classIconMap.put(
			NotationSystem.class,
			new ImageIcon(EditorFactory.class.getResource("icons/note.gif")));
		classIconMap.put(
			NotationStaff.class,
			new ImageIcon(EditorFactory.class.getResource("icons/note.gif")));
	}

	// --------------- methods for creating an editor ------------------------------

	// --------------- public methods ----------------------------------------------

	/**
	 * Creates an appropriate editor. <br>
	 * This is done by calling <code>createEditor(editObj,null)</code>.
	 * 
	 * @param object
	 *            to edit
	 * @return editor
	 * @throws EditorConstructionException
	 */
	public static Editor createEditor(Object editObj)
		throws EditorConstructionException {
		return createEditor(editObj, null);
	}

	/**
	 * This method creates an editor according to the given EditingProfile
	 * <code>profile</code>. If the profile is null
	 * <code>getOrCreateProfile(editObj)</code> is invoked. <br>
	 * Since createByEditorClassName is invoked by this method different reasons
	 * for an EditorConstructionException might occur. It is thrown as well if
	 * <code>editObj</code> is null! <br>
	 * If <code>propertyName</code> in <code>profile</code> is null it is
	 * assumend that this editor is the outmost editor. This means the editor is
	 * constructed for <code>editObj</code> itself, otherwise it is
	 * constructed for the property <code>propertyName</code> of
	 * <code>editObj</code>.
	 * 
	 * @param object
	 *            to construct the editor for - or object which has the property
	 *            <code>propertyName</code> to be the edited
	 * @param profile
	 * @return editor object
	 * @throws EditorConstructionException
	 */
	public static Editor createEditor(Object editObj, EditingProfile profile)
		throws EditorConstructionException {
		return createEditor(editObj, profile, null, null);
	}

	/**
	 * Returns an Editor for the specified Object <code>editObj</code>
	 * constructed according to the specified EditingProfile
	 * <code>profile</code>. If <code>profile</code> is null, an
	 * EditingProfile is asked form the Object <code>editObj</code> or a
	 * default-profile is created. The editor to be constructed is given the
	 * specified <code>rootEditor</code>.
	 * 
	 * @param editObj Object for which an Editor is to be created
	 * @param profile EditingProfile specifying how to edit the Object, or null
	 * @param rootEditor Display being the top parent of the Editor to be created, or null 
	 * @return Editor editing the specified Object according to the specified EditingProfile 
	 * @throws
	 *         EditorConstructionException
	 */
	public static Editor createEditor(
		Object editObj,
		EditingProfile profile,
		Display rootEditor)
		throws EditorConstructionException {
		return createEditor(editObj, profile, null, rootEditor);
	}

	/**
	 * Returns an Editor for the specified <code>editObj</code> of the type
	 * given by <code>editortype</code>. The editor's <code>rootEditor</code>
	 * is set to the specified <code>rootEditor</code> if it is not null, to
	 * the editor itself if argument <code>rootEditor</code> is null.
	 * 
	 * @param editObj
	 *            Object the Editor is to be created for
	 * @param rootEditor
	 *            Editor to be the <code>rootEditor</code> of the created
	 *            Editor, or null
	 * @param editortype
	 *            String indicating the type of the Editor to be created, or null
	 * @return Editor
	 * @throws EditorConstructionException
	 */
	public static Editor createEditor(
		Object editObj,
		Display rootEditor,
		String editortype)
		throws EditorConstructionException {
		return createEditor(editObj, null, editortype, rootEditor);
	}

	/**
	 * Returns an Editor for the specified Object. The Object argument must not be null.
	 * All other arguments might be null.
	 * <br>
	 * If the EditingProfile is null, a profile is provided either asking the 
	 * EditingProfile from the <code>editObj</code> if the Object is an Editable or
	 * creating a default profile.
	 * <br>
	 * If the String is null, the default editortype specified in the EditingProfile
	 * is taken. 
	 * If the profile does not specify an editor type, the editor type is asked 
	 * from the EditorRegistry. 
	 * If the EditorRegistry does not provide an editor type, the
	 * <code>defaultOutmostEditorType</code> or <code>defaultPropertyEditorType</code>
	 * (if the Editor is to edit a property) is taken.
	 * <br>
	 * If the Display is null, the root of the created Editor will be the Editor itself
	 * (i.e., the Editor will be an outmost Editor).
	 * <br>
	 * If the Object is null, an EditorConstructionException is thrown.
	 * 
	 * @param editObj Object to be edited
	 * @param profile EditingProfile specifying how to edit the Object
	 * @param editortype String indicating the editor type which should be created
	 * @param root Display to be the root display of the Editor
	 * @return Editor, or null if no Editor could be created
	 * @throws EditorConstructionException if the specified Object is null
	 * 
	 */
	public static Editor createEditor(
		Object editObj,
		EditingProfile profile,
		String editorTypeName,
		Display root)
		throws EditorConstructionException {
		//check argument editObj
		if (editObj == null) {
			throw new EditorConstructionException(
				"In EditorFactory.createEditor(Object, EditingProfile, String, Display)\n"
					+ "The Object argument must not be null.");
		}
		//get or create EditingProfile if necessary
		if (profile == null) {
			profile = getOrCreateProfile(editObj);
		}
		//if no editortype specified, get from profile or EditorRegistry
		if (editorTypeName == null) {
			//			editorTypeName = getEditortypeName(editObj, profile);
			editorTypeName = getFirstEditortypeName(editObj, profile);
		}
		//create editor via EditorType and initialize it
		Editor editor = createByEditortype(editorTypeName);
		if (editor != null) {
			if (root != null)
				editor.init(editObj, profile, root);
			else
				editor.init(editObj, profile, editor);
		}
		return editor;
	}

	/**
	 * Returns an Editor for the specified Object. If <code>readOnly</code> is true,
	 * this Editor does not allow editing, but only displaying the <code>editObj</code>.
	 * If <code>readOnly</code> is false, the Editor will allow editing.
	 * 
	 * @param editObj Object to be edited or displayed
	 * @param readOnly boolean indicating if the Editor will be for editing or displaying the <code>editObj</code>
	 * @return Editor displaying (if <code>readOnly</code> is false) or editing (if <code>readOnly</code> is true) the <code>editObj</code>
	 * @throws EditorConstructionException
	 */
	public static Editor createEditor(Object editObj, boolean readOnly)
		throws EditorConstructionException {
		return createEditor(editObj, readOnly, null, null);
	}

	/**
	 * Returns an Editor for the specified Object. The Editor will be of the type
	 * indicated by the specified String.  
	 * If <code>readOnly</code> is true, this Editor does not allow editing, 
	 * but only displaying the <code>editObj</code>.
	 * If <code>readOnly</code> is false, the Editor will allow editing.
	 * 
	 * @param editObj Object to be edited or displayed
	 * @param readOnly boolean indicating if the Editor will display (if true) or edit (if false) the <code>editObj</code>
	 * @param editorTypeName String indicating the type of editor to be created, may be null
	 * @param root Display to be set as root display of the created Editor, may be null
	 * @return Editor of the spcified type for displaying or editing the specified Object
	 * @throws EditorConstructionException
	 */
	public static Editor createEditor(
		Object editObj,
		boolean readOnly,
		String editorTypeName,
		Display root)
		throws EditorConstructionException {
		if (editObj == null)
			throw new EditorConstructionException(
				"In EditorFactory.createEditor(Object, EditingProfile, String, Display)\n"
					+ "The Object argument must not be null.");
		EditingProfile profile = getOrCreateProfile(editObj);
		profile.setReadOnly(readOnly);
		return createEditor(editObj, profile, editorTypeName, root);
	}
	
	/**
	 * Returns an Editor for the specified Object which is of the specified Class and
	 * edits the Object according to the specified EditingProfile.
	 * If no Editor could be created from the specified Class, 
	 * an EditorConstructionException is thrown.
	 * <br>
	 * All arguments except of the <code>editObj</code> might be null.
	 * 
	 * @param editObj Object for which an Editor is created, must not be null
	 * @param profile EditingProfile specifying how to edit the Object, or null
	 * @param editorClass Class of an Editor, or null
	 * @param root Display to be set as top parent of the Editor to be created, or null
	 * @return Editor of the specified Class editing the specified Object
	 * @throws EditorConstructionException
	 */
	public static Editor createEditor(Class editorClass, Object editObj, EditingProfile profile, Display root)
		throws EditorConstructionException {
	    if (editorClass==null)
	        return createEditor(editObj, profile, root);
	    if (editObj==null){
	        throw new EditorConstructionException("In EditorFactory.createEditor(Class, Object, EditingProfile, Display)\n"
	        +"The Object argument must not be null.");
	    }
	    if (!Editor.class.isAssignableFrom(editorClass)){
	        throw new EditorConstructionException("In EditorFactory.createEditor(Class, Object, EditingProfile, Display)\n"
	    	+ "The specified Class is no valid Editor class.");
	    }
	    if (profile==null)
	        profile = getOrCreateProfile(editObj);
	    Object editor = null;
	    try {
            editor = editorClass.newInstance();
        } catch (InstantiationException ie) {
            throw new EditorConstructionException("In EditorFactory.createEditor(Class, Object, EditingProfile, Display)\n"
        	    	+ "No instance could be created from the specified Class.");
        } catch (IllegalAccessException iae){
            throw new EditorConstructionException("In EditorFactory.createEditor(Class, Object, EditingProfile, Display)\n"
        	    	+ "The specified Class denies access.");
        }
        if (editor!=null && editor instanceof Editor){
            if (root!=null)
                ((Editor)editor).init(editObj, profile, root);
            else
                ((Editor)editor).init(editObj, profile, (Display)editor); 
        }
        if (editor==null)
            throw new EditorConstructionException("In EditorFactory.createEditor(Class, Object, EditingProfile, Display)\n"
        	        +"The EditorFactory could not create an Editor of the specified Class.");
	    return (Editor)editor;
	}

	// ----------- methods for creating a wrapper editor -----------------------

	/**
	 * This method returns a new <code>PopUpEditor</code> and sets the given
	 * editor <code>editor</code> as <code>editorToPopUp</code>.<br>
	 * So this method can be considered as providing a "wrapper-PopUpEditor"
	 * around a given editor.
	 * 
	 * @param editor Editor for which the PopUpWrapper is requested 
	 * @return Editor a PopUpEditor wrapping the specified Editor
	 */
	public static Editor createPopUpWrapper(Editor editor) { 
		EditingProfile profile = new EditingProfile();
		profile.setEditortype("PopUp");
		profile.setLabel(editor.getEditingProfile().getLabel());
		profile.setPropertyName(editor.getEditingProfile().getPropertyName());
		try {
			PopUpEditor popup =
				(PopUpEditor) createEditor(editor.getEditObj(), profile);
			popup.setWrappedView(editor);
			return popup;
		} catch (EditorConstructionException e) {
			// this should be only possible to happen if the class PopUpEditor
			// is missing!
			e.printStackTrace();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns an ExpandEditor wrapping the specified <code>editorToWrap</code>.
	 * 
	 * @param editorToWrap
	 *            Editor to be wrapped by an ExpandEditor
	 * @return Editor wrapping ExpandEditor
	 */
	public static Editor createExpandWrapper(Editor editorToWrap) {
		EditingProfile profile = new EditingProfile();
		profile.setEditortype("Expand");
		profile.setLabel(editorToWrap.getEditingProfile().getLabel());
		profile.setPropertyName(
			editorToWrap.getEditingProfile().getPropertyName());
		try {
			ExpandEditor expandEditor =
				(ExpandEditor) EditorFactory.createEditor(
					editorToWrap.getEditObj(),
					profile);
			if (expandEditor != null)
				expandEditor.setWrappedView(editorToWrap);
			return expandEditor;
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates an IconEditor wrapping the specified <code>editor</code>. That
	 * is, returns an IconEditor whose <code>editorToPopUp</code> is the
	 * specified Editor.
	 * 
	 * @param editor
	 *            Editor to be wrapped by an IconEditor
	 * @return IconEditor wrapping the specified Editor
	 */
	public static Editor createIconWrapper(Editor editor, Icon icon) {
		IconEditor iconWrapper = null;
		EditingProfile wrapperProfile = editor.getEditingProfile();
		wrapperProfile.setEditortype("icon");
		wrapperProfile.setIcons(icon);
		try {
			iconWrapper =
				(IconEditor) createEditor(editor.getEditObj(), wrapperProfile);
			iconWrapper.setWrappedView(editor);
		} catch (EditorConstructionException e) {
			e.printStackTrace();
		}
		return iconWrapper;
	}
	
	/**
	 * Returns a Display which wraps the <code>displayToWrap</code>.
	 * The <code>wrappingType</code> may be "PopUp", "Icon" or "Expand".
	 * If no <code>wrapperType</code> is specified, a PopUpDisplay is created.
	 * 
	 * @param wrapperType String giving the editor type name of the wrapping view
	 * @param displayToWrap Display to be wrapped
	 * @return Display wrapping the specified Display
	 */
	public static Display createWrappingDisplay(String wrapperType, Display displayToWrap)
		throws EditorConstructionException {
	    if (displayToWrap == null){
	        throw new EditorConstructionException("in EditorFactory.createWrappingDisplay(String, Display):\nThe Display argument must not be null.");
	    }
	    if (wrapperType==null){
	        wrapperType = "PopUp";
	    }
	    Display wrapper = null;
//	    EditingProfile wrapperProfile = (EditingProfile) ObjectCopy.copyObject(displayToWrap.getEditingProfile());
	    EditingProfile wrapperProfile = displayToWrap.getEditingProfile();
	    wrapperProfile.setEditortype(wrapperType);
	    try {
            wrapper = createDisplay(displayToWrap.getEditObj(), wrapperProfile);
            if (wrapper instanceof Wrapper){
                ((Wrapper)wrapper).setWrappedView(displayToWrap);
            }
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return wrapper;
	}
	
	/**
	 * Returns an Editor which wraps the <code>editorToWrap</code>.
	 * The <code>wrappingType</code> may be "PopUp", "Icon" or "Expand".
	 * If no <code>wrapperType</code> is specified, a PopUpEditor is created.
	 * 
	 * @param wrapperType String giving the editor type name of the wrapping view
	 * @param editorToWrap Editor to be wrapped
	 * @return Editor wrapping the specified Editor
	 */
	public static Editor createWrappingEditor(String wrapperType, Editor editorToWrap)
		throws EditorConstructionException {
	    if (editorToWrap == null){
	        throw new EditorConstructionException("In EditorFactory.createWrappingEditor(String, Editor):\nThe argument Editor must not be null.");
	    }
	    if (wrapperType==null){
	        wrapperType = "PopUp";
	    }
	    Editor wrapper = null;
	    EditingProfile wrapperProfile = (EditingProfile) ObjectCopy.copyObject(editorToWrap.getEditingProfile());
	    wrapperProfile.setEditortype(wrapperType);
	    try {
            wrapper = createEditor(editorToWrap.getEditObj(), wrapperProfile);
            if (wrapper instanceof Wrapper){
                ((Wrapper)wrapper).setWrappedView(editorToWrap);
            }
        } catch (EditorConstructionException e) {
            e.printStackTrace();
        }
        return wrapper;
	}
	

	//-------------- non public methods for creating an Editor -------------

	/**
	 * Returns an Editor of the type indicated by the <code>editorTypeName</code>.
	 * 
	 * @param editorTypeName String giving the type of editor to create
	 * @return Editor of the type specified by the String argument
	 * @throws EditorConstructionException
	 */
	private static Editor createByEditortype(String editorTypeName)
		throws EditorConstructionException {
		//get editor class via EditorType
		String editorClassName =
			EditorRegistry.getEditorClassName(editorTypeName);
		//			getEditorClassName(editorTypeName);
		//if editorClassName is null, create class name from type name (default mechanism)
		if (editorClassName == null) {
			editorClassName = getEditorClassName(editorTypeName);
		}
		//create Editor
		Editor editor = createByEditorClassName(editorClassName);
		return editor;
	}

	/**
	 * This method creates an editor by the given editor classname
	 * <code>className</code>. This method returns an editor constructed by
	 * the empty constructor. Invoke method <code>construct(...)</code> before
	 * using it! 
	 * TODO Kommentar aktualisieren 
	 * 
	 * @param complete
	 *            className
	 * @return constructed editor
	 * @throws EditorConstructionException
	 *             if some construction exception occurs, e. g. class not found
	 */
	private static Editor createByEditorClassName(String className)
		throws EditorConstructionException {

		// get class
		Class cla = null;
		try {
			cla = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new EditorConstructionException(
				"Editorclass \"" + className + "\" not found.");
		}

		// get Constructor
		Constructor constructor = null;
		try {
			constructor = cla.getConstructor(new Class[0]);
		} catch (SecurityException e2) {
			throw new EditorConstructionException(
				"Access to information in Editorclass \""
					+ className
					+ "\" is denied.");
		} catch (NoSuchMethodException e2) {
			throw new EditorConstructionException(
				"Editorclass \""
					+ className
					+ "\" must have a public empty constructor.");
		}

		// get instance
		Object obj = null;
		try {
			obj = constructor.newInstance(new Object[0]);
		} catch (IllegalArgumentException e1) {
			throw new EditorConstructionException(
				"Problems instantiating Editorclass \"" + className + "\".");
		} catch (InstantiationException e1) {
			throw new EditorConstructionException(
				"Editorclass \"" + className + "\" is abstract.");
		} catch (IllegalAccessException e1) {
			throw new EditorConstructionException(
				"Problems instantiating Editorclass \"" + className + "\".");
		} catch (InvocationTargetException e1) {
			throw new EditorConstructionException(
				"Problems instantiating Editorclass \"" + className + "\".");
		}

		// check whether instance of Editor
		if (!(obj instanceof Editor)) {
			throw new EditorConstructionException(
				"Class \"" + className + "\" is not a valid editorclass.");
		}

		return (Editor) obj;
	}

	/**
	 * This method creates editors for children of <code>editObj</code>
	 * according to the given editing profile <code>profile</code>.<br>
	 * <br>
	 * It is recursive by calling createEditor which meight call this method
	 * again.
	 * <br><br>
	 * This method is public to be used in class AbstractComplexEditor.
	 * 
	 * @param editObj
	 * @param childrenProfiles
	 * @return editors for children
	 */
	/*	public static Editor[] createChildrenEditors(
			  Object editObj,
			  EditingProfile profile)
			  throws EditorConstructionException {
		
			  ReflectionAccess ref =
				  ReflectionAccess.accessForClass(editObj.getClass());
			  EditingProfile[] childrenProfiles = profile.getChildren();
			  Vector v = new Vector();
			  for (int i = 0; i < childrenProfiles.length; i++) {
		
				  EditingProfile childProfile = childrenProfiles[i];
				  Editor editor = null;
				  if (ref.hasPropertyName(childProfile.getPropertyName())) {
					  try {
						  editor = createEditor(editObj, childProfile);
					  } catch (Exception e) {
						  // exception if profile is broken
						  e.printStackTrace();
					  } // end try/catch
				  }
		
				  // editor construction might be unsuccessful!
				  if (editor != null)
					  v.add(editor);
			  }
		
			  Editor[] children = new Editor[v.size()];
			  int i = 0;
			  for (Iterator iter = v.iterator(); iter.hasNext();) {
				  children[i++] = (Editor) iter.next();
			  }
			  return children;
		  }
	*/

	/** Returns the name of a class "implementing" the given editortype.
	 *
	 * This method tries first to find an appropriate class by looking up in the
	 * internal hashtable <code>editorTypeOntoEditorClassName</code>.<br> 
	 * If this is unsuccessfull a classname is constructed according to
	 * conventional aspects : 
	 * <br>set the first letter to upper case
	 * <br>add "Editor" at the end if not already done
	 * <br>add(or replace) the package hierarchy <code>editorPackageHirachy</code> 
	 * in front of the name.<br> 
	 * Notice that this might cause an error later on if the editor class does not 
	 * exist in the right package.
	 * TODO update comment
	 * 
	 * @param editortype
	 * @return classname of editor for editortype <code>editortype</code>.
	 */
	protected static String getEditorClassName(String editortype) {

		String classname;
		// look up in hashtable
		//		classname = (String) editorTypeOntoEditorClassName.get(editortype);
		//		if (classname != null)
		//			return classname;
		// create default
		if (editortype.equalsIgnoreCase("java.lang.Integer"))
			editortype = "int";
		if (editortype.equalsIgnoreCase("java.lang.Character"))
			editortype = "char";
		editortype = firstToUpperCase(editortype);
		if (!(editortype.endsWith("Editor")))
			editortype = editortype + "Editor";
		if (editortype.indexOf(".") == -1)
			editortype = EDITORPACKAGEHIERACHY + editortype;
		else
			editortype =
				EDITORPACKAGEHIERACHY
					+ editortype.substring(editortype.lastIndexOf('.') + 1);
		return editortype;
	}

	/**
	 * Return string s with first letter in uppercase.
	 * 
	 * @param s
	 * @return
	 */
	private static String firstToUpperCase(String s) {
		return s.substring(0, 1).toUpperCase().concat(
			s.substring(1, s.length()));
	}

	/**
	 * Return string s with first letter in lowercase.
	 * 
	 * @param s
	 * @return
	 */
	private static String firstToLowerCase(String s) {
		return s.substring(0, 1).toLowerCase().concat(
			s.substring(1, s.length()));
	}

	/**
	 * Inserts blanks in front of all upper case letters (except the first one).
	 * So "IAmAWonderfulString" becomes "I Am A Wonderful String".
	 * 
	 * @param s
	 * @return
	 */
	private static String insertBlanks(String s) {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (Character.isUpperCase(s.charAt(i)) && i > 0) {
				sb.append(' ');
			}
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	/**
	 * Returns a default label by taking the name of class <code>cla</code>
	 * and cutting off the package hierarchy.
	 * 
	 * @param cla
	 * @return default label
	 */
	private static String createDefaultLabel(Class cla) {
		// set label per default to classname
		String label = cla.toString();
		label = label.substring(label.lastIndexOf(".") + 1);
		
		return insertBlanks(label);
	}

	// ------------ methods for creating a Display ---------------------------

	// ------------ public methods -------------------------------------------

	/**
	 * Returns a Display for the specified <code>editObj</code>.
	 * 
	 * @param editObj Object to be displayed
	 * @return Display displaying the specified Object
	 * @throws EditorConstructionException
	 */
	public static Display createDisplay(Object editObj)
		throws EditorConstructionException {
	    return createDisplay(editObj, null, null, null);
	}
	
	/**
	 * Returns a Display for the specified <code>editObj</code>. The Display
	 * is created according to the parameters given in the specified
	 * EditingProfile. The specified EditingProfile might be null.
	 * 
	 * @param editObj
	 *            Object for which the Display is to be created
	 * @param profile
	 *            EditingProfile specifying how the Display is to be created, or null
	 * @return Display created for the specified Object according to the
	 *         specified EditingProfile
	 * @throws EditorConstructionException
	 */
	public static Display createDisplay(Object editObj, EditingProfile profile)
		throws EditorConstructionException {
		return createDisplay(editObj, profile, null, null);
	}
	
	/**
	 * Returns a Display for the specified <code>editObj</code>. For a
	 * Display which is also an Editor, the boolean argument specifies, if 
	 * it is to be used as Display (if <code>readOnly</code> is true)
	 * or as Editor which allows editing the Object (if <code>readOnly</code>
	 * is false).
	 * <br>
	 * (A Display which is not an Editor is always run in the read only-mode,
	 * i.e. the argument does not influence its creation and behaviour. If the
	 * <code>displayTypeName</code> does not lead to an Editor, the Display is
	 * created as read only Display even if the specified <code>readOnly</code>
	 * is false.)
	 * 
	 * @param editObj Object to be displayed
	 * @param displayTypeName String indicating which type of Display to create
	 * @param readOnly boolean specifying the mode in which a Display being an Editor is set up
	 * @return Display for the specified Object
	 * @throws EditorConstructionException
	 */
	public static Display createDisplay(Object editObj, String displayTypeName, boolean readOnly)
		throws EditorConstructionException {
	    EditingProfile profile = EditorFactory.getOrCreateProfile(editObj);
	    if (!isEditor(displayTypeName))
	        profile.setReadOnly(true);
	    else
	        profile.setReadOnly(readOnly);
	    return createDisplay(editObj, profile, displayTypeName, null);
	}
	
	/**
	 * Returns a Display for the specified <code>editObj</code> which is of the type
	 * indicated by the specified String and displaying the Object according to the
	 * specified EditingProfile.
	 * <br>
	 * The Object argument must not be null, else an EditorConstructionException is
	 * thrown. The arguments <code>profile</code> and/or <code>displayTypeName/code>
	 * can be null. In this case, the EditingProfile resp. String are provided by
	 * the EditorFactory.
	 * 
	 * @param editObj Object for which a Display is to be created
	 * @param profile EditingProfile specifying how to display the <code>editObj</code>, or null
	 * @param displayTypeName String indicating the type of the Display to be created, or null
	 * @return Display of the specified type displaying the specified Object according to the specified EditingProfile 
	 * @throws EditorConstructionException
	 */
	public static Display createDisplay(Object editObj, EditingProfile profile, String displayTypeName)
		throws EditorConstructionException {
	    return createDisplay(editObj, profile, displayTypeName, null);
	}
	
	/**
	 * Returns a Display for the specified Object. The Object argument must not be null.
	 * All other arguments might be null.
	 * <br>
	 * If the EditingProfile is null, a profile is provided either asking the 
	 * EditingProfile from the <code>editObj</code> if the Object is an Editable or
	 * creating a default profile. In the profile, the "read only" mode is set.
	 * <br>
	 * If the String is null, the default editortype specified in the EditingProfile
	 * is taken. 
	 * If the profile does not specify an editor type, the editor type is asked 
	 * from the EditorRegistry. 
	 * If the EditorRegistry does not provide an editor type, the
	 * <code>defaultOutmostEditorType</code> or <code>defaultPropertyEditorType</code>
	 * (if the Editor is to edit a property) is taken.
	 * <br>
	 * If the Display is null, the root of the created Display will be the Display itself
	 * (i.e., the Display will be an outmost Display).
	 * <br>
	 * If the Object is null, an EditorConstructionException is thrown.
	 * 
	 * @param editObj Object to be displayed
	 * @param profile EditingProfile specifying how to display the Object, or null
	 * @param displayTypeName String indicating which type of display should be created, or null
	 * @param root Display to be the root display of the created Display, or null
	 * @return Display, or null if no Display could be created
	 * @throws EditorConstructionException
	 */
	public static Display createDisplay(
		Object editObj,
		EditingProfile profile,
		String displayTypeName,
		Display root)
		throws EditorConstructionException {
		//check argument editObj
		if (editObj == null) {
			throw new EditorConstructionException(
				"In EditorFactory.createEditor(Object, EditingProfile, String, Display)\n"
					+ "The Object argument must not be null.");
		}
		//get or create EditingProfile if necessary
		if (profile == null) {
			profile = getOrCreateProfile(editObj);
			profile.setReadOnly(true);
		}
		//if no editortype specified, get from profile or EditorRegistry
		if (displayTypeName == null) {
			//			displayTypeName = getEditortypeName(editObj, profile);
			displayTypeName = getDisplayTypeNames(editObj, profile)[0];
		}
		//create and initialize display
		Display display = createByDisplaytype(displayTypeName);
		if (display != null) {
			if (root != null)
				display.init(editObj, profile, root);
			else
				display.init(editObj, profile, display);
		}
		return display;
	}
	/**
	 * Returns a Display for the specified Object which is of the specified Class and
	 * displays the Object according to the specified EditingProfile.
	 * If no Display could be created from the specified Class, 
	 * an EditorConstructionException is thrown.
	 * <br>
	 * All arguments except of the <code>editObj</code> might be null.
	 * 
	 * @param editObj Object for which a Display is created, must not be null
	 * @param profile EditingProfile specifying how to display the Object, or null
	 * @param displayClass Class of a Display, or null
	 * @param root Display to be set as top parent of the Display to be created, or null
	 * @return Display of the specified Class displaying the specified Object
	 * @throws EditorConstructionException
	 */
	public static Display createDisplay(Class displayClass, Object editObj, EditingProfile profile, Display root)
		throws EditorConstructionException {
	    if (displayClass==null)
	        return createDisplay(editObj, profile, null, root);
	    if (editObj==null){
	        throw new EditorConstructionException("In EditorFactory.createDisplay(Class, Object, EditingProfile, Display)\n"
	        +"The Object argument must not be null.");
	    }
	    if (!Display.class.isAssignableFrom(displayClass)){
	        throw new EditorConstructionException("In EditorFactory.createDisplay(Class, Object, EditingProfile, Display)\n"
	    	+ "The specified Class is no valid Display class.");
	    }
	    if (profile==null)
	        profile = getOrCreateProfile(editObj);
	    Object display = null;
	    try {
            display = displayClass.newInstance();
        } catch (InstantiationException ie) {
            throw new EditorConstructionException("In EditorFactory.createDisplay(Class, Object, EditingProfile, Display)\n"
        	    	+ "No instance could be created from the specified Class.");
        } catch (IllegalAccessException iae){
            throw new EditorConstructionException("In EditorFactory.createDisplay(Class, Object, EditingProfile, Display)\n"
        	    	+ "The specified Class denies access.");
        }
        if (display!=null && display instanceof Display){
            if (root!=null)
                ((Display)display).init(editObj, profile, root);
            else
                ((Display)display).init(editObj, profile, (Display)display); 
        }
        if (display==null)
            throw new EditorConstructionException("In EditorFactory.createDisplay(Object, EditingProfile, Class, Display)\n"
        	        +"The EditorFactory could not create a Display of the specified Class.");
	    return (Display)display;
	}

	// ------------ non public methods for creating a Display ----------------

	/**
	 * Returns a Display corresponding to the specified
	 * <code>displayClassName</code>.
	 * 
	 * @param displayClassName
	 *            String giving the class name of the Display to create
	 * @return Display instance of the Display class indicated by the specified
	 *         String
	 * @throws EditorConstructionException
	 */
	private static Display createByDisplayClassName(String displayClassName)
		throws EditorConstructionException {
		// get class
		Class cla = null;
		try {
			cla = Class.forName(displayClassName);
		} catch (ClassNotFoundException e) {
			throw new EditorConstructionException(
				"Display class \"" + displayClassName + "\" not found.");
		}
		// get Constructor
		Constructor constructor = null;
		try {
			constructor = cla.getConstructor(new Class[0]);
		} catch (SecurityException e2) {
			throw new EditorConstructionException(
				"Access to information in Display class \""
					+ displayClassName
					+ "\" is denied.");
		} catch (NoSuchMethodException e2) {
			throw new EditorConstructionException(
				"Display class \""
					+ displayClassName
					+ "\" must have a public empty constructor.");
		}
		// get instance
		Object obj = null;
		try {
			obj = constructor.newInstance(new Object[0]);
		} catch (IllegalArgumentException e1) {
			throw new EditorConstructionException(
				"Problems instantiating Display class \""
					+ displayClassName
					+ "\".");
		} catch (InstantiationException e1) {
			throw new EditorConstructionException(
				"Display class \"" + displayClassName + "\" is abstract.");
		} catch (IllegalAccessException e1) {
			throw new EditorConstructionException(
				"Problems instantiating Display class \""
					+ displayClassName
					+ "\".");
		} catch (InvocationTargetException e1) {
			throw new EditorConstructionException(
				"Problems instantiating Display class \""
					+ displayClassName
					+ "\".");
		}
		// check whether instance of Display
		if (!(obj instanceof Display)) {
			throw new EditorConstructionException(
				"Class \""
					+ displayClassName
					+ "\" is not a valid Display class.");
		}
		return (Display) obj;
	}

	/**
	 * Returns a Display of the type indicated by the <code>displayTypeName</code>.
	 * 
	 * @param displayTypeName String giving the type of the Display to create
	 * @return Display of the type specified by the String argument
	 * @throws EditorConstructionException
	 */
	private static Display createByDisplaytype(String displayTypeName)
		throws EditorConstructionException {
		//get editor class via EditorType
		String displayClassName =
			EditorRegistry.getEditorClassName(displayTypeName);
		//if editorClassName is null, create class name from type name (default mechanism)
		if (displayClassName == null) {
			displayClassName = getEditorClassName(displayTypeName);
			//Modif040817
			if (isDefaultDisplayType(displayTypeName))
				if (displayClassName.endsWith("Editor")){
					StringBuffer stringBuffer = new StringBuffer(displayClassName.substring(0,displayClassName.length()-"Editor".length()));
					stringBuffer.append("Display");
					displayClassName = stringBuffer.toString();
				}
			//end Modif040817 
		}
		//create Editor
		Display display = createByDisplayClassName(displayClassName);
		return display;
	}
	
	/**
	 * Returns true if the specified String is the name of a default Display
	 * as specified by the <code>defaultDisplays</code>. Default displays are
	 * displays whose class name ends on "Display" appended to a abbreviated
	 * object class name. In general, there will be an Editor with similar name.
	 * E.g. "PopUpDisplay" and "PopUpEditor". The <code>defaultTypes</code> are
	 * used to distinguidh between those Displays and Editors.
	 * 
	 * @param displayTypeName String specifying the short type name of a Display
	 * @return true if the specified String is contained (case ignored) in the <code>defaultDisplays</code>
	 */
	private static boolean isDefaultDisplayType(String displayTypeName){
	    for (Iterator iter = defaultDisplays.iterator(); iter.hasNext();) {
            String typeName = (String) iter.next();
            if (typeName.equalsIgnoreCase(displayTypeName))
                return true;
        }
	    return false;
	}

	// ------------ merhods for creating a wrapper Display -------------------

	/**
		 * This method returns a <code>PopUpDisplay</code> and sets the specified
		 * Display as its <code>displayToPopUp</code>.<br>
		 * So this method can be considered as providing a "wrapper-PopUpDisplay"
		 * around a given Display.
		 * 
		 * @param display Display for which the PopUpWrapper is requested 
		 * @return Display a PopUpDisplay wrapping the specified Display
		 */
	public static Display createPopUpWrapper(Display display) { 
		EditingProfile profile = new EditingProfile();
		profile.setEditortype("PopUp");
		profile.setLabel(display.getEditingProfile().getLabel());
		profile.setPropertyName(display.getEditingProfile().getPropertyName());
		try {
			PopUpDisplay popup =
				(PopUpDisplay) createDisplay(display.getEditObj(), profile, "PopUp", null);
			popup.setWrappedView(display);
			return popup;
		} catch (EditorConstructionException e) {
			// this should be only possible to happen if the class PopUpEditor
			// is missing!
			e.printStackTrace();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
		}
		return null;
	}

	// -------- methods for determining the EditingProfile --------------------

	/**
	 * Returns either profile via getEditingProfile (if <code>editObj</code>
	 * is instance of <code>Editable</code>) or a default profile is created
	 * by <code>createDefaultProfile(editObj)</code>.
	 * 
	 * @param editObj
	 * @return profile for <code>editObj</code>
	 */
	public static EditingProfile getOrCreateProfile(Object editObj) {
		if (editObj instanceof Editable)
			return ((Editable) editObj).getEditingProfile();
		else
			return createDefaultProfile(editObj);
	}

	/**
	 * Constructs a profile by using reflection access to the class of
	 * <code>editObj</code>. This is actually done by invoking
	 * <code>createDefaultProfile(editObj,null)</code>.
	 * 
	 * @param editObj Object for which the EditingProfile is to be created
	 * @return EditingProfile default profile for the specified Object
	 */
	public static EditingProfile createDefaultProfile(Object editObj) {
		return createDefaultProfile(editObj, null);
	}

	/**
	 * Returns a default EditingProfile for the specified Object. 
	 * 
	 * @param editObj Object for which the EditingProfile is to be created
	 * @param propertyName String indicating the property to be edited, migth be null
	 * @return EditingProfile default profile for the specified Object or its property identified by the specified String
	 */
	public static EditingProfile createDefaultProfile(
		Object editObj,
		String propertyName) {
		EditingProfile profile = new EditingProfile();
		//set propertyName (if not null) and label
		if (propertyName != null) {
			profile.setLabel(StringUtils.capitalizeFirst(insertBlanks(propertyName)));
			//TODO UpperCase?
			profile.setPropertyName(propertyName);
		} else {
			// set label per default to classname
			profile.setLabel(StringUtils.capitalizeFirst(createDefaultLabel(editObj.getClass())));
			if (ObjectCopy.isPrimitiveType(editObj)
				|| editObj instanceof String)
				profile.setReadOnly(true);
		}
		//set editortype
		String[] editortypes = getDisplayTypeNames(editObj, profile);
		//derives editortype from class name
		if (editortypes != null) {
			if (editortypes.length == 1)
				profile.setEditortype(editortypes[0]);
			if (editortypes.length > 1)
				profile.setEditortype(editortypes);
		}
		//if complex (that is, if there are accessible properties), set
		// children
		profile = setChildrenProfiles(editObj, profile);
		return profile;
	}

	/**
	 * Creates the children EditingProfiles for the specified Object and sets them
	 * in the specified EditingProfile. The children profiles might be null, if there
	 * are no properties for which profiles are needed.
	 * Returns the specified profile with the children profiles set.
	 * 
	 * @param editObj Object to be edited
	 * @param profile EditingProfile for the <code>editObj</code>
	 * @return EditingProfile the <code>profile</code> with its children profiles set (they might be null)
	 */
	private static EditingProfile setChildrenProfiles(
		Object editObj,
		EditingProfile profile) {
		Object objEdited = null;
		if (profile.getPropertyName() == null)
			objEdited = editObj;
		else
			objEdited = getPropertyForName(editObj, profile.getPropertyName());
		if (objEdited != null) {
			ReflectionAccess ref =
				ReflectionAccess.accessForClass(objEdited.getClass());
			String[] propertyNames = ref.getPropertyNames();
			if (propertyNames != null && propertyNames.length > 0) {
				EditingProfile[] childrenProfiles =
					createChildrenProfiles(objEdited, propertyNames);
				if (childrenProfiles != null)
					profile.setChildren(childrenProfiles);
			}
		}
		return profile;
	}

	/**
	 * Returns an Array of EditingProfiles for the properties of the specified
	 * Object to be set as <code>children</code> profiles in the
	 * EditingProfile of the Object.
	 * 
	 * @param editObj
	 *            Object in whose EditingProfile the returned Array is to be set
	 *            as <code>children</code>
	 * @param propertyNames
	 *            Array of Strings identifying the properties of the specified
	 *            Object
	 * @return EditingProfile[] to be the <code>children</code> profiles in
	 *         the Object's EditingProfile
	 */
	public static EditingProfile[] createChildrenProfiles(
		Object editObj,
		String[] propertyNames) {
		ArrayList children = new ArrayList();
		for (int i = 0; i < propertyNames.length; i++) {
			//			EditingProfile profile =
			//				createDefaultProfile2(editObj, propertyNames[i]);
		    if (TypedCollection.class.isAssignableFrom(editObj.getClass())){
		        if (propertyNames[i].equals("content")){
		            break;
		        }
		    }
			EditingProfile profile = null;
			Object property = getPropertyForName(editObj, propertyNames[i]);
			if (property != null)
				//				profile = createDefaultProfile(property, propertyNames[i]);
				profile = createDefaultProfile(editObj, propertyNames[i]);
			if (profile != null)
				children.add(profile);
		}
		EditingProfile[] childrenProfiles = new EditingProfile[children.size()];
		for (int i = 0; i < childrenProfiles.length; i++) {
			childrenProfiles[i] = (EditingProfile) children.get(i);
		}
		return childrenProfiles;
	}

	// --------------- inner class EditorConstructionException -------------------

	/**
	 * Thrown if some excpetion during editor-construction occured (e. g. class
	 * not found). Notice the method <code>showDialog()</code> which lets you
	 * easily handle useroutput for this exception.
	 * 
	 * @author Tobias Widdra
	 */
	public static class EditorConstructionException extends Exception {

		EditorConstructionException() {
		}

		EditorConstructionException(String message) {
			super(message);
		}

		/**
		 * Shows a error-dialog displaying the message.
		 */
		public void showDialog() {
			JOptionPane.showMessageDialog(
				null,
				getMessage(),
				"Editor Construction Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	// ------------------ methods for determining the editortype --------------	

	/**
	 * Returns a default editortype for the specified <code>editObj</code> or
	 * its property to be edited (if <code>propertyName</code> in the
	 * specified EditingProfile is not null) which is <br>- the default
	 * editortype given in the specified profile (for <code>editObj</code>)
	 * resp. in the child profile corresponding to the property, or <br>- the
	 * editortype derived from the class of <code>editObj</code> or its
	 * property to be edited, if the profile does not provide a default
	 * editortype. <br>
	 * If no editortype can be derived from the class, the
	 * <code>defaultOutmostEditorType</code> (for <code>editObj</code>)
	 * resp. the <code>defaultPropertyEditorType</code> (for the property) is
	 * returned. <br>
	 * If the specified EditingProfile is null, the
	 * <code>defaultOutmostEditorType</code> is returned. If the
	 * <code>editObj</code> does not have a property of name
	 * <code>propertyName</code> given in the EditingProfile, null is
	 * returned.
	 * 
	 * @param editObj
	 *            Object to be edited
	 * @param profile
	 *            EditingProfile specifying how <code>editObj</code> is edited
	 * @return String indicating the default editortype for the specified
	 *         Object, or null
	 */
	//TODO überflüssig?
	static String getDefaultEditorType(
		Object editObj,
		EditingProfile profile) {
		return getDisplayTypeNames(editObj, profile)[0];
	}

	/**
	 * Returns a String giving the name of the editor type for the specified Object.
	 * The returned String is either the default editortype specified in the 
	 * <code>profile</code>, the default editortype for the <code>editObj</code>'s class
	 * in the EditorRegistry or the <code>defaultOutmostEditorType</code> or
	 * <code>defaultPropertyEditorType</code>.
	 * 
	 * @param editObj Object to be edited
	 * @param profile EditingProfile specifying how to edit the <code>editObj</code>
	 * @return String name of the editor type to create
	 */
	//	private static String getEditortypeName(
	//		Object editObj,
	//		EditingProfile profile) {
	//		return getDisplayTypeNames(editObj, profile)[0];
	//	}

	/**
	 * Helper method. Returns the property of the specified Object <code>editObj</code>
	 * identified by the specified String <code>propertyName</code>.
	 * 
	 * @param editObj Object whose property is requested
	 * @param propertyName String indiciating which property is requested
	 * @return Object the property of the Object identified by the String
	 */
	private static Object getPropertyForName(
		Object editObj,
		String propertyName) {
		Object property = null;
		ReflectionAccess ref =
			ReflectionAccess.accessForClass(editObj.getClass());
		if (ref.hasPropertyName(propertyName))
			property = ref.getProperty(editObj, propertyName);
		return property;
	}

	/**
	 * Returns a String array containing the names of the editor types possible with the
	 * specified Object or its property indicated by the <code>propertyName</code> in
	 * the specified EditingProfile.
	 * <br>
	 * The names of the editor types are got 
	 * <br>- from the EditingProfile (if given there), or
	 * <br>- from the EditorRegistry (if there is an appropriate entry in the
	 * registry's map).
	 * <br>
	 * If neither profile nor registry do specify editortypes, the method tries
	 * to derive an editor type name from the class name of the object resp. its 
	 * property. This mechanism works for primitives, Rational, Maps and Collections
	 * only (and only leads to editor names ending on -"Editor", for which the 
	 * corresponding editor classes are defined in the framework.editor-package).
	 * <br>
	 * If still no editortype name could be created, the default editortype for an
	 * editObj or a property is returned (as defined by the final fields of the
	 * EditorFactory).
	 * 
	 * TODO Kommentar aktualisieren (alle möglichen Editor-Typen)
	 * 
	 * 
	 * 
	 * @param editObj Object to be edited (the editortype might be needed for a property only)
	 * @param profile EditingProfile specifying which property to edit (if any), and possibly specifiying editortypes
	 * @return String[] containing the names of editor types for the specified Object
	 */
	public static String[] getDisplayTypeNames(
		Object editObj,
		EditingProfile profile) {
		String[] typeNames = null;
		Collection<String> types = new ArrayList<String>();
		//get from profile
		if (profile == null)
			profile = getOrCreateProfile(editObj);
		if (profile != null && profile.getEditortypes() != null) {
			for (int i = 0; i < profile.getEditortypes().length; i++) {
				types.add(profile.getEditortypes()[i]);
			}
		}
		//		typeNames = profile.getEditortypes();
		//		if (typeNames != null && typeNames.length > 0)
		//			return typeNames;
		//		}
		//check if editortype for editObj or for property
		Object objEdited = null;
		if (profile.getPropertyName() == null)
			objEdited = editObj;
		else
			objEdited = getPropertyForName(editObj, profile.getPropertyName());
		if (objEdited != null) {
		    
		    //Ergänzung 08/12/04
		    if (objEdited instanceof Long && editObj instanceof MObject){
		        if (profile.getPropertyName().equalsIgnoreCase("UID")){
		            return new String[] {"MusitechLink"};
		        } 
		    }
		    //TODO flexiblere Lösung
		    //Ende Ergänzung 08/12/04
		    
			//get from EditorRegistry
			//			typeNames =
			String[] names =
				EditorRegistry.getEditortypeNamesForClass(
					objEdited.getClass());
			if (names != null && names.length > 0)
				for (int i = 0; i < names.length; i++) {
					if (!types.contains(names[i]))
						types.add(names[i]);
				}
			//			if (typeNames != null && typeNames.length > 0)
			//				return typeNames;
			//else derive from class name
			String typeName = getEditortypeNameByClass(objEdited.getClass());
			if (typeName != null) {
				if (!types.contains(typeName))
					types.add(typeName);
			}

			//			if (typeName != null)
			//				return new String[] { typeName };
		}
		//else return default type
		if (objEdited == editObj) {
			//			return new String[] { defaultOutmostEditorType };
			if (!types.contains(defaultOutmostEditorType))
				types.add(defaultOutmostEditorType);
		} else
			//			return new String[] { defaultPropertyEditorType };
			if (!types.contains(defaultPropertyEditorType))
				types.add(defaultPropertyEditorType);
		//		return (String[])types.toArray();
		typeNames = new String[types.size()];
		int i = 0;
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			typeNames[i++] = element;
		}
		return typeNames;
	}

	/**
	 * Returns an Array with the names of editor types possible for the specified
	 * Object. Only types are returned which correspond to Editors (not just Displays).
	 * 
	 * @param editObj Object to be edited 
	 * @param profile EditingProfile indicating how to edit the Object
	 * @return String[] containing the names of editor types possible for the specified Object
	 */
	public static String[] getEditortypeNames(
		Object editObj,
		EditingProfile profile) {
		List editorNames = new ArrayList();
		List displayNames =
			Arrays.asList(getDisplayTypeNames(editObj, profile));
		for (Iterator iter = displayNames.iterator(); iter.hasNext();) {
			String displayName = (String) iter.next();
			if (isEditor(displayName))
				editorNames.add(displayName);
		}
		return (String[]) editorNames.toArray(new String[]{});
	}

	/**
	 * Returns a String giving the name of the editortype for the specified class.
	 * The editortype name is derived from the name of the Class by attaching "Editor"
	 * to the "simple" class name (e.g. "String"+"Editor").
	 * The method returns an editortype name for primitive classes, Rational.class, 
	 * Collection or Map classes. Else null is returned. 
	 * 
	 * @param cla Class for which the editortype name is requested (deriving the editortype form the class name)
	 * @return String giving the name of the editortype, or null
	 */
	private static String getEditortypeNameByClass(Class cla) {
		String className = null;
		if (cla.isPrimitive() || ObjectCopy.isElementaryClass(cla))
		    if(!(cla.equals(java.net.URL.class) || cla.equals(java.io.File.class))) //TODO url & file are primitive types in Objectcopy but could not be regarded as primitives here
		        className = cla.getName();
		if (Rational.class.isAssignableFrom(cla))
			className = cla.getName();
		//		if (complexClasses.contains(cla))
		//			className = cla.getName();
		if (className == null)
			className = getClassNameOfComplexClass(cla);
		//get "simple" editortype name
		if (className != null) {
			int index = className.lastIndexOf(".");
			className = className.substring(index + 1);
			if (className.equals("Character"))
				className = "Char";
			//Modif
			if (className.equals("Integer"))
				className = "Int";
			//end
			return className;
		}
		return null;
	}

	/**
	 * Checks if the specified Class is an element or a subclass of an element
	 * in <code>complexClasses</code>. If it is, the class name of the corresponding
	 * element of <code>complexClasses</code> is returned. Else null is returned.
	 * <br><br>
	 * Presently, Collections and Maps and their subclasses lead to a non-null String.
	 * 
	 * @param cla Class for which a class name is requested (might be the name of a superclass)
	 * @return String name of the complex class (might be a superclass of the specified class)
	 */
	private static String getClassNameOfComplexClass(Class cla) {
		for (Iterator iter = complexClasses.iterator(); iter.hasNext();) {
			Class complClass = (Class) iter.next();
			if (complClass.isAssignableFrom(cla))
				return complClass.getName();
		}
		return null;
	}

	/**
	 * Returns a String array containing the names of the editortypes for the
	 * specified Class.
	 * The editortype names are asked from the EditorRegistry.
	 * If there is no appropriate entry in the registry's map, a default editortype
	 * name is derived from the class name (this works for primitives, Rationals,
	 * Collections and Maps only).
	 * If no editortype could be found for the specified class, null is returned.
	 * 
	 * @param cla Class for which the editortype names are requested
	 * @return String[] containing the names of the editortypes appropriate for the specified Class
	 */
	public static String[] getDisplayTypeNames(Class cla) {
	    if (!ObjectCopy.isElementaryClass(cla)){
	    //get display types for instance of cla
	    Object obj = null;
	    try {
            obj = cla.newInstance();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException iae){
            iae.printStackTrace();
        }
        if (obj!=null)
            return getDisplayTypeNames(obj, null);
	    }
        //else
        Collection<String> displayTypeNames = new ArrayList<String>();
		String[] editortypeNames = null;
		//get editortype names from EditorRegistry
		editortypeNames =
			EditorRegistry.getEditortypeNamesForClass(cla);
		if (editortypeNames!=null && editortypeNames.length>0)
		    displayTypeNames.addAll(Arrays.asList(editortypeNames));
		//else derive from class name TODO anhängen
//		if (editortypeNames == null || editortypeNames.length == 0) {
//			String typeName = getEditortypeNameByClass(cla);
//			if (typeName != null)
//				editortypeNames = new String[] { typeName };
//		}
//		return editortypeNames;    
		String typeName = getEditortypeNameByClass(cla);
		if (typeName != null && !displayTypeNames.contains(typeName))
		    displayTypeNames.add(typeName);
		return (String[]) displayTypeNames.toArray(new String[]{});
	}

	/**
	 * Returns the first type name for the specified <code>editObj</code> which
	 * leads to an Editor (and not just a Display).
	 * Uses method <code>getEditortypeNames(Object, EditingProfile)</code> and
	 * takes the first element of the Array returned by that method which 
	 * corresponds to an Editor. 
	 * <br>
	 * As in method <code>getEditortypeNames(Object, EditingProfile)</code> either 
	 * the <code>defaultOutMostEditorType</code> or the <code>defaultPropertyEditorType</code> 
	 * are added to the editor type names for the specified Object, there will be a
	 * non-null return value here.
	 * 
	 * @param editObj Object to be edited
	 * @param profile EditingProfile according to which the editObj will be edited
	 * @return String type name of an Editor
	 */
	private static String getFirstEditortypeName(
		Object editObj,
		EditingProfile profile) {
		String[] typeNames = getDisplayTypeNames(editObj, profile);
		for (int i = 0; i < typeNames.length; i++) {
			if (isEditor(typeNames[i]))
				return typeNames[i];
		}
		return null; //should not be reached
	}

	/**
	 * Returns true if the display type given by the specified String
	 * leads to an Editor (and not just a Display), false otherwise.
	 * 
	 * @param displayTypeName String indicating the type of display 
	 * @return boolean true if the specified String corresponds to an Editor, false otherwise
	 */
	public static boolean isEditor(String displayTypeName) {
		String displayClassName =
			EditorRegistry.getEditorClassName(displayTypeName);
		if (displayClassName == null)
			displayClassName = getEditorClassName(displayTypeName);

		Class displayClass = null;
		try {
			displayClass = Class.forName(displayClassName);
			return (Editor.class.isAssignableFrom(displayClass));
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return false;
	}
	
	/**
	 * Returns true if the display type given by the specified String
	 * leads to a Display, false otherwise.
	 * 
	 * @param displayTypeName String indicating the type of display 
	 * @return boolean true if the specified String corresponds to a Display, false otherwise
	 */
	public static boolean isDisplay(String displayTypeName) {
		String displayClassName =
			EditorRegistry.getEditorClassName(displayTypeName);
		if (displayClassName == null)
			displayClassName = getEditorClassName(displayTypeName);

		Class displayClass = null;
		try {
			displayClass = Class.forName(displayClassName);
			return (Display.class.isAssignableFrom(displayClass));
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return false;
	}

	//	Renderers for Trees and Lists
	static DefaultTreeCellRenderer tcr = new DefaultTreeCellRenderer();

	static DefaultListCellRenderer lcr = new DefaultListCellRenderer();

	// the cell renderer  
	static EditorCellRenderer renderer = new EditorCellRenderer();

	/**
	 * Get the treeCellRenderer
	 * @return
	 */
	public static TreeCellRenderer getTreeCellRenderer() {
		return renderer;
	}

	/**
	 * Get the ListCellRenderer
	 * @return
	 */
	public static ListCellRenderer getListCellRenderer() {
		return renderer;
	}

}