package ast;

import lexer.LocatedString;

public class IfNode extends ASTNode {
	public ASTNode cond;
	public BlockNode branchThen;
	public BlockNode branchElse;

	IfNode(LocatedString lexeme, ASTNode cond, BlockNode branchThen, BlockNode branchElse) {
		super(lexeme);
		this.cond = cond;
		this.branchThen = branchThen;
		this.branchElse = branchElse;
	}

	@Override
	public void acceptVisitor(ASTVisitor visitor) {
		visitor.visitEnter(this);
		cond.acceptVisitor(visitor);
		branchThen.acceptVisitor(visitor);
		branchElse.acceptVisitor(visitor);
		visitor.visitExit(this);
	}
}
