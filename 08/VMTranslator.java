import java.io.File;
import java.io.FileFilter;
import java.io.IOException;


public class VMTranslator {

    public static void main(String[] args) throws IOException {
        FileFilter filter = (file) -> file.getName().endsWith(".vm");
        File input = new File(args[0]);
        File[] files;
        CodeWriter codeWriter;
        if (input.isDirectory()){
            files = input.listFiles(filter);
            codeWriter  = new CodeWriter(args[0] + "/" + args[0].substring(args[0].lastIndexOf("/"))+".asm");
            codeWriter.writeInit();
        }
        else {
            files = new File[]{input};
            codeWriter  = new CodeWriter(args[0].replace(".vm",".asm"));
        }

        for (File file : files){
            Parser parser = new Parser(file);
            codeWriter.setFileName(file.getName().replace(".vm",""));
            while (parser.hasMoreLine()) {
                parser.advance();
                if (parser.commandType().equals("C_PUSH")) {
                    codeWriter.writePushPop("C_PUSH", parser.arg1(), Integer.toString(parser.arg2()));
                }
                else if (parser.commandType().equals("C_POP")) {
                    codeWriter.writePushPop("C_POP", parser.arg1(), Integer.toString(parser.arg2()));
                }
                else if (parser.commandType().equals("C_ARITHMETIC")) {
                    codeWriter.writeArithmetic(parser.arg1());
                }
                else if (parser.commandType().equals("C_LABEL")){
                    codeWriter.writeLabel(parser.arg1());
                }
                else if (parser.commandType().equals("C_GOTO")){
                    codeWriter.writeGoto(parser.arg1());
                }
                else if (parser.commandType().equals("C_IF")){
                    codeWriter.writeIf(parser.arg1());
                }
                else if (parser.commandType().equals("C_FUNCTION")){
                    codeWriter.writeFunction(parser.arg1(), parser.arg2());
                }
                else if (parser.commandType().equals("C_RETURN")){
                    codeWriter.writeReturn();
                }
                else if (parser.commandType().equals("C_CALL")){
                    codeWriter.writeCall(parser.arg1(), parser.arg2());
                }
            }
        }
        codeWriter.close();
    }
}