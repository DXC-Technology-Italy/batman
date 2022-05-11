package com.dxc.batman.decider.common;

import javax.sql.DataSource;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;

import com.dxc.batman.decider.generic.IGenericDBDecider;

/**
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public abstract class CommonAbstractDBDecider implements IGenericDBDecider {
    private DataSource dataSource;

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}

	public abstract FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution);

}