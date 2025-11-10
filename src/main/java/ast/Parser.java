package ast;

import lexer.*;

import java.util.*;

public class Parser {
	Lexer stream;

	public Parser(Lexer stream) {
		this.stream = stream;
	}

	public ASTNode parseProgram() {
		return parseBlock();
	}

	private boolean expect(StaticToken... tokens) {
		Token read = this.stream.next();
		for (StaticToken token : tokens) {
			if (read instanceof StaticTokenImpl t && t.token == token) {
				return true;
			}
		}
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
		startsStatement();
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
		Token token = stream.peek();
		expect(StaticToken.CHECK);
		expect(StaticToken.LEFT_PAREN);
		ASTNode expr = parseBoolExpr();
		expect(StaticToken.RIGHT_PAREN);
		return new CheckNode(token.getLexeme(), expr);
	}

	private ASTNode parsePrintStatement() {
		Token token = stream.peek();
		expect(StaticToken.PRINT);
		expect(StaticToken.LEFT_PAREN);
		Token label = stream.next();
		// assert label instanceof LabelToken;
		expect(StaticToken.RIGHT_PAREN);
		return new PrintNode(token.getLexeme(), label.getLexeme());
	}

	private ASTNode parseAssignmentStatement() {
		Token label = stream.next();
		// assert label instanceof LabelToken;
		Token assign = stream.peek();
		expect(StaticToken.ASSIGN);
		ASTNode expr = parseIntExpr();
		return new AssignmentNode(assign.getLexeme(), new LabelNode(label.getLexeme()), expr);
	}

	private ASTNode parseIntExpr() {
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
		Token cmp = stream.peek();
		expect(StaticToken.GREATER, StaticToken.EQUAL, StaticToken.LESSER);
		ASTNode snd = parseIntExpr();
		return new BoolCompareNode((StaticTokenImpl) cmp, fst, snd);
	}
}
