import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
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
	static VarTableRow[] varTable = new VarTableRow[100]; //Array of VarTableRow (Class containing variable name and value)
	static int varTableIndex = 0; //First empty index of varTable
	static int endOfFile; //Index of the last instructionArray[instructionPointer]
	static int instructionPointer; //Line of code to execute
	static String directoryPath = "C:/Users/nerla/Desktop/Coding/Projects/BareBonesInterpreter/src/"; //Path of the directory where the BareBones code is stored
	
	
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
	
	public static void writeLog(String logLine) { //Writes a line in the log file
		try {
			if(Files.notExists(Paths.get(directoryPath+"log.txt"))) {
				Files.createFile(Paths.get(directoryPath+"log.txt"));
			}
			Files.write(Paths.get(directoryPath+"log.txt"), (logLine+"\n").getBytes(), StandardOpenOption.APPEND);	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void logVariables() { //Writes the variables' name and value to the log file
		writeLog("Instruction Pointer: "+instructionPointer);
		for (int i=0; i < varTableIndex; i++) {
			writeLog(varTable[i].toString());
		}
		writeLog("");
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
		writeLog("Log: "+ LocalDate.now());
		while(instructionPointer<endOfFile) { 
			try {
				interpretCommand();
			} catch (VariableNotDeclared | InstructionNotValid e) {
				e.printStackTrace();
				writeLog(e.toString()+"\n");
			}
			instructionPointer++;
		}
		for(int i=0; i<varTableIndex; i++) {
			System.out.print(varTable[i].varName);
			System.out.print(" = ");
			System.out.println(varTable[i].varValue);
		}
		writeLog("\n\n");
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
		writeLog("Instruction: "+instructionArray[instructionPointer]);
		logVariables();
	}
	
	

	private static void loop() throws VariableNotDeclared, InstructionNotValid { //Deals with "while varName not 0;" instructionArray[instructionPointer]s
		int startIndex = instructionPointer+1; //index of the "while varName not 0;" instructionArray[instructionPointer]
		int endIndex = findEndIndex(instructionArray[instructionPointer]); //index of the "end;" instructionArray[instructionPointer]
		int varNotZeroIndex = findVarIndex(instructionArray[instructionPointer].charAt(instructionArray[instructionPointer].indexOf("n")-2)); //index of the variable used for the "while varName not 0"; instructionArray[instructionPointer]
		while(varTable[varNotZeroIndex].varValue != 0) { 
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
			varTable[findVarIndex(extractVarName())].varValue += 1;	
		} else {
			throw new VariableNotDeclared("Exception: Variable "+extractVarName()+" not declared");	//Exception thrown if the variable to be incremented has not been declared
		}
	}
	
	public static void decrVariable() throws VariableNotDeclared { //Deals with "decr varName;" instructionArray[instructionPointer]s
		if(findVarIndex(extractVarName()) != -1) {
			varTable[findVarIndex(extractVarName())].varValue -= 1;	
		} else {
			throw new VariableNotDeclared("Exception: Variable "+extractVarName()+" not declared");	//Exception thrown if the variable to be decremented has not been declared	
		}
	}
	
	public static void clearVariable() { //Deals with "clear varName;" instructionArray[instructionPointer]s
		if(findVarIndex(extractVarName()) != -1) {
			varTable[findVarIndex(extractVarName())].varValue = 0;	
		} else {
			varTable[varTableIndex] = new VarTableRow();
			varTable[varTableIndex].varName = extractVarName();
			varTable[varTableIndex].varValue = 0;
			varTableIndex +=1;
		}
	}
	
	private static int findVarIndex(char varName) { //Finds the index of the variable in varTable
		int flag = -1;
		for(int i=0; i<varTableIndex; i++) {
			if(varTable[i].varName == varName) {
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
	}

}
