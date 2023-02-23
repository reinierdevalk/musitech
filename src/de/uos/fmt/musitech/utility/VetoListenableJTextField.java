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
package de.uos.fmt.musitech.utility;/**
 * A Vetoable and Listenable TextField.
 * Creation date: (22.01.2002 19:03:30)
 * @author TW
 * @version 0.113
 */

public class VetoListenableJTextField extends ListenableJTextField {
	protected java.lang.String lastVetoText = "";
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	boolean notifying = false;	
	
	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == VetoListenableJTextField.this)
				connEtoC1();
		};
		@Override
		public void focusGained(java.awt.event.FocusEvent e) {
		};
		@Override
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == VetoListenableJTextField.this)
				connEtoC2(e);
		};
	};
	protected java.lang.String textPropertyName = null;

	/**
	 * Constructor.
	 */
	public VetoListenableJTextField() {
		super();
		initialize();
	}

	/**
	 * connEtoC1:  (VetoListenableJTextField.action. --> VetoListenableJTextField.notifyVetoableChange()V)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1() {
		try {
			// user code begin {1}
			// user code end
			this.notifyVetoableChange();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoC2:  (VetoListenableJTextField.focus.focusLost(java.awt.event.FocusEvent) --> VetoListenableJTextField.notifyVetoableChange()V)
	 * @param arg1 java.awt.event.FocusEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC2(java.awt.event.FocusEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.notifyVetoableChange();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * Insert the method's description here.
	 * @date (22.04.2001 03:35:02)
	 * @return java.lang.String
	 */
	public java.lang.String getTextPropertyName() {
		return textPropertyName;
	}

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		// exception.printStackTrace(System.out);
	}

	/**
	 * Initializes connections
	 * @exception java.lang.Exception The exception description.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initConnections() throws java.lang.Exception {
		// user code begin {1}
		// user code end
		this.addActionListener(ivjEventHandler);
		this.addFocusListener(ivjEventHandler);
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("VetoListenableJTextField");
			setSize(83, 21);
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			VetoListenableJTextField aVetoListenableJTextField;
			aVetoListenableJTextField = new VetoListenableJTextField();
			frame.setContentPane(aVetoListenableJTextField);
			frame.setSize(aVetoListenableJTextField.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of utility.VetoListenableJTextField");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Notfies the listeners of a vetoable change.
	 */
	protected void notifyVetoableChange() {
		synchronized (this) {
			if (!notifying)
				notifying = true;
			else
				return;
		}
		String newText = getText();
		if (newText != null && newText.equals(lastVetoText)) {
			notifying = Boolean.FALSE;
			return;
		}
		try {
			fireVetoableChange("text", lastVetoText, newText);
			if (textPropertyName != null)
				fireVetoableChange(textPropertyName, lastVetoText, newText);
		} catch (java.beans.PropertyVetoException pve) {
			// if anyone vetoed, set back the text
			javax.swing.JOptionPane.showMessageDialog(getTopLevelAncestor(), pve.getMessage(), "Invalid Text", javax.swing.JOptionPane.ERROR_MESSAGE);
			super.setText(lastVetoText);
			notifying = Boolean.FALSE;
			return;
		}
		// else remeber unvetoed text
		lastVetoText = newText;
		notifying = Boolean.FALSE;
	}

	/**
	 * Sets the text property (java.lang.String) value.
	 * @param text The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getText
	 */
	@Override
	public void setText(java.lang.String newText) {
		super.setText(newText);
		notifyVetoableChange();
	}

	/**
	 * Sets the textPropertyName.
	 * @date (22.04.2001 03:35:02)
	 * @param newTextPropertyName java.lang.String
	 */
	public void setTextPropertyName(java.lang.String newTextPropertyName) {
		textPropertyName = newTextPropertyName;
	}

	/**
	 *
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88GB885B8ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13598EDECDBD735C0FFCCDBA7A6C851D8C1CA091359095B24E4C8AD5B34A095E9137A238326C5D311BAD4C605268924D40AD0FF5C541FCDD23BDD1CAD9BC16B568A35E4B20595913230B5F5D2925A31C03334C311680BFDEDBF7A6C77703BEE92EDBFB867FEBCFF44EEB7BEA21D5CF74F3977FC1FF3CFC2E22FB5867A0E4488698B9079737BBEC28E8F91927D620EE794AE5CB866A3E5BF2F831CA0891F9F
		5691200578FEB6B588EB84609C60F07D98566364C6EAC46297A4DEC15F91C2D2DB714CC729D1096B02B35DGA781FFFE2BB8FF855623DC722B2363725C48238D4475583150B164D346D301D75E5DABD7C26E892EFE8DE539381EF8A78C7B8BGE83C0FFC981A00F5F27EE6661D940D97CDBB1E4E31F82EE86B0CA63371A2538D1D2DCDCCACD2E6DE51EDC6735A32C17F3CC0D759A51D9AA9C2FAB87F67D3E14E0F10C7E03F2189F13F4B204CF1781E8748B6A97CE10E5F8D5F7BGFA1A141D4F3DB7A96D70BF69A2
		6E27BE6C0B5631F350A56CBB6FAA59096BABBCA61F73236FAD20B5G388176GBC8FB058BC667BB95006485D3EDF41BA354C92E6AEE76627AC6B2211AA3CE72628815F9393C011383F5124D9GBBC53CDBD3EF565067F6A06E6D7C547704CF5CEF2958DB4FBCC0AE4CBC9B8C5570C957D0B8F43AD978E232396493A17B27F5E5AFB5D766E6256CEB24DCF64365D0A8D0C376866F0120A579ECAC137DD0B3465A43E36D026FBE199F825F49713DE5F81127EFF3BC6E43ADE3BE23C565465B6FA8DDB6BC72F9B25B78
		D4CDBF74EEBC985C5BA2F4B858D22D0B15C55E3187054C00234BC79CFF268CAFF4B94271381F84DAA300482FCDC12CDB3F9B6A95E09CE09E408258845068D6B976DD5F2FBFE10E4DEB85F631289BA9DAA0626EA67FCB30C2159534393C4D34FC12AEEA859D0B4FA6724CE6BF7AE039625E83BC7713A063CFB4CF8B9A534DBCF057F2145102AD4EB479CF601982655485BDC7C1C04EA20AEF1B9FDAA1B1B41BFDE025B4C6CBA7843D530FB13925AD82C7E060545D2E907A2B8B6C6F86B86878A19EC0FE17E9A1CF0D
		0909AB26F933E8AD20A5643C877D9C2658BB8E437918C779789BE1442D427EBE0F62C383AF1671195613283ED6D8D3B5561E415E6C72281CF9613A4A19BD7F78B2E1B7DF2819B3880FFBC42E6C77D4574EAE4E9375927B294C75CFD14375406529DDF7C2465F7FA7B2FCF570423F277AB1D66156B15FE90069D665634640C9113397B51B2AACFD3FG89C6D3A50F8BBDEFE543521FBEC7774FF05C92705B582AFC964EDEAFBB775F5AD55BE72FF39948BF70FF10A1FC7325A03EC774964D33E9EA51FC8ABEECC97B
		EA90F3CE56EB5AF46EB4F13C85751D0002329D3CA708B528B1D650170B0CDE250C6979CC093E034B320B4BD629B5A8D9BB03E8233982756D1CFFB4083511B473F94A0364DC117447F8EDD20366C06904D94CB3017FF688737BA6DD133D6109D010E0ED9BC56CB3E2C66850865006021E0350E32BA2BD2DD8372F51D8190F3C0043D9E7FF2B2A3F15D3CE7DBD3D191C4956FEBFCE97E32149B6D18F73ED257A937AFCB3DCDB6FAE5E9FA45E96FA3E7AD76CFD75A6951177CB83484F82B99B813E522672FE72A852D5
		EF11891FB08DC338537173597E6398977A2FA205160C92AEBB3EBB577FE295EDD17AD150477B510FC6D97F73FB79DB18D5F360C3ED22876EE9133E147E90678CDD1D1B770AF3D9EF65B961079B9F7C1248196E934FEFF7D1B53FB5FAD57C9EFED74DE3A78FB5132EC3C321019A71C368768A793D5EDA73D84BA0729E5FB6661B5D267C5D59DF7EA6CC5936195479F3A4FD35BCD09EDF014B8C74431A568BB61B0362DBD5FE4D8D203E78DEC934FC3F77B96F779248EDG707173899DFB0646839B17BE5B75724B30
		66CD26275750D76825C4D64BE76819330BF29E1A983DA06DE577EEA6779F6C8D561ACF3B1E7CD9F04FB661071EED157E08B83AED5615EE97360F79D637AB5D8E6BB817264DE491A6F17CFD45345919FFEA3734383642623C7944AFA1E186BF4F2A7EF7AF3D5DFC5931FD7DFBA3E2F3BE02B53C9B687B81FA810E370F794E352BB89D9AF80B5CE10619D90551A22F99D7AB1A151AA586FD223F48FD7BE0C0759722F47FC136DA777976CAFD0D2A3D387713F57762D57B7B2A6E891B6F0EE29D3901369BE09FC0AF40
		E8874CF2GCB9D4A76BF9C7B639DED47A0AC1889404DC2DDA72A7B2F236B967DBF3351336EDE350D9FF7D45E93B3150B57F383503C0EBD13117A6FD7A2D27B7D1A0B54FE3F5E0F1C90F860D1BE3DAA7A35C83D776D973C5E1EF8A94A6F5BB69519BC2DB1CD52FE4406C90D376CAFB0449251230648AB7AD1F9E60619095FB8F29B2275ED5C922DED6356A87EBDED1B45C212D65B610E0EB834D2F467984D21BA722D667719D64850F5FE58496F8BDA3D7B5F0A5673153075BB95367E1628BE3FE3857BF19A06FBB8
		FE5522C9125A0131FF182A7FBD6C05589FG70837EB440B40068774FD2B53BF981F7005F0B264B5FF314BDA673CD5027AA68A756516D8ABA6A7CE0855D17A927238D27A4DD74579BEF2877E6F0360174DEBBD9777F9CEFFEDD4E8BF2DD9AFDB7D82A1967EE5B8BEB01109F392E64F7DD3A1F6CBB777DE03D776EAEA957AD576FC979223FFCB6067E6F86DA8F40C8A774D300E90025CED50B29A15491BBC8CAE7D30CE949ACFF69D4E3D95469CA890D41953C9F9FC69FDA40E793C08740EE00EE072FFB688D413786
		4F19D486F3987F1AE4AABF6F09552F17AF446E7CB76816983E494BA6E3E68E79BBB59543D8B05362C801EB09211F8BFAA64B44D1097746B0978D1A66E8015BBBDCDF2F27076F2C577EE1BEBFG3F05B5CB727C61B04E36E6BAEDD3477E9F8F9FF7F0F3763CE96BD81F7F86D0CB87889AEC674D9188GGA494GGD0CB818294G94G88G88GB885B8AC9AEC674D9188GGA494GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCB88G
		GGG
	**end of data**/
	}
}