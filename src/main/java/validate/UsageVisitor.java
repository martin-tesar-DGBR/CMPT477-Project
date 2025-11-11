package validate;

import ast.*;
import lexer.LocatedString;
import logging.*;

import java.util.*;

public class UsageVisitor extends ASTVisitor.Default {
	List<Set<String>> declaredVariables;
	Stack<Set<String>> branchVariables;
	Set<LocatedString> invalidVariables;
	Logger logger;

	public UsageVisitor() {
		this.declaredVariables = new ArrayList<>();
		this.branchVariables = new Stack<>();
		this.invalidVariables = new HashSet<>();
		this.logger = Logger.get(LogType.VERIFIER);
	}

	private boolean isDeclared(LocatedString var) {
		for (Set<String> scope : this.declaredVariables) {
			if (scope.contains(var.s)) {
				return true;
			}
		}
		return false;
	}

	private void addDeclared(LocatedString var) {
		this.declaredVariables.get(this.declaredVariables.size() - 1).add(var.s);
	}

	@Override
	public void visitEnter(BlockNode node) {
		// add scope to declaredVariables
		this.declaredVariables.add(new HashSet<>());
	}

	@Override
	public void visitExit(BlockNode node) {
		// move scope to stack
		this.branchVariables.push(this.declaredVariables.remove(this.declaredVariables.size() - 1));
	}

	@Override
	public void visitExit(IfNode node) {
		// take top two scopes off stack, compare
		Set<String> scope1 = this.branchVariables.pop();
		Set<String> scope2 = this.branchVariables.pop();
		scope1.retainAll(scope2);
		this.declaredVariables.get(this.declaredVariables.size() - 1).addAll(scope1);
	}

	@Override
	public void visitExit(AssignmentNode node) {
		addDeclared(node.lhs);
	}

	@Override
	public void visit(PrintNode node) {
		if (!this.isDeclared(node.variable)) {
			logger.log(LogLevel.SEVERE, "Variable " + node.variable + " used before declared.");
			invalidVariables.add(node.variable);
		}
	}

	@Override
	public void visit(LabelNode node) {
		if (!this.isDeclared(node.label)) {
			logger.log(LogLevel.SEVERE, "Variable " + node.label + " used before declared.");
			invalidVariables.add(node.label);
		}
	}
}
