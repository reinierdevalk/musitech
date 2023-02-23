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
import java.util.HashSet;
import java.util.Iterator;
import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.structure.container.ContainerNotationFilter;
import de.uos.fmt.musitech.structure.container.NotationFilter;
import de.uos.fmt.musitech.structure.container.Score2Score;
import de.uos.fmt.musitech.utility.HashCodeGenerator;
import de.uos.fmt.musitech.utility.collection.TypedCollection;

/**
 * This is the base class for musical Containers. It accepts only types
 * implementing Containable. The allowed types are determined at creation.
 * 
 * @version $Revision: 7838 $, $Date: 2010-05-01 22:23:24 +0200 (Sat, 01 May 2010) $
 * @author Tillman Weyde
 * @param <T> 
 * 
 * @hibernate.class table = "BasicContainer"
 */

public class BasicContainer<T extends Containable> extends TypedCollection<T> implements Container<T>, Cloneable, Named {

    private static final long serialVersionUID = 837114738739070154L;

    String name;

    protected Context context;

    protected RenderingHints renderingHints;

    protected NotationFilter notationFilter;

    //	Unique ID for this object.
    private Long uid;

    private int hashCode = HashCodeGenerator.getHashCode();

    /**
     * Returns a unique ID.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode() {
        return hashCode;
    }

    /**
     * @return Returns the hashCode.
     *  
     */
    public int getHashCode() {
        return hashCode;
    }

    public BasicContainer() {
        super(Containable.class);
    }

    public BasicContainer(Context context) {
        super(Containable.class);
        setContext(context);
    }

    /**
     * Constructor. The default comparator will be used.
     * 
     * @param type Must be a Comparable and Containable.
     */
    public BasicContainer(Context context, Class type) throws IllegalArgumentException {
        super(type);
        if (!Containable.class.isAssignableFrom(type))
            throw new IllegalArgumentException("Type for " + this.getClass().getName() + " must implement Containable");
        setContext(context);
    }

//    /**
//     * Constructor. The default comparator will be used.
//     * 
//     * @param type Must be a Comparable and Containable.
//     */
//    public BasicContainer(Context context, Class types[]) throws IllegalArgumentException {
//        super(types);
//        for (int i = 0; i < types.length; i++) {
//            Class type = types[i];
//            if (!Containable.class.isAssignableFrom(type))
//                throw new IllegalArgumentException("Type for " + this.getClass().getName()
//                                                   + " must implement Containable");
//        }
//        setContext(context);
//    }

    @Override
	protected boolean typeCheck(Object obj) {
        if (obj == null)
            throw new IllegalArgumentException("TypedColletion - add(Obj): Argument must not be null.");
        Class objClass = obj.getClass();
        for (int i = 0; i < getTypes().length; i++) {
            if (getTypes()[i].isAssignableFrom(objClass))
                return true;
        }
        throw new IllegalArgumentException("TypedColletion - add(Obj): Argument " + obj + " is not of correct type.");
    }

    /**
     * Adds an object to this container.
     * 
     * @param obj The obejct to add. 
     * @return true if obj has been added.
     * @see java.util.Collection#add(java.lang.Object)
     */
    @Override
	public boolean add(T obj) {
        typeCheck(obj);
        return super.add(obj);
    }

    /**
     * @param pos 
     * @param obj 
     * @see java.util.List#add(int, Object)
     */
    @Override
	public void add(int pos, T obj) {
        typeCheck(obj);
        super.add(pos, obj);
    }

    /**
     * Add all elements of another collection to this container.
     * 
     * @param c the collection with the elements to add.
     * @return true if the collection has been changed by this call.
     * @see java.util.Collection#addAll(Collection)
     */
	@Override
	public boolean addAll(Container<? extends T> c) {
		if(c == null)
			return false;
        boolean changed = false;
		for(T obj : c) {
            changed &= add(obj);
        }
        return changed;
    }

    /**
     * Checks for object identity.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Gathers a list of all contents 
     * @param list The list to fill with the data 
     * @return the 
     * @see de.uos.fmt.musitech.data.structure.container.Container#getContentsRecursiveList(Collection)
     */
    @Override
	public Collection<Containable> getContentsRecursiveList(Collection<Containable> list) {
        if (list == null)
            list = new HashSet<Containable>();
        for (int i = 0; i < size(); i++) {
        	Containable obj = get(i);
            if(list.add(obj) && obj instanceof Container) {
            	Container container = (Container) obj;
                container.getContentsRecursiveList(list);
            }
        }
        return list;
    }

    /**
     * @see de.uos.fmt.musitech.data.structure.container.Container#containsRecursive(de.uos.fmt.musitech.data.structure.Containable)
     */
    @Override
	public boolean containsRecursive(Containable containable) {
        for (int i = 0; i < size(); i++) {
            Object obj = get(i);
            if (obj == containable)
                return true;
            if (obj instanceof Container) {
                Container container = (Container) obj;
                if (container.containsRecursive(containable))
                    return true;
            }
        }
        return false;
    }

    /**
     * @see de.uos.fmt.musitech.data.Named#getName()
     * @hibernate.property
     */
    @Override
	public String getName() {
        return name;
    }

    /**
     * @see de.uos.fmt.musitech.data.Named#setName(java.lang.String)
     */
    @Override
	public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the context.
     * 
     * @return Context
     * 
     * @hibernate.many-to-one class =
     *                        "de.uos.fmt.musitech.data.structure.Context"
     *                        foreign-key = "uid" cascade = "all"
     *  
     */
    @Override
	public Context getContext() {
        if (context == null)
            // TODO adapt when context concept is done.
            setContext(Context.getDefaultContext());
        return context;
    }

    /**
     * Sets the context.
     * 
     * @param context The context to set
     */
    public void setContext(Context context) {
        this.context = context;
        //        if (context != null) {
        //            Piece work = context.getPiece();
        //        }
        //		if(addToPool)
        //		addToPool:{
        //			if(work == null)
        //			break addToPool;
        //			Container contPool = work.getContainerPool();
        //			if(contPool == null)
        //				break addToPool;
        //			if(this == work.getNotePool()
        //				|| this == work.getAudioPool()
        //				|| this == work.getMetaInfoPool()
        //				)
        //				break addToPool;
        //				
        //			contPool.add(this);
        //		}

    }

    //	/**
    //	 * @see de.uos.fmt.musitech.data.structure.Container#getBeginning()
    //	 */
    //	public TimeStamp getBeginning() {
    //		TimeStamp ts = new TimeStamp(Long.MAX_VALUE);
    //		for (int i = 0; i < size(); i++) {
    //			if (get(i) instanceof Timed)
    //				if (ts.compareTo(((Timed) get(i)).getTimeStamp()) > 0)
    //					ts = ((Timed) get(i)).getTimeStamp();
    //		}
    //		if (ts.getTimeMillis() == Long.MAX_VALUE)
    //			ts = new TimeStamp(0);
    //		return ts;
    //	}

    /**
     * @see de.uos.fmt.musitech.data.time.Timed#getTime()
     */
    @Override
	public long getTime() {
        long thisTime = Long.MAX_VALUE;
        for (Iterator<T> iter = iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof Timed) {
                long time = ((Timed) obj).getTime();
                if (time != Timed.INVALID_TIME) {
                    if (time < thisTime) {
                        thisTime = time;
                    }
                }
            }
        }
        if (thisTime == Long.MAX_VALUE)
            thisTime = 0;
        return thisTime;
    }

    /**
     * @see de.uos.fmt.musitech.data.time.Timed#getDuration()
     */
    @Override
	public long getDuration() {
        long lastEnd = getTime();
        if (lastEnd == Timed.INVALID_TIME)
            return 0;
        for (int i = 0; i < size(); i++) {
            if (get(i) instanceof Timed) {
                Timed timed = (Timed) get(i);
                if (timed.getTime() == Timed.INVALID_TIME)
                    continue;
                long end = timed.getTime() + timed.getDuration();
                if (end > lastEnd) {
                    lastEnd = end;
                }
            }
        }
        return lastEnd - getTime();
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#isValidValue(java.lang.String,
     *      java.lang.Object)
     */
    @Override
	public boolean isValidValue(String propertyName, Object value) {
        // TODO Auto-generated method stub
        return true; //default
    }

    /**
     * This method uses
     * 
     * @return An array with contents of this container and ist recursie subcontainers.
     * 
     * @see de.uos.fmt.musitech.data.structure.container.Container#getContentsRecursiveList(Collection)
     */
    @Override
	public Containable[] getContentsRecursive() {
        Collection<Containable> list = getContentsRecursiveList(null);
        return list.toArray(new Containable[list.size()]);
    }

    /**
     * getUid
     * 
     * @see de.uos.fmt.musitech.data.MObject#getUid()
     * 
     * @hibernate.id generator-class="native"
     */
    @Override
	public Long getUid() {
        return uid;
    }

    /**
     * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
     */
    @Override
	public void setUid(Long uid) {
        this.uid = uid;
    }

//    /**
//     * @hibernate.bag name="objInBasicColl" cascade="save-update" table =
//     *                "obj_in_BasColl"
//     * 
//     * 
//     * @hibernate.collection-key column="coll_id"
//     * 
//     * 
//     * @hibernate.collection-many-to-many class =
//     *                                    "de.uos.fmt.musitech.data.MObject"
//     *                                    column="object_id"
//     */
//    public List getContent() {
//        return super.getContent();
//    }

    /**
     * 
     * @see de.uos.fmt.musitech.data.rendering.RenderingSupported#getRenderingHints()
     */
    @Override
	public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    /**
     * 
     * @see de.uos.fmt.musitech.data.rendering.RenderingSupported#setRenderingHints(de.uos.fmt.musitech.data.rendering.RenderingHints)
     */
    @Override
	public void setRenderingHints(RenderingHints renderingHints) {
        this.renderingHints = renderingHints;
        if (renderingHints != null)
            renderingHints.setParentClass(this.getClass());
    }

    @Override
	public void addRenderingHint(String key, Object value) {
        if (renderingHints == null)
            renderingHints = new RenderingHints();
        renderingHints.registerHint(key, value);
    }

    @Override
	public Object getRenderingHint(String key) {
        if (renderingHints == null)
            return null;
        return renderingHints.getValue(key);
    }

    /**
     * Sets the NotationFilter used for creating the score of this
     * BasicContainer.
     * 
     * @param notationFilter NotationFilter to be set as the
     *            <code>notationFilter</code> of this BasicContainer
     */
    public void setNotationFilter(NotationFilter notationFilter) {
        this.notationFilter = notationFilter;
    }

    private NotationSystem score = null;

    // TODO This should be done outside of this class.
    /**
     * Returns a score representation of this BasicContainer. For this, the
     * <code>score</code> in the Piece of this BasicContainer's Context is
     * copied filtered by the NotationFilter <code>filter</code>. If
     * <code>filter</code> is null, a ContainerNotationFilter is used. If the
     * <code>score</code> in the Context is null, this method returns null.
     * 
     * @see de.uos.fmt.musitech.data.structure.container.Container#getScore()
     */
    @Override
	public NotationSystem getScore() {
        if (score == null) {
            if (notationFilter == null) {
                notationFilter = new ContainerNotationFilter(this);
            }
            if (getContext().getPiece().getScore() != null) {
                // return
                // Score2Score.copyScore(getContext().getPiece().getScore(),
                // notationFilter);
//                if (getRenderingHints()!=null && getRenderingHint("excerpt")!=null && getRenderingHint("excerpt").equals("only")){
//                    getContext().getPiece().getScore().addRenderingHint("gaps", "none");
//                }
                score = Score2Score.copyScore(getContext().getPiece().getScore(), notationFilter);
            }
        }
        return score;
    }
    
    
    /**
     * Lists the object id and the contents.
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return super.toString() + list.toString();
    }
    
    /**
     * @see de.uos.fmt.musitech.utility.collection.TypedCollection#iterator()
     */
    @Override
	public Iterator<T> iterator(){
    	return super.iterator(); 
    }

}