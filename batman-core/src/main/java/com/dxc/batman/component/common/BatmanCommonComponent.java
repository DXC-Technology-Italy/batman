package com.dxc.batman.component.common;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import com.dxc.batman.config.core.common.BatmanCommonConfig;
import com.dxc.batman.helper.JobExecVarEnum;
import com.dxc.batman.listner.execution.JobCompletionListener;
import com.dxc.batman.properties.Scenario;
import com.dxc.batman.rest.JobInvokerController;

/**
 * Abstract Component for a {@link Job}. </br>
 * It is equipped with the property <code>enabled</code> that allows the run </br>
 * 
 * In implementations it is very important to set in:
 * <ul>
 * <li>the properties</li>
 * <li>the constructor</li>
 * <li>and in the Component</li>
 * </ul>
 * the same name that will identify the {@link Job} within the Spring context
 * and will allow the start/ stop through REST calls (see also {@link JobInvokerController})
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

@Component
@EnableScheduling
@EnableBatchProcessing
public abstract class BatmanCommonComponent {
  private static Logger logger = LoggerFactory.getLogger(BatmanCommonComponent.class);

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private BatmanCommonConfig batchConfigCommon;
	
	private String batchName;
	private Scenario scenario;
	private String cron;
	
	/**
	 * A property used to determine whether the {@link Job} can be started or not
	 * It is possible to change the status with the methods <code>activate()</code> and <code>disactivate()</code>
	 */
	private AtomicBoolean enabled = new AtomicBoolean(Boolean.FALSE);

	/**
	 * Register the name of the {@link Job} in the register {@link JobRegistry} 
	 * of the Spring context used 
	 *  
	 * @throws DuplicateJobException If the name is already in the registry
	 * @throws Exception For problems when creating the {@link Job}
	 */
	@PostConstruct
	private void register() throws DuplicateJobException, Exception {
		setScenario();
		getBatchConfigCommon().getJobRegistry().register(new ReferenceJobFactory(getJob()));
		logger.debug("Added job in registry " + getBatchName());
	}
	
	/**
	 * He deals with:
	 * <ul>
	 * <li>assign a <code>JobID</code> based on the system timestamp to {@link Job}
	 * <li>create and assign {@link JobParameters} related to the execution of the {@link Job}
	 * <li>assign the <code>scenario</code> (see {@link Scenario}) reference read by properties
	 * <li>run the {@link Job} if the property <code>enabled</code> is <code>true</code>
	 * </ul>
	 * 
	 * @throws NoSuchJobException 
	 * @throws NoSuchJobExecutionException 
	 * @throws JobParametersInvalidException 
	 * @throws JobInstanceAlreadyCompleteException 
	 * @throws JobRestartException 
	 * @throws JobExecutionAlreadyRunningException 
	 * @throws Exception
	 */
	public void run() throws NoSuchJobException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, NoSuchJobExecutionException {
		logger.debug(getBatchName() + " enabled >" + enabled.get() + "<");
		String JobID = String.valueOf(System.currentTimeMillis());
		JobParameters params = getJobParameters(JobID);
		Job j = getBatchConfigCommon().getJobRegistry().getJob(getBatchName());
		if (enabled.get()) {
			batchConfigCommon.run(j, params);
		}
	}

	/**
	 * Generate default {@link JobParameters} </br>
	 * Specifically, the parameters are set: 
	 * <ul>
	 * <li><code>JobID</code> from the value of the parameter <code>id</code>
	 * <li><code>time</code> based on system timestamp
	 * </ul>
	 * 
	 * @param id Represents the JobID
	 * @return
	 */
	protected JobParameters getJobParameters(String id) {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
	    jobParametersBuilder.addLong("time", System.currentTimeMillis());
	    jobParametersBuilder.addString(JobExecVarEnum.BATCH_JOB_ID.getValue(), id);
	    return jobParametersBuilder.toJobParameters();
	}
	
	/**
	 * Generate default {@link Job} </br>
	 * These properties are set: 
	 * <ul>
	 * <li><code>beanName</code> by the <code>getBatchName()</code> method
	 * <li>{@link JobExecutionListner} with the default listner
	 * <li>default <code>step</code> by the <code>getStep()</code> method (see {@link #getStep()})
	 * <li>{@link JobRepository} from the Spring context
	 * </ul>
	 * To add more {@link Step} you need to override the implementation class
	 * 
	 * @return
	 * @throws Exception
	 */
	protected Job getJob() throws Exception {
		SimpleJob j = new SimpleJob(getBatchName());
		j.setBeanName(getBatchName());
		j.registerJobExecutionListener(getJobExecutionListner());
		j.addStep(getStep());
		j.setJobRepository(getBatchConfigCommon().getJobRepository());
		j.afterPropertiesSet();
		return j;
	}
	
	/**
	 * Abstract method to be implemented in subclasses.</br>
	 * 
	 * It will have to deal with the creation of the {@link Step}
	 * 
	 * @return
	 */
	protected abstract Step getStep();

	/**
	 * Abstract method to be implemented in subclasses.</br>
	 * 
	 * It is designed to be the method with common name to be able to schedule
	 * and normally its implementation will call only the <code>run()</code>
	 * 
	 * @throws Exception
	 */
	public abstract void schedule() throws Exception;
	
	/**
	 * Returns a {@link ScheduledMethodRunnable} of the common method <code>schedule()</code>
	 * to be used for the scheduling of {@link Job}
	 * 
	 * @return
	 * @throws NoSuchMethodException
	 */
	public Runnable getRunnableSchedule() throws NoSuchMethodException {
		ScheduledMethodRunnable smr;
		try {
			smr = new ScheduledMethodRunnable(this, "schedule");
		} catch (NoSuchMethodException e) {
			logger.error("The schedule method does not exist in the class " + getClass().getName());
			e.printStackTrace();
			throw e;
		}
		return smr;
	}
	
	/**
	 * Returns a {@link JobExecutionListener} with a default implementation
	 * of type {@link JobCompletionListener}
	 * 
	 * @return
	 */
	protected JobExecutionListener getJobExecutionListner() {
		return new JobCompletionListener(getBatchName());
	}
	
	public String getBatchName() {
		return batchName;
	}
	
	public void setBatchName(String bm) {
		this.batchName = bm;
	}
	
	public Scenario getScenario() {
		return scenario;
	}
	
	public void setScenario() {
		this.scenario = getBatchConfigCommon().getScenari().getScenario(getBatchName());
	}
	
	public BatmanCommonConfig getBatchConfigCommon() {
		return batchConfigCommon;
	}
	
	public StepBuilderFactory getStepBuilderFactory() {
		return stepBuilderFactory;
	}
	
	public String getCron() {
		return cron;
	}
	
	public void setCron(String c) {
		this.cron = c;
	}

	/**
	 * invoking this method triggers the run of the scheduled {@link Job}
	 * 
	 * @return
	 */
	public boolean activate() {
		boolean activate = Boolean.FALSE;
		if (!enabled.get()) {
			enabled.set(!enabled.get());
			activate = Boolean.TRUE;
			logger.debug("Job " + getBatchName() + " activated!");
		} else {
			logger.debug("Job " + getBatchName() + " is already active");
		}
		return activate;
    }

	/**
	 * invoking this method disables the run of the scheduled {@link Job}
	 * 
	 * @return
	 */
	public boolean disactivate() {
		boolean disactivate = Boolean.FALSE;
		if (enabled.get()) {
			enabled.set(!enabled.get());
			disactivate = Boolean.TRUE;
			logger.debug("Job " + getBatchName() + " deactivated!");
		} else {
			logger.debug("Job " + getBatchName() + " is already deactivated");
		}
		return disactivate;
    }
}