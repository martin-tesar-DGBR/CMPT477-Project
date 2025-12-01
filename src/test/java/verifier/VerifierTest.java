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
		try {
			Lexer lexer = Lexer.make(filename);
			Parser parser = new Parser(lexer);
			ASTNode program = parser.parseProgram();
			boolean pass = lexer.dumpLogs() && program != null;
			Assert.assertTrue("Program " + filename + " failed parsing.", pass);
            
            VerificationVisitor verifier = new VerificationVisitor();
            program.acceptVisitor(verifier);
			Assert.assertTrue("Program " + filename + " failed verification.", verifier.verifyCondition());

			// warmup
			for (int i = 0; i < 10; i++){
				verifier = new VerificationVisitor();
				program.acceptVisitor(verifier);
				verifier.verifyCondition();
			}

			int numRuns = 30;
			long totalVCGenTimeNs = 0;
			long totalSolveTimeNs = 0;
			long totalTimeNs = 0;
			for (int i = 0; i < numRuns; i++){
				verifier = new VerificationVisitor();
				final long beginVCGen = System.nanoTime();
				program.acceptVisitor(verifier);
				final long endVCGen = System.nanoTime();

				final long beginZ3Solve = System.nanoTime();
				verifier.verifyCondition();
				final long endZ3Solve = System.nanoTime();
				
				totalVCGenTimeNs += endVCGen - beginVCGen;
				totalSolveTimeNs += endZ3Solve - beginZ3Solve;
				totalTimeNs +=  endVCGen + endZ3Solve - beginZ3Solve - beginVCGen;
			}

			long totalVCGenTimeMs = TimeUnit.NANOSECONDS.toMillis(totalVCGenTimeNs);
			long totalSolveTimeMs = TimeUnit.NANOSECONDS.toMillis(totalSolveTimeNs);
			long totalTimeMs = TimeUnit.NANOSECONDS.toMillis(totalTimeNs);
			long avgVCGenMs = totalVCGenTimeMs/numRuns;
			long avgSolveMs = totalSolveTimeMs/numRuns;
			long avgTimeMs = totalTimeMs/numRuns;
			System.out.println("Total average time for verifying " + filename + " is " + avgTimeMs + "ms");
			System.out.println("Average time for VC generation of " + filename + " is " + avgVCGenMs + "ms");
			System.out.println("Average time for Z3 solving " + filename + " is " + avgSolveMs + "ms");
			System.out.println();
		} catch (IOException e) {
			Assert.fail("Could not open file " + filename);
		}
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
		testPass("src/test/java/verifier/pass/test10.txt");
		testPass("src/test/java/verifier/pass/test11.txt");
		testPass("src/test/java/verifier/pass/test12.txt");
		testPass("src/test/java/verifier/pass/test13.txt");
		testPass("src/test/java/verifier/pass/test14.txt");
		testPass("src/test/java/verifier/pass/test15.txt");
		testPass("src/test/java/verifier/pass/test16a.txt");
		testPass("src/test/java/verifier/pass/test16b.txt");
		testPass("src/test/java/verifier/pass/test16c.txt");
		testPass("src/test/java/verifier/pass/test16d.txt");
		testPass("src/test/java/verifier/pass/test16e.txt");
		testPass("src/test/java/verifier/pass/test16f.txt");
		testPass("src/test/java/verifier/pass/test16g.txt");
		testPass("src/test/java/verifier/pass/test16h.txt");
    }

    @Test
    public void fail() {
		testFail("src/test/java/verifier/fail/test1.txt");
		testFail("src/test/java/verifier/fail/test2.txt");
		testFail("src/test/java/verifier/fail/test3.txt");
		testFail("src/test/java/verifier/fail/test4.txt");
		testFail("src/test/java/verifier/fail/test5.txt");
		testFail("src/test/java/verifier/fail/test6.txt");
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
		testTime("src/test/java/verifier/pass/test10.txt");
		testTime("src/test/java/verifier/pass/test11.txt");
		testTime("src/test/java/verifier/pass/test12.txt");
		testTime("src/test/java/verifier/pass/test13.txt");
		testTime("src/test/java/verifier/pass/test14.txt");
		testTime("src/test/java/verifier/pass/test15.txt");
		// testTime("src/test/java/verifier/pass/test16a.txt"); // 1
		// testTime("src/test/java/verifier/pass/test16b.txt"); // 10
		// testTime("src/test/java/verifier/pass/test16c.txt"); // 50
		// testTime("src/test/java/verifier/pass/test16d.txt"); // 100
		// testTime("src/test/java/verifier/pass/test16e.txt"); // 200
		// testTime("src/test/java/verifier/pass/test16f.txt"); // 500
		// testTime("src/test/java/verifier/pass/test16g.txt"); // 1000
		// testTime("src/test/java/verifier/pass/test16h.txt"); // 1500
	}
    
}
