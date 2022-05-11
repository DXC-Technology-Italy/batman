package com.dxc.batman.rest;
 
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dxc.batman.config.core.common.BatmanCommonConfig;

/**
 * Class that exposes REST services to govern the Spring Batch environment</br>
 * Through the invocations it is possible to carry out:
 * <ul>
 * <li><code>listRunningJob</code>: Starting from the Jobs registered in the JobRegistry 
 * returns the list of those that have an active execution at the moment
 * 
 * <li><code>listScheduledJob</code>: Returns the list of Jobs that have an active schedule 
 * at the ScheduledFuture level in the BatchScheduler
 * 
 * <li><code>listRegistry</code>: Returns the list of Jobs in the JobRegistry
 * 
 * <li><code>listAllBeans</code>: Convenient service, extracts from the Spring Context 
 * the list of all the beans contained
 * 
 * <li><code>activateJob</code>: through the parameter <code>jobName</code> takes care of looking 
 * in the Spring context for the corresponding bean, if it is found activates the run of the job 
 * by changing the enabled property if this is false
 * 
 * <li><code>activateAllJob</code>: Extract from JobRegistry all the Jobs present and activate 
 * the run by changing the enabled property if this is false
 * 
 * <li><code>disactivateJob</code>: through the parameter <code>jobName</code> takes care of searching 
 * in the Spring context for the corresponding bean, if it is found deactivates the run of the job 
 * by changing the enabled property if this is true
 * 
 * <li><code>disactivateAllJob</code>: Extract all the Jobs present from JobRegistry and disable 
 * the run by changing the enabled property if this is true
 * 
 * <li><code>startAllSchedules</code>: Enables the scheduling of all batches in the JobRegistry 
 * with their CRON expressions defined in the properties file
 * 
 * <li><code>stopAllScheduling</code>: Stop all schedules by removing all Job ScheduledFuture 
 * from the BatchScheduler
 * 
 * <li><code>runJob</code>: through the parameter <code>jobName</code> takes care of searching 
 * in the Spring context for the corresponding bean, if it is found activates the run of the Job 
 * by changing its enabled property if this is false and invokes the method run, 
 * after which it deactivates it
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

@RestController
public class JobInvokerController implements ErrorController {
	private static Logger logger = LoggerFactory.getLogger(JobInvokerController.class);
	
	private static final String START = "---START---</br>";
	private static final String END = "---END---</br>";
	private static final String HTML = "<html><head><title>%s</title></head><body><h1>%s</h1>%s %s %s</body></html>";
	
	@Autowired
	public BatmanCommonConfig batchCommonConfig;
 
    @RequestMapping(value = "/listRunningJob")
    public String listRunningJob() throws Exception {
        return risposta(batchCommonConfig.listRunningJob(), "listRunningJob");
    }
    
    @RequestMapping(value = "/listScheduledJob")
    public String listScheduledJob() throws Exception {
        return risposta(batchCommonConfig.listScheduledJob(), "listScheduledJob");
    }
    
    @RequestMapping(value = "/listRegistry")
    public String listRegistry() throws Exception {
        return risposta(batchCommonConfig.listRegistry(), "listRegistry");
    }
    
    @RequestMapping(value = "/listAllBeans")
    public String listAllBeans() throws Exception {
        return risposta(batchCommonConfig.listAllBeans(), "listAllBeans");
    }
    
    @RequestMapping(value = "/activateJob", params = "jobName")
    public String activateJob(@RequestParam("jobName") String jobName) throws Exception {
        return risposta(batchCommonConfig.activateJob(jobName), "activateJob");
    }
    
    @RequestMapping(value = "/activateAllJob")
    public String activateAllJob() throws Exception {
        return risposta(batchCommonConfig.activateAllJob(), "activateAllJob");
    }

    @RequestMapping(value = "/disactivateJob", params = "jobName")
    public String disactivateJob(@RequestParam("jobName") String jobName) throws Exception {
        return risposta(batchCommonConfig.disactivateJob(jobName), "disactivateJob");
    }

    @RequestMapping(value = "/disactivateAllJob")
    public String disactivateAllJob() throws Exception {
        return risposta(batchCommonConfig.disactivateAllJob(), "disactivateAllJob");
    }

    @RequestMapping(value = "/startAllSchedules")
    public String startAllSchedules() throws Exception {
        return risposta(batchCommonConfig.startAllSchedules(), "startAllSchedules");
    }

    @RequestMapping(value = "/stopAllScheduling")
    public String stopAllScheduling() throws Exception {
        return risposta(batchCommonConfig.stopAllScheduling(), "stopAllScheduling");
    }
    
    @RequestMapping(value = "/runJob", params = "jobName")
    public String runJob(@RequestParam("jobName") String jobName) throws Exception {
        return risposta(batchCommonConfig.runJob(jobName), "runJob");
    }
    
    @RequestMapping(value = "/cronSchedules", params = {"jobName", "cron"})
    public String cronSchedules(@RequestParam("jobName") String jobName
    							, @RequestParam("cron") String cron) throws Exception {
        return risposta(batchCommonConfig.cronSchedules(jobName, cron), "cronSchedules");
    }
    
	@RequestMapping("/error")
	public String handleError(HttpServletRequest request) {
	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    if (status != null) {
	        Integer statusCode = Integer.valueOf(status.toString());
	        if (statusCode == HttpStatus.NOT_FOUND.value()) {
	            return risposta("error-404</br>", "ERROR");
	        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	            return risposta("error-500</br>", "ERROR");
	        }
	    }
	    return risposta("error</br>", "ERROR");
	}
	
	private String risposta(String risp, String metodo) {
    	return String.format(HTML, metodo, metodo, START, risp, END);
    }

}