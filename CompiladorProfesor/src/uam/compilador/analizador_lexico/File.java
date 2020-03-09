package uam.compilador.analizador_lexico;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class File {
	private BufferedReader reader;
	/**
	 * Abre un archivo para su lectura. 
	 *
	 * @param  file  nombre del archivo
	 * @see    BufferedReader
	 */
	protected void open(String file)throws IOException {

		reader = new BufferedReader(new FileReader(file));


	}
	/**
	 * Regresa  una linea del archivo abierto
	 *
	 * @return una linea del archivo
	 * @see    IOException
	 */
	protected String getLine() throws IOException {

		return reader.readLine();

	}
	/**
	 * Cierra un archivo 
	 *
	 * @see    IOException
	 */
	protected void close() throws IOException {
		
		reader.close();

	}

}

