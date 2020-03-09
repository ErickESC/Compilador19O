package uam.compilador.analizador_lexico;

public interface LexicoAnalyzer {

	void createTokenList();
	Token getToken();
	void setBackToken(Token t);
}
