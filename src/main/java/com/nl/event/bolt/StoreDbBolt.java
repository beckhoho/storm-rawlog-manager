/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月3日 上午9:23:30
 * @Description: 无
 */
package com.nl.event.bolt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.bean.ActiveOpp;
import com.nl.util.config.DBCfg;
import com.nl.util.db.DBAccess;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class StoreDbBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StoreDbBolt.class);
	private static final String DOUBLE_SINGLE_QUOTES_COMMA="','";
	private static final String LEFT_SINGLE_QUOTES_COMMA="',";
	private static final String RIGHT_SINGLE_QUOTES_COMMA=",'";
	private static final String INSERT="insert into ";
	private static final String COLUMNS=" (MON_NUM,CHANCE_ID,EVENT_TYPE,USER_ID,MSISDN,BRAND_ID,USERGROUP_ID,ACTIVE_ID,STEP_ID,ACTIVE_OPP_ID,RULE_ID,RULE_INSTANCE_ID,EVT_ARG_LIST,INSERT_TIME,ACCEPT_TIME,DEAL_STAT)";
	private static final String VALUES_LABEL=" values ('";
	private static final String NULL="null";
	private static final String SYSDATE="SYSDATE";
	private static final String END="') ";
	
	protected OutputCollector collector;

	private  final DBCfg dbCfg;
	private  final String tablename;
	private  DBAccess dbAccess;
	

	/**
	 * 
	 * @param dbCfg 数据库配置
	 * @param tablename 表名
	 */
	public StoreDbBolt(final DBCfg dbCfg, final String tablename) {
		this.dbCfg = dbCfg;
		this.tablename = tablename;
	}

	@Override
	public void execute(Tuple tuple) {
		final ActiveOpp activeOpp = (ActiveOpp) tuple.getValue(0);
		try {
			dbAccess.runSql(spliceSql(activeOpp));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		Connection conn = null;
		try {
			LOG.info("driver:"+this.dbCfg.getDriver());
			Class.forName(this.dbCfg.getDriver());
			LOG.info("1conn:"+conn);
			conn = DriverManager.getConnection(this.dbCfg.getUrl(),
					this.dbCfg.getUser(), this.dbCfg.getPassword());
			LOG.info("2conn:"+conn);
			this.dbAccess = new DBAccess(conn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String spliceSql(final ActiveOpp activeOpp){
		final StringBuffer sql = new StringBuffer(1024);

		sql.append(INSERT);
		sql.append(this.tablename);
        sql.append(COLUMNS);
		sql.append(VALUES_LABEL);
		sql.append(activeOpp.getMonNum());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getChanceId());
		sql.append(LEFT_SINGLE_QUOTES_COMMA);
		//sql.append(activeOpp.getEventType());
		sql.append(NULL);
		sql.append(RIGHT_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getUserId());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getMsisdn());
		sql.append(LEFT_SINGLE_QUOTES_COMMA);
		//sql.append(activeOpp.getBrandId());
		sql.append(NULL);
		sql.append(RIGHT_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getUserGroupId());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getActiveId());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getStepId());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getActiveOppId());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getRuleId());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getRuleInstanceId());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getEvtArgList());
		sql.append(LEFT_SINGLE_QUOTES_COMMA);
		sql.append(SYSDATE);
		sql.append(RIGHT_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getAcceptTime());
		sql.append(DOUBLE_SINGLE_QUOTES_COMMA);
		sql.append(activeOpp.getDealStat());
		sql.append(END);
		com.nl.util.log.Log.info("[StoreDbBolt]-sql:"+sql.toString());
		return sql.toString();
		
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// do nothing
	}

}
