package com.dxc.batman.config.datasource.common;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
* Common Oracle DataSource Configuration for Your Application </br>
* 
* @author marco.fioriti@dxc.com
* @version 1.0
* @since 1.0
*
*/

@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
public class DataSourceCommonOracleConfig extends DataSourceCommonConfig {

	@Override
	protected EmbeddedDatabase dataSourceH2() {
		EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
	    return embeddedDatabaseBuilder
	            .setType(EmbeddedDatabaseType.H2)
	            .build();
	}

}