package com.dxc.batman.step.common;

import javax.sql.DataSource;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import com.dxc.batman.helper.StoredProcedureParam;
import com.dxc.batman.properties.Scenario;
import com.dxc.batman.step.generic.IGenericTask;

/**
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public abstract class AbstractTask implements IGenericTask {
    private DataSource dataSource;
    private Scenario scenario;
    private JdbcTemplate jdbcTemplate;

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}

	@Override
	public Scenario getScenario() {
		return scenario;
	}

	@Override
	public void setScenario(Scenario s) {
		this.scenario = s;
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public void setJdbcTemplate(JdbcTemplate ft) {
		this.jdbcTemplate = ft;
	}

	@Override
	public abstract RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception;
	
	@SuppressWarnings("unused")
	private StoredProcedureParam getParm(StoredProcedureParam[] parms, String parmName) {
		for (int i =  0; i < parms.length; i++) {
			if (parms[i].getName().equalsIgnoreCase(parmName)) {
				return parms[i];
			}
		}
		
		return null;
	}
}
