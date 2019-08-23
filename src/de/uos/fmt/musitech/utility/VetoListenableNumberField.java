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
 * Implements a vetoListenable field for numbers.
 * @date (18.07.00 19:34:13)
 * @author TW
 * @version 0.113
 */
public class VetoListenableNumberField extends VetoListenableJTextField {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	boolean notifying = false;
	double number = 0.0;	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == VetoListenableNumberField.this)
				connEtoC1(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {
		};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == VetoListenableNumberField.this)
				connEtoC2(e);
		};
	};


	String numberPropertyName;

	/**
	 * Constructor.
	 */
	public VetoListenableNumberField() {
		super();
		initialize();
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
	 *
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
		/*V1.1
		**start of data**
		D0CB838494G88G88G9033A3AAGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFDEC94479537790A53180FBF280214C4355AC4C241CD5C06C8EEF0DBC7A5ED9AD155C8B4F12AB4A2AAAAC8B5A9D2C8CBDB544E9DF1F002CB4CC788A4263DC2BECCB85F5967431883026D7379835BF00A0D63A6D6E06D9B5B8BF737476E9EF7F69C681B191D3D75F95746E9EA69B1BB3FB79F6F3DF973664D9CE87F3FF2CAED767D8859CA1176F75206501AFFA37440FB0507B8569BAD30A1439F3051
		9E54547D8A14A16035AFAE30D520BAFF995413D05F3E28407696FC6FC38D7E2483BEAC6689826FBBE7DF785B74737C64C3BE4FCEDA2FECAD07F2B75084E0B9C0A5D1B23F377520062F067AEE6D1B104D0E5092093439277620812F11D83B9014C9205198E9F33135D2639783368568DD409BE22CED904A5728F4EFD7B6E86D8E7E70C13472474D76FF10398C63F34A537A2E534A8AD4C88D3183DD3974BE14E70257C6BE0A2BE2D8D447FC3EBE2CCADD22226228B0984667FBF1D2ED95F1B804D04BF5A25F6335E5
		34AFC265D0AF2258A98F31C996FC67G65DF67B2663EFDC613417172AAD4367300250C7BE5A6DB031C12714A1F5E5C31C9A663A7294DC93FA4547300187CBF6A78E08679AF44A303D8E68AB0798BDB397CCE054B7FC2B599BF8475892025BA3E5CC570DC282F86FACE61FA7D2E1B6B754A5A47D07B77F75A5F3050EB2B427429D4D3FAADEB8E5B8FD1DF3A68A47E508F3C8920A5C0AB015681ED85FA077A706B47BF0432E9D075CB110894ED0A451A43A139DB8A61B0FC7BFC4051300F7110A443BAB1BF7DE92D53
		C49ECE657149BECA0869768962FE957BF59E3A3F730C3D54C437A539076C8EED8CEFBC251B9AA7F67BAE7507DE78A67DF681B1FC9545738DB8DB5FF394A775E2601D39416DFC7AA3AECBC6418328BF67F57BCE93D94A669F36675CE0B2645FC84942ECFC4CC5EC3C81F014A8309D842A818A8245925C463D476BE632F1C7D445F2D488774487E3022CB64745F0884B1A2DEF36CEE7EBB3E27D32E75D0F59E5F91DF10D02018326F6A95AF7561E1DE07638BF11328B1B7B894B39D7A7267A46EEFD8DAF51BDD11CE0
		EB38C347AFD3FC1381E7EB3B116EADD25F863C01E40166A315EE0297A6D97BD2FD1C8FA83E4E0033F19E226353BA703A468A34757D191B444BBC282F815AG349D28F29CE2A6D057B8DF63EF9DF75F66BE8A9A5617756DF79E0092A221ACF4C495D5088E61BEC196C900D11056C6F0923B8ECE6AB7CC7777904C510623D896D4D10A4268C28403B3A92C0D645CC35A4818F2FB45880689A2B14447CDB877C199969475D2ACA428B8550271EFB909CF5C907AE0C498G3E6F9DA7767A8D0D53E1784E9A67365ADAC3
		3075D05F265B65AF4E3DD0F602F363304F57A28E91B184F90C7B68A39EB8B8D1799730EEC045C00D93E06B893EC7DF6C673E38667243E85E3523263EC8684A174C2F12DF267B6263F40E9C408B015683211B85B647CDFE6686BC7D1A8C46710A3FB077D3CEC4C6D3BF567AB5EAFCB64767F40E9DA67BF43AB9F6D96025067DCACAE67FC19A679AC12F8120091BFC8DCED4BB18EFB68B8A665EF8D186C74221540A30369E97B9EB708D9CD5DBF08CC7C37021E83EF04A75DA4ACF07A55D074ED11C44ADBFB8122263
		81579E064398C62F677CEE4A97D4D5968763AA6E412AAAC6C7D27DAFD1D914C9A1D151F48D5591DD17020E39C0853A2E4F3B76997C2DCB122E45E33DE4A7E9FE7B9CBD1F0B207DA6FD7DBFF3119C2C6C962FE3DACF71B7D7774F30CE33A5B25764D87D2A6B6B1E4368EBEC0EA3EE22BB0F1F8C2B26D88D48532F6B3F124AE2C3D37D756BA0B647AFDCFCBF147E9F66E03E3056ED6D4B45EEF3DFFE5EED6E4B9B5C537BEA095B4CD7996FA5B751D1CAC48D3E3F4DCD626210940DE23A8175AE08651EAF7692BD4428
		28FEA206C6305AA30EE3B819EF919F5ECAE3EF86FCAF3D4563E462289EA71FD9019AC64D73AAEF3C43DE22799EA4BDFA1CE4B27D39066CA99C4691B014DF0AC7D5067F1D46F58DD7F6D02CF164BFD0BA2A2C72F927C055F2B92A2BBFF2D8B60ACA2AB8BCC65804619F9522A3386AD85F319A4DB7762125BB7B271FE7D89A0AABDFF11ADAB607E87F4CE48EC3EEBF0DB246D6565A30F95C1F5F1CE19E268C7D37CBD2542A2B428D41A5A4D497F5E2D7ABE1844867312FA8CAAD376BDB2D8BEECFE7D68431BCAC4991
		9CCA972909327FA719EA7879F53717781F837C528B541541E37A9735044FE33AF6F07925F018ED9D602D4EA07B620F741C673E9E4AE07767BCA899BFD1E746AF4F647CED26FC074E9F731A713DBA7FCA3D993FBD136BB16A29A0B182DF0FE3C8710891BA71984F53BCFB53F8FDC2B81E4A7F929E128B048D6799432F56196F59D89DB9432E69632BF52FA2124731B1D90C49934818B1D90C4071CF8EE84D0EAF359A75A0EF84644DE1A0135F0B979EA66BE1FD93C628A413666CEE7E56904BE467EDD6A69B6B49CC
		96FBD83BE7EBF83B06B92CDDF04E64F6D9F3C83BDD9EFE37F66B58910A2D85650E3E426FAF5D9546378B671213263110464AB9EC7C75F3526FF5EBCE10F151DC18E3AEDF3F976B0C39C51322C8C3A2CD2B3535384FEB0C138C7B3677F5A807C5D901BBDE88A779FAAE7792D9C95E2D415A1A2CB8FE96CD3DC784C222AA49BCF573A768FE6B157C107A3703CB7875E324CF4489445FD22A6B383D5E1D4B4F09D5670CEFA6C5AF375BF7D958E6E0AE3349445C14ED861A43F6B676C37A588540E7185B4B312DBA1627
		E34EF99C3B3B1EE3A19D9BE8AB53ECF0AFE06CCE166566F732F500154FAF303158758737874A5947A7EE639F3CC55E6FAC421166C33E4AC64D8E6A4295E87926EA53B7182235EEFB4BFCE61F707C141D3ACEA76CDEA026073D166B118FED3668FA0835646D6576E33E7E0662B17AF7512AC6CB7F2EDA40E4BA33A05D3F73CE92FB77839EDE407D7B6809BD867F7E98BC8AEC8211C64F27CE6CE0B1C52B7F6AC4A90FA90867F51E3A593CB9E4AC60F7474D8DA466AC05FA9E5013C06B01509DDC36017A995FD08249
		E95ED09E1915DC0EBBF8AE6C754E261FD76F77682C7A356B7D6E2D1FCD3F813D1F50467B456E18FAAF1DE70231FE0B5C6973ADCBEB3BB22D2E49B9251FAB2DDE1C560F2D335FC7E27D966015E795585E85EA848A81E5G5D1F4557FBC53DD99C1C3C5ED6C1B0F54FE43216CDF92B3CBD9D8FCC69172E636AACB39D671EA6FB6BB96015687A141DA4EF53FCEF85A5B12AA23E2F9EBDC94EE6C996C744A8479E235810A453CB833B3BF650183B9D46BCF8A79F7765866BBB4E768673BB0E7D14799D276C547477750A
		D3D6F7A0A65FD33AFCCE100FE11DDE0EB56A589A9D6B5531C76AB9B62AE3C9FAA6CCC0FD190E851D844B057AEA20430844731F9FF8034A2DA818C54A96C1959063EDD45FC4A6772C8BC284A31E8BBFC16FFC24CDC0157C353A3E5DBEEB5B767A4CED7B290F2C33A24565A11CEEBF41F7D067253C35C34591A20E164F517E2AA0431DAF3D7FF55A1F712C7A0F7B2C560869FA2E56282BC79FBB6BB47D7DC32FE7537A9862E7A6EE5A2D4F9D0806B487E6B958953A6E77C107F308E3AFD06C9E40949D9B246B3E9968
		340E7D16E257215DB7F56C29FA0EDD5265DE5CC66C921371B016A17BE757E16478867D9D057A49A670072DF7729C75FB8912578C4BC284546F0961A1340D369DF1725F48C2D0CF82AD834A87AA86EA3C0B7CC6F73E996973861C7F04B20A938E7DDCEE91A13FA30F21D55E9A2743F3C2BF17AF6FB31E4B83FF2D35BC17675D456246B22DDCBF5E2965EC3FF772B71AA26015E5936CD035B19FA7F255E9310871BBA771497A9F1B447FF2921F7A6DA47E43137825067ECC2FABC7F84ED85E7EA0AA6B3FE479BBDBB0
		1B6923EAE5E1C6A76A5DA85959B8EFF80D76C19F1F375BAC4649557A67E72767ADCF515FE1CBGAFDB48E360626663D07689D727E64BG86E5A906E5F50C2174D6254577AD4E2F767B486EFEEE0F425607D0C94DDE4B9C3AEAA1133FDDAB9BB2BB8D6704B7C0FCB1893C25C079C0C5C09B00366B3ABD5DCCE4A4279D085F242A4250A83DDD70C310A47CA918B8BA9B7B13D6A2A99B23527F1E15FDE00473605797872FC2B06995C674BE3F7493B975B8424C26711E7693FFA1EFD322909647699528879AC6C73437
		59B6422FD88467GD090A88414DC44F57A3465885349C41FC0E80444C672AB084A67BB9B300E43AD0119FFBB698E10BB5820242AD2040C4F711EG7183D50AD1D07BADA6C056980E62D115B555704F8374778EBCCCE106055B2C65D25AE616EB0246AD8C63750E4534B1B35BE8AEB0BC2CE0DD7F85ED95BA562184A5C5A496E77F87E1CBE7A359AF1BD129A2233DD3E3D45322E71F493D5C325F0E6C9D6855D1CC946BC02589B1240EA6E31AABD6B13F5E58E05C1F6B0B5F370CDF590B19BF67ACCE5F1F8D97E86E
		83783645FC3D333A2B27713F1E08908E2BC278DAD3B4C4FC31F9CC45291C646C85B23E425BE8CCED6DAF90392F9A7A7C97D0CB878842A86BD6078CGG74A1GGD0CB818294G94G88G88G9033A3AA42A86BD6078CGG74A1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG418CGGGG
		**end of data**/
	}

	/**
	 * Gets the number property (double) value.
	 * @return The number property value.
	 * @see #setNumber
	 */
	public double getNumber() {
		return number;
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
			setName("VetoListenableNumberField");
			setPreferredSize(new java.awt.Dimension(40, 25));
			setSize(50, 25);
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
			VetoListenableNumberField aVetoListenableJTextField;
			aVetoListenableJTextField = new VetoListenableNumberField();
			frame.setContentPane(aVetoListenableJTextField);
			frame.setSize(aVetoListenableJTextField.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of utility.VetoListenableNumberField");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Notifies a listeners of a vetoable change.
	 */
	protected void notifyVetoableChange() {
		synchronized (this) {
			if (!notifying)
				notifying = true;
			else
				return;
		}
		String newText = getText();
		try {
			setNumber(Double.parseDouble(newText));
		} catch (Exception e) {
			// if anyone vetoed, set back the text
			String message;
			if (e instanceof NumberFormatException)
				message = "This is not a number";
			else
				message = e.getMessage();
			javax.swing.JOptionPane.showMessageDialog(getTopLevelAncestor(), message, "Invalid Number", javax.swing.JOptionPane.ERROR_MESSAGE);
			super.setText(new Double(getNumber()).toString());
			notifying = Boolean.FALSE;
			return;
		}
		// else remeber unvetoed text
		notifying = Boolean.FALSE;
	}

	/**
	 * Sets the number property (double) value.
	 * @param number The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getNumber
	 */
	public void setNumber(double newNumber) throws java.beans.PropertyVetoException {
		double oldValue = number;
		fireVetoableChange("number", new Double(oldValue), new Double(newNumber));
		if (numberPropertyName != null)
			fireVetoableChange(numberPropertyName, new Double(oldValue), new Double(newNumber));
		number = newNumber;
		super.setText(Double.toString(number));
		firePropertyChange("number", new Double(oldValue), new Double(newNumber));
		if (numberPropertyName != null)
			firePropertyChange(numberPropertyName, oldValue, newNumber);
	}

	/**
	 * connEtoC1:  (VetoListenableNumberField.action.actionPerformed(java.awt.event.ActionEvent) --> VetoListenableNumberField.notifyVetoableChange(Ljava.awt.Event;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
	 * Returns the numberPropertyName.
	 * @date (02.01.01 12:32:40)
	 * @return java.lang.String
	 */
	public java.lang.String getNumberPropertyName() {
		return numberPropertyName;
	}

	/**
	 * Sets numberPropertyName.
	 * @date (02.01.01 12:32:40)
	 * @param newNumberPropertyName java.lang.String
	 */
	public void setNumberPropertyName(java.lang.String newNumberPropertyName) {
		numberPropertyName = newNumberPropertyName;
	}
}