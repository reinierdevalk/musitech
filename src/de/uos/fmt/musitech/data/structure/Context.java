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
package de.uos.fmt.musitech.data.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.structure.container.Container;

/**
 * A Context for musical objects. This Context contains a hierarchy of containers with at
 * least one piece.
 * 
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 * @author Tillman Weyde
 * 
 * @hibernate.class table = "Context"
 */
public class Context implements Serializable{

	private static final long serialVersionUID = -1720018752834297407L;
    Long uid;
    private List<Object> list = new ArrayList<Object>();
    private static Context defaultContext;
    Piece piece;

    /**
     * Create an empty context.
     */
    public Context() {
    }

    /**
     * Create a context for a given piece.
     * 
     * @param work The piece for this context.
     */
    public Context(Piece work) {
        list.add(work);
        piece = work;
        piece.setContext(this);
    }

    
    /**
     * 
     * @param cont
     */
    public Context(Container cont[]) {
        int works = 0;
        for (int i = 0; i < cont.length; i++) {
            Container container = cont[i];
            if(container instanceof Piece)
            	works++;
            list.add(container);
        }
        if (works != 1)
            throw new IllegalArgumentException("Context must contain exactly one Piece");
    }

    public Piece getPiece() {
        if(piece != null){
            return piece;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            Object obj = list.get(i);
            if (obj instanceof Piece) {
                return (Piece) obj;

            }

        }
        return Piece.getDefaultPiece();
    }

    public MObject[] getAll() {
        return list.toArray(new MObject[]{});
    }

    
    /**
     * @return Returns the list.
     * 
     * @hibernate.list name="objInContext"
     * cascade="save-update"
     * table = "obj_in_context"
     * 
     * 
     * @hibernate.collection-key column="context_id"
     * @hibernate.collection-index column="position"
     * 
     * @hibernate.collection-many-to-many class = "de.uos.fmt.musitech.data.MObject"
     * column="object_id"
     */
    public List getList() {
        return list;
    }
    /**
     * @param list The list to set.
     */
    public void setList(List list) {
        this.list = list;
    }
    
    
    /**
     * @return Returns the uid.
     * @hibernate.id generator-class="native"
     */
    public Long getUid() {
        return uid;
    }
    /**
     * @param uid The uid to set.
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }
    /**
     * @return Returns the defaultContext.
     */
    public static Context getDefaultContext() {
        if(defaultContext == null){
            defaultContext = Piece.getDefaultPiece().getContext();
        }
        return defaultContext;
    }
}