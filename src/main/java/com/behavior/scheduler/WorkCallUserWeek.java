package com.behavior.scheduler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper9101.CallTask9101Mapper;
import com.cobin.util.Tools;

/**
 * @author  Cobin
 * @date    2019/7/24 17:03
 * @version 1.0
*/
public class WorkCallUserWeek extends WorkJob {
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
	public void execWork(BehaviorMain bm,String sdate){
		CallTask9101Mapper ct91 = bm.getMapper(CallTask9101Mapper.class);
		//活动周期表
//		List<Map<String,Object>> config = ct91.queryConfig("20171231", "20171231");
		//某一日的用户比例及总数
//		List<List<Map<String, Object>>> users = ct91.queryDepartRate(20180102,0.15);
		//获取excel中配置系数
//		List<List<Map<String,Double>>> excel0 = getExcelConfig("计算招聘人数新单.xlsx");
//		List<List<Map<String,Object>>> weekUsers0 = ct91.queryWeekUsersTrapezium(excel0.get(0), excel0.get(1),excel0.get(2), users.get(0));
		
		List<List<Map<String,Double>>> excel = getExcelConfig("计算招聘人数.xlsx");
//		ct91.updateConfigPersonnel(excel.get(0), 1);
		ct91.updateConfigRetention(excel.get(1), 1);
		ct91.updateActStatus(excel.get(2), 1);
		//根据参数进行迭代生成每周新用户的信息
		// 0 全部各个中心数据 1 梯形数据全部  2 各个中心梯形数据
//		List<List<Map<String,Object>>> weekUsers = ct91.queryWeekUsersTrapezium(excel.get(0), excel.get(1),excel.get(2), users.get(0));
//		try {
//
//			Map<String,Object> _cf = config.get(0);
//			
//			int nNum = (int)_cf.get("nWeekEnd"); //第二个活动开始周
//			
//			String outFileName = "上拽1-6周招聘人员计划0111.xlsx";				
//			Map<String,Object> data0 = new HashMap<>();
//			//去掉前4周的数据
//			List<Map<String,Object>> newUser = weekUsers0.get(0);
//			for(int i=0;i<4;i++) newUser.remove(0);
//			//合并数据
//			List<Map<String,Object>> weekUser = weekUsers.get(0);
//			String[] keys = {"newUser7Day","newUser2MonthInner","newUser2MonthOut"};
//			for(int i=0;i<weekUser.size() && i<newUser.size() && i<=nNum;i++) {
//				Map<String,Object> dataTmp0 = weekUser.get(i);
//				Map<String,Object> dataTmp1 = newUser.get(i);
//				for(Map.Entry<String, Object> v:dataTmp1.entrySet()) {
//					String ky = v.getKey();					
//					for(String k:keys) {						
//						if(ky.indexOf(k)!=-1) {
//							double d = (double)dataTmp0.get(ky);
//							d +=(double)v.getValue();
//							dataTmp0.put(ky,d);
//							break;
//						}
//					}
//				}				
//			}			
//			
//			//年初人数 first_xx,年末人数last，新单部年初人数 newFirst,newLast
//			Map<String,Object> userAll = new HashMap<>();
//			Map<String,Object> newUser0 = newUser.get(0);
//			//for(Map<String, Object> row:weekUsers.get(0)) {
//			for(int i=0; i<nNum+1;i++) {
//				Map<String, Object> row=weekUsers.get(0).get(i);
//				for(Map<String, Object> u:users.get(0)) {
//					int regionType =(int) u.get("regionType");
//					int userCount = (int)u.get("userCount");
//					int pullUp = (int)u.get("pullUser");
//					row.put("pullUp_"+regionType, pullUp);
//					if(i==0) {
//						double n0 = (double)newUser0.get("newUser7Day_"+regionType)+(double)newUser0.get("newUser2MonthInner_"+regionType)+(double)newUser0.get("newUser2MonthOut_"+regionType);
//						data0.put("pullUp_"+regionType, pullUp);
//						data0.put("oldUser_"+regionType, userCount);
//						int adeptId = deptCenter[0];
//						if(regionType==41) {
//							adeptId=deptCenter[1];
//						}else if(regionType==61) {
//							adeptId=deptCenter[2];
//						}
//						userAll.put("adeptId_"+regionType, adeptId);
//						userAll.put("first_"+regionType, pullUp==0?userCount:pullUp);
//						userAll.put("newFirst_"+regionType, (pullUp>0?userCount:0)+n0);
//					}
//				}					
//			}
//			//每期活动每周的数据集合
//			List<Map<String,Object>> exportData = new ArrayList<>();
//			//每期活动的每个小组人员分配及订单分配
//			List<Map<String,Object>> exportActUser = new ArrayList<>();
//			
//			//活动结束的时候，老人剩余的数据
//			Map<String,Object> data1 = new HashMap<>();
//			//活动数据
//			Map<String,Object> dataAct = new HashMap<>();			
//			
//			Map<String, Object> rowData = weekUsers.get(0).get(nNum);
//			for(Map<String, Object> u:users.get(0)) {
//				int regionType =(int) u.get("regionType");
//				data1.put("oldUser_"+regionType, rowData.get("oldUserAll_"+regionType));
//			}
//			
//			exportData.add(data0);
//			
//			List<Map<String,Object>> userList = new ArrayList<>();
//			
//			//第一期活动内的人员变化情况
//			int actType =(int) _cf.get("ActType");
//			int actId = (int)_cf.get("ActId");
//			List<Map<String,Object>> weekUser0 = weekUsers.get(0);
//			for(int i=0;i<weekUser0.size() && i<nNum+1;i++) {
//				Map<String,Object> _mdata = new HashMap<>();
//				_mdata.putAll(weekUser0.get(i));
//				setSignCount(users.get(0), _mdata,actId,actType,excel.get(2),userList,i);			
//				exportData.add(_mdata);				
//				setExportAct(dataAct,users.get(0),_mdata);				
//			}	
//			
//			dataAct.put("actId", _cf.get("ActId"));
//			dataAct.put("actType", actType);
//			exportActUser.add(dataAct);
//			
//			exportData.add(data1);
//			int prevWeekStart =1;
//			int weekStart =1;
//			int weekEnd =1;
//			//各期活动中的人员变化情况
//			for(int j=1;j<config.size();j++) {
//				Map<String,Object> cf = config.get(j);
//				weekStart = (int)cf.get("nWeekStart")-1;
//				weekEnd = (int)cf.get("nWeekEnd")-1;
//				actType =(int)cf.get("ActType");
//				actId = (int)cf.get("ActId");
//				callNextAct(ct91, excel, users,weekUsers.get(2), weekUser0, exportData,actId,actType
//						,prevWeekStart,weekStart,weekEnd,exportActUser
//						,userAll,userList);
//				prevWeekStart = weekStart-8>0?weekStart-8:1;
//			}
//
//			//System.out.println(userList.toString().replaceAll("\\}, \\{", "},\n{"));
//			
//			List<Map<String,Object>> uAll = new ArrayList<>();
//			Map<String,Map<String,Object>> uTmp = new HashMap<>();
//			for(Map.Entry<String, Object> mv:userAll.entrySet()) {
//				String key = mv.getKey();
//				String region = key.substring(key.length()-2);
//				Map<String,Object> m = uTmp.get(region);
//				if(m==null) {
//					m = new HashMap<>();
//					uTmp.put(region, m);
//					m.put("regionType", region);
//					uAll.add(m);
//				}
//				m.put(key.substring(0, key.length()-3),mv.getValue());
//			}
//						
//			ct91.queryWeek(userList,uAll,users.get(1),1);
//			
//			getActWeekOver(weekUser0,weekEnd,exportActUser.get(exportActUser.size()-1));
//			
//			List<Map<String,Object>> dUser = anyActData(exportActUser);
//			
//			ct91.updateForecastDept(users.get(1),dUser,1);
//			
//			writeExcel(outFileName, exportData);
//			
			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}
	
	protected List<Map<String,Object>> anyActData(List<Map<String,Object>> exportData) {
		List<Map<String,Object>> data=new ArrayList<>();
		Map<String,Map<String,Object>> map = new HashMap<>();
		for(Map<String, Object> actData:exportData) {//每一行数据
			map.clear();
			//需要处理，把云集中心的新单部合并处理
			for(Map.Entry<String, Object> mv:actData.entrySet()) { // 每一个元素处理
				String key =mv.getKey();
				if(key.indexOf("_")!=-1) {
					String region = key.substring(key.length()-2);
					Map<String,Object> m = map.get(region);
					if(m==null) {
						m = new HashMap<>();
						map.put(region, m);
						m.put("regionType", region);
						m.put("actId", actData.get("actId"));
						m.put("actType", actData.get("actType"));
						data.add(m);
						if(region.equals("41")) {
							m.put("adeptId", deptCenter[1]);
						}else if(region.equals("61")) {
							m.put("adeptId", deptCenter[2]);
						}else {
							m.put("adeptId", deptCenter[0]);
						}
					}
					String _key = key.substring(0, key.length()-3);
					m.put(_key, mv.getValue());
					if(region.equals("41") || region.equals("61")) {
						m.put(_key+"Xd", mv.getValue());
					}else {
						Double dd =(Double) m.get(_key+"Xd");
						if(dd==null) dd = 0d;
						dd +=(double)mv.getValue();
						m.put(_key+"Xd", dd);
					}
				}
			}
		}
		return data;
	}
	
	private void setSignCount(List<Map<String,Object>> userRegion,Map<String,Object> mData
			,int actId,int actType,List<Map<String,Double>> excel2
			,List<Map<String,Object>> userList,int weekId) {
		
		Map<String,Object> userMap = new HashMap<>();
		userMap.put("weekId", weekId);
		double d1=SingularNumber[1],d2=SingularNumber[2];
		if(actType==1) {
			d2=d1;
		}
		for(Map<String, Object> u:userRegion) {
			int regionType =(int) u.get("regionType");
			Double _v = (Double)mData.get("signRegionXr_"+regionType); //新单部做的新单数 
			Double _vp = (Double)mData.get("personCount_"+regionType); //新单部的人数
			Double _vs = (Double)mData.get("signRegion_"+regionType);  //小单期间非新单部做的小单数
			if(_v==null) _v=0d;
			if(_vs==null) _vs = 0d;
			if(_vp==null) _vp = 0d;
			
			for(Map.Entry<String, Object> mv:mData.entrySet()) {
				String key = mv.getKey();				
				if(key.endsWith(""+regionType)) {
					if(!userMap.containsKey("upUser_"+regionType)) {
						userMap.put("upUser_"+regionType, 0d);
					}
					if(!userMap.containsKey("backUser_"+regionType)) {
						userMap.put("backUser_"+regionType, 0d);
					}
					if(key.startsWith("oldUser_")) {
						if(actType!=1) {
							if(isUp(excel2,actId,regionType)) {
								_vs+=(double)mv.getValue()*d2;
								userMap.put("upUser_"+regionType, mv.getValue());
							}else {
								_v +=(double)mv.getValue()*d2;
								_vp+=(double)mv.getValue();
								userMap.put("backUser_"+regionType, mv.getValue());
							}
						}else {
							_vs+=(double)mv.getValue()*d2;
							userMap.put("upUser_"+regionType, mv.getValue());
						}
					}else if(key.startsWith("newUser2MonthInner_")) {
						_v +=(double)mv.getValue()*SingularNumber[0];
						_vp+=(double)mv.getValue();
						userMap.put(key, mv.getValue());
					}else if(key.startsWith("newUser2MonthOut_")) {
						_v +=(double)mv.getValue()*d1;
						_vp+=(double)mv.getValue();
						userMap.put(key, mv.getValue());
					}else {
						userMap.put(key, mv.getValue());
					}
				}				
			}			
			mData.put("signRegionXr_"+regionType,_v);
			mData.put("signRegion_"+regionType,_vs);
			mData.put("personCount_"+regionType,_vp);
		}		
		userList.add(userMap);
	}
	
	public boolean isUp(List<Map<String,Double>> excel2,int actId,int regionType) {
		for(Map<String, Double> mv:excel2) {
			if(mv.get("actId")==actId && mv.get("regionType")==regionType) {
				return mv.get("isUp")==1;
			}
		}
		return false;
	}
	
	public void setExportAct(Map<String,Object> actData,List<Map<String,Object>> userRegion,Map<String,Object> mData) {
		for(Map<String, Object> u:userRegion) {
			int regionType =(int) u.get("regionType");
			Double _v = (Double)actData.get("signRegion_"+regionType);
			Double _vr = (Double)actData.get("signRegionXr_"+regionType);
			if(_v==null) _v=0d;
			if(_vr==null) _vr=0d;
			_v+=(double)mData.get("signRegion_"+regionType);
			actData.put("signRegion_"+regionType,_v);
			_vr+=(double)mData.get("signRegionXr_"+regionType);
			actData.put("signRegionXr_"+regionType,_vr);
			actData.put("personCount_"+regionType, mData.get("personCount_"+regionType));
		}
	}
	
	
	public void writeExcel(String outFileName,List<Map<String,Object>> exportData) throws IOException  {
		Workbook  wb = new XSSFWorkbook();  	
		
		writerUsersExcel(wb,exportData,titleCell1,0);//用户总体统计数据
		
		//创建文件流   
        OutputStream stream = new FileOutputStream(outFileName);  
        //写入数据   
        wb.write(stream);  
        wb.close();
        //关闭文件流   
        stream.close(); 
	}
	
	
	public void callNextAct(CallTask9101Mapper ct91
			,List<List<Map<String,Double>>> excel,List<List<Map<String, Object>>> users 
			,List<Map<String, Object>> weekUsers 
			,List<Map<String,Object>> weekUser0,List<Map<String,Object>> exportData
			,int actId,int actType,int prevWeekStart,int weekStart,int weekEnd
			,List<Map<String,Object>> exportActUser
			,Map<String,Object> userAll
			,List<Map<String,Object>> userList) {
		//此处迭代进行人员分配，每一期活动结束后把成单数据进行
		Map<String,Object> exportAct = exportActUser.get(exportActUser.size()-1);
		//第二期...N期活动 ，actType 活动类型 1 小单360  2 3600 3 18000
		Map<String,Double> vData = getActWeekOver(weekUser0,weekStart,exportAct);		
//		System.out.println(weekStart+">>>>>"+vData);
		//根据用户信息进行重新计算老人的留存情况，非小单活动则获取留下人员的 10% 为轮耕人员既回炉用户
		List<Map<String,Object>> back = new ArrayList<>();
		Map<String,Object> exportDataLast = exportData.get(exportData.size()-1);
		for(Map.Entry<String, Double> v:vData.entrySet()) {
			double old = getRetentionOldTurnoverRate(excel.get(1), weekStart, Tools.getInt(v.getKey()));
			double rate = getRetentionBakeAgainRate(excel.get(2), weekStart, weekEnd,Tools.getInt(v.getKey()));
			Map<String,Object> val = new HashMap<>();
			exportDataLast.put("pullUp_"+v.getKey(), v.getValue());
			val.put("regionType", v.getKey());
			val.put("allUserCount", v.getValue()*(1-rate));
			val.put("userCount", v.getValue()*rate/old);
			back.add(val);			
		}
		
		Map<String,Object> dataAct = new HashMap<>();
		List<Map<String,Object>> backUser = ct91.queryBakeAgain(excel.get(0), excel.get(1),back);
		for(int i=weekStart;i<weekUser0.size() && i<=weekEnd;i++) {
			Map<String,Object> _back = backUser.get(i);
			Map<String,Object> _mdata = new HashMap<>();
			_mdata.putAll(weekUser0.get(i));
			for(Map<String, Object> u:users.get(0)) {
				int regionType =(int) u.get("regionType");				 
				_mdata.put("pullUp_"+regionType, _back.get("oldUserAll_"+regionType));
				
				double _dd = (double)_mdata.get("newUser2MonthOut_"+regionType);
				Map<String, Object> txData = getRegionWeek(weekUsers, regionType, i);
				double _sdd = getWeekFullOver(txData,1, weekStart-8);
				_mdata.put("newUser2MonthOut_"+regionType,_dd-_sdd);
//				_mdata.put("oldUser_"+regionType, _back.get("oldUser_"+regionType));
				if(actType==1) {
					_sdd = getWeekFullOver(txData, prevWeekStart, weekStart-8);	
//					_mdata.put("oldUser_"+regionType, (double)_back.get("oldUser_"+regionType));
					if(regionType==61) {						
//						log.debug(actId+">>>"+weekStart+">>>>"+_mdata+">>>>>"+_sdd);
						_mdata.put("oldUser_"+regionType, (double)_back.get("oldUser_"+regionType));
					}else {
						_mdata.put("oldUser_"+regionType, (double)_mdata.get("oldUserAll_"+regionType)+_sdd);
					}
				}else {
					_mdata.put("oldUser_"+regionType, _back.get("oldUser_"+regionType));
				}
				double pup = (double) _mdata.get("pullUp_"+regionType);
				userAll.put("last_"+regionType, pup>0?pup:_mdata.get("oldUser_"+regionType));
				double n0 = (double)_mdata.get("newUser7Day_"+regionType)+(double)_mdata.get("newUser2MonthInner_"+regionType)+(double)_mdata.get("newUser2MonthOut_"+regionType);
				userAll.put("newLast_"+regionType, (pup>0?(double)_mdata.get("oldUser_"+regionType):0)+n0);
//				if(regionType==11)
//					log.debug(i+":::::"+_dd+"::::"+_sdd);
			}
			setSignCount(users.get(0), _mdata,actId,actType,excel.get(2),userList,i);
			exportData.add(_mdata);
			setExportAct(dataAct,users.get(0),_mdata);
		}
		dataAct.put("actId", actId);
		dataAct.put("actType", actType);
		exportActUser.add(dataAct);
		
		if(weekEnd<weekUser0.size()) {
			Map<String,Object> data1 = new HashMap<>();
			Map<String, Object> rowData =weekUser0.get(weekEnd);
			for(Map<String, Object> u:users.get(0)) {
				int regionType =(int) u.get("regionType");
				data1.put("oldUser_"+regionType, rowData.get("oldUserAll_"+regionType));
			}
			exportData.add(data1);
		}
	}
	
	private double getRetentionBakeAgainRate(List<Map<String,Double>> data,int weekStart,int weekEnd,int regionType) {
		double rate = 1;
		for(Map<String,Double> mv:data) {
			int nWeekStart = Tools.getInt(mv.get("weekStart").toString())-1;
			int nWeekEnd = Tools.getInt(mv.get("weekEnd").toString())-1;	
			int nRegionType =Tools.getInt(mv.get("regionType").toString());
			if(regionType==nRegionType
					&& nWeekStart>=weekStart && nWeekEnd<=weekEnd) {
				rate = mv.get("rate");
				break;
			}
		}
		return rate;
	}
	
	public Map<String,Double> getActWeekOver(List<Map<String,Object>> weekUser0,int weekStart,Map<String,Object> exportAct) {
		//第二期...N期活动 ，actType 活动类型 1 小单360  2 3600 3 18000
		Map<String,Object> vM = weekUser0.get(weekStart-1); //获取上轮活动结束时的人员情况
		Map<String,Double> vData = new HashMap<>();
		for(Map.Entry<String, Object> v:vM.entrySet()) {
			String key = v.getKey();
			String rg = key.substring(key.length()-2);
			Double dv = vData.get(rg);
			if(dv==null) {
				dv = 0d;
			}
			//符合条件进行人员合并，也就是老用户+过新人的（2月以后的用户）便于下一个活动进行分配
			if(key.startsWith("oldUserAll_") || key.startsWith("newUser2MonthOut_")){ 
				vData.put(rg, (double)v.getValue()+dv);
				exportAct.put("userRegion_"+rg, (double)v.getValue()+dv);	
			}
		}
		return vData;
	}
	
//	public void setUserRegion(Map<String,Double> vData,List<Map<String,Object>> exportActUser) {
//		//此处迭代进行人员分配，每一期活动结束后把成单数据进行
//		Map<String,Object> exportAct = exportActUser.get(exportActUser.size()-1);
////		System.out.println(weekStart+">>>>>"+vData);
//		//根据用户信息进行重新计算老人的留存情况，非小单活动则获取留下人员的 10% 为轮耕人员既回炉用户
//		for(Map.Entry<String, Double> v:vData.entrySet()) {	
//			exportAct.put("userRegion_"+v.getKey(), v.getValue());	
//		}
//	}
	
	public double getRetentionOldTurnoverRate(List<Map<String,Double>> retention,int weekNum,int regionType) {
		for(Map<String,Double> row:retention) {
			if(Tools.getInt(row.get("RegionType").toString())==regionType && Tools.getInt(row.get("WeekId").toString())==weekNum) {
				return Tools.getDouble(row.get("OldTurnoverRate").toString());
			}
		}
		return 1;
	}
	
	public Map<String, Object> getRegionWeek(List<Map<String,Object>> list, int nRegionType,int nWeekId) {
		for(Map<String, Object> v:list) {
			if((int)v.get("WeekNum")==nWeekId+1 && (int)v.get("RegionType")==nRegionType) {
//				if(nRegionType==11)
//					log.debug(nWeekId+">>>>"+v);
				return v;
			}
		}
		return null;
	}
	
	public double getWeekFullOver(Map<String, Object> val,int nStartWeekId,int nSubWeekId) {
		double ret = 0;
		if(val!=null) {
			for(int i=nStartWeekId;i<=nSubWeekId;i++) {
				ret +=(double)val.get(""+i);
			}
		}
		return ret;
	}
	
	public void execWork11(BehaviorMain bm){
		int exportFlag = 1;	
		CallTask9101Mapper ct91 = bm.getMapper(CallTask9101Mapper.class);
		//活动周期表
		List<Map<String,Object>> config = ct91.queryConfig("20180101", "20171229");
		
		//某一日的用户比例及总数
		List<List<Map<String, Object>>> users = ct91.queryDepartRate(20180102,0.15);
		//获取excel中配置系数
		List<List<Map<String,Double>>> excel = getExcelConfig(exportFlag==1?"计算招聘人数.xlsx":"计算招聘人数.xlsx");
 
		//根据参数进行迭代生成每周新用户的信息
		List<List<Map<String,Object>>> weekUsers = ct91.queryWeekUsersTrapezium(excel.get(0), excel.get(1),excel.get(2), users.get(0));
			
		try {
			String outFileName = "招聘计划00.xlsx";
			Workbook  wb = new XSSFWorkbook();  			
			if(exportFlag==1) {
				outFileName = "上拽1-6周招聘人员计划0111.xlsx";
				Map<String,Object> data0 = new HashMap<>();
				
				for(Map<String, Object> row:weekUsers.get(0)) {
					for(Map<String, Object> u:users.get(0)) {
						int regionType =(int) u.get("regionType");
						int userCount = (int)u.get("userCount");
						int pullUp = (int)u.get("pullUser");
						row.put("pullUp_"+regionType, pullUp);
						data0.put("pullUp_"+regionType, pullUp);
						data0.put("oldUser_"+regionType, userCount);
					}					
				}
				List<Map<String,Object>> exportData = new ArrayList<>();
				Map<String,Object> data1 = new HashMap<>();
				Map<String, Object> rowData = weekUsers.get(0).get(5);
				for(Map<String, Object> u:users.get(0)) {
					int regionType =(int) u.get("regionType");
					data1.put("oldUser_"+regionType, rowData.get("oldUserAll_"+regionType));
				}
				exportData.add(data0);
				List<Map<String,Object>> _data = weekUsers.get(0);
				for(int i=0;i<_data.size() && i<6;i++) {
					Map<String,Object> _mdata = _data.get(i);
					exportData.add(_mdata);
				}
				exportData.add(data1);

				writerUsersExcel(wb,exportData,titleCell1,0);//用户总体统计数据
			}else {			
				writerUsersExcel(wb,weekUsers.get(0),titleCell,0);//用户总体统计数据
				writerTrapeziumExcel(wb,"合计",weekUsers.get(1)); //所有新用户的梯形数据
				writerTrapeziumExcel(wb,"分中心",weekUsers.get(2)); //所有新用户的梯形数据			
				writerDistributionExcel(wb,users.get(0),users.get(1),config,weekUsers.get(2),weekUsers.get(0)); //根据周期生成对应的人员分布情况
			}
			//创建文件流   
	        OutputStream stream = new FileOutputStream(outFileName);  
	        //写入数据   
	        wb.write(stream);  
	        wb.close();
	        //关闭文件流   
	        stream.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public List<List<Map<String,Double>>> getExcelConfig(String pzExcel) {
		List<List<Map<String,Double>>> config = new ArrayList<>();
		int[] cols1 = new int[] {1,2,3,4};
		String[] key1 = new String[] {"WeekId" ,
		          "RegionType" ,
		          "SZRecruitCount" ,
				  "NJRecruitCount" };
		int[] cols2 = new int[] {7,7,8,9,10,11};
		String[] key2 = new String[] {"numIndex ", "WeekId", 
				"RegionType" , 
				"NewSZTurnoverRate" ,
				"NewNJTurnoverRate" ,
				"OldTurnoverRate" };
		int[] cols3 = new int[] {1,2,3,4,5,6};
		String[] key3 = new String[] {"actId","weekStart", "weekEnd", 
				"regionType" , 
				"rate",
				"isUp"};
		List<Map<String,Double>> c1 = new ArrayList<>();
		List<Map<String,Double>> c2 = new ArrayList<>();
		List<Map<String,Double>> c3 = new ArrayList<>();
		try {
			InputStream is = new FileInputStream(pzExcel);//"计算招聘人数新单.xlsx"
			Workbook wb = new XSSFWorkbook(is);  
			for(int j=0;j<6 && j<wb.getNumberOfSheets();j++) {
				Sheet sheet = wb.getSheetAt(j);
				int rowNum = sheet.getLastRowNum();  
				// 正文内容应该从第二行开始,第一行为表头的标题  
		        for (int i = 1; i <= rowNum; i++) {  
		        	Row row = sheet.getRow(i);
		        	Object s = row.getCell(cols1[1]);
		        	if(s==null || s.toString().length()==0) break;
		        	//if(row.getCell(1).getNumericCellValue()>0) break;
		        	if(j<5) {
		        		Map<String,Double> mm= getCells(row,cols1,key1);
			        	if(mm.size()==cols1.length) {
			        		c1.add(mm);
			        	}
			            c2.add(getCells(row,cols2,key2));
		        	}else {
		        		c3.add(getCells(row,cols3,key3));
		        	}
		        }  
			}
			wb.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		config.add(c1);
		config.add(c2);
		config.add(c3);
		return config;
	}
	
	private Map<String,Double> getCells(Row row,int[] cols,String[] keys){
		Map<String,Double> cellValue = new HashMap<>();  
        for (int j=0;j<cols.length;j++) {  
            String obj = getCellFormatValue(row.getCell(cols[j])).toString();  
            if(obj.trim().length()==0) {
            	obj = "0";
            }
            cellValue.put(keys[j], Double.parseDouble(obj));
        } 
        return cellValue;
	}
	
    private Object getCellFormatValue(Cell cell) {  
        Object cellvalue = "";  
        if (cell != null) {  
            // 判断当前Cell的Type  
            switch (cell.getCellType()) {  
            case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC  
            case Cell.CELL_TYPE_FORMULA: {  
                // 判断当前的cell是否为Date  
                if (DateUtil.isCellDateFormatted(cell)) {  
                    // 如果是Date类型则，转化为Data格式  
                    // data格式是带时分秒的：2013-7-10 0:00:00  
                    // cellvalue = cell.getDateCellValue().toLocaleString();  
                    // data格式是不带带时分秒的：2013-7-10  
                    Date date = cell.getDateCellValue();  
                    cellvalue = date;  
                } else {// 如果是纯数字  
                    // 取得当前Cell的数值  
                    cellvalue = String.valueOf(cell.getNumericCellValue());  
                }  
                break;  
            }  
            case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING  
                // 取得当前的Cell字符串  
                cellvalue = cell.getRichStringCellValue().getString();  
                break;  
            default:// 默认的Cell值  
                cellvalue = "";  
            }  
        } else {  
            cellvalue = "";  
        }  
        return cellvalue;  
    }  
    
    private CellStyle getExcelCellStyle(Workbook  wb) {
    	CellStyle style = wb.createCellStyle(); // 样式对象      
        // 设置单元格的背景颜色为淡蓝色  
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);         
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直      
        style.setAlignment(CellStyle.ALIGN_CENTER);// 水平   
        style.setWrapText(true);// 指定当单元格内容显示不下时自动换行               
        
        Font font = wb.createFont();  
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);  
        font.setFontName("宋体");  
        font.setFontHeight((short) 280);  
        style.setFont(font); 
        return style;
    }
    
    private List<Map<String,Object>> getWeekRegionTypeUsers(List<Map<String,Object>> weekRegionTypeUsers,int regionType){
    	List<Map<String,Object>> result = new ArrayList<>();
    	for(Map<String,Object> row:weekRegionTypeUsers) {
    		if(regionType==(Integer)row.get("RegionType")) {
    			result.add(row);
    		}
    	}
    	return result;
    }
    /**
     * 通过中心标识获取中心对应的周数据及中心各个部门的比例
     * @param wb
     * @param userAll
     * @param userPart
     * @param config
     * @param weekRegionTypeUsers
     */
    public void writerDistributionExcel(Workbook wb
    		,List<Map<String,Object>> userAll,List<Map<String,Object>> userPart
    		,List<Map<String,Object>> config,List<Map<String,Object>> weekRegionTypeUsers
    		,List<Map<String,Object>> weekOldUsers) {      	
    	for(Map<String,Object> user:userAll) { //中心数据    	
    		List<Map<String,Object>> outData = new ArrayList<>();
    		int regionType = (int)user.get("regionType");
    		List<Map<String,Object>> data = getWeekRegionTypeUsers(weekRegionTypeUsers,regionType); //中心对应的周梯形数据
    		int nStart = 1; //最后一次的可用新人的周
    		int nMinStart = 8;
    		for(Map<String,Object> row:config) { //活动周期数据列表
        		int weekStart = (int)row.get("nWeekStart")-1;
        		int weekEnd = (int)row.get("nWeekEnd")-1;
        		if(weekEnd>=nMinStart) { //判断活动区间内是否满足可使用新人的时间，默认8周(2月)后可以使用
        			if(weekStart<nMinStart) { //如果活动起始周小于则取最小值
        				weekStart = nMinStart;
        			}
        			for(int i=weekStart;i<weekEnd;i++) { //活动周期内运行，因为i是下标为零开始，周是1开始，因此需要矫正
        				Map<String,Object> rowData = data.get(i);
        				double total = 0;
        				for(int j=nStart;j<=i-nMinStart+1;j++) {
        					double d = (double)rowData.get(String.valueOf(j));
        					total+=d;
        				}
        				double oldUser = (double)weekOldUsers.get(i).get("oldUser_"+regionType);
//        				System.out.println(total+">>>>>>>"+i+",,,"+nStart);
        				Map<String,Object> oData1 = new HashMap<>();
    					oData1.put("a周", i+1);
        				for(Map<String,Object> part:userPart) {
        					int _regId = (int)part.get("regionType");
        					if(_regId == regionType) {
        						double rate = ((BigDecimal)part.get("rate")).doubleValue();	
        						int depetid = (int)part.get("adpetId");  
        						String deptName = (String)part.get("deptName");
	        					oData1.put(String.format("b%s(%d)新人",deptName,depetid), rate*total);
	        					oData1.put(String.format("d%s(%d)老人",deptName,depetid), rate*oldUser);
        					}
        				}
        				oData1.put("c", "");
        				nStart = i-nMinStart+2;
        				outData.add(oData1);
        			}        			
        		}
        	}
    		writeDistributionExcel(wb,regionType,outData);
    	}
    }
    
    public void writeDistributionExcel(Workbook wb,int regionType,List<Map<String,Object>> list) {
    	Sheet sheet = wb.createSheet(getRegion(regionType));
    	Row row = sheet.createRow(0);    //添加表头 
    	Map<String,Object> mV = list.get(0);
    	List<String> listKey = new ArrayList<>();
    	listKey.addAll(mV.keySet());
    	Collections.sort(listKey);
    	CellStyle style = getExcelCellStyle(wb);
        for(int i = 0;i < listKey.size();i++){  
            Cell cell = row.createCell(i);  
            cell.setCellValue(listKey.get(i).substring(1));  
            cell.setCellStyle(style); // 样式，居中
            sheet.setColumnWidth(i, 20 * 256);            
        }  
        row.setHeight((short) (540*2)); 
      //循环写入行数据   
        for (int i = 0; i < list.size(); i++) {  
            row = sheet.createRow(i+1);  
            row.setHeight((short) 500); 
            Map<String,Object> val = list.get(i);
            for(int j = 0;j < listKey.size();j++){ 
            	Cell cell0 = row.createCell(j); 
            	Object obj =  val.get(listKey.get(j)); 
            	setCellValue(cell0,obj);
            }
        }
    }
    
    private void setCellValue(Cell cell,Object obj) {
    	if(obj!=null) {
	    	if (obj instanceof Double) {
				cell.setCellValue((double)obj);
	    	}else if(obj instanceof Integer) {
				cell.setCellValue((int)obj);
			}else {
				cell.setCellValue(obj.toString());
			}
    	}
    }
    
    public void writerTrapeziumExcel(Workbook  wb,String lab,List<Map<String,Object>> list) throws IOException { 
    	Sheet sheet = wb.createSheet(lab+"周梯形数据");
    	Row row = sheet.createRow(0);    //添加表头 
    	Map<String,Object> v = list.get(list.size()-1);
    	int py = 0;
    	CellStyle style = getExcelCellStyle(wb);
        for(int i = 0;i < v.size();i++){  
            Cell cell = row.createCell(i); 
            String val =String.valueOf(i-py);
            if(i==0) {val="周";}
            else if(i==1 && v.containsKey("RegionType")) {val="中心";py=1;}
            cell.setCellValue(val);  
            cell.setCellStyle(style); // 样式，居中
            sheet.setColumnWidth(i, 20 * 256);            
        }  
        row.setHeight((short) 540); 
        
        //循环写入行数据   
        for (int i = 0; i < list.size(); i++) {  
            row = sheet.createRow(i+1);  
            row.setHeight((short) 500); 
            Map<String,Object> val = list.get(i);
            for(int j = 0;j < v.size();j++){ 
            	Cell cell0 = row.createCell(j); 
            	Object obj = null;
            	if(j==0) {
            		obj = val.get("WeekNum");
            	}else if(j==1 && v.containsKey("RegionType")) {	
            		obj = val.get("RegionType");            		
            	}else {
            		obj = val.get(String.valueOf(j-py));
            	}
            	setCellValue(cell0,obj);
            }
        }
        
    }
    public void writerUsersExcel(Workbook  wb,List<Map<String,Object>> list,String[] titleRow,int maxCount) throws IOException {  
       
        Sheet sheet = wb.createSheet("分中心统计");         
        //添加表头  
//        Row row = sheet.createRow(0);
//        Cell cell = row.createCell(0);
//        cell.setCellValue("周数据内容");    //创建第一行    
        
        CellStyle style = getExcelCellStyle(wb);
        
//        cell.setCellStyle(style); // 样式，居中
 
        // 单元格合并      
        // 四个参数分别是：起始行，起始列，结束行，结束列      
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));  
//        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 20));  
//        sheet.autoSizeColumn(5200);
        
        Row row = sheet.createRow(0);    //创建第二行
        for(int i = 0;i < titleRow.length;i+=2){  
        	Cell cell = row.createCell(i/2);  
            cell.setCellValue(titleRow[i+1]);  
            cell.setCellStyle(style); // 样式，居中
            sheet.setColumnWidth(i/2, 20 * 256);            
        }  
        row.setHeight((short) 540);         
        CellStyle rowStyle = wb.createCellStyle();
        rowStyle.setShrinkToFit(true);
        rowStyle.setLocked(true);
        row.setRowStyle(rowStyle);
        //循环写入行数据   
        for (int i = 0; i < list.size(); i++) {  
            row = sheet.createRow(i+1);  
            row.setHeight((short) 500); 
            Map<String,Object> val = list.get(i);
            for(int j = 0;j < titleRow.length;j+=2){ 
            	Cell cell0 = row.createCell(j/2);            	
            	Object obj = val.get(titleRow[j]);
            	setCellValue(cell0,obj);
            }
            if(maxCount>0 && i>=maxCount-1) {
            	break;
            }
        }          
    } 
    
    private String getRegion(int regionType) {
    	for(int i=0;i<titleRegion.length;i+=2) {
    		if(titleRegion[i].equals(""+regionType)) {
    			return titleRegion[i+1];
    		}
    	}
    	return "分中心"+regionType;
    }
    
    private  String[] titleCell1 = new String[] {
			"WeekNum","周",
			"pullUp_11","云集中心一上拽",
			"oldUser_11","云集中心一回炉",
//			"newSingle_11","云集中心一小单",
			"newUser7Day_11","云集中心一在训",
			"newUser2MonthInner_11","云集中心一新人",
			"newUser2MonthOut_11","云集中心一过新人",
//			"signRegion_11","云集中心一单量",
//			"signRegionXr_11","云集中心一新单部单量",
//			"personCount_11","云集中心一新单部人数",

			"pullUp_12","云集中心二上拽",
			"oldUser_12","云集中心二回炉",
//			"newSingle_12","云集中心二小单",
			"newUser7Day_12","云集中心二在训",
			"newUser2MonthInner_12","云集中心二新人",
			"newUser2MonthOut_12","云集中心二过新人",
//			"signRegion_12","云集中心二单量",
//			"signRegionXr_12","云集中心二新单部单量",
//			"personCount_12","云集中心二新单部人数",

			"pullUp_13","云集中心三上拽",
			"oldUser_13","云集中心三回炉",
//			"newSingle_13","云集中心三小单",
			"newUser7Day_13","云集中心三在训",
			"newUser2MonthInner_13","云集中心三新人",
			"newUser2MonthOut_13","云集中心三过新人",
//			"signRegion_13","云集中心三单量",
//			"signRegionXr_13","云集中心三新单部单量",
//			"personCount_12","云集中心三新单部人数",
			
			"pullUp_41","智富上拽",
			"oldUser_41","智富回炉",
//			"newSingle_41","智富小单",
			"newUser7Day_41","智富在训",
			"newUser2MonthInner_41","智富新人",
			"newUser2MonthOut_41","智富过新人",
//			"signRegion_41","智富单量",
//			"signRegionXr_41","智富新单部单量",
//			"personCount_12","智富新单部人数",

			"pullUp_61","宝盛上拽",
			"oldUser_61","宝盛回炉",
//			"newSingle_61","宝盛小单",
			"newUser7Day_61","宝盛在训",
			"newUser2MonthInner_61","宝盛新人",
			"newUser2MonthOut_61","宝盛过新人"
//			"signRegion_61","宝盛单量",
//			"signRegionXr_61","宝盛新单部单量",
//			"personCount_61","宝盛新单部人数"
	};
    
    private  String[] titleCell = new String[] {
			"WeekNum","周",
			"newUser7Day_11","云集中心一7天",
			"newUser2MonthInner_11","云集中心一2月",
			"newUser2MonthOut_11","云集中心一过2月",
//			"oldUser_11","云集中心一老人",
			"oldUserAll_11","云集中心一老人",
//			"total_11","云集中心一合计",
			"newUser7Day_12","云集中心二7天",
			"newUser2MonthInner_12","云集中心二2月",
			"newUser2MonthOut_12","云集中心二过2月",
//			"oldUser_12","云集中心二老人",
			"oldUserAll_12","云集中心二老人",
//			"total_12","云集中心二合计",
			"newUser7Day_13","云集中心三7天",
			"newUser2MonthInner_13","云集中心三2月",
			"newUser2MonthOut_13","云集中心三过2月",
//			"oldUser_13","云集中心三老人",
			"oldUserAll_13","云集中心三老人",
//			"total_13","云集中心三合计",
			"newUser7Day_41","智富7天",
			"newUser2MonthInner_41","智富2月",
			"newUser2MonthOut_41","智富过2月",
//			"oldUser_41","智富老人",
			"oldUserAll_41","智富老人",
//			"total_41","智富合计",
			"newUser7Day_61","宝盛7天",
			"newUser2MonthInner_61","宝盛2月",
			"newUser2MonthOut_61","宝盛过2月",
//			"oldUser_61","宝盛老人",
			"oldUserAll_61","宝盛老人"
//			"total_61","宝盛合计"
	};
    
    private String[] titleRegion = {"11","云集中心一","12","云集中心二","13","云集中心三","41","智富","61","宝盛"};
    
    private double[] SingularNumber= {
    		5.0/4 // 培训
    		,14.4/4 //过新人
    		,18.0/4 //轮耕，回炉
    		,14.4/4 //宝盛小单
    		,18.0/4};//宝盛轮耕
    private int[] deptCenter= {8198,8432,8566};//云集：8198  智富：8432 宝盛：8566

}
