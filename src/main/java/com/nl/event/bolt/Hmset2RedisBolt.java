/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月4日 下午1:39:53
 * @Description: 无
 */
package com.nl.event.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.bean.NLAtomEvent;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;

import redis.clients.jedis.JedisCluster;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 将NL原子事件存入Redis 
 */
public class Hmset2RedisBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 7239045099254159871L;
	private static final Logger LOG = LoggerFactory.getLogger(Hmset2RedisBolt.class);
	
	private JedisCluster redisCluster;
	private final RedisClusterCfg redisCfg;
	private final String tableName;
	private final int expire;
	
	/**
	 * 
	 * @param redisCfg Redis配置
	 * @param tableName 表名
	 */
	public Hmset2RedisBolt(final RedisClusterCfg redisCfg, final String tableName,final int expire) {
		LOG.info("instancing...");
		this.redisCfg = redisCfg;
		this.tableName = tableName;
		this.expire=expire;
	
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// 初始化redis
		RedisCluster.getRedisCluster().setCfg(redisCfg);
		this.redisCluster = RedisCluster.getRedisCluster().getInstance();
	}

	public void execute(Tuple tuple, BasicOutputCollector collector) {		
		final NLAtomEvent nlAtomEvent =(NLAtomEvent) tuple.getValue(0);
		final String key=nlAtomEvent.getKey();
		final Map<String, String> events=nlAtomEvent.getValues();
		final String dynamicArgs=nlAtomEvent.getDynamicArgs();
		try {
			this.redisCluster.hmset(this.tableName +key,events);
			for (Entry<String, String> event : events.entrySet()) {
				this.redisCluster.setex(this.tableName+key+":"+event.getKey(), this.expire, event.getValue());
				// 存入动态参数
				this.redisCluster.set("XJAE:AEDA:"+key+":"+event.getKey(),dynamicArgs);
			}
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