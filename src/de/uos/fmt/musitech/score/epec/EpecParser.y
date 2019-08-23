/* Parser source. Should be translated to Java with jay, the byacc port for Java. 
   (see: http://www.informatik.uni-osnabrueck.de/alumni/bernd/jay)
   $Id: Parser.y,v 1.1 2003/02/06 23:08:34 mgieseki Exp $ */
%{
package de.uos.fmt.musitech.music;

import java.io.*;
import java.util.*;
import de.uos.fmt.musitech.utility.*;
import de.uos.fmt.musitech.music.SlurContainer;
import de.uos.fmt.musitech.music.BeamContainer;

class EpecParserContext
{
   Integer    octave   = new Integer(0);
   Rational   duration = new Rational(1,4);
   Rational   attack   = Rational.ZERO;
   NotationStaff      staff;
   NotationVoice currentVoice;
   NotationChord chord;
   NotationChord currentChord, prevChord;
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
	 <b>clef: </b> C (8|-8)? {c | f | g | p} (:-?[0-9])?<br> 
	 The lower case letters select the clef type (p means 'percussion').	 
	 You can supply 8 or -8 between "C" and the clef type to denote an octave shift
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
    @version $Revision: 1.1 $, $Date: 2003/02/06 23:08:34 $ */
class EpecParser
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
		context.staff = new NotationStaff(system.getContext(), system);
		context.currentVoice = new NotationVoice(system.getContext(), context.staff);
	
		if (system.getContext().getWork().getMeterTrack() == null) {
			system.getContext().getWork().setMeterTrack(new MetricalTimeLine(system.getContext()));
		}
		context.meterTrack = system.getContext().getWork().getMeterTrack();
		
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
         context.staff = new NotationStaff(system.getContext(), system);
      }
      return context.staff;
   }
   
   NotationVoice getCurrentVoice () {
		if (context.currentVoice == null) {
			context.currentVoice = new NotationVoice(system.getContext(), context.staff);
		}
		return context.currentVoice;
   }
   

   NotationStaff nextStaff ()
   {
      context.staff = new NotationStaff(system.getContext(), system);
      context.attack.assign(0);
      context.chord = null;
      context.currentChord = context.prevChord = null;
      context.pitch = null;

      nextVoice(); //new staff implies new voice

      return context.staff;
   }
   
   NotationVoice nextVoice() {
		context.currentVoice = new NotationVoice(system.getContext(), context.staff);
		context.attack.assign(0);
		context.chord = null;
		context.currentChord = context.prevChord = null;
		context.pitch = null;

		return context.currentVoice;
   }

%}

%token<Character> BASEPITCH
%token<Barline>   BARLINE
%token<Integer>   INTEGER
%token<Integer>   AP_SEQ  COLON_SEQ  DOT_SEQ
%token<Object>    PRINT
%token<String>    MODE
%token            VOICESEP  STAFFSEP  CLEF  KEYSIGNATURE  TIMESIGNATURE LINEBREAK
%token            NOACCENT STACCATO MARCATO PORTATO

%type<Integer>       accidental  octave  keysig_type  plus_seq  minus_seq
%type<Rational>      duration tuplet
%type<NotationSystem>       staves
%type<NotationStaff>         staff
%type<NotationChord>      element timed_event
%type<NotationVoice> elements
%type<Note>         pitch
%type<Vector>       pitches
%type<NotationChord>         chord basic_chord
%type<Clef>          clef
%type<KeySignature>  key_signature
%type<TimeSignature> time_signature
%type<Character>     clef_letter

%start staves

%%

staves   
   : staff                    {system.add($1); }
   | staves STAFFSEP          {nextStaff();}
     staff                    {system.add($4); }
   | error                    {}
   ;

staff    
   : elements                 {$$ = getCurrentStaff(); $<NotationStaff>$.add($1);}
   | staff VOICESEP           {nextVoice();}
     elements                 {$$ = getCurrentStaff(); 
                               $<NotationStaff>$.add($4);}
   ;

elements
   : element                  {$$ = getCurrentVoice(); $<NotationVoice>$.add($1);}
   | elements                 {}
     element                  {$$ = getCurrentVoice(); $<NotationVoice>$.add($3);}
   | elements BARLINE         {system.addBarline(new Barline(context.attack));}
     element                  {$$ = getCurrentVoice(); $<NotationVoice>$.add($4);}
   ;

element
   : timed_event LINEBREAK    {$$ = $1; context.attack = context.attack.add(context.duration);
                               context.linebreaks.add(context.attack);}
   | timed_event              {$$ = $1; context.attack = context.attack.add(context.duration);}
   | octave                   {context.octave = $1;   $$ = null;}
   | duration                 {context.duration = $1; $$ = null;}
   | tuplet LINEBREAK         {context.attack = context.attack.add($1); $$ = null;
                               context.linebreaks.add(context.attack);}
   | tuplet                   {context.attack = context.attack.add($1); $$ = null;}
   | clef                     {$$ = null;}
   | key_signature            {$$ = null;}
   | time_signature           {$$ = null;}
   | beam                     {}
   | slur                     {}
   | accent                   {}
   | beam LINEBREAK           {context.linebreaks.add(context.attack);}
   | slur LINEBREAK           {context.linebreaks.add(context.attack);}
   ;
   

tuplet
   : '<' INTEGER ':' pitches '>' {Rational tupletDuration = new Rational(1, $2.intValue());
                                  getCurrentVoice().addTuplet((Note[])$4.toArray(new Note[]{}), tupletDuration);
                                  context.pitches = null;
                                  $$ = tupletDuration;}
   ;


timed_event
   : chord                    {$$ = $1;}
   | 'r'                      {$$ = new NotationChord(system.getContext(), new Note(new ScoreNote(context.attack, context.duration, 'r', (byte)0, (byte)0), null));}
   | 'i'                      {System.out.println("NO INVISIBLE RESTS YET"); $$ = null;}
   ;


chord
   : basic_chord              {$$ = $1;
                               if (context.beam != null) context.beam.add($1);}
   | basic_chord '='          {$$ = $1;}
   ;


basic_chord
   : pitch                    {context.prevChord = context.currentChord;
                               $$ = context.currentChord = new NotationChord(system.getContext());
                               $<NotationChord>$.add($1);}
   | '{'                      {context.prevChord = context.currentChord;
                               $$ = context.currentChord = new NotationChord(system.getContext()); 
                               contextStack.push(context);}
     chord_body
     '}'                      {context = (EpecParserContext)contextStack.pop(); $$ = $2;} 
   | '*'                      {System.out.println("REPEATER NOT IMPLEMENTED YET");}
   ;


pitches                       
   : pitch                    {if (context.pitches == null) context.pitches = new Vector(); $$ = context.pitches;
                               $<Vector>$.add($1);}
   | pitches pitch            {if (context.pitches == null) context.pitches = new Vector(); $$ = context.pitches;
                               $<Vector>$.add($2);}
   ;

pitch    
   : BASEPITCH                {ScoreNote sn = new ScoreNote(context.attack, context.duration, $1.charValue(), context.octave.byteValue(), (byte)0);
   								Note n = new Note(sn, null);
                               if (context.slur != null) context.slur.add(n);
			   			       $$ = n;}
   | BASEPITCH accidental     {ScoreNote sn = new ScoreNote(context.attack, context.duration, $1.charValue(), context.octave.byteValue(), $2.byteValue());
   								Note n = new Note(sn, null);
                               if (context.slur != null) context.slur.add(n);
			   			       $$ = n;}
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
   : INTEGER                  {$$ = new Rational(1,$1.intValue());}
   | INTEGER DOT_SEQ          {$$ = new Rational(2); 
                               $$ = $<Rational>$.sub(1, 1 << $2.intValue());
                               $$ = $<Rational>$.mul(new Rational(1,$1.intValue()));}
   ;

clef
   : CLEF clef_letter {getCurrentStaff().setClefType($2.charValue());}
   | CLEF INTEGER clef_letter {getCurrentStaff().setClefType($3.charValue(), $2.intValue()); }
   | CLEF clef_letter  ':' INTEGER    {getCurrentStaff().setClefType($2.charValue(), $4.intValue(), 0);}
   | CLEF INTEGER clef_letter ':' INTEGER {getCurrentStaff().setClefType($3.charValue(), $5.intValue(), $2.intValue());}
   ;

clef_letter
   : BASEPITCH                {$$ = $1;}
   | 'p'                      {$$ = new Character('p');}
   ;

key_signature 
   : KEYSIGNATURE INTEGER keysig_type         { KeyMarker marker = new KeyMarker(context.attack, 0L);
						                        marker.setAccidentalNum($2.intValue() * $3.intValue());
   												context.meterTrack.add(marker);}
	| KEYSIGNATURE BASEPITCH MODE              {}
	| KEYSIGNATURE BASEPITCH keysig_type MODE  {}
   ;

keysig_type   
   : '+'                      {$$ = new Integer(1);}
   | '-'                      {$$ = new Integer(-1);}
   ;

time_signature
: TIMESIGNATURE INTEGER '/' INTEGER         {
  context.meterTrack.add(new TimeSignatureMarker($2.intValue(), $4.intValue(), context.attack));
}
| TIMESIGNATURE '(' INTEGER '/' INTEGER ')' {}
;

beam          
   : '['                      {context.beam = new BeamContainer(system.getContext());}
   | ']'                      {getCurrentVoice().addBeamContainer(context.beam); context.beam = null;}
   ;
              
slur          
   : '('                      {context.slur = new SlurContainer(system.getContext());}
   | ')'                      {getCurrentVoice().addSlurContainer(context.slur); context.slur = null;}              
   ;

plus_seq      
   : '+'                      {$$ = new Integer(1);}
   | plus_seq '+'             {$$ = new Integer($<Integer>$.intValue()+1);}
   ;

minus_seq     
   : '-'                      {$$ = new Integer(-1);}
   | minus_seq '-'            {$$ = new Integer($<Integer>$.intValue()-1);}
   ;

// @@ funktioniert noch nicht richtig: f?r jeden Akkord mu? ein neuer Akzent konstruiert werden
accent        
   : STACCATO                 {}
   | MARCATO                  {}
   | PORTATO                  {}
   | NOACCENT                 {}
   ;             
%%
} // class Parser
