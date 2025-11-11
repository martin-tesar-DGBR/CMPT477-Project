package ast;

import lexer.LocatedString;

public class LabelNode extends ASTNode {
	public LocatedString label;

	LabelNode(LocatedString lexeme) {
		super(lexeme);
		this.label = lexeme;
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
