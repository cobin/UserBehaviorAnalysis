package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper69.CallTask69Mapper;

public class WorkCallZHouKeVisitNotify extends WorkJob {
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

	public void execWork(BehaviorMain bm,String qDate){
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask69Mapper ct69 = bm.getMapper(CallTask69Mapper.class);
		loadNextVisitTime(ct111,ct69);
	}
	
	protected int loadNextVisitTime(CallTask111Mapper ct111,CallTask69Mapper ct69){
		List<Map<Object,Object>> result = ct69.queryNextVisitTime();
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		int execCount = 0;
		log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-NextVisit同步总数为:"+result.size());	
		for(Map<Object,Object> r:result){
			execCount++;
			iData.add(r);
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
				if(qData.size()>=3){
					log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount);						
					ct111.insertNextVisitTime(qData);
					qData.clear();
				}
			}
		}
		if(iData.size()>0){
			qData.add(iData);
		}
		if(qData.size()>0){
			ct111.insertNextVisitTime(qData);
		}
		return result.size();
	}
	
	public static final int insertSize = 1000;
}
