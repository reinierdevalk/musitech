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

					// line 5 "Parser.y"
package de.uos.fmt.musitech.score.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Stack;

import de.uos.fmt.musitech.utility.math.Rational;

class ParserContext
{
   Integer    octave   = new Integer(0);
   Duration   duration = new Duration(1,4);
   Rational   attack   = Rational.ZERO;
   Measure    measure;
   Staff      staff;
   LocalSim   lsim;
   Chord currentChord;
   Chord prevChord;
   Pitch      pitch;
   int        voice = 0;
   Beam       beam;
   Slur       slur;
   Accent     accent;
   Tuplet     tuplet;
}

/** This class is used to parse the text representation of 
    the music notation. A notation system (a set of staves) is described by a string
	 consisting of a sequence of the following elements (described by regular expressions, parenthesis 
	 are used as grouping meta symbols, symbols or symbol sequences in 'single ticks' denote plain characters,
	 the single tick itself is written as \'):<br>
	 <b>clef: </b> C {c | f | g | p} : [1-5]<br> 
	 The lower case letters select the clef type (p means 'percussion').	 
	 The vertical position is denoted by the succeeding line number (1 denotes a staff's bottom line).<p>
	 
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
    @version $Revision: 7766 $, $Date: 2010-03-10 18:53:18 +0100 (Wed, 10 Mar 2010) $ */
class Parser
{
   Stack contextStack   = new Stack();
   ParserContext context = new ParserContext();
   SSystem system        = new SSystem();    // script describes this system
   int numStaves = 1;
   Lexer lexer;
   
  
   /** Runs the parser interactively by asking the user to type in the code manually. */
   public void run (Score score) throws IOException, yyException
   {
      lexer = new Lexer(new InputStreamReader(System.in));
      execute(score);
   }
   

   /** Parses the given string that contains the input code. */
   public void run (Score score, String string) throws IOException, yyException
   {
      lexer = new Lexer(new StringReader(string));
      execute(score);
   }

   
   public void run (Score score, InputStream istream) throws IOException, yyException
   {
      lexer = new Lexer(istream);
      execute(score);
   }  
      

   // helper function; calls yyparse and handles exceptions
   void execute (Score score) throws IOException, yyException
   {
      // >>> uncomment the next lines if you want to see debugging output from the parser <<<
      //jay.yydebug.yyDebug debug = new jay.yydebug.yyDebugAdapter();
		// >>> uncomment the following line if you want to see debugging output from the parser <<<
		//yyparse(lexer, debug);
		yyparse(lexer);
		Page page = new Page();
		system.setParent(page);
		page.add(system);
		page.setParent(score);
		score.add(page);
   }
   

   /** Returns the current measure. If it doesn't exist, a new measure is created. */
   Measure currentMeasure ()
   {
      if (context.measure == null)
      {
         context.measure = new Measure();
      }
      return context.measure;
   }
   
   Measure nextMeasure ()
   {
//    context.measure = (Measure)context.staff.child(context.measure.getNumber()+1);
//      if (context.measure == null)
      context.measure = new Measure();
//      context.attack.assign(0);
      return context.measure;
   }
   
   /** Returns the current staff. If it doesn't exist, a new staff is created. */
   Staff currentStaff ()
   {
      if (context.staff == null)
      {
         System.out.println(">>> creating new staff");
         context.staff = new Staff();
      }
      else
         System.out.println(">>> using current staff");
      return context.staff;
   }

   Staff nextStaff ()
   {
      context.staff = new Staff();
      context.attack.assign(0);
      context.measure = null;
      context.lsim = null;
      context.currentChord = context.prevChord = null;
      context.pitch = null;
      System.out.println(">>> creating new staff");
      return context.staff;
   }

   /** Returns the current LocalSim. If it doesn't exist, a new LocalSim is created. */
   LocalSim currentLocalSim ()
   {
      if (context.measure != null)
      {
         System.out.println(">>> looking for local sim with attack time " + context.attack);
         context.lsim = context.measure.localSim(context.attack);
      }
      else
         context.lsim = null;
      
      if (context.lsim == null)
      {
         System.out.println(">>> creating new local sim with attack time " + context.attack);
         context.lsim = new LocalSim(context.attack);
      }
      else
         System.out.println(">>> using existing local sim");
      return context.lsim;
   }
					// line 196 "-"
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
  public static final int NOACCENT = 270;
  public static final int STACCATO = 271;
  public static final int MARCATO = 272;
  public static final int PORTATO = 273;
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

  protected static final int yyFinal = 24;

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
					// line 226 "Parser.y"
  {system.add(((Staff)yyVals[0+yyTop]));}
  break;
case 2:
					// line 227 "Parser.y"
  {nextStaff();}
  break;
case 3:
					// line 228 "Parser.y"
  {system.add(((Staff)yyVals[0+yyTop]));}
  break;
case 4:
					// line 229 "Parser.y"
  {}
  break;
case 5:
					// line 233 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 6:
					// line 234 "Parser.y"
  {context.voice++; 
                               context.attack.assign(0); 
                               context.measure = (Measure)context.staff.child(0);
                               System.out.println(">>> resetting attack time to 0");}
  break;
case 7:
					// line 238 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 8:
					// line 243 "Parser.y"
  {yyVal = currentStaff(); if (((Measure)yyVals[0+yyTop]).getScoreParent() == null) ((Staff)yyVal).add(((Measure)yyVals[0+yyTop]));}
  break;
case 9:
					// line 244 "Parser.y"
  {yyVal = currentStaff(); if (((Measure)yyVals[0+yyTop]).getScoreParent() == null) ((Staff)yyVal).add(((Measure)yyVals[0+yyTop]));}
  break;
case 10:
					// line 249 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 11:
					// line 250 "Parser.y"
  {yyVal = (yyVals[-1+yyTop]);
                               ((Barline)yyVals[0+yyTop]).setParent(((Measure)yyVal));
                               ((Measure)yyVal).setRightBarline(((Barline)yyVals[0+yyTop]));
                               nextMeasure();}
  break;
case 12:
					// line 258 "Parser.y"
  {if (((LocalSim)yyVals[0+yyTop]) != null) {
                                  yyVal = currentMeasure(); 
                                  if (((LocalSim)yyVals[0+yyTop]).getScoreParent() == null) ((Measure)yyVal).add(((LocalSim)yyVals[0+yyTop]));
                              }}
  break;
case 13:
					// line 262 "Parser.y"
  {if (((LocalSim)yyVals[0+yyTop]) != null) {
                                  yyVal = currentMeasure(); 
                                  if (((LocalSim)yyVals[0+yyTop]).getScoreParent() == null) ((Measure)yyVal).add(((LocalSim)yyVals[0+yyTop]));
                              }}
  break;
case 14:
					// line 269 "Parser.y"
  {yyVal = currentLocalSim();   
                               ((LocalSim)yyVal).add(((Event)yyVals[0+yyTop]));
                               if (context.beam != null)   {context.beam.add(((Event)yyVals[0+yyTop])); ((Event)yyVals[0+yyTop]).setInBeam(true);}
                               if (context.slur != null)   {context.slur.add(((Event)yyVals[0+yyTop]));}
                               if (context.tuplet != null) {context.tuplet.add(((Event)yyVals[0+yyTop]));}
                               if (context.accent != null) {((Event)yyVals[0+yyTop]).setAccent(context.accent);}
                               context.attack = context.attack.add(context.duration.toRational());
                               System.out.println(">>> attack time increased to " + context.attack);}
  break;
case 15:
					// line 277 "Parser.y"
  {context.octave = ((Integer)yyVals[0+yyTop]);   yyVal = null;}
  break;
case 16:
					// line 278 "Parser.y"
  {context.duration = ((Duration)yyVals[0+yyTop]); yyVal = null;}
  break;
case 17:
					// line 279 "Parser.y"
  {}
  break;
case 18:
					// line 280 "Parser.y"
  {currentMeasure().setClef(((Clef)yyVals[0+yyTop])); yyVal = null;}
  break;
case 19:
					// line 281 "Parser.y"
  {currentMeasure().setKeySignature(((KeySignature)yyVals[0+yyTop])); yyVal = null;}
  break;
case 20:
					// line 282 "Parser.y"
  {currentMeasure().setTimeSignature(((TimeSignature)yyVals[0+yyTop])); yyVal = null;}
  break;
case 21:
					// line 283 "Parser.y"
  {}
  break;
case 22:
					// line 284 "Parser.y"
  {}
  break;
case 23:
					// line 285 "Parser.y"
  {System.out.println(">>> Accent " + context.accent.getType());}
  break;
case 24:
					// line 290 "Parser.y"
  {context.tuplet = new Tuplet(((Integer)yyVals[-1+yyTop]).intValue(), context.duration.toRational());}
  break;
case 25:
					// line 291 "Parser.y"
  {currentStaff().addTuplet(context.tuplet); context.tuplet = null;}
  break;
case 26:
					// line 296 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 27:
					// line 297 "Parser.y"
  {yyVal = new Rest(context.duration, context.voice, null);}
  break;
case 28:
					// line 298 "Parser.y"
  {System.out.println("NO INVISIBLE RESTS YET"); yyVal = null;}
  break;
case 29:
					// line 303 "Parser.y"
  {yyVal = (yyVals[0+yyTop]); 
                               if (context.prevChord instanceof TiedChord)
                                  ((TiedChord)context.prevChord).setSuccessor(((Chord)yyVals[0+yyTop]));
                              }
  break;
case 30:
					// line 307 "Parser.y"
  {yyVal = context.currentChord = new TiedChord(((Chord)yyVals[-1+yyTop]));
                               if (context.prevChord instanceof TiedChord)                                
                                  ((TiedChord)context.prevChord).setSuccessor(((Chord)yyVals[-1+yyTop]));
                              }
  break;
case 31:
					// line 315 "Parser.y"
  {context.prevChord = context.currentChord;
                               yyVal = context.currentChord = new Chord(context.duration, context.voice); 
                               ((Chord)yyVal).add(((Pitch)yyVals[0+yyTop]));}
  break;
case 32:
					// line 318 "Parser.y"
  {context.prevChord = context.currentChord;
                               yyVal = context.currentChord = new Chord(context.duration, context.voice); 
                               contextStack.push(context);}
  break;
case 33:
					// line 322 "Parser.y"
  {context = (ParserContext)contextStack.pop(); yyVal = (yyVals[-2+yyTop]);}
  break;
case 34:
					// line 323 "Parser.y"
  {System.out.println("REPEATER NOT IMPLEMENTED YET");}
  break;
case 35:
					// line 328 "Parser.y"
  {yyVal = new Pitch(context.duration, ((Character)yyVals[0+yyTop]).charValue(), (byte)0, false, context.octave.byteValue());}
  break;
case 36:
					// line 329 "Parser.y"
  {yyVal = new Pitch(context.duration, ((Character)yyVals[-1+yyTop]).charValue(), ((Integer)yyVals[0+yyTop]).byteValue(), false, context.octave.byteValue());}
  break;
case 37:
					// line 330 "Parser.y"
  {yyVal = new Pitch(context.duration, ((Character)yyVals[-1+yyTop]).charValue(), (byte)0, true, context.octave.byteValue());}
  break;
case 38:
					// line 331 "Parser.y"
  {yyVal = new Pitch(context.duration, ((Character)yyVals[-2+yyTop]).charValue(), ((Integer)yyVals[-1+yyTop]).byteValue(), true, context.octave.byteValue());}
  break;
case 39:
					// line 335 "Parser.y"
  {context.octave = ((Integer)yyVals[0+yyTop]);}
  break;
case 40:
					// line 336 "Parser.y"
  {context.currentChord.add(((Pitch)yyVals[0+yyTop]));}
  break;
case 41:
					// line 337 "Parser.y"
  {context.octave = ((Integer)yyVals[0+yyTop]);}
  break;
case 42:
					// line 338 "Parser.y"
  {context.currentChord.add(((Pitch)yyVals[0+yyTop]));}
  break;
case 43:
					// line 342 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 44:
					// line 343 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 45:
					// line 347 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 46:
					// line 348 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 47:
					// line 349 "Parser.y"
  {yyVal = new Integer(((Integer)yyVals[0+yyTop]).intValue() - 4);}
  break;
case 48:
					// line 354 "Parser.y"
  {yyVal = new Duration(new Rational(1,((Integer)yyVals[0+yyTop]).intValue()), 0);
                               if (context.tuplet != null) yyVal = new IrregularDuration(context.tuplet, ((Duration)yyVal));}
  break;
case 49:
					// line 356 "Parser.y"
  {yyVal = new Duration(new Rational(1,((Integer)yyVals[-1+yyTop]).intValue()), ((Integer)yyVals[0+yyTop]).intValue());
                               if (context.tuplet != null) yyVal = new IrregularDuration(context.tuplet, ((Duration)yyVal));}
  break;
case 50:
					// line 361 "Parser.y"
  {yyVal = new Clef(((Character)yyVals[-2+yyTop]).charValue(), ((Integer)yyVals[0+yyTop]).intValue());}
  break;
case 51:
					// line 365 "Parser.y"
  {yyVal = (yyVals[0+yyTop]);}
  break;
case 52:
					// line 366 "Parser.y"
  {yyVal = new Character('p');}
  break;
case 53:
					// line 370 "Parser.y"
  {yyVal = new KeySignature(((Integer)yyVals[-1+yyTop]).intValue()*((Integer)yyVals[0+yyTop]).intValue());}
  break;
case 54:
					// line 371 "Parser.y"
  {yyVal = new KeySignature(((Character)yyVals[-1+yyTop]).charValue(), 0, ((String)yyVals[0+yyTop]));}
  break;
case 55:
					// line 372 "Parser.y"
  {yyVal = new KeySignature(((Character)yyVals[-2+yyTop]).charValue(), ((Integer)yyVals[-1+yyTop]).intValue(), ((String)yyVals[0+yyTop]));}
  break;
case 56:
					// line 376 "Parser.y"
  {yyVal = new Integer(1);}
  break;
case 57:
					// line 377 "Parser.y"
  {yyVal = new Integer(-1);}
  break;
case 58:
					// line 381 "Parser.y"
  {yyVal = new TimeSignature(((Integer)yyVals[-2+yyTop]).intValue(), ((Integer)yyVals[0+yyTop]).intValue(), false);}
  break;
case 59:
					// line 382 "Parser.y"
  {yyVal = new TimeSignature(((Integer)yyVals[-3+yyTop]).intValue(), ((Integer)yyVals[-1+yyTop]).intValue(), true);}
  break;
case 60:
					// line 386 "Parser.y"
  {if (context.beam == null) context.beam = new Beam();}
  break;
case 61:
					// line 387 "Parser.y"
  {system.addBeam(context.beam); context.beam = null;}
  break;
case 62:
					// line 391 "Parser.y"
  {if (context.slur == null) context.slur = new Slur();}
  break;
case 63:
					// line 392 "Parser.y"
  {system.addSlur(context.slur); context.slur = null;}
  break;
case 64:
					// line 396 "Parser.y"
  {yyVal = new Integer(1);}
  break;
case 65:
					// line 397 "Parser.y"
  {yyVal = new Integer(((Integer)yyVal).intValue()+1);}
  break;
case 66:
					// line 401 "Parser.y"
  {yyVal = new Integer(-1);}
  break;
case 67:
					// line 402 "Parser.y"
  {yyVal = new Integer(((Integer)yyVal).intValue()-1);}
  break;
case 68:
					// line 407 "Parser.y"
  {context.accent = new Accent(Accent.STACCATO);}
  break;
case 69:
					// line 408 "Parser.y"
  {context.accent = new Accent(Accent.MARCATO);}
  break;
case 70:
					// line 409 "Parser.y"
  {context.accent = new Accent(Accent.PORTATO);}
  break;
case 71:
					// line 410 "Parser.y"
  {context.accent = null;}
  break;
					// line 748 "-"
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
          0,   20,    0,    0,    9,   21,    9,   10,   10,    7,
          7,    8,    8,   11,   11,   11,   11,   11,   11,   11,
         11,   11,   11,   22,   22,   12,   12,   12,   14,   14,
         15,   27,   15,   15,   13,   13,   13,   13,   26,   26,
         26,   26,    1,    1,    2,    2,    2,    6,    6,   16,
         19,   19,   17,   17,   17,    3,    3,   18,   18,   23,
         23,   24,   24,    4,    4,    5,    5,   25,   25,   25,
         25,
    };
  } /* End of class YyLhsClass */

  protected static final class YyLenClass {

    public static final short yyLen [] = {           2,
          1,    0,    4,    1,    1,    0,    4,    1,    2,    1,
          2,    1,    2,    1,    1,    1,    1,    1,    1,    1,
          1,    1,    1,    3,    1,    1,    1,    1,    1,    2,
          1,    0,    4,    1,    1,    2,    2,    3,    1,    1,
          2,    2,    1,    1,    1,    1,    2,    1,    2,    4,
          1,    1,    3,    3,    4,    1,    1,    4,    6,    1,
          1,    1,    1,    1,    2,    1,    2,    1,    1,    1,
          1,
    };
  } /* End class YyLenClass */

  protected static final class YyDefRedClass {

    public static final short yyDefRed [] = {            0,
          4,    0,    0,   45,   46,    0,    0,    0,   71,   68,
         69,   70,    0,   25,   27,   28,   32,   34,    0,   62,
         63,   60,   61,    0,   15,   16,    8,    0,    0,    0,
         12,   14,   31,   26,    0,   18,   19,   20,   17,   21,
         22,   23,   37,   64,   66,    0,    0,    0,   49,   51,
         52,    0,    0,    0,    0,    0,    0,    0,   47,    2,
         11,   13,    6,    9,   30,   38,   65,   67,    0,   54,
         56,   57,    0,   53,    0,    0,   24,   39,   40,    0,
          0,    0,   50,   55,   58,    0,   33,   41,   42,    0,
          0,    0,   59,
    };
  } /* End of class YyDefRedClass */

  protected static final class YyDgotoClass {

    public static final short yyDgoto [] = {            24,
         46,   25,   73,   47,   48,   26,   27,   28,   29,   30,
         31,   32,   33,   34,   35,   36,   37,   38,   52,   81,
         82,   39,   40,   41,   42,   80,   58,
    };
  } /* End of class YyDgotoClass */

  protected static final class YySindexClass {

    public static final short yySindex [] = {          315,
          0,  -24, -254,    0,    0, -109, -246,  -36,    0,    0,
          0,    0, -242,    0,    0,    0,    0,    0, -239,    0,
          0,    0,    0, -244,    0,    0,    0,  372, -240,  411,
          0,    0,    0,    0,  -38,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,   -7,  -12,  -10,    0,    0,
          0,  -22,  -43,  -31,   -9, -222,  -19,  -95,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0, -219,    0,
          0,    0, -220,    0, -214,   -1,    0,    0,    0, -101,
        411,  411,    0,    0,    0, -212,    0,    0,    0, -240,
        411,    7,    0,
    };
  } /* End of class YySindexClass */

  protected static final class YyRindexClass {

    public static final short yyRindex [] = {            0,
          0,   56,  131,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,   27,   15,   30,
          0,    0,    0,    0,  189,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,   93,    1,   18,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,   33,
         32,    0,    0,
    };
  } /* End of class YyRindexClass */

  protected static final class YyGindexClass {

    public static final short yyGindex [] = {            0,
          0,  -52,   -5,    0,    0,    0,  -25,    0,  -29,  -32,
         25,    0,  -51,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,
    };
  } /* End of class YyGindexClass */

  protected static final class YyTableClass {

    public static final short yyTable [] = {            71,
         43,   72,   51,   56,   64,   78,   79,   49,   43,   19,
         53,   71,   54,   72,    1,   19,   57,   44,   44,   59,
         45,   60,   65,   87,   63,   66,   10,   88,   89,    5,
         67,    7,    3,   43,   68,   69,   76,   75,   77,   83,
         43,   43,   43,   84,   85,   86,   92,   93,   74,   91,
         44,   90,   62,    0,    0,   35,    0,   44,   44,   44,
         43,   43,   43,    0,    0,   64,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,   44,   44,   44,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,   43,   36,   43,    0,   35,   35,   35,    0,    0,
          0,    0,    0,    0,    0,   43,    0,    0,   44,    0,
         44,   43,    0,    0,   43,   35,   35,   35,    0,    0,
          0,    0,   44,   43,    0,   43,    0,    0,   44,    0,
         48,   44,   36,   36,   36,    0,    0,    0,    0,    0,
         44,    0,   44,    0,    0,    0,   35,   50,   35,    0,
          0,    0,   36,   36,   36,    2,    0,    0,    4,    5,
         35,    2,    0,    0,    4,    5,   35,    0,    0,   35,
         48,   48,   48,    0,    0,    0,    0,    0,   35,    0,
         35,    0,    0,   36,    0,   36,    0,    0,   29,    0,
         48,    0,   48,    0,    0,    0,    0,   36,    0,    0,
          0,    0,    0,   36,    0,    0,   36,    0,    0,    0,
          0,    0,    0,    0,    0,   36,    0,   36,    0,    0,
         70,   48,   55,   48,    0,    0,    0,    0,   29,   29,
         29,    0,    0,    0,    0,   48,    0,    0,    0,    0,
          0,   48,    0,    0,   48,    0,    0,    0,   29,    0,
         29,    0,    0,   48,    0,    0,    0,   43,   43,   43,
         43,   43,    0,    0,    0,   43,   43,   43,   43,   43,
         43,   43,   43,   43,   44,   44,   44,   44,   44,   29,
          1,   29,   44,   44,   44,   44,   44,   44,   44,   44,
         44,   10,   10,   29,    5,    5,    7,    7,    3,   29,
          0,    0,   29,    0,    0,    0,    0,    0,    0,    0,
          0,   29,   35,   35,   35,   35,   35,    0,    0,    0,
         35,   35,   35,   35,   35,   35,   35,   35,   35,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,   36,
         36,   36,   36,   36,   20,   21,   18,   36,   36,   36,
         36,   36,   36,   36,   36,   36,    0,    0,    0,    0,
          0,    0,    0,    0,   13,    0,   14,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,   48,   48,   48,
         48,   48,    0,    0,    0,   48,   48,   48,   48,   48,
         48,   48,   48,   48,    0,   22,    0,   23,    0,    0,
          0,   20,   21,   18,    0,    0,    0,    0,    0,   16,
          0,    0,    0,    0,    0,   19,    0,    0,   15,    0,
          0,   13,    0,   14,    0,    0,    0,   17,    0,    0,
          0,    0,    0,    0,    0,   29,   29,   29,   29,   29,
         20,   21,   18,   29,   29,   29,   29,   29,   29,   29,
         29,   29,   22,    0,   23,    0,    0,    0,    0,    0,
         13,    0,   14,    0,    0,    0,   16,    0,    0,    0,
          0,    0,   19,    0,    0,   15,    0,    0,    0,    0,
          0,    0,    0,    0,   17,    0,    0,    0,    0,    0,
          0,   22,    0,   23,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,   16,    0,    0,    0,    0,
          0,   19,    0,    0,   15,    0,    0,    0,    0,    0,
          0,    0,    0,   17,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          1,    2,    0,    3,    4,    5,    0,    0,    0,    0,
          0,    6,    7,    8,    9,   10,   11,   12,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    2,   61,
          3,    4,    5,    0,    0,    0,    0,    0,    6,    7,
          8,    9,   10,   11,   12,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
          0,    0,    0,    0,    0,    0,    0,    2,    0,    3,
          4,    5,    0,    0,    0,    0,    0,    6,    7,    8,
          9,   10,   11,   12,
    };
  } /* End of class YyTableClass */

  protected static final class YyCheckClass {

    public static final short yyCheck [] = {            43,
          0,   45,  112,   40,   30,   58,   58,  262,   33,  111,
        257,   43,  259,   45,    0,  111,  259,    0,   43,  259,
         45,  266,   61,  125,  265,   33,    0,   80,   80,    0,
         43,    0,    0,   33,   45,   58,  259,   47,   58,  259,
         40,   41,   42,  264,  259,   47,  259,   41,   54,   82,
         33,   81,   28,   -1,   -1,    0,   -1,   40,   41,   42,
         60,   61,   62,   -1,   -1,   91,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   60,   61,   62,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   91,    0,   93,   -1,   40,   41,   42,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,  105,   -1,   -1,   91,   -1,
         93,  111,   -1,   -1,  114,   60,   61,   62,   -1,   -1,
         -1,   -1,  105,  123,   -1,  125,   -1,   -1,  111,   -1,
          0,  114,   40,   41,   42,   -1,   -1,   -1,   -1,   -1,
        123,   -1,  125,   -1,   -1,   -1,   91,  257,   93,   -1,
         -1,   -1,   60,   61,   62,  257,   -1,   -1,  260,  261,
        105,  257,   -1,   -1,  260,  261,  111,   -1,   -1,  114,
         40,   41,   42,   -1,   -1,   -1,   -1,   -1,  123,   -1,
        125,   -1,   -1,   91,   -1,   93,   -1,   -1,    0,   -1,
         60,   -1,   62,   -1,   -1,   -1,   -1,  105,   -1,   -1,
         -1,   -1,   -1,  111,   -1,   -1,  114,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,  123,   -1,  125,   -1,   -1,
        264,   91,  259,   93,   -1,   -1,   -1,   -1,   40,   41,
         42,   -1,   -1,   -1,   -1,  105,   -1,   -1,   -1,   -1,
         -1,  111,   -1,   -1,  114,   -1,   -1,   -1,   60,   -1,
         62,   -1,   -1,  123,   -1,   -1,   -1,  257,  258,  259,
        260,  261,   -1,   -1,   -1,  265,  266,  267,  268,  269,
        270,  271,  272,  273,  257,  258,  259,  260,  261,   91,
        266,   93,  265,  266,  267,  268,  269,  270,  271,  272,
        273,  265,  266,  105,  265,  266,  265,  266,  266,  111,
         -1,   -1,  114,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,  123,  257,  258,  259,  260,  261,   -1,   -1,   -1,
        265,  266,  267,  268,  269,  270,  271,  272,  273,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,
        258,  259,  260,  261,   40,   41,   42,  265,  266,  267,
        268,  269,  270,  271,  272,  273,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   60,   -1,   62,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,  258,  259,
        260,  261,   -1,   -1,   -1,  265,  266,  267,  268,  269,
        270,  271,  272,  273,   -1,   91,   -1,   93,   -1,   -1,
         -1,   40,   41,   42,   -1,   -1,   -1,   -1,   -1,  105,
         -1,   -1,   -1,   -1,   -1,  111,   -1,   -1,  114,   -1,
         -1,   60,   -1,   62,   -1,   -1,   -1,  123,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,  257,  258,  259,  260,  261,
         40,   41,   42,  265,  266,  267,  268,  269,  270,  271,
        272,  273,   91,   -1,   93,   -1,   -1,   -1,   -1,   -1,
         60,   -1,   62,   -1,   -1,   -1,  105,   -1,   -1,   -1,
         -1,   -1,  111,   -1,   -1,  114,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,  123,   -1,   -1,   -1,   -1,   -1,
         -1,   91,   -1,   93,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,  105,   -1,   -1,   -1,   -1,
         -1,  111,   -1,   -1,  114,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,  123,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
        256,  257,   -1,  259,  260,  261,   -1,   -1,   -1,   -1,
         -1,  267,  268,  269,  270,  271,  272,  273,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,  258,
        259,  260,  261,   -1,   -1,   -1,   -1,   -1,  267,  268,
        269,  270,  271,  272,  273,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
         -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,   -1,  259,
        260,  261,   -1,   -1,   -1,   -1,   -1,  267,  268,  269,
        270,  271,  272,  273,
    };
  } /* End of class YyCheckClass */


  protected static final class YyRuleClass {

    public static final String yyRule [] = {
    "$accept : staves",
    "staves : staff",
    "$$1 :",
    "staves : staves STAFFSEP $$1 staff",
    "staves : error",
    "staff : measures",
    "$$2 :",
    "staff : staff VOICESEP $$2 measures",
    "measures : measure",
    "measures : measures measure",
    "measure : elements",
    "measure : elements BARLINE",
    "elements : element",
    "elements : elements element",
    "element : timed_event",
    "element : octave",
    "element : duration",
    "element : tuplet",
    "element : clef",
    "element : key_signature",
    "element : time_signature",
    "element : beam",
    "element : slur",
    "element : accent",
    "tuplet : '<' INTEGER ':'",
    "tuplet : '>'",
    "timed_event : chord",
    "timed_event : 'r'",
    "timed_event : 'i'",
    "chord : basic_chord",
    "chord : basic_chord '='",
    "basic_chord : pitch",
    "$$3 :",
    "basic_chord : '{' $$3 chord_body '}'",
    "basic_chord : '*'",
    "pitch : BASEPITCH",
    "pitch : BASEPITCH accidental",
    "pitch : BASEPITCH '!'",
    "pitch : BASEPITCH accidental '!'",
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
    "clef : CLEF clef_letter ':' INTEGER",
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
    null,null,null,null,null,null,null,"'!'",null,null,null,null,null,
    null,"'('","')'","'*'","'+'",null,"'-'",null,"'/'",null,null,null,
    null,null,null,null,null,null,null,"':'",null,"'<'","'='","'>'",null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,
    "'['",null,"']'",null,null,null,null,null,null,null,null,null,null,
    null,"'i'",null,null,null,null,null,"'o'","'p'",null,"'r'",null,null,
    null,null,null,null,null,null,"'{'",null,"'}'",null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,"BASEPITCH","BARLINE","INTEGER","AP_SEQ","COLON_SEQ","DOT_SEQ",
    "PRINT","MODE","VOICESEP","STAFFSEP","CLEF","KEYSIGNATURE",
    "TIMESIGNATURE","NOACCENT","STACCATO","MARCATO","PORTATO",
    };
  } /* End of class YyNameClass */


					// line 413 "Parser.y"
} // class Parser
					// line 1130 "-"
