/*
 * The way that this program executes BareBones code is inspired by the Classic CISC Pipeline:
 * 
 * 		1- Instruction Fetch
 * 		2- Instruction Decode
 *		3- Execute
 *		4- Memory Access [Skipped]
 *		5- Writeback
 * 
 * The program starts with 200 Instruction Memory Addresses
 * The program starts with 50 Data Memory Addresses
 * 
 * Extended BareBones instructions:
 * 		Comments:							//CommentExample
 * 		Copy:								mov a, b; 			Copies b into a
 * 		Addition:							add a, b in x;		Executes a+b and puts result in x
 * 		Subtraction:						sub a, b in x;		Executes a-b and puts result in x
 * 		Multiplication:						mul a, b in x;		Executes a*b and puts result in x
 * 		Division (With Remainder) :			div a, b in x;		Executes (a-a%b)/b and puts result in x 
 * 		Exponentiation:						exp a, b in x;		Executes a^b and puts result in x
 */



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Scanner;

class VarTableRow {
	
	public char varName;
	public double varValue;
	
	public String toString() { //Returns a String with the variable's name and its value
		return ""+varName+" : "+varValue;
	}
	
}

@SuppressWarnings("serial")
class InstructionNotValid extends Exception { //Exception thrown in the event of an invalid instruction
	public InstructionNotValid(String message) {
		super(message);
	}
}

@SuppressWarnings("serial")
class VariableNotDeclared extends Exception { //Exception thrown in the event of an operation on an undeclared variable
	public VariableNotDeclared(String message) {
		super(message);
	}
}

public class ExtendedBareBonesInterpreter {

	static String[] instructionArray = new String[200]; //Array containing instructionArray[instructionPointer]s from the .txt file
	static VarTableRow[] dataArray = new VarTableRow[50]; //Array of VarTableRow (Class containing variable name and value)
	static int dataArrayIndex = 0; //First empty index of dataArray
	static int endOfFile; //Index of the last instructionArray[instructionPointer]
	static int instructionPointer; //Line of code to execute
	static String directoryPath = "C:/Users/nerla/Desktop/Coding/Projects/ExtendedBareBonesInterpreter/src/"; //Path of the directory where the BareBones code is stored
	static String logBuffer = "";
	
	public static void readFile() { //Reads from the file containing the BareBones code
		int i=0;
		try {
			Scanner sc = new Scanner(new File(directoryPath+input("Source File: ")));
			while(sc.hasNextLine()) {
				instructionArray[i] = sc.nextLine();
				if(instructionArray[i].contains("//")) {
					instructionArray[i] = instructionArray[i].substring(0, instructionArray[i].indexOf(";")+1);
				}
				i += 1;
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			endOfFile = i-1;
		}
		endOfFile=i;
	}
	
	public static void writeLogBuffer(String logLine) {
		logBuffer += logLine+"\n";
	}
	
	public static void writeLog() { //Writes a line in the log file
		try {
			if(Files.notExists(Paths.get(directoryPath+"log.txt"))) {
				Files.createFile(Paths.get(directoryPath+"log.txt"));
			}
			Files.write(Paths.get(directoryPath+"log.txt"), (logBuffer).getBytes(), StandardOpenOption.APPEND);	
			System.out.println("\n[Log Written]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void logVariables() { //Writes the variables' name and value to the log file
		writeLogBuffer("Instruction Pointer: "+instructionPointer);
		for (int i=0; i < dataArrayIndex; i++) {
			writeLogBuffer(dataArray[i].toString());
		}
		writeLogBuffer("");
	}
	
	public static String input(String request) { //Manages input
		Scanner input =  new Scanner(System.in);
		System.out.print(request);
		String s = input.nextLine();
		input.close();
		return s;
	}

	public static void interpret() { //Interprets the BareBones code
		readFile();
		System.out.println("");
		writeLogBuffer("Log: "+ LocalDateTime.now() + "\n");
		while(instructionPointer<endOfFile) { 
			try {
				interpretCommand();
			} catch (VariableNotDeclared | InstructionNotValid e) {
				e.printStackTrace();
				writeLogBuffer(e.toString()+"\n");
			}
			instructionPointer++;
		}
		for(int i=0; i<dataArrayIndex; i++) {
			System.out.print(dataArray[i].varName);
			System.out.print(" = ");
			System.out.println(dataArray[i].varValue);
		}
		writeLogBuffer("\n\n");
	}
	
	public static void interpretCommand() throws VariableNotDeclared, InstructionNotValid { //Interprets a single instructionArray[instructionPointer]

		
		if(instructionArray[instructionPointer].contains("clear")) {
			clearVariable();
		} else if (instructionArray[instructionPointer].contains("incr")) {
			incrVariable();
		} else if (instructionArray[instructionPointer].contains("decr")) {
			decrVariable();
		} else if (instructionArray[instructionPointer].contains("while")) {
			loop();
		} else if (instructionArray[instructionPointer].contains("end;")) {
			instructionPointer++;
		} else if (instructionArray[instructionPointer].contains("mov")) {
			mov();
		} else if (instructionArray[instructionPointer].contains("add")) {
			add();
		} else if (instructionArray[instructionPointer].contains("sub")) {
			sub();
		} else if (instructionArray[instructionPointer].contains("mul")) {
			mul();
		} else if (instructionArray[instructionPointer].contains("div")) {
			div();
		} else if (instructionArray[instructionPointer].contains("exp")) {
			exp();
		} else {
			throw new InstructionNotValid("Exception: Instruction Not Valid - "+instructionArray[instructionPointer]); //Exception thrown if the instructionArray[instructionPointer] is not recognised
		}
		writeLogBuffer("Instruction: "+instructionArray[instructionPointer]);
		logVariables();
	}
	
	

	private static void mov() { //Explained in "Extended BareBones Instructions"
		int targetVar = findVarIndex(extractDoubleVarName()[0]);
		int fromVar = findVarIndex(extractDoubleVarName()[1]);
		dataArray[targetVar].varValue = dataArray[fromVar].varValue;
	}
	
	private static void add() { //Explained in "Extended BareBones Instructions" 
		int firstVar = findVarIndex(extractTripleVarName()[0]);
		int secondVar = findVarIndex(extractTripleVarName()[1]);
		int targetVar = findVarIndex(extractTripleVarName()[2]);
		dataArray[targetVar].varValue = dataArray[firstVar].varValue + dataArray[secondVar].varValue;
	}
	
	private static void sub() { //Explained in "Extended BareBones Instructions"
		int firstVar = findVarIndex(extractTripleVarName()[0]);
		int secondVar = findVarIndex(extractTripleVarName()[1]);
		int targetVar = findVarIndex(extractTripleVarName()[2]);
		dataArray[targetVar].varValue = dataArray[firstVar].varValue - dataArray[secondVar].varValue;
	}
	
	private static void mul() { //Explained in "Extended BareBones Instructions"
		int firstVar = findVarIndex(extractTripleVarName()[0]);
		int secondVar = findVarIndex(extractTripleVarName()[1]);
		int targetVar = findVarIndex(extractTripleVarName()[2]);
		dataArray[targetVar].varValue = dataArray[firstVar].varValue * dataArray[secondVar].varValue;
	}
	
	private static void div() { //Explained in "Extended BareBones Instructions"
		int firstVar = findVarIndex(extractTripleVarName()[0]);
		int secondVar = findVarIndex(extractTripleVarName()[1]);
		int targetVar = findVarIndex(extractTripleVarName()[2]);
		dataArray[targetVar].varValue = (dataArray[firstVar].varValue - (dataArray[firstVar].varValue % dataArray[secondVar].varValue)) / dataArray[secondVar].varValue;
	}
	
	private static void exp() { //Explained in "Extended BareBones Instructions"
		int firstVar = findVarIndex(extractTripleVarName()[0]);
		int secondVar = findVarIndex(extractTripleVarName()[1]);
		int targetVar = findVarIndex(extractTripleVarName()[2]);
		dataArray[targetVar].varValue = (int) Math.pow(dataArray[firstVar].varValue, dataArray[secondVar].varValue);
	}

	private static char[] extractDoubleVarName() {
		char[] varArray = new char[2];
		varArray[0] = instructionArray[instructionPointer].charAt(instructionArray[instructionPointer].indexOf(",")-1);
		varArray[1] = instructionArray[instructionPointer].charAt(instructionArray[instructionPointer].indexOf(",")+2);
		return varArray;
	}
	
	private static char[] extractTripleVarName() {
		char[] varArray = new char[3];
		varArray[0] = instructionArray[instructionPointer].charAt(instructionArray[instructionPointer].indexOf(",")-1);
		varArray[1] = instructionArray[instructionPointer].charAt(instructionArray[instructionPointer].indexOf(",")+2);
		varArray[2] = instructionArray[instructionPointer].charAt(instructionArray[instructionPointer].indexOf(";")-1);
		return varArray;
	}

	private static void loop() throws VariableNotDeclared, InstructionNotValid { //Deals with "while varName not 0;" instructionArray[instructionPointer]s
		int startIndex = instructionPointer+1; //index of the "while varName not 0;" instructionArray[instructionPointer]
		int endIndex = findEndIndex(instructionArray[instructionPointer]); //index of the "end;" instructionArray[instructionPointer]
		int varNotZeroIndex = findVarIndex(instructionArray[instructionPointer].charAt(instructionArray[instructionPointer].indexOf("n")-2)); //index of the variable used for the "while varName not 0"; instructionArray[instructionPointer]
		while(dataArray[varNotZeroIndex].varValue != 0) { 
			instructionPointer = startIndex; 
			while(instructionPointer < endIndex) {
				interpretCommand();
				instructionPointer++;
			}
			
		}
		instructionPointer = endIndex; //Exits the loop
	}

	private static int findEndIndex(String s) { //Finds the index of the end of a loop
		s=s.substring(0, s.indexOf('w'));
		int i=instructionPointer;
		while(!instructionArray[i].contains("end;") || instructionArray[i].contains("   "+s)) {
			i++;
		}
		return i;
	}

	public static void incrVariable() throws VariableNotDeclared { //Deals with "incr varName;" instructionArray[instructionPointer]s
		if(findVarIndex(extractVarName()) != -1) {
			dataArray[findVarIndex(extractVarName())].varValue += 1;	
		} else {
			throw new VariableNotDeclared("Exception: Variable "+extractVarName()+" not declared");	//Exception thrown if the variable to be incremented has not been declared
		}
	}
	
	public static void decrVariable() throws VariableNotDeclared { //Deals with "decr varName;" instructionArray[instructionPointer]s
		if(findVarIndex(extractVarName()) != -1) {
			dataArray[findVarIndex(extractVarName())].varValue -= 1;	
		} else {
			throw new VariableNotDeclared("Exception: Variable "+extractVarName()+" not declared");	//Exception thrown if the variable to be decremented has not been declared	
		}
	}
	
	public static void clearVariable() { //Deals with "clear varName;" instructionArray[instructionPointer]s
		if(findVarIndex(extractVarName()) != -1) {
			dataArray[findVarIndex(extractVarName())].varValue = 0;	
		} else {
			dataArray[dataArrayIndex] = new VarTableRow();
			dataArray[dataArrayIndex].varName = extractVarName();
			dataArray[dataArrayIndex].varValue = 0;
			dataArrayIndex +=1;
		}
	}
	
	private static int findVarIndex(char varName) { //Finds the index of the variable in dataArray
		int flag = -1;
		for(int i=0; i<dataArrayIndex; i++) {
			if(dataArray[i].varName == varName) {
				flag = i;
			}
		}
		return flag;
	}

	private static char extractVarName() { //Extracts the variable name from the instructionArray[instructionPointer]
		return instructionArray[instructionPointer].charAt(instructionArray[instructionPointer].indexOf(";")-1);
	}

	public static void main(String[] args) {
		interpret();
		writeLog();
	}

}
