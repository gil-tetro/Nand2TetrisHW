import java.util.HashMap;

public class SymbolTable {
  // Create a HashMap to hold the actual symbol table
  public HashMap<String, Integer> SymbolTable;

  /*
   * Creates and initializes a SymbolTable with the fields symbol and address, and
   * initializes the built in HACK symbols
   */
  public SymbolTable() {
    SymbolTable = new HashMap<String, Integer>();

    SymbolTable.put("R0", 0);
    SymbolTable.put("R1", 1);
    SymbolTable.put("R2", 2);
    SymbolTable.put("R3", 3);
    SymbolTable.put("R4", 4);
    SymbolTable.put("R5", 5);
    SymbolTable.put("R6", 6);
    SymbolTable.put("R7", 7);
    SymbolTable.put("R8", 8);
    SymbolTable.put("R9", 9);
    SymbolTable.put("R10", 10);
    SymbolTable.put("R11", 11);
    SymbolTable.put("R12", 12);
    SymbolTable.put("R13", 13);
    SymbolTable.put("R14", 14);
    SymbolTable.put("R15", 15);
    SymbolTable.put("SCREEN", 16384);
    SymbolTable.put("KBD", 24576);
    SymbolTable.put("SP", 0);
    SymbolTable.put("LCL", 1);
    SymbolTable.put("ARG", 2);
    SymbolTable.put("THIS", 3);
    SymbolTable.put("THAT", 4);
  }

  /* Adds <symbol, address> to the table */
  public void addEntry(String symbol, int address) {
    SymbolTable.put(symbol, address);
  }

  /* Check if symbol exists in the table */
  public boolean contains(String symbol) {
    return SymbolTable.containsKey(symbol);
  }

  /* Return the address associated with the symbol */
  public int getAddress(String symbol) {
    return SymbolTable.get(symbol);
  }
}
