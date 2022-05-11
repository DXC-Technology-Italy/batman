package com.dxc.batman.step.generic;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.jdbc.core.JdbcTemplate;

import com.dxc.batman.properties.Scenario;

/**
 * Interface for {@link Job} Tasklet </br>
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public interface IGenericTask extends Tasklet {
	public Scenario getScenario();
	public void setScenario(Scenario scenario);
	public DataSource getDataSource();
	public void setDataSource(DataSource dataSource);
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate);
	public JdbcTemplate getJdbcTemplate();
}