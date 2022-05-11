package com.dxc.batman.listner.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * Default listner class of {@link Step} 
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public class StepListeners implements StepExecutionListener {
	private static Logger logger = LoggerFactory.getLogger(StepListeners.class);
	
	@Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}