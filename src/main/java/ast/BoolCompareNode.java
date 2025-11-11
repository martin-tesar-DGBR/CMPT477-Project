package ast;

import lexer.StaticTokenImpl;

public class BoolCompareNode extends ASTNode {

	public enum Compare {
		GREATER,
		EQUAL,
		LESSER,
	}

	public Compare cmp;
	public ASTNode left, right;

	BoolCompareNode(StaticTokenImpl token, ASTNode left, ASTNode right) {
		super(token.getLexeme());
		assert left != null && right != null;
		switch (token.token) {
			case GREATER -> {
				this.cmp = Compare.GREATER;
			}
			case EQUAL -> {
				this.cmp = Compare.EQUAL;
			}
			case LESSER -> {
				this.cmp = Compare.LESSER;
			}
		}
		this.left = left;
		this.right = right;
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visitEnter(this);
		left.acceptVisitor(visitor);
		right.acceptVisitor(visitor);
		visitor.visitExit(this);
	}
}
