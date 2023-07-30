import java.io.*;
import java.util.Scanner;
/* 
 * Reads and parses an instruction.
 * 
 * hasMoreLines(): Checks if there there are more lines (instructions) to read (boolean).
 * advance(): Gets the next instruction and makes it the current instruction (string).
 * 
 * Parsing the current instruction:
 * instructionType(): Returns the current instruction type (constant):
 *    - A_INSTRUCTION for @ xxx, where xxx is either a decimal number or a symbol
 *    - C_INSTRUCTION for dest = comp ; jump
 *    - L_INSTRUCTION for (label)
 * 
 * symbol(): Return the instruction's symbol (string).
 * Using 3 getter methods from the Code class:
 *    - dest(): Returns the instruction’s dest field (string)
 *    - comp(): Returns the instruction’s comp field (string)
 *    - jump(): Returns the instruction’s jump field (string) 
 * 
 * !!! THIS IMPLEMENTATION ASSUMES THE SOURCE PATH, FILE & SYNTAX ARE VALID !!!
 */

public class Parser {
  private Scanner input;
  private String currentLine;
  private Code code = new Code();

  // Denotes the different types of instructions (instead of using constants)
  enum instructionType {
    A_INSTRUCTION,
    C_INSTRUCTION,
    L_INSTRUCTION,
  }

  /* Initialize the Parser with a scanner to read lines from the file */
  public Parser(File source) throws FileNotFoundException {
    this.input = new Scanner(source);
  }

  /* Checks if there there are more lines (instructions) to read (boolean) */
  public boolean hasMoreLines() {
    return input.hasNextLine();
  }

  public void advance() {
    if (hasMoreLines()) {
      String str = input.nextLine();
      str = str.replaceAll(" ", "");
      if (str.isEmpty()) {
        advance();
      } else if (str.startsWith("//")) {
        advance();
      } else if (str.contains("//")) {
        currentLine = str.split("//")[0];
      } else {
        currentLine = str;
      }
    }
  }

  /* Returns the current instruction type */
  public instructionType getInstructionType() {
    char firstChar = this.currentLine.charAt(0);
    switch (firstChar) {
      case '@':
        return instructionType.A_INSTRUCTION;
      case '(':
        return instructionType.L_INSTRUCTION;
      default:
        return instructionType.C_INSTRUCTION;
    }
  }

  public String instructionTypeString() {
    return getInstructionType() + "";
  }

  /* Returns the instruction’s symbol (string) */
  public String symbol() {
    return this.currentLine.replaceAll("[()@]", "");
  }

  /* Returns the instruction’s dest field (string) - Used only on C_INSTRUCTION */
  public String dest() {
    if (this.currentLine.contains("=")) {
      return this.code.dest(currentLine.split("=")[0]);
    }
    return this.code.dest("NULL");
  }

  /* Returns the instruction’s comp field (string) - Used only on C_INSTRUCTION */
  public String comp() {
    if (this.currentLine.contains("=")) {
      String temp = this.currentLine.split("=")[1];
      if (temp.contains(";")) {
        temp = temp.split(";")[0];
      }
      return this.code.comp(temp);
    }
    String temp = this.currentLine.split(";")[0];
    return this.code.comp(temp);
  }

  /* Returns the instruction’s jump field (string) - Used only on C_INSTRUCTION */
  public String jump() {
    if (this.currentLine.contains(";")) {
      return this.code.jump(currentLine.split(";")[1]);
    }
    return this.code.jump("NULL");
  }
}
