package lexer;

public class StaticTokenImpl implements Token {
	StaticToken token;
	LocatedString lexeme;

	StaticTokenImpl(StaticToken token, int line, int col) {
		this.token = token;
		this.lexeme = new LocatedString(token.lexeme, line, col);
	}

	@Override
	public String getLexeme() {
		return lexeme.s;
	}
}
