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
/**
 * @author  Cobin
 * @date    2019/7/24 17:13
 * @version 1.0
 * @DisallowConcurrentExecution  不允许并发执行
*/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallZHouKeQuestionNotify extends WorkJob {
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
		int xCount=0;
		curQuery1 = null;
		curQuery2 = null;
		while(true){
			loadQuestion(ct111,ct69);
			loadQuestionLog(ct111,ct69);
			if(curQuery1.getTime()>System.currentTimeMillis()){
				break;
			}
			if(curQuery2.getTime()>System.currentTimeMillis()){
				break;
			}
			xCount++;
			if(xCount>50){
				break;
			}
		}
	}
	
	protected int loadQuestion(CallTask111Mapper ct111,CallTask69Mapper ct69){
		if(curQuery1==null){
			curQuery1 = ct111.getEbPersonQuestionMaxDate();
			curQuery1.setTime(curQuery1.getTime()-1000);
		}
		log.debug("Oracle-Question>>查询开始时间为："+curQuery1);
		Date date = new Date(curQuery1.getTime()+loadSubTime);
		List<Map<Object,Object>> result = ct69.queryEbPersonQuestion(curQuery1, date);
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		int execCount = 0;
		log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-Question同步总数为:"+result.size());	
		for(Map<Object,Object> r:result){
			execCount++;
			changeMapVal(keysQuestion,r);
//			for(String key:keysQuestion){
//				Object obj = r.get(key);
//				if(obj==null){
//					r.put(key, "NULL");
//				}else if(obj instanceof String){
//					r.put(key, "'"+obj+"'");
//				}else if(obj instanceof Date){
//					r.put(key, "'"+obj+"'");
//				}
//			}
			iData.add(r);
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
				if(qData.size()>=3){
					log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount);						
					ct111.insertEbPersonQuestion(qData);
					qData.clear();
				}
			}
		}
		if(iData.size()>0){
			qData.add(iData);
		}
		if(qData.size()>0){
			ct111.insertEbPersonQuestion(qData);
		}
		curQuery1.setTime(date.getTime()-1000);
		return result.size();
	}
	
	protected int loadQuestionLog(CallTask111Mapper ct111,CallTask69Mapper ct69){
		if(curQuery2==null){
			curQuery2 = ct111.getEbPersonQuestionLogMaxDate();
			curQuery2.setTime(curQuery2.getTime()-1000);
		}
		log.debug("Oracle-QuestionLog>>查询开始时间为："+curQuery2);
		Date date = new Date(curQuery2.getTime()+loadSubTime);
		List<Map<Object,Object>> result = ct69.queryEbPersonQuestionLog(curQuery2, date);
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		int execCount = 0;
		log.debug(Thread.currentThread().getName()+">"+TAG+">Oracle-QuestionLog同步总数为:"+result.size());	
		for(Map<Object,Object> r:result){
			execCount++;
			changeMapVal(keysQuestionLog,r);
//			for(String key:keysQuestionLog){
//				Object obj = r.get(key);
//				if(obj==null){
//					r.put(key, "NULL");
//				}else if(obj instanceof String){
//					r.put(key, "'"+obj+"'");
//				}else if(obj instanceof Date){
//					r.put(key, "'"+obj+"'");
//				}
//			}
			iData.add(r);
			if(iData.size()>=insertSize){
				qData.add(iData);
				iData = new ArrayList<>();
				if(qData.size()>=3){
					log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,同步数据开始执行编号为:"+execCount);						
					ct111.insertEbPersonQuestionLog(qData);
					qData.clear();
				}
			}
		}
		if(iData.size()>0){
			qData.add(iData);
		}
		if(qData.size()>0){
			ct111.insertEbPersonQuestionLog(qData);
		}
		curQuery2.setTime(date.getTime()-1000);
		return result.size();
	}
	
	public static final int insertSize = 1000;
	private static final int loadSubTime = 24*3600000;
	private Date curQuery1 = null;
	private Date curQuery2 = null;
	private static String[] keysQuestion = {"CREATETIME","MODIFYTIME","LASTOPNAME"};
	private static String[] keysQuestionLog = {"OPNAME","LOGTIME","CLASSID"};
}
