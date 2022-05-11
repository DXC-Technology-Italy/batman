package com.dxc.batman.helper;

import java.sql.Types;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * 
 * Activate a batch step
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public class PackageCallExecutor {
	private static Logger logger = LoggerFactory.getLogger(PackageCallExecutor.class);
	
	private PackageCall pkgCall;
	private DataSource dataSource;
	
	public PackageCallExecutor(DataSource ds, PackageCall pkgCall) {
		setDataSource(ds);
		setPkgCall(pkgCall);
	}
	
    public PackageCall getPkgCall() {
		return pkgCall;
	}

	public void setPkgCall(PackageCall pkgCall) {
		this.pkgCall = pkgCall;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}

	public RepeatStatus execute(StoredProcedureParam[] parms) throws Exception {
        logger.debug(getPkgCall().getNome() + " start..");

        if (logger.isDebugEnabled()) {
        	traceCall(getPkgCall().getNome(), parms);
        }

        StoredProcedureRunner.ProcedureType procType = (getPkgCall().isIsfunction()) 
        		? StoredProcedureRunner.ProcedureType.DB_FUNCTION 
        		: StoredProcedureRunner.ProcedureType.DB_PROCEDURE;

        StoredProcedureRunner proc = new StoredProcedureRunner(procType, getDataSource());
		Map<String, Object> results = proc.execProcedure(getPkgCall().getRef(), parms);

		if (logger.isDebugEnabled()) {
			results.forEach((k, v) -> logger.debug("Out: " + k + " " + v.toString()));
		}

		// Retrieving the value of output or input/output parameters
		if (procType == StoredProcedureRunner.ProcedureType.DB_PROCEDURE) {
			// The I/O values of the procedures are in the call parameters
			logger.debug("start overturning parameters " + getPkgCall().getRefParms().size());
			for (PackageCallParam pkgParm : getPkgCall().getRefParms()) {
				logger.debug("parameter: " + pkgParm.getNome() + " " + pkgParm.getInout());
				if (pkgParm.getInout().equalsIgnoreCase("O") || pkgParm.getInout().equalsIgnoreCase("IO")) {
					for (int i = 0; i < parms.length; i++) {
						logger.debug("parameter search: " + parms[i].getName() + " " + pkgParm.getRef());
						if (parms[i].getName().equalsIgnoreCase(pkgParm.getRef())) {
							parms[i].setValue(results.get(parms[i].getName()));
						}
					}
				}
			}
		} else {
			// The output value of a function is in the return parameter
			Optional<Map.Entry<String, Object>> oValue = results.entrySet().stream().findFirst();
			String resValue = oValue.isPresent()
					? oValue.get().getValue().toString() 
					: "0";
			logger.debug("Function Result: " + oValue.getClass() + " : " + resValue);
			//TODO generalize the type of return object
			parms[parms.length - 1] = new StoredProcedureParam(JobExecVarEnum.RET_VALUE.getValue(), Types.NUMERIC,  new Integer(resValue).intValue(), "O");
		}

		//TODO Manage return code
		if (logger.isDebugEnabled()) {
			traceCall(getPkgCall().getNome() + " (OUT)", parms);
		}

        logger.debug(getPkgCall().getNome() + " done..");
        return RepeatStatus.FINISHED;
    }   

    private void traceCall(String procedureName, StoredProcedureParam[] parms) {
		logger.debug("Call stored procedure/function " + procedureName + " with:");
    	for (int i = 0; i < parms.length; i++) {
    		StoredProcedureParam p = parms[i];
    		logger.debug("-- " + p.getName() + "=" + p.getValue());
    	}
    }
}