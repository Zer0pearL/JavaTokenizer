package tokenizer2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//: Keyword, Identifier, Operator, Separator, Number, or String.

public class Main {
	// placeholders for variables
	public static ArrayList<String> stringVariables = new ArrayList<>();
	public static ArrayList<String> integerVariables = new ArrayList<>();
	public static ArrayList<String> doubleVariables = new ArrayList<>();
	public static ArrayList<String> floatVariables = new ArrayList<>();
	// basic elements, placed in arrays
	public static String[] keywordsString = { "if", "else", "return", "int", "float", "String", "boolean", "double" },
			operatorsString = { "+", "-", "*", "/", "=", "==", "!=", ">", "<" },
			serparatorsString = { "{", "}", "(", ")", ",", ";" };
	// basic sequences of code
	public static String[] integerInitialization = { "int", "IDENTIFIER", "=", "LITERAL", ";" },
			floatInitialization = { "float", "IDENTIFIER", "=", "LITERAL", ";" },
			doubleInitialization = { "double", "IDENTIFIER", "=", "LITERAL", ";" },
			stringInitialization = { "String", "IDENTIFIER", "=", "LITERAL", ";" },
			characterInitialization = { "char", "IDENTIFIER", "=", "LITERAL", ";" },
			ifStatement = { "if", "(", "IDENTIFIER", "OPERATOR", "IDENTIFIER", ")", ";" },
			integerAssignment = { "IDENTIFIER", "=", "LITERAL", ";" },
			floatAssignment = { "IDENTIFIER", "=", "LITERAL", ";" },
			doubleAssignment = { "IDENTIFIER", "=", "LITERAL", ";" },
			stringAssignment = { "IDENTIFIER", "=", "LITERAL", ";" },
			characterAssignment = { "IDENTIFIER", "=", "LITERAL", ";" };
	// ArrayLists of known elements, to be constructed later
	public static ArrayList<String> keywords = new ArrayList<>(), operators = new ArrayList<>(),
			separators = new ArrayList<>();
	// basic sequences of code as ArrayLists
	public static ArrayList<String> integerInitializationSequence = new ArrayList<>(),
			floatInitializationSequence = new ArrayList<>(), doubleInitializationSequence = new ArrayList<>(),
			stringInitializationSequence = new ArrayList<>(), characterInitializationSequence = new ArrayList<>(),
			ifStatementSequence = new ArrayList<>(), integerAssignSequence = new ArrayList<>(),
			floatAssignSequence = new ArrayList<>(), doubleAssignSequence = new ArrayList<>(),
			stringAssignSequence = new ArrayList<>(), characterAssignSequence = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		// read file, place in a string
		FileReader fr = new FileReader("C:\\Users\\Admin\\Desktop\\dataB.txt");
		BufferedReader br = new BufferedReader(fr);
		String input = "";
		while (br.ready())
			input += br.readLine();
		// transform arrays into arraylists
		commenceTransformation();
		// run program
		run(input);

	}

	public static void run(String input) {
		// ArrayList<String> characterSequence = new ArrayList<>();
		ArrayList<Character> characterList = new ArrayList<>(); // current word
		ArrayList<String> elementsSequence = new ArrayList<>(); // sequence of words
		ArrayList<String> elementsTypeSequence = new ArrayList<>(); // sequence of types to check correctness
		ArrayList<String> bracketsSequence = new ArrayList<String>(); // sequence of brackets to ensure they are matcheing
		String wordToBeTokenized = "";
		for (int i = 0; i < input.length(); i++) {
			char tempCharacter = input.charAt(i); // current character
			if(tempCharacter==';') {
				tokenize(";");
				elementsSequence.add(";");
				if (!validateSequence(elementsSequence))
					return;
				elementsSequence = new ArrayList<>();
			}
			if (tempCharacter == '{' || tempCharacter == '}' || tempCharacter == '(' || tempCharacter == ')')
				bracketsSequence.add("" + tempCharacter);
			if ((tempCharacter == ' ' || tempCharacter == ';')) {
				for (Character character : characterList) {
					wordToBeTokenized += character;
				}
				if (tempCharacter == ' '&&!wordToBeTokenized.isEmpty()) {
					tokenize(wordToBeTokenized);
					elementsSequence.add(wordToBeTokenized);
					if(wordToBeTokenized.compareTo("if")==0) {
						i = runIfStatement(i, input);
						if(i==-1) return;
						wordToBeTokenized="";
						characterList = new ArrayList<Character>();
						continue;
					}
				} else if(!wordToBeTokenized.isEmpty()) {
					characterList.add('}');
					tokenize(tempCharacter+"");
					if (!validateSequence(elementsSequence))
						return;
					elementsSequence = new ArrayList<String>();
					System.out.println("//");
				}
				wordToBeTokenized = "";
				characterList = new ArrayList<Character>();
			}
			else
				characterList.add(tempCharacter);
		}
		if (!isSymmetric(bracketsSequence)) {
			System.out.println("Incorrect brackets/paranthesis  heirarchy");
		}
			
	}
	// validates the if condition, then validates the if statements.
	// returns -1 for error, otherwise index of next chat after the }
	public static int runIfStatement(int i, String input) {
		int j = i;
		ArrayList<String> ifStatementcondition = new ArrayList<String>();
		String word = "";
		for (j = i; j < input.length(); j++) {
			if(input.charAt(j)=='}') {
				return j;
			}
			if(input.charAt(j)=='{') {
				if(validateIfCondition(ifStatementcondition)==false)
					return -1;
				if(validateIfStatements(j, input)==false)
					return -1;
			}
			if(!(input.charAt(j)==' ')) {
				word+=input.charAt(j);
			}
			else {
				ifStatementcondition.add(word);
				word = "";
			}
		}
		System.out.println("Syntax Error: If statement never closed");
		return -1;
	}
	// validates if statemnts, by using run() function
	
	public static boolean validateIfStatements(int i, String input) {
		ArrayList<String> statements = new ArrayList<String>();
		String statementsString = "";
		String word = "";
		int j;
		for(j = i; j<input.length(); j++) {
			if(!(input.charAt(j)==' '))
				word+=input.charAt(j);
			else if(input.charAt(j)=='}') {
				break;
			}
			else {
				statements.add(word);
				word = "";
			}
		}
		statements.removeFirst();
		statements.removeLast();
		for (String string : statements) {
			statementsString += string + " ";
		}
		if(input.contains("}")) {
			run(statementsString);
			return true;
		}
		System.out.println("Syntax Error: Missing }  for the if statement");
		return false;
	}
	//validates if condition, and uses run() 
	
	public static boolean validateIfCondition(ArrayList<String> elementsSequence) {
		boolean leftParanthesis = false;
		boolean rightParanthesis = false;
		String previousElement = "";
		String conditionSequence = "";
		for (String string : elementsSequence) {
			conditionSequence+=string+" ";
			if(leftParanthesis==false&&string.compareTo("")==0) continue;
			if(string.compareTo("(")==0&&!(leftParanthesis==true)) {
				leftParanthesis = true;
				previousElement = string;
				continue;
			}
			else if(string.compareTo("(")==0&&(leftParanthesis==true)) {
				System.out.println("Syntax Error: Invalid ( paranthesis");
				return false;
			}
			if(string.compareTo(")")==0&&!(rightParanthesis==true)) {
				rightParanthesis = true;
				break;
			}
			else if (string.compareTo(")")==0&&(rightParanthesis==true)) {
				System.out.println("Syntax Error: Invalid ) paranthesis");
				return false;
			}
			if(validatePosition(string, previousElement)==false) 
				return false;
			previousElement = string;
		}
		if(leftParanthesis&&rightParanthesis==false) {
			System.out.println("Syntax Error: Missing Paranthesis");
			return false;
		}
		run(conditionSequence);
		return true;
	}
	// transformation of the arrays to arraylists
	public static void commenceTransformation() {
		keywords = transformArrayToArrayList(keywordsString);
		operators = transformArrayToArrayList(operatorsString);
		separators = transformArrayToArrayList(serparatorsString);
		integerInitializationSequence = transformArrayToArrayList(integerInitialization);
		floatInitializationSequence = transformArrayToArrayList(floatInitialization);
		doubleInitializationSequence = transformArrayToArrayList(doubleInitialization);
		stringInitializationSequence = transformArrayToArrayList(stringInitialization);
		characterInitializationSequence = transformArrayToArrayList(characterInitialization);
		ifStatementSequence = transformArrayToArrayList(ifStatement);
		integerAssignSequence = transformArrayToArrayList(integerAssignment);
		floatAssignSequence = transformArrayToArrayList(floatAssignment);
		doubleAssignSequence = transformArrayToArrayList(doubleAssignment);
		stringAssignSequence = transformArrayToArrayList(stringAssignment);
		characterAssignSequence = transformArrayToArrayList(characterAssignment);
	}
	// method to transform arrays to arraylists
	public static ArrayList<String> transformArrayToArrayList(String[] array) {
		ArrayList<String> arraylist = new ArrayList<String>();
		for (String string : array) {
			arraylist.add(string);
		}
		return arraylist;
	}
	// checks the type of the element by running through isKeyword, isOperator, isSeparator, isLiteral, where if true,
	// it returns the element type and tokenizes it, otherwise returns Identifier while tokenizing.
	public static String tokenize(String string) {
		if (isKeyword(string)) {
			System.out.println("('" + string + "', 'KEYWORD')");
			return "Keyword";
		}
		if (isOperator(string)) {
			System.out.println("('" + string + "', 'OPERATOR')");
			return "Operator";
		}
		if (isSeparator(string)) {
			System.out.println("('" + string + "', 'SEPARATOR')");
			return "Separator";
		}
		if (!(isLiteral(string).equals("null"))) {
			System.out.println("('" + string + "', 'LITERAL')");
			return "Literal";
		}
		System.out.println("('" + string + "', 'IDENTIFIER')");
		return "Identifier";

	}

	public static boolean isKeyword(String string) {
		return keywords.contains(string);
	}

	public static boolean isOperator(String string) {
		return operators.contains(string);
	}

	public static boolean isSeparator(String string) {
		return separators.contains(string);
	}
	// very messy, but uses nested try catch methods to check for what the literal is
	public static String isLiteral(String string) {
		try {
			Integer.parseInt(string);
			return "int";
		} catch (Exception e) {
			try {
				Float.parseFloat(string);
				return "float";
			} catch (Exception f) {
				try {
					Double.parseDouble(string);
					return "double";
				} catch (Exception g) {
					if (string.equals("true") || string.equals("false") || string.charAt(0) == '"'
							|| string.charAt(string.length() - 1) == '"')
						return "string";
				}
			}
		}
		return "null";
	}

	public static boolean isIdentifier(String string) {
		return !isKeyword(string) && !isOperator(string) && !isSeparator(string) && isLiteral(string).equals("null");
	}

	public static String toString(Character character) {
		String string = "";
		return string += character;
	}
	// nice function that checks the first element of each statement. Consequently, it calls a function
	// which specializes in validating what type of statement it is.
	public static boolean validateSequence(ArrayList<String> elementsSequence) {
		String firstElement = elementsSequence.getFirst();
		if (firstElement.compareTo("int") == 0)
			return validateIntInitializationSequence(elementsSequence);
		if (firstElement.compareTo("float") == 0)
			return validateFloatInitializationSequence(elementsSequence);
		if (firstElement.compareTo("double") == 0)
			return validateDoubleInitializationSequence(elementsSequence);
		if (firstElement.compareTo("String") == 0)
			return validateStringInitializationSequence(elementsSequence);
		if (firstElement.compareTo("if") == 0)
			return validateIfCondition(elementsSequence);
		if (isIdentifier(firstElement))
			return validateIdentifierSequence(elementsSequence);
		System.out.println("Syntax Error: Expected keyword or identifier, found " + firstElement);
		return false;
	}

	public static String identifierType(String string) {
		if (integerVariables.contains(string))
			return "int";
		if (stringVariables.contains(string))
			return "string";
		if (floatVariables.contains(string))
			return "float";
		if (doubleVariables.contains(string))
			return "double";
		return "null";
	}
	// validates an assignment sequence, if the sequence of elements is correct and if the identifier is defined
	public static boolean validateIdentifierSequence(ArrayList<String> elementsSequence) {
		String identifier = "";
		String previousElement = "";
		String identifierType = "";
		for (String string : elementsSequence) {
			if (isIdentifier(string) && identifier.compareTo("") == 0) {
				identifierType = identifierType(string);
				if (identifierType.compareTo("null") == 0) {
					System.out.println("Identifier not defined: " + string);
					return false;
				}
				identifier = string;
				continue;
			}
			String literalType = isLiteral(string);
			if (isLiteralBoolean(string) && literalType.compareTo("null") == 0 || isIdentifier(string)) {
				System.out.println("Literal not identified: " + string);
				return false;
			}
			if (isLiteralBoolean(string) && literalType.compareTo(identifierType) != 0) {
				System.out.println(literalType);

				System.out.println("Assignment Error: Cannot assign identifier of type '" + identifierType
						+ "' to a value " + "of type '" + literalType + "'");
				return false;
			}
			if (!validatePosition(string, previousElement))
				return false;
			previousElement = string;
		}

		return true;
	}
	// validates integer initialization sequence
	public static boolean validateIntInitializationSequence(ArrayList<String> elementsSequence) {
		String previousElement = "";
		String identifier = "";
		for (String string : elementsSequence) {
			if (isIdentifier(string))
				if (integerVariables.contains(string)) {
					System.out.println("Initialization Error For "+string+": Integer already defined");
					return false;
				} else {
					integerVariables.add(string);
					identifier = string;
					if(isLiteral(identifier.charAt(0)+"").equals("int")) {
						System.out.println("Invalid Initialization: cannot begin variable with a number");
						return false;
					};
				}
			if (isLiteralBoolean(string)) {
				if (isLiteral(string).compareTo("null") == 0) {
					System.out.println("Type mismatch: cannot assign value of type 'unknown' to variable " + identifier
							+ " of type 'int'" + string);
					return false;
				} else if (isLiteral(string).compareTo("int") != 0) {
					System.out.println("Type mismatch: cannot assign value of type '" + isLiteral(string)
							+ "' to variable " + identifier + " of type 'int'");
					return false;
				}
			}
			if (!isKeyword(string))
				if (!validatePosition(string, previousElement))
					return false;
			previousElement = string;
		}
		return true;
	}
	// helper method to turn isLiteral() to a boolean method
	public static boolean isLiteralBoolean(String string) {
		return !(isLiteral(string).compareTo("null") == 0);
	}
	// validates double initialization sequence
	public static boolean validateDoubleInitializationSequence(ArrayList<String> elementsSequence) {
		String previousElement = "";
		String identifier = "";
		for (String string : elementsSequence) {
			if (isIdentifier(string))
				if (doubleVariables.contains(string)) {
					System.out.println("Initialization Error: Double already defined");
					return false;
				} else {
					doubleVariables.add(string);
					identifier = string;
					if(isLiteral(identifier.charAt(0)+"").equals("int")) {
						System.out.println("Invalid Initialization: cannot begin variable with a number");
						return false;
					};
				}
			if (isLiteralBoolean(string)) {
				if (isLiteral(string).compareTo("null") == 0) {
					System.out.println("Type mismatch: cannot assign value of type 'unknown' to variable " + identifier
							+ " of type 'double'" + string);
					return false;
				} else if (isLiteral(string).compareTo("double") != 0) {
					System.out.println("Type mismatch: cannot assign value of type '" + isLiteral(string)
							+ "' to variable " + identifier + " of type 'double'");
					return false;
				}
			}
			if (!isKeyword(string))
				if (!validatePosition(string, previousElement))
					return false;
			previousElement = string;
		}
		return true;
	}
	// validates string initialization sequence
	public static boolean validateStringInitializationSequence(ArrayList<String> elementsSequence) {
		String previousElement = "";
		String identifier = "";
		for (String string : elementsSequence) {
			if (isIdentifier(string))
				if (stringVariables.contains(string)) {
					System.out.println("Initialization Error: String already defined");
					return false;
				} else {
					stringVariables.add(string);
					identifier = string;
					if(isLiteral(identifier.charAt(0)+"").equals("int")) {
						System.out.println("Invalid Initialization: cannot begin variable with a number");
					};
				}
			if (isLiteralBoolean(string)) {
				if (isLiteral(string).compareTo("null") == 0) {
					System.out.println("Type mismatch: cannot assign value of type 'unknown' to variable " + identifier
							+ " of type 'String'" + string);
					return false;
				} else if (isLiteral(string).compareTo("string") != 0) {
					System.out.println("Type mismatch: cannot assign value of type '" + isLiteral(string)
							+ "' to variable " + identifier + " of type 'String'");
					return false;
				}
			}
			if (!isKeyword(string))
				if (!validatePosition(string, previousElement))
					return false;
			previousElement = string;
		}
		return true;
	}
	// validates float initialization sequence
	public static boolean validateFloatInitializationSequence(ArrayList<String> elementsSequence) {
		String previousElement = "";
		String identifier = "";
		for (String string : elementsSequence) {
			if (isIdentifier(string))
				if (floatVariables.contains(string)) {
					System.out.println("Initialization Error: Float already defined");
					return false;
				} else {
					floatVariables.add(string);
					identifier = string;
					if(isLiteral(identifier.charAt(0)+"").equals("int")) {
						System.out.println("Invalid Initialization: cannot begin variable with a number");
						return false;
					};
				}
			if (isLiteralBoolean(string)) {
				if (isLiteral(string).compareTo("null") == 0) {
					System.out.println("Type mismatch: cannot assign value of type 'unknown' to variable " + identifier
							+ " of type 'float'" + string);
					return false;
				} else if (isLiteral(string).compareTo("float") != 0) {
					System.out.println("Type mismatch: cannot assign value of type '" + isLiteral(string)
							+ "' to variable " + identifier + " of type 'float'");
					return false;
				}
			}
			if (!isKeyword(string))
				if (!validatePosition(string, previousElement))
					return false;
			previousElement = string;
		}
		return true;
	}
	// checks is a sequence is correct
	public static boolean compareArrayLists(ArrayList<String> elementsSequence, ArrayList<String> correctSequence) {
		if (elementsSequence.size() != correctSequence.size()) {
			System.out.println("Incorrect Format");
			return false;
		}
		return true;
	}
	// validates the position of an element in a statement or if statament
	public static boolean validatePosition(String string, String previousElement) {
		// System.out.println(previousElement);
		if (previousElement.compareTo("if") == 0) {
			System.out.println("Unexpected token " + string + " : if should be followed by (");
			return false;
		}
		if ((isIdentifier(string) || !(isLiteral(string).equals("null")))
				&& (isIdentifier(previousElement) || !(isLiteral(previousElement).equals("null")))) {
			System.out.println("Error: expected operator or separator before " + string + ", at " + previousElement
					+ " " + string);
			return false;
		}
		if (string == "{" && !(previousElement == ")")) {
			System.out.println("Error: Invalid position for }");
			return false;
		}
		if (isOperator(string) && isOperator(previousElement) || isOperator(string) && isSeparator(previousElement)
				|| isSeparator(previousElement) && isSeparator(string)
				|| isOperator(previousElement) && isSeparator(string)) {
			System.out.println("Error: expected identifier or literal before " + string + ", at " + previousElement
					+ " " + string);
			return false;
		}
		return true;
	}
	// used for checking brackets
	public static boolean isSymmetric(ArrayList<String> list) {
		// cant use for each
		if (list.size() % 2 != 0)
			return false;
		for (int i = 0; i < list.size(); i++) {
			if (list.isEmpty())
				return true;
			if (list.get(i) == "{" && !(list.get(list.size() - 1 - i) == "}")
					|| list.get(i) == "(" && !(list.get(list.size() - i - 1) == ")"))
				return false;
			list.removeFirst();
			list.removeLast();
		}
		return true;
	}
}
