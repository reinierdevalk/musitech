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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Context;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.time.Timed;
import de.uos.fmt.musitech.data.time.TimedComparator;
import de.uos.fmt.musitech.structure.container.ContainerNotationFilter;
import de.uos.fmt.musitech.structure.container.NotationFilter;
import de.uos.fmt.musitech.structure.container.Score2Score;
import de.uos.fmt.musitech.time.TimeRange;
import de.uos.fmt.musitech.utility.HashCodeGenerator;
import de.uos.fmt.musitech.utility.collection.SortedUniqesCollection;

/**
 * This is a typed Collection with sorted elements. The type must must be
 * provided at creation. If the type is not a Comparable a comparator must also
 * be provided.
 * 
 * @see de.uos.fmt.musitech.data.structure.container.BasicContainer
 * @version $Revision: 8587 $, $Date: 2014-02-13 05:51:26 +0100 (Thu, 13 Feb 2014) $
 * @author Tillman Weyde
 * @param <T> The type of object to be stored in this container.
 * 
 * @hibernate.class table = "SortedContainer"
 */
public class SortedContainer<T> extends SortedUniqesCollection<T> implements
        Container<T> {

    private static final long serialVersionUID = -7861749918992887043L;
	
    private String name = "";

    protected Context context;
    
    protected RenderingHints renderingHints;
    
    protected NotationFilter notationFilter;

	//	Unique ID for this object.
	private Long uid;
	private int hashCode = HashCodeGenerator.getHashCode();

	/**
	 * Returns a unique ID. 
	 * @see java.lang.Object#hashCode()
	 *
	 * a Hibernate Method Only
	 *  
	 * hibernate.property 
	 */
	public int getHashCode() {
		return hashCode;
	}
	
	/**
	 * Returns a unique ID. 
	 * @see java.lang.Object#hashCode()
	 * 
	 */
	
	public int hashCode() {
		return hashCode;
	}

	
    /**
     * This comparator does not really compare, always returns 0;
     * 
     * @author tweyde
     */
    class DummyComparator implements Comparator {
        /**
         * This compare does not really compare, always returns 0;
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            return 0;
        }
    }

    /**
     * @see de.uos.fmt.musitech.data.structure.SortedUniqesCollection#SortedUniqesCollection(java.lang.Class,
     *      java.util.Comparator)
     * @param types
     *            Classes must be subinterfaces of classes implementing
     *            Conainable.
     * @param comp
     *            the Comparator must work for all classes in types.
     */
    public SortedContainer(Context context, Class type, Comparator comp) {
        super(type, comp);
        setContext(context);
    }

    /**
     * @see de.uos.fmt.musitech.data.structure.SortedUniqesCollection#SortedUniqesCollection(java.lang.Class,
     *      java.util.Comparator)
     * @param types
     *            Classes must be subinterfaces of classes implementing
     *            Conainable.
     * @param comp
     *            the Comparator must work for all classes in types.
     */
    public SortedContainer(Context context, Comparator comp) {
        super(comp);
        setContext(context);
    }

//    /**
//     * @see de.uos.fmt.musitech.data.structure.SortedUniqesCollection#SortedUniqesCollection(java.lang.Class,
//     *      java.util.Comparator)
//     * @param types
//     *            Classes must be sub-interfaces of classes implementing
//     *            Containable.
//     * @param comp
//     *            the Comparator must work for all classes in types.
//     */
//    public SortedContainer(Context context, Class types[], Comparator comp) {
//        super(types, comp);
//        for (int i = 0; i < types.length; i++) {
//            Class type = types[i];
//            if (!Containable.class.isAssignableFrom(type))
//                throw new IllegalArgumentException("Type " + type + " for "
//                        + this.getClass().getName()
//                        + " must implement de.uos.fmt.musitech.Containable");
//
//        }
//        setContext(context);
//    }

    /**
     * Constructor. The default comparator will be used.
     * 
     * @param type
     *            Must be a Comparable and Containable.
     * TODO: Right implementation for non-parameter SortedContainer constructor  
     */
    public SortedContainer(){
        super(Note.class, new TimedComparator());
    }

    /**
     * Constructor. The default comparator will be used.
     * 
     * @param type
     *            Must be a Comparable and Containable.
     */
    SortedContainer(Context context, Class type)
            throws IllegalArgumentException {
        this(context, new Class[] { type });
    }

    /**
     * Constructor. The default comparator will be used.
     * 
     * @param type
     *            Must be a Comparable and Containable.
     */
    private SortedContainer(Context context, Class types[])
            throws IllegalArgumentException {
        super(types);
        for (int i = 0; i < types.length; i++) {
            Class type = types[i];
            if (!Containable.class.isAssignableFrom(type)
                    || !Comparable.class.isAssignableFrom(type))
                throw new IllegalArgumentException("Tpye " + type + " for "
                        + this.getClass().getName()
                        + " must implement Containable and Comparable.");
        }
        setContext(context);
    }

    /**
     * Returns the name.
     * 
     * @return String
     * 
     * @hibernate.property
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * @see de.uos.fmt.musitech.data.structure.container.Container#getContentsRecursiveList()
     */
    public Collection getContentsRecursiveList(Collection list) {
        if (list == null)
            list = new HashSet();
        for (int i = 0; i < size(); i++) {
            Object obj = get(i);
            if( list.add(obj) && obj instanceof Container) {
                Container container = (Container) obj;
                Containable contents[] = container.getContentsRecursive();
                for (int j = 0; j < contents.length; j++)
                    list.add(contents[j]);
            }
        }
        return list;
    }

    /**
     * @see de.uos.fmt.musitech.data.structure.container.Container#getContents()
     */
    public Containable[] getContentsRecursive() {
        Collection list = getContentsRecursiveList(null);
        return (Containable[]) list.toArray(new Containable[list.size()]);
    }

    /**
     * @see de.uos.fmt.musitech.data.structure.container.Container#containsRecursive(de.uos.fmt.musitech.data.structure.Containable)
     */
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
     * Returns the context.
     * 
     * @return Context
     * 
     * @hibernate.many-to-one
     * class = "de.uos.fmt.musitech.data.structure.Context"
     * foreign-key = "uid"     
     */
    
    public Context getContext() {
        if (context == null)
            // TODO adapt when context concept is done.
            setContext(new Piece().getContext());
        return context;
    }

    /**
     * Sets the context.
     * 
     * @param context
     *            The context to set
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Sets the context.
     * 
     * @param context
     *            The context to set
     */

    //	public void setContext(Context context) {
    //		this.context = context;
    //
    //		if (context == null)
    //			return;
    //
    //		Piece work = context.getWork();
    //	}

    /**
     * Gets the next element after the given one.
     * 
     * @param obj
     *            the element to look for.
     * @return The element following the given one, null if the element is not
     *         in the container or if it is the last one in the container.
     * @throws IllegalArgumentException
     *             if Object is not of the type expected for this Container.
     */
    Containable nextElement(Object obj) throws IllegalArgumentException {
        typeCheck(obj);
        int index = indexOf(obj);
        if (index < size() - 1)
            return (Containable) get(index - 1);
        else
            return null;
    }

    /**
     * Gets the element before the given one.
     * 
     * @param obj
     *            the element to look for.
     * @return The element before the given one, null if the element is not in
     *         the container or if it is the first one in the container.
     * @throws IllegalArgumentException
     *             if Object is not of the type expected for this Container.
     */
    public T previousElement(T obj) {
        int index = indexOf(obj);
        index--;
        if (index >= 0)
            return get(index);
        else
            return null;
    }

    /**
     * Method getTimeRange gets the temporal extension of the selection.
     * 
     * @return TimeRange The time covered by the selection. This is null if
     *         there are no timed objects in the selection.
     */
    public TimeRange getTimeRange() {
        //		long start = 0;
        //		long end = 0;
        SortedUniqesCollection timedObjects = getTimedObjects();
        if (timedObjects == null || timedObjects.size() == 0)
            return null;
        TimeRange timeRange = new TimeRange();
        timeRange.setStart(((Timed) timedObjects.get(0)).getTime());
        timeRange.setEnd(((Timed) timedObjects.get(timedObjects.size() - 1))
                .getTime());
        return timeRange;
    }

    public SortedUniqesCollection getTimedObjects() {
        SortedUniqesCollection setOfTimedObjects = new SortedUniqesCollection(
                Timed.class, new TimedComparator());
        for (Iterator iter = iterator(); iter.hasNext();) {
            MObject element = (MObject) iter.next();
            if (Timed.class.isInstance(element)) {
                setOfTimedObjects.add(element);
            }
        }
        return setOfTimedObjects;
    }

	/** 
	 * getUid
	 * @see de.uos.fmt.musitech.data.MObject#getUid()
	 * 
	 * @hibernate.id 
	 * 		generator-class="native" 
	 *  	 
	 */
	public Long getUid() {
		return uid;
	}

	/**
	 * @see de.uos.fmt.musitech.data.MObject#setUid(java.lang.Long)
	 */
	 public void setUid(Long uid) {
	     this.uid = uid;
	}

    /**
     * @see de.uos.fmt.musitech.data.time.Timed#getTime()
     */
    public long getTime() {
        long thisTime = Long.MAX_VALUE;
        for (Iterator iter = iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof Timed) {
                long time = ((Timed) obj).getTime();
                if (time != Timed.INVALID_TIME && time < thisTime) {
                    thisTime = time;
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
    public long getDuration() {
        long lastEnd = getTime();
        if(lastEnd == Timed.INVALID_TIME)
            return 0;
        for (int i = 0; i < size(); i++) {
            if (get(i) instanceof Timed) {
                Timed timed = (Timed) get(i);
                if(timed.getTime() == Timed.INVALID_TIME || timed.getDuration() == Timed.INVALID_TIME)
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
    public boolean isValidValue(String propertyName, Object value) {
        // TODO Auto-generated method stub
        return true; //default
    }
    

    /** 
     * 
     * @see de.uos.fmt.musitech.utility.collection.TypedCollection#getContent()
     * 
     * @hibernate.list name="objInSortedColl"
     * cascade="save-update"
     * table = "obj_in_SortedColl"
     * 
     * 
     * @hibernate.collection-key column="coll_id"
     * @hibernate.collection-index column="position"
     * 
     * 
     * @hibernate.collection-many-to-many class = "de.uos.fmt.musitech.data.MObject"
     * column="object_id"
     * 
     */
    public List<T> getContent() {
        return super.getContent();
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.data.rendering.RenderingSupported#getRenderingHints()
     */
    public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    /**
     * TODO add comment
     * @see de.uos.fmt.musitech.data.rendering.RenderingSupported#setRenderingHints(de.uos.fmt.musitech.data.rendering.RenderingHints)
     */
    public void setRenderingHints(RenderingHints renderingHints) {
        this.renderingHints = renderingHints;
        
    }
    
    public void addRenderingHint(String key, Object value) {
    	if (renderingHints == null)
    		renderingHints = new RenderingHints();
    	renderingHints.registerHint(key, value);
    }
    
    public Object getRenderingHint(String key) {
    	if (renderingHints == null)
    		return null;
    	else 
    		return renderingHints.getValue(key);
    }
    
    /**
     * Sets the NotationFilter used for creating the score of this BasicContainer.
     * 
     * @param notationFilter NotationFilter to be set as the <code>notationFilter</code> of this BasicContainer
     */
    public void setNotationFilter(NotationFilter notationFilter){
        this.notationFilter = notationFilter;
    }

    

    /** 
     * Returns a score representation of this SortedContainer. For this, the <code>score</code>
     * in the Piece of this SortedContainer's Context is copied filtered by the NotationFilter
     * <code>filter</code>. If <code>filter</code> is null, a ContainerNotationFilter is used.
     * If the <code>score</code> in the Context is null, this method returns null.
     * 
     * @see de.uos.fmt.musitech.data.structure.container.Container#getScore()
     */
    public NotationSystem getScore() {
        if (notationFilter!=null){
            notationFilter = new ContainerNotationFilter();
        }
        if (getContext().getPiece().getScore()!=null){
            return Score2Score.copyScore(getContext().getPiece().getScore(), notationFilter);
        }
        return null;
    }

    

    
}