import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Parser {
    private Scanner scanner;
    private String currIns;
    private CommandTable comTab = new CommandTable();

    //sets the scanner to read from the input file to be translated to a hack program
    public Parser(File file) throws FileNotFoundException {
        this.scanner = new Scanner(file);
    }

    //returns true if the file has another line to read
    public boolean hasMoreLine() {
        return scanner.hasNextLine();
    }

    public void advance() { // advances the parser to a valid line to work with
        if (hasMoreLine()) {
            String str = scanner.nextLine().trim();
            if (str.equals("\n") || str.equals(" ") || str.isEmpty()) {
                advance();
            } else if (str.startsWith("//")) {
                advance();
            } else if (str.contains("//")) {
                currIns = str.split("//")[0].trim();
            } else {
                currIns = str;
            }
        }
    }

    public String commandType() { //returns the command type of the current instruction
        if (currIns != null) {
            return comTab.getComType(currIns.split(" ")[0]);
        }
        return null;
    }

    public String arg1() { //returns "segment" or the command if a segment does not exist
        if (!this.currIns.isEmpty()) {
            if (comTab.getComType(this.currIns.split(" ")[0]).equals("C_ARITHMETIC")) {
                return this.currIns.split(" ")[0];
            } else if (!comTab.getComType(this.currIns.split(" ")[0]).equals("C_RETURN")) {
                return this.currIns.split(" ")[1];
            }
        }
        return null;
    }

    public int arg2() { //returns the second argument of the current command
        if (!this.currIns.isEmpty()) {
            return Integer.parseInt(this.currIns.split(" ")[2]);
        }
        return -1;
    }
}
