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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import de.uos.fmt.musitech.data.time.Metrical;
import de.uos.fmt.musitech.score.util.TextUtil;
import de.uos.fmt.musitech.utility.math.Rational;


/** This is the abstract base class for all visible score objects.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
public abstract class ScoreObject extends JComponent implements Comparable, Metrical {
	private ScoreObject scoreParent; // reference to parent object
	//   private Pair        pos;     // relative position in pixel units
	private int xpos = 0;
	private int ypos = 0;
	protected float scale = 1.0f; // scale factor [0..1]
	private Color color; // 
	private boolean visible; // true, if the object is visible
	private int leftPadding = 0;
	protected List<CustomScoreObject> customScoreObjects = new ArrayList<CustomScoreObject>();
	protected List<GraphicalSelection> selections = null;
	
	/**
	 * @return The last point in metric time this object deals with. E.g.
	 * a Pitch would return metricStart + metricDuration. If a metric time
	 * is not applicable for a type of object (e.g. Accent) than Rational.ZERO
	 * is returned;
	 */
	public Rational getMetricEndPoint() {
		return Rational.ZERO;
	}
	
	/**
	 * This returns the global selections this object is a part of.
	 * Global selection means, that the object itself or one of it's parents
	 * is part of a selection.
	 * @return the list of global selections
	 */
	public List<GraphicalSelection> getGlobalSelections() {
		List<GraphicalSelection> sel;
		if (selections == null)
			sel = new ArrayList<GraphicalSelection>();
		else 
			sel = new ArrayList<GraphicalSelection>(selections);
		
		if (scoreParent != null)
			sel.addAll(scoreParent.getGlobalSelections());
		
		return sel;
	}
	
	public void addSelection(GraphicalSelection sel) {
		if (selections == null)
			selections = new ArrayList<GraphicalSelection>();
		if (!selections.contains(sel)) {
			selections.add(sel);
			if (score() != null) {
				score().stateChanged();
			}
			else {
				System.err.println(this.getClass() + " cannot find its score!");
			}
		}
	}
	
	public void removeSelection(GraphicalSelection sel) {
		if (selections != null && selections.remove(sel)) {
			if (score() != null) {
				score().stateChanged();
			}
		}
	}
	
	public Rational getMetricTime() {
		return Rational.ZERO;
	}
	
	public Rational getMetricDuration() {
		return getMetricEndPoint().sub(getMetricTime());
	}
	
	
	
	public void setLeftPadding(int pad) {
		leftPadding = pad;
	}
	
	public int getLeftPadding() {
		return leftPadding;
	}
	
	public ScoreObject() {
		scoreParent = null;
		visible = true;
		//      pos = new Pair(0,0);
		color = Color.black; // default color is black
	}

	public ScoreObject(ScoreObject p) {
		if (p.getClass().isAssignableFrom(parentClass()))
			scoreParent = p;
		else
			throw new IllegalArgumentException(
				"\na "
					+ TextUtil.stripPackage(getClass())
					+ "'s parent must be of type "
					+ TextUtil.stripPackage(parentClass()));
		visible = true;
		//      pos = new Pair(0,0);
		color = Color.black;
	}

	/** Returns the class of the superior object.
	 * Nearly all score objects can contain other score objects of
	 * special type, i.e. a system contains several staves. By this means
	 * a unique tree structure is build, so every object class
	 * has a unique parent class, except the top object Score. */
	abstract Class<? extends ScoreObject> parentClass();

	public ScoreObject scoreParent() {
		return scoreParent;
	}
	
	/** Returns the absolute x-coordinate of the object's reference point 
	 * in pixel units. */
	public int absX() {
		int x = (int) Math.round(xpos);
		if (scoreParent != null)
			x += scoreParent.absX();
		return x;
	}

	/** Returns the absolute y-coordinate of the object's reference point 
	 * in pixel units. */
	public int absY() {
		int y = (int) Math.round(ypos);
		if (scoreParent != null)
			y += scoreParent.absY();
		return y;
	}

   /* @@ das arrange-Konzept ist noch eher sub-optimal, es sollte durch eine
      durchdachtere Logik ersetzt werden... */
	int arrange(int pass) {
		int max = 0;
		for (Iterator<CustomScoreObject> i = customScoreObjects.iterator(); i.hasNext();) {
			max = Math.max(max, i.next().arrange(pass));
		}
		return max;
	}
   
	void arrange() {
		int numPasses = arrange(0);
		for (int i = 1; i < numPasses; i++)
			arrange(i);
	}
	
	private boolean alreadyRegistered = false;
	public void registerComponent() {
		
		/* for some reason a component may not be added to a layered pane if it
		 * is alread in there. For JPane it wasn't a problem. Hard to find bug.
		 */ 

		if (alreadyRegistered)
			return;
		
		Score score = score();
		if (score != null) {
			score.registerComponent(this);
			alreadyRegistered = true;
		}
	}
	
	/**
	 * This method registeres this object and all children of it if they are instances of one
	 * of the passed classes. E.g. you can pass Class[]{Measure} to a Page-Object and all
	 * measures of that page object would register themselves as components in this score.
	 * @param classes The types of objects you themselves to register
	 */
	public void registerComponent(Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			if (this.getClass().equals(classes[i])) {
				registerComponent();
				break;
			}
		}
		if (this instanceof ScoreContainerBase) {
			ScoreContainerBase sc = (ScoreContainerBase)this;
			for (int i = 0; i < sc.numChildren(); i++) {
				sc.child(i).registerComponent(classes);
			}
		}
	}
	
	public int compareTo(Object obj) {
		return 0;
	}
	
	public ScoreObject getScoreParent() {
		return scoreParent;
	}
   
	public void setScoreParent(ScoreObject parent) {
		this.scoreParent = parent;
	}
	
	public Color getColor() {
		return color;
	}

	/** Returns true if this object is visible. */
	public final boolean isVisible() {
		return visible;
	}

	public Score score() {
		return scoreParent != null ? scoreParent.score() : null;
	}

	public Chord chord() {
		return scoreParent != null ? scoreParent.chord() : null;
	}
	
	public Page page() {
		return scoreParent != null ? scoreParent.page() : null;
	}
   
	public SSystem system() {
		return scoreParent != null ? scoreParent.system() : null;
	}
   
	public Measure measure() {
		return scoreParent != null ? scoreParent.measure() : null;
	}
   
	public Staff staff() {
		return scoreParent != null ? scoreParent.staff() : null;
	}
   
	public LocalSim localSim() {
		return scoreParent != null ? scoreParent.localSim() : null;
	}
   
	public Pitch pitch() {
		return scoreParent != null ? scoreParent.pitch() : null;
	}
   
	public Event event() {
		return scoreParent != null ? scoreParent.event() : null;
	}
   
	public void paintBackground(Graphics g) {
		if (selections == null)
			return;
		
		Color oldColor = g.getColor();
		
		List<GraphicalSelection> globalSelections = getGlobalSelections();
		int alpha = 50 + 50 * globalSelections.size();
		for (int i = 0; i < globalSelections.size(); i++) {
			GraphicalSelection sel = globalSelections.get(i);
			if (sel.contains(this)) {
				g.setColor(new Color(sel.getColor().getRed(), 
									 sel.getColor().getGreen(),
									 sel.getColor().getBlue(),
									 alpha));
				g.fillRect(getLocation().x, getLocation().y, getSize().width, getSize().height);
			}
		}
			
		g.setColor(oldColor);
	}
	
	public void paint(Graphics g) {
		for (Iterator<CustomScoreObject> i = customScoreObjects.iterator(); i.hasNext();) {
			i.next().paint(g);
		}
	}

	public int lwidth() {
		return 0;
	}
   
	public int rwidth() {
		return 0;
	}
   
	/**
	 * This method returns how much a ScoreObject needs on it's right side
	 * in addition to its normal rwidth. (currently only used by Rest)
	 * @return
	 */
	public int extraRSpace() {
		return 0;
	}
	
	public int height() {
		return 0;
	}
   
	public int depth() {
		return 0;
	}

	/** Sets and changes	the current parent object. 
	 * This method also makes sure that only objects of the valid classes  
	 * are assigned (otherwise an exception is thrown). 
	 * @param  p the new parent object
	 * @throws IllegalArgumentException */
	public final void setParent(ScoreObject p) {
		if (parentClass().isAssignableFrom(p.getClass()))
			scoreParent = p;
		else
			throw new IllegalArgumentException(
				"\na "
					+ TextUtil.stripPackage(getClass())
					+ "'s parent must be of type "
					+ TextUtil.stripPackage(parentClass())
					+ " (tried to assign a "
					+ TextUtil.stripPackage(p.getClass())
					+ ")");
	}

	/** Changes the visibility of this object.
	 * @param v true, if object is visible */
	public void setVisible(boolean v) {
		visible = v;
	}

   /** Sets the relative horizontal position in pixel units to a given value. 
    * The position of a ScoreObject is related to its parent objects. */
	public final void setXPos(int x) {
		xpos = x;
	}
   
   /** Sets the relative vertical position in pixel units to a given value. 
   * The position of a ScoreObject is related to its parent objects. */
	public void setYPos(int y) {
		ypos = y;
	}
   
   /** Returns the current relative horizontal position in piuxel units. */
	public final int getXPos() {
		return xpos;
	}
   
   /** Returns the current relative vertical position in piuxel units. */
	public final int getYPos() {
		return ypos;
	}
   
   /** Increases the relative horizontal position by a given value. */
	public final void addToXPos(int dx) {
		xpos += dx;
	}
   
   /** Increases the relative vertical position by a given value. */
	public final void addToYPos(int dy) {
		ypos += dy;
	}
   
   /** Sets a new color value. The ScoreObject is not repainted automatically. */
    /* @@ this method was final... Why? */
	public void setColor(Color c) {
		color = c;
	}

   /** Returns true if the bounding box of this ScoreObject contains a given absolute
    * position. 
 * @param x 
 * @param y 
 * @param c 
 * @return */
	/*
	public boolean contains(int x, int y) {
		if (x > absX() - lwidth() &&
			x < absX() + rwidth() &&
			y > absY() - height() &&
			y < absY() + depth())
			return true;
		return false;
	}
	*/
	
	 /** Returns the ScoreObject o a given class at a given absolute position. 
	 * @param x 
	 * @param y 
	 * @param c 
	 * @return The ScoreObject */
	public ScoreObject catchScoreObject(int x, int y, Class<? extends ScoreObject> c) {
		if (c.isInstance(this) &&
			this.contains(x,y)) {
			return this;
		}
		
		return null;
	}

	/* Returns the bounding box of this ScoreObject. 
	public Box getBoundingBox() {
		int x = absX(), y = absY();
		return new Box(x - lwidth(), y - height(), x + rwidth(), y + depth());
	} */

	
	/** only for debugging purposes */
	void drawFrame(Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(Color.RED);
		g.drawRect(getLocation().x, getLocation().y, getSize().width, getSize().height);
		System.err.println("drawFrame: (" + this.getClass() + ") " + getLocation().x + " " + getLocation().y + " " + 
							getSize().width + " " + (getSize().height));
		g.fillOval(getLocation().x - 1, getLocation().y - 1, 2, 2);
		g.setColor(oldColor);
		
	}

	void movePropertiesTo(ScoreObject o) {
		o.scoreParent = scoreParent;
		o.xpos = xpos;
		o.ypos = ypos;
		o.scale = scale;
		o.color = color;
		o.visible = visible;
	}
	
   /** Returns a string representation of this ScoreObject. 
 * @return The String.*/
	@Override
	public String toString() {
		return TextUtil.stripPackage(getClass());
	}
	
	public List<CustomScoreObject> getCustomScoreObjects() {
		return customScoreObjects;
	}
	
	public void clearCustomScoreObjects() {
		customScoreObjects.clear();
	}
	
	public void addCustomScoreObject(CustomScoreObject so) {
		customScoreObjects.add(so);
		so.setMaster(this);
		so.setScoreParent(this);
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		for (Iterator<CustomScoreObject> iter = customScoreObjects.iterator(); iter.hasNext();) {
			ScoreObject element = iter.next();
			element.setScale(scale);
		}
	}
	
	public float getScale() {
		return scale;
	}

	private Font oldFont;
	protected void setScaledFont(Graphics g) {
		if (oldFont != null) //a scaled font is already in use
			return;
		if (scale == 1.0f)
			return;
		oldFont = g.getFont();
		g.setFont(oldFont.deriveFont(oldFont.getSize() * scale));
	}
	
	protected void restoreFont(Graphics g) {
		g.setFont(oldFont);
		oldFont = null;
	}
	
	/**
	 * this method returns the succossor of this object. E.g. for a LocalSim, the next LocalSim is returned.
	 * If there is no such successor, null is returned
	 * @return
	 */
	public ScoreObject getScoreSuccessor() {
		return getScoreSuccessor((ScoreContainer)this.getScoreParent());
	}
	
	public ScoreObject getScoreSuccessor(ScoreContainer cont) {
		if (cont == null)
			return null;
		for (int i = 0; i < cont.numChildren(); i++) {
			if (cont.child(i) == this) {
				if (i == (cont.numChildren() - 1)) { //we are the last object in our parent
					ScoreContainer succ = (ScoreContainer)cont.getScoreSuccessor();
					if (succ == null)
						return null;
					return succ.child(0);
				}
				else {
					return cont.child(i + 1);
				}
				
			}
		}
		return null;
	}
	
	ScoreObject getDirectPredecessor() {
		if (!(scoreParent instanceof ScoreContainer)) {
			throw new IllegalStateException("to get the predecessor the parent has to be a ScoreContainer");
		}
		int id = ((ScoreContainer)scoreParent).indexOf(this);
		if (id <= 0) {
			return null;
		}
		return ((ScoreContainer)scoreParent).child(id - 1);
	}
	
	class MouseAction extends MouseAdapter {
		ScoreObject parent;
		
		public MouseAction(ScoreObject parent) {
			this.parent = parent;
		}
		
		public void mouseClicked(MouseEvent e) {
			
		}
	}
	
	public Point getLocation() { // TODO check this !
	    Point point = super.getLocation();
	    point.x += getLeftPadding(); // does this change the position permanently ?
	    return point;
	}
	
//    /**
//     * 
//     * @see java.awt.Component#setLocation(int, int)
//     */
//    public void setLocation(int x, int y) {
//        super.setLocation(x+getLeftPadding(), y);
//    }
	
}
