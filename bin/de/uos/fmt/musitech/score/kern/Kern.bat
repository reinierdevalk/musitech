REM This batch file will create the kern lexer and parser class with the help of 
REM the Kern.g grammar.
REM    KernLexer.java
REM    KernParser.java
REM    KernParserTokenTypes.java
REM    KernParserTokenTypes.txt
REM
REM Note: Add antlr.jar to your CLASSPATH.
java antlr.Tool Kern.g
sleep 10
exit