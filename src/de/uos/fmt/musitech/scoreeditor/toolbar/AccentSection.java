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
package de.uos.fmt.musitech.scoreeditor.toolbar;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;

import de.uos.fmt.musitech.score.gui.Accent;
import de.uos.fmt.musitech.scoreeditor.icons.Icons;


/**
 * @author Sascha Wegener
 * 
 * @version 30.08.2007
 */
public class AccentSection extends AbstractToolbarSection {

	public AccentSection(SectionController toolBarPanel) {
		super(toolBarPanel);
	}

	@Override
	public void addItems(JToolBar center) {
		center
				.add(new AbstractAction("Staccato", Icons //$NON-NLS-1$
						.getIcon("staccato.png")) { //$NON-NLS-1$
					@Override
					public void actionPerformed(ActionEvent e) {
						getScoreEditor().getScoreManipulator()
								.setAccentOnSelection(Accent.STACCATO);
					}
				});

		center
				.add(new AbstractAction("Marcarto", Icons //$NON-NLS-1$
						.getIcon("marcato.png")) { //$NON-NLS-1$
					@Override
					public void actionPerformed(ActionEvent e) {
						getScoreEditor().getScoreManipulator()
								.setAccentOnSelection(Accent.MARCATO);
					}
				});

		center.add(new AbstractAction("Portato", Icons.getIcon("portato.png")) { //$NON-NLS-1$ //$NON-NLS-2$
			@Override
			public void actionPerformed(ActionEvent e) {
				getScoreEditor().getScoreManipulator().setAccentOnSelection(
						Accent.PORTATO);
			}
		});

		center.add(new AbstractAction(Messages.getString("AccentSection.Trill"), Icons.getIcon("triller.png")) { //$NON-NLS-1$ //$NON-NLS-2$
			@Override
			public void actionPerformed(ActionEvent e) {
				getScoreEditor().getScoreManipulator().setAccentOnSelection(
						Accent.TRILL);
			}
		});

		center.add(new AbstractAction(Messages.getString("AccentSection.UpperMordent"), Icons //$NON-NLS-1$
				.getIcon("mordent1.png")) { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent e) {
				getScoreEditor().getScoreManipulator().setAccentOnSelection(
						Accent.MORDENT_UP);
			}
		});
		center
				.add(new AbstractAction(Messages.getString("AccentSection.LowerMordent"), Icons //$NON-NLS-1$
						.getIcon("mordent2.png")) { //$NON-NLS-1$
					@Override
					public void actionPerformed(ActionEvent e) {
						getScoreEditor().getScoreManipulator()
								.setAccentOnSelection(Accent.MORDENT_DOWN);
					}
				});
		center.add(new AbstractAction(Messages.getString("AccentSection.Turn"), Icons.getIcon("turn.png")) { //$NON-NLS-1$ //$NON-NLS-2$
			@Override
			public void actionPerformed(ActionEvent e) {
				getScoreEditor().getScoreManipulator().setAccentOnSelection(
						Accent.TURN);
			}
		});
	}

	@Override
	protected void createItems() {
	}

}
