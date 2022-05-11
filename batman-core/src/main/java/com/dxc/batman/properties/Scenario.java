package com.dxc.batman.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dxc.batman.helper.PackageCall;
import com.dxc.batman.helper.PackageCallParam;
import com.dxc.batman.helper.Query;

/**
 * directives of a single scenario
 * 
 * @author marco.fioriti@dxc.com
 *
 */
public class Scenario {
	private static Logger logger = LoggerFactory.getLogger(Scenario.class);
	
	private String codicescenario;
    private String nomescenario;
    private String cron;
	private List<PackageCallParam> defaultrefparms = new ArrayList<>();
	private Map<String, PackageCallParam> defaultParamRefs;
	
	private List<PackageCall> packages = new ArrayList<>();
	private Map<String, PackageCall> packageRefs;
	private List<Query> queries = new ArrayList<>();
	private Map<String, Query> queryRefs;
	
	public String getCodicescenario() {
		return codicescenario;
	}
	public void setCodicescenario(String cs) {
		this.codicescenario = cs;
	}
	public String getNomescenario() {
		return nomescenario;
	}
	public void setNomescenario(String ns) {
		this.nomescenario = ns;
	}
	public List<PackageCall> getPackages() {
		return packages;
	}
	
	public void setPackages(List<PackageCall> p) {
		this.packages = p;
		// Creazione reference packages per velocizzare le successive ricerche 
		logger.debug("Loading packages ");
		packageRefs = new HashMap<String, PackageCall>();
		for (PackageCall pkg : this.packages) {
			packageRefs.put(pkg.getNome(), pkg);
			logger.debug("Loaded package " + pkg.getNome());
		}
	}
	
	public void setdefaultrefparms(List<PackageCallParam> listdp) {
		this.defaultrefparms = listdp;	
		// Creation of reference default parameters to speed up subsequent searches
		logger.debug("Loading default parms");
		defaultParamRefs = new LinkedHashMap<String, PackageCallParam>();
		for (PackageCallParam parm : listdp) {
			defaultParamRefs.put(parm.getNome(), parm);
			logger.debug("Loaded parameter: " + parm.getNome());
		}
		// Overturning default parameters on all packages with default
		packageRefs.forEach((k, v) -> {
			PackageCall pkgCall = (PackageCall) v;
			if (pkgCall.getRefparmstype() == PackageCall.PARMS_TYPE.DEFAULT) {
				pkgCall.setRefParms(listdp);
			}
		});
	}
	
	public List<Query> getQueries() {
		return queries;
	}
	
	public void setQueries(List<Query> ListQueries) {
		this.queries = ListQueries;
		logger.debug("Loading queries ");
		queryRefs = new HashMap<String, Query>();
		for (Query query : this.queries) {
			queryRefs.put(query.getNome(), query);
			logger.debug("Query loaded " + query.getNome());
		}
	}
	
	public PackageCall getPackage(String nomePackage) {
		PackageCall pkg = packageRefs.get(nomePackage);
		if (pkg == null) {
			logger.warn("Package [" + nomePackage + "] not registered");
		}
		return pkg;
	}
	
	public Query getQuery(String nomeQuery) {
		Query q = queryRefs.get(nomeQuery);
		if (q == null) {
			logger.warn("Query [" + nomeQuery + "] not registered");
		}
		return q;
	}
	
	public PackageCallParam getDefaultParms(String nomeParm) {
		return defaultParamRefs.get(nomeParm);
	}
	public List<PackageCallParam> getDefaultParamRefs() {
		return defaultrefparms;
	}
	public void setCron(String c) {
		this.cron = c;
	}
	public String getCron() {
		return cron;
	}
}