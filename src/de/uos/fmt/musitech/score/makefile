#
# Makefile to build the lexer and parser class files (MG)
# $Id: Makefile,v 1.1 2003/02/06 23:06:07 mgieseki Exp $
#

# If 'make' doesn't work correctly you probably have to install
# missing tools like jay or jflex. You could also have to modify
# the following path definitions.

SEP       = /
JAVAHOME  = /usr/local/j2sdk1.4.1_01
JFLEXPATH = /usr/local/JFlex
JAYPATH   = /usr/local

CLASSPATH = "../../../../..:$(JAVAHOME)/jre/lib/rt.jar"


# you should not need to change anything below this comment
# (unless you prefer using javac instead of jikes)

JAVAC = javac
JFLEX = $(JFLEXPATH)$(SEP)bin$(SEP)jflex 
JAY   = $(JAYPATH)$(SEP)bin$(SEP)jay


all: Parser.class Lexer.class
	
Lexer.class: Lexer.java Parser.java

Parser.class: Parser.java Lexer.java
   
Lexer.java: Lexer.l 
	$(JFLEX) --nobak --skel "$(JFLEXPATH)$(SEP)src$(SEP)skeleton.nested" $<
	
Parser.java: Parser.y
	$(JAY) -t -v <$(JAYPATH)$(SEP)skeleton $< >$@


.SUFFIXES: .java .class

.java.class:
	$(JAVAC) -source 1.4 -classpath $(CLASSPATH) $<


.PHONY: clean 

clean:
	rm -f Lexer.java Lexer.class
	rm -f Parser.java Parser.class

