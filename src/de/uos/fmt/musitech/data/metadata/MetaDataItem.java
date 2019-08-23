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
package de.uos.fmt.musitech.data.metadata;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.framework.editor.Editable;
import de.uos.fmt.musitech.framework.editor.EditingProfile;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;
import de.uos.fmt.musitech.utility.HashCodeGenerator;
import de.uos.fmt.musitech.utility.xml.XMLHelper;

/**
 * A MetaDataItem contains a key defning the meaning of the data and a MetaDataValue
 * object.
 * 
 * @author Kerstin Neubarth, Tillman Weyde
 * 
 * 
 * @hibernate.class table = "MetaDataItem"
 */
public class MetaDataItem implements Containable, Editable, IMPEGSerializable {

    private static final long serialVersionUID = 7625728025920256360L;
    
    //	Unique ID for this object.
    private Long uid;
    private int hashCode = HashCodeGenerator.getHashCode();

    /**
     * Returns a unique ID.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return hashCode;
    }

    private java.lang.String key;
    private MetaDataValue metaDataValue;

    /**
     * Constructor.
     * 
     * Creation date: (10.1.2002 17:42:03)
     * 
     * @param key
     *            java.lang.String
     */
    public MetaDataItem(String key) {
        this.key = key;
    }

    /**
     * Convenience constructor for text values.
     * 
     * Creation date: (10.1.2002 17:42:03)
     * 
     * @param key
     *            java.lang.String
     * @param value
     *            java.lang.String
     */
    public MetaDataItem(String key, String value) {
        this.key = key;
        //		this.metaValue = new MetaDataValue("text",value);
        this.metaDataValue = new MetaDataValue("string", value);
    }

    /**
     * Default constructor.
     * 
     * Creation date: (10.1.2002 17:42:03)
     */
    public MetaDataItem() {
    }

    /**
     * Getter for <code>key</code>.
     * 
     * Creation date: (10.1.2002 17:01:15)
     * 
     * @return String <code>key</code>
     * 
     * @hibernate.property
     */
    public java.lang.String getKey() {
        return key;
    }

    /**
     * Sets the key.
     * 
     * @param argKey The key to set.
     */
    public void setKey(java.lang.String argKey) {
        this.key = argKey;
    }

    /**
     * Getter for <code>metaDataValue</code>.
     * 
     * Creation date: (10.1.2002 17:47:38)
     * 
     * @return de.uos.fmt.musitech.data.structure.MetaValue
     * 
     * @hibernate.many-to-one class =
     *                        "de.uos.fmt.musitech.data.metadata.MetaDataValue"
     *                        foreign-key = "id"
     *  
     */
    public MetaDataValue getMetaDataValue() {
        return metaDataValue;
    }

    /**
     * Setter for <code>metaDataValue</code>.
     * 
     * Creation date: (10.1.2002 17:47:38)
     * 
     * @param newValue
     *            de.uos.fmt.musitech.data.structure.MetaValue
     */
    public void setMetaValue(MetaDataValue newValue) {
        metaDataValue = newValue;
    }

    /**
     * Returns an EditingProfile with <code>label</code> corresponding to the
     * <code>key</code> and <code>propertyName</code> "metaDataValue". The
     * <code>editortype</code> is set to the <code>mimeType</code> of
     * <code>metaDataValue</code> if not null.
     * 
     * @see de.uos.fmt.musitech.framework.editor.Editable#getEditingProfile()
     */
    public EditingProfile getEditingProfile() {
        // TODO make this functional
        //Ergaenzung 09/12/03
        //		if (metaDataValue==null){
        //			EditingProfile profile = new EditingProfile("metaDataValue");
        //			profile.setLabel(key);
        //			return profile;
        //		}
        //		return (new EditingProfile(key, metaDataValue.getMimeType(),
        // "metaDataValue"));
        //101203
        return new EditingProfile(key, "MetaDataItem", null);
    }

    /**
     * Returns a String representation of this MetaDataItem.
     * 
     * @return String representing the data of this MetaDataItem
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer("MetaDataItem: ");
        String valueToString = "null";
        if (metaDataValue != null) {
            valueToString = metaDataValue.toString();
        }
        buffer.append(" key " + key).append("  value " + valueToString);
        return buffer.toString();
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
     *      java.lang.Object)
     */
    public boolean isValidValue(String propertyName, Object value) {
        // TODO Auto-generated method stub
        return true; //default
    }

    /**
     * getUid
     * 
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     * 
     * @hibernate.id generator-class="native"
     */
    public Long getUid() {
        return uid;
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
     */
    public void setUid(Long argUid) {
        this.uid = argUid;
    }

    /**
     * Returns a Collection containing as String elements the languages for
     * which the <code>metaDataValue</code> is provided.
     * 
     * @return Collection containing the provided languages as String elements
     */
    public Collection returnProvidedLanguages() {
        if (metaDataValue!=null)
            return metaDataValue.returnLanguageOptions();
        return new ArrayList();
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        Node metaDataItem = XMLHelper.addElement(parent, "metaDataItem");
        if (instance.knowsObject(metaDataItem, object))
            return true;

        // create child-elements

        Node metaDataKey = XMLHelper.addElement(metaDataItem, "metaDataKey");
        metaDataKey.setNodeValue(this.getKey());

        instance.writeXML(metaDataItem, this.getMetaDataValue(), null, null);

        return true;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element)
     */
    public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
        // reference-handling
        Object reference = instance.getReferenced(node, this);
        if (reference != null)
            return reference;

        // MetaKey
        this.setKey(XMLHelper.getText(XMLHelper.getElement(node, "metaDataKey")));
        // MetaDataValue
        Element metaValue = XMLHelper.getElement(node, "metaDataValue");
        this.setMetaValue((MetaDataValue) instance.readXML(metaValue, MetaDataValue.class));

        return this;
    }
}