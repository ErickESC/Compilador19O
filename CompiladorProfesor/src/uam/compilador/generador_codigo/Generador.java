package uam.compilador.generador_codigo;

public class Generador {

	private int contadorEtiquetas=1;
	private boolean error=false;
	public void emitir(String instruccion) {
		if(!error)
			System.out.println(instruccion);
	}

	public int getNumeroEtiqueta(){
		return contadorEtiquetas;
	}

	public void incrementaNumeroEtiqueta() {
		contadorEtiquetas++;
	}

	public void decremnetaNumeroEtiqueta() {
		contadorEtiquetas--;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
}
