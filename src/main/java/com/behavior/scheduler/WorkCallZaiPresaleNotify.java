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
 * @date    2019/7/24 17:00
 * @version 1.0
 * @DisallowConcurrentExecution 不允许并发执行
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallZaiPresaleNotify extends WorkJob {
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
//		CDate c = new CDate("2019-07-05 21:00:00");
//		//c.addMonth(-1);
//		CDate c1 = new CDate("2019-07-07 00:00:00");
		//while(c.getTime()<c1.getTime()) {//System.currentTimeMillis()
//			loadPresale(ct111,ct69,c.toDate(),c1.toDate()); 
		//}
		loadPresale(ct111, ct69, null, null);

		//处理销售员下的客户的新增B及B1情况
		ct111.updateEbPersonKeeperBandB1();
		log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-Presale-B-B1");	
	}	
	
	protected int loadPresale(CallTask111Mapper ct111,CallTask69Mapper ct69,Date sDate,Date eDate){
		try{
			log.debug(Thread.currentThread().getName()+">"+TAG+">开始进行Oracle-Presale数据同步>>>开始日期:"+sDate+"..."+eDate);
			List<Map<Object,Object>> result = ct69.queryEbPersonPresaleLog(sDate,eDate);
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			int execCount = 0;
			log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-Presale同步总数为:"+result.size());	
			for(Map<Object,Object> r:result){
				execCount++;
//				if("206561398".equals(r.get("NERVEID").toString())) {
//					log.debug(Thread.currentThread().getName()+">"+TAG+">"+r);
//				}
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
						ct111.insertEbPersonPresaleLog(qData);
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){
				ct111.insertEbPersonPresaleLog(qData);
			}
			return result.size();
		}catch (Exception e) { 
			log.error(e);
		}
		return rowCount;
	}
	private String[] keys = {"LOGTIME","CUSTESTIMATE"};
	private static final int rowCount = 20000;
	public static final int insertSize = 1000;
}
