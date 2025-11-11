package ast;

import lexer.LocatedString;

public class AssignmentNode extends ASTNode {
	public LocatedString lhs;
	public ASTNode rhs;

	AssignmentNode(LocatedString lexeme, LocatedString lhs, ASTNode rhs) {
		super(lexeme);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visitEnter(this);
		rhs.acceptVisitor(visitor);
		visitor.visitExit(this);
	}
}
