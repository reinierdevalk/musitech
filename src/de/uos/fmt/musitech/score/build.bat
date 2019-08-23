@echo off
rem build script for the gin lexer and parser classes 
rem we now use jflex instead of jlex because of better features
rem $Id: build.bat,v 1.6 2003/01/28 13:40:05 mgieseki Exp $

rem maybe you have to adapt the following 3 lines 
set classpath=..\..\..\..\..;c:\jdk1.4\jre\lib\rt.jar
set jaypath=c:\develop\java\jay
set jflexpath=c:\develop\java\jflex
echo ------------------------------------------------------------------------------
echo BUILDING LEXER
rem java JLex.Main Lexer.lex
call %jflexpath%\bin\jflex --nobak --skel %jflexpath%\src\skeleton.nested Lexer.lex
echo ------------------------------------------------------------------------------
echo BUILDING PARSER
if exist \tmp goto tmpok
	md \tmp
	echo ----- directory \tmp was created for jay -----
:tmpok
%jaypath%\code\jay\jay -t -v <%jaypath%\code\jay\skeleton Parser.y >Parser.java
set jaypath=
rem del Lexer.java >nul                 NEEDED FOR JLEX  
rem ren Lexer.lex.java Lexer.java >nul  NEEDED FOR JLEX
echo ------------------------------------------------------------------------------
echo COMPILING LEXER
jikes -source 1.4 Lexer.java
echo ------------------------------------------------------------------------------
echo COMPILING PARSER
jikes -source 1.4 Parser.java
:end
