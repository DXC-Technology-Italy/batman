package com.dxc.batman.decider.common;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatStatus;

import com.dxc.batman.helper.PackageCall;
import com.dxc.batman.helper.PackageCallExecutor;
import com.dxc.batman.helper.StoredProcedureParam;

/**
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public abstract class CommonDBPackageCallDecider extends CommonAbstractDBDecider {
	private static Logger logger = LoggerFactory.getLogger(CommonDBPackageCallDecider.class);
	
	private PackageCall pkgCall;
	private PackageCallExecutor pkgCallExecutor;
	
	public CommonDBPackageCallDecider(DataSource dataSource, PackageCall pkgCall) {
		super();
		setDataSource(dataSource);
		setPkgCall(pkgCall);
		setPkgCallExecutor(new PackageCallExecutor(dataSource, pkgCall));
	}
	
	public void setPkgCall(PackageCall pkgCall) {
		this.pkgCall = pkgCall;
	}
	
	public PackageCall getPkgCall() {
		return pkgCall;
	}

	public PackageCallExecutor getPkgCallExecutor() {
		return pkgCallExecutor;
	}

	public void setPkgCallExecutor(PackageCallExecutor pkgCallExecutor) {
		this.pkgCallExecutor = pkgCallExecutor;
	}
	
	protected RepeatStatus execute(StoredProcedureParam[] parms) throws Exception {
		return getPkgCallExecutor().execute(parms);
	}
	
	protected StoredProcedureParam getParm(StoredProcedureParam[] parms, String parmName) {
		for (int i = 0; i < parms.length; i++) {
			if (parms[i].getName().equalsIgnoreCase(parmName)) {
				return parms[i];
			}
		}
		return null;
	}

}