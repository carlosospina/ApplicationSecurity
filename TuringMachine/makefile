# define a makefile variable for the java compiler
#
JCC = javac

# define a makefile variable for compilation flags
# the -g flag compiles with debugging information
#
JFLAGS = -d bin -classpath bin

# typing 'make' will invoke the first target entry in the makefile 
# (the default one in this case)
#
default: TuringMachine.class TMMain.class

# this target entry builds the Average class
# the Average.class file is dependent on the Average.java file
# and the rule associated with this entry gives the command to create it
#
TuringMachine.class:
	$(JCC) $(JFLAGS) src/edu/nyu/TuringMachine/TuringMachine.java

TMMain.class: 
	$(JCC) $(JFLAGS) src/edu/nyu/TuringMachine/TMMain.java

copyToBin:
	movetoBin src/edu/nyu/TuringMachine/*.class  bin/

# To start over from scratch, type 'make clean'.  
# Removes all .class files, so that the next make rebuilds them
#
clean: 
	$(RM) *.class