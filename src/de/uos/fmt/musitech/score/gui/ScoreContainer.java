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
package de.uos.fmt.musitech.score.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.score.util.ReverseIterator;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Martin Gieseking
 * @version $Revision: 8217 $, $Date: 2012-06-21 23:38:50 +0200 (Thu, 21 Jun 2012) $
 */
abstract class ScoreContainer extends ScoreObject implements ScoreContainerBase {

    private List<ScoreObject> children = new ArrayList<ScoreObject>();
    private List<ContentChangeListener> listeners = new ArrayList<ContentChangeListener>();
    
    public int indexOf(ScoreObject child) {
    	return children.indexOf(child);
    }
    
    /**
     * @return the end point in metric time of the last ScoreObject contained in
     *         this container.
     */
    @Override
	public Rational getMetricEndPoint() {
    	if (numChildren() == 0) {
    		return Rational.ZERO;
    	}
        return child(-1).getMetricEndPoint();
    }

    @Override
	public void setScale(float argScale) {
        super.setScale(argScale);
        for (int i = 0; i < numChildren(); i++)
            child(i).setScale(argScale);
    }

    /**
     * This method finds the ScoreObject which metricEndPoint is just before the
     * given point in metric time. If a ScoreObject is found thats also a
     * ScoreContainer, this method works recursivly on that container. That
     * means a ScoreContainer is never returned
     * 
     * 
     * @param m the point in metric time
     * @return the ScoreObject just before metric time equal to m
     */
    public ScoreObject getScoreObjectBeforeMetricTime(Rational m) {
        return getScoreObjectBeforeMetricTime(m, this, ScoreObject.class);
    }

    public ScoreObject getScoreObjectBeforeMetricTime(Rational m, Class<? extends ScoreObject> c) {
        return getScoreObjectBeforeMetricTime(m, this, c);
    }

    public ScoreObject getScoreObjectBeforeMetricTime(Rational m, ScoreContainerBase cont, Class<? extends ScoreObject> c) {
        for (int i = 0; i < cont.numChildren(); i++) {
            if (cont.child(i).getMetricEndPoint().isGreater(m)) {
                if (!(c != null && c.isInstance(cont.child(i))) && cont.child(i) instanceof ScoreContainerBase) {
                    return getScoreObjectBeforeMetricTime(m, (ScoreContainerBase) cont.child(i), c);
                } else { //it's a ScoreObject or an object of type c
                    return cont.child(i);
                }
            }
        }
        return null;
    }

    /*
     * public ScoreObject getScoreObjectAfterMetricTime(Rational m) {
     * ScoreContainer cont = this; for (int i = 0; i < cont.numChildren(); i++) {
     * if (cont.child(i).getMetricTime().isGreaterOrEqual(m)) { if
     * (cont.child(i) instanceof ScoreContainerBase) { return
     * getScoreObjectAfterMetricTime(m, (ScoreContainerBase)cont.child(i)); }
     * else { //it's a ScoreObject return cont.child(i); } } } return null; }
     */

    public List<ScoreObject> getChildren() {
        return children;
    }

    /** Returns a forward iterator for this ScoreContainer. 
     * @return The iterator. 
     * */
    @Override
	public Iterator<ScoreObject> iterator() {
        return children.iterator();
    }

    /** Returns a reverse (backward) iterator for this ScoreContainer. 
     * @return The iterator. 
     */
    @Override
	public Iterator<ScoreObject> reverseIterator() {
        return new ReverseIterator<ScoreObject>(children);
    }

    /** Adds a ScoreObject to this ScoreContainer. 
     * @param obj 
     * @return true if the object has been added succesfully */
    @Override
	public boolean add(ScoreObject obj) {
        if (obj == null)
            throw new IllegalArgumentException("obj must not be null");
        children.add(obj);
        obj.setParent(this);
        if (scale != 1)
            obj.setScale(scale);
        for (ContentChangeListener element: listeners) {
			element.contentAdded(obj);
		}
        return true;
    }

    private MetricalComparator metricalComp = new MetricalComparator();

    public boolean addMetrical(ScoreObject m) {
        if (m == null)
            throw new IllegalArgumentException("m must not be null");

        m.setParent(this);

        //TODO: do a binary search
        //TODO: disallow mixing of addMetrical and add
        for (int i = 0; i < children.size(); i++) {
            if (metricalComp.compare(m, (children.get(i))) <= 0) {
                children.add(i, m);
                return true;
            }
        }
        children.add(m);
        return true;
    }

    public void remove(ScoreObject obj) {
        if (obj == null)
            throw new IllegalArgumentException("obj must not be null");
        children.remove(obj);
        //TODO: what about the parent of obj?
    }

    public void removeScoreObject(int index) {
        Object obj = children.get(index);
    	children.remove(index);
        for (ContentChangeListener element: listeners) {
			element.contentRemoved(obj);
		}
    }

    @Override
	public void removeAll() {
        for (ContentChangeListener element: listeners) {
			for (ScoreObject obj : children) {
				element.contentRemoved(obj);
			}
        }
    	children.clear();
    }

    public boolean add(ScoreObject[] objs) {
        for (int i = 0; i < objs.length; i++) {
            add(objs[i]);
        }
        return true;
    }

    public void add(List<ScoreObject> objs) {
        for (ScoreObject obj: objs) {
            add(obj);
        }
    }

    @Override
	int arrange(int pass) {
        if (pass < 0)
            throw new IllegalArgumentException("\npass number must be non-negative");
        int max = 0;
        for (int i = 0; i < children.size(); i++) {
            max = Math.max(max, children.get(i).arrange(pass));
        }
        max = Math.max(max, super.arrange(pass));
        return max;
    }

    /**
     * Returns the specified ScoreObject from the container.
     * 
     * @param n Index of child to be retrieved. A negative value gets the |n|-th
     *            last child.
     * @return The ScoreObject.
     */
    @Override
	public ScoreObject child(int n) {
        if (n < -children.size() || n >= children.size())
            return null;
        if (n < 0)
            n += children.size();
        return children.get(n);
    }

    /** Returns the number of elements in this container. 
     * @return The number of elements.
     */
    @Override
	public int numChildren() {
        return children.size();
    }

    @Override
	public void paint(Graphics g) {
    	if(!isVisible()) return;
        for (int i = 0; i < children.size(); i++)
            child(i).paint(g);
        super.paint(g);
    }

	@Override
	public void clear() {
		children.clear();
	}

	@Override
	public boolean contains(ScoreObject o) {
		return children.contains(o);
	}

    @Override
	public ScoreObject catchScoreObject(int x, int y, Class<? extends ScoreObject> objectClass) {
        ScoreObject so = super.catchScoreObject(x, y, objectClass);
        if (so != null)
            return so;

        for (int i = 0; i < numChildren(); i++) {
            so = child(i).catchScoreObject(x, y, objectClass);
            if (so != null)
                return so;
        }

        return null;
    }

    /** Returns a string representation of this ScoreContainer. */
    @Override
	public String toString() {
        String res = "";
        for (int i = 0; i < children.size(); i++)
            res += children.get(i) + "\n";
        return res;
    }

    @Override
	public void setColor(Color c) {
        super.setColor(c);

        for (int i = 0; i < numChildren(); i++) {
            child(i).setColor(c);
        }
    }

    int[] getMinMaxY() {
        return getMinMaxY(new ScoreObject[] {});
    }

    int[] getMinMaxY(ScoreObject[] additionals) {
        int maxY = 0;
        int minY = Integer.MAX_VALUE;

        if (numChildren() == 0 &&
            additionals.length == 0) {
          	minY = 0;
        }
        else {
        	for (int i = 0; i < numChildren(); i++) {
        		maxY = Math.max(maxY, child(i).getLocation().y + child(i).getSize().height);
        		minY = Math.min(minY, child(i).getLocation().y);
        	}

        	for (int i = 0; i < additionals.length; i++) {
        		maxY = Math.max(maxY, additionals[i].getLocation().y + additionals[i].getSize().height);
        		minY = Math.min(minY, additionals[i].getLocation().y);
        	}
        }
        
        if (maxY > 2000)
        	System.err.println("AP ?");
        
        return new int[] {minY, maxY};
    }

    int[] getMinMaxX() {
        return getMinMaxX(new ScoreObject[] {});
    }

    int[] getMinMaxX(ScoreObject[] additionals) {
        int maxX = 0;
        int minX = Integer.MAX_VALUE;

        for (int i = 0; i < numChildren(); i++) {
            maxX = Math.max(maxX, child(i).getLocation().x + child(i).getSize().width);
            minX = Math.min(minX, child(i).getLocation().x);
        }

        for (int i = 0; i < additionals.length; i++) {
            maxX = Math.max(maxX, additionals[i].getLocation().x + additionals[i].getSize().width);
            minX = Math.min(minX, additionals[i].getLocation().x);
        }
        return new int[] {minX, maxX};
    }
    
    @Override
	public void addListener(ContentChangeListener l) {
    	listeners.add(l);
    }
    
    @Override
	public void removeListener(ContentChangeListener l) {
    	listeners.remove(l);
    }

}