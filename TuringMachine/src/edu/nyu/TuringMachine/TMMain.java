package edu.nyu.TuringMachine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class Starts a Turing machine.
 * Run TMMain help to get print of usage 
 * 
 * @author carlos
 *
 */
public class TMMain {
	/**
	 * Maximum program size
	 */
	private static int maxProgramSize=1024;
	
	/**
	 * Maximum tape size
	 */
	private static int maxTapeSize=2048;
	
	/**
	 * The entry point to the program
	 * @param args
	 * 		args0 = Program file
	 * 		args1 = Tape file
	 */
	public static void main(String[] args) {
		//Check for input values
		if(args.length<2 || args[0]=="help"){
			printInstructions();
			return;			
		}else if(args[0]==null || args[1]==null){
			printInstructions();
			return;
		}
		
		HashMap<String, String> program = readProgram( args[0] );
		StringBuffer tape = readTape( args[1] );
		
		TuringMachine tm=new TuringMachine(maxProgramSize,maxTapeSize,false);
		tm.runTuringMachine( program, tape );
	}

	/**
	 * This program reads the tape initialization file
	 * and returns the tape initialized
	 * @param string
	 * @return The tape represented by a SpringBuffer
	 */
	private static StringBuffer readTape(String tapePath) {
		FileReader tapeFile;
		StringBuffer tape=new StringBuffer();
		try{
			tapeFile=new FileReader(tapePath);
			BufferedReader in=new BufferedReader(tapeFile);
			String line;
			while( (line = in.readLine() ) != null ) {
				//Split comments from line
				String[] lineArrTemp=line.split("#");
				//Trim the line, to remove trailing spaces 
				line=lineArrTemp[0].trim();
				//Check The line has a command.
				if(line.length()!=0){
					String tapeStr=validateTape(line);
					//Remove carry return
					tapeStr.replace("\n", "");
					//Replace spaces with B (blanks)
					tapeStr.replace(" ", "B");				
					//Append to the tape
					tape.append(tapeStr);
					if(tape.length()>maxTapeSize){
						in.close();
						throw new RuntimeException("The tape is larger than the Machine limit: "+maxTapeSize+ "characters");
					}
				}
			}
			//Make sure the tape is initialized:
			if(tape.length()==0){
				tape.append("B");
			}			
			in.close();
		}catch (IOException e) {
			throw new RuntimeException("The tape file can't be found: "+new File(tapePath).getAbsolutePath());
		}
		return tape;
	}

	/**
	 * This method parses the program file to a simple HasMap.
	 * The HashMap definition is:
	 * 		- Key: Its a string of <Current State>,<Current Value>
	 * 		- Value: Its a string of <New State, New Value, Action> 
	 * @param programPath
	 * @return The program represented in a HashMap as described
	 */
	private static HashMap<String, String> readProgram(String programPath) {
		FileReader programFile;
		HashMap<String,String> programHM=new HashMap<String,String>();
		BufferedReader in=null;
		try {
			programFile = new FileReader(programPath);
			in=new BufferedReader(programFile);
			String line;
			int lineNumber=1;
			while( (line = in.readLine() ) != null ) {
				//Split comments from command
				String[] lineArrTemp=line.split("#");
				//Trim the command, to remove trailing spaces 
				String command=lineArrTemp[0].trim();
				//Check The line has a command.
				if(command.length()!=0){
					String[] lineArr=validateCommandLine(command, lineNumber);
					//Define the key
					StringBuffer key=new StringBuffer()	;
					key.append(lineArr[0]).append(",").append(lineArr[1]);
					//Define the Value
					StringBuffer value=new StringBuffer()	;
					value.append(lineArr[2]).append(",");
					value.append(lineArr[3]).append(",");
					value.append(lineArr[4]);
					//Check the key is not repeated
					if(programHM.get(key.toString())==null){
						programHM.put(key.toString(), value.toString());
					}else{
						throw new RuntimeException("There's a duplicated state on the program at line "+lineNumber+". The key is:"+key.toString());						
					}
					if(programHM.size()>maxProgramSize){
						throw new RuntimeException("The program is larger than the Machine limit: "+maxProgramSize+ "lines");
					}
				}
				lineNumber++;
			}
		} catch (IOException e) {
			throw new RuntimeException("The program file can't be found: "+new File(programPath).getAbsolutePath());
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException("Error closing the stream: "+new File(programPath).getAbsolutePath());
			}
		}
		return programHM;
	}

	
	/**
	 * This method verifies that a command line in the program file
	 * complies with the rules.
	 * On failure and unhandled exception is thrown braking the execution of the 
	 * TuringMachine
	 * @param line is the command line to validate
	 * @return An a array of the different elements on the command line
	 */
	private static String[] validateCommandLine(String line, int lineNumber){
		//Split the command.
		String[] lineArr=line.split(",");
		
		//Check the number of elements on the command line
		if(lineArr.length!=5){
			throw new RuntimeException("Line "+lineNumber+". Malformed command line. Wrong number of elements: "+line);
		}
		
		//Check that none of the elements is null
		for(int i=0;i<lineArr.length;i++){
			if(lineArr[i]==null){
				throw new RuntimeException("Line "+lineNumber+". Malformed command line. Null value in command line: "+line);
			}
		}
		//Check the value of the State column. It should be alphanumeric, 
		//and not empty
		if(!lineArr[0].matches("[0-9]+")){
			throw new RuntimeException("Line "+lineNumber+". Malformed command line. Wrong State: "+line);
		}
		//Check the value of the Current Value column. It should be 0,1 or B
		//Only one character and not empty
		if(!lineArr[1].matches("^[_0-9A-Za-z]$")){
			throw new RuntimeException("Line "+lineNumber+". Malformed command line. Wrong Current Value: "+line);
		}
		//Check the value of the New State column. It should be alphanumeric, 
		//and not empty
		if(!lineArr[2].matches("[0-9]+")){
			throw new RuntimeException("Line "+lineNumber+". Malformed command line. Wrong New State Value: "+line);
		}
		//Check the value of the New Value column. It should be 0,1 or B
		//Only one character and not empty
		if(!lineArr[3].matches("^[_0-9A-Za-z]$")){
			throw new RuntimeException("Line "+lineNumber+". Malformed command line. Wrong New Value Value: "+line);
		}
		//Check the value of the Action column. It should be R, L or H
		//Only one character and not empty
		if(!lineArr[4].matches("^[RLHPID]$")){
			throw new RuntimeException("Line "+lineNumber+". Malformed command line. Action invalid: "+line);
		}
		return lineArr;
	}

	/**
	 * This method validates the tape to make sure it only contains data values
	 * 1, 0 or B. On Error, an unhandled exception will be thrown to stop the
	 * TuringMachine execution
	 * @param line the Line representing the tap
	 * @return The line if it's successful
	 */
	private static String validateTape(String line){
		//Check the values on the String. They should be a sequence of 0,1 or _
		//and not empty
		if(!line.matches("^[_0-9a-zA-Z]+$")){
			throw new RuntimeException("Malformed tape. Wrong Values: "+line);
		}		
		return line;
	}

	private static void printInstructions(){
		StringBuffer welcomeMsg=new StringBuffer();
		welcomeMsg.append("Welcome to the TuringMachine by Carlos Ospina\n");
		welcomeMsg.append("Usage: java TMMain.class <program file> <tape file>\n");		
		welcomeMsg.append("\nProgram File: is a text file that contains one command line per line\n");
		welcomeMsg.append("\nCommand line: is a comma separated line where the columns are:\n" +
				 "\tColumn 1: Current State. It can only be a positive number and the initial state is always 0\n"+ 
				 "\tColumn 2: Value under the head. It can be one alphanumeric charactater (case insensitive) or '_' for blank\n"+
				 "\tColumn 3: New State. It can only be a positive number\n"+
				 "\tColumn 4: New Value under the head. It can be one alphanumeric charactater (case insensitive) or '_' for blank\n"+
				 "\tColumn 5: Action. It can be one of these three values:\n"+
				 "\t\tR: Move the head right\n"+
				 "\t\tL: Move the head left\n"+
				 "\t\tH: Stop\n" +
				 "\t\tP: Print to output\n" +
				 "\t\tI: Insert a blank space left to current position\n" +
				 "\t\tI: Delete (Remove) space under the head\n" +
				 "\tComments are allowed by using the '#' character\n"+
				 "\texample: 0,1,1,1,R\t# Some comment\n");
		welcomeMsg.append("\nTape File: Is a file with the initial values of the tape, \n" +
				 "Each value can be any alphanumeric character (case insensitive) or '_' for blank");
		welcomeMsg.append("\nAbout Limits:\n" +
				"->The machine has a máximum program size that can be run of %d lines.\n" +
				"->The machine has a máximum input tape lenght of %d places (bytes)");
		
		String welcomeStr=welcomeMsg.toString();
		welcomeStr=String.format(welcomeStr,maxProgramSize,maxTapeSize);
		System.out.println(welcomeMsg);
	}
}
