package com.behavior.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper9102.CallTask9102Mapper;
import com.cobin.util.CDate;

public class WorkCallQuestionInquireWebNotify extends WorkJob {
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
		CallTask9102Mapper ct9102 = bm.getMapper(CallTask9102Mapper.class); 		
		String sDate =CDate.getInstance().addDate(-1).getShortDate();		
		String eDate =CDate.getInstance().addDate(1).getShortDate();		
		Map<Object,Object> qParam = new HashMap<>();
		qParam.put("startDate", sDate);		
		qParam.put("endDate", eDate);	
		ct9102.updateKeeperPersonAchievement(qParam); 
		log.info(TAG+">调用定时抓取业绩结果："+qParam);
	}
	
}
