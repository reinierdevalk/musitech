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
 * File ReflectionAccessTest.java created on 07.01.2004 by tweyde.
 */
package de.uos.fmt.musitech.utility.obj;

import junit.framework.TestCase;

/**
 * 
 * @author Tillman Weyde
 */
public class ReflectionAccessTest extends TestCase {

	private String testProperty1 = "testProperty1";
	public String TestProperty1 = "TestProperty1";
	public String testProperty2 = "testProperty2";
	
	/**
	 * @return
	 */
	public String getTestProperty1() {
		return testProperty1;
	}

	/**
	 * @param string
	 */
	public void setTestProperty1(String string) {
		testProperty1 = string;
	}

	
	/**
	 * test the property access by getters and setters.
	 */
	public void testAccess(){
		ReflectionAccess class_access = ReflectionAccess.accessForClass(this.getClass());
		assertTrue(class_access.getPropertyType("testProperty1")==String.class);
		String value = (String)class_access.getProperty(this,"testProperty1");
		assertTrue(value.equals("testProperty1"));
		assertTrue(class_access.getPropertyType("TestProperty1")==String.class);
		value = (String)class_access.getProperty(this,"TestProperty1");
		assertTrue(value.equals("TestProperty1"));
		assertTrue(class_access.getPropertyType("testProperty2")==String.class);
		value = (String)class_access.getProperty(this,"testProperty2");
		assertTrue(value.equals("testProperty2"));
	}

	/**
	 * test the property names list.
	 */
	public void testNames(){
		ReflectionAccess class_access = ReflectionAccess.accessForClass(this.getClass());
		String[] names = class_access.getPropertyNames();
		boolean contains1 = false;
		boolean contains1a = false;
		boolean contains2 = false;
		for (int i = 0; i < names.length; i++) {
			if(names[i].equals("testProperty1"))
				contains1 = true; 
			if(names[i].equals("TestProperty1"))
				contains1a = true; 
			if(names[i].equals("testProperty2"))
				contains2 = true; 
		}
		assertTrue(contains1);
		assertTrue(contains1a);
		assertTrue(contains2);
	}
	

}
