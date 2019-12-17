package com.behavior.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper111.CallTask111Mapper;
import com.cobin.io.BufferedReader;
import com.cobin.util.CDate;
import com.cobin.util.Tools;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @author Cobin
 */
public class WorkCallApacheLogNotify extends WorkJob {
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
	
	
	public boolean downApacheLog(String[] fNames,String sDate) {
		// SSH连接用户名
		String user = "dataReader1";
		// SSH连接密码
		String password = "dataReader123";
		// SSH服务器
		String host = "172.17.161.75";
		// SSH访问端口
		int port = 22;
		Session session = null;
		
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");			
			session.connect();	
			log.debug("登录远程服务器:"+host+",获取日志...");
//			log.debug(session.getServerVersion());// 这里打印SSH服务器版本信息			
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp  sftp = (ChannelSftp) channel;
			sftp.cd("/var/log/web/cweb/log");
//			String[] fNames={"firstin","dounlock","unlock","lockstate"};
			File fDir = new File("./apacheLog");
			if(!fDir.exists()){
				fDir.mkdirs();
			}
			for(String _fName:fNames){
				String fName = _fName+"_"+sDate;
				File file = new File( fDir,fName);
				log.debug("下载："+fName);
				sftp.get(fName, new FileOutputStream(file));
			}
			sftp.quit();
			channel.disconnect();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			if(session!=null){
				session.disconnect();
			}
		}
		return false;
	}

	@Override
	public void execWork(BehaviorMain bm,String sdate ){
		CDate cDate  = new CDate();
		cDate.addDate(-1);
		String sDate =  cDate.getShortDate();
		File fexit = new File("apacheLog/unlock_"+sDate);
		if(fexit.exists()){
			log.debug("日志已经处理过，不重复处理。");
			return;
		}
		
		CallTask111Mapper ct111 = bm.getMapper(CallTask111Mapper.class);
		//"firstin","lockstate","dounlock",
		String[] sfromTypes = new String[]{"firstin","lockstate","dounlock","unlock"};
		
//		while(cDate.getTime()<System.currentTimeMillis()-24*60*60*1000){		
//		String sDate =  CDate.addShortCDate(-1);
//		sDate = "20170413" ;
		downApacheLog(sfromTypes,sDate);				
		for(String sfromType:sfromTypes){
			try {
				parseApacheLog(sfromType,sDate,ct111);			
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		}
//			cDate.addDate(1);
//		}
	}
	/**
	 *  刘青松 [13:29:00]:
		  139    /var/log/httpd/cweb/20161227*
		刘青松 [13:29:25]:
		  unlock  显示解锁界面
		  {gid=13, productid=5356, pcode=qy, channel=49, cks=3304744d622cb9168369b900fd30b701, 
		  pid=5356, gs=0, ?action=unlock, osver=5.1.2600, cam=0, 
		  productlist=5539,5358,5356,5315,5214,5148,5048, 
		  listchk=1411b2240e94b46492103f86f98e45aa, 
		  uid=195210938, rd=15c9dcec, softver=1612240201
		  , function=hy, 
		  personid=195210938, user=195210938, 
		  md5=60a29d98262e326e714b38778a82ae6c, 
		  ts=1482768172}
		  
		刘青松 [13:29:33]:
		  dounlock  执行解锁功能
		刘青松 [13:30:01]:
		  lockstate/firstin  为显示权限窗口 
		  
		以后会有个from参数，from=cover就是首页，lockstate是权限查询，firstin是首次弹出

		echo "password" | sudo -S netstat -tlnp
	 */
	public boolean parseApacheLog(String sfromType,String sDate,CallTask111Mapper ct111) throws Exception {
		String fileName = "apacheLog/"+sfromType+"_"+sDate;
		File f = new File(fileName); 
		if(!f.exists()){
			return false;
		}
		BufferedReader br = new BufferedReader(f);
		String line ;
		//List<Map<String,String>> list = new ArrayList<>();
		int readSize = 0;
		int nFromType = getFromType(sfromType);
		while((line=br.readLine())!=null){			
			String[] sV = line.substring(line.indexOf("?")+1).split("&");
			Map<String,Object> mV = new HashMap<>();
			for(String s:sV){
				String[] svv = s.replaceAll("[ \"](.)+", "").split("=");
				if(svv.length>1){
					try{
						mV.put(svv[0], java.net.URLDecoder.decode(svv[svv.length-1],"GBK"));
					}catch(Exception ex){
						log.debug(ex);
					}
				}
			}	
			String action = (String)mV.get("action");
			String from = (String)mV.get("from");
			if(from!=null){
				action +="|"+from;
				mV.put("action", action);
			}
			mV.put("fromType",nFromType);
			mV.put("sdate",sDate);
			mV.put("from", getFromType(from));
			readSize++;
			Object pId = mV.get("personid");
			if(pId==null || "".equals(pId) || Tools.getInt(pId.toString())<0){
				continue;
			}
			ct111.insertApacheLog(mV);

		} 
		log.debug(String.format("%s,读取总行数:%d",fileName,readSize));
		return true;
	}
	
	private int getFromType(String sFromType){
		if(sFromType==null){
			return 0;
		}
		if("dounlock".equals(sFromType)){
			return 10;
		}else if("unlock".equals(sFromType)){
			return 30;
		}else if(sFromType.startsWith("firstin")){
			return 80;
		}else if("lockstate".equals(sFromType)){
			return 81;
		}else if("cover".equals(sFromType)){
			//首页
			return 11;
		}
		return 999999;
	}
}
