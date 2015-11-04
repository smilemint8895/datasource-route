/**
* Copyright © 2015 meilishuo.com All rights reserved.
*/
package org.lumint.spring.datasource.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericApplicationContext;

/**  
 * @Title: Configs.java

 * @Prject: datasource

 * @Package: org.lumint.spring.datasource.config

 * @Description: see class name

 * @author: weizhong

 * @date: Jan 2, 2015 5:12:14 PM

 */
public final class Configs {

	private static Map<Object,Object> dataSourceMap = Maps.newHashMap();
	private static Map<String,Properties> propMap = Maps.newHashMap();
	private static final String defaultPathKey = "dataSourcePath";
	private static final String defaultPathValue = "datasources";
	private static final String defaultDataSourceKey = "defaultDataSource";
	private static final String defaultDataSourceValue = "defaultDataSource";
	private static final String bakDataSourceKey = "bakDataSource";
	private static final String bakDataSourceValue = "bakDataSource";
	private static final String defaultHeartbeatKey = "heartbeat";
	private static final String defaultHeartbeatValue = "flase";
	private static final String heartbeatTimeKey = "heartbeatTime";
	private static final String heartbeatTimeValue = "10000";
	private static final String heartbeatDelayKey = "heartbeatDelay";
	private static final String heartbeatDelayValue = "300000";
	private static final String defaultHeartbeatSQLKey = "heartbeatSQL";
	private static final String defaultHeartbeatSQLValue = "select 1";
	private static final String defaultConfigPath = "datasources.properties";
	private static String defaultDataSource = "defaultDataSource";
	private static String bakDataSource = "";
	private static String heartbeat = "";
	private static String heartbeatSQL = "";
	private static String heartbeatTime ="";
	private static String heartbeatDelay ="";
	private static Log LOG = LogFactory.getLog(Configs.class);
	
	private static void loadProp(String folderPath,boolean isClear){
		try{
			if(isClear)propMap.clear();
			String folderRealPath = Configs.class.getClassLoader().getResource(folderPath).getPath();
			File folder = new File(folderRealPath);
			File[] files = folder.listFiles();
			for(File file : files){
				Properties properties = new Properties();
				properties.load(new FileInputStream(file));
				propMap.put(getFileNameKey(file),properties);
			}
		}catch(Exception e){
			// ignore e
			LOG.error("load file error!", e);
		}
	}

    private static String getFileNameKey(File file) {
        if(file == null) return "";
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }

    private static void reloadProp(String folderPath) {
        loadProp(folderPath,true);
    }
	static{
		init();
	}
	public static void init(){
		Properties config = new Properties();
		try {
			config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(defaultConfigPath));
			String dataSourcePath = config.getProperty(defaultPathKey, defaultPathValue);
			defaultDataSource = config.getProperty(defaultDataSourceKey, defaultDataSourceValue);
			bakDataSource = config.getProperty(bakDataSourceKey, bakDataSourceValue);
			heartbeat = config.getProperty(defaultHeartbeatKey, defaultHeartbeatValue);
			heartbeatSQL = config.getProperty(defaultHeartbeatSQLKey, defaultHeartbeatSQLValue);
			heartbeatTime = config.getProperty(heartbeatTimeKey, heartbeatTimeValue);
			heartbeatDelay = config.getProperty(heartbeatDelayKey, heartbeatDelayValue);
			initDataSource(dataSourcePath);
		} catch (IOException e) {
			LOG.error("load config file error!", e);
		}
		
	}
	
	private static void initDataSource(String dataSourcePath){
        reloadProp(dataSourcePath);
		GenericApplicationContext ctx = new GenericApplicationContext();  
		addPropBean(ctx);
		//addAspectBean(ctx);
		StringBuilder sb= new StringBuilder();
		for(String name :ctx.getBeanDefinitionNames()){
			sb.append(name+",");
		}
		LOG.debug("Configs init:"+sb.toString());
	}

	private static void addPropBean(GenericApplicationContext ctx) {
		for(String key : propMap.keySet()){
			Properties prop = propMap.get(key);
			BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.rootBeanDefinition(BasicDataSource.class);
			for(Object propKey : prop.keySet()){	
				beanDefBuilder.addPropertyValue(propKey.toString(), prop.getProperty(propKey.toString()));
			}
			ctx.registerBeanDefinition(key, beanDefBuilder.getBeanDefinition());
			dataSourceMap.put(key, ctx.getBean(key));
		}
	}
	
//	private static void addAspectBean(GenericApplicationContext ctx){
//		BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.rootBeanDefinition(DatasourceAspect.class);
//		ctx.registerBeanDefinition("datasourceAspect", beanDefBuilder.getBeanDefinition());
//	}
	
	public static Map<Object, Object> getDataSources(){
		return ImmutableMap.copyOf(dataSourceMap);
	}
	public static Object getDefaultDataSource(){
		 return dataSourceMap.get(defaultDataSource);
	}
	
	public static Object getBakDataSource(){
		return dataSourceMap.get(bakDataSource);
	}
	
	public static String getHeartbeatSQL(){
		return heartbeatSQL;
	}
	
	public static boolean isOpenHeartbeat(){
		return Boolean.valueOf(heartbeat.toLowerCase());
	}
	
	public static long getHeartbeatTime(){
		return Long.valueOf(heartbeatTime);
	}
	public static long getHeartbeatDelay(){
		return Long.valueOf(heartbeatDelay);
	}
	
	public static boolean containsKey(String datasourceKey){
		return dataSourceMap.containsKey(datasourceKey);
	}
}
