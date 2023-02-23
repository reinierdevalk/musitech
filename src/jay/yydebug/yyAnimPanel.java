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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/** An implementation of yyDebug with a graphical interface.
    This has not been checked to be serializable.
  */
public class yyAnimPanel extends Panel implements yyDebug {

  protected transient TextField token;
  protected transient TextField value;
  protected transient TextArea comments;	// running explanations
  protected transient Stack stack;		// state/value stack

  protected transient boolean tokenBreak= true ;
  // breakpoints, set in GUI
 protected transient boolean stackBreak;
  protected transient boolean commentsBreak;

  public yyAnimPanel (Font font) {
    super(new BorderLayout());

    Button b;
    Checkbox c;
    Panel p, q;

    p = new Panel(new BorderLayout());
      p.add(c = new Checkbox("token ", tokenBreak), "West");
        c.addItemListener(new ItemListener() {
	  @Override
	public void itemStateChanged (ItemEvent ie) {
	    tokenBreak = ie.getStateChange() == ItemEvent.SELECTED;
          }
        });
      q = new Panel(new BorderLayout());
        q.add(token = new TextField(12), "West");
          token.setEditable(false); token.setBackground(Color.white);
          token.setFont(font);
        q.add(value = new TextField(24), "Center");
          value.setEditable(false); value.setBackground(Color.white);
          value.setFont(font);
      p.add(q, "Center");
      p.add(b = new Button(" continue "), "East");
        b.addActionListener(new ActionListener() {
	  @Override
	public void actionPerformed (ActionEvent ae) {
	    synchronized (yyAnimPanel.this) {
	      yyAnimPanel.this.notify();
	    }
          }
        });
    add(p, "North");

    p = new Panel(new BorderLayout());
      q = new Panel(new BorderLayout());
        q.add(c = new Checkbox("stack", stackBreak), "North");
          c.addItemListener(new ItemListener() {
	    @Override
		public void itemStateChanged (ItemEvent ie) {
	      stackBreak = ie.getStateChange() == ItemEvent.SELECTED;
            }
          });
        q.add(stack = new Stack(font), "Center");
      p.add(q, "Center");
      q = new Panel(new BorderLayout());
        q.add(c = new Checkbox("comments", commentsBreak), "North");
          c.addItemListener(new ItemListener() {
	    @Override
		public void itemStateChanged (ItemEvent ie) {
	      commentsBreak = ie.getStateChange() == ItemEvent.SELECTED;
            }
          });
        q.add(comments = new TextArea(10, 40), "Center");
          comments.setEditable(false); comments.setBackground(Color.white);
          comments.setFont(font);
      p.add(q, "East");
    add(p, "Center");
  }

  protected final static class Stack extends ScrollPane {
    protected transient Font font;
    protected transient Panel panel;

    protected static final GridBagConstraints level = new GridBagConstraints();
    static {
      level.anchor = GridBagConstraints.NORTH;
      level.fill = GridBagConstraints.HORIZONTAL;
      level.gridheight = 1; level.gridwidth = GridBagConstraints.REMAINDER;
      level.gridx = 0; level.gridy = GridBagConstraints.RELATIVE;
      level.weightx = 1.0;
    }

    public Stack (Font font) {
      super(SCROLLBARS_AS_NEEDED);
      this.font = font;
      setSize(50, 100);
      add(panel = new Panel(new GridBagLayout()));
    }

    public void push (int state, Object value) {
      Panel q = new Panel(new BorderLayout());
      TextField t;
      q.add(t = new TextField(""+state, 5), "West");
	t.setEditable(false); t.setBackground(Color.white); t.setFont(font);
      q.add(t = new TextField(value != null ? value.toString() : ""), "Center");
	t.setEditable(false); t.setBackground(Color.white); t.setFont(font);
      panel.add(q, level, 0); validate();
    }

    public void pop (int len) {
      for (int n = 0; n < len; ++ n) {
	panel.remove(0);
        validate();	// Rhapsody DR2 java crashes if this is outside loop
      }
    }

    public void pop () {
      panel.removeAll(); validate();
    }
  }

  protected synchronized void explain (String what) {
    if (comments.getText().length() > 0) comments.append("\n");
    comments.append(what);
    if (commentsBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  @Override
public synchronized void lex (int state, int token, String name, Object value)
  { this.token.setText(name);
    this.value.setText(value == null ? "" : value.toString());
    explain("read "+name);
    if (tokenBreak && !commentsBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  @Override
public void shift (int from, int to, int errorFlag) {
    switch (errorFlag) {
    default:				// normally
      explain("shift to "+to);
      break;
    case 0: case 1: case 2:		// in error recovery
      explain("shift to "+to+", "+errorFlag+" left to recover");
      break;
    case 3:				// normally
      explain("shift to "+to+" on error");
    }
  }

  @Override
public void discard (int state, int token, String name, Object value) {
    explain("discard token "+name+", value "+value);
  }

  @Override
public void shift (int from, int to) {
    explain("go to "+to);
  }

  @Override
public synchronized void accept (Object value) {
    explain("accept, value "+value);
    stack.pop();
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  @Override
public void error (String message) {
    explain("error message");
  }

  @Override
public void reject () {
    explain("reject");
    stack.pop();
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  @Override
public synchronized void push (int state, Object value) {
    stack.push(state, value);
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  @Override
public synchronized void pop (int state) {
    explain("pop "+state+" on error");
    stack.pop(1);
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  @Override
public synchronized void reduce (int from, int to, int rule, String text,
								int len) {
    explain("reduce ("+rule+"), uncover "+to+"\n("+rule+") "+ text);
    stack.pop(len);
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }
}
