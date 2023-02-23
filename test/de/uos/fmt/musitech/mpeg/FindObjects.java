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
 * Created on Jan 7, 2005
 *
 */
package de.uos.fmt.musitech.mpeg;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.score.NotationDisplay;

/**
 * @author collin
 *
 */
public class FindObjects extends JPanel implements ActionListener {
	JTextField searchField;
	Piece piece;
	JFrame mainWindow;
	BasicContainer markup = new BasicContainer();
	NotationDisplay display;
	
	public FindObjects(Piece piece, JFrame mainWindow) {
		super();
		this.piece = piece;
		this.mainWindow = mainWindow;
		
		setLayout(new BorderLayout());
		
		Box vBox = new Box(BoxLayout.Y_AXIS);
//		add(vBox, BorderLayout.SOUTH);
		
		Box hBox = new Box(BoxLayout.X_AXIS);
		
		searchField = new JTextField();
		vBox.add(searchField);

		vBox.add(hBox);
		
		JButton searchButton = new JButton();
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
		searchButton.setText("show");

		hBox.add(searchButton);
		
		JButton showButton = new JButton();
		showButton.setActionCommand("show");
		showButton.addActionListener(this);
		showButton.setText("id list");
		
		hBox.add(showButton);
		
		/*
		JButton clearButton = new JButton();
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);
		clearButton.setText("clear");
		
		hBox.add(clearButton);
		*/
				
		try {
			display = (NotationDisplay)EditorFactory.createDisplay(piece);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
//		add(display, BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane(display);
		scroll.setOpaque(false);
		add(scroll, BorderLayout.CENTER);
		
		long id = 0;
		Collection musicalObjects = piece.getContainerPool().getContentsRecursiveList(null);
		for (Iterator iter = musicalObjects.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof MObject) {
				MObject musicalObject = (MObject)element;
				musicalObject.setUid(new Long(id++));
			}
		}
		
		add(createButtonComp(), BorderLayout.SOUTH);
		
	}
	
	private JComponent createButtonComp(){
	    Box box = Box.createHorizontalBox();
	    box.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
	    Collection musicalObjects = piece.getContainerPool().getContentsRecursiveList(null);
	    box.add(Box.createHorizontalStrut(50));
	    JLabel label = new JLabel("ID of the note to find: ");
	    box.add(label);
	    box.add(Box.createHorizontalStrut(50));    
	    final JComboBox comboBox = new JComboBox(returnOptions());
	    comboBox.setEditable(true);
	    comboBox.addActionListener(new ActionListener(){
            @Override
			public void actionPerformed(ActionEvent e) {
                selectID((String)comboBox.getSelectedItem());
            }});
	    box.add(comboBox);
	    box.add(Box.createHorizontalStrut(50));
	    JButton searchButton = new JButton("Search");
	    searchButton.addActionListener(new ActionListener(){
            @Override
			public void actionPerformed(ActionEvent e) {
                markUpSelected();
            }});
	    box.add(searchButton);
	    box.add(Box.createHorizontalGlue());
	    return box;
	}
	
	private String[] returnOptions(){
	    Collection UIDs = new ArrayList();
	    Collection contents = piece.getContainerPool().getContentsRecursiveList(null);
	    for (Iterator iter = contents.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof Note){
                if (((Note)element).getUid()!=null)
                    UIDs.add(((Note)element).getUid());
            }
        }
//	    Long[] IDs = new Long[UIDs.size()];
	    String[] options = new String[UIDs.size()+1];
	    options[0] = "";
	    int i=1;
	    for (Iterator iter = UIDs.iterator(); iter.hasNext();) {
            Long element = (Long) iter.next();
            options[i++] = element.toString();
        }
	    return options;
	}
	
	private void selectID(String selectedID){
	    markup.clear();
	    Long id;
	    try {
			id = new Long(selectedID);
		} catch (NumberFormatException ex) {
			return;
		}
	    MObject found = null;
		Collection musicalObjects = piece.getContainerPool().getContentsRecursiveList(null);
		for (Iterator iter = musicalObjects.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof MObject) {
				MObject musicalObject = (MObject)element;
				if (musicalObject.getUid().equals(id)) {
					found = musicalObject;
					break;
				}
			}
		}
		if (found!=null && found instanceof Note)
		    markup.add(found);
	}
	
	private void markUpSelected(){   
	    display.removeMarkup(markup);
	    if (markup.size()>0)
	        display.addMarkup(markup);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("search".equals(command)) {
			Long id;
			try {
				id = new Long(searchField.getText());
			} catch (NumberFormatException ex) {
				return;
			}
			
			MObject found = null;
			Collection musicalObjects = piece.getContainerPool().getContentsRecursiveList(null);
			for (Iterator iter = musicalObjects.iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (element instanceof MObject) {
					MObject musicalObject = (MObject)element;
					if (musicalObject.getUid().equals(id)) {
						found = musicalObject;
						break;
					}
				}
			}
			display.removeMarkup(markup);
			if (found != null &&
				found instanceof Note) {
				markup = new BasicContainer();
				markup.add(found);
				display.addMarkup(markup);
			}
		}
		else if ("show".equals(command)) {
			String list = "<html><ul>";
			
			Collection musicalObjects = piece.getContainerPool().getContentsRecursiveList(null);
			for (Iterator iter = musicalObjects.iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (element instanceof MObject) {
					MObject musicalObject = (MObject)element;
					list += "<li>" + musicalObject.getUid();
				}
			}
			list += "</ul>";
			JDialog dialog = new JDialog(mainWindow, "ids", false);
			JLabel label = new JLabel();
			label.setText(list);
			dialog.getContentPane().add(new JScrollPane(label));
			dialog.pack();
			dialog.setSize(100, 300);
			dialog.setVisible(true);
		}
		else if ("clear".equals(command)) {
			display.removeMarkup(markup);
			markup = new BasicContainer();
		}
	}
}
