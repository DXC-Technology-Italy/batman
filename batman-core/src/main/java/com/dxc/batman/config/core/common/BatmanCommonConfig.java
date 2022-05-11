package com.dxc.batman.config.core.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.support.CronTrigger;

import com.dxc.batman.component.common.BatmanCommonComponent;
import com.dxc.batman.config.datasource.impl.DataSourceBatchConfig;
import com.dxc.batman.properties.ScenarioDirective;
import com.dxc.batman.rest.JobInvokerController;
import com.dxc.batman.scheduler.BatmanScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Configuration class that deals with core operations
 * 
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

@Configuration
@EnableScheduling
@EnableBatchProcessing
public class BatmanCommonConfig {
	private static Logger logger = LoggerFactory.getLogger(BatmanCommonConfig.class);

	@Inject
	private DataSourceBatchConfig dsBatchConfig;

	@Autowired
	@Lazy
	private JobExplorer jobExplorer;
	
	@Autowired
	@Lazy
	private JobRegistry jobRegistry;

	@Autowired
	@Lazy
	private JobOperator jobOperator;
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	@Lazy
	private JobLocator jobLocator;
	
	@Inject
	private ScenarioDirective scenari;

	private JobBuilderFactory jobBuilderFactory;
	private JobRepository jobRepository;
	private JobLauncher jobLauncher;
	private TaskExecutor taskExecutor;
	
	public JobRegistry getJobRegistry() {
		return jobRegistry;
	}
	
	public JobBuilderFactory getJobBuilderFactory() throws Exception {
		if (jobBuilderFactory == null) {
			jobBuilderFactory = createJobBuilderFactory();
		}
		return jobBuilderFactory;
	}

	private JobBuilderFactory createJobBuilderFactory() throws Exception {
		return new JobBuilderFactory(getJobRepository());
	}

	public JobRepository getJobRepository() throws Exception {
		if (jobRepository == null) {
			jobRepository = createJobRepository();
		}
		return jobRepository;
	}
	
	public JobLauncher getJobLauncher() throws Exception {
		if (jobLauncher == null) {
			jobLauncher = createJobLauncher();
		}
		return jobLauncher;
	}
	
	public TaskExecutor getTaskExecutor() {
		if (taskExecutor == null) {
			taskExecutor = createTaskExecutor();
		}
		return taskExecutor;
	}

	@Bean("batmanTransactionManager")
	public ResourcelessTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}
	
	/**
	 * Returns a {@link TaskExecutor} that will be used to manage the execution threads
	 * 
	 * @return
	 */
	@Bean
	@Lazy
	public TaskExecutor createTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(15);
		taskExecutor.setMaxPoolSize(20);
		taskExecutor.setQueueCapacity(30);
		taskExecutor.initialize();
		return taskExecutor;
	}
	
	/**
	 * Returns a default {@link JobLauncher} 
	 * 
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Lazy
	public JobLauncher createJobLauncher() throws Exception {
		SimpleJobLauncher jl = new SimpleJobLauncher();
		jl.setTaskExecutor(getTaskExecutor());
		jl.setJobRepository(getJobRepository());
		jl.afterPropertiesSet();
		return jl;
	}
	
	/**
	 * Returns a default {@link TaskScheduler} of type {@link BatmanScheduler}
	 * 
	 * @return
	 */
	@Bean
    public TaskScheduler poolScheduler() {
        return new BatmanScheduler();
    }
	
	/**
	 * Returns a default {@link JobOperator}
	 * 
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Lazy
	public JobOperator jobOperator() throws Exception {
		SimpleJobOperator jo = new SimpleJobOperator();
		jo.setJobExplorer(jobExplorer);
		jo.setJobLauncher(getJobLauncher());
		jo.setJobRegistry(jobRegistry);
		jo.setJobRepository(getJobRepository());
		return jo;
	}


	/**
	 * Returns a default {@link JobRepository} with directives to the primary DataBase connection
	 * 
	 * @return
	 * @throws Exception
	 */
	@Lazy
	protected JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		if (dsBatchConfig.isEmbedded()) {
			factory.setDatabaseType(DatabaseType.H2.getProductName());
		}
		factory.setDataSource(dsBatchConfig.getDataSource());
	    factory.setTransactionManager(transactionManager());
	    return factory.getObject();
	}
	
	/**
	 * Method that takes care of running the {@link Job} </br>
	 * Before running, check if there is another instance of the same {@link Job} in run 
	 * preventing it from launching
	 * 			
	 * @param job Represents the Job to be executed
	 * @param jobParameters Represents job execution parameters
	 * 
	 * @throws JobExecutionAlreadyRunningException
	 * @throws JobRestartException
	 * @throws JobInstanceAlreadyCompleteException
	 * @throws JobParametersInvalidException
	 * @throws NoSuchJobExecutionException
	 */
	public void run(Job job, JobParameters jobParameters) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, NoSuchJobExecutionException {
		try {
			String jobName = job.getName();
	    	if (isRunningJobExecutions(jobName)) {
	    		logger.warn("Job " + jobName + " still running");
	    		return;	    	
	    	} 
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

		try {
			getJobLauncher().run(job, jobParameters);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	/**
	 * Check if there are other run instances of the {@link Job}
	 * 
	 * @param jobName Represents the name of the Job to search for
	 * @return
	 */
	private boolean isRunningJobExecutions(String jobName) {
		Iterator<JobExecution> je = findRunningJobExecutions(jobName);
		return je.hasNext();
	}
	
	/**
	 * Extracts from {@link JobExplorer} the active executions of the {@link Job}
	 * 
	 * @param jobName Represents the name of the Job to search for
	 * @return
	 */
	private Iterator<JobExecution> findRunningJobExecutions(String jobName) {
		Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobName);
		return jobExecutions.iterator();
	}
	
	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Takes care of searching in the Spring context for the corresponding <code>batchName</code> bean, 
	 * if it is found activates the run of the {@link Job} by changing its property <code>enabled</code> 
	 * if this is <code>false</code> 
	 * 
	 * @param batchName Represents the name of the Job to be activated
	 * @return
	 */
	public String activateJob(String batchName) {
		StringBuilder sb = new StringBuilder();
		if (context.containsBean(batchName)) {
			BatmanCommonComponent b = (BatmanCommonComponent) context.getBean(batchName);
			if (b.activate()) {
				sb.append(batchName + " found and activated </br>");
			} else {
				sb.append(batchName + " found but already active </br>");
			}
		} else {
			sb.append(batchName + " NOT found </br>");
		}
		return sb.toString();
	}
	
	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Extract from {@link JobRegistry} all the {@link Job} present and activate the run  
	 * by changing its property <code>enabled</code> if this is <code>false</code> 
	 *  
	 * @return
	 */
	public String activateAllJob() {
		StringBuilder sb = new StringBuilder();
		Collection<String> c = jobRegistry.getJobNames();
		if (!c.isEmpty()) {
			for (String jName : c) {
				BatmanCommonComponent b = (BatmanCommonComponent) context.getBean(jName);
				if (b.activate()) {
					sb.append(jName + " activated </br>");
				} else {
					sb.append(jName + " already active </br>");
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Enable the scheduling of all batches in the {jobRegistry @link} 
	 * with their CRON expressions defined in the properties file
	 * 
	 * @return
	 * @throws NoSuchMethodException
	 */
	public String startAllSchedules() {
		StringBuilder sb = new StringBuilder();
		BatmanScheduler bs = (BatmanScheduler) context.getBean("poolScheduler");
		for (String s : jobRegistry.getJobNames()) {
			BatmanCommonComponent bcc = (BatmanCommonComponent) context.getBean(s);
			try {
				bs.schedule(bcc.getRunnableSchedule(), new CronTrigger(bcc.getCron()));
				sb.append(bcc.getBatchName() + " scheduled with trigger " + bcc.getCron() + " <br>");
			} catch (NoSuchMethodException e) {
				sb.append(bcc.getBatchName() + " ERROR in scheduling: " + e.getMessage() + " <br>");
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Takes care of searching in the Spring context for the corresponding <code>batchName</code> bean, 
	 * if it is found disable the run of the {@link Job} by changing its property <code>enabled</code> 
	 * if this is <code>true</code> 
	 * 
	 * @param batchName Rappresenta il nome del Job da disattivare
	 * @return
	 */
	public String disactivateJob(String batchName) {
		StringBuilder sb = new StringBuilder();
		if (context.containsBean(batchName)) {
			BatmanCommonComponent b = (BatmanCommonComponent) context.getBean(batchName);
			if (b.disactivate()) {
				sb.append(batchName + " found and deactivated </br>");
			} else {
				sb.append(batchName + " found but already deactivated </br>");
			}
		} else {
			sb.append(batchName + " NOT found </br>");
		}
		return sb.toString();
	}
	
	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Extract from {@link JobRegistry} all the {@link Job} present and disable the run  
	 * by changing its property <code>enabled</code> if this is <code>true</code> 
	 * 
	 * @return
	 */
	public String disactivateAllJob() {
		StringBuilder sb = new StringBuilder();
		Collection<String> c = jobRegistry.getJobNames();
		if (!c.isEmpty()) {
			for (String jName : c) {
				BatmanCommonComponent b = (BatmanCommonComponent) context.getBean(jName);
				if (b.disactivate()) {
					sb.append(jName + " deactivated </br>");
				} else {
					sb.append(jName + " already deactivated </br>");
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Stop all schedules by removing from the {@link BatmanScheduler} 
	 * all the {@link ScheduledFuture} of the {@link Job}
	 * 
	 * @return
	 */
	public String stopAllScheduling() {
		StringBuilder sb = new StringBuilder();
		BatmanScheduler bs = (BatmanScheduler) context.getBean("poolScheduler");
		Map<Object, ScheduledFuture<?>> st = bs.getScheduledTasks();
		List<Object> toRemove = new ArrayList<>();
		/*
		 * NOTE:
		 * It is not performing to execute the loop twice  
		 * but it serves to avoid a ConcurrentModificationException 
		 * The alternative is to invoke the <code>cancelFutureSchedulerTasks</code>
		 * method of {@link BatchCommonComponent} without arguments
		 * avoiding the two loops but in that case you will not have 
		 * the names of the stopped schedules
		 * 
		 */
		for (Object k : st.keySet()) {
			toRemove.add(k);
		}
		for (Object k : toRemove) {
			BatmanCommonComponent bcc = (BatmanCommonComponent) k;
			bs.cancelFutureSchedulerTasks(k.getClass());
			sb.append(bcc.getBatchName() + " deschedulate<br>");
		}
		return sb.toString();
	}
	
	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Extracts from the Spring Context the list of all the beans contained
	 * 
	 * @return
	 */
	public String listAllBeans() {
		StringBuilder sb = new StringBuilder();
		String[] s = context.getBeanDefinitionNames();
		for (String string : s) {
			sb.append(string + " </br>");
		}
		return sb.toString();
	}

	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Starting from the {@link Job} registered in the {@link JobRegistry} 
	 * returns the list of those that have an active execution at the moment
	 * 
	 * @return
	 */
	public String listRunningJob() {
		StringBuilder sb = new StringBuilder();
	    Collection<String> c = jobRegistry.getJobNames();
		if (!c.isEmpty()) {
	    	sb.append("RunningJob exists: </br>");
	    	boolean isFind = Boolean.FALSE;
			for (String jName : c) {
				if (isRunningJobExecutions(jName)) {
					isFind = Boolean.TRUE;
					sb.append("- " + jName + "</br>");
				}
			}
			if (!isFind) {
				sb.append("- There are no tasks in running! </br>");
			}
		} else {
			sb.append("There are no jobs defined in the registry! </br>");
		}
		return sb.toString();
	}

	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Returns the list of {@link Job} that have an active schedule 
	 * at the {@link ScheduledFuture} level in the {@link BatmanScheduler}
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	public String listScheduledJob() {
		StringBuilder sb = new StringBuilder();
		BatmanScheduler bs = (BatmanScheduler) context.getBean("poolScheduler");
		Map<Object, ScheduledFuture<?>> st = bs.getScheduledTasks();
		for (Object iterable_element : st.keySet()) {
			sb.append("- " + iterable_element.getClass().getName() + "</br>");
		}
		return sb.toString();
	}
	
	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Returns the list of {@link Job} in the {@link jobregistry}
	 * 
	 * @return
	 */
	public String listRegistry() {
		StringBuilder sb = new StringBuilder();
		for (String s : jobRegistry.getJobNames()) {
			sb.append("- " + s + "</br>");
		}
		return sb.toString();
	}
	
	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Takes care of searching in the Spring context for the corresponding <code>batchName</code> bean, 
	 * if it is found activates the run of the {@link Job} by changing its property <code>enabled</code> 
	 * if this is <code>false</code>, calls the <code>run</code> method then turn it off
	 * 
	 * @param batchName
	 * @return
	 * @throws Exception
	 */
	public String runJob(String batchName) throws Exception {
		StringBuilder sb = new StringBuilder();
		if (context.containsBean(batchName)) {
			BatmanCommonComponent b = (BatmanCommonComponent) context.getBean(batchName);
			if (b.activate()) {
				sb.append(batchName + " found, activated, started </br>");
			} else {
				sb.append(batchName + " found already active, started </br>");
			}
			b.run();
			b.disactivate();
		} else {
			sb.append(batchName + " NOT found </br>");
		}
		return sb.toString();
	}

	/**
	 * Exposed method for REST calls (see {@link JobInvokerController}) </br>
	 * 
	 * Takes care of searching in the Spring context for the corresponding <code>batchName</code> bean, 
	 * then deschedule, if any, all the {@link ScheduledFuture} for the <code>batchName</code> 
	 * and reschedules it by setting the new <code>cronExpression</code>
	 * 
	 * @param batchName
	 * @param cronExpression
	 * @return
	 * @throws NoSuchMethodException
	 */
	public String cronSchedules(String batchName, String cronExpression) throws NoSuchMethodException {
		StringBuilder sb = new StringBuilder();
		
		BatmanScheduler bs = (BatmanScheduler) context.getBean("poolScheduler");
		Map<Object, ScheduledFuture<?>> st = bs.getScheduledTasks();
		for (Object iterable_element : st.keySet()) {
			if (!(iterable_element instanceof BatmanCommonComponent)) continue;
			BatmanCommonComponent bcc = (BatmanCommonComponent) iterable_element;
			if (bcc.getBatchName().equals(batchName)) {
				bs.cancelFutureSchedulerTasks(iterable_element.getClass());
				sb.append(batchName + " is descheduled from previous cron trigger " + bcc.getCron() + " </br>");
				break;
			}
		}
		if (context.containsBean(batchName)) {
			BatmanCommonComponent b = (BatmanCommonComponent) context.getBean(batchName);
			b.setCron(cronExpression);
			bs.schedule(b.getRunnableSchedule(), new CronTrigger(b.getCron()));
			sb.append(batchName + " is now scheduled with trigger " + cronExpression + " </br>");
		} else {
			sb.append(batchName + " NOT found </br>");
		}
		return sb.toString();
	}

	public ScenarioDirective getScenari() {
		return scenari;
	}

}