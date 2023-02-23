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
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import de.uos.fmt.musitech.scoreeditor.icons.Icons;
import de.uos.fmt.musitech.utility.math.Rational;

/**
 * @author Sascha Wegener
 * 
 * @version 30.08.2007
 */
public class KeySignatureSection extends AbstractToolbarSection {

	public KeySignatureSection(SectionController toolBarPanel) {
		super(toolBarPanel);
	}

	@Override
	public void addItems(JToolBar tb) {
		final JButton keybutton = new JButton(Icons.getIcon("b.png")); //$NON-NLS-1$
		String[] keys = { Messages.getString("KeySignatureSection.GfEf"), Messages.getString("KeySignatureSection.DfBf"), Messages.getString("KeySignatureSection.AfF"), Messages.getString("KeySignatureSection.EfC"), Messages.getString("KeySignatureSection.BfG"), Messages.getString("KeySignatureSection.FD"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				Messages.getString("KeySignatureSection.CA"), Messages.getString("KeySignatureSection.GE"), Messages.getString("KeySignatureSection.DB"), Messages.getString("KeySignatureSection.AFs"), Messages.getString("KeySignatureSection.ECs"), Messages.getString("KeySignatureSection.BGs"), Messages.getString("KeySignatureSection.FsDs") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		final JPopupMenu keypop = new JPopupMenu();
		for (int i = 0; i < keys.length; i++) {
			final int key = i - 6;
			JMenuItem jmi = new JMenuItem(keys[i]);
			jmi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getScoreEditor().getScoreManipulator().setKey(key);
				}
			});
			keypop.add(jmi);
		}
		keybutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keypop.show(keybutton, 0, 0);
			}
		});
		tb.add(keybutton);

		final JButton timebutton = new JButton(Icons.getIcon("time.png")); //$NON-NLS-1$
		String[] times = { "3/4", "4/4", "6/8" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		final Rational[] timesR = { new Rational(3, 4), new Rational(4, 4),
				new Rational(6, 8) };
		final JPopupMenu timepop = new JPopupMenu();
		for (int i = 0; i < times.length; i++) {
			final int index = i;
			JMenuItem jmi = new JMenuItem(times[i]);
			jmi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getScoreEditor().getScoreManipulator().setTimeSignature(
							timesR[index]);
				}
			});
			timepop.add(jmi);
		}
		timebutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timepop.show(timebutton, 0, 0);
			}
		});
		tb.add(timebutton);
	}

	@Override
	protected void createItems() {
	}

}
