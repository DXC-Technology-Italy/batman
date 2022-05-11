package com.dxc.batman.rowmapper.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * Custom RowMapper class used for Scenario 2 and 3</br>
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public class BatchJobInstanceRowMapper implements RowMapper<String> {

	@Override
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append(rs.getBigDecimal("JOB_INSTANCE_ID")).append(";");
		sb.append(rs.getBigDecimal("VERSION")).append(";");
		sb.append(rs.getString("JOB_NAME")).append(";");
		sb.append(rs.getString("JOB_KEY")).append(";");
		return sb.toString();
	}

}
