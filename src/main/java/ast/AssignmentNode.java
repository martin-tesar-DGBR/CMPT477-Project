package ast;

import lexer.LocatedString;

public class AssignmentNode extends ASTNode {
	LabelNode lhs;
	ASTNode rhs;

	AssignmentNode(LocatedString lexeme, LabelNode lhs, ASTNode rhs) {
		super(lexeme);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visitEnter(this);
		lhs.acceptVisitor(visitor);
		rhs.acceptVisitor(visitor);
		visitor.visitExit(this);
	}
}
