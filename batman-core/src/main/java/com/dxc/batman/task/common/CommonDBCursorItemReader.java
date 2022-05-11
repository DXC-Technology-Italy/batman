package com.dxc.batman.task.common;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.RowMapper;

/**
 * Common Reader class used to read the database</br>
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public class CommonDBCursorItemReader<T> extends JdbcCursorItemReader<T> implements ItemReader<T> {
	private static Logger logger = LoggerFactory.getLogger(CommonDBCursorItemReader.class);
	
	public CommonDBCursorItemReader(DataSource ds, String query, RowMapper<T> rm) {
		super();
		setDataSource(ds);
		setSql(query);
		setRowMapper(rm);
	}
    
}