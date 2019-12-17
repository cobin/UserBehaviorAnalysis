package com.behavior;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.behavior.base.BaseImpService;
import com.behavior.scheduler.WorkJob;
import com.cobin.util.Tools;

public class BehaviorMain extends BaseImpService implements Runnable{
	private int once ;
	private String clazz;
	public static void main(String[] args) {
		int once = 0;
		String clazz = null;
		for(String arg:args){
			if(arg.startsWith("-once=")){
				once =  Tools.getInt(arg.substring(6).trim()); 
			}else if(arg.startsWith("WorkCall")) {
				once = 1;
				clazz = arg;
			}
		}
		new Thread(new BehaviorMain(once,clazz), "BehaviorMain").start();
	}
	public BehaviorMain(int once,String clazz) {
		this.once = once;
		this.clazz = clazz;
	}
	@Override
	public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				if (scheduler != null) {
					scheduler.shutdown();
				}
			} catch (SchedulerException e) {
			}
			BehaviorServer.stopServer();
			log.info("系统退出.");
		}));
		
		loadContext();
		if(once==1){
			if(clazz!=null) {
				String[] clas = clazz.split("\\|");
				for(String cla:clas) {
					try {
						log.debug("执行>>"+cla);
						Class<?> cl = Class.forName("com.behavior.scheduler."+cla.trim());
						WorkJob job = (WorkJob)cl.newInstance();
						job.execWork(this, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			log.info(Tools.getRuntimeMemory());
			startScheduler();
			BehaviorServer.startServer(this);
			while (true) {
				try {
					Thread.sleep(nWait * 60 * 1000);
					// log.debug("系统正在运行中")
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void initLoadConfig() {
		// 分钟
		nWait = getConfig("web.cycle.wait", 1);
	}

	protected void startScheduler(){
		try {
			System.getProperties().put("org.quartz.properties", "./config/quartz.properties");
			System.getProperties().put("org.quartz.plugin.jobInitializer.fileNames", "./config/quartz_data.xml");
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.getContext().put("context", this);
			scheduler.start();
			log.info("quartz定时器已经起启动.");
		} catch (SchedulerException e) {
			log.error("quartz启动异常!");
		}
	} 
	/** 轮询时间 **/
	private int nWait;
	private Scheduler scheduler = null;
}

