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
 * @date    2019/12/17 17:23
 * @version 1.0
 * DisallowConcurrentExecution //// 不允许并发执行
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallZHouKeMobileRegNotify extends WorkJob {
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
		log.debug(Thread.currentThread().getName()+">"+TAG+">开始进行Oracle-MobileReg数据同步...");	
		execWork(bm,0);
	}
	public void execWork(BehaviorMain bm,int personId){
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask69Mapper ct69 = bm.getMapper(CallTask69Mapper.class);	
		while( personId>-1) {
			personId = loadCustPresale(ct111,ct69,personId);
		}
	}
	
	protected int loadCustPresale(CallTask111Mapper ct111,CallTask69Mapper ct69,int personId){
		try{
			StringBuffer buffInsert = new StringBuffer();
			StringBuffer buffUpdate = new StringBuffer();
			Map<Integer,Map<Object,Object>> mapCompareResult = null;
			List<Map<Object,Object>> result = ct69.queryEbMobileRegLog(personId, rowCount);
			log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-MobileReg同步区间:<"+personId+">,总数为:"+result.size());
			List<Map<Object,Object>> compareResult = null ;
			if(result.size()>0){
				personId = ((BigDecimal)result.get(result.size()-1).get("PERSONID")).intValue();
				int sPersonId =  ((BigDecimal)result.get(0).get("PERSONID")).intValue();
				compareResult = ct111.queryEbMobileRegLog(sPersonId, personId);
				mapCompareResult = new HashMap<>(compareResult.size());
				for(Map<Object,Object> tmpMv:compareResult) {
					mapCompareResult.put(((Integer)tmpMv.get("PERSONID")).intValue(), tmpMv);
				}
				log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-MobileReg同步区间:<"+personId+">,总数为:"+result.size()+",已有:"+compareResult.size());	
			}else{
				return -1;
			}
			long _dLongInsert = System.currentTimeMillis();
			long _dLongUpdate = System.currentTimeMillis();
			List<String> qDataInsert = new ArrayList<>();
			List<String> qDataUpdate = new ArrayList<>();
			int execCountInsert = 0;
			int execCountUpdate = 0;			
			int execCount = 0;			
			for(Map<Object,Object> r:result){
				execCount++;
				boolean isInsert = true;
				boolean isUpdate = false;
				int _personId = ((BigDecimal)r.get("PERSONID")).intValue();
				Map<Object,Object> _compare = mapCompareResult.get(_personId);
				if(_compare!=null) //for循环遍历改为map查找，提高效率
				{
					isInsert = false;
					int nStatus = (Integer)_compare.get("STATUS");//以前删除过，现在又出现，需要更新
					if(nStatus==2){
						isUpdate = true;
					}else{
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
					}
					//表示此条记录存在，假设删除了，在后续中不需要执行删除操作
					_compare.put("STATUS", 2);
				}				
				
				//表示此纪录没有任何变化不需要处理
				if(!(isInsert || isUpdate)){
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
				
				if(isInsert){
					execCountInsert++;
					if(buffInsert.length()>0){
						buffInsert.append(",");
					}
					buffInsert.append("(");
					for(String key:vals){					
						buffInsert.append(r.get(key)).append(",");
					}
					buffInsert.append("0");
					buffInsert.append(")");
					if(execCountInsert%insertSize==0){
						qDataInsert.add(buffInsert.toString());
						buffInsert.setLength(0);
						if(qDataInsert.size()>=1){
							try {
								ct111.insertEbMobileRegLog(qDataInsert);
							}catch(Exception ex) {
								log.error(ex);
							}
							log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCountInsert+"/"+execCount+">>耗时:"+(System.currentTimeMillis() - _dLongInsert));
							_dLongInsert = System.currentTimeMillis();
							qDataInsert.clear();
						}
					}
				}else{
					execCountUpdate++;
					if(buffUpdate.length()>0){
						buffUpdate.append(",");
					}
					buffUpdate.append("(");
					for(String key:vals){					
						buffUpdate.append(r.get(key)).append(",");
					}
					int len = buffUpdate.length()-1;					
					buffUpdate.deleteCharAt(len);
					buffUpdate.append(")");
					if(execCountUpdate%insertSize==0){
						qDataUpdate.add(buffUpdate.toString());
						buffUpdate.setLength(0);
						if(qDataUpdate.size()>=1){
							ct111.updateEbMobileRegLog(qDataUpdate);
							log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步更新数据开始执行编号为:"+execCountUpdate+"/"+execCount+">>耗时:"+(System.currentTimeMillis() - _dLongUpdate));
							_dLongUpdate = System.currentTimeMillis();
							qDataUpdate.clear();
						}
					}
				}
			}
			if(buffInsert.length()>0){
				qDataInsert.add(buffInsert.toString());	
				try {
					ct111.insertEbMobileRegLog(qDataInsert);
				}catch(Exception ex) {
					log.error(ex);
				}
			}
			if(buffUpdate.length()>0){
				qDataUpdate.add(buffUpdate.toString());			
				ct111.updateEbMobileRegLog(qDataUpdate);
			}
			if(compareResult.size()>0){
				buffUpdate.setLength(0);
				execCountUpdate = 0;
				qDataUpdate.clear();
				_dLongUpdate = System.currentTimeMillis();
				for(Map<Object,Object> r:compareResult){
					int status = ((Integer)r.get("STATUS")).intValue();
					//表示此条记录没有被删除过，则进行操作
					if(status!=2) {
						execCountUpdate++;
						if(buffUpdate.length()>0){
							buffUpdate.append(",");
						}
						buffUpdate.append(r.get("PERSONID"));					
						if(execCountUpdate%insertSize==0){
							qDataUpdate.add(buffUpdate.toString());
							buffUpdate.setLength(0);
							if(qDataUpdate.size()>=1){
								ct111.deleteEbMobileRegLog(qDataUpdate);
								log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步删除数据开始执行编号为:"+execCountUpdate+"/"+compareResult.size()+">>耗时:"+(System.currentTimeMillis() - _dLongUpdate));
								_dLongUpdate = System.currentTimeMillis();
								qDataUpdate.clear();
							}
						}
					}
				}
				if(buffUpdate.length()>0){
					qDataUpdate.add(buffUpdate.toString());			
					ct111.deleteEbMobileRegLog(qDataUpdate);
				}
			}
			log.debug(Thread.currentThread().getName()+">"+TAG+">处理完毕:"+personId+"/"+compareResult.size()+">>耗时:"+(System.currentTimeMillis() - _dLongUpdate));			
		
			return personId;
		}catch (Exception e) { 
			e.printStackTrace();
			log.error(e);
		}
		return personId;
	}

	private String[] vals = {"PERSONID", 
	           "NERVEID"  ,
	           "KEEPERID" ,
	           "CUSTESTACT1" ,
	           "CREATETIME",
	           "FINISHTIME",
	           "FIRSTALLOCTIME",
	           "A0",
	           "BPLUS",
	           "B0",
	           "B1",
	           "B2",
	           "B3",
	           "PREALLOCGID" , 
	           "PRESALETYPE",
	           "TYPE",
	           "BOUGHT",
	           "KEEPERGID",
	           "FROMWHERE",
	           "DOMAIN"};
	private String[] keys = {"CREATETIME","CUSTESTACT1","FINISHTIME","FIRSTALLOCTIME","FROMWHERE","DOMAIN"};
	public static final int rowCount = 50000;
	public static final int insertSize = 550;	
}
