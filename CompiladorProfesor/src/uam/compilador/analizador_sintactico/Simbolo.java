package uam.compilador.analizador_sintactico;

import uam.compilador.analizador_lexico.TokenSubType;

public class Simbolo {

	private String nombre;
	private int ivalor=0;
	private double rvalor=0.0;
	private boolean bvalor=false;
	private String chvalor;
	private boolean defaultValor=true;
	private TokenSubType stype;
	

	public Simbolo(String nombre,TokenSubType  stype) {
		super();
		this.nombre = nombre;
		this.stype = stype;
	}
	/*
	 * Integer
	 */
	public Simbolo(String nombre, int ivalor,TokenSubType  stype) {
		super();
		this.nombre = nombre;
		this.ivalor = ivalor;
		this.defaultValor = false;
		this.stype = stype;
	}	
	/*
	 * Real
	 */
	public Simbolo(String nombre,double rvalor, TokenSubType  stype) {
		super();
		this.nombre = nombre;
		this.rvalor = rvalor;
		this.defaultValor = false;
		this.stype = stype;
	}
	public Simbolo(String nombre,boolean bvalor, TokenSubType  stype) {
		super();
		this.nombre = nombre;
		this.bvalor = bvalor;
		this.defaultValor = false;
		this.stype = stype;
	}
	/*
	 * Char
	 */
	public Simbolo(String nombre,String chvalor, TokenSubType  stype) {
		super();
		this.nombre = nombre;
		this.chvalor = chvalor;
		this.defaultValor = false;
		this.stype = stype;
	}
	public Simbolo(String nombre) {
		super();
		this.nombre = nombre;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public int getIvalor() {
		return ivalor;
	}
	public void setValor(int ivalor) {
		this.ivalor = ivalor;
	}
	public void setValor(double rvalor) {
		this.rvalor = rvalor;
	}
	public double getRvalor() {
		return rvalor;
	}
	public int getBvalor() {
		return ivalor;
	}
	public void setBValor(boolean bvalor) {
		this.bvalor = bvalor;
	}
	public void setChValor(String chvalor) {
		this.chvalor = chvalor;
	}
	public String getChvalor() {
		return chvalor;
	}

	public boolean isDefaultValor() {
		return defaultValor;
	}
	public void setDefaultValor(boolean defaultValor) {
		this.defaultValor = defaultValor;
	}
	
	public TokenSubType getStyte() {
		return stype;
	}
	public void setStyte(TokenSubType ttypte) {
		this.stype = ttypte;
	}
	@Override
	public String toString() {
		return "Simbolo [Nombre=" + nombre + ", ivalor=" + ivalor + ", rvalor=" + rvalor +", bvalor=" + bvalor + ", chvalor=" + chvalor + "]";
	}
	
}

