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

import java.io.File;
import java.util.List;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.utility.HashCodeGenerator;
import de.uos.fmt.musitech.utility.collection.TypedCollection;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * A MusicCollection represents a collection of works, e.g. 
 * Beethoven's Sonatas, Bach's Chorales, Charlie Parker's 
 * Saxophone Solos. A MusicCollection can contain Works or 
 * WorkCollections, e.g. Subcollections like the first and 
 * second book of Bach's 'Wohltemperiertes Klavier'.
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 * @author Tillman Weyde
 * 
 * @hibernate.class table="MusicCollection"
 * hibernate.joined-subclass
 * hibernate.joined-subclass-key  column = "uid"
 * 
 * 
 */
public class MusicCollection extends TypedCollection implements MObject, Named  {
	private static final long serialVersionUID = -4741804763477322480L;
	//	Unique ID for this object.
	private Long uid;
	
	private int hashCode = (int) HashCodeGenerator.getHashCode();
	
	/** 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return hashCode;
	}
    
	String name;
	
	public static void main(String[] arguments) {
		MusicCollection wc = new MusicCollection();
		Piece work = new Piece();
		work.setSampleData();
		wc.add(work);
		ObjectCopy.writeXML( wc, new File("sampleDataMX.xml"), new File("trans2xml.xsl"));	
	}
	
	public MusicCollection(){
		super(new Class[]{Piece.class,MusicCollection.class});
	}


	/**
	 * @see de.uos.fmt.musitech.data.Named#getName()
	 * 
	 * @hibernate.property
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see de.uos.fmt.musitech.data.Named#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** 
	 * getUid
	 * @see de.uos.fmt.musitech.data.MObject#getUid()
	 * 
     * @hibernate.id 
	 * 		generator-class="native" 
	 * 
	 */
	public Long getUid() {
		return uid;
	}

	/**
	 * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
	 */
	 public void setUid(Long uid) {
	     this.uid = uid;
	}

	/** 
	 * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String, java.lang.Object)
	 */
	public boolean isValidValue(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return true; //default
	}

    /**
     * @see de.uos.fmt.musitech.utility.collection.TypedCollection#getContent()
     * 
     * @hibernate.list name="objInTypedColl"
     * cascade="all"
     * table = "obj_in_TypedColl"
     * 
     * 
     * @hibernate.collection-key column="coll_id"
     * @hibernate.collection-index column="position"
     * 
     * 
     * @hibernate.collection-many-to-many class = "de.uos.fmt.musitech.data.MObject"
     * column="object_id"
     * 
     */
    public List getContent() {
        // TODO Auto-generated method stub
        return super.getContent();
    }

}
