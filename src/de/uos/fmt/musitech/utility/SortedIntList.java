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
package de.uos.fmt.musitech.utility;

/**
 * @author Nicolai Strauch
 * 
 * The elements will be sorted by the Addingprocess.
 * All sort and search-processes are linear, not good for great amoungth of
 * data.
 * Use the Klass for little amoungth of data.
 * 
 * You can add and delete data. 
 * The littlest Element allways will be returned by getFirst().
 * 
 *
 * 
 */
public class SortedIntList {

	int object = Integer.MIN_VALUE;
	SortedIntList nextElem;
	
	public SortedIntList(){}
	private SortedIntList(int elem, SortedIntList next)
	{
		object = elem;
		nextElem = next;
	}
	/** 
	 * @return int - the first element, that is the littlest Element added. Or
	 * Integer.MIN_VALUE, if not Element are added and not deleted in the List
	 */
	public int getFirst()
	{
		if(nextElem == null) return object;
		return nextElem.object;
	}
	/**
	 * add before the first greater Element
	 * @param element
	 */
	public void add(int element)
	{
		SortedIntList tmp = this;
		while(tmp.nextElem!=null && tmp.nextElem.object<element) tmp = tmp.nextElem;
		tmp.nextElem = new SortedIntList(element, nextElem);
	}
	/**
	 * Search for the given int, and delete the first element whith this value.
	 * @param element to be deleted
	 * @return boolean true if deleted an element (if the element was found),
	 * else false
	 */
	public boolean del(int element)
	{
		return delAnd(element)!=null;
	}
	/**
	 * Search for the given int, and delete the first element whith this value.
	 * @param element to be deleted
	 * @return SortedIntList the element before the deleted Element, or null if
	 * nothing was deleted
	 */
	private SortedIntList delAnd(int element)
	{
// the first element is lost, add(int i) never will be fill this element. A bug?
//		if(element==object) 
//		{
//			if(nextElem==null) 
//				object = Integer.MIN_VALUE;
//			else 
//			{
//				object = nextElem.object;
//				nextElem = nextElem.nextElem;
//			}
//			return this;
//		}
		SortedIntList tmp = this;
		while(tmp.nextElem!=null && element!=tmp.nextElem.object) tmp = tmp.nextElem;
		if(element==tmp.nextElem.object)
		{
			if(tmp.nextElem.nextElem == null)
			{
				tmp.nextElem = null;
			}
			else
			{
				tmp.nextElem = tmp.nextElem.nextElem;
			}
			return tmp;
			
		}
		return null;
	}
	/**
	 * delete the first element, and add the second.
	 * @param delElem
	 * @param addElem
	 * @return boolean true if deleted, otherwise false
	 */
	public boolean delAndAdd(int delElem, int addElem)
	{
		if(delElem<addElem)
		{		
			SortedIntList tmp = delAnd(delElem);
			if(tmp==null) return false;
			tmp.add(addElem);
			return true;
		}
		if(delElem==addElem) return true;
		add(addElem);
		return del(delElem);
	}
	/**
	 * Ad an value to all elements, so that all Elements will be greater by this
	 * value
	 * @param toAdd - value to be added
	 */
	public void addOnAll(int toAdd)
	{
		while(nextElem!=null)
		{
			nextElem.object += toAdd;
			nextElem.addOnAll(toAdd);
		}
	}

}
