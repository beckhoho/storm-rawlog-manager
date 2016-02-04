package com.nl.util.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import redis.clients.jedis.HostAndPort;

public class RedisClusterCfg implements Serializable,
		Comparable<RedisClusterCfg> {

	private static final long serialVersionUID = 1L;
	private int timeOut = 15000;
	private int maxRedirections = 100;
	private List<String> hostAndport=new ArrayList<String>(Arrays.asList("127.0.0.1:6379"));;
	private int poolConfigMaxIdle = 32;// 最大能够保持idel状态的对象数
	private int poolConfigMinIdle = 1;
	private int poolConfigMaxTotal = 128;// 最大分配的对象数
	private long poolConfigMaxWaitMillis = 10000;
	private long poolConfigMinEvictableIdleTimeMillis = 900000;// 空闲连接多长时间后会被收回
	private long poolConfigTimeBetweenEvictionRunsMillis = 100000;// 多长时间检查一次空闲的连接
	

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int getMaxRedirections() {
		return maxRedirections;
	}

	public void setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public List<String> getHostAndport() {
		return hostAndport;
	}

	public void setHostAndport(List<String> hostAndport) {
		this.hostAndport = hostAndport;
	}

	public int getPoolConfigMaxIdle() {
		return poolConfigMaxIdle;
	}

	public void setPoolConfigMaxIdle(int poolConfigMaxIdle) {
		this.poolConfigMaxIdle = poolConfigMaxIdle;
	}

	public int getPoolConfigMinIdle() {
		return poolConfigMinIdle;
	}

	public void setPoolConfigMinIdle(int poolConfigMinIdle) {
		this.poolConfigMinIdle = poolConfigMinIdle;
	}

	public int getPoolConfigMaxTotal() {
		return poolConfigMaxTotal;
	}

	public void setPoolConfigMaxTotal(int poolConfigMaxTotal) {
		this.poolConfigMaxTotal = poolConfigMaxTotal;
	}

	public long getPoolConfigMaxWaitMillis() {
		return poolConfigMaxWaitMillis;
	}

	public void setPoolConfigMaxWaitMillis(long poolConfigMaxWaitMillis) {
		this.poolConfigMaxWaitMillis = poolConfigMaxWaitMillis;
	}

	public long getPoolConfigMinEvictableIdleTimeMillis() {
		return poolConfigMinEvictableIdleTimeMillis;
	}

	public void setPoolConfigMinEvictableIdleTimeMillis(
			long poolConfigMinEvictableIdleTimeMillis) {
		this.poolConfigMinEvictableIdleTimeMillis = poolConfigMinEvictableIdleTimeMillis;
	}

	public long getPoolConfigTimeBetweenEvictionRunsMillis() {
		return poolConfigTimeBetweenEvictionRunsMillis;
	}

	public void setPoolConfigTimeBetweenEvictionRunsMillis(
			long poolConfigTimeBetweenEvictionRunsMillis) {
		this.poolConfigTimeBetweenEvictionRunsMillis = poolConfigTimeBetweenEvictionRunsMillis;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int compareTo(RedisClusterCfg o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
