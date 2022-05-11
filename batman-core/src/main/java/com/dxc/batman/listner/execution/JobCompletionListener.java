package com.dxc.batman.listner.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

/**
 * Default listner class of {@link Job}
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public class JobCompletionListener extends JobExecutionListenerSupport {
	private static Logger logger = LoggerFactory.getLogger(JobCompletionListener.class);
	
	private String batchName;
	
	public JobCompletionListener(String bn) {
		this.batchName = bn;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		logger.debug(batchName + " in the beforeJob method");
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		logger.debug(batchName + " in the afterJob method - status: " + jobExecution.getStatus());
	}

}