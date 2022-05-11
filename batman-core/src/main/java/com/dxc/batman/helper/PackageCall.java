package com.dxc.batman.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Package invoked by a scenario
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public class PackageCall {
	private static Logger logger = LoggerFactory.getLogger(PackageCall.class);
	public enum PARMS_TYPE { CUSTOM, DEFAULT, EMPTY };
	
	private String nome;
	private String ref;
	private boolean isfunction;
	private String refparmstype;
	private List<PackageCallParam> refparms = new ArrayList<>();
	private Map<String, PackageCallParam> paramRefs;
	private int outCounter;
	private int inCounter;
	private int inOutCounter;

	public void setRefParms(List<PackageCallParam> refparms) {
		this.refparms = refparms;
		logger.debug("Loading refparms (" + getNome() + "):");
		paramRefs = new LinkedHashMap<String, PackageCallParam>();
		for (PackageCallParam param : this.refparms) {
			paramRefs.put(param.getNome(), param);
			logger.debug("- loaded refparms " + param.getNome());
			switch(param.getInout().toUpperCase()) {
				case "I": 
					++inCounter;
					break;
				case "O": 
					++outCounter;
					break;
				default: 
					++inOutCounter;
			}
		}
	}
	
	public PackageCallParam getPackageParam(String nomeParm) {
		PackageCallParam pkgParm = paramRefs.get(nomeParm);
		if (pkgParm == null) {
			logger.warn("Package param [" + nomeParm + "] not registered for the package [" + nome + "]");
		}
		return pkgParm;
	}
	
	public PARMS_TYPE getRefparmstype() {
		switch (refparmstype.toUpperCase()) {
			case "CUSTOM": return PARMS_TYPE.CUSTOM;
			case "EMPTY": return PARMS_TYPE.EMPTY;
			default: return PARMS_TYPE.DEFAULT;
		}
	}
	
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
	public List<PackageCallParam> getRefParms() {
		return refparms;
	}
	public Map<String, PackageCallParam> getParamRefs() {
		return paramRefs;
	}
	public void setParamRefs(Map<String, PackageCallParam> p) {
		this.paramRefs = p;
	}
	public boolean isIsfunction() {
		return isfunction;
	}
	public void setIsfunction(boolean isf) {
		this.isfunction = isf;
	}
	public void setRefparmstype(String refparmstype) {
		this.refparmstype = refparmstype;
	}
	public int getInCounter() {
		return inCounter;
	}
	public int getOutCounter() {
		return outCounter;
	}
	public int getInOutCounter() {
		return inOutCounter;
	}
}