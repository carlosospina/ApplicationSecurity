package edu.nyu.TuringMachine;
import java.util.HashMap;

/**
 * This class defines the behavior of the Turing machine
 * It requires:
 * 		1) A program: Its defined by a series of command lines (See bellow),
 * 			and provided to the machine using a text file
 * 		2) A Tape: Its a file that contains the initial state of the tape, 
 * 			before running the program 
 * 
 * About the Command Line:
 * A command line is defined by a comma separated line with five elements:
 * 		Column 1: Current State. It can only be a number
 * 		Column 2: Value under the head. It can be one of these values: 1,0 or B for blank
 * 		Column 3: New State. It can only be a number
 * 		Column 4: New Value under the head. It can be one of these values: 1,0 or B for blank
 * 		Column 5: Action. It can be one of these three values:
 * 					- R: Move the head right
 * 					- L: Move the head left
 * 					- H: Stop
 * 					- P: Print to output
 * 		example: 0,1,1,1,R
 * 
 * About the Tape:
 * The initial tape state is provided at the beginning of the program using a text file.
 * 
 * @author carlos
 *
 */
public class TuringMachine {
	/**
	 * Maximum program size
	 */
	private int maxProgramSize=1024;
	
	/**
	 * Maximum tape size
	 */
	private int maxTapeSize=2048;
	
	/**
	 * Debug switch. True=do debug printing
	 */
	private boolean debug=false;
	
	public TuringMachine(int maxProgramSize, int maxTapeSize, boolean debug) {
		this.maxProgramSize=maxProgramSize;
		this.maxTapeSize=maxTapeSize;
		this.debug=debug;
	}

	/**
	 * Run the Turing Machine according to the specification
	 * It is assumed that the parameters have been verified before calling this machine
	 * @param program 
	 * 	 	A HashMap which definition is:
	 * 		- Key: Its a string of <Current State>,<Current Value>
	 * 		- Value: Its a string with <New State, New Value, Action> values
	 * @param tape
	 * 		The tape where the program is to be executed. It is a continues line. No
	 * 		line feeds
	 */
	protected void runTuringMachine(HashMap<String, String> program,
			StringBuffer tape) {
		//Check the size of the program
		if(program.size()==0){
			throw new RuntimeException("Nothing to execute. Program size is 0");
		}
		
		//Program Aux Variables
		boolean halt=false;
		int stepNumber=0;
		
		//The machine starts at state 0
		String currentState="0";
		String currentValue;
		int headPosition=0;
		String key=null;
		String command;
		
		//Print the Tape at the begining:
		printTape("INPUT AT START",-1,"-,-",-1,tape.toString());
		//System.out.println("PROCESS:");		
		while(!halt){
			//Create space if necessary
			if(headPosition==0){
				//Create 3 blank spaces to the left
				tape.insert(0, "___");
				//Correct headPosition
				headPosition=3;
			}else if(headPosition==tape.length()-1){
				//Create 3 blank spaces to the left
				tape.append("___");					
			}
						
			//get current head value
			currentValue=tape.charAt(headPosition)+"";
			//build the <Current State>,<Current Value> key
			key=currentState+","+currentValue;

			//Print the Tape at the state
			if(debug){
				printTape("DEBUG",stepNumber,key,headPosition,tape.toString());
			}
			
			//Get the command for the key
			command=program.get(key);
			if(command==null){
				throw new RuntimeException("There's no command defined for <state>,<value>="+key);
			}
			//Convert all values to UpperCase to simplify comparisons
			command=command.toUpperCase();
			//Get the command values
			String[] commandArr=command.split(",");
			//Change the state
			currentState=commandArr[0];
			//Write the new value
			tape.setCharAt(headPosition, commandArr[1].charAt(0));
			//Execute the action:
			char action=commandArr[2].charAt(0);
			switch (action){
			case 'L': //Move left
				headPosition--;
				break;
			case 'R': //Move right
				headPosition++;
				break;
			case 'H': //Halt
				halt=true;
				break;
			case 'P': //Halt
				//Print the Tape 
				String resp=tape.toString();
				resp=resp.replace("_", "");
				printTape("OUTPUT",stepNumber,key,headPosition,resp);		
				break;
			default:
				throw new RuntimeException("Invalid action: "+action+", Step: "+stepNumber);
			}
			
			//Update step counter
			stepNumber++;
			//Security check of program and tape size
			if(tape.length()>maxTapeSize){
				throw new RuntimeException("The tape is larger than the Machine limit: "+maxTapeSize+ "characters");
			}
			if(program.size()>maxProgramSize){
				throw new RuntimeException("The program is larger than the Machine limit: "+maxProgramSize+ "lines");
			}

		}
	}
	
	/**
	 * This method prints the tape in its current state
	 * @param stepNumber The current step in the execution
	 * @param key The <state>,<currentValue> pair
	 * @param headPosition the position of the head
	 * @param tape the contents of the tape
	 */
	private void printTape(String reason, int stepNumber, String key, int headPosition, String tape){
		StringBuffer message=new StringBuffer();
		message.append("%s==>");
		message.append("Step %3d, ");
		message.append("<state>,<value>: %s, ");
		message.append(" Head Position: %3d ==> ");
		message.append(tape);
		String outStr=message.toString();
		outStr=String.format(outStr, reason, stepNumber,key,headPosition);
		System.out.println(outStr);
	}
}
