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
import com.cobin.util.CDate;
/**
 * @author  Cobin
 * @date    2019/7/24 17:15
 * @version 1.0
*/ 
public class WorkCallZhouKeFlashMatureNotify extends WorkJob {
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
    public void execWork(BehaviorMain bm, String qDate){
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask69Mapper ct69 = bm.getMapper(CallTask69Mapper.class);
		CDate cDate = new CDate();
		int nDate=cDate.getYearMonthDate();
		loadFlashMature(ct111,ct69,nDate);
	}	
	
	protected int loadFlashMature(CallTask111Mapper ct111,CallTask69Mapper ct69,int nDate){
		try{
			StringBuffer buff = new StringBuffer();
			log.debug(Thread.currentThread().getName()+">"+TAG+">开始进行Oracle-loadFlashMature<"+nDate+">数据同步...");
			List<Map<Object,Object>> result = ct69.queryEbFlashMaturevalue(nDate);
			if(result.size()>0){
				ct111.deleteEbFlashMaturevalue(nDate);
			}
			List<String> qData = new ArrayList<>();
			int execCount = 0;
			log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-loadFlashMature同步总数为:"+result.size());	
			long _dLong = System.currentTimeMillis();
			for(Map<Object,Object> r:result){
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
				if(buff.length()>0){
					buff.append(",");
				}
				buff.append("(");
				for(String key:vals){					
					buff.append(r.get(key)).append(",");
				}
				buff.deleteCharAt(buff.length()-1);
				buff.append(")");
				if(execCount%insertSize==0){
					qData.add(buff.toString());
					buff.setLength(0);
					if(qData.size()>=1){
						ct111.insertEbFlashMaturevalue(qData);
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount+">>耗时:"+(System.currentTimeMillis() - _dLong));						
						_dLong = System.currentTimeMillis();
						qData.clear();
					}
				}
			}
			if(buff.length()>0){
				qData.add(buff.toString());			 
				ct111.insertEbFlashMaturevalue(qData);
			}
			return result.size();
		}catch (Exception e) { 
			log.error(e);
		}
		return rowCount;
	}
	private String[] keys = {"MODIFYTIME","LOGINDAYS","NEWSARTICLECNT","ROOMDAYS","CALLINCNT"};
	private static final int rowCount = 20000;
	public static final int insertSize = 560;
	private String[] vals = {"PERSONID"
			,"NID"
			,"LOGINDAYS"
			,"NEWSARTICLECNT"
			,"ROOMDAYS"
			,"CALLINCNT"
			,"MODIFYTIME"
			,"NDATE"};
}
