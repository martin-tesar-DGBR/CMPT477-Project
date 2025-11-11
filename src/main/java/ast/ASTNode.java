package ast;

import lexer.LocatedString;

public abstract class ASTNode {
	public LocatedString lexeme;

	ASTNode(LocatedString lexeme) {
		this.lexeme = lexeme;
	}

	// the pattern every node needs to follow is this:
	// - call visitor.visitEnter(this)
	// - for each child call child.accept(visitor)
	// - after each child is done call visitor.visitExit(this)
	public abstract void acceptVisitor(ASTVisitor visitor);
}
