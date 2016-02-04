/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月5日 下午1:14:25
 * @Description: 无
 */
package com.nl.event.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisCluster;

import com.nl.event.conf.ConfManager;
import com.nl.bean.RuleInstance;
import com.nl.bean.SaleActive;
import com.nl.bean.SaleActiveRuleParam;
import com.nl.bean.User;
import com.nl.util.GlobalConst;
import com.nl.util.config.DBCfg;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 
 * @ClassName: MatchSaleActiveUser 
 * @Description: 配对营销客户
 *               </p>
 * 
 * <pre>
 * </pre>
 */
public class MatchSaleActiveUserListBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(MatchSaleActiveUserListBolt.class);
	private static final Fields FIELDS=new Fields("saleActive", "ruleInstanceList","ruleInstancesParamsMap","user");
	private JedisCluster redisCluster;
	// 构造函数参数
	private final RedisClusterCfg redisCfg;
	private final String tablename;
	private final DBCfg dbCfg;
	private final ArrayList<String> streams;
	private final int STREAMSIZE;
	
	/**
	 * 
	 * @param dbCfg 数据库配置
	 * @param redisCfg Redis配置
	 * @param tablename 表名
	 * @param streams 输出流
	 */
	public MatchSaleActiveUserListBolt(final DBCfg dbCfg, final RedisClusterCfg redisCfg,final String tablename, final ArrayList<String> streams) {
		LOG.info("instancing...");
		this.dbCfg = dbCfg;
		this.redisCfg = redisCfg;
		this.tablename=tablename;
		this.streams = streams;
		this.STREAMSIZE = streams.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// 初始化redis
		RedisCluster.getRedisCluster().setCfg(redisCfg);
		this.redisCluster = RedisCluster.getRedisCluster().getInstance();
		ConfManager.getInstance().setDb(dbCfg); // 初始化数据库
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// 获取tuple数据
		final SaleActive saleActive = (SaleActive) tuple.getValue(0);
		final List<RuleInstance> ruleInstanceList = (List<RuleInstance>) tuple.getValue(1);
		final Map<String, List<SaleActiveRuleParam>> ruleInstancesParamsMap=(Map<String, List<SaleActiveRuleParam>>) tuple.getValue(2);	
		
		final List<String> userGroup = this.redisCluster.lrange(this.tablename+saleActive.getActiveId(), 0, -1);// 获取客户群 :jedisCluster.rpush(Active_id, User_id+","+Msisdn);
		for (final String strUser : userGroup) {
			final User user = new User();
			final String[] fields = strUser.split(GlobalConst.DEFAULT_SEPARATOR);
			user.setUserId(fields[0]);
			user.setMsisdn(fields[1]);
			LOG.info("------------------Msisdn:" + user.getMsisdn());
			collector.emit(streams.get(Math.abs(user.getMsisdn().hashCode() % this.STREAMSIZE)),new Values(saleActive, ruleInstanceList,ruleInstancesParamsMap, user));			
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		for (final String stream : streams)
			declarer.declareStream(stream, FIELDS);
	}

	@Override
	public void cleanup() {
		this.redisCluster.close();
	}
}
/*LOG.info("--------1----------ActiveId:" + saleActive.getActiveId());
for (RuleInstance ruleInstance : ruleInstanceList) {
	LOG.info("--------2----------RuleInstanceId:" + ruleInstance.getRuleInstanceId());
	List<SaleActiveRuleParam> params = ruleInstancesParamsMap.get(ruleInstance.getRuleInstanceId());
	for (SaleActiveRuleParam p : params) {
		LOG.info("--------3----------ParamId:" + p.getRuleInstanceId()
				+ ":" + p.getParamId() + ":" + p.getParamValue());
	}
}*/