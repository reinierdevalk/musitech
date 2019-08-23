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
// created by jay 0.8 (c) 1998 Axel.Schreiner@informatik.uni-osnabrueck.de

					// line 5 "EpecParser.y"
package de.uos.fmt.musitech.score.epec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Stack;
import java.util.Vector;

import de.uos.fmt.musitech.data.score.BeamContainer;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.SlurContainer;
import de.uos.fmt.musitech.data.score.Barline;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.utility.math.Rational;

class EpecParserContext
{
   Integer    octave   = new Integer(0);
   Rational   duration = new Rational(1,4);
   Rational   attack   = Rational.ZERO;
   NotationStaff      staff;
   NotationVoice currentVoice;
   NotationChord chord;
   NotationChord currentChord;
   NotationChord prevChord;
   MetricalTimeLine meterTrack;
   Note      pitch;
   int        voice = 0;
   SlurContainer slur = null;
   BeamContainer beam = null;
   byte     accent = -1;
   Vector     pitches;
   Vector linebreaks = new Vector();
}

/** This class is used to parse the text representation of 
    the music notation. A notation system (a set of staves) is described by a string
	 consisting of a sequence of the following elements (described by regular expressions, parenthesis 
	 are used as grouping meta symbols, symbols or symbol sequences in 'single ticks' denote plain characters,
	 the single tick itself is written as \'):<br>
	 <b>clef: </b> C {c | f | g | p} (:-?[0-9])?<br> 
	 The lower case letters select the clef type (p means 'percussion').	 
	 The vertical position is denoted by the succeeding line number (0 the middle line).<p>
	 
	 <b>key signature: </b> K ([0-7] ('-' | '+')) | ([a-h][-+]? ('major' | 'minor')<br>
	 Example: <tt>K 3+</tt> = 3 sharps (a major, f sharp minor)<br>
    <tt>K e- major</tt> = 3 flats<p>
	 
	 <b>time signature: </b> T ([0-9]+ / [0-9]+) | ('(' [0-9]+ / [0-9]+ ')')<br>
	 The optional parenthesis denote an abbreviated time signature if possible. Examples: T 4/4 = regular 4/4 time signature, 
	 <tt>T (4/4)</tt> = abbreviated 4/4 time signature (C, alla semibreve).<p>
	  
	 <b>base pitch: </b> [a-h]<br> 
	 h is a synonym for b, the 7th note of the c major scale.<p>

	 <b>accidental: </b> ('--' | '-' | '+' | '++')<br> 
	 An accidental must immediately succeed after a base pitch. Examples: <tt>c+</tt> = c sharp, 
	 <tt>f-</tt> = f flat, <tt>d++</tt> = d double sharp<p>

	 <b>octave: </b> (,+ | \'+)<br>
	 The middle octave is selected by a single tick: '. All following pitches remain in it until a different one
	 is choosen. Each additional tick moves the pitches one octave higher. Commas select lower pitches. A single comma
	 denotes the small octave (octave below middle c), all further commas move the pitches one octave down.<p>

	 <b>duration: </b> (1 | 2 | 4 | 8 | 16 | 32 | 64)<p>

	 <b>tuplet: </b> &lt;[0-9]+:[a-h]+&gt;<br>
	 This creates an n-tuplet. The Number before the colon declares the duration of the whole tuplet. After the colon
	 the pitches of the n-tuplet are given. The duration is 1 divided by the given number. Example for the (standard) 1/8 triole:<br>
	 8&lt;4:ccc&gt;<p>

	 <b>barline: </b> ('|' | '||' | '|||')<br>
	 | denotes a single barline, || a double barline and ||| the ending barline. Barline for reapeats are not yet implemented
	 but will be coded as '||:' and ':||'. <p>
	 
	 <b>comments: </b> (//.*) | ('/<>*'.*'*<>/')<br>
	 Example: <tt>'cdefgah''c  // c major scale</tt><p>
	 
	 <b>voices: </b> Different voices sharing a staff are seperated by a semicolon. 
	 Example: <tt>''cdefg ; 'gfedc</tt><p>

	 <b>staves: </b> Different staves are seperated by 2 semicolons. Example: 
	 <pre>
	 // first staff
	 ''cdefg ; 'cdefg ;;
	 // second staff
	 ''gfedc ; 'gfedc
	 </pre>
	 
    @author  Martin Gieseking
    @version $Revision: 8574 $, $Date: 2013-10-31 13:56:25 +0100 (Thu, 31 Oct 2013) $ */
public class EpecParser
{
   Stack contextStack   = new Stack();
   EpecParserContext context = new EpecParserContext();
   NotationSystem system        = new NotationSystem();    // script describes this system
   int numStaves = 1;
   EpecLexer lexer;
   
  
   /** Runs the parser interactively by asking the user to type in the code manually. */
   public void run (NotationSystem system) throws IOException, yyException
   {
      lexer = new EpecLexer(new InputStreamReader(System.in));
      execute(system);
   }
   

   /** Parses the given string that contains the input code. */
   public void run (NotationSystem system, String string) throws IOException, yyException
   {
      lexer = new EpecLexer(new StringReader(string));
      execute(system);
   }

   
   public void run (NotationSystem system, InputStream istream) throws IOException, yyException
   {
      lexer = new EpecLexer(istream);
      execute(system);
   }  
      

   // helper function; calls yyparse and handles exceptions
   void execute (NotationSystem system) throws IOException, yyException
   {

		this.system = system;
		context.staff = new NotationStaff(system);
		context.currentVoice = new NotationVoice(context.staff);
	
		if (system.getContext().getPiece().getMetricalTimeLine() == null) {
			system.getContext().getPiece().setMetricalTimeLine(new MetricalTimeLine(system.getContext()));
		}
		context.meterTrack = system.getContext().getPiece().getMetricalTimeLine();
		
		//jay.yydebug.yyDebug debug = new jay.yydebug.yyDebugAdapter();
		yyparse(lexer);

		//we must add the linebreaks afterwards, so the system can check if they are valid
		for (int i = 0; i < context.linebreaks.size(); i++) {
		  system.addLinebreak((Rational)context.linebreaks.get(i));
		}

		/*
		Page page = new Page();
		system.setParent(page);
		page.add(system);
		page.setParent(score);
		score.add(page);
		*/
   }
   
   /** Returns the current staff. If it doesn't exist, a new staff is created. */
   NotationStaff getCurrentStaff ()
   {
      if (context.staff == null)
      {
         context.staff = new NotationStaff(system);
      }
      return context.staff;
   }
   
   NotationVoice getCurrentVoice () {
		if (context.currentVoice == null) {
			context.currentVoice = new NotationVoice(context.staff);
		}
		return context.currentVoice;
   }
   

   NotationStaff nextStaff ()
   {
      context.staff = new NotationStaff(system);
      context.attack.assign(0);
      context.chord = null;
      context.currentChord = context.prevChord = null;
      context.pitch = null;

      nextVoice(); //new staff implies new voice

      return context.staff;
   }
   
   NotationVoice nextVoice() {
		context.currentVoice = new NotationVoice(context.staff);
		context.attack.assign(0);
		context.chord = null;
		context.currentChord = context.prevChord = null;
		context.pitch = null;

		return context.currentVoice;
   }

					// line 191 "-"
// %token constants

  public static final int BASEPITCH = 257;
  public static final int BARLINE = 258;
  public static final int INTEGER = 259;
  public static final int AP_SEQ = 260;
  public static final int COLON_SEQ = 261;
  public static final int DOT_SEQ = 262;
  public static final int PRINT = 263;
  public static final int MODE = 264;
  public static final int VOICESEP = 265;
  public static final int STAFFSEP = 266;
  public static final int CLEF = 267;
  public static final int KEYSIGNATURE = 268;
  public static final int TIMESIGNATURE = 269;
  public static final int LINEBREAK = 270;
  public static final int NOACCENT = 271;
  public static final int STACCATO = 272;
  public static final int MARCATO = 273;
  public static final int PORTATO = 274;
  public static final int yyErrorCode = 256;

  /** thrown for irrecoverable syntax errors and stack overflow.
    */
  public static class yyException extends java.lang.Exception {
    public yyException (String message) {
      super(message);
    }
  }

  /** must be implemented by a scanner object to supply input to the parser.
    */
  public interface yyInput {
    /** move on to next token.
        @return false if positioned beyond tokens.
        @throws IOException on input error.
      */
    boolean advance () throws java.io.IOException;
    /** classifies current token.
        Should not be called if advance() returned false.
        @return current %token or single character.
      */
    int token ();
    /** associated with current token.
        Should not be called if advance() returned false.
        @return value for token().
      */
    Object value ();
  }

  /** simplified error message.
      @see <a href="#yyerror(java.lang.String, java.lang.String[])">yyerror</a>
    */
  public void yyerror (String message) {
    yyerror(message, null);
  }

  /** (syntax) error message.
      Can be overwritten to control message format.
      @param message text to be displayed.
      @param expected vector of acceptable tokens, if available.
    */
  public void yyerror (String message, String[] expected) {
    if (expected != null && expected.length > 0) {
      System.err.print(message+", expecting");
      for (int n = 0; n < expected.length; ++ n)
        System.err.print(" "+expected[n]);
      System.err.println();
    } else
      System.err.println(message);
  }

  /** debugging support, requires the package jay.yydebug.
      Set to null to suppress debugging messages.
    */
  protected jay.yydebug.yyDebug yydebug;

  protected static final int yyFinal = 23;

  /** index-checked interface to yyName[].
      @param token single character or %token value.
      @return token name or [illegal] or [unknown].
    */
  public static final String yyname (int token) {
    if (token < 0 || token > YyNameClass.yyName.length) return "[illegal]";
    String name;
    if ((name = YyNameClass.yyName[token]) != null) return name;
    return "[unknown]";
  }

  /** computes list of expected tokens on error by tracing the tables.
      @param state for which to compute the list.
      @return list of token names.
    */
  protected String[] yyExpecting (int state) {
    int token, n, len = 0;
    boolean[] ok = new boolean[YyNameClass.yyName.length];

    if ((n = YySindexClass.yySindex[state]) != 0)
      for (token = n < 0 ? -n : 0;
           token < YyNameClass.yyName.length && n+token < YyTableClass.yyTable.length; ++ token)
        if (YyCheckClass.yyCheck[n+token] == token && !ok[token] && YyNameClass.yyName[token] != null) {
          ++ len;
          ok[token] = true;
        }
    if ((n = YyRindexClass.yyRindex[state]) != 0)
      for (token = n < 0 ? -n : 0;
           token < YyNameClass.yyName.length && n+token < YyTableClass.yyTable.length; ++ token)
        if (YyCheckClass.yyCheck[n+token] == token && !ok[token] && YyNameClass.yyName[token] != null) {
          ++ len;
          ok[token] = true;
        }

    String result[] = new String[len];
    for (n = token = 0; n < len;  ++ token)
      if (ok[token]) result[n++] = YyNameClass.yyName[token];
    return result;
  }

  /** the generated parser, with debugging messages.
      Maintains a state and a value stack, currently with fixed maximum size.
      @param yyLex scanner.
      @param yydebug debug message writer implementing yyDebug, or null.
      @return result of the last reduction, if any.
      @throws yyException on irrecoverable parse error.
    */
  public Object yyparse (yyInput yyLex, Object yydebug)
				throws java.io.IOException, yyException {
    this.yydebug = (jay.yydebug.yyDebug)yydebug;
    return yyparse(yyLex);
  }

  /** initial size and increment of the state/value stack [default 256].
      This is not final so that it can be overwritten outside of invocations
      of yyparse().
    */
  protected int yyMax;

  /** executed at the beginning of a reduce action.
      Used as $$ = yyDefault($1), prior to the user-specified action, if any.
      Can be overwritten to provide deep copy, etc.
      @param first value for $1, or null.
      @return first.
    */
  protected Object yyDefault (Object first) {
    return first;
  }

  /** the generated parser.
      Maintains a state and a value stack, currently with fixed maximum size.
      @param yyLex scanner.
      @return result of the last reduction, if any.
      @throws yyException on irrecoverable parse error.
    */
  public Object yyparse (yyInput yyLex)
				throws java.io.IOException, yyException {
    if (yyMax <= 0) yyMax = 256;			// initial size
    int yyState = 0, yyStates[] = new int[yyMax];	// state stack
    Object yyVal = null, yyVals[] = new Object[yyMax];	// value stack
    int yyToken = -1;					// current input
    int yyErrorFlag = 0;				// #tks to shift

    yyLoop: for (int yyTop = 0;; ++ yyTop) {
      if (yyTop >= yyStates.length) {			// dynamically increase
        int[] i = new int[yyStates.length+yyMax];
        System.arraycopy(yyStates, 0, i, 0, yyStates.length);
        yyStates = i;
        Object[] o = new Object[yyVals.length+yyMax];
        System.arraycopy(yyVals, 0, o, 0, yyVals.length);
        yyVals = o;
      }
      yyStates[yyTop] = yyState;
      yyVals[yyTop] = yyVal;
      if (yydebug != null) yydebug.push(yyState, yyVal);

      yyDiscarded: for (;;) {	// discarding a token does not change stack
        int yyN;
        if ((yyN = YyDefRedClass.yyDefRed[yyState]) == 0) {	// else [default] reduce (yyN)
          if (yyToken < 0) {
            yyToken = yyLex.advance() ? yyLex.token() : 0;
            if (yydebug != null)
              yydebug.lex(yyState, yyToken, yyname(yyToken), yyLex.value());
          }
          if ((yyN = YySindexClass.yySindex[yyState]) != 0 && (yyN += yyToken) >= 0
              && yyN < YyTableClass.yyTable.length && YyCheckClass.yyCheck[yyN] == yyToken) {
            if (yydebug != null)
              yydebug.shift(yyState, YyTableClass.yyTable[yyN], yyErrorFlag-1);
            yyState = YyTableClass.yyTable[yyN];		// shift to yyN
            yyVal = yyLex.value();
            yyToken = -1;
            if (yyErrorFlag > 0) -- yyErrorFlag;
            continue yyLoop;
          }
          if ((yyN = YyRindexClass.yyRindex[yyState]) != 0 && (yyN += yyToken) >= 0
              && yyN < YyTableClass.yyTable.length && YyCheckClass.yyCheck[yyN] == yyToken)
            yyN = YyTableClass.yyTable[yyN];			// reduce (yyN)
          else
            switch (yyErrorFlag) {
  
            case 0:
              yyerror("syntax error", yyExpecting(yyState));
              if (yydebug != null) yydebug.error("syntax error");
  
            case 1: case 2:
              yyErrorFlag = 3;
              do {
                if ((yyN = YySindexClass.yySindex[yyStates[yyTop]]) != 0
                    && (yyN += yyErrorCode) >= 0 && yyN < YyTableClass.yyTable.length
                    && YyCheckClass.yyCheck[yyN] == yyErrorCode) {
                  if (yydebug != null)
                    yydebug.shift(yyStates[yyTop], YyTableClass.yyTable[yyN], 3);
                  yyState = YyTableClass.yyTable[yyN];
                  yyVal = yyLex.value();
                  continue yyLoop;
                }
                if (yydebug != null) yydebug.pop(yyStates[yyTop]);
              } while (-- yyTop >= 0);
              if (yydebug != null) yydebug.reject();
              throw new yyException("irrecoverable syntax error");
  
            case 3:
              if (yyToken == 0) {
                if (yydebug != null) yydebug.reject();
                throw new yyException("irrecoverable syntax error at end-of-file");
              }
              if (yydebug != null)
                yydebug.discard(yyState, yyToken, yyname(yyToken),
  							yyLex.value());
              yyToken = -1;
              continue yyDiscarded;		// leave stack alone
            }
        }
        int yyV = yyTop + 1-YyLenClass.yyLen[yyN];
        if (yydebug != null)
          yydebug.reduce(yyState, yyStates[yyV-1], yyN, YyRuleClass.yyRule[yyN], YyLenClass.yyLen[yyN]);
        yyVal = yyDefault(yyV > yyTop ? null : yyVals[yyV]);
        switch (yyN) {
case 1:
					// line 221 "EpecParser.y"
  {system.add(((NotationStaff)yyVals[0+yyTop])); }
  break;
case 2:
					// line 222 "EpecParser.y"
  {nextStaff();}
  break;
case 3:
					// line 223 "EpecParser.y"
  {system.add(((NotationStaff)yyVals[0+yyTop])); }
  break;
case 4:
					// line 224 "EpecParser.y"
  {}
  break;
case 5:
					// line 228 "EpecParser.y"
  {yyVal = getCurrentStaff(); ((NotationStaff)yyVal).add(((NotationVoice)yyVals[0+yyTop]));}
  break;
case 6:
					// line 229 "EpecParser.y"
  {nextVoice();}
  break;
case 7:
					// line 230 "EpecParser.y"
  {yyVal = getCurrentStaff(); 
                               ((NotationStaff)yyVal).add(((NotationVoice)yyVals[0+yyTop]));}
  break;
case 8:
					// line 235 "EpecParser.y"
  {yyVal = getCurrentVoice(); ((NotationVoice)yyVal).add(((NotationChord)yyVals[0+yyTop]));}
  break;
case 9:
					// line 236 "EpecParser.y"
  {}
  break;
case 10:
					// line 237 "EpecParser.y"
  {yyVal = getCurrentVoice(); ((NotationVoice)yyVal).add(((NotationChord)yyVals[0+yyTop]));}
  break;
case 11:
					// line 238 "EpecParser.y"
  {system.addBarline(new Barline(context.attack));}
  break;
case 12:
					// line 239 "EpecParser.y"
  {yyVal = getCurrentVoice(); ((NotationVoice)yyVal).add(((NotationChord)yyVals[0+yyTop]));}
  break;
case 13:
					// line 243 "EpecParser.y"
  {yyVal = ((NotationChord)yyVals[-1+yyTop]); context.attack = context.attack.add(context.duration);
                               context.linebreaks.add(context.attack);}
  break;
case 14:
					// line 245 "EpecParser.y"
  {yyVal = ((NotationChord)yyVals[0+yyTop]); context.attack = context.attack.add(context.duration);}
  break;
case 15:
					// line 246 "EpecParser.y"
  {context.octave = ((Integer)yyVals[0+yyTop]);   yyVal = null;}
  break;
case 16:
					// line 247 "EpecParser.y"
  {context.duration = ((Rational)yyVals[0+yyTop]); yyVal = null;}
  break;
case 17:
					// line 248 "EpecParser.y"
  {context.attack = context.attack.add(((Rational)yyVals[-1+yyTop])); yyVal = null;
                               context.linebreaks.add(context.attack);}
  break;
case 18:
					// line 250 "EpecParser.y"
  {context.attack = context.attack.add(((Rational)yyVals[0+yyTop])); yyVal = null;}
  break;
case 19:
					// line 251 "EpecParser.y"
  {yyVal = null;}
  break;
case 20:
					// line 252 "EpecParser.y"
  {yyVal = null;}
  break;
case 21:
					// line 253 "EpecParser.y"
  {yyVal = null;}
  break;
case 22:
					// line 254 "EpecParser.y"
  {}
  break;
case 23:
					// line 255 "EpecParser.y"
  {}
  break;
case 24:
					// line 256 "EpecParser.y"
  {}
  break;
case 25:
					// line 257 "EpecParser.y"
  {context.linebreaks.add(context.attack);}
  break;
case 26:
					// line 258 "EpecParser.y"
  {context.linebreaks.add(context.attack);}
  break;
case 27:
					// line 263 "EpecParser.y"
  {Rational tupletDuration = new Rational(1, ((Integer)yyVals[-3+yyTop]).intValue());
                                  getCurrentVoice().addTuplet((Note[])((Vector)yyVals[-1+yyTop]).toArray(new Note[]{}), tupletDuration);
                                  context.pitches = null;
                                  yyVal = tupletDuration;}
  break;
case 28:
					// line 271 "EpecParser.y"
  {yyVal = ((NotationChord)yyVals[0+yyTop]);}
  break;
case 29:
					// line 272 "EpecParser.y"
  {yyVal = new NotationChord(system.getContext(), new Note(new ScoreNote(context.attack, context.duration, 'r', (byte)0, (byte)0), null));}
  break;
case 30:
					// line 273 "EpecParser.y"
{Note n = new Note(new ScoreNote(context.attack, context.duration, 'r', (byte)0, (byte)1), null);n.addRenderingHint("visible", false);yyVal = new NotationChord(system.getContext(), n);}
  break;
case 31:
					// line 278 "EpecParser.y"
  {yyVal = ((NotationChord)yyVals[0+yyTop]);
                               if (context.beam != null) context.beam.add(((NotationChord)yyVals[0+yyTop]));}
  break;
case 32:
					// line 280 "EpecParser.y"
  {yyVal = ((NotationChord)yyVals[-1+yyTop]);}
  break;
case 33:
					// line 285 "EpecParser.y"
  {context.prevChord = context.currentChord;
                               yyVal = context.currentChord = new NotationChord(system.getContext());
                               ((NotationChord)yyVal).add(((Note)yyVals[0+yyTop]));}
  break;
case 34:
					// line 288 "EpecParser.y"
  {context.prevChord = context.currentChord;
                               yyVal = context.currentChord = new NotationChord(system.getContext()); 
                               contextStack.push(context);}
  break;
case 35:
					// line 292 "EpecParser.y"
  {context = (EpecParserContext)contextStack.pop(); yyVal = ((NotationChord)yyVals[-2+yyTop]);}
  break;
case 36:
					// line 293 "EpecParser.y"
  {System.out.println("REPEATER NOT IMPLEMENTED YET");}
  break;
case 37:
					// line 298 "EpecParser.y"
  {if (context.pitches == null) context.pitches = new Vector(); yyVal = context.pitches;
                               ((Vector)yyVal).add(((Note)yyVals[0+yyTop]));}
  break;
case 38:
					// line 300 "EpecParser.y"
  {if (context.pitches == null) context.pitches = new Vector(); yyVal = context.pitches;
                               ((Vector)yyVal).add(((Note)yyVals[0+yyTop]));}
  break;
case 39:
					// line 305 "EpecParser.y"
  {ScoreNote sn = new ScoreNote(context.attack, context.duration, ((Character)yyVals[0+yyTop]).charValue(), context.octave.byteValue(), (byte)0);
  								Note n = new Note(sn, null);
  								if (context.slur != null) context.slur.add(n);
  								yyVal = n;}
  break;
case 40:
					// line 308 "EpecParser.y"
  {ScoreNote sn = new ScoreNote(context.attack, context.duration, ((Character)yyVals[-1+yyTop]).charValue(), context.octave.byteValue(), ((Integer)yyVals[0+yyTop]).byteValue());
  								Note n = new Note(sn, null);
  								if (context.slur != null) context.slur.add(n);
  								yyVal = n;}
  break;
case 41:
					// line 314 "EpecParser.y"
  {context.octave = ((Integer)yyVals[0+yyTop]);}
  break;
case 42:
					// line 315 "EpecParser.y"
  {context.currentChord.add(((Note)yyVals[0+yyTop]));}
  break;
case 43:
					// line 316 "EpecParser.y"
  {context.octave = ((Integer)yyVals[0+yyTop]);}
  break;
case 44:
					// line 317 "EpecParser.y"
  {context.currentChord.add(((Note)yyVals[0+yyTop]));}
  break;
case 45:
					// line 321 "EpecParser.y"
  {yyVal = ((Integer)yyVals[0+yyTop]);}
  break;
case 46:
					// line 322 "EpecParser.y"
  {yyVal = ((Integer)yyVals[0+yyTop]);}
  break;
case 47:
					// line 326 "EpecParser.y"
  {yyVal = ((Integer)yyVals[0+yyTop]);}
  break;
case 48:
					// line 327 "EpecParser.y"
  {yyVal = ((Integer)yyVals[0+yyTop]);}
  break;
case 49:
					// line 328 "EpecParser.y"
  {yyVal = new Integer(((Integer)yyVals[0+yyTop]).intValue() - 4);}
  break;
case 50:
					// line 333 "EpecParser.y"
  {Rational r = new Rational(1,((Integer)yyVals[0+yyTop]).intValue()); if(r.getDenom()==6) r.setDenom(16);yyVal = r; }
  break;
case 51:
					// line 334 "EpecParser.y"
  {yyVal = new Rational(2); 
                               yyVal = ((Rational)yyVal).sub(1, 1 << ((Integer)yyVals[0+yyTop]).intValue());
                               yyVal = ((Rational)yyVal).mul(new Rational(1,((Integer)yyVals[-1+yyTop]).intValue()));}
  break;
case 52:
					// line 340 "EpecParser.y"
  {getCurrentStaff().setClefType(((Character)yyVals[0+yyTop]).charValue());}
  break;
case 53:
					// line 341 "EpecParser.y"
  {getCurrentStaff().setClefType(((Character)yyVals[0+yyTop]).charValue(), ((Integer)yyVals[-1+yyTop]).intValue()); }
  break;
case 54:
					// line 342 "EpecParser.y"
  {getCurrentStaff().setClefType(((Character)yyVals[-2+yyTop]).charValue(), ((Integer)yyVals[0+yyTop]).intValue(), 0);}
  break;
case 55:
					// line 343 "EpecParser.y"
  {getCurrentStaff().setClefType(((Character)yyVals[-2+yyTop]).charValue(), ((Integer)yyVals[0+yyTop]).intValue(), ((Integer)yyVals[-3+yyTop]).intValue());}
  break;
case 56:
					// line 347 "EpecParser.y"
  {yyVal = ((Character)yyVals[0+yyTop]);}
  break;
case 57:
					// line 348 "EpecParser.y"
  {yyVal = new Character('p');}
  break;
case 58:
					// line 352 "EpecParser.y"
  { KeyMarker marker = new KeyMarker(context.attack, 0L);
						                        marker.setAccidentalNum(((Integer)yyVals[-1+yyTop]).intValue() * ((Integer)yyVals[0+yyTop]).intValue());
   												context.meterTrack.add(marker);}
  break;
case 59:
					// line 355 "EpecParser.y"
  {}
  break;
case 60:
					// line 356 "EpecParser.y"
  {}
  break;
case 61:
					// line 360 "EpecParser.y"
  {yyVal = new Integer(1);}
  break;
case 62:
					// line 361 "EpecParser.y"
  {yyVal = new Integer(-1);}
  break;
case 63:
					// line 365 "EpecParser.y"
  {
	context.meterTrack.add(new TimeSignatureMarker(((Integer)yyVals[-2+yyTop]).intValue(), ((Integer)yyVals[0+yyTop]).intValue(), context.attack));
}
  break;
case 64:
					// line 370 "EpecParser.y"
  {}
  break;
case 65:
					// line 374 "EpecParser.y"
  {context.beam = new BeamContainer(system.getContext());}
  break;
case 66:
					// line 375 "EpecParser.y"
  {getCurrentVoice().addBeamContainer(context.beam); context.beam = null;}
  break;
case 67:
					// line 379 "EpecParser.y"
  {context.slur = new SlurContainer(system.getContext());}
  break;
case 68:
					// line 380 "EpecParser.y"
  {getCurrentVoice().addSlurContainer(context.slur); context.slur = null;}
  break;
case 69:
					// line 384 "EpecParser.y"
  {yyVal = new Integer(1);}
  break;
case 70:
					// line 385 "EpecParser.y"
  {yyVal = new Integer(((Integer)yyVal).intValue()+1);}
  break;
case 71:
					// line 389 "EpecParser.y"
  {yyVal = new Integer(-1);}
  break;
case 72:
					// line 390 "EpecParser.y"
  {yyVal = new Integer(((Integer)yyVal).intValue()-1);}
  break;
case 73:
					// line 395 "EpecParser.y"
  {}
  break;
case 74:
					// line 396 "EpecParser.y"
  {}
  break;
case 75:
					// line 397 "EpecParser.y"
  {}
  break;
case 76:
					// line 398 "EpecParser.y"
  {}
  break;
					// line 758 "-"
        }
        yyTop -= YyLenClass.yyLen[yyN];
        yyState = yyStates[yyTop];
        int yyM = YyLhsClass.yyLhs[yyN];
        if (yyState == 0 && yyM == 0) {
          if (yydebug != null) yydebug.shift(0, yyFinal);
          yyState = yyFinal;
          if (yyToken < 0) {
            yyToken = yyLex.advance() ? yyLex.token() : 0;
            if (yydebug != null)
               yydebug.lex(yyState, yyToken,yyname(yyToken), yyLex.value());
          }
          if (yyToken == 0) {
            if (yydebug != null) yydebug.accept(yyVal);
            return yyVal;
          }
          continue yyLoop;
        }
        if ((yyN = YyGindexClass.yyGindex[yyM]) != 0 && (yyN += yyState) >= 0
            && yyN < YyTableClass.yyTable.length && YyCheckClass.yyCheck[yyN] == yyState)
          yyState = YyTableClass.yyTable[yyN];
        else
          yyState = YyDgotoClass.yyDgoto[yyM];
        if (yydebug != null) yydebug.shift(yyStates[yyTop], yyState);
	 continue yyLoop;
      }
    }
  }

  protected static final class YyLhsClass {

    public static final short yyLhs [] = {              -1,
          0,   20,    0,    0,    8,   21,    8,   11,   22,   11,
         23,   11,    9,    9,    9,    9,    9,    9,    9,    9,
          9,    9,    9,    9,    9,    9,    7,   10,   10,   10,
         14,   14,   15,   28,   15,   15,   13,   13,   12,   12,
         27,   27,   27,   27,    1,    1,    2,    2,    2,    6,
          6,   16,   16,   16,   16,   19,   19,   17,   17,   17,
          3,    3,   18,   18,   24,   24,   25,   25,    4,    4,
          5,    5,   26,   26,   26,   26,
    };
  } /* End of class YyLhsClass */

  protected static final class YyLenClass {

    public static final short yyLen [] = {           2,
          1,    0,    4,    1,    1,    0,    4,    1,    0,    3,
          0,    4,    2,    1,    1,    1,    2,    1,    1,    1,
          1,    1,    1,    1,    2,    2,    5,    1,    1,    1,
          1,    2,    1,    0,    4,    1,    1,    2,    1,    2,
          1,    1,    2,    2,    1,    1,    1,    1,    2,    1,
          2,    2,    3,    4,    5,    1,    1,    3,    3,    4,
          1,    1,    4,    6,    1,    1,    1,    1,    1,    2,
          1,    2,    1,    1,    1,    1,
    };
  } /* End class YyLenClass */

  protected static final class YyDefRedClass {

    public static final short yyDefRed [] = {            0,
          4,    0,    0,   47,   48,    0,    0,    0,   76,   73,
         74,   75,    0,   29,   30,   34,   36,    0,   67,   68,
         65,   66,    0,   15,   16,    0,    0,    8,    0,    0,
         33,   28,    0,   19,   20,   21,    0,    0,   24,   69,
         71,   40,    0,    0,   51,   56,    0,   57,    0,    0,
          0,    0,    0,    0,    0,   49,    2,   17,    6,   13,
         11,    0,   32,   25,   26,   70,   72,    0,    0,   59,
         61,   62,    0,   58,    0,    0,    0,   41,   42,    0,
          0,    0,    0,   10,    0,   54,   60,   63,    0,   37,
          0,   35,   43,   44,    0,    0,   12,   55,    0,   27,
         38,   64,
    };
  } /* End of class YyDefRedClass */

  protected static final class YyDgotoClass {

    public static final short yyDgoto [] = {            23,
         42,   24,   73,   43,   44,   25,   26,   27,   28,   29,
         30,   31,   91,   32,   33,   34,   35,   36,   49,   81,
         82,   62,   83,   37,   38,   39,   80,   55,
    };
  } /* End of class YyDgotoClass */

  protected static final class YySindexClass {

    public static final short yySindex [] = {          753,
          0,  -31, -243,    0,    0,  -91, -250,  -36,    0,    0,
          0,    0, -237,    0,    0,    0,    0, -236,    0,    0,
          0,    0, -242,    0,    0, -244, -238,    0, -241, -227,
          0,    0,  -29,    0,    0,    0, -235, -234,    0,    0,
          0,    0,   -6,   -7,    0,    0,  -95,    0,  -19,  -43,
        -27,   -2, -219,  -12,  -96,    0,    0,    0,    0,    0,
          0,  775,    0,    0,    0,    0,    0,  -11, -211,    0,
          0,    0, -214,    0, -208,    5, -204,    0,    0, -105,
        775,  775,  775,    0, -205,    0,    0,    0, -203,    0,
        -57,    0,    0,    0, -238, -227,    0,    0,   14,    0,
          0,    0,
    };
  } /* End of class YySindexClass */

  protected static final class YyRindexClass {

    public static final short yyRindex [] = {            0,
          0,    1,  117,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,  141,   10,    0,  379,  491,
          0,    0,   93,    0,    0,    0,  403,  425,    0,    0,
          0,    0,   25,   49,    0,    0,    0,    0,  447,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,  469,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,   11,  731,    0,    0,    0,    0,
          0,    0,
    };
  } /* End of class YyRindexClass */

  protected static final class YyGindexClass {

    public static final short yyGindex [] = {            0,
          0,  -52,    6,    0,    0,    0,    0,  -23,  -49,    0,
        -22,  -47,    0,    0,    0,    0,    0,    0,   12,    0,
          0,    0,    0,    0,    0,    0,    0,    0,
    };
  } /* End of class YyGindexClass */

  protected static final class YyTableClass {

    public static final short yyTable [] = {            71,
         39,   72,   78,   53,  100,   18,   50,   79,   51,    1,
          3,   40,   84,   41,   18,   71,   48,   72,   45,   92,
         48,   54,   56,   57,   45,   58,   59,   93,   60,   90,
         61,   63,   94,   97,   64,   65,   66,   67,   69,   76,
         39,   39,   39,  101,   75,   77,   85,   86,   46,   87,
         88,   89,    2,   98,  102,   99,   74,   95,   68,   96,
         39,   39,   39,    0,   45,   45,   45,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,   45,   45,   45,    0,   46,   46,
         46,   39,   31,   39,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,   39,    0,    0,   46,   46,
         46,   39,    0,    0,   39,   45,   50,   45,    0,    0,
          0,    0,    0,   39,    0,   39,    0,    0,    0,   45,
          0,    0,   31,   31,   31,   45,    0,    0,   45,   46,
         18,   46,    0,    0,    0,    0,    0,   45,    0,   45,
          0,    2,   31,   46,    4,    5,   50,   50,   50,   46,
          2,   46,   46,    4,    5,   46,    0,   47,    0,    0,
          0,   46,    0,   46,    0,    0,   50,    0,    0,    0,
         18,   18,   18,   31,    0,   31,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,   31,    0,    2,
         18,    0,    0,   31,    0,    0,   31,   50,    0,   50,
          0,    0,    0,    0,    0,   31,    0,    0,    0,    0,
         70,   50,   52,    0,    0,    0,    0,   50,    0,    0,
         50,   18,    0,   18,    0,    0,    0,    0,    0,   50,
          0,    0,    0,    0,    0,   18,    0,    0,    0,    0,
          0,   18,    0,    0,   18,    0,    0,   39,   39,   39,
         39,   39,    0,   18,    0,   39,   39,   39,   39,   39,
         39,   39,   39,   39,   39,    1,    3,    0,    0,    0,
          0,   45,   45,   45,   45,   45,    0,    0,    0,   45,
         45,   45,   45,   45,   45,   45,   45,   45,   45,    0,
          0,    0,    0,    0,    0,   46,   46,   46,   46,   46,
          0,    0,    0,   46,   46,   46,   46,   46,   46,   46,
         46,   46,   46,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,   31,
         31,   31,   31,   31,    0,    0,    0,   31,   31,   31,
         31,   31,   31,   31,   31,   31,   31,    0,    0,    0,
          0,    0,    0,   50,   50,   50,   50,   50,   14,    0,
          0,   50,   50,   50,   50,   50,    0,   50,   50,   50,
         50,    0,    0,    0,    0,    0,    0,   18,   18,   18,
         18,   18,   22,    0,    0,   18,   18,   18,   18,   18,
          0,   18,   18,   18,   18,    0,    0,    0,   14,   14,
         14,    0,    0,    0,   23,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,   14,    0,
          0,    0,   22,   22,   22,    0,   52,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,   22,    0,   23,   23,   23,    0,   53,   14,
          0,   14,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,   14,   23,    0,   52,   52,   52,   14,
          5,    0,   14,   22,    0,   22,    0,    0,    0,    0,
          0,   14,    0,    0,    0,    0,   52,   22,   53,   53,
         53,    0,    0,   22,    0,   23,   22,   23,    0,    0,
          0,    0,    0,    0,    0,   22,    0,    0,   53,   23,
          9,    9,    9,    0,    0,   23,    0,   52,   23,   52,
          0,    0,    0,    0,    0,    0,    0,   23,    0,    0,
          9,   52,    0,    0,    0,    0,    0,   52,    0,   53,
         52,   53,    0,    0,    0,    0,    0,    0,    0,   52,
          0,    0,    0,   53,    0,    0,    0,    0,    0,   53,
          0,    9,   53,    9,    0,    0,    0,    0,    0,    0,
          0,   53,    0,    0,    0,    9,    0,    0,    0,    0,
          0,    9,    0,    0,    9,    0,    0,    0,    0,    0,
          0,    0,    0,    9,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,   14,   14,   14,   14,   14,
          0,    0,    0,   14,   14,   14,   14,   14,    0,   14,
         14,   14,   14,    0,    0,    0,    0,    0,    0,   22,
         22,   22,   22,   22,    0,    0,    0,   22,   22,   22,
         22,   22,    0,   22,   22,   22,   22,    0,    0,    0,
          0,   23,   23,   23,   23,   23,    0,    0,    0,   23,
         23,   23,   23,   23,    0,   23,   23,   23,   23,    0,
          0,    0,    0,   52,   52,   52,   52,   52,    0,    0,
          0,   52,   52,   52,   52,   52,    0,   52,   52,   52,
         52,    0,    0,    0,    0,   53,   53,   53,   53,   53,
          7,    0,    0,   53,   53,   53,   53,   53,    0,   53,
         53,   53,   53,    0,    0,    0,    0,    9,    0,    9,
          9,    9,    0,    0,    0,    5,    5,    9,    9,    9,
          0,    9,    9,    9,    9,    0,    0,    0,    0,    0,
          9,    9,    9,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          9,    0,   19,   20,   17,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,   13,    0,   19,   20,   17,    0,    0,    0,
          0,    9,    0,    9,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,   13,    9,    0,    0,    0,    0,
          0,    9,    0,   21,    9,   22,    0,    0,    0,    0,
          0,    0,    0,    9,    0,    0,    0,   15,    0,    0,
          0,    0,    0,   18,    0,   21,   14,   22,    0,    0,
          0,    0,    0,    0,    0,   16,    0,    0,    0,   15,
          0,    0,    0,    0,    0,   18,    0,    0,   14,    0,
          0,    0,    0,    0,    0,    0,    0,   16,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    9,    0,    9,
          9,    9,    0,    0,    0,    7,    7,    9,    9,    9,
          0,    9,    9,    9,    9,    0,    0,    0,    1,    2,
          0,    3,    4,    5,    0,    0,    0,    0,    0,    6,
          7,    8,    0,    9,   10,   11,   12,    0,    0,    0,
          0,    2,    0,    3,    4,    5,    0,    0,    0,    0,
          0,    6,    7,    8,    0,    9,   10,   11,   12,
    };
  } /* End of class YyTableClass */

  protected static final class YyCheckClass {

    public static final short yyCheck [] = {            43,
          0,   45,   55,   40,   62,  111,  257,   55,  259,    0,
          0,   43,   62,   45,  111,   43,  112,   45,  262,  125,
        112,  259,  259,  266,    0,  270,  265,   80,  270,   77,
        258,   61,   80,   83,  270,  270,   43,   45,   58,  259,
         40,   41,   42,   91,   47,   58,   58,  259,    0,  264,
        259,   47,  257,  259,   41,  259,   51,   81,   47,   82,
         60,   61,   62,   -1,   40,   41,   42,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   60,   61,   62,   -1,   40,   41,
         42,   91,    0,   93,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,  105,   -1,   -1,   60,   61,
         62,  111,   -1,   -1,  114,   91,    0,   93,   -1,   -1,
         -1,   -1,   -1,  123,   -1,  125,   -1,   -1,   -1,  105,
         -1,   -1,   40,   41,   42,  111,   -1,   -1,  114,   91,
          0,   93,   -1,   -1,   -1,   -1,   -1,  123,   -1,  125,
         -1,  257,   60,  105,  260,  261,   40,   41,   42,  111,
        257,  257,  114,  260,  261,  257,   -1,  259,   -1,   -1,
         -1,  123,   -1,  125,   -1,   -1,   60,   -1,   -1,   -1,
         40,   41,   42,   91,   -1,   93,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,  105,   -1,  257,
         60,   -1,   -1,  111,   -1,   -1,  114,   91,   -1,   93,
         -1,   -1,   -1,   -1,   -1,  123,   -1,   -1,   -1,   -1,
        264,  105,  259,   -1,   -1,   -1,   -1,  111,   -1,   -1,
        114,   91,   -1,   93,   -1,   -1,   -1,   -1,   -1,  123,
         -1,   -1,   -1,   -1,   -1,  105,   -1,   -1,   -1,   -1,
         -1,  111,   -1,   -1,  114,   -1,   -1,  257,  258,  259,
        260,  261,   -1,  123,   -1,  265,  266,  267,  268,  269,
        270,  271,  272,  273,  274,  266,  266,   -1,   -1,   -1,
         -1,  257,  258,  259,  260,  261,   -1,   -1,   -1,  265,
        266,  267,  268,  269,  270,  271,  272,  273,  274,   -1,
         -1,   -1,   -1,   -1,   -1,  257,  258,  259,  260,  261,
         -1,   -1,   -1,  265,  266,  267,  268,  269,  270,  271,
        272,  273,  274,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,
        258,  259,  260,  261,   -1,   -1,   -1,  265,  266,  267,
        268,  269,  270,  271,  272,  273,  274,   -1,   -1,   -1,
         -1,   -1,   -1,  257,  258,  259,  260,  261,    0,   -1,
         -1,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
        274,   -1,   -1,   -1,   -1,   -1,   -1,  257,  258,  259,
        260,  261,    0,   -1,   -1,  265,  266,  267,  268,  269,
         -1,  271,  272,  273,  274,   -1,   -1,   -1,   40,   41,
         42,   -1,   -1,   -1,    0,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   60,   -1,
         -1,   -1,   40,   41,   42,   -1,    0,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   60,   -1,   40,   41,   42,   -1,    0,   91,
         -1,   93,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,  105,   60,   -1,   40,   41,   42,  111,
          0,   -1,  114,   91,   -1,   93,   -1,   -1,   -1,   -1,
         -1,  123,   -1,   -1,   -1,   -1,   60,  105,   40,   41,
         42,   -1,   -1,  111,   -1,   91,  114,   93,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,  123,   -1,   -1,   60,  105,
         40,   41,   42,   -1,   -1,  111,   -1,   91,  114,   93,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,  123,   -1,   -1,
         60,  105,   -1,   -1,   -1,   -1,   -1,  111,   -1,   91,
        114,   93,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  123,
         -1,   -1,   -1,  105,   -1,   -1,   -1,   -1,   -1,  111,
         -1,   91,  114,   93,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,  123,   -1,   -1,   -1,  105,   -1,   -1,   -1,   -1,
         -1,  111,   -1,   -1,  114,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,  123,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,  257,  258,  259,  260,  261,
         -1,   -1,   -1,  265,  266,  267,  268,  269,   -1,  271,
        272,  273,  274,   -1,   -1,   -1,   -1,   -1,   -1,  257,
        258,  259,  260,  261,   -1,   -1,   -1,  265,  266,  267,
        268,  269,   -1,  271,  272,  273,  274,   -1,   -1,   -1,
         -1,  257,  258,  259,  260,  261,   -1,   -1,   -1,  265,
        266,  267,  268,  269,   -1,  271,  272,  273,  274,   -1,
         -1,   -1,   -1,  257,  258,  259,  260,  261,   -1,   -1,
         -1,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
        274,   -1,   -1,   -1,   -1,  257,  258,  259,  260,  261,
          0,   -1,   -1,  265,  266,  267,  268,  269,   -1,  271,
        272,  273,  274,   -1,   -1,   -1,   -1,  257,   -1,  259,
        260,  261,   -1,   -1,   -1,  265,  266,  267,  268,  269,
         -1,  271,  272,  273,  274,   -1,   -1,   -1,   -1,   -1,
         40,   41,   42,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         60,   -1,   40,   41,   42,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   60,   -1,   40,   41,   42,   -1,   -1,   -1,
         -1,   91,   -1,   93,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   60,  105,   -1,   -1,   -1,   -1,
         -1,  111,   -1,   91,  114,   93,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,  123,   -1,   -1,   -1,  105,   -1,   -1,
         -1,   -1,   -1,  111,   -1,   91,  114,   93,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,  123,   -1,   -1,   -1,  105,
         -1,   -1,   -1,   -1,   -1,  111,   -1,   -1,  114,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,  123,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,   -1,  259,
        260,  261,   -1,   -1,   -1,  265,  266,  267,  268,  269,
         -1,  271,  272,  273,  274,   -1,   -1,   -1,  256,  257,
         -1,  259,  260,  261,   -1,   -1,   -1,   -1,   -1,  267,
        268,  269,   -1,  271,  272,  273,  274,   -1,   -1,   -1,
         -1,  257,   -1,  259,  260,  261,   -1,   -1,   -1,   -1,
         -1,  267,  268,  269,   -1,  271,  272,  273,  274,
    };
  } /* End of class YyCheckClass */


  protected static final class YyRuleClass {

    public static final String yyRule [] = {
    "$accept : staves",
    "staves : staff",
    "$$1 :",
    "staves : staves STAFFSEP $$1 staff",
    "staves : error",
    "staff : elements",
    "$$2 :",
    "staff : staff VOICESEP $$2 elements",
    "elements : element",
    "$$3 :",
    "elements : elements $$3 element",
    "$$4 :",
    "elements : elements BARLINE $$4 element",
    "element : timed_event LINEBREAK",
    "element : timed_event",
    "element : octave",
    "element : duration",
    "element : tuplet LINEBREAK",
    "element : tuplet",
    "element : clef",
    "element : key_signature",
    "element : time_signature",
    "element : beam",
    "element : slur",
    "element : accent",
    "element : beam LINEBREAK",
    "element : slur LINEBREAK",
    "tuplet : '<' INTEGER ':' pitches '>'",
    "timed_event : chord",
    "timed_event : 'r'",
    "timed_event : 'i'",
    "chord : basic_chord",
    "chord : basic_chord '='",
    "basic_chord : pitch",
    "$$5 :",
    "basic_chord : '{' $$5 chord_body '}'",
    "basic_chord : '*'",
    "pitches : pitch",
    "pitches : pitches pitch",
    "pitch : BASEPITCH",
    "pitch : BASEPITCH accidental",
    "chord_body : octave",
    "chord_body : pitch",
    "chord_body : chord_body octave",
    "chord_body : chord_body pitch",
    "accidental : plus_seq",
    "accidental : minus_seq",
    "octave : AP_SEQ",
    "octave : COLON_SEQ",
    "octave : 'o' INTEGER",
    "duration : INTEGER",
    "duration : INTEGER DOT_SEQ",
    "clef : CLEF clef_letter",
    "clef : CLEF INTEGER clef_letter",
    "clef : CLEF clef_letter ':' INTEGER",
    "clef : CLEF INTEGER clef_letter ':' INTEGER",
    "clef_letter : BASEPITCH",
    "clef_letter : 'p'",
    "key_signature : KEYSIGNATURE INTEGER keysig_type",
    "key_signature : KEYSIGNATURE BASEPITCH MODE",
    "key_signature : KEYSIGNATURE BASEPITCH keysig_type MODE",
    "keysig_type : '+'",
    "keysig_type : '-'",
    "time_signature : TIMESIGNATURE INTEGER '/' INTEGER",
    "time_signature : TIMESIGNATURE '(' INTEGER '/' INTEGER ')'",
    "beam : '['",
    "beam : ']'",
    "slur : '('",
    "slur : ')'",
    "plus_seq : '+'",
    "plus_seq : plus_seq '+'",
    "minus_seq : '-'",
    "minus_seq : minus_seq '-'",
    "accent : STACCATO",
    "accent : MARCATO",
    "accent : PORTATO",
    "accent : NOACCENT",
    };
  } /* End of class YyRuleClass */

  protected static final class YyNameClass {

    public static final String yyName [] = {    
    "end-of-file",null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    "'('","')'","'*'","'+'",null,"'-'",null,"'/'",null,null,null,null,
    null,null,null,null,null,null,"':'",null,"'<'","'='","'>'",null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,"'['",
    null,"']'",null,null,null,null,null,null,null,null,null,null,null,
    "'i'",null,null,null,null,null,"'o'","'p'",null,"'r'",null,null,null,
    null,null,null,null,null,"'{'",null,"'}'",null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    "BASEPITCH","BARLINE","INTEGER","AP_SEQ","COLON_SEQ","DOT_SEQ",
    "PRINT","MODE","VOICESEP","STAFFSEP","CLEF","KEYSIGNATURE",
    "TIMESIGNATURE","LINEBREAK","NOACCENT","STACCATO","MARCATO","PORTATO",
    };
  } /* End of class YyNameClass */


					// line 401 "EpecParser.y"
} // class Parser
					// line 1220 "-"
