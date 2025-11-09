package lexer;

public class IntToken implements Token {
	// the reason this is a String instead of a BigInteger is because
	// the Z3 API only accepts int, long, or String arguments
	public LocatedString lexeme;

	IntToken(LocatedString value) {
		this.lexeme = value;
	}

	@Override
	public String getLexeme() {
		return lexeme.s;
	}
}
