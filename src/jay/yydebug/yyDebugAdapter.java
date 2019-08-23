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

import java.io.PrintStream;

/** A trivial implementation of yyDebug */

public class yyDebugAdapter implements yyDebug {
  protected PrintStream out;

  public yyDebugAdapter (PrintStream out) {
    this.out = out;
  }

  public yyDebugAdapter () {
    this(System.out);
  }

  public void push (int state, Object value) {
    out.println("push\tstate "+state+"\tvalue "+ value);
  }

  public void lex (int state, int token, String name, Object value) {
    out.println("lex\tstate "+state+"\treading "+name+"\tvalue "+value);
  }

  public void shift (int from, int to, int errorFlag) {
    switch (errorFlag) {
    default:				// normally
      out.println("shift\tfrom state "+from+" to "+to);
      break;
    case 0: case 1: case 2:		// in error recovery
      out.println("shift\tfrom state "+from+" to "+to
				+"\t"+errorFlag+" left to recover");
      break;
    case 3:				// normally
      out.println("shift\tfrom state "+from+" to "+to+"\ton error");
      break;
    }
  }

  public void pop (int state) {
    out.println("pop\tstate "+state+"\ton error");
  }

  public void discard (int state, int token, String name, Object value) {
    out.println("discard\tstate "+state+"\ttoken "+name+"\tvalue "+value);
  }

  public void reduce (int from, int to, int rule, String text, int len) {
    out.println("reduce\tstate "+from+"\tuncover "+to
						+"\trule ("+rule+") "+text);
  }

  public void shift (int from, int to) {
    out.println("goto\tfrom state "+from+" to "+to);
  }

  public void accept (Object value) {
    out.println("accept\tvalue "+value);
  }

  public void error (String message) {
    out.println("error\t"+message);
  }

  public void reject () {
    out.println("reject");
  }
}
