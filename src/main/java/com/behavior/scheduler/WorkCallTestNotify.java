package com.behavior.scheduler;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;

/**
 * @author  Cobin
 * @date    2019/7/24 17:02
 * @version 1.0
*/
@DisallowConcurrentExecution
public class WorkCallTestNotify extends WorkJob {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			BehaviorMain bm = (BehaviorMain)arg0.getScheduler().getContext().get("context");
			execWork(bm, null);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void execWork(BehaviorMain bm,String qDate){
		
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
		log.info(Thread.currentThread().getName()+">>>>TEST====="+System.currentTimeMillis());
	}	
}
