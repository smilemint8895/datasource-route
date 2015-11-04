/**
* Copyright © 2015 meilishuo.com All rights reserved.
*/
package org.lumint.spring.datasource;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.lumint.spring.datasource.config.Configs.*;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**  
 * @Title: DatasourceRoute.java

 * @Prject: datasource

 * @Package: org.lumint.spring.datasource

 * @Description: see class name

 * @author: weizhong

 * @date: Jan 2, 2015 6:19:28 PM

 */
public class DatasourceRoute extends AbstractRoutingDataSource{

	private static final Log LOG = LogFactory.getLog(DatasourceRoute.class);
	private Object defaultDataSource;
	private Object bakDataSource;
	private Timer timer = new Timer();
	
	public DatasourceRoute(){
		LOG.debug("DatasourceRoute load");
		this.setTargetDataSources(getDataSources());
		defaultDataSource = getDefaultDataSource();
		bakDataSource = getBakDataSource();
		this.setDefaultTargetDataSource(defaultDataSource);
		openHeartbeat();
		LOG.info("load dataSources ["+ getDataSources()+"],defaultDataSource["+defaultDataSource+"],bak DataSource["+bakDataSource+"],heartbeat["+ isOpenHeartbeat()+"],heartbeatSQL["+getHeartbeatSQL()+"],heartbeatTime["+getHeartbeatTime()+"]" );
	}

	public void openHeartbeat() {
        if(!isOpenHeartbeat()) return;
		if(timer!=null){
			timer.cancel();
			timer.schedule(new TimerTask() {
				public void run() {
					try {
                        final String heartbeatSQL = getHeartbeatSQL();
                        if(null==heartbeatSQL || "".equals(heartbeatSQL.trim()))return;
						LOG.debug("heart beat SQL begin:"+heartbeatSQL);
						getConnection().createStatement().execute(heartbeatSQL);
						LOG.debug("heart beat SQL end:"+heartbeatSQL);
					} catch (Exception e) {
						LOG.error("heart beat SQL ERROR. dynamic switch dataSource",e);
						switchDataSource();
					}
				}
			},getHeartbeatDelay(),getHeartbeatTime());
		}
	}
	
	private void switchDataSource(){
		LOG.info("switch dataSource begin: defaultDataSource["+defaultDataSource+"],bakDataSource["+bakDataSource+"]");
		Object temp = defaultDataSource;
		defaultDataSource =bakDataSource;
		bakDataSource = temp;
		this.setDefaultTargetDataSource(defaultDataSource);
		LOG.info("switch dataSource end: defaultDataSource["+defaultDataSource+"],bakDataSource["+bakDataSource+"]");

	}
	
	@Override
	protected Object determineCurrentLookupKey() {
		LOG.info("determineCurrentLookupKey:["+DatasourceHolder.getDatasource()+"]" );
		return DatasourceHolder.getDatasource();
	}
	
}
