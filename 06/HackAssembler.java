import java.io.*;

/* The Assembler uses the services of the SymbolTable, Code and Parser classes  */

public class HackAssembler {
  private static int lineCounter = 0;
  private static int nextVarIndex = 16;

  public static void main(String[] args) throws Exception {
    // initialize source and output file names
    String source = args[0].replaceFirst(".+\\\\", ""); // filename with extention
    source = source.substring(0, source.lastIndexOf('.'));
    String output = source + ".hack";

    // initialize the requiered services for the HackAssembler
    FileWriter outFile = new FileWriter(output);
    SymbolTable symboltable = new SymbolTable();
    Parser parser1 = new Parser(new File(args[0]));
    Parser parser2 = new Parser(new File(args[0]));

    scan1(parser1, symboltable);
    scan2(parser2, symboltable, outFile);
  }

  /*
   * Reads the program lines, one by one focusing only on (label) declarations.
   * Adds the found labels to the symbol table
   */
  private static void scan1(Parser parser, SymbolTable symboltable) {
    while (parser.hasMoreLines()) {
      parser.advance();
      if (parser.instructionTypeString().equals("L_INSTRUCTION")) {
        symboltable.addEntry(parser.symbol(), lineCounter);
      } else {
        lineCounter++;
      }
    }
  }

  /*
   * (starts again from the beginning of the file)
   * While there are more lines to process:
   * Gets the next instruction, and parses it
   * If the instruction is @ symbol
   * If symbol is not in the symbol table, adds it
   * Translates the symbol into its binary value
   * If the instruction is dest =comp ; jump
   * Translates each of the three fields into its binary value
   * Assembles the binary values into a string of sixteen 0’s and 1’s
   * Writes the string to the output file
   */
  private static void scan2(Parser parser, SymbolTable symboltable, FileWriter outFile) throws Exception {
    int symbol = 0;
    while (parser.hasMoreLines()) {
      parser.advance();
      // handle C_INSTRUCTIONs
      if (parser.instructionTypeString().equals("C_INSTRUCTION")) {
        // build the output line for the output file
        StringBuilder outputLine = new StringBuilder();
        outputLine.append("111");
        outputLine.append(parser.comp());
        outputLine.append(parser.dest());
        outputLine.append(parser.jump());

        if (parser.hasMoreLines()) {
          outputLine.append("\n");
        }
        outFile.write(outputLine.toString());
      } else if (parser.instructionTypeString().equals("A_INSTRUCTION")) {
        // handle the case of an A instruction with a symbolic name
        if (Character.isLetter(parser.symbol().charAt(0))) {
          if (!symboltable.contains(parser.symbol())) {
            symboltable.addEntry(parser.symbol(), nextVarIndex);
            nextVarIndex++;
          }
          // hendle the case on an A instruction with numbers
          if (parser.hasMoreLines()) {
            outFile.write("0" + String.format("%15s", Integer.toBinaryString(symboltable.getAddress(parser.symbol())))
                .replaceAll(" ", "0") + "\n");
          } else {
            outFile.write("0" + String.format("%15s", Integer.toBinaryString(symboltable.getAddress(parser.symbol())))
                .replaceAll(" ", "0"));
          }
        } else {
          symbol = Integer.parseInt(parser.symbol());
          symboltable.addEntry(parser.symbol(), symbol);
          if (parser.hasMoreLines()) {
            outFile.write("0" + String.format("%15s", Integer.toBinaryString(symbol)).replaceAll(" ", "0") + "\n");
          } else {
            outFile.write("0" + String.format("%15s", Integer.toBinaryString(symbol)).replaceAll(" ", "0"));
          }
        }
      }
    }
    outFile.close();
  }
}
