package uam.compilador.analizador_lexico;

public enum TokenSubType {

	REALNUMBER("[0-9]+\\.[0-9]+"),
	INTEGERNUMBER("[0-9]+"), 
	COMMA(","), 
	SEMICOLON(";"), 
	COLON(":"), 
	ARITHMETIC_ADD("+|-"),
	ARITHMETIC_MUL("*|/||%"),
	SPACE("[\t\f\r]+"),
	LINE_BREAK("\n"),
	AND("[&|\\|]"),
	OR("!"),
	NEGATION("!"),
	LEFT_PARENTHESIS("("),
	RIGHT_PARENTHESIS(")"),
	EQUALITY("[=]"),
	FUNCTION("function"),
	ENDFUNCTION("endfunction"),
	RETURN("return"),
	FALSE("false"),
	TRUE("true"),
	SWITCH("switch"),
	DO("do"),
	BREAK("break"),
	DEFAULT("default"),
	ENDSWITCH("endswitch"),
	WHILE("while"),
	ENDWHILE("endwhile"),
    FOR("for"),
    UNTIL("until"),
    WITH("with"),
    STEP("step"),
    ENDFOR("endfor"),
    IN_LINES("/_*_*_/"),
    IN_LINE("/_/"),
	PROCESS("process"),ENDPROCESS("endprocess"),READ("read"),WRITE("write"),
	IF("if"),THEN("then"),ELSE("else"),ENDIF("endif"),INTEGER("integer"), REAL("double"), BOOLEAN("boolean"),
	CHARACTER("character");
	private final String pattern;
	private TokenSubType(String pattern) {
		this.pattern = pattern;
	}
	
	public String getType() {	
		return pattern;
	}
}

