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
 * Created on 2003-06-06
 *
 */
package de.uos.fmt.musitech.data.metadata;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.utility.HashCodeGenerator;

/**
 * MetaDataProfile contains the structure for metadata for a specific type of content or data.
 * @author Christophe
 * 
 * @hibernate.class table = "MetaDataProfile"
 */
public class MetaDataProfile extends ArrayList<MetaDataProfileItem> implements Named {
    private Long uid;
    private int hashCode = HashCodeGenerator.getHashCode();

	
	/**
	 * Default constructor. Objects created accept only MetaDataProfileItems.
	 */
	public MetaDataProfile() {
//		super(MetaDataProfileItem.class);
	}

	String name;
	/** 
	 * @see de.uos.fmt.musitech.data.Named#getName()
	 * @hibernate.property
	 */
	@Override
	public String getName() {
		return name;
	}

	/** 
	 * @see de.uos.fmt.musitech.data.Named#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/** 
	 * listOfKeys
	 * @return Array containing all keys
	 */
	public String[] listOfKeys() {

		String[] listOfKeysInProfile = new String[size()];
		int loop = 0;
		for (Iterator<MetaDataProfileItem> iter = iterator(); iter.hasNext();) {
			MetaDataProfileItem element = iter.next();
			listOfKeysInProfile[loop] = element.getKey();
			loop++;
		}
		return listOfKeysInProfile;
	}

	
    /**
     * @return Returns the uid.
     * @hibernate.id 
     * 		generator-class="native" 
     * 
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
     * @return Returns the hashCode.
     * 
     * @hibernate.property
     */
    public int getHashCode() {
        return hashCode;
    }
    
    
    /**
     * @return 
     * @see de.uos.fmt.musitech.utility.collection.TypedCollection#getContent()
     * 
     * @hibernate.bag name="objInMetaDataProfile"
     * cascade="save-update"
     * table = "obj_in_MetaData"
     * 
     * 
     * @hibernate.collection-key column="MetaData_id"
     * 
     * 
     * @hibernate.collection-many-to-many class = "de.uos.fmt.musitech.data.MObject"
     * column="object_id"
     * 
     */
    public List<MetaDataProfileItem> getContent() {
        return this;
    }
    
    /**
     * @param hashCode The hashCode to set.
     */
    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }
}
