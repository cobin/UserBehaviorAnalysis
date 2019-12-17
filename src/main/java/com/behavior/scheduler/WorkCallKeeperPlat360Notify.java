package com.behavior.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper1110.CallTask1110Mapper;
/**
 * @author  Cobin
 * @date    2019/7/24 16:58
 * @version 1.0
*/ 
public class WorkCallKeeperPlat360Notify extends WorkJob {
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
	public void execWork(BehaviorMain bm ,String sdate){		 
		CallTask1110Mapper ct1110 = bm.getMapper(CallTask1110Mapper.class); 
		Map<Object,Object> qParam = new HashMap<>();
		//qParam.put("sDate", 20160822);
		ct1110.updateKeeperPlatStatus360(qParam);
		log.info(TAG+">调用定时计算KeeperPlat结果："+qParam);
	}
	
	
}
