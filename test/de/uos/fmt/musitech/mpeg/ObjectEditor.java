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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.metadata.MetaDataCollection;
import de.uos.fmt.musitech.data.rendering.RenderingHints;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.metadata.MetaDataEditor;
import de.uos.fmt.musitech.score.NotationDisplay;
import de.uos.fmt.musitech.score.gui.Pitch;
import de.uos.fmt.musitech.score.gui.Score;

/**
 * @author collin
 *
 */
public class ObjectEditor extends JPanel implements ActionListener {
	Piece piece;
	Score score;
	HashMap map = new HashMap();
	JFrame frame;
	JList list;
	
	private Object selectedObj;
	private Container markUp = new BasicContainer();
	private JButton showButton = new JButton();
	private NotationDisplay display = null;
	private JDialog openDialog;
	
	public ObjectEditor(JFrame frame, Piece piece) {
		super();
		this.piece = piece;
		this.frame = frame;
		
		setLayout(new BorderLayout());
		
//		NotationDisplay display = null;
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
		
		score = display.getScorePanel().getScore();
		
//		display.getScorePanel().addMouseListener(new ScoreObjectCatcher());
		ScoreObjectCatcher listener = new ScoreObjectCatcher();
		listener.setNotationDisplay(display);
		display.getScorePanel().addMouseListener(listener);
		display.getScorePanel().setSelectionColorsMulti(false);
		
//		Box vBox = new Box(BoxLayout.Y_AXIS);
//		add(vBox, BorderLayout.EAST);
		
		Vector objects = new Vector();
		objects.add(piece);
		map.put(piece, "Piece");
		int staffCounter = 1;
		NotationSystem system = piece.getScore();
		if (system == null) {
			system = display.getScorePanel().getNotationSystem();
		}
		for (Iterator iter = system.iterator(); iter.hasNext();) {
			NotationStaff element = (NotationStaff) iter.next();
			objects.add(element);
			map.put(element, "  Staff " + staffCounter++);
			int voiceCounter = 1;
			for (Iterator iterator = element.iterator(); iterator.hasNext();) {
				NotationVoice element2 = (NotationVoice) iterator.next();
				objects.add(element2);
				map.put(element2, "    Voice " + voiceCounter++);
			}
		}
		
		list = new JList(objects);
		list.setCellRenderer(new NotationCellRenderer());
		//Erg. K.N. 11.01.05
		list.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e) {
                clearMarkUp(display);
                setSelectedObj(list.getSelectedValue());
//                updateMarkUp();
            }
		});
		Border out = BorderFactory.createLineBorder(Color.BLACK);
		Border in = BorderFactory.createEmptyBorder(10,10,10,10);		
		list.setBorder(BorderFactory.createCompoundBorder(out, in));
		list.setToolTipText("Please select a structure to open its metadata.");
		Box listBox = Box.createVerticalBox();
		listBox.setBorder(BorderFactory.createEmptyBorder(0,50,0,30));
		listBox.add(Box.createVerticalGlue());		
		listBox.add(list);
		listBox.add(Box.createVerticalGlue());
		add(listBox, BorderLayout.EAST);
		//Ende Erg
//		vBox.add(list);
//		JButton editButton = new JButton();
//		editButton.setActionCommand("edit");
//		editButton.addActionListener(this);
//		editButton.setText("edit");		
//		vBox.add(editButton);
		
		//K.N. 11.01.05
		showButton.setText("Open Metadata");
		showButton.setToolTipText("Shows the metadata for the selected note or structure.");
		showButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                openMetaData();
            }});
		showButton.setEnabled(false);
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(showButton);
		buttonBox.add(Box.createHorizontalGlue());
		add(buttonBox, BorderLayout.SOUTH);
		//Ende K.N. 11.01.05
	}
	
	public void setSelectedObj(Object selectedObj){
	    this.selectedObj = selectedObj;
	    if (selectedObj!=null){
	        showButton.setEnabled(true);
	    } else {
	        showButton.setEnabled(false);
	    }
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		
		if ("edit".equals(command)) {
			MetaDataCollection mdc = (MetaDataCollection)piece.getMetaMap().get(list.getSelectedValue());
			mdc = showMetaDataEditor(mdc);
			piece.getMetaMap().put((MObject)list.getSelectedValue(), mdc);
		}
	}
	
	private void openMetaData(){
	    //get MetaDataCollection for selectedObj
	    MetaDataCollection mdc = (MetaDataCollection)piece.getMetaMap().get(selectedObj);
	    //display metadata
	    mdc = showMetaDataEditor(mdc);
		piece.getMetaMap().put((MObject)selectedObj, mdc);
	}
	
	MetaDataCollection showMetaDataEditor(MetaDataCollection mdc) {
		if (mdc == null) {
			mdc = new MetaDataCollection();
		}
		
		MetaDataEditor editor = null;
		try {
			editor = (MetaDataEditor)EditorFactory.createEditor(mdc, false);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (openDialog!=null)
		    openDialog.dispose();
		JDialog dialog = new JDialog(frame, "meta data", false);
		dialog.getContentPane().add(editor);
//		dialog.pack();
		dialog.setSize(600,300);
		dialog.setVisible(true);
		openDialog = dialog;
		return mdc;
	}
	
	public void clearMarkUp(Object obj){
	    if (obj==display){
	        display.removeMarkup(markUp);
	        markUp = new BasicContainer();
	    }
	    if (obj==list){
	        list.clearSelection();
	    }
	}
	
	private void updateMarkUp(){
	    markUp = new BasicContainer();
	    if (selectedObj instanceof Piece){
	        if (((Piece)selectedObj).getScore()==null){
	                piece.setScore(NotationDisplay.createNotationSystem(piece));
	        }
	  	    markUp.add(((Piece)selectedObj).getScore());    
	  	    
	    } else {
	        if (selectedObj instanceof Container){
//	            markUp.add((Container)selectedObj);
//	            Collection contents = ((Container)selectedObj).getContentsRecursiveList(null);
//	            markUp.addAll(contents);
	            markUp = (Container)selectedObj;
	            /*RenderingHints rh = ((Container)selectedObj).getRenderingHints();
                String str = (String)((Container)selectedObj).getRenderingHint("color");
                if (str!=null){
                    Collection contents = ((Container)selectedObj).getContentsRecursiveList(null);
                    for (Iterator iter = contents.iterator(); iter.hasNext();) {
                        Object element = (Object) iter.next();
                        if (element instanceof Container){
                            ((Container)element).addRenderingHint("color", str);
                        }
                    }
                }*/
	        }
	    }
	    display.removeMarkup(markUp);
	    display.addMarkup(markUp);
	}
	
	class NotationCellRenderer extends JLabel implements ListCellRenderer {
			public Component getListCellRendererComponent(JList list, Object value,
														  int index, boolean isSelected, boolean hasFocus) {
				setText((String)map.get(value));
		        if (isSelected) {
		        	setBackground(list.getSelectionBackground());
		            setForeground(list.getSelectionForeground());
		        }
		        else {
		            setBackground(list.getBackground());
		            setForeground(list.getForeground());
		        }
		        setEnabled(list.isEnabled());
		        setFont(list.getFont());
		        setOpaque(true);
				return this;
			}
	}

	class ScoreObjectCatcher extends MouseAdapter {
	    
	    NotationDisplay notationDisplay;
//	    Container markUp = new BasicContainer();
	    
	    public void setNotationDisplay(NotationDisplay display){
	        this.notationDisplay = display;
	    }
	    
	    public NotationDisplay getNotationDisplay(){
	        return notationDisplay;
	    }
		
		public void mouseClicked(MouseEvent event) {
			Pitch pitch = (Pitch)score.catchScoreObject(event.getX(), event.getY(), Pitch.class);
			if (pitch!=null && pitch.getNote()!=null){
			    if (notationDisplay!=null){
			        notationDisplay.removeMarkup(markUp);
//			        markUp = new BasicContainer();
			        markUp = new BasicContainer();
			        clearMarkUp(list);
			        markUp.add(pitch.getNote());
			        notationDisplay.addMarkup(markUp);
			        setSelectedObj(pitch.getNote());
			        BasicContainer mark = new BasicContainer();
			    }
			}    
//			if (pitch != null && pitch.getNote() != null) {   
//				MetaDataCollection mdc = (MetaDataCollection)piece.getMetaMap().get(pitch.getNote());
//				mdc = showMetaDataEditor(mdc);
//				piece.getMetaMap().put(pitch.getNote(), mdc);
//			}
		}
	}
}
