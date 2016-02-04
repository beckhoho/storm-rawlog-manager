/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年10月14日 下午1:56:23
* @Description: 无
*/
package com.nl.util.redis;

import java.util.ArrayList;

import redis.clients.jedis.JedisCluster;

public class Test {

	public static void main(String[] args) {
		ArrayList<String> places = new ArrayList<String>();
		places.add("192.168.1.101:7001");
		places.add("192.168.1.101:7002");
		places.add("192.168.1.101:7003");
		places.add("192.168.1.101:8001");
		places.add("192.168.1.101:8002");
		places.add("192.168.1.101:8003");
		//JedisCluster jc=new RedisCluster(places).getInstance();
		//System.out.println(jc.zrevrangeWithScores("topN-Website",0,4));
		
		//redisCluster.
		
		/*for (int i = 0; i < 5000000; i++) {
			jc.hset("test_hash_size", "field" + i, "value" + i);
		}*/
		//System.out.println(jc.hgetAll("test_hash_size").size());
	}

}
