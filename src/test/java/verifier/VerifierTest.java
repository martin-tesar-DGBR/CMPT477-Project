package verifier;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import verifier.VerificationVisitor;

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
            
            VerificationVisitor verifier = new VerificationVisitor();
            program.acceptVisitor(verifier);
			Assert.assertTrue("Program " + filename + " failed verification.", verifier.verifyCondition());
		} catch (IOException e) {
			Assert.fail("Could not open file " + filename);
		}
		Logger.clearLogs();
	}

    void testFail(String filename) {
		try {
			Lexer lexer = Lexer.make(filename);
			Parser parser = new Parser(lexer);
			ASTNode program = parser.parseProgram();
			boolean pass = Logger.get(LogType.LEXER).dump() == LogLevel.DEBUG && Logger.get(LogType.PARSER).dump() == LogLevel.DEBUG;
			Assert.assertTrue("Program " + filename + " failed parsing.", pass);

			VerificationVisitor verifier = new VerificationVisitor();
			program.acceptVisitor(verifier);
			Assert.assertFalse("Program " + filename + " passed verification.", verifier.verifyCondition());
		} catch (IOException e) {
			Assert.fail("Could not open file " + filename);
		}
		Logger.clearLogs();
	}

    @Test
    public void pass() {
        testPass("src/test/java/verifier/pass/test1.txt");
		testPass("src/test/java/verifier/pass/test2.txt");
		testPass("src/test/java/verifier/pass/test3.txt");
		testPass("src/test/java/verifier/pass/test4.txt");
		testPass("src/test/java/verifier/pass/test5.txt");
		testPass("src/test/java/verifier/pass/test6.txt");
		testPass("src/test/java/verifier/pass/test7.txt");
		testPass("src/test/java/verifier/pass/test8.txt");
		testPass("src/test/java/verifier/pass/test9.txt");
    }

    @Test
    public void fail() {
		testFail("src/test/java/verifier/fail/test1.txt");
		testFail("src/test/java/verifier/fail/test2.txt");
		testFail("src/test/java/verifier/fail/test3.txt");
		testFail("src/test/java/verifier/fail/test4.txt");
		testFail("src/test/java/verifier/fail/test5.txt");
    }
    
}
