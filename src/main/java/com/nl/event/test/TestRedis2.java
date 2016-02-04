/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月25日 下午8:46:41
 * @Description: 无
 */
package com.nl.event.test;

import com.nl.util.redis.RedisCluster;

public class TestRedis2 {
	public static void main(String[] args) {

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					RedisCluster.getRedisCluster().getInstance();
				}
			}

		}).start();
	}
}
