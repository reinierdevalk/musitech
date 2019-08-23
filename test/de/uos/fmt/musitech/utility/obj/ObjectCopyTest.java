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
 * Created on 04.11.2003
 *
 */
package de.uos.fmt.musitech.utility.obj;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.TestCase;
import de.uos.fmt.musitech.data.performance.MidiNoteMulti;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.utility.math.*;

/**
 * Class containing some methods for testing class <code>ObjectCopy</code>.
 * 
 * @author Kerstin Neubarth
 *
 */
public class ObjectCopyTest extends TestCase {

	/**
	 * DEPRECATED!
	 * 
	 * Tests copying the elements of a collection into another collection.
	 * Therefore, a collection is copied to a second object using method
	 * <code>copyObject(Object)</code>. The second collection is changed
	 * by adding an element. Afterwards the elements of this second 
	 * collection are to be copied back into the original collection 
	 * without creating a new object by using method 
	 * <code>copyPublicProperties(Object, Object)</code>.
	 * 
	 * In order to check if copying back has been successful the collections'
	 * sizes are compared and both collections' elements are printed to the
	 * command line.
	 * 
	 * N.B.:
	 * Testing <code>copyPublicProperties(Object, Object)</code> for copying back
	 * is no longer valid, as a new method <code>copyCollection(Collection, Collection)</code>
	 * has been added to class <code>ObjectCopy</code>. 
	 * 
	 */
	public void testCopyObjectWithCollection() {
		System.out.println("\nObjectCopyTest.testCopyObjectWithCollection(deprecated).");
		//create collection
		Collection coll = new Vector();
		for (int i = 0; i < 3; i++) {
			coll.add("Element" + i);
		}
		System.out.println(
			"original coll: number of elements = " + coll.size());
		System.out.print("original coll: elements are\t");
		for (Iterator iterator = coll.iterator(); iterator.hasNext();) {
			String element = (String) iterator.next();
			System.out.print(element + "\t");
		}
		//provide collection's copy
		Collection copy = (Collection) ObjectCopy.copyObject(coll);
		//change copy
		copy.add("New Element");
		//copy back to coll (given object)
		ObjectCopy.copyPublicProperties(copy, coll);
		//check copying
		if (copy.size() != coll.size())
			System.out.println("Error in copying elements of collection");
		System.out.println("after having changed the copy:");
		System.out.println("copy: number of elements = " + copy.size());
		System.out.print("copy: elements are\t");
		for (Iterator iterator = copy.iterator(); iterator.hasNext();) {
			String element = (String) iterator.next();
			System.out.print(element + "\t");
		}
		System.out.println();
		System.out.println("coll: number of elements = " + coll.size());
		System.out.print("coll: elements are\t");
		for (Iterator iterator = coll.iterator(); iterator.hasNext();) {
			String element = (String) iterator.next();
			System.out.print(element + "\t");
		}
	}

	/**
	 * Tests copying an object with properties which themselves are objects
	 * (not primitive data types). Checks if these properties as well are copies. 
	 * I.e., tests that method <code>copyObject(Object)</code> returns a deep copy.
	 */
	public void testCopyComplexObject() {
		MidiNoteMulti perfNote = new MidiNoteMulti();
		perfNote.setNext(perfNote);
		ScoreNote scoreNote = new ScoreNote();
		Note note = new Note(scoreNote, perfNote);
		Note copy = (Note) ObjectCopy.copyObject(note);
		System.out.println("\n\nObjectCopyTest.testCopyComplexObject");
		System.out.println("Object:");
		System.out.println("note==copy: " + (note == copy));
		System.out.println("note equals copy: " + (note.equals(copy)));
		ReflectionAccess refCopy =
			ReflectionAccess.accessForClass(copy.getClass());
		if (refCopy.hasPropertyName("ScoreNote")) {
			ScoreNote scoreCopy =
				(ScoreNote) refCopy.getProperty(copy, "ScoreNote");
			System.out.println("Property:");
			System.out.println(
				"scoreNote==scoreCopy: " + (scoreNote == scoreCopy));
			System.out.println(
				"scoreNote equals scoreCopy: " + (scoreNote.equals(scoreCopy)));
		}
		Note rCopy = (Note) ObjectCopy.copyObjectRef(note);		
		assertTrue(ObjectCopy.equalValues(note, rCopy));
		assertTrue(ObjectCopy.comparePublicProperties(note,rCopy));
	}

//	/**
//	 * "Replaces" test method <code>testCopyObjectWithCollection()</code>.
//	 * 
//	 * Tests copying the elements of a collection into another collection.
//	 * Therefore, a collection is copied to a second object using method
//	 * <code>copyObject(Object)</code>. The second collection is changed
//	 * by adding an element. Afterwards the elements of this second 
//	 * collection are to be copied back into the original collection 
//	 * without creating a new object by using method 
//	 * <code>copyPublicProperties(Object, Object)</code>.
//	 * 
//	 * In order to check if copying back has been successful the collections'
//	 * sizes are compared and both collections' elements are printed to the
//	 * command line.
//	 */
//	public void testCopyCollection() {
//		//create collection
//		Collection coll = new Vector();
//		for (int i = 0; i < 3; i++) {
//			coll.add("Element" + i);
//		}
//		//provide collection's copy
//		Collection copy = (Collection) ObjectCopy.copyObject(coll);
//		//change copy
//		copy.add("New Element");
//		//copy back to coll (given object)
//		coll.clear();
//		ObjectCopy.copyCollection(copy, coll);
//		//check copying
//		System.out.println(
//			"\nObjectCopyTest.copyCollection.\nComparing coll with copy after copy has been changed and copied back to coll:");
//		if (copy.size() != coll.size()){
//			System.out.println("Error in copying elements of collection");
//			fail();
//		}	
//		System.out.println("copy: number of elements = " + copy.size());
//		System.out.print("copy: elements are\t");
//		for (Iterator iterator = copy.iterator(); iterator.hasNext();) {
//			String element = (String) iterator.next();
//			System.out.print(element + "\t");
//		}
//		System.out.println();
//		System.out.println("coll: number of elements = " + coll.size());
//		System.out.print("coll: elements are\t");
//		for (Iterator iterator = coll.iterator(); iterator.hasNext();) {
//			String element = (String) iterator.next();
//			System.out.print(element + "\t");
//		}
//	}
	
	/**
	 * Tests copying a Rational <code>source</code> onto an already existing
	 * Rational <code>dest</code> using method <code>copyPublicProperties(src,dest)</code>.
	 */
	public void testCopyRational(){
		Rational source = new Rational(1,4);
		Rational dest = new Rational(0,1);
		System.out.println("\nObjectCopyTest.testCopyRational.");
		System.out.println("source: " + source.toString());
		System.out.println("dest before copying: " + dest.toString());
		ObjectCopy.copyPublicProperties(source, dest);
		System.out.println("dest after copying: "+ dest.toString());
		if (!dest.equals(source)){
			System.out.println("Error in copying source to dest for Rationals!");
			fail();
		}
	}
	
	public void testEqualValues(){
		Object obj1 = new Rational(3,4); 
		Object obj2 = new Rational(3,4);
		Object obj3 = new Rational(6,8); 
		Object obj4 = new Rational(6,8);
		Assert.assertTrue(ObjectCopy.equalValues(obj1,obj2)); 
		Assert.assertTrue(ObjectCopy.comparePublicProperties(obj1,obj2)); 		
		obj1 = new Rational(3,4); 
		obj2 = new Rational(2,3);
		Assert.assertFalse(ObjectCopy.comparePublicProperties(obj1,obj2)); 		
		Assert.assertFalse(ObjectCopy.equalValues(obj1,obj2)); 
		obj1 = "Hallo1"; 
		obj2 = "Hallo2";
		Assert.assertFalse(ObjectCopy.equalValues(obj1,obj2)); 
		obj1 = "Hallo"; 
		obj2 = "Hallo";
		Assert.assertTrue(ObjectCopy.equalValues(obj1,obj2));
		// Test with collections 
		Vector vec1 = new Vector();
		vec1.add(obj1);
		vec1.add(obj3); 
		Vector vec2 = new Vector();
		vec2.add(obj2);
		vec2.add(obj4); 
		Assert.assertTrue(ObjectCopy.equalValues(vec1,vec2)); 
		// Test with Maps 
		Map map1 = new HashMap();
		map1.put(obj1,obj3);
		Map map2 = new HashMap();
		map2.put(obj2,obj4); 
		Assert.assertTrue(ObjectCopy.equalValues(map1,map2)); 
	}

	

}
