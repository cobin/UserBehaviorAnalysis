package com.behavior.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.behavior.mapper.mapper1114.CallTask1114Mapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;

/**
 * @author  Cobin
 * @date    2019/12/17 17:16
 * @version 1.0
*/
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
			CallTask1114Mapper ct1113 = bm.getMapper(CallTask1114Mapper.class);
			List<Map<String,Integer>> acts = ct1113.getSmallOrderWapActInfo();
			for(Map<String,Integer> act:acts) {
				Map<Object, Object> qParam = new HashMap<>(20);
				qParam.put("Opid", 2021394);
				qParam.put("actId", act.get("actId"));
				qParam.put("searchType", 1);
				qParam.put("startDate", null);
				qParam.put("endDate", null);
				log.info(TAG + ">Wap新资源处理(成单_B_呼叫):" + qParam);
				//执行1 部门
				ct1113.updateSmallOrderWap(qParam);
				qParam.put("searchType", 2);
				log.info(TAG + ">Wap新资源处理(成单_B_呼叫):" + qParam);
				//执行1	小组
				ct1113.updateSmallOrderWap(qParam);
				qParam.put("searchType", 3);
				log.info(TAG + ">Wap新资源处理(成单_B_呼叫):" + qParam);
				//执行1	个人
				ct1113.updateSmallOrderWap(qParam);
				qParam.put("searchType", 4);
				log.info(TAG + ">Wap新资源处理(成单_B_呼叫):" + qParam);
				//执行4	中心
				ct1113.updateSmallOrderWap(qParam);
				log.info(TAG + ">Wap新资源处理(成单_B_呼叫):处理结果：" + qParam);
			}
//			c.addDate(1);
//			if(c.getIntDate()>=20191024) break;
//		}
	}
}
