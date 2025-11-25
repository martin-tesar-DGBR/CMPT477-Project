package verifier;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import verifier.Verifier;

import ast.ASTNode;
import ast.Parser;
import lexer.Lexer;
import logging.*;

public class VerifierTest {
    void testPass(String filename) {
		try {
			Lexer lexer = Lexer.make(filename);
			Parser parser = new Parser(lexer);
			ASTNode program = parser.parseProgram();
			boolean pass = Logger.get(LogType.LEXER).dump() == LogLevel.DEBUG && Logger.get(LogType.PARSER).dump() == LogLevel.DEBUG;
			Assert.assertTrue("Program " + filename + " failed parsing.", pass);
            
            Verifier verifier = new Verifier();
            verifier.run(program);
		} catch (IOException e) {
			Assert.fail("Could not open file " + filename);
		}
		Logger.clearLogs();
	}

    void testFail(String filename, String expectedMsg) {
		try {
			Lexer lexer = Lexer.make(filename);
			Parser parser = new Parser(lexer);
			ASTNode program = parser.parseProgram();
			boolean pass = Logger.get(LogType.LEXER).dump() == LogLevel.DEBUG && Logger.get(LogType.PARSER).dump() == LogLevel.DEBUG;
			Assert.assertTrue("Program " + filename + " failed parsing.", pass);
            
			try {
				Verifier verifier = new Verifier();
            	verifier.run(program);
				Assert.fail("Expected IllegalArgumentException to be thrown");
			} catch (IllegalArgumentException e) {
				Assert.assertEquals(e.getMessage(),expectedMsg);
			}
		} catch (IOException e) {
			Assert.fail("Could not open file " + filename);
		}
		Logger.clearLogs();
	}

    @Test
    public void pass() {
		System.out.println("Starting verifier pass tests...");
        testPass("src/test/java/verifier/pass/test1.txt");
		testPass("src/test/java/verifier/pass/test2.txt");//
		testPass("src/test/java/verifier/pass/test3.txt");
		testPass("src/test/java/verifier/pass/test4.txt");
		testPass("src/test/java/verifier/pass/test5.txt");//
		testPass("src/test/java/verifier/pass/test6.txt");
		testPass("src/test/java/verifier/pass/test7.txt");
		testPass("src/test/java/verifier/pass/test8.txt");
    }

    @Test
    public void fail() {
		System.out.println("Starting verifier fail tests...");
		testFail(
			"src/test/java/verifier/fail/test1.txt",
			"Check at line 2 failed"
		);
		testFail(
			"src/test/java/verifier/fail/test2.txt",
			"Check at line 6 failed"
		);
		testFail(
			"src/test/java/verifier/fail/test3.txt",
			"Check at line 10 failed"
		);
		testFail(
			"src/test/java/verifier/fail/test4.txt",
			"Check at line 5 failed"
		);
    }
    
}
