package ast;

public interface ASTVisitor {

	void visitEnter(BlockNode node);
	void visitExit(BlockNode node);

	void visitEnter(CheckNode node);
	void visitExit(CheckNode node);

	void visitEnter(AssignmentNode node);
	void visitExit(AssignmentNode node);

	void visitEnter(IfNode node);
	void visitExit(IfNode node);

	void visitEnter(IntOperatorNode node);
	void visitExit(IntOperatorNode node);

	void visitEnter(BoolOperatorNode node);
	void visitExit(BoolOperatorNode node);

	void visitEnter(BoolCompareNode node);
	void visitExit(BoolCompareNode node);

	void visit(LabelNode node);
	void visit(IntConstantNode node);
	void visit(PrintNode node);
	void visit(ErrorNode node);

	class Default implements ASTVisitor {
		@Override
		public void visitEnter(BlockNode node) {}

		@Override
		public void visitExit(BlockNode node) {}

		@Override
		public void visitEnter(CheckNode node) {}

		@Override
		public void visitExit(CheckNode node) {}

		@Override
		public void visitEnter(AssignmentNode node) {}

		@Override
		public void visitExit(AssignmentNode node) {}

		@Override
		public void visitEnter(IfNode node) {}

		@Override
		public void visitExit(IfNode node) {}

		@Override
		public void visitEnter(IntOperatorNode node) {}

		@Override
		public void visitExit(IntOperatorNode node) {}

		@Override
		public void visitEnter(BoolOperatorNode node) {}

		@Override
		public void visitExit(BoolOperatorNode node) {}

		@Override
		public void visitEnter(BoolCompareNode node) {}

		@Override
		public void visitExit(BoolCompareNode node) {}

		@Override
		public void visit(LabelNode node) {}

		@Override
		public void visit(IntConstantNode node) {}

		@Override
		public void visit(PrintNode node) {}

		@Override
		public void visit(ErrorNode node) {}
	}
}
