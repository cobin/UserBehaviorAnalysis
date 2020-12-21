package com.behavior;

import static org.junit.Assert.assertTrue;

import com.behavior.mapper.mapper45.CallTask45Mapper;
import com.cobin.util.CDate;
import com.cobin.util.Tools;
import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.OpenJSSEPlatform;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String file = "zjw20200902.xls";
        file = "zjw2018.xls";
        FileInputStream fis = new FileInputStream(file);
        jxl.Workbook rwb = Workbook.getWorkbook(fis);
        StringBuilder sb = new StringBuilder();
        Sheet rs = rwb.getSheet(0);
        Cell[] dates = rs.getRow(1);
        Cell[] companys = rs.getRow(0);
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
                double v = 0;
                if(cells[k] instanceof NumberCell){
                    v = ((NumberCell)cells[k]).getValue();
                }
                if(v!=0) {
                    if (rowNumber % 1000 == 0) {
                        sb.append("\r\n").append(tabHead);
                    }

                    sb.append(rowNumber % 1000 > 0 ? "," : "").append("(").append(shead)
                            .append(",'").append(companys[k].getContents())
                            .append("','").append(dates[k].getContents())
                            .append("','").append(v)
                            .append("')");
                    rowNumber++;
                }
            }
            sb.append("\r\n");
        }
        fis.close();
        rwb.close();
        FileOutputStream outputStream = new FileOutputStream(String.format("out%s.sql",file.substring(0,file.length()-4)));
        outputStream.write(sb.toString().getBytes());
        outputStream.close();
    }


    @Test
    public void readUserExcel() throws Exception {
        String file = "lqy20201119.xls";
        FileInputStream fis = new FileInputStream(file);
        jxl.Workbook rwb = Workbook.getWorkbook(fis);
        StringBuilder sb = new StringBuilder();
        Sheet rs = rwb.getSheet(0);
         int rowNumber = 0;
        String tabHead = "INSERT INTO #tempNerveStore\n" +
                "\t(PersonName,UserCode,PersonId,PassWd)VALUES\r\n";

        for (int j = 1; j < rs.getRows(); j++) {
            Cell[] cells = rs.getRow(j);
            if (rowNumber % 1000 == 0) {
                sb.append("\r\n").append(tabHead);
            }
            sb.append(rowNumber % 1000 > 0 ? "," : "").append("(");
            for(int k=1;k<cells.length ;k++) {
                if(k>1) sb.append(",");
                sb.append("'").append(cells[k].getContents()).append("'");
            }
            sb.append(")");
            rowNumber++;
            sb.append("\r\n");
        }
        fis.close();
        rwb.close();
//        FileOutputStream outputStream = new FileOutputStream("out.sql");
//        outputStream.write(sb.toString().getBytes());
//        outputStream.close();
        System.out.println(sb.toString());
    }


    @Test
    public void readTxt(){
        //交易日期 营业部编号 营业部名称 客户编号 客户姓名 资金账号 P1 担保品结算基数 P2 信用结算基数 P3 客户资产规模 P4担保品佣金率 P5信用佣金率 担保品开户日期 信用开户日期 结算比例 担保品实际结算基数 信用实际结算基数 客户类型
        String[] fields = new String[]{"sdate","personId","personName","userId","userName","FoundAccount","p1","p2","p3","p4","p5","openGDate","openCDate","p6","p7","p8","remark"};

        //String[] fields = new String[]{"sdate","personId","personName","userId","userAddr","p1","p2","p3","lastDate","p4","p5","remark"};
        try {
//            String fileName = "C:\\Users\\think\\Documents\\WeChat Files\\cobinxp\\FileStorage\\File\\2020-09\\20200901_new.txt";
            String fileName = "rzrq_20201130_new.txt";
            String charset = charsetDetector(fileName,"GBK");//EncodingDetect.getJavaEncode(fileName);
            System.out.println(charset);
            BufferedReader reader = new BufferedReader(
                   new InputStreamReader (new FileInputStream(fileName),charset)
            );
            String s = null;
            int index = 0;
            long cur = System.currentTimeMillis();
            ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
            CallTask45Mapper ct45 = context.getBean(CallTask45Mapper.class);
            List<List<Map<Object,Object>>> traceList = new ArrayList<>();
            List<Map<Object,Object>> dataList = null;
            while((s =  reader.readLine())!=null) {
                if(index%1000==0){
                    if(traceList.size()>1){
//                        ct45.insertUserData(traceList);
                        ct45.insertOneDayUserData(traceList);
                        traceList.clear();
                        System.out.println(new Date()+">>增加到："+index);
                    }
                    dataList = new ArrayList<>();
                    traceList.add(dataList);
                }
                String[] ls = s.replaceAll("\"", "").split(",");
                Map<Object,Object> map = new HashMap<>();
                for(int i=0;i<ls.length;i++){
                    map.put(fields[i],ls[i]);
                }
                dataList.add(map);
                index++;
                //System.out.printf("%d.%s\r\n",index,Arrays.toString(ls));
                //Thread.sleep(1);
            }
            reader.close();
//            ct45.insertUserData(traceList);
            ct45.insertOneDayUserData(traceList);
            System.out.println(System.currentTimeMillis()-cur);
            System.out.println(traceList.size()+",,"+index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String charsetDetector(String fileName,String defCharset) throws java.io.IOException{
        byte[] buf = new byte[4096];
        java.io.FileInputStream fis = new java.io.FileInputStream(fileName);
        UniversalDetector detector = new UniversalDetector(null);
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding==null?defCharset:encoding;
    }

    /**
     * 判断文件的编码格式
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(File fileName) throws Exception{
        InputStream bin = new FileInputStream(fileName);
        int p = (bin.read() << 8) + bin.read();
        bin.close();
        String code = null;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        return code;
    }


    @Test
    public void queryIpAddr(){
        String url = "https://ipchaxun.com/%s/";
        try {
            Document doc = Jsoup.connect(String.format(url,"101.89.224.97")).get();
            //System.out.println(doc.html());
            Elements newsHeadlines = doc.select("div .info");
            for (Element headline : newsHeadlines) {
                System.out.println(headline);
                Elements vals = headline.select("span");
                for (Element val:vals){
                    if("value".equals(val.attr("class"))) {
                        System.out.println(val.text());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testIp(){
        int delay = 0;
        ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
        CallTask45Mapper ct45 = context.getBean(CallTask45Mapper.class);
        List<Map<Object, Object>> result = ct45.queryIpArea();
        System.out.println("已经存在："+result.size());
        List<String> qIps = getIps();
        System.out.println("需要抓取的地址:"+qIps.size());
        int catchId = 0;
        for (String ip : qIps) {
            catchId++;
            long ipInt = (ipToInt(ip));
            if(checkIp(ipInt,result)) {
                List<Map<Object, Object>> list = query138ip(ip);
                if (list != null) {
                    for(Map<Object, Object> map:list) {
                        ct45.insertIps(map);
                        result.add(map);
                        delay = 2000;
                    }
                }else{
                    delay+=60000;
                }
                System.out.println(catchId+". "+new Date()+",抓取:"+ip);
                try {
                    Thread.sleep(2000+(int)(Math.random()*3000)+delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println(catchId+". "+new Date()+",已经存在，不需要抓取:"+ip);
            }
        }
    }

    public boolean checkIp(long ipInt,List<Map<Object,Object>> result){
        for(Map<Object,Object> map:result){
            long sIp = (long) map.get("startIpInt");
            long eIp = (long) map.get("endIpInt");
            if(ipInt>=sIp && ipInt<=eIp){
                return false;
            }
        }
        return true;
    }

    @Test
    public void testIpInt(){
        long ipInt = ipToInt("175.19.4.0");
        System.out.println(ipInt);
        String ipAddr ="";
        for(int i=0;i<4;i++){
            long t = (long) Math.pow(256,3-i);
            long a = ipInt/t;
            if(i>0) ipAddr+=".";
            ipAddr+=a;
            ipInt %=t;
        }
        System.out.println(ipAddr);
    }

    public long ipToInt(String ip){
        String[] ips = ip.split("\\.");
        if(ips.length!=4) return 0;
        int[] ipFlag = new int[]{16777216,65536,256,1};
        long ret = 0;
        for(int i=0;i<ipFlag.length;i++){
            ret += Long.parseLong(ips[i].trim())*ipFlag[i];
        }
        return ret;
    }
    public List<String> getIps(){
        return getIps("ips.txt");
    }
    public List<String> getIps(String ipName){
        List<String> rets = new ArrayList<>();
        try (BufferedReader reader  = new BufferedReader(
                    new InputStreamReader (new FileInputStream(ipName),"UTF-8")
            )){
            String line = null;
            while ((line = reader.readLine())!=null){
                if(line.length()>6 && line.contains(".")) {
                    rets.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rets;
    }

    public List<Map<Object,Object>> query138ip(String ipAddr){
        String url = "https://www.ip138.com/iplookup.asp?ip=%s&action=2";
        try {//59.144.48.34  101.89.224.97 85.171.52.251
            Connection connect = Jsoup.connect(String.format(url,ipAddr));
//            Map header = new HashMap();
//            header.put("Host", "https://www.ip138.com");
//            header.put("User-Agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
//            header.put("Accept", "  text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            header.put("Accept-Language", "zh-cn,zh;q=0.5");
//            header.put("Accept-Charset", "  GB2312,utf-8;q=0.7,*;q=0.7");
//            header.put("Connection", "keep-alive");
//            Connection data = connect.headers(header);
            Document doc = connect.get();
            Elements newsHeadlines = doc.getElementsByTag("script");
            for (Element headline : newsHeadlines) {
                if(headline.childNodeSize()>0) {
                    String val =  ((DataNode)headline.childNode(0)).getWholeData().replaceAll("[\r\n\t]","");
                    if(val.contains("ip_result")) {
                        val = val.substring(val.indexOf("ip_result"));
                        val = (val.substring(val.indexOf("{"), val.indexOf(";")));
                        JSONObject json = new JSONObject(val);
                        JSONArray ipList = json.getJSONArray("ip_c_list");
                        List<Map<Object, Object>> maps = new ArrayList<>();
                        for (int i = 0; i < ipList.length(); i++) {
                            JSONObject ip_c_list = ipList.getJSONObject(0);
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

    @Test
    public void test138(){
        String[] ip = "60.191.11.251:3128".split(":");
        try {
            Document doc = Jsoup.connect("https://202020.ip138.com/")
                    .proxy(ip[0],Integer.parseInt(ip[1]))
                    .timeout(10000)
                    .userAgent("Mozilla").get();
            System.out.println(doc.text());
        } catch (IOException e) {
            e.printStackTrace();
        }
//       List<Map<Object,Object>> result = query138ip("112.49.80.203");
//       System.out.println(result);

    }

    public String sendHttpResponse(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendHttpResponse1(String url){
        try {
            StringBuffer buffer = new StringBuffer();
            URL urls = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(urls.openStream()));
            String line = null;
            while((line=reader.readLine())!=null) {
                buffer.append(line);
            }
            reader.close();
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Test
    public void testIpblock(){
        ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
        CallTask45Mapper ct45 = context.getBean(CallTask45Mapper.class);
        try {
            Document doc = Jsoup.connect("http://ipblock.chacuo.net/list").get();
            Elements elements = doc.getElementsByTag("table");
            for(Element table:elements){
                Elements trs = table.getElementsByTag("tr");
                for(Element tr:trs){
                    Elements tds = tr.getElementsByTag("td");
                    if(tds.size()==5 && Tools.getInt(tds.get(0).text())>0) {
                       System.out.println(tds.text());
                       String ipUrl = "http://ipblock.chacuo.net/down/t_txt=c_%s";
                       Document docIps = Jsoup.connect(String.format(ipUrl,tds.get(1).text())).get();
                       String[] sVal = docIps.text().split("\r\n");
                       for(String ipVals:sVal){
                           String[] ips = ipVals.split("\t");
                           if(ips.length==4) {
                               Map<Object, Object> map = new HashMap<>();
                               map.put("startIp", ips[0]);
                               map.put("endIp", ips[1]);
                               map.put("startIpInt", ipToInt(ips[0]));
                               map.put("endIpInt", ipToInt(ips[1]));
                               map.put("regionName", tds.get(2).text());
                               ct45.insertChacuoIps(map);
                           }else{
                               System.out.println(ipVals);
                           }
                       }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadIps(){
        String countryId = "KP";
        String country = "朝鲜";
        ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
        CallTask45Mapper ct45 = context.getBean(CallTask45Mapper.class);
        try {
            String ipUrl = "http://ipblock.chacuo.net/down/t_txt=c_%s";
            Document docIps = Jsoup.connect(String.format(ipUrl, countryId)).get();
            String[] sVal = docIps.text().split("\r\n");
            int rowCount = 0;
            for (String ipVals : sVal) {
                String[] ips = ipVals.split("\t");
                if (ips.length == 4) {
                    Map<Object, Object> map = new HashMap<>();
                    map.put("startIp", ips[0]);
                    map.put("endIp", ips[1]);
                    map.put("startIpInt", ipToInt(ips[0]));
                    map.put("endIpInt", ipToInt(ips[1]));
                    map.put("regionName", country);
                    ct45.insertChacuoIps(map);
                    rowCount++;
                } else {
                    System.out.println(ipVals);
                }
            }
            System.out.println("总数:"+rowCount);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void loadQueryIpAddr(){
        ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
        CallTask45Mapper ct45 = context.getBean(CallTask45Mapper.class);
        List<String> ipAddrs = getIps("ip202004.txt");
        List<List<Map<Object,Object>>> result = new ArrayList<>();
        List<Map<Object,Object>> data  = null;
        int allCount = ipAddrs.size();
        int index = 0;
        for(String ip:ipAddrs){
            if(index%1000==0){
                if(result.size()>80){
                    ct45.insertIpAddress(result);
                    result.clear();
                    System.out.println(new Date()+">>导入到:"+index+"("+allCount+")");
                }
                data = new ArrayList<>();
                result.add(data);
            }
            Map<Object,Object> map = new HashMap<>();
            map.put("ipAddr",ip);
            map.put("ip2Int",ipToInt(ip));
            data.add(map);
            index++;
        }
        ct45.insertIpAddress(result);
        System.out.println("导入完毕:"+ipAddrs.size());
    }

    @Test
    public void testTask(){
        ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
        CallTask45Mapper ct45 = context.getBean(CallTask45Mapper.class);
        // 2004504 2035669
        int[] opIds = {2004504 , 2035669};
        for(int opId :opIds) {
            System.out.println(">>>>"+opId);
            List<List<Map<Object, Object>>> result = ct45.getTaskEveryDay(opId);
            for (List<Map<Object, Object>> r : result)
                System.out.println(r);
        }
    }

    @Test
    public void testLocalDate(){
        String startDate = LocalDate.now().plusMonths(-1).format(DateTimeFormatter.BASIC_ISO_DATE);
        System.out.println(startDate);
    }
}
