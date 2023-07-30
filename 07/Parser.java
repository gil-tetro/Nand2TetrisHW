import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles the parsing of a signle .vm file.
 * Reads a VM command, parses the command into its lexical components,
 * and provides convenient access to these components.
 * Ignores white space and comments.
 */
public class Parser {
  private Scanner lines; // lines in file
  private String currentLine; // current line in file

  private int commandType; // first arg of a vm command
  private String memorySegment; // second arg of a vm command
  private int value; // third arg of a vm command

  // Create a map for the command types
  public static final Map<String, Integer> commandTypes = new HashMap<String, Integer>();
  // Create a list for the arithmetic command names
  public static final ArrayList<String> arithmeticCommands = new ArrayList<String>();

  /**
   * Mapping the commandType fileds.
   */
  private void initializeCommandTypes() {
    commandTypes.put("ARITHMETIC", 0);
    commandTypes.put("PUSH", 1);
    commandTypes.put("POP", 2);
    commandTypes.put("LABEL", 3);
    commandTypes.put("GOTO", 4);
    commandTypes.put("IF", 5);
    commandTypes.put("FUNCTION", 6);
    commandTypes.put("RETURN", 7);
    commandTypes.put("CALL", 8);
  }

  /**
   * Initializes the arithmetic command names.
   */
  private void initializeArithmeticCommandNames() {
    arithmeticCommands.add("add");
    arithmeticCommands.add("sub");
    arithmeticCommands.add("neg");
    arithmeticCommands.add("eq");
    arithmeticCommands.add("gt");
    arithmeticCommands.add("lt");
    arithmeticCommands.add("and");
    arithmeticCommands.add("or");
    arithmeticCommands.add("not");
  }

  /**
   * initialize the requiered predefined command names and types.
   * Opens the input file(.vm)/stream and get ready to parse it.
   * 
   * @param input
   */

  public Parser(File input) {

    initializeCommandTypes();
    initializeArithmeticCommandNames();
    commandType = -1;
    memorySegment = "";
    value = -1;

    // attempt to read file and handle invalid input
    try {
      lines = new Scanner(input);
      StringBuilder preprocessed = new StringBuilder();
      String line = "";

      while (lines.hasNext()) {
        line = eraseComments(lines.nextLine());

        if (line.length() > 0) {
          preprocessed.append(line);
          preprocessed.append("\n");
        }
      }
      lines = new Scanner(preprocessed.toString());

    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
  }

  /**
   * Checks if there are more lines to read from the file.
   * 
   * @return
   */
  public boolean hasMoreLines() {
    return lines.hasNextLine();
  }

  /**
   * Reads next command from the input and makes it current command
   * Should be called only when hasMoreLines() returns true
   */
  public void advance() {
    // step and initialization
    currentLine = lines.nextLine();
    memorySegment = "";
    value = -1;

    // command validation
    String[] arguments = currentLine.split(" ");
    if (arguments.length > 3) {
      throw new IllegalArgumentException("Too many arguments");
    }

    // handle arithmetic type commands
    if (arithmeticCommands.contains(arguments[0])) {

      commandType = commandTypes.get("ARITHMETIC");
      memorySegment = arguments[0];
      // handle return commands
    } else if (arguments[0].equals("return")) {

      commandType = commandTypes.get("RETURN");
      memorySegment = arguments[0];
      // handle other commands
    } else {
      memorySegment = arguments[1];

      // Switch case for different commands
      switch (arguments[0]) {
        case "push":
          commandType = commandTypes.get("PUSH");
          break;
        case "pop":
          commandType = commandTypes.get("POP");
          break;
        case "label":
          commandType = commandTypes.get("LABEL");
          break;
        case "if":
          commandType = commandTypes.get("IF");
          break;
        case "goto":
          commandType = commandTypes.get("GOTO");
          break;
        case "function":
          commandType = commandTypes.get("FUNCTION");
          break;
        case "call":
          commandType = commandTypes.get("CALL");
          break;

        default:
          throw new IllegalArgumentException("Unknown command");
      }
      // access and validate the numeric value \ address for relevant commands
      if (commandType == commandTypes.get("PUSH") || commandType == commandTypes.get("POP")
          || commandType == commandTypes.get("FUNCTION") || commandType == commandTypes.get("CALL")) {
        try {
          value = Integer.parseInt(arguments[2]);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("value is not an integer");
        }
      }
    }
  }

  /**
   * Returns a constant representing the type of the current command.
   * If the current command is an arithmetic-logical command, returns ARITHMETIC.
   * 
   * @return
   *         command type
   * 
   */
  public int CommandType() {
    if (commandType != -1) {
      return commandType;
    } else {
      throw new IllegalStateException("No command was given");
    }
  }

  /**
   * Returns the first argument of the current command
   * in the case of ARITHMETIC, the command itself (add, sub, etc.) is returned.
   * Should not be called if the current command is RETURN.
   * 
   * @return
   *         first argument of the current command
   */
  public String arg1() {
    if (commandType != commandTypes.get("RETURN")) {
      return memorySegment;
    } else {
      throw new IllegalStateException("Command does not include a memory segment");
    }
  }

  /**
   * Return the second argument of the current command.
   * Should be called only if the current command is is PUSH, POP, FUNCTION or
   * CALL.
   * 
   * @return
   *         second argument of the current command
   */
  public int arg2() {
    if (commandType == commandTypes.get("PUSH") || commandType == commandTypes.get("POP")
        || commandType == commandTypes.get("FUNCTION") || commandType == commandTypes.get("CALL")) {
      return value;
    } else {
      throw new IllegalStateException("Command does not include an address / value");
    }
  }

  /**
   * handles command with inline comments
   * 
   * @param line
   * @return
   *         command without inline comments
   */
  private String eraseComments(String line) {
    String output = line;
    if (line.contains("//")) {
      output = line.split("//")[0];
    }
    return output.trim();
  }

  /**
   * Get the file extension of the input file name
   * 
   * @param file
   * @return
   *         file extention string
   */
  public static String getFileExtension(String file) {
    int index = file.lastIndexOf('.');

    return (index != -1) ? file.substring(index) : "";
  }
}