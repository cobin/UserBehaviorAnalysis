package com.behavior.scheduler;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.behavior.mapper.mapper1112.CallTask1112Mapper;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cobin(Cobin)
 * @desc TODO
 * @Date 2020/3/23 11:31
 */
public class WorkCallSmallOrderUserInfo extends WorkJob {
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
            CallTask1114Mapper ct1114 = bm.getMapper(CallTask1114Mapper.class);
            CallTask69Mapper ct69 = bm.getMapper(CallTask69Mapper.class);
            // 中心编号 11,12,13,15,18,41,48,49
            //部门编号 8198,8432 培训部门
            // 8979,8907,8565,8980,8848,9224 小单部门
            // 9183,9074,9184,9095 电商部门
            String sDate = "2020-03-23";
            String eDate = "2020-04-05";
            String[][] adepts ={
                    null
                    ,{"电商部门","9183,9074,9184,9095"}
                    ,{"小单部","8979,8907,8565,8980,8848,9224"}
                    //,{"云集直营部","11,12,13,15"}
                    //,{"智富直营部","41"}
                    ,{"云集招聘培训部","8198"}
                    ,{"智富招聘培训部","8432"}
            };

            File f = new File("lx"+ CDate.formatToLongDate().replaceFirst(" ","_").replaceAll(":","-")+".xls");
            try {
                WritableWorkbook workbook= Workbook.createWorkbook(f);
                int iSheet = 0;
                for(String[] doDepts:adepts) {
                    if(doDepts==null){
                        continue;
                    }
                    List<Map<Object, Object>> xlsResult = getResult(ct1114, ct69, sDate, eDate, doDepts[1]);
                    WritableSheet sheet = workbook.createSheet(doDepts[0], iSheet);
                    writeExcel(sheet, xlsResult);
                    iSheet++;
                }
                workbook.write();
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private List<Map<Object,Object>> getResult(CallTask1114Mapper ct1114,CallTask69Mapper ct69,String sDate,String eDate,String adepts){
            List<Map<Object,Object>> result = ct1114.getSmallOrderUserInfo(sDate,eDate,adepts);
            StringBuffer buff = new StringBuffer();
            Map<Object,Map<Object,Object>> mResult = new HashMap<>(result.size());
            for(Map<Object,Object> r:result){
                mResult.put(String.valueOf(r.get("keeperId")),r);
                if(buff.length()>0){
                    buff.append(",");
                }
                buff.append(r.get("keeperId"));
            }
            List<Map<Object,Object>> speedUsers = ct69.queryPvConfPresale(buff.toString());
            for(Map<Object,Object> r:speedUsers){
                Map<Object,Object> mv = mResult.get(String.valueOf(r.get("PERSONID")));
                if(mv!=null){
                    mv.putAll(r);
                }
            }
            return result;
        }

        private void writeExcel(WritableSheet sheet, List<Map<Object,Object>> result) throws WriteException {
            int rows = 1;
            for(Map<Object,Object> r:result) {
                for (int i = 0; i < exlHead.length; i += 2) {
                    String key = exlHead[i];
                    String head = exlHead[i + 1];
                    if(rows==1){
                        sheet.addCell(new Label(i/2,0,head));
                    }
                    Object obj = r.get(key);
                    if(obj instanceof Integer){
                        sheet.addCell(new Number(i/2,rows,(Integer)obj));
                    }else if(obj instanceof BigDecimal){
                        sheet.addCell(new Number(i/2,rows,((BigDecimal)obj).intValue()));
                    }else {
                        sheet.addCell(new Label(i / 2, rows, String.valueOf(obj)));
                    }
                }
                rows++;
            }
        }

        static final  String[] exlHead = {
                "keeperId","神经id","personName","姓名","groupName","小组","deptName","部门","hireDate","入职时间","leaveStatus","人员状态",
                "newResource","新号领取数","newTransNum","新单个数","b01NewTransNum","B0或B1的新单个数","specialNum","特别订单","lowerNum","低价订单",
                "b0NewTransNum","B0的新单数","b1NewTransNum","B1的新单数","ALLOCSPEED","新号流速","SPEEDDATE","激活日期"
        } ;
}
