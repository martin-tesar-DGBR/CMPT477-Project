package ast;

import lexer.LocatedString;

public class ErrorNode extends ASTNode {
	ErrorNode(LocatedString lexeme) {
		super(lexeme);
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
