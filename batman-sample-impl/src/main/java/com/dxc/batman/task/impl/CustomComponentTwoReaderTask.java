package com.dxc.batman.task.impl;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.dxc.batman.task.common.CommonDBCursorItemReader;

/**
 * Custom Reader class used for Scenario 2 and 3</br>
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */
public class CustomComponentTwoReaderTask extends CommonDBCursorItemReader<String> {

	private static Logger logger = LoggerFactory.getLogger(CustomComponentTwoReaderTask.class);

	public CustomComponentTwoReaderTask(DataSource ds, String query, RowMapper<String> rm) {
		super(ds, query, rm);
	}

}