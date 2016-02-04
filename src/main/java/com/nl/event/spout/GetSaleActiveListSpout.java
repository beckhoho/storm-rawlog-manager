package com.nl.event.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisCluster;

import com.nl.event.conf.ConfManager;
import com.nl.bean.RuleInstance;
import com.nl.bean.SaleActive;
import com.nl.bean.SaleActiveRuleParam;
import com.nl.util.config.DBCfg;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.db.DBAccess;
import com.nl.util.redis.RedisCluster;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月5日 上午10:48:11
 * @ClassName: GetSaleActiveListSpout
 * @Description: 获取待处理营销活动编码
 *               </p>
 *               </pre>
 */
public class GetSaleActiveListSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(GetSaleActiveListSpout.class);
	private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
	private static final Fields FIELDS=new Fields("saleActive", "ruleInstanceList","ruleInstancesParamsMap");
	private final DBCfg dbCfg;
	private  DBAccess dbAccess;
	private final RedisClusterCfg redisCfg;
	private final int lockTimeSecond;
	private final static long WAITING_TIME_MS = TimeUnit.SECONDS.toMillis(900);// 时间间隔
	//private TopologyContext context = null;
	private SpoutOutputCollector collector = null;
	private final Map<String,Date> workSpace=new HashMap<String,Date>();
	
	private JedisCluster redisCluster;
	
	public GetSaleActiveListSpout(final DBCfg dbCfg,final RedisClusterCfg redisCfg,final int lockTimeSecond) {
		this.dbCfg = dbCfg;
		this.redisCfg = redisCfg;
		this.lockTimeSecond=lockTimeSecond;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		//this.context = context;
		this.collector = collector;
		ConfManager.getInstance().setDb(dbCfg); // 初始化数据库
		// 初始化redis
		RedisCluster.getRedisCluster().setCfg(redisCfg);
		this.redisCluster = RedisCluster.getRedisCluster().getInstance();

		Connection conn = null;
		try {
			Class.forName(this.dbCfg.getDriver());
			conn = DriverManager.getConnection(this.dbCfg.getUrl(),
					this.dbCfg.getUser(), this.dbCfg.getPassword());
			this.dbAccess = new DBAccess(conn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 计划任务：记录已处理营销活动
		final Runnable recordEmitedSaleActive = new Runnable() {
			public void run() {
				StringBuffer sql = new StringBuffer();
				for (Entry<String, Date> entry : workSpace.entrySet()) {
					final String code = entry.getKey();
					final String timeStamp = code.substring(0, 13);
					final String saleActiveId = code.substring(13, code.length());
					final String dealTime = entry.getValue().toString();
					sql.setLength(0);
					sql.append("insert into rpt_record_sale_active (active_id,original_timestamp,deal_time,insert_time) values('")
							.append(saleActiveId).append("','")
							.append(timeStamp).append("','").append(dealTime)
							.append("',now() )");
					try {
						dbAccess.runSql(sql.toString());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				workSpace.clear();
			}
		};
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		SERVICE.scheduleAtFixedRate(recordEmitedSaleActive, 600, 60, TimeUnit.SECONDS);
	}

	@Override
	public void nextTuple() {
		ConfManager.getInstance().loadSaleActive();
		final List<SaleActive> saleActiveList = ConfManager.getInstance().getSaleActiveList();
		final Map<String, List<RuleInstance>> ruleInstances = ConfManager.getInstance().getRuleInstances();
		final Map<String, List<SaleActiveRuleParam>> ruleInstanceParams=ConfManager.getInstance().getRuleInstanceParams();	
		
		for (final SaleActive saleActive : saleActiveList) {
			com.nl.util.log.Log.info("[GetSaleActiveListSpout]-ActiveId:"
					+ saleActive.getActiveId());
		}
		
		if(saleActiveList!=null && ruleInstances!=null){
		for (final SaleActive saleActive : saleActiveList) {
			final String activeId = saleActive.getActiveId();
			final String unique = saleActive.getRequestTimeStamp() + activeId;
			final List<RuleInstance> ruleInstanceList=ruleInstances.get(activeId);
			final Map<String, List<SaleActiveRuleParam>> ruleInstancesParamsMap = new HashMap<String, List<SaleActiveRuleParam>>() ;
			for (final RuleInstance ri:ruleInstanceList){
				ruleInstancesParamsMap.put(ri.getRuleInstanceId(), ruleInstanceParams.get(ri.getRuleInstanceId()));
			}
			//if (!workingSet.contains(unique)) {
			if(this.redisCluster.get(unique)==null){
				this.collector.emit(new Values(saleActive, ruleInstanceList,ruleInstancesParamsMap), unique);
				lock(unique);
				workSpace.put(unique, new Date());
				com.nl.util.log.Log.info("[GetSaleActiveListSpout]-Emit saleActive:" + unique);
			}
		}}
		try {
			Thread.sleep(WAITING_TIME_MS);// 间隔
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(FIELDS);
	}

	@Override
	public void ack(Object unique) {
		//workSpace.put(unique.toString(), new Date());
		com.nl.util.log.Log.info("[GetSaleActiveListSpout]-ack saleActive:" + unique);
	}

	@Override
	public void fail(Object unique) {
		//this.redisCluster.del(unique.toString());
		com.nl.util.log.Log.error("[GetSaleActiveListSpout]-fail saleActive:" + unique);
	}
	private void lock(String unique){
		this.redisCluster.setex(unique, this.lockTimeSecond, "1");	
	}
}
