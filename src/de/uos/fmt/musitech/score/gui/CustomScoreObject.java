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
 * Created on Oct 30, 2004
 *
 * This is the base for all custom objects which are to be rendered. Objects are mainly
 * constructed through the static factory method from this class, which maps a MetricAttachable
 * to a CustomScoreObject. Which subclass of CustomScoreObject is to be used is determined by
 * the symbol of MetricAttachable 
 * 
 */
package de.uos.fmt.musitech.score.gui;

import java.awt.Font;
import javax.swing.JComponent;
import de.uos.fmt.musitech.data.score.Attachable;
import de.uos.fmt.musitech.data.score.CharSymbol;
import de.uos.fmt.musitech.data.score.DualMetricAttachable;
import de.uos.fmt.musitech.data.score.MetricAttachable;
import de.uos.fmt.musitech.data.score.SVGSymbol;
import de.uos.fmt.musitech.data.score.StackSymbol;
import de.uos.fmt.musitech.data.score.StringSymbol;
import de.uos.fmt.musitech.data.structure.harmony.ChordSymbol;
import de.uos.fmt.musitech.structure.harmony.gui.ChordSymbolDisplay2;

/*
*/

/**
 * @author collin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CustomScoreObject extends ScoreObject {
	ScoreObject master;
	ScoreObject anker;
	MetricAttachable attachable;
	
	public CustomScoreObject(ScoreObject master) {
		this.master = master;
	}

	@Override
	int arrange(int pass) {
		if (pass == 2) {
			Measure m = (Measure)master;
			int distance = attachable.getDistance() * m.staff().getLineDistance();
			if (anker != null) {
				int x = anker.absX();
				if (attachable.getAlignment() == MetricAttachable.CENTER) {
					x += anker.rwidth() / 2;
				}
				setXPos(x);
			}
			else {
				setXPos(m.getXForMetricTime(attachable.getMetricTime()));
			}
			if (attachable.getRelativePosition() == MetricAttachable.SOUTH) {
				int y, locY;
				if (anker != null) {
					y = anker.getLocation().y + anker.getSize().height;
					locY = y;
					y = Math.max(master.staff().getBottomLineY(), y);
				}
				else {
					y = master.getLocation().y + master.getSize().height;
					locY = master.getLocation().y + master.getSize().height;
				}
				setYPos(y + distance);
				setLocation(master.getLocation().x, locY + distance);
			}
			else if (attachable.getRelativePosition() == MetricAttachable.NORTH) {
				int y, locY;
				
				if (anker != null) {
					y = anker.getLocation().y;
					locY = y;
					
					y = Math.min(master.staff().getTopLineY(), y);
				}
				else {
					y = master.getLocation().y;
					locY = y;
				}
				setYPos(y - distance);
				setLocation(master.getLocation().x, locY - distance);
			}
			setSize(lwidth() + rwidth(), depth() + height());
		}
		return 3;
	}
	
	public static CustomScoreObject createCustomScoreObject(ScoreObject master, MetricAttachable ma) {
		return createCustomScoreObject(master, null, ma);
	}
	
	public static CustomDualScoreObject createCustomDualScoreObject(ScoreObject master, ScoreObject anker, ScoreMapper scoreMapper, DualMetricAttachable dma) {
		CustomDualScoreObject cdso = new CustomDualScoreObject(master, dma);
		if (anker != null)
			cdso.setAnker(anker);
		cdso.setScoreMapper(scoreMapper);
		
		return cdso;
	}
	
	public static CustomScoreObject createCustomScoreObject(ScoreObject master, ScoreObject anker, Attachable attachable) {
		if (attachable.getSymbol() instanceof SVGSymbol) {
			CustomScoreObject cso = new CustomAttachableScoreObject(master, (SVGSymbol)attachable.getSymbol(), attachable);
			cso.setAnker(anker);
			return cso;
		}
		else {
			throw new IllegalArgumentException(attachable.getSymbol().getClass() + " is not a supported symbol for attachables");
		}
	}
	
	public static CustomScoreObject createCustomScoreObject(ScoreObject master, ScoreObject anker, MetricAttachable ma) {
		CustomScoreObject cso = null;
		if (ma.getSymbol() instanceof CharSymbol) {
			if (((CharSymbol)ma.getSymbol()).isParanthesised()) {
				cso = new CustomMultiFontScoreObject(
								master, 
								new String[]{"(", ((CharSymbol)ma.getSymbol()).getSymbol() + "", ")"},
								new Font[]{CustomMultiFontScoreObject.DEFAULT_FONT, null, CustomMultiFontScoreObject.DEFAULT_FONT},
								ma);
			}
			else {
				cso = new CustomCharScoreObject(master, (CharSymbol)ma.getSymbol(), ma);
			}
		}
		else if (ma.getSymbol() instanceof StringSymbol) {
			cso = new CustomStringScoreObject(master, (StringSymbol)ma.getSymbol(), ma);
		}
		else if (ma.getSymbol() instanceof StackSymbol) {
			cso = new CustomStackScoreObject(master, (StackSymbol)ma.getSymbol(), ma);
		}
		else if (ma.getSymbol() instanceof SVGSymbol) {
			cso = new CustomSVGScoreObject(master, (SVGSymbol)ma.getSymbol(), ma);
		}
		else if (ma.getSymbol() instanceof ChordSymbol) {
			ChordSymbolDisplay2 symbolDisplay = new ChordSymbolDisplay2();
			Object o = ma.getSymbol();
			symbolDisplay.init(ma.getSymbol(), null, null);
			JComponent jcomp = symbolDisplay;
			cso = new CustomComponentScoreObject(master, jcomp, ma);
		}
		else {
			throw new IllegalArgumentException("cannot handle " + ma.getSymbol().getClass() + "(yet)");
		}
		
		if (cso != null &&
			anker != null)
			cso.setAnker(anker);
		return cso;
	}
	
	@Override
	public int absX() {
		return getXPos();
	}
	
	@Override
	public int absY() {
		return getYPos();
	}
	
	public CustomScoreObject(ScoreObject master, MetricAttachable ma) {
		this(master);
		attachable = ma;
	}
	
	public void setMaster(ScoreObject so) {
		this.master = so;
	}
	
	
	/* (non-Javadoc)
	 * @see de.uos.fmt.musitech.score.gui.ScoreObject#parentClass()
	 */
	@Override
	Class parentClass() {
		return null;
	}

	
	public MetricAttachable getAttachable() {
		return attachable;
	}
	public void setAttachable(MetricAttachable attachable) {
		this.attachable = attachable;
	}
	
	public ScoreObject getAnker() {
		return anker;
	}
	public void setAnker(ScoreObject anker) {
		this.anker = anker;
	}
	public ScoreObject getMaster() {
		return master;
	}
}
