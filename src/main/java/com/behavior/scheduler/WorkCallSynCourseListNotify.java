package com.behavior.scheduler;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper1111.CallTask1111Mapper;
import com.behavior.mapper.mapper131.CallTask131Mapper;
import com.cobin.util.CDate;
import com.cobin.util.Tools;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  Cobin
 * @date    2020/8/8 9:48
 * @version 1.0
*/
public class WorkCallSynCourseListNotify extends WorkJob {
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
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class);
		CallTask131Mapper ct131 = bm.getMapper(CallTask131Mapper.class);
		String sDate =CDate.getInstance().addDate(-10).toShortDate();
		List<Map<Object,Object>> result = ct131.queryCmsCourse(sDate);
		WokCallExcuteAll wokCallExcuteAll = new WokCallExcuteAll(ct111);
		exec(result, wokCallExcuteAll.workFlag(0));
		result = ct131.queryCourseProperty(sDate);
		exec(result, wokCallExcuteAll.workFlag(1));
		result = ct131.queryVotes(ct111.getVotesMaxId());
		exec(result,wokCallExcuteAll.workFlag(2));
		result = ct131.queryVoteItems(ct111.getVoteItemsMaxId());
		exec(result,wokCallExcuteAll.workFlag(3));
		result = ct131.queryWebNews(ct111.getWebNewsMaxDate());
		exec(result,wokCallExcuteAll.workFlag(4));
		result = ct131.queryWebNewsClasses(ct111.getWebNewsClassesMaxId());
		exec(result,wokCallExcuteAll.workFlag(5));
		result = ct131.queryWebNewsComment(ct111.getWebNewsCommentMaxId());
		exec(result,wokCallExcuteAll.workFlag(6));
		result = ct131.queryCmsUserStock(ct111.getCmsUserStokMaxTime());
		exec(result,wokCallExcuteAll.workFlag(7));
//		result = ct131.queryVoteRecord2(ct111.getVoteRecordMaxId());
//		exec(result,wokCallExcuteAll.workFlag(8));
	}

	private class WokCallExcuteAll implements WorkCallExcute{
		private CallTask111Mapper ct111;
		private int workFlag ;
		public WokCallExcuteAll(CallTask111Mapper ct111){
			this.ct111 = ct111;
		}

		public WokCallExcuteAll workFlag(int workFlag) {
			this.workFlag = workFlag;
			return this;
		}

		@Override
		public void execute(List<List<Map<Object, Object>>> qData) {
			if(workFlag==0) {
				ct111.updateCmsCourse(qData);
			}else if(workFlag==1){
				ct111.updateCourseProperty(qData);
			}else if(workFlag==2){
				ct111.updateVotes(qData);
			}else if(workFlag==3){
				ct111.updateVoteItems(qData);
			}else if(workFlag==4){
				ct111.updateWebNews(qData);
			}else if(workFlag==5){
				ct111.updateWebNewsClasses(qData);
			}else if(workFlag==6){
				ct111.updateWebNewsComment(qData);
			}else if(workFlag==7){
				ct111.updateCmsUserStock(qData);
			}else if(workFlag==8){
				ct111.updateVoteRecord(qData);
			}
		}

		@Override
		public String[] getKeys() {
			if(workFlag==0) {
				return new String[]{"title","date","startTime","endTime","roomName","teacher"};
			}else if(workFlag==2){
				return new String[]{"title","type","levels","enabled","start","end","tags","multiple","addPerson","addPersonName","addTime","modifyPerson","modifyPersonName","modifyTime"};
			}else if(workFlag==3){
				return new String[]{"voteid","pid","content","color","score","type","enabled","counts","sums","right","jumpto","notrequired","orderindex","addperson","addtime","modifyperson","modifytime","rightanswer","picture","tag_name","tag_value"};
			}else if(workFlag==4){
				return new String[]{"classid","title","subtitle","link","type","contentType","spareVideo","authlevel","serviceLevel","starLevel","times","author","authorinfo","keyword","tags","mediaId","tencentMediaId","content","auditing","isDelete","isFocus","isSpecial","isShadow","orderIndex"};
			}else if(workFlag==5){
				return new String[]{"parentID","className","type","description","isHidden","orderindex","serviceLevel","enabled","addPerson","addTime","modfiyPerson","modfiyTime"};
			}else if(workFlag==6){
				return new String[]{"newsid","personid","score","ip","time","from"};
			}else if(workFlag==7){
				return new String[]{"statdate","personId","stockCode","stockName","operateType","time","ip","measured","hited","hitDate","from"};
			}else if(workFlag==8){
				return new String[]{"itemid","person_id","person_name","time","ip","from","info"};
			}
			return new String[0];
		}

		@Override
		public String getTag() {
			String tag = "";
			if(workFlag==0){
				tag = "Curser";
			}else if(workFlag==1){
				tag = "Property";
			}else if(workFlag==2){
				tag = "Votes";
			}else if(workFlag==3){
				tag = "VotesItems";
			}else if(workFlag==4){
				tag = "Webnews";
			}else if(workFlag==5){
				tag = "WebnewsClasses";
			}else if(workFlag==6){
				tag = "WebnewsComment";
			}else if(workFlag==7){
				tag = "CmsUserStock";
			}else if(workFlag==8){
				tag = "VoteRecord";
			}
			return String.format("%s>%s",TAG,tag);
		}

		@Override
		public int getInserSize() {
			return 1000;
		}
	}

}
