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

import java.io.File;

/**
 * Implements a file filter.
 * 
 * @date (02.06.00 10:38:07)
 * @author Tillman Weyde
 * @version 0.113
 */
public class ExtensionFilter extends javax.swing.filechooser.FileFilter implements
		java.io.FilenameFilter {

	protected String extension[];
	protected String description;
	protected boolean doShowDirs;

	/**
	 * ExtensionFilter constructor.
	 * 
	 * @param ext The extensions to be accepted by this filter.
	 */
	public ExtensionFilter(String ext) {
		this(null, false, ext);
	}

	/**
	 * ExtensionFilter constructor with description.
	 * 
	 * @param desc The description for this filter.
	 * @param ext The extensions to be accepted by this filter.
	 */
	public ExtensionFilter(String desc, String... ext) {
		this(desc, true, ext);
	}

	/**
	 * ExtensionFilter constructor with description.
	 * 
	 * @param desc The description for this filter.
	 * @param ext The extensions to be accepted by this filter.
	 */
	public ExtensionFilter(String desc, Boolean showDirs, String... ext) {
		if (ext == null)
			System.out.println("WARNING: " + this.getClass()
								+ " constructor called without extention strings.");
		extension = ext;
		description = desc;
		doShowDirs = showDirs;
	}

	/**
	 * Returns true if the extension in dir is accepted.
	 * 
	 * @param dir The directory the file is in.
	 * @param name The name of the file without the containing directory.
	 * @return True if the filename has the extension required by this filter.
	 */
	@Override
	public boolean accept(java.io.File dir, String name) {
		if (doShowDirs && new File(dir, name).isDirectory())
			return true;
		if (extension == null)
			return false;
		for (String ext : extension)
			if (name.endsWith("." + ext))
				return true;
		return false;
	}

	/**
	 * Returns the extension.
	 * 
	 * @date (02.06.00 10:39:28)
	 * @return String
	 */
	public java.lang.String[] getExtension() {
		return extension;
	}

	/**
	 * Sets the extension.
	 * 
	 * @date (02.06.00 10:39:28)
	 * @param newExtension java.lang.String
	 */
	public void setExtension(String... newExtension) {
		extension = newExtension;
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File f) {
		return this.accept(f.getParentFile(), f.getName());
	}

	/**
	 * Sets the description.
	 * 
	 * @param argDescription
	 */
	public void setDescription(final String argDescription) {
		this.description = argDescription;
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

}
