import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {

    private static FileWriter output;

    private static int lines = 0;

    private static String name;

    private static int labelNum = 0;

    String pushStr = "D=M\n@SP\nA=M\nM=D\n@SP\nM=M+1"; //a pattern that was repeated alot in the code thus changed into a variable for elegance

    public CodeWriter(String input) throws IOException {
        output = new FileWriter(input);   //create and output file to write into using the path we received
    }

    public void setFileName(String name) {
        this.name = name; // saves the file name without the path
    }

    public void writeArithmetic(String str) throws IOException {
        if (str.equalsIgnoreCase("add") || str.equalsIgnoreCase("sub")) {
            write2output("@SP");
            write2output("A=M-1");
            write2output("D=M");
            write2output("A=A-1");

            if (str.equalsIgnoreCase("add")) {
                write2output("M=M+D");
                write2output("D=A+1");
                write2output("@SP");
                write2output("M=D");
            } else if (str.equalsIgnoreCase("sub")) {
                write2output("M=M-D");
                write2output("D=A+1");
                write2output("@SP");
                write2output("M=D");
            }
        } else if (str.equalsIgnoreCase("and") || str.equalsIgnoreCase("or")) {
            write2output("@SP");
            write2output("AM=M-1");
            write2output("D=M");
            write2output("A=A-1");

            if (str.equalsIgnoreCase("and")) {
                write2output("M=D&M");
            } else if (str.equalsIgnoreCase("or")) {
                write2output("M=D|M");
            }

        } else if (str.equalsIgnoreCase("eq") || str.equalsIgnoreCase("lt")
                || str.equalsIgnoreCase("gt")) {
            write2output("@SP");
            write2output("M=M-1");
            write2output("A=M");
            write2output("D=M");
            write2output("A=A-1");
            write2output("D=M-D");
            write2output("@jump" + lines);

            if (str.equalsIgnoreCase("eq")) {
                write2output("D;JEQ");
            } else if (str.equalsIgnoreCase("gt")) {
                write2output("D;JGT");
            } else if (str.equalsIgnoreCase("lt")) {
                write2output("D;JLT");
            }

            write2output("@SP");
            write2output("A=M-1");
            write2output("M=0");
            write2output("@END" + lines);
            write2output("0;JMP");
            write2output("(jump" + lines + ")");
            write2output("@SP");
            write2output("A=M-1");
            write2output("M=-1");
            write2output("(END" + lines + ")");
            lines++;

        } else if (str.equalsIgnoreCase("not")) {
            write2output("@SP");
            write2output("A=M-1");
            write2output("D=M");
            write2output("M=!M");

        } else if (str.equalsIgnoreCase("neg")) {
            write2output("@SP");
            write2output("A=M-1");
            write2output("D=M");
            write2output("M=-M");
        }
    }

    public void writePushPop(String command, String segment, String index) throws IOException {
        if (command.equals("C_PUSH")) {
            if (segment.equals("pointer")) {
                if (index.equals("0")) {
                    write2output("@THIS");
                } else if (index.equals("1")) {
                    write2output("@THAT");
                }
                write2output("D=M");
            } else if (segment.equals("static")) {
                write2output("@" + name + "." + index);
                write2output("D=M");
            } else if (segment.equals("temp")) {
                write2output("@R5");
                write2output("D=A");
                write2output("@" + index);
                write2output("A=D+A");
                write2output("D=M");
            } else if (segment.equals("constant")) {
                write2output("@" + index);
                write2output("D=A");
            } else if (segment.equals("local")) {
                write2output("@LCL");
                write2output("D=M");
                write2output("@" + index);
                write2output("A=D+A");
                write2output("D=M");
            } else if (segment.equals("argument")) {
                write2output("@ARG");
                write2output("D=M");
                write2output("@" + index);
                write2output("A=D+A");
                write2output("D=M");
            } else if (segment.equals("this")) {
                write2output("@THIS");
                write2output("D=M");
                write2output("@" + index);
                write2output("A=D+A");
                write2output("D=M");
            } else if (segment.equals("that")) {
                write2output("@THAT");
                write2output("D=M");
                write2output("@" + index);
                write2output("A=D+A");
                write2output("D=M");
            }
            write2output("@SP");
            write2output("A=M");
            write2output("M=D");
            write2output("@SP");
            write2output("M=M+1");
        } else if (command.equals("C_POP")) {
            if (segment.equals("pointer")) {
                if (index.equals("0")) {
                    write2output("@THIS");
                } else if (index.equals("1")) {
                    write2output("@THAT");
                }
                write2output("D=A");
            } else if (segment.equals("local")) {
                write2output("@LCL");
                write2output("D=M");
                write2output("@" + index);
                write2output("D=D+A");
            } else if (segment.equals("argument")) {
                write2output("@ARG");
                write2output("D=M");
                write2output("@" + index);
                write2output("D=D+A");
            } else if (segment.equals("this")) {
                write2output("@THIS");
                write2output("D=M");
                write2output("@" + index);
                write2output("D=D+A");
            } else if (segment.equals("that")) {
                write2output("@THAT");
                write2output("D=M");
                write2output("@" + index);
                write2output("D=D+A");
            } else if (segment.equals("static")) {
                write2output("@" + name + "." + index);
                write2output("D=A");
            } else if (segment.equals("temp")) {
                write2output("@R5");
                write2output("D=A");
                write2output("@" + index);
                write2output("D=D+A");
            }
            write2output("@R13");
            write2output("M=D");
            write2output("@SP");
            write2output("AM=M-1");
            write2output("D=M");
            write2output("@R13");
            write2output("A=M");
            write2output("M=D");
        }
    }

    public void writeLabel(String label) throws IOException {
        write2output("(" + label + ")");
    }

    public void writeGoto(String label) throws IOException {
        write2output("@" + label);
        write2output("0;JMP");
    }

    public void writeIf(String label) throws IOException {
        write2output("@SP");
        write2output("AM=M-1");
        write2output("D=M");
        write2output("A=A-1");
        write2output("@" + label);
        write2output("D;JNE");
    }

    public void writeFunction(String funcName, int nVars) throws IOException {
        write2output("(" + funcName + ")");
        for (int i = 0; i < nVars; i++) {
            writePushPop("C_PUSH", "constant", "0");
        }
    }

    public void writeCall(String funcName, int nArgs) throws IOException {
        String label = "RETURN_LABEL" + labelNum;
        labelNum++;
        write2output("@" + label);
        write2output("D=A");
        write2output("@SP");
        write2output("A=M");
        write2output("M=D");
        write2output("@SP");
        write2output("M=M+1");
        write2output("@LCL");
        write2output(pushStr);
        write2output("@ARG");
        write2output(pushStr);
        write2output("@THIS");
        write2output(pushStr);
        write2output("@THAT");
        write2output(pushStr);
        write2output("@SP");
        write2output("D=M");
        write2output("@5");
        write2output("D=D-A");
        write2output("@" + nArgs);
        write2output("D=D-A");
        write2output("@ARG");
        write2output("M=D");
        write2output("@SP");
        write2output("D=M");
        write2output("@LCL");
        write2output("M=D");
        write2output("@" + funcName);
        write2output("0;JMP");
        write2output("(" + label + ")");
    }

    public void writeReturn() throws IOException {
        write2output("@LCL");
        write2output("D=M");
        write2output("@FRAME");
        write2output("M=D");
        write2output("@5");
        write2output("A=D-A");
        write2output("D=M");
        write2output("@RET");
        write2output("M=D");
        write2output("@ARG");
        write2output("D=M");
        write2output("@0");
        write2output("D=D+A");
        write2output("@R13");
        write2output("M=D");
        write2output("@SP");
        write2output("AM=M-1");
        write2output("D=M");
        write2output("@R13");
        write2output("A=M");
        write2output("M=D");
        write2output("@ARG");
        write2output("D=M");
        write2output("@SP");
        write2output("M=D+1");
        write2output("@FRAME");
        write2output("D=M-1");
        write2output("AM=D");
        write2output("D=M");
        write2output("@THAT");
        write2output("M=D");
        write2output("@FRAME");
        write2output("D=M-1");
        write2output("AM=D");
        write2output("D=M");
        write2output("@THIS");
        write2output("M=D");
        write2output("@FRAME");
        write2output("D=M-1");
        write2output("AM=D");
        write2output("D=M");
        write2output("@ARG");
        write2output("M=D");
        write2output("@FRAME");
        write2output("D=M-1");
        write2output("AM=D");
        write2output("D=M");
        write2output("@LCL");
        write2output("M=D");
        write2output("@RET");
        write2output("A=M");
        write2output("0;JMP");
    }
    public void writeInit() throws IOException {
        write2output("@256");
        write2output("D=A");
        write2output("@SP");
        write2output("M=D");
        writeCall("Sys.init",0);
    }

    private void write2output(String str) throws IOException {
        output.write(str + "\n");
    }

    public void close() throws IOException {
        output.close();
    }
}
