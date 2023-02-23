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
 * Implements a listenable JTextField
 * @date (31.05.00 13:09:13)
 * @author TW
 * @version 0.113
 */
public class ListenableJTextField extends javax.swing.JTextField implements DocumentListener {
	java.awt.Color colorEdit;
	java.awt.Color colorNonEdit;
	String lastText;

	/**
	 * ListenableJTextField constructor.
	 */
	public ListenableJTextField() {
		super();
		initialize();
	}

	/**
	 * Sends a notify document changed.
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

	protected void notifyChange() {
		String newText = getText();
		firePropertyChange("text", lastText, newText);
		lastText = newText;
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		notifyChange();
	}

	/**
	 * Sets the document.
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
	 * Set editable.
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
		D0CB838494G88G88GB885B8ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D7EBECD4C5946E8FFE18C882A6552011245887FDA190C9A4DAE3B50D29CA8CA420042801241146A8B68A8951BFD3F65BAD52EEDB5AE055A285D12B3BEC57363434B6C0A90516C2C42925A6A4325D1D36575E5D3B5CBB5BC752984F39B3333B453BBC925C644B5CB9E7661CB367B533247EC8CAD9693EFAC2CA4B0878F51692D2D9C3481160451FA42DA245E5A3F13F2300DA32245A8EE383709C667C37
		2ACA981DGAF50F01C073106A8D58D02BEAC6892257B8839398257AC2AEA9434FCD833955088F46F8A3EFEG467D26662F9ACF09F58DAFA41334972B9CD5A8A7CE26C426583BC10C04BCDC4E1EC2DD2BCCBBF10F9D66BDGBC3C0DFCF33883461E2BD39327835493518C4FF810F90291C3E154BF6909B0C5D558DCF777E545E0B4649BD36925E1BA4BAEA8D48D90D2E24A5ED3D5E14AA2E4BD4C67D31116EAC3FD6D70FD9990C815743F6DC84F076F2D0012D4F946772E7408B358B725114CADFF16D6A7B8E3C59A
		BF1BB7ADF6C69C3FB463116CC63F2B40DB8AC88394820A819569AE5B57402BA3EFD6E310FB47D81F968CEA215EF0781C9A502FE8812A42F7F7B7F0846DF76A57F4B8A70F75B3D5EE8BFB6E843E6F257B5E47FD1259A6639E5E7684B95B3F235CE161137C3A8AC7D3BA7745297418CF386ECF936A9ECDDF18178BF5DFA7713A13079C0EB28B5DC939C5652A10139427FBCDBA467A8BB356A970DDAA7203530F1A745DF1F41E23D9E64E605C1E6132CDE6485C78653434A5A9EF8999C859E269073D0B0A4BB7E4F09B
		0AB2EE3765DDD3E74DCA2E33B4EA4BCE135E98C76736046D32DE1A0137984073EB3987EB3684661519AE5BF1C08F6086E09630B8D366D8112B659EF36C2ACFE767A20A9A20BA61FB173B8F4188D5267B86C38673057CF44427ABD8FC8691EBB25C680331857BF24C5C77030E0BB4C4F59FD33490C87785A9233A41572CF357629A1D1A5CE1A5C8C1C1B0CC245CBCF79D560F4FE0FF0483BEC6E3ABB83F400DB119760D00C490902D3B57DA50DF39F07E824066289F4E1CC0F9C3D48FD1953A0826CDC54243F89252
		1405FE2EF5E26FA80675A4CB7A7895AF5282B01F4F12F226CF9C0C13F3DE712379BEFDCE5658E1BB766525D9B2E7F60D4A1C5970576304CD6D324C99441AAC1EAB05D937574E8F26CC340B4FD75BC6AD76AF4D322E237B8557D1E8294376G6447FAE4FBAB462A846C2E4CF6595C595247EBCFB4711CBD67B3284C52DFF5C8B09A08F91C5BD9E43A06FFFF9877BDED37327FC1016BD8723F6A603ED869417B07CE5390BBCF43B494008FC3701E76E00E097A9C5722FB561974B054F39F94109125BF67411A73B126
		ABE391C62FD1461450C40C7F32294B080C05E32DC06AFA5503FDD71B01FA0E2EFF4303356057C2A1EA86A53AC570B71A35C8D59A84237B34C80809FB2F8583B5C567C4AF5856D2CE3016558876957E9ED863BB00E75015A0049ADB93F9A89B6B3459D99F672373C043F78D4B1675B673ED345E5EFA14B4CFDA5F9747A3750E0D5922C7E7476A0D5B733A575A6ECDE6BF90F4035B19B60476D83DCAF80E875BD016BD47E5EBGF8F3E40EDF799979320F08646E53D4153BB26A636E569A0C893D952150FED143D054
		EF3D2D87EF630D889FF27EE0AB7AD00D6BF55C66026F0E9171563A27F755A3CE792E9A4811FEFE7E0CFCA7B56DC9A779FB6A9C87AC7CEC5E15B95C3FA9B9D66F24E75BD176F66015C4FDD3509A5F2BFB8DC373AB66B5A14EE53445470153E65BBE07F1DC518DB688C5B2ABFD70CF9B5A0B7708A00B5AFD4CF9273710BD773F3D145B7B49C90CF783705B811781814022DCE9FBF9FB9B394BFD5BBF8B57E04827DEDBD0E8725EEB37715A90737C76B2D99B68EB732EDAE546A38DF466C575FEF6B2F11F5857E15DA7
		AABA2C7B0433231653C1C67C2BC0728FF5A46AA3BC3F564EE0AE0C43058F64EBE16AA7EC955ADD60147FC536035D3B8175C0EF878C83F82E3DEF1E8D73A88FE83B4DFD8D5571B50F7EF181BCG4E3F34000F367D3800BF3F003FDFA0463FE50B6717456D6739FE73184C754A01E43277FAD3427FBDBDCF733C39A9469B0DE74AE37745C7F74CB9ABF07D4ED129BF7F42B27236771D72C43536CC68DDAF464DC2BF4F5D42CE74FFB1706C2BDD36E640D940B0C0DDAD73A83B434D7337BF2030DE46FC7EC9334AE472
		0EA8F4A6C64660F2594EAE74E19248F992108F588CA80E4A5D55F10C4B3510599F1820FD1A7942E4B247B6F5A64E672D1DF7FF176E6844FEB026B12685D13E246F6C44D8B0ADEC92B96D03CE74332ECCCCB23ED450BF6E44DCD46938C996395F15582E43DDF7376B7BAE33F7013C61393010696A427BCF9B9FB7E8747C5E2E1AA8ED50382A417FE0E8007F82D0CB8788713F65BF2286GGB490GGD0CB818294G94G88G88GB885B8AC713F65BF2286GGB490GG8CGGGGGGGGGGGGGGG
		GGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5C86GGGG
	**end of data**/
	}
}