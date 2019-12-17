package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.cobin.util.CDate;
@PersistJobDataAfterExecution
@DisallowConcurrentExecution //// 不允许并发执行
public class WorkCallZHouKeNotify extends WorkJob {
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
//		CallTask91Mapper ct91 = bm.getMapper(CallTask91Mapper.class);
		long cTime =System.currentTimeMillis()-8*60*60*1000;
		int nDate = ct111.getZhouKeMaxDate360();
//		nDate = 20190504;
		if(nDate>0){
			sDate = String.valueOf(nDate);
		}
		
		CDate date = new CDate(sDate);
		if(nDate>0){
			date.addDate(1);
		}	
		log.debug("日期:"+date);
		while(date.getTime()<cTime){
			List<Map<Object,Object>> users = ct111.querySourceZK360(date.toDate());	
			if(!users.isEmpty()) {
				//加载用户成单之前的查看股票情况
				loadUser(ct111,users,date.getIntDate()); //全部资源
			}
			date.addDate(1);
		}
	}
		
	protected void loadUser(CallTask111Mapper ct111,List<Map<Object,Object>> users,int sDate){
		List<List<Map<Object,Object>>> qList = new ArrayList<>();
		List<Map<Object,Object>> cList = new ArrayList<>();
		int execCount = 0;
		log.debug(TAG+">处理人数:"+users.size());
		for(Map<Object,Object> row:users){
			execCount++;
			row.put("userType", 1);
			cList.add(row);	
			if(cList.size()>=insertSize){
				qList.add(cList);
				cList = new ArrayList<>();
				if(qList.size()>=50){
					log.debug(Thread.currentThread().getName()+">"+TAG+">功能及股票开始"+sDate+"执行编号为:"+execCount);						
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
			log.debug(Thread.currentThread().getName()+">"+TAG+">功能及股票开始"+sDate+"执行编号为:"+execCount);			
			callUserFunctionStat(ct111, sDate, qList); 
			callUserActiveStocks(ct111, sDate, qList); 
			qList.clear();
		}
	}
	
	public void callUserFunctionStat(CallTask111Mapper ct111,int logDate,List<List<Map<Object,Object>>> qList){
		try{
			List<Map<Object,Object>> result = ct111.queryUserFunctionStat(logDate,qList,null);
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			log.debug(TAG+">处理功能:"+result.size());
			int _eCount = 0;
			for(Map<Object,Object> r:result){
				iData.add(r);
				if(iData.size()>=insertSize){
					_eCount++;
					qData.add(iData);
					iData = new ArrayList<>();
					if(qData.size()>=50){
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,功能开始"+logDate+"执行编号为:"+_eCount);						
						ct111.insertUserFunctionStatZK(qData);
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){
				ct111.insertUserFunctionStatZK(qData);
			}
		}catch(Exception ex){
			log.error(ex);			
			//callUserFunctionStat(ct111,logDate,qList);
		}
	}
	
	public void callUserActiveStocks(CallTask111Mapper ct111,int logDate,List<List<Map<Object,Object>>> qList){
		try{
			List<Map<Object,Object>> result = ct111.queryUserActiveStocks(logDate,qList);
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			log.debug(TAG+">处理股票:"+result.size());
			int _eCount = 0;
			for(Map<Object,Object> r:result){			
				iData.add(r);
				if(iData.size()>=insertSize){
					_eCount++;
					qData.add(iData);
					iData = new ArrayList<>();
					if(qData.size()>=50){
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,股票开始"+logDate+"执行编号为:"+_eCount);						
						ct111.insertUserActiveStocksZK(qData);
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){			
				ct111.insertUserActiveStocksZK(qData);
			}
		}catch(Exception ex){
			log.error(ex);
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) { 
//			}
			//callUserActiveStocks(ct111,logDate,qList);
		}
	}
	
	public static final int insertSize = 1000;
}
