package com.dxc.batman.config.datasource.common;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
* Common configuration for Application DataSources </br>
* It is the secondary datasource (see {@link DataSourceBatchConfig)
* to use to define links to the application-specific database
* 
* @author marco.fioriti@dxc.com
* @version 1.0
* @since 1.0
*
*/

@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
public class DataSourceCommonConfig {
	private boolean embedded;
	private boolean jdbc;
	private String jdbcName;
	private String url;
	private String userName;
	private String password;
	private String driver;

	public DataSource getDataSource() {
		if (embedded) {
			return dataSourceH2();
		} 
		if (jdbc) {
			return new JndiDataSourceLookup()
					.getDataSource(jdbcName);
		} else {
	        return DataSourceBuilder
	                .create()
	                .url(url)
	                .username(userName)
	                .password(password)
	                .driverClassName(driver)
	                .build();
		}
	}
	
	/**
	 * Returns points to H2 Embedded Database 
	 * 
	 * @return
	 * @throws Exception
	 */
	protected EmbeddedDatabase dataSourceH2() {
		EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
	    return embeddedDatabaseBuilder
	    		.addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
	            .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
	            .setType(EmbeddedDatabaseType.H2)
	            .build();
	}

	public boolean isEmbedded() {
		return embedded;
	}
	public void setEmbedded(boolean e) {
		this.embedded = e;
	}
	public boolean isJdbc() {
		return jdbc;
	}
	public void setJdbc(boolean jdbc) {
		this.jdbc = jdbc;
	}
	public String getJdbcName() {
		return jdbcName;
	}
	public void setJdbcName(String jdbcName) {
		this.jdbcName = jdbcName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String u) {
		this.url = u;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String un) {
		this.userName = un;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String p) {
		this.password = p;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String d) {
		this.driver = d;
	}

}