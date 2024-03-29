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
 * Implements a text panel.
 * @date (08.11.00 04:20:53)
 * @author TW
 * @version 0.113
 */
public class TextPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTextArea ivjJTextArea1 = null;
	protected String text;

	/**
	 * TestPanel constructor.
	 */
	public TextPanel() {
		super();
		initialize();
	}

	/**
	 * TestPanel constructor.
	 * @param text String
	 */
	public TextPanel(String text) {
		this();
		setText(text);
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
				getJScrollPane1().setViewportView(getJTextArea1());
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
				ivjJTextArea1.setBounds(0, 0, 160, 120);
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
				@Override
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
	@Override
	public void setFont(java.awt.Font f) {
		getJTextArea1().setFont(f);
	}

	/**
	 * Sets the text.
	 * @date (08.11.00 04:24:50)
	 * @param newText int
	 */
	public void setText(String newText) {
		text = newText;
		getJTextArea1().setText(text);
	}

	/**
	 *
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88GC785B8ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D8FDD0D4579527ADB3B52D1D249DBAE3B3CECB944272E1C5A5CA8C99E9EAD37F2089CD50D04734CD47F1E847D8BA4609FFA41DCB96978D2028CC6A871D50449190D8D8D8823BEC8C88A460C73A0D9BE2B20E6541BEE0636E3E4D6EDB01097DB8675EF75F3E0537CBF4A2B33F39FB4FFD775EF34FBD67F74E05543E19FC206C35DAC24A8E90652F3B0C10252788C9CF7876BAAEBB185CECA41A3FFFG0E
		12313365501E00318A5ACF2B2C0276B0209DE4585E063606EC2ABC2148DD0A1C236CB5C29C8F61B75BAB6B94D9BEFCD30C73C0FE6621487707684E276AFA147E094713C84A266A0AEADCC733A6C722B257203404FC5764DC01FB992B2A14B93F073193A043439B491FEFB4C36BF05E1CBA6F964DE1A9E41E7449E6DFB864114509E9F3D876F8BD721C5D6E92E7E52760973D24002EF7296AA01DCFC892742F2DC459379C38C78D7CEE847481187E3B2EB8947D4AFF1DC28CDBBFADBB92C37F65AB195E1BD7C67447
		76A435F5F2835A1424000DGBD006B005B00650053B0F41C241DBD87ED7F38BCA079FC123FBF903860F5872FC8EE518B3F6DF698D1E49F0993D250CD883B47C3D5ADBA7A44831B7737BB1E47ECE26864F79A587EA0699BFA41D421E3137C6387ABF2D218AD8AD2A2B6E1FBBBE36E3DBBA55A6722777E04E877CE3AD4D1F1C0E76F042C1DA6124A5629CD0D6CBD180AF77DB23DEBAB7CAED37C00494B5EC579920D1C795FFD74FB6CAF0331D7D239EF741E673AA4645DCF0613376A5AE1FFE209E96CE126436C4391
		DD18DF24B7E19C2500BC9FD08CA8855424B59BDBGFDE95CB7CA4F36FDC55FF08AC179C25863F50BC1426626B73C81AD84C1D0987107E441BFA10E8AC10FB06E95C3C479E6ED836ABE9EB5EF8375598958638351AF868559A379E1F541A74AE2B0443E49EFB80A5F84C5BA6A7278C4584097A0FC5DAD8D4721758AA1796300DB10454897EC3C3081EDF9CB980595E181B5DE361DC31B0F4179E781CBD4BBDCEA4475AE09C188E73B7D32A45D8C87DCF892D21C06F7B5C5E3FBB97C4E8130351EE9C6D91D2159E8B5
		70F53E59F8CC334E20E782559702F3BCB6DEF5A0D78E9B78DD6F39466F3A605FBFA2724DBD3AF70D98B3A8F7ED186F73956F621A28976B5F6F38F6077147B1EC500F1105B1EE386BBD0CDF619BE65F3ED63C2BE450A98F30C33571630D754CE7AF88A111FB69BF0360E022BBE2F1766D3AA664D57116681787450068F7430F1062A39B1B8EC57CF7D2D2FDAB1F4A83606F8360E0A1D57EE4937A24A04BC14FF8D8962F0A326C71CFC5461F22FB05426301C828703D36B6A11FC8B3606F6A775B1B0E51B8707BC52A
		34BAC5995FC1FDD5740ABED0FAC08A7BE5A67F43B93C6F1B621C92AB3B4E19282FFB4398F7AC07E58B15F806204787264050A5036968477F2D62B99871925894F3718998EBC967BEB973366A134F7D10B4CE6BF36119F0ED4552F466AF4969911FE4BABD572C2F7B73B4E694F908697A058B77F5C17C32EC4A6FFF4E0CEB64435AC5005D697C7EBFEDB92909B1656287A42F1719D1356F7B2DB5F89F6297E1912889037912EA33AB2D47660D0DAA76E3639F37227D3C9A9EE03A6EEDC27B515A60F36116B0EB8E4D
		008F586D97296A2C8EA821DC015F17E770BA60FC8F1E4581FD97401B41ED1DF50A5781757BD249FE538B2618F5CC06D247E4685581BFEF43BDCBE0ECFF8637D3E12B16537AC3A1E942C369D4B9637F4C5A3BE032C4736B50CEFA02A1F98402E5165B63BBE6549779D691ABBC706202FCDB159961107A4C05FC42F262DAEA8FEB667CDC19C765031973F365E08F4F15AE984B4D649CCCACF89FF5D9D07BG1C00E9409200A10B5B61D8DBA7D9A4470D78A17578856F5528606539264D4862CD6997349D607186AD4B
		8F6555E84B4D30E7B1024A1EBE0C32D720DF3B0AF3F3EE8F46EE0F5AFF02761DEA7FE24F5D722A9638FEEC6E1E78DA760845634CC607AC31F93746224F3BEFD874F9374EF21449E18DED9542474FD86273F20BC50F170DF46CF435097A751C9416619D308A6F2D231AF3248C7D25009C409645260C87564CA0BFCCC25182DBDD8D0893A46F27B8F757E1BE37917A8E40F540ED40F2G5B733E0F5017AFDE1D8882E7A1EFD898B76F6D51F2F379EAD6EB34C3BB88E0F170D9B5FF33DC87D952EA7EE659E546332A3C
		E496E6E43B7D02045CF8199D0EF1538A6A27B83FE0B56726C23AE6F9F633318EE04D665C748B750D32AC6FA7E4590D3D3A5CD474F62169F2B633CDA0BB1A1B581ED5C7781ED9B03EB39BE576A33573FC69AFGB3000DFB234671FE1B2246FF9CB50E7A7617E9478B23468FE86633330D3D454FD6B51CC476FFD29F737D5832061D492934576B5EB3C57CE0EA686FC4B79FC4EED735C3330D3F8F172C61369F36539C877DE2C0699A4E83E355F77776725F653C34BB1E4778FA45E1547F60DA4657E52ABC134AEBB5
		F2663F5BD47F2D073144351C4777F4206F17C03FFC9D78A1408AF081826BB80F7F4C52CE9661F11794388C2463555084BB5F51A3F777A6BDFC0D7BC97E7B4B480E7667CD15B17CA4F19D730F9425ADD27C041D6F9BD6BCDF910C1582EAF3205E8274812E6570739D6AE8E1679BF2FB64FED996A626E9E666079B7508B391B1128EBBDB3BAD5E593CB931F2F09E753D041C79B978C9AAFFA0E7FE8E7E504EF3F0B20C15BF42FD55C0C31E7DDE23793D237A6B48B3717DF65FBD5D035D4B4B9D31F359DFBB74F3D9F9
		27FEAEFB3DB3FE2EBA56997B8DF132D37B06E0FE754BAE6439E9380BC400811087A8FCC47D5FCF67DB4C27F47CE948BDA58EC87465AE2BF51EB576D93FEFDD7C3D7F20956B47F1C916A59F2E4F6549D63CAFD98AD0A11325DA51C6C14F54344CBED56419D664C92FB8C9452C167A4046EB295D2A5FBDE1277FF7DC4F7B85345F2E76DD76FB659B11BDFD77EC8F23EA0F273BE25F49362E456F64F7DD344E87DB3A6682A2136D6C423AC71A1C8C096A5D7F312BC6150D041CD248030F8577FA347F8DDBFD9CDD1D6B
		9947307A66CF9674010B786F517E20A8D80057699A2F5A35B9B98BE4D877962D67F55117B654E9C044DA1C736FE987675F444F12C8DF69B693460F9E7F36EFD0727306689A046569DF7DA72ABE0A64E92E2512232D3DBCC7BF300157C10F3D27FD23656FBBDE91AB87ECD176AE592077C6DB550DB6EC4C05FA8EB08DC8GA48132G790062DC9E37DF3E53886D2870391265B0A5C8C19A39A0E4059D0D5D2060714B4C97ED3D717240CB398B73G536B2C8D6F6560234D46B3G874085109F653AECFEE751775350
		EC1C77D3EEF7EC7F5D543D387FEE69566714273A97F24A335D7A1C721BEEAD27B059EF15F551169F8ABEF55D1D5DD88B32B7419541AFCCD13EE5BC646C65BC14245A47EC0BFD3EAE1BFEFE6835696707FEDB7C374E08ADD67EE06F0F29DE7E0648857D8AG3B81D59B215EGB09F7FDEAF2F47AFEF647578E33D716B7197FB675763E5D163EFBA6257637FF24C2F4779FC663BBF086B3BFA78BFD0CB8788BDBE36F45089GG349AGGD0CB818294G94G88G88GC785B8ACBDBE36F45089GG349AGG8CG
		GGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8A8AGGGG
	**end of data**/
	}
}