package com.dxc.batman.helper;

import javax.sql.DataSource;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;

/**
 * Helper for a stored procedure/function run
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public class StoredProcedureRunner {
	private static Logger logger = LoggerFactory.getLogger(StoredProcedureRunner.class);
	public enum ProcedureType { DB_PROCEDURE, DB_FUNCTION };
	private Map<String, Integer> parmTypes;
	private Map<String, Object> parmValues;
	private Map<String, String> parmDirection;
	private StoredProcedure procedure;
	private boolean isFunction;
	
	/**
	 * Constructor
	 * @param type type of stored procedure (i.e. Procedures|Function)
	 * @param dataSource stored procedure repository
	 */
	public StoredProcedureRunner(ProcedureType type, DataSource dataSource) {
		procedure = new GenericStoredProcedure();
		procedure.setDataSource(dataSource);
		isFunction = type == ProcedureType.DB_FUNCTION
				? true 
				: false;
		procedure.setFunction(isFunction);
	}
	
	/**
	 * Executor 
	 * @param name Name of Procedure|Function
	 * @param parms parameters of the Procedure|Function
	 * @return output execution
	 */
	public Map<String, Object> execProcedure(String name, StoredProcedureParam[] parms) {
		loadParms(parms);

		procedure.setSql(name);
		parmTypes.forEach((k, v) -> {
			if (parmDirection.get(k).equalsIgnoreCase("I")) {
				procedure.declareParameter(new SqlParameter(k, v));
			} else {
				procedure.declareParameter(new SqlOutParameter(k, v));
			}
		});
		
		//TODO extend the type of data returned by a function
		if (isFunction) {
			procedure.declareParameter(new SqlOutParameter(name, Types.NUMERIC));
		}
		procedure.compile();

		if (logger.isDebugEnabled()) {
			if (parms.length > 0) {
				StringBuffer pt = new StringBuffer();
				parmTypes.forEach((k, v) -> pt.append(k + "=" + v + ", ")); 
				StringBuffer pv = new StringBuffer();
				parmValues.forEach((k, v) -> pv.append(k + "=" + v + ", ")); 
				logger.debug("Call statement: " + procedure.getSql() + " -> " + pt.toString() + " - " + pv.toString());
			} else {
				logger.debug("Call statement: " + procedure.getSql() + " -> no input parameters");
			}
		}
	    
	    return procedure.execute(parmValues);
	}
	
	private void loadParms(StoredProcedureParam[] parms) {
		// The last parameter of a function is the return value and therefore should not be valued 
		// for the call
		int totParms = isFunction
				? parms.length - 1 
				: parms.length;
		
		parmTypes     = new LinkedHashMap<String, Integer>();
		parmValues    = new LinkedHashMap<String, Object>();
		parmDirection = new LinkedHashMap<String, String>();
		for (int i = 0; i < totParms; i++) {
			parmTypes.put(parms[i].getName(), parms[i].getType());
			parmValues.put(parms[i].getName(), parms[i].getValue());
			parmDirection.put(parms[i].getName(), parms[i].getDir());
		}
	}

}
