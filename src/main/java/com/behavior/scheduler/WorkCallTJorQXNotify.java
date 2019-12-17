package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper91.CallTask91Mapper;
import com.cobin.util.CDate;

/**
 * @author  Cobin
 * @date    2019/7/24 16:58
 * @version 1.0
 */
public class WorkCallTJorQXNotify extends WorkJob {
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
		String sDate = qDate; 
		if(qDate==null){			 
			sDate = CDate.formatShortDate(System.currentTimeMillis()-24*60*60*4*1000);
		}
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask91Mapper ct91 = bm.getMapper(CallTask91Mapper.class);
		CDate date = new CDate(sDate);
		while(date.getTime()<System.currentTimeMillis()){
			//加载用户成单之前的查看股票情况
			//推荐
			loadUser(ct111,ct91,"7,11",date.getIntDate(),1);
			//清洗
			loadUser(ct111,ct91,"5,20",date.getIntDate(),2);
			date.addDate(1);
		}
	}
	


	
	protected void loadUser(CallTask111Mapper ct111,CallTask91Mapper ct91,String logType,int sDate,int nType){
		List<List<Map<Object,Object>>> qList = new ArrayList<>();
		List<Map<Object,Object>> cList = new ArrayList<>();
		int execCount = 0;
		List<Map<Object,Object>> loginOneDay  = ct91.queryPersonReg(logType,sDate);
		log.debug(TAG+">处理:"+loginOneDay.size());
		CDate ldate = new CDate(""+sDate);
		Set<Integer> person = new HashSet<>();
		int index = 0;
		while(index<7 && !loginOneDay.isEmpty()){
			cList.clear();
			int logDate = ldate.getIntDate();
			execCount=0;
			person.clear();
//			for(int i=0;i<loginOneDay.size();i++){
//				Map<Object,Object> mx = loginOneDay.get(i);
//				Integer _sd = (Integer)mx.get("createDate");
//				if(_sd!=null && _sd<=logDate){ 
//					loginOneDay.remove(i);
//					i--;
//				}
//			}
//			if(loginOneDay.isEmpty()){
//				break;
//			}
			log.debug(TAG+">"+logDate+">>处理:"+loginOneDay.size());
			for(Map<Object,Object> row:loginOneDay){
				execCount++;
				row.put("sdate", sDate);
				row.put("userType",nType);
				cList.add(row);	
				if(cList.size()>=insertSize){
					qList.add(cList);
					cList = new ArrayList<>();
					if(qList.size()>=1){
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,股票开始"+logDate+"执行编号为:"+execCount);						
						callLoginUserStock(ct111, logDate, qList,nType,person);
						qList.clear();
					}
				}			
			}
			if(cList.size()>=1){
				qList.add(cList); 				
			}
			if(qList.size()>0){
				log.debug(Thread.currentThread().getName()+">"+TAG+">股票开始"+logDate+"执行编号为:"+execCount);			
				callLoginUserStock(ct111, logDate, qList,nType,person); 
				qList.clear();
			}
			log.debug(TAG+">有用户数据："+person.size());
			for(Integer p:person){
				Map<Object,Object> del = null;
				for(Map<Object,Object> row:loginOneDay){
					if((int)row.get("personId")==p){
						del = row;
						break;
					}
				}
//				System.out.println(p+">>>"+del+">>"+loginOneDay.size());
				if(del!=null){					
					loginOneDay.remove(del);
				}
			}
			ldate.addDate(1);
			index++;
		}
	}
	
	
	public void callLoginUserStock(CallTask111Mapper ct111,int logDate,List<List<Map<Object,Object>>> qList,int nType,Set<Integer> person){
		List<Map<Object,Object>> result = ct111.queryUserActiveStocks(logDate,qList);
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		for(Map<Object,Object> r:result){
			person.add((int)r.get("personid"));
			iData.add(r);
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
			}
		}
		if(iData.size()>0){
			qData.add(iData);
		}
		if(qData.size()>0){
			if(nType==1) {
				ct111.insertUserActiveStocksTJ(qData);
			}else {
				ct111.insertUserActiveStocksQX(qData);
			}
		}
	}
	
	public static final int insertSize = 1000;
}
