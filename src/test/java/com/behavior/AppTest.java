package com.behavior;

import static org.junit.Assert.assertTrue;

import com.cobin.util.Tools;
import org.junit.Test;

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
}
