/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月23日 上午11:25:45
 * @Description: 无
 */
package com.nl.loadscenarios.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;
import redis.clients.jedis.JedisCluster;

import java.util.Map;


public class Hset2RedisBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 7239045099254159871L;
	private static final Logger LOG = LoggerFactory.getLogger(Hset2RedisBolt.class);
	
	private JedisCluster redisCluster;
	private final RedisClusterCfg redisCfg;
	
	/**
	 * 
	 * @param redisCfg Redis配置
	 */
	public Hset2RedisBolt(final RedisClusterCfg redisCfg) {
		LOG.info("instancing...");
		this.redisCfg = redisCfg;
	
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// 初始化redis
		RedisCluster.getRedisCluster().setCfg(redisCfg);
		this.redisCluster = RedisCluster.getRedisCluster().getInstance();
	}

	public void execute(Tuple tuple, BasicOutputCollector collector) {		
		final String tabelname = tuple.getString(0);
		final String field = tuple.getString(1);
		final String value = tuple.getString(2);
		try {
			this.redisCluster.hset(tabelname, field, value);
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
}