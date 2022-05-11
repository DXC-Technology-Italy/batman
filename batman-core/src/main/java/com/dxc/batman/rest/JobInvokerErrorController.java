package com.dxc.batman.rest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;

/**
 * Error Controller
 * 
 * @author marco.fioriti@dxc.com
 *
 */

public class JobInvokerErrorController implements ErrorController  {
	private static Logger logger = LoggerFactory.getLogger(JobInvokerErrorController.class);
	
	private static final String START = "---START---</br>";
	private static final String END = "---END---</br>";
	private static final String HTML = "<html><head><title>%s</title></head><body><h1>%s</h1>%s %s %s</body></html>";
	
	public String handleError(HttpServletRequest request) {
	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    if (status != null) {
	        Integer statusCode = Integer.valueOf(status.toString());
	        if (statusCode == HttpStatus.NOT_FOUND.value()) {
	            return risposta("error-404", "ERROR");
	        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	            return risposta("error-500", "ERROR");
	        }
	    }
	    return risposta("error", "ERROR");
	}
	
    private String risposta(String risp, String metodo) {
    	return String.format(HTML, metodo, metodo, START, risp, END);
    }

}