package com.dxc.batman.task.generic;

import javax.sql.DataSource;

import org.springframework.batch.core.step.tasklet.Tasklet;

/**
 * Interface for Tasklets using DB connection </br>
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public interface IGenericDBTasklet extends Tasklet {
	public DataSource getDataSource();
	public void setDataSource(DataSource dataSource);
}