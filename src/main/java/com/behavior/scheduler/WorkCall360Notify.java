package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper1110.CallTask1110Mapper;
import com.cobin.util.CDate;
import com.cobin.util.Tools;

/**
 * @author  Cobin
 * @date    2019/7/24 16:32
 * @version 1.0
*/
public class WorkCall360Notify extends WorkJob {

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
		int sActDate = Tools.getInt(qDate) ;
		if(qDate==null){
			sActDate=20160824;
		}
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask1110Mapper ct1110 = bm.getMapper(CallTask1110Mapper.class);
		int sDate = ct111.queryUserFunctionStat360Date();
		if(sDate<20160822){
			sDate = 20160822;
		}
		loadUser360(ct111, ct1110, sActDate,sDate,0);
//		loadUser360Sample(ct111,sActDate,sDate);
//		loadUser360(ct111, ct91, sActDate,sDate,5);
	}
	
	private void loadUser360(CallTask111Mapper ct111, CallTask1110Mapper ct1110, int sActDate, int sDate, int servLevelId){
		List<List<Map<Object,Object>>> qList = new ArrayList<>();
		List<Map<Object,Object>> cList = new ArrayList<>();
		int execCount = 0;
//		第一步获取新号码
		int nLastDate = CDate.getInstance().getIntDate();
//		if(nLastDate<sDate){//不满足抓取最小时间
//			log.debug(TAG+">没有满足当前需要的日"+sDate+",终止抓取！");
//			return;
//		}
//		if(nLastDate<sActDate){
//			sActDate = nLastDate;
//		}
//		List<Map<Object,Object>> newClassAll = ct91.queryUpgraderClass360(servLevelId,sActDate); 
		
		//int actId =-1;
		
		List<Map<Object,Object>> pDate = ct1110.queryActPeriod();		
		if(pDate.isEmpty() || pDate.get(0)==null){
			log.debug(TAG+">找不到3600上拽活动信息,终止抓取！");
			return;
		}
		Map<Object,Object> p = pDate.get(0);
		int actId = (Integer) p.get("actId");
		int startDate=(Integer) p.get("startDate");
		int endDate =(Integer)p.get("endDate");
		if(nLastDate<startDate || nLastDate>endDate){
			log.debug(TAG+">已经超出活动范围内了<"+actId+"-"+startDate+"-"+endDate+">,终止抓取！");
			return ;
		}else{
			log.debug(TAG+">活动信息：{"+actId+","+startDate+","+endDate+"}");
		}
		
		if(sDate<startDate){
			sDate = startDate;
		}
		
		List<Map<Object,Object>> newClassAll = ct1110.queryFunctionUsers(actId); 
		
		if(newClassAll.isEmpty()){
			log.debug(TAG+">此活动下没有需要抓取的用户<"+actId+"-"+startDate+"-"+endDate+">,终止抓取！");
			return ;
		}
		
		log.debug(TAG+">从"+sActDate+"开始资源数:"+newClassAll.size());
		
		CDate cDate = new CDate(sDate+"000000");
		//cDate.addDate(1);		
		while(cDate.getIntDate()<nLastDate){			
			for(Map<Object,Object> row:newClassAll){			
				cList.add(row);
				execCount++;
				if(cList.size()>=insertSize){
					qList.add(cList);
					cList = new ArrayList<>();
					if(qList.size()>=4){
						log.debug(Thread.currentThread().getName()+">"+TAG+">"+cDate.getShortDate()+"开始执行编号为:"+execCount);
						callUserFunctionStat360(ct111, cDate.getIntDate(), qList);
						qList.clear();
					}
				}
			}
			if(cList.size()>=1){
				qList.add(cList);
			}
			if(qList.size()>0){
				log.debug(Thread.currentThread().getName()+">"+TAG+">"+cDate.getShortDate()+"开始执行编号为:"+execCount);
				callUserFunctionStat360(ct111,cDate.getIntDate(),  qList);
			}
			cList.clear();
			qList.clear();
			execCount = 0;
			cDate.addDate(1);
		}
		log.info(TAG+">执行完毕！");
	}
	
	
/*
	protected void loadUser360Sample(CallTask111Mapper ct111 ,int sActDate,int sDate){
		List<List<Map<Object,Object>>> qList = new ArrayList<>();
		List<Map<Object,Object>> cList = new ArrayList<>();
		int execCount = 0;
//		第一步获取新号码
		int nLastDate = CDate.getInstance().getIntDate();
		//不满足抓取最小时间
		if(nLastDate<sDate){
			log.debug(TAG+">没有满足当前需要的日"+sDate+",终止抓取！");
			return;
		}
		if(nLastDate<sActDate){
			sActDate = nLastDate;
		}
		List<Map<Object,Object>> newClassAll = ct111.querySample360Users();
		log.debug(TAG+">从"+sActDate+"开始新资源数:"+newClassAll.size());
		CDate cDate = new CDate(sDate+"000000");
		//cDate.addDate(1);
		while(cDate.getIntDate()<nLastDate){
			for(Map<Object,Object> row:newClassAll){
				cList.add(row);
				execCount++;
				if(cList.size()>=insertSize){
					qList.add(cList);
					cList = new ArrayList<>();
					if(qList.size()>=4){
						log.debug(Thread.currentThread().getName()+">"+TAG+">"+cDate.getShortDate()+"开始执行编号为:"+execCount);
						callUserFunctionStat360(ct111, cDate.getIntDate(), qList);
						qList.clear();
					}
				}
			}
			if(cList.size()>=1){
				qList.add(cList);
			}
			if(qList.size()>0){
				log.debug(Thread.currentThread().getName()+">"+TAG+">"+cDate.getShortDate()+"开始执行编号为:"+execCount);
				callUserFunctionStat360(ct111,cDate.getIntDate(),  qList);
			}
			cList.clear();
			qList.clear();
			execCount = 0;
			cDate.addDate(1);
		}
		log.info(TAG+">执行完毕！");
	}
*/


	private void callUserFunctionStat360(CallTask111Mapper ct111, int logDate, List<List<Map<Object, Object>>> qList){
		List<Map<Object,Object>> result = ct111.queryUserFunctionStat360(logDate,qList);
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		for(Map<Object,Object> r:result){
			r.put("sDate", logDate);
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
			ct111.insertUserFunctionStat360(qData);
		}
	}
	
	public static final int insertSize = 1000;
}
