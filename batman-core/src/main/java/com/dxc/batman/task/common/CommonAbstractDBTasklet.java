package com.dxc.batman.task.common;

import javax.sql.DataSource;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.dxc.batman.task.generic.IGenericDBTasklet;

/**
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public abstract class CommonAbstractDBTasklet implements IGenericDBTasklet {
    private DataSource dataSource;

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}

	@Override
	public abstract RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception;
	
}
