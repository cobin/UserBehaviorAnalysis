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
/**
 * @author  Cobin
 * @date    2019/12/17 17:14
 * @version 1.0
 * DisallowConcurrentExecution //// 不允许并发执行
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallPersonUnlockNotify extends WorkJob {
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
		while(true){
			loadPersonUnlock(ct111,ct69);			
			if(curQuery.getTime()>System.currentTimeMillis()){
				break;
			}
			xCount++;
			if(xCount>100){
				break;
			}
		}
	}
	
	protected int loadPersonUnlock(CallTask111Mapper ct111,CallTask69Mapper ct69){
		if(curQuery==null){
			curQuery = ct111.getEbPersonUnLockLogMaxDate();
			curQuery.setTime(curQuery.getTime()-1000);
		}
		log.debug("Oracle-PersonUnlock>>查询开始时间为："+curQuery);
		Date date = new Date();
		date.setTime(curQuery.getTime()+loadSubTime);
		List<Map<Object,Object>> result = ct69.queryEbPersonUnLockLog(curQuery,date);
	
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		int execCount = 0;
		log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-PersonUnlock同步总数为:"+result.size());	
		for(Map<Object,Object> r:result){
			execCount++;
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
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
				if(qData.size()>=3){
					log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount);						
					ct111.insertEbPersonUnLockLog(qData);
					qData.clear();
				}
			}
		}
		if(iData.size()>0){
			qData.add(iData);
		}
		if(qData.size()>0){
			ct111.insertEbPersonUnLockLog(qData);
		}
		curQuery.setTime(date.getTime()-1000);
		return result.size();
	}
	
	public static final int insertSize = 1000;
	public static final int loadSubTime = 8*60*60*1000;
	private Date curQuery = null;
	public static String[] keysPersonKeeper= {"LOGTIME","COMMENTS","PARA3","OPNAME"};
}
