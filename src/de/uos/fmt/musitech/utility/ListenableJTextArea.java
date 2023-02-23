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
package de.uos.fmt.musitech.utility;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 * Implements a listable JTextArea
 * @date (31.05.00 13:09:13)
 * @author TW
 * @version 0.113
 */
public class ListenableJTextArea extends javax.swing.JTextArea implements DocumentListener {
	java.awt.Color colorEdit;
	java.awt.Color colorNonEdit;
	String lastText;

	/**
	 * ListenableJTextField constructor.
	 */
	public ListenableJTextArea() {
		super();
		initialize();
	}

	/**
	 * Sends a notfiy document changed.
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		notifyChange();
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
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			getDocument().addDocumentListener(this);
			// user code end
			setName("ListenableJTextField");
			setSize(4, 21);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		notifyChange();
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			ListenableJTextField aListenableJTextField;
			aListenableJTextField = new ListenableJTextField();
			frame.setContentPane(aListenableJTextField);
			frame.setSize(aListenableJTextField.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of RhythmData.ListenableJTextField");
			exception.printStackTrace(System.out);
		}
	}

	private void notifyChange() {
		String newText = getText();
		firePropertyChange("text", lastText, newText);
		lastText = newText;
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		notifyChange();
	}

	/**
	 * Sets a document.
	 * @date (18.07.00 19:17:02)
	 * @param doc javax.swing.text.Document
	 */
	@Override
	public void setDocument(javax.swing.text.Document doc) {
		//	getDocument().removeDocumentListener(this);
		super.setDocument(doc);
		getDocument().addDocumentListener(this);
	}

	/**
	 * Sets editable.
	 * @date (18.07.00 18:15:43)
	 * @param b boolean
	 */
	@Override
	public void setEditable(boolean b) {
		if (isEditable())
			colorEdit = getBackground();
		else
			colorNonEdit = getBackground();
		if (b)
			if (colorEdit != null)
				setBackground(colorEdit);
			else
				setBackground(java.awt.Color.white);
		else
			if (colorNonEdit != null)
				setBackground(colorNonEdit);
			else
				setBackground(java.awt.Color.lightGray);
		super.setEditable(b);
	}

	/**
	 * Sets the listenableText property (java.lang.String) value.
	 * @param listenableText The new value for the property.
	 * @see #getListenableText
	 */
	@Override
	public void setText(java.lang.String newText) {
		String oldText = getText();
		if (getText().equals(newText))
			return;
		super.setText(newText);
		lastText = newText;
		firePropertyChange("text", oldText, newText);
	}

	/**
	 *
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88GC085B8ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D7FDEC94C594EFA2D193B1B6918C78918B7D20F72DD2741FC6B1748F0CA6768FC9CAA0FEC4C808B4A6C2E300709F535EE725E0210D295044D211A0A525119AC9FB3DD25AE231A52254D2A3C636F753F6533D5B73F60E3606343E37B333F72DFBA089DE724B6C3EB7735E1BB73F77F60E54FF1965292C2AA72452C3446F7BCAC2EAEA89E94BAE6A15B2FFD60B0B247CCEG0E90F7179B468650794C778D
		9DB5B09E8634018C47BBB05612AD9D8DC2BEAC649215D5045CDA05F3BE69E894329218339550887236D5FC7EG0C552667E30D97443C065716111C8D07FC0750CE0ACD09FC3136D40C04BC66E52B5157714E9A31468D6F81GEE5EC55A473E03B1F0EDEAA298262D895DE89D0F3056C842D0998DCD36A6182A29EC3623E3C8B5980DAAE39A7DF1184E301EB8D5E0DD05E9BB5469B7ED91D28C6FF732D1D658057E5A61F988904E16F2A3087292F85E8A284816FB7C70EAC06C413DA90764EF7E2D7233B4FB746770
		3D3565A47708639766F97485B16F9A68B281B900B78165GFFEE0B6BB8686A48E0579998FB46D82F9E0968511ED86C12960EDF5543D403670E8E508859AFB4244743047033BE51F956A61E3B012FBBF75F6BF8CE724F4BF30F3D73B439583F5D6B3349C9C91D5F57144BF3F1A1B719936EFBB22D6F515C053CDC687B86C9753DEC50677358784EA85C6155041D0C945FC539F856CD66D9E741F3256087171FB165FBD2641C23AF191C41F7F7DE0BEBB2CFF223B3A8E349D87F8499485AEC1B077DCB4A3D25F9BC06
		9DF90BE359E97A2CDD43FDD6DA31D418724694B90FE5B6A86B65B4681682B83FEE4FE14DD640FBCDFE0B6BA4A0G38891881AC4D179CDB56F56EBFF26C1A92E717922A9626F14257DE8DFE8EA3D4D9DC3992B518928D5191A52EE27199C44C9989E28E46962C7B5D64FE88FC7CC023B42EB0D50F02F5A5C2990D9BFCCEB8F88467C07922F6D80DD0F0900991E9D78B56E17DA8867BB596D698CD4E60FA9644B3392D0C00C5B0E055DD76BC662B90763F91D0E665E137E9EF104623D403AE226BD30958B06E04B4B9
		B04F73E66FA8077944A1F37CBCC1D9985E6FB8241DD35DC7D36C7429A18CDF094F4A9ABB95443E1C69101C59B5AAB9D37A67CA4226F659F286D16460DCF9533138F65ACC1B9897FFAF6E9A35D91F69302F237B8577314956076B8158CF7648C3D5F8D69590F70D3345F556A9F32CF4B7F14EDED28CAAD97AD39C88C643490C73B8BF8E92AB66FDA94F73C13B789F94380F957F2B8F1E0B0CB97C7E505BB44A7AE80CC64370E0885DA3F348B1D11F633A3566F1D39E03FA6E0582B2AC7913F3D8F38AE3F1F5AC4168
		F54A189A1DC86A1FB1FD990931D83295C8DF2B67306F6A53D04F567CB5F3D88BA1BD9A256621D8CB045EE156A255E884026E5593D14665EB67114FD3F4D6740217673D84EBD9CBE0DF617701A265A06EA12EC6602831B511C71DD827CFF5562764288FE4F82FE1CED9EF53DFD97536E5B9B9BDE97F3DB81928773D6D94BD5A192CB79ECF8E310F5BE97683A1B7F81CB903984F22DB49E515EAE14E714BAE3465AEE8F1B5G5A8A2CBED209FA59C78439FBF5CD632934F2DCD7D50BE7C27FCAD0E83F68E1504A5B31
		2A230BF4A3A207DC5FDC05B954D2FA9D0FF963574DC45C35525D2B4C9D48FB55816BDEB5D0A0737CEA373CA7B56D4DA5A5FB6BFC87ED72ECFEAB8BF8FE338A6C6EC919EE343D8DF495D6EE1E2BCA6D55BD062107D473B3A176B564CABD87AE7B5975A90C63EA5CE0D720C8E6E48EEE187162F7C408C56D3A83F73B8B398B7F5DCBF93C259EBC6F8650378306GE14012C2997B872E73649E5F5B7E99788CC6956D7A02C29371AEF73BF8ED087715EE0F2C8D4C35792DBA9E407D67004F75165FB2CF7ABE712E473E
		CFEC775877091D1EA3DC8EB6D2EF85D23F5B132E0FF0FE2D1BC6AE0C43879F4457E3B4C4588B9877378179DFE49B443D87D08F72F640B000F3ED545C9B72E8BD4876186B9E8E2456BC662785508A607A6285FA0C6D1B857A0F96682B85127A3F3BD2751E14751C6B371AA557EB8616117DB71A527E6F893C48F9F3CB0CB79B3B3D496F459FF7651C9D387F432352FF496595643D36773D69EAED0570DBAC46B2611FF3F7158F73DF8EBA775A9657E940C540B0C0DBABF9741077AC67EFFFD8E5BD0CA921C9334AA4
		F9C7D4BA1D946361F25BBB7D18438C3073AC2084D086A8376C3E61ED66F6EDEC7607A7E82FEE5EB019641853171E4F6BFC773E173E6243FEB026B326C7503E14177870AC189EB305DC763A8F739CD7A7A6991FAA64EF79100B9A9DB745027B7E74F16D735FBB2E83FE33F7013D61591830692E466F1FBEBEEED0EB7F555535166C0AF1CD077F4150G7F81D0CB878842584D422186GGB490GGD0CB818294G94G88G88GC085B8AC42584D422186GGB490GG8CGGGGGGGGGGGGGGGG
		GE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5B86GGGG
	**end of data**/
	}
}