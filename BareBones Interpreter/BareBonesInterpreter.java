/*
 * The way that this program executes BareBones code is inspired by the Classic CISC Pipeline:
 * 
 * 		1- Instruction Fetch
 * 		2- Instruction Decode
 *		3- Execute
 *		4- Memory Access [Skipped]
 *		5- Writeback
 * 
 * The program starts with 100 Instruction Memory Addresses
 * The program starts with 25 Data Memory Addresses
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
	public int varValue;
	
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

public class BareBonesInterpreter {

	static String[] instructionArray = new String[100]; //Array containing instructionArray[instructionPointer]s from the .txt file
	static VarTableRow[] dataArray = new VarTableRow[25]; //Array of VarTableRow (Class containing variable name and value)
	static int dataArrayIndex = 0; //First empty index of dataArray
	static int endOfFile; //Index of the last instructionArray[instructionPointer]
	static int instructionPointer; //Line of code to execute
	static String directoryPath = "C:/Users/nerla/Desktop/Coding/Projects/BareBonesInterpreter/src/"; //Path of the directory where the BareBones code is stored
	static String logBuffer = "";
	
	public static void readFile() { //Reads from the file containing the BareBones code
		int i=0;
		try {
			Scanner sc = new Scanner(new File(directoryPath+input("Source File: ")));
			while(sc.hasNextLine()) {
				instructionArray[i] = sc.nextLine();
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
		} else {
			throw new InstructionNotValid("Exception: Instruction Not Valid - "+instructionArray[instructionPointer]); //Exception thrown if the instructionArray[instructionPointer] is not recognised
		}
		writeLogBuffer("Instruction: "+instructionArray[instructionPointer]);
		logVariables();
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
