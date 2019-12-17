package com.behavior.base;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author think
 */
public abstract class BaseImpService extends BaseService {

	public void loadContext() {
		if (context == null) {
			log.info("加载数据库 ...");
			context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
		}
	}
	public <T> T getMapper(Class<T> paramClass) {
		return getMapper(context, paramClass);
	}

	public <T> T getMapper(ApplicationContext context, Class<T> paramClass) {
		return context.getBean(paramClass);
	}
	private ApplicationContext context;

}
