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

import de.uos.fmt.musitech.framework.editor.Display;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.editor.Editor;

/**
 * A TranslatingEditor is a special kind of editor which creates its <code>editObj</code>
 * from a different data object used for input.
 * <br>Examples:
 * One example is an Editor editing a NoteList whose data is entered in epec. Another 
 * example is an Editor which generates an Icon from an URL.
 * <br><br>
 * It is recommended to name the Editor according to the convention:
 * type of editObj + type of inputObj + Editor.
 * So, e.g. an IconURLEditor creates an Icon from an URL. The Icon is the object which
 * is edited (an which will be returned when asking for the <code>editObj</code>), while
 * the URL is used for entering the "data" of the <code>editObj</code> (and will be
 * returned when asking for the <code>inputObj</code>).
 * 
 * @author Kerstin Neubarth
 * 
 */
public interface TranslatingEditor extends Editor {
    
    /**
     * Returns the Object used for input.
     * In an Editor which derives a NoteList from epec, the <code>inputObj</code> is the
     * String with the epec, while the <code>editObj</code> is the NoteList.
     * 
     * @return
     */
    public Object getInputObj();
    
    /**
     * Replaces the <code>init</code>-method of the Editor in order to set the 
     * <code>inputObj</code> instead of the <code>editObj</code>.
     * The <code>editObj</code> is not set, but created from the <code>inputObj</code>.
     *  
     * @see de.uos.fmt.musitech.framework.editor.Display#init(java.lang.Object, de.uos.fmt.musitech.framework.editor.EditingProfile, de.uos.fmt.musitech.framework.editor.Display)
     */
    @Override
	public void init(Object inputObj, EditingProfile profile, Display root);

}
