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

package de.uos.fmt.musitech.performance.midi.gui;

/**
 * Dialog for MIDI settings.
 * @author: TW
 * @version $Reveision: $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $
 */

import java.awt.Toolkit;

import javax.sound.midi.MidiDevice;
import javax.swing.JFrame;

import de.uos.fmt.musitech.performance.midi.SequencingXX;

public class SequencingEditorDialog extends javax.swing.JDialog {
	private javax.swing.JScrollPane ivjJDialogContentPane = null;
	private javax.swing.JButton ivjButtonApply = null;
	private javax.swing.JButton ivjButtonCancel = null;
	private javax.swing.JButton ivjButtonOK = null;
	private javax.swing.JLabel ivjClickChannelLabel = null;
	private MidiChannelSelector ivjClickChannelSelector = null;
	private javax.swing.JLabel ivjDownbeatLabel = null;
	private MidiNoteSelector ivjDownbeatSelector = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JLabel ivjSoundChannelLabel = null;
	private MidiChannelSelector ivjSoundChannelSelector = null;
	private javax.swing.JLabel ivjUpbeatLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private MidiNoteSelector ivjUpbeatSelector = null;
	private javax.swing.JLabel ivjMidiOutLabel = null;
	private javax.swing.JComboBox ivjMidiOutSelector = null;
	protected MidiDevice.Info[] midiInputs;
	protected MidiDevice.Info[] midiOutputs;
	protected MidiDevice.Info[] midiSequencers;
	private javax.swing.JLabel ivjMidiSequencerLabel = null;
	private javax.swing.JComboBox ivjMidiSequencerSelector = null;
	private SequencingXX ivjSequencing = null;
	private javax.swing.JLabel ivjMidiInLabel = null;
	private javax.swing.JComboBox ivjMidiInSelector = null;class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM9(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonCancel())
				connEtoM10(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonOK())
				connEtoM11(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonOK())
				connEtoM12(e);
			if (e.getSource() == SequencingEditorDialog.this.getDownbeatSelector())
				connEtoM7(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM1(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM2(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM4(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM3(e);
			if (e.getSource() == SequencingEditorDialog.this.getUpbeatSelector())
				connEtoM8(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM13(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM14(e);
			if (e.getSource() == SequencingEditorDialog.this.getMidiInSelector())
				connEtoM17(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM18(e);
			if (e.getSource() == SequencingEditorDialog.this.getButtonApply())
				connEtoM20(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == SequencingEditorDialog.this.getSoundChannelSelector())
				connEtoM5(e);
			if (e.getSource() == SequencingEditorDialog.this.getClickChannelSelector())
				connEtoM6(e);
			if (e.getSource() == SequencingEditorDialog.this.getMidiOutSelector())
				connEtoM15(e);
			if (e.getSource() == SequencingEditorDialog.this.getMidiSequencerSelector())
				connEtoM16(e);
			if (e.getSource() == SequencingEditorDialog.this.getMidiThruJCheckBox())
				connEtoM19(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SequencingEditorDialog.this.getSequencing() && (evt.getPropertyName().equals("clickNoteStrong")))
				connPtoP3SetTarget();
			if (evt.getSource() == SequencingEditorDialog.this.getSequencing() && (evt.getPropertyName().equals("clickChannelNum")))
				connPtoP2SetTarget();
			if (evt.getSource() == SequencingEditorDialog.this.getSequencing() && (evt.getPropertyName().equals("soundChannelNum")))
				connPtoP1SetTarget();
			if (evt.getSource() == SequencingEditorDialog.this.getSequencing() && (evt.getPropertyName().equals("clickNoteWeak")))
				connPtoP4SetTarget();
			if (evt.getSource() == SequencingEditorDialog.this.getSequencing() && (evt.getPropertyName().equals("sequencer")))
				connPtoP11SetTarget();
			if (evt.getSource() == SequencingEditorDialog.this.getSequencing() && (evt.getPropertyName().equals("outDevice")))
				connPtoP12SetTarget();
			if (evt.getSource() == SequencingEditorDialog.this.getSequencing() && (evt.getPropertyName().equals("inDevice")))
				connPtoP14SetTarget();
			if (evt.getSource() == SequencingEditorDialog.this.getSequencing() && (evt.getPropertyName().equals("midiThru")))
				connPtoP16SetTarget();
		};
	};
;

	private javax.swing.JCheckBox ivjMidiThruJCheckBox = null;
	private javax.swing.JLabel ivjMidiThruJLabel = null;

	/**
	 * SequencingEditorDialog constructor.
	 */
	public SequencingEditorDialog() {
		super();
		initialize();
	}
	
	/**
	 * SequencingEditorDialog constructor.
	 * @param parent the parent of the dialog
	 */
	public SequencingEditorDialog(JFrame parent) {
		super(parent);
		initialize();
	}

	/**
	 * Main entrypoint - starts the part when it is run as an application.
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			SequencingEditorDialog aSequencingEditorDialog;
			aSequencingEditorDialog = new SequencingEditorDialog();
			aSequencingEditorDialog.setModal(true);
			aSequencingEditorDialog.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			aSequencingEditorDialog.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JDialog");
			exception.printStackTrace(System.out);
		}
	}

	/**
	 * Returns a info-vector of midi-sequencers.
	 * @date (14.02.2001 01:25:38)
	 * @return javax.sound.midi.MidiDevice.Info[]
	 */
	public javax.sound.midi.MidiDevice.Info[] getMidiSequencers() {
		if (midiSequencers == null) {
			javax.sound.midi.MidiDevice.Info[] midiDevs =
				javax.sound.midi.MidiSystem.getMidiDeviceInfo();
			java.util.Vector outVec = new java.util.Vector();
			for (int i = 0; i < midiDevs.length; i++) {
				javax.sound.midi.MidiDevice device = null;
				try {
					device = javax.sound.midi.MidiSystem.getMidiDevice(midiDevs[i]);
					if(device instanceof javax.sound.midi.Sequencer)
						outVec.add(midiDevs[i]);
				} catch (Exception e) {
				} finally {
				}
			}
			midiSequencers =
				(
					javax
						.sound
						.midi
						.MidiDevice
						.Info[]) outVec
						.toArray(new javax.sound.midi.MidiDevice.Info[] {
			});
		}
		return midiSequencers;
	}

	/**
	 * connEtoM1:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> Sequencing1.soundChannelNum)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM1(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getSequencing().setSoundChannelNum(getSoundChannelSelector().getSelectedIndex());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM10:  (ButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> SequencingEditorDialog.dispose()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM10(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.dispose();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM11:  (ButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonApply.doClick(I)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM11(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().doClick(0);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM12:  (ButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonCancel.doClick(I)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM12(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonCancel().doClick(1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM2:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> Sequencing1.clickChannelNum)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM2(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getSequencing().setClickChannelNum(getClickChannelSelector().getSelectedIndex());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM3:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> Sequencing1.clickNoteStrong)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM3(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getSequencing().setClickNoteStrong(getDownbeatSelector().getSelectedIndex());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM4:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> Sequencing1.clickNoteWeak)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM4(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getSequencing().setClickNoteWeak(getUpbeatSelector().getSelectedIndex());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM5:  (SoundChannelSelector.item.itemStateChanged(java.awt.event.ItemEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM5(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled(true);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM6:  (ClickChannelSelector.item.itemStateChanged(java.awt.event.ItemEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM6(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled(true);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM7:  (DownbeatSelector.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM7(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled(true);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM8:  (UpbeatSelector.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM8(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled(true);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM9:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM9(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled(false);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP10SetTarget:  (MidiSequencerSelector.this <--> MidiSequencerLabel.labelFor)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP10SetTarget() {
		/* Set the target from the source */
		try {
			getMidiSequencerLabel().setLabelFor(getMidiSequencerSelector());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP11SetTarget:  (Sequencing1.sequencerInfo <--> MidiSequencerSelector.selectedItem)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP11SetTarget() {
		/* Set the target from the source */
		try {
			getMidiSequencerSelector().setSelectedItem(getSequencing().getSequencerInfo());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP12SetTarget:  (Sequencing1.outDeviceInfo <--> MidiOutSelector.selectedItem)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP12SetTarget() {
		/* Set the target from the source */
		try {
			getMidiOutSelector().setSelectedItem(getSequencing().getOutDeviceInfo());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP1SetTarget:  (Sequencing1.soundChannelNum <--> SoundChannelSelector.selectedIndex)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			getSoundChannelSelector().setSelectedIndex(getSequencing().getSoundChannelNum());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP2SetTarget:  (Sequencing1.clickChannelNum <--> ClickChannelSelector.selectedIndex)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP2SetTarget() {
		/* Set the target from the source */
		try {
			getClickChannelSelector().setSelectedIndex(getSequencing().getClickChannelNum());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP3SetTarget:  (Sequencing1.clickNoteStrong <--> DownbeatSelector.selectedIndex)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP3SetTarget() {
		/* Set the target from the source */
		try {
			getDownbeatSelector().setSelectedIndex(getSequencing().getClickNoteStrong());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP4SetTarget:  (Sequencing1.clickNoteWeak <--> UpbeatSelector.selectedIndex)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP4SetTarget() {
		/* Set the target from the source */
		try {
			getUpbeatSelector().setSelectedIndex(getSequencing().getClickNoteWeak());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP5SetTarget:  (SoundChannelSelector.this <--> SoundChannelLabel.labelFor)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP5SetTarget() {
		/* Set the target from the source */
		try {
			getSoundChannelLabel().setLabelFor(getSoundChannelSelector());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP6SetTarget:  (ClickChannelSelector.this <--> ClickChannelLabel.labelFor)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP6SetTarget() {
		/* Set the target from the source */
		try {
			getClickChannelLabel().setLabelFor(getClickChannelSelector());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP7SetTarget:  (DownbeatSelector.this <--> DownbeatLabel.labelFor)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP7SetTarget() {
		/* Set the target from the source */
		try {
			getDownbeatLabel().setLabelFor(getDownbeatSelector());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP8SetTarget:  (UpbeatSelector.this <--> UpbeatLabel.labelFor)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP8SetTarget() {
		/* Set the target from the source */
		try {
			getUpbeatLabel().setLabelFor(getUpbeatSelector());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP9SetTarget:  (MidiNoteSelector.this <--> JLabel.labelFor)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP9SetTarget() {
		/* Set the target from the source */
		try {
			getMidiOutLabel().setLabelFor(getMidiOutSelector());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * Return the ButtonApply property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	javax.swing.JButton getButtonApply() {
		if (ivjButtonApply == null) {
			try {
				ivjButtonApply = new javax.swing.JButton();
				ivjButtonApply.setName("ButtonApply");
				ivjButtonApply.setMnemonic('A');
				ivjButtonApply.setText("Apply");
				ivjButtonApply.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
				ivjButtonApply.setPreferredSize(new java.awt.Dimension(80, 27));
				ivjButtonApply.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
				ivjButtonApply.setEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjButtonApply;
	}

	/**
	 * Return the ButtonCancel property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	javax.swing.JButton getButtonCancel() {
		if (ivjButtonCancel == null) {
			try {
				ivjButtonCancel = new javax.swing.JButton();
				ivjButtonCancel.setName("ButtonCancel");
				ivjButtonCancel.setMnemonic('c');
				ivjButtonCancel.setText("Cancel");
				ivjButtonCancel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
				ivjButtonCancel.setPreferredSize(new java.awt.Dimension(80, 27));
				ivjButtonCancel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjButtonCancel;
	}

	/**
	 * Return the ButtonOK property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	javax.swing.JButton getButtonOK() {
		if (ivjButtonOK == null) {
			try {
				ivjButtonOK = new javax.swing.JButton();
				ivjButtonOK.setName("ButtonOK");
				ivjButtonOK.setMnemonic('o');
				ivjButtonOK.setText("OK");
				ivjButtonOK.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
				ivjButtonOK.setPreferredSize(new java.awt.Dimension(80, 27));
				ivjButtonOK.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjButtonOK;
	}

	/**
	 * Return the ClickChannelLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getClickChannelLabel() {
		if (ivjClickChannelLabel == null) {
			try {
				ivjClickChannelLabel = new javax.swing.JLabel();
				ivjClickChannelLabel.setName("ClickChannelLabel");
				ivjClickChannelLabel.setLabelFor(getClickChannelSelector());
				ivjClickChannelLabel.setDisplayedMnemonic('m');
				ivjClickChannelLabel.setText("Metronome Channel");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjClickChannelLabel;
	}

	/**
	 * Return the ClickChannelSelector property value.
	 * @return music.MidiChannelSelector
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	MidiChannelSelector getClickChannelSelector() {
		if (ivjClickChannelSelector == null) {
			try {
				ivjClickChannelSelector = new MidiChannelSelector();
				ivjClickChannelSelector.setName("ClickChannelSelector");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjClickChannelSelector;
	}

	/**
	 * Return the DownbeatLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getDownbeatLabel() {
		if (ivjDownbeatLabel == null) {
			try {
				ivjDownbeatLabel = new javax.swing.JLabel();
				ivjDownbeatLabel.setName("DownbeatLabel");
				ivjDownbeatLabel.setLabelFor(getDownbeatSelector());
				ivjDownbeatLabel.setDisplayedMnemonic('D');
				ivjDownbeatLabel.setText("Downbeat Metronome Note");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDownbeatLabel;
	}

	/**
	 * Return the DownbeatSelector property value.
	 * @return music.MidiNoteSelector
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	MidiNoteSelector getDownbeatSelector() {
		if (ivjDownbeatSelector == null) {
			try {
				ivjDownbeatSelector = new MidiNoteSelector();
				ivjDownbeatSelector.setName("DownbeatSelector");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDownbeatSelector;
	}

	/**
	 * Return the JDialogContentPane property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getJDialogContentPane() {
		if (ivjJDialogContentPane == null) {
			try {
				ivjJDialogContentPane = new javax.swing.JScrollPane();
				ivjJDialogContentPane.setName("JDialogContentPane");
				ivjJDialogContentPane.setPreferredSize(new java.awt.Dimension(318, 390));
				getJDialogContentPane().setViewportView(getJPanel1());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJDialogContentPane;
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
				ivjJPanel1.setPreferredSize(new java.awt.Dimension(300, 345));
				ivjJPanel1.setLayout(new java.awt.GridBagLayout());
				ivjJPanel1.setBounds(-87, -28, 456, 339);

				java.awt.GridBagConstraints constraintsSoundChannelLabel = new java.awt.GridBagConstraints();
				constraintsSoundChannelLabel.gridx = 0; constraintsSoundChannelLabel.gridy = 0;
				constraintsSoundChannelLabel.weightx = 1.0;
				constraintsSoundChannelLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getSoundChannelLabel(), constraintsSoundChannelLabel);

				java.awt.GridBagConstraints constraintsClickChannelLabel = new java.awt.GridBagConstraints();
				constraintsClickChannelLabel.gridx = 0; constraintsClickChannelLabel.gridy = 1;
				constraintsClickChannelLabel.weightx = 1.0;
				constraintsClickChannelLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getClickChannelLabel(), constraintsClickChannelLabel);

				java.awt.GridBagConstraints constraintsDownbeatLabel = new java.awt.GridBagConstraints();
				constraintsDownbeatLabel.gridx = 0; constraintsDownbeatLabel.gridy = 2;
				constraintsDownbeatLabel.weightx = 1.0;
				constraintsDownbeatLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getDownbeatLabel(), constraintsDownbeatLabel);

				java.awt.GridBagConstraints constraintsUpbeatLabel = new java.awt.GridBagConstraints();
				constraintsUpbeatLabel.gridx = 0; constraintsUpbeatLabel.gridy = 3;
				constraintsUpbeatLabel.weightx = 1.0;
				constraintsUpbeatLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getUpbeatLabel(), constraintsUpbeatLabel);

				java.awt.GridBagConstraints constraintsSoundChannelSelector = new java.awt.GridBagConstraints();
				constraintsSoundChannelSelector.gridx = 1; constraintsSoundChannelSelector.gridy = 0;
				constraintsSoundChannelSelector.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsSoundChannelSelector.weightx = 0.5;
				constraintsSoundChannelSelector.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getSoundChannelSelector(), constraintsSoundChannelSelector);

				java.awt.GridBagConstraints constraintsClickChannelSelector = new java.awt.GridBagConstraints();
				constraintsClickChannelSelector.gridx = 1; constraintsClickChannelSelector.gridy = 1;
				constraintsClickChannelSelector.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsClickChannelSelector.weightx = 0.5;
				constraintsClickChannelSelector.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getClickChannelSelector(), constraintsClickChannelSelector);

				java.awt.GridBagConstraints constraintsDownbeatSelector = new java.awt.GridBagConstraints();
				constraintsDownbeatSelector.gridx = 1; constraintsDownbeatSelector.gridy = 2;
				constraintsDownbeatSelector.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsDownbeatSelector.weightx = 0.5;
				constraintsDownbeatSelector.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getDownbeatSelector(), constraintsDownbeatSelector);

				java.awt.GridBagConstraints constraintsUpbeatSelector = new java.awt.GridBagConstraints();
				constraintsUpbeatSelector.gridx = 1; constraintsUpbeatSelector.gridy = 3;
				constraintsUpbeatSelector.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsUpbeatSelector.weightx = 0.5;
				constraintsUpbeatSelector.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getUpbeatSelector(), constraintsUpbeatSelector);

				java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
				constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 8;
				constraintsJPanel2.gridwidth = 2;
				constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
				constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getJPanel2(), constraintsJPanel2);

				java.awt.GridBagConstraints constraintsMidiOutLabel = new java.awt.GridBagConstraints();
				constraintsMidiOutLabel.gridx = 0; constraintsMidiOutLabel.gridy = 4;
				constraintsMidiOutLabel.weightx = 1.0;
				constraintsMidiOutLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getMidiOutLabel(), constraintsMidiOutLabel);

				java.awt.GridBagConstraints constraintsMidiOutSelector = new java.awt.GridBagConstraints();
				constraintsMidiOutSelector.gridx = 1; constraintsMidiOutSelector.gridy = 4;
				constraintsMidiOutSelector.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsMidiOutSelector.weightx = 0.5;
				constraintsMidiOutSelector.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getMidiOutSelector(), constraintsMidiOutSelector);

				java.awt.GridBagConstraints constraintsMidiSequencerLabel = new java.awt.GridBagConstraints();
				constraintsMidiSequencerLabel.gridx = 0; constraintsMidiSequencerLabel.gridy = 6;
				constraintsMidiSequencerLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getMidiSequencerLabel(), constraintsMidiSequencerLabel);

				java.awt.GridBagConstraints constraintsMidiSequencerSelector = new java.awt.GridBagConstraints();
				constraintsMidiSequencerSelector.gridx = 1; constraintsMidiSequencerSelector.gridy = 6;
				constraintsMidiSequencerSelector.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsMidiSequencerSelector.weightx = 1.0;
				constraintsMidiSequencerSelector.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getMidiSequencerSelector(), constraintsMidiSequencerSelector);

				java.awt.GridBagConstraints constraintsMidiInSelector = new java.awt.GridBagConstraints();
				constraintsMidiInSelector.gridx = 1; constraintsMidiInSelector.gridy = 7;
				constraintsMidiInSelector.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsMidiInSelector.weightx = 1.0;
				constraintsMidiInSelector.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getMidiInSelector(), constraintsMidiInSelector);

				java.awt.GridBagConstraints constraintsMidiInLabel = new java.awt.GridBagConstraints();
				constraintsMidiInLabel.gridx = 0; constraintsMidiInLabel.gridy = 7;
				constraintsMidiInLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getMidiInLabel(), constraintsMidiInLabel);

				java.awt.GridBagConstraints constraintsMidiThruJLabel = new java.awt.GridBagConstraints();
				constraintsMidiThruJLabel.gridx = 0; constraintsMidiThruJLabel.gridy = 5;
				constraintsMidiThruJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getMidiThruJLabel(), constraintsMidiThruJLabel);

				java.awt.GridBagConstraints constraintsMidiThruJCheckBox = new java.awt.GridBagConstraints();
				constraintsMidiThruJCheckBox.gridx = 1; constraintsMidiThruJCheckBox.gridy = 5;
				constraintsMidiThruJCheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsMidiThruJCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
				getJPanel1().add(getMidiThruJCheckBox(), constraintsMidiThruJCheckBox);
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
	 * Return the JPanel2 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanel2() {
		if (ivjJPanel2 == null) {
			try {
				ivjJPanel2 = new javax.swing.JPanel();
				ivjJPanel2.setName("JPanel2");
				ivjJPanel2.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
				ivjJPanel2.setLayout(new java.awt.FlowLayout());
				getJPanel2().add(getButtonOK(), getButtonOK().getName());
				getJPanel2().add(getButtonCancel(), getButtonCancel().getName());
				getJPanel2().add(getButtonApply(), getButtonApply().getName());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJPanel2;
	}

	/**
	 * Return the JLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getMidiOutLabel() {
		if (ivjMidiOutLabel == null) {
			try {
				ivjMidiOutLabel = new javax.swing.JLabel();
				ivjMidiOutLabel.setName("MidiOutLabel");
				ivjMidiOutLabel.setLabelFor(getUpbeatSelector());
				ivjMidiOutLabel.setDisplayedMnemonic('O');
				ivjMidiOutLabel.setText("Midi Out Device");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMidiOutLabel;
	}

	/**
	 * Return the MidiNoteSelector property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	javax.swing.JComboBox getMidiOutSelector() {
		if (ivjMidiOutSelector == null) {
			try {
				ivjMidiOutSelector = new javax.swing.JComboBox();
				ivjMidiOutSelector.setName("MidiOutSelector");
				// user code begin {1}
				MidiDevice.Info[] midiOutputs = getSequencing().getMidiOutputs();
				for (int i = 0; i < midiOutputs.length; i++){
					ivjMidiOutSelector.addItem(midiOutputs[i]);
				}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMidiOutSelector;
	}

	/**
	 * Return the MidiSequencerLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getMidiSequencerLabel() {
		if (ivjMidiSequencerLabel == null) {
			try {
				ivjMidiSequencerLabel = new javax.swing.JLabel();
				ivjMidiSequencerLabel.setName("MidiSequencerLabel");
				ivjMidiSequencerLabel.setLabelFor(getUpbeatSelector());
				ivjMidiSequencerLabel.setDisplayedMnemonic('Q');
				ivjMidiSequencerLabel.setText("Midi Sequencer");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMidiSequencerLabel;
	}

	/**
	 * Return the MidiSequencerSelector property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	javax.swing.JComboBox getMidiSequencerSelector() {
		if (ivjMidiSequencerSelector == null) {
			try {
				ivjMidiSequencerSelector = new javax.swing.JComboBox();
				ivjMidiSequencerSelector.setName("MidiSequencerSelector");
				// user code begin {1}
				for (int i = 0; i < getMidiSequencers().length; i++){
					ivjMidiSequencerSelector.addItem(getMidiSequencers()[i]);
				}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMidiSequencerSelector;
	}

	/**
	 * Return the SoundChannelLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getSoundChannelLabel() {
		if (ivjSoundChannelLabel == null) {
			try {
				ivjSoundChannelLabel = new javax.swing.JLabel();
				ivjSoundChannelLabel.setName("SoundChannelLabel");
				ivjSoundChannelLabel.setLabelFor(getSoundChannelSelector());
				ivjSoundChannelLabel.setDisplayedMnemonic('s');
				ivjSoundChannelLabel.setText("Sound Channel");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjSoundChannelLabel;
	}

	/**
	 * Return the SoundChannelSelector property value.
	 * @return music.MidiChannelSelector
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	MidiChannelSelector getSoundChannelSelector() {
		if (ivjSoundChannelSelector == null) {
			try {
				ivjSoundChannelSelector = new MidiChannelSelector();
				ivjSoundChannelSelector.setName("SoundChannelSelector");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjSoundChannelSelector;
	}

	/**
	 * Return the UpbeatLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getUpbeatLabel() {
		if (ivjUpbeatLabel == null) {
			try {
				ivjUpbeatLabel = new javax.swing.JLabel();
				ivjUpbeatLabel.setName("UpbeatLabel");
				ivjUpbeatLabel.setLabelFor(getUpbeatSelector());
				ivjUpbeatLabel.setDisplayedMnemonic('U');
				ivjUpbeatLabel.setText("Upbeat Metronome Note");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUpbeatLabel;
	}

	/**
	 * Return the UpbeatSelector property value.
	 * @return music.MidiNoteSelector
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	MidiNoteSelector getUpbeatSelector() {
		if (ivjUpbeatSelector == null) {
			try {
				ivjUpbeatSelector = new MidiNoteSelector();
				ivjUpbeatSelector.setName("UpbeatSelector");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUpbeatSelector;
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
		getButtonApply().addActionListener(ivjEventHandler);
		getButtonCancel().addActionListener(ivjEventHandler);
		getButtonOK().addActionListener(ivjEventHandler);
		getSoundChannelSelector().addItemListener(ivjEventHandler);
		getClickChannelSelector().addItemListener(ivjEventHandler);
		getDownbeatSelector().addActionListener(ivjEventHandler);
		getSequencing().addPropertyChangeListener(ivjEventHandler);
		getUpbeatSelector().addActionListener(ivjEventHandler);
		getMidiOutSelector().addItemListener(ivjEventHandler);
		getMidiSequencerSelector().addItemListener(ivjEventHandler);
		getMidiInSelector().addActionListener(ivjEventHandler);
		getMidiThruJCheckBox().addItemListener(ivjEventHandler);
		connPtoP3SetTarget();
		connPtoP2SetTarget();
		connPtoP1SetTarget();
		connPtoP4SetTarget();
		connPtoP5SetTarget();
		connPtoP6SetTarget();
		connPtoP7SetTarget();
		connPtoP8SetTarget();
		connPtoP9SetTarget();
		connPtoP10SetTarget();
		connPtoP11SetTarget();
		connPtoP12SetTarget();
		connPtoP13SetTarget();
		connPtoP14SetTarget();
		connPtoP15SetTarget();
		connPtoP16SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("SequencingEditorDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(389, 390);
			setTitle("Midi Parameters");
			setContentPane(getJDialogContentPane());
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		getButtonApply().setEnabled(false);
		// user code end
	}

	/**
	 * connEtoM13:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> Sequencing1.sequencerInfo)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM13(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getSequencing().setSequencerInfo((javax.sound.midi.MidiDevice.Info)getMidiSequencerSelector().getSelectedItem());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM14:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> Sequencing1.outDeviceInfo)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM14(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getSequencing().setOutDeviceInfo((javax.sound.midi.MidiDevice.Info)getMidiOutSelector().getSelectedItem());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			ivjExc.printStackTrace();
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM15:  (MidiOutSelector.item.itemStateChanged(java.awt.event.ItemEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM15(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled(true);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM16:  (MidiSequencerSelector.item.itemStateChanged(java.awt.event.ItemEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM16(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled(true);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM17:  (MidiInSelector.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM17(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled(true);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM18:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> Sequencing.inDeviceInfo)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM18(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getSequencing().setInDeviceInfo((javax.sound.midi.MidiDevice.Info)getMidiInSelector().getSelectedItem());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM19:  (MidiThruJCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> ButtonApply.enabled)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM19(java.awt.event.ItemEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getButtonApply().setEnabled((arg1 != null));
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connEtoM20:  (ButtonApply.action.actionPerformed(java.awt.event.ActionEvent) --> Sequencing.setMidiThru(Z)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connEtoM20(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			getSequencing().setMidiThru(getMidiThruJCheckBox().isSelected());
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP13SetTarget:  (JComboBox.this <--> MidiInLabel.labelFor)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP13SetTarget() {
		/* Set the target from the source */
		try {
			getMidiInLabel().setLabelFor(getMidiInSelector());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP14SetTarget:  (Sequencing.inDeviceInfo <--> MidiInSelector.selectedItem)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP14SetTarget() {
		/* Set the target from the source */
		try {
			getMidiInSelector().setSelectedItem(getSequencing().getInDeviceInfo());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP15SetTarget:  (MidiThruJCheckBox.this <--> MidiThruJLabel.labelFor)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connPtoP15SetTarget() {
		/* Set the target from the source */
		try {
			getMidiThruJLabel().setLabelFor(getMidiThruJCheckBox());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * connPtoP16SetTarget:  (Sequencing.midiThru <--> MidiThruJCheckBox.selected)
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	void connPtoP16SetTarget() {
		/* Set the target from the source */
		try {
			getMidiThruJCheckBox().setSelected(getSequencing().isMidiThru());
			// user code begin {1}
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
		D0CB838494G88G88G02E68BABGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFDDCD4D59AB03BD0CB1B3B4B363E9B151BE4E4D4565AE645EE34D1D96AC6E52D956D32C5E51BEDD6EC51C6C9F987D0C1D151D0912930AC2148781491D191B0C5C18851F033E4E4C6989DE6461961C345FC1F73711CFB674E1D99A4F97FF8FF4B4F47BB77B94F57F94EF33E6EBD67DCA97B4330F93A14ECC9524D1378DF25CE12E6BFA8C98F6E7C65C708131A82F51262EFAD40B2E90DA38D2ED91016
		1D9B244B12FED5148E776B886D2AA05DAA787D1E54DEBDFD9A7C7022A78252DE5EF66F915FFAC2041E85747E1766CC38AE81A8GDCA3C0A8C016B45E1C4371F638DF42FF935025C8D275DEC2F373E00E829F476932A0ED9DC9EFA6B4F71B73F8FA906046G44G446FE534C9F04D20561DE856F33A28748849D53ABCF5B15125100F505E4CF88779B5CB7AD38AF1C42A347B33CF61DA5DF23CEBEBCF2F535CF9783859D5D5354FF42257E46DB4DB3BF6984DAE1B235EEC30583A20DC28FDCF8C26D3DEC91A847783
		AD8457FC06782498FE6F8268E8C19BDFE92A66B6F43CFC2BD47C549A2FB6C635B05B62DBE49BA34EFE1B9A72B5117FAF6AF342178F776DG4CFE5B610F5C6C77E7FB69B134FD4657E87B2EB2A2BB33954AG203A9571DB4A893E8D6EADGC12D1827E737E11EAC5DFF146A776FCFDB6AA5CFE3DAD9DEA6374AF98AB871ED6AEA9AC7F9AE928B52BE508B2087B884B088B086E08D0D5FA79C1F41F57B91D71D2D2747E65DEE3757DA0C0EA61B51E4015FD5D51042F12D26CE1B43A8C9ACC663860BB46CC118344FBD
		BE89303CFDA3E1CC05265CA485FF78F96ABC0D3C359D15D227F299F17B643C656DA7FE3BC3A2FECB86BC613B980061B3F4845F31DF4633325DCFE3075C879C8854457D977DBC3AGED493CA4DC1A5035A8F50106AD9DD356276A8FB39BDA8E4B36B09F7F271778F8907021G938126824C82C86FC09F2FF5167973F10355E5F2D88D16FD3DC76C86072B3657ECB11A9C5C5789E6DF3E568246776EB973B13FC414A94BA87D0E251AFE8908DA133A2E0379233AC3768B53DDF94C1B6EC39D1E3131C414E1942DAB76
		8ED6067384FE8A4587A9702CEC77D2BC398F0534E4GC6FFE6A30D858323BFA8647C0162730E48F8A6270E56C5F29F8BE96126C09DAB5F517D242D34F7876AC6810483C4814481A481E41A310CAD4EAF07D80FDA94654BF8BFF12D042BE94065B0B4D81DAE033553F44060B09B0ED8CCCE0953ECF0913F9EF163AB23753B93F46CB4D9CD8E034BEC3302F4C30F89024949E82ADD4B880D43C4D35B4CBDA6D050E317D0EE3D6BBD38DA8CCE57FE3B5160B24994AC3D51C5E2224FF0GA402GD2FEE6622F57E99BDD
		843FF71951D7F78F90DC985CC78A3FCCF1AD076B9E88EE13252A2A5E5CC94CB0B8CEE20C5EADE5C3C144G7DCCG7DB170B3C09CC041F128D3GFACB20AE82E0D78F4885E833866AA6839C32C13F85D0ED07F4006C93503681A482F4B8A08D205C99288BG088148F2856ADAGC68144G6474C23B8B9089D050877596C05F9F281B8E209FG19GB9A783F541G31G7527409E0059G07CEC37983048E826D996081487BA1D0E7870882A8B88BB682948700DC00341F00AE0099G461FC29C81E485023DGD9C1
		A08FA08CA073824081E4DFA87A37F62C0BC94F5FA4455E1629D997E99FFCA12BD7768B55F5F1290E78B8947011G31GC9GB9BF835B815A8186GC281A281E28112GF202A19D209DE0GA094A092A096A089A067A2C887E88798G888508840885C8824879B9248334838CG0482C4824482A48164DC8C69G6DG83G21G11G31GC9GB923A09D209DE0GA094A092A096A089A06797108E508EB0G908A9089908B10841073CBC887E88798G888508840885C8824879950E373EBD53126669CF92E73E5A
		CA92DB340D9C1469C89C13B87445C7E25BD7BA09E992036752EE93A031C547B9BF483C244EA8E9C84C62EF9233781B54ABD2B7346412BAC56A12BF7D137D784B2D8F30F952127AC52E33791554EBD27F07A20F54F7D217E9F91CE2ED40A88DBFE7DF28AD0F34B5246E133ACA5A9A5296503A7443391703961076033499C6DE6FE9B9BB5DE56B4F7856C55A8CEFE9243D2257E32CADC97B09CCCB5A13215AC95ABB7CCD5AA57C9D20321534EFEA5EE085EFCE2FE7BAE97FB27D58C25A5C3CA178BCAC48CBB97631F6
		D709A37D8AE90729FCBBEBF335F8E33C443FAFA0FD53A8959F69E7C81F425A96478FDAED4BEABFF23F6457438A9C19D3FC6207EF111F748F819A4EB10F847A79552C402DD051FC27785D2978CD664915DE642E8778E2887A571C032D6FEB6076736BC1FEA5736E25C31477B940B67E7B9B00C2090D1355F497FB61A773511DGA7A4B29E12249D9C5FB50CF25002FDG5F824010CABA43F1E5AA9A039F9927FD246D61D7924F01GCEC55AC567E0E7336277CF943FCBD4F447B5F83FD67C5E22115E8B70B3BF7A1B
		GCCC3307382AF78BA009E950E4CBBEC7C7747GE73D70EE9EC63912F17BA6952E82E03B445A0FE5521727481CC58F6318B6G572FF04C1D521BAF5139C82D41E94259C73383A68EA623BC82E734F3FBC9BDB075192C2EFA135DE4B5428FA79F7B4F6FA3F14E67A5C7C9BE99BE136249BC358EA68ECE01DF5E370C61C106F216036939B45D60F2B94CC7FADD26FDA6174BEC6D1279BF6AA336B85D2640CE1E571253A42F23A00F61G13C4DEEF68A3F58A67970DB65B71DEFB9B19B971F94ADE7ABCE6AA50470B71
		5EBDE58417968263749044BDC8F10426C6605A4A5A07D0CEA867DC4B17696899120ED49EEB43539138F122445ABC9223248FD94FE3087810341724BC97F39DAB38BE9D0FC162DFD2AE24EFD945E512FE04340324EDA46DB7692FC8BDAE63B1DA49E31744BD292F5BF85C12BAC45ABF529E12FE0834AD24DE12360154F1521611367A9BE1FBA4353D837C9B8CB091A096C07AB5789C2085E0GA08CE0AAC082C04EA5D006G5DGA1G11G71G693F01F1GC0BBC0GC08440F400A4007C51B03F8430830482C483
		4C82487ABFB0AE8468G88861888908BA07D96748334G8CG0481CC85C8G483994632721BCBA0579914BEF817775A26E6F258A9F2F607ED5164B079A65B652FEBF57987B9F7C7F8DAC1613E9AC1338A471C9E2F53D44E21744A609DF92279833A40EC93CAA631C442D3A9E0CE49C63D86FFF70BFC0E0E306AB7B3D0FF92E69579E735C502FBD6639395F7718660F7DDAE39A16965DCF470F7DD64598693EFE723C3453E6351A68C0CD30C86CFD54ABEDA49FE62B59750D5958E5F26DB31F02C1ED7725E42C3370
		8C994354406F36CB7179E17F47787C50F860BA2966CCF20A567B1630E51F24CE38140DE122AF151F9FB21B6E9FA0E3AF134554830E2A3375DADD8C9FC31F77F23CB31562EE51AF042B7C0E0C557977244B9611BA1BDFA0271494B9C114312E5BGF9356C6D6D492F7E620B2D797B9448FDA03B53E5F394B9698F1331814AEA2030184B7C70A3CF191D96F367F1354CBA85520B4C92AE73E3F31D364C3DB6A8DD1743E66DE2B26B215C0E188CAEAF72CA393C43195B3C4B7B46E4B84E246D377B10D546E53D317988
		15D55B6BF251D78C1613C5869AA59B58254564B8EAF374180C85478CFD068D06FE57869A659B3653649D64F7E1BE753277E2A8AC47B22BBDF98955B7629A0B7A8C16DED37E900B3F826D4B2D9C91FF28C228F0A36A33D50D10BF549A957E98C268D622FD97FFB1F27E10433FD00F7A1ED9774B917407C223429FFE2A5DA6346D4FFA43087A02D45D4212E289757D336B62917685552870044FA623845B33E95C8F1A754A6CB275ECA07F6DF399DCA6125A65E9DD8324ABECD3E64FE4A5B3EDE3E1C92970438D0DCC
		1FE65C1EB7FDE5C25F778BBA28BE0F3898266BB55515B3DD638B3E273AD41EBF1F1AC45BF64A7C6D790ADFCDC59B391EB3DB0FA9745411F74F169F9F364A15B1C5C63353EEF31A8A8A8F941608362A14BEAFC63D4DFB4EC766C0914DDD838C1F8ACB74A2C646271FCFDD5CC7C69B8D710286122DCDC2570983074EFF3E142A74CC4F58462D5E6B352B5B6C749CCDB59A0E005D96727FD7B6C7E1E9B15AFC7C3B2D5E6B2CACCB192C16D50232463AB6EA57C7D98EA629E514220C3B0E16EA55B3D982CBD07317A1FF
		556AAFA9FF1359E8263D0827045DEA66F24E3BBF23DC70B677EAA967A99E82987F4F4E5688FEDE9FCC8EAFD2C43ADAD6851335AA75AB4FB1B37210590906A84F7EG1A5342D2B10679752E9A8F1936DE18877519BBCD6EB21559F717D649E4C5970CC89F6B164542D2399EDF537F6B919A0F3AE9D47431C3F12DDC770F250D08BF5C0A2730CC9EF33CF0F804469C6E9AD57E709A96E5B86678D36EA14DBAF43E3A7F32D226E7306E080F3AF65E3409F6A5F8A44614DA53AF2E6F33EFC7C21F46748647D229352A36
		E118D3DFAE6F110FBAC64AFE45901C693A33D95DD67E08C7815C7E841B074C61BF3260B2D7170CD8193ACF51383E7A6EDF0CC49B716326E85C3640A5A3521E7F7869B437AF3EFCE46C7B510FBF70D9D5434FC736EC4FF54A496D2ABE3E55EBBBB264C7DEDC565AAFF6F85441E18F01D0E60B7A195CB006C0DC561253A351462907C03C3EB8C3C6A69EFF54700759B623F3A47C209E7AB0DD0DA39477439E7670D838336F8BEA576EBADB4F91DB2DEDC0B55EEF302A867AE5F2DF7FE971C1956FF91C6D1749ED463E
		767376DCC161B93315B92E308C1FAB5CD594B8A2E5041A54C5A47B4D3DF0301E5E54DCAA460A8DD6AF93B24D82E27C97BA3F5762BF4F0544F435779C9E41B2E2D5894B292BA3787FE9B9297C27AAACBE6EDD39C55DBEAB45A94B4B0BA0F97CF3AF5DEF444A2D2D5B513B3B2E5B54F95C33EE52B4F5697336ADFF5377DEE41C2FB9C639989FCEB8B1824F7041BBAEB43E603F05878A4BC51BF5C155C5A3542738EBACB2B83ACA725D3CAD3C08E5D7D8AEBFC72E7CC12F0E019E4E28AAB48DB1E5EC4D18F1103C370C
		3FB4D0178810990AEB46F61DA46938E60CAF0C2B33D9AC6C15AB24F507127729C13AECC9FEC73AAE146D45EC03ABCB2F1E27CC1F485317887EEEBF69237D24C77BC91F6D16BE5BA3BDE73EEF7E7A793E796DFE784778611F6A07BF518FFF7E825F7C0D8BFC738F78C98F7713BEDDB33D68B2AC7F00F42D749A113EAB4337FC171F74B0BF69B1FE5213B2FC7BEF5DC25F69ADFE52877D24C778C91F61A7DDDA64BB3D48CFFA3B1F7420C53E7DB7518FFF1C9F7E74CC5F7C65193E79BB7C240F72139E69A7FD269F7B
		32967B662FDE6C1B3F5BCF7AE8BF72237D704F76131E3344CF7B67A75D6EA7FD0C1F7429FE529317784EFF7ED2BF6D5FD25F7C83FE78437D24CF77A3BF59CFFAC116EF79EDFE52AF4E72AD7FF6BF694F79113FF4196F744DFE523B1679567F9BBF69F779117F1257F4B60E29B885ED240433F7B2125CE3BA097BFECFADD7259DA0E3F0311F387E94D91BE9D12C17E7784A535AEB3D361CA6E7D49C9772371F1EAF11FD41CCA6DB1BB651C0E45A9D669E0363A459G404F24B09DD31EC951867DA3B93FE2902EA117
		61B98BE95F11BD505E4ED67036360C79E9D93952CF91DCBE79ED391C24E7D762F997B110B693A06DF2F62D07EBBBG23BB20C73AC8381FFEB923A3FB8292AED74ADB388969522F885465DFA14B2B015F6DD7203CEDD5C8E7D150913904AEC85059B7A3DDA860A682CC3FC236AFF38C52CD2CC63AFC4069473853358B3AE736883D008B82889FA3534D94F49FEDC33A24B132DF487E072C5FA9737B2A20AB7A1DBBDDBD271B78BB3AF73F8669BAFE475287FE47740F066B87C2DE48F624CBD6494B3ED2A96F3BDA24
		AB3ED25627D748E3F49F57A15D2EABE57B099F5D753EA868BA2EF457BB407391FEA5217BFDBD528DDC497791DCA967E303206B5C01F489D732BD8804CE6FC1376AAB91AFE359FE85C2077649F48F883A7C31ECAF8315B7D626E379BD3B93695A474AFEC3BAB93FBBF6A11DFDACCBD746C1B05CB3F9FD8DC8B7E6AC33EB92DCA7834C9C2B74730E5DA2DE4632FC923A2CB0B55D22BD225C42185F885DAE8F3AC784DDF798330F5085F850DD5A08F4E11096994668E2BD683EEB9276C1DA56D5013AE2009A000E2B14
		F4B19624F3DD457CB76AAAC69F61C6F7FB8F52C5F33A582B18FE24E3FE9EE3C53A442B18FFB34749F26538F258102EF19C5BE3C24A9775EE9072BE35A31DF11C3B3CD8B7F933855D60B836D70548C37BE4F9E1A710EE74B816BE014B1D855764F1589EBC6FC03A2C2B19DDCA3A022BB1DED63A102E9AF0EDGAE00A000702B15FEF9DE50C5F23A69D7B3F9D961013AF561287773BE245307337416F0165F10F0E5FE9774A3DDC4B85B03C372CB64C607AB6B475D02AE164BC988E7F46957A84B777A81D15FG2F87
		E8876886883EC6190F13A7C57D60F4132EE1F6A6DC23ECA7771DC23A3471ACBDEFBC53FFE83CB29F2BCE0B76E5BC5BB794B21E498D9B2F4C47BF855DA4AEA7C6C1C76C0B9FCF68561E41B3073E94B8BD45BD871D741EB4BC5725A9CBF91ED35C63875234560293D8F7AD5FCFFE2D3C961CBDD3390C4AED87FC773578BC650E534ABDF85B1DCEDB27199EB74247944786156B44994EB6C87A7223E60753C55F78603824F71058CA4EA361E8BE36F8F1A5B907C9FD3E8A9BA508C3F4BA49439BF68E539DBA22136CB3
		993C965742DFDE2ABCABEA5421F7D396F9497FA41E6F990A7C571F78B61559F2F6E003DFDB6A7A6B449A7E83E6D3BF6623E52756D9C0EADE7ACCCDA50065EBC1BA16F5F6C4200E61EE973846889CCF351EA065C54F2DA2CF6D86B6B87BE144D4D5359B07D4DC5E434750CFC191F8AE55269EB2860C027B99C40710794A521512373330709C1DAD15F89ECF5AF5013A6A6B70D9484F76AA47E8AE400F810886E0E33B70CA726C6F5C1FBB6258703BD212877EBC15676D0DCAF19657F562EC2D15B55C068975932578
		DB8BD2354E564AFFEAFDEA66752C6C4B2F17E3E0524C0329D381D8DE4F6AB3397CC12019F4BD66B52CCFAB2F332FC7BF0EDFC5E2DFDE96093EAB5B043E13EE00FAFE834ADBF4D6CB1E7D86149732104431E215A04F7F34CD187FA020E5BA3A2BD0C7946092EF4098FA378AE90BA6A06DE51B11568838B193501E3939CAFBE6GDE3A915A739B31AC25EA528E1D6333F1EEF3CBB55A213F916DF8329A6D3083AE62C63423AEC74B0E789BB1CFBF59023292052C05DBD1D651CD01BA63CDA8EB4EEA252CB1B7B1D913
		EF42BC1D58BA5C78E4365C3395ED093D89ED59338D63A76B77B0BE7ABD6AFAFC9B5DB3643ECA0047C8CE8D7284GFD2460393A067028D6BEF01E6785CF825097CCC41E466D02C7DE49416BD37FF632EF4F6D253C047D485E754ABA243F35CA338E65C4E624F6CCE4F5A7F822DC07F6459FCCCD92F5680F5B5136D820493A99ED2B2FDD2C340DAC9B10301EDCD80BBC3B00BE60E674E7E39D62A38117A0704B6B91DF7087988B7E817131829F8A3869FF407A143D15DC695CD664F9E28D66B9B2E51CB4D034C1B34F
		F40CFF8B4BEB71ADF21E4BA14F71A24F23F608B2841A48DB501E1A2FC4B9812EE0926293853E83F02113B0E60FE7E8456CF44197319379E682AE6FD644DBF7E19DE8835C28DB313E2CEC40981DACE847B6A0EDB2604AEF4372193F1BBC8FA06F5018ADD31A1536D8EEE33604887A8D3B09ECFC35466D580D324301AE6EB64C5783AB35E4E55F0EB6156FC13E8240F55C0EFC4F2F56628B95FCEF8B3E89001BA9784665E87165C5A2DFF4A37A3198F05D11483731D24997964978260A74F125DAF263045C8B1B501E
		8440957C91795E57145B71C77465AFAC6E3E6F2E505263927437DABCFD7F0685F58783DD3420F57530FA2FD891A209355DF4DC20FC534AE44577202CA410D37CA714F558CAE4392D2A90F578BEAB7A349B684384CF11ED386DAC13FB05CD34A7A0B36B8E74F533DD3499008B907895A7C41B813884011FF6C244D89478BE8A6DABF68C27EFE33C1D8E4FB14A858E2CF321D128FB0993F5CF87DC7A1DD8BFEFF3A2ED4D1DC83B4D05768FGEE621D98C347AA34E2E8561D180F71AE7798DAD42995C349027EA117E7
		8CAD69C5BB337E8C7D571F11363777470D652E6B757453833D187700BFE35E5B7AD0FFA46092047EBB7AFE1C7ED729DC651283262F38DF74A3F7C1CC5C057A2E6B572E977F6A97E7D3826DF4C15FBD20164F70178CE84B796BG6ACD0F06F9E0B416F1045BF34F01E8D6E6E15128276A24FB99DF2659CECC9274C7CEFA16713813180729C47E5DC81BF1EA7875207A1432FC191E41D318C7BD68308BBD4F1D9AEED9323E7CB7ECAC653E9A016B2CB90DBAA3C0DF3C50F9ADFD56CD96AC307B07CF27AB4A0571AE9D
		C45E1CFB82F5AD77A0EF9F1D6B62D285FE66ED29F25EEDF9E713661937F41CF48F9BB30C39C7BD6F7ED8A2F2A3819FF38F4E3B9386495EF31CF73702F560FF8B993370FD6EE708CD47CDA7797D38B34AF18DBB57A1DA27BE27B57E9E79AC1BC4456F74FB59EFE6CF87DD9B1187B8BDC08BC0B7C0703DE8DB618FFE4F3C5DB160634C5B4C74211CDD9BF12FEC9F630B9A92DF14875F8CFADE6DF485DE89B3D57824FB711C599BE95F17B9191DA7CB7C4D7037D3FC1E824F5B6B9E0C1BA2C88B190CE74554D52A6DCE
		1C2CBAD76EBE77FB467735875F8655FD390A0F550B257B3CBC9FE04B00A4BCCFBCB0DD796E2671BE56E658619AF49F76CBFF5ECBE2135433B177E1FE5E7630CB8B247BE55BD8DC5D16C21EF9E5833E98E0970091A098A09CE06A7D98DF7DBFEC769BDF24A1E83331D621CEFD2282775F85FAFFF6EA7B7DB7FE79E65D6F4E474FB66FA33EA263A2E98A7AEA532661157DCE8FBEF5599B27E859D0FB96ED9894B6BCC4E545CF510EFFADE0FC562A6159BE68412736BDF10A96DF6AE64217B975DC6D9C3BF9F8F65E68
		412736B3FF2AD67DB23412F3F0144F4AC88724DA594F6B58E3796ABA26871907G6CD3310EEDDC03631F6029D8E67B2B07D24722272A5B3A27BD7806660B17BD78543E18B1D5AB5ECE65206D33056D97EE990A6D6B2629ED4F5ABABC5BBF7060D35B2E1F26D50EAF2C4D53A8C7652ED2FE6EFFAE6631E59A66F10A07CEAD88764863575B0657A69D764BB7E61A26EFEA06272F48AF5FA4CDFDAF8DD35FDBFE79E2D47AD81F72C59AA9431910A67DA5D01783D08E508E6082887D8B76A953FEA07B5FFC1F610E877D
		C03F02BFBD3B96EE6FDD5B07174F877D72CD7C0B965F285A61693B5CAF5FD4CDFD2DF54353576117AFDED3DFCE7D7074FD6A17AFC9D35FD34354B75BBF5F83DAF1FA43BC92278510D68FF088E0GE08CC0A4C05C8398276F1F759B2762B3GC30953CBF68CAF1FE1FE79929F50627B6A2B6169EB734B179923457776B075256F744737AEC62B3F9B1701FDC1F58C7685B7791525EDC314DFBE17E61EDD3B0657CF7E5403EF036AFE14263EB58DC3696B42E354FD5DB5FE79081D119EFC3778650B831EC4859F6B4F
		DFBFCA46DA9A739E3A7703151F71EBD2FEE46E1E76A0161FEB77706C7C29DF3E6A875D6DE4B6242CC29B5A048DD5FB06E743AE3FFC23B4ED484BC59B42058DD26370ECD86217AFCE5306A7F35006C4E1437DC33241B3C6E77865ABFAC8EB8C58E5D9239DB338670C5B5AA35EB757BF046376A7CB517E4107507ED1CD43736165FE79A6BD246543B6E1D70C30EBEC05087107512EB996DF722B01CE5B2F99BD3E78BA00EF60E1AD3EFF7A64732EAF51A7DF8870C5E86AFB40BA3C38F952AFDF34263E2BEC435377FB
		3FFC311A7A765B07272F53AFDF02263EB5A706272F50AFDF7AF4AD3E07BD780656277D4303EF036ABEDFD35F4F9DC3695374535575B45BB9BCBBBF7260D35B5928E9679D9EFCDAE07430F307EBF865374FAF5F004ACE5666DCD5016DD048F4EC07EAAB319D1ABA9D5B216C5EE17A4F03CF6DBF6991AD3E699EFCDA1073085AFF7F6D9B1E1D5D9EFCEABB0BB46D5C58BF3C722A774BD72F29AF41A71F777679DD1FFC07C017DDD35F1383434B5F8BFE7902D57AD83F3E750037FE1DEFA965F17BF4B92E118BFB8463
		F6FA8562E29F41380DB8B93CF8384D036F83F53E9F559C2B1F9A1E3FC27C7295E86A7B6A74B067EDFE79EAB4752D70790DC96F7ABE764B572E5247665FAF4EA7E5EA073400C7F12EFD7AACF9CE0E6F58DAECE62BCB4277EB85925983E3F3183B4CD644FDC1F11DB687BD5419FD0362893A96B584E4CE84E038592984B7836EE78B5C62F984177557C05D3A3F227EEE1D77332AEDBA6D332AFBF55AE7D51F56797EBEC7204E5BD9564C3E0F69BA53EA302D6D2FE873DF854EA2F0AF51B525C1F09FA1F03547B13FD1
		82F7B7DD17928F7709FF45BAF4CB8FF977AA4D407BBB693DDEBCB75E51B3146FE51C8B90DD6E71737DF957112A787DBE3D1F36F2A9AD8B27536424AF2A6A8DAE83CFFB4135C252B883FC2F2147A40973C1ABB3B84D8E172D091C2944673705DE6637AD86AB4EEF6FC84736ABFF862E79ED7D013C17EC07FBBBC030707F3DFB553E880334C9GD3E73857A1A544C1DA02AABD6D31C0DD4EE36EB896874B7434FEDC9928ABFA8C63BEBA45FB5CCFC9510E7B87D3685AA5DB2F2353240E6D99A9B9A2CDFE90C616A690
		37B21AFFD0FE174151E572289BB3A9BFCB73467FF20A377A434AAC0A6EDF43B2A30787B39F477529FDDC83FEE88368FE4C3B0F87A1ADC4159E2EC14FF45BB73969AE916F904F2AF5C7020C9800F89F3A93A1AD7DF177743C47BD69D979BECF57A197C3FA4D6338CE621AAAB7FBCA11B6F7B35236895A47B7ABE31D1CE5435E1B3D5223FCEF567DB85B2BB79A2E910FE3BF3DF8AD46FA7463986B477612D8CF027B2CA782F585CFE02CEFDE632B5F28F942DB7EAC55983FEDCF204DAB37A8ED5EA8213F4F66785251
		6DD5476F36200EC121E367D6250ECA2123B257677C582B0E2736220EF021A3F81BD2C70908277DB5CA3CFCEE47C75B915F62323514097AD319663DBEEFCD532E4F75E95E6B731EB47F7539AD4DFBFD7EB64DFFFDB62611F575C74CC633031F8C4272781F6D4A3C4BE7B85DDB2B4C3B6C130B6A14F859074D75CAB9721956ABF6ACC27A238EDB8FF81159F36BBC6FBE0C1A276D43FB66F9776134F97EFD7868BC6FBEFCF21EFF9FBEBBCF5B0753D4F9446FA51C794A9D0FE70A166CF4471773D83FB643D72CC7EA44
		BA13777CCEE51948E5F843AEE51968C55DA8ED403A9183B2D9BF7D1CF51548BF6D431B7BCC8E0759E822BDB51397572054C35AAE461B31030CEB2CB62B0965437CB5DDFF416DCD645774D8F64D0B75160F9BF6AB654B6D4C02D5C4DEB1672B6757F6FE35F815F7E20FD21E5C262C25EDCAG679B4D2F917C9A65D5DEE523D21EDE48FB0A360353B95FCCFECD62574CA73D49FB47CD5EA6A14F61F33DD13E06BCEEC7A961AB67693B7875903F5A3D5AF1EF53C2059DF2DFFE50279DC1DE6D882FA0FC21BCFD823FC6
		736B8C8DBE96C779E7C98C32BA6CAD86CBFB084F441AEE11075DBDEEF990E7C23DEFA574E25D36202F303A510BF3DED3ECEEFD39B83BB15666DEE7ABB8FE145D0DDE1C3156FA421D9E4FA81B6FF0232F40BAF915836B642CA7313F5A63F4235D08340FBA11B6D9508E3A5C72D306E5B13E42D799E67D4DDB996E24E53F0E276B793511DFBBB478384D3DEEB6D7224DAF74224D2E3F214D3F6EF323955F626869F7779D1EFB371EFE2387579AB35C568137FC8B7FE71DF443D7E03EE61623BF0295F99843FFCF746A
		0F47049FA7ABE8E2796F59DE7D7174C937BC0AEFFA7C711492AF1FDFFF6634FB5E759C5FB868161F0D58EF3DB21F2C49A47B19587DEB741EFCBF154D21EC9D646C0CAE07415EED6EF4561B1C1D8E331DAC5712867FCE471DBA526FC970BB9F2085407EF775FB461BE9F99CF598FAE08A304FEE6A145A6388EFEA9A0ED9A783CFACC092C08E246963F07F516F4B48152E4396BED9B7CFFD768523DD798D292BBDE6A3FDB4C1669AEC7DE9FDBA99630645295725A6D27CC40E1FA764279CA7F3CE2369282157622A33
		581C26E63B49E1606721A5D35B5F27F3F842B3996ED9FE8E9E477C547C83463FGAE0051G137E01B19BEBA1BCAE330B2ECD1E81F8E8616992C8169E56C47C4DF72642C042C51FCFD8CDE20D325B1A5E1C27E01E78146C6F1A27BC63086575FEBA27EDFB0A65F50140BFCA7196851E2D4DED27F8F29F84E9331E4267944F53752D0DCF876ABA1EC6DCE1B3413960BEC860FECEF1A1F09FA1F00FD3DC945C478A5C5FCA88EEB65C2745A36E53D2FCBE12A3F0DFD19CB92B219AF02C8C8A68DC19585DA2685E6DC37B
		0C82D75B0F768D885C557DE8DF10403D5A0F76058A5C23CDE85F84017BE0AF5A97ADF0663328F726407D61AC6ACD9238D5926ACDFBC670CA28B7C760F674215EA2013376215E1AE7B01E12B7617AFC661365D5689363B368273A4D782E41758C562B2E9D244F54F8B6322F53E133D8D84871E73E1FDAB156031E41F9E3FEBE090FA8381F81B07B991CB7AEDF0B711976AC0B451CE76518ACFA56DBFC9EEDA672EA1EE5F178314087D0FC08824F6253CE672D64BE9E52121FC39F352E913173BC66BFD06C236DFE9E
		69FE1B03F48302EE1258A3BBD5506D4DC53AD8C17706583FDA70CF247BF38652697F09F48DE28FE130208B206BA9C261FE0240BDC4F1D1F09FA7F0392D8417G77528B08BB5B0ABAB2856E03D5A86F1040F52DC2F996013BE1954A8B9038CC6103D1822727790D037B84013B006224D950464FC25CA339A82FDC60CA056FEB846E0D9C1497A2F0552BD1DE1840852CC6F913846E1955221E895CC515084BFAD16820EDC3875C3B84EEBB7D06F1905C078B5CAFA8AE926E278A5C40A614B7C3606CC2473AFFA16E8E
		7A0E438577418237E0A375BD5CC78A5CBE0A0B017BB8010B5C0872E67D8BE3A3D26CF5533F043897451E36DDAFA16F1D95485BAEF05FD37BA260BECA602E273898381FADF07F21B8E9B67C1E0D38EF75A8AFEFB66AFDD76CA733883AB974BD4FA05C078ADC9245CD007BA801BBA86C1BAAF033AB10B767E544B5EDC45EA2017B03700B7EE53117AA0574FFF4F9718645E7B8E03CE2FC05BEF7CFD9A7316F2B924860C01EB586030C6CD758F37B72D7D05E4329E4EFCE1F61C817792873907469765ED3A59BB7409D
		CBFF11DC783E661BB7601998239C37C9B587ABD397CB5AFB13A2DEE16DD88C3F123DC99179D724327632B08D5B6034D7213FFA15603EC9D36EB7A3CA49330F8D248D266997392513BC5C29D3265F61161E61417F32DBBAE92B8F2631F416374F3F453C35ED7C13248FCC771A3720D779FC16DF638EBAF5C5CD1D292CFD5F1DCE7A1B78D7455E967A7CBC916E539350676547C99F2068D6B6FCE3369AED649490274BGA3338D758D7BDA1A776DB85C3C77F0DDA37CE0B6BEA3A97706E5C515FA5D9B161740ECAB46
		6BE1A7FF46F976B87A3EBB81FD3F4822767D62D4256F36D8543EDF6F66DB1BC56D7BC5EE7CD7D9543E4732E1797AD22F4C5764776726683C64AB0C67E7C2029CD77C3DD93A67F8201076B971C01B1800380805227F7BB716D18C9D4BF5437DA040E8010F25F8F2965BF4FEE69BFF677343D05E7990D95E523A399E2DB42623FFC8BAFCFD67BD154B7216761E94C047DFBDD6D30F4DEA6EE4ED5D92BA868DA4BE7EB7466B3433F4EF28E9404546636F9291DC4686094CB58C96F317151C58F390477F373031AC4818
		AE648450FDC4F60769284961B0995967E8B97DDCCAAF3D86FD77EBC87FCA3A1B6CF624CD257D199E689A8186DF23B1DA1A63A52FCB048ED7A9467EE421E31B1E787028416214EDD9CF4739D3GE2010E6579A572B815274774E35EE63D06FDC48E1DBB673C8EFD626B081B5304F6363C0EF6D66C75EF672534DC1E8688F89D755F1BA56B7FCAC27DA1C257E594978D77B182B7BF8375A7897D977479524FFCD2D8C5FCA4FFBB6A3F1B49FD09084D4F459A3702C44F982E564031316AE8BAA6EEC9D44FB32F24780E
		C4757E47CFA91E5C5BA1EDC6222857EB317F543FA14690FB687C826EBB842E32896BFA509BD8F7AD4D75F00D007B29G33G527E83FDBDC0B540A100810051G91G51GF1G49G79EF828DC09B007DCD31B7094A1B4C4F36A3675611B3694839F8FAFE1EA2B92B0E1C41934B4F232B6167473571F3503A79B9E1CCDE5F5EAC1DEC9F51C96C7355864479C9A7C072F94887DD5E90E4E8817105C779F39FAAF1915C27A24E7F43FCC97C98BD845D7C7DBE758D0987D2E7497CAC438F5F6AA1640148DE6AA3FD51B0
		7CC2E02D86EE31447ACFA50EBCCBF9CFF1CF1EB3117D00E46E36EA88FAE25F24F364B5757CBEC423CC084F30EE9188871AC800955C17B1EF323A07B46FC917D753B65F51EBE272EF4CC17979C95A7215D01E64AEFFD712DA7E83DB5C651BF2D1FE1B97797394723BD57287BD64E7EEF3177FD8864A8F76A2FF01C27E98157C099E72BB9AD0BE3B3F144ED171BC0419C9C467CDDF931A28A46CEB1E7F1A14BDB94B4BE2B8E9B2B6710ED792FDA0157182C041DB48D3526A5E77C690913C4C2FEA45BC695F724C1352
		DE820DEF49F9A2780E3764BCB1FBF72EA23AACEFE17F91C26744FF87987D9676C357AC1775FFACE2A25C0BFEE5CCC42A74473C2576E9F32E278F0F66921BE205BF46647A7661EDD48679CEC53230794CFB324D8D221EE43E2DED33B24E725FF637397CED354FBED8CD6C2BF99BFDE622F3729B08FF5FC67D17E74B7A13D623FE3B977D4AB88CD2698FD568E77242AAC599E848A3B2B214E5GB4B1EF4BF19D7F36C73DA973AC030F4BC89EE73F0DE550DE663B8C9CD446D86263B9624C3EB277B87E598A59A73F5A
		04F9481B236D93E59E0A0126FE0E6C1376B9EA1F7CCC784428A10F4EEF94728600E6549C59A7E1F354BEF9344253A7AFD6107CCC90797BE023EF1F6869DC7EEA9277026758C6F71F9CDEA97B64610D1807B8AFBED166E114A28FA4AD41A38F8FBB5C5BAB49655EBE4E28C0FD49DEFC36D421AFAB99463949324F7449EAFD3D151EBE9BD5C9F456A7E37E6F2E746D333FD3993FA1B402E7315E5DE7CD2BE41FB9741887D732364F14F988861A60E459E7E3BC7230224F5DE7872D5E67AF6BE80CC4000CC942D6232A
		FCBF4911ED358BFF47EB58CA7897A9ECCD849A829FF37B525F11EDE57AD750784A86FC7EBB586E3C2350B7CD44D363BB5A3E5152A77A8A053ED5B4EF5BD38260FA615123702FBB38733210004BAEDB99FC24B6BDB835EEFEF0E7DDFAF0CA6D7C60A37579D2F91D145ED9DFDBDADF1B11FFE4C77F2A937575451DBBCFBA7B334B730F6C6C6F6FDFFE2438732BD57DA776C9730F54FE3DECC56D7C6E4E7ACF762F83B9C7CA495F7CE0729DCCC9DA998C4A0E92FD781775AE49DBDF8ABEDF30001DE45C154473481E09
		ADCDD5BE9343B6116E17837E30F7E5FF3067919FAEF6BB7B5631B275BDC97BF9C4B467CD6057B623D3475687676692672F1B9B28AB8758855081608288190B6B963FDEDC88576D76232DA676D23E49E0B5F45113EF2B2AG5D5083F74AD70AFC9C3CA1EDA8FB9014407235208D1FF10D7E7DB5D237E52E57B3EDA74C654F6F7835FB0F13DED95EA256103CA5C3DA0E94242B86E883688688GE0F98BDDCCECA4A72B984DE4A73C21331B1E680B1B0E49B135B21A3C74E4E7911B69F31C7DA912748BB00C61F269BC
		769D38DF150238920AFB82F00BD2502E877288EEB458B0D1587174624F61FA40F04C7374DCC036B8ECF61343F512E169D78F380F1FBDFE2EBEE6B65CCF26A57CDD71A7CBBE71269BBC305B50E7E8BEF24C5469EAB3F4C9586654E410B561509E5AFADDB017528511E72399B8E71F8E791A85108578F5GF4DEC96B77DD8B31FE8781DE2BFE13369647B621C0B381E0853F0F065F6EED5FE3749D9EFE191E61A24BB2850EFD591E617FDE35C86069B741380CCF1A49F57D7675F4BEC36A90FD46C37F6237B11A3FB4
		13B6CD9C4ED5E76B315B2C64002E3A9DFB5BF6349E5E5E5830F3EF937C666F02BB08A86C53CA485E872B29BFDF70571BA11C1CE61B35C0DFDC14DFDAD9A879501DDBBAD45D2C7E4CF1A81F91E7DE2357FCC6EC7CB4A9A50617C1823F126714B5EBCE25B13D9717AA1FB9B15C56FE6A2B4E757C7E397EF3734B19FC59AFD31A066297C63BFC6FD075B07A1B4ED2BBEDE867F7523959F959DA594E2A3E215B79FE46D07570F75AD6FCC634AEA508BF8FFA65C371BE85F0FC1FECBE69238F097BF0FACF4E2DE277C579
		3E1E43923EF3E9A3544077A97854117AA36453719C9D9F3452E7E6F630F79000571FBD1EED4DBF77E0DB9381F451G33G128108CF58BA0E3DCDC8131D9A24ABG5885508D901C0A32C3361376C6790962C637B5AA13E93A72934B6E69C974CCE17CBC34FB5AE735243DE15F84E6A98CFF7D76EC15CEBCF70725BFC55335BE6B4C521755E6A9F43A275556AE91BA4511CCDC6F3735249C592E89DC1BBA3F1B34157CE33054C806AFAEDBAA6305A01676625E65BCCDFCC817E71C253F39E305BADDB60553949CDA42
		E9648FDEE25AAD290BC41A52EFBF64A00F7CE5CB6439E259B2E51A586D06EB9FAEDF03EB9FA2206C355ADACF9C2E6B99A447747336B54F09ED6B6C1D93A457BFF2D3F12C2B0E6778D4D6670A53643635885AD6E64FD3B9E8CFF71A36BDCC7795C4BC4F43EFF311A74CA70FF31D4CB3FFA172447A6459C306G9873DF13B1C217492595228505429741E54A71E7E24322D4FCFE2776C556BC6603FCFEA56F79182E152D341DF62E6776BE5A0A76364C5332B7015BFB74D3595E6FD6F1FB352A8C351751DD3B0A6A6A
		C1DDED228C02FD6A5A3ADE56B5183B0269D2D7BF051E87F3291EFA54F3CA44DE34CFBDAB8BE4BD652B5FE3FA5C2BF2811EB7BA381A6A580FBA8AC5ECA479547161E7F2ECBCD209BC45734F35CE3C7919EC6B26B25F36DED4C6EDEDC6DB23B7095857541B60C36F431F4B76C78A7B434ED94EADC5327DF3ABFC5B5F3C115A7FB55AAF893D312017493B788B592EF99BB1BDEB41395A5539C136EBDC25EF3BDE235F96DE5F00F6DD2DC73D3BB475220E4DDF4A3F5B0BE5FDAF177B986714D740B8273850FD5FBB5195
		60D3D7F1097CBB3DD456D53B51072E8A50D5D2A9F43DAD7C9939G7D9DCFE531B9FE881D63AB7F82G91CD7FA4E97C783F7CA5A1C112664E1DB307DC4360AFBC1C5010BFC6BFF72ECCBF177EC9677C475E85BE1AC27AD37AEC8BECADC88F625F68B08DE2DB99B4E71CB4E67D62D4EFF35A0EF456C68670AB192B33FCFE11DA8357B3C9B047CC06FA4EE16E3B4CFE8FA078E4994EA6B1BB59330856D4FC96B1817450331039BEB6BF0CA9A31DC8BC60928152B302F4F9G65G0DG07G6CG23B2F07E3AEDB5E93C
		5861DDBB79AA632A2A3AAEBC8F5A081BFB6843824673B1FDE6EE957BEA5667AA4F72E53822DC72EE5D419FBCB49891DF1ACB46B2C60B056E89607B31F249D84CECF512B0F2197BA8B96E474A4DF2CF2313E81657ECFA06F48464E5B240AC00E4002C05183707F279F7FAB41E8F6CB0F619F0BD8C6A3AF90D77BDCD11EB4843D33E2709F0F502A58EB3B9B6DAFC47706EB5A44FC7ECB0CF6DA17291BFE58D49334BE627C8060BD9C3624BE16E6AF6B1D20EFF1476B99653D10AE63869F924FC45DECE76980153BF11
		17CD7D833EB1D84C2768A269FDC0E86D625F05494038AE823F989786717527E525EEE76E120ADCD5751545565F48076B3C8ECF4940B58DE300172DBDFD034A1C817733815296E1DDB9150AE32C64425B2441796BB4672FC5F7ACCF4DDB44E236E611BC469AFD62DB3EFEABC10CFD8697E937D15E5B2D71E45B912F8B8115E49EB189E4CC87C884484C8C52658368B3B1C642BFA4716F6D798DF3860D930741ECF571778FF46E5788B25A8102B2493CE6DE3E7FF58F179F154771A55C5EA7C9734253EF5C13AFF9ED
		DB4B0AE142DAD6DED1484F3BEF44F20D817DE90B31DCCBBA449921F2396EF4180D3506AE169B6663830DD82E550B31DC0328CC975C0F828887E0342EA6240D829CDBF3B52EC93D266B5A94651A2C99CD6AB5DD5320F51653676FD52F697AF52A12DF3FD73D26EB9F67E7F35A31EE7BE393412E74A53E674AACA62650F7C1F9C02B87B8846082888688831884B095A08EA091A0F3A9448BC0B940AE00F6G8BC040D20C1F3BBFAA7491BF5C71F2G897B1FF35BFBBB9A64C5AC75EDBF633B42EDBFED9470CC9F925F
		2205CA3E19401364070F1FD73A19347519D9185FDFFE72110F7C56590CA6567A205EA7DA15FA73C1CEF956D06CBD68661FDD40F3E8C8FCE75D62428EBCC1C3625B606657D06019B0A43E475D7658C5834F0CA1715D67F6DE76AC60C99E925F1DEE7A3216856956AD9B8A5FBCB7BEBD70B48E09EF61C6A5DF87703806449761F6A6F9B0700C99921F7BFE370940B3F9C8FCF35D6C0C851E59C3620BCDB95767707C3C38F366E37529261A3E2CDF9E244B86A8872887B8349C6BD7FED93E24B59E23DF3DB299DB8C96
		134BE5E263CACBAADD34477FF07C4B7A07CBD231FFF0016C89C2FEAC5D4F55EFB63A3AA59CC37C230CBC276BB6114188621E25F4B07AD3503DD0C6463E4ECE0355E2361ADA8CC60EFF19BE37BFF252E5F236189C0D1046702F53F772C6034B406E5FACA33E3333ACF05E64B21287E89C2DCEFE980CF940E4E1E929656EE91DA71B8C4E63ACAD2F427BD86D638A7FE335A2BA8FB241982B6D241D5BDBD2C14F50B9FA54E992FE58D811A5F08D4E969B99045A2CAC86AE70395F5BFB6C7CE618FCB1674C4756BF1A97
		12721939DCFE8743708E0ACFD060D96C4C91E346E4C86BF8AF083FF37C6996B246198C7771GC9GD959C13AE20046EC0C2F87AA48445467F940EDB6FBA3941CC5F16C8F4B5F9BEEFDD48748F4E57B2E43CC67C5155EE3A12452FFACDCD629BDEE9FDB69B9EE8F2F549E37337D9FB8EEE7E33BE55BC83D646FDBBEA4755A5B582EC48FE33BCD4505EC9C7D50C66533CAB2B67636D7A0187BE7C236BC0E1663A55B6F99235AF17651B0793EBDE7BE9607C9F45CB9BBDB9D1FBA7ACD3224ECF5FC7EDC0CFD53D68469
		3AD7603B1513FE4FA4D583FBEFF39B1DF3452CE0FA78FEA30A1B49F12C0C7F3E49FB1CBD3B49FF1C3D34C9BB4E7E3D49B34E7E33C9BB4E12B7F94E8FF5D5242D6E0231E63B201BAFF08785EED1D58647FD63567EAF23675F907CAE37BEE0D595312B1F625AB9AECF01BB48F1EB2BC83B89D96EA66F82986E53AA52769FB5DBF8DB7E9925A173F81770471754C6335D20303BDC608E0A32F8BB4733FC6666A84B076FE74A75243BBD57136E23051EF46B97FA16F749E6E233130FE779BB3ED6CF5E57DBBDF558B5EC
		6E553079379A31F7459ACF3A9D9AF95B2D113717ABBD69DE2B74240B5B64C9774CA6CF3A999A721E54101727C137D603EE6DC6CF3ACFB7FA525D2577241B2C7724E37B735C696E297024DB29E15F1ACA4FB25FD5CD47C2862B51A26AE7D3B5F9EFC646B72DA63B436404EA4FB72333747DF4AD3353FC0A57470355E41CE431590E775A5BC88B40698E53EF475A1C2DBBEBF7D8A12ACCCE56EE05268E2F3D9DBFCCBEFAC4867FBD20782DDBF83E4F1A73541DBB22BA181FAF5E623D1D3EE40B7659C421DB344FA93C
		F20B37F354D87AF82A4B697659B054F563965216597A69710BBC66B297F9466132C54AB8E4E32DEDB5F4F15D4AA0DD9EC08DC0BB0085A0E8A50E355A3711F31A3C4D45DBEDB6FAF281B94440GE3F20762A984BF9B35D1AE7B85CD726F1046110F036BC6D4877F6E5127243E71E723344BE3783C0145B29E3BBC2C7FB55EE3273946FF9F5FDE235D47FFDF635947F756E87771669AE59F4F66FAB97679B0101C7032B44FA5ADA52F4B7345E7B51CAEE99EDD9665126633E5D3AEA925F90FCBDACC5F836773972BAE
		E9D91309FCD65956E312F12B70FDE83E1CC83EA86412D63037D79EF8263D3957154F5E913824A594A3568CA02241AAA8527273A18A25F4FC4F6FF4C84BBD47415BFF796493938E57E727C8A98D52FCB69FED106651496A001D3B2745F9C27A01AECE563AF3C8CB055C86BA58086FAB2BCAFD6E6A5494A98D043202E9105208F7C1360369C80502219A5214C0E5AF7B9F568716F81EBC5078428F8DC9299C0EF49D8C83F5C5626EC2002D380EA1EA07F29DC3D176FBDE77AB3767327911A5D71EA771EFD952BD7F91
		001F1C0B6D2A53C1DE8AFBDBD7392F47E031388C16635B2DC632463216BC7790F53740C964BB1106A772712E1358FDCC4173FF81D0CB878831EF9A0ACAB8GG944EGGD0CB818294G94G88G88G02E68BAB31EF9A0ACAB8GG944EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG04B8GGGG
	**end of data**/
	}

	/**
	 * Return the MidiInLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getMidiInLabel() {
		if (ivjMidiInLabel == null) {
			try {
				ivjMidiInLabel = new javax.swing.JLabel();
				ivjMidiInLabel.setName("MidiInLabel");
				ivjMidiInLabel.setLabelFor(getUpbeatSelector());
				ivjMidiInLabel.setDisplayedMnemonic('I');
				ivjMidiInLabel.setText("Midi In Device");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMidiInLabel;
	}

	/**
	 * Return the JComboBox property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	javax.swing.JComboBox getMidiInSelector() {
		if (ivjMidiInSelector == null) {
			try {
				ivjMidiInSelector = new javax.swing.JComboBox();
				ivjMidiInSelector.setName("MidiInSelector");
				// user code begin {1}
				MidiDevice.Info[] midiInputs = getSequencing().getMidiInputs();
				for (int i = 0; i < midiInputs.length; i++){
					ivjMidiInSelector.addItem(midiInputs[i]);
				}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMidiInSelector;
	}

	/**
	 * Return the MidiThruJCheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	javax.swing.JCheckBox getMidiThruJCheckBox() {
		if (ivjMidiThruJCheckBox == null) {
			try {
				ivjMidiThruJCheckBox = new javax.swing.JCheckBox();
				ivjMidiThruJCheckBox.setName("MidiThruJCheckBox");
				ivjMidiThruJCheckBox.setPreferredSize(new java.awt.Dimension(45, 20));
				ivjMidiThruJCheckBox.setText("  on");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMidiThruJCheckBox;
	}

	/**
	 * Return the MidiThruJLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getMidiThruJLabel() {
		if (ivjMidiThruJLabel == null) {
			try {
				ivjMidiThruJLabel = new javax.swing.JLabel();
				ivjMidiThruJLabel.setName("MidiThruJLabel");
				ivjMidiThruJLabel.setPreferredSize(new java.awt.Dimension(55, 17));
				ivjMidiThruJLabel.setText("MidiThru");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjMidiThruJLabel;
	}

	/**
	 * Return the Sequencing1 property value.
	 * @return music.Sequencing
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	SequencingXX getSequencing() {
		if (ivjSequencing == null) {
			try {
				ivjSequencing = SequencingXX.getInstance();
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjSequencing;
	}

	/**
	 * This method will center the dialog on the screen.
	 */	
	public void centerOnScreen() {
		// Center the dialog
		java.awt.Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();				  
		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;	
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2 - 50;
		setLocation(x, y);
	}
}