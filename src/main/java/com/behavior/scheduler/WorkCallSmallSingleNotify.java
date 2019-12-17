package com.behavior.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper1112.CallTask1112Mapper;

public class WorkCallSmallSingleNotify extends WorkJob {
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
		CallTask1112Mapper ct1112 = bm.getMapper(CallTask1112Mapper.class);		
		Map<Object,Object> qParam = new HashMap<>();
		qParam.put("Opid", 2021394);
		qParam.put("sDate", null);		
		log.info(TAG+">开始抓取小单资源数据:"+qParam);		
		ct1112.updateSmallSingleCompare(qParam); //执行原始数据整合			
		log.info(TAG+">调用抓取小单资源处理结果："+qParam);			
	}
}
