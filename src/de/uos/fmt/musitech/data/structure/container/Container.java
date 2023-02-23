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

package de.uos.fmt.musitech.data.structure.container;

import java.util.Collection;
import java.util.Iterator;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.rendering.RenderingSupported;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.time.Timed;

/**
 * Containers represent structures of musical data, e.g. a motif, a voice, a section, a chord etc. 
 * Containers extend collections.
 * 
 * @author Tillman Weyde
 * @param <Type> The type of object to be stored in this container.
 */
public interface Container<Type> extends Named, MObject, Containable, Timed, RenderingSupported, Iterable<Type>
{

	/**
	 * Set the RenderingHints for this container
	 * @param rh The RenderingHints to set.
	 */
	@Override
	void setRenderingHints(RenderingHints rh);
	
	/**
	 * This method should return for the key. If the key is unknown or no RenderingHints
	 * are set it should return null. A NullPointerException must not be thrown.
	 * @param key The key for the RenderingHint.
	 * @return The RenderingHint stored for this key, if there is one, null otherwise.
	 */
	Object getRenderingHint(String key);
	
	
	/**
	 * This method adds a rendering hint. It should work even if the renderingHints have not
	 * been initialised. If a rendering hint has been stored under this key, it will be overwritten. 
	 * 
	 * @param key The key to store under.
	 * @param value The RenderingHint to store.
	 */
	void addRenderingHint(String key, Object value);
	
	/**
	 * Add an Object to this Container
	 * 
	 * @param obj
	 *           the object to add to the Container
	 * @return true if the object was successfully added
	 */
	public boolean add( Type obj );

	/**
	 * Return the Iterator from the Container's contents
	 * 
	 * @return the Iterator
	 */
	@Override
	public Iterator<Type> iterator();

	/**
	 * Return the object at a specific index
	 * 
	 * @param i
	 *           the index of the object to retrieve
	 * @return the Object found at the index given
	 */
	Type get( int i );

	/**
	 * The number of Objects in this container
	 * 
	 * @return the number of objects in this container
	 */
	public int size();

	/**
	 * Remove the given object from the container if it exists
	 * 
	 * @param obj
	 *           the object to be remove
	 * @return true if the object was removed successfully
	 */
	boolean remove( Object obj );

	/**
	 * Add the given containers to this one
	 * 
	 * @param c
	 *           the container object to add
	 * @return true if adding the container was successful
	 */
	boolean  addAll( Container<? extends Type> c );

	/**
	 * Retrieve the index of the objec tin this container
	 * 
	 * @param obj
	 *           the object of which to retrieve the index
	 * @return the index of the given object
	 */
	int indexOf( Object obj );

	/**
	 * Does this container contain the given object?
	 * 
	 * @param obj
	 *           the object to test its existence in the container
	 * @return whether or not the object is in the container
	 */
	boolean contains( Object obj );

	/**
	 * Gets all recursively contained Containables and adds them to the given list.
	 * 
	 * @param set
	 *           The collection to put the contents into. If <code>set</code> is null, a new one
	 *           must be created by implementors.
	 * @return The collection with the added contents.
	 */
	Collection<Containable> getContentsRecursiveList( Collection<Containable> set );

	/**
	 * Gets an array of all recursively contained Containables.
	 * 
	 * @return The recursive content of all contents.
	 */
	Containable[] getContentsRecursive();

	/**
	 * Tests if an object is contained in this container or in its sub-containers.
	 * 
	 * @param containable
	 *           The object to look for.
	 * @return true if contained.
	 */
	boolean containsRecursive( Containable containable );

	/**
	 * Get the context of this container.
	 * 
	 * @return The context of this container.
	 */
	Context getContext();
	
	/**
	 * Returns a cached NotationSystem with score representation of 
	 * this Container's content. Will return null it this has not been 
	 * created previously.
	 * 
	 * @return NotationSystem a score representation of this Container
	 */
	NotationSystem getScore();

	/**
	 * Removes all contents from the Container.
	 */
	public void clear();
	
}