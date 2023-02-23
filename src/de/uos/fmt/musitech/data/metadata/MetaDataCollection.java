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
 * Created on 03.06.2003
 *  
 */
package de.uos.fmt.musitech.data.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.framework.persistence.IMPEGSerializable;
import de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer;

/**
 * This class contains MetaDataItems. The MetaDataItems are stored in the order
 * in which they have been inserted.
 * 
 * @author Kerstin Neubarth, Tillman Weyde, Christophe Hinz
 * 
 * @hibernate.class table = "MetaDataCollection"
 * 
 * @hibernate.joined-subclass
 * @hibernate.joined-subclass-key column = "uid"
 * 
 *  
 */
public class MetaDataCollection implements Containable, IMPEGSerializable {
    
    private static final long serialVersionUID = 6891420720990569246L;

    private Map<String,MetaDataItem> dataMap = new LinkedHashMap<String,MetaDataItem>();

    /** List of MetaDataProfiles used in editing the MetaDataCollection. */
    public List<MetaDataProfile> listOfProfilesInCollection = new ArrayList<MetaDataProfile>();

    /**
     * Adds a MetaDataProfile to a list of profiles in the collection.
     * 
     * @param profile
     */
    public void addProfile(MetaDataProfile profile) {
        listOfProfilesInCollection.add(profile);
    }

    /**
     * Removes a MetaDataProfile from the list of profiles in the collection.
     * 
     * @param profile
     */
    public void removeProfile(MetaDataProfile profile) {
        listOfProfilesInCollection.remove(profile);
    }

    /**
     * Returns a vector containing the list of all MetaDataProfiles of this
     * collection
     * 
     * @return List
     * 
     * @hibernate.list name="profiles" cascade="all" table =
     *                 "profiles_in_MDColl"
     * 
     * 
     * @hibernate.collection-key column="coll_id"
     * @hibernate.collection-index column="position"
     * 
     * 
     * @hibernate.collection-many-to-many class =
     *                                    "de.uos.fmt.musitech.data.metadata.MetaDataProfile"
     *                                    column="object_id"
     */
    public List<MetaDataProfile> getListOfProfiles() {
        return listOfProfilesInCollection;
    }

    /**
     * Set the list of all profiles associated with this collections.
     * 
     * @param profileList
     */
    public void setListOfProfiles(List<MetaDataProfile> profileList) {
        listOfProfilesInCollection = profileList;
    }

    /**
     * Default constructor. Constructs an empty insertion-ordered
     * MetaDataCollection.
     */
    public MetaDataCollection() {
    }

    /**
     * Adds the specified MetaDataItem item to this MetaDataCollection.
     * 
     * @param item
     *            the MetaDataItem to be added to this MetaDataCollection
     */
    public void addMetaDataItem(MetaDataItem item) {
        dataMap.put(item.getKey(), item);
    }

    /**
     * Adds all MetaDataItems of the specified MetaDataCollection to this
     * MetaDataCollection.
     * 
     * @param collection
     *            the MetaDataCollection to be added to this MetaDataCollection
     */
    public void addMetaDataCollection(MetaDataCollection collection) {
        dataMap.putAll(collection.getDataMap());
    }

    /**
     * Removes the specified MetaDataItem item from this MetaDataCollection.
     * 
     * @param item
     *            the MetaDataItem to be removed from this MetaDataCollection
     * @return removed MetaDataItem, or null if there was no mapping for
     *         item.getKey()
     */
    public MetaDataItem removeMetaDataItem(MetaDataItem item) {
        if (item == null)
            return null;
        return dataMap.remove(item.getKey());
    }
    
    /**
     * Returns the keys of all MetaDataItems contained in this MetaDataCollection.
     * 
     * @return A set containing the keys of all items.
     */
    public Set<String> getKeys() {
    	return getDataMap().keySet();
    }

    /**
     * Removes all MetaDataItems contained in the specified MetaDataCollection
     * from this MetaDataCollection.
     * 
     * @param collection
     *            MetaDataCollection of the MetaDataItems to be removed from
     *            this MetaDataCollection
     * @return returns removed MetaDataItems, or null if there was no mapping
     *         for item.getKey() for all items of the specified
     *         MetaDataCollection
     */
    public MetaDataCollection removeMetaDataCollection(MetaDataCollection collection) {
        if (collection == null)
            return null;
        MetaDataCollection removed = new MetaDataCollection();
        for (Iterator<String> iter = collection.getDataMap().keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            if (dataMap.get(key) != null)
                removed.addMetaDataItem(dataMap.remove(key));
        }
        if (removed.size() > 0)
            return removed;
        return null;
    }

    /**
     * Returns the MetaDataItem the key of which is matched the specified key.
     * 
     * @param key
     *            String that must be matched by the searched item's key
     * @return MetaDataItem the key of which matches the specified key
     */
    public MetaDataItem getItemByKey(String key) {
        return get(key);
    }

    /**
     * Get the MetaDataItem for this key.
     * 
     * @param key
     * @return
     */
    private MetaDataItem get(Object key) {
        return dataMap.get(key);
    }

    /**
     * Returns true if one of the collection's MetaDataProfiles contains the
     * specified key.
     * 
     * @param key
     *            String
     * @return <code>true</code> if the key is in one of the profiles, <code>false</code> otherwise.
     */
    public boolean isKeyInProfiles(String key) {
        for (Iterator<MetaDataProfile> iter = getListOfProfiles().iterator(); iter.hasNext();) {
            MetaDataProfile profile = iter.next();
            String keys[] = profile.listOfKeys();
            for (int i = 0; i < keys.length; i++) {
                if (key.equalsIgnoreCase(keys[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if thie MetaDataCollection contains the specified
     * MetaDataItem.
     * 
     * @param item
     *            MetaDataItem to be checked ig being contained in this
     *            MetaDataCollection
     * @return booelan true if this MetaDataCollection contains the specified
     *         MetaDataItem, false otherwise
     */
    public boolean containsMetaDataItem(MetaDataItem item) {
        if (get(item.getKey()) == null)
            if (!containsKey(item.getKey()))
                return false;
        return true;
    }

    /**
     * @param key The key to search for.
     * @return true if the key is contained in this map.
     */
    private boolean containsKey(Object key) {
        return dataMap.containsKey(key);
    }

    /**
     * Copies this MetaDataCollection into the specified MetaDataCollection
     * <code>dest</code>. In case, <code>dest</code> is not empty, all its
     * current elements are removed before adding the MetaDataItems of this
     * MetaDataCollection. <br>
     * Copying a MetaDataCollection involves putting the MetaDataItems contained
     * in this MetaDataCollection into <code>dest</code> as well as replacing
     * the MetaDataProfiles contained in the
     * <code>listOfProfilesInCollection</code> of <code>dest</code> (if
     * there are currently any) by the MetaDataProfiles contained in
     * <code>listOfProfileInCollection</code> of this MetaDataCollection.
     * 
     * @param dest
     *            MetaDataCollection into which MetaDataItems and
     *            MetaDataProfiles of this MetadataCollection are to be copied
     */
    public void copyToMetaDataCollection(MetaDataCollection dest) {
        if (!dest.isEmpty())
            dest.clear();
        dest.addMetaDataCollection(this);
        if (!dest.getListOfProfiles().isEmpty())
            dest.getListOfProfiles().clear();
        dest.getListOfProfiles().addAll(this.getListOfProfiles());
    }

    /**
     * @see Map#clear()
     *  
     */
    public void clear() {
        dataMap.clear();

    }

    /**
     * @return true if this collection is empty.
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    Long uid;

    /**
     * Get the id of this MetsDataCollection.
     * 
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     * 
     * @hibernate.id generator-class="native"
     *  
     */
    @Override
	public Long getUid() {
        return uid;
    }

    /**
     * Set the id of this MetsDataCollection.
     * 
     * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
     */
    @Override
	public void setUid(Long argUid) {
        this.uid = argUid;
    }

    /**
     * TODO Value check not yet implemented.
     * 
     * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
     *      java.lang.Object)
     */
    @Override
	public boolean isValidValue(String propertyName, Object value) {
        return true;
    }

    MObject describedObject;

    /**
     * @return Returns the describedObject.
     * 
     * @hibernate.many-to-one class = "de.uos.fmt.musitech.data.MObject"
     *                        foreign-key = "uid" cascade = "all"
     *  
     */
    public MObject getDescribedObject() {
        return describedObject;
    }

    /**
     * Sets the describedObject.
     * 
     * @param argDescribedObject
     *            The describedObject to set.
     */
    public void setDescribedObject(MObject argDescribedObject) {
        this.describedObject = argDescribedObject;
    }

    /**
     * @return Returns the dataMap.
     * 
     * @hibernate.map name="datamapInMDColl" cascade="all" table =
     *                "obj_in_MDColl"
     * 
     * 
     * @hibernate.collection-key column="MDColl_id"
     * 
     * @hibernate.collection-index column = "item_id" type = "java.lang.String"
     * 
     * @hibernate.collection-many-to-many column = "MetaDataItem" class =
     *                                    "de.uos.fmt.musitech.data.metadata.MetaDataItem"
     *  
     */
    public Map<String, MetaDataItem> getDataMap() {
        return dataMap;
    }

    /**
     * @param metaData The map containing metadata to set.
     */
    public void setDataMap(Map<String, MetaDataItem> metaData) {
        this.dataMap = metaData;
    }

    /**
     * @return the current set of keys.
     * @see Map#keySet()
     */
    public Set<String> keySet() {
        return dataMap.keySet();
    }

    /**
     * @return the size of the collection.
     * @see Map#size()
     */
    public int size() {
        return dataMap.size();
    }

    /**
     * @return the set of map entries.
     * @see Map#entrySet()
     */
    public Set<?> entrySet() {
        return dataMap.entrySet();
    }

    /**
     * @return The current set of values.
     * @see Map#values()
     */
    public Collection<MetaDataItem> values() {
        return dataMap.values();
    }

    /**
     * Returns a MetaDataCollection which contains those MetaDataItems of this
     * MetaDataCollection which are matched by an item in the specified
     * MetaDataProfile.
     * 
     * @param profile
     *            MetaDataProfile whose keys determine the MetaDataItems of this
     *            MetaDataCollection to tbe returned
     * @return MetaDataCollection with the MetaDataItems of this
     *         MetaDataCollection whose keys correspond to the keys in the
     *         specified MetaDataProfile
     */
    public MetaDataCollection returnMetaDataForProfile(MetaDataProfile profile) {
        MetaDataCollection coll = new MetaDataCollection();
        for (int i = 0; i < profile.listOfKeys().length; i++) {
            if (containsKey(profile.listOfKeys()[i])) {
                coll.addMetaDataItem(get(profile.listOfKeys()[i]));
            }
        }
        return coll;
    }

    /**
     * Returns a Collection containing as String elements the languages for
     * which meata data is provided in this MetaDataCollection.
     * 
     * @return Collection containing the languages used in this
     *         MetaDataCollection
     */
    public Collection<String> returnProvidedLanguages() {
        Collection<String> languages = new ArrayList<String>();
        Collection<MetaDataItem> items = dataMap.values();
        for (Iterator<MetaDataItem> iter = items.iterator(); iter.hasNext();) {
            MetaDataItem element = iter.next();
            Collection<?> elementLanguages = element.returnProvidedLanguages();
            for (Iterator<?> iterator = elementLanguages.iterator(); iterator.hasNext();) {
                String language = (String) iterator.next();
                if (!languages.contains(language)) {
                    languages.add(language);
                }
            }
        }
        return languages;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#toMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Node, java.lang.Object, java.lang.String)
     */
    @Override
	public boolean toMPEG(MusiteXMLSerializer instance, Node parent, Object object, String fieldname) {
        // deserialization is done by MPEG_SMR_Tools.serializeMetaData()
        return false;
    }

    /**
     * @see de.uos.fmt.musitech.framework.persistence.IMPEGSerializable#fromMPEG(de.uos.fmt.musitech.framework.persistence.MusiteXMLSerializer,
     *      org.w3c.dom.Element)
     */
    @Override
	public Object fromMPEG(MusiteXMLSerializer instance, Element node) {
        // reference-handling
        Object reference = instance.getReferenced(node, this);
        if (reference != null)
            return reference;
        
        // deserialize metaDataItems
        NodeList metaDataList = node.getElementsByTagName("metaDataItem");
        for (int i = 0; i < metaDataList.getLength(); i++) {
            this.addMetaDataItem((MetaDataItem) instance.readXML((Element) metaDataList.item(i), MetaDataItem.class));
        }
        return this;
    }

}