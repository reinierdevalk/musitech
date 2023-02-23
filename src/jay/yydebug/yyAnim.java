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

import java.awt.*;
import java.awt.event.*;

/** An implementation of yyDebug with a graphical interface.
    This has not been checked to be serializable.
  */

public class yyAnim extends Frame implements yyDebug {

  protected transient static int nFrames;	// controls exit
  { ++ nFrames;					// new instance
  }

  /** trap System.in */		public static final int IN = 1;
  /** trap System.out, .err */  public static final int OUT = 2;

  protected transient yyAnimPanel panel;	// input, stack, comment
  protected transient Thread eventThread;	// set by Checkbox-listener
  protected transient boolean outputBreak	// breakpoint, only! set in GUI
				 = false;

				/** creates and displays the frame
				    @param io trap System.in and/or .out,.err
				 */
  public yyAnim (String title, int io) {
    setTitle(title);

    Font font = new Font("Monospaced", Font.PLAIN, 12);

    MenuBar mb = new MenuBar();
      Menu m = new Menu("yyAnim");
        MenuItem mi = new MenuItem("Quit");
          mi.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed (ActionEvent ae) {
	      System.exit(0);
            }
          });
        m.add(mi);
      mb.add(m);
    setMenuBar(mb);

    add(panel = new yyAnimPanel(font), "Center");

    if ((io & (IN|OUT)) != 0) {
      Panel p = new Panel(new BorderLayout());
	switch (io) {
	case IN:
          p.add(new Label("terminal input"), "North");
	  break;
	case OUT: case IN|OUT:
	  Checkbox c;
	  String ct = (io&IN) != 0 ? "terminal i/o" : "terminal output";
          p.add(c = new Checkbox(ct, outputBreak), "North");
            c.addItemListener(new ItemListener() {
	      @Override
		public void itemStateChanged (ItemEvent ie) {
	        eventThread = Thread.currentThread();
	        outputBreak = ie.getStateChange() == ItemEvent.SELECTED;
              }
            });
	}

	final TextArea t;
        p.add(t = new TextArea(10, 50), "Center");
          t.setBackground(Color.white); t.setFont(font);

	  if ((io&IN) != 0) {
            yyInputStream in = new yyInputStream();
            t.addKeyListener(in); t.setEditable(true);
            System.setIn(in);
	  }

	  if ((io&OUT) != 0) {
            System.setOut(new yyPrintStream() {	// PrintStream into TextArea
	      @Override
		public void close () { }
	      @Override
		public void write (byte b[], int off, int len) {
	        String s = new String(b, off, len);
	        t.append(s); t.setCaretPosition(t.getText().length());
	        if (outputBreak && s.indexOf("\n") >= 0 && eventThread != null
	            && Thread.currentThread() != eventThread)
	          try {
	            synchronized (panel) { panel.wait(); }
	          } catch (InterruptedException ie) { }
	      }
	      @Override
		public void write (int b) {
	        write(new byte[] { (byte)b }, 0, 1);
	      }
            });
            System.setErr(System.out);
	  }
      add(p, "South");
    }

    addWindowListener(new WindowAdapter() {
      @Override
	public void windowClosing (WindowEvent we) {
	dispose();
        if (-- nFrames <= 0) System.exit(0);
      }
    });
    pack();
    setStaggeredLocation(this);
    show();
  }

  public static void setStaggeredLocation (Component c) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension d = c.getPreferredSize();

    int x = (screen.width - d.width)/2 + (nFrames-1)*32;
    if (x < 32) x = 32;
    else if (x > screen.width-128) x = screen.width-128;

    int y = (screen.height - d.height)/2 + (nFrames-1)*32;
    if (y < 32) y = 32;
    else if (y > screen.height-128) y = screen.height-128;

    c.setLocation(x, y);
  }

  @Override
public synchronized void lex (int state, int token, String name, Object value)
  { panel.lex(state, token, name, value);
  }

  @Override
public void shift (int from, int to, int errorFlag) {
    panel.shift(from, to, errorFlag);
  }

  @Override
public void discard (int state, int token, String name, Object value) {
    panel.discard(state, token, name, value);
  }

  @Override
public void shift (int from, int to) {
    panel.shift(from, to);
  }

  @Override
public synchronized void accept (Object value) {
    panel.accept(value);
  }

  @Override
public void error (String message) {
    panel.error(message);
  }

  @Override
public void reject () {
    panel.reject();
  }

  @Override
public synchronized void push (int state, Object value) {
    panel.push(state, value);
  }

  @Override
public synchronized void pop (int state) {
    panel.pop(state);
  }

  @Override
public synchronized void reduce (int from, int to, int rule, String text,
								int len) {
    panel.reduce(from, to, rule, text, len);
  }
}
