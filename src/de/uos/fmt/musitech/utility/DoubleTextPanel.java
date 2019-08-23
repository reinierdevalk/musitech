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
 * Displazs two text panels side by side
 * Creation date: (22.01.2002 19:00:04)
 * @author TW
 * @version 0.113
 */
public class DoubleTextPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTextArea ivjJTextArea1 = null;
	protected String text;
	private javax.swing.JPanel ivjJPanel1 = null;
	private java.awt.GridLayout ivjJPanel1GridLayout = null;
	private javax.swing.JTextArea ivjJTextArea2 = null;

	/**
	 * TestPanel constructor.
	 */
	public DoubleTextPanel() {
		super();
		initialize();
	}

	/**
	 * TestPanel constructor.
	 * @param text1 String
	 * @param text2 String
	 */
	public DoubleTextPanel(String text1, String text2) {
		this();
		setText1(text1);
		setText2(text2);
	}

	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanel1() {
		if (ivjJPanel1 == null) {
			try {
				ivjJPanel1 = new javax.swing.JPanel();
				ivjJPanel1.setName("JPanel1");
				ivjJPanel1.setLayout(getJPanel1GridLayout());
				ivjJPanel1.setBounds(0, 0, 160, 120);
				getJPanel1().add(getJTextArea1(), getJTextArea1().getName());
				getJPanel1().add(getJTextArea2(), getJTextArea2().getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJPanel1;
	}

	/**
	 * Return the JPanel1GridLayout property value.
	 * @return java.awt.GridLayout
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.GridLayout getJPanel1GridLayout() {
		java.awt.GridLayout ivjJPanel1GridLayout = null;
		try {
			/* Create part */
			ivjJPanel1GridLayout = new java.awt.GridLayout();
			ivjJPanel1GridLayout.setVgap(2);
			ivjJPanel1GridLayout.setHgap(2);
			ivjJPanel1GridLayout.setColumns(2);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		};
		return ivjJPanel1GridLayout;
	}

	/**
	 * Return the JScrollPane1 property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getJScrollPane1() {
		if (ivjJScrollPane1 == null) {
			try {
				ivjJScrollPane1 = new javax.swing.JScrollPane();
				ivjJScrollPane1.setName("JScrollPane1");
				getJScrollPane1().setViewportView(getJPanel1());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJScrollPane1;
	}

	/**
	 * Return the JTextArea1 property value.
	 * @return javax.swing.JTextArea
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextArea getJTextArea1() {
		if (ivjJTextArea1 == null) {
			try {
				ivjJTextArea1 = new javax.swing.JTextArea();
				ivjJTextArea1.setName("JTextArea1");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextArea1;
	}

	/**
	 * Return the JTextArea2 property value.
	 * @return javax.swing.JTextArea
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextArea getJTextArea2() {
		if (ivjJTextArea2 == null) {
			try {
				ivjJTextArea2 = new javax.swing.JTextArea();
				ivjJTextArea2.setName("JTextArea2");
				ivjJTextArea2.setBackground(java.awt.Color.white);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextArea2;
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
			// user code end
			setName("TestPanel");
			setLayout(new java.awt.BorderLayout());
			setSize(160, 120);
			add(getJScrollPane1(), "Center");
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
			TextPanel aTestPanel;
			aTestPanel = new TextPanel();
			frame.setContentPane(aTestPanel);
			frame.setSize(aTestPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.show();
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Sets the font.
	 * @date (15.04.2001 19:50:52)
	 * @param name java.lang.String
	 * @param type int
	 * @param size int
	 */
	public void setFont(java.awt.Font f) {
		getJTextArea1().setFont(f);
		getJTextArea2().setFont(f);
	}

	/**
	 * Sets the new text in the left column.
	 * @date (08.11.00 04:24:50)
	 * @param newText String
	 */
	public void setText1(String newText) {
		text = newText;
		getJTextArea1().setText(text);
	}

	/**
	 * Sets the new text in the right column.
	 * @date (08.11.00 04:24:50)
	 * @param newText String
	 */
	 public void setText2(String newText) {
		text = newText;
		getJTextArea2().setText(text);
	}

	/**
	 *
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88GB085B8ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8DF0D4559546B6ED25E5464C942758B2B5CA00GC183C4FECA0070E75396C6F4D03103CE1C216AA8C52C5846160EF73349E6839B9212D5A306108ABA58E0880529B4847237790FB51294529A11175D17E44DE65F7276ADC9C4E84F39775D37EF17379BCDF5E74E3CF74F7DBB775CF33EF34E6E927B2138ECD3261D90D3B6D1BF6F1B88A9FE0790254673B867D9634A4DC477798B50FE7220B38B1E45
		5057323E5CDCC03EDBED0576B1E8772EABB73F8E6FC5445FF364883CC458E777E1C2AAD6355C9FFD1FCE935FA707362F1D3541F39FD0A5707079C59C6E7F433345AA3FDB65F3B2E59262388B475CF336C465254218ADC04540BFF6D7F0FCAE5D79C049E935DD3CE2AA09DF151713076B685664B4C51D1B24BE8979012567EE5C6BD1AAA74EF9867A4A000707B71311EBA760D95DB9B4F04EA5D684A4FFC57F30D2B19C703B9551B9D891D05C9E37B2D6D5651082FD9E31DB9CD5BA852F6801B91B693A074ED969BA
		04CC0776659948DBF69D77AA0677CA2096A0F60E27BE28D6654CFAB01EA4BC7CEFD3FE04F3444FE072EF1891BC87BE5F20BA0F4DC45DDA63C1D7C035C0AE209820F820B769DD7F4874D7F85675A98D527030642D73796ABDAE798349A5FA603D2A8AFAD45EF951A949AEC258FDBEFE761801BC51084DFB6EAB4FE3BAC9B8496F56776B9F135A46A7ADB986BAC9AD32662444B3DDEC0E8F6A046DFD64DC243DF745075ADE685E9709FE6F296DB9B95986FBCFCA5CE6314EE46B144C8C6E5DB1936F7A3D9BF8575570
		EED26D00715F277CD89D1F5961AAEA9B580E033E2CD95CB64E1C6332CCCA390D3444BDEC28070C18E7AC0333188C13E685E5E1F6F1C7967AD3A27053005201B2000AE7171BCF81354C663611EEBA7EA5ED23D310157A005B6392E542665E11799ABC418DE421556BD7842FD33CA048EE811C43CF54B1F7E7226CFDA173E6D31BF5429E4D22D7148545ADF9E1F5E1D8D4C4594F46A4E56E47B132C8FB3B5D43A2ECB06CA3FC5D2519C5707488FE65DFBE17200841912CBFB593F5F9D538GAB42821A3F5C1F0DBA9F
		0473CF820A5574F0460C6B350BB2B8F4D5D507A48D85FC5DF89212BE9B6F6A8C756DF8F8CF81E2EB2D31A06FC8C23939BA012FA31B8BF56BB85CCE94DF104738EF1C3C01185919406FFAC78F3F6B8D1F5EC114219D06F70DB418205EF5C2384D1F25EB22DC2C3D7EFA4FD774BFCE1D8946BEF2330FBFB161BD4CDFE28C53EF25956FAA81E4DA8734CD5371FCF3993359FA41AFF2AB7DD0868393DDC10D3331B33290D7452B22D7F108BE516B02973FEAA3F333F203765BAFE93615C479BE307786B0B03F46DF1C05
		B6A9A80A6C6E8BA8E21728A8EE6FC030FFA55D4B9F687385DD056F35BE8B71C49A81FB57467FB22B107A0157ABD22135A9EA7F86EA2B22C79C86219B2400D7E17C0732713E0744B155D79E4936D0DB7784506FD8ACCB92766299E477B02882DD17F44CC1BBBEFF0E47E2245D20D30C49E550F7EA8E374911431ACDBEFABBB9BAE80C05C782761C29F3183DA44C895AA413696796E359B7D21FD179FEA66B15EE5C379B7C1747D3F67F6EDCDCA38D565E8A34EB8E3F7F561CB7F4BE26DEFC036471B0B5EA7A7D3B35
		G6FC33C9290811A50195BB51D1D35960675DDD075477A9B2D28BF0F8E87182CAF3E0B7A23B942E742D5E13442BF82B6D0D555C6C5E7F9C08A458A9CEF1B4B7300E76A70AC8EE87782A9F3392E938F70BC206C2519A4437224A5E2BEB3D74DE7669A65810B76601EBB210F4C63FACA356AB12D4E6F171CEE8A276A9925DC7DDDB01E123B871E7DEE592F3402330CF2FDFC1E0B72A25E2AEC9587D65F94EF6D730298F2F45E4DF842E2E2EFBD0D157342E3E5BF65F74C8B0F15BB6AF82C6C053E54F99C03F76E457B
		B81288398FD08F108FA896A8A9116BA1F34FC9B2CE0CEB75C268718A1E2E90676531E60F19791B5ACE59134D7D8D1EACBEEC21F73E8176CCC7223C7BEA1117B53F5CDCB21FE3F3DFBD7AEE2D567650F60F565ED5B7D1DC55932E9F993B736A3F0EBDA261B853514BFBA363AE3199632E45E60C3BB65BFE4607B574D9886F375B2263F2094D88174D346F79F396EA57E3D2C001FAE0BE5E1B2506E36497501E8A148234E9BE0B7D8C87960CA0BE74C35282DBF579C4A7D9F78F4E5DD04B67D6C25B8164820A810A87
		E2FBCEBE0F365C566514813390B7CEB0ECCE2857E3336DDE365FE9F8F6GB1BF282861350B8BF8536F6572BCD501E755F029C298D12A2A6AA544468EF6B806CDD7EB2C6A7C4D77F2EC0A27EB5A124A4DC7002A13B8B62D57EA14E9A9F712E917DEB444264D07B7D93A13183D0DA605E2935B73F735FC4FE4685F1E043C32BAFB18AD3DGD481447A4F0774637D3E9B529F93520F725619747D0BC37A33F5735959AE3F454FE6EB19CAB2AE16C52CA3CFADE0E76AD11F3DA5B5963E26198CB49EA40671A0F83B1A9E
		526BF9FD983B006B7E4F8DB446C1BB9DA8E3814701D3B5932B3D6AA7B86F5AD71E47707AEAAD4ADF30106135C9637F1772CBF4FCE63FF3B57BBD8AFDD396F29CFFA28FEDFFA734ED0B408E012A01FA01C697F19CDFE02BA46360F837646BG507168E0021D6F8F35932BC97775F0BBC9ED1AC636D6BEEE599B41CE26ACE2761128BEB72BF6424EF7A59F4F37957AB200CA12A15F82EA813A1C4C4F77326D98BBDF234B2D54A90A609C2411199F6E02DB9C893291F45859FED39F6DECCAF22498FC4501F7F1EBF2F8
		8C3EC679531243E3305941E3F08274596E63367AC5AD516C68BB3A77241A2FA34EC4375BDFFC23FB30FBF9BAAFF2AC5B11E79C4BF665994732BF66C50FD5197BA25790397B74B584332BA58508F3BE380BA9C0C9C06B00365C27FD77336FADE6D3867654689A909BA4DA39ABDA1E179F792C37640FDF6FFFBF9F7347BEC9D124E1DC1F73E373713E9449C7190CF7FBBE6AC8F68F8CAAEC284A7FC9BE6224C76C27EC16CB6D2E673954AE4D6EC69C18BB15AD666DDBE87B14564EF5C4338D1C777F5B5FDBD17778B2
		356E7876372AA0721D5CDFB07E1DBCD0C073FC50E57718CFE43C4D851877C87D7DFED13B7B478A8AB4DE2B3FD3723B31D870ACC67D97B514C51133C755A54BEF2AAF21EED8C5F4C22DBB97BA426BCE4CF913E1DE5AE21E97D5B620CC8DA266629CFF5F2C66789B731F2924F667A396749FA37CCDD7E530ADB14AC11EF5701A2CF6890F3D4F36A21E0DC2BB96A8E1890FC59F9562D9D3213D9508ACADB797839D83F2G75G7916F27F9A5AFF941E97044F54E80450AD495443004992B06AE3326076AA4CE61EF704
		63751425C1DB0CDB9A892FAF35213C49CB99AEE7EBFCB7652769788C2FF75273E1FBAB7455AE63B27796C636A7E761787664A9B476F13970E69F9FA9B476716B05FA9FE73CEF296B224E7EA98CEB6B5EDA0839994B51BF903C42G45BF068B16C6DEF745AF63F5D7F3B3470A34E59C8BDCED485B8E6D2C1FF11EC4F9A5502E56F83FEF9DAF1E607C097872F6CD167C367176481E407A5C7FD88DF33C117BDBAF482BGCD87CAD2E5E7354A25E6BEA68D78DB01F68315AC87FDAC67BE796C893C13B67C3123CE9605
		935C478F3505707F867942F2DC732EB6FD5E5D8BBC446398F8327EF75A0D7A5317737A66E2A34F8FF7816FD88A2FEF9E7894FDB32C3EE9165DAE353A61FEFEA735875433AB057B792CA6DCF3BA341301B6G3131B14D9C933625703AA425B9FADD3223B93CAEB105741735C42FCB4E370457A5FCBEBB67466BA1F5DC301E601AD64F39250D5792A5ABF83D363CC67FDDD26ACBC5B911F2556E954C8E1495C6D879C9BB57CB728AEE331FB4CE2C2E981A603C7828733603DCE4E5508F6D704E3070C0B35E75511561
		7548E14ABF35B23C9EC9EC6276D68BFD71AB79F9A7B76B77372E823FD8955D6F555F4C5A2249DDE930860BB92F5951AE9C50FF99E894A896A8E995476C5222F13F43EA9CB57C8ECB7D5E29BD12DC1BE00FED86F2B1ACDDCE7DA9837A2D299CDF4DCD94B721FDCC6335D3DEAD343BD3398E4B1A092617F7F56F8B1B3E793A60D75F689E6C4CF194BF86613CE7CC1CB71372A6816FB9ED5C2EB69E0F2627EA77E90F9C035F369B579465F6631A6204BD7A77E37F30C72AB918FCE3545FD3C136AD5AFDFE6FBA17F9FB
		AA0F858FB5E2FEFDF5C070152D265FE9B7F21CA8D84D479CE842B1035A184AA6BE46250D69EB42BBF2CA1E403057CF5838B1ED1C321AE33E708E7A265B8BE928603968F6A9030CDFB21A9764370818DBC0BB062E3337DD1FE706935BBF361D669902F3E8C01682DE97D1E5C820FA58G6FDB0058FABF3DBE7EFA2FB4A2FEC088D4F356CF0B747FD1687853EE0B516FA7C8992B551CF50D7AFF0957EAACEC4D66A63E26AAF3D3689E131D7A6F2F6C9FBDE4785D1C527D1845212EBD38A6946B272F417FF444624F44
		6A9E7F3D0E984443EBC5036411640A11C137A2B20CBAFB9075138E73F62F61F63C51DECEA2673F1D326493E5E50CF169AFACAA5E092528571235656653C03DC08A502DC0F1EB795AC52FC64B2DD9608F6656CCAF7927743A5F79512BB9A6833DA0A52FE57AC8DD9B7E5BD57D41FDFAFFE1E22B3E54FA509E34BD7767746714AE98689DE07D5E5248FE9DA89DBF373ED1EA1CDB47145E1CDBCFAEB54E2DEFAB556756CC5F9B8F912D7F502137A3699542C89B5C7F07FD1F09CE25DB9800F16C7FC2BDFD782723445F
		12EC3FCC76055A08E9666B5FAE386DE4A013D81B0825DF70788605D612CDFFA069A0FB7B2804D886F994D25001F30F435C63F46E198E126974AB644DA05F09FC565B65931CA28C49F5897290DD618A39C1CC9F135CFEF9200FEE20104C0F1BBE06EDE98B3B4D9DE40FB2085FECA820EB053C9EDC3A938DC56DB8B30A7FA6A02F4047C7BF1AEFBDFDDAEFC31B363C9051FF272CE7369337BE7C7F97E9AE3C032D404FD84F6D395875DE943DF78D8B9E0FA2F8066A3CAE3C037AB1C58CE27CC6972E6F67E354CE3516
		F7652AFE4B677C8FD0CB878898A9980A6E8DGGC0A6GGD0CB818294G94G88G88GB085B8AC98A9980A6E8DGGC0A6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA88EGGGG
	**end of data**/
	}
}