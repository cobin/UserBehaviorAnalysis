package com.behavior.scheduler;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper45.CallTask45Mapper;
import com.behavior.mapper.mapper9106.CallTask9106Mapper;
import com.cobin.util.CDate;
import com.cobin.util.Tools;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cobin(Cobin)
 * @desc TODO
 * @Date 2020/9/25 16:35
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WorkCallLoadIpInfoNotify extends WorkJob {
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            BehaviorMain bm = (BehaviorMain) arg0.getScheduler().getContext().get("context");
            execWork(bm, null);
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    @Override
    public void execWork(BehaviorMain bm, String qDate) {
        int delay = 0;
        CallTask9106Mapper ct910 = bm.getMapper(CallTask9106Mapper.class);
        CallTask45Mapper ct45 = bm.getMapper(CallTask45Mapper.class);
        List<Map<Object, Object>> result = ct45.queryIpArea();
        log.info("已经存在："+result.size());
        //addResult(null,result);
        int doCount = 0;
        int startDate = Tools.getInt(LocalDate.now().plusMonths(-1).format(DateTimeFormatter.BASIC_ISO_DATE));
        int endDate = Tools.getInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        for(int page=1;page<2;page++) {
            List<Long> qIps = ct910.getActiveDatePeerIp(startDate,endDate);
            log.info("需要抓取的地址:" + qIps.size());
            int catchId = (page-1)*100000;
            long ltime = System.currentTimeMillis();
            int fromIndex = 1;
            for (Long ipInt : qIps) {
                catchId++;
                String ip =  int2Ip(ipInt);
                int retCheck = checkIp(ipInt, result,fromIndex);
                fromIndex = Math.abs(retCheck);
                if (retCheck>=0) {
                    List<Map<Object, Object>> list = query138ip(ip);
                    if (list != null) {
                        doCount++;
                        String errMsg = "";
                        for (Map<Object, Object> map : list) {
                            map.put("opId",2020050);
                            ct45.insertIps(map);
                            addResult(map,result);
                            delay = 1000;
                            errMsg = (String)map.get("errMsg");
                        }
                        log.info(catchId + ".("+doCount+") 已抓:" + ip +"(" + list.size()+"),"+(catchId*100/qIps.size())+"% ,"+errMsg);
                    } else {
                        delay += 60000;
                    }
                    try {
                        Thread.sleep(2000 + (int) (Math.random() * 3000) + delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(catchId%100000==0){
                    log.info("寻址:"+fromIndex+",10W耗时："+(System.currentTimeMillis()-ltime));
                    ltime = System.currentTimeMillis();
                }
            }
        }
    }

    public int checkIp(long ipInt,List<Map<Object,Object>> result,int fromIndex){
        int sizeCount = result.size();
        int startIndex = Math.max(fromIndex-1,0);
        for(int i=startIndex;i<sizeCount;i++){
            Map<Object,Object> map = result.get(i);
            long sIp = (long) map.get("startIpInt");
            long eIp = (long) map.get("endIpInt");
            if(ipInt>=sIp && ipInt<=eIp){
                return -(i+1);
            }else if(ipInt<sIp){
                return i;
            }
        }
        return fromIndex;
    }

    public void addResult(Map<Object,Object> map,List<Map<Object,Object>> result){
        if(map!=null) {
            result.add(map);
        }
        Collections.sort(result, new Comparator<Map<Object, Object>>() {
            @Override
            public int compare(Map<Object, Object> o1, Map<Object, Object> o2) {
                return (long)o1.get("startIpInt")>=(long)o2.get("startIpInt")?1:-1;
            }
        });
    }

    public String int2Ip(long ipInt){
        String ipAddr ="";
        for(int i=0;i<4;i++){
            long t = (long) Math.pow(256,3-i);
            long a = ipInt/t;
            if(i>0){
                ipAddr+=".";
            }
            ipAddr+=a;
            ipInt %=t;
        }
        return ipAddr;
    }
    public long ipToInt(String ip){
        String[] ips = ip.split("\\.");
        if(ips.length!=4){
            return 0;
        }
        int[] ipFlag = new int[]{16777216,65536,256,1};
        long ret = 0;
        for(int i=0;i<ipFlag.length;i++){
            ret += Long.parseLong(ips[i].trim())*ipFlag[i];
        }
        return ret;
    }

    public List<Map<Object,Object>> query138ip(String ipAddr){
        String url = "https://www.ip138.com/iplookup.asp?ip=%s&action=2";
        try {
            Connection connect = Jsoup.connect(String.format(url,ipAddr));
            Document doc = connect.get();
            Elements newsHeadlines = doc.getElementsByTag("script");
            for (Element headline : newsHeadlines) {
                if(headline.childNodeSize()>0) {
                    String val =  ((DataNode)headline.childNode(0)).getWholeData().replaceAll("[\r\n\t]","");
                    if(val.contains("ip_result")) {
                        val = val.substring(val.indexOf("ip_result"));
                        val = (val.substring(val.indexOf("{"),val.indexOf(";")));
                        JSONObject json = new JSONObject(val);
                        JSONArray ipList = json.getJSONArray("ip_c_list");
                        List<Map<Object,Object>> maps = new ArrayList<>();
                        for(int i=0;i<ipList.length();i++) {
                            JSONObject ip_c_list = ipList.getJSONObject(i);
                            String[] ips = json.getString("iP段").split("-");
                            Map<Object, Object> map = new HashMap<>();
                            long startIpInt = ip_c_list.getLong("begin");
                            long endIpInt = ip_c_list.getLong("end");
                            map.put("startIp", int2Ip(startIpInt));
                            map.put("endIp", int2Ip(endIpInt));
                            map.put("startIpInt", startIpInt);
                            map.put("endIpInt", endIpInt);
                            map.put("regionName", ip_c_list.getString("ct"));
                            map.put("province", ip_c_list.getString("prov"));
                            map.put("remark",
                                    String.format("%s,%s,%s"
                                            , json.optString("ASN归属地")
                                            , json.optString("兼容IPv6地址")
                                            , json.optString("映射IPv6地址")
                                    )
                            );
                            maps.add(map);
                        }
                        return maps;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
