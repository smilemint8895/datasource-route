/**
* Copyright © 2015 meilishuo.com All rights reserved.
*/
package org.lumint.spring.datasource;
/**  
 * @Title: DatasourceHolder.java

 * @Prject: datasource

 * @Package: org.lumint.spring.datasource

 * @Description: see class name

 * @author: weizhong

 * @date: Jan 2, 2015 5:15:05 PM

 */
public class DatasourceHolder {

	private static final ThreadLocal<String> dataSourceLocal = new ThreadLocal<String>();
	
	public static String getDatasource(){
		return dataSourceLocal.get();
	}
	
	public static void setDatasource(String datasourceKey){
		dataSourceLocal.set(datasourceKey);
	}
}
