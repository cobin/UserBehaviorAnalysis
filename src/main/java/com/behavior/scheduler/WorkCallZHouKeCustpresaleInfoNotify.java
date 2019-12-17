package com.behavior.scheduler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
 * @date    2019/12/17 17:22
 * @version 1.0
 * DisallowConcurrentExecution //// 不允许并发执行
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallZHouKeCustpresaleInfoNotify extends WorkJob {
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
		log.debug(Thread.currentThread().getName()+">"+TAG+">开始进行Oracle-CustpresaleInfo数据同步...");	
		execWork(bm,0);
	}
	public void execWork(BehaviorMain bm,int personId){
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask69Mapper ct69 = bm.getMapper(CallTask69Mapper.class);	
		totalCount = 0;
		long _dLongInsert = System.currentTimeMillis();
		while( personId>-1) {
			personId = loadCustPresaleInfo(ct111,ct69,personId);
		}
		log.debug(Thread.currentThread().getName()+">"+TAG+">执行结束总数:"+updateTotalCount+"/"+totalCount+">>总耗时:"+(System.currentTimeMillis() - _dLongInsert));
		
	}
	
	protected int loadCustPresaleInfo(CallTask111Mapper ct111,CallTask69Mapper ct69,int personId){
		try{
			Map<Integer,Map<Object,Object>> mapCompareResult = null;
			List<Map<Object,Object>> result = ct69.queryEbCustpresaleInfo(personId, rowCount);
			log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-CustpresaleInfo同步区间:<"+personId+">,总数为:"+result.size());
			List<Map<Object,Object>> compareResult = null ;
			if(result.size()>0){
				personId = ((BigDecimal)result.get(result.size()-1).get("PERSONID")).intValue();
				int sPersonId =  ((BigDecimal)result.get(0).get("PERSONID")).intValue();
				compareResult = ct111.queryEbCustpresaleInfo(sPersonId, personId);
				mapCompareResult = new HashMap<>(compareResult.size());
				for(Map<Object,Object> tmpMv:compareResult) {
					mapCompareResult.put(((Integer)tmpMv.get("PERSONID")).intValue(), tmpMv);
				}
				log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-CustpresaleInfo同步区间:<"+personId+">,总数为:"+result.size()+",已有:"+compareResult.size());	
			}else{
				return -1;
			}
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> dataUpdate = new ArrayList<>();	
			long _dLongInsert = System.currentTimeMillis();
			int execCount = 0;		
			int updateCount = 0;
			for(Map<Object,Object> r:result){
				execCount++;
				totalCount++;
				int _personId = ((BigDecimal)r.get("PERSONID")).intValue();
				Map<Object,Object> _compare = mapCompareResult.remove(_personId);
				boolean isUpdate = false;
				if(_compare!=null){					
					for(String key:vals){
						Object obj = r.get(key);
						Object objc = _compare.get(key);
						if(obj==null || objc==null){
							if(!(obj==null && objc==null)){
								isUpdate = true;
								break;
							}
						}else{
							if(obj instanceof Date){
								if(((Date)_compare.get(key)).getTime()!=((Date)obj).getTime()){
									isUpdate = true;
									break;
								}
							}else if(obj instanceof BigDecimal){
								if(((Integer)_compare.get(key)).intValue()!=((BigDecimal)obj).intValue()){
									isUpdate = true;
									break;
								}
							}else{								
								if(!((String)objc).equals(obj.toString())){
									isUpdate = true;
									break;
								}
							}	
						}
					}					
				}else {
					isUpdate = true;
				}
				
				//表示此纪录没有任何变化不需要处理
				if(!isUpdate){
					continue;
				}
				
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
				updateTotalCount++;
				updateCount++;
				dataUpdate.add(r);	
				
				if(updateCount%insertSize==0){
					qData.add(dataUpdate);	
					dataUpdate = new ArrayList<>();
					if(qData.size()>=5){
						try {
							ct111.insertEbCustpresaleInfo(qData);
						}catch(Exception ex) {
							log.error(ex);
						}
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+updateCount+"/"+execCount+">>耗时:"+(System.currentTimeMillis() - _dLongInsert));
						qData.clear();
						_dLongInsert = System.currentTimeMillis();
					}
				}
				
			}
			if(dataUpdate.size()>0){
				qData.add(dataUpdate);	
			}
			if(qData.size()>0) {
				try {
					ct111.insertEbCustpresaleInfo(qData);
				}catch(Exception ex) {
					log.error(ex);
				}
			}			
			log.debug(Thread.currentThread().getName()+">"+TAG+">处理完毕:"+personId+"/"+compareResult.size()+"("+mapCompareResult.size()+")/"+totalCount+">>耗时:"+(System.currentTimeMillis() - _dLongInsert));			
			
			qData.clear();
			dataUpdate.clear();
			updateCount = 0;
			for(Map.Entry<Integer, Map<Object,Object>> me:mapCompareResult.entrySet()) {
				Map<Object,Object> r = me.getValue();
				dataUpdate.add(r);
				updateCount++;
				if(updateCount%insertSize==0){
					qData.add(dataUpdate);	
					dataUpdate = new ArrayList<>();
					if(qData.size()>=5){
						try {
							ct111.updateEbCustpresaleInfo(qData);
						}catch(Exception ex) {
							log.error(ex);
						}
						qData.clear();
						_dLongInsert = System.currentTimeMillis();
					}
				}
			}
			if(dataUpdate.size()>0){
				qData.add(dataUpdate);	
			}
			if(qData.size()>0) {
				try {
					ct111.updateEbCustpresaleInfo(qData);
				}catch(Exception ex) {
					log.error(ex);
				}
			}
			return personId;
		}catch (Exception e) { 
			e.printStackTrace();
			log.error(e);
		}
		return personId;
	}

	private String[] vals = {"PERSONID",
	           "A0",
	           "BPLUS",
	           "B0",
	           "B1",
	           "B2",
	           "B3"};
	private String[] keys = {};
	private int totalCount = 0,updateTotalCount = 0;
	public static final int rowCount = 150000;
	public static final int insertSize = 1000;	
}
