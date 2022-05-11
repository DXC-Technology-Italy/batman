package com.dxc.batman.decider.generic;

import javax.sql.DataSource;

import org.springframework.batch.core.job.flow.JobExecutionDecider;

/**
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public interface IGenericDBDecider extends JobExecutionDecider {
	public DataSource getDataSource();
	public void setDataSource(DataSource dataSource);
}