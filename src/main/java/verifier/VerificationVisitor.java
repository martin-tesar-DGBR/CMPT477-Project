package verifier;

import ast.*;
import com.microsoft.z3.*;
import logging.*;

import java.util.*;

public class VerificationVisitor extends ASTVisitor.Default {
	Context ctx;
	Map<String, IntExpr> vars;
	// weakest preconditions for the current block
	// bottom of stack is outermost scope, next are blocks on top of if statements, next are nested if statements, etc.
	Stack<BoolExpr> wp;
	// postconditions to pass to new blocks
	Stack<BoolExpr> blockPostconditions;
	// traversal of bool and int expressions
	Stack<BoolExpr> boolExprTree;
	Stack<ArithExpr> intExprTree;

	Logger logger;

	public VerificationVisitor() {
		this.ctx = new Context();
		this.vars = new HashMap<>();
		this.wp = new Stack<>();
		this.blockPostconditions = new Stack<>();
		this.blockPostconditions.push(ctx.mkBool(true));
		this.boolExprTree = new Stack<>();
		this.intExprTree = new Stack<>();

		this.logger = Logger.get(LogType.VERIFIER);
	}

	public boolean verifyCondition() {
		if (this.wp.size() != 1) {
			throw new IllegalStateException("Verification visitor failed; number of weakest preconditions " + this.wp.size() + " not 1.");
		}
		Solver solver = this.ctx.mkSolver();
		BoolExpr val = this.wp.pop();
		System.out.println(val);
		solver.add(ctx.mkNot(val));
		Status status = solver.check();
		return status == Status.UNSATISFIABLE;
	}

	@Override
	public BlockTraversalOrder getTraversalOrder() {
		return BlockTraversalOrder.BACKWARDS;
	}

	@Override
	public void visitEnter(BlockNode node) {
		super.visitEnter(node);
		this.wp.push(this.blockPostconditions.pop());
	}

	@Override
	public void visitExit(CheckNode node) {
		super.visitExit(node);
		if (this.boolExprTree.isEmpty()) {
			throw new IllegalStateException(node.lexeme.toString() + " does not have precisely one expression.");
		}
		if (this.wp.isEmpty()) {
			throw new IllegalStateException(node.lexeme.toString() + " does not have a weakest precondition.");
		}
		BoolExpr expr = this.boolExprTree.pop();
		BoolExpr wp = this.wp.pop();
		BoolExpr check = ctx.mkAnd(expr, wp);
		this.wp.push(check);
	}

	@Override
	public void visitExit(AssignmentNode node) {
		super.visitExit(node);
		if (this.intExprTree.size() != 1) {
			throw new IllegalStateException("Assignment " + node.lexeme + " has invalid right hand side.");
		}
		if (this.wp.isEmpty()) {
			throw new IllegalStateException("No weakest precondition at " + node.lexeme + ".");
		}
		IntExpr lhs;
		if (this.vars.containsKey(node.lhs.s)) {
			lhs = this.vars.get(node.lhs.s);
		}
		else {
			lhs = ctx.mkIntConst(node.lhs.s);
			this.vars.put(node.lhs.s, lhs);
		}
		ArithExpr rhs = this.intExprTree.pop();
		BoolExpr wp = this.wp.pop();
		Expr sub = wp.substitute(lhs, rhs);
		if (!sub.isBool()) {
			throw new IllegalStateException("Substituted weakest precondition at " + node.lexeme + " is not of boolean sort.");
		}
		this.wp.push((BoolExpr) sub);
	}

	@Override
	public void visitEnter(IfNode node) {
		super.visitEnter(node);
		// duplicate the current weakest precondition for each branch
		if (this.wp.isEmpty()) {
			throw new IllegalStateException("No postcondition at " + node.lexeme);
		}
		BoolExpr wp = this.wp.pop();
		this.blockPostconditions.push(wp);
		this.blockPostconditions.push(wp);
	}

	@Override
	public void visitExit(IfNode node) {
		super.visitExit(node);
		if (this.wp.size() < 2) {
			throw new IllegalStateException("There must be precisely two weakest preconditions at " + node.lexeme + ".");
		}
		if (this.boolExprTree.isEmpty()) {
			throw new IllegalStateException("If statement at " + node.lexeme + " must have a condition.");
		}
		BoolExpr cond = this.boolExprTree.pop();
		BoolExpr wpElse = this.wp.pop();
		BoolExpr wpThen = this.wp.pop();
		BoolExpr next = this.ctx.mkAnd(this.ctx.mkImplies(cond, wpThen), this.ctx.mkImplies(this.ctx.mkNot(cond), wpElse));
		this.wp.push(next);
	}

	@Override
	public void visitExit(IntOperatorNode node) {
		super.visitExit(node);
		ArithExpr expr;
		switch (node.op) {
			case ADD -> {
				if (this.intExprTree.size() < 2) {
					throw new IllegalStateException("Operator at " + node.lexeme + " does not have two subexpressions.");
				}
				ArithExpr exprR = this.intExprTree.pop();
				ArithExpr exprL = this.intExprTree.pop();
				expr = this.ctx.mkAdd(exprL, exprR);
			}
			case SUB -> {
				if (this.intExprTree.size() < 2) {
					throw new IllegalStateException("Operator at " + node.lexeme + " does not have two subexpressions.");
				}
				ArithExpr exprR = this.intExprTree.pop();
				ArithExpr exprL = this.intExprTree.pop();
				expr = this.ctx.mkSub(exprL, exprR);
			}
			case MUL -> {
				if (this.intExprTree.size() < 2) {
					throw new IllegalStateException("Operator at " + node.lexeme + " does not have two subexpressions.");
				}
				ArithExpr exprR = this.intExprTree.pop();
				ArithExpr exprL = this.intExprTree.pop();
				expr = this.ctx.mkMul(exprL, exprR);
			}
			case NEGATE -> {
				if (this.intExprTree.size() < 1) {
					throw new IllegalStateException("Operator at " + node.lexeme + " does not have a subexpression.");
				}
				ArithExpr ex = this.intExprTree.pop();
				expr = this.ctx.mkUnaryMinus(ex);
			}
			default -> {
				throw new IllegalStateException("Invalid operator at " + node.lexeme + ".");
			}
		}
		this.intExprTree.push(expr);
	}

	@Override
	public void visitExit(BoolOperatorNode node) {
		super.visitExit(node);
		BoolExpr expr;
		switch (node.op) {
			case OR -> {
				if (this.boolExprTree.size() < 2) {
					throw new IllegalStateException("Operator at " + node.lexeme + " does not have two subexpressions.");
				}
				BoolExpr exprR = this.boolExprTree.pop();
				BoolExpr exprL = this.boolExprTree.pop();
				expr = this.ctx.mkOr(exprL, exprR);
			}
			case AND -> {
				if (this.boolExprTree.size() < 2) {
					throw new IllegalStateException("Operator at " + node.lexeme + " does not have two subexpressions.");
				}
				BoolExpr exprR = this.boolExprTree.pop();
				BoolExpr exprL = this.boolExprTree.pop();
				expr = this.ctx.mkAnd(exprL, exprR);
			}
			case NOT -> {
				if (this.boolExprTree.size() < 1) {
					throw new IllegalStateException("Operator at " + node.lexeme + " does not have a subexpression.");
				}
				BoolExpr ex = this.boolExprTree.pop();
				expr = this.ctx.mkNot(ex);
			}
			default -> {
				throw new IllegalStateException("Invalid operator at " + node.lexeme + ".");
			}
		}
		this.boolExprTree.push(expr);
	}

	@Override
	public void visitExit(BoolCompareNode node) {
		super.visitExit(node);
		if (this.intExprTree.size() != 2) {
			throw new IllegalStateException("Comparison at " + node.lexeme + " does not have precisely two subexpressions.");
		}
		ArithExpr exprR = this.intExprTree.pop();
		ArithExpr exprL = this.intExprTree.pop();
		BoolExpr cmp;
		switch (node.cmp) {
			case EQUAL -> {
				cmp = this.ctx.mkEq(exprL, exprR);
			}
			case LESSER -> {
				cmp = this.ctx.mkLt(exprL, exprR);
			}
			case GREATER -> {
				cmp = this.ctx.mkGt(exprL, exprR);
			}
			default -> {
				throw new IllegalStateException("Invalid comparison at " + node.lexeme + ".");
			}
		}
		this.boolExprTree.push(cmp);
	}

	@Override
	public void visit(LabelNode node) {
		super.visit(node);
		IntExpr var;
		if (this.vars.containsKey(node.label.s)) {
			var = this.vars.get(node.label.s);
		}
		else {
			var = ctx.mkIntConst(node.label.s);
			this.vars.put(node.label.s, var);
		}
		this.intExprTree.push(var);
	}

	@Override
	public void visit(IntConstantNode node) {
		super.visit(node);
		IntExpr expr = ctx.mkInt(node.lexeme.s);
		this.intExprTree.push(expr);
	}
}
