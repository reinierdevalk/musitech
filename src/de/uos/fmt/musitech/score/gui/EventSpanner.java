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
import java.util.Iterator;
import java.util.Vector;

import de.uos.fmt.musitech.score.util.ReverseIterator;

/** This class is an abstract base for all notation elements that span several
 *  notes/rests horizontally, like beams, slurs, crescendos etc.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public abstract class EventSpanner extends ScoreObject {
	private Vector events = new Vector();

	/**
	 * @param i the index of the to be returned event
	 * @return the event at the specified index
	 */
	public Event get(int i) {
		return (Event)events.get(i);
	}
	
	/**
	 * @return the number of events in the spanner
	 */
	public int spannerSize() {
		return events.size();
	}

	/** Adds an event that should be recognized by the spanner. */
	public void add(Event ev) {
		events.add(ev);
	}

	public boolean contains(Event ev) {
		return events.contains(ev);
	}
	
	public int indexOf(Event ev) {
		return events.indexOf(ev);
	}
	
	public void removeLast() {
		events.remove(events.size() - 1);
	}

	public void remove(Event e) {
		events.remove(e);
	}
	
	/** Number of currently spanned events. */
	public int numEvents() {
		return events.size();
	}

	/** Returns the n-th event of this spanner (starting with index 0). 
	 * If n is negative the (n+numEvents())-th event is returned. */
	public Event getEvent(int n) {
		if (n < 0)
			n += events.size();
		return n < 0 ? null : (Event) (events.get(n));
	}

	/** Draws this spanner onto the given Graphics object. */
	public abstract void paint(Graphics g);

	Class parentClass() {
		return System.class;
	}

	public Iterator iterator() {
		return events.iterator();
	}
	public Iterator reverseIterator() {
		return new ReverseIterator(events);
	}
	
	public ScoreObject catchScoreObject(int x, int y, Class objectClass) {
		if (this.getClass().equals(objectClass)) {
			//if one of the objects below matches we return this one
			for (int i = 0; i < spannerSize(); i++) {
				//we cannot use contains() here, because Chord has a comples way of
				//  determining if it matches which is only implemented in catchScoreObject()
				if (get(i).catchScoreObject(x, y, get(i).getClass()) != null) {
					return this;		
				}
			}
		}	
	
		for (int i = 0; i < spannerSize(); i++) {
			ScoreObject so = get(i).catchScoreObject(x, y, objectClass);
			if (so != null)
				return so;
		}
	
		return null;
	}

	
	public void setColor(Color c) {
		super.setColor(c);
		
		for (int i = 0; i < spannerSize(); i++) {
			get(i).setColor(c);
		}
	}
}