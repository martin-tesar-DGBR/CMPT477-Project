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

    /**
     * Helper to run a program from a file and capture its stdout.
     * Fails if parsing fails.
     */
    private String interpretFile(String filename) throws IOException {
        // capture System.out
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        try {
            Lexer lexer = Lexer.make(filename);
            Parser parser = new Parser(lexer);
            ASTNode program = parser.parseProgram();

            boolean parsedOk =
                    Logger.get(LogType.LEXER).dump() == LogLevel.DEBUG &&
                    Logger.get(LogType.PARSER).dump() == LogLevel.DEBUG;

            Assert.assertTrue("Program " + filename + " failed parsing.", parsedOk);
            Logger.clearLogs();

            Interpreter interpreter = new Interpreter();
            interpreter.run(program);

        } finally {
            System.out.flush();
            System.setOut(originalOut);
            Logger.clearLogs();
        }

        return baos.toString().replace("\r\n", "\n");
    }

    // ---------- PASS TESTS ----------

    @Test
    public void simpleAssignAndPrint() throws IOException {
        String out = interpretFile("src/test/java/interpreter/pass/test1.txt");
        Assert.assertEquals("1\n2\n", out);
    }

    @Test
    public void ifElseExecution() throws IOException {
        String out = interpretFile("src/test/java/interpreter/pass/test2.txt");
        Assert.assertEquals("8\n", out);
    }

    // ---------- FAIL TESTS ----------

    @Test
    public void useBeforeAssignFailsAtRuntime() throws IOException {
        try {
            interpretFile("src/test/java/interpreter/fail/test1.txt");
            Assert.fail("Program should have failed at runtime (use-before-assign).");
        } catch (RuntimeException e) {
        }
    }
}
