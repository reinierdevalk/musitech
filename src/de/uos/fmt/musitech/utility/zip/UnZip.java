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
package de.uos.fmt.musitech.utility.zip;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * UnZip -- print or unzip a JAR or PKZIP file using java.util.zip. Command-line
 * version: extracts files.
 * 
 * @author Ian Darwin, Ian@DarwinSys.com $Id: UnZip.java,v 1.7 2004/03/07
 *         17:40:35 ian Exp $
 */
public class UnZip {

	/** Constants for mode listing or mode extracting. */
	public static final int LIST = 0, EXTRACT = 1;
	/** Whether we are extracting or just printing TOC */
	protected int mode = LIST;

	/** The ZipFile that is used to read an archive */
	protected ZipFile zippy;

	/** The buffer for reading/writing the ZipFile data */
	protected byte[] b;
	
	protected File destDir;

	/**
	 * Simple main program, construct an UnZipper, process each .ZIP file from
	 * argv[] through that object.
	 */
	public static void main(String[] argv) {
		UnZip u = new UnZip();

		for (int i = 0; i < argv.length; i++) {
			if ("-x".equals(argv[i])) {
				u.setMode(EXTRACT);
				continue;
			}
			String candidate = argv[i];
			// System.err.println("Trying path " + candidate);
			if (candidate.endsWith(".zip") || candidate.endsWith(".jar"))
				u.unZip(candidate);
			else
				System.err.println("Not a zip file? " + candidate);
		}
		System.err.println("All done!");
	}

	/** Construct an UnZip object. Just allocate the buffer */
	public UnZip() {
		b = new byte[8092];
	}

	/**
	 * Construct an UnZip object. Just allocate the buffer and set the mode of
	 * operation.
	 * 
	 * @param argEx true if the mode should be set to extraction.
	 * @throws Exception 
	 */
	public UnZip(boolean argEx, File argDestDir) throws Exception {
		b = new byte[8092];
		mode = argEx?EXTRACT:LIST;
		if(!argDestDir.isDirectory())
			throw(new Exception("destDir must be a directory"));
		destDir = argDestDir;
	}

	/** Set the Mode (list, extract). */
	protected void setMode(int m) {
		if (m == LIST || m == EXTRACT)
			mode = m;
	}

	/** Cache of paths we've mkdir()ed. */
	protected SortedSet dirsMade;

	/** For a given Zip file, process each entry. */
	public void unZip(String fileName) {
		dirsMade = new TreeSet();
		try {
			zippy = new ZipFile(fileName);
			Enumeration all = zippy.entries();
			while (all.hasMoreElements()) {
				getFile((ZipEntry) all.nextElement());
			}
		} catch (IOException err) {
			System.err.println("IO Error: " + err);
			return;
		}
	}

	protected boolean warnedMkDir = false;

	/**
	 * Process one file from the zip, given its name. Either print the name, or
	 * create the file on disk.
	 */
	protected void getFile(ZipEntry e) throws IOException {
		String zipName = e.getName();
		switch (mode) {
		case EXTRACT:
			if (zipName.startsWith("/")) {
				if (!warnedMkDir)
					System.out.println("Ignoring absolute paths");
				warnedMkDir = true;
				zipName = zipName.substring(1);
			}
			// if a directory, just return. We mkdir for every file,
			// since some widely-used Zip creators don't put out
			// any directory entries, or put them in the wrong place.
			if (zipName.endsWith("/")) {
				return;
			}
			// Else must be a file; open the file for output
			// Get the directory part.
			int ix = zipName.lastIndexOf('/');
			if (ix > 0) {
				String dirName = zipName.substring(0, ix);
				if (!dirsMade.contains(dirName)) {
					File d = new File(destDir,dirName);
					// If it already exists as a dir, don't do anything
					if (!(d.exists() && d.isDirectory())) {
						// Try to create the directory, warn if it fails
						System.out.println("Creating Directory: " + dirName);
						if (!d.mkdirs()) {
							System.err.println("Warning: unable to mkdir "
												+ dirName);
						}
						dirsMade.add(dirName);
					}
				}
			}
			System.err.println("Creating " + zipName);
			FileOutputStream os = new FileOutputStream(zipName);
			InputStream is = zippy.getInputStream(e);
			int n = 0;
			while ((n = is.read(b)) > 0)
				os.write(b, 0, n);
			is.close();
			os.close();
			break;
		case LIST:
			// Not extracting, just list
			if (e.isDirectory()) {
				System.out.println("Directory " + zipName);
			} else {
				System.out.println("File " + zipName);
			}
			break;
		default:
			throw new IllegalStateException("mode value (" + mode + ") bad");
		}
	}
}
