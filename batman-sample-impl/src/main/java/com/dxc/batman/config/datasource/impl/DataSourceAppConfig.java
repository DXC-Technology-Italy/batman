package com.dxc.batman.config.datasource.impl;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.dxc.batman.config.datasource.common.DataSourceCommonOracleConfig;

/**
* Common configuration for Application DataSources </br>
* It is the secondary datasource (see {@link DataSourceBatchConfig})
* 
* @author marco.fioriti@dxc.com
* @version 1.0
* @since 1.0
*
*/

@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
public class DataSourceAppConfig extends DataSourceCommonOracleConfig {

	@Bean(name = "appDataSource")
	public DataSource getDataSource() {
		return super.getDataSource();
	}
	@Override
	@Value("${datasource.app.embedded:false}")
	public void setEmbedded(boolean embedded) {
		super.setEmbedded(embedded);
	}
	@Override
	@Value("${datasource.app.jdbc:false}")
	public void setJdbc(boolean jdbc) {
		super.setJdbc(jdbc);
	}
	@Override
	@Value("${datasource.app.jdbcName:#{null}}")
	public void setJdbcName(String jdbcName) {
		super.setJdbcName(jdbcName);
	}
	@Override
	@Value("${datasource.app.url:#{null}}")
	public void setUrl(String url) {
		super.setUrl(url);
	}
	@Override
	@Value("${datasource.app.username:#{null}}")
	public void setUserName(String userName) {
		super.setUserName(userName);
	}
	@Override
	@Value("${datasource.app.password:#{null}}")
	public void setPassword(String password) {
		super.setPassword(password);
	}
	@Override
	@Value("${datasource.app.driver:#{null}}")
	public void setDriver(String driver) {
		super.setDriver(driver);
	}
}