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
package de.uos.fmt.musitech.time.gui;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.uos.fmt.musitech.data.time.TimeSignature;
/**
 * Class to display the TimeSignature class.
 * @date (01.04.00 04:36:09)
 * @author TW
 * @version 0.109
 */
public class TimeSignatureDisplay extends JPanel {
	private JLabel ivjFraction = null;
	private de.uos.fmt.musitech.utility.VetoListenableJTextField ivjNumaratorField = null;
	private de.uos.fmt.musitech.utility.VetoListenableJTextField ivjDenominatorField = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private java.awt.GridLayout ivjTimeSignatureDisplayGridLayout = null;
	private boolean fieldEditable = false;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private TimeSignature ivjTimeSignature = null;	class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == TimeSignatureDisplay.this.getTimeSignature() && (evt.getPropertyName().equals("denominator")))
				connPtoP2SetTarget();
			if (evt.getSource() == TimeSignatureDisplay.this.getDenominatorField() && (evt.getPropertyName().equals("text")))
				connPtoP2SetSource();
			if (evt.getSource() == TimeSignatureDisplay.this.getTimeSignature() && (evt.getPropertyName().equals("numarator")))
				connPtoP1SetTarget();
			if (evt.getSource() == TimeSignatureDisplay.this.getNumaratorField() && (evt.getPropertyName().equals("text")))
				connPtoP1SetSource();
		};
	};


	/**
	 * TimesignatureDisplay constructor.
	 */
	public TimeSignatureDisplay() {
		super();
		initialize();
	}

	/**
	 * TimesignatureDisplay constructor.
	 */
	public TimeSignatureDisplay(TimeSignature ts) {
		super();
		setTimeSignature(ts);
		initialize();
	}

	/**
	 * connPtoP1SetSource:  (TimeSignature.numarator <--> NumaratorField.signatureText)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP1SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				getTimeSignature().setNumerator(Integer.parseInt(getNumaratorField().getText()));
				// user code begin {2}
				// user code end
				ivjConnPtoP1Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP1Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP1SetTarget:  (NumaratorField.signatureText <--> TimeSignature.numarator)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP1Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP1Aligning = true;
				getNumaratorField().setText(String.valueOf(getTimeSignature().getNumerator()));
				// user code begin {2}
				// user code end
				ivjConnPtoP1Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP1Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetSource:  (TimeSignature.denominator <--> DenominatorField.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP2SetSource() {
		/* Set the source from the target */
		try {
			if (ivjConnPtoP2Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP2Aligning = true;
				getTimeSignature().setDenominator(Integer.parseInt(getDenominatorField().getText()));
				// user code begin {2}
				// user code end
				ivjConnPtoP2Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP2Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetTarget:  (TimeSignature.numarator <--> NumaratorField.text)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			if (ivjConnPtoP2Aligning == false) {
				// user code begin {1}
				// user code end
				ivjConnPtoP2Aligning = true;
				getDenominatorField().setText(String.valueOf(getTimeSignature().getDenominator()));
				// user code begin {2}
				// user code end
				ivjConnPtoP2Aligning = false;
			}
		} catch (java.lang.Throwable ivjExc) {
			ivjConnPtoP2Aligning = false;
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * Gets the editable property (boolean) value.
	 * @return The editable property value.
	 * @see #setEditable
	 */
	public boolean getEditable() {
		return fieldEditable;
	}

	/**
	 * Return the Fraction property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getFraction() {
		if (ivjFraction == null) {
			try {
				ivjFraction = new javax.swing.JLabel();
				ivjFraction.setName("Fraction");
				ivjFraction.setText("/");
				ivjFraction.setMaximumSize(new java.awt.Dimension(3, 15));
				ivjFraction.setHorizontalTextPosition(0);
				ivjFraction.setMinimumSize(new java.awt.Dimension(3, 15));
				ivjFraction.setHorizontalAlignment(0);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjFraction;
	}

	/**
	 * Accessor for the propertyChange field.
	 * @return java.beans.PropertyChangeSupport
	 */
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	/**
	 * Return the TimeSignature property value.
	 * @return RhythmData.TimeSignature
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public TimeSignature getTimeSignature() {
		if (ivjTimeSignature == null) {
			try {
				ivjTimeSignature = new TimeSignature();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTimeSignature;
	}

	/**
	 * Method generated to support the promotion of the timeSignatureNumarator attribute.
	 * @return int
	 */
	public int getTimeSignatureNumarator() {
		return getTimeSignature().getNumerator();
	}

	/**
	 * Return the TimeSignatureDisplayGridLayout property value.
	 * @return java.awt.GridLayout
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.GridLayout getTimeSignatureDisplayGridLayout() {
		java.awt.GridLayout ivjTimeSignatureDisplayGridLayout = null;
		try {
			/* Create part */
			ivjTimeSignatureDisplayGridLayout = new java.awt.GridLayout(1, 0);
			ivjTimeSignatureDisplayGridLayout.setVgap(0);
			ivjTimeSignatureDisplayGridLayout.setHgap(0);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		};
		return ivjTimeSignatureDisplayGridLayout;
	}

	/**
	 * Accessor for the vetoPropertyChange field.
	 */
	protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
		if (vetoPropertyChange == null) {
			vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoPropertyChange;
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
		getNumaratorField().addVetoableChangeListener(getTimeSignature());
		getDenominatorField().addVetoableChangeListener(getTimeSignature());
		// user code end
		getTimeSignature().addPropertyChangeListener(ivjEventHandler);
		getDenominatorField().addPropertyChangeListener(ivjEventHandler);
		getNumaratorField().addPropertyChangeListener(ivjEventHandler);
		connPtoP2SetTarget();
		connPtoP1SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("TimeSignatureDisplay");
			setToolTipText("Time Signature");
			setLayout(getTimeSignatureDisplayGridLayout());
			setBounds(new java.awt.Rectangle(0, 0, 75, 28));
			setSize(75, 28);
			setMinimumSize(new java.awt.Dimension(33, 19));
			add(getNumaratorField(), getNumaratorField().getName());
			add(getFraction(), getFraction().getName());
			add(getDenominatorField(), getDenominatorField().getName());
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}

	/**
	 * Accessor method
	 * @date (18.07.00 18:24:41)
	 * @return boolean
	 */
	public boolean isEditable() {
		return fieldEditable;
	}

	/**
	 * Main entrypoint - starts the part when it is run as an application.
	 * Only for testing the class.
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			java.awt.Frame frame = new java.awt.Frame();
			TimeSignatureDisplay aTimesignatureDisplay;
			aTimesignatureDisplay = new TimeSignatureDisplay();
			frame.add("Center", aTimesignatureDisplay);
			frame.setSize(aTimesignatureDisplay.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			aTimesignatureDisplay.setTimeSignature(new TimeSignature(5, 8));
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of java.awt.Container");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Accessor method
	 * @date (18.07.00 18:24:41)
	 * @param newEditable boolean
	 */
	public void setEditable(boolean b) {
		getNumaratorField().setEditable(b);
		getDenominatorField().setEditable(b);
		fieldEditable = b;
	}

	/**
	 * Sets the timeSignature property (RhythmData.TimeSignature) value.
	 * @param timeSignature The new value for the property.
	 * @see #getTimeSignature
	 */
	public void setTimeSignature(TimeSignature nts) {
		TimeSignature ots = ivjTimeSignature;
		ivjTimeSignature = nts;
		getNumaratorField().setText(Integer.toString(nts.getNumerator()));
		getDenominatorField().setText(Integer.toString(nts.getDenominator()));
		//	firePropertyChange("timeSignature", oldValue, timeSignature);
	}

	/**
	 * Method generated to support the promotion of the timeSignatureNumarator attribute.
	 * @param arg1 int
	 */
	public void setTimeSignatureNumarator(int arg1) {
		getTimeSignature().setNumerator(arg1);
	}



	/**
	 * Return the NumaratorField property value.
	 * @return RhythmData.VetoableJTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	de.uos.fmt.musitech.utility.VetoListenableJTextField getNumaratorField() {
		if (ivjNumaratorField == null) {
			try {
				ivjNumaratorField = new de.uos.fmt.musitech.utility.VetoListenableJTextField();
				ivjNumaratorField.setName("NumaratorField");
				ivjNumaratorField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
				ivjNumaratorField.setColumns(2);
				// user code begin {1}
				ivjNumaratorField.addVetoableChangeListener(getTimeSignature());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNumaratorField;
	}



	/**
	 * Return the DenominatorField property value.
	 * @return de.uos.fmt.musitech.utility.VetoListenableJTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public de.uos.fmt.musitech.utility.VetoListenableJTextField getDenominatorField() {
		if (ivjDenominatorField == null) {
			try {
				ivjDenominatorField = new de.uos.fmt.musitech.utility.VetoListenableJTextField();
				ivjDenominatorField.setName("DenominatorField");
				ivjDenominatorField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
				ivjDenominatorField.setColumns(2);
				// user code begin {1}
				ivjDenominatorField.addVetoableChangeListener(getTimeSignature());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDenominatorField;
	}



	/**
	 *
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88GF09CCBACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8DD01CC715CA1C09E354B813EA2AF1E434AAF1E8A75A4C08D32A19A91D091D5AC6DB155A3403B6B669D81DF40A1DD831AE97A0D4C9CA9B5A5094AB12B6C3DACAA0A1CDCA1372F784C8282101G8182FCF75F5E5D875F5DF7796EBBB85294FCEF775B6F3EBB6EA069A813B77B6D5B377B5E3EFD6F6DFBFBA1657FCAABAAF01493D2D0C44C3F6385042C2CA16429475779852E77E2EAB1317D7D9B6085
		3265F4A13493B0D6F2C3EAF19959BDD782FD837AA7D3D20B774177DE7237262C037011044FAA98FBB8EF05EBF1BE0F3EA7786CE27DCC29945ABDG9140E11BFD9179EF12AACC7C9A93AF2040C1484D43C873FB29524415C03F8AE08240986674AB20FD0EF15EDFF942242B38A31DE47F384E319B57312DA96089F3EE5988EF2BC8C6C1668872AAF7151AF33A201F82001B5FC91E5CF9945A133DD35E77E5DA9756C2F59E3FD1678F0794033AFDF5FEC5D61A1A7A95BF6DD33C8149886B542904022AB4CB9AC7F15D
		D1D789DB0710FCD8F38343490711D7843ED782240F8A7C45FA4497DECAAD2E84E83CA476775887A7CD798BFF16C91C47279C4FA759DF4FA53EAF63D2F4FF2464E851AB6CAC8E1CC21D2F033187G3681140E2596578274G3C4AEC604D13EFC25B6AB25AB53FDF8B348603ED2A2CFF2049D4056F26A698B1F16729DB53E5C278B96FF755A610E7B16073EA2EFB9E57C97AD1F16641DF5FCAA6BA9C0EDD89F4123B7710C39E633A38BA96558967ADA5653DE6AC56A6E3F98F92BB6F75E7EB9CC589F827E594B95619
		6BE45BF86F9843334EE1B630853EF19E4E6778C7997E64F8944F6DF31261315F8BE36B4705ED3C773E10A5A56B1B441776F4C1A2BDD4FC7AB9C745841721FEA2DE161FB23B9B1D0817A50761039371320C578BD9164158B6GEEDF354D682FBDD2EA318FA085E0B5C0A64083G5BA4E1E32113072F5146FAA55DE88BAB2ACCF54267561EFA99DA9AB1F429BB90B224001B8EC83AA239D49AA2A64D31D32883D74C3CA6E67BEE6051C983D4178CC58B406A121F9AD48FF11A76D3AFA00DCE59A8BAB5B0708709D877
		6C293D502AD248389014A503C6A978F85FA9BC13E9E9GD60485601B6B65E28B6A2B4485B18C205EA574F0AFDB6FAC5583D4EDEA6A513429F0309FF7C2D2DC2867880B9DCE789EF5899DDFE8C5DCB674B7DB6B943D7F22ED9D2762C671A5FDD67858A307F14EF6DC53AD6C660FC342EE6E993F052C1AFABE217FA0543A393DB85D717E73B8DB97E5D35DA25E878F8F454DC73E57635FF6A8F3474E65BCBE3300474ECF387E9E530E6DFD2E67BB5B704CD6G7FF4000DEE216B16772B386D36C9A1AA2C751C8E06C6
		652866391C77570B583F4F127D91064B0575CAE421B3DAAF76F32D7B58F79D7B63BC662E1BC73CDE96832E1BF71A71DE2253B4E0B8E91086E4788819E3271A516ECC3F75E8561CF6068F021F3703E305ACFCF7B37A22E4983A628A9B340F9A069270C647BFE43CC2E1D7B09AA2842F21E614DD1B81BF3768AFB5230F3835C00032C332261863634CC729CA7DA0F43B968E989CCFDBB01F192233E60CD0DAB069191654B0469B1EA32C137E01FB5095BF9CBD06AC12A2237FFE41D5E153D1BD60B057690756A78BBF
		1CB9E079612F5612AEDF623B4499BE6658A8F3DF58ACC77D104BB45C1AD8768996ABCCFC084B7A1B43105811C4894BCEB647216E0E9F37FCA23F9D7964635EE8EAF18D95BE31399547C57CB11D21DDD3D52EEA9873D154475B756536BD35D02E0F287D7C242D8C4F14DE8ED3886B98884FDAFA5F5476E25C5800F986FCBC278D4FC0354550B28F72ACEB90B964BA4A6354C6E8F381880C3F86F8CE17EB510DFAB81D816DF200F4130E6B6F29C3F826AC9F1C142625C8DDE8866C322969CC2F843B67B95F1FD95CC6
		F9B2BDA2375BCED1F715DE486BGFA3C627CB3760B5C2E6ACFBFA4953B9CC9F3D7594BF5355C9BBD7F1A17C5EEF73A83F9EE04319C2FB8173EF67B7D529A8AE9EE05DDED264E9CCE3BFDF05CAE675F2175A8FA486886870E887D6EF6223CF87719E8D39FDFF13FA27401C931BBAE99A6A2E73A3D593E4F5CBFB5BA7E11E41FCF187BAB75C577193D6BE8914FBD5EED9039C78F0CAF8360F61B56006BC144517C8A3056F453660A676DB6176563F769F6EB5E5D8D685F8684A6AE67013DADA62D713935247416AD85
		09F26DFBFE7BF4C12142652BD522F2E65CF859316C264B8EAE6BF3C7CA890F37D701E63DA2F83EF3EC970BC56028D448395613C84EA72CB96347041CBC1EBF6B42F8CEA62377E37964355D33B5F1F4A7215F9B0763791F6CC17965C91E7F6D3270C1063FEA43737DBE4DEC9C7B2BE0EC472448D98EF4224F0CCE2596C7GD6822483EC8458B6A5EC75C6A756D90B66045D282E0024764584FD111BF5201EA0CE1B7DF39DE8A3ACCE93913F3E5805BAAED4A18F83B881508B90D4058C3F6CD8D2060EC8C2997819B4
		B7ACD54B2C84DE6B55282E793C8F161C1723461E8F773D098B87056F058DC5D50C59262681EAE8BDCA4820818C02E77A41E0CEABD405BA0D4726D373A5A65F6D2AB81F2D5D987BEA7C2945CE00D10088409A00CC004DFE211F03275B1652CF2B38307BB56B33BD7E52957B7EC5633568EBE1AE73681273727C3173785EF3BDE26F258128CFD80EDD65E6B9B674C7AD5C8F98AE887DE596EE4A0B38F468E7D9387DEC3DCD505FE6615EE3F425DAEA716B1A486FA6193F1C00FEAF00EC6127999E6F18940D5FC99C1F
		CF6373DB841C4B6E816D7A736993DA3C4D3D64C9442348C6F33DBC6CF925D92BF5A64F870FF6A64E875F6DCC1C8FB6F73E407130063DAA94631D1D0B670B67BA13670BB7F5A517B32DAB311C5F6ECAACE7C65762F22E6FCAA667BF198E379AAF1184F96AFF20D035E89E8DE07EEFC83A179A9DA87539E92A6B0ACC99953FAF269B1EB35769B5345EA35639374DAF66BBE9261D2CB75B0D1A707DA73B136BE8C7F7E29DBD5B0D39CCC88B6BEE9A2F0722B39556D8B4C0F49B540F1B60B4FB4E6C367699BF7FA5B61F
		0FA51B7F5A994C89DC0A2C68E6A64AF5D3FA4C2E1B86CBB7E7FD0B69E63329136DE6DB9894F773F1B772B1B4CD6DD70298F1C9CB90E341B996C7AA607BF50B76DEB7568F10EEEBE18356E2F46D32289316C1FF8DC0B6C0CE1067GFC5E273CB84F85B9038C4642573F09451A9317A1EEDF966F155F1BC1D9BC3A84E91037AFC85D24C4C75A860F60E1G6DF200F4000DG5BAE8B9E673DB897A6AA7E303FCF39C2CDBE130CCFAF2CB32A8BDA08F1G6DBC07E1B70C40A36DE9DDE28A97B84C6B1D515C0C51733CC7
		D76CF94FF21D6FB9C3977174623C08319BACDC9643ED01FEE1C860FE47F095502F35F0AE1F185BE2616E72AD1C3BCF91F3E500C8885F35FF5ECB4C1836172CF3237C551858D6D919EDB54F9747775A5F1E553F7CB3E17D46F23A90371BCDE63B59D9ED66CDD332F8FBAEB1C0D6C31C65FD63D6EE0D35C61DB4E3B4B5F50223771866E35EEB3749A8BF4A93B1C43D11496E16F5505F8810EB08FA63AE6BADF9F556EDA4F32CBCE13D11F76049029D861733B29C4DEF4BCB0E96F17D5CF1BE2A1FBD1E57218D5019AA
		A1E65DF9F0B8A960D5E9CDFDED957CBBFE2C366A4809C3DC5EC1F6EFA24FA1D8FBE598F11F0E49FFF15DF7816AG78788FE246B1069E0A99FFBCE69C777A651871D73D7671A25BFC2E1709EA2117522EF4D2B1541A348E4BB17511EF36D9E72A4932EF0D6C64FAF923BF2A177B69EB71FAF1028D8570F1306668515A2A06F78FD9F525A7C9DD59A339C4DD391659A94A55B88DBEB24DF271F961572B27976685DC0E4DBE74C11F26ABD73400A1292DAA98B156FC26FFBF6D937613B7AD7C3BCE61B50A9A7687C2A6
		5DF1765E0772D64E887E33BE413FF6E6A1FFBE2FC39173DAE6049DBE5FE8771B32736FB88A12683B57DCF7F4A6EA07BCEF5DF78157DD09324C08FBEA5F07EFC1BBA0CD1A59E9072CC040E169A9A0FBF5ADC8F5E316E3598B02997BAF4B1F6C770B79CFB8AFB3E65EBDF13A4B05FE1E8DD79201FA0A55DE35DE760E9061359782479FE3789A9B1E47169FF9C4ECE9043155911173F78D603965C33FE2B635389E208B408730ECD668F26243FA32C43E5F2F85FBA0A9D0ED298E5F5F2867137D2E33E7C87861065399
		A43F7E5902E46F842BE739FEB24D3671ECB543737DBDB4087B5B86E305D7D20BEBGDAG7A8154ABE2FF0E3EDA3EBFB487A8D7A4370F3D04084D8DA8F4A60A464B146F6DD6A5D16E1CF2054B314AEC532FAC1D27E7814DA600ADB6DA6E6BDF1A97BE33630A70194F386DB12A7143278A924528D57D4F9454FF44576B7FA86AB351F830DB41FA4F96E66A22F5E25D796E4E2EFECE771F43387691CB262F7B04CC4BBF92F1E2D491B4DC6F6D97D1D2732D347F0DE43E885ABD837C4F39A6612268173C561C4A8BA827
		159FF397B5478EDC40988E690921C82AF205BDE57581E1404B6F717B58BD3881E45B7411F537D3F65FC3BF4F420DB3DC49D5307D2B82F7095DC93550EF39AA6A30A3A816692F6F593E339356CE7FAB60BC9E7E3F72B05F5087126774C38309F37A310144F50FBC30F87D258EA42F3FC2837677FAEEC73D436C1D9A4EA288308AA09DA06B2A705D0241EA42FCB7015FF648DE2CA97057C1C3705FB218FC2F3F9FDC7AB7453CC1FC97F5E9062179F1FD01FFE6904F4B50028C49F1FF9BC49D690A57E7F0D2D1738C21
		0D295443505C6EB6313BB18776354D32C557A4627221DF7E31406DE53ED7837D9E8BB7E165379396AE45A75E9F16D938F476FFBDD6C3BF73E3E15BDF0D29836F81FC6E470B47ABFEAEDF9941381A8734E5F329450DGBDG93G11B96BADEF28A6195F8312A72F6CFCF4C989186F19B74754DEABE02D34394565617167B1995FC5F5EDA644F30DFCD97C6636FE4E4AE5A852CCFB25A027F1D211FFE6DBB4530C46E7512C30E89E30E88C4F421C6833D6AE3B455A7F50F0F2DB9B9FDE5A56FC436CDD9EEC24FFB6E8
		56327EE12C7FB40FA7C4AD1B56074BACDCF728D78BA9511AEB2BA25E0B391CFFE5F61687B212F9311F59C9D66FC91138FA6F0B4C7E4A002E5222FD5307F2C7F3C83CB084C7AB4F7389DDF44D0B7CF046E77F8DAC9E783C35EC5E904C11ADFEBF1EDBBCE7FDF0CE70DAB9AF6C7C7B6C4ECC037EF720B55F7298AE93F0EFD9FE784D116467735D11254F67361144316007A38BE3410FC69247026C11053160F65FC279B7786C72F35D6C25386F862BBF427A75D67F22A7367F8D2B4FEB1448A5E20D0DCDE2DBFD385A
		9CDBB1969DAB7161FBD3327A854A17E307AC5AD1DFAC0F347168BADF1B3B76F5364F09F5F8DDA49D447C553C7387955970F1FCE5E4F7945FC5D13F047FAE8E77C9479D50E67C019499E4CFEC559E52490B8B0B54BB57CD966D3FFB52F01042EED26CE32BF5132299E457CD8AA2G33917CFFA018B71DA566DF4CDA5606DABF7F602FB2461D658E62D83096DB836D7A5F86A9B89C3ACC66582E519AF74676084807371E3057C95BF3DECC7A3E50F3830F1793B74457C98FF5228D2D81FC668DA2C63D5579B6C91E1B
		7579A5D5852F1BEA8D481827354D9AB4FA1767F6627AA1C1E38E72312D6C7DF652B6673FD0CB8788CADBF49F1A8FGG38A8GGD0CB818294G94G88G88GF09CCBACCADBF49F1A8FGG38A8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG548FGGGG
	**end of data**/
	}
}