package com.dxc.batman.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * Custom Reader class used for Scenario 1 and 3</br>
 * Simulate reading with a String array as input
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public class CustomComponentOneReaderTask implements ItemReader<String> {
	private static Logger logger = LoggerFactory.getLogger(CustomComponentOneReaderTask.class);

	private String[] messages = {
			"www.dxc.com",
			"this is an example message",
			"the message will be capitalized in the processor" 
	};

	private int count = 0;

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (count < messages.length) {
			return messages[count++];
		} else {
			count = 0;
		}
		return null;
	}
	
}