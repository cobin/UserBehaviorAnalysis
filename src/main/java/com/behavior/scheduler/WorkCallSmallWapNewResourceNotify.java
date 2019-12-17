package com.behavior.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper1113.CallTask1113Mapper;

public class WorkCallSmallWapNewResourceNotify extends WorkJob {
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
//		CDate c = new CDate("2019-10-09");	
//		for(;;) {
			CallTask1113Mapper ct1113 = bm.getMapper(CallTask1113Mapper.class);		
			Map<Object,Object> qParam = new HashMap<>();
			qParam.put("Opid", 2021394);
			qParam.put("searchType", 1);
			qParam.put("startDate", null);		
			qParam.put("endDate", null);		
			log.info(TAG+">Wap新资源处理(成单_B_呼叫):"+qParam);		
			ct1113.updateSmallOrderWap(qParam); //执行1 部门	
			qParam.put("searchType", 2);
			log.info(TAG+">Wap新资源处理(成单_B_呼叫):"+qParam);		
			ct1113.updateSmallOrderWap(qParam); //执行1	小组
			qParam.put("searchType", 3);
			log.info(TAG+">Wap新资源处理(成单_B_呼叫):"+qParam);		
			ct1113.updateSmallOrderWap(qParam); //执行1	个人
			qParam.put("searchType", 4);
			log.info(TAG+">Wap新资源处理(成单_B_呼叫):"+qParam);		
			ct1113.updateSmallOrderWap(qParam); //执行4	中心
			log.info(TAG+">Wap新资源处理(成单_B_呼叫):处理结果："+qParam);		
//			c.addDate(1);
//			if(c.getIntDate()>=20191024) break;
//		}
	}
}
