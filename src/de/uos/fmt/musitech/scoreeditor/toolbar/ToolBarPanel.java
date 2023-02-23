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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import de.uos.fmt.musitech.framework.change.DataChangeEvent;
import de.uos.fmt.musitech.framework.change.DataChangeManager;
import de.uos.fmt.musitech.score.ScoreEditor;
import de.uos.fmt.musitech.scoreeditor.icons.Icons;

/**
 * @author Sascha Wegener
 * 
 * @version 27.06.2007
 */
public class ToolBarPanel extends JPanel implements SectionController {

	private JToolBar upper, center, lower;

	private ScoreEditor ed;

	private static final Dimension SEPERATOR_SIZE = new Dimension(10, 10);

	private List<AbstractToolbarSection> sections;

	public ToolBarPanel(ScoreEditor ed) {
		this.ed = ed;
		sections = new ArrayList<AbstractToolbarSection>();
		setLayout(new GridLayout(3, 1));
		createUpperToolbar();
		createCenterToolbar();
		createLowerToolbar();
		add(upper);
		add(center);
		add(lower);
		DataChangeManager.getInstance().interestExpandObject(this,
				getScoreEditor().getNotationSystem());
	}

	@Override
	public void update() {
		for (AbstractToolbarSection ats : sections)
			ats.update();
	}

	@Override
	public ScoreEditor getScoreEditor() {
		return ed;
	}

	private void createLowerToolbar() {
		lower = new JToolBar() {
			@Override
			public void addSeparator() {
				super.addSeparator(SEPERATOR_SIZE);
			}

			@Override
			public JButton add(Action a) {
				JButton b = super.add(a);
				b.setToolTipText(a.getValue(Action.NAME).toString());
				return b;
			}
		};
		lower.setRollover(true);
		lower.setFloatable(false);

		AbstractToolbarSection systems = new SystemSection(this);
		sections.add(systems);
		systems.addItems(lower);

		lower.addSeparator();

		AbstractToolbarSection clefs = new ClefSection(this);
		sections.add(clefs);
		clefs.addItems(lower);

		lower.addSeparator();

		AbstractToolbarSection keys = new KeySignatureSection(this);
		sections.add(keys);
		keys.addItems(lower);

		lower.add(new JPanel());
		lower.add(new JPanel());
	}

	private void createCenterToolbar() {
		center = new JToolBar() {
			@Override
			public void addSeparator() {
				super.addSeparator(SEPERATOR_SIZE);
			}

			@Override
			public JButton add(Action a) {
				JButton b = super.add(a);
				b.setToolTipText(a.getValue(Action.NAME).toString());
				return b;
			}
		};
		center.setRollover(true);
		center.setFloatable(false);

		center.add(new AbstractAction(Messages.getString("ToolBarPanel.DeleteSelection"), Icons //$NON-NLS-1$
				.getIcon("radierer.png")) { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent e) {
				ed.getScoreManipulator().deleteSelection();
			}
		});

		center.addSeparator();

		AbstractToolbarSection alterations = new AlterationSection(this);
		sections.add(alterations);
		alterations.addItems(center);

		center.addSeparator();

		AbstractToolbarSection accents = new AccentSection(this);
		sections.add(accents);
		accents.addItems(center);
		
		center.addSeparator();
		
		AbstractToolbarSection ties = new TieSection(this);
		sections.add(ties);
		ties.addItems(center);

		center.add(new JPanel());
		center.add(new JPanel());

	}

	private void createUpperToolbar() {
		upper = new JToolBar() {
			@Override
			public void addSeparator() {
				super.addSeparator(SEPERATOR_SIZE);
			}

			@Override
			public JButton add(Action a) {
				JButton b = super.add(a);
				b.setToolTipText(a.getValue(Action.NAME).toString());
				return b;
			}
		};
		upper.setRollover(true);
		upper.setFloatable(false);

//		AbstractToolbarSection file = new FileSection(this);
//		sections.add(file);
//		file.addItems(upper);
//
//		upper.addSeparator();

		AbstractToolbarSection modus = new ModeSection(this);
		sections.add(modus);
		modus.addItems(upper);

		upper.addSeparator();

		AbstractToolbarSection raster = new GridSection(this);
		sections.add(raster);
		raster.addItems(upper);

		upper.addSeparator();

		AbstractToolbarSection length = new NoteLengthSection(this);
		sections.add(length);
		length.addItems(upper);

		upper.addSeparator();

		AbstractToolbarSection player = new PlayerSection(this);
		sections.add(player);
		player.addItems(upper);

		upper.add(new JPanel());
		upper.add(new JPanel());
	}

	@Override
	public void dataChanged(DataChangeEvent e) {
		update();
	}
}