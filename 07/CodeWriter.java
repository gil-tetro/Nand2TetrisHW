import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Generates assembly code from the parsed VM command
 */
public class CodeWriter {
  private int jumpCounter;
  private PrintWriter outputWriter;

  /**
   * Opens an output file / stream and gets ready to write into it,
   * and initializes the jump counter.
   */
  public CodeWriter(File outputFile) {
    try {
      outputWriter = new PrintWriter(outputFile);
      jumpCounter = 0;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Writes to the output file the assembly code that implements the given
   * arithmetic-logical command.
   * 
   * @param command
   */
  public void writeArithmetic(String command) {
    switch (command) {
      case "add":
        outputWriter.print(arithmeticTemplate() + "M=M+D\n");
        break;
      case "sub":
        outputWriter.print(arithmeticTemplate() + "M=M-D\n");
        break;
      case "and":
        outputWriter.print(arithmeticTemplate() + "M=M&D\n");
        break;
      case "or":
        outputWriter.print(arithmeticTemplate() + "M=M|D\n");
        break;
      case "gt":
        outputWriter.print(comparisonNegationTemplate("JLE"));
        jumpCounter++;
        break;
      case "lt":
        outputWriter.print(comparisonNegationTemplate("JGE"));
        jumpCounter++;
        break;
      case "eq":
        outputWriter.print(comparisonNegationTemplate("JNE"));
        jumpCounter++;
        break;
      case "not":
        outputWriter.print("@SP\nA=M-1\nM=!M\n");
        
        break;
      case "neg":
        outputWriter.print("D=0\n@SP\nA=M-1\nM=D-M\n");
        break;
      default:
      // debug
      System.out.print(command);
        throw new IllegalArgumentException("Undefined for non-arithmetic commands");
    }
  }

  /**
   * Writes to the output file the assembly code that implements the given push or
   * pop command.
   * 
   * @param commandType
   * @param memorySegment
   * @param value
   */
  public void writePushPop(int commandType, String memorySegment, int value) {
    // handle PUSH commands
    if (commandType == Parser.commandTypes.get("PUSH")) {
      switch (memorySegment) {
        case "constant":
          outputWriter.print("@" + value + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
          break;
        case "local":
          outputWriter.print(pushTemplate("LCL", value, false));
          break;
        case "argument":
          outputWriter.print(pushTemplate("ARG", value, false));
          break;
        case "this":
          outputWriter.print(pushTemplate("THIS", value, false));
          break;
        case "that":
          outputWriter.print(pushTemplate("THAT", value, false));
          break;
        case "temp":
          outputWriter.print(pushTemplate("R5", value + 5, false));
          break;
        case "pointer":
          if (value == 0) {
            outputWriter.print(pushTemplate("THIS", value, true));
          } else if (value == 1) {
            outputWriter.print(pushTemplate("THAT", value, true));
          }
          break;
        case "static":
          outputWriter.print(pushTemplate(String.valueOf(value + 16), value, true));
          break;
      }
      // handle POP commands
    } else if (commandType == Parser.commandTypes.get("POP")) {
      switch (memorySegment) {
        case "local":
          outputWriter.print(popTemplate("LCL", value, false));
          break;
        case "argument":
          outputWriter.print(popTemplate("ARG", value, false));
          break;
        case "this":
          outputWriter.print(popTemplate("THIS", value, false));
          break;
        case "that":
          outputWriter.print(popTemplate("THAT", value, false));
          break;
        case "temp":
          outputWriter.print(popTemplate("R5", value + 5, false));
          break;
        case "pointer":
          if (value == 0) {
            outputWriter.print(popTemplate("THIS", value, true));
          } else if (value == 1) {
            outputWriter.print(popTemplate("THAT", value, true));
          }
          break;
        case "static":
          outputWriter.print(popTemplate(String.valueOf(value + 16), value, true));
          break;
      }
    } else {
      throw new IllegalArgumentException("Undefined for non push / pop commands");
    }
  }

  /**
   * Closes the output file
   */

  public void close() {
    outputWriter.close();
  }

  /**
   * Assembly template for arithmetic operations(+,-,&,|)
   * 
   * @return
   *         a header for arithmetic assembly commands
   */
  private String arithmeticTemplate() {
    return "@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "A=A-1\n";
  }

  /**
   * Assembly template for comparison operations(<,>,=)
   * 
   * @param type
   * @return
   *         Assembly commands for comparison based jump
   */
  private String comparisonNegationTemplate(String type) {
    return "@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "A=A-1\n" +
        "D=M-D\n" +
        "@FALSE" + jumpCounter + "\n" +
        "D;" + type + "\n" +
        "@SP\n" +
        "A=M-1\n" +
        "M=-1\n" +
        "@CONTINUE" + jumpCounter + "\n" +
        "0;JMP\n" +
        "(FALSE" + jumpCounter + ")\n" +
        "@SP\n" +
        "A=M-1\n" +
        "M=0\n" +
        "(CONTINUE" + jumpCounter + ")\n";
  }

  /**
   * Assembly template for the different push commands
   * 
   * @param memSegment
   * @param value
   * @param isDirect   determines if its a pointer or static
   * @return
   *         Assembly commands for push commands
   */
  private String pushTemplate(String memSegment, int value, boolean isDirect) {
    String staticCode = (isDirect) ? "" : "@" + value + "\n" + "A=D+A\nD=M\n";

    return "@" + memSegment + "\n" +
        "D=M\n" +
        staticCode +
        "@SP\n" +
        "A=M\n" +
        "M=D\n" +
        "@SP\n" +
        "M=M+1\n";
  }

  /**
   * Assembly template for the different pop commands
   * 
   * @param memSegment
   * @param value
   * @param isDirect   determines if its a pointer or static
   * @return
   *         Assembly commands for pop commands
   */
  private String popTemplate(String memSegment, int value, boolean isDirect) {
    String staticCode = (isDirect) ? "D=A\n" : "D=M\n@" + value + "\nD=D+A\n";
    return "@" + memSegment + "\n" +
        staticCode +
        "@R13\n" +
        "M=D\n" +
        "@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "@R13\n" +
        "A=M\n" +
        "M=D\n";
  }
}