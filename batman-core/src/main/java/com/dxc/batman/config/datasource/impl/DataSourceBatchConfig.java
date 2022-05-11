package com.dxc.batman.config.datasource.impl;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.dxc.batman.config.datasource.common.DataSourceCommonConfig;

 /**
 *Common configuration for Batch DataSources </br>
 * It is the main datasource annotated with @Primary </br>
 * It is necessary to make it explicit in order to operate on two different databases: 
 * the one for the Spring Batch {@link JobRepository} (this) 
 * and the one for the application data (see {@link DataSourceCommonConfig}) 
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
public class DataSourceBatchConfig extends DataSourceCommonConfig {

	@Bean(name = "batchDataSource")
	@Primary
	@Override
	public DataSource getDataSource() {
		return super.getDataSource();
	}
	
	@Override
	@Value("${datasource.batch.embedded:false}")
	public void setEmbedded(boolean embedded) {
		super.setEmbedded(embedded);
	}
	@Override
	@Value("${datasource.batch.jdbc:false}")
	public void setJdbc(boolean jdbc) {
		super.setJdbc(jdbc);
	}
	@Override
	@Value("${datasource.batch.jdbcName:#{null}}")
	public void setJdbcName(String jdbcName) {
		super.setJdbcName(jdbcName);
	}
	@Override
	@Value("${datasource.batch.url:#{null}}")
	public void setUrl(String url) {
		super.setUrl(url);
	}
	@Override
	@Value("${datasource.batch.username:#{null}}")
	public void setUserName(String userName) {
		super.setUserName(userName);
	}
	@Override
	@Value("${datasource.batch.password:#{null}}")
	public void setPassword(String password) {
		super.setPassword(password);
	}
	@Override
	@Value("${datasource.batch.driver:#{null}}")
	public void setDriver(String driver) {
		super.setDriver(driver);
	}
}