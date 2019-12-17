package com.behavior.base;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseService {
	protected Log log = LogFactory.getLog(getClass());

	public BaseService() {
		log.debug("读取配置文件.");
		properties = new Properties();
		File f = new File("config/conf.ini");
		if (f.exists()) {
			try {
				properties.load(new InputStreamReader(new java.io.FileInputStream(f), "UTF-8"));
			} catch (Exception e) {
				log.error(e);
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

	public String getConfig(String key, String defVal) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		return defVal;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public abstract void initLoadConfig();

	private Properties properties;
}
