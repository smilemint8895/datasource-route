/**
* Copyright © 2015 meilishuo.com All rights reserved.
*/
package org.lumint.spring.datasource.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.lumint.spring.datasource.DatasourceHolder;
import org.lumint.spring.datasource.annotation.MarkDatasource;
import org.lumint.spring.datasource.config.Configs;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


/**  
 * @Title: DatasourceAspect.java

 * @Prject: datasource

 * @Package: org.lumint.spring.datasource.aspect

 * @Description: see class name

 * @author: weizhong

 * @date: Jan 2, 2015 5:13:02 PM

 */
@Aspect
@EnableAspectJAutoProxy
@Configuration
public class DatasourceAspect {

	private static Log LOG = LogFactory.getLog(DatasourceAspect.class);
	
	@Around(value="@annotation(markDatasource)")
	public Object doAround(ProceedingJoinPoint pjp,MarkDatasource markDatasource) throws Throwable{
		String currentDatasourceName = null;
		try{
			currentDatasourceName =  DatasourceHolder.getDatasource();
			DatasourceHolder.setDatasource(markDatasource.value());
			LOG.info("markDatasource:"+markDatasource.value());
			if(!Configs.containsKey(markDatasource.value()))LOG.info("datasource not contains:"+markDatasource.value());
		}catch(Exception e){/*ignore e*/}
		try {
			return pjp.proceed();
		}catch (Throwable e) {
			throw e;
		}finally{
			try{
				DatasourceHolder.setDatasource(currentDatasourceName);
			}catch(Exception e){
				//ignore e
			}
		}
	}
}
