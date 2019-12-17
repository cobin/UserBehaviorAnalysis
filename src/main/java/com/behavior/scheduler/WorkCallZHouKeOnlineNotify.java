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
import com.behavior.mapper.mapper76.CallTask76Mapper;
import com.cobin.util.CDate;
@PersistJobDataAfterExecution
@DisallowConcurrentExecution //// 不允许并发执行
public class WorkCallZHouKeOnlineNotify extends WorkJob {
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
		CallTask76Mapper ct76 = bm.getMapper(CallTask76Mapper.class);
		int nDate = ct111.getUserOnlineMaxDate();
		if(nDate>0){
			sDate = String.valueOf(nDate);
		}
		CDate date = new CDate(sDate);
		if(nDate>0){
			date.addDate(1);
		}
		while(date.getTime()<System.currentTimeMillis()){
			//加载用户成单之前的查看股票情况
			loadUser(ct111,ct76,date.getIntDate()); //全部资源
			date.addDate(1);
		}
	}
		
	protected void loadUser(CallTask111Mapper ct111,CallTask76Mapper ct76,int sDate){
		List<Map<Object,Object>> result = ct76.queryUserOnline(sDate);
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		int execCount = 0;
		log.debug(Thread.currentThread().getName()+">"+TAG+">日期<"+sDate+">同步总数为:"+result.size());		
		for(Map<Object,Object> r:result){
			execCount++;
			iData.add(r);
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
				if(qData.size()>=3){
					log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始"+sDate+"执行编号为:"+execCount);						
					ct111.insertUserOnline(qData);
					qData.clear();
				}
			}
		}
		if(iData.size()>0){
			qData.add(iData);
		}
		if(qData.size()>0){
			ct111.insertUserOnline(qData);
		}
	}
	
	public static final int insertSize = 1000;
}
