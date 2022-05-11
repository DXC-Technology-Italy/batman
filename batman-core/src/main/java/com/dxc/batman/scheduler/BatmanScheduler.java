package com.dxc.batman.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

/**
 * Custom {@link ThreadPoolTaskScheduler} thread handling class
 * The property <code>scheduledTasks</code> contains the {@link ScheduledFuture} of threads 
 * whose removal leads to deschedulation
 * 
 * @author marco.fioriti@dxc.com
 * @version 1.0
 * @since 1.0
 *
 */

public class BatmanScheduler extends ThreadPoolTaskScheduler {
	private static Logger logger = LoggerFactory.getLogger(BatmanScheduler.class);
	
	private static final long serialVersionUID = 1L;
	
    private final Map<Object, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
    	ScheduledFuture<?> future = super.schedule(task, startTime);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
    	ScheduledFuture<?> future = super.schedule(task, startTime);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
    	ScheduledFuture<?> future = super.schedule(task, trigger);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);
        return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
    	ScheduledFuture<?> future = super.scheduleAtFixedRate(task, startTime, period);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
    	ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
    	ScheduledFuture<?> future = super.scheduleAtFixedRate(task, startTime, period);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
    	ScheduledFuture<?> future = super.scheduleWithFixedDelay(task, startTime, delay);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
    	ScheduledFuture<?> future = super.scheduleWithFixedDelay(task, delay);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
    	ScheduledFuture<?> future = super.scheduleWithFixedDelay(task, startTime, delay);
    	return setTask(task, future);
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
    	ScheduledFuture<?> future = super.scheduleWithFixedDelay(task, delay);
    	return setTask(task, future);
    }

	private ScheduledFuture<?> setTask(Runnable task, ScheduledFuture<?> future) {
		ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
        scheduledTasks.put(runnable.getTarget(), future);
        return future;
	}

	public Map<Object, ScheduledFuture<?>> getScheduledTasks() {
		return scheduledTasks;
	}

	/**
	 *  Method that clears all schedules of {@link ScheduledFuture} and cleans up the task list
	 */
	public void cancelFutureSchedulerTasks() {
        scheduledTasks.forEach((k, v) -> {
            v.cancel(false);
        });
        scheduledTasks.clear();
    }
	
	/**
	 * Method that clears the single schedule of the {@link scheduledfuture} 
	 * and removes it from the task list
	 */
	public <T> void cancelFutureSchedulerTasks(Class<T> c) {
		List<Object> toRemove = new ArrayList<>();
        scheduledTasks.forEach((k, v) -> {
            if (c.isInstance(k)) {
                v.cancel(false);
                toRemove.add(k);
            }
        });
        for (Object k : toRemove) {
        	scheduledTasks.remove(k);
		}
    }
}