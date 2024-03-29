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
package de.uos.fmt.musitech.audio.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Felix Kugel
 *
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class JMultiSlider extends JPanel implements MultiSlider {

	//private float[][] matrix;
	
	private MultiSliderModel model;
	private JSlider[][] sliders;


	/**
	 * @see de.uos.fmt.musitech.audio.proc.filter.MultiSlider#getRows()
	 */
	@Override
	public int getRows() {
		return getMatrix().length;
	}

	/**
	 * @see de.uos.fmt.musitech.audio.proc.filter.MultiSlider#getColumns()
	 */
	@Override
	public int getColumns() {
		return getMatrix()[0].length;
	}


	/**
	 * @see de.uos.fmt.musitech.audio.proc.filter.MultiSlider#newInstance(int, int)
	 */
//	public void initialize(int rows, int columns) {
//		matrix = new float[columns][rows];
//		sliders = (JSlider[][])new JSlider[columns][rows];
//		setLayout(new GridLayout (rows, columns));
//	}


	public void printMatrix(){
		
		for (int c = 0; c < getColumns(); c++) {
			for (int r = 0; r < getRows(); r++) {
				System.out.print(getMatrix()[c][r]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * @see de.uos.fmt.musitech.audio.proc.filter.MultiSlider#getSliders()
	 */
	@Override
	public Object[][] getSliders() {
		if (sliders == null)
			sliders = new JSlider[getColumns()][getRows()];
			
		return sliders;
	}
	
	@Override
	public void addSlider(final int r, final int c, final SliderPrototype slider){
	
		getSliders()[c][r]= slider;
		add((JComponent)slider);
		
		getModel().addPropertyChangeListener(
			slider 
		);
		
		((JSlider)slider).addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				JSlider slider = (JSlider)(e.getSource());
				getModel().getMatrix()[c][r] = (float)slider.getValue()/(float)slider.getMaximum();
				
				//gainCorrection();
			}
		});
	}
	
	public void gainCorrection(){

		for (int i = 0; i < 2; i++) {
			float sum =0.0f;
			for (int j = 0; j < getMatrix().length; j++) {
				sum += getMatrix()[i][j];
			}
			if (sum>0)
				for (int j = 0; j < getMatrix().length; j++) {
					getMatrix()[i][j] /= sum;
				}
		}
	}
	
	public static void main(String[] args){
	
		MultiSliderFactory msf = MultiSliderFactory.newInstance(
			new JSliderPrototypeImpl( SwingConstants.VERTICAL, 0, 100, 0));
			
		final float[][] matrix = new float[3][5];	
			
		MultiSlider ms = msf.newMultiSlider(
			new MultiSliderModel(){
				@Override
				public float[][] getMatrix(){
					return matrix;
				}
				
				@Override
				public void addPropertyChangeListener(PropertyChangeListener l){}
			}
		);
		
		
		JFrame frame = new JFrame("MultiSlider");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add((JMultiSlider)ms, BorderLayout.CENTER);
		frame.setBounds(30,30,600,10);
		
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
	}
	
	@Override
	public float[][] getMatrix(){
		return model.getMatrix();
	}

	/**
	 * Returns the model.
	 * @return MultiSliderModel
	 */
	public MultiSliderModel getModel() {
		return model;
	}

	/**
	 * Sets the model.
	 * @param model The model to set
	 */
	@Override
	public void setModel(MultiSliderModel model) {
		this.model = model;
		setLayout(new GridLayout(getRows(), getColumns()));
	}
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
//		for (int i = 0; i < sliders.length; i++) {
//			for (int j = 0; j < sliders[0].length; j++) {
//				(SliderPrototype)(sliders[j][i])
//			}
//		}
	}

}
