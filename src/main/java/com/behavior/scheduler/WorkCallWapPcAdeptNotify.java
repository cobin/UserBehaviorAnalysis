package com.behavior.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper1112.CallTask1112Mapper;
/**
 * @author  Cobin
 * @date    2019/12/17 17:19
 * @version 1.0
 * DisallowConcurrentExecution //// 不允许并发执行
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallWapPcAdeptNotify extends WorkJob {
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
		List<Map<String,Integer>> acts=ct1112.queryMonitorWapPcActId();
		String wappcRegions = bm.getConfig("wappc.regions");
		if(wappcRegions==null) {
			wappcRegions="11,12,13,41,61";
		}
		for(Map<String,Integer> m:acts) {
			List<Map<Object, Object>> list = ct1112.queryMonitorWapPcAdept(m.get("actId"),wappcRegions);
			Map<Object, Object> qParam1 = new HashMap<>();
			qParam1.put("Opid", 2021394);
			qParam1.put("sDate", null);
			qParam1.put("actId", m.get("actId"));
			//qParam1.put("unitId", 61);
			log.info(TAG+">开始处理WapPc的新资源>活动:"+m.get("actId")+",部门个数:"+list.size()+">>>"+list.get(list.size()-1));
			try {
				int listCount = 0;
				for (Map<Object, Object> adept : list) {
					qParam1.put("unitId", adept.get("deptId"));
					ct1112.updateMonitorWapPc(qParam1);
					log.info(TAG+">>"+(++listCount)+">>处理部门结果:"+qParam1);
				}
			} catch (Exception ex) {
				log.error(ex);
			}
			log.info(TAG+">调用WapPc新资源处理完毕："+qParam1);
		}
	}
}
