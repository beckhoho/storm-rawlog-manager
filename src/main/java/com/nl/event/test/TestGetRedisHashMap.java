/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月17日 下午2:53:43
 * @Description: 无
 *//*
package com.nl.event.test;

import java.util.ArrayList;

import redis.clients.jedis.JedisCluster;

import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;

public class TestGetRedisHashMap {
	public static void main(String[] args) {
		RedisClusterCfg cfg = new RedisClusterCfg();

		ArrayList<String> places = new ArrayList<String>();
		places.add("192.168.1.101:7001");
		places.add("192.168.1.101:7002");
		places.add("192.168.1.101:7003");
		places.add("192.168.1.101:8001");
		places.add("192.168.1.101:8002");
		places.add("192.168.1.101:8003");

		cfg.setHostAndport(places);
		final JedisCluster jc = new RedisCluster(cfg).getInstance();

		System.out.println(jc.hgetAll("hmap_userid").size());
	}

}
*/