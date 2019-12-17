package com.behavior.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper69.CallTask69Mapper;
import com.behavior.mapper.mapper76.CallTask76Mapper;
import com.behavior.mapper.mapper91.CallTask91Mapper;
import com.cobin.util.CDate;
import com.cobin.util.Tools;

public class WorkCallNotify extends WorkJob {
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
		String sDate = qDate;
		String sActDate = qDate;
		if(qDate==null){
			sActDate=CDate.formatShortDate(System.currentTimeMillis()-24*60*60*10*1000);
			sDate = CDate.formatShortDate(System.currentTimeMillis()-24*60*60*4*1000);
		}
		CallTask76Mapper ct76 = bm.getMapper(CallTask76Mapper.class); 
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		CallTask91Mapper ct91 = bm.getMapper(CallTask91Mapper.class);
		CallTask69Mapper ct69 = bm.getMapper(CallTask69Mapper.class);
		loadUser(ct111, ct76, ct91, sActDate, sDate);
		loadUserPhone(ct111,ct91,sActDate);
//		获取只有登录为仅一天的非成单用户
//		获取成单用户的首次登录信息
		loadUser(ct111,sActDate,sDate);
		
//		调用定型方法进行统计数据持久化
		callUserIndividualIndex(ct111, sActDate);
		
//		更新用户解锁方式 是用户自解手还是业务员解
		callUserUnLock(ct111,ct69,sActDate);
		
	}
	
	protected void loadUser(CallTask111Mapper ct111,CallTask76Mapper ct76,CallTask91Mapper ct91 ,String sActDate ,String sDate){
		List<List<Map<Object,Object>>> qList = new ArrayList<>();
		List<Map<Object,Object>> cList = new ArrayList<>();
		int nDate = CDate.formatIntDate();
		int execCount = 0;
//		第一步获取新号码
		//ct91.queryNewBieUserClassAll(Tools.getInt(sActDate),nDate); 
		List<Map<Object,Object>> newClassAll = ct111.queryNewPersonKeeper(Tools.getInt(sActDate),nDate);

		log.debug(TAG+">从"+sActDate+"开始新资源数:"+newClassAll.size());
		//ct91.queryNewBieUserClass(Tools.getInt(sDate),nDate); 
		List<Map<Object,Object>> newClass = ct111.queryNewPersonKeeper(Tools.getInt(sDate),nDate);
		if(newClass.size()>0){
			log.debug(TAG+">"+sDate+"有新号记录:"+newClass.size());
		}
		callTask(ct111,newClass,1);//新号	
		newClass.clear();
		newClass = null;
		for(Map<Object,Object> row:newClassAll){			
			cList.add(row);
			execCount++;
			if(cList.size()>=insertSize){
				qList.add(cList);
				cList = new ArrayList<>();
				if(qList.size()>=2){
					//log.debug(row);
					log.debug(Thread.currentThread().getName()+">"+TAG+">"+sDate+"开始执行编号为:"+execCount);
					callTask(ct111, ct76, ct91, qList, sDate);
					qList.clear();
				}
			}
		}
		if(cList.size()>=1){
			qList.add(cList);
		}
		if(qList.size()>0){
			log.debug(Thread.currentThread().getName()+">"+TAG+">"+sDate+"开始执行编号为:"+execCount);
			callTask(ct111, ct76, ct91, qList, sDate);
		}
	}
	//只获取此分号用户的第一次通话所在天的通话记录
	protected void loadUserPhone(CallTask111Mapper ct111,CallTask91Mapper ct91,String sDate){
		int execCount=0;
		int nDate = ct111.queryPhoneMaxDate();
		if(nDate>0){
			sDate = String.valueOf(nDate);
		}
		if(sDate.length()==8){
			sDate+="000000";
		}
		CDate qDate = new CDate(sDate);
		if(nDate>0)
			qDate.addDate(-7);
		
		int classDate = ct111.queryNewClassMaxDate();
		CDate cqDate = new CDate(""+classDate);		
		List<List<Map<Object,Object>>> qList = new ArrayList<>();
		List<Map<Object,Object>> cList = new ArrayList<>();
		//获取分号哪一天没有获取过通话记录的用户
		log.info(TAG+">开始处理<"+qDate.toShortDate()+">>>><"+cqDate.toLongDate()+">");
		while(qDate.getTime()<cqDate.getTime() && qDate.getTime()<System.currentTimeMillis())
		{			
			CDate _qDate = new CDate(sDate);
			if(_qDate.getTime()<qDate.getTime()){
				_qDate.setTime(qDate.getTime());
			}
			//查询时间必须小于当前时间，并且查询时间在7天以内
			List<Map<Object,Object>> phoneUser;
			while(_qDate.getTime()<System.currentTimeMillis() && _qDate.getTime()<qDate.getTime()+(7*24*60*60*1000)){
				phoneUser = ct111.queryPhoneUsers(qDate.getIntDate());
				log.debug(TAG+">处理分号["+qDate.getShortDate()+"]用户通话("+_qDate.getShortDate()+"):"+phoneUser.size());
				if(phoneUser.size()>0){ 
					//获取电话信息
					execCount=0;
					cList.clear();
					qList.clear();
					for(Map<Object,Object> row:phoneUser){
						execCount++;
						cList.add(row);
						if(cList.size()>=insertSize){
							qList.add(cList);
							cList = new ArrayList<>();
							if(qList.size()>=2){
								log.debug(Thread.currentThread().getName()+">"+TAG+">电话用户开始"+_qDate.getShortDate()+"执行编号为:"+execCount);						
								callPhoneUser(ct111,ct91,_qDate.getShortDate(),qList); 
								qList.clear();
							}
						}
					}
					if(cList.size()>=1){
						qList.add(cList); 				
					}
					if(qList.size()>0){
						log.debug(Thread.currentThread().getName()+">"+TAG+">电话用户开始"+_qDate.getShortDate()+"执行编号为:"+execCount);						
						callPhoneUser(ct111,ct91,_qDate.getShortDate(),qList); 
						qList.clear();
					}
					if(qDate.getIntDate()==_qDate.getIntDate()){
						
					}
				}
				_qDate.addDate(1);
			}
			if(qDate.getTime()<System.currentTimeMillis()-24*60*60*1000){
				try{
					//表示第一天通话
					phoneUser = ct111.queryPhoneUsersOneDay(qDate.getIntDate());
					log.debug(TAG+">用户第一天有通话("+qDate.getShortDate()+")个数:"+phoneUser.size());
					execCount=0;
					cList.clear();
					qList.clear();
					for(Map<Object,Object> row:phoneUser){
						execCount++;
						cList.add(row);
						if(cList.size()>=insertSize){
							qList.add(cList);
							cList = new ArrayList<>();
							if(qList.size()>=2){
								log.debug(Thread.currentThread().getName()+">"+TAG+">第一天电话用户执行编号为:"+execCount);						
								callPhoneUser(ct111,ct91,null,qList); 
								qList.clear();
							}
						}
					}
					if(cList.size()>=1){
						qList.add(cList); 				
					}
					if(qList.size()>0){
						log.debug(Thread.currentThread().getName()+">"+TAG+">第一天电话用户执行编号为:"+execCount);						
						callPhoneUser(ct111,ct91,null,qList); 
						qList.clear();
					}
				}catch(Exception ex){
					log.error(ex);
				}
			}
			qDate.addDate(1);
		}
	}
	
	protected void callPhoneUser(CallTask111Mapper ct111,CallTask91Mapper ct91,String sDate,List<List<Map<Object,Object>>> cList){
		List<Map<Object,Object>> result = null;
		if(sDate!=null){
			CDate _date1 = new CDate(sDate);		
			sDate = _date1.getShortDate();
			result = ct91.queryTelPhoneDate(sDate,_date1.addDate(1).getShortDate() ,cList);
		}else{
			result = ct91.queryTelPhoneFirstDate(cList);
			sDate = "第一天";
		}
		if(result.size()>0){
			log.debug(TAG+">"+sDate+"有通话记录:"+result.size());
			callTask(ct111,result,2);//通话日志
		}
	}
	
	protected void loadUser(CallTask111Mapper ct111,String sDate,String loginDate){
		List<List<Map<Object,Object>>> qList = new ArrayList<>();
		List<Map<Object,Object>> cList = new ArrayList<>();
		int execCount = 0;
		List<Map<Object,Object>> loginOneDay  = ct111.queryLoginUser(sDate,loginDate);
		log.debug(TAG+">处理:"+loginOneDay.size());
		cList.clear();
		int logDate = 0;
		execCount=0;
		for(Map<Object,Object> row:loginOneDay){
			execCount++;
			row.put("sourceId", 0);
			int _ldate = ((Integer)row.get("loginDate")).intValue();
			if(logDate==0){
				logDate = _ldate;
			}
			if(logDate==_ldate){
				cList.add(row);	
				if(cList.size()>=insertSize/4){
					qList.add(cList);
					cList = new ArrayList<>();
					if(qList.size()>=1){
						log.debug(Thread.currentThread().getName()+">"+TAG+">满足容量,登录开始"+logDate+"执行编号为:"+execCount);						
						callLoginUser(ct111,logDate,qList);
						callLoginUserStock(ct111, logDate, qList);
						qList.clear();
					}
				}
			}else{
				if(cList.size()>=1){
					qList.add(cList);
					cList = new ArrayList<>();					
				}
				if(qList.size()>0){
					log.debug(Thread.currentThread().getName()+">"+TAG+">登录开始"+logDate+"执行编号为:"+execCount);
					callLoginUser(ct111,logDate,qList);	
					callLoginUserStock(ct111, logDate, qList);
					qList.clear();
				}
				logDate = _ldate;
			}
		}
		if(cList.size()>=1){
			qList.add(cList); 				
		}
		if(qList.size()>0){
			log.debug(Thread.currentThread().getName()+">"+TAG+">登录开始"+logDate+"执行编号为:"+execCount);
			callLoginUser(ct111,logDate,qList);	
			callLoginUserStock(ct111, logDate, qList); 
		}
	}
	
	protected void callTask(CallTask111Mapper ct111,CallTask76Mapper ct76,CallTask91Mapper ct91,List<List<Map<Object,Object>>> cList,String sDate){
//		List<Map<Object,Object>> result = ct76.queryLogin(sDate.substring(0,6), cList,7);
		List<Map<Object,Object>> result = ct76.queryLoginOnline(cList,7);
		if(result.size()>0){
			log.debug(TAG+">"+sDate+"有登录记录:"+result.size());
			callTask(ct111,result,3);//登录日志
		}
		result = ct91.queryNerve(cList,7);
		if(result.size()>0){
			log.debug(TAG+">"+sDate+"有订单记录:"+result.size());
			callTask(ct111,result,4);//订单
		}
	}
	
	public static void callTask(CallTask111Mapper ct111,List<Map<Object,Object>> result,int traceAction){
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
//		int iStep = 0;
//		CDate cDate = new CDate();
//		Set<Integer> persons = new HashSet<>();
		for(Map<Object,Object> r:result){			
			r.put("traceAction", traceAction);
			r.put("hold", String.valueOf(r.get("hold")));
			r.put("times", String.valueOf(r.get("times")));
			r.put("extra_0",String.valueOf(r.get("KeeperId")));
			r.put("extra_1",String.valueOf(r.get("nFlag")));
//			if(traceAction==2){
////				if(!persons.add((Integer)r.get("personId"))){
////					iStep++;
////					cDate.setTime(cDate.getTime()+iStep*1000);
////				}
////				Long l = (Long)r.get("traceTime");
////				r.put("traceTime", l+cDate.getIntTime());
//			}
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
			ct111.insertUserTrace(qData);
		}
	}
	
	public void callLoginUser(CallTask111Mapper ct111,int logDate,List<List<Map<Object,Object>>> qList){
		List<Map<Object,Object>> result = ct111.queryUserFunctionStat(logDate,qList,null);
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
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
		if(qData.size()>0){
			ct111.insertUserFunctionStat(qData);
		}
	}
	
	public void callLoginUserStock(CallTask111Mapper ct111,int logDate,List<List<Map<Object,Object>>> qList){
		List<Map<Object,Object>> result = ct111.queryUserActiveStocks(logDate,qList);
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
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
		if(qData.size()>0){
			ct111.insertUserActiveStocks(qData);
		}
	}
	
	public void callUserIndividualIndex(CallTask111Mapper ct111,String sActDate){
		Map<Object,Object> mPro = new HashMap<>();
		mPro.put("sdate", Tools.getInt(sActDate));
		mPro.put("edate", CDate.formatIntDate());
		ct111.updateUserIndividualIndex(mPro);
		log.debug(mPro);
	}
	public void callUserUnLock(CallTask111Mapper ct111,CallTask69Mapper ct69,String sActDate){
		List<Map<Object,Object>> result = ct111.queryUserLock(Tools.getInt(sActDate),CDate.formatIntDate());
		List<List<Map<Object,Object>>> qData = new ArrayList<>();
		List<Map<Object,Object>> iData = new ArrayList<>();
		List<Map<Object,Object>> resultData = new ArrayList<>();
		log.debug(TAG+">取出锁定用户："+result.size());
		for(Map<Object,Object> r:result){
			iData.add(r);
			if(iData.size()>=insertSize){
				resultData.addAll(ct69.queryKeeperUnLockUser(iData));
				//qData.add(iData);
				//iData = new ArrayList<>();
				iData.clear();
			}
		}
		if(iData.size()>0){
//			qData.add(iData);
			resultData.addAll(ct69.queryKeeperUnLockUser(iData));
		}
//		result = ct91.queryKeeperUnLockUser(qData);
		log.debug(TAG+">更新销售解锁:"+resultData.size());
		qData.clear();
		iData.clear();
		for(Map<Object,Object> r:resultData){
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
			ct111.updateUserUnLock(qData);
		}
	}
	
	public static final int insertSize = 1000;
}
