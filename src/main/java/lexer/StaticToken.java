package lexer;

import java.util.*;

public enum StaticToken {
	LEFT_BRACE("{"),
	RIGHT_BRACE("}"),
	LEFT_PAREN("("),
	RIGHT_PAREN(")"),
	ADD("+"),
	SUB("-"),
	MUL("*"),
	NOT("!"),
	OR("||"),
	AND("&&"),
	GREATER(">"),
	EQUAL("=="),
	LESSER("<"),
	ASSIGN(":="),
	IF("if"),
	ELSE("else"),
	CHECK("check"),
	PRINT("print")
	;

	public final String lexeme;

	StaticToken(String lexeme) {
		this.lexeme = lexeme;
	}

	static class TokenStateTree {
		Map<Character, TokenStateTree> nextStates;
		// null if not in a terminal state,
		StaticToken terminal;

		TokenStateTree() {
			nextStates = new HashMap<>();
			terminal = null;
		}

		static void indent(int depth) {
			for (int i = 0; i < depth; i += 1) {
				System.out.print("  ");
			}
		}

		//debugging purposes
		static void printTree(TokenStateTree tree, int depth) {
			indent(depth);
			System.out.println("TOKEN: " + tree.terminal);
			for (Map.Entry<Character, TokenStateTree> bucket : tree.nextStates.entrySet()) {
				indent(depth + 1);
				System.out.println(bucket.getKey());
				printTree(bucket.getValue(), depth + 1);
			}
		}
	}

	public static TokenStateTree tokenTree;
	static {
		tokenTree = initTokenTree(Arrays.stream(StaticToken.values()).toList(), 0);
	}

	private static TokenStateTree initTokenTree(List<StaticToken> tokens, int depth) {
		Map<Character, List<StaticToken>> buckets = new HashMap<>();
		StaticToken terminal = null;
		for (StaticToken token : tokens) {
			if (depth == token.lexeme.length()) {
				if (terminal == null) {
					terminal = token;
				}
				else {
					throw new IllegalStateException("Ambiguous static token definition: \"" + terminal.name() + "\" and \"" + token.name() + "\"");
				}
				continue;
			}
			char id = token.lexeme.charAt(depth);
			if (buckets.containsKey(id)) {
				buckets.get(id).add(token);
			}
			else {
				List<StaticToken> bucket = new ArrayList<>();
				bucket.add(token);
				buckets.put(id, bucket);
			}
		}

		TokenStateTree ret = new TokenStateTree();
		for (Map.Entry<Character, List<StaticToken>> bucket : buckets.entrySet()) {
			ret.nextStates.put(bucket.getKey(), initTokenTree(bucket.getValue(), depth + 1));
		}
		ret.terminal = terminal;

		return ret;
	}
}
