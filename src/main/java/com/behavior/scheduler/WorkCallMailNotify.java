package com.behavior.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.behavior.BehaviorMain;
import com.cobin.util.Tools;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class WorkCallMailNotify extends WorkJob {
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
		try {		
			getMail(bm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@SuppressWarnings("restriction")
	public void getMail(BehaviorMain bm) throws MessagingException, IOException{
//		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//	    String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		Properties props = new Properties();  
//        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
//        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", "110");
//        props.setProperty("mail.pop3.socketFactory.port", "995");
        // 以下步骤跟一般的JavaMail操作相同
        Session session = Session.getDefaultInstance(props, null);

        // 请将红色部分对应替换成你的邮箱帐号和密码
        URLName urln = new URLName("pop3", "pop.qq.com", 110, null,
        		 "1483471604@qq.com", bm.getConfig("mail.code") );
        Store store = session.getStore(urln);
        log.debug("登录邮箱...");
        store.connect();
        
//		//存储接收邮件服务器使用的协议，这里以POP3为例  
//		props.setProperty("mail.store.protocol", "pop3");  
//		//设置接收邮件服务器的地址，这里还是以网易163为例  
//		props.setProperty("mail.pop3.host", "pop.qq.com");  
//		MailSSLSocketFactory sf = null;  
//		try {  
//			sf = new MailSSLSocketFactory();  
//			sf.setTrustAllHosts(true);  
//		} catch (GeneralSecurityException e) {  
//			e.printStackTrace();  
//		}  
//		props.put("mail.pop3.ssl.socketFactory", sf);  
//		//根据属性新建一个邮件会话.  
//		Session session=Session.getInstance(props);  
//		//从会话对象中获得POP3协议的Store对象  
//		Store store = session.getStore("pop3");  
//		//如果需要查看接收邮件的详细信息，需要设置Debug标志  
//		session.setDebug(false);  
//		//连接邮件服务器  
//		store.connect("pop.qq.com", "1483471604@qq.com", "123WUJIAMEI123");//woplicysmzjlhcbj
        
        //创建搜索条件  
//        SearchTerm st=new OrTerm (new FromStringTerm("22830507@qq.com"), new FromStringTerm("22830507@qq.com"));  
        //创建搜索条件  
        Date d=new Date();
        d.setTime(d.getTime()-2*24*60*60*1000);
        
        //951874227@qq.com  22830507@qq.com new FromStringTerm("951874227@qq.com"),new SentDateTerm(ComparisonTerm.GE,d),new SentDateTerm(ComparisonTerm.LE ,new Date())
        SearchTerm st=new AndTerm(new FromStringTerm(bm.getConfig("mail.from")),new SentDateTerm(ComparisonTerm.GE,d));  
		//获取邮件服务器的收件箱  
        log.debug("进入收件邮箱.");
		Folder folder = store.getFolder("INBOX");  
		//以只读权限打开收件箱  
		folder.open(Folder.READ_ONLY);  
		log.debug("收件箱邮件："+folder.getMessageCount());
//		Message _messages[] = folder.getMessages();  
//		for(Message msg : _messages ) {
//			log.debug(msg.getFrom()[0]);
//		}
		log.debug("过滤发件人为:"+bm.getConfig("mail.from"));		
		Message messages[] = folder.search(st);
		log.debug("收到今天对方发送的邮件>>"+messages.length);
		for (Message message : messages) {
			// 获取邮件具体信息
			System.out.println(message.getSubject());
			System.out.println(message.getSentDate());
			Object obj = message.getContent();
			if (obj instanceof Multipart) {
				Multipart mp = (Multipart) obj;
				for (int i = 0, n = mp.getCount(); i < n; i++) {
					Part part = mp.getBodyPart(i);
					String disposition = part.getDisposition();
					if ((disposition != null)
							&& (disposition.equals(Part.ATTACHMENT) || (disposition.equals(Part.INLINE)))) {
						String pName = (new String(Tools.base64Decode(part.getFileName().replaceFirst("=\\?gb18030\\?B\\?", "")),"GBK"));
						boolean save=saveFile(pName, part.getInputStream());
						if(save){
							uploadExcel(pName);
						}
					}
				}
			}
		}
		//关闭连接  
		folder.close(false);  
		store.close(); 
	}
	
	public boolean saveFile(String pName, InputStream in){
		log.debug("附件文件名为："+pName);
		File dir = new File("mail");
		if(!dir.exists()){
			dir.mkdirs();
		}
		File f = new File(dir,pName);
		if(!f.exists()){
			try {
				ByteArrayOutputStream aout = new ByteArrayOutputStream();
				byte[] b=new byte[5*1024];
				int len = 0;
				while((len=in.read(b))!=-1){
					aout.write(b,0,len);
				}
				in.close();
				FileOutputStream out = new FileOutputStream(f);
				out.write(aout.toByteArray());
				out.close();
				return true;
			} catch (Exception e) {
				log.error(e);
			}
		}else{
			log.debug("文件已经下载过.");
		}
		return false;
	}
	
	public boolean uploadExcel(String fileName) {		
		File fup = new File("mail/"+fileName);
		if(fup.exists()){		
			String user = "brokerage";// SSH连接用户名
			String password = "Wjm_123456";// SSH连接密码
			String host = "172.17.161.75";// SSH服务器
			int port = 22;// SSH访问端口
			com.jcraft.jsch.Session session = null;		
			try {
				JSch jsch = new JSch();			
				session = jsch.getSession(user, host, port);
				session.setPassword(password);
				session.setConfig("StrictHostKeyChecking", "no");			
				log.debug("登录远程服务器:"+host+"...");
	//			log.debug(session.getServerVersion());// 这里打印SSH服务器版本信息	
				session.connect();
				sftp(session,fup);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			} finally {
				if(session!=null){
					session.disconnect();
				}
			}
			log.debug("退出远程服务器:"+host);
		}
		return false;
	}
	
	public void sftp(com.jcraft.jsch.Session session,File file) throws JSchException, SftpException, IOException{
		log.debug("启动sftp上载文件...");		
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp  sftp = (ChannelSftp) channel;
		sftp.lcd("mail/");
		sftp.cd("/search/ChangJiangImportWeb/Txt/");
		log.debug("正在上传文件....");
		InputStream inputStream = new FileInputStream(file);
		sftp.put(inputStream,file.getName());
		inputStream.close();
		sftp.disconnect();
	}
	
	public void exec(com.jcraft.jsch.Session session) throws JSchException, IOException{
		ChannelExec exec= (ChannelExec)session.openChannel("exec");			
		exec.setCommand("ps");
		exec.setInputStream( null );  
		exec.setErrStream(System.err);
		exec.connect(3000);	
		InputStream in = exec.getInputStream();
		int res = -1;
		byte[] b = new byte[5*1024];
		StringBuffer buf = new StringBuffer( 1024 ); 			
        while ( in.available() > 0 ) {  
            int i = in.read( b, 0, 1024 );  
            if ( i < 0 ) break;  
            buf.append( new String( b, 0, i ) );  
        }  
        if ( exec.isClosed() ) {  
            res = exec.getExitStatus();  
            System.out.println( String.format( "Exit-status: %d", res ) );                 
        }  
        System.out.println( buf.toString() );  
        exec.disconnect(); 
	}
	
	
	public void shell(com.jcraft.jsch.Session session) throws JSchException, IOException, InterruptedException {
		ChannelShell shell= (ChannelShell)session.openChannel("shell");
		PipedInputStream pipeIn = new PipedInputStream();  
        PipedOutputStream pipeOut = new PipedOutputStream( pipeIn );   
        shell.setInputStream( pipeIn );  
        shell.setOutputStream( System.out );  
		shell.connect(3000);
		pipeOut.write("ps -ef".getBytes());
		Thread.sleep( 3000 );  
        pipeOut.close();  
        pipeIn.close();   
        shell.disconnect(); 
	}
	
	public static void main(String[] args) {
		BehaviorMain behaviorMain = new BehaviorMain(0, null);
		behaviorMain.reloadConfig();
		WorkCallMailNotify mailNotify = new WorkCallMailNotify();
		mailNotify.execWork(behaviorMain, null);
	}
}
