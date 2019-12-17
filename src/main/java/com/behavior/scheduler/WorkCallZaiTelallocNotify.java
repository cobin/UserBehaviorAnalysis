package com.behavior.scheduler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper69.CallTask69Mapper;
import com.behavior.mapper.mapper91.CallTask91Mapper;
/**
 * @author  Cobin
 * @date    2019/7/24 17:01
 * @version 1.0
 * @DisallowConcurrentExecution 不允许并发执行
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallZaiTelallocNotify extends WorkJob {
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
		CallTask91Mapper ct91 = bm.getMapper(CallTask91Mapper.class); 
		CallTask69Mapper ct69 = bm.getMapper(CallTask69Mapper.class);
		userId = 0;
		while(loadTelalloc(ct91, ct69)>=rowCount){
			//循环获取抓取信息只到信息全部抓取完毕
	
		}
	}	
	
	protected int loadTelalloc(CallTask91Mapper ct91,CallTask69Mapper ct69){
		try{
			int date = ct91.getSalesRelTelallocStatMaxDate();
			ct91.deleteSalesRelTelallocStat(date);
			if(qDate!=date){
				userId = 0;
				qDate = date;
			}
			log.debug(TAG+">查询日期："+date+">>"+userId);
			List<Map<Object,Object>> result = ct69.querySalesRelTelallocStat(date,userId, rowCount);
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			int execCount = 0;
			log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-Telalloc同步总数为:"+result.size());	
			for(Map<Object,Object> r:result){
				userId = ((BigDecimal)r.get("USERID")).intValue();
				execCount++;
				changeMapVal(keys,r);
//				for(String key:keys){
//					Object obj = r.get(key);
//					if(obj==null){
//						r.put(key, "NULL");
//					}else if(obj instanceof String){
//						r.put(key, "'"+obj+"'");
//					}else if(obj instanceof Date){
//						r.put(key, "'"+obj+"'");
//					}
//				}
				iData.add(r);
				if(iData.size()>=insertSize){
					qData.add(iData);
					iData = new ArrayList<>();
					if(qData.size()>=3){
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount);						
						ct91.insertSalesRelTelallocStat(qData);
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){
				ct91.insertSalesRelTelallocStat(qData);
			}
			return result.size();
		}catch (Exception e) { 
			log.error(e);
		}
		return rowCount;
	}
	private int userId = 0;
	private int qDate;
	private String[] keys = {"STATDATE","CALLOUTCOST","CALLOUT"};
	private static final int rowCount = 20000;
	public static final int insertSize = 1000;
}
