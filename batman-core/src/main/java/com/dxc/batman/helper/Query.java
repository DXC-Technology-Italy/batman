package com.dxc.batman.helper;

/**
 * Query performed by a scenario
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public class Query {
	private String nome;
	private String query;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String n) {
		this.nome = n;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String q) {
		this.query = q;
	}
}