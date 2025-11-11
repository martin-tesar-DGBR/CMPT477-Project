package ast;

import lexer.*;
import logging.*;

import java.util.*;

public class Parser {
	public Lexer stream;
	public Logger logger;

	public Parser(Lexer stream) {
		this.stream = stream;
		this.logger = Logger.get(LogType.PARSER);
	}

	public ASTNode parseProgram() {
		ASTNode program = parseBlock();
		return program;
	}

	private boolean expect(StaticToken... tokens) {
		if (tokens.length < 1) {
			throw new IllegalArgumentException("There must be at least one argument to expect()");
		}
		if (!stream.hasNext()) {
			StringBuilder builder = new StringBuilder();
			if (tokens.length == 1) {
				builder.append("Expected");
			}
			else {
				builder.append("Expected one of");
			}
			for (StaticToken token : tokens) {
				builder.append(' ')
					.append(token.lexeme);
			}
			builder.append(", reached end of file");
			logger.log(LogLevel.SEVERE, builder.toString());
			return false;
		}
		Token read = this.stream.peek();
		for (StaticToken token : tokens) {
			if (read instanceof StaticTokenImpl t && t.token == token) {
				this.stream.next();
				return true;
			}
		}
		LocatedString lexeme = read.getLexeme();
		StringBuilder builder = new StringBuilder();
		if (tokens.length == 1) {
			builder.append("Expected");
		}
		else {
			builder.append("Expected one of");
		}
		for (StaticToken token : tokens) {
			builder.append(' ')
				.append(token.lexeme);
		}
		builder.append(", got ")
			.append(lexeme.s)
			.append(" at line ")
			.append(lexeme.line)
			.append(", column ")
			.append(lexeme.col);
		logger.log(LogLevel.SEVERE, builder.toString());
		return false;
	}

	private ASTNode parseBlock() {
		// TODO: proper error handling when parsing
		Token blockToken = this.stream.peek();
		expect(StaticToken.LEFT_BRACE);
		List<ASTNode> statements = new ArrayList<>();
		while (startsStatement()) {
			ASTNode statement = parseStatement();
			statements.add(statement);
		}
		expect(StaticToken.RIGHT_BRACE);
		BlockNode block = new BlockNode(blockToken.getLexeme());
		block.children = statements;
		return block;
	}

	private boolean startsStatement() {
		if (!stream.hasNext()) {
			return false;
		}
		Token next = stream.peek();
		if (next instanceof StaticTokenImpl st) {
			return st.token == StaticToken.IF || st.token == StaticToken.CHECK || st.token == StaticToken.PRINT;
		}
		else if (next instanceof LabelToken) {
			return true;
		}
		return false;
	}

	private ASTNode parseStatement() {
		// honestly this can be folded into parseBlock
		if (!stream.hasNext()) {
			return null;
		}
		Token next = stream.peek();
		if (next instanceof StaticTokenImpl st) {
			switch (st.token) {
				case IF -> {
					return parseIfStatement();
				}
				case CHECK -> {
					return parseCheckStatement();
				}
				case PRINT -> {
					return parsePrintStatement();
				}
				default -> {
					throw new IllegalStateException("unreachable");
				}
			}
		}
		else if (next instanceof LabelToken) {
			return parseAssignmentStatement();
		}
		throw new IllegalStateException("unreachable");
	}

	private ASTNode parseIfStatement() {
		if (!stream.hasNext()) {
			return null;
		}
		Token token = stream.peek();
		expect(StaticToken.IF);
		ASTNode cond = parseBoolExpr();
		ASTNode branchThen = parseBlock();
		// assert branchThen instanceof BlockNode;
		expect(StaticToken.ELSE);
		ASTNode branchElse = parseBlock();
		// assert branchElse instanceof BlockNode;

		IfNode node = new IfNode(token.getLexeme(), cond, (BlockNode) branchThen, (BlockNode) branchElse);
		return node;
	}

	private ASTNode parseCheckStatement() {
		if (!stream.hasNext()) {
			return null;
		}
		Token token = stream.peek();
		expect(StaticToken.CHECK);
		expect(StaticToken.LEFT_PAREN);
		ASTNode expr = parseBoolExpr();
		expect(StaticToken.RIGHT_PAREN);
		return new CheckNode(token.getLexeme(), expr);
	}

	private ASTNode parsePrintStatement() {
		if (!stream.hasNext()) {
			return null;
		}
		Token token = stream.peek();
		expect(StaticToken.PRINT);
		expect(StaticToken.LEFT_PAREN);
		Token label = stream.next();
		// assert label instanceof LabelToken;
		expect(StaticToken.RIGHT_PAREN);
		return new PrintNode(token.getLexeme(), label.getLexeme());
	}

	private ASTNode parseAssignmentStatement() {
		if (!stream.hasNext()) {
			return null;
		}
		Token label = stream.next();
		// assert label instanceof LabelToken;
		Token assign = stream.peek();
		expect(StaticToken.ASSIGN);
		ASTNode expr = parseIntExpr();
		return new AssignmentNode(assign.getLexeme(), label.getLexeme(), expr);
	}

	private ASTNode parseIntExpr() {
		if (!stream.hasNext()) {
			return null;
		}
		Token next = stream.peek();
		if (next instanceof StaticTokenImpl t && t.token == StaticToken.SUB) {
			expect(StaticToken.SUB);
			ASTNode expr = parseIntAddExpr();
			return new IntOperatorNode(t, expr, null);
		}
		else {
			return parseIntAddExpr();
		}
	}

	private ASTNode parseIntAddExpr() {
		ASTNode fst = parseIntMulExpr();
		if (!stream.hasNext()) {
			return null;
		}
		Token op = stream.peek();
		if (op instanceof StaticTokenImpl t && (t.token == StaticToken.ADD || t.token == StaticToken.SUB)) {
			expect(StaticToken.ADD, StaticToken.SUB);
			ASTNode snd = parseIntMulExpr();
			return new IntOperatorNode(t, fst, snd);
		}
		else {
			return fst;
		}
	}

	private ASTNode parseIntMulExpr() {
		ASTNode fst = parseIntParenExpr();
		if (!stream.hasNext()) {
			return null;
		}
		Token op = stream.peek();
		if (op instanceof StaticTokenImpl t && t.token == StaticToken.MUL) {
			expect(StaticToken.MUL);
			ASTNode snd = parseIntParenExpr();
			return new IntOperatorNode(t, fst, snd);
		}
		else {
			return fst;
		}
	}

	private ASTNode parseIntParenExpr() {
		if (!stream.hasNext()) {
			return null;
		}
		Token token = stream.peek();
		if (token instanceof StaticTokenImpl) {
			expect(StaticToken.LEFT_PAREN);
			ASTNode expr = parseIntExpr();
			expect(StaticToken.RIGHT_PAREN);
			return expr;
		}
		else if (token instanceof LabelToken label) {
			stream.next();
			return new LabelNode(label.getLexeme());
		}
		else if (token instanceof IntToken integer) {
			stream.next();
			return new IntConstantNode(integer.getLexeme());
		}
		else {
			throw new RuntimeException("Could not parse IntParenExpr");
		}
	}

	private ASTNode parseBoolExpr() {
		if (!stream.hasNext()) {
			return null;
		}
		Token token = stream.peek();
		if (token instanceof StaticTokenImpl t && t.token == StaticToken.NOT) {
			expect(StaticToken.NOT);
			ASTNode expr = parseBoolOrExpr();
			return new BoolOperatorNode(t, expr, null);
		}
		else {
			return parseBoolOrExpr();
		}
	}

	private ASTNode parseBoolOrExpr() {
		ASTNode fst = parseBoolAndExpr();
		Token token = stream.peek();
		if (token instanceof StaticTokenImpl t && t.token == StaticToken.OR) {
			expect(StaticToken.OR);
			ASTNode snd = parseBoolAndExpr();
			return new BoolOperatorNode(t, fst, snd);
		}
		else {
			return fst;
		}
	}

	private ASTNode parseBoolAndExpr() {
		ASTNode fst = parseBoolParenExpr();
		if (!stream.hasNext()) {
			return null;
		}
		Token token = stream.peek();
		if (token instanceof StaticTokenImpl t && t.token == StaticToken.AND) {
			expect(StaticToken.AND);
			ASTNode snd = parseBoolParenExpr();
			return new BoolOperatorNode(t, fst, snd);
		}
		else {
			return fst;
		}
	}

	private ASTNode parseBoolParenExpr() {
		if (!stream.hasNext()) {
			return null;
		}
		Token token = stream.peek();
		if (token instanceof StaticTokenImpl) {
			expect(StaticToken.LEFT_PAREN);
			ASTNode expr = parseBoolExpr();
			expect(StaticToken.RIGHT_PAREN);
			return expr;
		}
		else {
			return parseBoolCmpExpr();
		}
	}

	private ASTNode parseBoolCmpExpr() {
		ASTNode fst = parseIntExpr();
		if (!stream.hasNext()) {
			return null;
		}
		Token cmp = stream.peek();
		expect(StaticToken.GREATER, StaticToken.EQUAL, StaticToken.LESSER);
		ASTNode snd = parseIntExpr();
		return new BoolCompareNode((StaticTokenImpl) cmp, fst, snd);
	}
}
