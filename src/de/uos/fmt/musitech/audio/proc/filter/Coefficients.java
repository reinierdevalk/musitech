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
package de.uos.fmt.musitech.audio.proc.filter;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * @author Felix Kugel
 *
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */
public class Coefficients {
	
	int n;
	private float[][] matrix;
	int[] sparseRowIndices;
	
	public Coefficients(final int argN){
		setOrder(argN);
		initialize();
	}
	
	public void initialize(){
		matrix = new float[2][n];
	}
	
	void calculateSparseRowIndices(){
		Vector sIndices = new Vector();
		
		for (int i = 0; i < matrix[0].length; i++) {
			if ((matrix[0][i] > 0) | (matrix[1][i] > 0))
				sIndices.add(new Integer(i));
		}
		{
			sparseRowIndices = new int[sIndices.size()];
			
			int i=0;
			for (Iterator it = sIndices.iterator(); it.hasNext();) {
				sparseRowIndices[i++] = ((Integer) it.next()).intValue();
			}
		}
	}
	
	public int getOrder(){
		return n;
	}
	
	private void setOrder(int N){
		this.n = N;
	}
	
	public void setXCoefficients(int i, float value){
		matrix[0][i] = value;
	}	
	
	public void setYCoefficients(int i, float value){
		matrix[1][i] = value;
	}

	public float[] getXCoefficients() {
		return matrix[0];
	}

	public float[] getYCoefficients() {
		return matrix[1];
	}

	public void setXCoefficients(float[] xCoefficients) {
		matrix[0] = xCoefficients;
	}

	public void setYCoefficients(float[] yCoefficients) {
		matrix[1] = yCoefficients;
	}

	/**
	 * Returns the matrix.
	 * @return float[][]
	 */
	public float[][] getMatrix() {
		return matrix;
	}
	
	
	public CoefficientTableModel getTableModel(){
		return new CoefficientTableModel();
	}
	
	
	/** For use with JTable */
	public class CoefficientTableModel extends AbstractTableModel{

		private boolean isSparse = false;

		public void setSparse(boolean b){
			isSparse = b;
			calculateSparseRowIndices();
			this.fireTableChanged(new TableModelEvent(this));
		}

		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return (isSparse)?sparseRowIndices.length:getOrder();
		}

		public Object getValueAt(int arg0, int arg1) {

			switch(arg1){
			
				case 0:
					return new Float(getXCoefficients()[getRowIndices()[arg0]]);
				case 1:
					return new Float(getYCoefficients()[getRowIndices()[arg0]]);
				case 2:
					return new Float(getRowIndices()[arg0]);
				default:
					new Exception("arg1:"+arg1+"  arg0:"+arg0).printStackTrace();
				
			}	
			return null;
		}
		

		public boolean isCellEditable(int arg0, int arg1) {
			return true;
		}
		
		


		private int[] getRowIndices(){
			
			if (isSparse)
				return sparseRowIndices;
			
			int[] indices = new int[getRowCount()];
			for (int i = 0; i < indices.length; i++) {
				indices[i]=i;
			}
			
			return indices;	
		}

		public void setValueAt(Object arg0, int y, int x) {
			
			switch(x){
			
				case 0:
					getXCoefficients()[getRowIndices()[y]] = ((Float)arg0).floatValue();
					break;
					
				case 1:
					getYCoefficients()[getRowIndices()[y]] = ((Float)arg0).floatValue();
					break;
					
				case 2:
					int newIndex = (int)((Float)arg0).intValue();
					cutRowToNewRow(y, newIndex);
					break;
					
				default:
					new Exception("arg1:"+x+"  arg2:"+y).printStackTrace();
				
			}	
			calculateSparseRowIndices();
			fireTableChanged(new TableModelEvent(this));	
		}

		public void cutRowToNewRow(int y, int newIndex) {
			getXCoefficients()[newIndex] = getXCoefficients()[getRowIndices()[y]];
			getYCoefficients()[newIndex] = getYCoefficients()[getRowIndices()[y]];
			
			getXCoefficients()[getRowIndices()[y]] = 0;
			getYCoefficients()[getRowIndices()[y]] = 0; 
		}

		public Class getColumnClass(int column) {
			return Float.class;
		}

		public String getColumnName(int arg0) {
			return super.getColumnName(arg0);
		}

	}

}
