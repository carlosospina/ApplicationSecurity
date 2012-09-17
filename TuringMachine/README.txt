This is the Turing Machine Simulator by Carlos Ospina
 
To Make the project:
1) Uncompress the zip file to a folder
2) go to folder TuringMachine
3) run: make

To run the Project:
run: java -cp bin TMMain <path to program file> <path to initial tape file>

To get usage help:
run: java -cp bin TMMain help

Programs:
The programs are inside the "programs" folder
1) Fibonacci, run: java -jar TuringMachine.java programs\fibonacci.txt programs\fibonacciTape.txt
2) Count 10 to 1, run: java -jar TuringMachine.java programs\count10to1.txt programs\count10to1Tape.txt