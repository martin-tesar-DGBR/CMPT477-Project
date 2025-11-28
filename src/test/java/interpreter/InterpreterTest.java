package interpreter;

import ast.ASTNode;
import ast.Parser;
import interpret.Interpreter;
import lexer.Lexer;
import logging.LogLevel;
import logging.LogType;
import logging.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class InterpreterTest {

   
    private String interpretFile(String filename) throws IOException {
        return interpretFileWithStdoutAndStderr(filename)[0];
    }

    private String[] interpretFileWithStdoutAndStderr(String filename) throws IOException {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
        ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
        PrintStream outPs = new PrintStream(outBaos);
        PrintStream errPs = new PrintStream(errBaos);

        System.setOut(outPs);
        System.setErr(errPs);

        try {
            Lexer lexer = Lexer.make(filename);
            Parser parser = new Parser(lexer);
            ASTNode program = parser.parseProgram();

            boolean parsedOk = lexer.dumpLogs() && program != null;

            Assert.assertTrue("Program " + filename + " failed parsing.", parsedOk);

            Interpreter interpreter = new Interpreter();
            interpreter.run(program);

        } finally {
            System.out.flush();
            System.err.flush();
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        String out = outBaos.toString().replace("\r\n", "\n");
        String err = errBaos.toString().replace("\r\n", "\n");
        return new String[]{out, err};
    }

    // ---------- existing PASS tests ----------

    @Test
    public void simpleAssignAndPrint() throws IOException {
        String out = interpretFile("src/test/java/interpreter/pass/test1.txt");
        Assert.assertEquals("x: 1\ny: 2\n", out);
    }

    @Test
    public void ifElseExecution() throws IOException {
        String out = interpretFile("src/test/java/interpreter/pass/test2.txt");
        Assert.assertEquals("z: 8\n", out);
    }

    // ---------- existing FAIL test ----------

    @Test
    public void checkPasses_NoErrorPrinted() throws IOException {
        String[] outErr = interpretFileWithStdoutAndStderr(
                "src/test/java/interpreter/pass/test3.txt");
        Assert.assertEquals("x: 5\n", outErr[0]);
        Assert.assertEquals("", outErr[1]);          
    }
}
