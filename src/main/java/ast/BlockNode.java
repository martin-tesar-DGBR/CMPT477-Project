package ast;

import lexer.LocatedString;

import java.util.List;

public class BlockNode extends ASTNode {
	public List<ASTNode> children;

	BlockNode(LocatedString lexeme) {
		super(lexeme);
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visitEnter(this);
		for (ASTNode child : children) {
			child.acceptVisitor(visitor);
		}
		visitor.visitExit(this);
	}
}
