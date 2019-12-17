package com.behavior.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper1111.CallTask1111Mapper;
import com.cobin.util.CDate;
import com.cobin.util.Tools;
/**
 * @author  Cobin
 * @date    2019/7/24 16:43
 * @version 1.0
*/
public class WorkCallCourseNotify extends WorkJob {
	private int execStep = 0;
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			BehaviorMain bm = (BehaviorMain)arg0.getScheduler().getContext().get("context");
			execStep = 0;
			for(int i=0;i<3;i++) {
				try {
					execWork(bm,null);					
				}catch(Exception ex) {
					log.error(ex);
				}
				if(execStep==-1){
					break;
				}
				int nWait = (execStep==0?10:5);
				log.debug("调用课程处理失败，等待"+nWait+"分钟后再次执行.");
				Thread.sleep(nWait*60*1000);
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void execWork(BehaviorMain bm,String sdate ){		 
		CallTask1111Mapper ct1111 = bm.getMapper(CallTask1111Mapper.class); 
		//CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class); 
		//判断是否18点之后，如果是则抓取今天的课程，否则昨天的课程
		String sDate =CDate.getInstance().addDate(-1).getShortDate();
		if(CDate.getInstance().getHourOfDay()>17) {
			sDate = CDate.getInstance().getShortDate();
		}
		Map<Object,Object> qParam = new HashMap<>();
		qParam.put("CalcDate", sDate);
		//ct1111.updateUsersCourseVideo(qParam);
		int r = 1;
		log.info(TAG+">开始抓取课程数据:"+qParam);
		if(execStep<1) {
			//执行原始数据整合
			ct1111.updateRoomDataByDate(qParam);
			execStep=1;
			log.info(TAG+">调用定时抓取课程结果："+qParam);
			r = (Integer)qParam.get("return");
		}
		if(r>0){
			qParam.clear();
			qParam.put("Sdate", Tools.getInt(sDate));
			ct1111.updateUsersCourseInsureDate(qParam);
			log.info(TAG+">调用抓取保险课程资源结果："+qParam);
			if(execStep<2) {
				//更新360课程信息
				ct1111.updateUsersCourse360Date(qParam);
				execStep=2;
				log.info(TAG+">调用抓取360课程资源结果："+qParam);
			}
			if(execStep<3) {
				//更新18000课程信息
				ct1111.updateUsersCourse3600Date(qParam);
				execStep=3;
				log.info(TAG+">调用抓取3600课程资源结果："+qParam);
			}
			if(execStep<4) {
				//更新18000课程信息
				ct1111.updateUsersCourse18000Date(qParam);
				execStep=4;
				log.info(TAG+">调用抓取18000课程资源结果："+qParam);
			}
			if(execStep<5) {
				//更新全部课程信息
				ct1111.updateUsersCourse28800Date(qParam);
				execStep=5;
				log.info(TAG+">调用抓取28800课程资源结果："+qParam);
			}
			if(execStep<6) {
				//更新全部课程信息
				ct1111.updateUsersCourseAllDate(qParam);
				execStep=6;
				log.info(TAG+">调用抓取全部课程资源结果："+qParam);
			}
			
			qParam.clear();
			qParam.put("startDate", sDate);
			qParam.put("endDate", sDate);	
			ct1111.updateUserClassRoomStaticInsure(qParam);
			log.info(TAG+">调用抓取保险课程用户统计结果："+qParam);
			if(execStep<10) {
				//整合360
				ct1111.updateUserClassRoomStatic360(qParam);
				execStep=10;
				log.info(TAG+">调用抓取360课程用户统计结果："+qParam);
			}
			if(execStep<11) {
				ct1111.updateUserClassRoomStatic3600(qParam);
				execStep=11;
				log.info(TAG+">调用抓取3600课程用户统计结果："+qParam);
			}
			if(execStep<12) {
				ct1111.updateUserClassRoomStatic18000(qParam);
				execStep=12;
				log.info(TAG+">调用抓取18000课程用户统计结果："+qParam);
			}
			if(execStep<13) {
				ct1111.updateUserClassRoomStatic28800(qParam);
				execStep=13;
				log.info(TAG+">调用抓取28800课程用户统计结果："+qParam);
			}
			if(execStep<14) {
				//整合全部
				ct1111.updateUserClassRoomStaticAll(qParam);
				execStep=14;
				log.info(TAG+">调用抓取全部课程用户统计结果："+qParam);
			}
			qParam.clear();
			qParam.put("nEndDate", sDate);
			ct1111.updateUserClassRoomStatic3600Expand(qParam);
			execStep=-1;
			log.info(TAG+">调用抓取3600课程用户对比结果："+qParam);
		}
//		List<Map<Object,Object>> roomData = ct1111.queryRoomData(sDate);
//		if(!roomData.isEmpty()) {
//			writeRoomData(roomData);
//		}
	}
	
	protected void writeRoomData(List<Map<Object,Object>> roomData) {
		String titles = "PersonId,RoomId,LoginTime,LogOutTime";
		String[] title = titles.split(",");
		FileOutputStream fs;
		try {
			fs = new FileOutputStream(new File("./logs/roomData_"+CDate.formatLongDate()+".csv"));
			PrintStream p = new PrintStream(fs);
			p.println(titles);
			for(Map<Object,Object> map:roomData) {
				String s = "";
				for(String t:title) {
					if(s.length()>0){
						s+=",";
					}
					s +=map.get(t);
				}
				p.println(s);
			}
			p.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
