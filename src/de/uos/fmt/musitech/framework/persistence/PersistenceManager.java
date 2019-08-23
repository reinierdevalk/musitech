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
 * Created on 20.01.2004
 */
package de.uos.fmt.musitech.framework.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.framework.persistence.exceptions.PersistenceException;

/**
 * PersistenceManager defines the interface for persistent data repositories,
 * e.g. xml-files or a database system. <BR>
 * <BR>
 * A PersistenceManager can contain 'dependent' and 'independent' objects.
 * Independent objects are stored by name, dependent objects are stored when they are
 * directly or indirectly referenced by an independent object. <BR>
 * <BR>
 * A PersistenceManager may work with in-memory objects, therefore a call to
 * store(), update(), or delete does not necessarily ensure that the data is
 * written to disk persistently. To make sure that everything is written to
 * disk, call writeDirty(). <BR>
 * <BR>
 * The data manager may drop dependent objects that are not referenced any more
 * by independent objects at any time. Using create does not guaranty that the
 * object is stored persistently.
 * 
 * @author Jens Wissmann and Tillman Weyde
 */
public interface PersistenceManager {

    /**
     * Store an object in the repository, a new id for the given object is
     * created, but the object will not be stored persistently, if it is not
     * referenced by an independent object, i.e. one that is stored with a name.
     * If the object is already in the repository, its value will be updated.
     * 
     * @param object The object to store
     * @return object-id The ID assigned to this object.
     * @throws PersistenceException If the object could not be saved.
     */
    public Long store(MObject object) throws PersistenceException;

    /**
     * Update an existing object in the repository, using the given ID.
     * 
     * @param object The object to store.
     * @throws PersistenceException
     */
    public void update(MObject object) throws PersistenceException;

    /**
     * Read an object from the repository.
     * 
     * @param id The ID to look for.
     * @return The retrieved object, may be null if no object with the given ID
     *         has been stored.
     * @throws PersistenceException If an internal error occurs.
     */
    public MObject read(Long id) throws PersistenceException;

    // 	  Not needed any more. Independent data can be deleted using
    //    storeObjectByName(null), dependent data is deleted sutomatically.
    //    /**
    //     * Delete the object with the given id
    //     *
    //     * @param id The id of the object to delete.
    //     * @return true if there was an object to delete.
    //     * @throws PersistenceException
    //     */
    //    public boolean delete(Long id) throws PersistenceException;

    // removed, because it is not reliable anyway (objects may be removed
    // anytime if not referenced).
    //    /**
    //     * Check whether the object with the given id exists in the repository.
    //     *
    //     * @param id the id to look for.
    //     * @return True if the object exists.
    //     */
    //    public boolean exists(Long id);

    /**
     * Get the names of the independent objects in the repository.
     * 
     * @return A list with the independent objects' names.
     */
    public Collection getNames();

    /**
     * Get an independent object by name.
     * 
     * @param name The name to identify the object, null may be used to delete
     *            an entry.
     * @return the Object stored under the given name, or null if there was
     *         none.
     */
    public MObject getByName(String name);

    /**
     * Stores the object under the given name. The name must not be used.
     * 
     * @param object The object to be stored.
     * @param name The name of the object.
     * @throws PersistenceException If an error occures, especially if the name
     *             is already used.
     */
    public void storeByName(MObject object, String name) throws PersistenceException;

    /**
     * Writes all data to the database, making sure that there in no data left
     * in caches.
     *  
     */
    public void writeDirty();

    /**
     * This method closes the repository and writes all data to the repository,
     * that has not been written yet. The framework must ensure that this method
     * is called before the application terminates (e.g. using a shutdown hook).
     */
    public void close();

    /**
     * export the contents of this repository to the given file as MusiteXML.
     * 
     * @param file The file to write to
     */
    public void export(File file) throws IOException;

}