package com.behavior.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper91.CallTask91Mapper;

public class WorkCallProNotify extends WorkJob {
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
	public void execWork(BehaviorMain bm,String sdate){
		CallTask91Mapper ct91 = bm.getMapper(CallTask91Mapper.class);
		Map<Object,Object> para = new HashMap<>();
		//para.put("isNewDay", "1");
		ct91.updateWorkerOnCallLog(para);
		log.debug(TAG+">调用过程<WorkerOnCallLog>:"+para);
	}
}
