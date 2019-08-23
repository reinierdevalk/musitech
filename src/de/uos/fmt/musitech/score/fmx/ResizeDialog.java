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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 * @author Martin Gieseking
 */
public class ResizeDialog extends JDialog
{
   private JTextField sizeTextField = new JTextField("0");
   private JButton okButton = new JButton("OK");
   private JButton cancelButton = new JButton("Cancel");
   private boolean ok = false;
   private int enteredSize = 0;
   
   public ResizeDialog (Frame owner)
   {
      super(owner, "Enter new size", true);      
		okButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent ev) {okButtonPressed();}
		});
		cancelButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent ev) {dispose();}
		});
			
      JPanel upperPanel = new JPanel();      
      upperPanel.setLayout(new BorderLayout());
      upperPanel.add(new JLabel("New size:"), BorderLayout.WEST);
      upperPanel.add(sizeTextField, BorderLayout.CENTER);
      
      JPanel lowerPanel = new JPanel();
      lowerPanel.add(okButton);
      lowerPanel.add(cancelButton);
      
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(upperPanel, BorderLayout.NORTH);
      getContentPane().add(lowerPanel, BorderLayout.SOUTH);
      
      setResizable(false);
      pack();
   }

	private void okButtonPressed()
	{
		try 
		{ 
			int newSize = Integer.parseInt(sizeTextField.getText());
			if (newSize <= 0)
				formatError();  // show error box and wait for new input
			else
			{
			   enteredSize = newSize;
		   	ok = true;
				dispose();      // new size is valid: close dialog
			}
		}
		catch (NumberFormatException e) 
		{
		   formatError();     // non-digits have been entered: show error box
		}				   
	}
	
	private void formatError ()
	{
	   JOptionPane.showMessageDialog(this, "Please enter a positive integer!", 
	                                 "New size", JOptionPane.OK_OPTION);
	}
	
	/** Returns true if the OK-button was pressed. */
   public boolean isOk () {return ok;}


   /** Gets the last valid entered size. */
   public int getEnteredSize()
   {
      return enteredSize;
   }

   
   /** Changes the size displayed in the text field. The new value
    * is also highlighted, so it can easily be overwritten. 
    * @param size the new size */
   public void setEnteredSize(int size)
   {
      this.enteredSize = size;
      sizeTextField.setText(""+size);
      sizeTextField.setSelectionStart(0);
   }
}
