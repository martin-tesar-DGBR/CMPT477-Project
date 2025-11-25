package verifier;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;

import ast.ASTNode;
import ast.BoolCompareNode;
import ast.BoolOperatorNode;
import ast.IntConstantNode;
import ast.IntOperatorNode;
import ast.LabelNode;

public class ASTNodeToZ3Convertor {

    public IntExpr intOperatorToZ3(IntOperatorNode node,Context ctx) {
        IntOperatorNode.Operator op = node.op;

        // unary minus
        if (op == IntOperatorNode.Operator.NEGATE) {
            IntExpr v = intToZ3(node.left,ctx);
            return (IntExpr) ctx.mkUnaryMinus(v);
        }
        
        IntExpr left = intToZ3(node.left,ctx);
        IntExpr right = intToZ3(node.right,ctx);

        switch (op) {
            case ADD -> {
                return (IntExpr) ctx.mkAdd(left,right);
            }
            case SUB -> {
                return (IntExpr) ctx.mkSub(left,right);
            }
            case MUL -> {
                return (IntExpr) ctx.mkMul(left,right);
            }
            default -> {
                throw new IllegalStateException(
                    "Unexpected int operator: " + op
                );
            }
        }
    }

    public IntExpr intToZ3(ASTNode node, Context ctx) {
        if (node instanceof IntConstantNode c) {
            return ctx.mkInt(Integer.parseInt(c.lexeme.s));
        } else if (node instanceof LabelNode l) {
            String name = l.label.s;
            return ctx.mkIntConst(name);
        } else if (node instanceof IntOperatorNode intOperatorNode) {
            return intOperatorToZ3(intOperatorNode,ctx);
        } else {
            throw new IllegalStateException(
                "Expected integer expression, got " + node.getClass()
            );
        }
    }

    public BoolExpr boolCompareNodeToZ3(BoolCompareNode node, Context ctx) {
        IntExpr L = intToZ3(node.left,ctx);
        IntExpr R = intToZ3(node.right,ctx);

        switch (node.cmp) {
            case GREATER -> {
                return ctx.mkGt(L, R);
            }
            case LESSER -> {
                return ctx.mkLt(L, R);
            }
            case EQUAL -> {
                return ctx.mkEq(L, R);
            }
            default -> throw new IllegalStateException("Unexpected comparison: " + node.cmp);
        }
        
    }

    public BoolExpr boolOperatorNodeToZ3(BoolOperatorNode node,Context ctx) {
        BoolOperatorNode.Operator op = node.op;

        if (op == BoolOperatorNode.Operator.NOT) {
            // unary NOT, right is null
            return ctx.mkNot(boolToZ3(node.left,ctx));
        }

        BoolExpr left = boolToZ3(node.left,ctx);
        BoolExpr right = boolToZ3(node.right,ctx);

        if (null == op) {
            throw new IllegalStateException("Unexpected boolean operator: " + op);
        } else
        switch (op) {
            case AND -> {
                return ctx.mkAnd(left,right);
            }
            case OR -> {
                return ctx.mkOr(left,right);
            }
            default -> throw new IllegalStateException("Unexpected boolean operator: " + op);
        }
    }

    public BoolExpr boolToZ3(ASTNode node, Context ctx) {
        if (node instanceof BoolCompareNode boolCompareNode) {
            return boolCompareNodeToZ3(boolCompareNode,ctx);
        } else if (node instanceof BoolOperatorNode boolOperatorNode) {
            return boolOperatorNodeToZ3(boolOperatorNode,ctx);
        } else {
            throw new IllegalStateException(
                "Expected boolean expression, got " + node.getClass()
            );
        }
    }

    public BoolExpr convert(ASTNode node, Context ctx) {
        return boolToZ3(node,ctx);
    }
}
