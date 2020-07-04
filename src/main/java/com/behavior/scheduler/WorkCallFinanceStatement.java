package com.behavior.scheduler;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper9105.CallTask9105Mapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.spi.LocaleServiceProvider;

/**
 * @author cobin(Cobin)
 * @desc TODO
 * @Date 2020/3/23 11:31
 */
public class WorkCallFinanceStatement extends WorkJob {
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
        public void execWork(BehaviorMain bm,String qDate) {
            //CallTask9105Mapper call9105 = bm.getMapper(CallTask9105Mapper.class);
            List<Map<String,String>> lists = getExcelData();
            List<List<Map<String,String>>> rows = new ArrayList<>();
            List<Map<String,String>> row= null ;
            for(int i=0;i<lists.size();i++) {
                Map<String,String> m =lists.get(i);
                if(i%1000==0){
                    row = new ArrayList<>();
                    rows.add(row);
                }
                row.add(m);
                if(m.get("subjectId").equals("1001") && m.get("subjectTypeId").equals("1125")){
                    System.out.println(m);
                }
            }
//            List<Map<Object,Object>> result = call9105.updateMuliFinanceStatement(rows);
//            for(Map<Object,Object> r:result){
//                System.out.println(r);
//            }
        }

        private List<Map<String,String>> getExcelData(){
            List<Map<String,String>> result = new ArrayList<>();
            Workbook wb = null;
            String filePath="D:\\ZNZ-Excel\\财务数据模板 0508.xlsx";
            String extString = filePath.substring(filePath.lastIndexOf("."));
            InputStream is = null;
            try {
                is = new FileInputStream(filePath);
                if(".xls".equals(extString)){
                    wb = new HSSFWorkbook(is);
                }else if(".xlsx".equals(extString)){
                    wb = new XSSFWorkbook(is);
                }
                if(wb!=null){
                    //获取第一个sheet
                    Sheet sheet = wb.getSheetAt(0);
                    //获取最大行数
                    int rownum = sheet.getPhysicalNumberOfRows();
                    //获取第一行
                    Row row = sheet.getRow(1);
                    //获取最大列数
                    //int colnum = row.getPhysicalNumberOfCells();
                    List<int[]> colnums = new ArrayList<>();
                    for(int i=0;i<row.getPhysicalNumberOfCells();i++){
                        String v =  getCellFormatValue(row.getCell(i));
                        String[] vs = v.split("_");
                        if(vs.length==2){
                            colnums.add(new int[]{i,Integer.parseInt(vs[0]),Integer.parseInt(vs[1])});
                        }
                    }
                    for (int i = 2; i<rownum; i++) {
                        row = sheet.getRow(i);
                        if(row !=null){
                            String subjectId = getCellFormatValue(row.getCell(0));
                            String subjectTypeId = getCellFormatValue(row.getCell(1));
                            if(subjectId.length()==0){
                                subjectId="0";
                                subjectTypeId="0";
                            }
                            String sCode =  getCellFormatValue(row.getCell(2));
                            String subjectName =  getCellFormatValue(row.getCell(3));
                            String tCode =  getCellFormatValue(row.getCell(4));
                            String subjectTypeName =  getCellFormatValue(row.getCell(5));
                            for (int[] colnum:colnums){
                               int j = colnum[0];
                               String cellData =  getCellFormatValue(row.getCell(j));
                               if(subjectName.length()==0 || subjectTypeName.length()==0){
                                   continue;
                               }
                               if(cellData.length()==0){
                                   cellData = "0";
                               }
                               Map<String,String> map = new HashMap<>();
                               map.put("sCode",sCode);
                               map.put("subjectName",subjectName);
                               map.put("tCode",tCode);
                               map.put("subjectTypeName",subjectTypeName);
                               map.put("companyId",String.valueOf(colnum[1]));
                               map.put("sDate",String.valueOf(colnum[2]));
                               map.put("subjectId",subjectId);
                               map.put("subjectTypeId",subjectTypeId);
                               map.put("reportVal", cellData);
                               result.add(map);
                            }
                        }else{
                            break;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

    public static String getCellFormatValue(Cell cell){
        String cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
                case Cell.CELL_TYPE_NUMERIC:{
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA:{
                    //判断cell是否为日期格式
                    if(DateUtil.isCellDateFormatted(cell)){
                        //转换为日期格式YYYY-mm-dd
                        //cellValue = cell.getDateCellValue();
                    }else{
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:{
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }
}
