package com.behavior.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.cobin.util.Tools;

/**
 * @author Cobin
 */
public abstract class BaseImpService extends BaseService {
	private Properties config;
	public void loadContext() {
		if (context == null) {
			log.debug("加载数据库 ...");
			context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
		}
		reloadConfig();
	}
	
	public void reloadConfig() {
		try {
			config = new Properties();
			config.load(new FileInputStream("config/conf.ini"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getConfigVals() {
		return config.toString();
	}
	
	public Properties getConfig() {
		return config;
	}

	public <T> T getMapper(Class<T> paramClass) {
		return getMapper(context, paramClass);
	}

	public <T> T getMapper(ApplicationContext context, Class<T> paramClass) {
		return context.getBean(paramClass);
	}
	private ApplicationContext context;

	public String getConfig(String key){
		return config.getProperty(key);
	}
	public int getConfigInt(String key) {
		return Tools.getInt(getConfig(key));
	}
}
