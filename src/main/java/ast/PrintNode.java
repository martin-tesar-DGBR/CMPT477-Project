package ast;

import lexer.LocatedString;

public class PrintNode extends ASTNode {
	public LocatedString variable;

	PrintNode(LocatedString lexeme, LocatedString variable) {
		super(lexeme);
		this.variable = variable;
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
