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
/**
 * @author  Cobin
 * @date    2019/12/17 17:21
 * @version 1.0
 * DisallowConcurrentExecution //// 不允许并发执行
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallZHouBehavLog8Notify extends WorkJob {
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
		String loadDate = bm.getConfig("behav8.date");
		if(loadDate==null || loadDate.length()<8) {
			curQuery = null;//new CDate("2018-11-07 00:00:01").toDate();
		}else {
			curQuery =new CDate(loadDate).toDate();
			log.debug("定点指定查询日期：" + curQuery);
		}
		
		String param3 = bm.getConfig("bahav8.param3");
		String s3 = bm.getConfig("behav8.s3");
		log.debug("查询条件：p3="+ (param3==null?0: param3.length())+",s3="+(s3==null?0:s3.length()));
		while(loadBehavLog8(ct111, ct69,param3,s3)>0){
			//循环获取抓取信息只到信息全部抓取完毕
			if(curQuery.getTime()>System.currentTimeMillis()){
				break;
			}
		}
	}	
	
	protected int loadBehavLog8(CallTask111Mapper ct111,CallTask69Mapper ct69,String param3,String s3){
		try{
			if(curQuery==null){
				curQuery = ct111.getBehavLog8MaxDate();
				curQuery.setTime(curQuery.getTime()-1000);
			}
			log.debug(TAG+">(8)查询日期："+curQuery);
			Date date = new Date(curQuery.getTime()+loadSubTime);
			List<Map<Object,Object>> result = ct69.queryBehavLog8(curQuery, date,param3,s3);
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			int execCount = 0;
			log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-Behaviour8同步总数为:"+result.size());	
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
					if(qData.size()>=3){
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount);						
						ct111.insertBehavLog8(qData);
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){
				ct111.insertBehavLog8(qData);
			}
			curQuery.setTime(date.getTime()-1000);
			return result.size()+1;
		}catch (Exception e) { 
			log.error(e);
		}
		return 0;
	}
	
	private String[] keys = {"USERCARD","MEMO","PARAM1","PARAM2","PARAM3","PARAM4","PARAM5","PARAM6","SERVICEID","S3"};
	public static final int loadSubTime = 12*3600000;
	private Date curQuery = null;
	public static final int insertSize = 1000;
}
