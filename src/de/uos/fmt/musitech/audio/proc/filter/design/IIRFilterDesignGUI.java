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
package de.uos.fmt.musitech.audio.proc.filter.design;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.TextEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class IIRFilterDesignGUI extends JPanel {

	float rate = 44100.0f; // fixed sampling rate
	int freqPoints = 250; // number of points in FR plot
	int maxOrder = 16; // maximum filter order
	int order; // oder of the filter to design
	String filterType;
	String prototype;
	float[] gain = new float[freqPoints + 1];
	IIRFilterDesign filter = new IIRFilterDesign();
	GraphPlot frPlot = new GraphPlot();
	PoleZeroPlot pzPlot = new PoleZeroPlot();
	boolean isStandalone = false;
	BorderLayout borderLayout1 = new BorderLayout();
	Panel pnlControls = new Panel();
	GridLayout gridLayout1 = new GridLayout();
	Panel pnlLeftPanel = new Panel();
	GridLayout gridLayout2 = new GridLayout();
	Panel pnlRightPanel = new Panel();
	GridLayout gridLayout3 = new GridLayout();
	Panel pnlFilterType = new Panel();
	FlowLayout flowLayout1 = new FlowLayout();
	Label lblFilterType = new Label();
	CheckboxGroup cbgFilterType = new CheckboxGroup();
	Checkbox cbLowPass = new Checkbox();
	Checkbox cbBandPass = new Checkbox();
	Checkbox cbHighPass = new Checkbox();
	Panel pnlPrototype = new Panel();
	FlowLayout flowLayout2 = new FlowLayout();
	Label lblPrototype = new Label();
	CheckboxGroup cbgPrototype = new CheckboxGroup();
	Checkbox cbButterworth = new Checkbox();
	Checkbox cbChebyshev = new Checkbox();
	Panel pnlOrder = new Panel();
	FlowLayout flowLayout3 = new FlowLayout();
	Label lblOrder = new Label();
	TextField tfOrder = new TextField();
	Panel pnlPassband = new Panel();
	FlowLayout flowLayout4 = new FlowLayout();
	Label lblPassband = new Label();
	TextField tfFreq1 = new TextField();
	Label lblTo = new Label();
	TextField tfFreq2 = new TextField();
	Label lblHz = new Label();
	Panel pnlRipple = new Panel();
	FlowLayout flowLayout5 = new FlowLayout();
	Label lblRipple = new Label();
	TextField tfRipple = new TextField();
	Label lblRippleUnit = new Label();
	Panel pnlButtons1 = new Panel();
	Panel pnlMinGain = new Panel();
	FlowLayout flowLayout6 = new FlowLayout();
	FlowLayout flowLayout7 = new FlowLayout();
	Button btnResponse = new Button();
	Button btnDesign = new Button();
	Label lblMinGain = new Label();
	Choice chMinGain = new Choice();
	Label lblMinGainUnit = new Label();
	Button btnPoleZero = new Button();
	Label lblMaxOrder = new Label();
	Panel pnlDisplay = new Panel();
	CardLayout cardLayout1 = new CardLayout();
	Panel pnlPZPlot = new Panel();
	Panel pnlFRPlot = new Panel();
	BorderLayout borderLayout2 = new BorderLayout();
	BorderLayout borderLayout3 = new BorderLayout();
	Panel pnlButtons2 = new Panel();
	FlowLayout flowLayout8 = new FlowLayout();
	Panel pnlCoeffs = new Panel();
	BorderLayout borderLayout4 = new BorderLayout();
	TextArea txtCoeffs = new TextArea();
	Button btnCoeffs = new Button();

	//Get a parameter value
	public String getParameter(String key, String def) {
//		return isStandalone ? System.getProperty(key, def) : (getParameter(key) != null ? getParameter(key) : def);
		return System.getProperty(key, def);
	}

	//Construct the applet
	public IIRFilterDesignGUI() {
		init();
	}

	//Initialize the applet
	public void init() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setRate(44100);
		
	}

	//Component initialization
	public void jbInit() throws Exception {
		Font f = new Font("Dialog", 0, 11);
		this.setSize(new Dimension(600, 400));
		gridLayout1.setColumns(2);
		gridLayout2.setColumns(1);
		gridLayout3.setRows(4);
		gridLayout3.setColumns(1);
		flowLayout1.setAlignment(0);
		lblFilterType.setFont(f);
		lblFilterType.setText("Filter type:");
		cbLowPass.setFont(f);
		cbLowPass.setLabel("LP");
		cbLowPass.setCheckboxGroup(cbgFilterType);
		cbLowPass.addItemListener(new IIRFilterDesign_cbLowPass_itemAdapter(this));
		cbBandPass.setFont(f);
		cbBandPass.setLabel("BP");
		cbBandPass.setCheckboxGroup(cbgFilterType);
		cbBandPass.addItemListener(new IIRFilterDesign_cbBandPass_itemAdapter(this));
		cbHighPass.setFont(f);
		cbHighPass.setLabel("HP");
		cbHighPass.setCheckboxGroup(cbgFilterType);
		cbHighPass.addItemListener(new IIRFilterDesign_cbHighPass_itemAdapter(this));
		cbLowPass.setState(true);
		flowLayout2.setAlignment(0);
		lblPrototype.setFont(f);
		lblPrototype.setText("Prototype:");
		cbButterworth.setFont(f);
		cbButterworth.setLabel("Butterworth");
		cbButterworth.setCheckboxGroup(cbgPrototype);
		cbButterworth.addItemListener(new IIRFilterDesign_cbButterworth_itemAdapter(this));
		cbChebyshev.setFont(f);
		cbChebyshev.setLabel("Chebyshev");
		cbChebyshev.setCheckboxGroup(cbgPrototype);
		cbChebyshev.addItemListener(new IIRFilterDesign_cbChebyshev_itemAdapter(this));
		cbButterworth.setState(true);
		flowLayout3.setAlignment(0);
		lblOrder.setFont(f);
		lblOrder.setText("Filter order:");
		tfOrder.setFont(f);
		tfOrder.setText("1");
		tfOrder.setColumns(3);
		tfOrder.addTextListener(new IIRFilterDesign_tfOrder_textAdapter(this));
		lblMaxOrder.setFont(new Font("Dialog", 2, 11));
		lblMaxOrder.setText("(max " + String.valueOf(maxOrder) + ")");
		flowLayout8.setAlignment(0);
		txtCoeffs.setText("Filter Coefficients\n\n");
		btnCoeffs.addActionListener(new IIRFilterDesign_btnCoeffs_actionAdapter(this));
		btnCoeffs.setEnabled(false);
		btnCoeffs.setLabel("Coefficients");
		pnlCoeffs.setLayout(borderLayout4);
		pnlButtons2.setLayout(flowLayout8);
		pnlFRPlot.setLayout(borderLayout3);
//		frPlot.setPlotStyle(GraphPlot.SPECTRUM);
		frPlot.setPlotStyle(GraphPlot.SIGNAL);
		frPlot.setTracePlot(true);
		frPlot.setLogScale(true);
		pzPlot.setSize(new Dimension(300, 268));
		frPlot.setBgColor(Color.lightGray);
		frPlot.setPlotColor(Color.blue);
		frPlot.setAxisColor(Color.darkGray);
		frPlot.setGridColor(Color.darkGray);
		pnlFRPlot.add(frPlot, BorderLayout.CENTER);
		pnlCoeffs.add(txtCoeffs, BorderLayout.CENTER);
		pnlPZPlot.setLayout(borderLayout2);
		pnlPZPlot.add(pzPlot, BorderLayout.CENTER);
		pnlDisplay.setLayout(cardLayout1);
		pnlDisplay.add(pnlPZPlot, "PZPlot");
		pnlDisplay.add(pnlFRPlot, "FRPlot");
		pnlDisplay.add(pnlCoeffs, "Coeffs");
		flowLayout4.setAlignment(0);
		lblPassband.setFont(f);
		lblPassband.setText("Passband:");
		tfFreq1.setFont(f);
		tfFreq1.setText("0");
		tfFreq1.setColumns(5);
		tfFreq1.addTextListener(new IIRFilterDesign_tfFreq1_textAdapter(this));
		lblTo.setFont(f);
		lblTo.setAlignment(1);
		lblTo.setText("to");
		tfFreq2.setFont(f);
		tfFreq2.setText("1000");
		tfFreq2.setColumns(5);
		tfFreq2.addTextListener(new IIRFilterDesign_tfFreq2_textAdapter(this));
		lblHz.setFont(f);
		lblHz.setText("Hz");
		flowLayout5.setAlignment(0);
		lblRipple.setEnabled(false);
		lblRipple.setFont(f);
		lblRipple.setText("Passband ripple:");
		tfRipple.setEnabled(false);
		tfRipple.setFont(f);
		tfRipple.setText("1.0");
		tfRipple.setColumns(3);
		tfRipple.addTextListener(new IIRFilterDesign_tfRipple_textAdapter(this));
		lblRippleUnit.setEnabled(false);
		lblRippleUnit.setFont(f);
		lblRippleUnit.setText("dB");
		flowLayout6.setAlignment(0);
		flowLayout7.setAlignment(0);
		btnResponse.setEnabled(false);
		btnResponse.setFont(f);
		btnResponse.setLabel("Frequency response");
		btnResponse.addActionListener(new IIRFilterDesign_btnResponse_actionAdapter(this));
		btnDesign.setFont(f);
		btnDesign.setLabel("Design");
		lblMinGain.setEnabled(false);
		lblMinGain.setFont(f);
		lblMinGain.setText("Minimum plot gain:");
		lblMinGainUnit.setEnabled(false);
		lblMinGainUnit.setFont(f);
		lblMinGainUnit.setText("dB");
		btnPoleZero.setLabel("Poles/Zeros");
		btnPoleZero.setEnabled(false);
		btnPoleZero.setFont(f);
		btnPoleZero.addActionListener(new IIRFilterDesign_btnPoleZero_actionAdapter(this));
		chMinGain.addItem("-10");
		chMinGain.addItem("-50");
		chMinGain.addItem("-100");
		chMinGain.addItem("-200");
		chMinGain.select("-100");
		chMinGain.addItemListener(new IIRFilterDesign_chMinGain_itemAdapter(this));
		chMinGain.setEnabled(false);
		chMinGain.setFont(f);
		btnDesign.addActionListener(new IIRFilterDesign_btnDesign_actionAdapter(this));
		pnlButtons1.setLayout(flowLayout7);
		pnlMinGain.setLayout(flowLayout6);
		pnlRipple.setLayout(flowLayout5);
		pnlPassband.setLayout(flowLayout4);
		pnlOrder.setLayout(flowLayout3);
		pnlPrototype.setLayout(flowLayout2);
		pnlFilterType.setLayout(flowLayout1);
		pnlRightPanel.setLayout(gridLayout3);
		gridLayout2.setRows(4);
		pnlLeftPanel.setLayout(gridLayout2);
		pnlControls.setLayout(gridLayout1);
		this.setLayout(borderLayout1);
		// Gain frequency response plot
		this.add(pnlDisplay, BorderLayout.CENTER);
		this.add(pnlControls, BorderLayout.SOUTH);
		pnlControls.add(pnlLeftPanel, null);
		pnlLeftPanel.add(pnlFilterType, null);
		pnlFilterType.add(lblFilterType, null);
		pnlFilterType.add(cbLowPass, null);
		pnlFilterType.add(cbBandPass, null);
		pnlFilterType.add(cbHighPass, null);
		pnlLeftPanel.add(pnlPrototype, null);
		pnlPrototype.add(lblPrototype, null);
		pnlPrototype.add(cbButterworth, null);
		pnlPrototype.add(cbChebyshev, null);
		pnlLeftPanel.add(pnlOrder, null);
		pnlOrder.add(lblOrder, null);
		pnlOrder.add(tfOrder, null);
		pnlOrder.add(lblMaxOrder, null);
		pnlLeftPanel.add(pnlPassband, null);
		pnlPassband.add(lblPassband, null);
		pnlPassband.add(tfFreq1, null);
		pnlPassband.add(lblTo, null);
		pnlPassband.add(tfFreq2, null);
		pnlPassband.add(lblHz, null);
		pnlControls.add(pnlRightPanel, null);
		pnlRightPanel.add(pnlRipple, null);
		pnlRipple.add(lblRipple, null);
		pnlRipple.add(tfRipple, null);
		pnlRipple.add(lblRippleUnit, null);
		pnlRightPanel.add(pnlMinGain, null);
		pnlMinGain.add(lblMinGain, null);
		pnlMinGain.add(chMinGain, null);
		pnlMinGain.add(lblMinGainUnit, null);
		pnlRightPanel.add(pnlButtons1, null);
		pnlButtons1.add(btnDesign, null);
		pnlButtons1.add(btnPoleZero, null);
		pnlRightPanel.add(pnlButtons2, null);
		pnlButtons2.add(btnResponse, null);
		pnlButtons2.add(btnCoeffs, null);
	}

	//Get Applet information
	public String getAppletInfo() {
		return "(C) 1998 Dr Iain A Robin\nIIR filter design code based on a Pascal program\n"
			+ "listed in 'Digital Signal Processing with Computer Applications'\n"
			+ "by P. Lynn and W. Fuerst (Prentice Hall)";
	}

	//Get parameter info
	public String[][] getParameterInfo() {
		return null;
	}

	private void setRippleState(boolean b) {
		lblRipple.setEnabled(b);
		tfRipple.setEnabled(b);
		lblRippleUnit.setEnabled(b);
	}

	private void setMinGainState(boolean b) {
		lblMinGain.setEnabled(b);
		chMinGain.setEnabled(b);
		lblMinGainUnit.setEnabled(b);
	}

	void cbChebyshev_itemStateChanged() {
		setRippleState(cbChebyshev.getState());
		btnResponse.setEnabled(false);
		btnPoleZero.setEnabled(false);
		btnPoleZero.setEnabled(false);
	}

	void cbButterworth_itemStateChanged() {
		setRippleState(cbChebyshev.getState());
		btnResponse.setEnabled(false);
		btnPoleZero.setEnabled(false);
		btnPoleZero.setEnabled(false);
	}

	/** 
	 * designFilter reads tthe selected values from the GUI and
	 * calls the design method of filter.
	 * 
	 */
	void designFilter() {
		order = Integer.parseInt(tfOrder.getText());
		if (order > maxOrder) {
			showStatus("Filter order too high (max. " + String.valueOf(maxOrder) + ")");
		} else {
			filterType = cbgFilterType.getSelectedCheckbox().getLabel();
			if ((filterType.equals("BP")) && ((order % 2) != 0)) {
				showStatus("Filter order must be even for BP filter");
			} else {
				filter.setFilterType(filterType);
				prototype = cbgPrototype.getSelectedCheckbox().getLabel();
				filter.setPrototype(prototype);
				filter.setRate(rate);
				filter.setOrder(order);
				filter.setFreq1(Float.valueOf(tfFreq1.getText()).floatValue());
				filter.setFreq2(Float.valueOf(tfFreq2.getText()).floatValue());
				if (cbChebyshev.getState())
					filter.setRipple(Float.valueOf(tfRipple.getText()).floatValue());
				filter.design();
				btnResponse.setEnabled(true);
				btnPoleZero.setEnabled(true);
			}
		}
	}

	/** 
	 * showStatus
	 * @param string
	 */
	private void showStatus(String string) {
		// TODO Auto-generated method stub
		
	}

	void btnDesign_actionPerformed(ActionEvent e) {
		designFilter();
	}

	void plotPolesAndZeros() {
		// pole and zero locations
		// NB poles and zeros have indices 1 .. order
		float[] pReal = new float[order + 1];
		float[] pImag = new float[order + 1];
		float[] z = new float[order + 1];
		for (int i = 1; i <= order; i++) {
			pReal[i] = filter.getPReal(i);
			pImag[i] = filter.getPImag(i);
			z[i] = filter.getZero(i);
		}
		pzPlot.setPolesAndZeros(pReal, pImag, z);
	}

	void btnPoleZero_actionPerformed(ActionEvent e) {
		setMinGainState(false);
		plotPolesAndZeros();
		cardLayout1.show(pnlDisplay, "PZPlot");
	}

	void plotResponse() {
		filter.setFreqPoints(freqPoints);
		gain = filter.filterGain();
		frPlot.setYmax(minPlotGain());
		frPlot.setPlotValues(gain);
	}

	void listCoeffs() {
		txtCoeffs.setText(prototype + " IIR filter\n\n");
		txtCoeffs.append("Filter type: " + filterType + "\n");
		txtCoeffs.append("Passband: " + tfFreq1.getText() + " - " + tfFreq2.getText() + " Hz\n");
		if (cbChebyshev.getState())
			txtCoeffs.append("Passband ripple: " + tfRipple.getText() + " dB\n");
		txtCoeffs.append("Order: " + String.valueOf(order) + "\n\n");
		txtCoeffs.append("Coefficients\n\n");
		for (int i = 0; i <= order; i++)
			txtCoeffs.append(
				"a["
					+ String.valueOf(i)
					+ "] = "
					+ String.valueOf(filter.getACoeff(i))
					+ "     \tb["
					+ String.valueOf(i)
					+ "] = "
					+ String.valueOf(filter.getBCoeff(i))
					+ "\n");
		cardLayout1.show(pnlDisplay, "Coeffs");
	}

	void btnResponse_actionPerformed(ActionEvent e) {
		setMinGainState(true);
		plotResponse();
		btnCoeffs.setEnabled(true);
		cardLayout1.show(pnlDisplay, "FRPlot");
	}

	void btnCoeffs_actionPerformed(ActionEvent e) {
		listCoeffs();
	}

	void cbLowPass_itemStateChanged(ItemEvent e) {
		if (cbLowPass.getState()) {
			tfFreq1.setText("0");
			tfFreq2.setText("");
			tfFreq1.setEditable(false);
			tfFreq2.setEditable(true);
			btnResponse.setEnabled(false);
			btnPoleZero.setEnabled(false);
			btnCoeffs.setEnabled(false);
		}
	}

	void cbBandPass_itemStateChanged(ItemEvent e) {
		if (cbBandPass.getState()) {
			tfFreq1.setText("");
			tfFreq2.setText("");
			tfFreq1.setEditable(true);
			tfFreq2.setEditable(true);
			btnResponse.setEnabled(false);
			btnPoleZero.setEnabled(false);
			btnCoeffs.setEnabled(false);
		}
	}

	void cbHighPass_itemStateChanged(ItemEvent e) {
		if (cbHighPass.getState()) {
			tfFreq1.setText("");
			tfFreq2.setText(String.valueOf(Math.round(rate / 2)).toString());
			tfFreq1.setEditable(true);
			tfFreq2.setEditable(false);
			btnResponse.setEnabled(false);
			btnPoleZero.setEnabled(false);
			btnCoeffs.setEnabled(false);
		}
	}

	private float minPlotGain() {
		return Math.abs(Float.valueOf(chMinGain.getSelectedItem()).floatValue());
	}

	void chMinGain_itemStateChanged(ItemEvent e) {
		if (btnResponse.isEnabled()) {
			plotResponse();
		}
	}

	void tfFreq1_textValueChanged(TextEvent e) {
		btnResponse.setEnabled(false);
		btnPoleZero.setEnabled(false);
		btnCoeffs.setEnabled(false);
	}

	void tfFreq2_textValueChanged(TextEvent e) {
		btnPoleZero.setEnabled(false);
		btnResponse.setEnabled(false);
		btnCoeffs.setEnabled(false);
	}

	void tfOrder_textValueChanged(TextEvent e) {
		btnPoleZero.setEnabled(false);
		btnResponse.setEnabled(false);
		btnCoeffs.setEnabled(false);
	}

	void tfRipple_textValueChanged(TextEvent e) {
		btnPoleZero.setEnabled(false);
		btnResponse.setEnabled(false);
		btnCoeffs.setEnabled(false);
	}
	
	public static void main(String[] args) {
		JFrame jFrame = new JFrame("Filter Design");
		IIRFilterDesignGUI filterDesign = new IIRFilterDesignGUI();
		jFrame.getContentPane().add(filterDesign);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.pack();
		jFrame.show();		
	} // end main


	/** 
	 * getRate
	 * @return
	 */
	public float getRate() {
		return rate;
	}

	/** 
	 * setRate
	 * @param f
	 */
	public void setRate(float f) {
		rate = f;
	}

	/** 
	 * getFilter
	 * @return
	 */
	public IIRFilterDesign getFilter() {
		return filter;
	}

	/** 
	 * setFilter
	 * @param filter
	 */
	public void setFilter(IIRFilterDesign filter) {
		this.filter = filter;
	}

}

/* ----- end of IIRFilterDesignGUI class ------ */

class IIRFilterDesign_cbChebyshev_itemAdapter implements java.awt.event.ItemListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_cbChebyshev_itemAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		adaptee.cbChebyshev_itemStateChanged();
	}
}

class IIRFilterDesign_cbButterworth_itemAdapter implements java.awt.event.ItemListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_cbButterworth_itemAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		adaptee.cbButterworth_itemStateChanged();
	}
}

class IIRFilterDesign_btnDesign_actionAdapter implements java.awt.event.ActionListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_btnDesign_actionAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.btnDesign_actionPerformed(e);
	}
}

class IIRFilterDesign_btnResponse_actionAdapter implements java.awt.event.ActionListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_btnResponse_actionAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.btnResponse_actionPerformed(e);
	}
}

class IIRFilterDesign_cbLowPass_itemAdapter implements java.awt.event.ItemListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_cbLowPass_itemAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		adaptee.cbLowPass_itemStateChanged(e);
	}
}

class IIRFilterDesign_cbBandPass_itemAdapter implements java.awt.event.ItemListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_cbBandPass_itemAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		adaptee.cbBandPass_itemStateChanged(e);
	}
}

class IIRFilterDesign_cbHighPass_itemAdapter implements java.awt.event.ItemListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_cbHighPass_itemAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		adaptee.cbHighPass_itemStateChanged(e);
	}
}

class IIRFilterDesign_chMinGain_itemAdapter implements java.awt.event.ItemListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_chMinGain_itemAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		adaptee.chMinGain_itemStateChanged(e);
	}
}

class IIRFilterDesign_tfFreq1_textAdapter implements java.awt.event.TextListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_tfFreq1_textAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		adaptee.tfFreq1_textValueChanged(e);
	}
}

class IIRFilterDesign_tfFreq2_textAdapter implements java.awt.event.TextListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_tfFreq2_textAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		adaptee.tfFreq2_textValueChanged(e);
	}
}

class IIRFilterDesign_tfOrder_textAdapter implements java.awt.event.TextListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_tfOrder_textAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		adaptee.tfOrder_textValueChanged(e);
	}
}

class IIRFilterDesign_tfRipple_textAdapter implements java.awt.event.TextListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_tfRipple_textAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		adaptee.tfRipple_textValueChanged(e);
	}
}

class IIRFilterDesign_btnPoleZero_actionAdapter implements java.awt.event.ActionListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_btnPoleZero_actionAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.btnPoleZero_actionPerformed(e);
	}
}

class IIRFilterDesign_btnCoeffs_actionAdapter implements java.awt.event.ActionListener {
	IIRFilterDesignGUI adaptee;

	IIRFilterDesign_btnCoeffs_actionAdapter(IIRFilterDesignGUI adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		adaptee.btnCoeffs_actionPerformed(e);
	}
	
}
