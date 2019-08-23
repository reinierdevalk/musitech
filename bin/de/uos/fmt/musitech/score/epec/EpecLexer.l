// This file must be translated with JFlex (http://www.jflex.de) 
// use option --skel skeleton.nested

package de.uos.fmt.musitech.music;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/** This class encapsulates the lexical analyzer that collects the tokens 
    from the input language (Plaine and Easy Code).
    @author Martin Gieseking
    @version $Revision: 1.1 $, $Date: 2003/02/06 23:06:07 $ */
%%

%class EpecLexer
%implements EpecParser.yyInput
%integer

%eofval{
   return YYEOF;
%eofval}
%eofclose

%8bit   // enable 8-bit character support
%line   // enable line counting (line number stored in yyline, 1st line = 0)
%column // enable column counting (column number stored in yycolumn, 1st char = 0)


%{
   private int    token;      
   private Object value;        // semantic value of recognized token
   //private int    commentCount = 0;
   private Stack  includeStack = new Stack(); // stores the included filenames
   
   public boolean advance () throws java.io.IOException 
   {
      token = yylex();
      return token != YYEOF; // gets true if reached end of input stream
   }

   public int token ()    {return token;}   
   public Object value () {return value;}    
   public int line ()     {return yyline+1;}
   public int column ()   {return yycolumn+1;}

   public void printErrorMessage (String message)
   {
      System.out.println("error at line "+line()+", col "+column()+": "+message);
   }

   public void printWarningMessage (String message)
   {
      System.out.println("warning at line "+line()+", col "+column()+": "+message);
   }
   
   // @@ todo: first file (if available) must be pushed on the include stack     
   private void includeFile (String fname)
   {
//      System.out.println("trying to include file "+fname);
      boolean ok = true;
      Iterator it = includeStack.iterator();
      while (it.hasNext() && ok)
         ok = !((String)it.next()).equals(fname);
      if (!ok)
         printWarningMessage("file \"" + fname + "\" already included (skipping)");
      else
      {
         try {
            yypushStream(new FileReader(fname));         
            includeStack.push(fname);
         }
         catch (IOException e) {         
            printErrorMessage("file \"" + fname + "\" not found");
         }
      }
   }

   private boolean leaveIncludeFile ()
   {
      if (!yymoreStreams())
         return false;
      try {
         yypopStream();
         includeStack.pop();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      return true;
   }

   private String cutQuotes (String str)
   {      
      return str.substring(1, str.lastIndexOf('"'));   
   }
   
%}


%state comment
%state include

BLANK  = (\r\n)|(\x0c)|([ \r\n\t])

%%

<include>{
   \"[^ \t\n\"]+\"  {includeFile(cutQuotes(yytext())); yybegin(YYINITIAL);}
   \n               {printErrorMessage("include filename expected");}
}

<<EOF>>      {if (!leaveIncludeFile()) return YYEOF;}

<comment>{   // handle char sequences inside a multi-line comment   
   "*/"      {yybegin(YYINITIAL);}
   "/*"      {}
   <<EOF>>   {printErrorMessage("missing '*/' at end of file"); yybegin(YYINITIAL);}
   \*+[^/]   {} // ignore char sequences like *******x where x != / 
   \/+[^*]   {} // ignore char sequences like ///////x where x != * 
   [^/*]+    {} // ignore any character sequence not containing '/' or '*' 
}

{BLANK}+     {}
[a-h]        {value = new Character(yytext().charAt(0)); return EpecParser.BASEPITCH;}
-?[0-9]+     {value = new Integer(yytext()); return EpecParser.INTEGER;}
"//".*       {}  
"/*"         {yybegin(comment);}
"*/"         {printWarningMessage("no corresponding /* found (skipping)");}
#include     {yybegin(include);}
exit         {value = null; return YYEOF;}
print        {value = null; return EpecParser.PRINT;}
major        {value = "major"; return EpecParser.MODE;}
minor        {value = "minor"; return EpecParser.MODE;}
stacc        {return EpecParser.STACCATO;}
marc         {return EpecParser.MARCATO;}
port         {return EpecParser.PORTATO;}
noacc        {return EpecParser.NOACCENT;}
C            {value = null; return EpecParser.CLEF;}
K            {value = null; return EpecParser.KEYSIGNATURE;}
T            {value = null; return EpecParser.TIMESIGNATURE;}
"'"+         {value = new Integer(yytext().length()-1);  return EpecParser.AP_SEQ;}
","+         {value = new Integer(-yytext().length());  return EpecParser.COLON_SEQ;}
"."+         {value = new Integer(yytext().length());  return EpecParser.DOT_SEQ;}
";;"         {value = null; return EpecParser.STAFFSEP;}
";"          {value = null; return EpecParser.VOICESEP;}
"|"          {value = null; return EpecParser.BARLINE;}
"||"         {value = null; return EpecParser.BARLINE;}
"linebreak"  {value = null; return EpecParser.LINEBREAK;}
.            {value = null; return yytext().charAt(0);}
