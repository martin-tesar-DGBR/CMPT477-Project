package ast;

public class PrintVisitor implements ASTVisitor {

	int indentation;
	boolean newLine;

	public PrintVisitor() {
		this.indentation = 0;
		this.newLine = true;
	}

	private void printIndentation() {
		for (int i = 0; i < indentation; i += 1) {
			System.out.print("  ");
		}
	}

	private void print(String s) {
		if (this.newLine) {
			this.printIndentation();
		}
		System.out.print(s);
		this.newLine = false;
	}

	private void println(String s) {
		if (this.newLine) {
			this.printIndentation();
		}
		System.out.println(s);
		this.newLine = true;
	}

	@Override
	public void visitEnter(BlockNode node) {
		this.println("BLOCK");
		this.indentation += 1;
	}

	@Override
	public void visitExit(BlockNode node) {
		this.indentation -= 1;
	}

	@Override
	public void visitEnter(CheckNode node) {
		this.println("check");
		this.indentation += 1;
	}

	@Override
	public void visitExit(CheckNode node) {
		this.indentation -= 1;
	}

	@Override
	public void visitEnter(AssignmentNode node) {
		this.println(":=");
		this.indentation += 1;
	}

	@Override
	public void visitExit(AssignmentNode node) {
		this.indentation -= 1;
	}

	@Override
	public void visitEnter(IfNode node) {
		this.println("if");
		this.indentation += 1;
	}

	@Override
	public void visitExit(IfNode node) {
		this.indentation -= 1;
	}

	@Override
	public void visitEnter(IntOperatorNode node) {
		this.println(node.op.name());
		this.indentation += 1;
	}

	@Override
	public void visitExit(IntOperatorNode node) {
		this.indentation -= 1;
	}

	@Override
	public void visitEnter(BoolOperatorNode node) {
		this.println(node.op.name());
		this.indentation += 1;
	}

	@Override
	public void visitExit(BoolOperatorNode node) {
		this.indentation -= 1;
	}

	@Override
	public void visitEnter(BoolCompareNode node) {
		this.println(node.cmp.name());
		this.indentation += 1;
	}

	@Override
	public void visitExit(BoolCompareNode node) {
		this.indentation -= 1;
	}

	@Override
	public void visit(LabelNode node) {
		this.println(node.label.s);
	}

	@Override
	public void visit(IntConstantNode node) {
		this.println(node.lexeme.s);
	}

	@Override
	public void visit(PrintNode node) {
		this.println("print: " + node.variable.s);
	}

	@Override
	public void visit(ErrorNode node) {
		this.println("ERROR");
	}
}
