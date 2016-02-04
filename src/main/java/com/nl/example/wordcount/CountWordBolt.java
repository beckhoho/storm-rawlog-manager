package com.nl.example.wordcount;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.util.redis.RedisCluster;

import redis.clients.jedis.JedisCluster;
import clojure.lang.Compiler.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountWordBolt extends BaseBasicBolt {

	private JedisCluster redisCluster;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		ArrayList<String> nodes = new ArrayList<String>();
		nodes.add("192.168.1.101:7001");
		nodes.add("192.168.1.101:7002");
		nodes.add("192.168.1.101:7003");
		nodes.add("192.168.1.101:8001");
		nodes.add("192.168.1.101:8002");
		nodes.add("192.168.1.101:8003");
		//redisCluster = new RedisCluster(nodes).getInstance();

	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		String value = redisCluster.hget("wordcount", tuple.getString(0));
		if (value != null) {
			redisCluster.hset("wordcount", tuple.getString(0),
					(Integer.parseInt(value) + 1) + "");
		} else {
			redisCluster.hset("wordcount", tuple.getString(0), "1");
		}
	}

	@Override
	public void cleanup() {
		redisCluster.close();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}
}