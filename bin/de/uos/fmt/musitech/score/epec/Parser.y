/* Parser source. Should be translated to Java with jay, the byacc port for Java. 
   (see: http://www.informatik.uni-osnabrueck.de/alumni/bernd/jay)
   $Id: Parser.y,v 1.2 2003/12/03 20:34:28 crogowsk Exp $ */
%{
package de.uos.fmt.musitech.gin;

import java.io.*;
import java.lang.*;
import java.util.*;
import de.uos.fmt.musitech.utility.*;

class ParserContext
{
   Integer    octave   = new Integer(0);
   Duration   duration = new Duration(1,4);
   Rational   attack   = Rational.ZERO;
   Measure    measure;
   Staff      staff;
   LocalSim   lsim;
   Chord      currentChord, prevChord;
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
    @version $Revision: 1.2 $, $Date: 2003/12/03 20:34:28 $ */
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
         System.out.println(">>> creating new measure");
         context.measure = new Measure();
      }
      else
         System.out.println(">>> using existing measure");
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
%}

%token<Character> BASEPITCH
%token<Barline>   BARLINE
%token<Integer>   INTEGER
%token<Integer>   AP_SEQ  COLON_SEQ  DOT_SEQ
%token<Object>    PRINT
%token<String>    MODE
%token            VOICESEP  STAFFSEP  CLEF  KEYSIGNATURE  TIMESIGNATURE
%token            NOACCENT STACCATO MARCATO PORTATO

%type<Integer>       accidental  octave  keysig_type  plus_seq  minus_seq
%type<Duration>      duration    
%type<SSystem>       staves
%type<Measure>       measure  elements
%type<Staff>         staff    measures    
%type<LocalSim>      element
%type<Event>         timed_event 
%type<Pitch>         pitch
%type<Chord>         chord basic_chord
%type<Clef>          clef
%type<KeySignature>  key_signature
%type<TimeSignature> time_signature
%type<Character>     clef_letter

%start staves

%%

staves   
   : staff                    {system.add($1);}
   | staves STAFFSEP          {nextStaff();}
     staff                    {system.add($4);}
   | error                    {}
   ;

staff    
   : measures                 {$$ = $1;}
   | staff VOICESEP           {context.voice++; 
                               context.attack.assign(0); 
                               context.measure = (Measure)context.staff.child(0);
                               System.out.println(">>> resetting attack time to 0");}
     measures                 {$$ = $4;}
   ;


measures 
   : measure                  {$$ = currentStaff(); if ($1.getScoreParent() == null) $<Staff>$.add($1);}
   | measures measure         {$$ = currentStaff(); if ($2.getScoreParent() == null) $<Staff>$.add($2);}
   ;


measure  
   : elements                 {$$ = $1;}
   | elements BARLINE         {$$ = $1;
                               $2.setParent($<Measure>$);
                               $<Measure>$.setRightBarline($2);
                               nextMeasure();}
   ; 


elements
   : element                  {if ($1 != null) {
                                  $$ = currentMeasure(); 
                                  if ($1.getScoreParent() == null) $<Measure>$.add($1);
                              }}
   | elements element         {if ($2 != null) {
                                  $$ = currentMeasure(); 
                                  if ($2.getScoreParent() == null) $<Measure>$.add($2);
                              }}
   ;

element
   : timed_event              {$$ = currentLocalSim();   
                               $<LocalSim>$.add($1);
                               if (context.beam != null)   {context.beam.add($1); $1.setInBeam(true);}
                               if (context.slur != null)   {context.slur.add($1);}
                               if (context.tuplet != null) {context.tuplet.add($1);}
                               if (context.accent != null) {$1.setAccent(context.accent);}
                               context.attack = context.attack.add(context.duration.toRational());
                               System.out.println(">>> attack time increased to " + context.attack);}
   | octave                   {context.octave = $1;   $$ = null;}
   | duration                 {context.duration = $1; $$ = null;}
   | tuplet                   {}
   | clef                     {currentMeasure().setClef($1); $$ = null;}
   | key_signature            {currentMeasure().setKeySignature($1); $$ = null;}
   | time_signature           {currentMeasure().setTimeSignature($1); $$ = null;}
   | beam                     {}
   | slur                     {}
   | accent                   {System.out.println(">>> Accent " + context.accent.getType());}
   ;
   

tuplet   
   : '<' INTEGER ':'          {context.tuplet = new Tuplet($2.intValue(), context.duration.toRational());}
   | '>'                      {currentStaff().addTuplet(context.tuplet); context.tuplet = null;}
   ;


timed_event
   : chord                    {$$ = $1;}
   | 'r'                      {$$ = new Rest(context.duration, context.voice);}
   | 'i'                      {System.out.println("NO INVISIBLE RESTS YET"); $$ = null;}
   ;


chord
   : basic_chord              {$$ = $1; 
                               if (context.prevChord instanceof TiedChord)
                                  ((TiedChord)context.prevChord).setSuccessor($1);
                              }
   | basic_chord '='          {$$ = context.currentChord = new TiedChord($1);
                               if (context.prevChord instanceof TiedChord)                                
                                  ((TiedChord)context.prevChord).setSuccessor($1);
                              }
   ;


basic_chord
   : pitch                    {context.prevChord = context.currentChord;
                               $$ = context.currentChord = new Chord(context.duration, context.voice); 
                               $<Chord>$.add($1);}
   | '{'                      {context.prevChord = context.currentChord;
                               $$ = context.currentChord = new Chord(context.duration, context.voice); 
                               contextStack.push(context);}
     chord_body
     '}'                      {context = (ParserContext)contextStack.pop(); $$ = $2;} 
   | '*'                      {System.out.println("REPEATER NOT IMPLEMENTED YET");}
   ;


pitch    
   : BASEPITCH                {$$ = new Pitch(context.duration, $1.charValue(), (byte)0, false, context.octave.byteValue());}
   | BASEPITCH accidental     {$$ = new Pitch(context.duration, $1.charValue(), $2.byteValue(), false, context.octave.byteValue());}
   | BASEPITCH '!'            {$$ = new Pitch(context.duration, $1.charValue(), (byte)0, true, context.octave.byteValue());}
   | BASEPITCH accidental '!' {$$ = new Pitch(context.duration, $1.charValue(), $2.byteValue(), true, context.octave.byteValue());}
   ;
           
chord_body 
   : octave                   {context.octave = $1;}
   | pitch                    {context.currentChord.add($1);}
   | chord_body octave        {context.octave = $2;}
   | chord_body pitch         {context.currentChord.add($2);}
   ;
           
accidental 
   : plus_seq                 {$$ = $1;}
   | minus_seq                {$$ = $1;}
   ;

octave     
   : AP_SEQ                   {$$ = $1;}
   | COLON_SEQ                {$$ = $1;}
   | 'o' INTEGER              {$$ = new Integer($2.intValue() - 4);}
   ;


duration   
   : INTEGER                  {$$ = new Duration(new Rational(1,$1.intValue()), 0);
                               if (context.tuplet != null) $$ = new IrregularDuration(context.tuplet, $<Duration>$);}
   | INTEGER DOT_SEQ          {$$ = new Duration(new Rational(1,$1.intValue()), $2.intValue());
                               if (context.tuplet != null) $$ = new IrregularDuration(context.tuplet, $<Duration>$);}
   ;

clef
   : CLEF clef_letter  ':' INTEGER    {$$ = new Clef($2.charValue(), $4.intValue());}
   ;

clef_letter   
   : BASEPITCH                {$$ = $1;}
   | 'p'                      {$$ = new Character('p');}
   ;

key_signature 
   : KEYSIGNATURE INTEGER keysig_type         {$$ = new KeySignature($2.intValue()*$3.intValue());}
	| KEYSIGNATURE BASEPITCH MODE              {$$ = new KeySignature($2.charValue(), 0, $3);}
	| KEYSIGNATURE BASEPITCH keysig_type MODE  {$$ = new KeySignature($2.charValue(), $3.intValue(), $4);}
   ;

keysig_type   
   : '+'                      {$$ = new Integer(1);}
   | '-'                      {$$ = new Integer(-1);}
   ;

time_signature
   : TIMESIGNATURE INTEGER '/' INTEGER         {$$ = new TimeSignature($2.intValue(), $4.intValue(), false);}
   | TIMESIGNATURE '(' INTEGER '/' INTEGER ')' {$$ = new TimeSignature($3.intValue(), $5.intValue(), true);}
   ;

beam          
   : '['                      {if (context.beam == null) context.beam = new Beam();}
   | ']'                      {system.addBeam(context.beam); context.beam = null;}
   ;
              
slur          
   : '('                      {if (context.slur == null) context.slur = new Slur();}
   | ')'                      {system.addSlur(context.slur); context.slur = null;}              
   ;

plus_seq      
   : '+'                      {$$ = new Integer(1);}
   | plus_seq '+'             {$$ = new Integer($<Integer>$.intValue()+1);}
   ;

minus_seq     
   : '-'                      {$$ = new Integer(-1);}
   | minus_seq '-'            {$$ = new Integer($<Integer>$.intValue()-1);}
   ;

// @@ funktioniert noch nicht richtig: für jeden Akkord muß ein neuer Akzent konstruiert werden
accent        
   : STACCATO                 {context.accent = new Accent(Accent.STACCATO);}
   | MARCATO                  {context.accent = new Accent(Accent.MARCATO);}
   | PORTATO                  {context.accent = new Accent(Accent.PORTATO);}
   | NOACCENT                 {context.accent = null;}
   ;             
%%
} // class Parser
