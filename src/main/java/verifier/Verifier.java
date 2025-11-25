package verifier;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import ast.*;
import lexer.StaticToken;

public class Verifier {

    public void run(ASTNode root) {
        ArrayList<ASTNode> currentStmts = new ArrayList<>();
        if (!(root instanceof BlockNode)) {
            throw new IllegalArgumentException("Program root must be a BlockNode");
        }
        executeBlock((BlockNode) root, currentStmts);
    }

    public void executeBlock(BlockNode block,ArrayList<ASTNode> currentStmts) {
        List<ASTNode> statements = block.children;
        if (statements == null) return;

        for (ASTNode stmt : statements) {
            executeStatement(stmt,currentStmts);
        }
    }

    private void executeAssignment(AssignmentNode node,ArrayList<ASTNode> currentStmts) {
        currentStmts.add(node);
    }

    private void executeIf(IfNode node,ArrayList<ASTNode> currentStmts) {
        BoolOperatorNode notC = new BoolOperatorNode(
            StaticToken.NOT,
            node.cond,
            null 
        );

        ArrayList<ASTNode> currThenStmts = new ArrayList<>(currentStmts);
        ArrayList<ASTNode> currElseStmts = new ArrayList<>(currentStmts);

        currThenStmts.add(node.cond);
        currElseStmts.add(notC);

        executeBlock(node.branchThen, currThenStmts);
        executeBlock(node.branchElse, currElseStmts);

        currentStmts.add(node);
    }

    private Status checkWP(ASTNode wpNode)
    {
        ASTNodeToZ3Convertor convertor = new ASTNodeToZ3Convertor();
        Context ctx = new Context();
        BoolExpr wp = convertor.convert(wpNode,ctx);

        Solver solver = ctx.mkSolver();
        BoolExpr negation = ctx.mkNot(wp);
        solver.add(negation);

        Status s = solver.check();

        return s;
    }

    private void executeCheck(CheckNode check,ArrayList<ASTNode> currentStmts) {
        PrintVisitor v = new PrintVisitor();
        WPFinder wpFinder = new WPFinder();
        ASTNode wpNode = null;
        ASTNode post = check.expr;
        
        for (int i = currentStmts.size()-1; i >= 0; i--) {
            wpNode = wpFinder.findWP(currentStmts.get(i), post);
            post = wpNode;
        }

        if (wpNode == null){
            // case if check is by itself 
            // with no statements before it
            wpNode = check.expr;
        } 

        Status s = checkWP(wpNode);

        String line = Integer.toString(check.lexeme.line);
        String col = Integer.toString(check.lexeme.col);
        // DEBUG
        // System.out.println(
        //     "Weakest Precondition for check at line " + line + "  is: "
        // );
        // wpNode.acceptVisitor(v);

        switch (s){
            case UNSATISFIABLE -> {
                System.out.println(
                    // "Check at line: " + line + ", col: " + col + " passed"
                    "Check at line " + line + " passed"
                );
            }
            default -> {
                throw new IllegalArgumentException(
                    // "Check at line: " + line + ", col: " + col + " failed"
                    "Check at line " + line + " failed"
                );
            }
        }
    }

    private void executeStatement(ASTNode stmt,ArrayList<ASTNode> currentStmts ) {
        if (stmt instanceof AssignmentNode assignNode) {
            executeAssignment(assignNode,currentStmts);
        } else if (stmt instanceof IfNode ifNode) {
            executeIf(ifNode,currentStmts);
        } else if (stmt instanceof CheckNode checkNode) {
            executeCheck(checkNode,currentStmts);
        } else if (stmt instanceof PrintNode) {
        } else if (stmt instanceof BlockNode blockNode) {
            executeBlock(blockNode,currentStmts);
        }
        else if (stmt instanceof ErrorNode) {
            throw new RuntimeException("Cannot verify program with ErrorNode present.");
        } else {
            throw new IllegalStateException("Unexpected statement node type: " + stmt.getClass());
        }
    }
}
