package com.behavior;

import static org.junit.Assert.assertTrue;

import com.cobin.util.Tools;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testMe(){
        System.out.println("Cashew");
    }

    @Test
    public void testXX(){
        String[] strVs= Tools.regExPickUp("退货产生  原始订单 4046666 冲销订单 4048229 原始资金号 728143新产生的资金号 728809","原始资金号[^\\d]{0,2}([\\d]+)");
        strVs = Tools.regExPickUp("退款-对应流水[124194]","退款[^\\d]{4,10}\\[([\\d]+)\\]");
        for(String v:strVs)
            System.out.println(v);
    }

    @Test
    public void testArray(){
        List<Integer> testList = new ArrayList<>();
        for(int i = 100;i<(50000*Math.random());i++){
            testList.add(i);
        }
        System.out.println(testList.size());
        for(int i=0;i<testList.size()/100+1;i++){
            int endIndex = Math.min((i+1)*100,testList.size());
            System.out.println(testList.subList(i*100,endIndex));
        }
    }

    @Test
    public void readExcel() throws Exception {
        String file = "zjw20200722.xls";
        FileInputStream fis = new FileInputStream(file);
        jxl.Workbook rwb = Workbook.getWorkbook(fis);
        StringBuilder sb = new StringBuilder();
        Sheet rs = rwb.getSheet(0);
        Cell[] dates = rs.getRow(0);
        Cell[] companys = rs.getRow(1);
        int rowNumber = 0;
        String tabHead = "INSERT INTO #finaceStatement(sCode,    sName,    tCode,    tName,    companyName,    sdate,    reportVal)VALUES\r\n";
        for (int j = 2; j < rs.getRows(); j++) {
            Cell[] cells = rs.getRow(j);
            String shead = "";
            for(int k=0;k<4;k++){
                shead +=(k==0?"'":",'")+cells[k].getContents()+"'";
            }
            for(int k=4;k<cells.length && k<companys.length && k<dates.length;k++) {
//                if(Float.parseFloat(dates[k].getContents())<2020.04){
//                    continue;
//                }
                if(rowNumber%1000==0){
                    sb.append("\r\n").append(tabHead);
                }
                sb.append(rowNumber%1000>0?",":"").append("(").append(shead)
                        .append(",'").append(companys[k].getContents())
                        .append("','").append(dates[k].getContents())
                        .append("','").append(cells[k].getContents())
                        .append("')");
                rowNumber++;
            }
            sb.append("\r\n");
        }
        fis.close();
        rwb.close();
        System.out.println(sb.toString());
    }
}
