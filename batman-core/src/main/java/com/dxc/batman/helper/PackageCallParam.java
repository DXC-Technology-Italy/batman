package com.dxc.batman.helper;

import java.sql.Types;

/**
 * Parameter of a package
 * 
 * @author marco.fioriti@dxc.com
 *
 */
public class PackageCallParam {
	private String nome;
	private String ref;
	private String inout;
	private String tipo;
	private int sqlType;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String n) {
		this.nome = n;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String r) {
		this.ref = r;
	}
	public String getInout() {
		return inout;
	}
	public void setInout(String io) {
		this.inout = io;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String t) {
		this.tipo = t.toUpperCase();
        switch (this.tipo) { 
            case "VARCHAR": 
            	sqlType = Types.VARCHAR; 
                break; 
            default: 
            	sqlType = Types.VARCHAR;  
        } 
	}
	public int getSqlType() {
		return sqlType;
	}
	
}