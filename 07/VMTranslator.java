import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Constructs a Parser to handle the input file
 * Constructs a CodeWriter to handle the output file
 * Marches through the input file, parsing each line and generating code from
 * it.
 */
public class VMTranslator {
  /**
   * Get .vm files from a given directory
   * 
   * @param dir
   * @return
   *         arrayList of files
   */
  public static ArrayList<File> getVMfiles(File dir) throws FileNotFoundException {
    ArrayList<File> vmFiles = new ArrayList<File>();
    File[] files = dir.listFiles();

    for (File file : files) {
      if (file.getName().endsWith(".vm")) {
        vmFiles.add(file);
      }
    }
    return vmFiles;
  }

  public static void main(String[] args) {
    // initialize the required services for the VM translator
    File inputFile = new File(args[0]);
    File outputFile;

    String outputPath = "";
    ArrayList<File> vmFiles = new ArrayList<File>();

    CodeWriter codeWriter;

    // file validation
    if (inputFile.isFile()) {
      String absPath = inputFile.getAbsolutePath();

      if (!Parser.getFileExtension(absPath).equals(".vm")) {
        throw new IllegalArgumentException("No vm file found");
      }

      vmFiles.add(inputFile);
      // set output path
      outputPath = absPath.substring(0, absPath.lastIndexOf(".")) + ".asm";
    } else if (inputFile.isDirectory()) {
      try {
        // get all files from the directory
        vmFiles = getVMfiles(inputFile);

        if (vmFiles.isEmpty()) {
          throw new IllegalArgumentException("No vm file found");
        }
        // set output path
        outputPath = inputFile.getAbsolutePath() + "/" + inputFile.getName() + ".asm";
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    // create and write into the output file
    outputFile = new File(outputPath);
    codeWriter = new CodeWriter(outputFile);

    for (File file : vmFiles) {
      Parser parser = new Parser(file);
      int commandType = -1;

      while (parser.hasMoreLines()) {
        parser.advance();
        commandType = parser.CommandType();
        // handle ARITHMETIC commands
        if (commandType == Parser.commandTypes.get("ARITHMETIC")) {
          codeWriter.writeArithmetic(parser.arg1());
          // handle PUSH / POP commands
        } else if (commandType == Parser.commandTypes.get("POP") || commandType == Parser.commandTypes.get("PUSH")) {
          codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
        }
      }
    }

    // close the output file
    codeWriter.close();
  }
}