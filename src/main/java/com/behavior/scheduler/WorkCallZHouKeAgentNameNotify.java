package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper69.CallTask69Mapper;
/**
 * @author  Cobin
 * @date    2019/12/17 17:22
 * @version 1.0
*/
public class WorkCallZHouKeAgentNameNotify extends WorkJob {
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
		loadAgentName2Domain(ct111,ct69);
	}
	
	protected int loadAgentName2Domain(CallTask111Mapper ct111,CallTask69Mapper ct69){
		try{
			log.debug(Thread.currentThread().getName()+">"+TAG+">开始进行Oracle-AgentName数据同步...");	
			List<Map<Object,Object>> result = ct69.queryAgentName2Domain();
			long _dLong = System.currentTimeMillis();
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			int execCount = 0;
			log.debug(Thread.currentThread().getName()+">"+TAG+">AgentName数据大小为："+result.size());	
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
						ct111.insertAgentName2Domain(qData);
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,AgentName同步执行编号为:"+execCount+">>耗时>>"+(System.currentTimeMillis() - _dLong));
						_dLong = System.currentTimeMillis();
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
				ct111.insertAgentName2Domain(qData);
			}
			return result.size();
		}catch (Exception e) { 
			log.error(e);
		}
		return rowCount;
	}

	private String[] keys = {"KEYWORD","DOMAIN","ADDTIME","MODIFYTIME","TYPE","ADDPERSON","MATCHLEVEL","MODIFYPERSON"};
	public static final int rowCount = 50000;
	public static final int insertSize = 50000;
}
