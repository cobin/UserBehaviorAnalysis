package com.behavior;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.behavior.scheduler.WorkJob;
import com.cobin.util.BaseObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


/**
 * @author Cobin
 */
@SuppressWarnings("restriction")
public class BehaviorServer extends BaseObject {
	private HttpServer hs = null;
	private BehaviorMain behavior;
	private ExecutorService singleThreadPool;
	public BehaviorServer(BehaviorMain behavior) {
		this.behavior = behavior;
	}
	private void createServer() {
		try{
			ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
					.setNameFormat("web-pool-%d").build();
			singleThreadPool = new ThreadPoolExecutor(1, 1,
					0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

			log.info("监控服务启动...(29999)");
			// 设置HttpServer的端口为80
			hs = HttpServer.create(new InetSocketAddress(29999),0);
			// 用MyHandler类内处理到/的请求
			hs.createContext("/bm", new MyHandler());
			// creates a default executor
			hs.setExecutor(null);
			hs.start(); 
		}catch(Exception ioe){
			log.error(ioe);
		}
		hs = null;
	}
	
	private void stop(){
		if(hs!=null){
			hs.stop(0);
			singleThreadPool.shutdown();
		}
	}

	class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException{
//			InputStream is = t.getRequestBody();  
//			ByteArrayOutputStream in=new ByteArrayOutputStream();
//			byte[] bytes=new byte[1024];
//			while(is.available()>0){
//				int i =is.read(bytes);
//				if(i==-1){
//					break;
//				}
//				in.write(bytes,0,i);
//			}
//			String reqVal = new String(in.toByteArray());			
			//log.debug(reqVal);

			JSONObject json = new JSONObject();
			try {
				json.put("success", true);
				json.put("msg", "OK");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String reqQuery =t.getRequestURI().toString();
						
			log.info(reqQuery);
			
			if(reqQuery.contains("reloadConfig")) {
				behavior.reloadConfig();
				Properties p=behavior.getConfig();
				for (Entry<Object, Object> entry : p.entrySet()) {
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					try {
						json.put(key, value.split(","));
					} catch (JSONException e) {
					}
				}
//				String sVs = behavior.getConfigVals();
//				response = "{\"success\":\"true\",\"msg\":\""+Tools.urlEncode(sVs)+"\"}";
			}else if(reqQuery.contains("WorkCall")) {
				final String cla = reqQuery.substring(reqQuery.indexOf("WorkCall"));
				singleThreadPool.execute(()-> {
					try {
						log.info("执行>>"+cla);
						Class<?> cl = Class.forName("com.behavior.scheduler."+cla.trim());
						WorkJob job = (WorkJob)cl.newInstance();
						job.execWork(behavior, null);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				});
			}
			
			String response = json.toString();
			
			t.getResponseHeaders().add("content-type","application:json;charset=utf8");  
			t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			t.getResponseHeaders().add("Access-Control-Allow-Methods","POST");  
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();   
			os.write(response.getBytes());   
			os.close();
//			
//			if(reqVal.length()>2){
//				
//			}
		}	
		
	}  
	public static void startServer(BehaviorMain bm) {
		cHttpServer = new BehaviorServer(bm);
		cHttpServer.createServer();
	}
	public static void stopServer() {
		if(cHttpServer!=null) {
			cHttpServer.stop();
		}
	}
	private static BehaviorServer cHttpServer;
}
