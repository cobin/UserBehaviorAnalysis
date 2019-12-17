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
@PersistJobDataAfterExecution
@DisallowConcurrentExecution //// 不允许并发执行
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
		String wappcRegions = bm.getConfig("wappc.regions");
		if(wappcRegions==null) {
			wappcRegions="11,12,13,41,61";
		}		
		List<Map<Object,Object>> list = ct1112.queryMonitorWapPcAdept(wappcRegions);
		Map<Object,Object> qParam1 = new HashMap<>();
		qParam1.put("Opid", 2021394);
		qParam1.put("sDate", null);	
		//qParam1.put("unitId", 61);
		log.info(TAG+">开始处理WapPc的新资源>部门个数:"+list.size()+">>>"+list.get(list.size()-1));
		try {
			int listCount = 0;
			for(Map<Object,Object> adept:list) {
				qParam1.put("unitId", adept.get("deptId"));
				log.info(TAG+">>"+(++listCount)+">>开始处理部门:"+adept.get("deptId"));
				ct1112.updateMonitorWapPc(qParam1);
			}
		}catch(Exception ex) {
			log.error(ex);
		}
		log.info(TAG+">调用WapPc新资源结果："+qParam1);	
	}
}
