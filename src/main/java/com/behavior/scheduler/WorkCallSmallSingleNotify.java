package com.behavior.scheduler;

import java.util.HashMap;
import java.util.Map;

import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper1111.CallTask1111Mapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper1112.CallTask1112Mapper;
/**
 * @author  Cobin
 * @date    2019/12/17 17:15
 * @version 1.0
*/
public class WorkCallSmallSingleNotify extends WorkJob {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			BehaviorMain bm = (BehaviorMain)arg0.getScheduler().getContext().get("context");
			execWork(bm,null);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void execWork(BehaviorMain bm,String sdate ){		 
		CallTask1112Mapper ct1112 = bm.getMapper(CallTask1112Mapper.class);		
		Map<Object,Object> qParam = new HashMap<>();
		qParam.put("Opid", 2021394);
		qParam.put("sDate", null);		
		log.info(TAG+">开始抓取小单资源数据:"+qParam);		
		//执行原始数据整合
		ct1112.updateSmallSingleCompare(qParam);
		log.info(TAG+">调用抓取小单资源处理结果："+qParam);
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class);
		qParam.remove("sDate");
		//0 全部,1-12执行12个表的备份,1001-1004分成4个块进行备份12个表
		int backCount = 1005;
		for(int i=1001;i<backCount;i++) {
			qParam.put("action", i );
			try {
				ct111.backupFromOraToHis(qParam);
				backCount =(Integer)qParam.get("returnVal");
				log.info(TAG + ">备份OraToHis结果：" + qParam);
			}catch(Exception ex) {
				log.error(ex);
			}
		}
	}
}
