package ast;

import lexer.*;

public class IntOperatorNode extends ASTNode {

	public enum Operator {
		ADD,
		SUB,
		MUL,
		NEGATE,
	}

	ASTNode left;
	ASTNode right;
	Operator op;

	public IntOperatorNode(StaticTokenImpl token, ASTNode left, ASTNode right) {
		super(token.getLexeme());
		assert left != null;
		switch (token.token) {
			case ADD -> {
				assert right != null;
				this.op = Operator.ADD;
			}
			case SUB -> {
				if (right == null) {
					this.op = Operator.NEGATE;
				}
				else {
					this.op = Operator.SUB;
				}
			}
			case MUL -> {
				assert right != null;
				this.op = Operator.MUL;
			}
			default -> {
				assert false;
			}
		}
		this.left = left;
		this.right = right;
	}


	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visitEnter(this);
		left.acceptVisitor(visitor);
		if (right != null) {
			right.acceptVisitor(visitor);
		}
		visitor.visitExit(this);
	}
}
