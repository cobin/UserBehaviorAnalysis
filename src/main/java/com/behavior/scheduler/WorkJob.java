package com.behavior.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;

import com.behavior.BehaviorMain;

public abstract class WorkJob implements Job {
	protected String TAG = getClass().getSimpleName();
	protected Log log = LogFactory.getLog(getClass());
	public abstract void execWork(BehaviorMain bm,String qDate);
}
