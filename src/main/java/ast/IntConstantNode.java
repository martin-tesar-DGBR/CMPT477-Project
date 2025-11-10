package ast;

import lexer.LocatedString;

public class IntConstantNode extends ASTNode {
	// for the same reason as in the lexer, the integer representation is a String.
	// We use super's lexeme

	public IntConstantNode(LocatedString lexeme) {
		super(lexeme);
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
