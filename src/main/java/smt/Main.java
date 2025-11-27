package smt;

import ast.*;
import interpret.Interpreter;
import lexer.*;
import logging.*;
import validate.UsageVisitor;
import verifier.VerificationVisitor;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Lexer lexer;
        try {
            lexer = Lexer.make(args[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
        while (lexer.hasNext()) {
            lexer.peek();
            System.out.println(lexer.next().getLexeme());
        }
        //*/

        Parser parser = new Parser(lexer);
        ASTNode root = parser.parseProgram();
        if (Logger.get(LogType.LEXER).dump() != LogLevel.DEBUG || Logger.get(LogType.PARSER).dump() != LogLevel.DEBUG) {
            return;
        }
        else {
            PrintVisitor visitor = new PrintVisitor();
            root.acceptVisitor(visitor);
            UsageVisitor usageVisitor = new UsageVisitor();
            root.acceptVisitor(usageVisitor);
            if (Logger.get(LogType.VERIFIER).dump() != LogLevel.DEBUG) {
                return;
            }
            Logger.clearLogs();

            VerificationVisitor verificationVisitor = new VerificationVisitor();
            root.acceptVisitor(verificationVisitor);

            if (!verificationVisitor.verifyCondition()) {
                return;
            }
            Logger.clearLogs();

            Interpreter interpreter = new Interpreter();
            interpreter.run(root);
        }
    }
}
