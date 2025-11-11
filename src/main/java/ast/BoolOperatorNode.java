package ast;

import lexer.StaticTokenImpl;

public class BoolOperatorNode extends ASTNode {

	public enum Operator {
		OR,
		AND,
		NOT,
	}

	public Operator op;
	public ASTNode left;
	public ASTNode right;

	BoolOperatorNode(StaticTokenImpl token, ASTNode left, ASTNode right) {
		super(token.getLexeme());
		assert left != null;
		switch (token.token) {
			case OR -> {
				assert right != null;
				this.op = Operator.OR;
			}
			case AND -> {
				assert right != null;
				this.op = Operator.AND;
			}
			case NOT -> {
				assert right == null;
				this.op = Operator.NOT;
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
