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
 * Created on 23.11.2004
 *
 */
package de.uos.fmt.musitech.data.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.framework.persistence.PersistenceFactory;
import de.uos.fmt.musitech.framework.persistence.PersistenceManager;
import de.uos.fmt.musitech.framework.persistence.exceptions.PersistenceException;
import de.uos.fmt.musitech.utility.collection.TypedCollection;

/**
 * This class provides methods for storing and retrieving Objects that are
 * associated with a given Context.
 * 
 * @author Kerstin Neubarth
 *  
 */
public class ContextRepository {

    /**
     * PersistenceManager which is checked for stored objects. If no
     * PersistenceManager is specified, the default PersistenceManager provided
     * by the PersistenceFactory is taken.
     */
    PersistenceManager persistenceManager;

    /**
     * Constructor. The default PersistenceManager provided by the
     * PersistenceFactory is set as the <code>persistenceManager</code> of
     * this ContextRepository.
     */
    public ContextRepository() {
        setPersistenceManager(PersistenceFactory.getDefaultPersistenceManager());
    }

    /**
     * Constructor. Sets the specified PersistenceManager as the
     * <code>persistenceManager</code> of this ContextRepository.
     * 
     * @param persistenceManager
     *            PersistenceManager to be set as the
     *            <code>persistenceManager</code> of this ContextRepository
     */
    public ContextRepository(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    /**
     * Setter for <code>persistenceManager</code>.
     * 
     * @param persistenceManager
     *            PersistenceManager to be set as the
     *            <code>persistenceManager</code> of this ContextRepository
     */
    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    /**
     * Getter for <code>persistenceManager</code>.
     * 
     * @return PersistenceManager the <code>persistenceManager</code> of this
     *         ContextRepository
     */
    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    /**
     * Returns a Collection which contains the Objects of the specified Context
     * which are known to the <code>persistenceManager</code>.
     * 
     * @param context
     *            Context the stored objects of which are to be returned
     * @return Collection containing the Objects of the specified Context that
     *         are stored by the <code>persistenceManager</code>
     */
    public Collection getStoredObjects(Context context) {
        List contextList = Arrays.asList(context.getAll());
        Collection storedObjects = new ArrayList();
        if (persistenceManager == null) {
            persistenceManager = PersistenceFactory
                    .getDefaultPersistenceManager();
        }
        if (persistenceManager != null) {
            Collection objectNames = persistenceManager.getNames();
            for (Iterator iter = objectNames.iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                if (contextList.contains(persistenceManager.getByName(name))) {
                    storedObjects.add(persistenceManager.getByName(name));
                }
            }
        }
        return storedObjects;
    }

    /**
     * Returns a TypedCollection which contains the Objects of the specified
     * Context known to the <code>persistenceManager</code>. The types of the
     * TypedCollection are set to the specified classes.
     * 
     * @param context
     *            Context the stored objects of which are to be returned
     * @param types
     *            Class[] specifying which types of Objects are to be returned
     * @return TypedCollection which contains stored Objects of the specified
     *         Context being of the specified types
     */
    public TypedCollection getStoredObjects(Context context, Class[] types) {
        TypedCollection storedObjects = new TypedCollection(types);
        Collection allContextObjects = getStoredObjects(context);
        for (Iterator iter = allContextObjects.iterator(); iter.hasNext();) {
            MObject element = (MObject) iter.next();
            try {
                storedObjects.add(element);
            } catch (IllegalArgumentException e) {
                //nothing to be done
            }
        }
        return storedObjects;
    }

    /**
     * Returns a TypedCollection which contains the Objects of the specified
     * Context known to the <code>persistenceManager</code>. The type of the
     * TypedCollection is set according to the specified class name.
     * 
     * @param context
     *            Context the stored objects of which are to be returned
     * @param className
     *            String specifying which type of Objects are to be returned
     * @return TypedCollection which contains stored Objects of the specified
     *         Context being of the specified type
     */
    public TypedCollection getStoredObjects(Context context, String className) {
        try {
            Class type = Class.forName(className);
            return getStoredObjects(context, new Class[] { type });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Stores all Objects of the specified Context with the
     * <code>persistenceManager</code> of this ContextRepository.
     * 
     * @param context
     *            Context whose Objects are to be stored
     */
    public void storeObjects(Context context) {
        List contextObjects = context.getList();
        storeContextObjects(contextObjects);
    }

    /**
     * Stores those Objects of the specified Context which are of the specified
     * types with the <code>persistenceManager</code> of this
     * ContextRepository.
     * 
     * @param context
     *            Context whose Objects of the specified types are to be stored
     * @param types
     *            Class[] specifying which types of Objects of the specified
     *            Context are to be stored
     */
    public void storeObjects(Context context, Class[] types) {
        List contextObjects = context.getList();
        List typedObjects = new ArrayList();
        TypedCollection coll = new TypedCollection(types);
        for (int i = 0; i < contextObjects.size(); i++) {
            try {
                coll.add(contextObjects.get(i));	//to check type
                typedObjects.add(contextObjects.get(i));
            } catch (IllegalArgumentException e) {
                //nothing to be done
            }
        }
        storeContextObjects(typedObjects);
    }

    /**
     * Stores those Objects of the specified Context which are of the specified
     * type with the <code>persistenceManager</code> of this
     * ContextRepository.
     * 
     * @param context
     *            Context whose Objects of the specified type are to be stored
     * @param className
     *            String specifying which type of Objects of the specified
     *            Context are to be stored
     */
    public void storeObjects(Context context, String className) {
        List contextObjects = context.getList();
        List typedObjects = new ArrayList();
        try {
            Class type = Class.forName(className);
            for (int i = 0; i < contextObjects.size(); i++) {
               if (type.isAssignableFrom(contextObjects.get(i).getClass())){
                   typedObjects.add(contextObjects.get(i));
               }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }        
        storeContextObjects(typedObjects);
    }
    
    private void storeContextObjects(List contextObjects){
        for (int i = 0; i < contextObjects.size(); i++) {
            if (contextObjects.get(i) instanceof MObject) {
                try {
                    if (contextObjects.get(i) instanceof Named) {
                        persistenceManager.storeByName((MObject)contextObjects.get(i), ((Named)contextObjects.get(i)).getName());
                    } else {
                        persistenceManager.store((MObject) contextObjects.get(i));
                    }
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }
            }
        }  
    }

}