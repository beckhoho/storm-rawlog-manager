/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月12日 下午11:39:53
 * @Description: 无
 *//*
package com.nl.event.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.util.GlobalConst;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;

import redis.clients.jedis.JedisCluster;

import java.util.Map;

*//**
 * 用于持久化数据
 *//*
public class Hset2RedisBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 7239045099254159871L;
	private static Logger LOG = LoggerFactory.getLogger(Hset2RedisBolt.class);
	private JedisCluster redisCluster;
	private final RedisClusterCfg redisCfg;
	private final String tableName;

	*//**
	 * 
	 * @param redisCfg redis配置
	 * @param tableName 表名
	 *//*
	public Hset2RedisBolt(RedisClusterCfg redisCfg, String tableName) {
		LOG.info("instancing...");
		this.redisCfg = redisCfg;
		this.tableName = tableName;
		LOG.info(GlobalConst.MARK_STRING + "\n" + "PARAMETERS:\n" + "redisCfg:"
				+ this.redisCfg + "\n" + "tableName:" + this.tableName + "\n"
				+ GlobalConst.MARK_STRING + "\n");		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		this.redisCluster = new RedisCluster(this.redisCfg).getInstance();
	}

	public void execute(Tuple tuple, BasicOutputCollector collector) {
		try {
			this.redisCluster.hset(this.tableName + tuple.getStringByField("key"),tuple.getStringByField("field"),tuple.getStringByField("value"));
		} catch (Exception e) {
			e.printStackTrace();
			this.redisCluster.close();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// do nothing
	}

	@Override
	public void cleanup() {
		this.redisCluster.close();
	}
}*/