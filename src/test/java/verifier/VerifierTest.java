package verifier;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
			boolean pass = lexer.dumpLogs() && program != null;
			Assert.assertTrue("Program " + filename + " failed parsing.", pass);
            
            VerificationVisitor verifier = new VerificationVisitor();
            program.acceptVisitor(verifier);
			Assert.assertTrue("Program " + filename + " failed verification.", verifier.verifyCondition());
		} catch (IOException e) {
			Assert.fail("Could not open file " + filename);
		}
	}

    void testFail(String filename) {
		try {
			Lexer lexer = Lexer.make(filename);
			Parser parser = new Parser(lexer);
			ASTNode program = parser.parseProgram();
			boolean pass = lexer.dumpLogs() && program != null;
			Assert.assertTrue("Program " + filename + " failed parsing.", pass);

			VerificationVisitor verifier = new VerificationVisitor();
			program.acceptVisitor(verifier);
			Assert.assertFalse("Program " + filename + " passed verification.", verifier.verifyCondition());
		} catch (IOException e) {
			Assert.fail("Could not open file " + filename);
		}
	}

	void testTime(String filename){
		// warmup
		for (int i = 0; i < 10; i++){
			testPass(filename);
		}

		int numRuns = 30;
		long totalTimeNs = 0;
		for (int i = 0; i < numRuns; i++){
			final long begin = System.nanoTime();
			testPass(filename);
			final long end = System.nanoTime();
			totalTimeNs = totalTimeNs + (end - begin);
		}
		long totalTimeMs = TimeUnit.NANOSECONDS.toMillis(totalTimeNs);
		long avgTimeMs = totalTimeMs/numRuns;
		System.out.println("Average time for " + filename + " is " + avgTimeMs + "ms");
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
		testPass("src/test/java/verifier/pass/test11.txt");
		testPass("src/test/java/verifier/pass/test12.txt");
		testPass("src/test/java/verifier/pass/test15.txt");
		testPass("src/test/java/verifier/pass/test16a.txt");
		testPass("src/test/java/verifier/pass/test16c.txt");
		testPass("src/test/java/verifier/pass/test17a.txt");
		testPass("src/test/java/verifier/pass/test17b.txt");
		testPass("src/test/java/verifier/pass/test18.txt");
    }

    @Test
    public void fail() {
		testFail("src/test/java/verifier/fail/test1.txt");
		testFail("src/test/java/verifier/fail/test2.txt");
		testFail("src/test/java/verifier/fail/test3.txt");
		testFail("src/test/java/verifier/fail/test4.txt");
		testFail("src/test/java/verifier/fail/test5.txt");
		testFail("src/test/java/verifier/fail/test7.txt");
    }

	@Test
	public void testsTime(){
        testTime("src/test/java/verifier/pass/test1.txt");
		testTime("src/test/java/verifier/pass/test2.txt");
		testTime("src/test/java/verifier/pass/test3.txt");
		testTime("src/test/java/verifier/pass/test4.txt");
		testTime("src/test/java/verifier/pass/test5.txt");
		testTime("src/test/java/verifier/pass/test6.txt");
		testTime("src/test/java/verifier/pass/test7.txt");
		testTime("src/test/java/verifier/pass/test8.txt");
		testTime("src/test/java/verifier/pass/test9.txt");
		testTime("src/test/java/verifier/pass/test11.txt");
		testTime("src/test/java/verifier/pass/test12.txt");
		testTime("src/test/java/verifier/pass/test15.txt");
		testTime("src/test/java/verifier/pass/test16a.txt");
		testTime("src/test/java/verifier/pass/test16c.txt");
		testTime("src/test/java/verifier/pass/test17a.txt");
		testTime("src/test/java/verifier/pass/test17b.txt");
		testTime("src/test/java/verifier/pass/test18.txt");
	}
    
}
