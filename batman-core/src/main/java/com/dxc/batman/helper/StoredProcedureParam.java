package com.dxc.batman.helper;

/**
 * stored procedure parameter
 * 
 * @author marco.fioriti@dxc.com
 *
 */
public class StoredProcedureParam {
	/**
	 * nome interno (i.e. the one referenced by the stored procedure)
	 */
	private String name;
	/**
	 * sql type of the parameter
	 */
	private int type;
	/**
	 * parameter value
	 */
	private Object value;
	/**
	 * Parameter I/O definition
	 */
	private String dir;
	
	public StoredProcedureParam(String spName, int spType, Object spValue, String spDir) {
		this.name = spName;
		this.type = spType;
		this.value = spValue;
		this.dir = spDir;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String n) {
		this.name = n;
	}
	public int getType() {
		return type;
	}
	public void setType(int t) {
		this.type = t;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object v) {
		this.value = v;
	}
	public String getDir() {
		return dir.toUpperCase();
	}
	public void setDir(String d) {
		this.dir = d;
	}
	public boolean isOutput() {
		return (dir.equalsIgnoreCase("I")) ? false : true;
	}
}