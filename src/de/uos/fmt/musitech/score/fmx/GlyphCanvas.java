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
package de.uos.fmt.musitech.score.fmx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import javax.swing.JComponent;

/** This class is used to show a graphics area showing a glyph of
 * a given font and its bounding box. This box consists of height, depth 
 * and width.
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class GlyphCanvas extends JComponent implements Serializable
{
	private char glyph;                        // current glyph
	private Font font;                         // current font
	private int height;
	private int depth;
	private int width;
	private int defHeight;
	private int defDepth;
	private int defWidth;
	private int xOffset = 0;
	private boolean updateFont = true;
	private boolean updateDefaultMetrics = true;
	private boolean modified   = false;
	private GlyphCanvasOwner owner;

	public GlyphCanvas () 
	{
 		//setPreferredSize(new Dimension(300,300));
		setBackground(Color.white);
	}
	
	/** Constructs a GlyphCanvas. 
	 * @param owner Container object that owns this component.
	 * @param f     Font that will be used to display the glyphs.
	 * @param initialGlyph the initially displayed glyph */
	public GlyphCanvas (GlyphCanvasOwner owner, Font f, char initialGlyph)
	{
	   this.owner = owner;
	   glyph = (initialGlyph > 0) ? initialGlyph : 'a';
	   font = f;
 		//setPreferredSize(new Dimension(300,300));
		setBackground(Color.white);
		assignSystemMetrics();
	}
	
	/** Changes the displayed glyph. */
	public void setGlyph (char glyph)	
	{
	   this.glyph = glyph;
	   updateDefaultMetrics = true;
	   modified = false;
  	   repaint();
	}
	
	/** Returns the currently displayed glyph. */
	public char getGlyph ()			
	{
	   return glyph;
	}
	
	/** Changes the used font. */
	@Override
	public void setFont (Font f)	
	{
	   font = f;
	   repaint();
	   modified = true;
	}
	
	/** Scales the current font size by a given factor. */
	public void scaleFontSize (double factor)
	{
	   float size = font.getSize();
	   font = font.deriveFont(size*(float)factor);
	   setMetrics((int)Math.round(factor*height), 
	   			  (int)Math.round(factor*depth), 
	              (int)Math.round(factor*width));
	   repaint();
	}
	
	/** Returns the currently used font. */
	@Override
	public Font getFont ()			
	{
	   return font;
	}
	
	
/*	public Dimension getPreferredSize () 
	{
	   return new Dimension(300,300);
	}*/
	
	@Override
	public void paint (Graphics g)
	{
	   super.paint(g);
	   if (updateFont)
	   	g.setFont(font);
	   if (updateDefaultMetrics)
	   {
	   	Graphics2D graphics = (Graphics2D)getGraphics();
	   	String s = ""+glyph;
	   	char[] c = {glyph};
	   	java.awt.FontMetrics fm = graphics.getFontMetrics();
	   	GlyphVector gv = font.createGlyphVector(graphics.getFontRenderContext(), c);
	   	Rectangle2D r = gv.getGlyphMetrics(0).getBounds2D();
	   	defDepth  = fm.getDescent();
	   	defHeight = (int)r.getHeight();
	   	defHeight -= depth;
	   	defWidth  = fm.charWidth(glyph);
			updateDefaultMetrics = false;
			owner.glyphCanvasChangedDefaults();
	   }
		if (updateFont)
		{
  	   	height = defHeight;
			depth = defDepth;
			width = defWidth;
			updateFont = false;
		}

	   int ybase = 250;
	   int x = 50;
		g.drawString(""+glyph, x-xOffset, ybase);
		g.setColor(Color.red);
		g.drawRect(x, ybase-height, width, height);
		g.drawRect(x, ybase, width, depth);
	}

	/** Assigns the default font metrics to the currently shown bounding boxes. */	
	public void assignSystemMetrics ()
	{
		height = defHeight;
		depth = defDepth;
		width = defWidth;
		repaint();   
	}
	
   /** Gets the height of the current glyph. */
   public int getGlyphHeight()  {return height;}

   /** Sets the height of the current shapet.
    * @param height the height to set */
   public void setGlyphHeight(int height) 
   {
      this.height = height;
      modified = true;
      repaint();
   }

   /** Gets the width of the current glyph. */
   public int getGlyphWidth()  {return width;}

   /** Sets the width of the current glyph.
    * @param width the width to set */
   public void setGlyphWidth(int width)  
   {
      this.width = width;
      modified = true;
      repaint();
   }

   /** Gets the depth of the current glyph. */
   public int getGlyphDepth()  {return depth;}

   /** Sets the depth of the current glyph.
    * @param depth the depth to set */
   public void setGlyphDepth(int depth) 
   {
      this.depth = depth;
      modified = true;      
      repaint();
   }

   /** Gets the xOffset. */
   public int getXOffset() {return xOffset;}

	public void setMetrics (int height, int depth, int width)
	{
	   this.height = height;
		this.depth  = depth;
		this.width  = width;   
		modified = true;
		repaint();
	}
	
   /** Sets the xOffset. */
   public void setXOffset(int xOffset)
   {
      this.xOffset = xOffset;
      modified = true;
      repaint();
   }

   /** Returns true if the glyph metrics have been modified. */
   public boolean isModified() {return modified;}

   /** Gets the default depth that is delivered by the Java system. */
   public int getDefaultDepth() {return defDepth;}

   /** Gets the default height that is delivered by the Java system. */
   public int getDefaultHeight() {return defHeight;}

   /** Gets the default width that is delivered by the Java system. */
   public int getDefaultWidth() {return defWidth;}
}
