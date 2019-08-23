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

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * This class includes a couple of test methods for the MetadataCollection class.
 * It is written using the JUnit framework.
 * @author kerstin, tillman
 * @version 1.0
 * @see de.uos.fmt.musitech.data.metadata.MetaDataCollection
 */
public class MetaDataCollectionTest extends TestCase {
	/**
	 * This method tests the sorting of MetaDataItems in a MetaDataCollection.
	 * @see de.uos.fmt.musitech.data.structure.MetaDataCollection(de.uos.fmt.musitech.data.structure)
	 */
	public void testSortingMetaDataCollection() {
		MetaDataItem item1 = new MetaDataItem("Name1");
		MetaDataItem item2 = new MetaDataItem("Name2");
		MetaDataItem item3 = new MetaDataItem("Name3");
		MetaDataValue wert1 = new MetaDataValue("int1", "value1");
		MetaDataValue wert2 = new MetaDataValue("int2", "value2");
		MetaDataValue wert3 = new MetaDataValue("int3", "value3");
		MetaDataCollection collection = new MetaDataCollection();
		/* former version of MetaDataCollection (extends SortedCollection)
		collection.add(item2);
		collection.add(item1);
		collection.add(item3);
		*/
		collection.addMetaDataItem(item2);
		collection.addMetaDataItem(item1);
		collection.addMetaDataItem(item3);
		item1.setMetaValue(wert1);
		item2.setMetaValue(wert2);
		item3.setMetaValue(wert3);
		for (Iterator iter = collection.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key);
		}
		//System.out.println(collection.getItemByKey("Name2").getValue().getMimeType());
		System.out.println("Test: removing  item1");
		MetaDataItem removedItem = collection.removeMetaDataItem(item1);
		for (Iterator iter = collection.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			System.out.println(key);
		}
		System.out.println("Key of removed item: " + removedItem.getKey());
	}


	/**
	 * Tests method <code>copyToMetaDataCollection(MetaDataCollection)</code> of class
	 * MetaDataCollection.
	 */
	public void testCopyingMetaDataCollection() {
		MetaDataCollection src = createMetaDataCollection1();
		MetaDataCollection dest = createMetaDataCollection2();
		//test precondition (src and dest are different)
		assertFalse(ObjectCopy.equalValues(src, dest));
		//copy src -> dest
		src.copyToMetaDataCollection(dest);
		//test result of copying
		assertTrue(ObjectCopy.equalValues(src, dest));
	}

	/**
	 * Private "helper" method to free test methods from the code involved in creating 
	 * a MetaDataCollection for testing.
	 * <br>
	 * <br>Note: 
	 * <code>createMetaDataCollection1()</code> and <code>createMetaDataCollection2()</code>
	 * return different MetaDataCollections, that is, two different objects containing
	 * elements differing in their values.
	 * 
	 * @return MetaDataCollection for testing
	 */
	private MetaDataCollection createMetaDataCollection1() {
		MetaDataCollection mdColl = new MetaDataCollection();
		MetaDataProfile profile = new MetaDataProfile();
		profile.setName("ProfileOfFirstColl");
		for (int i = 0; i < 4; i++) {
			String key = "Name" + i;
			String type = "String";
			//create MetaDataItems
			MetaDataItem item = new MetaDataItem(key);
			MetaDataValue metaDataValue = new MetaDataValue(type, "StringValue" + i);
			item.setMetaValue(metaDataValue);
			mdColl.addMetaDataItem(item);
			//create MetaDataProfile
			MetaDataProfileItem profileItem =
				new MetaDataProfileItem(key, type);
			profile.add(profileItem);
			mdColl.addProfile(profile);
		}
		return mdColl;
	}

	/**
	 * Private "helper" method to free test methods from the code involved in creating 
	 * a MetaDataCollection for testing.
	 * <br>
	 * <br>Note: 
	 * <code>createMetaDataCollection1()</code> and <code>createMetaDataCollection2()</code>
	 * return different MetaDataCollections, that is, two different objects containing
	 * elements differing in their values.
	 * 
	 * @return MetaDataCollection for testing
	 */
	private MetaDataCollection createMetaDataCollection2() {
		MetaDataCollection mdColl = new MetaDataCollection();
		MetaDataProfile profile = new MetaDataProfile();
		profile.setName("ProfileOfSecondColl");
		for (int i = 0; i < 3; i++) {
			String key = "Key" + i;
			String type = "int";
			//create MetaDataItems
			MetaDataItem item = new MetaDataItem(key);
			MetaDataValue metaDataValue = new MetaDataValue(type, "IntValue" + i);
			item.setMetaValue(metaDataValue);
			mdColl.addMetaDataItem(item);
			//create MetaDataProfile
			MetaDataProfileItem profileItem =
				new MetaDataProfileItem(key, type);
			profile.add(profileItem);
			mdColl.addProfile(profile);
		}
		return mdColl;
	}
	
	/**
	 * Tests method <code>addMetaDataItem(MetaDataItem)</code>.
	 */
	public void testAddMetaDataItem(){
	    MetaDataCollection mdColl = new MetaDataCollection();
	    //test pre-condition
	    assertEquals(mdColl.size(), 0);
	    //add MDItem
	    MetaDataItem mdItem = new MetaDataItem("Key");
	    MetaDataValue metaDataValue = new MetaDataValue("string", "Value");
	    mdItem.setMetaValue(metaDataValue);
	    mdColl.addMetaDataItem(mdItem);
	    //test post-condition
	    assertEquals(mdColl.size(), 1);
	    assertTrue(mdColl.containsMetaDataItem(mdItem));
	    assertFalse(mdColl.containsMetaDataItem(new MetaDataItem()));
	}
	
	/**
	 * Tests method <code>removeMetaDataItem(MetaDataItem)</code>. 
	 */
	public void testRemoveMetaDataItem(){
	    MetaDataCollection mdColl = new MetaDataCollection();
	    MetaDataItem mdItem = new MetaDataItem("Key 1");
	    mdColl.addMetaDataItem(mdItem);
	    mdColl.addMetaDataItem(new MetaDataItem("Key 2"));
	    //test pre-conditions
	    assertEquals(mdColl.size(), 2);
	    assertTrue(mdColl.containsMetaDataItem(mdItem));
	    //remove MDItem
	    mdColl.removeMetaDataItem(mdItem);
	    //test post-condition
	    assertEquals(mdColl.size(), 1);
	    assertFalse(mdColl.containsMetaDataItem(mdItem));
	    //remove MDItem not contained
	    MetaDataItem item = mdColl.removeMetaDataItem(new MetaDataItem());
	    assertNull(item);
	    assertEquals(mdColl.size(), 1);
	}
	
	/**
	 * Tests methods <code>addProfile(MetaDataProfile)</code> and
	 * <code>removeProfile(MetaDataProfile)</code>.
	 */
	public void testAddAndRemoveProfile(){
	    MetaDataCollection mdColl = new MetaDataCollection();
	    List profiles = mdColl.getListOfProfiles();
	    //check pre-condition
	    assertEquals(profiles.size(), 0);
	    //add profile
	    MetaDataProfile mdProfile = new MetaDataProfile();
	    mdColl.addProfile(mdProfile);
	    //test post-condition
	    assertEquals(profiles.size(), 1);
	    assertTrue(mdColl.getListOfProfiles().contains(mdProfile));
	    //remove mdProfile
	    mdColl.removeProfile(mdProfile);
	    //test post-condition
	    assertEquals(profiles.size(), 0);
	    assertFalse(mdColl.getListOfProfiles().contains(mdProfile));
	}
	
	/**
	 * Tests methods <code>addMetaDataCollection(MetaDataCollection)</code> and
	 * <code>removeMetaDataCollection(MetaDataCollection)</code>.
	 */
	public void testAddAndRemoveMetaDataCollection(){
	    MetaDataCollection coll1 = createMetaDataCollection1();
	    MetaDataCollection coll2 = createMetaDataCollection2();
	    //remember pre-condition
	    int size1 = coll1.size();
	    int size2 = coll2.size();
	    //add coll2 to coll1
	    coll1.addMetaDataCollection(coll2);
	    //test post-condition
	    assertTrue(coll1.size() > size1);
	    assertEquals(coll1.size(), size1+size2);
	    //remove coll2 from new coll1
	    coll1.removeMetaDataCollection(coll2);
	    //test post-condition
	    assertEquals(coll1.size(), size1);
	    //test removing a MetaDataCollection not contained
	    MetaDataCollection removedItems = coll2.removeMetaDataCollection(coll1);
	    assertNull(removedItems);
	}
	
	/**
	 * Tests method <code>isEmpty()</code>.
	 */
	public void testIsEmpty(){
	    MetaDataCollection mdColl = new MetaDataCollection();
	    assertTrue(mdColl.isEmpty());
	    mdColl.addMetaDataItem(new MetaDataItem());
	    assertFalse(mdColl.isEmpty());
	}

}
