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
public class WorkCallXZTelallocStatNotify extends WorkJob {
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
		curQuery = new CDate();//"2017-11-28"
		if(curQuery.getHourOfDay()<12) {
			curQuery.addDate(-1);
		}
		while(true) {
			loadTelallocStat(ct111, ct69);
			curQuery.addDate(1);
			if(curQuery.getIntDate()>CDate.getInstance().getIntDate()) {
				break;
			}
		}
	}	
	
	protected int loadTelallocStat(CallTask111Mapper ct111,CallTask69Mapper ct69){
		try{			
			log.debug(TAG+">TelallocStat查询日期："+curQuery);
			List<Map<Object,Object>> result = ct69.queryEbSalesRelTelallocStat(curQuery.toShortDate());
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			int execCount = 0;
			log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-TelallocStat同步总数为:"+result.size());	
			for(Map<Object,Object> r:result){
				execCount++;
				for(String key:keys){
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
					if(qData.size()>=1){
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据为:"+execCount);						
						ct111.insertEbSalesRelTelallocStat(qData);
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){
				ct111.insertEbSalesRelTelallocStat(qData);
			}
			return result.size();
		}catch (Exception e) { 
			log.error(e);
		}
		return 0;
	}
	
	private String[] keys = {"CNAME","STATDATE"};
	private CDate curQuery = null;
	public static final int insertSize = 600;
}
