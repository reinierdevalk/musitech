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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * FolderZiper provide a static method to zip a folder.
 * This class is taken from 
 * http://www.theserverside.com/discussions/thread.tss?thread_id=34906
 * where it has been posted by Mohit Gupta.
 *
 * @author pitchoun
 */
public class FolderZiper {

	
	/**
	 * Command line interface. 
	 * 
	 * @param a
	 * @throws Exception
	 */
	public static void main(final String[] a) throws Exception {
		if(a[0] == null)
			System.out.println("USAGE: FolderZiper folder \n will zip 'folder' to 'folder.zip'");
		else
			zipFolder(a[0], a[0] + ".zip");
	}

	/**
	* Zip the srcFolder into the destFileZipFile. All the folder subtree of the src folder is added to the destZipFile
	* archive.
	*
	* TODO handle the use case of srcFolder being a file.
	*
	* @param srcFolder String, the path of the srcFolder
	* @param destZipFile String, the path of the destination zipFile. This file will be created or erased.
	*/	static public void zipFolder(String srcFolder, String destZipFile)
			throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	/**
	 * Write the content of srcFile in a new ZipEntry, named path+srcFile, of
	 * the zip stream. The result is that the srcFile will be in the path folder
	 * in the generated archive.
	 * 
	 * @param path String, the relative path with the root archive.
	 * @param srcFile String, the absolute path of the file to add
	 * @param zip ZipOutputStram, the stream to use to write the given file.
	 */
	static private void addFileToZip(String path, String srcFile,
										ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	/**
	 * add the srcFolder to the zip stream.
	 * 
	 * @param path String, the relative path with the root archive.
	 * @param srcFile String, the absolute path of the file to add
	 * @param zip ZipOutputStram, the stream to use to write the given file.
	 */
	static private void addFolderToZip(String path, String srcFolder,
										ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/"
															+ fileName, zip);
			}
		}
	}
}
