package com.behavior.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;

import com.behavior.BehaviorMain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * @author  Cobin
 * @date    2019/7/24 16:49
 * @version 1.0
 */
public abstract class WorkJob implements Job {
	protected String TAG = getClass().getSimpleName();
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * @param bm
	 * @param qDate
	 */
	public abstract void execWork(BehaviorMain bm,String qDate);

	public void changeMapVal(String[] keys, Map<Object,Object> r){
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
	}

	public void intoData(List<List<Map<Object,Object>>> qData, List<Map<Object,Object>> result,int insertSize){
		List<Map<Object,Object>> iData = new ArrayList<>();
		for(Map<Object,Object> r:result){
			iData.add(r);
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
			}
		}
		if(iData.size()>0){
			qData.add(iData);
		}
	}
}
