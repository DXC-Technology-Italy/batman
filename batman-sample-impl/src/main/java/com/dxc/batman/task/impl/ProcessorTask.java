package com.dxc.batman.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * Custom Processor class used for Scenario 1, 2, and 3</br>
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */
public class ProcessorTask implements ItemProcessor<String, String> {
	private static Logger logger = LoggerFactory.getLogger(ProcessorTask.class);

	@Override
	public String process(String data) throws Exception {
		Thread.sleep(5000);
		return data.toUpperCase();
	}

}
