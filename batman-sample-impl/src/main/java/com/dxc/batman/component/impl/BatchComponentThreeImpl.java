package com.dxc.batman.component.impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.dxc.batman.component.common.BatmanCommonComponent;
import com.dxc.batman.config.datasource.impl.DataSourceAppConfig;
import com.dxc.batman.rest.JobInvokerController;
import com.dxc.batman.rowmapper.impl.BatchJobInstanceRowMapper;
import com.dxc.batman.task.impl.CustomComponentOneReaderTask;
import com.dxc.batman.task.impl.CustomComponentTwoReaderTask;
import com.dxc.batman.task.impl.ProcessorTask;
import com.dxc.batman.task.impl.WriterTask;

/**
 * Component class for {@link Job} that extends {@link BatmanCommonComponent}</br>
 * 
 * It is a test class to demonstrate the scheduling and the run of a {@link Job}</br>
 * Very important is to set in the properties, in the constructor and in the Component the same name 
 * that will identify the {@link Job} within the Spring context and will allow the start/stop 
 * via REST calls (see {@link JobInvokerController})</br></br>
 * 
 * <b>Scenario 3:</b> multi step, you will create by concatenating two steps using the previous scenarios: 
 * the first step will reproduce scenario 1 while the second step scenario 2
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

//Be aware of this name
@Component("BatchComponentThree")
@EnableScheduling
public class BatchComponentThreeImpl extends BatmanCommonComponent {
	private static Logger logger = LoggerFactory.getLogger(BatchComponentThreeImpl.class);
	
	@Inject
	DataSourceAppConfig dbDataSource;
	
	/**
	 * Only Constructor allowed</br>
	 * Very important is to set in the properties, in the constructor and in the Component the same name 
	 * that will identify the {@link Job} within the Spring context and will allow the start/stop 
	 * and scheduling/deschedulation via REST calls (see {@link JobInvokerController})
	 */
	public BatchComponentThreeImpl() {
		setBatchName("BatchComponentThree");
	}
	
	/**
	 * Implemented method on which you can decide during development whether:
	 * <ul>
	 * <li>schedule it immediately by adding the annotation @Scheduled with the appropriate CRON expression 
	 * <li>or leave the schedule to REST calls (see {@link JobInvokerController})
	 * </ul> 
	 * 
	 */
	public void schedule() throws Exception {
		run();
	}

	/**
	 * Method implemented by the abstract subclass {@link BatmanCommonComponent}</br>
	 * Allows the definition and customization of the {@link Step}
	 */
	public Step getStep() {
		CustomComponentOneReaderTask r = new CustomComponentOneReaderTask();
		ProcessorTask p = new ProcessorTask();
		WriterTask w = new WriterTask();
		
		return getStepBuilderFactory()
				.get("BatchComponentThreeStep")
				.<String, String>chunk(1)
				.reader(r)
				.processor(p)
				.writer(w)
				.build();
	}
	
	/**
	 * Custom private method by definition of other {@link Step}
	 * 
	 * @return
	 */
	private Step getOtherStep() {
		CustomComponentTwoReaderTask r = new CustomComponentTwoReaderTask(
												dbDataSource.getDataSource(),
												getScenario().getQuery("TEST_QUERY").getQuery(), 
												new BatchJobInstanceRowMapper()
											);
		ProcessorTask p = new ProcessorTask();
		WriterTask w = new WriterTask();

		return getStepBuilderFactory()
				.get("BatchComponentThreeOtherStep")
				.<String, String>chunk(1)
				.reader(r)
				.processor(p)
				.writer(w)
				.build();
    }

	/**
	 * Override required to be able to add a new {@link Step}
	 * 
	 */
	@Override
	protected Job getJob() throws Exception {
		SimpleJob j = (SimpleJob) super.getJob();
		j.addStep(getOtherStep());
		return j;
	}
	
}