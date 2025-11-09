package lexer;

public class LocatedChar {
	char c;
	int line;
	int col;

	LocatedChar(char c, int line, int col) {
	this.c = c;
	this.line = line;
	this.col = col;
	}
}
