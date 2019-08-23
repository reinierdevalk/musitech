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
 * XMLPersistenceManager.java Created on 2004-6-25
 */
package de.uos.fmt.musitech.framework.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.framework.persistence.exceptions.PersistenceException;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * A PersistenceManager, that uses an in-memeory representation and writes the
 * data to XML for persistence.
 * 
 * @author Jens Wissmann and Tillman Weyde
 */
public class MemoryXMLPersistenceManager implements PersistenceManager {

	/**
	 * <code>instance</code> is the only instance of this class.
	 */
//	private static MemoryXMLPersistenceManager instance;
	private static Map<File, MemoryXMLPersistenceManager> fileManagerMap = new HashMap<File, MemoryXMLPersistenceManager>();

	private File dbFile = new File("db.xml");

	/**
	 * <code>entryMap</code> contains all <code>MObject</code> s in the
	 * PersistenceManager. TODO use WeakReferences instead of direct ones.
	 */
	private Map<Long, MObject> entryMap = new HashMap<Long, MObject>();

	/**
	 * Counter used for generating IDs.
	 */
	private long freeID = -1;

	/**
	 * <code>nameMap</code> contains all the independent objects.
	 */
	private Map<String, MObject> nameMap = new HashMap<String, MObject>();

	/**
	 * Create the instance, is private to prevent multiple instances.
	 */
	@SuppressWarnings("unchecked")
	private MemoryXMLPersistenceManager() {
		this(null);
	}

	/**
	 * Create the instance, is private to prevent multiple instances.
	 */
	@SuppressWarnings("unchecked")
	private MemoryXMLPersistenceManager(File file) {
		if (file != null)
			dbFile = file;
		if (dbFile.exists() && dbFile.length() > 0) {
			Map<String, MObject> readXML = (Map<String, MObject>) ObjectCopy.readXML(dbFile);
			nameMap = readXML;
		}
		if (nameMap == null) {
			nameMap = new HashMap<String, MObject>();
		}
		fillEntryMap();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				MemoryXMLPersistenceManager.this.close();
			}
		});
	}

	/**
	 * Gives the object a new id and stores. Storage is persistent only if the
	 * object is refereneces by an independent object. If the object is already
	 * in the repository, nothing happens and the MObject's uid is returned.
	 * 
	 * @param mobject The object to store.
	 * @return The id of the stored object.
	 * @throws PersistenceException
	 * @see de.uos.fmt.musitech.framework.persistence.PersistenceManager#store(MObject)
	 */
	public synchronized Long store(MObject mobject) throws PersistenceException {
		// assign an ID and store in DB
		if (entryMap.containsKey(mobject.getUid()))
			return mobject.getUid();
		Long id = getFreeID();
		mobject.setUid(id);
		recurseMObjects(fillEntryProcessor, mobject);
		return id;
	}

	/**
	 * Update the state of an object, not effective here, only in-memeory store
	 * is used. To write to disk, use writeDirty (wirtes all data to the
	 * XML-file, may take some time).
	 * 
	 * @see de.uos.fmt.musitech.framework.persistence.PersistenceManager#update(MObject)
	 */
	public synchronized void update(MObject mobject) throws PersistenceException {
		// nothing to do here, we keep objects only in memory.
	}

	/**
	 * Retrieve an MObject by its ID.
	 * 
	 * @see de.uos.fmt.musitech.framework.persistence.PersistenceManager#read(long)
	 */
	public MObject read(Long id) throws PersistenceException {
		return (MObject) entryMap.get(id);
	}

	/**
	 * Get an indepenent object.
	 * 
	 * @return The object associated with the name, or null if there is none.
	 * @see de.uos.fmt.musitech.framework.persistence.PersistenceManager#getByName(java.lang.String)
	 */
	public MObject getByName(String name) {
		return (MObject) nameMap.get(name);
	}

	/**
	 * Gets all the names under which independent obejcts are stored.
	 * 
	 * @see de.uos.fmt.musitech.framework.persistence.PersistenceManager#getNames()
	 */
	public Collection getNames() {
		return nameMap.keySet();
	}

	/**
	 * @see de.uos.fmt.musitech.framework.persistence.PersistenceManager#storeByName(de.uos.fmt.musitech.data.MObject,
	 *      java.lang.String)
	 */
	public void storeByName(MObject object, String name) throws PersistenceException {
		nameMap.put(name, object);
		if (object != null)
			store(object);
	}

	/**
	 * Writes all data to file.
	 * 
	 * @see de.uos.fmt.musitech.framework.persistence.PersistenceManager#writeDirty()
	 */
	public void writeDirty() {
		// write data to file
		if (!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ObjectCopy.writeXML(nameMap, dbFile);
	}

	/**
	 * Close the database, write in-memory-data to db-file
	 */
	public void close() {
		// delete instance
		writeDirty();
		fileManagerMap.remove(dbFile);
//		instance = null;
	}

	/**
	 * <code>fillEntryProcessor</code> generates entries for all
	 */
	MObjectProcessor fillEntryProcessor = new MObjectProcessor() {

		public boolean processMObject(MObject obj) {
			Long id = obj.getUid();
			Object existing = entryMap.get(id);
			if (existing != null && obj != existing) {
				System.out
						.println("Warning: corrupt data : Multiple objects with same ID. Changing ID of object "
									+ obj);
				obj.setUid(getFreeID());
			}
			entryMap.put(id, obj);
			return true;
		}
	};

	/**
	 * TODO add comment
	 */
	private void fillEntryMap() {
		recurseIndependentObjects(fillEntryProcessor);
	}

	/**
	 * Recurse through all independent objects using the given processor.
	 * 
	 * @param processor
	 */
	private void recurseIndependentObjects(MObjectProcessor processor) {
		Set keys = nameMap.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			MObject obj = nameMap.get(iter.next());
			processor.processMObject(obj);
		}
	}

	/**
	 * Recurse through the MObjects, using the given processor.
	 * 
	 * @param processor The processor to use on the Mobjects.
	 * @param obj The object to process recursively.
	 * @return true if recursing should be contiued, false if recursing and
	 *         processing should stop.
	 */
	private boolean recurseMObjects(MObjectProcessor processor, MObject obj)
			throws PersistenceException {
		if (!processor.processMObject(obj))
			return false;
		if (obj instanceof Collection) {
			for (Iterator iter = ((Collection) obj).iterator(); iter.hasNext();) {
				Object o2 = iter.next();
				if (o2 instanceof MObject) {
					if (!recurseMObjects(processor, obj))
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * Data are saved before the Manager is finalized.
	 * 
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

//	/**
//	 * Gets the singleton instance of this Manager.
//	 * 
//	 * @return The instance of the MemoryXMLPersistenceManager.
//	 */
//	public static MemoryXMLPersistenceManager getInstance() {
//		if (instance == null)
//			return new MemoryXMLPersistenceManager();
//		else
//			return instance;
//	}

	/**
	 * Gets the singleton instance of this Manager.
	 * 
	 * @return The instance of the MemoryXMLPersistenceManager.
	 */
	public static MemoryXMLPersistenceManager getInstance(File file) {
		MemoryXMLPersistenceManager finst = fileManagerMap.get(file);
		if (finst == null) {
			finst = new MemoryXMLPersistenceManager(file);
			fileManagerMap.put(file,finst);
		}
		return finst;
	}

	/**
	 * Get an unused ID.
	 * 
	 * @return the ID.
	 */
	private Long getFreeID() {
		long result = -1;

		// if no freeid is unknown find a free id
		if (freeID == -1) {
			freeID = findNextFreeID();
		}

		// set freeid as result
		result = freeID;
		// find next free ID for next call
		freeID = findNextFreeID();

		return new Long(result);
	}

	/**
	 * Get the next free ID as a long.
	 * 
	 * @return The long value of the next free ID.
	 */
	private long findNextFreeID() {
		// loop until free ID is found
		while (true) {
			freeID++;
			if (!entryMap.containsKey(new Long(freeID)))
				return freeID;
		}
	}

	/**
	 * TODO add comment
	 * 
	 * @see de.uos.fmt.musitech.framework.persistence.PersistenceManager#export(java.io.File)
	 */
	public void export(File file) throws IOException {
		// TODO Auto-generated method stub

	}

}