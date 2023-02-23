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
package jay.yydebug;

import java.io.FilterOutputStream;
import java.io.PrintStream;

/** reroutes and auto-flushes a PrintStream and avoids blocking */

public abstract class yyPrintStream extends PrintStream {
  protected static final String nl = System.getProperty("line.separator", "\n");

  public yyPrintStream () {
    super(new FilterOutputStream(null), 	// null results in NullPointerException...
          true);
  }

// inherited	public boolean checkError ()
  @Override
public abstract void close ();
// inherited	public void flush() 
  @Override
public void print (boolean b) { print(""+b); }
  @Override
public void print (char c) { print(""+c); }
  @Override
public void print (char[] s) { print(s != null ? ""+s.toString() : ""+null); }
  @Override
public void print (double d) { print(""+d); }
  @Override
public void print (float f) { print(""+f); }
  @Override
public void print (int i) { print(""+i); }
  @Override
public void print (long l) { print(""+l); }
  @Override
public void print (Object obj) { print(""+obj); }
  @Override
public void print (String s) {
    byte[] buf = (s != null ? s : ""+null).getBytes();
    if (buf.length > 0) write(buf, 0, buf.length);
  }
  @Override
public void println () { print(nl); }
  @Override
public void println (boolean b) { print(""+b+nl); }
  @Override
public void println (char c) { print(""+c+nl); }
  @Override
public void println (char[] s) { print(s != null ? s.toString()+nl : null+nl); }
  @Override
public void println (double d) { print(""+d+nl); }
  @Override
public void println (float f) { print(""+f+nl); }
  @Override
public void println (int i) { print(""+i+nl); }
  @Override
public void println (long l) { print(""+l+nl); }
  @Override
public void println (Object obj) { print(""+obj+nl); }
  @Override
public void println (String s) { print(s != null ? s+nl : null+nl); }
// inherited	public void setError ()
  @Override
public abstract void write (byte[] buf, int off, int len);
  @Override
public abstract void write (int b);
}
