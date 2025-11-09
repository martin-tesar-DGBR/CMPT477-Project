package lexer;

public class LocatedString {
	public String s;
	public int line;
	public int col;

	public LocatedString(String s, int line, int col) {
		this.s = s;
		this.line = line;
		this.col = col;
	}
}
