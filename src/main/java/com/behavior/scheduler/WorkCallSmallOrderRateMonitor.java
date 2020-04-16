package com.behavior.scheduler;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper1114.CallTask1114Mapper;
import com.behavior.mapper.mapper69.CallTask69Mapper;
import com.cobin.util.CDate;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

/**
 * @author cobin(Cobin)
 * @desc TODO
 * @Date 2020/3/23 11:31
 */
public class WorkCallSmallOrderRateMonitor extends WorkJob {
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
            long runTime = System.currentTimeMillis();
            CallTask1114Mapper ct1114 = bm.getMapper(CallTask1114Mapper.class);
            //电商,小单部，大团队 1 WAP,2 PC,3 ALL
            // actId = 140 ,141 ,139
            int[] actIds = {140 ,141 ,139};
            List<Map<Object,Object>> traceList = new ArrayList<>();
            for(int nType=0;nType<actIds.length;nType++) {
                int actId = actIds[nType];
                List<Integer> units = ct1114.getMissionActUnits(actId);
                int fromWhere = 1;
                List<Integer[]> nDateList = getActDateList(ct1114,actId);
                Map<String,List<Map<String,Object>>> dsResult = getHistoryActOrderRate(ct1114,nType+1,fromWhere==1?"WAP":"PC");
                for(Integer unit:units){
                    log.info(String.format("%d,,,%d,,,,%d",actId,unit,nType+1));
                    execActMonitorRate(nDateList,dsResult,traceList,actId,unit,fromWhere);
                }
                int totalSize = traceList.size();
                log.info("需要更新数据："+totalSize);
                for (int i=0;i<=totalSize/1000;i++) {
                    ct1114.updateForecastData(traceList.subList(i*1000,Math.min((i+1)*1000,totalSize)));
                }
                traceList.clear();
            }
            log.info("运行时长："+(System.currentTimeMillis()-runTime)/1000);
        }

        private void execActMonitorRate(List<Integer[]> nDateList,Map<String,List<Map<String,Object>>> dsResult,List<Map<Object,Object>> traceList,int actId,int deptId,int fromWhere){
            List<Map<String,Object>> p360 = dsResult.get(deptId+"_360/260");
            if(p360==null){
                log.error("无历史数据参考>>"+actId+","+deptId+","+fromWhere);
                return;
            }

            List<List<Float>> data = new ArrayList<>();
            float minData = 0.04f;
            for(int i=0;i<nDateList.size();i++){
                List<Float> f = new ArrayList<>();
                for(int j=0;j<nDateList.size();j++){
                    if(j<p360.size()) {
                        Map<String, Object> mData = p360.get(j);
                        float allRate = ((BigDecimal) mData.get("allRate")).floatValue();

                        if(allRate>0) {
                            minData = Math.min(minData, allRate);
                            f.add(allRate);
                        }else{
                            //如果当前值为0表示此日没有历史数据参考，使用最后一日数据与最小数值之间的值
                            f.add((f.get(f.size()-1)+minData)/2);
                        }
                    }else{
                        //如果此日没有历史数据参考，使用最后一日数据与最小数值之间的值
                        f.add((f.get(f.size()-1)+minData)/2);
                    }
                }
                data.add(f);
            }
            int personCount = nDateList.get(0)[1];
            float lastRate = 0;
            //循环日
            for(int i=0;i<nDateList.size();i++){
                float v = 0;
                //第N日的成单率，倒梯形数值之和
                for(int k=0;k<=i;k++) {
                    List<Float> _data = data.get(k);
                    //每日倒梯形的横列之和
                    for (int j = 0; j <= i-k; j++) {
                        v +=_data.get(j);
                    }
                }
                v = v/(i+1);
                lastRate = v;
                // println(nDateList.get(i)[0]+"\t"+v)
                Map<Object,Object> mData = new HashMap<>();
                mData.put("unitId",deptId);
                mData.put("unitType",0);
                mData.put("actId",actId+90000);
                mData.put("sDate",nDateList.get(i)[0]);
                if(fromWhere==1) {
                    mData.put("wapOrderRate", v);
                    mData.put("pcOrderRate", "null");
                }else{
                    mData.put("wapOrderRate", "null");
                    mData.put("pcOrderRate", v);
                }
                traceList.add(mData);
            }
            int orderCount =(int)(personCount*5*nDateList.size()*lastRate);

        }

        private List<Integer[]> getActDateList(CallTask1114Mapper ct1114,int actId){
            //139 大团队 140 电商 141 小单部
            Map<String,Object> actInfo = ct1114.getMissionActInfoById(actId);
            LocalDateTime startDate = ((Timestamp) actInfo.get("startDate")).toLocalDateTime();
            LocalDateTime endDate = ((Timestamp) actInfo.get("endDate")).toLocalDateTime().plusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            List<Integer[]> nDateList = new ArrayList<>();
            int personCount = (int)actInfo.get("personCount");
            for(;startDate.isBefore(endDate);){
                int nDate = Integer.parseInt(startDate.format(formatter));
                int nMate = nDate%10000;
                if(startDate.getDayOfWeek().getValue()!=6) {
                    if(!(nMate>=501 && nMate<=503)){
                        nDateList.add(new Integer[]{ nDate, personCount});
                    }
                }
                startDate = startDate.plusDays(1);
            }
            return nDateList;
        }

        private Map<String,List<Map<String,Object>>> getHistoryActOrderRate(CallTask1114Mapper ct1114,int nType,String fromWhere){
            List<Map<String,Object>> resultRate = ct1114.getHistoryActProductRate(nType,fromWhere);
            Map<String,List<Map<String,Object>>> result = new HashMap<>(500);
            for(Map<String,Object> map:resultRate){
                String actName = (String)map.get("actName");
                List<Map<String,Object>> pData = result.get(actName);
                if(pData==null){
                    pData = new ArrayList<>();
                    result.put(actName,pData);
                    //BigDecimal
                }
                int rowId = (int)map.get("rowId");
                int nDays = (int)map.get("nDays");
                if(rowId<nDays && pData.size()<nDays){
                    int start = rowId;
                    if(!pData.isEmpty()){
                       Map<String,Object> m = pData.get(pData.size()-1);
                       start = (int)m.get("nDays")+1;
                    }
                    for(int i=start;i<nDays;i++){
                        Map<String,Object> mClone = new HashMap<>(map.size());
                        mClone.putAll(map);
                        mClone.put("nDays",i);
                        pData.add(mClone);
                    }
                }
                pData.add(map);
            }
            return result;
        }
}
