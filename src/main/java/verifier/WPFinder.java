package verifier;

import ast.*;
import lexer.StaticToken;

public class WPFinder {

    public ASTNode findWPAssign(AssignmentNode assignNode,ASTNode postExpr){
        String varName = assignNode.lhs.s;
        ASTNode value = assignNode.rhs;
        ASTNode replaced = replaceBool(postExpr, varName, value);
        return replaced;
    }

    public ASTNode findWPIf(IfNode ifNode,ASTNode postExpr){
        ASTNode wpThen = findWP(ifNode.branchThen,postExpr);
        ASTNode wpElse = findWP(ifNode.branchElse,postExpr);

        // in binaryoperator NOT node
        // left is not null, but right is null
        BoolOperatorNode notC = new BoolOperatorNode(
            StaticToken.NOT,
            ifNode.cond,
            null 
        );
        // ¬ p or q <-> p -> q
        BoolOperatorNode thenImp = new BoolOperatorNode(
            StaticToken.OR,
            new BoolOperatorNode(StaticToken.NOT,ifNode.cond,null),
            wpThen
        );

        BoolOperatorNode elseImp = new BoolOperatorNode(
            StaticToken.OR,
            new BoolOperatorNode(StaticToken.NOT,notC,null),
            wpElse
        );
        // wp(if C then S1 else S2, Q) = (C ⇒ wp(S1, Q)) ∧ (¬C ⇒ wp(S2, Q))
        BoolOperatorNode newPost = new BoolOperatorNode(
            StaticToken.AND, 
            thenImp, 
            elseImp
        );

        return newPost;
    }

    public ASTNode findWPBlock(BlockNode blockNode,ASTNode postExpr){
        ASTNode post = postExpr;
        ASTNode wp = null;
        for (ASTNode s: blockNode.children)
        {
            wp = findWP(s,post);
            post = wp;
        }
        return wp;
    }
    
    public ASTNode findWP(ASTNode stmt, ASTNode postExpr)
    {
        if (stmt instanceof AssignmentNode assignmentNode) {
            return findWPAssign(assignmentNode,postExpr);
        } else if (stmt instanceof IfNode ifNode) {
            return findWPIf(ifNode,postExpr);
        } else if (stmt instanceof CheckNode) {
        } else if (stmt instanceof PrintNode) {
        } else if (stmt instanceof BlockNode blockNode) {
            return findWPBlock(blockNode,postExpr);
        } else if (stmt instanceof BoolOperatorNode boolOpNode) {
            // for if statement branch conditions
            BoolOperatorNode res = new BoolOperatorNode(
                StaticToken.OR,
                new BoolOperatorNode(StaticToken.NOT,boolOpNode,null),
                postExpr
            );
            return res;
        } else if (stmt instanceof BoolCompareNode boolCmpNode) {
            // for if statement branch conditions
            BoolOperatorNode res = new BoolOperatorNode(
                StaticToken.OR,
                new BoolOperatorNode(StaticToken.NOT,boolCmpNode,null),
                postExpr
            );
            return res;
        } else if (stmt instanceof ErrorNode) {
            throw new RuntimeException("Cannot verify program with ErrorNode present.");
        } else {
            throw new IllegalStateException("Unexpected statement node type: " + stmt.getClass());
        }
        return postExpr;
    }

    // replace functions

    public BoolCompareNode replaceBoolCompare(BoolCompareNode node, String x, ASTNode E) {
        ASTNode left = replaceInt(node.left, x, E);
        ASTNode right = replaceInt(node.right, x, E);

        BoolCompareNode res = node;
        res.left = left;
        res.right = right;
        return res;
    }

    public BoolOperatorNode replaceBoolOperator(BoolOperatorNode node,String x, ASTNode E) {
        BoolOperatorNode.Operator op = node.op;

        ASTNode left = replaceBool(node.left, x, E);
        ASTNode right = null;
        // when op is unaryminus, right is null
        if (!op.equals(BoolOperatorNode.Operator.NOT))
        {
            right = replaceBool(node.right, x, E);   
        }
        
        BoolOperatorNode res = node;
        res.left = left;
        res.right = right;
        return res;
    }

    public ASTNode replaceBool(ASTNode node,String x, ASTNode E) {
        if (node instanceof BoolCompareNode boolCompareNode) {
            return replaceBoolCompare(boolCompareNode,x,E);
        } else if (node instanceof BoolOperatorNode boolOperatorNode) {
            return replaceBoolOperator(boolOperatorNode,x,E);
        } else {
            throw new IllegalStateException(
                "Expected boolean expression, got " + node.getClass()
            );
        }
    }

    public IntOperatorNode replaceIntOperator(IntOperatorNode node,String x, ASTNode E) {;
        ASTNode left = replaceInt(node.left, x, E);
        ASTNode right = replaceInt(node.right, x, E);

        IntOperatorNode res = node;
        res.left = left;
        res.right = right;
        return res;
    }

    public ASTNode replaceInt(ASTNode node, String x, ASTNode E) {
        if (node instanceof IntConstantNode c) {
            // cannot replace a constant
            return node;
        } else if (node instanceof LabelNode l) {
            String name = l.label.s;
            if (name.equals(x)){
                return E;
            }
            else {
                // return the same node, do not replace anything
                return node;
            }
        } else if (node instanceof IntOperatorNode intOperatorNode) {
            return replaceIntOperator(intOperatorNode,x,E);
        } else {
            throw new IllegalStateException(
                "Expected integer expression, got " + node.getClass()
            );
        }
    }

}
