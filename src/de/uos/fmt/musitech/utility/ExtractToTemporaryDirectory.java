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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtractToTemporaryDirectory {
	// Programmierbeginn: 10:45

	static boolean quiet = true;

	public static File extractToTempDirectory(String zipFile) {
		File f = null;
		try {
			// get directory name of temporary directory to extract the zip file to
			f = new File(zipFile);
			String path = f.getAbsolutePath();
			if ((path.lastIndexOf('.') > path.lastIndexOf('\\')) && (path.lastIndexOf('.') > path.lastIndexOf('/')))
				path = path.substring(0, path.lastIndexOf('.'));
			path = path + "-1";
			f = new File(path);
			int i = 2;
			while (f.exists()) {
				path = path.substring(0, path.lastIndexOf('-') + 1) + i++;
				f = new File(path);
			}
			// open ZIP file
			ZipFile archive;
			archive = new ZipFile(zipFile);
			// create temporary directory
			if (!f.mkdir())
				throw new IOException("Could not create temporary directory " + f);
			if (!quiet)
				System.out.println("Extract to: " + path);
			// extract all files
			Enumeration enumer = archive.entries();
			while (enumer.hasMoreElements()) {
				ZipEntry elem = (ZipEntry)(enumer.nextElement());
				File g = new File(path + "/" + elem.getName());
				// create needed sub-directories
				String s1 = elem.getName();
				s1 = s1.replace('\\', '/');
				String s2 = path;
				if (s1.indexOf('/') != -1) {
					s2 = s2 + '/' + s1.substring(0, s1.lastIndexOf('/'));
					s1 = s1.substring(s1.lastIndexOf('/') + 1, s1.length());
					File h = new File(s2);
					if (!h.mkdirs())
						throw new IOException("Could not create temporary directory " + h);
					if (!quiet)
						System.out.println ("Created Subdirectory " + h);
				}
				// output files
				if (!quiet)
					System.out.print (elem.getName() + " -> ");
				if (!quiet)
					System.out.println(g);
				InputStream input = archive.getInputStream(elem);
				BufferedInputStream bufin = new BufferedInputStream(input);
				OutputStream output = new FileOutputStream(g);
				BufferedOutputStream bufout = new BufferedOutputStream(output);
				int j;
				while ((j = bufin.read()) != -1) {
					bufout.write(j);
				}
				bufout.close();
				bufin.close();
				// please note that this method does NOT set date/time properties or attributes
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	return f;
	}

	public static void main(String[] args) {

		if (args.length != 1)
			throw new IllegalArgumentException("Usage: java ExtractToTemporaryDirectory archivename");
		quiet = false;
		System.out.println(extractToTempDirectory(args[0]));

	}
}