package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper76.CallTask76Mapper;
import com.cobin.util.CDate;

public class WorkCallKaiUsersLoginNotify extends WorkJob {
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
		CallTask76Mapper ct76 = bm.getMapper(CallTask76Mapper.class);
		CDate cDate = new CDate();
		
		int hours = 0;
		int curDate = 0;
		if(qDate==null) {
			cDate.addDate(-1);
		}else {
			cDate.setDate(qDate);
		}
		curDate = cDate.getIntDate();
		if(curDate<20000000) {
			log.debug("无效的抓取日期");
			return ;
		}
		
		String nMonth = String.valueOf(curDate/100);
		
		if(ct76.checkCompassTable(nMonth)==0) {
			log.debug("不存在此月份<"+nMonth+">数据");
			return ;
		}
		
		int delRowCount = ct111.deleteUsersLogin(curDate);
		log.debug("删除当前抓取日期的用户信息个数:"+delRowCount);
		delRowCount = ct111.deleteUsersLogin(cDate.addDate(-30).getIntDate());
		log.debug("删除30天之前的那天用户信息个数:"+delRowCount);
		
		qDate = String.valueOf(curDate);
		while(hours<24) {
			userId = 0;			
			while(loadUsersLogin(ct111, ct76,nMonth,qDate,hours)>=rowCount){
				//循环获取抓取信息只到信息全部抓取完毕
			}
			hours++;
		}
	}	
	
	protected int loadUsersLogin(CallTask111Mapper ct111,CallTask76Mapper ct76,String months,String curDate,int hours){
		try{			
			log.debug(TAG+">查询日期："+curDate+">>"+hours+">>"+userId);			
			List<Map<Object,Object>> result = ct76.queryCompassUsersLogin(months,curDate,hours,userId, rowCount);
			List<List<Map<Object,Object>>> qData = new ArrayList<>();
			List<Map<Object,Object>> iData = new ArrayList<>();
			int execCount = 0;
			log.debug(Thread.currentThread().getName()+">"+TAG+">76Server-UsersLogin同步总数为:"+result.size());	
			for(Map<Object,Object> r:result){
				userId = ((Integer)r.get("PersonId")).intValue();
				execCount++;				
				iData.add(r);
				if(iData.size()>=insertSize){
					qData.add(iData);
					iData = new ArrayList<>();
					if(qData.size()>=3){
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount);						
						ct111.insertUsersLogin(qData);
						qData.clear();
					}
				}
			}
			if(iData.size()>0){
				qData.add(iData);
			}
			if(qData.size()>0){
				ct111.insertUsersLogin(qData);
			}
			return result.size();
		}catch (Exception e) { 
			log.error(e);
		}
		return rowCount;
	}
	private int userId = 0;
	public static final int rowCount = 20000;
	public static final int insertSize = 1000;
}
