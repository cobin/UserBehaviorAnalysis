package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper1110.CallTask1110Mapper;
import com.cobin.util.CDate;
/**
 * @author  Cobin
 * @date    2019/12/17 17:13
 * @version 1.0
*/ 
public class WorkCallPerson3600Notify extends WorkJob {
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
		String sDate = qDate; 
		if(qDate==null){			 
			sDate = CDate.formatShortDate(System.currentTimeMillis()-24*60*60*4*1000);
		}
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask1110Mapper ct1110 = bm.getMapper(CallTask1110Mapper.class);
		long cTime =System.currentTimeMillis()-60*60*1000;
		int cDate = ct111.queryUserFunctionStat3600MaxDate(sDate);
		if(cDate>0){
			sDate = String.valueOf(cDate);
		}
		List<Map<Object,Object>> users  = ct1110.queryPerson3600(null);
		log.debug(TAG+">处理:"+users.size()+",开始日期为:"+sDate);
		CDate date = new CDate(sDate);
		date.addDate(1);
		while(date.getTime()<cTime){
			loadUser(ct111,users,date.getIntDate()); 
			date.addDate(1);
		}
	}

	
	protected void loadUser(CallTask111Mapper ct111,List<Map<Object,Object>> users,int sDate){
		List<List<Map<Object,Object>>> qList = new ArrayList<>();
		List<Map<Object,Object>> cList = new ArrayList<>();
		int execCount = 0;	
		for(Map<Object,Object> row:users){
			execCount++;
			row.put("sourceId", execCount);
			row.put("sdate", sDate);
			cList.add(row);	
			if(cList.size()>=insertSize){
				qList.add(cList);
				cList = new ArrayList<>();
				if(qList.size()>=2){
					log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,3600目标用户开始"+sDate+"执行编号为:"+execCount);						
					callUserFunctionStat(ct111, sDate, qList);
					callUserActiveStocks(ct111, sDate, qList);
					qList.clear();
				}
			}			
		}
		if(cList.size()>=1){
			qList.add(cList); 				
		}
		if(qList.size()>0){
			log.debug(Thread.currentThread().getName()+">"+TAG+">3600目标用户开始"+sDate+"执行编号为:"+execCount);			
			callUserFunctionStat(ct111, sDate, qList); 
			callUserActiveStocks(ct111, sDate, qList);
			qList.clear();
		}			
	}
	
	public void callUserFunctionStat(CallTask111Mapper ct111,int logDate,List<List<Map<Object,Object>>> qList){
		List<Map<Object,Object>> result = ct111.queryUserFunctionStat(logDate,qList,null);
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		for(Map<Object,Object> r:result){
			iData.add(r);
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
			}
		}
		if(iData.size()>0){
			qData.add(iData);
		}
		if(qData.size()>0){
			ct111.insertUserFunctionStat3600(qData); 
		}
	}
	
	public void callUserActiveStocks(CallTask111Mapper ct111,int logDate,List<List<Map<Object,Object>>> qList){
		try{
			List<Map<Object,Object>> result = ct111.queryUserActiveStocks(logDate,qList);
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			for(Map<Object,Object> r:result){			
				iData.add(r);
				if(iData.size()>=insertSize){
					qData.add(iData);
					iData = new ArrayList<>();
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){			
				ct111.insertUserActiveStocks3600(qData);
			}
		}catch(Exception ex){
			log.error(ex);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) { 
			}
			callUserActiveStocks(ct111,logDate,qList);
		}
	}
	public static final int insertSize = 1000;
}
