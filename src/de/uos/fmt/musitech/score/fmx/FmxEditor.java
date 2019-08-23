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
package de.uos.fmt.musitech.score.fmx;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** This is a simple visual Editor for GIN font metric files (FMX).
 * @author Martin Gieseking
 * @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ 
 */
public class FmxEditor extends JFrame implements GlyphCanvasOwner {
	private FontMetrics metrics = new FontMetrics("Arial", 160);
	private JButton newButton = new JButton("New");
	private JButton openButton = new JButton("Open");
	private JButton saveButton = new JButton("Save");
	private JButton saveAsButton = new JButton("Save as");
	private JButton resizeButton = new JButton("Resize");
	private GlyphCanvas glyphCanvas;
	private SpinButton glyphEdit = new SpinButton("Glyph", 'a', 32, 255);
	private SpinButton heightEdit = new SpinButton("Height", 0);
	private SpinButton depthEdit = new SpinButton("Depth", 0);
	private SpinButton widthEdit = new SpinButton("Width", 0);
	private SpinButton xOffsetEdit = new SpinButton("x-Offset", 0);
	private JLabel fontLabel = new JLabel();
	private JLabel filenameLabel = new JLabel();
	private File fmxFile;

	// the revision number is automatically updated by CVS
	// so don't modify it manually!
	private static final String revision = "$Revision: 7766 $";

	/** Constructs a new FmxEditor. */
	public FmxEditor() {
		super("FMX Editor " + revisionString());

		// register the action listeners
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent ev) {
				System.exit(0);
			}
		});
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				newFont();
			}
		});
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				openFile();
			}
		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveFile(false);
			}
		});
		saveAsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveFile(true);
			}
		});
		resizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				resizeMetrics();
			}
		});

		glyphEdit.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				updateGlyph();
			}
		});
		heightEdit.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				int height = heightEdit.getValue();
				glyphCanvas.setGlyphHeight(height);
				metrics.setHeight(glyphCanvas.getGlyph(), height);
			}
		});
		depthEdit.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				int depth = depthEdit.getValue();
				glyphCanvas.setGlyphDepth(depth);
				metrics.setDepth(glyphCanvas.getGlyph(), depth);
			}
		});
		widthEdit.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				int width = widthEdit.getValue();
				glyphCanvas.setGlyphWidth(width);
				metrics.setWidth(glyphCanvas.getGlyph(), width);
			}
		});
		xOffsetEdit.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				glyphCanvas.setXOffset(xOffsetEdit.getValue());
			}
		});

		updateFontInfo();
		// put buttons on horizontal panel
		getContentPane().setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new GridLayout(2, 1));
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel fileInfoPanel = new JPanel(new GridLayout(2, 1));
		topPanel.add(buttonPanel);
		topPanel.add(fileInfoPanel);
		buttonPanel.add(newButton);
		buttonPanel.add(openButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(saveAsButton);
		buttonPanel.add(resizeButton);
		fileInfoPanel.add(fontLabel);
		fileInfoPanel.add(filenameLabel);
		getContentPane().add(topPanel, BorderLayout.NORTH);

		// add a GlyphCanvas 
		glyphCanvas = new GlyphCanvas(this, new Font(metrics.getFontName(), Font.PLAIN, metrics.getFontSize()), (char) glyphEdit.getValue());

		getContentPane().add(glyphCanvas, BorderLayout.CENTER);

		// put spin buttons on vertical panel
		JPanel spinPanel = new JPanel(new GridLayout(5, 1));
		spinPanel.add(glyphEdit);
		spinPanel.add(heightEdit);
		spinPanel.add(depthEdit);
		spinPanel.add(widthEdit);
		spinPanel.add(xOffsetEdit);
		getContentPane().add(spinPanel, BorderLayout.EAST);
		pack();
		setVisible(true);
	}

	/** Updates the glyph in the glyphCanvas and stores the current metrics */
	protected void updateGlyph() {
		char newGlyph = (char) glyphEdit.getValue();
		glyphCanvas.setGlyph(newGlyph);
		glyphCanvas.setMetrics(metrics.getHeight(newGlyph), metrics.getDepth(newGlyph), metrics.getWidth(newGlyph));
		updateMetricEdits();
	}

	/** Returns a string containing the CVS revision number in
	 * form (r xx.yy). */
	private static String revisionString() {
		if (revision.charAt(0) != '$' || revision.charAt(revision.length() - 1) != '$')
			return "";
		int len = revision.length();
		int first = revision.indexOf(" ") + 1;
		if (first < 0 || first >= len)
			return "";
		return "(r" + revision.substring(first, len - 2) + ")";
	}

	/** Opens a FontChooser and lets the user select a new font. This 
	 * method is called when pressing the "New"-button. */
	public void newFont() {
		FontChooser chooser = new FontChooser(this);
		chooser.setVisible(true);
		Font f = chooser.getSelectedFont();
		if (f != null) {
			glyphCanvas.setFont(f);
			metrics = new FontMetrics(f.getName(), f.getSize());
			updateFontInfo();
		}
	}

	/** Reads the font metrics from a file. The filename is 
	 * selected from a file chooser. */
	public void openFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FmxFileFilter());
		// set the default directory to the location where fmx files should be stored
		File self = new File(getClass().getResource("FmxEditor.class").getFile());
		chooser.setCurrentDirectory(self);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			try {
				File selection = chooser.getSelectedFile();
				if (!metrics.readFromFile(selection))
					error("Unknown or invalid file '" + selection.getName() + "'");
				else {
					fmxFile = selection;
					filenameLabel.setText("FMX file: " + fmxFile.getName());
					Font font = new Font(metrics.getFontName(), Font.PLAIN, metrics.getFontSize());
					glyphCanvas.setFont(font);
					updateGlyph();
					updateFontInfo();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}

	/** Saves the present font metrics to the current fmx file. 
	 * @param showChooser true, if file chooser should be shown */
	public void saveFile(boolean showChooser) {
		if (fmxFile == null || showChooser) {
			JFileChooser chooser = new JFileChooser();
			// set the default directory to the location where fmx files should be stored
			File self = new File(getClass().getResource("FmxEditor.class").getFile());
			chooser.setCurrentDirectory(self);
			chooser.setFileFilter(new FmxFileFilter());
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				fmxFile = chooser.getSelectedFile();
				metrics.writeToFile(fmxFile);
				updateFontInfo();
			}
		}
		else
			metrics.writeToFile(fmxFile);
	}

	/** Opens a ResizeDialog that lets the user enter a new font size. 
	 * Afer selecting "OK" the new size is assigned to all glyph metrics
	 * of the current fmx data, so the old metrics are scaled properly. This
	 * method is called when pressing the "Resize"-button. */
	public void resizeMetrics() {
		ResizeDialog resizeDialog = new ResizeDialog(this);
		resizeDialog.setEnteredSize(metrics.getFontSize());
		resizeDialog.setVisible(true);
		if (resizeDialog.isOk()) {
			double newSize = resizeDialog.getEnteredSize();
			double oldSize = metrics.getFontSize();
			double scaleFactor = newSize / oldSize;
			metrics.scale(scaleFactor);
			//char glyph = 
			glyphCanvas.getGlyph();
			glyphCanvas.scaleFontSize(scaleFactor);
			updateMetricEdits();
			updateFontInfo();
		}
	}

	/** Updates the font info labels. */
	private void updateFontInfo() {
		fontLabel.setText(metrics.getFontName() + ", " + metrics.getFontSize());
		filenameLabel.setText("FMX file: " + (fmxFile != null ? fmxFile.getName() : "(untitled)"));
	}

	/** Updates the spin buttons to the current metric values displayed
	 * in glyphCanvas. */
	protected void updateMetricEdits() {
		char glyph = (char) glyphEdit.getValue();
		heightEdit.setValue(metrics.getHeight(glyph));
		depthEdit.setValue(metrics.getDepth(glyph));
		widthEdit.setValue(metrics.getWidth(glyph));
	}

	/** This implements the only method of interface GlyphCanvasOwner. It is called
	 * when the owned GlyphCanvas updated the default metrics of the currently 
	 * displayed glyph. These metrics can only be obtained by a Graphics object
	 * that seems only be alive inside the related paint method. 
	 * @see GlyphCanvasOwner */
	public void glyphCanvasChangedDefaults() {
		char glyph = (char) glyphEdit.getValue();
		if (metrics.getWidth(glyph) == 0) {
			metrics.setHeight(glyph, glyphCanvas.getDefaultHeight());
			metrics.setDepth(glyph, glyphCanvas.getDefaultDepth());
			metrics.setWidth(glyph, glyphCanvas.getDefaultWidth());
			updateMetricEdits();
		}
	}

	/** Shows an error message box. 
	 * @param message error message to be shown */
	private void error(String message) {
		JOptionPane.showMessageDialog(this, message, "FMX editor", JOptionPane.OK_OPTION);
	}

	/**
	 * For testing.
	 * @param args ignored.
	 */
	public static void main(final String[] args) {
		//FmxEditor edit = 
		new FmxEditor();
	}
}