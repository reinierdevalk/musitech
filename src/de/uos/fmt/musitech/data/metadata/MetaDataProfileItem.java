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
 * Created on 2003-06-10
 */
package de.uos.fmt.musitech.data.metadata;

/**
 * The class MetaDataProfileItem manages the key and type of every Item.
 * @author Christophe
 * @hibernate.class table = "MetaDataProfileItem"
 * 
 */
public class MetaDataProfileItem {

	String key;
	String type;
	Long uId;

	/**
	 * Default constructor
	 */
	public MetaDataProfileItem() {
	}

	/**
	 * Constructor with arguments
	 * @param key the key of this item
	 * @param type the type of this item
	 */
	public MetaDataProfileItem(String key, String type) {
		this.key = key;
		this.type = type;
	}

	/**
	 * Method to return the key of the MetaDataProfileItem
	 * @return String
	 * 
	 * @hibernate.property
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Method to set the key of the MetaDataProfileItem
	 * @param key the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Method to return the type of the MetaDataProfileItem
	 * @return String
	 * 
	 * @hibernate.property
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Method to set the type of the MetaDataProfileItem
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
    /**
     * @return Returns the uId.
     * 
	 * @hibernate.id 
	 * 		generator-class="native" 
     * 
     */
    public Long getUId() {
        return uId;
    }
    /**
     * @param id The uId to set.
     */
    public void setUId(Long id) {
        uId = id;
    }
}
