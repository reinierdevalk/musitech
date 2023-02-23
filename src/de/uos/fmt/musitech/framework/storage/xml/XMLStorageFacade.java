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
package de.uos.fmt.musitech.framework.storage.xml;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import de.uos.fmt.musitech.framework.storage.StorageSystemException;
import de.uos.fmt.musitech.framework.storage.StorageSystemFacadeBase;
import de.uos.fmt.musitech.utility.obj.ObjectCopy;

/**
 * @author Jens Geisler
 */
public class XMLStorageFacade extends StorageSystemFacadeBase {

  private static Hashtable dataBases;

  private File file;
  private String canonicalName;
  private Hashtable theObjects;
  private long nextID;

  public static XMLStorageFacade getXMLStorageFacade(File file, boolean readOnly) throws StorageSystemException {
    XMLStorageFacade storage;
    try {
      storage = (XMLStorageFacade) getDataBases().get(file.getCanonicalPath());
    } catch (IOException e) {
      throw new StorageSystemException("Could not get canonical name of file '" + file.getName() + "'");
    }
    if (storage == null) {
      storage = new XMLStorageFacade(file, readOnly);
    } // end if
    return storage;
  }

  private XMLStorageFacade(File file, boolean readOnly) throws StorageSystemException {
    super();

    Hashtable dbs = getDataBases();
    try {
      canonicalName = file.getCanonicalPath();
    } catch (IOException e) {
      throw new StorageSystemException("Could not get canonical name of file '" + file.getName() + "'");
    }

    synchronized (dataBases) {
      if (dbs.get(canonicalName) != null)
        throw new StorageSystemException("There is already a data base object for the file '" + canonicalName + "'");

      this.file = file;

      if (file.canRead()) {
        if (!readOnly && !file.canWrite())
          throw new StorageSystemException("You won't be able to write back to this file!");

        try {
          theObjects = (Hashtable) ObjectCopy.readXML(file);
        } catch (Exception e) {
          throw new StorageSystemException("Could not get data from file " + file.getName() + " because " + e.toString());
        }

        try {
          nextID = ((Long) theObjects.get("NextID")).longValue();
        } catch (Exception e) {
          throw new StorageSystemException("Invalid data file, could not find next id object");
        }
      } else if (!file.exists()) {
        try {
          file.createNewFile();
        } catch (Exception e) {
          throw new StorageSystemException(e);
        }
        theObjects = new Hashtable();
        nextID = -2;
        theObjects.put("NextID", new Long(nextID));
      } else
        throw new StorageSystemException("Could not read from file " + file.getName());

      dbs.put(canonicalName, this);
    }
  }

  protected synchronized static Hashtable getDataBases() {
    if (dataBases == null)
      dataBases = new Hashtable();
    return dataBases;
  }

  protected void write() {
    theObjects.put("NextID", new Long(nextID));
    ObjectCopy.writeXML(theObjects, file);
  }

  @Override
protected Object getData(long id, boolean locking) throws StorageSystemException {
    return getData(id);
  }

  @Override
protected Object getData(long id) throws StorageSystemException {
    Object o;
    o = theObjects.get(new Long(id));
    if (o == null)
      throw new StorageSystemException("No object with id " + id + " found");

    try {
      return ObjectCopy.copyObject(o);
    } catch (Exception e) {
      throw new StorageSystemException("Could not copy object of type '" + o.getClass().getName() + "'");
    }
  }

  @Override
protected synchronized void setData(long id, Object data) throws StorageSystemException {
    Object o;
    if ((o = theObjects.get(new Long(id))) == null) {
      if (id > 0) {
        try {
          theObjects.put(new Long(id), ObjectCopy.copyObject(data));
        } catch (Exception e) {
          throw new StorageSystemException("Could not copy object of type '" + o.getClass().getName() + "'");
        }
      } else
        throw new StorageSystemException("You can not set data to nonexistent ids < 0");
    } else {
      if (o.getClass().equals(data.getClass())) {
        try {
          theObjects.put(new Long(id), ObjectCopy.copyObject(data));
        } catch (Exception e) {
          throw new StorageSystemException("Could not copy object of type '" + o.getClass().getName() + "'");
        }
      } else
        throw new StorageSystemException("You can only set data of type '" + o.getClass().getName() + "' to id " + id);
    }
  }

  @Override
protected synchronized long setData(Object o, boolean locking) throws StorageSystemException {
    long id = nextID--;

    try {
      theObjects.put(new Long(id), ObjectCopy.copyObject(o));
    } catch (Exception e) {
      throw new StorageSystemException("Could not copy object of type '" + o.getClass().getName() + "'");
    }
    return id;
  }

  @Override
public void remove(long id) throws StorageSystemException {
    try {
      if (theObjects.remove(new Long(id)) == null)
        throw new StorageSystemException("ID not Found: " + id);
    } catch (Exception e) {
      throw new StorageSystemException("Could not remove object with id '" + id + "'");
    }
  }

  @Override
public String getClassName(long id) throws StorageSystemException {
    Object o;
    if ((o = theObjects.get(new Long(id))) == null)
      throw new StorageSystemException("No object with id " + id + " found");
    return o.getClass().getName();
  }

  @Override
public void setLocking(boolean locking) throws StorageSystemException {
  }
  @Override
public void unlock(long id) {
  }
  @Override
public void unlockAll() {
  }
  @Override
public boolean isLocked(long id) {
    return false;
  }

  @Override
public String getOwner(long id) throws StorageSystemException {
    return "xml";
  }

  @Override
public void setOwner(long id, String name) throws StorageSystemException {
  }

  @Override
public String getReadGroup(long id) throws StorageSystemException {
    return "xml";
  }

  @Override
public void setReadGroup(long id, String name) throws StorageSystemException {
  }

  @Override
public String getWriteGroup(long id) throws StorageSystemException {
    return "xml";
  }

  @Override
public void setWriteGroup(long id, String name) {
  }

  @Override
protected void releaseResources() {
    if (file == null)
      return;
    Hashtable dbs = getDataBases();

    write();
    theObjects = null;
    file = null;
    dbs.remove(canonicalName);
  }
}
