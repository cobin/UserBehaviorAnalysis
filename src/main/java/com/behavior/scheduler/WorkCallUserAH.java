package com.behavior.scheduler;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper91.CallTask91Mapper;
/**
 * @author  Cobin
 * @date    2019/12/17 17:16
 * @version 1.0
*/
public class WorkCallUserAH extends WorkJob {
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
		CallTask91Mapper mapper = bm.getMapper(CallTask91Mapper.class);
		
		List<Map<Object,Object>> ver2 =mapper.getVer2();
		int autoId = 0;
		int rowId = 0;
		for(Map<Object, Object> v:ver2) {
			rowId++;
			int verAutoId = (Integer)v.get("AutoID");
			List<Map<Object,Object>> ver1 = mapper.getVer1(verAutoId,autoId);
			
			if(ver1.size()>0) {
				log.debug(rowId+">>"+ver1.get(0));
				autoId = (Integer) ver1.get(0).get("AutoID");
				mapper.updateVer2(""+autoId, verAutoId);
			}			
		}
	}
	
}
