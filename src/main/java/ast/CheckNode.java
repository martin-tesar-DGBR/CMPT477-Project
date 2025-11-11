package ast;

import lexer.LocatedString;

public class CheckNode extends ASTNode {
	public ASTNode expr;

	CheckNode(LocatedString lexeme, ASTNode expr) {
		super(lexeme);
		this.expr = expr;
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visitEnter(this);
		expr.acceptVisitor(visitor);
		visitor.visitExit(this);
	}
}
