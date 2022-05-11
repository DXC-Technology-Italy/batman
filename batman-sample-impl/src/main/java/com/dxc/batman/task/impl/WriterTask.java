package com.dxc.batman.task.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

/**
 * Custom Writer class used for Scenario 1, 2, and 3</br>
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */
public class WriterTask implements ItemWriter<String> {
	private static Logger logger = LoggerFactory.getLogger(WriterTask.class);

	@Override
	public void write(List<? extends String> messages) throws Exception {
		for (String msg : messages) {
			logger.debug("**Writing data: " + msg);
		}
	}

}