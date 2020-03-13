 package uam.compilador.analizador_sintactico;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.TreeMap;

import uam.compilador.analizador_lexico.Alex;
import uam.compilador.analizador_lexico.Token;
import uam.compilador.analizador_lexico.TokenSubType;
import uam.compilador.analizador_lexico.TokenType;
import uam.compilador.generador_codigo.Generador;


@SuppressWarnings("unused")
public class Parser {
	private Alex lexico;
	private TreeMap<String, Simbolo> tablaSimbolos = new TreeMap<String, Simbolo>();
	private Generador generador = new Generador(); 
	private LinkedList<String> e = new LinkedList<String>();
	private boolean hayError=false;
	private BufferedWriter archivoEscritura;
	private File traduccionObjeto;
	private File variables;
	String codigoObjeto="";
	
	Parser(String source){

		lexico = new Alex(source);
		System.out.println("\nINICIA EL RECONOCIMIENTO");
		PROCESS();
		System.out.println("\nTERMINA EL RECONOCIMIENTO");
		for(Simbolo s:tablaSimbolos.values()) {
			System.out.println(s);
		}
		generarTablaSimbolos();
		generarTraduccionObjeto();
	}

	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param  tt El Token que se esperaba
	 * @param  linea Linea donde se detecto el error
	 * @return ---
	 */
	private void error(TokenSubType st, int linea) {
		System.out.println("\t\tError en la linea "+linea+" se espera "+st.name());
		hayError=true;
	}

	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param  tt El Token que se esperaba
	 * @param  linea Linea donde se detecto el error
	 * @return ---
	 */
	private void error(TokenType tt, int linea) {
		System.out.println("\t\tError en la linea "+linea+" se espera "+tt.name());
		hayError=true;
	}

	private void error(String string, int linea) {
		System.out.println("\t\tError en la linea "+linea+" ::"+string);
		hayError=true;
	}

	private void error(String string) {
		System.out.println("\t\tError "+string);
		hayError=true;
	}


	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param  rw La palabra reservada que se esperaba
	 * @return ---
	 */
	private void error(TokenSubType ts) {
		System.out.println("\t\tError...se espera "+ts.name());
		hayError=true;
	}	
	private boolean se_espera(Token t, TokenType preanalisis) {

		if(t!=null) {
			if(t.getType()==preanalisis) {
				return true;
			}

		}
		return false;
	}

	private boolean se_espera(Token t, TokenSubType preanalisis) {

		if(t!=null) {
			if(t.getSubType()==preanalisis) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Este metodo es llamado por expresion
	 * obtiene !Expresion*+/!Expreson
	 */
	private void E() {
		T();
		EP();
	}
	
	/**
	 * Si encuentra + o - lo añade a e y despues hace T y EP 
	 */
	private void EP() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			if(aux.getLexeme().equals("+")||aux.getLexeme().equals("-")) {
				e.add(aux.getLexeme()+"");
				T();
				EP();
			}else {
				lexico.setBackToken(aux);
			}
	}
	
	/**
	 * Este metodo es llamado por E
	 * N hace negacion con expresion o solo expresion 
	 * TP si encuentra * , / o %
	 */
	
	private void T() {

		N();
		TP();

	}

	/**
	 * Si encuentra *, / o % lo añade a e y hace N TP
	 * si no encuentra lo anterior no hace na'
	 */
	private void TP() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			if(aux.getLexeme().equals("*")||aux.getLexeme().equals("/")||aux.getLexeme().equals("%")) {
				e.add(aux.getLexeme()+"");
				N();
				TP();
			}else {

				lexico.setBackToken(aux);
			}

	}
	/**
	 * Si encuentra una negacion la agrega a e y hace F
	 * Si no encuentra negacion, hace F
	 */
	private void N() {

		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			if(aux.getLexeme().equals("!")) {
				e.add(aux.getLexeme()+"");
				F();
			}else {
				lexico.setBackToken(aux);
				F();
			}
	}

	/**
	 * Si encuentra un identificador,entero o real lo agrega a e
	 * Si es un '(' lo agrega a e y vuelve a usar Expresion con ')' al final
	 */
	private void F() {
		Token aux;
		aux=lexico.getToken();
		if(!(se_espera(aux,TokenType.IDENTIFIER)|| 
				se_espera(aux,TokenSubType.INTEGERNUMBER) || 
				se_espera(aux,TokenSubType.REALNUMBER) )) {

			if(!(se_espera(aux,TokenSubType.LEFT_PARENTHESIS))){

				error("Error en linea "+aux.getLine()+" se espera numero o identificador");				
			}else {
				e.add(aux.getLexeme()+"");
				EXPRESION();
				aux=lexico.getToken();
				if(!(se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))){
					error("Error en linea "+aux.getLine()+" se espera )");				

				}else
					e.add(aux.getLexeme()+"");

			}

		}
		else
			e.add(aux.getLexeme()+"");
	}
	
	private void OPERADORO() {
		Token aux;
		aux = lexico.getToken();
		if (aux != null)
			if (aux.getLexeme().equals("||")) {
				e.add(aux.getLexeme() + "");
				Y();
				OPERADORO();
			} else {
				lexico.setBackToken(aux);
			}
	}
		
	private void Y() {
		CONDICION();
		OPERADORY();
	}
		
	private void OPERADORY() {
		Token aux;
		aux = lexico.getToken();
		if (aux != null)
			if (aux.getLexeme().equals("&&")) {
				e.add(aux.getLexeme() + "");
				CONDICION();
				OPERADORY();
			} else {
				lexico.setBackToken(aux);
			}
	}
		
	private void CONDICION() {
		EXPMAYMEN();
		IGUALODIFERENTE();
	}
		
	private void IGUALODIFERENTE() {
		Token aux;
		aux = lexico.getToken();
		if (aux != null)
			if (aux.getLexeme().equals("==") || aux.getLexeme().equals("!=")) {
				e.add(aux.getLexeme() + "");
				EXPMAYMEN();
				IGUALODIFERENTE();
			} else {
				lexico.setBackToken(aux);
			}
	}
		
	private void EXPMAYMEN() {
		E();
		MENOROMAYOR();
	}
		
	private void MENOROMAYOR() {
		Token aux;
		aux = lexico.getToken();
		if (aux != null)
			if (aux.getLexeme().equals("<") || aux.getLexeme().equals(">") || aux.getLexeme().equals(">=")
					|| aux.getLexeme().equals("<=")) {
				e.add(aux.getLexeme() + "");
				E();
				MENOROMAYOR();
			} else {
				lexico.setBackToken(aux);
			}
	}


	private void STATEMENT_INTEGER() {
		Token preanalisis;
		Simbolo s=null;
		preanalisis=lexico.getToken();
		TokenSubType tr;
		Token tipo;
		Token nombre;

		if(!se_espera(preanalisis,TokenSubType.INTEGER))
			error("Error, se espera un integer y se recibio "+preanalisis+"  Linea:"+preanalisis.getLine());
		
		do{
			preanalisis=lexico.getToken();
			if(!se_espera(preanalisis,TokenType.IDENTIFIER))
				error(TokenType.IDENTIFIER, preanalisis.getLine());
			nombre=preanalisis;
			preanalisis=lexico.getToken();

			//NO SE ESPERA PUNTO Y COMA
			if(!se_espera(preanalisis,TokenSubType.SEMICOLON)) {

				//NO SE ESPERA SIMBOLO DE ASIGNACION
				if(!se_espera(preanalisis,TokenType.ASSIGNMENT)) {

					//SE CREA UN Simbolo CON EL LEXEMA Y EL TIPO (INTEGER)
					s=new Simbolo(nombre.getLexeme(),TokenSubType.INTEGERNUMBER);
					if(!tablaSimbolos.containsKey(s.getNombre()))
						tablaSimbolos.put(s.getNombre(), s);
					else//SI LA VARIABLE YA ESTA DECLARADA, HAY UN ERROR
						error("Error: La variable "+s.getNombre()+" ya fue declarada");
					if(!se_espera(preanalisis,TokenSubType.COMMA))
						error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());


				}else {

					preanalisis=lexico.getToken(); 

					//SE VIENE DE UNA ASIGNACION, ENTONCES ESPERO UN ENTERO
					
					if(preanalisis.getSubType()==TokenSubType.INTEGERNUMBER) {
						tr=TokenSubType.INTEGERNUMBER;

						if(!se_espera(preanalisis,tr))
							error(tr,preanalisis.getLine());
						else {
							s=new Simbolo(nombre.getLexeme(), Integer.parseInt(preanalisis.getLexeme()),TokenSubType.INTEGERNUMBER);

							if(!tablaSimbolos.containsKey(s.getNombre()))
								tablaSimbolos.put(s.getNombre(), s);
							else
								error("Error: La variable "+s.getNombre()+" ya fue declarada");
						}

						preanalisis=lexico.getToken();

						if(preanalisis.getSubType()!=TokenSubType.SEMICOLON) {

							if(!se_espera(preanalisis,TokenSubType.COMMA))
								error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());
							
						}

					}	
				}

			}else {

				//SE ESPERA UN PUNTO Y COMA
				if(!se_espera(preanalisis,TokenSubType.SEMICOLON))
					error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());

				s=new Simbolo(nombre.getLexeme(),TokenSubType.INTEGERNUMBER);
				if(!tablaSimbolos.containsKey(s.getNombre()))
					tablaSimbolos.put(s.getNombre(), s);
				else
					error("Error: La variable "+s.getNombre()+" ya fue declarada");

			}


		}while(preanalisis.getSubType()!=TokenSubType.SEMICOLON);



	}



	/**
	 * Identifica que tipo de operacion a reconocer. Observe
	 * que en esta version solo se reconoce una operacion por
	 * estructura de control. Ademas, observe que el 
	 * el Token leido (if, read, etc.) se devuelve a la lista de Tokens a fin que 
	 * la estructura a reconocer  no  tenga problemas.
	 */
	@SuppressWarnings("incomplete-switch")
	private boolean OPERACION(String t) {
		Token aux;
		aux=lexico.getToken();
		//System.out.println("MIToken:"+aux);
		if(aux!=null) {
			//Se devuelve el Token a la lista
			lexico.setBackToken(aux);

			if(aux.getSubType()!=null) {
				switch(aux.getSubType()) {

				case IF:
					//Se reconoce un if. Al devolver el Token el metodo IF puede
					//indicar que espera de inicio un TokenSubType IF.
					IF(t);
					return true;	
				case READ:
					READ(t);
					return  true;
					
				case INTEGER:
					STATEMENT_INTEGER();
					return true;
					
				case WHILE:
					WHILE(t);
					return true;
				
				case DO:
					DOWHILE(t);
					return true;
					
				case SWITCH:
					SWITCH(t);
					return true;
					
				case FOR:
					FOR(t);
					return true;
					
				case REAL:
					STATEMENT_REAL();
					return true;
					
				case BOOLEAN:
					STATEMENT_BOOLEAN();
					return true;
					
				case CHARACTER:
					STATEMENT_CHARACTER();
					return true;
					
				case WRITE:
					WRITE(t);
					return true;
					
				default:break;	

				
				}
			}else {
				switch(aux.getType()) {
				case IDENTIFIER:
					aux=lexico.getToken();
					Token aux1=lexico.getToken();
					if(se_espera(aux1,TokenType.ASSIGNMENT)) {
						lexico.setBackToken(aux1);
						lexico.setBackToken(aux);
						ASIGNACION(t);
						return true;
					}
					else if(se_espera(aux1,TokenSubType.LEFT_PARENTHESIS)) {
						lexico.setBackToken(aux1);
						lexico.setBackToken(aux);
						LLAMADOFUNCION(t);
						return true;
					}
				}
			}
		}
		System.out.println("Token No Reconocido ->  "+aux.getLexeme());
		return false;

	}



	private void EXPRESION() {

		E();
	}

	/**
	 * Inicia el reconocimiento de un SI.Observe que si la estructura
	 * Si no tiene un Sino
	 */
	private void IF(String t) {
		Token aux; 
		int etiqueta1,etiqueta2,etiqueta3;
		String expresion="";
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.IF))
			error(TokenSubType.IF);

		aux=lexico.getToken();
		
		if(!se_espera(aux,TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);

		EXPRESION();
		while(!e.isEmpty())
			expresion=expresion+e.pop();

		etiqueta1=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta2=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta3=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		generador.emitir(t+"cmp "+expresion+" true");
		generador.emitir(t+"jmpc ETIQUETA"+etiqueta1);
		generador.emitir(t+"jump ETIQUETA"+etiqueta2);
		
		codigoObjeto=codigoObjeto+"\n"+t+"cmp "+expresion+" true";
		codigoObjeto=codigoObjeto+"\n"+t+"jmpc ETIQUETA"+etiqueta1;
		codigoObjeto=codigoObjeto+"\n"+t+"jump ETIQUETA"+etiqueta2;
		
		aux=lexico.getToken();
		
		if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);

		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.THEN)) {
			error(TokenSubType.THEN,aux.getLine());

		}
		generador.emitir(t+"ETIQUETA"+etiqueta1+":");
		codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta1+":";
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.ELSE)&&
				!se_espera(aux,TokenSubType.ENDIF)&& aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES(t+"\t");
		}
		if(aux==null)
			error(TokenSubType.ENDIF);
		else {

			if(aux.getSubType()==TokenSubType.ELSE) {

				generador.emitir(t+"\t"+"jump ETIQUETA"+etiqueta3);
				generador.emitir(t+"ETIQUETA"+etiqueta2+":");
				codigoObjeto=codigoObjeto+"\n"+t+"\t"+"jump ETIQUETA"+etiqueta3;
				codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta2+":";
				
				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.ENDIF)&& aux!=null) {

					lexico.setBackToken(aux);
					aux=OPERACIONES(t+"\t");
				}
				if(aux==null)
					error(TokenSubType.ENDIF);
				else {
					generador.emitir(t+"ETIQUETA"+etiqueta3+":");
					codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta3+":";
				}
				
			}else {
				generador.emitir(t+"ETIQUETA"+etiqueta2+":");
				codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta2+":";
			}
		}		
	}

	Token OPERACIONES(String t) {
		Token aux;
		boolean f;
		f=OPERACION(t);
		if(!f) {
			aux=lexico.getToken();
			error("Error en linea "+aux.getLine()+" se recibio: "+aux.getLexeme());
			aux=lexico.getToken();
		}else
			aux=lexico.getToken();
		return aux;
	}


	private void READ(String t) {
		Token aux;
		String id;
		boolean salir=true;
		aux=lexico.getToken();

		if(!se_espera(aux,TokenSubType.READ))
			error(TokenSubType.READ,aux.getLine());

		do {
			aux=lexico.getToken();
			if(!se_espera(aux,TokenType.IDENTIFIER)){
				error(TokenType.IDENTIFIER,aux.getLine()); 
			}
			generador.emitir(t+"input "+aux.getLexeme());
			codigoObjeto=codigoObjeto+"\n"+t+"input "+aux.getLexeme();
			aux=lexico.getToken();
			salir=se_espera(aux,TokenSubType.SEMICOLON);
			if(!salir) {
				if(!se_espera(aux,TokenSubType.SEMICOLON)){
					error(TokenSubType.SEMICOLON,aux.getLine());

				}else {
					salir=false;
				}	
			}

		}while(salir && aux!=null);	


	}

	void PROCESS() {
		Token aux;
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.PROCESS))
			error(TokenSubType.PROCESS,aux.getLine());
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER,aux.getLine());
		
		generador.emitir("begin process");
		codigoObjeto=codigoObjeto+"\n"+"begin process";
		
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.ENDPROCESS)&& aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES("\t");
		}
		if(aux==null) 
			error(TokenSubType.ENDPROCESS);
		else if(se_espera(aux,TokenSubType.ENDPROCESS)){
			generador.emitir("end process");
			codigoObjeto=codigoObjeto+"\n"+"end process";
			
			aux=lexico.getToken();
			if(aux!=null) {
				lexico.setBackToken(aux);
				FUNCTION();
			}
		}

	}
	
	
	private void WHILE(String t) {
		Token aux;
		int etiqueta1,etiqueta2;
		String expresion="";
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.WHILE))
			error(TokenSubType.WHILE,aux.getLine());
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.LEFT_PARENTHESIS))
				error(TokenSubType.LEFT_PARENTHESIS,aux.getLine());
		EXPRESION();
		while(!e.isEmpty())
			expresion=expresion+e.pop();
		
		etiqueta1=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta2=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		generador.emitir(t+"ETIQUETAY:");
		generador.emitir(t+"\t"+"cmp "+expresion+" true");
		generador.emitir(t+"\t"+"jmpc ETIQUETA"+etiqueta1);
		generador.emitir(t+"\t"+"jump ETIQUETA"+etiqueta2);
		codigoObjeto=codigoObjeto+"\n"+t+"\t"+"jump ETIQUETA"+etiqueta2;
		
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS,aux.getLine());
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.DO))
			error(TokenSubType.DO,aux.getLine());
		
		generador.emitir(t+"ETIQUETA"+etiqueta1+":");
		codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta1+":";
		
		aux = lexico.getToken();
		while(aux!=null&&!se_espera(aux, TokenSubType.ENDWHILE)) {
			lexico.setBackToken(aux);
			aux=OPERACIONES(t+"\t");
		}
		if(aux==null)
			error(TokenSubType.ENDWHILE);
		else {
			generador.emitir(t+"\t"+"jump ETIQUETAY:");
			generador.emitir(t+"jump ETIQUETA"+etiqueta2+":");
			codigoObjeto=codigoObjeto+"\n"+t+"\t"+"jump ETIQUETAY:";
			codigoObjeto=codigoObjeto+"\n"+t+"jump ETIQUETA"+etiqueta2+":";
			
		}
	}
	
	private void DOWHILE(String t) {
		Token aux;
		int etiqueta1;
		String expresion="";
		
		aux=lexico.getToken();
		if(!se_espera(aux, TokenSubType.DO))
			error(TokenSubType.DO,aux.getLine());
		
		etiqueta1=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		generador.emitir(t+"ETIQUETA"+etiqueta1+":");
		codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta1+":";
		
		
		aux = lexico.getToken();
		while(aux!=null&&!se_espera(aux, TokenSubType.WHILE)) {
			lexico.setBackToken(aux);
			aux=OPERACIONES(t+"\t");
		}
		if(aux==null)
			error(TokenSubType.WHILE);
		else{
			aux=lexico.getToken();
			if(!se_espera(aux, TokenSubType.LEFT_PARENTHESIS))
				error(TokenSubType.LEFT_PARENTHESIS,aux.getLine());
			EXPRESION();
			while(!e.isEmpty())
				expresion=expresion+e.pop();
			
			generador.emitir(t+"\t"+"cmp "+expresion+" true");
			codigoObjeto=codigoObjeto+"\n"+t+"\t"+"cmp "+expresion+" true";
			
			aux=lexico.getToken();
			if(!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
				error(TokenSubType.RIGHT_PARENTHESIS,aux.getLine());
			else {
				generador.emitir(t+"\t"+"jmpc ETIQUETA"+etiqueta1);
				codigoObjeto=codigoObjeto+"\n"+t+"\t"+"jmpc ETIQUETA"+etiqueta1;
			}
			aux=lexico.getToken();
			if(!se_espera(aux, TokenSubType.SEMICOLON)) 
				error(TokenSubType.COLON,aux.getLine());
		}
	}
	
	public void SWITCH(String t) {
		Token aux;
		String opcion="";
		int etiqueta1,etiqueta2;
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.SWITCH))
			error(TokenSubType.SWITCH,aux.getLine());
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS,aux.getLine());
		opcion=OPCION();
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS,aux.getLine());
		
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.DO))
			error(TokenSubType.DO,aux.getLine());
		
		LISTADECASOS(t);
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.DEFAULT))
			error(TokenSubType.DEFAULT,aux.getLine());
		generador.emitir(t+"cmp "+opcion+" entero");
		etiqueta1=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta2=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		generador.emitir(t+"jmpc ETIQUETA"+etiqueta1);
		generador.emitir(t+"jump ETIQUETA"+etiqueta2);
		generador.emitir(t+"ETIQUETA"+etiqueta1+":");
		codigoObjeto=codigoObjeto+"\n"+t+"jmpc ETIQUETA"+etiqueta1;
		codigoObjeto=codigoObjeto+"\n"+t+"jump ETIQUETA"+etiqueta2;
		codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta1+":";
		
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.COLON))
			error(TokenSubType.COLON,aux.getLine());
		aux = lexico.getToken();
		while(aux!=null&&!se_espera(aux, TokenSubType.BREAK)) {
			lexico.setBackToken(aux);
			aux=OPERACIONES(t+"\t");
		}
		if(aux==null)
			error(TokenSubType.BREAK);
		else {
			generador.emitir(t+"jump ETIQUETAX");
			generador.emitir(t+"ETIQUETA"+etiqueta2+":");
			codigoObjeto=codigoObjeto+"\n"+t+"jump ETIQUETAX";
			codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta2+":";
			
			
			aux = lexico.getToken();
			if(!se_espera(aux, TokenSubType.SEMICOLON))
				error(TokenSubType.SEMICOLON,aux.getLine());
			aux = lexico.getToken();
			if(!se_espera(aux, TokenSubType.ENDSWITCH))
				error(TokenSubType.ENDSWITCH,aux.getLine());
			generador.emitir(t+"ETIQUETAX:");
			codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETAX:";
		}
	}
	
	private String OPCION() {
		Token aux;
		aux = lexico.getToken();
		if(!(se_espera(aux, TokenType.IDENTIFIER)||se_espera(aux, TokenSubType.INTEGERNUMBER))) {
			error("Error en opcion de switch",aux.getLine());
			return "";
		}
		else {
			return aux.getLexeme();
		}
	}
	
	private void LISTADECASOS(String t) {
		Token aux;
		
		int etiqueta1;
		etiqueta1=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		
		CASO(t,etiqueta1);
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.DEFAULT)&&aux!=null) {
			lexico.setBackToken(aux);
			LISTADECASOS(t);
		}else if(aux!=null) {
			//se regresa el token default
			lexico.setBackToken(aux);
		}else
			error(TokenSubType.DEFAULT);
	}

	private void CASO(String t,int etiqueta1) {
		Token aux;
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.INTEGERNUMBER))
			error(TokenSubType.INTEGERNUMBER,aux.getLine());
		
		generador.emitir(t+"cmp "+aux.getLexeme()+" entero");
		generador.emitir(t+"jmpc ETIQUETA"+etiqueta1);
		codigoObjeto=codigoObjeto+"\n"+t+"cmp "+aux.getLexeme()+" entero";
		codigoObjeto=codigoObjeto+"\n"+t+"jmpc ETIQUETA"+etiqueta1;
		
		aux = lexico.getToken();
		if(se_espera(aux, TokenSubType.COLON)) {
			
			int etiqueta2;
			etiqueta2=generador.getNumeroEtiqueta();
			generador.incrementaNumeroEtiqueta();
			generador.emitir(t+"jump ETIQUETA"+etiqueta2);
			generador.emitir(t+"ETIQUETA"+etiqueta1+":");
			codigoObjeto=codigoObjeto+"\n"+t+"jump ETIQUETA"+etiqueta2;
			codigoObjeto=codigoObjeto+"\n"+t+"jmpc ETIQUETA"+etiqueta1;
			
			aux = lexico.getToken();
			while(aux!=null&&!se_espera(aux, TokenSubType.BREAK)) {
				lexico.setBackToken(aux);
				aux=OPERACIONES(t+"\t");
			}
			if(aux==null)
				error(TokenSubType.BREAK);
			else {
				generador.emitir(t+"jump ETIQUETAX");
				generador.emitir(t+"ETIQUETA"+etiqueta2+":");
				codigoObjeto=codigoObjeto+"\n"+t+"jump ETIQUETAX";
				codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta2+":";
				
				aux=lexico.getToken();
				if(!se_espera(aux, TokenSubType.SEMICOLON))
					error(TokenSubType.SEMICOLON,aux.getLine());
			}
		}else if(se_espera(aux, TokenSubType.COMMA))
			CASO(t,etiqueta1);
	}
	
	private void FOR(String t) {
		Token aux; 
		aux=lexico.getToken();
		int etiqueta1,etiqueta2,etiqueta3;
		String opcion1="",opcion2="",opcion3="",opcion4="";
		String expresion="";
		boolean step=false;

		etiqueta1=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta2=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta3=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		
		if(!se_espera(aux,TokenSubType.FOR))
			error(TokenSubType.FOR);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER,aux.getLine());
		opcion1=aux.getLexeme();
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.ASSIGNMENT))
			error(TokenType.ASSIGNMENT, aux.getLine());	
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER)&&!se_espera(aux,TokenSubType.INTEGERNUMBER))
			error("Error en asignacion en for ", aux.getLine());
		opcion2=aux.getLexeme();
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.UNTIL))
			error(TokenSubType.UNTIL);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER)&&!se_espera(aux,TokenSubType.INTEGERNUMBER))
			error("Error en asignacion en for ", aux.getLine());
		opcion3=aux.getLexeme();
		aux=lexico.getToken();
		
		if(se_espera(aux,TokenSubType.WITH)) {
			aux=lexico.getToken();
			if(!se_espera(aux,TokenSubType.STEP))
				error(TokenSubType.STEP,aux.getLine());
			aux=lexico.getToken();
			if(!se_espera(aux,TokenType.IDENTIFIER)&&!se_espera(aux,TokenSubType.INTEGERNUMBER)) 
				error("Error en asignacion en for ", aux.getLine());
			opcion4=aux.getLexeme();
			step=true;
			aux=lexico.getToken();
		}
		
		if(!se_espera(aux,TokenSubType.DO))
			error(TokenSubType.DO,aux.getLine());
		
		generador.emitir(t+"mv "+opcion1+" "+opcion2);
		generador.emitir(t+"ETIQUETA Y:");
		generador.emitir(t+"\t"+"cmp "+opcion1+" "+opcion3);
		generador.emitir(t+"\t"+"jmpc ETIQUETA"+etiqueta1);
		generador.emitir(t+"\t"+"jump ETIQUETA"+etiqueta2);
		generador.emitir(t+"ETIQUETA"+etiqueta1+":");
		codigoObjeto=codigoObjeto+"\n"+t+"mv "+opcion1+" "+opcion2;
		codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA Y:";
		codigoObjeto=codigoObjeto+"\n"+t+"\t"+"cmp "+opcion1+" "+opcion3;
		codigoObjeto=codigoObjeto+"\n"+t+"\t"+"jmpc ETIQUETA"+etiqueta1;
		codigoObjeto=codigoObjeto+"\n"+t+"\t"+"jump ETIQUETA"+etiqueta2;
		codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta1+":";
		
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.ENDFOR)&& aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES(t+"\t");
		}
		
		if(step) {
			generador.emitir(t+"\t"+"add "+opcion1+" "+opcion4);
			codigoObjeto=codigoObjeto+"\n"+t+"\t"+"add "+opcion1+" "+opcion4;
		}
		else {
			generador.emitir(t+"\t"+"add "+opcion1+" 1");
			codigoObjeto=codigoObjeto+"\n"+t+"\t"+"add "+opcion1+" 1";
		}
		generador.emitir(t+"\t"+"jump ETIQUETA Y");
		codigoObjeto=codigoObjeto+"\n"+t+"\t"+"jump ETIQUETA Y";
		
		if(aux==null) 
			error(TokenSubType.ENDFOR);
		else {
			generador.emitir(t+"ETIQUETA"+etiqueta2+":");
			codigoObjeto=codigoObjeto+"\n"+t+"ETIQUETA"+etiqueta2+":";
		}
	}
	
	private void FUNCTION() {
		Token aux; 
		String opcion="", expresion="";

		TIPOVARIABLE();

		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.FUNCTION))
			error(TokenSubType.FUNCTION);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER,aux.getLine());
		opcion=aux.getLexeme();
		
		generador.emitir("begin "+opcion);
		codigoObjeto=codigoObjeto+"\n"+"begin "+opcion;
		
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS, aux.getLine());
		boolean salir=true;
		do {
			aux=lexico.getToken();
			if(!se_espera(aux,TokenType.IDENTIFIER)){
				error(TokenType.IDENTIFIER,aux.getLine()); 
			}
			aux=lexico.getToken();
			salir=se_espera(aux,TokenSubType.COMMA);
			if(!salir) {
				if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS)){
					error(TokenSubType.RIGHT_PARENTHESIS,aux.getLine());
				}else {
					salir=false;
				}	
			}

		}while(salir && aux!=null);

		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.RETURN)&& aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES("\t");
		}
		if(!se_espera(aux,TokenSubType.RETURN))
			error(TokenSubType.RETURN,aux.getLine());

		EXPRESION();
		while(!e.isEmpty()) {
			expresion=expresion+e.pop();
		}
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.SEMICOLON))
			error(TokenSubType.SEMICOLON,aux.getLine());
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.ENDFUNCTION))
			error(TokenSubType.ENDFUNCTION,aux.getLine());
		
		generador.emitir("end "+opcion);
		codigoObjeto=codigoObjeto+"\n"+"end "+opcion;
		
		aux=lexico.getToken();
		if(aux!=null) {
			lexico.setBackToken(aux);
			FUNCTION();
		}

	}
	
	private void TIPOVARIABLE() {
		Token aux;
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.INTEGER)&&!se_espera(aux,TokenSubType.REAL)&&!se_espera(aux,TokenSubType.BOOLEAN)&&!se_espera(aux,TokenSubType.CHARACTER))
			error("Error se espera un tipo (INTEGER, REAL, BOOLEAN O CHARACTER) en la linea " +aux.getLine());
	}
	
	private void WRITE(String t) {
		Token aux;
		boolean salir = true;
		String expresion="";
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.WRITE))
			error(TokenSubType.WRITE, aux.getLine());
		do {
			aux = lexico.getToken();
			salir = se_espera(aux, TokenType.STRING);
			if(salir) {
				generador.emitir(t+"output "+aux.getLexeme());
				codigoObjeto=codigoObjeto+"\n"+t+"output "+aux.getLexeme();
			}

			else if (!salir) {
				lexico.setBackToken(aux);
				EXPRESION();
				while(!e.isEmpty()) {
					expresion=expresion+e.pop();
				}
				
				generador.emitir(t+"output "+expresion);
				codigoObjeto=codigoObjeto+"\n"+t+"output "+expresion;
				
				expresion="";
			}
			aux = lexico.getToken();
			salir = se_espera(aux, TokenSubType.COMMA);
			if (!salir) {
				if (!se_espera(aux, TokenSubType.SEMICOLON)) {
					error(TokenSubType.SEMICOLON, aux.getLine());
				} else {
					salir = false;
				}
			}
		} while (salir && aux != null);
	}
	
	private void ASIGNACION(String t) {
		Token aux;
		String s=" ";
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER, aux.getLine());
		s=s+aux.getLexeme()+" ";
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.ASSIGNMENT))
			error(TokenType.ASSIGNMENT, aux.getLine());

		EXPRESION();
		while(!e.isEmpty())
			s=s+e.pop();
		
		generador.emitir(t+"mov  "+s);
		codigoObjeto=codigoObjeto+"\n"+t+"mov  "+s;
		
		aux=lexico.getToken();

		if(!se_espera(aux,TokenSubType.SEMICOLON))
			error(TokenSubType.SEMICOLON);
	}
	
	private void LLAMADOFUNCION(String t) {
		{
			Token aux;
			aux=lexico.getToken();
			boolean salir;
			String cadena="";
			if(!se_espera(aux,TokenType.IDENTIFIER))
				error(TokenType.IDENTIFIER, aux.getLine());
			cadena=aux.getLexeme();
			aux=lexico.getToken();

			if(!se_espera(aux,TokenSubType.LEFT_PARENTHESIS))
				error(TokenSubType.LEFT_PARENTHESIS, aux.getLine());
			cadena=cadena+aux.getLexeme();
			do {
				aux=lexico.getToken();

				if(!se_espera(aux,TokenType.IDENTIFIER)){
					error(TokenType.IDENTIFIER,aux.getLine()); 
				}
				cadena=cadena+aux.getLexeme();
				aux=lexico.getToken();
				salir=se_espera(aux,TokenSubType.COMMA);
				if(!salir) {
					if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS)){
						error(TokenSubType.RIGHT_PARENTHESIS,aux.getLine());
						cadena=cadena+aux.getLexeme();

					}else {

						salir=false;
					}	
				}
				cadena=cadena+aux.getLexeme();
			}while(salir && aux!=null);
			
			generador.emitir(t+cadena);
			codigoObjeto=codigoObjeto+"\n"+t+cadena;
			
			aux=lexico.getToken();
			if(!se_espera(aux,TokenSubType.SEMICOLON))
				error(TokenSubType.SEMICOLON,aux.getLine());
		}
	}

	private void STATEMENT_REAL() {
		Token preanalisis;
		Simbolo s=null;
		preanalisis=lexico.getToken();
		TokenSubType tr;
		Token tipo;
		Token nombre;

		if(!se_espera(preanalisis,TokenSubType.REAL))
			error("Error, se espera un real y se recibio "+preanalisis+"  Linea:"+preanalisis.getLine());

		do{
			preanalisis=lexico.getToken();
			if(!se_espera(preanalisis,TokenType.IDENTIFIER))
				error(TokenType.IDENTIFIER, preanalisis.getLine());
			nombre=preanalisis;
			preanalisis=lexico.getToken();

			//NO SE ESPERA PUNTO Y COMA
			if(!se_espera(preanalisis,TokenSubType.SEMICOLON)) {

				//NO SE ESPERA SIMBOLO DE ASIGNACION
				if(!se_espera(preanalisis,TokenType.ASSIGNMENT)) {

					//SE CREA UN Simbolo CON EL LEXEMA Y EL TIPO (INTEGER)
					s=new Simbolo(nombre.getLexeme(),TokenSubType.REALNUMBER);
					if(!tablaSimbolos.containsKey(s.getNombre()))
						tablaSimbolos.put(s.getNombre(), s);
					else//SI LA VARIABLE YA ESTA DECLARADA, HAY UN ERROR
						error("Error: La variable "+s.getNombre()+" ya fue declarada");
					if(!se_espera(preanalisis,TokenSubType.COMMA))
						error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());


				}else {

					preanalisis=lexico.getToken(); 

					//SE VIENE DE UNA ASIGNACION, ENTONCES ESPERO UN REAL

					if(preanalisis.getSubType()==TokenSubType.REALNUMBER) {
						tr=TokenSubType.REALNUMBER;

						if(!se_espera(preanalisis,tr))
							error(tr,preanalisis.getLine());
						else {
							s=new Simbolo(nombre.getLexeme(), Double.parseDouble(preanalisis.getLexeme()),TokenSubType.REALNUMBER);

							if(!tablaSimbolos.containsKey(s.getNombre()))
								tablaSimbolos.put(s.getNombre(), s);
							else
								error("Error: La variable "+s.getNombre()+" ya fue declarada");
						}

						preanalisis=lexico.getToken();

						if(preanalisis.getSubType()!=TokenSubType.SEMICOLON) {

							if(!se_espera(preanalisis,TokenSubType.COMMA))
								error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());
						}
					}	
				}

			}else {
				//SE ESPERA UN PUNTO Y COMA
				if(!se_espera(preanalisis,TokenSubType.SEMICOLON))
					error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());

				s=new Simbolo(nombre.getLexeme(),TokenSubType.REALNUMBER);
				if(!tablaSimbolos.containsKey(s.getNombre()))
					tablaSimbolos.put(s.getNombre(), s);
				else
					error("Error: La variable "+s.getNombre()+" ya fue declarada");
			}
		}while(preanalisis.getSubType()!=TokenSubType.SEMICOLON);
	}
	
	private void STATEMENT_BOOLEAN() {
		Token preanalisis;
		Simbolo s=null;
		preanalisis=lexico.getToken();
		TokenSubType tr;
		Token tipo;
		Token nombre;

		if(!se_espera(preanalisis,TokenSubType.BOOLEAN))
			error("Error, se espera un boolean y se recibio "+preanalisis+"  Linea:"+preanalisis.getLine());

		do{
			preanalisis=lexico.getToken();
			if(!se_espera(preanalisis,TokenType.IDENTIFIER))
				error(TokenType.IDENTIFIER, preanalisis.getLine());
			nombre=preanalisis;
			preanalisis=lexico.getToken();

			//NO SE ESPERA PUNTO Y COMA
			if(!se_espera(preanalisis,TokenSubType.SEMICOLON)) {

				//NO SE ESPERA SIMBOLO DE ASIGNACION
				if(!se_espera(preanalisis,TokenType.ASSIGNMENT)) {

					//SE CREA UN Simbolo CON EL LEXEMA Y EL TIPO (FALSE)
					s=new Simbolo(nombre.getLexeme(),TokenSubType.FALSE);

					if(!tablaSimbolos.containsKey(s.getNombre()))
						tablaSimbolos.put(s.getNombre(), s);
					else//SI LA VARIABLE YA ESTA DECLARADA, HAY UN ERROR
						error("Error: La variable "+s.getNombre()+" ya fue declarada");
					if(!se_espera(preanalisis,TokenSubType.COMMA))
						error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());


				}else {

					preanalisis=lexico.getToken(); 

					//SE VIENE DE UNA ASIGNACION, ENTONCES ESPERO TRUE O FALSE

					if(preanalisis.getSubType()==TokenSubType.TRUE) {
						tr=TokenSubType.TRUE;

						if(!se_espera(preanalisis,tr))
							error(tr,preanalisis.getLine());
						else {
							s=new Simbolo(nombre.getLexeme(), Boolean.parseBoolean(preanalisis.getLexeme()),TokenSubType.TRUE);

							if(!tablaSimbolos.containsKey(s.getNombre()))
								tablaSimbolos.put(s.getNombre(), s);
							else
								error("Error: La variable "+s.getNombre()+" ya fue declarada");
						}

						preanalisis=lexico.getToken();

						if(preanalisis.getSubType()!=TokenSubType.SEMICOLON) {

							if(!se_espera(preanalisis,TokenSubType.COMMA))
								error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());
						}
					}else {
						if(preanalisis.getSubType()==TokenSubType.FALSE) {
							tr=TokenSubType.FALSE;

							if(!se_espera(preanalisis,tr))
								error(tr,preanalisis.getLine());
							else {
								s=new Simbolo(nombre.getLexeme(), Boolean.parseBoolean(preanalisis.getLexeme()),TokenSubType.FALSE);

								if(!tablaSimbolos.containsKey(s.getNombre()))
									tablaSimbolos.put(s.getNombre(), s);
								else
									error("Error: La variable "+s.getNombre()+" ya fue declarada");
							}

							preanalisis=lexico.getToken();

							if(preanalisis.getSubType()!=TokenSubType.SEMICOLON) {

								if(!se_espera(preanalisis,TokenSubType.COMMA))
									error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());
							}
						}
					}
				}

			}else {
				//SE ESPERA UN PUNTO Y COMA
				//SE CREA UN Simbolo CON EL LEXEMA Y EL TIPO (FALSE)
				s=new Simbolo(nombre.getLexeme(),TokenSubType.FALSE);
				if(!tablaSimbolos.isEmpty()) {
					if(!tablaSimbolos.containsKey(s.getNombre()))
						tablaSimbolos.put(s.getNombre(), s);
					else
						error("Error: La variable "+s.getNombre()+" ya fue declarada");
				}
			}
		}while(preanalisis.getSubType()!=TokenSubType.SEMICOLON);
	}
	
	private void STATEMENT_CHARACTER() {
		Token preanalisis;
		Simbolo s=null;
		preanalisis=lexico.getToken();
		TokenSubType tr;
		Token tipo;
		Token nombre;

		if(!se_espera(preanalisis,TokenSubType.CHARACTER))
			error("Error, se espera un real y se recibio "+preanalisis+"  Linea:"+preanalisis.getLine());

		do{
			preanalisis=lexico.getToken();
			if(!se_espera(preanalisis,TokenType.IDENTIFIER))
				error(TokenType.IDENTIFIER, preanalisis.getLine());
			nombre=preanalisis;
			preanalisis=lexico.getToken();

			//NO SE ESPERA PUNTO Y COMA
			if(!se_espera(preanalisis,TokenSubType.SEMICOLON)) {

				//NO SE ESPERA SIMBOLO DE ASIGNACION
				if(!se_espera(preanalisis,TokenType.ASSIGNMENT)) {

					//SE CREA UN Simbolo CON EL LEXEMA Y EL TIPO (STRING)
					s=new Simbolo(nombre.getLexeme(),TokenSubType.CHAR);
					if(!tablaSimbolos.containsKey(s.getNombre()))
						tablaSimbolos.put(s.getNombre(), s);
					else//SI LA VARIABLE YA ESTA DECLARADA, HAY UN ERROR
						error("Error: La variable "+s.getNombre()+" ya fue declarada");
					if(!se_espera(preanalisis,TokenSubType.COMMA))
						error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());


				}else {

					preanalisis=lexico.getToken(); 

					//SE VIENE DE UNA ASIGNACION, ENTONCES ESPERO UN REAL

					if(preanalisis.getSubType()==TokenSubType.CHAR) {
						tr=TokenSubType.CHAR;

						if(!se_espera(preanalisis,tr))
							error(tr,preanalisis.getLine());
						else {
							s=new Simbolo(nombre.getLexeme(), preanalisis.getLexeme(),TokenSubType.CHAR);

							if(!tablaSimbolos.containsKey(s.getNombre()))
								tablaSimbolos.put(s.getNombre(), s);
							else
								error("Error: La variable "+s.getNombre()+" ya fue declarada");
						}

						preanalisis=lexico.getToken();

						if(preanalisis.getSubType()!=TokenSubType.SEMICOLON) {

							if(!se_espera(preanalisis,TokenSubType.COMMA))
								error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());
						}
					}	
				}

			}else {
				//SE ESPERA UN PUNTO Y COMA
				if(!se_espera(preanalisis,TokenSubType.SEMICOLON))
					error("Se esperaba una COMA, una ASIGNACION o un PUNTO Y COMA", preanalisis.getLine());

				s=new Simbolo(nombre.getLexeme(),TokenSubType.CHAR);
				if(!tablaSimbolos.containsKey(s.getNombre()))
					tablaSimbolos.put(s.getNombre(), s);
				else
					error("Error: La variable "+s.getNombre()+" ya fue declarada");
			}
		}while(preanalisis.getSubType()!=TokenSubType.SEMICOLON);
	}
	
	private void generarTraduccionObjeto() {
		traduccionObjeto = new File("src/traduccionObjeto.txt");
		if(!hayError) {
			try {
				archivoEscritura = new BufferedWriter(new FileWriter(traduccionObjeto));
					archivoEscritura.write("\n"+codigoObjeto);
				

				archivoEscritura.close();
			} catch (Exception e) {
				System.out.println("\nError al crear la traduccion objeto :( ");
			}
		}else {
			System.out.println("\nError al crear la traduccion objeto, tiene errores "
					+ "de codigo");
			if(traduccionObjeto.exists()){
				traduccionObjeto.delete();

			}
		}	
	}
	
	private void generarTablaSimbolos() {
		String simbolo = "";
		variables = new File("src/tablaDeSimbolos.txt");
		if (!hayError) {
			try {
				archivoEscritura = new BufferedWriter(new FileWriter(variables));
				archivoEscritura.write("Tabla de simbolos: \n");
				for (Simbolo s : tablaSimbolos.values()) {
					simbolo = s.toString();
					archivoEscritura.write("\n" + simbolo);
				}
				archivoEscritura.close();
			} catch (Exception e) {
				System.out.println("\nError al crear la tabla de simbolos");
			}
		} else {
			System.out.println("\nError al crear la tabla de simbolos, tiene erroes"
					+ " de codigo");
			if(variables.exists()){
				variables.delete();
			}
		}
	}
	
	public static void main(String[] args) {
		new Parser("src/programa1.txt");
	}

}
