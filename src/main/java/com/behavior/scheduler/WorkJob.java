package com.behavior.scheduler;

import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper69.CallTask69Mapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;

import com.behavior.BehaviorMain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class WorkJob implements Job {
	protected String TAG = getClass().getSimpleName();
	protected Log log = LogFactory.getLog(getClass());
	public abstract void execWork(BehaviorMain bm,String qDate);

	public int exec(List<Map<Object,Object>> result ,WorkCallExcute excute){
		try{
			String tag = excute.getTag();
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			int execCount = 0;
			int insertSize = excute.getInserSize();
			log.debug(Thread.currentThread().getName()+">"+tag+">同步总数为:"+result.size());
			for(Map<Object,Object> r:result){
				execCount++;
				for(String key:excute.getKeys()){
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
						log.debug(Thread.currentThread().getName()+">"+tag+">满足容量,同步数据为:"+execCount);
						excute.execute(qData);
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){
				excute.execute(qData);
			}
			return result.size();
		}catch (Exception e) {
			log.error(e);
		}
		return 0;
	}
}
