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
package de.uos.fmt.musitech.framework.change;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Felix Kugel
 * 
 * Ereignis wird an Views verschickt, wenn sich deren Daten geändert haben
 * 
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class DataChangeEvent implements Serializable{

	// TODO List of changed data objects.
	
	//source sending the DataChangeEvent, 
	//(that is the view calling the constructor of this class)
	Object source;
	//collection to contain the objects that have been changed by the source
	Collection changedData = new ArrayList();

	/**
	 * Empty constructor to meet the requirements of JavaBeans.
	 */
	public DataChangeEvent() {
	}

	/**
	 * Constructor with argument <code>source</code>.
	 * @param source Object instantiating this DataChangeEvent.
	 */
	public DataChangeEvent(Object source) {
		this.source = source;
	}

	/**
	 * Constructor with argument <code>changedData</code>
	 * @param changedData Collection containing the objects that have been operated on by a view
	 */
	public DataChangeEvent(Collection changedData) {
		if (!this.changedData.isEmpty())
			this.changedData.clear();
		this.changedData.addAll(changedData);
	}

	/**
	 * Constructor with arguments <code>source</code> and <code>changedData</code>.
	 * @param source Object instantiating this DataChangeEvent
	 * @param changedData Collection of objects that have been changed
	 */
	public DataChangeEvent(Object source, Collection changedData) {
		this(changedData);
		this.source = source;
	}

	/**
	 * Returns the DataChangeEvent's <code>source</code>.
	 * @return Object <code>source</code> of this DataChangeEvent
	 */
	public Object getSource() {
		if (source == null)
			throw new IllegalArgumentException("null source");
		return source;
	}

	/**
	 * Sets the Collection <code>changedData</code> of this DataChangeEvent.
	 * @param changedData Collection of objects to be set as this DataChangeEvent's <code>source</code>
	 */
	public void setChangedData(Collection changedData) {
		if (!this.changedData.isEmpty())
			this.changedData.clear();
		this.changedData.addAll(changedData);
	}

	/**
	 * Returns the Collection <code>changedData</code> of this DataChangeEvent.
	 * @return Collection <code>changedData</code> of this DataChangeEvent
	 */
	public Collection getChangedData(){
		return changedData;
	}
	
}
