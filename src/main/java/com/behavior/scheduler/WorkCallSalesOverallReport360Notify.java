package com.behavior.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper9103.CallTask9103Mapper;
/**
 * @author  Cobin
 * @date    2020/3/28 10:12
 * @version 1.0
*/
public class WorkCallSalesOverallReport360Notify extends WorkJob {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			BehaviorMain bm = (BehaviorMain)arg0.getScheduler().getContext().get("context");			
			execWork(bm,null);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void execWork(BehaviorMain bm,String sdate ){		 
		CallTask9103Mapper ct9103 = bm.getMapper(CallTask9103Mapper.class); 				
		Map<Object,Object> qParam = new HashMap<>();
		qParam.put("sdate", null);		
		qParam.put("actId", null);	
		ct9103.updateUpgradeControlCenterAll360DSData(qParam); 
		log.info(TAG+">调用定时收集电商部中控信息："+qParam);
		ct9103.updateUpgradeControlCenterAll360XDData(qParam); 
		log.info(TAG+">调用定时收集小单部中控信息："+qParam);
	}
	
}
