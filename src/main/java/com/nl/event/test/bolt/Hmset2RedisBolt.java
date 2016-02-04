/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月4日 下午1:39:53
 * @Description: 无
 *//*
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

import com.nl.event.bean.NLAtomEvent;
import com.nl.util.GlobalConst;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;

import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.Map;

*//**
 * 用于持久化数据
 *//*
public class Hmset2RedisBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 7239045099254159871L;
	private static Logger LOG = LoggerFactory.getLogger(Hmset2RedisBolt.class);
	
	private JedisCluster redisCluster;
	private final ArrayList<String> streams;
	private final int STREAMSIZE;
	private final RedisClusterCfg redisCfg;
	private final String tableName;
	*//**
	 * 
	 * @param redisCfg
	 * @param tableName
	 * @param streams
	 *//*
	public Hmset2RedisBolt(RedisClusterCfg redisCfg, String tableName, ArrayList<String> streams) {
		LOG.info("instancing...");
		this.redisCfg = redisCfg;
		this.tableName = tableName;
		this.streams = streams;
		this.STREAMSIZE = streams.size();
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
		
		NLAtomEvent nlAtomEvent =(NLAtomEvent) tuple.getValue(0);
		String key=nlAtomEvent.getKey();
		try {
			this.redisCluster.hmset(this.tableName +key,nlAtomEvent.getValues());
		} catch (Exception e) {
			e.printStackTrace();
			this.redisCluster.close();
		}finally{
			collector.emit(streams.get(Math.abs(key.hashCode() % this.STREAMSIZE)),new Values(nlAtomEvent));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		for (String stream : streams)
			declarer.declareStream(stream, new Fields("nlAtomEvent"));
	}

	@Override
	public void cleanup() {
		this.redisCluster.close();
	}
}*/