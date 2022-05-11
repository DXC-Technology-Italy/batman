package com.dxc.batman.task.common;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatStatus;

import com.dxc.batman.helper.PackageCall;
import com.dxc.batman.helper.PackageCallExecutor;
import com.dxc.batman.helper.StoredProcedureParam;

/**
 *  
 * Common Tasklet class per Stored Procedure or Function </br>
 *  
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public abstract class CommonDBPackageCallTasklet extends CommonAbstractDBTasklet {
	private static Logger logger = LoggerFactory.getLogger(CommonDBPackageCallTasklet.class);
	
	private PackageCall pkgCall;
	private PackageCallExecutor pkgCallExecutor;
	
	public CommonDBPackageCallTasklet(DataSource ds, PackageCall pc) {
		super();
		setDataSource(ds);
		setPkgCall(pc);
		setPkgCallExecutor(new PackageCallExecutor(ds, pc));
	}
	
	public PackageCall getPkgCall() {
		return pkgCall;
	}
	
	public void setPkgCall(PackageCall pc) {
		this.pkgCall = pc;
	}

	public PackageCallExecutor getPkgCallExecutor() {
		return pkgCallExecutor;
	}

	public void setPkgCallExecutor(PackageCallExecutor pce) {
		this.pkgCallExecutor = pce;
	}
	
	protected RepeatStatus execute(StoredProcedureParam[] parms) throws Exception {
		return getPkgCallExecutor().execute(parms);
	}
}