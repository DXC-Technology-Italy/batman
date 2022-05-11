package com.dxc.batman.task.common;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.StoredProcedureItemReader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.dxc.batman.helper.PackageCall;
import com.dxc.batman.helper.StoredProcedureParam;

/**
 * Common Reader class for Stored Procedure or Function </br>
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public abstract class CommonSPOrFunctionReader<T> extends StoredProcedureItemReader<T> {
	private static Logger logger = LoggerFactory.getLogger(CommonSPOrFunctionReader.class);
	
	private PackageCall pkgCall;
	private String jobId;
	
	public CommonSPOrFunctionReader(DataSource ds, boolean isFunction, RowMapper<T> rm, PackageCall pc, String jId) {
		super();
		setDataSource(ds);
		setFunction(isFunction);
		setRowMapper(rm);
		setPkgCall(pc);
		setJobId(jId);
		setParameters(overturningParameters());
	}

	public PackageCall getPkgCall() {
		return pkgCall;
	}

	public void setPkgCall(PackageCall pc) {
		this.pkgCall = pc;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jId) {
		this.jobId = jId;
	}

	protected abstract StoredProcedureParam[] overturningParameters();
	
	private void setParameters(StoredProcedureParam[] parms) {
        SqlParameter[] sp = new SqlParameter[8];
		logger.debug("start overturning parameters " + parms.length);
		for (int j = 0; j < parms.length; j++) {
			StoredProcedureParam spp = parms[j];
			if (spp.getDir().equalsIgnoreCase("I")) {
				sp[j] = new SqlParameter(spp.getName(), spp.getType());
			} else {
				sp[j] = new SqlOutParameter(spp.getName(), spp.getType());
			}
			logger.debug("parameter: " + spp.getName() + " " + spp.getDir());
		}
		setParameters(sp);
	}

}