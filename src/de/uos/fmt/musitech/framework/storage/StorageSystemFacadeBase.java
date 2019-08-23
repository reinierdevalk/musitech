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
package de.uos.fmt.musitech.framework.storage;


/**
 * @author Jens Geisler
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class StorageSystemFacadeBase implements StorageSystemFacade {
	private boolean isClosed = false;

	public StorageSystemFacadeBase() {
		Thread t = new Thread() {
			public void run() {
				try {
					close();
				} catch (Exception e) { 
					e.printStackTrace();
				}
			}
		};
		try {
			java.lang.reflect.Method m =
				Runtime.class.getMethod("addShutdownHook", new Class[] {Thread.class});
			if (m != null) {
				m.invoke(Runtime.getRuntime(), new Object[] {t});
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}		

	protected abstract long setData(Object data, boolean locking) throws StorageSystemException;
	protected abstract void setData(long id, Object data) throws StorageSystemException;
	protected abstract Object getData(long id) throws StorageSystemException;
	protected abstract Object getData(long id, boolean locking) throws StorageSystemException;

	public long store(Object data, boolean locking) throws StorageSystemException {
		System.out.println("In store");
		return setData(data, locking);
	}
	
	public void store(long id, Object data) throws StorageSystemException {
		setData(id, data);
	}
	
	public Object load(long id) throws StorageSystemException {
		Object o = getData(id);
		if(o instanceof StorageSystemDependend)
			((StorageSystemDependend)o).setStorageSystem(this);
		return o;
	}
	
	public Object load(long id, boolean locking) throws StorageSystemException {
		Object o = getData(id, locking);
		if(o instanceof StorageSystemDependend)
			((StorageSystemDependend)o).setStorageSystem(this);
		return o;
	}
	
	public abstract void remove(long id) throws StorageSystemException;
	
	public abstract String getClassName(long id) throws StorageSystemException;

	public abstract void setLocking(boolean locking) throws StorageSystemException;
	public abstract void unlock(long id) throws StorageSystemException;
	public abstract void unlockAll();
	public abstract boolean isLocked(long id);
	
	public abstract String getOwner(long id) throws StorageSystemException;
	public abstract void setOwner(long id, String name) throws StorageSystemException;
	public abstract String getReadGroup(long id) throws StorageSystemException;
	public abstract void setReadGroup(long id, String name) throws StorageSystemException;
	public abstract String getWriteGroup(long id) throws StorageSystemException;
	public abstract void setWriteGroup(long id, String name) throws StorageSystemException;
	
	
	
	protected abstract void releaseResources();

	public final void close() {
		if(!isClosed) {
			releaseResources();
			isClosed = true;
		}			
	}
	
	protected void finalize() throws StorageSystemException {
		close();
	}
}
