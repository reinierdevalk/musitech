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
 * File: HorizontalTimedLayout
 * Created on 22.04.2003
 */
package de.uos.fmt.musitech.time.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import de.uos.fmt.musitech.data.structure.lyrics.LyricsSyllable;
import de.uos.fmt.musitech.structure.text.LyricsSyllableDisplay;
import de.uos.fmt.musitech.utility.general.WrongArgumentException;

/**
 * A HorizontalTimedLayout is a layout manager that manages 
 * HorizontalTimedLayouts.  
 * @author Tillman Weyde, Felix Kugel
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class HorizontalTimedLayout implements LayoutManager {

	private double minScale;

	static int MIN_DISTANCE = 5;

	/**
	 * 
	 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
	 */
	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
		// XXX Auto-generated method stub
		System.out.println("" + arg1 + " added to LM");

	}

	/* (non-Javadoc)
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	@Override
	public void removeLayoutComponent(Component arg0) {
		// XXX Auto-generated method stub

	}

	/**
	 * Get the preferred size
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension preferredLayoutSize(Container arg0) {
		// TODO implement 
		return null;
	}

	/**
	 * Return the minimal size of this layout.
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension minimumLayoutSize(Container arg0) {
		// TODO implement this
		return null;
	}

	/* (non-Javadoc)
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 */
	@Override
	public void layoutContainer(Container arg0) {

		if (!(arg0 instanceof HorizontalTimedDisplay))
			return;

		Component[] components = arg0.getComponents();
		HorizontalTimedDisplay htd = (HorizontalTimedDisplay) arg0;

		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof LyricsSyllableDisplay) {
				LyricsSyllableDisplay lsd =
					(LyricsSyllableDisplay) components[i];

				Dimension size = lsd.getPreferredSize();

				try {

//					int x =
//						htd.getMinimalPositionForTime(
//							lsd.getElement().getTime(),
//							null);
					int x = htd.getMinimalPositionForTime(
						((LyricsSyllable)lsd.getEditObj()).getTime(),
						null);

					lsd.setBounds(x, 1, size.width, size.height);

				} catch (WrongArgumentException e) {
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * Sets the minimal scale in pixels per millisecond.
	 * @param scale
	 */
	public void setMinScale(double scale) {
		minScale = scale;
	}

}
