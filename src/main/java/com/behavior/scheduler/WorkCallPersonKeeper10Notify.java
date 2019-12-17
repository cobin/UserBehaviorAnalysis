package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper69.CallTask69Mapper;
import com.cobin.util.CDate;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution //// 不允许并发执行
public class WorkCallPersonKeeper10Notify extends WorkJob {
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
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask69Mapper ct69 = bm.getMapper(CallTask69Mapper.class);
		int xCount=0;
		curQuery = null;
//		curQuery =(new CDate("2019-05-10 00:00:00")).toDate();
		while(true){
			loadPersonKeeper(ct111,ct69);			
			if(curQuery.getTime()>System.currentTimeMillis()){
				break;
			}
			xCount++;
			if(xCount>150)break;
		}
		if(curQuery!=null)
			ct111.updateEbPersonKeeper10(CDate.formatIntDate());
		
	}
	//
	protected int loadPersonKeeper(CallTask111Mapper ct111,CallTask69Mapper ct69){
		if(curQuery==null){
			curQuery = ct111.getEbPersonKeeper10MaxDate();
			curQuery.setTime(curQuery.getTime()-5000);
		}
		log.debug("Oracle-PersonKeeper-10>>查询开始时间为："+curQuery);
		Date date = new Date();
		date.setTime(curQuery.getTime()+loadSubTime);
		List<Map<Object,Object>> result = ct69.queryEbPersonKeeperLog10(curQuery,date);
		List<Map<Object,Object>> resultB01 = ct69.queryEbPersonPresaleLog10(curQuery,date);
		//		
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<List<Map<Object,Object>>> qDataB01 = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		List<Map<Object,Object>> iDataB01 = new ArrayList<>();
		
		int execCount = 0;
		log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-PersonKeeper-10同步总数为:"+result.size());	
		for(Map<Object,Object> r:result){
			for(String key:keysPersonKeeper){
				Object obj = r.get(key);
				if(obj==null){
					r.put(key, "NULL");
				}else if(obj instanceof String){
					r.put(key, "'"+obj+"'");
				}else if(obj instanceof Date){
					r.put(key, "'"+obj+"'");
				}				
			}
			iData.add(r);
			if(execCount<resultB01.size()) {
				Map<Object,Object> rb = resultB01.get(execCount);
				iDataB01.add(rb);
			}
			execCount++;
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
				if(!iDataB01.isEmpty()) {
					qDataB01.add(iDataB01);
					iDataB01 = new ArrayList<>();
				}
//				if(qData.size()>=3){
//					log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount);						
//					ct111.insertEbPersonKeeper10(qData);
//					qData.clear();
//				}
			}
		}
		if(iData.size()>0){
			qData.add(iData);
			if(!iDataB01.isEmpty()) {
				qDataB01.add(iDataB01);
			}
		}
		if(qData.size()>0){
			ct111.insertEbPersonKeeper10(qData,qDataB01);
		}
		curQuery.setTime(date.getTime()-5000);
		return result.size();
	}
	
	public static final int insertSize = 1000;
	public static final int loadSubTime = 24*60*60*1000;
	private Date curQuery = null;
	public static String[] keysPersonKeeper= {"LOGTIME","COMMENTS","OLDKEEPERNAME","NEWKEEPERNAME","FROMWHERE","DOMAIN","FINISHTIME"};
}
