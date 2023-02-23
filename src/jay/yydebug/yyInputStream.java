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

import java.awt.TextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/** feeds an InputStream from a TextArea (only!)<br>
    as one should not read from within the event thread,
    this should not deadlock
 */

public class yyInputStream extends InputStream implements KeyListener {
  protected final StringBuffer line = new StringBuffer();
  protected Vector queue = new Vector();	// null after close()

  @Override
public synchronized int available () throws IOException {
    if (queue == null) throw new IOException("closed");
    return queue.isEmpty() ? 0 : ((byte[])queue.firstElement()).length;
  }

  @Override
public synchronized void close () throws IOException {
    if (queue == null) throw new IOException("closed");
    queue = null;
  }

  @Override
public synchronized int read () throws IOException {
    if (queue == null) throw new IOException("closed");
    while (queue.isEmpty())
      try {
        wait();
      } catch (InterruptedException ie) { }

    byte[] buf = (byte[])queue.firstElement();
    switch (buf.length) {
    case 0:
      return -1;
    case 1:
      queue.removeElementAt(0);
      break;
    default:
      byte[] nbuf = new byte[buf.length-1];
      System.arraycopy(buf, 1, nbuf, 0, nbuf.length);
      queue.setElementAt(nbuf, 0);
    }
    return buf[0] & 255;
  }

  @Override
public synchronized int read(byte[] b, int off, int len) throws IOException {
    if (queue == null) throw new IOException("closed");
    while (queue.isEmpty())
      try {
        wait();
      } catch (InterruptedException ie) { }

    byte[] buf = (byte[])queue.firstElement();
    if (buf.length == 0) return -1;

    if (buf.length <= len) {
      System.arraycopy(buf, 0, b, off, buf.length);
      queue.removeElementAt(0);
      return buf.length;
    }
    
    System.arraycopy(buf, 0, b, off, len);
    byte[] nbuf = new byte[buf.length-len];
    System.arraycopy(buf, len, nbuf, 0, nbuf.length);
    queue.setElementAt(nbuf, 0);
    return len;
  }

  @Override
public long skip (long len) {
    return 0;				// don't skip on terminal
  }

  // BUG: Rhapsody DR2 seems to not send some keys to keyTyped()
  //	e.g. German keyboard + is dropped, but numeric pad + is processed

  @Override
public void keyTyped (KeyEvent ke) {
    TextArea ta = (TextArea)ke.getComponent();
    char ch = ke.getKeyChar();

    switch (ch) {
      case '\n': case '\r':		// \n|\r -> \n, release line
	line.append('\n');
	break;

      case 'D'&31:			// ^D: release line
	ta.append("^D"); ta.setCaretPosition(ta.getText().length());
	break;

      case '\b':			// \b: erase char, if any
	int len = line.length();
	if (len > 0) line.setLength(len-1);
	return;

      case 'U'&31:			// ^U: erase line, if any
	line.setLength(0);
	ta.append("^U\n"); ta.setCaretPosition(ta.getText().length());
	return;

      default:
	line.append(ch);
	return;
    }
    synchronized(this) {
      queue.addElement(line.toString().getBytes());
      notify();
    }
    line.setLength(0);
  }

  @Override
public void keyPressed (KeyEvent ke) {
  }

  @Override
public void keyReleased (KeyEvent ke) {
  }
}
