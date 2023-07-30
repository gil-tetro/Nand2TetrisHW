import java.util.HashMap;

/* Generates binary code for a given C command 
*  comp(string): Returns the binary representation of the parsed comp field (string)
 * dest(string): Returns the binary representation of the parsed dest field (string)
 * jump(string): Returns the binary representation of the parsed jump field (string)
 * according to the HACK languege specification.
*/
public class Code {
  // Create HashMaps for each section of a C instruction
  private HashMap<String, String> Comp;
  private HashMap<String, String> Dest;
  private HashMap<String, String> Jump;

  public Code() {
    // Create and fill the Comp table with the predifined mnemonics
    this.Comp = new HashMap<String, String>();
    Comp.put("0", "0101010");
    Comp.put("1", "0111111");
    Comp.put("-1", "0111010");
    Comp.put("D", "0001100");
    Comp.put("A", "0110000");
    Comp.put("M", "1110000");
    Comp.put("!D", "0001101");
    Comp.put("!A", "0110001");
    Comp.put("!M", "1110001");
    Comp.put("-D", "0001111");
    Comp.put("-A", "0110011");
    Comp.put("-M", "1110011");
    Comp.put("D+1", "0011111");
    Comp.put("A+1", "0110111");
    Comp.put("M+1", "1110111");
    Comp.put("D-1", "0001110");
    Comp.put("A-1", "0110010");
    Comp.put("M-1", "1110010");
    Comp.put("D+A", "0000010");
    Comp.put("D+M", "1000010");
    Comp.put("D-A", "0010011");
    Comp.put("D-M", "1010011");
    Comp.put("A-D", "0000111");
    Comp.put("M-D", "1000111");
    Comp.put("D&A", "0000000");
    Comp.put("D&M", "1000000");
    Comp.put("D|A", "0010101");
    Comp.put("D|M", "1010101");

    // Create and fill the Dest table with the predifined mnemonics
    this.Dest = new HashMap<String, String>();
    Dest.put("NULL", "000");
		Dest.put("M", "001");
		Dest.put("D", "010");
		Dest.put("MD", "011");
		Dest.put("A", "100");
		Dest.put("AM", "101");
		Dest.put("AD", "110");
		Dest.put("AMD", "111");

    // Create and fill the Jump table with the predifined mnemonics
    this.Jump = new HashMap<String, String>();
    Jump.put("NULL", "000");
		Jump.put("JGT", "001");
		Jump.put("JEQ", "010");
		Jump.put("JGE", "011");
		Jump.put("JLT", "100");
		Jump.put("JNE", "101");
		Jump.put("JLE", "110");
		Jump.put("JMP", "111");
  }
  

  //  The Code getter functions:

  /* Returns the binary representation of the parsed comp field (string) */
  public String comp(String mnemonic) {
    return this.Comp.get(mnemonic);
  }

  /* Returns the binary representation of the parsed dest field (string) */
  public String dest(String mnemonic) {
    return this.Dest.get(mnemonic);
  }
  /* Returns the binary representation of the parsed jump field (string) */
  public String jump(String mnemonic) {
    return this.Jump.get(mnemonic);
  }
}
