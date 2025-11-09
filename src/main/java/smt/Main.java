package smt;

import com.microsoft.z3.*;
import lexer.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Lexer lexer;
        try {
            lexer = Lexer.make(args[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (lexer.hasNext()) {
            Token t = lexer.next();
            System.out.println(t.getClass().toString() + ": " + t.getLexeme());
        }
    }
}
