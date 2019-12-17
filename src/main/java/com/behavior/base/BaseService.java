package com.behavior.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author think
 */
public abstract class BaseService {
	protected Log log = LogFactory.getLog(getClass());

	public BaseService() {
		reloadConfig();
	}

	public void reloadConfig() {
		Reader reader = null ;
		try {
			log.info("加载配置文件...");
			properties = new Properties();
			File f = new File("config/conf.ini");
			if (f.exists()) {
				reader = new InputStreamReader(new java.io.FileInputStream(f), "UTF-8");
				properties.load(reader);
			}
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} finally {
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		initLoadConfig();
	}

	public int getConfig(String key, int defVal) {
		if (properties.containsKey(key)) {
			try {
				return Integer.parseInt(properties.getProperty(key));
			} catch (Exception e) {

			}
		}
		return defVal;
	}

	public long getConfig(String key, long defVal) {
		if (properties.containsKey(key)) {
			try {
				return Long.parseLong(properties.getProperty(key));
			} catch (Exception e) {
			}
		}
		return defVal;
	}

	public String getConfig(String key){
		return getConfig(key,null);
	}

	public String getConfig(String key, String defVal) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		return defVal;
	}

	public Properties getConfig() {
		return this.properties;
	}

	public String getConfigVals() {
		return properties.toString();
	}
	public abstract void initLoadConfig();

	private Properties properties;
}
