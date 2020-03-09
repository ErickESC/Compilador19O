package uam.compilador.analizador_lexico;

public class Token {
	private TokenType type=null;
	private TokenSubType stype=null;	
	private String lexeme=null;
	private int line=0;

	/**
	 * Se crea un Token
	 */
	public Token() {
	}
	/**
	 * Creacion de un Token con tipo, sub-tipo y lexema
	 * @param  type  Tipo del Token
	 * @param  stype Sub-tipo del Token
	 * @param  lexeme Lexema del Token
	 */
	public Token(TokenType type, TokenSubType stype,String lexeme) {
		this.type = type;
		this.stype = stype;
		this.lexeme = lexeme;
	}

	/**
	 * Creacion de un Token con tipo y sub-tipo
	 * @param  type  Tipo del Token
	 * @param  stype Sub-tipo del Token
	 */
	public Token(TokenType type, TokenSubType stype) {
		this.type = type;
		this.stype = stype;
	}
	/**
	 * Creacion de un Token con tipo y lexema
	 * @param  type  Tipo del Token
	 * @param  lexeme Lexema del Token
	 */
	public Token(TokenType type, String lexeme) {
		this.type = type;
		this.lexeme = lexeme;
	}
	/**
	 * Regresa el TokenType del Token
	 * @return Regresa el TokenType del Token
	 * @see    TokenType
	 */
	public TokenType getType() {
		return type;
	}
	/**
	 * Aplica un tipo al Token
	 * @param  type  Tipo del Token
	 * @see    TokenType
	 */
	public void setType(TokenType type) {
		this.type = type;
	}
	/**
	 * Regresa el TokenSubType del Token
	 *
	 * @return Regresa el TokenSubType del Token, null si no tiene
	 * @see    TokenSubType
	 */
	public TokenSubType getSubType() {
		return stype;
	}
	/**
	 * Aplica un sub-tipo al Token
	 * @param  stype  sub-tipo del Token
	 * @see    TokenSubType
	 */
	public void setSubType(TokenSubType stype) {
		this.stype = stype;
	}

	/**
	 * Regresa el Lexema del Token
	 *
	 * @return Regresa el Lexema del Token
	 * 
	 */
	public String getLexeme() {
		return lexeme;
	}

	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}

	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	@Override
	public String toString() {

		if(stype!=null)
			return String.format("(Token type: %s Token sub-type: %s Lexeme: %s)", type.name(), stype.name(),lexeme);
		else
			return String.format("(Token type: %s Lexeme: %s)", type.name(),lexeme);
	}
}
