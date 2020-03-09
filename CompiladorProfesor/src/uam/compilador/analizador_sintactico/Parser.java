 package uam.compilador.analizador_sintactico;

import java.util.LinkedList;
import java.util.TreeMap;

import uam.compilador.analizador_lexico.Alex;
import uam.compilador.analizador_lexico.Token;
import uam.compilador.analizador_lexico.TokenSubType;
import uam.compilador.analizador_lexico.TokenType;
import uam.compilador.generador_codigo.Generador;


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
	
	Parser(String source){

		lexico = new Alex(source);
		System.out.println("\nINICIA EL RECONOCIMIENTO");
		PROCESS();
		System.out.println("\nTERMINA EL RECONOCIMIENTO");
		for(Simbolo s:tablaSimbolos.values()) {
			System.out.println(s);
		}

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
	}

	private void error(String string, int linea) {
		System.out.println("\t\tError en la linea "+linea+" ::"+string);

	}

	private void error(String string) {
		System.out.println("\t\tError "+string);

	}


	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param  rw La palabra reservada que se esperaba
	 * @return ---
	 */
	private void error(TokenSubType ts) {

		System.out.println("\t\tError...se espera "+ts.name());

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
		//System.out.println("\t   Operador Expresion:"+aux.getData());
	}

	/**
	 * STATEMENT_INTEGER
	 */

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
					READ();
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
					SWITCH();
					return true;
				
				case FOR:
					FOR();
					return true;
					
				default:break;	

				
				}
			}else {
				switch(aux.getType()) {

				case IDENTIFIER:
					break;
				}
			}
		}
		System.out.println("Token No Reconocido ->  "+aux.getLexeme());
		return false;

	}

	/**
	 * COMIENZA EXPRESION
	 */

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
		
		aux=lexico.getToken();
		
		if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);

		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.THEN)) {
			error(TokenSubType.THEN,aux.getLine());

		}
		generador.emitir(t+"ETIQUETA"+etiqueta1+":");
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

				generador.emitir(t+t+"jump ETIQUETA"+etiqueta3);
				generador.emitir(t+"ETIQUETA"+etiqueta2+":");
				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.ENDIF)&& aux!=null) {

					lexico.setBackToken(aux);
					aux=OPERACIONES(t+"\t");
				}
				if(aux==null)
					error(TokenSubType.ENDIF);
				else
					generador.emitir(t+"ETIQUETA"+etiqueta3+":");
			}else
				generador.emitir(t+"ETIQUETA"+etiqueta2+":");
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


	private void READ() {
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
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.ENDPROCESS)&& aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES("\t");
		}
		generador.emitir("end process");
		if(aux==null) 
			error(TokenSubType.ENDPROCESS);

	}
	private void LISTAREAD() {
		Token aux;
		aux=lexico.getToken();
		if(!se_espera(aux, TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER,aux.getLine());
		aux=lexico.getToken();
		if(aux.getSubType()==TokenSubType.COMMA) {
			System.out.println("vuelve a listaread");
			LISTAREAD();
		}else {
			lexico.setBackToken(aux);
			System.out.println("termina listaread");
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
		generador.emitir(t+t+"cmp "+expresion+" true");
		generador.emitir(t+t+"jmpc ETIQUETA"+etiqueta1);
		generador.emitir(t+t+"jump ETIQUETA"+etiqueta2);
		
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS,aux.getLine());
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.DO))
			error(TokenSubType.DO,aux.getLine());
		
		generador.emitir(t+"ETIQUETA"+etiqueta1+":");
		
		aux = lexico.getToken();
		while(aux!=null&&!se_espera(aux, TokenSubType.ENDWHILE)) {
			lexico.setBackToken(aux);
			aux=OPERACIONES(t+"\t");
		}
		if(aux==null)
			error(TokenSubType.ENDWHILE);
		else {
			generador.emitir(t+t+"jump ETIQUETAY:");
			generador.emitir(t+"jump ETIQUETA"+etiqueta2+":");
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
			
			generador.emitir(t+t+"cmp "+expresion+" true");
			
			aux=lexico.getToken();
			if(!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
				error(TokenSubType.RIGHT_PARENTHESIS,aux.getLine());
			else 
				generador.emitir(t+t+"jmpc ETIQUETA"+etiqueta1);
			aux=lexico.getToken();
			if(!se_espera(aux, TokenSubType.SEMICOLON)) 
				error(TokenSubType.COLON,aux.getLine());
		}
	}
	
	public void SWITCH() {
		Token aux;
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.SWITCH))
			error(TokenSubType.SWITCH,aux.getLine());
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS,aux.getLine());
		OPCION();
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS,aux.getLine());
		
		//no se si va el do
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.DO))
			error(TokenSubType.DO,aux.getLine());
		
		LISTADECASOS();
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.DEFAULT))
			error(TokenSubType.DEFAULT,aux.getLine());
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.COLON))
			error(TokenSubType.COLON,aux.getLine());
		aux = lexico.getToken();
		while(aux!=null&&!se_espera(aux, TokenSubType.BREAK)) {
			lexico.setBackToken(aux);
			aux=OPERACIONES("\t");
		}
		if(aux==null)
			error(TokenSubType.BREAK);
		else {
			aux = lexico.getToken();
			if(!se_espera(aux, TokenSubType.SEMICOLON))
				error(TokenSubType.SEMICOLON,aux.getLine());
			aux = lexico.getToken();
			if(!se_espera(aux, TokenSubType.ENDSWITCH))
				error(TokenSubType.ENDSWITCH,aux.getLine());
			else
				System.out.println("acaba endswitch");
		}
	}
	
	private void OPCION() {
		Token aux;
		aux = lexico.getToken();
		if(!(se_espera(aux, TokenType.IDENTIFIER)||se_espera(aux, TokenSubType.INTEGERNUMBER)))
			error("Error en opcion0",aux.getLine());
		else
			System.out.println("pasó opcion");
	}
	
	private void LISTADECASOS() {
		Token aux;
		CASO();
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.DEFAULT)&&aux!=null) {
			lexico.setBackToken(aux);
			LISTADECASOS();
		}else if(aux!=null) {
			//se regresa el token default
			lexico.setBackToken(aux);
			System.out.println("pasó listadecasos");
		}else
			error(TokenSubType.DEFAULT);
	}

	private void CASO() {
		Token aux;
		aux = lexico.getToken();
		if(!se_espera(aux, TokenSubType.INTEGERNUMBER))
			error(TokenSubType.INTEGERNUMBER,aux.getLine());
		aux = lexico.getToken();
		if(se_espera(aux, TokenSubType.COLON)) {
			aux = lexico.getToken();
			while(aux!=null&&!se_espera(aux, TokenSubType.BREAK)) {
				lexico.setBackToken(aux);
				aux=OPERACIONES("\t");
			}
			if(aux==null)
				error(TokenSubType.BREAK);
			else {
				aux=lexico.getToken();
				if(!se_espera(aux, TokenSubType.SEMICOLON))
					error(TokenSubType.SEMICOLON,aux.getLine());
				else
					System.out.println("pasó caso");
			}
		}else if(se_espera(aux, TokenSubType.COMMA))
			CASO();
		else
			error("Error en : o ,",aux.getLine());
	}
	
    /**
     * método que se llama dentro de la función for para saber si lo que se espera es 
     * diferente a un identificador o un numero entero
     */
	private void ID_IN() {
		Token preanalisis;
		preanalisis = lexico.getToken();
		if (!(se_espera(preanalisis, TokenType.IDENTIFIER) || se_espera(preanalisis, TokenSubType.INTEGERNUMBER)))
			error("Error, se espera identificador o entero");
	}
	
	private void FOR() {
		Token preanalisis;
		preanalisis = lexico.getToken();
		if (!se_espera(preanalisis, TokenSubType.FOR))
			error(TokenSubType.FOR);

		preanalisis = lexico.getToken();
		if (!se_espera(preanalisis, TokenType.IDENTIFIER))
			error(TokenSubType.CHARACTER);

		preanalisis = lexico.getToken();
		if (!se_espera(preanalisis, TokenType.ASSIGNMENT))
			error(TokenSubType.EQUALITY);

		ID_IN();

		preanalisis = lexico.getToken();
		if (!se_espera(preanalisis, TokenSubType.UNTIL)) {
			error(TokenSubType.UNTIL, preanalisis.getLine());
		}
		ID_IN();

		preanalisis = lexico.getToken();
		if (!se_espera(preanalisis, TokenSubType.DO)) {

			if (!se_espera(preanalisis, TokenSubType.WITH)) {
				error(TokenSubType.WITH);
			}

			preanalisis = lexico.getToken();
			if (!se_espera(preanalisis, TokenSubType.STEP)) {
				error(TokenSubType.STEP);
			}

			ID_IN();

			preanalisis = lexico.getToken();

			if (!se_espera(preanalisis,TokenSubType.DO)) {
				error(TokenSubType.DO, preanalisis.getLine());
			}

		}
		preanalisis = lexico.getToken();
		while (!se_espera(preanalisis, TokenSubType.ENDFOR) && preanalisis != null) {

			lexico.setBackToken(preanalisis);
			preanalisis = OPERACIONES(lexico.getToken().toString());

			preanalisis = lexico.getToken();
			if (preanalisis == null)
				error(TokenSubType.ENDFOR);

		}

	}
	
	private void LISTAPARAMETROS(){
		Token aux;
		aux=lexico.getToken();
		if(!se_espera(aux, TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER,aux.getLine());
		aux=lexico.getToken();
		if(aux.getSubType()==TokenSubType.COMMA) {
			LISTAPARAMETROS();
		}else {
			lexico.setBackToken(aux);
		}
	}
	
	public static void main(String[] args) {
		new Parser("src/ejemplo");
	}

}
